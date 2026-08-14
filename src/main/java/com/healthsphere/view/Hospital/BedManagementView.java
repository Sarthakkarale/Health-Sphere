package com.healthsphere.view.Hospital;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BedManagementView {

    // =========================================================
    // COLOR PALETTE
    // =========================================================
    private static final String PRIMARY_BLUE = "#1920df";
    private static final String PRIMARY_LIGHT = "#EFF5FF";
    private static final String DARK_TEXT = "#0F172A";
    private static final String SECONDARY_TEXT = "#64748B";
    private static final String LIGHT_BACKGROUND = "#F8FAFC";
    private static final String CARD_BG = "#FFFFFF";
    private static final String BORDER = "#E2E8F0";

    // Dark Sidebar Colors
    private static final String DARK_SIDEBAR_BG = "#0F172A";
    private static final String DARK_SIDEBAR_BORDER = "#1E293B";
    private static final String DARK_TEXT_MUTED = "#94A3B8";
    private static final String DARK_ACCENT = "#38BDF8";
    private static final String DARK_HOVER_BG = "#1E293B";

    private static final String SUCCESS_GREEN = "#059669";
    private static final String SUCCESS_LIGHT = "#ECFDF5";

    private static final String WARNING_ORANGE = "#D97706";
    private static final String WARNING_LIGHT = "#FFFBEB";

    private static final String ERROR_RED = "#DC2626";
    private static final String ERROR_LIGHT = "#FEF2F2";

    private static final String PURPLE = "#7C3AED";
    private static final String PURPLE_LIGHT = "#F5F3FF";

    // =========================================================
    // BUS RESERVATION DATA MODELS & DYNAMIC REFRESH LABELS
    // =========================================================
    private enum BedStatus { AVAILABLE, OCCUPIED, RESERVED }
    private final Map<String, BedStatus> bedGridData = new HashMap<>();
    
    private Label totalBedsKpiLabel;
    private Label occupiedKpiLabel;
    private Label availableKpiLabel;
    private ProgressBar totalProgressBar;
    private Label totalOccupancyPctLabel;

    private ComboBox<String> wardFilter;
    private ComboBox<String> statusFilter;
    private TextField searchInput;
    private GridPane reservationGrid;

    public BedManagementView() {
        for (int i = 1; i <= 24; i++) {
            String bedId = "B-" + (i < 10 ? "0" + i : i);
            if (i % 3 == 0) {
                bedGridData.put(bedId, BedStatus.OCCUPIED);
            } else if (i % 7 == 0) {
                bedGridData.put(bedId, BedStatus.RESERVED);
            } else {
                bedGridData.put(bedId, BedStatus.AVAILABLE);
            }
        }
    }

    // =========================================================
    // CREATE SCENE
    // =========================================================
    public Scene createScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + LIGHT_BACKGROUND + ";");

        root.setLeft(createSidebar(stage));
        root.setTop(createTopBar());

        ScrollPane scrollPane = new ScrollPane(createMainContent(stage));
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");

        root.setCenter(scrollPane);

        return new Scene(root, stage.getWidth(), stage.getHeight());
    }

    // =========================================================
    // DARK SIDEBAR
    // =========================================================
    private VBox createSidebar(Stage stage) {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(240);
        sidebar.setPadding(new Insets(24, 16, 20, 16));
        sidebar.setStyle(
                "-fx-background-color: " + DARK_SIDEBAR_BG + ";" +
                "-fx-border-color: " + DARK_SIDEBAR_BORDER + ";" +
                "-fx-border-width: 0 1 0 0;"
        );

        // LOGO
        VBox logoBox = new VBox(2);
        logoBox.setPadding(new Insets(0, 8, 24, 8));

        Label logo = new Label("Health-Sphere");
        logo.setStyle("-fx-font-size: 22px; -fx-font-weight: 800; -fx-text-fill: " + DARK_ACCENT + ";");

        Label subtitle = new Label("SMART HEALTHCARE");
        subtitle.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-letter-spacing: 1px; -fx-text-fill: " + DARK_TEXT_MUTED + ";");

        logoBox.getChildren().addAll(logo, subtitle);
        sidebar.getChildren().add(logoBox);

        // NAVIGATION BUTTONS
        Button dashboardButton = createNavigationButton("▦", "Dashboard", false);
        Button doctorButton = createNavigationButton("♙", "Doctors", false);
        Button departmentButton = createNavigationButton("✚", "Departments", false);
        Button bedButton = createNavigationButton("▥", "Beds", true);
        Button appointmentButton = createNavigationButton("▣", "Appointments", false);
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

        // NAVIGATION ACTIONS
        dashboardButton.setOnAction(event -> {
            HospitalDashboardView dashboardView = new HospitalDashboardView();
            stage.setScene(dashboardView.createScene(stage));
        });

        doctorButton.setOnAction(event -> {
            DoctorManagementView doctorView = new DoctorManagementView();
            stage.setScene(doctorView.createScene(stage));
        });

        departmentButton.setOnAction(event -> {
            DepartmentManagementView departmentView = new DepartmentManagementView();
            stage.setScene(departmentView.createScene(stage));
        });

        appointmentButton.setOnAction(event -> {
            AppointmentManagementView appointmentView = new AppointmentManagementView();
            stage.setScene(appointmentView.createScene(stage));
        });

        analyticsButton.setOnAction(event -> {
            HospitalAnalyticsView analyticsView = new HospitalAnalyticsView();
            stage.setScene(analyticsView.createScene(stage));
        });

        settingsButton.setOnAction(event -> {
            HospitalProfileSettingsView settingsView = new HospitalProfileSettingsView();
            stage.setScene(settingsView.createScene(stage));
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);

        Button helpButton = createNavigationButton("?", "Help Center", false);
        Button logoutButton = createNavigationButton("↪", "Logout", false);

        sidebar.getChildren().addAll(helpButton, logoutButton);

        return sidebar;
    }

    private Button createNavigationButton(String icon, String text, boolean selected) {
        Button button = new Button();

        String unselectedColor = DARK_TEXT_MUTED;
        String selectedColor = DARK_ACCENT;

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + (selected ? selectedColor : unselectedColor) + ";");

        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: " + (selected ? "bold" : "500") + "; -fx-text-fill: " + (selected ? selectedColor : unselectedColor) + ";");

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
            button.setOnMouseEntered(e -> button.setStyle(baseStyle + "-fx-background-color: " + DARK_HOVER_BG + ";"));
            button.setOnMouseExited(e -> button.setStyle(baseStyle + "-fx-background-color: transparent;"));
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
        topBar.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: " + BORDER + "; -fx-border-width: 0 0 1 0;");

        Label searchIcon = new Label("⌕");
        searchIcon.setStyle("-fx-font-size: 18px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        TextField searchField = new TextField();
        searchField.setPromptText("Search beds, wards...");
        searchField.setStyle("-fx-background-color: transparent; -fx-prompt-text-fill: #94A3B8; -fx-font-size: 13px; -fx-text-inner-color: " + DARK_TEXT + ";");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPrefWidth(360);
        searchBox.setPrefHeight(40);
        searchBox.setPadding(new Insets(0, 12, 0, 12));
        searchBox.setStyle("-fx-background-color: " + LIGHT_BACKGROUND + "; -fx-background-radius: 8; -fx-border-color: " + BORDER + "; -fx-border-radius: 8;");
        searchBox.getChildren().addAll(searchIcon, searchField);

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
                createHeader(stage),
                createKpiCards(),
                createFilterBar(),
                createBusReservationGridCard(),
                createLowerSection(stage)
        );

        return content;
    }

    // =========================================================
    // HEADER WITH ACTION BUTTONS
    // =========================================================
    private HBox createHeader(Stage stage) {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Bed Management");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 800; -fx-text-fill: " + DARK_TEXT + ";");

        Label subtitle = new Label("Monitor hospital beds, wards, and real-time availability.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button manageWardsBtn = new Button("Manage Wards");
        manageWardsBtn.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                "-fx-text-fill: " + DARK_TEXT + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: 600;" +
                "-fx-padding: 8 16;" +
                "-fx-cursor: hand;"
        );

        Button addBedBtn = new Button("+ Add Bed");
        addBedBtn.setStyle(
                "-fx-background-color: " + PRIMARY_BLUE + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 16;" +
                "-fx-cursor: hand;"
        );

        manageWardsBtn.setOnAction(e -> {
            ManageWardsView manageWardsView = new ManageWardsView();
            stage.setScene(manageWardsView.createScene(stage));
        });

        addBedBtn.setOnAction(e -> {
            AddBedView addBedView = new AddBedView();
            stage.setScene(addBedView.createScene(stage));
        });

        HBox actionBox = new HBox(12, manageWardsBtn, addBedBtn);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        header.getChildren().addAll(titleBox, spacer, actionBox);

        return header;
    }

    // =========================================================
    // KPI CARDS
    // =========================================================
    private HBox createKpiCards() {
        HBox cards = new HBox(16);

        VBox totalCard = createKpiCard("Total Beds", "520", "Hospital capacity", "=", PRIMARY_BLUE, PRIMARY_LIGHT);
        VBox occupiedCard = createKpiCard("Occupied", "386", "74.2% occupancy rate", "●", ERROR_RED, ERROR_LIGHT);
        VBox availableCard = createKpiCard("Available", "134", "Beds ready for patients", "✓", SUCCESS_GREEN, SUCCESS_LIGHT);
        VBox icuCard = createKpiCard("ICU Beds", "48", "36 occupied currently", "♥", PURPLE, PURPLE_LIGHT);
        VBox emergencyCard = createKpiCard("Emergency Beds", "24", "18 available for triage", "!", WARNING_ORANGE, WARNING_LIGHT);

        totalBedsKpiLabel = (Label) totalCard.getChildren().get(1);
        occupiedKpiLabel = (Label) occupiedCard.getChildren().get(1);
        availableKpiLabel = (Label) availableCard.getChildren().get(1);

        cards.getChildren().addAll(totalCard, occupiedCard, availableCard, icuCard, emergencyCard);

        for (javafx.scene.Node node : cards.getChildren()) {
            HBox.setHgrow(node, Priority.ALWAYS);
        }

        return cards;
    }

    private VBox createKpiCard(String title, String value, String subtitle, String icon, String color, String bgColor) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        applyCardStyle(card);

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label iconLabel = new Label(icon);
        iconLabel.setAlignment(Pos.CENTER);
        iconLabel.setPrefSize(28, 28);
        iconLabel.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                "-fx-background-radius: 6;" +
                "-fx-text-fill: " + color + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );

        top.getChildren().addAll(titleLabel, spacer, iconLabel);

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: 800; -fx-text-fill: " + DARK_TEXT + ";");

        Label subLabel = new Label(subtitle);
        subLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        card.getChildren().addAll(top, valueLabel, subLabel);

        return card;
    }

    // =========================================================
    // FILTER BAR
    // =========================================================
    private HBox createFilterBar() {
        HBox filterBar = new HBox(12);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(12, 16, 12, 16));
        filterBar.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 10; -fx-border-color: " + BORDER + "; -fx-border-radius: 10;");

        searchInput = new TextField();
        searchInput.setPromptText("Search bed number or patient...");
        searchInput.setStyle("-fx-background-color: transparent; -fx-prompt-text-fill: #94A3B8; -fx-font-size: 13px;");
        searchInput.setPrefWidth(260);

        HBox searchContainer = new HBox(searchInput);
        searchContainer.setStyle("-fx-border-color: " + BORDER + "; -fx-border-radius: 6; -fx-padding: 2;");

        wardFilter = new ComboBox<>();
        wardFilter.getItems().addAll("All Wards", "General Ward", "ICU", "Emergency", "Private Ward");
        wardFilter.setValue("All Wards");
        wardFilter.setStyle("-fx-background-color: " + LIGHT_BACKGROUND + "; -fx-border-color: " + BORDER + "; -fx-border-radius: 6;");

        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All Status", "Available", "Occupied", "Reserved");
        statusFilter.setValue("All Status");
        statusFilter.setStyle("-fx-background-color: " + LIGHT_BACKGROUND + "; -fx-border-color: " + BORDER + "; -fx-border-radius: 6;");

        statusFilter.setOnAction(e -> renderReservationGrid());
        searchInput.textProperty().addListener((obs, oldV, newV) -> renderReservationGrid());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button filtersBtn = new Button("⚙ Filters");
        filtersBtn.setStyle("-fx-background-color: transparent; -fx-border-color: " + BORDER + "; -fx-border-radius: 6; -fx-font-size: 12px; -fx-cursor: hand;");

        Button exportBtn = new Button("↓ Export");
        exportBtn.setStyle("-fx-background-color: transparent; -fx-border-color: " + BORDER + "; -fx-border-radius: 6; -fx-font-size: 12px; -fx-cursor: hand;");

        filterBar.getChildren().addAll(searchContainer, wardFilter, statusFilter, spacer, filtersBtn, exportBtn);

        return filterBar;
    }

    // =========================================================
    // BUS RESERVATION STYLE BED MATRIX
    // =========================================================
    private VBox createBusReservationGridCard() {
        VBox card = createCard();

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        Label title = new Label("Visual Bed Layout (Bus Reservation View)");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: 700; -fx-text-fill: " + DARK_TEXT + ";");
        Label subtitle = new Label("Click on any bed seat to manage booking or status");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox legendBox = new HBox(12);
        legendBox.setAlignment(Pos.CENTER_RIGHT);
        legendBox.getChildren().addAll(
                createLegendItem("Available", SUCCESS_GREEN),
                createLegendItem("Occupied", ERROR_RED),
                createLegendItem("Reserved", WARNING_ORANGE)
        );

        header.getChildren().addAll(titleBox, spacer, legendBox);
        card.getChildren().add(header);

        reservationGrid = new GridPane();
        reservationGrid.setHgap(12);
        reservationGrid.setVgap(12);

        renderReservationGrid();

        card.getChildren().add(reservationGrid);

        return card;
    }

    private void renderReservationGrid() {
        if (reservationGrid == null) return;
        reservationGrid.getChildren().clear();

        String selectedStatus = statusFilter != null ? statusFilter.getValue() : "All Status";
        String query = searchInput != null ? searchInput.getText().toLowerCase().trim() : "";

        int col = 0;
        int row = 0;

        for (Map.Entry<String, BedStatus> entry : bedGridData.entrySet()) {
            String bedId = entry.getKey();
            BedStatus status = entry.getValue();

            if (!query.isEmpty() && !bedId.toLowerCase().contains(query)) {
                continue;
            }
            if (!selectedStatus.equals("All Status")) {
                if (selectedStatus.equalsIgnoreCase("Available") && status != BedStatus.AVAILABLE) continue;
                if (selectedStatus.equalsIgnoreCase("Occupied") && status != BedStatus.OCCUPIED) continue;
                if (selectedStatus.equalsIgnoreCase("Reserved") && status != BedStatus.RESERVED) continue;
            }

            Button bedSeatBtn = new Button("🛏 " + bedId);
            bedSeatBtn.setPrefSize(95, 50);

            String statusColor;
            String statusBg;
            if (status == BedStatus.AVAILABLE) {
                statusColor = SUCCESS_GREEN;
                statusBg = SUCCESS_LIGHT;
            } else if (status == BedStatus.OCCUPIED) {
                statusColor = ERROR_RED;
                statusBg = ERROR_LIGHT;
            } else {
                statusColor = WARNING_ORANGE;
                statusBg = WARNING_LIGHT;
            }

            bedSeatBtn.setStyle(
                    "-fx-background-color: " + statusBg + ";" +
                    "-fx-border-color: " + statusColor + ";" +
                    "-fx-border-radius: 8;" +
                    "-fx-background-radius: 8;" +
                    "-fx-text-fill: " + statusColor + ";" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 11px;" +
                    "-fx-cursor: hand;"
            );

            bedSeatBtn.setOnAction(e -> handleBedReservationClick(bedId, status));

            reservationGrid.add(bedSeatBtn, col, row);

            col++;
            if (col == 6) {
                col = 0;
                row++;
            }
        }
    }

    private void handleBedReservationClick(String bedId, BedStatus currentStatus) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Bed Reservation Action");
        alert.setHeaderText("Manage Bed Seat: " + bedId + " (Current: " + currentStatus + ")");
        alert.setContentText("Select an action to update this bed seat's status:");

        ButtonType reserveBtn = new ButtonType("Reserve Bed");
        ButtonType occupyBtn = new ButtonType("Occupy Bed");
        ButtonType releaseBtn = new ButtonType("Make Available");
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonType.CANCEL.getButtonData());

        alert.getButtonTypes().setAll(reserveBtn, occupyBtn, releaseBtn, cancelBtn);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == reserveBtn) {
                bedGridData.put(bedId, BedStatus.RESERVED);
            } else if (result.get() == occupyBtn) {
                bedGridData.put(bedId, BedStatus.OCCUPIED);
            } else if (result.get() == releaseBtn) {
                bedGridData.put(bedId, BedStatus.AVAILABLE);
            }
            updateBedMetrics();
            renderReservationGrid();
        }
    }

    private void updateBedMetrics() {
        int occupiedCount = 0;
        int total = bedGridData.size();

        for (BedStatus status : bedGridData.values()) {
            if (status == BedStatus.OCCUPIED || status == BedStatus.RESERVED) {
                occupiedCount++;
            }
        }

        int availableCount = total - occupiedCount;
        double ratio = (double) occupiedCount / total;

        if (totalBedsKpiLabel != null) totalBedsKpiLabel.setText(String.valueOf(total));
        if (occupiedKpiLabel != null) occupiedKpiLabel.setText(String.valueOf(occupiedCount));
        if (availableKpiLabel != null) availableKpiLabel.setText(String.valueOf(availableCount));

        if (totalProgressBar != null) totalProgressBar.setProgress(ratio);
        if (totalOccupancyPctLabel != null) {
            totalOccupancyPctLabel.setText(String.format("%.1f%%", ratio * 100));
        }
    }

    // =========================================================
    // LOWER SECTION (BED AVAILABILITY & WARD MANAGEMENT)
    // =========================================================
    private HBox createLowerSection(Stage stage) {
        HBox lower = new HBox(20);

        VBox bedAvailability = createBedAvailabilityCard();
        VBox wardManagement = createWardManagementCard(stage);

        lower.getChildren().addAll(bedAvailability, wardManagement);

        HBox.setHgrow(bedAvailability, Priority.ALWAYS);
        HBox.setHgrow(wardManagement, Priority.ALWAYS);

        return lower;
    }

    private VBox createBedAvailabilityCard() {
        VBox card = createCard();

        HBox header = new HBox();
        VBox text = new VBox(2);
        Label title = new Label("Bed Availability");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: 700; -fx-text-fill: " + DARK_TEXT + ";");
        Label subtitle = new Label("Current hospital occupancy breakdown");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        text.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        totalOccupancyPctLabel = new Label("74.2%");
        totalOccupancyPctLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: 800; -fx-text-fill: " + PRIMARY_BLUE + ";");

        header.getChildren().addAll(text, spacer, totalOccupancyPctLabel);
        card.getChildren().add(header);

        totalProgressBar = new ProgressBar(0.742);
        totalProgressBar.setMaxWidth(Double.MAX_VALUE);
        totalProgressBar.setStyle("-fx-accent: " + PRIMARY_BLUE + ";");
        card.getChildren().add(totalProgressBar);

        HBox legend = new HBox(16);
        legend.getChildren().addAll(
                createLegendItem("Occupied (386)", ERROR_RED),
                createLegendItem("Available (134)", SUCCESS_GREEN),
                createLegendItem("Reserved (18)", WARNING_ORANGE)
        );
        card.getChildren().add(legend);

        VBox wardBars = new VBox(12);
        wardBars.getChildren().addAll(
                createWardProgressBar("General Ward", 180, 240, PRIMARY_BLUE),
                createWardProgressBar("ICU", 36, 48, PURPLE),
                createWardProgressBar("Emergency", 6, 24, ERROR_RED),
                createWardProgressBar("Private Ward", 84, 120, PRIMARY_BLUE)
        );

        card.getChildren().add(wardBars);

        return card;
    }

    private HBox createLegendItem(String label, String color) {
        HBox item = new HBox(6);
        item.setAlignment(Pos.CENTER_LEFT);
        Circle dot = new Circle(4, Color.web(color));
        Label text = new Label(label);
        text.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        item.getChildren().addAll(dot, text);
        return item;
    }

    private VBox createWardProgressBar(String wardName, int occupied, int total, String color) {
        VBox box = new VBox(4);
        HBox top = new HBox();

        Label name = new Label(wardName);
        name.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: " + DARK_TEXT + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label count = new Label(occupied + " / " + total);
        count.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        top.getChildren().addAll(name, spacer, count);

        ProgressBar bar = new ProgressBar((double) occupied / total);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setStyle("-fx-accent: " + color + ";");

        box.getChildren().addAll(top, bar);

        return box;
    }

    private VBox createWardManagementCard(Stage stage) {
        VBox card = createCard();

        HBox header = new HBox();
        VBox text = new VBox(2);
        Label title = new Label("Ward Management");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: 700; -fx-text-fill: " + DARK_TEXT + ";");
        Label subtitle = new Label("Ward-wise availability details");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        text.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewAll = new Button("View All Wards");
        viewAll.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-cursor: hand;");
        viewAll.setOnAction(e -> {
            ManageWardsView wardView = new ManageWardsView();
            stage.setScene(wardView.createScene(stage));
        });

        header.getChildren().addAll(text, spacer, viewAll);
        card.getChildren().add(header);

        VBox wardList = new VBox(14);
        wardList.getChildren().addAll(
                createWardDetailRow("General Ward", "240 Beds", "60 Available", "75% occupied", SUCCESS_GREEN, PRIMARY_LIGHT),
                createWardDetailRow("ICU", "48 Beds", "12 Available", "75% occupied", PURPLE, PURPLE_LIGHT),
                createWardDetailRow("Emergency", "24 Beds", "18 Available", "25% occupied", WARNING_ORANGE, WARNING_LIGHT),
                createWardDetailRow("Private Ward", "120 Beds", "36 Available", "70% occupied", PRIMARY_BLUE, PRIMARY_LIGHT)
        );

        card.getChildren().add(wardList);

        return card;
    }

    private HBox createWardDetailRow(String wardName, String totalBeds, String available, String occupiedPct, String color, String bgColor) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 0, 8, 0));

        Label icon = new Label("=");
        icon.setAlignment(Pos.CENTER);
        icon.setPrefSize(32, 32);
        icon.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: " + color + "; -fx-background-radius: 6; -fx-font-weight: bold;");

        VBox wardInfo = new VBox(2);
        Label name = new Label(wardName);
        name.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: " + DARK_TEXT + ";");
        Label beds = new Label(totalBeds);
        beds.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        wardInfo.getChildren().addAll(name, beds);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox statusInfo = new VBox(2);
        statusInfo.setAlignment(Pos.CENTER_RIGHT);
        Label availLabel = new Label(available);
        availLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: " + color + ";");
        Label pctLabel = new Label(occupiedPct);
        pctLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        statusInfo.getChildren().addAll(availLabel, pctLabel);

        row.getChildren().addAll(icon, wardInfo, spacer, statusInfo);

        return row;
    }

    // =========================================================
    // CARD STYLING
    // =========================================================
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
}