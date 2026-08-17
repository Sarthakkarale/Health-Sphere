package com.healthsphere.view.doctor;

import com.healthsphere.util.Navigation;
import com.healthsphere.util.ResourceImage;

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
 * DoctorEditProfileView handles editing doctor profile information for Health-Sphere.
 */
public class DoctorEditProfileView {

    private final Stage stage;
    private final Scene scene;

    public DoctorEditProfileView(Stage stage) {
        this.stage = stage;
        this.scene = createScene();
    }

    public Scene getScene() {
        return this.scene;
    }

    private Scene createScene() {
        BorderPane mainRoot = new BorderPane();
        mainRoot.getStyleClass().add("root-pane");

        // --- Sidebar (Left Navigation - Fixed & Constant Width & Styling) ---
        VBox sidebar = createSidebar();
        mainRoot.setLeft(sidebar);

        // --- Main Content Area ---
        VBox contentArea = new VBox(20);
        contentArea.setPadding(new Insets(20, 30, 30, 30));
        contentArea.getStyleClass().add("content-area");

        // Header Section
        HBox header = createHeaderSection();
        contentArea.getChildren().add(header);

        // Edit Profile Form Card
        VBox formCard = createFormCard();
        contentArea.getChildren().add(formCard);

        // ScrollPane Container configured for full vertical scrolling
        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.getStyleClass().add("content-scrollpane");

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        mainRoot.setCenter(scrollPane);

        Scene editScene = new Scene(mainRoot, stage.getWidth(), stage.getHeight());
        try {
            editScene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/css/doctor_profile.css")).toExternalForm());
        } catch (Exception ignored) {}

        return editScene;
    }

    /** Creates Sidebar Navigation strictly matching Dashboard dark theme & fixed width */
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPadding(new Insets(25, 15, 25, 15));
        sidebar.getStyleClass().add("sidebar");
        sidebar.setStyle("-fx-background-color: #0F172A;"); // Dark Navy background matching Dashboard
        sidebar.setMinWidth(260);
        sidebar.setPrefWidth(260);
        sidebar.setMaxWidth(260);

        // Logo Section
        HBox logoSection = new HBox(12);
        logoSection.setPadding(new Insets(0, 0, 25, 5));
        logoSection.setAlignment(Pos.CENTER_LEFT);

        StackPane logoIconBox = new StackPane();
        logoIconBox.getStyleClass().add("logo-icon-box");
        logoIconBox.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 8px; -fx-padding: 8px;");
        ImageView logoIcon = new ImageView(ResourceImage.load("/images/icons/ic_shield.png"));
        logoIcon.setFitWidth(20);
        logoIcon.setFitHeight(20);
        logoIconBox.getChildren().add(logoIcon);

        VBox logoText = new VBox(2);
        Label appName = new Label("Health-Sphere");
        appName.getStyleClass().add("logo-name");
        appName.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 16px;");

        Label doctorSubtext = new Label("Doctor Dashboard");
        doctorSubtext.getStyleClass().add("logo-subtext");
        doctorSubtext.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12px;");

        logoText.getChildren().addAll(appName, doctorSubtext);
        logoSection.getChildren().addAll(logoIconBox, logoText);

        // Navigation Tabs
        VBox navItems = new VBox(6);
        String[] tabs = {
            "Dashboard", "Today's Schedule", "Appointments", "Patient Details",
            "Medical Reports & Prescription", "Availability & Schedule", "Doctor Profile", "AI Health Assistant"
        };
        String[] icons = {
            "ic_dashboard", "ic_schedule", "ic_appointments", "ic_patient",
            "ic_reports", "ic_availability", "ic_profile", "ic_ai"
        };

        for (int i = 0; i < tabs.length; i++) {
            HBox navTab = new HBox(12);
            navTab.setAlignment(Pos.CENTER_LEFT);
            navTab.setPadding(new Insets(10, 14, 10, 14));
            navTab.getStyleClass().add("nav-tab");

            ImageView icon = new ImageView(ResourceImage.load("/images/icons/" + icons[i] + ".png"));
            icon.setFitWidth(18);
            icon.setFitHeight(18);

            Label tabLabel = new Label(tabs[i]);
            tabLabel.getStyleClass().add("nav-text");

            if (i == 6) { // Active Highlight: Doctor Profile
                navTab.getStyleClass().add("nav-tab-active");
                navTab.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 8px;");
                tabLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold;");
            } else {
                navTab.setStyle("-fx-background-color: transparent; -fx-background-radius: 8px;");
                tabLabel.setStyle("-fx-text-fill: #94A3B8;");
            }

            navTab.getChildren().addAll(icon, tabLabel);
            navItems.getChildren().add(navTab);

            final int index = i;
            navTab.setOnMouseClicked(e -> handleSidebarTabClick(index));
        }

        // Spacer to push footer to bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Footer Section (Doctor Profile Card & Logout Button)
        VBox footer = new VBox(10);
        footer.setPadding(new Insets(15, 0, 0, 0));

        // Bottom Doctor Profile Box
        HBox sidebarProfile = new HBox(12);
        sidebarProfile.setAlignment(Pos.CENTER_LEFT);
        sidebarProfile.setPadding(new Insets(10, 12, 10, 12));
        sidebarProfile.getStyleClass().add("sidebar-profile-box");
        sidebarProfile.setStyle("-fx-background-color: #1E293B; -fx-background-radius: 10px; -fx-cursor: hand;");

        ImageView profileAvatar = new ImageView(ResourceImage.load("/images/doctor/portrait-3d-male-doctor.png"));
        profileAvatar.setFitWidth(36);
        profileAvatar.setFitHeight(36);
        Circle profileClip = new Circle(18, 18, 18);
        profileAvatar.setClip(profileClip);

        VBox profileTexts = new VBox(2);
        Label profSubText = new Label("Doctor Profile");
        profSubText.setStyle("-fx-text-fill: #64748B; -fx-font-size: 11px;");
        Label profName = new Label("Dr. Sarah");
        profName.getStyleClass().add("sidebar-profile-name");
        profName.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 13px;");

        profileTexts.getChildren().addAll(profSubText, profName);
        sidebarProfile.getChildren().addAll(profileAvatar, profileTexts);
        sidebarProfile.setOnMouseClicked(e -> Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene()));

        // Logout Tab
        HBox logoutTab = new HBox(12);
        logoutTab.setAlignment(Pos.CENTER_LEFT);
        logoutTab.setPadding(new Insets(10, 14, 10, 14));
        logoutTab.getStyleClass().add("nav-tab");
        logoutTab.setStyle("-fx-cursor: hand;");

        ImageView logoutIcon = new ImageView(ResourceImage.load("/images/icons/ic_logout.png"));
        logoutIcon.setFitWidth(18);
        logoutIcon.setFitHeight(18);

        Label logoutLabel = new Label("Logout");
        logoutLabel.getStyleClass().add("nav-text");
        logoutLabel.setStyle("-fx-text-fill: #94A3B8;");

        logoutTab.getChildren().addAll(logoutIcon, logoutLabel);
        logoutTab.setOnMouseClicked(e -> System.out.println("Logging out..."));

        footer.getChildren().addAll(sidebarProfile, logoutTab);

        sidebar.getChildren().addAll(logoSection, navItems, spacer, footer);
        return sidebar;
    }

    private void handleSidebarTabClick(int index) {
        switch (index) {
            case 0:
                Navigation.goTo(stage, () -> new DoctorDashboardView(stage).getScene());
                break;
            case 1:
                Navigation.goTo(stage, () -> new TodaysScheduleView(stage).getScene());
                break;
            case 2:
                Navigation.goTo(stage, () -> new AppointmentsView(stage).getScene());
                break;
            case 3:
                Navigation.goTo(stage, () -> new PatientDetailsView(stage).getScene());
                break;
            case 4:
                Navigation.goTo(stage, () -> new MedicalReportsView(stage).getScene());
                break;
            case 5:
                Navigation.goTo(stage, () -> new AvailabilityScheduleView(stage).getScene());
                break;
            case 6:
                Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene());
                break;
            case 7:
                Navigation.goTo(stage, () -> new AIHealthAssistantView(stage).getScene());
                break;
            default:
                break;
        }
    }

    private HBox createHeaderSection() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Edit Profile");
        title.getStyleClass().add("banner-doc-name");
        Label subtitle = new Label("Update your personal information and professional details.");
        subtitle.getStyleClass().add("banner-subtext");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = new Button("Back to Profile");
        backBtn.getStyleClass().add("btn-secondary-action");
        backBtn.setOnAction(e -> Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene()));

        header.getChildren().addAll(titleBox, spacer, backBtn);
        return header;
    }

    private VBox createFormCard() {
        VBox card = new VBox(20);
        card.getStyleClass().add("panel-card");
        card.setPadding(new Insets(24));

        // Profile Photo Section
        HBox photoSection = new HBox(20);
        photoSection.setAlignment(Pos.CENTER_LEFT);

        ImageView avatar = new ImageView(ResourceImage.load("/images/mocks/dr_julian_large.png"));
        avatar.setFitWidth(90);
        avatar.setFitHeight(90);
        Circle clip = new Circle(45, 45, 45);
        avatar.setClip(clip);

        VBox photoActions = new VBox(8);
        photoActions.setAlignment(Pos.CENTER_LEFT);

        Button changePhotoBtn = new Button("Change Photo");
        changePhotoBtn.getStyleClass().add("btn-secondary-action");

        Label photoHint = new Label("Allowed JPG or PNG. Max size 2MB");
        photoHint.getStyleClass().add("detail-field-label");

        photoActions.getChildren().addAll(changePhotoBtn, photoHint);
        photoSection.getChildren().addAll(avatar, photoActions);

        Separator sep1 = new Separator();

        // Form Fields Grid
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(16);

        TextField nameField = new TextField("Dr. Julian Smith");
        TextField titleField = new TextField("Senior Cardiologist");
        TextField emailField = new TextField("dr.julian@healthsphere.com");
        TextField phoneField = new TextField("+1 (555) 123-4567");
        TextField locationField = new TextField("St. Mary's Hospital, New York");
        TextField languagesField = new TextField("English, Spanish, French");

        grid.add(createFormField("Full Name", nameField), 0, 0);
        grid.add(createFormField("Title / Specialty", titleField), 1, 0);
        grid.add(createFormField("Email Address", emailField), 0, 1);
        grid.add(createFormField("Phone Number", phoneField), 1, 1);
        grid.add(createFormField("Hospital / Location", locationField), 0, 2);
        grid.add(createFormField("Languages Spoken", languagesField), 1, 2);

        // Bio Text Area
        VBox bioBox = new VBox(6);
        Label bioLabel = new Label("Bio");
        bioLabel.getStyleClass().add("detail-field-label");

        TextArea bioArea = new TextArea("Dedicated and compassionate Senior Cardiologist with over 15 years of clinical experience in diagnosing and treating cardiovascular diseases.");
        bioArea.setPrefRowCount(3);
        bioArea.setWrapText(true);

        bioBox.getChildren().addAll(bioLabel, bioArea);

        Separator sep2 = new Separator();

        // Save / Cancel Action Bar
        HBox actionsBox = new HBox(12);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("btn-secondary-action");
        cancelBtn.setOnAction(e -> Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene()));

        Button saveBtn = new Button("Save Changes");
        saveBtn.getStyleClass().add("btn-primary-action");
        saveBtn.setOnAction(e -> {
            // Add persistence logic here if required
            Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene());
        });

        actionsBox.getChildren().addAll(cancelBtn, saveBtn);

        card.getChildren().addAll(photoSection, sep1, grid, bioBox, sep2, actionsBox);
        return card;
    }

    private VBox createFormField(String labelText, Control inputControl) {
        VBox box = new VBox(6);
        Label label = new Label(labelText);
        label.getStyleClass().add("detail-field-label");
        box.getChildren().addAll(label, inputControl);
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }
}