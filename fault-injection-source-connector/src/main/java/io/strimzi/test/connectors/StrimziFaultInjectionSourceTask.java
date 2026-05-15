/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.strimzi.test.connectors;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Source task for the {@link StrimziFaultInjectionSourceConnector}.
 */
public class StrimziFaultInjectionSourceTask extends SourceTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(StrimziFaultInjectionSourceTask.class);
    private static final Schema VALUE_SCHEMA = SchemaBuilder.int64().build();

    private long taskStopTime;
    private long taskPollTime;
    private final AtomicLong record = new AtomicLong(0);
    private long taskPollRecords;
    private String topicName;
    private int numPartitions;

    @Override
    public String version() {
        return getClass().getPackage().getImplementationVersion();
    }

    @Override
    public void start(Map<String, String> map) {
        LOGGER.info("Starting task {}", this);
        StrimziFaultInjectionSourceConnectorConfig config = new StrimziFaultInjectionSourceConnectorConfig(map);
        long taskStartTime = config.getLong(StrimziFaultInjectionSourceConnectorConfig.TASK_START_TIME_MS);
        taskStopTime = config.getLong(StrimziFaultInjectionSourceConnectorConfig.TASK_STOP_TIME_MS);
        taskPollTime = config.getLong(StrimziFaultInjectionSourceConnectorConfig.TASK_POLL_TIME_MS);
        taskPollRecords = config.getLong(StrimziFaultInjectionSourceConnectorConfig.TASK_POLL_RECORDS);
        topicName = config.getString(StrimziFaultInjectionSourceConnectorConfig.TOPIC_NAME);
        numPartitions = config.getInt(StrimziFaultInjectionSourceConnectorConfig.NUM_PARTITIONS);
        FaultInjectionUtils.maybeInjectDelay(taskStartTime);
        FaultInjectionUtils.maybeInjectFailure(config.getBoolean(StrimziFaultInjectionSourceConnectorConfig.TASK_FAIL_ON_START),
                new ConnectException("Task failed to start"));
        LOGGER.info("Started task {}", this);
    }

    @Override
    public List<SourceRecord> poll() {
        LOGGER.debug("Poll {}", this);
        FaultInjectionUtils.maybeInjectDelay(taskPollTime);
        List<SourceRecord> records = new ArrayList<>();
        for (int i = 0; i < taskPollRecords; i++) {
            long currentRecord = record.getAndIncrement();
            records.add(new SourceRecord(Collections.singletonMap("", ""),
                    Collections.singletonMap("", ""),
                    topicName, (int) (currentRecord % numPartitions),
                    null, null,
                    VALUE_SCHEMA, currentRecord));
        }
        LOGGER.info("Returning {} records for topic {} from poll", taskPollRecords, topicName);
        return records;
    }

    @Override
    public void stop() {
        LOGGER.info("Stopping task {}", this);
        FaultInjectionUtils.maybeInjectDelay(taskStopTime);
    }
}