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

    public static void logout(Stage stage) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Logout");
        alert.setHeaderText("Log Out of Health-Sphere?");
        alert.setContentText("Are you sure you want to end your current session?");
        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            SessionManager.clearSession();
            clearHistory();
            if (stage != null) {
                stage.setScene(new com.healthsphere.view.authentication.LoginView(stage).getScene());
            }
        }
    }
}
