package com.healthsphere.view.admin;

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

public class ComplaintsManagementView {

    private final Stage stage;
    private TableView<ComplaintModel> ticketTable;
    private ObservableList<ComplaintModel> masterTicketData;
    private FilteredList<ComplaintModel> filteredData;

    private Label totalTicketsLabel;
    private Label openTicketsLabel;
    private Label inReviewTicketsLabel;
    private Label avgResolutionTimeLabel;
    private BarChart<String, Number> categoryChart;

    public ComplaintsManagementView(Stage stage) {
        this.stage = stage;
    }

    /**
     * Creates and returns a new Scene wrapping the root View node.
     */
    public Scene getScene() {
        return new Scene(getView());
    }

    /**
     * Builds and returns the main View container as a Parent node.
     */
    public Parent getView() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #F8FAFC;");

        // 1. Top Header & Action Controls
        HBox header = createHeader();

        // 2. Overview Stat Cards
        HBox kpiSection = createKPISection();

        // 3. Search & Filter Bar
        HBox filterBar = createFilterBar();

        // 4. Main Body: Left (Tickets Table) & Right (Category Graph + Quick Insights)
        GridPane contentGrid = new GridPane();
        contentGrid.setHgap(20);
        contentGrid.setVgap(20);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(65);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(35);
        contentGrid.getColumnConstraints().addAll(col1, col2);

        VBox tableContainer = createTicketTableCard();
        VBox sidebarContainer = createSidebarAnalyticsCard();

        contentGrid.add(tableContainer, 0, 0);
        contentGrid.add(sidebarContainer, 1, 0);

        root.getChildren().addAll(header, kpiSection, filterBar, contentGrid);

        // Load Sample Complaint Data
        loadComplaintData();

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
        Label title = new Label("Reports & Content Moderation Desk");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#0F172A"));

        Label subTitle = new Label("Investigate user tickets, disputed consultation charges, provider issues, and audit logs.");
        subTitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        subTitle.setTextFill(Color.web("#64748B"));

        titleBox.getChildren().addAll(title, subTitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button exportBtn = new Button("Export Moderation Report");
        exportBtn.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #334155; -fx-border-color: #CBD5E1; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 8 16; -fx-font-weight: bold; -fx-cursor: hand;");
        exportBtn.setOnAction(e -> showInfo("Report Exported", "Complaints & moderation summary log has been saved to CSV format."));

        Button newTicketBtn = new Button("+ File Internal Ticket");
        newTicketBtn.setStyle("-fx-background-color: #4F46E5; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 8px; -fx-cursor: hand;");
        newTicketBtn.setOnAction(e -> showCreateTicketModal());

        HBox buttonGroup = new HBox(12, exportBtn, newTicketBtn);
        buttonGroup.setAlignment(Pos.CENTER_RIGHT);

        header.getChildren().addAll(titleBox, spacer, buttonGroup);
        return header;
    }

    private HBox createKPISection() {
        HBox section = new HBox(16);
        section.setAlignment(Pos.CENTER);

        totalTicketsLabel = new Label("0");
        openTicketsLabel = new Label("0");
        inReviewTicketsLabel = new Label("0");
        avgResolutionTimeLabel = new Label("3.2 hrs");

        VBox totalCard = createKpiCard("Total Tickets", totalTicketsLabel, "All time submissions", "#4F46E5");
        VBox openCard = createKpiCard("Open Tickets", openTicketsLabel, "Action required", "#DC2626");
        VBox reviewCard = createKpiCard("In Review", inReviewTicketsLabel, "Under investigation", "#D97706");
        VBox avgTimeCard = createKpiCard("Avg Resolution", avgResolutionTimeLabel, "Target: < 6.0 hrs", "#059669");

        HBox.setHgrow(totalCard, Priority.ALWAYS);
        HBox.setHgrow(openCard, Priority.ALWAYS);
        HBox.setHgrow(reviewCard, Priority.ALWAYS);
        HBox.setHgrow(avgTimeCard, Priority.ALWAYS);

        section.getChildren().addAll(totalCard, openCard, reviewCard, avgTimeCard);
        return section;
    }

    private VBox createKpiCard(String title, Label valueLabel, String subtext, String accentColorHex) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(16));
        card.setStyle(
            "-fx-background-color: #FFFFFF; " +
            "-fx-background-radius: 12px; " +
            "-fx-border-color: #E2E8F0; " +
            "-fx-border-radius: 12px;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        titleLabel.setTextFill(Color.web("#64748B"));

        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        valueLabel.setTextFill(Color.web("#0F172A"));

        Label subLabel = new Label(subtext);
        subLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
        subLabel.setTextFill(Color.web(accentColorHex));

        card.getChildren().addAll(titleLabel, valueLabel, subLabel);
        return card;
    }

    private HBox createFilterBar() {
        HBox bar = new HBox(14);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(14));
        bar.setStyle(
            "-fx-background-color: #FFFFFF; " +
            "-fx-background-radius: 10px; " +
            "-fx-border-color: #E2E8F0; " +
            "-fx-border-radius: 10px;"
        );

        TextField searchInput = new TextField();
        searchInput.setPromptText("🔍 Search Ticket ID, Complainant, or Issue...");
        searchInput.setPrefWidth(320);
        searchInput.setStyle(
            "-fx-background-color: #F8FAFC; " +
            "-fx-text-fill: #0F172A; " +
            "-fx-border-color: #CBD5E1; " +
            "-fx-border-radius: 6px; " +
            "-fx-padding: 8px 12px;"
        );

        ComboBox<String> priorityFilter = new ComboBox<>();
        priorityFilter.getItems().addAll("All Priorities", "HIGH", "MEDIUM", "LOW");
        priorityFilter.setValue("All Priorities");
        priorityFilter.setStyle("-fx-background-color: #F8FAFC; -fx-text-fill: #0F172A; -fx-border-color: #CBD5E1; -fx-border-radius: 6px;");

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All Statuses", "OPEN", "IN_REVIEW", "RESOLVED");
        statusFilter.setValue("All Statuses");
        statusFilter.setStyle("-fx-background-color: #F8FAFC; -fx-text-fill: #0F172A; -fx-border-color: #CBD5E1; -fx-border-radius: 6px;");

        Runnable applyFilters = () -> {
            String query = searchInput.getText().toLowerCase().trim();
            String selectedPriority = priorityFilter.getValue();
            String selectedStatus = statusFilter.getValue();

            filteredData.setPredicate(ticket -> {
                boolean matchesSearch = query.isEmpty() ||
                        ticket.getTicketId().toLowerCase().contains(query) ||
                        ticket.getComplainant().toLowerCase().contains(query) ||
                        ticket.getIssueTitle().toLowerCase().contains(query) ||
                        ticket.getCategory().toLowerCase().contains(query);

                boolean matchesPriority = selectedPriority.equals("All Priorities") || ticket.getPriority().equalsIgnoreCase(selectedPriority);
                boolean matchesStatus = selectedStatus.equals("All Statuses") || ticket.getStatus().equalsIgnoreCase(selectedStatus);

                return matchesSearch && matchesPriority && matchesStatus;
            });
        };

        searchInput.textProperty().addListener((obs, oldVal, newVal) -> applyFilters.run());
        priorityFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters.run());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters.run());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button resetBtn = new Button("Reset Filters");
        resetBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 8 14; -fx-cursor: hand;");
        resetBtn.setOnAction(e -> {
            searchInput.clear();
            priorityFilter.setValue("All Priorities");
            statusFilter.setValue("All Statuses");
        });

        bar.getChildren().addAll(searchInput, priorityFilter, statusFilter, spacer, resetBtn);
        return bar;
    }

    @SuppressWarnings("unchecked")
    private VBox createTicketTableCard() {
        VBox container = new VBox(12);
        container.setPadding(new Insets(16));
        container.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px;");

        Label cardTitle = new Label("Active Moderation Tickets");
        cardTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        cardTitle.setTextFill(Color.web("#0F172A"));

        ticketTable = new TableView<>();
        ticketTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ticketTable.setStyle("-fx-background-color: transparent;");
        ticketTable.setPrefHeight(400);

        TableColumn<ComplaintModel, String> idCol = new TableColumn<>("Ticket ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("ticketId"));
        idCol.setPrefWidth(90);

        TableColumn<ComplaintModel, String> issueCol = new TableColumn<>("Issue & Complainant");
        issueCol.setCellValueFactory(new PropertyValueFactory<>("issueTitle"));
        issueCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String titleText, boolean empty) {
                super.updateItem(titleText, empty);
                if (empty || titleText == null) {
                    setGraphic(null);
                } else {
                    ComplaintModel ticket = getTableView().getItems().get(getIndex());
                    VBox box = new VBox(3);

                    Label titleLbl = new Label(titleText);
                    titleLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
                    titleLbl.setTextFill(Color.web("#0F172A"));

                    Label subLbl = new Label(ticket.getComplainant() + " • " + ticket.getCategory());
                    subLbl.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
                    subLbl.setTextFill(Color.web("#64748B"));

                    box.getChildren().addAll(titleLbl, subLbl);
                    setGraphic(box);
                }
            }
        });

        TableColumn<ComplaintModel, String> priorityCol = new TableColumn<>("Priority");
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        priorityCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String priority, boolean empty) {
                super.updateItem(priority, empty);
                if (empty || priority == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(priority.toUpperCase());
                    badge.setPadding(new Insets(4, 10, 4, 10));
                    badge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));

                    switch (priority.toUpperCase()) {
                        case "HIGH" -> badge.setStyle("-fx-background-color: #FFE4E6; -fx-text-fill: #E11D48; -fx-background-radius: 20px;");
                        case "MEDIUM" -> badge.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #D97706; -fx-background-radius: 20px;");
                        default -> badge.setStyle("-fx-background-color: #EEF2FF; -fx-text-fill: #4F46E5; -fx-background-radius: 20px;");
                    }
                    setGraphic(badge);
                }
            }
        });

        TableColumn<ComplaintModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(status.replace("_", " "));
                    badge.setPadding(new Insets(4, 10, 4, 10));
                    badge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));

                    switch (status.toUpperCase()) {
                        case "RESOLVED" -> badge.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #15803D; -fx-background-radius: 6px;");
                        case "IN_REVIEW" -> badge.setStyle("-fx-background-color: #E0E7FF; -fx-text-fill: #3730A3; -fx-background-radius: 6px;");
                        default -> badge.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #B91C1C; -fx-background-radius: 6px;");
                    }
                    setGraphic(badge);
                }
            }
        });

        TableColumn<ComplaintModel, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button inspectBtn = new Button("Inspect");
            private final Button actionBtn = new Button();
            private final HBox btnGroup = new HBox(6, inspectBtn, actionBtn);

            {
                btnGroup.setAlignment(Pos.CENTER);
                inspectBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #334155; -fx-cursor: hand; -fx-font-size: 11px; -fx-background-radius: 4px;");

                inspectBtn.setOnAction(e -> {
                    ComplaintModel ticket = getTableView().getItems().get(getIndex());
                    showTicketDetailsModal(ticket);
                });

                actionBtn.setOnAction(e -> {
                    ComplaintModel ticket = getTableView().getItems().get(getIndex());
                    if ("RESOLVED".equalsIgnoreCase(ticket.getStatus())) {
                        ticket.setStatus("OPEN");
                    } else if ("OPEN".equalsIgnoreCase(ticket.getStatus())) {
                        ticket.setStatus("IN_REVIEW");
                    } else {
                        ticket.setStatus("RESOLVED");
                    }
                    ticketTable.refresh();
                    recalculateStats();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    ComplaintModel ticket = getTableView().getItems().get(getIndex());
                    if ("RESOLVED".equalsIgnoreCase(ticket.getStatus())) {
                        actionBtn.setText("Re-open");
                        actionBtn.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B; -fx-cursor: hand; -fx-font-size: 11px; -fx-background-radius: 4px;");
                    } else if ("OPEN".equalsIgnoreCase(ticket.getStatus())) {
                        actionBtn.setText("Review");
                        actionBtn.setStyle("-fx-background-color: #E0E7FF; -fx-text-fill: #3730A3; -fx-cursor: hand; -fx-font-size: 11px; -fx-background-radius: 4px;");
                    } else {
                        actionBtn.setText("Resolve");
                        actionBtn.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #166534; -fx-cursor: hand; -fx-font-size: 11px; -fx-background-radius: 4px;");
                    }
                    setGraphic(btnGroup);
                }
            }
        });

        ticketTable.getColumns().addAll(idCol, issueCol, priorityCol, statusCol, actionCol);
        container.getChildren().addAll(cardTitle, ticketTable);
        return container;
    }

    private VBox createSidebarAnalyticsCard() {
        VBox container = new VBox(16);

        // Chart Card
        VBox chartCard = new VBox(12);
        chartCard.setPadding(new Insets(16));
        chartCard.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px;");

        Label chartTitle = new Label("Complaints by Category");
        chartTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        chartTitle.setTextFill(Color.web("#0F172A"));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Tickets");

        categoryChart = new BarChart<>(xAxis, yAxis);
        categoryChart.setPrefHeight(220);
        categoryChart.setLegendVisible(false);
        categoryChart.setAnimated(false);

        chartCard.getChildren().addAll(chartTitle, categoryChart);

        // Moderation SLA Guidelines Box
        VBox slaCard = new VBox(10);
        slaCard.setPadding(new Insets(16));
        slaCard.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 12px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px;");

        Label slaTitle = new Label("Moderation SLA Policy");
        slaTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        slaTitle.setTextFill(Color.web("#0F172A"));

        Label slaDesc1 = new Label("• High Priority: Initial response within 1 hour.");
        slaDesc1.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
        slaDesc1.setTextFill(Color.web("#475569"));

        Label slaDesc2 = new Label("• Billing Disputes: Refund clearance within 24 hours.");
        slaDesc2.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
        slaDesc2.setTextFill(Color.web("#475569"));

        Label slaDesc3 = new Label("• Doctor Complaints: escalates to Medical Ethics Board.");
        slaDesc3.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
        slaDesc3.setTextFill(Color.web("#475569"));

        slaCard.getChildren().addAll(slaTitle, slaDesc1, slaDesc2, slaDesc3);

        container.getChildren().addAll(chartCard, slaCard);
        return container;
    }

    // ------------------------------------------------------------------------
    // DATA LOAD & LOGIC
    // ------------------------------------------------------------------------

    private void loadComplaintData() {
        masterTicketData = FXCollections.observableArrayList(
            new ComplaintModel("TKT-8801", "Billing Dispute", "Incorrect Billing Charge", "Patient reported double charge of ₹1,200 for Tele-consultation.", "Ananya Verma", "HIGH", "OPEN", "2026-08-10 09:30"),
            new ComplaintModel("TKT-8802", "Provider Misconduct", "Doctor No-Show", "Doctor missed scheduled Tele-consultation appointment without notice.", "Vikram Malhotra", "MEDIUM", "IN_REVIEW", "2026-08-11 11:15"),
            new ComplaintModel("TKT-8803", "Prescription Issue", "Missing E-Prescription", "Prescription not generated after completed consultation session.", "Saurabh Deshmukh", "HIGH", "OPEN", "2026-08-11 14:00"),
            new ComplaintModel("TKT-8804", "Platform Bug", "Video Call Lag & Disconnect", "Audio/Video lost connection repeatedly during appointment.", "Pooja Hegde", "LOW", "RESOLVED", "2026-08-09 16:45"),
            new ComplaintModel("TKT-8805", "Billing Dispute", "Refund Pending", "Refund initiated 5 days ago but not credited to UPI account.", "Rohan Gupta", "MEDIUM", "IN_REVIEW", "2026-08-12 08:20")
        );

        filteredData = new FilteredList<>(masterTicketData, p -> true);
        ticketTable.setItems(filteredData);

        recalculateStats();
    }

    private void recalculateStats() {
        int total = masterTicketData.size();
        long open = masterTicketData.stream().filter(t -> "OPEN".equalsIgnoreCase(t.getStatus())).count();
        long review = masterTicketData.stream().filter(t -> "IN_REVIEW".equalsIgnoreCase(t.getStatus())).count();

        totalTicketsLabel.setText(String.valueOf(total));
        openTicketsLabel.setText(String.valueOf(open));
        inReviewTicketsLabel.setText(String.valueOf(review));

        updateBarChart();
    }

    private void updateBarChart() {
        categoryChart.getData().clear();

        long billing = masterTicketData.stream().filter(t -> "Billing Dispute".equalsIgnoreCase(t.getCategory())).count();
        long provider = masterTicketData.stream().filter(t -> "Provider Misconduct".equalsIgnoreCase(t.getCategory())).count();
        long rx = masterTicketData.stream().filter(t -> "Prescription Issue".equalsIgnoreCase(t.getCategory())).count();
        long bug = masterTicketData.stream().filter(t -> "Platform Bug".equalsIgnoreCase(t.getCategory())).count();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Billing", billing));
        series.getData().add(new XYChart.Data<>("Provider", provider));
        series.getData().add(new XYChart.Data<>("Rx Issue", rx));
        series.getData().add(new XYChart.Data<>("Platform", bug));

        categoryChart.getData().add(series);
    }

    private void showTicketDetailsModal(ComplaintModel ticket) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Ticket Moderation Inspection");
        dialog.setHeaderText("Ticket Details: " + ticket.getTicketId() + " [" + ticket.getPriority() + " PRIORITY]");

        dialog.setContentText(
            "Category: " + ticket.getCategory() + "\n" +
            "Issue: " + ticket.getIssueTitle() + "\n" +
            "Complainant: " + ticket.getComplainant() + "\n" +
            "Filed Date: " + ticket.getCreatedDate() + "\n" +
            "Current Status: " + ticket.getStatus() + "\n\n" +
            "Complaint Summary:\n" + ticket.getDescription() + "\n\n" +
            "Moderation Actions:\n" +
            "• Checked logs: Session ID #SESS-9921 verification completed.\n" +
            "• Resolution Path: Payment gateway refund or doctor reassignment."
        );
        dialog.showAndWait();
    }

    private void showCreateTicketModal() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("File Internal Moderation Ticket");
        dialog.setHeaderText("Create new Complaint / Support Ticket");
        dialog.setContentText("Enter Issue Title:");

        dialog.showAndWait().ifPresent(title -> {
            if (!title.trim().isEmpty()) {
                String newId = "TKT-" + (8800 + masterTicketData.size() + 1);
                String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                ComplaintModel newTicket = new ComplaintModel(newId, "Platform Bug", title, "Internal ticket created by Admin desk.", "Admin Desk", "MEDIUM", "OPEN", now);
                masterTicketData.add(0, newTicket);
                recalculateStats();
                showInfo("Ticket Created", "New ticket " + newId + " has been added to the moderation queue.");
            }
        });
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // ------------------------------------------------------------------------
    // DATA MODEL CLASS
    // ------------------------------------------------------------------------

    public static class ComplaintModel {
        private final String ticketId;
        private final String category;
        private final String issueTitle;
        private final String description;
        private final String complainant;
        private final String priority;
        private String status;
        private final String createdDate;

        public ComplaintModel(String ticketId, String category, String issueTitle, String description, String complainant, String priority, String status, String createdDate) {
            this.ticketId = ticketId;
            this.category = category;
            this.issueTitle = issueTitle;
            this.description = description;
            this.complainant = complainant;
            this.priority = priority;
            this.status = status;
            this.createdDate = createdDate;
        }

        public String getTicketId() { return ticketId; }
        public String getCategory() { return category; }
        public String getIssueTitle() { return issueTitle; }
        public String getDescription() { return description; }
        public String getComplainant() { return complainant; }
        public String getPriority() { return priority; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getCreatedDate() { return createdDate; }
    }
}