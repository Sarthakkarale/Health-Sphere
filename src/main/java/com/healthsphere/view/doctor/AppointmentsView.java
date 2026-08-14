package com.healthsphere.view.doctor;

import com.healthsphere.util.Navigation;
import com.healthsphere.util.ResourceImage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * AppointmentsView represents the appointment management screen for Doctors in Health-Sphere.
 * Fully interactive filter tabs, navigation, and appointment detail actions.
 */
public class AppointmentsView {

    private final Stage stage;
    private final Scene scene;

    // Container for the cards so we can clear/re-populate when filtering
    private HBox cardsGrid;
    
    // Store master list of appointment model data
    private final List<AppointmentData> appointmentList = new ArrayList<>();

    public AppointmentsView(Stage stage) {
        this.stage = stage;
        initSampleData(); // Load appointment data
        this.scene = createScene();
    }

    public Scene getScene() {
        return this.scene;
    }

    /** Helper Data Class to store appointment attributes for filtering */
    private static class AppointmentData {
        String aptId;
        String status;        // "Confirmed", "In Progress", "Pending", "Completed", "Cancelled"
        String filterCategory; // "Upcoming", "Completed", "Cancelled"
        String statusClass;
        String avatarPath;
        String initials;
        String patientName;
        String consultationType;
        String typeIconPath;
        String dateTime;
        String notes;
        boolean isHighlighted;

        public AppointmentData(String aptId, String status, String filterCategory, String statusClass,
                               String avatarPath, String initials, String patientName,
                               String consultationType, String typeIconPath, String dateTime,
                               String notes, boolean isHighlighted) {
            this.aptId = aptId;
            this.status = status;
            this.filterCategory = filterCategory;
            this.statusClass = statusClass;
            this.avatarPath = avatarPath;
            this.initials = initials;
            this.patientName = patientName;
            this.consultationType = consultationType;
            this.typeIconPath = typeIconPath;
            this.dateTime = dateTime;
            this.notes = notes;
            this.isHighlighted = isHighlighted;
        }
    }

    /** Initialize mock appointment dataset */
    private void initSampleData() {
        appointmentList.add(new AppointmentData(
                "#APT-1024", "Confirmed", "Upcoming", "pill-status-confirmed",
                "/images/mocks/robert_chen.png", null, "Robert Chen",
                "Video Consultation", "/images/icons/ic_video.png", "Oct 26, 2023 | 10:30 AM",
                "Persistent mild headache for 3 days, accompanied by slight...", false
        ));

        appointmentList.add(new AppointmentData(
                "#APT-1025", "In Progress", "Upcoming", "pill-status-inprogress",
                null, "ES", "Elena Smith",
                "In-Person", "/images/icons/ic_hospital.png", "Oct 26, 2023 | 11:15 AM",
                "Annual physical checkup. Bloodwork results review.", true
        ));

        appointmentList.add(new AppointmentData(
                "#APT-1026", "Pending", "Upcoming", "pill-status-pending",
                "/images/mocks/margaret_johnson.png", null, "Margaret Johnson",
                "Follow-up", "/images/icons/ic_followup.png", "Oct 26, 2023 | 01:00 PM",
                "Post-surgery recovery check. Joint mobility assessment.", false
        ));

        appointmentList.add(new AppointmentData(
                "#APT-1020", "Completed", "Completed", "pill-status-completed",
                null, "SJ", "Sarah Jenkins",
                "General Checkup", "/images/icons/ic_hospital.png", "Oct 25, 2023 | 09:00 AM",
                "Routine health inspection completed. Vitals normal.", false
        ));

        appointmentList.add(new AppointmentData(
                "#APT-1018", "Cancelled", "Cancelled", "pill-status-cancelled",
                null, "MC", "Michael Chang",
                "Consultation", "/images/icons/ic_video.png", "Oct 24, 2023 | 03:30 PM",
                "Cancelled by patient due to scheduling conflict.", false
        ));
    }

    private Scene createScene() {
        BorderPane mainRoot = new BorderPane();
        mainRoot.getStyleClass().add("root-pane");

        // --- Sidebar (Left Navigation) ---
        VBox sidebar = createSidebar();
        mainRoot.setLeft(sidebar);

        // --- Main Content Area ---
        VBox contentArea = new VBox(20);
        contentArea.setPadding(new Insets(20, 30, 20, 30));
        contentArea.getStyleClass().add("content-area");

        // Top Header
        HBox topHeader = createTopHeader();
        contentArea.getChildren().add(topHeader);

        // Title and Action Header
        BorderPane titleSection = createTitleSection();
        contentArea.getChildren().add(titleSection);

        // Filter Bar (Interactive Tabs, Date Picker, Filter Button)
        HBox filterBar = createFilterBar();
        contentArea.getChildren().add(filterBar);

        // Appointments Grid
        cardsGrid = new HBox(20);
        renderFilteredAppointments("All"); // Default render all cards
        contentArea.getChildren().add(cardsGrid);

        // ScrollPane Container
        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("content-scrollpane");
        mainRoot.setCenter(scrollPane);

        Scene appointmentsScene = new Scene(mainRoot, stage.getWidth(), stage.getHeight());
        appointmentsScene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/css/appointments.css")).toExternalForm());

        return appointmentsScene;
    }

    /** Creates Filter Bar with dynamic click handling for tabs */
    private HBox createFilterBar() {
        HBox bar = new HBox(15);
        bar.getStyleClass().add("filter-container-card");
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(12, 16, 12, 16));

        HBox filterTabs = new HBox(10);
        String[] filters = {"All", "Upcoming", "Completed", "Cancelled"};
        List<Button> tabButtons = new ArrayList<>();

        for (String filterName : filters) {
            Button filterBtn = new Button(filterName);
            filterBtn.setMinWidth(90);
            filterBtn.setAlignment(Pos.CENTER);

            if (filterName.equals("All")) {
                filterBtn.getStyleClass().add("filter-pill-active");
            } else {
                filterBtn.getStyleClass().add("filter-pill");
            }

            tabButtons.add(filterBtn);

            // TAB CLICK EVENT LISTENER
            filterBtn.setOnAction(e -> {
                // Reset styling for all tabs
                for (Button btn : tabButtons) {
                    btn.getStyleClass().remove("filter-pill-active");
                    if (!btn.getStyleClass().contains("filter-pill")) {
                        btn.getStyleClass().add("filter-pill");
                    }
                }

                // Set active styling on clicked tab
                filterBtn.getStyleClass().remove("filter-pill");
                filterBtn.getStyleClass().add("filter-pill-active");

                // Filter appointment cards dynamically
                renderFilteredAppointments(filterName);
            });

            filterTabs.getChildren().add(filterBtn);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        DatePicker datePicker = new DatePicker();
        datePicker.getStyleClass().add("custom-date-picker");
        datePicker.setPromptText("10/26/2023");

        Button filterIconBtn = new Button();
        filterIconBtn.getStyleClass().add("btn-icon-filter");
        ImageView filterIcon = new ImageView(ResourceImage.load("/images/icons/ic_filter.png"));
        filterIcon.setFitWidth(16); filterIcon.setFitHeight(16);
        filterIconBtn.setGraphic(filterIcon);

        bar.getChildren().addAll(filterTabs, spacer, datePicker, filterIconBtn);
        return bar;
    }

    /** Renders/Filters appointment cards based on selected filter */
    private void renderFilteredAppointments(String category) {
        cardsGrid.getChildren().clear();

        for (AppointmentData data : appointmentList) {
            if (category.equals("All") || data.filterCategory.equalsIgnoreCase(category)) {
                VBox card = createAppointmentCard(data);
                cardsGrid.getChildren().add(card);
            }
        }

        if (cardsGrid.getChildren().isEmpty()) {
            Label emptyLabel = new Label("No appointments found for '" + category + "'.");
            emptyLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 14px; -fx-padding: 20px 0;");
            cardsGrid.getChildren().add(emptyLabel);
        }
    }

    /** Creates dynamic appointment card from model */
    private VBox createAppointmentCard(AppointmentData data) {
        VBox card = new VBox(14);
        card.setMinWidth(320);
        card.setMaxWidth(340);
        card.setPadding(new Insets(18));

        card.getStyleClass().add(data.isHighlighted ? "appointment-card-active" : "appointment-card");

        // Top Row: ID + Status Pill
        BorderPane topRow = new BorderPane();
        Label idLabel = new Label(data.aptId);
        idLabel.getStyleClass().add("apt-id-label");

        Label statusPill = new Label("• " + data.status);
        statusPill.getStyleClass().addAll("pill-status", data.statusClass);

        topRow.setLeft(idLabel);
        topRow.setRight(statusPill);

        // Profile Row
        HBox profileRow = new HBox(12);
        profileRow.setAlignment(Pos.CENTER_LEFT);

        Node avatarNode;
        if (data.avatarPath != null) {
            ImageView img = new ImageView(ResourceImage.load(data.avatarPath));
            img.setFitWidth(42); img.setFitHeight(42);
            Circle clip = new Circle(21, 21, 21);
            img.setClip(clip);
            avatarNode = img;
        } else {
            StackPane initialsAvatar = new StackPane();
            initialsAvatar.getStyleClass().add("initials-avatar-large");
            Label initialsText = new Label(data.initials);
            initialsText.getStyleClass().add("initials-text-large");
            initialsAvatar.getChildren().add(initialsText);
            avatarNode = initialsAvatar;
        }

        VBox nameBox = new VBox(2);
        Label nameLbl = new Label(data.patientName);
        nameLbl.getStyleClass().add("card-patient-name");

        HBox typeBox = new HBox(5);
        typeBox.setAlignment(Pos.CENTER_LEFT);

        ImageView typeIcon = new ImageView(ResourceImage.load(data.typeIconPath));
        typeIcon.setFitWidth(14); typeIcon.setFitHeight(14);

        Label typeLbl = new Label(data.consultationType);
        typeLbl.getStyleClass().add("card-consult-type");
        typeBox.getChildren().addAll(typeIcon, typeLbl);

        nameBox.getChildren().addAll(nameLbl, typeBox);
        profileRow.getChildren().addAll(avatarNode, nameBox);

        // Date Row
        HBox timeRow = new HBox(8);
        timeRow.setAlignment(Pos.CENTER_LEFT);
        ImageView clockIcon = new ImageView(ResourceImage.load("/images/icons/ic_clock.png"));
        clockIcon.setFitWidth(14); clockIcon.setFitHeight(14);
        Label timeLbl = new Label(data.dateTime);
        timeLbl.getStyleClass().add("card-time-text");
        timeRow.getChildren().addAll(clockIcon, timeLbl);

        // Notes Row
        HBox notesRow = new HBox(8);
        notesRow.setAlignment(Pos.TOP_LEFT);
        ImageView notesIcon = new ImageView(ResourceImage.load("/images/icons/ic_stethoscope.png"));
        notesIcon.setFitWidth(14); notesIcon.setFitHeight(14);
        Label notesLbl = new Label(data.notes);
        notesLbl.setWrapText(true);
        notesLbl.getStyleClass().add("card-notes-text");
        notesRow.getChildren().addAll(notesIcon, notesLbl);

        // Actions Row
        HBox actionRow = new HBox(10);
        actionRow.setPadding(new Insets(10, 0, 0, 0));

        Button rescheduleBtn = new Button("Reschedule");
        rescheduleBtn.getStyleClass().add("btn-card-reschedule");
        HBox.setHgrow(rescheduleBtn, Priority.ALWAYS);
        rescheduleBtn.setMaxWidth(Double.MAX_VALUE);

        Button detailsBtn = new Button("View Details");
        detailsBtn.getStyleClass().add("btn-card-details");
        HBox.setHgrow(detailsBtn, Priority.ALWAYS);
        detailsBtn.setMaxWidth(Double.MAX_VALUE);
        detailsBtn.setOnAction(e -> Navigation.goTo(stage, () -> new PatientDetailsView(stage).getScene()));

        actionRow.getChildren().addAll(rescheduleBtn, detailsBtn);

        card.getChildren().addAll(topRow, profileRow, timeRow, notesRow, actionRow);
        return card;
    }

    /** Creates Sidebar Navigation */
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPadding(new Insets(30, 15, 30, 15));
        sidebar.getStyleClass().add("sidebar");
        sidebar.setMinWidth(250);

        HBox logoSection = new HBox(10);
        logoSection.setPadding(new Insets(0, 0, 30, 0));
        logoSection.setAlignment(Pos.CENTER_LEFT);

        StackPane logoIconBox = new StackPane();
        logoIconBox.getStyleClass().add("logo-icon-box");
        Label logoAbbr = new Label("HS");
        logoAbbr.getStyleClass().add("logo-icon-text");
        logoIconBox.getChildren().add(logoAbbr);

        VBox logoText = new VBox(0);
        Label appName = new Label("Health-Sphere");
        appName.getStyleClass().add("logo-name");
        Label doctorSubtext = new Label("Doctor Dashboard");
        doctorSubtext.getStyleClass().add("logo-subtext");
        logoText.getChildren().addAll(appName, doctorSubtext);
        logoSection.getChildren().addAll(logoIconBox, logoText);

        VBox navItems = new VBox(8);
        String[] tabs = {
            "Dashboard", "Today's Schedule", "Appointments", "Patient Details",
            "Medical Reports & Prescription", "Availability & Schedule", "Doctor Profile", "AI Health Assistant"
        };
        String[] icons = {
            "ic_dashboard", "ic_schedule", "ic_appointments", "ic_patient",
            "ic_reports", "ic_availability", "ic_profile", "ic_ai"
        };

        for (int i = 0; i < tabs.length; i++) {
            HBox navTab = new HBox(15);
            navTab.getStyleClass().add("nav-tab");
            navTab.setAlignment(Pos.CENTER_LEFT);

            if (i == 2) {
                navTab.getStyleClass().add("nav-tab-active");
            }

            ImageView icon = new ImageView(ResourceImage.load("/images/icons/" + icons[i] + ".png"));
            icon.setFitWidth(18); icon.setFitHeight(18);

            Label tabLabel = new Label(tabs[i]);
            tabLabel.getStyleClass().add("nav-text");

            navTab.getChildren().addAll(icon, tabLabel);
            navItems.getChildren().add(navTab);

            final int index = i;
            navTab.setOnMouseClicked(e -> handleSidebarTabClick(index));
        }

        VBox footer = new VBox(15);
        footer.setAlignment(Pos.BOTTOM_CENTER);
        VBox.setVgrow(footer, Priority.ALWAYS);

        HBox doctorProfile = new HBox(12);
        doctorProfile.getStyleClass().add("sidebar-profile");
        doctorProfile.setAlignment(Pos.CENTER_LEFT);

        ImageView profilePhoto = new ImageView(ResourceImage.load("/images/doctor/portrait-3d-male-doctor.png"));
        profilePhoto.setFitWidth(28); profilePhoto.setFitHeight(28);
        Circle profileClip = new Circle(14, 14, 14);
        profilePhoto.setClip(profileClip);

        VBox profileText = new VBox(0);
        Label doctorName = new Label("Dr. Sarah");
        doctorName.getStyleClass().add("sidebar-profile-name");
        profileText.getChildren().add(doctorName);
        doctorProfile.getChildren().addAll(profilePhoto, profileText);

        doctorProfile.setOnMouseClicked(e -> Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene()));

        HBox logout = new HBox(15);
        logout.getStyleClass().add("nav-tab");
        logout.setAlignment(Pos.CENTER_LEFT);

        ImageView logoutIcon = new ImageView(ResourceImage.load("/images/icons/ic_logout.png"));
        logoutIcon.setFitWidth(18); logoutIcon.setFitHeight(18);

        Label logoutLabel = new Label("Logout");
        logoutLabel.getStyleClass().add("nav-text-logout");
        logout.getChildren().addAll(logoutIcon, logoutLabel);

        footer.getChildren().addAll(doctorProfile, logout);
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
            case 6: Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene()); break;
            case 7: Navigation.goTo(stage, () -> new AIHealthAssistantView(stage).getScene()); break;
            default: break;
        }
    }

    private HBox createTopHeader() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_RIGHT);

        HBox searchField = new HBox(8);
        searchField.getStyleClass().add("search-input-box");
        searchField.setAlignment(Pos.CENTER_LEFT);

        ImageView searchIcon = new ImageView(ResourceImage.load("/images/icons/ic_search.png"));
        searchIcon.setFitWidth(16); searchIcon.setFitHeight(16);

        TextField searchInput = new TextField();
        searchInput.setPromptText("Search patients or IDs...");
        searchInput.getStyleClass().add("search-text-field");
        searchField.getChildren().addAll(searchIcon, searchInput);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox rightIcons = new HBox(18);
        rightIcons.setAlignment(Pos.CENTER_RIGHT);

        StackPane notificationBox = new StackPane();
        ImageView bellIcon = new ImageView(ResourceImage.load("/images/icons/ic_bell.png"));
        bellIcon.setFitWidth(18); bellIcon.setFitHeight(18);
        Circle badge = new Circle(4, Color.RED);
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        notificationBox.getChildren().addAll(bellIcon, badge);
        notificationBox.getStyleClass().add("clickable-icon");

        ImageView userAvatar = new ImageView(ResourceImage.load("/images/doctor/portrait-3d-male-doctor.png"));
        userAvatar.setFitWidth(32); userAvatar.setFitHeight(32);
        Circle clip = new Circle(16, 16, 16);
        userAvatar.setClip(clip);
        userAvatar.getStyleClass().add("clickable-icon");
        userAvatar.setOnMouseClicked(e -> Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene()));

        rightIcons.getChildren().addAll(notificationBox, userAvatar);
        topBar.getChildren().addAll(searchField, spacer, rightIcons);
        return topBar;
    }

    private BorderPane createTitleSection() {
        BorderPane section = new BorderPane();

        VBox titleBox = new VBox(2);
        Label mainTitle = new Label("Appointments");
        mainTitle.getStyleClass().add("page-title");
        Label subTitle = new Label("24 Total Appointments");
        subTitle.getStyleClass().add("page-subtitle");
        titleBox.getChildren().addAll(mainTitle, subTitle);

        Button newApptBtn = new Button("+ New Appointment");
        newApptBtn.getStyleClass().add("btn-primary-action");

        section.setLeft(titleBox);
        section.setRight(newApptBtn);
        return section;
    }
}