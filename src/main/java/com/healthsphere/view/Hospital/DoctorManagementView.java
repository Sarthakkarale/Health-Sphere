package com.healthsphere.view.Hospital;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DoctorManagementView {

    // =========================================================
    // COLOR PALETTE (Modern Light Theme with Dark Sidebar)
    // =========================================================

    private static final String PRIMARY_BLUE = "#170eca";
    private static final String PRIMARY_LIGHT = "#EFF5FF";
    private static final String DARK_TEXT = "#0F172A";
    private static final String SECONDARY_TEXT = "#64748B";
    private static final String LIGHT_BACKGROUND = "#F8FAFC";
    private static final String CARD_BG = "#FFFFFF";
    private static final String BORDER = "#E2E8F0";

    // Dark Sidebar Theme Colors
    private static final String SIDEBAR_BG = "#0F172A";
    private static final String SIDEBAR_HOVER = "#1E293B";
    private static final String SIDEBAR_TEXT_MUTED = "#94A3B8";
    private static final String DARK_ACCENT = "#38BDF8";
    private static final String SIDEBAR_BORDER = "#1E293B";

    private static final String SUCCESS_GREEN = "#059669";
    private static final String SUCCESS_LIGHT = "#ECFDF5";

    private static final String ERROR_RED = "#DC2626";
    private static final String ERROR_LIGHT = "#FEF2F2";

    private static final String WARNING_ORANGE = "#D97706";
    private static final String WARNING_LIGHT = "#FFFBEB";

    private static final String PURPLE = "#7C3AED";
    private static final String PURPLE_LIGHT = "#F5F3FF";

    // =========================================================
    // DATA MODELS & STATE MANAGEMENT
    // =========================================================

    public static class Doctor {
        private String id;
        private String name;
        private String department;
        private String qualification;
        private String status;

        public Doctor(String id, String name, String department, String qualification, String status) {
            this.id = id;
            this.name = name;
            this.department = department;
            this.qualification = qualification;
            this.status = status;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getQualification() { return qualification; }
        public void setQualification(String qualification) { this.qualification = qualification; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getInitials() {
            if (name == null || name.isEmpty()) return "DR";
            String cleanName = name.replace("Dr. ", "").trim();
            String[] parts = cleanName.split("\\s+");
            if (parts.length >= 2) {
                return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
            } else if (parts.length == 1 && !parts[0].isEmpty()) {
                return ("" + parts[0].charAt(0)).toUpperCase();
            }
            return "DR";
        }
    }

    private final ObservableList<Doctor> masterDoctorList = FXCollections.observableArrayList();
    private FilteredList<Doctor> filteredDoctorList;

    private Label totalDocsValLabel;
    private Label activeDocsValLabel;
    private Label onLeaveDocsValLabel;
    private Label totalDeptValLabel;
    private Label doctorCountHeaderLabel;
    private VBox doctorRowsContainer;

    private TextField searchField;
    private ComboBox<String> departmentFilter;
    private ComboBox<String> statusFilter;

    // =========================================================
    // CREATE SCENE
    // =========================================================

    public Scene createScene(Stage stage) {
        initSampleData();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + LIGHT_BACKGROUND + ";");

        root.setLeft(createSidebar(stage));
        root.setTop(createTopBar());

        // Wrap main content in a ScrollPane for smooth responsiveness
        ScrollPane scrollPane = new ScrollPane(createMainContent(stage));
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");

        root.setCenter(scrollPane);

        // Initial Filter and Dynamic UI refresh
        applyFiltersAndRefreshUI();

        return new Scene(root, stage.getWidth(), stage.getHeight());
    }

    private void initSampleData() {
        if (masterDoctorList.isEmpty()) {
            masterDoctorList.add(new Doctor("DOC-1024", "Dr. Ananya Sharma", "Cardiology", "MD, Cardiology", "Active"));
            masterDoctorList.add(new Doctor("DOC-1025", "Dr. Rahul Patil", "Neurology", "MD, Neurology", "Active"));
            masterDoctorList.add(new Doctor("DOC-1026", "Dr. Priya Mehta", "Pediatrics", "MD, Pediatrics", "On Leave"));
            masterDoctorList.add(new Doctor("DOC-1027", "Dr. Amit Joshi", "Orthopedics", "MS, Orthopedics", "Inactive"));
        }
        filteredDoctorList = new FilteredList<>(masterDoctorList, p -> true);
    }

    // =========================================================
    // DARK SIDEBAR (Standardized layout across views)
    // =========================================================

    private VBox createSidebar(Stage stage) {

        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(240);
        sidebar.setPadding(new Insets(24, 16, 20, 16));

        sidebar.setStyle(
                "-fx-background-color: " + SIDEBAR_BG + ";" +
                "-fx-border-color: " + SIDEBAR_BORDER + ";" +
                "-fx-border-width: 0 1 0 0;"
        );

        // LOGO
        VBox logoBox = new VBox(2);
        logoBox.setPadding(new Insets(0, 8, 24, 8));

        Label logo = new Label("Health-Sphere");
        logo.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: #FFFFFF;"
        );

        Label subtitle = new Label("SMART HEALTHCARE");
        subtitle.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-letter-spacing: 1px;" +
                "-fx-text-fill: " + SIDEBAR_TEXT_MUTED + ";"
        );

        logoBox.getChildren().addAll(logo, subtitle);
        sidebar.getChildren().add(logoBox);

        // NAVIGATION BUTTONS (Doctors Selected)
        Button dashboardButton = createNavigationButton("▦", "Dashboard", false);
        Button doctorButton = createNavigationButton("♙", "Doctors", true);
        Button departmentButton = createNavigationButton("✚", "Departments", false);
        Button bedButton = createNavigationButton("▥", "Beds", false);
        Button appointmentButton = createNavigationButton("▣", "Appointments", false);
        Button analyticsButton = createNavigationButton("◈", "Analytics", false);
        Button settingsButton = createNavigationButton("⚙", "Hospital Settings", false);

        sidebar.getChildren().addAll(
                dashboardButton,
                doctorButton,
                departmentButton,
                bedButton,
                appointmentButton,
                analyticsButton,
                settingsButton
        );

        // DIRECT NAVIGATION
        dashboardButton.setOnAction(event -> stage.setScene(new HospitalDashboardView().createScene(stage)));
        departmentButton.setOnAction(event -> stage.setScene(new DepartmentManagementView().createScene(stage)));
        bedButton.setOnAction(event -> stage.setScene(new BedManagementView().createScene(stage)));
        appointmentButton.setOnAction(event -> stage.setScene(new AppointmentManagementView().createScene(stage)));
        analyticsButton.setOnAction(event -> stage.setScene(new HospitalAnalyticsView().createScene(stage)));
        settingsButton.setOnAction(event -> stage.setScene(new HospitalProfileSettingsView().createScene(stage)));

        // SPACER
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);

        // HELP & LOGOUT
        Button helpButton = createNavigationButton("?", "Help Center", false);
        Button logoutButton = createNavigationButton("↪", "Logout", false);

        helpButton.setOnAction(e -> showAlert("Help Center", "For assistance, please contact support@healthsphere.com"));
        logoutButton.setOnAction(e -> showAlert("Logout", "Logged out successfully."));

        sidebar.getChildren().addAll(helpButton, logoutButton);

        return sidebar;
    }

    private Button createNavigationButton(String icon, String text, boolean selected) {

        Button button = new Button();

        Label iconLabel = new Label(icon);
        iconLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-text-fill: " + (selected ? "#FFFFFF" : SIDEBAR_TEXT_MUTED) + ";"
        );

        Label textLabel = new Label(text);
        textLabel.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: " + (selected ? "bold" : "500") + ";" +
                "-fx-text-fill: " + (selected ? "#FFFFFF" : SIDEBAR_TEXT_MUTED) + ";"
        );

        HBox content = new HBox(12);
        content.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().addAll(iconLabel, textLabel);

        button.setGraphic(content);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(42);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(0, 12, 0, 12));

        String baseStyle = "-fx-background-radius: 8; -fx-cursor: hand;";

        if (selected) {
            button.setStyle(baseStyle + "-fx-background-color: " + PRIMARY_BLUE + ";");
        } else {
            button.setStyle(baseStyle + "-fx-background-color: transparent;");

            button.setOnMouseEntered(e -> button.setStyle(baseStyle + "-fx-background-color: " + SIDEBAR_HOVER + ";"));
            button.setOnMouseExited(e -> button.setStyle(baseStyle + "-fx-background-color: transparent;"));
        }

        return button;
    }

    // =========================================================
    // TOP BAR
    // =========================================================

    private HBox createTopBar() {

        HBox topBar = new HBox(16);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 28, 12, 28));

        topBar.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 0 0 1 0;"
        );

        Label searchIcon = new Label("⌕");
        searchIcon.setStyle("-fx-font-size: 18px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        TextField topSearchField = new TextField();
        topSearchField.setPromptText("Search doctors, departments...");
        topSearchField.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-prompt-text-fill: #94A3B8;" +
                "-fx-font-size: 13px;" +
                "-fx-text-inner-color: " + DARK_TEXT + ";"
        );
        topSearchField.textProperty().addListener((obs, oldV, newV) -> {
            if (searchField != null) {
                searchField.setText(newV);
            }
        });
        HBox.setHgrow(topSearchField, Priority.ALWAYS);

        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPrefWidth(360);
        searchBox.setPrefHeight(40);
        searchBox.setPadding(new Insets(0, 12, 0, 12));
        searchBox.setStyle(
                "-fx-background-color: " + LIGHT_BACKGROUND + ";" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 8;"
        );
        searchBox.getChildren().addAll(searchIcon, topSearchField);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label notification = new Label("🔔");
        notification.setStyle("-fx-font-size: 16px; -fx-cursor: hand; -fx-text-fill: " + SECONDARY_TEXT + ";");
        notification.setOnMouseClicked(e -> showAlert("Notifications", "You have 0 new notifications."));

        Label settings = new Label("⚙");
        settings.setStyle("-fx-font-size: 18px; -fx-cursor: hand; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label administrator = new Label("Hospital Administrator");
        administrator.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TEXT + ";");

        Label role = new Label("HOSPITAL ADMIN");
        role.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");

        VBox userInfo = new VBox(2);
        userInfo.setAlignment(Pos.CENTER_RIGHT);
        userInfo.getChildren().addAll(administrator, role);

        Circle avatar = new Circle(18);
        avatar.setFill(Color.web(PRIMARY_LIGHT));
        avatar.setStroke(Color.web(BORDER));

        Label avatarText = new Label("HA");
        avatarText.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        StackPane avatarBox = new StackPane(avatar, avatarText);

        topBar.getChildren().addAll(searchBox, spacer, notification, settings, userInfo, avatarBox);

        return topBar;
    }

    // =========================================================
    // MAIN CONTENT
    // =========================================================

    private VBox createMainContent(Stage stage) {

        VBox content = new VBox(24);
        content.setPadding(new Insets(28));
        content.setStyle("-fx-background-color: " + LIGHT_BACKGROUND + ";");

        content.getChildren().addAll(
                createPageHeader(stage),
                createStatistics(),
                createSearchAndFilters(stage),
                createDoctorList(stage)
        );

        return content;
    }

    // =========================================================
    // PAGE HEADER
    // =========================================================

    private HBox createPageHeader(Stage stage) {

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);

        Label title = new Label("Doctor Management");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 800; -fx-text-fill: " + DARK_TEXT + ";");

        Label subtitle = new Label("Manage doctors, departments and availability");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addDoctor = new Button("＋ Add Doctor");
        addDoctor.setPrefHeight(42);
        addDoctor.setPadding(new Insets(0, 20, 0, 20));

        String actionBtnStyle = 
                "-fx-background-color: " + PRIMARY_BLUE + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;";

        addDoctor.setStyle(actionBtnStyle);
        addDoctor.setOnMouseEntered(e -> addDoctor.setStyle(actionBtnStyle + "-fx-background-color: #1550B0;"));
        addDoctor.setOnMouseExited(e -> addDoctor.setStyle(actionBtnStyle));

        // Add Doctor Action
        addDoctor.setOnAction(e -> showAddDoctorDialog(stage));

        header.getChildren().addAll(titleBox, spacer, addDoctor);

        return header;
    }

    // =========================================================
    // STATISTICS
    // =========================================================

    private HBox createStatistics() {

        HBox statistics = new HBox(18);

        VBox totalCard = createStatisticCard("Total Doctors", "0", "Across all departments", "♙", PRIMARY_BLUE, PRIMARY_LIGHT);
        VBox activeCard = createStatisticCard("Active Doctors", "0", "Currently available", "✓", SUCCESS_GREEN, SUCCESS_LIGHT);
        VBox leaveCard = createStatisticCard("On Leave", "0", "Currently unavailable", "◷", WARNING_ORANGE, WARNING_LIGHT);
        VBox deptCard = createStatisticCard("Departments", "0", "Medical departments", "✚", PURPLE, PURPLE_LIGHT);

        totalDocsValLabel = (Label) totalCard.getChildren().get(1);
        activeDocsValLabel = (Label) activeCard.getChildren().get(1);
        onLeaveDocsValLabel = (Label) leaveCard.getChildren().get(1);
        totalDeptValLabel = (Label) deptCard.getChildren().get(1);

        statistics.getChildren().addAll(totalCard, activeCard, leaveCard, deptCard);

        for (javafx.scene.Node node : statistics.getChildren()) {
            HBox.setHgrow(node, Priority.ALWAYS);
        }

        return statistics;
    }

    private VBox createStatisticCard(String title, String value, String subtitle, String icon, String color, String bgColor) {

        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setPrefHeight(120);

        applyCardStyle(card);

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label iconLabel = new Label(icon);
        iconLabel.setPrefSize(36, 36);
        iconLabel.setAlignment(Pos.CENTER);
        iconLabel.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                "-fx-background-radius: 8;" +
                "-fx-text-fill: " + color + ";" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;"
        );

        top.getChildren().addAll(titleLabel, spacer, iconLabel);

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: 800; -fx-text-fill: " + DARK_TEXT + ";");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(top, valueLabel, subtitleLabel);

        return card;
    }

    // =========================================================
    // SEARCH AND FILTERS
    // =========================================================

    private HBox createSearchAndFilters(Stage stage) {

        HBox container = new HBox(12);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(16));
        applyCardStyle(container);

        searchField = new TextField();
        searchField.setPromptText("Search by doctor name...");
        searchField.setPrefHeight(40);
        searchField.setPrefWidth(320);
        searchField.setStyle(
                "-fx-background-color: " + LIGHT_BACKGROUND + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 0 14;" +
                "-fx-font-size: 12px;"
        );
        searchField.textProperty().addListener((obs, oldV, newV) -> applyFiltersAndRefreshUI());

        departmentFilter = new ComboBox<>();
        departmentFilter.getItems().addAll(
                "All Departments",
                "Cardiology",
                "Neurology",
                "Orthopedics",
                "Pediatrics",
                "General Medicine"
        );
        departmentFilter.setValue("All Departments");
        departmentFilter.setPrefHeight(40);
        departmentFilter.setStyle("-fx-font-size: 12px;");
        departmentFilter.setOnAction(e -> applyFiltersAndRefreshUI());

        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll(
                "All Status",
                "Active",
                "Inactive",
                "On Leave"
        );
        statusFilter.setValue("All Status");
        statusFilter.setPrefHeight(40);
        statusFilter.setStyle("-fx-font-size: 12px;");
        statusFilter.setOnAction(e -> applyFiltersAndRefreshUI());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button filterButton = new Button("☷  Reset Filters");
        filterButton.setPrefHeight(40);
        filterButton.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-text-fill: " + DARK_TEXT + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: 600;" +
                "-fx-cursor: hand;"
        );
        filterButton.setOnAction(e -> {
            searchField.clear();
            departmentFilter.setValue("All Departments");
            statusFilter.setValue("All Status");
            applyFiltersAndRefreshUI();
        });

        Button exportButton = new Button("↓  Export");
        exportButton.setPrefHeight(40);
        exportButton.setPadding(new Insets(0, 16, 0, 16));
        exportButton.setStyle(
                "-fx-background-color: " + PRIMARY_LIGHT + ";" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );
        exportButton.setOnAction(e -> exportDoctorDataToCSV(stage));

        container.getChildren().addAll(searchField, departmentFilter, statusFilter, spacer, filterButton, exportButton);

        return container;
    }

    // =========================================================
    // DOCTOR LIST
    // =========================================================

    private VBox createDoctorList(Stage stage) {

        VBox card = new VBox(0);
        applyCardStyle(card);

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20, 24, 20, 24));

        Label title = new Label("Doctors Directory");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: " + DARK_TEXT + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        doctorCountHeaderLabel = new Label("0 Doctors");
        doctorCountHeaderLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(title, spacer, doctorCountHeaderLabel);
        card.getChildren().add(header);

        card.getChildren().add(new Separator());

        // Column header
        card.getChildren().add(createTableHeader());

        card.getChildren().add(new Separator());

        // Rows Container
        doctorRowsContainer = new VBox(0);
        card.getChildren().add(doctorRowsContainer);

        return card;
    }

    private HBox createTableHeader() {

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(12, 24, 12, 24));
        header.setStyle("-fx-background-color: #FAFAFA;");

        Label doctor = createHeaderLabel("DOCTOR", 280);
        Label department = createHeaderLabel("DEPARTMENT", 160);
        Label qualification = createHeaderLabel("QUALIFICATION", 180);
        Label status = createHeaderLabel("STATUS", 120);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label actions = createHeaderLabel("ACTIONS", 180);
        actions.setAlignment(Pos.CENTER_RIGHT);

        header.getChildren().addAll(doctor, department, qualification, status, spacer, actions);

        return header;
    }

    private Label createHeaderLabel(String text, double width) {

        Label label = new Label(text);
        label.setPrefWidth(width);
        label.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        return label;
    }

    // =========================================================
    // DYNAMIC FILTERING & REFRESH LOGIC
    // =========================================================

    private void applyFiltersAndRefreshUI() {
        String searchText = searchField != null && searchField.getText() != null ? searchField.getText().toLowerCase().trim() : "";
        String deptVal = departmentFilter != null && departmentFilter.getValue() != null ? departmentFilter.getValue() : "All Departments";
        String statusVal = statusFilter != null && statusFilter.getValue() != null ? statusFilter.getValue() : "All Status";

        filteredDoctorList.setPredicate(doc -> {
            boolean matchesSearch = searchText.isEmpty() ||
                    doc.getName().toLowerCase().contains(searchText) ||
                    doc.getId().toLowerCase().contains(searchText) ||
                    doc.getQualification().toLowerCase().contains(searchText);

            boolean matchesDept = deptVal.equals("All Departments") || doc.getDepartment().equalsIgnoreCase(deptVal);
            boolean matchesStatus = statusVal.equals("All Status") || doc.getStatus().equalsIgnoreCase(statusVal);

            return matchesSearch && matchesDept && matchesStatus;
        });

        rebuildTableRows();
        updateStatistics();
    }

    private void rebuildTableRows() {
        if (doctorRowsContainer == null) return;
        doctorRowsContainer.getChildren().clear();

        if (filteredDoctorList.isEmpty()) {
            Label emptyLabel = new Label("No doctors match the selected search criteria.");
            emptyLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 24;");
            doctorRowsContainer.getChildren().add(emptyLabel);
            return;
        }

        for (int i = 0; i < filteredDoctorList.size(); i++) {
            Doctor doc = filteredDoctorList.get(i);

            String statusColor = SUCCESS_GREEN;
            String statusBgColor = SUCCESS_LIGHT;

            if ("On Leave".equalsIgnoreCase(doc.getStatus())) {
                statusColor = WARNING_ORANGE;
                statusBgColor = WARNING_LIGHT;
            } else if ("Inactive".equalsIgnoreCase(doc.getStatus())) {
                statusColor = ERROR_RED;
                statusBgColor = ERROR_LIGHT;
            }

            HBox row = createDoctorRow(doc, statusColor, statusBgColor);
            doctorRowsContainer.getChildren().add(row);

            if (i < filteredDoctorList.size() - 1) {
                doctorRowsContainer.getChildren().add(new Separator());
            }
        }
    }

    private void updateStatistics() {
        int total = masterDoctorList.size();
        int active = 0;
        int onLeave = 0;
        Set<String> departments = new HashSet<>();

        for (Doctor d : masterDoctorList) {
            if ("Active".equalsIgnoreCase(d.getStatus())) active++;
            else if ("On Leave".equalsIgnoreCase(d.getStatus())) onLeave++;
            if (d.getDepartment() != null && !d.getDepartment().trim().isEmpty()) {
                departments.add(d.getDepartment().trim());
            }
        }

        if (totalDocsValLabel != null) totalDocsValLabel.setText(String.valueOf(total));
        if (activeDocsValLabel != null) activeDocsValLabel.setText(String.valueOf(active));
        if (onLeaveDocsValLabel != null) onLeaveDocsValLabel.setText(String.valueOf(onLeave));
        if (totalDeptValLabel != null) totalDeptValLabel.setText(String.valueOf(departments.size()));
        if (doctorCountHeaderLabel != null) doctorCountHeaderLabel.setText(filteredDoctorList.size() + " Doctors");
    }

    private HBox createDoctorRow(
            Doctor doctor,
            String statusColor,
            String statusBgColor
    ) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14, 24, 14, 24));

        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #F8FAFC;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-background-color: transparent;"));

        // Doctor Info
        HBox doctorBox = new HBox(12);
        doctorBox.setAlignment(Pos.CENTER_LEFT);
        doctorBox.setPrefWidth(280);

        Circle avatar = new Circle(18);
        avatar.setFill(Color.web(PRIMARY_LIGHT));

        Label initialsLabel = new Label(doctor.getInitials());
        initialsLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";"
        );

        StackPane avatarPane = new StackPane(avatar, initialsLabel);

        VBox doctorInfo = new VBox(2);

        Label nameLabel = new Label(doctor.getName());
        nameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: " + DARK_TEXT + ";");

        Label idLabel = new Label("Doctor ID: " + doctor.getId());
        idLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        doctorInfo.getChildren().addAll(nameLabel, idLabel);
        doctorBox.getChildren().addAll(avatarPane, doctorInfo);

        // Department
        Label departmentLabel = new Label(doctor.getDepartment());
        departmentLabel.setPrefWidth(160);
        departmentLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: " + DARK_TEXT + ";");

        // Qualification
        Label qualificationLabel = new Label(doctor.getQualification());
        qualificationLabel.setPrefWidth(180);
        qualificationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        // Status Tag
        Label statusLabel = new Label(doctor.getStatus());
        statusLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: " + statusColor + ";" +
                "-fx-background-color: " + statusBgColor + ";" +
                "-fx-padding: 4 10;" +
                "-fx-background-radius: 12;"
        );

        HBox statusBox = new HBox(statusLabel);
        statusBox.setPrefWidth(120);
        statusBox.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Actions
        HBox actions = new HBox(8);
        actions.setPrefWidth(180);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button viewButton = createSmallButton("View", PRIMARY_BLUE, PRIMARY_LIGHT);
        Button editButton = createSmallButton("Edit", PURPLE, PURPLE_LIGHT);
        Button deleteButton = createSmallButton("Delete", ERROR_RED, ERROR_LIGHT);

        // Action Handlers
        viewButton.setOnAction(e -> showViewDoctorDialog(doctor));
        editButton.setOnAction(e -> showEditDoctorDialog(doctor));
        deleteButton.setOnAction(e -> handleDeleteDoctor(doctor));

        actions.getChildren().addAll(viewButton, editButton, deleteButton);

        row.getChildren().addAll(doctorBox, departmentLabel, qualificationLabel, statusBox, spacer, actions);

        return row;
    }

    private Button createSmallButton(String text, String color, String bgColor) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                "-fx-text-fill: " + color + ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 4 10;"
        );
        return btn;
    }

    // =========================================================
    // DIALOGS & ACTIONS
    // =========================================================

    private void showAddDoctorDialog(Stage parentStage) {
        Stage dialog = createModalDialog(parentStage, "Add New Doctor");

        VBox form = new VBox(16);
        form.setPadding(new Insets(24));

        TextField nameInput = createInputField("Full Name (e.g. Dr. John Doe)");
        ComboBox<String> deptInput = new ComboBox<>(FXCollections.observableArrayList(
                "Cardiology", "Neurology", "Orthopedics", "Pediatrics", "General Medicine"
        ));
        deptInput.setPromptText("Select Department");
        deptInput.setMaxWidth(Double.MAX_VALUE);

        TextField qualInput = createInputField("Qualification (e.g. MD, Cardiology)");
        ComboBox<String> statusInput = new ComboBox<>(FXCollections.observableArrayList("Active", "Inactive", "On Leave"));
        statusInput.setValue("Active");
        statusInput.setMaxWidth(Double.MAX_VALUE);

        Button saveBtn = new Button("Add Doctor");
        saveBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setPrefHeight(40);

        saveBtn.setOnAction(e -> {
            if (nameInput.getText().trim().isEmpty() || deptInput.getValue() == null) {
                showAlert("Validation Error", "Please provide at least a name and department.");
                return;
            }

            String newId = "DOC-" + (1024 + masterDoctorList.size() + 1);
            Doctor newDoc = new Doctor(
                    newId,
                    nameInput.getText().trim(),
                    deptInput.getValue(),
                    qualInput.getText().trim().isEmpty() ? "MBBS" : qualInput.getText().trim(),
                    statusInput.getValue()
            );

            masterDoctorList.add(newDoc);
            applyFiltersAndRefreshUI();
            dialog.close();
            showAlert("Success", "Doctor added successfully with ID " + newId);
        });

        form.getChildren().addAll(
                new Label("Doctor Name:"), nameInput,
                new Label("Department:"), deptInput,
                new Label("Qualification:"), qualInput,
                new Label("Initial Status:"), statusInput,
                new Region(), saveBtn
        );

        dialog.setScene(new Scene(form, 400, 420));
        dialog.showAndWait();
    }

    private void showEditDoctorDialog(Doctor doctor) {
        Stage dialog = createModalDialog((Stage) doctorRowsContainer.getScene().getWindow(), "Edit Doctor - " + doctor.getId());

        VBox form = new VBox(16);
        form.setPadding(new Insets(24));

        TextField nameInput = createInputField("Full Name");
        nameInput.setText(doctor.getName());

        ComboBox<String> deptInput = new ComboBox<>(FXCollections.observableArrayList(
                "Cardiology", "Neurology", "Orthopedics", "Pediatrics", "General Medicine"
        ));
        deptInput.setValue(doctor.getDepartment());
        deptInput.setMaxWidth(Double.MAX_VALUE);

        TextField qualInput = createInputField("Qualification");
        qualInput.setText(doctor.getQualification());

        ComboBox<String> statusInput = new ComboBox<>(FXCollections.observableArrayList("Active", "Inactive", "On Leave"));
        statusInput.setValue(doctor.getStatus());
        statusInput.setMaxWidth(Double.MAX_VALUE);

        Button saveBtn = new Button("Save Changes");
        saveBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setPrefHeight(40);

        saveBtn.setOnAction(e -> {
            doctor.setName(nameInput.getText().trim());
            doctor.setDepartment(deptInput.getValue());
            doctor.setQualification(qualInput.getText().trim());
            doctor.setStatus(statusInput.getValue());

            applyFiltersAndRefreshUI();
            dialog.close();
            showAlert("Updated", "Doctor details updated successfully.");
        });

        form.getChildren().addAll(
                new Label("Doctor Name:"), nameInput,
                new Label("Department:"), deptInput,
                new Label("Qualification:"), qualInput,
                new Label("Status:"), statusInput,
                new Region(), saveBtn
        );

        dialog.setScene(new Scene(form, 400, 420));
        dialog.showAndWait();
    }

    private void showViewDoctorDialog(Doctor doctor) {
        Stage dialog = createModalDialog((Stage) doctorRowsContainer.getScene().getWindow(), "Doctor Details");

        VBox content = new VBox(12);
        content.setPadding(new Insets(24));

        content.getChildren().addAll(
                new Label("ID: " + doctor.getId()),
                new Label("Name: " + doctor.getName()),
                new Label("Department: " + doctor.getDepartment()),
                new Label("Qualification: " + doctor.getQualification()),
                new Label("Status: " + doctor.getStatus())
        );

        for (javafx.scene.Node n : content.getChildren()) {
            n.setStyle("-fx-font-size: 14px; -fx-text-fill: " + DARK_TEXT + ";");
        }

        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> dialog.close());
        content.getChildren().add(closeBtn);

        dialog.setScene(new Scene(content, 320, 260));
        dialog.showAndWait();
    }

    private void handleDeleteDoctor(Doctor doctor) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Doctor");
        alert.setHeaderText("Remove " + doctor.getName() + "?");
        alert.setContentText("Are you sure you want to remove this doctor from records?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            masterDoctorList.remove(doctor);
            applyFiltersAndRefreshUI();
            showAlert("Deleted", doctor.getName() + " has been removed.");
        }
    }

    private void exportDoctorDataToCSV(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Doctor List");
        fileChooser.setInitialFileName("Doctors_Export.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("ID,Name,Department,Qualification,Status");
                for (Doctor d : filteredDoctorList) {
                    writer.println(String.format("%s,\"%s\",\"%s\",\"%s\",%s",
                            d.getId(), d.getName(), d.getDepartment(), d.getQualification(), d.getStatus()));
                }
                showAlert("Export Success", "Data exported successfully to " + file.getName());
            } catch (Exception ex) {
                showAlert("Export Error", "Failed to export data: " + ex.getMessage());
            }
        }
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    private TextField createInputField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefHeight(38);
        tf.setStyle("-fx-background-color: " + LIGHT_BACKGROUND + "; -fx-border-color: " + BORDER + "; -fx-border-radius: 6; -fx-background-radius: 6;");
        return tf;
    }

    private Stage createModalDialog(Stage parent, String title) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(parent);
        dialog.setTitle(title);
        return dialog;
    }

    private void applyCardStyle(Pane card) {
        card.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;"
        );
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(15, 23, 42, 0.04));
        shadow.setRadius(8);
        shadow.setOffsetY(2);
        card.setEffect(shadow);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}