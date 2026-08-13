package com.healthsphere.view.Patient;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class HealthPassport {

    private final PatientNavigator navigator;

    public HealthPassport(PatientNavigator navigator) {
        this.navigator = navigator;
    }

    public Scene getScene() {

        VBox content = new VBox(18);

        VBox personal =
                PatientUI.card("Personal Information");

        personal.getChildren().addAll(
                row("Name", "Sarah Johnson"),
                row("Date of Birth", "15 March 1995"),
                row("Blood Group", "O+"),
                row("Gender", "Female")
        );

        VBox medical =
                PatientUI.card("Medical Information");

        medical.getChildren().addAll(
                row("Allergies", "No known allergies"),
                row("Chronic Conditions", "None recorded"),
                row("Emergency Contact", "+91 XXXXX XXXXX"),
                row("Primary Physician", "Dr. Sarah Jenkins")
        );

        VBox insurance =
                PatientUI.card("Insurance Information");

        insurance.getChildren().addAll(
                row("Provider", "HealthCare Insurance"),
                row("Policy Number", "HS-2026-001"),
                row("Status", "Active")
        );

        content.getChildren().addAll(
                personal,
                medical,
                insurance
        );

        return PatientUI.createScene(
                navigator,
                "Health Passport",
                "Health Passport",
                "Your consolidated personal and medical health information.",
                content
        );
    }

    private HBox row(
            String label,
            String value
    ) {

        HBox row = new HBox(20);

        Label left =
                new Label(label);

        left.setStyle(
                "-fx-font-weight: bold;"
        );

        Label right =
                new Label(value);

        right.setStyle(
                "-fx-text-fill: #64748b;"
        );

        row.getChildren().addAll(
                left,
                right
        );

        return row;
    }
}