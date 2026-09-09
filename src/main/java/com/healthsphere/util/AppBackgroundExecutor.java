package com.healthsphere.util;

import javafx.concurrent.Task;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Global, application-wide controlled background thread executor for HealthSphere.
 * Manages a bounded pool of daemon worker threads across Patient, Doctor, Hospital,
 * and Admin modules to prevent thread proliferation and OutOfMemoryError crashes.
 */
public final class AppBackgroundExecutor {

    private static final int THREAD_POOL_SIZE = 4;

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(THREAD_POOL_SIZE, new ThreadFactory() {
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "HealthSphereWorker-" + threadNumber.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        }
    });

    private AppBackgroundExecutor() { }

    /**
     * Submits a JavaFX Task to run on the application-wide thread pool.
     */
    public static <T> void execute(Task<T> task) {
        if (task != null) {
            SessionManager.registerTask(task);
            EXECUTOR.submit(task);
        }
    }

    /**
     * Submits a Runnable to run on the application-wide thread pool.
     */
    public static void execute(Runnable runnable) {
        if (runnable != null) {
            EXECUTOR.submit(runnable);
        }
    }

    /**
     * Helper to safely execute a task and track it.
     */
    public static <T> Task<T> submitTask(Task<T> task) {
        execute(task);
        return task;
    }
}
