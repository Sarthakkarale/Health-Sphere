package com.healthsphere.model;

public class HospitalDoctorDetails {

    private HospitalDoctor hospitalDoctor;
    private DoctorProfile doctorProfile;

    public HospitalDoctorDetails() {
    }

    public HospitalDoctorDetails(
            HospitalDoctor hospitalDoctor,
            DoctorProfile doctorProfile) {

        this.hospitalDoctor = hospitalDoctor;
        this.doctorProfile = doctorProfile;
    }

    public HospitalDoctor getHospitalDoctor() {
        return hospitalDoctor;
    }

    public void setHospitalDoctor(
            HospitalDoctor hospitalDoctor) {

        this.hospitalDoctor = hospitalDoctor;
    }

    public DoctorProfile getDoctorProfile() {
        return doctorProfile;
    }

    public void setDoctorProfile(
            DoctorProfile doctorProfile) {

        this.doctorProfile = doctorProfile;
    }

    public String getDoctorId() {

        return hospitalDoctor != null
                ? hospitalDoctor.getDoctorId()
                : null;
    }

    public String getAssociationId() {

        return hospitalDoctor != null
                ? hospitalDoctor.getAssociationId()
                : null;
    }

    public String getDepartmentId() {

        return hospitalDoctor != null
                ? hospitalDoctor.getDepartmentId()
                : null;
    }

    public String getQualification() {

        return hospitalDoctor != null
                ? hospitalDoctor.getQualification()
                : null;
    }

    public String getStatus() {

        return hospitalDoctor != null
                ? hospitalDoctor.getStatus()
                : null;
    }

    public boolean isActive() {

        return hospitalDoctor != null
                && hospitalDoctor.isActive();
    }

    public String getFirstName() {

        return doctorProfile != null
                ? doctorProfile.getFirstName()
                : "";
    }

    public String getLastName() {

        return doctorProfile != null
                ? doctorProfile.getLastName()
                : "";
    }

    public String getFullName() {

        String firstName = getFirstName();
        String lastName = getLastName();

        return (firstName + " " + lastName).trim();
    }

    public String getEmail() {

        return doctorProfile != null
                ? doctorProfile.getEmail()
                : "";
    }

    public String getPhone() {

        return doctorProfile != null
                ? doctorProfile.getPhone()
                : "";
    }

    public String getSpecialization() {

        return doctorProfile != null
                ? doctorProfile.getSpecialization()
                : "";
    }

    public String getRegistrationNumber() {

        return doctorProfile != null
                ? doctorProfile.getRegistrationNumber()
                : "";
    }

    public String getExperience() {

        return doctorProfile != null
                ? doctorProfile.getExperience()
                : "";
    }

    @Override
    public String toString() {

        return "HospitalDoctorDetails{" +
                "associationId='" +
                getAssociationId() + '\'' +
                ", doctorId='" +
                getDoctorId() + '\'' +
                ", name='" +
                getFullName() + '\'' +
                ", departmentId='" +
                getDepartmentId() + '\'' +
                ", qualification='" +
                getQualification() + '\'' +
                ", status='" +
                getStatus() + '\'' +
                '}';
    }
}