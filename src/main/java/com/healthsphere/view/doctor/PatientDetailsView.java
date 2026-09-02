package com.healthsphere.view.doctor;

import com.healthsphere.controller.doctor.PatientController;
import com.healthsphere.model.AuthenticationResponse;
import com.healthsphere.model.PatientProfile;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * PatientDetailsView displays detailed patient information.
 *
 * Features:
 * - Loads real patients from Firestore
 * - Shows only patients who have appointments with logged-in doctor
 * - Dynamic patient selection
 * - Existing patient details UI preserved
 * - Sidebar navigation preserved
 * - Existing CSS preserved
 *
 * Backend flow:
 *
 * SessionManager
 * ↓
 * Doctor UID
 * ↓
 * PatientController
 * ↓
 * AppointmentDAO
 * ↓
 * patientUid
 * ↓
 * PatientDAO
 * ↓
 * PatientProfile
 */
public class PatientDetailsView {

    private final Stage stage;
    private final Scene scene;

    // ============================================================
    // BACKEND CONTROLLER
    // ============================================================

    private final PatientController patientController;

    // ============================================================
    // DYNAMIC UI COMPONENT REFERENCES
    // ============================================================

    private Label nameLbl;
    private Label metaLbl;
    private Label idLbl;

    private ImageView profileImg;

    private VBox vitalsContent;

    private Label historyText;

    private HBox patientSelectorBar;

    private final List<Button> patientTabButtons = new ArrayList<>();

    // ============================================================
    // PATIENT DATA FOR EXISTING UI
    // ============================================================

    private static class PatientData {

        String name;
        String meta;
        String id;
        String imgPath;
        String[] vitals;
        String history;

        PatientData(
                String name,
                String meta,
                String id,
                String imgPath,
                String[] vitals,
                String history) {

            this.name = name;
            this.meta = meta;
            this.id = id;
            this.imgPath = imgPath;
            this.vitals = vitals;
            this.history = history;
        }
    }

    private final List<PatientData> patientList = new ArrayList<>();

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public PatientDetailsView(Stage stage) {

        this.stage = stage;

        /*
         * Create backend controller.
         */
        this.patientController = new PatientController();

        /*
         * Load real patients from Firestore.
         */
        initPatientData();

        /*
         * Create existing UI.
         */
        this.scene = createScene();
    }

    // ============================================================
    // GET SCENE
    // ============================================================

    public Scene getScene() {

        return this.scene;
    }

    // ============================================================
    // LOAD PATIENT DATA FROM FIRESTORE
    // ============================================================

    private void initPatientData() {

        patientList.clear();

        try {

            // ====================================================
            // GET CURRENT LOGGED-IN USER
            // ====================================================

            AuthenticationResponse authenticationResponse = SessionManager
                    .getInstance()
                    .getAuthenticationResponse();

            String doctorUid = authenticationResponse.getUid();

            if (doctorUid == null ||
                    doctorUid.trim().isEmpty()) {

                System.err.println(
                        "Doctor UID is missing.");

                return;
            }

            System.out.println(
                    "Loading patients for doctor: "
                            + doctorUid);

            // ====================================================
            // GET PATIENTS FOR DOCTOR
            // ====================================================

            List<PatientProfile> patients = patientController
                    .getPatientsForDoctor(
                            doctorUid);

            // ====================================================
            // CONVERT PATIENT PROFILE TO EXISTING UI DATA
            // ====================================================

            for (PatientProfile patientProfile : patients) {

                PatientData patientData = convertPatientProfile(
                        patientProfile);

                patientList.add(
                        patientData);
            }

            System.out.println(
                    "Patients loaded successfully: "
                            + patientList.size());

        } catch (Exception e) {

            System.err.println(
                    "Unable to load patients from Firestore.");

            e.printStackTrace();
        }
    }

    // ============================================================
    // CONVERT FIRESTORE PATIENT PROFILE
    // TO EXISTING PATIENT DATA
    // ============================================================

    private PatientData convertPatientProfile(
            PatientProfile patientProfile) {

        String firstName = patientProfile.getFirstName();

        String lastName = patientProfile.getLastName();

        if (firstName == null) {
            firstName = "";
        }

        if (lastName == null) {
            lastName = "";
        }

        String fullName = (firstName
                + " "
                + lastName).trim();

        if (fullName.isEmpty()) {

            fullName = "Unknown Patient";
        }

        // ========================================================
        // GENDER
        // ========================================================

        String gender = patientProfile.getGender();

        if (gender == null ||
                gender.trim().isEmpty()) {

            gender = "Not specified";
        }

        // ========================================================
        // DATE OF BIRTH
        // ========================================================

        String dateOfBirth = patientProfile.getDateOfBirth();

        if (dateOfBirth == null ||
                dateOfBirth.trim().isEmpty()) {

            dateOfBirth = "Date of birth not available";
        }

        // ========================================================
        // BLOOD GROUP
        // ========================================================

        String bloodGroup = patientProfile.getBloodGroup();

        if (bloodGroup == null ||
                bloodGroup.trim().isEmpty()) {

            bloodGroup = "Not specified";
        }

        // ========================================================
        // META INFORMATION
        // ========================================================

        String meta = gender
                + " • DOB: "
                + dateOfBirth
                + " • Blood Group: "
                + bloodGroup;

        // ========================================================
        // PATIENT ID
        // ========================================================

        String uid = patientProfile.getUid();

        if (uid == null ||
                uid.trim().isEmpty()) {

            uid = "Unknown";
        }

        String patientId = "Patient ID: #"
                + uid;

        // ========================================================
        // DEFAULT PROFILE IMAGE
        // ========================================================

        String imagePath = "/images/mocks/robert_chen.png";

        // ========================================================
        // VITALS
        // ========================================================
        //
        // PatientProfile currently does not contain vital fields.
        //
        // Therefore we do NOT invent medical values.
        //
        // These can later be loaded from a medical-record/vitals
        // collection.
        //

        String[] vitals = {

                "• Heart Rate: Not available",

                "• Blood Pressure: Not available",

                "• Temperature: Not available",

                "• SpO2: Not available"
        };

        // ========================================================
        // MEDICAL HISTORY
        // ========================================================
        //
        // PatientProfile currently does not contain a history
        // field.
        //
        // Therefore we display a clear message instead of fake
        // medical information.
        //

        String history = "No medical history or clinical notes "
                + "are currently available for this patient.";

        return new PatientData(

                fullName,

                meta,

                patientId,

                imagePath,

                vitals,

                history);
    }

    // ============================================================
    // CREATE SCENE
    // ============================================================

    private Scene createScene() {

        BorderPane mainRoot = new BorderPane();

        mainRoot.getStyleClass()
                .add("root-pane");

        // ========================================================
        // SIDEBAR
        // ========================================================

        VBox sidebar = createSidebar();

        mainRoot.setLeft(
                sidebar);

        // ========================================================
        // MAIN CONTENT AREA
        // ========================================================

        VBox contentArea = new VBox(20);

        contentArea.setPadding(
                new Insets(
                        20,
                        30,
                        30,
                        30));

        contentArea.getStyleClass()
                .add("content-area");

        // ========================================================
        // TOP HEADER
        // ========================================================

        HBox topHeader = createTopHeader();

        contentArea.getChildren()
                .add(topHeader);

        // ========================================================
        // TITLE
        // ========================================================

        BorderPane titleSection = createTitleSection();

        contentArea.getChildren()
                .add(titleSection);

        // ========================================================
        // PATIENT SELECTOR
        // ========================================================

        patientSelectorBar = createPatientSelectorBar();

        contentArea.getChildren()
                .add(
                        patientSelectorBar);

        // ========================================================
        // PATIENT OVERVIEW
        // ========================================================

        VBox patientCard = createPatientOverviewCard();

        contentArea.getChildren()
                .add(
                        patientCard);

        // ========================================================
        // DETAILS GRID
        // ========================================================

        GridPane detailsGrid = createDetailsGrid();

        contentArea.getChildren()
                .add(
                        detailsGrid);

        mainRoot.setCenter(
                contentArea);

        // ========================================================
        // OUTER SCROLL PANE
        // ========================================================

        ScrollPane outerScrollPane = new ScrollPane(
                mainRoot);

        outerScrollPane.setFitToWidth(
                true);

        outerScrollPane.setFitToHeight(
                true);

        outerScrollPane.getStyleClass()
                .add(
                        "content-scrollpane");

        Scene patientDetailsScene = new Scene(
                outerScrollPane,
                stage.getWidth(),
                stage.getHeight());

        // ========================================================
        // CSS
        // ========================================================

        try {

            patientDetailsScene
                    .getStylesheets()
                    .add(
                            Objects.requireNonNull(
                                    getClass()
                                            .getResource(
                                                    "/css/appointments.css"))
                                    .toExternalForm());

        } catch (Exception ignored) {
        }

        return patientDetailsScene;
    }

    // ============================================================
    // SIDEBAR
    // ============================================================

    private VBox createSidebar() {

        VBox sidebar = new VBox();

        sidebar.setPadding(
                new Insets(
                        25,
                        15,
                        25,
                        15));

        sidebar.getStyleClass()
                .add("sidebar");

        sidebar.setStyle(
                "-fx-background-color: #0F172A;");

        sidebar.setMinWidth(260);

        sidebar.setPrefWidth(260);

        sidebar.setMaxWidth(260);

        // ========================================================
        // LOGO
        // ========================================================

        HBox logoSection = new HBox(12);

        logoSection.setPadding(
                new Insets(
                        0,
                        0,
                        25,
                        5));

        logoSection.setAlignment(
                Pos.CENTER_LEFT);

        StackPane logoIconBox = new StackPane();

        logoIconBox.getStyleClass()
                .add(
                        "logo-icon-box");

        logoIconBox.setStyle(
                "-fx-background-color: #3B82F6; "
                        + "-fx-background-radius: 8px; "
                        + "-fx-padding: 8px;");

        ImageView logoIcon = new ImageView(
                ResourceImage.load(
                        "/images/icons/ic_shield.png"));

        logoIcon.setFitWidth(20);

        logoIcon.setFitHeight(20);

        logoIconBox.getChildren()
                .add(
                        logoIcon);

        VBox logoText = new VBox(2);

        Label appName = new Label(
                "Health-Sphere");

        appName.getStyleClass()
                .add("logo-name");

        appName.setStyle(
                "-fx-text-fill: #FFFFFF; "
                        + "-fx-font-weight: bold; "
                        + "-fx-font-size: 16px;");

        Label doctorSubtext = new Label(
                "Doctor Dashboard");

        doctorSubtext.getStyleClass()
                .add("logo-subtext");

        doctorSubtext.setStyle(
                "-fx-text-fill: #94A3B8; "
                        + "-fx-font-size: 12px;");

        logoText.getChildren()
                .addAll(
                        appName,
                        doctorSubtext);

        logoSection.getChildren()
                .addAll(
                        logoIconBox,
                        logoText);

        // ========================================================
        // NAVIGATION
        // ========================================================

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

            HBox navTab = new HBox(12);

            navTab.setAlignment(
                    Pos.CENTER_LEFT);

            navTab.setPadding(
                    new Insets(
                            10,
                            14,
                            10,
                            14));

            navTab.getStyleClass()
                    .add(
                            "nav-tab");

            ImageView icon = new ImageView(
                    ResourceImage.load(
                            "/images/icons/"
                                    + icons[i]
                                    + ".png"));

            icon.setFitWidth(18);

            icon.setFitHeight(18);

            Label tabLabel = new Label(
                    tabs[i]);

            tabLabel.getStyleClass()
                    .add(
                            "nav-text");

            // ====================================================
            // ACTIVE PATIENT DETAILS TAB
            // ====================================================

            if (i == 3) {

                navTab.getStyleClass()
                        .add(
                                "nav-tab-active");

                navTab.setStyle(
                        "-fx-background-color: #3B82F6; "
                                + "-fx-background-radius: 8px;");

                tabLabel.setStyle(
                        "-fx-text-fill: #FFFFFF; "
                                + "-fx-font-weight: bold; "
                                + "-fx-font-size: 14px;");

            } else {

                navTab.setStyle(
                        "-fx-background-color: transparent; "
                                + "-fx-background-radius: 8px;");

                tabLabel.setStyle(
                        "-fx-text-fill: #94A3B8; "
                                + "-fx-font-size: 14px;");
            }

            navTab.getChildren()
                    .addAll(
                            icon,
                            tabLabel);

            navItems.getChildren()
                    .add(
                            navTab);

            final int index = i;

            navTab.setOnMouseClicked(
                    e -> handleSidebarTabClick(
                            index));
        }

        // ========================================================
        // SPACER
        // ========================================================

        Region spacer = new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS);

        // ========================================================
        // FOOTER
        // ========================================================

        VBox footer = new VBox(10);

        footer.setPadding(
                new Insets(
                        15,
                        0,
                        0,
                        0));

        // ========================================================
        // DOCTOR PROFILE
        // ========================================================

        HBox sidebarProfile = new HBox(12);

        sidebarProfile.setAlignment(
                Pos.CENTER_LEFT);

        sidebarProfile.setPadding(
                new Insets(
                        10,
                        12,
                        10,
                        12));

        sidebarProfile.getStyleClass()
                .add(
                        "sidebar-profile-box");

        sidebarProfile.setStyle(
                "-fx-background-color: #1E293B; "
                        + "-fx-background-radius: 10px; "
                        + "-fx-cursor: hand;");

        ImageView profileAvatar = new ImageView(
                ResourceImage.load(
                        "/images/doctor/"
                                + "portrait-3d-male-doctor.png"));

        profileAvatar.setFitWidth(36);

        profileAvatar.setFitHeight(36);

        Circle profileClip = new Circle(
                18,
                18,
                18);

        profileAvatar.setClip(
                profileClip);

        VBox profileTexts = new VBox(2);

        Label profSubText = new Label(
                "Doctor Profile");

        profSubText.setStyle(
                "-fx-text-fill: #64748B; "
                        + "-fx-font-size: 11px;");

        Label profName = new Label(
                "Dr. Sarah");

        profName.getStyleClass()
                .add(
                        "sidebar-profile-name");

        profName.setStyle(
                "-fx-text-fill: #FFFFFF; "
                        + "-fx-font-weight: bold; "
                        + "-fx-font-size: 13px;");

        profileTexts.getChildren()
                .addAll(
                        profSubText,
                        profName);

        sidebarProfile.getChildren()
                .addAll(
                        profileAvatar,
                        profileTexts);

        sidebarProfile.setOnMouseClicked(
                e -> Navigation.goTo(
                        stage,
                        () -> new DoctorProfileView(
                                stage).getScene()));

        // ========================================================
        // LOGOUT
        // ========================================================

        HBox logoutTab = new HBox(12);

        logoutTab.setAlignment(
                Pos.CENTER_LEFT);

        logoutTab.setPadding(
                new Insets(
                        10,
                        14,
                        10,
                        14));

        logoutTab.getStyleClass()
                .add(
                        "nav-tab");

        logoutTab.setStyle(
                "-fx-cursor: hand;");

        ImageView logoutIcon = new ImageView(
                ResourceImage.load(
                        "/images/icons/ic_logout.png"));

        logoutIcon.setFitWidth(18);

        logoutIcon.setFitHeight(18);

        Label logoutLabel = new Label(
                "Logout");

        logoutLabel.getStyleClass()
                .add(
                        "nav-text");

        logoutLabel.setStyle(
                "-fx-text-fill: #94A3B8; "
                        + "-fx-font-size: 14px;");

        logoutTab.getChildren()
                .addAll(
                        logoutIcon,
                        logoutLabel);

        logoutTab.setOnMouseClicked(
                e -> System.out.println(
                        "Logging out..."));

        footer.getChildren()
                .addAll(
                        sidebarProfile,
                        logoutTab);

        sidebar.getChildren()
                .addAll(
                        logoSection,
                        navItems,
                        spacer,
                        footer);

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
                        () -> new DoctorDashboardView(
                                stage).getScene());

                break;

            case 1:

                Navigation.goTo(
                        stage,
                        () -> new TodaysScheduleView(
                                stage).getScene());

                break;

            case 2:

                Navigation.goTo(
                        stage,
                        () -> new AppointmentsView(
                                stage).getScene());

                break;

            case 3:

                Navigation.goTo(
                        stage,
                        () -> new PatientDetailsView(
                                stage).getScene());

                break;

            case 4:

                Navigation.goTo(
                        stage,
                        () -> new MedicalReportsView(
                                stage).getScene());

                break;

            case 5:

                Navigation.goTo(
                        stage,
                        () -> new AvailabilityScheduleView(
                                stage).getScene());

                break;

            case 6:

                Navigation.goTo(
                        stage,
                        () -> new DoctorProfileView(
                                stage).getScene());

                break;

            case 7:

                Navigation.goTo(
                        stage,
                        () -> new AIHealthAssistantView(
                                stage).getScene());

                break;

            default:
                break;
        }
    }

    // ============================================================
    // PATIENT SELECTOR BAR
    // ============================================================

    private HBox createPatientSelectorBar() {

        HBox bar = new HBox(10);

        bar.setAlignment(
                Pos.CENTER_LEFT);

        bar.setPadding(
                new Insets(
                        5,
                        0,
                        5,
                        0));

        Label selectLabel = new Label(
                "Select Patient:");

        selectLabel.setStyle(
                "-fx-font-weight: bold; "
                        + "-fx-text-fill: #475569; "
                        + "-fx-font-size: 13px;");

        bar.getChildren()
                .add(
                        selectLabel);

        rebuildPatientTabs(
                bar);

        return bar;
    }

    // ============================================================
    // REBUILD PATIENT TABS
    // ============================================================

    private void rebuildPatientTabs(
            HBox bar) {

        bar.getChildren()
                .removeIf(
                        node -> node instanceof Button);

        patientTabButtons.clear();

        for (int i = 0; i < patientList.size(); i++) {

            PatientData patient = patientList.get(i);

            Button patientBtn = new Button(
                    patient.name);

            patientBtn.setCursor(
                    javafx.scene.Cursor.HAND);

            final int index = i;

            patientBtn.setOnAction(
                    e -> switchPatient(
                            index));

            patientTabButtons.add(
                    patientBtn);

            bar.getChildren()
                    .add(
                            patientBtn);
        }

        if (!patientList.isEmpty()) {

            updatePatientTabStyles(
                    0);
        }
    }

    // ============================================================
    // SWITCH PATIENT
    // ============================================================

    private void switchPatient(
            int index) {

        if (index < 0 ||
                index >= patientList.size()) {

            return;
        }

        PatientData patient = patientList.get(index);

        nameLbl.setText(
                patient.name);

        metaLbl.setText(
                patient.meta);

        idLbl.setText(
                patient.id);

        if (patient.imgPath != null) {

            profileImg.setImage(
                    ResourceImage.load(
                            patient.imgPath));
        }

        vitalsContent
                .getChildren()
                .clear();

        for (String vital : patient.vitals) {

            vitalsContent
                    .getChildren()
                    .add(
                            new Label(
                                    vital));
        }

        historyText.setText(
                patient.history);

        updatePatientTabStyles(
                index);
    }

    // ============================================================
    // UPDATE PATIENT TAB STYLES
    // ============================================================

    private void updatePatientTabStyles(
            int activeIndex) {

        for (int i = 0; i < patientTabButtons.size(); i++) {

            Button btn = patientTabButtons.get(i);

            if (i == activeIndex) {

                btn.setStyle(
                        "-fx-background-color: #3B82F6; "
                                + "-fx-text-fill: white; "
                                + "-fx-font-weight: bold; "
                                + "-fx-background-radius: 6px; "
                                + "-fx-padding: 6 14;");

            } else {

                btn.setStyle(
                        "-fx-background-color: #E2E8F0; "
                                + "-fx-text-fill: #334155; "
                                + "-fx-font-weight: normal; "
                                + "-fx-background-radius: 6px; "
                                + "-fx-padding: 6 14;");
            }
        }
    }

    // ============================================================
    // TITLE SECTION
    // ============================================================

    private BorderPane createTitleSection() {

        BorderPane section = new BorderPane();

        VBox titleBox = new VBox(2);

        Label mainTitle = new Label(
                "Patient Details");

        mainTitle.getStyleClass()
                .add(
                        "page-title");

        Label subTitle = new Label(
                "Comprehensive medical record and personal profile");

        subTitle.getStyleClass()
                .add(
                        "page-subtitle");

        titleBox.getChildren()
                .addAll(
                        mainTitle,
                        subTitle);

        Button addPatientBtn = new Button(
                "+ Add Patient");

        addPatientBtn.getStyleClass()
                .add(
                        "btn-primary-action");

        /*
         * Keep original button behavior.
         *
         * Note:
         * This still adds only temporary UI data.
         * We will later decide whether doctors should be
         * allowed to create patient profiles.
         */
        addPatientBtn.setOnAction(
                e -> openAddPatientDialog());

        section.setLeft(
                titleBox);

        section.setRight(
                addPatientBtn);

        return section;
    }

    // ============================================================
    // ADD PATIENT DIALOG
    // ============================================================

    private void openAddPatientDialog() {

        Stage dialog = new Stage();

        dialog.initModality(
                Modality.APPLICATION_MODAL);

        dialog.initOwner(
                stage);

        dialog.setTitle(
                "Add New Patient");

        VBox form = new VBox(12);

        form.setPadding(
                new Insets(20));

        form.setStyle(
                "-fx-background-color: #FFFFFF;");

        Label dialogTitle = new Label(
                "New Patient Information");

        dialogTitle.setStyle(
                "-fx-font-size: 16px; "
                        + "-fx-font-weight: bold; "
                        + "-fx-text-fill: #1E293B;");

        TextField nameField = new TextField();

        nameField.setPromptText(
                "Full Name (e.g. John Doe)");

        TextField ageField = new TextField();

        ageField.setPromptText(
                "Age (e.g. 30)");

        ComboBox<String> genderBox = new ComboBox<>();

        genderBox.getItems()
                .addAll(
                        "Male",
                        "Female",
                        "Other");

        genderBox
                .getSelectionModel()
                .selectFirst();

        TextField bloodGroupField = new TextField();

        bloodGroupField.setPromptText(
                "Blood Group (e.g. O+)");

        TextField vitalsField = new TextField();

        vitalsField.setPromptText(
                "Vitals (comma-separated)");

        TextArea historyArea = new TextArea();

        historyArea.setPromptText(
                "Medical History & Notes...");

        historyArea.setPrefRowCount(
                3);

        HBox actionButtons = new HBox(10);

        actionButtons.setAlignment(
                Pos.CENTER_RIGHT);

        Button cancelBtn = new Button(
                "Cancel");

        cancelBtn.setOnAction(
                e -> dialog.close());

        Button saveBtn = new Button(
                "Add Patient");

        saveBtn.setStyle(
                "-fx-background-color: #3B82F6; "
                        + "-fx-text-fill: white; "
                        + "-fx-font-weight: bold;");

        saveBtn.setOnAction(e -> {

            String name = nameField
                    .getText()
                    .trim();

            if (name.isEmpty()) {

                name = "New Patient";
            }

            String age = ageField
                    .getText()
                    .trim();

            if (age.isEmpty()) {

                age = "30";
            }

            String gender = genderBox.getValue();

            String bloodGroup = bloodGroupField
                    .getText()
                    .trim();

            if (bloodGroup.isEmpty()) {

                bloodGroup = "A+";
            }

            String meta = gender
                    + " • "
                    + age
                    + " Years Old • Blood Group: "
                    + bloodGroup;

            String[] vitals;

            if (!vitalsField
                    .getText()
                    .trim()
                    .isEmpty()) {

                String[] rawVitals = vitalsField
                        .getText()
                        .split(",");

                vitals = new String[rawVitals.length];

                for (int i = 0; i < rawVitals.length; i++) {

                    vitals[i] = "• "
                            + rawVitals[i]
                                    .trim();
                }

            } else {

                vitals = new String[] {
                        "• Heart Rate: 72 bpm",
                        "• Blood Pressure: 120/80 mmHg",
                        "• Temperature: 98.6 °F",
                        "• SpO2: 99%"
                };
            }

            String history = historyArea
                    .getText()
                    .trim();

            if (history.isEmpty()) {

                history = "No prior medical history recorded.";
            }

            /*
             * Keep original temporary UI behavior.
             */
            String randomId = "Patient ID: #TEMP-"
                    + System.currentTimeMillis();

            PatientData newPatient = new PatientData(

                    name,

                    meta,

                    randomId,

                    "/images/mocks/robert_chen.png",

                    vitals,

                    history);

            patientList.add(
                    newPatient);

            rebuildPatientTabs(
                    patientSelectorBar);

            if (!patientList.isEmpty()) {

                switchPatient(
                        patientList.size() - 1);
            }

            dialog.close();
        });

        actionButtons.getChildren()
                .addAll(
                        cancelBtn,
                        saveBtn);

        form.getChildren()
                .addAll(

                        dialogTitle,

                        new Label("Name:"),
                        nameField,

                        new Label("Age:"),
                        ageField,

                        new Label("Gender:"),
                        genderBox,

                        new Label("Blood Group:"),
                        bloodGroupField,

                        new Label("Vitals:"),
                        vitalsField,

                        new Label("History & Notes:"),
                        historyArea,

                        actionButtons);

        Scene dialogScene = new Scene(
                form,
                400,
                500);

        dialog.setScene(
                dialogScene);

        dialog.showAndWait();
    }

    // ============================================================
    // PATIENT OVERVIEW CARD
    // ============================================================

    private VBox createPatientOverviewCard() {

        VBox card = new VBox(15);

        card.getStyleClass()
                .add(
                        "filter-container-card");

        card.setPadding(
                new Insets(20));

        HBox profileHeader = new HBox(20);

        profileHeader.setAlignment(
                Pos.CENTER_LEFT);

        /*
         * ========================================================
         * NO PATIENTS
         * ========================================================
         */

        if (patientList.isEmpty()) {

            profileImg = new ImageView();

            profileImg.setFitWidth(70);

            profileImg.setFitHeight(70);

            StackPane emptyAvatar = new StackPane();

            emptyAvatar.setPrefSize(
                    70,
                    70);

            emptyAvatar.setStyle(
                    "-fx-background-color: #E2E8F0; "
                            + "-fx-background-radius: 35px;");

            Label emptyInitial = new Label(
                    "?");

            emptyInitial.setStyle(
                    "-fx-font-size: 24px; "
                            + "-fx-font-weight: bold; "
                            + "-fx-text-fill: #64748B;");

            emptyAvatar.getChildren()
                    .add(
                            emptyInitial);

            VBox infoBox = new VBox(4);

            nameLbl = new Label(
                    "No Patients Found");

            nameLbl.setStyle(
                    "-fx-font-size: 20px; "
                            + "-fx-font-weight: bold; "
                            + "-fx-text-fill: #1E293B;");

            metaLbl = new Label(
                    "Patients will appear here "
                            + "after they book an appointment "
                            + "with you.");

            metaLbl.setWrapText(
                    true);

            metaLbl.setStyle(
                    "-fx-font-size: 14px; "
                            + "-fx-text-fill: #64748B;");

            idLbl = new Label(
                    "");

            infoBox.getChildren()
                    .addAll(
                            nameLbl,
                            metaLbl,
                            idLbl);

            profileHeader.getChildren()
                    .addAll(
                            emptyAvatar,
                            infoBox);

            card.getChildren()
                    .add(
                            profileHeader);

            return card;
        }

        // ========================================================
        // FIRST PATIENT
        // ========================================================

        PatientData initialData = patientList.get(0);

        profileImg = new ImageView(
                ResourceImage.load(
                        initialData.imgPath));

        profileImg.setFitWidth(70);

        profileImg.setFitHeight(70);

        Circle clip = new Circle(
                35,
                35,
                35);

        profileImg.setClip(
                clip);

        VBox infoBox = new VBox(4);

        nameLbl = new Label(
                initialData.name);

        nameLbl.setStyle(
                "-fx-font-size: 20px; "
                        + "-fx-font-weight: bold; "
                        + "-fx-text-fill: #1E293B;");

        metaLbl = new Label(
                initialData.meta);

        metaLbl.setWrapText(
                true);

        metaLbl.setStyle(
                "-fx-font-size: 14px; "
                        + "-fx-text-fill: #64748B;");

        idLbl = new Label(
                initialData.id);

        idLbl.setStyle(
                "-fx-font-size: 13px; "
                        + "-fx-text-fill: #3B82F6; "
                        + "-fx-font-weight: bold;");

        infoBox.getChildren()
                .addAll(
                        nameLbl,
                        metaLbl,
                        idLbl);

        profileHeader.getChildren()
                .addAll(
                        profileImg,
                        infoBox);

        card.getChildren()
                .add(
                        profileHeader);

        return card;
    }

    // ============================================================
    // DETAILS GRID
    // ============================================================

    private GridPane createDetailsGrid() {

        GridPane grid = new GridPane();

        grid.setHgap(20);

        grid.setVgap(20);

        // ========================================================
        // NO PATIENTS
        // ========================================================

        if (patientList.isEmpty()) {

            VBox emptyCard = new VBox(12);

            emptyCard.getStyleClass()
                    .add(
                            "filter-container-card");

            emptyCard.setPadding(
                    new Insets(20));

            Label title = new Label(
                    "Patient Information");

            title.setStyle(
                    "-fx-font-size: 16px; "
                            + "-fx-font-weight: bold; "
                            + "-fx-text-fill: #1E293B;");

            Label message = new Label(
                    "There are currently no patients "
                            + "associated with your appointments.");

            message.setWrapText(
                    true);

            message.setStyle(
                    "-fx-text-fill: #64748B; "
                            + "-fx-font-size: 13px;");

            emptyCard.getChildren()
                    .addAll(
                            title,
                            message);

            ColumnConstraints fullColumn = new ColumnConstraints();

            fullColumn.setPercentWidth(
                    100);

            grid.getColumnConstraints()
                    .add(
                            fullColumn);

            grid.add(
                    emptyCard,
                    0,
                    0);

            return grid;
        }

        // ========================================================
        // INITIAL PATIENT
        // ========================================================

        PatientData initialData = patientList.get(0);

        // ========================================================
        // VITALS CARD
        // ========================================================

        VBox vitalsCard = new VBox(12);

        vitalsCard.getStyleClass()
                .add(
                        "filter-container-card");

        vitalsCard.setPadding(
                new Insets(20));

        Label vitalsTitle = new Label(
                "Recent Vitals");

        vitalsTitle.setStyle(
                "-fx-font-size: 16px; "
                        + "-fx-font-weight: bold; "
                        + "-fx-text-fill: #1E293B;");

        vitalsContent = new VBox(8);

        for (String vital : initialData.vitals) {

            vitalsContent
                    .getChildren()
                    .add(
                            new Label(
                                    vital));
        }

        vitalsCard.getChildren()
                .addAll(
                        vitalsTitle,
                        vitalsContent);

        // ========================================================
        // HISTORY CARD
        // ========================================================

        VBox historyCard = new VBox(12);

        historyCard.getStyleClass()
                .add(
                        "filter-container-card");

        historyCard.setPadding(
                new Insets(20));

        Label historyTitle = new Label(
                "Medical History & Notes");

        historyTitle.setStyle(
                "-fx-font-size: 16px; "
                        + "-fx-font-weight: bold; "
                        + "-fx-text-fill: #1E293B;");

        historyText = new Label(
                initialData.history);

        historyText.setWrapText(
                true);

        historyText.setStyle(
                "-fx-text-fill: #475569; "
                        + "-fx-font-size: 13px;");

        historyCard.getChildren()
                .addAll(
                        historyTitle,
                        historyText);

        // ========================================================
        // COLUMNS
        // ========================================================

        ColumnConstraints col1 = new ColumnConstraints();

        col1.setPercentWidth(
                50);

        ColumnConstraints col2 = new ColumnConstraints();

        col2.setPercentWidth(
                50);

        grid.getColumnConstraints()
                .addAll(
                        col1,
                        col2);

        grid.add(
                vitalsCard,
                0,
                0);

        grid.add(
                historyCard,
                1,
                0);

        return grid;
    }

    // ============================================================
    // TOP HEADER
    // ============================================================

    private HBox createTopHeader() {

        HBox topBar = new HBox();

        topBar.setAlignment(
                Pos.CENTER_RIGHT);

        // ========================================================
        // SEARCH
        // ========================================================

        HBox searchField = new HBox(8);

        searchField.getStyleClass()
                .add(
                        "search-input-box");

        searchField.setAlignment(
                Pos.CENTER_LEFT);

        ImageView searchIcon = new ImageView(
                ResourceImage.load(
                        "/images/icons/ic_search.png"));

        searchIcon.setFitWidth(16);

        searchIcon.setFitHeight(16);

        TextField searchInput = new TextField();

        searchInput.setPromptText(
                "Search patients or IDs...");

        searchInput.getStyleClass()
                .add(
                        "search-text-field");

        searchField.getChildren()
                .addAll(
                        searchIcon,
                        searchInput);

        // ========================================================
        // SPACER
        // ========================================================

        Region spacer = new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS);

        // ========================================================
        // RIGHT ICONS
        // ========================================================

        HBox rightIcons = new HBox(18);

        rightIcons.setAlignment(
                Pos.CENTER_RIGHT);

        // ========================================================
        // NOTIFICATION
        // ========================================================

        StackPane notificationBox = new StackPane();

        ImageView bellIcon = new ImageView(
                ResourceImage.load(
                        "/images/icons/ic_bell.png"));

        bellIcon.setFitWidth(18);

        bellIcon.setFitHeight(18);

        Circle badge = new Circle(
                4,
                Color.RED);

        StackPane.setAlignment(
                badge,
                Pos.TOP_RIGHT);

        notificationBox.getChildren()
                .addAll(
                        bellIcon,
                        badge);

        notificationBox.getStyleClass()
                .add(
                        "clickable-icon");

        // ========================================================
        // USER AVATAR
        // ========================================================

        ImageView userAvatar = new ImageView(
                ResourceImage.load(
                        "/images/doctor/"
                                + "portrait-3d-male-doctor.png"));

        userAvatar.setFitWidth(32);

        userAvatar.setFitHeight(32);

        Circle clip = new Circle(
                16,
                16,
                16);

        userAvatar.setClip(
                clip);

        userAvatar.getStyleClass()
                .add(
                        "clickable-icon");

        userAvatar.setOnMouseClicked(
                e -> Navigation.goTo(
                        stage,
                        () -> new DoctorProfileView(
                                stage).getScene()));

        rightIcons.getChildren()
                .addAll(
                        notificationBox,
                        userAvatar);

        topBar.getChildren()
                .addAll(
                        searchField,
                        spacer,
                        rightIcons);

        return topBar;
    }
}