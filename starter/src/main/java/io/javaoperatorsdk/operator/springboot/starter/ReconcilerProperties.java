package io.javaoperatorsdk.operator.springboot.starter;

import java.time.Duration;
import java.util.Set;

public class ReconcilerProperties {
  private String name;
  private String finalizerName;
  private Boolean generationAware;
  private Boolean clusterScoped;
  private Set<String> namespaces;
  private RetryProperties retry;
  private String labelSelector;
  private Duration reconciliationMaxInterval;
  private String fieldManager;
  private Boolean triggerReconcilerOnAllEvents;
  private Long informerListLimit;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFinalizerName() {
    return finalizerName;
  }

  public void setFinalizerName(String finalizerName) {
    this.finalizerName = finalizerName;
  }

  public Boolean isGenerationAware() {
    return generationAware;
  }

  public void setGenerationAware(Boolean generationAware) {
    this.generationAware = generationAware;
  }

  public Boolean isClusterScoped() {
    return clusterScoped;
  }

  public void setClusterScoped(Boolean clusterScoped) {
    this.clusterScoped = clusterScoped;
  }

  public Set<String> getNamespaces() {
    return namespaces;
  }

  public void setNamespaces(Set<String> namespaces) {
    this.namespaces = namespaces;
  }

  public RetryProperties getRetry() {
    return retry;
  }

  public void setRetry(RetryProperties retry) {
    this.retry = retry;
  }

  public String getLabelSelector() {
    return labelSelector;
  }

  public void setLabelSelector(String labelSelector) {
    this.labelSelector = labelSelector;
  }

  public Duration getReconciliationMaxInterval() {
    return reconciliationMaxInterval;
  }

  public void setReconciliationMaxInterval(Duration reconciliationMaxInterval) {
    this.reconciliationMaxInterval = reconciliationMaxInterval;
  }

  public String getFieldManager() {
    return fieldManager;
  }

  public void setFieldManager(String fieldManager) {
    this.fieldManager = fieldManager;
  }

  public Boolean isTriggerReconcilerOnAllEvents() {
    return triggerReconcilerOnAllEvents;
  }

  public void setTriggerReconcilerOnAllEvents(Boolean triggerReconcilerOnAllEvents) {
    this.triggerReconcilerOnAllEvents = triggerReconcilerOnAllEvents;
  }

  public Long getInformerListLimit() {
    return informerListLimit;
  }

  public void setInformerListLimit(Long informerListLimit) {
    this.informerListLimit = informerListLimit;
  }
}
