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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ComplaintsManagementView {

    private final Stage stage;
    private final ModerationController moderationController;

    private TableView<ComplaintModel> ticketTable;

    private ObservableList<ComplaintModel> masterTicketData;
    private FilteredList<ComplaintModel> filteredData;

    private Label totalTicketsLabel;
    private Label openTicketsLabel;
    private Label inReviewTicketsLabel;
    private Label resolvedTicketsLabel;

    private BarChart<String, Number> categoryChart;
    private PieChart statusChart;

    private ComboBox<String> categoryFilter;
    private ComboBox<String> statusFilter;
    private TextField searchInput;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ComplaintsManagementView(Stage stage) {
        this.stage = stage;
        this.moderationController = new ModerationController();
    }

    public Scene getScene() {
        return new Scene(getView());
    }

    public Parent getView() {

        VBox root = new VBox(24);

        root.setPadding(new Insets(28));

        root.setStyle(
                "-fx-background-color: #F8FAFC;"
        );

        HBox header = createHeader();

        HBox kpiSection = createKPISection();

        HBox filterBar = createFilterBar();

        GridPane contentGrid = new GridPane();

        contentGrid.setHgap(20);
        contentGrid.setVgap(20);

        ColumnConstraints tableColumn =
                new ColumnConstraints();

        tableColumn.setPercentWidth(68);

        ColumnConstraints analyticsColumn =
                new ColumnConstraints();

        analyticsColumn.setPercentWidth(32);

        contentGrid.getColumnConstraints().addAll(
                tableColumn,
                analyticsColumn
        );

        VBox tableContainer =
                createTicketTableCard();

        VBox analyticsContainer =
                createSidebarAnalyticsCard();

        contentGrid.add(
                tableContainer,
                0,
                0
        );

        contentGrid.add(
                analyticsContainer,
                1,
                0
        );

        root.getChildren().addAll(
                header,
                kpiSection,
                filterBar,
                contentGrid
        );

        loadComplaintData();

        ScrollPane scrollPane =
                new ScrollPane(root);

        scrollPane.setFitToWidth(true);

        scrollPane.setStyle(
                "-fx-background-color: #F8FAFC;" +
                "-fx-background: #F8FAFC;" +
                "-fx-border-color: transparent;"
        );

        return scrollPane;
    }

    // ============================================================
    // HEADER
    // ============================================================

    private HBox createHeader() {

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox titleBox =
                new VBox(4);

        Label title =
                new Label(
                        "Reports, Moderation & Telemetry"
                );

        title.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        24
                )
        );

        title.setTextFill(
                Color.web("#0F172A")
        );

        Label subtitle =
                new Label(
                        "Monitor complaints, moderation activity, provider issues, and support tickets."
                );

        subtitle.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.NORMAL,
                        13
                )
        );

        subtitle.setTextFill(
                Color.web("#64748B")
        );

        titleBox.getChildren().addAll(
                title,
                subtitle
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button refreshButton =
                new Button(
                        "Refresh Data"
                );

        refreshButton.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-text-fill: #334155;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;" +
                "-fx-padding: 8 16;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        refreshButton.setOnAction(
                e -> loadComplaintData()
        );

        Button exportButton =
                new Button(
                        "Export Report"
                );

        exportButton.setStyle(
                "-fx-background-color: #4F46E5;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 16;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;"
        );

        exportButton.setOnAction(
                e -> exportComplaints()
        );

        Button newTicketButton =
                new Button(
                        "+ File Internal Ticket"
                );

        newTicketButton.setStyle(
                "-fx-background-color: #059669;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 16;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;"
        );

        newTicketButton.setOnAction(
                e -> showCreateTicketModal()
        );

        HBox buttons =
                new HBox(
                        10,
                        refreshButton,
                        exportButton,
                        newTicketButton
                );

        buttons.setAlignment(
                Pos.CENTER_RIGHT
        );

        header.getChildren().addAll(
                titleBox,
                spacer,
                buttons
        );

        return header;
    }

    // ============================================================
    // KPI SECTION
    // ============================================================

    private HBox createKPISection() {

        HBox section =
                new HBox(16);

        section.setAlignment(
                Pos.CENTER
        );

        totalTicketsLabel =
                new Label("0");

        openTicketsLabel =
                new Label("0");

        inReviewTicketsLabel =
                new Label("0");

        resolvedTicketsLabel =
                new Label("0");

        VBox totalCard =
                createKpiCard(
                        "Total Tickets",
                        totalTicketsLabel,
                        "Live Firestore count",
                        "#4F46E5"
                );

        VBox openCard =
                createKpiCard(
                        "Open Tickets",
                        openTicketsLabel,
                        "Requires action",
                        "#DC2626"
                );

        VBox reviewCard =
                createKpiCard(
                        "In Review",
                        inReviewTicketsLabel,
                        "Currently investigated",
                        "#D97706"
                );

        VBox resolvedCard =
                createKpiCard(
                        "Resolved Tickets",
                        resolvedTicketsLabel,
                        "Successfully resolved",
                        "#059669"
                );

        HBox.setHgrow(
                totalCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                openCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                reviewCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                resolvedCard,
                Priority.ALWAYS
        );

        section.getChildren().addAll(
                totalCard,
                openCard,
                reviewCard,
                resolvedCard
        );

        return section;
    }

    private VBox createKpiCard(
            String title,
            Label valueLabel,
            String subtitle,
            String accentColor) {

        VBox card =
                new VBox(6);

        card.setPadding(
                new Insets(16)
        );

        card.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 12px;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 12px;"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.SEMI_BOLD,
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
                        24
                )
        );

        valueLabel.setTextFill(
                Color.web("#0F172A")
        );

        Label subtitleLabel =
                new Label(subtitle);

        subtitleLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.NORMAL,
                        11
                )
        );

        subtitleLabel.setTextFill(
                Color.web(accentColor)
        );

        card.getChildren().addAll(
                titleLabel,
                valueLabel,
                subtitleLabel
        );

        return card;
    }

    // ============================================================
    // FILTER BAR
    // ============================================================

    private HBox createFilterBar() {

        HBox bar =
                new HBox(12);

        bar.setAlignment(
                Pos.CENTER_LEFT
        );

        bar.setPadding(
                new Insets(14)
        );

        bar.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 10px;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 10px;"
        );

        searchInput =
                new TextField();

        searchInput.setPromptText(
                "Search Ticket ID, Reporter, Issue..."
        );

        searchInput.setPrefWidth(330);

        categoryFilter =
                new ComboBox<>();

        categoryFilter.setPromptText(
                "All Categories"
        );

        categoryFilter.setPrefWidth(170);

        statusFilter =
                new ComboBox<>();

        statusFilter.setPromptText(
                "All Statuses"
        );

        statusFilter.setPrefWidth(150);

        Button resetButton =
                new Button(
                        "Reset Filters"
                );

        resetButton.setOnAction(
                e -> resetFilters()
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        searchInput.textProperty().addListener(
                (obs, oldValue, newValue) ->
                        applyFilters()
        );

        categoryFilter.valueProperty().addListener(
                (obs, oldValue, newValue) ->
                        applyFilters()
        );

        statusFilter.valueProperty().addListener(
                (obs, oldValue, newValue) ->
                        applyFilters()
        );

        bar.getChildren().addAll(
                searchInput,
                categoryFilter,
                statusFilter,
                spacer,
                resetButton
        );

        return bar;
    }

    private void resetFilters() {

        if (searchInput != null) {
            searchInput.clear();
        }

        if (categoryFilter != null) {
            categoryFilter.setValue(null);
        }

        if (statusFilter != null) {
            statusFilter.setValue(null);
        }

        applyFilters();
    }

    private void applyFilters() {

        if (filteredData == null) {
            return;
        }

        String search =
                searchInput == null
                        ? ""
                        : safe(searchInput.getText())
                                .trim()
                                .toLowerCase();

        String selectedCategory =
                categoryFilter == null
                        ? null
                        : categoryFilter.getValue();

        String selectedStatus =
                statusFilter == null
                        ? null
                        : statusFilter.getValue();

        filteredData.setPredicate(
                ticket -> {

                    boolean matchesSearch =
                            search.isEmpty()
                                    ||
                            safe(ticket.getTicketId())
                                    .toLowerCase()
                                    .contains(search)
                                    ||
                            safe(ticket.getComplainant())
                                    .toLowerCase()
                                    .contains(search)
                                    ||
                            safe(ticket.getIssueTitle())
                                    .toLowerCase()
                                    .contains(search)
                                    ||
                            safe(ticket.getDescription())
                                    .toLowerCase()
                                    .contains(search)
                                    ||
                            safe(ticket.getCategory())
                                    .toLowerCase()
                                    .contains(search);

                    boolean matchesCategory =
                            selectedCategory == null
                                    ||
                            selectedCategory.equals(
                                    ticket.getCategory()
                            );

                    boolean matchesStatus =
                            selectedStatus == null
                                    ||
                            selectedStatus.equalsIgnoreCase(
                                    safe(ticket.getStatus())
                            );

                    return matchesSearch
                            && matchesCategory
                            && matchesStatus;
                }
        );
    }

    // ============================================================
    // TABLE
    // ============================================================

    private VBox createTicketTableCard() {

        VBox container =
                new VBox(12);

        container.setPadding(
                new Insets(16)
        );

        container.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 12px;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 12px;"
        );

        Label title =
                new Label(
                        "Live Moderation Tickets"
                );

        title.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        15
                )
        );

        ticketTable =
                new TableView<>();

        ticketTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        ticketTable.setPrefHeight(450);

        TableColumn<ComplaintModel, String> ticketIdColumn =
                new TableColumn<>(
                        "Ticket ID"
                );

        ticketIdColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "ticketId"
                )
        );

        TableColumn<ComplaintModel, String> issueColumn =
                new TableColumn<>(
                        "Issue / Reporter"
                );

        issueColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "issueTitle"
                )
        );

        issueColumn.setCellFactory(
                column ->
                        new TableCell<>() {

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

                                    setGraphic(null);
                                    return;
                                }

                                ComplaintModel ticket =
                                        getTableView()
                                                .getItems()
                                                .get(
                                                        getIndex()
                                                );

                                VBox box =
                                        new VBox(3);

                                Label issueLabel =
                                        new Label(
                                                issue
                                        );

                                issueLabel.setFont(
                                        Font.font(
                                                "Segoe UI",
                                                FontWeight.BOLD,
                                                12
                                        )
                                );

                                Label reporterLabel =
                                        new Label(
                                                "By: "
                                                        + safe(
                                                                ticket.getComplainant()
                                                        )
                                                        + " • "
                                                        + safe(
                                                                ticket.getCategory()
                                                        )
                                        );

                                reporterLabel.setFont(
                                        Font.font(
                                                "Segoe UI",
                                                10
                                        )
                                );

                                reporterLabel.setTextFill(
                                        Color.web(
                                                "#64748B"
                                        )
                                );

                                box.getChildren().addAll(
                                        issueLabel,
                                        reporterLabel
                                );

                                setGraphic(box);
                            }
                        }
        );

        TableColumn<ComplaintModel, String> priorityColumn =
                new TableColumn<>(
                        "Priority"
                );

        priorityColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "priority"
                )
        );

        priorityColumn.setCellFactory(
                column ->
                        new TableCell<>() {

                            @Override
                            protected void updateItem(
                                    String priority,
                                    boolean empty) {

                                super.updateItem(
                                        priority,
                                        empty
                                );

                                if (
                                        empty ||
                                        priority == null
                                ) {

                                    setGraphic(null);
                                    return;
                                }

                                Label label =
                                        new Label(
                                                priority
                                        );

                                label.setPadding(
                                        new Insets(
                                                5,
                                                10,
                                                5,
                                                10
                                        )
                                );

                                label.setStyle(
                                        getPriorityStyle(
                                                priority
                                        )
                                );

                                setGraphic(label);
                            }
                        }
        );

        TableColumn<ComplaintModel, String> statusColumn =
                new TableColumn<>(
                        "Status"
                );

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "status"
                )
        );

        statusColumn.setCellFactory(
                column ->
                        new TableCell<>() {

                            @Override
                            protected void updateItem(
                                    String status,
                                    boolean empty) {

                                super.updateItem(
                                        status,
                                        empty
                                );

                                if (
                                        empty ||
                                        status == null
                                ) {

                                    setGraphic(null);
                                    return;
                                }

                                Label label =
                                        new Label(
                                                status
                                        );

                                label.setPadding(
                                        new Insets(
                                                5,
                                                10,
                                                5,
                                                10
                                        )
                                );

                                label.setStyle(
                                        getStatusStyle(
                                                status
                                        )
                                );

                                setGraphic(label);
                            }
                        }
        );

        TableColumn<ComplaintModel, String> dateColumn =
                new TableColumn<>(
                        "Created"
                );

        dateColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "createdDate"
                )
        );

        TableColumn<ComplaintModel, Void> actionColumn =
                new TableColumn<>(
                        "Moderation Action"
                );

        actionColumn.setCellFactory(
                column ->
                        new TableCell<>() {

                            private final Button inspectButton =
                                    new Button(
                                            "Inspect"
                                    );

                            private final Button actionButton =
                                    new Button();

                            private final HBox buttons =
                                    new HBox(
                                            6,
                                            inspectButton,
                                            actionButton
                                    );

                            {

                                inspectButton.setOnAction(
                                        event -> {

                                            ComplaintModel ticket =
                                                    getCurrentTicket();

                                            if (ticket != null) {
                                                showTicketDetailsModal(
                                                        ticket
                                                );
                                            }
                                        }
                                );

                                actionButton.setOnAction(
                                        event -> {

                                            ComplaintModel ticket =
                                                    getCurrentTicket();

                                            if (ticket != null) {
                                                changeTicketStatus(
                                                        ticket
                                                );
                                            }
                                        }
                                );
                            }

                            private ComplaintModel getCurrentTicket() {

                                if (
                                        getIndex() < 0 ||
                                        getIndex()
                                                >= getTableView()
                                                        .getItems()
                                                        .size()
                                ) {
                                    return null;
                                }

                                return getTableView()
                                        .getItems()
                                        .get(
                                                getIndex()
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

                                if (empty) {

                                    setGraphic(null);
                                    return;
                                }

                                ComplaintModel ticket =
                                        getCurrentTicket();

                                if (ticket == null) {

                                    setGraphic(null);
                                    return;
                                }

                                String status =
                                        safe(
                                                ticket.getStatus()
                                        );

                                if (
                                        "OPEN"
                                                .equalsIgnoreCase(
                                                        status
                                                )
                                ) {

                                    actionButton.setText(
                                            "Review"
                                    );

                                } else if (
                                        "IN_REVIEW"
                                                .equalsIgnoreCase(
                                                        status
                                                )
                                ) {

                                    actionButton.setText(
                                            "Resolve"
                                    );

                                } else {

                                    actionButton.setText(
                                            "Re-open"
                                    );
                                }

                                setGraphic(buttons);
                            }
                        }
        );

        ticketTable.getColumns().addAll(
                ticketIdColumn,
                issueColumn,
                priorityColumn,
                statusColumn,
                dateColumn,
                actionColumn
        );

        container.getChildren().addAll(
                title,
                ticketTable
        );

        VBox.setVgrow(
                ticketTable,
                Priority.ALWAYS
        );

        return container;
    }

    private String getPriorityStyle(
            String priority) {

        if (
                "CRITICAL".equalsIgnoreCase(
                        priority
                ) ||
                "HIGH".equalsIgnoreCase(
                        priority
                )
        ) {

            return
                    "-fx-background-color: #FEE2E2;" +
                    "-fx-text-fill: #B91C1C;" +
                    "-fx-background-radius: 12px;";
        }

        if (
                "MEDIUM".equalsIgnoreCase(
                        priority
                )
        ) {

            return
                    "-fx-background-color: #FEF3C7;" +
                    "-fx-text-fill: #92400E;" +
                    "-fx-background-radius: 12px;";
        }

        return
                "-fx-background-color: #E0F2FE;" +
                "-fx-text-fill: #075985;" +
                "-fx-background-radius: 12px;";
    }

    private String getStatusStyle(
            String status) {

        if (
                "OPEN".equalsIgnoreCase(
                        status
                )
        ) {

            return
                    "-fx-background-color: #FEE2E2;" +
                    "-fx-text-fill: #B91C1C;" +
                    "-fx-background-radius: 12px;";
        }

        if (
                "IN_REVIEW".equalsIgnoreCase(
                        status
                )
        ) {

            return
                    "-fx-background-color: #FEF3C7;" +
                    "-fx-text-fill: #92400E;" +
                    "-fx-background-radius: 12px;";
        }

        if (
                "RESOLVED".equalsIgnoreCase(
                        status
                )
        ) {

            return
                    "-fx-background-color: #D1FAE5;" +
                    "-fx-text-fill: #047857;" +
                    "-fx-background-radius: 12px;";
        }

        return
                "-fx-background-color: #E2E8F0;" +
                "-fx-text-fill: #334155;" +
                "-fx-background-radius: 12px;";
    }

    // ============================================================
    // ANALYTICS
    // ============================================================

    private VBox createSidebarAnalyticsCard() {

        VBox container =
                new VBox(16);

        VBox categoryCard =
                new VBox(10);

        categoryCard.setPadding(
                new Insets(16)
        );

        categoryCard.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 12px;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 12px;"
        );

        Label categoryTitle =
                new Label(
                        "Complaints by Category"
                );

        categoryTitle.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        14
                )
        );

        CategoryAxis xAxis =
                new CategoryAxis();

        NumberAxis yAxis =
                new NumberAxis();

        yAxis.setLabel(
                "Tickets"
        );

        categoryChart =
                new BarChart<>(
                        xAxis,
                        yAxis
                );

        categoryChart.setLegendVisible(
                false
        );

        categoryChart.setAnimated(
                false
        );

        categoryChart.setPrefHeight(
                280
        );

        categoryCard.getChildren().addAll(
                categoryTitle,
                categoryChart
        );

        VBox statusCard =
                new VBox(10);

        statusCard.setPadding(
                new Insets(16)
        );

        statusCard.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 12px;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 12px;"
        );

        Label statusTitle =
                new Label(
                        "Status Breakdown"
                );

        statusTitle.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        14
                )
        );

        statusChart =
                new PieChart();

        statusChart.setLegendVisible(
                true
        );

        statusChart.setLabelsVisible(
                true
        );

        statusChart.setAnimated(
                false
        );

        statusChart.setPrefHeight(
                250
        );

        statusCard.getChildren().addAll(
                statusTitle,
                statusChart
        );

        container.getChildren().addAll(
                categoryCard,
                statusCard
        );

        return container;
    }

    // ============================================================
    // LOAD FIRESTORE DATA
    // ============================================================

    private void loadComplaintData() {

        try {

            List<ComplaintModel> complaints =
                    moderationController
                            .getAllComplaints();

            if (complaints == null) {
                complaints =
                        List.of();
            }

            masterTicketData =
                    FXCollections.observableArrayList(
                            complaints
                    );

            filteredData =
                    new FilteredList<>(
                            masterTicketData,
                            ticket -> true
                    );

            if (ticketTable != null) {

                ticketTable.setItems(
                        filteredData
                );
            }

            populateFilters();

            recalculateStats();

        } catch (Exception e) {

            masterTicketData =
                    FXCollections.observableArrayList();

            filteredData =
                    new FilteredList<>(
                            masterTicketData,
                            ticket -> true
                    );

            if (ticketTable != null) {

                ticketTable.setItems(
                        filteredData
                );
            }

            updateEmptyAnalytics();

            showError(
                    "Firestore Error",
                    "Unable to load complaints.\n\n"
                            + safe(
                                    e.getMessage()
                            )
            );
        }
    }

    // ============================================================
    // FILTER VALUES FROM REAL DATA
    // ============================================================

    private void populateFilters() {

        if (
                masterTicketData == null ||
                categoryFilter == null ||
                statusFilter == null
        ) {
            return;
        }

        String oldCategory =
                categoryFilter.getValue();

        String oldStatus =
                statusFilter.getValue();

        List<String> categories =
                masterTicketData.stream()
                        .map(
                                ComplaintModel::getCategory
                        )
                        .filter(
                                value ->
                                        value != null
                                                &&
                                        !value.trim().isEmpty()
                        )
                        .map(String::trim)
                        .distinct()
                        .sorted()
                        .collect(
                                Collectors.toList()
                        );

        List<String> statuses =
                masterTicketData.stream()
                        .map(
                                ComplaintModel::getStatus
                        )
                        .filter(
                                value ->
                                        value != null
                                                &&
                                        !value.trim().isEmpty()
                        )
                        .map(String::trim)
                        .distinct()
                        .sorted()
                        .collect(
                                Collectors.toList()
                        );

        categoryFilter.getItems().setAll(
                categories
        );

        statusFilter.getItems().setAll(
                statuses
        );

        if (
                oldCategory != null &&
                categories.contains(oldCategory)
        ) {

            categoryFilter.setValue(
                    oldCategory
            );
        } else {

            categoryFilter.setValue(
                    null
            );
        }

        if (
                oldStatus != null &&
                statuses.contains(oldStatus)
        ) {

            statusFilter.setValue(
                    oldStatus
            );
        } else {

            statusFilter.setValue(
                    null
            );
        }
    }

    // ============================================================
    // DYNAMIC STATISTICS
    // ============================================================

    private void recalculateStats() {

        if (masterTicketData == null) {
            return;
        }

        long total =
                masterTicketData.size();

        long open =
                masterTicketData.stream()
                        .filter(
                                ticket ->
                                        "OPEN"
                                                .equalsIgnoreCase(
                                                        safe(
                                                                ticket.getStatus()
                                                        )
                                                )
                        )
                        .count();

        long inReview =
                masterTicketData.stream()
                        .filter(
                                ticket ->
                                        "IN_REVIEW"
                                                .equalsIgnoreCase(
                                                        safe(
                                                                ticket.getStatus()
                                                        )
                                                )
                        )
                        .count();

        long resolved =
                masterTicketData.stream()
                        .filter(
                                ticket ->
                                        "RESOLVED"
                                                .equalsIgnoreCase(
                                                        safe(
                                                                ticket.getStatus()
                                                        )
                                                )
                        )
                        .count();

        totalTicketsLabel.setText(
                String.valueOf(total)
        );

        openTicketsLabel.setText(
                String.valueOf(open)
        );

        inReviewTicketsLabel.setText(
                String.valueOf(inReview)
        );

        resolvedTicketsLabel.setText(
                String.valueOf(resolved)
        );

        updateCategoryChart();

        updateStatusChart();
    }

    // ============================================================
    // DYNAMIC CATEGORY CHART
    // ============================================================

    private void updateCategoryChart() {

        if (categoryChart == null) {
            return;
        }

        categoryChart.getData().clear();

        if (
                masterTicketData == null ||
                masterTicketData.isEmpty()
        ) {
            return;
        }

        Map<String, Long> categoryCounts =
                masterTicketData.stream()
                        .collect(
                                Collectors.groupingBy(
                                        ticket -> {

                                            String category =
                                                    safe(
                                                            ticket.getCategory()
                                                    ).trim();

                                            return category.isEmpty()
                                                    ? "Uncategorized"
                                                    : category;
                                        },
                                        Collectors.counting()
                                )
                        );

        XYChart.Series<String, Number> series =
                new XYChart.Series<>();

        categoryCounts.entrySet()
                .stream()
                .sorted(
                        Map.Entry.<String, Long>comparingByValue()
                                .reversed()
                )
                .forEach(
                        entry ->
                                series.getData().add(
                                        new XYChart.Data<>(
                                                entry.getKey(),
                                                entry.getValue()
                                        )
                                )
                );

        categoryChart.getData().add(
                series
        );
    }

    // ============================================================
    // DYNAMIC STATUS CHART
    // ============================================================

    private void updateStatusChart() {

        if (statusChart == null) {
            return;
        }

        statusChart.getData().clear();

        if (
                masterTicketData == null ||
                masterTicketData.isEmpty()
        ) {
            return;
        }

        Map<String, Long> statusCounts =
                masterTicketData.stream()
                        .collect(
                                Collectors.groupingBy(
                                        ticket -> {

                                            String status =
                                                    safe(
                                                            ticket.getStatus()
                                                    ).trim();

                                            return status.isEmpty()
                                                    ? "UNKNOWN"
                                                    : status;
                                        },
                                        Collectors.counting()
                                )
                        );

        statusCounts.forEach(
                (status, count) ->
                        statusChart.getData().add(
                                new PieChart.Data(
                                        status,
                                        count
                                )
                        )
        );
    }

    private void updateEmptyAnalytics() {

        if (totalTicketsLabel != null) {
            totalTicketsLabel.setText("0");
        }

        if (openTicketsLabel != null) {
            openTicketsLabel.setText("0");
        }

        if (inReviewTicketsLabel != null) {
            inReviewTicketsLabel.setText("0");
        }

        if (resolvedTicketsLabel != null) {
            resolvedTicketsLabel.setText("0");
        }

        if (categoryChart != null) {
            categoryChart.getData().clear();
        }

        if (statusChart != null) {
            statusChart.getData().clear();
        }
    }

    // ============================================================
    // CHANGE STATUS
    // ============================================================

    private void changeTicketStatus(
            ComplaintModel ticket) {

        String currentStatus =
                safe(
                        ticket.getStatus()
                );

        String newStatus;

        if (
                "OPEN".equalsIgnoreCase(
                        currentStatus
                )
        ) {

            newStatus =
                    "IN_REVIEW";

        } else if (
                "IN_REVIEW".equalsIgnoreCase(
                        currentStatus
                )
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
                                    ticket.getTicketId(),
                                    newStatus
                            );

            if (!success) {

                showError(
                        "Update Failed",
                        "Unable to update ticket status."
                );

                return;
            }

            ticket.setStatus(
                    newStatus
            );

            ticketTable.refresh();

            recalculateStats();

            showInfo(
                    "Status Updated",
                    "Ticket "
                            + ticket.getTicketId()
                            + " is now "
                            + newStatus
                            + "."
            );

        } catch (Exception e) {

            showError(
                    "Update Failed",
                    e.getMessage()
            );
        }
    }

    // ============================================================
    // TICKET DETAILS
    // ============================================================

    private void showTicketDetailsModal(
            ComplaintModel ticket) {

        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle(
                "Moderation Inspection"
        );

        dialog.setHeaderText(
                "Ticket: "
                        + safe(
                                ticket.getTicketId()
                        )
        );

        Label details =
                new Label(
                        "Ticket ID: "
                                + safe(
                                        ticket.getTicketId()
                                )
                                + "\n\nCategory: "
                                + safe(
                                        ticket.getCategory()
                                )
                                + "\n\nIssue: "
                                + safe(
                                        ticket.getIssueTitle()
                                )
                                + "\n\nDescription:\n"
                                + safe(
                                        ticket.getDescription()
                                )
                                + "\n\nComplainant: "
                                + safe(
                                        ticket.getComplainant()
                                )
                                + "\n\nPriority: "
                                + safe(
                                        ticket.getPriority()
                                )
                                + "\n\nStatus: "
                                + safe(
                                        ticket.getStatus()
                                )
                                + "\n\nCreated Date: "
                                + safe(
                                        ticket.getCreatedDate()
                                )
                );

        details.setWrapText(
                true
        );

        details.setMaxWidth(
                550
        );

        details.setPadding(
                new Insets(10)
        );

        ButtonType closeButton =
                new ButtonType(
                        "Close",
                        ButtonBar.ButtonData.CANCEL_CLOSE
                );

        ButtonType deleteButton =
                new ButtonType(
                        "Delete Ticket",
                        ButtonBar.ButtonData.LEFT
                );

        dialog.getDialogPane()
                .setContent(
                        details
                );

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        deleteButton,
                        closeButton
                );

        dialog.setResultConverter(
                buttonType ->
                        buttonType
        );

        dialog.showAndWait()
                .ifPresent(
                        result -> {

                            if (
                                    result == deleteButton
                            ) {

                                deleteTicket(
                                        ticket
                                );
                            }
                        }
                );
    }

    // ============================================================
    // DELETE TICKET
    // ============================================================

    private void deleteTicket(
            ComplaintModel ticket) {

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Delete Complaint"
        );

        confirmation.setHeaderText(
                "Delete ticket "
                        + ticket.getTicketId()
                        + "?"
        );

        confirmation.setContentText(
                "This will permanently remove the complaint from Firestore."
        );

        confirmation.showAndWait()
                .ifPresent(
                        result -> {

                            if (
                                    result
                                            == ButtonType.OK
                            ) {

                                try {

                                    boolean success =
                                            moderationController
                                                    .deleteComplaint(
                                                            ticket.getTicketId()
                                                    );

                                    if (!success) {

                                        showError(
                                                "Delete Failed",
                                                "Unable to delete complaint."
                                        );

                                        return;
                                    }

                                    masterTicketData.remove(
                                            ticket
                                    );

                                    recalculateStats();

                                    showInfo(
                                            "Deleted",
                                            "Ticket "
                                                    + ticket.getTicketId()
                                                    + " has been deleted."
                                    );

                                } catch (Exception e) {

                                    showError(
                                            "Delete Failed",
                                            e.getMessage()
                                    );
                                }
                            }
                        }
                );
    }

    // ============================================================
    // CREATE INTERNAL TICKET
    // ============================================================

    private void showCreateTicketModal() {

        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle(
                "Create Internal Complaint"
        );

        dialog.setHeaderText(
                "File a new moderation ticket"
        );

        GridPane form =
                new GridPane();

        form.setHgap(10);
        form.setVgap(12);
        form.setPadding(
                new Insets(10)
        );

        TextField issueTitleField =
                new TextField();

        issueTitleField.setPromptText(
                "Issue title"
        );

        ComboBox<String> categoryBox =
                new ComboBox<>();

        categoryBox.setEditable(
                true
        );

        categoryBox.getItems().addAll(
                "Billing Dispute",
                "Doctor Misbehavior",
                "Provider Misconduct",
                "Prescription Issue",
                "App Bug",
                "Fake Profile",
                "Platform Bug"
        );

        TextArea descriptionArea =
                new TextArea();

        descriptionArea.setPromptText(
                "Complaint description"
        );

        descriptionArea.setPrefRowCount(
                4
        );

        TextField complainantField =
                new TextField();

        complainantField.setPromptText(
                "Complainant"
        );

        ComboBox<String> priorityBox =
                new ComboBox<>();

        priorityBox.getItems().addAll(
                "LOW",
                "MEDIUM",
                "HIGH",
                "CRITICAL"
        );

        priorityBox.setValue(
                "MEDIUM"
        );

        form.add(
                new Label("Issue Title:"),
                0,
                0
        );

        form.add(
                issueTitleField,
                1,
                0
        );

        form.add(
                new Label("Category:"),
                0,
                1
        );

        form.add(
                categoryBox,
                1,
                1
        );

        form.add(
                new Label("Description:"),
                0,
                2
        );

        form.add(
                descriptionArea,
                1,
                2
        );

        form.add(
                new Label("Complainant:"),
                0,
                3
        );

        form.add(
                complainantField,
                1,
                3
        );

        form.add(
                new Label("Priority:"),
                0,
                4
        );

        form.add(
                priorityBox,
                1,
                4
        );

        ButtonType createButton =
                new ButtonType(
                        "Create",
                        ButtonBar.ButtonData.OK_DONE
                );

        ButtonType cancelButton =
                new ButtonType(
                        "Cancel",
                        ButtonBar.ButtonData.CANCEL_CLOSE
                );

        dialog.getDialogPane()
                .setContent(
                        form
                );

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        createButton,
                        cancelButton
                );

        dialog.showAndWait()
                .ifPresent(
                        result -> {

                            if (
                                    result == createButton
                            ) {

                                createComplaint(
                                        issueTitleField.getText(),
                                        categoryBox.getValue(),
                                        descriptionArea.getText(),
                                        complainantField.getText(),
                                        priorityBox.getValue()
                                );
                            }
                        }
                );
    }

    private void createComplaint(
            String issueTitle,
            String category,
            String description,
            String complainant,
            String priority) {

        if (
                issueTitle == null ||
                issueTitle.trim().isEmpty()
        ) {

            showError(
                    "Invalid Input",
                    "Issue title is required."
            );

            return;
        }

        if (
                category == null ||
                category.trim().isEmpty()
        ) {

            showError(
                    "Invalid Input",
                    "Category is required."
            );

            return;
        }

        String ticketId =
                "TKT-"
                        + System.currentTimeMillis();

        String createdDate =
                LocalDateTime.now()
                        .format(
                                DATE_FORMATTER
                        );

        ComplaintModel complaint =
                new ComplaintModel(
                        ticketId,
                        category.trim(),
                        issueTitle.trim(),
                        safe(description).trim(),
                        safe(complainant).trim(),
                        priority == null
                                ? "MEDIUM"
                                : priority,
                        "OPEN",
                        createdDate
                );

        try {

            boolean success =
                    moderationController
                            .createComplaint(
                                    complaint
                            );

            if (!success) {

                showError(
                        "Creation Failed",
                        "Unable to create complaint."
                );

                return;
            }

            masterTicketData.add(
                    0,
                    complaint
            );

            populateFilters();

            recalculateStats();

            showInfo(
                    "Ticket Created",
                    "Ticket "
                            + ticketId
                            + " has been created successfully."
            );

        } catch (Exception e) {

            showError(
                    "Creation Failed",
                    e.getMessage()
            );
        }
    }

    // ============================================================
    // EXPORT ACTUAL FIRESTORE DATA
    // ============================================================

    private void exportComplaints() {

        if (
                masterTicketData == null ||
                masterTicketData.isEmpty()
        ) {

            showInfo(
                    "Nothing to Export",
                    "There are no complaints available."
            );

            return;
        }

        FileChooser fileChooser =
                new FileChooser();

        fileChooser.setTitle(
                "Export Complaint Report"
        );

        fileChooser.setInitialFileName(
                "complaints-report.csv"
        );

        fileChooser.getExtensionFilters()
                .add(
                        new FileChooser.ExtensionFilter(
                                "CSV Files",
                                "*.csv"
                        )
                );

        File file =
                fileChooser.showSaveDialog(
                        stage
                );

        if (file == null) {
            return;
        }

        try (
                FileWriter writer =
                        new FileWriter(file)
        ) {

            writer.append(
                    "Ticket ID,Category,Issue Title,Description,"
                            + "Complainant,Priority,Status,Created Date\n"
            );

            for (
                    ComplaintModel ticket :
                    masterTicketData
            ) {

                writer.append(
                        csv(ticket.getTicketId())
                );

                writer.append(",");

                writer.append(
                        csv(ticket.getCategory())
                );

                writer.append(",");

                writer.append(
                        csv(ticket.getIssueTitle())
                );

                writer.append(",");

                writer.append(
                        csv(ticket.getDescription())
                );

                writer.append(",");

                writer.append(
                        csv(ticket.getComplainant())
                );

                writer.append(",");

                writer.append(
                        csv(ticket.getPriority())
                );

                writer.append(",");

                writer.append(
                        csv(ticket.getStatus())
                );

                writer.append(",");

                writer.append(
                        csv(ticket.getCreatedDate())
                );

                writer.append("\n");
            }

            writer.flush();

            showInfo(
                    "Export Successful",
                    "Complaint report exported successfully."
            );

        } catch (Exception e) {

            showError(
                    "Export Failed",
                    e.getMessage()
            );
        }
    }

    private String csv(
            String value) {

        String safeValue =
                safe(value)
                        .replace(
                                "\"",
                                "\"\""
                        );

        return "\"" + safeValue + "\"";
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private String safe(
            String value) {

        return value == null
                ? ""
                : value;
    }

    private void showInfo(
            String title,
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(
                title
        );

        alert.setHeaderText(
                null
        );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }

    private void showError(
            String title,
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle(
                title
        );

        alert.setHeaderText(
                null
        );

        alert.setContentText(
                message == null ||
                        message.trim().isEmpty()
                        ? "An unexpected error occurred."
                        : message
        );

        alert.showAndWait();
    }
}