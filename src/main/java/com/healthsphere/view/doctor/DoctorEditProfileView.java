package com.healthsphere.view.doctor;

import com.healthsphere.dao.authentication.DoctorDAO;
import com.healthsphere.model.DoctorProfile;
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
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * DoctorEditProfileView handles editing doctor profile information for Health-Sphere.
 */
public class DoctorEditProfileView {

    private final Stage stage;
    private final Scene scene;
    private final DoctorDAO doctorDAO;
    private DoctorProfile doctorProfile;

    public DoctorEditProfileView(Stage stage) {
        this.stage = stage;
        this.doctorDAO = new DoctorDAO();
        loadCurrentProfile();
        this.scene = createScene();
    }

    private void loadCurrentProfile() {
        String docUid = SessionManager.getDoctorUid();
        if (docUid != null && !docUid.isBlank()) {
            try {
                this.doctorProfile = doctorDAO.getDoctorProfile(docUid);
            } catch (Exception e) {
                System.err.println("Error loading doctor profile for editing: " + e.getMessage());
            }
        }
        if (this.doctorProfile == null) {
            this.doctorProfile = new DoctorProfile();
            if (docUid != null) {
                this.doctorProfile.setUid(docUid);
            }
        }
    }

    public Scene getScene() {
        return this.scene;
    }

    private Scene createScene() {
        BorderPane mainRoot = new BorderPane();
        mainRoot.getStyleClass().add("root-pane");

        // --- Sidebar (Left Navigation) ---
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

        // ScrollPane Container
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

    private VBox createSidebar() {
        return DoctorSidebar.create(stage, 7);
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

        ImageView avatar = new ImageView(ResourceImage.load("/images/doctor/doctor_profile.png"));
        if (avatar.getImage() == null) {
            avatar = new ImageView(ResourceImage.load("/images/doctor/portrait-3d-male-doctor.png"));
        }
        avatar.setFitWidth(90);
        avatar.setFitHeight(90);
        Circle clip = new Circle(45, 45, 45);
        avatar.setClip(clip);

        VBox photoActions = new VBox(8);
        photoActions.setAlignment(Pos.CENTER_LEFT);

        Label photoHint = new Label("Doctor Profile Photo");
        photoHint.getStyleClass().add("detail-field-label");

        photoActions.getChildren().add(photoHint);
        photoSection.getChildren().addAll(avatar, photoActions);

        Separator sep1 = new Separator();

        // Populate fields from current doctor profile
        String existingFirstName = doctorProfile.getFirstName() != null ? doctorProfile.getFirstName().trim() : "";
        String existingLastName = doctorProfile.getLastName() != null ? doctorProfile.getLastName().trim() : "";
        String fullInitialName = (existingFirstName + " " + existingLastName).trim();
        if (fullInitialName.isBlank()) {
            fullInitialName = SessionManager.getDoctorDisplayName();
            if (fullInitialName.startsWith("Dr.")) {
                fullInitialName = fullInitialName.substring(3).trim();
            }
        }

        TextField nameField = new TextField(fullInitialName);
        TextField titleField = new TextField(doctorProfile.getSpecialization() != null ? doctorProfile.getSpecialization() : "");
        TextField emailField = new TextField(doctorProfile.getEmail() != null ? doctorProfile.getEmail() : "");
        TextField phoneField = new TextField(doctorProfile.getPhone() != null ? doctorProfile.getPhone() : "");
        TextField locationField = new TextField(doctorProfile.getHospitalAffiliation() != null ? doctorProfile.getHospitalAffiliation() : "");
        TextField experienceField = new TextField(doctorProfile.getExperience() != null ? doctorProfile.getExperience() : "");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(16);

        grid.add(createFormField("Full Name *", nameField), 0, 0);
        grid.add(createFormField("Specialty / Title *", titleField), 1, 0);
        grid.add(createFormField("Email Address *", emailField), 0, 1);
        grid.add(createFormField("Phone Number", phoneField), 1, 1);
        grid.add(createFormField("Hospital / Location", locationField), 0, 2);
        grid.add(createFormField("Years of Experience", experienceField), 1, 2);

        // Validation / status message label
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        statusLabel.setVisible(false);

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
            String rawName = nameField.getText() != null ? nameField.getText().trim() : "";
            String rawSpec = titleField.getText() != null ? titleField.getText().trim() : "";
            String rawEmail = emailField.getText() != null ? emailField.getText().trim() : "";
            String rawPhone = phoneField.getText() != null ? phoneField.getText().trim() : "";
            String rawHospital = locationField.getText() != null ? locationField.getText().trim() : "";
            String rawExp = experienceField.getText() != null ? experienceField.getText().trim() : "";

            if (rawName.isBlank()) {
                statusLabel.setStyle("-fx-text-fill: #dc2626;");
                statusLabel.setText("Doctor Name cannot be empty.");
                statusLabel.setVisible(true);
                return;
            }

            if (rawSpec.isBlank()) {
                statusLabel.setStyle("-fx-text-fill: #dc2626;");
                statusLabel.setText("Specialization cannot be empty.");
                statusLabel.setVisible(true);
                return;
            }

            // Strip "Dr." prefix if user typed it into name field
            String cleanName = rawName;
            if (cleanName.toLowerCase().startsWith("dr.")) {
                cleanName = cleanName.substring(3).trim();
            } else if (cleanName.toLowerCase().startsWith("dr ")) {
                cleanName = cleanName.substring(3).trim();
            }

            String firstName = cleanName;
            String lastName = "";
            int spaceIdx = cleanName.indexOf(' ');
            if (spaceIdx > 0) {
                firstName = cleanName.substring(0, spaceIdx).trim();
                lastName = cleanName.substring(spaceIdx + 1).trim();
            }

            String docUid = SessionManager.getDoctorUid();
            if (docUid == null || docUid.isBlank()) {
                statusLabel.setStyle("-fx-text-fill: #dc2626;");
                statusLabel.setText("Session expired. Please log in again.");
                statusLabel.setVisible(true);
                return;
            }

            doctorProfile.setUid(docUid);
            doctorProfile.setFirstName(firstName);
            doctorProfile.setLastName(lastName);
            doctorProfile.setSpecialization(rawSpec);
            doctorProfile.setEmail(rawEmail);
            doctorProfile.setPhone(rawPhone);
            doctorProfile.setHospitalAffiliation(rawHospital);
            doctorProfile.setExperience(rawExp);

            try {
                doctorDAO.updateDoctorProfile(doctorProfile);
                SessionManager.setDoctorProfile(doctorProfile);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Profile Updated");
                alert.setHeaderText(null);
                alert.setContentText("Profile updated successfully.");
                alert.showAndWait();

                Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene());

            } catch (Exception ex) {
                ex.printStackTrace();
                statusLabel.setStyle("-fx-text-fill: #dc2626;");
                statusLabel.setText("Failed to update profile: " + ex.getMessage());
                statusLabel.setVisible(true);
            }
        });

        actionsBox.getChildren().addAll(cancelBtn, saveBtn);

        card.getChildren().addAll(photoSection, sep1, grid, statusLabel, sep2, actionsBox);
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