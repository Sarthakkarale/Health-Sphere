
package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Notifications {

    private final Stage stage;

    public Notifications(Stage stage) {
        this.stage = stage;
    }

    // =========================================================
    // MAIN SCENE
    // =========================================================

    public Scene getScene() {

        // =====================================================
        // MAIN CONTENT
        // =====================================================

        VBox content =
                new VBox(20);

        content.setPadding(
                new Insets(5)
        );

        // =====================================================
        // FIRST IMAGE
        // SMALL SQUARE IMAGE
        // =====================================================

        VBox imageBanner =
                new VBox();

        imageBanner.setAlignment(
                Pos.CENTER
        );

        imageBanner.setPadding(
                new Insets(15)
        );

        imageBanner.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: #dbeafe;" +
                "-fx-border-radius: 18;"
        );

        ImageView bannerImage =
                createImage(
                        "/images/notifications/notification1.jpg",
                        220,
                        220
                );

        imageBanner.getChildren().add(
                bannerImage
        );

        // =====================================================
        // RECENT NOTIFICATIONS
        // =====================================================

        VBox notifications =
                PatientUI.coloredCard(
                        "🔔  Recent Notifications",
                        "#dbeafe"
                );

        notifications.getChildren().addAll(

                notification(
                        "/images/notifications/notification2.jpg",
                        "Appointment Reminder",
                        "Your cardiology appointment is scheduled for tomorrow at 10:00 AM.",
                        "Today"
                ),

                notification(
                        "/images/notifications/notification3.jpg",
                        "Medical Record Updated",
                        "Your latest medical record has been successfully added.",
                        "Yesterday"
                ),

                notification(
                        "/images/notifications/notification4.jpg",
                        "Health Passport",
                        "Your Health Passport contains newly updated information.",
                        "2 days ago"
                )
        );

        // =====================================================
        // ADD CONTENT
        // =====================================================

        content.getChildren().addAll(
                imageBanner,
                notifications
        );

        // =====================================================
        // SCROLL
        // =====================================================

        ScrollPane scroll =
                new ScrollPane(
                        content
                );

        scroll.setFitToWidth(
                true
        );

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scroll.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scroll.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;"
        );

        // =====================================================
        // IMPORTANT
        //
        // PatientUI creates:
        //
        // LEFT SIDEBAR
        // TOP HEADER
        // CENTER CONTENT
        //
        // Do NOT create another BorderPane here.
        // =====================================================

        VBox wrapper =
                new VBox(
                        scroll
                );

        // =====================================================
        // USE SHARED PATIENT UI
        // =====================================================

        return PatientUI.createScene(
                stage,
                "Notifications",
                "Notifications",
                "Stay updated with your latest health activities and reminders.",
                wrapper
        );
    }

    // =========================================================
    // NOTIFICATION CARD
    // =========================================================

    private VBox notification(
            String imagePath,
            String title,
            String description,
            String time
    ) {

        VBox box =
                new VBox(10);

        box.setPadding(
                new Insets(14)
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 14;"
        );

        // =====================================================
        // IMAGE
        // =====================================================

        ImageView image =
                createImage(
                        imagePath,
                        150,
                        80
                );

        // =====================================================
        // TITLE
        // =====================================================

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1e3a8a;"
        );

        // =====================================================
        // DESCRIPTION
        // =====================================================

        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setWrapText(
                true
        );

        descriptionLabel.setStyle(
                "-fx-text-fill: #475569;" +
                "-fx-font-size: 14px;"
        );

        // =====================================================
        // TIME
        // =====================================================

        Label timeLabel =
                new Label(time);

        timeLabel.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 12px;"
        );

        // =====================================================
        // ADD
        // =====================================================

        box.getChildren().addAll(
                image,
                titleLabel,
                descriptionLabel,
                timeLabel
        );

        return box;
    }

    // =========================================================
    // SAFE IMAGE LOADER
    // =========================================================

    private ImageView createImage(
            String path,
            double width,
            double height
    ) {

        ImageView view =
                new ImageView();

        var resource =
                getClass().getResource(path);

        // =====================================================
        // IMAGE NOT FOUND
        // =====================================================

        if (resource == null) {

            System.err.println(
                    "Notification image not found: "
                            + path
            );

            view.setFitWidth(
                    width
            );

            view.setFitHeight(
                    height
            );

            return view;
        }

        // =====================================================
        // LOAD IMAGE
        // =====================================================

        Image image =
                new Image(
                        resource.toExternalForm()
                );

        view.setImage(
                image
        );

        // =====================================================
        // IMAGE SIZE
        // =====================================================

        view.setFitWidth(
                width
        );

        view.setFitHeight(
                height
        );

        // =====================================================
        // IMPORTANT
        //
        // Preserve the original image proportions.
        // This prevents the image from being stretched.
        // =====================================================

        view.setPreserveRatio(
                true
        );

        // =====================================================
        // CENTER IMAGE
        // =====================================================

        view.setSmooth(
                true
        );

        return view;
    }
}

