package com.healthsphere.model;

import java.util.ArrayList;
import java.util.List;

public class MedicalTourismRequest {

    private String requestId;
    private String patientId;
    private String patientName;
    private String patientEmail;

    private String hospitalId;
    private String hospitalName;

    private String doctorId;
    private String doctorName;

    private String treatmentName;
    private String preferredLocation;
    private double minimumBudget;
    private double maximumBudget;
    private String preferredDate;
    private String patientType; // International, Outstation, Local

    private String additionalRequirements;
    private boolean accommodationRequired;
    private boolean airportAssistanceRequired;
    private List<String> medicalDocuments = new ArrayList<>();

    private String requestDate;
    private String status = "PENDING"; // PENDING, UNDER_REVIEW, ACCEPTED, REJECTED, MORE_INFORMATION_REQUIRED, APPOINTMENT_SCHEDULED, TREATMENT_COMPLETED
    private String hospitalNotes;
    private String appointmentId;

    public MedicalTourismRequest() {
        // Required for Firestore deserialization
    }

    public MedicalTourismRequest(
            String requestId,
            String patientId,
            String patientName,
            String patientEmail,
            String hospitalId,
            String hospitalName,
            String doctorId,
            String doctorName,
            String treatmentName,
            String preferredLocation,
            double minimumBudget,
            double maximumBudget,
            String preferredDate,
            String patientType,
            String additionalRequirements,
            boolean accommodationRequired,
            boolean airportAssistanceRequired,
            List<String> medicalDocuments,
            String requestDate,
            String status) {

        this.requestId = requestId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientEmail = patientEmail;
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.treatmentName = treatmentName;
        this.preferredLocation = preferredLocation;
        this.minimumBudget = minimumBudget;
        this.maximumBudget = maximumBudget;
        this.preferredDate = preferredDate;
        this.patientType = patientType;
        this.additionalRequirements = additionalRequirements;
        this.accommodationRequired = accommodationRequired;
        this.airportAssistanceRequired = airportAssistanceRequired;
        if (medicalDocuments != null) {
            this.medicalDocuments = medicalDocuments;
        }
        this.requestDate = requestDate;
        this.status = status != null ? status : "PENDING";
    }

    // Getters and Setters

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public String getPreferredLocation() {
        return preferredLocation;
    }

    public void setPreferredLocation(String preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    public double getMinimumBudget() {
        return minimumBudget;
    }

    public void setMinimumBudget(double minimumBudget) {
        this.minimumBudget = minimumBudget;
    }

    public double getMaximumBudget() {
        return maximumBudget;
    }

    public void setMaximumBudget(double maximumBudget) {
        this.maximumBudget = maximumBudget;
    }

    public String getPreferredDate() {
        return preferredDate;
    }

    public void setPreferredDate(String preferredDate) {
        this.preferredDate = preferredDate;
    }

    public String getPatientType() {
        return patientType;
    }

    public void setPatientType(String patientType) {
        this.patientType = patientType;
    }

    public String getAdditionalRequirements() {
        return additionalRequirements;
    }

    public void setAdditionalRequirements(String additionalRequirements) {
        this.additionalRequirements = additionalRequirements;
    }

    public boolean isAccommodationRequired() {
        return accommodationRequired;
    }

    public void setAccommodationRequired(boolean accommodationRequired) {
        this.accommodationRequired = accommodationRequired;
    }

    public boolean isAirportAssistanceRequired() {
        return airportAssistanceRequired;
    }

    public void setAirportAssistanceRequired(boolean airportAssistanceRequired) {
        this.airportAssistanceRequired = airportAssistanceRequired;
    }

    public List<String> getMedicalDocuments() {
        return medicalDocuments;
    }

    public void setMedicalDocuments(List<String> medicalDocuments) {
        this.medicalDocuments = medicalDocuments != null ? medicalDocuments : new ArrayList<>();
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHospitalNotes() {
        return hospitalNotes;
    }

    public void setHospitalNotes(String hospitalNotes) {
        this.hospitalNotes = hospitalNotes;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }
}
