
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

public class MedicalRecords {

    private final Stage stage;

    public MedicalRecords(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {

        // =========================================================
        // MAIN CONTENT
        // PatientUI will provide:
        //
        // LEFT SIDEBAR
        // TOP HEADER
        // PAGE TITLE
        // PAGE SUBTITLE
        //
        // =========================================================

        VBox content = new VBox(22);

        content.setPadding(
                new Insets(0)
        );

        // =========================================================
        // IMAGE GALLERY
        // =========================================================

        HBox gallery = createImageGallery();

        // =========================================================
        // RECENT MEDICAL RECORDS
        // =========================================================

        VBox records =
                PatientUI.coloredCard(
                        "📄  Recent Medical Records",
                        "#dbeafe"
                );

        records.getChildren().addAll(

                record(
                        "Complete Blood Count",
                        "Laboratory Report",
                        "12 August 2026"
                ),

                record(
                        "Cardiology Consultation",
                        "Doctor Consultation",
                        "05 August 2026"
                ),

                record(
                        "Blood Pressure Monitoring",
                        "Diagnostic Report",
                        "29 July 2026"
                ),

                record(
                        "Annual Health Checkup",
                        "Health Examination",
                        "15 July 2026"
                )
        );

        // =========================================================
        // PRESCRIPTIONS
        // =========================================================

        VBox prescriptions =
                PatientUI.coloredCard(
                        "💊  Prescriptions",
                        "#dcfce7"
                );

        prescriptions.getChildren().addAll(

                prescription(
                        "Amlodipine",
                        "5 mg • Once daily"
                ),

                prescription(
                        "Vitamin D3",
                        "1000 IU • Once daily"
                ),

                prescription(
                        "Omega 3",
                        "1000 mg • Once daily"
                )
        );

        // =========================================================
        // LOWER CONTENT ROW
        // =========================================================

        HBox lower =
                new HBox(18);

        lower.setAlignment(
                Pos.TOP_LEFT
        );

        HBox.setHgrow(
                records,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                prescriptions,
                Priority.ALWAYS
        );

        records.setMaxWidth(
                Double.MAX_VALUE
        );

        prescriptions.setMaxWidth(
                Double.MAX_VALUE
        );

        lower.getChildren().addAll(
                records,
                prescriptions
        );

        // =========================================================
        // QUICK ACTIONS
        // =========================================================

        VBox quickActions =
                PatientUI.coloredCard(
                        "⚡  Quick Actions",
                        "#ede9fe"
                );

        HBox actions =
                new HBox(12);

        actions.setAlignment(
                Pos.CENTER_LEFT
        );

        Button healthPassport =
                PatientUI.button(
                        "Health Passport",
                        () -> stage.setScene(
                                new HealthPassport(stage)
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

        Button aiAssistant =
                PatientUI.button(
                        "AI Health Assistant",
                        () -> stage.setScene(
                                new AiHealthAssistant(stage)
                                        .getScene()
                        )
                );

        actions.getChildren().addAll(
                healthPassport,
                appointments,
                aiAssistant
        );

        quickActions.getChildren().add(
                actions
        );

        // =========================================================
        // ADD CONTENT
        // =========================================================

        content.getChildren().addAll(
                gallery,
                lower,
                quickActions
        );

        // =========================================================
        // SCROLL CONTENT
        //
        // IMPORTANT:
        // PatientUI creates the main ScrollPane.
        // Therefore we DO NOT create another ScrollPane here.
        // =========================================================

        return PatientUI.createScene(
                stage,
                "Medical Records",
                "Medical Records",
                "Access your medical history, reports, prescriptions and clinical documents.",
                content
        );
    }

    // =============================================================
    // IMAGE GALLERY
    // =============================================================

    private HBox createImageGallery() {

        HBox gallery =
                new HBox(15);

        gallery.setAlignment(
                Pos.CENTER_LEFT
        );

        gallery.getChildren().addAll(

                imageCard(
                        "/images/medicalrecords/medicalrecord1.jpg"
                ),

                imageCard(
                        "/images/medicalrecords/medicalrecord2.jpg"
                ),

                imageCard(
                        "/images/medicalrecords/medicalrecord3.jpg"
                ),

                imageCard(
                        "/images/medicalrecords/medicalrecord4.jpg"
                )
        );

        return gallery;
    }

    // =============================================================
    // IMAGE CARD
    // =============================================================

    private VBox imageCard(
            String path
    ) {

        VBox box =
                new VBox();

        box.setAlignment(
                Pos.CENTER
        );

        box.setPrefWidth(
                260
        );

        box.setPrefHeight(
                145
        );

        box.setMinWidth(
                260
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #e2e8f0;" +
                "-fx-border-radius: 14;"
        );

        // ---------------------------------------------------------
        // SAFE IMAGE LOADING
        // ---------------------------------------------------------

        var resource =
                getClass().getResource(path);

        if (resource == null) {

            Label error =
                    new Label(
                            "Image unavailable"
                    );

            error.setStyle(
                    "-fx-text-fill: #64748b;" +
                    "-fx-font-size: 13px;"
            );

            box.getChildren().add(
                    error
            );

            System.err.println(
                    "Medical Records image not found: "
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
                260
        );

        imageView.setFitHeight(
                145
        );

        imageView.setPreserveRatio(
                false
        );

        box.getChildren().add(
                imageView
        );

        return box;
    }

    // =============================================================
    // MEDICAL RECORD
    // =============================================================

    private VBox record(
            String title,
            String type,
            String date
    ) {

        VBox box =
                new VBox(7);

        box.setPadding(
                new Insets(12)
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 10;"
        );

        Label name =
                new Label(title);

        name.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label typeLabel =
                new Label(type);

        typeLabel.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 13px;"
        );

        Label dateLabel =
                new Label(date);

        dateLabel.setStyle(
                "-fx-text-fill: #2563eb;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;"
        );

        Button view =
                PatientUI.secondaryButton(
                        "View Record",
                        () -> showRecord(
                                title,
                                type,
                                date
                        )
                );

        box.getChildren().addAll(
                name,
                typeLabel,
                dateLabel,
                view
        );

        return box;
    }

    // =============================================================
    // RECORD INFORMATION
    // =============================================================

    private void showRecord(
            String title,
            String type,
            String date
    ) {

        javafx.scene.control.Alert alert =
                new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.INFORMATION
                );

        alert.setTitle(
                "Medical Record"
        );

        alert.setHeaderText(
                title
        );

        alert.setContentText(
                "Record Type: " + type +
                "\nDate: " + date +
                "\n\nThis medical record is available in your HealthSphere patient profile."
        );

        alert.showAndWait();
    }

    // =============================================================
    // PRESCRIPTION
    // =============================================================

    private HBox prescription(
            String medicine,
            String dosage
    ) {

        HBox row =
                new HBox(12);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(10)
        );

        row.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 9;" +
                "-fx-border-color: #bbf7d0;" +
                "-fx-border-radius: 9;"
        );

        Label icon =
                new Label("💊");

        icon.setStyle(
                "-fx-font-size: 22px;"
        );

        VBox info =
                new VBox(3);

        Label name =
                new Label(medicine);

        name.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #0f172a;"
        );

        Label dose =
                new Label(dosage);

        dose.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 13px;"
        );

        info.getChildren().addAll(
                name,
                dose
        );

        row.getChildren().addAll(
                icon,
                info
        );

        return row;
    }
}

