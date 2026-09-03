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
 * PatientEditProfileView allows doctors to edit patient records.
 * Features full vertical scrolling down to the bottom and sidebar navigation identical to DoctorDashboardView.
 */
public class PatientEditProfileView {

    private final Stage stage;
    private final Scene scene;

    public PatientEditProfileView(Stage stage) {
        this.stage = stage;
        this.scene = createScene();
    }

    public Scene getScene() {
        return this.scene;
    }

    private Scene createScene() {
        BorderPane mainRoot = new BorderPane();
        mainRoot.getStyleClass().add("root-pane");

        // --- Sidebar (Left Navigation matching DashboardView) ---
        VBox sidebar = createSidebar();
        mainRoot.setLeft(sidebar);

        // --- Main Content Area ---
        VBox contentArea = new VBox(20);
        contentArea.setPadding(new Insets(20, 30, 30, 30));
        contentArea.getStyleClass().add("content-area");

        // Top Header
        HBox topHeader = createTopHeader();
        contentArea.getChildren().add(topHeader);

        // Title and Back Button Header
        BorderPane titleSection = createTitleSection();
        contentArea.getChildren().add(titleSection);

        // Edit Form Card
        VBox editFormCard = createEditFormCard();
        contentArea.getChildren().add(editFormCard);

        mainRoot.setCenter(contentArea);

        // --- Outer ScrollPane Container for full-page vertical scrolling ---
        ScrollPane outerScrollPane = new ScrollPane(mainRoot);
        outerScrollPane.setFitToWidth(true);
        outerScrollPane.setFitToHeight(true);
        outerScrollPane.getStyleClass().add("content-scrollpane");

        Scene editProfileScene = new Scene(outerScrollPane, stage.getWidth(), stage.getHeight());

        try {
            editProfileScene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/css/appointments.css")).toExternalForm());
        } catch (Exception ignored) {}

        return editProfileScene;
    }

    private BorderPane createTitleSection() {
        BorderPane section = new BorderPane();

        VBox titleBox = new VBox(2);
        Label mainTitle = new Label("Edit Patient Profile");
        mainTitle.getStyleClass().add("page-title");
        Label subTitle = new Label("Update personal information, medical history, and vitals");
        subTitle.getStyleClass().add("page-subtitle");
        titleBox.getChildren().addAll(mainTitle, subTitle);

        Button backBtn = new Button("← Back to Patient Details");
        backBtn.getStyleClass().add("btn-card-reschedule");
        backBtn.setOnAction(e -> Navigation.goTo(stage, () -> new PatientDetailsView(stage).getScene()));

        section.setLeft(titleBox);
        section.setRight(backBtn);
        return section;
    }

    private VBox createEditFormCard() {
        VBox card = new VBox(20);
        card.getStyleClass().add("filter-container-card");
        card.setPadding(new Insets(24));
        card.setMaxWidth(800);

        GridPane formGrid = new GridPane();
        formGrid.setHgap(20);
        formGrid.setVgap(16);

        // Patient Full Name
        Label nameLbl = new Label("Full Name");
        nameLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        TextField nameField = new TextField("Robert Chen");
        nameField.getStyleClass().add("search-text-field");
        nameField.setPrefHeight(36);

        // Age
        Label ageLbl = new Label("Age");
        ageLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        TextField ageField = new TextField("42");
        ageField.getStyleClass().add("search-text-field");
        ageField.setPrefHeight(36);

        // Gender
        Label genderLbl = new Label("Gender");
        genderLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        ComboBox<String> genderCombo = new ComboBox<>();
        genderCombo.getItems().addAll("Male", "Female", "Other");
        genderCombo.getSelectionModel().select("Male");
        genderCombo.setPrefWidth(300);
        genderCombo.setPrefHeight(36);

        // Blood Group
        Label bloodLbl = new Label("Blood Group");
        bloodLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        ComboBox<String> bloodCombo = new ComboBox<>();
        bloodCombo.getItems().addAll("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
        bloodCombo.getSelectionModel().select("A+");
        bloodCombo.setPrefWidth(300);
        bloodCombo.setPrefHeight(36);

        // Contact Number
        Label phoneLbl = new Label("Phone Number");
        phoneLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        TextField phoneField = new TextField("+1 (555) 019-2834");
        phoneField.getStyleClass().add("search-text-field");
        phoneField.setPrefHeight(36);

        // Email Address
        Label emailLbl = new Label("Email Address");
        emailLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        TextField emailField = new TextField("robert.chen@example.com");
        emailField.getStyleClass().add("search-text-field");
        emailField.setPrefHeight(36);

        // Medical History & Notes
        Label historyLbl = new Label("Medical History & Notes");
        historyLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
        TextArea historyArea = new TextArea("Patient has a history of mild migraine. No known drug allergies reported.");
        historyArea.setPrefRowCount(4);
        historyArea.setWrapText(true);

        // Layout Grid Placement
        formGrid.add(nameLbl, 0, 0);
        formGrid.add(nameField, 0, 1);

        formGrid.add(ageLbl, 1, 0);
        formGrid.add(ageField, 1, 1);

        formGrid.add(genderLbl, 0, 2);
        formGrid.add(genderCombo, 0, 3);

        formGrid.add(bloodLbl, 1, 2);
        formGrid.add(bloodCombo, 1, 3);

        formGrid.add(phoneLbl, 0, 4);
        formGrid.add(phoneField, 0, 5);

        formGrid.add(emailLbl, 1, 4);
        formGrid.add(emailField, 1, 5);

        formGrid.add(historyLbl, 0, 6, 2, 1);
        formGrid.add(historyArea, 0, 7, 2, 1);

        // Bottom Action Buttons
        HBox actionBox = new HBox(12);
        actionBox.setAlignment(Pos.CENTER_RIGHT);
        actionBox.setPadding(new Insets(10, 0, 0, 0));

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("btn-card-reschedule");
        cancelBtn.setOnAction(e -> Navigation.goTo(stage, () -> new PatientDetailsView(stage).getScene()));

        Button saveBtn = new Button("Save Changes");
        saveBtn.getStyleClass().add("btn-primary-action");
        saveBtn.setOnAction(e -> Navigation.goTo(stage, () -> new PatientDetailsView(stage).getScene()));

        actionBox.getChildren().addAll(cancelBtn, saveBtn);

        card.getChildren().addAll(formGrid, actionBox);
        return card;
    }

    private VBox createSidebar() {
        return DoctorSidebar.create(stage, 4);
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