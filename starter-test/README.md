# operator-framework-spring-boot-starter-test

This module provides Spring Boot test support for the Java Operator SDK starter.

## Spring-managed kube-apiserver

Add the test starter to the test classpath:

```xml
<dependency>
  <groupId>io.javaoperatorsdk</groupId>
  <artifactId>operator-framework-spring-boot-starter-test</artifactId>
  <scope>test</scope>
</dependency>
```

Then enable the real kube-apiserver integration on a test class:

```java
@SpringBootTest
@EnableKubeAPITest
class OperatorIntegrationTest {
}
```

`@EnableKubeAPITest` starts and stops a `kube-api-test` server with the Spring application context.
It also exposes an in-memory Fabric8 `Config`, so the starter's normal Kubernetes client
auto-configuration is used.

CRD application remains disabled by default, matching the starter runtime default. Enable it
explicitly when the test needs CRDs; it uses the starter's existing `javaoperatorsdk.crd.path` and
`javaoperatorsdk.crd.suffix` properties and applies matching CRDs before the operator starts:

```java
@EnableKubeAPITest(applyCrdsOnStartup = true)
```

An existing `javaoperatorsdk.crd.apply-on-startup` property is preserved when the annotation uses
its default.

The server can be customized with the following optional properties:

```yaml
javaoperatorsdk:
  test:
    kube-api:
      test-dir: C:/tmp/kubeapitest
      api-server-version: 1.31.*
      offline-mode: false
      api-server-flags:
        - --feature-gates=...
      wait-for-etcd-health-check-on-startup: true
      startup-timeout: 120000
```

Unset server properties are left to `kube-api-test`'s own environment-variable and built-in default
handling.
`updateKubeConfig` is intentionally not exposed: by default, the Spring integration constructs the
server with kubeconfig updates disabled. The kube-api-test binary and certificate cache may still
be created in the configured `test-dir`, but the user's kubeconfig is not modified unless a test
supplies its own `KubeAPIServerConfig`, `KubeAPIServer`, or `Config` bean that changes this.

`@EnableMockOperator` remains a separate CRUD-mock backend. Enabling kube-api-test does not change
the mock annotation's behavior.