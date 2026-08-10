package com.healthsphere.model;

/**
 * Contains information specific to a doctor.
 */
public class DoctorProfile {

    private String userId;
    private String medicalLicenseNumber;
    private String medicalCouncil;
    private String specialization;
    private int yearsOfExperience;
    private String hospitalOrClinic;
    private String department;

    public DoctorProfile() {
    }

    public DoctorProfile(
            String userId,
            String medicalLicenseNumber,
            String medicalCouncil,
            String specialization,
            int yearsOfExperience,
            String hospitalOrClinic,
            String department) {

        this.userId = userId;
        this.medicalLicenseNumber = medicalLicenseNumber;
        this.medicalCouncil = medicalCouncil;
        this.specialization = specialization;
        this.yearsOfExperience = yearsOfExperience;
        this.hospitalOrClinic = hospitalOrClinic;
        this.department = department;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMedicalLicenseNumber() {
        return medicalLicenseNumber;
    }

    public void setMedicalLicenseNumber(String medicalLicenseNumber) {
        this.medicalLicenseNumber = medicalLicenseNumber;
    }

    public String getMedicalCouncil() {
        return medicalCouncil;
    }

    public void setMedicalCouncil(String medicalCouncil) {
        this.medicalCouncil = medicalCouncil;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getHospitalOrClinic() {
        return hospitalOrClinic;
    }

    public void setHospitalOrClinic(String hospitalOrClinic) {
        this.hospitalOrClinic = hospitalOrClinic;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}