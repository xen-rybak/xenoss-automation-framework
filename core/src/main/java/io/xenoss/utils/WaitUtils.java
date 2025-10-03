package io.xenoss.utils;

import java.util.concurrent.TimeUnit;

/**
 * Utility class for waiting/sleeping operations.
 */
public class WaitUtils {
    /**
     * Sleeps for the specified number of seconds.
     * Properly handles InterruptedException by restoring the interrupted status.
     *
     * @param seconds number of seconds to sleep
     * @throws RuntimeException if interrupted during sleep (preserves interrupt status)
     */
    public static void forSeconds(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread()
                  .interrupt();
            throw new RuntimeException("Thread was interrupted during wait", e);
        }
    }

    /**
     * Sleeps for the specified number of milliseconds.
     * Properly handles InterruptedException by restoring the interrupted status.
     *
     * @param milliseconds number of milliseconds to sleep
     * @throws RuntimeException if interrupted during sleep (preserves interrupt status)
     */
    public static void forMilliseconds(long milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread()
                  .interrupt();
            throw new RuntimeException("Thread was interrupted during wait", e);
        }
    }
}
