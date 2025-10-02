package io.xenoss.telemetry;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.Connection;
import okhttp3.Protocol;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

@Slf4j
public class ConnectionEventListener extends EventListener {
    
    @Override
    public void connectStart(@NotNull Call call, @NotNull InetSocketAddress inetSocketAddress, @NotNull Proxy proxy) {
        log.debug("Connection starting to {}", inetSocketAddress);
    }
    
    @Override
    public void connectEnd(@NotNull Call call, @NotNull InetSocketAddress inetSocketAddress, @NotNull Proxy proxy, @Nullable Protocol protocol) {
        log.debug("New connection established to {}", inetSocketAddress);
        ConnectionPoolMetrics.onConnectionCreated();
    }
    
    @Override
    public void connectionReleased(@NotNull Call call, @NotNull Connection connection) {
        log.debug("Connection released back to pool: {}", connection);
        // Connection is being returned to pool after use - it's now idle
    }
    
    @Override
    public void connectionAcquired(@NotNull Call call, @NotNull Connection connection) {
        log.debug("Connection acquired from pool: {}", connection);
        // Connection is being taken from pool for use - it's now active
    }
    
    @Override
    public void callEnd(@NotNull Call call) {
        // Request completed successfully
    }
    
    @Override
    public void callFailed(@NotNull Call call, IOException ioe) {
        // Request failed
        log.debug("Call failed: {}", ioe.getMessage());
    }
    
    public static class Factory implements EventListener.Factory {
        @Override
        public @NotNull EventListener create(@NotNull Call call) {
            return new ConnectionEventListener();
        }
    }
}
