package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class BookAppointment {

    private final PatientNavigator navigator;

    public BookAppointment(PatientNavigator navigator) {
        this.navigator = navigator;
    }

    public Scene getScene() {

        BorderPane root = new BorderPane();

        // =========================
        // SIDEBAR
        // =========================

        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(255);
        sidebar.setPadding(new Insets(22));

        sidebar.setStyle(
                "-fx-background-color: #0f172a;"
        );

        Label brand = new Label("✚  MediNexus AI");
        brand.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 21px;" +
                "-fx-font-weight: bold;"
        );

        Label module = new Label("Patient Module");
        module.setStyle(
                "-fx-text-fill: #94a3b8;"
        );

        sidebar.getChildren().addAll(
                brand,
                module,
                new Separator(),

                navButton("▦", "Dashboard",
                        navigator::showDashboard),

                navButton("⊞", "Search Hospitals",
                        navigator::showSearchHospitals),

                navButton("▣", "Appointments",
                        navigator::showAppointments),

                navButton("▧", "Health Passport",
                        navigator::showHealthPassport),

                navButton("▱", "Medical Records",
                        navigator::showMedicalRecords),

                navButton("♙", "AI Health Assistant",
                        navigator::showAIHealthAssistant),

                navButton("⌖", "Emergency Assistance",
                        navigator::showEmergencyAssistance)
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(
                spacer,

                navButton("♧", "Notifications",
                        navigator::showNotifications),

                navButton("⚙", "Profile & Settings",
                        navigator::showProfileSettings)
        );

        // =========================
        // HEADER
        // =========================

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_RIGHT);
        header.setPadding(
                new Insets(16, 28, 16, 28)
        );

        header.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #e2e8f0;"
        );

        Region headerSpacer = new Region();
        HBox.setHgrow(
                headerSpacer,
                Priority.ALWAYS
        );

        Button notifications =
                new Button("Notifications");

        notifications.setOnAction(
                e -> navigator.showNotifications()
        );

        Button profile =
                new Button("Sarah");

        profile.setOnAction(
                e -> navigator.showProfileSettings()
        );

        header.getChildren().addAll(
                headerSpacer,
                notifications,
                profile
        );

        // =========================
        // MAIN CONTENT
        // =========================

        VBox content = new VBox(22);
        content.setPadding(new Insets(28));

        Label title =
                new Label("Book an Appointment");

        title.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;"
        );

        Label subtitle =
                new Label(
                        "Choose a doctor, date and time for your appointment."
                );

        subtitle.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #64748b;"
        );

        VBox heading =
                new VBox(5, title, subtitle);

        // =========================
        // FORM CARD
        // =========================

        VBox formCard = new VBox(18);
        formCard.setPadding(new Insets(25));

        formCard.setMaxWidth(850);

        formCard.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #e2e8f0;" +
                "-fx-border-radius: 12;"
        );

        // Doctor

        Label doctorLabel =
                new Label("Select Doctor");

        doctorLabel.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;"
        );

        ComboBox<String> doctorBox =
                new ComboBox<>();

        doctorBox.getItems().addAll(
                "Dr. Sarah Jenkins - Cardiology",
                "Dr. Michael Anderson - Neurology",
                "Dr. Emily Wilson - General Medicine",
                "Dr. James Carter - Orthopedics",
                "Dr. Olivia Brown - Dermatology"
        );

        doctorBox.setPromptText(
                "Choose a doctor"
        );

        doctorBox.setMaxWidth(Double.MAX_VALUE);

        // Department

        Label departmentLabel =
                new Label("Department");

        departmentLabel.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;"
        );

        ComboBox<String> departmentBox =
                new ComboBox<>();

        departmentBox.getItems().addAll(
                "Cardiology",
                "Neurology",
                "General Medicine",
                "Orthopedics",
                "Dermatology"
        );

        departmentBox.setPromptText(
                "Choose department"
        );

        departmentBox.setMaxWidth(
                Double.MAX_VALUE
        );

        // Date

        Label dateLabel =
                new Label("Appointment Date");

        dateLabel.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;"
        );

        DatePicker datePicker =
                new DatePicker();

        datePicker.setPromptText(
                "Select appointment date"
        );

        datePicker.setMaxWidth(
                Double.MAX_VALUE
        );

        // Time

        Label timeLabel =
                new Label("Appointment Time");

        timeLabel.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;"
        );

        ComboBox<String> timeBox =
                new ComboBox<>();

        timeBox.getItems().addAll(
                "09:00 AM",
                "10:00 AM",
                "11:00 AM",
                "12:00 PM",
                "02:00 PM",
                "03:00 PM",
                "04:00 PM",
                "05:00 PM"
        );

        timeBox.setPromptText(
                "Select available time"
        );

        timeBox.setMaxWidth(
                Double.MAX_VALUE
        );

        // Reason

        Label reasonLabel =
                new Label("Reason for Visit");

        reasonLabel.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;"
        );

        TextArea reason =
                new TextArea();

        reason.setPromptText(
                "Briefly describe the reason for your visit..."
        );

        reason.setPrefRowCount(4);

        reason.setWrapText(true);

        // Buttons

        HBox buttons =
                new HBox(12);

        Button back =
                new Button("Back");

        back.setPrefHeight(42);

        back.setOnAction(
                e -> navigator.showAppointments()
        );

        Button confirm =
                new Button("Confirm Appointment");

        confirm.setPrefHeight(42);

        confirm.setStyle(
                "-fx-background-color: #2563eb;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 7;"
        );

        confirm.setOnAction(e -> {

            if (doctorBox.getValue() == null ||
                    departmentBox.getValue() == null ||
                    datePicker.getValue() == null ||
                    timeBox.getValue() == null) {

                showAlert(
                        "Missing Information",
                        "Please select doctor, department, date and time."
                );

                return;
            }

            showAlert(
                    "Appointment Confirmed",
                    "Your appointment has been successfully booked."
            );

            navigator.showAppointments();
        });

        buttons.getChildren().addAll(
                back,
                confirm
        );

        formCard.getChildren().addAll(
                doctorLabel,
                doctorBox,

                departmentLabel,
                departmentBox,

                dateLabel,
                datePicker,

                timeLabel,
                timeBox,

                reasonLabel,
                reason,

                new Separator(),

                buttons
        );

        content.getChildren().addAll(
                heading,
                formCard
        );

        ScrollPane scroll =
                new ScrollPane(content);

        scroll.setFitToWidth(true);

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        root.setLeft(sidebar);
        root.setTop(header);
        root.setCenter(scroll);

        return new Scene(
                root,
                1440,
                900
        );
    }

    // =========================
    // NAVIGATION BUTTON
    // =========================

    private Button navButton(
            String icon,
            String text,
            Runnable action
    ) {

        Button button =
                new Button(icon + "   " + text);

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setAlignment(
                Pos.CENTER_LEFT
        );

        button.setPrefHeight(45);

        button.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;"
        );

        button.setOnAction(
                e -> action.run()
        );

        return button;
    }

    // =========================
    // ALERT
    // =========================

    private void showAlert(
            String title,
            String message
    ) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}