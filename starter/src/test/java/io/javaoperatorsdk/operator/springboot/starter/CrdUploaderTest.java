package io.javaoperatorsdk.operator.springboot.starter;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable;
import io.javaoperatorsdk.operator.springboot.starter.model.TestResource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrdUploaderTest {

  private final List<CrdUploader.CrdTransformer> crdTransformers = List.of(t -> {
    t.getMetadata().setLabels(Map.of("transformed", "true"));
    return t;
  });
  @Mock
  private KubernetesClient kubernetesClient;
  @Mock
  private NamespaceListVisitFromServerGetDeleteRecreateWaitApplicable<HasMetadata> loadedResource;
  private boolean applyOnStartup = true;
  private String crdSuffix = "-v2.yml";
  private String crdPath = "/META-INF/fabric8/";

  private CrdUploader uploader() {
    return new CrdUploader(kubernetesClient, crdTransformers, applyOnStartup, crdPath, crdSuffix);
  }

  @Test
  void shouldNotUploadWhenDisabled() throws Exception {
    applyOnStartup = false;

    uploader().upload();

    verifyNoInteractions(kubernetesClient);
  }

  @Test
  void shouldUploadAndApplyTransformer() throws Exception {
    var testResource = new TestResource();
    var crds = List.<HasMetadata>of(testResource);
    when(loadedResource.items()).thenReturn(crds);
    when(kubernetesClient.load(any())).thenReturn(loadedResource);
    when(kubernetesClient.resourceList(crds)).thenReturn(loadedResource);

    uploader().upload();

    assertThat(testResource.getMetadata().getLabels().get("transformed"))
        .isEqualTo("true");
  }

  @Test
  void shouldNotUploadWhenNoCrdsFound() throws Exception {
    crdSuffix = "not-found-suffix";

    uploader().upload();

    verifyNoInteractions(kubernetesClient);
  }

  @Test
  void shouldThrowWhenBadPath() {
    crdPath = "badPath";
    assertThatThrownBy(() -> uploader().upload())
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Could not find the configured CRD path");

    verifyNoInteractions(kubernetesClient);
  }

}
