package com.healthsphere.view.doctor;

import com.healthsphere.util.Navigation;
import com.healthsphere.util.ResourceImage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * TodaysScheduleView represents the daily agenda screen for Doctors in Health-Sphere.
 * Fully interactive layout with responsive dynamic timeline line rendering, interactive mini-calendar,
 * date switcher navigation, and appointment rescheduling dialog.
 */
public class TodaysScheduleView {

    private final Stage stage;
    private final Scene scene;

    // Dynamic State for Date Navigation
    private LocalDate currentDate = LocalDate.of(2023, 10, 25);
    private LocalDate selectedCalendarDate = LocalDate.of(2023, 10, 25);
    private Label dateTitleLabel;
    private Label monthLabel;
    private GridPane calendarGrid;

    public TodaysScheduleView(Stage stage) {
        this.stage = stage;
        this.scene = createScene();
    }

    public Scene getScene() {
        return this.scene;
    }

    private Scene createScene() {
        BorderPane mainRoot = new BorderPane();
        mainRoot.getStyleClass().add("root-pane");

        // --- Sidebar (LEFT - Static navigation matching Dashboard) ---
        VBox sidebar = createSidebar();
        mainRoot.setLeft(sidebar);

        // --- Main Center Layout ---
        VBox centerLayout = new VBox(20);
        centerLayout.setPadding(new Insets(20, 25, 20, 25));
        centerLayout.getStyleClass().add("content-area");

        // Top Navigation Header
        BorderPane topHeader = createTopHeader();
        centerLayout.getChildren().add(topHeader);

        // Main 2-Column Split
        HBox mainGrid = new HBox(20);

        VBox leftColumn = createScheduleTimelineColumn();
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        VBox rightColumn = createSideCardsColumn();
        rightColumn.setMinWidth(320);
        rightColumn.setMaxWidth(340);

        mainGrid.getChildren().addAll(leftColumn, rightColumn);
        centerLayout.getChildren().add(mainGrid);

        // ScrollPane wrapping ONLY centerLayout so sidebar stays completely static
        ScrollPane contentScrollPane = new ScrollPane(centerLayout);
        contentScrollPane.setFitToWidth(true);
        contentScrollPane.setFitToHeight(true);
        contentScrollPane.getStyleClass().add("content-scrollpane");

        mainRoot.setCenter(contentScrollPane);

        Scene scheduleScene = new Scene(mainRoot, stage.getWidth(), stage.getHeight());

        try {
            scheduleScene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/css/todays_schedule.css")).toExternalForm());
        } catch (Exception ignored) {}

        return scheduleScene;
    }

    /** 
     * Creates Left Sidebar styled strictly like DoctorDashboardView with 
     * dark navy background, blue active highlight pill, and proper doctor profile footer card.
     */
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPadding(new Insets(25, 15, 25, 15));
        sidebar.getStyleClass().add("sidebar");
        sidebar.setStyle("-fx-background-color: #0F172A;"); // Dark Navy matching Dashboard
        sidebar.setMinWidth(260);
        sidebar.setPrefWidth(260);
        sidebar.setMaxWidth(260);

        // Logo Section
        HBox logoSection = new HBox(12);
        logoSection.setPadding(new Insets(0, 0, 25, 5));
        logoSection.setAlignment(Pos.CENTER_LEFT);

        StackPane logoIconBox = new StackPane();
        logoIconBox.getStyleClass().add("logo-icon-box");
        logoIconBox.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 8px; -fx-padding: 8px;");
        ImageView logoIcon = new ImageView(ResourceImage.load("/images/icons/ic_shield.png"));
        logoIcon.setFitWidth(20);
        logoIcon.setFitHeight(20);
        logoIconBox.getChildren().add(logoIcon);

        VBox logoText = new VBox(2);
        Label appName = new Label("Health-Sphere");
        appName.getStyleClass().add("logo-name");
        appName.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 16px;");

        Label doctorSubtext = new Label("Doctor Dashboard");
        doctorSubtext.getStyleClass().add("logo-subtext");
        doctorSubtext.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12px;");

        logoText.getChildren().addAll(appName, doctorSubtext);
        logoSection.getChildren().addAll(logoIconBox, logoText);

        // Navigation Items
        VBox navItems = new VBox(6);
        String[] tabs = {
            "Dashboard", 
            "Today's Schedule", 
            "Appointments", 
            "Patient Details", 
            "Medical Reports & Prescription", 
            "Availability & Schedule", 
            "Doctor Profile", 
            "AI Health Assistant"
        };
        String[] icons = {
            "ic_dashboard", 
            "ic_schedule", 
            "ic_appointments", 
            "ic_patient", 
            "ic_reports", 
            "ic_availability", 
            "ic_profile", 
            "ic_ai"
        };

        for (int i = 0; i < tabs.length; i++) {
            final int tabIndex = i;
            HBox navTab = new HBox(12);
            navTab.setAlignment(Pos.CENTER_LEFT);
            navTab.setPadding(new Insets(10, 14, 10, 14));
            navTab.getStyleClass().add("nav-tab");

            ImageView icon = new ImageView(ResourceImage.load("/images/icons/" + icons[i] + ".png"));
            icon.setFitWidth(18);
            icon.setFitHeight(18);

            Label tabLabel = new Label(tabs[i]);
            tabLabel.getStyleClass().add("nav-text");

            if (i == 1) { // Highlight Today's Schedule (Active)
                navTab.getStyleClass().add("nav-tab-active");
                navTab.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 8px;");
                tabLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 14px;");
            } else {
                navTab.setStyle("-fx-background-color: transparent; -fx-background-radius: 8px;");
                tabLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 14px;");
            }

            navTab.getChildren().addAll(icon, tabLabel);

            // Handle Navigation Click
            navTab.setOnMouseClicked(event -> handleSidebarTabClick(tabIndex));
            navItems.getChildren().add(navTab);
        }

        // Spacer to push footer to bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Footer Section (Doctor Profile Card & Logout Button)
        VBox footer = new VBox(10);
        footer.setPadding(new Insets(15, 0, 0, 0));

        // Bottom Doctor Profile Box
        HBox sidebarProfile = new HBox(12);
        sidebarProfile.setAlignment(Pos.CENTER_LEFT);
        sidebarProfile.setPadding(new Insets(10, 12, 10, 12));
        sidebarProfile.getStyleClass().add("sidebar-profile-box");
        sidebarProfile.setStyle("-fx-background-color: #1E293B; -fx-background-radius: 10px; -fx-cursor: hand;");

        ImageView profileAvatar = new ImageView(ResourceImage.load("/images/doctor/portrait-3d-male-doctor.png"));
        profileAvatar.setFitWidth(36);
        profileAvatar.setFitHeight(36);
        Circle profileClip = new Circle(18, 18, 18);
        profileAvatar.setClip(profileClip);

        VBox profileTexts = new VBox(2);
        Label profSubText = new Label("Doctor Profile");
        profSubText.setStyle("-fx-text-fill: #64748B; -fx-font-size: 11px;");
        Label profName = new Label("Dr. Sarah");
        profName.getStyleClass().add("sidebar-profile-name");
        profName.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 13px;");

        profileTexts.getChildren().addAll(profSubText, profName);
        sidebarProfile.getChildren().addAll(profileAvatar, profileTexts);
        sidebarProfile.setOnMouseClicked(event -> handleSidebarTabClick(6)); // Doctor Profile

        // Logout Tab
        HBox logoutTab = new HBox(12);
        logoutTab.setAlignment(Pos.CENTER_LEFT);
        logoutTab.setPadding(new Insets(10, 14, 10, 14));
        logoutTab.getStyleClass().add("nav-tab");
        logoutTab.setStyle("-fx-cursor: hand;");

        ImageView logoutIcon = new ImageView(ResourceImage.load("/images/icons/ic_logout.png"));
        logoutIcon.setFitWidth(18);
        logoutIcon.setFitHeight(18);

        Label logoutLabel = new Label("Logout");
        logoutLabel.getStyleClass().add("nav-text");
        logoutLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 14px;");

        logoutTab.getChildren().addAll(logoutIcon, logoutLabel);
        logoutTab.setOnMouseClicked(event -> showInformationAlert("Logout", "Logged out successfully."));

        footer.getChildren().addAll(sidebarProfile, logoutTab);
        sidebar.getChildren().addAll(logoSection, navItems, spacer, footer);
        return sidebar;
    }

    /** Router handler for Sidebar Tab Clicks */
    private void handleSidebarTabClick(int index) {
        switch (index) {
            case 0:
                Navigation.goTo(stage, () -> new DoctorDashboardView(stage).getScene());
                break;
            case 1:
                Navigation.goTo(stage, () -> new TodaysScheduleView(stage).getScene());
                break;
            case 2:
                Navigation.goTo(stage, () -> new AppointmentsView(stage).getScene());
                break;
            case 3:
                Navigation.goTo(stage, () -> new PatientDetailsView(stage).getScene());
                break;
            case 4:
                Navigation.goTo(stage, () -> new MedicalReportsView(stage).getScene());
                break;
            case 5:
                Navigation.goTo(stage, () -> new AvailabilityScheduleView(stage).getScene());
                break;
            case 6:
                Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene());
                break;
            case 7:
                Navigation.goTo(stage, () -> new AIHealthAssistantView(stage).getScene());
                break;
            default:
                break;
        }
    }

    /** Top Bar Breadcrumbs, Notifications, and Search */
    private BorderPane createTopHeader() {
        BorderPane header = new BorderPane();

        HBox breadcrumbBox = new HBox(8);
        breadcrumbBox.setAlignment(Pos.CENTER_LEFT);
        Label parentLabel = new Label("Health-Sphere");
        parentLabel.getStyleClass().add("breadcrumb-parent");
        parentLabel.setOnMouseClicked(e -> Navigation.goTo(stage, () -> new DoctorDashboardView(stage).getScene()));

        Label separator = new Label(">");
        separator.getStyleClass().add("breadcrumb-separator");
        Label currentLabel = new Label("Today's Schedule");
        currentLabel.getStyleClass().add("breadcrumb-current");
        breadcrumbBox.getChildren().addAll(parentLabel, separator, currentLabel);

        HBox rightControls = new HBox(18);
        rightControls.setAlignment(Pos.CENTER_RIGHT);

        ImageView searchBtn = createImageView("/images/icons/ic_search.png", 18, 18);
        if (searchBtn != null) {
            searchBtn.getStyleClass().add("clickable-icon");
            searchBtn.setOnMouseClicked(e -> showSearchDialog());
        }

        StackPane notificationBox = new StackPane();
        ImageView bellIcon = createImageView("/images/icons/ic_bell.png", 18, 18);
        Circle badge = new Circle(4, Color.RED);
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        if (bellIcon != null) {
            notificationBox.getChildren().add(bellIcon);
        }
        notificationBox.getChildren().add(badge);
        notificationBox.getStyleClass().add("clickable-icon");
        notificationBox.setOnMouseClicked(e -> showInformationAlert("Notifications", "You have 1 critical alert and 3 pending appointment requests."));

        ImageView userAvatar = createImageView("/images/mocks/dr_sarah_avatar.png", 32, 32);
        if (userAvatar != null) {
            Circle clip = new Circle(16, 16, 16);
            userAvatar.setClip(clip);
            userAvatar.getStyleClass().add("clickable-icon");
            userAvatar.setOnMouseClicked(e -> Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene()));
        }

        if (searchBtn != null) rightControls.getChildren().add(searchBtn);
        rightControls.getChildren().add(notificationBox);
        if (userAvatar != null) rightControls.getChildren().add(userAvatar);

        header.setLeft(breadcrumbBox);
        header.setRight(rightControls);
        return header;
    }

    /** Creates Left Column: Date Bar, Critical Banner, and Schedule Timeline */
    private VBox createScheduleTimelineColumn() {
        VBox container = new VBox(15);

        // Date Controls Bar
        BorderPane dateHeader = new BorderPane();
        VBox dateTextGroup = new VBox(2);

        dateTitleLabel = new Label(formatDateTitle(currentDate));
        dateTitleLabel.getStyleClass().add("date-title");

        Label apptSubtitle = new Label("5 Appointments remaining today");
        apptSubtitle.getStyleClass().add("date-subtitle");
        dateTextGroup.getChildren().addAll(dateTitleLabel, apptSubtitle);

        HBox navButtons = new HBox(8);
        Button prevBtn = new Button("<");
        prevBtn.getStyleClass().add("btn-date-nav");
        prevBtn.setOnAction(e -> updateCurrentDate(currentDate.minusDays(1)));

        Button todayBtn = new Button("Today");
        todayBtn.setMinWidth(70);
        todayBtn.getStyleClass().add("btn-date-today");
        todayBtn.setOnAction(e -> updateCurrentDate(LocalDate.of(2023, 10, 25)));

        Button nextBtn = new Button(">");
        nextBtn.getStyleClass().add("btn-date-nav");
        nextBtn.setOnAction(e -> updateCurrentDate(currentDate.plusDays(1)));

        navButtons.getChildren().addAll(prevBtn, todayBtn, nextBtn);

        dateHeader.setLeft(dateTextGroup);
        dateHeader.setRight(navButtons);

        // Critical Alert Banner
        HBox criticalAlert = new HBox(15);
        criticalAlert.getStyleClass().add("critical-alert-box");
        criticalAlert.setAlignment(Pos.CENTER_LEFT);

        StackPane alertIconContainer = new StackPane();
        ImageView alertIcon = createImageView("/images/icons/ic_alert_red.png", 20, 20);
        if (alertIcon != null) {
            alertIconContainer.getChildren().add(alertIcon);
        }

        VBox alertContent = new VBox(3);
        Label alertTitle = new Label("Critical Alert");
        alertTitle.getStyleClass().add("alert-title");
        Label alertDesc = new Label("Patient Thomas Wright (2:00 PM) reported severe chest pain in pre-screening.\nPrepare crash cart in Room 3.");
        alertDesc.getStyleClass().add("alert-desc");
        alertContent.getChildren().addAll(alertTitle, alertDesc);

        criticalAlert.getChildren().addAll(alertIconContainer, alertContent);
        criticalAlert.setOnMouseClicked(e -> Navigation.goTo(stage, () -> new PatientDetailsView(stage).getScene()));

        // Timeline Container Card
        VBox timelineCard = new VBox(15);
        timelineCard.getStyleClass().add("timeline-card");
        timelineCard.setPadding(new Insets(20));

        // Interactive Timeline Slots
        timelineCard.getChildren().add(createTimelineSlot("09:00 AM", "Sarah Jenkins", "General Checkup", "Completed", false, false));
        timelineCard.getChildren().add(createTimelineSlot("10:00 AM", "Michael Chang", "Follow-up", "Completed", false, false));

        // Current Time Indicator (11:15 AM)
        HBox timeIndicatorRow = new HBox(10);
        timeIndicatorRow.setAlignment(Pos.CENTER_LEFT);
        Label timeIndicatorLabel = new Label("11:15");
        timeIndicatorLabel.getStyleClass().add("time-indicator-label");

        Circle blueDot = new Circle(4, Color.web("#0052CC"));
        Line indicatorLine = new Line();
        indicatorLine.setStartX(0);
        indicatorLine.setStroke(Color.web("#0052CC"));
        indicatorLine.setStrokeWidth(1.5);

        HBox lineBox = new HBox(blueDot, indicatorLine);
        lineBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(lineBox, Priority.ALWAYS);

        lineBox.widthProperty().addListener((obs, oldVal, newVal) -> {
            double lineLength = newVal.doubleValue() - blueDot.getRadius() * 2 - 10;
            if (lineLength > 0) {
                indicatorLine.setEndX(lineLength);
            }
        });

        timeIndicatorRow.getChildren().addAll(timeIndicatorLabel, lineBox);
        timelineCard.getChildren().add(timeIndicatorRow);

        // Active In-Room Slot
        timelineCard.getChildren().add(createTimelineSlot("11:00 AM", "Elena Rodriguez", "Cardiology Consult", "In Room", true, false));

        // Lunch Break Slot
        timelineCard.getChildren().add(createTimelineSlot("12:00 PM", "", "Lunch Break", "", false, true));

        // Waiting Future Slot
        timelineCard.getChildren().add(createTimelineSlot("01:00 PM", "David Kim", "Annual Physical", "Waiting", false, false));

        container.getChildren().addAll(dateHeader, criticalAlert, timelineCard);
        return container;
    }

    /** Creates individual timeline appointment slot with click listener */
    private HBox createTimelineSlot(String time, String patientName, String detail, String status, boolean isActive, boolean isBreak) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);

        Label timeLabel = new Label(time);
        timeLabel.getStyleClass().add("slot-time-label");
        timeLabel.setMinWidth(70);

        if (isBreak) {
            HBox breakCard = new HBox();
            breakCard.getStyleClass().add("slot-break-card");
            breakCard.setAlignment(Pos.CENTER);
            HBox.setHgrow(breakCard, Priority.ALWAYS);
            Label breakLabel = new Label(detail);
            breakLabel.getStyleClass().add("slot-break-text");
            breakCard.getChildren().add(breakLabel);

            row.getChildren().addAll(timeLabel, breakCard);
            return row;
        }

        HBox card = new HBox();
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setPadding(new Insets(12, 15, 12, 15));

        if (isActive) {
            card.getStyleClass().add("slot-card-active");

            VBox info = new VBox(4);
            Label nameLbl = new Label(patientName);
            nameLbl.getStyleClass().add("slot-card-active-title");
            Label detailLbl = new Label(detail);
            detailLbl.getStyleClass().add("slot-card-active-sub");

            HBox metaBox = new HBox(15);
            metaBox.setPadding(new Insets(5, 0, 0, 0));

            Label timeMeta = new Label("🕒 11:00 - 11:45");
            timeMeta.getStyleClass().add("slot-card-active-meta");
            Label roomMeta = new Label("🏥 Room 2");
            roomMeta.getStyleClass().add("slot-card-active-meta");
            metaBox.getChildren().addAll(timeMeta, roomMeta);

            info.getChildren().addAll(nameLbl, detailLbl, metaBox);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label statusPill = new Label(status);
            statusPill.getStyleClass().add("pill-in-room");

            card.getChildren().addAll(info, spacer, statusPill);
        } else {
            card.getStyleClass().add("slot-card-standard");

            VBox info = new VBox(2);
            Label nameLbl = new Label(patientName);
            nameLbl.getStyleClass().add(status.equals("Completed") ? "slot-card-completed-title" : "slot-card-title");
            Label detailLbl = new Label(detail);
            detailLbl.getStyleClass().add("slot-card-sub");
            info.getChildren().addAll(nameLbl, detailLbl);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label statusPill = new Label(status);
            statusPill.getStyleClass().add(status.equals("Completed") ? "pill-completed" : "pill-waiting");

            card.getChildren().addAll(info, spacer, statusPill);
        }

        card.setOnMouseClicked(e -> Navigation.goTo(stage, () -> new PatientDetailsView(stage).getScene()));

        row.getChildren().addAll(timeLabel, card);
        return row;
    }

    /** Right Column Side Panels */
    private VBox createSideCardsColumn() {
        VBox sideColumn = new VBox(15);

        VBox calendarCard = createMiniCalendarCard();
        VBox currentPatientCard = createCurrentPatientCard();
        VBox queueCard = createQueueCard();

        sideColumn.getChildren().addAll(calendarCard, currentPatientCard, queueCard);
        return sideColumn;
    }

    /** Interactive Mini Calendar Card */
    private VBox createMiniCalendarCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("side-card");
        card.setPadding(new Insets(15));

        BorderPane header = new BorderPane();
        monthLabel = new Label(formatMonthTitle(selectedCalendarDate));
        monthLabel.getStyleClass().add("calendar-month-title");

        HBox nav = new HBox(8);
        Label prev = new Label("<");
        prev.getStyleClass().add("calendar-nav-arrow");
        prev.setOnMouseClicked(e -> {
            selectedCalendarDate = selectedCalendarDate.minusMonths(1);
            refreshCalendarDisplay();
        });

        Label next = new Label(">");
        next.getStyleClass().add("calendar-nav-arrow");
        next.setOnMouseClicked(e -> {
            selectedCalendarDate = selectedCalendarDate.plusMonths(1);
            refreshCalendarDisplay();
        });
        nav.getChildren().addAll(prev, next);

        header.setLeft(monthLabel);
        header.setRight(nav);

        calendarGrid = new GridPane();
        calendarGrid.setHgap(8);
        calendarGrid.setVgap(8);
        calendarGrid.setAlignment(Pos.CENTER);

        refreshCalendarDisplay();

        card.getChildren().addAll(header, calendarGrid);
        return card;
    }

    /** Re-renders mini-calendar dates and attaches click events */
    private void refreshCalendarDisplay() {
        calendarGrid.getChildren().clear();
        monthLabel.setText(formatMonthTitle(selectedCalendarDate));

        String[] days = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
        for (int i = 0; i < days.length; i++) {
            Label dayLbl = new Label(days[i]);
            dayLbl.getStyleClass().add("calendar-day-header");
            calendarGrid.add(dayLbl, i, 0);
        }

        String[] dates = {"22", "23", "24", "25", "26", "27", "28"};
        for (int i = 0; i < dates.length; i++) {
            StackPane cell = new StackPane();
            cell.getStyleClass().add("calendar-date-cell");
            cell.setPrefSize(28, 28);
            Label dateLbl = new Label(dates[i]);

            int dateVal = Integer.parseInt(dates[i]);
            if (dateVal == currentDate.getDayOfMonth()) {
                Circle circle = new Circle(12, Color.web("#0052CC"));
                dateLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                cell.getChildren().addAll(circle, dateLbl);
            } else {
                dateLbl.getStyleClass().add("calendar-date-number");
                cell.getChildren().add(dateLbl);
            }

            final int dayNum = dateVal;
            cell.setOnMouseClicked(e -> {
                LocalDate clickedDate = LocalDate.of(selectedCalendarDate.getYear(), selectedCalendarDate.getMonth(), dayNum);
                updateCurrentDate(clickedDate);
            });

            calendarGrid.add(cell, i, 1);
        }
    }

    /** Interactive Current Patient Live Card */
    private VBox createCurrentPatientCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("side-card");
        card.setPadding(new Insets(15));

        BorderPane header = new BorderPane();
        Label title = new Label("CURRENT PATIENT");
        title.getStyleClass().add("side-card-subtitle");

        HBox liveIndicator = new HBox(4);
        liveIndicator.setAlignment(Pos.CENTER);
        Circle liveDot = new Circle(3, Color.web("#0052CC"));
        Label liveText = new Label("Live");
        liveText.getStyleClass().add("live-text");
        liveIndicator.getChildren().addAll(liveDot, liveText);

        header.setLeft(title);
        header.setRight(liveIndicator);

        HBox patientProfile = new HBox(12);
        patientProfile.setAlignment(Pos.CENTER_LEFT);

        ImageView avatar = createImageView("/images/mocks/elena_rodriguez.png", 50, 50);
        if (avatar != null) {
            Circle clip = new Circle(25, 25, 25);
            avatar.setClip(clip);
        }

        VBox details = new VBox(2);
        Label name = new Label("Elena Rodriguez");
        name.getStyleClass().add("patient-card-name");
        Label meta = new Label("Female, 42 yrs");
        meta.getStyleClass().add("patient-card-meta");
        details.getChildren().addAll(name, meta);

        if (avatar != null) {
            patientProfile.getChildren().add(avatar);
        }
        patientProfile.getChildren().add(details);

        GridPane vitalsGrid = new GridPane();
        vitalsGrid.setVgap(8);
        vitalsGrid.setHgap(20);
        vitalsGrid.setPadding(new Insets(5, 0, 5, 0));

        Label reasonKey = new Label("Reason");
        reasonKey.getStyleClass().add("vital-key");
        Label reasonVal = new Label("Cardiology Consult");
        reasonVal.getStyleClass().add("vital-val");

        Label vitalsKey = new Label("Vitals");
        vitalsKey.getStyleClass().add("vital-key");
        Label vitalsVal = new Label("BP 140/90");
        vitalsVal.getStyleClass().add("vital-val-alert");

        vitalsGrid.add(reasonKey, 0, 0);
        vitalsGrid.add(reasonVal, 1, 0);
        vitalsGrid.add(vitalsKey, 0, 1);
        vitalsGrid.add(vitalsVal, 1, 1);

        Button profileBtn = new Button("View Full Profile");
        profileBtn.getStyleClass().add("btn-primary-block");
        profileBtn.setMaxWidth(Double.MAX_VALUE);
        profileBtn.setOnAction(e -> Navigation.goTo(stage, () -> new PatientDetailsView(stage).getScene()));

        Button rescheduleBtn = new Button("Reschedule Appointment");
        rescheduleBtn.getStyleClass().add("btn-outline-block");
        rescheduleBtn.setMaxWidth(Double.MAX_VALUE);

        ImageView calendarIcon = createImageView("/images/icons/ic_calendar.png", 14, 14);
        if (calendarIcon != null) {
            rescheduleBtn.setGraphic(calendarIcon);
        }

        rescheduleBtn.setOnAction(e -> showRescheduleDialog("Elena Rodriguez"));

        card.getChildren().addAll(header, patientProfile, vitalsGrid, profileBtn, rescheduleBtn);
        return card;
    }

    /** Interactive Queue Card */
    private VBox createQueueCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("side-card");
        card.setPadding(new Insets(15));

        BorderPane header = new BorderPane();
        Label title = new Label("Queue");
        title.getStyleClass().add("queue-title");

        Label badge = new Label("4 Waiting");
        badge.getStyleClass().add("queue-badge");

        header.setLeft(title);
        header.setRight(badge);

        VBox queueList = new VBox(8);

        // Queue Item 1
        HBox item1 = new HBox(10);
        item1.getStyleClass().add("queue-item");
        item1.setAlignment(Pos.CENTER_LEFT);

        StackPane avatar1 = createInitialsAvatar("DK");
        VBox info1 = new VBox(2);
        Label name1 = new Label("David Kim");
        name1.getStyleClass().add("queue-name");
        Label time1 = new Label("01:00 PM");
        time1.getStyleClass().add("queue-time");
        info1.getChildren().addAll(name1, time1);

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        Label status1 = new Label("Waiting");
        status1.getStyleClass().add("pill-waiting");

        item1.getChildren().addAll(avatar1, info1, spacer1, status1);
        item1.setOnMouseClicked(e -> Navigation.goTo(stage, () -> new PatientDetailsView(stage).getScene()));

        // Queue Item 2 (Emergency Alert Item)
        HBox item2 = new HBox(10);
        item2.getStyleClass().add("queue-item-alert");
        item2.setAlignment(Pos.CENTER_LEFT);

        StackPane avatar2 = createInitialsAvatar("TW");
        avatar2.setStyle("-fx-background-color: #FCE8E6;");
        VBox info2 = new VBox(2);
        Label name2 = new Label("Thomas Wright");
        name2.getStyleClass().add("queue-name");
        Label time2 = new Label("02:00 PM");
        time2.getStyleClass().add("queue-time");
        info2.getChildren().addAll(name2, time2);

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        ImageView warningIcon = createImageView("/images/icons/ic_alert_red.png", 16, 16);

        item2.getChildren().addAll(avatar2, info2, spacer2);
        if (warningIcon != null) {
            item2.getChildren().add(warningIcon);
        }
        item2.setOnMouseClicked(e -> Navigation.goTo(stage, () -> new PatientDetailsView(stage).getScene()));

        queueList.getChildren().addAll(item1, item2);
        card.getChildren().addAll(header, queueList);
        return card;
    }

    /** Interactive Modal Dialog for Rescheduling Appointments */
    private void showRescheduleDialog(String patientName) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setTitle("Reschedule Appointment");

        VBox dialogRoot = new VBox(15);
        dialogRoot.setPadding(new Insets(20));
        dialogRoot.setAlignment(Pos.CENTER_LEFT);

        Label header = new Label("Reschedule Appointment for " + patientName);
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(1));
        datePicker.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> timeSlotCombo = new ComboBox<>();
        timeSlotCombo.getItems().addAll("09:00 AM", "10:00 AM", "11:30 AM", "02:00 PM", "04:00 PM");
        timeSlotCombo.setValue("10:00 AM");
        timeSlotCombo.setMaxWidth(Double.MAX_VALUE);

        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> dialog.close());

        Button confirmBtn = new Button("Confirm Reschedule");
        confirmBtn.setStyle("-fx-background-color: #0052CC; -fx-text-fill: white; -fx-font-weight: bold;");
        confirmBtn.setOnAction(e -> {
            dialog.close();
            showInformationAlert("Appointment Rescheduled", 
                    "Successfully rescheduled " + patientName + "'s appointment to " 
                    + datePicker.getValue() + " at " + timeSlotCombo.getValue() + ".");
        });

        actionButtons.getChildren().addAll(cancelBtn, confirmBtn);
        dialogRoot.getChildren().addAll(header, new Label("Select New Date:"), datePicker, new Label("Select Time Slot:"), timeSlotCombo, actionButtons);

        Scene dialogScene = new Scene(dialogRoot, 360, 260);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    /** Interactive Global Search Modal */
    private void showSearchDialog() {
        TextInputDialog searchDialog = new TextInputDialog();
        searchDialog.setTitle("Global Search");
        searchDialog.setHeaderText("Search Doctor Agenda");
        searchDialog.setContentText("Enter patient name or record ID:");
        searchDialog.showAndWait().ifPresent(query -> {
            if (!query.trim().isEmpty()) {
                showInformationAlert("Search Result", "Found 1 record matching '" + query + "'.");
            }
        });
    }

    private void updateCurrentDate(LocalDate newDate) {
        this.currentDate = newDate;
        this.selectedCalendarDate = newDate;
        if (dateTitleLabel != null) {
            dateTitleLabel.setText(formatDateTitle(currentDate));
        }
        refreshCalendarDisplay();
    }

    private String formatDateTitle(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("EEEE, MMM d"));
    }

    private String formatMonthTitle(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
    }

    private void showInformationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private StackPane createInitialsAvatar(String initials) {
        StackPane avatar = new StackPane();
        avatar.getStyleClass().add("initials-avatar");
        Label text = new Label(initials);
        text.getStyleClass().add("initials-text");
        avatar.getChildren().add(text);
        return avatar;
    }

    /** Helper method for safely loading image resources */
    private ImageView createImageView(String resourcePath, double width, double height) {
        try {
            InputStream is = getClass().getResourceAsStream(resourcePath);
            if (is != null) {
                ImageView imageView = new ImageView(new Image(is));
                imageView.setFitWidth(width);
                imageView.setFitHeight(height);
                imageView.setPreserveRatio(true);
                return imageView;
            }
        } catch (Exception ignored) {}
        return null;
    }
}