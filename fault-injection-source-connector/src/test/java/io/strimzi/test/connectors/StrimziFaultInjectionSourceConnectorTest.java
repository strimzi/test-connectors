/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.strimzi.test.connectors;

import org.apache.kafka.common.config.ConfigDef;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StrimziFaultInjectionSourceConnectorTest {

    private Map<String, String> defaultConfig() {
        Map<String, String> config = new HashMap<>();
        config.put(StrimziFaultInjectionSourceConnectorConfig.START_TIME_MS, "0");
        config.put(StrimziFaultInjectionSourceConnectorConfig.STOP_TIME_MS, "0");
        config.put(StrimziFaultInjectionSourceConnectorConfig.TASK_START_TIME_MS, "0");
        config.put(StrimziFaultInjectionSourceConnectorConfig.TASK_STOP_TIME_MS, "0");
        config.put(StrimziFaultInjectionSourceConnectorConfig.TASK_POLL_TIME_MS, "0");
        config.put(StrimziFaultInjectionSourceConnectorConfig.TOPIC_NAME, "test-topic");
        config.put(StrimziFaultInjectionSourceConnectorConfig.NUM_PARTITIONS, "3");
        return config;
    }

    @Test
    void testStartAndStop() {
        StrimziFaultInjectionSourceConnector connector = new StrimziFaultInjectionSourceConnector();
        connector.start(defaultConfig());
        connector.stop();
    }

    @Test
    void testFailOnStart() {
        StrimziFaultInjectionSourceConnector connector = new StrimziFaultInjectionSourceConnector();
        Map<String, String> config = defaultConfig();
        config.put(StrimziFaultInjectionSourceConnectorConfig.FAIL_ON_START, "true");
        assertThrows(RuntimeException.class, () -> connector.start(config));
    }

    @Test
    void testTaskConfigs() {
        StrimziFaultInjectionSourceConnector connector = new StrimziFaultInjectionSourceConnector();
        connector.start(defaultConfig());
        List<Map<String, String>> taskConfigs = connector.taskConfigs(3);
        assertThat(taskConfigs.size(), is(3));
        for (Map<String, String> taskConfig : taskConfigs) {
            assertThat(taskConfig.get(StrimziFaultInjectionSourceConnectorConfig.TOPIC_NAME), is("test-topic"));
            assertThat(taskConfig.get(StrimziFaultInjectionSourceConnectorConfig.NUM_PARTITIONS), is("3"));
        }
        connector.stop();
    }

    @Test
    void testTaskConfigsAreIndependentCopies() {
        StrimziFaultInjectionSourceConnector connector = new StrimziFaultInjectionSourceConnector();
        connector.start(defaultConfig());
        List<Map<String, String>> taskConfigs = connector.taskConfigs(2);
        taskConfigs.get(0).put("extra.key", "value");
        assertThat(taskConfigs.get(1).containsKey("extra.key"), is(false));
        connector.stop();
    }

    @Test
    void testTaskClass() {
        StrimziFaultInjectionSourceConnector connector = new StrimziFaultInjectionSourceConnector();
        assertThat(connector.taskClass().equals(StrimziFaultInjectionSourceTask.class), is(true));
    }

    @Test
    void testConfigDef() {
        StrimziFaultInjectionSourceConnector connector = new StrimziFaultInjectionSourceConnector();
        ConfigDef configDef = connector.config();
        assertThat(configDef.configKeys().containsKey(StrimziFaultInjectionSourceConnectorConfig.FAIL_ON_START), is(true));
        assertThat(configDef.configKeys().containsKey(StrimziFaultInjectionSourceConnectorConfig.TOPIC_NAME), is(true));
        assertThat(configDef.configKeys().containsKey(StrimziFaultInjectionSourceConnectorConfig.NUM_PARTITIONS), is(true));
    }

    @Test
    void testNumPartitionsMinimumValidation() {
        Map<String, String> config = defaultConfig();
        config.put(StrimziFaultInjectionSourceConnectorConfig.NUM_PARTITIONS, "0");
        assertThrows(Exception.class, () -> new StrimziFaultInjectionSourceConnectorConfig(config));
    }

    @Test
    void testDefaultValues() {
        StrimziFaultInjectionSourceConnectorConfig config = new StrimziFaultInjectionSourceConnectorConfig(new HashMap<>());
        assertThat(config.getBoolean(StrimziFaultInjectionSourceConnectorConfig.FAIL_ON_START), is(false));
        assertThat(config.getBoolean(StrimziFaultInjectionSourceConnectorConfig.TASK_FAIL_ON_START), is(false));
        assertThat(config.getLong(StrimziFaultInjectionSourceConnectorConfig.START_TIME_MS), is(5_000L));
        assertThat(config.getString(StrimziFaultInjectionSourceConnectorConfig.TOPIC_NAME), is("my-topic"));
        assertThat(config.getInt(StrimziFaultInjectionSourceConnectorConfig.NUM_PARTITIONS), is(1));
    }

    @Test
    void testVersionReturnsImplementationVersion() {
        StrimziFaultInjectionSourceConnector connector = new StrimziFaultInjectionSourceConnector();
        // Implementation version comes from JAR manifest, which is absent during tests
        assertThat(connector.version(), is(nullValue()));
    }

    @Test
    void testSleepWithZeroDoesNotBlock() {
        long start = System.currentTimeMillis();
        StrimziFaultInjectionSourceConnector.sleep(0);
        long elapsed = System.currentTimeMillis() - start;
        assertThat(elapsed, is(lessThan(100L)));
    }

    @Test
    void testSleepWithNegativeDoesNotBlock() {
        long start = System.currentTimeMillis();
        StrimziFaultInjectionSourceConnector.sleep(-1);
        long elapsed = System.currentTimeMillis() - start;
        assertThat(elapsed, is(lessThan(100L)));
    }

    @Test
    void testSleepWithPositiveValueSleeps() {
        long start = System.currentTimeMillis();
        StrimziFaultInjectionSourceConnector.sleep(200);
        long elapsed = System.currentTimeMillis() - start;
        assertThat(elapsed, is(greaterThanOrEqualTo(150L)));
    }

    @Test
    void testSleepRestoresInterruptFlag() throws InterruptedException {
        Thread testThread = new Thread(() -> {
            Thread.currentThread().interrupt();
            StrimziFaultInjectionSourceConnector.sleep(5_000);
            assertThat(Thread.currentThread().isInterrupted(), is(true));
        });
        testThread.start();
        testThread.join(2_000);
        assertThat("Thread should have finished quickly after interrupt", testThread.isAlive(), is(false));
    }
}