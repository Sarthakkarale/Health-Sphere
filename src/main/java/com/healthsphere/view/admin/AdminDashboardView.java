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
        setStyle("-fx-background-color: #F8FAFC; -fx-background: #F8FAFC;");

        // Embed Modern Light Theme CSS
        this.getStylesheets().add("data:text/css," + getLightThemeCSS());

        VBox mainContainer = new VBox(25);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setStyle("-fx-background-color: #F8FAFC;");

        // 1. Mission Control Header Bar
        HBox header = createCommandHeader();

        // 2. Crisp Light KPI Cards
        HBox statsSection = createStatsSection();

        // 3. Middle Section: Dynamic Telemetry Chart + Interactive System Switches
        HBox middleSection = createMiddleTelemetrySection();

        // 4. Lower Section: Activity Grid & Live Log Console
        GridPane bottomGrid = createBottomGrid();

        mainContainer.getChildren().addAll(header, statsSection, middleSection, bottomGrid);
        setContent(mainContainer);

        // Start Live Telemetry Updates
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
        title.setTextFill(Color.web("#0F172A")); // Deep Slate Text

        HBox statusBox = new HBox(8);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        Circle liveDot = new Circle(5, Color.web("#059669"));
        Label statusText = new Label("SYSTEM STATUS: ONLINE • NODE AP-SOUTH-1 (MUMBAI)");
        statusText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        statusText.setTextFill(Color.web("#059669"));
        statusBox.getChildren().addAll(liveDot, statusText);

        titleBox.getChildren().addAll(title, statusBox);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Action Buttons with Light Palette
        Button flushCacheBtn = new Button("⚡ Flush Cache");
        flushCacheBtn.setStyle(
            "-fx-background-color: #FFFFFF; " +
            "-fx-text-fill: #2563EB; " +
            "-fx-border-color: #CBD5E1; " +
            "-fx-border-radius: 8px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 8px 16px; " +
            "-fx-background-radius: 8px; " +
            "-fx-cursor: hand;"
        );
        flushCacheBtn.setOnAction(e -> logCommand("EXEC: Redis L2 Cache Purged successfully."));

        Button lockBtn = new Button("🔒 Lockdown");
        lockBtn.setStyle(
            "-fx-background-color: #FEF2F2; " +
            "-fx-text-fill: #DC2626; " +
            "-fx-border-color: #FCA5A5; " +
            "-fx-border-radius: 8px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 8px 16px; " +
            "-fx-background-radius: 8px; " +
            "-fx-cursor: hand;"
        );
        lockBtn.setOnAction(e -> logCommand("CRITICAL: Emergency System Lockdown Triggered by Admin!"));

        Button alertBtn = new Button("📢 Broadcast Alert");
        alertBtn.setStyle(
            "-fx-background-color: #4F46E5; " +
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

        VBox card1 = createLightStatCard("Registered Hospitals", totalHospitalsVal, "🏢 +12% this month", "#4F46E5", "#EEF2FF");
        VBox card2 = createLightStatCard("Active Doctors", activeDoctorsVal, "👨‍⚕️ +5% this month", "#059669", "#ECFDF5");
        VBox card3 = createLightStatCard("Pending Verifications", pendingVerificationsVal, "⚠️ Requires Action", "#D97706", "#FFFBEB");
        VBox card4 = createLightStatCard("Active Incidents", activeIncidentsVal, "⚡ 2 High Priority", "#DC2626", "#FEF2F2");

        HBox.setHgrow(card1, Priority.ALWAYS);
        HBox.setHgrow(card2, Priority.ALWAYS);
        HBox.setHgrow(card3, Priority.ALWAYS);
        HBox.setHgrow(card4, Priority.ALWAYS);

        statsLayout.getChildren().addAll(card1, card2, card3, card4);
        return statsLayout;
    }

    private VBox createLightStatCard(String title, Label valueLabel, String subtext, String accentHex, String softBgHex) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(18));
        card.setStyle(
            "-fx-background-color: #FFFFFF; " +
            "-fx-background-radius: 12px; " +
            "-fx-border-color: #E2E8F0; " +
            "-fx-border-radius: 12px; " +
            "-fx-border-width: 1px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(15,23,42,0.03), 8, 0, 0, 2);"
        );

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        titleLabel.setTextFill(Color.web("#64748B"));

        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        valueLabel.setTextFill(Color.web("#0F172A"));

        HBox badge = new HBox();
        badge.setAlignment(Pos.CENTER_LEFT);
        badge.setPadding(new Insets(4, 8, 4, 8));
        badge.setStyle("-fx-background-color: " + softBgHex + "; -fx-background-radius: 6px;");

        Label subLabel = new Label(subtext);
        subLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        subLabel.setTextFill(Color.web(accentHex));
        badge.getChildren().add(subLabel);

        card.getChildren().addAll(titleLabel, valueLabel, badge);
        return card;
    }

    private HBox createMiddleTelemetrySection() {
        HBox section = new HBox(20);

        // 1. Telemetry Chart Card
        VBox chartCard = createCardContainer("Live Server Telemetry (req/sec)", "Real-time load balancing on AP-SOUTH-1 cluster.");
        HBox.setHgrow(chartCard, Priority.ALWAYS);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(0, 500, 100);
        xAxis.setTickLabelFill(Color.web("#64748B"));
        yAxis.setTickLabelFill(Color.web("#64748B"));

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
        row.setStyle("-fx-background-color: #F1F5F9; -fx-background-radius: 8px;");

        Label lbl = new Label(labelText);
        lbl.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        lbl.setTextFill(Color.web("#334155"));

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
            "-fx-control-inner-background: #0F172A; " +
            "-fx-text-fill: #38BDF8; " +
            "-fx-font-family: 'Consolas', 'Courier New', monospace; " +
            "-fx-font-size: 12px;"
        );

        logCommand("System Engine Initialized. Spring Boot Backend Port 8080 Active.");
        logCommand("JWT Public Key Loaded. Redis Cache Connected.");

        consoleCard.getChildren().add(liveConsoleLog);

        // Column 2: System Health Telemetry
        VBox healthCard = createCardContainer("Service Telemetry", "Real-time microservice status.");
        VBox statusList = new VBox(12);
        statusList.setPadding(new Insets(10, 0, 0, 0));

        cpuUsageLabel = new Label("28.4 %");
        activeSessionsLabel = new Label("1,429");

        statusList.getChildren().addAll(
            createStatusItem("Database Cluster (PostgreSQL)", "Operational", "#059669"),
            createStatusItem("Auth API Service", "Operational", "#059669"),
            createStatusItem("CPU Load (4 Cores)", cpuUsageLabel.getText(), "#2563EB"),
            createStatusItem("Active Patient Sessions", activeSessionsLabel.getText(), "#D97706")
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
            "-fx-background-color: #FFFFFF; " +
            "-fx-background-radius: 12px; " +
            "-fx-border-color: #E2E8F0; " +
            "-fx-border-radius: 12px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(15,23,42,0.03), 8, 0, 0, 2);"
        );

        Label title = new Label(titleText);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#0F172A"));

        Label subtitle = new Label(subtitleText);
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        subtitle.setTextFill(Color.web("#64748B"));

        card.getChildren().addAll(title, subtitle);
        return card;
    }

    private HBox createStatusItem(String serviceName, String status, String statusColor) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 8px; -fx-border-color: #E2E8F0; -fx-border-radius: 8px;");

        Label nameLabel = new Label(serviceName);
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        nameLabel.setTextFill(Color.web("#1E293B"));

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
        TextInputDialog textInput = new TextInputDialog("Scheduled maintenance in 30 minutes.");
        textInput.setTitle("Broadcast Alert");
        textInput.setHeaderText("Enter System-Wide Notification Message:");
        textInput.showAndWait().ifPresent(msg -> {
            logCommand("BROADCAST SENT: \"" + msg + "\" to all active nodes.");
        });
    }

    private void initLiveTelemetryEngine() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            double cpuVal = 22.0 + Math.random() * 15.0;
            int loadVal = 180 + (int) (Math.random() * 140);
            int activeUsers = 1420 + (int) (Math.random() * 30);

            if (cpuUsageLabel != null) {
                cpuUsageLabel.setText(String.format("%.1f %%", cpuVal));
            }

            if (activeSessionsLabel != null) {
                activeSessionsLabel.setText(String.format("%,d", activeUsers));
            }

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

    private String getLightThemeCSS() {
        return """
            .scroll-bar:vertical, .scroll-bar:horizontal {
                -fx-background-color: #F8FAFC;
            }
            .scroll-bar:vertical .thumb, .scroll-bar:horizontal .thumb {
                -fx-background-color: #CBD5E1;
                -fx-background-radius: 4px;
            }
            .text-area {
                -fx-background-color: #0F172A;
            }
            .text-area .content {
                -fx-background-color: #0F172A;
            }
            .chart-line-symbol {
                -fx-background-color: #2563EB, #FFFFFF;
            }
            .default-color0.chart-series-line {
                -fx-stroke: #2563EB;
                -fx-stroke-width: 2.5px;
            }
            """;
    }
}