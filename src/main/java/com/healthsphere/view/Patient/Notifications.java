package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class Notifications {

    private final PatientNavigator navigator;

    public Notifications(
            PatientNavigator navigator
    ) {
        this.navigator = navigator;
    }

    public Scene getScene() {

        VBox content =
                new VBox(18);

        VBox notifications =
                PatientUI.card("Notifications");

        notifications.getChildren().addAll(

                notification(
                        "Appointment Reminder",
                        "Your appointment with Dr. Sarah Jenkins is tomorrow at 10:00 AM.",
                        "Today"
                ),

                notification(
                        "Health Passport Updated",
                        "Your health passport has been updated with your latest information.",
                        "Yesterday"
                ),

                notification(
                        "Medical Record Available",
                        "Your latest medical report is available to view.",
                        "2 days ago"
                ),

                notification(
                        "AI Health Insight",
                        "A new health insight is available for review.",
                        "3 days ago"
                )
        );

        content.getChildren().add(
                notifications
        );

        return PatientUI.createScene(
                navigator,
                "Notifications",
                "Notifications",
                "Stay updated with appointments, health records and important alerts.",
                content
        );
    }

    private HBox notification(
            String title,
            String message,
            String time
    ) {

        HBox row =
                new HBox(15);

        row.setPadding(
                new Insets(14)
        );

        VBox details =
                new VBox(5);

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;"
        );

        Label messageLabel =
                new Label(message);

        messageLabel.setWrapText(true);

        messageLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        details.getChildren().addAll(
                titleLabel,
                messageLabel
        );

        Label timeLabel =
                PatientUI.muted(time);

        HBox.setHgrow(
                details,
                Priority.ALWAYS
        );

        row.getChildren().addAll(
                details,
                timeLabel
        );

        return row;
    }
}