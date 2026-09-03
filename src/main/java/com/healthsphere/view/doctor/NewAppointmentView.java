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
        return DoctorSidebar.create(stage, 2);
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
            case 3: Navigation.goTo(stage, () -> new DoctorSessionsView(stage).getScene()); break;
            case 4: Navigation.goTo(stage, () -> new PatientDetailsView(stage).getScene()); break;
            case 5: Navigation.goTo(stage, () -> new MedicalReportsView(stage).getScene()); break;
            case 6: Navigation.goTo(stage, () -> new AvailabilityScheduleView(stage).getScene()); break;
            case 7: Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene()); break;
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