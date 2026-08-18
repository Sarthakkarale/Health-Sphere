package com.healthsphere.view.admin;

import com.healthsphere.controller.admin.ModerationController;
import com.healthsphere.model.ComplaintModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportsAnalyticsView extends ScrollPane {

    // ============================================================
    // CONTROLLER
    // ============================================================

    private final ModerationController moderationController;

    private Stage primaryStage;

    // ============================================================
    // TABLE DATA
    // ============================================================

    private TableView<ComplaintModel> reportTable;

    private ObservableList<ComplaintModel> masterReportData;

    private FilteredList<ComplaintModel> filteredData;

    // ============================================================
    // KPI LABELS
    // ============================================================

    private Label openTicketsLabel;
    private Label resolvedTicketsLabel;
    private Label inReviewTicketsLabel;
    private Label avgSlaLabel;

    // ============================================================
    // CHARTS
    // ============================================================

    private BarChart<String, Number> statusBarChart;

    private PieChart categoryPieChart;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public ReportsAnalyticsView() {
        this(null);
    }

    public ReportsAnalyticsView(Stage stage) {

        this.primaryStage = stage;

        this.moderationController =
                new ModerationController();

        setFitToWidth(true);

        setStyle(
                "-fx-background-color: #F8FAFC;" +
                "-fx-background: #F8FAFC;" +
                "-fx-border-color: transparent;"
        );

        VBox mainContainer =
                new VBox(24);

        mainContainer.setPadding(
                new Insets(30)
        );

        mainContainer.setStyle(
                "-fx-background-color: #F8FAFC;"
        );

        // ========================================================
        // HEADER
        // ========================================================

        VBox header =
                createHeader();

        // ========================================================
        // KPI + CHARTS
        // ========================================================

        VBox analyticsSection =
                createAnalyticsSection();

        // ========================================================
        // FILTER BAR
        // ========================================================

        HBox filterBar =
                createFilterBar();

        // ========================================================
        // TABLE
        // ========================================================

        VBox tableContainer =
                createTableContainer();

        mainContainer.getChildren().addAll(
                header,
                analyticsSection,
                filterBar,
                tableContainer
        );

        setContent(mainContainer);

        // ========================================================
        // LOAD FIRESTORE DATA
        // ========================================================

        loadReportData();
    }

    // ============================================================
    // VIEW
    // ============================================================

    public Parent getView() {
        return this;
    }

    public Scene createScene() {
        return new Scene(this);
    }

    // ============================================================
    // HEADER
    // ============================================================

    private VBox createHeader() {

        VBox header =
                new VBox(6);

        Label title =
                new Label(
                        "Reports, Moderation & Telemetry BI"
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

        Label subtitle =
                new Label(
                        "Live moderation analytics powered by Firestore complaint data."
                );

        subtitle.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.NORMAL,
                        14
                )
        );

        subtitle.setTextFill(
                Color.web("#64748B")
        );

        header.getChildren().addAll(
                title,
                subtitle
        );

        return header;
    }

    // ============================================================
    // ANALYTICS SECTION
    // ============================================================

    private VBox createAnalyticsSection() {

        VBox section =
                new VBox(20);

        // ========================================================
        // KPI ROW
        // ========================================================

        HBox statsRow =
                new HBox(20);

        statsRow.setAlignment(
                Pos.CENTER
        );

        openTicketsLabel =
                new Label("0");

        resolvedTicketsLabel =
                new Label("0");

        inReviewTicketsLabel =
                new Label("0");

        avgSlaLabel =
                new Label("N/A");

        VBox openCard =
                createStatCard(
                        "Open Tickets",
                        openTicketsLabel,
                        "Requires moderation action",
                        "#DC2626",
                        "#FEF2F2"
                );

        VBox resolvedCard =
                createStatCard(
                        "Resolved Tickets",
                        resolvedTicketsLabel,
                        "Completed complaints",
                        "#059669",
                        "#ECFDF5"
                );

        VBox reviewCard =
                createStatCard(
                        "In Review",
                        inReviewTicketsLabel,
                        "Currently under investigation",
                        "#D97706",
                        "#FFFBEB"
                );

        VBox avgCard =
                createStatCard(
                        "Resolution Time",
                        avgSlaLabel,
                        "Available when resolution timestamps exist",
                        "#4F46E5",
                        "#EEF2FF"
                );

        HBox.setHgrow(
                openCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                resolvedCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                reviewCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                avgCard,
                Priority.ALWAYS
        );

        statsRow.getChildren().addAll(
                openCard,
                resolvedCard,
                reviewCard,
                avgCard
        );

        // ========================================================
        // CHART ROW
        // ========================================================

        HBox chartsRow =
                new HBox(20);

        // ========================================================
        // STATUS BAR CHART
        // ========================================================

        VBox statusChartCard =
                createCardContainer();

        HBox.setHgrow(
                statusChartCard,
                Priority.ALWAYS
        );

        Label statusTitle =
                new Label(
                        "Live Ticket Status"
                );

        statusTitle.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        15
                )
        );

        statusTitle.setTextFill(
                Color.web("#0F172A")
        );

        CategoryAxis xAxis =
                new CategoryAxis();

        NumberAxis yAxis =
                new NumberAxis();

        yAxis.setLabel(
                "Tickets"
        );

        statusBarChart =
                new BarChart<>(
                        xAxis,
                        yAxis
                );

        statusBarChart.setPrefHeight(
                240
        );

        statusBarChart.setLegendVisible(
                false
        );

        statusBarChart.setAnimated(
                false
        );

        statusChartCard.getChildren().addAll(
                statusTitle,
                statusBarChart
        );

        // ========================================================
        // CATEGORY PIE CHART
        // ========================================================

        VBox pieChartCard =
                createCardContainer();

        pieChartCard.setMinWidth(
                400
        );

        Label pieTitle =
                new Label(
                        "Live Category Breakdown"
                );

        pieTitle.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        15
                )
        );

        pieTitle.setTextFill(
                Color.web("#0F172A")
        );

        categoryPieChart =
                new PieChart();

        categoryPieChart.setPrefHeight(
                240
        );

        categoryPieChart.setLegendVisible(
                true
        );

        categoryPieChart.setLabelsVisible(
                true
        );

        categoryPieChart.setAnimated(
                false
        );

        pieChartCard.getChildren().addAll(
                pieTitle,
                categoryPieChart
        );

        chartsRow.getChildren().addAll(
                statusChartCard,
                pieChartCard
        );

        section.getChildren().addAll(
                statsRow,
                chartsRow
        );

        return section;
    }

    // ============================================================
    // STAT CARD
    // ============================================================

    private VBox createStatCard(
            String title,
            Label valueLabel,
            String subtext,
            String accentColor,
            String backgroundColor) {

        VBox card =
                new VBox(8);

        card.setPadding(
                new Insets(18)
        );

        card.setStyle(
                "-fx-background-color: "
                        + backgroundColor
                        + ";" +
                "-fx-background-radius: 12px;" +
                "-fx-border-color: "
                        + accentColor
                        + "55;" +
                "-fx-border-radius: 12px;" +
                "-fx-border-width: 1px;"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.SEMI_BOLD,
                        13
                )
        );

        titleLabel.setTextFill(
                Color.web("#64748B")
        );

        valueLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        26
                )
        );

        valueLabel.setTextFill(
                Color.web("#0F172A")
        );

        Label subLabel =
                new Label(subtext);

        subLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.NORMAL,
                        11
                )
        );

        subLabel.setWrapText(true);

        subLabel.setTextFill(
                Color.web(accentColor)
        );

        card.getChildren().addAll(
                titleLabel,
                valueLabel,
                subLabel
        );

        return card;
    }

    // ============================================================
    // CARD
    // ============================================================

    private VBox createCardContainer() {

        VBox card =
                new VBox(12);

        card.setPadding(
                new Insets(18)
        );

        card.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 12px;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 12px;"
        );

        return card;
    }

    // ============================================================
    // FILTER BAR
    // ============================================================

    private HBox createFilterBar() {

        HBox bar =
                new HBox(15);

        bar.setAlignment(
                Pos.CENTER_LEFT
        );

        bar.setPadding(
                new Insets(15)
        );

        bar.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 10px;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 10px;"
        );

        // ========================================================
        // SEARCH
        // ========================================================

        TextField searchInput =
                new TextField();

        searchInput.setPromptText(
                "🔍 Search Ticket ID, Complainant, Issue..."
        );

        searchInput.setPrefWidth(
                330
        );

        searchInput.setStyle(
                "-fx-background-color: #F8FAFC;" +
                "-fx-text-fill: #0F172A;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-border-radius: 6px;" +
                "-fx-padding: 8px 12px;"
        );

        // ========================================================
        // CATEGORY
        // ========================================================

        ComboBox<String> categoryFilter =
                new ComboBox<>();

        categoryFilter.getItems().addAll(
                "All Categories",
                "Billing Dispute",
                "Provider Misconduct",
                "Prescription Issue",
                "Platform Bug"
        );

        categoryFilter.setValue(
                "All Categories"
        );

        // ========================================================
        // STATUS
        // ========================================================

        ComboBox<String> statusFilter =
                new ComboBox<>();

        statusFilter.getItems().addAll(
                "All Status",
                "OPEN",
                "IN_REVIEW",
                "RESOLVED"
        );

        statusFilter.setValue(
                "All Status"
        );

        // ========================================================
        // FILTER FUNCTION
        // ========================================================

        Runnable applyFilter =
                () -> {

                    if (filteredData == null) {
                        return;
                    }

                    String query =
                            safe(
                                    searchInput.getText()
                            )
                            .toLowerCase()
                            .trim();

                    String category =
                            categoryFilter.getValue();

                    String status =
                            statusFilter.getValue();

                    filteredData.setPredicate(
                            complaint -> {

                                String ticketId =
                                        safe(
                                                complaint.getTicketId()
                                        );

                                String complainant =
                                        safe(
                                                complaint.getComplainant()
                                        );

                                String issue =
                                        safe(
                                                complaint.getIssueTitle()
                                        );

                                String categoryValue =
                                        safe(
                                                complaint.getCategory()
                                        );

                                boolean matchesSearch =
                                        query.isEmpty()
                                        ||
                                        ticketId
                                                .toLowerCase()
                                                .contains(query)
                                        ||
                                        complainant
                                                .toLowerCase()
                                                .contains(query)
                                        ||
                                        issue
                                                .toLowerCase()
                                                .contains(query)
                                        ||
                                        categoryValue
                                                .toLowerCase()
                                                .contains(query);

                                boolean matchesCategory =
                                        "All Categories"
                                                .equals(category)
                                        ||
                                        categoryValue
                                                .equalsIgnoreCase(
                                                        category
                                                );

                                boolean matchesStatus =
                                        "All Status"
                                                .equals(status)
                                        ||
                                        safe(
                                                complaint.getStatus()
                                        )
                                                .equalsIgnoreCase(
                                                        status
                                                );

                                return matchesSearch
                                        && matchesCategory
                                        && matchesStatus;
                            }
                    );
                };

        searchInput.textProperty()
                .addListener(
                        (obs, oldValue, newValue) ->
                                applyFilter.run()
                );

        categoryFilter.valueProperty()
                .addListener(
                        (obs, oldValue, newValue) ->
                                applyFilter.run()
                );

        statusFilter.valueProperty()
                .addListener(
                        (obs, oldValue, newValue) ->
                                applyFilter.run()
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
        // REFRESH
        // ========================================================

        Button refreshBtn =
                new Button(
                        "↻ Refresh Data"
                );

        refreshBtn.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-text-fill: #334155;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-border-radius: 6px;" +
                "-fx-background-radius: 6px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8px 14px;" +
                "-fx-cursor: hand;"
        );

        refreshBtn.setOnAction(
                e -> loadReportData()
        );

        // ========================================================
        // EXPORT
        // ========================================================

        Button exportBtn =
                new Button(
                        "📥 Export BI Audit Trail"
                );

        exportBtn.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        13
                )
        );

        exportBtn.setStyle(
                "-fx-background-color: #4F46E5;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 6px;" +
                "-fx-padding: 8px 16px;" +
                "-fx-cursor: hand;"
        );

        exportBtn.setOnAction(
                e -> exportAuditTrail()
        );

        bar.getChildren().addAll(
                searchInput,
                categoryFilter,
                statusFilter,
                spacer,
                refreshBtn,
                exportBtn
        );

        return bar;
    }

    // ============================================================
    // TABLE
    // ============================================================

    private VBox createTableContainer() {

        VBox container =
                new VBox();

        container.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 12px;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 12px;"
        );

        reportTable =
                new TableView<>();

        reportTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        reportTable.setPrefHeight(
                430
        );

        // ========================================================
        // TICKET / COMPLAINANT
        // ========================================================

        TableColumn<ComplaintModel, String> ticketCol =
                new TableColumn<>(
                        "Incident / Complainant"
                );

        ticketCol.setCellValueFactory(
                data ->
                        new javafx.beans.property.SimpleStringProperty(
                                safe(
                                        data.getValue()
                                                .getTicketId()
                                )
                        )
        );

        ticketCol.setCellFactory(
                col -> new TableCell<>() {

                    @Override
                    protected void updateItem(
                            String ticketId,
                            boolean empty) {

                        super.updateItem(
                                ticketId,
                                empty
                        );

                        if (
                                empty ||
                                ticketId == null
                        ) {

                            setGraphic(null);
                            setText(null);
                            return;
                        }

                        ComplaintModel complaint =
                                getTableRow()
                                        .getItem();

                        if (complaint == null) {
                            setGraphic(null);
                            setText(null);
                            return;
                        }

                        HBox box =
                                new HBox(10);

                        box.setAlignment(
                                Pos.CENTER_LEFT
                        );

                        StackPane badge =
                                createComplaintBadge(
                                        complaint.getPriority()
                                );

                        VBox text =
                                new VBox(2);

                        Label idLabel =
                                new Label(
                                        ticketId
                                                + " • "
                                                + safe(
                                                        complaint
                                                                .getCategory()
                                                )
                                );

                        idLabel.setFont(
                                Font.font(
                                        "Segoe UI",
                                        FontWeight.BOLD,
                                        13
                                )
                        );

                        idLabel.setTextFill(
                                Color.web("#0F172A")
                        );

                        Label complainantLabel =
                                new Label(
                                        "By: "
                                                + safe(
                                                        complaint
                                                                .getComplainant()
                                                )
                                );

                        complainantLabel.setFont(
                                Font.font(
                                        "Segoe UI",
                                        FontWeight.NORMAL,
                                        11
                                )
                        );

                        complainantLabel.setTextFill(
                                Color.web("#64748B")
                        );

                        text.getChildren().addAll(
                                idLabel,
                                complainantLabel
                        );

                        box.getChildren().addAll(
                                badge,
                                text
                        );

                        setGraphic(box);
                        setText(null);
                    }
                }
        );

        // ========================================================
        // ISSUE
        // ========================================================

        TableColumn<ComplaintModel, String> issueCol =
                new TableColumn<>(
                        "Incident Summary"
                );

        issueCol.setCellValueFactory(
                data ->
                        new javafx.beans.property.SimpleStringProperty(
                                safe(
                                        data.getValue()
                                                .getIssueTitle()
                                )
                        )
        );

        issueCol.setCellFactory(
                col -> new TableCell<>() {

                    @Override
                    protected void updateItem(
                            String issue,
                            boolean empty) {

                        super.updateItem(
                                issue,
                                empty
                        );

                        if (
                                empty ||
                                issue == null
                        ) {

                            setText(null);
                            return;
                        }

                        setText(issue);

                        setTextFill(
                                Color.web("#334155")
                        );

                        setFont(
                                Font.font(
                                        "Segoe UI",
                                        FontWeight.NORMAL,
                                        12
                                )
                        );
                    }
                }
        );

        // ========================================================
        // PRIORITY
        // ========================================================

        TableColumn<ComplaintModel, String> priorityCol =
                new TableColumn<>(
                        "Priority"
                );

        priorityCol.setCellValueFactory(
                data ->
                        new javafx.beans.property.SimpleStringProperty(
                                safe(
                                        data.getValue()
                                                .getPriority()
                                )
                        )
        );

        priorityCol.setCellFactory(
                col -> new TableCell<>() {

                    @Override
                    protected void updateItem(
                            String priority,
                            boolean empty) {

                        super.updateItem(
                                priority,
                                empty
                        );

                        setText(null);

                        if (
                                empty ||
                                priority == null
                        ) {

                            setGraphic(null);
                            return;
                        }

                        Label badge =
                                new Label(priority);

                        badge.setPadding(
                                new Insets(
                                        4,
                                        10,
                                        4,
                                        10
                                )
                        );

                        badge.setFont(
                                Font.font(
                                        "Segoe UI",
                                        FontWeight.BOLD,
                                        11
                                )
                        );

                        if (
                                "CRITICAL"
                                        .equalsIgnoreCase(priority)
                        ) {

                            badge.setStyle(
                                    "-fx-background-color: #FFE4E6;" +
                                    "-fx-text-fill: #9F1239;" +
                                    "-fx-background-radius: 20px;"
                            );

                        } else if (
                                "HIGH"
                                        .equalsIgnoreCase(priority)
                        ) {

                            badge.setStyle(
                                    "-fx-background-color: #FFEDD5;" +
                                    "-fx-text-fill: #9A3412;" +
                                    "-fx-background-radius: 20px;"
                            );

                        } else if (
                                "MEDIUM"
                                        .equalsIgnoreCase(priority)
                        ) {

                            badge.setStyle(
                                    "-fx-background-color: #FEF3C7;" +
                                    "-fx-text-fill: #92400E;" +
                                    "-fx-background-radius: 20px;"
                            );

                        } else {

                            badge.setStyle(
                                    "-fx-background-color: #F1F5F9;" +
                                    "-fx-text-fill: #475569;" +
                                    "-fx-background-radius: 20px;"
                            );
                        }

                        setGraphic(badge);
                    }
                }
        );

        // ========================================================
        // STATUS
        // ========================================================

        TableColumn<ComplaintModel, String> statusCol =
                new TableColumn<>(
                        "Status"
                );

        statusCol.setCellValueFactory(
                data ->
                        new javafx.beans.property.SimpleStringProperty(
                                safe(
                                        data.getValue()
                                                .getStatus()
                                )
                        )
        );

        statusCol.setCellFactory(
                col -> new TableCell<>() {

                    @Override
                    protected void updateItem(
                            String status,
                            boolean empty) {

                        super.updateItem(
                                status,
                                empty
                        );

                        setText(null);

                        if (
                                empty ||
                                status == null
                        ) {

                            setGraphic(null);
                            return;
                        }

                        Label badge =
                                new Label(status);

                        badge.setPadding(
                                new Insets(
                                        4,
                                        10,
                                        4,
                                        10
                                )
                        );

                        badge.setFont(
                                Font.font(
                                        "Segoe UI",
                                        FontWeight.BOLD,
                                        11
                                )
                        );

                        if (
                                "RESOLVED"
                                        .equalsIgnoreCase(status)
                        ) {

                            badge.setStyle(
                                    "-fx-background-color: #D1FAE5;" +
                                    "-fx-text-fill: #065F46;" +
                                    "-fx-background-radius: 6px;"
                            );

                        } else if (
                                "IN_REVIEW"
                                        .equalsIgnoreCase(status)
                        ) {

                            badge.setStyle(
                                    "-fx-background-color: #FEF3C7;" +
                                    "-fx-text-fill: #92400E;" +
                                    "-fx-background-radius: 6px;"
                            );

                        } else {

                            badge.setStyle(
                                    "-fx-background-color: #FEE2E2;" +
                                    "-fx-text-fill: #991B1B;" +
                                    "-fx-background-radius: 6px;"
                            );
                        }

                        setGraphic(badge);
                    }
                }
        );

        // ========================================================
        // ACTIONS
        // ========================================================

        TableColumn<ComplaintModel, Void> actionCol =
                new TableColumn<>(
                        "Moderation Action"
                );

        actionCol.setCellFactory(
                col -> new TableCell<>() {

                    private final Button inspectBtn =
                            new Button(
                                    "🔍 Investigate"
                            );

                    private final Button actionBtn =
                            new Button();

                    private final HBox group =
                            new HBox(
                                    8,
                                    inspectBtn,
                                    actionBtn
                            );

                    {

                        group.setAlignment(
                                Pos.CENTER
                        );

                        inspectBtn.setStyle(
                                "-fx-background-color: #E2E8F0;" +
                                "-fx-text-fill: #1E293B;" +
                                "-fx-cursor: hand;" +
                                "-fx-font-size: 11px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-padding: 5px 10px;" +
                                "-fx-background-radius: 4px;"
                        );

                        inspectBtn.setOnAction(
                                e -> {

                                    ComplaintModel complaint =
                                            getTableRow()
                                                    .getItem();

                                    if (complaint != null) {

                                        showInvestigationModal(
                                                complaint
                                        );
                                    }
                                }
                        );

                        actionBtn.setOnAction(
                                e -> {

                                    ComplaintModel complaint =
                                            getTableRow()
                                                    .getItem();

                                    if (complaint == null) {
                                        return;
                                    }

                                    updateTicketStatus(
                                            complaint
                                    );
                                }
                        );
                    }

                    @Override
                    protected void updateItem(
                            Void item,
                            boolean empty) {

                        super.updateItem(
                                item,
                                empty
                        );

                        setText(null);

                        if (empty) {

                            setGraphic(null);

                            return;
                        }

                        ComplaintModel complaint =
                                getTableRow()
                                        .getItem();

                        if (complaint == null) {

                            setGraphic(null);

                            return;
                        }

                        String status =
                                safe(
                                        complaint.getStatus()
                                );

                        if (
                                "OPEN"
                                        .equalsIgnoreCase(status)
                        ) {

                            actionBtn.setText(
                                    "✓ Review"
                            );

                            actionBtn.setStyle(
                                    "-fx-background-color: #D97706;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-padding: 5px 10px;" +
                                    "-fx-background-radius: 4px;" +
                                    "-fx-cursor: hand;"
                            );

                        } else if (
                                "IN_REVIEW"
                                        .equalsIgnoreCase(status)
                        ) {

                            actionBtn.setText(
                                    "✓ Resolve"
                            );

                            actionBtn.setStyle(
                                    "-fx-background-color: #059669;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-padding: 5px 10px;" +
                                    "-fx-background-radius: 4px;" +
                                    "-fx-cursor: hand;"
                            );

                        } else {

                            actionBtn.setText(
                                    "↻ Re-open"
                            );

                            actionBtn.setStyle(
                                    "-fx-background-color: #4F46E5;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-padding: 5px 10px;" +
                                    "-fx-background-radius: 4px;" +
                                    "-fx-cursor: hand;"
                            );
                        }

                        setGraphic(group);
                    }
                }
        );

        reportTable.getColumns().addAll(
                ticketCol,
                issueCol,
                priorityCol,
                statusCol,
                actionCol
        );

        container.getChildren().add(
                reportTable
        );

        return container;
    }

    // ============================================================
    // BADGE
    // ============================================================

    private StackPane createComplaintBadge(
            String priority) {

        Circle circle =
                new Circle(15);

        String firstLetter =
                priority == null ||
                priority.isEmpty()
                        ? "?"
                        : priority.substring(
                                0,
                                1
                        );

        if (
                "CRITICAL"
                        .equalsIgnoreCase(priority)
        ) {

            circle.setFill(
                    Color.web("#FFE4E6")
            );

            circle.setStroke(
                    Color.web("#F43F5E")
            );

        } else if (
                "HIGH"
                        .equalsIgnoreCase(priority)
        ) {

            circle.setFill(
                    Color.web("#FFEDD5")
            );

            circle.setStroke(
                    Color.web("#F97316")
            );

        } else {

            circle.setFill(
                    Color.web("#F1F5F9")
            );

            circle.setStroke(
                    Color.web("#94A3B8")
            );
        }

        circle.setStrokeWidth(
                1.5
        );

        Label label =
                new Label(
                        firstLetter
                );

        label.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        11
                )
        );

        label.setTextFill(
                Color.web("#475569")
        );

        return new StackPane(
                circle,
                label
        );
    }

    // ============================================================
    // FIRESTORE LOAD
    // ============================================================

    private void loadReportData() {

        try {

            List<ComplaintModel> complaints =
                    moderationController
                            .getAllComplaints();

            if (complaints == null) {

                complaints =
                        java.util.Collections.emptyList();
            }

            masterReportData =
                    FXCollections.observableArrayList(
                            complaints
                    );

            filteredData =
                    new FilteredList<>(
                            masterReportData,
                            complaint -> true
                    );

            reportTable.setItems(
                    filteredData
            );

            updateAnalyticsAndCharts();

        } catch (Exception e) {

            masterReportData =
                    FXCollections.observableArrayList();

            filteredData =
                    new FilteredList<>(
                            masterReportData,
                            complaint -> true
                    );

            reportTable.setItems(
                    filteredData
            );

            showError(
                    "Firestore Error",
                    "Unable to load moderation data.\n\n"
                            + safe(e.getMessage())
            );
        }
    }

    // ============================================================
    // ANALYTICS
    // ============================================================

    private void updateAnalyticsAndCharts() {

        if (masterReportData == null) {
            return;
        }

        long openCount =
                masterReportData.stream()
                        .filter(
                                complaint ->
                                        "OPEN"
                                                .equalsIgnoreCase(
                                                        safe(
                                                                complaint
                                                                        .getStatus()
                                                        )
                                                )
                        )
                        .count();

        long reviewCount =
                masterReportData.stream()
                        .filter(
                                complaint ->
                                        "IN_REVIEW"
                                                .equalsIgnoreCase(
                                                        safe(
                                                                complaint
                                                                        .getStatus()
                                                        )
                                                )
                        )
                        .count();

        long resolvedCount =
                masterReportData.stream()
                        .filter(
                                complaint ->
                                        "RESOLVED"
                                                .equalsIgnoreCase(
                                                        safe(
                                                                complaint
                                                                        .getStatus()
                                                        )
                                                )
                        )
                        .count();

        // ========================================================
        // KPI
        // ========================================================

        openTicketsLabel.setText(
                String.valueOf(openCount)
        );

        resolvedTicketsLabel.setText(
                String.valueOf(resolvedCount)
        );

        inReviewTicketsLabel.setText(
                String.valueOf(reviewCount)
        );

        /*
         * ComplaintModel currently provides createdDate,
         * but no resolution timestamp.
         *
         * Therefore we deliberately DO NOT display a fake
         * resolution time such as "1.8 Hours".
         */

        avgSlaLabel.setText(
                "N/A"
        );

        // ========================================================
        // UPDATE CHARTS
        // ========================================================

        updateStatusChart();

        updateCategoryChart();
    }

    // ============================================================
    // STATUS BAR CHART
    // ============================================================

    private void updateStatusChart() {

        statusBarChart.getData().clear();

        if (masterReportData == null) {
            return;
        }

        long open =
                masterReportData.stream()
                        .filter(
                                c ->
                                        "OPEN"
                                                .equalsIgnoreCase(
                                                        safe(
                                                                c.getStatus()
                                                        )
                                                )
                        )
                        .count();

        long review =
                masterReportData.stream()
                        .filter(
                                c ->
                                        "IN_REVIEW"
                                                .equalsIgnoreCase(
                                                        safe(
                                                                c.getStatus()
                                                        )
                                                )
                        )
                        .count();

        long resolved =
                masterReportData.stream()
                        .filter(
                                c ->
                                        "RESOLVED"
                                                .equalsIgnoreCase(
                                                        safe(
                                                                c.getStatus()
                                                        )
                                                )
                        )
                        .count();

        XYChart.Series<String, Number> series =
                new XYChart.Series<>();

        series.setName(
                "Complaints"
        );

        series.getData().add(
                new XYChart.Data<>(
                        "OPEN",
                        open
                )
        );

        series.getData().add(
                new XYChart.Data<>(
                        "IN REVIEW",
                        review
                )
        );

        series.getData().add(
                new XYChart.Data<>(
                        "RESOLVED",
                        resolved
                )
        );

        statusBarChart.getData().add(
                series
        );
    }

    // ============================================================
    // CATEGORY PIE CHART
    // ============================================================

    private void updateCategoryChart() {

        categoryPieChart.getData().clear();

        if (masterReportData == null) {
            return;
        }

        Map<String, Long> categoryCounts =
                masterReportData.stream()
                        .collect(
                                Collectors.groupingBy(
                                        complaint ->
                                                safe(
                                                        complaint
                                                                .getCategory()
                                                ).isEmpty()
                                                        ? "Uncategorized"
                                                        : safe(
                                                                complaint
                                                                        .getCategory()
                                                        ),
                                        Collectors.counting()
                                )
                        );

        ObservableList<PieChart.Data> pieData =
                FXCollections.observableArrayList();

        categoryCounts.forEach(
                (category, count) ->
                        pieData.add(
                                new PieChart.Data(
                                        category,
                                        count
                                )
                        )
        );

        categoryPieChart.setData(
                pieData
        );
    }

    // ============================================================
    // UPDATE STATUS
    // ============================================================

    private void updateTicketStatus(
            ComplaintModel complaint) {

        String currentStatus =
                safe(
                        complaint.getStatus()
                );

        String newStatus;

        if (
                "OPEN"
                        .equalsIgnoreCase(currentStatus)
        ) {

            newStatus =
                    "IN_REVIEW";

        } else if (
                "IN_REVIEW"
                        .equalsIgnoreCase(currentStatus)
        ) {

            newStatus =
                    "RESOLVED";

        } else {

            newStatus =
                    "OPEN";
        }

        try {

            boolean success =
                    moderationController
                            .updateComplaintStatus(
                                    complaint.getTicketId(),
                                    newStatus
                            );

            if (success) {

                complaint.setStatus(
                        newStatus
                );

                reportTable.refresh();

                updateAnalyticsAndCharts();

            } else {

                showError(
                        "Update Failed",
                        "Firestore could not update ticket status."
                );
            }

        } catch (Exception e) {

            showError(
                    "Update Failed",
                    safe(e.getMessage())
            );
        }
    }

    // ============================================================
    // INVESTIGATION MODAL
    // ============================================================

    private void showInvestigationModal(
            ComplaintModel complaint) {

        Alert dialog =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        dialog.setTitle(
                "HealthSphere Moderation Console"
        );

        dialog.setHeaderText(
                "Ticket: "
                        + safe(
                                complaint.getTicketId()
                        )
                        + " | "
                        + safe(
                                complaint.getCategory()
                        )
        );

        String content =
                "Ticket ID: "
                        + safe(
                                complaint.getTicketId()
                        )
                        + "\n\n"

                        + "Complainant: "
                        + safe(
                                complaint.getComplainant()
                        )
                        + "\n\n"

                        + "Category: "
                        + safe(
                                complaint.getCategory()
                        )
                        + "\n\n"

                        + "Issue: "
                        + safe(
                                complaint.getIssueTitle()
                        )
                        + "\n\n"

                        + "Priority: "
                        + safe(
                                complaint.getPriority()
                        )
                        + "\n\n"

                        + "Status: "
                        + safe(
                                complaint.getStatus()
                        )
                        + "\n\n"

                        + "Created Date: "
                        + safe(
                                complaint.getCreatedDate()
                        )
                        + "\n\n"

                        + "Description:\n"
                        + safe(
                                complaint.getDescription()
                        );

        dialog.setContentText(
                content
        );

        ButtonType reviewBtn =
                new ButtonType(
                        "Move to Review"
                );

        ButtonType resolveBtn =
                new ButtonType(
                        "Resolve"
                );

        ButtonType closeBtn =
                new ButtonType(
                        "Close",
                        ButtonBar.ButtonData.CANCEL_CLOSE
                );

        dialog.getButtonTypes().setAll(
                reviewBtn,
                resolveBtn,
                closeBtn
        );

        dialog.showAndWait()
                .ifPresent(
                        button -> {

                            if (
                                    button == reviewBtn
                            ) {

                                setStatusFromModal(
                                        complaint,
                                        "IN_REVIEW"
                                );

                            } else if (
                                    button == resolveBtn
                            ) {

                                setStatusFromModal(
                                        complaint,
                                        "RESOLVED"
                                );
                            }
                        }
                );
    }

    // ============================================================
    // MODAL STATUS UPDATE
    // ============================================================

    private void setStatusFromModal(
            ComplaintModel complaint,
            String status) {

        try {

            boolean success =
                    moderationController
                            .updateComplaintStatus(
                                    complaint.getTicketId(),
                                    status
                            );

            if (success) {

                complaint.setStatus(
                        status
                );

                reportTable.refresh();

                updateAnalyticsAndCharts();

            } else {

                showError(
                        "Update Failed",
                        "Unable to update ticket in Firestore."
                );
            }

        } catch (Exception e) {

            showError(
                    "Update Failed",
                    safe(e.getMessage())
            );
        }
    }

    // ============================================================
    // EXPORT REAL DATA
    // ============================================================

    private void exportAuditTrail() {

        if (
                masterReportData == null ||
                masterReportData.isEmpty()
        ) {

            showError(
                    "Nothing to Export",
                    "There are no complaint records available."
            );

            return;
        }

        FileChooser chooser =
                new FileChooser();

        chooser.setTitle(
                "Export Moderation Audit Trail"
        );

        chooser.setInitialFileName(
                "HealthSphere_Moderation_Audit.csv"
        );

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "CSV Files",
                        "*.csv"
                )
        );

        File file =
                chooser.showSaveDialog(
                        primaryStage
                );

        if (file == null) {
            return;
        }

        try (
                PrintWriter writer =
                        new PrintWriter(
                                new FileWriter(file)
                        )
        ) {

            writer.println(
                    "Ticket ID,Complainant,Category,Issue,Priority,Status,Created Date"
            );

            for (
                    ComplaintModel complaint :
                    masterReportData
            ) {

                writer.println(
                        csv(
                                complaint.getTicketId()
                        )
                        + ","
                        + csv(
                                complaint.getComplainant()
                        )
                        + ","
                        + csv(
                                complaint.getCategory()
                        )
                        + ","
                        + csv(
                                complaint.getIssueTitle()
                        )
                        + ","
                        + csv(
                                complaint.getPriority()
                        )
                        + ","
                        + csv(
                                complaint.getStatus()
                        )
                        + ","
                        + csv(
                                complaint.getCreatedDate()
                        )
                );
            }

            showInfo(
                    "Export Complete",
                    "Actual Firestore complaint data exported successfully."
            );

        } catch (Exception e) {

            showError(
                    "Export Failed",
                    safe(e.getMessage())
            );
        }
    }

    // ============================================================
    // CSV ESCAPE
    // ============================================================

    private String csv(String value) {

        String text =
                safe(value)
                        .replace(
                                "\"",
                                "\"\""
                        );

        return "\""
                + text
                + "\"";
    }

    // ============================================================
    // SAFE
    // ============================================================

    private String safe(
            String value) {

        return value == null
                ? ""
                : value;
    }

    // ============================================================
    // INFO
    // ============================================================

    private void showInfo(
            String title,
            String content) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(title);

        alert.setHeaderText(null);

        alert.setContentText(
                content
        );

        alert.showAndWait();
    }

    // ============================================================
    // ERROR
    // ============================================================

    private void showError(
            String title,
            String content) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle(title);

        alert.setHeaderText(null);

        alert.setContentText(
                content == null ||
                content.trim().isEmpty()
                        ? "An unexpected error occurred."
                        : content
        );

        alert.showAndWait();
    }
}