package com.healthsphere.view.hospital;

import com.healthsphere.controller.hospital.DepartmentController;
import com.healthsphere.model.HospitalDepartment;
import com.healthsphere.util.SessionManager;
import com.healthsphere.util.Navigation;
import com.healthsphere.util.ShimmerPlaceholder;

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
import java.util.List;
import java.util.Optional;

public class DepartmentManagementView {

    // =========================================================
    // COLOR PALETTE
    // =========================================================

    private static final String PRIMARY_BLUE = "#170eca";
    private static final String PRIMARY_LIGHT = "#EFF5FF";

    private static final String DARK_TEXT = "#0F172A";
    private static final String SECONDARY_TEXT = "#64748B";

    private static final String LIGHT_BACKGROUND = "linear-gradient(to bottom right, #EFF6FF, #F5F3FF, #F8FAFC)";
    private static final String CARD_BG = "#FFFFFF";
    private static final String BORDER = "#E2E8F0";

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
    // CONTROLLER
    // =========================================================

    private final DepartmentController departmentController;

    // =========================================================
    // UI PRESENTATION MODEL
    // =========================================================

    public static class Appointment {

        private String id;
        private String patientName;
        private String doctorName;
        private String timeSlot;
        private String status;

        public Appointment(
                String id,
                String patientName,
                String doctorName,
                String timeSlot,
                String status) {

            this.id = id;
            this.patientName = patientName;
            this.doctorName = doctorName;
            this.timeSlot = timeSlot;
            this.status = status;
        }

        public String getId() {
            return id;
        }

        public String getPatientName() {
            return patientName;
        }

        public String getDoctorName() {
            return doctorName;
        }

        public String getTimeSlot() {
            return timeSlot;
        }

        public String getStatus() {
            return status;
        }
    }

    /*
     * UI-only presentation model.
     *
     * HospitalDepartment is the actual Firestore model.
     * Additional UI information remains here.
     */
    public static class Department {

        private String departmentId;

        private String name;
        private String head;

        private int doctorCount;
        private int patientCount;

        private String category;

        private String themeColor;
        private String themeBgColor;
        private String icon;

        private boolean is247;
        private boolean active;

        private final ObservableList<Appointment> appointments =
                FXCollections.observableArrayList();

        public Department(
                String departmentId,
                String name,
                String head,
                int doctorCount,
                int patientCount,
                String category,
                String themeColor,
                String themeBgColor,
                String icon,
                boolean is247,
                boolean active) {

            this.departmentId = departmentId;
            this.name = name;
            this.head = head;
            this.doctorCount = doctorCount;
            this.patientCount = patientCount;
            this.category = category;
            this.themeColor = themeColor;
            this.themeBgColor = themeBgColor;
            this.icon = icon;
            this.is247 = is247;
            this.active = active;
        }

        public String getDepartmentId() {
            return departmentId;
        }

        public void setDepartmentId(String departmentId) {
            this.departmentId = departmentId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHead() {
            return head;
        }

        public void setHead(String head) {
            this.head = head;
        }

        public int getDoctorCount() {
            return doctorCount;
        }

        public void setDoctorCount(int doctorCount) {
            this.doctorCount = doctorCount;
        }

        public int getPatientCount() {
            return patientCount;
        }

        public void setPatientCount(int patientCount) {
            this.patientCount = patientCount;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getThemeColor() {
            return themeColor;
        }

        public String getThemeBgColor() {
            return themeBgColor;
        }

        public String getIcon() {
            return icon;
        }

        public boolean isIs247() {
            return is247;
        }

        public void setIs247(boolean is247) {
            this.is247 = is247;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public ObservableList<Appointment> getAppointments() {
            return appointments;
        }
    }

    // =========================================================
    // STATE
    // =========================================================

    private final ObservableList<Department> masterDepartmentList =
            FXCollections.observableArrayList();

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
    // CONSTRUCTOR
    // =========================================================

    public DepartmentManagementView() {

        departmentController =
                new DepartmentController();
    }

    // =========================================================
    // CREATE SCENE
    // =========================================================

    public Scene createScene(Stage stage) {

        if (!SessionManager.isLoggedIn()) {

            throw new IllegalStateException(
                    "No active hospital session."
            );
        }

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color: "
                        + LIGHT_BACKGROUND
                        + ";"
        );

        root.setLeft(
                HospitalSidebar.createSidebar(stage, HospitalSidebar.HospitalTab.DEPARTMENTS)
        );

        root.setTop(
                createTopBar()
        );

        ScrollPane scrollPane =
                new ScrollPane(
                        createMainContent(stage)
                );

        scrollPane.setFitToWidth(true);

        scrollPane.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-background: transparent;"
                        + "-fx-padding: 0;"
        );

        root.setCenter(scrollPane);

        loadDepartmentsFromFirestore();

        return new Scene(
                root,
                stage.getWidth(),
                stage.getHeight()
        );
    }

    // =========================================================
    // LOAD REAL FIRESTORE DATA
    // =========================================================

    private void loadDepartmentsFromFirestore() {
        if (departmentGridPane != null) {
            departmentGridPane.getChildren().clear();
            departmentGridPane.getChildren().add(ShimmerPlaceholder.createListShimmer(3));
        }

        javafx.concurrent.Task<List<HospitalDepartment>> loadTask =
                new javafx.concurrent.Task<>() {
                    @Override
                    protected List<HospitalDepartment> call() throws Exception {
                        return departmentController.getAllDepartments();
                    }
                };

        loadTask.setOnSucceeded(event -> {
            masterDepartmentList.clear();
            List<HospitalDepartment> departments = loadTask.getValue();
            if (departments != null) {
                for (HospitalDepartment hospitalDepartment : departments) {
                    if (hospitalDepartment == null || !hospitalDepartment.isActive()) {
                        continue;
                    }
                    masterDepartmentList.add(convertToUIDepartment(hospitalDepartment));
                }
            }
            filteredDepartmentList = new FilteredList<>(masterDepartmentList, p -> true);
            applyFiltersAndRefreshUI();
        });

        loadTask.setOnFailed(event -> {
            masterDepartmentList.clear();
            filteredDepartmentList = new FilteredList<>(masterDepartmentList, p -> true);
            applyFiltersAndRefreshUI();
            showAlert("Department Loading Error", getErrorMessage(loadTask.getException()));
        });

        new Thread(loadTask).start();
    }

    // =========================================================
    // CONVERT FIRESTORE MODEL TO UI MODEL
    // =========================================================

    private Department convertToUIDepartment(
            HospitalDepartment hospitalDepartment) {

        String category =
                hospitalDepartment.getCategory();

        if (category == null
                || category.trim().isEmpty()) {

            category = "Clinical";
        }

        String head =
                hospitalDepartment.getHeadDoctorId();

        if (head == null
                || head.trim().isEmpty()) {

            head = "Not Assigned";

        } else {

            head =
                    "Doctor ID: "
                            + head;
        }

        String themeColor =
                PRIMARY_BLUE;

        String themeBgColor =
                PRIMARY_LIGHT;

        String icon =
                "✚";

        if ("Surgical".equalsIgnoreCase(category)) {

            themeColor =
                    SUCCESS_GREEN;

            themeBgColor =
                    SUCCESS_LIGHT;

            icon =
                    "⌁";

        } else if ("Diagnostic".equalsIgnoreCase(category)) {

            themeColor =
                    PURPLE;

            themeBgColor =
                    PURPLE_LIGHT;

            icon =
                    "◉";

        } else if ("Emergency".equalsIgnoreCase(category)) {

            themeColor =
                    ERROR_RED;

            themeBgColor =
                    ERROR_LIGHT;

            icon =
                    "!";

        } else if ("Support".equalsIgnoreCase(category)) {

            themeColor =
                    WARNING_ORANGE;

            themeBgColor =
                    WARNING_LIGHT;

            icon =
                    "♧";
        }

        return new Department(
                hospitalDepartment.getDepartmentId(),
                hospitalDepartment.getName(),
                head,
                0,
                0,
                category,
                themeColor,
                themeBgColor,
                icon,
                hospitalDepartment.is24x7(),
                hospitalDepartment.isActive()
        );
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private VBox createSidebar(Stage stage) {
        return HospitalSidebar.createSidebar(stage, HospitalSidebar.HospitalTab.DEPARTMENTS);
    }

    // =========================================================
    // NAVIGATION BUTTON
    // =========================================================

    private Button createNavigationButton(
            String icon,
            String text,
            boolean selected) {

        Button button =
                new Button(
                        icon + "    " + text
                );

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setAlignment(
                Pos.CENTER_LEFT
        );

        button.setPadding(
                new Insets(
                        12,
                        14,
                        12,
                        14
                )
        );

        String normalStyle;

        if (selected) {

            normalStyle =
                    "-fx-background-color: "
                            + PRIMARY_BLUE
                            + ";"
                            + "-fx-text-fill: white;"
                            + "-fx-background-radius: 8;"
                            + "-fx-font-size: 12px;"
                            + "-fx-font-weight: bold;"
                            + "-fx-cursor: hand;";

        } else {

            normalStyle =
                    "-fx-background-color: transparent;"
                            + "-fx-text-fill: "
                            + SIDEBAR_TEXT_MUTED
                            + ";"
                            + "-fx-background-radius: 8;"
                            + "-fx-font-size: 12px;"
                            + "-fx-font-weight: 600;"
                            + "-fx-cursor: hand;";
        }

        button.setStyle(
                normalStyle
        );

        if (!selected) {

            button.setOnMouseEntered(
                    e ->
                            button.setStyle(
                                    "-fx-background-color: "
                                            + SIDEBAR_HOVER
                                            + ";"
                                            + "-fx-text-fill: white;"
                                            + "-fx-background-radius: 8;"
                                            + "-fx-font-size: 12px;"
                                            + "-fx-font-weight: 600;"
                                            + "-fx-cursor: hand;"
                            )
            );

            button.setOnMouseExited(
                    e ->
                            button.setStyle(
                                    normalStyle
                            )
            );
        }

        return button;
    }

    // =========================================================
    // TOP BAR
    // =========================================================

    private HBox createTopBar() {

        HBox topBar =
                new HBox(16);

        topBar.setAlignment(
                Pos.CENTER_LEFT
        );

        topBar.setPadding(
                new Insets(
                        14,
                        24,
                        14,
                        24
                )
        );

        topBar.setStyle(
                "-fx-background-color: "
                        + CARD_BG
                        + ";"
                        + "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-width: 0 0 1 0;"
        );

        Label searchIcon =
                new Label("⌕");

        searchIcon.setStyle(
                "-fx-font-size: 18px;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        TextField topSearchField =
                new TextField();

        topSearchField.setPromptText(
                "Search departments..."
        );

        topSearchField.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-border-width: 0;"
                        + "-fx-font-size: 13px;"
                        + "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
                        + "-fx-prompt-text-fill: "
                        + SIDEBAR_TEXT_MUTED
                        + ";"
        );

        HBox.setHgrow(
                topSearchField,
                Priority.ALWAYS
        );

        topSearchField.textProperty()
                .addListener(
                        (obs, oldValue, newValue) -> {

                            if (searchField != null) {

                                searchField.setText(
                                        newValue
                                );
                            }
                        }
                );

        HBox searchBox =
                new HBox(8);

        searchBox.setAlignment(
                Pos.CENTER_LEFT
        );

        searchBox.setPrefWidth(
                360
        );

        searchBox.setPrefHeight(
                40
        );

        searchBox.setPadding(
                new Insets(
                        0,
                        12,
                        0,
                        12
                )
        );

        searchBox.setStyle(
                "-fx-background-color: "
                        + LIGHT_BACKGROUND
                        + ";"
                        + "-fx-background-radius: 8;"
                        + "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-radius: 8;"
        );

        searchBox.getChildren().addAll(
                searchIcon,
                topSearchField
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label notification =
                new Label("🔔");

        notification.setStyle(
                "-fx-font-size: 16px;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        Label settings =
                new Label("⚙");

        settings.setStyle(
                "-fx-font-size: 18px;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        Label administrator =
                new Label(
                        "Hospital Administrator"
                );

        administrator.setStyle(
                "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Label role =
                new Label(
                        "HOSPITAL ADMIN"
                );

        role.setStyle(
                "-fx-font-size: 9px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        VBox userInfo =
                new VBox(2);

        userInfo.setAlignment(
                Pos.CENTER_RIGHT
        );

        userInfo.getChildren().addAll(
                administrator,
                role
        );

        Circle avatar =
                new Circle(18);

        avatar.setFill(
                Color.web(
                        PRIMARY_LIGHT
                )
        );

        avatar.setStroke(
                Color.web(
                        BORDER
                )
        );

        Label avatarText =
                new Label("HA");

        avatarText.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + PRIMARY_BLUE
                        + ";"
        );

        StackPane avatarBox =
                new StackPane(
                        avatar,
                        avatarText
                );

        topBar.getChildren().addAll(
                searchBox,
                spacer,
                notification,
                settings,
                userInfo,
                avatarBox
        );

        return topBar;
    }

    // =========================================================
    // MAIN CONTENT
    // =========================================================

    private VBox createMainContent(
            Stage stage) {

        VBox content =
                new VBox(24);

        content.setPadding(
                new Insets(28)
        );

        content.setStyle(
                "-fx-background-color: "
                        + LIGHT_BACKGROUND
                        + ";"
        );

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

    private HBox createPageHeader(
            Stage stage) {

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox titleBox =
                new VBox(4);

        Label title =
                new Label(
                        "Department Management"
                );

        title.setStyle(
                "-fx-font-size: 26px;"
                        + "-fx-font-weight: 800;"
                        + "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Label subtitle =
                new Label(
                        "Manage hospital departments, departmental appointments, and staff heads"
                );

        subtitle.setStyle(
                "-fx-font-size: 13px;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
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

        Button addDepartment =
                new Button(
                        "＋ Add Department"
                );

        addDepartment.setPrefHeight(
                42
        );

        addDepartment.setPadding(
                new Insets(
                        0,
                        20,
                        0,
                        20
                )
        );

        String actionBtnStyle =
                "-fx-background-color: "
                        + PRIMARY_BLUE
                        + ";"
                        + "-fx-text-fill: white;"
                        + "-fx-background-radius: 8;"
                        + "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-cursor: hand;";

        addDepartment.setStyle(
                actionBtnStyle
        );

        addDepartment.setOnMouseEntered(
                e ->
                        addDepartment.setStyle(
                                actionBtnStyle
                                        + "-fx-background-color: #1550B0;"
                        )
        );

        addDepartment.setOnMouseExited(
                e ->
                        addDepartment.setStyle(
                                actionBtnStyle
                        )
        );

        addDepartment.setOnAction(
                e ->
                        showAddDepartmentDialog(
                                stage
                        )
        );

        header.getChildren().addAll(
                titleBox,
                spacer,
                addDepartment
        );

        return header;
    }

    // =========================================================
    // STATISTICS
    // =========================================================

    private HBox createStatistics() {

        HBox statistics =
                new HBox(18);

        VBox totalCard =
                createStatisticCard(
                        "Total Departments",
                        "0",
                        "Active hospital departments",
                        "✚",
                        PRIMARY_BLUE,
                        PRIMARY_LIGHT
                );

        VBox docsCard =
                createStatisticCard(
                        "Total Doctors",
                        "0",
                        "Across all departments",
                        "♙",
                        PURPLE,
                        PURPLE_LIGHT
                );

        VBox activeCard =
                createStatisticCard(
                        "Active Departments",
                        "0",
                        "Currently operational",
                        "✓",
                        SUCCESS_GREEN,
                        SUCCESS_LIGHT
                );

        VBox emergencyCard =
                createStatisticCard(
                        "24/7 Departments",
                        "0",
                        "Emergency services",
                        "◷",
                        WARNING_ORANGE,
                        WARNING_LIGHT
                );

        totalDeptValLabel =
                (Label) totalCard
                        .getChildren()
                        .get(1);

        totalDocsValLabel =
                (Label) docsCard
                        .getChildren()
                        .get(1);

        activeDeptValLabel =
                (Label) activeCard
                        .getChildren()
                        .get(1);

        emergencyDeptValLabel =
                (Label) emergencyCard
                        .getChildren()
                        .get(1);

        statistics.getChildren().addAll(
                totalCard,
                docsCard,
                activeCard,
                emergencyCard
        );

        for (javafx.scene.Node node :
                statistics.getChildren()) {

            HBox.setHgrow(
                    node,
                    Priority.ALWAYS
            );
        }

        return statistics;
    }

    // =========================================================
    // STATISTIC CARD
    // =========================================================

    private VBox createStatisticCard(
            String title,
            String value,
            String subtitle,
            String icon,
            String color,
            String bgColor) {

        VBox card =
                new VBox(10);

        card.setPadding(
                new Insets(20)
        );

        card.setPrefHeight(
                120
        );

        applyCardStyle(card);

        HBox top =
                new HBox();

        top.setAlignment(
                Pos.CENTER_LEFT
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 12px;"
                        + "-fx-font-weight: 600;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label iconLabel =
                new Label(icon);

        iconLabel.setPrefSize(
                36,
                36
        );

        iconLabel.setAlignment(
                Pos.CENTER
        );

        iconLabel.setStyle(
                "-fx-background-color: "
                        + bgColor
                        + ";"
                        + "-fx-background-radius: 8;"
                        + "-fx-text-fill: "
                        + color
                        + ";"
                        + "-fx-font-size: 16px;"
                        + "-fx-font-weight: bold;"
        );

        top.getChildren().addAll(
                titleLabel,
                spacer,
                iconLabel
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-font-size: 26px;"
                        + "-fx-font-weight: 800;"
                        + "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Label subtitleLabel =
                new Label(subtitle);

        subtitleLabel.setStyle(
                "-fx-font-size: 10px;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        card.getChildren().addAll(
                top,
                valueLabel,
                subtitleLabel
        );

        return card;
    }

    // =========================================================
    // SEARCH BAR
    // =========================================================

    private HBox createSearchBar(
            Stage stage) {

        HBox container =
                new HBox(12);

        container.setAlignment(
                Pos.CENTER_LEFT
        );

        searchField =
                new TextField();

        searchField.setPromptText(
                "Search department or head doctor..."
        );

        searchField.setPrefHeight(
                40
        );

        searchField.setPrefWidth(
                300
        );

        searchField.setStyle(
                "-fx-background-color: "
                        + CARD_BG
                        + ";"
                        + "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-radius: 8;"
                        + "-fx-background-radius: 8;"
                        + "-fx-font-size: 12px;"
        );

        searchField.textProperty()
                .addListener(
                        (obs, oldValue, newValue) ->
                                applyFiltersAndRefreshUI()
                );

        categoryFilter =
                new ComboBox<>();

        categoryFilter.getItems().addAll(
                "All Departments",
                "Clinical",
                "Surgical",
                "Diagnostic",
                "Emergency",
                "Support"
        );

        categoryFilter.setValue(
                "All Departments"
        );

        categoryFilter.setPrefHeight(
                40
        );

        categoryFilter.setPrefWidth(
                180
        );

        categoryFilter.valueProperty()
                .addListener(
                        (obs, oldValue, newValue) ->
                                applyFiltersAndRefreshUI()
                );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button filterButton =
                new Button(
                        "☷  Reset Filters"
                );

        filterButton.setPrefHeight(
                40
        );

        filterButton.setStyle(
                "-fx-background-color: "
                        + CARD_BG
                        + ";"
                        + "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-radius: 8;"
                        + "-fx-background-radius: 8;"
                        + "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
                        + "-fx-font-size: 12px;"
                        + "-fx-font-weight: 600;"
                        + "-fx-cursor: hand;"
        );

        filterButton.setOnAction(
                e -> {

                    searchField.clear();

                    categoryFilter.setValue(
                            "All Departments"
                    );

                    applyFiltersAndRefreshUI();
                }
        );

        Button exportButton =
                new Button(
                        "↓  Export"
                );

        exportButton.setPrefHeight(
                40
        );

        exportButton.setPadding(
                new Insets(
                        0,
                        16,
                        0,
                        16
                )
        );

        exportButton.setStyle(
                "-fx-background-color: "
                        + PRIMARY_LIGHT
                        + ";"
                        + "-fx-text-fill: "
                        + PRIMARY_BLUE
                        + ";"
                        + "-fx-background-radius: 8;"
                        + "-fx-font-size: 12px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-cursor: hand;"
        );

        exportButton.setOnAction(
                e ->
                        exportDepartmentDataToCSV(
                                stage
                        )
        );

        container.getChildren().addAll(
                searchField,
                categoryFilter,
                spacer,
                filterButton,
                exportButton
        );

        return container;
    }

    // =========================================================
    // GRID SECTION
    // =========================================================

    private VBox createDepartmentGridSection(
            Stage stage) {

        VBox container =
                new VBox(16);

        HBox heading =
                new HBox();

        heading.setAlignment(
                Pos.CENTER_LEFT
        );

        Label title =
                new Label(
                        "Hospital Departments"
                );

        title.setStyle(
                "-fx-font-size: 16px;"
                        + "-fx-font-weight: 800;"
                        + "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        departmentCountHeaderLabel =
                new Label(
                        "0 Departments"
                );

        departmentCountHeaderLabel.setStyle(
                "-fx-font-size: 12px;"
                        + "-fx-font-weight: 600;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        heading.getChildren().addAll(
                title,
                spacer,
                departmentCountHeaderLabel
        );

        container.getChildren().add(
                heading
        );

        departmentGridPane =
                new FlowPane();

        departmentGridPane.setHgap(
                18
        );

        departmentGridPane.setVgap(
                18
        );

        container.getChildren().add(
                departmentGridPane
        );

        return container;
    }

    // =========================================================
    // FILTER
    // =========================================================

    private void applyFiltersAndRefreshUI() {

        if (filteredDepartmentList == null) {

            filteredDepartmentList =
                    new FilteredList<>(
                            masterDepartmentList,
                            p -> true
                    );
        }

        String searchText =
                searchField != null
                        && searchField.getText() != null
                        ? searchField
                                .getText()
                                .toLowerCase()
                                .trim()
                        : "";

        String categoryValue =
                categoryFilter != null
                        && categoryFilter.getValue() != null
                        ? categoryFilter.getValue()
                        : "All Departments";

        filteredDepartmentList.setPredicate(
                department -> {

                    String name =
                            department.getName() == null
                                    ? ""
                                    : department
                                            .getName()
                                            .toLowerCase();

                    String head =
                            department.getHead() == null
                                    ? ""
                                    : department
                                            .getHead()
                                            .toLowerCase();

                    boolean matchesSearch =
                            searchText.isEmpty()
                                    || name.contains(
                                            searchText
                                    )
                                    || head.contains(
                                            searchText
                                    );

                    boolean matchesCategory =
                            "All Departments"
                                    .equals(
                                            categoryValue
                                    )
                                    || department
                                            .getCategory()
                                            .equalsIgnoreCase(
                                                    categoryValue
                                            );

                    return matchesSearch
                            && matchesCategory;
                }
        );

        rebuildDepartmentGrid();

        updateStatistics();
    }

    // =========================================================
    // REBUILD GRID
    // =========================================================

    private void rebuildDepartmentGrid() {

        if (departmentGridPane == null) {
            return;
        }

        departmentGridPane.getChildren().clear();

        if (filteredDepartmentList.isEmpty()) {

            Label emptyLabel =
                    new Label(
                            "No departments match the specified filter criteria."
                    );

            emptyLabel.setStyle(
                    "-fx-font-size: 13px;"
                            + "-fx-text-fill: "
                            + SECONDARY_TEXT
                            + ";"
                            + "-fx-padding: 24;"
            );

            departmentGridPane.getChildren().add(
                    emptyLabel
            );

            return;
        }

        for (Department department :
                filteredDepartmentList) {

            VBox card =
                    createDepartmentCard(
                            department
                    );

            card.setPrefWidth(
                    375
            );

            departmentGridPane
                    .getChildren()
                    .add(card);
        }
    }

    // =========================================================
    // STATISTICS UPDATE
    // =========================================================

    private void updateStatistics() {

        int totalDepartments =
                masterDepartmentList.size();

        int totalDoctors = 0;

        int activeDepartments =
                masterDepartmentList.size();

        int emergencyDepartments = 0;

        for (Department department :
                masterDepartmentList) {

            /*
             * Doctor count will become dynamic when department
             * ↔ doctor statistics are integrated.
             */
            totalDoctors +=
                    department.getDoctorCount();

            if (department.isIs247()
                    || "Emergency"
                    .equalsIgnoreCase(
                            department.getCategory()
                    )) {

                emergencyDepartments++;
            }
        }

        if (totalDeptValLabel != null) {

            totalDeptValLabel.setText(
                    String.valueOf(
                            totalDepartments
                    )
            );
        }

        if (totalDocsValLabel != null) {

            totalDocsValLabel.setText(
                    String.valueOf(
                            totalDoctors
                    )
            );
        }

        if (activeDeptValLabel != null) {

            activeDeptValLabel.setText(
                    String.valueOf(
                            activeDepartments
                    )
            );
        }

        if (emergencyDeptValLabel != null) {

            emergencyDeptValLabel.setText(
                    String.valueOf(
                            emergencyDepartments
                    )
            );
        }

        if (departmentCountHeaderLabel != null) {

            departmentCountHeaderLabel.setText(
                    filteredDepartmentList.size()
                            + " Departments"
            );
        }
    }

    // =========================================================
    // DEPARTMENT CARD
    // =========================================================

    private VBox createDepartmentCard(
            Department dept) {

        VBox card =
                new VBox(12);

        card.setPadding(
                new Insets(18)
        );

        applyCardStyle(card);

        // -----------------------------------------------------
        // TOP SECTION
        // -----------------------------------------------------

        HBox top =
                new HBox();

        top.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle iconCircle =
                new Circle(20);

        iconCircle.setFill(
                Color.web(
                        dept.getThemeBgColor()
                )
        );

        Label iconLabel =
                new Label(
                        dept.getIcon()
                );

        iconLabel.setStyle(
                "-fx-font-size: 16px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + dept.getThemeColor()
                        + ";"
        );

        StackPane iconBox =
                new StackPane(
                        iconCircle,
                        iconLabel
                );

        VBox nameBox =
                new VBox(3);

        Label name =
                new Label(
                        dept.getName()
                );

        name.setStyle(
                "-fx-font-size: 15px;"
                        + "-fx-font-weight: 800;"
                        + "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Label typeLabel =
                new Label(
                        dept.getCategory()
                );

        typeLabel.setStyle(
                "-fx-font-size: 9px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + dept.getThemeColor()
                        + ";"
        );

        nameBox.getChildren().addAll(
                name,
                typeLabel
        );

        HBox.setMargin(
                nameBox,
                new Insets(
                        0,
                        0,
                        0,
                        10
                )
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button addAppointment =
                new Button(
                        "+ Appointment"
                );

        addAppointment.setStyle(
                "-fx-background-color: "
                        + PRIMARY_LIGHT
                        + ";"
                        + "-fx-text-fill: "
                        + PRIMARY_BLUE
                        + ";"
                        + "-fx-background-radius: 6;"
                        + "-fx-font-size: 10px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-cursor: hand;"
        );

        addAppointment.setOnAction(
                e ->
                        showAddAppointmentDialog(
                                dept
                        )
        );

        top.getChildren().addAll(
                iconBox,
                nameBox,
                spacer,
                addAppointment
        );

        // -----------------------------------------------------
        // HEAD / STATS
        // -----------------------------------------------------

        HBox infoBox =
                new HBox(16);

        infoBox.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox headBox =
                new VBox(2);

        Label headTitle =
                new Label("Head");

        headTitle.setStyle(
                "-fx-font-size: 10px;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        Label headName =
                new Label(
                        dept.getHead()
                );

        headName.setStyle(
                "-fx-font-size: 12px;"
                        + "-fx-font-weight: 700;"
                        + "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        headBox.getChildren().addAll(
                headTitle,
                headName
        );

        Region infoSpacer =
                new Region();

        HBox.setHgrow(
                infoSpacer,
                Priority.ALWAYS
        );

        Label docsIconLabel =
                new Label(
                        "♙ "
                                + dept.getDoctorCount()
                                + " Docs"
                );

        docsIconLabel.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
                        + "-fx-font-weight: 600;"
        );

        Label patientsIconLabel =
                new Label(
                        "👤 "
                                + dept.getPatientCount()
                                + " Patients"
                );

        patientsIconLabel.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
                        + "-fx-font-weight: 600;"
        );

        infoBox.getChildren().addAll(
                headBox,
                infoSpacer,
                docsIconLabel,
                patientsIconLabel
        );

        // -----------------------------------------------------
        // APPOINTMENTS
        // -----------------------------------------------------

        VBox appointmentsSection =
                createEmbeddedAppointmentsSection(
                        dept
                );

        // -----------------------------------------------------
        // ACTIONS
        // -----------------------------------------------------

        HBox actions =
                new HBox(8);

        actions.setPadding(
                new Insets(
                        4,
                        0,
                        0,
                        0
                )
        );

        Button edit =
                createSmallButton(
                        "Edit Details",
                        PRIMARY_BLUE,
                        PRIMARY_LIGHT
                );

        Button delete =
                createSmallButton(
                        "Delete",
                        ERROR_RED,
                        ERROR_LIGHT
                );

        edit.setOnAction(
                e ->
                        showEditDepartmentDialog(
                                dept
                        )
        );

        delete.setOnAction(
                e ->
                        handleDeleteDepartment(
                                dept
                        )
        );

        actions.getChildren().addAll(
                edit,
                delete
        );

        card.getChildren().addAll(
                top,
                new Separator(),
                infoBox,
                new Separator(),
                appointmentsSection,
                actions
        );

        return card;
    }

    // =========================================================
    // EMBEDDED APPOINTMENTS
    // =========================================================

    private VBox createEmbeddedAppointmentsSection(
            Department dept) {

        VBox box =
                new VBox(6);

        box.setStyle(
                "-fx-background-color: #F8FAFC;"
                        + "-fx-padding: 8;"
                        + "-fx-background-radius: 8;"
        );

        Label title =
                new Label(
                        "Recent Appointments"
                );

        title.setStyle(
                "-fx-font-size: 10px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        box.getChildren().add(
                title
        );

        if (dept.getAppointments().isEmpty()) {

            Label empty =
                    new Label(
                            "No appointments scheduled."
                    );

            empty.setStyle(
                    "-fx-font-size: 10px;"
                            + "-fx-text-fill: "
                            + SECONDARY_TEXT
                            + ";"
            );

            box.getChildren().add(
                    empty
            );

            return box;
        }

        for (Appointment appointment :
                dept.getAppointments()) {

            HBox appointmentRow =
                    new HBox(6);

            appointmentRow.setAlignment(
                    Pos.CENTER_LEFT
            );

            Label time =
                    new Label(
                            appointment.getTimeSlot()
                    );

            time.setStyle(
                    "-fx-font-size: 9px;"
                            + "-fx-font-weight: bold;"
                            + "-fx-text-fill: "
                            + PRIMARY_BLUE
                            + ";"
            );

            Label patient =
                    new Label(
                            appointment.getPatientName()
                    );

            patient.setStyle(
                    "-fx-font-size: 10px;"
                            + "-fx-text-fill: "
                            + DARK_TEXT
                            + ";"
            );

            Region spacer =
                    new Region();

            HBox.setHgrow(
                    spacer,
                    Priority.ALWAYS
            );

            String statusValue =
                    appointment.getStatus();

            String statusColor =
                    "Scheduled".equalsIgnoreCase(
                            statusValue
                    )
                            ? WARNING_ORANGE
                            : "In-Progress"
                            .equalsIgnoreCase(
                                    statusValue
                            )
                            ? PRIMARY_BLUE
                            : SUCCESS_GREEN;

            Label status =
                    new Label(
                            statusValue
                    );

            status.setStyle(
                    "-fx-font-size: 9px;"
                            + "-fx-font-weight: bold;"
                            + "-fx-text-fill: "
                            + statusColor
                            + ";"
            );

            appointmentRow.getChildren().addAll(
                    time,
                    new Label("•"),
                    patient,
                    spacer,
                    status
            );

            box.getChildren().add(
                    appointmentRow
            );
        }

        return box;
    }

    // =========================================================
    // SMALL BUTTON
    // =========================================================

    private Button createSmallButton(
            String text,
            String color,
            String bgColor) {

        Button button =
                new Button(text);

        button.setPrefHeight(
                32
        );

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox.setHgrow(
                button,
                Priority.ALWAYS
        );

        String style =
                "-fx-background-color: "
                        + bgColor
                        + ";"
                        + "-fx-text-fill: "
                        + color
                        + ";"
                        + "-fx-background-radius: 6;"
                        + "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-cursor: hand;";

        button.setStyle(
                style
        );

        button.setOnMouseEntered(
                e ->
                        button.setStyle(
                                style.replace(
                                        bgColor,
                                        color + "25"
                                )
                        )
        );

        button.setOnMouseExited(
                e ->
                        button.setStyle(
                                style
                        )
        );

        return button;
    }

    // =========================================================
    // ADD DEPARTMENT
    // =========================================================

    private void showAddDepartmentDialog(
            Stage owner) {

        Stage dialog =
                new Stage();

        dialog.initModality(
                Modality.APPLICATION_MODAL
        );

        dialog.initOwner(
                owner
        );

        dialog.setTitle(
                "Add New Department"
        );

        VBox layout =
                new VBox(12);

        layout.setPadding(
                new Insets(20)
        );

        layout.setStyle(
                "-fx-background-color: white;"
        );

        Label dialogTitle =
                new Label(
                        "Register New Department"
                );

        dialogTitle.setStyle(
                "-fx-font-size: 16px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        TextField nameInput =
                new TextField();

        nameInput.setPromptText(
                "Department Name (e.g. Oncology)"
        );

        TextField headInput =
                new TextField();

        headInput.setPromptText(
                "Head Doctor UID (optional)"
        );

        ComboBox<String> catInput =
                new ComboBox<>();

        catInput.getItems().addAll(
                "Clinical",
                "Surgical",
                "Diagnostic",
                "Emergency",
                "Support"
        );

        catInput.setValue(
                "Clinical"
        );

        catInput.setMaxWidth(
                Double.MAX_VALUE
        );

        CheckBox is247Check =
                new CheckBox(
                        "Operates 24/7"
                );

        Button saveButton =
                new Button(
                        "Add Department"
                );

        saveButton.setMaxWidth(
                Double.MAX_VALUE
        );

        saveButton.setPrefHeight(
                36
        );

        saveButton.setStyle(
                "-fx-background-color: "
                        + PRIMARY_BLUE
                        + ";"
                        + "-fx-text-fill: white;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 6;"
                        + "-fx-cursor: hand;"
        );

        saveButton.setOnAction(
                event -> {

                    try {

                        String name =
                                nameInput
                                        .getText()
                                        .trim();

                        String headDoctorId =
                                headInput
                                        .getText()
                                        .trim();

                        String category =
                                catInput
                                        .getValue();

                        if (name.isEmpty()) {

                            showAlert(
                                    "Validation Error",
                                    "Department name is required."
                            );

                            return;
                        }

                        departmentController.addDepartment(
                                name,
                                headDoctorId,
                                category,
                                is247Check.isSelected()
                        );

                        reloadDepartments();

                        showAlert(
                                "Success",
                                "Department added successfully."
                        );

                        dialog.close();

                    } catch (Exception e) {

                        showAlert(
                                "Unable to Add Department",
                                getErrorMessage(e)
                        );
                    }
                }
        );

        layout.getChildren().addAll(
                dialogTitle,

                new Label(
                        "Department Name:"
                ),

                nameInput,

                new Label(
                        "Head Doctor UID:"
                ),

                headInput,

                new Label(
                        "Category:"
                ),

                catInput,

                is247Check,

                saveButton
        );

        dialog.setScene(
                new Scene(
                        layout,
                        380,
                        400
                )
        );

        dialog.showAndWait();
    }

    // =========================================================
    // EDIT DEPARTMENT
    // =========================================================

    private void showEditDepartmentDialog(
            Department dept) {

        Stage dialog =
                new Stage();

        dialog.initModality(
                Modality.APPLICATION_MODAL
        );

        dialog.setTitle(
                "Edit Department - "
                        + dept.getName()
        );

        VBox layout =
                new VBox(12);

        layout.setPadding(
                new Insets(20)
        );

        layout.setStyle(
                "-fx-background-color: white;"
        );

        Label title =
                new Label(
                        "Edit Department"
                );

        title.setStyle(
                "-fx-font-size: 16px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Label idLabel =
                new Label(
                        "Department ID: "
                                + dept.getDepartmentId()
                );

        idLabel.setStyle(
                "-fx-font-size: 10px;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        TextField nameInput =
                new TextField(
                        dept.getName()
                );

        TextField headInput =
                new TextField();

        String currentHead =
                dept.getHead();

        if (currentHead != null
                && currentHead.startsWith(
                        "Doctor ID: "
                )) {

            currentHead =
                    currentHead.substring(
                            "Doctor ID: ".length()
                    );
        }

        if ("Not Assigned"
                .equalsIgnoreCase(
                        currentHead
                )) {

            currentHead = "";
        }

        headInput.setText(
                currentHead
        );

        ComboBox<String> categoryInput =
                new ComboBox<>();

        categoryInput.getItems().addAll(
                "Clinical",
                "Surgical",
                "Diagnostic",
                "Emergency",
                "Support"
        );

        categoryInput.setValue(
                dept.getCategory()
        );

        categoryInput.setMaxWidth(
                Double.MAX_VALUE
        );

        CheckBox is247Check =
                new CheckBox(
                        "Operates 24/7"
                );

        is247Check.setSelected(
                dept.isIs247()
        );

        Button saveButton =
                new Button(
                        "Save Changes"
                );

        saveButton.setMaxWidth(
                Double.MAX_VALUE
        );

        saveButton.setPrefHeight(
                36
        );

        saveButton.setStyle(
                "-fx-background-color: "
                        + PRIMARY_BLUE
                        + ";"
                        + "-fx-text-fill: white;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 6;"
                        + "-fx-cursor: hand;"
        );

        saveButton.setOnAction(
                event -> {

                    try {

                        String name =
                                nameInput
                                        .getText()
                                        .trim();

                        String headDoctorId =
                                headInput
                                        .getText()
                                        .trim();

                        String category =
                                categoryInput
                                        .getValue();

                        departmentController.updateDepartment(
                                dept.getDepartmentId(),
                                name,
                                headDoctorId,
                                category,
                                is247Check.isSelected()
                        );

                        reloadDepartments();

                        showAlert(
                                "Success",
                                "Department updated successfully."
                        );

                        dialog.close();

                    } catch (Exception e) {

                        showAlert(
                                "Unable to Update Department",
                                getErrorMessage(e)
                        );
                    }
                }
        );

        layout.getChildren().addAll(
                title,
                idLabel,

                new Label(
                        "Department Name:"
                ),

                nameInput,

                new Label(
                        "Head Doctor UID:"
                ),

                headInput,

                new Label(
                        "Category:"
                ),

                categoryInput,

                is247Check,

                saveButton
        );

        dialog.setScene(
                new Scene(
                        layout,
                        380,
                        430
                )
        );

        dialog.showAndWait();
    }

    // =========================================================
    // ADD APPOINTMENT
    // =========================================================

    private void showAddAppointmentDialog(
            Department dept) {

        Stage dialog =
                new Stage();

        dialog.initModality(
                Modality.APPLICATION_MODAL
        );

        dialog.setTitle(
                "Schedule Appointment - "
                        + dept.getName()
        );

        VBox layout =
                new VBox(12);

        layout.setPadding(
                new Insets(20)
        );

        Label title =
                new Label(
                        "Schedule for "
                                + dept.getName()
                );

        title.setStyle(
                "-fx-font-size: 15px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + PRIMARY_BLUE
                        + ";"
        );

        TextField patientInput =
                new TextField();

        patientInput.setPromptText(
                "Patient Full Name"
        );

        TextField doctorInput =
                new TextField();

        doctorInput.setPromptText(
                "Assigned Doctor Name"
        );

        TextField timeInput =
                new TextField();

        timeInput.setPromptText(
                "Time Slot (e.g. 11:30 AM)"
        );

        ComboBox<String> statusInput =
                new ComboBox<>();

        statusInput.getItems().addAll(
                "Scheduled",
                "In-Progress",
                "Completed"
        );

        statusInput.setValue(
                "Scheduled"
        );

        Button saveButton =
                new Button(
                        "Confirm Appointment"
                );

        saveButton.setMaxWidth(
                Double.MAX_VALUE
        );

        saveButton.setStyle(
                "-fx-background-color: "
                        + PRIMARY_BLUE
                        + ";"
                        + "-fx-text-fill: white;"
                        + "-fx-font-weight: bold;"
        );

        saveButton.setOnAction(
                event -> {

                    if (patientInput
                            .getText()
                            .trim()
                            .isEmpty()
                            || timeInput
                            .getText()
                            .trim()
                            .isEmpty()) {

                        showAlert(
                                "Validation Error",
                                "Patient Name and Time Slot are required."
                        );

                        return;
                    }

                    String doctorName =
                            doctorInput
                                    .getText()
                                    .trim();

                    if (doctorName.isEmpty()) {

                        doctorName =
                                dept.getHead();
                    }

                    String appointmentId =
                            "APT-"
                                    + String.format(
                                    "%03d",
                                    dept.getAppointments()
                                            .size()
                                            + 1
                            );

                    Appointment appointment =
                            new Appointment(
                                    appointmentId,
                                    patientInput
                                            .getText()
                                            .trim(),
                                    doctorName,
                                    timeInput
                                            .getText()
                                            .trim(),
                                    statusInput
                                            .getValue()
                            );

                    dept.getAppointments()
                            .add(
                                    appointment
                            );

                    /*
                     * Appointment backend is separate.
                     * This remains UI-only until AppointmentController
                     * is integrated.
                     */
                    dept.setPatientCount(
                            dept.getPatientCount()
                                    + 1
                    );

                    applyFiltersAndRefreshUI();

                    dialog.close();
                }
        );

        layout.getChildren().addAll(
                title,

                new Label(
                        "Patient Name:"
                ),

                patientInput,

                new Label(
                        "Doctor Name:"
                ),

                doctorInput,

                new Label(
                        "Time Slot:"
                ),

                timeInput,

                new Label(
                        "Status:"
                ),

                statusInput,

                saveButton
        );

        dialog.setScene(
                new Scene(
                        layout,
                        350,
                        380
                )
        );

        dialog.showAndWait();
    }

    // =========================================================
    // DELETE / DEACTIVATE
    // =========================================================

    private void handleDeleteDepartment(
            Department dept) {

        Alert alert =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        alert.setTitle(
                "Confirm Department Removal"
        );

        alert.setHeaderText(
                "Delete "
                        + dept.getName()
                        + " Department?"
        );

        alert.setContentText(
                "Are you sure you want to remove this department?"
                        + "\n\nThe department will be deactivated "
                        + "rather than permanently deleted."
        );

        Optional<ButtonType> result =
                alert.showAndWait();

        if (result.isPresent()
                && result.get()
                == ButtonType.OK) {

            try {

                departmentController.removeDepartment(
                        dept.getDepartmentId()
                );

                reloadDepartments();

                showAlert(
                        "Success",
                        "Department removed successfully."
                );

            } catch (Exception e) {

                showAlert(
                        "Unable to Remove Department",
                        getErrorMessage(e)
                );
            }
        }
    }

    // =========================================================
    // RELOAD
    // =========================================================

    private void reloadDepartments() {

        try {

            loadDepartmentsFromFirestore();

            applyFiltersAndRefreshUI();

        } catch (Exception e) {

            showAlert(
                    "Refresh Error",
                    getErrorMessage(e)
            );
        }
    }

    // =========================================================
    // CSV EXPORT
    // =========================================================

    private void exportDepartmentDataToCSV(
            Stage stage) {

        FileChooser fileChooser =
                new FileChooser();

        fileChooser.setTitle(
                "Export Departments Data"
        );

        fileChooser.setInitialFileName(
                "Hospital_Departments.csv"
        );

        fileChooser
                .getExtensionFilters()
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

        try (PrintWriter writer =
                     new PrintWriter(file)) {

            writer.println(
                    "Department ID,"
                            + "Department Name,"
                            + "Head,"
                            + "Category,"
                            + "24/7,"
                            + "Doctor Count,"
                            + "Patient Count,"
                            + "Appointments Count"
            );

            for (Department dept :
                    filteredDepartmentList) {

                writer.printf(
                        "\"%s\",\"%s\",\"%s\",\"%s\",%s,%d,%d,%d%n",

                        escapeCSV(
                                dept.getDepartmentId()
                        ),

                        escapeCSV(
                                dept.getName()
                        ),

                        escapeCSV(
                                dept.getHead()
                        ),

                        escapeCSV(
                                dept.getCategory()
                        ),

                        dept.isIs247()
                                ? "Yes"
                                : "No",

                        dept.getDoctorCount(),

                        dept.getPatientCount(),

                        dept.getAppointments()
                                .size()
                );
            }

            showAlert(
                    "Export Successful",
                    "Department records successfully exported to:\n"
                            + file.getAbsolutePath()
            );

        } catch (Exception e) {

            showAlert(
                    "Export Error",
                    "Could not export data: "
                            + getErrorMessage(e)
            );
        }
    }

    private String escapeCSV(
            String value) {

        if (value == null) {
            return "";
        }

        return value.replace(
                "\"",
                "\"\""
        );
    }

    // =========================================================
    // CARD STYLE
    // =========================================================

    private void applyCardStyle(
            Region region) {

        region.setStyle(
                "-fx-background-color: "
                        + CARD_BG
                        + ";"
                        + "-fx-background-radius: 12;"
                        + "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-radius: 12;"
        );

        DropShadow shadow =
                new DropShadow();

        shadow.setRadius(8);

        shadow.setOffsetY(2);

        shadow.setColor(
                Color.rgb(
                        15,
                        23,
                        42,
                        0.06
                )
        );

        region.setEffect(
                shadow
        );
    }

    // =========================================================
    // SAFE NAVIGATION
    // =========================================================

    private void navigateSafely(
            Stage stage,
            SceneSupplier sceneSupplier) {

        try {

            stage.setScene(
                    sceneSupplier.get()
            );

        } catch (Exception e) {

            showAlert(
                    "Navigation Error",
                    getErrorMessage(e)
            );
        }
    }

    @FunctionalInterface
    private interface SceneSupplier {
        Scene get();
    }

    // =========================================================
    // ALERT
    // =========================================================

    private void showAlert(
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

    // =========================================================
    // ERROR MESSAGE
    // =========================================================

    private String getErrorMessage(
            Throwable exception) {

        if (exception == null) {
            return "Unknown error.";
        }

        Throwable cause =
                exception;

        while (cause.getCause() != null) {

            cause =
                    cause.getCause();
        }

        if (cause.getMessage() != null
                && !cause.getMessage()
                .trim()
                .isEmpty()) {

            return cause.getMessage();
        }

        if (exception.getMessage() != null
                && !exception.getMessage()
                .trim()
                .isEmpty()) {

            return exception.getMessage();
        }

        return "An unexpected error occurred.";
    }
}