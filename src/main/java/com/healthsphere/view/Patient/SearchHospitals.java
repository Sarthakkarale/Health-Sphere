package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SearchHospitals {

    private final Stage stage;

    public SearchHospitals(Stage stage) {
        this.stage = stage;
    }

    // =========================================================
    // MAIN SCENE
    // =========================================================

    public Scene getScene() {

        VBox content = new VBox(20);

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

        Label instruction =
                new Label(
                        "Search for hospitals, clinics and healthcare facilities near you."
                );

        instruction.setWrapText(true);

        instruction.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 14px;"
        );

        HBox searchRow =
                new HBox(12);

        searchRow.setAlignment(
                Pos.CENTER_LEFT
        );

        TextField searchField =
                new TextField();

        searchField.setPromptText(
                "Enter hospital name, location or speciality"
        );

        searchField.setPrefHeight(42);

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

        Button searchButton =
                PatientUI.button(
                        "Search",
                        () -> filterHospitals(
                                searchField.getText()
                        )
                );

        searchRow.getChildren().addAll(
                searchField,
                searchButton
        );

        searchCard.getChildren().addAll(
                instruction,
                searchRow
        );

        // =====================================================
        // HOSPITAL RESULTS
        // =====================================================

        VBox results =
                PatientUI.card(
                        "Nearby Hospitals"
                );

        // =====================================================
        // HOSPITAL 1
        // =====================================================

        HBox hospital1 =
                hospital(
                        "/images/hospitals/hospital1.jpg",
                        "Apollo Hospitals",
                        "Hyderabad",
                        "Multi-Speciality Hospital",
                        "4.8"
                );

        // =====================================================
        // HOSPITAL 2
        // =====================================================

        HBox hospital2 =
                hospital(
                        "/images/hospitals/hospital2.jpg",
                        "Care Hospitals",
                        "Hyderabad",
                        "General & Specialty Care",
                        "4.6"
                );

        // =====================================================
        // HOSPITAL 3
        // =====================================================

        HBox hospital3 =
                hospital(
                        "/images/hospitals/hospital3.jpg",
                        "Yashoda Hospitals",
                        "Hyderabad",
                        "Advanced Healthcare Centre",
                        "4.7"
                );

        // =====================================================
        // HOSPITAL 4
        // =====================================================

        HBox hospital4 =
                hospital(
                        "/images/hospitals/hospital4.jpg",
                        "KIMS Hospitals",
                        "Hyderabad",
                        "Multi-Speciality Hospital",
                        "4.5"
                );

        results.getChildren().addAll(
                hospital1,
                hospital2,
                hospital3,
                hospital4
        );

        // =====================================================
        // ADD CONTENT
        // =====================================================

        content.getChildren().addAll(
                searchCard,
                results
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

        scroll.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;"
        );

        // =====================================================
        // COMMON PATIENT UI
        // =====================================================

        return PatientUI.createScene(
                stage,
                "Search Hospitals",
                "Search Hospitals",
                "Find nearby hospitals and healthcare facilities.",
                scroll
        );
    }

    // =========================================================
    // SEARCH / FILTER
    // =========================================================

    private void filterHospitals(
            String searchText
    ) {

        if (searchText == null ||
                searchText.trim().isEmpty()) {

            System.out.println(
                    "Please enter a hospital, location or speciality."
            );

            return;
        }

        System.out.println(
                "Searching hospitals for: "
                        + searchText.trim()
        );

        /*
         * Backend hospital search can be connected here later.
         *
         * Example:
         *
         * HospitalController
         *      -> HospitalDAO
         *      -> Firebase
         *
         * For now this is only UI/demo functionality.
         */
    }

    // =========================================================
    // HOSPITAL CARD
    // =========================================================

    private HBox hospital(
            String imagePath,
            String name,
            String location,
            String speciality,
            String rating
    ) {

        HBox box =
                new HBox(18);

        box.setPadding(
                new Insets(14)
        );

        box.setAlignment(
                Pos.CENTER_LEFT
        );

        box.setMaxWidth(
                Double.MAX_VALUE
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 14;"
        );

        // =====================================================
        // HOSPITAL IMAGE
        // =====================================================

        ImageView image =
                createImage(
                        imagePath,
                        180,
                        110
                );

        // =====================================================
        // HOSPITAL INFORMATION
        // =====================================================

        VBox information =
                new VBox(7);

        HBox.setHgrow(
                information,
                Priority.ALWAYS
        );

        Label nameLabel =
                new Label(
                        name
                );

        nameLabel.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label locationLabel =
                new Label(
                        "📍 " + location
                );

        locationLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        Label specialityLabel =
                new Label(
                        speciality
                );

        specialityLabel.setStyle(
                "-fx-text-fill: #2563eb;" +
                "-fx-font-weight: bold;"
        );

        Label ratingLabel =
                new Label(
                        "★ " + rating + " rating"
                );

        ratingLabel.setStyle(
                "-fx-text-fill: #16a34a;" +
                "-fx-font-weight: bold;"
        );

        information.getChildren().addAll(
                nameLabel,
                locationLabel,
                specialityLabel,
                ratingLabel
        );

        // =====================================================
        // VIEW BUTTON
        // =====================================================

        Button view =
                PatientUI.secondaryButton(
                        "View",
                        () -> showHospitalDetails(
                                name,
                                location,
                                speciality,
                                rating
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
    // SHOW HOSPITAL DETAILS
    // =========================================================

    private void showHospitalDetails(
            String name,
            String location,
            String speciality,
            String rating
    ) {

        stage.setScene(
                new HospitalDetails(
                        stage,
                        name,
                        location,
                        speciality,
                        rating,
                        getSpecialties(name)
                ).getScene()
        );

        stage.show();
    }

    // =========================================================
    // HOSPITAL SPECIALITIES
    // =========================================================

    private String getSpecialties(
            String hospitalName
    ) {

        switch (hospitalName) {

            case "Apollo Hospitals":

                return "Cardiology, Neurology, Orthopedics, " +
                        "Oncology, General Medicine";

            case "Care Hospitals":

                return "Cardiology, Gastroenterology, Pulmonology, " +
                        "Orthopedics, General Medicine";

            case "Yashoda Hospitals":

                return "Cardiology, Neurology, Oncology, " +
                        "Nephrology, Gastroenterology";

            case "KIMS Hospitals":

                return "Cardiology, Orthopedics, Neurology, " +
                        "Urology, General Medicine";

            default:

                return "General Medicine, Cardiology, Orthopedics";
        }
    }

    // =========================================================
    // IMAGE LOADER
    // =========================================================

    private ImageView createImage(
            String path,
            double width,
            double height
    ) {

        ImageView view =
                new ImageView();

        view.setFitWidth(
                width
        );

        view.setFitHeight(
                height
        );

        view.setPreserveRatio(
                false
        );

        var resource =
                getClass().getResource(
                        path
                );

        if (resource == null) {

            System.err.println(
                    "Hospital image not found: "
                            + path
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

        return view;
    }
}