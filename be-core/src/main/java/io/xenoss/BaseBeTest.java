package io.xenoss;

import io.xenoss.backend.client.BaseClient;
import io.xenoss.config.ConfigurationManager;
import io.xenoss.telemetry.TelemetryConsoleLogger;
import io.xenoss.telemetry.server.TestReporterHttpServer;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import java.io.IOException;

@Slf4j
public class BaseBeTest extends BaseTest {
    private static final int THREAD_POOL_SIZE = CONFIG.getThreadPoolSize();
    @BeforeSuite(alwaysRun = true)
    public static void initStartTelemetryServer() throws IOException {
        if (ConfigurationManager.getConfig()
                                .getStartTelemetryServer()) {
            TestReporterHttpServer.start();
            // Start telemetry monitoring for HTTP client metrics
            BaseClient.startTelemetryMonitoring();
        } else {
            log.info("Telemetry server is disabled in the config. Using console logging only");
        }

        TelemetryConsoleLogger.start();
    }

    @AfterSuite(alwaysRun = true)
    public static void shutdownTelemetryServer() {
        log.info("Test suite finished, shutting down telemetry servers...");
        try {
            // Stop telemetry monitoring thread
            BaseClient.stopTelemetryMonitoring();
            // Stop HTTP/WebSocket servers
            TestReporterHttpServer.stopServers();
        } catch (Exception e) {
            log.warn("Error during telemetry server shutdown", e);
        }
    }

    @BeforeClass(alwaysRun = true)
    public void setupParallelExecution(ITestContext context) {
        context.getCurrentXmlTest()
               .getSuite()
               .setDataProviderThreadCount(THREAD_POOL_SIZE);
        context.getCurrentXmlTest()
               .getSuite()
               .setPreserveOrder(false);
    }

    @AfterClass(alwaysRun = true)
    public void cleanupThreadLocalResources() {
        // Clean up BaseClient ThreadLocal variables to prevent memory leaks
        // This is critical because TestNG uses thread pools
        BaseClient.cleanupStaticThreadLocals();
        log.debug("Cleaned up ThreadLocal resources for current thread");
    }
}
