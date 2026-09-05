
package com.healthsphere.model;

import java.time.Instant;

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

    // Account Balance
    private double accountBalance = 0.00;

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    // Verification information
    private String verificationStatus;
    private String verifiedBy;
    private Instant updatedAt;

    public HospitalProfile() {
        // Required for Firestore deserialization
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



    // ============================================================
    // VERIFICATION STATUS
    // ============================================================

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(
            String verificationStatus) {

        this.verificationStatus = verificationStatus;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}