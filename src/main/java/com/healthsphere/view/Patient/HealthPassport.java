
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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;

public class HealthPassport {

    private final Stage stage;

    public HealthPassport(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {

        VBox content = new VBox(20);
        content.setPadding(new Insets(5));

        /*
         * ============================================================
         * IMAGE GALLERY
         * ============================================================
         */

        HBox images = createImageGallery();

        /*
         * ============================================================
         * PERSONAL INFORMATION CARD
         * ============================================================
         */

        VBox personalCard = PatientUI.coloredCard(
                "👤  Personal Information",
                "#dbeafe"
        );

        personalCard.getChildren().addAll(

                information(
                        "Full Name",
                        "Sarah Williams"
                ),

                information(
                        "Date of Birth",
                        "15 March 1995"
                ),

                information(
                        "Blood Group",
                        "O+"
                ),

                information(
                        "Gender",
                        "Female"
                )
        );

        /*
         * ============================================================
         * MEDICAL INFORMATION CARD
         * ============================================================
         */

        VBox medicalCard = PatientUI.coloredCard(
                "🏥  Medical Information",
                "#dcfce7"
        );

        medicalCard.getChildren().addAll(

                information(
                        "Allergies",
                        "No known allergies"
                ),

                information(
                        "Emergency Contact",
                        "+91 98765 43210"
                ),

                information(
                        "Primary Physician",
                        "Dr. Sarah Jenkins"
                ),

                information(
                        "Insurance",
                        "HealthSecure Plus"
                )
        );

        /*
         * ============================================================
         * INFORMATION ROW
         * ============================================================
         */

        HBox informationRow = new HBox(18);

        HBox.setHgrow(
                personalCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                medicalCard,
                Priority.ALWAYS
        );

        personalCard.setMaxWidth(Double.MAX_VALUE);
        medicalCard.setMaxWidth(Double.MAX_VALUE);

        informationRow.getChildren().addAll(
                personalCard,
                medicalCard
        );

        /*
         * ============================================================
         * HEALTH SUMMARY CARD
         * ============================================================
         */

        VBox recordsCard = PatientUI.coloredCard(
                "📋  Health Summary",
                "#fef3c7"
        );

        recordsCard.getChildren().addAll(

                summary(
                        "Medical Records",
                        "12 records available"
                ),

                summary(
                        "Prescriptions",
                        "5 active prescriptions"
                ),

                summary(
                        "Appointments",
                        "3 upcoming appointments"
                ),

                summary(
                        "Lab Reports",
                        "8 reports available"
                )
        );

        /*
         * ============================================================
         * VIEW MEDICAL RECORDS BUTTON
         * ============================================================
         */

        Button recordsButton = PatientUI.button(
                "View Medical Records",
                () -> stage.setScene(
                        new MedicalRecords(stage).getScene()
                )
        );

        recordsCard.getChildren().add(
                recordsButton
        );

        /*
         * ============================================================
         * QUICK ACTIONS
         * ============================================================
         */

        VBox actionsCard = PatientUI.coloredCard(
                "⚡  Quick Actions",
                "#ede9fe"
        );

        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER_LEFT);

        Button appointments = PatientUI.button(
                "Appointments",
                () -> stage.setScene(
                        new Appointments(stage).getScene()
                )
        );

        Button records = PatientUI.button(
                "Medical Records",
                () -> stage.setScene(
                        new MedicalRecords(stage).getScene()
                )
        );

        Button aiAssistant = PatientUI.button(
                "AI Assistant",
                () -> stage.setScene(
                        new AiHealthAssistant(stage).getScene()
                )
        );

        Button emergency = createEmergencyButton();

        Button profile = PatientUI.button(
                "Profile & Settings",
                () -> stage.setScene(
                        new ProfileSettings(stage).getScene()
                )
        );

        actions.getChildren().addAll(
                appointments,
                records,
                aiAssistant,
                emergency,
                profile
        );

        actionsCard.getChildren().add(
                actions
        );

        /*
         * ============================================================
         * EXTRA HEALTH CARD
         * ============================================================
         */

        VBox statusCard = PatientUI.coloredCard(
                "💚  Health Status",
                "#ccfbf1"
        );

        statusCard.getChildren().addAll(

                status(
                        "Heart Rate",
                        "72 BPM",
                        "Normal"
                ),

                status(
                        "Blood Pressure",
                        "118 / 76 mmHg",
                        "Healthy"
                ),

                status(
                        "Oxygen Level",
                        "98%",
                        "Normal"
                ),

                status(
                        "Last Health Check",
                        "10 August 2026",
                        "Up to date"
                )
        );

        /*
         * ============================================================
         * ADD CONTENT
         * ============================================================
         */

        content.getChildren().addAll(
                images,
                informationRow,
                recordsCard,
                statusCard,
                actionsCard
        );

        /*
         * ============================================================
         * SCROLL PANE
         * ============================================================
         */

        ScrollPane scroll = new ScrollPane(
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

        /*
         * ============================================================
         * IMPORTANT
         *
         * PatientUI.createScene() creates:
         *
         * LEFT SIDEBAR
         * TOP HEADER
         * CENTER CONTENT
         *
         * Therefore DO NOT create another BorderPane here.
         * ============================================================
         */

        return PatientUI.createScene(
                stage,
                "Health Passport",
                "My Health Passport",
                "Your complete digital health identity and important medical information.",
                scroll
        );
    }

    /*
     * ================================================================
     * IMAGE GALLERY
     * ================================================================
     */

    private HBox createImageGallery() {

        HBox gallery = new HBox(15);

        gallery.setAlignment(
                Pos.CENTER_LEFT
        );

        gallery.getChildren().addAll(

                imageCard(
                        "/images/healthpassport/healthpassport1.jpg"
                ),

                imageCard(
                        "/images/healthpassport/healthpassport2.jpg"
                ),

                imageCard(
                        "/images/healthpassport/healthpassport3.jpg"
                ),

                imageCard(
                        "/images/healthpassport/healthpassport4.jpg"
                )
        );

        return gallery;
    }

    /*
     * ================================================================
     * IMAGE CARD
     * ================================================================
     */

    private VBox imageCard(String path) {

        VBox box = new VBox();

        box.setAlignment(
                Pos.CENTER
        );

        box.setPrefWidth(
                240
        );

        box.setPrefHeight(
                140
        );

        box.setMinWidth(
                220
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #cbd5e1;" +
                "-fx-border-radius: 14;"
        );

        URL resource =
                getClass().getResource(path);

        /*
         * Safe image loading.
         *
         * This prevents:
         *
         * Input stream must not be null
         */

        if (resource == null) {

            Label unavailable =
                    new Label(
                            "Image unavailable"
                    );

            unavailable.setStyle(
                    "-fx-text-fill: #64748b;" +
                    "-fx-font-size: 13px;"
            );

            box.getChildren().add(
                    unavailable
            );

            System.err.println(
                    "Health Passport image not found: "
                            + path
            );

            return box;
        }

        Image image =
                new Image(
                        resource.toExternalForm()
                );

        ImageView imageView =
                new ImageView(
                        image
                );

        imageView.setFitWidth(
                240
        );

        imageView.setFitHeight(
                140
        );

        imageView.setPreserveRatio(
                false
        );

        box.getChildren().add(
                imageView
        );

        return box;
    }

    /*
     * ================================================================
     * INFORMATION ROW
     * ================================================================
     */

    private HBox information(
            String title,
            String value
    ) {

        HBox row = new HBox(12);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(10)
        );

        row.setStyle(
                "-fx-background-color: rgba(255,255,255,0.85);" +
                "-fx-background-radius: 9;" +
                "-fx-border-color: #cbd5e1;" +
                "-fx-border-radius: 9;"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #475569;"
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        row.getChildren().addAll(
                titleLabel,
                spacer,
                valueLabel
        );

        return row;
    }

    /*
     * ================================================================
     * SUMMARY ROW
     * ================================================================
     */

    private HBox summary(
            String title,
            String value
    ) {

        HBox row = new HBox(12);

        row.setAlignment(
                Pos.CENTER_LEFT
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

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-text-fill: #475569;"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        row.getChildren().addAll(
                titleLabel,
                spacer,
                valueLabel
        );

        return row;
    }

    /*
     * ================================================================
     * HEALTH STATUS ROW
     * ================================================================
     */

    private HBox status(
            String title,
            String value,
            String condition
    ) {

        HBox row = new HBox(12);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(10)
        );

        row.setStyle(
                "-fx-background-color: rgba(255,255,255,0.85);" +
                "-fx-background-radius: 9;" +
                "-fx-border-color: #99f6e4;" +
                "-fx-border-radius: 9;"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #134e4a;"
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label conditionLabel =
                new Label(condition);

        conditionLabel.setStyle(
                "-fx-background-color: #dcfce7;" +
                "-fx-text-fill: #166534;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 5 9;" +
                "-fx-background-radius: 8;"
        );

        Region spacer1 =
                new Region();

        Region spacer2 =
                new Region();

        HBox.setHgrow(
                spacer1,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                spacer2,
                Priority.ALWAYS
        );

        row.getChildren().addAll(
                titleLabel,
                spacer1,
                valueLabel,
                spacer2,
                conditionLabel
        );

        return row;
    }

    /*
     * ================================================================
     * EMERGENCY BUTTON
     *
     * We create it locally because PatientUI.redButton()
     * does not exist in your current PatientUI.
     * ================================================================
     */

    private Button createEmergencyButton() {

        Button button =
                new Button(
                        "Emergency"
                );

        button.setPrefHeight(
                38
        );

        button.setStyle(
                "-fx-background-color: #dc2626;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 8 14;"
        );

        button.setOnAction(
                e -> stage.setScene(
                        new EmergencyAssistance(stage)
                                .getScene()
                )
        );

        return button;
    }
}

