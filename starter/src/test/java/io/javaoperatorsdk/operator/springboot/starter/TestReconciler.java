package io.javaoperatorsdk.operator.springboot.starter;

import org.springframework.stereotype.Component;

import io.javaoperatorsdk.operator.api.reconciler.*;
import io.javaoperatorsdk.operator.springboot.starter.model.TestResource;

@Component
@ControllerConfiguration
public class TestReconciler implements Reconciler<TestResource>, Cleaner<TestResource> {

  @Override
  public UpdateControl<TestResource> reconcile(TestResource testResource, Context context) {
    return UpdateControl.noUpdate();
  }

  @Override
  public DeleteControl cleanup(TestResource resource, Context context) {
    return DeleteControl.defaultDelete();
  }
}
