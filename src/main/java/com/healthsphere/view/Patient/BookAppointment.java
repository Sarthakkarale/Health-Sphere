package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BookAppointment {

    private final Stage stage;

    public BookAppointment(Stage stage) {
        this.stage = stage;
    }

    // =========================================================
    // MAIN SCENE
    // =========================================================

    public Scene getScene() {

        VBox content =
                new VBox(22);

        content.setPadding(
                new Insets(5)
        );

        // =====================================================
        // IMAGES
        // =====================================================

        HBox images =
                new HBox(15);

        images.setAlignment(
                Pos.CENTER_LEFT
        );

        images.getChildren().addAll(

                imageCard(
                        "/images/appointments/appointment1.jpg"
                ),

                imageCard(
                        "/images/appointments/appointment2.jpg"
                ),

                imageCard(
                        "/images/appointments/appointment3.jpg"
                ),

                imageCard(
                        "/images/appointments/appointment4.jpg"
                )
        );

        // =====================================================
        // FORM CARD
        // =====================================================

        VBox form =
                PatientUI.card(
                        "Appointment Details"
                );

        // =====================================================
        // PATIENT NAME
        // =====================================================

        TextField patientName =
                field(
                        "Enter patient name"
                );

        // =====================================================
        // DOCTOR
        // =====================================================

        ComboBox<String> doctor =
                new ComboBox<>();

        doctor.getItems().addAll(
                "Dr. Sarah Jenkins",
                "Dr. Michael Brown",
                "Dr. Emily Wilson",
                "Dr. Robert Smith"
        );

        doctor.setPromptText(
                "Select doctor"
        );

        doctor.setPrefHeight(43);
        doctor.setMaxWidth(
                Double.MAX_VALUE
        );

        // =====================================================
        // SPECIALTY
        // =====================================================

        ComboBox<String> specialty =
                new ComboBox<>();

        specialty.getItems().addAll(
                "Cardiology",
                "General Medicine",
                "Dermatology",
                "Orthopedics",
                "Neurology",
                "Pediatrics"
        );

        specialty.setPromptText(
                "Select specialty"
        );

        specialty.setPrefHeight(43);
        specialty.setMaxWidth(
                Double.MAX_VALUE
        );

        // =====================================================
        // HOSPITAL
        // =====================================================

        ComboBox<String> hospital =
                new ComboBox<>();

        hospital.getItems().addAll(
                "Apollo Hospitals",
                "Care Hospitals",
                "Yashoda Hospitals",
                "KIMS Hospitals"
        );

        hospital.setPromptText(
                "Select hospital"
        );

        hospital.setPrefHeight(43);
        hospital.setMaxWidth(
                Double.MAX_VALUE
        );

        // =====================================================
        // DATE
        // =====================================================

        DatePicker date =
                new DatePicker();

        date.setPromptText(
                "Select appointment date"
        );

        date.setPrefHeight(43);
        date.setMaxWidth(
                Double.MAX_VALUE
        );

        // =====================================================
        // TIME
        // =====================================================

        ComboBox<String> time =
                new ComboBox<>();

        time.getItems().addAll(
                "09:00 AM",
                "09:30 AM",
                "10:00 AM",
                "10:30 AM",
                "11:00 AM",
                "11:30 AM",
                "02:00 PM",
                "02:30 PM",
                "03:00 PM",
                "03:30 PM",
                "04:00 PM"
        );

        time.setPromptText(
                "Select time"
        );

        time.setPrefHeight(43);
        time.setMaxWidth(
                Double.MAX_VALUE
        );

        // =====================================================
        // REASON
        // =====================================================

        TextArea reason =
                new TextArea();

        reason.setPromptText(
                "Briefly describe the reason for your visit..."
        );

        reason.setPrefRowCount(4);

        reason.setWrapText(true);

        reason.setStyle(
                "-fx-background-color: #f8fafc;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        // =====================================================
        // LEFT FORM
        // =====================================================

        VBox left =
                new VBox(
                        7,

                        label("Patient Name"),
                        patientName,

                        label("Doctor"),
                        doctor,

                        label("Specialty"),
                        specialty
                );

        // =====================================================
        // RIGHT FORM
        // =====================================================

        VBox right =
                new VBox(
                        7,

                        label("Hospital"),
                        hospital,

                        label("Date"),
                        date,

                        label("Time"),
                        time
                );

        HBox.setHgrow(
                left,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                right,
                Priority.ALWAYS
        );

        HBox row =
                new HBox(20);

        row.getChildren().addAll(
                left,
                right
        );

        // =====================================================
        // REASON LABEL
        // =====================================================

        Label reasonLabel =
                label(
                        "Reason for Visit"
                );

        // =====================================================
        // ACTION BUTTONS
        // =====================================================

        Button confirm =
                PatientUI.button(
                        "Confirm Appointment",
                        () -> confirmAppointment(
                                patientName,
                                doctor,
                                specialty,
                                hospital,
                                date,
                                time,
                                reason
                        )
                );

        Button cancel =
                PatientUI.secondaryButton(
                        "Cancel",
                        this::showAppointments
                );

        HBox actions =
                new HBox(
                        12,
                        confirm,
                        cancel
                );

        form.getChildren().addAll(
                row,
                reasonLabel,
                reason,
                actions
        );

        // =====================================================
        // ADD CONTENT
        // =====================================================

        content.getChildren().addAll(
                images,
                form
        );

        // =====================================================
        // SCROLL
        // =====================================================

        ScrollPane scroll =
                new ScrollPane(content);

        scroll.setFitToWidth(true);

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

        VBox wrapper =
                new VBox(scroll);

        // =====================================================
        // COMMON PATIENT HEADER
        // =====================================================

        return PatientUI.createScene(
                stage,
                "Book Appointment",
                "Book Appointment",
                "Choose a doctor, hospital, date and convenient time.",
                wrapper
        );
    }

    // =========================================================
    // CONFIRM APPOINTMENT
    // =========================================================

    private void confirmAppointment(
            TextField patientName,
            ComboBox<String> doctor,
            ComboBox<String> specialty,
            ComboBox<String> hospital,
            DatePicker date,
            ComboBox<String> time,
            TextArea reason
    ) {

        if (
                patientName.getText().trim().isEmpty()
                        ||
                doctor.getValue() == null
                        ||
                specialty.getValue() == null
                        ||
                hospital.getValue() == null
                        ||
                date.getValue() == null
                        ||
                time.getValue() == null
        ) {

            showMessage(
                    "Please complete all required appointment details."
            );

            return;
        }

        showMessage(
                "Appointment confirmed successfully!"
        );
    }

    // =========================================================
    // SUCCESS SCREEN
    // =========================================================

    private void showMessage(
            String message
    ) {

        VBox content =
                new VBox(20);

        content.setAlignment(
                Pos.CENTER
        );

        content.setPadding(
                new Insets(40)
        );

        Label icon =
                new Label("✓");

        icon.setStyle(
                "-fx-font-size: 60px;" +
                "-fx-text-fill: #16a34a;" +
                "-fx-font-weight: bold;"
        );

        Label title =
                new Label(
                        "Appointment Confirmed"
                );

        title.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label description =
                new Label(message);

        description.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-text-fill: #64748b;"
        );

        Button appointments =
                PatientUI.button(
                        "View Appointments",
                        this::showAppointments
                );

        content.getChildren().addAll(
                icon,
                title,
                description,
                appointments
        );

        VBox wrapper =
                new VBox(content);

        wrapper.setAlignment(
                Pos.CENTER
        );

        stage.setScene(
                PatientUI.createScene(
                        stage,
                        "Appointment Confirmed",
                        "Appointment Confirmed",
                        "Your healthcare appointment has been successfully booked.",
                        wrapper
                )
        );

        stage.show();
    }

    // =========================================================
    // FORM FIELD
    // =========================================================

    private TextField field(
            String prompt
    ) {

        TextField field =
                new TextField();

        field.setPromptText(
                prompt
        );

        field.setPrefHeight(43);

        field.setStyle(
                "-fx-background-color: #f8fafc;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        return field;
    }

    // =========================================================
    // LABEL
    // =========================================================

    private Label label(
            String text
    ) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #334155;"
        );

        return label;
    }

    // =========================================================
    // IMAGE CARD
    // =========================================================

    private VBox imageCard(
            String path
    ) {

        VBox box =
                new VBox();

        ImageView image =
                loadImage(
                        path,
                        270,
                        145
                );

        box.getChildren().add(
                image
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 14;"
        );

        return box;
    }

    // =========================================================
    // IMAGE LOADER
    // =========================================================

    private ImageView loadImage(
            String path,
            double width,
            double height
    ) {

        ImageView view =
                new ImageView();

        var resource =
                getClass().getResource(path);

        if (resource == null) {

            System.err.println(
                    "Appointment image not found: "
                            + path
            );

            view.setFitWidth(width);
            view.setFitHeight(height);

            return view;
        }

        Image image =
                new Image(
                        resource.toExternalForm()
                );

        view.setImage(image);

        view.setFitWidth(width);
        view.setFitHeight(height);

        view.setPreserveRatio(false);

        return view;
    }

    // =========================================================
    // NAVIGATION
    // =========================================================

    private void showAppointments() {

        stage.setScene(
                new Appointments(stage).getScene()
        );

        stage.show();
    }
}