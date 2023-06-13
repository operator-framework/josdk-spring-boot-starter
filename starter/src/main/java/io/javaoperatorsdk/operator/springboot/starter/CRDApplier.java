package io.javaoperatorsdk.operator.springboot.starter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.function.UnaryOperator;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.KubernetesClient;

import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static org.slf4j.LoggerFactory.getLogger;

@FunctionalInterface
public interface CRDApplier {

  CRDApplier NOOP = () -> getLogger(CRDApplier.class).debug("Not searching for CRDs to apply");

  void apply();

  interface CRDTransformer extends UnaryOperator<HasMetadata> {
    default CRDTransformer thenTransform(@NotNull CRDApplier.CRDTransformer after) {
      return t -> after.apply(apply(t));
    }

    static CRDTransformer reduce(List<CRDTransformer> transformers) {
      return transformers.stream().reduce(t -> t, CRDTransformer::thenTransform);
    }
  }

  class DefaultCRDApplier implements CRDApplier {

    private static final Logger log = getLogger(DefaultCRDApplier.class);

    private static final int CRD_READY_WAIT = 2000;

    private final CRDTransformer crdTransformer;
    private final KubernetesClient kubernetesClient;
    private final String crdSuffix;
    private final String crdPath;

    public DefaultCRDApplier(KubernetesClient kubernetesClient, List<CRDTransformer> transformers,
        String crdPath, String crdSuffix) {
      this.crdTransformer = CRDTransformer.reduce(transformers);
      this.kubernetesClient = kubernetesClient;
      this.crdSuffix = crdSuffix;
      this.crdPath = crdPath;
    }

    @Override
    public void apply() {
      log.debug("Uploading CRDs with suffix {} under {}", crdSuffix, crdPath);
      stream(findFiles()).forEach(this::applyCrd);
    }

    private File[] findFiles() {
      var resource = requireNonNull(
          getClass().getResource(crdPath),
          "Could not find the configured CRD path");

      try {
        return new File(resource.toURI()).listFiles((ignored, name) -> name.endsWith(crdSuffix));
      } catch (URISyntaxException e) {
        throw new RuntimeException(e);
      }
    }

    private void applyCrd(File crdFile) {
      try (var is = new FileInputStream(crdFile)) {
        var crds = kubernetesClient.load(is).items().stream().map(crdTransformer).toList();
        kubernetesClient.resourceList(crds).createOrReplace();

        Thread.sleep(CRD_READY_WAIT); // readiness is not applicable for CRD, just wait a little

        logUploaded(crds);
      } catch (InterruptedException | IOException ex) {
        throw new RuntimeException(ex);
      }
    }

    private void logUploaded(List<HasMetadata> crds) {
      var crdNames = crds.stream()
          .map(HasMetadata::getMetadata)
          .map(ObjectMeta::getName)
          .collect(joining(", "));
      log.info("Uploaded CRDs: {}", crdNames);
    }

  }
}
