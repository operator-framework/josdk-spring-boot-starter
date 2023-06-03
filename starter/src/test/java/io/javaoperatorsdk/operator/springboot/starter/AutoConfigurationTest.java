package io.javaoperatorsdk.operator.springboot.starter;

import java.util.List;
import java.util.function.Consumer;

import io.javaoperatorsdk.operator.api.config.ConfigurationServiceOverrider;
import io.javaoperatorsdk.operator.api.config.ConfigurationServiceProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.Operator;
import io.javaoperatorsdk.operator.ReconcilerUtils;
import io.javaoperatorsdk.operator.api.config.Cloner;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AutoConfigurationTest {

  static final int CUSTOM_RECONCILE_THREADS = 42;

  @Autowired
  private OperatorConfigurationProperties config;

  @MockBean
  private Operator operator;

  @Autowired
  private KubernetesClient kubernetesClient;

  @Autowired
  private List<Reconciler<?>> reconcilers;

  @Autowired
  private Consumer<ConfigurationServiceOverrider> compositeConfigurationServiceOverrider;

  @MockBean
  private Cloner cloner;

  @Test
  public void loadsKubernetesClientPropertiesProperly() {
    final var operatorProperties = config.getClient();
    assertEquals("user", operatorProperties.getUsername().get());
    assertEquals("password", operatorProperties.getPassword().get());
    assertEquals("token", operatorProperties.getOauthToken().get());
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
    assertNotNull(compositeConfigurationServiceOverrider);
  }

  @Test
  public void configServiceOverridesAppliedInCorrectOrder() {
    var configurationService = ConfigurationServiceProvider.overrideCurrent(compositeConfigurationServiceOverrider);

    assertThat(config.getConcurrentReconciliationThreads())
        .isNotEqualTo(CUSTOM_RECONCILE_THREADS);
    assertThat(configurationService.concurrentReconciliationThreads())
        .isEqualTo(CUSTOM_RECONCILE_THREADS);
    assertEquals(configurationService.getResourceCloner(), cloner);
  }

  @Test
  public void reconcilersAreDiscovered() {
    assertEquals(1, reconcilers.size());
    assertTrue(reconcilers.get(0) instanceof TestReconciler);
  }

}
