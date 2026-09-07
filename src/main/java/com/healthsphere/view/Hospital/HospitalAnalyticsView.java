package com.healthsphere.view.hospital;

import com.healthsphere.util.Navigation;
import com.healthsphere.util.ShimmerPlaceholder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class HospitalAnalyticsView {

    // =========================================================
    // COLOR PALETTE (Modern Light Content Theme + Dark Sidebar)
    // =========================================================

    private static final String PRIMARY_BLUE = "#2F80ED";
    private static final String PRIMARY_LIGHT = "#EEF3FF";
    private static final String DARK_TEXT = "#172B4D";
    private static final String SECONDARY_TEXT = "#64748B";
    private static final String LIGHT_BACKGROUND = "linear-gradient(to bottom right, #F4F8FC, #EEF3FF)";
    private static final String CARD_BG = "#FFFFFF";
    private static final String BORDER = "#E2E8F0";

    // Dark Sidebar Palette
    private static final String SIDEBAR_BG = "#12355B";
    private static final String SIDEBAR_BORDER = "#1D4E7A";
    private static final String SIDEBAR_TEXT = "#D6E4F0";
    private static final String SIDEBAR_TEXT_ACTIVE = "#FFFFFF";
    private static final String SIDEBAR_ICON_ACTIVE = "#2F80ED";
    private static final String SIDEBAR_ACTIVE_BG = "#2F80ED";
    private static final String SIDEBAR_HOVER_BG = "#1D4E7A";

    private static final String SUCCESS_GREEN = "#059669";
    private static final String SUCCESS_LIGHT = "#ECFDF5";

    private static final String ERROR_RED = "#DC2626";
    private static final String ERROR_LIGHT = "#FEF2F2";

    private static final String WARNING_ORANGE = "#D97706";
    private static final String WARNING_LIGHT = "#FFFBEB";

    private static final String PURPLE = "#7C3AED";
    private static final String PURPLE_LIGHT = "#F5F3FF";

    // =========================================================
    // FUNCTIONAL CONTROLLER STATE & CONTROLS
    // =========================================================

    private BarChart<String, Number> revenueChart;
    private LineChart<String, Number> appointmentChart;

    // Dynamic Label References for KPIs
    private Label revenueValueLabel;
    private Label revenueBadgeLabel;
    private Label appointmentValueLabel;
    private Label appointmentBadgeLabel;
    private Label bedOccupancyValueLabel;
    private Label bedOccupancyBadgeLabel;
    private Label patientGrowthValueLabel;
    private Label patientGrowthBadgeLabel;

    // Report Section Functional Elements
    private VBox reportListContainer;
    private TextField searchField;
    private ComboBox<String> periodBox;

    // Data Class to encapsulate dynamic reports
    private static class ReportItem {
        String name;
        String size;
        String date;

        ReportItem(String name, String size, String date) {
            this.name = name;
            this.size = size;
            this.date = date;
        }
    }

    private final List<ReportItem> allReports = new ArrayList<>(List.of(
            new ReportItem("Monthly Operational Overview - June", "PDF • 2.4 MB", "Generated June 30, 2026"),
            new ReportItem("Financial Performance & Revenue Audit", "XLSX • 4.1 MB", "Generated June 28, 2026"),
            new ReportItem("Departmental Efficiency & Occupancy Report", "PDF • 1.8 MB", "Generated June 25, 2026")
    ));

    // =========================================================
    // CREATE SCENE
    // =========================================================

    public Scene createScene(Stage stage) {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + LIGHT_BACKGROUND + ";");

        root.setLeft(HospitalSidebar.createSidebar(stage, null));
        root.setTop(createTopBar());

        // Smooth scroll wrapper
        ScrollPane scrollPane = new ScrollPane(createMainContent(stage));
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");

        root.setCenter(scrollPane);

        return new Scene(root, stage.getWidth(), stage.getHeight());
    }

    // =========================================================
    // SIDEBAR (DARK THEME)
    // =========================================================

    private VBox createSidebar(Stage stage) {
        return HospitalSidebar.createSidebar(stage, null);
    }

    // =========================================================
    // NAVIGATION BUTTON (DARK THEME)
    // =========================================================

    private Button createNavigationButton(String icon, String text, boolean selected) {

        Button button = new Button();

        Label iconLabel = new Label(icon);
        iconLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-text-fill: " + (selected ? SIDEBAR_ICON_ACTIVE : SIDEBAR_TEXT) + ";"
        );

        Label textLabel = new Label(text);
        textLabel.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: " + (selected ? "bold" : "500") + ";" +
                "-fx-text-fill: " + (selected ? SIDEBAR_TEXT_ACTIVE : SIDEBAR_TEXT) + ";"
        );

        HBox content = new HBox(12);
        content.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().addAll(iconLabel, textLabel);

        button.setGraphic(content);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(42);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(0, 12, 0, 12));

        String baseStyle = "-fx-background-radius: 8; -fx-cursor: hand;";

        if (selected) {
            button.setStyle(baseStyle + "-fx-background-color: " + PRIMARY_BLUE + ";");
        } else {
            button.setStyle(baseStyle + "-fx-background-color: transparent;");

            // Hover effects
            button.setOnMouseEntered(e -> button.setStyle(baseStyle + "-fx-background-color: " + SIDEBAR_HOVER_BG + ";"));
            button.setOnMouseExited(e -> button.setStyle(baseStyle + "-fx-background-color: transparent;"));
        }

        return button;
    }

    // =========================================================
    // TOP BAR
    // =========================================================

    private HBox createTopBar() {

        HBox topBar = new HBox(16);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 28, 12, 28));

        topBar.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 0 0 1 0;"
        );

        Label searchIcon = new Label("⌕");
        searchIcon.setStyle("-fx-font-size: 18px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        searchField = new TextField();
        searchField.setPromptText("Search analytics, reports...");
        searchField.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-prompt-text-fill: #94A3B8;" +
                "-fx-font-size: 13px;" +
                "-fx-text-inner-color: " + DARK_TEXT + ";"
        );
        HBox.setHgrow(searchField, Priority.ALWAYS);

        // Functional Live Search Listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> renderReportList());

        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPrefWidth(360);
        searchBox.setPrefHeight(40);
        searchBox.setPadding(new Insets(0, 12, 0, 12));
        searchBox.setStyle(
                "-fx-background-color: " + LIGHT_BACKGROUND + ";" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 8;"
        );
        searchBox.getChildren().addAll(searchIcon, searchField);

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        Label notification = new Label("🔔");
        notification.setStyle("-fx-font-size: 16px; -fx-cursor: hand; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label settings = new Label("⚙");
        settings.setStyle("-fx-font-size: 18px; -fx-cursor: hand; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label administrator = new Label("Hospital Administrator");
        administrator.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TEXT + ";");

        Label role = new Label("HOSPITAL ADMIN");
        role.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");

        VBox userInfo = new VBox(2);
        userInfo.setAlignment(Pos.CENTER_RIGHT);
        userInfo.getChildren().addAll(administrator, role);

        Circle avatar = new Circle(18);
        avatar.setFill(Color.web(PRIMARY_LIGHT));
        avatar.setStroke(Color.web(BORDER));

        Label avatarText = new Label("HA");
        avatarText.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        StackPane avatarBox = new StackPane(avatar, avatarText);

        topBar.getChildren().addAll(searchBox, topSpacer, notification, settings, userInfo, avatarBox);

        return topBar;
    }

    // =========================================================
    // MAIN CONTENT
    // =========================================================

    private VBox createMainContent(Stage stage) {

        VBox analyticsContent = new VBox(24);
        analyticsContent.setPadding(new Insets(28));
        analyticsContent.setStyle("-fx-background-color: " + LIGHT_BACKGROUND + ";");

        analyticsContent.getChildren().addAll(
                createPageHeader(stage),
                createKpiCards(),
                createChartsRow(),
                createPerformanceRow(),
                createReportSection(stage)
        );

        return analyticsContent;
    }

    // =========================================================
    // PAGE HEADER
    // =========================================================

    private HBox createPageHeader(Stage stage) {

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);

        Label title = new Label("Hospital Analytics");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 800; -fx-text-fill: " + DARK_TEXT + ";");

        Label subtitle = new Label("Monitor hospital performance and operational insights");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        periodBox = new ComboBox<>();
        periodBox.getItems().addAll("This Month", "Last Month", "Last 3 Months", "This Year");
        periodBox.setValue("This Month");
        periodBox.setPrefHeight(40);
        periodBox.setStyle("-fx-font-size: 12px;");

        // Dynamic Filtering Listener
        periodBox.setOnAction(e -> updateDataForSelectedPeriod(periodBox.getValue()));

        Button exportButton = new Button("↓  Export Report");
        exportButton.setPrefHeight(40);
        exportButton.setPadding(new Insets(0, 20, 0, 20));

        String actionBtnStyle =
                "-fx-background-color: " + PRIMARY_BLUE + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;";

        exportButton.setStyle(actionBtnStyle);
        exportButton.setOnMouseEntered(e -> exportButton.setStyle(actionBtnStyle + "-fx-background-color: #1550B0;"));
        exportButton.setOnMouseExited(e -> exportButton.setStyle(actionBtnStyle));

        // Functional Export Action
        exportButton.setOnAction(e -> handleExportReport(stage));

        HBox controls = new HBox(12);
        controls.setAlignment(Pos.CENTER_RIGHT);
        controls.getChildren().addAll(periodBox, exportButton);

        header.getChildren().addAll(titleBox, spacer, controls);

        return header;
    }

    // =========================================================
    // KPI CARDS
    // =========================================================

    private HBox createKpiCards() {

        HBox cards = new HBox(18);

        VBox revenueCard = createKpiCard("Total Revenue", "₹48.6L", "+12.8%", "vs last month", "₹", PRIMARY_BLUE, PRIMARY_LIGHT, true, 0);
        VBox appointmentCard = createKpiCard("Appointments", "3,842", "+8.4%", "vs last month", "▣", SUCCESS_GREEN, SUCCESS_LIGHT, true, 1);
        VBox occupancyCard = createKpiCard("Bed Occupancy", "74.2%", "+3.2%", "vs last month", "▥", PURPLE, PURPLE_LIGHT, true, 2);
        VBox growthCard = createKpiCard("Patients Growth", "12,458", "+15.6%", "vs last month", "♙", WARNING_ORANGE, WARNING_LIGHT, true, 3);

        cards.getChildren().addAll(revenueCard, appointmentCard, occupancyCard, growthCard);

        for (javafx.scene.Node node : cards.getChildren()) {
            HBox.setHgrow(node, Priority.ALWAYS);
        }

        return cards;
    }

    // =========================================================
    // KPI CARD
    // =========================================================

    private VBox createKpiCard(
            String title,
            String value,
            String percentage,
            String period,
            String icon,
            String color,
            String bgColor,
            boolean positive,
            int kpiType
    ) {

        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setPrefHeight(125);

        applyCardStyle(card);

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        Label iconLabel = new Label(icon);
        iconLabel.setPrefSize(36, 36);
        iconLabel.setAlignment(Pos.CENTER);
        iconLabel.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                "-fx-background-radius: 8;" +
                "-fx-text-fill: " + color + ";" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;"
        );

        top.getChildren().addAll(titleLabel, topSpacer, iconLabel);

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: 800; -fx-text-fill: " + DARK_TEXT + ";");

        HBox trendBox = new HBox(6);
        trendBox.setAlignment(Pos.CENTER_LEFT);

        Label badge = new Label(percentage);
        badge.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: " + (positive ? SUCCESS_GREEN : ERROR_RED) + ";" +
                "-fx-background-color: " + (positive ? SUCCESS_LIGHT : ERROR_LIGHT) + ";" +
                "-fx-padding: 2 6;" +
                "-fx-background-radius: 4;"
        );

        Label periodLabel = new Label(period);
        periodLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        trendBox.getChildren().addAll(badge, periodLabel);

        card.getChildren().addAll(top, valueLabel, trendBox);

        // Bind references to class properties for live updates
        if (kpiType == 0) {
            revenueValueLabel = valueLabel;
            revenueBadgeLabel = badge;
        } else if (kpiType == 1) {
            appointmentValueLabel = valueLabel;
            appointmentBadgeLabel = badge;
        } else if (kpiType == 2) {
            bedOccupancyValueLabel = valueLabel;
            bedOccupancyBadgeLabel = badge;
        } else if (kpiType == 3) {
            patientGrowthValueLabel = valueLabel;
            patientGrowthBadgeLabel = badge;
        }

        return card;
    }

    // =========================================================
    // CHARTS ROW
    // =========================================================

    private HBox createChartsRow() {

        HBox row = new HBox(18);

        VBox revenueCard = createRevenueChart();
        VBox appointmentCard = createAppointmentChart();

        HBox.setHgrow(revenueCard, Priority.ALWAYS);
        HBox.setHgrow(appointmentCard, Priority.ALWAYS);

        row.getChildren().addAll(revenueCard, appointmentCard);

        return row;
    }

    // =========================================================
    // REVENUE CHART
    // =========================================================

    private VBox createRevenueChart() {

        VBox card = new VBox(14);
        card.setPrefHeight(340);
        card.setPadding(new Insets(20));

        applyCardStyle(card);

        HBox header = createChartHeader("Revenue Overview", "Monthly revenue performance in Lakhs (₹)");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(60);
        yAxis.setTickUnit(10);
        yAxis.setStyle("-fx-tick-label-fill: " + SECONDARY_TEXT + "; -fx-font-size: 10px;");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setStyle("-fx-tick-label-fill: " + SECONDARY_TEXT + "; -fx-font-size: 10px;");

        revenueChart = new BarChart<>(xAxis, yAxis);
        revenueChart.setLegendVisible(false);
        revenueChart.setAnimated(false);
        revenueChart.setStyle("-fx-background-color: transparent;");

        // Initial Data Populate
        populateRevenueChartData("This Month");

        card.getChildren().addAll(header, revenueChart);
        VBox.setVgrow(revenueChart, Priority.ALWAYS);

        return card;
    }

    // =========================================================
    // APPOINTMENT CHART
    // =========================================================

    private VBox createAppointmentChart() {

        VBox card = new VBox(14);
        card.setPrefHeight(340);
        card.setPadding(new Insets(20));

        applyCardStyle(card);

        HBox header = createChartHeader("Appointment Trends", "Appointments overall in selected period");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setStyle("-fx-tick-label-fill: " + SECONDARY_TEXT + "; -fx-font-size: 10px;");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setStyle("-fx-tick-label-fill: " + SECONDARY_TEXT + "; -fx-font-size: 10px;");

        appointmentChart = new LineChart<>(xAxis, yAxis);
        appointmentChart.setLegendVisible(false);
        appointmentChart.setAnimated(false);
        appointmentChart.setCreateSymbols(true);
        appointmentChart.setStyle("-fx-background-color: transparent;");

        // Initial Data Populate
        populateAppointmentChartData("This Month");

        card.getChildren().addAll(header, appointmentChart);
        VBox.setVgrow(appointmentChart, Priority.ALWAYS);

        return card;
    }

    // =========================================================
    // CHART HEADER
    // =========================================================

    private HBox createChartHeader(String title, String subtitle) {

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: " + DARK_TEXT + ";");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        titleBox.getChildren().addAll(titleLabel, subtitleLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button menu = new Button("•••");
        menu.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        header.getChildren().addAll(titleBox, spacer, menu);

        return header;
    }

    // =========================================================
    // PERFORMANCE ROW
    // =========================================================

    private HBox createPerformanceRow() {

        HBox row = new HBox(18);

        VBox department = createDepartmentPerformance();
        VBox utilization = createBedUtilization();
        VBox growth = createPatientGrowth();

        HBox.setHgrow(department, Priority.ALWAYS);
        HBox.setHgrow(utilization, Priority.ALWAYS);
        HBox.setHgrow(growth, Priority.ALWAYS);

        row.getChildren().addAll(department, utilization, growth);

        return row;
    }

    // =========================================================
    // DEPARTMENT PERFORMANCE
    // =========================================================

    private VBox createDepartmentPerformance() {

        VBox card = new VBox(14);
        card.setPadding(new Insets(20));

        applyCardStyle(card);

        HBox header = createSimpleHeader("Department Performance", "Patient visits by department");

        VBox list = new VBox(12);
        list.getChildren().addAll(
                createPerformanceItem("Cardiology", "1,284 visits", 0.88, PRIMARY_BLUE),
                createPerformanceItem("Orthopedics", "986 visits", 0.72, PURPLE),
                createPerformanceItem("Neurology", "824 visits", 0.61, SUCCESS_GREEN),
                createPerformanceItem("Pediatrics", "642 visits", 0.48, WARNING_ORANGE)
        );

        card.getChildren().addAll(header, list);

        return card;
    }

    // =========================================================
    // PERFORMANCE ITEM
    // =========================================================

    private VBox createPerformanceItem(String name, String value, double progress, String color) {

        VBox item = new VBox(6);

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: " + DARK_TEXT + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: 600; -fx-text-fill: " + SECONDARY_TEXT + ";");

        top.getChildren().addAll(nameLabel, spacer, valueLabel);

        ProgressBar progressBar = new ProgressBar(progress);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(8);
        progressBar.setStyle("-fx-accent: " + color + ";");

        item.getChildren().addAll(top, progressBar);

        return item;
    }

    // =========================================================
    // BED UTILIZATION
    // =========================================================

    private VBox createBedUtilization() {

        VBox card = new VBox(14);
        card.setPadding(new Insets(20));

        applyCardStyle(card);

        card.getChildren().add(createSimpleHeader("Bed Utilization", "Current occupancy by ward"));

        HBox content = new HBox(20);
        content.setAlignment(Pos.CENTER_LEFT);

        StackPane circularChart = createCircularProgress(74.2, PRIMARY_BLUE);

        VBox details = new VBox(10);
        details.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(details, Priority.ALWAYS);

        details.getChildren().addAll(
                createMiniStat("General Ward", "75%", PRIMARY_BLUE),
                createMiniStat("ICU Ward", "75%", PURPLE),
                createMiniStat("Emergency Ward", "25%", ERROR_RED),
                createMiniStat("Private Ward", "70%", SUCCESS_GREEN)
        );

        content.getChildren().addAll(circularChart, details);
        card.getChildren().add(content);

        return card;
    }

    // =========================================================
    // CIRCULAR PROGRESS
    // =========================================================

    private StackPane createCircularProgress(double percentage, String color) {

        StackPane container = new StackPane();
        container.setPrefSize(120, 120);

        Circle background = new Circle(48);
        background.setFill(Color.TRANSPARENT);
        background.setStroke(Color.web("#E2E8F0"));
        background.setStrokeWidth(10);

        Arc progress = new Arc();
        progress.setCenterX(0);
        progress.setCenterY(0);
        progress.setRadiusX(48);
        progress.setRadiusY(48);
        progress.setStartAngle(90);
        progress.setLength(-(percentage / 100.0) * 360);
        progress.setType(ArcType.OPEN);
        progress.setFill(Color.TRANSPARENT);
        progress.setStroke(Color.web(color));
        progress.setStrokeWidth(10);

        Label value = new Label(String.format("%.1f%%", percentage));
        value.setStyle("-fx-font-size: 18px; -fx-font-weight: 800; -fx-text-fill: " + DARK_TEXT + ";");

        Label label = new Label("Occupied");
        label.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        VBox center = new VBox(0);
        center.setAlignment(Pos.CENTER);
        center.getChildren().addAll(value, label);

        container.getChildren().addAll(background, progress, center);

        return container;
    }

    // =========================================================
    // MINI STAT
    // =========================================================

    private HBox createMiniStat(String title, String value, String color) {

        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);

        Circle dot = new Circle(4);
        dot.setFill(Color.web(color));

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: " + DARK_TEXT + ";");

        row.getChildren().addAll(dot, titleLabel, spacer, valueLabel);

        return row;
    }

    // =========================================================
    // PATIENT GROWTH
    // =========================================================

    private VBox createPatientGrowth() {

        VBox card = new VBox(12);
        card.setPadding(new Insets(20));

        applyCardStyle(card);

        card.getChildren().add(createSimpleHeader("Patient Growth", "Monthly patient registration"));

        Label value = new Label("12,458");
        value.setStyle("-fx-font-size: 26px; -fx-font-weight: 800; -fx-text-fill: " + DARK_TEXT + ";");

        Label growth = new Label("↑ 15.6% target growth rate");
        growth.setStyle("-fx-font-size: 11px; -fx-font-weight: 600; -fx-text-fill: " + SUCCESS_GREEN + ";");

        ProgressBar growthBar = new ProgressBar(0.78);
        growthBar.setMaxWidth(Double.MAX_VALUE);
        growthBar.setPrefHeight(8);
        growthBar.setStyle("-fx-accent: " + SUCCESS_GREEN + ";");

        HBox monthly = new HBox();
        monthly.setAlignment(Pos.CENTER_LEFT);

        Label monthlyLabel = new Label("Monthly target");
        monthlyLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label target = new Label("16,000");
        target.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: " + DARK_TEXT + ";");

        monthly.getChildren().addAll(monthlyLabel, spacer, target);

        card.getChildren().addAll(value, growth, growthBar, monthly);

        return card;
    }

    // =========================================================
    // SIMPLE HEADER
    // =========================================================

    private HBox createSimpleHeader(String title, String subtitle) {

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: " + DARK_TEXT + ";");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        titleBox.getChildren().addAll(titleLabel, subtitleLabel);
        header.getChildren().add(titleBox);

        return header;
    }

    // =========================================================
    // REPORT SECTION
    // =========================================================

    private VBox createReportSection(Stage stage) {

        VBox card = new VBox(16);
        card.setPadding(new Insets(20));

        applyCardStyle(card);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);

        Label title = new Label("Monthly Reports");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: " + DARK_TEXT + ";");

        Label subtitle = new Label("Download generated operational and performance reports");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button generateButton = new Button("＋  Generate Custom Report");
        generateButton.setPrefHeight(36);
        generateButton.setPadding(new Insets(0, 16, 0, 16));

        String btnStyle =
                "-fx-background-color: " + PRIMARY_LIGHT + ";" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;";

        generateButton.setStyle(btnStyle);
        generateButton.setOnAction(e -> handleGenerateCustomReport());

        header.getChildren().addAll(titleBox, spacer, generateButton);

        reportListContainer = new VBox(10);
        renderReportList();

        card.getChildren().addAll(header, reportListContainer);

        return card;
    }

    private void renderReportList() {
        reportListContainer.getChildren().clear();
        String query = searchField != null && searchField.getText() != null ? searchField.getText().toLowerCase().trim() : "";

        for (ReportItem item : allReports) {
            if (query.isEmpty() || item.name.toLowerCase().contains(query)) {
                reportListContainer.getChildren().add(createReportItemRow(item));
            }
        }
    }

    private HBox createReportItemRow(ReportItem item) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle(
                "-fx-background-color: " + LIGHT_BACKGROUND + ";" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 8;"
        );

        Label docIcon = new Label("📄");
        docIcon.setStyle("-fx-font-size: 18px;");

        VBox info = new VBox(2);
        Label title = new Label(item.name);
        title.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: " + DARK_TEXT + ";");

        Label meta = new Label(item.size + " • " + item.date);
        meta.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        info.getChildren().addAll(title, meta);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button downloadBtn = new Button("Download");
        downloadBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        downloadBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Downloading " + item.name + "...", ButtonType.OK);
            alert.showAndWait();
        });

        row.getChildren().addAll(docIcon, info, spacer, downloadBtn);
        return row;
    }

    // =========================================================
    // DYNAMIC DATA BACKEND LOGIC
    // =========================================================

    private void updateDataForSelectedPeriod(String period) {
        populateRevenueChartData(period);
        populateAppointmentChartData(period);

        if ("This Month".equals(period)) {
            revenueValueLabel.setText("₹48.6L");
            revenueBadgeLabel.setText("+12.8%");
            appointmentValueLabel.setText("3,842");
            appointmentBadgeLabel.setText("+8.4%");
            bedOccupancyValueLabel.setText("74.2%");
            bedOccupancyBadgeLabel.setText("+3.2%");
            patientGrowthValueLabel.setText("12,458");
            patientGrowthBadgeLabel.setText("+15.6%");
        } else if ("Last Month".equals(period)) {
            revenueValueLabel.setText("₹43.0L");
            revenueBadgeLabel.setText("+4.1%");
            appointmentValueLabel.setText("3,540");
            appointmentBadgeLabel.setText("+2.1%");
            bedOccupancyValueLabel.setText("71.0%");
            bedOccupancyBadgeLabel.setText("-1.2%");
            patientGrowthValueLabel.setText("10,800");
            patientGrowthBadgeLabel.setText("+8.2%");
        } else if ("Last 3 Months".equals(period)) {
            revenueValueLabel.setText("₹138.6L");
            revenueBadgeLabel.setText("+9.5%");
            appointmentValueLabel.setText("10,532");
            appointmentBadgeLabel.setText("+6.3%");
            bedOccupancyValueLabel.setText("72.8%");
            bedOccupancyBadgeLabel.setText("+2.0%");
            patientGrowthValueLabel.setText("32,150");
            patientGrowthBadgeLabel.setText("+11.4%");
        } else if ("This Year".equals(period)) {
            revenueValueLabel.setText("₹248.2L");
            revenueBadgeLabel.setText("+18.4%");
            appointmentValueLabel.setText("18,572");
            appointmentBadgeLabel.setText("+14.2%");
            bedOccupancyValueLabel.setText("75.4%");
            bedOccupancyBadgeLabel.setText("+5.1%");
            patientGrowthValueLabel.setText("64,200");
            patientGrowthBadgeLabel.setText("+22.0%");
        }
    }

    private void populateRevenueChartData(String period) {
        revenueChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        if ("This Month".equals(period) || "Last Month".equals(period)) {
            series.getData().add(new XYChart.Data<>("Jan", 32));
            series.getData().add(new XYChart.Data<>("Feb", 38));
            series.getData().add(new XYChart.Data<>("Mar", 41));
            series.getData().add(new XYChart.Data<>("Apr", 47));
            series.getData().add(new XYChart.Data<>("May", 43));
            series.getData().add(new XYChart.Data<>("Jun", 48.6));
        } else {
            series.getData().add(new XYChart.Data<>("Q1", 111));
            series.getData().add(new XYChart.Data<>("Q2", 138.6));
            series.getData().add(new XYChart.Data<>("Q3", 145));
            series.getData().add(new XYChart.Data<>("Q4", 160));
        }

        revenueChart.getData().add(series);

        for (XYChart.Data<String, Number> data : series.getData()) {
            if (data.getNode() != null) {
                data.getNode().setStyle("-fx-bar-fill: " + PRIMARY_BLUE + "; -fx-background-radius: 4 4 0 0;");
            }
        }
    }

    private void populateAppointmentChartData(String period) {
        appointmentChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        if ("This Month".equals(period) || "Last Month".equals(period)) {
            series.getData().add(new XYChart.Data<>("Jan", 2480));
            series.getData().add(new XYChart.Data<>("Feb", 2670));
            series.getData().add(new XYChart.Data<>("Mar", 2890));
            series.getData().add(new XYChart.Data<>("Apr", 3150));
            series.getData().add(new XYChart.Data<>("May", 3540));
            series.getData().add(new XYChart.Data<>("Jun", 3842));
        } else {
            series.getData().add(new XYChart.Data<>("Q1", 8040));
            series.getData().add(new XYChart.Data<>("Q2", 10532));
            series.getData().add(new XYChart.Data<>("Q3", 11200));
            series.getData().add(new XYChart.Data<>("Q4", 12400));
        }

        appointmentChart.getData().add(series);

        if (series.getNode() != null) {
            series.getNode().setStyle("-fx-stroke: " + SUCCESS_GREEN + "; -fx-stroke-width: 3px;");
        }
    }

    private void handleExportReport(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Analytics CSV Report");
        fileChooser.setInitialFileName("Hospital_Analytics_Data.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("Metric,Value,Period");
                writer.println("Revenue," + revenueValueLabel.getText() + "," + periodBox.getValue());
                writer.println("Appointments," + appointmentValueLabel.getText() + "," + periodBox.getValue());
                writer.println("Bed Occupancy," + bedOccupancyValueLabel.getText() + "," + periodBox.getValue());
                writer.println("Patient Growth," + patientGrowthValueLabel.getText() + "," + periodBox.getValue());

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Report exported successfully to " + file.getName(), ButtonType.OK);
                alert.showAndWait();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error exporting file: " + ex.getMessage(), ButtonType.OK);
                alert.showAndWait();
            }
        }
    }

    private void handleGenerateCustomReport() {
        allReports.add(0, new ReportItem("Custom Generated Summary Report", "PDF • 3.2 MB", "Generated Just Now"));
        renderReportList();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Custom report generated successfully!", ButtonType.OK);
        alert.showAndWait();
    }

    // =========================================================
    // HELPER STYLES
    // =========================================================

    private void applyCardStyle(VBox card) {
        card.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;"
        );
    }
}