
package com.healthsphere.view.Patient;

import com.healthsphere.controller.patient.ReviewController;
import com.healthsphere.model.Appointment;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GiveReview {

    private final Stage stage;
    private final Appointment appointment;
    private final ReviewController reviewController;

    public GiveReview(
            Stage stage,
            Appointment appointment) {

        this.stage = stage;
        this.appointment = appointment;

        this.reviewController =
                new ReviewController();
    }

    // =========================================================
    // MAIN SCENE
    // =========================================================

    public Scene getScene() {

        VBox content =
                new VBox(20);

        content.setPadding(
                new Insets(10)
        );

        content.setMinWidth(0);

        content.setMaxWidth(
                Double.MAX_VALUE
        );

        // =====================================================
        // INTRODUCTION
        // =====================================================

        VBox intro =
                new VBox(8);

        intro.setPadding(
                new Insets(20)
        );

        intro.setStyle(
                "-fx-background-color: #eff6ff;" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 15;"
        );

        Label introTitle =
                new Label(
                        "Share Your Experience"
                );

        introTitle.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label introText =
                new Label(
                        "Your feedback helps doctors and hospitals "
                                + "improve their services."
                );

        introText.setWrapText(true);

        introText.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #475569;"
        );

        intro.getChildren().addAll(
                introTitle,
                introText
        );

        content.getChildren().add(
                intro
        );

        // =====================================================
        // APPOINTMENT INFORMATION
        // =====================================================

        VBox appointmentInfo =
                PatientUI.card(
                        "Appointment Information"
                );

        appointmentInfo.setMinWidth(0);

        appointmentInfo.setMaxWidth(
                Double.MAX_VALUE
        );

        String appointmentType =
                safe(
                        appointment.getBookingType(),
                        "Appointment"
                );

        Label typeLabel =
                detailLabel(
                        "Appointment Type",
                        appointmentType
                );

        Label dateLabel =
                detailLabel(
                        "Date",
                        formatDate(
                                appointment.getAppointmentDate()
                        )
                );

        appointmentInfo.getChildren().addAll(
                typeLabel,
                dateLabel
        );

        if (hasText(
                appointment.getDoctorName())) {

            appointmentInfo.getChildren().add(
                    detailLabel(
                            "Doctor",
                            appointment.getDoctorName()
                    )
            );
        }

        if (hasText(
                appointment.getHospitalName())) {

            appointmentInfo.getChildren().add(
                    detailLabel(
                            "Hospital",
                            appointment.getHospitalName()
                    )
            );
        }

        content.getChildren().add(
                appointmentInfo
        );

        // =====================================================
        // DOCTOR REVIEW
        // =====================================================

        if (hasText(
                appointment.getDoctorUid())
                &&
                hasText(
                        appointment.getDoctorName())) {

            content.getChildren().add(
                    createDoctorReviewCard()
            );
        }

        // =====================================================
        // HOSPITAL REVIEW
        // =====================================================

        if (hasText(
                appointment.getHospitalId())
                &&
                hasText(
                        appointment.getHospitalName())) {

            content.getChildren().add(
                    createHospitalReviewCard()
            );
        }

        // =====================================================
        // NO REVIEW TARGET
        // =====================================================

        if (!hasText(
                appointment.getDoctorUid())
                &&
                !hasText(
                        appointment.getHospitalId())) {

            Label noTarget =
                    new Label(
                            "Review information is not available "
                                    + "for this appointment."
                    );

            noTarget.setWrapText(true);

            noTarget.setStyle(
                    "-fx-text-fill: #64748b;" +
                    "-fx-font-size: 14px;"
            );

            content.getChildren().add(
                    noTarget
            );
        }

        // =====================================================
        // BACK BUTTON
        // =====================================================

        Button back =
                PatientUI.secondaryButton(
                        "Back to Appointments",
                        this::showAppointments
                );

        content.getChildren().add(
                back
        );

        return PatientUI.createScene(
                stage,
                "Appointments",
                "Give Review",
                "Rate your doctor and hospital experience.",
                content
        );
    }

    // =========================================================
    // DOCTOR REVIEW CARD
    // =========================================================

    private VBox createDoctorReviewCard() {

        VBox card =
                PatientUI.card(
                        "Review Doctor"
                );

        card.setMinWidth(0);

        card.setMaxWidth(
                Double.MAX_VALUE
        );

        Label doctorName =
                new Label(
                        safe(
                                appointment.getDoctorName(),
                                "Doctor"
                        )
                );

        doctorName.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        card.getChildren().add(
                doctorName
        );

        // -----------------------------------------------------
        // CHECK EXISTING REVIEW
        // -----------------------------------------------------

        boolean alreadyReviewed =
                false;

        try {

            alreadyReviewed =
                    reviewController
                            .hasCurrentPatientReviewed(

                                    appointment
                                            .getAppointmentId(),

                                    "DOCTOR",

                                    appointment
                                            .getDoctorUid()
                            );

        } catch (Exception e) {

            System.err.println(
                    "Unable to check doctor review: "
                            + e.getMessage()
            );
        }

        if (alreadyReviewed) {

            Label reviewed =
                    new Label(
                            "✓ You have already reviewed this doctor."
                    );

            reviewed.setStyle(
                    "-fx-text-fill: #16a34a;" +
                    "-fx-font-weight: bold;"
            );

            card.getChildren().add(
                    reviewed
            );

            return card;
        }

        // -----------------------------------------------------
        // RATING
        // -----------------------------------------------------

        Label ratingLabel =
                new Label(
                        "Select Rating"
                );

        ratingLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #334155;"
        );

        final int[] rating =
                {0};

        HBox stars =
                new HBox(8);

        stars.setAlignment(
                Pos.CENTER_LEFT
        );

        Button[] starButtons =
                new Button[5];

        for (int i = 0;
                i < 5;
                i++) {

            final int selectedRating =
                    i + 1;

            Button star =
                    new Button("☆");

            star.setStyle(
                    "-fx-font-size: 28px;" +
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: #f59e0b;" +
                    "-fx-cursor: hand;"
            );

            star.setOnAction(e -> {

                rating[0] =
                        selectedRating;

                updateStars(
                        starButtons,
                        selectedRating
                );
            });

            starButtons[i] =
                    star;

            stars.getChildren().add(
                    star
            );
        }

        // -----------------------------------------------------
        // COMMENT
        // -----------------------------------------------------

        Label commentLabel =
                new Label(
                        "Your Review"
                );

        commentLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #334155;"
        );

        TextArea comment =
                new TextArea();

        comment.setPromptText(
                "Write about your experience with this doctor..."
        );

        comment.setWrapText(true);

        comment.setPrefRowCount(4);

        comment.setMaxWidth(
                Double.MAX_VALUE
        );

        // -----------------------------------------------------
        // SUBMIT
        // -----------------------------------------------------

        Button submit =
                PatientUI.button(
                        "Submit Doctor Review",
                        () -> {

                            try {

                                reviewController
                                        .createReview(

                                                appointment
                                                        .getAppointmentId(),

                                                "DOCTOR",

                                                appointment
                                                        .getDoctorUid(),

                                                appointment
                                                        .getDoctorName(),

                                                rating[0],

                                                comment
                                                        .getText()
                                        );

                                showSuccess(
                                        "Doctor review submitted successfully."
                                );

                                showAppointments();

                            } catch (Exception e) {

                                showError(
                                        e.getMessage()
                                );
                            }
                        }
                );

        card.getChildren().addAll(
                ratingLabel,
                stars,
                commentLabel,
                comment,
                submit
        );

        return card;
    }

    // =========================================================
    // HOSPITAL REVIEW CARD
    // =========================================================

    private VBox createHospitalReviewCard() {

        VBox card =
                PatientUI.card(
                        "Review Hospital"
                );

        card.setMinWidth(0);

        card.setMaxWidth(
                Double.MAX_VALUE
        );

        Label hospitalName =
                new Label(
                        safe(
                                appointment.getHospitalName(),
                                "Hospital"
                        )
                );

        hospitalName.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        card.getChildren().add(
                hospitalName
        );

        // -----------------------------------------------------
        // CHECK EXISTING REVIEW
        // -----------------------------------------------------

        boolean alreadyReviewed =
                false;

        try {

            alreadyReviewed =
                    reviewController
                            .hasCurrentPatientReviewed(

                                    appointment
                                            .getAppointmentId(),

                                    "HOSPITAL",

                                    appointment
                                            .getHospitalId()
                            );

        } catch (Exception e) {

            System.err.println(
                    "Unable to check hospital review: "
                            + e.getMessage()
            );
        }

        if (alreadyReviewed) {

            Label reviewed =
                    new Label(
                            "✓ You have already reviewed this hospital."
                    );

            reviewed.setStyle(
                    "-fx-text-fill: #16a34a;" +
                    "-fx-font-weight: bold;"
            );

            card.getChildren().add(
                    reviewed
            );

            return card;
        }

        // -----------------------------------------------------
        // RATING
        // -----------------------------------------------------

        Label ratingLabel =
                new Label(
                        "Select Rating"
                );

        ratingLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #334155;"
        );

        final int[] rating =
                {0};

        HBox stars =
                new HBox(8);

        stars.setAlignment(
                Pos.CENTER_LEFT
        );

        Button[] starButtons =
                new Button[5];

        for (int i = 0;
                i < 5;
                i++) {

            final int selectedRating =
                    i + 1;

            Button star =
                    new Button("☆");

            star.setStyle(
                    "-fx-font-size: 28px;" +
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: #f59e0b;" +
                    "-fx-cursor: hand;"
            );

            star.setOnAction(e -> {

                rating[0] =
                        selectedRating;

                updateStars(
                        starButtons,
                        selectedRating
                );
            });

            starButtons[i] =
                    star;

            stars.getChildren().add(
                    star
            );
        }

        // -----------------------------------------------------
        // COMMENT
        // -----------------------------------------------------

        Label commentLabel =
                new Label(
                        "Your Review"
                );

        commentLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #334155;"
        );

        TextArea comment =
                new TextArea();

        comment.setPromptText(
                "Write about your experience with this hospital..."
        );

        comment.setWrapText(true);

        comment.setPrefRowCount(4);

        comment.setMaxWidth(
                Double.MAX_VALUE
        );

        // -----------------------------------------------------
        // SUBMIT
        // -----------------------------------------------------

        Button submit =
                PatientUI.button(
                        "Submit Hospital Review",
                        () -> {

                            try {

                                reviewController
                                        .createReview(

                                                appointment
                                                        .getAppointmentId(),

                                                "HOSPITAL",

                                                appointment
                                                        .getHospitalId(),

                                                appointment
                                                        .getHospitalName(),

                                                rating[0],

                                                comment
                                                        .getText()
                                        );

                                showSuccess(
                                        "Hospital review submitted successfully."
                                );

                                showAppointments();

                            } catch (Exception e) {

                                showError(
                                        e.getMessage()
                                );
                            }
                        }
                );

        card.getChildren().addAll(
                ratingLabel,
                stars,
                commentLabel,
                comment,
                submit
        );

        return card;
    }

    // =========================================================
    // UPDATE STARS
    // =========================================================

    private void updateStars(
            Button[] stars,
            int rating) {

        for (int i = 0;
                i < stars.length;
                i++) {

            if (i < rating) {

                stars[i].setText(
                        "★"
                );

            } else {

                stars[i].setText(
                        "☆"
                );
            }
        }
    }

    // =========================================================
    // DETAIL LABEL
    // =========================================================

    private Label detailLabel(
            String title,
            String value) {

        Label label =
                new Label(
                        title
                                + ": "
                                + safe(
                                        value,
                                        "Not available"
                                )
                );

        label.setWrapText(true);

        label.setMaxWidth(
                Double.MAX_VALUE
        );

        label.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #475569;"
        );

        return label;
    }

    // =========================================================
    // SAFE
    // =========================================================

    private String safe(
            String value,
            String fallback) {

        if (value == null ||
                value.isBlank()) {

            return fallback;
        }

        return value;
    }

    // =========================================================
    // HAS TEXT
    // =========================================================

    private boolean hasText(
            String value) {

        return value != null &&
                !value.isBlank();
    }

    // =========================================================
    // FORMAT DATE
    // =========================================================

    private String formatDate(
            String date) {

        if (!hasText(date)) {

            return "Not available";
        }

        return date;
    }

    // =========================================================
    // SUCCESS ALERT
    // =========================================================

    private void showSuccess(
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(
                "Review Submitted"
        );

        alert.setHeaderText(
                null
        );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }

    // =========================================================
    // ERROR ALERT
    // =========================================================

    private void showError(
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle(
                "Review Error"
        );

        alert.setHeaderText(
                "Unable to submit review"
        );

        alert.setContentText(
                safe(
                        message,
                        "Something went wrong."
                )
        );

        alert.showAndWait();
    }

    // =========================================================
    // BACK TO APPOINTMENTS
    // =========================================================

    private void showAppointments() {

        stage.setScene(
                new Appointments(stage)
                        .getScene()
        );

        stage.show();

        if (!stage.isMaximized()) {

            stage.setMaximized(true);
        }
    }
}