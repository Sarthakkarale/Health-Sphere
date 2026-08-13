package com.healthsphere.view.doctor;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;

public class DoctorDashboardView {

    private final Stage stage;

    public DoctorDashboardView(Stage stage) {
        this.stage = stage;
    }

    public Scene createScene() {
        StackPane rootOverlayPane = new StackPane();
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("main-container");

        // 1. Left Sidebar
        mainLayout.setLeft(createSidebar());

        // 2. Main Scrollable Content
        VBox mainContentBox = new VBox(20);
        mainContentBox.setPadding(new Insets(24, 32, 32, 32));
        mainContentBox.getStyleClass().add("content-area");

        mainContentBox.getChildren().addAll(
                createHeaderBar(),
                createHeroSection(),
                createKpiSection(),
                createAnalyticsSection(),
                createBottomSection()
        );

        ScrollPane scrollPane = new ScrollPane(mainContentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("custom-scroll-pane");

        mainLayout.setCenter(scrollPane);

        // 3. Floating Action Button (FAB)
        Button fabButton = new Button();
        fabButton.getStyleClass().add("fab-button");
        SVGPath sparkIcon = createSVGPath("M12 2L14.5 9.5L22 12L14.5 14.5L12 22L9.5 14.5L2 12L9.5 9.5L12 2Z", "#FFFFFF", 0.9);
        fabButton.setGraphic(sparkIcon);
        fabButton.setOnAction(e -> stage.setScene(new AiAssistantView(stage).createScene()));

        StackPane.setAlignment(fabButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(fabButton, new Insets(0, 32, 32, 0));

        rootOverlayPane.getChildren().addAll(mainLayout, fabButton);

        Scene scene = new Scene(rootOverlayPane, stage.getWidth(), stage.getHeight());

        try {
            String cssPath = getClass().getResource("/css/dashboard.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.err.println("CSS file /css/dashboard.css not found.");
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

    // --- SIDEBAR ---
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(240);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(24, 16, 24, 16));

        Label brandLabel = new Label("MediNexus AI");
        brandLabel.getStyleClass().add("brand-title");

        VBox navBox = new VBox(6);
        navBox.setPadding(new Insets(28, 0, 0, 0));

        // Navigation Items
        Button btnDashboard = createNavButton("Dashboard", true, "M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8v-10h-8v10zm0-18v6h8V3h-8z");
        Button btnAppointments = createNavButton("Appointments", false, "M19 4h-1V2h-2v2H8V2H6v2H5c-1.11 0-1.99.9-1.99 2L3 20c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V10h14v10z");
        Button btnPatients = createNavButton("Patients", false, "M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5z");
        Button btnReports = createNavButton("Reports", false, "M14 2H6c-1.1 0-1.99.9-1.99 2L4 20c0 1.1.89 2 1.99 2H18c1.1 0 2-.9 2-2V8l-6-6zm2 16H8v-2h8v2zm0-4H8v-2h8v2zm-3-5V3.5L18.5 9H13z");
        Button btnRevenue = createNavButton("Revenue", false, "M21 18v1c0 1.1-.9 2-2 2H5c-1.11 0-2-.9-2-2V5c0-1.1.89-2 2-2h14c1.1 0 2 .9 2 2v1h-9c-1.11 0-2 .9-2 2v8c0 1.1.89 2 2 2h9zm-9-2h10V8H12v8zm4-2.5c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5z");
        Button btnSettings = createNavButton("Settings", false, "M19.43 12.98c.04-.32.07-.64.07-.98s-.03-.66-.07-.98l2.11-1.65c.19-.15.24-.42.12-.64l-2-3.46c-.12-.22-.39-.3-.61-.22l-2.49 1c-.52-.4-1.08-.73-1.69-.98l-.38-2.65C14.46 2.18 14.25 2 14 2h-4c-.25 0-.46.18-.49.42l-.38 2.65c-.61.25-1.17.59-1.69.98l-2.49-1c-.23-.09-.49 0-.61.22l-2 3.46c-.13.22-.07.49.12.64l2.11 1.65c-.04.32-.07.65-.07.98s.03.66.07.98l-2.11 1.65c-.19.15-.24.42-.12.64l2 3.46c.12.22.39.3.61.22l2.49-1c.52.4 1.08.73 1.69.98l.38 2.65c.03.24.24.42.49.42h4c.25 0 .46-.18.49-.42l.38-2.65c.61-.25 1.17-.59 1.69-.98l2.49 1c.23.09.49 0 .61-.22l2-3.46c.12-.22.07-.49-.12-.64l-2.11-1.65zM12 15.5c-1.93 0-3.5-1.57-3.5-3.5s1.57-3.5 3.5-3.5 3.5 1.57 3.5 3.5-1.57 3.5-3.5 3.5z");

        btnDashboard.setOnAction(e -> stage.setScene(new DoctorDashboardView(stage).createScene()));
        btnAppointments.setOnAction(e -> stage.setScene(new ScheduleView(stage).createScene()));
        btnPatients.setOnAction(e -> stage.setScene(new PatientQueueView(stage).createScene()));
        btnReports.setOnAction(e -> stage.setScene(new PrescriptionManagementView(stage).createScene()));
        btnScheduleNavigation(btnRevenue);
        btnScheduleNavigation(btnSettings);

        navBox.getChildren().addAll(btnDashboard, btnAppointments, btnPatients, btnReports, btnRevenue, btnSettings);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Profile Box
        HBox profileCard = new HBox(12);
        profileCard.getStyleClass().add("profile-card");
        profileCard.setAlignment(Pos.CENTER_LEFT);
        profileCard.setPadding(new Insets(10));
        profileCard.setOnMouseClicked(e -> stage.setScene(new DoctorProfileView(stage).createScene()));

        StackPane avatarContainer = new StackPane();
        Circle avatarBg = new Circle(18, Color.web("#1E56A0"));
        Label avatarText = new Label("DS");
        avatarText.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");
        avatarContainer.getChildren().addAll(avatarBg, avatarText);

        VBox userDetails = new VBox(1);
        Label userName = new Label("Dr. Sterling");
        userName.getStyleClass().add("profile-name");
        Label userRole = new Label("Cardiologist");
        userRole.getStyleClass().add("profile-role");
        userDetails.getChildren().addAll(userName, userRole);

        Region profileSpacer = new Region();
        HBox.setHgrow(profileSpacer, Priority.ALWAYS);

        Label btnMore = new Label("⋮");
        btnMore.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 16px; -fx-cursor: hand;");

        profileCard.getChildren().addAll(avatarContainer, userDetails, profileSpacer, btnMore);
        sidebar.getChildren().addAll(brandLabel, navBox, spacer, profileCard);

        return sidebar;
    }

    private void btnScheduleNavigation(Button button) {
        button.setOnAction(e -> stage.setScene(new DoctorScheduleView(stage).createScene()));
    }

    private Button createNavButton(String title, boolean isActive, String iconSvg) {
        Button button = new Button(title);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.getStyleClass().add(isActive ? "nav-button-active" : "nav-button");

        SVGPath icon = createSVGPath(iconSvg, isActive ? "#FFFFFF" : "#64748B", 0.75);
        button.setGraphic(icon);
        button.setGraphicTextGap(12);

        return button;
    }

    // --- HEADER ---
    private HBox createHeaderBar() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        Label breadcrumb = new Label("Home  ›  Dashboard");
        breadcrumb.getStyleClass().add("breadcrumb");
        Label pageTitle = new Label("Medical Overview");
        pageTitle.getStyleClass().add("page-title");
        titleBox.getChildren().addAll(breadcrumb, pageTitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actionsBox = new HBox(12);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);

        HBox searchContainer = new HBox(8);
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.getStyleClass().add("search-container");

        SVGPath searchIcon = createSVGPath("M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z", "#94A3B8", 0.7);
        TextField searchField = new TextField();
        searchField.setPromptText("Search patients, reports...");
        searchField.getStyleClass().add("search-field-inner");

        searchContainer.getChildren().addAll(searchIcon, searchField);

        Button btnBell = new Button();
        btnBell.getStyleClass().add("icon-button");
        SVGPath bellIcon = createSVGPath("M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.64-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z", "#64748B", 0.75);
        btnBell.setGraphic(bellIcon);

        actionsBox.getChildren().addAll(searchContainer, btnBell);
        header.getChildren().addAll(titleBox, spacer, actionsBox);
        return header;
    }

    // --- HERO & AI SECTION ---
    private HBox createHeroSection() {
        HBox container = new HBox(16);

        // Gradient Hero Banner
        HBox heroBanner = new HBox();
        HBox.setHgrow(heroBanner, Priority.ALWAYS);
        heroBanner.getStyleClass().add("hero-banner");
        heroBanner.setPadding(new Insets(24, 28, 24, 28));

        VBox bannerTextGroup = new VBox(12);
        bannerTextGroup.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(bannerTextGroup, Priority.ALWAYS);

        Label welcomeTitle = new Label("Welcome back, Dr. Sterling");
        welcomeTitle.getStyleClass().add("hero-title");

        Label welcomeSub = new Label("You have 18 appointments today and 5 critical consultations pending. Your AI assistant has prepared your morning briefing.");
        welcomeSub.setWrapText(true);
        welcomeSub.getStyleClass().add("hero-subtitle");

        Button btnSchedule = new Button("View Schedule");
        btnSchedule.getStyleClass().add("hero-button");
        btnSchedule.setOnAction(e -> stage.setScene(new ScheduleView(stage).createScene()));

        bannerTextGroup.getChildren().addAll(welcomeTitle, welcomeSub, btnSchedule);

        // Futuristic Hologram Heart Container
        StackPane graphicPane = new StackPane();
        graphicPane.setPrefSize(220, 140);
        graphicPane.getStyleClass().add("hero-graphic-placeholder");

        SVGPath heartMesh = createSVGPath("M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z", "#38BDF8", 3.0);
        heartMesh.setOpacity(0.35);
        graphicPane.getChildren().add(heartMesh);

        heroBanner.getChildren().addAll(bannerTextGroup, graphicPane);

        // AI Briefing Card
        VBox aiCard = new VBox(12);
        aiCard.setPrefWidth(320);
        aiCard.getStyleClass().add("card");
        aiCard.setPadding(new Insets(20));

        HBox aiHeader = new HBox(6);
        aiHeader.setAlignment(Pos.CENTER_LEFT);

        SVGPath spark = createSVGPath("M12 2L14.5 9.5L22 12L14.5 14.5L12 22L9.5 14.5L2 12L9.5 9.5L12 2Z", "#2563EB", 0.65);
        Label aiTitle = new Label("AI Briefing");
        aiTitle.getStyleClass().add("card-title");

        HBox titleGroup = new HBox(8);
        titleGroup.setAlignment(Pos.CENTER_LEFT);
        titleGroup.getChildren().addAll(spark, aiTitle);

        Region aiSpacer = new Region();
        HBox.setHgrow(aiSpacer, Priority.ALWAYS);

        Label activeBadge = new Label("Active");
        activeBadge.getStyleClass().add("badge-active");

        aiHeader.getChildren().addAll(titleGroup, aiSpacer, activeBadge);

        VBox briefingList = new VBox(10);
        briefingList.getChildren().addAll(
                createBriefingItem("✓", "Lab results for Mr. Grayson are in. Critical potassium levels detected.", "#10B981"),
                createBriefingItem("ℹ", "New research paper added to your feed: \"Innovations in Cardiology 2024\".", "#3B82F6")
        );

        Button btnOpenAi = new Button("Open AI Assistant");
        btnOpenAi.setMaxWidth(Double.MAX_VALUE);
        btnOpenAi.getStyleClass().add("secondary-button");
        btnOpenAi.setOnAction(e -> stage.setScene(new AiAssistantView(stage).createScene()));

        aiCard.getChildren().addAll(aiHeader, briefingList, btnOpenAi);
        container.getChildren().addAll(heroBanner, aiCard);
        return container;
    }

    private HBox createBriefingItem(String symbol, String text, String colorHex) {
        HBox item = new HBox(8);
        item.setAlignment(Pos.TOP_LEFT);

        Circle iconBg = new Circle(8, Color.web(colorHex, 0.15));
        Label iconLabel = new Label(symbol);
        iconLabel.setStyle("-fx-text-fill: " + colorHex + "; -fx-font-weight: bold; -fx-font-size: 10px;");
        StackPane iconPane = new StackPane(iconBg, iconLabel);

        Label textLabel = new Label(text);
        textLabel.setWrapText(true);
        textLabel.getStyleClass().add("briefing-text");

        item.getChildren().addAll(iconPane, textLabel);
        return item;
    }

    // --- KPI CARDS ---
    private HBox createKpiSection() {
        HBox kpiGrid = new HBox(12);
        kpiGrid.getChildren().addAll(
                createKpiCard("M19 4h-1V2h-2v2H8V2H6v2H5c-1.11 0-1.99.9-1.99 2L3 20c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V10h14v10z", "Today's Appts", "18", "+3 new", true, "#2563EB"),
                createKpiCard("M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5z", "Total Patients", "1,248", null, false, "#0EA5E9"),
                createKpiCard("M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67z", "Pending Cons.", "5", null, false, "#8B5CF6"),
                createKpiCard("M14 2H6c-1.1 0-1.99.9-1.99 2L4 20c0 1.1.89 2 1.99 2H18c1.1 0 2-.9 2-2V8l-6-6zm2 16H8v-2h8v2zm0-4H8v-2h8v2zm-3-5V3.5L18.5 9H13z", "Reports Pending", "12", null, false, "#6366F1"),
                createKpiCard("M21 18v1c0 1.1-.9 2-2 2H5c-1.11 0-2-.9-2-2V5c0-1.1.89-2 2-2h14c1.1 0 2 .9 2 2v1h-9c-1.11 0-2 .9-2 2v8c0 1.1.89 2 2 2h9zm-9-2h10V8H12v8zm4-2.5c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5z", "Today's Revenue", "$1,450", "↑ 12%", true, "#10B981")
        );
        return kpiGrid;
    }

    private VBox createKpiCard(String iconPath, String title, String value, String subValue, boolean isPositive, String iconColor) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(16));
        HBox.setHgrow(card, Priority.ALWAYS);

        SVGPath icon = createSVGPath(iconPath, iconColor, 0.75);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("kpi-title");

        HBox valueBox = new HBox(6);
        valueBox.setAlignment(Pos.BASELINE_LEFT);
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("kpi-value");
        valueBox.getChildren().add(valueLabel);

        if (subValue != null) {
            Label subLabel = new Label(subValue);
            subLabel.getStyleClass().add(isPositive ? "kpi-badge-positive" : "kpi-badge-neutral");
            valueBox.getChildren().add(subLabel);
        }

        card.getChildren().addAll(icon, titleLabel, valueBox);
        return card;
    }

    // --- ANALYTICS / CHARTS ---
    private HBox createAnalyticsSection() {
        HBox analyticsBox = new HBox(16);

        // Chart 1: Appointments This Week (Stacked Bar Chart)
        VBox chart1Card = new VBox(12);
        chart1Card.getStyleClass().add("card");
        chart1Card.setPadding(new Insets(20));
        HBox.setHgrow(chart1Card, Priority.ALWAYS);

        HBox c1Header = new HBox();
        Label c1Title = new Label("Appointments This Week");
        c1Title.getStyleClass().add("card-title");
        Region r1 = new Region();
        HBox.setHgrow(r1, Priority.ALWAYS);
        ComboBox<String> filterBox = new ComboBox<>();
        filterBox.getItems().addAll("Weekly", "Monthly");
        filterBox.setValue("Weekly");
        filterBox.getStyleClass().add("chart-filter");
        c1Header.getChildren().addAll(c1Title, r1, filterBox);

        CategoryAxis xAxis1 = new CategoryAxis();
        NumberAxis yAxis1 = new NumberAxis(0, 10, 2);
        yAxis1.setTickLabelsVisible(false);
        yAxis1.setOpacity(0);

        StackedBarChart<String, Number> barChart = new StackedBarChart<>(xAxis1, yAxis1);
        barChart.setPrefHeight(160);
        barChart.setLegendVisible(false);
        barChart.setAnimated(false);

        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.getData().add(new XYChart.Data<>("Mon", 4));
        series1.getData().add(new XYChart.Data<>("Tue", 6));
        series1.getData().add(new XYChart.Data<>("Wed", 5));
        series1.getData().add(new XYChart.Data<>("Thu", 7));
        series1.getData().add(new XYChart.Data<>("Fri", 3));
        series1.getData().add(new XYChart.Data<>("Sat", 2));

        barChart.getData().add(series1);
        chart1Card.getChildren().addAll(c1Header, barChart);

        // Chart 2: Patient Growth (Vector Wave Chart)
        VBox chart2Card = new VBox(12);
        chart2Card.getStyleClass().add("card");
        chart2Card.setPadding(new Insets(20));
        HBox.setHgrow(chart2Card, Priority.ALWAYS);

        HBox c2Header = new HBox();
        Label c2Title = new Label("Patient Growth");
        c2Title.getStyleClass().add("card-title");
        Region r2 = new Region();
        HBox.setHgrow(r2, Priority.ALWAYS);
        Label yearBadge = new Label("● 2024");
        yearBadge.setStyle("-fx-text-fill: #1E3A8A; -fx-font-weight: bold; -fx-font-size: 11px;");
        c2Header.getChildren().addAll(c2Title, r2, yearBadge);

        StackPane vectorChartPane = createVectorCurveChart();
        chart2Card.getChildren().addAll(c2Header, vectorChartPane);

        analyticsBox.getChildren().addAll(chart1Card, chart2Card);
        return analyticsBox;
    }

    private StackPane createVectorCurveChart() {
        StackPane pane = new StackPane();
        pane.setPrefHeight(160);

        // Smooth Smooth Spline Curve matching exact image design
        Path areaPath = new Path();
        areaPath.getElements().addAll(
                new MoveTo(10, 120),
                new CubicCurveTo(50, 110, 80, 115, 120, 100),
                new CubicCurveTo(160, 80, 190, 40, 230, 40),
                new CubicCurveTo(270, 40, 290, 110, 320, 100),
                new CubicCurveTo(340, 95, 360, 40, 380, 20),
                new LineTo(380, 140),
                new LineTo(10, 140),
                new ClosePath()
        );
        areaPath.setFill(Color.web("#2563EB", 0.12));
        areaPath.setStroke(Color.TRANSPARENT);

        Path strokePath = new Path();
        strokePath.getElements().addAll(
                new MoveTo(10, 120),
                new CubicCurveTo(50, 110, 80, 115, 120, 100),
                new CubicCurveTo(160, 80, 190, 40, 230, 40),
                new CubicCurveTo(270, 40, 290, 110, 320, 100),
                new CubicCurveTo(340, 95, 360, 40, 380, 20)
        );
        strokePath.setStroke(Color.web("#2563EB"));
        strokePath.setStrokeWidth(2.5);
        strokePath.setFill(null);

        // Data Points
        Circle dot1 = new Circle(230, 40, 4, Color.web("#2563EB"));
        Circle dot2 = new Circle(380, 20, 4, Color.web("#2563EB"));

        Pane canvas = new Pane(areaPath, strokePath, dot1, dot2);

        HBox xLabels = new HBox();
        xLabels.setAlignment(Pos.BOTTOM_CENTER);
        xLabels.setSpacing(38);
        xLabels.setPadding(new Insets(130, 0, 0, 10));
        xLabels.getChildren().addAll(
                new Label("Jan"), new Label("Mar"), new Label("May"),
                new Label("Jul"), new Label("Sep"), new Label("Nov")
        );
        xLabels.getChildren().forEach(node -> node.getStyleClass().add("chart-axis-label"));

        pane.getChildren().addAll(canvas, xLabels);
        return pane;
    }

    // --- TIMELINE & TABLE SECTION ---
    private HBox createBottomSection() {
        HBox container = new HBox(16);

        // Timeline
        VBox timelineCard = new VBox(16);
        timelineCard.setPrefWidth(340);
        timelineCard.getStyleClass().add("card");
        timelineCard.setPadding(new Insets(20));

        Label timelineTitle = new Label("Upcoming Timeline");
        timelineTitle.getStyleClass().add("card-title");

        VBox timelineItems = new VBox(16);
        timelineItems.getChildren().addAll(
                createTimelineItem("09:30 AM", "Jonathan Doe", "General Checkup - Room 4B", "#2563EB"),
                createTimelineItem("10:15 AM", "Sarah Jenkins", "Follow-up Cardiology", "#10B981"),
                createTimelineItem("11:00 AM", "Michael Rossi", "Lab Results Review", "#CBD5E1")
        );

        Button btnFullDay = new Button("View Full Day");
        btnFullDay.setMaxWidth(Double.MAX_VALUE);
        btnFullDay.getStyleClass().add("outline-button");
        btnFullDay.setOnAction(e -> stage.setScene(new ScheduleView(stage).createScene()));

        timelineCard.getChildren().addAll(timelineTitle, timelineItems, btnFullDay);

        // Recent Patient Activity
        VBox activityCard = new VBox(16);
        activityCard.getStyleClass().add("card");
        activityCard.setPadding(new Insets(20));
        HBox.setHgrow(activityCard, Priority.ALWAYS);

        HBox actHeader = new HBox();
        Label activityTitle = new Label("Recent Patient Activity");
        activityTitle.getStyleClass().add("card-title");
        Region actSpacer = new Region();
        HBox.setHgrow(actSpacer, Priority.ALWAYS);
        Hyperlink seeAllLink = new Hyperlink("See All");
        seeAllLink.getStyleClass().add("link-button");
        seeAllLink.setOnAction(e -> stage.setScene(new PatientQueueView(stage).createScene()));
        actHeader.getChildren().addAll(activityTitle, actSpacer, seeAllLink);

        VBox tableBox = new VBox(12);

        // Table Header
        HBox tableHeader = new HBox();
        tableHeader.setPadding(new Insets(0, 0, 8, 0));
        Label h1 = new Label("Patient Name"); h1.setPrefWidth(160); h1.getStyleClass().add("table-header-cell");
        Label h2 = new Label("Status"); h2.setPrefWidth(110); h2.getStyleClass().add("table-header-cell");
        Label h3 = new Label("Action"); h3.setPrefWidth(140); h3.getStyleClass().add("table-header-cell");
        Label h4 = new Label("Time"); h4.setPrefWidth(90); h4.getStyleClass().add("table-header-cell");
        tableHeader.getChildren().addAll(h1, h2, h3, h4);

        tableBox.getChildren().addAll(
                tableHeader,
                createTableRow("Robert Fox", "Checked In", "badge-green", "Report Update", "2 mins ago", "RF"),
                createTableRow("Jane Cooper", "Scheduled", "badge-blue", "Lab Ordered", "15 mins ago", "JC"),
                createTableRow("Guy Hawkins", "Priority", "badge-red", "Urgent Consultation", "1 hour ago", "GH"),
                createTableRow("Leslie Alexander", "In Consult", "badge-purple", "Profile Updated", "3 hours ago", "LA")
        );

        activityCard.getChildren().addAll(actHeader, tableBox);
        container.getChildren().addAll(timelineCard, activityCard);
        return container;
    }

    private HBox createTimelineItem(String time, String name, String subtitle, String colorHex) {
        HBox item = new HBox(12);
        item.setAlignment(Pos.TOP_LEFT);

        Circle nodeDot = new Circle(5, Color.web(colorHex));
        HBox.setMargin(nodeDot, new Insets(4, 0, 0, 0));

        VBox details = new VBox(2);
        Label timeLabel = new Label(time);
        timeLabel.getStyleClass().add("timeline-time");
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("timeline-name");
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("timeline-sub");

        details.getChildren().addAll(timeLabel, nameLabel, subtitleLabel);
        item.getChildren().addAll(nodeDot, details);
        return item;
    }

    private HBox createTableRow(String name, String status, String statusClass, String action, String time, String initials) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("table-row");
        row.setPadding(new Insets(6, 0, 6, 0));
        row.setOnMouseClicked(e -> stage.setScene(new PatientDetailView(stage).createScene()));

        HBox nameCell = new HBox(8);
        nameCell.setPrefWidth(160);
        nameCell.setAlignment(Pos.CENTER_LEFT);

        Circle avatarBg = new Circle(12, Color.web("#E2E8F0"));
        Label avatarText = new Label(initials);
        avatarText.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #475569;");
        StackPane avatarContainer = new StackPane(avatarBg, avatarText);

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("table-cell-bold");
        nameCell.getChildren().addAll(avatarContainer, nameLabel);

        Label statusBadge = new Label(status);
        statusBadge.getStyleClass().addAll("status-badge", statusClass);
        StackPane statusPane = new StackPane(statusBadge);
        statusPane.setPrefWidth(110);
        statusPane.setAlignment(Pos.CENTER_LEFT);

        Label actionLabel = new Label(action);
        actionLabel.setPrefWidth(140);
        actionLabel.getStyleClass().add("table-cell");

        Label timeLabel = new Label(time);
        timeLabel.setPrefWidth(90);
        timeLabel.getStyleClass().add("table-cell-subtle");

        row.getChildren().addAll(nameCell, statusPane, actionLabel, timeLabel);
        return row;
    }
}