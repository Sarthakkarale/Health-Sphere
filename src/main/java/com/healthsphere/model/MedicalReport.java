package com.healthsphere.model;

public class MedicalReport {

    // ============================================================
    // REPORT IDENTIFICATION
    // ============================================================

    private String reportId;

    private String patientUid;

    private String doctorUid;

    private String hospitalId;

    private String appointmentId;


    // ============================================================
    // REPORT INFORMATION
    // ============================================================

    private String reportName;

    private String reportType;

    private String fileName;

    private String fileUrl;

    private String storagePath;

    private String fileType;


    // ============================================================
    // UPLOAD INFORMATION
    // ============================================================

    private String uploadedByUid;

    private String uploadedByRole;

    private String uploadedAt;


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public MedicalReport() {
    }


    public MedicalReport(
            String reportId,
            String patientUid,
            String doctorUid,
            String hospitalId,
            String appointmentId,
            String reportName,
            String reportType,
            String fileName,
            String fileUrl,
            String storagePath,
            String fileType,
            String uploadedByUid,
            String uploadedByRole,
            String uploadedAt) {

        this.reportId = reportId;
        this.patientUid = patientUid;
        this.doctorUid = doctorUid;
        this.hospitalId = hospitalId;
        this.appointmentId = appointmentId;
        this.reportName = reportName;
        this.reportType = reportType;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.storagePath = storagePath;
        this.fileType = fileType;
        this.uploadedByUid = uploadedByUid;
        this.uploadedByRole = uploadedByRole;
        this.uploadedAt = uploadedAt;
    }


    // ============================================================
    // REPORT ID
    // ============================================================

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }


    // ============================================================
    // PATIENT UID
    // ============================================================

    public String getPatientUid() {
        return patientUid;
    }

    public void setPatientUid(String patientUid) {
        this.patientUid = patientUid;
    }


    // ============================================================
    // DOCTOR UID
    // ============================================================

    public String getDoctorUid() {
        return doctorUid;
    }

    public void setDoctorUid(String doctorUid) {
        this.doctorUid = doctorUid;
    }


    // ============================================================
    // HOSPITAL ID
    // ============================================================

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }


    // ============================================================
    // APPOINTMENT ID
    // ============================================================

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }


    // ============================================================
    // REPORT NAME
    // ============================================================

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }


    // ============================================================
    // REPORT TYPE
    // ============================================================

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }


    // ============================================================
    // FILE NAME
    // ============================================================

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    // ============================================================
    // FILE URL
    // ============================================================

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }


    // ============================================================
    // STORAGE PATH
    // ============================================================

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }


    // ============================================================
    // FILE TYPE
    // ============================================================

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }


    // ============================================================
    // UPLOADED BY UID
    // ============================================================

    public String getUploadedByUid() {
        return uploadedByUid;
    }

    public void setUploadedByUid(String uploadedByUid) {
        this.uploadedByUid = uploadedByUid;
    }


    // ============================================================
    // UPLOADED BY ROLE
    // ============================================================

    public String getUploadedByRole() {
        return uploadedByRole;
    }

    public void setUploadedByRole(String uploadedByRole) {
        this.uploadedByRole = uploadedByRole;
    }


    // ============================================================
    // UPLOADED AT
    // ============================================================

    public String getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(String uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}