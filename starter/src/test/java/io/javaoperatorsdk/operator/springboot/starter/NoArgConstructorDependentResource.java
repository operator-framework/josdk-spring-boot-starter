package io.javaoperatorsdk.operator.springboot.starter;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.springboot.starter.model.TestResource;

public class NoArgConstructorDependentResource
    extends CRUDKubernetesDependentResource<ConfigMap, TestResource> {

  public NoArgConstructorDependentResource() {
    super(ConfigMap.class);
  }

  @Override
  protected ConfigMap desired(TestResource primary, Context<TestResource> context) {
    return new ConfigMapBuilder()
        .withNewMetadata()
        .withName(primary.getMetadata().getName() + "-no-arg")
        .withNamespace(primary.getMetadata().getNamespace())
        .endMetadata()
        .build();
  }
}
