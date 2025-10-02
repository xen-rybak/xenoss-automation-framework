package io.xenoss.utils;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;

public class WaitUtils {
    @SneakyThrows
    public static void forSeconds(int seconds) {
        TimeUnit.SECONDS.sleep(seconds);
    }
}
