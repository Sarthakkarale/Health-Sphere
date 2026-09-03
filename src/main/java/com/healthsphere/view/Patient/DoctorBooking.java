package com.healthsphere.view.Patient;

import java.time.LocalDate;
import java.util.List;

import com.healthsphere.controller.patient.AppointmentController;
import com.healthsphere.controller.patient.PatientController;
import com.healthsphere.model.DoctorProfile;
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

public class DoctorBooking {

    private final Stage stage;
    private final AppointmentController appointmentController;
    private final PatientController patientController;
    private final DoctorProfile preselectedDoctor;

    public DoctorBooking(Stage stage) {
        this(stage, null);
    }

    public DoctorBooking(Stage stage, DoctorProfile preselectedDoctor) {

        this.stage = stage;
        this.preselectedDoctor = preselectedDoctor;

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
                        "Doctor Appointment"
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
        // REAL DOCTORS FROM FIRESTORE
        // =====================================================

        ComboBox<DoctorProfile> doctor =
                new ComboBox<>();

        doctor.setPromptText(
                "Select doctor"
        );

        doctor.setPrefHeight(43);

        doctor.setMaxWidth(
                Double.MAX_VALUE
        );

        loadDoctors(
                doctor
        );

        // =====================================================
        // SPECIALTY
        // =====================================================

        ComboBox<String> specialty =
                new ComboBox<>();

        specialty.setPromptText(
                "Select specialty"
        );

        specialty.setPrefHeight(43);

        specialty.setMaxWidth(
                Double.MAX_VALUE
        );

        // Automatically change specialty
        // when doctor is selected.
        doctor.valueProperty().addListener(
                (observable, oldValue, newValue) -> {

                    specialty.getItems().clear();

                    if (newValue == null) {
                        return;
                    }

                    String specialization =
                            newValue.getSpecialization();

                    if (specialization != null &&
                            !specialization.isBlank()) {

                        specialty.getItems().add(
                                specialization.trim()
                        );

                        specialty.setValue(
                                specialization.trim()
                        );
                    }
                }
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
                        label("Doctor"),
                        doctor
                );

        // =====================================================
        // RIGHT
        // =====================================================

        VBox right =
                new VBox(
                        7,
                        label("Specialty"),
                        specialty,
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

                        "Confirm Doctor Appointment",

                        () ->
                                confirmAppointment(
                                        doctor,
                                        specialty,
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

                "Doctor Booking",

                "Doctor Appointment",

                "Book an appointment directly with a doctor.",

                content
        );
    }

    // =========================================================
    // LOAD REAL DOCTORS
    // =========================================================

    private void loadDoctors(
            ComboBox<DoctorProfile> doctorComboBox) {

        try {

            List<DoctorProfile> doctors =
                    appointmentController
                            .getAllDoctors();

            doctorComboBox.getItems().clear();

            doctorComboBox
                    .getItems()
                    .addAll(doctors);

            if (preselectedDoctor != null && doctors != null) {
                for (DoctorProfile d : doctors) {
                    if (d != null) {
                        boolean matchUid = d.getUid() != null && preselectedDoctor.getUid() != null
                                && d.getUid().equals(preselectedDoctor.getUid());
                        boolean matchName = getDoctorDisplayName(d).equalsIgnoreCase(getDoctorDisplayName(preselectedDoctor));
                        if (matchUid || matchName) {
                            doctorComboBox.setValue(d);
                            break;
                        }
                    }
                }
            }

            // Display doctor name instead of
            // object memory address.
            doctorComboBox.setButtonCell(
                    new javafx.scene.control.ListCell<DoctorProfile>() {

                        @Override
                        protected void updateItem(
                                DoctorProfile doctor,
                                boolean empty) {

                            super.updateItem(
                                    doctor,
                                    empty
                            );

                            if (empty ||
                                    doctor == null) {

                                setText(
                                        "Select doctor"
                                );

                            } else {

                                setText(
                                        getDoctorDisplayName(
                                                doctor
                                        )
                                );
                            }
                        }
                    }
            );

            doctorComboBox.setCellFactory(
                    listView ->
                            new javafx.scene.control.ListCell<DoctorProfile>() {

                                @Override
                                protected void updateItem(
                                        DoctorProfile doctor,
                                        boolean empty) {

                                    super.updateItem(
                                            doctor,
                                            empty
                                    );

                                    if (empty ||
                                            doctor == null) {

                                        setText(null);

                                    } else {

                                        setText(
                                                getDoctorDisplayName(
                                                        doctor
                                                )
                                        );
                                    }
                                }
                            }
            );

        } catch (Exception e) {

            showError(
                    "Unable to load doctors.\n\n"
                            + e.getMessage()
            );
        }
    }

    // =========================================================
    // DOCTOR DISPLAY NAME
    // =========================================================

    private String getDoctorDisplayName(
            DoctorProfile doctor) {

        if (doctor == null) {
            return "Doctor";
        }

        String firstName =
                doctor.getFirstName() == null
                        ? ""
                        : doctor.getFirstName().trim();

        String lastName =
                doctor.getLastName() == null
                        ? ""
                        : doctor.getLastName().trim();

        String name =
                ("Dr. " + firstName + " " + lastName)
                        .trim();

        if (firstName.isBlank() &&
                lastName.isBlank()) {

            return "Doctor";
        }

        return name;
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

            ComboBox<DoctorProfile> doctor,

            ComboBox<String> specialty,

            DatePicker date,

            ComboBox<String> time,

            TextArea reason) {

        // -----------------------------------------------------
        // DOCTOR
        // -----------------------------------------------------

        if (doctor.getValue() == null) {

            showError(
                    "Please select a doctor."
            );

            return;
        }

        DoctorProfile selectedDoctor =
                doctor.getValue();

        // -----------------------------------------------------
        // SPECIALTY
        // -----------------------------------------------------

        if (specialty.getValue() == null ||
                specialty.getValue().isBlank()) {

            showError(
                    "Doctor specialization is missing."
            );

            return;
        }

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
                    .createDoctorAppointment(

                            selectedDoctor.getUid(),

                            getDoctorDisplayName(
                                    selectedDoctor
                            ),

                            specialty.getValue(),

                            date.getValue().toString(),

                            time.getValue(),

                            reason.getText()
                    );

            showSuccess();

        } catch (Exception e) {

            showError(

                    "Unable to book the doctor appointment.\n\n"
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
                        "Doctor Appointment Confirmed"
                );

        title.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label description =
                new Label(
                        "Your doctor appointment has been successfully booked."
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

                        "Doctor Appointment Confirmed",

                        "Appointment Confirmed",

                        "Your doctor appointment has been successfully booked.",

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

                        this::showDoctorBooking
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

                        "Doctor Appointment Error",

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

    private void showDoctorBooking() {

        stage.setScene(

                new DoctorBooking(stage)
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