package io.javaoperatorsdk.operator.springboot.starter;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import io.javaoperatorsdk.operator.Operator;
import io.javaoperatorsdk.operator.api.config.ConfigurationServiceOverrider;
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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

  @Test
  @SuppressWarnings("unchecked")
  void ambiguousUserProvidedFactoriesAreLeftForJosdksOwnDefaultInsteadOfFailing() {
    // two non-primary DependentResourceFactory beans are ambiguous; applying the overrider must
    // not try to resolve one of them (that would throw NoUniqueBeanDefinitionException) and
    // should instead leave JOSDK's own default factory in place.
    DependentResourceFactory<?, ?> first = mock(DependentResourceFactory.class);
    DependentResourceFactory<?, ?> second = mock(DependentResourceFactory.class);

    runner.withBean("firstDependentResourceFactory", DependentResourceFactory.class, () -> first)
        .withBean("secondDependentResourceFactory", DependentResourceFactory.class, () -> second)
        .run(ctx -> {
          Consumer<ConfigurationServiceOverrider> overriderConsumer =
              (Consumer<ConfigurationServiceOverrider>) ctx
                  .getBean("dependentResourceFactoryConfigServiceOverrider");
          ConfigurationServiceOverrider overrider = mock(ConfigurationServiceOverrider.class);

          assertThatCode(() -> overriderConsumer.accept(overrider)).doesNotThrowAnyException();
          verify(overrider, never()).withDependentResourceFactory(any());
        });
  }
}
