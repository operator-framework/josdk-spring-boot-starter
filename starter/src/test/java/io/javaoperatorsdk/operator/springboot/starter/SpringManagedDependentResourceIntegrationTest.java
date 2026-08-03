package io.javaoperatorsdk.operator.springboot.starter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import io.javaoperatorsdk.operator.Operator;
import io.javaoperatorsdk.operator.ReconcilerUtilsInternal;
import io.javaoperatorsdk.operator.api.config.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;

@SpringBootTest(properties = {
    "javaoperatorsdk.client.masterUrl=http://master.url",
    "javaoperatorsdk.client.username=user",
    "javaoperatorsdk.client.password=password",
    "javaoperatorsdk.client.oauthToken=token"
})
public class SpringManagedDependentResourceIntegrationTest {

  @MockitoSpyBean
  private Operator operator;

  @Autowired
  private GreetingService greetingService;

  @Test
  void managedDependentResourceIsCreatedThroughSpringAndReceivesInjectedBean() {
    var dependentResource = SpringManagedDependentResource.LAST_CREATED_INSTANCE.get();

    assertThat(dependentResource).isNotNull();
    assertThat(dependentResource.getGreetingService()).isSameAs(greetingService);
  }

  @Test
  void managedDependentResourceIsStillConfiguredByJosdk() {
    var dependentResource = SpringManagedDependentResource.LAST_CREATED_INSTANCE.get();

    assertThat(dependentResource.configuration()).isPresent();
  }

  @Test
  void managedDependentResourcesAreRegisteredInTheReconcilerWorkflow() {
    var controller = operator
        .getRegisteredController(
            ReconcilerUtilsInternal.getNameFor(SpringManagedDependentReconciler.class))
        .orElseThrow();

    ControllerConfiguration<?> configuration = controller.getConfiguration();
    var dependentResourceClasses = configuration.getWorkflowSpec()
        .orElseThrow()
        .getDependentResourceSpecs()
        .stream()
        .map(spec -> spec.getDependentResourceClass())
        .toList();

    assertThat(dependentResourceClasses)
        .containsExactlyInAnyOrder(SpringManagedDependentResource.class,
            NoArgConstructorDependentResource.class);
  }

  @TestConfiguration
  static class TestConfig {

    @Bean
    public GreetingService greetingService() {
      return () -> "hello from spring";
    }

    @Bean
    public Reconciler<?> springManagedDependentReconciler() {
      return new SpringManagedDependentReconciler();
    }

    @Bean
    public BeanPostProcessor operatorStartSuppressor() {
      return new BeanPostProcessor() {
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
          if (bean instanceof Operator operator) {
            doNothing().when(operator).start();
          }
          return bean;
        }
      };
    }
  }
}
