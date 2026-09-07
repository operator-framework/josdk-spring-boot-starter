package io.javaoperatorsdk.operator.springboot.starter.test.kubeapitest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.javaoperatorsdk.operator.springboot.starter.CRDApplier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    classes = testsupport.KubeAPITestOptOutApplication.class,
    properties = "javaoperatorsdk.crd.apply-on-startup=true")
@EnableKubeAPITest
class EnableKubeAPITestPropertyTests {

  @Autowired
  private CRDApplier crdApplier;

  @Test
  void preservesExplicitCrdApplicationProperty() {
    assertThat(crdApplier).isNotSameAs(CRDApplier.NOOP);
  }
}
