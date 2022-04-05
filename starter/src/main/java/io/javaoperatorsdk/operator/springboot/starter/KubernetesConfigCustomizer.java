package io.javaoperatorsdk.operator.springboot.starter;

import io.fabric8.kubernetes.client.ConfigBuilder;

/**
 * Callback interface that can be implemented by beans wishing to further customize the
 * {@link ConfigBuilder}.
 */
@FunctionalInterface
public interface KubernetesConfigCustomizer {

  void customize(ConfigBuilder configBuilder);
}
