package com.healthsphere.view.Hospital;

import com.healthsphere.controller.hospital.DepartmentController;
import com.healthsphere.model.HospitalDepartment;
import com.healthsphere.util.SessionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

/**
 * Department Management View
 *
 * Architecture:
 *
 * JavaFX View
 *      ↓
 * DepartmentController
 *      ↓
 * DepartmentDAO
 *      ↓
 * Firebase Firestore
 *
 * This view does not directly access Firestore.
 *
 * Department data is stored in the HospitalDepartment model.
 * The inner Department class is only a presentation/UI model.
 */
public class DepartmentManagementView {

    // =========================================================
    // COLOR PALETTE
    // =========================================================

    private static final String PRIMARY_BLUE = "#170eca";
    private static final String PRIMARY_LIGHT = "#EFF5FF";

    private static final String DARK_TEXT = "#0F172A";
    private static final String SECONDARY_TEXT = "#64748B";

    private static final String LIGHT_BACKGROUND = "#F8FAFC";
    private static final String CARD_BG = "#FFFFFF";
    private static final String BORDER = "#E2E8F0";

    // Dark Sidebar
    private static final String SIDEBAR_BG = "#0F172A";
    private static final String SIDEBAR_HOVER = "#1E293B";
    private static final String SIDEBAR_TEXT_MUTED = "#94A3B8";
    private static final String SIDEBAR_BORDER = "#1E293B";

    // Status colors
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

    /*
     * IMPORTANT:
     *
     * This is intentionally NOT final.
     *
     * We initialize the controller inside createScene()
     * after the user session has already been established.
     *
     * This prevents premature Firebase/session access.
     */
    private DepartmentController departmentController;

    // =========================================================
    // UI APPOINTMENT MODEL
    // =========================================================

    /**
     * Appointment is currently a UI-only presentation object.
     *
     * Appointment persistence belongs to the Appointment module.
     */
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

    // =========================================================
    // UI DEPARTMENT MODEL
    // =========================================================

    /**
     * UI presentation model.
     *
     * HospitalDepartment is the actual Firestore model.
     *
     * This class contains additional UI-only information:
     *
     * - doctor count
     * - patient count
     * - theme color
     * - icon
     * - embedded appointments
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

        public void setThemeColor(String themeColor) {
            this.themeColor = themeColor;
        }

        public String getThemeBgColor() {
            return themeBgColor;
        }

        public void setThemeBgColor(String themeBgColor) {
            this.themeBgColor = themeBgColor;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
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

        /*
         * Do not initialize DepartmentController here.
         *
         * SessionManager may not contain the current hospital
         * before login has completed.
         *
         * Controller is created inside createScene().
         */
    }

    // =========================================================
    // CREATE SCENE
    // =========================================================

    public Scene createScene(Stage stage) {

        // -----------------------------------------------------
        // SESSION CHECK
        // -----------------------------------------------------

        if (!SessionManager.isLoggedIn()) {

            showAlert(
                    "Session Expired",
                    "No active hospital session was found. Please log in again."
            );

            return new Scene(
                    new StackPane(
                            new Label("Session expired. Please log in again.")
                    ),
                    1000,
                    700
            );
        }

        // -----------------------------------------------------
        // INITIALIZE CONTROLLER
        // -----------------------------------------------------

        departmentController =
                new DepartmentController();

        // -----------------------------------------------------
        // LOAD FIRESTORE DATA
        // -----------------------------------------------------

        try {

            loadDepartmentsFromFirestore();

        } catch (Exception e) {

            masterDepartmentList.clear();

            filteredDepartmentList =
                    new FilteredList<>(
                            masterDepartmentList,
                            p -> true
                    );

            showAlert(
                    "Department Loading Error",
                    getErrorMessage(e)
            );
        }

        // -----------------------------------------------------
        // ROOT
        // -----------------------------------------------------

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color: "
                        + LIGHT_BACKGROUND
                        + ";"
        );

        // -----------------------------------------------------
        // SIDEBAR
        // -----------------------------------------------------

        root.setLeft(
                createSidebar(stage)
        );

        // -----------------------------------------------------
        // TOP BAR
        // -----------------------------------------------------

        root.setTop(
                createTopBar()
        );

        // -----------------------------------------------------
        // MAIN CONTENT
        // -----------------------------------------------------

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

        // -----------------------------------------------------
        // INITIAL UI REFRESH
        // -----------------------------------------------------

        applyFiltersAndRefreshUI();

        double width =
                stage.getWidth() > 0
                        ? stage.getWidth()
                        : 1200;

        double height =
                stage.getHeight() > 0
                        ? stage.getHeight()
                        : 800;

        return new Scene(
                root,
                width,
                height
        );
    }

    // =========================================================
    // LOAD DEPARTMENTS FROM FIRESTORE
    // =========================================================

    private void loadDepartmentsFromFirestore() {

        masterDepartmentList.clear();

        List<HospitalDepartment> departments =
                departmentController.getAllDepartments();

        if (departments == null) {

            filteredDepartmentList =
                    new FilteredList<>(
                            masterDepartmentList,
                            p -> true
                    );

            return;
        }

        for (HospitalDepartment hospitalDepartment :
                departments) {

            if (hospitalDepartment == null) {
                continue;
            }

            if (!hospitalDepartment.isActive()) {
                continue;
            }

            Department department =
                    convertToUIDepartment(
                            hospitalDepartment
                    );

            masterDepartmentList.add(
                    department
            );
        }

        filteredDepartmentList =
                new FilteredList<>(
                        masterDepartmentList,
                        p -> true
                );
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

        category =
                category.trim();

        String name =
                hospitalDepartment.getName();

        if (name == null
                || name.trim().isEmpty()) {

            name = "Unnamed Department";
        }

        String headDoctorId =
                hospitalDepartment.getHeadDoctorId();

        String head;

        if (headDoctorId == null
                || headDoctorId.trim().isEmpty()) {

            head = "Not Assigned";

        } else {

            head =
                    "Doctor ID: "
                            + headDoctorId.trim();
        }

        String themeColor =
                getCategoryColor(category);

        String themeBackground =
                getCategoryBackground(category);

        String icon =
                getCategoryIcon(category);

        /*
         * Doctor and patient counts are intentionally 0.
         *
         * HospitalDepartment does not store these counts.
         *
         * They should later be calculated from the real
         * HospitalDoctor / Appointment collections.
         */
        int doctorCount = 0;

        int patientCount = 0;

        return new Department(
                hospitalDepartment.getDepartmentId(),
                name.trim(),
                head,
                doctorCount,
                patientCount,
                category,
                themeColor,
                themeBackground,
                icon,
                hospitalDepartment.is24x7(),
                hospitalDepartment.isActive()
        );
    }

    // =========================================================
    // CATEGORY COLOR
    // =========================================================

    private String getCategoryColor(
            String category) {

        if (category == null) {
            return PRIMARY_BLUE;
        }

        switch (category.toLowerCase()) {

            case "surgical":
                return ERROR_RED;

            case "diagnostic":
                return PURPLE;

            case "emergency":
                return WARNING_ORANGE;

            case "support":
                return SUCCESS_GREEN;

            case "clinical":
            default:
                return PRIMARY_BLUE;
        }
    }

    // =========================================================
    // CATEGORY BACKGROUND
    // =========================================================

    private String getCategoryBackground(
            String category) {

        if (category == null) {
            return PRIMARY_LIGHT;
        }

        switch (category.toLowerCase()) {

            case "surgical":
                return ERROR_LIGHT;

            case "diagnostic":
                return PURPLE_LIGHT;

            case "emergency":
                return WARNING_LIGHT;

            case "support":
                return SUCCESS_LIGHT;

            case "clinical":
            default:
                return PRIMARY_LIGHT;
        }
    }

    // =========================================================
    // CATEGORY ICON
    // =========================================================

    private String getCategoryIcon(
            String category) {

        if (category == null) {
            return "✚";
        }

        switch (category.toLowerCase()) {

            case "surgical":
                return "✚";

            case "diagnostic":
                return "◉";

            case "emergency":
                return "⚠";

            case "support":
                return "✓";

            case "clinical":
            default:
                return "✚";
        }
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private VBox createSidebar(
            Stage stage) {

        VBox sidebar =
                new VBox(6);

        sidebar.setPrefWidth(240);

        sidebar.setPadding(
                new Insets(
                        24,
                        16,
                        20,
                        16
                )
        );

        sidebar.setStyle(
                "-fx-background-color: "
                        + SIDEBAR_BG
                        + ";"
                        + "-fx-border-color: "
                        + SIDEBAR_BORDER
                        + ";"
                        + "-fx-border-width: 0 1 0 0;"
        );

        // -----------------------------------------------------
        // LOGO
        // -----------------------------------------------------

        VBox logoBox =
                new VBox(2);

        logoBox.setPadding(
                new Insets(
                        0,
                        8,
                        24,
                        8
                )
        );

        Label logo =
                new Label(
                        "Health-Sphere"
                );

        logo.setStyle(
                "-fx-font-size: 22px;"
                        + "-fx-font-weight: 800;"
                        + "-fx-text-fill: #FFFFFF;"
        );

        Label subtitle =
                new Label(
                        "SMART HEALTHCARE"
                );

        subtitle.setStyle(
                "-fx-font-size: 9px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-letter-spacing: 1px;"
                        + "-fx-text-fill: "
                        + SIDEBAR_TEXT_MUTED
                        + ";"
        );

        logoBox.getChildren().addAll(
                logo,
                subtitle
        );

        sidebar.getChildren().add(
                logoBox
        );

        // -----------------------------------------------------
        // NAVIGATION BUTTONS
        // -----------------------------------------------------

        Button dashboardButton =
                createNavigationButton(
                        "▦",
                        "Dashboard",
                        false
                );

        Button doctorButton =
                createNavigationButton(
                        "♙",
                        "Doctors",
                        false
                );

        Button departmentButton =
                createNavigationButton(
                        "✚",
                        "Departments",
                        true
                );

        Button bedButton =
                createNavigationButton(
                        "▥",
                        "Beds",
                        false
                );

        Button appointmentButton =
                createNavigationButton(
                        "▣",
                        "Appointments",
                        false
                );

        Button analyticsButton =
                createNavigationButton(
                        "◈",
                        "Analytics",
                        false
                );

        Button settingsButton =
                createNavigationButton(
                        "⚙",
                        "Hospital Settings",
                        false
                );

        sidebar.getChildren().addAll(
                dashboardButton,
                doctorButton,
                departmentButton,
                bedButton,
                appointmentButton,
                analyticsButton,
                settingsButton
        );

        // -----------------------------------------------------
        // NAVIGATION ACTIONS
        // -----------------------------------------------------

        dashboardButton.setOnAction(
                event ->
                        navigateSafely(
                                stage,
                                () ->
                                        new HospitalDashboardView()
                                                .createScene(stage)
                        )
        );

        doctorButton.setOnAction(
                event ->
                        navigateSafely(
                                stage,
                                () ->
                                        new DoctorManagementView()
                                                .createScene(stage)
                        )
        );

        departmentButton.setOnAction(
                event ->
                        navigateSafely(
                                stage,
                                () ->
                                        new DepartmentManagementView()
                                                .createScene(stage)
                        )
        );

        bedButton.setOnAction(
                event ->
                        navigateSafely(
                                stage,
                                () ->
                                        new BedManagementView()
                                                .createScene(stage)
                        )
        );

        appointmentButton.setOnAction(
                event ->
                        navigateSafely(
                                stage,
                                () ->
                                        new AppointmentManagementView()
                                                .createScene(stage)
                        )
        );

        analyticsButton.setOnAction(
                event ->
                        navigateSafely(
                                stage,
                                () ->
                                        new HospitalAnalyticsView()
                                                .createScene(stage)
                        )
        );

        settingsButton.setOnAction(
                event ->
                        navigateSafely(
                                stage,
                                () ->
                                        new HospitalProfileSettingsView()
                                                .createScene(stage)
                        )
        );

        // -----------------------------------------------------
        // SPACER
        // -----------------------------------------------------

        Region spacer =
                new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        sidebar.getChildren().add(
                spacer
        );

        // -----------------------------------------------------
        // HELP
        // -----------------------------------------------------

        Button helpButton =
                createNavigationButton(
                        "?",
                        "Help Center",
                        false
                );

        helpButton.setOnAction(
                event ->
                        showAlert(
                                "Help Center",
                                "For assistance, please contact support@healthsphere.com"
                        )
        );

        // -----------------------------------------------------
        // LOGOUT
        // -----------------------------------------------------

        Button logoutButton =
                createNavigationButton(
                        "↪",
                        "Logout",
                        false
                );

        logoutButton.setOnAction(
                event ->
                        handleLogout(stage)
        );

        sidebar.getChildren().addAll(
                helpButton,
                logoutButton
        );

        return sidebar;
    }

    // =========================================================
    // NAVIGATION BUTTON
    // =========================================================

    private Button createNavigationButton(
            String icon,
            String text,
            boolean selected) {

        Button button =
                new Button();

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setPrefHeight(42);

        button.setAlignment(
                Pos.CENTER_LEFT
        );

        button.setPadding(
                new Insets(
                        0,
                        12,
                        0,
                        12
                )
        );

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-font-size: 16px;"
                        + "-fx-text-fill: "
                        + (selected
                        ? "#FFFFFF"
                        : SIDEBAR_TEXT_MUTED)
                        + ";"
        );

        Label textLabel =
                new Label(text);

        textLabel.setStyle(
                "-fx-font-size: 13px;"
                        + "-fx-font-weight: "
                        + (selected
                        ? "bold"
                        : "500")
                        + ";"
                        + "-fx-text-fill: "
                        + (selected
                        ? "#FFFFFF"
                        : SIDEBAR_TEXT_MUTED)
                        + ";"
        );

        HBox content =
                new HBox(12);

        content.setAlignment(
                Pos.CENTER_LEFT
        );

        content.getChildren().addAll(
                iconLabel,
                textLabel
        );

        button.setGraphic(
                content
        );

        String normalStyle;

        if (selected) {

            normalStyle =
                    "-fx-background-color: "
                            + PRIMARY_BLUE
                            + ";"
                            + "-fx-background-radius: 8;"
                            + "-fx-cursor: hand;";

        } else {

            normalStyle =
                    "-fx-background-color: transparent;"
                            + "-fx-background-radius: 8;"
                            + "-fx-cursor: hand;";
        }

        button.setStyle(
                normalStyle
        );

        if (!selected) {

            button.setOnMouseEntered(
                    event ->
                            button.setStyle(
                                    "-fx-background-color: "
                                            + SIDEBAR_HOVER
                                            + ";"
                                            + "-fx-background-radius: 8;"
                                            + "-fx-cursor: hand;"
                            )
            );

            button.setOnMouseExited(
                    event ->
                            button.setStyle(
                                    normalStyle
                            )
            );
        }

        return button;
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
    // LOGOUT
    // =========================================================

    private void handleLogout(
            Stage stage) {

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Logout"
        );

        confirmation.setHeaderText(
                "Are you sure you want to logout?"
        );

        confirmation.setContentText(
                "Your current hospital session will be cleared."
        );

        Optional<ButtonType> result =
                confirmation.showAndWait();

        if (result.isPresent()
                && result.get() == ButtonType.OK) {

            SessionManager.clearSession();

            showAlert(
                    "Logout",
                    "Logged out successfully."
            );

            /*
             * We only clear the shared session here.
             *
             * Login-screen navigation depends on the existing
             * authentication/navigation implementation.
             */
            stage.close();
        }
    }

    // =========================================================
    // TOP BAR
    // =========================================================

    private HBox createTopBar() {

        HBox topBar =
                new HBox();

        topBar.setAlignment(
                Pos.CENTER_LEFT
        );

        topBar.setPadding(
                new Insets(
                        16,
                        24,
                        16,
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

        // -----------------------------------------------------
        // SEARCH
        // -----------------------------------------------------

        Label searchIcon =
                new Label("⌕");

        searchIcon.setStyle(
                "-fx-font-size: 18px;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        TextField topSearch =
                new TextField();

        topSearch.setPromptText(
                "Search departments..."
        );

        topSearch.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-prompt-text-fill: #94A3B8;"
                        + "-fx-font-size: 13px;"
                        + "-fx-text-inner-color: "
                        + DARK_TEXT
                        + ";"
        );

        topSearch.textProperty()
                .addListener(
                        (obs, oldValue, newValue) -> {

                            if (searchField != null) {

                                searchField.setText(
                                        newValue
                                );
                            }
                        }
                );

        HBox.setHgrow(
                topSearch,
                Priority.ALWAYS
        );

        HBox searchBox =
                new HBox(8);

        searchBox.setAlignment(
                Pos.CENTER_LEFT
        );

        searchBox.setPrefWidth(360);

        searchBox.setPrefHeight(40);

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
                topSearch
        );

        // -----------------------------------------------------
        // SPACER
        // -----------------------------------------------------

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        // -----------------------------------------------------
        // NOTIFICATION
        // -----------------------------------------------------

        Label notification =
                new Label("🔔");

        notification.setStyle(
                "-fx-font-size: 16px;"
                        + "-fx-cursor: hand;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        notification.setOnMouseClicked(
                event ->
                        showAlert(
                                "Notifications",
                                "You have 0 new notifications."
                        )
        );

        // -----------------------------------------------------
        // SETTINGS
        // -----------------------------------------------------

        Label settings =
                new Label("⚙");

        settings.setStyle(
                "-fx-font-size: 18px;"
                        + "-fx-cursor: hand;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        // -----------------------------------------------------
        // USER INFO
        // -----------------------------------------------------

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

        // -----------------------------------------------------
        // AVATAR
        // -----------------------------------------------------

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

        addDepartment.setPrefHeight(42);

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
                event ->
                        addDepartment.setStyle(
                                actionBtnStyle
                                        + "-fx-background-color: #1550B0;"
                        )
        );

        addDepartment.setOnMouseExited(
                event ->
                        addDepartment.setStyle(
                                actionBtnStyle
                        )
        );

        addDepartment.setOnAction(
                event ->
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

        HBox.setHgrow(
                statistics,
                Priority.ALWAYS
        );

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
            String backgroundColor) {

        VBox card =
                new VBox(6);

        card.setPadding(
                new Insets(18)
        );

        card.setMinHeight(115);

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

        Circle iconCircle =
                new Circle(17);

        iconCircle.setFill(
                Color.web(
                        backgroundColor
                )
        );

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + color
                        + ";"
        );

        StackPane iconBox =
                new StackPane(
                        iconCircle,
                        iconLabel
                );

        top.getChildren().addAll(
                titleLabel,
                spacer,
                iconBox
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-font-size: 25px;"
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
    // SEARCH + FILTER BAR
    // =========================================================

    private HBox createSearchBar(
            Stage stage) {

        HBox container =
                new HBox(10);

        container.setAlignment(
                Pos.CENTER_LEFT
        );

        container.setPadding(
                new Insets(
                        4,
                        0,
                        4,
                        0
                )
        );

        // -----------------------------------------------------
        // SEARCH FIELD
        // -----------------------------------------------------

        searchField =
                new TextField();

        searchField.setPromptText(
                "Search by department name or head..."
        );

        searchField.setPrefHeight(40);

        searchField.setPrefWidth(300);

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
                        + "-fx-padding: 0 12px;"
        );

        searchField.textProperty()
                .addListener(
                        (obs, oldValue, newValue) ->
                                applyFiltersAndRefreshUI()
                );

        // -----------------------------------------------------
        // CATEGORY FILTER
        // -----------------------------------------------------

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

        categoryFilter.setPrefHeight(40);

        categoryFilter.setPrefWidth(180);

        categoryFilter.valueProperty()
                .addListener(
                        (obs, oldValue, newValue) ->
                                applyFiltersAndRefreshUI()
                );

        // -----------------------------------------------------
        // SPACER
        // -----------------------------------------------------

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        // -----------------------------------------------------
        // RESET BUTTON
        // -----------------------------------------------------

        Button filterButton =
                new Button(
                        "☷  Reset Filters"
                );

        filterButton.setPrefHeight(40);

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
                event -> {

                    searchField.clear();

                    categoryFilter.setValue(
                            "All Departments"
                    );

                    applyFiltersAndRefreshUI();
                }
        );

        // -----------------------------------------------------
        // EXPORT BUTTON
        // -----------------------------------------------------

        Button exportButton =
                new Button(
                        "↓  Export"
                );

        exportButton.setPrefHeight(40);

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
                event ->
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
    // DEPARTMENT GRID SECTION
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

        // -----------------------------------------------------
        // FLOW GRID
        // -----------------------------------------------------

        departmentGridPane =
                new FlowPane();

        departmentGridPane.setHgap(18);

        departmentGridPane.setVgap(18);

        departmentGridPane.setPrefWrapLength(
                800
        );

        container.getChildren().add(
                departmentGridPane
        );

        return container;
    }

    // =========================================================
    // FILTER AND REFRESH
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

        String category =
                categoryFilter != null
                        && categoryFilter.getValue() != null
                        ? categoryFilter.getValue()
                        : "All Departments";

        filteredDepartmentList.setPredicate(
                department -> {

                    String name =
                            safeString(
                                    department.getName()
                            ).toLowerCase();

                    String head =
                            safeString(
                                    department.getHead()
                            ).toLowerCase();

                    String departmentCategory =
                            safeString(
                                    department.getCategory()
                            );

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
                                    .equalsIgnoreCase(
                                            category
                                    )
                                    || departmentCategory
                                    .equalsIgnoreCase(
                                            category
                                    );

                    return matchesSearch
                            && matchesCategory;
                }
        );

        rebuildDepartmentGrid();

        updateStatistics();
    }

    // =========================================================
    // REBUILD DEPARTMENT GRID
    // =========================================================

    private void rebuildDepartmentGrid() {

        if (departmentGridPane == null) {
            return;
        }

        departmentGridPane
                .getChildren()
                .clear();

        if (filteredDepartmentList == null
                || filteredDepartmentList.isEmpty()) {

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

            departmentGridPane
                    .getChildren()
                    .add(
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

            card.setPrefWidth(375);

            card.setMinWidth(350);

            departmentGridPane
                    .getChildren()
                    .add(
                            card
                    );
        }
    }

    // =========================================================
    // UPDATE STATISTICS
    // =========================================================

    private void updateStatistics() {

        int totalDepartments =
                masterDepartmentList.size();

        int totalDoctors =
                0;

        int activeDepartments =
                0;

        int emergencyDepartments =
                0;

        for (Department department :
                masterDepartmentList) {

            totalDoctors +=
                    department.getDoctorCount();

            if (department.isActive()) {

                activeDepartments++;
            }

            if (department.isIs247()
                    || "Emergency".equalsIgnoreCase(
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

            int count =
                    filteredDepartmentList == null
                            ? 0
                            : filteredDepartmentList.size();

            departmentCountHeaderLabel.setText(
                    count
                            + (
                            count == 1
                                    ? " Department"
                                    : " Departments"
                    )
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
        // HOVER
        // -----------------------------------------------------

        card.setOnMouseEntered(
                event ->
                        card.setStyle(
                                "-fx-background-color: "
                                        + CARD_BG
                                        + ";"
                                        + "-fx-background-radius: 12;"
                                        + "-fx-border-color: "
                                        + PRIMARY_BLUE
                                        + ";"
                                        + "-fx-border-radius: 12;"
                        )
        );

        card.setOnMouseExited(
                event ->
                        applyCardStyle(card)
        );

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

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button addAppointmentButton =
                new Button(
                        "＋"
                );

        addAppointmentButton.setTooltip(
                new javafx.scene.control.Tooltip(
                        "Schedule Appointment"
                )
        );

        addAppointmentButton.setPrefWidth(32);

        addAppointmentButton.setPrefHeight(30);

        addAppointmentButton.setStyle(
                "-fx-background-color: "
                        + PRIMARY_LIGHT
                        + ";"
                        + "-fx-text-fill: "
                        + PRIMARY_BLUE
                        + ";"
                        + "-fx-background-radius: 6;"
                        + "-fx-font-size: 14px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-cursor: hand;"
        );

        addAppointmentButton.setOnAction(
                event ->
                        showAddAppointmentDialog(
                                dept
                        )
        );

        top.getChildren().addAll(
                iconBox,
                nameBox,
                spacer,
                addAppointmentButton
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
                new Label(
                        "Head"
                );

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

        headName.setWrapText(
                true
        );

        headName.setMaxWidth(
                170
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

        Label patsIconLabel =
                new Label(
                        "👤 "
                                + dept.getPatientCount()
                                + " Patients"
                );

        patsIconLabel.setStyle(
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
                patsIconLabel
        );

        // -----------------------------------------------------
        // 24/7 BADGE
        // -----------------------------------------------------

        HBox statusBox =
                new HBox();

        statusBox.setAlignment(
                Pos.CENTER_LEFT
        );

        if (dept.isIs247()) {

            Label emergencyLabel =
                    new Label(
                            "24/7 Operational"
                    );

            emergencyLabel.setStyle(
                    "-fx-background-color: "
                            + WARNING_LIGHT
                            + ";"
                            + "-fx-text-fill: "
                            + WARNING_ORANGE
                            + ";"
                            + "-fx-background-radius: 6;"
                            + "-fx-padding: 4 8 4 8;"
                            + "-fx-font-size: 9px;"
                            + "-fx-font-weight: bold;"
            );

            statusBox.getChildren().add(
                    emergencyLabel
            );
        }

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
                event ->
                        showEditDepartmentDialog(
                                dept
                        )
        );

        delete.setOnAction(
                event ->
                        handleDeleteDepartment(
                                dept
                        )
        );

        actions.getChildren().addAll(
                edit,
                delete
        );

        // -----------------------------------------------------
        // ADD TO CARD
        // -----------------------------------------------------

        card.getChildren().addAll(
                top,
                new Separator(),
                infoBox
        );

        if (!statusBox.getChildren().isEmpty()) {

            card.getChildren().add(
                    statusBox
            );
        }

        card.getChildren().addAll(
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
                        + "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-radius: 8;"
        );

        Label title =
                new Label(
                        "Appointments ("
                                + dept.getAppointments().size()
                                + ")"
                );

        title.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + DARK_TEXT
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

        } else {

            int displayCount =
                    Math.min(
                            dept.getAppointments().size(),
                            3
                    );

            for (int i = 0;
                 i < displayCount;
                 i++) {

                Appointment appointment =
                        dept.getAppointments()
                                .get(i);

                HBox appointmentRow =
                        new HBox(6);

                appointmentRow.setAlignment(
                        Pos.CENTER_LEFT
                );

                Label time =
                        new Label(
                                appointment
                                        .getTimeSlot()
                        );

                time.setStyle(
                        "-fx-font-size: 10px;"
                                + "-fx-font-weight: bold;"
                                + "-fx-text-fill: "
                                + PRIMARY_BLUE
                                + ";"
                );

                Label patient =
                        new Label(
                                appointment
                                        .getPatientName()
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

                String statusColor =
                        getAppointmentStatusColor(
                                appointment.getStatus()
                        );

                Label status =
                        new Label(
                                appointment.getStatus()
                        );

                status.setStyle(
                        "-fx-font-size: 9px;"
                                + "-fx-font-weight: bold;"
                                + "-fx-text-fill: "
                                + statusColor
                                + ";"
                );

                appointmentRow
                        .getChildren()
                        .addAll(
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

            if (dept.getAppointments().size() > 3) {

                Label more =
                        new Label(
                                "+"
                                        + (
                                        dept.getAppointments()
                                                .size()
                                                - 3
                                )
                                        + " more appointments"
                        );

                more.setStyle(
                        "-fx-font-size: 9px;"
                                + "-fx-text-fill: "
                                + SECONDARY_TEXT
                                + ";"
                );

                box.getChildren().add(
                        more
                );
            }
        }

        return box;
    }

    // =========================================================
    // APPOINTMENT STATUS COLOR
    // =========================================================

    private String getAppointmentStatusColor(
            String status) {

        if (status == null) {
            return DARK_TEXT;
        }

        switch (status.toLowerCase()) {

            case "scheduled":
                return WARNING_ORANGE;

            case "in-progress":
                return PRIMARY_BLUE;

            case "completed":
                return SUCCESS_GREEN;

            default:
                return SECONDARY_TEXT;
        }
    }

    // =========================================================
    // SMALL BUTTON
    // =========================================================

    private Button createSmallButton(
            String text,
            String color,
            String backgroundColor) {

        Button button =
                new Button(text);

        button.setPrefHeight(32);

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox.setHgrow(
                button,
                Priority.ALWAYS
        );

        String style =
                "-fx-background-color: "
                        + backgroundColor
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
                event ->
                        button.setStyle(
                                "-fx-background-color: "
                                        + color
                                        + "25;"
                                        + "-fx-text-fill: "
                                        + color
                                        + ";"
                                        + "-fx-background-radius: 6;"
                                        + "-fx-font-size: 11px;"
                                        + "-fx-font-weight: bold;"
                                        + "-fx-cursor: hand;"
                        )
        );

        button.setOnMouseExited(
                event ->
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

        // -----------------------------------------------------
        // NAME
        // -----------------------------------------------------

        Label nameLabel =
                new Label(
                        "Department Name:"
                );

        TextField nameInput =
                new TextField();

        nameInput.setPromptText(
                "e.g. Oncology"
        );

        // -----------------------------------------------------
        // HEAD DOCTOR UID
        // -----------------------------------------------------

        Label headLabel =
                new Label(
                        "Head Doctor UID:"
                );

        TextField headInput =
                new TextField();

        headInput.setPromptText(
                "Optional Doctor UID"
        );

        // -----------------------------------------------------
        // CATEGORY
        // -----------------------------------------------------

        Label categoryLabel =
                new Label(
                        "Category:"
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
                "Clinical"
        );

        categoryInput.setMaxWidth(
                Double.MAX_VALUE
        );

        // -----------------------------------------------------
        // 24/7
        // -----------------------------------------------------

        CheckBox is247Check =
                new CheckBox(
                        "Operates 24/7 Emergency"
                );

        // -----------------------------------------------------
        // SAVE
        // -----------------------------------------------------

        Button saveButton =
                new Button(
                        "Add Department"
                );

        saveButton.setMaxWidth(
                Double.MAX_VALUE
        );

        saveButton.setPrefHeight(
                38
        );

        saveButton.setStyle(
                "-fx-background-color: "
                        + PRIMARY_BLUE
                        + ";"
                        + "-fx-text-fill: white;"
                        + "-fx-background-radius: 7;"
                        + "-fx-font-weight: bold;"
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

                        if (name.isEmpty()) {

                            showAlert(
                                    "Validation Error",
                                    "Department name is required."
                            );

                            return;
                        }

                        if (category == null
                                || category.trim().isEmpty()) {

                            showAlert(
                                    "Validation Error",
                                    "Department category is required."
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

                        dialog.close();

                        showAlert(
                                "Success",
                                "Department created successfully."
                        );

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

                nameLabel,
                nameInput,

                headLabel,
                headInput,

                categoryLabel,
                categoryInput,

                is247Check,

                saveButton
        );

        dialog.setScene(
                new Scene(
                        layout,
                        380,
                        390
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

        // -----------------------------------------------------
        // NAME
        // -----------------------------------------------------

        Label nameLabel =
                new Label(
                        "Department Name:"
                );

        TextField nameInput =
                new TextField(
                        dept.getName()
                );

        // -----------------------------------------------------
        // HEAD DOCTOR UID
        // -----------------------------------------------------

        Label headLabel =
                new Label(
                        "Head Doctor UID:"
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

        if (currentHead == null
                || "Not Assigned"
                .equalsIgnoreCase(
                        currentHead
                )) {

            currentHead = "";
        }

        headInput.setText(
                currentHead
        );

        headInput.setPromptText(
                "Optional Doctor UID"
        );

        // -----------------------------------------------------
        // CATEGORY
        // -----------------------------------------------------

        Label categoryLabel =
                new Label(
                        "Category:"
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

        // -----------------------------------------------------
        // 24/7
        // -----------------------------------------------------

        CheckBox is247Check =
                new CheckBox(
                        "Operates 24/7 Emergency"
                );

        is247Check.setSelected(
                dept.isIs247()
        );

        // -----------------------------------------------------
        // SAVE
        // -----------------------------------------------------

        Button saveButton =
                new Button(
                        "Save Changes"
                );

        saveButton.setMaxWidth(
                Double.MAX_VALUE
        );

        saveButton.setPrefHeight(
                38
        );

        saveButton.setStyle(
                "-fx-background-color: "
                        + PRIMARY_BLUE
                        + ";"
                        + "-fx-text-fill: white;"
                        + "-fx-background-radius: 7;"
                        + "-fx-font-weight: bold;"
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

                        dialog.close();

                        showAlert(
                                "Success",
                                "Department updated successfully."
                        );

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

                nameLabel,
                nameInput,

                headLabel,
                headInput,

                categoryLabel,
                categoryInput,

                is247Check,

                saveButton
        );

        dialog.setScene(
                new Scene(
                        layout,
                        380,
                        390
                )
        );

        dialog.showAndWait();
    }

    // =========================================================
    // ADD APPOINTMENT
    // =========================================================

    private void showAddAppointmentDialog(
            Department dept) {

        /*
         * IMPORTANT:
         *
         * Appointment persistence does NOT belong here.
         *
         * The Appointment module will later use
         * AppointmentController / AppointmentDAO.
         *
         * For now, this maintains the existing UI behavior.
         */

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

        layout.setStyle(
                "-fx-background-color: white;"
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

        // -----------------------------------------------------
        // PATIENT
        // -----------------------------------------------------

        TextField patientInput =
                new TextField();

        patientInput.setPromptText(
                "Patient Full Name"
        );

        // -----------------------------------------------------
        // DOCTOR
        // -----------------------------------------------------

        TextField doctorInput =
                new TextField();

        doctorInput.setPromptText(
                "Assigned Doctor Name"
        );

        // -----------------------------------------------------
        // TIME
        // -----------------------------------------------------

        TextField timeInput =
                new TextField();

        timeInput.setPromptText(
                "Time Slot (e.g. 11:30 AM)"
        );

        // -----------------------------------------------------
        // STATUS
        // -----------------------------------------------------

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

        statusInput.setMaxWidth(
                Double.MAX_VALUE
        );

        // -----------------------------------------------------
        // SAVE
        // -----------------------------------------------------

        Button saveButton =
                new Button(
                        "Confirm Appointment"
                );

        saveButton.setMaxWidth(
                Double.MAX_VALUE
        );

        saveButton.setPrefHeight(
                38
        );

        saveButton.setStyle(
                "-fx-background-color: "
                        + PRIMARY_BLUE
                        + ";"
                        + "-fx-text-fill: white;"
                        + "-fx-background-radius: 7;"
                        + "-fx-font-weight: bold;"
                        + "-fx-cursor: hand;"
        );

        saveButton.setOnAction(
                event -> {

                    String patientName =
                            patientInput
                                    .getText()
                                    .trim();

                    String doctorName =
                            doctorInput
                                    .getText()
                                    .trim();

                    String timeSlot =
                            timeInput
                                    .getText()
                                    .trim();

                    String status =
                            statusInput
                                    .getValue();

                    if (patientName.isEmpty()) {

                        showAlert(
                                "Validation Error",
                                "Patient name is required."
                        );

                        return;
                    }

                    if (timeSlot.isEmpty()) {

                        showAlert(
                                "Validation Error",
                                "Time slot is required."
                        );

                        return;
                    }

                    if (doctorName.isEmpty()) {

                        doctorName =
                                dept.getHead();
                    }

                    String appointmentId =
                            "APT-"
                                    + (
                                    100
                                            + dept
                                            .getAppointments()
                                            .size()
                                            + 1
                            );

                    Appointment appointment =
                            new Appointment(
                                    appointmentId,
                                    patientName,
                                    doctorName,
                                    timeSlot,
                                    status
                            );

                    dept.getAppointments()
                            .add(
                                    appointment
                            );

                    /*
                     * UI-only count.
                     *
                     * This will eventually be calculated from
                     * the actual appointment collection.
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
                        360,
                        400
                )
        );

        dialog.showAndWait();
    }

    // =========================================================
    // DELETE / DEACTIVATE DEPARTMENT
    // =========================================================

    private void handleDeleteDepartment(
            Department dept) {

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Confirm Department Removal"
        );

        confirmation.setHeaderText(
                "Delete "
                        + dept.getName()
                        + " Department?"
        );

        confirmation.setContentText(
                "Are you sure you want to remove this department?"
                        + "\n\n"
                        + "The department will be deactivated "
                        + "rather than permanently deleted."
        );

        Optional<ButtonType> result =
                confirmation.showAndWait();

        if (result.isPresent()
                && result.get() == ButtonType.OK) {

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

            if (filteredDepartmentList == null) {
                return;
            }

            for (Department department :
                    filteredDepartmentList) {

                writer.printf(
                        "\"%s\",\"%s\",\"%s\",\"%s\",%s,%d,%d,%d%n",

                        escapeCSV(
                                department
                                        .getDepartmentId()
                        ),

                        escapeCSV(
                                department
                                        .getName()
                        ),

                        escapeCSV(
                                department
                                        .getHead()
                        ),

                        escapeCSV(
                                department
                                        .getCategory()
                        ),

                        department.isIs247()
                                ? "Yes"
                                : "No",

                        department
                                .getDoctorCount(),

                        department
                                .getPatientCount(),

                        department
                                .getAppointments()
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
                    "Could not export data:\n"
                            + getErrorMessage(e)
            );
        }
    }

    // =========================================================
    // CSV ESCAPE
    // =========================================================

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
    // ERROR ALERT
    // =========================================================

    private void showErrorAlert(
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
                message
        );

        alert.showAndWait();
    }

    // =========================================================
    // ERROR MESSAGE
    // =========================================================

    private String getErrorMessage(
            Exception exception) {

        if (exception == null) {

            return "Unknown error.";
        }

        Throwable cause =
                exception;

        while (cause.getCause() != null) {

            cause =
                    cause.getCause();
        }

        String message =
                cause.getMessage();

        if (message == null
                || message.trim().isEmpty()) {

            message =
                    exception.getMessage();
        }

        if (message == null
                || message.trim().isEmpty()) {

            return "An unexpected error occurred.";
        }

        return message;
    }

    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safeString(
            String value) {

        return value == null
                ? ""
                : value;
    }

    // =========================================================
    // CARD STYLE
    // =========================================================

    private void applyCardStyle(
            javafx.scene.layout.Pane pane) {

        pane.setStyle(
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

        shadow.setColor(
                Color.rgb(
                        15,
                        23,
                        42,
                        0.04
                )
        );

        shadow.setRadius(
                10
        );

        shadow.setOffsetY(
                3
        );

        pane.setEffect(
                shadow
        );
    }
}