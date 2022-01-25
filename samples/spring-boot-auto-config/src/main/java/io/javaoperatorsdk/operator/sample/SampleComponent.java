package io.javaoperatorsdk.operator.sample;

import org.springframework.stereotype.Component;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.Operator;

/** This component just showcases what beans are registered. */
@Component
public class SampleComponent {

  private final Operator operator;

  private final KubernetesClient kubernetesClient;

  private final CustomServiceReconciler customServiceReconciler;

  public SampleComponent(
      Operator operator,
      KubernetesClient kubernetesClient,
      CustomServiceReconciler customServiceReconciler) {
    this.operator = operator;
    this.kubernetesClient = kubernetesClient;
    this.customServiceReconciler = customServiceReconciler;
  }
}
