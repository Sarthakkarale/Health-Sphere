package com.healthsphere.view.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class HospitalManagementView extends ScrollPane {

    private Stage primaryStage;
    private TableView<HospitalModel> hospitalTable;
    private ObservableList<HospitalModel> masterHospitalData;
    private FilteredList<HospitalModel> filteredData;
    
    private Label pendingCountLabel;
    private Label verifiedCountLabel;
    private BarChart<String, Number> cityBarChart;

    public HospitalManagementView() {
        this(null);
    }

    public HospitalManagementView(Stage stage) {
        this.primaryStage = stage;

        setFitToWidth(true);
        setStyle("-fx-background-color: #0F172A; -fx-background: #0F172A;");

        VBox mainContainer = new VBox(25);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setStyle("-fx-background-color: #0F172A;");

        // 1. Header Section
        VBox header = createHeader();

        // 2. Verification Pipeline Stats + City Bar Chart
        HBox topAnalyticsSection = createTopAnalyticsSection();

        // 3. Search and Action Filter Bar
        HBox filterBar = createFilterBar();

        // 4. Data Table Container
        VBox tableContainer = createTableContainer();

        mainContainer.getChildren().addAll(header, topAnalyticsSection, filterBar, tableContainer);
        setContent(mainContainer);

        // Load Sample Data
        loadHospitalData();
    }

    public Parent getView() {
        return this;
    }

    private VBox createHeader() {
        VBox header = new VBox(5);
        Label title = new Label("Hospital Verification & Regulatory Oversight");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Inspect healthcare facility credentials, compliance certifications, bed capacity, and authorization requests.");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        subtitle.setTextFill(Color.web("#94A3B8"));

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private HBox createTopAnalyticsSection() {
        HBox section = new HBox(20);
        section.setAlignment(Pos.CENTER);

        // Stats Box
        VBox statsBox = new VBox(15);
        HBox.setHgrow(statsBox, Priority.ALWAYS);

        pendingCountLabel = new Label("18");
        verifiedCountLabel = new Label("124");

        VBox pendingCard = createStatCard("Pending Verifications", pendingCountLabel, "Action Required Immediately", "#F59E0B");
        VBox verifiedCard = createStatCard("Verified Facilities", verifiedCountLabel, "100% Compliance Clear", "#10B981");
        VBox rejectedCard = createStatCard("Rejected Applications", new Label("07"), "Failed Document Audit", "#EF4444");

        HBox topRow = new HBox(15, pendingCard, verifiedCard);
        HBox.setHgrow(pendingCard, Priority.ALWAYS);
        HBox.setHgrow(verifiedCard, Priority.ALWAYS);
        HBox.setHgrow(rejectedCard, Priority.ALWAYS);

        statsBox.getChildren().addAll(topRow, rejectedCard);

        // Bar Chart Card
        VBox chartCard = new VBox(10);
        chartCard.setPadding(new Insets(15));
        chartCard.setMinWidth(420);
        chartCard.setStyle(
            "-fx-background-color: #1E293B; " +
            "-fx-background-radius: 12px; " +
            "-fx-border-color: #334155; " +
            "-fx-border-radius: 12px;"
        );

        Label chartTitle = new Label("Regional Distribution (Hospitals)");
        chartTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        chartTitle.setTextFill(Color.WHITE);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickLabelFill(Color.web("#94A3B8"));
        yAxis.setTickLabelFill(Color.web("#94A3B8"));

        cityBarChart = new BarChart<>(xAxis, yAxis);
        cityBarChart.setPrefHeight(170);
        cityBarChart.setLegendVisible(false);
        cityBarChart.setAnimated(true);

        chartCard.getChildren().addAll(chartTitle, cityBarChart);

        section.getChildren().addAll(statsBox, chartCard);
        return section;
    }

    private VBox createStatCard(String title, Label valueLabel, String subtext, String accentColor) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(15));
        card.setStyle(
            "-fx-background-color: #1E293B; " +
            "-fx-background-radius: 12px; " +
            "-fx-border-color: #334155; " +
            "-fx-border-radius: 12px;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        titleLabel.setTextFill(Color.web("#94A3B8"));

        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        valueLabel.setTextFill(Color.WHITE);

        Label subLabel = new Label(subtext);
        subLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
        subLabel.setTextFill(Color.web(accentColor));

        card.getChildren().addAll(titleLabel, valueLabel, subLabel);
        return card;
    }

    private HBox createFilterBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(15));
        bar.setStyle(
            "-fx-background-color: #1E293B; " +
            "-fx-background-radius: 10px; " +
            "-fx-border-color: #334155; " +
            "-fx-border-radius: 10px;"
        );

        TextField searchInput = new TextField();
        searchInput.setPromptText("🔍 Search by Hospital Name, License No, or City...");
        searchInput.setPrefWidth(350);
        searchInput.setStyle(
            "-fx-background-color: #0F172A; " +
            "-fx-text-fill: white; " +
            "-fx-border-color: #475569; " +
            "-fx-border-radius: 6px; " +
            "-fx-padding: 8px 12px;"
        );

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All Status", "PENDING", "VERIFIED", "REJECTED");
        statusFilter.setValue("All Status");
        statusFilter.setStyle("-fx-background-color: #0F172A; -fx-mark-color: white;");

        Runnable applyFilter = () -> {
            String query = searchInput.getText().toLowerCase().trim();
            String status = statusFilter.getValue();

            filteredData.setPredicate(hosp -> {
                boolean matchesQuery = query.isEmpty() ||
                        hosp.getName().toLowerCase().contains(query) ||
                        hosp.getLicenseNo().toLowerCase().contains(query) ||
                        hosp.getCity().toLowerCase().contains(query);

                boolean matchesStatus = status.equals("All Status") || hosp.getStatus().equalsIgnoreCase(status);

                return matchesQuery && matchesStatus;
            });
        };

        searchInput.textProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button registerHospBtn = new Button("+ Register Facility");
        registerHospBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        registerHospBtn.setStyle(
            "-fx-background-color: #10B981; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 6px; " +
            "-fx-padding: 8px 16px; " +
            "-fx-cursor: hand;"
        );
        registerHospBtn.setOnAction(e -> showRegisterHospitalDialog());

        bar.getChildren().addAll(searchInput, statusFilter, spacer, registerHospBtn);
        return bar;
    }

    @SuppressWarnings("unchecked")
    private VBox createTableContainer() {
        VBox container = new VBox();
        container.setStyle("-fx-background-color: #1E293B; -fx-background-radius: 12px; -fx-border-color: #334155; -fx-border-radius: 12px;");

        hospitalTable = new TableView<>();
        hospitalTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        hospitalTable.setStyle("-fx-background-color: transparent;");
        hospitalTable.setPrefHeight(400);

        // Hospital Name Column
        TableColumn<HospitalModel, String> nameCol = new TableColumn<>("Facility Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty || name == null) {
                    setGraphic(null);
                } else {
                    HospitalModel hosp = getTableView().getItems().get(getIndex());
                    HBox box = new HBox(12);
                    box.setAlignment(Pos.CENTER_LEFT);

                    StackPane icon = createHospitalBadge(hosp.getName().substring(0, 1));

                    VBox textContainer = new VBox(2);
                    Label nameLbl = new Label(name);
                    nameLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                    nameLbl.setTextFill(Color.WHITE);

                    Label licenseLbl = new Label("Lic: " + hosp.getLicenseNo() + " • " + hosp.getCity());
                    licenseLbl.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
                    licenseLbl.setTextFill(Color.web("#94A3B8"));

                    textContainer.getChildren().addAll(nameLbl, licenseLbl);
                    box.getChildren().addAll(icon, textContainer);
                    setGraphic(box);
                }
            }
        });

        // Bed Capacity
        TableColumn<HospitalModel, Integer> bedsCol = new TableColumn<>("Beds / ICU");
        bedsCol.setCellValueFactory(new PropertyValueFactory<>("bedCapacity"));
        bedsCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer beds, boolean empty) {
                super.updateItem(beds, empty);
                if (empty || beds == null) {
                    setText(null);
                } else {
                    setText(beds + " Beds");
                    setTextFill(Color.web("#CBD5E1"));
                    setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
                }
            }
        });

        // Verification Status Badge
        TableColumn<HospitalModel, String> statusCol = new TableColumn<>("Verification Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(status);
                    badge.setPadding(new Insets(4, 10, 4, 10));
                    badge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));

                    switch (status.toUpperCase()) {
                        case "VERIFIED" -> badge.setStyle("-fx-background-color: #064E3B; -fx-text-fill: #10B981; -fx-background-radius: 20px;");
                        case "REJECTED" -> badge.setStyle("-fx-background-color: #7F1D1D; -fx-text-fill: #EF4444; -fx-background-radius: 20px;");
                        default -> badge.setStyle("-fx-background-color: #78350F; -fx-text-fill: #FBBF24; -fx-background-radius: 20px;");
                    }
                    setGraphic(badge);
                }
            }
        });

        // Applied Date
        TableColumn<HospitalModel, String> dateCol = new TableColumn<>("Applied Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("appliedDate"));

        // Actions
        TableColumn<HospitalModel, Void> actionCol = new TableColumn<>("Actions & Audit");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button inspectBtn = new Button("Inspect Docs");
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final HBox btnGroup = new HBox(6, inspectBtn, approveBtn, rejectBtn);

            {
                btnGroup.setAlignment(Pos.CENTER);
                inspectBtn.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11px;");
                approveBtn.setStyle("-fx-background-color: #064E3B; -fx-text-fill: #34D399; -fx-cursor: hand; -fx-font-size: 11px;");
                rejectBtn.setStyle("-fx-background-color: #7F1D1D; -fx-text-fill: #FCA5A5; -fx-cursor: hand; -fx-font-size: 11px;");

                inspectBtn.setOnAction(e -> {
                    HospitalModel hosp = getTableView().getItems().get(getIndex());
                    showDocumentInspectionModal(hosp);
                });

                approveBtn.setOnAction(e -> {
                    HospitalModel hosp = getTableView().getItems().get(getIndex());
                    hosp.setStatus("VERIFIED");
                    hospitalTable.refresh();
                    updateCounters();
                });

                rejectBtn.setOnAction(e -> {
                    HospitalModel hosp = getTableView().getItems().get(getIndex());
                    hosp.setStatus("REJECTED");
                    hospitalTable.refresh();
                    updateCounters();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnGroup);
                }
            }
        });

        hospitalTable.getColumns().addAll(nameCol, bedsCol, statusCol, dateCol, actionCol);
        container.getChildren().add(hospitalTable);
        return container;
    }

    private StackPane createHospitalBadge(String letter) {
        Circle circle = new Circle(16);
        circle.setFill(Color.web("#1E1B4B"));
        circle.setStroke(Color.web("#818CF8"));
        circle.setStrokeWidth(1.5);

        Label label = new Label(letter);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        label.setTextFill(Color.web("#C7D2FE"));

        return new StackPane(circle, label);
    }

    private void loadHospitalData() {
        masterHospitalData = FXCollections.observableArrayList(
            new HospitalModel("HOSP-801", "City Care Superspeciality Hospital", "MH-MUM-8890", "Mumbai", 350, "PENDING", "2026-08-01"),
            new HospitalModel("HOSP-802", "Ruby Hall Medical Center", "MH-PUN-1044", "Pune", 500, "VERIFIED", "2026-07-15"),
            new HospitalModel("HOSP-803", "Orange City Care Hospital", "MH-NAG-3321", "Nagpur", 180, "PENDING", "2026-08-05"),
            new HospitalModel("HOSP-804", "Apex Multi-Speciality Clinic", "MH-NAS-9981", "Nashik", 90, "REJECTED", "2026-07-28"),
            new HospitalModel("HOSP-805", "Sahyadri Health Campus", "MH-PUN-7712", "Pune", 420, "VERIFIED", "2026-06-19")
        );

        filteredData = new FilteredList<>(masterHospitalData, p -> true);
        hospitalTable.setItems(filteredData);

        updateCounters();
        loadCityChartData();
    }

    private void updateCounters() {
        long pending = masterHospitalData.stream().filter(h -> h.getStatus().equalsIgnoreCase("PENDING")).count();
        long verified = masterHospitalData.stream().filter(h -> h.getStatus().equalsIgnoreCase("VERIFIED")).count();

        pendingCountLabel.setText(String.valueOf(pending));
        verifiedCountLabel.setText(String.valueOf(verified));
    }

    private void loadCityChartData() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Mumbai", 35));
        series.getData().add(new XYChart.Data<>("Pune", 48));
        series.getData().add(new XYChart.Data<>("Nagpur", 22));
        series.getData().add(new XYChart.Data<>("Nashik", 19));

        cityBarChart.getData().clear();
        cityBarChart.getData().add(series);
    }

    private void showDocumentInspectionModal(HospitalModel hosp) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Regulatory Document Inspection");
        dialog.setHeaderText("Verification Audit: " + hosp.getName());
        dialog.setContentText(
            "📜 License Number: " + hosp.getLicenseNo() + "\n" +
            "📍 Location: " + hosp.getCity() + ", Maharashtra\n" +
            "🛏️ Registered Beds: " + hosp.getBedCapacity() + "\n" +
            "📅 Application Date: " + hosp.getAppliedDate() + "\n\n" +
            "✓ Fire & Safety Clearance Certificate: VERIFIED (PDF Valid)\n" +
            "✓ Medical Council Accreditation: OK\n" +
            "✓ ICU Emergency Backup Protocol: COMPLIANT"
        );
        dialog.showAndWait();
    }

    private void showRegisterHospitalDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Hospital Onboarding");
        dialog.setHeaderText("Register Hospital into HealthSphere Regulatory Network");
        dialog.setContentText("Enter Hospital Name:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                String newId = "HOSP-" + (800 + masterHospitalData.size() + 1);
                HospitalModel newHosp = new HospitalModel(newId, name, "MH-NEW-" + (1000 + masterHospitalData.size()), "Pune", 150, "PENDING", "2026-08-11");
                masterHospitalData.add(newHosp);
                updateCounters();
            }
        });
    }

    // --- Inner Model Class ---
    public static class HospitalModel {
        private final String hospitalId;
        private final String name;
        private final String licenseNo;
        private final String city;
        private final int bedCapacity;
        private String status;
        private final String appliedDate;

        public HospitalModel(String hospitalId, String name, String licenseNo, String city, int bedCapacity, String status, String appliedDate) {
            this.hospitalId = hospitalId;
            this.name = name;
            this.licenseNo = licenseNo;
            this.city = city;
            this.bedCapacity = bedCapacity;
            this.status = status;
            this.appliedDate = appliedDate;
        }

        public String getHospitalId() { return hospitalId; }
        public String getName() { return name; }
        public String getLicenseNo() { return licenseNo; }
        public String getCity() { return city; }
        public int getBedCapacity() { return bedCapacity; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getAppliedDate() { return appliedDate; }
    }
}