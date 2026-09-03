package com.healthsphere.model;

public class ComplaintModel {

    private String ticketId;
    private String category;
    private String issueTitle;
    private String description;
    private String complainant;
    private String priority;
    private String status;
    private String createdDate;
    private String resolvedDate;

    // ============================================================
    // DEFAULT CONSTRUCTOR
    // Required for Firestore object mapping
    // ============================================================

    public ComplaintModel() {
    }

    // ============================================================
    // EXISTING CONSTRUCTOR
    // Keeps compatibility with ComplaintsManagementView
    // ============================================================

    public ComplaintModel(
            String ticketId,
            String category,
            String issueTitle,
            String description,
            String complainant,
            String priority,
            String status,
            String createdDate) {

        this(
                ticketId,
                category,
                issueTitle,
                description,
                complainant,
                priority,
                status,
                createdDate,
                null
        );
    }

    // ============================================================
    // FULL CONSTRUCTOR
    // ============================================================

    public ComplaintModel(
            String ticketId,
            String category,
            String issueTitle,
            String description,
            String complainant,
            String priority,
            String status,
            String createdDate,
            String resolvedDate) {

        this.ticketId = ticketId;
        this.category = category;
        this.issueTitle = issueTitle;
        this.description = description;
        this.complainant = complainant;
        this.priority = priority;
        this.status = status;
        this.createdDate = createdDate;
        this.resolvedDate = resolvedDate;
    }

    // ============================================================
    // GETTERS
    // ============================================================

    public String getTicketId() {
        return ticketId;
    }

    public String getCategory() {
        return category;
    }

    public String getIssueTitle() {
        return issueTitle;
    }

    public String getDescription() {
        return description;
    }

    public String getComplainant() {
        return complainant;
    }

    public String getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getResolvedDate() {
        return resolvedDate;
    }

    // ============================================================
    // SETTERS
    // ============================================================

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setIssueTitle(String issueTitle) {
        this.issueTitle = issueTitle;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setComplainant(String complainant) {
        this.complainant = complainant;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public void setResolvedDate(String resolvedDate) {
        this.resolvedDate = resolvedDate;
    }

    // ============================================================
    // TO STRING
    // ============================================================

    @Override
    public String toString() {

        return "ComplaintModel{" +
                "ticketId='" + ticketId + '\'' +
                ", category='" + category + '\'' +
                ", issueTitle='" + issueTitle + '\'' +
                ", description='" + description + '\'' +
                ", complainant='" + complainant + '\'' +
                ", priority='" + priority + '\'' +
                ", status='" + status + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", resolvedDate='" + resolvedDate + '\'' +
                '}';
    }
}