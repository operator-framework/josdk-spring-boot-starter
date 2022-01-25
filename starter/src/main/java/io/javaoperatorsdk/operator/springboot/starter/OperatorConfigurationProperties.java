package io.javaoperatorsdk.operator.springboot.starter;

import java.util.Collections;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.javaoperatorsdk.operator.api.config.ConfigurationService;

@ConfigurationProperties(prefix = "javaoperatorsdk")
public class OperatorConfigurationProperties {

  private KubernetesClientProperties client = new KubernetesClientProperties();
  private Map<String, ReconcilerProperties> reconcilers = Collections.emptyMap();
  private boolean checkCrdAndValidateLocalModel = true;
  private int concurrentReconciliationThreads =
      ConfigurationService.DEFAULT_RECONCILIATION_THREADS_NUMBER;

  public KubernetesClientProperties getClient() {
    return client;
  }

  public void setClient(KubernetesClientProperties client) {
    this.client = client;
  }

  public Map<String, ReconcilerProperties> getReconcilers() {
    return reconcilers;
  }

  public void setReconcilers(Map<String, ReconcilerProperties> reconcilers) {
    this.reconcilers = reconcilers;
  }

  public boolean getCheckCrdAndValidateLocalModel() {
    return checkCrdAndValidateLocalModel;
  }

  public void setCheckCrdAndValidateLocalModel(boolean checkCrdAndValidateLocalModel) {
    this.checkCrdAndValidateLocalModel = checkCrdAndValidateLocalModel;
  }

  public int getConcurrentReconciliationThreads() {
    return concurrentReconciliationThreads;
  }

  public void setConcurrentReconciliationThreads(int concurrentReconciliationThreads) {
    this.concurrentReconciliationThreads = concurrentReconciliationThreads;
  }
}
