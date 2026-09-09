package com.healthsphere.util;

import javafx.concurrent.Task;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Shared, controlled background thread executor for the Patient Module.
 * Prevents thread explosion and OutOfMemoryError by managing a bounded pool
 * of daemon worker threads.
 */
public class PatientBackgroundExecutor {

    private static final int THREAD_POOL_SIZE = 4;

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(THREAD_POOL_SIZE, new ThreadFactory() {
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "PatientBackgroundWorker-" + threadNumber.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        }
    });

    private PatientBackgroundExecutor() {}

    /**
     * Submits a JavaFX Task to run on the shared background thread pool.
     */
    public static <T> void execute(Task<T> task) {
        if (task != null) {
            EXECUTOR.submit(task);
        }
    }

    /**
     * Submits a Runnable to run on the shared background thread pool.
     */
    public static void execute(Runnable runnable) {
        if (runnable != null) {
            EXECUTOR.submit(runnable);
        }
    }

    /**
     * Helper to safely execute a task and handle lifecycle check.
     */
    public static <T> Task<T> submitTask(Task<T> task) {
        if (task != null) {
            EXECUTOR.submit(task);
        }
        return task;
    }
}
