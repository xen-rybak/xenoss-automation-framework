package io.xenoss.telemetry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Thread-safe telemetry data manager using ConcurrentHashMap for metrics storage
 * and ReadWriteLock for data snapshot operations.
 * <p>
 * This refactoring removes excessive synchronization and uses appropriate concurrent data structures:
 * - ConcurrentHashMap for systemMetrics and testMetrics (thread-safe without extra locks)
 * - ReadWriteLock for protecting the data snapshot to allow concurrent reads
 * - AtomicLong for rate limiting timestamp
 */
public class TelemetryData {
    private static final long START_TIME = System.currentTimeMillis();
    private static final String START_TIME_FORMATTED = LocalDateTime.now()
                                                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    // Rate limiting for updates during heavy load using atomic variable
    private static final AtomicLong lastUpdateTime = new AtomicLong(0);
    private static final long MIN_UPDATE_INTERVAL_MS = 100; // Minimum 100ms between updates

    // ConcurrentHashMap provides thread-safety without additional synchronization
    private static final Map<String, String> systemMetrics = new ConcurrentHashMap<>();
    private static final Map<String, String> testMetrics = new ConcurrentHashMap<>();

    // Use ReadWriteLock for the data snapshot - allows multiple concurrent readers
    private static final Map<String, String> data = new LinkedHashMap<>();
    private static final ReadWriteLock dataLock = new ReentrantReadWriteLock();

    /**
     * Updates system metrics and triggers combined data update.
     * No synchronization needed - ConcurrentHashMap.putAll is thread-safe.
     */
    public static void updateSystemMetrics(Map<String, String> metrics) {
        systemMetrics.putAll(metrics);
        updateCombinedData();
    }

    /**
     * Updates test metrics and triggers combined data update.
     * No synchronization needed - ConcurrentHashMap.putAll is thread-safe.
     */
    public static void updateTestMetrics(Map<String, String> metrics) {
        testMetrics.putAll(metrics);
        updateCombinedData();
    }

    /**
     * Helper method to add system metrics in a specific order.
     * ConcurrentHashMap.get() is thread-safe without additional locking.
     */
    private static void addSystemMetricInOrder(Map<String, String> orderedData, String key) {
        String value = systemMetrics.get(key);
        if (value != null) {
            orderedData.put(key, value);
        }
    }

    /**
     * Updates the combined data snapshot with rate limiting.
     * Uses write lock only when actually updating the data snapshot.
     */
    private static void updateCombinedData() {
        // Rate limiting: Skip update if called too frequently during heavy load
        long currentTime = System.currentTimeMillis();
        long lastUpdate = lastUpdateTime.get();

        // Use compareAndSet for atomic rate limit check
        if (currentTime - lastUpdate < MIN_UPDATE_INTERVAL_MS) {
            return; // Skip this update to reduce contention
        }

        // Try to update lastUpdateTime atomically
        if (!lastUpdateTime.compareAndSet(lastUpdate, currentTime)) {
            return; // Another thread is already updating
        }

        // Build the complete ordered data first (no locks needed for reads from ConcurrentHashMap)
        Map<String, String> orderedData = new LinkedHashMap<>();

        // System information section
        orderedData.put("📊 SYSTEM METRICS", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        orderedData.put("⏰ Application Start Time", START_TIME_FORMATTED);
        orderedData.put("⏱️ Running Time", formatUptime(System.currentTimeMillis() - START_TIME));

        // Add system metrics in a consistent order
        addSystemMetricInOrder(orderedData, "🌐 HTTP Client");
        addSystemMetricInOrder(orderedData, "🏊 Max Connection Pool Size");
        addSystemMetricInOrder(orderedData, "🔧 GZIP Enabled");
        addSystemMetricInOrder(orderedData, "🔗 Active Connections");
        addSystemMetricInOrder(orderedData, "💤 Idle Connections");
        addSystemMetricInOrder(orderedData, "📈 Total Connections Created");
        addSystemMetricInOrder(orderedData, "📉 Total Connections Closed");
        addSystemMetricInOrder(orderedData, "📤 Total Requests Sent");
        addSystemMetricInOrder(orderedData, "📥 Total Responses Received");
        addSystemMetricInOrder(orderedData, "💾 Memory Used");
        addSystemMetricInOrder(orderedData, "💽 Memory Total");
        addSystemMetricInOrder(orderedData, "⚡ Available Processors");

        // Add any additional system metrics that might not be in the predefined list
        // Snapshot iteration is safe with ConcurrentHashMap
        for (Map.Entry<String, String> entry : systemMetrics.entrySet()) {
            if (!orderedData.containsKey(entry.getKey())) {
                orderedData.put(entry.getKey(), entry.getValue());
            }
        }

        // Test metrics section
        if (!testMetrics.isEmpty()) {
            orderedData.put("🧪 TEST METRICS", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            orderedData.putAll(testMetrics);
        }

        // Add last updated timestamp
        orderedData.put("🔄 Last Updated",
                LocalDateTime.now()
                             .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        // Replace the entire data map using write lock
        dataLock.writeLock()
                .lock();
        try {
            data.clear();
            data.putAll(orderedData);
        } finally {
            dataLock.writeLock()
                    .unlock();
        }
    }

    private static String formatUptime(long uptimeMs) {
        long seconds = uptimeMs / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, secs);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, secs);
        } else {
            return String.format("%ds", secs);
        }
    }

    /**
     * Returns a defensive copy of the current telemetry data snapshot.
     * Uses read lock to allow concurrent reads without blocking.
     *
     * @return a copy of the current telemetry data
     */
    public static Map<String, String> getData() {
        dataLock.readLock()
                .lock();
        try {
            return new LinkedHashMap<>(data);
        } finally {
            dataLock.readLock()
                    .unlock();
        }
    }
}
