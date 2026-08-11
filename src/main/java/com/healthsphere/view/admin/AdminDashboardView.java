package com.healthsphere.view.admin;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AdminDashboardView extends ScrollPane {

    private Stage primaryStage;

    // Dynamic Live KPI Labels
    private Label totalHospitalsVal;
    private Label activeDoctorsVal;
    private Label pendingVerificationsVal;
    private Label activeIncidentsVal;

    // Real-Time Telemetry & Console
    private Label cpuUsageLabel;
    private Label activeSessionsLabel;
    private TextArea liveConsoleLog;
    private LineChart<String, Number> loadChart;
    private XYChart.Series<String, Number> loadSeries;

    // Default Constructor
    public AdminDashboardView() {
        this(null);
    }

    // Constructor with Stage
    public AdminDashboardView(Stage stage) {
        this.primaryStage = stage;

        setFitToWidth(true);
        setStyle("-fx-background-color: #0F172A; -fx-background: #0F172A;");

        // Embed Dark Glass Theme CSS
        this.getStylesheets().add("data:text/css," + getDarkThemeCSS());

        VBox mainContainer = new VBox(25);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setStyle("-fx-background-color: #0F172A;");

        // 1. Mission Control Header Bar with Live Pulse & Quick Command Actions
        HBox header = createCommandHeader();

        // 2. High-Impact Glassmorphic KPI Cards
        HBox statsSection = createStatsSection();

        // 3. Middle Section: Dynamic Telemetry Chart + Interactive System Switches
        HBox middleSection = createMiddleTelemetrySection();

        // 4. Lower Section: Activity Grid & Live Terminal Console
        GridPane bottomGrid = createBottomGrid();

        mainContainer.getChildren().addAll(header, statsSection, middleSection, bottomGrid);
        setContent(mainContainer);

        // Start Live Heartbeat Engine (Real-time updates)
        initLiveTelemetryEngine();
    }

    public Parent getView() {
        return this;
    }

    private HBox createCommandHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("HealthSphere Command Center");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        title.setTextFill(Color.WHITE);

        HBox statusBox = new HBox(8);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        Circle liveDot = new Circle(5, Color.web("#10B981"));
        Label statusText = new Label("SYSTEM STATUS: ONLINE • NODE AP-SOUTH-1 (MUMBAI)");
        statusText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        statusText.setTextFill(Color.web("#10B981"));
        statusBox.getChildren().addAll(liveDot, statusText);

        titleBox.getChildren().addAll(title, statusBox);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Quick Emergency Action Buttons
        Button lockBtn = new Button("🔒 Lockdown");
        lockBtn.setStyle(
            "-fx-background-color: #881337; " +
            "-fx-text-fill: #FDA4AF; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 9px 16px; " +
            "-fx-background-radius: 8px; " +
            "-fx-cursor: hand;"
        );
        lockBtn.setOnAction(e -> logCommand("CRITICAL: Emergency System Lockdown Triggered by Admin!"));

        Button flushCacheBtn = new Button("⚡ Flush Cache");
        flushCacheBtn.setStyle(
            "-fx-background-color: #1E293B; " +
            "-fx-text-fill: #38BDF8; " +
            "-fx-border-color: #38BDF8; " +
            "-fx-border-radius: 8px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 8px 16px; " +
            "-fx-background-radius: 8px; " +
            "-fx-cursor: hand;"
        );
        flushCacheBtn.setOnAction(e -> logCommand("EXEC: Redis L2 Cache Purged successfully."));

        Button alertBtn = new Button("📢 Broadcast Alert");
        alertBtn.setStyle(
            "-fx-background-color: #6366F1; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 9px 16px; " +
            "-fx-background-radius: 8px; " +
            "-fx-cursor: hand;"
        );
        alertBtn.setOnAction(e -> showBroadcastDialog());

        header.getChildren().addAll(titleBox, spacer, flushCacheBtn, lockBtn, alertBtn);
        return header;
    }

    private HBox createStatsSection() {
        HBox statsLayout = new HBox(20);
        statsLayout.setAlignment(Pos.CENTER);

        totalHospitalsVal = new Label("124");
        activeDoctorsVal = new Label("1,480");
        pendingVerificationsVal = new Label("18");
        activeIncidentsVal = new Label("05");

        VBox card1 = createGlassStatCard("Registered Hospitals", totalHospitalsVal, "🏢 +12% this month", "#6366F1", "#1E1B4B");
        VBox card2 = createGlassStatCard("Active Doctors", activeDoctorsVal, "👨‍⚕️ +5% this month", "#10B981", "#06281E");
        VBox card3 = createGlassStatCard("Pending Verifications", pendingVerificationsVal, "⚠️ Requires Action", "#F59E0B", "#451A03");
        VBox card4 = createGlassStatCard("Active Incidents", activeIncidentsVal, "⚡ 2 High Priority", "#EF4444", "#311B20");

        HBox.setHgrow(card1, Priority.ALWAYS);
        HBox.setHgrow(card2, Priority.ALWAYS);
        HBox.setHgrow(card3, Priority.ALWAYS);
        HBox.setHgrow(card4, Priority.ALWAYS);

        statsLayout.getChildren().addAll(card1, card2, card3, card4);
        return statsLayout;
    }

    private VBox createGlassStatCard(String title, Label valueLabel, String subtext, String accentHex, String bgGlowHex) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(18));
        card.setStyle(
            "-fx-background-color: " + bgGlowHex + "99; " +
            "-fx-background-radius: 14px; " +
            "-fx-border-color: " + accentHex + "55; " +
            "-fx-border-radius: 14px; " +
            "-fx-border-width: 1.5px;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        titleLabel.setTextFill(Color.web("#94A3B8"));

        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        valueLabel.setTextFill(Color.WHITE);

        Label subLabel = new Label(subtext);
        subLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 11));
        subLabel.setTextFill(Color.web(accentHex));

        card.getChildren().addAll(titleLabel, valueLabel, subLabel);
        return card;
    }

    private HBox createMiddleTelemetrySection() {
        HBox section = new HBox(20);

        // 1. Server Traffic & Load Telemetry Chart
        VBox chartCard = createCardContainer("Live Server Telemetry (req/sec)", "Real-time load balancing on AP-SOUTH-1 cluster.");
        HBox.setHgrow(chartCard, Priority.ALWAYS);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(0, 500, 100);
        xAxis.setTickLabelFill(Color.web("#94A3B8"));
        yAxis.setTickLabelFill(Color.web("#94A3B8"));

        loadChart = new LineChart<>(xAxis, yAxis);
        loadChart.setPrefHeight(210);
        loadChart.setLegendVisible(false);
        loadChart.setAnimated(false);

        loadSeries = new XYChart.Series<>();
        loadChart.getData().add(loadSeries);

        chartCard.getChildren().add(loadChart);

        // 2. Feature Switch Panel
        VBox switchPanel = createCardContainer("System Command Switches", "Toggle microservices and platform state.");
        switchPanel.setMinWidth(320);

        VBox switchesBox = new VBox(10);
        switchesBox.setPadding(new Insets(10, 0, 0, 0));
        switchesBox.getChildren().addAll(
            createToggleRow("Maintenance Mode", false),
            createToggleRow("AI Fraud Engine", true),
            createToggleRow("Payment Webhooks", true),
            createToggleRow("Doctor Live API", true)
        );

        switchPanel.getChildren().add(switchesBox);

        section.getChildren().addAll(chartCard, switchPanel);
        return section;
    }

    private HBox createToggleRow(String labelText, boolean initialValue) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 12, 8, 12));
        row.setStyle("-fx-background-color: #0F172A; -fx-background-radius: 8px;");

        Label lbl = new Label(labelText);
        lbl.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        lbl.setTextFill(Color.web("#E2E8F0"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        CheckBox toggle = new CheckBox();
        toggle.setSelected(initialValue);
        toggle.setOnAction(e -> {
            String state = toggle.isSelected() ? "ENABLED" : "DISABLED";
            logCommand("CONFIG CHANGE: " + labelText + " set to " + state);
        });

        row.getChildren().addAll(lbl, spacer, toggle);
        return row;
    }

    private GridPane createBottomGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        // Column 1: Live Audit Console
        VBox consoleCard = createCardContainer("Live Terminal Log Stream", "Automated system events, auth triggers, and API logs.");
        liveConsoleLog = new TextArea();
        liveConsoleLog.setEditable(false);
        liveConsoleLog.setPrefHeight(160);
        liveConsoleLog.setStyle(
            "-fx-control-inner-background: #020617; " +
            "-fx-text-fill: #38BDF8; " +
            "-fx-font-family: 'Consolas', 'Courier New', monospace; " +
            "-fx-font-size: 12px;"
        );

        logCommand("System Engine Initialized. Spring Boot Backend Port 8080 Active.");
        logCommand("JWT Public Key Loaded. Redis Cache Connected.");

        consoleCard.getChildren().add(liveConsoleLog);

        // Column 2: System Health Telemetry Indicators
        VBox healthCard = createCardContainer("Service Telemetry", "Real-time microservice status.");
        VBox statusList = new VBox(12);
        statusList.setPadding(new Insets(10, 0, 0, 0));

        cpuUsageLabel = new Label("28.4 %");
        activeSessionsLabel = new Label("1,429");

        statusList.getChildren().addAll(
            createStatusItem("Database Cluster (PostgreSQL)", "Operational", "#10B981"),
            createStatusItem("Auth API Service", "Operational", "#10B981"),
            createStatusItem("CPU Load (4 Cores)", cpuUsageLabel.getText(), "#38BDF8"),
            createStatusItem("Active Patient Sessions", activeSessionsLabel.getText(), "#F59E0B")
        );

        healthCard.getChildren().add(statusList);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(60);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(40);
        grid.getColumnConstraints().addAll(col1, col2);

        grid.add(consoleCard, 0, 0);
        grid.add(healthCard, 1, 0);

        return grid;
    }

    private VBox createCardContainer(String titleText, String subtitleText) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(18));
        card.setStyle(
            "-fx-background-color: #1E293B; " +
            "-fx-background-radius: 12px; " +
            "-fx-border-color: #334155; " +
            "-fx-border-radius: 12px;"
        );

        Label title = new Label(titleText);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label(subtitleText);
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        subtitle.setTextFill(Color.web("#94A3B8"));

        card.getChildren().addAll(title, subtitle);
        return card;
    }

    private HBox createStatusItem(String serviceName, String status, String statusColor) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-background-color: #0F172A; -fx-background-radius: 8px;");

        Label nameLabel = new Label(serviceName);
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        nameLabel.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusLabel = new Label(status);
        statusLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        statusLabel.setTextFill(Color.web(statusColor));

        row.getChildren().addAll(nameLabel, spacer, statusLabel);
        return row;
    }

    private void logCommand(String message) {
        if (liveConsoleLog != null) {
            String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            liveConsoleLog.appendText("[" + timestamp + "] " + message + "\n");
        }
    }

    private void showBroadcastDialog() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("HealthSphere System Broadcast");
        dialog.setHeaderText("Broadcast Emergency Notice to All Users");

        TextInputDialog textInput = new TextInputDialog("Scheduled maintenance in 30 minutes.");
        textInput.setTitle("Broadcast Alert");
        textInput.setHeaderText("Enter System-Wide Notification Message:");
        textInput.showAndWait().ifPresent(msg -> {
            logCommand("BROADCAST SENT: \"" + msg + "\" to all active nodes.");
        });
    }

    private void initLiveTelemetryEngine() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            // Dynamic CPU & Request Load simulation
            double cpuVal = 22.0 + Math.random() * 15.0;
            int loadVal = 180 + (int) (Math.random() * 140);
            int activeUsers = 1420 + (int) (Math.random() * 30);

            if (cpuUsageLabel != null) {
                cpuUsageLabel.setText(String.format("%.1f %%", cpuVal));
            }

            if (activeSessionsLabel != null) {
                activeSessionsLabel.setText(String.format("%,d", activeUsers));
            }

            // Update Chart
            String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            if (loadSeries != null) {
                loadSeries.getData().add(new XYChart.Data<>(currentTime, loadVal));
                if (loadSeries.getData().size() > 8) {
                    loadSeries.getData().remove(0);
                }
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private String getDarkThemeCSS() {
        return """
            .scroll-bar:vertical, .scroll-bar:horizontal {
                -fx-background-color: #0F172A;
            }
            .scroll-bar:vertical .thumb, .scroll-bar:horizontal .thumb {
                -fx-background-color: #334155;
                -fx-background-radius: 4px;
            }
            .text-area {
                -fx-background-color: #020617;
            }
            .text-area .content {
                -fx-background-color: #020617;
            }
            .chart-line-symbol {
                -fx-background-color: #38BDF8, #0F172A;
            }
            .default-color0.chart-series-line {
                -fx-stroke: #38BDF8;
                -fx-stroke-width: 2.5px;
            }
            """;
    }
}