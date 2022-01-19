package io.javaoperatorsdk.operator.springboot.starter;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.http.HttpClient;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftConfig;
import io.javaoperatorsdk.operator.Operator;
import io.javaoperatorsdk.operator.ReconcilerUtils;
import io.javaoperatorsdk.operator.api.config.AbstractConfigurationService;
import io.javaoperatorsdk.operator.api.config.RetryConfiguration;
import io.javaoperatorsdk.operator.api.config.Utils;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.config.runtime.AnnotationConfiguration;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OperatorConfigurationProperties.class)
public class OperatorAutoConfiguration extends AbstractConfigurationService {
  @Autowired private OperatorConfigurationProperties configuration;

  public OperatorAutoConfiguration() {
    super(Utils.loadFromProperties());
  }

  @Bean
  @ConditionalOnMissingBean
  public KubernetesClient kubernetesClient(Optional<HttpClient.Factory> httpClientFactory) {
    final var config = getClientConfiguration();
    return configuration.getClient().isOpenshift()
      ? httpClientFactory
        .map(it -> new DefaultOpenShiftClient(it.createHttpClient(config), new OpenShiftConfig(config)))
        .orElseGet(() -> new DefaultOpenShiftClient(config))
      : httpClientFactory
        .map(it -> new DefaultKubernetesClient(it.createHttpClient(config), config))
        .orElseGet(() -> new DefaultKubernetesClient(config));
  }

  @Override
  public Config getClientConfiguration() {
    final var clientCfg = configuration.getClient();
    ConfigBuilder config = new ConfigBuilder();
    config.withTrustCerts(clientCfg.isTrustSelfSignedCertificates());
    clientCfg.getMasterUrl().ifPresent(config::withMasterUrl);
    clientCfg.getUsername().ifPresent(config::withUsername);
    clientCfg.getPassword().ifPresent(config::withPassword);
    return config.build();
  }

  @Override
  public boolean checkCRDAndValidateLocalModel() {
    return configuration.getCheckCrdAndValidateLocalModel();
  }

  @Bean
  @ConditionalOnMissingBean(ResourceClassResolver.class)
  public ResourceClassResolver resourceClassResolver() {
    return new NaiveResourceClassResolver();
  }

  @Bean(destroyMethod = "stop")
  @ConditionalOnMissingBean(Operator.class)
  public Operator operator(
    KubernetesClient kubernetesClient,
    List<Reconciler<?>> reconcilers,
    ResourceClassResolver resourceClassResolver) {
    Operator operator = new Operator(kubernetesClient, this);

    reconcilers.forEach(r -> operator.register(processReconciler(r, resourceClassResolver)));

    operator.start();

    return operator;
  }

  private Reconciler<?> processReconciler(
    Reconciler<?> reconciler, ResourceClassResolver resourceClassResolver) {
    final var reconcilerPropertiesMap = configuration.getReconcilers();
    final var name = ReconcilerUtils.getNameFor(reconciler);
    var controllerProps = reconcilerPropertiesMap.get(name);
    register(new ConfigurationWrapper(reconciler, controllerProps, resourceClassResolver));
    return reconciler;
  }

  private static class ConfigurationWrapper<R extends CustomResource<?, ?>>
    extends AnnotationConfiguration<R> {
    private final Optional<ReconcilerProperties> properties;
    private final Reconciler<R> reconciler;
    private final ResourceClassResolver resourceClassResolver;

    private ConfigurationWrapper(
      Reconciler<R> reconciler,
      ReconcilerProperties properties,
      ResourceClassResolver resourceClassResolver) {
      super(reconciler);
      this.reconciler = reconciler;
      this.properties = Optional.ofNullable(properties);
      this.resourceClassResolver = resourceClassResolver;
    }

    @Override
    public String getName() {
      return super.getName();
    }

    @Override
    public String getFinalizer() {
      return properties.map(ReconcilerProperties::getFinalizer).orElse(super.getFinalizer());
    }

    @Override
    public boolean isGenerationAware() {
      return properties
        .map(ReconcilerProperties::isGenerationAware)
        .orElse(super.isGenerationAware());
    }

    @Override
    public Class<R> getResourceClass() {
      return resourceClassResolver.resolveCustomResourceClass(reconciler);
    }

    @Override
    public Set<String> getNamespaces() {
      return properties.map(ReconcilerProperties::getNamespaces).orElse(super.getNamespaces());
    }

    @Override
    public boolean watchAllNamespaces() {
      return super.watchAllNamespaces();
    }

    @Override
    public RetryConfiguration getRetryConfiguration() {
      return properties
        .map(ReconcilerProperties::getRetry)
        .map(RetryProperties::asRetryConfiguration)
        .orElse(RetryConfiguration.DEFAULT);
    }
  }

  @Override
  public int concurrentReconciliationThreads() {
    return configuration.getConcurrentReconciliationThreads();
  }
}
