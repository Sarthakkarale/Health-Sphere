package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
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

    // =========================================================
    // MAIN SCENE
    // =========================================================

    public Scene getScene() {

        VBox content = new VBox(20);

        content.setPadding(
                new Insets(5)
        );

        content.setFillWidth(true);

        // =====================================================
        // BACK BUTTON
        // =====================================================

        Button back =
                PatientUI.secondaryButton(
                        "← Back to Hospitals",
                        this::showSearchHospitals
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
                new Label(
                        hospitalName
                );

        name.setWrapText(true);

        name.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1e3a8a;"
        );

        Label locationLabel =
                new Label(
                        "📍 " + location
                );

        locationLabel.setWrapText(true);

        locationLabel.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #475569;"
        );

        Label typeLabel =
                new Label(
                        type
                );

        typeLabel.setWrapText(true);

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
        // ABOUT HOSPITAL
        // =====================================================

        VBox about =
                coloredCard(
                        "#f0fdf4",
                        "#16a34a"
                );

        Label aboutTitle =
                new Label(
                        "About Hospital"
                );

        aboutTitle.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #15803d;"
        );

        Label aboutText =
                new Label(
                        hospitalName +
                        " is a modern multi-speciality healthcare facility " +
                        "providing comprehensive medical services and " +
                        "patient-focused care."
                );

        aboutText.setWrapText(true);

        aboutText.setMaxWidth(
                Double.MAX_VALUE
        );

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
                new Label(
                        "Available Specialities"
                );

        specialityTitle.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #7e22ce;"
        );

        Label specialityText =
                new Label(
                        specialties
                );

        specialityText.setWrapText(true);

        specialityText.setMaxWidth(
                Double.MAX_VALUE
        );

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
                new Label(
                        "Hospital Services"
                );

        servicesTitle.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #c2410c;"
        );

        services.getChildren().addAll(

                servicesTitle,

                service(
                        "24/7 Emergency Services"
                ),

                service(
                        "Pharmacy"
                ),

                service(
                        "Diagnostic Laboratory"
                ),

                service(
                        "Radiology"
                ),

                service(
                        "Ambulance Services"
                ),

                service(
                        "Online Consultation"
                )
        );

        // =====================================================
        // BOOK APPOINTMENT
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
                        "Choose this hospital to book an appointment " +
                        "with an available doctor."
                );

        appointmentText.setWrapText(true);

        appointmentText.setMaxWidth(
                Double.MAX_VALUE
        );

        appointmentText.setStyle(
                "-fx-text-fill: #475569;" +
                "-fx-font-size: 14px;"
        );

        Button book =
                PatientUI.button(
                        "Book Appointment",
                        this::showBookAppointment
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

        // =====================================================
        // SCROLL
        // =====================================================

        ScrollPane scroll =
                new ScrollPane(
                        content
                );

        scroll.setFitToWidth(true);

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scroll.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scroll.setPannable(true);

        scroll.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;" +
                "-fx-border-color: transparent;"
        );

        // =====================================================
        // COMMON PATIENT UI
        // =====================================================

        return PatientUI.createScene(

                stage,

                "Hospital Details",

                "Hospital Details",

                "View hospital information, specialties and available services.",

                scroll
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

        label.setWrapText(true);

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

        card.setMaxWidth(
                Double.MAX_VALUE
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
    // NAVIGATION
    // =========================================================

    private void showSearchHospitals() {

        stage.setScene(
                new SearchHospitals(stage)
                        .getScene()
        );

        stage.show();

        stage.setMaximized(true);
    }

    private void showBookAppointment() {

        stage.setScene(
                new BookAppointment(stage)
                        .getScene()
        );

        stage.show();

        stage.setMaximized(true);
    }
}