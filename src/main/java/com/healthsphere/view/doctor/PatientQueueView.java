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
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.Objects;

public class PatientQueueView {

    private final Stage stage;

    public PatientQueueView(Stage stage) {
        this.stage = stage;
    }

    public Scene createScene() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("main-container");

        // 1. Sidebar Navigation
        mainLayout.setLeft(createSidebar());

        // 2. Main Content Area Split
        HBox mainContent = new HBox(0);
        HBox.setHgrow(mainContent, Priority.ALWAYS);

        // Center Feed Column
        VBox centerContent = new VBox(20);
        centerContent.setPadding(new Insets(24, 28, 28, 28));
        centerContent.getStyleClass().add("content-area");
        HBox.setHgrow(centerContent, Priority.ALWAYS);

        HBox headerBar = createHeaderBar();

        // Top 4 Metrics Stat Cards
        HBox metricsGrid = new HBox(16);
        metricsGrid.getChildren().addAll(
                createMetricCard("Patients in Queue", "24", "+12% vs avg", "#1D4ED8", "#EFF6FF", "M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5z"),
                createMetricCard("Avg. Wait Time", "18 min", "+5m delay", "#0D9488", "#CCFBF1", "M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zm3.3 14.71L11 12.41V7h2v4.59l3.71 3.71-1.42 1.41z"),
                createMetricCard("Critical Cases", "03", "Action Required", "#DC2626", "#FEE2E2", "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"),
                createMetricCard("Next Urgent", "#TK-4022", "Ready in Room 04", "#7C3AED", "#F3E8FF", "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z")
        );

        // Active Consultation Section + AI Insights Split
        HBox activeSectionGrid = new HBox(16);
        
        VBox currentPatientCard = createCurrentPatientCard();
        HBox.setHgrow(currentPatientCard, Priority.ALWAYS);

        VBox aiInsightsCard = createAiInsightsCard();
        aiInsightsCard.setPrefWidth(220);

        activeSectionGrid.getChildren().addAll(currentPatientCard, aiInsightsCard);

        // Queue Filter & Search Bar Area
        VBox queueTableContainer = createQueueTableSection();

        centerContent.getChildren().addAll(headerBar, metricsGrid, activeSectionGrid, queueTableContainer);

        ScrollPane centerScroll = new ScrollPane(centerContent);
        centerScroll.setFitToWidth(true);
        centerScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        centerScroll.getStyleClass().add("custom-scroll-pane");
        HBox.setHgrow(centerScroll, Priority.ALWAYS);

        // Right Sidebar: Live Activity Panel
        VBox rightActivityPanel = createLiveActivityPanel();
        rightActivityPanel.setPrefWidth(280);

        mainContent.getChildren().addAll(centerScroll, rightActivityPanel);
        mainLayout.setCenter(mainContent);

        Scene scene = new Scene(mainLayout, stage.getWidth(), stage.getHeight());

        try {
            scene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/css/patient_queue.css")).toExternalForm());
        } catch (Exception e) {
            System.err.println("CSS file /css/patient_queue.css not found.");
        }

        return scene;
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

    // =========================================================================
    // 1. LEFT SIDEBAR
    // =========================================================================
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(220);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(24, 16, 24, 16));

        Label brandLabel = new Label("MedPulse AI");
        brandLabel.getStyleClass().add("brand-title");

        VBox navBox = new VBox(6);
        navBox.setPadding(new Insets(28, 0, 0, 0));

        Button btnDash = createNavButton("Dashboard", false, "M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z");
        Button btnSchedule = createNavButton("Schedule", false, "M19 4h-1V2h-2v2H8V2H6v2H5c-1.11 0-1.99.9-1.99 2L3 20c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V10h14v10z");
        Button btnQueue = createNavButton("Patient Queue", true, "M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5z");
        Button btnInsights = createNavButton("Insights", false, "M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z");

        btnDash.setOnAction(e -> stage.setScene(new DoctorDashboardView(stage).createScene()));
        btnSchedule.setOnAction(e -> stage.setScene(new ScheduleView(stage).createScene()));
        btnQueue.setOnAction(e -> stage.setScene(new PatientQueueView(stage).createScene()));

        navBox.getChildren().addAll(btnDash, btnSchedule, btnQueue, btnInsights);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox bottomNav = new VBox(6);
        Button btnSettings = createNavButton("Settings", false, "M19.43 12.98c.04-.32.07-.64.07-.98s-.03-.66-.07-.98l2.11-1.65c.19-.15.24-.42.12-.64l-2-3.46c-.12-.22-.39-.3-.61-.22l-2.49 1c-.52-.4-1.08-.73-1.69-.98l-.38-2.65C14.46 2.18 14.25 2 14 2h-4c-.25 0-.46.18-.49.42l-.38 2.65c-.61.25-1.17.59-1.69.98l-2.49-1c-.23-.09-.49 0-.61.22l-2 3.46c-.13.22-.07.49.12.64l2.11 1.65c-.04.32-.07.65-.07.98s.03.66.07.98l-2.11 1.65c-.19.15-.24.42-.12.64l2 3.46c.12.22.39.3.61.22l2.49-1c.52.4 1.08.73 1.69.98l.38 2.65c.03.24.24.42.49.42h4c.25 0 .46-.18.49-.42l.38-2.65c.61-.25 1.17-.59 1.69-.98l2.49 1c.23.09.49 0 .61-.22l2-3.46c.12-.22.07-.49-.12-.64l-2.11-1.65zM12 15.5c-1.93 0-3.5-1.57-3.5-3.5s1.57-3.5 3.5-3.5 3.5 1.57 3.5 3.5-1.57 3.5-3.5 3.5z");
        Button btnSignOut = createNavButton("Sign Out", false, "M10.09 15.59L11.5 17l5-5-5-5-1.41 1.41L12.67 11H3v2h9.67l-2.58 2.59zM19 3H5c-1.11 0-2 .9-2 2v4h2V5h14v14H5v-4H3v4c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2z");
        btnSignOut.getStyleClass().add("sign-out-btn");

        bottomNav.getChildren().addAll(btnSettings, btnSignOut);

        sidebar.getChildren().addAll(brandLabel, navBox, spacer, bottomNav);
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

    // =========================================================================
    // 2. HEADER BAR
    // =========================================================================
    private HBox createHeaderBar() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        HBox breadcrumbs = new HBox(6);
        breadcrumbs.setAlignment(Pos.CENTER_LEFT);
        
        Hyperlink homeLink = new Hyperlink("Home");
        homeLink.getStyleClass().add("breadcrumb");
        homeLink.setOnAction(e -> stage.setScene(new DoctorDashboardView(stage).createScene()));

        Label sep1 = new Label("/");
        sep1.setStyle("-fx-text-fill: #94A3B8;");

        Hyperlink patLink = new Hyperlink("Patients");
        patLink.getStyleClass().add("breadcrumb");

        Label sep2 = new Label("/");
        sep2.setStyle("-fx-text-fill: #94A3B8;");

        Label activeLink = new Label("Queue");
        activeLink.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E3A8A; -fx-font-size: 12px;");

        breadcrumbs.getChildren().addAll(homeLink, sep1, patLink, sep2, activeLink);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox userBox = new HBox(12);
        userBox.setAlignment(Pos.CENTER_RIGHT);

        Button btnBell = new Button();
        btnBell.getStyleClass().add("icon-button");
        SVGPath bellIcon = createSVGPath("M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.64-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z", "#475569", 0.75);
        btnBell.setGraphic(bellIcon);

        VBox docInfo = new VBox(2);
        docInfo.setAlignment(Pos.CENTER_RIGHT);
        Label docName = new Label("Dr. Julian Vance");
        docName.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label docRole = new Label("Chief Surgeon");
        docRole.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748B;");
        docInfo.getChildren().addAll(docName, docRole);

        ImageView avatar = createImageView("/images/doctor/uifaces-popular-avatar.png", 32, 32);
        Node avatarNode;
        if (avatar != null) {
            Circle clip = new Circle(16, 16, 16);
            avatar.setClip(clip);
            avatarNode = avatar;
        } else {
            Circle circle = new Circle(16, Color.web("#1E3A8A"));
            Label init = new Label("JV");
            init.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px;");
            avatarNode = new StackPane(circle, init);
        }

        userBox.getChildren().addAll(btnBell, docInfo, avatarNode);
        header.getChildren().addAll(breadcrumbs, spacer, userBox);
        return header;
    }

    // =========================================================================
    // 3. METRIC CARDS
    // =========================================================================
    private VBox createMetricCard(String title, String val, String badgeText, String iconColor, String iconBg, String svgPath) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(14));
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBox = new StackPane();
        iconBox.setStyle("-fx-background-color: " + iconBg + "; -fx-background-radius: 8px; -fx-padding: 8px;");
        SVGPath icon = createSVGPath(svgPath, iconColor, 0.75);
        iconBox.getChildren().add(icon);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badge = new Label(badgeText);
        badge.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-padding: 2 6; -fx-background-radius: 4; -fx-background-color: " + iconBg + "; -fx-text-fill: " + iconColor + ";");

        topRow.getChildren().addAll(iconBox, spacer, badge);

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");

        Label valLbl = new Label(val);
        valLbl.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        card.getChildren().addAll(topRow, titleLbl, valLbl);
        return card;
    }

    // =========================================================================
    // 4. ACTIVE CONSULTATION & AI INSIGHTS
    // =========================================================================
    private VBox createCurrentPatientCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        HBox profileRow = new HBox(16);
        profileRow.setAlignment(Pos.CENTER_LEFT);

        ImageView avatar = createImageView("/images/doctor/uifaces-popular-avatar (1).png", 72, 72);
        Node avatarNode;
        if (avatar != null) {
            Circle clip = new Circle(36, 36, 36);
            avatar.setClip(clip);
            avatarNode = avatar;
        } else {
            Circle circle = new Circle(36, Color.web("#93C5FD"));
            Label init = new Label("SJ");
            init.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");
            avatarNode = new StackPane(circle, init);
        }

        VBox infoBox = new VBox(6);
        HBox nameTagRow = new HBox(10);
        nameTagRow.setAlignment(Pos.CENTER_LEFT);

        Label nameLbl = new Label("Sarah Jenkins");
        nameLbl.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Label statusTag = new Label("• IN CONSULTATION");
        statusTag.setStyle("-fx-background-color: #047857; -fx-text-fill: #FFFFFF; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 12;");

        Label urgentTag = new Label("Urgent");
        urgentTag.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 4 8; -fx-background-radius: 6;");

        nameTagRow.getChildren().addAll(nameLbl, statusTag, urgentTag);

        HBox subRow = new HBox(12);
        Label idLbl = new Label("🆔 #TK-4019");
        idLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        Label ageLbl = new Label("🎂 68 Years");
        ageLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        subRow.getChildren().addAll(idLbl, ageLbl);

        Label reasonLbl = new Label("🩺 Post-Op Follow-up");
        reasonLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #334155;");

        infoBox.getChildren().addAll(nameTagRow, subRow, reasonLbl);
        profileRow.getChildren().addAll(avatarNode, infoBox);

        // Action Buttons Row
        HBox btnRow = new HBox(12);
        Button btnComplete = new Button("✔ Complete");
        btnComplete.getStyleClass().add("primary-button");

        Button btnNoShow = new Button("🚫 No-Show");
        btnNoShow.getStyleClass().add("subtle-button");

        Button btnNote = new Button("✏ Quick Note");
        btnNote.getStyleClass().add("outline-button");

        btnRow.getChildren().addAll(btnComplete, btnNoShow, btnNote);

        card.getChildren().addAll(profileRow, btnRow);
        return card;
    }

    private VBox createAiInsightsCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("ai-insights-card");
        card.setPadding(new Insets(16));

        HBox header = new HBox(6);
        header.setAlignment(Pos.CENTER_LEFT);
        SVGPath spark = createSVGPath("M12 2L14.5 9.5L22 12L14.5 14.5L12 22L9.5 14.5L2 12L9.5 9.5L12 2Z", "#1D4ED8", 0.6);
        Label title = new Label("AI Insights");
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");
        header.getChildren().addAll(spark, title);

        Label sub1 = new Label("CONGESTION WARNING");
        sub1.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #1D4ED8;");

        Label desc1 = new Label("Arrival surge predicted at 14:00. Recommend opening Room 06 early.");
        desc1.setWrapText(true);
        desc1.setStyle("-fx-font-size: 10px; -fx-text-fill: #334155;");

        Label sub2 = new Label("WAIT TIME FORECAST");
        sub2.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #1D4ED8;");

        // Mini bar chart vector graphic
        HBox chart = new HBox(4);
        chart.setAlignment(Pos.BOTTOM_LEFT);
        double[] heights = {12, 18, 24, 30, 36};
        String[] colors = {"#BFDBFE", "#93C5FD", "#60A5FA", "#3B82F6", "#EF4444"};
        for (int i = 0; i < heights.length; i++) {
            Rectangle bar = new Rectangle(8, heights[i], Color.web(colors[i]));
            bar.setArcWidth(2);
            bar.setArcHeight(2);
            chart.getChildren().add(bar);
        }

        Label forecastText = new Label("Next 2 hours: Increasing 15%");
        forecastText.setStyle("-fx-font-size: 9px; -fx-text-fill: #64748B;");

        Button btnOptimize = new Button("Optimize Queue Flow");
        btnOptimize.getStyleClass().add("primary-button");
        btnOptimize.setMaxWidth(Double.MAX_VALUE);

        card.getChildren().addAll(header, sub1, desc1, sub2, chart, forecastText, btnOptimize);
        return card;
    }

    // =========================================================================
    // 5. QUEUE TABLE SECTION
    // =========================================================================
    private VBox createQueueTableSection() {
        VBox container = new VBox(12);
        container.getStyleClass().add("card");
        container.setPadding(new Insets(16));

        HBox filterBar = new HBox(12);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Queue");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        TextField searchField = new TextField();
        searchField.setPromptText("Search Patient ID or Token...");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(200);

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All Status", "Waiting", "In Consultation", "Completed");
        statusFilter.setValue("All Status");
        statusFilter.getStyleClass().add("custom-combo");

        filterBar.getChildren().addAll(title, spacer, searchField, statusFilter);

        VBox tableRows = new VBox(8);
        tableRows.getChildren().addAll(
                createQueueRow("#TK-4020", "Robert Fox", "10:15 AM", "12 min", "Waiting", "Room 02", false),
                createQueueRow("#TK-4021", "Esther Howard", "10:30 AM", "5 min", "Waiting", "Room 01", true),
                createQueueRow("#TK-4022", "Jenny Wilson", "10:45 AM", "Upcoming", "Urgent", "Room 04", true)
        );

        container.getChildren().addAll(filterBar, tableRows);
        return container;
    }

    private HBox createQueueRow(String token, String name, String time, String wait, String status, String room, boolean isUrgent) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 10 14; -fx-background-radius: 8px; -fx-border-color: #E2E8F0; -fx-border-radius: 8px;");

        Label tokenLbl = new Label(token);
        tokenLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #1D4ED8; -fx-pref-width: 80;");

        Label nameLbl = new Label(name);
        nameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #0F172A; -fx-pref-width: 140;");

        Label timeLbl = new Label("⏰ " + time);
        timeLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B; -fx-pref-width: 90;");

        Label waitLbl = new Label("⏳ " + wait);
        waitLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B; -fx-pref-width: 90;");

        Label roomLbl = new Label("🚪 " + room);
        roomLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #334155; -fx-pref-width: 80;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusBadge = new Label(status);
        if (isUrgent) {
            statusBadge.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 4 8; -fx-background-radius: 6;");
        } else {
            statusBadge.setStyle("-fx-background-color: #E0F2FE; -fx-text-fill: #0369A1; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 4 8; -fx-background-radius: 6;");
        }

        Button btnCall = new Button("Call In");
        btnCall.getStyleClass().add("outline-button");

        row.getChildren().addAll(tokenLbl, nameLbl, timeLbl, waitLbl, roomLbl, spacer, statusBadge, btnCall);
        return row;
    }

    // =========================================================================
    // 6. RIGHT LIVE ACTIVITY PANEL
    // =========================================================================
    private VBox createLiveActivityPanel() {
        VBox panel = new VBox(16);
        panel.setStyle("-fx-background-color: #F1F5F9; -fx-border-color: #E2E8F0; -fx-border-width: 0 0 0 1px; -fx-padding: 24 16;");

        Label title = new Label("Live Activity");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        VBox timeline = new VBox(16);
        timeline.getChildren().addAll(
                createActivityItem("10:45 AM", "Check-in Complete", "Patient #TK-4024 arrived at reception.", "#059669"),
                createActivityItem("10:30 AM", "Priority Update", "AI escalated #TK-4022 to 'Urgent' based on vitals.", "#1D4ED8"),
                createActivityItem("10:15 AM", "Consultation End", "Dr. Miller completed session with #TK-4018.", "#059669"),
                createActivityItem("10:00 AM", "Shift Start", "Morning clinic hours commenced.", "#94A3B8")
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Queue Health Bottom Box
        VBox healthCard = new VBox(8);
        healthCard.setStyle("-fx-background-color: #E0F2FE; -fx-border-color: #BAE6FD; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 12px;");

        HBox healthHeader = new HBox();
        Label healthTitle = new Label("Queue Health");
        healthTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0369A1;");

        Region hSpacer = new Region();
        HBox.setHgrow(hSpacer, Priority.ALWAYS);

        Label healthStatus = new Label("Stable");
        healthStatus.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #059669;");

        healthHeader.getChildren().addAll(healthTitle, hSpacer, healthStatus);

        ProgressBar progress = new ProgressBar(0.85);
        progress.setMaxWidth(Double.MAX_VALUE);
        progress.setStyle("-fx-accent: #059669;");

        Label healthSub = new Label("Resource allocation is optimal for current volume.");
        healthSub.setStyle("-fx-font-size: 9px; -fx-text-fill: #0369A1;");

        healthCard.getChildren().addAll(healthHeader, progress, healthSub);
        panel.getChildren().addAll(title, timeline, spacer, healthCard);
        return panel;
    }

    private HBox createActivityItem(String time, String title, String desc, String dotColor) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.TOP_LEFT);

        Circle dot = new Circle(4, Color.web(dotColor));
        VBox.setMargin(dot, new Insets(4, 0, 0, 0));

        VBox content = new VBox(2);
        Label timeLbl = new Label(time);
        timeLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748B;");

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Label descLbl = new Label(desc);
        descLbl.setWrapText(true);
        descLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748B;");

        content.getChildren().addAll(timeLbl, titleLbl, descLbl);
        item.getChildren().addAll(dot, content);
        return item;
    }
}