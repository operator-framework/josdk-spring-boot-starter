package io.javaoperatorsdk.operator.springboot.starter.sample;

import java.util.Map;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;

@KubernetesDependent(labelSelector = "app.kubernetes.io/managed-by=custom-service-operator")
public class ConfigMapDpendentResource
    extends CRUDKubernetesDependentResource<ConfigMap, CustomService> {
  public ConfigMapDpendentResource() {
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
