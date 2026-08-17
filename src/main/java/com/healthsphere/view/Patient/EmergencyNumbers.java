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

public class EmergencyNumbers {

    private final Stage stage;

    public EmergencyNumbers(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {

        VBox content = new VBox(20);
        content.setPadding(new Insets(25));

        // =====================================================
        // HERO IMAGE
        // =====================================================

        VBox hero = imageHero(
                "/images/emergency/emergency5.jpg",
                "🚨 Emergency Services",
                "Important emergency numbers in India."
        );

        // =====================================================
        // MAIN EMERGENCY NUMBER
        // =====================================================

        VBox national = new VBox(12);

        national.setPadding(new Insets(22));
        national.setAlignment(Pos.CENTER);

        national.setStyle(
                "-fx-background-color: #fee2e2;" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: #fca5a5;" +
                "-fx-border-radius: 18;"
        );

        Label nationalTitle = new Label(
                "🇮🇳 National Emergency Number"
        );

        nationalTitle.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #991b1b;"
        );

        Label number = new Label("112");

        number.setStyle(
                "-fx-font-size: 46px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #dc2626;"
        );

        Label explanation = new Label(
                "Integrated emergency response for police, " +
                "fire and health-related emergencies."
        );

        explanation.setWrapText(true);
        explanation.setAlignment(Pos.CENTER);

        explanation.setStyle(
                "-fx-text-fill: #7f1d1d;" +
                "-fx-font-size: 14px;"
        );

        national.getChildren().addAll(
                nationalTitle,
                number,
                explanation
        );

        // =====================================================
        // NUMBERS
        // =====================================================

        VBox numbersCard = PatientUI.coloredCard(
                "📞 Emergency Helplines",
                "#dbeafe"
        );

        numbersCard.getChildren().addAll(

                numberRow(
                        "🚓",
                        "Police",
                        "100",
                        "Police emergency assistance."
                ),

                numberRow(
                        "🚒",
                        "Fire",
                        "101",
                        "Fire and rescue emergency assistance."
                ),

                numberRow(
                        "🚑",
                        "Ambulance",
                        "102",
                        "National Ambulance Service."
                ),

                numberRow(
                        "👶",
                        "Child Helpline",
                        "1098",
                        "Emergency assistance for children."
                ),

                numberRow(
                        "🚗",
                        "Road Accident",
                        "1073",
                        "Road accident assistance."
                ),

                numberRow(
                        "🆘",
                        "Disaster / Emergency",
                        "112",
                        "Use the national emergency response number."
                )
        );

        // =====================================================
        // WARNING
        // =====================================================

        VBox warning = new VBox(8);

        warning.setPadding(new Insets(18));

        warning.setStyle(
                "-fx-background-color: #fff7ed;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #fed7aa;" +
                "-fx-border-radius: 14;"
        );

        Label warningTitle = new Label("⚠ Important");

        warningTitle.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #c2410c;"
        );

        Label warningText = new Label(
                "For a life-threatening emergency, call the appropriate " +
                "emergency service immediately. Do not delay emergency care."
        );

        warningText.setWrapText(true);

        warningText.setStyle(
                "-fx-text-fill: #7c2d12;"
        );

        warning.getChildren().addAll(
                warningTitle,
                warningText
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
                hero,
                national,
                numbersCard,
                warning,
                back
        );

        // =====================================================
        // NO INNER SCROLLPANE
        // =====================================================

        return PatientUI.createScene(
                stage,
                "Emergency Numbers",
                "Emergency Numbers",
                "Important emergency service numbers in India.",
                content
        );
    }

    // =========================================================
    // NUMBER ROW
    // =========================================================

    private HBox numberRow(
            String icon,
            String title,
            String number,
            String description
    ) {

        HBox row = new HBox(14);

        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));

        row.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 12;"
        );

        Label iconLabel = new Label(icon);

        iconLabel.setStyle(
                "-fx-font-size: 27px;"
        );

        VBox text = new VBox(3);

        HBox.setHgrow(
                text,
                Priority.ALWAYS
        );

        Label titleLabel = new Label(title);

        titleLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-font-size: 16px;" +
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

        Label numberLabel = new Label(number);

        numberLabel.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #dc2626;"
        );

        row.getChildren().addAll(
                iconLabel,
                text,
                numberLabel
        );

        return row;
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

        box.setStyle(
                "-fx-background-radius: 18;" +
                "-fx-border-radius: 18;" +
                "-fx-background-color: #fee2e2;"
        );

        var resource = getClass().getResource(path);

        if (resource != null) {

            Image image = new Image(
                    resource.toExternalForm()
            );

            ImageView imageView = new ImageView(image);

            imageView.setFitWidth(1000);
            imageView.setFitHeight(250);
            imageView.setPreserveRatio(false);

            box.getChildren().add(imageView);
        }

        VBox overlay = new VBox(5);

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
                "-fx-font-size: 14px;" +
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