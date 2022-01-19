package io.javaoperatorsdk.operator.springboot.starter;

import io.javaoperatorsdk.operator.api.reconciler.*;
import io.javaoperatorsdk.operator.springboot.starter.model.TestResource;
import org.springframework.stereotype.Component;

@Component
@ControllerConfiguration
public class TestReconciler implements Reconciler<TestResource> {

  @Override
  public UpdateControl<TestResource> reconcile(TestResource testResource, Context context) {
    return UpdateControl.noUpdate();
  }

  @Override
  public DeleteControl cleanup(TestResource resource, Context context) {
    return Reconciler.super.cleanup(resource, context);
  }
}
