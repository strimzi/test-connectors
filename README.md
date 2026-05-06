[![Build Status](https://github.com/strimzi/test-connectors/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/strimzi/test-connectors/actions/workflows/build.yml?query=branch%3Amain)
[![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![GitHub release](https://img.shields.io/github/release/strimzi/test-connectors.svg)](https://github.com/strimzi/test-connectors/releases/latest)
[![Maven Central](https://img.shields.io/maven-central/v/io.strimzi/strimzi-fault-injection-source-connector)](https://search.maven.org/artifact/io.strimzi/strimzi-fault-injection-source-connector)
[![Pull Requests](https://img.shields.io/github/issues-pr/strimzi/test-connectors)](https://github.com/strimzi/test-connectors/pulls)
[![Issues](https://img.shields.io/github/issues/strimzi/test-connectors)](https://github.com/strimzi/test-connectors/issues)
[![Twitter Follow](https://img.shields.io/twitter/follow/strimziio?style=social)](https://twitter.com/strimziio)

# Strimzi Test Connectors

Kafka Connect connectors with configurable fault-injection behavior for testing in the [Strimzi](https://strimzi.io) project.
Testing Kafka Connect integration in Strimzi requires connectors whose behavior can be controlled (i.e, injecting delays, failures, and specific record rates without depending on external systems).
These connectors provide that, allowing tests to verify how the operator and Connect runtime handle various fault scenarios.

The connector JARs are pre-installed in the [strimzi/test-container-images](https://github.com/strimzi/test-container-images) 
and used via [strimzi-test-container](https://github.com/strimzi/test-container).

## Connectors

* [Strimzi Fault Injection Source Connector](fault-injection-source-connector/README.md)

## License

Strimzi Test Connectors is licensed under the [Apache License](./LICENSE), Version 2.0.