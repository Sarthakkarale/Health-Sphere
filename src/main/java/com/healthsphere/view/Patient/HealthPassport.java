
package com.healthsphere.view.Patient;

import java.net.URL;

import com.healthsphere.controller.patient.PatientController;
import com.healthsphere.model.PatientProfile;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HealthPassport {

    private final Stage stage;
    private final PatientController patientController;

    public HealthPassport(Stage stage) {
        this.stage = stage;
        this.patientController = new PatientController();
    }

    // =========================================================
    // MAIN SCENE
    // =========================================================

    public Scene getScene() {

        VBox content = new VBox(20);
        content.setPadding(new Insets(5));
        content.setFillWidth(true);

        HBox images = createImageGallery();
        VBox dynamicContainer = new VBox(20);
        dynamicContainer.setMinWidth(0);
        dynamicContainer.setMaxWidth(Double.MAX_VALUE);

        // Show Shimmer Skeleton Initially
        VBox shimmer = com.healthsphere.util.ShimmerPlaceholder.createListShimmer(3);
        dynamicContainer.getChildren().add(shimmer);

        content.getChildren().addAll(images, dynamicContainer);

        javafx.concurrent.Task<PatientProfile> task = new javafx.concurrent.Task<>() {
            @Override
            protected PatientProfile call() throws Exception {
                return patientController.getCurrentPatientProfile();
            }
        };

        task.setOnSucceeded(e -> {
            dynamicContainer.getChildren().clear();
            PatientProfile patientProfile = task.getValue();
            if (patientProfile == null) {
                patientProfile = new PatientProfile();
            }

            final PatientProfile profileRef = patientProfile;

            // Personal Info Card
            VBox personalCard = PatientUI.coloredCard("👤  Personal Information", "#dbeafe");
            personalCard.getChildren().addAll(
                    information("Full Name", getFullName(profileRef)),
                    information("Date of Birth", safeValue(profileRef.getDateOfBirth(), "Not provided")),
                    information("Blood Group", safeValue(profileRef.getBloodGroup(), "Not provided")),
                    information("Gender", safeValue(profileRef.getGender(), "Not provided")),
                    PatientUI.button("✏ Edit Personal Information", () -> showPersonalEditDialog(profileRef))
            );

            // Contact Info Card
            VBox contactCard = PatientUI.coloredCard("📞  Contact Information", "#e0f2fe");
            contactCard.getChildren().addAll(
                    information("Email", safeValue(profileRef.getEmail(), "Not provided")),
                    information("Phone", safeValue(profileRef.getPhone(), "Not provided")),
                    information("Address", safeValue(profileRef.getAddress(), "Not provided")),
                    information("Emergency Contact", safeValue(profileRef.getEmergencyContact(), "Not provided")),
                    PatientUI.button("✏ Edit Contact Information", () -> showContactEditDialog(profileRef))
            );

            HBox informationRow = new HBox(18);
            informationRow.setFillHeight(true);
            personalCard.setMaxWidth(Double.MAX_VALUE);
            contactCard.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(personalCard, Priority.ALWAYS);
            HBox.setHgrow(contactCard, Priority.ALWAYS);
            informationRow.getChildren().addAll(personalCard, contactCard);

            // Medical Card
            VBox medicalCard = PatientUI.coloredCard("🏥  Medical Information", "#dcfce7");
            medicalCard.getChildren().addAll(
                    information("Blood Group", safeValue(profileRef.getBloodGroup(), "Not provided")),
                    information("Gender", safeValue(profileRef.getGender(), "Not provided")),
                    information("Emergency Contact", safeValue(profileRef.getEmergencyContact(), "Not provided")),
                    information("Patient ID", safeValue(profileRef.getUid(), "Not available")),
                    PatientUI.button("✏ Edit Medical Information", () -> showMedicalEditDialog(profileRef))
            );

            // Records Card
            VBox recordsCard = PatientUI.coloredCard("📋  Health Summary", "#fef3c7");
            recordsCard.getChildren().addAll(
                    summary("Medical Records", "12 records available"),
                    summary("Prescriptions", "5 active prescriptions"),
                    summary("Appointments", "3 upcoming appointments"),
                    summary("Lab Reports", "8 reports available"),
                    PatientUI.button("View Medical Records", () -> stage.setScene(new MedicalRecords(stage).getScene()))
            );

            // Status Card
            VBox statusCard = PatientUI.coloredCard("💚  Health Status", "#ccfbf1");
            statusCard.getChildren().addAll(
                    status("Heart Rate", safeValue(profileRef.getHeartRate(), "Not provided"), "Normal"),
                    status("Blood Pressure", safeValue(profileRef.getBloodPressure(), "Not provided"), "Healthy"),
                    status("Oxygen Level", safeValue(profileRef.getOxygenLevel(), "Not provided"), "Normal"),
                    status("Last Health Check", safeValue(profileRef.getLastHealthCheck(), "Not provided"), "Up to date"),
                    PatientUI.button("✏ Edit Health Status", () -> showHealthStatusEditDialog(profileRef))
            );

            // Quick Actions Card
            VBox actionsCard = PatientUI.coloredCard("⚡  Quick Actions", "#ede9fe");
            HBox actions = new HBox(12);
            actions.setAlignment(Pos.CENTER_LEFT);
            actions.getChildren().addAll(
                    PatientUI.button("Appointments", () -> stage.setScene(new Appointments(stage).getScene())),
                    PatientUI.button("Medical Records", () -> stage.setScene(new MedicalRecords(stage).getScene())),
                    PatientUI.button("AI Assistant", () -> stage.setScene(new AiHealthAssistant(stage).getScene())),
                    createEmergencyButton(),
                    PatientUI.button("Profile & Settings", () -> stage.setScene(new ProfileSettings(stage).getScene()))
            );
            actionsCard.getChildren().add(actions);

            dynamicContainer.getChildren().addAll(
                    informationRow,
                    medicalCard,
                    recordsCard,
                    statusCard,
                    actionsCard
            );
        });

        task.setOnFailed(e -> {
            dynamicContainer.getChildren().clear();
            Throwable ex = task.getException();
            Label errLabel = new Label("Unable to load your health profile. Please try again.");
            errLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold; -fx-font-size: 15px;");
            dynamicContainer.getChildren().add(errLabel);
        });

        com.healthsphere.util.PatientBackgroundExecutor.execute(task);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setPannable(true);

        return PatientUI.createScene(
                stage,
                "Health Passport",
                "Health Passport",
                "Your complete personal health identity and records.",
                scroll
        );
    }

    // =========================================================
    // PERSONAL INFORMATION EDIT
    // =========================================================

    private void showPersonalEditDialog(
            PatientProfile profile) {

        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle(
                "Edit Personal Information"
        );

        dialog.setHeaderText(
                "Update your personal information"
        );

        GridPane grid =
                createFormGrid();

        TextField fullName =
                createTextField(
                        getFullName(profile)
                );

        TextField dateOfBirth =
                createTextField(
                        profile.getDateOfBirth()
                );

        ComboBox<String> bloodGroup =
                createBloodGroupCombo(
                        profile.getBloodGroup()
                );

        ComboBox<String> gender =
                createGenderCombo(
                        profile.getGender()
                );

        grid.add(
                new Label("Full Name"),
                0,
                0
        );

        grid.add(
                fullName,
                1,
                0
        );

        grid.add(
                new Label("Date of Birth"),
                0,
                1
        );

        grid.add(
                dateOfBirth,
                1,
                1
        );

        grid.add(
                new Label("Blood Group"),
                0,
                2
        );

        grid.add(
                bloodGroup,
                1,
                2
        );

        grid.add(
                new Label("Gender"),
                0,
                3
        );

        grid.add(
                gender,
                1,
                3
        );

        addDialogButtons(dialog);

        dialog.getDialogPane()
                .setContent(grid);

        ButtonType result =
                dialog.showAndWait()
                        .orElse(
                                ButtonType.CANCEL
                        );

        if (result.getButtonData() ==
                ButtonBar.ButtonData.OK_DONE) {

            try {

                patientController
                        .updateHealthPassportProfile(

                                fullName.getText(),

                                safeText(
                                        profile.getEmail()
                                ),

                                safeText(
                                        profile.getPhone()
                                ),

                                safeText(
                                        profile.getAddress()
                                ),

                                dateOfBirth.getText(),

                                gender.getValue(),

                                bloodGroup.getValue(),

                                safeText(
                                        profile.getEmergencyContact()
                                )
                        );

                showSuccess(
                        "Personal information updated successfully."
                );

                refreshHealthPassport();

            } catch (Exception e) {

                showError(
                        "Unable to update personal information.",
                        e
                );
            }
        }
    }

    // =========================================================
    // CONTACT INFORMATION EDIT
    // =========================================================

    private void showContactEditDialog(
            PatientProfile profile) {

        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle(
                "Edit Contact Information"
        );

        dialog.setHeaderText(
                "Update your contact information"
        );

        GridPane grid =
                createFormGrid();

        TextField email =
                createTextField(
                        profile.getEmail()
                );

        TextField phone =
                createTextField(
                        profile.getPhone()
                );

        TextField address =
                createTextField(
                        profile.getAddress()
                );

        TextField emergency =
                createTextField(
                        profile.getEmergencyContact()
                );

        grid.add(
                new Label("Email"),
                0,
                0
        );

        grid.add(
                email,
                1,
                0
        );

        grid.add(
                new Label("Phone"),
                0,
                1
        );

        grid.add(
                phone,
                1,
                1
        );

        grid.add(
                new Label("Address"),
                0,
                2
        );

        grid.add(
                address,
                1,
                2
        );

        grid.add(
                new Label("Emergency Contact"),
                0,
                3
        );

        grid.add(
                emergency,
                1,
                3
        );

        addDialogButtons(dialog);

        dialog.getDialogPane()
                .setContent(grid);

        ButtonType result =
                dialog.showAndWait()
                        .orElse(
                                ButtonType.CANCEL
                        );

        if (result.getButtonData() ==
                ButtonBar.ButtonData.OK_DONE) {

            try {

                patientController
                        .updateHealthPassportProfile(

                                getFullName(profile),

                                email.getText(),

                                phone.getText(),

                                address.getText(),

                                safeText(
                                        profile.getDateOfBirth()
                                ),

                                safeText(
                                        profile.getGender()
                                ),

                                safeText(
                                        profile.getBloodGroup()
                                ),

                                emergency.getText()
                        );

                showSuccess(
                        "Contact information updated successfully."
                );

                refreshHealthPassport();

            } catch (Exception e) {

                showError(
                        "Unable to update contact information.",
                        e
                );
            }
        }
    }

    // =========================================================
    // MEDICAL INFORMATION EDIT
    // =========================================================

    private void showMedicalEditDialog(
            PatientProfile profile) {

        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle(
                "Edit Medical Information"
        );

        dialog.setHeaderText(
                "Update your medical information"
        );

        GridPane grid =
                createFormGrid();

        ComboBox<String> bloodGroup =
                createBloodGroupCombo(
                        profile.getBloodGroup()
                );

        ComboBox<String> gender =
                createGenderCombo(
                        profile.getGender()
                );

        TextField emergency =
                createTextField(
                        profile.getEmergencyContact()
                );

        grid.add(
                new Label("Blood Group"),
                0,
                0
        );

        grid.add(
                bloodGroup,
                1,
                0
        );

        grid.add(
                new Label("Gender"),
                0,
                1
        );

        grid.add(
                gender,
                1,
                1
        );

        grid.add(
                new Label("Emergency Contact"),
                0,
                2
        );

        grid.add(
                emergency,
                1,
                2
        );

        grid.add(
                new Label("Patient ID"),
                0,
                3
        );

        Label patientId =
                new Label(
                        safeValue(
                                profile.getUid(),
                                "Not available"
                        )
                );

        patientId.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-weight: bold;"
        );

        grid.add(
                patientId,
                1,
                3
        );

        addDialogButtons(dialog);

        dialog.getDialogPane()
                .setContent(grid);

        ButtonType result =
                dialog.showAndWait()
                        .orElse(
                                ButtonType.CANCEL
                        );

        if (result.getButtonData() ==
                ButtonBar.ButtonData.OK_DONE) {

            try {

                patientController
                        .updateHealthPassportProfile(

                                getFullName(profile),

                                safeText(
                                        profile.getEmail()
                                ),

                                safeText(
                                        profile.getPhone()
                                ),

                                safeText(
                                        profile.getAddress()
                                ),

                                safeText(
                                        profile.getDateOfBirth()
                                ),

                                gender.getValue(),

                                bloodGroup.getValue(),

                                emergency.getText()
                        );

                showSuccess(
                        "Medical information updated successfully."
                );

                refreshHealthPassport();

            } catch (Exception e) {

                showError(
                        "Unable to update medical information.",
                        e
                );
            }
        }
    }

    // =========================================================
    // HEALTH STATUS EDIT
    // =========================================================

    private void showHealthStatusEditDialog(
            PatientProfile profile) {

        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle(
                "Edit Health Status"
        );

        dialog.setHeaderText(
                "Update your current health status"
        );

        GridPane grid =
                createFormGrid();

        TextField heartRate =
                createTextField(
                        profile.getHeartRate()
                );

        TextField bloodPressure =
                createTextField(
                        profile.getBloodPressure()
                );

        TextField oxygenLevel =
                createTextField(
                        profile.getOxygenLevel()
                );

        TextField lastHealthCheck =
                createTextField(
                        profile.getLastHealthCheck()
                );

        heartRate.setPromptText(
                "Example: 72 BPM"
        );

        bloodPressure.setPromptText(
                "Example: 118 / 76 mmHg"
        );

        oxygenLevel.setPromptText(
                "Example: 98%"
        );

        lastHealthCheck.setPromptText(
                "Example: 10 August 2026"
        );

        grid.add(
                new Label("Heart Rate"),
                0,
                0
        );

        grid.add(
                heartRate,
                1,
                0
        );

        grid.add(
                new Label("Blood Pressure"),
                0,
                1
        );

        grid.add(
                bloodPressure,
                1,
                1
        );

        grid.add(
                new Label("Oxygen Level"),
                0,
                2
        );

        grid.add(
                oxygenLevel,
                1,
                2
        );

        grid.add(
                new Label("Last Health Check"),
                0,
                3
        );

        grid.add(
                lastHealthCheck,
                1,
                3
        );

        addDialogButtons(dialog);

        dialog.getDialogPane()
                .setContent(grid);

        ButtonType result =
                dialog.showAndWait()
                        .orElse(
                                ButtonType.CANCEL
                        );

        if (result.getButtonData() ==
                ButtonBar.ButtonData.OK_DONE) {

            try {

                patientController.updateHealthStatus(

                        heartRate.getText(),

                        bloodPressure.getText(),

                        oxygenLevel.getText(),

                        lastHealthCheck.getText()
                );

                showSuccess(
                        "Health status updated successfully."
                );

                refreshHealthPassport();

            } catch (Exception e) {

                showError(
                        "Unable to update health status.",
                        e
                );
            }
        }
    }

    // =========================================================
    // ADD DIALOG BUTTONS
    // =========================================================

    private void addDialogButtons(
            Dialog<ButtonType> dialog) {

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(

                        new ButtonType(
                                "Save",
                                ButtonBar.ButtonData.OK_DONE
                        ),

                        new ButtonType(
                                "Cancel",
                                ButtonBar.ButtonData.CANCEL_CLOSE
                        )
                );
    }

    // =========================================================
    // FORM GRID
    // =========================================================

    private GridPane createFormGrid() {

        GridPane grid =
                new GridPane();

        grid.setHgap(15);

        grid.setVgap(15);

        grid.setPadding(
                new Insets(15)
        );

        return grid;
    }

    // =========================================================
    // TEXT FIELD
    // =========================================================

    private TextField createTextField(
            String value) {

        TextField field =
                new TextField();

        field.setText(
                safeText(value)
        );

        field.setPrefWidth(
                300
        );

        return field;
    }

    // =========================================================
    // BLOOD GROUP
    // =========================================================

    private ComboBox<String> createBloodGroupCombo(
            String currentValue) {

        ComboBox<String> combo =
                new ComboBox<>();

        combo.getItems().addAll(
                "A+",
                "A-",
                "B+",
                "B-",
                "AB+",
                "AB-",
                "O+",
                "O-"
        );

        if (currentValue != null &&
                !currentValue.isBlank()) {

            combo.setValue(
                    currentValue
            );
        }

        combo.setPrefWidth(
                300
        );

        return combo;
    }

    // =========================================================
    // GENDER
    // =========================================================

    private ComboBox<String> createGenderCombo(
            String currentValue) {

        ComboBox<String> combo =
                new ComboBox<>();

        combo.getItems().addAll(
                "Male",
                "Female",
                "Other",
                "Prefer not to say"
        );

        if (currentValue != null &&
                !currentValue.isBlank()) {

            combo.setValue(
                    currentValue
            );
        }

        combo.setPrefWidth(
                300
        );

        return combo;
    }

    // =========================================================
    // REFRESH PAGE
    // =========================================================

    private void refreshHealthPassport() {

        stage.setScene(
                new HealthPassport(stage)
                        .getScene()
        );

        stage.show();

        if (!stage.isMaximized()) {
            stage.setMaximized(true);
        }
    }

    // =========================================================
    // SUCCESS ALERT
    // =========================================================

    private void showSuccess(
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(
                "Health Passport"
        );

        alert.setHeaderText(
                "Update Successful"
        );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }

    // =========================================================
    // ERROR ALERT
    // =========================================================

    private void showError(
            String message,
            Exception exception) {

        exception.printStackTrace();

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle(
                "Health Passport"
        );

        alert.setHeaderText(
                "Update Failed"
        );

        String errorMessage =
                exception.getMessage();

        if (errorMessage == null ||
                errorMessage.isBlank()) {

            errorMessage =
                    "Please try again.";
        }

        alert.setContentText(
                message +
                "\n\n" +
                errorMessage
        );

        alert.showAndWait();
    }

    // =========================================================
    // IMAGE GALLERY
    // =========================================================

    private HBox createImageGallery() {

        HBox gallery =
                new HBox(15);

        gallery.setAlignment(
                Pos.CENTER_LEFT
        );

        gallery.getChildren().addAll(

                imageCard(
                        "/images/healthpassport/healthpassport1.jpg"
                ),

                imageCard(
                        "/images/healthpassport/healthpassport2.jpg"
                ),

                imageCard(
                        "/images/healthpassport/healthpassport3.jpg"
                ),

                imageCard(
                        "/images/healthpassport/healthpassport4.jpg"
                )
        );

        return gallery;
    }

    // =========================================================
    // IMAGE CARD
    // =========================================================

    private VBox imageCard(
            String path) {

        VBox box =
                new VBox();

        box.setAlignment(
                Pos.CENTER
        );

        box.setPrefWidth(
                240
        );

        box.setPrefHeight(
                140
        );

        box.setMinWidth(
                220
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #cbd5e1;" +
                "-fx-border-radius: 14;"
        );

        URL resource =
                getClass().getResource(path);

        if (resource == null) {

            Label unavailable =
                    new Label(
                            "Image unavailable"
                    );

            unavailable.setStyle(
                    "-fx-text-fill: #64748b;" +
                    "-fx-font-size: 13px;"
            );

            box.getChildren().add(
                    unavailable
            );

            System.err.println(
                    "Health Passport image not found: "
                            + path
            );

            return box;
        }

        Image image =
                new Image(
                        resource.toExternalForm()
                );

        ImageView imageView =
                new ImageView(image);

        imageView.setFitWidth(
                240
        );

        imageView.setFitHeight(
                140
        );

        imageView.setPreserveRatio(
                false
        );

        box.getChildren().add(
                imageView
        );

        return box;
    }

    // =========================================================
    // INFORMATION ROW
    // =========================================================

    private HBox information(
            String title,
            String value) {

        HBox row =
                new HBox(12);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(10)
        );

        row.setStyle(
                "-fx-background-color: rgba(255,255,255,0.85);" +
                "-fx-background-radius: 9;" +
                "-fx-border-color: #cbd5e1;" +
                "-fx-border-radius: 9;"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #475569;"
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setWrapText(true);

        valueLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        row.getChildren().addAll(
                titleLabel,
                spacer,
                valueLabel
        );

        return row;
    }

    // =========================================================
    // SUMMARY ROW
    // =========================================================

    private HBox summary(
            String title,
            String value) {

        HBox row =
                new HBox(12);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(10)
        );

        row.setStyle(
                "-fx-background-color: rgba(255,255,255,0.85);" +
                "-fx-background-radius: 9;" +
                "-fx-border-color: #fde68a;" +
                "-fx-border-radius: 9;"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-text-fill: #475569;"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        row.getChildren().addAll(
                titleLabel,
                spacer,
                valueLabel
        );

        return row;
    }

    // =========================================================
    // HEALTH STATUS ROW
    // =========================================================

    private HBox status(
            String title,
            String value,
            String condition) {

        HBox row =
                new HBox(12);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(10)
        );

        row.setStyle(
                "-fx-background-color: rgba(255,255,255,0.85);" +
                "-fx-background-radius: 9;" +
                "-fx-border-color: #99f6e4;" +
                "-fx-border-radius: 9;"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #134e4a;"
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setWrapText(true);

        valueLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label conditionLabel =
                new Label(condition);

        conditionLabel.setStyle(
                "-fx-background-color: #dcfce7;" +
                "-fx-text-fill: #166534;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 5 9;" +
                "-fx-background-radius: 8;"
        );

        Region spacer1 =
                new Region();

        Region spacer2 =
                new Region();

        HBox.setHgrow(
                spacer1,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                spacer2,
                Priority.ALWAYS
        );

        row.getChildren().addAll(
                titleLabel,
                spacer1,
                valueLabel,
                spacer2,
                conditionLabel
        );

        return row;
    }

    // =========================================================
    // EMERGENCY BUTTON
    // =========================================================

    private Button createEmergencyButton() {

        Button button =
                new Button(
                        "Emergency"
                );

        button.setPrefHeight(
                38
        );

        button.setStyle(
                "-fx-background-color: #dc2626;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 8 14;" +
                "-fx-cursor: hand;"
        );

        button.setOnAction(
                e -> stage.setScene(
                        new EmergencyAssistance(stage)
                                .getScene()
                )
        );

        return button;
    }

    // =========================================================
    // FULL NAME
    // =========================================================

    private String getFullName(
            PatientProfile profile) {

        String firstName =
                safeValue(
                        profile.getFirstName(),
                        ""
                );

        String lastName =
                safeValue(
                        profile.getLastName(),
                        ""
                );

        String fullName =
                (firstName + " " + lastName)
                        .trim();

        if (fullName.isEmpty()) {
            return "Not provided";
        }

        return fullName;
    }

    // =========================================================
    // SAFE VALUE
    // =========================================================

    private String safeValue(
            String value,
            String fallback) {

        if (value == null ||
                value.trim().isEmpty()) {

            return fallback;
        }

        return value.trim();
    }

    // =========================================================
    // SAFE TEXT
    // =========================================================

    private String safeText(
            String value) {

        if (value == null) {
            return "";
        }

        return value.trim();
    }

    // =========================================================
    // ERROR SCENE
    // =========================================================

    private Scene createErrorScene(
            String message) {

        VBox content =
                new VBox(20);

        content.setAlignment(
                Pos.CENTER
        );

        content.setPadding(
                new Insets(40)
        );

        Label icon =
                new Label("⚠");

        icon.setStyle(
                "-fx-font-size: 50px;" +
                "-fx-text-fill: #dc2626;"
        );

        Label title =
                new Label(
                        "Unable to Load Health Passport"
                );

        title.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label description =
                new Label(message);

        description.setWrapText(true);

        description.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #64748b;"
        );

        Button back =
                PatientUI.button(
                        "Back to Dashboard",
                        () -> stage.setScene(
                                new Dashboard(stage)
                                        .getScene()
                        )
                );

        content.getChildren().addAll(
                icon,
                title,
                description,
                back
        );

        return PatientUI.createScene(
                stage,
                "Health Passport",
                "Health Passport",
                "Unable to load patient information.",
                content
        );
    }
}

