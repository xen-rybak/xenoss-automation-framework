package io.xenoss.telemetry;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@Slf4j
public class HttpMetricsInterceptor implements Interceptor {
    
    @Override
    public @NotNull Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        
        // Track request sent
        ConnectionPoolMetrics.onRequestSent();
        
        long startTime = System.currentTimeMillis();
        
        try {
            Response response = chain.proceed(request);
            
            // Track response received
            ConnectionPoolMetrics.onResponseReceived();
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            log.debug("HTTP {} {} -> {} ({}ms)", 
                    request.method(), 
                    request.url(),
                    response.code(), 
                    duration);
                    
            return response;
            
        } catch (IOException e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            log.debug("HTTP {} {} -> ERROR ({}ms): {}", 
                    request.method(), 
                    request.url(),
                    duration,
                    e.getMessage());
                    
            throw e;
        }
    }
}
