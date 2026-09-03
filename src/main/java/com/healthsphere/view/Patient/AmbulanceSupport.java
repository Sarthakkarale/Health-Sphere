package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AmbulanceSupport {

    private final Stage stage;

    public AmbulanceSupport(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {

        VBox content = new VBox(20);

        content.setPadding(
                new Insets(25)
        );

        // =====================================================
        // HERO
        // =====================================================

        VBox hero =
                imageHero(
                        "/images/emergency/emergency6.jpg",
                        "🚑 Ambulance Assistance",
                        "Emergency medical transportation support."
                );

        // =====================================================
        // NATIONAL NUMBER
        // =====================================================

        VBox emergency =
                new VBox(10);

        emergency.setAlignment(
                Pos.CENTER
        );

        emergency.setPadding(
                new Insets(22)
        );

        emergency.setStyle(
                "-fx-background-color: #fee2e2;" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: #fca5a5;" +
                "-fx-border-radius: 18;"
        );

        Label title =
                new Label(
                        "Emergency Ambulance"
                );

        title.setStyle(
                "-fx-font-size: 21px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #991b1b;"
        );

        Label number =
                new Label(
                        "102"
                );

        number.setStyle(
                "-fx-font-size: 42px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #dc2626;"
        );

        Label info =
                new Label(
                        "National Ambulance Service"
                );

        info.setStyle(
                "-fx-text-fill: #7f1d1d;" +
                "-fx-font-size: 14px;"
        );

        emergency.getChildren().addAll(
                title,
                number,
                info
        );

        // =====================================================
        // AMBULANCE OPTIONS
        // =====================================================

        VBox ambulanceCard =
                PatientUI.coloredCard(
                        "🚑 Available Emergency Assistance",
                        "#dcfce7"
                );

        ambulanceCard.getChildren().addAll(

                ambulanceRow(
                        "🚑",
                        "Emergency Ambulance",
                        "Emergency medical transportation.",
                        "102"
                ),

                ambulanceRow(
                        "🚑",
                        "Emergency Response",
                        "For integrated emergency response.",
                        "112"
                ),

                ambulanceRow(
                        "🏥",
                        "Hospital Ambulance",
                        "Contact a nearby hospital for ambulance assistance."
                )
        );

        // =====================================================
        // IMPORTANT INFORMATION
        // =====================================================

        VBox information =
                PatientUI.coloredCard(
                        "⚠ Before the Ambulance Arrives",
                        "#fef3c7"
                );

        information.getChildren().addAll(
                infoRow(
                        "Share your location",
                        "Provide the exact location and nearby landmarks."
                ),
                infoRow(
                        "Keep the patient safe",
                        "Keep the patient in a safe position and follow professional instructions."
                ),
                infoRow(
                        "Prepare medical information",
                        "Keep medications, allergies and important medical records ready."
                )
        );

        // =====================================================
        // BACK
        // =====================================================

        Button back =
                PatientUI.secondaryButton(
                        "← Back to Emergency Assistance",
                        () -> stage.setScene(
                                new EmergencyAssistance(stage)
                                        .getScene()
                        )
                );

        content.getChildren().addAll(
                hero,
                emergency,
                ambulanceCard,
                information,
                back
        );

        ScrollPane scroll =
                new ScrollPane(content);

        scroll.setFitToWidth(true);

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scroll.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;"
        );

        return PatientUI.createScene(
                stage,
                "Ambulance Assistance",
                "Ambulance Assistance",
                "Find emergency ambulance assistance.",
                scroll
        );
    }

    // =========================================================
    // AMBULANCE ROW
    // =========================================================

    private HBox ambulanceRow(
            String icon,
            String title,
            String description,
            String number
    ) {

        HBox row =
                new HBox(14);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(14)
        );

        row.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #bbf7d0;" +
                "-fx-border-radius: 12;"
        );

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-font-size: 28px;"
        );

        VBox text =
                new VBox(4);

        HBox.setHgrow(
                text,
                Priority.ALWAYS
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-font-size: 16px;"
        );

        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setWrapText(true);

        descriptionLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        text.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );

        Label numberLabel =
                new Label(number);

        numberLabel.setStyle(
                "-fx-font-size: 21px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #16a34a;"
        );

        row.getChildren().addAll(
                iconLabel,
                text,
                numberLabel
        );

        return row;
    }

    private HBox ambulanceRow(
            String icon,
            String title,
            String description
    ) {

        return ambulanceRow(
                icon,
                title,
                description,
                "Contact Hospital"
        );
    }

    // =========================================================
    // INFORMATION ROW
    // =========================================================

    private HBox infoRow(
            String title,
            String description
    ) {

        HBox row =
                new HBox(10);

        row.setPadding(
                new Insets(11)
        );

        row.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #fde68a;" +
                "-fx-border-radius: 10;"
        );

        Label bullet =
                new Label("●");

        bullet.setStyle(
                "-fx-text-fill: #d97706;"
        );

        VBox text =
                new VBox(3);

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-weight: bold;"
        );

        Label descriptionLabel =
                new Label(description);

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

        VBox box =
                new VBox();

        box.setPrefHeight(250);

        box.setAlignment(
                Pos.BOTTOM_LEFT
        );

        box.setStyle(
                "-fx-background-color: #fee2e2;" +
                "-fx-background-radius: 18;"
        );

        var resource =
                getClass().getResource(path);

        if (resource != null) {

            Image image =
                    new Image(
                            resource.toExternalForm()
                    );

            ImageView view =
                    new ImageView(image);

            view.setFitWidth(1000);
            view.setFitHeight(250);
            view.setPreserveRatio(false);

            box.getChildren().add(view);
        }

        VBox overlay =
                new VBox(4);

        overlay.setPadding(
                new Insets(18)
        );

        overlay.setStyle(
                "-fx-background-color: rgba(0,0,0,0.58);"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 25px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;"
        );

        Label subtitleLabel =
                new Label(subtitle);

        subtitleLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;"
        );

        overlay.getChildren().addAll(
                titleLabel,
                subtitleLabel
        );

        box.getChildren().add(
                overlay
        );

        return box;
    }
}