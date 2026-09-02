package com.healthsphere.view.doctor;

import com.healthsphere.controller.doctor.PatientController;
import com.healthsphere.controller.medical.MedicalReportController;
import com.healthsphere.controller.doctor.PrescriptionController;
import com.healthsphere.model.MedicalReport;
import com.healthsphere.model.PatientProfile;
import com.healthsphere.model.Prescription;
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

import java.awt.Desktop;
import java.net.URI;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * MedicalReportsView
 *
 * Doctor's Prescription Manager and Medical Passport screen.
 *
 * Doctor can:
 *  - Select a patient
 *  - View patient medical reports
 *  - Open/download medical reports
 *  - Create prescriptions
 *  - View existing prescriptions
 *
 * Doctor cannot:
 *  - Upload medical passport reports
 *  - Delete medical passport reports
 *  - Edit medical passport reports
 *
 * Architecture:
 *
 * View
 *    ↓
 * Controller
 *    ↓
 * DAO
 *    ↓
 * Firebase / Cloudinary
 */
public class MedicalReportsView {

    private final Stage stage;
    private final Scene scene;

    /**
     * Currently selected patient UID.
     */
    private String patientUid;

    /**
     * Currently selected patient.
     */
    private PatientProfile patientProfile;

    /**
     * Patients belonging to the logged-in doctor.
     */
    private List<PatientProfile> doctorPatients;

    /**
     * Patient selector.
     */
    private ComboBox<PatientProfile> patientSelector;

    /**
     * Controllers.
     */
    private final PatientController patientController;
    private final MedicalReportController medicalReportController;
    private final PrescriptionController prescriptionController;


    // ============================================================
    // CONSTRUCTORS
    // ============================================================

    /**
     * Default constructor used by sidebar navigation.
     *
     * No patient is selected initially.
     */
    public MedicalReportsView(Stage stage) {
        this(stage, null);
    }


    /**
     * Patient-specific constructor.
     *
     * Used when opening Medical Reports from a selected patient
     * or appointment.
     */
    public MedicalReportsView(
            Stage stage,
            String patientUid
    ) {

        this.stage = stage;
        this.patientUid = patientUid;

        this.patientController =
                new PatientController();

        this.medicalReportController =
                new MedicalReportController();

        this.prescriptionController =
                new PrescriptionController();

        /*
         * First load all patients belonging to this doctor.
         */
        loadDoctorPatients();

        /*
         * Then load the selected patient if one was supplied.
         */
        loadPatient();

        this.scene = createScene();
    }


    public Scene getScene() {
        return this.scene;
    }


    // ============================================================
    // LOAD DOCTOR PATIENTS
    // ============================================================

    /**
     * Loads patients who have an appointment relationship
     * with the logged-in doctor.
     */
    private void loadDoctorPatients() {

        doctorPatients =
                new ArrayList<>();

        String doctorUid =
                getCurrentDoctorUid();

        if (doctorUid == null
                || doctorUid.isBlank()) {

            return;
        }

        try {

            doctorPatients =
                    patientController
                            .getPatientsForDoctorSafe(
                                    doctorUid
                            );

        } catch (Exception e) {

            e.printStackTrace();

            doctorPatients.clear();
        }
    }


    // ============================================================
    // LOAD SELECTED PATIENT
    // ============================================================

    private void loadPatient() {

        /*
         * If there is no patient UID, there is no selected patient.
         */
        if (patientUid == null
                || patientUid.isBlank()) {

            patientProfile = null;
            return;
        }


        /*
         * First try to find the patient in the already loaded
         * doctor patient list.
         */
        if (doctorPatients != null) {

            for (PatientProfile patient :
                    doctorPatients) {

                if (patient != null
                        && patientUid.equals(
                                patient.getUid()
                        )) {

                    patientProfile = patient;
                    return;
                }
            }
        }


        /*
         * Fallback: fetch directly from Firestore.
         */
        try {

            patientProfile =
                    patientController
                            .getPatientProfile(
                                    patientUid
                            );

        } catch (Exception e) {

            e.printStackTrace();

            patientProfile = null;
        }
    }


    // ============================================================
    // CREATE SCENE
    // ============================================================

    private Scene createScene() {

        BorderPane mainRoot =
                new BorderPane();

        mainRoot.getStyleClass()
                .add("root-pane");


        // --------------------------------------------------------
        // SIDEBAR
        // --------------------------------------------------------

        VBox sidebar =
                createSidebar();

        mainRoot.setLeft(sidebar);


        // --------------------------------------------------------
        // CONTENT
        // --------------------------------------------------------

        VBox contentArea =
                new VBox(20);

        contentArea.setPadding(
                new Insets(
                        24,
                        32,
                        24,
                        32
                )
        );

        contentArea.getStyleClass()
                .add("content-area");


        HBox topHeader =
                createTopHeader();


        /*
         * NEW:
         * Patient selector appears at the top.
         */
        HBox selector =
                createPatientSelector();


        BorderPane patientHeader =
                createPatientHeader();


        contentArea.getChildren()
                .addAll(
                        topHeader,
                        selector,
                        patientHeader
                );


        // --------------------------------------------------------
        // BODY
        // --------------------------------------------------------

        HBox bodyLayout =
                createBodyLayout();

        contentArea.getChildren()
                .add(bodyLayout);


        // --------------------------------------------------------
        // SCROLL
        // --------------------------------------------------------

        ScrollPane contentScrollPane =
                new ScrollPane(
                        contentArea
                );

        contentScrollPane.setFitToWidth(true);
        contentScrollPane.setFitToHeight(true);

        contentScrollPane.getStyleClass()
                .add("content-scrollpane");


        mainRoot.setCenter(
                contentScrollPane
        );


        Scene medicalReportsScene =
                new Scene(
                        mainRoot,
                        stage.getWidth(),
                        stage.getHeight()
                );


        try {

            medicalReportsScene
                    .getStylesheets()
                    .add(
                            Objects.requireNonNull(
                                    getClass()
                                            .getResource(
                                                    "/css/medical_reports.css"
                                            )
                            ).toExternalForm()
                    );

        } catch (Exception ignored) {
        }


        return medicalReportsScene;
    }


    // ============================================================
    // PATIENT SELECTOR
    // ============================================================

    private HBox createPatientSelector() {

        HBox box =
                new HBox(12);

        box.setAlignment(
                Pos.CENTER_LEFT
        );

        box.setPadding(
                new Insets(
                        4,
                        0,
                        0,
                        0
                )
        );


        Label label =
                new Label(
                        "Select Patient:"
                );

        label.setStyle(
                "-fx-font-weight: bold;"
                        + "-fx-text-fill: #334155;"
                        + "-fx-font-size: 13px;"
        );


        patientSelector =
                new ComboBox<>();

        patientSelector.setPrefWidth(
                350
        );

        patientSelector.setPromptText(
                "Choose a patient"
        );


        if (doctorPatients != null) {

            patientSelector
                    .getItems()
                    .addAll(
                            doctorPatients
                    );
        }


        /*
         * Display patient names in dropdown.
         */
        patientSelector.setCellFactory(
                listView ->
                        new ListCell<>() {

                            @Override
                            protected void updateItem(
                                    PatientProfile patient,
                                    boolean empty
                            ) {

                                super.updateItem(
                                        patient,
                                        empty
                                );


                                if (empty
                                        || patient == null) {

                                    setText(null);

                                } else {

                                    setText(
                                            getPatientDisplayName(
                                                    patient
                                            )
                                    );
                                }
                            }
                        }
        );


        /*
         * Display selected patient's name in ComboBox.
         */
        patientSelector.setButtonCell(
                new ListCell<>() {

                    @Override
                    protected void updateItem(
                            PatientProfile patient,
                            boolean empty
                    ) {

                        super.updateItem(
                                patient,
                                empty
                        );


                        if (empty
                                || patient == null) {

                            setText(null);

                        } else {

                            setText(
                                    getPatientDisplayName(
                                            patient
                                    )
                            );
                        }
                    }
                }
        );


        /*
         * If MedicalReportsView was opened with a patient UID,
         * automatically select that patient.
         */
        if (patientUid != null
                && !patientUid.isBlank()) {

            for (PatientProfile patient :
                    doctorPatients) {

                if (patient != null
                        && patientUid.equals(
                                patient.getUid()
                        )) {

                    patientSelector.setValue(
                            patient
                    );

                    break;
                }
            }
        }


        /*
         * Patient selection event.
         */
        patientSelector.setOnAction(
                e -> handlePatientSelection()
        );


        box.getChildren()
                .addAll(
                        label,
                        patientSelector
                );


        return box;
    }


    // ============================================================
    // PATIENT SELECTION
    // ============================================================

    private void handlePatientSelection() {

        PatientProfile selectedPatient =
                patientSelector.getValue();


        if (selectedPatient == null) {
            return;
        }


        /*
         * Update selected patient.
         */
        patientProfile =
                selectedPatient;


        /*
         * Update selected patient UID.
         */
        patientUid =
                selectedPatient.getUid();


        /*
         * Recreate the screen so every component
         * uses the newly selected patient.
         */
        refreshPatientData();
    }


    // ============================================================
    // REFRESH
    // ============================================================

    private void refreshPatientData() {

        /*
         * Reload selected patient from Firestore.
         */
        loadPatient();


        /*
         * Recreate the complete screen.
         */
        Scene refreshedScene =
                createScene();


        stage.setScene(
                refreshedScene
        );
    }


    // ============================================================
    // PATIENT DISPLAY NAME
    // ============================================================

    private String getPatientDisplayName(
            PatientProfile patient
    ) {

        if (patient == null) {
            return "Unknown Patient";
        }


        String firstName =
                patient.getFirstName() == null
                        ? ""
                        : patient.getFirstName()
                        .trim();


        String lastName =
                patient.getLastName() == null
                        ? ""
                        : patient.getLastName()
                        .trim();


        String fullName =
                (
                        firstName
                                + " "
                                + lastName
                ).trim();


        if (fullName.isBlank()) {

            return safeText(
                    patient.getUid(),
                    "Unknown Patient"
            );
        }


        return fullName;
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

        logoIconBox.getStyleClass()
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


        logoIconBox.getChildren()
                .add(logoIcon);


        VBox logoText =
                new VBox(2);


        Label appName =
                new Label(
                        "Health-Sphere"
                );

        appName.getStyleClass()
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

        doctorSubtext.getStyleClass()
                .add("logo-subtext");

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


            Label tabLabel =
                    new Label(
                            tabs[i]
                    );

            tabLabel.getStyleClass()
                    .add("nav-text");


            if (i == 4) {

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
                );

            } else {

                navTab.setStyle(
                        "-fx-background-color: transparent;"
                                + "-fx-background-radius: 8px;"
                );

                tabLabel.setStyle(
                        "-fx-text-fill: #94A3B8;"
                );
            }


            navTab.getChildren()
                    .addAll(
                            icon,
                            tabLabel
                    );


            navItems.getChildren()
                    .add(navTab);


            final int index = i;


            navTab.setOnMouseClicked(
                    e ->
                            handleSidebarTabClick(
                                    index
                            )
            );
        }


        // --------------------------------------------------------
        // SPACER
        // --------------------------------------------------------

        Region spacer =
                new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );


        // --------------------------------------------------------
        // FOOTER
        // --------------------------------------------------------

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
                new ImageView(
                        ResourceImage.load(
                                "/images/doctor/portrait-3d-male-doctor.png"
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
                        "Doctor"
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


        sidebarProfile.getChildren()
                .addAll(
                        profileAvatar,
                        profileTexts
                );


        sidebarProfile.setOnMouseClicked(
                e ->
                        Navigation.goTo(
                                stage,
                                () ->
                                        new DoctorProfileView(
                                                stage
                                        ).getScene()
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

        logoutLabel.getStyleClass()
                .add("nav-text");

        logoutLabel.setStyle(
                "-fx-text-fill: #94A3B8;"
        );


        logoutTab.getChildren()
                .addAll(
                        logoutIcon,
                        logoutLabel
                );


        logoutTab.setOnMouseClicked(
                e -> handleLogout()
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

                if (patientUid != null
                        && !patientUid.isBlank()) {

                    Navigation.goTo(
                            stage,
                            () ->
                                    new MedicalReportsView(
                                            stage,
                                            patientUid
                                    ).getScene()
                    );

                } else {

                    Navigation.goTo(
                            stage,
                            () ->
                                    new MedicalReportsView(
                                            stage
                                    ).getScene()
                    );
                }

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

        } catch (Exception e) {

            e.printStackTrace();
        }


        System.out.println(
                "Doctor logged out."
        );
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


        Label pageTitle =
                new Label(
                        "Prescription Manager"
                );

        pageTitle.getStyleClass()
                .add(
                        "header-title"
                );


        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );


        HBox searchField =
                new HBox(10);

        searchField.getStyleClass()
                .add(
                        "search-input-box"
                );

        searchField.setAlignment(
                Pos.CENTER_LEFT
        );

        searchField.setPrefWidth(300);


        ImageView searchIcon =
                new ImageView(
                        ResourceImage.load(
                                "/images/icons/ic_search.png"
                        )
                );

        searchIcon.setFitWidth(16);
        searchIcon.setFitHeight(16);


        TextField searchInput =
                new TextField();

        searchInput.setPromptText(
                "Search medications, ICD-10..."
        );

        searchInput.getStyleClass()
                .add(
                        "search-text-field"
                );

        HBox.setHgrow(
                searchInput,
                Priority.ALWAYS
        );


        searchField.getChildren()
                .addAll(
                        searchIcon,
                        searchInput
                );


        HBox rightIcons =
                new HBox(16);

        rightIcons.setAlignment(
                Pos.CENTER_RIGHT
        );

        rightIcons.setPadding(
                new Insets(
                        0,
                        0,
                        0,
                        16
                )
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


        Circle badge =
                new Circle(
                        4,
                        Color.web("#EF4444")
                );

        StackPane.setAlignment(
                badge,
                Pos.TOP_RIGHT
        );


        notificationBox.getChildren()
                .addAll(
                        bellIcon,
                        badge
                );

        notificationBox.getStyleClass()
                .add(
                        "clickable-icon"
                );


        ImageView userAvatar =
                new ImageView(
                        ResourceImage.load(
                                "/images/mocks/dr_sarah_avatar.png"
                        )
                );

        userAvatar.setFitWidth(32);
        userAvatar.setFitHeight(32);


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


        rightIcons.getChildren()
                .addAll(
                        notificationBox,
                        userAvatar
                );


        topBar.getChildren()
                .addAll(
                        pageTitle,
                        spacer,
                        searchField,
                        rightIcons
                );


        return topBar;
    }


    // ============================================================
    // PATIENT HEADER
    // ============================================================

    private BorderPane createPatientHeader() {

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


        VBox patientInfo =
                new VBox(4);


        Label name =
                new Label(
                        getPatientName()
                );

        name.getStyleClass()
                .add(
                        "patient-name-title"
                );


        HBox metaRow =
                new HBox(10);

        metaRow.setAlignment(
                Pos.CENTER_LEFT
        );


        Label details =
                new Label(
                        getPatientMeta()
                );

        details.getStyleClass()
                .add(
                        "patient-sub-details"
                );


        Label tag =
                new Label(
                        "Medical Passport"
                );

        tag.getStyleClass()
                .add(
                        "tag-follow-up"
                );


        metaRow.getChildren()
                .addAll(
                        details,
                        tag
                );


        patientInfo.getChildren()
                .addAll(
                        name,
                        metaRow
                );


        // --------------------------------------------------------
        // ACTION BUTTONS
        // --------------------------------------------------------

        HBox actionBtns =
                new HBox(12);

        actionBtns.setAlignment(
                Pos.CENTER_RIGHT
        );


        Button downloadBtn =
                new Button(
                        "Download Reports"
                );


        ImageView dlIcon =
                new ImageView(
                        ResourceImage.load(
                                "/images/icons/ic_download.png"
                        )
                );

        dlIcon.setFitWidth(14);
        dlIcon.setFitHeight(14);


        downloadBtn.setGraphic(
                dlIcon
        );

        downloadBtn.getStyleClass()
                .add(
                        "btn-secondary-action"
                );


        downloadBtn.setOnAction(
                e -> downloadReports()
        );


        Button sendBtn =
                new Button(
                        "Send to Patient"
                );


        ImageView sendIcon =
                new ImageView(
                        ResourceImage.load(
                                "/images/icons/ic_send.png"
                        )
                );

        sendIcon.setFitWidth(14);
        sendIcon.setFitHeight(14);


        sendBtn.setGraphic(
                sendIcon
        );

        sendBtn.getStyleClass()
                .add(
                        "btn-primary-action"
                );


        sendBtn.setOnAction(
                e -> sendPrescription()
        );


        actionBtns.getChildren()
                .addAll(
                        downloadBtn,
                        sendBtn
                );


        header.setLeft(
                patientInfo
        );

        header.setRight(
                actionBtns
        );


        return header;
    }


    // ============================================================
    // PATIENT INFORMATION
    // ============================================================

    private String getPatientName() {

        if (patientProfile == null) {

            return "No Patient Selected";
        }


        String firstName =
                patientProfile.getFirstName();

        String lastName =
                patientProfile.getLastName();


        String fullName =
                (
                        (firstName == null
                                ? ""
                                : firstName)
                                + " "
                                + (lastName == null
                                ? ""
                                : lastName)
                ).trim();


        if (fullName.isEmpty()) {

            return "Patient";
        }


        return fullName;
    }


    private String getPatientMeta() {

        if (patientProfile == null) {

            return "Patient information unavailable";
        }


        String gender =
                patientProfile.getGender();


        String dateOfBirth =
                patientProfile.getDateOfBirth();


        String age =
                calculateAge(
                        dateOfBirth
                );


        String patientId =
                patientProfile.getUid();


        String genderText =
                gender == null
                        || gender.isBlank()
                        ? "N/A"
                        : gender;


        return genderText
                + " • "
                + age
                + " • ID: "
                + safeText(
                        patientId,
                        "N/A"
                );
    }


    private String calculateAge(
            String dateOfBirth
    ) {

        if (dateOfBirth == null
                || dateOfBirth.isBlank()) {

            return "Age N/A";
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


            return age + " yrs";

        } catch (Exception e) {

            return "Age N/A";
        }
    }


    // ============================================================
    // BODY
    // ============================================================

    private HBox createBodyLayout() {

        HBox layout =
                new HBox(20);


        // --------------------------------------------------------
        // LEFT COLUMN
        // --------------------------------------------------------

        VBox leftColumn =
                new VBox(20);

        HBox.setHgrow(
                leftColumn,
                Priority.ALWAYS
        );


        VBox rxEditorCard =
                createRxEditorCard();


        VBox medicalPassportCard =
                createMedicalPassportCard();


        leftColumn.getChildren()
                .addAll(
                        rxEditorCard,
                        medicalPassportCard
                );


        // --------------------------------------------------------
        // RIGHT COLUMN
        // --------------------------------------------------------

        VBox rightColumn =
                new VBox(20);

        rightColumn.setMinWidth(330);
        rightColumn.setMaxWidth(360);


        VBox clinicalAssistantCard =
                createClinicalAssistantCard();


        VBox recentHistoryCard =
                createRecentHistoryCard();


        rightColumn.getChildren()
                .addAll(
                        clinicalAssistantCard,
                        recentHistoryCard
                );


        layout.getChildren()
                .addAll(
                        leftColumn,
                        rightColumn
                );


        return layout;
    }


    // ============================================================
    // RX EDITOR
    // ============================================================

    private VBox createRxEditorCard() {

        VBox card =
                new VBox(16);

        card.getStyleClass()
                .add(
                        "panel-card"
                );

        card.setPadding(
                new Insets(20)
        );


        BorderPane cardHeader =
                new BorderPane();


        HBox titleBox =
                new HBox(8);

        titleBox.setAlignment(
                Pos.CENTER_LEFT
        );


        ImageView rxIcon =
                new ImageView(
                        ResourceImage.load(
                                "/images/icons/ic_rx.png"
                        )
                );

        rxIcon.setFitWidth(18);
        rxIcon.setFitHeight(18);


        Label titleLbl =
                new Label(
                        "Rx Editor"
                );

        titleLbl.getStyleClass()
                .add(
                        "section-card-title"
                );


        titleBox.getChildren()
                .addAll(
                        rxIcon,
                        titleLbl
                );


        Hyperlink addMedBtn =
                new Hyperlink(
                        "+ Add Medication"
                );

        addMedBtn.getStyleClass()
                .add(
                        "link-add-medication"
                );


        addMedBtn.setOnAction(
                e -> clearPrescriptionFields()
        );


        cardHeader.setLeft(
                titleBox
        );

        cardHeader.setRight(
                addMedBtn
        );


        // --------------------------------------------------------
        // FORM
        // --------------------------------------------------------

        VBox formBox =
                new VBox(12);

        formBox.getStyleClass()
                .add(
                        "med-form-box"
                );

        formBox.setPadding(
                new Insets(16)
        );


        BorderPane medHeader =
                new BorderPane();


        VBox nameBox =
                new VBox(6);


        Label medicationLabel =
                new Label(
                        "Medication"
                );

        medicationLabel.getStyleClass()
                .add(
                        "input-label"
                );


        TextField medicationInput =
                new TextField();

        medicationInput.setPromptText(
                "e.g. Amoxicillin"
        );

        medicationInput.getStyleClass()
                .add(
                        "input-field"
                );


        Label strengthLabel =
                new Label(
                        "Strength"
                );

        strengthLabel.getStyleClass()
                .add(
                        "input-label"
                );


        TextField strengthInput =
                new TextField();

        strengthInput.setPromptText(
                "e.g. 500mg"
        );

        strengthInput.getStyleClass()
                .add(
                        "input-field"
                );


        nameBox.getChildren()
                .addAll(
                        medicationLabel,
                        medicationInput,
                        strengthLabel,
                        strengthInput
                );


        medHeader.setLeft(
                nameBox
        );


        GridPane fieldsGrid =
                new GridPane();

        fieldsGrid.setHgap(12);
        fieldsGrid.setVgap(6);


        // DOSAGE

        fieldsGrid.add(
                createFieldLabel(
                        "Dosage"
                ),
                0,
                0
        );


        TextField dosageInput =
                new TextField();

        dosageInput.setPromptText(
                "e.g. 1 Tablet"
        );

        dosageInput.getStyleClass()
                .add(
                        "input-field"
                );


        fieldsGrid.add(
                dosageInput,
                0,
                1
        );


        // FREQUENCY

        fieldsGrid.add(
                createFieldLabel(
                        "Frequency"
                ),
                1,
                0
        );


        ComboBox<String> frequencyInput =
                new ComboBox<>();


        frequencyInput
                .getItems()
                .addAll(
                        "TID (3x a day)",
                        "BID (2x a day)",
                        "QD (1x a day)",
                        "QID (4x a day)",
                        "PRN (As needed)"
                );


        frequencyInput.setPromptText(
                "Select frequency"
        );


        frequencyInput.getStyleClass()
                .add(
                        "input-select"
                );


        fieldsGrid.add(
                frequencyInput,
                1,
                1
        );


        // DURATION

        fieldsGrid.add(
                createFieldLabel(
                        "Duration"
                ),
                2,
                0
        );


        TextField durationInput =
                new TextField();

        durationInput.setPromptText(
                "e.g. 7 Days"
        );

        durationInput.getStyleClass()
                .add(
                        "input-field"
                );


        fieldsGrid.add(
                durationInput,
                2,
                1
        );


        // TIMING

        fieldsGrid.add(
                createFieldLabel(
                        "Timing"
                ),
                3,
                0
        );


        TextField timingInput =
                new TextField();

        timingInput.setPromptText(
                "e.g. After Meals"
        );

        timingInput.getStyleClass()
                .add(
                        "input-field"
                );


        fieldsGrid.add(
                timingInput,
                3,
                1
        );


        for (int i = 0;
             i < 4;
             i++) {

            ColumnConstraints column =
                    new ColumnConstraints();

            column.setPercentWidth(
                    25
            );

            fieldsGrid
                    .getColumnConstraints()
                    .add(column);
        }


        formBox.getChildren()
                .addAll(
                        medHeader,
                        fieldsGrid
                );


        // --------------------------------------------------------
        // NOTES
        // --------------------------------------------------------

        VBox notesBox =
                new VBox(6);


        Label notesLabel =
                new Label(
                        "Physician Notes / Instructions"
                );

        notesLabel.getStyleClass()
                .add(
                        "input-label"
                );


        TextArea notesArea =
                new TextArea();

        notesArea.setPromptText(
                "Enter prescription instructions..."
        );

        notesArea.getStyleClass()
                .add(
                        "notes-text-area"
                );

        notesArea.setPrefRowCount(3);


        notesBox.getChildren()
                .addAll(
                        notesLabel,
                        notesArea
                );


        // --------------------------------------------------------
        // SAVE
        // --------------------------------------------------------

        HBox actionBox =
                new HBox(10);

        actionBox.setAlignment(
                Pos.CENTER_RIGHT
        );


        Button saveButton =
                new Button(
                        "Save Prescription"
                );

        saveButton.getStyleClass()
                .add(
                        "btn-primary-action"
                );


        saveButton.setOnAction(
                e ->
                        savePrescription(
                                medicationInput,
                                strengthInput,
                                dosageInput,
                                frequencyInput,
                                durationInput,
                                timingInput,
                                notesArea
                        )
        );


        actionBox.getChildren()
                .add(
                        saveButton
                );


        card.getChildren()
                .addAll(
                        cardHeader,
                        formBox,
                        notesBox,
                        actionBox
                );


        return card;
    }


    private Label createFieldLabel(
            String text
    ) {

        Label label =
                new Label(text);

        label.getStyleClass()
                .add(
                        "input-label"
                );

        return label;
    }


    // ============================================================
    // SAVE PRESCRIPTION
    // ============================================================

    private void savePrescription(
            TextField medicationInput,
            TextField strengthInput,
            TextField dosageInput,
            ComboBox<String> frequencyInput,
            TextField durationInput,
            TextField timingInput,
            TextArea notesInput
    ) {

        if (patientUid == null
                || patientUid.isBlank()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "No Patient Selected",
                    "Please select a patient before creating a prescription."
            );

            return;
        }


        String doctorUid =
                getCurrentDoctorUid();


        if (doctorUid == null
                || doctorUid.isBlank()) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Doctor Session",
                    "Doctor session is not available."
            );

            return;
        }


        String medication =
                medicationInput
                        .getText()
                        .trim();


        String strength =
                strengthInput
                        .getText()
                        .trim();


        String dosage =
                dosageInput
                        .getText()
                        .trim();


        String frequency =
                frequencyInput.getValue();


        String duration =
                durationInput
                        .getText()
                        .trim();


        String timing =
                timingInput
                        .getText()
                        .trim();


        String notes =
                notesInput
                        .getText()
                        .trim();


        // --------------------------------------------------------
        // VALIDATION
        // --------------------------------------------------------

        if (medication.isBlank()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Validation Error",
                    "Medication is required."
            );

            medicationInput.requestFocus();

            return;
        }


        if (strength.isBlank()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Validation Error",
                    "Strength is required."
            );

            strengthInput.requestFocus();

            return;
        }


        if (dosage.isBlank()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Validation Error",
                    "Dosage is required."
            );

            dosageInput.requestFocus();

            return;
        }


        if (frequency == null
                || frequency.isBlank()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Validation Error",
                    "Please select a frequency."
            );

            frequencyInput.requestFocus();

            return;
        }


        if (duration.isBlank()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Validation Error",
                    "Duration is required."
            );

            durationInput.requestFocus();

            return;
        }


        // --------------------------------------------------------
        // CREATE PRESCRIPTION
        // --------------------------------------------------------

        try {

            Prescription prescription =
                    new Prescription();


            prescription.setPatientUid(
                    patientUid
            );


            prescription.setDoctorUid(
                    doctorUid
            );


            /*
             * Appointment linking will be added when the
             * doctor opens this page from a specific appointment.
             */
            prescription.setAppointmentId(
                    null
            );


            prescription.setPatientName(
                    getPatientName()
            );


            prescription.setDoctorName(
                    "Doctor"
            );


            prescription.setMedication(
                    medication
            );


            prescription.setStrength(
                    strength
            );


            prescription.setDosage(
                    dosage
            );


            prescription.setFrequency(
                    frequency
            );


            prescription.setDuration(
                    duration
            );


            prescription.setTiming(
                    timing
            );


            prescription.setNotes(
                    notes
            );


            prescription.setStatus(
                    "ACTIVE"
            );


            prescriptionController
                    .createPrescription(
                            prescription
                    );


            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Prescription Saved",
                    "Prescription has been saved successfully."
            );


            clearPrescriptionFields(
                    medicationInput,
                    strengthInput,
                    dosageInput,
                    frequencyInput,
                    durationInput,
                    timingInput,
                    notesInput
            );


            /*
             * Refresh Existing Prescriptions.
             */
            refreshPatientData();


        } catch (Exception e) {

            e.printStackTrace();


            showAlert(
                    Alert.AlertType.ERROR,
                    "Save Failed",
                    e.getMessage()
            );
        }
    }


    // ============================================================
    // CLEAR PRESCRIPTION
    // ============================================================

    private void clearPrescriptionFields() {

        showAlert(
                Alert.AlertType.INFORMATION,
                "New Medication",
                "Enter the new medication details in the prescription form."
        );
    }


    private void clearPrescriptionFields(
            TextField medicationInput,
            TextField strengthInput,
            TextField dosageInput,
            ComboBox<String> frequencyInput,
            TextField durationInput,
            TextField timingInput,
            TextArea notesInput
    ) {

        medicationInput.clear();

        strengthInput.clear();

        dosageInput.clear();

        frequencyInput
                .getSelectionModel()
                .clearSelection();

        durationInput.clear();

        timingInput.clear();

        notesInput.clear();
    }


    // ============================================================
    // MEDICAL PASSPORT
    // ============================================================

    /**
     * Doctor can only view Medical Passport reports.
     *
     * No upload/delete buttons are provided here.
     */
    private VBox createMedicalPassportCard() {

        VBox card =
                new VBox(12);

        card.getStyleClass()
                .add(
                        "panel-card"
                );

        card.setPadding(
                new Insets(20)
        );


        HBox header =
                new HBox(8);

        header.setAlignment(
                Pos.CENTER_LEFT
        );


        ImageView icon =
                new ImageView(
                        ResourceImage.load(
                                "/images/icons/ic_reports.png"
                        )
                );

        icon.setFitWidth(18);
        icon.setFitHeight(18);


        Label title =
                new Label(
                        "Medical Passport"
                );

        title.getStyleClass()
                .add(
                        "section-card-title"
                );


        header.getChildren()
                .addAll(
                        icon,
                        title
                );


        Label description =
                new Label(
                        "Patient medical reports — "
                                + "view and download only."
                );

        description.setWrapText(
                true
        );

        description.setStyle(
                "-fx-text-fill: #64748B;"
                        + "-fx-font-size: 12px;"
        );


        VBox reportList =
                new VBox(10);


        loadMedicalReports(
                reportList
        );


        card.getChildren()
                .addAll(
                        header,
                        description,
                        reportList
                );


        return card;
    }


    // ============================================================
    // LOAD MEDICAL REPORTS
    // ============================================================

    private void loadMedicalReports(
            VBox reportList
    ) {

        reportList.getChildren()
                .clear();


        if (patientUid == null
                || patientUid.isBlank()) {

            Label empty =
                    new Label(
                            "Select a patient to view Medical Passport reports."
                    );

            empty.setWrapText(
                    true
            );

            empty.getStyleClass()
                    .add(
                            "history-item-subtext"
                    );


            reportList.getChildren()
                    .add(
                            empty
                    );

            return;
        }


        String doctorUid =
                getCurrentDoctorUid();


        if (doctorUid == null
                || doctorUid.isBlank()) {

            Label error =
                    new Label(
                            "Doctor session is not available."
                    );

            error.setStyle(
                    "-fx-text-fill: #EF4444;"
            );


            reportList.getChildren()
                    .add(
                            error
                    );

            return;
        }


        try {

            List<MedicalReport> reports =
                    medicalReportController
                            .getPatientMedicalReportsForDoctorSafe(
                                    doctorUid,
                                    patientUid
                            );


            if (reports == null
                    || reports.isEmpty()) {

                Label empty =
                        new Label(
                                "No medical reports found in the Medical Passport."
                        );

                empty.setWrapText(
                        true
                );

                empty.getStyleClass()
                        .add(
                                "history-item-subtext"
                        );


                reportList.getChildren()
                        .add(
                                empty
                        );

                return;
            }


            for (MedicalReport report :
                    reports) {

                if (report == null) {
                    continue;
                }


                reportList.getChildren()
                        .add(
                                createMedicalReportRow(
                                        report
                                )
                        );
            }


        } catch (Exception e) {

            e.printStackTrace();


            Label error =
                    new Label(
                            "Unable to load Medical Passport reports."
                    );

            error.setWrapText(
                    true
            );

            error.setStyle(
                    "-fx-text-fill: #EF4444;"
            );


            reportList.getChildren()
                    .add(
                            error
                    );
        }
    }


    // ============================================================
    // MEDICAL REPORT ROW
    // ============================================================

    private HBox createMedicalReportRow(
            MedicalReport report
    ) {

        HBox row =
                new HBox(12);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(10)
        );

        row.setStyle(
                "-fx-background-color: #F8FAFC;"
                        + "-fx-background-radius: 8px;"
        );


        VBox info =
                new VBox(3);

        HBox.setHgrow(
                info,
                Priority.ALWAYS
        );


        Label name =
                new Label(
                        safeText(
                                report.getReportName(),
                                "Medical Report"
                        )
                );

        name.setStyle(
                "-fx-font-weight: bold;"
                        + "-fx-text-fill: #1E293B;"
        );


        String details =
                safeText(
                        report.getReportType(),
                        "Medical Report"
                );


        if (report.getUploadedAt() != null
                && !report.getUploadedAt().isBlank()) {

            details +=
                    " • "
                            + formatDate(
                                    report.getUploadedAt()
                            );
        }


        Label detailsLabel =
                new Label(
                        details
                );

        detailsLabel.setWrapText(
                true
        );

        detailsLabel.setStyle(
                "-fx-text-fill: #64748B;"
                        + "-fx-font-size: 11px;"
        );


        info.getChildren()
                .addAll(
                        name,
                        detailsLabel
                );


        Button viewButton =
                new Button(
                        "View"
                );

        viewButton.getStyleClass()
                .add(
                        "btn-secondary-action"
                );


        viewButton.setOnAction(
                e ->
                        openMedicalReport(
                                report
                        )
        );


        row.getChildren()
                .addAll(
                        info,
                        viewButton
                );


        return row;
    }


    // ============================================================
    // OPEN MEDICAL REPORT
    // ============================================================

    private void openMedicalReport(
            MedicalReport report
    ) {

        if (report == null) {
            return;
        }


        try {

            String doctorUid =
                    getCurrentDoctorUid();


            if (doctorUid == null
                    || doctorUid.isBlank()) {

                showAlert(
                        Alert.AlertType.ERROR,
                        "Doctor Session",
                        "Doctor session is not available."
                );

                return;
            }


            String url =
                    medicalReportController
                            .getMedicalReportUrlForDoctor(
                                    doctorUid,
                                    report.getReportId()
                            );


            if (url == null
                    || url.isBlank()) {

                throw new IllegalStateException(
                        "Unable to generate medical report URL."
                );
            }


            if (!Desktop.isDesktopSupported()) {

                showAlert(
                        Alert.AlertType.ERROR,
                        "Unable to Open Report",
                        "Desktop browser is not available."
                );

                return;
            }


            Desktop.getDesktop()
                    .browse(
                            new URI(url)
                    );


        } catch (Exception e) {

            e.printStackTrace();


            showAlert(
                    Alert.AlertType.ERROR,
                    "Unable to Open Report",
                    e.getMessage()
            );
        }
    }


    // ============================================================
    // DOWNLOAD REPORTS
    // ============================================================

    private void downloadReports() {

        if (patientUid == null
                || patientUid.isBlank()) {

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "No Patient Selected",
                    "Please select a patient first."
            );

            return;
        }


        String doctorUid =
                getCurrentDoctorUid();


        if (doctorUid == null
                || doctorUid.isBlank()) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Doctor Session",
                    "Doctor session is not available."
            );

            return;
        }


        try {

            List<MedicalReport> reports =
                    medicalReportController
                            .getPatientMedicalReportsForDoctor(
                                    doctorUid,
                                    patientUid
                            );


            if (reports == null
                    || reports.isEmpty()) {

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "No Reports",
                        "This patient has no Medical Passport reports."
                );

                return;
            }


            for (MedicalReport report :
                    reports) {

                if (report == null
                        || report.getReportId() == null) {

                    continue;
                }


                String url =
                        medicalReportController
                                .getMedicalReportUrlForDoctor(
                                        doctorUid,
                                        report.getReportId()
                                );


                if (url != null
                        && !url.isBlank()
                        && Desktop.isDesktopSupported()) {

                    Desktop.getDesktop()
                            .browse(
                                    new URI(url)
                            );
                }
            }


        } catch (Exception e) {

            e.printStackTrace();


            showAlert(
                    Alert.AlertType.ERROR,
                    "Unable to Open Reports",
                    e.getMessage()
            );
        }
    }


    // ============================================================
    // EXISTING PRESCRIPTIONS
    // ============================================================

    private VBox createRecentHistoryCard() {

        VBox card =
                new VBox(16);

        card.getStyleClass()
                .add(
                        "panel-card"
                );

        card.setPadding(
                new Insets(18)
        );


        HBox titleBox =
                new HBox(8);

        titleBox.setAlignment(
                Pos.CENTER_LEFT
        );


        ImageView clockIcon =
                new ImageView(
                        ResourceImage.load(
                                "/images/icons/ic_history.png"
                        )
                );

        clockIcon.setFitWidth(18);
        clockIcon.setFitHeight(18);


        Label titleLbl =
                new Label(
                        "Existing Prescriptions"
                );

        titleLbl.getStyleClass()
                .add(
                        "section-card-title"
                );


        titleBox.getChildren()
                .addAll(
                        clockIcon,
                        titleLbl
                );


        VBox prescriptionList =
                new VBox(12);


        loadExistingPrescriptions(
                prescriptionList
        );


        card.getChildren()
                .addAll(
                        titleBox,
                        prescriptionList
                );


        return card;
    }


    // ============================================================
    // LOAD EXISTING PRESCRIPTIONS
    // ============================================================

    private void loadExistingPrescriptions(
            VBox prescriptionList
    ) {

        prescriptionList.getChildren()
                .clear();


        if (patientUid == null
                || patientUid.isBlank()) {

            Label empty =
                    new Label(
                            "Select a patient to view prescriptions."
                    );

            empty.setWrapText(
                    true
            );

            empty.getStyleClass()
                    .add(
                            "history-item-subtext"
                    );


            prescriptionList.getChildren()
                    .add(
                            empty
                    );

            return;
        }


        String doctorUid =
                getCurrentDoctorUid();


        if (doctorUid == null
                || doctorUid.isBlank()) {

            Label error =
                    new Label(
                            "Doctor session is not available."
                    );

            error.setStyle(
                    "-fx-text-fill: #EF4444;"
            );


            prescriptionList.getChildren()
                    .add(
                            error
                    );

            return;
        }


        try {

            List<Prescription> prescriptions =
                    prescriptionController
                            .getPatientPrescriptionsForDoctorSafe(
                                    doctorUid,
                                    patientUid
                            );


            if (prescriptions == null
                    || prescriptions.isEmpty()) {

                Label empty =
                        new Label(
                                "No prescriptions found for this patient."
                        );

                empty.setWrapText(
                        true
                );

                empty.getStyleClass()
                        .add(
                                "history-item-subtext"
                        );


                prescriptionList.getChildren()
                        .add(
                                empty
                        );

                return;
            }


            for (Prescription prescription :
                    prescriptions) {

                if (prescription == null) {
                    continue;
                }


                prescriptionList.getChildren()
                        .add(
                                createPrescriptionRow(
                                        prescription
                                )
                        );
            }


        } catch (Exception e) {

            e.printStackTrace();


            Label error =
                    new Label(
                            "Unable to load prescriptions."
                    );

            error.setWrapText(
                    true
            );

            error.setStyle(
                    "-fx-text-fill: #EF4444;"
            );


            prescriptionList.getChildren()
                    .add(
                            error
                    );
        }
    }


    // ============================================================
    // PRESCRIPTION ROW
    // ============================================================

    private VBox createPrescriptionRow(
            Prescription prescription
    ) {

        VBox box =
                new VBox(6);

        box.setPadding(
                new Insets(12)
        );

        box.setStyle(
                "-fx-background-color: #F8FAFC;"
                        + "-fx-background-radius: 8px;"
        );


        HBox medicationRow =
                new HBox(8);

        medicationRow.setAlignment(
                Pos.CENTER_LEFT
        );


        String medication =
                safeText(
                        prescription.getMedication(),
                        "Medication"
                );


        Label medicationLabel =
                new Label(
                        medication
                );

        medicationLabel.setStyle(
                "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #1E293B;"
        );


        String strength =
                safeText(
                        prescription.getStrength(),
                        ""
                );


        Label strengthLabel =
                new Label(
                        strength
                );

        strengthLabel.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-text-fill: #64748B;"
        );


        medicationRow.getChildren()
                .addAll(
                        medicationLabel,
                        strengthLabel
                );


        String dosage =
                safeText(
                        prescription.getDosage(),
                        "N/A"
                );


        String frequency =
                safeText(
                        prescription.getFrequency(),
                        "N/A"
                );


        String duration =
                safeText(
                        prescription.getDuration(),
                        "N/A"
                );


        Label dosageLabel =
                new Label(
                        dosage
                                + " • "
                                + frequency
                                + " • "
                                + duration
                );

        dosageLabel.setWrapText(
                true
        );

        dosageLabel.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-text-fill: #475569;"
        );


        String timing =
                safeText(
                        prescription.getTiming(),
                        ""
                );


        Label timingLabel =
                new Label(
                        timing.isBlank()
                                ? ""
                                : "Timing: " + timing
                );

        timingLabel.setWrapText(
                true
        );

        timingLabel.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-text-fill: #64748B;"
        );


        String status =
                safeText(
                        prescription.getStatus(),
                        "ACTIVE"
                );


        Label statusLabel =
                new Label(
                        status
                );

        statusLabel.setStyle(
                "-fx-font-size: 10px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #2563EB;"
        );


        box.getChildren()
                .addAll(
                        medicationRow,
                        dosageLabel
                );


        if (!timing.isBlank()) {

            box.getChildren()
                    .add(
                            timingLabel
                    );
        }


        box.getChildren()
                .add(
                        statusLabel
                );


        return box;
    }


    // ============================================================
    // CLINICAL ASSISTANT
    // ============================================================

    private VBox createClinicalAssistantCard() {

        VBox card =
                new VBox(14);

        card.getStyleClass()
                .add(
                        "assistant-card"
                );

        card.setPadding(
                new Insets(18)
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
                        "Clinical Assistant"
                );

        titleLbl.getStyleClass()
                .add(
                        "assistant-title"
                );


        titleBox.getChildren()
                .addAll(
                        aiIcon,
                        titleLbl
                );


        VBox alert1 =
                new VBox(6);

        alert1.getStyleClass()
                .add(
                        "assistant-alert-box"
                );

        alert1.setPadding(
                new Insets(12)
        );


        HBox alert1Header =
                new HBox(6);

        alert1Header.setAlignment(
                Pos.CENTER_LEFT
        );


        ImageView warnIcon =
                new ImageView(
                        ResourceImage.load(
                                "/images/icons/ic_warning.png"
                        )
                );

        warnIcon.setFitWidth(14);
        warnIcon.setFitHeight(14);


        Label alert1Title =
                new Label(
                        "Potential Interaction"
                );

        alert1Title.getStyleClass()
                .add(
                        "alert-warning-title"
                );


        alert1Header.getChildren()
                .addAll(
                        warnIcon,
                        alert1Title
                );


        Label alert1Text =
                new Label(
                        "Clinical interaction analysis "
                                + "can use the patient's "
                                + "prescription and clinical "
                                + "record data."
                );

        alert1Text.setWrapText(
                true
        );

        alert1Text.getStyleClass()
                .add(
                        "assistant-alert-desc"
                );


        alert1.getChildren()
                .addAll(
                        alert1Header,
                        alert1Text
                );


        VBox alert2 =
                new VBox(6);

        alert2.getStyleClass()
                .add(
                        "assistant-alert-box"
                );

        alert2.setPadding(
                new Insets(12)
        );


        HBox alert2Header =
                new HBox(6);

        alert2Header.setAlignment(
                Pos.CENTER_LEFT
        );


        ImageView lightIcon =
                new ImageView(
                        ResourceImage.load(
                                "/images/icons/ic_bulb.png"
                        )
                );

        lightIcon.setFitWidth(14);
        lightIcon.setFitHeight(14);


        Label alert2Title =
                new Label(
                        "Guideline Suggestion"
                );

        alert2Title.getStyleClass()
                .add(
                        "alert-info-title"
                );


        alert2Header.getChildren()
                .addAll(
                        lightIcon,
                        alert2Title
                );


        Label alert2Text =
                new Label(
                        "AI-based clinical guideline "
                                + "suggestions will be integrated "
                                + "later."
                );

        alert2Text.setWrapText(
                true
        );

        alert2Text.getStyleClass()
                .add(
                        "assistant-alert-desc"
                );


        alert2.getChildren()
                .addAll(
                        alert2Header,
                        alert2Text
                );


        card.getChildren()
                .addAll(
                        titleBox,
                        alert1,
                        alert2
                );


        return card;
    }


    // ============================================================
    // SEND PRESCRIPTION
    // ============================================================

    private void sendPrescription() {

        if (patientUid == null
                || patientUid.isBlank()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "No Patient Selected",
                    "Please select a patient first."
            );

            return;
        }


        showAlert(
                Alert.AlertType.INFORMATION,
                "Send Prescription",
                "Prescription sending will be connected to the patient notification workflow."
        );
    }


    // ============================================================
    // SESSION
    // ============================================================

    private String getCurrentDoctorUid() {

        SessionManager session =
                SessionManager.getInstance();


        if (!session.isLoggedIn()) {
            return null;
        }


        if (session.getCurrentUser() == null) {
            return null;
        }


        return session
                .getCurrentUser()
                .getUid();
    }


    // ============================================================
    // HELPERS
    // ============================================================

    private String safeText(
            String value,
            String fallback
    ) {

        if (value == null
                || value.isBlank()) {

            return fallback;
        }

        return value;
    }


    private String formatDate(
            String timestamp
    ) {

        if (timestamp == null
                || timestamp.isBlank()) {

            return "";
        }


        try {

            if (timestamp.length() >= 10) {

                return timestamp.substring(
                        0,
                        10
                );
            }

        } catch (Exception ignored) {
        }


        return timestamp;
    }


    private void showAlert(
            Alert.AlertType type,
            String title,
            String message
    ) {

        Alert alert =
                new Alert(type);

        alert.setTitle(title);

        alert.setHeaderText(null);

        alert.setContentText(
                message == null
                        ? ""
                        : message
        );

        alert.showAndWait();
    }
}