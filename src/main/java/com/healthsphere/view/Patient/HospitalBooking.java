package com.healthsphere.view.Patient;

import java.time.LocalDate;
import java.util.List;

import com.healthsphere.controller.patient.AppointmentController;
import com.healthsphere.controller.patient.PatientController;
import com.healthsphere.model.HospitalProfile;
import com.healthsphere.model.PatientProfile;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HospitalBooking {

    private final Stage stage;

    private final AppointmentController appointmentController;

    private final PatientController patientController;

    public HospitalBooking(Stage stage) {

        this.stage = stage;

        this.appointmentController =
                new AppointmentController();

        this.patientController =
                new PatientController();
    }

    // =========================================================
    // SCENE
    // =========================================================

    public Scene getScene() {

        VBox content =
                new VBox(20);

        content.setPadding(
                new Insets(5)
        );

        content.setMinWidth(0);

        content.setMaxWidth(
                Double.MAX_VALUE
        );

        VBox form =
                PatientUI.card(
                        "Hospital Appointment"
                );

        // =====================================================
        // PATIENT
        // =====================================================

        TextField patientName =
                new TextField();

        patientName.setPromptText(
                "Patient name"
        );

        patientName.setPrefHeight(43);

        patientName.setEditable(false);

        patientName.setStyle(
                "-fx-background-color: #e2e8f0;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-text-fill: #334155;"
        );

        loadPatientName(
                patientName
        );

        // =====================================================
        // REAL HOSPITALS FROM FIRESTORE
        // =====================================================

        ComboBox<HospitalProfile> hospital =
                new ComboBox<>();

        hospital.setPromptText(
                "Select hospital"
        );

        hospital.setPrefHeight(43);

        hospital.setMaxWidth(
                Double.MAX_VALUE
        );

        loadHospitals(
                hospital
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

        date.setDayCellFactory(
                picker ->
                        new DateCell() {

                            @Override
                            public void updateItem(
                                    LocalDate item,
                                    boolean empty) {

                                super.updateItem(
                                        item,
                                        empty
                                );

                                if (!empty &&
                                        item.isBefore(
                                                LocalDate.now()
                                        )) {

                                    setDisable(true);
                                }
                            }
                        }
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

        // =====================================================
        // LEFT
        // =====================================================

        VBox left =
                new VBox(

                        7,

                        label("Patient Name"),

                        patientName,

                        label("Hospital"),

                        hospital
                );

        // =====================================================
        // RIGHT
        // =====================================================

        VBox right =
                new VBox(

                        7,

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

        // =====================================================
        // ROW
        // =====================================================

        HBox row =
                new HBox(

                        20,

                        left,

                        right
                );

        // =====================================================
        // CONFIRM
        // =====================================================

        Button confirm =
                PatientUI.button(

                        "Confirm Hospital Appointment",

                        () ->
                                confirmAppointment(

                                        hospital,

                                        date,

                                        time,

                                        reason
                                )
                );

        // =====================================================
        // CANCEL
        // =====================================================

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

        // =====================================================
        // FORM
        // =====================================================

        form.getChildren().addAll(

                row,

                label("Reason for Visit"),

                reason,

                actions
        );

        content.getChildren().add(
                form
        );

        return PatientUI.createScene(

                stage,

                "Hospital Booking",

                "Hospital Appointment",

                "Book an appointment directly with a hospital.",

                content
        );
    }

    // =========================================================
    // LOAD REAL HOSPITALS
    // =========================================================

    private void loadHospitals(
            ComboBox<HospitalProfile> hospitalComboBox) {

        try {

            List<HospitalProfile> hospitals =
                    appointmentController
                            .getAllHospitals();

            hospitalComboBox
                    .getItems()
                    .clear();

            hospitalComboBox
                    .getItems()
                    .addAll(hospitals);

            // -------------------------------------------------
            // SELECTED ITEM DISPLAY
            // -------------------------------------------------

            hospitalComboBox.setButtonCell(

                    new javafx.scene.control.ListCell<HospitalProfile>() {

                        @Override
                        protected void updateItem(

                                HospitalProfile hospital,

                                boolean empty) {

                            super.updateItem(
                                    hospital,
                                    empty
                            );

                            if (empty ||
                                    hospital == null) {

                                setText(
                                        "Select hospital"
                                );

                            } else {

                                setText(
                                        getHospitalDisplayName(
                                                hospital
                                        )
                                );
                            }
                        }
                    }
            );

            // -------------------------------------------------
            // DROPDOWN DISPLAY
            // -------------------------------------------------

            hospitalComboBox.setCellFactory(

                    listView ->

                            new javafx.scene.control.ListCell<HospitalProfile>() {

                                @Override
                                protected void updateItem(

                                        HospitalProfile hospital,

                                        boolean empty) {

                                    super.updateItem(
                                            hospital,
                                            empty
                                    );

                                    if (empty ||
                                            hospital == null) {

                                        setText(null);

                                    } else {

                                        setText(
                                                getHospitalDisplayName(
                                                        hospital
                                                )
                                        );
                                    }
                                }
                            }
            );

        } catch (Exception e) {

            showError(

                    "Unable to load hospitals.\n\n"
                            + e.getMessage()
            );
        }
    }

    // =========================================================
    // HOSPITAL DISPLAY NAME
    // =========================================================

    private String getHospitalDisplayName(
            HospitalProfile hospital) {

        if (hospital == null) {
            return "Hospital";
        }

        String name =
                hospital.getHospitalName();

        if (name == null ||
                name.isBlank()) {

            return "Hospital";
        }

        return name.trim();
    }

    // =========================================================
    // PATIENT NAME
    // =========================================================

    private void loadPatientName(
            TextField patientName) {

        try {

            PatientProfile profile =
                    patientController
                            .getCurrentPatientProfile();

            if (profile == null) {

                patientName.setText(
                        "Patient"
                );

                return;
            }

            String firstName =
                    profile.getFirstName() == null
                            ? ""
                            : profile.getFirstName()
                                    .trim();

            String lastName =
                    profile.getLastName() == null
                            ? ""
                            : profile.getLastName()
                                    .trim();

            String fullName =
                    (firstName + " " + lastName)
                            .trim();

            patientName.setText(

                    fullName.isBlank()
                            ? "Patient"
                            : fullName
            );

        } catch (Exception e) {

            patientName.setText(
                    "Patient"
            );
        }
    }

    // =========================================================
    // CONFIRM
    // =========================================================

    private void confirmAppointment(

            ComboBox<HospitalProfile> hospital,

            DatePicker date,

            ComboBox<String> time,

            TextArea reason) {

        // -----------------------------------------------------
        // HOSPITAL
        // -----------------------------------------------------

        if (hospital.getValue() == null) {

            showError(
                    "Please select a hospital."
            );

            return;
        }

        HospitalProfile selectedHospital =
                hospital.getValue();

        // -----------------------------------------------------
        // DATE
        // -----------------------------------------------------

        if (date.getValue() == null) {

            showError(
                    "Please select an appointment date."
            );

            return;
        }

        if (date.getValue().isBefore(
                LocalDate.now())) {

            showError(
                    "Appointment date cannot be in the past."
            );

            return;
        }

        // -----------------------------------------------------
        // TIME
        // -----------------------------------------------------

        if (time.getValue() == null) {

            showError(
                    "Please select an appointment time."
            );

            return;
        }

        // -----------------------------------------------------
        // CREATE APPOINTMENT
        // -----------------------------------------------------

        try {

            appointmentController
                    .createHospitalAppointment(

                            selectedHospital.getUid(),

                            getHospitalDisplayName(
                                    selectedHospital
                            ),

                            date.getValue().toString(),

                            time.getValue(),

                            reason.getText()
                    );

            showSuccess();

        } catch (Exception e) {

            showError(

                    "Unable to book the hospital appointment.\n\n"
                            + e.getMessage()
            );
        }
    }

    // =========================================================
    // SUCCESS
    // =========================================================

    private void showSuccess() {

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

                        "Hospital Appointment Confirmed"
                );

        title.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label description =
                new Label(

                        "Your hospital appointment has been successfully booked."
                );

        description.setWrapText(true);

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

        stage.setScene(

                PatientUI.createScene(

                        stage,

                        "Hospital Appointment Confirmed",

                        "Appointment Confirmed",

                        "Your hospital appointment has been successfully booked.",

                        content
                )
        );

        stage.show();
    }

    // =========================================================
    // ERROR
    // =========================================================

    private void showError(
            String message) {

        VBox content =
                new VBox(18);

        content.setAlignment(
                Pos.CENTER
        );

        content.setPadding(
                new Insets(40)
        );

        Label icon =
                new Label("!");

        icon.setStyle(
                "-fx-font-size: 50px;" +
                "-fx-text-fill: #dc2626;" +
                "-fx-font-weight: bold;"
        );

        Label title =
                new Label(

                        "Appointment Not Booked"
                );

        title.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;"
        );

        Label description =
                new Label(message);

        description.setWrapText(true);

        description.setMaxWidth(600);

        Button back =
                PatientUI.secondaryButton(

                        "Back",

                        this::showHospitalBooking
                );

        content.getChildren().addAll(

                icon,

                title,

                description,

                back
        );

        stage.setScene(

                PatientUI.createScene(

                        stage,

                        "Hospital Appointment Error",

                        "Appointment Error",

                        "Please review the details and try again.",

                        content
                )
        );

        stage.show();
    }

    // =========================================================
    // BACK TO BOOKING
    // =========================================================

    private void showHospitalBooking() {

        stage.setScene(

                new HospitalBooking(stage)
                        .getScene()
        );

        stage.show();
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
    }

    // =========================================================
    // LABEL
    // =========================================================

    private Label label(
            String text) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #334155;"
        );

        return label;
    }
}