
package com.healthsphere.view.Patient;

import java.util.ArrayList;
import java.util.List;

import com.healthsphere.controller.patient.HospitalController;
import com.healthsphere.controller.patient.ReviewController;
import com.healthsphere.dao.authentication.DoctorDAO;
import com.healthsphere.model.DoctorProfile;
import com.healthsphere.model.HospitalProfile;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SearchHospitals {

    private final Stage stage;
    private final HospitalController hospitalController;
    private final DoctorDAO doctorDAO;
    private final ReviewController reviewController;

    private List<HospitalProfile> allHospitals =
            new ArrayList<>();

    private List<DoctorProfile> allDoctors =
            new ArrayList<>();

    private VBox hospitalResults;
    private VBox doctorResults;

    private TextField hospitalSearchField;
    private TextField doctorSearchField;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SearchHospitals(Stage stage) {

        this.stage = stage;

        this.hospitalController =
                new HospitalController();

        this.doctorDAO =
                new DoctorDAO();

        this.reviewController =
                new ReviewController();
    }

    // =========================================================
    // MAIN SCENE
    // =========================================================

    public Scene getScene() {

        VBox content = new VBox(20);

        content.setFillWidth(true);
        content.setMinWidth(0);
        content.setMaxWidth(Double.MAX_VALUE);

        content.setPadding(
                new Insets(5)
        );

        // =====================================================
        // TWO COLUMN CONTAINER
        // =====================================================

        HBox mainContainer = new HBox(20);

        mainContainer.setAlignment(
                Pos.TOP_CENTER
        );

        mainContainer.setFillHeight(true);
        mainContainer.setMinWidth(0);

        mainContainer.setMaxWidth(
                Double.MAX_VALUE
        );

        // =====================================================
        // LEFT - HOSPITAL SECTION
        // =====================================================

        VBox hospitalSection = new VBox(15);

        hospitalSection.setMinWidth(0);

        hospitalSection.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox.setHgrow(
                hospitalSection,
                Priority.ALWAYS
        );

        // =====================================================
        // HOSPITAL SEARCH CARD
        // =====================================================

        VBox hospitalSearchCard =
                PatientUI.card(
                        "Find a Hospital"
                );

        hospitalSearchCard.setMinWidth(0);

        hospitalSearchCard.setMaxWidth(
                Double.MAX_VALUE
        );

        Label hospitalInstruction =
                new Label(
                        "Search for hospitals, clinics and healthcare facilities."
                );

        hospitalInstruction.setWrapText(true);

        hospitalInstruction.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 14px;"
        );

        // =====================================================
        // HOSPITAL SEARCH FIELD
        // =====================================================

        hospitalSearchField =
                new TextField();

        hospitalSearchField.setPromptText(
                "Hospital name, location or type"
        );

        hospitalSearchField.setPrefHeight(42);

        hospitalSearchField.setMinWidth(0);

        hospitalSearchField.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox.setHgrow(
                hospitalSearchField,
                Priority.ALWAYS
        );

        hospitalSearchField.setStyle(
                "-fx-background-color: #f8fafc;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10;"
        );

        Button hospitalSearchButton =
                PatientUI.button(
                        "Search",
                        () -> filterHospitals(
                                hospitalSearchField.getText()
                        )
                );

        Button hospitalClearButton =
                PatientUI.secondaryButton(
                        "Clear",
                        () -> {

                            hospitalSearchField.clear();

                            displayHospitals(
                                    allHospitals
                            );
                        }
                );

        HBox hospitalSearchRow =
                new HBox(8);

        hospitalSearchRow.setAlignment(
                Pos.CENTER_LEFT
        );

        hospitalSearchRow.setFillHeight(true);

        hospitalSearchRow.setMinWidth(0);

        hospitalSearchRow.setMaxWidth(
                Double.MAX_VALUE
        );

        hospitalSearchRow.getChildren().addAll(
                hospitalSearchField,
                hospitalSearchButton,
                hospitalClearButton
        );

        hospitalSearchField.setOnAction(
                event -> filterHospitals(
                        hospitalSearchField.getText()
                )
        );

        hospitalSearchCard.getChildren().addAll(
                hospitalInstruction,
                hospitalSearchRow
        );

        // =====================================================
        // HOSPITAL RESULTS
        // =====================================================

        hospitalResults =
                PatientUI.card(
                        "Hospital Results"
                );

        hospitalResults.setFillWidth(true);

        hospitalResults.setMinWidth(0);

        hospitalResults.setMaxWidth(
                Double.MAX_VALUE
        );

        // =====================================================
        // HOSPITAL COLUMN
        // =====================================================

        hospitalSection.getChildren().addAll(
                hospitalSearchCard,
                hospitalResults
        );

        // =====================================================
        // RIGHT - DOCTOR SECTION
        // =====================================================

        VBox doctorSection = new VBox(15);

        doctorSection.setMinWidth(0);

        doctorSection.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox.setHgrow(
                doctorSection,
                Priority.ALWAYS
        );

        // =====================================================
        // DOCTOR SEARCH CARD
        // =====================================================

        VBox doctorSearchCard =
                PatientUI.card(
                        "Find a Doctor"
                );

        doctorSearchCard.setMinWidth(0);

        doctorSearchCard.setMaxWidth(
                Double.MAX_VALUE
        );

        Label doctorInstruction =
                new Label(
                        "Search doctors by name, specialization or hospital."
                );

        doctorInstruction.setWrapText(true);

        doctorInstruction.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 14px;"
        );

        // =====================================================
        // DOCTOR SEARCH FIELD
        // =====================================================

        doctorSearchField =
                new TextField();

        doctorSearchField.setPromptText(
                "Doctor name, specialization or hospital"
        );

        doctorSearchField.setPrefHeight(42);

        doctorSearchField.setMinWidth(0);

        doctorSearchField.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox.setHgrow(
                doctorSearchField,
                Priority.ALWAYS
        );

        doctorSearchField.setStyle(
                "-fx-background-color: #f8fafc;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10;"
        );

        Button doctorSearchButton =
                PatientUI.button(
                        "Search",
                        () -> filterDoctors(
                                doctorSearchField.getText()
                        )
                );

        Button doctorClearButton =
                PatientUI.secondaryButton(
                        "Clear",
                        () -> {

                            doctorSearchField.clear();

                            displayDoctors(
                                    allDoctors
                            );
                        }
                );

        HBox doctorSearchRow =
                new HBox(8);

        doctorSearchRow.setAlignment(
                Pos.CENTER_LEFT
        );

        doctorSearchRow.setFillHeight(true);

        doctorSearchRow.setMinWidth(0);

        doctorSearchRow.setMaxWidth(
                Double.MAX_VALUE
        );

        doctorSearchRow.getChildren().addAll(
                doctorSearchField,
                doctorSearchButton,
                doctorClearButton
        );

        doctorSearchField.setOnAction(
                event -> filterDoctors(
                        doctorSearchField.getText()
                )
        );

        doctorSearchCard.getChildren().addAll(
                doctorInstruction,
                doctorSearchRow
        );

        // =====================================================
        // DOCTOR RESULTS
        // =====================================================

        doctorResults =
                PatientUI.card(
                        "Doctor Results"
                );

        doctorResults.setFillWidth(true);

        doctorResults.setMinWidth(0);

        doctorResults.setMaxWidth(
                Double.MAX_VALUE
        );

        // =====================================================
        // DOCTOR COLUMN
        // =====================================================

        doctorSection.getChildren().addAll(
                doctorSearchCard,
                doctorResults
        );

        // =====================================================
        // BOTH COLUMNS
        // =====================================================

        mainContainer.getChildren().addAll(
                hospitalSection,
                doctorSection
        );

        content.getChildren().add(
                mainContainer
        );

        // =====================================================
        // LOAD DATA
        // =====================================================

        loadHospitals();
        loadDoctors();

        // =====================================================
        // CREATE PATIENT SCENE
        // =====================================================

        return PatientUI.createScene(
                stage,
                "Search Hospitals",
                "Search Hospitals & Doctors",
                "Find hospitals and doctors in one place.",
                content
        );
    }

    // =========================================================
    // LOAD HOSPITALS
    // =========================================================

    private void loadHospitals() {

        try {

            System.out.println(
                    "Loading hospitals..."
            );

            allHospitals =
                    hospitalController
                            .getAllHospitals();

            if (allHospitals == null) {

                allHospitals =
                        new ArrayList<>();
            }

            System.out.println(
                    "Hospitals loaded: "
                            + allHospitals.size()
            );

            displayHospitals(
                    allHospitals
            );

        } catch (Exception e) {

            e.printStackTrace();

            showHospitalError(
                    "Unable to load hospitals."
            );
        }
    }

    // =========================================================
    // LOAD DOCTORS
    // =========================================================

    private void loadDoctors() {

        try {

            System.out.println(
                    "Loading doctors from Firestore..."
            );

            allDoctors =
                    doctorDAO.getAllDoctors();

            if (allDoctors == null) {

                allDoctors =
                        new ArrayList<>();
            }

            System.out.println(
                    "Doctors loaded: "
                            + allDoctors.size()
            );

            for (DoctorProfile doctor :
                    allDoctors) {

                if (doctor == null) {
                    continue;
                }

                System.out.println(
                        "Doctor: "
                                + getDoctorFullName(
                                        doctor
                                )
                );
            }

            displayDoctors(
                    allDoctors
            );

        } catch (Exception e) {

            e.printStackTrace();

            showDoctorError(
                    "Unable to load doctors from Firestore."
            );
        }
    }

    // =========================================================
    // FILTER HOSPITALS
    // =========================================================

    private void filterHospitals(
            String searchText) {

        if (searchText == null ||
                searchText.trim().isEmpty()) {

            displayHospitals(
                    allHospitals
            );

            return;
        }

        try {

            List<HospitalProfile> filtered =
                    hospitalController
                            .searchHospitals(
                                    searchText.trim()
                            );

            if (filtered == null) {

                filtered =
                        new ArrayList<>();
            }

            displayHospitals(
                    filtered
            );

        } catch (Exception e) {

            e.printStackTrace();

            showHospitalError(
                    "Unable to search hospitals."
            );
        }
    }

    // =========================================================
    // FILTER DOCTORS
    // =========================================================

    private void filterDoctors(
            String searchText) {

        if (searchText == null) {

            displayDoctors(
                    allDoctors
            );

            return;
        }

        String search =
                normalizeSearchText(
                        searchText
                );

        if (search.isEmpty()) {

            displayDoctors(
                    allDoctors
            );

            return;
        }

        List<DoctorProfile> filtered =
                new ArrayList<>();

        for (DoctorProfile doctor :
                allDoctors) {

            if (doctor == null) {
                continue;
            }

            String firstName =
                    normalizeSearchText(
                            doctor.getFirstName()
                    );

            String lastName =
                    normalizeSearchText(
                            doctor.getLastName()
                    );

            String fullName =
                    normalizeSearchText(
                            getDoctorFullName(
                                    doctor
                            )
                    );

            String specialization =
                    normalizeSearchText(
                            doctor.getSpecialization()
                    );

            String hospital =
                    normalizeSearchText(
                            doctor.getHospitalAffiliation()
                    );

            String email =
                    normalizeSearchText(
                            doctor.getEmail()
                    );

            String phone =
                    normalizeSearchText(
                            doctor.getPhone()
                    );

            if (firstName.contains(search) ||
                    lastName.contains(search) ||
                    fullName.contains(search) ||
                    specialization.contains(search) ||
                    hospital.contains(search) ||
                    email.contains(search) ||
                    phone.contains(search)) {

                filtered.add(
                        doctor
                );
            }
        }

        System.out.println(
                "Doctor search: '"
                        + searchText
                        + "' -> "
                        + filtered.size()
                        + " result(s)"
        );

        displayDoctors(
                filtered
        );
    }

    // =========================================================
    // NORMALIZE SEARCH TEXT
    // =========================================================

    private String normalizeSearchText(
            String value) {

        if (value == null) {
            return "";
        }

        String result =
                value
                        .trim()
                        .toLowerCase();

        result =
                result.replace(
                        "dr.",
                        ""
                );

        result =
                result.replace(
                        "dr ",
                        ""
                );

        result =
                result.replaceAll(
                        "\\s+",
                        " "
                );

        return result.trim();
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
                safeDisplay(
                        doctor.getFirstName(),
                        ""
                );

        String lastName =
                safeDisplay(
                        doctor.getLastName(),
                        ""
                );

        return (
                firstName
                        + " "
                        + lastName
        ).trim();
    }

    // =========================================================
    // DISPLAY HOSPITALS
    // =========================================================

    private void displayHospitals(
            List<HospitalProfile> hospitals) {

        if (hospitalResults == null) {
            return;
        }

        hospitalResults
                .getChildren()
                .clear();

        if (hospitals == null ||
                hospitals.isEmpty()) {

            Label empty =
                    new Label(
                            "No hospitals found."
                    );

            empty.setStyle(
                    "-fx-text-fill: #64748b;" +
                    "-fx-font-size: 14px;" +
                    "-fx-padding: 15;"
            );

            hospitalResults
                    .getChildren()
                    .add(empty);

            return;
        }

        for (HospitalProfile hospital :
                hospitals) {

            if (hospital == null) {
                continue;
            }

            hospitalResults
                    .getChildren()
                    .add(
                            createHospitalCard(
                                    hospital
                            )
                    );
        }
    }

    // =========================================================
    // DISPLAY DOCTORS
    // =========================================================

    private void displayDoctors(
            List<DoctorProfile> doctors) {

        if (doctorResults == null) {
            return;
        }

        doctorResults
                .getChildren()
                .clear();

        if (doctors == null ||
                doctors.isEmpty()) {

            Label empty =
                    new Label(
                            "No doctors found."
                    );

            empty.setStyle(
                    "-fx-text-fill: #64748b;" +
                    "-fx-font-size: 14px;" +
                    "-fx-padding: 15;"
            );

            doctorResults
                    .getChildren()
                    .add(empty);

            return;
        }

        for (DoctorProfile doctor :
                doctors) {

            if (doctor == null) {
                continue;
            }

            doctorResults
                    .getChildren()
                    .add(
                            createDoctorCard(
                                    doctor
                            )
                    );
        }
    }

    // =========================================================
    // HOSPITAL CARD
    // =========================================================

    private HBox createHospitalCard(
            HospitalProfile hospital) {

        HBox card =
                new HBox(15);

        card.setPadding(
                new Insets(14)
        );

        card.setAlignment(
                Pos.CENTER_LEFT
        );

        card.setMinWidth(0);

        card.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox.setHgrow(
                card,
                Priority.ALWAYS
        );

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 12;"
        );

        // =====================================================
        // IMAGE
        // =====================================================

        ImageView image =
                createHospitalImage();

        image.setFitWidth(105);
        image.setFitHeight(75);
        image.setPreserveRatio(false);

        // =====================================================
        // INFORMATION
        // =====================================================

        VBox information =
                new VBox(6);

        information.setAlignment(
                Pos.CENTER_LEFT
        );

        information.setMinWidth(0);

        information.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox.setHgrow(
                information,
                Priority.ALWAYS
        );

        // =====================================================
        // NAME
        // =====================================================

        Label nameLabel =
                new Label(
                        safeDisplay(
                                hospital.getHospitalName(),
                                "Hospital"
                        )
                );

        nameLabel.setWrapText(true);

        nameLabel.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        // =====================================================
        // ADDRESS
        // =====================================================

        Label addressLabel =
                new Label(
                        "📍 "
                                + safeDisplay(
                                        hospital.getAddress(),
                                        "Address not available"
                                )
                );

        addressLabel.setWrapText(true);

        addressLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        // =====================================================
        // TYPE
        // =====================================================

        Label typeLabel =
                new Label(
                        safeDisplay(
                                hospital.getHospitalType(),
                                "Healthcare Facility"
                        )
                );

        typeLabel.setWrapText(true);

        typeLabel.setStyle(
                "-fx-text-fill: #2563eb;" +
                "-fx-font-weight: bold;"
        );

        Label contactLabel =
                new Label(
                        "☎ "
                                + safeDisplay(
                                        hospital.getContact(),
                                        "Not available"
                                )
                );

        contactLabel.setWrapText(true);

        contactLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        // =====================================================
        // RATING
        // =====================================================

        String hospRatingText = reviewController.getFormattedRatingText("HOSPITAL", hospital.getUid());

        Label hospRatingLabel =
                new Label(
                        hospRatingText
                );

        hospRatingLabel.setWrapText(true);

        hospRatingLabel.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #d97706;" +
                "-fx-padding: 2 0 2 0;"
        );

        information.getChildren().addAll(
                nameLabel,
                addressLabel,
                typeLabel,
                hospRatingLabel,
                contactLabel
        );

        // =====================================================
        // BUTTONS
        // =====================================================

        Button bookButton =
                PatientUI.button(
                        "Book Appointment",
                        () -> showBookHospital(
                                hospital
                        )
                );

        Button viewButton =
                PatientUI.secondaryButton(
                        "View Details",
                        () -> showHospitalDetails(
                                hospital
                        )
                );

        HBox buttonContainer =
                new HBox(8);

        buttonContainer.setAlignment(
                Pos.CENTER_RIGHT
        );

        buttonContainer.getChildren().addAll(
                bookButton,
                viewButton
        );

        card.getChildren().addAll(
                image,
                information,
                buttonContainer
        );

        return card;
    }

    // =========================================================
    // DOCTOR CARD
    // =========================================================

    private HBox createDoctorCard(
            DoctorProfile doctor) {

        HBox card =
                new HBox(15);

        card.setPadding(
                new Insets(14)
        );

        card.setAlignment(
                Pos.CENTER_LEFT
        );

        card.setMinWidth(0);

        card.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox.setHgrow(
                card,
                Priority.ALWAYS
        );

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 12;"
        );

        // =====================================================
        // DOCTOR IMAGE
        // =====================================================

        ImageView image =
                createDoctorImage();

        image.setFitWidth(105);
        image.setFitHeight(75);
        image.setPreserveRatio(false);

        // =====================================================
        // INFORMATION
        // =====================================================

        VBox information =
                new VBox(6);

        information.setAlignment(
                Pos.CENTER_LEFT
        );

        information.setMinWidth(0);

        information.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox.setHgrow(
                information,
                Priority.ALWAYS
        );

        // =====================================================
        // DOCTOR NAME
        // =====================================================

        String doctorName =
                getDoctorFullName(
                        doctor
                );

        if (doctorName.isEmpty()) {
            doctorName = "Doctor";
        }

        Label nameLabel =
                new Label(
                        "Dr. " + doctorName
                );

        nameLabel.setWrapText(true);

        nameLabel.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        // =====================================================
        // SPECIALIZATION
        // =====================================================

        Label specializationLabel =
                new Label(
                        safeDisplay(
                                doctor.getSpecialization(),
                                "General Medicine"
                        )
                );

        specializationLabel.setWrapText(true);

        specializationLabel.setStyle(
                "-fx-text-fill: #2563eb;" +
                "-fx-font-weight: bold;"
        );

        // =====================================================
        // EXPERIENCE
        // =====================================================

        Label experienceLabel =
                new Label(
                        "Experience: "
                                + safeDisplay(
                                        doctor.getExperience(),
                                        "Not available"
                                )
                );

        experienceLabel.setWrapText(true);

        experienceLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        Label hospitalLabel =
                new Label(
                        "🏥 "
                                + safeDisplay(
                                        doctor.getHospitalAffiliation(),
                                        "Not specified"
                                )
                );

        hospitalLabel.setWrapText(true);

        hospitalLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        // =====================================================
        // RATING
        // =====================================================

        String docRatingText = reviewController.getFormattedRatingText("DOCTOR", doctor.getUid());

        Label docRatingLabel =
                new Label(
                        docRatingText
                );

        docRatingLabel.setWrapText(true);

        docRatingLabel.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #d97706;" +
                "-fx-padding: 2 0 2 0;"
        );

        information.getChildren().addAll(
                nameLabel,
                specializationLabel,
                docRatingLabel,
                experienceLabel,
                hospitalLabel
        );

        // =====================================================
        // BUTTONS
        // =====================================================

        Button bookButton =
                PatientUI.button(
                        "Book Appointment",
                        () -> showBookDoctor(
                                doctor
                        )
                );

        Button viewButton =
                PatientUI.secondaryButton(
                        "View Details",
                        () -> showDoctorDetails(
                                doctor
                        )
                );

        HBox buttonContainer =
                new HBox(8);

        buttonContainer.setAlignment(
                Pos.CENTER_RIGHT
        );

        buttonContainer.getChildren().addAll(
                bookButton,
                viewButton
        );

        card.getChildren().addAll(
                image,
                information,
                buttonContainer
        );

        return card;
    }

    // =========================================================
    // HOSPITAL DETAILS
    // =========================================================

    private void showHospitalDetails(
            HospitalProfile hospital) {

        String name =
                safeDisplay(
                        hospital.getHospitalName(),
                        "Hospital"
                );

        String location =
                safeDisplay(
                        hospital.getAddress(),
                        "Address not available"
                );

        String speciality =
                safeDisplay(
                        hospital.getHospitalType(),
                        "Healthcare Facility"
                );

        // =====================================================
        // GET ACTUAL RATING
        // =====================================================

        String rating =
                reviewController.getFormattedRatingText(
                        "HOSPITAL",
                        hospital.getUid()
                );

        stage.setScene(
                new HospitalDetails(
                        stage,
                        name,
                        location,
                        speciality,
                        rating,
                        getSpecialties(
                                hospital
                        )
                ).getScene()
        );

        stage.show();

        if (!stage.isMaximized()) {
            stage.setMaximized(true);
        }
    }

    // =========================================================
    // DOCTOR DETAILS
    // =========================================================

    private void showDoctorDetails(
            DoctorProfile doctor) {

        String doctorName =
                getDoctorFullName(
                        doctor
                );

        if (doctorName.isEmpty()) {
            doctorName = "Doctor";
        }

        VBox details =
                new VBox(12);

        details.setPadding(
                new Insets(20)
        );

        details.setMaxWidth(
                Double.MAX_VALUE
        );

        details.setStyle(
                "-fx-background-color: #ffffff;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #e2e8f0;" +
                "-fx-border-radius: 12;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 8, 0, 0, 2);"
        );

        // =====================================================
        // TITLE
        // =====================================================

        Label title =
                new Label(
                        "Dr. " + doctorName
                );

        title.setWrapText(true);

        title.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #172b4d;"
        );

        // =====================================================
        // RATING
        // =====================================================

        String ratingStr = reviewController.getFormattedRatingText("DOCTOR", doctor.getUid());

        Label rating =
                new Label(
                        ratingStr
                );

        rating.setWrapText(true);

        rating.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #d97706;" +
                "-fx-padding: 2 0 6 0;"
        );

        // =====================================================
        // DETAIL FIELDS WITH HIGH CONTRAST DARK TEXT (#172B4D / #475569)
        // =====================================================

        Label specialization = createDetailLabel("Specialization", doctor.getSpecialization(), "General Medicine");
        Label experience = createDetailLabel("Experience", doctor.getExperience(), "Not available");
        Label hospital = createDetailLabel("Hospital Affiliation", doctor.getHospitalAffiliation(), "Not specified");
        Label registration = createDetailLabel("Registration No", doctor.getRegistrationNumber(), "Not available");
        Label council = createDetailLabel("Medical Council", doctor.getMedicalCouncil(), "Not available");
        Label email = createDetailLabel("Email", doctor.getEmail(), "Not available");
        Label phone = createDetailLabel("Phone", doctor.getPhone(), "Not available");

        // =====================================================
        // BOOK BUTTON
        // =====================================================

        Button bookButton =
                PatientUI.button(
                        "Book Appointment",
                        () -> showBookDoctor(
                                doctor
                        )
                );

        // =====================================================
        // ADD DETAILS
        // =====================================================

        details.getChildren().addAll(
                title,
                rating,
                specialization,
                experience,
                hospital,
                registration,
                council,
                email,
                phone,
                bookButton
        );

        // =====================================================
        // REMOVE PREVIOUS DETAILS CARD
        // =====================================================

        if (doctorResults.getChildren().size() > 1) {

            if (doctorResults.getChildren().get(0)
                    instanceof VBox) {

                doctorResults
                        .getChildren()
                        .remove(0);
            }
        }

        // =====================================================
        // ADD DETAILS AT TOP
        // =====================================================

        doctorResults
                .getChildren()
                .add(
                        0,
                        details
                );
    }

    // =========================================================
    // HOSPITAL SPECIALTIES
    // =========================================================

    private String getSpecialties(
            HospitalProfile hospital) {

        String type =
                hospital.getHospitalType();

        if (type == null ||
                type.isBlank()) {

            return
                    "General Medicine, " +
                    "Cardiology, " +
                    "Orthopedics";
        }

        return type.trim();
    }

    // =========================================================
    // HOSPITAL IMAGE
    // =========================================================

    private ImageView createHospitalImage() {

        ImageView view =
                new ImageView();

        String[] images = {

                "/images/hospitals/hospital1.jpg",
                "/images/hospitals/hospital2.jpg",
                "/images/hospitals/hospital3.jpg",
                "/images/hospitals/hospital4.jpg"
        };

        for (String path : images) {

            var resource =
                    getClass().getResource(path);

            if (resource == null) {
                continue;
            }

            try {

                view.setImage(
                        new Image(
                                resource.toExternalForm()
                        )
                );

                return view;

            } catch (Exception e) {

                System.err.println(
                        "Unable to load hospital image: "
                                + path
                );
            }
        }

        return view;
    }

    // =========================================================
    // DOCTOR IMAGE
    // =========================================================

    private ImageView createDoctorImage() {

        ImageView view =
                new ImageView();

        String[] images = {

                "/images/doctors/doctor1.jpg",
                "/images/doctors/doctor2.jpg",
                "/images/doctors/doctor3.jpg",
                "/images/doctors/doctor4.jpg"
        };

        for (String path : images) {

            var resource =
                    getClass().getResource(path);

            if (resource == null) {
                continue;
            }

            try {

                view.setImage(
                        new Image(
                                resource.toExternalForm()
                        )
                );

                return view;

            } catch (Exception e) {

                System.err.println(
                        "Unable to load doctor image: "
                                + path
                );
            }
        }

        return view;
    }

    // =========================================================
    // HOSPITAL ERROR
    // =========================================================

    private void showHospitalError(
            String message) {

        if (hospitalResults == null) {
            return;
        }

        hospitalResults
                .getChildren()
                .clear();

        Label error =
                new Label(
                        message
                );

        error.setWrapText(true);

        error.setStyle(
                "-fx-text-fill: #dc2626;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 15;"
        );

        hospitalResults
                .getChildren()
                .add(error);
    }

    // =========================================================
    // DOCTOR ERROR
    // =========================================================

    private void showDoctorError(
            String message) {

        if (doctorResults == null) {
            return;
        }

        doctorResults
                .getChildren()
                .clear();

        Label error =
                new Label(
                        message
                );

        error.setWrapText(true);

        error.setStyle(
                "-fx-text-fill: #dc2626;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 15;"
        );

        doctorResults
                .getChildren()
                .add(error);
    }

    // =========================================================
    // SAFE DISPLAY
    // =========================================================

    private String safeDisplay(
            String value,
            String defaultValue) {

        if (value == null ||
                value.trim().isEmpty()) {

            return defaultValue;
        }

        return value.trim();
    }

    // =========================================================
    // SAFE RATING
    // =========================================================

    private String safeRating(
            String rating) {

        if (rating == null ||
                rating.trim().isEmpty()) {

            return "Not Rated";
        }

        return rating.trim() + " / 5";
    }

    // =========================================================
    // NAVIGATION TO BOOKING
    // =========================================================

    private void showBookDoctor(
            DoctorProfile doctor) {

        stage.setScene(
                new DoctorBooking(
                        stage,
                        doctor
                ).getScene()
        );

        stage.show();

        if (!stage.isMaximized()) {
            stage.setMaximized(true);
        }
    }

    private void showBookHospital(
            HospitalProfile hospital) {

        stage.setScene(
                new HospitalBooking(
                        stage,
                        hospital
                ).getScene()
        );

        stage.show();

        if (!stage.isMaximized()) {
            stage.setMaximized(true);
        }
    }

    private Label createDetailLabel(String title, String val, String fallback) {
        String displayVal = safeDisplay(val, fallback);
        Label label = new Label(title + ": " + displayVal);
        label.setWrapText(true);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #172b4d; -fx-padding: 2 0 2 0;");
        return label;
    }
}

