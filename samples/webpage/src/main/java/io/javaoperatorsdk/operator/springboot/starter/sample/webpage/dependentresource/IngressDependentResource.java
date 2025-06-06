package io.javaoperatorsdk.operator.springboot.starter.sample.webpage.dependentresource;

import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.javaoperatorsdk.operator.api.config.informer.Informer;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;
import io.javaoperatorsdk.operator.springboot.starter.sample.webpage.WebPageManagedDependentsReconciler;
import io.javaoperatorsdk.operator.springboot.starter.sample.webpage.customresource.WebPage;

import static io.javaoperatorsdk.operator.springboot.starter.sample.webpage.Utils.makeDesiredIngress;


// this annotation only activates when using managed dependents and is not otherwise needed
@KubernetesDependent(
    informer = @Informer(labelSelector = WebPageManagedDependentsReconciler.SELECTOR))
public class IngressDependentResource extends CRUDKubernetesDependentResource<Ingress, WebPage> {

  @Override
  protected Ingress desired(WebPage webPage, Context<WebPage> context) {
    return makeDesiredIngress(webPage);
  }
}
