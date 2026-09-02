package com.healthsphere.model;

public class HospitalDoctor {

    private String associationId;
    private String hospitalId;
    private String doctorId;

    private String departmentId;
    private String qualification;
    private String status;

    private boolean active;

    public HospitalDoctor() {
        // Required by Firestore
    }

    public HospitalDoctor(
            String associationId,
            String hospitalId,
            String doctorId,
            String departmentId,
            String qualification,
            String status,
            boolean active) {

        this.associationId = associationId;
        this.hospitalId = hospitalId;
        this.doctorId = doctorId;
        this.departmentId = departmentId;
        this.qualification = qualification;
        this.status = status;
        this.active = active;
    }

    public String getAssociationId() {
        return associationId;
    }

    public void setAssociationId(String associationId) {
        this.associationId = associationId;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "HospitalDoctor{" +
                "associationId='" + associationId + '\'' +
                ", hospitalId='" + hospitalId + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", departmentId='" + departmentId + '\'' +
                ", qualification='" + qualification + '\'' +
                ", status='" + status + '\'' +
                ", active=" + active +
                '}';
    }
}