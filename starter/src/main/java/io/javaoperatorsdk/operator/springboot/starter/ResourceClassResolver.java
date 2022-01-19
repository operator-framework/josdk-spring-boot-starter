package io.javaoperatorsdk.operator.springboot.starter;

import io.fabric8.kubernetes.client.CustomResource;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;

/** Resolves the CustomResource class of a given Reconciler object. */
@FunctionalInterface
public interface ResourceClassResolver {

  <R extends CustomResource<?, ?>> Class<R> resolveCustomResourceClass(Reconciler<?> reconciler);
}
