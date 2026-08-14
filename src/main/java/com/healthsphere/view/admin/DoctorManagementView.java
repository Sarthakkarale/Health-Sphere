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
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DoctorManagementView extends ScrollPane {

    private Stage primaryStage;
    private TableView<DoctorModel> doctorTable;
    private ObservableList<DoctorModel> masterDoctorData;
    private FilteredList<DoctorModel> filteredData;

    private Label totalCountLabel;
    private Label activeCountLabel;
    private Label leaveCountLabel;
    private Label surgeryCountLabel;
    private BarChart<String, Number> departmentBarChart;

    public DoctorManagementView() {
        this(null);
    }

    public DoctorManagementView(Stage stage) {
        this.primaryStage = stage;

        setFitToWidth(true);
        setStyle("-fx-background-color: #F8FAFC; -fx-background: #F8FAFC; -fx-border-color: transparent;");

        VBox mainContainer = new VBox(24);
        mainContainer.setPadding(new Insets(28));
        mainContainer.setStyle("-fx-background-color: #F8FAFC;");

        // 1. Header Section
        HBox header = createHeader();

        // 2. Doctor Analytics & Department Breakdown Section
        HBox topAnalyticsSection = createTopAnalyticsSection();

        // 3. Multi-Filter Bar
        HBox filterBar = createFilterBar();

        // 4. Data Table Container
        VBox tableContainer = createTableContainer();

        mainContainer.getChildren().addAll(header, topAnalyticsSection, filterBar, tableContainer);
        setContent(mainContainer);

        // Load Initial Sample Data
        loadDoctorData();
    }

    public Parent getView() {
        return this;
    }

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
        Label title = new Label("Doctor & Specialist Directory");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#0F172A"));

        Label subtitle = new Label("Manage medical staff credentials, verification approvals, department allocations, and OPD availability.");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        subtitle.setTextFill(Color.web("#64748B"));

        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button exportBtn = new Button("Export Directory");
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
        exportBtn.setOnAction(e -> showAlert("Directory Exported", "Doctor roster and department metrics exported to CSV successfully."));

        Button addDoctorBtn = new Button("+ Add New Doctor");
        addDoctorBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        addDoctorBtn.setStyle(
            "-fx-background-color: #4F46E5; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 8px; " +
            "-fx-padding: 8px 16px; " +
            "-fx-cursor: hand;"
        );
        addDoctorBtn.setOnAction(e -> showAddDoctorDialog());

        HBox buttonGroup = new HBox(12, exportBtn, addDoctorBtn);
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
        activeCountLabel = new Label("0");
        leaveCountLabel = new Label("0");
        surgeryCountLabel = new Label("0");

        VBox totalCard = createStatCard("Total Specialists", totalCountLabel, "Registered Doctors", "#4F46E5");
        VBox activeCard = createStatCard("On Duty / OPD", activeCountLabel, "Available for Consult", "#059669");
        VBox leaveCard = createStatCard("On Leave / Off Duty", leaveCountLabel, "Unavailable Today", "#D97706");
        VBox surgeryCard = createStatCard("In Surgery", surgeryCountLabel, "OT / Critical Duties", "#DC2626");

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(50);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(50);
        statsGrid.getColumnConstraints().addAll(c1, c2);

        statsGrid.add(totalCard, 0, 0);
        statsGrid.add(activeCard, 1, 0);
        statsGrid.add(leaveCard, 0, 1);
        statsGrid.add(surgeryCard, 1, 1);

        VBox chartCard = new VBox(12);
        chartCard.setPadding(new Insets(16));
        chartCard.setMinWidth(420);
        chartCard.setStyle(
            "-fx-background-color: #FFFFFF; " +
            "-fx-background-radius: 12px; " +
            "-fx-border-color: #E2E8F0; " +
            "-fx-border-radius: 12px;"
        );

        Label chartTitle = new Label("Departmental Staff Allocation");
        chartTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        chartTitle.setTextFill(Color.web("#0F172A"));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickLabelFill(Color.web("#64748B"));
        yAxis.setTickLabelFill(Color.web("#64748B"));

        departmentBarChart = new BarChart<>(xAxis, yAxis);
        departmentBarChart.setPrefHeight(180);
        departmentBarChart.setLegendVisible(false);
        departmentBarChart.setAnimated(false);

        chartCard.getChildren().addAll(chartTitle, departmentBarChart);

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
        searchInput.setPromptText("🔍 Search Doctor Name, ID, Specialization, or Email...");
        searchInput.setPrefWidth(260);
        searchInput.setStyle(
            "-fx-background-color: #F8FAFC; " +
            "-fx-text-fill: #0F172A; " +
            "-fx-border-color: #CBD5E1; " +
            "-fx-border-radius: 6px; " +
            "-fx-padding: 8px 12px;"
        );

        ComboBox<String> verificationFilter = new ComboBox<>();
        verificationFilter.getItems().addAll("All Approvals", "VERIFIED", "PENDING", "REJECTED");
        verificationFilter.setValue("All Approvals");
        verificationFilter.setStyle("-fx-background-color: #F8FAFC; -fx-text-fill: #0F172A; -fx-border-color: #CBD5E1; -fx-border-radius: 6px;");

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All Statuses", "ON DUTY", "ON LEAVE", "IN SURGERY");
        statusFilter.setValue("All Statuses");
        statusFilter.setStyle("-fx-background-color: #F8FAFC; -fx-text-fill: #0F172A; -fx-border-color: #CBD5E1; -fx-border-radius: 6px;");

        ComboBox<String> deptFilter = new ComboBox<>();
        deptFilter.getItems().addAll("All Departments", "Cardiology", "Neurology", "Orthopedics", "Pediatrics", "Oncology", "General Medicine");
        deptFilter.setValue("All Departments");
        deptFilter.setStyle("-fx-background-color: #F8FAFC; -fx-text-fill: #0F172A; -fx-border-color: #CBD5E1; -fx-border-radius: 6px;");

        Runnable applyFilter = () -> {
            String query = searchInput.getText().toLowerCase().trim();
            String selectedVerification = verificationFilter.getValue();
            String selectedStatus = statusFilter.getValue();
            String selectedDept = deptFilter.getValue();

            filteredData.setPredicate(doctor -> {
                boolean matchesQuery = query.isEmpty() ||
                        doctor.getName().toLowerCase().contains(query) ||
                        doctor.getDoctorId().toLowerCase().contains(query) ||
                        doctor.getSpecialization().toLowerCase().contains(query) ||
                        doctor.getEmail().toLowerCase().contains(query);

                boolean matchesVerification = selectedVerification.equals("All Approvals") || doctor.getVerificationStatus().equalsIgnoreCase(selectedVerification);
                boolean matchesStatus = selectedStatus.equals("All Statuses") || doctor.getStatus().equalsIgnoreCase(selectedStatus);
                boolean matchesDept = selectedDept.equals("All Departments") || doctor.getDepartment().equalsIgnoreCase(selectedDept);

                return matchesQuery && matchesVerification && matchesStatus && matchesDept;
            });
        };

        searchInput.textProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        verificationFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        deptFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button resetBtn = new Button("Reset Filters");
        resetBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        resetBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #475569; -fx-background-radius: 6px; -fx-padding: 8px 14px; -fx-cursor: hand;");
        resetBtn.setOnAction(e -> {
            searchInput.clear();
            verificationFilter.setValue("All Approvals");
            statusFilter.setValue("All Statuses");
            deptFilter.setValue("All Departments");
        });

        bar.getChildren().addAll(searchInput, verificationFilter, statusFilter, deptFilter, spacer, resetBtn);
        return bar;
    }

    @SuppressWarnings("unchecked")
    private VBox createTableContainer() {
        VBox container = new VBox(12);
        container.setPadding(new Insets(16));
        container.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px;");

        Label tableTitle = new Label("Medical Staff & Specialist Roster");
        tableTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        tableTitle.setTextFill(Color.web("#0F172A"));

        doctorTable = new TableView<>();
        doctorTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        doctorTable.setStyle("-fx-background-color: transparent;");
        doctorTable.setPrefHeight(400);

        // Doctor Name & Avatar Column
        TableColumn<DoctorModel, String> nameCol = new TableColumn<>("Doctor Profile");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty || name == null) {
                    setGraphic(null);
                } else {
                    DoctorModel doctor = getTableView().getItems().get(getIndex());
                    HBox box = new HBox(12);
                    box.setAlignment(Pos.CENTER_LEFT);

                    StackPane icon = createDoctorAvatar(doctor.getName());

                    VBox textContainer = new VBox(2);
                    Label nameLbl = new Label(name);
                    nameLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                    nameLbl.setTextFill(Color.web("#0F172A"));

                    Label subLbl = new Label("ID: " + doctor.getDoctorId() + " • " + doctor.getSpecialization());
                    subLbl.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
                    subLbl.setTextFill(Color.web("#64748B"));

                    textContainer.getChildren().addAll(nameLbl, subLbl);
                    box.getChildren().addAll(icon, textContainer);
                    setGraphic(box);
                }
            }
        });

        // Department & Room Column
        TableColumn<DoctorModel, String> deptCol = new TableColumn<>("Department & OPD Room");
        deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        deptCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String dept, boolean empty) {
                super.updateItem(dept, empty);
                if (empty || dept == null) {
                    setGraphic(null);
                } else {
                    DoctorModel doctor = getTableView().getItems().get(getIndex());
                    VBox textContainer = new VBox(2);

                    Label deptLbl = new Label(dept);
                    deptLbl.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
                    deptLbl.setTextFill(Color.web("#334155"));

                    Label roomLbl = new Label("OPD Room: " + doctor.getOpdRoom());
                    roomLbl.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
                    roomLbl.setTextFill(Color.web("#64748B"));

                    textContainer.getChildren().addAll(deptLbl, roomLbl);
                    setGraphic(textContainer);
                }
            }
        });

        // Contact Info Column
        TableColumn<DoctorModel, String> contactCol = new TableColumn<>("Contact & Email");
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactNo"));
        contactCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String contact, boolean empty) {
                super.updateItem(contact, empty);
                if (empty || contact == null) {
                    setGraphic(null);
                } else {
                    DoctorModel doctor = getTableView().getItems().get(getIndex());
                    VBox textContainer = new VBox(2);

                    Label phoneLbl = new Label(contact);
                    phoneLbl.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
                    phoneLbl.setTextFill(Color.web("#334155"));

                    Label emailLbl = new Label(doctor.getEmail());
                    emailLbl.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
                    emailLbl.setTextFill(Color.web("#64748B"));

                    textContainer.getChildren().addAll(phoneLbl, emailLbl);
                    setGraphic(textContainer);
                }
            }
        });

        // Verification Status Column
        TableColumn<DoctorModel, String> verificationCol = new TableColumn<>("App Approval");
        verificationCol.setCellValueFactory(new PropertyValueFactory<>("verificationStatus"));
        verificationCol.setCellFactory(col -> new TableCell<>() {
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
                        case "PENDING" -> badge.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #D97706; -fx-background-radius: 20px;");
                        default -> badge.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #B91C1C; -fx-background-radius: 20px;");
                    }
                    setGraphic(badge);
                }
            }
        });

        // Duty Status Badge Column
        TableColumn<DoctorModel, String> statusCol = new TableColumn<>("Availability Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
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
                        case "ON DUTY" -> badge.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #15803D; -fx-background-radius: 20px;");
                        case "IN SURGERY" -> badge.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #B91C1C; -fx-background-radius: 20px;");
                        default -> badge.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #D97706; -fx-background-radius: 20px;");
                    }
                    setGraphic(badge);
                }
            }
        });

        // Shift Schedule Column
        TableColumn<DoctorModel, String> shiftCol = new TableColumn<>("Current Shift");
        shiftCol.setCellValueFactory(new PropertyValueFactory<>("shift"));

        // Actions Column
        TableColumn<DoctorModel, Void> actionCol = new TableColumn<>("Management");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button profileBtn = new Button("View");
            private final Button toggleStatusBtn = new Button("Duty");
            private final HBox btnGroup = new HBox(5, profileBtn, toggleStatusBtn);

            {
                btnGroup.setAlignment(Pos.CENTER);
                profileBtn.setStyle("-fx-background-color: #EEF2FF; -fx-text-fill: #4338CA; -fx-cursor: hand; -fx-font-size: 10px; -fx-background-radius: 4px;");
                toggleStatusBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #334155; -fx-cursor: hand; -fx-font-size: 10px; -fx-background-radius: 4px;");

                profileBtn.setOnAction(e -> {
                    DoctorModel doctor = getTableView().getItems().get(getIndex());
                    showDoctorProfileModal(doctor);
                });

                toggleStatusBtn.setOnAction(e -> {
                    DoctorModel doctor = getTableView().getItems().get(getIndex());
                    if ("ON DUTY".equalsIgnoreCase(doctor.getStatus())) {
                        doctor.setStatus("ON LEAVE");
                    } else {
                        doctor.setStatus("ON DUTY");
                    }
                    doctorTable.refresh();
                    updateCountersAndChart();
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

        doctorTable.getColumns().addAll(nameCol, deptCol, contactCol, verificationCol, statusCol, shiftCol, actionCol);
        container.getChildren().addAll(tableTitle, doctorTable);
        return container;
    }

    private StackPane createDoctorAvatar(String name) {
        String initials = "DR";
        if (name != null && name.contains(" ")) {
            String[] parts = name.replace("Dr. ", "").split(" ");
            if (parts.length >= 2) {
                initials = ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
            } else if (parts.length == 1 && !parts[0].isEmpty()) {
                initials = ("" + parts[0].charAt(0)).toUpperCase();
            }
        }

        Circle circle = new Circle(16);
        circle.setFill(Color.web("#E0E7FF"));
        circle.setStroke(Color.web("#4F46E5"));
        circle.setStrokeWidth(1.5);

        Label label = new Label(initials);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        label.setTextFill(Color.web("#3730A3"));

        return new StackPane(circle, label);
    }

    // ------------------------------------------------------------------------
    // DATA MANAGEMENT & COMPUTATIONS
    // ------------------------------------------------------------------------

    private void loadDoctorData() {
        masterDoctorData = FXCollections.observableArrayList(
            new DoctorModel("DOC-101", "Dr. Rajesh Sharma", "Cardiology", "Interventional Cardiologist", "+91 98220 12345", "r.sharma@healthsphere.org", "OPD-102", "ON DUTY", "Morning (08:00 - 14:00)", "2021-03-15", "VERIFIED"),
            new DoctorModel("DOC-102", "Dr. Ananya Deshmukh", "Neurology", "Neurosurgeon & Specialist", "+91 97650 98765", "a.deshmukh@healthsphere.org", "OPD-204", "IN SURGERY", "Full Day (09:00 - 17:00)", "2019-07-22", "VERIFIED"),
            new DoctorModel("DOC-103", "Dr. Vikram Patil", "Orthopedics", "Joint Replacement Surgeon", "+91 94221 45678", "v.patil@healthsphere.org", "OPD-108", "ON DUTY", "Evening (14:00 - 20:00)", "2020-11-01", "PENDING"),
            new DoctorModel("DOC-104", "Dr. Meera Joshi", "Pediatrics", "Pediatric Intensivist", "+91 98902 34567", "m.joshi@healthsphere.org", "OPD-005", "ON LEAVE", "Night Shift", "2022-01-10", "VERIFIED"),
            new DoctorModel("DOC-105", "Dr. Siddharth Rao", "Oncology", "Medical Oncologist", "+91 91582 67890", "s.rao@healthsphere.org", "OPD-301", "ON DUTY", "Morning (08:00 - 14:00)", "2018-05-19", "PENDING")
        );

        filteredData = new FilteredList<>(masterDoctorData, d -> true);
        doctorTable.setItems(filteredData);

        updateCountersAndChart();
    }

    private void updateCountersAndChart() {
        int total = masterDoctorData.size();
        long active = masterDoctorData.stream().filter(d -> d.getStatus().equalsIgnoreCase("ON DUTY")).count();
        long leave = masterDoctorData.stream().filter(d -> d.getStatus().equalsIgnoreCase("ON LEAVE")).count();
        long surgery = masterDoctorData.stream().filter(d -> d.getStatus().equalsIgnoreCase("IN SURGERY")).count();

        totalCountLabel.setText(String.valueOf(total));
        activeCountLabel.setText(String.valueOf(active));
        leaveCountLabel.setText(String.valueOf(leave));
        surgeryCountLabel.setText(String.valueOf(surgery));

        departmentBarChart.getData().clear();

        Map<String, Long> deptCounts = masterDoctorData.stream()
                .collect(Collectors.groupingBy(DoctorModel::getDepartment, Collectors.counting()));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        deptCounts.forEach((dept, count) -> series.getData().add(new XYChart.Data<>(dept, count)));

        departmentBarChart.getData().add(series);
    }

    // ------------------------------------------------------------------------
    // DIALOGS & ACTIONS
    // ------------------------------------------------------------------------

    private void showDoctorProfileModal(DoctorModel doctor) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Doctor Credentials & Verification");
        dialog.setHeaderText("Specialist Profile: " + doctor.getName() + " (" + doctor.getDoctorId() + ")");

        VBox content = new VBox(14);
        content.setPadding(new Insets(15));
        content.setPrefWidth(420);

        Label details = new Label(
            "🩺 Department: " + doctor.getDepartment() + " | Specialization: " + doctor.getSpecialization() + "\n" +
            "🏢 OPD Location: " + doctor.getOpdRoom() + "\n" +
            "📞 Contact: " + doctor.getContactNo() + "\n" +
            "✉️ Email: " + doctor.getEmail() + "\n" +
            "⏰ Shift Schedule: " + doctor.getShift() + "\n" +
            "📅 Joining Date: " + doctor.getJoiningDate() + "\n" +
            "🏷️ Current Duty Status: " + doctor.getStatus() + "\n" +
            "🛡️ Current Approval Status: " + doctor.getVerificationStatus() + "\n\n" +
            "Weekly Consultation Hours:\n" +
            "• Mon - Thu: 09:00 AM - 01:00 PM (OPD)\n" +
            "• Fri: 02:00 PM - 06:00 PM (Rounds / Consultations)"
        );
        details.setWrapText(true);

        Label actionTitle = new Label("Change Verification Status:");
        actionTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        actionTitle.setTextFill(Color.web("#0F172A"));

        Button verifyBtn = new Button("✓ Verify & Approve");
        verifyBtn.setMaxWidth(Double.MAX_VALUE);
        verifyBtn.setStyle("-fx-background-color: #DCFCE7; -fx-text-fill: #15803D; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 8px; -fx-background-radius: 6px;");

        Button pendingBtn = new Button("⏳ Set as Pending");
        pendingBtn.setMaxWidth(Double.MAX_VALUE);
        pendingBtn.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #D97706; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 8px; -fx-background-radius: 6px;");

        Button rejectBtn = new Button("✕ Reject Verification");
        rejectBtn.setMaxWidth(Double.MAX_VALUE);
        rejectBtn.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #B91C1C; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 8px; -fx-background-radius: 6px;");

        verifyBtn.setOnAction(e -> {
            doctor.setVerificationStatus("VERIFIED");
            doctorTable.refresh();
            showAlert("Doctor Verified", doctor.getName() + " has been successfully verified and approved.");
            dialog.close();
        });

        pendingBtn.setOnAction(e -> {
            doctor.setVerificationStatus("PENDING");
            doctorTable.refresh();
            showAlert("Status Updated", doctor.getName() + " verification status is now set to PENDING.");
            dialog.close();
        });

        rejectBtn.setOnAction(e -> {
            doctor.setVerificationStatus("REJECTED");
            doctorTable.refresh();
            showAlert("Verification Rejected", doctor.getName() + " verification has been rejected.");
            dialog.close();
        });

        content.getChildren().addAll(details, new Separator(), actionTitle, verifyBtn, pendingBtn, rejectBtn);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void showAddDoctorDialog() {
        Dialog<DoctorModel> dialog = new Dialog<>();
        dialog.setTitle("Register New Medical Specialist");
        dialog.setHeaderText("Add New Doctor to HealthSphere Roster");

        ButtonType addDoctorButtonType = new ButtonType("Add Specialist", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addDoctorButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Dr. Full Name");

        ComboBox<String> deptCombo = new ComboBox<>();
        deptCombo.getItems().addAll("Cardiology", "Neurology", "Orthopedics", "Pediatrics", "Oncology", "General Medicine");
        deptCombo.setValue("Cardiology");

        TextField specField = new TextField();
        specField.setPromptText("e.g., Senior Surgeon");

        TextField phoneField = new TextField();
        phoneField.setPromptText("+91 98765 XXXXX");

        TextField emailField = new TextField();
        emailField.setPromptText("doctor@healthsphere.org");

        TextField roomField = new TextField();
        roomField.setPromptText("OPD-101");

        ComboBox<String> shiftCombo = new ComboBox<>();
        shiftCombo.getItems().addAll("Morning (08:00 - 14:00)", "Evening (14:00 - 20:00)", "Night Shift", "Full Day (09:00 - 17:00)");
        shiftCombo.setValue("Morning (08:00 - 14:00)");

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Department:"), 0, 1);
        grid.add(deptCombo, 1, 1);
        grid.add(new Label("Specialization:"), 0, 2);
        grid.add(specField, 1, 2);
        grid.add(new Label("Contact No:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Email Address:"), 0, 4);
        grid.add(emailField, 1, 4);
        grid.add(new Label("OPD Room:"), 0, 5);
        grid.add(roomField, 1, 5);
        grid.add(new Label("Shift Schedule:"), 0, 6);
        grid.add(shiftCombo, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addDoctorButtonType && !nameField.getText().trim().isEmpty()) {
                String newId = "DOC-" + (100 + masterDoctorData.size() + 1);
                String name = nameField.getText().trim().startsWith("Dr.") ? nameField.getText().trim() : "Dr. " + nameField.getText().trim();
                String spec = specField.getText().trim().isEmpty() ? "General Consultant" : specField.getText().trim();
                String phone = phoneField.getText().trim().isEmpty() ? "+91 90000 00000" : phoneField.getText().trim();
                String email = emailField.getText().trim().isEmpty() ? "doctor@healthsphere.org" : emailField.getText().trim();
                String room = roomField.getText().trim().isEmpty() ? "OPD-101" : roomField.getText().trim();

                return new DoctorModel(newId, name, deptCombo.getValue(), spec, phone, email, room, "ON DUTY", shiftCombo.getValue(), LocalDate.now().toString(), "PENDING");
            }
            return null;
        });

        Optional<DoctorModel> result = dialog.showAndWait();
        result.ifPresent(newDoctor -> {
            masterDoctorData.add(0, newDoctor);
            updateCountersAndChart();
            showAlert("Doctor Registered", newDoctor.getName() + " has been registered and is pending admin verification.");
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
    // INNER MODEL CLASS
    // ------------------------------------------------------------------------

    public static class DoctorModel {
        private final String doctorId;
        private final String name;
        private final String department;
        private final String specialization;
        private final String contactNo;
        private final String email;
        private final String opdRoom;
        private String status;
        private final String shift;
        private final String joiningDate;
        private String verificationStatus;

        public DoctorModel(String doctorId, String name, String department, String specialization, String contactNo, String email, String opdRoom, String status, String shift, String joiningDate, String verificationStatus) {
            this.doctorId = doctorId;
            this.name = name;
            this.department = department;
            this.specialization = specialization;
            this.contactNo = contactNo;
            this.email = email;
            this.opdRoom = opdRoom;
            this.status = status;
            this.shift = shift;
            this.joiningDate = joiningDate;
            this.verificationStatus = verificationStatus;
        }

        public String getDoctorId() { return doctorId; }
        public String getName() { return name; }
        public String getDepartment() { return department; }
        public String getSpecialization() { return specialization; }
        public String getContactNo() { return contactNo; }
        public String getEmail() { return email; }
        public String getOpdRoom() { return opdRoom; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getShift() { return shift; }
        public String getJoiningDate() { return joiningDate; }
        public String getVerificationStatus() { return verificationStatus; }
        public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
    }
}