package com.healthsphere.view.hospital;

import com.healthsphere.controller.hospital.HospitalDashboardController;
import com.healthsphere.model.Appointment;
import com.healthsphere.model.HospitalDepartment;
import com.healthsphere.util.SessionManager;
import com.healthsphere.util.ShimmerPlaceholder;
import com.healthsphere.util.Navigation;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * HospitalDashboardView
 *
 * Dynamic Hospital Dashboard for Health-Sphere.
 *
 * Architecture:
 *
 * HospitalDashboardView
 *          ↓
 * HospitalDashboardController
 *          ↓
 * Existing Hospital Controllers
 *          ↓
 * Existing DAOs
 *          ↓
 * Firebase Firestore
 *
 * No Firestore/database code is placed directly in this View.
 */
public class HospitalDashboardView {

    // =========================================================
    // COLORS
    // =========================================================

    private static final String PRIMARY_BLUE =
            "#1E62D0";

    private static final String PRIMARY_LIGHT =
            "#EFF5FF";

    private static final String DARK_TEXT =
            "#0F172A";

    private static final String SECONDARY_TEXT =
            "#64748B";

    private static final String LIGHT_BACKGROUND =
            "linear-gradient(to bottom right, #EFF6FF, #F5F3FF, #F8FAFC)";

    private static final String CARD_BG =
            "#FFFFFF";

    private static final String BORDER =
            "#E2E8F0";

    private static final String DARK_SIDEBAR_BG =
            "#0F172A";

    private static final String DARK_SIDEBAR_BORDER =
            "#1E293B";

    private static final String DARK_TEXT_MUTED =
            "#94A3B8";

    private static final String DARK_ACCENT =
            "#38BDF8";

    private static final String DARK_HOVER_BG =
            "#1E293B";

    private static final String SIDEBAR_SELECTED =
            "#170ECA";

    private static final String SUCCESS_GREEN =
            "#059669";

    private static final String SUCCESS_LIGHT =
            "#ECFDF5";

    private static final String WARNING_ORANGE =
            "#D97706";

    private static final String WARNING_LIGHT =
            "#FFFBEB";

    private static final String ERROR_RED =
            "#DC2626";

    private static final String ERROR_LIGHT =
            "#FEF2F2";

    private static final String PURPLE =
            "#7C3AED";

    private static final String PURPLE_LIGHT =
            "#F5F3FF";

    // =========================================================
    // CONTROLLER
    // =========================================================

    private final HospitalDashboardController
            dashboardController;

    // =========================================================
    // DYNAMIC UI REFERENCES
    // =========================================================

    private Label totalDoctorsValue;

    private Label totalAppointmentsValue;

    private Label availableBedsValue;

    private Label emergencyCasesValue;

    private Label todayApptLabel;

    private Label completedApptLabel;

    private Label waitingApptLabel;

    private Label upcomingApptLabel;

    private Label cancelledApptLabel;

    private Label bedOccupancyLabel;

    private Label bedCapacityDetailsLabel;

    private StackPane bedProgressContainer;

    private Region bedOccupancyProgress;

    private double currentOccupancyPercentage = 0.0;

    private VBox departmentRowsContainer;

    private VBox recentActivityList;

    private Stage stage;

    public HospitalDashboardView() {
        dashboardController = new HospitalDashboardController();
    }

    public HospitalDashboardView(Stage stage) {
        this.stage = stage;
        dashboardController = new HospitalDashboardController();
    }

    public Scene getScene() {
        return createScene(this.stage);
    }

    public Scene getScene(Stage stage) {
        this.stage = stage;
        return createScene(stage);
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
                HospitalSidebar.createSidebar(stage, HospitalSidebar.HospitalTab.DASHBOARD)
        );

        root.setTop(
                createTopBar(stage)
        );

        ScrollPane scrollPane =
                new ScrollPane(
                        createMainContent(stage)
                );

        scrollPane.setFitToWidth(true);

        scrollPane.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;" +
                "-fx-padding: 0;"
        );

        root.setCenter(
                scrollPane
        );

        /*
         * Load real Firestore-backed information.
         */
        refreshDashboardData();

        return new Scene(
                root,
                stage.getWidth(),
                stage.getHeight()
        );
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private VBox createSidebar(
            Stage stage
    ) {
        return HospitalSidebar.createSidebar(stage, HospitalSidebar.HospitalTab.DASHBOARD);
    }

    // =========================================================
    // NAVIGATION BUTTON
    // =========================================================

    private Button createNavigationButton(
            String icon,
            String text,
            boolean selected
    ) {

        Button button =
                new Button();

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-text-fill: "
                        + (
                        selected
                                ? "#FFFFFF"
                                : DARK_TEXT_MUTED
                )
                        + ";"
        );

        Label textLabel =
                new Label(text);

        textLabel.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: "
                        + (
                        selected
                                ? "bold"
                                : "500"
                )
                        + ";" +
                "-fx-text-fill: "
                        + (
                        selected
                                ? "#FFFFFF"
                                : DARK_TEXT_MUTED
                )
                        + ";"
        );

        HBox content =
                new HBox(12);

        content.setAlignment(
                Pos.CENTER_LEFT
        );

        content.getChildren().addAll(
                iconLabel,
                textLabel
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
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;";

        if (selected) {

            button.setStyle(
                    baseStyle +
                    "-fx-background-color: "
                            + SIDEBAR_SELECTED
                            + ";"
            );

        } else {

            button.setStyle(
                    baseStyle +
                    "-fx-background-color: transparent;"
            );

            button.setOnMouseEntered(
                    event ->
                            button.setStyle(
                                    baseStyle +
                                    "-fx-background-color: "
                                            + DARK_HOVER_BG
                                            + ";"
                            )
            );

            button.setOnMouseExited(
                    event ->
                            button.setStyle(
                                    baseStyle +
                                    "-fx-background-color: transparent;"
                            )
            );
        }

        return button;
    }

    // =========================================================
    // TOP BAR
    // =========================================================

    private HBox createTopBar(
            Stage stage
    ) {

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
                        + ";" +
                "-fx-border-color: "
                        + BORDER
                        + ";" +
                "-fx-border-width: 0 0 1 0;"
        );

        // =====================================================
        // SEARCH
        // =====================================================

        Label searchIcon =
                new Label("⌕");

        searchIcon.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        TextField searchField =
                new TextField();

        searchField.setPromptText(
                "Search patients, doctors, records..."
        );

        searchField.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-prompt-text-fill: #94A3B8;" +
                "-fx-font-size: 13px;" +
                "-fx-text-inner-color: "
                        + DARK_TEXT
                        + ";"
        );

        searchField.setOnAction(
                event ->
                        handleSearch(
                                searchField.getText()
                        )
        );

        HBox searchBox =
                new HBox(8);

        searchBox.setAlignment(
                Pos.CENTER_LEFT
        );

        searchBox.setPrefWidth(
                360
        );

        searchBox.setPrefHeight(
                40
        );

        searchBox.setPadding(
                new Insets(
                        0,
                        12,
                        0,
                        12
                )
        );

        searchBox.setStyle(
                "-fx-background-color: "
                        + LIGHT_BACKGROUND
                        + ";" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: "
                        + BORDER
                        + ";" +
                "-fx-border-radius: 8;"
        );

        searchBox.getChildren().addAll(
                searchIcon,
                searchField
        );

        // =====================================================
        // SPACER
        // =====================================================

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        // =====================================================
        // NOTIFICATION
        // =====================================================

        Label notification =
                new Label("🔔");

        notification.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-cursor: hand;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        notification.setOnMouseClicked(
                event ->
                        showNotificationsMenu(
                                notification
                        )
        );

        // =====================================================
        // SETTINGS
        // =====================================================

        Label settings =
                new Label("⚙");

        settings.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-cursor: hand;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        settings.setOnMouseClicked(
                event ->
                        stage.setScene(
                                new HospitalProfileSettingsView()
                                        .createScene(stage)
                        )
        );

        // =====================================================
        // ADMIN INFO
        // =====================================================

        Label administrator =
                new Label(
                        "Hospital Administrator"
                );

        administrator.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Label role =
                new Label(
                        "HOSPITAL ADMIN"
                );

        role.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        VBox userInfo =
                new VBox(2);

        userInfo.setAlignment(
                Pos.CENTER_RIGHT
        );

        userInfo.getChildren().addAll(
                administrator,
                role
        );

        // =====================================================
        // AVATAR
        // =====================================================

        Circle avatar =
                new Circle(18);

        avatar.setFill(
                Color.web(
                        PRIMARY_LIGHT
                )
        );

        avatar.setStroke(
                Color.web(
                        BORDER
                )
        );

        Label avatarText =
                new Label("HA");

        avatarText.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: "
                        + PRIMARY_BLUE
                        + ";"
        );

        StackPane avatarBox =
                new StackPane(
                        avatar,
                        avatarText
                );

        avatarBox.setStyle(
                "-fx-cursor: hand;"
        );

        avatarBox.setOnMouseClicked(
                event ->
                        showProfileMenu(
                                avatarBox,
                                stage
                        )
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

    private VBox createMainContent(
            Stage stage
    ) {

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
                createHeader(),
                createHospitalImageBanner(),
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

        VBox header =
                new VBox(4);

        HBox titleRow =
                new HBox();

        titleRow.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox titleBox =
                new VBox(4);

        Label title =
                new Label(
                        "Hospital Dashboard"
                );

        title.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Label subtitle =
                new Label(
                        "Overview of hospital operations, "
                                + "bed capacity, and today's schedule"
                );

        subtitle.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: "
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

        Button refreshButton =
                new Button(
                        "↻  Refresh"
                );

        refreshButton.setPrefHeight(
                38
        );

        refreshButton.setPadding(
                new Insets(
                        0,
                        16,
                        0,
                        16
                )
        );

        refreshButton.setStyle(
                "-fx-background-color: "
                        + CARD_BG
                        + ";" +
                "-fx-text-fill: "
                        + PRIMARY_BLUE
                        + ";" +
                "-fx-border-color: "
                        + BORDER
                        + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        refreshButton.setOnAction(
                event ->
                        refreshDashboardData()
        );

        titleRow.getChildren().addAll(
                titleBox,
                spacer,
                refreshButton
        );

        header.getChildren().add(
                titleRow
        );

        return header;
    }

    // =========================================================
    // HOSPITAL IMAGE BANNER
    // =========================================================

    /**
     * Displays the hospital image supplied for the dashboard.
     *
     * The image contains no dashboard text. Hospital information
     * remains separate from the image so the original photograph
     * is not modified.
     *
     * Image location:
     *
     * src/main/resources/images/hospital_dashboard.jpg
     */
    private GridPane createHospitalImageBanner() {

        /*
         * STATIC THREE-IMAGE ROW
         *
         * Only three photographs are displayed here.
         * No animation, slideshow, timer, overlay text or transition.
         * The row has a small fixed height so it does NOT cover the
         * dashboard content below it.
         */
        GridPane imageGrid = new GridPane();

        imageGrid.setHgap(12);
        imageGrid.setVgap(0);
        imageGrid.setAlignment(Pos.CENTER);

        imageGrid.setPrefHeight(190);
        imageGrid.setMinHeight(190);
        imageGrid.setMaxHeight(190);

        imageGrid.setMaxWidth(Double.MAX_VALUE);

        ColumnConstraints c1 = new ColumnConstraints();
        ColumnConstraints c2 = new ColumnConstraints();
        ColumnConstraints c3 = new ColumnConstraints();

        c1.setPercentWidth(33.3333);
        c2.setPercentWidth(33.3333);
        c3.setPercentWidth(33.3334);

        c1.setHgrow(Priority.ALWAYS);
        c2.setHgrow(Priority.ALWAYS);
        c3.setHgrow(Priority.ALWAYS);

        imageGrid.getColumnConstraints().addAll(c1, c2, c3);

        StackPane image1 = createDashboardImage(
                "/images/hospital_dashboard.jpg"
        );

        StackPane image2 = createDashboardImage(
                "/images/hospital_surgery.jpg"
        );

        StackPane image3 = createDashboardImage(
                "/images/hospital_care.jpg"
        );

        imageGrid.add(image1, 0, 0);
        imageGrid.add(image2, 1, 0);
        imageGrid.add(image3, 2, 0);

        GridPane.setHgrow(image1, Priority.ALWAYS);
        GridPane.setHgrow(image2, Priority.ALWAYS);
        GridPane.setHgrow(image3, Priority.ALWAYS);

        GridPane.setFillWidth(image1, true);
        GridPane.setFillWidth(image2, true);
        GridPane.setFillWidth(image3, true);

        return imageGrid;
    }

    // =========================================================
    // STATIC DASHBOARD IMAGE
    // =========================================================

    private StackPane createDashboardImage(String resourcePath) {

        StackPane frame = new StackPane();

        frame.setPrefHeight(190);
        frame.setMinHeight(190);
        frame.setMaxHeight(190);
        frame.setMinWidth(0);
        frame.setMaxWidth(Double.MAX_VALUE);

        frame.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 14;"
        );

        java.io.InputStream stream =
                getClass().getResourceAsStream(resourcePath);

        if (stream == null) {

            Label missing = new Label(
                    "Image not found"
            );

            missing.setStyle(
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: " + SECONDARY_TEXT + ";"
            );

            frame.getChildren().add(missing);
            return frame;
        }

        Image image = new Image(stream);

        if (image.isError()) {

            Label error = new Label(
                    "Unable to load image"
            );

            error.setStyle(
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: " + ERROR_RED + ";"
            );

            frame.getChildren().add(error);
            return frame;
        }

        ImageView imageView = new ImageView(image);

        imageView.setSmooth(true);
        imageView.setCache(true);
        imageView.setPreserveRatio(true);

        /*
         * The image must COVER the complete card.
         *
         * We use the ImageView viewport to crop the source image
         * and then fit the cropped image to the complete card.
         * This prevents white/empty space while also preventing
         * the photograph from being stretched.
         */
        imageView.fitWidthProperty().bind(
                frame.widthProperty()
        );

        imageView.fitHeightProperty().bind(
                frame.heightProperty()
        );

        Rectangle clip = new Rectangle();

        clip.setArcWidth(28);
        clip.setArcHeight(28);

        clip.widthProperty().bind(
                frame.widthProperty()
        );

        clip.heightProperty().bind(
                frame.heightProperty()
        );

        imageView.setClip(clip);

        StackPane.setAlignment(
                imageView,
                Pos.CENTER
        );

        frame.getChildren().add(imageView);

        /*
         * Recalculate the source viewport whenever the card or
         * source image dimensions become available/change.
         */
        Runnable updateCrop = () -> {

            double frameWidth = frame.getWidth();
            double frameHeight = frame.getHeight();

            double imageWidth = image.getWidth();
            double imageHeight = image.getHeight();

            if (
                    frameWidth <= 0 ||
                    frameHeight <= 0 ||
                    imageWidth <= 0 ||
                    imageHeight <= 0
            ) {
                return;
            }

            double frameRatio =
                    frameWidth / frameHeight;

            double imageRatio =
                    imageWidth / imageHeight;

            double viewportWidth = imageWidth;
            double viewportHeight = imageHeight;
            double viewportX = 0;
            double viewportY = 0;

            /*
             * Landscape source: crop the left/right edges.
             */
            if (imageRatio > frameRatio) {

                viewportWidth =
                        imageHeight * frameRatio;

                viewportX =
                        (imageWidth - viewportWidth) / 2.0;
            }

            /*
             * Portrait source: crop the top/bottom edges.
             */
            else if (imageRatio < frameRatio) {

                viewportHeight =
                        imageWidth / frameRatio;

                viewportY =
                        (imageHeight - viewportHeight) / 2.0;
            }

            imageView.setViewport(
                    new javafx.geometry.Rectangle2D(
                            viewportX,
                            viewportY,
                            viewportWidth,
                            viewportHeight
                    )
            );
        };

        frame.widthProperty().addListener(
                (observable, oldValue, newValue) ->
                        updateCrop.run()
        );

        frame.heightProperty().addListener(
                (observable, oldValue, newValue) ->
                        updateCrop.run()
        );

        image.widthProperty().addListener(
                (observable, oldValue, newValue) ->
                        updateCrop.run()
        );

        image.heightProperty().addListener(
                (observable, oldValue, newValue) ->
                        updateCrop.run()
        );

        javafx.application.Platform.runLater(
                updateCrop
        );

        return frame;
    }

    // =========================================================
    // KPI CARDS
    // =========================================================

    private HBox createKpiCards() {

        HBox cards =
                new HBox(18);

        VBox doctorsCard =
                createKpiCard(
                        "Total Doctors",
                        "0",
                        "From hospital doctor roster",
                        "♙",
                        PRIMARY_BLUE,
                        PRIMARY_LIGHT
                );

        VBox appointmentsCard =
                createKpiCard(
                        "Total Appointments",
                        "0",
                        "All hospital appointments",
                        "▣",
                        PURPLE,
                        PURPLE_LIGHT
                );

        VBox bedsCard =
                createKpiCard(
                        "Available Beds",
                        "0",
                        "From hospital bed roster",
                        "▥",
                        SUCCESS_GREEN,
                        SUCCESS_LIGHT
                );

        VBox emergencyCard =
                createKpiCard(
                        "Emergency Cases",
                        "0",
                        "Current hospital emergency cases",
                        "!",
                        ERROR_RED,
                        ERROR_LIGHT
                );

        totalDoctorsValue =
                getCardValueLabel(
                        doctorsCard
                );

        totalAppointmentsValue =
                getCardValueLabel(
                        appointmentsCard
                );

        availableBedsValue =
                getCardValueLabel(
                        bedsCard
                );

        emergencyCasesValue =
                getCardValueLabel(
                        emergencyCard
                );

        cards.getChildren().addAll(
                doctorsCard,
                appointmentsCard,
                bedsCard,
                emergencyCard
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
            String description,
            String icon,
            String color,
            String iconBackground
    ) {

        VBox card =
                new VBox(10);

        card.setMinHeight(
                145
        );

        card.setPadding(
                new Insets(20)
        );

        applyCardStyle(card);

        HBox top =
                new HBox();

        top.setAlignment(
                Pos.CENTER_LEFT
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: 600;" +
                "-fx-text-fill: "
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
                new Label(icon);

        iconLabel.setAlignment(
                Pos.CENTER
        );

        iconLabel.setPrefSize(
                38,
                38
        );

        iconLabel.setStyle(
                "-fx-background-color: "
                        + iconBackground
                        + ";" +
                "-fx-background-radius: 10;" +
                "-fx-text-fill: "
                        + color
                        + ";" +
                "-fx-font-size: 18px;" +
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
                "-fx-font-size: 28px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setWrapText(
                true
        );

        descriptionLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: "
                        + color
                        + ";"
        );

        card.getChildren().addAll(
                top,
                valueLabel,
                descriptionLabel
        );

        return card;
    }

    private Label getCardValueLabel(
            VBox card
    ) {

        if (
                card.getChildren().size() > 1
                &&
                card.getChildren().get(1)
                        instanceof Label
        ) {

            return (Label)
                    card.getChildren().get(1);
        }

        return new Label("0");
    }

    // =========================================================
    // MIDDLE SECTION
    // =========================================================

    private HBox createMiddleSection() {

        HBox section =
                new HBox(20);

        VBox appointmentOverview =
                createAppointmentOverview();

        VBox bedOccupancy =
                createBedOccupancy();

        section.getChildren().addAll(
                appointmentOverview,
                bedOccupancy
        );

        HBox.setHgrow(
                appointmentOverview,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                bedOccupancy,
                Priority.ALWAYS
        );

        return section;
    }

    // =========================================================
    // APPOINTMENT OVERVIEW
    // =========================================================

    private VBox createAppointmentOverview() {

        VBox card =
                createCard();

        card.getChildren().add(
                createCardHeading(
                        "Appointment Overview",
                        "Hospital appointment status breakdown"
                )
        );

        GridPane grid =
                new GridPane();

        grid.setHgap(
                14
        );

        grid.setVgap(
                14
        );

        // -----------------------------------------------------
        // COMPLETED
        // -----------------------------------------------------

        VBox completedBox =
                createStatusBox(
                        "Completed",
                        "0",
                        SUCCESS_GREEN,
                        SUCCESS_LIGHT
                );

        completedApptLabel =
                getStatusValueLabel(
                        completedBox
                );

        // -----------------------------------------------------
        // WAITING
        // -----------------------------------------------------

        VBox waitingBox =
                createStatusBox(
                        "Waiting",
                        "0",
                        WARNING_ORANGE,
                        WARNING_LIGHT
                );

        waitingApptLabel =
                getStatusValueLabel(
                        waitingBox
                );

        // -----------------------------------------------------
        // UPCOMING
        // -----------------------------------------------------

        VBox upcomingBox =
                createStatusBox(
                        "Upcoming",
                        "0",
                        PRIMARY_BLUE,
                        PRIMARY_LIGHT
                );

        upcomingApptLabel =
                getStatusValueLabel(
                        upcomingBox
                );

        // -----------------------------------------------------
        // CANCELLED
        // -----------------------------------------------------

        VBox cancelledBox =
                createStatusBox(
                        "Cancelled",
                        "0",
                        ERROR_RED,
                        ERROR_LIGHT
                );

        cancelledApptLabel =
                getStatusValueLabel(
                        cancelledBox
                );

        // -----------------------------------------------------
        // TODAY
        // -----------------------------------------------------

        VBox todayBox =
                createStatusBox(
                        "Today",
                        "0",
                        PURPLE,
                        PURPLE_LIGHT
                );

        todayApptLabel =
                getStatusValueLabel(
                        todayBox
                );

        grid.add(
                completedBox,
                0,
                0
        );

        grid.add(
                waitingBox,
                1,
                0
        );

        grid.add(
                upcomingBox,
                0,
                1
        );

        grid.add(
                cancelledBox,
                1,
                1
        );

        grid.add(
                todayBox,
                0,
                2,
                2,
                1
        );

        card.getChildren().add(
                grid
        );

        return card;
    }

    // =========================================================
    // STATUS BOX
    // =========================================================

    private VBox createStatusBox(
            String title,
            String value,
            String color,
            String background
    ) {

        VBox box =
                new VBox(6);

        box.setPadding(
                new Insets(16)
        );

        box.setStyle(
                "-fx-background-color: "
                        + background
                        + ";" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: "
                        + color
                        + "33;" +
                "-fx-border-radius: 10;"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: 600;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-font-size: 24px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: "
                        + color
                        + ";"
        );

        box.getChildren().addAll(
                titleLabel,
                valueLabel
        );

        return box;
    }

    private Label getStatusValueLabel(
            VBox box
    ) {

        if (
                box.getChildren().size() > 1
                &&
                box.getChildren().get(1)
                        instanceof Label
        ) {

            return (Label)
                    box.getChildren().get(1);
        }

        return new Label("0");
    }

    // =========================================================
    // BED OCCUPANCY
    // =========================================================

    private VBox createBedOccupancy() {

        VBox card =
                createCard();

        card.getChildren().add(
                createCardHeading(
                        "Bed Occupancy",
                        "Current hospital capacity status"
                )
        );

        HBox occupancyHeader =
                new HBox();

        occupancyHeader.setAlignment(
                Pos.BASELINE_LEFT
        );

        bedOccupancyLabel =
                new Label("0.0%");

        bedOccupancyLabel.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Label occupancyText =
                new Label(
                        " occupancy"
                );

        occupancyText.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        occupancyHeader.getChildren().addAll(
                bedOccupancyLabel,
                occupancyText
        );

        card.getChildren().add(
                occupancyHeader
        );

        // -----------------------------------------------------
        // PROGRESS BAR
        // -----------------------------------------------------

        bedProgressContainer =
                new StackPane();

        bedProgressContainer.setPrefHeight(
                12
        );

        Region background =
                new Region();

        background.setMaxWidth(
                Double.MAX_VALUE
        );

        background.setPrefHeight(
                10
        );

        background.setStyle(
                "-fx-background-color: #E2E8F0;" +
                "-fx-background-radius: 10;"
        );

        bedOccupancyProgress =
                new Region();

        bedOccupancyProgress.setPrefWidth(
                0
        );

        bedOccupancyProgress.setPrefHeight(
                10
        );

        bedOccupancyProgress.setStyle(
                "-fx-background-color: "
                        + PRIMARY_BLUE
                        + ";" +
                "-fx-background-radius: 10;"
        );

        StackPane.setAlignment(
                bedOccupancyProgress,
                Pos.CENTER_LEFT
        );

        bedProgressContainer
                .getChildren()
                .addAll(
                        background,
                        bedOccupancyProgress
                );

        bedProgressContainer
                .widthProperty()
                .addListener(
                        (observable, oldWidth, newWidth) ->
                                updateBedOccupancyProgress()
                );

        card.getChildren().add(
                bedProgressContainer
        );

        // -----------------------------------------------------
        // CURRENT CAPACITY
        // -----------------------------------------------------

        VBox information =
                new VBox(6);

        bedCapacityDetailsLabel =
                new Label(
                        "Total: 0  |  Occupied: 0  |  "
                                + "Available: 0  |  Reserved: 0"
                );

        bedCapacityDetailsLabel.setWrapText(
                true
        );

        bedCapacityDetailsLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: 600;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        information.getChildren().add(
                bedCapacityDetailsLabel
        );

        card.getChildren().add(
                information
        );

        return card;
    }

    // =========================================================
    // BOTTOM SECTION
    // =========================================================

    private HBox createBottomSection(
            Stage stage
    ) {

        HBox section =
                new HBox(20);

        VBox departments =
                createDepartmentStatistics();

        VBox activities =
                createRecentActivities();

        VBox quickActions =
                createQuickActions(stage);

        section.getChildren().addAll(
                departments,
                activities,
                quickActions
        );

        HBox.setHgrow(
                departments,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                activities,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                quickActions,
                Priority.ALWAYS
        );

        return section;
    }

    // =========================================================
    // DEPARTMENT STATISTICS
    // =========================================================

    private VBox createDepartmentStatistics() {

        VBox card =
                createCard();

        card.getChildren().add(
                createCardHeading(
                        "Departments",
                        "Doctors allocation"
                )
        );

        departmentRowsContainer =
                new VBox(12);

        card.getChildren().add(
                departmentRowsContainer
        );

        refreshDepartmentSection();

        return card;
    }

    private void refreshDepartmentSection() {

        if (
                departmentRowsContainer == null
        ) {

            return;
        }

        departmentRowsContainer
                .getChildren()
                .clear();

        try {

            List<HospitalDepartment>
                    departments =
                    dashboardController
                            .getDepartments();

            String[] colors = {
                    PRIMARY_BLUE,
                    PURPLE,
                    SUCCESS_GREEN,
                    WARNING_ORANGE
            };

            if (
                    departments == null
                    ||
                    departments.isEmpty()
            ) {

                Label empty =
                        new Label(
                                "No departments found."
                        );

                empty.setStyle(
                        "-fx-font-size: 12px;" +
                        "-fx-text-fill: "
                                + SECONDARY_TEXT
                                + ";"
                );

                departmentRowsContainer
                        .getChildren()
                        .add(
                                empty
                        );

                return;
            }

            int colorIndex = 0;

            for (
                    HospitalDepartment department :
                    departments
            ) {

                if (
                        department == null
                ) {

                    continue;
                }

                String departmentId =
                        safe(
                                department
                                        .getDepartmentId()
                        );

                String departmentName =
                        safe(
                                department
                                        .getName()
                        );

                if (
                        departmentName.isEmpty()
                ) {

                    departmentName =
                            "Unnamed Department";
                }

                int doctorCount =
                        dashboardController
                                .getDoctorsForDepartment(
                                        departmentId
                                );

                departmentRowsContainer
                        .getChildren()
                        .add(
                                createDepartmentRow(
                                        departmentName,
                                        String.valueOf(
                                                doctorCount
                                        ),
                                        colors[
                                                colorIndex
                                                        % colors.length
                                        ]
                                )
                        );

                colorIndex++;
            }

        } catch (Exception e) {

            Label error =
                    new Label(
                            "Unable to load departments."
                    );

            error.setWrapText(
                    true
            );

            error.setStyle(
                    "-fx-font-size: 12px;" +
                    "-fx-text-fill: "
                            + ERROR_RED
                            + ";"
            );

            departmentRowsContainer
                    .getChildren()
                    .add(
                            error
                    );
        }
    }

    // =========================================================
    // DEPARTMENT ROW
    // =========================================================

    private HBox createDepartmentRow(
            String department,
            String doctorCount,
            String color
    ) {

        HBox row =
                new HBox(10);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle dot =
                new Circle(
                        4,
                        Color.web(color)
                );

        Label departmentLabel =
                new Label(
                        department
                );

        departmentLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: 500;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label countLabel =
                new Label(
                        doctorCount
                                + " Doctors"
                );

        countLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: 600;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        row.getChildren().addAll(
                dot,
                departmentLabel,
                spacer,
                countLabel
        );

        return row;
    }

    // =========================================================
    // RECENT ACTIVITIES
    // =========================================================

    private VBox createRecentActivities() {

        VBox card =
                createCard();

        card.getChildren().add(
                createCardHeading(
                        "Recent Activities",
                        "Latest hospital updates"
                )
        );

        recentActivityList =
                new VBox(12);

        card.getChildren().add(
                recentActivityList
        );

        refreshRecentActivities();

        return card;
    }

    private void refreshRecentActivities() {

        if (
                recentActivityList == null
        ) {

            return;
        }

        recentActivityList
                .getChildren()
                .clear();

        try {

            List<Appointment>
                    appointments =
                    dashboardController
                            .getRecentAppointments(
                                    5
                            );

            if (
                    appointments == null
                    ||
                    appointments.isEmpty()
            ) {

                recentActivityList
                        .getChildren()
                        .add(
                                createActivity(
                                        "No recent appointment activity",
                                        "No activity",
                                        SECONDARY_TEXT
                                )
                        );

                return;
            }

            for (
                    Appointment appointment :
                    appointments
            ) {

                String patient =
                        safe(
                                appointment
                                        .getPatientName()
                        );

                if (
                        patient.isEmpty()
                ) {

                    patient =
                            "Unknown patient";
                }

                String status =
                        safe(
                                appointment
                                        .getStatus()
                        );

                String activity =
                        getActivityText(
                                patient,
                                status
                        );

                String color =
                        getActivityColor(
                                status
                        );

                String time =
                        formatActivityTime(
                                appointment
                                        .getUpdatedAt()
                        );

                recentActivityList
                        .getChildren()
                        .add(
                                createActivity(
                                        activity,
                                        time,
                                        color
                                )
                        );
            }

        } catch (Exception e) {

            recentActivityList
                    .getChildren()
                    .add(
                            createActivity(
                                    "Unable to load recent activities",
                                    "Error",
                                    ERROR_RED
                            )
                    );
        }
    }

    // =========================================================
    // ACTIVITY TEXT
    // =========================================================

    private String getActivityText(
            String patient,
            String status
    ) {

        if (
                "COMPLETED"
                        .equalsIgnoreCase(
                                status
                        )
        ) {

            return "Appointment completed for "
                    + patient;
        }

        if (
                "CANCELLED"
                        .equalsIgnoreCase(
                                status
                        )
        ) {

            return "Appointment cancelled for "
                    + patient;
        }

        if (
                "REJECTED"
                        .equalsIgnoreCase(
                                status
                        )
        ) {

            return "Appointment rejected for "
                    + patient;
        }

        if (
                "CONFIRMED"
                        .equalsIgnoreCase(
                                status
                        )
        ) {

            return "Doctor assigned to "
                    + patient;
        }

        if (
                "ACCEPTED"
                        .equalsIgnoreCase(
                                status
                        )
        ) {

            return "Appointment accepted for "
                    + patient;
        }

        if (
                "PENDING_ASSIGNMENT"
                        .equalsIgnoreCase(
                                status
                        )
        ) {

            return "New hospital appointment request from "
                    + patient;
        }

        return "Appointment updated for "
                + patient;
    }

    // =========================================================
    // ACTIVITY COLOR
    // =========================================================

    private String getActivityColor(
            String status
    ) {

        if (status == null) {

            return PRIMARY_BLUE;
        }

        if (
                "COMPLETED"
                        .equalsIgnoreCase(
                                status
                        )
        ) {

            return SUCCESS_GREEN;
        }

        if (
                "CANCELLED"
                        .equalsIgnoreCase(
                                status
                        )
                ||
                "REJECTED"
                        .equalsIgnoreCase(
                                status
                        )
        ) {

            return ERROR_RED;
        }

        if (
                "CONFIRMED"
                        .equalsIgnoreCase(
                                status
                        )
                ||
                "ACCEPTED"
                        .equalsIgnoreCase(
                                status
                        )
        ) {

            return PURPLE;
        }

        if (
                "PENDING"
                        .equalsIgnoreCase(
                                status
                        )
                ||
                "PENDING_ASSIGNMENT"
                        .equalsIgnoreCase(
                                status
                        )
        ) {

            return WARNING_ORANGE;
        }

        return PRIMARY_BLUE;
    }

    // =========================================================
    // ACTIVITY COMPONENT
    // =========================================================

    private HBox createActivity(
            String text,
            String time,
            String color
    ) {

        HBox row =
                new HBox(10);

        row.setAlignment(
                Pos.TOP_LEFT
        );

        Circle dot =
                new Circle(
                        4,
                        Color.web(color)
                );

        VBox information =
                new VBox(2);

        Label textLabel =
                new Label(text);

        textLabel.setWrapText(
                true
        );

        textLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: 500;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Label timeLabel =
                new Label(time);

        timeLabel.setStyle(
                "-fx-font-size: 10px;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        information.getChildren().addAll(
                textLabel,
                timeLabel
        );

        row.getChildren().addAll(
                dot,
                information
        );

        return row;
    }

    // =========================================================
    // QUICK ACTIONS
    // =========================================================

    private VBox createQuickActions(
            Stage stage
    ) {

        VBox card =
                createCard();

        card.getChildren().add(
                createCardHeading(
                        "Quick Actions",
                        "Common operations"
                )
        );

        VBox buttons =
                new VBox(10);

        Button addDoctor =
                createActionButton(
                        "＋  Add Doctor",
                        PRIMARY_BLUE,
                        PRIMARY_LIGHT
                );

        Button appointments =
                createActionButton(
                        "▣  Appointments",
                        PURPLE,
                        PURPLE_LIGHT
                );

        Button beds =
                createActionButton(
                        "▥  Manage Beds",
                        SUCCESS_GREEN,
                        SUCCESS_LIGHT
                );

        Button analytics =
                createActionButton(
                        "◈  View Analytics",
                        WARNING_ORANGE,
                        WARNING_LIGHT
                );

        addDoctor.setOnAction(
                event ->
                        navigateSafely(
                                stage,
                                () ->
                                        new DoctorManagementView()
                                                .createScene(stage)
                        )
        );

        appointments.setOnAction(
                event ->
                        navigateSafely(
                                stage,
                                () ->
                                        new AppointmentManagementView()
                                                .createScene(stage)
                        )
        );

        beds.setOnAction(
                event ->
                        navigateSafely(
                                stage,
                                () ->
                                        new BedManagementView()
                                                .createScene(stage)
                        )
        );

        analytics.setOnAction(
                event ->
                        navigateSafely(
                                stage,
                                () ->
                                        new HospitalAnalyticsView()
                                                .createScene(stage)
                        )
        );

        buttons.getChildren().addAll(
                addDoctor,
                appointments,
                beds,
                analytics
        );

        card.getChildren().add(
                buttons
        );

        return card;
    }

    // =========================================================
    // ACTION BUTTON
    // =========================================================

    private Button createActionButton(
            String text,
            String color,
            String background
    ) {

        Button button =
                new Button(text);

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setPrefHeight(
                38
        );

        button.setAlignment(
                Pos.CENTER_LEFT
        );

        button.setPadding(
                new Insets(
                        0,
                        14,
                        0,
                        14
                )
        );

        String normalStyle =
                "-fx-background-color: "
                        + background
                        + ";" +
                "-fx-text-fill: "
                        + color
                        + ";" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;";

        button.setStyle(
                normalStyle
        );

        button.setOnMouseEntered(
                event ->
                        button.setStyle(
                                "-fx-background-color: "
                                        + color
                                        + ";" +
                                "-fx-text-fill: white;" +
                                "-fx-background-radius: 8;" +
                                "-fx-font-size: 11px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-cursor: hand;"
                        )
        );

        button.setOnMouseExited(
                event ->
                        button.setStyle(
                                normalStyle
                        )
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
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 700;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Label subtitleLabel =
                new Label(subtitle);

        subtitleLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
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
                "-fx-font-size: 12px;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";" +
                "-fx-cursor: hand;"
        );

        more.setOnMouseClicked(
                event ->
                        showMoreOptionsMenu(
                                more,
                                title
                        )
        );

        heading.getChildren().addAll(
                text,
                spacer,
                more
        );

        return heading;
    }

    // =========================================================
    // CARD
    // =========================================================

    private VBox createCard() {

        VBox card =
                new VBox(16);

        card.setPadding(
                new Insets(20)
        );

        applyCardStyle(
                card
        );

        return card;
    }

    private void applyCardStyle(
            VBox card
    ) {

        card.setStyle(
                "-fx-background-color: "
                        + CARD_BG
                        + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: "
                        + BORDER
                        + ";" +
                "-fx-border-radius: 12;"
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

        card.setEffect(
                shadow
        );
    }

    // =========================================================
    // REFRESH DASHBOARD
    // =========================================================

    private void refreshDashboardData() {
        if (departmentRowsContainer != null) {
            departmentRowsContainer.getChildren().clear();
            departmentRowsContainer.getChildren().add(ShimmerPlaceholder.createListShimmer(2));
        }
        if (recentActivityList != null) {
            recentActivityList.getChildren().clear();
            recentActivityList.getChildren().add(ShimmerPlaceholder.createListShimmer(2));
        }

        javafx.concurrent.Task<HospitalDashboardController.DashboardSummary> loadTask =
                new javafx.concurrent.Task<>() {
                    @Override
                    protected HospitalDashboardController.DashboardSummary call() throws Exception {
                        return dashboardController.getSummary();
                    }
                };

        loadTask.setOnSucceeded(event -> {
            HospitalDashboardController.DashboardSummary summary = loadTask.getValue();
            if (summary == null) return;

            if (totalDoctorsValue != null) {
                totalDoctorsValue.setText(String.valueOf(summary.getTotalDoctors()));
            }
            if (totalAppointmentsValue != null) {
                totalAppointmentsValue.setText(String.valueOf(summary.getTotalAppointments()));
            }
            if (availableBedsValue != null) {
                availableBedsValue.setText(String.valueOf(summary.getAvailableBeds()));
            }
            if (emergencyCasesValue != null) {
                emergencyCasesValue.setText(String.valueOf(summary.getEmergencyCases()));
            }
            if (todayApptLabel != null) {
                todayApptLabel.setText(String.valueOf(summary.getTodayAppointments()));
            }
            if (completedApptLabel != null) {
                completedApptLabel.setText(String.valueOf(summary.getCompletedAppointments()));
            }
            if (waitingApptLabel != null) {
                waitingApptLabel.setText(String.valueOf(summary.getWaitingAppointments()));
            }
            if (upcomingApptLabel != null) {
                upcomingApptLabel.setText(String.valueOf(summary.getUpcomingAppointments()));
            }
            if (cancelledApptLabel != null) {
                cancelledApptLabel.setText(String.valueOf(summary.getCancelledAppointments()));
            }

            currentOccupancyPercentage = clampPercentage(summary.getOccupancyPercentage());
            if (bedOccupancyLabel != null) {
                bedOccupancyLabel.setText(String.format("%.1f%%", currentOccupancyPercentage));
            }
            if (bedCapacityDetailsLabel != null) {
                bedCapacityDetailsLabel.setText(
                        "Total: " + summary.getTotalBeds() +
                        "  |  Occupied: " + summary.getOccupiedBeds() +
                        "  |  Available: " + summary.getAvailableBeds() +
                        "  |  Reserved: " + summary.getReservedBeds()
                );
            }
            updateBedOccupancyProgress();
            refreshDepartmentSection();
            refreshRecentActivities();
        });

        loadTask.setOnFailed(event -> {
            Throwable e = loadTask.getException();
            showAlert(
                    Alert.AlertType.ERROR,
                    "Dashboard Loading Error",
                    getRootMessage(e)
            );
        });

        new Thread(loadTask).start();
    }

    // =========================================================
    // BED OCCUPANCY PROGRESS
    //
    // The values displayed by this dashboard come from
    // HospitalDashboardController -> HospitalDashboardDAO.
    // No demo/statistical values are used here.
    // =========================================================
    // =========================================================

    private void updateBedOccupancyProgress() {

        if (
                bedProgressContainer == null
                ||
                bedOccupancyProgress == null
        ) {

            return;
        }

        double percentage =
                clampPercentage(
                        currentOccupancyPercentage
                );

        double width =
                bedProgressContainer.getWidth();

        if (
                width <= 0
        ) {

            return;
        }

        bedOccupancyProgress.setPrefWidth(
                width * percentage / 100.0
        );
    }

    private double clampPercentage(
            double percentage
    ) {

        if (
                Double.isNaN(percentage)
                ||
                Double.isInfinite(percentage)
        ) {

            return 0.0;
        }

        return Math.max(
                0.0,
                Math.min(
                        100.0,
                        percentage
                )
        );
    }

    // =========================================================
    // SEARCH
    // =========================================================

    private void handleSearch(
            String query
    ) {

        if (
                query == null
                ||
                query.trim().isEmpty()
        ) {

            return;
        }

        String search =
                query.trim()
                        .toLowerCase();

        try {

            int doctorMatches = 0;

            int appointmentMatches = 0;

            // -------------------------------------------------
            // SEARCH DOCTORS
            // -------------------------------------------------

            for (
                    com.healthsphere.model.HospitalDoctorDetails doctor :
                    dashboardController.getDoctors()
            ) {

                if (
                        doctor == null
                ) {

                    continue;
                }

                String doctorId =
                        safe(
                                doctor
                                        .getDoctorId()
                        )
                                .toLowerCase();

                String doctorName =
                        safe(
                                doctor
                                        .getFullName()
                        )
                                .toLowerCase();

                if (
                        doctorId.contains(search)
                        ||
                        doctorName.contains(search)
                ) {

                    doctorMatches++;
                }
            }

            // -------------------------------------------------
            // SEARCH APPOINTMENTS
            // -------------------------------------------------

            for (
                    Appointment appointment :
                    dashboardController.getAppointments()
            ) {

                if (
                        appointment == null
                ) {

                    continue;
                }

                String patientName =
                        safe(
                                appointment
                                        .getPatientName()
                        )
                                .toLowerCase();

                String patientUid =
                        safe(
                                appointment
                                        .getPatientUid()
                        )
                                .toLowerCase();

                if (
                        patientName.contains(search)
                        ||
                        patientUid.contains(search)
                ) {

                    appointmentMatches++;
                }
            }

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Search Results",
                    "Doctors found: "
                            + doctorMatches
                            + "\nAppointments found: "
                            + appointmentMatches
            );

        } catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Search Error",
                    getRootMessage(e)
            );
        }
    }

    // =========================================================
    // NOTIFICATIONS
    // =========================================================

    private void showNotificationsMenu(
            Label anchor
    ) {

        ContextMenu menu =
                new ContextMenu();

        try {

            int waiting =
                    dashboardController
                            .getWaitingAppointments();

            MenuItem waitingItem =
                    new MenuItem(
                            "📅 "
                                    + waiting
                                    + " appointment(s) waiting"
                    );

            MenuItem refreshItem =
                    new MenuItem(
                            "↻ Refresh Dashboard"
                    );

            refreshItem.setOnAction(
                    event ->
                            refreshDashboardData()
            );

            menu.getItems().addAll(
                    waitingItem,
                    refreshItem
            );

        } catch (Exception e) {

            menu.getItems().add(
                    new MenuItem(
                            "Unable to load notifications"
                    )
            );
        }

        menu.show(
                anchor,
                javafx.geometry.Side.BOTTOM,
                0,
                0
        );
    }

    // =========================================================
    // PROFILE MENU
    // =========================================================

    private void showProfileMenu(
            StackPane anchor,
            Stage stage
    ) {

        ContextMenu menu =
                new ContextMenu();

        MenuItem profileItem =
                new MenuItem(
                        "Profile Settings"
                );

        MenuItem logoutItem =
                new MenuItem(
                        "Log Out"
                );

        profileItem.setOnAction(
                event ->
                        stage.setScene(
                                new HospitalProfileSettingsView()
                                        .createScene(stage)
                        )
        );

        logoutItem.setOnAction(
                event ->
                        handleLogout(stage)
        );

        menu.getItems().addAll(
                profileItem,
                logoutItem
        );

        menu.show(
                anchor,
                javafx.geometry.Side.BOTTOM,
                0,
                0
        );
    }

    // =========================================================
    // MORE OPTIONS
    // =========================================================

    private void showMoreOptionsMenu(
            Label anchor,
            String sectionTitle
    ) {

        ContextMenu menu =
                new ContextMenu();

        MenuItem refreshItem =
                new MenuItem(
                        "Refresh "
                                + sectionTitle
                );

        refreshItem.setOnAction(
                event ->
                        refreshDashboardData()
        );

        menu.getItems().add(
                refreshItem
        );

        menu.show(
                anchor,
                javafx.geometry.Side.BOTTOM,
                0,
                0
        );
    }

    // =========================================================
    // HELP
    // =========================================================

    private void showHelpDialog() {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(
                "Health-Sphere Help Center"
        );

        alert.setHeaderText(
                "Hospital Administration Help"
        );

        alert.setContentText(
                "For assistance with hospital "
                        + "management, doctors, departments, "
                        + "appointments, or beds, contact "
                        + "your system administrator."
        );

        alert.showAndWait();
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    private void handleLogout(
            Stage stage
    ) {
        Navigation.logout(stage);
    }

    // =========================================================
    // SAFE NAVIGATION
    // =========================================================

    private void navigateSafely(
            Stage stage,
            SceneSupplier supplier
    ) {

        try {

            Scene scene =
                    supplier.get();

            if (
                    scene != null
            ) {

                stage.setScene(
                        scene
                );
            }

        } catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Navigation Error",
                    getRootMessage(e)
            );
        }
    }

    // =========================================================
    // FUNCTIONAL INTERFACE
    // =========================================================

    @FunctionalInterface
    private interface SceneSupplier {

        Scene get();
    }

    // =========================================================
    // ALERT
    // =========================================================

    private void showAlert(
            Alert.AlertType type,
            String title,
            String message
    ) {

        Alert alert =
                new Alert(type);

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
    // ROOT ERROR MESSAGE
    // =========================================================

    private String getRootMessage(
            Throwable throwable
    ) {

        if (
                throwable == null
        ) {

            return "Unknown error.";
        }

        Throwable current =
                throwable;

        String message =
                throwable.getMessage();

        while (
                current.getCause() != null
        ) {

            current =
                    current.getCause();

            if (
                    current.getMessage() != null
                    &&
                    !current.getMessage()
                            .trim()
                            .isEmpty()
            ) {

                message =
                        current.getMessage();
            }
        }

        if (
                message == null
                ||
                message.trim().isEmpty()
        ) {

            return "An unexpected error occurred.";
        }

        return message;
    }

    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safe(
            String value
    ) {

        return value == null
                ? ""
                : value.trim();
    }

    // =========================================================
    // FORMAT ACTIVITY TIME
    // =========================================================

    private String formatActivityTime(
            String timestamp
    ) {

        if (
                timestamp == null
                ||
                timestamp.trim().isEmpty()
        ) {

            return "Recently";
        }

        try {

            LocalDateTime dateTime =
                    LocalDateTime.parse(
                            timestamp
                    );

            return dateTime.format(
                    DateTimeFormatter.ofPattern(
                            "dd MMM, hh:mm a"
                    )
            );

        } catch (Exception e) {

            return timestamp;
        }
    }
}