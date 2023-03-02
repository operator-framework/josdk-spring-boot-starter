package io.javaoperatorsdk.operator.springboot.starter;

import java.util.Set;

public class ReconcilerProperties {
  private String name;
  private String finalizerName;
  private Boolean generationAware;
  private Boolean clusterScoped;
  private Set<String> namespaces;
  private RetryProperties retry;

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
}
