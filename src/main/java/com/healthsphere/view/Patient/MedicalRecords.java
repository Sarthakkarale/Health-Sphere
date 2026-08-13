package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MedicalRecords {

    private final PatientNavigator navigator;

    public MedicalRecords(PatientNavigator navigator) {
        this.navigator = navigator;
    }

    public Scene getScene() {

        VBox content = new VBox(18);

        VBox records =
                PatientUI.card("Medical Records");

        records.getChildren().addAll(
                record(
                        "Blood Test Report",
                        "Laboratory",
                        "12 August 2026"
                ),

                record(
                        "Cardiology Consultation",
                        "Cardiology",
                        "05 August 2026"
                ),

                record(
                        "General Health Checkup",
                        "General Medicine",
                        "20 July 2026"
                ),

                record(
                        "Prescription",
                        "Pharmacy",
                        "20 July 2026"
                )
        );

        VBox contentInfo =
                PatientUI.card("Record Information");

        contentInfo.getChildren().addAll(
                PatientUI.muted(
                        "Your medical records are securely organized in one place."
                ),
                PatientUI.muted(
                        "You can review your previous reports and consultations."
                )
        );

        content.getChildren().addAll(
                records,
                contentInfo
        );

        return PatientUI.createScene(
                navigator,
                "Medical Records",
                "Medical Records",
                "View and manage your medical reports, prescriptions and consultations.",
                content
        );
    }

    private HBox record(
            String name,
            String department,
            String date
    ) {

        HBox row =
                new HBox(15);

        row.setPadding(
                new Insets(14)
        );

        VBox details =
                new VBox(5);

        Label nameLabel =
                new Label(name);

        nameLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;"
        );

        details.getChildren().addAll(
                nameLabel,
                PatientUI.muted(department),
                PatientUI.muted(date)
        );

        Button view =
                PatientUI.button(
                        "View",
                        () -> {}
                );

        HBox.setHgrow(
                details,
                Priority.ALWAYS
        );

        row.getChildren().addAll(
                details,
                view
        );

        return row;
    }
}