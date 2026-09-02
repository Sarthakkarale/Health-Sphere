package com.healthsphere.view.doctor;

import com.healthsphere.controller.appointment.AppointmentController;
import com.healthsphere.controller.doctor.DoctorScheduleController;
import com.healthsphere.model.Appointment;
import com.healthsphere.model.DoctorSchedule;
import com.healthsphere.util.Navigation;
import com.healthsphere.util.ResourceImage;
import com.healthsphere.util.SessionManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * AvailabilityScheduleView
 *
 * Doctor availability and weekly schedule management page.
 *
 * Architecture:
 *
 * View
 *   ↓
 * DoctorScheduleController / AppointmentController
 *   ↓
 * DAO
 *   ↓
 * Firebase Firestore
 *
 * Backend functionality:
 *
 * - Loads doctor's weekly working hours
 * - Loads default appointment slot
 * - Loads emergency availability
 * - Saves working hours
 * - Saves emergency availability
 * - Loads real doctor appointments
 * - Displays appointments in weekly calendar
 * - Supports previous/next week navigation
 *
 * No hardcoded appointment data is used.
 */
public class AvailabilityScheduleView {

    private final Stage stage;
    private final Scene scene;

    // ============================================================
    // BACKEND
    // ============================================================

    private final DoctorScheduleController scheduleController;
    private final AppointmentController appointmentController;

    private final String doctorUid;

    private DoctorSchedule currentSchedule;

    // ============================================================
    // UI REFERENCES
    // ============================================================

    private final java.util.Map<String, CheckBox> dayCheckBoxes =
            new java.util.HashMap<>();

    private final java.util.Map<String, ComboBox<String>> startTimeCombos =
            new java.util.HashMap<>();

    private final java.util.Map<String, ComboBox<String>> endTimeCombos =
            new java.util.HashMap<>();

    private ComboBox<String> slotCombo;

    private ToggleButton emergencyToggle;

    private GridPane scheduleGrid;

    private Label dateRangeLabel;

    private TextField searchInput;

    // ============================================================
    // CALENDAR STATE
    // ============================================================

    private LocalDate displayedWeekMonday;

    private List<Appointment> doctorAppointments =
            new ArrayList<>();

    // ============================================================
    // DATE FORMATTERS
    // ============================================================

    private static final DateTimeFormatter
            APPOINTMENT_DATE_FORMAT =
            DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd"
            );

    private static final DateTimeFormatter
            APPOINTMENT_TIME_FORMAT =
            DateTimeFormatter.ofPattern(
                    "h:mm a",
                    Locale.ENGLISH
            );

    private static final DateTimeFormatter
            HEADER_MONTH_FORMAT =
            DateTimeFormatter.ofPattern(
                    "MMMM yyyy",
                    Locale.ENGLISH
            );


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public AvailabilityScheduleView(Stage stage) {

        this.stage = stage;

        this.scheduleController =
                new DoctorScheduleController();

        this.appointmentController =
                new AppointmentController();

        this.doctorUid =
                getCurrentDoctorUid();

        /*
         * Start calendar on the current week.
         */
        this.displayedWeekMonday =
                LocalDate.now()
                        .with(
                                DayOfWeek.MONDAY
                        );

        /*
         * Load doctor's saved schedule.
         *
         * If the doctor has never configured availability,
         * DoctorScheduleController returns a default schedule.
         */
        this.currentSchedule =
                loadDoctorSchedule();

        /*
         * Load real appointments.
         */
        loadDoctorAppointments();

        /*
         * Build UI.
         */
        this.scene =
                createScene();
    }


    // ============================================================
    // PUBLIC SCENE
    // ============================================================

    public Scene getScene() {

        return this.scene;
    }


    // ============================================================
    // CREATE SCENE
    // ============================================================

    private Scene createScene() {

        BorderPane mainRoot =
                new BorderPane();

        mainRoot
                .getStyleClass()
                .add("root-pane");


        // --------------------------------------------------------
        // FIXED SIDEBAR
        // --------------------------------------------------------

        VBox sidebar =
                createSidebar();

        mainRoot.setLeft(
                sidebar
        );


        // --------------------------------------------------------
        // MAIN CONTENT
        // --------------------------------------------------------

        VBox contentArea =
                new VBox(20);

        contentArea.setPadding(
                new Insets(
                        24,
                        32,
                        32,
                        32
                )
        );

        contentArea
                .getStyleClass()
                .add("content-area");


        // Top header
        contentArea.getChildren()
                .add(
                        createTopHeader()
                );


        // Page header
        contentArea.getChildren()
                .add(
                        createPageHeader()
                );


        // Body
        contentArea.getChildren()
                .add(
                        createBodyLayout()
                );


        // --------------------------------------------------------
        // CONTENT SCROLL
        // --------------------------------------------------------

        ScrollPane contentScrollPane =
                new ScrollPane(
                        contentArea
                );

        contentScrollPane
                .setFitToWidth(true);

        contentScrollPane
                .setFitToHeight(false);

        contentScrollPane
                .setVbarPolicy(
                        ScrollPane.ScrollBarPolicy.AS_NEEDED
                );

        contentScrollPane
                .setHbarPolicy(
                        ScrollPane.ScrollBarPolicy.NEVER
                );

        contentScrollPane
                .getStyleClass()
                .add("content-scrollpane");


        mainRoot.setCenter(
                contentScrollPane
        );


        // --------------------------------------------------------
        // SCENE
        // --------------------------------------------------------

        Scene availabilityScene =
                new Scene(
                        mainRoot,
                        stage.getWidth(),
                        stage.getHeight()
                );


        try {

            availabilityScene
                    .getStylesheets()
                    .add(
                            Objects.requireNonNull(
                                    getClass()
                                            .getResource(
                                                    "/css/availability_schedule.css"
                                            )
                            )
                                    .toExternalForm()
                    );

        } catch (Exception ignored) {
        }


        return availabilityScene;
    }


    // ============================================================
    // CURRENT DOCTOR
    // ============================================================

    private String getCurrentDoctorUid() {

        if (SessionManager
                .getInstance()
                .getCurrentUser() == null) {

            throw new IllegalStateException(
                    "No user is currently logged in."
            );
        }


        String uid =
                SessionManager
                        .getInstance()
                        .getCurrentUser()
                        .getUid();


        if (uid == null
                || uid.trim().isEmpty()) {

            throw new IllegalStateException(
                    "Current doctor UID is not available."
            );
        }


        return uid;
    }


    // ============================================================
    // LOAD SCHEDULE
    // ============================================================

    private DoctorSchedule loadDoctorSchedule() {

        try {

            DoctorSchedule schedule =
                    scheduleController
                            .getDoctorSchedule(
                                    doctorUid
                            );


            if (schedule == null) {

                return new DoctorSchedule(
                        doctorUid
                );
            }


            return schedule;

        } catch (Exception e) {

            e.printStackTrace();

            /*
             * The page should still open even if Firestore
             * temporarily fails.
             */
            return new DoctorSchedule(
                    doctorUid
            );
        }
    }


    // ============================================================
    // LOAD APPOINTMENTS
    // ============================================================

    private void loadDoctorAppointments() {

        try {

            List<Appointment> appointments =
                    appointmentController
                            .getDoctorAppointments(
                                    doctorUid
                            );


            if (appointments == null) {

                doctorAppointments =
                        new ArrayList<>();

            } else {

                doctorAppointments =
                        new ArrayList<>(
                                appointments
                        );
            }


            System.out.println(
                    "Availability Schedule - "
                            + "Doctor appointments loaded: "
                            + doctorAppointments.size()
            );

        } catch (Exception e) {

            e.printStackTrace();

            doctorAppointments =
                    new ArrayList<>();
        }
    }


    // ============================================================
    // SIDEBAR
    // ============================================================

    private VBox createSidebar() {

        VBox sidebar =
                new VBox();


        sidebar.setPadding(
                new Insets(
                        25,
                        15,
                        25,
                        15
                )
        );


        sidebar
                .getStyleClass()
                .add("sidebar");


        sidebar.setStyle(
                "-fx-background-color: #0F172A;"
        );


        sidebar.setMinWidth(260);
        sidebar.setPrefWidth(260);
        sidebar.setMaxWidth(260);


        // --------------------------------------------------------
        // LOGO
        // --------------------------------------------------------

        HBox logoSection =
                new HBox(12);

        logoSection.setPadding(
                new Insets(
                        0,
                        0,
                        25,
                        5
                )
        );

        logoSection.setAlignment(
                Pos.CENTER_LEFT
        );


        StackPane logoIconBox =
                new StackPane();

        logoIconBox
                .getStyleClass()
                .add("logo-icon-box");


        logoIconBox.setStyle(
                "-fx-background-color: #3B82F6;"
                        + "-fx-background-radius: 8px;"
                        + "-fx-padding: 8px;"
        );


        ImageView logoIcon =
                new ImageView(
                        ResourceImage.load(
                                "/images/icons/ic_shield.png"
                        )
                );

        logoIcon.setFitWidth(20);
        logoIcon.setFitHeight(20);


        logoIconBox
                .getChildren()
                .add(logoIcon);


        VBox logoText =
                new VBox(2);


        Label appName =
                new Label(
                        "Health-Sphere"
                );

        appName
                .getStyleClass()
                .add("logo-name");

        appName.setStyle(
                "-fx-text-fill: #FFFFFF;"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-size: 16px;"
        );


        Label doctorSubtext =
                new Label(
                        "Doctor Dashboard"
                );

        doctorSubtext
                .getStyleClass()
                .add("logo-subtext");

        doctorSubtext.setStyle(
                "-fx-text-fill: #94A3B8;"
                        + "-fx-font-size: 12px;"
        );


        logoText
                .getChildren()
                .addAll(
                        appName,
                        doctorSubtext
                );


        logoSection
                .getChildren()
                .addAll(
                        logoIconBox,
                        logoText
                );


        // --------------------------------------------------------
        // NAVIGATION
        // --------------------------------------------------------

        VBox navItems =
                new VBox(6);


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


        for (int i = 0;
             i < tabs.length;
             i++) {

            HBox navTab =
                    new HBox(12);


            navTab.setAlignment(
                    Pos.CENTER_LEFT
            );


            navTab.setPadding(
                    new Insets(
                            10,
                            14,
                            10,
                            14
                    )
            );


            navTab
                    .getStyleClass()
                    .add("nav-tab");


            ImageView icon =
                    new ImageView(
                            ResourceImage.load(
                                    "/images/icons/"
                                            + icons[i]
                                            + ".png"
                            )
                    );


            icon.setFitWidth(18);
            icon.setFitHeight(18);
            icon.setMouseTransparent(true);


            Label tabLabel =
                    new Label(
                            tabs[i]
                    );


            tabLabel
                    .getStyleClass()
                    .add("nav-text");


            tabLabel.setMouseTransparent(
                    true
            );


            if (i == 5) {

                navTab
                        .getStyleClass()
                        .add("nav-tab-active");


                navTab.setStyle(
                        "-fx-background-color: #3B82F6;"
                                + "-fx-background-radius: 8px;"
                                + "-fx-cursor: hand;"
                );


                tabLabel.setStyle(
                        "-fx-text-fill: #FFFFFF;"
                                + "-fx-font-weight: bold;"
                );

            } else {

                navTab.setStyle(
                        "-fx-background-color: transparent;"
                                + "-fx-background-radius: 8px;"
                                + "-fx-cursor: hand;"
                );


                tabLabel.setStyle(
                        "-fx-text-fill: #94A3B8;"
                );
            }


            navTab
                    .getChildren()
                    .addAll(
                            icon,
                            tabLabel
                    );


            navItems
                    .getChildren()
                    .add(navTab);


            final int index = i;


            navTab.setOnMouseClicked(
                    event ->
                            handleSidebarTabClick(
                                    index
                            )
            );
        }


        // --------------------------------------------------------
        // FOOTER
        // --------------------------------------------------------

        Region spacer =
                new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );


        VBox footer =
                new VBox(10);

        footer.setPadding(
                new Insets(
                        15,
                        0,
                        0,
                        0
                )
        );


        // Doctor profile
        HBox sidebarProfile =
                new HBox(12);


        sidebarProfile.setAlignment(
                Pos.CENTER_LEFT
        );


        sidebarProfile.setPadding(
                new Insets(
                        10,
                        12,
                        10,
                        12
                )
        );


        sidebarProfile
                .getStyleClass()
                .add(
                        "sidebar-profile-box"
                );


        sidebarProfile.setStyle(
                "-fx-background-color: #1E293B;"
                        + "-fx-background-radius: 10px;"
                        + "-fx-cursor: hand;"
        );


        ImageView profileAvatar =
                new ImageView(
                        ResourceImage.load(
                                "/images/doctor/"
                                        + "portrait-3d-male-doctor.png"
                        )
                );


        profileAvatar.setFitWidth(36);
        profileAvatar.setFitHeight(36);


        Circle profileClip =
                new Circle(
                        18,
                        18,
                        18
                );


        profileAvatar.setClip(
                profileClip
        );


        VBox profileTexts =
                new VBox(2);


        Label profSubText =
                new Label(
                        "Doctor Profile"
                );


        profSubText.setStyle(
                "-fx-text-fill: #64748B;"
                        + "-fx-font-size: 11px;"
        );


        Label profName =
                new Label(
                        "Dr. Sarah"
                );


        profName
                .getStyleClass()
                .add(
                        "sidebar-profile-name"
                );


        profName.setStyle(
                "-fx-text-fill: #FFFFFF;"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-size: 13px;"
        );


        profileTexts
                .getChildren()
                .addAll(
                        profSubText,
                        profName
                );


        sidebarProfile
                .getChildren()
                .addAll(
                        profileAvatar,
                        profileTexts
                );


        sidebarProfile.setOnMouseClicked(
                event ->
                        Navigation.goTo(
                                stage,
                                () ->
                                        new DoctorProfileView(
                                                stage
                                        ).getScene()
                        )
        );


        // Logout
        HBox logoutTab =
                new HBox(12);


        logoutTab.setAlignment(
                Pos.CENTER_LEFT
        );


        logoutTab.setPadding(
                new Insets(
                        10,
                        14,
                        10,
                        14
                )
        );


        logoutTab
                .getStyleClass()
                .add("nav-tab");


        logoutTab.setStyle(
                "-fx-cursor: hand;"
        );


        ImageView logoutIcon =
                new ImageView(
                        ResourceImage.load(
                                "/images/icons/ic_logout.png"
                        )
                );


        logoutIcon.setFitWidth(18);
        logoutIcon.setFitHeight(18);


        Label logoutLabel =
                new Label(
                        "Logout"
                );


        logoutLabel
                .getStyleClass()
                .add("nav-text");


        logoutLabel.setStyle(
                "-fx-text-fill: #94A3B8;"
        );


        logoutTab
                .getChildren()
                .addAll(
                        logoutIcon,
                        logoutLabel
                );


        logoutTab.setOnMouseClicked(
                event ->
                        showInformationAlert(
                                "Logout",
                                "Logout functionality is handled by the authentication module."
                        )
        );


        footer
                .getChildren()
                .addAll(
                        sidebarProfile,
                        logoutTab
                );


        sidebar
                .getChildren()
                .addAll(
                        logoSection,
                        navItems,
                        spacer,
                        footer
                );


        return sidebar;
    }


    // ============================================================
    // SIDEBAR NAVIGATION
    // ============================================================

    private void handleSidebarTabClick(
            int index) {

        switch (index) {

            case 0:
                Navigation.goTo(
                        stage,
                        () ->
                                new DoctorDashboardView(
                                        stage
                                ).getScene()
                );
                break;


            case 1:
                Navigation.goTo(
                        stage,
                        () ->
                                new TodaysScheduleView(
                                        stage
                                ).getScene()
                );
                break;


            case 2:
                Navigation.goTo(
                        stage,
                        () ->
                                new AppointmentsView(
                                        stage
                                ).getScene()
                );
                break;


            case 3:
                Navigation.goTo(
                        stage,
                        () ->
                                new PatientDetailsView(
                                        stage
                                ).getScene()
                );
                break;


            case 4:
                Navigation.goTo(
                        stage,
                        () ->
                                new MedicalReportsView(
                                        stage
                                ).getScene()
                );
                break;


            case 5:
                Navigation.goTo(
                        stage,
                        () ->
                                new AvailabilityScheduleView(
                                        stage
                                ).getScene()
                );
                break;


            case 6:
                Navigation.goTo(
                        stage,
                        () ->
                                new DoctorProfileView(
                                        stage
                                ).getScene()
                );
                break;


            case 7:
                Navigation.goTo(
                        stage,
                        () ->
                                new AIHealthAssistantView(
                                        stage
                                ).getScene()
                );
                break;


            default:
                break;
        }
    }


    // ============================================================
    // TOP HEADER
    // ============================================================

    private HBox createTopHeader() {

        HBox topBar =
                new HBox();

        topBar.setAlignment(
                Pos.CENTER_LEFT
        );


        HBox breadcrumbs =
                new HBox(6);

        breadcrumbs.setAlignment(
                Pos.CENTER_LEFT
        );


        Label p1 =
                new Label(
                        "Patients"
                );

        p1.getStyleClass()
                .add(
                        "breadcrumb-inactive"
                );


        p1.setStyle(
                "-fx-cursor: hand;"
        );


        p1.setOnMouseClicked(
                e ->
                        Navigation.goTo(
                                stage,
                                () ->
                                        new PatientDetailsView(
                                                stage
                                        ).getScene()
                        )
        );


        Label sep =
                new Label(
                        "›"
                );


        sep.getStyleClass()
                .add(
                        "breadcrumb-separator"
                );


        Label p2 =
                new Label(
                        "Availability & Schedule"
                );


        p2.getStyleClass()
                .add(
                        "breadcrumb-active"
                );


        breadcrumbs
                .getChildren()
                .addAll(
                        p1,
                        sep,
                        p2
                );


        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );


        // --------------------------------------------------------
        // SEARCH
        // --------------------------------------------------------

        HBox searchField =
                new HBox(10);

        searchField
                .getStyleClass()
                .add(
                        "search-input-box"
                );


        searchField.setAlignment(
                Pos.CENTER_LEFT
        );


        searchField.setPrefWidth(
                260
        );


        ImageView searchIcon =
                new ImageView(
                        ResourceImage.load(
                                "/images/icons/ic_search.png"
                        )
                );


        searchIcon.setFitWidth(16);
        searchIcon.setFitHeight(16);


        searchInput =
                new TextField();


        searchInput.setPromptText(
                "Search appointments..."
        );


        searchInput
                .getStyleClass()
                .add(
                        "search-text-field"
                );


        searchInput
                .textProperty()
                .addListener(
                        (obs, oldValue, newValue) ->
                                refreshCalendar()
                );


        HBox.setHgrow(
                searchInput,
                Priority.ALWAYS
        );


        searchField
                .getChildren()
                .addAll(
                        searchIcon,
                        searchInput
                );


        // --------------------------------------------------------
        // RIGHT SIDE
        // --------------------------------------------------------

        HBox rightIcons =
                new HBox(16);


        rightIcons.setAlignment(
                Pos.CENTER_RIGHT
        );


        StackPane notificationBox =
                new StackPane();


        ImageView bellIcon =
                new ImageView(
                        ResourceImage.load(
                                "/images/icons/ic_bell.png"
                        )
                );


        bellIcon.setFitWidth(18);
        bellIcon.setFitHeight(18);
        bellIcon.setMouseTransparent(true);


        Circle badge =
                new Circle(
                        4,
                        Color.web(
                                "#EF4444"
                        )
                );


        badge.setMouseTransparent(true);


        StackPane.setAlignment(
                badge,
                Pos.TOP_RIGHT
        );


        notificationBox
                .getChildren()
                .addAll(
                        bellIcon,
                        badge
                );


        notificationBox
                .getStyleClass()
                .add(
                        "clickable-icon"
                );


        notificationBox.setStyle(
                "-fx-cursor: hand;"
        );


        notificationBox.setOnMouseClicked(
                e ->
                        showInformationAlert(
                                "Notifications",
                                "No new notifications."
                        )
        );


        HBox userProfile =
                new HBox(10);


        userProfile.setAlignment(
                Pos.CENTER_LEFT
        );


        userProfile
                .getStyleClass()
                .add(
                        "clickable-icon"
                );


        userProfile.setStyle(
                "-fx-cursor: hand;"
        );


        ImageView userAvatar =
                new ImageView(
                        ResourceImage.load(
                                "/images/doctor/"
                                        + "portrait-3d-male-doctor.png"
                        )
                );


        userAvatar.setFitWidth(36);
        userAvatar.setFitHeight(36);


        Circle clip =
                new Circle(
                        18,
                        18,
                        18
                );


        userAvatar.setClip(
                clip
        );


        VBox userDetails =
                new VBox(0);


        Label docName =
                new Label(
                        "Dr. Sarah Jenkins"
                );


        docName.getStyleClass()
                .add(
                        "profile-name"
                );


        Label docDept =
                new Label(
                        "Cardiology"
                );


        docDept.getStyleClass()
                .add(
                        "profile-dept"
                );


        userDetails
                .getChildren()
                .addAll(
                        docName,
                        docDept
                );


        userProfile
                .getChildren()
                .addAll(
                        userAvatar,
                        userDetails
                );


        userProfile.setOnMouseClicked(
                e ->
                        Navigation.goTo(
                                stage,
                                () ->
                                        new DoctorProfileView(
                                                stage
                                        ).getScene()
                        )
        );


        rightIcons
                .getChildren()
                .addAll(
                        notificationBox,
                        userProfile
                );


        topBar
                .getChildren()
                .addAll(
                        breadcrumbs,
                        spacer,
                        searchField,
                        rightIcons
                );


        return topBar;
    }


    // ============================================================
    // PAGE HEADER
    // ============================================================

    private BorderPane createPageHeader() {

        BorderPane header =
                new BorderPane();


        header.setPadding(
                new Insets(
                        4,
                        0,
                        8,
                        0
                )
        );


        VBox titles =
                new VBox(4);


        Label title =
                new Label(
                        "Schedule Management"
                );


        title.getStyleClass()
                .add(
                        "page-title"
                );


        Label subtext =
                new Label(
                        "Configure your working hours, breaks, and view your weekly calendar."
                );


        subtext.getStyleClass()
                .add(
                        "page-subtext"
                );


        titles
                .getChildren()
                .addAll(
                        title,
                        subtext
                );


        HBox actionBtns =
                new HBox(12);


        actionBtns.setAlignment(
                Pos.CENTER_RIGHT
        );


        // --------------------------------------------------------
        // EXPORT
        // --------------------------------------------------------

        Button exportBtn =
                new Button(
                        "Export"
                );


        ImageView exportIcon =
                new ImageView(
                        ResourceImage.load(
                                "/images/icons/ic_export.png"
                        )
                );


        exportIcon.setFitWidth(14);
        exportIcon.setFitHeight(14);


        exportBtn.setGraphic(
                exportIcon
        );


        exportBtn
                .getStyleClass()
                .add(
                        "btn-secondary-action"
                );


        exportBtn.setOnAction(
                e ->
                        exportSchedule()
        );


        // --------------------------------------------------------
        // SAVE
        // --------------------------------------------------------

        Button saveBtn =
                new Button(
                        "Save Changes"
                );


        ImageView saveIcon =
                new ImageView(
                        ResourceImage.load(
                                "/images/icons/ic_save.png"
                        )
                );


        saveIcon.setFitWidth(14);
        saveIcon.setFitHeight(14);


        saveBtn.setGraphic(
                saveIcon
        );


        saveBtn
                .getStyleClass()
                .add(
                        "btn-primary-action"
                );


        saveBtn.setOnAction(
                e ->
                        saveSchedule()
        );


        actionBtns
                .getChildren()
                .addAll(
                        exportBtn,
                        saveBtn
                );


        header.setLeft(
                titles
        );


        header.setRight(
                actionBtns
        );


        return header;
    }


    // ============================================================
    // BODY
    // ============================================================

    private HBox createBodyLayout() {

        HBox layout =
                new HBox(20);


        VBox scheduleCard =
                createScheduleCalendarCard();


        HBox.setHgrow(
                scheduleCard,
                Priority.ALWAYS
        );


        VBox controlsPanel =
                new VBox(20);


        controlsPanel.setMinWidth(
                320
        );


        controlsPanel.setPrefWidth(
                340
        );


        controlsPanel.setMaxWidth(
                340
        );


        VBox aiAssistantCard =
                createAIAssistantCard();


        VBox workingHoursCard =
                createWorkingHoursCard();


        VBox emergencyCard =
                createEmergencyCard();


        controlsPanel
                .getChildren()
                .addAll(
                        aiAssistantCard,
                        workingHoursCard,
                        emergencyCard
                );


        layout
                .getChildren()
                .addAll(
                        scheduleCard,
                        controlsPanel
                );


        return layout;
    }


    // ============================================================
    // CALENDAR CARD
    // ============================================================

    private VBox createScheduleCalendarCard() {

        VBox card =
                new VBox(16);


        card.getStyleClass()
                .add(
                        "panel-card"
                );


        card.setPadding(
                new Insets(20)
        );


        // --------------------------------------------------------
        // CALENDAR HEADER
        // --------------------------------------------------------

        BorderPane calHeader =
                new BorderPane();


        Label calTitle =
                new Label(
                        "Weekly Schedule View"
                );


        calTitle.getStyleClass()
                .add(
                        "card-title"
                );


        HBox toggleGroup =
                new HBox(0);


        toggleGroup
                .getStyleClass()
                .add(
                        "segmented-button-bar"
                );


        Button weekBtn =
                new Button(
                        "Week"
                );


        weekBtn.getStyleClass()
                .addAll(
                        "segmented-btn",
                        "segmented-btn-active"
                );


        Button monthBtn =
                new Button(
                        "Month"
                );


        monthBtn
                .getStyleClass()
                .add(
                        "segmented-btn"
                );


        /*
         * Backend currently stores a recurring weekly schedule.
         *
         * Therefore Week is the real supported calendar view.
         * Month remains in the UI but does not invent monthly data.
         */
        weekBtn.setOnAction(
                e -> {

                    weekBtn
                            .getStyleClass()
                            .add(
                                    "segmented-btn-active"
                            );

                    monthBtn
                            .getStyleClass()
                            .remove(
                                    "segmented-btn-active"
                            );

                    refreshCalendar();
                }
        );


        monthBtn.setOnAction(
                e -> {

                    monthBtn
                            .getStyleClass()
                            .add(
                                    "segmented-btn-active"
                            );

                    weekBtn
                            .getStyleClass()
                            .remove(
                                    "segmented-btn-active"
                            );

                    showInformationAlert(
                            "Month View",
                            "Monthly view is not connected yet. "
                                    + "The weekly schedule and real appointments are currently displayed."
                    );

                    /*
                     * Keep Week as the actual data view.
                     */
                    monthBtn
                            .getStyleClass()
                            .remove(
                                    "segmented-btn-active"
                            );

                    weekBtn
                            .getStyleClass()
                            .add(
                                    "segmented-btn-active"
                            );
                }
        );


        toggleGroup
                .getChildren()
                .addAll(
                        weekBtn,
                        monthBtn
                );


        calHeader.setLeft(
                calTitle
        );


        calHeader.setRight(
                toggleGroup
        );


        // --------------------------------------------------------
        // DATE NAVIGATION
        // --------------------------------------------------------

        BorderPane navLegendRow =
                new BorderPane();


        navLegendRow.setPadding(
                new Insets(
                        4,
                        0,
                        4,
                        0
                )
        );


        HBox dateNav =
                new HBox(12);


        dateNav.setAlignment(
                Pos.CENTER_LEFT
        );


        Button prevArrow =
                new Button(
                        "‹"
                );


        prevArrow
                .getStyleClass()
                .add(
                        "nav-arrow-btn"
                );


        prevArrow.setStyle(
                "-fx-cursor: hand;"
        );


        prevArrow.setOnAction(
                e -> {

                    displayedWeekMonday =
                            displayedWeekMonday
                                    .minusWeeks(
                                            1
                                    );

                    refreshCalendar();
                }
        );


        dateRangeLabel =
                new Label();


        dateRangeLabel
                .getStyleClass()
                .add(
                        "nav-date-label"
                );


        updateDateRangeLabel();


        Button nextArrow =
                new Button(
                        "›"
                );


        nextArrow
                .getStyleClass()
                .add(
                        "nav-arrow-btn"
                );


        nextArrow.setStyle(
                "-fx-cursor: hand;"
        );


        nextArrow.setOnAction(
                e -> {

                    displayedWeekMonday =
                            displayedWeekMonday
                                    .plusWeeks(
                                            1
                                    );

                    refreshCalendar();
                }
        );


        Button todayButton =
                new Button(
                        "Today"
                );


        todayButton
                .getStyleClass()
                .add(
                        "segmented-btn"
                );


        todayButton.setOnAction(
                e -> {

                    displayedWeekMonday =
                            LocalDate.now()
                                    .with(
                                            DayOfWeek.MONDAY
                                    );

                    refreshCalendar();
                }
        );


        dateNav
                .getChildren()
                .addAll(
                        prevArrow,
                        dateRangeLabel,
                        nextArrow,
                        todayButton
                );


        // --------------------------------------------------------
        // LEGENDS
        // --------------------------------------------------------

        HBox legends =
                new HBox(16);


        legends.setAlignment(
                Pos.CENTER_RIGHT
        );


        legends
                .getChildren()
                .addAll(
                        createLegendItem(
                                "Clinical",
                                "#2563EB"
                        ),
                        createLegendItem(
                                "Surgery",
                                "#0891B2"
                        ),
                        createLegendItem(
                                "Unavailable",
                                "#94A3B8"
                        )
                );


        navLegendRow.setLeft(
                dateNav
        );


        navLegendRow.setRight(
                legends
        );


        // --------------------------------------------------------
        // GRID
        // --------------------------------------------------------

        scheduleGrid =
                createScheduleGrid();


        card
                .getChildren()
                .addAll(
                        calHeader,
                        navLegendRow,
                        scheduleGrid
                );


        return card;
    }


    // ============================================================
    // LEGEND
    // ============================================================

    private HBox createLegendItem(
            String label,
            String hexColor) {

        HBox box =
                new HBox(6);


        box.setAlignment(
                Pos.CENTER_LEFT
        );


        Circle dot =
                new Circle(
                        4,
                        Color.web(
                                hexColor
                        )
                );


        Label l =
                new Label(
                        label
                );


        l.getStyleClass()
                .add(
                        "legend-text"
                );


        box
                .getChildren()
                .addAll(
                        dot,
                        l
                );


        return box;
    }


    // ============================================================
    // CALENDAR GRID
    // ============================================================

    private GridPane createScheduleGrid() {

        GridPane grid =
                new GridPane();


        grid.getStyleClass()
                .add(
                        "calendar-grid"
                );


        LocalDate monday =
                displayedWeekMonday;


        // --------------------------------------------------------
        // HEADER
        // --------------------------------------------------------

        Label emptyHeader =
                new Label();


        emptyHeader
                .getStyleClass()
                .add(
                        "calendar-header-cell"
                );


        emptyHeader.setPrefWidth(
                55
        );


        grid.add(
                emptyHeader,
                0,
                0
        );


        for (int i = 0;
             i < 5;
             i++) {

            LocalDate date =
                    monday.plusDays(
                            i
                    );


            String dayName =
                    date.getDayOfWeek()
                            .getDisplayName(
                                    TextStyle.SHORT,
                                    Locale.ENGLISH
                            );


            Label headerLabel =
                    new Label(
                            dayName
                                    + " "
                                    + date.getDayOfMonth()
                    );


            headerLabel
                    .getStyleClass()
                    .add(
                            "calendar-header-cell"
                    );


            if (date.equals(
                    LocalDate.now()
            )) {

                headerLabel
                        .getStyleClass()
                        .add(
                                "calendar-header-active"
                        );
            }


            headerLabel.setPrefWidth(
                    100
            );


            headerLabel.setAlignment(
                    Pos.CENTER
            );


            grid.add(
                    headerLabel,
                    i + 1,
                    0
            );
        }


        // --------------------------------------------------------
        // TIME ROWS
        // --------------------------------------------------------

        String[] timeSlots = {
                "8 AM",
                "9 AM",
                "10 AM",
                "11 AM",
                "12 PM",
                "1 PM"
        };


        for (int row = 0;
             row < timeSlots.length;
             row++) {

            Label timeLabel =
                    new Label(
                            timeSlots[row]
                    );


            timeLabel
                    .getStyleClass()
                    .add(
                            "calendar-time-cell"
                    );


            timeLabel.setAlignment(
                    Pos.CENTER_RIGHT
            );


            timeLabel.setPadding(
                    new Insets(
                            0,
                            10,
                            0,
                            0
                    )
            );


            grid.add(
                    timeLabel,
                    0,
                    row + 1
            );


            for (int col = 1;
                 col <= 5;
                 col++) {

                Pane emptyCell =
                        new Pane();


                emptyCell
                        .getStyleClass()
                        .add(
                                "calendar-slot-cell"
                        );


                emptyCell.setPrefHeight(
                        60
                );


                final int selectedColumn =
                        col;


                final int selectedRow =
                        row;


                emptyCell.setOnMouseClicked(
                        e ->
                                handleEmptyCalendarSlot(
                                        selectedColumn,
                                        selectedRow
                                )
                );


                grid.add(
                        emptyCell,
                        col,
                        row + 1
                );
            }
        }


        // --------------------------------------------------------
        // COLUMN WIDTHS
        // --------------------------------------------------------

        ColumnConstraints firstColumn =
                new ColumnConstraints();


        firstColumn.setMinWidth(
                55
        );


        firstColumn.setPrefWidth(
                55
        );


        grid.getColumnConstraints()
                .add(
                        firstColumn
                );


        for (int i = 1;
             i <= 5;
             i++) {

            ColumnConstraints column =
                    new ColumnConstraints();


            column.setPercentWidth(
                    20
            );


            grid.getColumnConstraints()
                    .add(
                            column
                    );
        }


        // --------------------------------------------------------
        // REAL APPOINTMENTS
        // --------------------------------------------------------

        addRealAppointments(
                grid
        );


        return grid;
    }


    // ============================================================
    // REAL APPOINTMENTS
    // ============================================================

    private void addRealAppointments(
            GridPane grid) {

        if (doctorAppointments == null) {
            return;
        }


        String searchText =
                searchInput == null
                        ? ""
                        : searchInput
                                .getText()
                                .trim()
                                .toLowerCase(
                                        Locale.ROOT
                                );


        LocalDate monday =
                displayedWeekMonday;


        for (Appointment appointment :
                doctorAppointments) {

            if (appointment == null) {
                continue;
            }


            String patientName =
                    safeText(
                            appointment.getPatientName(),
                            "Patient"
                    );


            String reason =
                    safeText(
                            appointment.getReason(),
                            "Consultation"
                    );


            /*
             * Search filter.
             */
            if (!searchText.isEmpty()) {

                String searchable =
                        (
                                patientName
                                        + " "
                                        + reason
                                        + " "
                                        + safeText(
                                                appointment.getDoctorName(),
                                                ""
                                        )
                        )
                                .toLowerCase(
                                        Locale.ROOT
                                );


                if (!searchable.contains(
                        searchText
                )) {

                    continue;
                }
            }


            LocalDate appointmentDate =
                    parseAppointmentDate(
                            appointment
                                    .getAppointmentDate()
                    );


            if (appointmentDate == null) {
                continue;
            }


            long dayDifference =
                    ChronoUnit.DAYS.between(
                            monday,
                            appointmentDate
                    );


            /*
             * The UI displays Monday-Friday.
             */
            if (dayDifference < 0
                    || dayDifference > 4) {

                continue;
            }


            int column =
                    (int) dayDifference + 1;


            String appointmentTime =
                    appointment.getAppointmentTime();


            if (appointmentTime == null
                    || appointmentTime
                            .trim()
                            .isEmpty()) {

                continue;
            }


            int row =
                    getCalendarRow(
                            appointmentTime
                    );


            if (row < 1) {

                /*
                 * Appointment outside the visible 8 AM-1 PM
                 * range.
                 */
                continue;
            }


            String status =
                    safeText(
                            appointment.getStatus(),
                            "Pending"
                    );


            String type =
                    safeText(
                            appointment.getBookingType(),
                            "DOCTOR"
                    );


            String text =
                    patientName
                            + "\n"
                            + appointmentTime
                            + "\n"
                            + status;


            String styleClass =
                    getAppointmentStyleClass(
                            type
                    );


            VBox block =
                    createCalendarBlock(
                            text,
                            styleClass
                    );


            grid.add(
                    block,
                    column,
                    row
            );
        }
    }


    // ============================================================
    // CALENDAR BLOCK
    // ============================================================

    private VBox createCalendarBlock(
            String text,
            String styleClass) {

        VBox block =
                new VBox();


        block.getStyleClass()
                .addAll(
                        "calendar-block",
                        styleClass
                );


        block.setPadding(
                new Insets(
                        8
                )
        );


        block.setStyle(
                "-fx-cursor: hand;"
        );


        Label label =
                new Label(
                        text
                );


        label
                .getStyleClass()
                .add(
                        "calendar-block-text"
                );


        label.setWrapText(
                true
        );


        label.setMouseTransparent(
                true
        );


        block
                .getChildren()
                .add(
                        label
                );


        block.setOnMouseClicked(
                e ->
                        showAppointmentDetails(
                                text
                        )
        );


        return block;
    }


    // ============================================================
    // APPOINTMENT STYLE
    // ============================================================

    private String getAppointmentStyleClass(
            String bookingType) {

        if (bookingType == null) {

            return "block-clinical";
        }


        if (bookingType
                .equalsIgnoreCase(
                        "HOSPITAL"
                )) {

            return "block-surgery";
        }


        return "block-clinical";
    }


    // ============================================================
    // TIME → GRID ROW
    // ============================================================

    private int getCalendarRow(
            String appointmentTime) {

        try {

            String normalized =
                    appointmentTime
                            .trim()
                            .toUpperCase(
                                    Locale.ENGLISH
                            );


            java.time.LocalTime time =
                    java.time.LocalTime.parse(
                            normalized,
                            APPOINTMENT_TIME_FORMAT
                    );


            int hour =
                    time.getHour();


            /*
             * Calendar starts at 8 AM.
             *
             * 8 AM  → row 1
             * 9 AM  → row 2
             * 10 AM → row 3
             * 11 AM → row 4
             * 12 PM → row 5
             * 1 PM  → row 6
             */
            if (hour < 8
                    || hour > 13) {

                return -1;
            }


            return hour - 7;

        } catch (Exception e) {

            /*
             * Some appointments may use formats such as:
             *
             * 10:00 AM
             * 10 AM
             *
             * Try the simpler format.
             */
            try {

                java.time.LocalTime time =
                        java.time.LocalTime.parse(
                                appointmentTime
                                        .trim()
                                        .toUpperCase(
                                                Locale.ENGLISH
                                        ),
                                DateTimeFormatter.ofPattern(
                                        "h a",
                                        Locale.ENGLISH
                                )
                        );


                int hour =
                        time.getHour();


                if (hour < 8
                        || hour > 13) {

                    return -1;
                }


                return hour - 7;

            } catch (Exception ignored) {

                return -1;
            }
        }
    }


    // ============================================================
    // DATE PARSER
    // ============================================================

    private LocalDate parseAppointmentDate(
            String date) {

        if (date == null
                || date.trim().isEmpty()) {

            return null;
        }


        String value =
                date.trim();


        /*
         * Normal project format:
         *
         * yyyy-MM-dd
         */
        try {

            return LocalDate.parse(
                    value,
                    APPOINTMENT_DATE_FORMAT
            );

        } catch (DateTimeParseException ignored) {
        }


        /*
         * Fallback for dd/MM/yyyy.
         */
        try {

            return LocalDate.parse(
                    value,
                    DateTimeFormatter.ofPattern(
                            "dd/MM/yyyy"
                    )
            );

        } catch (DateTimeParseException ignored) {
        }


        /*
         * Fallback for MM/dd/yyyy.
         */
        try {

            return LocalDate.parse(
                    value,
                    DateTimeFormatter.ofPattern(
                            "MM/dd/yyyy"
                    )
            );

        } catch (DateTimeParseException ignored) {
        }


        return null;
    }


    // ============================================================
    // DATE RANGE LABEL
    // ============================================================

    private void updateDateRangeLabel() {

        if (dateRangeLabel == null) {
            return;
        }


        LocalDate start =
                displayedWeekMonday;


        LocalDate end =
                start.plusDays(
                        4
                );


        String monthYear =
                start.format(
                        HEADER_MONTH_FORMAT
                );


        /*
         * If week crosses month boundary, display both months.
         */
        if (start.getMonth()
                != end.getMonth()) {

            monthYear =
                    start.format(
                            DateTimeFormatter.ofPattern(
                                    "MMM",
                                    Locale.ENGLISH
                            )
                    )
                            + " - "
                            + end.format(
                            DateTimeFormatter.ofPattern(
                                    "MMM yyyy",
                                    Locale.ENGLISH
                            )
                    );
        }


        long weekNumber =
                java.time.temporal.WeekFields
                        .of(
                                Locale.getDefault()
                        )
                        .weekOfYear()
                        .getFrom(
                                start
                        );


        dateRangeLabel.setText(
                monthYear
                        + ", Week "
                        + weekNumber
        );
    }


    // ============================================================
    // REFRESH CALENDAR
    // ============================================================

    private void refreshCalendar() {

        updateDateRangeLabel();


        if (scheduleGrid == null) {
            return;
        }


        GridPane newGrid =
                createScheduleGrid();


        /*
         * Replace the existing grid inside the card.
         */
        VBox parent =
                (VBox) scheduleGrid.getParent();


        int index =
                parent
                        .getChildren()
                        .indexOf(
                                scheduleGrid
                        );


        if (index >= 0) {

            parent
                    .getChildren()
                    .set(
                            index,
                            newGrid
                    );


            scheduleGrid =
                    newGrid;
        }
    }


    // ============================================================
    // EMPTY SLOT
    // ============================================================

    private void handleEmptyCalendarSlot(
            int column,
            int row) {

        if (column < 1
                || column > 5
                || row < 0) {

            return;
        }


        LocalDate selectedDate =
                displayedWeekMonday
                        .plusDays(
                                column - 1
                        );


        int hour =
                8 + row;


        String formattedTime =
                String.format(
                        Locale.ENGLISH,
                        "%d:00 %s",
                        hour > 12
                                ? hour - 12
                                : hour,
                        hour >= 12
                                ? "PM"
                                : "AM"
                );


        showInformationAlert(
                "Schedule Slot",
                "Date: "
                        + selectedDate
                        + "\nTime: "
                        + formattedTime
                        + "\n\nAppointment booking is handled by the Appointments module."
        );
    }


    // ============================================================
    // APPOINTMENT DETAILS
    // ============================================================

    private void showAppointmentDetails(
            String text) {

        String cleanText =
                text.replace(
                        "\n",
                        " • "
                );


        showInformationAlert(
                "Appointment",
                cleanText
        );
    }


    // ============================================================
    // AI ASSISTANT
    // ============================================================

    private VBox createAIAssistantCard() {

        VBox card =
                new VBox(12);


        card.getStyleClass()
                .add(
                        "ai-assistant-card"
                );


        card.setPadding(
                new Insets(
                        18
                )
        );


        HBox titleBox =
                new HBox(8);


        titleBox.setAlignment(
                Pos.CENTER_LEFT
        );


        ImageView aiIcon =
                new ImageView(
                        ResourceImage.load(
                                "/images/icons/ic_robot.png"
                        )
                );


        aiIcon.setFitWidth(18);
        aiIcon.setFitHeight(18);


        Label titleLbl =
                new Label(
                        "AI Scheduling Assistant"
                );


        titleLbl.getStyleClass()
                .add(
                        "ai-card-title"
                );


        titleBox
                .getChildren()
                .addAll(
                        aiIcon,
                        titleLbl
                );


        /*
         * Keep the AI text as presentation content.
         * It is not falsely presented as a Firebase-generated
         * recommendation.
         */
        Label suggestion =
                new Label(
                        "Review your weekly availability and real appointment load to optimize patient flow."
                );


        suggestion.setWrapText(
                true
        );


        suggestion
                .getStyleClass()
                .add(
                        "ai-card-desc"
                );


        Hyperlink optLink =
                new Hyperlink(
                        "View Optimization Suggestions →"
                );


        optLink
                .getStyleClass()
                .add(
                        "ai-card-link"
                );


        optLink.setOnAction(
                e ->
                        Navigation.goTo(
                                stage,
                                () ->
                                        new AIHealthAssistantView(
                                                stage
                                        ).getScene()
                        )
        );


        card
                .getChildren()
                .addAll(
                        titleBox,
                        suggestion,
                        optLink
                );


        return card;
    }


    // ============================================================
    // WORKING HOURS
    // ============================================================

    private VBox createWorkingHoursCard() {

        VBox card =
                new VBox(14);


        card.getStyleClass()
                .add(
                        "panel-card"
                );


        card.setPadding(
                new Insets(
                        18
                )
        );


        BorderPane header =
                new BorderPane();


        Label title =
                new Label(
                        "Working Hours"
                );


        title.getStyleClass()
                .add(
                        "card-title"
                );


        ImageView gearIcon =
                new ImageView(
                        ResourceImage.load(
                                "/images/icons/ic_settings.png"
                        )
                );


        gearIcon.setFitWidth(16);
        gearIcon.setFitHeight(16);


        gearIcon
                .getStyleClass()
                .add(
                        "clickable-icon"
                );


        gearIcon.setStyle(
                "-fx-cursor: hand;"
        );


        gearIcon.setOnMouseClicked(
                e ->
                        showInformationAlert(
                                "Working Hours",
                                "Change the working hours below and click Save Changes."
                        )
        );


        header.setLeft(
                title
        );


        header.setRight(
                gearIcon
        );


        VBox daysList =
                new VBox(10);


        daysList.getChildren()
                .add(
                        createWorkingDayRow(
                                "Mon",
                                currentSchedule
                                        .isMondayEnabled(),
                                currentSchedule
                                        .getMondayStartTime(),
                                currentSchedule
                                        .getMondayEndTime()
                        )
                );


        daysList.getChildren()
                .add(
                        createWorkingDayRow(
                                "Tue",
                                currentSchedule
                                        .isTuesdayEnabled(),
                                currentSchedule
                                        .getTuesdayStartTime(),
                                currentSchedule
                                        .getTuesdayEndTime()
                        )
                );


        daysList.getChildren()
                .add(
                        createWorkingDayRow(
                                "Wed",
                                currentSchedule
                                        .isWednesdayEnabled(),
                                currentSchedule
                                        .getWednesdayStartTime(),
                                currentSchedule
                                        .getWednesdayEndTime()
                        )
                );


        daysList.getChildren()
                .add(
                        createWorkingDayRow(
                                "Thu",
                                currentSchedule
                                        .isThursdayEnabled(),
                                currentSchedule
                                        .getThursdayStartTime(),
                                currentSchedule
                                        .getThursdayEndTime()
                        )
                );


        daysList.getChildren()
                .add(
                        createWorkingDayRow(
                                "Fri",
                                currentSchedule
                                        .isFridayEnabled(),
                                currentSchedule
                                        .getFridayStartTime(),
                                currentSchedule
                                        .getFridayEndTime()
                        )
                );


        // --------------------------------------------------------
        // APPOINTMENT SLOT
        // --------------------------------------------------------

        VBox slotBox =
                new VBox(6);


        slotBox.setPadding(
                new Insets(
                        10,
                        0,
                        0,
                        0
                )
        );


        Label slotLabel =
                new Label(
                        "Default Appointment Slot"
                );


        slotLabel.getStyleClass()
                .add(
                        "input-label"
                );


        slotCombo =
                new ComboBox<>();


        slotCombo
                .getItems()
                .addAll(
                        "15 Minutes",
                        "30 Minutes",
                        "45 Minutes",
                        "60 Minutes"
                );


        slotCombo.setValue(
                convertMinutesToSlot(
                        currentSchedule
                                .getDefaultAppointmentSlotMinutes()
                )
        );


        slotCombo.setMaxWidth(
                Double.MAX_VALUE
        );


        slotCombo
                .getStyleClass()
                .add(
                        "input-select"
                );


        slotBox
                .getChildren()
                .addAll(
                        slotLabel,
                        slotCombo
                );


        card
                .getChildren()
                .addAll(
                        header,
                        daysList,
                        slotBox
                );


        return card;
    }


    // ============================================================
    // WORKING DAY ROW
    // ============================================================

    private HBox createWorkingDayRow(
            String day,
            boolean isChecked,
            String startTime,
            String endTime) {

        HBox row =
                new HBox(6);


        row.setAlignment(
                Pos.CENTER_LEFT
        );


        CheckBox cb =
                new CheckBox(
                        day
                );


        cb.setSelected(
                isChecked
        );


        cb.getStyleClass()
                .add(
                        "day-checkbox"
                );


        cb.setPrefWidth(
                54
        );


        dayCheckBoxes.put(
                day,
                cb
        );


        ComboBox<String> startCombo =
                createTimeCombo(
                        startTime
                );


        ComboBox<String> endCombo =
                createTimeCombo(
                        endTime
                );


        startTimeCombos.put(
                day,
                startCombo
        );


        endTimeCombos.put(
                day,
                endCombo
        );


        Label sep =
                new Label(
                        "-"
                );


        sep.getStyleClass()
                .add(
                        "time-separator"
                );


        startCombo.setDisable(
                !isChecked
        );


        endCombo.setDisable(
                !isChecked
        );


        /*
         * When checkbox changes, enable/disable time selectors.
         */
        cb.selectedProperty()
                .addListener(
                        (obs, oldValue, newValue) -> {

                            startCombo.setDisable(
                                    !newValue
                            );

                            endCombo.setDisable(
                                    !newValue
                            );
                        }
                );


        row
                .getChildren()
                .addAll(
                        cb,
                        startCombo,
                        sep,
                        endCombo
                );


        return row;
    }


    // ============================================================
    // TIME COMBO
    // ============================================================

    private ComboBox<String> createTimeCombo(
            String selectedTime) {

        ComboBox<String> combo =
                new ComboBox<>();


        combo
                .getItems()
                .addAll(
                        "08:00 AM",
                        "08:30 AM",
                        "09:00 AM",
                        "09:30 AM",
                        "10:00 AM",
                        "10:30 AM",
                        "11:00 AM",
                        "11:30 AM",
                        "12:00 PM",
                        "12:30 PM",
                        "01:00 PM",
                        "01:30 PM",
                        "02:00 PM",
                        "02:30 PM",
                        "03:00 PM",
                        "03:30 PM",
                        "04:00 PM",
                        "04:30 PM",
                        "05:00 PM",
                        "05:30 PM",
                        "06:00 PM",
                        "06:30 PM",
                        "07:00 PM",
                        "07:30 PM",
                        "08:00 PM"
                );


        String value =
                selectedTime;


        if (value != null
                && combo
                        .getItems()
                        .contains(
                                value
                        )) {

            combo.setValue(
                    value
            );

        } else {

            combo.setValue(
                    "09:00 AM"
            );
        }


        combo
                .getStyleClass()
                .add(
                        "time-select"
                );


        combo.setPrefWidth(
                95
        );


        return combo;
    }


    // ============================================================
    // EMERGENCY CARD
    // ============================================================

    private VBox createEmergencyCard() {

        VBox card =
                new VBox(8);


        card.getStyleClass()
                .add(
                        "panel-card"
                );


        card.setPadding(
                new Insets(
                        16
                )
        );


        BorderPane header =
                new BorderPane();


        HBox left =
                new HBox(6);


        left.setAlignment(
                Pos.CENTER_LEFT
        );


        Label aster =
                new Label(
                        "✻"
                );


        aster.getStyleClass()
                .add(
                        "emergency-asterisk"
                );


        Label title =
                new Label(
                        "Emergency Availability"
                );


        title.getStyleClass()
                .add(
                        "card-title"
                );


        left
                .getChildren()
                .addAll(
                        aster,
                        title
                );


        emergencyToggle =
                new ToggleButton();


        emergencyToggle.setSelected(
                currentSchedule
                        .isEmergencyAvailable()
        );


        emergencyToggle
                .getStyleClass()
                .add(
                        "switch-toggle"
                );


        header.setLeft(
                left
        );


        header.setRight(
                emergencyToggle
        );


        Label desc =
                new Label(
                        "Accept urgent cases outside regular slots."
                );


        desc.getStyleClass()
                .add(
                        "emergency-desc"
                );


        card
                .getChildren()
                .addAll(
                        header,
                        desc
                );


        return card;
    }


    // ============================================================
    // SAVE SCHEDULE
    // ============================================================

    private void saveSchedule() {

        try {

            updateScheduleFromUI();


            scheduleController
                    .saveDoctorSchedule(
                            currentSchedule
                    );


            /*
             * Reload from Firestore after save.
             *
             * This verifies that the UI now represents the
             * persisted backend state.
             */
            currentSchedule =
                    scheduleController
                            .getDoctorSchedule(
                                    doctorUid
                            );


            showInformationAlert(
                    "Schedule Saved",
                    "Your working hours, appointment slot, and emergency availability have been saved successfully."
            );


            refreshCalendar();


        } catch (Exception e) {

            e.printStackTrace();


            showErrorAlert(
                    "Save Failed",
                    getRootErrorMessage(
                            e
                    )
            );
        }
    }


    // ============================================================
    // UI → MODEL
    // ============================================================

    private void updateScheduleFromUI() {

        currentSchedule.setDoctorUid(
                doctorUid
        );


        // Monday
        currentSchedule.setMondayEnabled(
                dayCheckBoxes
                        .get("Mon")
                        .isSelected()
        );


        currentSchedule.setMondayStartTime(
                startTimeCombos
                        .get("Mon")
                        .getValue()
        );


        currentSchedule.setMondayEndTime(
                endTimeCombos
                        .get("Mon")
                        .getValue()
        );


        // Tuesday
        currentSchedule.setTuesdayEnabled(
                dayCheckBoxes
                        .get("Tue")
                        .isSelected()
        );


        currentSchedule.setTuesdayStartTime(
                startTimeCombos
                        .get("Tue")
                        .getValue()
        );


        currentSchedule.setTuesdayEndTime(
                endTimeCombos
                        .get("Tue")
                        .getValue()
        );


        // Wednesday
        currentSchedule.setWednesdayEnabled(
                dayCheckBoxes
                        .get("Wed")
                        .isSelected()
        );


        currentSchedule.setWednesdayStartTime(
                startTimeCombos
                        .get("Wed")
                        .getValue()
        );


        currentSchedule.setWednesdayEndTime(
                endTimeCombos
                        .get("Wed")
                        .getValue()
        );


        // Thursday
        currentSchedule.setThursdayEnabled(
                dayCheckBoxes
                        .get("Thu")
                        .isSelected()
        );


        currentSchedule.setThursdayStartTime(
                startTimeCombos
                        .get("Thu")
                        .getValue()
        );


        currentSchedule.setThursdayEndTime(
                endTimeCombos
                        .get("Thu")
                        .getValue()
        );


        // Friday
        currentSchedule.setFridayEnabled(
                dayCheckBoxes
                        .get("Fri")
                        .isSelected()
        );


        currentSchedule.setFridayStartTime(
                startTimeCombos
                        .get("Fri")
                        .getValue()
        );


        currentSchedule.setFridayEndTime(
                endTimeCombos
                        .get("Fri")
                        .getValue()
        );


        // Appointment slot
        currentSchedule.setDefaultAppointmentSlotMinutes(
                convertSlotToMinutes(
                        slotCombo.getValue()
                )
        );


        // Emergency
        currentSchedule.setEmergencyAvailable(
                emergencyToggle.isSelected()
        );
    }


    // ============================================================
    // SLOT CONVERSION
    // ============================================================

    private int convertSlotToMinutes(
            String slot) {

        if (slot == null) {
            return 30;
        }


        switch (slot) {

            case "15 Minutes":
                return 15;

            case "30 Minutes":
                return 30;

            case "45 Minutes":
                return 45;

            case "60 Minutes":
                return 60;

            default:
                return 30;
        }
    }


    private String convertMinutesToSlot(
            int minutes) {

        switch (minutes) {

            case 15:
                return "15 Minutes";

            case 30:
                return "30 Minutes";

            case 45:
                return "45 Minutes";

            case 60:
                return "60 Minutes";

            default:
                return "30 Minutes";
        }
    }


    // ============================================================
    // EXPORT
    // ============================================================

    private void exportSchedule() {

        /*
         * Export currently creates a readable text summary.
         *
         * It does not invent data and uses the values currently
         * loaded from Firestore.
         */
        StringBuilder builder =
                new StringBuilder();


        builder.append(
                "Health-Sphere Doctor Schedule\n"
        );


        builder.append(
                "Doctor UID: "
        );


        builder.append(
                doctorUid
        );


        builder.append(
                "\n\nWorking Hours\n"
        );


        appendScheduleLine(
                builder,
                "Monday",
                currentSchedule.isMondayEnabled(),
                currentSchedule.getMondayStartTime(),
                currentSchedule.getMondayEndTime()
        );


        appendScheduleLine(
                builder,
                "Tuesday",
                currentSchedule.isTuesdayEnabled(),
                currentSchedule.getTuesdayStartTime(),
                currentSchedule.getTuesdayEndTime()
        );


        appendScheduleLine(
                builder,
                "Wednesday",
                currentSchedule.isWednesdayEnabled(),
                currentSchedule.getWednesdayStartTime(),
                currentSchedule.getWednesdayEndTime()
        );


        appendScheduleLine(
                builder,
                "Thursday",
                currentSchedule.isThursdayEnabled(),
                currentSchedule.getThursdayStartTime(),
                currentSchedule.getThursdayEndTime()
        );


        appendScheduleLine(
                builder,
                "Friday",
                currentSchedule.isFridayEnabled(),
                currentSchedule.getFridayStartTime(),
                currentSchedule.getFridayEndTime()
        );


        builder.append(
                "\nDefault Appointment Slot: "
        );


        builder.append(
                currentSchedule
                        .getDefaultAppointmentSlotMinutes()
        );


        builder.append(
                " Minutes"
        );


        builder.append(
                "\nEmergency Availability: "
        );


        builder.append(
                currentSchedule
                        .isEmergencyAvailable()
                        ? "Enabled"
                        : "Disabled"
        );


        builder.append(
                "\n\nDisplayed Week: "
        );


        builder.append(
                displayedWeekMonday
        );


        builder.append(
                " to "
        );


        builder.append(
                displayedWeekMonday
                        .plusDays(4)
        );


        builder.append(
                "\nAppointments Displayed: "
        );


        builder.append(
                getAppointmentsForDisplayedWeek()
                        .size()
        );


        TextArea area =
                new TextArea(
                        builder.toString()
                );


        area.setEditable(
                false
        );


        area.setWrapText(
                true
        );


        area.setPrefRowCount(
                18
        );


        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );


        alert.setTitle(
                "Schedule Export"
        );


        alert.setHeaderText(
                "Schedule Summary"
        );


        alert.getDialogPane()
                .setContent(
                        area
                );


        alert.showAndWait();
    }


    private void appendScheduleLine(
            StringBuilder builder,
            String day,
            boolean enabled,
            String start,
            String end) {

        builder.append(
                day
        );


        builder.append(
                ": "
        );


        if (!enabled) {

            builder.append(
                    "Off Duty"
            );

        } else {

            builder.append(
                    start
            );

            builder.append(
                    " - "
            );

            builder.append(
                    end
            );
        }


        builder.append(
                "\n"
        );
    }


    // ============================================================
    // DISPLAYED WEEK APPOINTMENTS
    // ============================================================

    private List<Appointment> getAppointmentsForDisplayedWeek() {

        List<Appointment> result =
                new ArrayList<>();


        LocalDate monday =
                displayedWeekMonday;


        for (Appointment appointment :
                doctorAppointments) {

            if (appointment == null) {
                continue;
            }


            LocalDate date =
                    parseAppointmentDate(
                            appointment
                                    .getAppointmentDate()
                    );


            if (date == null) {
                continue;
            }


            long difference =
                    ChronoUnit.DAYS.between(
                            monday,
                            date
                    );


            if (difference >= 0
                    && difference <= 4) {

                result.add(
                        appointment
                );
            }
        }


        return result;
    }


    // ============================================================
    // TEXT HELPERS
    // ============================================================

    private String safeText(
            String value,
            String fallback) {

        if (value == null
                || value.trim().isEmpty()) {

            return fallback;
        }


        return value.trim();
    }


    private String getRootErrorMessage(
            Throwable throwable) {

        Throwable current =
                throwable;


        while (current.getCause() != null) {

            current =
                    current.getCause();
        }


        if (current.getMessage() == null
                || current.getMessage()
                        .trim()
                        .isEmpty()) {

            return "An unexpected error occurred.";
        }


        return current.getMessage();
    }


    // ============================================================
    // ALERTS
    // ============================================================

    private void showInformationAlert(
            String title,
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );


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


    private void showErrorAlert(
            String title,
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );


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
}