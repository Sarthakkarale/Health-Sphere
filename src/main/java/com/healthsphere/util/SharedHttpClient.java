package com.healthsphere.util;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Singleton Java 11 HttpClient instance shared across the entire application.
 * Prevents native thread pool explosion (httpclient-dispatch-* & HttpClient-*-SelectorManager)
 * and eliminates OutOfMemoryError / EXCEPTION_ACCESS_VIOLATION crashes.
 */
public final class SharedHttpClient {

    private static final HttpClient INSTANCE;

    static {
        java.util.concurrent.atomic.AtomicInteger threadCount = new java.util.concurrent.atomic.AtomicInteger(1);
        HttpClient client;
        try {
            client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .executor(java.util.concurrent.Executors.newFixedThreadPool(4, r -> {
                        Thread t = new Thread(r, "SharedHttpClient-Worker-" + threadCount.getAndIncrement());
                        t.setDaemon(true);
                        return t;
                    }))
                    .build();
        } catch (Exception e) {
            client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
        }
        INSTANCE = client;
    }

    private SharedHttpClient() { }

    /**
     * Returns the global shared HttpClient instance.
     */
    public static HttpClient getInstance() {
        return INSTANCE;
    }
}
