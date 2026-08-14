package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HospitalDetails {

    private final Stage stage;

    private final String hospitalName;
    private final String location;
    private final String type;
    private final String rating;
    private final String specialties;

    public HospitalDetails(
            Stage stage,
            String hospitalName,
            String location,
            String type,
            String rating,
            String specialties
    ) {

        this.stage = stage;

        this.hospitalName = hospitalName;
        this.location = location;
        this.type = type;
        this.rating = rating;
        this.specialties = specialties;
    }

    public Scene getScene() {

        BorderPane root =
                new BorderPane();

        root.setLeft(
                createSidebar()
        );

        root.setTop(
                createHeader()
        );

        VBox content =
                new VBox(20);

        content.setPadding(
                new Insets(28)
        );

        content.setStyle(
                "-fx-background-color: #f8fafc;"
        );

        // =====================================================
        // BACK BUTTON
        // =====================================================

        Button back =
                new Button("← Back to Hospitals");

        back.setStyle(
                "-fx-background-color: #e2e8f0;" +
                "-fx-text-fill: #334155;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10 18 10 18;"
        );

        back.setOnAction(
                e -> showSearchHospitals()
        );

        // =====================================================
        // HOSPITAL HEADER
        // =====================================================

        VBox hospitalHeader =
                coloredCard(
                        "#eff6ff",
                        "#2563eb"
                );

        Label name =
                new Label(hospitalName);

        name.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1e3a8a;"
        );

        Label locationLabel =
                new Label(
                        "📍 " + location
                );

        locationLabel.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #475569;"
        );

        Label typeLabel =
                new Label(type);

        typeLabel.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #2563eb;"
        );

        Label ratingLabel =
                new Label(
                        "⭐ " + rating
                );

        ratingLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #d97706;"
        );

        hospitalHeader.getChildren().addAll(
                name,
                locationLabel,
                typeLabel,
                ratingLabel
        );

        // =====================================================
        // ABOUT
        // =====================================================

        VBox about =
                coloredCard(
                        "#f0fdf4",
                        "#16a34a"
                );

        Label aboutTitle =
                new Label("About Hospital");

        aboutTitle.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #15803d;"
        );

        Label aboutText =
                new Label(
                        hospitalName +
                        " is a modern multi-speciality healthcare facility " +
                        "providing comprehensive medical services and patient-focused care."
                );

        aboutText.setWrapText(true);

        aboutText.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #475569;"
        );

        about.getChildren().addAll(
                aboutTitle,
                aboutText
        );

        // =====================================================
        // SPECIALITIES
        // =====================================================

        VBox specialityCard =
                coloredCard(
                        "#f3e8ff",
                        "#9333ea"
                );

        Label specialityTitle =
                new Label("Available Specialities");

        specialityTitle.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #7e22ce;"
        );

        Label specialityText =
                new Label(specialties);

        specialityText.setWrapText(true);

        specialityText.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #475569;"
        );

        specialityCard.getChildren().addAll(
                specialityTitle,
                specialityText
        );

        // =====================================================
        // SERVICES
        // =====================================================

        VBox services =
                coloredCard(
                        "#fff7ed",
                        "#ea580c"
                );

        Label servicesTitle =
                new Label("Hospital Services");

        servicesTitle.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #c2410c;"
        );

        services.getChildren().addAll(
                servicesTitle,

                service("24/7 Emergency Services"),
                service("Pharmacy"),
                service("Diagnostic Laboratory"),
                service("Radiology"),
                service("Ambulance Services"),
                service("Online Consultation")
        );

        // =====================================================
        // APPOINTMENT
        // =====================================================

        VBox appointment =
                coloredCard(
                        "#ecfdf5",
                        "#059669"
                );

        Label appointmentTitle =
                new Label(
                        "Book an Appointment"
                );

        appointmentTitle.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #047857;"
        );

        Label appointmentText =
                new Label(
                        "Choose this hospital to book an appointment with an available doctor."
                );

        appointmentText.setWrapText(true);

        appointmentText.setStyle(
                "-fx-text-fill: #475569;" +
                "-fx-font-size: 14px;"
        );

        Button book =
                new Button(
                        "Book Appointment"
                );

        book.setPrefHeight(45);

        book.setStyle(
                "-fx-background-color: #059669;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 0 25 0 25;"
        );

        book.setOnAction(
                e -> showBookAppointment()
        );

        appointment.getChildren().addAll(
                appointmentTitle,
                appointmentText,
                book
        );

        // =====================================================
        // ADD CONTENT
        // =====================================================

        content.getChildren().addAll(
                back,
                hospitalHeader,
                about,
                specialityCard,
                services,
                appointment
        );

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
    // SERVICE ROW
    // =========================================================

    private HBox service(
            String text
    ) {

        HBox row =
                new HBox(10);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        Label check =
                new Label("✓");

        check.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #16a34a;"
        );

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-text-fill: #475569;" +
                "-fx-font-size: 14px;"
        );

        row.getChildren().addAll(
                check,
                label
        );

        return row;
    }

    // =========================================================
    // COLORED CARD
    // =========================================================

    private VBox coloredCard(
            String background,
            String accent
    ) {

        VBox card =
                new VBox(12);

        card.setPadding(
                new Insets(20)
        );

        card.setStyle(
                "-fx-background-color: " +
                background +
                ";" +

                "-fx-border-color: " +
                accent +
                ";" +

                "-fx-border-width: 0 0 0 5;" +

                "-fx-background-radius: 12;" +
                "-fx-border-radius: 12;"
        );

        return card;
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
                        15,
                        28,
                        15,
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

        Label title =
                new Label(
                        "Hospital Details"
                );

        title.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #334155;"
        );

        Button notifications =
                new Button("Notifications");

        notifications.setOnAction(
                e -> showNotifications()
        );

        Button profile =
                new Button("Sarah");

        profile.setOnAction(
                e -> showProfileSettings()
        );

        header.getChildren().addAll(
                spacer,
                title,
                notifications,
                profile
        );

        return header;
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
                        "✚  MediNexus AI"
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

                nav(
                        "▦",
                        "Dashboard",
                        false,
                        this::showDashboard
                ),

                nav(
                        "⊞",
                        "Search Hospitals",
                        true,
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

        item.setStyle(
                "-fx-background-color: " +
                (selected
                        ? "#2563eb"
                        : "transparent") +
                ";" +
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
    // NAVIGATION
    // =========================================================

    private void showSearchHospitals() {

        stage.setScene(
                new SearchHospitals(stage).getScene()
        );

        stage.show();
    }

    private void showBookAppointment() {

        stage.setScene(
                new BookAppointment(stage).getScene()
        );

        stage.show();
    }

    private void showDashboard() {

        stage.setScene(
                new Dashboard(stage).getScene()
        );

        stage.show();
    }

    private void showAppointments() {

        stage.setScene(
                new Appointments(stage).getScene()
        );

        stage.show();
    }

    private void showHealthPassport() {

        stage.setScene(
                new HealthPassport(stage).getScene()
        );

        stage.show();
    }

    private void showMedicalRecords() {

        stage.setScene(
                new MedicalRecords(stage).getScene()
        );

        stage.show();
    }

    private void showAIHealthAssistant() {

        stage.setScene(
                new AiHealthAssistant(stage).getScene()
        );

        stage.show();
    }

    private void showEmergencyAssistance() {

        stage.setScene(
                new EmergencyAssistance(stage).getScene()
        );

        stage.show();
    }

    private void showNotifications() {

        stage.setScene(
                new Notifications(stage).getScene()
        );

        stage.show();
    }

    private void showProfileSettings() {

        stage.setScene(
                new ProfileSettings(stage).getScene()
        );

        stage.show();
    }
}
