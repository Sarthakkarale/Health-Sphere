package com.healthsphere.view.Hospital;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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

public class DepartmentManagementView {

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

        Label subtitle =
                new Label("SMART HEALTHCARE");

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

        // -----------------------------------------------------
        // SIDEBAR SPACER
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

        // Search
        Label searchIcon =
                new Label("⌕");

        searchIcon.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        Label searchText =
                new Label(
                        "Search departments..."
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
                new VBox(20);

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
                createSearchBar()
        );

        content.getChildren().add(
                createDepartmentGrid()
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
                        "Department Management"
                );

        title.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label subtitle =
                new Label(
                        "Manage hospital departments and department heads"
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

        Button addDepartment =
                new Button(
                        "+  Add Department"
                );

        addDepartment.setPrefHeight(40);

        addDepartment.setPadding(
                new Insets(0, 18, 0, 18)
        );

        addDepartment.setStyle(
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
                addDepartment
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
                        "Total Departments",
                        "12",
                        "Active hospital departments",
                        "✚",
                        PRIMARY_BLUE
                )
        );

        statistics.getChildren().add(
                createStatisticCard(
                        "Total Doctors",
                        "128",
                        "Across all departments",
                        "♙",
                        PURPLE
                )
        );

        statistics.getChildren().add(
                createStatisticCard(
                        "Active Departments",
                        "11",
                        "Currently operational",
                        "✓",
                        SUCCESS_GREEN
                )
        );

        statistics.getChildren().add(
                createStatisticCard(
                        "24/7 Departments",
                        "06",
                        "Emergency services",
                        "◷",
                        WARNING_ORANGE
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
    // SEARCH BAR
    // =========================================================

    private HBox createSearchBar() {

        HBox container =
                new HBox(12);

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
                "Search department name..."
        );

        searchField.setPrefWidth(320);
        searchField.setPrefHeight(38);

        searchField.setStyle(
                "-fx-background-color: #F7F8FC;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 7;" +
                "-fx-background-radius: 7;" +
                "-fx-padding: 0 12;" +
                "-fx-font-size: 10px;"
        );

        ComboBox<String> departmentType =
                new ComboBox<>();

        departmentType.getItems().addAll(
                "All Departments",
                "Clinical",
                "Surgical",
                "Diagnostic",
                "Emergency",
                "Support"
        );

        departmentType.setValue(
                "All Departments"
        );

        departmentType.setPrefHeight(38);

        departmentType.setStyle(
                "-fx-font-size: 10px;"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button filterButton =
                new Button(
                        "☷  Filters"
                );

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
                new Button(
                        "↓  Export"
                );

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
                departmentType,
                spacer,
                filterButton,
                exportButton
        );

        return container;
    }

    // =========================================================
    // DEPARTMENT GRID
    // =========================================================

    private VBox createDepartmentGrid() {

        VBox container =
                new VBox(12);

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
                "-fx-font-size: 14px;" +
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
                        "12 Departments"
                );

        count.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        heading.getChildren().addAll(
                title,
                spacer,
                count
        );

        container.getChildren().add(
                heading
        );

        HBox rowOne =
                new HBox(15);

        rowOne.getChildren().addAll(
                createDepartmentCard(
                        "Cardiology",
                        "Dr. Ananya Sharma",
                        "24 Doctors",
                        "186 Patients",
                        "Clinical",
                        PRIMARY_BLUE,
                        "♥"
                ),
                createDepartmentCard(
                        "Neurology",
                        "Dr. Rahul Patil",
                        "18 Doctors",
                        "142 Patients",
                        "Clinical",
                        PURPLE,
                        "◉"
                ),
                createDepartmentCard(
                        "Orthopedics",
                        "Dr. Amit Joshi",
                        "16 Doctors",
                        "128 Patients",
                        "Surgical",
                        SUCCESS_GREEN,
                        "⌁"
                )
        );

        for (javafx.scene.Node node :
                rowOne.getChildren()) {

            HBox.setHgrow(
                    node,
                    Priority.ALWAYS
            );
        }

        HBox rowTwo =
                new HBox(15);

        rowTwo.getChildren().addAll(
                createDepartmentCard(
                        "Pediatrics",
                        "Dr. Priya Mehta",
                        "14 Doctors",
                        "115 Patients",
                        "Clinical",
                        WARNING_ORANGE,
                        "♧"
                ),
                createDepartmentCard(
                        "Emergency",
                        "Dr. Vikram Singh",
                        "20 Doctors",
                        "94 Cases",
                        "Emergency",
                        ERROR_RED,
                        "!"
                ),
                createDepartmentCard(
                        "General Medicine",
                        "Dr. Neha Kulkarni",
                        "22 Doctors",
                        "203 Patients",
                        "Clinical",
                        PRIMARY_BLUE,
                        "+"
                )
        );

        for (javafx.scene.Node node :
                rowTwo.getChildren()) {

            HBox.setHgrow(
                    node,
                    Priority.ALWAYS
            );
        }

        container.getChildren().addAll(
                rowOne,
                rowTwo
        );

        return container;
    }

    // =========================================================
    // DEPARTMENT CARD
    // =========================================================

    private VBox createDepartmentCard(
            String departmentName,
            String departmentHead,
            String doctorCount,
            String patientCount,
            String type,
            String color,
            String icon
    ) {

        VBox card =
                new VBox(11);

        card.setPadding(
                new Insets(16)
        );

        card.setPrefHeight(190);

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;"
        );

        // -----------------------------------------------------
        // TOP
        // -----------------------------------------------------

        HBox top =
                new HBox();

        top.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle iconCircle =
                new Circle(21);

        iconCircle.setFill(
                Color.web(color + "18")
        );

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + color + ";"
        );

        StackPane iconBox =
                new StackPane(
                        iconCircle,
                        iconLabel
                );

        VBox nameBox =
                new VBox(2);

        Label name =
                new Label(
                        departmentName
                );

        name.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label typeLabel =
                new Label(type);

        typeLabel.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-text-fill: " + color + ";" +
                "-fx-font-weight: bold;"
        );

        nameBox.getChildren().addAll(
                name,
                typeLabel
        );

        HBox.setMargin(
                nameBox,
                new Insets(0, 0, 0, 10)
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button moreButton =
                new Button("•••");

        moreButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";" +
                "-fx-font-size: 13px;" +
                "-fx-cursor: hand;"
        );

        top.getChildren().addAll(
                iconBox,
                nameBox,
                spacer,
                moreButton
        );

        // -----------------------------------------------------
        // SEPARATOR
        // -----------------------------------------------------

        Separator separator =
                new Separator();

        // -----------------------------------------------------
        // DEPARTMENT HEAD
        // -----------------------------------------------------

        Label headTitle =
                new Label("Department Head");

        headTitle.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        Label headName =
                new Label(departmentHead);

        headName.setStyle(
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        // -----------------------------------------------------
        // STATISTICS
        // -----------------------------------------------------

        HBox statistics =
                new HBox(25);

        Label doctors =
                new Label(doctorCount);

        doctors.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-text-fill: " + color + ";" +
                "-fx-font-weight: bold;"
        );

        Label patients =
                new Label(patientCount);

        patients.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";" +
                "-fx-font-weight: bold;"
        );

        statistics.getChildren().addAll(
                doctors,
                patients
        );

        // -----------------------------------------------------
        // ACTION BUTTONS
        // -----------------------------------------------------

        HBox actions =
                new HBox(7);

        Button edit =
                createSmallButton(
                        "Edit",
                        PRIMARY_BLUE
                );

        Button delete =
                createSmallButton(
                        "Delete",
                        ERROR_RED
                );

        actions.getChildren().addAll(
                edit,
                delete
        );

        card.getChildren().addAll(
                top,
                separator,
                headTitle,
                headName,
                statistics,
                actions
        );

        return card;
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
                new Insets(0, 11, 0, 11)
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