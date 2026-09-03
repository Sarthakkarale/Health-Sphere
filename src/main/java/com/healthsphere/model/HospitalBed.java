package com.healthsphere.model;

public class HospitalBed {

    public enum BedStatus {
        AVAILABLE,
        OCCUPIED,
        RESERVED,
        MAINTENANCE
    }

    private String bedId;
    private String hospitalId;
    private String wardId;
    private String bedNumber;
    private String bedType;
    private BedStatus status;
    private String patientId;
    private boolean active;

    // Required by Firestore
    public HospitalBed() {
    }

    public HospitalBed(
            String bedId,
            String hospitalId,
            String wardId,
            String bedNumber,
            String bedType,
            BedStatus status,
            String patientId,
            boolean active
    ) {
        this.bedId = bedId;
        this.hospitalId = hospitalId;
        this.wardId = wardId;
        this.bedNumber = bedNumber;
        this.bedType = bedType;
        this.status = status;
        this.patientId = patientId;
        this.active = active;
    }

    public String getBedId() {
        return bedId;
    }

    public void setBedId(String bedId) {
        this.bedId = bedId;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getWardId() {
        return wardId;
    }

    public void setWardId(String wardId) {
        this.wardId = wardId;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public void setBedNumber(String bedNumber) {
        this.bedNumber = bedNumber;
    }

    public String getBedType() {
        return bedType;
    }

    public void setBedType(String bedType) {
        this.bedType = bedType;
    }

    public BedStatus getStatus() {
        return status;
    }

    // Keep ONLY this setStatus()
    public void setStatus(BedStatus status) {
        this.status = status;
    }

    public String getStatusName() {
        return status != null ? status.name() : "";
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}