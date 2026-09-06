package com.healthsphere.model;

public class TravelSupportRequest {

    private String requestId;
    private String patientId;
    private String patientName;
    private String patientEmail;

    private String medicalTourismRequestId;
    private String hospitalId;
    private String hospitalName;
    private String doctorId;
    private String doctorName;
    private String treatmentName;

    private String serviceType; // AIRPORT_PICKUP, ACCOMMODATION, LOCAL_TRANSPORTATION, LANGUAGE_ASSISTANCE, TRAVEL_COORDINATION, EMERGENCY_ASSISTANCE
    private String travelDate;
    private String arrivalDate;
    private String departureDate;
    private String arrivalLocation;
    private String accommodationPreference;
    private int numberOfTravelers = 1;
    private String preferredLanguage = "English";
    private String contactPhone;
    private String additionalRequirements;

    private String status = "PENDING"; // PENDING, UNDER_REVIEW, APPROVED, REJECTED, COMPLETED, CANCELLED
    private String hospitalNotes;
    private String requestDate;
    private String updatedAt;

    public TravelSupportRequest() {
        // Required for Firestore deserialization
    }

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

    public String getMedicalTourismRequestId() {
        return medicalTourismRequestId;
    }

    public void setMedicalTourismRequestId(String medicalTourismRequestId) {
        this.medicalTourismRequestId = medicalTourismRequestId;
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

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(String travelDate) {
        this.travelDate = travelDate;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getArrivalLocation() {
        return arrivalLocation;
    }

    public void setArrivalLocation(String arrivalLocation) {
        this.arrivalLocation = arrivalLocation;
    }

    public String getAccommodationPreference() {
        return accommodationPreference;
    }

    public void setAccommodationPreference(String accommodationPreference) {
        this.accommodationPreference = accommodationPreference;
    }

    public int getNumberOfTravelers() {
        return numberOfTravelers;
    }

    public void setNumberOfTravelers(int numberOfTravelers) {
        this.numberOfTravelers = numberOfTravelers;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getAdditionalRequirements() {
        return additionalRequirements;
    }

    public void setAdditionalRequirements(String additionalRequirements) {
        this.additionalRequirements = additionalRequirements;
    }

    public String getDetails() {
        if (additionalRequirements != null && !additionalRequirements.isBlank()) {
            return additionalRequirements;
        }
        StringBuilder sb = new StringBuilder();
        if (arrivalLocation != null && !arrivalLocation.isBlank()) sb.append("Location: ").append(arrivalLocation).append(". ");
        if (accommodationPreference != null && !accommodationPreference.isBlank()) sb.append("Hotel: ").append(accommodationPreference).append(". ");
        if (preferredLanguage != null && !preferredLanguage.isBlank()) sb.append("Lang: ").append(preferredLanguage).append(". ");
        return sb.length() > 0 ? sb.toString() : "Standard Support Requested";
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

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
