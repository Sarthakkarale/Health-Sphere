package com.healthsphere.view.Hospital;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class HospitalAnalyticsView {

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
                        true
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
                        "Search analytics..."
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

        ScrollPane scrollPane =
                new ScrollPane();

        VBox analyticsContent =
                new VBox(18);

        analyticsContent.setPadding(
                new Insets(2, 4, 25, 2)
        );

        analyticsContent.getChildren().add(
                createPageHeader()
        );

        analyticsContent.getChildren().add(
                createKpiCards()
        );

        analyticsContent.getChildren().add(
                createChartsRow()
        );

        analyticsContent.getChildren().add(
                createPerformanceRow()
        );

        analyticsContent.getChildren().add(
                createReportSection()
        );

        scrollPane.setContent(
                analyticsContent
        );

        scrollPane.setFitToWidth(true);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;"
        );

        content.getChildren().add(
                scrollPane
        );

        VBox.setVgrow(
                scrollPane,
                Priority.ALWAYS
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
                        "Hospital Analytics"
                );

        title.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label subtitle =
                new Label(
                        "Monitor hospital performance and operational insights"
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

        ComboBox<String> periodBox =
                new ComboBox<>();

        periodBox.getItems().addAll(
                "This Month",
                "Last Month",
                "Last 3 Months",
                "This Year"
        );

        periodBox.setValue(
                "This Month"
        );

        periodBox.setPrefWidth(130);
        periodBox.setPrefHeight(38);

        Button exportButton =
                new Button(
                        "↓  Export Report"
                );

        exportButton.setPrefHeight(38);

        exportButton.setPadding(
                new Insets(0, 16, 0, 16)
        );

        exportButton.setStyle(
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
                spacer,
                periodBox,
                exportButton
        );

        return header;
    }

    // =========================================================
    // KPI CARDS
    // =========================================================

    private HBox createKpiCards() {

        HBox cards =
                new HBox(15);

        cards.getChildren().add(
                createKpiCard(
                        "Total Revenue",
                        "₹48.6L",
                        "+12.8%",
                        "vs last month",
                        "₹",
                        PRIMARY_BLUE,
                        true
                )
        );

        cards.getChildren().add(
                createKpiCard(
                        "Appointments",
                        "3,842",
                        "+8.4%",
                        "vs last month",
                        "▣",
                        SUCCESS_GREEN,
                        true
                )
        );

        cards.getChildren().add(
                createKpiCard(
                        "Bed Occupancy",
                        "74.2%",
                        "+3.2%",
                        "vs last month",
                        "▥",
                        PURPLE,
                        true
                )
        );

        cards.getChildren().add(
                createKpiCard(
                        "Patients",
                        "12,458",
                        "+15.6%",
                        "vs last month",
                        "♙",
                        WARNING_ORANGE,
                        true
                )
        );

        for (javafx.scene.Node node :
                cards.getChildren()) {

            HBox.setHgrow(
                    node,
                    Priority.ALWAYS
            );
        }

        return cards;
    }

    // =========================================================
    // KPI CARD
    // =========================================================

    private VBox createKpiCard(
            String title,
            String value,
            String percentage,
            String period,
            String icon,
            String color,
            boolean positive
    ) {

        VBox card =
                new VBox(7);

        card.setPadding(
                new Insets(16)
        );

        card.setPrefHeight(120);

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

        Region topSpacer =
                new Region();

        HBox.setHgrow(
                topSpacer,
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
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
        );

        top.getChildren().addAll(
                titleLabel,
                topSpacer,
                iconLabel
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-font-size: 23px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label trend =
                new Label(
                        percentage + "  " + period
                );

        trend.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-text-fill: " +
                (positive
                        ? SUCCESS_GREEN
                        : ERROR_RED) + ";"
        );

        card.getChildren().addAll(
                top,
                valueLabel,
                trend
        );

        return card;
    }

    // =========================================================
    // CHARTS ROW
    // =========================================================

    private HBox createChartsRow() {

        HBox row =
                new HBox(18);

        VBox revenueChart =
                createRevenueChart();

        VBox appointmentChart =
                createAppointmentChart();

        HBox.setHgrow(
                revenueChart,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                appointmentChart,
                Priority.ALWAYS
        );

        row.getChildren().addAll(
                revenueChart,
                appointmentChart
        );

        return row;
    }

    // =========================================================
    // REVENUE CHART
    // =========================================================

    private VBox createRevenueChart() {

        VBox card =
                new VBox(10);

        card.setPrefHeight(310);

        card.setPadding(
                new Insets(18)
        );

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;"
        );

        HBox header =
                createChartHeader(
                        "Revenue Overview",
                        "Monthly revenue performance"
                );

        NumberAxis yAxis =
                new NumberAxis();

        yAxis.setLabel(
                "Revenue (₹ Lakhs)"
        );

        yAxis.setAutoRanging(false);

        yAxis.setLowerBound(0);
        yAxis.setUpperBound(70);
        yAxis.setTickUnit(10);

        CategoryAxis xAxis =
                new CategoryAxis();

        xAxis.setLabel(
                "Month"
        );

        BarChart<String, Number> chart =
                new BarChart<>(
                        xAxis,
                        yAxis
                );

        chart.setLegendVisible(false);

        chart.setAnimated(false);

        chart.setPrefHeight(220);

        chart.setStyle(
                "-fx-background-color: transparent;"
        );

        XYChart.Series<String, Number> series =
                new XYChart.Series<>();

        series.getData().add(
                new XYChart.Data<>(
                        "Jan",
                        32
                )
        );

        series.getData().add(
                new XYChart.Data<>(
                        "Feb",
                        38
                )
        );

        series.getData().add(
                new XYChart.Data<>(
                        "Mar",
                        41
                )
        );

        series.getData().add(
                new XYChart.Data<>(
                        "Apr",
                        47
                )
        );

        series.getData().add(
                new XYChart.Data<>(
                        "May",
                        43
                )
        );

        series.getData().add(
                new XYChart.Data<>(
                        "Jun",
                        48.6
                )
        );

        chart.getData().add(
                series
        );

        card.getChildren().addAll(
                header,
                chart
        );

        VBox.setVgrow(
                chart,
                Priority.ALWAYS
        );

        return card;
    }

    // =========================================================
    // APPOINTMENT CHART
    // =========================================================

    private VBox createAppointmentChart() {

        VBox card =
                new VBox(10);

        card.setPrefHeight(310);

        card.setPadding(
                new Insets(18)
        );

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;"
        );

        HBox header =
                createChartHeader(
                        "Appointment Trends",
                        "Appointments over the last 6 months"
                );

        CategoryAxis xAxis =
                new CategoryAxis();

        xAxis.setLabel(
                "Month"
        );

        NumberAxis yAxis =
                new NumberAxis();

        yAxis.setLabel(
                "Appointments"
        );

        LineChart<String, Number> chart =
                new LineChart<>(
                        xAxis,
                        yAxis
                );

        chart.setLegendVisible(false);

        chart.setAnimated(false);

        chart.setCreateSymbols(true);

        chart.setPrefHeight(220);

        XYChart.Series<String, Number> series =
                new XYChart.Series<>();

        series.getData().add(
                new XYChart.Data<>(
                        "Jan",
                        2480
                )
        );

        series.getData().add(
                new XYChart.Data<>(
                        "Feb",
                        2670
                )
        );

        series.getData().add(
                new XYChart.Data<>(
                        "Mar",
                        2890
                )
        );

        series.getData().add(
                new XYChart.Data<>(
                        "Apr",
                        3150
                )
        );

        series.getData().add(
                new XYChart.Data<>(
                        "May",
                        3540
                )
        );

        series.getData().add(
                new XYChart.Data<>(
                        "Jun",
                        3842
                )
        );

        chart.getData().add(
                series
        );

        card.getChildren().addAll(
                header,
                chart
        );

        VBox.setVgrow(
                chart,
                Priority.ALWAYS
        );

        return card;
    }

    // =========================================================
    // CHART HEADER
    // =========================================================

    private HBox createChartHeader(
            String title,
            String subtitle
    ) {

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox titleBox =
                new VBox(3);

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label subtitleLabel =
                new Label(subtitle);

        subtitleLabel.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        titleBox.getChildren().addAll(
                titleLabel,
                subtitleLabel
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button menu =
                new Button("•••");

        menu.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        header.getChildren().addAll(
                titleBox,
                spacer,
                menu
        );

        return header;
    }

    // =========================================================
    // PERFORMANCE ROW
    // =========================================================

    private HBox createPerformanceRow() {

        HBox row =
                new HBox(18);

        row.getChildren().add(
                createDepartmentPerformance()
        );

        row.getChildren().add(
                createBedUtilization()
        );

        row.getChildren().add(
                createPatientGrowth()
        );

        for (javafx.scene.Node node :
                row.getChildren()) {

            HBox.setHgrow(
                    node,
                    Priority.ALWAYS
            );
        }

        return row;
    }

    // =========================================================
    // DEPARTMENT PERFORMANCE
    // =========================================================

    private VBox createDepartmentPerformance() {

        VBox card =
                new VBox(11);

        card.setPadding(
                new Insets(18)
        );

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;"
        );

        HBox header =
                createSimpleHeader(
                        "Department Performance",
                        "Patient visits by department"
                );

        card.getChildren().add(
                header
        );

        card.getChildren().add(
                createPerformanceItem(
                        "Cardiology",
                        "1,284 visits",
                        0.88,
                        PRIMARY_BLUE
                )
        );

        card.getChildren().add(
                createPerformanceItem(
                        "Orthopedics",
                        "986 visits",
                        0.72,
                        PURPLE
                )
        );

        card.getChildren().add(
                createPerformanceItem(
                        "Neurology",
                        "824 visits",
                        0.61,
                        SUCCESS_GREEN
                )
        );

        card.getChildren().add(
                createPerformanceItem(
                        "Pediatrics",
                        "642 visits",
                        0.48,
                        WARNING_ORANGE
                )
        );

        return card;
    }

    // =========================================================
    // PERFORMANCE ITEM
    // =========================================================

    private VBox createPerformanceItem(
            String name,
            String value,
            double progress,
            String color
    ) {

        VBox item =
                new VBox(4);

        HBox top =
                new HBox();

        Label nameLabel =
                new Label(name);

        nameLabel.setStyle(
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

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        top.getChildren().addAll(
                nameLabel,
                spacer,
                valueLabel
        );

        ProgressBar progressBar =
                new ProgressBar(progress);

        progressBar.setMaxWidth(
                Double.MAX_VALUE
        );

        progressBar.setPrefHeight(6);

        progressBar.setStyle(
                "-fx-accent: " + color + ";"
        );

        item.getChildren().addAll(
                top,
                progressBar
        );

        return item;
    }

    // =========================================================
    // BED UTILIZATION
    // =========================================================

    private VBox createBedUtilization() {

        VBox card =
                new VBox(10);

        card.setPadding(
                new Insets(18)
        );

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;"
        );

        card.getChildren().add(
                createSimpleHeader(
                        "Bed Utilization",
                        "Current occupancy by ward"
                )
        );

        HBox content =
                new HBox(15);

        StackPane circularChart =
                createCircularProgress(
                        74.2,
                        PRIMARY_BLUE
                );

        content.getChildren().add(
                circularChart
        );

        VBox details =
                new VBox(8);

        details.getChildren().add(
                createMiniStat(
                        "General Ward",
                        "75%",
                        PRIMARY_BLUE
                )
        );

        details.getChildren().add(
                createMiniStat(
                        "ICU",
                        "75%",
                        PURPLE
                )
        );

        details.getChildren().add(
                createMiniStat(
                        "Emergency",
                        "25%",
                        ERROR_RED
                )
        );

        details.getChildren().add(
                createMiniStat(
                        "Private",
                        "70%",
                        SUCCESS_GREEN
                )
        );

        content.getChildren().add(
                details
        );

        card.getChildren().add(
                content
        );

        return card;
    }

    // =========================================================
    // CIRCULAR PROGRESS
    // =========================================================

    private StackPane createCircularProgress(
            double percentage,
            String color
    ) {

        StackPane container =
                new StackPane();

        container.setPrefSize(
                125,
                125
        );

        Circle background =
                new Circle(
                        48
                );

        background.setFill(
                Color.TRANSPARENT
        );

        background.setStroke(
                Color.web("#E9EDF3")
        );

        background.setStrokeWidth(
                10
        );

        Arc progress =
                new Arc();

        progress.setCenterX(0);
        progress.setCenterY(0);

        progress.setRadiusX(48);
        progress.setRadiusY(48);

        progress.setStartAngle(90);

        progress.setLength(
                -(percentage / 100.0) * 360
        );

        progress.setType(
                ArcType.OPEN
        );

        progress.setFill(
                Color.TRANSPARENT
        );

        progress.setStroke(
                Color.web(color)
        );

        progress.setStrokeWidth(
                10
        );

        Label value =
                new Label(
                        String.format(
                                "%.1f%%",
                                percentage
                        )
                );

        value.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        VBox center =
                new VBox(1);

        center.setAlignment(
                Pos.CENTER
        );

        Label label =
                new Label("Occupied");

        label.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        center.getChildren().addAll(
                value,
                label
        );

        container.getChildren().addAll(
                background,
                progress,
                center
        );

        return container;
    }

    // =========================================================
    // MINI STAT
    // =========================================================

    private HBox createMiniStat(
            String title,
            String value,
            String color
    ) {

        HBox row =
                new HBox(7);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle dot =
                new Circle(4);

        dot.setFill(
                Color.web(color)
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        row.getChildren().addAll(
                dot,
                titleLabel,
                spacer,
                valueLabel
        );

        return row;
    }

    // =========================================================
    // PATIENT GROWTH
    // =========================================================

    private VBox createPatientGrowth() {

        VBox card =
                new VBox(10);

        card.setPadding(
                new Insets(18)
        );

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;"
        );

        card.getChildren().add(
                createSimpleHeader(
                        "Patient Growth",
                        "Monthly patient registration"
                )
        );

        Label value =
                new Label(
                        "12,458"
                );

        value.setStyle(
                "-fx-font-size: 23px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label growth =
                new Label(
                        "↑ 15.6% growth"
                );

        growth.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-text-fill: " + SUCCESS_GREEN + ";"
        );

        ProgressBar growthBar =
                new ProgressBar(
                        0.78
                );

        growthBar.setMaxWidth(
                Double.MAX_VALUE
        );

        growthBar.setPrefHeight(8);

        growthBar.setStyle(
                "-fx-accent: " + SUCCESS_GREEN + ";"
        );

        HBox monthly =
                new HBox();

        monthly.setAlignment(
                Pos.CENTER_LEFT
        );

        Label monthlyLabel =
                new Label(
                        "Monthly target"
                );

        monthlyLabel.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label target =
                new Label(
                        "16,000"
                );

        target.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        monthly.getChildren().addAll(
                monthlyLabel,
                spacer,
                target
        );

        card.getChildren().addAll(
                value,
                growth,
                growthBar,
                monthly
        );

        return card;
    }

    // =========================================================
    // SIMPLE HEADER
    // =========================================================

    private HBox createSimpleHeader(
            String title,
            String subtitle
    ) {

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox titleBox =
                new VBox(3);

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label subtitleLabel =
                new Label(subtitle);

        subtitleLabel.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        titleBox.getChildren().addAll(
                titleLabel,
                subtitleLabel
        );

        header.getChildren().add(
                titleBox
        );

        return header;
    }

    // =========================================================
    // REPORT SECTION
    // =========================================================

    private VBox createReportSection() {

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

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox titleBox =
                new VBox(3);

        Label title =
                new Label(
                        "Monthly Reports"
                );

        title.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label subtitle =
                new Label(
                        "Hospital performance reports"
                );

        subtitle.setStyle(
                "-fx-font-size: 8px;" +
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

        Button generateButton =
                new Button(
                        "+  Generate Report"
                );

        generateButton.setPrefHeight(34);

        generateButton.setStyle(
                "-fx-background-color: #EAF1FF;" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";" +
                "-fx-background-radius: 7;" +
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        header.getChildren().addAll(
                titleBox,
                spacer,
                generateButton
        );

        card.getChildren().add(
                header
        );

        card.getChildren().add(
                new Separator()
        );

        GridPane reports =
                new GridPane();

        reports.setHgap(12);
        reports.setVgap(10);

        reports.add(
                createReportItem(
                        "June 2026",
                        "Hospital Performance Report",
                        "Generated",
                        SUCCESS_GREEN
                ),
                0,
                0
        );

        reports.add(
                createReportItem(
                        "May 2026",
                        "Monthly Analytics Report",
                        "Generated",
                        SUCCESS_GREEN
                ),
                1,
                0
        );

        reports.add(
                createReportItem(
                        "April 2026",
                        "Operational Summary",
                        "Generated",
                        SUCCESS_GREEN
                ),
                0,
                1
        );

        reports.add(
                createReportItem(
                        "March 2026",
                        "Financial Analytics",
                        "Generated",
                        SUCCESS_GREEN
                ),
                1,
                1
        );

        GridPane.setHgrow(
                reports.getChildren().get(0),
                Priority.ALWAYS
        );

        card.getChildren().add(
                reports
        );

        return card;
    }

    // =========================================================
    // REPORT ITEM
    // =========================================================

    private HBox createReportItem(
            String month,
            String reportName,
            String status,
            String statusColor
    ) {

        HBox item =
                new HBox(10);

        item.setAlignment(
                Pos.CENTER_LEFT
        );

        item.setPadding(
                new Insets(10)
        );

        item.setStyle(
                "-fx-background-color: #F8F9FC;" +
                "-fx-background-radius: 8;"
        );

        Label documentIcon =
                new Label("▤");

        documentIcon.setPrefSize(
                30,
                30
        );

        documentIcon.setAlignment(
                Pos.CENTER
        );

        documentIcon.setStyle(
                "-fx-background-color: #E8F0FF;" +
                "-fx-background-radius: 7;" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";" +
                "-fx-font-size: 12px;"
        );

        VBox details =
                new VBox(3);

        Label monthLabel =
                new Label(month);

        monthLabel.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label reportLabel =
                new Label(reportName);

        reportLabel.setStyle(
                "-fx-font-size: 7px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        details.getChildren().addAll(
                monthLabel,
                reportLabel
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label statusLabel =
                new Label(status);

        statusLabel.setStyle(
                "-fx-background-color: " +
                statusColor + "18;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 4 8;" +
                "-fx-text-fill: " +
                statusColor + ";" +
                "-fx-font-size: 7px;" +
                "-fx-font-weight: bold;"
        );

        Button downloadButton =
                new Button("↓");

        downloadButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";" +
                "-fx-font-size: 13px;" +
                "-fx-cursor: hand;"
        );

        item.getChildren().addAll(
                documentIcon,
                details,
                spacer,
                statusLabel,
                downloadButton
        );

        return item;
    }
}