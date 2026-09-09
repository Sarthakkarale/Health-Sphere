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

        loadDoctors(
                doctor,
                specialty
        );

        specialty.setOnAction(e -> {
            String selectedSpec = specialty.getValue();
            if (selectedSpec != null && !selectedSpec.isBlank()) {
                List<DoctorProfile> allDocs = appointmentController.getAllDoctors();
                if (allDocs != null) {
                    DoctorProfile currentSel = doctor.getValue();
                    doctor.getItems().clear();
                    for (DoctorProfile d : allDocs) {
                        if (d != null && selectedSpec.equalsIgnoreCase(d.getSpecialization())) {
                            doctor.getItems().add(d);
                        }
                    }
                    if (currentSel != null && selectedSpec.equalsIgnoreCase(currentSel.getSpecialization())) {
                        doctor.setValue(currentSel);
                    }
                }
            }
        });

        doctor.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue == null) {
                        return;
                    }

                    String specialization =
                            newValue.getSpecialization();

                    if (specialization != null &&
                            !specialization.isBlank()) {

                        if (!specialty.getItems().contains(specialization.trim())) {
                            specialty.getItems().add(specialization.trim());
                        }

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

        time.setPromptText(
                "Select time"
        );

        time.setPrefHeight(43);

        time.setMaxWidth(
                Double.MAX_VALUE
        );

        // =====================================================
        // DYNAMIC SLOTS UPDATER
        // =====================================================

        date.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (doctor.getValue() != null && newVal != null) {
                List<String> slots = appointmentController.getAvailableSlots(doctor.getValue().getUid(), newVal.toString());
                time.getItems().clear();
                if (slots != null && !slots.isEmpty()) {
                    time.getItems().addAll(slots);
                } else {
                    time.setPromptText("No slots available");
                }
            }
        });

        Label feeLabel = new Label("Consultation Fee: Select a doctor");
        feeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        doctor.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && date.getValue() != null) {
                List<String> slots = appointmentController.getAvailableSlots(newVal.getUid(), date.getValue().toString());
                time.getItems().clear();
                if (slots != null && !slots.isEmpty()) {
                    time.getItems().addAll(slots);
                } else {
                    time.setPromptText("No slots available");
                }
            }

            if (newVal != null && newVal.getUid() != null) {
                com.healthsphere.util.PatientBackgroundExecutor.execute(() -> {
                    try {
                        com.healthsphere.dao.doctor.DoctorAvailabilityDAO availDao = new com.healthsphere.dao.doctor.DoctorAvailabilityDAO();
                        com.healthsphere.model.DoctorAvailability avail = availDao.getAvailability(newVal.getUid().trim());
                        javafx.application.Platform.runLater(() -> {
                            if (avail != null && avail.getConsultationFee() > 0) {
                                feeLabel.setText("Consultation Fee: ₹" + (int) avail.getConsultationFee());
                                feeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #059669;");
                            } else {
                                feeLabel.setText("Consultation Fee: Not configured by doctor");
                                feeLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #D97706;");
                            }
                        });
                    } catch (Exception ignored) {}
                });
            } else {
                feeLabel.setText("Consultation Fee: Select a doctor");
                feeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");
            }
        });

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
                        doctor,
                        feeLabel
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
            ComboBox<DoctorProfile> doctorComboBox,
            ComboBox<String> specialtyComboBox) {

        javafx.concurrent.Task<List<DoctorProfile>> task = new javafx.concurrent.Task<>() {
            @Override
            protected List<DoctorProfile> call() {
                return appointmentController.getAllDoctors();
            }
        };

        task.setOnSucceeded(e -> {
            List<DoctorProfile> doctors = task.getValue();
            doctorComboBox.getItems().clear();
            specialtyComboBox.getItems().clear();

            if (doctors != null) {
                doctorComboBox.getItems().addAll(doctors);

                java.util.Set<String> specs = new java.util.TreeSet<>();
                for (DoctorProfile d : doctors) {
                    if (d != null && d.getSpecialization() != null && !d.getSpecialization().isBlank()) {
                        specs.add(d.getSpecialization().trim());
                    }
                }
                if (specs.isEmpty()) {
                    specs.addAll(java.util.List.of("Cardiology", "Dermatology", "General Medicine", "Neurology", "Orthopedics", "Pediatrics"));
                }
                specialtyComboBox.getItems().addAll(specs);
            }

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

            // Display doctor name instead of object memory address.
            doctorComboBox.setButtonCell(
                    new javafx.scene.control.ListCell<DoctorProfile>() {
                        @Override
                        protected void updateItem(DoctorProfile doctor, boolean empty) {
                            super.updateItem(doctor, empty);
                            if (empty || doctor == null) {
                                setText("Select doctor");
                            } else {
                                setText(getDoctorDisplayName(doctor));
                            }
                        }
                    }
            );

            doctorComboBox.setCellFactory(
                    listView -> new javafx.scene.control.ListCell<DoctorProfile>() {
                        @Override
                        protected void updateItem(DoctorProfile doctor, boolean empty) {
                            super.updateItem(doctor, empty);
                            if (empty || doctor == null) {
                                setText(null);
                            } else {
                                setText(getDoctorDisplayName(doctor));
                            }
                        }
                    }
            );
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            showError("Unable to load doctors.\n\n" + (ex != null ? ex.getMessage() : ""));
        });

        com.healthsphere.util.PatientBackgroundExecutor.execute(task);
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

        javafx.concurrent.Task<String> task = new javafx.concurrent.Task<>() {
            @Override
            protected String call() {
                try {
                    PatientProfile profile = patientController.getCurrentPatientProfile();
                    if (profile == null) return "Patient";
                    String firstName = profile.getFirstName() == null ? "" : profile.getFirstName().trim();
                    String lastName = profile.getLastName() == null ? "" : profile.getLastName().trim();
                    String fullName = (firstName + " " + lastName).trim();
                    return fullName.isBlank() ? "Patient" : fullName;
                } catch (Exception e) {
                    return "Patient";
                }
            }
        };

        task.setOnSucceeded(e -> patientName.setText(task.getValue()));
        task.setOnFailed(e -> patientName.setText("Patient"));

        com.healthsphere.util.PatientBackgroundExecutor.execute(task);
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

        if (appointmentController.isSlotBooked(selectedDoctor.getUid(), date.getValue().toString(), time.getValue())) {
            showError("This time slot is already booked. Please choose another time or date.");
            return;
        }

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