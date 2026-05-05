[![Build Status](https://github.com/strimzi/test-connectors/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/strimzi/test-connectors/actions/workflows/build.yml?query=branch%3Amain)
[![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![GitHub release](https://img.shields.io/github/release/strimzi/test-connectors.svg)](https://github.com/strimzi/test-connectors/releases/latest)
[![Maven Central](https://img.shields.io/maven-central/v/io.strimzi/strimzi-fault-injection-source-connector)](https://search.maven.org/artifact/io.strimzi/strimzi-fault-injection-source-connector)
[![Pull Requests](https://img.shields.io/github/issues-pr/strimzi/test-connectors)](https://github.com/strimzi/test-connectors/pulls)
[![Issues](https://img.shields.io/github/issues/strimzi/test-connectors)](https://github.com/strimzi/test-connectors/issues)
[![Twitter Follow](https://img.shields.io/twitter/follow/strimziio?style=social)](https://twitter.com/strimziio)

# Strimzi Test Connectors

Kafka Connect connectors with configurable fault-injection behavior for testing in the [Strimzi](https://strimzi.io) project.
The connector JARs are pre-installed in the [strimzi/test-container-images](https://github.com/strimzi/test-container-images) 
and used via [strimzi-test-container](https://github.com/strimzi/test-container).

## Usage

These connectors are not used directly as dependencies.
They are packaged into the [strimzi/test-container-images](https://github.com/strimzi/test-container-images) Docker image 
and enabled through the [strimzi-test-container](https://github.com/strimzi/test-container) API using dedicated builder methods.

```java
StrimziConnectCluster connectCluster = new StrimziConnectCluster.StrimziConnectClusterBuilder()
        .withKafkaCluster(kafkaCluster)
        .withGroupId("my-group")
        .withStrimziFaultInjectionSourceConnector()
        .build();

connectCluster.start();
```

## License

Strimzi Test Connectors is licensed under the [Apache License](./LICENSE), Version 2.0.