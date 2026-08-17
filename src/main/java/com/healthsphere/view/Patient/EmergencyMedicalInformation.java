package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EmergencyMedicalInformation {

    private final Stage stage;

    public EmergencyMedicalInformation(Stage stage) {
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
                        "/images/emergency/emergency9.jpg",
                        "💊 Emergency Medical Information",
                        "Basic information to help you respond safely during emergencies."
                )
        );

        // =====================================================
        // FIRST AID IMAGE
        // =====================================================

        content.getChildren().add(
                imageBanner(
                        "/images/emergency/emergency10.jpg"
                )
        );

        // =====================================================
        // EMERGENCY INFORMATION
        // =====================================================

        VBox information = PatientUI.coloredCard(
                "🩹 Basic Emergency Guidance",
                "#dbeafe"
        );

        information.getChildren().addAll(

                emergencyInfo(
                        "Severe Bleeding",
                        "Apply firm pressure with clean cloth or gauze and seek emergency medical care immediately."
                ),

                emergencyInfo(
                        "Burns",
                        "Cool a minor burn with cool running water. Do not apply ice directly to the skin."
                ),

                emergencyInfo(
                        "Fainting",
                        "Help the person lie down safely and check for breathing. Seek emergency help if they do not recover."
                ),

                emergencyInfo(
                        "Breathing Difficulty",
                        "If someone is having severe difficulty breathing, contact emergency services immediately."
                ),

                emergencyInfo(
                        "Chest Pain",
                        "Sudden or severe chest pain can be an emergency. Seek immediate professional medical assistance."
                ),

                emergencyInfo(
                        "Unconscious Person",
                        "Check responsiveness and breathing and contact emergency services immediately."
                )
        );

        // =====================================================
        // MEDICATION INFORMATION
        // =====================================================

        VBox medication = PatientUI.coloredCard(
                "💊 Medication & Allergy Information",
                "#ede9fe"
        );

        medication.getChildren().addAll(

                emergencyInfo(
                        "Keep Medication List Ready",
                        "Keep a current list of medicines and doses available for emergency professionals."
                ),

                emergencyInfo(
                        "Allergies",
                        "Tell emergency professionals about known medicine, food or other serious allergies."
                ),

                emergencyInfo(
                        "Medical Conditions",
                        "Keep important information about existing conditions available when seeking emergency care."
                )
        );

        // =====================================================
        // IMPORTANT NOTE
        // =====================================================

        VBox note = new VBox(8);

        note.setPadding(new Insets(18));

        note.setStyle(
                "-fx-background-color: #fee2e2;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #fca5a5;" +
                "-fx-border-radius: 14;"
        );

        Label noteTitle = new Label(
                "🚨 Emergency Warning"
        );

        noteTitle.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #991b1b;"
        );

        Label noteText = new Label(
                "This information is for basic emergency awareness only. " +
                "It does not replace professional medical care. " +
                "For a life-threatening emergency, contact emergency services immediately."
        );

        noteText.setWrapText(true);

        noteText.setStyle(
                "-fx-text-fill: #7f1d1d;"
        );

        note.getChildren().addAll(
                noteTitle,
                noteText
        );

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
                information,
                medication,
                note,
                back
        );

        // =====================================================
        // NO INNER SCROLLPANE
        // =====================================================

        return PatientUI.createScene(
                stage,
                "Emergency Medical Information",
                "Emergency Medical Information",
                "Basic emergency information and first-aid guidance.",
                content
        );
    }

    // =========================================================
    // INFORMATION ROW
    // =========================================================

    private HBox emergencyInfo(
            String title,
            String description
    ) {

        HBox row = new HBox(12);

        row.setAlignment(Pos.TOP_LEFT);
        row.setPadding(new Insets(13));

        row.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 10;"
        );

        Label bullet = new Label("●");

        bullet.setStyle(
                "-fx-text-fill: #2563eb;" +
                "-fx-font-size: 12px;"
        );

        VBox text = new VBox(3);

        HBox.setHgrow(
                text,
                Priority.ALWAYS
        );

        Label titleLabel = new Label(title);

        titleLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #0f172a;"
        );

        Label descriptionLabel = new Label(description);

        descriptionLabel.setWrapText(true);

        descriptionLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        text.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );

        row.getChildren().addAll(
                bullet,
                text
        );

        return row;
    }

    // =========================================================
    // HERO
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
                "-fx-background-color: rgba(0,0,0,0.58);"
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

    // =========================================================
    // SECOND IMAGE
    // =========================================================

    private VBox imageBanner(String path) {

        VBox box = new VBox();

        box.setPrefHeight(210);

        var resource = getClass().getResource(path);

        if (resource != null) {

            Image image = new Image(
                    resource.toExternalForm()
            );

            ImageView view = new ImageView(image);

            view.setFitWidth(1000);
            view.setFitHeight(210);
            view.setPreserveRatio(false);

            box.getChildren().add(view);
        }

        box.setStyle(
                "-fx-background-radius: 16;" +
                "-fx-border-radius: 16;" +
                "-fx-border-color: #ddd6fe;"
        );

        return box;
    }
}