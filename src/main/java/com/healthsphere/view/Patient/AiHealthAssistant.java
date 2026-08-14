
package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AiHealthAssistant {

    private final Stage stage;

    public AiHealthAssistant(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {

        VBox content = new VBox(22);
        content.setPadding(new Insets(25));

        // =========================================================
        // PAGE HEADING
        // =========================================================

        Label title = new Label("AI Health Assistant");

        title.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label subtitle = new Label(
                "Get intelligent health guidance and understand your health information."
        );

        subtitle.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #64748b;"
        );

        VBox heading = new VBox(
                5,
                title,
                subtitle
        );

        // =========================================================
        // IMAGE GALLERY
        // =========================================================

        HBox gallery = createImageGallery();

        // =========================================================
        // AI ASSISTANT CARD
        // =========================================================

        VBox assistantCard = new VBox(15);

        assistantCard.setPadding(
                new Insets(22)
        );

        assistantCard.setStyle(
                "-fx-background-color: #ede9fe;" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: #c4b5fd;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 16;"
        );

        Label assistantTitle = new Label(
                "🤖  How can I help you?"
        );

        assistantTitle.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #4c1d95;"
        );

        Label message = new Label(
                "Ask me about symptoms, medications, appointments, medical records or general health information."
        );

        message.setWrapText(true);

        message.setStyle(
                "-fx-text-fill: #475569;" +
                "-fx-font-size: 14px;"
        );

        // =========================================================
        // QUESTION FIELD
        // =========================================================

        TextField question = new TextField();

        question.setPromptText(
                "Type your health question..."
        );

        question.setPrefHeight(45);

        question.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #c4b5fd;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 0 12;"
        );

        // =========================================================
        // ASK BUTTON
        // =========================================================

        Button ask = new Button("Ask AI");

        ask.setPrefHeight(45);
        ask.setPrefWidth(110);

        ask.setStyle(
                "-fx-background-color: #7c3aed;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
        );

        // =========================================================
        // RESPONSE AREA
        // =========================================================

        TextArea response = new TextArea();

        response.setEditable(false);
        response.setWrapText(true);
        response.setPrefRowCount(6);

        response.setPromptText(
                "AI response will appear here..."
        );

        response.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #c4b5fd;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        // =========================================================
        // ASK ACTION
        // =========================================================

        ask.setOnAction(e -> {

            String text = question.getText();

            if (text == null ||
                    text.trim().isEmpty()) {

                response.setText(
                        "Please enter a health-related question."
                );

                return;
            }

            response.setText(
                    "AI Health Assistant\n\n" +
                    "Your question:\n" +
                    text.trim() +
                    "\n\n" +
                    "Thank you for your question. " +
                    "This demo assistant can help you understand " +
                    "general health information, appointments, " +
                    "medical records and medications.\n\n" +
                    "Please remember that this assistant does not " +
                    "replace a qualified healthcare professional. " +
                    "For diagnosis, treatment decisions or urgent " +
                    "medical concerns, please consult a healthcare professional."
            );
        });

        // =========================================================
        // ENTER KEY SUPPORT
        // =========================================================

        question.setOnAction(e -> ask.fire());

        // =========================================================
        // INPUT ROW
        // =========================================================

        HBox inputRow = new HBox(10);

        HBox.setHgrow(
                question,
                Priority.ALWAYS
        );

        inputRow.getChildren().addAll(
                question,
                ask
        );

        assistantCard.getChildren().addAll(
                assistantTitle,
                message,
                inputRow,
                response
        );

        // =========================================================
        // HEALTH INSIGHTS CARD
        // =========================================================

        VBox insights = createCard(
                "✨  Health Insights",
                "#dbeafe"
        );

        insights.getChildren().addAll(

                insight(
                        "Daily Health Check",
                        "Keep track of your vital signs and maintain regular health monitoring."
                ),

                insight(
                        "Medication Reminder",
                        "Review your active medications and follow your prescribed schedule."
                ),

                insight(
                        "Wellness Recommendation",
                        "Maintain a balanced diet, regular physical activity and adequate sleep."
                )
        );

        // =========================================================
        // QUICK QUESTIONS
        // =========================================================

        VBox quickQuestions = createCard(
                "💡  Quick Questions",
                "#dcfce7"
        );

        HBox quickRow = new HBox(10);

        Button symptomsButton = quickButton(
                "Understand symptoms"
        );

        Button medicationButton = quickButton(
                "Medication information"
        );

        Button appointmentButton = quickButton(
                "Prepare for appointment"
        );

        Button recordsButton = quickButton(
                "Understand medical records"
        );

        symptomsButton.setOnAction(e -> {
            question.setText(
                    "Can you help me understand my symptoms?"
            );
            ask.fire();
        });

        medicationButton.setOnAction(e -> {
            question.setText(
                    "Can you explain general medication safety?"
            );
            ask.fire();
        });

        appointmentButton.setOnAction(e -> {
            question.setText(
                    "How should I prepare for my appointment?"
            );
            ask.fire();
        });

        recordsButton.setOnAction(e -> {
            question.setText(
                    "Can you help me understand my medical records?"
            );
            ask.fire();
        });

        quickRow.getChildren().addAll(
                symptomsButton,
                medicationButton,
                appointmentButton,
                recordsButton
        );

        quickQuestions.getChildren().add(
                quickRow
        );

        // =========================================================
        // ADD CONTENT
        // =========================================================

        content.getChildren().addAll(
                heading,
                gallery,
                assistantCard,
                insights,
                quickQuestions
        );

        // =========================================================
        // SCROLL
        // =========================================================

        ScrollPane scroll = new ScrollPane(
                content
        );

        scroll.setFitToWidth(true);

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scroll.setStyle(
                "-fx-background-color: #f1f5f9;"
        );

        // =========================================================
        // USE COMMON PATIENT UI
        //
        // IMPORTANT:
        // PatientUI must contain the left sidebar.
        // =========================================================

        return PatientUI.createScene(
                stage,
                "AI Assistant",
                "AI Health Assistant",
                "Get intelligent health guidance and understand your health information.",
                content
        );
    }

    // =============================================================
    // IMAGE GALLERY
    // =============================================================

    private HBox createImageGallery() {

        HBox gallery = new HBox(15);

        gallery.setAlignment(
                Pos.CENTER_LEFT
        );

        gallery.getChildren().addAll(

                imageCard(
                        "/images/ai/ai1.jpg"
                ),

                imageCard(
                        "/images/ai/ai2.jpg"
                ),

                imageCard(
                        "/images/ai/ai3.jpg"
                ),

                imageCard(
                        "/images/ai/ai4.jpg"
                )
        );

        return gallery;
    }

    // =============================================================
    // IMAGE CARD
    // =============================================================

    private VBox imageCard(String path) {

        VBox box = new VBox();

        box.setPrefWidth(260);
        box.setPrefHeight(145);

        box.setAlignment(
                Pos.CENTER
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #cbd5e1;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 14;"
        );

        try {

            var stream =
                    getClass()
                            .getResourceAsStream(path);

            if (stream == null) {

                Label error =
                        new Label(
                                "Image unavailable"
                        );

                error.setStyle(
                        "-fx-text-fill: #64748b;"
                );

                box.getChildren().add(error);

                return box;
            }

            Image image =
                    new Image(stream);

            ImageView imageView =
                    new ImageView(image);

            imageView.setFitWidth(260);
            imageView.setFitHeight(145);

            imageView.setPreserveRatio(false);

            box.getChildren().add(
                    imageView
            );

        } catch (Exception e) {

            Label error =
                    new Label(
                            "Image unavailable"
                    );

            error.setStyle(
                    "-fx-text-fill: #64748b;"
            );

            box.getChildren().add(error);
        }

        return box;
    }

    // =============================================================
    // COLORED CARD
    // =============================================================

    private VBox createCard(
            String title,
            String color
    ) {

        VBox card = new VBox(14);

        card.setPadding(
                new Insets(20)
        );

        card.setStyle(
                "-fx-background-color: " +
                color +
                ";" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #cbd5e1;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 14;"
        );

        Label label =
                new Label(title);

        label.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        card.getChildren().add(
                label
        );

        return card;
    }

    // =============================================================
    // INSIGHT
    // =============================================================

    private VBox insight(
            String title,
            String description
    ) {

        VBox box =
                new VBox(5);

        box.setPadding(
                new Insets(12)
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 10;"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setWrapText(true);

        descriptionLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        box.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );

        return box;
    }

    // =============================================================
    // QUICK QUESTION BUTTON
    // =============================================================

    private Button quickButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setPrefHeight(42);

        button.setStyle(
                "-fx-background-color: white;" +
                "-fx-text-fill: #166534;" +
                "-fx-font-weight: bold;" +
                "-fx-border-color: #86efac;" +
                "-fx-border-width: 1;" +
                "-fx-background-radius: 8;" +
                "-fx-border-radius: 8;" +
                "-fx-padding: 8 14;" +
                "-fx-cursor: hand;"
        );

        return button;
    }
}

