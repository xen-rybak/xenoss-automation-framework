package io.xenoss.unit;

import io.xenoss.telemetry.TelemetryData;
import io.xenoss.telemetry.server.TestReporterHttpServer;
import io.xenoss.utils.ThreadingUtils;
import io.xenoss.utils.WaitUtils;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;

public class TelemetryReporterSelfTest {
    @Test
    public void launchTelemetryServer() throws IOException {
        try {
            TestReporterHttpServer.start();

            // Telemetry simulation with proper stopping
            var simulationThread = ThreadingUtils.startAsync(() -> {
                int progress = 0;
                while (progress < 100) {
                    WaitUtils.forSeconds(1); // Shorter sleep for faster test
                        progress += 10;
                        Map<String, String> testTelemetry = Map.of(
                                "🎯 Test Status", "Running Self-Test",
                                "📈 Progress", progress + "%",
                                "🧪 Test Type", "Telemetry System Validation",
                                "🔄 Test Phase", progress < 50 ? "Setup" : "Execution"
                        );
                        TelemetryData.updateTestMetrics(testTelemetry);
                }
                return null;
            }, "Test telemetry simulation thread", true);

            // Wait a bit to let the telemetry run
            WaitUtils.forSeconds(15);

            // Stop simulation thread
            simulationThread.terminate();

        } finally {
            // Always clean up servers
            TestReporterHttpServer.stopServers();
        }
    }
}
