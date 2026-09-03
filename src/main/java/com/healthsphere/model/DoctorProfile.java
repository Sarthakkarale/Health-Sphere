package com.healthsphere.model;

public class DoctorProfile {

    private String uid;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String registrationNumber;
    private String specialization;
    private String experience;
    private String hospitalAffiliation;
    private String medicalCouncil;

    // ============================================================
    // DOCTOR APPOINTMENT / AVAILABILITY INFORMATION
    // ============================================================

    private String appointmentFee;
    private String appointmentDuration;

    private String consultationType;

    private boolean onlineConsultation;
    private boolean emergencyAvailability;

    private String contactPhone;
    private String contactEmail;
    private String clinicAddress;

    // Monday
    private boolean mondayAvailable;
    private String mondayStartTime;
    private String mondayEndTime;

    // Tuesday
    private boolean tuesdayAvailable;
    private String tuesdayStartTime;
    private String tuesdayEndTime;

    // Wednesday
    private boolean wednesdayAvailable;
    private String wednesdayStartTime;
    private String wednesdayEndTime;

    // Thursday
    private boolean thursdayAvailable;
    private String thursdayStartTime;
    private String thursdayEndTime;

    // Friday
    private boolean fridayAvailable;
    private String fridayStartTime;
    private String fridayEndTime;

    // Saturday
    private boolean saturdayAvailable;
    private String saturdayStartTime;
    private String saturdayEndTime;

    // Sunday
    private boolean sundayAvailable;
    private String sundayStartTime;
    private String sundayEndTime;

    // ============================================================
    // DEFAULT CONSTRUCTOR
    // ============================================================

    public DoctorProfile() {
    }

    // ============================================================
    // EXISTING CONSTRUCTOR
    // ============================================================

    public DoctorProfile(
            String uid,
            String firstName,
            String lastName,
            String email,
            String phone,
            String registrationNumber,
            String specialization,
            String experience,
            String hospitalAffiliation,
            String medicalCouncil) {

        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.registrationNumber = registrationNumber;
        this.specialization = specialization;
        this.experience = experience;
        this.hospitalAffiliation = hospitalAffiliation;
        this.medicalCouncil = medicalCouncil;
    }

    // ============================================================
    // BASIC PROFILE GETTERS / SETTERS
    // ============================================================

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getHospitalAffiliation() {
        return hospitalAffiliation;
    }

    public void setHospitalAffiliation(String hospitalAffiliation) {
        this.hospitalAffiliation = hospitalAffiliation;
    }

    public String getMedicalCouncil() {
        return medicalCouncil;
    }

    public void setMedicalCouncil(String medicalCouncil) {
        this.medicalCouncil = medicalCouncil;
    }

    // ============================================================
    // APPOINTMENT INFORMATION
    // ============================================================

    public String getAppointmentFee() {
        return appointmentFee;
    }

    public void setAppointmentFee(String appointmentFee) {
        this.appointmentFee = appointmentFee;
    }

    public String getAppointmentDuration() {
        return appointmentDuration;
    }

    public void setAppointmentDuration(String appointmentDuration) {
        this.appointmentDuration = appointmentDuration;
    }

    public String getConsultationType() {
        return consultationType;
    }

    public void setConsultationType(String consultationType) {
        this.consultationType = consultationType;
    }

    public boolean isOnlineConsultation() {
        return onlineConsultation;
    }

    public void setOnlineConsultation(boolean onlineConsultation) {
        this.onlineConsultation = onlineConsultation;
    }

    public boolean isEmergencyAvailability() {
        return emergencyAvailability;
    }

    public void setEmergencyAvailability(boolean emergencyAvailability) {
        this.emergencyAvailability = emergencyAvailability;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
    }

    // ============================================================
    // MONDAY
    // ============================================================

    public boolean isMondayAvailable() {
        return mondayAvailable;
    }

    public void setMondayAvailable(boolean mondayAvailable) {
        this.mondayAvailable = mondayAvailable;
    }

    public String getMondayStartTime() {
        return mondayStartTime;
    }

    public void setMondayStartTime(String mondayStartTime) {
        this.mondayStartTime = mondayStartTime;
    }

    public String getMondayEndTime() {
        return mondayEndTime;
    }

    public void setMondayEndTime(String mondayEndTime) {
        this.mondayEndTime = mondayEndTime;
    }

    // ============================================================
    // TUESDAY
    // ============================================================

    public boolean isTuesdayAvailable() {
        return tuesdayAvailable;
    }

    public void setTuesdayAvailable(boolean tuesdayAvailable) {
        this.tuesdayAvailable = tuesdayAvailable;
    }

    public String getTuesdayStartTime() {
        return tuesdayStartTime;
    }

    public void setTuesdayStartTime(String tuesdayStartTime) {
        this.tuesdayStartTime = tuesdayStartTime;
    }

    public String getTuesdayEndTime() {
        return tuesdayEndTime;
    }

    public void setTuesdayEndTime(String tuesdayEndTime) {
        this.tuesdayEndTime = tuesdayEndTime;
    }

    // ============================================================
    // WEDNESDAY
    // ============================================================

    public boolean isWednesdayAvailable() {
        return wednesdayAvailable;
    }

    public void setWednesdayAvailable(boolean wednesdayAvailable) {
        this.wednesdayAvailable = wednesdayAvailable;
    }

    public String getWednesdayStartTime() {
        return wednesdayStartTime;
    }

    public void setWednesdayStartTime(String wednesdayStartTime) {
        this.wednesdayStartTime = wednesdayStartTime;
    }

    public String getWednesdayEndTime() {
        return wednesdayEndTime;
    }

    public void setWednesdayEndTime(String wednesdayEndTime) {
        this.wednesdayEndTime = wednesdayEndTime;
    }

    // ============================================================
    // THURSDAY
    // ============================================================

    public boolean isThursdayAvailable() {
        return thursdayAvailable;
    }

    public void setThursdayAvailable(boolean thursdayAvailable) {
        this.thursdayAvailable = thursdayAvailable;
    }

    public String getThursdayStartTime() {
        return thursdayStartTime;
    }

    public void setThursdayStartTime(String thursdayStartTime) {
        this.thursdayStartTime = thursdayStartTime;
    }

    public String getThursdayEndTime() {
        return thursdayEndTime;
    }

    public void setThursdayEndTime(String thursdayEndTime) {
        this.thursdayEndTime = thursdayEndTime;
    }

    // ============================================================
    // FRIDAY
    // ============================================================

    public boolean isFridayAvailable() {
        return fridayAvailable;
    }

    public void setFridayAvailable(boolean fridayAvailable) {
        this.fridayAvailable = fridayAvailable;
    }

    public String getFridayStartTime() {
        return fridayStartTime;
    }

    public void setFridayStartTime(String fridayStartTime) {
        this.fridayStartTime = fridayStartTime;
    }

    public String getFridayEndTime() {
        return fridayEndTime;
    }

    public void setFridayEndTime(String fridayEndTime) {
        this.fridayEndTime = fridayEndTime;
    }

    // ============================================================
    // SATURDAY
    // ============================================================

    public boolean isSaturdayAvailable() {
        return saturdayAvailable;
    }

    public void setSaturdayAvailable(boolean saturdayAvailable) {
        this.saturdayAvailable = saturdayAvailable;
    }

    public String getSaturdayStartTime() {
        return saturdayStartTime;
    }

    public void setSaturdayStartTime(String saturdayStartTime) {
        this.saturdayStartTime = saturdayStartTime;
    }

    public String getSaturdayEndTime() {
        return saturdayEndTime;
    }

    public void setSaturdayEndTime(String saturdayEndTime) {
        this.saturdayEndTime = saturdayEndTime;
    }

    // ============================================================
    // SUNDAY
    // ============================================================

    public boolean isSundayAvailable() {
        return sundayAvailable;
    }

    public void setSundayAvailable(boolean sundayAvailable) {
        this.sundayAvailable = sundayAvailable;
    }

    public String getSundayStartTime() {
        return sundayStartTime;
    }

    public void setSundayStartTime(String sundayStartTime) {
        this.sundayStartTime = sundayStartTime;
    }

    public String getSundayEndTime() {
        return sundayEndTime;
    }

    public void setSundayEndTime(String sundayEndTime) {
        this.sundayEndTime = sundayEndTime;
    }
}