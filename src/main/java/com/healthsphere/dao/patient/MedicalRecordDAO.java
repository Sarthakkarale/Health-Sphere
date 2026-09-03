
package com.healthsphere.dao.patient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.MedicalRecord;

public class MedicalRecordDAO {

    private final Firestore db;

    public MedicalRecordDAO() {
        db = FirebaseConfig.getFirestore();
    }

    // CREATE
    public void createMedicalRecord(MedicalRecord record) {
        try {
            if (record == null) {
                throw new DatabaseException("Medical record cannot be null.");
            }

            if (record.getPatientUid() == null ||
                    record.getPatientUid().isBlank()) {
                throw new DatabaseException("Patient UID is missing.");
            }

            if (record.getRecordId() == null ||
                    record.getRecordId().isBlank()) {
                record.setRecordId(UUID.randomUUID().toString());
            }

            db.collection("medicalRecords")
                    .document(record.getRecordId())
                    .set(record)
                    .get();

            System.out.println("Medical record created successfully.");

        } catch (DatabaseException e) {
            throw e;

        } catch (Exception e) {
            throw new DatabaseException(
                    "Unable to create medical record.", e
            );
        }
    }

    // GET ALL RECORDS FOR PATIENT
    public List<MedicalRecord> getMedicalRecords(String patientUid) {
        try {
            if (patientUid == null || patientUid.isBlank()) {
                throw new DatabaseException("Patient UID is missing.");
            }

            QuerySnapshot snapshot = db.collection("medicalRecords")
                    .whereEqualTo("patientUid", patientUid)
                    .get()
                    .get();

            List<MedicalRecord> records = new ArrayList<>();

            for (DocumentSnapshot document : snapshot.getDocuments()) {

                MedicalRecord record =
                        document.toObject(MedicalRecord.class);

                if (record != null) {

                    if (record.getRecordId() == null ||
                            record.getRecordId().isBlank()) {
                        record.setRecordId(document.getId());
                    }

                    records.add(record);
                }
            }

            System.out.println(
                    "Medical records found: " + records.size()
            );

            return records;

        } catch (DatabaseException e) {
            throw e;

        } catch (Exception e) {
            throw new DatabaseException(
                    "Unable to retrieve medical records.", e
            );
        }
    }

    // GET ONE RECORD
    public MedicalRecord getMedicalRecord(
            String patientUid,
            String recordId) {

        try {
            if (patientUid == null || patientUid.isBlank()) {
                throw new DatabaseException("Patient UID is missing.");
            }

            if (recordId == null || recordId.isBlank()) {
                throw new DatabaseException("Medical record ID is missing.");
            }

            DocumentSnapshot document = db.collection("medicalRecords")
                    .document(recordId)
                    .get()
                    .get();

            if (!document.exists()) {
                throw new DatabaseException(
                        "Medical record not found."
                );
            }

            MedicalRecord record =
                    document.toObject(MedicalRecord.class);

            if (record == null) {
                throw new DatabaseException(
                        "Unable to read medical record."
                );
            }

            if (record.getPatientUid() == null ||
                    !record.getPatientUid().equals(patientUid)) {
                throw new DatabaseException(
                        "Medical record does not belong to the current patient."
                );
            }

            if (record.getRecordId() == null ||
                    record.getRecordId().isBlank()) {
                record.setRecordId(document.getId());
            }

            return record;

        } catch (DatabaseException e) {
            throw e;

        } catch (Exception e) {
            throw new DatabaseException(
                    "Unable to retrieve medical record.", e
            );
        }
    }

    // UPDATE
    public void updateMedicalRecord(MedicalRecord record) {
        try {
            if (record == null) {
                throw new DatabaseException(
                        "Medical record cannot be null."
                );
            }

            if (record.getRecordId() == null ||
                    record.getRecordId().isBlank()) {
                throw new DatabaseException(
                        "Medical record ID is missing."
                );
            }

            db.collection("medicalRecords")
                    .document(record.getRecordId())
                    .set(record)
                    .get();

            System.out.println(
                    "Medical record updated successfully."
            );

        } catch (DatabaseException e) {
            throw e;

        } catch (Exception e) {
            throw new DatabaseException(
                    "Unable to update medical record.", e
            );
        }
    }

    // DELETE
    public void deleteMedicalRecord(
            String patientUid,
            String recordId) {

        try {
            // First verify ownership
            getMedicalRecord(patientUid, recordId);

            db.collection("medicalRecords")
                    .document(recordId)
                    .delete()
                    .get();

            System.out.println(
                    "Medical record deleted successfully."
            );

        } catch (DatabaseException e) {
            throw e;

        } catch (Exception e) {
            throw new DatabaseException(
                    "Unable to delete medical record.", e
            );
        }
    }
}

