package com.healthsphere.view.admin;

import com.healthsphere.controller.admin.AdminDashboardController;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AdminDashboardView {

    // ============================================================
    // STAGE / ROOT
    // ============================================================

    private Stage primaryStage;

    private final ScrollPane rootPane;

    // ============================================================
    // CONTROLLER
    // ============================================================

    private final AdminDashboardController
            adminDashboardController;

    // ============================================================
    // DYNAMIC KPI LABELS
    // ============================================================

    private Label totalHospitalsVal;

    private Label activeDoctorsVal;

    private Label pendingVerificationsVal;

    private Label activeIncidentsVal;

    // ============================================================
    // REAL-TIME TELEMETRY
    // ============================================================

    private Label cpuUsageLabel;

    private Label activeSessionsLabel;

    private TextArea liveConsoleLog;

    private LineChart<String, Number> loadChart;

    private XYChart.Series<String, Number> loadSeries;

    // ============================================================
    // TELEMETRY TIMELINE
    // ============================================================

    private Timeline telemetryTimeline;

    // ============================================================
    // DEFAULT CONSTRUCTOR
    // ============================================================

    public AdminDashboardView() {

        this(null);
    }

    // ============================================================
    // CONSTRUCTOR WITH STAGE
    // ============================================================

    public AdminDashboardView(Stage stage) {

        this.primaryStage = stage;

        this.adminDashboardController =
                new AdminDashboardController();

        this.rootPane =
                new ScrollPane();

        rootPane.setFitToWidth(true);

        rootPane.setStyle(
                "-fx-background-color: #F8FAFC; " +
                "-fx-background: #F8FAFC;"
        );

        // ========================================================
        // LIGHT THEME
        // ========================================================

        rootPane.getStylesheets().add(
                "data:text/css,"
                        + getLightThemeCSS()
        );

        // ========================================================
        // MAIN CONTAINER
        // ========================================================

        VBox mainContainer =
                new VBox(25);

        mainContainer.setPadding(
                new Insets(30)
        );

        mainContainer.setStyle(
                "-fx-background-color: #F8FAFC;"
        );

        // ========================================================
        // COMMAND HEADER
        // ========================================================

        HBox header =
                createCommandHeader();

        // ========================================================
        // DASHBOARD BANNER
        // ========================================================

        StackPane bannerSection =
                createDashboardBanner();

        // ========================================================
        // KPI STATISTICS
        // ========================================================

        HBox statsSection =
                createStatsSection();

        // ========================================================
        // TELEMETRY SECTION
        // ========================================================

        HBox middleSection =
                createMiddleTelemetrySection();

        // ========================================================
        // BOTTOM GRID
        // ========================================================

        GridPane bottomGrid =
                createBottomGrid();

        // ========================================================
        // ADD EVERYTHING
        // ========================================================

        mainContainer.getChildren().addAll(
                header,
                bannerSection,
                statsSection,
                middleSection,
                bottomGrid
        );

        rootPane.setContent(
                mainContainer
        );

        // ========================================================
        // START TELEMETRY
        // ========================================================

        initLiveTelemetryEngine();

        // ========================================================
        // LOAD FIRESTORE DATA
        // ========================================================

        loadDashboardData();
    }

    // ============================================================
    // PUBLIC VIEW
    // ============================================================

    public Parent getView() {

        return rootPane;
    }

    // ============================================================
    // SCENE
    // ============================================================

    public Scene getScene() {

        return new Scene(rootPane);
    }

    // ============================================================
    // COMMAND HEADER
    // ============================================================

    private HBox createCommandHeader() {

        HBox header =
                new HBox(20);

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        // ========================================================
        // TITLE
        // ========================================================

        VBox titleBox =
                new VBox(4);

        Label title =
                new Label(
                        "HealthSphere Command Center"
                );

        title.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        26
                )
        );

        title.setTextFill(
                Color.web("#0F172A")
        );

        // ========================================================
        // SYSTEM STATUS
        // ========================================================

        HBox statusBox =
                new HBox(8);

        statusBox.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle liveDot =
                new Circle(
                        5,
                        Color.web("#059669")
                );

        Label statusText =
                new Label(
                        "SYSTEM STATUS: ONLINE • "
                                + "NODE AP-SOUTH-1 (MUMBAI)"
                );

        statusText.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        12
                )
        );

        statusText.setTextFill(
                Color.web("#059669")
        );

        statusBox.getChildren().addAll(
                liveDot,
                statusText
        );

        titleBox.getChildren().addAll(
                title,
                statusBox
        );

        // ========================================================
        // SPACER
        // ========================================================

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        // ========================================================
        // FLUSH CACHE
        // ========================================================

        Button flushCacheBtn =
                new Button(
                        "⚡ Flush Cache"
                );

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

        flushCacheBtn.setOnAction(
                e -> logCommand(
                        "EXEC: Redis L2 Cache Purged successfully."
                )
        );

        // ========================================================
        // LOCKDOWN
        // ========================================================

        Button lockBtn =
                new Button(
                        "🔒 Lockdown"
                );

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

        lockBtn.setOnAction(
                e -> logCommand(
                        "CRITICAL: Emergency System "
                                + "Lockdown Triggered by Admin!"
                )
        );

        // ========================================================
        // BROADCAST
        // ========================================================

        Button alertBtn =
                new Button(
                        "📢 Broadcast Alert"
                );

        alertBtn.setStyle(
                "-fx-background-color: #4F46E5; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 9px 16px; " +
                "-fx-background-radius: 8px; " +
                "-fx-cursor: hand;"
        );

        alertBtn.setOnAction(
                e -> showBroadcastDialog()
        );

        header.getChildren().addAll(
                titleBox,
                spacer,
                flushCacheBtn,
                lockBtn,
                alertBtn
        );

        return header;
    }

    // ============================================================
    // DASHBOARD BANNER
    // ============================================================

    private StackPane createDashboardBanner() {

        StackPane bannerPane =
                new StackPane();

        bannerPane.setPrefHeight(160);

        bannerPane.setMaxWidth(
                Double.MAX_VALUE
        );

        String imageUrl = "";

        try {

            URL resource =
                    getClass().getResource(
                            "/images/dashboard_banner.png"
                    );

            if (resource != null) {

                imageUrl =
                        resource.toExternalForm();

            } else {

                logCommand(
                        "WARNING: Banner image not found at "
                                + "/images/dashboard_banner.png"
                );
            }

        } catch (Exception e) {

            logCommand(
                    "ERROR: Failed to load banner image."
            );
        }

        if (!imageUrl.isEmpty()) {

            bannerPane.setStyle(
                    "-fx-background-image: url('"
                            + imageUrl
                            + "'); "
                            + "-fx-background-size: cover; "
                            + "-fx-background-repeat: no-repeat; "
                            + "-fx-background-position: center; "
                            + "-fx-background-radius: 12px; "
                            + "-fx-border-color: #CBD5E1; "
                            + "-fx-border-radius: 12px; "
                            + "-fx-effect: dropshadow("
                            + "three-pass-box, "
                            + "rgba(15,23,42,0.05), "
                            + "10, 0, 0, 4);"
            );

        } else {

            bannerPane.setStyle(
                    "-fx-background-color: #0F172A; "
                            + "-fx-background-radius: 12px; "
                            + "-fx-border-color: #334155; "
                            + "-fx-border-radius: 12px;"
            );
        }

        return bannerPane;
    }

    // ============================================================
    // KPI SECTION
    // ============================================================

    private HBox createStatsSection() {

        HBox statsLayout =
                new HBox(20);

        statsLayout.setAlignment(
                Pos.CENTER
        );

        // ========================================================
        // INITIAL VALUES
        // ========================================================

        totalHospitalsVal =
                new Label("Loading...");

        activeDoctorsVal =
                new Label("Loading...");

        pendingVerificationsVal =
                new Label("Loading...");

        activeIncidentsVal =
                new Label("05");

        // ========================================================
        // CARDS
        // ========================================================

        VBox card1 =
                createLightStatCard(
                        "Registered Hospitals",
                        totalHospitalsVal,
                        "🏢 Live from Firestore",
                        "#4F46E5",
                        "#EEF2FF"
                );

        VBox card2 =
                createLightStatCard(
                        "Active Doctors",
                        activeDoctorsVal,
                        "👨‍⚕️ Live from Firestore",
                        "#059669",
                        "#ECFDF5"
                );

        VBox card3 =
                createLightStatCard(
                        "Pending Verifications",
                        pendingVerificationsVal,
                        "⚠️ Requires Action",
                        "#D97706",
                        "#FFFBEB"
                );

        VBox card4 =
                createLightStatCard(
                        "Active Incidents",
                        activeIncidentsVal,
                        "⚡ 2 High Priority",
                        "#DC2626",
                        "#FEF2F2"
                );

        HBox.setHgrow(
                card1,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                card2,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                card3,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                card4,
                Priority.ALWAYS
        );

        statsLayout.getChildren().addAll(
                card1,
                card2,
                card3,
                card4
        );

        return statsLayout;
    }

    // ============================================================
    // STAT CARD
    // ============================================================

    private VBox createLightStatCard(
            String title,
            Label valueLabel,
            String subtext,
            String accentHex,
            String softBgHex) {

        VBox card =
                new VBox(8);

        card.setPadding(
                new Insets(18)
        );

        card.setStyle(
                "-fx-background-color: #FFFFFF; "
                        + "-fx-background-radius: 12px; "
                        + "-fx-border-color: #E2E8F0; "
                        + "-fx-border-radius: 12px; "
                        + "-fx-border-width: 1px; "
                        + "-fx-effect: dropshadow("
                        + "three-pass-box, "
                        + "rgba(15,23,42,0.03), "
                        + "8, 0, 0, 2);"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        12
                )
        );

        titleLabel.setTextFill(
                Color.web("#64748B")
        );

        valueLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        28
                )
        );

        valueLabel.setTextFill(
                Color.web("#0F172A")
        );

        HBox badge =
                new HBox();

        badge.setAlignment(
                Pos.CENTER_LEFT
        );

        badge.setPadding(
                new Insets(
                        4,
                        8,
                        4,
                        8
                )
        );

        badge.setStyle(
                "-fx-background-color: "
                        + softBgHex
                        + "; "
                        + "-fx-background-radius: 6px;"
        );

        Label subLabel =
                new Label(subtext);

        subLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        11
                )
        );

        subLabel.setTextFill(
                Color.web(accentHex)
        );

        badge.getChildren().add(
                subLabel
        );

        card.getChildren().addAll(
                titleLabel,
                valueLabel,
                badge
        );

        return card;
    }

    // ============================================================
    // MIDDLE TELEMETRY SECTION
    // ============================================================

    private HBox createMiddleTelemetrySection() {

        HBox section =
                new HBox(20);

        // ========================================================
        // CHART CARD
        // ========================================================

        VBox chartCard =
                createCardContainer(
                        "Live Server Telemetry (req/sec)",
                        "Real-time load balancing on "
                                + "AP-SOUTH-1 cluster."
                );

        HBox.setHgrow(
                chartCard,
                Priority.ALWAYS
        );

        CategoryAxis xAxis =
                new CategoryAxis();

        NumberAxis yAxis =
                new NumberAxis(
                        0,
                        500,
                        100
                );

        xAxis.setTickLabelFill(
                Color.web("#64748B")
        );

        yAxis.setTickLabelFill(
                Color.web("#64748B")
        );

        loadChart =
                new LineChart<>(
                        xAxis,
                        yAxis
                );

        loadChart.setPrefHeight(
                210
        );

        loadChart.setLegendVisible(
                false
        );

        loadChart.setAnimated(
                false
        );

        loadSeries =
                new XYChart.Series<>();

        loadChart.getData().add(
                loadSeries
        );

        chartCard.getChildren().add(
                loadChart
        );

        // ========================================================
        // SWITCH PANEL
        // ========================================================

        VBox switchPanel =
                createCardContainer(
                        "System Command Switches",
                        "Toggle microservices and platform state."
                );

        switchPanel.setMinWidth(
                320
        );

        VBox switchesBox =
                new VBox(10);

        switchesBox.setPadding(
                new Insets(
                        10,
                        0,
                        0,
                        0
                )
        );

        switchesBox.getChildren().addAll(

                createToggleRow(
                        "Maintenance Mode",
                        false
                ),

                createToggleRow(
                        "AI Fraud Engine",
                        true
                ),

                createToggleRow(
                        "Payment Webhooks",
                        true
                ),

                createToggleRow(
                        "Doctor Live API",
                        true
                )
        );

        switchPanel.getChildren().add(
                switchesBox
        );

        section.getChildren().addAll(
                chartCard,
                switchPanel
        );

        return section;
    }

    // ============================================================
    // TOGGLE ROW
    // ============================================================

    private HBox createToggleRow(
            String labelText,
            boolean initialValue) {

        HBox row =
                new HBox(10);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(
                        8,
                        12,
                        8,
                        12
                )
        );

        row.setStyle(
                "-fx-background-color: #F1F5F9; "
                        + "-fx-background-radius: 8px;"
        );

        Label lbl =
                new Label(labelText);

        lbl.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.SEMI_BOLD,
                        12
                )
        );

        lbl.setTextFill(
                Color.web("#334155")
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        CheckBox toggle =
                new CheckBox();

        toggle.setSelected(
                initialValue
        );

        toggle.setOnAction(
                e -> {

                    String state =
                            toggle.isSelected()
                                    ? "ENABLED"
                                    : "DISABLED";

                    logCommand(
                            "CONFIG CHANGE: "
                                    + labelText
                                    + " set to "
                                    + state
                    );
                }
        );

        row.getChildren().addAll(
                lbl,
                spacer,
                toggle
        );

        return row;
    }

    // ============================================================
    // BOTTOM GRID
    // ============================================================

    private GridPane createBottomGrid() {

        GridPane grid =
                new GridPane();

        grid.setHgap(20);

        grid.setVgap(20);

        // ========================================================
        // LIVE CONSOLE
        // ========================================================

        VBox consoleCard =
                createCardContainer(
                        "Live Terminal Log Stream",
                        "Automated system events, auth triggers, "
                                + "and API logs."
                );

        liveConsoleLog =
                new TextArea();

        liveConsoleLog.setEditable(
                false
        );

        liveConsoleLog.setPrefHeight(
                160
        );

        liveConsoleLog.setStyle(
                "-fx-control-inner-background: #0F172A; "
                        + "-fx-text-fill: #38BDF8; "
                        + "-fx-font-family: 'Consolas', "
                        + "'Courier New', monospace; "
                        + "-fx-font-size: 12px;"
        );

        consoleCard.getChildren().add(
                liveConsoleLog
        );

        logCommand(
                "System Engine Initialized."
        );

        logCommand(
                "HealthSphere Admin Dashboard Loaded."
        );

        logCommand(
                "Firestore integration initialized."
        );

        // ========================================================
        // HEALTH CARD
        // ========================================================

        VBox healthCard =
                createCardContainer(
                        "Service Telemetry",
                        "Real-time microservice status."
                );

        VBox statusList =
                new VBox(12);

        statusList.setPadding(
                new Insets(
                        10,
                        0,
                        0,
                        0
                )
        );

        cpuUsageLabel =
                new Label("28.4 %");

        activeSessionsLabel =
                new Label("1,429");

        statusList.getChildren().addAll(

                createStatusItem(
                        "Firestore Database",
                        new Label("Operational"),
                        "#059669"
                ),

                createStatusItem(
                        "Auth API Service",
                        new Label("Operational"),
                        "#059669"
                ),

                createStatusItem(
                        "CPU Load (4 Cores)",
                        cpuUsageLabel,
                        "#2563EB"
                ),

                createStatusItem(
                        "Active Patient Sessions",
                        activeSessionsLabel,
                        "#D97706"
                )
        );

        healthCard.getChildren().add(
                statusList
        );

        // ========================================================
        // GRID COLUMNS
        // ========================================================

        ColumnConstraints col1 =
                new ColumnConstraints();

        col1.setPercentWidth(60);

        ColumnConstraints col2 =
                new ColumnConstraints();

        col2.setPercentWidth(40);

        grid.getColumnConstraints().addAll(
                col1,
                col2
        );

        grid.add(
                consoleCard,
                0,
                0
        );

        grid.add(
                healthCard,
                1,
                0
        );

        return grid;
    }

    // ============================================================
    // CARD CONTAINER
    // ============================================================

    private VBox createCardContainer(
            String titleText,
            String subtitleText) {

        VBox card =
                new VBox(8);

        card.setPadding(
                new Insets(18)
        );

        card.setStyle(
                "-fx-background-color: #FFFFFF; "
                        + "-fx-background-radius: 12px; "
                        + "-fx-border-color: #E2E8F0; "
                        + "-fx-border-radius: 12px; "
                        + "-fx-effect: dropshadow("
                        + "three-pass-box, "
                        + "rgba(15,23,42,0.03), "
                        + "8, 0, 0, 2);"
        );

        Label title =
                new Label(titleText);

        title.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        16
                )
        );

        title.setTextFill(
                Color.web("#0F172A")
        );

        Label subtitle =
                new Label(subtitleText);

        subtitle.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.NORMAL,
                        12
                )
        );

        subtitle.setTextFill(
                Color.web("#64748B")
        );

        card.getChildren().addAll(
                title,
                subtitle
        );

        return card;
    }

    // ============================================================
    // STATUS ITEM
    // ============================================================

    private HBox createStatusItem(
            String serviceName,
            Label statusLabel,
            String statusColor) {

        HBox row =
                new HBox();

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(10)
        );

        row.setStyle(
                "-fx-background-color: #F8FAFC; "
                        + "-fx-background-radius: 8px; "
                        + "-fx-border-color: #E2E8F0; "
                        + "-fx-border-radius: 8px;"
        );

        Label nameLabel =
                new Label(serviceName);

        nameLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.SEMI_BOLD,
                        12
                )
        );

        nameLabel.setTextFill(
                Color.web("#1E293B")
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        statusLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        12
                )
        );

        statusLabel.setTextFill(
                Color.web(statusColor)
        );

        row.getChildren().addAll(
                nameLabel,
                spacer,
                statusLabel
        );

        return row;
    }

    // ============================================================
    // FIRESTORE DASHBOARD DATA
    // ============================================================

    private void loadDashboardData() {

        if (totalHospitalsVal == null
                || activeDoctorsVal == null
                || pendingVerificationsVal == null) {

            return;
        }

        totalHospitalsVal.setText(
                "Loading..."
        );

        activeDoctorsVal.setText(
                "Loading..."
        );

        pendingVerificationsVal.setText(
                "Loading..."
        );

        logCommand(
                "Firestore: Loading dashboard statistics..."
        );

        adminDashboardController
                .loadDashboardStats()
                .thenAccept(stats -> {

                    javafx.application.Platform
                            .runLater(() -> {

                                totalHospitalsVal.setText(
                                        String.valueOf(
                                                stats
                                                        .getRegisteredHospitals()
                                        )
                                );

                                activeDoctorsVal.setText(
                                        String.valueOf(
                                                stats
                                                        .getActiveDoctors()
                                        )
                                );

                                pendingVerificationsVal.setText(
                                        String.valueOf(
                                                stats
                                                        .getPendingVerifications()
                                        )
                                );

                                logCommand(
                                        "Firestore: Dashboard "
                                                + "statistics loaded successfully."
                                );
                            });

                })
                .exceptionally(error -> {

                    javafx.application.Platform
                            .runLater(() -> {

                                totalHospitalsVal.setText(
                                        "N/A"
                                );

                                activeDoctorsVal.setText(
                                        "N/A"
                                );

                                pendingVerificationsVal.setText(
                                        "N/A"
                                );

                                Throwable cause =
                                        error.getCause() != null
                                                ? error.getCause()
                                                : error;

                                String message =
                                        cause.getMessage() != null
                                                ? cause.getMessage()
                                                : "Unknown Firestore error";

                                logCommand(
                                        "ERROR: Failed to load "
                                                + "dashboard statistics. "
                                                + message
                                );
                            });

                    return null;
                });
    }

    // ============================================================
    // LOG COMMAND
    // ============================================================

    private void logCommand(
            String message) {

        if (liveConsoleLog != null) {

            String timestamp =
                    LocalTime.now().format(
                            DateTimeFormatter.ofPattern(
                                    "HH:mm:ss"
                            )
                    );

            liveConsoleLog.appendText(
                    "["
                            + timestamp
                            + "] "
                            + message
                            + "\n"
            );
        }
    }

    // ============================================================
    // BROADCAST DIALOG
    // ============================================================

    private void showBroadcastDialog() {

        TextInputDialog textInput =
                new TextInputDialog(
                        "Scheduled maintenance in 30 minutes."
                );

        textInput.setTitle(
                "Broadcast Alert"
        );

        textInput.setHeaderText(
                "Enter System-Wide Notification Message:"
        );

        textInput.showAndWait()
                .ifPresent(
                        msg -> {

                            if (msg == null
                                    || msg.isBlank()) {

                                logCommand(
                                        "WARNING: Empty broadcast message."
                                );

                                return;
                            }

                            logCommand(
                                    "BROADCAST SENT: \""
                                            + msg
                                            + "\" to all active nodes."
                            );
                        }
                );
    }

    // ============================================================
    // LIVE TELEMETRY ENGINE
    // ============================================================

    private void initLiveTelemetryEngine() {

        telemetryTimeline =
                new Timeline(
                        new KeyFrame(
                                Duration.seconds(2),
                                e -> {

                                    double cpuVal =
                                            22.0
                                                    + Math.random()
                                                    * 15.0;

                                    int loadVal =
                                            180
                                                    + (int)
                                                    (
                                                            Math.random()
                                                                    * 140
                                                    );

                                    int activeUsers =
                                            1420
                                                    + (int)
                                                    (
                                                            Math.random()
                                                                    * 30
                                                    );

                                    // ====================================
                                    // CPU
                                    // ====================================

                                    if (cpuUsageLabel != null) {

                                        cpuUsageLabel.setText(
                                                String.format(
                                                        "%.1f %%",
                                                        cpuVal
                                                )
                                        );
                                    }

                                    // ====================================
                                    // ACTIVE USERS
                                    // ====================================

                                    if (activeSessionsLabel != null) {

                                        activeSessionsLabel.setText(
                                                String.format(
                                                        "%,d",
                                                        activeUsers
                                                )
                                        );
                                    }

                                    // ====================================
                                    // CHART
                                    // ====================================

                                    String currentTime =
                                            LocalTime.now()
                                                    .format(
                                                            DateTimeFormatter
                                                                    .ofPattern(
                                                                            "HH:mm:ss"
                                                                    )
                                                    );

                                    if (loadSeries != null) {

                                        loadSeries
                                                .getData()
                                                .add(
                                                        new XYChart.Data<>(
                                                                currentTime,
                                                                loadVal
                                                        )
                                                );

                                        if (loadSeries
                                                .getData()
                                                .size() > 8) {

                                            loadSeries
                                                    .getData()
                                                    .remove(0);
                                        }
                                    }
                                }
                        )
                );

        telemetryTimeline.setCycleCount(
                Timeline.INDEFINITE
        );

        telemetryTimeline.play();
    }

    // ============================================================
    // STOP TELEMETRY
    // ============================================================

    public void stopTelemetry() {

        if (telemetryTimeline != null) {

            telemetryTimeline.stop();
        }
    }

    // ============================================================
    // LIGHT THEME CSS
    // ============================================================

    private String getLightThemeCSS() {

        return """
            .scroll-bar:vertical,
            .scroll-bar:horizontal {
                -fx-background-color: #F8FAFC;
            }

            .scroll-bar:vertical .thumb,
            .scroll-bar:horizontal .thumb {
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