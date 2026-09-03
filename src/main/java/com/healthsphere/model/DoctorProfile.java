
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

    // NEW
    private String rating;

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

    // =========================================================
    // RATING
    // =========================================================

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}

