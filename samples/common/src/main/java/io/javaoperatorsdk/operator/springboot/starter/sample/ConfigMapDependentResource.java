package io.javaoperatorsdk.operator.springboot.starter.sample;

import java.util.Map;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.javaoperatorsdk.operator.api.config.informer.Informer;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;

@KubernetesDependent(
    informer = @Informer(labelSelector = "app.kubernetes.io/managed-by=custom-service-operator"))
public class ConfigMapDependentResource
    extends CRUDKubernetesDependentResource<ConfigMap, CustomService> {
  public ConfigMapDependentResource() {
    super(ConfigMap.class);
  }

  @Override
  protected ConfigMap desired(CustomService primary, Context<CustomService> context) {
    return new ConfigMapBuilder()
        .withMetadata(new ObjectMetaBuilder()
            .withName(primary.getMetadata().getName() + "-config")
            .withNamespace(primary.getMetadata().getNamespace())
            .build())
        .withData(Map.of("foo", "bar"))
        .build();
  }
}
