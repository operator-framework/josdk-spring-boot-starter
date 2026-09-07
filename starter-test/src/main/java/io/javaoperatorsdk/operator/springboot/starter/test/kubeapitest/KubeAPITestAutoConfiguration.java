package io.javaoperatorsdk.operator.springboot.starter.test.kubeapitest;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

import io.javaoperatorsdk.operator.springboot.starter.OperatorAutoConfiguration;

/**
 * Orders kube-api-test bean registration before the starter's runtime auto-configuration.
 */
@AutoConfiguration(before = OperatorAutoConfiguration.class)
@Import(KubeAPITestConfiguration.class)
public class KubeAPITestAutoConfiguration {
}
