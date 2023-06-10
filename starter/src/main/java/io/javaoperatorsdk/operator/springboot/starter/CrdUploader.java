package io.javaoperatorsdk.operator.springboot.starter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.function.UnaryOperator;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.KubernetesClient;

import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

public class CrdUploader {

  private static final Logger log = LoggerFactory.getLogger(CrdUploader.class);

  private static final int CRD_READY_WAIT = 2000;

  private final CrdTransformer crdTransformer;
  private final KubernetesClient kubernetesClient;
  private final boolean applyOnStartup;
  private final String crdSuffix;
  private final String crdPath;

  public CrdUploader(KubernetesClient kubernetesClient, List<CrdTransformer> transformers,
      boolean applyOnStartup, String crdPath, String crdSuffix) {
    this.crdTransformer = CrdTransformer.reduce(transformers);
    this.kubernetesClient = kubernetesClient;
    this.applyOnStartup = applyOnStartup;
    this.crdSuffix = crdSuffix;
    this.crdPath = crdPath;
  }

  public void upload() throws Exception {
    if (applyOnStartup) {
      log.debug("Uploading CRDs with suffix {} under {}", crdSuffix, crdPath);
      stream(findFiles()).forEach(this::applyCrd);
    } else {
      log.debug("Not searching for CRDs to upload");
    }
  }

  private File[] findFiles() throws URISyntaxException {
    var resource = requireNonNull(
        getClass().getResource(crdPath),
        "Could not find the configured CRD path");

    return new File(resource.toURI()).listFiles((ignored, name) -> name.endsWith(crdSuffix));
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

  public interface CrdTransformer extends UnaryOperator<HasMetadata> {
    default CrdTransformer thenTransform(@NotNull CrdTransformer after) {
      return t -> after.apply(apply(t));
    }

    static CrdTransformer reduce(List<CrdTransformer> transformers) {
      return transformers.stream().reduce(t -> t, CrdTransformer::thenTransform);
    }
  }

}
