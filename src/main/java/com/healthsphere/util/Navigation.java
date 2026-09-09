package com.healthsphere.util;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

/** Common navigation for Doctor, Patient, Hospital Modules: one Stage, bounded Scene history. */
public final class Navigation {

    private static final int MAX_HISTORY_SIZE = 4;
    private static final Deque<Scene> history = new ArrayDeque<>();

    private Navigation() { }

    public static void goTo(Stage stage, Supplier<Scene> nextScene) {
        Scene current = stage.getScene();
        if (current != null) {
            if (history.size() >= MAX_HISTORY_SIZE) {
                history.removeLast(); // Evict oldest scene graph to prevent RAM retention
            }
            history.push(current);
        }
        stage.setScene(nextScene.get());
    }

    /** Back action exposed as Runnable so it can be attached to any Back button. */
    public static Runnable backAction(Stage stage) {
        return () -> goBack(stage);
    }

    public static boolean goBack(Stage stage) {
        if (!history.isEmpty()) {
            stage.setScene(history.pop());
            return true;
        }
        return false;
    }

    public static void clearHistory() {
        history.clear();
    }

    public static void logout(Stage stage) {
        SessionManager.clearSession();
        clearHistory();
        if (stage != null) {
            stage.setScene(new com.healthsphere.view.authentication.LoginView(stage).getScene());
        }
    }
}
