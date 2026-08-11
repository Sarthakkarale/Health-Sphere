package com.healthsphere.view.Hospital;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
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

public class DoctorManagementView {

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

        return new Scene(root, stage.getWidth(), stage.getHeight());
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

        // Logo
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

        // Navigation buttons
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
                        true
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

        // =====================================================
        // NAVIGATION
        // =====================================================

        dashboardButton.setOnAction(event -> {

            HospitalDashboardView dashboardView =
                    new HospitalDashboardView();

            stage.setScene(
                    dashboardView.createScene(stage)
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

        appointmentButton.setOnAction(event -> {

            AppointmentManagementView appointmentView =
                    new AppointmentManagementView();

            stage.setScene(
                    appointmentView.createScene(stage)
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

        // Bottom spacer
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

        Label iconLabel = new Label(icon);

        iconLabel.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-text-fill: " +
                (selected
                        ? PRIMARY_BLUE
                        : DARK_TEXT) + ";"
        );

        Label textLabel = new Label(text);

        textLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: " +
                (selected ? "bold" : "normal") + ";" +
                "-fx-text-fill: " +
                (selected
                        ? PRIMARY_BLUE
                        : DARK_TEXT) + ";"
        );

        HBox content = new HBox(13);

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

        HBox topBar = new HBox(15);

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

        Label searchIcon = new Label("⌕");

        searchIcon.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        Label searchText = new Label(
                "Search doctors, departments..."
        );

        searchText.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #98A2B3;"
        );

        HBox searchBox = new HBox(8);

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

        Region spacer = new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label notification = new Label("♧");

        notification.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label settings = new Label("⚙");

        settings.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label administrator =
                new Label("Hospital Administrator");

        administrator.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label role =
                new Label("HOSPITAL ADMIN");

        role.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        VBox userInfo = new VBox(1);

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

        VBox content = new VBox(20);

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
                createSearchAndFilters()
        );

        content.getChildren().add(
                createDoctorList()
        );

        return content;
    }

    // =========================================================
    // PAGE HEADER
    // =========================================================

    private HBox createPageHeader() {

        HBox header = new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox titleBox = new VBox(4);

        Label title =
                new Label("Doctor Management");

        title.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label subtitle =
                new Label(
                        "Manage doctors, departments and availability"
                );

        subtitle.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        titleBox.getChildren().addAll(
                title,
                subtitle
        );

        Region spacer = new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button addDoctor =
                new Button("+  Add Doctor");

        addDoctor.setPrefHeight(40);

        addDoctor.setPadding(
                new Insets(0, 18, 0, 18)
        );

        addDoctor.setStyle(
                "-fx-background-color: " + PRIMARY_BLUE + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        header.getChildren().addAll(
                titleBox,
                spacer,
                addDoctor
        );

        return header;
    }

    // =========================================================
    // STATISTICS
    // =========================================================

    private HBox createStatistics() {

        HBox statistics = new HBox(15);

        statistics.getChildren().add(
                createStatisticCard(
                        "Total Doctors",
                        "128",
                        "Across all departments",
                        "♙",
                        PRIMARY_BLUE
                )
        );

        statistics.getChildren().add(
                createStatisticCard(
                        "Active Doctors",
                        "112",
                        "Currently available",
                        "✓",
                        SUCCESS_GREEN
                )
        );

        statistics.getChildren().add(
                createStatisticCard(
                        "On Leave",
                        "16",
                        "Currently unavailable",
                        "◷",
                        WARNING_ORANGE
                )
        );

        statistics.getChildren().add(
                createStatisticCard(
                        "Departments",
                        "12",
                        "Medical departments",
                        "✚",
                        PURPLE
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

        VBox card = new VBox(8);

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

        HBox top = new HBox();

        top.setAlignment(
                Pos.CENTER_LEFT
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 10px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        Region spacer = new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label iconLabel =
                new Label(icon);

        iconLabel.setPrefSize(30, 30);

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
    // SEARCH AND FILTERS
    // =========================================================

    private HBox createSearchAndFilters() {

        HBox container = new HBox(12);

        container.setAlignment(
                Pos.CENTER_LEFT
        );

        container.setPadding(
                new Insets(14)
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
                "Search by doctor name..."
        );

        searchField.setPrefHeight(38);
        searchField.setPrefWidth(300);

        searchField.setStyle(
                "-fx-background-color: #F7F8FC;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 7;" +
                "-fx-background-radius: 7;" +
                "-fx-padding: 0 12;" +
                "-fx-font-size: 10px;"
        );

        ComboBox<String> department =
                new ComboBox<>();

        department.getItems().addAll(
                "All Departments",
                "Cardiology",
                "Neurology",
                "Orthopedics",
                "Pediatrics",
                "General Medicine"
        );

        department.setValue(
                "All Departments"
        );

        department.setPrefHeight(38);

        department.setStyle(
                "-fx-font-size: 10px;"
        );

        ComboBox<String> status =
                new ComboBox<>();

        status.getItems().addAll(
                "All Status",
                "Active",
                "Inactive",
                "On Leave"
        );

        status.setValue(
                "All Status"
        );

        status.setPrefHeight(38);

        status.setStyle(
                "-fx-font-size: 10px;"
        );

        Region spacer = new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button filterButton =
                new Button("☷  Filters");

        filterButton.setPrefHeight(38);

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

        exportButton.setPrefHeight(38);

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
                department,
                status,
                spacer,
                filterButton,
                exportButton
        );

        return container;
    }

    // =========================================================
    // DOCTOR LIST
    // =========================================================

    private VBox createDoctorList() {

        VBox card = new VBox(0);

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;"
        );

        // Header
        HBox header = new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        header.setPadding(
                new Insets(17)
        );

        Label title =
                new Label("Doctors List");

        title.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Region spacer = new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label count =
                new Label("128 Doctors");

        count.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        header.getChildren().addAll(
                title,
                spacer,
                count
        );

        card.getChildren().add(header);

        card.getChildren().add(
                new Separator()
        );

        // Column header
        card.getChildren().add(
                createTableHeader()
        );

        card.getChildren().add(
                new Separator()
        );

        card.getChildren().add(
                createDoctorRow(
                        "Dr. Ananya Sharma",
                        "Cardiology",
                        "MD, Cardiology",
                        "DS",
                        "Active",
                        SUCCESS_GREEN
                )
        );

        card.getChildren().add(
                new Separator()
        );

        card.getChildren().add(
                createDoctorRow(
                        "Dr. Rahul Patil",
                        "Neurology",
                        "MD, Neurology",
                        "RP",
                        "Active",
                        SUCCESS_GREEN
                )
        );

        card.getChildren().add(
                new Separator()
        );

        card.getChildren().add(
                createDoctorRow(
                        "Dr. Priya Mehta",
                        "Pediatrics",
                        "MD, Pediatrics",
                        "PM",
                        "On Leave",
                        WARNING_ORANGE
                )
        );

        card.getChildren().add(
                new Separator()
        );

        card.getChildren().add(
                createDoctorRow(
                        "Dr. Amit Joshi",
                        "Orthopedics",
                        "MS, Orthopedics",
                        "AJ",
                        "Inactive",
                        ERROR_RED
                )
        );

        return card;
    }

    // =========================================================
    // TABLE HEADER
    // =========================================================

    private HBox createTableHeader() {

        HBox header = new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        header.setPadding(
                new Insets(10, 17, 10, 17)
        );

        Label doctor =
                createHeaderLabel(
                        "DOCTOR",
                        260
                );

        Label department =
                createHeaderLabel(
                        "DEPARTMENT",
                        150
                );

        Label qualification =
                createHeaderLabel(
                        "QUALIFICATION",
                        160
                );

        Label status =
                createHeaderLabel(
                        "STATUS",
                        110
                );

        Label actions =
                createHeaderLabel(
                        "ACTIONS",
                        160
                );

        header.getChildren().addAll(
                doctor,
                department,
                qualification,
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
    // DOCTOR ROW
    // =========================================================

    private HBox createDoctorRow(
            String name,
            String department,
            String qualification,
            String initials,
            String status,
            String statusColor
    ) {

        HBox row = new HBox();

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(11, 17, 11, 17)
        );

        // Doctor information
        HBox doctorBox =
                new HBox(10);

        doctorBox.setAlignment(
                Pos.CENTER_LEFT
        );

        doctorBox.setPrefWidth(260);

        Circle avatar =
                new Circle(20);

        avatar.setFill(
                Color.web("#E6EFFC")
        );

        Label initialsLabel =
                new Label(initials);

        initialsLabel.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";"
        );

        StackPane avatarPane =
                new StackPane(
                        avatar,
                        initialsLabel
                );

        VBox doctorInfo =
                new VBox(3);

        Label nameLabel =
                new Label(name);

        nameLabel.setStyle(
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label idLabel =
                new Label("Doctor ID: DOC-1024");

        idLabel.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        doctorInfo.getChildren().addAll(
                nameLabel,
                idLabel
        );

        doctorBox.getChildren().addAll(
                avatarPane,
                doctorInfo
        );

        // Department
        Label departmentLabel =
                new Label(department);

        departmentLabel.setPrefWidth(150);

        departmentLabel.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        // Qualification
        Label qualificationLabel =
                new Label(qualification);

        qualificationLabel.setPrefWidth(160);

        qualificationLabel.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        // Status
        Label statusLabel =
                new Label("●  " + status);

        statusLabel.setPrefWidth(110);

        statusLabel.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + statusColor + ";"
        );

        // Actions
        HBox actions =
                new HBox(6);

        actions.setPrefWidth(160);

        actions.setAlignment(
                Pos.CENTER_LEFT
        );

        Button viewButton =
                createSmallButton(
                        "View",
                        PRIMARY_BLUE
                );

        Button editButton =
                createSmallButton(
                        "Edit",
                        PURPLE
                );

        Button deleteButton =
                createSmallButton(
                        "Delete",
                        ERROR_RED
                );

        actions.getChildren().addAll(
                viewButton,
                editButton,
                deleteButton
        );

        row.getChildren().addAll(
                doctorBox,
                departmentLabel,
                qualificationLabel,
                statusLabel,
                actions
        );

        return row;
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

        button.setPrefHeight(28);

        button.setPadding(
                new Insets(0, 9, 0, 9)
        );

        button.setStyle(
                "-fx-background-color: " +
                color + "12;" +
                "-fx-text-fill: " +
                color + ";" +
                "-fx-background-radius: 6;" +
                "-fx-font-size: 8px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        return button;
    }
}