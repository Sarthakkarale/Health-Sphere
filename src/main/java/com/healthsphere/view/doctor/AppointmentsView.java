package com.healthsphere.view.doctor;

import com.healthsphere.controller.appointment.AppointmentController;
import com.healthsphere.model.Appointment;
import com.healthsphere.util.Navigation;
import com.healthsphere.util.ResourceImage;
import com.healthsphere.util.SessionManager;
import com.healthsphere.util.ShimmerPlaceholder;
import com.healthsphere.view.authentication.LoginView;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Doctor Appointments Screen
 *
 * Backend flow:
 *
 * JavaFX View
 *      ↓
 * AppointmentController
 *      ↓
 * AppointmentDAO
 *      ↓
 * Firebase Firestore
 *
 * Appointment workflow:
 *
 * PENDING / CONFIRMED
 *      ↓
 * APPROVE → ACCEPTED
 *      ↓
 * COMPLETE → COMPLETED
 *
 * PENDING / CONFIRMED
 *      ↓
 * REJECT → REJECTED
 *
 * ACCEPTED
 *      ↓
 * CANCEL → CANCELLED
 */
public class AppointmentsView {

    // ============================================================
    // BASIC
    // ============================================================

    private final Stage stage;
    private final Scene scene;

    // ============================================================
    // BACKEND
    // ============================================================

    private final AppointmentController appointmentController;

    private final String doctorUid;

    // ============================================================
    // REAL FIREBASE DATA
    // ============================================================

    private final List<Appointment> appointmentList =
            new ArrayList<>();

    // ============================================================
    // UI REFERENCES
    // ============================================================

    private FlowPane cardsGrid;

    private Label totalAppointmentsLabel;

    private TextField searchField;

    private DatePicker datePicker;

    // ============================================================
    // FILTER STATE
    // ============================================================

    private String selectedFilter = "All";

    // ============================================================
    // DATE FORMAT
    // ============================================================

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public AppointmentsView(Stage stage) {
        this.stage = stage;
        this.appointmentController = new AppointmentController();
        this.doctorUid = getCurrentDoctorUid();

        // Create scene immediately so navigation is instant
        this.scene = createScene();

        // Load appointments asynchronously
        loadAppointmentsFromFirebaseAsync();
    }

    public Scene getScene() {
        return scene;
    }

    private String getCurrentDoctorUid() {
        if (SessionManager.getInstance().getCurrentUser() == null) {
            throw new IllegalStateException("No logged-in user was found.");
        }
        String uid = SessionManager.getInstance().getCurrentUser().getUid();
        if (uid == null || uid.trim().isEmpty()) {
            throw new IllegalStateException("Doctor UID is not available in the current session.");
        }
        return uid;
    }

    private void loadAppointmentsFromFirebaseAsync() {
        // Show initial shimmer skeleton cards in cardsGrid
        if (cardsGrid != null) {
            cardsGrid.getChildren().clear();
            for (int i = 0; i < 4; i++) {
                cardsGrid.getChildren().add(ShimmerPlaceholder.createAppointmentCardShimmer());
            }
        }

        Task<List<Appointment>> loadTask = new Task<>() {
            @Override
            protected List<Appointment> call() throws Exception {
                List<Appointment> appointments = appointmentController.getDoctorAppointments(doctorUid);
                return appointments != null ? appointments : new ArrayList<>();
            }
        };

        loadTask.setOnSucceeded(event -> {
            appointmentList.clear();
            appointmentList.addAll(loadTask.getValue());
            appointmentList.sort(Comparator.comparing(this::getSortableDateTime));
            System.out.println("REAL APPOINTMENTS LOADED ASYNC: " + appointmentList.size());
            renderAppointments();
        });

        loadTask.setOnFailed(event -> {
            appointmentList.clear();
            Throwable ex = loadTask.getException();
            if (ex != null) ex.printStackTrace();
            showErrorAlert("Appointments Error", ex != null ? getRootErrorMessage(ex) : "Unable to load appointments.");
            renderAppointments();
        });

        Thread thread = new Thread(loadTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadAppointmentsFromFirebase() {
        loadAppointmentsFromFirebaseAsync();
    }

    // ============================================================
    // SORT DATE/TIME
    // ============================================================

    private String getSortableDateTime(
            Appointment appointment) {

        if (appointment == null) {

            return "9999-99-99-99-99";
        }

        String date =
                safe(
                        appointment.getAppointmentDate(),
                        "9999-99-99"
                );

        String time =
                safe(
                        appointment.getAppointmentTime(),
                        "99:99"
                );

        return date + " " + time;
    }

    // ============================================================
    // CREATE SCENE
    // ============================================================

    private Scene createScene() {

        BorderPane root =
                new BorderPane();

        root.getStyleClass()
                .add("root-pane");

        // --------------------------------------------------------
        // SIDEBAR
        // --------------------------------------------------------

        root.setLeft(
                createSidebar()
        );

        // --------------------------------------------------------
        // CONTENT
        // --------------------------------------------------------

        VBox content =
                new VBox(20);

        content.setPadding(
                new Insets(
                        20,
                        30,
                        30,
                        30
                )
        );

        content.getStyleClass()
                .add("content-area");

        // Top bar
        content.getChildren()
                .add(
                        createTopHeader()
                );

        // Title
        content.getChildren()
                .add(
                        createTitleSection()
                );

        // Filter section
        content.getChildren()
                .add(
                        createFilterBar()
                );

        // Cards
        cardsGrid =
                new FlowPane(
                        20,
                        20
                );

        cardsGrid.setAlignment(
                Pos.TOP_LEFT
        );

        cardsGrid.setPadding(
                new Insets(5)
        );

        ScrollPane cardsScroll =
                new ScrollPane(
                        cardsGrid
                );

        cardsScroll.setFitToWidth(
                true
        );

        cardsScroll.setPannable(
                true
        );

        cardsScroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        cardsScroll.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        cardsScroll.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-background: transparent;"
        );

        VBox.setVgrow(
                cardsScroll,
                Priority.ALWAYS
        );

        content.getChildren()
                .add(
                        cardsScroll
                );

        // --------------------------------------------------------
        // OUTER SCROLL
        // --------------------------------------------------------

        ScrollPane outerScroll =
                new ScrollPane(
                        content
                );

        outerScroll.setFitToWidth(
                true
        );

        outerScroll.setFitToHeight(
                true
        );

        outerScroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        outerScroll.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-background: transparent;"
        );

        root.setCenter(
                outerScroll
        );

        // --------------------------------------------------------
        // SCENE
        // --------------------------------------------------------

        Scene result =
                new Scene(
                        root,
                        stage.getWidth(),
                        stage.getHeight()
                );

        try {

            result.getStylesheets()
                    .add(
                            Objects.requireNonNull(
                                    getClass()
                                            .getResource(
                                                    "/css/appointments.css"
                                            )
                            ).toExternalForm()
                    );

        } catch (Exception e) {

            System.out.println(
                    "appointments.css could not be loaded."
            );
        }

        // Initial display
        renderAppointments();

        return result;
    }

    // ============================================================
    // SIDEBAR
    // ============================================================

    private VBox createSidebar() {
        return DoctorSidebar.create(stage, 2);
    }

    // ============================================================
    // SIDEBAR NAVIGATION
    // ============================================================

    private void handleSidebarNavigation(
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

                Navigation.goTo(
                        stage,
                        () ->
                                new MedicalReportsView(
                                        stage
                                ).getScene()
                );

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

        } catch (Exception e) {

            e.printStackTrace();
        }

        Navigation.goTo(
                stage,
                () -> new LoginView(stage).getScene()
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

        // --------------------------------------------------------
        // SEARCH BOX
        // --------------------------------------------------------

        HBox searchContainer =
                new HBox(10);

        searchContainer.setAlignment(
                Pos.CENTER_LEFT
        );

        searchContainer.setPrefWidth(
                300
        );

        searchContainer.setStyle(
                "-fx-background-color: white;"
                        + "-fx-background-radius: 24px;"
                        + "-fx-border-color: #E2E8F0;"
                        + "-fx-border-radius: 24px;"
                        + "-fx-padding: 5px 14px;"
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
                "Search appointments..."
        );

        searchField.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-border-color: transparent;"
                        + "-fx-padding: 5px;"
                        + "-fx-font-size: 13px;"
        );

        searchField
                .textProperty()
                .addListener(
                        (observable,
                         oldValue,
                         newValue) ->
                                renderAppointments()
                );

        HBox.setHgrow(
                searchField,
                Priority.ALWAYS
        );

        if (searchIcon != null) {

            searchContainer
                    .getChildren()
                    .add(
                            searchIcon
                    );
        }

        searchContainer
                .getChildren()
                .add(
                        searchField
                );

        // --------------------------------------------------------
        // SPACER
        // --------------------------------------------------------

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        // --------------------------------------------------------
        // NOTIFICATION
        // --------------------------------------------------------

        StackPane notification =
                new StackPane();

        ImageView bell =
                createImageView(
                        "/images/icons/ic_bell.png",
                        20,
                        20
                );

        Circle notificationDot =
                new Circle(
                        4,
                        Color.web(
                                "#EF4444"
                        )
                );

        StackPane.setAlignment(
                notificationDot,
                Pos.TOP_RIGHT
        );

        if (bell != null) {

            notification
                    .getChildren()
                    .add(
                            bell
                    );
        }

        notification
                .getChildren()
                .add(
                        notificationDot
                );

        // --------------------------------------------------------
        // AVATAR
        // --------------------------------------------------------

        ImageView avatar =
                createImageView(
                        "/images/mocks/dr_sarah_avatar.png",
                        36,
                        36
                );

        if (avatar != null) {

            Circle clip =
                    new Circle(
                            18,
                            18,
                            18
                    );

            avatar.setClip(
                    clip
            );

            avatar.setStyle(
                    "-fx-cursor: hand;"
            );

            avatar.setOnMouseClicked(
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

        topBar
                .getChildren()
                .addAll(
                        searchContainer,
                        spacer,
                        notification
                );

        if (avatar != null) {

            topBar
                    .getChildren()
                    .add(
                            avatar
                    );
        }

        return topBar;
    }

    // ============================================================
    // TITLE SECTION
    // ============================================================

    private BorderPane createTitleSection() {

        BorderPane section =
                new BorderPane();

        VBox titleBox =
                new VBox(4);

        Label title =
                new Label(
                        "Appointments"
                );

        title.getStyleClass()
                .add(
                        "page-title"
                );

        totalAppointmentsLabel =
                new Label(
                        appointmentList.size()
                                + " Total Appointments"
                );

        totalAppointmentsLabel
                .getStyleClass()
                .add(
                        "page-subtitle"
                );

        titleBox
                .getChildren()
                .addAll(
                        title,
                        totalAppointmentsLabel
                );

        // --------------------------------------------------------
        // NEW APPOINTMENT
        // --------------------------------------------------------

        Button newAppointment =
                new Button(
                        "+ New Appointment"
                );

        newAppointment.setStyle(
                "-fx-background-color: #0B57D0;"
                        + "-fx-text-fill: white;"
                        + "-fx-font-weight: bold;"
                        + "-fx-background-radius: 6px;"
                        + "-fx-padding: 10px 18px;"
                        + "-fx-cursor: hand;"
        );

        newAppointment.setOnAction(
                e ->
                        Navigation.goTo(
                                stage,
                                () ->
                                        new NewAppointmentView(
                                                stage
                                        ).getScene()
                        )
        );

        section.setLeft(
                titleBox
        );

        section.setRight(
                newAppointment
        );

        return section;
    }

    // ============================================================
    // FILTER BAR
    // ============================================================

    private HBox createFilterBar() {

        HBox bar =
                new HBox(12);

        bar.setAlignment(
                Pos.CENTER_LEFT
        );

        bar.setPadding(
                new Insets(
                        12,
                        16,
                        12,
                        16
                )
        );

        bar.setStyle(
                "-fx-background-color: white;"
                        + "-fx-background-radius: 10px;"
                        + "-fx-border-color: #E2E8F0;"
                        + "-fx-border-radius: 10px;"
        );

        // --------------------------------------------------------
        // FILTER BUTTONS
        // --------------------------------------------------------

        HBox filterButtons =
                new HBox(8);

        String[] filters = {
                "All",
                "Upcoming",
                "Completed",
                "Cancelled"
        };

        for (String filter :
                filters) {

            Button button =
                    new Button(
                            filter
                    );

            button.setMinWidth(
                    90
            );

            updateFilterButtonStyle(
                    button,
                    filter.equals(
                            selectedFilter
                    )
            );

            button.setOnAction(
                    e -> {

                        selectedFilter =
                                filter;

                        /*
                         * Refresh all buttons.
                         */
                        for (Node node :
                                filterButtons
                                        .getChildren()) {

                            if (node instanceof Button) {

                                Button current =
                                        (Button) node;

                                updateFilterButtonStyle(
                                        current,
                                        current.getText()
                                                .equals(
                                                        selectedFilter
                                                )
                                );
                            }
                        }

                        renderAppointments();
                    }
            );

            filterButtons
                    .getChildren()
                    .add(
                            button
                    );
        }

        // --------------------------------------------------------
        // SPACER
        // --------------------------------------------------------

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        // --------------------------------------------------------
        // DATE
        // --------------------------------------------------------

        datePicker =
                new DatePicker();

        /*
         * IMPORTANT:
         *
         * Do NOT initialize this with 10/26/2023.
         *
         * Empty means "show all real appointments".
         */
        datePicker.setPromptText(
                "Select date"
        );

        datePicker.setPrefWidth(
                160
        );

        datePicker
                .valueProperty()
                .addListener(
                        (observable,
                         oldValue,
                         newValue) ->
                                renderAppointments()
                );

        // --------------------------------------------------------
        // CLEAR DATE
        // --------------------------------------------------------

        Button clearDate =
                new Button(
                        "Clear"
                );

        clearDate.setStyle(
                "-fx-background-color: white;"
                        + "-fx-text-fill: #475569;"
                        + "-fx-border-color: #CBD5E1;"
                        + "-fx-border-radius: 6px;"
                        + "-fx-background-radius: 6px;"
                        + "-fx-padding: 7px 12px;"
                        + "-fx-cursor: hand;"
        );

        clearDate.setOnAction(
                e -> {

                    datePicker.setValue(
                            null
                    );

                    renderAppointments();
                }
        );

        bar
                .getChildren()
                .addAll(
                        filterButtons,
                        spacer,
                        datePicker,
                        clearDate
                );

        return bar;
    }

    // ============================================================
    // FILTER BUTTON STYLE
    // ============================================================

    private void updateFilterButtonStyle(
            Button button,
            boolean active) {

        if (active) {

            button.setStyle(
                    "-fx-background-color: #0B57D0;"
                            + "-fx-text-fill: white;"
                            + "-fx-font-weight: bold;"
                            + "-fx-background-radius: 20px;"
                            + "-fx-padding: 8px 16px;"
                            + "-fx-cursor: hand;"
            );

        } else {

            button.setStyle(
                    "-fx-background-color: white;"
                            + "-fx-text-fill: #475569;"
                            + "-fx-border-color: #CBD5E1;"
                            + "-fx-border-radius: 20px;"
                            + "-fx-background-radius: 20px;"
                            + "-fx-padding: 8px 16px;"
                            + "-fx-cursor: hand;"
            );
        }
    }

    // ============================================================
    // RENDER APPOINTMENTS
    // ============================================================

    private void renderAppointments() {

        if (cardsGrid == null) {

            return;
        }

        cardsGrid
                .getChildren()
                .clear();

        // --------------------------------------------------------
        // REAL TOTAL
        // --------------------------------------------------------

        if (totalAppointmentsLabel != null) {

            totalAppointmentsLabel.setText(
                    appointmentList.size()
                            + " Total Appointments"
            );
        }

        String search =
                searchField == null
                        ? ""
                        : safe(
                                searchField.getText(),
                                ""
                        )
                                .toLowerCase(
                                        Locale.ROOT
                                )
                                .trim();

        LocalDate selectedDate =
                datePicker == null
                        ? null
                        : datePicker.getValue();

        int displayed =
                0;

        // --------------------------------------------------------
        // LOOP THROUGH REAL FIREBASE DATA
        // --------------------------------------------------------

        for (Appointment appointment :
                appointmentList) {

            if (appointment == null) {

                continue;
            }

            // ----------------------------------------------------
            // STATUS FILTER
            // ----------------------------------------------------

            if (!matchesFilter(
                    appointment
            )) {

                continue;
            }

            // ----------------------------------------------------
            // SEARCH
            // ----------------------------------------------------

            if (!matchesSearch(
                    appointment,
                    search
            )) {

                continue;
            }

            // ----------------------------------------------------
            // DATE
            // ----------------------------------------------------

            if (!matchesDate(
                    appointment,
                    selectedDate
            )) {

                continue;
            }

            // ----------------------------------------------------
            // CREATE CARD
            // ----------------------------------------------------

            cardsGrid
                    .getChildren()
                    .add(
                            createAppointmentCard(
                                    appointment
                            )
                    );

            displayed++;
        }

        // --------------------------------------------------------
        // EMPTY
        // --------------------------------------------------------

        if (displayed == 0) {

            VBox emptyBox =
                    new VBox(10);

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
                            "No appointments found"
                    );

            emptyTitle.setStyle(
                    "-fx-font-size: 16px;"
                            + "-fx-font-weight: bold;"
                            + "-fx-text-fill: #334155;"
            );

            Label emptyText =
                    new Label(
                            getEmptyMessage()
                    );

            emptyText.setStyle(
                    "-fx-font-size: 13px;"
                            + "-fx-text-fill: #64748B;"
            );

            emptyBox
                    .getChildren()
                    .addAll(
                            emptyTitle,
                            emptyText
                    );

            cardsGrid
                    .getChildren()
                    .add(
                            emptyBox
                    );
        }
    }

    // ============================================================
    // FILTER MATCH
    // ============================================================

    private boolean matchesFilter(
            Appointment appointment) {

        if ("All".equalsIgnoreCase(
                selectedFilter
        )) {

            return true;
        }

        String status =
                normalizeStatus(
                        appointment.getStatus()
                );

        // --------------------------------------------------------
        // UPCOMING
        // --------------------------------------------------------

        if ("Upcoming".equalsIgnoreCase(
                selectedFilter
        )) {

            return status.equals(
                    "PENDING"
            )
                    || status.equals(
                    "CONFIRMED"
            )
                    || status.equals(
                    "ACCEPTED"
            );
        }

        // --------------------------------------------------------
        // COMPLETED
        // --------------------------------------------------------

        if ("Completed".equalsIgnoreCase(
                selectedFilter
        )) {

            return status.equals(
                    "COMPLETED"
            );
        }

        // --------------------------------------------------------
        // CANCELLED
        // --------------------------------------------------------

        if ("Cancelled".equalsIgnoreCase(
                selectedFilter
        )) {

            return status.equals(
                    "CANCELLED"
            )
                    || status.equals(
                    "REJECTED"
            );
        }

        return true;
    }

    // ============================================================
    // SEARCH
    // ============================================================

    private boolean matchesSearch(
            Appointment appointment,
            String search) {

        if (search == null
                || search.isEmpty()) {

            return true;
        }

        String patient =
                safe(
                        appointment.getPatientName(),
                        ""
                );

        String patientUid =
                safe(
                        appointment.getPatientUid(),
                        ""
                );

        String appointmentId =
                safe(
                        appointment.getAppointmentId(),
                        ""
                );

        String reason =
                safe(
                        appointment.getReason(),
                        ""
                );

        String doctor =
                safe(
                        appointment.getDoctorName(),
                        ""
                );

        String bookingType =
                safe(
                        appointment.getBookingType(),
                        ""
                );

        String combined =
                (
                        patient
                                + " "
                                + patientUid
                                + " "
                                + appointmentId
                                + " "
                                + reason
                                + " "
                                + doctor
                                + " "
                                + bookingType
                )
                        .toLowerCase(
                                Locale.ROOT
                        );

        return combined.contains(
                search
        );
    }

    // ============================================================
    // DATE MATCH
    // ============================================================

    private boolean matchesDate(
            Appointment appointment,
            LocalDate selectedDate) {

        if (selectedDate == null) {

            return true;
        }

        LocalDate appointmentDate =
                parseDate(
                        appointment
                                .getAppointmentDate()
                );

        return appointmentDate != null
                && appointmentDate.equals(
                selectedDate
        );
    }

    // ============================================================
    // EMPTY MESSAGE
    // ============================================================

    private String getEmptyMessage() {

        if (!safe(
                searchField == null
                        ? null
                        : searchField.getText(),
                ""
        ).trim().isEmpty()) {

            return "No appointment matches your search.";
        }

        if (datePicker != null
                && datePicker.getValue() != null) {

            return "No appointment exists on the selected date.";
        }

        if (!"All".equalsIgnoreCase(
                selectedFilter
        )) {

            return "No appointments exist in the "
                    + selectedFilter
                    + " category.";
        }

        return "No appointments are currently assigned to this doctor.";
    }

    // ============================================================
    // CREATE APPOINTMENT CARD
    // ============================================================

    private VBox createAppointmentCard(
            Appointment appointment) {

        VBox card =
                new VBox(14);

        card.setMinWidth(
                320
        );

        card.setPrefWidth(
                340
        );

        card.setMaxWidth(
                360
        );

        card.setPadding(
                new Insets(
                        18
                )
        );

        card.setStyle(
                "-fx-background-color: white;"
                        + "-fx-background-radius: 10px;"
                        + "-fx-border-color: #E2E8F0;"
                        + "-fx-border-radius: 10px;"
        );

        // --------------------------------------------------------
        // TOP ROW
        // --------------------------------------------------------

        BorderPane topRow =
                new BorderPane();

        Label idLabel =
                new Label(
                        shortAppointmentId(
                                appointment
                                        .getAppointmentId()
                        )
                );

        idLabel.setStyle(
                "-fx-background-color: #F1F5F9;"
                        + "-fx-text-fill: #64748B;"
                        + "-fx-background-radius: 5px;"
                        + "-fx-padding: 5px 7px;"
                        + "-fx-font-size: 10px;"
        );

        String status =
                normalizeStatus(
                        appointment.getStatus()
                );

        Label statusLabel =
                new Label(
                        status
                );

        statusLabel.setStyle(
                getStatusStyle(
                        status
                )
        );

        boolean isPaid = "PAID".equalsIgnoreCase(appointment.getPaymentStatus());
        Label paymentBadge = new Label(isPaid ? "✓ PAID" : "⚡ PENDING");
        paymentBadge.setStyle(isPaid ?
                "-fx-background-color: #D1FAE5; -fx-text-fill: #059669; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 3 7; -fx-background-radius: 5px;" :
                "-fx-background-color: #FEF3C7; -fx-text-fill: #D97706; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 3 7; -fx-background-radius: 5px;");

        HBox rightHeader = new HBox(6, statusLabel, paymentBadge);
        rightHeader.setAlignment(Pos.CENTER_RIGHT);

        topRow.setLeft(
                idLabel
        );

        topRow.setRight(
                rightHeader
        );

        // --------------------------------------------------------
        // PATIENT
        // --------------------------------------------------------

        HBox patientRow =
                new HBox(12);

        patientRow.setAlignment(
                Pos.CENTER_LEFT
        );

        String patientName =
                safe(
                        appointment.getPatientName(),
                        "Patient"
                );

        StackPane avatar =
                createInitialsAvatar(
                        getInitials(
                                patientName
                        )
                );

        VBox patientInfo =
                new VBox(3);

        Label patientLabel =
                new Label(
                        patientName
                );

        patientLabel.setStyle(
                "-fx-text-fill: #1E293B;"
                        + "-fx-font-size: 15px;"
                        + "-fx-font-weight: bold;"
        );

        String bookingType =
                safe(
                        appointment.getBookingType(),
                        "DOCTOR"
                );

        String appointmentType =
                bookingType.equalsIgnoreCase(
                        "HOSPITAL"
                )
                        ? "Hospital Appointment"
                        : "Doctor Consultation";

        Label typeLabel =
                new Label(
                        appointmentType
                );

        typeLabel.setStyle(
                "-fx-text-fill: #64748B;"
                        + "-fx-font-size: 12px;"
        );

        patientInfo
                .getChildren()
                .addAll(
                        patientLabel,
                        typeLabel
                );

        patientRow
                .getChildren()
                .addAll(
                        avatar,
                        patientInfo
                );

        // --------------------------------------------------------
        // DATE TIME
        // --------------------------------------------------------

        HBox dateTimeRow =
                new HBox(8);

        dateTimeRow.setAlignment(
                Pos.CENTER_LEFT
        );

        ImageView clock =
                createImageView(
                        "/images/icons/ic_schedule.png",
                        14,
                        14
                );

        Label dateTime =
                new Label(
                        safe(
                                appointment
                                        .getAppointmentDate(),
                                "Date unavailable"
                        )
                                + " | "
                                + safe(
                                appointment
                                        .getAppointmentTime(),
                                "Time unavailable"
                        )
                );

        dateTime.setStyle(
                "-fx-text-fill: #0B57D0;"
                        + "-fx-font-size: 12px;"
                        + "-fx-font-weight: bold;"
        );

        if (clock != null) {

            dateTimeRow
                    .getChildren()
                    .add(
                            clock
                    );
        }

        dateTimeRow
                .getChildren()
                .add(
                        dateTime
                );

        // --------------------------------------------------------
        // REASON
        // --------------------------------------------------------

        HBox reasonRow =
                new HBox(8);

        reasonRow.setAlignment(
                Pos.TOP_LEFT
        );

        Label bullet =
                new Label(
                        "○"
                );

        bullet.setStyle(
                "-fx-text-fill: #2563EB;"
                        + "-fx-font-size: 14px;"
        );

        Label reason =
                new Label(
                        safe(
                                appointment.getReason(),
                                "No reason provided"
                        )
                );

        reason.setWrapText(
                true
        );

        reason.setStyle(
                "-fx-text-fill: #64748B;"
                        + "-fx-font-size: 12px;"
        );

        reasonRow
                .getChildren()
                .addAll(
                        bullet,
                        reason
                );

        // --------------------------------------------------------
        // ACTION BUTTONS
        // --------------------------------------------------------

        HBox actions =
                new HBox(7);

        actions.setAlignment(
                Pos.CENTER_LEFT
        );

        Button viewButton =
                createActionButton(
                        "View Details",
                        "#0B57D0"
                );

        Button approveButton =
                createActionButton(
                        "Approve",
                        "#10B981"
                );

        Button rejectButton =
                createActionButton(
                        "Reject",
                        "#EF4444"
                );

        Button completeButton =
                createActionButton(
                        "Complete",
                        "#10B981"
                );

        Button cancelButton =
                createActionButton(
                        "Cancel",
                        "#EF4444"
                );

        // --------------------------------------------------------
        // VIEW
        // --------------------------------------------------------

        viewButton.setOnAction(
                e ->
                        showAppointmentDetails(
                                appointment
                        )
        );

        // --------------------------------------------------------
        // APPROVE
        // --------------------------------------------------------

        approveButton.setOnAction(
                e ->
                        handleApprove(
                                appointment
                        )
        );

        // --------------------------------------------------------
        // REJECT
        // --------------------------------------------------------

        rejectButton.setOnAction(
                e ->
                        handleReject(
                                appointment
                        )
        );

        // --------------------------------------------------------
        // COMPLETE
        // --------------------------------------------------------

        completeButton.setOnAction(
                e ->
                        handleComplete(
                                appointment
                        )
        );

        // --------------------------------------------------------
        // CANCEL
        // --------------------------------------------------------

        cancelButton.setOnAction(
                e ->
                        handleCancel(
                                appointment
                        )
        );

        // --------------------------------------------------------
        // STATUS-BASED BUTTONS
        // --------------------------------------------------------

        configureActions(
                appointment,
                actions,
                approveButton,
                rejectButton,
                completeButton,
                cancelButton,
                viewButton
        );

        // --------------------------------------------------------
        // ADD ALL
        // --------------------------------------------------------

        card
                .getChildren()
                .addAll(
                        topRow,
                        patientRow,
                        dateTimeRow,
                        reasonRow,
                        actions
                );

        return card;
    }

    // ============================================================
    // CONFIGURE ACTIONS
    // ============================================================

    private void configureActions(
            Appointment appointment,
            HBox actions,
            Button approveButton,
            Button rejectButton,
            Button completeButton,
            Button cancelButton,
            Button viewButton) {

        actions
                .getChildren()
                .clear();

        String status =
                normalizeStatus(
                        appointment.getStatus()
                );

        // --------------------------------------------------------
        // PENDING / CONFIRMED
        // --------------------------------------------------------

        if (status.equals(
                "PENDING"
        )
                || status.equals(
                "CONFIRMED"
        )) {

            actions
                    .getChildren()
                    .addAll(
                            approveButton,
                            rejectButton,
                            viewButton
                    );

            return;
        }

        // --------------------------------------------------------
        // ACCEPTED
        // --------------------------------------------------------

        if (status.equals(
                "ACCEPTED"
        )
                || status.equals(
                "CONFIRMED"
        )) {
            Button videoCallBtn = createActionButton("📹 Join Video Call", "#BA54F5");
            videoCallBtn.setOnAction(e -> handleStartVideoCall(appointment));

            actions
                    .getChildren()
                    .addAll(
                            videoCallBtn,
                            completeButton,
                            cancelButton,
                            viewButton
                    );

            return;
        }

        // --------------------------------------------------------
        // COMPLETED / REJECTED / CANCELLED
        // --------------------------------------------------------

        actions
                .getChildren()
                .add(
                        viewButton
                );
    }

    // ============================================================
    // APPROVE APPOINTMENT
    // ============================================================

    private void handleApprove(
            Appointment appointment) {

        if (appointment == null) {
            return;
        }

        String currentStatus =
                normalizeStatus(
                        appointment.getStatus()
                );

        if (!currentStatus.equals(
                "PENDING"
        )
                && !currentStatus.equals(
                "CONFIRMED"
        )) {

            showInformationAlert(
                    "Cannot Approve",
                    "Only pending or confirmed appointments can be approved."
            );

            return;
        }

        String patientName =
                safe(
                        appointment.getPatientName(),
                        "this patient"
                );

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Approve Appointment"
        );

        confirmation.setHeaderText(
                "Approve appointment for "
                        + patientName
                        + "?"
        );

        confirmation.setContentText(
                "The appointment will move from "
                        + currentStatus
                        + " to ACCEPTED."
        );

        ButtonType result =
                confirmation
                        .showAndWait()
                        .orElse(
                                ButtonType.CANCEL
                        );

        if (result != ButtonType.OK) {
            return;
        }

        updateStatus(
                appointment,
                "ACCEPTED",
                "Appointment Approved",
                "The appointment has been approved successfully."
        );
    }

    // ============================================================
    // REJECT APPOINTMENT
    // ============================================================

    private void handleReject(
            Appointment appointment) {

        if (appointment == null) {
            return;
        }

        String currentStatus =
                normalizeStatus(
                        appointment.getStatus()
                );

        if (!currentStatus.equals(
                "PENDING"
        )
                && !currentStatus.equals(
                "CONFIRMED"
        )) {

            showInformationAlert(
                    "Cannot Reject",
                    "Only pending or confirmed appointments can be rejected."
            );

            return;
        }

        String patientName =
                safe(
                        appointment.getPatientName(),
                        "this patient"
                );

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Reject Appointment"
        );

        confirmation.setHeaderText(
                "Reject appointment for "
                        + patientName
                        + "?"
        );

        confirmation.setContentText(
                "The appointment will be marked as REJECTED."
        );

        ButtonType result =
                confirmation
                        .showAndWait()
                        .orElse(
                                ButtonType.CANCEL
                        );

        if (result != ButtonType.OK) {
            return;
        }

        updateStatus(
                appointment,
                "REJECTED",
                "Appointment Rejected",
                "The appointment has been rejected."
        );
    }

    // ============================================================
    // COMPLETE APPOINTMENT
    // ============================================================

    private void handleComplete(
            Appointment appointment) {

        if (appointment == null) {
            return;
        }

        String currentStatus =
                normalizeStatus(
                        appointment.getStatus()
                );

        if (!currentStatus.equals(
                "ACCEPTED"
        )) {

            showInformationAlert(
                    "Cannot Complete",
                    "Only accepted appointments can be completed."
            );

            return;
        }

        String patientName =
                safe(
                        appointment.getPatientName(),
                        "this patient"
                );

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Complete Appointment"
        );

        confirmation.setHeaderText(
                "Complete appointment for "
                        + patientName
                        + "?"
        );

        confirmation.setContentText(
                "The appointment will be marked as COMPLETED."
        );

        ButtonType result =
                confirmation
                        .showAndWait()
                        .orElse(
                                ButtonType.CANCEL
                        );

        if (result != ButtonType.OK) {
            return;
        }

        updateStatus(
                appointment,
                "COMPLETED",
                "Appointment Completed",
                "The appointment has been completed successfully."
        );
    }

    // ============================================================
    // CANCEL APPOINTMENT
    // ============================================================

    private void handleCancel(
            Appointment appointment) {

        if (appointment == null) {
            return;
        }

        String currentStatus =
                normalizeStatus(
                        appointment.getStatus()
                );

        if (!currentStatus.equals(
                "ACCEPTED"
        )) {

            showInformationAlert(
                    "Cannot Cancel",
                    "Only accepted appointments can be cancelled."
            );

            return;
        }

        String patientName =
                safe(
                        appointment.getPatientName(),
                        "this patient"
                );

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Cancel Appointment"
        );

        confirmation.setHeaderText(
                "Cancel appointment for "
                        + patientName
                        + "?"
        );

        confirmation.setContentText(
                "The appointment will be marked as CANCELLED."
        );

        ButtonType result =
                confirmation
                        .showAndWait()
                        .orElse(
                                ButtonType.CANCEL
                        );

        if (result != ButtonType.OK) {
            return;
        }

        /*
         * Use the existing controller cancel method.
         */
        try {

            appointmentController
                    .cancelAppointment(
                            appointment
                                    .getAppointmentId()
                    );

            loadAppointmentsFromFirebase();

            renderAppointments();

            showInformationAlert(
                    "Appointment Cancelled",
                    "The appointment has been cancelled successfully."
            );

        } catch (Exception e) {

            e.printStackTrace();

            showErrorAlert(
                    "Cancel Failed",
                    getRootErrorMessage(e)
            );
        }
    }

    // ============================================================
    // GENERIC STATUS UPDATE
    // ============================================================

    private void updateStatus(
            Appointment appointment,
            String newStatus,
            String successTitle,
            String successMessage) {

        try {

            String appointmentId =
                    appointment.getAppointmentId();

            if (appointmentId == null
                    || appointmentId.trim().isEmpty()) {

                throw new IllegalArgumentException(
                        "Appointment ID is missing."
                );
            }

            /*
             * REAL FIREBASE UPDATE
             *
             * View
             * ↓
             * Controller
             * ↓
             * DAO
             * ↓
             * Firestore
             */
            appointmentController
                    .updateAppointmentStatus(
                            appointmentId,
                            newStatus
                    );

            /*
             * Reload from Firebase.
             *
             * This guarantees that the UI reflects
             * the actual database state.
             */
            loadAppointmentsFromFirebase();

            renderAppointments();

            showInformationAlert(
                    successTitle,
                    successMessage
            );

        } catch (Exception e) {

            e.printStackTrace();

            showErrorAlert(
                    "Status Update Failed",
                    getRootErrorMessage(e)
            );
        }
    }

    // ============================================================
    // VIEW DETAILS
    // ============================================================

    private void showAppointmentDetails(
            Appointment appointment) {

        if (appointment == null) {
            return;
        }

        String details =
                "Appointment ID: "
                        + safe(
                        appointment.getAppointmentId(),
                        "Not available"
                )
                        + "\n\n"
                        + "Patient: "
                        + safe(
                        appointment.getPatientName(),
                        "Not available"
                )
                        + "\n"
                        + "Patient UID: "
                        + safe(
                        appointment.getPatientUid(),
                        "Not available"
                )
                        + "\n\n"
                        + "Doctor: "
                        + safe(
                        appointment.getDoctorName(),
                        "Not available"
                )
                        + "\n"
                        + "Booking Type: "
                        + safe(
                        appointment.getBookingType(),
                        "Not available"
                )
                        + "\n\n"
                        + "Date: "
                        + safe(
                        appointment.getAppointmentDate(),
                        "Not available"
                )
                        + "\n"
                        + "Time: "
                        + safe(
                        appointment.getAppointmentTime(),
                        "Not available"
                )
                        + "\n\n"
                        + "Reason: "
                        + safe(
                        appointment.getReason(),
                        "Not provided"
                )
                        + "\n\n"
                        + "Status: "
                        + normalizeStatus(
                        appointment.getStatus()
                );

        showInformationAlert(
                "Appointment Details",
                details
        );
    }

    // ============================================================
    // STATUS NORMALIZATION
    // ============================================================

    private String normalizeStatus(
            String status) {

        if (status == null
                || status.trim().isEmpty()) {

            return "PENDING";
        }

        String value =
                status.trim()
                        .toUpperCase(
                                Locale.ROOT
                        );

        switch (value) {

            case "PENDING":
                return "PENDING";

            case "CONFIRMED":
                return "CONFIRMED";

            case "ACCEPTED":
                return "ACCEPTED";

            case "REJECTED":
                return "REJECTED";

            case "COMPLETED":
                return "COMPLETED";

            case "CANCELLED":
            case "CANCELED":
                return "CANCELLED";

            default:
                return value;
        }
    }

    // ============================================================
    // STATUS STYLE
    // ============================================================

    private String getStatusStyle(
            String status) {

        switch (normalizeStatus(status)) {

            case "ACCEPTED":
            case "CONFIRMED":

                return
                        "-fx-background-color: #ECFDF5;"
                                + "-fx-text-fill: #059669;"
                                + "-fx-background-radius: 20px;"
                                + "-fx-padding: 5px 9px;"
                                + "-fx-font-size: 10px;"
                                + "-fx-font-weight: bold;";

            case "COMPLETED":

                return
                        "-fx-background-color: #DCFCE7;"
                                + "-fx-text-fill: #15803D;"
                                + "-fx-background-radius: 20px;"
                                + "-fx-padding: 5px 9px;"
                                + "-fx-font-size: 10px;"
                                + "-fx-font-weight: bold;";

            case "REJECTED":
            case "CANCELLED":

                return
                        "-fx-background-color: #FEF2F2;"
                                + "-fx-text-fill: #DC2626;"
                                + "-fx-background-radius: 20px;"
                                + "-fx-padding: 5px 9px;"
                                + "-fx-font-size: 10px;"
                                + "-fx-font-weight: bold;";

            case "PENDING":
            default:

                return
                        "-fx-background-color: #FFF7ED;"
                                + "-fx-text-fill: #C2410C;"
                                + "-fx-background-radius: 20px;"
                                + "-fx-padding: 5px 9px;"
                                + "-fx-font-size: 10px;"
                                + "-fx-font-weight: bold;";
        }
    }

    // ============================================================
    // ACTION BUTTON
    // ============================================================

    private Button createActionButton(
            String text,
            String color) {

        Button button =
                new Button(
                        text
                );

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox.setHgrow(
                button,
                Priority.ALWAYS
        );

        button.setStyle(
                "-fx-background-color: "
                        + color
                        + ";"
                        + "-fx-text-fill: white;"
                        + "-fx-background-radius: 6px;"
                        + "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-padding: 8px 9px;"
                        + "-fx-cursor: hand;"
        );

        return button;
    }

    // ============================================================
    // INITIALS AVATAR
    // ============================================================

    private StackPane createInitialsAvatar(
            String initials) {

        Circle circle =
                new Circle(
                        21,
                        Color.web(
                                "#DBEAFE"
                        )
                );

        Label label =
                new Label(
                        initials
                );

        label.setStyle(
                "-fx-text-fill: #2563EB;"
                        + "-fx-font-size: 12px;"
                        + "-fx-font-weight: bold;"
        );

        StackPane avatar =
                new StackPane(
                        circle,
                        label
                );

        avatar.setMinSize(
                42,
                42
        );

        avatar.setMaxSize(
                42,
                42
        );

        return avatar;
    }

    // ============================================================
    // INITIALS
    // ============================================================

    private String getInitials(
            String name) {

        if (name == null
                || name.trim().isEmpty()) {

            return "PT";
        }

        String[] parts =
                name.trim()
                        .split(
                                "\\s+"
                        );

        if (parts.length == 1) {

            return parts[0]
                    .substring(
                            0,
                            1
                    )
                    .toUpperCase(
                            Locale.ROOT
                    );
        }

        return (
                ""
                        + parts[0]
                        .charAt(0)
                        + parts[parts.length - 1]
                        .charAt(0)
        )
                .toUpperCase(
                        Locale.ROOT
                );
    }

    // ============================================================
    // SHORT APPOINTMENT ID
    // ============================================================

    private String shortAppointmentId(
            String id) {

        if (id == null
                || id.trim().isEmpty()) {

            return "Appointment";
        }

        String value =
                id.trim();

        if (value.length() <= 28) {

            return value;
        }

        return value.substring(
                0,
                24
        ) + "...";
    }

    // ============================================================
    // PARSE DATE
    // ============================================================

    private LocalDate parseDate(
            String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return null;
        }

        try {

            return LocalDate.parse(
                    value.trim(),
                    DATE_FORMAT
            );

        } catch (DateTimeParseException e) {

            return null;
        }
    }

    // ============================================================
    // SAFE STRING
    // ============================================================

    private String safe(
            String value,
            String fallback) {

        if (value == null
                || value.trim().isEmpty()) {

            return fallback;
        }

        return value.trim();
    }

    // ============================================================
    // IMAGE
    // ============================================================

    private ImageView createImageView(
            String path,
            double width,
            double height) {

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

            System.out.println(
                    "Unable to load image: "
                            + path
            );

            return null;
        }
    }

    // ============================================================
    // ERROR MESSAGE
    // ============================================================

    private String getRootErrorMessage(
            Throwable throwable) {

        Throwable current =
                throwable;

        while (current.getCause() != null) {

            current =
                    current.getCause();
        }

        String message =
                current.getMessage();

        if (message == null
                || message.trim().isEmpty()) {

            return "An unexpected error occurred.";
        }

        return message;
    }

    // ============================================================
    // INFORMATION ALERT
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

    // ============================================================
    // ERROR ALERT
    // ============================================================

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

    private void handleStartVideoCall(Appointment appointment) {
        if (appointment == null) return;
        String callerEmail = SessionManager.getDoctorUid() != null ? SessionManager.getDoctorUid() : com.healthsphere.model.UserModel.getInstance().getEmail();
        String callerName = SessionManager.getDoctorDisplayName() != null ? SessionManager.getDoctorDisplayName() : "Doctor";

        String receiverEmail = appointment.getPatientUid() != null ? appointment.getPatientUid() : "patient@healthsphere.com";
        String receiverName = appointment.getPatientName() != null ? appointment.getPatientName() : "Patient";

        String roomId = com.healthsphere.config.RoomGenerator.generateRoomIdForAppointment(appointment.getAppointmentId());
        com.healthsphere.dao.CallDao callDao = new com.healthsphere.dao.CallDao();

        com.healthsphere.model.Call call = callDao.getOrCreateCallForAppointment(callerEmail, callerName, receiverEmail, receiverName, roomId);

        Stage modalStage = new Stage();
        modalStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        modalStage.initOwner(stage);
        modalStage.setTitle("Doctor Live Video Call - " + receiverName);

        com.healthsphere.view.common.VideoCallScreen videoCallScreen = new com.healthsphere.view.common.VideoCallScreen(call, modalStage::close);
        Scene modalScene = new Scene(videoCallScreen, 680, 520);
        modalStage.setScene(modalScene);
        modalStage.show();
    }
}