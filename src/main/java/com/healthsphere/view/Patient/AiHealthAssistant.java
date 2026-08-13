package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class AiHealthAssistant {

    private final PatientNavigator navigator;

    public AiHealthAssistant(PatientNavigator navigator) {
        this.navigator = navigator;
    }

    public Scene getScene() {

        VBox content = new VBox(18);

        VBox assistant =
                PatientUI.card(
                        "MediNexus AI Health Assistant"
                );

        Label intro =
                new Label(
                        "Ask questions about your health, symptoms, medications or medical records."
                );

        intro.setWrapText(true);

        intro.setStyle(
                "-fx-text-fill: #64748b;"
        );

        TextArea conversation =
                new TextArea();

        conversation.setEditable(false);
        conversation.setWrapText(true);
        conversation.setPrefHeight(350);

        conversation.setText(
                "MediNexus AI:\n" +
                "Hello Sarah! How can I help you today?\n\n"
        );

        TextField question =
                new TextField();

        question.setPromptText(
                "Type your health question..."
        );

        Button ask =
                PatientUI.button(
                        "Ask AI",
                        () -> {

                            String text =
                                    question.getText().trim();

                            if (!text.isEmpty()) {

                                conversation.appendText(
                                        "You:\n" +
                                        text +
                                        "\n\n"
                                );

                                conversation.appendText(
                                        "MediNexus AI:\n" +
                                        "I can help you understand general health information. " +
                                        "For urgent or serious symptoms, please contact a qualified healthcare professional.\n\n"
                                );

                                question.clear();
                            }
                        }
                );

        HBox input =
                new HBox(10);

        HBox.setHgrow(
                question,
                Priority.ALWAYS
        );

        input.getChildren().addAll(
                question,
                ask
        );

        assistant.getChildren().addAll(
                intro,
                conversation,
                input
        );

        VBox quick =
                PatientUI.card("Quick Questions");

        HBox quickButtons =
                new HBox(10);

        quickButtons.getChildren().addAll(

                PatientUI.button(
                        "Explain my records",
                        () -> {}
                ),

                PatientUI.button(
                        "Medication information",
                        () -> {}
                ),

                PatientUI.button(
                        "Prepare for appointment",
                        () -> {}
                )
        );

        quick.getChildren().add(
                quickButtons
        );

        content.getChildren().addAll(
                assistant,
                quick
        );

        return PatientUI.createScene(
                navigator,
                "AI Health Assistant",
                "AI Health Assistant",
                "Get personalized health information and guidance.",
                content
        );
    }
}