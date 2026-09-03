package com.healthsphere.view.doctor;

import com.healthsphere.util.Navigation;
import com.healthsphere.util.ResourceImage;
import com.healthsphere.util.SessionManager;
import com.healthsphere.view.authentication.LoginView;

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
 * NewAppointmentView represents the appointment creation screen for Doctors in Health-Sphere.
 * Maintains full sidebar navigation, top header consistency, and page scrollability.
 */
public class NewAppointmentView {

    private final Stage stage;
    private final Scene scene;

    public NewAppointmentView(Stage stage) {
        this.stage = stage;
        this.scene = createScene();
    }

    public Scene getScene() {
        return this.scene;
    }

    private Scene createScene() {
        BorderPane mainRoot = new BorderPane();
        mainRoot.getStyleClass().add("root-pane");

        // --- Sidebar (Left Navigation strictly matching Dashboard) ---
        VBox sidebar = createSidebar();
        mainRoot.setLeft(sidebar);

        // --- Main Content Area ---
        VBox contentArea = new VBox(20);
        contentArea.setPadding(new Insets(20, 30, 30, 30));
        contentArea.getStyleClass().add("content-area");

        // Top Header
        HBox topHeader = createTopHeader();
        contentArea.getChildren().add(topHeader);

        // Title and Back Action Header
        BorderPane titleSection = createTitleSection();
        contentArea.getChildren().add(titleSection);

        // Form Layout Container
        VBox formCard = createFormCard();
        contentArea.getChildren().add(formCard);

        mainRoot.setCenter(contentArea);

        // Outer ScrollPane Container
        ScrollPane outerScrollPane = new ScrollPane(mainRoot);
        outerScrollPane.setFitToWidth(true);
        outerScrollPane.setFitToHeight(true);
        outerScrollPane.getStyleClass().add("content-scrollpane");

        Scene newAppointmentScene = new Scene(outerScrollPane, stage.getWidth(), stage.getHeight());
        
        try {
            newAppointmentScene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/css/appointments.css")).toExternalForm());
        } catch (Exception ignored) {}

        return newAppointmentScene;
    }

    private BorderPane createTitleSection() {
        BorderPane section = new BorderPane();

        VBox titleBox = new VBox(2);
        Label mainTitle = new Label("Schedule New Appointment");
        mainTitle.getStyleClass().add("page-title");
        Label subTitle = new Label("Fill in details to book an appointment");
        subTitle.getStyleClass().add("page-subtitle");
        titleBox.getChildren().addAll(mainTitle, subTitle);

        Button backBtn = new Button("← Back to Appointments");
        backBtn.getStyleClass().add("btn-card-reschedule");
        backBtn.setOnAction(e -> Navigation.goTo(stage, () -> new AppointmentsView(stage).getScene()));

        section.setLeft(titleBox);
        section.setRight(backBtn);
        return section;
    }

    private VBox createFormCard() {
        VBox card = new VBox(20);
        card.getStyleClass().add("filter-container-card");
        card.setPadding(new Insets(24));
        card.setMaxWidth(800);

        GridPane formGrid = new GridPane();
        formGrid.setHgap(20);
        formGrid.setVgap(16);

        // Patient Name Field
        Label nameLbl = new Label("Patient Name");
        nameLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        TextField patientNameField = new TextField();
        patientNameField.setPromptText("e.g. Robert Chen");
        patientNameField.getStyleClass().add("search-text-field");
        patientNameField.setPrefHeight(36);

        // Consultation Type
        Label typeLbl = new Label("Consultation Type");
        typeLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("In-Person", "Video Consultation", "Follow-up", "General Checkup");
        typeCombo.getSelectionModel().selectFirst();
        typeCombo.setPrefWidth(300);
        typeCombo.setPrefHeight(36);

        // Date Picker
        Label dateLbl = new Label("Appointment Date");
        dateLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        DatePicker datePicker = new DatePicker();
        datePicker.getStyleClass().add("custom-date-picker");
        datePicker.setPrefHeight(36);

        // Time Field
        Label timeLbl = new Label("Time");
        timeLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        TextField timeField = new TextField();
        timeField.setPromptText("e.g. 10:30 AM");
        timeField.getStyleClass().add("search-text-field");
        timeField.setPrefHeight(36);

        // Notes Area
        Label notesLbl = new Label("Notes / Reason for Visit");
        notesLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Enter medical background or consultation notes...");
        notesArea.setPrefRowCount(4);
        notesArea.setWrapText(true);

        // Form Layout Grid Assignment
        formGrid.add(nameLbl, 0, 0);
        formGrid.add(patientNameField, 0, 1);

        formGrid.add(typeLbl, 1, 0);
        formGrid.add(typeCombo, 1, 1);

        formGrid.add(dateLbl, 0, 2);
        formGrid.add(datePicker, 0, 3);

        formGrid.add(timeLbl, 1, 2);
        formGrid.add(timeField, 1, 3);

        formGrid.add(notesLbl, 0, 4, 2, 1);
        formGrid.add(notesArea, 0, 5, 2, 1);

        // Action Buttons
        HBox actionBox = new HBox(12);
        actionBox.setAlignment(Pos.CENTER_RIGHT);
        actionBox.setPadding(new Insets(10, 0, 0, 0));

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("btn-card-reschedule");
        cancelBtn.setOnAction(e -> Navigation.goTo(stage, () -> new AppointmentsView(stage).getScene()));

        Button saveBtn = new Button("Create Appointment");
        saveBtn.getStyleClass().add("btn-primary-action");
        saveBtn.setOnAction(e -> Navigation.goTo(stage, () -> new AppointmentsView(stage).getScene()));

        actionBox.getChildren().addAll(cancelBtn, saveBtn);

        card.getChildren().addAll(formGrid, actionBox);
        return card;
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
            "Dashboard", "Today's Schedule", "Appointments", "Patient Details",
            "Medical Reports & Prescription", "Availability & Schedule", "Doctor Profile", "AI Health Assistant"
        };
        String[] icons = {
            "ic_dashboard", "ic_schedule", "ic_appointments", "ic_patient",
            "ic_reports", "ic_availability", "ic_profile", "ic_ai"
        };

        for (int i = 0; i < tabs.length; i++) {
            HBox navTab = new HBox(12);
            navTab.setAlignment(Pos.CENTER_LEFT);
            navTab.setPadding(new Insets(10, 14, 10, 14));
            navTab.getStyleClass().add("nav-tab");

            ImageView icon = new ImageView(ResourceImage.load("/images/icons/" + icons[i] + ".png"));
            icon.setFitWidth(18);
            icon.setFitHeight(18);

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

            navTab.getChildren().addAll(icon, tabLabel);
            navItems.getChildren().add(navTab);

            final int index = i;
            navTab.setOnMouseClicked(e -> handleSidebarTabClick(index));
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
        Label profName = new Label(SessionManager.getDoctorDisplayName());
        profName.getStyleClass().add("sidebar-profile-name");
        profName.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 13px;");

        profileTexts.getChildren().addAll(profSubText, profName);
        sidebarProfile.getChildren().addAll(profileAvatar, profileTexts);
        sidebarProfile.setOnMouseClicked(e -> Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene()));

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
        logoutTab.setOnMouseClicked(e -> handleLogout());

        footer.getChildren().addAll(sidebarProfile, logoutTab);
        sidebar.getChildren().addAll(logoSection, navItems, spacer, footer);
        return sidebar;
    }

    private void handleLogout() {
        try {
            SessionManager.clearSession();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Navigation.goTo(stage, () -> new LoginView(stage).getScene());
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
}