package com.healthsphere.view.Patient;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DoctorSupport {

    private final Stage stage;

    private VBox results;

    public DoctorSupport(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {

        VBox content = new VBox(20);

        content.setPadding(new Insets(25));

        // =====================================================
        // HERO
        // =====================================================

        content.getChildren().add(
                imageHero(
                        "/images/emergency/emergency8.jpg",
                        "👨‍⚕ Doctor Support",
                        "Find healthcare professionals for medical assistance."
                )
        );

        // =====================================================
        // SEARCH CARD
        // =====================================================

        VBox searchCard = PatientUI.coloredCard(
                "🔎 Search Doctors",
                "#ede9fe"
        );

        TextField search = new TextField();

        search.setPromptText(
                "Search by doctor name or specialty..."
        );

        search.setPrefHeight(42);

        search.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #c4b5fd;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10;"
        );

        ComboBox<String> specialty = new ComboBox<>(
                FXCollections.observableArrayList(
                        "All Specialties",
                        "General Physician",
                        "Cardiologist",
                        "Neurologist",
                        "Orthopedic",
                        "Pediatrician",
                        "Dermatologist",
                        "Emergency Medicine"
                )
        );

        specialty.setValue("All Specialties");
        specialty.setPrefHeight(42);

        Button searchButton = PatientUI.button(
                "🔎 Search",
                () -> performSearch(
                        search.getText(),
                        specialty.getValue()
                )
        );

        HBox searchRow = new HBox(
                12,
                search,
                specialty,
                searchButton
        );

        HBox.setHgrow(
                search,
                Priority.ALWAYS
        );

        searchCard.getChildren().add(searchRow);

        // =====================================================
        // RESULTS
        // =====================================================

        results = new VBox(12);

        VBox resultsCard = PatientUI.coloredCard(
                "👨‍⚕ Available Doctor Support",
                "#dbeafe"
        );

        resultsCard.getChildren().add(results);

        showDefaultDoctors();

        // =====================================================
        // IMPORTANT
        // =====================================================

        VBox information = PatientUI.coloredCard(
                "⚠ Emergency Note",
                "#fef3c7"
        );

        Label note = new Label(
                "Doctor search is useful for support and guidance. " +
                "For a life-threatening emergency, contact emergency services immediately."
        );

        note.setWrapText(true);

        note.setStyle(
                "-fx-text-fill: #78350f;" +
                "-fx-font-size: 14px;"
        );

        information.getChildren().add(note);

        // =====================================================
        // BACK
        // =====================================================

        Button back = PatientUI.secondaryButton(
                "← Back to Emergency Assistance",
                () -> stage.setScene(
                        new EmergencyAssistance(stage).getScene()
                )
        );

        content.getChildren().addAll(
                searchCard,
                resultsCard,
                information,
                back
        );

        // =====================================================
        // NO INNER SCROLLPANE
        // =====================================================

        return PatientUI.createScene(
                stage,
                "Doctor Support",
                "Doctor Support",
                "Search for healthcare professionals.",
                content
        );
    }

    // =========================================================
    // DEFAULT DOCTORS
    // =========================================================

    private void showDefaultDoctors() {

        results.getChildren().clear();

        results.getChildren().addAll(

                doctorCard(
                        "👨‍⚕",
                        "Emergency Medicine Specialist",
                        "Emergency Medicine",
                        "Available for emergency guidance"
                ),

                doctorCard(
                        "👩‍⚕",
                        "General Physician",
                        "General Medicine",
                        "Available for general medical support"
                ),

                doctorCard(
                        "👨‍⚕",
                        "Cardiology Specialist",
                        "Cardiology",
                        "Medical consultation support"
                )
        );
    }

    // =========================================================
    // SEARCH
    // =========================================================

    private void performSearch(
            String query,
            String specialty
    ) {

        results.getChildren().clear();

        String searchText =
                query == null
                        ? ""
                        : query.trim().toLowerCase();

        if (searchText.isBlank()
                && "All Specialties".equals(specialty)) {

            showDefaultDoctors();
            return;
        }

        boolean found = false;

        if (searchText.isBlank()
                || "Emergency Medicine".equals(specialty)
                || "Emergency Medicine Specialist"
                .toLowerCase()
                .contains(searchText)) {

            results.getChildren().add(
                    doctorCard(
                            "👨‍⚕",
                            "Emergency Medicine Specialist",
                            "Emergency Medicine",
                            "Emergency medical support"
                    )
            );

            found = true;
        }

        if (searchText.isBlank()
                || "General Physician".equals(specialty)
                || "General Physician"
                .toLowerCase()
                .contains(searchText)) {

            results.getChildren().add(
                    doctorCard(
                            "👩‍⚕",
                            "General Physician",
                            "General Medicine",
                            "General medical guidance"
                    )
            );

            found = true;
        }

        if (searchText.isBlank()
                || "Cardiologist".equals(specialty)
                || "Cardiology Specialist"
                .toLowerCase()
                .contains(searchText)) {

            results.getChildren().add(
                    doctorCard(
                            "👨‍⚕",
                            "Cardiology Specialist",
                            "Cardiology",
                            "Cardiac medical support"
                    )
            );

            found = true;
        }

        if (!found) {

            Label empty = new Label(
                    "No doctors found. Try another name or specialty."
            );

            empty.setStyle(
                    "-fx-text-fill: #64748b;" +
                    "-fx-font-size: 15px;"
            );

            results.getChildren().add(empty);
        }
    }

    // =========================================================
    // DOCTOR CARD
    // =========================================================

    private HBox doctorCard(
            String icon,
            String name,
            String specialty,
            String availability
    ) {

        HBox card = new HBox(15);

        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #c4b5fd;" +
                "-fx-border-radius: 12;"
        );

        Label iconLabel = new Label(icon);

        iconLabel.setStyle(
                "-fx-font-size: 34px;"
        );

        VBox information = new VBox(4);

        HBox.setHgrow(
                information,
                Priority.ALWAYS
        );

        Label nameLabel = new Label(name);

        nameLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #4c1d95;"
        );

        Label specialtyLabel = new Label(specialty);

        specialtyLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        Label availabilityLabel = new Label(
                "● " + availability
        );

        availabilityLabel.setStyle(
                "-fx-text-fill: #16a34a;" +
                "-fx-font-weight: bold;"
        );

        information.getChildren().addAll(
                nameLabel,
                specialtyLabel,
                availabilityLabel
        );

        Button support = new Button("Support");

        support.setStyle(
                "-fx-background-color: #7c3aed;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 8 15;" +
                "-fx-cursor: hand;"
        );

        card.getChildren().addAll(
                iconLabel,
                information,
                support
        );

        return card;
    }

    // =========================================================
    // IMAGE HERO
    // =========================================================

    private VBox imageHero(
            String path,
            String title,
            String subtitle
    ) {

        VBox box = new VBox();

        box.setPrefHeight(250);
        box.setAlignment(Pos.BOTTOM_LEFT);

        var resource = getClass().getResource(path);

        if (resource != null) {

            Image image = new Image(
                    resource.toExternalForm()
            );

            ImageView view = new ImageView(image);

            view.setFitWidth(1000);
            view.setFitHeight(250);
            view.setPreserveRatio(false);

            box.getChildren().add(view);
        }

        VBox overlay = new VBox(4);

        overlay.setPadding(new Insets(18));

        overlay.setStyle(
                "-fx-background-color: rgba(0,0,0,0.55);"
        );

        Label titleLabel = new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 25px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;"
        );

        Label subtitleLabel = new Label(subtitle);

        subtitleLabel.setStyle(
                "-fx-text-fill: white;"
        );

        overlay.getChildren().addAll(
                titleLabel,
                subtitleLabel
        );

        box.getChildren().add(overlay);

        return box;
    }
}