package io.javaoperatorsdk.operator.springboot.starter;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import io.javaoperatorsdk.operator.api.config.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.config.dependent.DependentResourceSpec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class SpringDependentResourceFactoryTest {

  private final AutowireCapableBeanFactory beanFactory = mock(AutowireCapableBeanFactory.class);
  private final SpringDependentResourceFactory factory =
      new SpringDependentResourceFactory(beanFactory);

  @Test
  @SuppressWarnings("unchecked")
  void associatedResourceTypeDestroysTheThrowawayInstanceItCreated() {
    DependentResourceSpec spec = mock(DependentResourceSpec.class);
    when(spec.getDependentResourceClass())
        .thenReturn((Class) NoArgConstructorDependentResource.class);
    NoArgConstructorDependentResource instance = new NoArgConstructorDependentResource();
    when(beanFactory.createBean(NoArgConstructorDependentResource.class)).thenReturn(instance);

    Class<?> resourceType = factory.associatedResourceType(spec);

    assertThat(resourceType).isEqualTo(instance.resourceType());
    InOrder inOrder = inOrder(beanFactory);
    inOrder.verify(beanFactory).createBean(NoArgConstructorDependentResource.class);
    inOrder.verify(beanFactory).destroyBean(instance);
    verifyNoMoreInteractions(beanFactory);
  }

  @Test
  @SuppressWarnings("unchecked")
  void associatedResourceTypeDestroysTheThrowawayInstanceEvenOnFailure() {
    DependentResourceSpec spec = mock(DependentResourceSpec.class);
    when(spec.getDependentResourceClass())
        .thenReturn((Class) NoArgConstructorDependentResource.class);
    NoArgConstructorDependentResource instance = mock(NoArgConstructorDependentResource.class);
    when(beanFactory.createBean(NoArgConstructorDependentResource.class)).thenReturn(instance);
    when(instance.resourceType()).thenThrow(new IllegalStateException("boom"));

    org.junit.jupiter.api.function.Executable call = () -> factory.associatedResourceType(spec);

    org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, call);
    verify(beanFactory).destroyBean(instance);
  }

  @Test
  @SuppressWarnings("unchecked")
  void createFromDoesNotDestroyTheReturnedInstance() {
    DependentResourceSpec spec = mock(DependentResourceSpec.class);
    when(spec.getDependentResourceClass())
        .thenReturn((Class) NoArgConstructorDependentResource.class);
    NoArgConstructorDependentResource instance = new NoArgConstructorDependentResource();
    when(beanFactory.createBean(NoArgConstructorDependentResource.class)).thenReturn(instance);
    ControllerConfiguration<?> controllerConfiguration = mock(ControllerConfiguration.class);

    var created = factory.createFrom(spec, controllerConfiguration);

    assertThat(created).isSameAs(instance);
    verify(beanFactory).createBean(NoArgConstructorDependentResource.class);
    verify(beanFactory, org.mockito.Mockito.never()).destroyBean(any());
  }
}
