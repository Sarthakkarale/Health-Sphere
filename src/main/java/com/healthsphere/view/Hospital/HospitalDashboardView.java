package com.healthsphere.view.Hospital;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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

public class HospitalDashboardView {

    // =========================================================
    // COLORS
    // =========================================================

    private static final String PRIMARY_BLUE = "#0756C9";
    private static final String DARK_TEXT = "#18212F";
    private static final String SECONDARY_TEXT = "#667085";
    private static final String LIGHT_BACKGROUND = "#F7F8FC";
    private static final String BORDER = "#E1E5ED";
    private static final String SUCCESS_GREEN = "#16856F";
    private static final String WARNING_ORANGE = "#E58A00";
    private static final String ERROR_RED = "#D64545";
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

        Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());

        return scene;
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
        // NAVIGATION
        // -----------------------------------------------------

        Button dashboardButton =
                createNavigationButton(
                        "▦",
                        "Dashboard",
                        true
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
        // DIRECT NAVIGATION
        // -----------------------------------------------------

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
        // HELP
        // -----------------------------------------------------

        Button helpButton =
                createNavigationButton(
                        "?",
                        "Help Center",
                        false
                );

        sidebar.getChildren().add(helpButton);

        // -----------------------------------------------------
        // LOGOUT
        // -----------------------------------------------------

        Button logoutButton =
                createNavigationButton(
                        "↪",
                        "Logout",
                        false
                );

        sidebar.getChildren().add(logoutButton);

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

        // Search
        Label searchIcon = new Label("⌕");

        searchIcon.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        Label searchText = new Label(
                "Search patients, doctors, records..."
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

        Label administrator = new Label(
                "Hospital Administrator"
        );

        administrator.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label role = new Label(
                "HOSPITAL ADMIN"
        );

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

        Circle avatar = new Circle(18);

        avatar.setFill(
                Color.web("#DCE8F8")
        );

        Label avatarText = new Label("HA");

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
                createHeader()
        );

        content.getChildren().add(
                createKpiCards()
        );

        content.getChildren().add(
                createMiddleSection()
        );

        content.getChildren().add(
                createBottomSection()
        );

        return content;
    }

    // =========================================================
    // HEADER
    // =========================================================

    private VBox createHeader() {

        VBox header = new VBox(4);

        Label title = new Label(
                "Hospital Dashboard"
        );

        title.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label subtitle = new Label(
                "Overview of hospital operations and today's activities"
        );

        subtitle.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        header.getChildren().addAll(
                title,
                subtitle
        );

        return header;
    }

    // =========================================================
    // KPI CARDS
    // =========================================================

    private HBox createKpiCards() {

        HBox cards = new HBox(15);

        cards.getChildren().add(
                createKpiCard(
                        "Total Doctors",
                        "128",
                        "+8.4%",
                        "♙",
                        PRIMARY_BLUE
                )
        );

        cards.getChildren().add(
                createKpiCard(
                        "Today's Appointments",
                        "86",
                        "+12.5%",
                        "▣",
                        PURPLE
                )
        );

        cards.getChildren().add(
                createKpiCard(
                        "Available Beds",
                        "42",
                        "18 available",
                        "▥",
                        SUCCESS_GREEN
                )
        );

        cards.getChildren().add(
                createKpiCard(
                        "Emergency Cases",
                        "07",
                        "3 critical",
                        "!",
                        ERROR_RED
                )
        );

        for (javafx.scene.Node node : cards.getChildren()) {

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
            String trend,
            String icon,
            String iconColor
    ) {

        VBox card = new VBox(10);

        card.setPadding(
                new Insets(18)
        );

        card.setMinHeight(125);

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

        iconLabel.setAlignment(
                Pos.CENTER
        );

        iconLabel.setPrefSize(
                32,
                32
        );

        iconLabel.setStyle(
                "-fx-background-color: " +
                iconColor + "18;" +
                "-fx-background-radius: 8;" +
                "-fx-text-fill: " +
                iconColor + ";" +
                "-fx-font-size: 17px;" +
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
                "-fx-font-size: 25px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label trendLabel =
                new Label(trend);

        trendLabel.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-text-fill: " + iconColor + ";" +
                "-fx-font-weight: bold;"
        );

        card.getChildren().addAll(
                top,
                valueLabel,
                trendLabel
        );

        return card;
    }

    // =========================================================
    // MIDDLE SECTION
    // =========================================================

    private HBox createMiddleSection() {

        HBox section = new HBox(18);

        section.getChildren().add(
                createAppointmentOverview()
        );

        section.getChildren().add(
                createBedOccupancy()
        );

        HBox.setHgrow(
                section.getChildren().get(0),
                Priority.ALWAYS
        );

        HBox.setHgrow(
                section.getChildren().get(1),
                Priority.ALWAYS
        );

        return section;
    }

    // =========================================================
    // APPOINTMENT OVERVIEW
    // =========================================================

    private VBox createAppointmentOverview() {

        VBox card = createCard();

        HBox heading = createCardHeading(
                "Appointment Overview",
                "Today's appointment status"
        );

        card.getChildren().add(heading);

        GridPane grid = new GridPane();

        grid.setHgap(12);
        grid.setVgap(12);

        grid.add(
                createStatusBox(
                        "Completed",
                        "42",
                        SUCCESS_GREEN
                ),
                0,
                0
        );

        grid.add(
                createStatusBox(
                        "Waiting",
                        "18",
                        WARNING_ORANGE
                ),
                1,
                0
        );

        grid.add(
                createStatusBox(
                        "Upcoming",
                        "19",
                        PRIMARY_BLUE
                ),
                0,
                1
        );

        grid.add(
                createStatusBox(
                        "Cancelled",
                        "07",
                        ERROR_RED
                ),
                1,
                1
        );

        card.getChildren().add(grid);

        return card;
    }

    // =========================================================
    // STATUS BOX
    // =========================================================

    private VBox createStatusBox(
            String title,
            String value,
            String color
    ) {

        VBox box = new VBox(5);

        box.setPadding(
                new Insets(13)
        );

        box.setStyle(
                "-fx-background-color: #F8FAFD;" +
                "-fx-background-radius: 9;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 9;"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + color + ";"
        );

        box.getChildren().addAll(
                titleLabel,
                valueLabel
        );

        return box;
    }

    // =========================================================
    // BED OCCUPANCY
    // =========================================================

    private VBox createBedOccupancy() {

        VBox card = createCard();

        card.setPrefWidth(400);

        HBox heading = createCardHeading(
                "Bed Occupancy",
                "Current hospital capacity"
        );

        card.getChildren().add(heading);

        // Progress bar
        StackPane progressContainer =
                new StackPane();

        progressContainer.setPrefHeight(20);

        Region background =
                new Region();

        background.setMaxWidth(
                Double.MAX_VALUE
        );

        background.setPrefHeight(10);

        background.setStyle(
                "-fx-background-color: #E8ECF2;" +
                "-fx-background-radius: 10;"
        );

        Region progress =
                new Region();

        progress.setPrefWidth(250);
        progress.setPrefHeight(10);

        progress.setStyle(
                "-fx-background-color: " +
                PRIMARY_BLUE + ";" +
                "-fx-background-radius: 10;"
        );

        StackPane.setAlignment(
                progress,
                Pos.CENTER_LEFT
        );

        progressContainer.getChildren().addAll(
                background,
                progress
        );

        card.getChildren().add(
                progressContainer
        );

        Label occupancy =
                new Label(
                        "72% Occupied"
                );

        occupancy.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        card.getChildren().add(
                occupancy
        );

        card.getChildren().add(
                createBedRow(
                        "General Ward",
                        "48 / 70",
                        PRIMARY_BLUE
                )
        );

        card.getChildren().add(
                createBedRow(
                        "ICU",
                        "18 / 25",
                        ERROR_RED
                )
        );

        card.getChildren().add(
                createBedRow(
                        "Emergency",
                        "08 / 15",
                        WARNING_ORANGE
                )
        );

        return card;
    }

    // =========================================================
    // BED ROW
    // =========================================================

    private HBox createBedRow(
            String name,
            String count,
            String color
    ) {

        HBox row = new HBox();

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        Label nameLabel =
                new Label(name);

        nameLabel.setStyle(
                "-fx-font-size: 10px;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Region spacer = new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label countLabel =
                new Label(count);

        countLabel.setStyle(
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + color + ";"
        );

        row.getChildren().addAll(
                nameLabel,
                spacer,
                countLabel
        );

        return row;
    }

    // =========================================================
    // BOTTOM SECTION
    // =========================================================

    private HBox createBottomSection() {

        HBox section = new HBox(18);

        section.getChildren().add(
                createDepartmentStatistics()
        );

        section.getChildren().add(
                createRecentActivities()
        );

        section.getChildren().add(
                createQuickActions()
        );

        HBox.setHgrow(
                section.getChildren().get(0),
                Priority.ALWAYS
        );

        HBox.setHgrow(
                section.getChildren().get(1),
                Priority.ALWAYS
        );

        HBox.setHgrow(
                section.getChildren().get(2),
                Priority.ALWAYS
        );

        return section;
    }

    // =========================================================
    // DEPARTMENT STATISTICS
    // =========================================================

    private VBox createDepartmentStatistics() {

        VBox card = createCard();

        card.getChildren().add(
                createCardHeading(
                        "Department Statistics",
                        "Doctors by department"
                )
        );

        card.getChildren().add(
                createDepartmentRow(
                        "Cardiology",
                        "24",
                        PRIMARY_BLUE
                )
        );

        card.getChildren().add(
                createDepartmentRow(
                        "Neurology",
                        "18",
                        PURPLE
                )
        );

        card.getChildren().add(
                createDepartmentRow(
                        "Orthopedics",
                        "16",
                        SUCCESS_GREEN
                )
        );

        card.getChildren().add(
                createDepartmentRow(
                        "Pediatrics",
                        "14",
                        WARNING_ORANGE
                )
        );

        return card;
    }

    // =========================================================
    // DEPARTMENT ROW
    // =========================================================

    private HBox createDepartmentRow(
            String department,
            String doctors,
            String color
    ) {

        HBox row = new HBox(10);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle dot = new Circle(5);

        dot.setFill(
                Color.web(color)
        );

        Label name =
                new Label(department);

        name.setStyle(
                "-fx-font-size: 10px;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Region spacer = new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label doctorCount =
                new Label(
                        doctors + " Doctors"
                );

        doctorCount.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        row.getChildren().addAll(
                dot,
                name,
                spacer,
                doctorCount
        );

        return row;
    }

    // =========================================================
    // RECENT ACTIVITIES
    // =========================================================

    private VBox createRecentActivities() {

        VBox card = createCard();

        card.getChildren().add(
                createCardHeading(
                        "Recent Activities",
                        "Latest hospital updates"
                )
        );

        card.getChildren().add(
                createActivity(
                        "Dr. Sharma added a new appointment",
                        "10 minutes ago",
                        PRIMARY_BLUE
                )
        );

        card.getChildren().add(
                createActivity(
                        "Bed #ICU-08 is now available",
                        "25 minutes ago",
                        SUCCESS_GREEN
                )
        );

        card.getChildren().add(
                createActivity(
                        "Emergency case admitted",
                        "42 minutes ago",
                        ERROR_RED
                )
        );

        card.getChildren().add(
                createActivity(
                        "New doctor profile updated",
                        "1 hour ago",
                        PURPLE
                )
        );

        return card;
    }

    // =========================================================
    // ACTIVITY
    // =========================================================

    private HBox createActivity(
            String text,
            String time,
            String color
    ) {

        HBox row = new HBox(9);

        row.setAlignment(
                Pos.TOP_LEFT
        );

        Circle dot = new Circle(4);

        dot.setFill(
                Color.web(color)
        );

        VBox info =
                new VBox(3);

        Label textLabel =
                new Label(text);

        textLabel.setWrapText(true);

        textLabel.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label timeLabel =
                new Label(time);

        timeLabel.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        info.getChildren().addAll(
                textLabel,
                timeLabel
        );

        row.getChildren().addAll(
                dot,
                info
        );

        return row;
    }

    // =========================================================
    // QUICK ACTIONS
    // =========================================================

    private VBox createQuickActions() {

        VBox card = createCard();

        card.getChildren().add(
                createCardHeading(
                        "Quick Actions",
                        "Common hospital operations"
                )
        );

        Button addDoctor =
                createActionButton(
                        "＋  Add Doctor",
                        PRIMARY_BLUE
                );

        Button appointment =
                createActionButton(
                        "▣  New Appointment",
                        PURPLE
                );

        Button manageBeds =
                createActionButton(
                        "▥  Manage Beds",
                        SUCCESS_GREEN
                );

        Button analytics =
                createActionButton(
                        "◈  View Analytics",
                        WARNING_ORANGE
                );

        card.getChildren().addAll(
                addDoctor,
                appointment,
                manageBeds,
                analytics
        );

        return card;
    }

    // =========================================================
    // QUICK ACTION BUTTON
    // =========================================================

    private Button createActionButton(
            String text,
            String color
    ) {

        Button button =
                new Button(text);

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setPrefHeight(37);

        button.setAlignment(
                Pos.CENTER_LEFT
        );

        button.setStyle(
                "-fx-background-color: " +
                color + "12;" +
                "-fx-text-fill: " +
                color + ";" +
                "-fx-background-radius: 7;" +
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        return button;
    }

    // =========================================================
    // CARD HEADING
    // =========================================================

    private HBox createCardHeading(
            String title,
            String subtitle
    ) {

        HBox heading =
                new HBox();

        heading.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox text =
                new VBox(2);

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
                "-fx-font-size: 9px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        text.getChildren().addAll(
                titleLabel,
                subtitleLabel
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label more =
                new Label("•••");

        more.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        heading.getChildren().addAll(
                text,
                spacer,
                more
        );

        return heading;
    }

    // =========================================================
    // COMMON CARD
    // =========================================================

    private VBox createCard() {

        VBox card =
                new VBox(15);

        card.setPadding(
                new Insets(18)
        );

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;"
        );

        return card;
    }
}