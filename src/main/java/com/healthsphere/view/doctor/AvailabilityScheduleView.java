package com.healthsphere.view.doctor;

import com.healthsphere.util.Navigation;
import com.healthsphere.util.ResourceImage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * AvailabilityScheduleView represents the Schedule Management and Weekly Calendar dashboard.
 */
public class AvailabilityScheduleView {

    private final Stage stage;
    private final Scene scene;

    public AvailabilityScheduleView(Stage stage) {
        this.stage = stage;
        this.scene = createScene();
    }

    public Scene getScene() {
        return this.scene;
    }

    private Scene createScene() {
        BorderPane mainRoot = new BorderPane();
        mainRoot.getStyleClass().add("root-pane");

        // --- Sidebar (Left Navigation) ---
        VBox sidebar = createSidebar();
        mainRoot.setLeft(sidebar);

        // --- Main Content Area ---
        VBox contentArea = new VBox(20);
        contentArea.setPadding(new Insets(24, 32, 24, 32));
        contentArea.getStyleClass().add("content-area");

        // Top Header
        HBox topHeader = createTopHeader();
        contentArea.getChildren().add(topHeader);

        // Page Sub-header Title & Top Action Buttons
        BorderPane pageHeader = createPageHeader();
        contentArea.getChildren().add(pageHeader);

        // Main Content Two-Column Grid (Calendar Grid on Left | Configuration Controls on Right)
        HBox bodyLayout = createBodyLayout();
        contentArea.getChildren().add(bodyLayout);

        // ScrollPane Container
        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("content-scrollpane");
        mainRoot.setCenter(scrollPane);

        Scene availabilityScene = new Scene(mainRoot, stage.getWidth(), stage.getHeight());
        availabilityScene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/css/availability_schedule.css")).toExternalForm());

        return availabilityScene;
    }

    /** Creates Sidebar Navigation matching UI mockup sidebar design */
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPadding(new Insets(28, 16, 28, 16));
        sidebar.getStyleClass().add("sidebar");
        sidebar.setMinWidth(240);
        sidebar.setPrefWidth(240);

        // Logo Section
        HBox logoSection = new HBox(12);
        logoSection.setPadding(new Insets(0, 0, 32, 0));
        logoSection.setAlignment(Pos.CENTER_LEFT);

        StackPane logoIconBox = new StackPane();
        logoIconBox.getStyleClass().add("logo-icon-box");
        ImageView logoIcon = new ImageView(ResourceImage.load("/images/icons/ic_shield_heart.png"));
        logoIcon.setFitWidth(20);
        logoIcon.setFitHeight(20);
        logoIconBox.getChildren().add(logoIcon);

        VBox logoText = new VBox(2);
        Label appName = new Label("Health-Sphere");
        appName.getStyleClass().add("logo-name");
        Label doctorSubtext = new Label("Doctor Dashboard");
        doctorSubtext.getStyleClass().add("logo-subtext");
        logoText.getChildren().addAll(appName, doctorSubtext);
        logoSection.getChildren().addAll(logoIconBox, logoText);

        // Navigation Tabs
        VBox navItems = new VBox(6);
        String[] tabs = {
            "Dashboard", "Today's Schedule", "Appointments", "Patient Details",
            "Medical Reports & Prescription", "Availability & Schedule", "AI Health Assistant", "Doctor Profile"
        };
        String[] icons = {
            "ic_dashboard", "ic_schedule", "ic_appointments", "ic_patient",
            "ic_reports", "ic_availability", "ic_robot", "ic_profile"
        };

        for (int i = 0; i < tabs.length; i++) {
            HBox navTab = new HBox(14);
            navTab.getStyleClass().add("nav-tab");
            navTab.setAlignment(Pos.CENTER_LEFT);

            if (i == 5) { // Active Highlight: Availability & Schedule
                navTab.getStyleClass().add("nav-tab-active");
            }

            ImageView icon = new ImageView(ResourceImage.load("/images/icons/" + icons[i] + ".png"));
            icon.setFitWidth(18);
            icon.setFitHeight(18);

            Label tabLabel = new Label(tabs[i]);
            tabLabel.getStyleClass().add("nav-text");

            navTab.getChildren().addAll(icon, tabLabel);
            navItems.getChildren().add(navTab);

            final int index = i;
            navTab.setOnMouseClicked(e -> handleSidebarTabClick(index));
        }

        // Sidebar Bottom / Footer Controls
        VBox footer = new VBox(12);
        footer.setAlignment(Pos.BOTTOM_LEFT);
        VBox.setVgrow(footer, Priority.ALWAYS);

        Separator lineDivider = new Separator();
        lineDivider.getStyleClass().add("sidebar-divider");

        HBox docProfile = new HBox(12);
        docProfile.setAlignment(Pos.CENTER_LEFT);
        docProfile.getStyleClass().add("sidebar-profile");

        ImageView profileIcon = new ImageView(ResourceImage.load("/images/icons/ic_doctor_profile_small.png"));
        profileIcon.setFitWidth(20);
        profileIcon.setFitHeight(20);

        Label docLabel = new Label("Doctor Profile");
        docLabel.getStyleClass().add("sidebar-profile-name");
        docProfile.getChildren().addAll(profileIcon, docLabel);
        docProfile.setOnMouseClicked(e ->
                Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene()));

        HBox logout = new HBox(12);
        logout.setAlignment(Pos.CENTER_LEFT);
        logout.getStyleClass().add("nav-tab-logout");

        ImageView logoutIcon = new ImageView(ResourceImage.load("/images/icons/ic_logout.png"));
        logoutIcon.setFitWidth(18);
        logoutIcon.setFitHeight(18);

        Label logoutLabel = new Label("Logout");
        logoutLabel.getStyleClass().add("nav-text-logout");
        logout.getChildren().addAll(logoutIcon, logoutLabel);
        logout.setOnMouseClicked(e -> System.out.println("Logging out..."));

        footer.getChildren().addAll(lineDivider, docProfile, logout);
        sidebar.getChildren().addAll(logoSection, navItems, footer);
        return sidebar;
    }

    private void handleSidebarTabClick(int index) {
        switch (index) {
            case 0: Navigation.goTo(stage, () -> new DoctorDashboardView(stage).getScene()); break;
            case 1: Navigation.goTo(stage, () -> new TodaysScheduleView(stage).getScene()); break;
            case 2: Navigation.goTo(stage, () -> new AppointmentsView(stage).getScene()); break;
            case 3: Navigation.goTo(stage, () -> new PatientDetailsView(stage).getScene()); break;
            case 4: Navigation.goTo(stage, () -> new MedicalReportsView(stage).getScene()); break;
            case 5: Navigation.goTo(stage, () -> new AvailabilityScheduleView(stage).getScene()); break;
            case 6: Navigation.goTo(stage, () -> new AIHealthAssistantView(stage).getScene()); break;
            case 7: Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene()); break;
            default: break;
        }
    }

    /** Top Navigation & Profile Bar */
    private HBox createTopHeader() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);

        HBox breadcrumbs = new HBox(6);
        breadcrumbs.setAlignment(Pos.CENTER_LEFT);
        Label p1 = new Label("Patients");
        p1.getStyleClass().add("breadcrumb-inactive");
        Label sep = new Label("›");
        sep.getStyleClass().add("breadcrumb-separator");
        Label p2 = new Label("Availability & Schedule");
        p2.getStyleClass().add("breadcrumb-active");
        breadcrumbs.getChildren().addAll(p1, sep, p2);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Search Box
        HBox searchField = new HBox(10);
        searchField.getStyleClass().add("search-input-box");
        searchField.setAlignment(Pos.CENTER_LEFT);
        searchField.setPrefWidth(280);

        ImageView searchIcon = new ImageView(ResourceImage.load("/images/icons/ic_search.png"));
        searchIcon.setFitWidth(16);
        searchIcon.setFitHeight(16);

        TextField searchInput = new TextField();
        searchInput.setPromptText("Search appointments...");
        searchInput.getStyleClass().add("search-text-field");
        HBox.setHgrow(searchInput, Priority.ALWAYS);

        searchField.getChildren().addAll(searchIcon, searchInput);

        // Notifications & Avatar Container
        HBox rightIcons = new HBox(16);
        rightIcons.setAlignment(Pos.CENTER_RIGHT);
        rightIcons.setPadding(new Insets(0, 0, 0, 16));

        StackPane notificationBox = new StackPane();
        ImageView bellIcon = new ImageView(ResourceImage.load("/images/icons/ic_bell.png"));
        bellIcon.setFitWidth(18);
        bellIcon.setFitHeight(18);

        Circle badge = new Circle(4, Color.web("#EF4444"));
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        notificationBox.getChildren().addAll(bellIcon, badge);
        notificationBox.getStyleClass().add("clickable-icon");
        notificationBox.setOnMouseClicked(e -> System.out.println("Opening notifications..."));

        HBox userProfile = new HBox(10);
        userProfile.setAlignment(Pos.CENTER_LEFT);
        userProfile.getStyleClass().add("clickable-icon");

        ImageView userAvatar = new ImageView(ResourceImage.load("/images/doctor/portrait-3d-male-doctor.png"));
        userAvatar.setFitWidth(36);
        userAvatar.setFitHeight(36);
        Circle clip = new Circle(18, 18, 18);
        userAvatar.setClip(clip);

        VBox userDetails = new VBox(0);
        Label docName = new Label("Dr. Sarah Jenkins");
        docName.getStyleClass().add("profile-name");
        Label docDept = new Label("Cardiology");
        docDept.getStyleClass().add("profile-dept");
        userDetails.getChildren().addAll(docName, docDept);

        userProfile.getChildren().addAll(userAvatar, userDetails);
        rightIcons.getChildren().addAll(notificationBox, userProfile);

        topBar.getChildren().addAll(breadcrumbs, spacer, searchField, rightIcons);
        return topBar;
    }

    /** Page Title and Top Actions Header */
    private BorderPane createPageHeader() {
        BorderPane header = new BorderPane();
        header.setPadding(new Insets(4, 0, 8, 0));

        VBox titles = new VBox(4);
        Label title = new Label("Schedule Management");
        title.getStyleClass().add("page-title");
        Label subtext = new Label("Configure your working hours, breaks, and view your weekly calendar.");
        subtext.getStyleClass().add("page-subtext");
        titles.getChildren().addAll(title, subtext);

        HBox actionBtns = new HBox(12);
        actionBtns.setAlignment(Pos.CENTER_RIGHT);

        Button exportBtn = new Button("Export");
        ImageView exportIcon = new ImageView(ResourceImage.load("/images/icons/ic_export.png"));
        exportIcon.setFitWidth(14);
        exportIcon.setFitHeight(14);
        exportBtn.setGraphic(exportIcon);
        exportBtn.getStyleClass().add("btn-secondary-action");
        exportBtn.setOnAction(e -> System.out.println("Exporting schedule..."));

        Button saveBtn = new Button("Save Changes");
        ImageView saveIcon = new ImageView(ResourceImage.load("/images/icons/ic_save.png"));
        saveIcon.setFitWidth(14);
        saveIcon.setFitHeight(14);
        saveBtn.setGraphic(saveIcon);
        saveBtn.getStyleClass().add("btn-primary-action");
        saveBtn.setOnAction(e -> System.out.println("Saving schedule changes..."));

        actionBtns.getChildren().addAll(exportBtn, saveBtn);

        header.setLeft(titles);
        header.setRight(actionBtns);
        return header;
    }

    /** Body Layout: Calendar View on Left & Settings/Assistant Panel on Right */
    private HBox createBodyLayout() {
        HBox layout = new HBox(20);

        // Left Column: Interactive Schedule Calendar
        VBox scheduleCard = createScheduleCalendarCard();
        HBox.setHgrow(scheduleCard, Priority.ALWAYS);

        // Right Column: Controls Panel
        VBox controlsPanel = new VBox(20);
        controlsPanel.setMinWidth(330);
        controlsPanel.setMaxWidth(360);

        VBox aiAssistantCard = createAIAssistantCard();
        VBox workingHoursCard = createWorkingHoursCard();
        VBox emergencyCard = createEmergencyCard();

        controlsPanel.getChildren().addAll(aiAssistantCard, workingHoursCard, emergencyCard);

        layout.getChildren().addAll(scheduleCard, controlsPanel);
        return layout;
    }

    /** Weekly Schedule Interactive View Pane */
    private VBox createScheduleCalendarCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("panel-card");
        card.setPadding(new Insets(20));

        // Top Controls: Title, Week/Month Switcher
        BorderPane calHeader = new BorderPane();

        Label calTitle = new Label("Weekly Schedule View");
        calTitle.getStyleClass().add("card-title");

        HBox toggleGroup = new HBox(0);
        toggleGroup.getStyleClass().add("segmented-button-bar");
        Button weekBtn = new Button("Week");
        weekBtn.getStyleClass().addAll("segmented-btn", "segmented-btn-active");
        Button monthBtn = new Button("Month");
        monthBtn.getStyleClass().add("segmented-btn");
        toggleGroup.getChildren().addAll(weekBtn, monthBtn);

        calHeader.setLeft(calTitle);
        calHeader.setRight(toggleGroup);

        // Navigation Subheader & Color Legends
        BorderPane navLegendRow = new BorderPane();
        navLegendRow.setPadding(new Insets(4, 0, 4, 0));

        HBox dateNav = new HBox(12);
        dateNav.setAlignment(Pos.CENTER_LEFT);
        Label prevArrow = new Label("‹");
        prevArrow.getStyleClass().add("nav-arrow-btn");
        Label dateRange = new Label("October 2023, Week 4");
        dateRange.getStyleClass().add("nav-date-label");
        Label nextArrow = new Label("›");
        nextArrow.getStyleClass().add("nav-arrow-btn");
        dateNav.getChildren().addAll(prevArrow, dateRange, nextArrow);

        HBox legends = new HBox(16);
        legends.setAlignment(Pos.CENTER_RIGHT);
        legends.getChildren().addAll(
                createLegendItem("Clinical", "#2563EB"),
                createLegendItem("Surgery", "#0891B2"),
                createLegendItem("Unavailable", "#94A3B8")
        );

        navLegendRow.setLeft(dateNav);
        navLegendRow.setRight(legends);

        // Schedule Grid Table
        GridPane scheduleGrid = createScheduleGrid();

        card.getChildren().addAll(calHeader, navLegendRow, scheduleGrid);
        return card;
    }

    private HBox createLegendItem(String label, String hexColor) {
        HBox box = new HBox(6);
        box.setAlignment(Pos.CENTER_LEFT);
        Circle dot = new Circle(4, Color.web(hexColor));
        Label l = new Label(label);
        l.getStyleClass().add("legend-text");
        box.getChildren().addAll(dot, l);
        return box;
    }

    /** Calendar Grid Builder matching mockup day/time headers and blocks */
    private GridPane createScheduleGrid() {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("calendar-grid");

        String[] headers = {"", "Mon 23", "Tue 24", "Wed 25", "Thu 26", "Fri 27"};
        for (int col = 0; col < headers.length; col++) {
            Label headerLabel = new Label(headers[col]);
            headerLabel.getStyleClass().add("calendar-header-cell");

            // Highlight current day column header (Thu 26)
            if (col == 4) {
                headerLabel.getStyleClass().add("calendar-header-active");
            }

            headerLabel.setPrefWidth(col == 0 ? 65 : 120);
            headerLabel.setAlignment(Pos.CENTER);
            grid.add(headerLabel, col, 0);
        }

        String[] timeSlots = {"8 AM", "9 AM", "10 AM", "11 AM", "12 PM", "1 PM"};
        for (int row = 0; row < timeSlots.length; row++) {
            Label timeLabel = new Label(timeSlots[row]);
            timeLabel.getStyleClass().add("calendar-time-cell");
            timeLabel.setAlignment(Pos.CENTER_RIGHT);
            timeLabel.setPadding(new Insets(0, 10, 0, 0));
            grid.add(timeLabel, 0, row + 1);

            for (int col = 1; col <= 5; col++) {
                Pane emptyCell = new Pane();
                emptyCell.getStyleClass().add("calendar-slot-cell");
                emptyCell.setPrefHeight(60);
                grid.add(emptyCell, col, row + 1);
            }
        }

        // Column Constraints for proper calendar column stretching
        ColumnConstraints col0 = new ColumnConstraints();
        col0.setMinWidth(65);
        grid.getColumnConstraints().add(col0);

        for (int i = 1; i <= 5; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(20);
            grid.getColumnConstraints().add(col);
        }

        // --- Appointment / Schedule Blocks Overlay ---

        // Monday: Consultations (9:00 AM - 11:00 AM)
        VBox consultBlock = createCalendarBlock("Consultations\n9:00 - 11:00", "block-clinical");
        grid.add(consultBlock, 1, 2, 1, 2);

        // Monday: Lunch Break (11:30 AM - 12:00 PM)
        VBox lunchBlock = createCalendarBlock("Lunch Break", "block-lunch");
        grid.add(lunchBlock, 1, 4, 1, 1);

        // Tuesday: Surgery Block (8:30 AM - 11:30 AM)
        VBox surgeryBlock = createCalendarBlock("Surgery Block\n8:30 - 11:30", "block-surgery");
        grid.add(surgeryBlock, 2, 1, 1, 3);

        // Thursday: Follow-ups (10:00 AM - 11:00 AM)
        VBox followUpBlock = createCalendarBlock("Follow-ups\n10:00 - 11:00", "block-clinical");
        grid.add(followUpBlock, 4, 3, 1, 1);

        return grid;
    }

    private VBox createCalendarBlock(String text, String styleClass) {
        VBox block = new VBox();
        block.getStyleClass().addAll("calendar-block", styleClass);
        block.setPadding(new Insets(8));

        Label lbl = new Label(text);
        lbl.getStyleClass().add("calendar-block-text");
        lbl.setWrapText(true);

        block.getChildren().add(lbl);
        return block;
    }

    /** AI Scheduling Assistant Panel */
    private VBox createAIAssistantCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("ai-assistant-card");
        card.setPadding(new Insets(18));

        HBox titleBox = new HBox(8);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        ImageView aiIcon = new ImageView(ResourceImage.load("/images/icons/ic_robot.png"));
        aiIcon.setFitWidth(18);
        aiIcon.setFitHeight(18);

        Label titleLbl = new Label("AI Scheduling Assistant");
        titleLbl.getStyleClass().add("ai-card-title");
        titleBox.getChildren().addAll(aiIcon, titleLbl);

        Label suggestion = new Label("Your patient load is typically highest on Tuesday mornings. Consider moving administrative tasks to Wednesday afternoons to optimize patient flow.");
        suggestion.setWrapText(true);
        suggestion.getStyleClass().add("ai-card-desc");

        Hyperlink optLink = new Hyperlink("View Optimization Suggestions →");
        optLink.getStyleClass().add("ai-card-link");
        optLink.setOnAction(e -> System.out.println("Opening AI Suggestions..."));

        card.getChildren().addAll(titleBox, suggestion, optLink);
        return card;
    }

    /** Working Hours Config Panel */
    private VBox createWorkingHoursCard() {
        VBox card = new VBox(14);
        card.getStyleClass().add("panel-card");
        card.setPadding(new Insets(18));

        BorderPane header = new BorderPane();
        Label title = new Label("Working Hours");
        title.getStyleClass().add("card-title");

        ImageView gearIcon = new ImageView(ResourceImage.load("/images/icons/ic_settings.png"));
        gearIcon.setFitWidth(16);
        gearIcon.setFitHeight(16);
        gearIcon.getStyleClass().add("clickable-icon");

        header.setLeft(title);
        header.setRight(gearIcon);

        VBox daysList = new VBox(10);

        daysList.getChildren().add(createWorkingDayRow("Mon", true, "09:00 AM", "05:00 PM"));
        daysList.getChildren().add(createWorkingDayRow("Tue", true, "08:30 AM", "04:30 PM"));
        daysList.getChildren().add(createWorkingDayRow("Wed", false, "", ""));
        daysList.getChildren().add(createWorkingDayRow("Thu", true, "09:00 AM", "05:00 PM"));
        daysList.getChildren().add(createWorkingDayRow("Fri", true, "09:00 AM", "02:00 PM"));

        VBox slotBox = new VBox(6);
        slotBox.setPadding(new Insets(10, 0, 0, 0));
        Label slotLabel = new Label("Default Appointment Slot");
        slotLabel.getStyleClass().add("input-label");

        ComboBox<String> slotCombo = new ComboBox<>();
        slotCombo.getItems().addAll("15 Minutes", "30 Minutes", "45 Minutes", "60 Minutes");
        slotCombo.setValue("30 Minutes");
        slotCombo.setMaxWidth(Double.MAX_VALUE);
        slotCombo.getStyleClass().add("input-select");

        slotBox.getChildren().addAll(slotLabel, slotCombo);

        card.getChildren().addAll(header, daysList, slotBox);
        return card;
    }

    private HBox createWorkingDayRow(String day, boolean isChecked, String startTime, String endTime) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);

        CheckBox cb = new CheckBox(day);
        cb.setSelected(isChecked);
        cb.getStyleClass().add("day-checkbox");
        cb.setPrefWidth(60);

        if (isChecked) {
            ComboBox<String> startCombo = new ComboBox<>();
            startCombo.setValue(startTime);
            startCombo.getStyleClass().add("time-select");

            Label sep = new Label("-");
            sep.getStyleClass().add("time-separator");

            ComboBox<String> endCombo = new ComboBox<>();
            endCombo.setValue(endTime);
            endCombo.getStyleClass().add("time-select");

            row.getChildren().addAll(cb, startCombo, sep, endCombo);
        } else {
            Label offDuty = new Label("Off Duty");
            offDuty.getStyleClass().add("off-duty-label");
            row.getChildren().addAll(cb, offDuty);
        }

        return row;
    }

    /** Emergency Availability Switch Box */
    private VBox createEmergencyCard() {
        VBox card = new VBox(8);
        card.getStyleClass().add("panel-card");
        card.setPadding(new Insets(16));

        BorderPane header = new BorderPane();
        HBox left = new HBox(6);
        left.setAlignment(Pos.CENTER_LEFT);

        Label aster = new Label("✻");
        aster.getStyleClass().add("emergency-asterisk");
        Label title = new Label("Emergency Availability");
        title.getStyleClass().add("card-title");
        left.getChildren().addAll(aster, title);

        ToggleButton toggle = new ToggleButton();
        toggle.setSelected(true);
        toggle.getStyleClass().add("switch-toggle");

        header.setLeft(left);
        header.setRight(toggle);

        Label desc = new Label("Accept urgent cases outside regular slots.");
        desc.getStyleClass().add("emergency-desc");

        card.getChildren().addAll(header, desc);
        return card;
    }
}