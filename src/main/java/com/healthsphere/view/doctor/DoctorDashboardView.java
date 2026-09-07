package com.healthsphere.view.doctor;

import com.healthsphere.controller.appointment.AppointmentController;
import com.healthsphere.model.Appointment;
import com.healthsphere.util.Navigation;
import com.healthsphere.util.SessionManager;
import com.healthsphere.util.ShimmerPlaceholder;
import com.healthsphere.util.SummaryCard;
import com.healthsphere.view.authentication.LoginView;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * DoctorDashboardView
 *
 * Dynamic Doctor Dashboard.
 *
 * Architecture:
 *
 * View
 *   ↓
 * AppointmentController
 *   ↓
 * AppointmentDAO
 *   ↓
 * Firebase Firestore
 *
 * Dashboard data is loaded for the currently logged-in doctor.
 */
public class DoctorDashboardView {

    // ============================================================
    // BASIC FIELDS
    // ============================================================

    private final Stage stage;
    private final Scene scene;

    private final AppointmentController appointmentController;


    // ============================================================
    // DASHBOARD DATA
    // ============================================================

    private List<Appointment> doctorAppointments =
            new ArrayList<>();


    // ============================================================
    // UI REFERENCES
    // ============================================================

    private VBox appointmentsList;

    private VBox activityList;

    private VBox aiInsightsList;

    private Label welcomeGreeting;

    private Label welcomeSummary;

    private Label totalPatientsValue;

    private Label todayAppointmentsValue;

    private Label patientGrowthValue;

    private Label revenueValue;

    private Label todayAppointmentsDetail;

    private Label patientGrowthDetail;

    private Label revenueDetail;

    private Label doctorSidebarName;


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public DoctorDashboardView(Stage stage) {
        this.stage = stage;
        this.appointmentController = new AppointmentController();

        // Create scene immediately so navigation is non-blocking and instant
        this.scene = createScene();

        // Load data asynchronously on background thread
        loadDashboardDataAsync();
    }

    public Scene getScene() {
        return scene;
    }

    private static class DoctorDashboardData {
        final List<Appointment> appointments;
        final double totalEarnings;
        final int validPaymentsCount;

        DoctorDashboardData(List<Appointment> appointments, double totalEarnings, int validPaymentsCount) {
            this.appointments = appointments;
            this.totalEarnings = totalEarnings;
            this.validPaymentsCount = validPaymentsCount;
        }
    }

    private void loadDashboardDataAsync() {
        showShimmerLoadingState();

        String doctorUid = getCurrentDoctorUid();
        if (doctorUid == null || doctorUid.isBlank()) {
            System.err.println("No logged-in doctor found.");
            return;
        }

        Task<DoctorDashboardData> loadTask = new Task<>() {
            @Override
            protected DoctorDashboardData call() throws Exception {
                List<Appointment> appointments = appointmentController.getDoctorAppointments(doctorUid);
                if (appointments == null) {
                    appointments = new ArrayList<>();
                }

                double earnings = 0.0;
                int validCount = 0;
                try {
                    com.healthsphere.dao.common.PaymentDAO paymentDAO = new com.healthsphere.dao.common.PaymentDAO();
                    earnings = paymentDAO.getDoctorTotalEarnings(doctorUid);
                    List<com.healthsphere.model.PaymentRecord> payments = paymentDAO.getPaymentsForDoctor(doctorUid);
                    if (payments != null) {
                        for (com.healthsphere.model.PaymentRecord p : payments) {
                            if (p != null && p.getStatus() != null &&
                                (p.getStatus().equalsIgnoreCase("COMPLETED") || p.getStatus().equalsIgnoreCase("SUCCESS") || p.getStatus().equalsIgnoreCase("PAID") || p.getStatus().equalsIgnoreCase("RECEIVED"))) {
                                validCount++;
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Unable to load earnings in background Task: " + e.getMessage());
                }

                return new DoctorDashboardData(appointments, earnings, validCount);
            }
        };

        loadTask.setOnSucceeded(event -> {
            DoctorDashboardData data = loadTask.getValue();
            doctorAppointments = data.appointments;
            doctorAppointments.sort(Comparator.comparing(this::getAppointmentDateTimeSafe));
            System.out.println("Doctor Dashboard loaded " + doctorAppointments.size() + " appointments.");
            updateUIWithLoadedData(data);
        });

        loadTask.setOnFailed(event -> {
            System.err.println("Unable to load dashboard data: " + getRootMessage(loadTask.getException()));
            updateUIWithLoadedData(new DoctorDashboardData(new ArrayList<>(), 0.0, 0));
        });

        Thread thread = new Thread(loadTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadDashboardData() {
        loadDashboardDataAsync();
    }

    private void showShimmerLoadingState() {
        if (appointmentsList != null) {
            appointmentsList.getChildren().setAll(ShimmerPlaceholder.createListShimmer(3));
        }
        if (activityList != null) {
            activityList.getChildren().setAll(ShimmerPlaceholder.createListShimmer(2));
        }
        if (totalPatientsValue != null) totalPatientsValue.setText("...");
        if (todayAppointmentsValue != null) todayAppointmentsValue.setText("...");
        if (patientGrowthValue != null) patientGrowthValue.setText("...");
        if (revenueValue != null) revenueValue.setText("...");
    }

    private void updateUIWithLoadedData(DoctorDashboardData data) {
        if (totalPatientsValue != null) {
            totalPatientsValue.setText(String.valueOf(getTotalPatientCount()));
        }
        if (todayAppointmentsValue != null) {
            todayAppointmentsValue.setText(String.valueOf(getTodayAppointments().size()));
        }
        if (patientGrowthValue != null) {
            int growth = calculatePatientGrowth();
            patientGrowthValue.setText(growth >= 0 ? "+" + growth : String.valueOf(growth));
        }
        if (todayAppointmentsDetail != null) {
            todayAppointmentsDetail.setText(getTodayAppointmentDetail());
        }
        if (patientGrowthDetail != null) {
            patientGrowthDetail.setText(getPatientGrowthDetail());
        }

        if (revenueValue != null) {
            revenueValue.setText(String.format("₹%,.2f", data.totalEarnings));
        }
        if (revenueDetail != null) {
            revenueDetail.setText(data.validPaymentsCount + " completed payments");
        }

        if (appointmentsList != null) {
            refreshAppointmentsList();
        }
        if (activityList != null) {
            refreshActivityList();
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
        // FIXED SIDEBAR
        // --------------------------------------------------------

        VBox sidebar =
                createSidebar();

        mainRoot.setLeft(sidebar);


        // --------------------------------------------------------
        // MAIN CONTENT
        // --------------------------------------------------------

        VBox mainContent =
                new VBox(24);

        mainContent.setPadding(
                new Insets(24)
        );

        mainContent.getStyleClass()
                .add("content-area");


        // Top bar
        mainContent.getChildren()
                .add(
                        createTopBar()
                );


        // Welcome banner
        mainContent.getChildren()
                .add(
                        createWelcomeBanner()
                );


        // Statistics
        mainContent.getChildren()
                .add(
                        createStatCardsRow()
                );


        // Today's appointments + AI
        mainContent.getChildren()
                .add(
                        createAppointmentsInsightsRow()
                );


        // Recent activity
        mainContent.getChildren()
                .add(
                        createRecentActivityTable()
                );


        // --------------------------------------------------------
        // DASHBOARD SCROLL
        // --------------------------------------------------------

        ScrollPane contentScrollPane =
                new ScrollPane(
                        mainContent
                );


        /*
         * Sidebar stays fixed because only mainContent
         * is inside this ScrollPane.
         */
        contentScrollPane.setFitToWidth(
                true
        );


        /*
         * IMPORTANT:
         *
         * false allows the content to become taller
         * than the viewport and enables vertical scrolling.
         */
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

        Scene dashboardScene =
                new Scene(
                        mainRoot,
                        stage.getWidth() > 0 ? stage.getWidth() : 1200,
                        stage.getHeight() > 0 ? stage.getHeight() : 750
                );


        try {

            dashboardScene
                    .getStylesheets()
                    .add(
                            Objects.requireNonNull(
                                    getClass()
                                            .getResource(
                                                    "/css/dashboard.css"
                                            )
                            ).toExternalForm()
                    );

        } catch (Exception e) {

            System.err.println(
                    "dashboard.css not found."
            );
        }


        return dashboardScene;
    }


    // ============================================================
    // SIDEBAR
    // ============================================================

    private VBox createSidebar() {
        return DoctorSidebar.create(stage, 0);
    }


    // ============================================================
    // NAVIGATION
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

        System.out.println(
                "Doctor logged out."
        );

        Navigation.goTo(
                stage,
                () -> new LoginView(stage).getScene()
        );
    }


    // ============================================================
    // TOP BAR
    // ============================================================

    private BorderPane createTopBar() {

        BorderPane topBar =
                new BorderPane();


        Label breadcrumb =
                new Label(
                        "Dashboard"
                );

        breadcrumb.getStyleClass()
                .add(
                        "breadcrumb"
                );


        // --------------------------------------------------------
        // RIGHT SIDE
        // --------------------------------------------------------

        HBox rightSection =
                new HBox(10);

        rightSection.setAlignment(
                Pos.CENTER_RIGHT
        );


        /*
         * Refresh button.
         *
         * This reloads Firestore data and rebuilds
         * the dashboard.
         */
        Button refreshButton =
                new Button(
                        "Refresh"
                );

        refreshButton.getStyleClass()
                .add(
                        "btn-secondary"
                );


        refreshButton.setOnAction(
                event ->
                        refreshDashboard()
        );


        rightSection.getChildren()
                .add(
                        refreshButton
                );


        topBar.setLeft(
                breadcrumb
        );


        topBar.setRight(
                rightSection
        );


        return topBar;
    }


    // ============================================================
    // REFRESH DASHBOARD
    // ============================================================

    private void refreshDashboard() {

        System.out.println(
                "Refreshing doctor dashboard..."
        );


        loadDashboardData();


        /*
         * Refresh statistic values.
         */
        if (totalPatientsValue != null) {

            totalPatientsValue.setText(
                    String.valueOf(
                            getTotalPatientCount()
                    )
            );
        }


        if (todayAppointmentsValue != null) {

            todayAppointmentsValue.setText(
                    String.valueOf(
                            getTodayAppointments()
                                    .size()
                    )
            );
        }


        if (patientGrowthValue != null) {

            int growth =
                    calculatePatientGrowth();


            patientGrowthValue.setText(
                    growth >= 0
                            ? "+" + growth
                            : String.valueOf(
                                    growth
                            )
            );
        }


        if (todayAppointmentsDetail != null) {

            todayAppointmentsDetail.setText(
                    getTodayAppointmentDetail()
            );
        }


        if (patientGrowthDetail != null) {

            patientGrowthDetail.setText(
                    getPatientGrowthDetail()
            );
        }


        /*
         * Refresh appointment list.
         */
        if (appointmentsList != null) {

            refreshAppointmentsList();
        }


        /*
         * Refresh recent activity.
         */
        if (activityList != null) {

            refreshActivityList();
        }


        /*
         * Refresh AI panel.
         */
        if (aiInsightsList != null) {

            refreshAiInsights();
        }


        System.out.println(
                "Dashboard refreshed."
        );
    }


    // ============================================================
    // WELCOME BANNER
    // ============================================================

    private HBox createWelcomeBanner() {

        HBox banner =
                new HBox(20);

        banner.getStyleClass()
                .add(
                        "welcome-banner"
                );

        banner.setPadding(
                new Insets(28)
        );

        banner.setAlignment(
                Pos.CENTER_LEFT
        );


        VBox textSection =
                new VBox(12);

        textSection.setAlignment(
                Pos.CENTER_LEFT
        );

        HBox.setHgrow(
                textSection,
                Priority.ALWAYS
        );


        welcomeGreeting =
                new Label(
                        getWelcomeGreeting()
                );

        welcomeGreeting.getStyleClass()
                .add(
                        "welcome-greeting"
                );


        welcomeSummary =
                new Label(
                        getWelcomeSummary()
                );

        welcomeSummary.getStyleClass()
                .add(
                        "welcome-summary"
                );

        welcomeSummary.setWrapText(
                true
        );


        HBox buttonsSection =
                new HBox(12);


        Button startConsultBtn =
                new Button(
                        "Start Consultations"
                );

        startConsultBtn.getStyleClass()
                .add(
                        "btn-primary"
                );


        Button viewScheduleBtn =
                new Button(
                        "View Schedule"
                );

        viewScheduleBtn.getStyleClass()
                .add(
                        "btn-secondary"
                );


        startConsultBtn.setOnAction(
                event ->
                        handleSidebarTabClick(
                                2
                        )
        );


        viewScheduleBtn.setOnAction(
                event ->
                        handleSidebarTabClick(
                                1
                        )
        );


        buttonsSection.getChildren()
                .addAll(
                        startConsultBtn,
                        viewScheduleBtn
                );


        textSection.getChildren()
                .addAll(
                        welcomeGreeting,
                        welcomeSummary,
                        buttonsSection
                );


        ImageView illustration =
                createImageView(
                        "/images/doctor/doctor_welcome.png",
                        220,
                        140
                );


        banner.getChildren()
                .add(
                        textSection
                );


        if (illustration != null) {

            banner.getChildren()
                    .add(
                            illustration
                    );
        }


        return banner;
    }


    // ============================================================
    // STAT CARDS
    // ============================================================

    private HBox createStatCardsRow() {
        HBox row = new HBox(16);

        int totalPatients = getTotalPatientCount();
        int todayAppointments = getTodayAppointments().size();
        int patientGrowth = calculatePatientGrowth();

        String growthText = patientGrowth >= 0 ? "+" + patientGrowth : String.valueOf(patientGrowth);

        SummaryCard.CardNode totalPatientsCard = SummaryCard.createCardNode(
                "TOTAL PATIENTS",
                String.valueOf(totalPatients),
                getPatientGrowthDetail(),
                "👥",
                SummaryCard.CardType.PURPLE
        );
        totalPatientsValue = totalPatientsCard.getValueLabel();

        SummaryCard.CardNode todayApptsCard = SummaryCard.createCardNode(
                "TODAY'S APPTS",
                String.valueOf(todayAppointments),
                getTodayAppointmentDetail(),
                "📅",
                SummaryCard.CardType.ORANGE
        );
        todayAppointmentsValue = todayApptsCard.getValueLabel();
        todayAppointmentsDetail = todayApptsCard.getSubtitleLabel();

        SummaryCard.CardNode growthCard = SummaryCard.createCardNode(
                "PATIENT GROWTH",
                growthText,
                getPatientGrowthDetail(),
                "📈",
                SummaryCard.CardType.GREEN
        );
        patientGrowthValue = growthCard.getValueLabel();
        patientGrowthDetail = growthCard.getSubtitleLabel();

        SummaryCard.CardNode revenueCard = SummaryCard.createCardNode(
                "REVENUE (MTD)",
                "—",
                "Billing data not available",
                "💳",
                SummaryCard.CardType.BLUE
        );
        revenueValue = revenueCard.getValueLabel();
        revenueDetail = revenueCard.getSubtitleLabel();

        row.getChildren().addAll(
                totalPatientsCard.getContainer(),
                todayApptsCard.getContainer(),
                growthCard.getContainer(),
                revenueCard.getContainer()
        );

        return row;
    }


    // ============================================================
    // STAT CARD
    // ============================================================

    private VBox createDynamicStatCard(
            String title,
            Label valueLabel,
            String iconName,
            Label detailLabel,
            boolean showProgress
    ) {

        VBox card =
                new VBox(12);

        card.getStyleClass()
                .add(
                        "stat-card"
                );

        HBox.setHgrow(
                card,
                Priority.ALWAYS
        );

        card.setPadding(
                new Insets(20)
        );


        HBox cardHeader =
                new HBox(10);

        cardHeader.setAlignment(
                Pos.CENTER_LEFT
        );


        Label titleLabel =
                new Label(
                        title
                );

        titleLabel.getStyleClass()
                .add(
                        "stat-title"
                );


        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );


        ImageView icon =
                createImageView(
                        "/images/icons/cards/"
                                + iconName
                                + ".png",
                        20,
                        20
                );


        cardHeader.getChildren()
                .addAll(
                        titleLabel,
                        spacer
                );


        if (icon != null) {

            cardHeader.getChildren()
                    .add(
                            icon
                    );
        }


        valueLabel.getStyleClass()
                .add(
                        "stat-value"
                );


        VBox footer =
                new VBox(6);


        detailLabel.getStyleClass()
                .add(
                        "stat-detail"
                );


        if (showProgress) {

            ProgressBar progress =
                    new ProgressBar(
                            calculateTodayProgress()
                    );

            progress.getStyleClass()
                    .add(
                            "stat-progress"
                    );

            progress.setMaxWidth(
                    Double.MAX_VALUE
            );


            footer.getChildren()
                    .addAll(
                            detailLabel,
                            progress
                    );

        } else {

            footer.getChildren()
                    .add(
                            detailLabel
                    );
        }


        card.getChildren()
                .addAll(
                        cardHeader,
                        valueLabel,
                        footer
                );


        return card;
    }


    // ============================================================
    // APPOINTMENTS + AI
    // ============================================================

    private HBox createAppointmentsInsightsRow() {

        HBox row =
                new HBox(16);


        // --------------------------------------------------------
        // APPOINTMENTS CARD
        // --------------------------------------------------------

        VBox appointmentsCard =
                new VBox(16);

        appointmentsCard.getStyleClass()
                .add(
                        "app-card"
                );

        HBox.setHgrow(
                appointmentsCard,
                Priority.ALWAYS
        );

        appointmentsCard.setPadding(
                new Insets(20)
        );


        HBox apptsHeader =
                new HBox(10);

        apptsHeader.setAlignment(
                Pos.CENTER_LEFT
        );


        Label apptsTitle =
                new Label(
                        "Today's Appointments"
                );

        apptsTitle.getStyleClass()
                .add(
                        "app-card-title"
                );


        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );


        Label viewCalendar =
                new Label(
                        "View Full Calendar"
                );

        viewCalendar.getStyleClass()
                .add(
                        "app-card-link"
                );


        viewCalendar.setOnMouseClicked(
                e ->
                        handleSidebarTabClick(
                                1
                        )
        );


        apptsHeader.getChildren()
                .addAll(
                        apptsTitle,
                        spacer,
                        viewCalendar
                );


        appointmentsList =
                new VBox(12);


        /*
         * IMPORTANT:
         *
         * No limit here.
         *
         * All today's appointments are displayed.
         *
         * If there are 10, 20, or 50 patients,
         * the dashboard ScrollPane handles scrolling.
         */
        refreshAppointmentsList();


        appointmentsCard.getChildren()
                .addAll(
                        apptsHeader,
                        appointmentsList
                );


        // --------------------------------------------------------
        // AI INSIGHTS CARD
        // --------------------------------------------------------

        VBox aiInsightsCard =
                new VBox(16);

        aiInsightsCard.getStyleClass()
                .add(
                        "ai-card"
                );

        aiInsightsCard.setMinWidth(
                340
        );

        aiInsightsCard.setPrefWidth(
                340
        );

        aiInsightsCard.setPadding(
                new Insets(20)
        );


        HBox aiHeader =
                new HBox(10);

        aiHeader.setAlignment(
                Pos.CENTER_LEFT
        );


        ImageView aiIcon =
                createImageView(
                        "/images/doctor/ai_assistant.png",
                        20,
                        20
                );


        Label aiTitle =
                new Label(
                        "AI Insights"
                );

        aiTitle.getStyleClass()
                .add(
                        "ai-card-title"
                );


        if (aiIcon != null) {

            aiHeader.getChildren()
                    .add(
                            aiIcon
                    );
        }


        aiHeader.getChildren()
                .add(
                        aiTitle
                );


        aiInsightsList =
                new VBox(12);


        refreshAiInsights();


        aiInsightsCard.getChildren()
                .addAll(
                        aiHeader,
                        aiInsightsList
                );


        row.getChildren()
                .addAll(
                        appointmentsCard,
                        aiInsightsCard
                );


        return row;
    }


    // ============================================================
    // REFRESH APPOINTMENTS
    // ============================================================

    private void refreshAppointmentsList() {

        if (appointmentsList == null) {
            return;
        }


        appointmentsList.getChildren()
                .clear();


        List<Appointment> today =
                getTodayAppointments();


        if (today.isEmpty()) {

            Label empty =
                    new Label(
                            "No appointments scheduled for today."
                    );

            empty.setWrapText(
                    true
            );

            empty.setStyle(
                    "-fx-text-fill: #64748B;"
                            + "-fx-font-size: 13px;"
            );


            appointmentsList.getChildren()
                    .add(
                            empty
                    );


            return;
        }


        /*
         * Display EVERY appointment.
         */
        for (Appointment appointment :
                today) {

            appointmentsList.getChildren()
                    .add(
                            createApptEntry(
                                    appointment
                            )
                    );
        }
    }


    // ============================================================
    // APPOINTMENT ENTRY
    // ============================================================

    private GridPane createApptEntry(
            Appointment appointment
    ) {

        String status =
                safeText(
                        appointment.getStatus(),
                        "Pending"
                );


        GridPane entry =
                new GridPane();

        entry.getStyleClass()
                .add(
                        "appt-entry"
                );


        if ("In Progress".equalsIgnoreCase(
                status
        )) {

            entry.getStyleClass()
                    .add(
                            "appt-entry-highlight"
                    );
        }


        entry.setHgap(
                16
        );

        entry.setPadding(
                new Insets(
                        12
                )
        );

        entry.setAlignment(
                Pos.CENTER_LEFT
        );


        Label timeLabel =
                new Label(
                        safeText(
                                appointment.getAppointmentTime(),
                                "Time N/A"
                        )
                );

        timeLabel.getStyleClass()
                .add(
                        "appt-time"
                );


        Circle timeline =
                new Circle(
                        4
                );


        if ("In Progress".equalsIgnoreCase(
                status
        )) {

            timeline.setFill(
                    Color.web(
                            "#2563EB"
                    )
            );

        } else if ("Completed".equalsIgnoreCase(
                status
        )) {

            timeline.setFill(
                    Color.GRAY
            );

        } else {

            timeline.setFill(
                    Color.WHITE
            );

            timeline.setStroke(
                    Color.GRAY
            );
        }


        VBox patientDetails =
                new VBox(2);


        Label nameLabel =
                new Label(
                        safeText(
                                appointment.getPatientName(),
                                "Unknown Patient"
                        )
                );

        nameLabel.getStyleClass()
                .add(
                        "appt-name"
                );


        Label purposeLabel =
                new Label(
                        safeText(
                                appointment.getReason(),
                                "Consultation"
                        )
                );

        purposeLabel.getStyleClass()
                .add(
                        "appt-purpose"
                );

        purposeLabel.setWrapText(
                true
        );


        patientDetails.getChildren()
                .addAll(
                        nameLabel,
                        purposeLabel
                );


        HBox statusPill =
                createPill(
                        status
                );


        entry.add(
                timeLabel,
                0,
                0
        );


        entry.add(
                timeline,
                1,
                0
        );


        entry.add(
                patientDetails,
                2,
                0
        );


        entry.add(
                statusPill,
                3,
                0
        );


        ColumnConstraints col1 =
                new ColumnConstraints(
                        80
                );


        ColumnConstraints col2 =
                new ColumnConstraints(
                        20
                );


        ColumnConstraints col3 =
                new ColumnConstraints();


        col3.setHgrow(
                Priority.ALWAYS
        );


        ColumnConstraints col4 =
                new ColumnConstraints(
                        110
                );


        entry.getColumnConstraints()
                .addAll(
                        col1,
                        col2,
                        col3,
                        col4
                );


        /*
         * Clicking appointment opens Patient Details.
         */
        entry.setOnMouseClicked(
                event ->
                        handlePatientFromAppointment(
                                appointment
                        )
        );


        return entry;
    }


    // ============================================================
    // PATIENT NAVIGATION
    // ============================================================

    private void handlePatientFromAppointment(
            Appointment appointment
    ) {

        if (appointment == null) {
            return;
        }


        String patientUid =
                appointment.getPatientUid();


        System.out.println(
                "Selected patient: "
                        + patientUid
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
    // AI INSIGHTS
    // ============================================================

    private void refreshAiInsights() {

        if (aiInsightsList == null) {
            return;
        }


        aiInsightsList.getChildren()
                .clear();


        List<Appointment> today =
                getTodayAppointments();


        long pending =
                today.stream()
                        .filter(
                                appointment ->
                                        appointment != null
                                                && "Pending"
                                                .equalsIgnoreCase(
                                                        safeText(
                                                                appointment
                                                                        .getStatus(),
                                                                ""
                                                        )
                                                )
                        )
                        .count();


        long confirmed =
                today.stream()
                        .filter(
                                appointment ->
                                        appointment != null
                                                && "Confirmed"
                                                .equalsIgnoreCase(
                                                        safeText(
                                                                appointment
                                                                        .getStatus(),
                                                                ""
                                                        )
                                                )
                        )
                        .count();


        long completed =
                today.stream()
                        .filter(
                                appointment ->
                                        appointment != null
                                                && "Completed"
                                                .equalsIgnoreCase(
                                                        safeText(
                                                                appointment
                                                                        .getStatus(),
                                                                ""
                                                        )
                                                )
                        )
                        .count();


        if (pending > 0) {

            aiInsightsList.getChildren()
                    .add(
                            createAiInsightEntry(
                                    "Action Required",
                                    pending
                                            + " appointment"
                                            + (
                                            pending == 1
                                                    ? ""
                                                    : "s"
                                    )
                                            + " "
                                            + (
                                            pending == 1
                                                    ? "is"
                                                    : "are"
                                    )
                                            + " still pending today.",
                                    "Review Appointments",
                                    Color.RED
                            )
                    );
        }


        if (confirmed > 0) {

            aiInsightsList.getChildren()
                    .add(
                            createAiInsightEntry(
                                    "Today's Schedule",
                                    confirmed
                                            + " confirmed appointment"
                                            + (
                                            confirmed == 1
                                                    ? ""
                                                    : "s"
                                    )
                                            + " scheduled for today.",
                                    "",
                                    Color.BLUE
                            )
                    );
        }


        if (completed > 0) {

            aiInsightsList.getChildren()
                    .add(
                            createAiInsightEntry(
                                    "Progress",
                                    completed
                                            + " appointment"
                                            + (
                                            completed == 1
                                                    ? ""
                                                    : "s"
                                    )
                                            + " completed today.",
                                    "",
                                    Color.BLUE
                            )
                    );
        }


        if (aiInsightsList.getChildren()
                .isEmpty()) {

            aiInsightsList.getChildren()
                    .add(
                            createAiInsightEntry(
                                    "Schedule Status",
                                    "No appointment alerts are currently available.",
                                    "",
                                    Color.BLUE
                            )
                    );
        }
    }


    // ============================================================
    // AI ENTRY
    // ============================================================

    private VBox createAiInsightEntry(
            String alert,
            String desc,
            String linkText,
            Color accentColor
    ) {

        VBox insight =
                new VBox(8);

        insight.getStyleClass()
                .add(
                        "ai-entry"
                );

        insight.setPadding(
                new Insets(
                        14
                )
        );


        if (accentColor == Color.RED) {

            insight.getStyleClass()
                    .add(
                            "ai-entry-alert"
                    );

        } else {

            insight.getStyleClass()
                    .add(
                            "ai-entry-info"
                    );
        }


        HBox header =
                new HBox(8);

        header.setAlignment(
                Pos.CENTER_LEFT
        );


        String iconPath =
                accentColor == Color.RED
                        ? "/images/icons/ic_alert.png"
                        : "/images/icons/ic_info.png";


        ImageView statusIcon =
                createImageView(
                        iconPath,
                        16,
                        16
                );


        Label alertLabel =
                new Label(
                        alert
                );

        alertLabel.getStyleClass()
                .add(
                        "ai-entry-label"
                );


        if (statusIcon != null) {

            header.getChildren()
                    .add(
                            statusIcon
                    );
        }


        header.getChildren()
                .add(
                        alertLabel
                );


        Label descLabel =
                new Label(
                        desc
                );

        descLabel.getStyleClass()
                .add(
                        "ai-entry-desc"
                );

        descLabel.setWrapText(
                true
        );


        insight.getChildren()
                .addAll(
                        header,
                        descLabel
                );


        if (!linkText.isEmpty()) {

            Label actionLink =
                    new Label(
                            linkText
                    );

            actionLink.getStyleClass()
                    .add(
                            "ai-entry-link"
                    );


            actionLink.setOnMouseClicked(
                    event ->
                            handleSidebarTabClick(
                                    2
                            )
            );


            insight.getChildren()
                    .add(
                            actionLink
                    );
        }


        return insight;
    }


    // ============================================================
    // RECENT ACTIVITY
    // ============================================================

    private VBox createRecentActivityTable() {

        VBox tableContainer =
                new VBox(16);

        tableContainer.getStyleClass()
                .add(
                        "activity-container"
                );

        tableContainer.setPadding(
                new Insets(
                        20
                )
        );


        HBox header =
                new HBox(10);

        header.setAlignment(
                Pos.CENTER_LEFT
        );


        Label title =
                new Label(
                        "Recent Patient Activity"
                );

        title.getStyleClass()
                .add(
                        "activity-title"
                );


        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );


        Label viewAll =
                new Label(
                        "View All Activity"
                );

        viewAll.getStyleClass()
                .add(
                        "activity-link"
                );


        viewAll.setOnMouseClicked(
                e ->
                        handleSidebarTabClick(
                                3
                        )
        );


        header.getChildren()
                .addAll(
                        title,
                        spacer,
                        viewAll
                );


        GridPane tableHeader =
                new GridPane();

        tableHeader.getStyleClass()
                .add(
                        "table-header"
                );

        tableHeader.setHgap(
                16
        );

        tableHeader.setPadding(
                new Insets(
                        0,
                        12,
                        8,
                        12
                )
        );


        String[] cols = {

                "PATIENT",

                "STATUS",

                "ACTION",

                "TIME"
        };


        for (int i = 0;
             i < cols.length;
             i++) {

            Label colLabel =
                    new Label(
                            cols[i]
                    );

            colLabel.getStyleClass()
                    .add(
                            "table-col-header"
                    );


            tableHeader.add(
                    colLabel,
                    i,
                    0
            );
        }


        setupTableColumns(
                tableHeader
        );


        activityList =
                new VBox(8);


        refreshActivityList();


        tableContainer.getChildren()
                .addAll(
                        header,
                        tableHeader,
                        activityList
                );


        return tableContainer;
    }


    // ============================================================
    // REFRESH ACTIVITY
    // ============================================================

    private void refreshActivityList() {

        if (activityList == null) {
            return;
        }


        activityList.getChildren()
                .clear();


        if (doctorAppointments.isEmpty()) {

            Label empty =
                    new Label(
                            "No patient activity available."
                    );

            empty.setStyle(
                    "-fx-text-fill: #64748B;"
                            + "-fx-font-size: 13px;"
            );


            activityList.getChildren()
                    .add(
                            empty
                    );

            return;
        }


        /*
         * Newest appointments first.
         *
         * NO LIMIT.
         */
        List<Appointment> sorted =
                new ArrayList<>(
                        doctorAppointments
                );


        sorted.sort(
                Comparator
                        .comparing(
                                this::getAppointmentDateTimeSafe
                        )
                        .reversed()
        );


        for (Appointment appointment :
                sorted) {

            activityList.getChildren()
                    .add(
                            createActivityEntry(
                                    appointment
                            )
                    );
        }
    }


    // ============================================================
    // ACTIVITY ENTRY
    // ============================================================

    private GridPane createActivityEntry(
            Appointment appointment
    ) {

        GridPane entry =
                new GridPane();

        entry.getStyleClass()
                .add(
                        "table-row"
                );

        entry.setHgap(
                16
        );

        entry.setPadding(
                new Insets(
                        12
                )
        );

        entry.setAlignment(
                Pos.CENTER_LEFT
        );


        String patientName =
                safeText(
                        appointment.getPatientName(),
                        "Unknown Patient"
                );


        String initials =
                getInitials(
                        patientName
                );


        HBox patientCell =
                new HBox(10);

        patientCell.setAlignment(
                Pos.CENTER_LEFT
        );


        Label avatar =
                new Label(
                        initials
                );

        avatar.getStyleClass()
                .add(
                        "table-avatar"
                );


        Label nameLabel =
                new Label(
                        patientName
                );

        nameLabel.getStyleClass()
                .add(
                        "table-patient-name"
                );


        patientCell.getChildren()
                .addAll(
                        avatar,
                        nameLabel
                );


        String status =
                safeText(
                        appointment.getStatus(),
                        "Pending"
                );


        HBox statusCell =
                createPill(
                        status
                );


        Label actionLabel =
                new Label(
                        getActivityAction(
                                status
                        )
                );

        actionLabel.getStyleClass()
                .add(
                        "table-action"
                );


        Label timeLabel =
                new Label(
                        getRelativeTime(
                                appointment
                        )
                );

        timeLabel.getStyleClass()
                .add(
                        "table-time"
                );


        entry.add(
                patientCell,
                0,
                0
        );


        entry.add(
                statusCell,
                1,
                0
        );


        entry.add(
                actionLabel,
                2,
                0
        );


        entry.add(
                timeLabel,
                3,
                0
        );


        setupTableColumns(
                entry
        );


        entry.setOnMouseClicked(
                event ->
                        handlePatientFromAppointment(
                                appointment
                        )
        );


        return entry;
    }


    // ============================================================
    // TABLE COLUMNS
    // ============================================================

    private void setupTableColumns(
            GridPane gridPane
    ) {

        ColumnConstraints col1 =
                new ColumnConstraints(
                        220
                );


        ColumnConstraints col2 =
                new ColumnConstraints(
                        130
                );


        ColumnConstraints col3 =
                new ColumnConstraints();


        col3.setHgrow(
                Priority.ALWAYS
        );


        ColumnConstraints col4 =
                new ColumnConstraints(
                        140
                );


        gridPane.getColumnConstraints()
                .addAll(
                        col1,
                        col2,
                        col3,
                        col4
                );
    }


    // ============================================================
    // STATUS PILL
    // ============================================================

    private HBox createPill(
            String status
    ) {

        HBox pill =
                new HBox();


        pill.setAlignment(
                Pos.CENTER
        );


        pill.getStyleClass()
                .add(
                        "status-pill"
                );


        pill.setPadding(
                new Insets(
                        4,
                        10,
                        4,
                        10
                )
        );


        Label statusLabel =
                new Label(
                        status
                );


        if ("Completed".equalsIgnoreCase(
                status
        )) {

            pill.getStyleClass()
                    .add(
                            "pill-completed"
                    );

            statusLabel.setTextFill(
                    Color.web(
                            "#059669"
                    )
            );


        } else if (
                "In Progress".equalsIgnoreCase(
                        status
                )
                        || "Waiting".equalsIgnoreCase(
                        status
                )
                        || "Pending".equalsIgnoreCase(
                        status
                )
        ) {

            pill.getStyleClass()
                    .add(
                            "pill-inprogress"
                    );

            statusLabel.setTextFill(
                    Color.web(
                            "#2563EB"
                    )
            );


        } else {

            pill.getStyleClass()
                    .add(
                            "pill-info"
                    );

            statusLabel.setTextFill(
                    Color.web(
                            "#6B7280"
                    )
            );
        }


        pill.getChildren()
                .add(
                        statusLabel
                );


        return pill;
    }


    // ============================================================
    // TOTAL PATIENTS
    // ============================================================

    private int getTotalPatientCount() {

        Set<String> patientIds =
                new HashSet<>();


        for (Appointment appointment :
                doctorAppointments) {

            if (appointment == null) {
                continue;
            }


            String patientUid =
                    appointment.getPatientUid();


            if (patientUid != null
                    && !patientUid.isBlank()) {

                patientIds.add(
                        patientUid
                );
            }
        }


        return patientIds.size();
    }


    // ============================================================
    // TODAY APPOINTMENTS
    // ============================================================

    private List<Appointment>
    getTodayAppointments() {

        List<Appointment> result =
                new ArrayList<>();


        String today =
                LocalDate.now()
                        .toString();


        for (Appointment appointment :
                doctorAppointments) {

            if (appointment == null) {
                continue;
            }


            String appointmentDate =
                    appointment.getAppointmentDate();


            if (appointmentDate == null
                    || appointmentDate.isBlank()) {

                continue;
            }


            if (today.equals(
                    appointmentDate.trim()
            )) {

                result.add(
                        appointment
                );
            }
        }


        result.sort(
                Comparator.comparing(
                        this::getAppointmentDateTimeSafe
                )
        );


        return result;
    }


    // ============================================================
    // TODAY APPOINTMENT DETAIL
    // ============================================================

    private String getTodayAppointmentDetail() {

        List<Appointment> today =
                getTodayAppointments();


        long completed =
                today.stream()
                        .filter(
                                appointment ->
                                        appointment != null
                                                && "Completed"
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
                today.size()
                        - completed;


        return completed
                + " completed, "
                + remaining
                + " remaining";
    }


    // ============================================================
    // PROGRESS
    // ============================================================

    private double calculateTodayProgress() {

        List<Appointment> today =
                getTodayAppointments();


        if (today.isEmpty()) {

            return 0;
        }


        long completed =
                today.stream()
                        .filter(
                                appointment ->
                                        appointment != null
                                                && "Completed"
                                                .equalsIgnoreCase(
                                                        safeText(
                                                                appointment
                                                                        .getStatus(),
                                                                ""
                                                        )
                                                )
                        )
                        .count();


        return Math.min(
                1.0,
                (double) completed
                        / today.size()
        );
    }


    // ============================================================
    // PATIENT GROWTH
    // ============================================================

    private int calculatePatientGrowth() {

        LocalDate today =
                LocalDate.now();


        LocalDate previousMonth =
                today.minusMonths(
                        1
                );


        Set<String> currentPatients =
                new HashSet<>();


        Set<String> previousPatients =
                new HashSet<>();


        for (Appointment appointment :
                doctorAppointments) {

            if (appointment == null) {
                continue;
            }


            String patientUid =
                    appointment.getPatientUid();


            String appointmentDate =
                    appointment.getAppointmentDate();


            if (patientUid == null
                    || patientUid.isBlank()
                    || appointmentDate == null
                    || appointmentDate.isBlank()) {

                continue;
            }


            try {

                LocalDate date =
                        LocalDate.parse(
                                appointmentDate
                        );


                if (date.getYear()
                        == today.getYear()
                        && date.getMonth()
                        == today.getMonth()) {

                    currentPatients.add(
                            patientUid
                    );
                }


                if (date.getYear()
                        == previousMonth.getYear()
                        && date.getMonth()
                        == previousMonth.getMonth()) {

                    previousPatients.add(
                            patientUid
                    );
                }


            } catch (DateTimeParseException ignored) {
            }
        }


        return currentPatients.size()
                - previousPatients.size();
    }


    private String getPatientGrowthDetail() {

        int growth =
                calculatePatientGrowth();


        if (growth > 0) {

            return "+"
                    + growth
                    + " patients vs last month";

        }


        if (growth < 0) {

            return growth
                    + " patients vs last month";
        }


        return "No change vs last month";
    }


    // ============================================================
    // WELCOME GREETING
    // ============================================================

    private String getWelcomeGreeting() {

        return "Good "
                + getCurrentDayPart()
                + ", "
                + getDoctorDisplayName()
                + ".";
    }


    // ============================================================
    // WELCOME SUMMARY
    // ============================================================

    private String getWelcomeSummary() {

        List<Appointment> today =
                getTodayAppointments();


        long pending =
                today.stream()
                        .filter(
                                appointment ->
                                        appointment != null
                                                && "Pending"
                                                .equalsIgnoreCase(
                                                        safeText(
                                                                appointment
                                                                        .getStatus(),
                                                                ""
                                                        )
                                                )
                        )
                        .count();


        return "You have "
                + today.size()
                + " appointment"
                + (
                today.size() == 1
                        ? ""
                        : "s"
        )
                + " scheduled for today. "
                + pending
                + " "
                + (
                pending == 1
                        ? "appointment is"
                        : "appointments are"
        )
                + " currently pending.";
    }


    // ============================================================
    // DOCTOR NAME
    // ============================================================

    private String getDoctorDisplayName() {
        return SessionManager.getDoctorDisplayName();
    }


    // ============================================================
    // DAY PART
    // ============================================================

    private String getCurrentDayPart() {

        int hour =
                LocalTime.now()
                        .getHour();


        if (hour < 12) {

            return "morning";
        }


        if (hour < 17) {

            return "afternoon";
        }


        return "evening";
    }


    // ============================================================
    // ACTIVITY ACTION
    // ============================================================

    private String getActivityAction(
            String status
    ) {

        if ("Completed".equalsIgnoreCase(
                status
        )) {

            return "Consultation completed";
        }


        if ("Confirmed".equalsIgnoreCase(
                status
        )) {

            return "Appointment confirmed";
        }


        if ("In Progress".equalsIgnoreCase(
                status
        )) {

            return "Consultation in progress";
        }


        if ("Cancelled".equalsIgnoreCase(
                status
        )) {

            return "Appointment cancelled";
        }


        return "Appointment pending";
    }


    // ============================================================
    // RELATIVE TIME
    // ============================================================

    private String getRelativeTime(
            Appointment appointment
    ) {

        if (appointment == null) {

            return "";
        }


        String date =
                appointment.getAppointmentDate();


        String time =
                appointment.getAppointmentTime();


        if (date == null
                || date.isBlank()) {

            return safeText(
                    time,
                    ""
            );
        }


        try {

            LocalDate appointmentDate =
                    LocalDate.parse(
                            date
                    );


            if (appointmentDate.equals(
                    LocalDate.now()
            )) {

                return "Today "
                        + safeText(
                                time,
                                ""
                        );
            }


            if (appointmentDate.equals(
                    LocalDate.now()
                            .minusDays(
                                    1
                            )
            )) {

                return "Yesterday";
            }


            return appointmentDate.toString();


        } catch (Exception e) {

            return safeText(
                    date,
                    ""
            );
        }
    }


    // ============================================================
    // DATE/TIME SORTING
    // ============================================================

    private LocalDateTime
    getAppointmentDateTimeSafe(
            Appointment appointment
    ) {

        if (appointment == null) {

            return LocalDateTime.MIN;
        }


        LocalDate date =
                LocalDate.MIN;


        LocalTime time =
                LocalTime.MIDNIGHT;


        try {

            String dateText =
                    appointment
                            .getAppointmentDate();


            if (dateText != null
                    && !dateText.isBlank()) {

                date =
                        LocalDate.parse(
                                dateText
                        );
            }

        } catch (Exception ignored) {
        }


        try {

            String timeText =
                    appointment
                            .getAppointmentTime();


            if (timeText != null
                    && !timeText.isBlank()) {

                DateTimeFormatter formatter =
                        DateTimeFormatter
                                .ofPattern(
                                        "h:mm a"
                                );


                time =
                        LocalTime.parse(
                                timeText
                                        .trim()
                                        .toUpperCase(),
                                formatter
                        );
            }

        } catch (Exception ignored) {
        }


        return LocalDateTime.of(
                date,
                time
        );
    }


    // ============================================================
    // INITIALS
    // ============================================================

    private String getInitials(
            String name
    ) {

        if (name == null
                || name.isBlank()) {

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
    // CURRENT DOCTOR UID
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
                    "Unable to get current doctor UID: "
                            + e.getMessage()
            );


            return null;
        }
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
                                new Image(is)
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
}