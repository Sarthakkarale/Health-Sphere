package com.healthsphere.view.doctor;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.Objects;

/**
 * DoctorDashboardView represents the main dashboard view for Doctors in Health-Sphere.
 */
public class DoctorDashboardView {

    private final Stage stage;
    private final Scene scene;

    public DoctorDashboardView(Stage stage) {
        this.stage = stage;
        this.scene = createScene();
    }

    public Scene getScene() {
        return this.scene;
    }

    private Scene createScene() {
        BorderPane mainRoot = new BorderPane();
        mainRoot.getStyleClass().add("root-pane");

        // --- Sidebar Navigation (LEFT) ---
        VBox sidebar = createSidebar();
        mainRoot.setLeft(sidebar);

        // --- Main Content Area (CENTER) ---
        VBox mainContent = new VBox(24);
        mainContent.setPadding(new Insets(24));
        mainContent.getStyleClass().add("content-area");

        // 1. Top Bar
        BorderPane topBar = createTopBar();
        mainContent.getChildren().add(topBar);

        // 2. Welcome Banner
        HBox welcomeBanner = createWelcomeBanner();
        mainContent.getChildren().add(welcomeBanner);

        // 3. Stat Cards Row
        HBox statCardsRow = createStatCardsRow();
        mainContent.getChildren().add(statCardsRow);

        // 4. Appointments & AI Insights Row
        HBox appointmentsInsightsRow = createAppointmentsInsightsRow();
        mainContent.getChildren().add(appointmentsInsightsRow);

        // 5. Recent Patient Activity Table
        VBox recentActivityTable = createRecentActivityTable();
        mainContent.getChildren().add(recentActivityTable);

        mainRoot.setCenter(mainContent);

        // Outer ScrollPane wrapping the entire mainRoot to allow vertical scrolling to the bottom
        ScrollPane outerScrollPane = new ScrollPane(mainRoot);
        outerScrollPane.setFitToWidth(true);
        outerScrollPane.setFitToHeight(true);
        outerScrollPane.getStyleClass().add("content-scrollpane");

        Scene dashboardScene = new Scene(outerScrollPane, stage.getWidth(), stage.getHeight());

        // Add external stylesheet if available
        try {
            dashboardScene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/dashboard.css")).toExternalForm()
            );
        } catch (Exception ignored) {}

        return dashboardScene;
    }

    /** Creates the left navigation sidebar. */
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPadding(new Insets(24, 16, 24, 16));
        sidebar.getStyleClass().add("sidebar");
        sidebar.setMinWidth(260);
        sidebar.setPrefWidth(260);

        // Logo Section
        HBox logoSection = new HBox(12);
        logoSection.setAlignment(Pos.CENTER_LEFT);
        logoSection.setPadding(new Insets(0, 0, 32, 0));

        ImageView logoImage = createImageView("/images/doctor/doctor_logo.png", 32, 32);
        VBox logoText = new VBox(2);
        Label appName = new Label("Health-Sphere");
        appName.getStyleClass().add("logo-name");
        Label doctorSubtext = new Label("Doctor Dashboard");
        doctorSubtext.getStyleClass().add("logo-subtext");
        logoText.getChildren().addAll(appName, doctorSubtext);

        if (logoImage != null) {
            logoSection.getChildren().add(logoImage);
        }
        logoSection.getChildren().add(logoText);

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
            HBox navTab = new HBox(14);
            navTab.setAlignment(Pos.CENTER_LEFT);
            navTab.setPadding(new Insets(10, 14, 10, 14));
            navTab.getStyleClass().add("nav-tab");

            if (i == 0) {
                navTab.getStyleClass().add("nav-tab-active");
            }

            ImageView icon = createImageView("/images/icons/" + icons[i] + ".png", 18, 18);
            Label tabLabel = new Label(tabs[i]);
            tabLabel.getStyleClass().add("nav-text");

            if (icon != null) {
                navTab.getChildren().add(icon);
            }
            navTab.getChildren().add(tabLabel);

            // Handle Navigation Click
            navTab.setOnMouseClicked(event -> handleSidebarTabClick(tabIndex));
            navItems.getChildren().add(navTab);
        }

        // Footer Section (Doctor Profile + Logout)
        VBox footer = new VBox(12);
        footer.setAlignment(Pos.BOTTOM_CENTER);
        VBox.setVgrow(footer, Priority.ALWAYS);

        HBox doctorProfile = new HBox(12);
        doctorProfile.setAlignment(Pos.CENTER_LEFT);
        doctorProfile.setPadding(new Insets(10, 14, 10, 14));
        doctorProfile.getStyleClass().add("sidebar-profile");

        ImageView profileIcon = createImageView("/images/doctor/doctor_profile.png", 32, 32);
        VBox profileText = new VBox(2);
        Label doctorName = new Label("Doctor Profile");
        doctorName.getStyleClass().add("sidebar-profile-role");
        Label doctorRole = new Label("Dr. Sarah");
        doctorRole.getStyleClass().add("sidebar-profile-name");
        profileText.getChildren().addAll(doctorName, doctorRole);

        if (profileIcon != null) {
            doctorProfile.getChildren().add(profileIcon);
        }
        doctorProfile.getChildren().add(profileText);
        doctorProfile.setOnMouseClicked(event -> handleSidebarTabClick(6)); // Doctor Profile

        HBox logout = new HBox(14);
        logout.setAlignment(Pos.CENTER_LEFT);
        logout.setPadding(new Insets(10, 14, 10, 14));
        logout.getStyleClass().add("nav-tab");

        ImageView logoutIcon = createImageView("/images/icons/ic_logout.png", 18, 18);
        Label logoutLabel = new Label("Logout");
        logoutLabel.getStyleClass().add("nav-text");

        if (logoutIcon != null) {
            logout.getChildren().add(logoutIcon);
        }
        logout.getChildren().add(logoutLabel);

        logout.setOnMouseClicked(event -> {
            System.out.println("Logging out...");
            // Handle Logout action or scene transition here
        });

        footer.getChildren().addAll(doctorProfile, logout);
        sidebar.getChildren().addAll(logoSection, navItems, footer);
        return sidebar;
    }

    /** Navigation routing method */
    private void handleSidebarTabClick(int index) {
        switch (index) {
            case 0:
                stage.setScene(new DoctorDashboardView(stage).getScene());
                break;
            case 1:
                stage.setScene(new TodaysScheduleView(stage).getScene());
                break;
            case 2:
                stage.setScene(new AppointmentsView(stage).getScene());
                break;
            case 3:
                stage.setScene(new PatientDetailsView(stage).getScene());
                break;
            case 4:
                stage.setScene(new MedicalReportsView(stage).getScene());
                break;
            case 5:
                stage.setScene(new AvailabilityScheduleView(stage).getScene());
                break;
            case 6:
                stage.setScene(new DoctorProfileView(stage).getScene());
                break;
            case 7:
                stage.setScene(new AIHealthAssistantView(stage).getScene());
                break;
            default:
                break;
        }
    }

    /** Creates top bar breadcrumb and search bar. */
    private BorderPane createTopBar() {
        BorderPane topBar = new BorderPane();

        Label breadcrumb = new Label("Dashboard");
        breadcrumb.getStyleClass().add("breadcrumb");

        HBox searchBar = new HBox(8);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.setPadding(new Insets(6, 12, 6, 12));
        searchBar.getStyleClass().add("search-bar");

        ImageView searchIcon = createImageView("/images/icons/ic_search.png", 16, 16);
        TextField searchField = new TextField();
        searchField.setPromptText("Search patients...");
        searchField.getStyleClass().add("search-field");

        if (searchIcon != null) {
            searchBar.getChildren().add(searchIcon);
        }
        searchBar.getChildren().add(searchField);

        topBar.setLeft(breadcrumb);
        topBar.setRight(searchBar);
        return topBar;
    }

    /** Creates the blue welcome banner. */
    private HBox createWelcomeBanner() {
        HBox banner = new HBox(20);
        banner.getStyleClass().add("welcome-banner");
        banner.setPadding(new Insets(28));
        banner.setAlignment(Pos.CENTER_LEFT);

        VBox textSection = new VBox(12);
        textSection.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textSection, Priority.ALWAYS);

        Label greeting = new Label("Good morning, Dr. Sarah.");
        greeting.getStyleClass().add("welcome-greeting");

        Label summary = new Label("You have 12 appointments scheduled for today. The AI assistant has flagged 2\npatient reports requiring your urgent review.");
        summary.getStyleClass().add("welcome-summary");

        HBox buttonsSection = new HBox(12);
        Button startConsultBtn = new Button("Start Consultations");
        startConsultBtn.getStyleClass().add("btn-primary");
        Button viewScheduleBtn = new Button("View Schedule");
        viewScheduleBtn.getStyleClass().add("btn-secondary");

        startConsultBtn.setOnAction(event -> handleSidebarTabClick(2)); // Navigates to Appointments
        viewScheduleBtn.setOnAction(event -> handleSidebarTabClick(1));  // Navigates to Today's Schedule

        buttonsSection.getChildren().addAll(startConsultBtn, viewScheduleBtn);
        textSection.getChildren().addAll(greeting, summary, buttonsSection);

        ImageView illustration = createImageView("/images/doctor/doctor_welcome.png", 220, 140);
        banner.getChildren().add(textSection);
        if (illustration != null) {
            banner.getChildren().add(illustration);
        }

        return banner;
    }

    /** Creates stat cards row. */
    private HBox createStatCardsRow() {
        HBox row = new HBox(16);
        row.getChildren().addAll(
            createStatCard("TOTAL PATIENTS", "1,432", "ic_total_patients", "+12% vs last month", false),
            createStatCard("TODAY'S APPTS", "12", "ic_today_appts", "4 completed, 8 remaining", true),
            createStatCard("PATIENT GROWTH", "+84", "ic_growth", "mock_growth_chart.png", false),
            createStatCard("REVENUE (MTD)", "$12.4k", "ic_revenue", "mock_revenue_chart.png", false)
        );
        return row;
    }

    /** Helper function to create stat card. */
    private VBox createStatCard(String title, String value, String iconName, String detail, boolean showProgressBar) {
        VBox card = new VBox(12);
        card.getStyleClass().add("stat-card");
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setPadding(new Insets(20));

        HBox cardHeader = new HBox(10);
        cardHeader.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ImageView icon = createImageView("/images/icons/cards/" + iconName + ".png", 20, 20);

        cardHeader.getChildren().addAll(titleLabel, spacer);
        if (icon != null) {
            cardHeader.getChildren().add(icon);
        }

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");

        VBox footer = new VBox(6);
        if (showProgressBar) {
            Label pbDetail = new Label(detail);
            pbDetail.getStyleClass().add("stat-detail");

            ProgressBar pb = new ProgressBar(0.33);
            pb.getStyleClass().add("stat-progress");
            pb.setMaxWidth(Double.MAX_VALUE);

            footer.getChildren().addAll(pbDetail, pb);
        } else if (detail.endsWith(".png")) {
            ImageView chart = createImageView("/images/mocks/" + detail, 180, 40);
            if (chart != null) {
                footer.getChildren().add(chart);
            } else {
                Label detailLabel = new Label(detail);
                detailLabel.getStyleClass().add("stat-detail");
                footer.getChildren().add(detailLabel);
            }
        } else {
            Label detailLabel = new Label(detail);
            detailLabel.getStyleClass().add("stat-detail");
            footer.getChildren().add(detailLabel);
        }

        card.getChildren().addAll(cardHeader, valueLabel, footer);
        return card;
    }

    /** Creates Appointments and AI Insights row. */
    private HBox createAppointmentsInsightsRow() {
        HBox row = new HBox(16);

        // Appointments Left Box
        VBox appointmentsCard = new VBox(16);
        appointmentsCard.getStyleClass().add("app-card");
        HBox.setHgrow(appointmentsCard, Priority.ALWAYS);
        appointmentsCard.setPadding(new Insets(20));

        HBox apptsHeader = new HBox(10);
        apptsHeader.setAlignment(Pos.CENTER_LEFT);

        Label apptsTitle = new Label("Today's Appointments");
        apptsTitle.getStyleClass().add("app-card-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label viewCalendar = new Label("View Full Calendar");
        viewCalendar.getStyleClass().add("app-card-link");
        viewCalendar.setOnMouseClicked(e -> handleSidebarTabClick(1)); // TodaysScheduleView

        apptsHeader.getChildren().addAll(apptsTitle, spacer, viewCalendar);

        VBox apptsList = new VBox(12);
        apptsList.getChildren().addAll(
            createApptEntry("09:00 AM", "Michael Chang", "General Checkup", "Completed"),
            createApptEntry("10:30 AM", "Elena Rodriguez", "Follow-up: Hypertension", "In Progress"),
            createApptEntry("11:15 AM", "David Kim", "New Patient: Consultation", "Waiting")
        );

        appointmentsCard.getChildren().addAll(apptsHeader, apptsList);

        // AI Insights Right Box
        VBox aiInsightsCard = new VBox(16);
        aiInsightsCard.getStyleClass().add("ai-card");
        aiInsightsCard.setMinWidth(340);
        aiInsightsCard.setPrefWidth(340);
        aiInsightsCard.setPadding(new Insets(20));

        HBox aiHeader = new HBox(10);
        aiHeader.setAlignment(Pos.CENTER_LEFT);

        ImageView aiIcon = createImageView("/images/doctor/ai_assistant.png", 20, 20);
        Label aiTitle = new Label("AI Insights");
        aiTitle.getStyleClass().add("ai-card-title");

        if (aiIcon != null) {
            aiHeader.getChildren().add(aiIcon);
        }
        aiHeader.getChildren().add(aiTitle);

        VBox aiInsightsList = new VBox(12);
        aiInsightsList.getChildren().addAll(
            createAiInsightEntry("Action Required", "Lab results for Sarah Connor show elevated LDL. Recommend adjusting statin dosage.", "Review Labs", Color.RED),
            createAiInsightEntry("Clinical Note", "Upcoming patient David Kim has a documented allergy to Penicillin.", "", Color.BLUE)
        );

        aiInsightsCard.getChildren().addAll(aiHeader, aiInsightsList);

        row.getChildren().addAll(appointmentsCard, aiInsightsCard);
        return row;
    }

    /** Creates appointment entry item. */
    private GridPane createApptEntry(String time, String patientName, String purpose, String status) {
        GridPane entry = new GridPane();
        entry.getStyleClass().add("appt-entry");
        if ("In Progress".equals(status)) {
            entry.getStyleClass().add("appt-entry-highlight");
        }
        entry.setHgap(16);
        entry.setPadding(new Insets(12));
        entry.setAlignment(Pos.CENTER_LEFT);

        Label timeLabel = new Label(time);
        timeLabel.getStyleClass().add("appt-time");

        Circle timeline = new Circle(4);
        if ("In Progress".equals(status)) {
            timeline.setFill(Color.web("#2563EB"));
        } else if ("Completed".equals(status)) {
            timeline.setFill(Color.GRAY);
        } else {
            timeline.setFill(Color.WHITE);
            timeline.setStroke(Color.GRAY);
        }

        VBox patientDetails = new VBox(2);
        Label nameLabel = new Label(patientName);
        nameLabel.getStyleClass().add("appt-name");
        Label purposeLabel = new Label(purpose);
        purposeLabel.getStyleClass().add("appt-purpose");
        patientDetails.getChildren().addAll(nameLabel, purposeLabel);

        HBox statusPill = createPill(status);

        entry.add(timeLabel, 0, 0);
        entry.add(timeline, 1, 0);
        entry.add(patientDetails, 2, 0);
        entry.add(statusPill, 3, 0);

        if ("In Progress".equals(status)) {
            ImageView actionIcon = createImageView("/images/icons/ic_external_link.png", 16, 16);
            if (actionIcon != null) {
                entry.add(actionIcon, 4, 0);
            }
        }

        ColumnConstraints col1 = new ColumnConstraints(80);
        ColumnConstraints col2 = new ColumnConstraints(20);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setHgrow(Priority.ALWAYS);
        ColumnConstraints col4 = new ColumnConstraints(110);

        entry.getColumnConstraints().addAll(col1, col2, col3, col4);

        entry.setOnMouseClicked(event -> handleSidebarTabClick(3)); // Patient Details
        return entry;
    }

    /** Creates AI insight card entry. */
    private VBox createAiInsightEntry(String alert, String desc, String linkText, Color accentColor) {
        VBox insight = new VBox(8);
        insight.getStyleClass().add("ai-entry");
        insight.setPadding(new Insets(14));

        if (accentColor == Color.RED) {
            insight.getStyleClass().add("ai-entry-alert");
        } else {
            insight.getStyleClass().add("ai-entry-info");
        }

        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);

        String iconPath = (accentColor == Color.RED) ? "/images/icons/ic_alert.png" : "/images/icons/ic_info.png";
        ImageView statusIcon = createImageView(iconPath, 16, 16);

        Label alertLabel = new Label(alert);
        alertLabel.getStyleClass().add("ai-entry-label");

        if (statusIcon != null) {
            header.getChildren().add(statusIcon);
        }
        header.getChildren().add(alertLabel);

        Label descLabel = new Label(desc);
        descLabel.getStyleClass().add("ai-entry-desc");
        descLabel.setWrapText(true);

        insight.getChildren().addAll(header, descLabel);

        if (!linkText.isEmpty()) {
            Label actionLink = new Label(linkText);
            actionLink.getStyleClass().add("ai-entry-link");
            actionLink.setOnMouseClicked(event -> handleSidebarTabClick(4)); // Medical Reports
            insight.getChildren().add(actionLink);
        }

        return insight;
    }

    /** Creates Recent Activity table container. */
    private VBox createRecentActivityTable() {
        VBox tableContainer = new VBox(16);
        tableContainer.getStyleClass().add("activity-container");
        tableContainer.setPadding(new Insets(20));

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Recent Patient Activity");
        title.getStyleClass().add("activity-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label viewAll = new Label("View All Activity");
        viewAll.getStyleClass().add("activity-link");
        viewAll.setOnMouseClicked(e -> handleSidebarTabClick(3)); // Patient Details

        header.getChildren().addAll(title, spacer, viewAll);

        GridPane tableHeader = new GridPane();
        tableHeader.getStyleClass().add("table-header");
        tableHeader.setHgap(16);
        tableHeader.setPadding(new Insets(0, 12, 8, 12));

        String[] cols = {"PATIENT", "STATUS", "ACTION", "TIME"};
        for (int i = 0; i < cols.length; i++) {
            Label colLabel = new Label(cols[i]);
            colLabel.getStyleClass().add("table-col-header");
            tableHeader.add(colLabel, i, 0);
        }

        setupTableColumns(tableHeader);

        VBox activityList = new VBox(8);
        activityList.getChildren().addAll(
            createActivityEntry("James Smith", "JS", "Check-in", "Arrived for annual physical", "12 mins ago"),
            createActivityEntry("Maria Lopez", "ML", "Lab Results", "Blood panel results uploaded", "45 mins ago"),
            createActivityEntry("Robert Brown", "RB", "Discharged", "Post-op follow-up completed", "1 hour ago")
        );

        tableContainer.getChildren().addAll(header, tableHeader, activityList);
        return tableContainer;
    }

    /** Creates individual activity row. */
    private GridPane createActivityEntry(String name, String initials, String status, String action, String time) {
        GridPane entry = new GridPane();
        entry.getStyleClass().add("table-row");
        entry.setHgap(16);
        entry.setPadding(new Insets(12));
        entry.setAlignment(Pos.CENTER_LEFT);

        HBox patientCell = new HBox(10);
        patientCell.setAlignment(Pos.CENTER_LEFT);

        Label avatar = new Label(initials);
        avatar.getStyleClass().add("table-avatar");

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("table-patient-name");

        patientCell.getChildren().addAll(avatar, nameLabel);

        HBox statusCell = createPill(status);

        Label actionLabel = new Label(action);
        actionLabel.getStyleClass().add("table-action");

        Label timeLabel = new Label(time);
        timeLabel.getStyleClass().add("table-time");

        entry.add(patientCell, 0, 0);
        entry.add(statusCell, 1, 0);
        entry.add(actionLabel, 2, 0);
        entry.add(timeLabel, 3, 0);

        setupTableColumns(entry);

        entry.setOnMouseClicked(event -> handleSidebarTabClick(3)); // Patient Details
        return entry;
    }

    /** Configures responsive column constraints for activity table alignment. */
    private void setupTableColumns(GridPane gridPane) {
        ColumnConstraints col1 = new ColumnConstraints(220);
        ColumnConstraints col2 = new ColumnConstraints(130);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setHgrow(Priority.ALWAYS);
        ColumnConstraints col4 = new ColumnConstraints(140);

        gridPane.getColumnConstraints().addAll(col1, col2, col3, col4);
    }

    /** Helper function to create status pill. */
    private HBox createPill(String status) {
        HBox pill = new HBox();
        pill.setAlignment(Pos.CENTER);
        pill.getStyleClass().add("status-pill");
        pill.setPadding(new Insets(4, 10, 4, 10));

        Label statusLabel = new Label(status);

        if ("Completed".equals(status) || "Check-in".equals(status)) {
            pill.getStyleClass().add("pill-completed");
            statusLabel.setTextFill(Color.web("#059669"));
        } else if ("In Progress".equals(status) || "Waiting".equals(status)) {
            pill.getStyleClass().add("pill-inprogress");
            statusLabel.setTextFill(Color.web("#2563EB"));
        } else {
            pill.getStyleClass().add("pill-info");
            statusLabel.setTextFill(Color.web("#6B7280"));
        }

        pill.getChildren().add(statusLabel);
        return pill;
    }

    /** Helper method for loading images without throwing errors if files are missing. */
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