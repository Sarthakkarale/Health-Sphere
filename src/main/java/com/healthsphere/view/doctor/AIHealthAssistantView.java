package com.healthsphere.view.doctor;

import com.healthsphere.util.Navigation;
import com.healthsphere.util.ResourceImage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * AIHealthAssistantView presents the Clinical Decision Support AI Chat interface.
 * Updated layout hierarchy to enable smooth vertical scrolling down to bottom elements,
 * added sidebar footer controls (Doctor Profile & Logout), and matched sidebar tab styles.
 */
public class AIHealthAssistantView {

    private final Stage stage;
    private final Scene scene;

    public AIHealthAssistantView(Stage stage) {
        this.stage = stage;
        this.scene = createScene();
    }

    public Scene getScene() {
        return this.scene;
    }

    private Scene createScene() {
        BorderPane mainRoot = new BorderPane();
        mainRoot.getStyleClass().add("root-pane");

        // --- Sidebar (Left Navigation) ---
        VBox sidebar = createSidebar();
        mainRoot.setLeft(sidebar);

        // --- Main Content Area ---
        VBox contentArea = new VBox(20);
        contentArea.setPadding(new Insets(20, 30, 30, 30));
        contentArea.getStyleClass().add("content-area");

        // Top Navigation Header
        HBox topHeader = createTopHeader();
        contentArea.getChildren().add(topHeader);

        // Main Two-Column Layout (Chat Window on Left | Clinical Decision Support Panel on Right)
        HBox bodyLayout = createBodyLayout();
        VBox.setVgrow(bodyLayout, Priority.ALWAYS);
        contentArea.getChildren().add(bodyLayout);

        mainRoot.setCenter(contentArea);

        // ScrollPane Container wrapping root layout to scroll down to bottom fully
        ScrollPane scrollPane = new ScrollPane(mainRoot);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("content-scrollpane");

        Scene aiScene = new Scene(scrollPane, stage.getWidth(), stage.getHeight());
        
        try {
            aiScene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/css/ai_health_assistant.css")).toExternalForm());
        } catch (Exception ignored) {}

        return aiScene;
    }

    /** Creates Sidebar Navigation with Doctor Profile & Logout buttons at the bottom */
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPadding(new Insets(25, 15, 25, 15));
        sidebar.getStyleClass().add("sidebar");
        sidebar.setMinWidth(240);
        sidebar.setPrefWidth(240);

        // Logo Section
        HBox logoSection = new HBox(10);
        logoSection.setPadding(new Insets(0, 0, 25, 0));
        logoSection.setAlignment(Pos.CENTER_LEFT);

        StackPane logoIconBox = new StackPane();
        logoIconBox.getStyleClass().add("logo-icon-box");
        Label logoBadgeText = new Label("HS");
        logoBadgeText.getStyleClass().add("logo-badge-text");
        logoIconBox.getChildren().add(logoBadgeText);

        VBox logoText = new VBox(0);
        Label appName = new Label("Health-Sphere");
        appName.getStyleClass().add("logo-name");
        Label doctorSubtext = new Label("AI Healthcare Admin");
        doctorSubtext.getStyleClass().add("logo-subtext");
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
            HBox navTab = new HBox(12);
            navTab.getStyleClass().add("nav-tab");
            navTab.setAlignment(Pos.CENTER_LEFT);
            navTab.setPadding(new Insets(8, 12, 8, 12));

            if (i == 7) { // Active Highlight: AI Health Assistant
                navTab.getStyleClass().add("nav-tab-active");
            }

            ImageView icon = new ImageView(ResourceImage.load("/images/icons/" + icons[i] + ".png"));
            icon.setFitWidth(18); 
            icon.setFitHeight(18);
            Label tabLabel = new Label(tabs[i]);
            tabLabel.getStyleClass().add("nav-text");

            navTab.getChildren().addAll(icon, tabLabel);
            navItems.getChildren().add(navTab);

            final int index = i;
            navTab.setOnMouseClicked(e -> handleSidebarTabClick(index));
        }

        // Sidebar Footer Controls (Doctor Profile & Logout Button)
        VBox footer = new VBox(8);
        footer.setAlignment(Pos.BOTTOM_LEFT);
        footer.setPadding(new Insets(16, 0, 0, 0));
        VBox.setVgrow(footer, Priority.ALWAYS);

        Separator lineDivider = new Separator();
        lineDivider.getStyleClass().add("sidebar-divider");

        HBox docProfile = new HBox(12);
        docProfile.setAlignment(Pos.CENTER_LEFT);
        docProfile.getStyleClass().add("sidebar-profile");
        docProfile.setPadding(new Insets(6, 12, 6, 12));

        ImageView profileIcon = new ImageView(ResourceImage.load("/images/icons/ic_doctor_profile_small.png"));
        profileIcon.setFitWidth(18);
        profileIcon.setFitHeight(18);

        Label docLabel = new Label("Doctor Profile");
        docLabel.getStyleClass().add("sidebar-profile-name");
        docProfile.getChildren().addAll(profileIcon, docLabel);
        docProfile.setOnMouseClicked(e ->
                Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene()));

        HBox logout = new HBox(12);
        logout.setAlignment(Pos.CENTER_LEFT);
        logout.getStyleClass().add("nav-tab-logout");
        logout.setPadding(new Insets(6, 12, 6, 12));

        ImageView logoutIcon = new ImageView(ResourceImage.load("/images/icons/ic_logout.png"));
        logoutIcon.setFitWidth(18);
        logoutIcon.setFitHeight(18);

        Label logoutLabel = new Label("Logout");
        logoutLabel.getStyleClass().add("nav-text-logout");
        logout.getChildren().addAll(logoutIcon, logoutLabel);
        logout.setOnMouseClicked(e -> System.out.println("Logging out..."));

        footer.getChildren().addAll(lineDivider, docProfile, logout);
        sidebar.getChildren().addAll(logoSection, navItems, footer);
        return sidebar;
    }

    private void handleSidebarTabClick(int index) {
        switch (index) {
            case 0:
                Navigation.goTo(stage, () -> new DoctorDashboardView(stage).getScene());
                break;
            case 1:
                Navigation.goTo(stage, () -> new TodaysScheduleView(stage).getScene());
                break;
            case 2:
                Navigation.goTo(stage, () -> new AppointmentsView(stage).getScene());
                break;
            case 3:
                Navigation.goTo(stage, () -> new PatientDetailsView(stage).getScene());
                break;
            case 4:
                Navigation.goTo(stage, () -> new MedicalReportsView(stage).getScene());
                break;
            case 5:
                Navigation.goTo(stage, () -> new AvailabilityScheduleView(stage).getScene());
                break;
            case 6:
                Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene());
                break;
            case 7:
                Navigation.goTo(stage, () -> new AIHealthAssistantView(stage).getScene());
                break;
            default:
                break;
        }
    }

    /** Top Navigation Header */
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
        docProfileLink.setOnMouseClicked(e -> 
                Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene()));

        ImageView bellIcon = new ImageView(ResourceImage.load("/images/doctor/bell.png"));
        bellIcon.setFitWidth(18); 
        bellIcon.setFitHeight(18);
        bellIcon.getStyleClass().add("clickable-icon");

        ImageView settingsIcon = new ImageView(ResourceImage.load("/images/doctor/settings.png"));
        settingsIcon.setFitWidth(18); 
        settingsIcon.setFitHeight(18);
        settingsIcon.getStyleClass().add("clickable-icon");

        ImageView topAvatar = new ImageView(ResourceImage.load("/images/doctor/portrait-3d-male-doctor.png"));
        topAvatar.setFitWidth(32); 
        topAvatar.setFitHeight(32);
        Circle clip = new Circle(16, 16, 16);
        topAvatar.setClip(clip);

        rightIcons.getChildren().addAll(docProfileLink, bellIcon, settingsIcon, topAvatar);

        topBar.getChildren().addAll(pageTitle, spacer, rightIcons);
        return topBar;
    }

    /** Main Two-Column Layout */
    private HBox createBodyLayout() {
        HBox layout = new HBox(20);

        // Left Column: Main Interactive AI Chat Window
        VBox chatCard = createChatWindowCard();
        HBox.setHgrow(chatCard, Priority.ALWAYS);

        // Right Column: Clinical Decision Support Panel
        VBox supportPanel = createClinicalDecisionSupportPanel();
        supportPanel.setMinWidth(320);
        supportPanel.setMaxWidth(340);

        layout.getChildren().addAll(chatCard, supportPanel);
        return layout;
    }

    /** Main Interactive Chat Card Component */
    private VBox createChatWindowCard() {
        VBox card = new VBox(0);
        card.getStyleClass().add("panel-card");
        VBox.setVgrow(card, Priority.ALWAYS);

        // Chat Header
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

        Button historyBtn = new Button("History");
        ImageView historyIcon = new ImageView(ResourceImage.load("/images/doctor/ai sparkelicon.png"));
        historyIcon.setFitWidth(14); 
        historyIcon.setFitHeight(14);
        historyBtn.setGraphic(historyIcon);
        historyBtn.getStyleClass().add("btn-history");

        chatHeader.setLeft(aiTitleBox);
        chatHeader.setRight(historyBtn);

        // Chat Scroll Area
        VBox chatMessagesContainer = new VBox(18);
        chatMessagesContainer.setPadding(new Insets(20));
        chatMessagesContainer.getStyleClass().add("chat-messages-area");

        // Message 1: Bot Greeting
        chatMessagesContainer.getChildren().add(createBotMessage(
                "Good morning, Dr. Profile. I am ready to assist with your patient consultations today. You can ask me to analyze symptoms, review drug interactions, or summarize patient histories."
        ));

        // Message 2: User Prompt
        chatMessagesContainer.getChildren().add(createUserMessage(
                "Analyze the latest lab results for John Doe (ID: 48291). He presented with fatigue and mild jaundice."
        ));

        // Message 3: Bot Detailed Analysis
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

        Label summaryText = new Label("These results, combined with fatigue and mild jaundice, suggest potential hepatic involvement or hemolytic anemia. I recommend reviewing his recent medication history, particularly the introduction of new NSAIDs last month.");
        summaryText.getStyleClass().add("chat-body-text");
        summaryText.setWrapText(true);

        analysisContent.getChildren().addAll(introText, abnCard, summaryText);
        chatMessagesContainer.getChildren().add(createBotCustomMessage(analysisContent));

        // Message 4: Bot Typing Indicator
        chatMessagesContainer.getChildren().add(createBotTypingIndicator());

        ScrollPane messageScrollPane = new ScrollPane(chatMessagesContainer);
        messageScrollPane.setFitToWidth(true);
        messageScrollPane.getStyleClass().add("chat-scrollpane");
        VBox.setVgrow(messageScrollPane, Priority.ALWAYS);

        // Chat Input Section
        VBox chatInputBox = createChatInputSection();

        card.getChildren().addAll(chatHeader, new Separator(), messageScrollPane, chatInputBox);
        return card;
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

    private HBox createBotTypingIndicator() {
        HBox row = new HBox(12);
        row.setAlignment(Pos.TOP_LEFT);

        StackPane avatar = createMiniBotAvatar();

        HBox bubble = new HBox(6);
        bubble.getStyleClass().add("bot-bubble");
        bubble.setPadding(new Insets(12, 16, 12, 16));
        bubble.setAlignment(Pos.CENTER);

        Circle dot1 = new Circle(3, Color.web("#2563EB"));
        Circle dot2 = new Circle(3, Color.web("#2563EB"));
        Circle dot3 = new Circle(3, Color.web("#2563EB"));
        bubble.getChildren().addAll(dot1, dot2, dot3);

        row.getChildren().addAll(avatar, bubble);
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

    /** Bottom Prompt Controls & Action Chips */
    private VBox createChatInputSection() {
        VBox container = new VBox(12);
        container.setPadding(new Insets(16, 20, 20, 20));
        container.getStyleClass().add("input-section-container");

        // Action Chip Suggestions
        HBox chipsBox = new HBox(10);
        chipsBox.getChildren().addAll(
                createChipButton("Analyze symptoms"),
                createChipButton("Check drug interactions"),
                createChipButton("Summarize patient history")
        );

        // Text Input Bar
        HBox inputBox = new HBox(12);
        inputBox.getStyleClass().add("chat-input-box");
        inputBox.setPadding(new Insets(8, 14, 8, 14));
        inputBox.setAlignment(Pos.CENTER_LEFT);

        ImageView attachIcon = new ImageView(ResourceImage.load("/images/doctor/attach_icon.png"));
        attachIcon.setFitWidth(18); 
        attachIcon.setFitHeight(18);
        attachIcon.getStyleClass().add("clickable-icon");

        TextField promptField = new TextField();
        promptField.setPromptText("Type your clinical query here...");
        promptField.getStyleClass().add("prompt-text-field");
        HBox.setHgrow(promptField, Priority.ALWAYS);

        ImageView sendIcon = new ImageView(ResourceImage.load("/images/doctor/send icon.png"));
        sendIcon.setFitWidth(18); 
        sendIcon.setFitHeight(18);
        sendIcon.getStyleClass().add("clickable-icon");

        inputBox.getChildren().addAll(attachIcon, promptField, sendIcon);

        container.getChildren().addAll(chipsBox, inputBox);
        return container;
    }

    private Button createChipButton(String text) {
        Button chip = new Button(text);
        chip.getStyleClass().add("suggestion-chip");
        chip.setOnAction(e -> System.out.println("Selected chip: " + text));
        return chip;
    }

    /** Clinical Decision Support Panel (Right Column) */
    private VBox createClinicalDecisionSupportPanel() {
        VBox panel = new VBox(16);
        panel.getStyleClass().add("cds-panel");
        panel.setPadding(new Insets(16));

        // Header
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        ImageView shieldIcon = new ImageView(ResourceImage.load("/images/doctor/shield_icon.jpg"));
        shieldIcon.setFitWidth(18); 
        shieldIcon.setFitHeight(18);
        Label title = new Label("Clinical Decision Support");
        title.getStyleClass().add("cds-header-title");
        header.getChildren().addAll(shieldIcon, title);

        // Section 1: Differential Diagnosis
        VBox diffCard = new VBox(12);
        diffCard.getStyleClass().add("cds-card");
        diffCard.setPadding(new Insets(14));

        HBox diffHeader = new HBox(6);
        diffHeader.setAlignment(Pos.CENTER_LEFT);
        Label diffTitle = new Label("Differential Diagnosis");
        diffTitle.getStyleClass().add("cds-card-title");
        diffHeader.getChildren().add(diffTitle);

        VBox diffList = new VBox(8);
        diffList.getChildren().add(createDiagnosisItem("Drug-induced\nHepatotoxicity", "High\nProb.", "high-prob-badge"));
        diffList.getChildren().add(createDiagnosisItem("Viral Hepatitis", "Mod Prob.", "mod-prob-badge"));

        diffCard.getChildren().addAll(diffHeader, diffList);

        // Section 2: Recommendations
        VBox recCard = new VBox(10);
        recCard.getStyleClass().add("cds-card");
        recCard.setPadding(new Insets(14));

        HBox recHeader = new HBox(6);
        recHeader.setAlignment(Pos.CENTER_LEFT);
        Label recTitle = new Label("Recommendations");
        recTitle.getStyleClass().add("cds-card-title");
        recHeader.getChildren().add(recTitle);

        VBox recCallout = new VBox(8);
        recCallout.getStyleClass().add("recommendation-callout");
        recCallout.setPadding(new Insets(12));

        Label recText = new Label("Consider immediate cessation of recent NSAID regimen. Order comprehensive viral hepatitis panel.");
        recText.getStyleClass().add("recommendation-text");
        recText.setWrapText(true);

        Hyperlink reviewLink = new Hyperlink("Review Guidelines ↗");
        reviewLink.getStyleClass().add("recommendation-link");

        recCallout.getChildren().addAll(recText, reviewLink);
        recCard.getChildren().addAll(recHeader, recCallout);

        // Section 3: Quick Lookup
        VBox lookupCard = new VBox(10);
        lookupCard.getStyleClass().add("cds-card");
        lookupCard.setPadding(new Insets(14));

        HBox lookupHeader = new HBox(6);
        lookupHeader.setAlignment(Pos.CENTER_LEFT);
        ImageView searchBlueIcon = new ImageView(ResourceImage.load("/images/doctor/search icon doctordash.png"));
        searchBlueIcon.setFitWidth(16); 
        searchBlueIcon.setFitHeight(16);
        Label lookupTitle = new Label("Quick Lookup");
        lookupTitle.getStyleClass().add("cds-card-title");
        lookupHeader.getChildren().addAll(searchBlueIcon, lookupTitle);

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
        HBox.setHgrow(lookupInput, Priority.ALWAYS);

        lookupSearchBox.getChildren().addAll(searchIcon, lookupInput);
        lookupCard.getChildren().addAll(lookupHeader, lookupSearchBox);

        panel.getChildren().addAll(header, diffCard, recCard, lookupCard);
        return panel;
    }

    private BorderPane createDiagnosisItem(String condition, String probText, String badgeStyle) {
        BorderPane row = new BorderPane();
        row.getStyleClass().add("diagnosis-row");
        row.setPadding(new Insets(8, 10, 8, 10));

        Label condLabel = new Label(condition);
        condLabel.getStyleClass().add("diagnosis-name");

        Label badge = new Label(probText);
        badge.getStyleClass().add(badgeStyle);
        badge.setAlignment(Pos.CENTER);

        row.setLeft(condLabel);
        row.setRight(badge);
        return row;
    }
}