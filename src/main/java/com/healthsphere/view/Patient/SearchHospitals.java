package com.healthsphere.view.Patient;

import java.util.ArrayList;
import java.util.List;

import com.healthsphere.controller.patient.HospitalController;
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

    // Stores hospitals currently loaded from Firestore
    private List<HospitalProfile> allHospitals =
            new ArrayList<>();

    // Results container
    private VBox results;

    // Search field
    private TextField searchField;

    public SearchHospitals(Stage stage) {

        this.stage = stage;

        this.hospitalController =
                new HospitalController();
    }

    // =========================================================
    // MAIN SCENE
    // =========================================================

    public Scene getScene() {

        VBox content =
                new VBox(20);

        /*
         * PatientUI already creates the ScrollPane.
         *
         * Therefore DO NOT create another ScrollPane here.
         */

        content.setFillWidth(true);

        content.setMinWidth(0);

        content.setMaxWidth(
                Double.MAX_VALUE
        );

        content.setPadding(
                new Insets(5)
        );

        // =====================================================
        // SEARCH CARD
        // =====================================================

        VBox searchCard =
                PatientUI.card(
                        "Find a Hospital"
                );

        searchCard.setMinWidth(0);

        searchCard.setMaxWidth(
                Double.MAX_VALUE
        );

        Label instruction =
                new Label(
                        "Search for hospitals, clinics and healthcare facilities."
                );

        instruction.setWrapText(true);

        instruction.setMaxWidth(
                Double.MAX_VALUE
        );

        instruction.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 14px;"
        );

        // =====================================================
        // SEARCH ROW
        // =====================================================

        HBox searchRow =
                new HBox(12);

        searchRow.setAlignment(
                Pos.CENTER_LEFT
        );

        searchRow.setFillHeight(true);

        searchRow.setMinWidth(0);

        searchRow.setMaxWidth(
                Double.MAX_VALUE
        );

        // =====================================================
        // SEARCH FIELD
        // =====================================================

        searchField =
                new TextField();

        searchField.setPromptText(
                "Enter hospital name, location or hospital type"
        );

        searchField.setPrefHeight(42);

        searchField.setMinWidth(0);

        searchField.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox.setHgrow(
                searchField,
                Priority.ALWAYS
        );

        searchField.setStyle(
                "-fx-background-color: #f8fafc;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10;"
        );

        // =====================================================
        // SEARCH BUTTON
        // =====================================================

        Button searchButton =
                PatientUI.button(
                        "Search",
                        () -> filterHospitals(
                                searchField.getText()
                        )
                );

        // =====================================================
        // CLEAR BUTTON
        // =====================================================

        Button clearButton =
                PatientUI.secondaryButton(
                        "Clear",
                        () -> {

                            searchField.clear();

                            displayHospitals(
                                    allHospitals
                            );
                        }
                );

        searchRow.getChildren().addAll(
                searchField,
                searchButton,
                clearButton
        );

        searchCard.getChildren().addAll(
                instruction,
                searchRow
        );

        // =====================================================
        // SEARCH WHEN PRESSING ENTER
        // =====================================================

        searchField.setOnAction(
                event ->
                        filterHospitals(
                                searchField.getText()
                        )
        );

        // =====================================================
        // HOSPITAL RESULTS CARD
        // =====================================================

        results =
                PatientUI.card(
                        "Hospitals"
                );

        results.setFillWidth(true);

        results.setMinWidth(0);

        results.setMaxWidth(
                Double.MAX_VALUE
        );

        // =====================================================
        // ADD CONTENT
        // =====================================================

        content.getChildren().addAll(
                searchCard,
                results
        );

        // =====================================================
        // LOAD REAL HOSPITALS
        // =====================================================

        loadHospitals();

        // =====================================================
        // CREATE PATIENT SCENE
        // =====================================================

        return PatientUI.createScene(

                stage,

                "Search Hospitals",

                "Search Hospitals",

                "Find hospitals and healthcare facilities.",

                content
        );
    }

    // =========================================================
    // LOAD HOSPITALS FROM FIRESTORE
    // =========================================================

    private void loadHospitals() {

        try {

            System.out.println(
                    "Loading hospitals from Firestore..."
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

            showError(
                    "Unable to load hospitals from Firestore."
            );
        }
    }

    // =========================================================
    // SEARCH / FILTER
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
                                    searchText
                            );

            displayHospitals(
                    filtered
            );

            System.out.println(
                    "Hospitals found for '"
                            + searchText.trim()
                            + "': "
                            + filtered.size()
            );

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Unable to search hospitals."
            );
        }
    }

    // =========================================================
    // DISPLAY HOSPITALS
    // =========================================================

    private void displayHospitals(
            List<HospitalProfile> hospitals) {

        if (results == null) {
            return;
        }

        // Remove old cards
        results.getChildren().clear();

        if (hospitals == null ||
                hospitals.isEmpty()) {

            Label empty =
                    new Label(
                            "No hospitals found."
                    );

            empty.setStyle(
                    "-fx-text-fill: #64748b;" +
                    "-fx-font-size: 15px;" +
                    "-fx-padding: 20;"
            );

            results.getChildren().add(
                    empty
            );

            return;
        }

        // =====================================================
        // ADD REAL FIRESTORE HOSPITALS
        // =====================================================

        for (HospitalProfile hospital :
                hospitals) {

            if (hospital == null) {
                continue;
            }

            results.getChildren().add(
                    hospital(hospital)
            );
        }
    }

    // =========================================================
    // HOSPITAL CARD
    // =========================================================

    private HBox hospital(
            HospitalProfile hospital) {

        HBox box =
                new HBox(18);

        box.setPadding(
                new Insets(14)
        );

        box.setAlignment(
                Pos.CENTER_LEFT
        );

        box.setMinWidth(0);

        box.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox.setHgrow(
                box,
                Priority.ALWAYS
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 14;"
        );

        // =====================================================
        // IMAGE
        // =====================================================

        ImageView image =
                createHospitalImage();

        image.setFitWidth(180);

        image.setFitHeight(110);

        image.setPreserveRatio(false);

        // =====================================================
        // INFORMATION
        // =====================================================

        VBox information =
                new VBox(7);

        information.setMinWidth(0);

        information.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox.setHgrow(
                information,
                Priority.ALWAYS
        );

        // =====================================================
        // HOSPITAL NAME
        // =====================================================

        String hospitalName =
                safeDisplay(
                        hospital.getHospitalName(),
                        "Hospital"
                );

        Label nameLabel =
                new Label(
                        hospitalName
                );

        nameLabel.setWrapText(true);

        nameLabel.setMaxWidth(
                Double.MAX_VALUE
        );

        nameLabel.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        // =====================================================
        // LOCATION
        // =====================================================

        String address =
                safeDisplay(
                        hospital.getAddress(),
                        "Address not available"
                );

        Label locationLabel =
                new Label(
                        "📍 " + address
                );

        locationLabel.setWrapText(true);

        locationLabel.setMaxWidth(
                Double.MAX_VALUE
        );

        locationLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        // =====================================================
        // HOSPITAL TYPE
        // =====================================================

        String hospitalType =
                safeDisplay(
                        hospital.getHospitalType(),
                        "Healthcare Facility"
                );

        Label typeLabel =
                new Label(
                        hospitalType
                );

        typeLabel.setWrapText(true);

        typeLabel.setStyle(
                "-fx-text-fill: #2563eb;" +
                "-fx-font-weight: bold;"
        );

        // =====================================================
        // BEDS
        // =====================================================

        String beds =
                safeDisplay(
                        hospital.getBeds(),
                        "N/A"
                );

        Label bedsLabel =
                new Label(
                        "Beds: " + beds
                );

        bedsLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        // =====================================================
        // CONTACT
        // =====================================================

        String contact =
                safeDisplay(
                        hospital.getContact(),
                        "Contact not available"
                );

        Label contactLabel =
                new Label(
                        "☎ " + contact
                );

        contactLabel.setWrapText(true);

        contactLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        information.getChildren().addAll(
                nameLabel,
                locationLabel,
                typeLabel,
                bedsLabel,
                contactLabel
        );

        // =====================================================
        // VIEW BUTTON
        // =====================================================

        Button view =
                PatientUI.secondaryButton(
                        "View",
                        () ->
                                showHospitalDetails(
                                        hospital
                                )
                );

        box.getChildren().addAll(
                image,
                information,
                view
        );

        return box;
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

        /*
         * Your current HospitalDetails class expects a
         * rating String.
         *
         * HospitalProfile does not currently contain a
         * rating field, so we use "Not Rated".
         *
         * If you later add rating to HospitalProfile,
         * this can be replaced with the real rating.
         */

        String rating =
                "Not Rated";

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

        // Keep the common Patient Stage maximized
        if (!stage.isMaximized()) {

            stage.setMaximized(true);
        }
    }

    // =========================================================
    // HOSPITAL SPECIALITIES
    // =========================================================

    private String getSpecialties(
            HospitalProfile hospital) {

        String type =
                hospital.getHospitalType();

        if (type == null ||
                type.isBlank()) {

            return "General Medicine, "
                    + "Cardiology, "
                    + "Orthopedics";
        }

        /*
         * Currently HospitalProfile contains hospitalType,
         * not a separate specialties field.
         *
         * Therefore show the hospital type here.
         */

        return type.trim();
    }

    // =========================================================
    // DEFAULT HOSPITAL IMAGE
    // =========================================================

    private ImageView createHospitalImage() {

        ImageView view =
                new ImageView();

        view.setFitWidth(180);

        view.setFitHeight(110);

        view.setPreserveRatio(false);

        /*
         * Uses one default image for real Firestore
         * hospitals.
         *
         * You can later add imagePath to HospitalProfile
         * and load individual hospital images.
         */

        String[] possibleImages = {

                "/images/hospitals/hospital1.jpg",

                "/images/hospitals/hospital2.jpg",

                "/images/hospitals/hospital3.jpg",

                "/images/hospitals/hospital4.jpg"
        };

        /*
         * Use the first available image.
         */

        for (String path :
                possibleImages) {

            var resource =
                    getClass().getResource(path);

            if (resource == null) {
                continue;
            }

            try {

                Image image =
                        new Image(
                                resource.toExternalForm()
                        );

                view.setImage(image);

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
    // ERROR MESSAGE
    // =========================================================

    private void showError(
            String message) {

        if (results == null) {
            return;
        }

        results.getChildren().clear();

        Label error =
                new Label(
                        message
                );

        error.setWrapText(true);

        error.setStyle(
                "-fx-text-fill: #dc2626;" +
                "-fx-font-size: 15px;" +
                "-fx-padding: 20;"
        );

        results.getChildren().add(
                error
        );
    }

    // =========================================================
    // SAFE DISPLAY VALUE
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
}