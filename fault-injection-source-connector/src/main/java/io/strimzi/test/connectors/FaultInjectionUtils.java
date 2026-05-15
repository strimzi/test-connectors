/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.strimzi.test.connectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shared utility methods for injecting faults (delays and failures) during connector and task lifecycle.
 */
final class FaultInjectionUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FaultInjectionUtils.class);

    private FaultInjectionUtils() {}

    /**
     * Injects a delay if the given duration is positive.
     *
     * @param ms delay in milliseconds
     */
    static void maybeInjectDelay(long ms) {
        if (ms > 0) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                LOGGER.warn("Interrupted during sleep", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Injects a failure if the flag is set.
     *
     * @param shouldFail whether to throw
     * @param exception  the exception to throw
     */
    static void maybeInjectFailure(boolean shouldFail, RuntimeException exception) {
        if (shouldFail) {
            throw exception;
        }
    }
}