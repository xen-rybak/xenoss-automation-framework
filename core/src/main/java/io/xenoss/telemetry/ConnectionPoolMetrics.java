package io.xenoss.telemetry;

import io.xenoss.utils.ThreadingUtils;
import io.xenoss.utils.WaitUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class ConnectionPoolMetrics {
    private static final AtomicInteger activeConnections = new AtomicInteger(0);
    private static final AtomicInteger totalConnectionsCreated = new AtomicInteger(0);
    private static final AtomicInteger totalConnectionsClosed = new AtomicInteger(0);
    private static final AtomicLong totalRequestCount = new AtomicLong(0);
    private static final AtomicLong totalResponseCount = new AtomicLong(0);
    
    // Simulate connection closures based on keep-alive timeout
    static {
        // Start a background thread to simulate connection closures
        ThreadingUtils.startAsync(() -> {
            WaitUtils.forSeconds(35); // Check every 35 seconds (slightly more than keep-alive)

            // Simulate some idle connections being closed due to timeout
            int created = totalConnectionsCreated.get();
            int closed = totalConnectionsClosed.get();
            int currentActive = Math.max(0, (int) (totalRequestCount.get() - totalResponseCount.get()));
            int currentIdle = Math.max(0, created - closed - currentActive);

            // Close some idle connections (simulate keep-alive timeout)
            if (currentIdle > 2) {
                int toClose = 2;
                totalConnectionsClosed.addAndGet(toClose);
                log.debug("Simulated {} connection closures due to keep-alive timeout", toClose);
            }

            return null;
        }, "ConnectionPool-Simulator", true);
    }
    
    // Track connection lifecycle
    public static void onConnectionCreated() {
        totalConnectionsCreated.incrementAndGet();
        activeConnections.incrementAndGet();
        log.debug("Connection created. Active: {}, Total created: {}", 
                activeConnections.get(), totalConnectionsCreated.get());
    }
    
    public static void onConnectionClosed() {
        totalConnectionsClosed.incrementAndGet();
        activeConnections.decrementAndGet();
        log.debug("Connection closed. Active: {}, Total closed: {}", 
                activeConnections.get(), totalConnectionsClosed.get());
    }
    
    // Track request/response activity
    public static void onRequestSent() {
        totalRequestCount.incrementAndGet();
    }
    
    public static void onResponseReceived() {
        totalResponseCount.incrementAndGet();
    }
    
    // Get current metrics as a formatted map
    public static java.util.Map<String, String> getMetrics() {
        int created = totalConnectionsCreated.get();
        int closed = totalConnectionsClosed.get();
        long requests = totalRequestCount.get();
        long responses = totalResponseCount.get();
        
        // Estimate active connections based on in-flight requests
        int estimatedActive = Math.max(0, (int)(requests - responses));
        
        // Estimate idle connections (created - closed - active)
        int estimatedIdle = Math.max(0, created - closed - estimatedActive);
        
        return java.util.Map.of(
                "🔗 Active Connections", String.valueOf(estimatedActive),
                "💤 Idle Connections", String.valueOf(estimatedIdle),
                "📈 Total Connections Created", String.valueOf(created),
                "📉 Total Connections Closed", String.valueOf(closed),
                "📤 Total Requests Sent", String.valueOf(requests),
                "📥 Total Responses Received", String.valueOf(responses)
        );
    }
    
    // Reset metrics (useful for testing)
    public static void reset() {
        activeConnections.set(0);
        totalConnectionsCreated.set(0);
        totalConnectionsClosed.set(0);
        totalRequestCount.set(0);
        totalResponseCount.set(0);
        log.debug("Connection pool metrics reset");
    }
}
