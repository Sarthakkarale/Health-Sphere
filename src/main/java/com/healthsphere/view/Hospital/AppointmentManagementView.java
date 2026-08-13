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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class AppointmentManagementView {

    // =========================================================
    // COLOR PALETTE (Clean Light Theme with Dark Sidebar)
    // =========================================================

    private static final String PRIMARY_BLUE = "#1E62D0";
    private static final String PRIMARY_LIGHT = "#EFF5FF";
    private static final String DARK_TEXT = "#0F172A";
    private static final String SECONDARY_TEXT = "#64748B";
    private static final String LIGHT_BACKGROUND = "#F8FAFC";
    private static final String CARD_BG = "#FFFFFF";
    private static final String BORDER = "#E2E8F0";

    // DARK SIDEBAR THEME
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
    // STATE & DATA MANAGEMENT
    // =========================================================

    private final ObservableList<Appointment> masterAppointmentList = FXCollections.observableArrayList();
    private FilteredList<Appointment> filteredAppointmentList;

    // Dynamic UI Component References for updates
    private VBox tableRowsContainer;
    private Label totalAppointmentsCountLabel;
    private Label todayCountVal;
    private Label completedCountVal;
    private Label upcomingCountVal;
    private Label cancelledCountVal;

    // Filter Controls
    private TextField filterSearchField;
    private DatePicker filterDatePicker;
    private ComboBox<String> filterDoctorCombo;
    private ComboBox<String> filterStatusCombo;

    public AppointmentManagementView() {
        seedInitialData();
    }

    // =========================================================
    // CREATE SCENE
    // =========================================================

    public Scene createScene(Stage stage) {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + LIGHT_BACKGROUND + ";");

        root.setLeft(createSidebar(stage));
        root.setTop(createTopBar());

        // Wrap main content in a scroll pane for smooth responsiveness
        ScrollPane scrollPane = new ScrollPane(createMainContent(stage));
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");

        root.setCenter(scrollPane);

        // Initial table load & stat update
        updateFilteredData();

        return new Scene(root, stage.getWidth(), stage.getHeight());
    }

    // =========================================================
    // SIDEBAR (DARK THEME)
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
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        logoBox.getChildren().addAll(logo, subtitle);
        sidebar.getChildren().add(logoBox);

        // NAVIGATION BUTTONS
        Button dashboardButton = createNavigationButton("▦", "Dashboard", false);
        Button doctorButton = createNavigationButton("♙", "Doctors", false);
        Button departmentButton = createNavigationButton("✚", "Departments", false);
        Button bedButton = createNavigationButton("▥", "Beds", false);
        Button appointmentButton = createNavigationButton("▣", "Appointments", true);
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

        // DIRECT NAVIGATION HANDLERS
        dashboardButton.setOnAction(event -> navigateSafely(stage, () -> new HospitalDashboardView().createScene(stage)));
        doctorButton.setOnAction(event -> navigateSafely(stage, () -> new DoctorManagementView().createScene(stage)));
        departmentButton.setOnAction(event -> navigateSafely(stage, () -> new DepartmentManagementView().createScene(stage)));
        bedButton.setOnAction(event -> navigateSafely(stage, () -> new BedManagementView().createScene(stage)));
        analyticsButton.setOnAction(event -> navigateSafely(stage, () -> new HospitalAnalyticsView().createScene(stage)));
        settingsButton.setOnAction(event -> navigateSafely(stage, () -> new HospitalProfileSettingsView().createScene(stage)));

        // SPACER
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);

        // FOOTER BUTTONS
        Button helpButton = createNavigationButton("?", "Help Center", false);
        Button logoutButton = createNavigationButton("↪", "Logout", false);

        helpButton.setOnAction(e -> showAlert(Alert.AlertType.INFORMATION, "Help Center", "Support contact: support@healthsphere.com"));
        logoutButton.setOnAction(e -> showAlert(Alert.AlertType.INFORMATION, "Logout", "Logged out successfully."));

        sidebar.getChildren().addAll(helpButton, logoutButton);

        return sidebar;
    }

    private void navigateSafely(Stage stage, SceneSupplier supplier) {
        try {
            stage.setScene(supplier.get());
        } catch (NoClassDefFoundError | Exception ex) {
            showAlert(Alert.AlertType.WARNING, "Navigation", "Destination view is currently unavailable.");
        }
    }

    @FunctionalInterface
    private interface SceneSupplier {
        Scene get() throws Exception;
    }

    // =========================================================
    // NAVIGATION BUTTON (DARK THEME)
    // =========================================================

    private Button createNavigationButton(String icon, String text, boolean selected) {

        Button button = new Button();

        Label iconLabel = new Label(icon);
        iconLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-text-fill: " + (selected ? "#FFFFFF" : SIDEBAR_TEXT) + ";"
        );

        Label textLabel = new Label(text);
        textLabel.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: " + (selected ? "bold" : "500") + ";" +
                "-fx-text-fill: " + (selected ? "#FFFFFF" : SIDEBAR_TEXT) + ";"
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

            button.setOnMouseEntered(e -> {
                button.setStyle(baseStyle + "-fx-background-color: " + SIDEBAR_HOVER_BG + ";");
                iconLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + SIDEBAR_TEXT_HOVER + ";");
                textLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 500; -fx-text-fill: " + SIDEBAR_TEXT_HOVER + ";");
            });
            button.setOnMouseExited(e -> {
                button.setStyle(baseStyle + "-fx-background-color: transparent;");
                iconLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + SIDEBAR_TEXT + ";");
                textLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 500; -fx-text-fill: " + SIDEBAR_TEXT + ";");
            });
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
        topSearchField.setPromptText("Search patients, doctors...");
        topSearchField.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-prompt-text-fill: #94A3B8;" +
                "-fx-font-size: 13px;" +
                "-fx-text-inner-color: " + DARK_TEXT + ";"
        );
        HBox.setHgrow(topSearchField, Priority.ALWAYS);

        topSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (filterSearchField != null) {
                filterSearchField.setText(newVal);
            }
        });

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

    private VBox createMainContent(Stage stage) {

        VBox content = new VBox(24);
        content.setPadding(new Insets(28));
        content.setStyle("-fx-background-color: " + LIGHT_BACKGROUND + ";");

        content.getChildren().addAll(
                createPageHeader(stage),
                createStatistics(),
                createFilters(stage),
                createAppointmentTable()
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

        Label title = new Label("Appointment Management");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 800; -fx-text-fill: " + DARK_TEXT + ";");

        Label subtitle = new Label("Manage patient appointments, track schedules, and handle bookings.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button newAppointment = new Button("＋  New Appointment");
        newAppointment.setPrefHeight(42);
        newAppointment.setPadding(new Insets(0, 20, 0, 20));

        String actionBtnStyle =
                "-fx-background-color: " + PRIMARY_BLUE + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;";

        newAppointment.setStyle(actionBtnStyle);

        newAppointment.setOnMouseEntered(e -> newAppointment.setStyle(
                actionBtnStyle + "-fx-background-color: #1550B0;"
        ));
        newAppointment.setOnMouseExited(e -> newAppointment.setStyle(actionBtnStyle));

        newAppointment.setOnAction(e -> openAppointmentFormDialog(stage, null));

        header.getChildren().addAll(titleBox, spacer, newAppointment);

        return header;
    }

    // =========================================================
    // STATISTICS
    // =========================================================

    private HBox createStatistics() {

        HBox statistics = new HBox(18);

        VBox todayCard = createStatisticCard("Today's Appointments", "0", "Scheduled for today", "▣", PRIMARY_BLUE, PRIMARY_LIGHT);
        VBox completedCard = createStatisticCard("Completed", "0", "Appointments finished", "✓", SUCCESS_GREEN, SUCCESS_LIGHT);
        VBox upcomingCard = createStatisticCard("Upcoming", "0", "Waiting for consultation", "◷", PURPLE, PURPLE_LIGHT);
        VBox cancelledCard = createStatisticCard("Cancelled", "0", "Cancelled appointments", "×", ERROR_RED, ERROR_LIGHT);

        todayCountVal = (Label) todayCard.getChildren().get(1);
        completedCountVal = (Label) completedCard.getChildren().get(1);
        upcomingCountVal = (Label) upcomingCard.getChildren().get(1);
        cancelledCountVal = (Label) cancelledCard.getChildren().get(1);

        statistics.getChildren().addAll(todayCard, completedCard, upcomingCard, cancelledCard);

        for (javafx.scene.Node node : statistics.getChildren()) {
            HBox.setHgrow(node, Priority.ALWAYS);
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

        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setPrefHeight(125);

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
    // FILTERS
    // =========================================================

    private HBox createFilters(Stage stage) {

        HBox container = new HBox(12);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(16));

        applyCardStyle(container);

        filterSearchField = new TextField();
        filterSearchField.setPromptText("Search patient or doctor...");
        filterSearchField.setPrefWidth(260);
        filterSearchField.setPrefHeight(40);
        filterSearchField.setStyle(
                "-fx-background-color: " + LIGHT_BACKGROUND + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 0 14;" +
                "-fx-font-size: 12px;"
        );

        filterDatePicker = new DatePicker();
        filterDatePicker.setPromptText("Select date");
        filterDatePicker.setPrefWidth(160);
        filterDatePicker.setPrefHeight(40);
        filterDatePicker.setStyle("-fx-font-size: 12px;");

        filterDoctorCombo = new ComboBox<>();
        filterDoctorCombo.getItems().addAll(
                "All Doctors",
                "Dr. Ananya Sharma",
                "Dr. Rahul Patil",
                "Dr. Priya Mehta",
                "Dr. Amit Joshi",
                "Dr. Neha Kulkarni"
        );
        filterDoctorCombo.setValue("All Doctors");
        filterDoctorCombo.setPrefWidth(160);
        filterDoctorCombo.setPrefHeight(40);
        filterDoctorCombo.setStyle("-fx-font-size: 12px;");

        filterStatusCombo = new ComboBox<>();
        filterStatusCombo.getItems().addAll(
                "All Status",
                "Completed",
                "Waiting",
                "Upcoming",
                "Cancelled"
        );
        filterStatusCombo.setValue("All Status");
        filterStatusCombo.setPrefWidth(140);
        filterStatusCombo.setPrefHeight(40);
        filterStatusCombo.setStyle("-fx-font-size: 12px;");

        filterSearchField.textProperty().addListener((o, oldV, newV) -> updateFilteredData());
        filterDatePicker.valueProperty().addListener((o, oldV, newV) -> updateFilteredData());
        filterDoctorCombo.valueProperty().addListener((o, oldV, newV) -> updateFilteredData());
        filterStatusCombo.valueProperty().addListener((o, oldV, newV) -> updateFilteredData());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button resetFilterButton = new Button("☷  Reset Filters");
        resetFilterButton.setPrefHeight(40);
        resetFilterButton.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-text-fill: " + DARK_TEXT + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: 600;" +
                "-fx-cursor: hand;"
        );
        resetFilterButton.setOnAction(e -> resetFilters());

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
        exportButton.setOnAction(e -> exportToCSV(stage));

        container.getChildren().addAll(
                filterSearchField,
                filterDatePicker,
                filterDoctorCombo,
                filterStatusCombo,
                spacer,
                resetFilterButton,
                exportButton
        );

        return container;
    }

    private void resetFilters() {
        filterSearchField.clear();
        filterDatePicker.setValue(null);
        filterDoctorCombo.setValue("All Doctors");
        filterStatusCombo.setValue("All Status");
        updateFilteredData();
    }

    // =========================================================
    // APPOINTMENT TABLE
    // =========================================================

    private VBox createAppointmentTable() {

        VBox table = new VBox(0);
        applyCardStyle(table);

        HBox tableHeader = new HBox();
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setPadding(new Insets(20));

        Label title = new Label("Appointments List");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: 700; -fx-text-fill: " + DARK_TEXT + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        totalAppointmentsCountLabel = new Label("0 Total Appointments");
        totalAppointmentsCountLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: " + SECONDARY_TEXT + ";");

        tableHeader.getChildren().addAll(title, spacer, totalAppointmentsCountLabel);

        tableRowsContainer = new VBox(0);

        table.getChildren().addAll(
                tableHeader,
                new Separator(),
                createTableHeader(),
                new Separator(),
                tableRowsContainer
        );

        return table;
    }

    private Separator createRowSeparator() {
        Separator sep = new Separator();
        sep.setStyle("-fx-opacity: 0.6;");
        return sep;
    }

    // =========================================================
    // TABLE HEADER
    // =========================================================

    private HBox createTableHeader() {

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(12, 20, 12, 20));
        header.setStyle("-fx-background-color: #F8FAFC;");

        header.getChildren().addAll(
                createHeaderLabel("TIME", 100),
                createHeaderLabel("PATIENT", 230),
                createHeaderLabel("DEPARTMENT", 150),
                createHeaderLabel("DOCTOR", 180),
                createHeaderLabel("TYPE", 130),
                createHeaderLabel("STATUS", 120),
                createHeaderLabel("ACTIONS", 180)
        );

        return header;
    }

    private Label createHeaderLabel(String text, double width) {
        Label label = new Label(text);
        label.setPrefWidth(width);
        label.setStyle(
                "-fx-font-size: 10px;" +
                "-fx-font-weight: 700;" +
                "-fx-letter-spacing: 0.5px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );
        return label;
    }

    // =========================================================
    // APPOINTMENT ROW RENDERER
    // =========================================================

    private HBox createAppointmentRow(Appointment appt) {

        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14, 20, 14, 20));

        Label timeLabel = new Label(appt.getTime());
        timeLabel.setPrefWidth(100);
        timeLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: " + DARK_TEXT + ";");

        HBox patientBox = new HBox(12);
        patientBox.setPrefWidth(230);
        patientBox.setAlignment(Pos.CENTER_LEFT);

        Circle avatar = new Circle(16);
        avatar.setFill(Color.web(PRIMARY_LIGHT));
        avatar.setStroke(Color.web(BORDER));

        Label initialsLabel = new Label(getInitials(appt.getPatientName()));
        initialsLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        StackPane avatarPane = new StackPane(avatar, initialsLabel);

        VBox patientInfo = new VBox(2);
        Label patientName = new Label(appt.getPatientName());
        patientName.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: " + DARK_TEXT + ";");

        Label patientId = new Label(appt.getPatientId());
        patientId.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        patientInfo.getChildren().addAll(patientName, patientId);
        patientBox.getChildren().addAll(avatarPane, patientInfo);

        Label departmentLabel = new Label(appt.getDepartment());
        departmentLabel.setPrefWidth(150);
        departmentLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + DARK_TEXT + ";");

        Label doctorLabel = new Label(appt.getDoctorName());
        doctorLabel.setPrefWidth(180);
        doctorLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 500; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label typeLabel = new Label(appt.getType());
        typeLabel.setPrefWidth(130);
        typeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + DARK_TEXT + ";");

        String statusColor = getStatusColor(appt.getStatus());
        String statusBgColor = getStatusBgColor(appt.getStatus());

        Label statusLabel = new Label("●  " + appt.getStatus());
        statusLabel.setPadding(new Insets(4, 10, 4, 10));
        statusLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: 700;" +
                "-fx-text-fill: " + statusColor + ";" +
                "-fx-background-color: " + statusBgColor + ";" +
                "-fx-background-radius: 12;"
        );

        HBox statusBox = new HBox(statusLabel);
        statusBox.setPrefWidth(120);
        statusBox.setAlignment(Pos.CENTER_LEFT);

        HBox actions = new HBox(8);
        actions.setPrefWidth(180);
        actions.setAlignment(Pos.CENTER_LEFT);

        Button view = createSmallButton("View", PRIMARY_BLUE, PRIMARY_LIGHT);
        Button reschedule = createSmallButton("Reschedule", PURPLE, PURPLE_LIGHT);
        Button cancel = createSmallButton("Cancel", ERROR_RED, ERROR_LIGHT);

        view.setOnAction(e -> showAppointmentDetailsDialog(appt));
        reschedule.setOnAction(e -> openAppointmentFormDialog((Stage) row.getScene().getWindow(), appt));
        cancel.setOnAction(e -> handleCancelAppointment(appt));

        actions.getChildren().addAll(view, reschedule, cancel);

        row.getChildren().addAll(
                timeLabel,
                patientBox,
                departmentLabel,
                doctorLabel,
                typeLabel,
                statusBox,
                actions
        );

        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #F8FAFC;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-background-color: transparent;"));

        return row;
    }

    // =========================================================
    // DYNAMIC FILTERING & STAT RECALCULATION
    // =========================================================

    private void updateFilteredData() {
        String searchText = filterSearchField != null && filterSearchField.getText() != null ? filterSearchField.getText().toLowerCase().trim() : "";
        LocalDate selectedDate = filterDatePicker != null ? filterDatePicker.getValue() : null;
        String selectedDoctor = filterDoctorCombo != null ? filterDoctorCombo.getValue() : "All Doctors";
        String selectedStatus = filterStatusCombo != null ? filterStatusCombo.getValue() : "All Status";

        filteredAppointmentList = masterAppointmentList.filtered(appt -> {
            boolean matchesSearch = searchText.isEmpty() ||
                    appt.getPatientName().toLowerCase().contains(searchText) ||
                    appt.getDoctorName().toLowerCase().contains(searchText) ||
                    appt.getPatientId().toLowerCase().contains(searchText);

            boolean matchesDate = selectedDate == null || appt.getDate().equals(selectedDate);
            boolean matchesDoctor = "All Doctors".equals(selectedDoctor) || appt.getDoctorName().equals(selectedDoctor);
            boolean matchesStatus = "All Status".equals(selectedStatus) || appt.getStatus().equalsIgnoreCase(selectedStatus);

            return matchesSearch && matchesDate && matchesDoctor && matchesStatus;
        });

        renderTableRows();
        recalculateStatistics();
    }

    private void renderTableRows() {
        if (tableRowsContainer == null) return;
        tableRowsContainer.getChildren().clear();

        if (filteredAppointmentList.isEmpty()) {
            VBox emptyBox = new VBox(10);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(40));
            Label emptyText = new Label("No matching appointments found.");
            emptyText.setStyle("-fx-font-size: 14px; -fx-text-fill: " + SECONDARY_TEXT + ";");
            emptyBox.getChildren().add(emptyText);
            tableRowsContainer.getChildren().add(emptyBox);
        } else {
            for (int i = 0; i < filteredAppointmentList.size(); i++) {
                Appointment appt = filteredAppointmentList.get(i);
                tableRowsContainer.getChildren().add(createAppointmentRow(appt));
                if (i < filteredAppointmentList.size() - 1) {
                    tableRowsContainer.getChildren().add(createRowSeparator());
                }
            }
        }

        if (totalAppointmentsCountLabel != null) {
            totalAppointmentsCountLabel.setText(filteredAppointmentList.size() + " Appointments Displayed");
        }
    }

    private void recalculateStatistics() {
        LocalDate today = LocalDate.now();

        long todayCount = masterAppointmentList.stream().filter(a -> a.getDate().equals(today)).count();
        long completedCount = masterAppointmentList.stream().filter(a -> "Completed".equalsIgnoreCase(a.getStatus())).count();
        long upcomingCount = masterAppointmentList.stream().filter(a -> "Upcoming".equalsIgnoreCase(a.getStatus()) || "Waiting".equalsIgnoreCase(a.getStatus())).count();
        long cancelledCount = masterAppointmentList.stream().filter(a -> "Cancelled".equalsIgnoreCase(a.getStatus())).count();

        if (todayCountVal != null) todayCountVal.setText(String.format("%02d", todayCount));
        if (completedCountVal != null) completedCountVal.setText(String.format("%02d", completedCount));
        if (upcomingCountVal != null) upcomingCountVal.setText(String.format("%02d", upcomingCount));
        if (cancelledCountVal != null) cancelledCountVal.setText(String.format("%02d", cancelledCount));
    }

    // =========================================================
    // MODALS & DIALOGS
    // =========================================================

    private void openAppointmentFormDialog(Stage owner, Appointment existingAppt) {
        boolean isEdit = existingAppt != null;

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.setTitle(isEdit ? "Reschedule / Edit Appointment" : "New Appointment");

        VBox root = new VBox(16);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: " + CARD_BG + ";");

        Label title = new Label(isEdit ? "Reschedule Appointment" : "Create New Appointment");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TEXT + ";");

        TextField patientNameField = new TextField(isEdit ? existingAppt.getPatientName() : "");
        patientNameField.setPromptText("Patient Full Name");

        DatePicker datePicker = new DatePicker(isEdit ? existingAppt.getDate() : LocalDate.now());
        
        ComboBox<String> timeCombo = new ComboBox<>();
        timeCombo.getItems().addAll("08:30 AM", "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "02:00 PM", "02:30 PM", "03:00 PM");
        timeCombo.setValue(isEdit ? existingAppt.getTime() : "09:00 AM");

        ComboBox<String> deptCombo = new ComboBox<>();
        deptCombo.getItems().addAll("Cardiology", "Neurology", "Pediatrics", "Orthopedics", "General Medicine");
        deptCombo.setValue(isEdit ? existingAppt.getDepartment() : "Cardiology");

        ComboBox<String> docCombo = new ComboBox<>();
        docCombo.getItems().addAll("Dr. Ananya Sharma", "Dr. Rahul Patil", "Dr. Priya Mehta", "Dr. Amit Joshi", "Dr. Neha Kulkarni");
        docCombo.setValue(isEdit ? existingAppt.getDoctorName() : "Dr. Ananya Sharma");

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Consultation", "Follow-up", "Emergency", "Routine Checkup");
        typeCombo.setValue(isEdit ? existingAppt.getType() : "Consultation");

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Upcoming", "Waiting", "Completed", "Cancelled");
        statusCombo.setValue(isEdit ? existingAppt.getStatus() : "Upcoming");

        Button submitBtn = new Button(isEdit ? "Update Appointment" : "Book Appointment");
        submitBtn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6;");

        submitBtn.setOnAction(e -> {
            if (patientNameField.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Patient name cannot be empty.");
                return;
            }

            if (isEdit) {
                existingAppt.setPatientName(patientNameField.getText().trim());
                existingAppt.setDate(datePicker.getValue());
                existingAppt.setTime(timeCombo.getValue());
                existingAppt.setDepartment(deptCombo.getValue());
                existingAppt.setDoctorName(docCombo.getValue());
                existingAppt.setType(typeCombo.getValue());
                existingAppt.setStatus(statusCombo.getValue());
            } else {
                Appointment newAppt = new Appointment(
                        timeCombo.getValue(),
                        patientNameField.getText().trim(),
                        "PAT-" + (1000 + masterAppointmentList.size() + 1),
                        deptCombo.getValue(),
                        docCombo.getValue(),
                        typeCombo.getValue(),
                        statusCombo.getValue(),
                        datePicker.getValue()
                );
                masterAppointmentList.add(0, newAppt);
            }

            updateFilteredData();
            dialog.close();
            showAlert(Alert.AlertType.INFORMATION, "Success", isEdit ? "Appointment updated successfully." : "Appointment created successfully.");
        });

        root.getChildren().addAll(
                title,
                new Label("Patient Name:"), patientNameField,
                new Label("Date:"), datePicker,
                new Label("Time:"), timeCombo,
                new Label("Department:"), deptCombo,
                new Label("Doctor:"), docCombo,
                new Label("Type:"), typeCombo,
                new Label("Status:"), statusCombo,
                submitBtn
        );

        Scene scene = new Scene(root, 380, 520);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showAppointmentDetailsDialog(Appointment appt) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Appointment Details");
        alert.setHeaderText("Details for " + appt.getPatientName() + " (" + appt.getPatientId() + ")");
        alert.setContentText(
                "Date: " + appt.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "\n" +
                "Time: " + appt.getTime() + "\n" +
                "Doctor: " + appt.getDoctorName() + "\n" +
                "Department: " + appt.getDepartment() + "\n" +
                "Type: " + appt.getType() + "\n" +
                "Status: " + appt.getStatus()
        );
        alert.showAndWait();
    }

    private void handleCancelAppointment(Appointment appt) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Cancel Appointment");
        confirmation.setHeaderText("Are you sure you want to cancel this appointment?");
        confirmation.setContentText("Patient: " + appt.getPatientName() + "\nDoctor: " + appt.getDoctorName());

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            appt.setStatus("Cancelled");
            updateFilteredData();
            showAlert(Alert.AlertType.INFORMATION, "Cancelled", "Appointment marked as cancelled.");
        }
    }

    private void exportToCSV(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Appointments Export");
        fileChooser.setInitialFileName("appointments.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("Date,Time,Patient ID,Patient Name,Department,Doctor,Type,Status");
                for (Appointment appt : filteredAppointmentList) {
                    writer.printf("%s,%s,%s,\"%s\",%s,\"%s\",%s,%s%n",
                            appt.getDate(), appt.getTime(), appt.getPatientId(),
                            appt.getPatientName(), appt.getDepartment(), appt.getDoctorName(),
                            appt.getType(), appt.getStatus());
                }
                showAlert(Alert.AlertType.INFORMATION, "Export Successful", "Data saved to " + file.getAbsolutePath());
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Export Failed", "Could not write file: " + ex.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // =========================================================
    // HELPER FUNCTIONS & SEED DATA
    // =========================================================

    private void seedInitialData() {
        LocalDate today = LocalDate.now();
        masterAppointmentList.addAll(
                new Appointment("09:00 AM", "Mrs. Kavita Sharma", "PAT-1024", "Cardiology", "Dr. Ananya Sharma", "Consultation", "Completed", today),
                new Appointment("09:30 AM", "Mr. Rajesh Kumar", "PAT-1025", "Neurology", "Dr. Rahul Patil", "Follow-up", "Waiting", today),
                new Appointment("10:00 AM", "Miss. Riya Shah", "PAT-1026", "Pediatrics", "Dr. Priya Mehta", "Consultation", "Upcoming", today),
                new Appointment("10:30 AM", "Mr. Suresh Patil", "PAT-1027", "Orthopedics", "Dr. Amit Joshi", "Follow-up", "Upcoming", today),
                new Appointment("11:00 AM", "Mrs. Neha Deshmukh", "PAT-1028", "General Medicine", "Dr. Neha Kulkarni", "Consultation", "Cancelled", today)
        );
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "P";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        }
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    private String getStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "completed": return SUCCESS_GREEN;
            case "waiting": return WARNING_ORANGE;
            case "upcoming": return PRIMARY_BLUE;
            case "cancelled": return ERROR_RED;
            default: return DARK_TEXT;
        }
    }

    private String getStatusBgColor(String status) {
        switch (status.toLowerCase()) {
            case "completed": return SUCCESS_LIGHT;
            case "waiting": return WARNING_LIGHT;
            case "upcoming": return PRIMARY_LIGHT;
            case "cancelled": return ERROR_LIGHT;
            default: return LIGHT_BACKGROUND;
        }
    }

    private Button createSmallButton(String text, String color, String bgColor) {

        Button button = new Button(text);
        button.setPrefHeight(30);
        button.setPadding(new Insets(0, 10, 0, 10));

        String baseStyle =
                "-fx-background-color: " + bgColor + ";" +
                "-fx-text-fill: " + color + ";" +
                "-fx-background-radius: 6;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: 700;" +
                "-fx-cursor: hand;";

        button.setStyle(baseStyle);

        button.setOnMouseEntered(e -> button.setStyle(
                baseStyle + "-fx-background-color: " + color + "; -fx-text-fill: white;"
        ));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));

        return button;
    }

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

    // =========================================================
    // INNER APPOINTMENT MODEL
    // =========================================================

    public static class Appointment {
        private String time;
        private String patientName;
        private String patientId;
        private String department;
        private String doctorName;
        private String type;
        private String status;
        private LocalDate date;

        public Appointment(String time, String patientName, String patientId, String department, String doctorName, String type, String status, LocalDate date) {
            this.time = time;
            this.patientName = patientName;
            this.patientId = patientId;
            this.department = department;
            this.doctorName = doctorName;
            this.type = type;
            this.status = status;
            this.date = date;
        }

        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }

        public String getPatientName() { return patientName; }
        public void setPatientName(String patientName) { this.patientName = patientName; }

        public String getPatientId() { return patientId; }
        public void setPatientId(String patientId) { this.patientId = patientId; }

        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }

        public String getDoctorName() { return doctorName; }
        public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
    }
}