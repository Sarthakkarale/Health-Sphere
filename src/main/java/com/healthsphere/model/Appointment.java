package com.healthsphere.model;

import com.google.cloud.firestore.annotation.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Appointment {

    private String appointmentId;

    private String patientUid;
    private String patientName;

    private String bookingType;

    private String doctorUid;
    private String doctorName;

    private String hospitalId;
    private String hospitalName;
    private String hospital;

    private String specialty;

    private String appointmentDate;
    private String appointmentTime;

    private String reason;

    private String status;

    private String createdAt;
    private String updatedAt;

    // ============================================================
    // FIRESTORE CONSTRUCTOR
    // ============================================================

    public Appointment() {
    }

    // ============================================================
    // FULL CONSTRUCTOR
    // ============================================================

    public Appointment(
            String appointmentId,
            String patientUid,
            String patientName,
            String bookingType,
            String doctorUid,
            String doctorName,
            String hospitalId,
            String hospitalName,
            String specialty,
            String appointmentDate,
            String appointmentTime,
            String reason,
            String status,
            String createdAt,
            String updatedAt) {

        this.appointmentId = appointmentId;

        this.patientUid = patientUid;
        this.patientName = patientName;

        this.bookingType = bookingType;

        this.doctorUid = doctorUid;
        this.doctorName = doctorName;

        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;

        this.specialty = specialty;

        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;

        this.reason = reason;

        this.status = status;

        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ============================================================
    // GETTERS
    // ============================================================

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getPatientUid() {
        return patientUid;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getBookingType() {
        return bookingType;
    }

    public String getDoctorUid() {
        return doctorUid;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public String getHospitalName() {
        return hospitalName != null ? hospitalName : hospital;
    }

    public String getHospital() {
        return hospital != null ? hospital : hospitalName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    // ============================================================
    // SETTERS
    // ============================================================

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setPatientUid(String patientUid) {
        this.patientUid = patientUid;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public void setDoctorUid(String doctorUid) {
        this.doctorUid = doctorUid;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
        if (this.hospital == null || this.hospital.trim().isEmpty()) {
            this.hospital = hospitalName;
        }
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
        if (this.hospitalName == null || this.hospitalName.trim().isEmpty()) {
            this.hospitalName = hospital;
        }
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}