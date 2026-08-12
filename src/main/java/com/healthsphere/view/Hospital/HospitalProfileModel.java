package com.healthsphere.view.Hospital;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class HospitalProfileModel {

    // Hospital Information
    private final StringProperty hospitalName = new SimpleStringProperty("CityCare Multispeciality Hospital");
    private final StringProperty registrationNumber = new SimpleStringProperty("HSP-2026-00124");
    private final StringProperty hospitalType = new SimpleStringProperty("Multispeciality Hospital");
    private final StringProperty establishedYear = new SimpleStringProperty("2008");

    // Contact Details & Address
    private final StringProperty phoneNumber = new SimpleStringProperty("+91 20 4567 8900");
    private final StringProperty emailAddress = new SimpleStringProperty("contact@citycarehospital.com");
    private final StringProperty website = new SimpleStringProperty("www.citycarehospital.com");
    private final StringProperty city = new SimpleStringProperty("Pune");
    private final StringProperty state = new SimpleStringProperty("Maharashtra");
    private final StringProperty postalCode = new SimpleStringProperty("411001");
    private final StringProperty address = new SimpleStringProperty("123 Healthcare Avenue, Central Business District");

    // Emergency Services
    private final BooleanProperty ambulance24x7 = new SimpleBooleanProperty(true);
    private final BooleanProperty emergencyWard24x7 = new SimpleBooleanProperty(true);
    private final BooleanProperty traumaUnit = new SimpleBooleanProperty(true);

    // Medical Tourism
    private final BooleanProperty medicalTourismAvailable = new SimpleBooleanProperty(true);
    private final BooleanProperty internationalDeskAvailable = new SimpleBooleanProperty(true);
    private final BooleanProperty airportPickupAvailable = new SimpleBooleanProperty(false);
    private final StringProperty primaryLanguage = new SimpleStringProperty("English");

    // Admin & Account Settings
    private final StringProperty adminName = new SimpleStringProperty("Hospital Administrator");
    private final StringProperty adminEmail = new SimpleStringProperty("admin@citycarehospital.com");
    private final StringProperty notificationPreference = new SimpleStringProperty("All Notifications");
    private final BooleanProperty emailNotifications = new SimpleBooleanProperty(true);
    private final BooleanProperty securityAlerts = new SimpleBooleanProperty(true);

    // Getters for Properties
    public StringProperty hospitalNameProperty() { return hospitalName; }
    public StringProperty registrationNumberProperty() { return registrationNumber; }
    public StringProperty hospitalTypeProperty() { return hospitalType; }
    public StringProperty establishedYearProperty() { return establishedYear; }

    public StringProperty phoneNumberProperty() { return phoneNumber; }
    public StringProperty emailAddressProperty() { return emailAddress; }
    public StringProperty websiteProperty() { return website; }
    public StringProperty cityProperty() { return city; }
    public StringProperty stateProperty() { return state; }
    public StringProperty postalCodeProperty() { return postalCode; }
    public StringProperty addressProperty() { return address; }

    public BooleanProperty ambulance24x7Property() { return ambulance24x7; }
    public BooleanProperty emergencyWard24x7Property() { return emergencyWard24x7; }
    public BooleanProperty traumaUnitProperty() { return traumaUnit; }

    public BooleanProperty medicalTourismAvailableProperty() { return medicalTourismAvailable; }
    public BooleanProperty internationalDeskAvailableProperty() { return internationalDeskAvailable; }
    public BooleanProperty airportPickupAvailableProperty() { return airportPickupAvailable; }
    public StringProperty primaryLanguageProperty() { return primaryLanguage; }

    public StringProperty adminNameProperty() { return adminName; }
    public StringProperty adminEmailProperty() { return adminEmail; }
    public StringProperty notificationPreferenceProperty() { return notificationPreference; }
    public BooleanProperty emailNotificationsProperty() { return emailNotifications; }
    public BooleanProperty securityAlertsProperty() { return securityAlerts; }
}