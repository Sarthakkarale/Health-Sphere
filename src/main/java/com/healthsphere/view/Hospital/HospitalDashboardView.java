package com.healthsphere.view.Hospital;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class HospitalDashboardView {

    // =========================================================
    // COLOR PALETTE (Light Theme & Refined Modern Accents)
    // =========================================================

    private static final String PRIMARY_BLUE = "#1E62D0";
    private static final String PRIMARY_LIGHT = "#EFF5FF";
    private static final String DARK_TEXT = "#0F172A";
    private static final String SECONDARY_TEXT = "#64748B";
    private static final String LIGHT_BACKGROUND = "#F8FAFC";
    private static final String CARD_BG = "#FFFFFF";
    private static final String BORDER = "#E2E8F0";
    
    private static final String SUCCESS_GREEN = "#059669";
    private static final String SUCCESS_LIGHT = "#ECFDF5";
    
    private static final String WARNING_ORANGE = "#D97706";
    private static final String WARNING_LIGHT = "#FFFBEB";
    
    private static final String ERROR_RED = "#DC2626";
    private static final String ERROR_LIGHT = "#FEF2F2";
    
    private static final String PURPLE = "#7C3AED";
    private static final String PURPLE_LIGHT = "#F5F3FF";

    // =========================================================
    // DYNAMIC UI COMPONENTS & DATA STATE
    // =========================================================

    private VBox recentActivityList;
    private Region bedProgressBar;
    private Label bedOccupancyLabel;
    private final ObservableList<HBox> activityEntries = FXCollections.observableArrayList();

    // KPI Labels for dynamic updates
    private Label totalDoctorsValue;
    private Label todayAppointmentsValue;
    private Label availableBedsValue;
    private Label emergencyCasesValue;

    // Appointment Status Labels
    private Label completedApptLabel;
    private Label waitingApptLabel;
    private Label upcomingApptLabel;
    private Label cancelledApptLabel;

    // =========================================================
    // CREATE SCENE
    // =========================================================

    public Scene createScene(Stage stage) {

        BorderPane root = new BorderPane();

        root.setStyle("-fx-background-color: " + LIGHT_BACKGROUND + ";");

        root.setLeft(createSidebar(stage));
        root.setTop(createTopBar(stage));

        // Wrap main content in a scroll pane for better adaptability
        ScrollPane scrollPane = new ScrollPane(createMainContent(stage));
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");
        
        root.setCenter(scrollPane);

        return new Scene(root, stage.getWidth(), stage.getHeight());
    }

    // =========================================================
    // SIDEBAR (Includes Patient Reviews Link)
    // =========================================================

    private VBox createSidebar(Stage stage) {

        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(240);
        sidebar.setPadding(new Insets(24, 16, 20, 16));

        sidebar.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 0 1 0 0;"
        );

        // LOGO
        VBox logoBox = new VBox(2);
        logoBox.setPadding(new Insets(0, 8, 24, 8));

        Label logo = new Label("Health-Sphere");
        logo.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";"
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
        Button dashboardButton = createNavigationButton("▦", "Dashboard", true);
        Button doctorButton = createNavigationButton("♙", "Doctors", false);
        Button departmentButton = createNavigationButton("✚", "Departments", false);
        Button bedButton = createNavigationButton("▥", "Beds", false);
        Button appointmentButton = createNavigationButton("▣", "Appointments", false);
        Button reviewsButton = createNavigationButton("★", "Patient Reviews", false); // Added Reviews Button
        Button analyticsButton = createNavigationButton("◈", "Analytics", false);
        Button settingsButton = createNavigationButton("⚙", "Hospital Settings", false);

        sidebar.getChildren().addAll(
                dashboardButton,
                doctorButton,
                departmentButton,
                bedButton,
                appointmentButton,
                reviewsButton,
                analyticsButton,
                settingsButton
        );

        // NAVIGATION ACTIONS
        dashboardButton.setOnAction(event -> {
            refreshDashboardData();
        });

        doctorButton.setOnAction(event -> {
            DoctorManagementView doctorView = new DoctorManagementView();
            stage.setScene(doctorView.createScene(stage));
        });

        departmentButton.setOnAction(event -> {
            DepartmentManagementView departmentView = new DepartmentManagementView();
            stage.setScene(departmentView.createScene(stage));
        });

        bedButton.setOnAction(event -> {
            BedManagementView bedView = new BedManagementView();
            stage.setScene(bedView.createScene(stage));
        });

        appointmentButton.setOnAction(event -> {
            AppointmentManagementView appointmentView = new AppointmentManagementView();
            stage.setScene(appointmentView.createScene(stage));
        });

        // Patient Reviews Navigation Event
        reviewsButton.setOnAction(event -> {
            HospitalReviewView reviewView = new HospitalReviewView();
            stage.setScene(reviewView.createScene(stage));
        });

        analyticsButton.setOnAction(event -> {
            HospitalAnalyticsView analyticsView = new HospitalAnalyticsView();
            stage.setScene(analyticsView.createScene(stage));
        });

        settingsButton.setOnAction(event -> {
            HospitalProfileSettingsView settingsView = new HospitalProfileSettingsView();
            stage.setScene(settingsView.createScene(stage));
        });

        // SPACER
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);

        // FOOTER BUTTONS
        Button helpButton = createNavigationButton("?", "Help Center", false);
        Button logoutButton = createNavigationButton("↪", "Logout", false);

        helpButton.setOnAction(event -> showHelpDialog());
        logoutButton.setOnAction(event -> handleLogout(stage));

        sidebar.getChildren().addAll(helpButton, logoutButton);

        return sidebar;
    }

    // =========================================================
    // NAVIGATION BUTTON
    // =========================================================

    private Button createNavigationButton(String icon, String text, boolean selected) {

        Button button = new Button();
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-text-fill: " + (selected ? PRIMARY_BLUE : SECONDARY_TEXT) + ";"
        );

        Label textLabel = new Label(text);
        textLabel.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: " + (selected ? "bold" : "500") + ";" +
                "-fx-text-fill: " + (selected ? PRIMARY_BLUE : DARK_TEXT) + ";"
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
            button.setStyle(baseStyle + "-fx-background-color: " + PRIMARY_LIGHT + ";");
        } else {
            button.setStyle(baseStyle + "-fx-background-color: transparent;");
            
            button.setOnMouseEntered(e -> button.setStyle(baseStyle + "-fx-background-color: #F1F5F9;"));
            button.setOnMouseExited(e -> button.setStyle(baseStyle + "-fx-background-color: transparent;"));
        }

        return button;
    }

    // =========================================================
    // TOP BAR
    // =========================================================

    private HBox createTopBar(Stage stage) {

        HBox topBar = new HBox(16);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 28, 12, 28));

        topBar.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 0 0 1 0;"
        );

        // Search Input Bar
        Label searchIcon = new Label("⌕");
        searchIcon.setStyle("-fx-font-size: 18px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        TextField searchField = new TextField();
        searchField.setPromptText("Search patients, doctors, records...");
        searchField.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-prompt-text-fill: #94A3B8;" +
                "-fx-font-size: 13px;" +
                "-fx-text-inner-color: " + DARK_TEXT + ";"
        );
        HBox.setHgrow(searchField, Priority.ALWAYS);

        searchField.setOnAction(e -> handleSearchQuery(searchField.getText()));

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
        searchBox.getChildren().addAll(searchIcon, searchField);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Topbar Icons
        Label notification = new Label("🔔");
        notification.setStyle("-fx-font-size: 16px; -fx-cursor: hand; -fx-text-fill: " + SECONDARY_TEXT + ";");
        notification.setOnMouseClicked(e -> showNotificationsMenu(notification));

        Label settings = new Label("⚙");
        settings.setStyle("-fx-font-size: 18px; -fx-cursor: hand; -fx-text-fill: " + SECONDARY_TEXT + ";");
        settings.setOnMouseClicked(e -> {
            HospitalProfileSettingsView settingsView = new HospitalProfileSettingsView();
            stage.setScene(settingsView.createScene(stage));
        });

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
        avatarBox.setStyle("-fx-cursor: hand;");
        avatarBox.setOnMouseClicked(e -> showProfileMenu(avatarBox, stage));

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
                createHeader(),
                createKpiCards(),
                createMiddleSection(),
                createBottomSection(stage)
        );

        return content;
    }

    // =========================================================
    // HEADER
    // =========================================================

    private VBox createHeader() {

        VBox header = new VBox(4);

        Label title = new Label("Hospital Dashboard");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 800; -fx-text-fill: " + DARK_TEXT + ";");

        Label subtitle = new Label("Overview of hospital operations, bed capacity, and today's schedule");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(title, subtitle);

        return header;
    }

    // =========================================================
    // KPI CARDS
    // =========================================================

    private HBox createKpiCards() {

        HBox cards = new HBox(18);

        VBox totalDoctorsCard = createKpiCard("Total Doctors", "128", "+8.4%", "♙", PRIMARY_BLUE, PRIMARY_LIGHT);
        VBox todayApptsCard = createKpiCard("Today's Appointments", "86", "+12.5%", "▣", PURPLE, PURPLE_LIGHT);
        VBox availableBedsCard = createKpiCard("Available Beds", "42", "18 available", "▥", SUCCESS_GREEN, SUCCESS_LIGHT);
        VBox emergencyCard = createKpiCard("Emergency Cases", "07", "3 critical", "!", ERROR_RED, ERROR_LIGHT);

        totalDoctorsValue = (Label) totalDoctorsCard.getChildren().get(1);
        todayAppointmentsValue = (Label) todayApptsCard.getChildren().get(1);
        availableBedsValue = (Label) availableBedsCard.getChildren().get(1);
        emergencyCasesValue = (Label) emergencyCard.getChildren().get(1);

        cards.getChildren().addAll(totalDoctorsCard, todayApptsCard, availableBedsCard, emergencyCard);

        for (javafx.scene.Node node : cards.getChildren()) {
            HBox.setHgrow(node, Priority.ALWAYS);
        }

        return cards;
    }

    private VBox createKpiCard(String title, String value, String trend, String icon, String color, String bgColor) {

        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setMinHeight(130);

        applyCardStyle(card);

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label iconLabel = new Label(icon);
        iconLabel.setAlignment(Pos.CENTER);
        iconLabel.setPrefSize(38, 38);
        iconLabel.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                "-fx-background-radius: 10;" +
                "-fx-text-fill: " + color + ";" +
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;"
        );

        top.getChildren().addAll(titleLabel, spacer, iconLabel);

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: 800; -fx-text-fill: " + DARK_TEXT + ";");

        Label trendLabel = new Label(trend);
        trendLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + color + "; -fx-font-weight: bold;");

        card.getChildren().addAll(top, valueLabel, trendLabel);

        return card;
    }

    // =========================================================
    // MIDDLE SECTION
    // =========================================================

    private HBox createMiddleSection() {

        HBox section = new HBox(20);

        VBox appointmentOverview = createAppointmentOverview();
        VBox bedOccupancy = createBedOccupancy();

        section.getChildren().addAll(appointmentOverview, bedOccupancy);

        HBox.setHgrow(appointmentOverview, Priority.ALWAYS);
        HBox.setHgrow(bedOccupancy, Priority.ALWAYS);

        return section;
    }

    // APPOINTMENT OVERVIEW
    private VBox createAppointmentOverview() {

        VBox card = createCard();
        card.getChildren().add(createCardHeading("Appointment Overview", "Today's appointment status breakdown"));

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(14);

        VBox completedBox = createStatusBox("Completed", "42", SUCCESS_GREEN, SUCCESS_LIGHT);
        VBox waitingBox = createStatusBox("Waiting", "18", WARNING_ORANGE, WARNING_LIGHT);
        VBox upcomingBox = createStatusBox("Upcoming", "19", PRIMARY_BLUE, PRIMARY_LIGHT);
        VBox cancelledBox = createStatusBox("Cancelled", "07", ERROR_RED, ERROR_LIGHT);

        completedApptLabel = (Label) completedBox.getChildren().get(1);
        waitingApptLabel = (Label) waitingBox.getChildren().get(1);
        upcomingApptLabel = (Label) upcomingBox.getChildren().get(1);
        cancelledApptLabel = (Label) cancelledBox.getChildren().get(1);

        grid.add(completedBox, 0, 0);
        grid.add(waitingBox, 1, 0);
        grid.add(upcomingBox, 0, 1);
        grid.add(cancelledBox, 1, 1);

        grid.getChildren().forEach(child -> GridPane.setHgrow(child, Priority.ALWAYS));

        card.getChildren().add(grid);

        return card;
    }

    private VBox createStatusBox(String title, String value, String color, String bgColor) {

        VBox box = new VBox(6);
        box.setPadding(new Insets(16));
        box.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: " + color + "33;" +
                "-fx-border-radius: 10;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: 600; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: 800; -fx-text-fill: " + color + ";");

        box.getChildren().addAll(titleLabel, valueLabel);

        return box;
    }

    // BED OCCUPANCY
    private VBox createBedOccupancy() {

        VBox card = createCard();
        card.getChildren().add(createCardHeading("Bed Occupancy", "Current hospital capacity status"));

        HBox occHeader = new HBox();
        occHeader.setAlignment(Pos.BASELINE_LEFT);
        
        bedOccupancyLabel = new Label("72%");
        bedOccupancyLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: 800; -fx-text-fill: " + DARK_TEXT + ";");
        
        Label occSub = new Label(" total beds occupied");
        occSub.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        occHeader.getChildren().addAll(bedOccupancyLabel, occSub);
        card.getChildren().add(occHeader);

        StackPane progressContainer = new StackPane();
        progressContainer.setPrefHeight(12);

        Region background = new Region();
        background.setMaxWidth(Double.MAX_VALUE);
        background.setPrefHeight(10);
        background.setStyle("-fx-background-color: #E2E8F0; -fx-background-radius: 10;");

        bedProgressBar = new Region();
        bedProgressBar.setPrefWidth(260);
        bedProgressBar.setPrefHeight(10);
        bedProgressBar.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-background-radius: 10;");

        StackPane.setAlignment(bedProgressBar, Pos.CENTER_LEFT);
        progressContainer.getChildren().addAll(background, bedProgressBar);

        card.getChildren().add(progressContainer);

        VBox rows = new VBox(10);
        rows.getChildren().addAll(
                createBedRow("General Ward", "48 / 70", PRIMARY_BLUE),
                createBedRow("ICU", "18 / 25", ERROR_RED),
                createBedRow("Emergency", "08 / 15", WARNING_ORANGE)
        );

        card.getChildren().add(rows);

        return card;
    }

    private HBox createBedRow(String name, String count, String color) {

        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 500; -fx-text-fill: " + DARK_TEXT + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label countLabel = new Label(count);
        countLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: " + color + ";");

        row.getChildren().addAll(nameLabel, spacer, countLabel);

        return row;
    }

    // =========================================================
    // BOTTOM SECTION
    // =========================================================

    private HBox createBottomSection(Stage stage) {

        HBox section = new HBox(20);

        VBox deptStats = createDepartmentStatistics();
        VBox recentActivities = createRecentActivities();
        VBox quickActions = createQuickActions(stage);

        section.getChildren().addAll(deptStats, recentActivities, quickActions);

        HBox.setHgrow(deptStats, Priority.ALWAYS);
        HBox.setHgrow(recentActivities, Priority.ALWAYS);
        HBox.setHgrow(quickActions, Priority.ALWAYS);

        return section;
    }

    // DEPARTMENT STATISTICS
    private VBox createDepartmentStatistics() {

        VBox card = createCard();
        card.getChildren().add(createCardHeading("Departments", "Doctors allocation"));

        VBox rows = new VBox(12);
        rows.getChildren().addAll(
                createDepartmentRow("Cardiology", "24", PRIMARY_BLUE),
                createDepartmentRow("Neurology", "18", PURPLE),
                createDepartmentRow("Orthopedics", "16", SUCCESS_GREEN),
                createDepartmentRow("Pediatrics", "14", WARNING_ORANGE)
        );

        card.getChildren().add(rows);

        return card;
    }

    private HBox createDepartmentRow(String department, String doctors, String color) {

        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        Circle dot = new Circle(4, Color.web(color));

        Label name = new Label(department);
        name.setStyle("-fx-font-size: 12px; -fx-font-weight: 500; -fx-text-fill: " + DARK_TEXT + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label doctorCount = new Label(doctors + " Doctors");
        doctorCount.setStyle("-fx-font-size: 11px; -fx-font-weight: 600; -fx-text-fill: " + SECONDARY_TEXT + ";");

        row.getChildren().addAll(dot, name, spacer, doctorCount);

        return row;
    }

    // RECENT ACTIVITIES
    private VBox createRecentActivities() {

        VBox card = createCard();
        card.getChildren().add(createCardHeading("Recent Activities", "Latest hospital updates"));

        recentActivityList = new VBox(12);

        addActivityItem("Dr. Sharma added a new appointment", "10 minutes ago", PRIMARY_BLUE);
        addActivityItem("Bed #ICU-08 is now available", "25 minutes ago", SUCCESS_GREEN);
        addActivityItem("Emergency case admitted", "42 minutes ago", ERROR_RED);
        addActivityItem("New doctor profile updated", "1 hour ago", PURPLE);

        card.getChildren().add(recentActivityList);

        return card;
    }

    private void addActivityItem(String text, String time, String color) {
        HBox activityRow = createActivity(text, time, color);
        activityEntries.add(0, activityRow);
        if (recentActivityList != null) {
            recentActivityList.getChildren().setAll(activityEntries);
        }
    }

    private HBox createActivity(String text, String time, String color) {

        HBox row = new HBox(10);
        row.setAlignment(Pos.TOP_LEFT);

        Circle dot = new Circle(4, Color.web(color));

        VBox info = new VBox(2);

        Label textLabel = new Label(text);
        textLabel.setWrapText(true);
        textLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: 500; -fx-text-fill: " + DARK_TEXT + ";");

        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        info.getChildren().addAll(textLabel, timeLabel);
        row.getChildren().addAll(dot, info);

        return row;
    }

    // QUICK ACTIONS
    private VBox createQuickActions(Stage stage) {

        VBox card = createCard();
        card.getChildren().add(createCardHeading("Quick Actions", "Common operations"));

        VBox buttons = new VBox(10);

        Button addDoctorBtn = createActionButton("＋  Add Doctor", PRIMARY_BLUE, PRIMARY_LIGHT);
        Button newAppointmentBtn = createActionButton("▣  New Appointment", PURPLE, PURPLE_LIGHT);
        Button manageBedsBtn = createActionButton("▥  Manage Beds", SUCCESS_GREEN, SUCCESS_LIGHT);
        Button viewAnalyticsBtn = createActionButton("◈  View Analytics", WARNING_ORANGE, WARNING_LIGHT);

        addDoctorBtn.setOnAction(event -> {
            DoctorManagementView doctorView = new DoctorManagementView();
            stage.setScene(doctorView.createScene(stage));
            recordActivity("Opened Doctor Management module", PRIMARY_BLUE);
        });

        newAppointmentBtn.setOnAction(event -> {
            AppointmentManagementView apptView = new AppointmentManagementView();
            stage.setScene(apptView.createScene(stage));
            recordActivity("Opened Appointment Management module", PURPLE);
        });

        manageBedsBtn.setOnAction(event -> {
            BedManagementView bedView = new BedManagementView();
            stage.setScene(bedView.createScene(stage));
            recordActivity("Opened Bed Allocation overview", SUCCESS_GREEN);
        });

        viewAnalyticsBtn.setOnAction(event -> {
            HospitalAnalyticsView analyticsView = new HospitalAnalyticsView();
            stage.setScene(analyticsView.createScene(stage));
            recordActivity("Opened Analytics Dashboard", WARNING_ORANGE);
        });

        buttons.getChildren().addAll(addDoctorBtn, newAppointmentBtn, manageBedsBtn, viewAnalyticsBtn);

        card.getChildren().add(buttons);

        return card;
    }

    private Button createActionButton(String text, String color, String bgColor) {

        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(38);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(0, 14, 0, 14));

        String baseStyle =
                "-fx-background-color: " + bgColor + ";" +
                "-fx-text-fill: " + color + ";" +
                "-fx-background-radius: 8;" +
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

    // =========================================================
    // COMMON CARD LAYOUT & SHADOWS
    // =========================================================

    private HBox createCardHeading(String title, String subtitle) {

        HBox heading = new HBox();
        heading.setAlignment(Pos.CENTER_LEFT);

        VBox text = new VBox(2);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 700; -fx-text-fill: " + DARK_TEXT + ";");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        text.getChildren().addAll(titleLabel, subtitleLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label more = new Label("•••");
        more.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-cursor: hand;");
        more.setOnMouseClicked(e -> showMoreOptionsMenu(more, title));

        heading.getChildren().addAll(text, spacer, more);

        return heading;
    }

    private VBox createCard() {
        VBox card = new VBox(16);
        card.setPadding(new Insets(20));
        applyCardStyle(card);
        return card;
    }

    private void applyCardStyle(VBox card) {
        card.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(15, 23, 42, 0.04));
        shadow.setRadius(10);
        shadow.setOffsetY(3);
        card.setEffect(shadow);
    }

    // =========================================================
    // EVENT HANDLERS & FUNCTIONAL UTILITIES
    // =========================================================

    private void handleSearchQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            return;
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Search Results");
        alert.setHeaderText("Health-Sphere Search");
        alert.setContentText("Searching database for query: \"" + query.trim() + "\"");
        alert.showAndWait();
    }

    private void showNotificationsMenu(Label anchor) {
        ContextMenu menu = new ContextMenu();
        MenuItem item1 = new MenuItem("🚨 3 Emergency cases pending review");
        MenuItem item2 = new MenuItem("📅 5 New appointment requests today");
        MenuItem item3 = new MenuItem("⚠️ ICU Capacity reached 72%");
        menu.getItems().addAll(item1, item2, item3);
        menu.show(anchor, javafx.geometry.Side.BOTTOM, 0, 0);
    }

    private void showProfileMenu(StackPane anchor, Stage stage) {
        ContextMenu menu = new ContextMenu();
        MenuItem profileItem = new MenuItem("Profile Settings");
        MenuItem logoutItem = new MenuItem("Log Out");

        profileItem.setOnAction(e -> {
            HospitalProfileSettingsView settingsView = new HospitalProfileSettingsView();
            stage.setScene(settingsView.createScene(stage));
        });

        logoutItem.setOnAction(e -> handleLogout(stage));

        menu.getItems().addAll(profileItem, logoutItem);
        menu.show(anchor, javafx.geometry.Side.BOTTOM, 0, 0);
    }

    private void showMoreOptionsMenu(Label anchor, String cardTitle) {
        ContextMenu menu = new ContextMenu();
        MenuItem refreshItem = new MenuItem("Refresh " + cardTitle);
        MenuItem exportItem = new MenuItem("Export Summary");

        refreshItem.setOnAction(e -> refreshDashboardData());
        exportItem.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export");
            alert.setHeaderText("Export Summary Data");
            alert.setContentText("Exporting report for " + cardTitle + "...");
            alert.showAndWait();
        });

        menu.getItems().addAll(refreshItem, exportItem);
        menu.show(anchor, javafx.geometry.Side.BOTTOM, 0, 0);
    }

    private void recordActivity(String actionText, String color) {
        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));
        addActivityItem(actionText, currentTime, color);
    }

    private void refreshDashboardData() {
        recordActivity("Dashboard data updated manually", PRIMARY_BLUE);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dashboard Refresh");
        alert.setHeaderText(null);
        alert.setContentText("Dashboard view and real-time statistics refreshed successfully.");
        alert.showAndWait();
    }

    private void showHelpDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help Center");
        alert.setHeaderText("Health-Sphere Support & Assistance");
        alert.setContentText("For technical support or issues, please contact:\nSupport Desk: support@healthsphere.com\nExt: 1800-456-789");
        alert.showAndWait();
    }

    private void handleLogout(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText("Sign Out");
        alert.setContentText("Are you sure you want to log out of Health-Sphere?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            stage.close();
        }
    }
}