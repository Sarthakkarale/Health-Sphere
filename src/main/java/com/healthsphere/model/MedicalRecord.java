package com.healthsphere.model;

import java.util.ArrayList;
import java.util.List;

public class MedicalRecord {

    private String recordId;
    private String patientUid;
    private String title;
    private String type;
    private String date;
    private String description;
    private String doctorName;
    private String hospitalName;

    // ============================================================
    // RECORD IDENTIFICATION
    // ============================================================

    private String recordId;

    private String patientUid;

    private String doctorUid;

    private String appointmentId;


    // ============================================================
    // PERSON INFORMATION
    // ============================================================

    private String patientName;

    private String doctorName;


    // ============================================================
    // CONSULTATION INFORMATION
    // ============================================================

    private String symptoms;

    private String diagnosis;


    // ============================================================
    // VITALS
    // ============================================================

    private String heartRate;

    private String bloodPressure;

    private String temperature;

    private String spo2;


    // ============================================================
    // CLINICAL INFORMATION
    // ============================================================

    private String clinicalNotes;

    private String prescription;


    // ============================================================
    // MEDICAL REPORTS
    // ============================================================
    //
    // Contains metadata about uploaded files.
    //
    // Actual PDF/image files will be stored in Firebase Storage.
    //
    // ============================================================

    private List<MedicalReport> reports;


    // ============================================================
    // TIMESTAMPS
    // ============================================================

    private String createdAt;

    private String updatedAt;


    // ============================================================
    // STATUS
    // ============================================================

    private String status;


    // ============================================================
    // DEFAULT CONSTRUCTOR
    // ============================================================
    //
    // Required by Firestore.
    //
    // ============================================================

    public MedicalRecord() {

        this.reports =
                new ArrayList<>();
    }


    // ============================================================
    // COMPLETE CONSTRUCTOR
    // ============================================================

    public MedicalRecord(
            String recordId,
            String patientUid,
            String doctorUid,
            String appointmentId,
            String patientName,
            String doctorName,
            String symptoms,
            String diagnosis,
            String heartRate,
            String bloodPressure,
            String temperature,
            String spo2,
            String clinicalNotes,
            String prescription,
            List<MedicalReport> reports,
            String createdAt,
            String updatedAt,
            String status) {

        this.recordId = recordId;
        this.patientUid = patientUid;
        this.doctorUid = doctorUid;
        this.appointmentId = appointmentId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.heartRate = heartRate;
        this.bloodPressure = bloodPressure;
        this.temperature = temperature;
        this.spo2 = spo2;
        this.clinicalNotes = clinicalNotes;
        this.prescription = prescription;

        this.reports =
                reports != null
                        ? reports
                        : new ArrayList<>();

        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
    }


    // ============================================================
    // RECORD ID
    // ============================================================

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
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
    // APPOINTMENT ID
    // ============================================================

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }


    // ============================================================
    // PATIENT NAME
    // ============================================================

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }


    // ============================================================
    // DOCTOR NAME
    // ============================================================

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }


    // ============================================================
    // SYMPTOMS
    // ============================================================

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }


    // ============================================================
    // DIAGNOSIS
    // ============================================================

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }


    // ============================================================
    // HEART RATE
    // ============================================================

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }


    // ============================================================
    // BLOOD PRESSURE
    // ============================================================

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }


    // ============================================================
    // TEMPERATURE
    // ============================================================

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }


    // ============================================================
    // SPO2
    // ============================================================

    public String getSpo2() {
        return spo2;
    }

    public void setSpo2(String spo2) {
        this.spo2 = spo2;
    }


    // ============================================================
    // CLINICAL NOTES
    // ============================================================

    public String getClinicalNotes() {
        return clinicalNotes;
    }

    public void setClinicalNotes(String clinicalNotes) {
        this.clinicalNotes = clinicalNotes;
    }


    // ============================================================
    // PRESCRIPTION
    // ============================================================

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }


    // ============================================================
    // MEDICAL REPORTS
    // ============================================================

    public List<MedicalReport> getReports() {

        if (reports == null) {

            reports =
                    new ArrayList<>();
        }

        return reports;
    }

    public void setReports(
            List<MedicalReport> reports) {

        this.reports =
                reports != null
                        ? reports
                        : new ArrayList<>();
    }


    // ============================================================
    // CREATED AT
    // ============================================================

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


    // ============================================================
    // UPDATED AT
    // ============================================================

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }


    // ============================================================
    // STATUS
    // ============================================================

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}