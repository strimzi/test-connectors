/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.strimzi.test.connectors;

import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StrimziFaultInjectionSourceTaskTest {
    private Map<String, String> defaultTaskConfig() {
        Map<String, String> config = new HashMap<>();
        config.put(StrimziFaultInjectionSourceConnectorConfig.TASK_FAIL_ON_START, "false");
        config.put(StrimziFaultInjectionSourceConnectorConfig.TASK_START_TIME_MS, "0");
        config.put(StrimziFaultInjectionSourceConnectorConfig.TASK_STOP_TIME_MS, "0");
        config.put(StrimziFaultInjectionSourceConnectorConfig.TASK_POLL_TIME_MS, "0");
        config.put(StrimziFaultInjectionSourceConnectorConfig.TASK_POLL_RECORDS, "5");
        config.put(StrimziFaultInjectionSourceConnectorConfig.TOPIC_NAME, "test-topic");
        config.put(StrimziFaultInjectionSourceConnectorConfig.NUM_PARTITIONS, "3");
        return config;
    }

    @Test
    void testStartAndStop() {
        StrimziFaultInjectionSourceTask task = new StrimziFaultInjectionSourceTask();
        task.start(defaultTaskConfig());
        task.stop();
    }

    @Test
    void testTaskFailOnStart() {
        StrimziFaultInjectionSourceTask task = new StrimziFaultInjectionSourceTask();
        Map<String, String> config = defaultTaskConfig();
        config.put(StrimziFaultInjectionSourceConnectorConfig.TASK_FAIL_ON_START, "true");
        assertThrows(ConnectException.class, () -> task.start(config));
    }

    @Test
    void testPollReturnsConfiguredNumberOfRecords() {
        StrimziFaultInjectionSourceTask task = new StrimziFaultInjectionSourceTask();
        task.start(defaultTaskConfig());
        List<SourceRecord> records = task.poll();
        assertThat(records.size(), is(5));
        task.stop();
    }

    @Test
    void testPollRecordsHaveCorrectTopic() {
        StrimziFaultInjectionSourceTask task = new StrimziFaultInjectionSourceTask();
        task.start(defaultTaskConfig());
        List<SourceRecord> records = task.poll();
        for (SourceRecord record : records) {
            assertThat(record.topic(), is("test-topic"));
        }
        task.stop();
    }

    @Test
    void testPollRecordsDistributeAcrossPartitions() {
        StrimziFaultInjectionSourceTask task = new StrimziFaultInjectionSourceTask();
        Map<String, String> config = defaultTaskConfig();
        config.put(StrimziFaultInjectionSourceConnectorConfig.TASK_POLL_RECORDS, "6");
        config.put(StrimziFaultInjectionSourceConnectorConfig.NUM_PARTITIONS, "3");
        task.start(config);
        List<SourceRecord> records = task.poll();
        assertThat(records.get(0).kafkaPartition(), is(0));
        assertThat(records.get(1).kafkaPartition(), is(1));
        assertThat(records.get(2).kafkaPartition(), is(2));
        assertThat(records.get(3).kafkaPartition(), is(0));
        task.stop();
    }

    @Test
    void testPollRecordsHaveSchema() {
        StrimziFaultInjectionSourceTask task = new StrimziFaultInjectionSourceTask();
        task.start(defaultTaskConfig());
        List<SourceRecord> records = task.poll();
        for (SourceRecord record : records) {
            assertThat(record.valueSchema(), is(notNullValue()));
        }
        task.stop();
    }

    @Test
    void testPollRecordCounterIncrements() {
        StrimziFaultInjectionSourceTask task = new StrimziFaultInjectionSourceTask();
        Map<String, String> config = defaultTaskConfig();
        config.put(StrimziFaultInjectionSourceConnectorConfig.TASK_POLL_RECORDS, "3");
        task.start(config);

        List<SourceRecord> firstPoll = task.poll();
        assertThat(firstPoll.get(0).value(), is(0L));
        assertThat(firstPoll.get(1).value(), is(1L));
        assertThat(firstPoll.get(2).value(), is(2L));

        List<SourceRecord> secondPoll = task.poll();
        assertThat(secondPoll.get(0).value(), is(3L));
        assertThat(secondPoll.get(1).value(), is(4L));
        assertThat(secondPoll.get(2).value(), is(5L));

        task.stop();
    }

    @Test
    void testVersionReturnsImplementationVersion() {
        StrimziFaultInjectionSourceTask task = new StrimziFaultInjectionSourceTask();
        // Implementation version comes from JAR manifest, which is absent during tests
        assertThat(task.version(), is(nullValue()));
    }
}