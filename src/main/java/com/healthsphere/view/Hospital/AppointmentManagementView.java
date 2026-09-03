package com.healthsphere.view.Hospital;

import com.healthsphere.controller.hospital.AppointmentController;
import com.healthsphere.controller.hospital.DoctorController;
import com.healthsphere.model.Appointment;
import com.healthsphere.model.HospitalDoctorDetails;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AppointmentManagementView {

    // =========================================================
    // COLORS
    // =========================================================

    private static final String PRIMARY_BLUE = "#102bdb";
    private static final String PRIMARY_LIGHT = "#EFF5FF";

    private static final String DARK_TEXT = "#0F172A";
    private static final String SECONDARY_TEXT = "#64748B";

    private static final String LIGHT_BACKGROUND = "#F8FAFC";
    private static final String CARD_BG = "#FFFFFF";
    private static final String BORDER = "#E2E8F0";

    private static final String SIDEBAR_BG = "#0F172A";
    private static final String SIDEBAR_BORDER = "#1E293B";
    private static final String SIDEBAR_TEXT = "#94A3B8";
    private static final String SIDEBAR_TEXT_HOVER = "#F8FAFC";
    private static final String SIDEBAR_HOVER_BG = "#1E293B";

    private static final String SUCCESS_GREEN = "#059669";
    private static final String SUCCESS_LIGHT = "#ECFDF5";

    private static final String WARNING_ORANGE = "#D97706";
    private static final String WARNING_LIGHT = "#FFFBEB";

    private static final String ERROR_RED = "#DC2626";
    private static final String ERROR_LIGHT = "#FEF2F2";

    private static final String PURPLE = "#7C3AED";
    private static final String PURPLE_LIGHT = "#F5F3FF";

    // =========================================================
    // CONTROLLERS
    // =========================================================

    private final AppointmentController appointmentController;
    private final DoctorController doctorController;

    // =========================================================
    // DATA
    // =========================================================

    private final ObservableList<Appointment> masterAppointmentList =
            FXCollections.observableArrayList();

    private List<Appointment> filteredAppointmentList =
            List.of();

    private final ObservableList<HospitalDoctorDetails> hospitalDoctors =
            FXCollections.observableArrayList();

    // =========================================================
    // UI REFERENCES
    // =========================================================

    private VBox tableRowsContainer;

    private Label totalAppointmentsCountLabel;
    private Label todayCountVal;
    private Label completedCountVal;
    private Label upcomingCountVal;
    private Label cancelledCountVal;

    private TextField filterSearchField;
    private DatePicker filterDatePicker;
    private ComboBox<String> filterDoctorCombo;
    private ComboBox<String> filterStatusCombo;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AppointmentManagementView() {

        appointmentController =
                new AppointmentController();

        doctorController =
                new DoctorController();
    }

    // =========================================================
    // CREATE SCENE
    // =========================================================

    public Scene createScene(Stage stage) {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color: "
                        + LIGHT_BACKGROUND
                        + ";"
        );

        root.setLeft(
                createSidebar(stage)
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
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;" +
                "-fx-padding: 0;"
        );

        root.setCenter(scrollPane);

        // Load actual hospital doctors
        loadHospitalDoctors();

        // Load actual appointments
        loadAppointments();

        return new Scene(
                root,
                stage.getWidth(),
                stage.getHeight()
        );
    }

    // =========================================================
    // LOAD HOSPITAL DOCTORS
    // =========================================================

    private void loadHospitalDoctors() {

        hospitalDoctors.clear();

        try {

            List<HospitalDoctorDetails> doctors =
                    doctorController.getAllDoctorDetails();

            if (doctors != null) {

                for (HospitalDoctorDetails doctor :
                        doctors) {

                    if (doctor == null) {
                        continue;
                    }

                    if (doctor.getDoctorId() == null ||
                            doctor.getDoctorId().trim().isEmpty()) {
                        continue;
                    }

                    if (doctor.getFullName() == null ||
                            doctor.getFullName().trim().isEmpty()) {
                        continue;
                    }

                    hospitalDoctors.add(doctor);
                }
            }

        } catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Unable to Load Doctors",
                    getErrorMessage(e)
            );
        }
    }

    // =========================================================
    // LOAD APPOINTMENTS
    // =========================================================

    private void loadAppointments() {

        try {

            List<Appointment> appointments =
                    appointmentController
                            .getHospitalAppointments();

            masterAppointmentList.clear();

            if (appointments != null) {

                masterAppointmentList.addAll(
                        appointments
                );
            }

            updateDoctorFilter();

            updateFilteredData();

        } catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Unable to Load Appointments",
                    getErrorMessage(e)
            );
        }
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private VBox createSidebar(Stage stage) {

        VBox sidebar = new VBox(6);

        sidebar.setPrefWidth(240);

        sidebar.setPadding(
                new Insets(24, 16, 20, 16)
        );

        sidebar.setStyle(
                "-fx-background-color: "
                        + SIDEBAR_BG
                        + ";" +
                "-fx-border-color: "
                        + SIDEBAR_BORDER
                        + ";" +
                "-fx-border-width: 0 1 0 0;"
        );

        VBox logoBox = new VBox(2);

        logoBox.setPadding(
                new Insets(0, 8, 24, 8)
        );

        Label logo =
                new Label("Health-Sphere");

        logo.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: #FFFFFF;"
        );

        Label subtitle =
                new Label("SMART HEALTHCARE");

        subtitle.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-letter-spacing: 1px;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        logoBox.getChildren().addAll(
                logo,
                subtitle
        );

        sidebar.getChildren().add(
                logoBox
        );

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
                        false
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
                        true
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

        dashboardButton.setOnAction(
                e -> navigateSafely(
                        stage,
                        () -> new HospitalDashboardView()
                                .createScene(stage)
                )
        );

        doctorButton.setOnAction(
                e -> navigateSafely(
                        stage,
                        () -> new DoctorManagementView()
                                .createScene(stage)
                )
        );

        departmentButton.setOnAction(
                e -> navigateSafely(
                        stage,
                        () -> new DepartmentManagementView()
                                .createScene(stage)
                )
        );

        bedButton.setOnAction(
                e -> navigateSafely(
                        stage,
                        () -> new BedManagementView()
                                .createScene(stage)
                )
        );

        analyticsButton.setOnAction(
                e -> navigateSafely(
                        stage,
                        () -> new HospitalAnalyticsView()
                                .createScene(stage)
                )
        );

        settingsButton.setOnAction(
                e -> navigateSafely(
                        stage,
                        () -> new HospitalProfileSettingsView()
                                .createScene(stage)
                )
        );

        Region spacer = new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        sidebar.getChildren().add(spacer);

        Button helpButton =
                createNavigationButton(
                        "?",
                        "Help Center",
                        false
                );

        Button logoutButton =
                createNavigationButton(
                        "↪",
                        "Logout",
                        false
                );

        helpButton.setOnAction(
                e -> showAlert(
                        Alert.AlertType.INFORMATION,
                        "Help Center",
                        "Support contact: support@healthsphere.com"
                )
        );

        logoutButton.setOnAction(
                e -> showAlert(
                        Alert.AlertType.INFORMATION,
                        "Logout",
                        "Logged out successfully."
                )
        );

        sidebar.getChildren().addAll(
                helpButton,
                logoutButton
        );

        return sidebar;
    }

    // =========================================================
    // NAVIGATION
    // =========================================================

    private void navigateSafely(
            Stage stage,
            SceneSupplier supplier
    ) {

        try {

            stage.setScene(
                    supplier.get()
            );

        } catch (NoClassDefFoundError |
                 Exception ex) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Navigation",
                    "Destination view is currently unavailable."
            );
        }
    }

    @FunctionalInterface
    private interface SceneSupplier {

        Scene get() throws Exception;
    }

    // =========================================================
    // NAVIGATION BUTTON
    // =========================================================

    private Button createNavigationButton(
            String icon,
            String text,
            boolean selected
    ) {

        Button button =
                new Button();

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-text-fill: "
                        + (
                        selected
                                ? "#FFFFFF"
                                : SIDEBAR_TEXT
                )
                        + ";"
        );

        Label textLabel =
                new Label(text);

        textLabel.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: "
                        + (
                        selected
                                ? "bold"
                                : "500"
                )
                        + ";" +
                "-fx-text-fill: "
                        + (
                        selected
                                ? "#FFFFFF"
                                : SIDEBAR_TEXT
                )
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

        button.setGraphic(content);

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setPrefHeight(42);

        button.setAlignment(
                Pos.CENTER_LEFT
        );

        button.setPadding(
                new Insets(0, 12, 0, 12)
        );

        String baseStyle =
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;";

        if (selected) {

            button.setStyle(
                    baseStyle +
                    "-fx-background-color: "
                            + PRIMARY_BLUE
                            + ";"
            );

        } else {

            button.setStyle(
                    baseStyle +
                    "-fx-background-color: transparent;"
            );

            button.setOnMouseEntered(
                    e -> {

                        button.setStyle(
                                baseStyle +
                                "-fx-background-color: "
                                        + SIDEBAR_HOVER_BG
                                        + ";"
                        );

                        iconLabel.setStyle(
                                "-fx-font-size: 16px;" +
                                "-fx-text-fill: "
                                        + SIDEBAR_TEXT_HOVER
                                        + ";"
                        );

                        textLabel.setStyle(
                                "-fx-font-size: 13px;" +
                                "-fx-font-weight: 500;" +
                                "-fx-text-fill: "
                                        + SIDEBAR_TEXT_HOVER
                                        + ";"
                        );
                    }
            );

            button.setOnMouseExited(
                    e -> {

                        button.setStyle(
                                baseStyle +
                                "-fx-background-color: transparent;"
                        );

                        iconLabel.setStyle(
                                "-fx-font-size: 16px;" +
                                "-fx-text-fill: "
                                        + SIDEBAR_TEXT
                                        + ";"
                        );

                        textLabel.setStyle(
                                "-fx-font-size: 13px;" +
                                "-fx-font-weight: 500;" +
                                "-fx-text-fill: "
                                        + SIDEBAR_TEXT
                                        + ";"
                        );
                    }
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
                new Insets(12, 28, 12, 28)
        );

        topBar.setStyle(
                "-fx-background-color: "
                        + CARD_BG
                        + ";" +
                "-fx-border-color: "
                        + BORDER
                        + ";" +
                "-fx-border-width: 0 0 1 0;"
        );

        Label searchIcon =
                new Label("⌕");

        searchIcon.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        TextField search =
                new TextField();

        search.setPromptText(
                "Search patients, doctors..."
        );

        search.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-prompt-text-fill: #94A3B8;" +
                "-fx-font-size: 13px;"
        );

        HBox.setHgrow(
                search,
                Priority.ALWAYS
        );

        search.textProperty().addListener(
                (obs, oldValue, newValue) -> {

                    if (filterSearchField != null) {

                        filterSearchField.setText(
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

        searchBox.setPrefWidth(360);

        searchBox.setPrefHeight(40);

        searchBox.setPadding(
                new Insets(0, 12, 0, 12)
        );

        searchBox.setStyle(
                "-fx-background-color: "
                        + LIGHT_BACKGROUND
                        + ";" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: "
                        + BORDER
                        + ";" +
                "-fx-border-radius: 8;"
        );

        searchBox.getChildren().addAll(
                searchIcon,
                search
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
                "-fx-font-size: 16px;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        Label settings =
                new Label("⚙");

        settings.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        Label administrator =
                new Label(
                        "Hospital Administrator"
                );

        administrator.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Label role =
                new Label("HOSPITAL ADMIN");

        role.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: "
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
                Color.web(PRIMARY_LIGHT)
        );

        avatar.setStroke(
                Color.web(BORDER)
        );

        Label avatarText =
                new Label("HA");

        avatarText.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: "
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
            Stage stage
    ) {

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
                createPageHeader(),
                createStatistics(),
                createFilters(stage),
                createAppointmentTable()
        );

        return content;
    }

    // =========================================================
    // PAGE HEADER
    // =========================================================

    private HBox createPageHeader() {

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox titleBox =
                new VBox(4);

        Label title =
                new Label(
                        "Appointment Management"
                );

        title.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Label subtitle =
                new Label(
                        "Manage patient appointments, track schedules, and handle bookings."
                );

        subtitle.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        titleBox.getChildren().addAll(
                title,
                subtitle
        );

        header.getChildren().add(
                titleBox
        );

        return header;
    }

    // =========================================================
    // STATISTICS
    // =========================================================

    private HBox createStatistics() {

        HBox statistics =
                new HBox(18);

        VBox todayCard =
                createStatisticCard(
                        "Today's Appointments",
                        "0",
                        "Scheduled for today",
                        "▣",
                        PRIMARY_BLUE,
                        PRIMARY_LIGHT
                );

        VBox completedCard =
                createStatisticCard(
                        "Completed",
                        "0",
                        "Appointments finished",
                        "✓",
                        SUCCESS_GREEN,
                        SUCCESS_LIGHT
                );

        VBox upcomingCard =
                createStatisticCard(
                        "Upcoming",
                        "0",
                        "Waiting for consultation",
                        "◷",
                        PURPLE,
                        PURPLE_LIGHT
                );

        VBox cancelledCard =
                createStatisticCard(
                        "Cancelled",
                        "0",
                        "Cancelled appointments",
                        "×",
                        ERROR_RED,
                        ERROR_LIGHT
                );

        todayCountVal =
                (Label) todayCard
                        .getChildren()
                        .get(1);

        completedCountVal =
                (Label) completedCard
                        .getChildren()
                        .get(1);

        upcomingCountVal =
                (Label) upcomingCard
                        .getChildren()
                        .get(1);

        cancelledCountVal =
                (Label) cancelledCard
                        .getChildren()
                        .get(1);

        statistics.getChildren().addAll(
                todayCard,
                completedCard,
                upcomingCard,
                cancelledCard
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
            String bgColor
    ) {

        VBox card =
                new VBox(10);

        card.setPadding(
                new Insets(20)
        );

        card.setPrefHeight(125);

        applyCardStyle(card);

        HBox top =
                new HBox();

        top.setAlignment(
                Pos.CENTER_LEFT
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: 600;" +
                "-fx-text-fill: "
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
                        + ";" +
                "-fx-background-radius: 8;" +
                "-fx-text-fill: "
                        + color
                        + ";" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;"
        );

        top.getChildren().addAll(
                titleLabel,
                spacer,
                iconLabel
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Label subtitleLabel =
                new Label(subtitle);

        subtitleLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-text-fill: "
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
    // FILTERS
    // =========================================================

    private HBox createFilters(
            Stage stage
    ) {

        HBox container =
                new HBox(12);

        container.setAlignment(
                Pos.CENTER_LEFT
        );

        container.setPadding(
                new Insets(16)
        );

        applyCardStyle(container);

        filterSearchField =
                new TextField();

        filterSearchField.setPromptText(
                "Search patient or doctor..."
        );

        filterSearchField.setPrefWidth(
                260
        );

        filterSearchField.setPrefHeight(
                40
        );

        filterDatePicker =
                new DatePicker();

        filterDatePicker.setPromptText(
                "Select date"
        );

        filterDatePicker.setPrefWidth(
                160
        );

        filterDatePicker.setPrefHeight(
                40
        );

        filterDoctorCombo =
                new ComboBox<>();

        filterDoctorCombo.setPrefWidth(
                180
        );

        filterDoctorCombo.setPrefHeight(
                40
        );

        filterDoctorCombo.setValue(
                "All Doctors"
        );

        filterStatusCombo =
                new ComboBox<>();

        filterStatusCombo.getItems().addAll(
                "All Status",
                "PENDING",
                "PENDING_ASSIGNMENT",
                "CONFIRMED",
                "ACCEPTED",
                "REJECTED",
                "COMPLETED",
                "CANCELLED"
        );

        filterStatusCombo.setValue(
                "All Status"
        );

        filterStatusCombo.setPrefWidth(
                180
        );

        filterStatusCombo.setPrefHeight(
                40
        );

        filterSearchField.textProperty()
                .addListener(
                        (o, oldV, newV) ->
                                updateFilteredData()
                );

        filterDatePicker.valueProperty()
                .addListener(
                        (o, oldV, newV) ->
                                updateFilteredData()
                );

        filterDoctorCombo.valueProperty()
                .addListener(
                        (o, oldV, newV) ->
                                updateFilteredData()
                );

        filterStatusCombo.valueProperty()
                .addListener(
                        (o, oldV, newV) ->
                                updateFilteredData()
                );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button resetButton =
                new Button(
                        "☷  Reset Filters"
                );

        resetButton.setPrefHeight(
                40
        );

        resetButton.setOnAction(
                e -> resetFilters()
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
                        + ";" +
                "-fx-text-fill: "
                        + PRIMARY_BLUE
                        + ";" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        exportButton.setOnAction(
                e -> exportToCSV(stage)
        );

        container.getChildren().addAll(
                filterSearchField,
                filterDatePicker,
                filterDoctorCombo,
                filterStatusCombo,
                spacer,
                resetButton,
                exportButton
        );

        return container;
    }

    // =========================================================
    // DOCTOR FILTER
    // =========================================================

    private void updateDoctorFilter() {

        if (filterDoctorCombo == null) {
            return;
        }

        String current =
                filterDoctorCombo.getValue();

        filterDoctorCombo.getItems().clear();

        filterDoctorCombo.getItems().add(
                "All Doctors"
        );

        hospitalDoctors.stream()
                .map(HospitalDoctorDetails::getFullName)
                .filter(
                        name ->
                                name != null
                                &&
                                !name.trim().isEmpty()
                )
                .distinct()
                .sorted()
                .forEach(
                        name ->
                                filterDoctorCombo
                                        .getItems()
                                        .add(
                                                formatDoctorName(name)
                                        )
                );

        if (current != null &&
                filterDoctorCombo
                        .getItems()
                        .contains(current)) {

            filterDoctorCombo.setValue(
                    current
            );

        } else {

            filterDoctorCombo.setValue(
                    "All Doctors"
            );
        }
    }

    // =========================================================
    // RESET
    // =========================================================

    private void resetFilters() {

        filterSearchField.clear();

        filterDatePicker.setValue(
                null
        );

        filterDoctorCombo.setValue(
                "All Doctors"
        );

        filterStatusCombo.setValue(
                "All Status"
        );

        updateFilteredData();
    }

    // =========================================================
    // TABLE
    // =========================================================

    private VBox createAppointmentTable() {

        VBox table =
                new VBox(0);

        applyCardStyle(table);

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        header.setPadding(
                new Insets(20)
        );

        Label title =
                new Label(
                        "Appointments List"
                );

        title.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: 700;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        totalAppointmentsCountLabel =
                new Label(
                        "0 Appointments Displayed"
                );

        totalAppointmentsCountLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: 600;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        header.getChildren().addAll(
                title,
                spacer,
                totalAppointmentsCountLabel
        );

        tableRowsContainer =
                new VBox(0);

        table.getChildren().addAll(
                header,
                new Separator(),
                createTableHeader(),
                new Separator(),
                tableRowsContainer
        );

        return table;
    }

    // =========================================================
    // TABLE HEADER
    // =========================================================

    private HBox createTableHeader() {

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        header.setPadding(
                new Insets(
                        12,
                        20,
                        12,
                        20
                )
        );

        header.setStyle(
                "-fx-background-color: #F8FAFC;"
        );

        header.getChildren().addAll(
                createHeaderLabel("TIME", 100),
                createHeaderLabel("PATIENT", 230),
                createHeaderLabel("DEPARTMENT", 150),
                createHeaderLabel("DOCTOR", 180),
                createHeaderLabel("TYPE", 130),
                createHeaderLabel("STATUS", 150),
                createHeaderLabel("ACTIONS", 300)
        );

        return header;
    }

    private Label createHeaderLabel(
            String text,
            double width
    ) {

        Label label =
                new Label(text);

        label.setPrefWidth(width);

        label.setStyle(
                "-fx-font-size: 10px;" +
                "-fx-font-weight: 700;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        return label;
    }

    // =========================================================
    // APPOINTMENT ROW
    // =========================================================

    private HBox createAppointmentRow(
            Appointment appt
    ) {

        HBox row =
                new HBox();

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(
                        14,
                        20,
                        14,
                        20
                )
        );

        Label time =
                new Label(
                        safe(
                                appt.getAppointmentTime()
                        )
                );

        time.setPrefWidth(100);

        time.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: 700;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        HBox patientBox =
                new HBox(12);

        patientBox.setPrefWidth(
                230
        );

        patientBox.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle avatar =
                new Circle(16);

        avatar.setFill(
                Color.web(PRIMARY_LIGHT)
        );

        avatar.setStroke(
                Color.web(BORDER)
        );

        String patientName =
                safe(
                        appt.getPatientName()
                );

        Label initials =
                new Label(
                        getInitials(patientName)
                );

        initials.setStyle(
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: "
                        + PRIMARY_BLUE
                        + ";"
        );

        StackPane avatarPane =
                new StackPane(
                        avatar,
                        initials
                );

        VBox patientInfo =
                new VBox(2);

        Label patientNameLabel =
                new Label(
                        patientName
                );

        patientNameLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: 700;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Label patientId =
                new Label(
                        safe(
                                appt.getPatientUid()
                        )
                );

        patientId.setStyle(
                "-fx-font-size: 10px;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        patientInfo.getChildren().addAll(
                patientNameLabel,
                patientId
        );

        patientBox.getChildren().addAll(
                avatarPane,
                patientInfo
        );

        Label department =
                new Label(
                        safe(
                                appt.getSpecialty()
                        )
                );

        department.setPrefWidth(
                150
        );

        department.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        String doctorName =
                appt.getDoctorName();

        if (!isValidDoctorName(
                doctorName
        )) {

            doctorName =
                    "Not Assigned";
        }

        Label doctor =
                new Label(
                        doctorName
                );

        doctor.setPrefWidth(
                180
        );

        doctor.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: 500;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        Label type =
                new Label(
                        formatBookingType(
                                appt.getBookingType()
                        )
                );

        type.setPrefWidth(
                130
        );

        type.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        String status =
                safe(
                        appt.getStatus()
                );

        Label statusLabel =
                new Label(
                        "●  "
                                +
                        formatStatus(status)
                );

        statusLabel.setPadding(
                new Insets(
                        4,
                        10,
                        4,
                        10
                )
        );

        statusLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: 700;" +
                "-fx-text-fill: "
                        + getStatusColor(status)
                        + ";" +
                "-fx-background-color: "
                        + getStatusBgColor(status)
                        + ";" +
                "-fx-background-radius: 12;"
        );

        HBox statusBox =
                new HBox(
                        statusLabel
                );

        statusBox.setPrefWidth(
                150
        );

        statusBox.setAlignment(
                Pos.CENTER_LEFT
        );

        HBox actions =
                new HBox(6);

        actions.setPrefWidth(
                300
        );

        actions.setAlignment(
                Pos.CENTER_LEFT
        );

        // -----------------------------------------------------
        // VIEW
        // -----------------------------------------------------

        Button view =
                createSmallButton(
                        "View",
                        PRIMARY_BLUE,
                        PRIMARY_LIGHT
                );

        view.setOnAction(
                e ->
                        showAppointmentDetailsDialog(
                                appt
                        )
        );

        actions.getChildren().add(
                view
        );

        // -----------------------------------------------------
        // ASSIGN DOCTOR
        // -----------------------------------------------------

        if (
                "HOSPITAL".equalsIgnoreCase(
                        safe(
                                appt.getBookingType()
                        )
                )
                &&
                !isValidDoctorName(
                        appt.getDoctorName()
                )
                &&
                "PENDING_ASSIGNMENT"
                        .equalsIgnoreCase(
                                status
                        )
        ) {

            Button assign =
                    createSmallButton(
                            "Assign Doctor",
                            SUCCESS_GREEN,
                            SUCCESS_LIGHT
                    );

            assign.setOnAction(
                    e ->
                            openAssignDoctorDialog(
                                    (Stage)
                                            row.getScene()
                                                    .getWindow(),
                                    appt
                            )
            );

            actions.getChildren().add(
                    assign
            );
        }

        // -----------------------------------------------------
        // ACCEPT
        // -----------------------------------------------------

        if ("CONFIRMED".equalsIgnoreCase(
                status
        )) {

            Button accept =
                    createSmallButton(
                            "Accept",
                            SUCCESS_GREEN,
                            SUCCESS_LIGHT
                    );

            accept.setOnAction(
                    e ->
                            handleStatusChange(
                                    appt,
                                    "ACCEPTED",
                                    "Accept Appointment",
                                    "Appointment accepted successfully."
                            )
            );

            actions.getChildren().add(
                    accept
            );

            // -------------------------------------------------
            // REJECT
            // -------------------------------------------------

            Button reject =
                    createSmallButton(
                            "Reject",
                            ERROR_RED,
                            ERROR_LIGHT
                    );

            reject.setOnAction(
                    e ->
                            handleStatusChange(
                                    appt,
                                    "REJECTED",
                                    "Reject Appointment",
                                    "Appointment rejected successfully."
                            )
            );

            actions.getChildren().add(
                    reject
            );
        }

        // -----------------------------------------------------
        // COMPLETE
        // -----------------------------------------------------

        if ("ACCEPTED".equalsIgnoreCase(
                status
        )) {

            Button complete =
                    createSmallButton(
                            "Complete",
                            SUCCESS_GREEN,
                            SUCCESS_LIGHT
                    );

            complete.setOnAction(
                    e ->
                            handleStatusChange(
                                    appt,
                                    "COMPLETED",
                                    "Complete Appointment",
                                    "Appointment marked as completed."
                            )
            );

            actions.getChildren().add(
                    complete
            );
        }

        // -----------------------------------------------------
        // CANCEL
        // -----------------------------------------------------

        if (!"COMPLETED".equalsIgnoreCase(status)
                &&
                !"CANCELLED".equalsIgnoreCase(status)
                &&
                !"REJECTED".equalsIgnoreCase(status)) {

            Button cancel =
                    createSmallButton(
                            "Cancel",
                            ERROR_RED,
                            ERROR_LIGHT
                    );

            cancel.setOnAction(
                    e ->
                            handleCancelAppointment(
                                    appt
                            )
            );

            actions.getChildren().add(
                    cancel
            );
        }

        row.getChildren().addAll(
                time,
                patientBox,
                department,
                doctor,
                type,
                statusBox,
                actions
        );

        return row;
    }

    // =========================================================
    // ASSIGN DOCTOR
    // =========================================================

    private void openAssignDoctorDialog(
            Stage owner,
            Appointment appointment
    ) {

        Stage dialog =
                new Stage();

        dialog.initModality(
                Modality.APPLICATION_MODAL
        );

        dialog.initOwner(
                owner
        );

        dialog.setTitle(
                "Assign Doctor"
        );

        VBox root =
                new VBox(16);

        root.setPadding(
                new Insets(24)
        );

        root.setStyle(
                "-fx-background-color: "
                        + CARD_BG
                        + ";"
        );

        Label title =
                new Label(
                        "Assign Doctor"
                );

        title.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Label patient =
                new Label(
                        "Patient: "
                                +
                        safe(
                                appointment
                                        .getPatientName()
                        )
                );

        patient.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        ComboBox<HospitalDoctorDetails>
                doctorCombo =
                new ComboBox<>();

        doctorCombo.setPrefWidth(
                320
        );

        doctorCombo.setPrefHeight(
                40
        );

        /*
         * REAL hospital doctors.
         *
         * No fake doctor names.
         * No fake doctor UID.
         */
        doctorCombo.setItems(
                FXCollections.observableArrayList(
                        hospitalDoctors
                )
        );

        doctorCombo.setPromptText(
                "Select Doctor"
        );

        doctorCombo.setCellFactory(
                listView ->
                        new ListCell<>() {

                            @Override
                            protected void updateItem(
                                    HospitalDoctorDetails doctor,
                                    boolean empty
                            ) {

                                super.updateItem(
                                        doctor,
                                        empty
                                );

                                if (empty ||
                                        doctor == null) {

                                    setText(null);

                                } else {

                                    setText(
                                            formatDoctorName(
                                                    doctor
                                                            .getFullName()
                                            )
                                            +
                                            " ("
                                            +
                                            safe(
                                                    doctor
                                                            .getDepartmentId()
                                            )
                                            +
                                            ")"
                                    );
                                }
                            }
                        }
        );

        doctorCombo.setButtonCell(
                new ListCell<>() {

                    @Override
                    protected void updateItem(
                            HospitalDoctorDetails doctor,
                            boolean empty
                    ) {

                        super.updateItem(
                                doctor,
                                empty
                        );

                        if (empty ||
                                doctor == null) {

                            setText(
                                    "Select Doctor"
                            );

                        } else {

                            setText(
                                    formatDoctorName(
                                            doctor
                                                    .getFullName()
                                    )
                                    +
                                    " ("
                                    +
                                    safe(
                                            doctor
                                                    .getDepartmentId()
                                    )
                                    +
                                    ")"
                            );
                        }
                    }
                }
        );

        Button assignButton =
                new Button(
                        "Assign Doctor"
                );

        assignButton.setStyle(
                "-fx-background-color: "
                        + SUCCESS_GREEN
                        + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 16;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;"
        );

        assignButton.setOnAction(
                e -> {

                    HospitalDoctorDetails
                            selectedDoctor =
                            doctorCombo.getValue();

                    if (selectedDoctor == null) {

                        showAlert(
                                Alert.AlertType.ERROR,
                                "Validation Error",
                                "Please select a doctor."
                        );

                        return;
                    }

                    String doctorUid =
                            selectedDoctor
                                    .getDoctorId();

                    String doctorName =
                            formatDoctorName(
                                    selectedDoctor
                                            .getFullName()
                            );

                    if (doctorUid == null ||
                            doctorUid.trim().isEmpty()) {

                        showAlert(
                                Alert.AlertType.ERROR,
                                "Assignment Failed",
                                "Selected doctor does not have a valid UID."
                        );

                        return;
                    }

                    try {

                        appointmentController
                                .assignDoctor(
                                        appointment
                                                .getAppointmentId(),
                                        doctorUid,
                                        doctorName
                                );

                        dialog.close();

                        loadAppointments();

                        showAlert(
                                Alert.AlertType.INFORMATION,
                                "Doctor Assigned",
                                doctorName
                                        +
                                " has been assigned successfully."
                        );

                    } catch (Exception ex) {

                        showAlert(
                                Alert.AlertType.ERROR,
                                "Assignment Failed",
                                getErrorMessage(ex)
                        );
                    }
                }
        );

        root.getChildren().addAll(
                title,
                patient,
                new Label(
                        "Select Doctor:"
                ),
                doctorCombo,
                assignButton
        );

        dialog.setScene(
                new Scene(
                        root,
                        420,
                        330
                )
        );

        dialog.showAndWait();
    }

    // =========================================================
    // ACCEPT / REJECT / COMPLETE
    // =========================================================

    private void handleStatusChange(
            Appointment appointment,
            String newStatus,
            String confirmationTitle,
            String successMessage
    ) {

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                confirmationTitle
        );

        confirmation.setHeaderText(
                "Change appointment status?"
        );

        confirmation.setContentText(
                "Patient: "
                        +
                safe(
                        appointment.getPatientName()
                )
                +
                "\nDoctor: "
                        +
                (
                        isValidDoctorName(
                                appointment
                                        .getDoctorName()
                        )
                                ?
                        appointment.getDoctorName()
                                :
                        "Not Assigned"
                )
        );

        Optional<ButtonType> result =
                confirmation.showAndWait();

        if (result.isEmpty() ||
                result.get() != ButtonType.OK) {

            return;
        }

        try {

            appointmentController
                    .updateAppointmentStatus(
                            appointment
                                    .getAppointmentId(),
                            newStatus
                    );

            loadAppointments();

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Success",
                    successMessage
            );

        } catch (Exception ex) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Status Update Failed",
                    getErrorMessage(ex)
            );
        }
    }

    // =========================================================
    // CANCEL
    // =========================================================

    private void handleCancelAppointment(
            Appointment appointment
    ) {

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Cancel Appointment"
        );

        confirmation.setHeaderText(
                "Are you sure you want to cancel this appointment?"
        );

        confirmation.setContentText(
                "Patient: "
                        +
                safe(
                        appointment.getPatientName()
                )
                +
                "\nDoctor: "
                        +
                (
                        isValidDoctorName(
                                appointment
                                        .getDoctorName()
                        )
                                ?
                        appointment.getDoctorName()
                                :
                        "Not Assigned"
                )
        );

        Optional<ButtonType> result =
                confirmation.showAndWait();

        if (result.isEmpty() ||
                result.get() != ButtonType.OK) {

            return;
        }

        try {

            appointmentController
                    .cancelAppointment(
                            appointment
                                    .getAppointmentId()
                    );

            loadAppointments();

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Cancelled",
                    "Appointment cancelled successfully."
            );

        } catch (Exception ex) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Cancellation Failed",
                    getErrorMessage(ex)
            );
        }
    }

    // =========================================================
    // VIEW DETAILS
    // =========================================================

    private void showAppointmentDetailsDialog(
            Appointment appointment
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(
                "Appointment Details"
        );

        alert.setHeaderText(
                safe(
                        appointment
                                .getPatientName()
                )
        );

        alert.setContentText(
                "Appointment ID: "
                        +
                safe(
                        appointment
                                .getAppointmentId()
                )
                +
                "\n\nPatient UID: "
                        +
                safe(
                        appointment
                                .getPatientUid()
                )
                +
                "\n\nBooking Type: "
                        +
                formatBookingType(
                        appointment
                                .getBookingType()
                )
                +
                "\n\nDoctor: "
                        +
                (
                        isValidDoctorName(
                                appointment
                                        .getDoctorName()
                        )
                                ?
                        appointment.getDoctorName()
                                :
                        "Not Assigned"
                )
                +
                "\nDoctor UID: "
                        +
                safe(
                        appointment
                                .getDoctorUid()
                )
                +
                "\n\nHospital: "
                        +
                safe(
                        appointment
                                .getHospitalName()
                )
                +
                "\n\nSpecialty: "
                        +
                safe(
                        appointment
                                .getSpecialty()
                )
                +
                "\n\nDate: "
                        +
                safe(
                        appointment
                                .getAppointmentDate()
                )
                +
                "\nTime: "
                        +
                safe(
                        appointment
                                .getAppointmentTime()
                )
                +
                "\n\nReason: "
                        +
                safe(
                        appointment
                                .getReason()
                )
                +
                "\n\nStatus: "
                        +
                formatStatus(
                        appointment
                                .getStatus()
                )
        );

        alert.showAndWait();
    }

    // =========================================================
    // FILTER
    // =========================================================

    private void updateFilteredData() {

        String search =
                filterSearchField != null
                        ?
                safe(
                        filterSearchField
                                .getText()
                )
                        .toLowerCase()
                        .trim()
                        :
                "";

        LocalDate selectedDate =
                filterDatePicker != null
                        ?
                filterDatePicker.getValue()
                        :
                null;

        String selectedDoctor =
                filterDoctorCombo != null
                        ?
                filterDoctorCombo.getValue()
                        :
                "All Doctors";

        String selectedStatus =
                filterStatusCombo != null
                        ?
                filterStatusCombo.getValue()
                        :
                "All Status";

        filteredAppointmentList =
                masterAppointmentList
                        .stream()
                        .filter(
                                appointment -> {

                                    boolean searchMatch =
                                            search.isEmpty()
                                            ||
                                            safe(
                                                    appointment
                                                            .getPatientName()
                                            )
                                                    .toLowerCase()
                                                    .contains(search)
                                            ||
                                            safe(
                                                    appointment
                                                            .getDoctorName()
                                            )
                                                    .toLowerCase()
                                                    .contains(search)
                                            ||
                                            safe(
                                                    appointment
                                                            .getPatientUid()
                                            )
                                                    .toLowerCase()
                                                    .contains(search);

                                    boolean dateMatch =
                                            selectedDate == null
                                            ||
                                            selectedDate
                                                    .toString()
                                                    .equals(
                                                            safe(
                                                                    appointment
                                                                            .getAppointmentDate()
                                                            )
                                                    );

                                    String appointmentDoctor =
                                            formatDoctorName(
                                                    appointment
                                                            .getDoctorName()
                                            );

                                    boolean doctorMatch =
                                            selectedDoctor == null
                                            ||
                                            "All Doctors"
                                                    .equals(
                                                            selectedDoctor
                                                    )
                                            ||
                                            selectedDoctor
                                                    .equals(
                                                            appointmentDoctor
                                                    );

                                    boolean statusMatch =
                                            selectedStatus == null
                                            ||
                                            "All Status"
                                                    .equals(
                                                            selectedStatus
                                                    )
                                            ||
                                            selectedStatus
                                                    .equalsIgnoreCase(
                                                            safe(
                                                                    appointment
                                                                            .getStatus()
                                                            )
                                                    );

                                    return searchMatch
                                            &&
                                            dateMatch
                                            &&
                                            doctorMatch
                                            &&
                                            statusMatch;
                                }
                        )
                        .toList();

        renderTableRows();

        recalculateStatistics();
    }

    // =========================================================
    // RENDER ROWS
    // =========================================================

    private void renderTableRows() {

        if (tableRowsContainer == null) {
            return;
        }

        tableRowsContainer
                .getChildren()
                .clear();

        if (filteredAppointmentList.isEmpty()) {

            VBox empty =
                    new VBox(10);

            empty.setAlignment(
                    Pos.CENTER
            );

            empty.setPadding(
                    new Insets(40)
            );

            Label message =
                    new Label(
                            "No matching appointments found."
                    );

            message.setStyle(
                    "-fx-font-size: 14px;" +
                    "-fx-text-fill: "
                            + SECONDARY_TEXT
                            + ";"
            );

            empty.getChildren()
                    .add(message);

            tableRowsContainer
                    .getChildren()
                    .add(empty);

            return;
        }

        for (int i = 0;
             i < filteredAppointmentList.size();
             i++) {

            tableRowsContainer
                    .getChildren()
                    .add(
                            createAppointmentRow(
                                    filteredAppointmentList
                                            .get(i)
                            )
                    );

            if (i <
                    filteredAppointmentList.size() - 1) {

                tableRowsContainer
                        .getChildren()
                        .add(
                                new Separator()
                        );
            }
        }

        if (totalAppointmentsCountLabel != null) {

            totalAppointmentsCountLabel
                    .setText(
                            filteredAppointmentList
                                    .size()
                                    +
                            " Appointments Displayed"
                    );
        }
    }

    // =========================================================
    // STATISTICS
    // =========================================================

    private void recalculateStatistics() {

        LocalDate today =
                LocalDate.now();

        long todayCount =
                masterAppointmentList
                        .stream()
                        .filter(
                                a ->
                                        today.toString()
                                                .equals(
                                                        safe(
                                                                a.getAppointmentDate()
                                                        )
                                                )
                        )
                        .count();

        long completed =
                masterAppointmentList
                        .stream()
                        .filter(
                                a ->
                                        "COMPLETED"
                                                .equalsIgnoreCase(
                                                        safe(
                                                                a.getStatus()
                                                        )
                                                )
                        )
                        .count();

        long upcoming =
                masterAppointmentList
                        .stream()
                        .filter(
                                a -> {

                                    String status =
                                            safe(
                                                    a.getStatus()
                                            );

                                    return
                                            "PENDING"
                                                    .equalsIgnoreCase(status)
                                            ||
                                            "PENDING_ASSIGNMENT"
                                                    .equalsIgnoreCase(status)
                                            ||
                                            "CONFIRMED"
                                                    .equalsIgnoreCase(status)
                                            ||
                                            "ACCEPTED"
                                                    .equalsIgnoreCase(status);
                                }
                        )
                        .count();

        long cancelled =
                masterAppointmentList
                        .stream()
                        .filter(
                                a ->
                                        "CANCELLED"
                                                .equalsIgnoreCase(
                                                        safe(
                                                                a.getStatus()
                                                        )
                                                )
                        )
                        .count();

        if (todayCountVal != null) {

            todayCountVal.setText(
                    String.format(
                            "%02d",
                            todayCount
                    )
            );
        }

        if (completedCountVal != null) {

            completedCountVal.setText(
                    String.format(
                            "%02d",
                            completed
                    )
            );
        }

        if (upcomingCountVal != null) {

            upcomingCountVal.setText(
                    String.format(
                            "%02d",
                            upcoming
                    )
            );
        }

        if (cancelledCountVal != null) {

            cancelledCountVal.setText(
                    String.format(
                            "%02d",
                            cancelled
                    )
            );
        }
    }

    // =========================================================
    // CSV
    // =========================================================

    private void exportToCSV(
            Stage stage
    ) {

        FileChooser chooser =
                new FileChooser();

        chooser.setTitle(
                "Save Appointments Export"
        );

        chooser.setInitialFileName(
                "appointments.csv"
        );

        chooser.getExtensionFilters()
                .add(
                        new FileChooser.ExtensionFilter(
                                "CSV Files",
                                "*.csv"
                        )
                );

        File file =
                chooser.showSaveDialog(stage);

        if (file == null) {
            return;
        }

        try (
                PrintWriter writer =
                        new PrintWriter(file)
        ) {

            writer.println(
                    "Appointment ID,Date,Time,Patient UID,Patient Name,Hospital,Doctor UID,Doctor,Specialty,Booking Type,Reason,Status"
            );

            for (Appointment appointment :
                    filteredAppointmentList) {

                writer.printf(
                        "\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",

                        csv(
                                appointment
                                        .getAppointmentId()
                        ),

                        csv(
                                appointment
                                        .getAppointmentDate()
                        ),

                        csv(
                                appointment
                                        .getAppointmentTime()
                        ),

                        csv(
                                appointment
                                        .getPatientUid()
                        ),

                        csv(
                                appointment
                                        .getPatientName()
                        ),

                        csv(
                                appointment
                                        .getHospitalName()
                        ),

                        csv(
                                appointment
                                        .getDoctorUid()
                        ),

                        csv(
                                appointment
                                        .getDoctorName()
                        ),

                        csv(
                                appointment
                                        .getSpecialty()
                        ),

                        csv(
                                appointment
                                        .getBookingType()
                        ),

                        csv(
                                appointment
                                        .getReason()
                        ),

                        csv(
                                appointment
                                        .getStatus()
                        )
                );
            }

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Export Successful",
                    "Appointment data exported successfully."
            );

        } catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Export Failed",
                    getErrorMessage(e)
            );
        }
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private String csv(
            String value
    ) {

        return value == null
                ? ""
                :
                value.replace(
                        "\"",
                        "\"\""
                );
    }

    private String safe(
            String value
    ) {

        return value == null
                ? ""
                :
                value;
    }

    private String formatDoctorName(
            String name
    ) {

        if (!isValidDoctorName(name)) {
            return "Not Assigned";
        }

        String clean =
                name.trim();

        if (clean.startsWith("Dr.")) {
            return clean;
        }

        return "Dr. " + clean;
    }

    private boolean isValidDoctorName(
            String name
    ) {

        return name != null
                &&
                !name.trim().isEmpty()
                &&
                !"N/A".equalsIgnoreCase(
                        name.trim()
                )
                &&
                !"null".equalsIgnoreCase(
                        name.trim()
                )
                &&
                !"Not Assigned"
                        .equalsIgnoreCase(
                                name.trim()
                        );
    }

    private String formatBookingType(
            String type
    ) {

        if (type == null ||
                type.trim().isEmpty()) {

            return "-";
        }

        if ("HOSPITAL".equalsIgnoreCase(type)) {
            return "Hospital";
        }

        if ("DOCTOR".equalsIgnoreCase(type)) {
            return "Doctor";
        }

        return type;
    }

    private String formatStatus(
            String status
    ) {

        if (status == null ||
                status.trim().isEmpty()) {

            return "-";
        }

        return status.replace(
                "_",
                " "
        );
    }

    private String getInitials(
            String name
    ) {

        if (name == null ||
                name.trim().isEmpty()) {

            return "P";
        }

        String[] parts =
                name.trim()
                        .split("\\s+");

        if (parts.length == 1) {

            return parts[0]
                    .substring(0, 1)
                    .toUpperCase();
        }

        return (
                parts[0]
                        .substring(0, 1)
                        +
                parts[parts.length - 1]
                        .substring(0, 1)
        ).toUpperCase();
    }

    private String getStatusColor(
            String status
    ) {

        if (status == null) {
            return DARK_TEXT;
        }

        switch (
                status.toUpperCase()
        ) {

            case "COMPLETED":
            case "ACCEPTED":
                return SUCCESS_GREEN;

            case "PENDING":
            case "PENDING_ASSIGNMENT":
                return WARNING_ORANGE;

            case "CONFIRMED":
                return PRIMARY_BLUE;

            case "REJECTED":
            case "CANCELLED":
                return ERROR_RED;

            default:
                return DARK_TEXT;
        }
    }

    private String getStatusBgColor(
            String status
    ) {

        if (status == null) {
            return LIGHT_BACKGROUND;
        }

        switch (
                status.toUpperCase()
        ) {

            case "COMPLETED":
            case "ACCEPTED":
                return SUCCESS_LIGHT;

            case "PENDING":
            case "PENDING_ASSIGNMENT":
                return WARNING_LIGHT;

            case "CONFIRMED":
                return PRIMARY_LIGHT;

            case "REJECTED":
            case "CANCELLED":
                return ERROR_LIGHT;

            default:
                return LIGHT_BACKGROUND;
        }
    }

    private Button createSmallButton(
            String text,
            String color,
            String bgColor
    ) {

        Button button =
                new Button(text);

        button.setPrefHeight(30);

        button.setPadding(
                new Insets(
                        0,
                        8,
                        0,
                        8
                )
        );

        String style =
                "-fx-background-color: "
                        + bgColor
                        + ";" +
                "-fx-text-fill: "
                        + color
                        + ";" +
                "-fx-background-radius: 6;" +
                "-fx-font-size: 10px;" +
                "-fx-font-weight: 700;" +
                "-fx-cursor: hand;";

        button.setStyle(style);

        return button;
    }

    private void applyCardStyle(
            Pane pane
    ) {

        pane.setStyle(
                "-fx-background-color: "
                        + CARD_BG
                        + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: "
                        + BORDER
                        + ";" +
                "-fx-border-radius: 12;"
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

        shadow.setRadius(10);

        shadow.setOffsetY(3);

        pane.setEffect(shadow);
    }

    private String getErrorMessage(
            Exception e
    ) {

        if (e == null) {
            return "Unknown error.";
        }

        if (e.getMessage() != null &&
                !e.getMessage()
                        .trim()
                        .isEmpty()) {

            return e.getMessage();
        }

        if (e.getCause() != null &&
                e.getCause().getMessage() != null) {

            return e.getCause()
                    .getMessage();
        }

        return "An unexpected error occurred.";
    }

    private void showAlert(
            Alert.AlertType type,
            String title,
            String message
    ) {

        Alert alert =
                new Alert(type);

        alert.setTitle(title);

        alert.setHeaderText(null);

        alert.setContentText(message);

        alert.showAndWait();
    }
}