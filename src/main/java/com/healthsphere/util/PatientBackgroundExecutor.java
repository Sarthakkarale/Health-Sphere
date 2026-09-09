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

    private PatientBackgroundExecutor() {}

    public static <T> void execute(Task<T> task) {
        AppBackgroundExecutor.execute(task);
    }

    public static void execute(Runnable runnable) {
        AppBackgroundExecutor.execute(runnable);
    }

    public static <T> Task<T> submitTask(Task<T> task) {
        return AppBackgroundExecutor.submitTask(task);
    }
}
