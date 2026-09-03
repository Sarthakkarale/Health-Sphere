package com.healthsphere.view.hospital;

import com.healthsphere.controller.hospital.BedController;
import com.healthsphere.controller.hospital.WardController;
import com.healthsphere.model.HospitalBed;
import com.healthsphere.model.HospitalWard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Hospital Ward Management.
 *
 * Firestore flow:
 *
 * ManageWardsView
 *       ↓
 * WardController / BedController
 *       ↓
 * WardDAO / BedDAO
 *       ↓
 * Firestore
 *
 * Clicking a ward opens BedManagementView for that ward.
 */
public class ManageWardsView {

    // =========================================================
    // COLORS
    // =========================================================

    private static final String PRIMARY_BLUE = "#1920DF";
    private static final String PRIMARY_LIGHT = "#EFF5FF";

    private static final String DARK_TEXT = "#0F172A";
    private static final String SECONDARY_TEXT = "#64748B";

    private static final String LIGHT_BACKGROUND = "#F8FAFC";
    private static final String CARD_BG = "#FFFFFF";
    private static final String BORDER = "#E2E8F0";

    private static final String SIDEBAR_BG = "#0F172A";
    private static final String SIDEBAR_HOVER = "#1E293B";
    private static final String SIDEBAR_TEXT = "#94A3B8";

    private static final String SUCCESS_GREEN = "#059669";
    private static final String SUCCESS_LIGHT = "#ECFDF5";

    private static final String ERROR_RED = "#DC2626";
    private static final String ERROR_LIGHT = "#FEF2F2";

    private static final String WARNING_ORANGE = "#D97706";
    private static final String WARNING_LIGHT = "#FFFBEB";

    private static final String PURPLE = "#7C3AED";
    private static final String PURPLE_LIGHT = "#F5F3FF";

    // =========================================================
    // CONTROLLERS
    // =========================================================

    private final WardController wardController;
    private final BedController bedController;

    // =========================================================
    // DATA
    // =========================================================

    private final List<HospitalWard> wardList =
            new ArrayList<>();

    private final List<HospitalBed> bedList =
            new ArrayList<>();

    // =========================================================
    // UI
    // =========================================================

    private VBox wardRowsContainer;

    private Label totalWardsLabel;
    private Label totalCapacityLabel;
    private Label availableBedsLabel;
    private Label occupiedBedsLabel;
    private Label wardCountLabel;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ManageWardsView() {

        wardController =
                new WardController();

        bedController =
                new BedController();
    }

    // =========================================================
    // CREATE SCENE
    // =========================================================

    public Scene createScene(Stage stage) {

        BorderPane root =
                new BorderPane();

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

        VBox mainContent =
                createMainContent(stage);

        ScrollPane scrollPane =
                new ScrollPane(
                        mainContent
                );

        scrollPane.setFitToWidth(
                true
        );

        scrollPane.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-background: transparent;"
                        + "-fx-padding: 0;"
        );

        root.setCenter(
                scrollPane
        );

        /*
         * Load Firestore data after UI controls
         * have been created.
         */
        loadData();

        return new Scene(
                root,
                stage.getWidth(),
                stage.getHeight()
        );
    }

    // =========================================================
    // LOAD DATA
    // =========================================================

    private void loadData() {

        try {

            wardList.clear();
            bedList.clear();

            List<HospitalWard> wards =
                    wardController.getAllWards();

            if (wards != null) {

                wardList.addAll(
                        wards
                );
            }

            List<HospitalBed> beds =
                    bedController.getAllBeds();

            if (beds != null) {

                bedList.addAll(
                        beds
                );
            }

            updateStatistics();

            renderWardRows();

        } catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Unable to Load Wards",
                    getErrorMessage(e)
            );
        }
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
                new Insets(
                        12,
                        28,
                        12,
                        28
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

        Label searchIcon =
                new Label("⌕");

        searchIcon.setStyle(
                "-fx-font-size: 20px;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        TextField searchField =
                new TextField();

        searchField.setPromptText(
                "Search beds, wards..."
        );

        searchField.setPrefWidth(
                330
        );

        searchField.setPrefHeight(
                40
        );

        searchField.setStyle(
                "-fx-background-color: "
                        + LIGHT_BACKGROUND
                        + ";"
                        + "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-radius: 8;"
                        + "-fx-background-radius: 8;"
                        + "-fx-padding: 0 12;"
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
                "-fx-font-size: 17px;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        Label settings =
                new Label("⚙");

        settings.setStyle(
                "-fx-font-size: 18px;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        VBox user =
                new VBox(2);

        user.setAlignment(
                Pos.CENTER_RIGHT
        );

        Label admin =
                new Label(
                        "Hospital Administrator"
                );

        admin.setStyle(
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

        user.getChildren().addAll(
                admin,
                role
        );

        Label avatar =
                new Label("HA");

        avatar.setAlignment(
                Pos.CENTER
        );

        avatar.setPrefSize(
                38,
                38
        );

        avatar.setStyle(
                "-fx-background-color: "
                        + PRIMARY_LIGHT
                        + ";"
                        + "-fx-background-radius: 50;"
                        + "-fx-text-fill: "
                        + PRIMARY_BLUE
                        + ";"
                        + "-fx-font-weight: bold;"
        );

        topBar.getChildren().addAll(
                searchIcon,
                searchField,
                spacer,
                notification,
                settings,
                user,
                avatar
        );

        return topBar;
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private VBox createSidebar(
            Stage stage) {

        VBox sidebar =
                new VBox(6);

        sidebar.setPrefWidth(
                240
        );

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
        );

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
                        + "-fx-text-fill: white;"
        );

        Label subtitle =
                new Label(
                        "SMART HEALTHCARE"
                );

        subtitle.setStyle(
                "-fx-font-size: 9px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + SIDEBAR_TEXT
                        + ";"
        );

        logoBox.getChildren().addAll(
                logo,
                subtitle
        );

        sidebar.getChildren().add(
                logoBox
        );

        Button dashboard =
                createNavigationButton(
                        "▦",
                        "Dashboard",
                        false
                );

        Button doctors =
                createNavigationButton(
                        "♙",
                        "Doctors",
                        false
                );

        Button departments =
                createNavigationButton(
                        "✚",
                        "Departments",
                        false
                );

        Button beds =
                createNavigationButton(
                        "▥",
                        "Beds",
                        true
                );

        Button appointments =
                createNavigationButton(
                        "▣",
                        "Appointments",
                        false
                );

        Button analytics =
                createNavigationButton(
                        "◈",
                        "Analytics",
                        false
                );

        Button hospitalSettings =
                createNavigationButton(
                        "⚙",
                        "Hospital Settings",
                        false
                );

        sidebar.getChildren().addAll(
                dashboard,
                doctors,
                departments,
                beds,
                appointments,
                analytics,
                hospitalSettings
        );

        dashboard.setOnAction(
                e -> stage.setScene(
                        new HospitalDashboardView()
                                .createScene(stage)
                )
        );

        doctors.setOnAction(
                e -> stage.setScene(
                        new DoctorManagementView()
                                .createScene(stage)
                )
        );

        departments.setOnAction(
                e -> stage.setScene(
                        new DepartmentManagementView()
                                .createScene(stage)
                )
        );

        beds.setOnAction(
                e -> stage.setScene(
                        new BedManagementView()
                                .createScene(stage)
                )
        );

        appointments.setOnAction(
                e -> stage.setScene(
                        new AppointmentManagementView()
                                .createScene(stage)
                )
        );

        analytics.setOnAction(
                e -> stage.setScene(
                        new HospitalAnalyticsView()
                                .createScene(stage)
                )
        );

        hospitalSettings.setOnAction(
                e -> stage.setScene(
                        new HospitalProfileSettingsView()
                                .createScene(stage)
                )
        );

        Region spacer =
                new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        sidebar.getChildren().add(
                spacer
        );

        Button help =
                createNavigationButton(
                        "?",
                        "Help Center",
                        false
                );

        Button logout =
                createNavigationButton(
                        "↪",
                        "Logout",
                        false
                );

        help.setOnAction(
                e -> showAlert(
                        Alert.AlertType.INFORMATION,
                        "Help Center",
                        "Please contact the Health-Sphere support team."
                )
        );

        logout.setOnAction(
                e -> showAlert(
                        Alert.AlertType.INFORMATION,
                        "Logout",
                        "Please use the application's existing logout flow."
                )
        );

        sidebar.getChildren().addAll(
                help,
                logout
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

        Label iconLabel =
                new Label(
                        icon
                );

        iconLabel.setStyle(
                "-fx-font-size: 15px;"
                        + "-fx-text-fill: "
                        + (
                        selected
                                ? "white"
                                : SIDEBAR_TEXT
                )
                        + ";"
        );

        Label textLabel =
                new Label(
                        text
                );

        textLabel.setStyle(
                "-fx-font-size: 13px;"
                        + "-fx-font-weight: "
                        + (
                        selected
                                ? "bold"
                                : "500"
                )
                        + ";"
                        + "-fx-text-fill: "
                        + (
                        selected
                                ? "white"
                                : SIDEBAR_TEXT
                )
                        + ";"
        );

        HBox content =
                new HBox(
                        12,
                        iconLabel,
                        textLabel
                );

        content.setAlignment(
                Pos.CENTER_LEFT
        );

        button.setGraphic(
                content
        );

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setPrefHeight(
                42
        );

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

        String baseStyle =
                "-fx-background-radius: 8;"
                        + "-fx-cursor: hand;"
                        + "-fx-background-color: "
                        + (
                        selected
                                ? SIDEBAR_HOVER
                                : "transparent"
                )
                        + ";";

        button.setStyle(
                baseStyle
        );

        if (!selected) {

            button.setOnMouseEntered(
                    e -> button.setStyle(
                            "-fx-background-radius: 8;"
                                    + "-fx-cursor: hand;"
                                    + "-fx-background-color: "
                                    + SIDEBAR_HOVER
                                    + ";"
                    )
            );

            button.setOnMouseExited(
                    e -> button.setStyle(
                            baseStyle
                    )
            );
        }

        return button;
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
                createKpiCards(),
                createWardCard()
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
                        "Manage Wards"
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
                        "Manage hospital wards, capacity and bed availability."
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

        Button addWard =
                new Button(
                        "+ Add Ward"
                );

        addWard.setStyle(
                "-fx-background-color: "
                        + PRIMARY_BLUE
                        + ";"
                        + "-fx-text-fill: white;"
                        + "-fx-background-radius: 8;"
                        + "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-padding: 10 18;"
                        + "-fx-cursor: hand;"
        );

        addWard.setOnAction(
                e -> showAddWardDialog(stage)
        );

        header.getChildren().addAll(
                titleBox,
                spacer,
                addWard
        );

        return header;
    }

    // =========================================================
    // KPI CARDS
    // =========================================================

    private HBox createKpiCards() {

        HBox cards =
                new HBox(16);

        VBox totalWardCard =
                createKpiCard(
                        "Total Wards",
                        "0",
                        "Active hospital wards",
                        "▤",
                        PRIMARY_BLUE,
                        PRIMARY_LIGHT
                );

        VBox capacityCard =
                createKpiCard(
                        "Total Capacity",
                        "0",
                        "Configured ward capacity",
                        "=",
                        PURPLE,
                        PURPLE_LIGHT
                );

        VBox availableCard =
                createKpiCard(
                        "Available Beds",
                        "0",
                        "Beds ready for patients",
                        "✓",
                        SUCCESS_GREEN,
                        SUCCESS_LIGHT
                );

        VBox occupiedCard =
                createKpiCard(
                        "Occupied Beds",
                        "0",
                        "Currently occupied",
                        "●",
                        ERROR_RED,
                        ERROR_LIGHT
                );

        totalWardsLabel =
                getValueLabel(
                        totalWardCard
                );

        totalCapacityLabel =
                getValueLabel(
                        capacityCard
                );

        availableBedsLabel =
                getValueLabel(
                        availableCard
                );

        occupiedBedsLabel =
                getValueLabel(
                        occupiedCard
                );

        cards.getChildren().addAll(
                totalWardCard,
                capacityCard,
                availableCard,
                occupiedCard
        );

        for (
                javafx.scene.Node node :
                cards.getChildren()
        ) {

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
            String subtitle,
            String icon,
            String color,
            String bgColor) {

        VBox card =
                new VBox(10);

        card.setPadding(
                new Insets(16)
        );

        applyCardStyle(
                card
        );

        HBox top =
                new HBox();

        top.setAlignment(
                Pos.CENTER_LEFT
        );

        Label titleLabel =
                new Label(
                        title
                );

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

        Label iconLabel =
                new Label(
                        icon
                );

        iconLabel.setAlignment(
                Pos.CENTER
        );

        iconLabel.setPrefSize(
                32,
                32
        );

        iconLabel.setStyle(
                "-fx-background-color: "
                        + bgColor
                        + ";"
                        + "-fx-background-radius: 7;"
                        + "-fx-text-fill: "
                        + color
                        + ";"
                        + "-fx-font-weight: bold;"
        );

        top.getChildren().addAll(
                titleLabel,
                spacer,
                iconLabel
        );

        Label valueLabel =
                new Label(
                        value
                );

        valueLabel.setStyle(
                "-fx-font-size: 26px;"
                        + "-fx-font-weight: 800;"
                        + "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Label subtitleLabel =
                new Label(
                        subtitle
                );

        subtitleLabel.setStyle(
                "-fx-font-size: 11px;"
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
    // GET KPI VALUE LABEL
    // =========================================================

    private Label getValueLabel(
            VBox card) {

        return (Label)
                card.getChildren().get(
                        1
                );
    }

    // =========================================================
    // WARD CARD
    // =========================================================

    private VBox createWardCard() {

        VBox card =
                new VBox();

        applyCardStyle(
                card
        );

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        header.setPadding(
                new Insets(
                        20
                )
        );

        VBox titleBox =
                new VBox(3);

        Label title =
                new Label(
                        "Hospital Wards"
                );

        title.setStyle(
                "-fx-font-size: 16px;"
                        + "-fx-font-weight: 700;"
                        + "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Label subtitle =
                new Label(
                        "Ward capacity and real-time bed availability"
                );

        subtitle.setStyle(
                "-fx-font-size: 12px;"
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

        wardCountLabel =
                new Label(
                        "0 Wards"
                );

        wardCountLabel.setStyle(
                "-fx-font-size: 12px;"
                        + "-fx-font-weight: 600;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        header.getChildren().addAll(
                titleBox,
                spacer,
                wardCountLabel
        );

        card.getChildren().add(
                header
        );

        card.getChildren().add(
                new Separator()
        );

        wardRowsContainer =
                new VBox();

        card.getChildren().add(
                wardRowsContainer
        );

        return card;
    }

    // =========================================================
    // RENDER WARD ROWS
    // =========================================================

    private void renderWardRows() {

        if (wardRowsContainer == null) {
            return;
        }

        wardRowsContainer
                .getChildren()
                .clear();

        if (wardList.isEmpty()) {

            VBox empty =
                    new VBox(8);

            empty.setAlignment(
                    Pos.CENTER
            );

            empty.setPadding(
                    new Insets(40)
            );

            Label message =
                    new Label(
                            "No active hospital wards found."
                    );

            message.setStyle(
                    "-fx-font-size: 14px;"
                            + "-fx-text-fill: "
                            + SECONDARY_TEXT
                            + ";"
            );

            empty.getChildren().add(
                    message
            );

            wardRowsContainer
                    .getChildren()
                    .add(
                            empty
                    );

            return;
        }

        for (int i = 0;
             i < wardList.size();
             i++) {

            HospitalWard ward =
                    wardList.get(i);

            if (ward == null) {
                continue;
            }

            WardStats stats =
                    calculateWardStats(
                            ward.getWardId()
                    );

            HBox row =
                    createWardRow(
                            ward,
                            stats
                    );

            wardRowsContainer
                    .getChildren()
                    .add(
                            row
                    );

            if (i < wardList.size() - 1) {

                Separator separator =
                        new Separator();

                separator.setPadding(
                        new Insets(
                                0,
                                20,
                                0,
                                20
                        )
                );

                wardRowsContainer
                        .getChildren()
                        .add(
                                separator
                        );
            }
        }
    }

    // =========================================================
    // CREATE WARD ROW
    // =========================================================

    private HBox createWardRow(
            HospitalWard ward,
            WardStats stats) {

        HBox row =
                new HBox();

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(
                        16,
                        20,
                        16,
                        20
                )
        );

        row.setMaxWidth(
                Double.MAX_VALUE
        );

        // -----------------------------------------------------
        // Ward
        // -----------------------------------------------------

        HBox wardBox =
                new HBox(12);

        wardBox.setAlignment(
                Pos.CENTER_LEFT
        );

        wardBox.setPrefWidth(
                280
        );

        Label icon =
                new Label(
                        "▥"
                );

        icon.setAlignment(
                Pos.CENTER
        );

        icon.setPrefSize(
                44,
                44
        );

        String iconColor =
                getWardColor(
                        ward
                );

        String iconBackground =
                getWardBackground(
                        ward
                );

        icon.setStyle(
                "-fx-background-color: "
                        + iconBackground
                        + ";"
                        + "-fx-background-radius: 8;"
                        + "-fx-text-fill: "
                        + iconColor
                        + ";"
                        + "-fx-font-weight: bold;"
        );

        VBox wardInfo =
                new VBox(3);

        Label name =
                new Label(
                        safe(
                                ward.getName()
                        )
                );

        name.setStyle(
                "-fx-font-size: 14px;"
                        + "-fx-font-weight: 700;"
                        + "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Label category =
                new Label(
                        safe(
                                ward.getCategory()
                        )
                );

        category.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        wardInfo.getChildren().addAll(
                name,
                category
        );

        wardBox.getChildren().addAll(
                icon,
                wardInfo
        );

        // -----------------------------------------------------
        // Capacity
        // -----------------------------------------------------

        VBox capacityBox =
                new VBox(3);

        capacityBox.setPrefWidth(
                160
        );

        Label capacityTitle =
                createSmallTitle(
                        "Capacity"
                );

        Label capacity =
                new Label(
                        ward.getCapacity()
                                + " Beds"
                );

        capacity.setStyle(
                "-fx-font-size: 12px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        capacityBox.getChildren().addAll(
                capacityTitle,
                capacity
        );

        // -----------------------------------------------------
        // Availability
        // -----------------------------------------------------

        VBox availabilityBox =
                new VBox(3);

        availabilityBox.setPrefWidth(
                200
        );

        Label availabilityTitle =
                createSmallTitle(
                        "Bed Availability"
                );

        Label availability =
                new Label(
                        stats.available
                                + " Available / "
                                + stats.total
                                + " Beds"
                );

        availability.setStyle(
                "-fx-font-size: 12px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + SUCCESS_GREEN
                        + ";"
        );

        availabilityBox.getChildren().addAll(
                availabilityTitle,
                availability
        );

        // -----------------------------------------------------
        // Occupancy
        // -----------------------------------------------------

        VBox occupancyBox =
                new VBox(3);

        occupancyBox.setPrefWidth(
                140
        );

        Label occupancyTitle =
                createSmallTitle(
                        "Occupancy"
                );

        double occupancy =
                stats.total == 0
                        ? 0
                        : (
                        (double) stats.occupied
                                / stats.total
                );

        Label occupancyLabel =
                new Label(
                        String.format(
                                "%.1f%%",
                                occupancy * 100
                        )
                );

        occupancyLabel.setStyle(
                "-fx-font-size: 12px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + (
                        occupancy >= 0.8
                                ? ERROR_RED
                                : SUCCESS_GREEN
                )
                        + ";"
        );

        occupancyBox.getChildren().addAll(
                occupancyTitle,
                occupancyLabel
        );

        // -----------------------------------------------------
        // 24/7
        // -----------------------------------------------------

        Label service =
                new Label(
                        ward.is24x7()
                                ? "24/7"
                                : "Standard"
                );

        service.setStyle(
                "-fx-background-color: "
                        + (
                        ward.is24x7()
                                ? SUCCESS_LIGHT
                                : LIGHT_BACKGROUND
                )
                        + ";"
                        + "-fx-text-fill: "
                        + (
                        ward.is24x7()
                                ? SUCCESS_GREEN
                                : SECONDARY_TEXT
                )
                        + ";"
                        + "-fx-background-radius: 12;"
                        + "-fx-padding: 5 10;"
                        + "-fx-font-size: 10px;"
                        + "-fx-font-weight: bold;"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        // -----------------------------------------------------
        // Edit
        // -----------------------------------------------------

        Button edit =
                new Button(
                        "Edit"
                );

        edit.setPrefWidth(
                58
        );

        edit.setStyle(
                "-fx-background-color: "
                        + PRIMARY_LIGHT
                        + ";"
                        + "-fx-text-fill: "
                        + PRIMARY_BLUE
                        + ";"
                        + "-fx-background-radius: 7;"
                        + "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-cursor: hand;"
        );

        edit.setOnAction(
                event -> {

                    event.consume();

                    Stage stage =
                            (Stage) edit
                                    .getScene()
                                    .getWindow();

                    showEditWardDialog(
                            stage,
                            ward
                    );
                }
        );

        row.getChildren().addAll(
                wardBox,
                capacityBox,
                availabilityBox,
                occupancyBox,
                service,
                spacer,
                edit
        );

        // =====================================================
        // CLICK ROW → BED MANAGEMENT
        // =====================================================

        row.setOnMouseClicked(
                event -> {

                    /*
                     * Ignore the Edit button.
                     */
                    if (event.getTarget()
                            instanceof Button) {

                        return;
                    }

                    openWardBeds(
                            row,
                            ward
                    );
                }
        );

        // =====================================================
        // HOVER
        // =====================================================

        row.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-background-radius: 8;"
                        + "-fx-cursor: hand;"
        );

        row.setOnMouseEntered(
                event -> row.setStyle(
                        "-fx-background-color: #F8FAFC;"
                                + "-fx-background-radius: 8;"
                                + "-fx-cursor: hand;"
                )
        );

        row.setOnMouseExited(
                event -> row.setStyle(
                        "-fx-background-color: transparent;"
                                + "-fx-background-radius: 8;"
                                + "-fx-cursor: hand;"
                )
        );

        return row;
    }

    // =========================================================
    // OPEN BED MANAGEMENT
    // =========================================================

    private void openWardBeds(
            HBox row,
            HospitalWard ward) {

        if (ward == null) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Unable to Open Ward",
                    "Ward information is unavailable."
            );

            return;
        }

        String wardId =
                safe(
                        ward.getWardId()
                );

        if (wardId.isEmpty()) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Unable to Open Ward",
                    "Ward ID is missing."
            );

            return;
        }

        Stage stage =
                (Stage) row
                        .getScene()
                        .getWindow();

        /*
         * The selected ward ID is passed to
         * BedManagementView.
         */
        BedManagementView bedView =
                new BedManagementView(
                        wardId
                );

        stage.setScene(
                bedView.createScene(
                        stage
                )
        );
    }

    // =========================================================
    // CALCULATE BED STATISTICS
    // =========================================================

    private WardStats calculateWardStats(
            String wardId) {

        WardStats stats =
                new WardStats();

        if (wardId == null
                || wardId.trim().isEmpty()) {

            return stats;
        }

        for (HospitalBed bed :
                bedList) {

            if (bed == null) {
                continue;
            }

            if (!bed.isActive()) {
                continue;
            }

            if (!wardId.equals(
                    bed.getWardId()
            )) {
                continue;
            }

            stats.total++;

            if (bed.getStatus()
                    == HospitalBed.BedStatus.AVAILABLE) {

                stats.available++;

            } else if (
                    bed.getStatus()
                            == HospitalBed.BedStatus.OCCUPIED) {

                stats.occupied++;

            } else if (
                    bed.getStatus()
                            == HospitalBed.BedStatus.RESERVED) {

                stats.reserved++;

            } else if (
                    bed.getStatus()
                            == HospitalBed.BedStatus.MAINTENANCE) {

                stats.maintenance++;
            }
        }

        return stats;
    }

    // =========================================================
    // UPDATE STATISTICS
    // =========================================================

    private void updateStatistics() {

        int totalWards =
                wardList.size();

        int totalCapacity =
                0;

        int available =
                0;

        int occupied =
                0;

        for (HospitalWard ward :
                wardList) {

            if (ward == null) {
                continue;
            }

            totalCapacity +=
                    Math.max(
                            ward.getCapacity(),
                            0
                    );

            WardStats stats =
                    calculateWardStats(
                            ward.getWardId()
                    );

            available +=
                    stats.available;

            occupied +=
                    stats.occupied;
        }

        if (totalWardsLabel != null) {

            totalWardsLabel.setText(
                    String.valueOf(
                            totalWards
                    )
            );
        }

        if (totalCapacityLabel != null) {

            totalCapacityLabel.setText(
                    String.valueOf(
                            totalCapacity
                    )
            );
        }

        if (availableBedsLabel != null) {

            availableBedsLabel.setText(
                    String.valueOf(
                            available
                    )
            );
        }

        if (occupiedBedsLabel != null) {

            occupiedBedsLabel.setText(
                    String.valueOf(
                            occupied
                    )
            );
        }

        if (wardCountLabel != null) {

            wardCountLabel.setText(
                    totalWards
                            + (
                            totalWards == 1
                                    ? " Ward"
                                    : " Wards"
                    )
            );
        }
    }

    // =========================================================
    // ADD WARD
    // =========================================================

    private void showAddWardDialog(
            Stage owner) {

        Stage dialog =
                new Stage();

        dialog.initOwner(
                owner
        );

        dialog.initModality(
                Modality.APPLICATION_MODAL
        );

        dialog.setTitle(
                "Add New Ward"
        );

        VBox root =
                new VBox(12);

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
                        "Create New Hospital Ward"
                );

        title.setStyle(
                "-fx-font-size: 18px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        TextField name =
                createInput(
                        "Ward Name"
                );

        ComboBox<String> category =
                new ComboBox<>();

        category.getItems().addAll(
                "General",
                "ICU",
                "Emergency",
                "Pediatrics",
                "Private",
                "Specialized"
        );

        category.setPromptText(
                "Select Category"
        );

        category.setMaxWidth(
                Double.MAX_VALUE
        );

        TextField capacity =
                createInput(
                        "Capacity"
                );

        CheckBox twentyFourSeven =
                new CheckBox(
                        "Available 24/7"
                );

        Button save =
                new Button(
                        "Create Ward"
                );

        save.setMaxWidth(
                Double.MAX_VALUE
        );

        save.setPrefHeight(
                40
        );

        save.setStyle(
                "-fx-background-color: "
                        + PRIMARY_BLUE
                        + ";"
                        + "-fx-text-fill: white;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 8;"
                        + "-fx-cursor: hand;"
        );

        save.setOnAction(
                event -> {

                    String wardName =
                            safe(
                                    name.getText()
                            );

                    String wardCategory =
                            category.getValue();

                    if (wardName.isEmpty()) {

                        showAlert(
                                Alert.AlertType.WARNING,
                                "Validation Error",
                                "Ward name is required."
                        );

                        return;
                    }

                    if (wardCategory == null
                            || wardCategory.trim().isEmpty()) {

                        showAlert(
                                Alert.AlertType.WARNING,
                                "Validation Error",
                                "Please select a category."
                        );

                        return;
                    }

                    int capacityValue;

                    try {

                        capacityValue =
                                Integer.parseInt(
                                        capacity.getText()
                                                .trim()
                                );

                    } catch (Exception e) {

                        showAlert(
                                Alert.AlertType.WARNING,
                                "Validation Error",
                                "Capacity must be a valid number."
                        );

                        return;
                    }

                    if (capacityValue <= 0) {

                        showAlert(
                                Alert.AlertType.WARNING,
                                "Validation Error",
                                "Capacity must be greater than zero."
                        );

                        return;
                    }

                    try {

                        wardController.createWard(
                                wardName,
                                wardCategory,
                                capacityValue,
                                twentyFourSeven
                                        .isSelected()
                        );

                        dialog.close();

                        loadData();

                        showAlert(
                                Alert.AlertType.INFORMATION,
                                "Success",
                                "Ward created successfully."
                        );

                    } catch (Exception e) {

                        showAlert(
                                Alert.AlertType.ERROR,
                                "Unable to Create Ward",
                                getErrorMessage(e)
                        );
                    }
                }
        );

        root.getChildren().addAll(
                title,
                new Label("Ward Name"),
                name,
                new Label("Category"),
                category,
                new Label("Capacity"),
                capacity,
                twentyFourSeven,
                save
        );

        dialog.setScene(
                new Scene(
                        root,
                        400,
                        440
                )
        );

        dialog.showAndWait();
    }

    // =========================================================
    // EDIT WARD
    // =========================================================

    private void showEditWardDialog(
            Stage owner,
            HospitalWard ward) {

        if (ward == null) {
            return;
        }

        Stage dialog =
                new Stage();

        dialog.initOwner(
                owner
        );

        dialog.initModality(
                Modality.APPLICATION_MODAL
        );

        dialog.setTitle(
                "Edit Ward"
        );

        VBox root =
                new VBox(12);

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
                        "Edit Ward Details"
                );

        title.setStyle(
                "-fx-font-size: 18px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        TextField name =
                createInput(
                        "Ward Name"
                );

        name.setText(
                safe(
                        ward.getName()
                )
        );

        ComboBox<String> category =
                new ComboBox<>();

        category.getItems().addAll(
                "General",
                "ICU",
                "Emergency",
                "Pediatrics",
                "Private",
                "Specialized"
        );

        String currentCategory =
                safe(
                        ward.getCategory()
                );

        if (!currentCategory.isEmpty()
                && !category
                .getItems()
                .contains(
                        currentCategory
                )) {

            category.getItems().add(
                    currentCategory
            );
        }

        category.setValue(
                currentCategory
        );

        category.setMaxWidth(
                Double.MAX_VALUE
        );

        TextField capacity =
                createInput(
                        "Capacity"
                );

        capacity.setText(
                String.valueOf(
                        ward.getCapacity()
                )
        );

        CheckBox twentyFourSeven =
                new CheckBox(
                        "Available 24/7"
                );

        twentyFourSeven.setSelected(
                ward.is24x7()
        );

        Button save =
                new Button(
                        "Save Changes"
                );

        save.setMaxWidth(
                Double.MAX_VALUE
        );

        save.setPrefHeight(
                40
        );

        save.setStyle(
                "-fx-background-color: "
                        + PRIMARY_BLUE
                        + ";"
                        + "-fx-text-fill: white;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 8;"
                        + "-fx-cursor: hand;"
        );

        save.setOnAction(
                event -> {

                    String wardName =
                            safe(
                                    name.getText()
                            );

                    String wardCategory =
                            category.getValue();

                    if (wardName.isEmpty()) {

                        showAlert(
                                Alert.AlertType.WARNING,
                                "Validation Error",
                                "Ward name is required."
                        );

                        return;
                    }

                    if (wardCategory == null
                            || wardCategory.trim().isEmpty()) {

                        showAlert(
                                Alert.AlertType.WARNING,
                                "Validation Error",
                                "Please select a category."
                        );

                        return;
                    }

                    int capacityValue;

                    try {

                        capacityValue =
                                Integer.parseInt(
                                        capacity.getText()
                                                .trim()
                                );

                    } catch (Exception e) {

                        showAlert(
                                Alert.AlertType.WARNING,
                                "Validation Error",
                                "Capacity must be a valid number."
                        );

                        return;
                    }

                    if (capacityValue <= 0) {

                        showAlert(
                                Alert.AlertType.WARNING,
                                "Validation Error",
                                "Capacity must be greater than zero."
                        );

                        return;
                    }

                    WardStats stats =
                            calculateWardStats(
                                    ward.getWardId()
                            );

                    if (capacityValue < stats.total) {

                        showAlert(
                                Alert.AlertType.WARNING,
                                "Invalid Capacity",
                                "Capacity cannot be less than "
                                        + stats.total
                                        + " existing bed(s)."
                        );

                        return;
                    }

                    try {

                        /*
                         * IMPORTANT:
                         *
                         * Your current project has WardController
                         * compiled with a void updateWard().
                         *
                         * Therefore we deliberately do NOT write:
                         *
                         * boolean result =
                         *     wardController.updateWard(...);
                         *
                         * We simply call the method.
                         */

                        wardController.updateWard(
                                ward.getWardId(),
                                wardName,
                                wardCategory,
                                capacityValue,
                                twentyFourSeven
                                        .isSelected()
                        );

                        dialog.close();

                        loadData();

                        showAlert(
                                Alert.AlertType.INFORMATION,
                                "Success",
                                "Ward updated successfully."
                        );

                    } catch (Exception e) {

                        showAlert(
                                Alert.AlertType.ERROR,
                                "Unable to Update Ward",
                                getErrorMessage(e)
                        );
                    }
                }
        );

        root.getChildren().addAll(
                title,
                new Label("Ward Name"),
                name,
                new Label("Category"),
                category,
                new Label("Capacity"),
                capacity,
                twentyFourSeven,
                save
        );

        dialog.setScene(
                new Scene(
                        root,
                        400,
                        440
                )
        );

        dialog.showAndWait();
    }

    // =========================================================
    // INPUT
    // =========================================================

    private TextField createInput(
            String prompt) {

        TextField field =
                new TextField();

        field.setPromptText(
                prompt
        );

        field.setPrefHeight(
                38
        );

        field.setStyle(
                "-fx-background-color: "
                        + LIGHT_BACKGROUND
                        + ";"
                        + "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-radius: 7;"
                        + "-fx-background-radius: 7;"
                        + "-fx-padding: 8 12;"
        );

        return field;
    }

    // =========================================================
    // SMALL TITLE
    // =========================================================

    private Label createSmallTitle(
            String text) {

        Label label =
                new Label(
                        text
                );

        label.setStyle(
                "-fx-font-size: 10px;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        return label;
    }

    // =========================================================
    // CARD STYLE
    // =========================================================

    private void applyCardStyle(
            Pane pane) {

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

    // =========================================================
    // WARD COLOR
    // =========================================================

    private String getWardColor(
            HospitalWard ward) {

        String name =
                safe(
                        ward.getName()
                ).toLowerCase();

        String category =
                safe(
                        ward.getCategory()
                ).toLowerCase();

        if (name.contains("icu")
                || category.contains("icu")) {

            return PURPLE;
        }

        if (name.contains("emergency")
                || category.contains("emergency")) {

            return ERROR_RED;
        }

        return PRIMARY_BLUE;
    }

    // =========================================================
    // WARD BACKGROUND
    // =========================================================

    private String getWardBackground(
            HospitalWard ward) {

        String name =
                safe(
                        ward.getName()
                ).toLowerCase();

        String category =
                safe(
                        ward.getCategory()
                ).toLowerCase();

        if (name.contains("icu")
                || category.contains("icu")) {

            return PURPLE_LIGHT;
        }

        if (name.contains("emergency")
                || category.contains("emergency")) {

            return ERROR_LIGHT;
        }

        return PRIMARY_LIGHT;
    }

    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safe(
            String value) {

        return value == null
                ? ""
                : value.trim();
    }

    // =========================================================
    // ERROR MESSAGE
    // =========================================================

    private String getErrorMessage(
            Throwable throwable) {

        Throwable current =
                throwable;

        Throwable deepest =
                throwable;

        while (current != null) {

            deepest =
                    current;

            current =
                    current.getCause();
        }

        String message =
                deepest.getMessage();

        if (message == null
                || message.trim().isEmpty()) {

            return deepest
                    .getClass()
                    .getSimpleName();
        }

        return message;
    }

    // =========================================================
    // ALERT
    // =========================================================

    private void showAlert(
            Alert.AlertType type,
            String title,
            String message) {

        Alert alert =
                new Alert(
                        type
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
    // WARD STATISTICS
    // =========================================================

    private static class WardStats {

        int total = 0;
        int available = 0;
        int occupied = 0;
        int reserved = 0;
        int maintenance = 0;
    }
}