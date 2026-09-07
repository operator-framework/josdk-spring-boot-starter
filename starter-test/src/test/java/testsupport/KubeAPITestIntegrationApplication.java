package testsupport;

import java.util.HashMap;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.springboot.starter.CRDApplier.CRDTransformer;
import io.javaoperatorsdk.operator.springboot.starter.sample.CustomServiceReconciler;

@Configuration(proxyBeanMethods = false)
@EnableAutoConfiguration
public class KubeAPITestIntegrationApplication {

  @Bean
  CustomServiceReconciler customServiceReconciler(KubernetesClient kubernetesClient) {
    return new CustomServiceReconciler(kubernetesClient);
  }

  @Bean
  CRDTransformer transformerOne() {
    return crd -> addLabel(crd, "One", "1");
  }

  @Bean
  CRDTransformer transformerTwo() {
    return crd -> addLabel(crd, "Two", "2");
  }

  private static HasMetadata addLabel(HasMetadata crd, String key, String value) {
    if (crd.getMetadata().getLabels() == null) {
      crd.getMetadata().setLabels(new HashMap<>());
    }
    crd.getMetadata().getLabels().put(key, value);
    return crd;
  }
}
