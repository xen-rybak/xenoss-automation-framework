package io.xenoss;

import io.xenoss.config.ConfigurationManager;
import io.xenoss.telemetry.TelemetryConsoleLogger;
import io.xenoss.telemetry.server.TestReporterHttpServer;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;
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
        } else {
            log.info("Telemetry server is disabled in the config. Using console logging only");
        }

        TelemetryConsoleLogger.start();
    }

    @AfterSuite(alwaysRun = true)
    public static void shutdownTelemetryServer() {
        log.info("Test suite finished, shutting down telemetry servers...");
        try {
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
}
