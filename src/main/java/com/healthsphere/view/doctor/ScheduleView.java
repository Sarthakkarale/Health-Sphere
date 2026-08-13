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
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.Objects;

public class ScheduleView {

    private final Stage stage;

    public ScheduleView(Stage stage) {
        this.stage = stage;
    }

    public Scene createScene() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("main-container");

        // 1. Sidebar Navigation
        mainLayout.setLeft(createSidebar());

        // 2. Center Content Area
        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(24, 28, 28, 28));
        contentBox.getStyleClass().add("content-area");

        HBox headerBar = createHeaderBar();

        HBox splitView = new HBox(20);
        HBox.setHgrow(splitView, Priority.ALWAYS);

        VBox leftColumn = createLeftColumn();
        VBox centerColumn = createCenterTimelineColumn();
        VBox rightColumn = createPatientDetailsDrawer();

        HBox.setHgrow(centerColumn, Priority.ALWAYS);

        splitView.getChildren().addAll(leftColumn, centerColumn, rightColumn);
        contentBox.getChildren().addAll(headerBar, splitView);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("custom-scroll-pane");

        mainLayout.setCenter(scrollPane);

        Scene scene = new Scene(mainLayout, stage.getWidth(), stage.getHeight());

        try {
            scene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/css/schedule.css")).toExternalForm());
        } catch (Exception e) {
            System.err.println("CSS file /css/schedule.css not found.");
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
    // 1. LEFT SIDEBAR NAVIGATION
    // =========================================================================
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(220);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(24, 16, 24, 16));

        Label brandLabel = new Label("MedPulse AI");
        brandLabel.getStyleClass().add("brand-title");

        VBox navBox = new VBox(6);
        navBox.setPadding(new Insets(28, 0, 0, 0));

        Button btnHome = createNavButton("Home", false, "M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z");
        Button btnSchedule = createNavButton("Schedule", true, "M19 4h-1V2h-2v2H8V2H6v2H5c-1.11 0-1.99.9-1.99 2L3 20c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V10h14v10z");
        Button btnPatients = createNavButton("Patients", false, "M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5z");
        Button btnAnalytics = createNavButton("Analytics", false, "M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z");

        btnHome.setOnAction(e -> stage.setScene(new DoctorDashboardView(stage).createScene()));
        btnSchedule.setOnAction(e -> stage.setScene(new ScheduleView(stage).createScene()));

        navBox.getChildren().addAll(btnHome, btnSchedule, btnPatients, btnAnalytics);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox profileCard = new HBox(10);
        profileCard.getStyleClass().add("profile-card");
        profileCard.setAlignment(Pos.CENTER_LEFT);
        profileCard.setPadding(new Insets(10));

        Circle avatarCircle = new Circle(16, Color.web("#1D4ED8"));
        Label avatarText = new Label("DR");
        avatarText.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px;");
        StackPane avatarNode = new StackPane(avatarCircle, avatarText);

        VBox userDetails = new VBox(2);
        Label userName = new Label("Dr. Sarah Miller");
        userName.getStyleClass().add("profile-name");
        Label userRole = new Label("Cardiologist");
        userRole.getStyleClass().add("profile-role");
        userDetails.getChildren().addAll(userName, userRole);

        profileCard.getChildren().addAll(avatarNode, userDetails);
        sidebar.getChildren().addAll(brandLabel, navBox, spacer, profileCard);
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
    // 2. TOP HEADER BAR
    // =========================================================================
    private HBox createHeaderBar() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Hyperlink breadcrumb = new Hyperlink("Home  /  Schedule");
        breadcrumb.getStyleClass().add("breadcrumb");
        breadcrumb.setOnAction(e -> stage.setScene(new DoctorDashboardView(stage).createScene()));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actionsBox = new HBox(12);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);

        HBox searchContainer = new HBox(8);
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.getStyleClass().add("search-container");
        searchContainer.setPadding(new Insets(0, 12, 0, 12));

        SVGPath searchIcon = createSVGPath("M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z", "#94A3B8", 0.7);
        TextField searchField = new TextField();
        searchField.setPromptText("Global search...");
        searchField.getStyleClass().add("search-field-inner");

        searchContainer.getChildren().addAll(searchIcon, searchField);

        Button btnBell = new Button();
        btnBell.getStyleClass().add("icon-button");
        SVGPath bellIcon = createSVGPath("M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.64-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z", "#DC2626", 0.75);
        btnBell.setGraphic(bellIcon);

        Button btnSettings = new Button();
        btnSettings.getStyleClass().add("icon-button");
        SVGPath gearIcon = createSVGPath("M19.43 12.98c.04-.32.07-.64.07-.98s-.03-.66-.07-.98l2.11-1.65c.19-.15.24-.42.12-.64l-2-3.46c-.12-.22-.39-.3-.61-.22l-2.49 1c-.52-.4-1.08-.73-1.69-.98l-.38-2.65C14.46 2.18 14.25 2 14 2h-4c-.25 0-.46.18-.49.42l-.38 2.65c-.61.25-1.17.59-1.69.98l-2.49-1c-.23-.09-.49 0-.61.22l-2 3.46c-.13.22-.07.49.12.64l2.11 1.65c-.04.32-.07.65-.07.98s.03.66.07.98l-2.11 1.65c-.19.15-.24.42-.12.64l2 3.46c.12.22.39.3.61.22l2.49-1c.52.4 1.08.73 1.69.98l.38 2.65c.03.24.24.42.49.42h4c.25 0 .46-.18.49-.42l.38-2.65c.61-.25 1.17-.59 1.69-.98l2.49 1c.23.09.49 0 .61-.22l2-3.46c.12-.22.07-.49-.12-.64l-2.11-1.65zM12 15.5c-1.93 0-3.5-1.57-3.5-3.5s1.57-3.5 3.5-3.5 3.5 1.57 3.5 3.5-1.57 3.5-3.5 3.5z", "#64748B", 0.75);
        btnSettings.setGraphic(gearIcon);

        actionsBox.getChildren().addAll(searchContainer, btnBell, btnSettings);
        header.getChildren().addAll(breadcrumb, spacer, actionsBox);
        return header;
    }

    // =========================================================================
    // 3. LEFT COLUMN (Calendar, Filters & AI Assistant)
    // =========================================================================
    private VBox createLeftColumn() {
        VBox column = new VBox(16);
        column.setPrefWidth(280);

        // Calendar Card
        VBox calendarCard = new VBox(12);
        calendarCard.getStyleClass().add("card");
        calendarCard.setPadding(new Insets(16));

        HBox calHeader = new HBox();
        calHeader.setAlignment(Pos.CENTER_LEFT);
        Label monthLabel = new Label("October 2023");
        monthLabel.getStyleClass().add("calendar-month-title");

        Region calSpacer = new Region();
        HBox.setHgrow(calSpacer, Priority.ALWAYS);

        Button btnPrev = new Button("<");
        btnPrev.getStyleClass().add("icon-button-subtle");
        Button btnNext = new Button(">");
        btnNext.getStyleClass().add("icon-button-subtle");

        calHeader.getChildren().addAll(monthLabel, calSpacer, btnPrev, btnNext);

        GridPane daysGrid = new GridPane();
        daysGrid.setHgap(8);
        daysGrid.setVgap(8);
        daysGrid.setAlignment(Pos.CENTER);

        String[] headers = {"S", "M", "T", "W", "T", "F", "S"};
        for (int i = 0; i < 7; i++) {
            Label hLbl = new Label(headers[i]);
            hLbl.getStyleClass().add("calendar-day-header");
            daysGrid.add(hLbl, i, 0);
        }

        String[][] dates = {
                {"26", "27", "28", "29", "30", "1", "2"},
                {"3", "4", "5", "6", "7", "8", "9"},
                {"10", "11", "12", "13", "14", "", ""}
        };

        for (int row = 0; row < dates.length; row++) {
            for (int col = 0; col < dates[row].length; col++) {
                String val = dates[row][col];
                if (!val.isEmpty()) {
                    Label dateLbl = new Label(val);
                    dateLbl.getStyleClass().add("calendar-date-cell");
                    if (val.equals("5")) {
                        dateLbl.getStyleClass().add("calendar-date-selected");
                    } else if (row == 0 && col < 5) {
                        dateLbl.getStyleClass().add("calendar-date-muted");
                    }
                    daysGrid.add(dateLbl, col, row + 1);
                }
            }
        }

        calendarCard.getChildren().addAll(calHeader, daysGrid);

        // Filters Card
        VBox filtersCard = new VBox(12);
        filtersCard.getStyleClass().add("card");
        filtersCard.setPadding(new Insets(16));

        Label filterTitle = new Label("Filters");
        filterTitle.getStyleClass().add("card-title");

        TextField filterSearch = new TextField();
        filterSearch.setPromptText("Search Patient ID...");
        filterSearch.getStyleClass().add("search-field");

        Label statusHeader = new Label("APPOINTMENT STATUS");
        statusHeader.getStyleClass().add("section-subtitle");

        VBox statusList = new VBox(10);
        statusList.getChildren().addAll(
                createFilterCheckboxRow("Confirmed", "12", "badge-purple", true),
                createFilterCheckboxRow("Pending", "4", "badge-subtle", true),
                createFilterCheckboxRow("Completed", "8", "badge-green", false)
        );

        filtersCard.getChildren().addAll(filterTitle, filterSearch, statusHeader, statusList);

        // AI Assistant Card
        VBox aiCard = new VBox(10);
        aiCard.getStyleClass().add("ai-card");
        aiCard.setPadding(new Insets(16));

        HBox aiHeader = new HBox(6);
        aiHeader.setAlignment(Pos.CENTER_LEFT);
        SVGPath aiSpark = createSVGPath("M12 2L14.5 9.5L22 12L14.5 14.5L12 22L9.5 14.5L2 12L9.5 9.5L12 2Z", "#1D4ED8", 0.6);
        Label aiTitle = new Label("AI Assistant");
        aiTitle.getStyleClass().add("ai-card-title");

        aiHeader.getChildren().addAll(aiSpark, aiTitle);

        Label aiDesc = new Label("Today's patient load is 15% higher than usual. High-priority case: Marcus Bennett (10:30 AM) showing elevated BP trends.");
        aiDesc.setWrapText(true);
        aiDesc.getStyleClass().add("ai-card-text");

        aiCard.getChildren().addAll(aiHeader, aiDesc);

        column.getChildren().addAll(calendarCard, filtersCard, aiCard);
        return column;
    }

    private HBox createFilterCheckboxRow(String labelText, String countText, String badgeClass, boolean isChecked) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);

        CheckBox cb = new CheckBox(labelText);
        cb.setSelected(isChecked);
        cb.getStyleClass().add("custom-checkbox");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badge = new Label(countText);
        badge.getStyleClass().addAll("count-badge", badgeClass);

        row.getChildren().addAll(cb, spacer, badge);
        return row;
    }

    // =========================================================================
    // 4. CENTER COLUMN (Timeline)
    // =========================================================================
    private VBox createCenterTimelineColumn() {
        VBox column = new VBox(16);

        HBox scheduleHeader = new HBox();
        scheduleHeader.setAlignment(Pos.CENTER_LEFT);

        VBox titleGroup = new VBox(2);
        Label title = new Label("Today's Schedule");
        title.getStyleClass().add("schedule-main-title");
        Label dateSub = new Label("Thursday, Oct 5");
        dateSub.getStyleClass().add("schedule-subtitle");
        titleGroup.getChildren().addAll(title, dateSub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox btnGroup = new HBox(10);
        Button btnListView = new Button("List View");
        btnListView.getStyleClass().add("outline-button");

        Button btnAddSlot = new Button("+ Add Slot");
        btnAddSlot.getStyleClass().add("primary-button");

        btnGroup.getChildren().addAll(btnListView, btnAddSlot);
        scheduleHeader.getChildren().addAll(titleGroup, spacer, btnGroup);

        VBox timelineFeed = new VBox(16);
        timelineFeed.setPadding(new Insets(8, 0, 0, 0));

        timelineFeed.getChildren().addAll(
                createTimelineSlot("09:00 AM", "James Wilson", "#MN-2938", "General Check-up", false, false),
                createTimelineSlot("10:30 AM", "Marcus Bennett", "#MN-1044", "Cardiology Review", true, true),
                createTimelineSlot("11:45 AM", "Elena Rodriguez", "#MN-1055", "Medication Review", false, false)
        );

        column.getChildren().addAll(scheduleHeader, timelineFeed);
        return column;
    }

    private HBox createTimelineSlot(String time, String patientName, String patientId, String reason, boolean isActive, boolean isSelected) {
        HBox slot = new HBox(16);
        slot.setAlignment(Pos.TOP_LEFT);

        VBox timeBox = new VBox(2);
        timeBox.setPrefWidth(65);
        timeBox.setAlignment(Pos.TOP_RIGHT);
        Label timeLbl = new Label(time);
        timeLbl.getStyleClass().add("timeline-time-label");
        timeBox.getChildren().add(timeLbl);

        HBox card = new HBox(12);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14, 16, 14, 16));
        HBox.setHgrow(card, Priority.ALWAYS);

        card.getStyleClass().add(isSelected ? "timeline-card-active" : "card");

        ImageView avatar = createImageView("/images/doctor/uifaces-popular-avatar.png", 36, 36);
        Node avatarNode;
        if (avatar != null) {
            Circle clip = new Circle(18, 18, 18);
            avatar.setClip(clip);
            avatarNode = avatar;
        } else {
            Circle defaultBg = new Circle(18, Color.web("#3B82F6"));
            Label init = new Label(patientName.substring(0, 1));
            init.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");
            avatarNode = new StackPane(defaultBg, init);
        }

        VBox details = new VBox(4);
        HBox nameRow = new HBox(8);
        nameRow.setAlignment(Pos.CENTER_LEFT);

        Label nameLbl = new Label(patientName);
        nameLbl.getStyleClass().add("patient-card-name");

        Label idLbl = new Label("ID: " + patientId);
        idLbl.getStyleClass().add("patient-card-id");

        nameRow.getChildren().addAll(nameLbl, idLbl);

        Label reasonLbl = new Label("📋 " + reason);
        reasonLbl.getStyleClass().add("patient-card-reason");

        details.getChildren().addAll(nameRow, reasonLbl);

        card.getChildren().addAll(avatarNode, details);
        slot.getChildren().addAll(timeBox, card);
        return slot;
    }

    // =========================================================================
    // 5. RIGHT COLUMN (Patient Details Drawer Card)
    // =========================================================================
    private VBox createPatientDetailsDrawer() {
        VBox card = new VBox(16);
        card.setPrefWidth(360);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Patient Details");
        title.getStyleClass().add("card-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnClose = new Button("✕");
        btnClose.getStyleClass().add("icon-button-subtle");

        header.getChildren().addAll(title, spacer, btnClose);

        HBox profileBox = new HBox(12);
        profileBox.setAlignment(Pos.CENTER_LEFT);

        ImageView avatar = createImageView("/images/doctor/uifaces-popular-avatar (1).png", 48, 48);
        Node avatarNode;
        if (avatar != null) {
            Circle clip = new Circle(24, 24, 24);
            avatar.setClip(clip);
            avatarNode = avatar;
        } else {
            Circle circle = new Circle(24, Color.web("#1E3A8A"));
            Label init = new Label("MB");
            init.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;");
            avatarNode = new StackPane(circle, init);
        }

        VBox profileDetails = new VBox(4);
        Label nameLbl = new Label("Marcus Bennett");
        nameLbl.getStyleClass().add("drawer-patient-name");

        Label demoLbl = new Label("Male, 72 Years Old");
        demoLbl.getStyleClass().add("drawer-patient-sub");

        HBox tagsBox = new HBox(6);
        Label tag1 = new Label("HYPERTENSION");
        tag1.getStyleClass().add("tag-danger");
        Label tag2 = new Label("TYPE 2 DM");
        tag2.getStyleClass().add("tag-purple");
        tagsBox.getChildren().addAll(tag1, tag2);

        profileDetails.getChildren().addAll(nameLbl, demoLbl, tagsBox);
        profileBox.getChildren().addAll(avatarNode, profileDetails);

        VBox vitalsSection = new VBox(8);

        HBox vitalsHeader = new HBox();
        vitalsHeader.setAlignment(Pos.CENTER_LEFT);
        Label vitalsTitle = new Label("LATEST VITALS");
        vitalsTitle.getStyleClass().add("section-subtitle");

        Region vitalsSpacer = new Region();
        HBox.setHgrow(vitalsSpacer, Priority.ALWAYS);

        Hyperlink trendsLink = new Hyperlink("View Trends");
        trendsLink.getStyleClass().add("table-link");

        vitalsHeader.getChildren().addAll(vitalsTitle, vitalsSpacer, trendsLink);

        HBox vitalsGrid = new HBox(10);

        VBox hrCard = createVitalCardWithChart("Heart Rate", "78", "bpm", new double[]{12, 18, 14, 22, 19}, "#0D9488", "vital-card-normal");
        VBox bpCard = createVitalCardWithChart("BP", "145/92", "mmHg", new double[]{18, 22, 28, 24, 32}, "#DC2626", "vital-card-warning");

        vitalsGrid.getChildren().addAll(hrCard, bpCard);
        vitalsSection.getChildren().addAll(vitalsHeader, vitalsGrid);

        VBox reasonBox = new VBox(8);
        reasonBox.getStyleClass().add("reason-box");
        reasonBox.setPadding(new Insets(12));

        Label reasonTitle = new Label("REASON FOR VISIT");
        reasonTitle.getStyleClass().add("section-subtitle");

        Label reasonText = new Label("Patient reports recurring dizziness and mild chest tightness after light physical activity. Follow-up for post-surgery medication adjustments.");
        reasonText.setWrapText(true);
        reasonText.getStyleClass().add("reason-text");

        reasonBox.getChildren().addAll(reasonTitle, reasonText);

        VBox historyBox = new VBox(8);
        Label historyTitle = new Label("RECENT HISTORY");
        historyTitle.getStyleClass().add("section-subtitle");

        VBox historyList = new VBox(8);
        historyList.getChildren().addAll(
                createHistoryItem("Coronary Stent Placement", "Aug 12, 2023 • Dr. Aris Thorne"),
                createHistoryItem("Annual Lab Results - Abnormal Glucose", "Jul 05, 2023 • LabCorp")
        );

        historyBox.getChildren().addAll(historyTitle, historyList);

        HBox actionRow = new HBox(10);
        actionRow.setAlignment(Pos.CENTER);

        Button btnRecords = new Button("📄 Open Records");
        btnRecords.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnRecords, Priority.ALWAYS);
        btnRecords.getStyleClass().add("primary-button");

        Button btnMore = new Button("⋮");
        btnMore.getStyleClass().add("outline-button");

        actionRow.getChildren().addAll(btnRecords, btnMore);

        card.getChildren().addAll(header, profileBox, vitalsSection, reasonBox, historyBox, actionRow);
        return card;
    }

    private VBox createVitalCardWithChart(String label, String val, String unit, double[] barHeights, String barColor, String styleClass) {
        VBox card = new VBox(6);
        card.getStyleClass().addAll("vital-card", styleClass);
        card.setPadding(new Insets(10));
        HBox.setHgrow(card, Priority.ALWAYS);

        Label titleLbl = new Label(label);
        titleLbl.getStyleClass().add("vital-title");

        HBox contentRow = new HBox();
        contentRow.setAlignment(Pos.BOTTOM_LEFT);

        VBox valBox = new VBox(2);
        Label valLbl = new Label(val);
        valLbl.getStyleClass().add("vital-value");

        Label unitLbl = new Label(unit);
        unitLbl.getStyleClass().add("vital-unit");
        valBox.getChildren().addAll(valLbl, unitLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox chartBars = new HBox(3);
        chartBars.setAlignment(Pos.BOTTOM_RIGHT);
        for (double h : barHeights) {
            Rectangle bar = new Rectangle(4, h, Color.web(barColor));
            bar.setArcWidth(2);
            bar.setArcHeight(2);
            chartBars.getChildren().add(bar);
        }

        contentRow.getChildren().addAll(valBox, spacer, chartBars);
        card.getChildren().addAll(titleLbl, contentRow);
        return card;
    }

    private VBox createHistoryItem(String title, String subtitle) {
        VBox item = new VBox(2);
        Label titleLbl = new Label("• " + title);
        titleLbl.getStyleClass().add("history-title");

        Label subLbl = new Label("  " + subtitle);
        subLbl.getStyleClass().add("history-sub");

        item.getChildren().addAll(titleLbl, subLbl);
        return item;
    }
}