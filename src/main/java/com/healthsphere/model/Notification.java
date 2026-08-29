package com.healthsphere.model;

public class Notification {

    private String notificationId;
    private String patientUid;
    private String title;
    private String description;
    private String type;
    private boolean read;
    private String createdAt;

    public Notification() {
    }

    public Notification(
            String notificationId,
            String patientUid,
            String title,
            String description,
            String type,
            boolean read,
            String createdAt) {

        this.notificationId = notificationId;
        this.patientUid = patientUid;
        this.title = title;
        this.description = description;
        this.type = type;
        this.read = read;
        this.createdAt = createdAt;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getPatientUid() {
        return patientUid;
    }

    public void setPatientUid(String patientUid) {
        this.patientUid = patientUid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}