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
 * AIHealthAssistantView represents the Clinical AI Assistant dashboard.
 * Provides real-time clinical decision support, diagnostic suggestions, and patient summary analytics.
 */
public class AIHealthAssistantView {

    private final Stage stage;
    private final Scene scene;

    // Chat components references for interactive updates
    private VBox messageStream;
    private TextField promptInput;

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

        // --- Sidebar (Left Navigation - Fixed & Constant Width & Styling) ---
        VBox sidebar = createSidebar();
        mainRoot.setLeft(sidebar);

        // --- Main Content Area ---
        VBox contentArea = new VBox(20);
        contentArea.setPadding(new Insets(24, 32, 32, 32));
        contentArea.getStyleClass().add("content-area");

        // Top Header
        HBox topHeader = createTopHeader();
        contentArea.getChildren().add(topHeader);

        // Page Sub-header Title & Top Action Buttons
        BorderPane pageHeader = createPageHeader();
        contentArea.getChildren().add(pageHeader);

        // Main Content Two-Column Grid (Interactive AI Chat Console on Left | Clinical Insights & Prompts on Right)
        HBox bodyLayout = createBodyLayout();
        contentArea.getChildren().add(bodyLayout);

        // --- ScrollPane Container for Content Only (Sidebar remains fixed on Left) ---
        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("content-scrollpane");
        mainRoot.setCenter(scrollPane);

        Scene aiAssistantScene = new Scene(mainRoot, stage.getWidth(), stage.getHeight());

        try {
            aiAssistantScene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/css/ai_health_assistant.css")).toExternalForm());
        } catch (Exception ignored) {}

        return aiAssistantScene;
    }

    /** Creates Sidebar Navigation strictly matching Dashboard dark theme & fixed width */
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPadding(new Insets(25, 15, 25, 15));
        sidebar.getStyleClass().add("sidebar");
        sidebar.setStyle("-fx-background-color: #0F172A;"); // Dark Navy background matching Dashboard
        sidebar.setMinWidth(260);
        sidebar.setPrefWidth(260);
        sidebar.setMaxWidth(260);

        // Logo Section
        HBox logoSection = new HBox(12);
        logoSection.setPadding(new Insets(0, 0, 25, 5));
        logoSection.setAlignment(Pos.CENTER_LEFT);

        StackPane logoIconBox = new StackPane();
        logoIconBox.getStyleClass().add("logo-icon-box");
        logoIconBox.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 8px; -fx-padding: 8px;");
        ImageView logoIcon = new ImageView(ResourceImage.load("/images/icons/ic_shield.png"));
        logoIcon.setFitWidth(20);
        logoIcon.setFitHeight(20);
        logoIconBox.getChildren().add(logoIcon);

        VBox logoText = new VBox(2);
        Label appName = new Label("Health-Sphere");
        appName.getStyleClass().add("logo-name");
        appName.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 16px;");

        Label doctorSubtext = new Label("Doctor Dashboard");
        doctorSubtext.getStyleClass().add("logo-subtext");
        doctorSubtext.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12px;");

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
            navTab.setAlignment(Pos.CENTER_LEFT);
            navTab.setPadding(new Insets(10, 14, 10, 14));
            navTab.getStyleClass().add("nav-tab");

            ImageView icon = new ImageView(ResourceImage.load("/images/icons/" + icons[i] + ".png"));
            icon.setFitWidth(18);
            icon.setFitHeight(18);

            Label tabLabel = new Label(tabs[i]);
            tabLabel.getStyleClass().add("nav-text");

            if (i == 7) { // Active Highlight: AI Health Assistant
                navTab.getStyleClass().add("nav-tab-active");
                navTab.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 8px;");
                tabLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold;");
            } else {
                navTab.setStyle("-fx-background-color: transparent; -fx-background-radius: 8px;");
                tabLabel.setStyle("-fx-text-fill: #94A3B8;");
            }

            navTab.getChildren().addAll(icon, tabLabel);
            navItems.getChildren().add(navTab);

            final int index = i;
            navTab.setOnMouseClicked(e -> handleSidebarTabClick(index));
        }

        // Spacer to push footer to bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Footer Section (Doctor Profile Card & Logout Button)
        VBox footer = new VBox(10);
        footer.setPadding(new Insets(15, 0, 0, 0));

        // Bottom Doctor Profile Box
        HBox sidebarProfile = new HBox(12);
        sidebarProfile.setAlignment(Pos.CENTER_LEFT);
        sidebarProfile.setPadding(new Insets(10, 12, 10, 12));
        sidebarProfile.getStyleClass().add("sidebar-profile-box");
        sidebarProfile.setStyle("-fx-background-color: #1E293B; -fx-background-radius: 10px; -fx-cursor: hand;");

        ImageView profileAvatar = new ImageView(ResourceImage.load("/images/doctor/portrait-3d-male-doctor.png"));
        profileAvatar.setFitWidth(36);
        profileAvatar.setFitHeight(36);
        Circle profileClip = new Circle(18, 18, 18);
        profileAvatar.setClip(profileClip);

        VBox profileTexts = new VBox(2);
        Label profSubText = new Label("Doctor Profile");
        profSubText.setStyle("-fx-text-fill: #64748B; -fx-font-size: 11px;");
        Label profName = new Label("Dr. Sarah");
        profName.getStyleClass().add("sidebar-profile-name");
        profName.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 13px;");

        profileTexts.getChildren().addAll(profSubText, profName);
        sidebarProfile.getChildren().addAll(profileAvatar, profileTexts);
        sidebarProfile.setOnMouseClicked(e -> Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene()));

        // Logout Tab
        HBox logoutTab = new HBox(12);
        logoutTab.setAlignment(Pos.CENTER_LEFT);
        logoutTab.setPadding(new Insets(10, 14, 10, 14));
        logoutTab.getStyleClass().add("nav-tab");
        logoutTab.setStyle("-fx-cursor: hand;");

        ImageView logoutIcon = new ImageView(ResourceImage.load("/images/icons/ic_logout.png"));
        logoutIcon.setFitWidth(18);
        logoutIcon.setFitHeight(18);

        Label logoutLabel = new Label("Logout");
        logoutLabel.getStyleClass().add("nav-text");
        logoutLabel.setStyle("-fx-text-fill: #94A3B8;");

        logoutTab.getChildren().addAll(logoutIcon, logoutLabel);
        logoutTab.setOnMouseClicked(e -> System.out.println("Logging out..."));

        footer.getChildren().addAll(sidebarProfile, logoutTab);

        sidebar.getChildren().addAll(logoSection, navItems, spacer, footer);
        return sidebar;
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

    /** Top Navigation & Profile Bar */
    private HBox createTopHeader() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);

        HBox breadcrumbs = new HBox(6);
        breadcrumbs.setAlignment(Pos.CENTER_LEFT);
        Label p1 = new Label("Clinical Support");
        p1.getStyleClass().add("breadcrumb-inactive");
        Label sep = new Label("›");
        sep.getStyleClass().add("breadcrumb-separator");
        Label p2 = new Label("AI Health Assistant");
        p2.getStyleClass().add("breadcrumb-active");
        breadcrumbs.getChildren().addAll(p1, sep, p2);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Search Box
        HBox searchField = new HBox(10);
        searchField.getStyleClass().add("search-input-box");
        searchField.setAlignment(Pos.CENTER_LEFT);
        searchField.setPrefWidth(260);

        ImageView searchIcon = new ImageView(ResourceImage.load("/images/icons/ic_search.png"));
        searchIcon.setFitWidth(16);
        searchIcon.setFitHeight(16);

        TextField searchInput = new TextField();
        searchInput.setPromptText("Search clinical queries...");
        searchInput.getStyleClass().add("search-text-field");
        HBox.setHgrow(searchInput, Priority.ALWAYS);

        searchField.getChildren().addAll(searchIcon, searchInput);

        // Notifications & Avatar Container
        HBox rightIcons = new HBox(16);
        rightIcons.setAlignment(Pos.CENTER_RIGHT);
        rightIcons.setPadding(new Insets(0, 0, 0, 16));

        StackPane notificationBox = new StackPane();
        ImageView bellIcon = new ImageView(ResourceImage.load("/images/icons/ic_bell.png"));
        bellIcon.setFitWidth(18);
        bellIcon.setFitHeight(18);

        Circle badge = new Circle(4, Color.web("#EF4444"));
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        notificationBox.getChildren().addAll(bellIcon, badge);
        notificationBox.getStyleClass().add("clickable-icon");
        notificationBox.setOnMouseClicked(e -> System.out.println("Opening notifications..."));

        HBox userProfile = new HBox(10);
        userProfile.setAlignment(Pos.CENTER_LEFT);
        userProfile.getStyleClass().add("clickable-icon");

        ImageView userAvatar = new ImageView(ResourceImage.load("/images/doctor/portrait-3d-male-doctor.png"));
        userAvatar.setFitWidth(36);
        userAvatar.setFitHeight(36);
        Circle clip = new Circle(18, 18, 18);
        userAvatar.setClip(clip);

        VBox userDetails = new VBox(0);
        Label docName = new Label("Dr. Sarah Jenkins");
        docName.getStyleClass().add("profile-name");
        Label docDept = new Label("Cardiology");
        docDept.getStyleClass().add("profile-dept");
        userDetails.getChildren().addAll(docName, docDept);

        userProfile.getChildren().addAll(userAvatar, userDetails);
        rightIcons.getChildren().addAll(notificationBox, userProfile);

        topBar.getChildren().addAll(breadcrumbs, spacer, searchField, rightIcons);
        return topBar;
    }

    /** Page Title and Top Actions Header */
    private BorderPane createPageHeader() {
        BorderPane header = new BorderPane();
        header.setPadding(new Insets(4, 0, 8, 0));

        VBox titles = new VBox(4);
        Label title = new Label("AI Health Assistant");
        title.getStyleClass().add("page-title");
        Label subtext = new Label("Intelligent diagnostic assistance, medical literature query, and clinical decision support.");
        subtext.getStyleClass().add("page-subtext");
        titles.getChildren().addAll(title, subtext);

        HBox actionBtns = new HBox(12);
        actionBtns.setAlignment(Pos.CENTER_RIGHT);

        Button clearBtn = new Button("Clear Chat");
        ImageView clearIcon = new ImageView(ResourceImage.load("/images/icons/ic_export.png"));
        clearIcon.setFitWidth(14);
        clearIcon.setFitHeight(14);
        clearBtn.setGraphic(clearIcon);
        clearBtn.getStyleClass().add("btn-secondary-action");
        clearBtn.setOnAction(e -> handleClearChat());

        Button newSessionBtn = new Button("New Consultation");
        ImageView addIcon = new ImageView(ResourceImage.load("/images/icons/ic_save.png"));
        addIcon.setFitWidth(14);
        addIcon.setFitHeight(14);
        newSessionBtn.setGraphic(addIcon);
        newSessionBtn.getStyleClass().add("btn-primary-action");
        newSessionBtn.setOnAction(e -> handleNewConsultation());

        actionBtns.getChildren().addAll(clearBtn, newSessionBtn);

        header.setLeft(titles);
        header.setRight(actionBtns);
        return header;
    }

    /** Body Layout: Interactive Assistant Console on Left & Analytics Panel on Right */
    private HBox createBodyLayout() {
        HBox layout = new HBox(20);

        // Left Column: Interactive Chat & Consultation Console
        VBox chatCard = createChatConsoleCard();
        HBox.setHgrow(chatCard, Priority.ALWAYS);

        // Right Column: Controls & Context Panel (FixedWidth fixed to prevent overlapping)
        VBox controlsPanel = new VBox(20);
        controlsPanel.setMinWidth(320);
        controlsPanel.setPrefWidth(340);
        controlsPanel.setMaxWidth(340);

        VBox activePatientCard = createActivePatientContextCard();
        VBox quickPromptsCard = createQuickPromptsCard();
        VBox aiDisclaimerCard = createAIDisclaimerCard();

        controlsPanel.getChildren().addAll(activePatientCard, quickPromptsCard, aiDisclaimerCard);

        layout.getChildren().addAll(chatCard, controlsPanel);
        return layout;
    }

    /** Main Interactive Chat Console Pane */
    private VBox createChatConsoleCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("panel-card");
        card.setPadding(new Insets(20));

        // Header: Model selection & Status
        BorderPane consoleHeader = new BorderPane();

        HBox modelInfo = new HBox(8);
        modelInfo.setAlignment(Pos.CENTER_LEFT);
        ImageView botIcon = new ImageView(ResourceImage.load("/images/icons/ic_robot.png"));
        botIcon.setFitWidth(20);
        botIcon.setFitHeight(20);
        Label consoleTitle = new Label("Clinical AI Co-Pilot");
        consoleTitle.getStyleClass().add("card-title");
        modelInfo.getChildren().addAll(botIcon, consoleTitle);

        HBox statusBadge = new HBox(6);
        statusBadge.setAlignment(Pos.CENTER_RIGHT);
        Circle statusDot = new Circle(4, Color.web("#10B981"));
        Label statusText = new Label("Medical LLM v4.2 Active");
        statusText.getStyleClass().add("legend-text");
        statusBadge.getChildren().addAll(statusDot, statusText);

        consoleHeader.setLeft(modelInfo);
        consoleHeader.setRight(statusBadge);

        // Chat Conversation Stream Container
        messageStream = new VBox(14);
        messageStream.setPadding(new Insets(12));
        messageStream.getStyleClass().add("chat-stream-box");

        // Initial default conversation setup
        loadDefaultChatMessages();

        VBox.setVgrow(messageStream, Priority.ALWAYS);

        // Input Prompt Box Area
        HBox inputContainer = new HBox(10);
        inputContainer.setAlignment(Pos.CENTER_LEFT);
        inputContainer.setPadding(new Insets(10, 0, 0, 0));

        promptInput = new TextField();
        promptInput.setPromptText("Ask a clinical question or request lab summary...");
        promptInput.getStyleClass().add("search-text-field");
        HBox.setHgrow(promptInput, Priority.ALWAYS);

        Button sendBtn = new Button("Send");
        ImageView sendIcon = new ImageView(ResourceImage.load("/images/icons/ic_save.png"));
        sendIcon.setFitWidth(14);
        sendIcon.setFitHeight(14);
        sendBtn.setGraphic(sendIcon);
        sendBtn.getStyleClass().add("btn-primary-action");
        sendBtn.setOnAction(e -> handleSendMessage());
        promptInput.setOnAction(e -> handleSendMessage());

        inputContainer.getChildren().addAll(promptInput, sendBtn);

        card.getChildren().addAll(consoleHeader, messageStream, inputContainer);
        return card;
    }

    /** Load default initial conversation messages */
    private void loadDefaultChatMessages() {
        messageStream.getChildren().clear();

        // Message 1: AI Welcome
        VBox msg1 = createChatMessage(
                "Clinical AI",
                "Hello Dr. Jenkins. I am synced with your current active patient file (John Doe, ID: #P-8842). How can I assist with diagnostic analysis or treatment guidelines today?",
                false
        );

        // Message 2: Doctor Query
        VBox msg2 = createChatMessage(
                "Dr. Sarah Jenkins",
                "Summarize the drug interaction risks between his prescribed Beta-Blocker and the new hypertension regimen.",
                true
        );

        // Message 3: AI Analysis Response
        VBox msg3 = createChatMessage(
                "Clinical AI",
                "Analysis complete. Combining Bisoprolol with Verapamil may increase the risk of severe bradycardia and AV block. Recommendation: Monitor heart rate closely or consider substituting with a Dihydropyridine CCB like Amlodipine.",
                false
        );

        messageStream.getChildren().addAll(msg1, msg2, msg3);
    }

    /** Handles Clear Chat Button Action */
    private void handleClearChat() {
        if (messageStream != null) {
            messageStream.getChildren().clear();
            // Show fresh greeting message after clearing history
            VBox welcomeMsg = createChatMessage(
                    "Clinical AI",
                    "Chat history cleared. How can I assist you with your next query, Dr. Jenkins?",
                    false
            );
            messageStream.getChildren().add(welcomeMsg);
        }
        if (promptInput != null) {
            promptInput.clear();
        }
    }

    /** Handles New Consultation Button Action */
    private void handleNewConsultation() {
        if (messageStream != null) {
            messageStream.getChildren().clear();
            // Start fresh consultation session message
            VBox newSessionMsg = createChatMessage(
                    "Clinical AI",
                    "New consultation session started. Ready to evaluate new patient diagnostics or medical records.",
                    false
            );
            messageStream.getChildren().add(newSessionMsg);
        }
        if (promptInput != null) {
            promptInput.clear();
        }
    }

    /** Handles user prompt sending */
    private void handleSendMessage() {
        if (promptInput == null || messageStream == null) return;
        String text = promptInput.getText().trim();
        if (!text.isEmpty()) {
            VBox userMsg = createChatMessage("Dr. Sarah Jenkins", text, true);
            messageStream.getChildren().add(userMsg);
            promptInput.clear();
        }
    }

    private VBox createChatMessage(String sender, String text, boolean isUser) {
        VBox msgBox = new VBox(4);
        msgBox.setPadding(new Insets(10, 14, 10, 14));
        msgBox.getStyleClass().add(isUser ? "chat-bubble-user" : "chat-bubble-ai");

        Label senderLbl = new Label(sender);
        senderLbl.getStyleClass().add(isUser ? "chat-sender-user" : "chat-sender-ai");

        Label contentLbl = new Label(text);
        contentLbl.setWrapText(true);
        contentLbl.getStyleClass().add("chat-text-content");

        msgBox.getChildren().addAll(senderLbl, contentLbl);
        return msgBox;
    }

    /** Active Patient Selection Card */
    private VBox createActivePatientContextCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("ai-assistant-card");
        card.setPadding(new Insets(18));

        HBox titleBox = new HBox(8);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        ImageView userIcon = new ImageView(ResourceImage.load("/images/icons/ic_patient.png"));
        userIcon.setFitWidth(18);
        userIcon.setFitHeight(18);

        Label titleLbl = new Label("Loaded Context");
        titleLbl.getStyleClass().add("ai-card-title");
        titleBox.getChildren().addAll(userIcon, titleLbl);

        Label patientDetails = new Label("Patient: John Doe (Male, 54)\nCondition: Hypertension, Stage 2\nLast Visit: Oct 20, 2023");
        patientDetails.setWrapText(true);
        patientDetails.getStyleClass().add("ai-card-desc");

        Hyperlink changeLink = new Hyperlink("Switch Patient Context →");
        changeLink.getStyleClass().add("ai-card-link");
        changeLink.setOnAction(e -> System.out.println("Changing patient context..."));

        card.getChildren().addAll(titleBox, patientDetails, changeLink);
        return card;
    }

    /** Suggested Clinical Shortcuts / Prompts Panel */
    private VBox createQuickPromptsCard() {
        VBox card = new VBox(14);
        card.getStyleClass().add("panel-card");
        card.setPadding(new Insets(18));

        Label title = new Label("Quick Clinical Prompts");
        title.getStyleClass().add("card-title");

        VBox promptList = new VBox(8);

        String[] prompts = {
            "Check Drug Interactions",
            "Generate Treatment Summary",
            "Suggest Differential Diagnosis",
            "Review Lab Trends"
        };

        for (String p : prompts) {
            Button promptBtn = new Button(p);
            promptBtn.setMaxWidth(Double.MAX_VALUE);
            promptBtn.setAlignment(Pos.CENTER_LEFT);
            promptBtn.getStyleClass().add("btn-secondary-action");
            promptBtn.setOnAction(e -> {
                if (promptInput != null) {
                    promptInput.setText(p);
                }
            });
            promptList.getChildren().add(promptBtn);
        }

        card.getChildren().addAll(title, promptList);
        return card;
    }

    /** AI Safety & Compliance Disclaimer */
    private VBox createAIDisclaimerCard() {
        VBox card = new VBox(8);
        card.getStyleClass().add("panel-card");
        card.setPadding(new Insets(16));

        BorderPane header = new BorderPane();
        HBox left = new HBox(6);
        left.setAlignment(Pos.CENTER_LEFT);

        Label aster = new Label("✻");
        aster.getStyleClass().add("emergency-asterisk");
        Label title = new Label("Clinical Disclaimer");
        title.getStyleClass().add("card-title");
        left.getChildren().addAll(aster, title);

        header.setLeft(left);

        Label desc = new Label("AI suggestions are for decision support only. Final clinical judgment remains with the attending physician.");
        desc.setWrapText(true);
        desc.getStyleClass().add("emergency-desc");

        card.getChildren().addAll(header, desc);
        return card;
    }
}