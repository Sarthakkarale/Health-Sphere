package com.healthsphere.model;

public class PaymentRecord {

    private String paymentId;
    private String appointmentId;
    private String patientUid;
    private String patientName;
    private String doctorUid;
    private String doctorName;
    private String hospitalId;
    private String hospitalName;
    private double amount;
    private String status; // "PENDING", "COMPLETED", "FAILED"
    private String paymentLinkId;
    private String paymentLinkUrl;
    private String razorpayPaymentId;
    private String createdAt;
    private String updatedAt;

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

    public PaymentRecord() {
    }

    public PaymentRecord(
            String paymentId,
            String appointmentId,
            String patientUid,
            String patientName,
            String doctorUid,
            String doctorName,
            double amount,
            String status,
            String paymentLinkId,
            String paymentLinkUrl,
            String razorpayPaymentId,
            String createdAt,
            String updatedAt) {
        this.paymentId = paymentId;
        this.appointmentId = appointmentId;
        this.patientUid = patientUid;
        this.patientName = patientName;
        this.doctorUid = doctorUid;
        this.doctorName = doctorName;
        this.amount = amount;
        this.status = status;
        this.paymentLinkId = paymentLinkId;
        this.paymentLinkUrl = paymentLinkUrl;
        this.razorpayPaymentId = razorpayPaymentId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentLinkId() {
        return paymentLinkId;
    }

    public void setPaymentLinkId(String paymentLinkId) {
        this.paymentLinkId = paymentLinkId;
    }

    public String getPaymentLinkUrl() {
        return paymentLinkUrl;
    }

    public void setPaymentLinkUrl(String paymentLinkUrl) {
        this.paymentLinkUrl = paymentLinkUrl;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
