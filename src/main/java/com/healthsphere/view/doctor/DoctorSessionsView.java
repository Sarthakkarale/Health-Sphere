package com.healthsphere.view.doctor;

import com.healthsphere.controller.appointment.AppointmentController;
import com.healthsphere.model.Appointment;
import com.healthsphere.util.Navigation;
import com.healthsphere.util.SessionManager;
import com.healthsphere.config.RoomGenerator;
import com.healthsphere.dao.CallDao;
import com.healthsphere.model.Call;
import com.healthsphere.model.UserModel;
import com.healthsphere.view.common.VideoCallScreen;
import com.healthsphere.util.ShimmerPlaceholder;
import com.healthsphere.view.authentication.LoginView;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * DoctorSessionsView
 *
 * Dedicated Video Consultation Sessions Screen for Doctors.
 * 
 * Features:
 * - Asynchronous multithreaded data loading using JavaFX Task so UI never freezes.
 * - Animated ShimmerPlaceholder skeleton cards during data fetching.
 * - Displays ONLY accepted appointment cards ready for video sessions.
 * - Interactive "Make Session" modal simulating live video call interface.
 */
public class DoctorSessionsView {

    private final Stage stage;
    private final Scene scene;
    private final AppointmentController appointmentController;
    private final String doctorUid;

    private List<Appointment> allAcceptedAppointments = new ArrayList<>();
    private FlowPane cardsGrid;
    private TextField searchField;
    private Label totalAcceptedCountLabel;
    private Label todaySessionsCountLabel;
    private String currentFilter = "ALL"; // ALL, TODAY, UPCOMING

    public DoctorSessionsView(Stage stage) {
        this.stage = stage;
        this.appointmentController = new AppointmentController();
        this.doctorUid = getCurrentDoctorUid();

        // Create UI scene graph immediately to ensure instant non-blocking navigation
        this.scene = createScene();

        // Load data asynchronously using multithreading & Task
        loadAcceptedAppointmentsAsync();
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
            throw new IllegalStateException("Doctor UID is not available in session.");
        }
        return uid;
    }

    /**
     * Asynchronously loads appointments on a background thread using JavaFX Task.
     * Displays shimmer placeholders while loading to keep the UI smooth and responsive.
     */
    private void loadAcceptedAppointmentsAsync() {
        // Show initial shimmer skeleton placeholders
        if (cardsGrid != null) {
            cardsGrid.getChildren().clear();
            for (int i = 0; i < 4; i++) {
                cardsGrid.getChildren().add(ShimmerPlaceholder.createAppointmentCardShimmer());
            }
        }

        Task<List<Appointment>> loadTask = new Task<>() {
            @Override
            protected List<Appointment> call() throws Exception {
                List<Appointment> rawAppointments = appointmentController.getDoctorAppointments(doctorUid);
                List<Appointment> acceptedList = new ArrayList<>();
                if (rawAppointments != null) {
                    for (Appointment apt : rawAppointments) {
                        if (isAcceptedAppointment(apt)) {
                            acceptedList.add(apt);
                        }
                    }
                }
                return acceptedList;
            }
        };

        loadTask.setOnSucceeded(event -> {
            allAcceptedAppointments = loadTask.getValue();
            allAcceptedAppointments.sort(Comparator.comparing(this::getSortableDateTime));
            System.out.println("Doctor Sessions view loaded " + allAcceptedAppointments.size() + " accepted appointments.");
            updateMetricsHeader();
            renderAppointmentCards();
        });

        loadTask.setOnFailed(event -> {
            System.err.println("Failed to load sessions data: " + loadTask.getException());
            allAcceptedAppointments.clear();
            updateMetricsHeader();
            renderAppointmentCards();
        });

        Thread backgroundThread = new Thread(loadTask);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    private boolean isAcceptedAppointment(Appointment apt) {
        if (apt == null || apt.getStatus() == null) return false;
        String status = apt.getStatus().trim().toUpperCase();
        return status.equals("ACCEPTED") || status.equals("CONFIRMED") || status.equals("APPROVED");
    }

    private String getSortableDateTime(Appointment apt) {
        if (apt == null) return "9999-99-99 99:99";
        String date = apt.getAppointmentDate() != null ? apt.getAppointmentDate() : "9999-99-99";
        String time = apt.getAppointmentTime() != null ? apt.getAppointmentTime() : "99:99";
        return date + " " + time;
    }

    private void updateMetricsHeader() {
        if (totalAcceptedCountLabel != null) {
            totalAcceptedCountLabel.setText(String.valueOf(allAcceptedAppointments.size()));
        }
        if (todaySessionsCountLabel != null) {
            String todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            long count = allAcceptedAppointments.stream()
                    .filter(a -> todayStr.equals(a.getAppointmentDate()))
                    .count();
            todaySessionsCountLabel.setText(String.valueOf(count));
        }
    }

    private Scene createScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-pane");

        // Fixed Sidebar navigation
        root.setLeft(createSidebar());

        // Main content area
        VBox mainContent = new VBox(24);
        mainContent.setPadding(new Insets(24, 30, 30, 30));
        mainContent.getStyleClass().add("content-area");

        // Top bar
        mainContent.getChildren().add(createTopBar());

        // Page Header Banner & Stat Cards
        mainContent.getChildren().add(createHeaderSection());

        // Search and Filter Bar
        mainContent.getChildren().add(createFilterBar());

        // Grid of Accepted Appointment Cards
        cardsGrid = new FlowPane(20, 20);
        cardsGrid.setAlignment(Pos.TOP_LEFT);
        cardsGrid.setPadding(new Insets(4));

        ScrollPane scrollPane = new ScrollPane(cardsGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        mainContent.getChildren().add(scrollPane);

        ScrollPane outerScroll = new ScrollPane(mainContent);
        outerScroll.setFitToWidth(true);
        outerScroll.setFitToHeight(true);
        outerScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        outerScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        root.setCenter(outerScroll);

        Scene resultScene = new Scene(root, stage.getWidth(), stage.getHeight());

        try {
            resultScene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/css/appointments.css")).toExternalForm()
            );
        } catch (Exception e) {
            System.err.println("appointments.css not found for DoctorSessionsView.");
        }

        return resultScene;
    }

    private VBox createSidebar() {
        return DoctorSidebar.create(stage, 3);
    }

    private BorderPane createTopBar() {
        BorderPane topBar = new BorderPane();

        HBox breadcrumbBox = new HBox(6);
        breadcrumbBox.setAlignment(Pos.CENTER_LEFT);

        Label rootLabel = new Label("Doctor Dashboard");
        rootLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 13px;");
        Label sep = new Label(" › ");
        sep.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 13px;");
        Label activeLabel = new Label("Sessions");
        activeLabel.setStyle("-fx-text-fill: #2563EB; -fx-font-size: 13px; -fx-font-weight: bold;");

        breadcrumbBox.getChildren().addAll(rootLabel, sep, activeLabel);
        topBar.setLeft(breadcrumbBox);

        Button refreshBtn = new Button("Refresh Sessions");
        refreshBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #334155; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8 16; -fx-background-radius: 8px; -fx-cursor: hand;");
        refreshBtn.setOnAction(e -> loadAcceptedAppointmentsAsync());
        topBar.setRight(refreshBtn);

        return topBar;
    }

    private VBox createHeaderSection() {
        VBox header = new VBox(16);

        VBox titleBox = new VBox(4);
        Label pageTitle = new Label("Video Consultation Sessions");
        pageTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label pageSub = new Label("Launch and manage live video call sessions for your accepted appointments.");
        pageSub.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");
        titleBox.getChildren().addAll(pageTitle, pageSub);

        HBox statCards = new HBox(16);

        VBox card1 = createStatCard("Total Accepted Sessions", totalAcceptedCountLabel = new Label("..."), "#3B82F6", "ic_video.png");
        VBox card2 = createStatCard("Today's Sessions", todaySessionsCountLabel = new Label("..."), "#10B981", "ic_schedule.png");

        statCards.getChildren().addAll(card1, card2);
        header.getChildren().addAll(titleBox, statCards);
        return header;
    }

    private VBox createStatCard(String title, Label valueLabel, String accentColor, String iconName) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setPrefWidth(260);
        card.setStyle(String.format("-fx-background-color: white; -fx-background-radius: 12px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 8, 0, 0, 2);"));

        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBox = new StackPane();
        iconBox.setStyle(String.format("-fx-background-color: %s20; -fx-background-radius: 8px; -fx-padding: 8px;", accentColor));
        ImageView icon = createImageView("/images/icons/" + iconName, 18, 18);
        if (icon != null) iconBox.getChildren().add(icon);

        Label cardTitle = new Label(title);
        cardTitle.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px; -fx-font-weight: bold;");

        topRow.getChildren().addAll(iconBox, cardTitle);

        valueLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        card.getChildren().addAll(topRow, valueLabel);
        return card;
    }

    private HBox createFilterBar() {
        HBox filterBar = new HBox(14);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(12, 16, 12, 16));
        filterBar.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-border-color: #E2E8F0; -fx-border-radius: 10px;");

        // Search Input
        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 20px; -fx-border-color: #E2E8F0; -fx-border-radius: 20px; -fx-padding: 4 14;");
        searchBox.setPrefWidth(280);

        ImageView searchIcon = createImageView("/images/icons/ic_search.png", 16, 16);
        searchField = new TextField();
        searchField.setPromptText("Search patient name or specialty...");
        searchField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-font-size: 12px;");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> renderAppointmentCards());

        if (searchIcon != null) searchBox.getChildren().add(searchIcon);
        searchBox.getChildren().add(searchField);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Filter Pills
        HBox pillsBox = new HBox(8);

        Button btnAll = createFilterPill("All Accepted", "ALL");
        Button btnToday = createFilterPill("Today's Sessions", "TODAY");
        Button btnUpcoming = createFilterPill("Upcoming", "UPCOMING");

        pillsBox.getChildren().addAll(btnAll, btnToday, btnUpcoming);
        filterBar.getChildren().addAll(searchBox, spacer, pillsBox);
        return filterBar;
    }

    private Button createFilterPill(String title, String filterKey) {
        Button pill = new Button(title);
        updatePillStyle(pill, filterKey.equals(currentFilter));

        pill.setOnAction(e -> {
            currentFilter = filterKey;
            // update all sibling pill styles
            HBox parent = (HBox) pill.getParent();
            if (parent != null) {
                parent.getChildren().forEach(node -> {
                    if (node instanceof Button) {
                        Button b = (Button) node;
                        updatePillStyle(b, b == pill);
                    }
                });
            }
            renderAppointmentCards();
        });
        return pill;
    }

    private void updatePillStyle(Button pill, boolean active) {
        if (active) {
            pill.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 20px; -fx-padding: 6 16; -fx-cursor: hand;");
        } else {
            pill.setStyle("-fx-background-color: transparent; -fx-border-color: #CBD5E1; -fx-border-radius: 20px; -fx-background-radius: 20px; -fx-text-fill: #64748B; -fx-font-size: 12px; -fx-padding: 6 16; -fx-cursor: hand;");
        }
    }

    /**
     * Renders filtered accepted appointment cards into cardsGrid.
     */
    private void renderAppointmentCards() {
        if (cardsGrid == null) return;
        cardsGrid.getChildren().clear();

        String query = searchField != null ? searchField.getText().trim().toLowerCase() : "";
        String todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<Appointment> filtered = allAcceptedAppointments.stream()
                .filter(apt -> {
                    // Filter by search query
                    if (!query.isEmpty()) {
                        String name = apt.getPatientName() != null ? apt.getPatientName().toLowerCase() : "";
                        String spec = apt.getSpecialty() != null ? apt.getSpecialty().toLowerCase() : "";
                        if (!name.contains(query) && !spec.contains(query)) {
                            return false;
                        }
                    }
                    // Filter by tab pill
                    if ("TODAY".equals(currentFilter)) {
                        return todayStr.equals(apt.getAppointmentDate());
                    } else if ("UPCOMING".equals(currentFilter)) {
                        String aptDate = apt.getAppointmentDate();
                        return aptDate != null && aptDate.compareTo(todayStr) >= 0;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            VBox emptyBox = new VBox(12);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(40));
            emptyBox.setPrefWidth(700);

            ImageView emptyIcon = createImageView("/images/icons/ic_video.png", 48, 48);
            Label emptyTitle = new Label("No Accepted Video Sessions Found");
            emptyTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #475569;");
            Label emptySub = new Label("Appointments accepted by you will automatically appear here ready to launch video calls.");
            emptySub.setStyle("-fx-font-size: 13px; -fx-text-fill: #94A3B8;");

            if (emptyIcon != null) emptyBox.getChildren().add(emptyIcon);
            emptyBox.getChildren().addAll(emptyTitle, emptySub);
            cardsGrid.getChildren().add(emptyBox);
            return;
        }

        for (Appointment apt : filtered) {
            cardsGrid.getChildren().add(createAcceptedAppointmentCard(apt));
        }
    }

    private VBox createAcceptedAppointmentCard(Appointment apt) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18));
        card.setPrefWidth(320);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.04), 10, 0, 0, 3);");

        // Header: Avatar, Name, ID badge
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        StackPane avatarBox = new StackPane();
        avatarBox.setStyle("-fx-background-color: #DBEAFE; -fx-background-radius: 22px; -fx-min-width: 44px; -fx-min-height: 44px;");
        Label initials = new Label(getInitials(apt.getPatientName()));
        initials.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1D4ED8;");
        avatarBox.getChildren().add(initials);

        VBox nameBox = new VBox(3);
        Label nameLabel = new Label(apt.getPatientName() != null ? apt.getPatientName() : "Patient");
        nameLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Label idLabel = new Label("ID: " + (apt.getAppointmentId() != null ? apt.getAppointmentId() : "N/A"));
        idLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B; -fx-background-color: #F1F5F9; -fx-padding: 2 6; -fx-background-radius: 4px;");

        nameBox.getChildren().addAll(nameLabel, idLabel);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        header.getChildren().addAll(avatarBox, nameBox, headerSpacer);

        // Information Section: Date, Time, Specialty, Reason
        VBox infoBox = new VBox(8);
        infoBox.setPadding(new Insets(10));
        infoBox.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 8px;");

        HBox dateTimeRow = new HBox(12);
        dateTimeRow.setAlignment(Pos.CENTER_LEFT);
        ImageView calIcon = createImageView("/images/icons/ic_calendar.png", 14, 14);
        Label dateLabel = new Label((apt.getAppointmentDate() != null ? apt.getAppointmentDate() : "Date N/A") + "  •  " + (apt.getAppointmentTime() != null ? apt.getAppointmentTime() : "Time N/A"));
        dateLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #334155;");
        if (calIcon != null) dateTimeRow.getChildren().add(calIcon);
        dateTimeRow.getChildren().add(dateLabel);

        HBox specRow = new HBox(12);
        specRow.setAlignment(Pos.CENTER_LEFT);
        Label specLabel = new Label("Specialty: " + (apt.getSpecialty() != null ? apt.getSpecialty() : "General"));
        specLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        specRow.getChildren().add(specLabel);

        if (apt.getReason() != null && !apt.getReason().isBlank()) {
            Label reasonLabel = new Label("Reason: " + apt.getReason());
            reasonLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #94A3B8; -fx-wrap-text: true;");
            infoBox.getChildren().addAll(dateTimeRow, specRow, reasonLabel);
        } else {
            infoBox.getChildren().addAll(dateTimeRow, specRow);
        }

        // Action Button: "Make Session"
        Button makeSessionBtn = new Button("Make Session");
        makeSessionBtn.setMaxWidth(Double.MAX_VALUE);
        makeSessionBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 10; -fx-background-radius: 8px; -fx-cursor: hand;");

        ImageView videoIcon = createImageView("/images/icons/ic_video_white.png", 16, 16);
        if (videoIcon == null) videoIcon = createImageView("/images/icons/ic_video.png", 16, 16);
        if (videoIcon != null) makeSessionBtn.setGraphic(videoIcon);

        makeSessionBtn.setOnAction(e -> openVideoCallModal(apt));

        card.getChildren().addAll(header, infoBox, makeSessionBtn);
        return card;
    }

    /**
     * Opens an interactive Video Call Room Modal for starting a session with the patient.
     */
    private void openVideoCallModal(Appointment apt) {
        String callerEmail = UserModel.getInstance().getEmail();
        String callerName = SessionManager.getDoctorDisplayName();

        String receiverEmail = apt.getPatientUid() != null ? apt.getPatientUid() : "patient@healthsphere.com";
        String receiverName = apt.getPatientName() != null ? apt.getPatientName() : "Patient";

        String roomId = RoomGenerator.generateRoomId(callerEmail, receiverEmail);
        CallDao callDao = new CallDao();

        String callId = callDao.startCall(callerEmail, callerName, receiverEmail, receiverName, roomId);

        Call call = new Call(callerEmail, callerName, receiverEmail, receiverName, roomId, "CALLING");
        call.setCallId(callId);

        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.initOwner(stage);
        modalStage.setTitle("Doctor Live Video Call - " + receiverName);

        VideoCallScreen videoCallScreen = new VideoCallScreen(call, modalStage::close);
        Scene modalScene = new Scene(videoCallScreen, 680, 520);
        modalStage.setScene(modalScene);
        modalStage.show();
    }

    private String getInitials(String name) {
        if (name == null || name.isBlank()) return "P";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    private ImageView createImageView(String path, double width, double height) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) {
                ImageView iv = new ImageView(new Image(is));
                iv.setFitWidth(width);
                iv.setFitHeight(height);
                iv.setPreserveRatio(true);
                return iv;
            }
        } catch (Exception e) {
            // Ignore missing icon grace
        }
        return null;
    }
}
