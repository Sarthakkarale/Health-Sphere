package com.healthsphere.view.doctor;

import com.healthsphere.util.Navigation;
import com.healthsphere.util.ResourceImage;
import com.healthsphere.util.SessionManager;
import com.healthsphere.view.authentication.LoginView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * AIHealthAssistantView presents the Clinical Decision Support AI Chat interface.
 * Fully interactive buttons, mouse click handlers, prompt submission, and sidebar navigation.
 */
public class AIHealthAssistantView {

    private final Stage stage;
    private final Scene scene;

    private VBox chatMessagesContainer;
    private ScrollPane messageScrollPane;
    private TextField promptField;

    public AIHealthAssistantView(Stage stage) {
        this.stage = stage;
        this.scene = createScene();
    }

    public Scene getScene() {
        return this.scene;
    }

    private Scene createScene() {
        BorderPane mainRoot = new BorderPane();
        mainRoot.setStyle("-fx-background-color: #F8FAFC;");

        // --- Global Mouse Diagnostics Filter ---
        // Prints target node name in console whenever ANY element is clicked
        mainRoot.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            System.out.println("[Click Diagnostic] Target: " + event.getTarget());
        });

        // --- Sidebar ---
        VBox sidebar = createSidebar();
        mainRoot.setLeft(sidebar);

        // --- Main Content Area ---
        VBox contentArea = new VBox(20);
        contentArea.setPadding(new Insets(24, 32, 32, 32));
        contentArea.setStyle("-fx-background-color: #F8FAFC;");

        // Top Navigation Header
        HBox topHeader = createTopHeader();
        contentArea.getChildren().add(topHeader);

        // Main Two-Column Layout
        HBox bodyLayout = createBodyLayout();
        VBox.setVgrow(bodyLayout, Priority.ALWAYS);
        contentArea.getChildren().add(bodyLayout);

        // ScrollPane for Center Content
        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");

        mainRoot.setCenter(scrollPane);

        Scene aiScene = new Scene(mainRoot, stage.getWidth(), stage.getHeight());
        
        try {
            aiScene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/css/ai_health_assistant.css")).toExternalForm());
        } catch (Exception ignored) {}

        return aiScene;
    }

    /** Creates Sidebar Navigation */
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPadding(new Insets(25, 15, 25, 15));
        sidebar.setStyle("-fx-background-color: #0F172A;");
        sidebar.setMinWidth(260);
        sidebar.setPrefWidth(260);
        sidebar.setMaxWidth(260);

        // Logo Section
        HBox logoSection = new HBox(12);
        logoSection.setPadding(new Insets(0, 0, 25, 5));
        logoSection.setAlignment(Pos.CENTER_LEFT);
        logoSection.setStyle("-fx-cursor: hand;");
        logoSection.setOnMouseClicked(e -> Navigation.goTo(stage, () -> new DoctorDashboardView(stage).getScene()));

        StackPane logoIconBox = new StackPane();
        logoIconBox.setPrefSize(42, 42);
        logoIconBox.setStyle("-fx-background-color: #2563EB; -fx-background-radius: 10px;");
        Label logo = new Label("+");
        logo.setStyle("-fx-text-fill: white; -fx-font-size: 25px; -fx-font-weight: bold;");
        logoIconBox.getChildren().add(logo);

        VBox logoText = new VBox(1);
        Label appName = new Label("Health-Sphere");
        appName.setStyle("-fx-text-fill: white; -fx-font-size: 17px; -fx-font-weight: bold;");
        Label doctorSubtext = new Label("Doctor Dashboard");
        doctorSubtext.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 11px;");
        logoText.getChildren().addAll(appName, doctorSubtext);
        logoSection.getChildren().addAll(logoIconBox, logoText);

        // Navigation Tabs
        VBox navItems = new VBox(6);
        String[] tabs = {
            "Dashboard", "Today's Schedule", "Appointments", "Patient Details",
            "Medical Reports & Prescription", "Availability & Schedule", "Doctor Profile", "AI Health Assistant"
        };
        String[] icons = {
            "ic_dashboard", "ic_schedule", "ic_appointments", "ic_patient",
            "ic_reports", "ic_availability", "ic_profile", "ic_ai"
        };

        for (int i = 0; i < tabs.length; i++) {
            HBox navTab = new HBox(14);
            navTab.setAlignment(Pos.CENTER_LEFT);
            navTab.setPadding(new Insets(10, 14, 10, 14));
            navTab.getStyleClass().add("nav-tab");

            ImageView icon = new ImageView(ResourceImage.load("/images/icons/" + icons[i] + ".png"));
            icon.setFitWidth(18); 
            icon.setFitHeight(18);

            Label tabLabel = new Label(tabs[i]);
            tabLabel.getStyleClass().add("nav-text");
            
            if (i == 7) { 
                navTab.getStyleClass().add("nav-tab-active");
                navTab.setStyle("-fx-background-color: #2563EB; -fx-background-radius: 8px; -fx-cursor: hand;");
                tabLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 13px;");
            } else {
                navTab.setStyle("-fx-background-color: transparent; -fx-background-radius: 8px; -fx-cursor: hand;");
                tabLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 13px;");
            }

            if (icon != null) {
                navTab.getChildren().add(icon);
            }
            navTab.getChildren().add(tabLabel);
            navItems.getChildren().add(navTab);

            final int index = i;
            navTab.setOnMouseClicked(e -> handleSidebarTabClick(index));
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Footer Section
        VBox footer = new VBox(12);
        footer.setAlignment(Pos.BOTTOM_CENTER);
        VBox.setVgrow(footer, Priority.ALWAYS);

        HBox sidebarProfile = new HBox(12);
        sidebarProfile.setAlignment(Pos.CENTER_LEFT);
        sidebarProfile.setPadding(new Insets(10, 14, 10, 14));
        sidebarProfile.getStyleClass().add("sidebar-profile");
        sidebarProfile.setStyle("-fx-cursor: hand;");

        ImageView profileIcon = new ImageView(ResourceImage.load("/images/doctor/doctor_profile.png"));
        profileIcon.setFitWidth(32);
        profileIcon.setFitHeight(32);

        VBox profileTexts = new VBox(2);
        Label profSubText = new Label("Doctor Profile");
        profSubText.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 11px;");
        
        Label profName = new Label(SessionManager.getDoctorDisplayName());
        profName.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 13px;");

        profileTexts.getChildren().addAll(profSubText, profName);
        sidebarProfile.getChildren().addAll(profileIcon, profileTexts);
        sidebarProfile.setOnMouseClicked(e -> Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene()));

        HBox logoutTab = new HBox(14);
        logoutTab.setAlignment(Pos.CENTER_LEFT);
        logoutTab.setPadding(new Insets(10, 14, 10, 14));
        logoutTab.getStyleClass().add("nav-tab");
        logoutTab.setStyle("-fx-cursor: hand; -fx-background-radius: 8px;");

        ImageView logoutIcon = new ImageView(ResourceImage.load("/images/icons/ic_logout.png"));
        logoutIcon.setFitWidth(18);
        logoutIcon.setFitHeight(18);

        Label logoutLabel = new Label("Logout");
        logoutLabel.getStyleClass().add("nav-text");
        logoutLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 13px;");

        logoutTab.getChildren().addAll(logoutIcon, logoutLabel);
        logoutTab.setOnMouseClicked(e -> handleLogout());

        footer.getChildren().addAll(sidebarProfile, logoutTab);

        sidebar.getChildren().addAll(logoSection, navItems, footer);
        return sidebar;
    }

    private void handleLogout() {
        try {
            SessionManager.clearSession();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Navigation.goTo(stage, () -> new LoginView(stage).getScene());
    }

    private void handleSidebarTabClick(int index) {
        switch (index) {
            case 0: Navigation.goTo(stage, () -> new DoctorDashboardView(stage).getScene()); break;
            case 1: Navigation.goTo(stage, () -> new TodaysScheduleView(stage).getScene()); break;
            case 2: Navigation.goTo(stage, () -> new AppointmentsView(stage).getScene()); break;
            case 3: Navigation.goTo(stage, () -> new PatientDetailsView(stage).getScene()); break;
            case 4: Navigation.goTo(stage, () -> new MedicalReportsView(stage).getScene()); break;
            case 5: Navigation.goTo(stage, () -> new AvailabilityScheduleView(stage).getScene()); break;
            case 6: Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene()); break;
            case 7: Navigation.goTo(stage, () -> new AIHealthAssistantView(stage).getScene()); break;
            default: break;
        }
    }

    /** Top Header */
    private HBox createTopHeader() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label pageTitle = new Label("Doctor Module");
        pageTitle.getStyleClass().add("module-header-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox rightIcons = new HBox(18);
        rightIcons.setAlignment(Pos.CENTER_RIGHT);

        Label docProfileLink = new Label("Doctor Profile");
        docProfileLink.getStyleClass().add("header-profile-link");
        docProfileLink.setStyle("-fx-cursor: hand;");
        docProfileLink.setOnMouseClicked(e -> Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene()));

        ImageView bellIcon = new ImageView(ResourceImage.load("/images/doctor/bell.png"));
        bellIcon.setFitWidth(18); 
        bellIcon.setFitHeight(18);
        bellIcon.setStyle("-fx-cursor: hand;");
        bellIcon.setOnMouseClicked(e -> showAlert("Notifications", "You have no new unread notifications."));

        ImageView settingsIcon = new ImageView(ResourceImage.load("/images/doctor/settings.png"));
        settingsIcon.setFitWidth(18); 
        settingsIcon.setFitHeight(18);
        settingsIcon.setStyle("-fx-cursor: hand;");
        settingsIcon.setOnMouseClicked(e -> showAlert("Settings", "AI Clinical Decision Support Configuration Menu."));

        ImageView topAvatar = new ImageView(ResourceImage.load("/images/doctor/portrait-3d-male-doctor.png"));
        topAvatar.setFitWidth(32); 
        topAvatar.setFitHeight(32);
        topAvatar.setStyle("-fx-cursor: hand;");
        Circle clip = new Circle(16, 16, 16);
        topAvatar.setClip(clip);
        topAvatar.setOnMouseClicked(e -> Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene()));

        rightIcons.getChildren().addAll(docProfileLink, bellIcon, settingsIcon, topAvatar);

        topBar.getChildren().addAll(pageTitle, spacer, rightIcons);
        return topBar;
    }

    /** Main Layout */
    private HBox createBodyLayout() {
        HBox layout = new HBox(20);

        VBox chatCard = createChatWindowCard();
        HBox.setHgrow(chatCard, Priority.ALWAYS);

        VBox supportPanel = createClinicalDecisionSupportPanel();
        supportPanel.setMinWidth(320);
        supportPanel.setMaxWidth(340);

        layout.getChildren().addAll(chatCard, supportPanel);
        return layout;
    }

    /** Chat Card Component */
    private VBox createChatWindowCard() {
        VBox card = new VBox(0);
        card.getStyleClass().add("panel-card");
        VBox.setVgrow(card, Priority.ALWAYS);

        BorderPane chatHeader = new BorderPane();
        chatHeader.setPadding(new Insets(16, 20, 16, 20));
        chatHeader.getStyleClass().add("chat-header");

        HBox aiTitleBox = new HBox(10);
        aiTitleBox.setAlignment(Pos.CENTER_LEFT);

        StackPane aiAvatar = new StackPane();
        aiAvatar.getStyleClass().add("ai-avatar-circle");
        ImageView aiIcon = new ImageView(ResourceImage.load("/images/doctor/ai sparkelicon.png"));
        aiIcon.setFitWidth(16); 
        aiIcon.setFitHeight(16);
        aiAvatar.getChildren().add(aiIcon);

        VBox titleText = new VBox(2);
        Label mainTitle = new Label("Health-Sphere AI");
        mainTitle.getStyleClass().add("chat-header-title");
        Label subTitle = new Label("Clinical Decision Support System");
        subTitle.getStyleClass().add("chat-header-subtitle");
        titleText.getChildren().addAll(mainTitle, subTitle);

        aiTitleBox.getChildren().addAll(aiAvatar, titleText);

        HBox headerActionBox = new HBox(8);
        headerActionBox.setAlignment(Pos.CENTER_RIGHT);

        // CLEAR CHAT BUTTON
        Button clearChatBtn = new Button("Clear Chat");
        clearChatBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #64748B; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand; -fx-padding: 6px 12px;");
        clearChatBtn.setOnAction(e -> handleClearChat());

        // HISTORY BUTTON
        Button historyBtn = new Button("History");
        ImageView historyIcon = new ImageView(ResourceImage.load("/images/doctor/ai sparkelicon.png"));
        historyIcon.setFitWidth(14); 
        historyIcon.setFitHeight(14);
        historyBtn.setGraphic(historyIcon);
        historyBtn.getStyleClass().add("btn-history");
        historyBtn.setStyle("-fx-cursor: hand;");
        historyBtn.setOnAction(e -> showAlert("Chat History", "Showing recent clinical AI prompt sessions."));

        headerActionBox.getChildren().addAll(clearChatBtn, historyBtn);

        chatHeader.setLeft(aiTitleBox);
        chatHeader.setRight(headerActionBox);

        chatMessagesContainer = new VBox(18);
        chatMessagesContainer.setPadding(new Insets(20));
        chatMessagesContainer.getStyleClass().add("chat-messages-area");

        loadInitialMessages();

        messageScrollPane = new ScrollPane(chatMessagesContainer);
        messageScrollPane.setFitToWidth(true);
        messageScrollPane.getStyleClass().add("chat-scrollpane");
        VBox.setVgrow(messageScrollPane, Priority.ALWAYS);

        VBox chatInputBox = createChatInputSection();

        card.getChildren().addAll(chatHeader, new Separator(), messageScrollPane, chatInputBox);
        return card;
    }

    private void loadInitialMessages() {
        chatMessagesContainer.getChildren().clear();

        chatMessagesContainer.getChildren().add(createBotMessage(
                "Good morning, Dr. Profile. I am ready to assist with your patient consultations today. You can ask me to analyze symptoms, review drug interactions, or summarize patient histories."
        ));

        chatMessagesContainer.getChildren().add(createUserMessage(
                "Analyze the latest lab results for John Doe (ID: 48291). He presented with fatigue and mild jaundice."
        ));

        VBox analysisContent = new VBox(12);
        Label introText = new Label("Based on John Doe's recent metabolic panel and reported symptoms, here is the analysis:");
        introText.getStyleClass().add("chat-body-text");

        VBox abnCard = new VBox(6);
        abnCard.getStyleClass().add("abnormalities-card");
        abnCard.setPadding(new Insets(12));

        Label abnTitle = new Label("Key Abnormalities:");
        abnTitle.getStyleClass().add("abnormalities-title");

        VBox abnList = new VBox(4);
        abnList.getChildren().addAll(
                createBulletPoint("Elevated Bilirubin (Total: 2.5 mg/dL)"),
                createBulletPoint("Elevated ALT (85 U/L) and AST (72 U/L)"),
                createBulletPoint("Slightly decreased Hemoglobin (12.8 g/dL)")
        );
        abnCard.getChildren().addAll(abnTitle, abnList);

        Label summaryText = new Label("These results suggest potential hepatic involvement. I recommend reviewing his recent medication history.");
        summaryText.getStyleClass().add("chat-body-text");
        summaryText.setWrapText(true);

        analysisContent.getChildren().addAll(introText, abnCard, summaryText);
        chatMessagesContainer.getChildren().add(createBotCustomMessage(analysisContent));
    }

    private void handleClearChat() {
        chatMessagesContainer.getChildren().clear();
        chatMessagesContainer.getChildren().add(createBotMessage(
                "Chat history cleared. How can I assist you with your patient consultations now?"
        ));
    }

    private void handleUserSendMessage(String query) {
        if (query == null || query.trim().isEmpty()) return;

        chatMessagesContainer.getChildren().add(createUserMessage(query.trim()));
        promptField.clear();

        String botReply = "Analyzing query: \"" + query.trim() + "\"...\n\n" +
                "Based on medical databases, the symptoms align with localized inflammation. Recommended next steps: Monitor vitals and consider standard diagnostic screening.";
        
        chatMessagesContainer.getChildren().add(createBotMessage(botReply));
        messageScrollPane.setVvalue(1.0);
    }

    private HBox createBotMessage(String text) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.TOP_LEFT);

        StackPane avatar = createMiniBotAvatar();

        VBox bubble = new VBox();
        bubble.getStyleClass().add("bot-bubble");
        bubble.setPadding(new Insets(12, 16, 12, 16));
        bubble.setMaxWidth(520);

        Label label = new Label(text);
        label.getStyleClass().add("chat-body-text");
        label.setWrapText(true);
        bubble.getChildren().add(label);

        row.getChildren().addAll(avatar, bubble);
        return row;
    }

    private HBox createBotCustomMessage(VBox customContent) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.TOP_LEFT);

        StackPane avatar = createMiniBotAvatar();

        VBox bubble = new VBox();
        bubble.getStyleClass().add("bot-bubble");
        bubble.setPadding(new Insets(14, 16, 14, 16));
        bubble.setMaxWidth(540);

        bubble.getChildren().add(customContent);
        row.getChildren().addAll(avatar, bubble);
        return row;
    }

    private HBox createUserMessage(String text) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.TOP_RIGHT);

        VBox bubble = new VBox();
        bubble.getStyleClass().add("user-bubble");
        bubble.setPadding(new Insets(12, 16, 12, 16));
        bubble.setMaxWidth(480);

        Label label = new Label(text);
        label.getStyleClass().add("user-chat-text");
        label.setWrapText(true);
        bubble.getChildren().add(label);

        ImageView userAvatar = new ImageView(ResourceImage.load("/images/doctor/portrait-3d-male-doctor.png"));
        userAvatar.setFitWidth(28); 
        userAvatar.setFitHeight(28);
        Circle clip = new Circle(14, 14, 14);
        userAvatar.setClip(clip);

        row.getChildren().addAll(bubble, userAvatar);
        return row;
    }

    private StackPane createMiniBotAvatar() {
        StackPane avatar = new StackPane();
        avatar.getStyleClass().add("mini-bot-avatar");
        ImageView icon = new ImageView(ResourceImage.load("/images/doctor/ai sparkelicon.png"));
        icon.setFitWidth(14); 
        icon.setFitHeight(14);
        avatar.getChildren().add(icon);
        return avatar;
    }

    private HBox createBulletPoint(String text) {
        HBox bullet = new HBox(8);
        bullet.setAlignment(Pos.CENTER_LEFT);
        Circle dot = new Circle(2.5, Color.web("#334155"));
        Label label = new Label(text);
        label.getStyleClass().add("bullet-text");
        bullet.getChildren().addAll(dot, label);
        return bullet;
    }

    /** Prompt Bar */
    private VBox createChatInputSection() {
        VBox container = new VBox(12);
        container.setPadding(new Insets(16, 20, 20, 20));
        container.getStyleClass().add("input-section-container");

        HBox chipsBox = new HBox(10);
        chipsBox.getChildren().addAll(
                createChipButton("Analyze symptoms"),
                createChipButton("Check drug interactions"),
                createChipButton("Summarize patient history")
        );

        HBox inputBox = new HBox(12);
        inputBox.getStyleClass().add("chat-input-box");
        inputBox.setPadding(new Insets(8, 14, 8, 14));
        inputBox.setAlignment(Pos.CENTER_LEFT);

        ImageView attachIcon = new ImageView(ResourceImage.load("/images/doctor/attach_icon.png"));
        attachIcon.setFitWidth(18); 
        attachIcon.setFitHeight(18);
        attachIcon.setStyle("-fx-cursor: hand;");
        attachIcon.setOnMouseClicked(e -> showAlert("Attachment", "Attach patient lab results or medical files."));

        promptField = new TextField();
        promptField.setPromptText("Type your clinical query here...");
        promptField.getStyleClass().add("prompt-text-field");
        
        // Key listener for ENTER key
        promptField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleUserSendMessage(promptField.getText());
            }
        });
        
        // Action listener for Enter inside TextField
        promptField.setOnAction(e -> handleUserSendMessage(promptField.getText()));
        HBox.setHgrow(promptField, Priority.ALWAYS);

        ImageView sendIcon = new ImageView(ResourceImage.load("/images/doctor/send icon.png"));
        sendIcon.setFitWidth(18); 
        sendIcon.setFitHeight(18);
        sendIcon.setStyle("-fx-cursor: hand;");
        sendIcon.setOnMouseClicked(e -> handleUserSendMessage(promptField.getText()));

        inputBox.getChildren().addAll(attachIcon, promptField, sendIcon);

        container.getChildren().addAll(chipsBox, inputBox);
        return container;
    }

    private Button createChipButton(String text) {
        Button chip = new Button(text);
        chip.getStyleClass().add("suggestion-chip");
        chip.setStyle("-fx-cursor: hand;");
        chip.setOnAction(e -> {
            promptField.setText(text);
            handleUserSendMessage(text);
        });
        return chip;
    }

    /** Right Panel */
    private VBox createClinicalDecisionSupportPanel() {
        VBox panel = new VBox(16);
        panel.getStyleClass().add("cds-panel");
        panel.setPadding(new Insets(16));

        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        ImageView shieldIcon = new ImageView(ResourceImage.load("/images/doctor/shield_icon.jpg"));
        shieldIcon.setFitWidth(18); 
        shieldIcon.setFitHeight(18);
        Label title = new Label("Clinical Decision Support");
        title.getStyleClass().add("cds-header-title");
        header.getChildren().addAll(shieldIcon, title);

        VBox diffCard = new VBox(12);
        diffCard.getStyleClass().add("cds-card");
        diffCard.setPadding(new Insets(14));

        Label diffTitle = new Label("Differential Diagnosis");
        diffTitle.getStyleClass().add("cds-card-title");

        VBox diffList = new VBox(8);
        diffList.getChildren().add(createDiagnosisItem("Drug-induced\nHepatotoxicity", "High\nProb.", "high-prob-badge"));
        diffList.getChildren().add(createDiagnosisItem("Viral Hepatitis", "Mod Prob.", "mod-prob-badge"));

        diffCard.getChildren().addAll(diffTitle, diffList);

        VBox recCard = new VBox(10);
        recCard.getStyleClass().add("cds-card");
        recCard.setPadding(new Insets(14));

        Label recTitle = new Label("Recommendations");
        recTitle.getStyleClass().add("cds-card-title");

        VBox recCallout = new VBox(8);
        recCallout.getStyleClass().add("recommendation-callout");
        recCallout.setPadding(new Insets(12));

        Label recText = new Label("Consider immediate cessation of recent NSAID regimen. Order comprehensive viral hepatitis panel.");
        recText.getStyleClass().add("recommendation-text");
        recText.setWrapText(true);

        Hyperlink reviewLink = new Hyperlink("Review Guidelines ↗");
        reviewLink.getStyleClass().add("recommendation-link");
        reviewLink.setOnAction(e -> showAlert("Clinical Guidelines", "Opening Hepatic & Drug-Induced Toxicity Clinical Guidelines manual..."));

        recCallout.getChildren().addAll(recText, reviewLink);
        recCard.getChildren().addAll(recTitle, recCallout);

        VBox lookupCard = new VBox(10);
        lookupCard.getStyleClass().add("cds-card");
        lookupCard.setPadding(new Insets(14));

        Label lookupTitle = new Label("Quick Lookup");
        lookupTitle.getStyleClass().add("cds-card-title");

        HBox lookupSearchBox = new HBox(8);
        lookupSearchBox.getStyleClass().add("lookup-search-box");
        lookupSearchBox.setAlignment(Pos.CENTER_LEFT);
        lookupSearchBox.setPadding(new Insets(6, 10, 6, 10));

        ImageView searchIcon = new ImageView(ResourceImage.load("/images/doctor/search icon doctordash.png"));
        searchIcon.setFitWidth(14); 
        searchIcon.setFitHeight(14);

        TextField lookupInput = new TextField();
        lookupInput.setPromptText("Search drugs, interactions...");
        lookupInput.getStyleClass().add("lookup-input-field");
        lookupInput.setOnAction(e -> {
            if (!lookupInput.getText().trim().isEmpty()) {
                showAlert("Quick Lookup", "Searching medical database for: " + lookupInput.getText().trim());
                lookupInput.clear();
            }
        });
        HBox.setHgrow(lookupInput, Priority.ALWAYS);

        lookupSearchBox.getChildren().addAll(searchIcon, lookupInput);
        lookupCard.getChildren().addAll(lookupTitle, lookupSearchBox);

        panel.getChildren().addAll(header, diffCard, recCard, lookupCard);
        return panel;
    }

    private BorderPane createDiagnosisItem(String condition, String probText, String badgeStyle) {
        BorderPane row = new BorderPane();
        row.getStyleClass().add("diagnosis-row");
        row.setPadding(new Insets(8, 10, 8, 10));
        row.setStyle("-fx-cursor: hand;");
        row.setOnMouseClicked(e -> showAlert("Diagnosis Details", "Condition: " + condition.replace("\n", " ") + "\nProbability: " + probText.replace("\n", " ")));

        Label condLabel = new Label(condition);
        condLabel.getStyleClass().add("diagnosis-name");

        Label badge = new Label(probText);
        badge.getStyleClass().add(badgeStyle);
        badge.setAlignment(Pos.CENTER);

        row.setLeft(condLabel);
        row.setRight(badge);
        return row;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}