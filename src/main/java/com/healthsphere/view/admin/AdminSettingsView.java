package com.healthsphere.view.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminSettingsView {

    private final Stage stage;
    private Label statusBanner;
    private Label apiStatusLabel;
    private TableView<AuditLogModel> auditTable;
    private ObservableList<AuditLogModel> auditLogData;
    private ProgressBar storageProgressBar;
    private Label storageUsageLabel;

    public AdminSettingsView(Stage stage) {
        this.stage = stage;
    }

    public Node getView() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #F8FAFC;");

        // 1. Header with Title & Action Buttons
        HBox headerBox = createHeader();

        // 2. Global Status Notification Banner
        statusBanner = createStatusBanner();

        // 3. Main Settings Grid (Left: AI & Security Config, Right: System Health Chart)
        GridPane topGrid = new GridPane();
        topGrid.setHgap(20);
        topGrid.setVgap(20);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(55);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(45);
        topGrid.getColumnConstraints().addAll(col1, col2);

        VBox leftColumn = new VBox(20);
        VBox securityCard = createSecurityConfigCard();
        VBox aiCard = createAiConfigCard();
        leftColumn.getChildren().addAll(securityCard, aiCard);

        VBox rightColumn = new VBox(20);
        VBox chartCard = createSystemMetricsChartCard();
        VBox backupCard = createBackupStorageCard();
        rightColumn.getChildren().addAll(chartCard, backupCard);

        topGrid.add(leftColumn, 0, 0);
        topGrid.add(rightColumn, 1, 0);

        // 4. Audit Trail Table Section
        VBox auditLogCard = createAuditLogCard();

        root.getChildren().addAll(headerBox, statusBanner, topGrid, auditLogCard);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #F8FAFC; -fx-background: #F8FAFC; -fx-border-color: transparent;");
        return scroll;
    }

    // ------------------------------------------------------------------------
    // UI BUILDERS
    // ------------------------------------------------------------------------

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("System Settings & Neural Configurations");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#0F172A"));

        Label subTitle = new Label("Global medical AI thresholds, security rules, storage metrics, and audit logs.");
        subTitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        subTitle.setTextFill(Color.web("#64748B"));

        titleBox.getChildren().addAll(title, subTitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button exportLogsBtn = new Button("Export Audit Logs");
        exportLogsBtn.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #334155; -fx-border-color: #CBD5E1; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 8 16; -fx-font-weight: bold; -fx-cursor: hand;");
        exportLogsBtn.setOnAction(e -> showInfo("Export Complete", "System audit logs exported to CSV format successfully."));

        Button saveAllBtn = new Button("Save Configuration Changes");
        saveAllBtn.setStyle("-fx-background-color: #4F46E5; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 8px; -fx-cursor: hand;");
        saveAllBtn.setOnAction(e -> {
            updateStatus("Configuration saved successfully at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), "#059669", "#ECFDF5");
            addAuditLog("GLOBAL_CONFIG_UPDATE", "Admin updated neural & security thresholds", "Prajwal Patil");
        });

        HBox buttonGroup = new HBox(12, exportLogsBtn, saveAllBtn);
        buttonGroup.setAlignment(Pos.CENTER_RIGHT);

        header.getChildren().addAll(titleBox, spacer, buttonGroup);
        return header;
    }

    private Label createStatusBanner() {
        Label banner = new Label("Ready to update system settings. All core services operating normally.");
        banner.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        banner.setMaxWidth(Double.MAX_VALUE);
        banner.setPadding(new Insets(10, 16, 10, 16));
        banner.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #334155; -fx-background-radius: 8px; -fx-border-color: #E2E8F0; -fx-border-radius: 8px;");
        return banner;
    }

    private VBox createSecurityConfigCard() {
        VBox card = createBaseCard("Security & Authentication Policies");

        CheckBox cb1 = new CheckBox("Enable Automated AI Medical Verification Pre-screening");
        cb1.setSelected(true);
        cb1.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        cb1.setTextFill(Color.web("#0F172A"));

        CheckBox cb2 = new CheckBox("Require Multi-Factor Authentication (MFA) for Super Admins");
        cb2.setSelected(true);
        cb2.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        cb2.setTextFill(Color.web("#0F172A"));

        CheckBox cb3 = new CheckBox("Strict Session Timeout (Auto Logout after 15 mins inactive)");
        cb3.setSelected(false);
        cb3.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        cb3.setTextFill(Color.web("#0F172A"));

        HBox loginAttemptsBox = new HBox(12);
        loginAttemptsBox.setAlignment(Pos.CENTER_LEFT);
        Label attemptsLabel = new Label("Max Failed Login Attempts:");
        attemptsLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 13));
        attemptsLabel.setTextFill(Color.web("#334155"));

        Spinner<Integer> attemptsSpinner = new Spinner<>(3, 10, 5);
        attemptsSpinner.setPrefWidth(90);
        attemptsSpinner.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #CBD5E1; -fx-border-radius: 6px;");

        loginAttemptsBox.getChildren().addAll(attemptsLabel, attemptsSpinner);

        card.getChildren().addAll(cb1, cb2, cb3, loginAttemptsBox);
        return card;
    }

    private VBox createAiConfigCard() {
        VBox card = createBaseCard("AI & Neural Engine Endpoint Configurations");

        // Model Selector
        HBox modelBox = new HBox(12);
        modelBox.setAlignment(Pos.CENTER_LEFT);
        Label modelLabel = new Label("AI Diagnostics Model:");
        modelLabel.setPrefWidth(140);
        modelLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 13));
        modelLabel.setTextFill(Color.web("#334155"));

        ComboBox<String> modelCombo = new ComboBox<>(FXCollections.observableArrayList(
                "HealthSphere MedGemma-7B (Production)",
                "HealthSphere Clinical-Vision 3.2",
                "HealthSphere BioLLM-13B (Experimental)"
        ));
        modelCombo.setValue("HealthSphere MedGemma-7B (Production)");
        modelCombo.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #CBD5E1; -fx-border-radius: 6px; -fx-text-fill: #0F172A;");
        modelBox.getChildren().addAll(modelLabel, modelCombo);

        // API Endpoint Field
        VBox apiBox = new VBox(6);
        Label apiLabel = new Label("Active AI Engine Endpoint:");
        apiLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 13));
        apiLabel.setTextFill(Color.web("#334155"));

        HBox apiInputGroup = new HBox(10);
        TextField apiField = new TextField("https://api.healthsphere.ai/v1/diagnose");
        HBox.setHgrow(apiField, Priority.ALWAYS);
        apiField.setStyle("-fx-background-color: #F8FAFC; -fx-text-fill: #0F172A; -fx-background-radius: 6px; -fx-padding: 8 12; -fx-border-color: #CBD5E1; -fx-border-radius: 6px;");

        Button testConnBtn = new Button("Test Endpoint");
        testConnBtn.setStyle("-fx-background-color: #E0E7FF; -fx-text-fill: #3730A3; -fx-font-weight: bold; -fx-padding: 8 14; -fx-background-radius: 6px; -fx-cursor: hand;");

        apiStatusLabel = new Label("Status: Connected (24ms)");
        apiStatusLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        apiStatusLabel.setTextFill(Color.web("#059669"));

        testConnBtn.setOnAction(e -> {
            apiStatusLabel.setText("Status: Testing...");
            apiStatusLabel.setTextFill(Color.web("#D97706"));
            // Mock latency response
            int ms = (int)(Math.random() * 20 + 15);
            apiStatusLabel.setText("Status: Operational (" + ms + "ms)");
            apiStatusLabel.setTextFill(Color.web("#059669"));
            updateStatus("AI Endpoint responded in " + ms + "ms with HTTP 200 OK.", "#059669", "#ECFDF5");
        });

        apiInputGroup.getChildren().addAll(apiField, testConnBtn);
        apiBox.getChildren().addAll(apiLabel, apiInputGroup, apiStatusLabel);

        // Confidence Slider
        VBox sliderBox = new VBox(6);
        Label confidenceLabel = new Label("Diagnostic Confidence Threshold: 85%");
        confidenceLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 13));
        confidenceLabel.setTextFill(Color.web("#334155"));

        Slider confidenceSlider = new Slider(50, 99, 85);
        confidenceSlider.setMajorTickUnit(10);
        confidenceSlider.setMinorTickCount(5);
        confidenceSlider.setShowTickLabels(true);
        confidenceSlider.setShowTickMarks(true);
        confidenceSlider.valueProperty().addListener((obs, oldVal, newVal) ->
            confidenceLabel.setText(String.format("Diagnostic Confidence Threshold: %d%%", newVal.intValue()))
        );

        sliderBox.getChildren().addAll(confidenceLabel, confidenceSlider);

        card.getChildren().addAll(modelBox, apiBox, sliderBox);
        return card;
    }

    private VBox createSystemMetricsChartCard() {
        VBox card = createBaseCard("Real-time AI Engine Latency (ms)");

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(0, 100, 20);
        xAxis.setLabel("Time");
        yAxis.setLabel("Latency (ms)");

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setPrefHeight(180);
        chart.setLegendVisible(false);
        chart.setAnimated(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("11:35", 22));
        series.getData().add(new XYChart.Data<>("11:36", 28));
        series.getData().add(new XYChart.Data<>("11:37", 25));
        series.getData().add(new XYChart.Data<>("11:38", 42));
        series.getData().add(new XYChart.Data<>("11:39", 30));
        series.getData().add(new XYChart.Data<>("11:40", 24));
        series.getData().add(new XYChart.Data<>("11:41", 26));

        chart.getData().add(series);

        card.getChildren().add(chart);
        return card;
    }

    private VBox createBackupStorageCard() {
        VBox card = createBaseCard("Automated System Backups & Cloud Storage");

        HBox storageInfo = new HBox();
        storageInfo.setAlignment(Pos.CENTER_LEFT);

        Label storageTitle = new Label("Encrypted Database Storage:");
        storageTitle.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 13));
        storageTitle.setTextFill(Color.web("#334155"));

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        storageUsageLabel = new Label("142.8 GB / 500 GB (28% used)");
        storageUsageLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        storageUsageLabel.setTextFill(Color.web("#0F172A"));

        storageInfo.getChildren().addAll(storageTitle, sp, storageUsageLabel);

        storageProgressBar = new ProgressBar(0.285);
        storageProgressBar.setMaxWidth(Double.MAX_VALUE);
        storageProgressBar.setStyle("-fx-accent: #4F46E5;");

        HBox actionsBox = new HBox(12);
        actionsBox.setAlignment(Pos.CENTER_LEFT);

        CheckBox autoBackupCb = new CheckBox("Daily Automated Cloud Backup (02:00 UTC)");
        autoBackupCb.setSelected(true);
        autoBackupCb.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 12));
        autoBackupCb.setTextFill(Color.web("#334155"));

        Region sp2 = new Region();
        HBox.setHgrow(sp2, Priority.ALWAYS);

        Button runBackupBtn = new Button("Run Backup Now");
        runBackupBtn.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #15803D; -fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 6px; -fx-cursor: hand;");
        runBackupBtn.setOnAction(e -> {
            storageProgressBar.setProgress(0.29);
            storageUsageLabel.setText("143.1 GB / 500 GB (29% used)");
            updateStatus("Manual snapshot backup created successfully.", "#15803D", "#DCFCE7");
            addAuditLog("MANUAL_BACKUP", "Admin triggered manual snapshot", "Prajwal Patil");
        });

        actionsBox.getChildren().addAll(autoBackupCb, sp2, runBackupBtn);

        card.getChildren().addAll(storageInfo, storageProgressBar, actionsBox);
        return card;
    }

    private VBox createAuditLogCard() {
        VBox card = createBaseCard("Recent System Settings Audit Trail");

        auditTable = new TableView<>();
        auditTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        auditTable.setPrefHeight(180);
        auditTable.setStyle("-fx-background-color: transparent;");

        TableColumn<AuditLogModel, String> timeCol = new TableColumn<>("Timestamp");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        timeCol.setPrefWidth(140);

        TableColumn<AuditLogModel, String> actionCol = new TableColumn<>("Action Type");
        actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));
        actionCol.setPrefWidth(160);

        TableColumn<AuditLogModel, String> detailsCol = new TableColumn<>("Description / Details");
        detailsCol.setCellValueFactory(new PropertyValueFactory<>("details"));
        detailsCol.setPrefWidth(340);

        TableColumn<AuditLogModel, String> userCol = new TableColumn<>("Performed By");
        userCol.setCellValueFactory(new PropertyValueFactory<>("performedBy"));
        userCol.setPrefWidth(140);

        auditTable.getColumns().addAll(timeCol, actionCol, detailsCol, userCol);

        // Load Initial Audit Log Sample Data
        auditLogData = FXCollections.observableArrayList(
                new AuditLogModel(LocalDateTime.now().minusMinutes(12).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), "SECURITY_POLICY", "Updated Super Admin MFA rules", "Prajwal Patil"),
                new AuditLogModel(LocalDateTime.now().minusHours(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), "API_ENDPOINT", "Changed diagnostic model to MedGemma-7B", "Prajwal Patil"),
                new AuditLogModel(LocalDateTime.now().minusHours(18).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), "AUTO_BACKUP", "System executed daily cloud snapshot", "SYSTEM_CRON"),
                new AuditLogModel(LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), "THRESHOLD_UPDATE", "Confidence threshold adjusted to 85%", "Dr. Rajesh Sharma")
        );

        auditTable.setItems(auditLogData);
        card.getChildren().add(auditTable);
        return card;
    }

    // ------------------------------------------------------------------------
    // HELPER METHODS
    // ------------------------------------------------------------------------

    private VBox createBaseCard(String titleText) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: #FFFFFF; " +
                "-fx-background-radius: 12px; " +
                "-fx-border-color: #E2E8F0; " +
                "-fx-border-radius: 12px;"
        );

        Label title = new Label(titleText);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        title.setTextFill(Color.web("#0F172A"));

        card.getChildren().add(title);
        return card;
    }

    private void updateStatus(String message, String textColorHex, String bgColorHex) {
        statusBanner.setText(message);
        statusBanner.setStyle(
                "-fx-background-color: " + bgColorHex + "; " +
                "-fx-text-fill: " + textColorHex + "; " +
                "-fx-background-radius: 8px; " +
                "-fx-border-color: " + textColorHex + "44; " +
                "-fx-border-radius: 8px;"
        );
    }

    private void addAuditLog(String action, String details, String user) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        auditLogData.add(0, new AuditLogModel(now, action, details, user));
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // ------------------------------------------------------------------------
    // AUDIT LOG MODEL CLASS
    // ------------------------------------------------------------------------

    public static class AuditLogModel {
        private final String timestamp;
        private final String action;
        private final String details;
        private final String performedBy;

        public AuditLogModel(String timestamp, String action, String details, String performedBy) {
            this.timestamp = timestamp;
            this.action = action;
            this.details = details;
            this.performedBy = performedBy;
        }

        public String getTimestamp() { return timestamp; }
        public String getAction() { return action; }
        public String getDetails() { return details; }
        public String getPerformedBy() { return performedBy; }
    }
}