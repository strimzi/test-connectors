# Strimzi Fault Injection Source Connector

A Kafka Connect source connector with configurable fault-injection behavior for testing.
It produces sequential `int64` records and allows injecting delays and failures into every connector lifecycle method.

## Connector class

```
io.strimzi.test.connectors.StrimziFaultInjectionSourceConnector
```

## Configuration properties

| Property             | Type    | Default    | Description                                                           |
|----------------------|---------|------------|-----------------------------------------------------------------------|
| `topic.name`         | String  | `my-topic` | Topic to produce records to                                           |
| `num.partitions`     | Integer | `1`        | Number of partitions to distribute records across (must be >= 1)      |
| `task.poll.records`  | Long    | `1000`     | Number of records returned per `poll()` call                          |
| `fail.on.start`      | Boolean | `false`    | If `true`, the connector throws a `RuntimeException` during `start()` |
| `task.fail.on.start` | Boolean | `false`    | If `true`, each task throws a `ConnectException` during `start()`     |
| `start.time.ms`      | Long    | `5000`     | Delay in milliseconds before the connector `start()` method returns   |
| `stop.time.ms`       | Long    | `5000`     | Delay in milliseconds before the connector `stop()` method returns    |
| `task.start.time.ms` | Long    | `5000`     | Delay in milliseconds before the task `start()` method returns        |
| `task.stop.time.ms`  | Long    | `5000`     | Delay in milliseconds before the task `stop()` method returns         |
| `task.poll.time.ms`  | Long    | `5000`     | Delay in milliseconds before each task `poll()` call returns          |