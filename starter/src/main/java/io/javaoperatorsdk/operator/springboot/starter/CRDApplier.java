package io.javaoperatorsdk.operator.springboot.starter;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.UnaryOperator;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.KubernetesClient;

import static java.util.Arrays.stream;
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
      stream(findResources()).forEach(this::applyCrd);
    }

    private Resource[] findResources() {
      final var resourceResolver = new PathMatchingResourcePatternResolver();
      final var resourceLocationPattern = Paths.get(crdPath, '*' + crdSuffix).toString();
      try {
        return resourceResolver.getResources(resourceLocationPattern);
      } catch (IOException e) {
        throw new RuntimeException(
            "could not find CRD resources from the location pattern: " + resourceLocationPattern);
      }
    }

    private void applyCrd(Resource crdResource) {
      try (var is = crdResource.getInputStream()) {
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
