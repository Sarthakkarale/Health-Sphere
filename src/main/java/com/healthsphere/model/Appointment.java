package com.healthsphere.model;

public class Appointment {

    private String appointmentId;

    private String patientUid;
    private String patientName;

    // DOCTOR or HOSPITAL
    private String bookingType;

    // =========================================================
    // DOCTOR
    // =========================================================

    private String doctorUid;
    private String doctorName;

    // =========================================================
    // HOSPITAL
    // =========================================================

    private String hospitalId;
    private String hospitalName;

    // =========================================================
    // APPOINTMENT DETAILS
    // =========================================================

    private String specialty;
    private String appointmentDate;
    private String appointmentTime;
    private String reason;

    // PENDING / PENDING_ASSIGNMENT / CONFIRMED / CANCELLED
    private String status;

    private String createdAt;
    private String updatedAt;

    // =========================================================
    // EMPTY CONSTRUCTOR
    // =========================================================

    public Appointment() {
    }

    // =========================================================
    // APPOINTMENT ID
    // =========================================================

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    // =========================================================
    // PATIENT
    // =========================================================

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

    // =========================================================
    // BOOKING TYPE
    // =========================================================

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    // =========================================================
    // DOCTOR
    // =========================================================

    public String getDoctorUid() {
        return doctorUid;
    }

    public void setDoctorUid(String doctorUid) {
        this.doctorUid = doctorUid;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    // =========================================================
    // HOSPITAL
    // =========================================================

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

    /*
     * Backward-compatible getter.
     *
     * Your existing Appointments.java is currently calling:
     *
     * appointment.getHospital()
     *
     * The actual model field is hospitalName.
     *
     * Keeping this method prevents the existing Appointments.java
     * from producing compilation errors.
     */
    public String getHospital() {
        return hospitalName;
    }

    // =========================================================
    // SPECIALTY
    // =========================================================

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    // =========================================================
    // DATE
    // =========================================================

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    // =========================================================
    // TIME
    // =========================================================

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    // =========================================================
    // REASON
    // =========================================================

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    // =========================================================
    // STATUS
    // =========================================================

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // =========================================================
    // CREATED AT
    // =========================================================

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // =========================================================
    // UPDATED AT
    // =========================================================

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}