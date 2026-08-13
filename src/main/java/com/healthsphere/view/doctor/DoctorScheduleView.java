package com.healthsphere.view.doctor;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.Objects;

public class DoctorScheduleView {

    private final Stage stage;

    public DoctorScheduleView(Stage stage) {
        this.stage = stage;
    }

    public Scene createScene() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("main-container");

        // 1. Sidebar Navigation
        mainLayout.setLeft(createSidebar());

        // 2. Main Workspace Layout
        VBox contentBox = new VBox(16);
        contentBox.setPadding(new Insets(16, 24, 24, 24));
        contentBox.getStyleClass().add("content-area");

        HBox topHeaderBar = createTopHeaderBar();
        HBox pageTitleBar = createPageTitleBar();

        HBox splitLayout = new HBox(20);
        HBox.setHgrow(splitLayout, Priority.ALWAYS);

        VBox leftCol = createLeftControlColumn();
        leftCol.setPrefWidth(280);

        VBox rightCol = createRightScheduleColumn();
        HBox.setHgrow(rightCol, Priority.ALWAYS);

        splitLayout.getChildren().addAll(leftCol, rightCol);
        contentBox.getChildren().addAll(topHeaderBar, pageTitleBar, splitLayout);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("custom-scroll-pane");

        mainLayout.setCenter(scrollPane);

        Scene scene = new Scene(mainLayout, stage.getWidth(), stage.getHeight());

        try {
            scene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/css/doctor_schedule.css")).toExternalForm());
        } catch (Exception e) {
            System.err.println("CSS file /css/doctor_schedule.css not found.");
        }

        return scene;
    }

    private SVGPath createSVGPath(String d, String fillColor, double scale) {
        SVGPath path = new SVGPath();
        path.setContent(d);
        path.setFill(Color.web(fillColor));
        path.setScaleX(scale);
        path.setScaleY(scale);
        return path;
    }

    private ImageView createImageView(String resourcePath, double width, double height) {
        try {
            InputStream stream = getClass().getResourceAsStream(resourcePath);
            if (stream != null) {
                Image img = new Image(stream);
                ImageView iv = new ImageView(img);
                iv.setFitWidth(width);
                iv.setFitHeight(height);
                iv.setPreserveRatio(true);
                iv.setSmooth(true);
                return iv;
            }
        } catch (Exception e) {
            System.err.println("Resource missing: " + resourcePath);
        }
        return null;
    }

    // =========================================================================
    // 1. SIDEBAR NAVIGATION
    // =========================================================================
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(210);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(24, 16, 24, 16));

        VBox brandBox = new VBox(2);
        Label brandLabel = new Label("MediNexus AI");
        brandLabel.getStyleClass().add("brand-title");
        Label brandSub = new Label("Doctor Module");
        brandSub.getStyleClass().add("brand-subtitle");
        brandBox.getChildren().addAll(brandLabel, brandSub);

        VBox mainNavBox = new VBox(6);
        mainNavBox.setPadding(new Insets(24, 0, 0, 0));

        Button btnDashboard = createNavButton("Dashboard", false, "M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z");
        Button btnSchedule = createNavButton("Schedule", true, "M19 4h-1V2h-2v2H8V2H6v2H5c-1.11 0-1.99.9-1.99 2L3 20c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V10h14v10zm0-12H5V6h14v2z");
        Button btnQueue = createNavButton("Patient Queue", false, "M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5z");
        Button btnPatients = createNavButton("Patients", false, "M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z");
        Button btnReports = createNavButton("Reports", false, "M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z");
        Button btnAiAssistant = createNavButton("AI Assistant", false, "M12 2L14.5 9.5L22 12L14.5 14.5L12 22L9.5 14.5L2 12L9.5 9.5L12 2Z");

        btnDashboard.setOnAction(e -> stage.setScene(new DoctorDashboardView(stage).createScene()));
        btnQueue.setOnAction(e -> stage.setScene(new PatientQueueView(stage).createScene()));
        btnReports.setOnAction(e -> stage.setScene(new PrescriptionManagementView(stage).createScene()));
        btnAiAssistant.setOnAction(e -> stage.setScene(new AiAssistantView(stage).createScene()));

        mainNavBox.getChildren().addAll(btnDashboard, btnSchedule, btnQueue, btnPatients, btnReports, btnAiAssistant);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnConsultation = new Button("+  New Consultation");
        btnConsultation.setMaxWidth(Double.MAX_VALUE);
        btnConsultation.getStyleClass().add("primary-button-lg");

        sidebar.getChildren().addAll(brandBox, mainNavBox, spacer, btnConsultation);
        return sidebar;
    }

    private Button createNavButton(String title, boolean isActive, String iconSvg) {
        Button btn = new Button(title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.getStyleClass().add(isActive ? "nav-button-active" : "nav-button");

        SVGPath icon = createSVGPath(iconSvg, isActive ? "#FFFFFF" : "#64748B", 0.75);
        btn.setGraphic(icon);
        btn.setGraphicTextGap(12);
        return btn;
    }

    // =========================================================================
    // 2. HEADER BARS
    // =========================================================================
    private HBox createTopHeaderBar() {
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        HBox searchContainer = new HBox(8);
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.getStyleClass().add("search-container");
        searchContainer.setPadding(new Insets(0, 12, 0, 12));
        searchContainer.setPrefWidth(360);

        SVGPath searchIcon = createSVGPath("M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z", "#94A3B8", 0.7);
        TextField searchField = new TextField();
        searchField.setPromptText("Search patients, schedules, or records...");
        searchField.getStyleClass().add("search-field-inner");
        searchContainer.getChildren().addAll(searchIcon, searchField);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnBell = new Button();
        btnBell.getStyleClass().add("icon-button");
        btnBell.setGraphic(createSVGPath("M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.64-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z", "#475569", 0.7));

        Button btnSettings = new Button();
        btnSettings.getStyleClass().add("icon-button");
        btnSettings.setGraphic(createSVGPath("M19.43 12.98c.04-.32.07-.64.07-.98s-.03-.66-.07-.98l2.11-1.65c.19-.15.24-.42.12-.64l-2-3.46c-.12-.22-.39-.3-.61-.22l-2.49 1c-.52-.4-1.08-.73-1.69-.98l-.38-2.65C14.46 2.18 14.25 2 14 2h-4c-.25 0-.46.18-.49.42l-.38 2.65c-.61.25-1.17.59-1.69.98l-2.49-1c-.23-.09-.49 0-.61.22l-2 3.46c-.13.22-.07.49.12.64l2.11 1.65c-.04.32-.07.65-.07.98s.03.66.07.98l-2.11 1.65c-.19.15-.24.42-.12.64l2 3.46c.12.22.39.3.61.22l2.49-1c.52.4 1.08.73 1.69.98l.38 2.65c.03.24.24.42.49.42h4c.25 0 .46-.18.49-.42l.38-2.65c.61-.25 1.17-.59 1.69-.98l2.49 1c.23.09.49 0 .61-.22l2-3.46c.12-.22.07-.49-.12-.64l-2.11-1.65zM12 15.5c-1.93 0-3.5-1.57-3.5-3.5s1.57-3.5 3.5-3.5 3.5 1.57 3.5 3.5-1.57 3.5-3.5-3.5z", "#475569", 0.7));

        Separator sep = new Separator(javafx.geometry.Orientation.VERTICAL);
        sep.setPrefHeight(20);

        HBox userBox = new HBox(8);
        userBox.setAlignment(Pos.CENTER_LEFT);
        VBox userText = new VBox(1);
        userText.setAlignment(Pos.CENTER_RIGHT);
        Label userName = new Label("Dr. Smith");
        userName.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label userRole = new Label("Neurosurgeon");
        userRole.setStyle("-fx-font-size: 9px; -fx-text-fill: #64748B;");
        userText.getChildren().addAll(userName, userRole);

        ImageView docAvatar = createImageView("/images/doctor/portrait-3d-male-doctor.png", 32, 32);
        Node avatarNode;
        if (docAvatar != null) {
            Circle clip = new Circle(16, 16, 16);
            docAvatar.setClip(clip);
            avatarNode = docAvatar;
        } else {
            avatarNode = new Circle(16, Color.web("#1D4ED8"));
        }

        userBox.getChildren().addAll(userText, avatarNode);
        header.getChildren().addAll(searchContainer, spacer, btnBell, btnSettings, sep, userBox);
        return header;
    }

    private HBox createPageTitleBar() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titles = new VBox(2);
        Label title = new Label("Availability & Schedule");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label subtitle = new Label("Define your clinical hours, consultation preferences, and specialized availability windows.");
        subtitle.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");
        titles.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actions = new HBox(10);
        Button btnCancel = new Button("Cancel");
        btnCancel.getStyleClass().add("outline-button");

        Button btnSave = new Button("Save Schedule");
        btnSave.getStyleClass().add("primary-button-action");

        actions.getChildren().addAll(btnCancel, btnSave);
        header.getChildren().addAll(titles, spacer, actions);
        return header;
    }

    // =========================================================================
    // 3. LEFT CONTROL COLUMN (Preferences, Special, Recurrence, AI Insights)
    // =========================================================================
    private VBox createLeftControlColumn() {
        VBox column = new VBox(14);

        // Consultation Types Card
        VBox consultTypesCard = createCard();
        Label ctTitle = createCardTitle("Consultation Types", "M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2z");

        VBox row1 = createToggleRow("Online Consultation", "Enable video/chat sessions", true);
        VBox row2 = createToggleRow("Offline Consultation", "Physical in-clinic visits", true);

        consultTypesCard.getChildren().addAll(ctTitle, row1, row2);

        // Special Availability Card
        VBox specialCard = createCard();
        Label saTitle = createCardTitle("Special Availability", "M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z");

        HBox emergencyBox = new HBox(8);
        emergencyBox.getStyleClass().add("emergency-box");
        emergencyBox.setAlignment(Pos.CENTER_LEFT);
        emergencyBox.setPadding(new Insets(10));

        SVGPath star = createSVGPath("M12 2L14.5 9.5L22 12L14.5 14.5L12 22L9.5 14.5L2 12L9.5 9.5L12 2Z", "#DC2626", 0.6);
        VBox emText = new VBox(2);
        Label emTitle = new Label("Emergency Mode");
        emTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #991B1B;");
        Label emSub = new Label("Override all breaks");
        emSub.setStyle("-fx-font-size: 9px; -fx-text-fill: #991B1B;");
        emText.getChildren().addAll(emTitle, emSub);

        Region emSpacer = new Region();
        HBox.setHgrow(emSpacer, Priority.ALWAYS);

        CheckBox chkEmergency = new CheckBox();

        emergencyBox.getChildren().addAll(star, emText, emSpacer, chkEmergency);

        Label vacLabel = new Label("Vacation Mode");
        vacLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #334155;");

        HBox datePickerBox = new HBox(8);
        datePickerBox.getStyleClass().add("input-box-subtle");
        datePickerBox.setAlignment(Pos.CENTER_LEFT);
        datePickerBox.setPadding(new Insets(8, 12, 8, 12));

        SVGPath calIcon = createSVGPath("M19 4h-1V2h-2v2H8V2H6v2H5c-1.11 0-1.99.9-1.99 2L3 20c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V10h14v10z", "#64748B", 0.6);
        Label dateVal = new Label("Nov 24 - Dec 02");
        dateVal.setStyle("-fx-font-size: 11px; -fx-text-fill: #334155;");
        datePickerBox.getChildren().addAll(calIcon, dateVal);

        specialCard.getChildren().addAll(saTitle, emergencyBox, vacLabel, datePickerBox);

        // Recurrence Selector Card
        VBox recurrenceCard = createCard();
        Label recTitle = new Label("Recurrence");
        recTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        HBox segmentedBtn = new HBox();
        segmentedBtn.getStyleClass().add("segmented-container");

        Button btnWeekly = new Button("Weekly");
        btnWeekly.getStyleClass().add("segmented-button-active");
        HBox.setHgrow(btnWeekly, Priority.ALWAYS);
        btnWeekly.setMaxWidth(Double.MAX_VALUE);

        Button btnMonthly = new Button("Monthly");
        btnMonthly.getStyleClass().add("segmented-button");
        HBox.setHgrow(btnMonthly, Priority.ALWAYS);
        btnMonthly.setMaxWidth(Double.MAX_VALUE);

        segmentedBtn.getChildren().addAll(btnWeekly, btnMonthly);
        recurrenceCard.getChildren().addAll(recTitle, segmentedBtn);

        // AI Schedule Analysis Card
        VBox aiCard = createCard();
        HBox aiHeader = new HBox(6);
        SVGPath spark = createSVGPath("M12 2L14.5 9.5L22 12L14.5 14.5L12 22L9.5 14.5L2 12L9.5 9.5L12 2Z", "#1D4ED8", 0.65);
        Label aiTitle = new Label("AI Schedule Analysis");
        aiTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");
        aiHeader.getChildren().addAll(spark, aiTitle);

        Label aiDesc = new Label("Based on your last 3 months, Tuesdays and Thursdays have a 45% higher patient load. We recommend extending afternoon hours by 30 mins to reduce wait times.");
        aiDesc.setWrapText(true);
        aiDesc.setStyle("-fx-font-size: 10px; -fx-text-fill: #475569; -fx-line-spacing: 2;");

        aiCard.getChildren().addAll(aiHeader, aiDesc);

        column.getChildren().addAll(consultTypesCard, specialCard, recurrenceCard, aiCard);
        return column;
    }

    private VBox createToggleRow(String title, String subtitle, boolean isSelected) {
        VBox box = new VBox(2);

        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);

        Label tLbl = new Label(title);
        tLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ToggleButton toggle = new ToggleButton();
        toggle.setSelected(isSelected);
        toggle.getStyleClass().add("custom-toggle");

        row.getChildren().addAll(tLbl, spacer, toggle);

        Label sLbl = new Label(subtitle);
        sLbl.setStyle("-fx-font-size: 9px; -fx-text-fill: #64748B;");

        box.getChildren().addAll(row, sLbl);
        return box;
    }

    // =========================================================================
    // 4. RIGHT SCHEDULE COLUMN (Standard Hours, Breaks, Forecast)
    // =========================================================================
    private VBox createRightScheduleColumn() {
        VBox column = new VBox(14);

        // Standard Working Hours Table Card
        VBox workingHoursCard = createCard();

        HBox whHeader = new HBox();
        whHeader.setAlignment(Pos.CENTER_LEFT);

        Label whTitle = new Label("Standard Working Hours");
        whTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Region whSpacer = new Region();
        HBox.setHgrow(whSpacer, Priority.ALWAYS);

        HBox navNav = new HBox(4);
        Button btnPrev = new Button("<");
        btnPrev.getStyleClass().add("icon-button-sm");
        Button btnNext = new Button(">");
        btnNext.getStyleClass().add("icon-button-sm");
        navNav.getChildren().addAll(btnPrev, btnNext);

        whHeader.getChildren().addAll(whTitle, whSpacer, navNav);

        VBox daysList = new VBox(10);
        daysList.setPadding(new Insets(8, 0, 8, 0));

        daysList.getChildren().addAll(
                createDayScheduleRow("Monday", "09:00 AM", "05:00 PM", "Lunch: 13:00 - 14:00", true),
                createDayScheduleRow("Tuesday", "09:00 AM", "05:00 PM", "Lunch: 13:00 - 14:00", true),
                createDayScheduleRow("Wednesday", "10:00 AM", "06:00 PM", "Break: 15:30 - 16:00", true),
                createClosedDayRow("Saturday", "Closed / Non-working day")
        );

        Button btnAddWindow = new Button("+  Add Custom Work Window");
        btnAddWindow.getStyleClass().add("link-button");

        workingHoursCard.getChildren().addAll(whHeader, daysList, btnAddWindow);

        // Default Breaks & Buffer Card
        VBox breaksCard = createCard();

        HBox bHeader = new HBox();
        bHeader.setAlignment(Pos.CENTER_LEFT);
        Label bTitle = new Label("Default Breaks & Buffer");
        bTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Region bSpacer = new Region();
        HBox.setHgrow(bSpacer, Priority.ALWAYS);

        Label globalSetting = new Label("Global Setting");
        globalSetting.getStyleClass().add("pill-badge");
        bHeader.getChildren().addAll(bTitle, bSpacer, globalSetting);

        GridPane breaksGrid = new GridPane();
        breaksGrid.setHgap(16);
        breaksGrid.setVgap(6);

        Label lblLunch = new Label("LUNCH BREAK DURATION");
        lblLunch.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #64748B;");

        ComboBox<String> cbLunch = new ComboBox<>();
        cbLunch.getItems().addAll("30 Minutes", "45 Minutes", "60 Minutes");
        cbLunch.setValue("60 Minutes");
        cbLunch.setMaxWidth(Double.MAX_VALUE);
        cbLunch.getStyleClass().add("custom-combo-box");

        Label lblBuffer = new Label("PATIENT PREP BUFFER");
        lblBuffer.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #64748B;");

        ComboBox<String> cbBuffer = new ComboBox<>();
        cbBuffer.getItems().addAll("5 Minutes", "10 Minutes", "15 Minutes");
        cbBuffer.setValue("10 Minutes");
        cbBuffer.setMaxWidth(Double.MAX_VALUE);
        cbBuffer.getStyleClass().add("custom-combo-box");

        breaksGrid.add(lblLunch, 0, 0);
        breaksGrid.add(cbLunch, 0, 1);
        breaksGrid.add(lblBuffer, 1, 0);
        breaksGrid.add(cbBuffer, 1, 1);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        breaksGrid.getColumnConstraints().addAll(col1, col2);

        breaksCard.getChildren().addAll(bHeader, breaksGrid);

        // Patient Load Forecast Card Placeholder
        VBox forecastCard = createCard();
        Label fcTitle = new Label("Patient Load Forecast");
        fcTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        forecastCard.getChildren().add(fcTitle);

        column.getChildren().addAll(workingHoursCard, breaksCard, forecastCard);
        return column;
    }

    private HBox createDayScheduleRow(String day, String startTime, String endTime, String breakText, boolean isChecked) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        CheckBox chk = new CheckBox();
        chk.setSelected(isChecked);

        Label lblDay = new Label(day);
        lblDay.setPrefWidth(80);
        lblDay.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        HBox startBox = createTimeBox(startTime);
        Label dash = new Label("-");
        dash.setStyle("-fx-text-fill: #94A3B8;");
        HBox endBox = createTimeBox(endTime);

        HBox breakBadge = new HBox(4);
        breakBadge.getStyleClass().add("break-chip");
        breakBadge.setAlignment(Pos.CENTER_LEFT);
        SVGPath clock = createSVGPath("M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67z", "#0D9488", 0.55);
        Label bLbl = new Label(breakText);
        bLbl.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #0D9488;");
        breakBadge.getChildren().addAll(clock, bLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnDelete = new Button();
        btnDelete.getStyleClass().add("icon-button-subtle");
        btnDelete.setGraphic(createSVGPath("M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z", "#94A3B8", 0.65));

        row.getChildren().addAll(chk, lblDay, startBox, dash, endBox, breakBadge, spacer, btnDelete);
        return row;
    }

    private HBox createClosedDayRow(String day, String text) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        CheckBox chk = new CheckBox(day);
        chk.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        chk.setPrefWidth(120);

        Label closedLbl = new Label(text);
        closedLbl.setStyle("-fx-font-size: 11px; -fx-font-style: italic; -fx-text-fill: #64748B;");

        row.getChildren().addAll(chk, closedLbl);
        return row;
    }

    private HBox createTimeBox(String time) {
        HBox box = new HBox(6);
        box.getStyleClass().add("time-input-box");
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(6, 10, 6, 10));

        Label tLbl = new Label(time);
        tLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #0F172A;");

        SVGPath clock = createSVGPath("M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67z", "#64748B", 0.55);

        box.getChildren().addAll(tLbl, clock);
        return box;
    }

    private VBox createCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(14));
        return card;
    }

    private Label createCardTitle(String text, String svg) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        if (svg != null) {
            SVGPath icon = createSVGPath(svg, "#1D4ED8", 0.65);
            lbl.setGraphic(icon);
            lbl.setGraphicTextGap(8);
        }
        return lbl;
    }
}