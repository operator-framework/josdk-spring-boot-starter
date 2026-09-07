package io.javaoperatorsdk.operator.springboot.starter.test.kubeapitest;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.PropertyMapping;

/**
 * Enables a Spring-managed kube-api-test server for a test application.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@ImportAutoConfiguration(KubeAPITestAutoConfiguration.class)
@PropertyMapping("javaoperatorsdk")
public @interface EnableKubeAPITest {

  /**
   * Controls whether the starter applies matching CRDs before starting the operator.
   *
   * @return whether CRDs should be applied on startup
   */
  @PropertyMapping(value = "crd.apply-on-startup", skip = PropertyMapping.Skip.ON_DEFAULT_VALUE)
  boolean applyCrdsOnStartup() default false;
}
