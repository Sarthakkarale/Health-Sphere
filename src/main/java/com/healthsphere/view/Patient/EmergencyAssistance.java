package com.healthsphere.view.Patient;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class EmergencyAssistance {

    private final PatientNavigator navigator;

    public EmergencyAssistance(
            PatientNavigator navigator
    ) {
        this.navigator = navigator;
    }

    public Scene getScene() {

        VBox content = new VBox(18);

        VBox emergency =
                PatientUI.card(
                        "Emergency Assistance"
                );

        Label warning =
                new Label(
                        "If you are experiencing a life-threatening emergency, contact your local emergency service immediately."
                );

        warning.setWrapText(true);

        warning.setStyle(
                "-fx-text-fill: #dc2626;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 15px;"
        );

        HBox buttons =
                new HBox(12);

        buttons.getChildren().addAll(

                PatientUI.button(
                        "Emergency Services",
                        () -> {}
                ),

                PatientUI.button(
                        "Find Nearby Hospital",
                        navigator::showSearchHospitals
                )
        );

        emergency.getChildren().addAll(
                warning,
                buttons
        );

        VBox contacts =
                PatientUI.card(
                        "Emergency Contacts"
                );

        contacts.getChildren().addAll(
                contact(
                        "Emergency Contact",
                        "+91 XXXXX XXXXX"
                ),

                contact(
                        "Primary Physician",
                        "Dr. Sarah Jenkins"
                ),

                contact(
                        "Nearest Hospital",
                        "Apollo Hospitals"
                )
        );

        content.getChildren().addAll(
                emergency,
                contacts
        );

        return PatientUI.createScene(
                navigator,
                "Emergency Assistance",
                "Emergency Assistance",
                "Quick access to emergency healthcare resources.",
                content
        );
    }

    private HBox contact(
            String name,
            String value
    ) {

        HBox row =
                new HBox(20);

        row.getChildren().addAll(
                new Label(name),
                PatientUI.muted(value)
        );

        return row;
    }
}