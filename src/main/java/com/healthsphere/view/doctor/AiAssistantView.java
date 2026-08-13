package com.healthsphere.view.doctor;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.Objects;

public class AiAssistantView {

    private final Stage stage;
    private final Runnable backHandler;

    public AiAssistantView(Stage stage) {
        this(stage, null);
    }

    public AiAssistantView(Stage stage, Runnable backHandler) {
        this.stage = stage;
        this.backHandler = backHandler;
    }

    public Scene getScene() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("main-container");

        HBox combinedSidebar = new HBox();
        combinedSidebar.getChildren().addAll(createPrimarySidebar(), createRecentQueriesSidebar());
        mainLayout.setLeft(combinedSidebar);

        BorderPane chatWorkspace = new BorderPane();
        chatWorkspace.setTop(createHeaderBar());

        VBox chatStream = createChatStream();
        ScrollPane chatScroll = new ScrollPane(chatStream);
        chatScroll.setFitToWidth(true);
        chatScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        chatScroll.getStyleClass().add("custom-scroll-pane");
        chatWorkspace.setCenter(chatScroll);

        chatWorkspace.setBottom(createChatInputSection());
        mainLayout.setCenter(chatWorkspace);

        Scene scene = new Scene(mainLayout,
                stage.getWidth() > 0 ? stage.getWidth() : 1400,
                stage.getHeight() > 0 ? stage.getHeight() : 920);

        try {
            scene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/css/ai_assistant.css")).toExternalForm());
        } catch (Exception e) {
            System.err.println("CSS file /css/ai_assistant.css not found.");
        }

        return scene;
    }

    public Scene createScene() {
        return getScene();
    }

    private SVGPath createSVGPath(String d, String fillColor, double scale) {
        SVGPath path = new SVGPath();
        path.setContent(d);
        path.setFill(Color.web(fillColor));
        path.setScaleX(scale);
        path.setScaleY(scale);
        return path;
    }

    private ImageView createImageView(String resourcePath, double width, double height) {
        try {
            InputStream stream = getClass().getResourceAsStream(resourcePath);
            if (stream != null) {
                Image img = new Image(stream);
                ImageView iv = new ImageView(img);
                iv.setFitWidth(width);
                iv.setFitHeight(height);
                iv.setPreserveRatio(true);
                iv.setSmooth(true);
                return iv;
            }
        } catch (Exception e) {
            System.err.println("Resource missing: " + resourcePath);
        }
        return null;
    }

    private VBox createPrimarySidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(220);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(24, 16, 24, 16));

        Label brandLabel = new Label("MediNexus AI");
        brandLabel.getStyleClass().add("brand-title");

        VBox mainNavBox = new VBox(6);
        mainNavBox.setPadding(new Insets(28, 0, 0, 0));

        Button btnDashboard = createNavButton("Dashboard", false, "M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z");
        Button btnAppointments = createNavButton("Appointments", false, "M19 4h-1V2h-2v2H8V2H6v2H5c-1.11 0-1.99.9-1.99 2L3 20c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V10h14v10zm0-12H5V6h14v2z");
        Button btnPatients = createNavButton("Patients", false, "M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5z");
        Button btnReports = createNavButton("Reports", false, "M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z");
        Button btnRevenue = createNavButton("Revenue", false, "M21 18v1c0 1.1-.9 2-2 2H5c-1.11 0-2-.9-2-2V5c0-1.1.89-2 2-2h14c1.1 0 2-.9 2-2v1h-9c-1.11 0-2 .9-2 2v8c0 1.1.89 2 2 2h9zm-9-2h10V8H12v8zm4-2.5c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5z");
        Button btnSettings = createNavButton("Settings", false, "M19.43 12.98c.04-.32.07-.64.07-.98s-.03-.66-.07-.98l2.11-1.65c.19-.15.24-.42.12-.64l-2-3.46c-.12-.22-.39-.3-.61-.22l-2.49 1c-.52-.4-1.08-.73-1.69-.98l-.38-2.65C14.46 2.18 14.25 2 14 2h-4c-.25 0-.46.18-.49.42l-.38 2.65c-.61.25-1.17.59-1.69.98l-2.49-1c-.23-.09-.49 0-.61.22l-2 3.46c-.13.22-.07.49.12.64l2.11 1.65c-.04.32-.07.65-.07.98s.03.66.07.98l-2.11 1.65c-.19.15-.24.42-.12.64l2 3.46c.12.22.39.3.61.22l2.49-1c.52.4 1.08.73 1.69.98l.38 2.65c.03.24.24.42.49.42h4c.25 0 .46-.18.49-.42l.38-2.65c.61-.25 1.17-.59 1.69-.98l2.49 1c.23.09.49 0 .61-.22l2-3.46c.12-.22.07-.49-.12-.64l-2.11-1.65zM12 15.5c-1.93 0-3.5-1.57-3.5-3.5s1.57-3.5 3.5-3.5 3.5 1.57 3.5 3.5-1.57 3.5-3.5-3.5z");

        btnDashboard.setOnAction(e -> stage.setScene(new DoctorDashboardView(stage, () -> stage.setScene(getScene())).getScene()));
        btnAppointments.setOnAction(e -> stage.setScene(new DoctorScheduleView(stage, () -> stage.setScene(getScene())).getScene()));
        btnPatients.setOnAction(e -> stage.setScene(new PatientQueueView(stage, () -> stage.setScene(getScene())).getScene()));
        btnReports.setOnAction(e -> stage.setScene(new PrescriptionManagementView(stage, () -> stage.setScene(getScene())).getScene()));

        mainNavBox.getChildren().addAll(btnDashboard, btnAppointments, btnPatients, btnReports, btnRevenue, btnSettings);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox profileCard = createProfileCard();

        sidebar.getChildren().addAll(brandLabel, mainNavBox, spacer, profileCard);
        return sidebar;
    }

    private Button createNavButton(String title, boolean isActive, String iconSvg) {
        Button btn = new Button(title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.getStyleClass().add(isActive ? "nav-button-active" : "nav-button");

        SVGPath icon = createSVGPath(iconSvg, isActive ? "#FFFFFF" : "#64748B", 0.75);
        btn.setGraphic(icon);
        btn.setGraphicTextGap(12);
        return btn;
    }

    private HBox createProfileCard() {
        HBox profileCard = new HBox(10);
        profileCard.getStyleClass().add("profile-card");
        profileCard.setAlignment(Pos.CENTER_LEFT);

        profileCard.setOnMouseClicked(e -> {
            Runnable backAction = () -> stage.setScene(getScene());
            stage.setScene(new DoctorProfileView(stage, backAction).getScene());
        });

        ImageView avatar = createImageView("/images/doctor/portrait-3d-male-doctor.png", 32, 32);
        Node avatarNode;
        if (avatar != null) {
            Circle clip = new Circle(16, 16, 16);
            avatar.setClip(clip);
            avatarNode = avatar;
        } else {
            avatarNode = new Circle(16, Color.web("#1D4ED8"));
        }

        VBox userDetails = new VBox(2);
        Label userName = new Label("Dr. Sterling");
        userName.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label userDept = new Label("Cardiologist");
        userDept.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748B;");
        userDetails.getChildren().addAll(userName, userDept);

        profileCard.getChildren().addAll(avatarNode, userDetails);
        return profileCard;
    }

    private VBox createRecentQueriesSidebar() {
        VBox sidebar = new VBox(14);
        sidebar.setPrefWidth(220);
        sidebar.getStyleClass().add("sidebar-secondary");
        sidebar.setPadding(new Insets(24, 16, 24, 16));

        Label headerTitle = new Label("Recent Queries");
        headerTitle.getStyleClass().add("section-header-title");

        VBox recentList = new VBox(10);
        recentList.getChildren().addAll(
                createRecentItem("Patient #4920: Lab Analysis", "2 mins ago", true),
                createRecentItem("Warfarin Interaction Check", "1 hour ago", false)
        );

        sidebar.getChildren().addAll(headerTitle, recentList);
        return sidebar;
    }

    private VBox createRecentItem(String title, String time, boolean isActive) {
        VBox card = new VBox(4);
        card.getStyleClass().add(isActive ? "recent-card-active" : "recent-card");
        card.setPadding(new Insets(10, 12, 10, 12));

        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add(isActive ? "recent-title-active" : "recent-title");

        Label timeLbl = new Label(time);
        timeLbl.getStyleClass().add("recent-time");

        card.getChildren().addAll(titleLbl, timeLbl);
        return card;
    }

    private HBox createHeaderBar() {
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 28, 16, 28));

        if (backHandler != null) {
            Button btnBack = new Button("← Back");
            btnBack.getStyleClass().add("outline-button");
            btnBack.setOnAction(e -> backHandler.run());
            header.getChildren().add(btnBack);
        }

        Label pageTitle = new Label("AI Clinical Assistant");
        pageTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(pageTitle, spacer);
        return header;
    }

    private VBox createChatStream() {
        VBox chatFeed = new VBox(20);
        chatFeed.setPadding(new Insets(20, 40, 20, 40));

        HBox userRow = new HBox();
        userRow.setAlignment(Pos.CENTER_RIGHT);

        VBox userBubble = new VBox(6);
        userBubble.getStyleClass().add("user-bubble");
        userBubble.setPadding(new Insets(14, 18, 14, 18));
        userBubble.setMaxWidth(600);

        Label userText = new Label("Analyze lab results for Patient ID #4920.");
        userText.setWrapText(true);
        userText.getStyleClass().add("user-bubble-text");

        userBubble.getChildren().add(userText);
        userRow.getChildren().add(userBubble);

        chatFeed.getChildren().add(userRow);
        return chatFeed;
    }

    private VBox createChatInputSection() {
        VBox bottomContainer = new VBox(10);
        bottomContainer.setPadding(new Insets(10, 40, 16, 40));

        VBox inputBox = new VBox(8);
        inputBox.getStyleClass().add("chat-input-container");
        inputBox.setPadding(new Insets(12, 16, 12, 16));

        TextField txtPrompt = new TextField();
        txtPrompt.setPromptText("Ask AI assistant...");
        txtPrompt.getStyleClass().add("chat-input-field");

        inputBox.getChildren().add(txtPrompt);
        bottomContainer.getChildren().add(inputBox);
        return bottomContainer;
    }
}