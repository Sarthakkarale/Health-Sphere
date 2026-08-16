package com.healthsphere.view.Patient;

import com.healthsphere.controller.patient.PatientController;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.PatientProfile;
import com.healthsphere.util.SessionManager;
import com.healthsphere.view.authentication.LoginView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProfileSettings {

    private final Stage stage;
    private final PatientController patientController;

    public ProfileSettings(Stage stage) {
        this.stage = stage;
        this.patientController = new PatientController();
    }

    // =========================================================
    // MAIN SCENE
    // =========================================================

    public Scene getScene() {

        // =====================================================
        // LOAD CURRENT PATIENT PROFILE
        // =====================================================

        PatientProfile patientProfile;

        try {

            patientProfile =
                    patientController
                            .getCurrentPatientProfile();

        } catch (IllegalStateException e) {

            return createErrorScene(
                    "Your session has expired. Please login again."
            );

        } catch (DatabaseException e) {

            e.printStackTrace();

            return createErrorScene(
                    "Unable to load your patient profile."
            );

        } catch (Exception e) {

            e.printStackTrace();

            return createErrorScene(
                    "An unexpected error occurred while loading your profile."
            );
        }

        // =====================================================
        // MAIN CONTENT
        // =====================================================

        VBox content =
                new VBox(22);

        content.setPadding(
                new Insets(5)
        );

        // =====================================================
        // PROFILE IMAGE
        // =====================================================

        VBox profileBanner =
                new VBox();

        profileBanner.setAlignment(
                Pos.CENTER
        );

        profileBanner.setPadding(
                new Insets(20)
        );

        profileBanner.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: #ddd6fe;" +
                "-fx-border-radius: 18;"
        );

        ImageView banner =
                createSquareImage(
                        "/images/profile/profile1.jpg",
                        300
                );

        profileBanner.getChildren().add(
                banner
        );

        // =====================================================
        // PATIENT NAME
        // =====================================================

        String fullName =
                buildFullName(
                        patientProfile.getFirstName(),
                        patientProfile.getLastName()
                );

        // =====================================================
        // PROFILE CARD
        // =====================================================

        HBox profileCard =
                new HBox(20);

        profileCard.setPadding(
                new Insets(20)
        );

        profileCard.setAlignment(
                Pos.CENTER_LEFT
        );

        profileCard.setStyle(
                "-fx-background-color: #f5f3ff;" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: #ddd6fe;" +
                "-fx-border-radius: 16;"
        );

        ImageView profileImage =
                createImage(
                        "/images/profile/profile2.jpg",
                        120,
                        120
                );

        VBox profileInformation =
                new VBox(7);

        Label name =
                new Label(
                        fullName.isBlank()
                                ? "Patient"
                                : fullName
                );

        name.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #4c1d95;"
        );

        String patientUid =
                patientProfile.getUid();

        Label patientId =
                new Label(
                        "Patient ID: "
                                +
                                (
                                        patientUid == null
                                                ? "N/A"
                                                : patientUid
                                )
                );

        patientId.setStyle(
                "-fx-text-fill: #64748b;"
        );

        Label status =
                new Label(
                        "● Active Patient"
                );

        status.setStyle(
                "-fx-text-fill: #16a34a;" +
                "-fx-font-weight: bold;"
        );

        profileInformation.getChildren().addAll(
                name,
                patientId,
                status
        );

        profileCard.getChildren().addAll(
                profileImage,
                profileInformation
        );

        // =====================================================
        // PERSONAL INFORMATION
        // =====================================================

        VBox personal =
                card(
                        "👤  Personal Information"
                );

        GridPane personalGrid =
                new GridPane();

        personalGrid.setHgap(
                15
        );

        personalGrid.setVgap(
                12
        );

        // =====================================================
        // FORM FIELDS
        // =====================================================

        TextField nameField =
                field(
                        fullName
                );

        TextField emailField =
                field(
                        safeValue(
                                patientProfile.getEmail()
                        )
                );

        TextField phoneField =
                field(
                        safeValue(
                                patientProfile.getPhone()
                        )
                );

        TextField cityField =
                field(
                        safeValue(
                                patientProfile.getAddress()
                        )
                );

        // =====================================================
        // NAME
        // =====================================================

        personalGrid.add(
                label("Full Name"),
                0,
                0
        );

        personalGrid.add(
                nameField,
                1,
                0
        );

        // =====================================================
        // EMAIL
        // =====================================================

        personalGrid.add(
                label("Email"),
                0,
                1
        );

        personalGrid.add(
                emailField,
                1,
                1
        );

        // =====================================================
        // PHONE
        // =====================================================

        personalGrid.add(
                label("Phone"),
                0,
                2
        );

        personalGrid.add(
                phoneField,
                1,
                2
        );

        // =====================================================
        // ADDRESS / CITY
        // =====================================================

        personalGrid.add(
                label("City"),
                0,
                3
        );

        personalGrid.add(
                cityField,
                1,
                3
        );

        personal.getChildren().add(
                personalGrid
        );

        // =====================================================
        // HEALTH & WELLNESS
        // =====================================================

        VBox imageCards =
                card(
                        "💜  Health & Wellness"
                );

        HBox images =
                new HBox(18);

        images.setAlignment(
                Pos.CENTER_LEFT
        );

        images.getChildren().addAll(

                imageCard(
                        "/images/profile/profile3.jpg",
                        "Healthy Lifestyle"
                ),

                imageCard(
                        "/images/profile/profile4.jpg",
                        "Personal Health"
                )
        );

        imageCards.getChildren().add(
                images
        );

        // =====================================================
        // ACCOUNT PREFERENCES
        // =====================================================

        VBox preferences =
                card(
                        "⚙  Account Preferences"
                );

        /*
         * Notifications:
         * Clicking Manage opens Notifications.java
         */
        preferences.getChildren().add(
                setting(
                        "Notifications",
                        "Receive appointment and health reminders.",
                        () -> stage.setScene(
                                new Notifications(stage)
                                        .getScene()
                        )
                )
        );

        /*
         * Health Insights:
         * Clicking Manage opens HealthPassport.java
         */
        preferences.getChildren().add(
                setting(
                        "Health Insights",
                        "Allow AI-generated health insights.",
                        () -> stage.setScene(
                                new HealthPassport(stage)
                                        .getScene()
                        )
                )
        );

        /*
         * Privacy option intentionally removed.
         */

        // =====================================================
        // SAVE BUTTON
        // =====================================================

        Button save =
                new Button(
                        "Save Changes"
                );

        save.setStyle(
                "-fx-background-color: #7c3aed;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 11 22;" +
                "-fx-cursor: hand;"
        );

        save.setOnAction(e -> {

            try {

                PatientProfile updatedProfile =
                        patientController
                                .updateCurrentPatientProfile(
                                        nameField.getText(),
                                        emailField.getText(),
                                        phoneField.getText(),
                                        cityField.getText()
                                );

                // -------------------------------------------------
                // Update the profile card immediately
                // -------------------------------------------------

                String updatedName =
                        buildFullName(
                                updatedProfile.getFirstName(),
                                updatedProfile.getLastName()
                        );

                name.setText(
                        updatedName.isBlank()
                                ? "Patient"
                                : updatedName
                );

                patientId.setText(
                        "Patient ID: "
                                +
                                safeValue(
                                        updatedProfile.getUid()
                                )
                );

                // -------------------------------------------------
                // Update form fields
                // -------------------------------------------------

                nameField.setText(
                        updatedName
                );

                emailField.setText(
                        safeValue(
                                updatedProfile.getEmail()
                        )
                );

                phoneField.setText(
                        safeValue(
                                updatedProfile.getPhone()
                        )
                );

                cityField.setText(
                        safeValue(
                                updatedProfile.getAddress()
                        )
                );

                showSuccessMessage(
                        "Profile updated successfully."
                );

            } catch (IllegalArgumentException ex) {

                showErrorMessage(
                        ex.getMessage()
                );

            } catch (DatabaseException ex) {

                ex.printStackTrace();

                showErrorMessage(
                        "Unable to update your profile. "
                                + "Please try again."
                );

            } catch (IllegalStateException ex) {

                showErrorMessage(
                        "Your session has expired. "
                                + "Please login again."
                );

            } catch (Exception ex) {

                ex.printStackTrace();

                showErrorMessage(
                        "An unexpected error occurred "
                                + "while updating your profile."
                );
            }
        });

        // =====================================================
        // BACK BUTTON
        // =====================================================

        Button back =
                new Button(
                        "← Back to Dashboard"
                );

        back.setStyle(
                "-fx-background-color: #0f172a;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10 18;" +
                "-fx-cursor: hand;"
        );

        back.setOnAction(
                e -> showDashboard()
        );

        // =====================================================
        // LOGOUT BUTTON
        // =====================================================

        Button logout =
                new Button(
                        "Logout"
                );

        logout.setStyle(
                "-fx-background-color: #dc2626;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10 22;" +
                "-fx-cursor: hand;"
        );

        logout.setOnAction(e -> {

            SessionManager.clearSession();

            showLogin();
        });

        // =====================================================
        // BUTTON ROW
        // =====================================================

        HBox buttons =
                new HBox(12);

        buttons.setAlignment(
                Pos.CENTER_LEFT
        );

        buttons.getChildren().addAll(
                save,
                back,
                logout
        );

        // =====================================================
        // ADD EVERYTHING TO CONTENT
        // =====================================================

        content.getChildren().addAll(

                profileBanner,

                profileCard,

                personal,

                imageCards,

                preferences,

                buttons
        );

        // =====================================================
        // SCROLL
        // =====================================================

        ScrollPane scroll =
                new ScrollPane(
                        content
                );

        scroll.setFitToWidth(
                true
        );

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scroll.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scroll.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;"
        );

        // =====================================================
        // WRAPPER
        // =====================================================

        VBox wrapper =
                new VBox(
                        scroll
                );

        VBox.setVgrow(
                scroll,
                Priority.ALWAYS
        );

        // =====================================================
        // RETURN SHARED PATIENT SCENE
        // =====================================================

        return PatientUI.createScene(
                stage,
                "Profile & Settings",
                "Profile & Settings",
                "Manage your personal information, preferences and account settings.",
                wrapper
        );
    }

    // =========================================================
    // BUILD FULL NAME
    // =========================================================

    private String buildFullName(
            String firstName,
            String lastName
    ) {

        String first =
                firstName == null
                        ? ""
                        : firstName.trim();

        String last =
                lastName == null
                        ? ""
                        : lastName.trim();

        return (first + " " + last).trim();
    }

    // =========================================================
    // SAFE STRING VALUE
    // =========================================================

    private String safeValue(
            String value
    ) {

        return value == null
                ? ""
                : value;
    }

    // =========================================================
    // SUCCESS MESSAGE
    // =========================================================

    private void showSuccessMessage(
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(
                "HealthSphere"
        );

        alert.setHeaderText(
                "Success"
        );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }

    // =========================================================
    // ERROR MESSAGE
    // =========================================================

    private void showErrorMessage(
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle(
                "HealthSphere"
        );

        alert.setHeaderText(
                "Unable to complete operation"
        );

        alert.setContentText(
                message == null || message.isBlank()
                        ? "An unexpected error occurred."
                        : message
        );

        alert.showAndWait();
    }

    // =========================================================
    // ERROR SCENE
    // =========================================================

    private Scene createErrorScene(
            String message
    ) {

        VBox root =
                new VBox(20);

        root.setAlignment(
                Pos.CENTER
        );

        root.setPadding(
                new Insets(30)
        );

        Label error =
                new Label(
                        message
                );

        error.setWrapText(
                true
        );

        error.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-text-fill: #dc2626;"
        );

        Button back =
                new Button(
                        "← Back to Dashboard"
                );

        back.setStyle(
                "-fx-background-color: #0f172a;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10 18;" +
                "-fx-cursor: hand;"
        );

        back.setOnAction(
                e -> showDashboard()
        );

        root.getChildren().addAll(
                error,
                back
        );

        return new Scene(
                root,
                1440,
                900
        );
    }

    // =========================================================
    // REUSABLE CARD
    // =========================================================

    private VBox card(
            String title
    ) {

        VBox box =
                new VBox(15);

        box.setPadding(
                new Insets(20)
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: #ddd6fe;" +
                "-fx-border-radius: 16;"
        );

        Label heading =
                new Label(
                        title
                );

        heading.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #4c1d95;"
        );

        box.getChildren().add(
                heading
        );

        return box;
    }

    // =========================================================
    // FORM LABEL
    // =========================================================

    private Label label(
            String text
    ) {

        Label label =
                new Label(
                        text
                );

        label.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #475569;"
        );

        return label;
    }

    // =========================================================
    // TEXT FIELD
    // =========================================================

    private TextField field(
            String text
    ) {

        TextField field =
                new TextField(
                        text
                );

        field.setPrefWidth(
                350
        );

        field.setStyle(
                "-fx-background-color: #f8fafc;" +
                "-fx-border-color: #c4b5fd;" +
                "-fx-border-radius: 7;" +
                "-fx-background-radius: 7;" +
                "-fx-padding: 9;"
        );

        return field;
    }

    // =========================================================
    // HEALTH & WELLNESS IMAGE CARD
    // =========================================================

    private VBox imageCard(
            String path,
            String title
    ) {

        VBox box =
                new VBox();

        box.setPrefWidth(
                360
        );

        box.setMaxWidth(
                Double.MAX_VALUE
        );

        box.setCursor(
                Cursor.HAND
        );

        box.setStyle(
                "-fx-background-color: #faf5ff;" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: #ddd6fe;" +
                "-fx-border-radius: 16;" +
                "-fx-effect: dropshadow(gaussian, rgba(91,33,182,0.10), 12, 0, 0, 4);"
        );

        ImageView image =
                createImage(
                        path,
                        360,
                        150
                );

        Label titleLabel =
                new Label(
                        title
                );

        titleLabel.setMaxWidth(
                Double.MAX_VALUE
        );

        titleLabel.setAlignment(
                Pos.CENTER
        );

        titleLabel.setPadding(
                new Insets(14)
        );

        titleLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #5b21b6;"
        );

        box.getChildren().addAll(
                image,
                titleLabel
        );

        // =====================================================
        // HOVER EFFECT
        // =====================================================

        box.setOnMouseEntered(e -> {

            box.setStyle(
                    "-fx-background-color: #f3e8ff;" +
                    "-fx-background-radius: 16;" +
                    "-fx-border-color: #8b5cf6;" +
                    "-fx-border-radius: 16;" +
                    "-fx-effect: dropshadow(gaussian, rgba(91,33,182,0.25), 18, 0, 0, 6);"
            );
        });

        box.setOnMouseExited(e -> {

            box.setStyle(
                    "-fx-background-color: #faf5ff;" +
                    "-fx-background-radius: 16;" +
                    "-fx-border-color: #ddd6fe;" +
                    "-fx-border-radius: 16;" +
                    "-fx-effect: dropshadow(gaussian, rgba(91,33,182,0.10), 12, 0, 0, 4);"
            );
        });

        // =====================================================
        // NAVIGATION
        // =====================================================

        if ("Healthy Lifestyle".equals(title)) {

            box.setOnMouseClicked(
                    e -> stage.setScene(
                            new HealthyLifestyle(stage)
                                    .getScene()
                    )
            );

        } else if ("Personal Health".equals(title)) {

            box.setOnMouseClicked(
                    e -> stage.setScene(
                            new PersonalHealth(stage)
                                    .getScene()
                    )
            );
        }

        return box;
    }

    // =========================================================
    // ACCOUNT SETTING
    // =========================================================

    private HBox setting(
            String title,
            String description,
            Runnable action
    ) {

        HBox row =
                new HBox(15);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(10)
        );

        row.setStyle(
                "-fx-background-color: #faf5ff;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #ede9fe;" +
                "-fx-border-radius: 10;"
        );

        VBox text =
                new VBox(4);

        Label titleLabel =
                new Label(
                        title
                );

        titleLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label descriptionLabel =
                new Label(
                        description
                );

        descriptionLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        descriptionLabel.setWrapText(
                true
        );

        text.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );

        HBox.setHgrow(
                text,
                Priority.ALWAYS
        );

        Button configure =
                new Button(
                        "Manage"
                );

        configure.setStyle(
                "-fx-background-color: #ede9fe;" +
                "-fx-text-fill: #6d28d9;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 7;" +
                "-fx-padding: 8 14;" +
                "-fx-cursor: hand;"
        );

        // =====================================================
        // NAVIGATION ACTION
        // =====================================================

        configure.setOnAction(
                e -> action.run()
        );

        row.getChildren().addAll(
                text,
                configure
        );

        return row;
    }

    // =========================================================
    // NORMAL IMAGE LOADER
    // =========================================================

    private ImageView createImage(
            String path,
            double width,
            double height
    ) {

        ImageView view =
                new ImageView();

        var resource =
                getClass().getResource(
                        path
                );

        if (resource == null) {

            System.err.println(
                    "Profile image not found: "
                            + path
            );

            view.setFitWidth(
                    width
            );

            view.setFitHeight(
                    height
            );

            return view;
        }

        Image image =
                new Image(
                        resource.toExternalForm()
                );

        view.setImage(
                image
        );

        view.setFitWidth(
                width
        );

        view.setFitHeight(
                height
        );

        view.setPreserveRatio(
                false
        );

        return view;
    }

    // =========================================================
    // SQUARE IMAGE LOADER
    // =========================================================

    private ImageView createSquareImage(
            String path,
            double size
    ) {

        ImageView view =
                new ImageView();

        var resource =
                getClass().getResource(
                        path
                );

        if (resource == null) {

            System.err.println(
                    "Profile image not found: "
                            + path
            );

            view.setFitWidth(
                    size
            );

            view.setFitHeight(
                    size
            );

            return view;
        }

        Image image =
                new Image(
                        resource.toExternalForm()
                );

        view.setImage(
                image
        );

        double imageWidth =
                image.getWidth();

        double imageHeight =
                image.getHeight();

        double cropSize =
                Math.min(
                        imageWidth,
                        imageHeight
                );

        double x =
                (imageWidth - cropSize) / 2;

        double y =
                (imageHeight - cropSize) / 2;

        view.setViewport(
                new Rectangle2D(
                        x,
                        y,
                        cropSize,
                        cropSize
                )
        );

        view.setFitWidth(
                size
        );

        view.setFitHeight(
                size
        );

        view.setPreserveRatio(
                false
        );

        return view;
    }

    // =========================================================
    // DASHBOARD
    // =========================================================

    private void showDashboard() {

        stage.setScene(
                new Dashboard(stage)
                        .getScene()
        );

        stage.show();
    }

    // =========================================================
    // LOGIN
    // =========================================================

    private void showLogin() {

        stage.setScene(
                new LoginView(stage)
                        .getScene()
        );

        stage.show();
    }
}