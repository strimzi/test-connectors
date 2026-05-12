/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.strimzi.test.connectors;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

/**
 * Configuration for the {@code StrimziFaultInjectionSourceConnector}.
 */
public class StrimziFaultInjectionSourceConnectorConfig extends AbstractConfig {
    /** Whether the connector {@code start()} should fail. */
    public static final String FAIL_ON_START = "fail.on.start";
    /** Whether the task {@code start()} should fail. */
    public static final String TASK_FAIL_ON_START = "task.fail.on.start";
    /** Delay in milliseconds before the connector {@code start()} returns. */
    public static final String START_TIME_MS = "start.time.ms";
    /** Delay in milliseconds before the connector {@code stop()} returns. */
    public static final String STOP_TIME_MS = "stop.time.ms";
    /** Delay in milliseconds before the task {@code start()} returns. */
    public static final String TASK_START_TIME_MS = "task.start.time.ms";
    /** Delay in milliseconds before the task {@code stop()} returns. */
    public static final String TASK_STOP_TIME_MS = "task.stop.time.ms";
    /** Delay in milliseconds before each task {@code poll()} returns. */
    public static final String TASK_POLL_TIME_MS = "task.poll.time.ms";
    /** Number of records returned per {@code poll()} call. */
    public static final String TASK_POLL_RECORDS = "task.poll.records";
    /** Topic to produce records to. */
    public static final String TOPIC_NAME = "topic.name";
    /** Number of partitions to distribute records across. */
    public static final String NUM_PARTITIONS = "num.partitions";

    /** Configuration definition for the connector. */
    public static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(FAIL_ON_START,
                    ConfigDef.Type.BOOLEAN, false,
                    ConfigDef.Importance.MEDIUM, "Whether start() should fail")
            .define(TASK_FAIL_ON_START,
                    ConfigDef.Type.BOOLEAN, false,
                    ConfigDef.Importance.MEDIUM, "Whether task start() should fail")
            .define(START_TIME_MS,
                    ConfigDef.Type.LONG, 5_000L,
                    ConfigDef.Importance.MEDIUM, "The time that start() should take to return")
            .define(STOP_TIME_MS,
                    ConfigDef.Type.LONG, 5_000L,
                    ConfigDef.Importance.MEDIUM, "The time that stop() should take to return")
            .define(TASK_START_TIME_MS,
                    ConfigDef.Type.LONG, 5_000L,
                    ConfigDef.Importance.MEDIUM, "The time that the task start() should take to return")
            .define(TASK_STOP_TIME_MS,
                    ConfigDef.Type.LONG, 5_000L,
                    ConfigDef.Importance.MEDIUM, "The time that the task stop() should take to return")
            .define(TASK_POLL_TIME_MS,
                    ConfigDef.Type.LONG, 5_000L,
                    ConfigDef.Importance.MEDIUM, "The time that the task poll() should take to return")
            .define(TASK_POLL_RECORDS,
                    ConfigDef.Type.LONG, 1_000L,
                    ConfigDef.Importance.MEDIUM, "The number of records that the task poll() should return")
            .define(TOPIC_NAME,
                    ConfigDef.Type.STRING, "my-topic",
                    ConfigDef.Importance.MEDIUM, "The name of the topic which the records are sent to")
            .define(NUM_PARTITIONS,
                    ConfigDef.Type.INT, 1, ConfigDef.Range.atLeast(1),
                    ConfigDef.Importance.MEDIUM, "The number of partitions which the records are sent to");

    /**
     * Constructor.
     *
     * @param props configuration properties
     */
    public StrimziFaultInjectionSourceConnectorConfig(Map<String, String> props) {
        super(CONFIG_DEF, props);
    }
}