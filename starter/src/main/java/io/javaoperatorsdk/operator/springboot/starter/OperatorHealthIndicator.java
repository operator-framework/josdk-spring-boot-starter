package io.javaoperatorsdk.operator.springboot.starter;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import io.javaoperatorsdk.operator.Operator;

@Component
public class OperatorHealthIndicator extends AbstractHealthIndicator {

  private final Operator operator;

  public OperatorHealthIndicator(final Operator operator) {
    super("OperatorSDK health check failed");
    Assert.notNull(operator, "OperatorSDK Operator not initialized");
    this.operator = operator;
  }

  @Override
  protected void doHealthCheck(Health.Builder builder) {
    final var runtimeInfo = operator.getRuntimeInfo();
    if (runtimeInfo.isStarted()) {
      final boolean[] healthy = {true};
      runtimeInfo.getRegisteredControllers().forEach(rc -> {
        final var name = rc.getConfiguration().getName();
        final var unhealthy = rc.getControllerHealthInfo().unhealthyEventSources();
        if (unhealthy.isEmpty()) {
          builder.withDetail(name, "OK");
        } else {
          healthy[0] = false;
          builder.withDetail(name, "unhealthy: " + String.join(", ", unhealthy.keySet()));
        }
      });
      if (healthy[0]) {
        builder.up();
      } else {
        builder.down();
      }
    } else {
      builder.unknown();
    }
  }
}
