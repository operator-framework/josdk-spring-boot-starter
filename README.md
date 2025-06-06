# Spring Boot Starter for Java Operator SDK

Supports Spring Boot 3.x

## Getting Started

Easiest way to get started, is to take a look on [WebPage sample](./samples/webpage).

## Configuration Properties

You can see configuration operator properties root [here](./starter/src/main/java/io/javaoperatorsdk/operator/springboot/starter/OperatorConfigurationProperties.java).

Reconciler properties [here](./starter/src/main/java/io/javaoperatorsdk/operator/springboot/starter/ReconcilerProperties.java).

## Overriding default implementation

You can provide own implementation instead of the by default provided beans,
life for the [Fabric8 client](https://github.com/operator-framework/josdk-spring-boot-starter/blob/main/starter/src/main/java/io/javaoperatorsdk/operator/springboot/starter/OperatorAutoConfiguration.java#L51)
but also the [Operator instance](https://github.com/operator-framework/josdk-spring-boot-starter/blob/main/starter/src/main/java/io/javaoperatorsdk/operator/springboot/starter/OperatorAutoConfiguration.java#L94).