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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * AppointmentsView represents the appointment management screen for Doctors in Health-Sphere.
 * Fully interactive filter tabs, navigation, horizontal and vertical scrolling cards container,
 * with a static left sidebar matching DoctorDashboardView.
 */
public class AppointmentsView {

    private final Stage stage;
    private final Scene scene;

    // FlowPane inside ScrollPane to handle horizontal flow & vertical multi-row card display
    private FlowPane cardsGrid;
    
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

        // --- Sidebar (LEFT - Static matching Dashboard) ---
        VBox sidebar = createSidebar();
        mainRoot.setLeft(sidebar);

        // --- Main Content Area ---
        VBox contentArea = new VBox(20);
        contentArea.setPadding(new Insets(20, 30, 30, 30));
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

        // Appointments Grid Container (Horizontal & Vertical Layout wrapped in ScrollPane)
        cardsGrid = new FlowPane(20, 20);
        cardsGrid.setAlignment(Pos.TOP_LEFT);

        ScrollPane cardsScrollPane = new ScrollPane(cardsGrid);
        cardsScrollPane.setFitToWidth(true);
        cardsScrollPane.setPannable(true);
        cardsScrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        renderFilteredAppointments("All"); // Default render all cards
        contentArea.getChildren().add(cardsScrollPane);

        // ScrollPane wrapping ONLY contentArea so sidebar remains fixed
        ScrollPane contentScrollPane = new ScrollPane(contentArea);
        contentScrollPane.setFitToWidth(true);
        contentScrollPane.setFitToHeight(true);
        contentScrollPane.getStyleClass().add("content-scrollpane");

        mainRoot.setCenter(contentScrollPane);

        Scene appointmentsScene = new Scene(mainRoot, stage.getWidth(), stage.getHeight());
        try {
            appointmentsScene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/css/appointments.css")).toExternalForm());
        } catch (Exception ignored) {}

        return appointmentsScene;
    }

    /**
     * Sidebar navigation styled strictly like Dashboard with dark navy background,
     * blue active highlight pill, and proper doctor profile footer card.
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
        ImageView logoIcon = createImageView("/images/icons/ic_shield.png", 20, 20);
        if (logoIcon != null) {
            logoIconBox.getChildren().add(logoIcon);
        }

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
            "Dashboard", "Today's Schedule", "Appointments", "Patient Details",
            "Medical Reports & Prescription", "Availability & Schedule", "Doctor Profile", "AI Health Assistant"
        };
        String[] icons = {
            "ic_dashboard", "ic_schedule", "ic_appointments", "ic_patient",
            "ic_reports", "ic_availability", "ic_profile", "ic_ai"
        };

        for (int i = 0; i < tabs.length; i++) {
            final int tabIndex = i;
            HBox navTab = new HBox(12);
            navTab.setAlignment(Pos.CENTER_LEFT);
            navTab.setPadding(new Insets(10, 14, 10, 14));
            navTab.getStyleClass().add("nav-tab");

            ImageView icon = createImageView("/images/icons/" + icons[i] + ".png", 18, 18);
            Label tabLabel = new Label(tabs[i]);
            tabLabel.getStyleClass().add("nav-text");

            if (i == 2) { // Active Tab: Appointments
                navTab.getStyleClass().add("nav-tab-active");
                navTab.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 8px;");
                tabLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 14px;");
            } else {
                navTab.setStyle("-fx-background-color: transparent; -fx-background-radius: 8px;");
                tabLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 14px;");
            }

            if (icon != null) {
                navTab.getChildren().add(icon);
            }
            navTab.getChildren().add(tabLabel);

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

        ImageView profileAvatar = createImageView("/images/doctor/portrait-3d-male-doctor.png", 36, 36);
        if (profileAvatar != null) {
            Circle profileClip = new Circle(18, 18, 18);
            profileAvatar.setClip(profileClip);
        }

        VBox profileTexts = new VBox(2);
        Label profSubText = new Label("Doctor Profile");
        profSubText.setStyle("-fx-text-fill: #64748B; -fx-font-size: 11px;");
        Label profName = new Label("Dr. Sarah");
        profName.getStyleClass().add("sidebar-profile-name");
        profName.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 13px;");

        profileTexts.getChildren().addAll(profSubText, profName);
        if (profileAvatar != null) {
            sidebarProfile.getChildren().add(profileAvatar);
        }
        sidebarProfile.getChildren().add(profileTexts);
        sidebarProfile.setOnMouseClicked(e -> Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene()));

        // Logout Tab
        HBox logoutTab = new HBox(12);
        logoutTab.setAlignment(Pos.CENTER_LEFT);
        logoutTab.setPadding(new Insets(10, 14, 10, 14));
        logoutTab.getStyleClass().add("nav-tab");
        logoutTab.setStyle("-fx-cursor: hand;");

        ImageView logoutIcon = createImageView("/images/icons/ic_logout.png", 18, 18);
        Label logoutLabel = new Label("Logout");
        logoutLabel.getStyleClass().add("nav-text");
        logoutLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 14px;");

        if (logoutIcon != null) {
            logoutTab.getChildren().add(logoutIcon);
        }
        logoutTab.getChildren().add(logoutLabel);
        logoutTab.setOnMouseClicked(event -> showInformationAlert("Logout", "Logged out successfully."));

        footer.getChildren().addAll(sidebarProfile, logoutTab);
        sidebar.getChildren().addAll(logoSection, navItems, spacer, footer);
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
        ImageView filterIcon = createImageView("/images/icons/ic_filter.png", 16, 16);
        if (filterIcon != null) {
            filterIconBtn.setGraphic(filterIcon);
        }

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
            ImageView img = createImageView(data.avatarPath, 42, 42);
            if (img != null) {
                Circle clip = new Circle(21, 21, 21);
                img.setClip(clip);
                avatarNode = img;
            } else {
                avatarNode = createInitialsAvatar(data.initials);
            }
        } else {
            avatarNode = createInitialsAvatar(data.initials);
        }

        VBox nameBox = new VBox(2);
        Label nameLbl = new Label(data.patientName);
        nameLbl.getStyleClass().add("card-patient-name");

        HBox typeBox = new HBox(5);
        typeBox.setAlignment(Pos.CENTER_LEFT);

        ImageView typeIcon = createImageView(data.typeIconPath, 14, 14);

        Label typeLbl = new Label(data.consultationType);
        typeLbl.getStyleClass().add("card-consult-type");
        if (typeIcon != null) {
            typeBox.getChildren().add(typeIcon);
        }
        typeBox.getChildren().add(typeLbl);

        nameBox.getChildren().addAll(nameLbl, typeBox);
        profileRow.getChildren().addAll(avatarNode, nameBox);

        // Date Row
        HBox timeRow = new HBox(8);
        timeRow.setAlignment(Pos.CENTER_LEFT);
        ImageView clockIcon = createImageView("/images/icons/ic_clock.png", 14, 14);
        Label timeLbl = new Label(data.dateTime);
        timeLbl.getStyleClass().add("card-time-text");
        if (clockIcon != null) {
            timeRow.getChildren().add(clockIcon);
        }
        timeRow.getChildren().add(timeLbl);

        // Notes Row
        HBox notesRow = new HBox(8);
        notesRow.setAlignment(Pos.TOP_LEFT);
        ImageView notesIcon = createImageView("/images/icons/ic_stethoscope.png", 14, 14);
        Label notesLbl = new Label(data.notes);
        notesLbl.setWrapText(true);
        notesLbl.getStyleClass().add("card-notes-text");
        if (notesIcon != null) {
            notesRow.getChildren().add(notesIcon);
        }
        notesRow.getChildren().add(notesLbl);

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

    private HBox createTopHeader() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_RIGHT);

        HBox searchField = new HBox(8);
        searchField.getStyleClass().add("search-input-box");
        searchField.setAlignment(Pos.CENTER_LEFT);

        ImageView searchIcon = createImageView("/images/icons/ic_search.png", 16, 16);

        TextField searchInput = new TextField();
        searchInput.setPromptText("Search patients or IDs...");
        searchInput.getStyleClass().add("search-text-field");
        if (searchIcon != null) {
            searchField.getChildren().add(searchIcon);
        }
        searchField.getChildren().add(searchInput);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox rightIcons = new HBox(18);
        rightIcons.setAlignment(Pos.CENTER_RIGHT);

        StackPane notificationBox = new StackPane();
        ImageView bellIcon = createImageView("/images/icons/ic_bell.png", 18, 18);
        Circle badge = new Circle(4, Color.RED);
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        if (bellIcon != null) {
            notificationBox.getChildren().add(bellIcon);
        }
        notificationBox.getChildren().add(badge);
        notificationBox.getStyleClass().add("clickable-icon");

        ImageView userAvatar = createImageView("/images/doctor/portrait-3d-male-doctor.png", 32, 32);
        if (userAvatar != null) {
            Circle clip = new Circle(16, 16, 16);
            userAvatar.setClip(clip);
            userAvatar.getStyleClass().add("clickable-icon");
            userAvatar.setOnMouseClicked(e -> Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene()));
        }

        rightIcons.getChildren().add(notificationBox);
        if (userAvatar != null) {
            rightIcons.getChildren().add(userAvatar);
        }
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
        
        // Navigation added to open NewAppointmentView
        newApptBtn.setOnAction(e -> Navigation.goTo(stage, () -> new NewAppointmentView(stage).getScene()));

        section.setLeft(titleBox);
        section.setRight(newApptBtn);
        return section;
    }

    private StackPane createInitialsAvatar(String initials) {
        StackPane initialsAvatar = new StackPane();
        initialsAvatar.getStyleClass().add("initials-avatar-large");
        Label initialsText = new Label(initials != null ? initials : "--");
        initialsText.getStyleClass().add("initials-text-large");
        initialsAvatar.getChildren().add(initialsText);
        return initialsAvatar;
    }

    private void showInformationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /** Helper method for safely loading image resources */
    private ImageView createImageView(String resourcePath, double width, double height) {
        if (resourcePath == null) return null;
        try {
            InputStream is = getClass().getResourceAsStream(resourcePath);
            if (is != null) {
                ImageView imageView = new ImageView(new javafx.scene.image.Image(is));
                imageView.setFitWidth(width);
                imageView.setFitHeight(height);
                imageView.setPreserveRatio(true);
                return imageView;
            }
        } catch (Exception ignored) {}
        return null;
    }
}