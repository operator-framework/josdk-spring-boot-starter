package io.javaoperatorsdk.operator.springboot.starter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.javaoperatorsdk.operator.Operator;

@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(Operator.class)
@Import(OperatorAutoConfiguration.class)
public class OperatorHealthIndicatorAutoConfiguration {

  @Bean
  public OperatorHealthIndicator createOperatorHealthIndicator(Operator operator) {
    return new OperatorHealthIndicator(operator);
  }

}
