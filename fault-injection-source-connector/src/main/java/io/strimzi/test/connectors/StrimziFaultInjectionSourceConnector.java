/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.strimzi.test.connectors;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A source connector with configurable fault-injection behavior for testing.
 */
public class StrimziFaultInjectionSourceConnector extends SourceConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(StrimziFaultInjectionSourceConnector.class);

    private boolean taskFailOnStart;
    private long stopTime;
    private long taskStartTime;
    private long taskStopTime;
    private long taskPollTime;
    private long taskPollRecords;
    private String topicName;
    private int numPartitions;

    @Override
    public void start(Map<String, String> map) {
        LOGGER.info("Starting connector {}", this);
        StrimziFaultInjectionSourceConnectorConfig config = new StrimziFaultInjectionSourceConnectorConfig(map);
        taskFailOnStart = config.getBoolean(StrimziFaultInjectionSourceConnectorConfig.TASK_FAIL_ON_START);
        long startTime = config.getLong(StrimziFaultInjectionSourceConnectorConfig.START_TIME_MS);
        stopTime = config.getLong(StrimziFaultInjectionSourceConnectorConfig.STOP_TIME_MS);
        taskStartTime = config.getLong(StrimziFaultInjectionSourceConnectorConfig.TASK_START_TIME_MS);
        taskStopTime = config.getLong(StrimziFaultInjectionSourceConnectorConfig.TASK_STOP_TIME_MS);
        taskPollTime = config.getLong(StrimziFaultInjectionSourceConnectorConfig.TASK_POLL_TIME_MS);
        taskPollRecords = config.getLong(StrimziFaultInjectionSourceConnectorConfig.TASK_POLL_RECORDS);
        topicName = config.getString(StrimziFaultInjectionSourceConnectorConfig.TOPIC_NAME);
        numPartitions = config.getInt(StrimziFaultInjectionSourceConnectorConfig.NUM_PARTITIONS);
        sleep(startTime);
        if (config.getBoolean(StrimziFaultInjectionSourceConnectorConfig.FAIL_ON_START)) {
            LOGGER.info("Failing connector {}", this);
            throw new RuntimeException("Failed to start connector");
        }
        LOGGER.info("Started connector {}", this);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return StrimziFaultInjectionSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int count) {
        Map<String, String> taskConfig = new HashMap<>();
        taskConfig.put(StrimziFaultInjectionSourceConnectorConfig.TASK_FAIL_ON_START, Boolean.toString(taskFailOnStart));
        taskConfig.put(StrimziFaultInjectionSourceConnectorConfig.TASK_START_TIME_MS, Long.toString(taskStartTime));
        taskConfig.put(StrimziFaultInjectionSourceConnectorConfig.TASK_STOP_TIME_MS, Long.toString(taskStopTime));
        taskConfig.put(StrimziFaultInjectionSourceConnectorConfig.TASK_POLL_TIME_MS, Long.toString(taskPollTime));
        taskConfig.put(StrimziFaultInjectionSourceConnectorConfig.TASK_POLL_RECORDS, Long.toString(taskPollRecords));
        taskConfig.put(StrimziFaultInjectionSourceConnectorConfig.TOPIC_NAME, topicName);
        taskConfig.put(StrimziFaultInjectionSourceConnectorConfig.NUM_PARTITIONS, Integer.toString(numPartitions));
        List<Map<String, String>> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(new HashMap<>(taskConfig));
        }
        return result;
    }

    @Override
    public void stop() {
        LOGGER.info("Stopping connector {}", this);
        sleep(stopTime);
        LOGGER.info("Stopped connector {}", this);
    }

    @Override
    public ConfigDef config() {
        return StrimziFaultInjectionSourceConnectorConfig.CONFIG_DEF;
    }

    @Override
    public String version() {
        return getClass().getPackage().getImplementationVersion();
    }

    static void sleep(long ms) {
        if (ms > 0) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                LOGGER.warn("Interrupted during sleep", e);
                Thread.currentThread().interrupt();
            }
        }
    }
}