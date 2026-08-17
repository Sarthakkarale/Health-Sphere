package com.healthsphere.view.Hospital;

import com.healthsphere.controller.hospital.HospitalController;
import com.healthsphere.model.HospitalProfile;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class HospitalProfileSettingsController {

    private final HospitalProfileModel model;
    private final Stage stage;

    // Backend controller
    private final HospitalController hospitalController;

    public HospitalProfileSettingsController(
            HospitalProfileModel model,
            Stage stage) {

        this.model = model;
        this.stage = stage;
        this.hospitalController = new HospitalController();

        // Load existing hospital data when the screen opens
        loadHospitalProfile();
    }

    // =========================================================
    // LOAD HOSPITAL PROFILE
    // =========================================================

    private void loadHospitalProfile() {

        try {

            HospitalProfile profile =
                    hospitalController.getHospitalProfile();

            if (profile == null) {
                return;
            }

            if (profile.getHospitalName() != null) {
                model.hospitalNameProperty()
                        .set(profile.getHospitalName());
            }

            if (profile.getRegistrationNumber() != null) {
                model.registrationNumberProperty()
                        .set(profile.getRegistrationNumber());
            }

            if (profile.getHospitalType() != null) {
                model.hospitalTypeProperty()
                        .set(profile.getHospitalType());
            }

            if (profile.getContact() != null) {
                model.phoneNumberProperty()
                        .set(profile.getContact());
            }

            if (profile.getEmail() != null) {
                model.emailAddressProperty()
                        .set(profile.getEmail());
            }

            if (profile.getAddress() != null) {
                model.addressProperty()
                        .set(profile.getAddress());
            }

        } catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Load Failed",
                    "Unable to load hospital profile.\n\n"
                            + e.getMessage()
            );
        }
    }

    // =========================================================
    // SAVE CHANGES
    // =========================================================

    public void handleSaveChanges() {

        try {

            HospitalProfile profile =
                    new HospitalProfile();

            profile.setHospitalName(
                    model.hospitalNameProperty().get()
            );

            profile.setRegistrationNumber(
                    model.registrationNumberProperty().get()
            );

            profile.setHospitalType(
                    model.hospitalTypeProperty().get()
            );

            profile.setContact(
                    model.phoneNumberProperty().get()
            );

            profile.setEmail(
                    model.emailAddressProperty().get()
            );

            profile.setAddress(
                    model.addressProperty().get()
            );

            hospitalController.saveHospitalProfile(profile);

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Settings Saved",
                    "Hospital profile has been saved successfully."
            );

        } catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Save Failed",
                    "Unable to save hospital profile.\n\n"
                            + e.getMessage()
            );
        }
    }

    // =========================================================
    // IMAGE UPLOAD
    // =========================================================

    public void handleUploadImage() {

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle(
                "Upload Hospital Image"
        );

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Image Files",
                        "*.png",
                        "*.jpg",
                        "*.jpeg"
                )
        );

        File selectedFile =
                fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Image Selected",
                    "Selected image: "
                            + selectedFile.getName()
            );
        }
    }

    // =========================================================
    // DOCUMENT UPLOAD
    // =========================================================

    public void handleUploadDocument() {

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle(
                "Upload Document / License"
        );

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "PDF Documents",
                        "*.pdf"
                )
        );

        File selectedFile =
                fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Document Selected",
                    "Selected document: "
                            + selectedFile.getName()
            );
        }
    }

    // =========================================================
    // VIEW DOCUMENT
    // =========================================================

    public void handleViewDocument(
            String docTitle) {

        showAlert(
                Alert.AlertType.INFORMATION,
                "Viewing Document",
                "Opening record viewer for: "
                        + docTitle
        );
    }

    // =========================================================
    // CHANGE PASSWORD
    // =========================================================

    public void handleChangePassword() {

        TextInputDialog dialog =
                new TextInputDialog();

        dialog.setTitle("Change Password");
        dialog.setHeaderText(
                "Security Verification"
        );
        dialog.setContentText(
                "Enter your new administrator password:"
        );

        Optional<String> result =
                dialog.showAndWait();

        result.ifPresent(password -> {

            if (!password.trim().isEmpty()) {

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Success",
                        "Password updated successfully."
                );

            } else {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Warning",
                        "Password cannot be empty."
                );
            }
        });
    }

    // =========================================================
    // DEACTIVATE ACCOUNT
    // =========================================================

    public void handleDeactivateAccount() {

        Alert confirmAlert =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmAlert.setTitle(
                "Confirm Deactivation"
        );

        confirmAlert.setHeaderText(
                "Deactivate Administrator Account"
        );

        confirmAlert.setContentText(
                "Are you sure you want to deactivate "
                        + "this account? This action requires "
                        + "super-admin approval."
        );

        Optional<ButtonType> result =
                confirmAlert.showAndWait();

        if (result.isPresent()
                && result.get() == ButtonType.OK) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Account Deactivation Requested",
                    "Deactivation request sent "
                            + "to System Administration."
            );
        }
    }

    // =========================================================
    // SEARCH
    // =========================================================

    public void handleSearch(String query) {

        if (query != null
                && !query.trim().isEmpty()) {

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Search Settings",
                    "Filtering view for: "
                            + query
            );
        }
    }

    // =========================================================
    // NAVIGATION
    // =========================================================

    public void handleNavigation(
            String viewName) {

        switch (viewName) {

            case "Dashboard":
                stage.setScene(
                        new HospitalDashboardView()
                                .createScene(stage)
                );
                break;

            case "Doctors":
                stage.setScene(
                        new DoctorManagementView()
                                .createScene(stage)
                );
                break;

            case "Departments":
                stage.setScene(
                        new DepartmentManagementView()
                                .createScene(stage)
                );
                break;

            case "Beds":
                stage.setScene(
                        new BedManagementView()
                                .createScene(stage)
                );
                break;

            case "Appointments":
                stage.setScene(
                        new AppointmentManagementView()
                                .createScene(stage)
                );
                break;

            case "Analytics":
                stage.setScene(
                        new HospitalAnalyticsView()
                                .createScene(stage)
                );
                break;

            case "Help":
                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Help Center",
                        "Opening Health-Sphere Support Documentation."
                );
                break;

            case "Logout":

                Alert confirm =
                        new Alert(
                                Alert.AlertType.CONFIRMATION,
                                "Are you sure you want to log out?",
                                ButtonType.YES,
                                ButtonType.NO
                        );

                confirm.showAndWait()
                        .ifPresent(response -> {

                            if (response == ButtonType.YES) {

                                showAlert(
                                        Alert.AlertType.INFORMATION,
                                        "Logout",
                                        "Logged out successfully."
                                );
                            }
                        });

                break;

            default:
                break;
        }
    }

    // =========================================================
    // ALERT
    // =========================================================

    private void showAlert(
            Alert.AlertType type,
            String title,
            String content) {

        Alert alert =
                new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        alert.showAndWait();
    }
}