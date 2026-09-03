package com.healthsphere.model;

public class Appointment {

    private String appointmentId;

    private String patientUid;
    private String patientName;

<<<<<<< HEAD
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
=======
    private String bookingType;

    private String doctorUid;
    private String doctorName;

    private String hospitalId;
    private String hospitalName;

    private String specialty;

    private String appointmentDate;
    private String appointmentTime;

    private String reason;

>>>>>>> origin/feature/hospital
    private String status;

    private String createdAt;
    private String updatedAt;

<<<<<<< HEAD
    // =========================================================
    // EMPTY CONSTRUCTOR
    // =========================================================
=======
    // ============================================================
    // FIRESTORE CONSTRUCTOR
    // ============================================================
>>>>>>> origin/feature/hospital

    public Appointment() {
    }

<<<<<<< HEAD
    // =========================================================
    // APPOINTMENT ID
    // =========================================================
=======
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
>>>>>>> origin/feature/hospital

    public String getAppointmentId() {
        return appointmentId;
    }

<<<<<<< HEAD
    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    // =========================================================
    // PATIENT
    // =========================================================

=======
>>>>>>> origin/feature/hospital
    public String getPatientUid() {
        return patientUid;
    }

<<<<<<< HEAD
    public void setPatientUid(String patientUid) {
        this.patientUid = patientUid;
    }

=======
>>>>>>> origin/feature/hospital
    public String getPatientName() {
        return patientName;
    }

<<<<<<< HEAD
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    // =========================================================
    // BOOKING TYPE
    // =========================================================

=======
>>>>>>> origin/feature/hospital
    public String getBookingType() {
        return bookingType;
    }

<<<<<<< HEAD
    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    // =========================================================
    // DOCTOR
    // =========================================================

=======
>>>>>>> origin/feature/hospital
    public String getDoctorUid() {
        return doctorUid;
    }

<<<<<<< HEAD
    public void setDoctorUid(String doctorUid) {
        this.doctorUid = doctorUid;
    }

=======
>>>>>>> origin/feature/hospital
    public String getDoctorName() {
        return doctorName;
    }

<<<<<<< HEAD
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    // =========================================================
    // HOSPITAL
    // =========================================================

=======
>>>>>>> origin/feature/hospital
    public String getHospitalId() {
        return hospitalId;
    }

<<<<<<< HEAD
    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

=======
>>>>>>> origin/feature/hospital
    public String getHospitalName() {
        return hospitalName;
    }

<<<<<<< HEAD
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

=======
>>>>>>> origin/feature/hospital
    public String getSpecialty() {
        return specialty;
    }

<<<<<<< HEAD
=======
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
    }

>>>>>>> origin/feature/hospital
    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

<<<<<<< HEAD
    // =========================================================
    // DATE
    // =========================================================

    public String getAppointmentDate() {
        return appointmentDate;
    }

=======
>>>>>>> origin/feature/hospital
    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

<<<<<<< HEAD
    // =========================================================
    // TIME
    // =========================================================

    public String getAppointmentTime() {
        return appointmentTime;
    }

=======
>>>>>>> origin/feature/hospital
    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

<<<<<<< HEAD
    // =========================================================
    // REASON
    // =========================================================

    public String getReason() {
        return reason;
    }

=======
>>>>>>> origin/feature/hospital
    public void setReason(String reason) {
        this.reason = reason;
    }

<<<<<<< HEAD
    // =========================================================
    // STATUS
    // =========================================================

    public String getStatus() {
        return status;
    }

=======
>>>>>>> origin/feature/hospital
    public void setStatus(String status) {
        this.status = status;
    }

<<<<<<< HEAD
    // =========================================================
    // CREATED AT
    // =========================================================

    public String getCreatedAt() {
        return createdAt;
    }

=======
>>>>>>> origin/feature/hospital
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

<<<<<<< HEAD
    // =========================================================
    // UPDATED AT
    // =========================================================

    public String getUpdatedAt() {
        return updatedAt;
    }

=======
>>>>>>> origin/feature/hospital
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}