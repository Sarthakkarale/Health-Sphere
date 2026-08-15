package com.healthsphere.model;

public class MedicalRecord {

    private String recordId;
    private String patientUid;
    private String title;
    private String type;
    private String date;
    private String description;
    private String doctorName;
    private String hospitalName;

    public MedicalRecord() {
    }

    public MedicalRecord(
            String recordId,
            String patientUid,
            String title,
            String type,
            String date,
            String description,
            String doctorName,
            String hospitalName) {

        this.recordId = recordId;
        this.patientUid = patientUid;
        this.title = title;
        this.type = type;
        this.date = date;
        this.description = description;
        this.doctorName = doctorName;
        this.hospitalName = hospitalName;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }
}