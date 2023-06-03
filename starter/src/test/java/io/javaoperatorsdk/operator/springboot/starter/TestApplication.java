package io.javaoperatorsdk.operator.springboot.starter;

import io.javaoperatorsdk.operator.api.config.ConfigurationServiceOverrider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Consumer;

import static io.javaoperatorsdk.operator.springboot.starter.AutoConfigurationTest.CUSTOM_RECONCILE_THREADS;

@SpringBootApplication
public class TestApplication {

  public static void main(String[] args) {
    SpringApplication.run(TestApplication.class, args);
  }

  @Bean
  public Consumer<ConfigurationServiceOverrider> additionalConfigServiceOverrider() {
    return overrider -> overrider.withConcurrentReconciliationThreads(CUSTOM_RECONCILE_THREADS);
  }
}
