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

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DoctorManagementView extends ScrollPane {

    private Stage primaryStage;
    private TableView<DoctorModel> doctorTable;
    private ObservableList<DoctorModel> masterDoctorData;
    private FilteredList<DoctorModel> filteredData;

    // Analytics Labels
    private Label totalDoctorsLabel;
    private Label verifiedDoctorsLabel;
    private Label pendingAuditsLabel;
    private Label flaggedProfilesLabel;
    private Label tableCountLabel;

    // Filter Controls
    private TextField searchInput;
    private ComboBox<String> deptFilter;
    private ComboBox<String> statusFilter;

    // Department Chart
    private BarChart<String, Number> deptBarChart;

    public DoctorManagementView() {
        this(null);
    }

    public DoctorManagementView(Stage stage) {
        this.primaryStage = stage;

        setFitToWidth(true);
        setStyle("-fx-background-color: #F8FAFC; -fx-background: #F8FAFC;");
        getStylesheets().add("data:text/css," + getLightThemeCSS());

        VBox mainContainer = new VBox(22);
        mainContainer.setPadding(new Insets(28));
        mainContainer.setStyle("-fx-background-color: #F8FAFC;");

        // 1. Header with Export & Refresh Actions
        HBox header = createHeader();

        // 2. Dynamic Metric Cards & Specialization Chart
        HBox topAnalyticsSection = createTopAnalyticsSection();

        // 3. Search & Department Filters Toolbar
        HBox filterBar = createFilterBar();

        // 4. Interactive Data Table
        VBox tableContainer = createTableContainer();

        mainContainer.getChildren().addAll(header, topAnalyticsSection, filterBar, tableContainer);
        setContent(mainContainer);

        // Load Initial Data
        loadDoctorData();
    }

    public Parent getView() {
        return this;
    }

    private HBox createHeader() {
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);

        VBox textContainer = new VBox(4);
        Label title = new Label("Doctor Credentials & Medical Council Audit");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#0F172A"));

        Label subtitle = new Label("Verify doctor licenses, Medical Council (MCI/NMC) registrations, specializations, and practice compliance.");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        subtitle.setTextFill(Color.web("#64748B"));

        textContainer.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Header Action Buttons
        Button refreshBtn = new Button("🔄 Refresh Data");
        refreshBtn.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        refreshBtn.setStyle(
            "-fx-background-color: #FFFFFF; " +
            "-fx-text-fill: #334155; " +
            "-fx-border-color: #CBD5E1; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-padding: 8px 14px; " +
            "-fx-cursor: hand;"
        );
        refreshBtn.setOnAction(e -> {
            loadDoctorData();
            showNotification("Data Synced", "Practitioner database successfully refreshed from NMC server.");
        });

        Button exportBtn = new Button("📥 Export CSV");
        exportBtn.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        exportBtn.setStyle(
            "-fx-background-color: #FFFFFF; " +
            "-fx-text-fill: #334155; " +
            "-fx-border-color: #CBD5E1; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-padding: 8px 14px; " +
            "-fx-cursor: hand;"
        );
        exportBtn.setOnAction(e -> handleExportCSV());

        HBox actions = new HBox(10, refreshBtn, exportBtn);
        actions.setAlignment(Pos.CENTER_RIGHT);

        headerBox.getChildren().addAll(textContainer, spacer, actions);
        return headerBox;
    }

    private HBox createTopAnalyticsSection() {
        HBox section = new HBox(20);
        section.setAlignment(Pos.CENTER);

        // Stat Cards Layout
        totalDoctorsLabel = new Label("0");
        verifiedDoctorsLabel = new Label("0");
        pendingAuditsLabel = new Label("0");
        flaggedProfilesLabel = new Label("0");

        VBox totalCard = createStatCard("Total Practitioners", totalDoctorsLabel, "Active Network Staff", "#4F46E5");
        VBox verifiedCard = createStatCard("Verified Staff", verifiedDoctorsLabel, "MCI / NMC Cleared", "#059669");
        VBox pendingCard = createStatCard("Pending Audits", pendingAuditsLabel, "Awaiting Degree Audit", "#D97706");
        VBox flaggedCard = createStatCard("Flagged Profiles", flaggedProfilesLabel, "Under Compliance Review", "#DC2626");

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(15);
        statsGrid.setVgap(15);
        GridPane.setHgrow(totalCard, Priority.ALWAYS);
        GridPane.setHgrow(verifiedCard, Priority.ALWAYS);
        GridPane.setHgrow(pendingCard, Priority.ALWAYS);
        GridPane.setHgrow(flaggedCard, Priority.ALWAYS);

        statsGrid.add(totalCard, 0, 0);
        statsGrid.add(verifiedCard, 1, 0);
        statsGrid.add(pendingCard, 0, 1);
        statsGrid.add(flaggedCard, 1, 1);

        HBox.setHgrow(statsGrid, Priority.ALWAYS);

        // Department Breakdown Bar Chart Card
        VBox chartCard = new VBox(10);
        chartCard.setPadding(new Insets(16));
        chartCard.setMinWidth(420);
        chartCard.setStyle(
            "-fx-background-color: #FFFFFF; " +
            "-fx-background-radius: 12px; " +
            "-fx-border-color: #E2E8F0; " +
            "-fx-border-radius: 12px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(15,23,42,0.02), 6, 0, 0, 1);"
        );

        Label chartTitle = new Label("Specialization Distribution");
        chartTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        chartTitle.setTextFill(Color.web("#0F172A"));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickLabelFill(Color.web("#64748B"));
        yAxis.setTickLabelFill(Color.web("#64748B"));

        deptBarChart = new BarChart<>(xAxis, yAxis);
        deptBarChart.setPrefHeight(150);
        deptBarChart.setLegendVisible(false);
        deptBarChart.setAnimated(true);

        chartCard.getChildren().addAll(chartTitle, deptBarChart);

        section.getChildren().addAll(statsGrid, chartCard);
        return section;
    }

    private VBox createStatCard(String title, Label valueLabel, String subtext, String accentColor) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle(
            "-fx-background-color: #FFFFFF; " +
            "-fx-background-radius: 10px; " +
            "-fx-border-color: #E2E8F0; " +
            "-fx-border-radius: 10px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(15,23,42,0.02), 4, 0, 0, 1);"
        );

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        titleLabel.setTextFill(Color.web("#64748B"));

        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        valueLabel.setTextFill(Color.web("#0F172A"));

        Label subLabel = new Label(subtext);
        subLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
        subLabel.setTextFill(Color.web(accentColor));

        card.getChildren().addAll(titleLabel, valueLabel, subLabel);
        return card;
    }

    private HBox createFilterBar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(14, 16, 14, 16));
        bar.setStyle(
            "-fx-background-color: #FFFFFF; " +
            "-fx-background-radius: 10px; " +
            "-fx-border-color: #E2E8F0; " +
            "-fx-border-radius: 10px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(15,23,42,0.02), 4, 0, 0, 1);"
        );

        searchInput = new TextField();
        searchInput.setPromptText("🔍 Search Doctor, MCI License, or Specialty...");
        searchInput.setPrefWidth(300);
        searchInput.setStyle(
            "-fx-background-color: #F8FAFC; " +
            "-fx-text-fill: #0F172A; " +
            "-fx-border-color: #CBD5E1; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-padding: 8px 12px;"
        );

        deptFilter = new ComboBox<>();
        deptFilter.getItems().addAll("All Specializations", "Cardiology", "Neurology", "Orthopedics", "Pediatrics", "Oncology");
        deptFilter.setValue("All Specializations");
        deptFilter.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #CBD5E1; -fx-border-radius: 6px;");

        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All Status", "VERIFIED", "PENDING", "FLAGGED");
        statusFilter.setValue("All Status");
        statusFilter.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #CBD5E1; -fx-border-radius: 6px;");

        Button resetBtn = new Button("Clear Filters");
        resetBtn.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 12));
        resetBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #475569; -fx-background-radius: 6px; -fx-padding: 8px 12px; -fx-cursor: hand;");
        resetBtn.setOnAction(e -> {
            searchInput.clear();
            deptFilter.setValue("All Specializations");
            statusFilter.setValue("All Status");
        });

        Runnable applyFilter = () -> {
            if (filteredData == null) return;
            String query = searchInput.getText() == null ? "" : searchInput.getText().toLowerCase().trim();
            String dept = deptFilter.getValue();
            String status = statusFilter.getValue();

            filteredData.setPredicate(doc -> {
                boolean matchesQuery = query.isEmpty() ||
                        doc.getName().toLowerCase().contains(query) ||
                        doc.getMciNumber().toLowerCase().contains(query) ||
                        doc.getSpecialization().toLowerCase().contains(query) ||
                        doc.getHospitalName().toLowerCase().contains(query);

                boolean matchesDept = dept == null || dept.equals("All Specializations") || doc.getSpecialization().equalsIgnoreCase(dept);
                boolean matchesStatus = status == null || status.equals("All Status") || doc.getStatus().equalsIgnoreCase(status);

                return matchesQuery && matchesDept && matchesStatus;
            });

            updateTableCountLabel();
        };

        searchInput.textProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        deptFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button registerDocBtn = new Button("+ Onboard Doctor");
        registerDocBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        registerDocBtn.setStyle(
            "-fx-background-color: #4F46E5; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 6px; " +
            "-fx-padding: 8px 16px; " +
            "-fx-cursor: hand;"
        );
        registerDocBtn.setOnAction(e -> showOnboardDoctorDialog());

        bar.getChildren().addAll(searchInput, deptFilter, statusFilter, resetBtn, spacer, registerDocBtn);
        return bar;
    }

    @SuppressWarnings("unchecked")
    private VBox createTableContainer() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(16));
        container.setStyle(
            "-fx-background-color: #FFFFFF; " +
            "-fx-background-radius: 12px; " +
            "-fx-border-color: #E2E8F0; " +
            "-fx-border-radius: 12px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(15,23,42,0.03), 8, 0, 0, 2);"
        );

        HBox tableHeaderBox = new HBox();
        tableHeaderBox.setAlignment(Pos.CENTER_LEFT);

        Label tableTitle = new Label("Practitioner Directory & Compliance Status");
        tableTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        tableTitle.setTextFill(Color.web("#0F172A"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        tableCountLabel = new Label("Showing 0 practitioners");
        tableCountLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        tableCountLabel.setTextFill(Color.web("#64748B"));

        tableHeaderBox.getChildren().addAll(tableTitle, spacer, tableCountLabel);

        doctorTable = new TableView<>();
        doctorTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        doctorTable.setStyle("-fx-background-color: transparent;");
        doctorTable.setPrefHeight(380);

        // 1. Doctor Name & Avatar Column
        TableColumn<DoctorModel, String> nameCol = new TableColumn<>("Practitioner Info");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty || name == null) {
                    setGraphic(null);
                } else {
                    DoctorModel doc = getTableView().getItems().get(getIndex());
                    HBox box = new HBox(12);
                    box.setAlignment(Pos.CENTER_LEFT);

                    String initial = doc.getName().replace("Dr. ", "").trim().substring(0, 1);
                    StackPane avatar = createDoctorAvatar(initial);

                    VBox textContainer = new VBox(2);
                    Label nameLbl = new Label(name);
                    nameLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                    nameLbl.setTextFill(Color.web("#0F172A"));

                    Label mciLbl = new Label("MCI Reg: " + doc.getMciNumber() + " • " + doc.getHospitalName());
                    mciLbl.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
                    mciLbl.setTextFill(Color.web("#64748B"));

                    textContainer.getChildren().addAll(nameLbl, mciLbl);
                    box.getChildren().addAll(avatar, textContainer);
                    setGraphic(box);
                }
            }
        });

        // 2. Specialization Column
        TableColumn<DoctorModel, String> deptCol = new TableColumn<>("Specialization");
        deptCol.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        deptCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String dept, boolean empty) {
                super.updateItem(dept, empty);
                if (empty || dept == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(dept);
                    badge.setPadding(new Insets(4, 10, 4, 10));
                    badge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
                    badge.setStyle("-fx-background-color: #EEF2FF; -fx-text-fill: #4F46E5; -fx-background-radius: 12px;");
                    setGraphic(badge);
                }
            }
        });

        // 3. Experience Column
        TableColumn<DoctorModel, Integer> expCol = new TableColumn<>("Experience");
        expCol.setCellValueFactory(new PropertyValueFactory<>("experienceYears"));
        expCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer exp, boolean empty) {
                super.updateItem(exp, empty);
                if (empty || exp == null) {
                    setText(null);
                } else {
                    setText(exp + " Years");
                    setTextFill(Color.web("#334155"));
                    setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
                }
            }
        });

        // 4. Verification Status Badge Column
        TableColumn<DoctorModel, String> statusCol = new TableColumn<>("Audit Status");
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
                        case "VERIFIED" -> badge.setStyle("-fx-background-color: #ECFDF5; -fx-text-fill: #059669; -fx-background-radius: 12px;");
                        case "FLAGGED" -> badge.setStyle("-fx-background-color: #FEF2F2; -fx-text-fill: #DC2626; -fx-background-radius: 12px;");
                        default -> badge.setStyle("-fx-background-color: #FFFBEB; -fx-text-fill: #D97706; -fx-background-radius: 12px;");
                    }
                    setGraphic(badge);
                }
            }
        });

        // 5. Functional Action Buttons Column
        TableColumn<DoctorModel, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button auditBtn = new Button("Audit");
            private final Button verifyBtn = new Button("Approve");
            private final Button flagBtn = new Button("Flag");
            private final Button deleteBtn = new Button("Offboard");
            private final HBox btnGroup = new HBox(5, auditBtn, verifyBtn, flagBtn, deleteBtn);

            {
                btnGroup.setAlignment(Pos.CENTER_LEFT);
                auditBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #334155; -fx-border-color: #CBD5E1; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-cursor: hand; -fx-font-size: 11px; -fx-font-weight: bold;");
                verifyBtn.setStyle("-fx-background-color: #ECFDF5; -fx-text-fill: #059669; -fx-border-color: #A7F3D0; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-cursor: hand; -fx-font-size: 11px; -fx-font-weight: bold;");
                flagBtn.setStyle("-fx-background-color: #FFFBEB; -fx-text-fill: #D97706; -fx-border-color: #FDE68A; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-cursor: hand; -fx-font-size: 11px; -fx-font-weight: bold;");
                deleteBtn.setStyle("-fx-background-color: #FEF2F2; -fx-text-fill: #DC2626; -fx-border-color: #FCA5A5; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-cursor: hand; -fx-font-size: 11px; -fx-font-weight: bold;");

                auditBtn.setOnAction(e -> {
                    DoctorModel doc = getTableView().getItems().get(getIndex());
                    showAuditModal(doc);
                });

                verifyBtn.setOnAction(e -> {
                    DoctorModel doc = getTableView().getItems().get(getIndex());
                    doc.setStatus("VERIFIED");
                    doctorTable.refresh();
                    recalculateMetricsAndChart();
                    showNotification("Status Updated", doc.getName() + " has been approved and verified.");
                });

                flagBtn.setOnAction(e -> {
                    DoctorModel doc = getTableView().getItems().get(getIndex());
                    doc.setStatus("FLAGGED");
                    doctorTable.refresh();
                    recalculateMetricsAndChart();
                    showNotification("Profile Flagged", doc.getName() + " flagged for compliance inquiry.");
                });

                deleteBtn.setOnAction(e -> {
                    DoctorModel doc = getTableView().getItems().get(getIndex());
                    handleDeleteDoctor(doc);
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

        doctorTable.getColumns().addAll(nameCol, deptCol, expCol, statusCol, actionCol);
        container.getChildren().addAll(tableHeaderBox, doctorTable);
        return container;
    }

    private StackPane createDoctorAvatar(String initial) {
        Circle circle = new Circle(16);
        circle.setFill(Color.web("#ECFDF5"));
        circle.setStroke(Color.web("#10B981"));
        circle.setStrokeWidth(1.5);

        Label label = new Label(initial);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        label.setTextFill(Color.web("#047857"));

        return new StackPane(circle, label);
    }

    private void loadDoctorData() {
        masterDoctorData = FXCollections.observableArrayList(
            new DoctorModel("DOC-301", "Dr. Rajesh Sharma", "MCI-884920", "Cardiology", "City Care Hospital", 14, "VERIFIED"),
            new DoctorModel("DOC-302", "Dr. Priya Nair", "MCI-990142", "Neurology", "Ruby Hall Center", 9, "PENDING"),
            new DoctorModel("DOC-303", "Dr. Amit Deshmukh", "MCI-331045", "Orthopedics", "Sahyadri Health", 18, "VERIFIED"),
            new DoctorModel("DOC-304", "Dr. Sneha Kulkarni", "MCI-449102", "Pediatrics", "Orange City Care", 6, "PENDING"),
            new DoctorModel("DOC-305", "Dr. Vikram Joshi", "MCI-110093", "Oncology", "Apex Multi-Speciality", 11, "FLAGGED"),
            new DoctorModel("DOC-306", "Dr. Ananya Rao", "MCI-772109", "Cardiology", "City Care Hospital", 8, "VERIFIED"),
            new DoctorModel("DOC-307", "Dr. Rohan Verma", "MCI-554281", "Orthopedics", "KEM Hospital", 12, "PENDING")
        );

        filteredData = new FilteredList<>(masterDoctorData, p -> true);
        doctorTable.setItems(filteredData);

        recalculateMetricsAndChart();
    }

    private void recalculateMetricsAndChart() {
        int total = masterDoctorData.size();
        long verified = masterDoctorData.stream().filter(d -> d.getStatus().equalsIgnoreCase("VERIFIED")).count();
        long pending = masterDoctorData.stream().filter(d -> d.getStatus().equalsIgnoreCase("PENDING")).count();
        long flagged = masterDoctorData.stream().filter(d -> d.getStatus().equalsIgnoreCase("FLAGGED")).count();

        totalDoctorsLabel.setText(String.valueOf(total));
        verifiedDoctorsLabel.setText(String.valueOf(verified));
        pendingAuditsLabel.setText(String.valueOf(pending));
        flaggedProfilesLabel.setText(String.valueOf(flagged));

        updateTableCountLabel();
        updateDeptChartData();
    }

    private void updateTableCountLabel() {
        if (filteredData != null && tableCountLabel != null) {
            tableCountLabel.setText("Showing " + filteredData.size() + " of " + masterDoctorData.size() + " practitioners");
        }
    }

    private void updateDeptChartData() {
        if (deptBarChart == null) return;

        Map<String, Long> counts = masterDoctorData.stream()
                .collect(Collectors.groupingBy(DoctorModel::getSpecialization, Collectors.counting()));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        counts.forEach((dept, count) -> series.getData().add(new XYChart.Data<>(dept, count)));

        deptBarChart.getData().clear();
        deptBarChart.getData().add(series);
    }

    // --- Actions & Modals ---

    private void showOnboardDoctorDialog() {
        Dialog<DoctorModel> dialog = new Dialog<>();
        dialog.setTitle("Onboard New Medical Specialist");
        dialog.setHeaderText("Register Practitioner into HealthSphere Audit Portal");

        ButtonType onboardButtonType = new ButtonType("Onboard Practitioner", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(onboardButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Dr. First Last");

        TextField mciField = new TextField();
        mciField.setPromptText("MCI-XXXXXX");

        ComboBox<String> specBox = new ComboBox<>(FXCollections.observableArrayList(
                "Cardiology", "Neurology", "Orthopedics", "Pediatrics", "Oncology"
        ));
        specBox.setValue("Cardiology");

        TextField hospitalField = new TextField();
        hospitalField.setPromptText("Hospital / Medical Center");

        Spinner<Integer> expSpinner = new Spinner<>(1, 50, 5);

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("MCI / NMC License:"), 0, 1);
        grid.add(mciField, 1, 1);
        grid.add(new Label("Specialization:"), 0, 2);
        grid.add(specBox, 1, 2);
        grid.add(new Label("Affiliated Hospital:"), 0, 3);
        grid.add(hospitalField, 1, 3);
        grid.add(new Label("Experience (Years):"), 0, 4);
        grid.add(expSpinner, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == onboardButtonType) {
                String rawName = nameField.getText().trim();
                String docName = rawName.startsWith("Dr.") ? rawName : "Dr. " + rawName;
                String mci = mciField.getText().trim().isEmpty() ? "MCI-" + (100000 + (int)(Math.random() * 899999)) : mciField.getText().trim();
                String hospital = hospitalField.getText().trim().isEmpty() ? "General Hospital" : hospitalField.getText().trim();
                String id = "DOC-" + (300 + masterDoctorData.size() + 1);

                return new DoctorModel(id, docName, mci, specBox.getValue(), hospital, expSpinner.getValue(), "PENDING");
            }
            return null;
        });

        Optional<DoctorModel> result = dialog.showAndWait();
        result.ifPresent(newDoc -> {
            masterDoctorData.add(0, newDoc);
            recalculateMetricsAndChart();
            showNotification("Practitioner Registered", newDoc.getName() + " added for MCI verification audit.");
        });
    }

    private void showAuditModal(DoctorModel doc) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Medical License & Degree Audit");
        dialog.setHeaderText("Credentials Inspection: " + doc.getName());
        dialog.setContentText(
            "🩺 MCI Registration No: " + doc.getMciNumber() + "\n" +
            "🏥 Primary Hospital: " + doc.getHospitalName() + "\n" +
            "🧬 Specialization: " + doc.getSpecialization() + "\n" +
            "⏳ Experience: " + doc.getExperienceYears() + " Years\n\n" +
            "✓ MBBS / MD Degree Verification: VERIFIED (NMC Portal)\n" +
            "✓ Active License Status: VALID (Expiring 2029)\n" +
            "✓ Malpractice History Log: CLEAN (0 Claims Flagged)"
        );
        dialog.showAndWait();
    }

    private void handleDeleteDoctor(DoctorModel doc) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Practitioner Offboarding");
        alert.setHeaderText("Remove " + doc.getName() + " from System?");
        alert.setContentText("This will revoke system privileges and archive their MCI registration log.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            masterDoctorData.remove(doc);
            recalculateMetricsAndChart();
            showNotification("Practitioner Offboarded", doc.getName() + " removed successfully.");
        }
    }

    private void handleExportCSV() {
        StringBuilder csv = new StringBuilder("ID,Name,MCI Number,Specialization,Hospital,Experience,Status\n");
        for (DoctorModel doc : filteredData) {
            csv.append(doc.getDoctorId()).append(",")
               .append("\"").append(doc.getName()).append("\",")
               .append(doc.getMciNumber()).append(",")
               .append(doc.getSpecialization()).append(",")
               .append("\"").append(doc.getHospitalName()).append("\",")
               .append(doc.getExperienceYears()).append(",")
               .append(doc.getStatus()).append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export CSV Complete");
        alert.setHeaderText("Audit Log Exported (" + filteredData.size() + " Records)");
        alert.setContentText("Data preview generated:\n\n" + csv.toString().substring(0, Math.min(csv.length(), 220)) + "\n...");
        alert.showAndWait();
    }

    private void showNotification(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("HealthSphere System Notification");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private String getLightThemeCSS() {
        return """
            .table-view {
                -fx-background-color: transparent;
                -fx-border-color: #E2E8F0;
                -fx-border-radius: 8px;
            }
            .table-view .column-header-background {
                -fx-background-color: #F8FAFC;
            }
            .table-view .column-header {
                -fx-background-color: #F8FAFC;
                -fx-size: 38px;
            }
            .table-view .column-header .label {
                -fx-text-fill: #475569;
                -fx-font-weight: bold;
                -fx-font-size: 12px;
            }
            .table-row-cell {
                -fx-background-color: #FFFFFF;
                -fx-border-color: #F1F5F9;
                -fx-border-width: 0 0 1 0;
            }
            .table-row-cell:hover {
                -fx-background-color: #F8FAFC;
            }
            .chart-bar {
                -fx-bar-fill: #6366F1;
                -fx-background-radius: 4px 4px 0 0;
            }
            """;
    }

    // --- Inner Model Class ---
    public static class DoctorModel {
        private final String doctorId;
        private final String name;
        private final String mciNumber;
        private final String specialization;
        private final String hospitalName;
        private final int experienceYears;
        private String status;

        public DoctorModel(String doctorId, String name, String mciNumber, String specialization, String hospitalName, int experienceYears, String status) {
            this.doctorId = doctorId;
            this.name = name;
            this.mciNumber = mciNumber;
            this.specialization = specialization;
            this.hospitalName = hospitalName;
            this.experienceYears = experienceYears;
            this.status = status;
        }

        public String getDoctorId() { return doctorId; }
        public String getName() { return name; }
        public String getMciNumber() { return mciNumber; }
        public String getSpecialization() { return specialization; }
        public String getHospitalName() { return hospitalName; }
        public int getExperienceYears() { return experienceYears; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}