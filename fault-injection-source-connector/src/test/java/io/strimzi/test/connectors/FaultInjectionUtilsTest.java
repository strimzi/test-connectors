/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.strimzi.test.connectors;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FaultInjectionUtilsTest {
    @Test
    void testMaybeInjectDelayWithZeroDoesNotBlock() {
        long start = System.currentTimeMillis();
        FaultInjectionUtils.maybeInjectDelay(0);
        long elapsed = System.currentTimeMillis() - start;
        assertThat(elapsed, is(lessThan(100L)));
    }

    @Test
    void testMaybeInjectDelayWithNegativeDoesNotBlock() {
        long start = System.currentTimeMillis();
        FaultInjectionUtils.maybeInjectDelay(-1);
        long elapsed = System.currentTimeMillis() - start;
        assertThat(elapsed, is(lessThan(100L)));
    }

    @Test
    void testMaybeInjectDelayWithPositiveValueSleeps() {
        long start = System.currentTimeMillis();
        FaultInjectionUtils.maybeInjectDelay(200);
        long elapsed = System.currentTimeMillis() - start;
        assertThat(elapsed, is(greaterThanOrEqualTo(150L)));
    }

    @Test
    void testMaybeInjectDelayRestoresInterruptFlag() throws InterruptedException {
        Thread testThread = new Thread(() -> {
            Thread.currentThread().interrupt();
            FaultInjectionUtils.maybeInjectDelay(5_000);
            assertThat(Thread.currentThread().isInterrupted(), is(true));
        });
        testThread.start();
        testThread.join(2_000);
        assertThat("Thread should have finished quickly after interrupt", testThread.isAlive(), is(false));
    }

    @Test
    void testMaybeInjectFailureWhenTrue() {
        assertThrows(RuntimeException.class, () -> FaultInjectionUtils.maybeInjectFailure(true, new RuntimeException("test")));
    }

    @Test
    void testMaybeInjectFailureWhenFalse() {
        assertDoesNotThrow(() -> FaultInjectionUtils.maybeInjectFailure(false, new RuntimeException("test")));
    }
}