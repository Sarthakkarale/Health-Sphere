package com.healthsphere.model;

/**
 * Represents a ward belonging to a hospital.
 *
 * Hospital identity is represented by hospitalId, which refers
 * to the authenticated hospital user's UID.
 *
 * Bed availability should be calculated from HospitalBed records
 * rather than permanently stored in this model.
 */
public class HospitalWard {

    private String wardId;
    private String hospitalId;
    private String name;
    private String category;
    private int capacity;
    private boolean is24x7;
    private boolean active;

    /**
     * Required by Firestore / object mapping.
     */
    public HospitalWard() {
    }

    /**
     * Creates a hospital ward.
     */
    public HospitalWard(
            String wardId,
            String hospitalId,
            String name,
            String category,
            int capacity,
            boolean is24x7,
            boolean active) {

        this.wardId = wardId;
        this.hospitalId = hospitalId;
        this.name = name;
        this.category = category;
        this.capacity = capacity;
        this.is24x7 = is24x7;
        this.active = active;
    }

    // =========================================================
    // GETTERS
    // =========================================================

    public String getWardId() {
        return wardId;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean is24x7() {
        return is24x7;
    }

    public boolean isActive() {
        return active;
    }

    // =========================================================
    // SETTERS
    // =========================================================

    public void setWardId(String wardId) {
        this.wardId = wardId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void set24x7(boolean is24x7) {
        this.is24x7 = is24x7;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "HospitalWard{" +
                "wardId='" + wardId + '\'' +
                ", hospitalId='" + hospitalId + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", capacity=" + capacity +
                ", is24x7=" + is24x7 +
                ", active=" + active +
                '}';
    }
}