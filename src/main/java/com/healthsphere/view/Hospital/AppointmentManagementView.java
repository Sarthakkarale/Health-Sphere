package com.healthsphere.view.Hospital;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class AppointmentManagementView {

    // =========================================================
    // COLORS
    // =========================================================

    private static final String PRIMARY_BLUE = "#0756C9";
    private static final String DARK_TEXT = "#18212F";
    private static final String SECONDARY_TEXT = "#667085";
    private static final String LIGHT_BACKGROUND = "#F7F8FC";
    private static final String BORDER = "#E1E5ED";
    private static final String SUCCESS_GREEN = "#16856F";
    private static final String ERROR_RED = "#D64545";
    private static final String WARNING_ORANGE = "#E58A00";
    private static final String PURPLE = "#7654C5";

    // =========================================================
    // CREATE SCENE
    // =========================================================

    public Scene createScene(Stage stage) {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color: " + LIGHT_BACKGROUND + ";"
        );

        root.setLeft(createSidebar(stage));
        root.setTop(createTopBar());
        root.setCenter(createMainContent());

        return new Scene(root, 1280, 820);
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private VBox createSidebar(Stage stage) {

        VBox sidebar = new VBox(8);

        sidebar.setPrefWidth(220);

        sidebar.setPadding(
                new Insets(22, 15, 18, 15)
        );

        sidebar.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 0 1 0 0;"
        );

        // -----------------------------------------------------
        // LOGO
        // -----------------------------------------------------

        VBox logoBox = new VBox(2);

        logoBox.setPadding(
                new Insets(0, 5, 18, 5)
        );

        Label logo = new Label("Health-Sphere");

        logo.setStyle(
                "-fx-font-size: 21px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";"
        );

        Label subtitle = new Label("SMART HEALTHCARE");

        subtitle.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        logoBox.getChildren().addAll(
                logo,
                subtitle
        );

        sidebar.getChildren().add(logoBox);

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

        // =====================================================
        // DIRECT NAVIGATION
        // =====================================================

        dashboardButton.setOnAction(event -> {

            HospitalDashboardView dashboardView =
                    new HospitalDashboardView();

            stage.setScene(
                    dashboardView.createScene(stage)
            );
        });

        doctorButton.setOnAction(event -> {

            DoctorManagementView doctorView =
                    new DoctorManagementView();

            stage.setScene(
                    doctorView.createScene(stage)
            );
        });

        departmentButton.setOnAction(event -> {

            DepartmentManagementView departmentView =
                    new DepartmentManagementView();

            stage.setScene(
                    departmentView.createScene(stage)
            );
        });

        bedButton.setOnAction(event -> {

            BedManagementView bedView =
                    new BedManagementView();

            stage.setScene(
                    bedView.createScene(stage)
            );
        });

        analyticsButton.setOnAction(event -> {

            HospitalAnalyticsView analyticsView =
                    new HospitalAnalyticsView();

            stage.setScene(
                    analyticsView.createScene(stage)
            );
        });

        settingsButton.setOnAction(event -> {

            HospitalProfileSettingsView settingsView =
                    new HospitalProfileSettingsView();

            stage.setScene(
                    settingsView.createScene(stage)
            );
        });

        // -----------------------------------------------------
        // SPACER
        // -----------------------------------------------------

        Region spacer = new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        sidebar.getChildren().add(spacer);

        // -----------------------------------------------------
        // HELP & LOGOUT
        // -----------------------------------------------------

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
            boolean selected
    ) {

        Button button = new Button();

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-text-fill: " +
                (selected
                        ? PRIMARY_BLUE
                        : DARK_TEXT) + ";"
        );

        Label textLabel =
                new Label(text);

        textLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: " +
                (selected ? "bold" : "normal") + ";" +
                "-fx-text-fill: " +
                (selected
                        ? PRIMARY_BLUE
                        : DARK_TEXT) + ";"
        );

        HBox content =
                new HBox(13);

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

        if (selected) {

            button.setStyle(
                    "-fx-background-color: #E8F0FF;" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;"
            );

        } else {

            button.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;"
            );
        }

        return button;
    }

    // =========================================================
    // TOP BAR
    // =========================================================

    private HBox createTopBar() {

        HBox topBar =
                new HBox(15);

        topBar.setAlignment(
                Pos.CENTER_LEFT
        );

        topBar.setPadding(
                new Insets(10, 22, 10, 20)
        );

        topBar.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 0 0 1 0;"
        );

        Label searchIcon =
                new Label("⌕");

        searchIcon.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        Label searchText =
                new Label(
                        "Search patients, doctors..."
                );

        searchText.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #98A2B3;"
        );

        HBox searchBox =
                new HBox(8);

        searchBox.setAlignment(
                Pos.CENTER_LEFT
        );

        searchBox.setPrefWidth(330);
        searchBox.setPrefHeight(38);

        searchBox.setPadding(
                new Insets(0, 12, 0, 12)
        );

        searchBox.setStyle(
                "-fx-background-color: #F5F6FC;" +
                "-fx-background-radius: 8;"
        );

        searchBox.getChildren().addAll(
                searchIcon,
                searchText
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label notification =
                new Label("♧");

        notification.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label settings =
                new Label("⚙");

        settings.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label administrator =
                new Label(
                        "Hospital Administrator"
                );

        administrator.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label role =
                new Label(
                        "HOSPITAL ADMIN"
                );

        role.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        VBox userInfo =
                new VBox(1);

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
                Color.web("#DCE8F8")
        );

        Label avatarText =
                new Label("HA");

        avatarText.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";"
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

    private VBox createMainContent() {

        VBox content =
                new VBox(18);

        content.setPadding(
                new Insets(24)
        );

        content.setStyle(
                "-fx-background-color: " +
                LIGHT_BACKGROUND + ";"
        );

        content.getChildren().add(
                createPageHeader()
        );

        content.getChildren().add(
                createStatistics()
        );

        content.getChildren().add(
                createFilters()
        );

        content.getChildren().add(
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
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label subtitle =
                new Label(
                        "Manage appointments, doctors and patient schedules"
                );

        subtitle.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
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

        Button newAppointment =
                new Button(
                        "+  New Appointment"
                );

        newAppointment.setPrefHeight(40);

        newAppointment.setPadding(
                new Insets(0, 18, 0, 18)
        );

        newAppointment.setStyle(
                "-fx-background-color: " +
                PRIMARY_BLUE + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        header.getChildren().addAll(
                titleBox,
                spacer,
                newAppointment
        );

        return header;
    }

    // =========================================================
    // STATISTICS
    // =========================================================

    private HBox createStatistics() {

        HBox statistics =
                new HBox(15);

        statistics.getChildren().add(
                createStatisticCard(
                        "Today's Appointments",
                        "86",
                        "Scheduled today",
                        "▣",
                        PRIMARY_BLUE
                )
        );

        statistics.getChildren().add(
                createStatisticCard(
                        "Completed",
                        "42",
                        "Appointments completed",
                        "✓",
                        SUCCESS_GREEN
                )
        );

        statistics.getChildren().add(
                createStatisticCard(
                        "Upcoming",
                        "19",
                        "Waiting for consultation",
                        "◷",
                        PURPLE
                )
        );

        statistics.getChildren().add(
                createStatisticCard(
                        "Cancelled",
                        "07",
                        "Cancelled appointments",
                        "×",
                        ERROR_RED
                )
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
            String color
    ) {

        VBox card =
                new VBox(8);

        card.setPadding(
                new Insets(16)
        );

        card.setPrefHeight(105);

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;"
        );

        HBox top =
                new HBox();

        top.setAlignment(
                Pos.CENTER_LEFT
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 10px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
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
                30,
                30
        );

        iconLabel.setAlignment(
                Pos.CENTER
        );

        iconLabel.setStyle(
                "-fx-background-color: " +
                color + "18;" +
                "-fx-background-radius: 8;" +
                "-fx-text-fill: " +
                color + ";" +
                "-fx-font-size: 15px;" +
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
                "-fx-font-size: 23px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label subtitleLabel =
                new Label(subtitle);

        subtitleLabel.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
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

    private HBox createFilters() {

        HBox container =
                new HBox(10);

        container.setAlignment(
                Pos.CENTER_LEFT
        );

        container.setPadding(
                new Insets(13)
        );

        container.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 10;"
        );

        TextField searchField =
                new TextField();

        searchField.setPromptText(
                "Search patient or doctor..."
        );

        searchField.setPrefWidth(250);
        searchField.setPrefHeight(37);

        searchField.setStyle(
                "-fx-background-color: #F7F8FC;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 7;" +
                "-fx-background-radius: 7;" +
                "-fx-padding: 0 12;" +
                "-fx-font-size: 10px;"
        );

        DatePicker datePicker =
                new DatePicker();

        datePicker.setPromptText(
                "Select date"
        );

        datePicker.setPrefWidth(145);
        datePicker.setPrefHeight(37);

        ComboBox<String> doctorFilter =
                new ComboBox<>();

        doctorFilter.getItems().addAll(
                "All Doctors",
                "Dr. Ananya Sharma",
                "Dr. Rahul Patil",
                "Dr. Priya Mehta",
                "Dr. Amit Joshi"
        );

        doctorFilter.setValue(
                "All Doctors"
        );

        doctorFilter.setPrefWidth(150);
        doctorFilter.setPrefHeight(37);

        ComboBox<String> statusFilter =
                new ComboBox<>();

        statusFilter.getItems().addAll(
                "All Status",
                "Completed",
                "Waiting",
                "Upcoming",
                "Cancelled"
        );

        statusFilter.setValue(
                "All Status"
        );

        statusFilter.setPrefWidth(130);
        statusFilter.setPrefHeight(37);

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button filterButton =
                new Button("☷  Filters");

        filterButton.setPrefHeight(37);

        filterButton.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 7;" +
                "-fx-background-radius: 7;" +
                "-fx-text-fill: " + DARK_TEXT + ";" +
                "-fx-font-size: 10px;" +
                "-fx-cursor: hand;"
        );

        Button exportButton =
                new Button("↓  Export");

        exportButton.setPrefHeight(37);

        exportButton.setStyle(
                "-fx-background-color: #F1F5FB;" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";" +
                "-fx-background-radius: 7;" +
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        container.getChildren().addAll(
                searchField,
                datePicker,
                doctorFilter,
                statusFilter,
                spacer,
                filterButton,
                exportButton
        );

        return container;
    }

    // =========================================================
    // APPOINTMENT TABLE
    // =========================================================

    private VBox createAppointmentTable() {

        VBox table =
                new VBox(0);

        table.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;"
        );

        // Header
        HBox tableHeader =
                new HBox();

        tableHeader.setAlignment(
                Pos.CENTER_LEFT
        );

        tableHeader.setPadding(
                new Insets(16)
        );

        Label title =
                new Label(
                        "Today's Appointments"
                );

        title.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label count =
                new Label(
                        "86 Appointments"
                );

        count.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        tableHeader.getChildren().addAll(
                title,
                spacer,
                count
        );

        table.getChildren().add(
                tableHeader
        );

        table.getChildren().add(
                new Separator()
        );

        // Column header
        table.getChildren().add(
                createTableHeader()
        );

        table.getChildren().add(
                new Separator()
        );

        // Rows
        table.getChildren().add(
                createAppointmentRow(
                        "09:00 AM",
                        "Mrs. Kavita Sharma",
                        "Cardiology",
                        "Dr. Ananya Sharma",
                        "Consultation",
                        "Completed",
                        SUCCESS_GREEN
                )
        );

        table.getChildren().add(
                new Separator()
        );

        table.getChildren().add(
                createAppointmentRow(
                        "09:30 AM",
                        "Mr. Rajesh Kumar",
                        "Neurology",
                        "Dr. Rahul Patil",
                        "Follow-up",
                        "Waiting",
                        WARNING_ORANGE
                )
        );

        table.getChildren().add(
                new Separator()
        );

        table.getChildren().add(
                createAppointmentRow(
                        "10:00 AM",
                        "Miss. Riya Shah",
                        "Pediatrics",
                        "Dr. Priya Mehta",
                        "Consultation",
                        "Upcoming",
                        PRIMARY_BLUE
                )
        );

        table.getChildren().add(
                new Separator()
        );

        table.getChildren().add(
                createAppointmentRow(
                        "10:30 AM",
                        "Mr. Suresh Patil",
                        "Orthopedics",
                        "Dr. Amit Joshi",
                        "Follow-up",
                        "Upcoming",
                        PRIMARY_BLUE
                )
        );

        table.getChildren().add(
                new Separator()
        );

        table.getChildren().add(
                createAppointmentRow(
                        "11:00 AM",
                        "Mrs. Neha Deshmukh",
                        "General Medicine",
                        "Dr. Neha Kulkarni",
                        "Consultation",
                        "Cancelled",
                        ERROR_RED
                )
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
                new Insets(10, 16, 10, 16)
        );

        Label time =
                createHeaderLabel(
                        "TIME",
                        90
                );

        Label patient =
                createHeaderLabel(
                        "PATIENT",
                        220
                );

        Label department =
                createHeaderLabel(
                        "DEPARTMENT",
                        140
                );

        Label doctor =
                createHeaderLabel(
                        "DOCTOR",
                        170
                );

        Label type =
                createHeaderLabel(
                        "TYPE",
                        120
                );

        Label status =
                createHeaderLabel(
                        "STATUS",
                        110
                );

        Label actions =
                createHeaderLabel(
                        "ACTIONS",
                        150
                );

        header.getChildren().addAll(
                time,
                patient,
                department,
                doctor,
                type,
                status,
                actions
        );

        return header;
    }

    // =========================================================
    // HEADER LABEL
    // =========================================================

    private Label createHeaderLabel(
            String text,
            double width
    ) {

        Label label =
                new Label(text);

        label.setPrefWidth(width);

        label.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        return label;
    }

    // =========================================================
    // APPOINTMENT ROW
    // =========================================================

    private HBox createAppointmentRow(
            String time,
            String patient,
            String department,
            String doctor,
            String type,
            String status,
            String statusColor
    ) {

        HBox row =
                new HBox();

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(11, 16, 11, 16)
        );

        // Time
        Label timeLabel =
                new Label(time);

        timeLabel.setPrefWidth(90);

        timeLabel.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        // Patient
        HBox patientBox =
                new HBox(8);

        patientBox.setPrefWidth(220);

        patientBox.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle avatar =
                new Circle(18);

        avatar.setFill(
                Color.web("#E6EFFC")
        );

        String initials =
                getInitials(patient);

        Label initialsLabel =
                new Label(initials);

        initialsLabel.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";"
        );

        StackPane avatarPane =
                new StackPane(
                        avatar,
                        initialsLabel
                );

        VBox patientInfo =
                new VBox(2);

        Label patientName =
                new Label(patient);

        patientName.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label patientId =
                new Label("PAT-1024");

        patientId.setStyle(
                "-fx-font-size: 7px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        patientInfo.getChildren().addAll(
                patientName,
                patientId
        );

        patientBox.getChildren().addAll(
                avatarPane,
                patientInfo
        );

        // Department
        Label departmentLabel =
                new Label(department);

        departmentLabel.setPrefWidth(140);

        departmentLabel.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        // Doctor
        Label doctorLabel =
                new Label(doctor);

        doctorLabel.setPrefWidth(170);

        doctorLabel.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        // Type
        Label typeLabel =
                new Label(type);

        typeLabel.setPrefWidth(120);

        typeLabel.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        // Status
        Label statusLabel =
                new Label("●  " + status);

        statusLabel.setPrefWidth(110);

        statusLabel.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + statusColor + ";"
        );

        // Actions
        HBox actions =
                new HBox(5);

        actions.setPrefWidth(150);

        actions.setAlignment(
                Pos.CENTER_LEFT
        );

        Button view =
                createSmallButton(
                        "View",
                        PRIMARY_BLUE
                );

        Button reschedule =
                createSmallButton(
                        "Reschedule",
                        PURPLE
                );

        Button cancel =
                createSmallButton(
                        "Cancel",
                        ERROR_RED
                );

        actions.getChildren().addAll(
                view,
                reschedule,
                cancel
        );

        row.getChildren().addAll(
                timeLabel,
                patientBox,
                departmentLabel,
                doctorLabel,
                typeLabel,
                statusLabel,
                actions
        );

        return row;
    }

    // =========================================================
    // GET INITIALS
    // =========================================================

    private String getInitials(String name) {

        String[] parts =
                name.split(" ");

        if (parts.length == 1) {
            return parts[0]
                    .substring(0, 1)
                    .toUpperCase();
        }

        return (
                parts[0].substring(0, 1) +
                parts[parts.length - 1]
                        .substring(0, 1)
        ).toUpperCase();
    }

    // =========================================================
    // SMALL BUTTON
    // =========================================================

    private Button createSmallButton(
            String text,
            String color
    ) {

        Button button =
                new Button(text);

        button.setPrefHeight(27);

        button.setPadding(
                new Insets(0, 8, 0, 8)
        );

        button.setStyle(
                "-fx-background-color: " +
                color + "12;" +
                "-fx-text-fill: " +
                color + ";" +
                "-fx-background-radius: 6;" +
                "-fx-font-size: 7px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        return button;
    }
}