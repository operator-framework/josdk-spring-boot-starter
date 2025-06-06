package io.javaoperatorsdk.operator.springboot.starter.sample.webpage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.javaoperatorsdk.operator.api.reconciler.Cleaner;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.DeleteControl;
import io.javaoperatorsdk.operator.api.reconciler.ErrorStatusUpdateControl;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import io.javaoperatorsdk.operator.api.reconciler.Workflow;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent;
import io.javaoperatorsdk.operator.springboot.starter.sample.webpage.customresource.WebPage;
import io.javaoperatorsdk.operator.springboot.starter.sample.webpage.customresource.WebPageStatus;
import io.javaoperatorsdk.operator.springboot.starter.sample.webpage.dependentresource.ConfigMapDependentResource;
import io.javaoperatorsdk.operator.springboot.starter.sample.webpage.dependentresource.DeploymentDependentResource;
import io.javaoperatorsdk.operator.springboot.starter.sample.webpage.dependentresource.ExposedIngressCondition;
import io.javaoperatorsdk.operator.springboot.starter.sample.webpage.dependentresource.IngressDependentResource;
import io.javaoperatorsdk.operator.springboot.starter.sample.webpage.dependentresource.ServiceDependentResource;

import static io.javaoperatorsdk.operator.springboot.starter.sample.webpage.Utils.createWebPageForStatusUpdate;

/** Shows how to implement a reconciler with managed dependent resources. */
@Workflow(
    dependents = {
        @Dependent(type = ConfigMapDependentResource.class),
        @Dependent(type = DeploymentDependentResource.class),
        @Dependent(type = ServiceDependentResource.class),
        @Dependent(
            type = IngressDependentResource.class,
            reconcilePrecondition = ExposedIngressCondition.class)
    })
@Component
@ControllerConfiguration(name = "webpage")
public class WebPageManagedDependentsReconciler implements Reconciler<WebPage>, Cleaner<WebPage> {

  public static final String SELECTOR = "managed";

  private static final Logger log =
      LoggerFactory.getLogger(WebPageManagedDependentsReconciler.class);

  @Override
  public ErrorStatusUpdateControl<WebPage> updateErrorStatus(
      WebPage resource, Context<WebPage> context, Exception e) {
    return handleError(resource, e);
  }

  @Override
  public UpdateControl<WebPage> reconcile(WebPage webPage, Context<WebPage> context) {
    final var name =
        context.getSecondaryResource(ConfigMap.class).orElseThrow().getMetadata().getName();

    log.info("Reconciled webpage with name: {} namespace: {}",
        webPage.getMetadata().getName(), webPage.getMetadata().getNamespace());

    return UpdateControl.patchStatus(createWebPageForStatusUpdate(webPage, name));
  }

  private static ErrorStatusUpdateControl<WebPage> handleError(WebPage resource, Exception e) {
    if (resource.getStatus() == null) {
      resource.setStatus(new WebPageStatus());
    }
    resource.getStatus().setErrorMessage("Error: " + e.getMessage());
    return ErrorStatusUpdateControl.patchStatus(resource);
  }

  @Override
  public DeleteControl cleanup(WebPage resource, Context<WebPage> context) {
    return DeleteControl.defaultDelete();
  }


}
