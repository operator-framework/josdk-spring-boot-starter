package io.javaoperatorsdk.operator.springboot.starter;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.k3s.K3sContainer;
import org.testcontainers.utility.DockerImageName;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ListOptionsBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.config.ConfigurationServiceProvider;
import io.javaoperatorsdk.operator.springboot.starter.CRDApplier.CRDTransformer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

@SpringBootTest(properties = {"javaoperatorsdk.crd.apply-on-startup = true"})
public class AutoConfigurationIntegrationTest {

  @Autowired
  private KubernetesClient kubernetesClient;

  @BeforeAll
  static void beforeAll() {
    ConfigurationServiceProvider.reset();
  }

  @Test
  void crdsUploadedAndTransformersApplied() {
    var list = kubernetesClient.apiextensions().v1()
        .customResourceDefinitions()
        .list(new ListOptionsBuilder().withLabelSelector("Glory = Glory, Man = United").build())
        .getItems();

    var crds = assertThat(list).hasSize(2);
    crds.filteredOn(it -> "testresource".equals(it.getSpec().getNames().getSingular()))
        .hasSize(1)
        .filteredOn(it -> "sample.javaoperatorsdk".equals(it.getSpec().getGroup()))
        .hasSize(1);
    crds.filteredOn(it -> "josdksecondresources".equals(it.getSpec().getNames().getPlural()))
        .hasSize(1)
        .filteredOn(it -> "josdk.io".equals(it.getSpec().getGroup()))
        .hasSize(1);
  }

  @TestConfiguration
  static class TestConfig {

    static {
      System.setProperty("kubernetes.namespace", "default");
    }

    @Bean(name = "k3sContainer", initMethod = "start", destroyMethod = "stop")
    public GenericContainer<K3sContainer> k3sContainer() {
      return new K3sContainer(DockerImageName.parse("rancher/k3s:v1.21.3-k3s1")) {
        @Override
        public void start() {
          super.start();

          try {
            var config = Files.createTempFile("k3s-kubeconfig", "yml");
            Files.writeString(config, getKubeConfigYaml());
            // path for io.fabric8.kubernetes.client.Config
            System.setProperty("kubeconfig", config.toAbsolutePath().toString());
          } catch (IOException e) {
            throw new IllegalStateException("Could not start k3s");
          }
        }
      }
          .withLogConsumer(new Slf4jLogConsumer(getLogger("k3s-logger")));
    }

    @Bean
    public CRDTransformer transformerOne() {
      return crd -> addLabel(crd, "Glory", "Glory");
    }


    @Bean
    public CRDTransformer transformerTwo() {
      return crd -> addLabel(crd, "Man", "United");
    }

    private static HasMetadata addLabel(HasMetadata crd, String k, String v) {
      crd.getMetadata().getLabels().put(k, v);
      return crd;
    }

  }

}
