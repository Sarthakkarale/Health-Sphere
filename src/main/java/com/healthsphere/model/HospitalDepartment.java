package com.healthsphere.model;

public class HospitalDepartment {

    private String departmentId;
    private String hospitalId;
    private String name;
    private String headDoctorId;
    private String category;
    private boolean is24x7;
    private boolean active;

    // Required by Firestore
    public HospitalDepartment() {
    }

    public HospitalDepartment(
            String departmentId,
            String hospitalId,
            String name,
            String headDoctorId,
            String category,
            boolean is24x7,
            boolean active) {

        this.departmentId = departmentId;
        this.hospitalId = hospitalId;
        this.name = name;
        this.headDoctorId = headDoctorId;
        this.category = category;
        this.is24x7 = is24x7;
        this.active = active;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadDoctorId() {
        return headDoctorId;
    }

    public void setHeadDoctorId(String headDoctorId) {
        this.headDoctorId = headDoctorId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean is24x7() {
        return is24x7;
    }

    public void set24x7(boolean is24x7) {
        this.is24x7 = is24x7;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "HospitalDepartment{" +
                "departmentId='" + departmentId + '\'' +
                ", hospitalId='" + hospitalId + '\'' +
                ", name='" + name + '\'' +
                ", headDoctorId='" + headDoctorId + '\'' +
                ", category='" + category + '\'' +
                ", is24x7=" + is24x7 +
                ", active=" + active +
                '}';
    }
}