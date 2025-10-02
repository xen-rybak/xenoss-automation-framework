package io.xenoss;

import io.xenoss.config.ConfigInstance;
import io.xenoss.config.ConfigurationManager;
import org.awaitility.core.ThrowingRunnable;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class BaseTest {
    protected static final ConfigInstance CONFIG = ConfigurationManager.getConfig();

    protected void assertWithAwait(ThrowingRunnable assertion, long timeoutSeconds) {
        await()
                .pollInterval(5L, TimeUnit.SECONDS)
                .timeout(timeoutSeconds, TimeUnit.SECONDS)
                .ignoreExceptions()
                .untilAsserted(assertion);
    }
}
