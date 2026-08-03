package io.javaoperatorsdk.operator.springboot.starter;

import io.javaoperatorsdk.operator.api.reconciler.*;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent;
import io.javaoperatorsdk.operator.springboot.starter.model.TestResource;

@ControllerConfiguration
@Workflow(dependents = {
    @Dependent(type = SpringManagedDependentResource.class),
    @Dependent(type = NoArgConstructorDependentResource.class)
})
public class SpringManagedDependentReconciler implements Reconciler<TestResource> {

  @Override
  public UpdateControl<TestResource> reconcile(TestResource testResource, Context context) {
    return UpdateControl.noUpdate();
  }
}
