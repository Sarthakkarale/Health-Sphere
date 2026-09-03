package com.healthsphere.controller.hospital;

import com.healthsphere.dao.hospital.HospitalProfileSettingsDAO;
import com.healthsphere.model.HospitalProfileModel;
import com.healthsphere.util.SessionManager;
import com.healthsphere.view.hospital.AppointmentManagementView;
import com.healthsphere.view.hospital.BedManagementView;
import com.healthsphere.view.hospital.DepartmentManagementView;
import com.healthsphere.view.hospital.DoctorManagementView;
import com.healthsphere.view.hospital.HospitalAnalyticsView;
import com.healthsphere.view.hospital.HospitalDashboardView;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.Year;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Controller for Hospital Profile & Settings.
 *
 * Responsibilities:
 * - Coordinate the Settings View and DAO
 * - Load hospital settings
 * - Validate user input
 * - Save hospital settings
 * - Handle image/document selection
 * - Handle navigation
 * - Handle logout
 *
 * Database operations are handled by:
 * HospitalProfileSettingsDAO
 */
public class HospitalProfileSettingsController {

    // =========================================================
    // VALIDATION PATTERNS
    // =========================================================

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
            );

    private static final Pattern PHONE_PATTERN =
            Pattern.compile(
                    "^[0-9+()\\- ]{7,20}$"
            );

    private static final Pattern POSTAL_CODE_PATTERN =
            Pattern.compile(
                    "^[0-9A-Za-z -]{3,10}$"
            );

    // =========================================================
    // FIELDS
    // =========================================================

    private final HospitalProfileModel model;

    private final HospitalProfileSettingsDAO dao;

    private final Stage stage;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public HospitalProfileSettingsController(
            HospitalProfileModel model,
            Stage stage) {

        if (model == null) {

            throw new IllegalArgumentException(
                    "Hospital settings model cannot be null."
            );
        }

        if (stage == null) {

            throw new IllegalArgumentException(
                    "Stage cannot be null."
            );
        }

        this.model = model;

        this.stage = stage;

        this.dao =
                new HospitalProfileSettingsDAO();

        /*
         * Load existing Firestore data immediately.
         */
        loadSettings();
    }

    // =========================================================
    // LOAD SETTINGS
    // =========================================================

    /**
     * Loads the current hospital settings from Firestore.
     */
    public void loadSettings() {

        try {

            HospitalProfileModel saved =
                    dao.getProfile();

            if (saved == null) {
                return;
            }

            copyModel(
                    saved,
                    model
            );

        } catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Load Failed",
                    getRootMessage(e)
            );
        }
    }

    // =========================================================
    // SAVE SETTINGS
    // =========================================================

    /**
     * Validates and saves all hospital settings.
     */
    public void handleSaveChanges() {

        String validationError =
                validate();

        if (validationError != null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Validation Error",
                    validationError
            );

            return;
        }

        try {

            dao.saveProfile(model);

            /*
             * Read the saved data again from Firestore.
             *
             * This ensures that the UI model represents the
             * actual persisted Firestore state.
             */
            HospitalProfileModel saved =
                    dao.getProfile();

            if (saved != null) {

                copyModel(
                        saved,
                        model
                );
            }

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Saved",
                    "Hospital settings saved successfully."
            );

        } catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Save Failed",
                    getRootMessage(e)
            );
        }
    }

    // =========================================================
    // SEARCH
    // =========================================================

    /**
     * Searches the currently loaded settings.
     */
    public void handleSearch(String query) {

        if (query == null
                || query.trim().isEmpty()) {

            return;
        }

        String searchText =
                query.trim().toLowerCase();

        StringBuilder result =
                new StringBuilder();

        addSearchMatch(
                result,
                "Hospital Name",
                model.getHospitalName(),
                searchText
        );

        addSearchMatch(
                result,
                "Registration Number",
                model.getRegistrationNumber(),
                searchText
        );

        addSearchMatch(
                result,
                "Hospital Type",
                model.getHospitalType(),
                searchText
        );

        addSearchMatch(
                result,
                "City",
                model.getCity(),
                searchText
        );

        addSearchMatch(
                result,
                "State",
                model.getState(),
                searchText
        );

        addSearchMatch(
                result,
                "Email",
                model.getEmailAddress(),
                searchText
        );

        addSearchMatch(
                result,
                "Phone",
                model.getPhoneNumber(),
                searchText
        );

        addSearchMatch(
                result,
                "Website",
                model.getWebsite(),
                searchText
        );

        addSearchMatch(
                result,
                "Administrator",
                model.getAdminName(),
                searchText
        );

        addSearchMatch(
                result,
                "Administrator Email",
                model.getAdminEmail(),
                searchText
        );

        if (result.length() == 0) {

            result.append(
                    "No matching setting found."
            );
        }

        showAlert(
                Alert.AlertType.INFORMATION,
                "Settings Search",
                result.toString()
        );
    }

    // =========================================================
    // IMAGE UPLOAD / SELECTION
    // =========================================================

    /**
     * Selects a hospital image.
     *
     * Current implementation stores the selected file name
     * in the model. Actual Firebase Storage upload can be
     * integrated separately once Storage configuration is
     * available in the project.
     */
    public void handleUploadImage() {

        FileChooser chooser =
                new FileChooser();

        chooser.setTitle(
                "Select Hospital Image"
        );

        chooser.getExtensionFilters()
                .add(
                        new FileChooser.ExtensionFilter(
                                "Image Files",
                                "*.png",
                                "*.jpg",
                                "*.jpeg",
                                "*.webp"
                        )
                );

        File file =
                chooser.showOpenDialog(stage);

        if (file == null) {
            return;
        }

        String fileName =
                file.getName();

        ChoiceDialog<String> dialog =
                new ChoiceDialog<>(
                        "Hospital Front",
                        "Hospital Front",
                        "Reception",
                        "Emergency"
                );

        dialog.setTitle(
                "Image Type"
        );

        dialog.setHeaderText(
                "Choose where this image belongs"
        );

        dialog.setContentText(
                "Image:"
        );

        Optional<String> choice =
                dialog.showAndWait();

        if (choice.isEmpty()) {
            return;
        }

        switch (choice.get()) {

            case "Hospital Front":

                model.setHospitalFrontImage(
                        fileName
                );

                break;

            case "Reception":

                model.setReceptionImage(
                        fileName
                );

                break;

            case "Emergency":

                model.setEmergencyImage(
                        fileName
                );

                break;

            default:
                break;
        }

        showAlert(
                Alert.AlertType.INFORMATION,
                "Image Selected",
                fileName
                        + " selected successfully.\n\n"
                        + "Click Save Changes to save the image information."
        );
    }

    // =========================================================
    // DOCUMENT UPLOAD / SELECTION
    // =========================================================

    /**
     * Selects a hospital document.
     *
     * Current implementation stores the selected file name
     * in the model.
     */
    public void handleUploadDocument() {

        FileChooser chooser =
                new FileChooser();

        chooser.setTitle(
                "Select Hospital Document"
        );

        chooser.getExtensionFilters()
                .add(
                        new FileChooser.ExtensionFilter(
                                "Documents",
                                "*.pdf",
                                "*.doc",
                                "*.docx"
                        )
                );

        File file =
                chooser.showOpenDialog(stage);

        if (file == null) {
            return;
        }

        String fileName =
                file.getName();

        ChoiceDialog<String> dialog =
                new ChoiceDialog<>(
                        "Hospital Registration",
                        "Hospital Registration",
                        "Medical License",
                        "NABH Accreditation"
                );

        dialog.setTitle(
                "Document Type"
        );

        dialog.setHeaderText(
                "Choose document type"
        );

        dialog.setContentText(
                "Document:"
        );

        Optional<String> choice =
                dialog.showAndWait();

        if (choice.isEmpty()) {
            return;
        }

        switch (choice.get()) {

            case "Hospital Registration":

                model.setHospitalRegistrationDocument(
                        fileName
                );

                break;

            case "Medical License":

                model.setMedicalLicenseDocument(
                        fileName
                );

                break;

            case "NABH Accreditation":

                model.setNabhAccreditationDocument(
                        fileName
                );

                break;

            default:
                break;
        }

        showAlert(
                Alert.AlertType.INFORMATION,
                "Document Selected",
                fileName
                        + " selected successfully.\n\n"
                        + "Click Save Changes to save the document information."
        );
    }

    // =========================================================
    // VIEW DOCUMENT
    // =========================================================

    public void handleViewDocument(
            String documentType) {

        String documentValue;

        switch (documentType) {

            case "Hospital Registration":

                documentValue =
                        model.getHospitalRegistrationDocument();

                break;

            case "Medical License":

                documentValue =
                        model.getMedicalLicenseDocument();

                break;

            case "NABH Accreditation":

                documentValue =
                        model.getNabhAccreditationDocument();

                break;

            default:

                documentValue = "";
        }

        if (isBlank(documentValue)) {

            showAlert(
                    Alert.AlertType.INFORMATION,
                    documentType,
                    "No document has been uploaded."
            );

            return;
        }

        showAlert(
                Alert.AlertType.INFORMATION,
                documentType,
                "Stored document:\n"
                        + documentValue
        );
    }

    // =========================================================
    // CHANGE PASSWORD
    // =========================================================

    public void handleChangePassword() {

        TextInputDialog dialog =
                new TextInputDialog();

        dialog.setTitle(
                "Change Password"
        );

        dialog.setHeaderText(
                "Password Change"
        );

        dialog.setContentText(
                "Password changes are handled by the "
                        + "Authentication module."
        );

        dialog.showAndWait();
    }

    // =========================================================
    // DEACTIVATE ACCOUNT
    // =========================================================

    public void handleDeactivateAccount() {

        Alert alert =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        alert.setTitle(
                "Deactivate Account"
        );

        alert.setHeaderText(
                "Deactivate hospital account?"
        );

        alert.setContentText(
                "Hospital account status is controlled "
                        + "by the Authentication/Admin module."
        );

        Optional<ButtonType> result =
                alert.showAndWait();

        if (result.isPresent()
                && result.get() == ButtonType.OK) {

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Account",
                    "Please use the Authentication/Admin "
                            + "module for account deactivation."
            );
        }
    }

    // =========================================================
    // NAVIGATION
    // =========================================================

    public void handleNavigation(
            String destination) {

        if (destination == null) {
            return;
        }

        try {

            switch (destination) {

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
                            "Hospital Settings allows you to "
                                    + "manage hospital information, "
                                    + "availability, services and "
                                    + "administrator preferences."
                    );

                    break;

                case "Logout":

                    logout();

                    break;

                default:

                    break;
            }

        } catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Navigation Error",
                    getRootMessage(e)
            );
        }
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    private void logout() {
        com.healthsphere.util.Navigation.logout(stage);
    }

    // =========================================================
    // VALIDATION
    // =========================================================

    private String validate() {

        // -----------------------------------------------------
        // HOSPITAL NAME
        // -----------------------------------------------------

        if (isBlank(
                model.getHospitalName()
        )) {

            return "Hospital Name is required.";
        }

        // -----------------------------------------------------
        // REGISTRATION NUMBER
        // -----------------------------------------------------

        if (isBlank(
                model.getRegistrationNumber()
        )) {

            return "Registration Number is required.";
        }

        // -----------------------------------------------------
        // HOSPITAL TYPE
        // -----------------------------------------------------

        if (isBlank(
                model.getHospitalType()
        )) {

            return "Hospital Type is required.";
        }

        // -----------------------------------------------------
        // ESTABLISHED YEAR
        // -----------------------------------------------------

        if (!isBlank(
                model.getEstablishedYear()
        )) {

            try {

                int establishedYear =
                        Integer.parseInt(
                                model
                                        .getEstablishedYear()
                                        .trim()
                        );

                int currentYear =
                        Year.now().getValue();

                if (establishedYear < 1800
                        || establishedYear > currentYear) {

                    return "Established Year must be between "
                            + "1800 and "
                            + currentYear
                            + ".";
                }

            } catch (NumberFormatException e) {

                return "Established Year must be a valid number.";
            }
        }

        // -----------------------------------------------------
        // PHONE
        // -----------------------------------------------------

        if (!isBlank(
                model.getPhoneNumber()
        )) {

            if (!PHONE_PATTERN.matcher(
                    model.getPhoneNumber().trim()
            ).matches()) {

                return "Enter a valid phone number.";
            }
        }

        // -----------------------------------------------------
        // EMAIL
        // -----------------------------------------------------

        if (!isBlank(
                model.getEmailAddress()
        )) {

            if (!EMAIL_PATTERN.matcher(
                    model.getEmailAddress().trim()
            ).matches()) {

                return "Enter a valid hospital email address.";
            }
        }

        // -----------------------------------------------------
        // ADMIN EMAIL
        // -----------------------------------------------------

        if (!isBlank(
                model.getAdminEmail()
        )) {

            if (!EMAIL_PATTERN.matcher(
                    model.getAdminEmail().trim()
            ).matches()) {

                return "Enter a valid administrator email address.";
            }
        }

        // -----------------------------------------------------
        // POSTAL CODE
        // -----------------------------------------------------

        if (!isBlank(
                model.getPostalCode()
        )) {

            if (!POSTAL_CODE_PATTERN.matcher(
                    model.getPostalCode().trim()
            ).matches()) {

                return "Enter a valid postal code.";
            }
        }

        // -----------------------------------------------------
        // WEBSITE
        // -----------------------------------------------------

        if (!isBlank(
                model.getWebsite()
        )) {

            String website =
                    model.getWebsite().trim();

            boolean validWebsite =
                    website.matches(
                            "^(https?://)?"
                                    + "[A-Za-z0-9.-]+"
                                    + "\\.[A-Za-z]{2,}"
                                    + ".*$"
                    );

            if (!validWebsite) {

                return "Enter a valid website.";
            }
        }

        return null;
    }

    // =========================================================
    // COPY MODEL
    // =========================================================

    /**
     * Copies all Firestore-loaded values into the active
     * JavaFX model.
     */
    private static void copyModel(
            HospitalProfileModel source,
            HospitalProfileModel target) {

        if (source == null
                || target == null) {

            return;
        }

        // -----------------------------------------------------
        // BASIC INFORMATION
        // -----------------------------------------------------

        target.setUid(
                source.getUid()
        );

        target.setHospitalName(
                source.getHospitalName()
        );

        target.setRegistrationNumber(
                source.getRegistrationNumber()
        );

        target.setHospitalType(
                source.getHospitalType()
        );

        target.setEstablishedYear(
                source.getEstablishedYear()
        );

        // -----------------------------------------------------
        // CONTACT
        // -----------------------------------------------------

        target.setPhoneNumber(
                source.getPhoneNumber()
        );

        target.setEmailAddress(
                source.getEmailAddress()
        );

        target.setWebsite(
                source.getWebsite()
        );

        target.setCity(
                source.getCity()
        );

        target.setState(
                source.getState()
        );

        target.setPostalCode(
                source.getPostalCode()
        );

        target.setAddress(
                source.getAddress()
        );

        // -----------------------------------------------------
        // OPERATING HOURS
        // -----------------------------------------------------

        target.setMondayHours(
                source.getMondayHours()
        );

        target.setTuesdayHours(
                source.getTuesdayHours()
        );

        target.setWednesdayHours(
                source.getWednesdayHours()
        );

        target.setThursdayHours(
                source.getThursdayHours()
        );

        target.setFridayHours(
                source.getFridayHours()
        );

        target.setSaturdayHours(
                source.getSaturdayHours()
        );

        target.setSundayHours(
                source.getSundayHours()
        );

        // -----------------------------------------------------
        // ACTIVE DAYS
        // -----------------------------------------------------

        target.setMondayActive(
                source.isMondayActive()
        );

        target.setTuesdayActive(
                source.isTuesdayActive()
        );

        target.setWednesdayActive(
                source.isWednesdayActive()
        );

        target.setThursdayActive(
                source.isThursdayActive()
        );

        target.setFridayActive(
                source.isFridayActive()
        );

        target.setSaturdayActive(
                source.isSaturdayActive()
        );

        target.setSundayActive(
                source.isSundayActive()
        );

        // -----------------------------------------------------
        // EMERGENCY SERVICES
        // -----------------------------------------------------

        target.setAmbulance24x7(
                source.isAmbulance24x7()
        );

        target.setEmergencyWard24x7(
                source.isEmergencyWard24x7()
        );

        target.setTraumaUnit(
                source.isTraumaUnit()
        );

        // -----------------------------------------------------
        // MEDICAL TOURISM
        // -----------------------------------------------------

        target.setMedicalTourismAvailable(
                source.isMedicalTourismAvailable()
        );

        target.setInternationalDeskAvailable(
                source.isInternationalDeskAvailable()
        );

        target.setAirportPickupAvailable(
                source.isAirportPickupAvailable()
        );

        target.setPrimaryLanguage(
                source.getPrimaryLanguage()
        );

        // -----------------------------------------------------
        // IMAGES
        // -----------------------------------------------------

        target.setHospitalFrontImage(
                source.getHospitalFrontImage()
        );

        target.setReceptionImage(
                source.getReceptionImage()
        );

        target.setEmergencyImage(
                source.getEmergencyImage()
        );

        // -----------------------------------------------------
        // DOCUMENTS
        // -----------------------------------------------------

        target.setHospitalRegistrationDocument(
                source.getHospitalRegistrationDocument()
        );

        target.setMedicalLicenseDocument(
                source.getMedicalLicenseDocument()
        );

        target.setNabhAccreditationDocument(
                source.getNabhAccreditationDocument()
        );

        // -----------------------------------------------------
        // ADMINISTRATOR
        // -----------------------------------------------------

        target.setAdminName(
                source.getAdminName()
        );

        target.setAdminEmail(
                source.getAdminEmail()
        );

        target.setNotificationPreference(
                source.getNotificationPreference()
        );

        target.setEmailNotifications(
                source.isEmailNotifications()
        );

        target.setSecurityAlerts(
                source.isSecurityAlerts()
        );
    }

    // =========================================================
    // SEARCH HELPER
    // =========================================================

    private static void addSearchMatch(
            StringBuilder result,
            String label,
            String value,
            String query) {

        if (value == null
                || value.trim().isEmpty()) {

            return;
        }

        if (value.toLowerCase()
                .contains(query)) {

            if (result.length() > 0) {

                result.append("\n");
            }

            result.append(label)
                    .append(": ")
                    .append(value);
        }
    }

    // =========================================================
    // EMPTY CHECK
    // =========================================================

    private static boolean isBlank(
            String value) {

        return value == null
                || value.trim().isEmpty();
    }

    // =========================================================
    // ROOT ERROR MESSAGE
    // =========================================================

    private static String getRootMessage(
            Throwable throwable) {

        if (throwable == null) {

            return "An unknown error occurred.";
        }

        Throwable current =
                throwable;

        String message =
                throwable.getMessage();

        while (current.getCause() != null) {

            current =
                    current.getCause();

            if (current.getMessage() != null
                    && !current.getMessage()
                    .trim()
                    .isEmpty()) {

                message =
                        current.getMessage();
            }
        }

        if (isBlank(message)) {

            return "An unexpected error occurred.";
        }

        return message;
    }

    // =========================================================
    // ALERT
    // =========================================================

    private void showAlert(
            Alert.AlertType type,
            String title,
            String message) {

        Alert alert =
                new Alert(type);

        alert.setTitle(title);

        alert.setHeaderText(null);

        alert.setContentText(message);

        alert.showAndWait();
    }
}