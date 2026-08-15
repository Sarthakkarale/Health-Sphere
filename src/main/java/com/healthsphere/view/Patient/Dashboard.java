package com.healthsphere.view.Patient;

import com.healthsphere.controller.patient.PatientController;
import com.healthsphere.model.PatientProfile;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Dashboard {

    private final Stage stage;
    private final PatientController patientController;

    public Dashboard(Stage stage) {

        this.stage = stage;
        this.patientController = new PatientController();
    }

    public Scene getScene() {

        BorderPane root = new BorderPane();

        root.setLeft(createSidebar());
        root.setTop(createHeader());

        VBox content = new VBox(22);
        content.setPadding(new Insets(28));
        content.setStyle("-fx-background-color: #f1f5f9;");

        // =========================================================
        // LOAD CURRENT PATIENT
        // =========================================================

        String patientName = "Patient";

        try {

            PatientProfile profile =
                    patientController.getCurrentPatientProfile();

            if (profile != null &&
                    profile.getFirstName() != null &&
                    !profile.getFirstName().isBlank()) {

                patientName =
                        profile.getFirstName().trim();
            }

        } catch (Exception e) {

            System.err.println(
                    "Unable to load patient profile: "
                            + e.getMessage()
            );
        }

        // =========================================================
        // HEADING
        // =========================================================

        Label title =
                new Label(
                        "Good evening, " + patientName
                );

        title.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label subtitle =
                new Label(
                        "Here is your health overview and today's important updates."
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

        // =========================================================
        // DASHBOARD IMAGES
        // =========================================================

        HBox imageRow =
                new HBox(15);

        imageRow.setPrefHeight(180);

        imageRow.getChildren().addAll(

                imageCard(
                        "/images/dashboard/dashboard1.jpg",
                        "Your Health"
                ),

                imageCard(
                        "/images/dashboard/dashboard2.jpg",
                        "Healthy Lifestyle"
                ),

                imageCard(
                        "/images/dashboard/dashboard3.jpg",
                        "Medical Care"
                ),

                imageCard(
                        "/images/dashboard/dashboard4.jpg",
                        "Wellness"
                )
        );

        // =========================================================
        // STAT CARDS
        // =========================================================

        HBox stats =
                new HBox(18);

        stats.getChildren().addAll(

                statCard(
                        "♥",
                        "Heart Rate",
                        "72 BPM",
                        "Normal"
                ),

                statCard(
                        "BP",
                        "Blood Pressure",
                        "118 / 76",
                        "Healthy"
                ),

                statCard(
                        "◷",
                        "Next Appointment",
                        "Tomorrow",
                        "10:00 AM"
                ),

                statCard(
                        "✦",
                        "AI Insights",
                        "3",
                        "New"
                )
        );

        // =========================================================
        // UPCOMING APPOINTMENT
        // =========================================================

        HBox middle =
                new HBox(18);

        VBox appointment =
                card("Upcoming Appointment");

        Label doctor =
                new Label(
                        "Dr. Sarah Jenkins"
                );

        doctor.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label specialty =
                muted("Cardiology");

        Label time =
                new Label(
                        "Tomorrow • 10:00 AM"
                );

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

        // =========================================================
        // AI INSIGHTS
        // =========================================================

        VBox insights =
                card("AI Health Insights");

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

        HBox.setHgrow(
                appointment,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                insights,
                Priority.ALWAYS
        );

        middle.getChildren().addAll(
                appointment,
                insights
        );

        // =========================================================
        // QUICK ACTIONS
        // =========================================================

        VBox quickActions =
                card("Quick Actions");

        HBox actions =
                new HBox(12);

        actions.getChildren().addAll(

                button(
                        "Find Hospitals",
                        this::showSearchHospitals
                ),

                button(
                        "Health Passport",
                        this::showHealthPassport
                ),

                button(
                        "Medical Records",
                        this::showMedicalRecords
                ),

                button(
                        "AI Assistant",
                        this::showAIHealthAssistant
                ),

                button(
                        "Emergency",
                        this::showEmergencyAssistance
                )
        );

        quickActions
                .getChildren()
                .add(actions);

        // =========================================================
        // ADD CONTENT
        // =========================================================

        content.getChildren().addAll(

                heading,
                imageRow,
                stats,
                middle,
                quickActions
        );

        // =========================================================
        // SCROLL
        // =========================================================

        ScrollPane scroll =
                new ScrollPane(content);

        scroll.setFitToWidth(true);

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        root.setCenter(scroll);

        return new Scene(
                root,
                1440,
                900
        );
    }

    // =========================================================
    // IMAGE CARD
    // =========================================================

    private VBox imageCard(
            String imagePath,
            String text
    ) {

        VBox box =
                new VBox();

        box.setPrefWidth(260);
        box.setPrefHeight(180);

        box.setAlignment(
                Pos.BOTTOM_LEFT
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #dbeafe;" +
                "-fx-border-radius: 14;"
        );

        Image image = null;

        try {

            if (getClass().getResourceAsStream(imagePath) != null) {

                image =
                        new Image(
                                getClass()
                                        .getResourceAsStream(imagePath)
                        );
            }

        } catch (Exception e) {

            System.err.println(
                    "Unable to load dashboard image: "
                            + imagePath
            );
        }

        if (image != null) {

            ImageView imageView =
                    new ImageView(image);

            imageView.setFitWidth(260);
            imageView.setFitHeight(180);

            imageView.setPreserveRatio(false);

            box.getChildren()
                    .add(imageView);
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

        label.setStyle(
                "-fx-background-color: rgba(15,23,42,0.82);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;"
        );

        box.getChildren().add(label);

        return box;
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private VBox createSidebar() {

        VBox sidebar =
                new VBox(8);

        sidebar.setPrefWidth(255);

        sidebar.setPadding(
                new Insets(22)
        );

        sidebar.setStyle(
                "-fx-background-color: #0f172a;"
        );

        Label brand =
                new Label(
                        "✚  Health-Sphere"
                );

        brand.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 21px;" +
                "-fx-font-weight: bold;"
        );

        Label module =
                new Label(
                        "Patient Module"
                );

        module.setStyle(
                "-fx-text-fill: #94a3b8;"
        );

        Region spacer =
                new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        sidebar.getChildren().addAll(

                brand,
                module,
                new Separator(),

                nav(
                        "▦",
                        "Dashboard",
                        true,
                        this::showDashboard
                ),

                nav(
                        "⊞",
                        "Search Hospitals",
                        false,
                        this::showSearchHospitals
                ),

                nav(
                        "▣",
                        "Appointments",
                        false,
                        this::showAppointments
                ),

                nav(
                        "▧",
                        "Health Passport",
                        false,
                        this::showHealthPassport
                ),

                nav(
                        "▱",
                        "Medical Records",
                        false,
                        this::showMedicalRecords
                ),

                nav(
                        "♙",
                        "AI Health Assistant",
                        false,
                        this::showAIHealthAssistant
                ),

                nav(
                        "⌖",
                        "Emergency Assistance",
                        false,
                        this::showEmergencyAssistance
                ),

                spacer,

                nav(
                        "♧",
                        "Notifications",
                        false,
                        this::showNotifications
                ),

                nav(
                        "⚙",
                        "Profile & Settings",
                        false,
                        this::showProfileSettings
                )
        );

        return sidebar;
    }

    // =========================================================
    // HEADER
    // =========================================================

    private HBox createHeader() {

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_RIGHT
        );

        header.setPadding(
                new Insets(
                        16,
                        28,
                        16,
                        28
                )
        );

        header.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #e2e8f0;"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button notifications =
                button(
                        "Notifications",
                        this::showNotifications
                );

        // =====================================================
        // GET PATIENT NAME FOR HEADER
        // =====================================================

        String patientName =
                "Patient";

        try {

            PatientProfile profile =
                    patientController
                            .getCurrentPatientProfile();

            if (profile != null &&
                    profile.getFirstName() != null &&
                    !profile.getFirstName().isBlank()) {

                patientName =
                        profile.getFirstName().trim();
            }

        } catch (Exception e) {

            System.err.println(
                    "Unable to load patient name for header: "
                            + e.getMessage()
            );
        }

        Button profile =
                button(
                        patientName,
                        this::showProfileSettings
                );

        header.getChildren().addAll(

                spacer,
                notifications,
                profile
        );

        return header;
    }

    // =========================================================
    // NAVIGATION ITEM
    // =========================================================

    private HBox nav(
            String icon,
            String text,
            boolean selected,
            Runnable action
    ) {

        HBox item =
                new HBox(12);

        item.setAlignment(
                Pos.CENTER_LEFT
        );

        item.setPadding(
                new Insets(12)
        );

        String background =
                selected
                        ? "#2563eb"
                        : "transparent";

        item.setStyle(
                "-fx-background-color: "
                        + background
                        + ";" +
                "-fx-background-radius: 8;"
        );

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 17px;"
        );

        Label textLabel =
                new Label(text);

        textLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;"
        );

        item.getChildren().addAll(
                iconLabel,
                textLabel
        );

        item.setOnMouseClicked(
                e -> action.run()
        );

        return item;
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

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-font-size: 24px;" +
                "-fx-text-fill: #2563eb;"
        );

        Label titleLabel =
                muted(title);

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

        HBox.setHgrow(
                box,
                Priority.ALWAYS
        );

        return box;
    }

    // =========================================================
    // COMMON CARD
    // =========================================================

    private VBox card(String title) {

        VBox box =
                new VBox(12);

        box.setPadding(
                new Insets(20)
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #dbeafe;" +
                "-fx-border-radius: 14;" +
                "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.08), 10, 0, 0, 3);"
        );

        if (!title.isEmpty()) {

            Label label =
                    new Label(title);

            label.setStyle(
                    "-fx-font-size: 18px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: #0f172a;"
            );

            box.getChildren()
                    .add(label);
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

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setWrapText(true);

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
                e -> action.run()
        );

        return button;
    }

    // =========================================================
    // MUTED LABEL
    // =========================================================

    private Label muted(String text) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-text-fill: #64748b;"
        );

        return label;
    }

    // =========================================================
    // GREEN LABEL
    // =========================================================

    private Label green(String text) {

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

    private void showDashboard() {

        stage.setScene(
                new Dashboard(stage)
                        .getScene()
        );
    }

    private void showSearchHospitals() {

        stage.setScene(
                new SearchHospitals(stage)
                        .getScene()
        );
    }

    private void showAppointments() {

        stage.setScene(
                new Appointments(stage)
                        .getScene()
        );
    }

    private void showHealthPassport() {

        stage.setScene(
                new HealthPassport(stage)
                        .getScene()
        );
    }

    private void showMedicalRecords() {

        stage.setScene(
                new MedicalRecords(stage)
                        .getScene()
        );
    }

    private void showAIHealthAssistant() {

        stage.setScene(
                new AiHealthAssistant(stage)
                        .getScene()
        );
    }

    private void showEmergencyAssistance() {

        stage.setScene(
                new EmergencyAssistance(stage)
                        .getScene()
        );
    }

    private void showNotifications() {

        stage.setScene(
                new Notifications(stage)
                        .getScene()
        );
    }

    private void showProfileSettings() {

        stage.setScene(
                new ProfileSettings(stage)
                        .getScene()
        );
    }
}