package io.javaoperatorsdk.operator.springboot.starter;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

@Configuration
@EnableConfigurationProperties(OperatorConfigurationProperties.class)
public class OperatorAutoConfiguration {

  private final static Logger log = LoggerFactory.getLogger(OperatorAutoConfiguration.class);

  @Autowired
  private OperatorConfigurationProperties configuration;

  @Autowired(required = false)
  private Cloner cloner;

  @Autowired(required = false)
  private KubernetesConfigCustomizer configCustomizer;

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

  @Bean(destroyMethod = "stop")
  @ConditionalOnMissingBean(Operator.class)
  public Operator operator(
      ConfigurationService configurationService,
      KubernetesClient kubernetesClient,
      List<Reconciler<?>> reconcilers) {

    Operator operator = new Operator(kubernetesClient, configurationService);
    reconcilers.forEach(r -> operator.register(r,
        o -> setControllerOverrides(o, configuration, r)));

    if (!reconcilers.isEmpty()) {
      operator.start();
    } else {
      log.warn("No Reconcilers found in the application context: Not starting the Operator");
    }
    return operator;
  }

  @Bean
  public ConfigurationService configurationService(ResourceClassResolver resourceClassResolver,
      Metrics metrics) {
    OverridableBaseConfigService conf =
        new OverridableBaseConfigService(Utils.loadFromProperties());
    if (cloner != null) {
      conf.setResourceCloner(cloner);
    }
    conf.setConcurrentReconciliationThreads(configuration.getConcurrentReconciliationThreads());
    conf.setMetrics(metrics);
    conf.setResourceClassResolver(resourceClassResolver);
    conf.setCheckCRDAndValidateLocalModel(configuration.getCheckCrdAndValidateLocalModel());
    return conf;
  }


  @SuppressWarnings("rawtypes")
  private void setControllerOverrides(ControllerConfigurationOverrider<?> o,
      OperatorConfigurationProperties configuration,
      Reconciler<?> reconciler) {
    final var reconcilerPropertiesMap = configuration.getReconcilers();
    final var name = ReconcilerUtils.getNameFor(reconciler);
    var props = reconcilerPropertiesMap.get(name);

    if (props != null) {
      Optional.ofNullable(props.getFinalizerName()).ifPresent(o::withFinalizer);
      Optional.ofNullable(props.getName()).ifPresent(o::withName);
      Optional.ofNullable(props.getNamespaces()).ifPresent(o::settingNamespaces);
      Optional.ofNullable(props.getRetry()).ifPresent(r -> {
        var retry = new GenericRetry();
        if (r.getInitialInterval() != null) {
          retry.setInitialInterval(r.getInitialInterval());
        }
        if (r.getMaxAttempts() != null) {
          retry.setMaxAttempts(r.getMaxAttempts());
        }
        if (r.getMaxInterval() != null) {
          retry.setMaxInterval(r.getMaxInterval());
        }
        if (r.getIntervalMultiplier() != null) {
          retry.setIntervalMultiplier(r.getIntervalMultiplier());
        }
        o.withRetry(retry);
      });
      Optional.ofNullable(props.isGenerationAware()).ifPresent(o::withGenerationAware);
      Optional.ofNullable(props.isClusterScoped()).ifPresent(clusterScoped -> {
        if (clusterScoped) {
          o.watchingAllNamespaces();
        }
      });
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
  public Config getClientConfiguration() {
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
}
