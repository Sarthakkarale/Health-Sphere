
package com.healthsphere.model;

public class HospitalProfile {

    private String uid;
    private String email;
    private String hospitalName;
    private String registrationNumber;
    private String hospitalType;
    private String beds;
    private String contact;
    private String address;

    // NEW
    private String rating;

    public HospitalProfile() {
    }

    public HospitalProfile(
            String uid,
            String email,
            String hospitalName,
            String registrationNumber,
            String hospitalType,
            String beds,
            String contact,
            String address) {

        this.uid = uid;
        this.email = email;
        this.hospitalName = hospitalName;
        this.registrationNumber = registrationNumber;
        this.hospitalType = hospitalType;
        this.beds = beds;
        this.contact = contact;
        this.address = address;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getBeds() {
        return beds;
    }

    public void setBeds(String beds) {
        this.beds = beds;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

