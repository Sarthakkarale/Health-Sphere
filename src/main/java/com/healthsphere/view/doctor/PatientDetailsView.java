package com.healthsphere.view.doctor;

import com.healthsphere.controller.doctor.PatientController;
import com.healthsphere.dao.appointment.AppointmentDAO;
import com.healthsphere.dao.medical.MedicalRecordDAO;
import com.healthsphere.model.Appointment;
import com.healthsphere.model.MedicalRecord;
import com.healthsphere.model.PatientProfile;
import com.healthsphere.util.Navigation;
import com.healthsphere.util.ResourceImage;
import com.healthsphere.util.SessionManager;
import com.healthsphere.util.ShimmerPlaceholder;
import com.healthsphere.view.authentication.LoginView;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * PatientDetailsView
 *
 * Doctor-side patient details screen.
 *
 * Architecture:
 *
 * View
 *   ↓
 * Controller / DAO
 *   ↓
 * Firebase Firestore
 *
 * Important project rules:
 * - Uses the existing application Stage.
 * - Does not create a new application Stage.
 * - Creates and returns a Scene.
 * - Uses Navigation.goTo(...) for navigation.
 * - Uses SessionManager for current doctor identity.
 * - Uses real Firestore data.
 * - No mock patient data.
 */
public class PatientDetailsView {

    // ============================================================
    // COMMON APPLICATION STAGE
    // ============================================================

    private final Stage stage;

    private Scene scene;

    // ============================================================
    // CONTROLLERS / DAOS
    // ============================================================

    private final PatientController patientController;
    private final MedicalRecordDAO medicalRecordDAO;
    private final AppointmentDAO appointmentDAO;

    // ============================================================
    // CURRENT DOCTOR
    // ============================================================

    private String doctorUid;

    // ============================================================
    // PATIENT DATA
    // ============================================================

    private List<PatientProfile> doctorPatients =
            new ArrayList<>();

    private PatientProfile selectedPatient;

    private List<MedicalRecord> medicalRecords =
            new ArrayList<>();

    private List<Appointment> appointments =
            new ArrayList<>();

    // ============================================================
    // UI REFERENCES
    // ============================================================

    private TextField searchField;

    private Label matchingCountLabel;

    private ComboBox<PatientProfile> patientSelector;

    private VBox patientListContainer;

    private Label patientNameLabel;
    private Label patientMetaLabel;
    private Label patientIdLabel;

    private Label relationshipLabel;

    private Label doctorNameLabel;
    private Label doctorSpecializationLabel;

    private VBox patientInformationContainer;

    private VBox latestVitalsContainer;

    private VBox medicalRecordsContainer;

    private VBox appointmentsContainer;

    private Label recordsCountLabel;

    // ============================================================
    // CONSTRUCTORS
    // ============================================================

    /**
     * Default constructor.
     *
     * Used by existing sidebar navigation.
     */
    public PatientDetailsView(Stage stage) {

        this(stage, null);
    }

    /**
     * Patient-specific constructor.
     *
     * Can be used when opening Patient Details for
     * a particular patient.
     */
    public PatientDetailsView(
            Stage stage,
            String patientUid
    ) {
        if (stage == null) {
            throw new IllegalArgumentException("Application Stage cannot be null.");
        }

        this.stage = stage;
        this.patientController = new PatientController();
        this.medicalRecordDAO = new MedicalRecordDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.doctorUid = getCurrentDoctorUid();

        // Create scene immediately so navigation is instant
        this.scene = createScene();

        // Load patients asynchronously
        loadDoctorPatientsAsync(patientUid);
    }

    public Scene getScene() {
        return scene;
    }

    private void loadDoctorPatientsAsync(String initialPatientUid) {
        if (doctorUid == null || doctorUid.trim().isEmpty()) {
            return;
        }

        Task<List<PatientProfile>> loadTask = new Task<>() {
            @Override
            protected List<PatientProfile> call() throws Exception {
                List<PatientProfile> patients = patientController.getPatientsForDoctorSafe(doctorUid);
                return patients != null ? patients : new ArrayList<>();
            }
        };

        loadTask.setOnSucceeded(event -> {
            doctorPatients = loadTask.getValue();
            if (doctorPatients != null) {
                doctorPatients.removeIf(Objects::isNull);
            } else {
                doctorPatients = new ArrayList<>();
            }

            if (patientSelector != null) {
                patientSelector.getItems().setAll(doctorPatients);
                patientSelector.setPromptText(doctorPatients.isEmpty() ? "No patients found" : "Choose a patient");
            }

            if (initialPatientUid != null && !initialPatientUid.trim().isEmpty()) {
                selectPatientByUid(initialPatientUid);
            } else if (!doctorPatients.isEmpty()) {
                selectedPatient = doctorPatients.get(0);
            }

            if (patientSelector != null && selectedPatient != null) {
                patientSelector.setValue(selectedPatient);
            }

            updateMatchingCount(doctorPatients.size());

            if (selectedPatient != null) {
                loadSelectedPatientDataAsync();
            } else {
                refreshPatientUI();
            }
        });

        Thread thread = new Thread(loadTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadSelectedPatientDataAsync() {
        if (selectedPatient == null || selectedPatient.getUid() == null || selectedPatient.getUid().trim().isEmpty()) {
            refreshPatientUI();
            return;
        }

        String targetUid = selectedPatient.getUid();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                medicalRecords = new ArrayList<>();
                appointments = new ArrayList<>();

                try {
                    List<MedicalRecord> records = medicalRecordDAO.getPatientMedicalRecords(targetUid);
                    if (records != null) medicalRecords.addAll(records);
                } catch (Exception ignored) {}

                try {
                    List<Appointment> patientApps = appointmentDAO.getPatientAppointments(targetUid);
                    if (patientApps != null) appointments.addAll(patientApps);
                } catch (Exception ignored) {}

                medicalRecords.sort(Comparator.comparing(PatientDetailsView.this::getRecordDateValue, Comparator.nullsLast(Comparator.reverseOrder())));
                appointments.sort(Comparator.comparing(PatientDetailsView.this::getAppointmentDateValue, Comparator.nullsLast(Comparator.reverseOrder())));
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            refreshPatientUI();
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    // ============================================================
    // LOAD CURRENT DOCTOR
    // ============================================================

    private String getCurrentDoctorUid() {

        SessionManager session =
                SessionManager.getInstance();

        if (session == null) {

            return null;
        }

        if (!session.isLoggedIn()) {

            return null;
        }

        if (session.getCurrentUser() == null) {

            return null;
        }

        String uid =
                session.getCurrentUser().getUid();

        if (uid == null
                || uid.trim().isEmpty()) {

            return null;
        }

        return uid.trim();
    }

    // ============================================================
    // LOAD DOCTOR PATIENTS
    // ============================================================

    private void loadDoctorPatients() {

        doctorPatients =
                new ArrayList<>();

        if (doctorUid == null
                || doctorUid.trim().isEmpty()) {

            return;
        }

        try {

            List<PatientProfile> patients =
                    patientController
                            .getPatientsForDoctorSafe(
                                    doctorUid
                            );

            if (patients != null) {

                doctorPatients.addAll(
                        patients
                );
            }

        } catch (Exception e) {

            System.err.println(
                    "Unable to load doctor patients: "
                            + e.getMessage()
            );

            doctorPatients.clear();
        }

        doctorPatients.removeIf(
                Objects::isNull
        );
    }

    // ============================================================
    // SELECT PATIENT
    // ============================================================

    private void selectPatientByUid(
            String patientUid
    ) {

        if (patientUid == null
                || patientUid.trim().isEmpty()) {

            return;
        }

        for (PatientProfile patient :
                doctorPatients) {

            if (patient != null
                    && patientUid.equals(
                            patient.getUid()
                    )) {

                selectedPatient =
                        patient;

                return;
            }
        }

        /*
         * If the patient is not already in the
         * doctor patient list, try Firestore.
         */
        try {

            PatientProfile patient =
                    patientController
                            .getPatientProfile(
                                    patientUid
                            );

            if (patient != null) {

                selectedPatient =
                        patient;
            }

        } catch (Exception e) {

            System.err.println(
                    "Unable to load patient "
                            + patientUid
                            + ": "
                            + e.getMessage()
            );
        }
    }

    // ============================================================
    // LOAD SELECTED PATIENT DATA
    // ============================================================

    private void loadSelectedPatientData() {

        medicalRecords =
                new ArrayList<>();

        appointments =
                new ArrayList<>();

        if (selectedPatient == null
                || selectedPatient.getUid() == null
                || selectedPatient.getUid().trim().isEmpty()) {

            return;
        }

        String patientUid =
                selectedPatient.getUid();

        // --------------------------------------------------------
        // MEDICAL RECORDS
        // --------------------------------------------------------

        try {

            List<MedicalRecord> records =
                    medicalRecordDAO
                            .getPatientMedicalRecords(
                                    patientUid
                            );

            if (records != null) {

                medicalRecords.addAll(
                        records
                );
            }

        } catch (Exception e) {

            System.err.println(
                    "Unable to load medical records: "
                            + e.getMessage()
            );
        }

        // --------------------------------------------------------
        // APPOINTMENTS
        // --------------------------------------------------------

        try {

            List<Appointment> patientAppointments =
                    appointmentDAO
                            .getPatientAppointments(
                                    patientUid
                            );

            if (patientAppointments != null) {

                appointments.addAll(
                        patientAppointments
                );
            }

        } catch (Exception e) {

            System.err.println(
                    "Unable to load appointments: "
                            + e.getMessage()
            );
        }

        // --------------------------------------------------------
        // SORT MEDICAL RECORDS
        // --------------------------------------------------------

        medicalRecords.sort(
                Comparator.comparing(
                        this::getRecordDateValue,
                        Comparator.nullsLast(
                                Comparator.reverseOrder()
                        )
                )
        );

        // --------------------------------------------------------
        // SORT APPOINTMENTS
        // --------------------------------------------------------

        appointments.sort(
                Comparator.comparing(
                        this::getAppointmentDateValue,
                        Comparator.nullsLast(
                                Comparator.reverseOrder()
                        )
                )
        );
    }

    // ============================================================
    // CREATE SCENE
    // ============================================================

    private Scene createScene() {

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color: #F5F7FB;"
        );

        // ========================================================
        // SIDEBAR
        // ========================================================

        VBox sidebar =
                createSidebar();

        root.setLeft(sidebar);

        // ========================================================
        // MAIN CONTENT
        // ========================================================

        VBox content =
                new VBox(20);

        content.setPadding(
                new Insets(
                        24,
                        32,
                        32,
                        32
                )
        );

        // Header
        content.getChildren().add(
                createTopHeader()
        );

        // Page heading
        content.getChildren().add(
                createPageHeader()
        );

        // Search
        content.getChildren().add(
                createSearchSection()
        );

        // Patient selector
        content.getChildren().add(
                createPatientSelectorSection()
        );

        // Patient profile header
        content.getChildren().add(
                createPatientHeader()
        );

        // Information sections
        content.getChildren().add(
                createPatientInformationSection()
        );

        content.getChildren().add(
                createMedicalOverviewSection()
        );

        content.getChildren().add(
                createMedicalRecordsSection()
        );

        content.getChildren().add(
                createAppointmentsSection()
        );

        // ========================================================
        // SCROLL PANE
        // ========================================================

        ScrollPane scrollPane =
                new ScrollPane(
                        content
                );

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-background: transparent;"
                        + "-fx-border-color: transparent;"
        );

        root.setCenter(
                scrollPane
        );

        // ========================================================
        // COMMON SCENE SIZE
        // ========================================================

        double width =
                stage.getWidth() > 0
                        ? stage.getWidth()
                        : 1400;

        double height =
                stage.getHeight() > 0
                        ? stage.getHeight()
                        : 850;

        Scene patientScene =
                new Scene(
                        root,
                        width,
                        height
                );

        // ========================================================
        // CSS
        // ========================================================

        try {

            String css =
                    Objects.requireNonNull(
                            getClass()
                                    .getResource(
                                            "/css/patient_details.css"
                                    )
                    ).toExternalForm();

            patientScene
                    .getStylesheets()
                    .add(css);

        } catch (Exception e) {

            System.err.println(
                    "patient_details.css could not be loaded: "
                            + e.getMessage()
            );
        }

        return patientScene;
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

        // --------------------------------------------------------
        // Search
        // --------------------------------------------------------

        HBox searchContainer =
                new HBox(8);

        searchContainer.setAlignment(
                Pos.CENTER_LEFT
        );

        searchContainer
                .getStyleClass()
                .add(
                        "search-input-box"
                );

        searchContainer.setPrefWidth(
                300
        );

        ImageView searchIcon =
                createImageView(
                        "/images/icons/ic_search.png",
                        16,
                        16
                );

        searchField =
                new TextField();

        searchField.setPromptText(
                "Search patients by name or ID..."
        );

        searchField
                .getStyleClass()
                .add(
                        "search-text-field"
                );

        HBox.setHgrow(
                searchField,
                Priority.ALWAYS
        );

        searchField.textProperty()
                .addListener(
                        (obs, oldValue, newValue) ->
                                filterPatients(
                                        newValue
                                )
                );

        if (searchIcon != null) {

            searchContainer
                    .getChildren()
                    .add(searchIcon);
        }

        searchContainer
                .getChildren()
                .add(searchField);

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        // --------------------------------------------------------
        // Doctor information
        // --------------------------------------------------------

        VBox doctorInfo =
                new VBox(2);

        doctorInfo.setAlignment(
                Pos.CENTER_RIGHT
        );

        doctorNameLabel =
                new Label(
                        getLoggedInDoctorDisplayName()
                );

        doctorNameLabel
                .getStyleClass()
                .add(
                        "profile-name"
                );

        doctorSpecializationLabel =
                new Label(
                        "Doctor"
                );

        doctorSpecializationLabel
                .getStyleClass()
                .add(
                        "profile-dept"
                );

        doctorInfo
                .getChildren()
                .addAll(
                        doctorNameLabel,
                        doctorSpecializationLabel
                );

        StackPane notificationBox =
                new StackPane();

        ImageView bellIcon =
                createImageView(
                        "/images/icons/ic_bell.png",
                        18,
                        18
                );

        if (bellIcon != null) {

            notificationBox
                    .getChildren()
                    .add(
                            bellIcon
                    );
        }

        Circle notificationBadge =
                new Circle(
                        4,
                        Color.web(
                                "#EF4444"
                        )
                );

        StackPane.setAlignment(
                notificationBadge,
                Pos.TOP_RIGHT
        );

        notificationBox
                .getChildren()
                .add(
                        notificationBadge
                );

        HBox doctorProfile =
                new HBox(10);

        doctorProfile.setAlignment(
                Pos.CENTER_RIGHT
        );

        ImageView doctorAvatar =
                createImageView(
                        "/images/doctor/portrait-3d-male-doctor.png",
                        36,
                        36
                );

        if (doctorAvatar != null) {

            Circle clip =
                    new Circle(
                            18,
                            18,
                            18
                    );

            doctorAvatar.setClip(
                    clip
            );

            doctorProfile
                    .getChildren()
                    .add(
                            doctorAvatar
                    );
        }

        doctorProfile
                .getChildren()
                .add(
                        doctorInfo
                );

        doctorProfile.setStyle(
                "-fx-cursor: hand;"
        );

        doctorProfile.setOnMouseClicked(
                e -> Navigation.goTo(
                        stage,
                        () ->
                                new DoctorProfileView(
                                        stage
                                ).getScene()
                )
        );

        topBar
                .getChildren()
                .addAll(
                        searchContainer,
                        spacer,
                        notificationBox,
                        doctorProfile
                );

        return topBar;
    }

    // ============================================================
    // PAGE HEADER
    // ============================================================

    private HBox createPageHeader() {

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox titleBox =
                new VBox(4);

        Label title =
                new Label(
                        "Patient Details"
                );

        title.setStyle(
                "-fx-font-size: 28px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #0F172A;"
        );

        Label subtitle =
                new Label(
                        "Patient profiles, clinical history, vitals and appointments"
                );

        subtitle.setStyle(
                "-fx-font-size: 14px;"
                        + "-fx-text-fill: #64748B;"
        );

        titleBox
                .getChildren()
                .addAll(
                        title,
                        subtitle
                );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label doctorStatus =
                new Label(
                        doctorUid == null
                                ? "Not signed in"
                                : "Doctor workspace"
                );

        doctorStatus.setStyle(
                "-fx-font-size: 12px;"
                        + "-fx-text-fill: #64748B;"
        );

        header
                .getChildren()
                .addAll(
                        titleBox,
                        spacer,
                        doctorStatus
                );

        return header;
    }

    // ============================================================
    // SEARCH SECTION
    // ============================================================

    private VBox createSearchSection() {

        VBox card =
                createCard();

        HBox row =
                new HBox(12);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        Label icon =
                new Label(
                        "⌕"
                );

        icon.setStyle(
                "-fx-font-size: 22px;"
                        + "-fx-text-fill: #2563EB;"
        );

        matchingCountLabel =
                new Label();

        matchingCountLabel.setStyle(
                "-fx-font-size: 13px;"
                        + "-fx-text-fill: #64748B;"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button clearButton =
                new Button(
                        "Clear"
                );

        clearButton.setStyle(
                "-fx-background-color: white;"
                        + "-fx-border-color: #CBD5E1;"
                        + "-fx-border-radius: 7;"
                        + "-fx-background-radius: 7;"
                        + "-fx-padding: 8 16;"
                        + "-fx-cursor: hand;"
        );

        clearButton.setOnAction(
                e -> {

                    searchField.clear();

                    if (!doctorPatients.isEmpty()) {

                        selectedPatient =
                                doctorPatients.get(0);

                        loadSelectedPatientData();

                        refreshPatientUI();
                    }
                }
        );

        row
                .getChildren()
                .addAll(
                        icon,
                        matchingCountLabel,
                        spacer,
                        clearButton
                );

        card.getChildren()
                .add(row);

        updateMatchingCount(
                doctorPatients.size()
        );

        return card;
    }

    // ============================================================
    // PATIENT SELECTOR
    // ============================================================

    private VBox createPatientSelectorSection() {

        VBox card =
                createCard();

        Label title =
                new Label(
                        "Select Patient"
                );

        title.setStyle(
                "-fx-font-size: 15px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #334155;"
        );

        patientSelector =
                new ComboBox<>();

        patientSelector
                .setMaxWidth(
                        Double.MAX_VALUE
                );

        patientSelector
                .getItems()
                .addAll(
                        doctorPatients
                );

        patientSelector
                .setPromptText(
                        doctorPatients.isEmpty()
                                ? "No patients found"
                                : "Choose a patient"
                );

        patientSelector.setCellFactory(
                list -> new ListCell<>() {

                    @Override
                    protected void updateItem(
                            PatientProfile item,
                            boolean empty
                    ) {

                        super.updateItem(
                                item,
                                empty
                        );

                        if (empty
                                || item == null) {

                            setText(null);

                        } else {

                            setText(
                                    getPatientDisplayName(
                                            item
                                    )
                            );
                        }
                    }
                }
        );

        patientSelector.setButtonCell(
                new ListCell<>() {

                    @Override
                    protected void updateItem(
                            PatientProfile item,
                            boolean empty
                    ) {

                        super.updateItem(
                                item,
                                empty
                        );

                        if (empty
                                || item == null) {

                            setText(
                                    "Choose a patient"
                            );

                        } else {

                            setText(
                                    getPatientDisplayName(
                                            item
                                    )
                            );
                        }
                    }
                }
        );

        patientSelector.setOnAction(
                e -> {

                    PatientProfile patient =
                            patientSelector
                                    .getValue();

                    if (patient == null) {

                        return;
                    }

                    selectedPatient =
                            patient;

                    loadSelectedPatientData();

                    refreshPatientUI();
                }
        );

        if (selectedPatient != null) {

            patientSelector
                    .setValue(
                            selectedPatient
                    );
        }

        card.getChildren()
                .addAll(
                        title,
                        patientSelector
                );

        return card;
    }

    // ============================================================
    // PATIENT HEADER
    // ============================================================

    private VBox createPatientHeader() {

        VBox card =
                createCard();

        HBox row =
                new HBox(18);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        StackPane avatar =
                createPatientAvatar(
                        selectedPatient
                );

        VBox details =
                new VBox(5);

        patientNameLabel =
                new Label(
                        getPatientDisplayName(
                                selectedPatient
                        )
                );

        patientNameLabel.setStyle(
                "-fx-font-size: 24px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #0F172A;"
        );

        patientMetaLabel =
                new Label(
                        getPatientMeta(
                                selectedPatient
                        )
                );

        patientMetaLabel.setStyle(
                "-fx-font-size: 14px;"
                        + "-fx-text-fill: #64748B;"
        );

        patientIdLabel =
                new Label(
                        getPatientIdText(
                                selectedPatient
                        )
                );

        patientIdLabel.setStyle(
                "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #2563EB;"
        );

        details
                .getChildren()
                .addAll(
                        patientNameLabel,
                        patientMetaLabel,
                        patientIdLabel
                );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        VBox relationshipBox =
                new VBox(5);

        relationshipBox.setAlignment(
                Pos.CENTER_RIGHT
        );

        Label relationshipTitle =
                new Label(
                        "Doctor Relationship"
                );

        relationshipTitle.setStyle(
                "-fx-font-size: 12px;"
                        + "-fx-text-fill: #64748B;"
        );

        relationshipLabel =
                new Label(
                        selectedPatient == null
                                ? "No patient selected"
                                : "Active patient"
                );

        relationshipLabel.setStyle(
                "-fx-font-size: 14px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #059669;"
        );

        relationshipBox
                .getChildren()
                .addAll(
                        relationshipTitle,
                        relationshipLabel
                );

        row.getChildren()
                .addAll(
                        avatar,
                        details,
                        spacer,
                        relationshipBox
                );

        card.getChildren()
                .add(row);

        return card;
    }

    // ============================================================
    // PATIENT INFORMATION
    // ============================================================

    private VBox createPatientInformationSection() {

        VBox card =
                createCard();

        Label title =
                sectionTitle(
                        "Patient Information"
                );

        patientInformationContainer =
                new VBox(14);

        populatePatientInformation();

        card.getChildren()
                .addAll(
                        title,
                        patientInformationContainer
                );

        return card;
    }

    private void populatePatientInformation() {

        if (patientInformationContainer == null) {

            return;
        }

        patientInformationContainer
                .getChildren()
                .clear();

        if (selectedPatient == null) {

            patientInformationContainer
                    .getChildren()
                    .add(
                            emptyMessage(
                                    "Select a patient to view their profile."
                            )
                    );

            return;
        }

        GridPane grid =
                new GridPane();

        grid.setHgap(40);
        grid.setVgap(16);

        addInfoField(
                grid,
                "Email",
                safeText(
                        selectedPatient.getEmail(),
                        "Not available"
                ),
                0,
                0
        );

        addInfoField(
                grid,
                "Phone",
                safeText(
                        selectedPatient.getPhone(),
                        "Not available"
                ),
                1,
                0
        );

        addInfoField(
                grid,
                "Date of Birth",
                safeText(
                        selectedPatient.getDateOfBirth(),
                        "Not available"
                ),
                0,
                1
        );

        addInfoField(
                grid,
                "Gender",
                safeText(
                        selectedPatient.getGender(),
                        "Not available"
                ),
                1,
                1
        );

        addInfoField(
                grid,
                "Blood Group",
                safeText(
                        selectedPatient.getBloodGroup(),
                        "Not available"
                ),
                0,
                2
        );

        addInfoField(
                grid,
                "Emergency Contact",
                safeText(
                        selectedPatient.getEmergencyContact(),
                        "Not available"
                ),
                1,
                2
        );

        addInfoField(
                grid,
                "Address",
                safeText(
                        selectedPatient.getAddress(),
                        "Not available"
                ),
                0,
                3,
                2
        );

        ColumnConstraints c1 =
                new ColumnConstraints();

        c1.setPercentWidth(
                50
        );

        ColumnConstraints c2 =
                new ColumnConstraints();

        c2.setPercentWidth(
                50
        );

        grid.getColumnConstraints()
                .addAll(
                        c1,
                        c2
                );

        patientInformationContainer
                .getChildren()
                .add(grid);
    }

    // ============================================================
    // MEDICAL OVERVIEW
    // ============================================================

    private HBox createMedicalOverviewSection() {

        HBox layout =
                new HBox(18);

        layout.setFillHeight(
                true
        );

        // --------------------------------------------------------
        // VITALS
        // --------------------------------------------------------

        VBox vitalsCard =
                createCard();

        HBox.setHgrow(
                vitalsCard,
                Priority.ALWAYS
        );

        Label vitalsTitle =
                sectionTitle(
                        "Latest Vitals"
                );

        latestVitalsContainer =
                new VBox(10);

        populateLatestVitals();

        vitalsCard
                .getChildren()
                .addAll(
                        vitalsTitle,
                        latestVitalsContainer
                );

        // --------------------------------------------------------
        // LATEST CLINICAL SUMMARY
        // --------------------------------------------------------

        VBox summaryCard =
                createCard();

        HBox.setHgrow(
                summaryCard,
                Priority.ALWAYS
        );

        Label summaryTitle =
                sectionTitle(
                        "Latest Clinical Summary"
                );

        VBox summaryContent =
                new VBox(10);

        populateLatestClinicalSummary(
                summaryContent
        );

        summaryCard
                .getChildren()
                .addAll(
                        summaryTitle,
                        summaryContent
                );

        layout.getChildren()
                .addAll(
                        vitalsCard,
                        summaryCard
                );

        return layout;
    }

    // ============================================================
    // LATEST VITALS
    // ============================================================

    private void populateLatestVitals() {

        latestVitalsContainer
                .getChildren()
                .clear();

        if (medicalRecords.isEmpty()) {

            latestVitalsContainer
                    .getChildren()
                    .add(
                            emptyMessage(
                                    "No medical record with vitals is available."
                            )
                    );

            return;
        }

        MedicalRecord latest =
                medicalRecords.get(0);

        addVital(
                latestVitalsContainer,
                "Heart Rate",
                latest.getHeartRate()
        );

        addVital(
                latestVitalsContainer,
                "Blood Pressure",
                latest.getBloodPressure()
        );

        addVital(
                latestVitalsContainer,
                "Temperature",
                latest.getTemperature()
        );

        addVital(
                latestVitalsContainer,
                "SpO2",
                latest.getSpo2()
        );

        Label updated =
                new Label(
                        "Latest record: "
                                + getRecordDate(
                                latest
                        )
                );

        updated.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-text-fill: #94A3B8;"
        );

        latestVitalsContainer
                .getChildren()
                .add(
                        updated
                );
    }

    private void addVital(
            VBox container,
            String name,
            String value
    ) {

        HBox row =
                new HBox(8);

        Label bullet =
                new Label(
                        "•"
                );

        bullet.setStyle(
                "-fx-text-fill: #2563EB;"
                        + "-fx-font-weight: bold;"
        );

        Label text =
                new Label(
                        name
                                + ": "
                                + safeText(
                                value,
                                "Not available"
                        )
                );

        text.setStyle(
                "-fx-font-size: 13px;"
                        + "-fx-text-fill: #334155;"
        );

        row.getChildren()
                .addAll(
                        bullet,
                        text
                );

        container
                .getChildren()
                .add(
                        row
                );
    }

    // ============================================================
    // CLINICAL SUMMARY
    // ============================================================

    private void populateLatestClinicalSummary(
            VBox container
    ) {

        container
                .getChildren()
                .clear();

        if (medicalRecords.isEmpty()) {

            container
                    .getChildren()
                    .add(
                            emptyMessage(
                                    "No clinical records are currently available."
                            )
                    );

            return;
        }

        MedicalRecord record =
                medicalRecords.get(0);

        addSummaryField(
                container,
                "Symptoms",
                record.getSymptoms()
        );

        addSummaryField(
                container,
                "Diagnosis",
                record.getDiagnosis()
        );

        addSummaryField(
                container,
                "Clinical Notes",
                record.getClinicalNotes()
        );

        addSummaryField(
                container,
                "Prescription",
                record.getPrescription()
        );
    }

    private void addSummaryField(
            VBox container,
            String title,
            String value
    ) {

        VBox box =
                new VBox(3);

        Label titleLabel =
                new Label(
                        title
                );

        titleLabel.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #64748B;"
        );

        Label valueLabel =
                new Label(
                        safeText(
                                value,
                                "Not available"
                        )
                );

        valueLabel.setWrapText(
                true
        );

        valueLabel.setStyle(
                "-fx-font-size: 13px;"
                        + "-fx-text-fill: #334155;"
        );

        box.getChildren()
                .addAll(
                        titleLabel,
                        valueLabel
                );

        container
                .getChildren()
                .add(
                        box
                );
    }

    // ============================================================
    // MEDICAL RECORDS
    // ============================================================

    private VBox createMedicalRecordsSection() {

        VBox card =
                createCard();

        BorderPane header =
                new BorderPane();

        Label title =
                sectionTitle(
                        "Medical Records"
                );

        recordsCountLabel =
                new Label();

        recordsCountLabel.setStyle(
                "-fx-font-size: 12px;"
                        + "-fx-text-fill: #64748B;"
        );

        updateRecordsCount();

        header.setLeft(
                title
        );

        header.setRight(
                recordsCountLabel
        );

        medicalRecordsContainer =
                new VBox(12);

        populateMedicalRecords();

        card.getChildren()
                .addAll(
                        header,
                        medicalRecordsContainer
                );

        return card;
    }

    private void populateMedicalRecords() {

        if (medicalRecordsContainer == null) {

            return;
        }

        medicalRecordsContainer
                .getChildren()
                .clear();

        updateRecordsCount();

        if (medicalRecords.isEmpty()) {

            medicalRecordsContainer
                    .getChildren()
                    .add(
                            emptyMessage(
                                    "No medical history or clinical records are currently available for this patient."
                            )
                    );

            return;
        }

        for (MedicalRecord record :
                medicalRecords) {

            medicalRecordsContainer
                    .getChildren()
                    .add(
                            createMedicalRecordCard(
                                    record
                            )
                    );
        }
    }

    private VBox createMedicalRecordCard(
            MedicalRecord record
    ) {

        VBox card =
                new VBox(10);

        card.setPadding(
                new Insets(16)
        );

        card.setStyle(
                "-fx-background-color: #F8FAFC;"
                        + "-fx-background-radius: 10;"
                        + "-fx-border-color: #E2E8F0;"
                        + "-fx-border-radius: 10;"
        );

        BorderPane header =
                new BorderPane();

        VBox titleBox =
                new VBox(3);

        Label diagnosis =
                new Label(
                        safeText(
                                record.getDiagnosis(),
                                "Clinical consultation"
                        )
                );

        diagnosis.setStyle(
                "-fx-font-size: 15px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #0F172A;"
        );

        Label date =
                new Label(
                        getRecordDate(
                                record
                        )
                );

        date.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-text-fill: #64748B;"
        );

        titleBox
                .getChildren()
                .addAll(
                        diagnosis,
                        date
                );

        Label status =
                new Label(
                        safeText(
                                record.getStatus(),
                                "RECORDED"
                        ).toUpperCase()
                );

        status.setStyle(
                "-fx-background-color: #DBEAFE;"
                        + "-fx-text-fill: #1D4ED8;"
                        + "-fx-font-size: 10px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-padding: 5 9;"
                        + "-fx-background-radius: 12;"
        );

        header.setLeft(
                titleBox
        );

        header.setRight(
                status
        );

        VBox details =
                new VBox(8);

        addRecordDetail(
                details,
                "Symptoms",
                record.getSymptoms()
        );

        addRecordDetail(
                details,
                "Diagnosis",
                record.getDiagnosis()
        );

        addRecordDetail(
                details,
                "Blood Pressure",
                record.getBloodPressure()
        );

        addRecordDetail(
                details,
                "Heart Rate",
                record.getHeartRate()
        );

        addRecordDetail(
                details,
                "Temperature",
                record.getTemperature()
        );

        addRecordDetail(
                details,
                "SpO2",
                record.getSpo2()
        );

        addRecordDetail(
                details,
                "Clinical Notes",
                record.getClinicalNotes()
        );

        addRecordDetail(
                details,
                "Prescription",
                record.getPrescription()
        );

        addRecordDetail(
                details,
                "Doctor",
                record.getDoctorName()
        );

        addRecordDetail(
                details,
                "Appointment ID",
                record.getAppointmentId()
        );

        card.getChildren()
                .addAll(
                        header,
                        details
                );

        return card;
    }

    private void addRecordDetail(
            VBox container,
            String label,
            String value
    ) {

        if (value == null
                || value.trim().isEmpty()) {

            return;
        }

        VBox box =
                new VBox(2);

        Label title =
                new Label(
                        label
                );

        title.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #64748B;"
        );

        Label valueLabel =
                new Label(
                        value
                );

        valueLabel.setWrapText(
                true
        );

        valueLabel.setStyle(
                "-fx-font-size: 13px;"
                        + "-fx-text-fill: #334155;"
        );

        box.getChildren()
                .addAll(
                        title,
                        valueLabel
                );

        container
                .getChildren()
                .add(
                        box
                );
    }

    // ============================================================
    // APPOINTMENTS
    // ============================================================

    private VBox createAppointmentsSection() {

        VBox card =
                createCard();

        BorderPane header =
                new BorderPane();

        Label title =
                sectionTitle(
                        "Appointment History"
                );

        Label count =
                new Label(
                        appointments.size()
                                + " appointments"
                );

        count.setStyle(
                "-fx-font-size: 12px;"
                        + "-fx-text-fill: #64748B;"
        );

        header.setLeft(
                title
        );

        header.setRight(
                count
        );

        appointmentsContainer =
                new VBox(10);

        populateAppointments();

        card.getChildren()
                .addAll(
                        header,
                        appointmentsContainer
                );

        return card;
    }

    private void populateAppointments() {

        if (appointmentsContainer == null) {

            return;
        }

        appointmentsContainer
                .getChildren()
                .clear();

        if (appointments.isEmpty()) {

            appointmentsContainer
                    .getChildren()
                    .add(
                            emptyMessage(
                                    "No appointments are available for this patient."
                            )
                    );

            return;
        }

        for (Appointment appointment :
                appointments) {

            appointmentsContainer
                    .getChildren()
                    .add(
                            createAppointmentCard(
                                    appointment
                            )
                    );
        }
    }

    private HBox createAppointmentCard(
            Appointment appointment
    ) {

        HBox card =
                new HBox(16);

        card.setAlignment(
                Pos.CENTER_LEFT
        );

        card.setPadding(
                new Insets(14)
        );

        card.setStyle(
                "-fx-background-color: #F8FAFC;"
                        + "-fx-background-radius: 10;"
                        + "-fx-border-color: #E2E8F0;"
                        + "-fx-border-radius: 10;"
        );

        VBox dateBox =
                new VBox(3);

        Label date =
                new Label(
                        safeText(
                                appointment.getAppointmentDate(),
                                "Date unavailable"
                        )
                );

        date.setStyle(
                "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #2563EB;"
        );

        Label time =
                new Label(
                        safeText(
                                appointment.getAppointmentTime(),
                                "Time unavailable"
                        )
                );

        time.setStyle(
                "-fx-font-size: 12px;"
                        + "-fx-text-fill: #64748B;"
        );

        dateBox
                .getChildren()
                .addAll(
                        date,
                        time
                );

        VBox details =
                new VBox(4);

        Label reason =
                new Label(
                        safeText(
                                appointment.getReason(),
                                "Appointment"
                        )
                );

        reason.setStyle(
                "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #334155;"
        );

        Label type =
                new Label(
                        safeText(
                                appointment.getBookingType(),
                                "Appointment"
                        )
                );

        type.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-text-fill: #64748B;"
        );

        details
                .getChildren()
                .addAll(
                        reason,
                        type
                );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label status =
                new Label(
                        safeText(
                                appointment.getStatus(),
                                "PENDING"
                        ).toUpperCase()
                );

        status.setStyle(
                getAppointmentStatusStyle(
                        appointment.getStatus()
                )
        );

        card.getChildren()
                .addAll(
                        dateBox,
                        details,
                        spacer,
                        status
                );

        return card;
    }

    // ============================================================
    // SEARCH / FILTER
    // ============================================================

    private void filterPatients(
            String query
    ) {

        if (patientListContainer == null) {

            return;
        }

        String search =
                query == null
                        ? ""
                        : query.trim()
                        .toLowerCase();

        patientListContainer
                .getChildren()
                .clear();

        int matches = 0;

        for (PatientProfile patient :
                doctorPatients) {

            if (patient == null) {

                continue;
            }

            String name =
                    getPatientDisplayName(
                            patient
                    ).toLowerCase();

            String uid =
                    safeText(
                            patient.getUid(),
                            ""
                    ).toLowerCase();

            String email =
                    safeText(
                            patient.getEmail(),
                            ""
                    ).toLowerCase();

            if (search.isEmpty()
                    || name.contains(search)
                    || uid.contains(search)
                    || email.contains(search)) {

                patientListContainer
                        .getChildren()
                        .add(
                                createPatientSearchItem(
                                        patient
                                )
                        );

                matches++;
            }
        }

        updateMatchingCount(
                matches
        );
    }

    private HBox createPatientSearchItem(
            PatientProfile patient
    ) {

        HBox item =
                new HBox(10);

        item.setAlignment(
                Pos.CENTER_LEFT
        );

        item.setPadding(
                new Insets(9)
        );

        item.setStyle(
                "-fx-background-color: white;"
                        + "-fx-background-radius: 8;"
                        + "-fx-border-color: #E2E8F0;"
                        + "-fx-border-radius: 8;"
                        + "-fx-cursor: hand;"
        );

        StackPane avatar =
                createPatientAvatar(
                        patient
                );

        avatar.setScaleX(
                0.65
        );

        avatar.setScaleY(
                0.65
        );

        VBox details =
                new VBox(2);

        Label name =
                new Label(
                        getPatientDisplayName(
                                patient
                        )
                );

        name.setStyle(
                "-fx-font-weight: bold;"
                        + "-fx-text-fill: #334155;"
        );

        Label id =
                new Label(
                        safeText(
                                patient.getUid(),
                                "ID unavailable"
                        )
                );

        id.setStyle(
                "-fx-font-size: 10px;"
                        + "-fx-text-fill: #94A3B8;"
        );

        details
                .getChildren()
                .addAll(
                        name,
                        id
                );

        item.getChildren()
                .addAll(
                        avatar,
                        details
                );

        item.setOnMouseClicked(
                e -> {

                    selectedPatient =
                            patient;

                    if (patientSelector != null) {

                        patientSelector
                                .setValue(
                                        patient
                                );
                    }

                    loadSelectedPatientData();

                    refreshPatientUI();
                }
        );

        return item;
    }

    // ============================================================
    // REFRESH UI AFTER PATIENT SELECTION
    // ============================================================

    private void refreshPatientUI() {

        if (patientNameLabel != null) {

            patientNameLabel.setText(
                    getPatientDisplayName(
                            selectedPatient
                    )
            );
        }

        if (patientMetaLabel != null) {

            patientMetaLabel.setText(
                    getPatientMeta(
                            selectedPatient
                    )
            );
        }

        if (patientIdLabel != null) {

            patientIdLabel.setText(
                    getPatientIdText(
                            selectedPatient
                    )
            );
        }

        if (relationshipLabel != null) {

            relationshipLabel.setText(
                    selectedPatient == null
                            ? "No patient selected"
                            : "Active patient"
            );
        }

        populatePatientInformation();

        populateLatestVitals();

        populateLatestClinicalSummary(
                findClinicalSummaryContainer()
        );

        populateMedicalRecords();

        populateAppointments();

        updateDoctorHeader();
    }

    /**
     * Finds the summary container from the overview section.
     *
     * This is intentionally rebuilt during scene creation, so
     * there is no need to maintain another global UI reference.
     */
    private VBox findClinicalSummaryContainer() {

        VBox container =
                new VBox(10);

        if (!medicalRecords.isEmpty()) {

            populateLatestClinicalSummary(
                    container
            );

        } else {

            container
                    .getChildren()
                    .add(
                            emptyMessage(
                                    "No clinical records are currently available."
                            )
                    );
        }

        return container;
    }

    // ============================================================
    // UPDATE DOCTOR HEADER
    // ============================================================

    private void updateDoctorHeader() {

        if (doctorNameLabel != null) {

            doctorNameLabel.setText(
                    getLoggedInDoctorDisplayName()
            );
        }

        if (doctorSpecializationLabel != null) {

            doctorSpecializationLabel.setText(
                    getLoggedInDoctorSpecialization()
            );
        }
    }

    // ============================================================
    // LOGGED-IN DOCTOR DISPLAY
    // ============================================================

    private String getLoggedInDoctorDisplayName() {

        /*
         * Prefer the real doctor name stored in the latest
         * medical record associated with this doctor.
         */
        for (MedicalRecord record :
                medicalRecords) {

            if (record == null) {

                continue;
            }

            if (doctorUid != null
                    && doctorUid.equals(
                            record.getDoctorUid()
                    )) {

                String doctorName =
                        record.getDoctorName();

                if (doctorName != null
                        && !doctorName.trim().isEmpty()) {

                    return doctorName;
                }
            }
        }

        /*
         * Otherwise use the doctor name from appointments.
         */
        for (Appointment appointment :
                appointments) {

            if (appointment == null) {

                continue;
            }

            if (doctorUid != null
                    && doctorUid.equals(
                            appointment.getDoctorUid()
                    )) {

                String doctorName =
                        appointment.getDoctorName();

                if (doctorName != null
                        && !doctorName.trim().isEmpty()) {

                    return doctorName;
                }
            }
        }

        /*
         * Do not show a fake doctor name.
         */
        return "Doctor";
    }

    private String getLoggedInDoctorSpecialization() {

        /*
         * Specialization is not stored in MedicalRecord or
         * Appointment according to the existing model structure.
         *
         * Therefore do not invent one.
         */
        return "Doctor";
    }

    // ============================================================
    // SIDEBAR
    // ============================================================

    private VBox createSidebar() {
        return DoctorSidebar.create(stage, 4, selectedPatient != null ? selectedPatient.getUid() : null);
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
                                new DoctorSessionsView(
                                        stage
                                ).getScene()
                );

                break;

            case 4:

                Navigation.goTo(
                        stage,
                        () ->
                                new PatientDetailsView(
                                        stage
                                ).getScene()
                );

                break;

            case 5:

                if (selectedPatient != null
                        && selectedPatient.getUid() != null) {

                    Navigation.goTo(
                            stage,
                            () ->
                                    new MedicalReportsView(
                                            stage,
                                            selectedPatient.getUid()
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

            case 6:

                Navigation.goTo(
                        stage,
                        () ->
                                new AvailabilityScheduleView(
                                        stage
                                ).getScene()
                );

                break;

            case 7:

                Navigation.goTo(
                        stage,
                        () ->
                                new DoctorProfileView(
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

            System.out.println(
                    "Doctor logged out."
            );

        } catch (Exception e) {

            System.err.println(
                    "Logout error: "
                            + e.getMessage()
            );
        }

        Navigation.goTo(
                stage,
                () -> new LoginView(stage).getScene()
        );
    }

    // ============================================================
    // HELPERS - CARD
    // ============================================================

    private VBox createCard() {

        VBox card =
                new VBox(14);

        card.setPadding(
                new Insets(
                        20
                )
        );

        card.setStyle(
                "-fx-background-color: white;"
                        + "-fx-background-radius: 12;"
                        + "-fx-border-color: #E2E8F0;"
                        + "-fx-border-radius: 12;"
        );

        return card;
    }

    private Label sectionTitle(
            String text
    ) {

        Label label =
                new Label(
                        text
                );

        label.setStyle(
                "-fx-font-size: 17px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #0F172A;"
        );

        return label;
    }

    // ============================================================
    // INFO FIELD
    // ============================================================

    private void addInfoField(
            GridPane grid,
            String title,
            String value,
            int column,
            int row
    ) {

        addInfoField(
                grid,
                title,
                value,
                column,
                row,
                1
        );
    }

    private void addInfoField(
            GridPane grid,
            String title,
            String value,
            int column,
            int row,
            int colspan
    ) {

        VBox box =
                new VBox(4);

        Label titleLabel =
                new Label(
                        title
                );

        titleLabel.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #64748B;"
        );

        Label valueLabel =
                new Label(
                        value
                );

        valueLabel.setWrapText(
                true
        );

        valueLabel.setStyle(
                "-fx-font-size: 13px;"
                        + "-fx-text-fill: #334155;"
        );

        box.getChildren()
                .addAll(
                        titleLabel,
                        valueLabel
                );

        grid.add(
                box,
                column,
                row,
                colspan,
                1
        );
    }

    // ============================================================
    // PATIENT AVATAR
    // ============================================================

    private StackPane createPatientAvatar(
            PatientProfile patient
    ) {

        StackPane avatar =
                new StackPane();

        avatar.setPrefSize(
                70,
                70
        );

        avatar.setStyle(
                "-fx-background-color: #DBEAFE;"
                        + "-fx-background-radius: 50;"
        );

        String initials =
                getPatientInitials(
                        patient
                );

        Label initialsLabel =
                new Label(
                        initials
                );

        initialsLabel.setStyle(
                "-fx-font-size: 20px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #2563EB;"
        );

        avatar.getChildren()
                .add(
                        initialsLabel
                );

        return avatar;
    }

    private String getPatientInitials(
            PatientProfile patient
    ) {

        if (patient == null) {

            return "P";
        }

        String first =
                safeText(
                        patient.getFirstName(),
                        ""
                ).trim();

        String last =
                safeText(
                        patient.getLastName(),
                        ""
                ).trim();

        StringBuilder result =
                new StringBuilder();

        if (!first.isEmpty()) {

            result.append(
                    Character.toUpperCase(
                            first.charAt(0)
                    )
            );
        }

        if (!last.isEmpty()) {

            result.append(
                    Character.toUpperCase(
                            last.charAt(0)
                    )
            );
        }

        if (result.length() == 0) {

            return "P";
        }

        return result.toString();
    }

    // ============================================================
    // PATIENT DISPLAY
    // ============================================================

    private String getPatientDisplayName(
            PatientProfile patient
    ) {

        if (patient == null) {

            return "No Patient Selected";
        }

        String first =
                safeText(
                        patient.getFirstName(),
                        ""
                ).trim();

        String last =
                safeText(
                        patient.getLastName(),
                        ""
                ).trim();

        String fullName =
                (first + " " + last)
                        .trim();

        if (!fullName.isEmpty()) {

            return fullName;
        }

        return "Unnamed Patient";
    }

    private String getPatientMeta(
            PatientProfile patient
    ) {

        if (patient == null) {

            return "No patient selected";
        }

        String gender =
                safeText(
                        patient.getGender(),
                        "Gender unavailable"
                );

        String dob =
                safeText(
                        patient.getDateOfBirth(),
                        "DOB unavailable"
                );

        String bloodGroup =
                safeText(
                        patient.getBloodGroup(),
                        "Blood group unavailable"
                );

        String age =
                calculateAge(
                        patient.getDateOfBirth()
                );

        if (!age.isEmpty()) {

            return gender
                    + " • "
                    + age
                    + " • DOB: "
                    + dob
                    + " • Blood Group: "
                    + bloodGroup;
        }

        return gender
                + " • DOB: "
                + dob
                + " • Blood Group: "
                + bloodGroup;
    }

    private String getPatientIdText(
            PatientProfile patient
    ) {

        if (patient == null) {

            return "Patient ID: unavailable";
        }

        return "Patient ID: #"
                + safeText(
                patient.getUid(),
                "unavailable"
        );
    }

    // ============================================================
    // AGE
    // ============================================================

    private String calculateAge(
            String dateOfBirth
    ) {

        if (dateOfBirth == null
                || dateOfBirth.trim().isEmpty()) {

            return "";
        }

        try {

            LocalDate dob =
                    LocalDate.parse(
                            dateOfBirth
                    );

            LocalDate today =
                    LocalDate.now();

            int age =
                    Period.between(
                            dob,
                            today
                    ).getYears();

            return age + " yrs";

        } catch (Exception e) {

            return "";
        }
    }

    // ============================================================
    // MEDICAL RECORD DATE
    // ============================================================

    private String getRecordDate(
            MedicalRecord record
    ) {

        if (record == null) {

            return "Date unavailable";
        }

        String updated =
                record.getUpdatedAt();

        if (updated != null
                && !updated.trim().isEmpty()) {

            return formatTimestamp(
                    updated
            );
        }

        String created =
                record.getCreatedAt();

        if (created != null
                && !created.trim().isEmpty()) {

            return formatTimestamp(
                    created
            );
        }

        return "Date unavailable";
    }

    private String getRecordDateValue(
            MedicalRecord record
    ) {

        if (record == null) {

            return null;
        }

        if (record.getUpdatedAt() != null
                && !record.getUpdatedAt()
                .trim()
                .isEmpty()) {

            return record.getUpdatedAt();
        }

        return record.getCreatedAt();
    }

    // ============================================================
    // APPOINTMENT DATE
    // ============================================================

    private String getAppointmentDateValue(
            Appointment appointment
    ) {

        if (appointment == null) {

            return null;
        }

        return appointment.getAppointmentDate();
    }

    // ============================================================
    // TIMESTAMP FORMAT
    // ============================================================

    private String formatTimestamp(
            String value
    ) {

        if (value == null
                || value.trim().isEmpty()) {

            return "Date unavailable";
        }

        try {

            if (value.length() >= 10) {

                return value.substring(
                        0,
                        10
                );
            }

        } catch (Exception ignored) {

            // Fall through to original value.
        }

        return value;
    }

    // ============================================================
    // APPOINTMENT STATUS STYLE
    // ============================================================

    private String getAppointmentStatusStyle(
            String status
    ) {

        String normalized =
                safeText(
                        status,
                        "PENDING"
                ).toUpperCase();

        if (normalized.equals(
                "COMPLETED"
        )) {

            return
                    "-fx-background-color: #DCFCE7;"
                            + "-fx-text-fill: #15803D;"
                            + "-fx-font-size: 10px;"
                            + "-fx-font-weight: bold;"
                            + "-fx-padding: 5 9;"
                            + "-fx-background-radius: 12;";
        }

        if (normalized.equals(
                "CANCELLED"
        )
                || normalized.equals(
                "CANCELED"
        )
                || normalized.equals(
                "REJECTED"
        )) {

            return
                    "-fx-background-color: #FEE2E2;"
                            + "-fx-text-fill: #B91C1C;"
                            + "-fx-font-size: 10px;"
                            + "-fx-font-weight: bold;"
                            + "-fx-padding: 5 9;"
                            + "-fx-background-radius: 12;";
        }

        if (normalized.equals(
                "ACCEPTED"
        )
                || normalized.equals(
                "CONFIRMED"
        )) {

            return
                    "-fx-background-color: #DBEAFE;"
                            + "-fx-text-fill: #1D4ED8;"
                            + "-fx-font-size: 10px;"
                            + "-fx-font-weight: bold;"
                            + "-fx-padding: 5 9;"
                            + "-fx-background-radius: 12;";
        }

        return
                "-fx-background-color: #FEF3C7;"
                        + "-fx-text-fill: #92400E;"
                        + "-fx-font-size: 10px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-padding: 5 9;"
                        + "-fx-background-radius: 12;";
    }

    // ============================================================
    // RECORD COUNT
    // ============================================================

    private void updateRecordsCount() {

        if (recordsCountLabel == null) {

            return;
        }

        int count =
                medicalRecords == null
                        ? 0
                        : medicalRecords.size();

        recordsCountLabel.setText(
                count
                        + (count == 1
                        ? " record"
                        : " records")
        );
    }

    // ============================================================
    // SEARCH COUNT
    // ============================================================

    private void updateMatchingCount(
            int count
    ) {

        if (matchingCountLabel != null) {

            matchingCountLabel.setText(
                    count
                            + (count == 1
                            ? " matching patient"
                            : " matching patients")
            );
        }
    }

    // ============================================================
    // EMPTY MESSAGE
    // ============================================================

    private Label emptyMessage(
            String message
    ) {

        Label label =
                new Label(
                        message
                );

        label.setWrapText(
                true
        );

        label.setStyle(
                "-fx-font-size: 13px;"
                        + "-fx-text-fill: #64748B;"
        );

        return label;
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
    // IMAGE HELPER
    // ============================================================

    private ImageView createImageView(
            String path,
            double width,
            double height
    ) {

        try {

            ImageView image =
                    new ImageView(
                            ResourceImage.load(
                                    path
                            )
                    );

            image.setFitWidth(
                    width
            );

            image.setFitHeight(
                    height
            );

            image.setPreserveRatio(
                    true
            );

            return image;

        } catch (Exception e) {

            return null;
        }
    }

    // ============================================================
    // ALERT
    // ============================================================

    private void showAlert(
            Alert.AlertType type,
            String title,
            String message
    ) {

        Alert alert =
                new Alert(
                        type
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