package io.javaoperatorsdk.operator.springboot.starter.properties;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.javaoperatorsdk.operator.api.config.ConfigurationService;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "javaoperatorsdk")
public class OperatorConfigurationProperties {

  CrdProperties crd = new CrdProperties();
  KubernetesClientProperties client = new KubernetesClientProperties();

  Map<String, ReconcilerProperties> reconcilers = Collections.emptyMap();
  boolean checkCrdAndValidateLocalModel = true;
  int concurrentReconciliationThreads = ConfigurationService.DEFAULT_RECONCILIATION_THREADS_NUMBER;
  Integer minConcurrentReconciliationThreads;
  Integer concurrentWorkflowExecutorThreads;
  Integer minConcurrentWorkflowExecutorThreads;
  Boolean closeClientOnStop;
  Boolean stopOnInformerErrorDuringStartup;
  Duration cacheSyncTimeout;
}
