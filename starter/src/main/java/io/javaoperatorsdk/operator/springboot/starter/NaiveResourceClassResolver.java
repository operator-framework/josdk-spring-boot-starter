package io.javaoperatorsdk.operator.springboot.starter;

import io.fabric8.kubernetes.client.CustomResource;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import org.springframework.core.ResolvableType;

/**
 * Uses a naive reflection-based strategy for resolving a Reconciler's CustomResource class.
 *
 * <p>This strategy may break should a Reconciler implement more than one interface, or if the
 * Reconciler extends an abstract class.
 */
public class NaiveResourceClassResolver implements ResourceClassResolver {

  @Override
  @SuppressWarnings("unchecked")
  public <R extends CustomResource<?, ?>> Class<R> resolveCustomResourceClass(
    Reconciler<?> reconciler) {
    final var type = ResolvableType.forClass(reconciler.getClass());
    return (Class<R>) type.getInterfaces()[0].resolveGeneric(0);
  }
}
