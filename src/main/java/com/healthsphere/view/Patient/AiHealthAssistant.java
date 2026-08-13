package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AiHealthAssistant {

    private final Stage stage;

    public AiHealthAssistant(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color: #f1f5f9;"
        );

        root.setLeft(createSidebar());
        root.setTop(createHeader());

        VBox content =
                new VBox(22);

        content.setPadding(
                new Insets(28)
        );

        Label title =
                new Label("AI Health Assistant");

        title.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label subtitle =
                new Label(
                        "Get personalized health guidance and answers to your questions."
                );

        subtitle.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 15px;"
        );

        VBox heading =
                new VBox(
                        5,
                        title,
                        subtitle
                );

        /*
         * AI welcome card
         */

        VBox welcome =
                coloredCard(
                        "#ede9fe",
                        "#7c3aed"
                );

        Label welcomeTitle =
                new Label(
                        "✦  Hello Sarah!"
                );

        welcomeTitle.setStyle(
                "-fx-font-size: 21px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #5b21b6;"
        );

        Label welcomeText =
                new Label(
                        "I'm your AI Health Assistant. " +
                        "I can help you understand your health information, " +
                        "prepare questions for your doctor and provide general health guidance."
                );

        welcomeText.setWrapText(true);

        welcomeText.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #475569;"
        );

        welcome.getChildren().addAll(
                welcomeTitle,
                welcomeText
        );

        /*
         * Chat box
         */

        VBox chatCard =
                coloredCard(
                        "#ffffff",
                        "#8b5cf6"
                );

        Label chatTitle =
                new Label(
                        "Ask your health question"
                );

        chatTitle.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;"
        );

        TextArea question =
                new TextArea();

        question.setPromptText(
                "Type your health question here..."
        );

        question.setWrapText(true);

        question.setPrefRowCount(4);

        question.setStyle(
                "-fx-background-color: #f8fafc;" +
                "-fx-border-color: #cbd5e1;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        Button ask =
                new Button("Ask AI Assistant");

        ask.setPrefHeight(42);

        ask.setStyle(
                "-fx-background-color: #7c3aed;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 0 20;"
        );

        VBox response =
                new VBox(5);

        response.setPadding(
                new Insets(14)
        );

        response.setStyle(
                "-fx-background-color: #f5f3ff;" +
                "-fx-border-color: #ddd6fe;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;"
        );

        Label responseTitle =
                new Label("AI Response");

        responseTitle.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #5b21b6;"
        );

        Label responseText =
                new Label(
                        "Your AI response will appear here. " +
                        "For medical emergencies or serious symptoms, " +
                        "please contact a qualified healthcare professional."
                );

        responseText.setWrapText(true);

        responseText.setStyle(
                "-fx-text-fill: #475569;"
        );

        response.getChildren().addAll(
                responseTitle,
                responseText
        );

        ask.setOnAction(
                e -> {

                    if (question.getText().trim().isEmpty()) {

                        responseText.setText(
                                "Please enter a health question first."
                        );

                    } else {

                        responseText.setText(
                                "Thank you for your question. " +
                                "This assistant can provide general health information, " +
                                "but it cannot replace a doctor or medical diagnosis."
                        );
                    }
                }
        );

        chatCard.getChildren().addAll(
                chatTitle,
                question,
                ask,
                response
        );

        /*
         * Suggested questions
         */

        VBox suggestions =
                coloredCard(
                        "#dbeafe",
                        "#2563eb"
                );

        Label suggestionTitle =
                new Label(
                        "Suggested Questions"
                );

        suggestionTitle.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1e3a8a;"
        );

        HBox suggestionButtons =
                new HBox(10);

        suggestionButtons.getChildren().addAll(

                suggestionButton(
                        "What does my blood pressure mean?",
                        question
                ),

                suggestionButton(
                        "How can I improve my sleep?",
                        question
                ),

                suggestionButton(
                        "What should I ask my doctor?",
                        question
                )
        );

        suggestions.getChildren().addAll(
                suggestionTitle,
                suggestionButtons
        );

        /*
         * Health insights
         */

        VBox insights =
                coloredCard(
                        "#dcfce7",
                        "#16a34a"
                );

        Label insightsTitle =
                new Label(
                        "Your Recent Health Insights"
                );

        insightsTitle.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #166534;"
        );

        insights.getChildren().addAll(

                insight(
                        "Blood Pressure",
                        "Recent reading: 118 / 76 mmHg"
                ),

                new Separator(),

                insight(
                        "Heart Rate",
                        "Recent reading: 72 BPM"
                ),

                new Separator(),

                insight(
                        "Upcoming Appointment",
                        "Cardiology consultation tomorrow at 10:00 AM"
                )
        );

        Button back =
                new Button("Back to Dashboard");

        back.setPrefHeight(42);

        back.setStyle(
                "-fx-background-color: #e2e8f0;" +
                "-fx-text-fill: #0f172a;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 0 20;"
        );

        back.setOnAction(
                e -> stage.setScene(
                        new Dashboard(stage).getScene()
                )
        );

        content.getChildren().addAll(
                heading,
                welcome,
                chatCard,
                suggestions,
                insights,
                back
        );

        ScrollPane scroll =
                new ScrollPane(content);

        scroll.setFitToWidth(true);

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scroll.setStyle(
                "-fx-background-color: transparent;"
        );

        root.setCenter(scroll);

        return new Scene(
                root,
                1440,
                900
        );
    }

    private Button suggestionButton(
            String text,
            TextArea question
    ) {

        Button button =
                new Button(text);

        button.setWrapText(true);

        button.setPrefHeight(42);

        button.setStyle(
                "-fx-background-color: white;" +
                "-fx-text-fill: #1e3a8a;" +
                "-fx-border-color: #93c5fd;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        button.setOnAction(
                e -> question.setText(text)
        );

        return button;
    }

    private VBox insight(
            String title,
            String description
    ) {

        VBox box =
                new VBox(4);

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-weight: bold;"
        );

        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setStyle(
                "-fx-text-fill: #475569;"
        );

        box.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );

        return box;
    }

    private VBox coloredCard(
            String background,
            String border
    ) {

        VBox box =
                new VBox(14);

        box.setPadding(
                new Insets(20)
        );

        box.setStyle(
                "-fx-background-color: " +
                        background + ";" +
                "-fx-border-color: " +
                        border + ";" +
                "-fx-border-width: 1.5;" +
                "-fx-background-radius: 14;" +
                "-fx-border-radius: 14;"
        );

        return box;
    }

    private HBox createHeader() {

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_RIGHT
        );

        header.setPadding(
                new Insets(
                        16,
                        28,
                        16,
                        28
                )
        );

        header.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #e2e8f0;"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button notification =
                new Button("Notifications");

        notification.setOnAction(
                e -> stage.setScene(
                        new Notifications(stage).getScene()
                )
        );

        Button profile =
                new Button("Sarah");

        profile.setOnAction(
                e -> stage.setScene(
                        new ProfileSettings(stage).getScene()
                )
        );

        header.getChildren().addAll(
                spacer,
                notification,
                profile
        );

        return header;
    }

    private VBox createSidebar() {

        VBox sidebar =
                new VBox(8);

        sidebar.setPrefWidth(255);

        sidebar.setPadding(
                new Insets(22)
        );

        sidebar.setStyle(
                "-fx-background-color: #0f172a;"
        );

        Label brand =
                new Label("✚  MediNexus AI");

        brand.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 21px;" +
                "-fx-font-weight: bold;"
        );

        Label module =
                new Label("Patient Module");

        module.setStyle(
                "-fx-text-fill: #94a3b8;"
        );

        Region spacer =
                new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        sidebar.getChildren().addAll(

                brand,
                module,
                new Separator(),

                nav(
                        "▦",
                        "Dashboard",
                        false,
                        () -> showDashboard()
                ),

                nav(
                        "⊞",
                        "Search Hospitals",
                        false,
                        () -> showSearchHospitals()
                ),

                nav(
                        "▣",
                        "Appointments",
                        false,
                        () -> showAppointments()
                ),

                nav(
                        "▧",
                        "Health Passport",
                        false,
                        () -> showHealthPassport()
                ),

                nav(
                        "▱",
                        "Medical Records",
                        false,
                        () -> showMedicalRecords()
                ),

                nav(
                        "♙",
                        "AI Health Assistant",
                        true,
                        () -> showAIHealthAssistant()
                ),

                nav(
                        "⌖",
                        "Emergency Assistance",
                        false,
                        () -> showEmergencyAssistance()
                ),

                spacer,

                nav(
                        "♧",
                        "Notifications",
                        false,
                        () -> showNotifications()
                ),

                nav(
                        "⚙",
                        "Profile & Settings",
                        false,
                        () -> showProfileSettings()
                )
        );

        return sidebar;
    }

    private HBox nav(
            String icon,
            String text,
            boolean selected,
            Runnable action
    ) {

        HBox item =
                new HBox(12);

        item.setAlignment(
                Pos.CENTER_LEFT
        );

        item.setPadding(
                new Insets(12)
        );

        item.setStyle(
                "-fx-background-color: " +
                        (selected
                                ? "#2563eb;"
                                : "transparent;") +
                "-fx-background-radius: 8;"
        );

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 17px;"
        );

        Label textLabel =
                new Label(text);

        textLabel.setStyle(
                "-fx-text-fill: white;"
        );

        item.getChildren().addAll(
                iconLabel,
                textLabel
        );

        item.setOnMouseClicked(
                e -> action.run()
        );

        return item;
    }

    private void showDashboard() {
        stage.setScene(
                new Dashboard(stage).getScene()
        );
    }

    private void showSearchHospitals() {
        stage.setScene(
                new SearchHospitals(stage).getScene()
        );
    }

    private void showAppointments() {
        stage.setScene(
                new Appointments(stage).getScene()
        );
    }

    private void showHealthPassport() {
        stage.setScene(
                new HealthPassport(stage).getScene()
        );
    }

    private void showMedicalRecords() {
        stage.setScene(
                new MedicalRecords(stage).getScene()
        );
    }

    private void showAIHealthAssistant() {
        stage.setScene(
                new AiHealthAssistant(stage).getScene()
        );
    }

    private void showEmergencyAssistance() {
        stage.setScene(
                new EmergencyAssistance(stage).getScene()
        );
    }

    private void showNotifications() {
        stage.setScene(
                new Notifications(stage).getScene()
        );
    }

    private void showProfileSettings() {
        stage.setScene(
                new ProfileSettings(stage).getScene()
        );
    }
}