package com.healthsphere.model;

/**
 * Represents an individual bed belonging to a hospital ward.
 *
 * A bed is identified by its bedId and belongs to a specific
 * hospital and ward.
 *
 * patientId refers to the UID of the patient occupying the bed.
 * It remains null when the bed is not occupied.
 */
public class HospitalBed {

    /**
     * Possible states of a hospital bed.
     */
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

    /**
     * Required by Firestore / object mapping.
     */
    public HospitalBed() {
    }

    /**
     * Creates a hospital bed.
     */
    public HospitalBed(
            String bedId,
            String hospitalId,
            String wardId,
            String bedNumber,
            String bedType,
            BedStatus status,
            String patientId,
            boolean active) {

        this.bedId = bedId;
        this.hospitalId = hospitalId;
        this.wardId = wardId;
        this.bedNumber = bedNumber;
        this.bedType = bedType;
        this.status = status;
        this.patientId = patientId;
        this.active = active;
    }

    // =========================================================
    // GETTERS
    // =========================================================

    public String getBedId() {
        return bedId;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public String getWardId() {
        return wardId;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public String getBedType() {
        return bedType;
    }

    public BedStatus getStatus() {
        return status;
    }

    public String getPatientId() {
        return patientId;
    }

    public boolean isActive() {
        return active;
    }

    // =========================================================
    // SETTERS
    // =========================================================

    public void setBedId(String bedId) {
        this.bedId = bedId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public void setWardId(String wardId) {
        this.wardId = wardId;
    }

    public void setBedNumber(String bedNumber) {
        this.bedNumber = bedNumber;
    }

    public void setBedType(String bedType) {
        this.bedType = bedType;
    }

    public void setStatus(BedStatus status) {
        this.status = status;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    /**
     * Returns true when this bed is currently occupied.
     */
    public boolean isOccupied() {
        return status == BedStatus.OCCUPIED;
    }

    /**
     * Returns true when this bed is currently available.
     */
    public boolean isAvailable() {
        return status == BedStatus.AVAILABLE;
    }

    /**
     * Returns true when this bed is reserved.
     */
    public boolean isReserved() {
        return status == BedStatus.RESERVED;
    }

    /**
     * Returns true when this bed is under maintenance.
     */
    public boolean isUnderMaintenance() {
        return status == BedStatus.MAINTENANCE;
    }

    /**
     * Marks the bed as available and removes the patient assignment.
     */
    public void makeAvailable() {
        this.status = BedStatus.AVAILABLE;
        this.patientId = null;
    }

    /**
     * Marks the bed as occupied by a patient.
     */
    public void occupy(String patientId) {
        this.status = BedStatus.OCCUPIED;
        this.patientId = patientId;
    }

    /**
     * Reserves the bed.
     */
    public void reserve() {
        this.status = BedStatus.RESERVED;
    }

    /**
     * Places the bed under maintenance.
     */
    public void putUnderMaintenance() {
        this.status = BedStatus.MAINTENANCE;
        this.patientId = null;
    }

    @Override
    public String toString() {
        return "HospitalBed{" +
                "bedId='" + bedId + '\'' +
                ", hospitalId='" + hospitalId + '\'' +
                ", wardId='" + wardId + '\'' +
                ", bedNumber='" + bedNumber + '\'' +
                ", bedType='" + bedType + '\'' +
                ", status=" + status +
                ", patientId='" + patientId + '\'' +
                ", active=" + active +
                '}';
    }
}