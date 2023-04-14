package io.javaoperatorsdk.operator.springboot.starter.test;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.Operator;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.springboot.starter.OperatorConfigurationProperties;
import io.javaoperatorsdk.operator.springboot.starter.ReconcilerProperties;
import io.javaoperatorsdk.operator.springboot.starter.sample.CustomService;
import io.javaoperatorsdk.operator.springboot.starter.sample.CustomServiceReconciler;
import io.javaoperatorsdk.operator.springboot.starter.sample.ServiceSpec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JsonTest
@EnableMockOperator(crdPaths = "classpath:crd.yml")
class EnableMockOperatorTests {

  @Autowired
  KubernetesClient client;

  @Autowired
  ApplicationContext applicationContext;

  @Test
  void testCrdLoaded() {
    assertThat(applicationContext.getBean(Operator.class)).isNotNull();
    assertThat(
        client
            .apiextensions()
            .v1()
            .customResourceDefinitions()
            .withName("customservices.sample.javaoperatorsdk")
            .get())
        .isNotNull();
    assertThat(
        client
            .apiextensions()
            .v1()
            .customResourceDefinitions()
            .withName("customservices.global.sample.javaoperatorsdk")
            .get())
        .isNotNull();
  }

  @Autowired
  private OperatorConfigurationProperties operatorConfigurationProperties;

  @Test
  void customServiceReconciler() {
    ReconcilerProperties properties =
        operatorConfigurationProperties.getReconcilers().get("customservicereconciler");
    assertNotNull(properties);
    String testNS = "test-ns";
    assertTrue(properties.getNamespaces().contains(testNS));
    // create a namespace for testing
    client.namespaces().resource(namespace(testNS)).create();
    assertNotNull(client.namespaces().withName(testNS).get());
    // create a CR
    client.resources(CustomService.class).inNamespace(testNS)
        .resource(customService("test-name", "test-label"))
        .create();
    assertNotNull(client.resources(CustomService.class).inNamespace(testNS)
        .withName("test-name").get());
    // test if a service was created by the CustomServiceReconciler
    await()
        .atMost(15, TimeUnit.SECONDS)
        .untilAsserted(
            () -> assertNotNull(
                client.resources(Service.class).inNamespace(testNS).withName("test-name").get()));
  }

  private Namespace namespace(String ns) {
    return new NamespaceBuilder()
        .withMetadata(
            new ObjectMetaBuilder().withName(ns).build())
        .build();
  }

  private CustomService customService(String name, String label) {
    CustomService resource = new CustomService();
    resource.setMetadata(
        new ObjectMetaBuilder()
            .withName(name)
            .withResourceVersion("v1")
            .build());
    resource.setSpec(new ServiceSpec());
    resource.getSpec().setName(name);
    resource.getSpec().setLabel(label);
    return resource;
  }

  @SpringBootApplication
  @ComponentScan(
      includeFilters = {
          @ComponentScan.Filter(type = FilterType.ANNOTATION, value = ControllerConfiguration.class)
      },
      basePackages = {
          "io.javaoperatorsdk"
      })
  static class SpringBootTestApplication {
    // Need to override the CustomServiceReconciler Bean to set KubernetesClient to the mocked one.
    @Bean
    @Primary
    CustomServiceReconciler customServiceReconciler(KubernetesClient kubernetesClient) {
      return new CustomServiceReconciler(kubernetesClient);
    }
  }
}
