package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EmergencyAssistance {

    private final Stage stage;

    public EmergencyAssistance(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {

        VBox content = new VBox(22);

        content.setPadding(
                new Insets(25)
        );

        // =========================================================
        // EMERGENCY BANNER
        // =========================================================

        VBox emergencyBanner = new VBox(15);

        emergencyBanner.setPadding(
                new Insets(25)
        );

        emergencyBanner.setAlignment(
                Pos.CENTER_LEFT
        );

        emergencyBanner.setStyle(
                "-fx-background-color: linear-gradient(to right, #fee2e2, #fecaca);" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: #fca5a5;" +
                "-fx-border-radius: 18;"
        );

        Label emergencyTitle =
                new Label(
                        "🚨 Need Emergency Help?"
                );

        emergencyTitle.setStyle(
                "-fx-font-size: 24px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #991b1b;"
        );

        Label emergencyText =
                new Label(
                        "If you are experiencing a life-threatening emergency, " +
                        "contact emergency services immediately."
                );

        emergencyText.setWrapText(true);

        emergencyText.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #7f1d1d;"
        );

        Button emergencyButton =
                PatientUI.button(
                        "🚨 CALL EMERGENCY SERVICES",
                        this::showEmergencyAlert
                );

        emergencyButton.setPrefHeight(
                48
        );

        emergencyButton.setStyle(
                "-fx-background-color: #dc2626;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 12 24;" +
                "-fx-cursor: hand;"
        );

        emergencyBanner.getChildren().addAll(
                emergencyTitle,
                emergencyText,
                emergencyButton
        );

        // =========================================================
        // IMAGE GALLERY
        // =========================================================

        GridPane imageGrid =
                createImageGallery();

        // =========================================================
        // EMERGENCY SERVICES
        // =========================================================

        VBox servicesCard =
                PatientUI.coloredCard(
                        "🚑 Emergency Services",
                        "#fee2e2"
                );

        servicesCard.getChildren().addAll(

                emergencyService(
                        "🚑",
                        "Ambulance",
                        "Request emergency medical transportation.",
                        "Ambulance Assistance",
                        "Request emergency ambulance support and prepare important medical information for the emergency team."
                ),

                emergencyService(
                        "🏥",
                        "Nearest Hospital",
                        "Find immediate hospital and emergency department care.",
                        "Hospital Emergency Assistance",
                        "Find immediate hospital care and access important emergency department information."
                ),

                emergencyService(
                        "👨‍⚕",
                        "Doctor Support",
                        "Contact your healthcare provider for medical assistance.",
                        "Doctor Support",
                        "Get guidance about contacting your healthcare provider and preparing important medical information."
                ),

                emergencyService(
                        "💊",
                        "Medication Information",
                        "Access important medication and allergy information.",
                        "Medication & Allergy Information",
                        "Keep your current medications, allergies and important medical information available for emergency healthcare professionals."
                )
        );

        // =========================================================
        // EMERGENCY INFORMATION
        // =========================================================

        VBox informationCard =
                PatientUI.coloredCard(
                        "⚠ Important Emergency Information",
                        "#fef3c7"
                );

        informationCard.getChildren().addAll(

                emergencyInfo(
                        "Stay Calm",
                        "Try to remain calm and clearly communicate your condition.",
                        "Stay Calm During an Emergency",
                        "Remain as calm as possible. Clearly explain what happened, your symptoms and any immediate concerns to emergency responders."
                ),

                emergencyInfo(
                        "Share Your Location",
                        "Provide your current location to emergency responders.",
                        "Share Your Location",
                        "Tell emergency responders your current location, nearby landmarks and any useful directions that can help them reach you quickly."
                ),

                emergencyInfo(
                        "Medical Information",
                        "Keep your medical records, allergies and medications available.",
                        "Important Medical Information",
                        "Keep information about your medical records, allergies, medications and existing healthcare details available for emergency professionals."
                ),

                emergencyInfo(
                        "Do Not Delay",
                        "For serious or life-threatening symptoms, seek professional emergency care immediately.",
                        "Do Not Delay Emergency Care",
                        "Do not delay professional emergency treatment when symptoms are serious or potentially life-threatening. Contact emergency services immediately."
                )
        );

        // =========================================================
        // QUICK ACTIONS
        // =========================================================

        VBox actionsCard =
                PatientUI.coloredCard(
                        "⚡ Quick Actions",
                        "#dbeafe"
                );

        HBox actions =
                new HBox(12);

        actions.setAlignment(
                Pos.CENTER_LEFT
        );

        Button hospitals =
                PatientUI.button(
                        "Find Hospitals",
                        () -> stage.setScene(
                                new SearchHospitals(stage)
                                        .getScene()
                        )
                );

        Button appointments =
                PatientUI.button(
                        "Appointments",
                        () -> stage.setScene(
                                new Appointments(stage)
                                        .getScene()
                        )
                );

        Button healthPassport =
                PatientUI.button(
                        "Health Passport",
                        () -> stage.setScene(
                                new HealthPassport(stage)
                                        .getScene()
                        )
                );

        Button medicalRecords =
                PatientUI.button(
                        "Medical Records",
                        () -> stage.setScene(
                                new MedicalRecords(stage)
                                        .getScene()
                        )
                );

        actions.getChildren().addAll(
                hospitals,
                appointments,
                healthPassport,
                medicalRecords
        );

        actionsCard.getChildren().add(
                actions
        );

        // =========================================================
        // ADD CONTENT
        // =========================================================

        content.getChildren().addAll(

                emergencyBanner,

                imageGrid,

                servicesCard,

                informationCard,

                actionsCard
        );

        // =========================================================
        // SCROLL
        // =========================================================

        ScrollPane scroll =
                new ScrollPane(
                        content
                );

        scroll.setFitToWidth(
                true
        );

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

        // =========================================================
        // COMMON PATIENT UI
        // =========================================================

        return PatientUI.createScene(
                stage,
                "Emergency Assistance",
                "Emergency Assistance",
                "Get immediate help and access important emergency services.",
                scroll
        );
    }

    // =============================================================
    // IMAGE GALLERY
    // =============================================================

    private GridPane createImageGallery() {

        GridPane grid =
                new GridPane();

        grid.setHgap(18);
        grid.setVgap(18);

        grid.add(
                imageCard(
                        "/images/emergency/emergency1.jpg",
                        "Emergency Care"
                ),
                0,
                0
        );

        grid.add(
                imageCard(
                        "/images/emergency/emergency2.jpg",
                        "Ambulance Services"
                ),
                1,
                0
        );

        grid.add(
                imageCard(
                        "/images/emergency/emergency3.jpg",
                        "Emergency Department"
                ),
                0,
                1
        );

        grid.add(
                imageCard(
                        "/images/emergency/emergency4.jpg",
                        "Emergency Medical Support"
                ),
                1,
                1
        );

        return grid;
    }

    // =============================================================
    // IMAGE CARD
    // =============================================================

    private VBox imageCard(
            String imagePath,
            String title
    ) {

        VBox card =
                new VBox();

        card.setPrefWidth(
                430
        );

        card.setPrefHeight(
                225
        );

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: #fecaca;" +
                "-fx-border-radius: 16;"
        );

        var resource =
                getClass().getResource(
                        imagePath
                );

        if (resource != null) {

            Image image =
                    new Image(
                            resource.toExternalForm()
                    );

            ImageView imageView =
                    new ImageView(
                            image
                    );

            imageView.setFitWidth(
                    430
            );

            imageView.setFitHeight(
                    175
            );

            imageView.setPreserveRatio(
                    false
            );

            card.getChildren().add(
                    imageView
            );

        } else {

            Label unavailable =
                    new Label(
                            "Image unavailable"
                    );

            unavailable.setStyle(
                    "-fx-text-fill: #64748b;" +
                    "-fx-font-size: 13px;"
            );

            VBox placeholder =
                    new VBox(
                            unavailable
                    );

            placeholder.setAlignment(
                    Pos.CENTER
            );

            placeholder.setPrefHeight(
                    175
            );

            card.getChildren().add(
                    placeholder
            );

            System.err.println(
                    "Emergency image not found: "
                            + imagePath
            );
        }

        Label titleLabel =
                new Label(
                        title
                );

        titleLabel.setPadding(
                new Insets(12)
        );

        titleLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #991b1b;"
        );

        card.getChildren().add(
                titleLabel
        );

        return card;
    }

    // =============================================================
    // EMERGENCY SERVICE ROW
    // =============================================================

    private HBox emergencyService(
            String icon,
            String title,
            String description,
            String screenTitle,
            String screenDescription
    ) {

        HBox row =
                new HBox(15);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(13)
        );

        row.setStyle(
                "-fx-background-color: rgba(255,255,255,0.9);" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #fecaca;" +
                "-fx-border-radius: 10;"
        );

        Label iconLabel =
                new Label(
                        icon
                );

        iconLabel.setStyle(
                "-fx-font-size: 26px;"
        );

        VBox information =
                new VBox(4);

        HBox.setHgrow(
                information,
                Priority.ALWAYS
        );

        Label titleLabel =
                new Label(
                        title
                );

        titleLabel.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label descriptionLabel =
                new Label(
                        description
                );

        descriptionLabel.setWrapText(
                true
        );

        descriptionLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        information.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );

        Button action =
                new Button(
                        "Help"
                );

        action.setStyle(
                "-fx-background-color: #dc2626;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 8 16;" +
                "-fx-cursor: hand;"
        );

        action.setOnAction(
                e -> openEmergencyScreen(
                        screenTitle,
                        screenDescription,
                        icon
                )
        );

        row.getChildren().addAll(
                iconLabel,
                information,
                action
        );

        return row;
    }

    // =============================================================
    // EMERGENCY INFORMATION ROW
    // =============================================================

    private HBox emergencyInfo(
            String title,
            String description,
            String screenTitle,
            String screenDescription
    ) {

        HBox row =
                new HBox(12);

        row.setAlignment(
                Pos.TOP_LEFT
        );

        row.setPadding(
                new Insets(10)
        );

        row.setStyle(
                "-fx-background-color: rgba(255,255,255,0.85);" +
                "-fx-background-radius: 9;" +
                "-fx-border-color: #fde68a;" +
                "-fx-border-radius: 9;"
        );

        Label bullet =
                new Label(
                        "●"
                );

        bullet.setStyle(
                "-fx-text-fill: #d97706;" +
                "-fx-font-size: 12px;"
        );

        VBox text =
                new VBox(3);

        HBox.setHgrow(
                text,
                Priority.ALWAYS
        );

        Label titleLabel =
                new Label(
                        title
                );

        titleLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label descriptionLabel =
                new Label(
                        description
                );

        descriptionLabel.setWrapText(
                true
        );

        descriptionLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        text.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );

        Button view =
                new Button(
                        "View"
                );

        view.setStyle(
                "-fx-background-color: #d97706;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 7 14;" +
                "-fx-cursor: hand;"
        );

        view.setOnAction(
                e -> openEmergencyScreen(
                        screenTitle,
                        screenDescription,
                        "⚠"
                )
        );

        row.getChildren().addAll(
                bullet,
                text,
                view
        );

        return row;
    }

    // =============================================================
    // OPEN EMERGENCY DETAIL SCREEN
    // =============================================================

    private void openEmergencyScreen(
            String title,
            String description,
            String icon
    ) {

        stage.setScene(
                new EmergencyDetailScreen(
                        stage,
                        title,
                        description,
                        icon
                ).getScene()
        );

        stage.show();
    }

    // =============================================================
    // EMERGENCY ALERT
    // =============================================================

    private void showEmergencyAlert() {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(
                "Emergency Assistance"
        );

        alert.setHeaderText(
                "🚨 Emergency Service"
        );

        alert.setContentText(
                "If this is a life-threatening emergency, " +
                "please contact your local emergency service immediately."
        );

        alert.showAndWait();
    }

    // =============================================================
    // EMERGENCY DETAIL SCREEN
    //
    // This is kept inside this file so you do NOT need to create
    // another Java file.
    // =============================================================

    private static class EmergencyDetailScreen {

        private final Stage stage;
        private final String title;
        private final String description;
        private final String icon;

        EmergencyDetailScreen(
                Stage stage,
                String title,
                String description,
                String icon
        ) {

            this.stage = stage;
            this.title = title;
            this.description = description;
            this.icon = icon;
        }

        Scene getScene() {

            VBox content =
                    new VBox(22);

            content.setPadding(
                    new Insets(25)
            );

            // =====================================================
            // MAIN DETAIL CARD
            // =====================================================

            VBox card =
                    new VBox(18);

            card.setPadding(
                    new Insets(30)
            );

            card.setStyle(
                    "-fx-background-color: #fee2e2;" +
                    "-fx-background-radius: 18;" +
                    "-fx-border-color: #fca5a5;" +
                    "-fx-border-radius: 18;"
            );

            Label iconLabel =
                    new Label(
                            icon
                    );

            iconLabel.setStyle(
                    "-fx-font-size: 46px;"
            );

            Label titleLabel =
                    new Label(
                            title
                    );

            titleLabel.setStyle(
                    "-fx-font-size: 28px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: #991b1b;"
            );

            Label descriptionLabel =
                    new Label(
                            description
                    );

            descriptionLabel.setWrapText(
                    true
            );

            descriptionLabel.setStyle(
                    "-fx-font-size: 16px;" +
                    "-fx-text-fill: #7f1d1d;"
            );

            card.getChildren().addAll(
                    iconLabel,
                    titleLabel,
                    descriptionLabel
            );

            // =====================================================
            // ACTION INFORMATION
            // =====================================================

            VBox actionCard =
                    PatientUI.coloredCard(
                            "⚡ Recommended Actions",
                            "#dbeafe"
                    );

            actionCard.getChildren().addAll(

                    information(
                            "Stay Calm",
                            "Remain calm and clearly communicate your situation."
                    ),

                    information(
                            "Keep Medical Information Ready",
                            "Keep your medications, allergies and medical records available."
                    ),

                    information(
                            "Seek Professional Help",
                            "For serious or life-threatening situations, contact emergency services immediately."
                    )
            );

            // =====================================================
            // BUTTONS
            // =====================================================

            HBox buttons =
                    new HBox(12);

            buttons.setAlignment(
                    Pos.CENTER_LEFT
            );

            Button emergency =
                    PatientUI.button(
                            "🚨 Emergency Services",
                            () -> {

                                Alert alert =
                                        new Alert(
                                                Alert.AlertType.INFORMATION
                                        );

                                alert.setTitle(
                                        "Emergency Services"
                                );

                                alert.setHeaderText(
                                        "🚨 Emergency Assistance"
                                );

                                alert.setContentText(
                                        "For a life-threatening emergency, " +
                                        "please contact your local emergency service immediately."
                                );

                                alert.showAndWait();
                            }
                    );

            Button back =
                    PatientUI.secondaryButton(
                            "← Back",
                            () -> stage.setScene(
                                    new EmergencyAssistance(stage)
                                            .getScene()
                            )
                    );

            buttons.getChildren().addAll(
                    emergency,
                    back
            );

            content.getChildren().addAll(
                    card,
                    actionCard,
                    buttons
            );

            ScrollPane scroll =
                    new ScrollPane(
                            content
                    );

            scroll.setFitToWidth(
                    true
            );

            scroll.setHbarPolicy(
                    ScrollPane.ScrollBarPolicy.NEVER
            );

            scroll.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-background: transparent;"
            );

            return PatientUI.createScene(
                    stage,
                    title,
                    title,
                    "Emergency assistance and important information.",
                    scroll
            );
        }

        // =========================================================
        // INFORMATION ROW
        // =========================================================

        private HBox information(
                String title,
                String description
        ) {

            HBox row =
                    new HBox(10);

            row.setPadding(
                    new Insets(12)
            );

            row.setAlignment(
                    Pos.TOP_LEFT
            );

            row.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #bfdbfe;" +
                    "-fx-border-radius: 10;"
            );

            Label bullet =
                    new Label(
                            "●"
                    );

            bullet.setStyle(
                    "-fx-text-fill: #2563eb;"
            );

            VBox text =
                    new VBox(3);

            Label titleLabel =
                    new Label(
                            title
                    );

            titleLabel.setStyle(
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: #0f172a;"
            );

            Label descriptionLabel =
                    new Label(
                            description
                    );

            descriptionLabel.setWrapText(
                    true
            );

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
    }
}