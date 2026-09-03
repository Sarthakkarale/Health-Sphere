package com.healthsphere.view.Patient;

import java.util.List;

import com.healthsphere.controller.patient.AIController;
import com.healthsphere.model.HealthAssessment;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AiHealthAssistant {

    private final Stage stage;
    private final AIController aiController;

    private VBox resultContainer;
    private TextField questionField;
    private Button askButton;

    public AiHealthAssistant(Stage stage) {
        this.stage = stage;
        this.aiController = new AIController();
    }

    public Scene getScene() {

        VBox content = new VBox(22);
        content.setPadding(new Insets(25));
        content.setFillWidth(true);

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
                "Get intelligent symptom analysis, disease predictions, and personalized care recommendations."
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
        // AI INPUT CARD
        // =========================================================

        VBox assistantCard = new VBox(15);
        assistantCard.setPadding(new Insets(22));
        assistantCard.setStyle(
                "-fx-background-color: #ede9fe;" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: #c4b5fd;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 16;"
        );

        Label assistantTitle = new Label("🤖 Describe Your Symptoms");
        assistantTitle.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #4c1d95;"
        );

        Label message = new Label(
                "Describe how you feel (e.g. 'I have a high fever, dry cough, and headache'). Our AI will analyze your symptoms and generate an interactive disease prediction card."
        );
        message.setWrapText(true);
        message.setStyle("-fx-text-fill: #475569; -fx-font-size: 14px;");

        // =========================================================
        // QUESTION INPUT ROW
        // =========================================================

        questionField = new TextField();
        questionField.setPromptText("Type your symptoms or health question here...");
        questionField.setPrefHeight(45);
        questionField.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #c4b5fd;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 0 12;" +
                "-fx-font-size: 14px;"
        );

        askButton = new Button("Analyze AI");
        askButton.setPrefHeight(45);
        askButton.setMinWidth(120);
        askButton.setStyle(
                "-fx-background-color: #7c3aed;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
        );

        HBox inputRow = new HBox(10);
        HBox.setHgrow(questionField, Priority.ALWAYS);
        inputRow.getChildren().addAll(questionField, askButton);

        // =========================================================
        // RESULT CONTAINER (REPLACES TEXTAREA WITH INTERACTIVE CARDS)
        // =========================================================

        resultContainer = new VBox(15);
        resultContainer.setPadding(new Insets(10, 0, 0, 0));

        // Initial welcome state
        renderInitialWelcomeCard();

        askButton.setOnAction(e -> handleAskAI());
        questionField.setOnAction(e -> handleAskAI());

        assistantCard.getChildren().addAll(
                assistantTitle,
                message,
                inputRow,
                resultContainer
        );

        // =========================================================
        // HEALTH INSIGHTS CARD
        // =========================================================

        VBox insights = createCard("✨  Health Monitoring Tips", "#dbeafe");
        insights.getChildren().addAll(
                insight("Track Symptoms Early", "Log onset duration, intensity, and secondary signs to assist clinical evaluation."),
                insight("Hydration & Immunity", "Maintain consistent fluid intake and monitor body temperature during viral symptoms."),
                insight("When to Seek Professional Care", "Severe chest pain, sudden breathlessness, or persistent high fever require immediate emergency care.")
        );

        // =========================================================
        // QUICK QUESTION BUTTONS
        // =========================================================

        VBox quickQuestions = createCard("💡  Common Symptom Checkers", "#dcfce7");
        HBox quickRow = new HBox(10);

        Button feverBtn = quickButton("Fever & Cough");
        Button headacheBtn = quickButton("Severe Headache");
        Button stomachBtn = quickButton("Stomach Pain & Nausea");
        Button allergyBtn = quickButton("Sneezing & Allergic Rash");

        feverBtn.setOnAction(e -> triggerQuickQuery("I have a fever, cough, and body ache for 2 days."));
        headacheBtn.setOnAction(e -> triggerQuickQuery("I am suffering from a severe throbbing headache and eye strain."));
        stomachBtn.setOnAction(e -> triggerQuickQuery("I have stomach cramps, nausea, and indigestion after eating."));
        allergyBtn.setOnAction(e -> triggerQuickQuery("I am experiencing continuous sneezing, runny nose, and skin rash."));

        quickRow.getChildren().addAll(feverBtn, headacheBtn, stomachBtn, allergyBtn);
        quickQuestions.getChildren().add(quickRow);

        content.getChildren().addAll(
                heading,
                gallery,
                assistantCard,
                insights,
                quickQuestions
        );

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color: #f1f5f9;");

        return PatientUI.createScene(
                stage,
                "AI Assistant",
                "AI Health Assistant",
                "Get intelligent health guidance and understand your symptoms.",
                scroll
        );
    }

    private void triggerQuickQuery(String text) {
        questionField.setText(text);
        handleAskAI();
    }

    /**
     * Handles symptom query submission asynchronously via AIController.
     */
    private void handleAskAI() {
        String input = questionField.getText();
        if (input == null || input.trim().isEmpty()) {
            renderScopeNoticeCard("Please enter a description of your symptoms or health concern.");
            return;
        }

        renderLoadingCard();

        Task<HealthAssessment> task = new Task<>() {
            @Override
            protected HealthAssessment call() {
                return aiController.analyzeSymptoms(input.trim());
            }
        };

        task.setOnSucceeded(e -> {
            HealthAssessment assessment = task.getValue();
            Platform.runLater(() -> renderAssessmentResult(assessment));
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> renderScopeNoticeCard("Unable to process your request at this time. Please try again."));
        });

        new Thread(task).start();
    }

    /**
     * Render Initial Welcome Card
     */
    private void renderInitialWelcomeCard() {
        resultContainer.getChildren().clear();

        VBox welcome = new VBox(10);
        welcome.setPadding(new Insets(18));
        welcome.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #ddd6fe;" +
                "-fx-border-radius: 12;"
        );

        Label title = new Label("👋  Ready for Symptom Analysis");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #5b21b6;");

        Label desc = new Label("Enter your symptoms above or select a quick checker to receive an instant AI disease prediction card with care recommendations.");
        desc.setWrapText(true);
        desc.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");

        welcome.getChildren().addAll(title, desc);
        resultContainer.getChildren().add(welcome);
    }

    /**
     * Render Loading State
     */
    private void renderLoadingCard() {
        resultContainer.getChildren().clear();

        VBox loading = new VBox(14);
        loading.setAlignment(Pos.CENTER);
        loading.setPadding(new Insets(25));
        loading.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #c4b5fd;" +
                "-fx-border-radius: 12;"
        );

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(40, 40);

        Label label = new Label("🤖 Analyzing symptoms with AI...");
        label.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #6d28d9;");

        loading.getChildren().addAll(spinner, label);
        resultContainer.getChildren().add(loading);
    }

    /**
     * Render Scope Restriction Notice Card for non-health queries.
     */
    private void renderScopeNoticeCard(String message) {
        resultContainer.getChildren().clear();

        VBox notice = new VBox(12);
        notice.setPadding(new Insets(18));
        notice.setStyle(
                "-fx-background-color: #fffbe6;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #fde047;" +
                "-fx-border-width: 1 1 1 5;" +
                "-fx-border-radius: 12;"
        );

        Label title = new Label("⚠️ Health Scope Notice");
        title.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #a16207;");

        Label msg = new Label(message);
        msg.setWrapText(true);
        msg.setStyle("-fx-font-size: 14px; -fx-text-fill: #854d0e;");

        notice.getChildren().addAll(title, msg);
        resultContainer.getChildren().add(notice);
    }

    /**
     * Render Interactive Disease Prediction Cards (No Markdown symbols, structured UI components).
     */
    private void renderAssessmentResult(HealthAssessment assessment) {
        resultContainer.getChildren().clear();

        if (assessment == null || !assessment.isHealthRelated()) {
            String msg = (assessment != null && assessment.getRejectionMessage() != null)
                    ? assessment.getRejectionMessage()
                    : "I am specialized strictly in symptom and medical health analysis. Please ask a symptom-related question.";
            renderScopeNoticeCard(msg);
            return;
        }

        // =========================================================
        // CARD 1: PREDICTED CONDITION & SEVERITY BADGE
        // =========================================================

        VBox conditionCard = new VBox(12);
        conditionCard.setPadding(new Insets(20));
        
        String severity = assessment.getSeverityLevel();
        String badgeColor;
        String badgeText;
        String cardBorderColor;

        if ("Emergency".equalsIgnoreCase(severity)) {
            badgeColor = "#dc2626";
            badgeText = "🆘 EMERGENCY CARE REQUIRED";
            cardBorderColor = "#ef4444";
        } else if ("Severe".equalsIgnoreCase(severity)) {
            badgeColor = "#ea580c";
            badgeText = "🔴 SEVERE - PROMPT CLINICAL CARE RECOMMENDED";
            cardBorderColor = "#f97316";
        } else if ("Moderate".equalsIgnoreCase(severity)) {
            badgeColor = "#d97706";
            badgeText = "🟡 MODERATE SEVERITY";
            cardBorderColor = "#f59e0b";
        } else {
            badgeColor = "#16a34a";
            badgeText = "🟢 MILD SEVERITY";
            cardBorderColor = "#22c55e";
        }

        conditionCard.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: " + cardBorderColor + ";" +
                "-fx-border-width: 0 0 0 6;" +
                "-fx-border-radius: 14;"
        );

        HBox badgeRow = new HBox(10);
        badgeRow.setAlignment(Pos.CENTER_LEFT);

        Label severityBadge = new Label(badgeText);
        severityBadge.setStyle(
                "-fx-background-color: " + badgeColor + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 12px;" +
                "-fx-padding: 4 12;" +
                "-fx-background-radius: 20;"
        );

        Label confidenceBadge = new Label("Confidence: " + assessment.getConfidenceScore());
        confidenceBadge.setStyle(
                "-fx-background-color: #f1f5f9;" +
                "-fx-text-fill: #475569;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 12px;" +
                "-fx-padding: 4 10;" +
                "-fx-background-radius: 20;"
        );

        badgeRow.getChildren().addAll(severityBadge, confidenceBadge);

        Label predictedHeader = new Label("Predicted Condition:");
        predictedHeader.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-font-weight: bold;");

        Label diseaseName = new Label(assessment.getPredictedCondition());
        diseaseName.setWrapText(true);
        diseaseName.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label overview = new Label(assessment.getOverview());
        overview.setWrapText(true);
        overview.setStyle("-fx-font-size: 14px; -fx-text-fill: #334155;");

        conditionCard.getChildren().addAll(badgeRow, predictedHeader, diseaseName, overview);

        // =========================================================
        // CARD 2: DETECTED SYMPTOMS CHIPS
        // =========================================================

        VBox symptomsCard = new VBox(10);
        symptomsCard.setPadding(new Insets(16));
        symptomsCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #e2e8f0; -fx-border-radius: 12;");

        Label symptomsTitle = new Label("🔍  Identified Symptoms");
        symptomsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1e293b;");

        FlowPane symptomsPane = new FlowPane();
        symptomsPane.setHgap(8);
        symptomsPane.setVgap(8);

        List<String> symptoms = assessment.getDetectedSymptoms();
        if (symptoms != null && !symptoms.isEmpty()) {
            for (String symptom : symptoms) {
                Label chip = new Label(symptom);
                chip.setStyle(
                        "-fx-background-color: #f3e8ff;" +
                        "-fx-text-fill: #6b21a8;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;" +
                        "-fx-padding: 5 12;" +
                        "-fx-background-radius: 16;"
                );
                symptomsPane.getChildren().add(chip);
            }
        } else {
            Label chip = new Label("General Symptoms Reported");
            chip.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-padding: 5 12; -fx-background-radius: 16;");
            symptomsPane.getChildren().add(chip);
        }

        symptomsCard.getChildren().addAll(symptomsTitle, symptomsPane);

        // =========================================================
        // CARD 3: RECOMMENDED ACTIONS & PRECAUTIONS
        // =========================================================

        VBox actionsCard = new VBox(12);
        actionsCard.setPadding(new Insets(18));
        actionsCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #e2e8f0; -fx-border-radius: 12;");

        Label actionsTitle = new Label("📋  Recommended Care & Next Steps");
        actionsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #0f172a;");

        VBox actionItems = new VBox(8);
        List<String> actions = assessment.getRecommendedActions();
        if (actions != null) {
            for (String act : actions) {
                HBox row = new HBox(8);
                row.setAlignment(Pos.TOP_LEFT);
                Label icon = new Label("✓");
                icon.setStyle("-fx-font-weight: bold; -fx-text-fill: #16a34a;");
                Label text = new Label(act);
                text.setWrapText(true);
                text.setStyle("-fx-text-fill: #334155; -fx-font-size: 14px;");
                row.getChildren().addAll(icon, text);
                actionItems.getChildren().add(row);
            }
        }

        List<String> precautions = assessment.getPrecautions();
        if (precautions != null && !precautions.isEmpty()) {
            Label precHeader = new Label("Precautions:");
            precHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #d97706; -fx-padding: 6 0 2 0;");
            actionItems.getChildren().add(precHeader);

            for (String prec : precautions) {
                HBox row = new HBox(8);
                row.setAlignment(Pos.TOP_LEFT);
                Label icon = new Label("⚠️");
                Label text = new Label(prec);
                text.setWrapText(true);
                text.setStyle("-fx-text-fill: #475569; -fx-font-size: 13px;");
                row.getChildren().addAll(icon, text);
                actionItems.getChildren().add(row);
            }
        }

        actionsCard.getChildren().addAll(actionsTitle, actionItems);

        // =========================================================
        // CARD 4: CLINICAL ADVICE BANNER
        // =========================================================

        VBox adviceCard = new VBox(8);
        adviceCard.setPadding(new Insets(14));
        adviceCard.setStyle(
                "-fx-background-color: #f0fdf4;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #bbf7d0;" +
                "-fx-border-radius: 10;"
        );

        Label adviceTitle = new Label("🩺  When to Consult a Physician");
        adviceTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #15803d;");

        Label adviceText = new Label(assessment.getWhenToSeeDoctor());
        adviceText.setWrapText(true);
        adviceText.setStyle("-fx-text-fill: #166534; -fx-font-size: 13px;");

        adviceCard.getChildren().addAll(adviceTitle, adviceText);

        // =========================================================
        // CARD 5: INTERACTIVE ACTION BUTTONS
        // =========================================================

        HBox actionBar = new HBox(12);
        actionBar.setAlignment(Pos.CENTER_LEFT);

        Button bookApptBtn = PatientUI.button(
                "Book Appointment for " + assessment.getPredictedCondition(),
                () -> showBookAppointment(assessment)
        );

        Button clearBtn = PatientUI.secondaryButton(
                "Analyze Another Query",
                () -> {
                    questionField.clear();
                    renderInitialWelcomeCard();
                }
        );

        actionBar.getChildren().addAll(bookApptBtn, clearBtn);

        if ("Emergency".equalsIgnoreCase(severity) || "Severe".equalsIgnoreCase(severity)) {
            Button emergencyBtn = new Button("🚨 Emergency Support");
            emergencyBtn.setPrefHeight(40);
            emergencyBtn.setStyle(
                    "-fx-background-color: #dc2626;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;"
            );
            emergencyBtn.setOnAction(e -> showEmergencyAssistance());
            actionBar.getChildren().add(1, emergencyBtn);
        }

        // Add all interactive UI cards to result container
        resultContainer.getChildren().addAll(
                conditionCard,
                symptomsCard,
                actionsCard,
                adviceCard,
                actionBar
        );
    }

    private void showBookAppointment(HealthAssessment assessment) {
        String condition = assessment != null ? assessment.getPredictedCondition() : "";
        stage.setScene(
                new BookAppointment(stage, condition)
                        .getScene()
        );
        stage.show();
        stage.setMaximized(true);
    }

    private void showEmergencyAssistance() {
        stage.setScene(
                new EmergencyAssistance(stage)
                        .getScene()
        );
        stage.show();
        stage.setMaximized(true);
    }

    // =============================================================
    // IMAGE GALLERY
    // =============================================================

    private HBox createImageGallery() {
        HBox gallery = new HBox(15);
        gallery.setAlignment(Pos.CENTER_LEFT);
        gallery.getChildren().addAll(
                imageCard("/images/ai/ai1.jpg"),
                imageCard("/images/ai/ai2.jpg"),
                imageCard("/images/ai/ai3.jpg"),
                imageCard("/images/ai/ai4.jpg")
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
        box.setAlignment(Pos.CENTER);
        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #cbd5e1;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 14;"
        );

        try {
            var stream = getClass().getResourceAsStream(path);
            if (stream == null) {
                Label error = new Label("Image unavailable");
                error.setStyle("-fx-text-fill: #64748b;");
                box.getChildren().add(error);
                return box;
            }
            Image image = new Image(stream);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(260);
            imageView.setFitHeight(145);
            imageView.setPreserveRatio(false);
            box.getChildren().add(imageView);
        } catch (Exception e) {
            Label error = new Label("Image unavailable");
            error.setStyle("-fx-text-fill: #64748b;");
            box.getChildren().add(error);
        }
        return box;
    }

    // =============================================================
    // COLORED CARD
    // =============================================================

    private VBox createCard(String title, String color) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #cbd5e1;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 14;"
        );

        Label label = new Label(title);
        label.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        card.getChildren().add(label);
        return card;
    }

    // =============================================================
    // INSIGHT
    // =============================================================

    private VBox insight(String title, String description) {
        VBox box = new VBox(5);
        box.setPadding(new Insets(12));
        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 10;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #0f172a;");

        Label descriptionLabel = new Label(description);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-text-fill: #64748b;");

        box.getChildren().addAll(titleLabel, descriptionLabel);
        return box;
    }

    // =============================================================
    // QUICK QUESTION BUTTON
    // =============================================================

    private Button quickButton(String text) {
        Button button = new Button(text);
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
