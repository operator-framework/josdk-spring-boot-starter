package io.javaoperatorsdk.operator.springboot.starter;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import io.javaoperatorsdk.operator.api.config.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.config.dependent.DependentResourceSpec;
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource;
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResourceFactory;

/**
 * {@link DependentResourceFactory} that creates managed dependent resource instances through
 * Spring's {@link AutowireCapableBeanFactory}, instead of relying on a no-arg constructor. This
 * allows managed dependent resources to receive Spring-managed dependencies, including via
 * constructor injection, while still going through standard bean post-processing.
 * <p>
 * The created instances are not registered as named beans in the application context: each managed
 * dependent resource keeps the lifecycle expected by Java Operator SDK instead of becoming an
 * application-wide singleton.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class SpringDependentResourceFactory implements DependentResourceFactory {

  private final AutowireCapableBeanFactory beanFactory;

  public SpringDependentResourceFactory(AutowireCapableBeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }

  @Override
  public DependentResource createFrom(DependentResourceSpec spec,
      ControllerConfiguration controllerConfiguration) {
    final DependentResource instance =
        (DependentResource) beanFactory.createBean(spec.getDependentResourceClass());
    configure(instance, spec, controllerConfiguration);
    return instance;
  }

  @Override
  public Class<?> associatedResourceType(DependentResourceSpec spec) {
    final DependentResource instance =
        (DependentResource) beanFactory.createBean(spec.getDependentResourceClass());
    return instance.resourceType();
  }
}
