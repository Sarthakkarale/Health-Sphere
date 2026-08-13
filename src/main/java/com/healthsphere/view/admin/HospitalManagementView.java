package com.healthsphere.view.admin;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HospitalManagementView extends ScrollPane {

    private Stage primaryStage;
    private TableView<HospitalModel> hospitalTable;
    private ObservableList<HospitalModel> masterHospitalData;
    private FilteredList<HospitalModel> filteredData;

    private Label totalCountLabel;
    private Label pendingCountLabel;
    private Label verifiedCountLabel;
    private Label rejectedCountLabel;
    private BarChart<String, Number> cityBarChart;

    public HospitalManagementView() {
        this(null);
    }

    public HospitalManagementView(Stage stage) {
        this.primaryStage = stage;

        setFitToWidth(true);
        setStyle("-fx-background-color: #F8FAFC; -fx-background: #F8FAFC; -fx-border-color: transparent;");

        VBox mainContainer = new VBox(24);
        mainContainer.setPadding(new Insets(28));
        mainContainer.setStyle("-fx-background-color: #F8FAFC;");

        // 1. Header Section
        HBox header = createHeader();

        // 2. Verification Pipeline Stats + Regional Bar Chart
        HBox topAnalyticsSection = createTopAnalyticsSection();

        // 3. Multi-Filter Bar
        HBox filterBar = createFilterBar();

        // 4. Data Table Container
        VBox tableContainer = createTableContainer();

        mainContainer.getChildren().addAll(header, topAnalyticsSection, filterBar, tableContainer);
        setContent(mainContainer);

        // Load Initial Sample Data
        loadHospitalData();
    }

    public Parent getView() {
        return this;
    }

    // Renamed from getScene() to createScene() to avoid clashing with Node.getScene()
    public Scene createScene() {
        return new Scene(this);
    }

    // ------------------------------------------------------------------------
    // UI COMPONENTS & LAYOUTS
    // ------------------------------------------------------------------------

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Hospital Verification & Regulatory Oversight");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#0F172A"));

        Label subtitle = new Label("Inspect healthcare facility credentials, compliance certifications, bed capacity, and authorization requests.");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        subtitle.setTextFill(Color.web("#64748B"));

        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button exportBtn = new Button("Export Audit Log");
        exportBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        exportBtn.setStyle(
            "-fx-background-color: #FFFFFF; " +
            "-fx-text-fill: #334155; " +
            "-fx-border-color: #CBD5E1; " +
            "-fx-border-radius: 8px; " +
            "-fx-background-radius: 8px; " +
            "-fx-padding: 8px 16px; " +
            "-fx-cursor: hand;"
        );
        exportBtn.setOnAction(e -> showAlert("Audit Report Exported", "The complete regulatory facility verification log has been exported to CSV format."));

        Button registerHospBtn = new Button("+ Register Facility");
        registerHospBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        registerHospBtn.setStyle(
            "-fx-background-color: #4F46E5; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 8px; " +
            "-fx-padding: 8px 16px; " +
            "-fx-cursor: hand;"
        );
        registerHospBtn.setOnAction(e -> showRegisterHospitalDialog());

        HBox buttonGroup = new HBox(12, exportBtn, registerHospBtn);
        buttonGroup.setAlignment(Pos.CENTER_RIGHT);

        header.getChildren().addAll(titleBox, spacer, buttonGroup);
        return header;
    }

    private HBox createTopAnalyticsSection() {
        HBox section = new HBox(20);
        section.setAlignment(Pos.CENTER);

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(16);
        statsGrid.setVgap(16);
        HBox.setHgrow(statsGrid, Priority.ALWAYS);

        totalCountLabel = new Label("0");
        pendingCountLabel = new Label("0");
        verifiedCountLabel = new Label("0");
        rejectedCountLabel = new Label("0");

        VBox totalCard = createStatCard("Total Facilities", totalCountLabel, "Registered on Platform", "#4F46E5");
        VBox pendingCard = createStatCard("Pending Verifications", pendingCountLabel, "Action Required", "#D97706");
        VBox verifiedCard = createStatCard("Verified Facilities", verifiedCountLabel, "Compliance Clear", "#059669");
        VBox rejectedCard = createStatCard("Rejected Applications", rejectedCountLabel, "Document Audit Failed", "#DC2626");

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(50);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(50);
        statsGrid.getColumnConstraints().addAll(c1, c2);

        statsGrid.add(totalCard, 0, 0);
        statsGrid.add(pendingCard, 1, 0);
        statsGrid.add(verifiedCard, 0, 1);
        statsGrid.add(rejectedCard, 1, 1);

        VBox chartCard = new VBox(12);
        chartCard.setPadding(new Insets(16));
        chartCard.setMinWidth(420);
        chartCard.setStyle(
            "-fx-background-color: #FFFFFF; " +
            "-fx-background-radius: 12px; " +
            "-fx-border-color: #E2E8F0; " +
            "-fx-border-radius: 12px;"
        );

        Label chartTitle = new Label("Regional Distribution (Hospitals by City)");
        chartTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        chartTitle.setTextFill(Color.web("#0F172A"));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickLabelFill(Color.web("#64748B"));
        yAxis.setTickLabelFill(Color.web("#64748B"));

        cityBarChart = new BarChart<>(xAxis, yAxis);
        cityBarChart.setPrefHeight(180);
        cityBarChart.setLegendVisible(false);
        cityBarChart.setAnimated(false);

        chartCard.getChildren().addAll(chartTitle, cityBarChart);

        section.getChildren().addAll(statsGrid, chartCard);
        return section;
    }

    private VBox createStatCard(String title, Label valueLabel, String subtext, String accentColorHex) {
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
        searchInput.setPromptText("🔍 Search Hospital Name, License No, or City...");
        searchInput.setPrefWidth(320);
        searchInput.setStyle(
            "-fx-background-color: #F8FAFC; " +
            "-fx-text-fill: #0F172A; " +
            "-fx-border-color: #CBD5E1; " +
            "-fx-border-radius: 6px; " +
            "-fx-padding: 8px 12px;"
        );

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All Statuses", "PENDING", "VERIFIED", "REJECTED");
        statusFilter.setValue("All Statuses");
        statusFilter.setStyle("-fx-background-color: #F8FAFC; -fx-text-fill: #0F172A; -fx-border-color: #CBD5E1; -fx-border-radius: 6px;");

        ComboBox<String> cityFilter = new ComboBox<>();
        cityFilter.getItems().addAll("All Cities", "Mumbai", "Pune", "Nagpur", "Nashik");
        cityFilter.setValue("All Cities");
        cityFilter.setStyle("-fx-background-color: #F8FAFC; -fx-text-fill: #0F172A; -fx-border-color: #CBD5E1; -fx-border-radius: 6px;");

        Runnable applyFilter = () -> {
            String query = searchInput.getText().toLowerCase().trim();
            String selectedStatus = statusFilter.getValue();
            String selectedCity = cityFilter.getValue();

            filteredData.setPredicate(hosp -> {
                boolean matchesQuery = query.isEmpty() ||
                        hosp.getName().toLowerCase().contains(query) ||
                        hosp.getLicenseNo().toLowerCase().contains(query) ||
                        hosp.getCity().toLowerCase().contains(query);

                boolean matchesStatus = selectedStatus.equals("All Statuses") || hosp.getStatus().equalsIgnoreCase(selectedStatus);
                boolean matchesCity = selectedCity.equals("All Cities") || hosp.getCity().equalsIgnoreCase(selectedCity);

                return matchesQuery && matchesStatus && matchesCity;
            });
        };

        searchInput.textProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        cityFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button resetBtn = new Button("Reset Filters");
        resetBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        resetBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #475569; -fx-background-radius: 6px; -fx-padding: 8px 14px; -fx-cursor: hand;");
        resetBtn.setOnAction(e -> {
            searchInput.clear();
            statusFilter.setValue("All Statuses");
            cityFilter.setValue("All Cities");
        });

        bar.getChildren().addAll(searchInput, statusFilter, cityFilter, spacer, resetBtn);
        return bar;
    }

    private VBox createTableContainer() {
        VBox container = new VBox(12);
        container.setPadding(new Insets(16));
        container.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px;");

        Label tableTitle = new Label("Registered Healthcare Facilities");
        tableTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        tableTitle.setTextFill(Color.web("#0F172A"));

        hospitalTable = new TableView<>();
        hospitalTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        hospitalTable.setStyle("-fx-background-color: transparent;");
        hospitalTable.setPrefHeight(400);

        // Facility Name Column
        TableColumn<HospitalModel, String> nameCol = new TableColumn<>("Facility Name & License");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        nameCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty || name == null) {
                    setGraphic(null);
                } else {
                    HospitalModel hosp = getTableRow() != null ? getTableRow().getItem() : null;
                    if (hosp == null) {
                        setGraphic(null);
                        return;
                    }
                    HBox box = new HBox(12);
                    box.setAlignment(Pos.CENTER_LEFT);

                    StackPane icon = createHospitalBadge(hosp.getName().substring(0, 1));

                    VBox textContainer = new VBox(2);
                    Label nameLbl = new Label(hosp.getName());
                    nameLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                    nameLbl.setTextFill(Color.web("#0F172A"));

                    Label licenseLbl = new Label("Lic: " + hosp.getLicenseNo() + " • " + hosp.getCity());
                    licenseLbl.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
                    licenseLbl.setTextFill(Color.web("#64748B"));

                    textContainer.getChildren().addAll(nameLbl, licenseLbl);
                    box.getChildren().addAll(icon, textContainer);
                    setGraphic(box);
                }
            }
        });

        // Bed Capacity Column
        TableColumn<HospitalModel, Number> bedsCol = new TableColumn<>("Beds / ICU");
        bedsCol.setCellValueFactory(cellData -> cellData.getValue().bedCapacityProperty());
        bedsCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number beds, boolean empty) {
                super.updateItem(beds, empty);
                if (empty || beds == null) {
                    setText(null);
                } else {
                    setText(beds.intValue() + " Beds");
                    setTextFill(Color.web("#334155"));
                    setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
                }
            }
        });

        // Verification Status Badge Column
        TableColumn<HospitalModel, String> statusCol = new TableColumn<>("Verification Status");
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(status.toUpperCase());
                    badge.setPadding(new Insets(4, 10, 4, 10));
                    badge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));

                    switch (status.toUpperCase()) {
                        case "VERIFIED" -> badge.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #15803D; -fx-background-radius: 20px;");
                        case "REJECTED" -> badge.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #B91C1C; -fx-background-radius: 20px;");
                        default -> badge.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #D97706; -fx-background-radius: 20px;");
                    }
                    setGraphic(badge);
                }
            }
        });

        // Applied Date Column
        TableColumn<HospitalModel, String> dateCol = new TableColumn<>("Applied Date");
        dateCol.setCellValueFactory(cellData -> cellData.getValue().appliedDateProperty());

        // Actions Column
        TableColumn<HospitalModel, HospitalModel> actionCol = new TableColumn<>("Actions & Audit");
        actionCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button inspectBtn = new Button("Inspect Docs");
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final HBox btnGroup = new HBox(6, inspectBtn, approveBtn, rejectBtn);

            {
                btnGroup.setAlignment(Pos.CENTER);
                inspectBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #334155; -fx-cursor: hand; -fx-font-size: 11px; -fx-background-radius: 4px;");
                approveBtn.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #15803D; -fx-cursor: hand; -fx-font-size: 11px; -fx-background-radius: 4px;");
                rejectBtn.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #B91C1C; -fx-cursor: hand; -fx-font-size: 11px; -fx-background-radius: 4px;");

                inspectBtn.setOnAction(e -> {
                    HospitalModel hosp = getItem();
                    if (hosp != null) showDocumentInspectionModal(hosp);
                });

                approveBtn.setOnAction(e -> {
                    HospitalModel hosp = getItem();
                    if (hosp != null) {
                        hosp.setStatus("VERIFIED");
                    }
                });

                rejectBtn.setOnAction(e -> {
                    HospitalModel hosp = getItem();
                    if (hosp != null) {
                        hosp.setStatus("REJECTED");
                    }
                });
            }

            @Override
            protected void updateItem(HospitalModel hosp, boolean empty) {
                super.updateItem(hosp, empty);
                if (empty || hosp == null) {
                    setGraphic(null);
                } else {
                    setGraphic(btnGroup);
                }
            }
        });

        hospitalTable.getColumns().addAll(nameCol, bedsCol, statusCol, dateCol, actionCol);
        container.getChildren().addAll(tableTitle, hospitalTable);
        return container;
    }

    private StackPane createHospitalBadge(String letter) {
        Circle circle = new Circle(16);
        circle.setFill(Color.web("#EEF2FF"));
        circle.setStroke(Color.web("#818CF8"));
        circle.setStrokeWidth(1.5);

        Label label = new Label(letter);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        label.setTextFill(Color.web("#4F46E5"));

        return new StackPane(circle, label);
    }

    // ------------------------------------------------------------------------
    // DATA MANAGEMENT & COMPUTATIONS
    // ------------------------------------------------------------------------

    private void loadHospitalData() {
        masterHospitalData = FXCollections.observableArrayList(
            hosp -> new Observable[]{ hosp.statusProperty(), hosp.cityProperty(), hosp.bedCapacityProperty() }
        );

        masterHospitalData.addAll(
            new HospitalModel("HOSP-801", "City Care Superspeciality Hospital", "MH-MUM-8890", "Mumbai", 350, "PENDING", "2026-08-01"),
            new HospitalModel("HOSP-802", "Ruby Hall Medical Center", "MH-PUN-1044", "Pune", 500, "VERIFIED", "2026-07-15"),
            new HospitalModel("HOSP-803", "Orange City Care Hospital", "MH-NAG-3321", "Nagpur", 180, "PENDING", "2026-08-05"),
            new HospitalModel("HOSP-804", "Apex Multi-Speciality Clinic", "MH-NAS-9981", "Nashik", 90, "REJECTED", "2026-07-28"),
            new HospitalModel("HOSP-805", "Sahyadri Health Campus", "MH-PUN-7712", "Pune", 420, "VERIFIED", "2026-06-19"),
            new HospitalModel("HOSP-806", "Lilavati Hospital & Research Centre", "MH-MUM-4432", "Mumbai", 320, "VERIFIED", "2026-07-10")
        );

        masterHospitalData.addListener((ListChangeListener<HospitalModel>) c -> updateCountersAndChart());

        filteredData = new FilteredList<>(masterHospitalData, p -> true);
        hospitalTable.setItems(filteredData);

        updateCountersAndChart();
    }

    private void updateCountersAndChart() {
        int total = masterHospitalData.size();
        long pending = masterHospitalData.stream().filter(h -> h.getStatus().equalsIgnoreCase("PENDING")).count();
        long verified = masterHospitalData.stream().filter(h -> h.getStatus().equalsIgnoreCase("VERIFIED")).count();
        long rejected = masterHospitalData.stream().filter(h -> h.getStatus().equalsIgnoreCase("REJECTED")).count();

        totalCountLabel.setText(String.valueOf(total));
        pendingCountLabel.setText(String.valueOf(pending));
        verifiedCountLabel.setText(String.valueOf(verified));
        rejectedCountLabel.setText(String.valueOf(rejected));

        cityBarChart.getData().clear();
        Map<String, Long> cityCounts = masterHospitalData.stream()
                .collect(Collectors.groupingBy(HospitalModel::getCity, Collectors.counting()));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        cityCounts.forEach((city, count) -> series.getData().add(new XYChart.Data<>(city, count)));

        cityBarChart.getData().add(series);
    }

    // ------------------------------------------------------------------------
    // DIALOGS & ACTIONS
    // ------------------------------------------------------------------------

    private void showDocumentInspectionModal(HospitalModel hosp) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Regulatory Document Inspection");
        dialog.setHeaderText("Verification Audit: " + hosp.getName());
        dialog.setContentText(
            "📜 License Number: " + hosp.getLicenseNo() + "\n" +
            "📍 Location: " + hosp.getCity() + ", Maharashtra\n" +
            "🛏️ Registered Bed Capacity: " + hosp.getBedCapacity() + " Beds\n" +
            "📅 Application Date: " + hosp.getAppliedDate() + "\n" +
            "🏷️ Current Status: " + hosp.getStatus() + "\n\n" +
            "Compliance Verification Logs:\n" +
            "✓ Fire & Safety Clearance Certificate: VERIFIED (PDF Valid)\n" +
            "✓ Medical Council Accreditation: OK\n" +
            "✓ ICU Emergency Backup Protocol: COMPLIANT\n" +
            "✓ Bio-Medical Waste Disposal Plan: APPROVED"
        );
        dialog.showAndWait();
    }

    private void showRegisterHospitalDialog() {
        Dialog<HospitalModel> dialog = new Dialog<>();
        dialog.setTitle("New Hospital Onboarding");
        dialog.setHeaderText("Register Hospital into HealthSphere Regulatory Network");

        ButtonType registerButtonType = new ButtonType("Register Facility", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(registerButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Hospital / Clinic Name");

        TextField licenseField = new TextField();
        licenseField.setPromptText("MH-XXX-1234");

        ComboBox<String> cityCombo = new ComboBox<>();
        cityCombo.getItems().addAll("Mumbai", "Pune", "Nagpur", "Nashik");
        cityCombo.setValue("Pune");

        TextField bedsField = new TextField();
        bedsField.setPromptText("150");

        grid.add(new Label("Facility Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("License Number:"), 0, 1);
        grid.add(licenseField, 1, 1);
        grid.add(new Label("City Location:"), 0, 2);
        grid.add(cityCombo, 1, 2);
        grid.add(new Label("Bed Capacity:"), 0, 3);
        grid.add(bedsField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registerButtonType && !nameField.getText().trim().isEmpty()) {
                String newId = "HOSP-" + (800 + masterHospitalData.size() + 1);
                int beds = 100;
                try {
                    beds = Integer.parseInt(bedsField.getText().trim());
                } catch (NumberFormatException ignored) {}

                String license = licenseField.getText().trim().isEmpty() ? "MH-REG-" + (2000 + masterHospitalData.size()) : licenseField.getText().trim();
                return new HospitalModel(newId, nameField.getText().trim(), license, cityCombo.getValue(), beds, "PENDING", LocalDate.now().toString());
            }
            return null;
        });

        Optional<HospitalModel> result = dialog.showAndWait();
        result.ifPresent(newHosp -> {
            masterHospitalData.add(0, newHosp);
            showAlert("Facility Registered", "New hospital " + newHosp.getName() + " was submitted for verification.");
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // ------------------------------------------------------------------------
    // INNER MODEL CLASS (JavaFX Properties)
    // ------------------------------------------------------------------------

    public static class HospitalModel {
        private final StringProperty hospitalId = new SimpleStringProperty();
        private final StringProperty name = new SimpleStringProperty();
        private final StringProperty licenseNo = new SimpleStringProperty();
        private final StringProperty city = new SimpleStringProperty();
        private final IntegerProperty bedCapacity = new SimpleIntegerProperty();
        private final StringProperty status = new SimpleStringProperty();
        private final StringProperty appliedDate = new SimpleStringProperty();

        public HospitalModel(String hospitalId, String name, String licenseNo, String city, int bedCapacity, String status, String appliedDate) {
            setHospitalId(hospitalId);
            setName(name);
            setLicenseNo(licenseNo);
            setCity(city);
            setBedCapacity(bedCapacity);
            setStatus(status);
            setAppliedDate(appliedDate);
        }

        public StringProperty hospitalIdProperty() { return hospitalId; }
        public String getHospitalId() { return hospitalId.get(); }
        public void setHospitalId(String hospitalId) { this.hospitalId.set(hospitalId); }

        public StringProperty nameProperty() { return name; }
        public String getName() { return name.get(); }
        public void setName(String name) { this.name.set(name); }

        public StringProperty licenseNoProperty() { return licenseNo; }
        public String getLicenseNo() { return licenseNo.get(); }
        public void setLicenseNo(String licenseNo) { this.licenseNo.set(licenseNo); }

        public StringProperty cityProperty() { return city; }
        public String getCity() { return city.get(); }
        public void setCity(String city) { this.city.set(city); }

        public IntegerProperty bedCapacityProperty() { return bedCapacity; }
        public int getBedCapacity() { return bedCapacity.get(); }
        public void setBedCapacity(int bedCapacity) { this.bedCapacity.set(bedCapacity); }

        public StringProperty statusProperty() { return status; }
        public String getStatus() { return status.get(); }
        public void setStatus(String status) { this.status.set(status); }

        public StringProperty appliedDateProperty() { return appliedDate; }
        public String getAppliedDate() { return appliedDate.get(); }
        public void setAppliedDate(String appliedDate) { this.appliedDate.set(appliedDate); }
    }
}
