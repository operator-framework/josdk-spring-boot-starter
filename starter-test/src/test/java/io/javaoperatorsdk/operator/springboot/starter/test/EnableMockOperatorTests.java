package io.javaoperatorsdk.operator.springboot.starter.test;

import static org.assertj.core.api.Assertions.assertThat;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.Operator;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@JsonTest
@EnableMockOperator(crdPaths = "classpath:crd.yml")
class EnableMockOperatorTests {

  @Autowired KubernetesClient client;

  @Autowired ApplicationContext applicationContext;

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

  @SpringBootApplication
  @ComponentScan(
    includeFilters = {
      @ComponentScan.Filter(type = FilterType.ANNOTATION, value = ControllerConfiguration.class)
    },
    basePackages = {
      "io.javaoperatorsdk"
    })
  static class SpringBootTestApplication {}
}
