package io.javaoperatorsdk.operator.springboot.starter;

import java.util.concurrent.atomic.AtomicReference;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.springboot.starter.model.TestResource;

public class SpringManagedDependentResource
    extends CRUDKubernetesDependentResource<ConfigMap, TestResource> {

  static final AtomicReference<SpringManagedDependentResource> LAST_CREATED_INSTANCE =
      new AtomicReference<>();

  private final GreetingService greetingService;

  public SpringManagedDependentResource(GreetingService greetingService) {
    super(ConfigMap.class);
    this.greetingService = greetingService;
    LAST_CREATED_INSTANCE.set(this);
  }

  GreetingService getGreetingService() {
    return greetingService;
  }

  @Override
  protected ConfigMap desired(TestResource primary, Context<TestResource> context) {
    return new ConfigMapBuilder()
        .withNewMetadata()
        .withName(primary.getMetadata().getName())
        .withNamespace(primary.getMetadata().getNamespace())
        .endMetadata()
        .addToData("greeting", greetingService.greeting())
        .build();
  }
}
