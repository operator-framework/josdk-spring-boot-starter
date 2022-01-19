package io.javaoperatorsdk.operator.springboot.starter;

import java.util.Set;

public class ReconcilerProperties {
  private String name;
  private String finalizer;
  private boolean generationAware;
  private boolean clusterScoped;
  private Set<String> namespaces;
  private RetryProperties retry;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFinalizer() {
    return finalizer;
  }

  public void setFinalizer(String finalizer) {
    this.finalizer = finalizer;
  }

  public boolean isGenerationAware() {
    return generationAware;
  }

  public void setGenerationAware(boolean generationAware) {
    this.generationAware = generationAware;
  }

  public boolean isClusterScoped() {
    return clusterScoped;
  }

  public void setClusterScoped(boolean clusterScoped) {
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
