package com.healthsphere.model;

import java.time.LocalDateTime;

public class ApplicationReview {

    private String reviewId;
    private String applicationId;

    private String applicantUid;
    private String applicantName;
    private String applicantRole;

    private int rating;
    private String reviewText;

    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Required by Firestore
    public ApplicationReview() {
    }

    public ApplicationReview(
            String reviewId,
            String applicationId,
            String applicantUid,
            String applicantName,
            String applicantRole,
            int rating,
            String reviewText,
            String status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.reviewId = reviewId;
        this.applicationId = applicationId;
        this.applicantUid = applicantUid;
        this.applicantName = applicantName;
        this.applicantRole = applicantRole;
        this.rating = rating;
        this.reviewText = reviewText;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicantUid() {
        return applicantUid;
    }

    public void setApplicantUid(String applicantUid) {
        this.applicantUid = applicantUid;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantRole() {
        return applicantRole;
    }

    public void setApplicantRole(String applicantRole) {
        this.applicantRole = applicantRole;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}