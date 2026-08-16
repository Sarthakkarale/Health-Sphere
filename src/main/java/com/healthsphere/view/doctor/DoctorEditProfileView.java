package com.healthsphere.view.doctor;

import com.healthsphere.util.Navigation;
import com.healthsphere.util.ResourceImage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
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

        // --- Sidebar Navigation ---
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
        editScene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/css/doctor_profile.css")).toExternalForm());

        return editScene;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPadding(new Insets(25, 15, 25, 15));
        sidebar.getStyleClass().add("sidebar");
        sidebar.setMinWidth(240);

        HBox logoSection = new HBox(10);
        logoSection.setPadding(new Insets(0, 0, 25, 0));
        logoSection.setAlignment(Pos.CENTER_LEFT);

        StackPane logoIconBox = new StackPane();
        logoIconBox.getStyleClass().add("logo-icon-box");
        ImageView logoIcon = new ImageView(ResourceImage.load("/images/icons/ic_shield.png"));
        logoIcon.setFitWidth(18); logoIcon.setFitHeight(18);
        logoIconBox.getChildren().add(logoIcon);

        VBox logoText = new VBox(0);
        Label appName = new Label("Health-Sphere");
        appName.getStyleClass().add("logo-name");
        Label doctorSubtext = new Label("Doctor Module");
        doctorSubtext.getStyleClass().add("logo-subtext");
        logoText.getChildren().addAll(appName, doctorSubtext);
        logoSection.getChildren().addAll(logoIconBox, logoText);

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
            navTab.getStyleClass().add("nav-tab");

            if (i == 6) {
                navTab.getStyleClass().add("nav-tab-active");
            }

            ImageView icon = new ImageView(ResourceImage.load("/images/icons/" + icons[i] + ".png"));
            icon.setFitWidth(18); icon.setFitHeight(18);
            Label tabLabel = new Label(tabs[i]);
            tabLabel.getStyleClass().add("nav-text");

            navTab.getChildren().addAll(icon, tabLabel);
            navItems.getChildren().add(navTab);

            final int index = i;
            navTab.setOnMouseClicked(e -> handleSidebarTabClick(index));
        }

        sidebar.getChildren().addAll(logoSection, navItems);
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
        avatar.setFitWidth(90); avatar.setFitHeight(90);
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