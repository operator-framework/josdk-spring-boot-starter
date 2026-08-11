# Spring Boot Starter for Java Operator SDK

Supports Spring Boot 4.x

## Getting Started

Easiest way to get started, is to take a look on [WebPage sample](./samples/webpage).

## Configuration Properties

You can see configuration operator properties root [here](./starter/src/main/java/io/javaoperatorsdk/operator/springboot/starter/OperatorConfigurationProperties.java).

Reconciler properties [here](./starter/src/main/java/io/javaoperatorsdk/operator/springboot/starter/ReconcilerProperties.java).

## Overriding default implementation

You can provide own implementation instead of the by default provided beans,
life for the [Fabric8 client](https://github.com/operator-framework/josdk-spring-boot-starter/blob/main/starter/src/main/java/io/javaoperatorsdk/operator/springboot/starter/OperatorAutoConfiguration.java#L50)
but also the [Operator instance](https://github.com/operator-framework/josdk-spring-boot-starter/blob/main/starter/src/main/java/io/javaoperatorsdk/operator/springboot/starter/OperatorAutoConfiguration.java#L94).

By default, managed dependent resources are created through Spring's `AutowireCapableBeanFactory`, so they can
receive Spring-managed dependencies (e.g. via constructor injection) instead of requiring a no-arg constructor.
Because of this, dependent resources also go through the same lifecycle as any other Spring bean: fields annotated
with `@Autowired`/`@Value` are populated, `@PostConstruct` methods run, and matching AOP advice wraps the instance in
a proxy - this differs from a plain no-arg-constructed instance, so double-check any existing dependent resource that
relies on annotation-driven injection being a no-op, or on being constructed without side effects.

You can provide your own [DependentResourceFactory](https://github.com/operator-framework/josdk-spring-boot-starter/blob/main/starter/src/main/java/io/javaoperatorsdk/operator/springboot/starter/OperatorAutoConfiguration.java#L173)
bean to override this default behavior, or set `javaoperatorsdk.dependent-resources.spring-managed=false` to fall
back to Java Operator SDK's own no-arg-constructor default.
