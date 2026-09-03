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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Hospital Bed Management View.
 *
 * Architecture:
 *
 * JavaFX View
 *      ↓
 * BedController / WardController
 *      ↓
 * BedDAO / WardDAO
 *      ↓
 * Firestore
 *
 * The view does NOT directly access Firestore.
 */
public class BedManagementView {

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
    private static final String SIDEBAR_BORDER = "#1E293B";
    private static final String SIDEBAR_TEXT = "#94A3B8";
    private static final String SIDEBAR_HOVER = "#1E293B";

    private static final String SUCCESS_GREEN = "#059669";
    private static final String SUCCESS_LIGHT = "#ECFDF5";

    private static final String ERROR_RED = "#DC2626";
    private static final String ERROR_LIGHT = "#FEF2F2";

    private static final String WARNING_ORANGE = "#D97706";
    private static final String WARNING_LIGHT = "#FFFBEB";

    private static final String PURPLE = "#7C3AED";
    private static final String PURPLE_LIGHT = "#F5F3FF";

    private static final String MAINTENANCE_GRAY = "#64748B";
    private static final String MAINTENANCE_LIGHT = "#F1F5F9";

    // =========================================================
    // CONTROLLERS
    // =========================================================

    private final BedController bedController;
    private final WardController wardController;

    // =========================================================
    // DATA
    // =========================================================

    private final List<HospitalBed> bedList =
            new ArrayList<>();

    private final List<HospitalWard> wardList =
            new ArrayList<>();

    /*
     * If this screen was opened by clicking a ward,
     * this contains that ward's ID.
     */
    private String selectedWardId;

    // =========================================================
    // UI REFERENCES
    // =========================================================

    private Label totalBedsKpiLabel;
    private Label occupiedKpiLabel;
    private Label availableKpiLabel;
    private Label reservedKpiLabel;
    private Label maintenanceKpiLabel;

    private Label totalOccupancyPctLabel;
    private ProgressBar totalProgressBar;

    private ComboBox<String> wardFilter;
    private ComboBox<String> statusFilter;
    private TextField searchInput;

    private GridPane reservationGrid;

    private VBox wardManagementContainer;

    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    /**
     * Opens Bed Management showing all wards.
     */
    public BedManagementView() {

        this(null);
    }

    /**
     * Opens Bed Management for a specific ward.
     *
     * @param selectedWardId Firestore ward ID
     */
    public BedManagementView(
            String selectedWardId) {

        this.bedController =
                new BedController();

        this.wardController =
                new WardController();

        this.selectedWardId =
                selectedWardId;
    }

    // =========================================================
    // CREATE SCENE
    // =========================================================

    public Scene createScene(
            Stage stage) {

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

        ScrollPane scrollPane =
                new ScrollPane(
                        createMainContent(stage)
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
         * Load real Firestore data.
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

            bedList.clear();
            wardList.clear();

            // -------------------------------------------------
            // Load wards
            // -------------------------------------------------

            List<HospitalWard> wards =
                    wardController.getAllWards();

            if (wards != null) {

                wardList.addAll(
                        wards
                );
            }

            // -------------------------------------------------
            // Load beds
            // -------------------------------------------------

            List<HospitalBed> beds =
                    bedController.getAllBeds();

            if (beds != null) {

                bedList.addAll(
                        beds
                );
            }

            // -------------------------------------------------
            // Sort beds by bed number
            // -------------------------------------------------

            bedList.sort(
                    Comparator.comparing(
                            bed -> safe(
                                    bed.getBedNumber()
                            )
                    )
            );

            // -------------------------------------------------
            // Populate filters
            // -------------------------------------------------

            populateWardFilter();

            // -------------------------------------------------
            // Update UI
            // -------------------------------------------------

            updateBedMetrics();

            renderReservationGrid();

            renderWardManagement();

        } catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Unable to Load Bed Data",
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
                        10,
                        28,
                        10,
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

        TextField globalSearch =
                new TextField();

        globalSearch.setPromptText(
                "Search beds, wards..."
        );

        globalSearch.setPrefWidth(
                330
        );

        globalSearch.setPrefHeight(
                40
        );

        globalSearch.setStyle(
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

        VBox userInfo =
                new VBox(2);

        userInfo.setAlignment(
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

        userInfo.getChildren().addAll(
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
                globalSearch,
                spacer,
                notification,
                settings,
                userInfo,
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
                        + "-fx-border-color: "
                        + SIDEBAR_BORDER
                        + ";"
                        + "-fx-border-width: 0 1 0 0;"
        );

        // -----------------------------------------------------
        // LOGO
        // -----------------------------------------------------

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
                        + "-fx-text-fill: #38BDF8;"
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

        // -----------------------------------------------------
        // NAVIGATION
        // -----------------------------------------------------

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

        Button settings =
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
                settings
        );

        // -----------------------------------------------------
        // NAVIGATION ACTIONS
        // -----------------------------------------------------

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

        settings.setOnAction(
                e -> stage.setScene(
                        new HospitalProfileSettingsView()
                                .createScene(stage)
                )
        );

        // -----------------------------------------------------
        // SPACER
        // -----------------------------------------------------

        Region spacer =
                new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        sidebar.getChildren().add(
                spacer
        );

        // -----------------------------------------------------
        // FOOTER
        // -----------------------------------------------------

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
                                ? "#FFFFFF"
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
                                ? "#FFFFFF"
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

        String normalStyle =
                "-fx-background-color: "
                        + (
                        selected
                                ? SIDEBAR_HOVER
                                : "transparent"
                )
                        + ";"
                        + "-fx-background-radius: 8;"
                        + "-fx-cursor: hand;";

        button.setStyle(
                normalStyle
        );

        if (!selected) {

            button.setOnMouseEntered(
                    e -> button.setStyle(
                            "-fx-background-color: "
                                    + SIDEBAR_HOVER
                                    + ";"
                                    + "-fx-background-radius: 8;"
                                    + "-fx-cursor: hand;"
                    )
            );

            button.setOnMouseExited(
                    e -> button.setStyle(
                            normalStyle
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
                createHeader(stage),
                createKpiCards(),
                createFilterBar(),
                createBedGridCard(),
                createLowerSection(stage)
        );

        return content;
    }

    // =========================================================
    // HEADER
    // =========================================================

    private HBox createHeader(
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
                        "Bed Management"
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
                        "Monitor hospital beds, wards, and real-time availability."
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

        Button manageWards =
                new Button(
                        "Manage Wards"
                );

        manageWards.setStyle(
                "-fx-background-color: "
                        + CARD_BG
                        + ";"
                        + "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
                        + "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-radius: 8;"
                        + "-fx-background-radius: 8;"
                        + "-fx-font-size: 13px;"
                        + "-fx-font-weight: 600;"
                        + "-fx-padding: 8 16;"
                        + "-fx-cursor: hand;"
        );

        Button addBed =
                new Button(
                        "+ Add Bed"
                );

        addBed.setStyle(
                "-fx-background-color: "
                        + PRIMARY_BLUE
                        + ";"
                        + "-fx-text-fill: white;"
                        + "-fx-background-radius: 8;"
                        + "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-padding: 8 16;"
                        + "-fx-cursor: hand;"
        );

        manageWards.setOnAction(
                e -> stage.setScene(
                        new ManageWardsView()
                                .createScene(stage)
                )
        );

        addBed.setOnAction(
                e -> stage.setScene(
                        new AddBedView()
                                .createScene(stage)
                )
        );

        HBox actions =
                new HBox(
                        12,
                        manageWards,
                        addBed
                );

        actions.setAlignment(
                Pos.CENTER_RIGHT
        );

        header.getChildren().addAll(
                titleBox,
                spacer,
                actions
        );

        return header;
    }

    // =========================================================
    // KPI CARDS
    // =========================================================

    private HBox createKpiCards() {

        HBox cards =
                new HBox(16);

        VBox total =
                createKpiCard(
                        "Total Beds",
                        "0",
                        "Hospital beds",
                        "=",
                        PRIMARY_BLUE,
                        PRIMARY_LIGHT
                );

        VBox occupied =
                createKpiCard(
                        "Occupied",
                        "0",
                        "Currently occupied",
                        "●",
                        ERROR_RED,
                        ERROR_LIGHT
                );

        VBox available =
                createKpiCard(
                        "Available",
                        "0",
                        "Beds ready for patients",
                        "✓",
                        SUCCESS_GREEN,
                        SUCCESS_LIGHT
                );

        VBox reserved =
                createKpiCard(
                        "Reserved",
                        "0",
                        "Reserved beds",
                        "●",
                        WARNING_ORANGE,
                        WARNING_LIGHT
                );

        VBox maintenance =
                createKpiCard(
                        "Maintenance",
                        "0",
                        "Under maintenance",
                        "⚙",
                        MAINTENANCE_GRAY,
                        MAINTENANCE_LIGHT
                );

        totalBedsKpiLabel =
                getKpiValue(
                        total
                );

        occupiedKpiLabel =
                getKpiValue(
                        occupied
                );

        availableKpiLabel =
                getKpiValue(
                        available
                );

        reservedKpiLabel =
                getKpiValue(
                        reserved
                );

        maintenanceKpiLabel =
                getKpiValue(
                        maintenance
                );

        cards.getChildren().addAll(
                total,
                occupied,
                available,
                reserved,
                maintenance
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
                30,
                30
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
                "-fx-font-size: 25px;"
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
    // GET KPI VALUE
    // =========================================================

    private Label getKpiValue(
            VBox card) {

        return (Label)
                card.getChildren().get(
                        1
                );
    }

    // =========================================================
    // FILTER BAR
    // =========================================================

    private HBox createFilterBar() {

        HBox filterBar =
                new HBox(12);

        filterBar.setAlignment(
                Pos.CENTER_LEFT
        );

        filterBar.setPadding(
                new Insets(
                        12,
                        16,
                        12,
                        16
                )
        );

        filterBar.setStyle(
                "-fx-background-color: "
                        + CARD_BG
                        + ";"
                        + "-fx-background-radius: 10;"
                        + "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-radius: 10;"
        );

        searchInput =
                new TextField();

        searchInput.setPromptText(
                "Search bed number or patient..."
        );

        searchInput.setPrefWidth(
                260
        );

        searchInput.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-prompt-text-fill: #94A3B8;"
                        + "-fx-font-size: 13px;"
        );

        HBox searchBox =
                new HBox(
                        searchInput
                );

        searchBox.setStyle(
                "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-radius: 6;"
                        + "-fx-padding: 2;"
        );

        // -----------------------------------------------------
        // Ward Filter
        // -----------------------------------------------------

        wardFilter =
                new ComboBox<>();

        wardFilter.setPrefWidth(
                145
        );

        wardFilter.setPromptText(
                "All Wards"
        );

        wardFilter.setStyle(
                "-fx-background-color: "
                        + LIGHT_BACKGROUND
                        + ";"
                        + "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-radius: 6;"
        );

        // -----------------------------------------------------
        // Status Filter
        // -----------------------------------------------------

        statusFilter =
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

        statusFilter.setPrefWidth(
                140
        );

        statusFilter.setStyle(
                "-fx-background-color: "
                        + LIGHT_BACKGROUND
                        + ";"
                        + "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-radius: 6;"
        );

        // -----------------------------------------------------
        // Listeners
        // -----------------------------------------------------

        wardFilter.setOnAction(
                e -> renderReservationGrid()
        );

        statusFilter.setOnAction(
                e -> renderReservationGrid()
        );

        searchInput.textProperty()
                .addListener(
                        (obs, oldValue, newValue) ->
                                renderReservationGrid()
                );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button refresh =
                new Button(
                        "↻ Refresh"
                );

        refresh.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-radius: 6;"
                        + "-fx-font-size: 12px;"
                        + "-fx-cursor: hand;"
        );

        refresh.setOnAction(
                e -> loadData()
        );

        Button export =
                new Button(
                        "↓ Export"
                );

        export.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-radius: 6;"
                        + "-fx-font-size: 12px;"
                        + "-fx-cursor: hand;"
        );

        export.setOnAction(
                e -> exportBeds()
        );

        filterBar.getChildren().addAll(
                searchBox,
                wardFilter,
                statusFilter,
                spacer,
                refresh,
                export
        );

        return filterBar;
    }

    // =========================================================
    // POPULATE WARD FILTER
    // =========================================================

    private void populateWardFilter() {

        if (wardFilter == null) {
            return;
        }

        wardFilter.getItems().clear();

        wardFilter.getItems().add(
                "All Wards"
        );

        String selectedWardName =
                null;

        for (HospitalWard ward :
                wardList) {

            if (ward == null) {
                continue;
            }

            String name =
                    safe(
                            ward.getName()
                    );

            if (name.isEmpty()) {
                continue;
            }

            if (!wardFilter
                    .getItems()
                    .contains(name)) {

                wardFilter
                        .getItems()
                        .add(name);
            }

            if (selectedWardId != null
                    && selectedWardId.equals(
                            ward.getWardId()
                    )) {

                selectedWardName =
                        name;
            }
        }

        if (selectedWardName != null) {

            wardFilter.setValue(
                    selectedWardName
            );

        } else {

            wardFilter.setValue(
                    "All Wards"
            );
        }
    }

    // =========================================================
    // BED GRID CARD
    // =========================================================

    private VBox createBedGridCard() {

        VBox card =
                new VBox(16);

        applyCardStyle(
                card
        );

        card.setPadding(
                new Insets(20)
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
                        "Visual Bed Layout"
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
                        "Click on any bed to manage its status."
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

        HBox legend =
                new HBox(12);

        legend.setAlignment(
                Pos.CENTER_RIGHT
        );

        legend.getChildren().addAll(
                createLegendItem(
                        "Available",
                        SUCCESS_GREEN
                ),
                createLegendItem(
                        "Occupied",
                        ERROR_RED
                ),
                createLegendItem(
                        "Reserved",
                        WARNING_ORANGE
                ),
                createLegendItem(
                        "Maintenance",
                        MAINTENANCE_GRAY
                )
        );

        header.getChildren().addAll(
                titleBox,
                spacer,
                legend
        );

        reservationGrid =
                new GridPane();

        reservationGrid.setHgap(
                12
        );

        reservationGrid.setVgap(
                12
        );

        card.getChildren().addAll(
                header,
                reservationGrid
        );

        return card;
    }

    // =========================================================
    // LEGEND
    // =========================================================

    private HBox createLegendItem(
            String text,
            String color) {

        HBox item =
                new HBox(6);

        item.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle dot =
                new Circle(
                        4,
                        Color.web(
                                color
                        )
                );

        Label label =
                new Label(
                        text
                );

        label.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        item.getChildren().addAll(
                dot,
                label
        );

        return item;
    }

    // =========================================================
    // RENDER BED GRID
    // =========================================================

    private void renderReservationGrid() {

        if (reservationGrid == null) {
            return;
        }

        reservationGrid
                .getChildren()
                .clear();

        String selectedWard =
                wardFilter == null
                        ? "All Wards"
                        : wardFilter.getValue();

        String selectedStatus =
                statusFilter == null
                        ? "All Status"
                        : statusFilter.getValue();

        String query =
                searchInput == null
                        ? ""
                        : safe(
                        searchInput.getText()
                ).toLowerCase();

        int column = 0;
        int row = 0;

        int displayedBeds = 0;

        for (HospitalBed bed :
                bedList) {

            if (bed == null) {
                continue;
            }

            if (!bed.isActive()) {
                continue;
            }

            // -------------------------------------------------
            // Ward filter
            // -------------------------------------------------

            if (!matchesWard(
                    bed,
                    selectedWard
            )) {

                continue;
            }

            // -------------------------------------------------
            // Status filter
            // -------------------------------------------------

            if (!matchesStatus(
                    bed,
                    selectedStatus
            )) {

                continue;
            }

            // -------------------------------------------------
            // Search
            // -------------------------------------------------

            if (!matchesSearch(
                    bed,
                    query
            )) {

                continue;
            }

            Button bedButton =
                    createBedButton(
                            bed
                    );

            reservationGrid.add(
                    bedButton,
                    column,
                    row
            );

            displayedBeds++;

            column++;

            if (column == 6) {

                column = 0;
                row++;
            }
        }

        if (displayedBeds == 0) {

            Label empty =
                    new Label(
                            "No beds found for the selected filters."
                    );

            empty.setStyle(
                    "-fx-font-size: 13px;"
                            + "-fx-text-fill: "
                            + SECONDARY_TEXT
                            + ";"
            );

            reservationGrid.add(
                    empty,
                    0,
                    0
            );
        }
    }

    // =========================================================
    // CREATE BED BUTTON
    // =========================================================

    private Button createBedButton(
            HospitalBed bed) {

        String bedNumber =
                safe(
                        bed.getBedNumber()
                );

        HospitalBed.BedStatus status =
                bed.getStatus();

        if (status == null) {

            status =
                    HospitalBed.BedStatus.AVAILABLE;
        }

        String statusColor =
                getStatusColor(
                        status
                );

        String statusBackground =
                getStatusBackground(
                        status
                );

        Button button =
                new Button(
                        "🛏 "
                                + (
                                bedNumber.isEmpty()
                                        ? safe(
                                        bed.getBedId()
                                )
                                        : bedNumber
                        )
                );

        button.setPrefSize(
                105,
                55
        );

        button.setStyle(
                "-fx-background-color: "
                        + statusBackground
                        + ";"
                        + "-fx-border-color: "
                        + statusColor
                        + ";"
                        + "-fx-border-radius: 8;"
                        + "-fx-background-radius: 8;"
                        + "-fx-text-fill: "
                        + statusColor
                        + ";"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-size: 11px;"
                        + "-fx-cursor: hand;"
        );

        button.setOnAction(
                e -> handleBedClick(
                        bed
                )
        );

        return button;
    }

    // =========================================================
    // HANDLE BED CLICK
    // =========================================================

    private void handleBedClick(
            HospitalBed bed) {

        if (bed == null) {
            return;
        }

        String bedNumber =
                safe(
                        bed.getBedNumber()
                );

        if (bedNumber.isEmpty()) {

            bedNumber =
                    safe(
                            bed.getBedId()
                    );
        }

        String currentStatus =
                bed.getStatus() == null
                        ? "UNKNOWN"
                        : bed.getStatus()
                        .name();

        Alert alert =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        alert.setTitle(
                "Bed Management"
        );

        alert.setHeaderText(
                "Manage Bed: "
                        + bedNumber
        );

        String patientId =
                safe(
                        bed.getPatientId()
                );

        String patientText =
                patientId.isEmpty()
                        ? "No patient assigned"
                        : "Patient: "
                        + patientId;

        alert.setContentText(
                "Current Status: "
                        + currentStatus
                        + "\n"
                        + patientText
                        + "\n\n"
                        + "Select an action:"
        );

        ButtonType reserve =
                new ButtonType(
                        "Reserve"
                );

        ButtonType occupy =
                new ButtonType(
                        "Occupy"
                );

        ButtonType release =
                new ButtonType(
                        "Make Available"
                );

        ButtonType maintenance =
                new ButtonType(
                        "Maintenance"
                );

        ButtonType cancel =
                new ButtonType(
                        "Cancel",
                        ButtonType.CANCEL
                                .getButtonData()
                );

        alert.getButtonTypes().setAll(
                reserve,
                occupy,
                release,
                maintenance,
                cancel
        );

        Optional<ButtonType> result =
                alert.showAndWait();

        if (!result.isPresent()
                || result.get() == cancel) {

            return;
        }

        try {

            if (result.get() == reserve) {

                bedController.reserveBed(
                        bed.getBedId()
                );

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Bed Reserved",
                        "Bed "
                                + bedNumber
                                + " has been reserved."
                );

            } else if (
                    result.get() == occupy) {

                occupyBed(
                        bed
                );

                return;

            } else if (
                    result.get() == release) {

                bedController.releaseBed(
                        bed.getBedId()
                );

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Bed Released",
                        "Bed "
                                + bedNumber
                                + " is now available."
                );

            } else if (
                    result.get() == maintenance) {

                bedController.setMaintenance(
                        bed.getBedId()
                );

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Maintenance",
                        "Bed "
                                + bedNumber
                                + " has been marked for maintenance."
                );
            }

            loadData();

        } catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Bed Update Failed",
                    getErrorMessage(e)
            );
        }
    }

    // =========================================================
    // OCCUPY BED
    // =========================================================

    private void occupyBed(
            HospitalBed bed) {

        Stage dialog =
                new Stage();

        dialog.initModality(
                Modality.APPLICATION_MODAL
        );

        dialog.setTitle(
                "Assign Patient"
        );

        VBox root =
                new VBox(14);

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
                        "Assign Patient to Bed"
                );

        title.setStyle(
                "-fx-font-size: 17px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Label instruction =
                new Label(
                        "Enter the existing Patient UID."
                );

        instruction.setStyle(
                "-fx-font-size: 12px;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        TextField patientId =
                new TextField();

        patientId.setPromptText(
                "Patient UID"
        );

        patientId.setPrefHeight(
                38
        );

        Button assign =
                new Button(
                        "Assign Patient"
                );

        assign.setMaxWidth(
                Double.MAX_VALUE
        );

        assign.setPrefHeight(
                40
        );

        assign.setStyle(
                "-fx-background-color: "
                        + PRIMARY_BLUE
                        + ";"
                        + "-fx-text-fill: white;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 8;"
                        + "-fx-cursor: hand;"
        );

        assign.setOnAction(
                e -> {

                    String id =
                            safe(
                                    patientId.getText()
                            );

                    if (id.isEmpty()) {

                        showAlert(
                                Alert.AlertType.WARNING,
                                "Patient Required",
                                "Please enter a patient UID."
                        );

                        return;
                    }

                    try {

                        bedController.occupyBed(
                                bed.getBedId(),
                                id
                        );

                        dialog.close();

                        showAlert(
                                Alert.AlertType.INFORMATION,
                                "Bed Occupied",
                                "Patient assigned successfully."
                        );

                        loadData();

                    } catch (Exception ex) {

                        showAlert(
                                Alert.AlertType.ERROR,
                                "Unable to Occupy Bed",
                                getErrorMessage(ex)
                        );
                    }
                }
        );

        root.getChildren().addAll(
                title,
                instruction,
                patientId,
                assign
        );

        dialog.setScene(
                new Scene(
                        root,
                        380,
                        260
                )
        );

        dialog.showAndWait();
    }

    // =========================================================
    // FILTER MATCH - WARD
    // =========================================================

    private boolean matchesWard(
            HospitalBed bed,
            String selectedWard) {

        if (selectedWard == null
                || selectedWard.equals(
                "All Wards"
        )) {

            return true;
        }

        String wardId =
                safe(
                        bed.getWardId()
                );

        for (HospitalWard ward :
                wardList) {

            if (ward == null) {
                continue;
            }

            if (!wardId.equals(
                    safe(
                            ward.getWardId()
                    )
            )) {

                continue;
            }

            return selectedWard.equals(
                    safe(
                            ward.getName()
                    )
            );
        }

        return false;
    }

    // =========================================================
    // FILTER MATCH - STATUS
    // =========================================================

    private boolean matchesStatus(
            HospitalBed bed,
            String selectedStatus) {

        if (selectedStatus == null
                || selectedStatus.equals(
                "All Status"
        )) {

            return true;
        }

        HospitalBed.BedStatus status =
                bed.getStatus();

        if (status == null) {
            return false;
        }

        return status.name()
                .equalsIgnoreCase(
                        selectedStatus
                );
    }

    // =========================================================
    // FILTER MATCH - SEARCH
    // =========================================================

    private boolean matchesSearch(
            HospitalBed bed,
            String query) {

        if (query == null
                || query.trim().isEmpty()) {

            return true;
        }

        String bedNumber =
                safe(
                        bed.getBedNumber()
                ).toLowerCase();

        String patientId =
                safe(
                        bed.getPatientId()
                ).toLowerCase();

        String bedId =
                safe(
                        bed.getBedId()
                ).toLowerCase();

        String bedType =
                safe(
                        bed.getBedType()
                ).toLowerCase();

        return bedNumber.contains(query)
                || patientId.contains(query)
                || bedId.contains(query)
                || bedType.contains(query);
    }

    // =========================================================
    // UPDATE BED METRICS
    // =========================================================

    private void updateBedMetrics() {

        int total =
                0;

        int occupied =
                0;

        int available =
                0;

        int reserved =
                0;

        int maintenance =
                0;

        for (HospitalBed bed :
                bedList) {

            if (bed == null
                    || !bed.isActive()) {

                continue;
            }

            /*
             * If this page was opened for a specific
             * ward, KPI values should represent that ward.
             */
            if (selectedWardId != null
                    && !selectedWardId.equals(
                    bed.getWardId()
            )) {

                continue;
            }

            total++;

            HospitalBed.BedStatus status =
                    bed.getStatus();

            if (status ==
                    HospitalBed.BedStatus.OCCUPIED) {

                occupied++;

            } else if (
                    status ==
                            HospitalBed.BedStatus.AVAILABLE) {

                available++;

            } else if (
                    status ==
                            HospitalBed.BedStatus.RESERVED) {

                reserved++;

            } else if (
                    status ==
                            HospitalBed.BedStatus.MAINTENANCE) {

                maintenance++;
            }
        }

        if (totalBedsKpiLabel != null) {

            totalBedsKpiLabel.setText(
                    String.valueOf(
                            total
                    )
            );
        }

        if (occupiedKpiLabel != null) {

            occupiedKpiLabel.setText(
                    String.valueOf(
                            occupied
                    )
            );
        }

        if (availableKpiLabel != null) {

            availableKpiLabel.setText(
                    String.valueOf(
                            available
                    )
            );
        }

        if (reservedKpiLabel != null) {

            reservedKpiLabel.setText(
                    String.valueOf(
                            reserved
                    )
            );
        }

        if (maintenanceKpiLabel != null) {

            maintenanceKpiLabel.setText(
                    String.valueOf(
                            maintenance
                    )
            );
        }

        double occupancy =
                total == 0
                        ? 0
                        : (
                        (double) occupied
                                / total
                );

        if (totalProgressBar != null) {

            totalProgressBar.setProgress(
                    occupancy
            );
        }

        if (totalOccupancyPctLabel != null) {

            totalOccupancyPctLabel.setText(
                    String.format(
                            "%.1f%%",
                            occupancy * 100
                    )
            );
        }
    }

    // =========================================================
    // LOWER SECTION
    // =========================================================

    private HBox createLowerSection(
            Stage stage) {

        HBox lower =
                new HBox(20);

        VBox availability =
                createBedAvailabilityCard();

        VBox wards =
                createWardManagementCard(
                        stage
                );

        lower.getChildren().addAll(
                availability,
                wards
        );

        HBox.setHgrow(
                availability,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                wards,
                Priority.ALWAYS
        );

        return lower;
    }

    // =========================================================
    // BED AVAILABILITY CARD
    // =========================================================

    private VBox createBedAvailabilityCard() {

        VBox card =
                new VBox(14);

        card.setPadding(
                new Insets(18)
        );

        applyCardStyle(
                card
        );

        HBox header =
                new HBox();

        VBox text =
                new VBox(3);

        Label title =
                new Label(
                        "Bed Availability"
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
                        "Current hospital occupancy breakdown"
                );

        subtitle.setStyle(
                "-fx-font-size: 12px;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        text.getChildren().addAll(
                title,
                subtitle
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        totalOccupancyPctLabel =
                new Label(
                        "0.0%"
                );

        totalOccupancyPctLabel.setStyle(
                "-fx-font-size: 22px;"
                        + "-fx-font-weight: 800;"
                        + "-fx-text-fill: "
                        + PRIMARY_BLUE
                        + ";"
        );

        header.getChildren().addAll(
                text,
                spacer,
                totalOccupancyPctLabel
        );

        totalProgressBar =
                new ProgressBar(
                        0
                );

        totalProgressBar.setMaxWidth(
                Double.MAX_VALUE
        );

        totalProgressBar.setStyle(
                "-fx-accent: "
                        + PRIMARY_BLUE
                        + ";"
        );

        HBox legend =
                new HBox(16);

        legend.getChildren().addAll(
                createLegendItem(
                        "Occupied",
                        ERROR_RED
                ),
                createLegendItem(
                        "Available",
                        SUCCESS_GREEN
                ),
                createLegendItem(
                        "Reserved",
                        WARNING_ORANGE
                ),
                createLegendItem(
                        "Maintenance",
                        MAINTENANCE_GRAY
                )
        );

        card.getChildren().addAll(
                header,
                totalProgressBar,
                legend
        );

        return card;
    }

    // =========================================================
    // WARD MANAGEMENT CARD
    // =========================================================

    private VBox createWardManagementCard(
            Stage stage) {

        VBox card =
                new VBox(12);

        card.setPadding(
                new Insets(18)
        );

        applyCardStyle(
                card
        );

        HBox header =
                new HBox();

        VBox text =
                new VBox(3);

        Label title =
                new Label(
                        "Ward Availability"
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
                        "Available beds by ward"
                );

        subtitle.setStyle(
                "-fx-font-size: 12px;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        text.getChildren().addAll(
                title,
                subtitle
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button manage =
                new Button(
                        "Manage Wards"
                );

        manage.setStyle(
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

        manage.setOnAction(
                e -> stage.setScene(
                        new ManageWardsView()
                                .createScene(stage)
                )
        );

        header.getChildren().addAll(
                text,
                spacer,
                manage
        );

        wardManagementContainer =
                new VBox(10);

        card.getChildren().addAll(
                header,
                wardManagementContainer
        );

        return card;
    }

    // =========================================================
    // RENDER WARD MANAGEMENT
    // =========================================================

    private void renderWardManagement() {

        if (wardManagementContainer == null) {
            return;
        }

        wardManagementContainer
                .getChildren()
                .clear();

        if (wardList.isEmpty()) {

            Label empty =
                    new Label(
                            "No wards available."
                    );

            empty.setStyle(
                    "-fx-font-size: 12px;"
                            + "-fx-text-fill: "
                            + SECONDARY_TEXT
                            + ";"
            );

            wardManagementContainer
                    .getChildren()
                    .add(
                            empty
                    );

            return;
        }

        for (HospitalWard ward :
                wardList) {

            if (ward == null) {
                continue;
            }

            if (selectedWardId != null
                    && !selectedWardId.equals(
                    ward.getWardId()
            )) {

                continue;
            }

            WardStats stats =
                    calculateWardStats(
                            ward.getWardId()
                    );

            HBox row =
                    new HBox(10);

            row.setAlignment(
                    Pos.CENTER_LEFT
            );

            Label name =
                    new Label(
                            safe(
                                    ward.getName()
                            )
                    );

            name.setStyle(
                    "-fx-font-size: 12px;"
                            + "-fx-font-weight: 600;"
                            + "-fx-text-fill: "
                            + DARK_TEXT
                            + ";"
            );

            Region spacer =
                    new Region();

            HBox.setHgrow(
                    spacer,
                    Priority.ALWAYS
            );

            Label availability =
                    new Label(
                            stats.available
                                    + " / "
                                    + stats.total
                                    + " available"
                    );

            availability.setStyle(
                    "-fx-font-size: 11px;"
                            + "-fx-font-weight: bold;"
                            + "-fx-text-fill: "
                            + (
                            stats.available > 0
                                    ? SUCCESS_GREEN
                                    : ERROR_RED
                    )
                            + ";"
            );

            row.getChildren().addAll(
                    name,
                    spacer,
                    availability
            );

            wardManagementContainer
                    .getChildren()
                    .add(
                            row
                    );
        }
    }

    // =========================================================
    // CALCULATE WARD STATS
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

            if (bed == null
                    || !bed.isActive()) {

                continue;
            }

            if (!wardId.equals(
                    bed.getWardId()
            )) {

                continue;
            }

            stats.total++;

            HospitalBed.BedStatus status =
                    bed.getStatus();

            if (status ==
                    HospitalBed.BedStatus.AVAILABLE) {

                stats.available++;

            } else if (
                    status ==
                            HospitalBed.BedStatus.OCCUPIED) {

                stats.occupied++;

            } else if (
                    status ==
                            HospitalBed.BedStatus.RESERVED) {

                stats.reserved++;

            } else if (
                    status ==
                            HospitalBed.BedStatus.MAINTENANCE) {

                stats.maintenance++;
            }
        }

        return stats;
    }

    // =========================================================
    // STATUS COLOR
    // =========================================================

    private String getStatusColor(
            HospitalBed.BedStatus status) {

        if (status ==
                HospitalBed.BedStatus.AVAILABLE) {

            return SUCCESS_GREEN;
        }

        if (status ==
                HospitalBed.BedStatus.OCCUPIED) {

            return ERROR_RED;
        }

        if (status ==
                HospitalBed.BedStatus.RESERVED) {

            return WARNING_ORANGE;
        }

        if (status ==
                HospitalBed.BedStatus.MAINTENANCE) {

            return MAINTENANCE_GRAY;
        }

        return SECONDARY_TEXT;
    }

    // =========================================================
    // STATUS BACKGROUND
    // =========================================================

    private String getStatusBackground(
            HospitalBed.BedStatus status) {

        if (status ==
                HospitalBed.BedStatus.AVAILABLE) {

            return SUCCESS_LIGHT;
        }

        if (status ==
                HospitalBed.BedStatus.OCCUPIED) {

            return ERROR_LIGHT;
        }

        if (status ==
                HospitalBed.BedStatus.RESERVED) {

            return WARNING_LIGHT;
        }

        if (status ==
                HospitalBed.BedStatus.MAINTENANCE) {

            return MAINTENANCE_LIGHT;
        }

        return LIGHT_BACKGROUND;
    }

    // =========================================================
    // EXPORT BEDS
    // =========================================================

    private void exportBeds() {

        try {

            File file =
                    new File(
                            System.getProperty(
                                    "user.home"
                            ),
                            "healthsphere_beds.csv"
                    );

            try (
                    PrintWriter writer =
                            new PrintWriter(
                                    file
                            )
            ) {

                writer.println(
                        "Bed ID,Bed Number,Ward ID,Bed Type,Status,Patient ID,Active"
                );

                for (HospitalBed bed :
                        bedList) {

                    if (bed == null) {
                        continue;
                    }

                    writer.println(
                            csv(
                                    bed.getBedId()
                            )
                                    + ","
                                    + csv(
                                    bed.getBedNumber()
                            )
                                    + ","
                                    + csv(
                                    bed.getWardId()
                            )
                                    + ","
                                    + csv(
                                    bed.getBedType()
                            )
                                    + ","
                                    + csv(
                                    bed.getStatus() == null
                                            ? ""
                                            : bed.getStatus()
                                            .name()
                            )
                                    + ","
                                    + csv(
                                    bed.getPatientId()
                            )
                                    + ","
                                    + bed.isActive()
                    );
                }
            }

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Export Complete",
                    "Bed data exported to:\n"
                            + file.getAbsolutePath()
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
    // CSV ESCAPE
    // =========================================================

    private String csv(
            String value) {

        String text =
                safe(
                        value
                );

        return "\""
                + text.replace(
                "\"",
                "\"\""
        )
                + "\"";
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

        if (throwable == null) {

            return "Unknown error.";
        }

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
                message == null
                        ? "An unexpected error occurred."
                        : message
        );

        alert.showAndWait();
    }

    // =========================================================
    // WARD STATS
    // =========================================================

    private static class WardStats {

        private int total;
        private int available;
        private int occupied;
        private int reserved;
        private int maintenance;
    }
}