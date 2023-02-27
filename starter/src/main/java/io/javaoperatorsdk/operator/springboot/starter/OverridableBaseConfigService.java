package io.javaoperatorsdk.operator.springboot.starter;

import java.util.concurrent.ExecutorService;

import io.javaoperatorsdk.operator.api.config.BaseConfigurationService;
import io.javaoperatorsdk.operator.api.config.Cloner;
import io.javaoperatorsdk.operator.api.config.ResourceClassResolver;
import io.javaoperatorsdk.operator.api.config.Version;
import io.javaoperatorsdk.operator.api.monitoring.Metrics;

public class OverridableBaseConfigService extends BaseConfigurationService {

  private Metrics metrics;
  private ExecutorService executorService;
  private int concurrentReconciliationThreads = -1;
  private Cloner resourceCloner;
  private ResourceClassResolver resourceClassResolver;
  private Boolean checkCRDAndValidateLocalModel;

  public OverridableBaseConfigService(Version version) {
    super(version);
  }

  public OverridableBaseConfigService setMetrics(Metrics metrics) {
    this.metrics = metrics;
    return this;
  }

  public OverridableBaseConfigService setExecutorService(ExecutorService executorService) {
    this.executorService = executorService;
    return this;
  }

  public OverridableBaseConfigService setConcurrentReconciliationThreads(
      int concurrentReconciliationThreads) {
    this.concurrentReconciliationThreads = concurrentReconciliationThreads;
    return this;
  }

  public OverridableBaseConfigService setResourceCloner(Cloner resourceCloner) {
    this.resourceCloner = resourceCloner;
    return this;
  }

  public OverridableBaseConfigService setResourceClassResolver(
      ResourceClassResolver resourceClassResolver) {
    this.resourceClassResolver = resourceClassResolver;
    return this;
  }

  public OverridableBaseConfigService setCheckCRDAndValidateLocalModel(
      Boolean checkCRDAndValidateLocalModel) {
    this.checkCRDAndValidateLocalModel = checkCRDAndValidateLocalModel;
    return this;
  }

  @Override
  public Metrics getMetrics() {
    return this.metrics != null ? this.metrics : super.getMetrics();
  }

  @Override
  public ExecutorService getExecutorService() {
    return this.executorService != null ? this.executorService : super.getExecutorService();
  }

  @Override
  public int concurrentReconciliationThreads() {
    return this.concurrentReconciliationThreads != -1 ? this.concurrentReconciliationThreads
        : super.concurrentReconciliationThreads();
  }

  @Override
  public Cloner getResourceCloner() {
    return this.resourceCloner != null ? this.resourceCloner : super.getResourceCloner();
  }

  @Override
  public ResourceClassResolver getResourceClassResolver() {
    return this.resourceClassResolver != null ? this.resourceClassResolver
        : super.getResourceClassResolver();
  }

  @Override
  public boolean checkCRDAndValidateLocalModel() {
    return this.checkCRDAndValidateLocalModel != null ? checkCRDAndValidateLocalModel
        : super.checkCRDAndValidateLocalModel();
  }
}
