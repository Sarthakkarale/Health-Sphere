package com.healthsphere.view.Patient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.healthsphere.controller.patient.NotificationController;
import com.healthsphere.model.Notification;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Notifications {

    private final Stage stage;

    private final NotificationController notificationController;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Notifications(Stage stage) {

        this.stage = stage;

        this.notificationController =
                new NotificationController();
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

        content.setFillWidth(true);

        // =====================================================
        // FIRST IMAGE
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
        // RECENT NOTIFICATIONS CARD
        // =====================================================

        VBox notificationsCard =
                PatientUI.coloredCard(
                        "🔔  Recent Notifications",
                        "#dbeafe"
                );

        notificationsCard.setMaxWidth(
                Double.MAX_VALUE
        );

        notificationsCard.setFillWidth(true);

        // =====================================================
        // LOAD DYNAMIC NOTIFICATIONS
        // =====================================================

        loadNotifications(
                notificationsCard
        );

        // =====================================================
        // REVIEW SECTION
        // =====================================================

        VBox reviewCard =
                createReviewCard();

        // =====================================================
        // ADD CONTENT
        // =====================================================

        content.getChildren().addAll(
                imageBanner,
                notificationsCard,
                reviewCard
        );

        // =====================================================
        // PATIENT UI
        //
        // PatientUI already provides the main ScrollPane.
        // Do NOT create another ScrollPane here.
        // =====================================================

        return PatientUI.createScene(
                stage,
                "Notifications",
                "Notifications",
                "Stay updated with your latest health activities and reminders.",
                content
        );
    }

    // =========================================================
    // LOAD NOTIFICATIONS FROM FIRESTORE
    // =========================================================

    private void loadNotifications(VBox notificationsCard) {
        if (notificationsCard == null) return;
        notificationsCard.getChildren().clear();
        notificationsCard.getChildren().add(com.healthsphere.util.ShimmerPlaceholder.createListShimmer(3));

        javafx.concurrent.Task<List<Notification>> task = new javafx.concurrent.Task<>() {
            @Override
            protected List<Notification> call() throws Exception {
                List<Notification> list = notificationController.getCurrentPatientNotifications();
                if (list != null) {
                    list.sort(Comparator.comparing(
                            Notification::getCreatedAt,
                            Comparator.nullsLast(Comparator.reverseOrder())
                    ));
                }
                return list != null ? list : new ArrayList<>();
            }
        };

        task.setOnSucceeded(e -> {
            notificationsCard.getChildren().clear();
            List<Notification> notificationList = task.getValue();
            int unreadCount = 0;
            for (Notification n : notificationList) {
                if (n != null && !n.isRead()) unreadCount++;
            }

            Label countLabel = new Label();
            if (unreadCount > 0) {
                countLabel.setText(unreadCount + " unread notification" + (unreadCount == 1 ? "" : "s"));
                countLabel.setStyle("-fx-text-fill: #1d4ed8; -fx-font-weight: bold; -fx-font-size: 13px;");
            } else {
                countLabel.setText("All notifications read");
                countLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
            }
            notificationsCard.getChildren().add(countLabel);

            if (notificationList.isEmpty()) {
                VBox emptyBox = new VBox(10);
                emptyBox.setAlignment(Pos.CENTER);
                emptyBox.setPadding(new Insets(20));
                Label emptyLabel = new Label("No notifications found.");
                emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");
                emptyBox.getChildren().add(emptyLabel);
                notificationsCard.getChildren().add(emptyBox);
                return;
            }

            for (Notification notification : notificationList) {
                if (notification != null) {
                    notificationsCard.getChildren().add(createNotificationCard(notification, notificationsCard));
                }
            }
        });

        task.setOnFailed(e -> {
            notificationsCard.getChildren().clear();
            Label errLbl = new Label("Unable to load notifications. Please try again.");
            errLbl.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
            notificationsCard.getChildren().add(errLbl);
        });

        com.healthsphere.util.PatientBackgroundExecutor.execute(task);
    }

    // =========================================================
    // CREATE DYNAMIC NOTIFICATION CARD
    // =========================================================

    private VBox createNotificationCard(
            Notification notification,
            VBox notificationsCard) {

        VBox box =
                new VBox(10);

        box.setPadding(
                new Insets(14)
        );

        box.setMaxWidth(
                Double.MAX_VALUE
        );

        // =====================================================
        // READ / UNREAD STYLE
        // =====================================================

        if (notification.isRead()) {

            box.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-background-radius: 14;" +
                    "-fx-border-color: #bfdbfe;" +
                    "-fx-border-radius: 14;"
            );

        } else {

            box.setStyle(
                    "-fx-background-color: #eff6ff;" +
                    "-fx-background-radius: 14;" +
                    "-fx-border-color: #60a5fa;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 14;"
            );
        }

        // =====================================================
        // TOP ROW
        // =====================================================

        HBox topRow =
                new HBox(10);

        topRow.setAlignment(
                Pos.CENTER_LEFT
        );

        // -----------------------------------------------------
        // IMAGE
        // -----------------------------------------------------

        ImageView image =
                createImage(
                        getNotificationImage(
                                notification.getType()
                        ),
                        150,
                        80
                );

        // -----------------------------------------------------
        // TITLE + DESCRIPTION
        // -----------------------------------------------------

        VBox textBox =
                new VBox(6);

        HBox.setHgrow(
                textBox,
                Priority.ALWAYS
        );

        // -----------------------------------------------------
        // TITLE
        // -----------------------------------------------------

        Label titleLabel =
                new Label(
                        safeText(
                                notification.getTitle(),
                                "Notification"
                        )
                );

        titleLabel.setWrapText(
                true
        );

        titleLabel.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1e3a8a;"
        );

        // -----------------------------------------------------
        // DESCRIPTION
        // -----------------------------------------------------

        Label descriptionLabel =
                new Label(
                        safeText(
                                notification.getDescription(),
                                ""
                        )
                );

        descriptionLabel.setWrapText(
                true
        );

        descriptionLabel.setStyle(
                "-fx-text-fill: #475569;" +
                "-fx-font-size: 14px;"
        );

        textBox.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );

        topRow.getChildren().addAll(
                image,
                textBox
        );

        // =====================================================
        // BOTTOM ROW
        // =====================================================

        HBox bottomRow =
                new HBox(10);

        bottomRow.setAlignment(
                Pos.CENTER_LEFT
        );

        // -----------------------------------------------------
        // TIME
        // -----------------------------------------------------

        Label timeLabel =
                new Label(
                        formatDateTime(
                                notification.getCreatedAt()
                        )
                );

        timeLabel.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 12px;"
        );

        // -----------------------------------------------------
        // TYPE
        // -----------------------------------------------------

        Label typeLabel =
                new Label(
                        formatType(
                                notification.getType()
                        )
                );

        typeLabel.setStyle(
                "-fx-background-color: #dbeafe;" +
                "-fx-text-fill: #1d4ed8;" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 4 10;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;"
        );

        // -----------------------------------------------------
        // READ STATUS
        // -----------------------------------------------------

        Label statusLabel =
                new Label();

        if (notification.isRead()) {

            statusLabel.setText(
                    "✓ READ"
            );

            statusLabel.setStyle(
                    "-fx-text-fill: #16a34a;" +
                    "-fx-font-size: 11px;" +
                    "-fx-font-weight: bold;"
            );

        } else {

            statusLabel.setText(
                    "● UNREAD"
            );

            statusLabel.setStyle(
                    "-fx-text-fill: #2563eb;" +
                    "-fx-font-size: 11px;" +
                    "-fx-font-weight: bold;"
            );
        }

        bottomRow.getChildren().addAll(
                typeLabel,
                timeLabel,
                statusLabel
        );

        // =====================================================
        // BUTTON ROW
        // =====================================================

        HBox buttonRow =
                new HBox(10);

        buttonRow.setAlignment(
                Pos.CENTER_RIGHT
        );

        // -----------------------------------------------------
        // MARK AS READ / UNREAD
        // -----------------------------------------------------

        Button readButton =
                new Button();

        if (notification.isRead()) {

            readButton.setText(
                    "Mark as Unread"
            );

        } else {

            readButton.setText(
                    "Mark as Read"
            );
        }

        readButton.setStyle(
                "-fx-background-color: #dbeafe;" +
                "-fx-text-fill: #1d4ed8;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 8 14;" +
                "-fx-cursor: hand;"
        );

        readButton.setOnAction(e -> {

            try {

                if (notification.isRead()) {

                    notificationController
                            .markAsUnread(
                                    notification
                                            .getNotificationId()
                            );

                } else {

                    notificationController
                            .markAsRead(
                                    notification
                                            .getNotificationId()
                            );
                }

                // Refresh the page
                refreshPage();

            } catch (Exception ex) {

                ex.printStackTrace();

                showError(
                        "Unable to update notification.",
                        ex.getMessage()
                );
            }
        });

        // -----------------------------------------------------
        // DELETE BUTTON
        // -----------------------------------------------------

        Button deleteButton =
                new Button(
                        "Delete"
                );

        deleteButton.setStyle(
                "-fx-background-color: #fee2e2;" +
                "-fx-text-fill: #dc2626;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 8 14;" +
                "-fx-cursor: hand;"
        );

        deleteButton.setOnAction(e -> {

            try {

                notificationController
                        .deleteNotification(
                                notification
                                        .getNotificationId()
                        );

                // Refresh page
                refreshPage();

            } catch (Exception ex) {

                ex.printStackTrace();

                showError(
                        "Unable to delete notification.",
                        ex.getMessage()
                );
            }
        });

        buttonRow.getChildren().addAll(
                readButton,
                deleteButton
        );

        // =====================================================
        // ADD EVERYTHING
        // =====================================================

        box.getChildren().addAll(
                topRow,
                bottomRow,
                buttonRow
        );

        return box;
    }

    // =========================================================
    // REFRESH PAGE
    // =========================================================

    private void refreshPage() {

        stage.setScene(
                new Notifications(stage)
                        .getScene()
        );
    }

    // =========================================================
    // EMPTY NOTIFICATION BOX
    // =========================================================

    private VBox createEmptyNotificationBox() {

        VBox box =
                new VBox(10);

        box.setAlignment(
                Pos.CENTER
        );

        box.setPadding(
                new Insets(35)
        );

        box.setMaxWidth(
                Double.MAX_VALUE
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #dbeafe;" +
                "-fx-border-radius: 14;"
        );

        Label icon =
                new Label(
                        "🔔"
                );

        icon.setStyle(
                "-fx-font-size: 38px;"
        );

        Label title =
                new Label(
                        "No notifications yet"
                );

        title.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1e3a8a;"
        );

        Label description =
                new Label(
                        "When something important happens in your " +
                        "HealthSphere account, it will appear here."
                );

        description.setWrapText(
                true
        );

        description.setAlignment(
                Pos.CENTER
        );

        description.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #64748b;"
        );

        box.getChildren().addAll(
                icon,
                title,
                description
        );

        return box;
    }

    // =========================================================
    // ERROR BOX
    // =========================================================

    private VBox createErrorNotificationBox(
            String errorMessage) {

        VBox box =
                new VBox(10);

        box.setAlignment(
                Pos.CENTER
        );

        box.setPadding(
                new Insets(25)
        );

        box.setMaxWidth(
                Double.MAX_VALUE
        );

        box.setStyle(
                "-fx-background-color: #fef2f2;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #fecaca;" +
                "-fx-border-radius: 14;"
        );

        Label title =
                new Label(
                        "Unable to load notifications"
                );

        title.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #b91c1c;"
        );

        Label message =
                new Label(
                        "Please check your Firebase connection " +
                        "and try again."
                );

        message.setWrapText(
                true
        );

        message.setStyle(
                "-fx-text-fill: #7f1d1d;" +
                "-fx-font-size: 13px;"
        );

        box.getChildren().addAll(
                title,
                message
        );

        return box;
    }

    // =========================================================
    // NOTIFICATION IMAGE
    // =========================================================

    private String getNotificationImage(
            String type) {

        if (type == null) {

            return "/images/notifications/notification2.jpg";
        }

        switch (
                type.trim().toUpperCase()
        ) {

            case "APPOINTMENT":

                return "/images/notifications/notification2.jpg";

            case "MEDICAL_RECORD":

                return "/images/notifications/notification3.jpg";

            case "HEALTH_PASSPORT":

                return "/images/notifications/notification4.jpg";

            default:

                return "/images/notifications/notification2.jpg";
        }
    }

    // =========================================================
    // FORMAT TYPE
    // =========================================================

    private String formatType(
            String type) {

        if (type == null ||
                type.isBlank()) {

            return "GENERAL";
        }

        switch (
                type.trim().toUpperCase()
        ) {

            case "APPOINTMENT":

                return "APPOINTMENT";

            case "MEDICAL_RECORD":

                return "MEDICAL RECORD";

            case "HEALTH_PASSPORT":

                return "HEALTH PASSPORT";

            default:

                return type.toUpperCase();
        }
    }

    // =========================================================
    // FORMAT DATE/TIME
    // =========================================================

    private String formatDateTime(
            String createdAt) {

        if (createdAt == null ||
                createdAt.isBlank()) {

            return "Recently";
        }

        return createdAt;
    }

    // =========================================================
    // SAFE TEXT
    // =========================================================

    private String safeText(
            String value,
            String defaultValue) {

        if (value == null ||
                value.isBlank()) {

            return defaultValue;
        }

        return value;
    }

    // =========================================================
    // REVIEW CARD
    // =========================================================

    private VBox createReviewCard() {

        VBox reviewCard =
                PatientUI.coloredCard(
                        "⭐  Review HealthSphere",
                        "#f3e8ff"
                );

        reviewCard.setSpacing(
                14
        );

        reviewCard.setMaxWidth(
                Double.MAX_VALUE
        );

        // =====================================================
        // DESCRIPTION
        // =====================================================

        Label description =
                new Label(
                        "We would love to hear about your experience with HealthSphere."
                );

        description.setWrapText(
                true
        );

        description.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #475569;"
        );

        // =====================================================
        // RATING TITLE
        // =====================================================

        Label ratingTitle =
                new Label(
                        "How would you rate the application?"
                );

        ratingTitle.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #4c1d95;"
        );

        // =====================================================
        // STAR RATING
        // =====================================================

        HBox stars =
                new HBox(8);

        stars.setAlignment(
                Pos.CENTER_LEFT
        );

        Button[] starButtons =
                new Button[5];

        final int[] selectedRating =
                {0};

        Label ratingText =
                new Label(
                        "Select a rating"
                );

        ratingText.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 13px;"
        );

        // -----------------------------------------------------
        // CREATE STARS
        // -----------------------------------------------------

        for (int i = 0; i < 5; i++) {

            final int rating =
                    i + 1;

            Button star =
                    new Button(
                            "★"
                    );

            starButtons[i] =
                    star;

            star.setPrefWidth(
                    48
            );

            star.setPrefHeight(
                    48
            );

            star.setMinWidth(
                    48
            );

            star.setMinHeight(
                    48
            );

            star.setFocusTraversable(
                    false
            );

            setStarStyle(
                    star,
                    false
            );

            star.setOnAction(e -> {

                selectedRating[0] =
                        rating;

                for (int j = 0; j < 5; j++) {

                    setStarStyle(
                            starButtons[j],
                            j < rating
                    );
                }

                switch (rating) {

                    case 1:

                        ratingText.setText(
                                "Poor"
                        );

                        break;

                    case 2:

                        ratingText.setText(
                                "Fair"
                        );

                        break;

                    case 3:

                        ratingText.setText(
                                "Good"
                        );

                        break;

                    case 4:

                        ratingText.setText(
                                "Very Good"
                        );

                        break;

                    case 5:

                        ratingText.setText(
                                "Excellent"
                        );

                        break;

                    default:

                        ratingText.setText(
                                "Select a rating"
                        );
                }
            });

            stars.getChildren().add(
                    star
            );
        }

        // =====================================================
        // REVIEW TEXT
        // =====================================================

        TextArea reviewText =
                new TextArea();

        reviewText.setPromptText(
                "Tell us what you think about HealthSphere..."
        );

        reviewText.setWrapText(
                true
        );

        reviewText.setPrefRowCount(
                4
        );

        reviewText.setMaxWidth(
                Double.MAX_VALUE
        );

        reviewText.setStyle(
                "-fx-background-color: #ffffff;" +
                "-fx-border-color: #ddd6fe;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10;" +
                "-fx-font-size: 14px;"
        );

        // =====================================================
        // SUBMIT BUTTON
        // =====================================================

        Button submit =
                new Button(
                        "Submit Review"
                );

        submit.setStyle(
                "-fx-background-color: #7c3aed;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10 22;" +
                "-fx-cursor: hand;"
        );

        submit.setOnAction(e -> {

            // -------------------------------------------------
            // CHECK RATING
            // -------------------------------------------------

            if (selectedRating[0] == 0) {

                showWarning(
                        "Please select a star rating before submitting."
                );

                return;
            }

            // -------------------------------------------------
            // CHECK REVIEW TEXT
            // -------------------------------------------------

            String review =
                    reviewText.getText() == null
                            ? ""
                            : reviewText.getText().trim();

            if (review.isBlank()) {

                showWarning(
                        "Please write a short review before submitting."
                );

                return;
            }

            // -------------------------------------------------
            // REVIEW SUBMITTED
            // -------------------------------------------------

            showSuccess(
                    "Thank you for reviewing HealthSphere!"
            );

            // -------------------------------------------------
            // CLEAR FORM
            // -------------------------------------------------

            reviewText.clear();

            selectedRating[0] =
                    0;

            ratingText.setText(
                    "Select a rating"
            );

            // -------------------------------------------------
            // RESET STARS
            // -------------------------------------------------

            for (Button star :
                    starButtons) {

                setStarStyle(
                        star,
                        false
                );
            }
        });

        // =====================================================
        // ADD EVERYTHING
        // =====================================================

        reviewCard.getChildren().addAll(
                description,
                ratingTitle,
                stars,
                ratingText,
                reviewText,
                submit
        );

        return reviewCard;
    }

    // =========================================================
    // STAR STYLE
    // =========================================================

    private void setStarStyle(
            Button star,
            boolean selected) {

        if (selected) {

            star.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: #f59e0b;" +
                    "-fx-font-size: 32px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 0;" +
                    "-fx-cursor: hand;"
            );

        } else {

            star.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: #cbd5e1;" +
                    "-fx-font-size: 32px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 0;" +
                    "-fx-cursor: hand;"
            );
        }
    }

    // =========================================================
    // WARNING
    // =========================================================

    private void showWarning(
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.WARNING
                );

        alert.setTitle(
                "HealthSphere"
        );

        alert.setHeaderText(
                "Review Required"
        );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }

    // =========================================================
    // SUCCESS
    // =========================================================

    private void showSuccess(
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(
                "HealthSphere"
        );

        alert.setHeaderText(
                "Review Submitted"
        );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }

    // =========================================================
    // ERROR
    // =========================================================

    private void showError(
            String header,
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle(
                "HealthSphere"
        );

        alert.setHeaderText(
                header
        );

        alert.setContentText(
                message == null ||
                        message.isBlank()
                        ? "Please try again."
                        : message
        );

        alert.showAndWait();
    }

    // =========================================================
    // SAFE IMAGE LOADER
    // =========================================================

    private ImageView createImage(
            String path,
            double width,
            double height) {

        ImageView view =
                new ImageView();

        var resource =
                getClass().getResource(
                        path
                );

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

        view.setPreserveRatio(
                true
        );

        view.setSmooth(
                true
        );

        return view;
    }
}