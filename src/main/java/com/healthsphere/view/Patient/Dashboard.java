package com.healthsphere.view.Patient;

import com.healthsphere.controller.patient.PatientController;
import com.healthsphere.model.PatientProfile;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Dashboard {

    private final Stage stage;
    private final PatientController patientController;

    public Dashboard(Stage stage) {

        this.stage = stage;

        this.patientController =
                new PatientController();
    }

    // =========================================================
    // SCENE
    // =========================================================

    public Scene getScene() {

        /*
         * IMPORTANT:
         *
         * Dashboard creates ONLY its content.
         *
         * PatientUI is responsible for:
         * - Sidebar
         * - Header
         * - ScrollPane
         * - Full width
         * - Full height
         * - Navigation
         */

        VBox content =
                new VBox(22);

        content.setPadding(
                new Insets(28)
        );

        content.setFillWidth(true);

        content.setMinWidth(0);

        content.setMaxWidth(
                Double.MAX_VALUE
        );

        content.setStyle(
                "-fx-background-color: #f1f5f9;"
        );

        // =====================================================
        // PATIENT NAME
        // =====================================================

        String patientName =
                "Patient";

        try {

            PatientProfile profile =
                    patientController
                            .getCurrentPatientProfile();

            if (profile != null
                    && profile.getFirstName() != null
                    && !profile.getFirstName().isBlank()) {

                patientName =
                        profile.getFirstName()
                                .trim();
            }

        } catch (Exception e) {

            System.err.println(
                    "Unable to load patient profile: "
                            + e.getMessage()
            );
        }

        // =====================================================
        // HEADING
        // =====================================================

        Label title =
                new Label(
                        "Good evening, "
                                + patientName
                );

        title.setWrapText(true);

        title.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label subtitle =
                new Label(
                        "Here is your health overview and today's important updates."
                );

        subtitle.setWrapText(true);

        subtitle.setMaxWidth(
                Double.MAX_VALUE
        );

        subtitle.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #64748b;"
        );

        VBox heading =
                new VBox(
                        5,
                        title,
                        subtitle
                );

        heading.setMaxWidth(
                Double.MAX_VALUE
        );

        // =====================================================
        // IMAGE CARDS
        // =====================================================

        HBox imageRow =
                new HBox(15);

        imageRow.setFillHeight(true);

        imageRow.setMaxWidth(
                Double.MAX_VALUE
        );

        VBox card1 =
                imageCard(
                        createDashboardImage(
                                "/images/dashboard/dashboard1.jpg"
                        ),
                        "Your Health"
                );

        VBox card2 =
                imageCard(
                        createDashboardImage(
                                "/images/dashboard/dashboard2.jpg"
                        ),
                        "Healthy Lifestyle"
                );

        VBox card3 =
                imageCard(
                        createDashboardImage(
                                "/images/dashboard/dashboard3.jpg"
                        ),
                        "Medical Care"
                );

        VBox card4 =
                imageCard(
                        createDashboardImage(
                                "/images/dashboard/dashboard4.jpg"
                        ),
                        "Wellness"
                );

        HBox.setHgrow(
                card1,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                card2,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                card3,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                card4,
                Priority.ALWAYS
        );

        imageRow.getChildren().addAll(
                card1,
                card2,
                card3,
                card4
        );

        // =====================================================
        // STAT CARDS
        // =====================================================

        HBox stats =
                new HBox(18);

        stats.setFillHeight(true);

        stats.setMaxWidth(
                Double.MAX_VALUE
        );

        VBox heartRate =
                statCard(
                        "♥",
                        "Heart Rate",
                        "72 BPM",
                        "Normal"
                );

        VBox bloodPressure =
                statCard(
                        "BP",
                        "Blood Pressure",
                        "118 / 76",
                        "Healthy"
                );

        VBox appointmentStat =
                statCard(
                        "◷",
                        "Next Appointment",
                        "Tomorrow",
                        "10:00 AM"
                );

        VBox aiInsightsStat =
                statCard(
                        "✦",
                        "AI Insights",
                        "3",
                        "New"
                );

        HBox.setHgrow(
                heartRate,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                bloodPressure,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                appointmentStat,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                aiInsightsStat,
                Priority.ALWAYS
        );

        stats.getChildren().addAll(
                heartRate,
                bloodPressure,
                appointmentStat,
                aiInsightsStat
        );

        // =====================================================
        // MIDDLE SECTION
        // =====================================================

        HBox middle =
                new HBox(18);

        middle.setFillHeight(true);

        middle.setMaxWidth(
                Double.MAX_VALUE
        );

        VBox appointment =
                card(
                        "Upcoming Appointment"
                );

        appointment.setMinWidth(0);

        appointment.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox.setHgrow(
                appointment,
                Priority.ALWAYS
        );

        Label doctor =
                new Label(
                        "Dr. Sarah Jenkins"
                );

        doctor.setWrapText(true);

        doctor.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label specialty =
                muted(
                        "Cardiology"
                );

        Label time =
                new Label(
                        "Tomorrow • 10:00 AM"
                );

        time.setWrapText(true);

        time.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-text-fill: #334155;"
        );

        Button appointmentButton =
                button(
                        "View Appointments",
                        this::showAppointments
                );

        appointment.getChildren().addAll(
                doctor,
                specialty,
                new Separator(),
                time,
                appointmentButton
        );

        // =====================================================
        // AI INSIGHTS
        // =====================================================

        VBox insights =
                card(
                        "AI Health Insights"
                );

        insights.setMinWidth(0);

        insights.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox.setHgrow(
                insights,
                Priority.ALWAYS
        );

        insights.getChildren().addAll(

                insight(
                        "Elevated Blood Pressure Trend",
                        "A slight upward trend has been detected in your recent readings."
                ),

                insight(
                        "Medication Review",
                        "Your medication list has been checked for possible interactions."
                ),

                insight(
                        "Health Passport",
                        "Your latest medical records are ready for review."
                )
        );

        middle.getChildren().addAll(
                appointment,
                insights
        );

        // =====================================================
        // QUICK ACTIONS
        // =====================================================

        VBox quickActions =
                card(
                        "Quick Actions"
                );

        quickActions.setMinWidth(0);

        quickActions.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox actions =
                new HBox(12);

        actions.setAlignment(
                Pos.CENTER_LEFT
        );

        actions.setFillHeight(true);

        actions.setMaxWidth(
                Double.MAX_VALUE
        );

        Button hospitalsButton =
                button(
                        "Find Hospitals",
                        this::showSearchHospitals
                );

        Button passportButton =
                button(
                        "Health Passport",
                        this::showHealthPassport
                );

        Button recordsButton =
                button(
                        "Medical Records",
                        this::showMedicalRecords
                );

        Button assistantButton =
                button(
                        "AI Assistant",
                        this::showAIHealthAssistant
                );

        Button emergencyButton =
                button(
                        "Emergency",
                        this::showEmergencyAssistance
                );

        actions.getChildren().addAll(
                hospitalsButton,
                passportButton,
                recordsButton,
                assistantButton,
                emergencyButton
        );

        quickActions.getChildren().add(
                actions
        );

        // =====================================================
        // ADD EVERYTHING
        // =====================================================

        content.getChildren().addAll(
                heading,
                imageRow,
                stats,
                middle,
                quickActions
        );

        // =====================================================
        // COMMON PATIENT UI
        // =====================================================

        /*
         * DO NOT create a ScrollPane here.
         *
         * PatientUI creates the single ScrollPane.
         *
         * This makes Dashboard behave exactly like:
         *
         * Search Hospitals
         * Appointments
         * Health Passport
         * Medical Records
         * AI Assistant
         * Emergency
         * Notifications
         * Profile & Settings
         */

        return PatientUI.createScene(
                stage,
                "Dashboard",
                "Dashboard",
                "Here is your health overview and today's important updates.",
                content
        );
    }

    // =========================================================
    // DASHBOARD IMAGE
    // =========================================================

    private ImageView createDashboardImage(
            String imagePath
    ) {

        Image image = null;

        try {

            if (getClass().getResource(
                    imagePath
            ) != null) {

                image =
                        new Image(
                                getClass()
                                        .getResourceAsStream(
                                                imagePath
                                        )
                        );
            }

        } catch (Exception e) {

            System.err.println(
                    "Unable to load dashboard image: "
                            + imagePath
            );
        }

        ImageView imageView =
                new ImageView();

        if (image != null) {

            imageView.setImage(
                    image
            );
        }

        imageView.setFitWidth(200);

        imageView.setFitHeight(150);

        imageView.setPreserveRatio(false);

        return imageView;
    }

    // =========================================================
    // IMAGE CARD
    // =========================================================

    private VBox imageCard(
            ImageView imageView,
            String text
    ) {

        VBox box =
                new VBox();

        box.setMinWidth(0);

        box.setPrefWidth(0);

        box.setMaxWidth(
                Double.MAX_VALUE
        );

        box.setMinHeight(180);

        box.setPrefHeight(180);

        box.setMaxHeight(180);

        box.setAlignment(
                Pos.BOTTOM_LEFT
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #dbeafe;" +
                "-fx-border-radius: 14;"
        );

        if (imageView.getImage() != null) {

            imageView.fitWidthProperty()
                    .bind(
                            box.widthProperty()
                    );

            imageView.fitHeightProperty()
                    .bind(
                            box.heightProperty()
                                    .subtract(40)
                    );

            box.getChildren().add(
                    imageView
            );
        }

        Label label =
                new Label(text);

        label.setPadding(
                new Insets(
                        8,
                        12,
                        8,
                        12
                )
        );

        label.setMaxWidth(
                Double.MAX_VALUE
        );

        label.setStyle(
                "-fx-background-color: rgba(15,23,42,0.82);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
        );

        box.getChildren().add(
                label
        );

        return box;
    }

    // =========================================================
    // STAT CARD
    // =========================================================

    private VBox statCard(
            String icon,
            String title,
            String value,
            String status
    ) {

        VBox box =
                card("");

        box.setMinWidth(0);

        box.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox.setHgrow(
                box,
                Priority.ALWAYS
        );

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-font-size: 24px;" +
                "-fx-text-fill: #2563eb;"
        );

        Label titleLabel =
                muted(title);

        titleLabel.setWrapText(true);

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label statusLabel =
                green(status);

        box.getChildren().addAll(
                iconLabel,
                titleLabel,
                valueLabel,
                statusLabel
        );

        return box;
    }

    // =========================================================
    // CARD
    // =========================================================

    private VBox card(
            String title
    ) {

        VBox box =
                new VBox(12);

        box.setPadding(
                new Insets(20)
        );

        box.setMinWidth(0);

        box.setMaxWidth(
                Double.MAX_VALUE
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #dbeafe;" +
                "-fx-border-radius: 14;" +
                "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.08), 10, 0, 0, 3);"
        );

        if (title != null &&
                !title.isEmpty()) {

            Label label =
                    new Label(title);

            label.setWrapText(true);

            label.setStyle(
                    "-fx-font-size: 18px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: #0f172a;"
            );

            box.getChildren().add(
                    label
            );
        }

        return box;
    }

    // =========================================================
    // INSIGHT
    // =========================================================

    private VBox insight(
            String title,
            String description
    ) {

        VBox box =
                new VBox(5);

        box.setMinWidth(0);

        box.setMaxWidth(
                Double.MAX_VALUE
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setWrapText(true);

        titleLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setWrapText(true);

        descriptionLabel.setMaxWidth(
                Double.MAX_VALUE
        );

        descriptionLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        box.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );

        return box;
    }

    // =========================================================
    // BUTTON
    // =========================================================

    private Button button(
            String text,
            Runnable action
    ) {

        Button button =
                new Button(text);

        button.setPrefHeight(42);

        button.setPadding(
                new Insets(
                        8,
                        18,
                        8,
                        18
                )
        );

        button.setStyle(
                "-fx-background-color: #2563eb;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
        );

        button.setOnAction(
                e -> {

                    if (action != null) {
                        action.run();
                    }
                }
        );

        return button;
    }

    // =========================================================
    // MUTED
    // =========================================================

    private Label muted(
            String text
    ) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-text-fill: #64748b;"
        );

        return label;
    }

    // =========================================================
    // GREEN
    // =========================================================

    private Label green(
            String text
    ) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-text-fill: #16a34a;" +
                "-fx-font-weight: bold;"
        );

        return label;
    }

    // =========================================================
    // NAVIGATION
    // =========================================================

    private void showAppointments() {

        stage.setScene(
                new Appointments(stage)
                        .getScene()
        );

        stage.show();
    }

    private void showSearchHospitals() {

        stage.setScene(
                new SearchHospitals(stage)
                        .getScene()
        );

        stage.show();
    }

    private void showHealthPassport() {

        stage.setScene(
                new HealthPassport(stage)
                        .getScene()
        );

        stage.show();
    }

    private void showMedicalRecords() {

        stage.setScene(
                new MedicalRecords(stage)
                        .getScene()
        );

        stage.show();
    }

    private void showAIHealthAssistant() {

        stage.setScene(
                new AiHealthAssistant(stage)
                        .getScene()
        );

        stage.show();
    }

    private void showEmergencyAssistance() {

        stage.setScene(
                new EmergencyAssistance(stage)
                        .getScene()
        );

        stage.show();
    }
}