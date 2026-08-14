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
import java.util.Optional;

public class DepartmentManagementView {

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

    public static class Appointment {
        private String id;
        private String patientName;
        private String doctorName;
        private String timeSlot;
        private String status; // Scheduled, In-Progress, Completed

        public Appointment(String id, String patientName, String doctorName, String timeSlot, String status) {
            this.id = id;
            this.patientName = patientName;
            this.doctorName = doctorName;
            this.timeSlot = timeSlot;
            this.status = status;
        }

        public String getId() { return id; }
        public String getPatientName() { return patientName; }
        public String getDoctorName() { return doctorName; }
        public String getTimeSlot() { return timeSlot; }
        public String getStatus() { return status; }
    }

    public static class Department {
        private String name;
        private String head;
        private int doctorCount;
        private int patientCount;
        private String category; // Clinical, Surgical, Diagnostic, Emergency, Support
        private String themeColor;
        private String themeBgColor;
        private String icon;
        private boolean is247;
        private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();

        public Department(String name, String head, int doctorCount, int patientCount, String category,
                          String themeColor, String themeBgColor, String icon, boolean is247) {
            this.name = name;
            this.head = head;
            this.doctorCount = doctorCount;
            this.patientCount = patientCount;
            this.category = category;
            this.themeColor = themeColor;
            this.themeBgColor = themeBgColor;
            this.icon = icon;
            this.is247 = is247;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getHead() { return head; }
        public void setHead(String head) { this.head = head; }
        public int getDoctorCount() { return doctorCount; }
        public void setDoctorCount(int doctorCount) { this.doctorCount = doctorCount; }
        public int getPatientCount() { return patientCount; }
        public void setPatientCount(int patientCount) { this.patientCount = patientCount; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getThemeColor() { return themeColor; }
        public String getThemeBgColor() { return themeBgColor; }
        public String getIcon() { return icon; }
        public boolean isIs247() { return is247; }
        public ObservableList<Appointment> getAppointments() { return appointments; }
    }

    private final ObservableList<Department> masterDepartmentList = FXCollections.observableArrayList();
    private FilteredList<Department> filteredDepartmentList;

    private Label totalDeptValLabel;
    private Label totalDocsValLabel;
    private Label activeDeptValLabel;
    private Label emergencyDeptValLabel;
    private Label departmentCountHeaderLabel;

    private FlowPane departmentGridPane;
    private TextField searchField;
    private ComboBox<String> categoryFilter;

    // =========================================================
    // CREATE SCENE
    // =========================================================

    public Scene createScene(Stage stage) {
        initSampleData();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + LIGHT_BACKGROUND + ";");

        root.setLeft(createSidebar(stage));
        root.setTop(createTopBar());

        ScrollPane scrollPane = new ScrollPane(createMainContent(stage));
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");

        root.setCenter(scrollPane);

        applyFiltersAndRefreshUI();

        return new Scene(root, stage.getWidth(), stage.getHeight());
    }

    private void initSampleData() {
        if (masterDepartmentList.isEmpty()) {
            Department card = new Department("Cardiology", "Dr. Ananya Sharma", 24, 186, "Clinical", PRIMARY_BLUE, PRIMARY_LIGHT, "♥", true);
            card.getAppointments().addAll(
                    new Appointment("APT-101", "Rohan Verma", "Dr. Ananya Sharma", "10:30 AM", "Scheduled"),
                    new Appointment("APT-102", "Sita Ram", "Dr. Rajesh Iyer", "11:15 AM", "In-Progress")
            );

            Department neuro = new Department("Neurology", "Dr. Rahul Patil", 18, 142, "Clinical", PURPLE, PURPLE_LIGHT, "◉", false);
            neuro.getAppointments().addAll(
                    new Appointment("APT-201", "Kavita Shah", "Dr. Rahul Patil", "09:45 AM", "Completed"),
                    new Appointment("APT-202", "Amitabh Sen", "Dr. Sunita Rao", "02:00 PM", "Scheduled")
            );

            Department ortho = new Department("Orthopedics", "Dr. Amit Joshi", 16, 128, "Surgical", SUCCESS_GREEN, SUCCESS_LIGHT, "⌁", false);
            ortho.getAppointments().addAll(
                    new Appointment("APT-301", "Vikram Malhotra", "Dr. Amit Joshi", "11:00 AM", "Scheduled")
            );

            Department pedia = new Department("Pediatrics", "Dr. Priya Mehta", 14, 115, "Clinical", WARNING_ORANGE, WARNING_LIGHT, "♧", false);
            pedia.getAppointments().addAll(
                    new Appointment("APT-401", "Baby Aarav", "Dr. Priya Mehta", "10:00 AM", "Scheduled")
            );

            Department emer = new Department("Emergency", "Dr. Vikram Singh", 20, 94, "Emergency", ERROR_RED, ERROR_LIGHT, "!", true);
            emer.getAppointments().addAll(
                    new Appointment("APT-501", "Critical Patient #1", "Dr. Vikram Singh", "Immediate", "In-Progress"),
                    new Appointment("APT-502", "Trauma Case #2", "Dr. Neeta Deshmukh", "Immediate", "Scheduled")
            );

            Department gen = new Department("General Medicine", "Dr. Neha Kulkarni", 22, 203, "Clinical", PRIMARY_BLUE, PRIMARY_LIGHT, "+", true);
            gen.getAppointments().addAll(
                    new Appointment("APT-601", "Suresh Kumar", "Dr. Neha Kulkarni", "01:30 PM", "Scheduled")
            );

            masterDepartmentList.addAll(card, neuro, ortho, pedia, emer, gen);
        }
        filteredDepartmentList = new FilteredList<>(masterDepartmentList, p -> true);
    }

    // =========================================================
    // DARK SIDEBAR
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

        // NAVIGATION BUTTONS
        Button dashboardButton = createNavigationButton("▦", "Dashboard", false);
        Button doctorButton = createNavigationButton("♙", "Doctors", false);
        Button departmentButton = createNavigationButton("✚", "Departments", true);
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
        doctorButton.setOnAction(event -> stage.setScene(new DoctorManagementView().createScene(stage)));
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

        TextField topSearch = new TextField();
        topSearch.setPromptText("Search departments...");
        topSearch.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-prompt-text-fill: #94A3B8;" +
                "-fx-font-size: 13px;" +
                "-fx-text-inner-color: " + DARK_TEXT + ";"
        );
        topSearch.textProperty().addListener((obs, oldV, newV) -> {
            if (searchField != null) {
                searchField.setText(newV);
            }
        });
        HBox.setHgrow(topSearch, Priority.ALWAYS);

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
        searchBox.getChildren().addAll(searchIcon, topSearch);

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
                createSearchBar(stage),
                createDepartmentGridSection(stage)
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

        Label title = new Label("Department Management");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 800; -fx-text-fill: " + DARK_TEXT + ";");

        Label subtitle = new Label("Manage hospital departments, departmental appointments, and staff heads");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addDepartment = new Button("＋ Add Department");
        addDepartment.setPrefHeight(42);
        addDepartment.setPadding(new Insets(0, 20, 0, 20));

        String actionBtnStyle =
                "-fx-background-color: " + PRIMARY_BLUE + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;";

        addDepartment.setStyle(actionBtnStyle);
        addDepartment.setOnMouseEntered(e -> addDepartment.setStyle(actionBtnStyle + "-fx-background-color: #1550B0;"));
        addDepartment.setOnMouseExited(e -> addDepartment.setStyle(actionBtnStyle));

        addDepartment.setOnAction(e -> showAddDepartmentDialog(stage));

        header.getChildren().addAll(titleBox, spacer, addDepartment);

        return header;
    }

    // =========================================================
    // STATISTICS
    // =========================================================

    private HBox createStatistics() {

        HBox statistics = new HBox(18);

        VBox totalCard = createStatisticCard("Total Departments", "0", "Active hospital departments", "✚", PRIMARY_BLUE, PRIMARY_LIGHT);
        VBox docsCard = createStatisticCard("Total Doctors", "0", "Across all departments", "♙", PURPLE, PURPLE_LIGHT);
        VBox activeCard = createStatisticCard("Active Departments", "0", "Currently operational", "✓", SUCCESS_GREEN, SUCCESS_LIGHT);
        VBox emergencyCard = createStatisticCard("24/7 Departments", "0", "Emergency services", "◷", WARNING_ORANGE, WARNING_LIGHT);

        totalDeptValLabel = (Label) totalCard.getChildren().get(1);
        totalDocsValLabel = (Label) docsCard.getChildren().get(1);
        activeDeptValLabel = (Label) activeCard.getChildren().get(1);
        emergencyDeptValLabel = (Label) emergencyCard.getChildren().get(1);

        statistics.getChildren().addAll(totalCard, docsCard, activeCard, emergencyCard);

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
    // SEARCH BAR & FILTERS
    // =========================================================

    private HBox createSearchBar(Stage stage) {

        HBox container = new HBox(12);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(16));
        applyCardStyle(container);

        searchField = new TextField();
        searchField.setPromptText("Search department name...");
        searchField.setPrefWidth(320);
        searchField.setPrefHeight(40);
        searchField.setStyle(
                "-fx-background-color: " + LIGHT_BACKGROUND + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 0 14;" +
                "-fx-font-size: 12px;"
        );
        searchField.textProperty().addListener((obs, oldV, newV) -> applyFiltersAndRefreshUI());

        categoryFilter = new ComboBox<>();
        categoryFilter.getItems().addAll(
                "All Departments",
                "Clinical",
                "Surgical",
                "Diagnostic",
                "Emergency",
                "Support"
        );
        categoryFilter.setValue("All Departments");
        categoryFilter.setPrefHeight(40);
        categoryFilter.setStyle("-fx-font-size: 12px;");
        categoryFilter.setOnAction(e -> applyFiltersAndRefreshUI());

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
            categoryFilter.setValue("All Departments");
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
        exportButton.setOnAction(e -> exportDepartmentDataToCSV(stage));

        container.getChildren().addAll(searchField, categoryFilter, spacer, filterButton, exportButton);

        return container;
    }

    // =========================================================
    // DEPARTMENT GRID SECTION
    // =========================================================

    private VBox createDepartmentGridSection(Stage stage) {

        VBox container = new VBox(16);

        HBox heading = new HBox();
        heading.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Hospital Departments");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: " + DARK_TEXT + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        departmentCountHeaderLabel = new Label("0 Departments");
        departmentCountHeaderLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: " + SECONDARY_TEXT + ";");

        heading.getChildren().addAll(title, spacer, departmentCountHeaderLabel);
        container.getChildren().add(heading);

        // Responsive FlowPane Grid
        departmentGridPane = new FlowPane();
        departmentGridPane.setHgap(18);
        departmentGridPane.setVgap(18);

        container.getChildren().add(departmentGridPane);

        return container;
    }

    // =========================================================
    // DYNAMIC FILTER & UI REFRESH
    // =========================================================

    private void applyFiltersAndRefreshUI() {
        String searchText = searchField != null && searchField.getText() != null ? searchField.getText().toLowerCase().trim() : "";
        String catVal = categoryFilter != null && categoryFilter.getValue() != null ? categoryFilter.getValue() : "All Departments";

        filteredDepartmentList.setPredicate(dept -> {
            boolean matchesSearch = searchText.isEmpty() ||
                    dept.getName().toLowerCase().contains(searchText) ||
                    dept.getHead().toLowerCase().contains(searchText);

            boolean matchesCategory = catVal.equals("All Departments") || dept.getCategory().equalsIgnoreCase(catVal);

            return matchesSearch && matchesCategory;
        });

        rebuildDepartmentGrid();
        updateStatistics();
    }

    private void rebuildDepartmentGrid() {
        if (departmentGridPane == null) return;
        departmentGridPane.getChildren().clear();

        if (filteredDepartmentList.isEmpty()) {
            Label emptyLabel = new Label("No departments match the specified filter criteria.");
            emptyLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-padding: 24;");
            departmentGridPane.getChildren().add(emptyLabel);
            return;
        }

        for (Department dept : filteredDepartmentList) {
            VBox card = createDepartmentCard(dept);
            card.setPrefWidth(375); // Fixed card width for flow layout
            departmentGridPane.getChildren().add(card);
        }
    }

    private void updateStatistics() {
        int totalDept = masterDepartmentList.size();
        int totalDocs = 0;
        int activeDept = masterDepartmentList.size(); // All active by default
        int emergencyDept = 0;

        for (Department d : masterDepartmentList) {
            totalDocs += d.getDoctorCount();
            if (d.isIs247() || "Emergency".equalsIgnoreCase(d.getCategory())) {
                emergencyDept++;
            }
        }

        if (totalDeptValLabel != null) totalDeptValLabel.setText(String.valueOf(totalDept));
        if (totalDocsValLabel != null) totalDocsValLabel.setText(String.valueOf(totalDocs));
        if (activeDeptValLabel != null) activeDeptValLabel.setText(String.valueOf(activeDept));
        if (emergencyDeptValLabel != null) emergencyDeptValLabel.setText(String.valueOf(emergencyDept));
        if (departmentCountHeaderLabel != null) departmentCountHeaderLabel.setText(filteredDepartmentList.size() + " Departments");
    }

    // =========================================================
    // DEPARTMENT CARD WITH EMBEDDED APPOINTMENTS
    // =========================================================

    private VBox createDepartmentCard(Department dept) {

        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        applyCardStyle(card);

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 12; -fx-border-color: " + PRIMARY_BLUE + "; -fx-border-radius: 12;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 12; -fx-border-color: " + BORDER + "; -fx-border-radius: 12;"));

        // 1. TOP SECTION
        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);

        Circle iconCircle = new Circle(20);
        iconCircle.setFill(Color.web(dept.getThemeBgColor()));

        Label iconLabel = new Label(dept.getIcon());
        iconLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + dept.getThemeColor() + ";");

        StackPane iconBox = new StackPane(iconCircle, iconLabel);

        VBox nameBox = new VBox(3);

        Label name = new Label(dept.getName());
        name.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: " + DARK_TEXT + ";");

        Label typeLabel = new Label(dept.getCategory());
        typeLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: " + dept.getThemeColor() + "; -fx-font-weight: 800; -fx-background-color: " + dept.getThemeBgColor() + "; -fx-padding: 2 6; -fx-background-radius: 4;");

        nameBox.getChildren().addAll(name, typeLabel);
        HBox.setMargin(nameBox, new Insets(0, 0, 0, 10));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addAptBtn = new Button("＋ Appointment");
        addAptBtn.setStyle("-fx-background-color: " + PRIMARY_LIGHT + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
        addAptBtn.setOnAction(e -> showAddAppointmentDialog(dept));

        top.getChildren().addAll(iconBox, nameBox, spacer, addAptBtn);

        // 2. DEPARTMENT HEAD & BASIC STATS
        HBox infoBox = new HBox(16);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        VBox headBox = new VBox(2);
        Label headTitle = new Label("Head");
        headTitle.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        Label headName = new Label(dept.getHead());
        headName.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: " + DARK_TEXT + ";");
        headBox.getChildren().addAll(headTitle, headName);

        Region infoSpacer = new Region();
        HBox.setHgrow(infoSpacer, Priority.ALWAYS);

        Label docsIconLabel = new Label("♙ " + dept.getDoctorCount() + " Docs");
        docsIconLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + DARK_TEXT + "; -fx-font-weight: 600;");

        Label patsIconLabel = new Label("👤 " + dept.getPatientCount() + " Patients");
        patsIconLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-font-weight: 600;");

        infoBox.getChildren().addAll(headBox, infoSpacer, docsIconLabel, patsIconLabel);

        // 3. APPOINTMENTS SECTION (Inside the card)
        VBox appointmentsSection = createEmbeddedAppointmentsSection(dept);

        // 4. ACTION BUTTONS
        HBox actions = new HBox(8);
        actions.setPadding(new Insets(4, 0, 0, 0));

        Button edit = createSmallButton("Edit Details", PRIMARY_BLUE, PRIMARY_LIGHT);
        Button delete = createSmallButton("Delete", ERROR_RED, ERROR_LIGHT);

        edit.setOnAction(e -> showEditDepartmentDialog(dept));
        delete.setOnAction(e -> handleDeleteDepartment(dept));

        actions.getChildren().addAll(edit, delete);

        card.getChildren().addAll(top, new Separator(), infoBox, new Separator(), appointmentsSection, actions);

        return card;
    }

    private VBox createEmbeddedAppointmentsSection(Department dept) {
        VBox box = new VBox(6);
        box.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 8; -fx-background-radius: 8; -fx-border-color: " + BORDER + "; -fx-border-radius: 8;");

        HBox aptHeader = new HBox();
        aptHeader.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Appointments (" + dept.getAppointments().size() + ")");
        title.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TEXT + ";");

        box.getChildren().add(title);

        if (dept.getAppointments().isEmpty()) {
            Label empty = new Label("No appointments scheduled.");
            empty.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");
            box.getChildren().add(empty);
        } else {
            int displayCount = Math.min(dept.getAppointments().size(), 3);
            for (int i = 0; i < displayCount; i++) {
                Appointment apt = dept.getAppointments().get(i);
                HBox aptRow = new HBox(6);
                aptRow.setAlignment(Pos.CENTER_LEFT);

                Label time = new Label(apt.getTimeSlot());
                time.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

                Label patient = new Label(apt.getPatientName());
                patient.setStyle("-fx-font-size: 10px; -fx-text-fill: " + DARK_TEXT + ";");

                Region sp = new Region();
                HBox.setHgrow(sp, Priority.ALWAYS);

                Label status = new Label(apt.getStatus());
                String statusColor = "Scheduled".equalsIgnoreCase(apt.getStatus()) ? WARNING_ORANGE :
                        ("In-Progress".equalsIgnoreCase(apt.getStatus()) ? PRIMARY_BLUE : SUCCESS_GREEN);
                status.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: " + statusColor + ";");

                aptRow.getChildren().addAll(time, new Label("•"), patient, sp, status);
                box.getChildren().add(aptRow);
            }
        }

        return box;
    }

    private Button createSmallButton(String text, String color, String bgColor) {

        Button button = new Button(text);
        button.setPrefHeight(32);
        button.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(button, Priority.ALWAYS);

        String style =
                "-fx-background-color: " + bgColor + ";" +
                "-fx-text-fill: " + color + ";" +
                "-fx-background-radius: 6;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;";

        button.setStyle(style);

        button.setOnMouseEntered(e -> button.setStyle(style.replace(bgColor, color + "25")));
        button.setOnMouseExited(e -> button.setStyle(style));

        return button;
    }

    // =========================================================
    // MODAL DIALOGS & ACTION IMPLEMENTATIONS
    // =========================================================

    private void showAddDepartmentDialog(Stage owner) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.setTitle("Add New Department");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");

        Label dialogTitle = new Label("Register New Department");
        dialogTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TEXT + ";");

        TextField nameInput = new TextField();
        nameInput.setPromptText("Department Name (e.g. Oncology)");

        TextField headInput = new TextField();
        headInput.setPromptText("Head of Department (e.g. Dr. Jane Doe)");

        ComboBox<String> catInput = new ComboBox<>();
        catInput.getItems().addAll("Clinical", "Surgical", "Diagnostic", "Emergency", "Support");
        catInput.setValue("Clinical");

        CheckBox is247Check = new CheckBox("Operates 24/7 Emergency");

        Button saveBtn = new Button("Add Department");
        saveBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold;");
        saveBtn.setOnAction(e -> {
            if (nameInput.getText().trim().isEmpty() || headInput.getText().trim().isEmpty()) {
                showAlert("Validation Error", "Please fill in all required department fields.");
                return;
            }

            Department newDept = new Department(
                    nameInput.getText().trim(),
                    headInput.getText().trim(),
                    8, 45,
                    catInput.getValue(),
                    PRIMARY_BLUE, PRIMARY_LIGHT, "✚", is247Check.isSelected()
            );

            masterDepartmentList.add(newDept);
            applyFiltersAndRefreshUI();
            dialog.close();
        });

        layout.getChildren().addAll(
                dialogTitle,
                new Label("Department Name:"), nameInput,
                new Label("Department Head:"), headInput,
                new Label("Category:"), catInput,
                is247Check,
                saveBtn
        );

        dialog.setScene(new Scene(layout, 360, 380));
        dialog.showAndWait();
    }

    private void showEditDepartmentDialog(Department dept) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit Department - " + dept.getName());

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));

        TextField nameInput = new TextField(dept.getName());
        TextField headInput = new TextField(dept.getHead());

        ComboBox<String> catInput = new ComboBox<>();
        catInput.getItems().addAll("Clinical", "Surgical", "Diagnostic", "Emergency", "Support");
        catInput.setValue(dept.getCategory());

        TextField docCountInput = new TextField(String.valueOf(dept.getDoctorCount()));
        TextField patCountInput = new TextField(String.valueOf(dept.getPatientCount()));

        Button saveBtn = new Button("Save Changes");
        saveBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold;");
        saveBtn.setOnAction(e -> {
            try {
                dept.setName(nameInput.getText().trim());
                dept.setHead(headInput.getText().trim());
                dept.setCategory(catInput.getValue());
                dept.setDoctorCount(Integer.parseInt(docCountInput.getText().trim()));
                dept.setPatientCount(Integer.parseInt(patCountInput.getText().trim()));

                applyFiltersAndRefreshUI();
                dialog.close();
            } catch (NumberFormatException ex) {
                showAlert("Input Error", "Doctors and Patients count must be numeric.");
            }
        });

        layout.getChildren().addAll(
                new Label("Department Name:"), nameInput,
                new Label("Department Head:"), headInput,
                new Label("Category:"), catInput,
                new Label("Doctor Count:"), docCountInput,
                new Label("Patient Count:"), patCountInput,
                saveBtn
        );

        dialog.setScene(new Scene(layout, 360, 420));
        dialog.showAndWait();
    }

    private void showAddAppointmentDialog(Department dept) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Schedule Appointment - " + dept.getName());

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));

        Label title = new Label("Schedule for " + dept.getName());
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        TextField patientInput = new TextField();
        patientInput.setPromptText("Patient Full Name");

        TextField doctorInput = new TextField();
        doctorInput.setPromptText("Assigned Doctor Name");

        TextField timeInput = new TextField();
        timeInput.setPromptText("Time Slot (e.g. 11:30 AM)");

        ComboBox<String> statusInput = new ComboBox<>();
        statusInput.getItems().addAll("Scheduled", "In-Progress", "Completed");
        statusInput.setValue("Scheduled");

        Button saveBtn = new Button("Confirm Appointment");
        saveBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold;");
        saveBtn.setOnAction(e -> {
            if (patientInput.getText().trim().isEmpty() || timeInput.getText().trim().isEmpty()) {
                showAlert("Validation Error", "Patient Name and Time Slot are required.");
                return;
            }

            String aptId = "APT-" + (100 + dept.getAppointments().size() + 1);
            Appointment newApt = new Appointment(
                    aptId,
                    patientInput.getText().trim(),
                    doctorInput.getText().trim().isEmpty() ? dept.getHead() : doctorInput.getText().trim(),
                    timeInput.getText().trim(),
                    statusInput.getValue()
            );

            dept.getAppointments().add(newApt);
            dept.setPatientCount(dept.getPatientCount() + 1);

            applyFiltersAndRefreshUI();
            dialog.close();
        });

        layout.getChildren().addAll(
                title,
                new Label("Patient Name:"), patientInput,
                new Label("Doctor Name:"), doctorInput,
                new Label("Time Slot:"), timeInput,
                new Label("Status:"), statusInput,
                saveBtn
        );

        dialog.setScene(new Scene(layout, 350, 380));
        dialog.showAndWait();
    }

    private void handleDeleteDepartment(Department dept) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Department Removal");
        alert.setHeaderText("Delete " + dept.getName() + " Department?");
        alert.setContentText("Are you sure you want to remove this department? This will unassign all linked staff and appointments.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            masterDepartmentList.remove(dept);
            applyFiltersAndRefreshUI();
        }
    }

    private void exportDepartmentDataToCSV(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Departments Data");
        fileChooser.setInitialFileName("Hospital_Departments.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("Department Name,Head,Category,Doctor Count,Patient Count,Appointments Count");
                for (Department dept : filteredDepartmentList) {
                    writer.printf("\"%s\",\"%s\",\"%s\",%d,%d,%d%n",
                            dept.getName(),
                            dept.getHead(),
                            dept.getCategory(),
                            dept.getDoctorCount(),
                            dept.getPatientCount(),
                            dept.getAppointments().size()
                    );
                }
                showAlert("Export Successful", "Department records successfully exported to: " + file.getAbsolutePath());
            } catch (Exception ex) {
                showAlert("Export Error", "Could not export data: " + ex.getMessage());
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // =========================================================
    // HELPER STYLING
    // =========================================================

    private void applyCardStyle(Pane pane) {
        pane.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(15, 23, 42, 0.04));
        shadow.setRadius(10);
        shadow.setOffsetY(3);
        pane.setEffect(shadow);
    }
}