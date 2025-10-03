package io.xenoss.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.testng.TestException;

import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public class ActionTimer {
    private static final int DEFAULT_TIMEOUT = 10; //seconds
    private final long expiredTime;
    private final long startTime;

    private ActionTimer(int intervalSeconds) {
        startTime = System.currentTimeMillis();
        expiredTime = startTime + intervalSeconds * 1000L;
    }
    public static ActionTimer start(int intervalSeconds) {
        log.debug("Initializing timer for {} seconds", intervalSeconds);
        return new ActionTimer(intervalSeconds);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiredTime;
    }

    public int elapsed() {
        return (int) ((System.currentTimeMillis() - startTime)/1000);
    }

    public static boolean waitFor(Supplier<Boolean> condition) {
        return waitFor(condition, DEFAULT_TIMEOUT);
    }

    public static boolean waitFor(Supplier<Boolean> condition, int timeoutSeconds) {
        try {
            waitFor(condition, (actionResult) -> actionResult, timeoutSeconds);
            return true;
        } catch (Throwable t){
            return false;
        }
    }

    public static <T> T waitFor(Supplier<T> action, Function<T, Boolean> condition) {
        return waitFor(action, condition, DEFAULT_TIMEOUT);
    }
    public static <T> T waitFor(Supplier<T> action, Function<T, Boolean> condition, int timeoutSeconds) {
        return waitFor(action, condition, timeoutSeconds, true);
    }

    @SneakyThrows
    public static <T> T waitFor(Supplier<T> action, Function<T, Boolean> condition,
                                 int timeoutSeconds, boolean throwIfError) {
        log.debug("Starting wait for condition...");

        T actionResult = null;
        var timer = start(timeoutSeconds);
        do {
            try {
                actionResult = action.get();
                if (condition.apply(actionResult)) {
                    log.debug("Condition successfully satisfied in {} seconds", timer.elapsed());
                    return actionResult;
                }
            } catch (Throwable t) {
                if (timer.isExpired()) {
                    throw t;
                } else {
                    log.debug("Exception ignored:\n{}", t.getMessage());
                }
            }
            log.debug("One more attempt...");
            WaitUtils.forSeconds(1);
        } while (!timer.isExpired());

        if (throwIfError) {
            String message = String.format("%s seconds timer is expired, condition is not satisfied",
                    timeoutSeconds);
            throw new TestException(message);
        } else {
            return actionResult;
        }
    }
}
