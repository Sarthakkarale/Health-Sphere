package com.healthsphere.controller.patient;

import java.util.List;

import com.healthsphere.dao.patient.MedicalRecordDAO;
import com.healthsphere.model.MedicalRecord;
import com.healthsphere.util.SessionManager;

public class MedicalRecordController {

    private final MedicalRecordDAO medicalRecordDAO;

    public MedicalRecordController() {
        this.medicalRecordDAO = new MedicalRecordDAO();
    }

    // =========================================================
    // GET CURRENT PATIENT UID
    // =========================================================

    private String getCurrentPatientUid() {

        if (!SessionManager.isLoggedIn()) {
            throw new IllegalStateException(
                    "No active user session."
            );
        }

        if (SessionManager.getCurrentUser() == null) {
            throw new IllegalStateException(
                    "Current user is null."
            );
        }

        String uid =
                SessionManager
                        .getCurrentUser()
                        .getUid();

        if (uid == null || uid.isBlank()) {
            throw new IllegalStateException(
                    "Current user UID is missing."
            );
        }

        System.out.println(
                "MedicalRecordController - Current Patient UID: "
                        + uid
        );

        return uid;
    }

    // =========================================================
    // GET CURRENT PATIENT MEDICAL RECORDS
    // =========================================================

    public List<MedicalRecord> getCurrentPatientRecords() {

        String uid = getCurrentPatientUid();

        System.out.println(
                "Fetching medical records for UID: " + uid
        );

        List<MedicalRecord> records =
                medicalRecordDAO.getMedicalRecords(uid);

        System.out.println(
                "Medical records found: " + records.size()
        );

        return records;
    }

    // =========================================================
    // GET SINGLE CURRENT PATIENT RECORD
    // =========================================================

    public MedicalRecord getCurrentPatientRecord(
            String recordId) {

        String uid = getCurrentPatientUid();

        return medicalRecordDAO.getMedicalRecord(
                uid,
                recordId
        );
    }

    // =========================================================
    // CREATE CURRENT PATIENT RECORD
    // =========================================================

    public void createCurrentPatientRecord(
            String title,
            String type,
            String date,
            String description) {

        String uid = getCurrentPatientUid();

        validate(
                title,
                type,
                date
        );

        MedicalRecord record =
                new MedicalRecord();

        record.setPatientUid(uid);
        record.setTitle(title.trim());
        record.setType(type.trim());
        record.setDate(date.trim());
        record.setDescription(
                description == null
                        ? ""
                        : description.trim()
        );

        medicalRecordDAO.createMedicalRecord(record);
    }

    // =========================================================
    // VALIDATION
    // =========================================================

    private void validate(
            String title,
            String type,
            String date) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Medical record title cannot be empty."
            );
        }

        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException(
                    "Medical record type cannot be empty."
            );
        }

        if (date == null || date.isBlank()) {
            throw new IllegalArgumentException(
                    "Medical record date cannot be empty."
            );
        }
    }
}