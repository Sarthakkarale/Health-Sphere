package com.healthsphere.view.doctor;

import com.healthsphere.controller.appointment.AppointmentController;
import com.healthsphere.dao.authentication.PatientDAO;
import com.healthsphere.model.Appointment;
import com.healthsphere.model.PatientProfile;
import com.healthsphere.util.Navigation;
import com.healthsphere.util.ResourceImage;
import com.healthsphere.util.SessionManager;

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
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * TodaysScheduleView
 *
 * Dynamic Doctor Today's Schedule.
 *
 * Architecture:
 *
 * JavaFX View
 *      ↓
 * AppointmentController
 *      ↓
 * AppointmentDAO
 *      ↓
 * Firestore
 *
 * Patient information:
 *
 * JavaFX View
 *      ↓
 * PatientDAO
 *      ↓
 * Firestore
 */
public class TodaysScheduleView {

    // ============================================================
    // BASIC
    // ============================================================

    private final Stage stage;
    private final Scene scene;


    // ============================================================
    // CONTROLLERS / DAO
    // ============================================================

    private final AppointmentController appointmentController;
    private final PatientDAO patientDAO;


    // ============================================================
    // DATA
    // ============================================================

    private List<Appointment> doctorAppointments =
            new ArrayList<>();


    private List<Appointment> selectedDateAppointments =
            new ArrayList<>();


    private Appointment currentPatientAppointment;


    // ============================================================
    // DATE STATE
    // ============================================================

    private LocalDate currentDate =
            LocalDate.now();


    private LocalDate selectedCalendarDate =
            LocalDate.now();


    // ============================================================
    // UI REFERENCES
    // ============================================================

    private Label dateTitleLabel;

    private Label appointmentSubtitleLabel;

    private Label monthLabel;

    private GridPane calendarGrid;

    private VBox timelineCard;

    private VBox currentPatientCardContainer;

    private VBox queueCardContainer;

    private HBox criticalAlertContainer;


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public TodaysScheduleView(Stage stage) {

        this.stage = stage;

        this.appointmentController =
                new AppointmentController();

        this.patientDAO =
                new PatientDAO();


        loadDoctorAppointments();


        this.scene =
                createScene();
    }


    // ============================================================
    // GET SCENE
    // ============================================================

    public Scene getScene() {

        return scene;
    }


    // ============================================================
    // LOAD DOCTOR APPOINTMENTS
    // ============================================================

    private void loadDoctorAppointments() {

        doctorAppointments =
                new ArrayList<>();


        String doctorUid =
                getCurrentDoctorUid();


        if (doctorUid == null
                || doctorUid.trim().isEmpty()) {

            System.err.println(
                    "Unable to load Today's Schedule: "
                            + "No logged-in doctor UID."
            );

            return;
        }


        try {

            List<Appointment> appointments =
                    appointmentController
                            .getDoctorAppointments(
                                    doctorUid
                            );


            if (appointments != null) {

                doctorAppointments.addAll(
                        appointments
                );
            }


            doctorAppointments.sort(
                    Comparator.comparing(
                            this::getAppointmentDateTime
                    )
            );


            System.out.println(
                    "Today's Schedule:"
            );


            System.out.println(
                    "Doctor UID: "
                            + doctorUid
            );


            System.out.println(
                    "Total appointments: "
                            + doctorAppointments.size()
            );


        } catch (Exception e) {

            System.err.println(
                    "Unable to load doctor appointments: "
                            + getRootMessage(e)
            );
        }
    }


    // ============================================================
    // CREATE SCENE
    // ============================================================

    private Scene createScene() {

        BorderPane mainRoot =
                new BorderPane();

        mainRoot.getStyleClass()
                .add(
                        "root-pane"
                );


        // --------------------------------------------------------
        // SIDEBAR
        // --------------------------------------------------------

        VBox sidebar =
                createSidebar();


        mainRoot.setLeft(
                sidebar
        );


        // --------------------------------------------------------
        // CENTER
        // --------------------------------------------------------

        VBox centerLayout =
                new VBox(20);


        centerLayout.setPadding(
                new Insets(
                        20,
                        25,
                        20,
                        25
                )
        );


        centerLayout.getStyleClass()
                .add(
                        "content-area"
                );


        // Header
        centerLayout.getChildren()
                .add(
                        createTopHeader()
                );


        // Main columns
        HBox mainGrid =
                new HBox(20);


        VBox leftColumn =
                createScheduleTimelineColumn();


        HBox.setHgrow(
                leftColumn,
                Priority.ALWAYS
        );


        VBox rightColumn =
                createSideCardsColumn();


        rightColumn.setMinWidth(
                320
        );


        rightColumn.setMaxWidth(
                340
        );


        mainGrid.getChildren()
                .addAll(
                        leftColumn,
                        rightColumn
                );


        centerLayout.getChildren()
                .add(
                        mainGrid
                );


        // --------------------------------------------------------
        // SCROLLPANE
        // --------------------------------------------------------

        ScrollPane contentScrollPane =
                new ScrollPane(
                        centerLayout
                );


        contentScrollPane.setFitToWidth(
                true
        );


        contentScrollPane.setFitToHeight(
                false
        );


        contentScrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );


        contentScrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );


        contentScrollPane.setPannable(
                true
        );


        contentScrollPane.getStyleClass()
                .add(
                        "content-scrollpane"
                );


        mainRoot.setCenter(
                contentScrollPane
        );


        // --------------------------------------------------------
        // SCENE
        // --------------------------------------------------------

        Scene scheduleScene =
                new Scene(
                        mainRoot,
                        stage.getWidth(),
                        stage.getHeight()
                );


        try {

            scheduleScene
                    .getStylesheets()
                    .add(
                            Objects.requireNonNull(
                                    getClass()
                                            .getResource(
                                                    "/css/todays_schedule.css"
                                            )
                            ).toExternalForm()
                    );

        } catch (Exception e) {

            System.err.println(
                    "todays_schedule.css not found."
            );
        }


        return scheduleScene;
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


        sidebar.getStyleClass()
                .add(
                        "sidebar"
                );


        sidebar.setStyle(
                "-fx-background-color: #0F172A;"
        );


        sidebar.setMinWidth(
                260
        );


        sidebar.setPrefWidth(
                260
        );


        sidebar.setMaxWidth(
                260
        );


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


        logoIconBox.getStyleClass()
                .add(
                        "logo-icon-box"
                );


        logoIconBox.setStyle(
                "-fx-background-color: #3B82F6;"
                        + "-fx-background-radius: 8px;"
                        + "-fx-padding: 8px;"
        );


        ImageView logoIcon =
                createImageView(
                        "/images/icons/ic_shield.png",
                        20,
                        20
                );


        if (logoIcon != null) {

            logoIconBox.getChildren()
                    .add(
                            logoIcon
                    );
        }


        VBox logoText =
                new VBox(2);


        Label appName =
                new Label(
                        "Health-Sphere"
                );


        appName.getStyleClass()
                .add(
                        "logo-name"
                );


        appName.setStyle(
                "-fx-text-fill: #FFFFFF;"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-size: 16px;"
        );


        Label doctorSubtext =
                new Label(
                        "Doctor Dashboard"
                );


        doctorSubtext.getStyleClass()
                .add(
                        "logo-subtext"
                );


        doctorSubtext.setStyle(
                "-fx-text-fill: #94A3B8;"
                        + "-fx-font-size: 12px;"
        );


        logoText.getChildren()
                .addAll(
                        appName,
                        doctorSubtext
                );


        logoSection.getChildren()
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

            final int tabIndex =
                    i;


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


            navTab.getStyleClass()
                    .add(
                            "nav-tab"
                    );


            ImageView icon =
                    createImageView(
                            "/images/icons/"
                                    + icons[i]
                                    + ".png",
                            18,
                            18
                    );


            Label tabLabel =
                    new Label(
                            tabs[i]
                    );


            tabLabel.getStyleClass()
                    .add(
                            "nav-text"
                    );


            if (i == 1) {

                navTab.getStyleClass()
                        .add(
                                "nav-tab-active"
                        );


                navTab.setStyle(
                        "-fx-background-color: #3B82F6;"
                                + "-fx-background-radius: 8px;"
                );


                tabLabel.setStyle(
                        "-fx-text-fill: #FFFFFF;"
                                + "-fx-font-weight: bold;"
                                + "-fx-font-size: 14px;"
                );

            } else {

                navTab.setStyle(
                        "-fx-background-color: transparent;"
                                + "-fx-background-radius: 8px;"
                );


                tabLabel.setStyle(
                        "-fx-text-fill: #94A3B8;"
                                + "-fx-font-size: 14px;"
                );
            }


            if (icon != null) {

                navTab.getChildren()
                        .add(
                                icon
                        );
            }


            navTab.getChildren()
                    .add(
                            tabLabel
                    );


            navTab.setOnMouseClicked(
                    event ->
                            handleSidebarTabClick(
                                    tabIndex
                            )
            );


            navItems.getChildren()
                    .add(
                            navTab
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


        sidebarProfile.getStyleClass()
                .add(
                        "sidebar-profile-box"
                );


        sidebarProfile.setStyle(
                "-fx-background-color: #1E293B;"
                        + "-fx-background-radius: 10px;"
                        + "-fx-cursor: hand;"
        );


        ImageView profileAvatar =
                createImageView(
                        "/images/doctor/portrait-3d-male-doctor.png",
                        36,
                        36
                );


        if (profileAvatar != null) {

            Circle profileClip =
                    new Circle(
                            18,
                            18,
                            18
                    );


            profileAvatar.setClip(
                    profileClip
            );
        }


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
                        getDoctorDisplayName()
                );


        profName.getStyleClass()
                .add(
                        "sidebar-profile-name"
                );


        profName.setStyle(
                "-fx-text-fill: #FFFFFF;"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-size: 13px;"
        );


        profileTexts.getChildren()
                .addAll(
                        profSubText,
                        profName
                );


        if (profileAvatar != null) {

            sidebarProfile.getChildren()
                    .add(
                            profileAvatar
                    );
        }


        sidebarProfile.getChildren()
                .add(
                        profileTexts
                );


        sidebarProfile.setOnMouseClicked(
                event ->
                        handleSidebarTabClick(
                                6
                        )
        );


        // --------------------------------------------------------
        // LOGOUT
        // --------------------------------------------------------

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


        logoutTab.getStyleClass()
                .add(
                        "nav-tab"
                );


        ImageView logoutIcon =
                createImageView(
                        "/images/icons/ic_logout.png",
                        18,
                        18
                );


        Label logoutLabel =
                new Label(
                        "Logout"
                );


        logoutLabel.getStyleClass()
                .add(
                        "nav-text"
                );


        logoutLabel.setStyle(
                "-fx-text-fill: #94A3B8;"
                        + "-fx-font-size: 14px;"
        );


        if (logoutIcon != null) {

            logoutTab.getChildren()
                    .add(
                            logoutIcon
                    );
        }


        logoutTab.getChildren()
                .add(
                        logoutLabel
                );


        logoutTab.setOnMouseClicked(
                event ->
                        handleLogout()
        );


        footer.getChildren()
                .addAll(
                        sidebarProfile,
                        logoutTab
                );


        sidebar.getChildren()
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
            int index
    ) {

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
    // LOGOUT
    // ============================================================

    private void handleLogout() {

        try {

            SessionManager
                    .getInstance()
                    .clearSession();


            showInformationAlert(
                    "Logout",
                    "Logged out successfully."
            );

        } catch (Exception e) {

            showInformationAlert(
                    "Logout Error",
                    "Unable to clear the current session."
            );
        }
    }


    // ============================================================
    // TOP HEADER
    // ============================================================

    private BorderPane createTopHeader() {

        BorderPane header =
                new BorderPane();


        HBox breadcrumbBox =
                new HBox(8);


        breadcrumbBox.setAlignment(
                Pos.CENTER_LEFT
        );


        Label parentLabel =
                new Label(
                        "Health-Sphere"
                );


        parentLabel.getStyleClass()
                .add(
                        "breadcrumb-parent"
                );


        parentLabel.setOnMouseClicked(
                e ->
                        Navigation.goTo(
                                stage,
                                () ->
                                        new DoctorDashboardView(
                                                stage
                                        ).getScene()
                        )
        );


        Label separator =
                new Label(
                        ">"
                );


        separator.getStyleClass()
                .add(
                        "breadcrumb-separator"
                );


        Label currentLabel =
                new Label(
                        "Today's Schedule"
                );


        currentLabel.getStyleClass()
                .add(
                        "breadcrumb-current"
                );


        breadcrumbBox.getChildren()
                .addAll(
                        parentLabel,
                        separator,
                        currentLabel
                );


        HBox rightControls =
                new HBox(18);


        rightControls.setAlignment(
                Pos.CENTER_RIGHT
        );


        ImageView searchBtn =
                createImageView(
                        "/images/icons/ic_search.png",
                        18,
                        18
                );


        if (searchBtn != null) {

            searchBtn.getStyleClass()
                    .add(
                            "clickable-icon"
                    );


            searchBtn.setOnMouseClicked(
                    e ->
                            showSearchDialog()
            );
        }


        StackPane notificationBox =
                new StackPane();


        ImageView bellIcon =
                createImageView(
                        "/images/icons/ic_bell.png",
                        18,
                        18
                );


        Circle badge =
                new Circle(
                        4,
                        Color.RED
                );


        StackPane.setAlignment(
                badge,
                Pos.TOP_RIGHT
        );


        if (bellIcon != null) {

            notificationBox.getChildren()
                    .add(
                            bellIcon
                    );
        }


        notificationBox.getChildren()
                .add(
                        badge
                );


        notificationBox.getStyleClass()
                .add(
                        "clickable-icon"
                );


        notificationBox.setOnMouseClicked(
                e ->
                        showNotificationDialog()
        );


        ImageView userAvatar =
                createImageView(
                        "/images/mocks/dr_sarah_avatar.png",
                        32,
                        32
                );


        if (userAvatar != null) {

            Circle clip =
                    new Circle(
                            16,
                            16,
                            16
                    );


            userAvatar.setClip(
                    clip
            );


            userAvatar.getStyleClass()
                    .add(
                            "clickable-icon"
                    );


            userAvatar.setOnMouseClicked(
                    e ->
                            Navigation.goTo(
                                    stage,
                                    () ->
                                            new DoctorProfileView(
                                                    stage
                                            ).getScene()
                            )
            );
        }


        if (searchBtn != null) {

            rightControls.getChildren()
                    .add(
                            searchBtn
                    );
        }


        rightControls.getChildren()
                .add(
                        notificationBox
                );


        if (userAvatar != null) {

            rightControls.getChildren()
                    .add(
                            userAvatar
                    );
        }


        header.setLeft(
                breadcrumbBox
        );


        header.setRight(
                rightControls
        );


        return header;
    }


    // ============================================================
    // MAIN SCHEDULE COLUMN
    // ============================================================

    private VBox createScheduleTimelineColumn() {

        VBox container =
                new VBox(15);


        // --------------------------------------------------------
        // DATE HEADER
        // --------------------------------------------------------

        BorderPane dateHeader =
                new BorderPane();


        VBox dateTextGroup =
                new VBox(2);


        dateTitleLabel =
                new Label(
                        formatDateTitle(
                                currentDate
                        )
                );


        dateTitleLabel.getStyleClass()
                .add(
                        "date-title"
                );


        appointmentSubtitleLabel =
                new Label();


        appointmentSubtitleLabel
                .getStyleClass()
                .add(
                        "date-subtitle"
                );


        updateDateSubtitle();


        dateTextGroup.getChildren()
                .addAll(
                        dateTitleLabel,
                        appointmentSubtitleLabel
                );


        HBox navButtons =
                new HBox(8);


        Button prevBtn =
                new Button(
                        "<"
                );


        prevBtn.getStyleClass()
                .add(
                        "btn-date-nav"
                );


        prevBtn.setOnAction(
                e ->
                        updateCurrentDate(
                                currentDate.minusDays(
                                        1
                                )
                        )
        );


        Button todayBtn =
                new Button(
                        "Today"
                );


        todayBtn.setMinWidth(
                70
        );


        todayBtn.getStyleClass()
                .add(
                        "btn-date-today"
                );


        todayBtn.setOnAction(
                e ->
                        updateCurrentDate(
                                LocalDate.now()
                        )
        );


        Button nextBtn =
                new Button(
                        ">"
                );


        nextBtn.getStyleClass()
                .add(
                        "btn-date-nav"
                );


        nextBtn.setOnAction(
                e ->
                        updateCurrentDate(
                                currentDate.plusDays(
                                        1
                                )
                        )
        );


        navButtons.getChildren()
                .addAll(
                        prevBtn,
                        todayBtn,
                        nextBtn
                );


        dateHeader.setLeft(
                dateTextGroup
        );


        dateHeader.setRight(
                navButtons
        );


        // --------------------------------------------------------
        // CRITICAL ALERT
        // --------------------------------------------------------

        criticalAlertContainer =
                createCriticalAlert();


        // --------------------------------------------------------
        // TIMELINE
        // --------------------------------------------------------

        timelineCard =
                new VBox(15);


        timelineCard.getStyleClass()
                .add(
                        "timeline-card"
                );


        timelineCard.setPadding(
                new Insets(
                        20
                )
        );


        refreshTimeline();


        container.getChildren()
                .add(
                        dateHeader
                );


        if (criticalAlertContainer != null) {

            container.getChildren()
                    .add(
                            criticalAlertContainer
                    );
        }


        container.getChildren()
                .add(
                        timelineCard
                );


        return container;
    }


    // ============================================================
    // CRITICAL ALERT
    // ============================================================

    private HBox createCriticalAlert() {

        Appointment priorityAppointment =
                findPriorityAppointment();


        if (priorityAppointment == null) {

            return null;
        }


        HBox alert =
                new HBox(15);


        alert.getStyleClass()
                .add(
                        "critical-alert-box"
                );


        alert.setAlignment(
                Pos.CENTER_LEFT
        );


        StackPane alertIconContainer =
                new StackPane();


        ImageView alertIcon =
                createImageView(
                        "/images/icons/ic_alert_red.png",
                        20,
                        20
                );


        if (alertIcon != null) {

            alertIconContainer.getChildren()
                    .add(
                            alertIcon
                    );
        }


        VBox alertContent =
                new VBox(3);


        Label alertTitle =
                new Label(
                        "Priority Alert"
                );


        alertTitle.getStyleClass()
                .add(
                        "alert-title"
                );


        String patientName =
                safeText(
                        priorityAppointment
                                .getPatientName(),
                        "Patient"
                );


        String reason =
                safeText(
                        priorityAppointment
                                .getReason(),
                        "Medical consultation"
                );


        String time =
                safeText(
                        priorityAppointment
                                .getAppointmentTime(),
                        ""
                );


        Label alertDesc =
                new Label(
                        patientName
                                + " ("
                                + time
                                + ") — "
                                + reason
                );


        alertDesc.getStyleClass()
                .add(
                        "alert-desc"
                );


        alertDesc.setWrapText(
                true
        );


        alertContent.getChildren()
                .addAll(
                        alertTitle,
                        alertDesc
                );


        alert.getChildren()
                .addAll(
                        alertIconContainer,
                        alertContent
                );


        alert.setOnMouseClicked(
                e ->
                        openPatient(
                                priorityAppointment
                        )
        );


        return alert;
    }


    // ============================================================
    // REFRESH TIMELINE
    // ============================================================

    private void refreshTimeline() {

        if (timelineCard == null) {

            return;
        }


        timelineCard.getChildren()
                .clear();


        selectedDateAppointments =
                getAppointmentsForDate(
                        currentDate
                );


        /*
         * No appointments.
         */
        if (selectedDateAppointments.isEmpty()) {

            VBox emptyBox =
                    new VBox(8);


            emptyBox.setAlignment(
                    Pos.CENTER
            );


            emptyBox.setPadding(
                    new Insets(
                            40
                    )
            );


            Label emptyTitle =
                    new Label(
                            "No appointments"
                    );


            emptyTitle.setStyle(
                    "-fx-font-size: 16px;"
                            + "-fx-font-weight: bold;"
                            + "-fx-text-fill: #64748B;"
            );


            Label emptyText =
                    new Label(
                            "There are no appointments scheduled for "
                                    + formatDateTitle(
                                    currentDate
                            )
                    );


            emptyText.setStyle(
                    "-fx-text-fill: #94A3B8;"
            );


            emptyBox.getChildren()
                    .addAll(
                            emptyTitle,
                            emptyText
                    );


            timelineCard.getChildren()
                    .add(
                            emptyBox
                    );


            updateDateSubtitle();


            return;
        }


        /*
         * Add appointments.
         */
        for (Appointment appointment :
                selectedDateAppointments) {

            boolean active =
                    isCurrentAppointment(
                            appointment
                    );


            timelineCard.getChildren()
                    .add(
                            createTimelineAppointment(
                                    appointment,
                                    active
                            )
                    );
        }


        updateDateSubtitle();
    }


    // ============================================================
    // TIMELINE APPOINTMENT
    // ============================================================

    private HBox createTimelineAppointment(
            Appointment appointment,
            boolean isActive
    ) {

        HBox row =
                new HBox(15);


        row.setAlignment(
                Pos.CENTER_LEFT
        );


        Label timeLabel =
                new Label(
                        safeText(
                                appointment
                                        .getAppointmentTime(),
                                "Time N/A"
                        )
                );


        timeLabel.getStyleClass()
                .add(
                        "slot-time-label"
                );


        timeLabel.setMinWidth(
                70
        );


        HBox card =
                new HBox();


        HBox.setHgrow(
                card,
                Priority.ALWAYS
        );


        card.setPadding(
                new Insets(
                        12,
                        15,
                        12,
                        15
                )
        );


        String status =
                safeText(
                        appointment.getStatus(),
                        "PENDING"
                );


        String patientName =
                safeText(
                        appointment.getPatientName(),
                        "Unknown Patient"
                );


        String reason =
                safeText(
                        appointment.getReason(),
                        "Consultation"
                );


        if (isActive) {

            card.getStyleClass()
                    .add(
                            "slot-card-active"
                    );


            VBox info =
                    new VBox(4);


            Label nameLbl =
                    new Label(
                            patientName
                    );


            nameLbl.getStyleClass()
                    .add(
                            "slot-card-active-title"
                    );


            Label detailLbl =
                    new Label(
                            reason
                    );


            detailLbl.getStyleClass()
                    .add(
                            "slot-card-active-sub"
                    );


            HBox metaBox =
                    new HBox(15);


            metaBox.setPadding(
                    new Insets(
                            5,
                            0,
                            0,
                            0
                    )
            );


            Label timeMeta =
                    new Label(
                            "🕒 "
                                    + safeText(
                                    appointment
                                            .getAppointmentTime(),
                                    ""
                            )
                    );


            timeMeta.getStyleClass()
                    .add(
                            "slot-card-active-meta"
                    );


            Label roomMeta =
                    new Label(
                            getRoomText(
                                    appointment
                            )
                    );


            roomMeta.getStyleClass()
                    .add(
                            "slot-card-active-meta"
                    );


            metaBox.getChildren()
                    .addAll(
                            timeMeta,
                            roomMeta
                    );


            info.getChildren()
                    .addAll(
                            nameLbl,
                            detailLbl,
                            metaBox
                    );


            Region spacer =
                    new Region();


            HBox.setHgrow(
                    spacer,
                    Priority.ALWAYS
            );


            Label statusPill =
                    new Label(
                            formatStatus(
                                    status
                            )
                    );


            statusPill.getStyleClass()
                    .add(
                            "pill-in-room"
                    );


            card.getChildren()
                    .addAll(
                            info,
                            spacer,
                            statusPill
                    );


        } else {

            card.getStyleClass()
                    .add(
                            "slot-card-standard"
                    );


            VBox info =
                    new VBox(2);


            Label nameLbl =
                    new Label(
                            patientName
                    );


            nameLbl.getStyleClass()
                    .add(
                            "Completed".equalsIgnoreCase(
                                    status
                            )
                                    ? "slot-card-completed-title"
                                    : "slot-card-title"
                    );


            Label detailLbl =
                    new Label(
                            reason
                    );


            detailLbl.getStyleClass()
                    .add(
                            "slot-card-sub"
                    );


            detailLbl.setWrapText(
                    true
            );


            info.getChildren()
                    .addAll(
                            nameLbl,
                            detailLbl
                    );


            Region spacer =
                    new Region();


            HBox.setHgrow(
                    spacer,
                    Priority.ALWAYS
            );


            Label statusPill =
                    new Label(
                            formatStatus(
                                    status
                            )
                    );


            statusPill.getStyleClass()
                    .add(
                            getStatusStyleClass(
                                    status
                            )
                    );


            card.getChildren()
                    .addAll(
                            info,
                            spacer,
                            statusPill
                    );
        }


        card.setOnMouseClicked(
                e ->
                        openPatient(
                                appointment
                        )
        );


        row.getChildren()
                .addAll(
                        timeLabel,
                        card
                );


        return row;
    }


    // ============================================================
    // RIGHT COLUMN
    // ============================================================

    private VBox createSideCardsColumn() {

        VBox sideColumn =
                new VBox(15);


        VBox calendarCard =
                createMiniCalendarCard();


        currentPatientCardContainer =
                createCurrentPatientCard();


        queueCardContainer =
                createQueueCard();


        sideColumn.getChildren()
                .addAll(
                        calendarCard,
                        currentPatientCardContainer,
                        queueCardContainer
                );


        return sideColumn;
    }


    // ============================================================
    // MINI CALENDAR
    // ============================================================

    private VBox createMiniCalendarCard() {

        VBox card =
                new VBox(10);


        card.getStyleClass()
                .add(
                        "side-card"
                );


        card.setPadding(
                new Insets(
                        15
                )
        );


        BorderPane header =
                new BorderPane();


        monthLabel =
                new Label(
                        formatMonthTitle(
                                selectedCalendarDate
                        )
                );


        monthLabel.getStyleClass()
                .add(
                        "calendar-month-title"
                );


        HBox nav =
                new HBox(8);


        Label prev =
                new Label(
                        "<"
                );


        prev.getStyleClass()
                .add(
                        "calendar-nav-arrow"
                );


        prev.setOnMouseClicked(
                e -> {

                    selectedCalendarDate =
                            selectedCalendarDate
                                    .minusMonths(
                                            1
                                    );


                    refreshCalendarDisplay();
                }
        );


        Label next =
                new Label(
                        ">"
                );


        next.getStyleClass()
                .add(
                        "calendar-nav-arrow"
                );


        next.setOnMouseClicked(
                e -> {

                    selectedCalendarDate =
                            selectedCalendarDate
                                    .plusMonths(
                                            1
                                    );


                    refreshCalendarDisplay();
                }
        );


        nav.getChildren()
                .addAll(
                        prev,
                        next
                );


        header.setLeft(
                monthLabel
        );


        header.setRight(
                nav
        );


        calendarGrid =
                new GridPane();


        calendarGrid.setHgap(
                8
        );


        calendarGrid.setVgap(
                8
        );


        calendarGrid.setAlignment(
                Pos.CENTER
        );


        refreshCalendarDisplay();


        card.getChildren()
                .addAll(
                        header,
                        calendarGrid
                );


        return card;
    }


    // ============================================================
    // REFRESH CALENDAR
    // ============================================================

    private void refreshCalendarDisplay() {

        if (calendarGrid == null) {

            return;
        }


        calendarGrid.getChildren()
                .clear();


        monthLabel.setText(
                formatMonthTitle(
                        selectedCalendarDate
                )
        );


        String[] days = {

                "Su",
                "Mo",
                "Tu",
                "We",
                "Th",
                "Fr",
                "Sa"
        };


        for (int i = 0;
             i < days.length;
             i++) {

            Label dayLabel =
                    new Label(
                            days[i]
                    );


            dayLabel.getStyleClass()
                    .add(
                            "calendar-day-header"
                    );


            calendarGrid.add(
                    dayLabel,
                    i,
                    0
            );
        }


        LocalDate firstDay =
                selectedCalendarDate
                        .withDayOfMonth(
                                1
                        );


        int startColumn =
                firstDay
                        .getDayOfWeek()
                        .getValue()
                        % 7;


        int daysInMonth =
                selectedCalendarDate
                        .lengthOfMonth();


        for (int day = 1;
             day <= daysInMonth;
             day++) {

            int position =
                    startColumn
                            + day
                            - 1;


            int column =
                    position % 7;


            int row =
                    position / 7
                            + 1;


            LocalDate cellDate =
                    LocalDate.of(
                            selectedCalendarDate
                                    .getYear(),
                            selectedCalendarDate
                                    .getMonth(),
                            day
                    );


            StackPane cell =
                    new StackPane();


            cell.getStyleClass()
                    .add(
                            "calendar-date-cell"
                    );


            cell.setPrefSize(
                    28,
                    28
            );


            Label dateLabel =
                    new Label(
                            String.valueOf(
                                    day
                            )
                    );


            boolean selected =
                    cellDate.equals(
                            currentDate
                    );


            boolean hasAppointment =
                    hasAppointmentOnDate(
                            cellDate
                    );


            if (selected) {

                Circle circle =
                        new Circle(
                                12,
                                Color.web(
                                        "#0052CC"
                                )
                        );


                dateLabel.setStyle(
                        "-fx-text-fill: white;"
                                + "-fx-font-weight: bold;"
                );


                cell.getChildren()
                        .addAll(
                                circle,
                                dateLabel
                        );

            } else {

                dateLabel.getStyleClass()
                        .add(
                                "calendar-date-number"
                        );


                cell.getChildren()
                        .add(
                                dateLabel
                        );
            }


            /*
             * Small appointment indicator.
             */
            if (hasAppointment
                    && !selected) {

                Circle appointmentDot =
                        new Circle(
                                2.5,
                                Color.web(
                                        "#0052CC"
                                )
                        );


                StackPane.setAlignment(
                        appointmentDot,
                        Pos.BOTTOM_RIGHT
                );


                StackPane.setMargin(
                        appointmentDot,
                        new Insets(
                                0,
                                3,
                                3,
                                0
                        )
                );


                cell.getChildren()
                        .add(
                                appointmentDot
                        );
            }


            cell.setOnMouseClicked(
                    e ->
                            updateCurrentDate(
                                    cellDate
                            )
            );


            calendarGrid.add(
                    cell,
                    column,
                    row
            );
        }
    }


    // ============================================================
    // CURRENT PATIENT CARD
    // ============================================================

    private VBox createCurrentPatientCard() {

        VBox card =
                new VBox(12);


        card.getStyleClass()
                .add(
                        "side-card"
                );


        card.setPadding(
                new Insets(
                        15
                )
        );


        BorderPane header =
                new BorderPane();


        Label title =
                new Label(
                        "CURRENT PATIENT"
                );


        title.getStyleClass()
                .add(
                        "side-card-subtitle"
                );


        HBox liveIndicator =
                new HBox(4);


        liveIndicator.setAlignment(
                Pos.CENTER
        );


        Circle liveDot =
                new Circle(
                        3,
                        Color.web(
                                "#0052CC"
                        )
                );


        Label liveText =
                new Label(
                        "Live"
                );


        liveText.getStyleClass()
                .add(
                        "live-text"
                );


        liveIndicator.getChildren()
                .addAll(
                        liveDot,
                        liveText
                );


        header.setLeft(
                title
        );


        header.setRight(
                liveIndicator
        );


        card.getChildren()
                .add(
                        header
                );


        Appointment appointment =
                findCurrentPatientAppointment();


        if (appointment == null) {

            Label empty =
                    new Label(
                            "No current patient"
                    );


            empty.setStyle(
                    "-fx-text-fill: #64748B;"
                            + "-fx-font-size: 13px;"
            );


            card.getChildren()
                    .add(
                            empty
                    );


            return card;
        }


        currentPatientAppointment =
                appointment;


        PatientProfile patient =
                loadPatient(
                        appointment.getPatientUid()
                );


        HBox patientProfile =
                new HBox(12);


        patientProfile.setAlignment(
                Pos.CENTER_LEFT
        );


        ImageView avatar =
                createPatientAvatar(
                        patient,
                        appointment
                );


        if (avatar != null) {

            Circle clip =
                    new Circle(
                            25,
                            25,
                            25
                    );


            avatar.setClip(
                    clip
            );


            patientProfile.getChildren()
                    .add(
                            avatar
                    );
        }


        VBox details =
                new VBox(2);


        String patientName =
                getPatientName(
                        patient,
                        appointment
                );


        Label name =
                new Label(
                        patientName
                );


        name.getStyleClass()
                .add(
                        "patient-card-name"
                );


        Label meta =
                new Label(
                        getPatientMeta(
                                patient
                        )
                );


        meta.getStyleClass()
                .add(
                        "patient-card-meta"
                );


        details.getChildren()
                .addAll(
                        name,
                        meta
                );


        patientProfile.getChildren()
                .add(
                        details
                );


        GridPane infoGrid =
                new GridPane();


        infoGrid.setVgap(
                8
        );


        infoGrid.setHgap(
                20
        );


        infoGrid.setPadding(
                new Insets(
                        5,
                        0,
                        5,
                        0
                )
        );


        Label reasonKey =
                new Label(
                        "Reason"
                );


        reasonKey.getStyleClass()
                .add(
                        "vital-key"
                );


        Label reasonVal =
                new Label(
                        safeText(
                                appointment
                                        .getReason(),
                                "Consultation"
                        )
                );


        reasonVal.getStyleClass()
                .add(
                        "vital-val"
                );


        reasonVal.setWrapText(
                true
        );


        Label statusKey =
                new Label(
                        "Status"
                );


        statusKey.getStyleClass()
                .add(
                        "vital-key"
                );


        Label statusVal =
                new Label(
                        formatStatus(
                                appointment
                                        .getStatus()
                        )
                );


        statusVal.getStyleClass()
                .add(
                        "vital-val"
                );


        infoGrid.add(
                reasonKey,
                0,
                0
        );


        infoGrid.add(
                reasonVal,
                1,
                0
        );


        infoGrid.add(
                statusKey,
                0,
                1
        );


        infoGrid.add(
                statusVal,
                1,
                1
        );


        Button profileBtn =
                new Button(
                        "View Full Profile"
                );


        profileBtn.getStyleClass()
                .add(
                        "btn-primary-block"
                );


        profileBtn.setMaxWidth(
                Double.MAX_VALUE
        );


        profileBtn.setOnAction(
                e ->
                        openPatient(
                                appointment
                        )
        );


        Button rescheduleBtn =
                new Button(
                        "Reschedule Appointment"
                );


        rescheduleBtn.getStyleClass()
                .add(
                        "btn-outline-block"
                );


        rescheduleBtn.setMaxWidth(
                Double.MAX_VALUE
        );


        ImageView calendarIcon =
                createImageView(
                        "/images/icons/ic_calendar.png",
                        14,
                        14
                );


        if (calendarIcon != null) {

            rescheduleBtn.setGraphic(
                    calendarIcon
            );
        }


        rescheduleBtn.setOnAction(
                e ->
                        showRescheduleDialog(
                                appointment
                        )
        );


        card.getChildren()
                .addAll(
                        patientProfile,
                        infoGrid,
                        profileBtn,
                        rescheduleBtn
                );


        return card;
    }


    // ============================================================
    // QUEUE CARD
    // ============================================================

    private VBox createQueueCard() {

        VBox card =
                new VBox(12);


        card.getStyleClass()
                .add(
                        "side-card"
                );


        card.setPadding(
                new Insets(
                        15
                )
        );


        BorderPane header =
                new BorderPane();


        Label title =
                new Label(
                        "Queue"
                );


        title.getStyleClass()
                .add(
                        "queue-title"
                );


        List<Appointment> waiting =
                getWaitingAppointments();


        Label badge =
                new Label(
                        waiting.size()
                                + " Waiting"
                );


        badge.getStyleClass()
                .add(
                        "queue-badge"
                );


        header.setLeft(
                title
        );


        header.setRight(
                badge
        );


        VBox queueList =
                new VBox(8);


        if (waiting.isEmpty()) {

            Label empty =
                    new Label(
                            "No patients waiting."
                    );


            empty.setStyle(
                    "-fx-text-fill: #64748B;"
                            + "-fx-font-size: 13px;"
            );


            queueList.getChildren()
                    .add(
                            empty
                    );

        } else {

            for (Appointment appointment :
                    waiting) {

                queueList.getChildren()
                        .add(
                                createQueueItem(
                                        appointment
                                )
                        );
            }
        }


        card.getChildren()
                .addAll(
                        header,
                        queueList
                );


        return card;
    }


    // ============================================================
    // QUEUE ITEM
    // ============================================================

    private HBox createQueueItem(
            Appointment appointment
    ) {

        HBox item =
                new HBox(10);


        item.getStyleClass()
                .add(
                        "queue-item"
                );


        item.setAlignment(
                Pos.CENTER_LEFT
        );


        String patientName =
                safeText(
                        appointment
                                .getPatientName(),
                        "Patient"
                );


        StackPane avatar =
                createInitialsAvatar(
                        getInitials(
                                patientName
                        )
                );


        VBox info =
                new VBox(2);


        Label name =
                new Label(
                        patientName
                );


        name.getStyleClass()
                .add(
                        "queue-name"
                );


        Label time =
                new Label(
                        safeText(
                                appointment
                                        .getAppointmentTime(),
                                ""
                        )
                );


        time.getStyleClass()
                .add(
                        "queue-time"
                );


        info.getChildren()
                .addAll(
                        name,
                        time
                );


        Region spacer =
                new Region();


        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );


        Label status =
                new Label(
                        "Waiting"
                );


        status.getStyleClass()
                .add(
                        "pill-waiting"
                );


        item.getChildren()
                .addAll(
                        avatar,
                        info,
                        spacer,
                        status
                );


        item.setOnMouseClicked(
                e ->
                        openPatient(
                                appointment
                        )
        );


        return item;
    }


    // ============================================================
    // UPDATE CURRENT DATE
    // ============================================================

    private void updateCurrentDate(
            LocalDate newDate
    ) {

        if (newDate == null) {

            return;
        }


        currentDate =
                newDate;


        selectedCalendarDate =
                newDate;


        if (dateTitleLabel != null) {

            dateTitleLabel.setText(
                    formatDateTitle(
                            currentDate
                    )
            );
        }


        refreshTimeline();


        refreshCalendarDisplay();


        refreshSideCards();


        if (criticalAlertContainer != null) {

            /*
             * Rebuild alert in parent.
             *
             * The easiest safe approach is to
             * replace it only if needed.
             */
        }
    }


    // ============================================================
    // REFRESH SIDE CARDS
    // ============================================================

    private void refreshSideCards() {

        if (currentPatientCardContainer == null
                || queueCardContainer == null) {

            return;
        }


        VBox parent =
                (VBox)
                        currentPatientCardContainer
                                .getParent();


        if (parent == null) {

            return;
        }


        int currentIndex =
                parent.getChildren()
                        .indexOf(
                                currentPatientCardContainer
                        );


        int queueIndex =
                parent.getChildren()
                        .indexOf(
                                queueCardContainer
                        );


        VBox newCurrentCard =
                createCurrentPatientCard();


        VBox newQueueCard =
                createQueueCard();


        parent.getChildren()
                .set(
                        currentIndex,
                        newCurrentCard
                );


        parent.getChildren()
                .set(
                        queueIndex,
                        newQueueCard
                );


        currentPatientCardContainer =
                newCurrentCard;


        queueCardContainer =
                newQueueCard;
    }


    // ============================================================
    // GET APPOINTMENTS FOR DATE
    // ============================================================

    private List<Appointment>
    getAppointmentsForDate(
            LocalDate date
    ) {

        List<Appointment> result =
                new ArrayList<>();


        if (date == null) {

            return result;
        }


        String targetDate =
                date.toString();


        for (Appointment appointment :
                doctorAppointments) {

            if (appointment == null) {

                continue;
            }


            String appointmentDate =
                    appointment
                            .getAppointmentDate();


            if (appointmentDate == null
                    || appointmentDate
                    .trim()
                    .isEmpty()) {

                continue;
            }


            if (targetDate.equals(
                    appointmentDate.trim()
            )) {

                result.add(
                        appointment
                );
            }
        }


        result.sort(
                Comparator.comparing(
                        this::getAppointmentDateTime
                )
        );


        return result;
    }


    // ============================================================
    // WAITING APPOINTMENTS
    // ============================================================

    private List<Appointment>
    getWaitingAppointments() {

        List<Appointment> result =
                new ArrayList<>();


        for (Appointment appointment :
                selectedDateAppointments) {

            if (appointment == null) {

                continue;
            }


            String status =
                    safeText(
                            appointment.getStatus(),
                            ""
                    );


            if ("PENDING".equalsIgnoreCase(
                    status
            )
                    || "WAITING".equalsIgnoreCase(
                    status
            )) {

                result.add(
                        appointment
                );
            }
        }


        return result;
    }


    // ============================================================
    // FIND CURRENT PATIENT
    // ============================================================

    private Appointment
    findCurrentPatientAppointment() {

        /*
         * First preference:
         * IN PROGRESS / IN ROOM
         */
        for (Appointment appointment :
                selectedDateAppointments) {

            String status =
                    safeText(
                            appointment.getStatus(),
                            ""
                    );


            if ("IN PROGRESS"
                    .equalsIgnoreCase(
                            status
                    )
                    || "IN ROOM"
                    .equalsIgnoreCase(
                            status
                    )) {

                return appointment;
            }
        }


        /*
         * Second preference:
         * Waiting appointment.
         */
        for (Appointment appointment :
                selectedDateAppointments) {

            String status =
                    safeText(
                            appointment.getStatus(),
                            ""
                    );


            if ("WAITING"
                    .equalsIgnoreCase(
                            status
                    )
                    || "PENDING"
                    .equalsIgnoreCase(
                            status
                    )) {

                return appointment;
            }
        }


        /*
         * Third preference:
         * First appointment that isn't cancelled.
         */
        for (Appointment appointment :
                selectedDateAppointments) {

            String status =
                    safeText(
                            appointment.getStatus(),
                            ""
                    );


            if (!"CANCELLED"
                    .equalsIgnoreCase(
                            status
                    )) {

                return appointment;
            }
        }


        return null;
    }


    // ============================================================
    // IS CURRENT APPOINTMENT
    // ============================================================

    private boolean isCurrentAppointment(
            Appointment appointment
    ) {

        if (appointment == null) {

            return false;
        }


        String status =
                safeText(
                        appointment.getStatus(),
                        ""
                );


        return "IN PROGRESS"
                .equalsIgnoreCase(
                        status
                )
                || "IN ROOM"
                .equalsIgnoreCase(
                        status
                );
    }


    // ============================================================
    // PRIORITY APPOINTMENT
    // ============================================================

    private Appointment
    findPriorityAppointment() {

        for (Appointment appointment :
                selectedDateAppointments) {

            if (appointment == null) {

                continue;
            }


            String reason =
                    safeText(
                            appointment.getReason(),
                            ""
                    )
                            .toLowerCase();


            if (reason.contains(
                    "severe"
            )
                    || reason.contains(
                    "emergency"
            )
                    || reason.contains(
                    "critical"
            )
                    || reason.contains(
                    "chest pain"
            )) {

                return appointment;
            }
        }


        return null;
    }


    // ============================================================
    // HAS APPOINTMENT ON DATE
    // ============================================================

    private boolean hasAppointmentOnDate(
            LocalDate date
    ) {

        return !getAppointmentsForDate(
                date
        ).isEmpty();
    }


    // ============================================================
    // UPDATE SUBTITLE
    // ============================================================

    private void updateDateSubtitle() {

        if (appointmentSubtitleLabel == null) {

            return;
        }


        int total =
                selectedDateAppointments.size();


        long completed =
                selectedDateAppointments
                        .stream()
                        .filter(
                                appointment ->
                                        "COMPLETED"
                                                .equalsIgnoreCase(
                                                        safeText(
                                                                appointment
                                                                        .getStatus(),
                                                                ""
                                                        )
                                                )
                        )
                        .count();


        long remaining =
                total
                        - completed;


        appointmentSubtitleLabel.setText(
                total
                        + " Appointment"
                        + (
                        total == 1
                                ? ""
                                : "s"
                )
                        + " • "
                        + remaining
                        + " remaining"
        );
    }


    // ============================================================
    // RESCHEDULE DIALOG
    // ============================================================

    private void showRescheduleDialog(
            Appointment appointment
    ) {

        if (appointment == null) {

            return;
        }


        String patientName =
                safeText(
                        appointment.getPatientName(),
                        "Patient"
                );


        Stage dialog =
                new Stage();


        dialog.initModality(
                Modality.APPLICATION_MODAL
        );


        dialog.initOwner(
                stage
        );


        dialog.setTitle(
                "Reschedule Appointment"
        );


        VBox dialogRoot =
                new VBox(15);


        dialogRoot.setPadding(
                new Insets(
                        20
                )
        );


        dialogRoot.setAlignment(
                Pos.CENTER_LEFT
        );


        Label header =
                new Label(
                        "Reschedule Appointment for "
                                + patientName
                );


        header.setStyle(
                "-fx-font-size: 16px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #1E293B;"
        );


        DatePicker datePicker =
                new DatePicker(
                        parseAppointmentDate(
                                appointment
                        )
                );


        datePicker.setMaxWidth(
                Double.MAX_VALUE
        );


        ComboBox<String> timeSlotCombo =
                new ComboBox<>();


        timeSlotCombo.getItems()
                .addAll(

                        "09:00 AM",

                        "09:30 AM",

                        "10:00 AM",

                        "10:30 AM",

                        "11:00 AM",

                        "11:30 AM",

                        "12:00 PM",

                        "01:00 PM",

                        "02:00 PM",

                        "03:00 PM",

                        "04:00 PM",

                        "05:00 PM"
                );


        String currentTime =
                appointment.getAppointmentTime();


        if (currentTime != null
                && timeSlotCombo.getItems()
                .contains(
                        currentTime
                )) {

            timeSlotCombo.setValue(
                    currentTime
            );

        } else {

            timeSlotCombo.setValue(
                    "10:00 AM"
            );
        }


        timeSlotCombo.setMaxWidth(
                Double.MAX_VALUE
        );


        HBox actionButtons =
                new HBox(10);


        actionButtons.setAlignment(
                Pos.CENTER_RIGHT
        );


        Button cancelBtn =
                new Button(
                        "Cancel"
                );


        cancelBtn.setOnAction(
                e ->
                        dialog.close()
        );


        Button confirmBtn =
                new Button(
                        "Confirm Reschedule"
                );


        confirmBtn.setStyle(
                "-fx-background-color: #0052CC;"
                        + "-fx-text-fill: white;"
                        + "-fx-font-weight: bold;"
        );


        confirmBtn.setOnAction(
                e -> {

                    LocalDate newDate =
                            datePicker.getValue();


                    String newTime =
                            timeSlotCombo
                                    .getValue();


                    if (newDate == null) {

                        showInformationAlert(
                                "Invalid Date",
                                "Please select a date."
                        );


                        return;
                    }


                    if (newTime == null
                            || newTime
                            .trim()
                            .isEmpty()) {

                        showInformationAlert(
                                "Invalid Time",
                                "Please select a time."
                        );


                        return;
                    }


                    try {

                        appointment
                                .setAppointmentDate(
                                        newDate.toString()
                                );


                        appointment
                                .setAppointmentTime(
                                        newTime
                                );


                        appointmentController
                                .updateAppointment(
                                        appointment
                                );


                        loadDoctorAppointments();


                        dialog.close();


                        updateCurrentDate(
                                newDate
                        );


                        showInformationAlert(
                                "Appointment Rescheduled",
                                "Successfully rescheduled "
                                        + patientName
                                        + " to "
                                        + newDate
                                        + " at "
                                        + newTime
                                        + "."
                        );


                    } catch (Exception ex) {

                        showInformationAlert(
                                "Reschedule Failed",
                                "Unable to reschedule the appointment.\n\n"
                                        + getRootMessage(
                                        ex
                                )
                        );
                    }
                }
        );


        actionButtons.getChildren()
                .addAll(
                        cancelBtn,
                        confirmBtn
                );


        dialogRoot.getChildren()
                .addAll(

                        header,

                        new Label(
                                "Select New Date:"
                        ),

                        datePicker,

                        new Label(
                                "Select Time Slot:"
                        ),

                        timeSlotCombo,

                        actionButtons
                );


        Scene dialogScene =
                new Scene(
                        dialogRoot,
                        360,
                        260
                );


        dialog.setScene(
                dialogScene
        );


        dialog.showAndWait();
    }


    // ============================================================
    // SEARCH
    // ============================================================

    private void showSearchDialog() {

        TextInputDialog searchDialog =
                new TextInputDialog();


        searchDialog.setTitle(
                "Search Doctor Agenda"
        );


        searchDialog.setHeaderText(
                "Search Patient"
        );


        searchDialog.setContentText(
                "Enter patient name:"
        );


        searchDialog.showAndWait()
                .ifPresent(
                        query -> {

                            if (query == null
                                    || query
                                    .trim()
                                    .isEmpty()) {

                                return;
                            }


                            String search =
                                    query
                                            .trim()
                                            .toLowerCase();


                            List<Appointment> matches =
                                    doctorAppointments
                                            .stream()
                                            .filter(
                                                    appointment ->
                                                            appointment != null
                                                                    && safeText(
                                                                    appointment
                                                                            .getPatientName(),
                                                                    ""
                                                            )
                                                                    .toLowerCase()
                                                                    .contains(
                                                                            search
                                                                    )
                                            )
                                            .toList();


                            if (matches.isEmpty()) {

                                showInformationAlert(
                                        "Search Result",
                                        "No patient found for: "
                                                + query
                                );


                                return;
                            }


                            Appointment first =
                                    matches.get(
                                            0
                                    );


                            showInformationAlert(
                                    "Search Result",
                                    matches.size()
                                            + " appointment"
                                            + (
                                            matches.size()
                                                    == 1
                                                    ? ""
                                                    : "s"
                                    )
                                            + " found.\n\n"
                                            + first
                                            .getPatientName()
                                            + "\n"
                                            + first
                                            .getAppointmentDate()
                                            + " "
                                            + first
                                            .getAppointmentTime()
                            );
                        }
                );
    }


    // ============================================================
    // NOTIFICATIONS
    // ============================================================

    private void showNotificationDialog() {

        List<Appointment> waiting =
                getWaitingAppointments();


        String message;


        if (waiting.isEmpty()) {

            message =
                    "No pending or waiting appointments.";

        } else {

            message =
                    "You have "
                            + waiting.size()
                            + " waiting/pending appointment"
                            + (
                            waiting.size() == 1
                                    ? ""
                                    : "s"
                    )
                            + " today.";
        }


        showInformationAlert(
                "Notifications",
                message
        );
    }


    // ============================================================
    // OPEN PATIENT
    // ============================================================

    private void openPatient(
            Appointment appointment
    ) {

        if (appointment == null) {

            return;
        }


        System.out.println(
                "Selected patient:"
        );


        System.out.println(
                "Patient UID: "
                        + appointment.getPatientUid()
        );


        System.out.println(
                "Patient Name: "
                        + appointment.getPatientName()
        );


        Navigation.goTo(
                stage,
                () ->
                        new PatientDetailsView(
                                stage
                        ).getScene()
        );
    }


    // ============================================================
    // LOAD PATIENT
    // ============================================================

    private PatientProfile loadPatient(
            String patientUid
    ) {

        if (patientUid == null
                || patientUid.trim().isEmpty()) {

            return null;
        }


        try {

            return patientDAO
                    .getPatientProfile(
                            patientUid
                    );

        } catch (Exception e) {

            System.err.println(
                    "Unable to load patient profile: "
                            + getRootMessage(
                            e
                    )
            );


            return null;
        }
    }


    // ============================================================
    // PATIENT NAME
    // ============================================================

    private String getPatientName(
            PatientProfile patient,
            Appointment appointment
    ) {

        if (patient != null) {

            String first =
                    safeText(
                            patient.getFirstName(),
                            ""
                    );


            String last =
                    safeText(
                            patient.getLastName(),
                            ""
                    );


            String full =
                    (
                            first
                                    + " "
                                    + last
                    ).trim();


            if (!full.isEmpty()) {

                return full;
            }
        }


        return safeText(
                appointment.getPatientName(),
                "Patient"
        );
    }


    // ============================================================
    // PATIENT META
    // ============================================================

    private String getPatientMeta(
            PatientProfile patient
    ) {

        if (patient == null) {

            return "Patient information unavailable";
        }


        String gender =
                safeText(
                        patient.getGender(),
                        "Unknown"
                );


        String age =
                calculateAge(
                        patient.getDateOfBirth()
                );


        String bloodGroup =
                safeText(
                        patient.getBloodGroup(),
                        "Unknown"
                );


        return gender
                + ", "
                + age
                + " yrs"
                + " • "
                + bloodGroup;
    }


    // ============================================================
    // AGE
    // ============================================================

    private String calculateAge(
            String dateOfBirth
    ) {

        if (dateOfBirth == null
                || dateOfBirth.trim().isEmpty()) {

            return "Unknown";
        }


        try {

            LocalDate dob =
                    LocalDate.parse(
                            dateOfBirth
                    );


            int age =
                    Period.between(
                            dob,
                            LocalDate.now()
                    ).getYears();


            return String.valueOf(
                    Math.max(
                            age,
                            0
                    )
            );


        } catch (DateTimeParseException e) {

            return "Unknown";
        }
    }


    // ============================================================
    // PATIENT AVATAR
    // ============================================================

    private ImageView createPatientAvatar(
            PatientProfile patient,
            Appointment appointment
    ) {

        /*
         * Try existing mock avatar first.
         */
        ImageView avatar =
                createImageView(
                        "/images/mocks/elena_rodriguez.png",
                        50,
                        50
                );


        return avatar;
    }


    // ============================================================
    // ROOM
    // ============================================================

    private String getRoomText(
            Appointment appointment
    ) {

        /*
         * Appointment model currently does not expose
         * a room field in the existing backend.
         *
         * Therefore we do not invent a Firestore field.
         */
        return "Room —";
    }


    // ============================================================
    // APPOINTMENT DATE/TIME
    // ============================================================

    private LocalDateTimeWrapper
    getAppointmentDateTime(
            Appointment appointment
    ) {

        if (appointment == null) {

            return new LocalDateTimeWrapper(
                    LocalDate.MIN,
                    LocalTime.MIN
            );
        }


        LocalDate date =
                LocalDate.MIN;


        LocalTime time =
                LocalTime.MIN;


        try {

            if (appointment
                    .getAppointmentDate()
                    != null) {

                date =
                        LocalDate.parse(
                                appointment
                                        .getAppointmentDate()
                        );
            }

        } catch (Exception ignored) {
        }


        try {

            if (appointment
                    .getAppointmentTime()
                    != null) {

                time =
                        LocalTime.parse(
                                appointment
                                        .getAppointmentTime()
                                        .trim()
                                        .toUpperCase(),
                                DateTimeFormatter.ofPattern(
                                        "h:mm a"
                                )
                        );
            }

        } catch (Exception ignored) {
        }


        return new LocalDateTimeWrapper(
                date,
                time
        );
    }


    // ============================================================
    // DATE PARSING
    // ============================================================

    private LocalDate parseAppointmentDate(
            Appointment appointment
    ) {

        try {

            return LocalDate.parse(
                    appointment
                            .getAppointmentDate()
            );

        } catch (Exception e) {

            return LocalDate.now()
                    .plusDays(
                            1
                    );
        }
    }


    // ============================================================
    // STATUS STYLE
    // ============================================================

    private String getStatusStyleClass(
            String status
    ) {

        if ("COMPLETED".equalsIgnoreCase(
                status
        )) {

            return "pill-completed";
        }


        if ("WAITING".equalsIgnoreCase(
                status
        )
                || "PENDING".equalsIgnoreCase(
                status
        )) {

            return "pill-waiting";
        }


        if ("CONFIRMED".equalsIgnoreCase(
                status
        )) {

            return "pill-waiting";
        }


        return "pill-waiting";
    }


    // ============================================================
    // STATUS FORMAT
    // ============================================================

    private String formatStatus(
            String status
    ) {

        if (status == null
                || status.trim().isEmpty()) {

            return "Pending";
        }


        String normalized =
                status.trim()
                        .toLowerCase();


        return Character
                .toUpperCase(
                        normalized.charAt(
                                0
                        )
                )
                + normalized.substring(
                        1
                );
    }


    // ============================================================
    // DOCTOR UID
    // ============================================================

    private String getCurrentDoctorUid() {

        try {

            SessionManager session =
                    SessionManager
                            .getInstance();


            if (!session.isLoggedIn()) {

                return null;
            }


            if (session.getCurrentUser()
                    == null) {

                return null;
            }


            return session
                    .getCurrentUser()
                    .getUid();


        } catch (Exception e) {

            System.err.println(
                    "Unable to retrieve doctor UID: "
                            + e.getMessage()
            );


            return null;
        }
    }


    // ============================================================
    // DOCTOR DISPLAY NAME
    // ============================================================

    private String getDoctorDisplayName() {

        for (Appointment appointment :
                doctorAppointments) {

            if (appointment == null) {

                continue;
            }


            String doctorName =
                    appointment.getDoctorName();


            if (doctorName != null
                    && !doctorName.trim().isEmpty()) {

                String name =
                        doctorName.trim();


                if (!name
                        .toLowerCase()
                        .startsWith(
                                "dr."
                        )) {

                    name =
                            "Dr. "
                                    + name;
                }


                return name;
            }
        }


        return "Doctor";
    }


    // ============================================================
    // FORMAT DATE
    // ============================================================

    private String formatDateTitle(
            LocalDate date
    ) {

        return date.format(
                DateTimeFormatter.ofPattern(
                        "EEEE, MMM d"
                )
        );
    }


    // ============================================================
    // FORMAT MONTH
    // ============================================================

    private String formatMonthTitle(
            LocalDate date
    ) {

        return date.format(
                DateTimeFormatter.ofPattern(
                        "MMMM yyyy"
                )
        );
    }


    // ============================================================
    // INITIALS
    // ============================================================

    private String getInitials(
            String name
    ) {

        if (name == null
                || name.trim().isEmpty()) {

            return "PT";
        }


        String[] parts =
                name.trim()
                        .split(
                                "\\s+"
                        );


        if (parts.length >= 2) {

            return (
                    ""
                            + parts[0].charAt(0)
                            + parts[1].charAt(0)
            ).toUpperCase();
        }


        return (
                ""
                        + parts[0].charAt(0)
        ).toUpperCase();
    }


    // ============================================================
    // INITIALS AVATAR
    // ============================================================

    private StackPane createInitialsAvatar(
            String initials
    ) {

        StackPane avatar =
                new StackPane();


        avatar.getStyleClass()
                .add(
                        "initials-avatar"
                );


        Label text =
                new Label(
                        initials
                );


        text.getStyleClass()
                .add(
                        "initials-text"
                );


        avatar.getChildren()
                .add(
                        text
                );


        return avatar;
    }


    // ============================================================
    // SAFE TEXT
    // ============================================================

    private String safeText(
            String value,
            String fallback
    ) {

        if (value == null
                || value.trim().isEmpty()) {

            return fallback;
        }


        return value.trim();
    }


    // ============================================================
    // ROOT ERROR
    // ============================================================

    private String getRootMessage(
            Throwable throwable
    ) {

        if (throwable == null) {

            return "Unknown error";
        }


        Throwable current =
                throwable;


        while (current.getCause() != null
                && current.getCause()
                != current) {

            current =
                    current.getCause();
        }


        return safeText(
                current.getMessage(),
                "Unknown error"
        );
    }


    // ============================================================
    // IMAGE LOADER
    // ============================================================

    private ImageView createImageView(
            String resourcePath,
            double width,
            double height
    ) {

        try {

            InputStream is =
                    getClass()
                            .getResourceAsStream(
                                    resourcePath
                            );


            if (is != null) {

                ImageView imageView =
                        new ImageView(
                                new Image(
                                        is
                                )
                        );


                imageView.setFitWidth(
                        width
                );


                imageView.setFitHeight(
                        height
                );


                imageView.setPreserveRatio(
                        true
                );


                return imageView;
            }

        } catch (Exception e) {

            System.err.println(
                    "Unable to load image: "
                            + resourcePath
            );
        }


        return null;
    }


    // ============================================================
    // INFORMATION ALERT
    // ============================================================

    private void showInformationAlert(
            String title,
            String message
    ) {

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


    // ============================================================
    // NOTIFICATION
    // ============================================================

    private void showNotificationDialogOld() {

        showInformationAlert(
                "Notifications",
                "No new notifications."
        );
    }


    // ============================================================
    // SMALL DATE/TIME WRAPPER
    // ============================================================

    private static class LocalDateTimeWrapper
            implements Comparable<LocalDateTimeWrapper> {

        private final LocalDate date;
        private final LocalTime time;


        LocalDateTimeWrapper(
                LocalDate date,
                LocalTime time
        ) {

            this.date = date;
            this.time = time;
        }


        @Override
        public int compareTo(
                LocalDateTimeWrapper other
        ) {

            int dateCompare =
                    date.compareTo(
                            other.date
                    );


            if (dateCompare != 0) {

                return dateCompare;
            }


            return time.compareTo(
                    other.time
            );
        }
    }
}