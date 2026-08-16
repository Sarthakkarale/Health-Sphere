package com.healthsphere.model;

import java.time.Instant;

public class HospitalVerification {

    private String verificationId;
    private String hospitalId;
    private String hospitalName;
    private String nabhLicenseNumber;
    private double aiOcrMatchScore;
    private String aiOcrResult;
    private String verificationStatus;
    private String documentStatus;
    private String rejectionReason;
    private String verifiedBy;
    private Instant createdAt;
    private Instant updatedAt;

    public HospitalVerification() {
        // Required for Firebase/Firestore deserialization
    }

    public HospitalVerification(
            String verificationId,
            String hospitalId,
            String hospitalName,
            String nabhLicenseNumber,
            double aiOcrMatchScore,
            String aiOcrResult,
            String verificationStatus,
            String documentStatus,
            String rejectionReason,
            String verifiedBy,
            Instant createdAt,
            Instant updatedAt) {

        this.verificationId = verificationId;
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.nabhLicenseNumber = nabhLicenseNumber;
        this.aiOcrMatchScore = aiOcrMatchScore;
        this.aiOcrResult = aiOcrResult;
        this.verificationStatus = verificationStatus;
        this.documentStatus = documentStatus;
        this.rejectionReason = rejectionReason;
        this.verifiedBy = verifiedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
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

    public String getNabhLicenseNumber() {
        return nabhLicenseNumber;
    }

    public void setNabhLicenseNumber(String nabhLicenseNumber) {
        this.nabhLicenseNumber = nabhLicenseNumber;
    }

    public double getAiOcrMatchScore() {
        return aiOcrMatchScore;
    }

    public void setAiOcrMatchScore(double aiOcrMatchScore) {
        this.aiOcrMatchScore = aiOcrMatchScore;
    }

    public String getAiOcrResult() {
        return aiOcrResult;
    }

    public void setAiOcrResult(String aiOcrResult) {
        this.aiOcrResult = aiOcrResult;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(String documentStatus) {
        this.documentStatus = documentStatus;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}