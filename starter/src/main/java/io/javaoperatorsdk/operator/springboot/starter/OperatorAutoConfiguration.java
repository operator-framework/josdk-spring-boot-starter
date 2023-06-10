package io.javaoperatorsdk.operator.springboot.starter;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.http.HttpClient;
import io.fabric8.openshift.client.OpenShiftClient;
import io.javaoperatorsdk.operator.Operator;
import io.javaoperatorsdk.operator.ReconcilerUtils;
import io.javaoperatorsdk.operator.api.config.*;
import io.javaoperatorsdk.operator.api.monitoring.Metrics;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.processing.retry.GenericRetry;
import io.javaoperatorsdk.operator.springboot.starter.CrdUploader.CrdTransformer;

@Configuration
@EnableConfigurationProperties(OperatorConfigurationProperties.class)
public class OperatorAutoConfiguration {

  private final static Logger log = LoggerFactory.getLogger(OperatorAutoConfiguration.class);

  @Autowired
  private OperatorConfigurationProperties configuration;

  @Bean
  @ConditionalOnMissingBean
  public KubernetesClient kubernetesClient(Optional<HttpClient.Factory> httpClientFactory,
      Config config) {
    return configuration.getClient().isOpenshift()
        ? httpClientFactory
            .map(it -> new KubernetesClientBuilder().withHttpClientFactory(it).withConfig(config)
                .build().adapt(OpenShiftClient.class))
            // new DefaultOpenShiftClient(it.createHttpClient(config),
            // new OpenShiftConfig(config)))
            .orElseGet(() -> new KubernetesClientBuilder().withConfig(config)
                .build().adapt(OpenShiftClient.class))
        : httpClientFactory
            .map(it -> new KubernetesClientBuilder().withHttpClientFactory(it).withConfig(config)
                .build())
            .orElseGet(() -> new KubernetesClientBuilder().withConfig(config)
                .build());
  }

  @Bean
  @ConditionalOnMissingBean(ResourceClassResolver.class)
  public ResourceClassResolver resourceClassResolver() {
    return new DefaultResourceClassResolver();
  }

  @Bean
  public CrdUploader crdUploader(KubernetesClient client, List<CrdTransformer> transformers) {
    var crd = configuration.getCrd();
    return new CrdUploader(client, transformers,
        crd.isApplyOnStartup(), crd.getPath(), crd.getSuffix());
  }

  @Bean
  public OperatorStarter operatorStarter(Operator operator, CrdUploader uploader) {
    return new OperatorStarter(operator, uploader);
  }

  @Bean(destroyMethod = "stop")
  @ConditionalOnMissingBean(Operator.class)
  public Operator operator(
      BiConsumer<Operator, Reconciler<?>> reconcilerRegisterer,
      Consumer<ConfigurationServiceOverrider> compositeConfigurationServiceOverrider,
      KubernetesClient kubernetesClient,
      List<Reconciler<?>> reconcilers) {

    var operator = new Operator(kubernetesClient, compositeConfigurationServiceOverrider);
    reconcilers.forEach(reconciler -> reconcilerRegisterer.accept(operator, reconciler));

    return operator;
  }

  @Bean
  public BiConsumer<Operator, Reconciler<?>> reconcilerRegisterer() {
    return (operator, reconciler) -> {
      var name = ReconcilerUtils.getNameFor(reconciler);
      var props = configuration.getReconcilers().get(name);

      operator.register(reconciler, overrider -> overrideFromProps(overrider, props));
    };
  }

  @Bean
  public Consumer<ConfigurationServiceOverrider> compositeConfigurationServiceOverrider(
      List<Consumer<ConfigurationServiceOverrider>> configServiceOverriders) {
    return configServiceOverriders.stream()
        .reduce(Consumer::andThen)
        .orElseThrow(
            () -> new IllegalStateException("Default Config Service Overrider Not Created"));
  }

  @Bean
  @Order(0)
  public Consumer<ConfigurationServiceOverrider> defaultConfigServiceOverrider(
      @Autowired(required = false) Cloner cloner,
      ResourceClassResolver resourceClassResolver,
      Metrics metrics) {
    return overrider -> {
      doIfPresent(cloner, overrider::withResourceCloner);
      doIfPresent(configuration.getMinConcurrentWorkflowExecutorThreads(),
          overrider::withMinConcurrentWorkflowExecutorThreads);
      doIfPresent(configuration.getMinConcurrentReconciliationThreads(),
          overrider::withMinConcurrentReconciliationThreads);
      doIfPresent(configuration.getStopOnInformerErrorDuringStartup(),
          overrider::withStopOnInformerErrorDuringStartup);
      doIfPresent(configuration.getConcurrentWorkflowExecutorThreads(),
          overrider::withConcurrentWorkflowExecutorThreads);
      doIfPresent(configuration.getCloseClientOnStop(), overrider::withCloseClientOnStop);
      doIfPresent(configuration.getCacheSyncTimeout(), overrider::withCacheSyncTimeout);
      overrider
          .withConcurrentReconciliationThreads(configuration.getConcurrentReconciliationThreads())
          .withMetrics(metrics)
          .withResourceClassResolver(resourceClassResolver)
          .checkingCRDAndValidateLocalModel(configuration.getCheckCrdAndValidateLocalModel());
    };
  }

  private void overrideFromProps(ControllerConfigurationOverrider<?> overrider,
      ReconcilerProperties props) {
    if (props != null) {
      doIfPresent(props.getFinalizerName(), overrider::withFinalizer);
      doIfPresent(props.getName(), overrider::withName);
      doIfPresent(props.getNamespaces(), overrider::settingNamespaces);
      doIfPresent(props.getRetry(), r -> {
        var retry = new GenericRetry();
        doIfPresent(r.getInitialInterval(), retry::setInitialInterval);
        doIfPresent(r.getMaxAttempts(), retry::setMaxAttempts);
        doIfPresent(r.getMaxInterval(), retry::setMaxInterval);
        doIfPresent(r.getIntervalMultiplier(), retry::setIntervalMultiplier);
        overrider.withRetry(retry);
      });
      doIfPresent(props.isGenerationAware(), overrider::withGenerationAware);
      doIfPresent(props.isClusterScoped(), clusterScoped -> {
        if (clusterScoped) {
          overrider.watchingAllNamespaces();
        }
      });
      doIfPresent(props.getLabelSelector(), overrider::withLabelSelector);
      doIfPresent(props.getReconciliationMaxInterval(), overrider::withReconciliationMaxInterval);
    }
  }

  @Bean
  @ConditionalOnMissingBean(name = "reconciliationExecutorService")
  public ExecutorService reconciliationExecutorService() {
    return Executors.newFixedThreadPool(configuration.getConcurrentReconciliationThreads());
  }

  @Bean
  @ConditionalOnMissingBean(Metrics.class)
  public Metrics metrics() {
    return Metrics.NOOP;
  }

  @Bean
  public Config getClientConfiguration(
      @Autowired(required = false) KubernetesConfigCustomizer configCustomizer) {
    return configuration.getClient().getContext()
        .map(Config::autoConfigure)
        .map(it -> {
          if (configCustomizer != null) {
            final var builder = new ConfigBuilder(it);
            configCustomizer.customize(builder);
            return builder.build();
          } else {
            return it;
          }
        })
        .orElseGet(() -> {
          final var clientCfg = configuration.getClient();
          ConfigBuilder config = new ConfigBuilder();
          config.withTrustCerts(clientCfg.isTrustSelfSignedCertificates());
          clientCfg.getMasterUrl().ifPresent(config::withMasterUrl);
          clientCfg.getUsername().ifPresent(config::withUsername);
          clientCfg.getPassword().ifPresent(config::withPassword);

          if (configCustomizer != null) {
            configCustomizer.customize(config);
          }

          return config.build();
        });
  }

  private <T> void doIfPresent(T prop, Consumer<T> action) {
    Optional.ofNullable(prop).ifPresent(action);
  }

}
