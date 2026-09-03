
package com.healthsphere.model;

public class MedicalReport {

    private String reportId;

    // Owner of the medical report
    private String patientUid;

    // Basic report information
    private String reportName;
    private String reportType;

    // File information
    private String fileName;
    private String fileUrl;
    private String storagePath;
    private String fileType;

    // Optional related information
    private String doctorUid;
    private String hospitalId;
    private String appointmentId;

    // Upload information
    private String uploadedBy;
    private String uploadDate;


    // =========================================================
    // EMPTY CONSTRUCTOR
    // =========================================================

    public MedicalReport() {
    }


    // =========================================================
    // FULL CONSTRUCTOR
    // =========================================================

    public MedicalReport(
            String reportId,
            String patientUid,
            String reportName,
            String reportType,
            String fileName,
            String fileUrl,
            String storagePath,
            String fileType,
            String doctorUid,
            String hospitalId,
            String appointmentId,
            String uploadedBy,
            String uploadDate) {

        this.reportId = reportId;
        this.patientUid = patientUid;
        this.reportName = reportName;
        this.reportType = reportType;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.storagePath = storagePath;
        this.fileType = fileType;
        this.doctorUid = doctorUid;
        this.hospitalId = hospitalId;
        this.appointmentId = appointmentId;
        this.uploadedBy = uploadedBy;
        this.uploadDate = uploadDate;
    }


    // =========================================================
    // GETTERS AND SETTERS
    // =========================================================

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }


    public String getPatientUid() {
        return patientUid;
    }

    public void setPatientUid(String patientUid) {
        this.patientUid = patientUid;
    }


    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }


    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }


    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }


    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }


    public String getDoctorUid() {
        return doctorUid;
    }

    public void setDoctorUid(String doctorUid) {
        this.doctorUid = doctorUid;
    }


    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }


    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }


    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }


    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }
}