package com.healthsphere.view.admin;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportsAnalyticsView extends ScrollPane {

    private Stage primaryStage;
    private TableView<ReportModel> reportTable;
    private ObservableList<ReportModel> masterReportData;
    private FilteredList<ReportModel> filteredData;

    private Label openTicketsLabel;
    private Label resolvedTicketsLabel;
    private Label avgSlaLabel;
    
    private LineChart<String, Number> activityLineChart;
    private PieChart categoryPieChart;

    public ReportsAnalyticsView() {
        this(null);
    }

    public ReportsAnalyticsView(Stage stage) {
        this.primaryStage = stage;

        setFitToWidth(true);
        setStyle("-fx-background-color: #F8FAFC; -fx-background: #F8FAFC;");

        VBox mainContainer = new VBox(25);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setStyle("-fx-background-color: #F8FAFC;");

        // Safely encode CSS Data URI for modern JavaFX compatibility
        try {
            String cssData = getLightThemeCSS();
            String encodedCss = URLEncoder.encode(cssData, StandardCharsets.UTF_8).replace("+", "%20");
            this.getStylesheets().add("data:text/css," + encodedCss);
        } catch (Exception e) {
            System.err.println("Failed to load inline CSS stylesheet: " + e.getMessage());
        }

        // 1. Header Section
        VBox header = createHeader();

        // 2. Metrics & Dual Chart BI Telemetry Section
        VBox biSection = createBITelemetrySection();

        // 3. Search & Category Filters
        HBox filterBar = createFilterBar();

        // 4. Data Table Container
        VBox tableContainer = createTableContainer();

        mainContainer.getChildren().addAll(header, biSection, filterBar, tableContainer);
        setContent(mainContainer);

        // Load Initial Data
        loadReportData();
    }

    public Parent getView() {
        return this;
    }

    // Renamed from getScene() to createScene() to avoid overriding Node.getScene()
    public Scene createScene() {
        return new Scene(this);
    }

    private VBox createHeader() {
        VBox header = new VBox(6);
        Label title = new Label("Reports, Moderation & Telemetry BI");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#0F172A"));

        Label subtitle = new Label("Monitor platform disputes, automated AI anomaly flags, medical malpractice inquiries, and support velocity.");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        subtitle.setTextFill(Color.web("#64748B"));

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private VBox createBITelemetrySection() {
        VBox section = new VBox(20);

        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER);

        openTicketsLabel = new Label("00");
        resolvedTicketsLabel = new Label("0");
        avgSlaLabel = new Label("1.8 Hours");

        VBox openCard = createStatCard("Active Incidents", openTicketsLabel, "⚡ 2 Critical Escalations", "#DC2626", "#FEF2F2");
        VBox resolvedCard = createStatCard("Resolved Tickets", resolvedTicketsLabel, "✓ 94.8% SLA Clearance Rate", "#059669", "#ECFDF5");
        VBox slaCard = createStatCard("Avg Resolution Velocity", avgSlaLabel, "⏱ -14 mins improvement w/o/w", "#4F46E5", "#EEF2FF");

        HBox.setHgrow(openCard, Priority.ALWAYS);
        HBox.setHgrow(resolvedCard, Priority.ALWAYS);
        HBox.setHgrow(slaCard, Priority.ALWAYS);

        statsRow.getChildren().addAll(openCard, resolvedCard, slaCard);

        HBox chartsRow = new HBox(20);

        VBox lineChartCard = createCardContainer();
        HBox.setHgrow(lineChartCard, Priority.ALWAYS);

        Label lineTitle = new Label("Incident Influx vs Resolution Velocity");
        lineTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        lineTitle.setTextFill(Color.web("#0F172A"));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickLabelFill(Color.web("#64748B"));
        yAxis.setTickLabelFill(Color.web("#64748B"));

        activityLineChart = new LineChart<>(xAxis, yAxis);
        activityLineChart.setPrefHeight(220);
        activityLineChart.setLegendVisible(true);
        activityLineChart.setAnimated(true);

        lineChartCard.getChildren().addAll(lineTitle, activityLineChart);

        VBox pieChartCard = createCardContainer();
        pieChartCard.setMinWidth(360);

        Label pieTitle = new Label("Category Breakdown");
        pieTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        pieTitle.setTextFill(Color.web("#0F172A"));

        categoryPieChart = new PieChart();
        categoryPieChart.setPrefHeight(220);
        categoryPieChart.setLegendVisible(false);
        categoryPieChart.setLabelsVisible(true);

        pieChartCard.getChildren().addAll(pieTitle, categoryPieChart);

        chartsRow.getChildren().addAll(lineChartCard, pieChartCard);

        section.getChildren().addAll(statsRow, chartsRow);
        return section;
    }

    private VBox createStatCard(String title, Label valueLabel, String subtext, String accentColor, String bgGlow) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(18));
        card.setStyle(
            "-fx-background-color: " + bgGlow + "; " +
            "-fx-background-radius: 12px; " +
            "-fx-border-color: " + accentColor + "33; " +
            "-fx-border-radius: 12px; " +
            "-fx-border-width: 1px;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        titleLabel.setTextFill(Color.web("#64748B"));

        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        valueLabel.setTextFill(Color.web("#0F172A"));

        Label subLabel = new Label(subtext);
        subLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 11));
        subLabel.setTextFill(Color.web(accentColor));

        card.getChildren().addAll(titleLabel, valueLabel, subLabel);
        return card;
    }

    private VBox createCardContainer() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle(
            "-fx-background-color: #FFFFFF; " +
            "-fx-background-radius: 12px; " +
            "-fx-border-color: #E2E8F0; " +
            "-fx-border-radius: 12px;"
        );
        return card;
    }

    private HBox createFilterBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(15));
        bar.setStyle(
            "-fx-background-color: #FFFFFF; " +
            "-fx-background-radius: 10px; " +
            "-fx-border-color: #E2E8F0; " +
            "-fx-border-radius: 10px;"
        );

        TextField searchInput = new TextField();
        searchInput.setPromptText("🔍 Search Ticket ID, Reporter, or Keyword...");
        searchInput.setPrefWidth(320);
        searchInput.setStyle(
            "-fx-background-color: #F8FAFC; " +
            "-fx-text-fill: #0F172A; " +
            "-fx-border-color: #CBD5E1; " +
            "-fx-border-radius: 6px; " +
            "-fx-padding: 8px 12px;"
        );

        ComboBox<String> categoryFilter = new ComboBox<>();
        categoryFilter.getItems().addAll("All Categories", "Billing Dispute", "Doctor Misbehavior", "App Bug", "Fake Profile");
        categoryFilter.setValue("All Categories");
        categoryFilter.setStyle("-fx-background-color: #F8FAFC; -fx-mark-color: #0F172A; -fx-border-color: #CBD5E1; -fx-border-radius: 6px;");

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All Status", "OPEN", "IN_REVIEW", "RESOLVED");
        statusFilter.setValue("All Status");
        statusFilter.setStyle("-fx-background-color: #F8FAFC; -fx-mark-color: #0F172A; -fx-border-color: #CBD5E1; -fx-border-radius: 6px;");

        Runnable applyFilter = () -> {
            String query = searchInput.getText().toLowerCase().trim();
            String cat = categoryFilter.getValue();
            String status = statusFilter.getValue();

            filteredData.setPredicate(rep -> {
                boolean matchesQuery = query.isEmpty() ||
                        rep.getTicketId().toLowerCase().contains(query) ||
                        rep.getReporterName().toLowerCase().contains(query) ||
                        rep.getSubject().toLowerCase().contains(query);

                boolean matchesCat = "All Categories".equals(cat) || rep.getCategory().equalsIgnoreCase(cat);
                boolean matchesStatus = "All Status".equals(status) || rep.getStatus().equalsIgnoreCase(status);

                return matchesQuery && matchesCat && matchesStatus;
            });
        };

        searchInput.textProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        categoryFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button exportBtn = new Button("📥 Export BI Audit Trail");
        exportBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        exportBtn.setStyle(
            "-fx-background-color: #4F46E5; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 6px; " +
            "-fx-padding: 8px 16px; " +
            "-fx-cursor: hand;"
        );
        exportBtn.setOnAction(e -> showExportConfirmation());

        bar.getChildren().addAll(searchInput, categoryFilter, statusFilter, spacer, exportBtn);
        return bar;
    }

    private VBox createTableContainer() {
        VBox container = new VBox();
        container.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px;");

        reportTable = new TableView<>();
        reportTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        reportTable.setPrefHeight(420);

        // Column 1: Ticket & Reporter
        TableColumn<ReportModel, String> idCol = new TableColumn<>("Incident / Reporter");
        idCol.setCellValueFactory(data -> data.getValue().ticketIdProperty());
        idCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String id, boolean empty) {
                super.updateItem(id, empty);
                if (empty || id == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    ReportModel rep = getTableRow() != null ? getTableRow().getItem() : null;
                    if (rep == null) {
                        setGraphic(null);
                        setText(null);
                        return;
                    }
                    HBox box = new HBox(12);
                    box.setAlignment(Pos.CENTER_LEFT);

                    StackPane icon = createReportBadge(rep.getPriority());

                    VBox textContainer = new VBox(2);
                    Label idLbl = new Label(id + " • " + rep.getCategory());
                    idLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                    idLbl.setTextFill(Color.web("#0F172A"));

                    Label repLbl = new Label("By: " + rep.getReporterName() + " (" + rep.getReporterRole() + ")");
                    repLbl.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
                    repLbl.setTextFill(Color.web("#64748B"));

                    textContainer.getChildren().addAll(idLbl, repLbl);
                    box.getChildren().addAll(icon, textContainer);
                    setGraphic(box);
                    setText(null);
                }
            }
        });

        // Column 2: Subject
        TableColumn<ReportModel, String> subjectCol = new TableColumn<>("Incident Summary");
        subjectCol.setCellValueFactory(data -> data.getValue().subjectProperty());
        subjectCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String subj, boolean empty) {
                super.updateItem(subj, empty);
                setGraphic(null);
                if (empty || subj == null) {
                    setText(null);
                } else {
                    setText(subj);
                    setTextFill(Color.web("#334155"));
                    setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
                }
            }
        });

        // Column 3: Priority
        TableColumn<ReportModel, String> priorityCol = new TableColumn<>("Priority");
        priorityCol.setCellValueFactory(data -> data.getValue().priorityProperty());
        priorityCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String prio, boolean empty) {
                super.updateItem(prio, empty);
                setText(null);
                if (empty || prio == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(prio);
                    badge.setPadding(new Insets(4, 10, 4, 10));
                    badge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));

                    switch (prio.toUpperCase()) {
                        case "CRITICAL" -> badge.setStyle("-fx-background-color: #FFE4E6; -fx-text-fill: #9F1239; -fx-background-radius: 20px;");
                        case "HIGH" -> badge.setStyle("-fx-background-color: #FFEDD5; -fx-text-fill: #9A3412; -fx-background-radius: 20px;");
                        default -> badge.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #475569; -fx-background-radius: 20px;");
                    }
                    setGraphic(badge);
                }
            }
        });

        // Column 4: Status
        TableColumn<ReportModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> data.getValue().statusProperty());
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                setText(null);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(status);
                    badge.setPadding(new Insets(4, 10, 4, 10));
                    badge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));

                    switch (status.toUpperCase()) {
                        case "RESOLVED" -> badge.setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: #065F46; -fx-background-radius: 6px;");
                        case "IN_REVIEW" -> badge.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #92400E; -fx-background-radius: 6px;");
                        default -> badge.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B; -fx-background-radius: 6px;");
                    }
                    setGraphic(badge);
                }
            }
        });

        // Column 5: Moderation Workflow Actions
        TableColumn<ReportModel, Void> actionCol = new TableColumn<>("Moderation Action");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button inspectBtn = new Button("🔍 Investigate");
            private final Button resolveBtn = new Button("✓ Resolve");
            private final HBox btnGroup = new HBox(8, inspectBtn, resolveBtn);

            {
                btnGroup.setAlignment(Pos.CENTER);
                inspectBtn.setStyle("-fx-background-color: #E2E8F0; -fx-text-fill: #1E293B; -fx-cursor: hand; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 4px;");
                resolveBtn.setStyle("-fx-background-color: #059669; -fx-text-fill: #FFFFFF; -fx-cursor: hand; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 5px 10px; -fx-background-radius: 4px;");

                inspectBtn.setOnAction(e -> {
                    ReportModel rep = getTableRow() != null ? getTableRow().getItem() : null;
                    if (rep != null) {
                        showAdvancedInvestigationModal(rep);
                    }
                });

                resolveBtn.setOnAction(e -> {
                    ReportModel rep = getTableRow() != null ? getTableRow().getItem() : null;
                    if (rep != null) {
                        rep.setStatus("RESOLVED");
                        updateAnalyticsAndCharts();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnGroup);
                }
            }
        });

        reportTable.getColumns().addAll(idCol, subjectCol, priorityCol, statusCol, actionCol);
        container.getChildren().add(reportTable);
        return container;
    }

    private StackPane createReportBadge(String priority) {
        Circle circle = new Circle(15);
        if ("CRITICAL".equalsIgnoreCase(priority)) {
            circle.setFill(Color.web("#FFE4E6"));
            circle.setStroke(Color.web("#F43F5E"));
        } else {
            circle.setFill(Color.web("#F1F5F9"));
            circle.setStroke(Color.web("#94A3B8"));
        }
        circle.setStrokeWidth(1.5);

        Label label = new Label(priority != null && !priority.isEmpty() ? priority.substring(0, 1) : "?");
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        label.setTextFill("CRITICAL".equalsIgnoreCase(priority) ? Color.web("#9F1239") : Color.web("#475569"));

        return new StackPane(circle, label);
    }

    private void loadReportData() {
        masterReportData = FXCollections.observableArrayList(
            new ReportModel("TKT-901", "Ananya Verma", "PATIENT", "Billing Dispute", "Incorrect consultation fee charged during online appointment", "CRITICAL", "OPEN", "2026-08-10"),
            new ReportModel("TKT-902", "Saurabh Deshmukh", "PATIENT", "Doctor Misbehavior", "Doctor arrived 40 mins late without prior notice", "HIGH", "IN_REVIEW", "2026-08-09"),
            new ReportModel("TKT-903", "Dr. Rajesh Sharma", "DOCTOR", "App Bug", "Prescription PDF download button unresponsive on Mobile Web", "LOW", "OPEN", "2026-08-11"),
            new ReportModel("TKT-904", "Vikram Malhotra", "PATIENT", "Fake Profile", "Suspected unverified profile claiming to be specialist", "CRITICAL", "IN_REVIEW", "2026-08-08"),
            new ReportModel("TKT-905", "Dr. Priya Nair", "DOCTOR", "Billing Dispute", "Hospital payout delayed for July consultation cycle", "HIGH", "RESOLVED", "2026-08-01")
        );

        filteredData = new FilteredList<>(masterReportData, p -> true);
        reportTable.setItems(filteredData);

        updateAnalyticsAndCharts();
        loadStaticActivityLineChart();
    }

    private void updateAnalyticsAndCharts() {
        long openCount = masterReportData.stream().filter(r -> !r.getStatus().equalsIgnoreCase("RESOLVED")).count();
        long resolvedCount = masterReportData.stream().filter(r -> r.getStatus().equalsIgnoreCase("RESOLVED")).count();

        openTicketsLabel.setText(String.format("%02d", openCount));
        resolvedTicketsLabel.setText(String.valueOf(342 + resolvedCount));

        // Dynamically compute category distribution from active table data
        Map<String, Long> categoryCounts = masterReportData.stream()
                .collect(Collectors.groupingBy(ReportModel::getCategory, Collectors.counting()));

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        categoryCounts.forEach((cat, count) -> pieData.add(new PieChart.Data(cat, count)));
        categoryPieChart.setData(pieData);
    }

    private void loadStaticActivityLineChart() {
        XYChart.Series<String, Number> seriesOpened = new XYChart.Series<>();
        seriesOpened.setName("Tickets Opened");
        seriesOpened.getData().add(new XYChart.Data<>("May", 45));
        seriesOpened.getData().add(new XYChart.Data<>("Jun", 52));
        seriesOpened.getData().add(new XYChart.Data<>("Jul", 38));
        seriesOpened.getData().add(new XYChart.Data<>("Aug", 24));

        XYChart.Series<String, Number> seriesResolved = new XYChart.Series<>();
        seriesResolved.setName("Tickets Resolved");
        seriesResolved.getData().add(new XYChart.Data<>("May", 42));
        seriesResolved.getData().add(new XYChart.Data<>("Jun", 50));
        seriesResolved.getData().add(new XYChart.Data<>("Jul", 40));
        seriesResolved.getData().add(new XYChart.Data<>("Aug", 22));

        activityLineChart.getData().clear();
        activityLineChart.getData().addAll(seriesOpened, seriesResolved);
    }

    private void showAdvancedInvestigationModal(ReportModel rep) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("HealthSphere Incident Intelligence Console");
        dialog.setHeaderText("Incident File: " + rep.getTicketId() + " (" + rep.getCategory() + ")");

        String content = String.format(
            "👤 Reporter: %s [%s]\n" +
            "📌 Subject: %s\n" +
            "⚡ Escalation Priority: %s\n" +
            "📅 Date Logged: %s\n\n" +
            "--- AI TELEMETRY & AUDIT TRAIL ---\n" +
            "• Anomaly Score: 0.88 (High Risk Flag)\n" +
            "• Video/Chat Log Telemetry: Encrypted Chat Session #9921 Attached\n" +
            "• Transaction Reference: TXN_881920_GATEWAY (Razorpay)\n\n" +
            "Select Moderation Action below:",
            rep.getReporterName(), rep.getReporterRole(), rep.getSubject(), rep.getPriority(), rep.getCreatedDate()
        );

        dialog.setContentText(content);

        ButtonType refundBtn = new ButtonType("Issue Full Refund");
        ButtonType warnBtn = new ButtonType("Issue Warning");
        ButtonType dismissBtn = new ButtonType("Dismiss Case", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getButtonTypes().setAll(refundBtn, warnBtn, dismissBtn);

        dialog.showAndWait().ifPresent(type -> {
            if (type == refundBtn) {
                rep.setStatus("RESOLVED");
                updateAnalyticsAndCharts();
            } else if (type == warnBtn) {
                rep.setStatus("IN_REVIEW");
                updateAnalyticsAndCharts();
            }
        });
    }

    private void showExportConfirmation() {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("BI Export System");
        dialog.setHeaderText("Exporting Business Intelligence Audit Trail");
        dialog.setContentText("Full platform BI telemetry log exported successfully to 'HealthSphere_BI_August2026.csv'.");
        dialog.showAndWait();
    }

    private String getLightThemeCSS() {
        return """
            .table-view {
                -fx-background-color: transparent;
                -fx-base: #FFFFFF;
                -fx-control-inner-background: #FFFFFF;
                -fx-background-insets: 0;
            }
            .table-view .column-header-background {
                -fx-background-color: #F8FAFC;
            }
            .table-view .column-header, .table-view .filler {
                -fx-background-color: #F8FAFC;
                -fx-border-color: #E2E8F0;
                -fx-border-width: 0 0 1px 0;
            }
            .table-view .column-header .label {
                -fx-text-fill: #64748B;
                -fx-font-weight: bold;
                -fx-alignment: CENTER-LEFT;
            }
            .table-row-cell {
                -fx-background-color: #FFFFFF;
                -fx-border-color: #F1F5F9;
                -fx-border-width: 0 0 1px 0;
            }
            .table-row-cell:odd {
                -fx-background-color: #F8FAFC;
            }
            .table-row-cell:selected {
                -fx-background-color: #E2E8F0;
            }
            .scroll-bar:vertical, .scroll-bar:horizontal {
                -fx-background-color: #F1F5F9;
            }
            .scroll-bar:vertical .thumb, .scroll-bar:horizontal .thumb {
                -fx-background-color: #CBD5E1;
                -fx-background-radius: 4px;
            }
            """;
    }

    // --- Inner Observable Model Class ---
    public static class ReportModel {
        private final StringProperty ticketId;
        private final StringProperty reporterName;
        private final StringProperty reporterRole;
        private final StringProperty category;
        private final StringProperty subject;
        private final StringProperty priority;
        private final StringProperty status;
        private final StringProperty createdDate;

        public ReportModel(String ticketId, String reporterName, String reporterRole, String category, String subject, String priority, String status, String createdDate) {
            this.ticketId = new SimpleStringProperty(ticketId);
            this.reporterName = new SimpleStringProperty(reporterName);
            this.reporterRole = new SimpleStringProperty(reporterRole);
            this.category = new SimpleStringProperty(category);
            this.subject = new SimpleStringProperty(subject);
            this.priority = new SimpleStringProperty(priority);
            this.status = new SimpleStringProperty(status);
            this.createdDate = new SimpleStringProperty(createdDate);
        }

        public String getTicketId() { return ticketId.get(); }
        public StringProperty ticketIdProperty() { return ticketId; }

        public String getReporterName() { return reporterName.get(); }
        public StringProperty reporterNameProperty() { return reporterName; }

        public String getReporterRole() { return reporterRole.get(); }
        public StringProperty reporterRoleProperty() { return reporterRole; }

        public String getCategory() { return category.get(); }
        public StringProperty categoryProperty() { return category; }

        public String getSubject() { return subject.get(); }
        public StringProperty subjectProperty() { return subject; }

        public String getPriority() { return priority.get(); }
        public StringProperty priorityProperty() { return priority; }

        public String getStatus() { return status.get(); }
        public StringProperty statusProperty() { return status; }
        public void setStatus(String status) { this.status.set(status); }

        public String getCreatedDate() { return createdDate.get(); }
        public StringProperty createdDateProperty() { return createdDate; }
    }
}