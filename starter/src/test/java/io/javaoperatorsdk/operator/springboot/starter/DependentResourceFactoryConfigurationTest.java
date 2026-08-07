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

  @Test
  void doesNotCreateSpringDependentResourceFactoryWhenDisabledByProperty() {
    // with no factory bean to fall back on, JOSDK keeps using its own reflection-based default.
    runner.withPropertyValues("javaoperatorsdk.dependent-resources.spring-managed=false")
        .run(ctx -> assertThat(ctx).doesNotHaveBean(DependentResourceFactory.class));
  }

  @Test
  void userProvidedDependentResourceFactoryStillAppliesWhenDisabledByProperty() {
    DependentResourceFactory<?, ?> custom = mock(DependentResourceFactory.class);

    runner.withPropertyValues("javaoperatorsdk.dependent-resources.spring-managed=false")
        .withBean("customDependentResourceFactory", DependentResourceFactory.class, () -> custom)
        .run(ctx -> assertThat(ctx)
            .getBean(DependentResourceFactory.class)
            .isSameAs(custom));
  }
}
