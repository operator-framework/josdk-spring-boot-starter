package io.javaoperatorsdk.operator.springboot.starter.test.kubeapitest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.fabric8.kubeapitest.KubeAPIServerConfig;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.Operator;
import io.javaoperatorsdk.operator.RuntimeInfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(
    classes = testsupport.KubeAPITestIntegrationApplication.class,
    properties = "javaoperatorsdk.crd.path=/crd")
@EnableKubeAPITest(applyCrdsOnStartup = true)
class KubeAPITestConfigurationIntegrationTest {

  @Autowired
  private KubernetesClient kubernetesClient;

  @Autowired
  private Config config;

  @Autowired
  private KubeAPIServerConfig kubeAPIServerConfig;

  @Autowired
  private Operator operator;

  @Test
  void crdsUploadedAndTransformersApplied() {
    var list = kubernetesClient.apiextensions().v1()
        .customResourceDefinitions().list().getItems();

    var crds = assertThat(list).hasSize(2);
    crds.filteredOn(it -> "customservice".equals(it.getSpec().getNames().getSingular()))
        .hasSize(2)
        .filteredOn(it -> "sample.javaoperatorsdk".equals(it.getSpec().getGroup()))
        .hasSize(1);
    crds.filteredOn(it -> "global.sample.javaoperatorsdk".equals(it.getSpec().getGroup()))
        .hasSize(1);
    crds.allSatisfy(crd -> assertThat(crd.getMetadata().getLabels())
        .containsEntry("One", "1")
        .containsEntry("Two", "2"));
  }

  @Test
  void clientUsesManagedServer() {
    assertThat(config.getMasterUrl()).startsWith("https://127.0.0.1:");
    assertThat(kubernetesClient.getConfiguration().getMasterUrl())
        .startsWith("https://127.0.0.1:");
  }

  @Test
  void controllerStarts() {
    await().untilAsserted(() -> {
      assertThat(new RuntimeInfo(operator).isStarted()).isTrue();
      assertThat(operator.getRegisteredControllers()).hasSize(1);
    });
  }

  @Test
  void doesNotUpdateKubeconfigByDefault() {
    assertThat(kubeAPIServerConfig.isUpdateKubeConfig()).isFalse();
  }
}
