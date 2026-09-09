package com.healthsphere.util;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Singleton Java 11 HttpClient instance shared across the entire application.
 * Prevents native thread pool explosion (httpclient-dispatch-* & HttpClient-*-SelectorManager)
 * and eliminates OutOfMemoryError / EXCEPTION_ACCESS_VIOLATION crashes.
 */
public final class SharedHttpClient {

    private static final HttpClient INSTANCE = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private SharedHttpClient() { }

    /**
     * Returns the global shared HttpClient instance.
     */
    public static HttpClient getInstance() {
        return INSTANCE;
    }
}
