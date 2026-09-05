package com.healthsphere.view.Patient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.healthsphere.controller.patient.AppointmentController;
import com.healthsphere.controller.patient.PatientController;
import com.healthsphere.dao.doctor.DoctorDAO;
import com.healthsphere.dao.hospital.HospitalDAO;
import com.healthsphere.model.DoctorProfile;
import com.healthsphere.model.HospitalProfile;
import com.healthsphere.model.PatientProfile;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BookAppointment {

    private final Stage stage;

    private final AppointmentController appointmentController;
    private final PatientController patientController;

    private final HospitalDAO hospitalDAO;
    private final DoctorDAO doctorDAO;

    private List<HospitalProfile> hospitals =
            new ArrayList<>();

    private List<DoctorProfile> doctors =
            new ArrayList<>();

    private final DoctorProfile preselectedDoctor;
    private final HospitalProfile preselectedHospital;
    private final String preselectedHospitalName;

    public BookAppointment(Stage stage) {
        this(stage, null, null, null);
    }

    public BookAppointment(Stage stage, DoctorProfile preselectedDoctor) {
        this(stage, preselectedDoctor, null, null);
    }

    public BookAppointment(Stage stage, HospitalProfile preselectedHospital) {
        this(stage, null, preselectedHospital, preselectedHospital != null ? preselectedHospital.getHospitalName() : null);
    }

    public BookAppointment(Stage stage, String hospitalName) {
        this(stage, null, null, hospitalName);
    }

    private BookAppointment(Stage stage, DoctorProfile preselectedDoctor, HospitalProfile preselectedHospital, String preselectedHospitalName) {

        this.stage = stage;
        this.preselectedDoctor = preselectedDoctor;
        this.preselectedHospital = preselectedHospital;
        this.preselectedHospitalName = preselectedHospitalName;

        this.appointmentController =
                new AppointmentController();

        this.patientController =
                new PatientController();

        this.hospitalDAO =
                new HospitalDAO();

        this.doctorDAO =
                new DoctorDAO();
    }

    // =========================================================
    // MAIN SCENE
    // =========================================================

    public Scene getScene() {

        // =====================================================
        // LOAD DATA
        // =====================================================

        loadHospitals();
        loadDoctors();

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
                        "Patient name"
                );

        loadPatientName(
                patientName
        );

        patientName.setEditable(false);

        patientName.setStyle(
                "-fx-background-color: #e2e8f0;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-text-fill: #334155;"
        );

        // =====================================================
        // BOOKING TYPE
        // =====================================================

        ComboBox<String> bookingType =
                new ComboBox<>();

        bookingType.getItems().addAll(
                "Book a Doctor",
                "Book a Hospital"
        );

        bookingType.setPromptText(
                "Select booking type"
        );

        bookingType.setPrefHeight(43);

        bookingType.setMaxWidth(
                Double.MAX_VALUE
        );

        // =====================================================
        // DOCTOR
        // =====================================================

        ComboBox<String> doctor =
                new ComboBox<>();

        doctor.setPromptText(
                "Select doctor"
        );

        doctor.setPrefHeight(43);

        doctor.setMaxWidth(
                Double.MAX_VALUE
        );

        for (DoctorProfile doctorProfile :
                doctors) {

            String doctorName =
                    getDoctorFullName(
                            doctorProfile
                    );

            if (!doctorName.isBlank()) {

                doctor.getItems().add(
                        doctorName
                );
            }
        }

        // =====================================================
        // SPECIALTY
        // =====================================================

        ComboBox<String> specialty =
                new ComboBox<>();

        specialty.setPromptText(
                "Specialty"
        );

        specialty.setPrefHeight(43);

        specialty.setMaxWidth(
                Double.MAX_VALUE
        );

        java.util.Set<String> uniqueSpecs = new java.util.TreeSet<>();
        for (DoctorProfile d : doctors) {
            if (d.getSpecialization() != null && !d.getSpecialization().isBlank()) {
                uniqueSpecs.add(d.getSpecialization().trim());
            }
        }
        if (uniqueSpecs.isEmpty()) {
            uniqueSpecs.addAll(java.util.List.of("Cardiology", "Dermatology", "General Medicine", "Neurology", "Orthopedics", "Pediatrics"));
        }
        specialty.getItems().addAll(uniqueSpecs);

        // =====================================================
        // HOSPITAL
        // =====================================================

        ComboBox<String> hospital =
                new ComboBox<>();

        hospital.setPromptText(
                "Select hospital"
        );

        hospital.setPrefHeight(43);

        hospital.setMaxWidth(
                Double.MAX_VALUE
        );

        for (HospitalProfile hospitalProfile :
                hospitals) {

            String hospitalName =
                    hospitalProfile
                            .getHospitalName();

            if (hospitalName != null &&
                    !hospitalName.isBlank()) {

                hospital.getItems().add(
                        hospitalName
                );
            }
        }

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
                        new javafx.scene.control.DateCell() {

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

                                    setDisable(
                                            true
                                    );
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

        reason.setWrapText(
                true
        );

        reason.setStyle(
                "-fx-background-color: #f8fafc;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        // =====================================================
        // DOCTOR SECTION
        // =====================================================

        VBox doctorSection =
                new VBox(
                        7,

                        label("Doctor"),

                        doctor,

                        label("Specialty"),

                        specialty
                );

        // =====================================================
        // HOSPITAL SECTION
        // =====================================================

        VBox hospitalSection =
                new VBox(
                        7,

                        label("Hospital"),

                        hospital
                );

        // Initially hidden
        doctorSection.setVisible(
                false
        );

        doctorSection.setManaged(
                false
        );

        hospitalSection.setVisible(
                false
        );

        hospitalSection.setManaged(
                false
        );

        // =====================================================
        // DOCTOR SELECTION
        // =====================================================

        specialty.setOnAction(event -> {
            String selectedSpec = specialty.getValue();
            if (selectedSpec != null && !selectedSpec.isBlank()) {
                doctor.getItems().clear();
                for (DoctorProfile d : doctors) {
                    if (selectedSpec.equalsIgnoreCase(d.getSpecialization())) {
                        String docName = getDoctorFullName(d);
                        if (!docName.isBlank()) {
                            doctor.getItems().add(docName);
                        }
                    }
                }
            }
        });

        doctor.setOnAction(
                event -> {
                    String selectedDoctorName = doctor.getValue();
                    if (selectedDoctorName == null) return;

                    DoctorProfile selectedDoctor = findDoctorByName(selectedDoctorName);
                    if (selectedDoctor != null && selectedDoctor.getSpecialization() != null && !selectedDoctor.getSpecialization().isBlank()) {
                        specialty.setValue(selectedDoctor.getSpecialization().trim());
                    }
                }
        );

        // =====================================================
        // BOOKING TYPE CHANGE
        // =====================================================

        bookingType.setOnAction(
                event -> {

                    String selectedType =
                            bookingType.getValue();

                    if (selectedType == null) {

                        doctorSection.setVisible(
                                false
                        );

                        doctorSection.setManaged(
                                false
                        );

                        hospitalSection.setVisible(
                                false
                        );

                        hospitalSection.setManaged(
                                false
                        );

                        return;
                    }

                    if (selectedType.equals(
                            "Book a Doctor"
                    )) {

                        // Show doctor section
                        doctorSection.setVisible(
                                true
                        );

                        doctorSection.setManaged(
                                true
                        );

                        // Hide hospital section
                        hospitalSection.setVisible(
                                false
                        );

                        hospitalSection.setManaged(
                                false
                        );

                        // Clear hospital
                        hospital.setValue(
                                null
                        );

                    } else if (selectedType.equals(
                            "Book a Hospital"
                    )) {

                        // Hide doctor section
                        doctorSection.setVisible(
                                false
                        );

                        doctorSection.setManaged(
                                false
                        );

                        // Show hospital section
                        hospitalSection.setVisible(
                                true
                        );

                        hospitalSection.setManaged(
                                true
                        );

                        // Clear doctor data
                        doctor.setValue(
                                null
                        );

                        specialty.setValue(
                                null
                        );
                    }
                }
        );

        if (preselectedDoctor != null) {
            bookingType.setValue("Book a Doctor");
            String docName = getDoctorFullName(preselectedDoctor);
            if (doctor.getItems().contains(docName)) {
                doctor.setValue(docName);
            } else if (!docName.isBlank()) {
                doctor.getItems().add(docName);
                doctor.setValue(docName);
            }
            if (preselectedDoctor.getSpecialization() != null && !preselectedDoctor.getSpecialization().isBlank()) {
                specialty.getItems().add(preselectedDoctor.getSpecialization().trim());
                specialty.setValue(preselectedDoctor.getSpecialization().trim());
            }
        } else if (preselectedHospital != null || (preselectedHospitalName != null && !preselectedHospitalName.isBlank())) {
            bookingType.setValue("Book a Hospital");
            String hospName = preselectedHospital != null ? preselectedHospital.getHospitalName() : preselectedHospitalName;
            if (hospName != null && !hospName.isBlank()) {
                if (hospital.getItems().contains(hospName)) {
                    hospital.setValue(hospName);
                } else {
                    hospital.getItems().add(hospName);
                    hospital.setValue(hospName);
                }
            }
        }

        // =====================================================
        // DATE + TIME SECTION
        // =====================================================

        VBox scheduleSection =
                new VBox(
                        7,

                        label("Appointment Date"),

                        date,

                        label("Appointment Time"),

                        time
                );

        // =====================================================
        // ACTION BUTTONS
        // =====================================================

        Button confirm =
                PatientUI.button(

                        "Confirm Appointment",

                        () -> confirmAppointment(

                                bookingType,

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

        // =====================================================
        // ADD FORM CONTENT
        // =====================================================

        form.getChildren().addAll(

                label(
                        "Patient Name"
                ),

                patientName,

                label(
                        "Booking Type"
                ),

                bookingType,

                doctorSection,

                hospitalSection,

                scheduleSection,

                label(
                        "Reason for Visit"
                ),

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
        // RETURN COMMON PATIENT UI
        // =====================================================

        return PatientUI.createScene(

                stage,

                "Book Appointment",

                "Book Appointment",

                "Choose a doctor or hospital, date and convenient time.",

                content
        );
    }

    // =========================================================
    // CONFIRM APPOINTMENT
    // =========================================================

    private void confirmAppointment(

            ComboBox<String> bookingType,

            ComboBox<String> doctor,

            ComboBox<String> specialty,

            ComboBox<String> hospital,

            DatePicker date,

            ComboBox<String> time,

            TextArea reason) {

        // =====================================================
        // BOOKING TYPE VALIDATION
        // =====================================================

        if (bookingType.getValue() == null) {

            showError(
                    "Please select a booking type."
            );

            return;
        }

        // =====================================================
        // DATE VALIDATION
        // =====================================================

        if (date.getValue() == null) {

            showError(
                    "Please select an appointment date."
            );

            return;
        }

        if (date.getValue().isBefore(
                LocalDate.now()
        )) {

            showError(
                    "Appointment date cannot be in the past."
            );

            return;
        }

        // =====================================================
        // TIME VALIDATION
        // =====================================================

        if (time.getValue() == null) {

            showError(
                    "Please select an appointment time."
            );

            return;
        }

        try {

            // =================================================
            // DOCTOR APPOINTMENT
            // =================================================

            if (bookingType.getValue().equals(
                    "Book a Doctor"
            )) {

                if (doctor.getValue() == null) {

                    showError(
                            "Please select a doctor."
                    );

                    return;
                }

                DoctorProfile selectedDoctor =
                        findDoctorByName(
                                doctor.getValue()
                        );

                if (selectedDoctor == null) {

                    showError(
                            "Selected doctor could not be found."
                    );

                    return;
                }

                String doctorUid =
                        selectedDoctor.getUid();

                if (doctorUid == null ||
                        doctorUid.isBlank()) {

                    showError(
                            "Selected doctor UID is missing."
                    );

                    return;
                }

                String doctorName =
                        getDoctorFullName(
                                selectedDoctor
                        );

                String doctorSpecialty =
                        selectedDoctor
                                .getSpecialization();

                if (appointmentController.isSlotBooked(doctorUid, date.getValue().toString(), time.getValue())) {
                    showError("This time slot is already booked. Please choose another time or date.");
                    return;
                }

                appointmentController
                        .createDoctorAppointment(

                                doctorUid,

                                doctorName,

                                doctorSpecialty,

                                date.getValue()
                                        .toString(),

                                time.getValue(),

                                reason.getText()
                        );
            }

            // =================================================
            // HOSPITAL APPOINTMENT
            // =================================================

            else if (bookingType.getValue().equals(
                    "Book a Hospital"
            )) {

                if (hospital.getValue() == null) {

                    showError(
                            "Please select a hospital."
                    );

                    return;
                }

                HospitalProfile selectedHospital =
                        findHospitalByName(
                                hospital.getValue()
                        );

                if (selectedHospital == null) {

                    showError(
                            "Selected hospital could not be found."
                    );

                    return;
                }

                String hospitalId =
                        selectedHospital.getUid();

                if (hospitalId == null ||
                        hospitalId.isBlank()) {

                    showError(
                            "Selected hospital UID is missing."
                    );

                    return;
                }

                String hospitalName =
                        selectedHospital
                                .getHospitalName();

                if (appointmentController.isSlotBooked(hospitalId, date.getValue().toString(), time.getValue())) {
                    showError("This time slot is already booked. Please choose another time or date.");
                    return;
                }

                appointmentController
                        .createHospitalAppointment(

                                hospitalId,

                                hospitalName,

                                date.getValue()
                                        .toString(),

                                time.getValue(),

                                reason.getText()
                        );
            }

            // =================================================
            // SUCCESS
            // =================================================

            showSuccess();

        } catch (Exception e) {

            e.printStackTrace();

            showError(

                    "Unable to book the appointment.\n\n"

                            + e.getMessage()
            );
        }
    }

    // =========================================================
    // LOAD HOSPITALS
    // =========================================================

    private void loadHospitals() {

        try {

            hospitals =
                    hospitalDAO
                            .getAllHospitals();

            System.out.println(
                    "Hospitals loaded: "
                            + hospitals.size()
            );

            for (HospitalProfile hospital :
                    hospitals) {

                System.out.println(

                        "Hospital: "
                                + hospital.getHospitalName()

                                + " | UID: "
                                + hospital.getUid()
                );

            }

        } catch (Exception e) {

            System.err.println(

                    "Unable to load hospitals: "
                            + e.getMessage()
            );

            e.printStackTrace();

            hospitals =
                    new ArrayList<>();
        }
    }

    // =========================================================
    // LOAD DOCTORS
    // =========================================================

    private void loadDoctors() {

        try {

            doctors =
                    doctorDAO
                            .getAllDoctors();

            System.out.println(
                    "Doctors loaded: "
                            + doctors.size()
            );

            for (DoctorProfile doctor :
                    doctors) {

                System.out.println(

                        "Doctor: "
                                + getDoctorFullName(
                                        doctor
                                )

                                + " | UID: "
                                + doctor.getUid()

                                + " | Specialization: "
                                + doctor.getSpecialization()
                );
            }

        } catch (Exception e) {

            System.err.println(

                    "Unable to load doctors: "
                            + e.getMessage()
            );

            e.printStackTrace();

            doctors =
                    new ArrayList<>();
        }
    }

    // =========================================================
    // FIND DOCTOR BY NAME
    // =========================================================

    private DoctorProfile findDoctorByName(
            String doctorName) {

        if (doctorName == null) {
            return null;
        }

        for (DoctorProfile doctor :
                doctors) {

            String fullName =
                    getDoctorFullName(
                            doctor
                    );

            if (fullName.equalsIgnoreCase(
                    doctorName
            )) {

                return doctor;
            }
        }

        return null;
    }

    // =========================================================
    // FIND HOSPITAL BY NAME
    // =========================================================

    private HospitalProfile findHospitalByName(
            String hospitalName) {

        if (hospitalName == null) {
            return null;
        }

        for (HospitalProfile hospital :
                hospitals) {

            String name =
                    hospital.getHospitalName();

            if (name != null &&
                    name.equalsIgnoreCase(
                            hospitalName
                    )) {

                return hospital;
            }
        }

        return null;
    }

    // =========================================================
    // GET DOCTOR FULL NAME
    // =========================================================

    private String getDoctorFullName(
            DoctorProfile doctor) {

        if (doctor == null) {
            return "";
        }

        String firstName =
                doctor.getFirstName() == null
                        ? ""
                        : doctor
                                .getFirstName()
                                .trim();

        String lastName =
                doctor.getLastName() == null
                        ? ""
                        : doctor
                                .getLastName()
                                .trim();

        String fullName =
                (firstName + " " + lastName)
                        .trim();

        if (fullName.isBlank()) {
            return "";
        }

        return "Dr. " + fullName;
    }

    // =========================================================
    // LOAD PATIENT NAME
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
                            : profile
                                    .getFirstName()
                                    .trim();

            String lastName =
                    profile.getLastName() == null
                            ? ""
                            : profile
                                    .getLastName()
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

            System.err.println(

                    "Unable to load patient name: "
                            + e.getMessage()
            );

            patientName.setText(
                    "Patient"
            );
        }
    }

    // =========================================================
    // SUCCESS SCREEN
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
                        "Appointment Confirmed"
                );

        title.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label description =
                new Label(
                        "Your appointment request has been successfully saved."
                );

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
    // ERROR SCREEN
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
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label description =
                new Label(message);

        description.setWrapText(
                true
        );

        description.setMaxWidth(
                600
        );

        description.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #64748b;"
        );

        Button back =
                PatientUI.secondaryButton(

                        "Back",

                        this::showBookAppointment
                );

        content.getChildren().addAll(

                icon,

                title,

                description,

                back
        );

        VBox wrapper =
                new VBox(content);

        wrapper.setAlignment(
                Pos.CENTER
        );

        stage.setScene(

                PatientUI.createScene(

                        stage,

                        "Appointment Error",

                        "Appointment Error",

                        "Please review the appointment details and try again.",

                        wrapper
                )
        );

        stage.show();
    }

    // =========================================================
    // TEXT FIELD
    // =========================================================

    private TextField field(
            String prompt) {

        TextField field =
                new TextField();

        field.setPromptText(
                prompt
        );

        field.setPrefHeight(
                43
        );

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
            String text) {

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
            String path) {

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
            double height) {

        ImageView view =
                new ImageView();

        var resource =
                getClass()
                        .getResource(path);

        if (resource == null) {

            System.err.println(
                    "Appointment image not found: "
                            + path
            );

            view.setFitWidth(
                    width
            );

            view.setFitHeight(
                    height
            );

            return view;
        }

        Image image =
                new Image(
                        resource.toExternalForm()
                );

        view.setImage(
                image
        );

        view.setFitWidth(
                width
        );

        view.setFitHeight(
                height
        );

        view.setPreserveRatio(
                false
        );

        return view;
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

    private void showBookAppointment() {

        stage.setScene(

                new BookAppointment(stage)
                        .getScene()
        );

        stage.show();
    }
}