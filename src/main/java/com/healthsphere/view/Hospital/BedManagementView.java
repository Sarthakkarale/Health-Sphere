package com.healthsphere.view.Hospital;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
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

public class BedManagementView {

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

        Label subtitle = new Label(
                "SMART HEALTHCARE"
        );

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
                        true
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
        // SPACER
        // -----------------------------------------------------

        Region sidebarSpacer = new Region();

        VBox.setVgrow(
                sidebarSpacer,
                Priority.ALWAYS
        );

        sidebar.getChildren().add(
                sidebarSpacer
        );

        // -----------------------------------------------------
        // BOTTOM BUTTONS
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
                (selected
                        ? "bold"
                        : "normal") + ";" +
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
                        "Search beds, wards..."
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

        Region topSpacer =
                new Region();

        HBox.setHgrow(
                topSpacer,
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
                topSpacer,
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
                createFilterBar()
        );

        HBox lowerContent =
                new HBox(18);

        HBox.setHgrow(
                createWardOverview(),
                Priority.ALWAYS
        );

        lowerContent.getChildren().addAll(
                createBedOverview(),
                createWardOverview()
        );

        content.getChildren().add(
                lowerContent
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
                        "Bed Management"
                );

        title.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label subtitle =
                new Label(
                        "Monitor hospital beds, wards and availability"
                );

        subtitle.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        titleBox.getChildren().addAll(
                title,
                subtitle
        );

        Region headerSpacer =
                new Region();

        HBox.setHgrow(
                headerSpacer,
                Priority.ALWAYS
        );

        Button manageWards =
                new Button(
                        "Manage Wards"
                );

        manageWards.setPrefHeight(40);

        manageWards.setPadding(
                new Insets(0, 18, 0, 18)
        );

        manageWards.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-text-fill: " + DARK_TEXT + ";" +
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        Button addBed =
                new Button(
                        "+  Add Bed"
                );

        addBed.setPrefHeight(40);

        addBed.setPadding(
                new Insets(0, 18, 0, 18)
        );

        addBed.setStyle(
                "-fx-background-color: " +
                PRIMARY_BLUE + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        header.getChildren().addAll(
                titleBox,
                headerSpacer,
                manageWards,
                addBed
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
                        "Total Beds",
                        "520",
                        "Hospital capacity",
                        "▥",
                        PRIMARY_BLUE
                )
        );

        statistics.getChildren().add(
                createStatisticCard(
                        "Occupied",
                        "386",
                        "74.2% occupancy",
                        "●",
                        ERROR_RED
                )
        );

        statistics.getChildren().add(
                createStatisticCard(
                        "Available",
                        "134",
                        "Beds ready for patients",
                        "✓",
                        SUCCESS_GREEN
                )
        );

        statistics.getChildren().add(
                createStatisticCard(
                        "ICU Beds",
                        "48",
                        "36 occupied",
                        "♥",
                        PURPLE
                )
        );

        statistics.getChildren().add(
                createStatisticCard(
                        "Emergency Beds",
                        "24",
                        "18 available",
                        "!",
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
                new VBox(7);

        card.setPadding(
                new Insets(15)
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
                "-fx-font-size: 9px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        Region cardSpacer =
                new Region();

        HBox.setHgrow(
                cardSpacer,
                Priority.ALWAYS
        );

        Label iconLabel =
                new Label(icon);

        iconLabel.setPrefSize(
                29,
                29
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
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
        );

        top.getChildren().addAll(
                titleLabel,
                cardSpacer,
                iconLabel
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-font-size: 22px;" +
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
    // FILTER BAR
    // =========================================================

    private HBox createFilterBar() {

        HBox filterBar =
                new HBox(10);

        filterBar.setAlignment(
                Pos.CENTER_LEFT
        );

        filterBar.setPadding(
                new Insets(13)
        );

        filterBar.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 10;"
        );

        TextField searchField =
                new TextField();

        searchField.setPromptText(
                "Search bed number or patient..."
        );

        searchField.setPrefWidth(270);
        searchField.setPrefHeight(37);

        searchField.setStyle(
                "-fx-background-color: #F7F8FC;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 7;" +
                "-fx-background-radius: 7;" +
                "-fx-padding: 0 12;" +
                "-fx-font-size: 10px;"
        );

        ComboBox<String> wardFilter =
                new ComboBox<>();

        wardFilter.getItems().addAll(
                "All Wards",
                "General Ward",
                "ICU",
                "Emergency",
                "Private Ward",
                "Pediatric Ward"
        );

        wardFilter.setValue(
                "All Wards"
        );

        wardFilter.setPrefWidth(145);
        wardFilter.setPrefHeight(37);

        ComboBox<String> statusFilter =
                new ComboBox<>();

        statusFilter.getItems().addAll(
                "All Status",
                "Available",
                "Occupied",
                "Reserved",
                "Maintenance"
        );

        statusFilter.setValue(
                "All Status"
        );

        statusFilter.setPrefWidth(135);
        statusFilter.setPrefHeight(37);

        Region filterSpacer =
                new Region();

        HBox.setHgrow(
                filterSpacer,
                Priority.ALWAYS
        );

        Button filterButton =
                new Button(
                        "☷  Filters"
                );

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
                new Button(
                        "↓  Export"
                );

        exportButton.setPrefHeight(37);

        exportButton.setStyle(
                "-fx-background-color: #F1F5FB;" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";" +
                "-fx-background-radius: 7;" +
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        filterBar.getChildren().addAll(
                searchField,
                wardFilter,
                statusFilter,
                filterSpacer,
                filterButton,
                exportButton
        );

        return filterBar;
    }

    // =========================================================
    // BED OVERVIEW
    // =========================================================

    private VBox createBedOverview() {

        VBox card =
                new VBox(15);

        card.setPrefWidth(520);

        card.setPadding(
                new Insets(18)
        );

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;"
        );

        HBox heading =
                new HBox();

        heading.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox headingText =
                new VBox(3);

        Label title =
                new Label(
                        "Bed Availability"
                );

        title.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label subtitle =
                new Label(
                        "Current hospital bed status"
                );

        subtitle.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        headingText.getChildren().addAll(
                title,
                subtitle
        );

        Region headingSpacer =
                new Region();

        HBox.setHgrow(
                headingSpacer,
                Priority.ALWAYS
        );

        Label occupancy =
                new Label(
                        "74.2%"
                );

        occupancy.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";"
        );

        heading.getChildren().addAll(
                headingText,
                headingSpacer,
                occupancy
        );

        card.getChildren().add(
                heading
        );

        // -----------------------------------------------------
        // OCCUPANCY BAR
        // -----------------------------------------------------

        ProgressBar occupancyBar =
                new ProgressBar(0.742);

        occupancyBar.setMaxWidth(
                Double.MAX_VALUE
        );

        occupancyBar.setPrefHeight(10);

        occupancyBar.setStyle(
                "-fx-accent: " + PRIMARY_BLUE + ";"
        );

        card.getChildren().add(
                occupancyBar
        );

        // -----------------------------------------------------
        // LEGEND
        // -----------------------------------------------------

        HBox legend =
                new HBox(20);

        legend.getChildren().add(
                createLegendItem(
                        "Occupied",
                        "386",
                        ERROR_RED
                )
        );

        legend.getChildren().add(
                createLegendItem(
                        "Available",
                        "134",
                        SUCCESS_GREEN
                )
        );

        legend.getChildren().add(
                createLegendItem(
                        "Reserved",
                        "18",
                        WARNING_ORANGE
                )
        );

        card.getChildren().add(
                legend
        );

        card.getChildren().add(
                new Separator()
        );

        // -----------------------------------------------------
        // BED CATEGORIES
        // -----------------------------------------------------

        card.getChildren().add(
                createBedCategory(
                        "General Ward",
                        "180 / 240",
                        0.75,
                        PRIMARY_BLUE
                )
        );

        card.getChildren().add(
                createBedCategory(
                        "ICU",
                        "36 / 48",
                        0.75,
                        PURPLE
                )
        );

        card.getChildren().add(
                createBedCategory(
                        "Emergency",
                        "06 / 24",
                        0.25,
                        ERROR_RED
                )
        );

        card.getChildren().add(
                createBedCategory(
                        "Private Ward",
                        "84 / 120",
                        0.70,
                        SUCCESS_GREEN
                )
        );

        return card;
    }

    // =========================================================
    // LEGEND ITEM
    // =========================================================

    private HBox createLegendItem(
            String title,
            String value,
            String color
    ) {

        HBox item =
                new HBox(6);

        item.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle dot =
                new Circle(4);

        dot.setFill(
                Color.web(color)
        );

        Label text =
                new Label(
                        title + " " + value
                );

        text.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        item.getChildren().addAll(
                dot,
                text
        );

        return item;
    }

    // =========================================================
    // BED CATEGORY
    // =========================================================

    private VBox createBedCategory(
            String title,
            String count,
            double progress,
            String color
    ) {

        VBox container =
                new VBox(5);

        HBox top =
                new HBox();

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label countLabel =
                new Label(count);

        countLabel.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        top.getChildren().addAll(
                titleLabel,
                spacer,
                countLabel
        );

        ProgressBar progressBar =
                new ProgressBar(progress);

        progressBar.setMaxWidth(
                Double.MAX_VALUE
        );

        progressBar.setPrefHeight(7);

        progressBar.setStyle(
                "-fx-accent: " + color + ";"
        );

        container.getChildren().addAll(
                top,
                progressBar
        );

        return container;
    }

    // =========================================================
    // WARD OVERVIEW
    // =========================================================

    private VBox createWardOverview() {

        VBox card =
                new VBox(12);

        card.setPadding(
                new Insets(18)
        );

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;"
        );

        HBox heading =
                new HBox();

        heading.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox headingText =
                new VBox(3);

        Label title =
                new Label(
                        "Ward Management"
                );

        title.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label subtitle =
                new Label(
                        "Ward-wise bed availability"
                );

        subtitle.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        headingText.getChildren().addAll(
                title,
                subtitle
        );

        Region headingSpacer =
                new Region();

        HBox.setHgrow(
                headingSpacer,
                Priority.ALWAYS
        );

        Button viewAll =
                new Button(
                        "View All"
                );

        viewAll.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";" +
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        heading.getChildren().addAll(
                headingText,
                headingSpacer,
                viewAll
        );

        card.getChildren().add(
                heading
        );

        // -----------------------------------------------------
        // WARD ROWS
        // -----------------------------------------------------

        card.getChildren().add(
                createWardRow(
                        "General Ward",
                        "240 Beds",
                        "60 Available",
                        SUCCESS_GREEN,
                        "75%"
                )
        );

        card.getChildren().add(
                createWardRow(
                        "ICU",
                        "48 Beds",
                        "12 Available",
                        PURPLE,
                        "75%"
                )
        );

        card.getChildren().add(
                createWardRow(
                        "Emergency",
                        "24 Beds",
                        "18 Available",
                        WARNING_ORANGE,
                        "25%"
                )
        );

        card.getChildren().add(
                createWardRow(
                        "Private Ward",
                        "120 Beds",
                        "36 Available",
                        PRIMARY_BLUE,
                        "70%"
                )
        );

        card.getChildren().add(
                createWardRow(
                        "Pediatric Ward",
                        "60 Beds",
                        "08 Available",
                        ERROR_RED,
                        "87%"
                )
        );

        return card;
    }

    // =========================================================
    // WARD ROW
    // =========================================================

    private HBox createWardRow(
            String wardName,
            String total,
            String available,
            String color,
            String occupancy
    ) {

        HBox row =
                new HBox(10);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(9, 0, 9, 0)
        );

        Circle statusCircle =
                new Circle(18);

        statusCircle.setFill(
                Color.web(color + "18")
        );

        Label wardIcon =
                new Label("▥");

        wardIcon.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: " + color + ";"
        );

        StackPane iconBox =
                new StackPane(
                        statusCircle,
                        wardIcon
                );

        VBox wardInfo =
                new VBox(3);

        Label name =
                new Label(wardName);

        name.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label totalLabel =
                new Label(total);

        totalLabel.setStyle(
                "-fx-font-size: 7px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        wardInfo.getChildren().addAll(
                name,
                totalLabel
        );

        Region rowSpacer =
                new Region();

        HBox.setHgrow(
                rowSpacer,
                Priority.ALWAYS
        );

        VBox availability =
                new VBox(2);

        availability.setAlignment(
                Pos.CENTER_RIGHT
        );

        Label availableLabel =
                new Label(available);

        availableLabel.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + color + ";"
        );

        Label occupancyLabel =
                new Label(
                        occupancy + " occupied"
                );

        occupancyLabel.setStyle(
                "-fx-font-size: 7px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        availability.getChildren().addAll(
                availableLabel,
                occupancyLabel
        );

        row.getChildren().addAll(
                iconBox,
                wardInfo,
                rowSpacer,
                availability
        );

        return row;
    }
}