package io.javaoperatorsdk.operator.springboot.starter;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.Operator;
import io.javaoperatorsdk.operator.ReconcilerUtils;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AutoConfigurationTest {

  @Autowired
  private OperatorConfigurationProperties config;

  @MockBean
  private Operator operator;

  @Autowired
  private KubernetesClient kubernetesClient;

  @Autowired
  private List<Reconciler<?>> reconcilers;

  @Test
  public void loadsKubernetesClientPropertiesProperly() {
    final var operatorProperties = config.getClient();
    assertEquals("user", operatorProperties.getUsername().get());
    assertEquals("password", operatorProperties.getPassword().get());
    assertEquals("http://master.url", operatorProperties.getMasterUrl().get());
  }

  @Test
  public void loadsRetryPropertiesProperly() {
    final var retryProperties =
        config.getReconcilers().get(ReconcilerUtils.getNameFor(TestReconciler.class)).getRetry();
    assertEquals(3, retryProperties.getMaxAttempts());
    assertEquals(1000, retryProperties.getInitialInterval());
    assertEquals(1.5, retryProperties.getIntervalMultiplier());
    assertEquals(50000, retryProperties.getMaxInterval());
  }

  @Test
  public void beansCreated() {
    assertNotNull(kubernetesClient);
  }

  @Test
  public void reconcilersAreDiscovered() {
    assertEquals(1, reconcilers.size());
    assertTrue(reconcilers.get(0) instanceof TestReconciler);
  }
}
