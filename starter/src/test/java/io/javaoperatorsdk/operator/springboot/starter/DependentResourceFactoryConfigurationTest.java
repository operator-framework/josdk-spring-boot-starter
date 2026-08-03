package io.javaoperatorsdk.operator.springboot.starter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import io.javaoperatorsdk.operator.Operator;
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DependentResourceFactoryConfigurationTest {

  private static final ApplicationContextRunner runner = new ApplicationContextRunner()
      .withUserConfiguration(OperatorAutoConfiguration.class)
      .withBean(Operator.class, () -> mock(Operator.class));

  @Test
  void createsSpringDependentResourceFactoryByDefault() {
    runner.run(ctx -> assertThat(ctx)
        .getBean(DependentResourceFactory.class)
        .isInstanceOf(SpringDependentResourceFactory.class));
  }

  @Test
  void userProvidedDependentResourceFactoryTakesPrecedence() {
    DependentResourceFactory<?, ?> custom = mock(DependentResourceFactory.class);

    runner.withBean("customDependentResourceFactory", DependentResourceFactory.class, () -> custom)
        .run(ctx -> assertThat(ctx)
            .getBean(DependentResourceFactory.class)
            .isSameAs(custom));
  }
}
