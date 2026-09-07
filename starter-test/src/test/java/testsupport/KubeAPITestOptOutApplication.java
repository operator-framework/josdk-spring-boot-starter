package testsupport;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.fabric8.kubeapitest.KubeAPIServer;
import io.fabric8.kubeapitest.KubeAPIServerConfig;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;

@Configuration(proxyBeanMethods = false)
@EnableAutoConfiguration
public class KubeAPITestOptOutApplication {

  @Bean
  KubeAPIServer kubeAPIServer(KubeAPIServerConfig config) {
    return new KubeAPIServer(config);
  }

  @Bean
  Config kubernetesConfig() {
    return new ConfigBuilder().withMasterUrl("http://localhost").build();
  }
}
