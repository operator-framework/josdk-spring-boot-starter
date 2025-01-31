package io.javaoperatorsdk.operator.springboot.starter.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;

/**
 * Note that we have multiple options here either we can add this component scan as seen below. Or
 * annotate controllers with @Component or @Service annotation or just register the bean within a
 * spring "@Configuration".
 */
@ComponentScan(
    includeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = ControllerConfiguration.class)
    })
@SpringBootApplication
public class SpringBootStarterSampleApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringBootStarterSampleApplication.class, args);
  }
}
