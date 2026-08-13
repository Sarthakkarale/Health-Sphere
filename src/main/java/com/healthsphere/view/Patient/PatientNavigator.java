package com.healthsphere.view.Patient;

import javafx.stage.Stage;

public class PatientNavigator {

    private final Stage stage;

    public PatientNavigator(Stage stage) {
        this.stage = stage;
    }

    public void showDashboard() {
        stage.setScene(
                new Dashboard(this).getScene()
        );
        stage.show();
    }

    public void showSearchHospitals() {
        stage.setScene(
                new SearchHospitals(this).getScene()
        );
        stage.show();
    }

    public void showAppointments() {
        stage.setScene(
                new Appointments(this).getScene()
        );
        stage.show();
    }

    public void showBookAppointment() {
        stage.setScene(
                new BookAppointment(this).getScene()
        );
        stage.show();
    }

    public void showHealthPassport() {
        stage.setScene(
                new HealthPassport(this).getScene()
        );
        stage.show();
    }

    public void showMedicalRecords() {
        stage.setScene(
                new MedicalRecords(this).getScene()
        );
        stage.show();
    }

    public void showAIHealthAssistant() {
        stage.setScene(
                new AiHealthAssistant(this).getScene()
        );
        stage.show();
    }

    public void showEmergencyAssistance() {
        stage.setScene(
                new EmergencyAssistance(this).getScene()
        );
        stage.show();
    }

    public void showNotifications() {
        stage.setScene(
                new Notifications(this).getScene()
        );
        stage.show();
    }

    public void showProfileSettings() {
        stage.setScene(
                new ProfileSettings(this).getScene()
        );
        stage.show();
    }
}