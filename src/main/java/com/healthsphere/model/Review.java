package com.healthsphere.model;

public class Review {

    private String reviewId;

    // =========================================================
    // PATIENT
    // =========================================================

    private String patientUid;

    private String patientName;

    // =========================================================
    // APPOINTMENT
    // =========================================================

    private String appointmentId;

    // =========================================================
    // REVIEW TARGET
    // =========================================================

    // DOCTOR or HOSPITAL
    private String targetType;

    private String targetId;

    private String targetName;

    // =========================================================
    // REVIEW DETAILS
    // =========================================================

    // Rating from 1 to 5
    private int rating;

    private String comment;

    private String createdAt;

    // =========================================================
    // EMPTY CONSTRUCTOR
    // =========================================================

    public Review() {
    }

    // =========================================================
    // REVIEW ID
    // =========================================================

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    // =========================================================
    // PATIENT UID
    // =========================================================

    public String getPatientUid() {
        return patientUid;
    }

    public void setPatientUid(String patientUid) {
        this.patientUid = patientUid;
    }

    // =========================================================
    // PATIENT NAME
    // =========================================================

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
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
    // TARGET TYPE
    // =========================================================

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    // =========================================================
    // TARGET ID
    // =========================================================

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    // =========================================================
    // TARGET NAME
    // =========================================================

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    // =========================================================
    // RATING
    // =========================================================

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    // =========================================================
    // COMMENT
    // =========================================================

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
}