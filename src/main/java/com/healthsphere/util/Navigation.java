package com.healthsphere.util;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

/** Common navigation for the Doctor Module: one Stage, multiple Scenes. */
public final class Navigation {

    private static final Deque<Scene> history = new ArrayDeque<>();

    private Navigation() { }

    public static void goTo(Stage stage, Supplier<Scene> nextScene) {
        Scene current = stage.getScene();
        if (current != null) {
            history.push(current);
        }
        stage.setScene(nextScene.get());
    }

    /** Back action exposed as Runnable so it can be attached to any Back button. */
    public static Runnable backAction(Stage stage) {
        return () -> goBack(stage);
    }

    public static void goBack(Stage stage) {
        if (!history.isEmpty()) {
            stage.setScene(history.pop());
        }
    }

    public static void clearHistory() {
        history.clear();
    }
}
