package io.javaoperatorsdk.operator.springboot.starter.test.kubeapitest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.fabric8.kubernetes.client.Config;
import io.javaoperatorsdk.operator.springboot.starter.CRDApplier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = testsupport.KubeAPITestOptOutApplication.class)
@EnableKubeAPITest
class EnableKubeAPITestTests {

  @Autowired
  private CRDApplier crdApplier;

  @Autowired
  private Config config;

  @Test
  void doesNotApplyCrdsByDefault() {
    assertThat(crdApplier).isSameAs(CRDApplier.NOOP);
  }

  @Test
  void preservesExplicitClientConfiguration() {
    assertThat(config.getMasterUrl()).startsWith("http://localhost");
  }

}
