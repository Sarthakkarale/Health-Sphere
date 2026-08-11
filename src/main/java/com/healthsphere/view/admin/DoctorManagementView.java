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

public class DoctorManagementView extends ScrollPane {

    private Stage primaryStage;
    private TableView<DoctorModel> doctorTable;
    private ObservableList<DoctorModel> masterDoctorData;
    private FilteredList<DoctorModel> filteredData;

    private Label pendingAuditsLabel;
    private Label verifiedDoctorsLabel;
    private BarChart<String, Number> deptBarChart;

    public DoctorManagementView() {
        this(null);
    }

    public DoctorManagementView(Stage stage) {
        this.primaryStage = stage;

        setFitToWidth(true);
        setStyle("-fx-background-color: #0F172A; -fx-background: #0F172A;");

        VBox mainContainer = new VBox(25);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setStyle("-fx-background-color: #0F172A;");

        // 1. Header
        VBox header = createHeader();

        // 2. Top Analytics Section
        HBox topAnalyticsSection = createTopAnalyticsSection();

        // 3. Search & Department Filters
        HBox filterBar = createFilterBar();

        // 4. Interactive Data Table
        VBox tableContainer = createTableContainer();

        mainContainer.getChildren().addAll(header, topAnalyticsSection, filterBar, tableContainer);
        setContent(mainContainer);

        // Load Data
        loadDoctorData();
    }

    public Parent getView() {
        return this;
    }

    private VBox createHeader() {
        VBox header = new VBox(5);
        Label title = new Label("Doctor Credentials & Medical Council Audit");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Verify doctor licenses, Medical Council (MCI/NMC) registrations, specializations, and practice compliance.");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        subtitle.setTextFill(Color.web("#94A3B8"));

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private HBox createTopAnalyticsSection() {
        HBox section = new HBox(20);
        section.setAlignment(Pos.CENTER);

        // Stats Cards Container
        VBox statsBox = new VBox(15);
        HBox.setHgrow(statsBox, Priority.ALWAYS);

        pendingAuditsLabel = new Label("14");
        verifiedDoctorsLabel = new Label("1,480");

        VBox verifiedCard = createStatCard("Verified Medical Staff", verifiedDoctorsLabel, "98.2% Clearance Rate", "#10B981");
        VBox pendingCard = createStatCard("Pending MCI Audits", pendingAuditsLabel, "Awaiting Degree Verification", "#F59E0B");
        VBox flaggedCard = createStatCard("Flagged Profiles", new Label("03"), "License Under Inquiry", "#EF4444");

        HBox topRow = new HBox(15, verifiedCard, pendingCard);
        HBox.setHgrow(verifiedCard, Priority.ALWAYS);
        HBox.setHgrow(pendingCard, Priority.ALWAYS);
        HBox.setHgrow(flaggedCard, Priority.ALWAYS);

        statsBox.getChildren().addAll(topRow, flaggedCard);

        // Department Breakdown BarChart
        VBox chartCard = new VBox(10);
        chartCard.setPadding(new Insets(15));
        chartCard.setMinWidth(420);
        chartCard.setStyle(
            "-fx-background-color: #1E293B; " +
            "-fx-background-radius: 12px; " +
            "-fx-border-color: #334155; " +
            "-fx-border-radius: 12px;"
        );

        Label chartTitle = new Label("Specialization Breakdown");
        chartTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        chartTitle.setTextFill(Color.WHITE);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickLabelFill(Color.web("#94A3B8"));
        yAxis.setTickLabelFill(Color.web("#94A3B8"));

        deptBarChart = new BarChart<>(xAxis, yAxis);
        deptBarChart.setPrefHeight(170);
        deptBarChart.setLegendVisible(false);
        deptBarChart.setAnimated(true);

        chartCard.getChildren().addAll(chartTitle, deptBarChart);

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
        searchInput.setPromptText("🔍 Search Doctor, MCI License, or Dept...");
        searchInput.setPrefWidth(320);
        searchInput.setStyle(
            "-fx-background-color: #0F172A; " +
            "-fx-text-fill: white; " +
            "-fx-border-color: #475569; " +
            "-fx-border-radius: 6px; " +
            "-fx-padding: 8px 12px;"
        );

        ComboBox<String> deptFilter = new ComboBox<>();
        deptFilter.getItems().addAll("All Specializations", "Cardiology", "Neurology", "Orthopedics", "Pediatrics", "Oncology");
        deptFilter.setValue("All Specializations");
        deptFilter.setStyle("-fx-background-color: #0F172A; -fx-mark-color: white;");

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All Status", "VERIFIED", "PENDING", "FLAGGED");
        statusFilter.setValue("All Status");
        statusFilter.setStyle("-fx-background-color: #0F172A; -fx-mark-color: white;");

        Runnable applyFilter = () -> {
            String query = searchInput.getText().toLowerCase().trim();
            String dept = deptFilter.getValue();
            String status = statusFilter.getValue();

            filteredData.setPredicate(doc -> {
                boolean matchesQuery = query.isEmpty() ||
                        doc.getName().toLowerCase().contains(query) ||
                        doc.getMciNumber().toLowerCase().contains(query) ||
                        doc.getSpecialization().toLowerCase().contains(query);

                boolean matchesDept = dept.equals("All Specializations") || doc.getSpecialization().equalsIgnoreCase(dept);
                boolean matchesStatus = status.equals("All Status") || doc.getStatus().equalsIgnoreCase(status);

                return matchesQuery && matchesDept && matchesStatus;
            });
        };

        searchInput.textProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        deptFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button registerDocBtn = new Button("+ Onboard Doctor");
        registerDocBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        registerDocBtn.setStyle(
            "-fx-background-color: #6366F1; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 6px; " +
            "-fx-padding: 8px 16px; " +
            "-fx-cursor: hand;"
        );
        registerDocBtn.setOnAction(e -> showOnboardDoctorDialog());

        bar.getChildren().addAll(searchInput, deptFilter, statusFilter, spacer, registerDocBtn);
        return bar;
    }

    @SuppressWarnings("unchecked")
    private VBox createTableContainer() {
        VBox container = new VBox();
        container.setStyle("-fx-background-color: #1E293B; -fx-background-radius: 12px; -fx-border-color: #334155; -fx-border-radius: 12px;");

        doctorTable = new TableView<>();
        doctorTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        doctorTable.setStyle("-fx-background-color: transparent;");
        doctorTable.setPrefHeight(400);

        // Doctor Name & Avatar Column
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

                    StackPane avatar = createDoctorAvatar(doc.getName().replace("Dr. ", "").substring(0, 1));

                    VBox textContainer = new VBox(2);
                    Label nameLbl = new Label(name);
                    nameLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                    nameLbl.setTextFill(Color.WHITE);

                    Label mciLbl = new Label("MCI Reg: " + doc.getMciNumber() + " • " + doc.getHospitalName());
                    mciLbl.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
                    mciLbl.setTextFill(Color.web("#94A3B8"));

                    textContainer.getChildren().addAll(nameLbl, mciLbl);
                    box.getChildren().addAll(avatar, textContainer);
                    setGraphic(box);
                }
            }
        });

        // Specialization Column
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
                    badge.setStyle("-fx-background-color: #1E1B4B; -fx-text-fill: #818CF8; -fx-background-radius: 20px;");
                    setGraphic(badge);
                }
            }
        });

        // Experience Column
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
                    setTextFill(Color.web("#CBD5E1"));
                    setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
                }
            }
        });

        // Verification Status Badge
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
                        case "VERIFIED" -> badge.setStyle("-fx-background-color: #064E3B; -fx-text-fill: #10B981; -fx-background-radius: 20px;");
                        case "FLAGGED" -> badge.setStyle("-fx-background-color: #7F1D1D; -fx-text-fill: #EF4444; -fx-background-radius: 20px;");
                        default -> badge.setStyle("-fx-background-color: #78350F; -fx-text-fill: #FBBF24; -fx-background-radius: 20px;");
                    }
                    setGraphic(badge);
                }
            }
        });

        // Action Buttons
        TableColumn<DoctorModel, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button auditBtn = new Button("Audit Degree");
            private final Button verifyBtn = new Button("Approve");
            private final Button flagBtn = new Button("Flag");
            private final HBox btnGroup = new HBox(6, auditBtn, verifyBtn, flagBtn);

            {
                btnGroup.setAlignment(Pos.CENTER);
                auditBtn.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11px;");
                verifyBtn.setStyle("-fx-background-color: #064E3B; -fx-text-fill: #34D399; -fx-cursor: hand; -fx-font-size: 11px;");
                flagBtn.setStyle("-fx-background-color: #7F1D1D; -fx-text-fill: #FCA5A5; -fx-cursor: hand; -fx-font-size: 11px;");

                auditBtn.setOnAction(e -> {
                    DoctorModel doc = getTableView().getItems().get(getIndex());
                    showAuditModal(doc);
                });

                verifyBtn.setOnAction(e -> {
                    DoctorModel doc = getTableView().getItems().get(getIndex());
                    doc.setStatus("VERIFIED");
                    doctorTable.refresh();
                    updateCounters();
                });

                flagBtn.setOnAction(e -> {
                    DoctorModel doc = getTableView().getItems().get(getIndex());
                    doc.setStatus("FLAGGED");
                    doctorTable.refresh();
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

        doctorTable.getColumns().addAll(nameCol, deptCol, expCol, statusCol, actionCol);
        container.getChildren().add(doctorTable);
        return container;
    }

    private StackPane createDoctorAvatar(String initial) {
        Circle circle = new Circle(16);
        circle.setFill(Color.web("#065F46"));
        circle.setStroke(Color.web("#34D399"));
        circle.setStrokeWidth(1.5);

        Label label = new Label(initial);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        label.setTextFill(Color.web("#A7F3D0"));

        return new StackPane(circle, label);
    }

    private void loadDoctorData() {
        masterDoctorData = FXCollections.observableArrayList(
            new DoctorModel("DOC-301", "Dr. Rajesh Sharma", "MCI-884920", "Cardiology", "City Care Hospital", 14, "VERIFIED"),
            new DoctorModel("DOC-302", "Dr. Priya Nair", "MCI-990142", "Neurology", "Ruby Hall Center", 9, "PENDING"),
            new DoctorModel("DOC-303", "Dr. Amit Deshmukh", "MCI-331045", "Orthopedics", "Sahyadri Health", 18, "VERIFIED"),
            new DoctorModel("DOC-304", "Dr. Sneha Kulkarni", "MCI-449102", "Pediatrics", "Orange City Care", 6, "PENDING"),
            new DoctorModel("DOC-305", "Dr. Vikram Joshi", "MCI-110093", "Oncology", "Apex Multi-Speciality", 11, "FLAGGED")
        );

        filteredData = new FilteredList<>(masterDoctorData, p -> true);
        doctorTable.setItems(filteredData);

        updateCounters();
        loadDeptChartData();
    }

    private void updateCounters() {
        long pending = masterDoctorData.stream().filter(d -> d.getStatus().equalsIgnoreCase("PENDING")).count();
        long verified = masterDoctorData.stream().filter(d -> d.getStatus().equalsIgnoreCase("VERIFIED")).count();

        pendingAuditsLabel.setText(String.valueOf(pending));
        verifiedDoctorsLabel.setText(String.valueOf(1480 + verified - 2));
    }

    private void loadDeptChartData() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Cardiology", 320));
        series.getData().add(new XYChart.Data<>("Neurology", 240));
        series.getData().add(new XYChart.Data<>("Orthopedics", 410));
        series.getData().add(new XYChart.Data<>("Pediatrics", 290));
        series.getData().add(new XYChart.Data<>("Oncology", 150));

        deptBarChart.getData().clear();
        deptBarChart.getData().add(series);
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

    private void showOnboardDoctorDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Onboard New Medical Specialist");
        dialog.setHeaderText("Register Practitioner into HealthSphere Network");
        dialog.setContentText("Enter Doctor's Full Name:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                String docName = name.startsWith("Dr.") ? name : "Dr. " + name;
                String newId = "DOC-" + (300 + masterDoctorData.size() + 1);
                DoctorModel newDoc = new DoctorModel(newId, docName, "MCI-" + (500000 + masterDoctorData.size()), "Cardiology", "City Care Hospital", 5, "PENDING");
                masterDoctorData.add(newDoc);
                updateCounters();
            }
        });
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