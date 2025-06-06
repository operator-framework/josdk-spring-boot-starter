package io.javaoperatorsdk.operator.springboot.starter.sample.webpage.dependentresource;

import java.util.HashMap;
import java.util.Map;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.javaoperatorsdk.operator.api.config.informer.Informer;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;
import io.javaoperatorsdk.operator.springboot.starter.sample.webpage.customresource.WebPage;

import static io.javaoperatorsdk.operator.springboot.starter.sample.webpage.Utils.configMapName;
import static io.javaoperatorsdk.operator.springboot.starter.sample.webpage.WebPageManagedDependentsReconciler.SELECTOR;

// this annotation only activates when using managed dependents and is not otherwise needed
@KubernetesDependent(informer = @Informer(labelSelector = SELECTOR))
public class ConfigMapDependentResource
    extends CRUDKubernetesDependentResource<ConfigMap, WebPage> {

  @Override
  protected ConfigMap desired(WebPage webPage, Context<WebPage> context) {
    Map<String, String> data = new HashMap<>();
    data.put("index.html", webPage.getSpec().getHtml());
    Map<String, String> labels = new HashMap<>();
    labels.put(SELECTOR, "true");
    return new ConfigMapBuilder()
        .withMetadata(
            new ObjectMetaBuilder()
                .withName(configMapName(webPage))
                .withNamespace(webPage.getMetadata().getNamespace())
                .withLabels(labels)
                .build())
        .withData(data)
        .build();
  }
}
