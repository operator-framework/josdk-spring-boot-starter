package io.javaoperatorsdk.operator.springboot.starter.properties;

import java.time.Duration;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReconcilerProperties {
  String name;
  String finalizerName;
  Boolean generationAware;
  Boolean clusterScoped;
  Set<String> namespaces;
  RetryProperties retry;
  String labelSelector;
  Duration reconciliationMaxInterval;
}
