package com.healthsphere.model;

public class Appointment {

    private String appointmentId;
    private String patientUid;
    private String patientName;
    private String doctorName;
    private String specialty;
    private String hospital;
    private String appointmentDate;
    private String appointmentTime;
    private String reason;
    private String status;

    public Appointment() {
    }

    public Appointment(
            String appointmentId,
            String patientUid,
            String patientName,
            String doctorName,
            String specialty,
            String hospital,
            String appointmentDate,
            String appointmentTime,
            String reason,
            String status) {

        this.appointmentId = appointmentId;
        this.patientUid = patientUid;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.specialty = specialty;
        this.hospital = hospital;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.reason = reason;
        this.status = status;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPatientUid() {
        return patientUid;
    }

    public void setPatientUid(String patientUid) {
        this.patientUid = patientUid;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
