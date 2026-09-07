package io.javaoperatorsdk.operator.springboot.starter.test.kubeapitest;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configures the kube-api-test server used by {@link EnableKubeAPITest}.
 */
@ConfigurationProperties("javaoperatorsdk.test.kube-api")
public class KubeAPITestProperties {

  private String testDir;
  private String apiServerVersion;
  private Boolean offlineMode;
  private List<String> apiServerFlags;
  private Boolean waitForEtcdHealthCheckOnStartup;
  private Integer startupTimeout;

  /**
   * Returns the kube-api-test working directory.
   *
   * <p>
   * Maps to {@code KubeAPIServerConfigBuilder#withTestDir}.
   *
   * @return configured test directory
   */
  public String getTestDir() {
    return testDir;
  }

  /**
   * Sets the kube-api-test working directory.
   *
   * <p>
   * Maps to {@code KubeAPIServerConfigBuilder#withTestDir}.
   *
   * @param testDir test directory
   */
  public void setTestDir(String testDir) {
    this.testDir = testDir;
  }

  /**
   * Returns the Kubernetes API server version.
   *
   * <p>
   * Maps to {@code KubeAPIServerConfigBuilder#withApiServerVersion}.
   *
   * @return configured API server version
   */
  public String getApiServerVersion() {
    return apiServerVersion;
  }

  /**
   * Sets the Kubernetes API server version.
   *
   * <p>
   * Maps to {@code KubeAPIServerConfigBuilder#withApiServerVersion}.
   *
   * @param apiServerVersion API server version
   */
  public void setApiServerVersion(String apiServerVersion) {
    this.apiServerVersion = apiServerVersion;
  }

  /**
   * Returns whether binaries may be used without downloading.
   *
   * <p>
   * Maps to {@code KubeAPIServerConfigBuilder#withOfflineMode}. If unset, handling of
   * kube-api-test's {@code KUBE_API_TEST_OFFLINE_MODE} environment variable and built-in default
   * remains delegated to kube-api-test.
   *
   * @return offline mode, or {@code null} to use the kube-api-test default
   */
  public Boolean getOfflineMode() {
    return offlineMode;
  }

  /**
   * Sets whether binaries may be used without downloading.
   *
   * <p>
   * Maps to {@code KubeAPIServerConfigBuilder#withOfflineMode}. If unset, handling of
   * kube-api-test's {@code KUBE_API_TEST_OFFLINE_MODE} environment variable and built-in default
   * remains delegated to kube-api-test.
   *
   * @param offlineMode offline mode
   */
  public void setOfflineMode(Boolean offlineMode) {
    this.offlineMode = offlineMode;
  }

  /**
   * Returns additional API server flags.
   *
   * <p>
   * Maps to {@code KubeAPIServerConfigBuilder#withApiServerFlags}.
   *
   * @return API server flags
   */
  public List<String> getApiServerFlags() {
    return apiServerFlags;
  }

  /**
   * Sets additional API server flags.
   *
   * <p>
   * Maps to {@code KubeAPIServerConfigBuilder#withApiServerFlags}.
   *
   * @param apiServerFlags API server flags
   */
  public void setApiServerFlags(List<String> apiServerFlags) {
    this.apiServerFlags = apiServerFlags;
  }

  /**
   * Returns whether startup waits for the etcd health check.
   *
   * <p>
   * Maps to {@code KubeAPIServerConfigBuilder#withWaitForEtcdHealthCheckOnStartup}. If unset,
   * handling of kube-api-test's {@code KUBE_API_TEST_WAIT_FOR_ETCD_HEALTH_CHECK} environment
   * variable and built-in default remains delegated to kube-api-test.
   *
   * @return etcd health-check setting, or {@code null} to use the kube-api-test default
   */
  public Boolean getWaitForEtcdHealthCheckOnStartup() {
    return waitForEtcdHealthCheckOnStartup;
  }

  /**
   * Sets whether startup waits for the etcd health check.
   *
   * <p>
   * Maps to {@code KubeAPIServerConfigBuilder#withWaitForEtcdHealthCheckOnStartup}. If unset,
   * handling of kube-api-test's {@code KUBE_API_TEST_WAIT_FOR_ETCD_HEALTH_CHECK} environment
   * variable and built-in default remains delegated to kube-api-test.
   *
   * @param waitForEtcdHealthCheckOnStartup etcd health-check setting
   */
  public void setWaitForEtcdHealthCheckOnStartup(Boolean waitForEtcdHealthCheckOnStartup) {
    this.waitForEtcdHealthCheckOnStartup = waitForEtcdHealthCheckOnStartup;
  }

  /**
   * Returns the server startup timeout.
   *
   * <p>
   * Maps to {@code KubeAPIServerConfigBuilder#withStartupTimeout}. If unset, handling of
   * kube-api-test's {@code KUBE_API_TEST_STARTUP_TIMEOUT} environment variable and built-in default
   * remains delegated to kube-api-test.
   *
   * @return startup timeout in milliseconds, or {@code null} to use the kube-api-test default
   */
  public Integer getStartupTimeout() {
    return startupTimeout;
  }

  /**
   * Sets the server startup timeout.
   *
   * <p>
   * Maps to {@code KubeAPIServerConfigBuilder#withStartupTimeout}. If unset, handling of
   * kube-api-test's {@code KUBE_API_TEST_STARTUP_TIMEOUT} environment variable and built-in default
   * remains delegated to kube-api-test.
   *
   * @param startupTimeout startup timeout in milliseconds
   */
  public void setStartupTimeout(Integer startupTimeout) {
    this.startupTimeout = startupTimeout;
  }
}
