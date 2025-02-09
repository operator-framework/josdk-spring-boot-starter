package io.javaoperatorsdk.operator.springboot.starter.properties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CrdProperties {

  boolean applyOnStartup;
  /**
   * path to the resource folder where CRDs are located
   */
  String path = "/META-INF/fabric8/";
  /**
   * file suffix to filter out CRDs
   */
  String suffix = "-v1.yml";
}
