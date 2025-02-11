package io.javaoperatorsdk.operator.springboot.starter.properties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RetryProperties {

  Integer maxAttempts;
  Long initialInterval;
  Double intervalMultiplier;
  Long maxInterval;
}
