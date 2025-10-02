package io.xenoss.telemetry.server;

import io.xenoss.telemetry.TelemetryData;
import io.xenoss.utils.ThreadingUtils;
import io.xenoss.utils.WaitUtils;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.Map;

@Slf4j
public class TelemetryWebSocketServer extends WebSocketServer {
    private volatile boolean running = true;
    private final ThreadingUtils.TaskInfo<Void> notificationThread;
    public TelemetryWebSocketServer(int port) {
        super(new InetSocketAddress(port));

        notificationThread = ThreadingUtils.startAsync(() -> {
            while (running) {
                WaitUtils.forSeconds(1);
                if (running) {
                    notifyChannels();
                }
            }
            log.debug("WebSocket notification thread stopped");
            return null;
        }, "TelemetryWebSocket-Notifier", true);
    }

    private void notifyChannels() {
        // Use LinkedHashMap to preserve the order from TelemetryData
        var telemetryFull = new java.util.LinkedHashMap<>(TelemetryData.getData());
        
        // Note: Don't add "Last Updated" here since it's already included in TelemetryData
        String json = telemetryToJson(telemetryFull);
        for (WebSocket conn : getConnections()) {
            conn.send(json);
        }
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        // Use LinkedHashMap to preserve ordering when sending initial data
        var initialData = new java.util.LinkedHashMap<>(TelemetryData.getData());
        conn.send(telemetryToJson(initialData));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) { }

    @Override
    public void onMessage(WebSocket conn, String message) { }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        log.error("WebSocket server thrown an error: ", ex);
    }

    @Override
    public void onStart() {
        log.info("WebSocket server started on port {}", getPort());
    }

    public void shutdown() {
        log.info("Shutting down WebSocket server on port {}", getPort());
        running = false;

        notificationThread.terminate();
        
        try {
            this.stop();
        } catch (Exception e) {
            log.warn("Error while stopping WebSocket server", e);
        }
        
        log.info("WebSocket server shutdown completed");
    }

    private static String telemetryToJson(Map<String, String> map) {
        StringBuilder json = new StringBuilder("{");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            json.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\",");
        }
        if (json.charAt(json.length() - 1) == ',') {
            json.deleteCharAt(json.length() - 1);
        }
        json.append("}");
        return json.toString();
    }
}
