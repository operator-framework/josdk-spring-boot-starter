package io.javaoperatorsdk.operator.springboot.starter.test.kubeapitest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import io.fabric8.kubeapitest.KubeAPIServer;
import io.fabric8.kubeapitest.KubeAPIServerConfig;
import io.fabric8.kubeapitest.KubeAPIServerConfigBuilder;
import io.fabric8.kubernetes.client.Config;

/**
 * Provides the Spring-managed kube-api-test server and its client configuration.
 */
@TestConfiguration(proxyBeanMethods = false)
@EnableConfigurationProperties(KubeAPITestProperties.class)
public class KubeAPITestConfiguration {

  /**
   * Creates the standard kube-api-test server configuration.
   *
   * @param properties configured kube-api-test properties
   * @return the server configuration
   */
  @Bean
  @ConditionalOnMissingBean(KubeAPIServerConfig.class)
  public KubeAPIServerConfig kubeAPIServerConfig(KubeAPITestProperties properties) {
    KubeAPIServerConfigBuilder builder = KubeAPIServerConfigBuilder.anAPIServerConfig();

    if (properties.getTestDir() != null) {
      builder.withTestDir(properties.getTestDir());
    }
    if (properties.getApiServerVersion() != null) {
      builder.withApiServerVersion(properties.getApiServerVersion());
    }
    if (properties.getOfflineMode() != null) {
      builder.withOfflineMode(properties.getOfflineMode());
    }
    if (properties.getApiServerFlags() != null) {
      builder.withApiServerFlags(properties.getApiServerFlags());
    }
    if (properties.getWaitForEtcdHealthCheckOnStartup() != null) {
      builder.withWaitForEtcdHealthCheckOnStartup(
          properties.getWaitForEtcdHealthCheckOnStartup());
    }
    if (properties.getStartupTimeout() != null) {
      builder.withStartupTimeout(properties.getStartupTimeout());
    }

    return builder.withUpdateKubeConfig(false).build();
  }

  /**
   * Creates the Spring-managed kube-api-test server.
   *
   * @param config server configuration
   * @return the kube-api-test server
   */
  @Bean(initMethod = "start", destroyMethod = "stop")
  @ConditionalOnMissingBean(KubeAPIServer.class)
  public KubeAPIServer kubeAPIServer(KubeAPIServerConfig config) {
    return new KubeAPIServer(config);
  }

  /**
   * Creates a Fabric8 client configuration from the running test server.
   *
   * @param server running kube-api-test server
   * @return an in-memory Fabric8 client configuration
   */
  @Bean
  @ConditionalOnMissingBean(Config.class)
  public Config kubernetesConfig(KubeAPIServer server) {
    return Config.fromKubeconfig(server.getKubeConfigYaml());
  }
}
