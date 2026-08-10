package com.healthsphere.model;

/**
 * Contains information specific to a hospital.
 */
public class HospitalProfile {

    private String userId;
    private String hospitalName;
    private String registrationNumber;
    private String hospitalType;
    private int numberOfBeds;
    private String address;
    private String representativeName;

    public HospitalProfile() {
    }

    public HospitalProfile(
            String userId,
            String hospitalName,
            String registrationNumber,
            String hospitalType,
            int numberOfBeds,
            String address,
            String representativeName) {

        this.userId = userId;
        this.hospitalName = hospitalName;
        this.registrationNumber = registrationNumber;
        this.hospitalType = hospitalType;
        this.numberOfBeds = numberOfBeds;
        this.address = address;
        this.representativeName = representativeName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getHospitalType() {
        return hospitalType;
    }

    public void setHospitalType(String hospitalType) {
        this.hospitalType = hospitalType;
    }

    public int getNumberOfBeds() {
        return numberOfBeds;
    }

    public void setNumberOfBeds(int numberOfBeds) {
        this.numberOfBeds = numberOfBeds;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRepresentativeName() {
        return representativeName;
    }

    public void setRepresentativeName(String representativeName) {
        this.representativeName = representativeName;
    }
}