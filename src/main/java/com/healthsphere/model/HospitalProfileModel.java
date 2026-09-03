package com.healthsphere.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * UI model for Hospital Settings.
 *
 * This model keeps the JavaFX controls independent from Firestore.
 * Firestore persistence is handled by HospitalProfileSettingsDAO.
 */
public class HospitalProfileModel {

    // =========================================================
    // BASIC HOSPITAL INFORMATION
    // =========================================================

    private final StringProperty uid =
            new SimpleStringProperty("");

    private final StringProperty hospitalName =
            new SimpleStringProperty("");

    private final StringProperty registrationNumber =
            new SimpleStringProperty("");

    private final StringProperty hospitalType =
            new SimpleStringProperty("");

    private final StringProperty establishedYear =
            new SimpleStringProperty("");

    // =========================================================
    // CONTACT INFORMATION
    // =========================================================

    private final StringProperty phoneNumber =
            new SimpleStringProperty("");

    private final StringProperty emailAddress =
            new SimpleStringProperty("");

    private final StringProperty website =
            new SimpleStringProperty("");

    private final StringProperty city =
            new SimpleStringProperty("");

    private final StringProperty state =
            new SimpleStringProperty("");

    private final StringProperty postalCode =
            new SimpleStringProperty("");

    private final StringProperty address =
            new SimpleStringProperty("");

    // =========================================================
    // OPERATING HOURS
    // =========================================================

    private final StringProperty mondayHours =
            new SimpleStringProperty("08:00 AM - 08:00 PM");

    private final StringProperty tuesdayHours =
            new SimpleStringProperty("08:00 AM - 08:00 PM");

    private final StringProperty wednesdayHours =
            new SimpleStringProperty("08:00 AM - 08:00 PM");

    private final StringProperty thursdayHours =
            new SimpleStringProperty("08:00 AM - 08:00 PM");

    private final StringProperty fridayHours =
            new SimpleStringProperty("08:00 AM - 08:00 PM");

    private final StringProperty saturdayHours =
            new SimpleStringProperty("09:00 AM - 04:00 PM");

    private final StringProperty sundayHours =
            new SimpleStringProperty("Emergency Only");

    private final BooleanProperty mondayActive =
            new SimpleBooleanProperty(true);

    private final BooleanProperty tuesdayActive =
            new SimpleBooleanProperty(true);

    private final BooleanProperty wednesdayActive =
            new SimpleBooleanProperty(true);

    private final BooleanProperty thursdayActive =
            new SimpleBooleanProperty(true);

    private final BooleanProperty fridayActive =
            new SimpleBooleanProperty(true);

    private final BooleanProperty saturdayActive =
            new SimpleBooleanProperty(true);

    private final BooleanProperty sundayActive =
            new SimpleBooleanProperty(false);

    // =========================================================
    // EMERGENCY / FACILITY SERVICES
    // =========================================================

    private final BooleanProperty ambulance24x7 =
            new SimpleBooleanProperty(false);

    private final BooleanProperty emergencyWard24x7 =
            new SimpleBooleanProperty(false);

    private final BooleanProperty traumaUnit =
            new SimpleBooleanProperty(false);

    // =========================================================
    // INTERNATIONAL SERVICES
    // =========================================================

    private final BooleanProperty medicalTourismAvailable =
            new SimpleBooleanProperty(false);

    private final BooleanProperty internationalDeskAvailable =
            new SimpleBooleanProperty(false);

    private final BooleanProperty airportPickupAvailable =
            new SimpleBooleanProperty(false);

    private final StringProperty primaryLanguage =
            new SimpleStringProperty("English");

    // =========================================================
    // HOSPITAL IMAGES
    // =========================================================

    private final StringProperty hospitalFrontImage =
            new SimpleStringProperty("");

    private final StringProperty receptionImage =
            new SimpleStringProperty("");

    private final StringProperty emergencyImage =
            new SimpleStringProperty("");

    // =========================================================
    // HOSPITAL DOCUMENTS
    // =========================================================

    private final StringProperty hospitalRegistrationDocument =
            new SimpleStringProperty("");

    private final StringProperty medicalLicenseDocument =
            new SimpleStringProperty("");

    private final StringProperty nabhAccreditationDocument =
            new SimpleStringProperty("");

    // =========================================================
    // ADMINISTRATOR SETTINGS
    // =========================================================

    private final StringProperty adminName =
            new SimpleStringProperty("");

    private final StringProperty adminEmail =
            new SimpleStringProperty("");

    private final StringProperty notificationPreference =
            new SimpleStringProperty("All Notifications");

    private final BooleanProperty emailNotifications =
            new SimpleBooleanProperty(true);

    private final BooleanProperty securityAlerts =
            new SimpleBooleanProperty(true);

    // =========================================================
    // UID
    // =========================================================

    public String getUid() {
        return uid.get();
    }

    public void setUid(String value) {
        uid.set(normalize(value));
    }

    public StringProperty uidProperty() {
        return uid;
    }

    // =========================================================
    // BASIC HOSPITAL INFORMATION
    // =========================================================

    public String getHospitalName() {
        return hospitalName.get();
    }

    public void setHospitalName(String value) {
        hospitalName.set(normalize(value));
    }

    public StringProperty hospitalNameProperty() {
        return hospitalName;
    }

    public String getRegistrationNumber() {
        return registrationNumber.get();
    }

    public void setRegistrationNumber(String value) {
        registrationNumber.set(normalize(value));
    }

    public StringProperty registrationNumberProperty() {
        return registrationNumber;
    }

    public String getHospitalType() {
        return hospitalType.get();
    }

    public void setHospitalType(String value) {
        hospitalType.set(normalize(value));
    }

    public StringProperty hospitalTypeProperty() {
        return hospitalType;
    }

    public String getEstablishedYear() {
        return establishedYear.get();
    }

    public void setEstablishedYear(String value) {
        establishedYear.set(normalize(value));
    }

    public StringProperty establishedYearProperty() {
        return establishedYear;
    }

    // =========================================================
    // CONTACT INFORMATION
    // =========================================================

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public void setPhoneNumber(String value) {
        phoneNumber.set(normalize(value));
    }

    public StringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress.get();
    }

    public void setEmailAddress(String value) {
        emailAddress.set(normalize(value));
    }

    public StringProperty emailAddressProperty() {
        return emailAddress;
    }

    public String getWebsite() {
        return website.get();
    }

    public void setWebsite(String value) {
        website.set(normalize(value));
    }

    public StringProperty websiteProperty() {
        return website;
    }

    public String getCity() {
        return city.get();
    }

    public void setCity(String value) {
        city.set(normalize(value));
    }

    public StringProperty cityProperty() {
        return city;
    }

    public String getState() {
        return state.get();
    }

    public void setState(String value) {
        state.set(normalize(value));
    }

    public StringProperty stateProperty() {
        return state;
    }

    public String getPostalCode() {
        return postalCode.get();
    }

    public void setPostalCode(String value) {
        postalCode.set(normalize(value));
    }

    public StringProperty postalCodeProperty() {
        return postalCode;
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String value) {
        address.set(normalize(value));
    }

    public StringProperty addressProperty() {
        return address;
    }

    // =========================================================
    // OPERATING HOURS
    // =========================================================

    public String getMondayHours() {
        return mondayHours.get();
    }

    public void setMondayHours(String value) {
        mondayHours.set(normalize(value));
    }

    public StringProperty mondayHoursProperty() {
        return mondayHours;
    }

    public String getTuesdayHours() {
        return tuesdayHours.get();
    }

    public void setTuesdayHours(String value) {
        tuesdayHours.set(normalize(value));
    }

    public StringProperty tuesdayHoursProperty() {
        return tuesdayHours;
    }

    public String getWednesdayHours() {
        return wednesdayHours.get();
    }

    public void setWednesdayHours(String value) {
        wednesdayHours.set(normalize(value));
    }

    public StringProperty wednesdayHoursProperty() {
        return wednesdayHours;
    }

    public String getThursdayHours() {
        return thursdayHours.get();
    }

    public void setThursdayHours(String value) {
        thursdayHours.set(normalize(value));
    }

    public StringProperty thursdayHoursProperty() {
        return thursdayHours;
    }

    public String getFridayHours() {
        return fridayHours.get();
    }

    public void setFridayHours(String value) {
        fridayHours.set(normalize(value));
    }

    public StringProperty fridayHoursProperty() {
        return fridayHours;
    }

    public String getSaturdayHours() {
        return saturdayHours.get();
    }

    public void setSaturdayHours(String value) {
        saturdayHours.set(normalize(value));
    }

    public StringProperty saturdayHoursProperty() {
        return saturdayHours;
    }

    public String getSundayHours() {
        return sundayHours.get();
    }

    public void setSundayHours(String value) {
        sundayHours.set(normalize(value));
    }

    public StringProperty sundayHoursProperty() {
        return sundayHours;
    }

    // =========================================================
    // OPERATING DAYS
    // =========================================================

    public boolean isMondayActive() {
        return mondayActive.get();
    }

    public void setMondayActive(boolean value) {
        mondayActive.set(value);
    }

    public BooleanProperty mondayActiveProperty() {
        return mondayActive;
    }

    public boolean isTuesdayActive() {
        return tuesdayActive.get();
    }

    public void setTuesdayActive(boolean value) {
        tuesdayActive.set(value);
    }

    public BooleanProperty tuesdayActiveProperty() {
        return tuesdayActive;
    }

    public boolean isWednesdayActive() {
        return wednesdayActive.get();
    }

    public void setWednesdayActive(boolean value) {
        wednesdayActive.set(value);
    }

    public BooleanProperty wednesdayActiveProperty() {
        return wednesdayActive;
    }

    public boolean isThursdayActive() {
        return thursdayActive.get();
    }

    public void setThursdayActive(boolean value) {
        thursdayActive.set(value);
    }

    public BooleanProperty thursdayActiveProperty() {
        return thursdayActive;
    }

    public boolean isFridayActive() {
        return fridayActive.get();
    }

    public void setFridayActive(boolean value) {
        fridayActive.set(value);
    }

    public BooleanProperty fridayActiveProperty() {
        return fridayActive;
    }

    public boolean isSaturdayActive() {
        return saturdayActive.get();
    }

    public void setSaturdayActive(boolean value) {
        saturdayActive.set(value);
    }

    public BooleanProperty saturdayActiveProperty() {
        return saturdayActive;
    }

    public boolean isSundayActive() {
        return sundayActive.get();
    }

    public void setSundayActive(boolean value) {
        sundayActive.set(value);
    }

    public BooleanProperty sundayActiveProperty() {
        return sundayActive;
    }

    // =========================================================
    // EMERGENCY / FACILITY SERVICES
    // =========================================================

    public boolean isAmbulance24x7() {
        return ambulance24x7.get();
    }

    public void setAmbulance24x7(boolean value) {
        ambulance24x7.set(value);
    }

    public BooleanProperty ambulance24x7Property() {
        return ambulance24x7;
    }

    public boolean isEmergencyWard24x7() {
        return emergencyWard24x7.get();
    }

    public void setEmergencyWard24x7(boolean value) {
        emergencyWard24x7.set(value);
    }

    public BooleanProperty emergencyWard24x7Property() {
        return emergencyWard24x7;
    }

    public boolean isTraumaUnit() {
        return traumaUnit.get();
    }

    public void setTraumaUnit(boolean value) {
        traumaUnit.set(value);
    }

    public BooleanProperty traumaUnitProperty() {
        return traumaUnit;
    }

    // =========================================================
    // INTERNATIONAL SERVICES
    // =========================================================

    public boolean isMedicalTourismAvailable() {
        return medicalTourismAvailable.get();
    }

    public void setMedicalTourismAvailable(boolean value) {
        medicalTourismAvailable.set(value);
    }

    public BooleanProperty medicalTourismAvailableProperty() {
        return medicalTourismAvailable;
    }

    public boolean isInternationalDeskAvailable() {
        return internationalDeskAvailable.get();
    }

    public void setInternationalDeskAvailable(boolean value) {
        internationalDeskAvailable.set(value);
    }

    public BooleanProperty internationalDeskAvailableProperty() {
        return internationalDeskAvailable;
    }

    public boolean isAirportPickupAvailable() {
        return airportPickupAvailable.get();
    }

    public void setAirportPickupAvailable(boolean value) {
        airportPickupAvailable.set(value);
    }

    public BooleanProperty airportPickupAvailableProperty() {
        return airportPickupAvailable;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage.get();
    }

    public void setPrimaryLanguage(String value) {
        primaryLanguage.set(
                value == null || value.trim().isEmpty()
                        ? "English"
                        : value.trim()
        );
    }

    public StringProperty primaryLanguageProperty() {
        return primaryLanguage;
    }

    // =========================================================
    // HOSPITAL IMAGES
    // =========================================================

    public String getHospitalFrontImage() {
        return hospitalFrontImage.get();
    }

    public void setHospitalFrontImage(String value) {
        hospitalFrontImage.set(normalize(value));
    }

    public StringProperty hospitalFrontImageProperty() {
        return hospitalFrontImage;
    }

    public String getReceptionImage() {
        return receptionImage.get();
    }

    public void setReceptionImage(String value) {
        receptionImage.set(normalize(value));
    }

    public StringProperty receptionImageProperty() {
        return receptionImage;
    }

    public String getEmergencyImage() {
        return emergencyImage.get();
    }

    public void setEmergencyImage(String value) {
        emergencyImage.set(normalize(value));
    }

    public StringProperty emergencyImageProperty() {
        return emergencyImage;
    }

    // =========================================================
    // HOSPITAL DOCUMENTS
    // =========================================================

    public String getHospitalRegistrationDocument() {
        return hospitalRegistrationDocument.get();
    }

    public void setHospitalRegistrationDocument(String value) {
        hospitalRegistrationDocument.set(normalize(value));
    }

    public StringProperty hospitalRegistrationDocumentProperty() {
        return hospitalRegistrationDocument;
    }

    public String getMedicalLicenseDocument() {
        return medicalLicenseDocument.get();
    }

    public void setMedicalLicenseDocument(String value) {
        medicalLicenseDocument.set(normalize(value));
    }

    public StringProperty medicalLicenseDocumentProperty() {
        return medicalLicenseDocument;
    }

    public String getNabhAccreditationDocument() {
        return nabhAccreditationDocument.get();
    }

    public void setNabhAccreditationDocument(String value) {
        nabhAccreditationDocument.set(normalize(value));
    }

    public StringProperty nabhAccreditationDocumentProperty() {
        return nabhAccreditationDocument;
    }

    // =========================================================
    // ADMINISTRATOR SETTINGS
    // =========================================================

    public String getAdminName() {
        return adminName.get();
    }

    public void setAdminName(String value) {
        adminName.set(normalize(value));
    }

    public StringProperty adminNameProperty() {
        return adminName;
    }

    public String getAdminEmail() {
        return adminEmail.get();
    }

    public void setAdminEmail(String value) {
        adminEmail.set(normalize(value));
    }

    public StringProperty adminEmailProperty() {
        return adminEmail;
    }

    public String getNotificationPreference() {
        return notificationPreference.get();
    }

    public void setNotificationPreference(String value) {
        notificationPreference.set(
                value == null || value.trim().isEmpty()
                        ? "All Notifications"
                        : value.trim()
        );
    }

    public StringProperty notificationPreferenceProperty() {
        return notificationPreference;
    }

    public boolean isEmailNotifications() {
        return emailNotifications.get();
    }

    public void setEmailNotifications(boolean value) {
        emailNotifications.set(value);
    }

    public BooleanProperty emailNotificationsProperty() {
        return emailNotifications;
    }

    public boolean isSecurityAlerts() {
        return securityAlerts.get();
    }

    public void setSecurityAlerts(boolean value) {
        securityAlerts.set(value);
    }

    public BooleanProperty securityAlertsProperty() {
        return securityAlerts;
    }

    // =========================================================
    // HELPER
    // =========================================================

    private static String normalize(String value) {
        return value == null ? "" : value;
    }
}