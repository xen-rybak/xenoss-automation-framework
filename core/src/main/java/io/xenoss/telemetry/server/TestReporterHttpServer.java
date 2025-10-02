package io.xenoss.telemetry.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import io.xenoss.config.ConfigurationManager;
import io.xenoss.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

@Slf4j
public class TestReporterHttpServer {

    private static final int HTTP_PORT = ConfigurationManager.getConfig()
                                                             .getTelemetryHttpPort();
    private static final int WS_PORT = ConfigurationManager.getConfig()
                                                           .getTelemetryWsPort();

    // Keep references to servers for proper shutdown
    private static TelemetryWebSocketServer wsServer;
    private static HttpServer httpServer;
    private static volatile boolean serversStarted = false;

    public static synchronized void start() throws IOException {
        if (serversStarted) {
            log.warn("Telemetry servers are already started");
            return;
        }

        try {
            // Start WebSocket server
            wsServer = new TelemetryWebSocketServer(WS_PORT);
            wsServer.start();

            // Start HTTP server
            httpServer = HttpServer.create(new InetSocketAddress(HTTP_PORT), 0);
            httpServer.createContext("/watch", new WatchHandler());
            httpServer.setExecutor(null);
            httpServer.start();

            serversStarted = true;

            log.info("HTTP server started on port {}", HTTP_PORT);
            log.info("WebSocket server started on port {}", WS_PORT);
            log.info("Please go to http://localhost:8080/watch for detailed tests statistics");

            // Add shutdown hook as safety net
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("JVM shutdown detected, stopping telemetry servers...");
                stopServers();
            }));

        } catch (Exception e) {
            log.error("Failed to start telemetry servers", e);
            stopServers(); // Clean up any partially started servers
            throw e;
        }
    }

    public static synchronized void stopServers() {
        if (!serversStarted) {
            return;
        }

        log.info("Stopping telemetry servers...");

        // Stop WebSocket server
        if (wsServer != null) {
            try {
                wsServer.shutdown();
            } catch (Exception e) {
                log.warn("Error stopping WebSocket server", e);
            }
        }

        // Stop HTTP server
        if (httpServer != null) {
            try {
                httpServer.stop(2); // Wait up to 2 seconds for connections to close
                log.info("HTTP server stopped");
            } catch (Exception e) {
                log.warn("Error stopping HTTP server", e);
            }
        }

        wsServer = null;
        httpServer = null;
        serversStarted = false;
        log.info("Telemetry servers shutdown completed");
    }

    static class WatchHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String htmlFile = FileUtils.getResourceFileAsString("telemetry/watch.html");
            htmlFile = htmlFile.replace("{{WS_PORT}}", String.valueOf(WS_PORT));
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            sendResponse(exchange, 200, htmlFile);
        }
    }


    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
