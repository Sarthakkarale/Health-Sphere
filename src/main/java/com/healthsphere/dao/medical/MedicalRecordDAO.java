package com.healthsphere.dao.medical;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.model.MedicalRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class MedicalRecordDAO {

    private static final String COLLECTION_NAME = "medicalRecords";

    private final Firestore firestore;

    public MedicalRecordDAO() {
        this.firestore = FirebaseConfig.getFirestore();
    }

    /**
     * Create a new medical record in Firestore.
     */
    public String createMedicalRecord(MedicalRecord medicalRecord)
            throws ExecutionException, InterruptedException {

        if (medicalRecord == null) {
            throw new IllegalArgumentException(
                    "Medical record cannot be null."
            );
        }

        String recordId = medicalRecord.getRecordId();

        if (recordId == null || recordId.trim().isEmpty()) {
            recordId = UUID.randomUUID().toString();
            medicalRecord.setRecordId(recordId);
        }

        DocumentReference documentReference =
                firestore.collection(COLLECTION_NAME)
                        .document(recordId);

        documentReference.set(medicalRecord).get();

        return recordId;
    }

    /**
     * Get a medical record using its record ID.
     */
    public MedicalRecord getMedicalRecord(String recordId)
            throws ExecutionException, InterruptedException {

        if (recordId == null || recordId.trim().isEmpty()) {
            return null;
        }

        DocumentSnapshot documentSnapshot =
                firestore.collection(COLLECTION_NAME)
                        .document(recordId)
                        .get()
                        .get();

        if (!documentSnapshot.exists()) {
            return null;
        }

        return documentSnapshot.toObject(MedicalRecord.class);
    }

    /**
     * Get all medical records of a patient.
     */
    public List<MedicalRecord> getPatientMedicalRecords(
            String patientUid)
            throws ExecutionException, InterruptedException {

        List<MedicalRecord> records = new ArrayList<>();

        if (patientUid == null || patientUid.trim().isEmpty()) {
            return records;
        }

        QuerySnapshot querySnapshot =
                firestore.collection(COLLECTION_NAME)
                        .whereEqualTo("patientUid", patientUid)
                        .get()
                        .get();

        for (DocumentSnapshot document : querySnapshot.getDocuments()) {

            MedicalRecord record =
                    document.toObject(MedicalRecord.class);

            if (record != null) {
                records.add(record);
            }
        }

        return records;
    }

    /**
     * Get all medical records created by a doctor.
     */
    public List<MedicalRecord> getDoctorMedicalRecords(
            String doctorUid)
            throws ExecutionException, InterruptedException {

        List<MedicalRecord> records = new ArrayList<>();

        if (doctorUid == null || doctorUid.trim().isEmpty()) {
            return records;
        }

        QuerySnapshot querySnapshot =
                firestore.collection(COLLECTION_NAME)
                        .whereEqualTo("doctorUid", doctorUid)
                        .get()
                        .get();

        for (DocumentSnapshot document : querySnapshot.getDocuments()) {

            MedicalRecord record =
                    document.toObject(MedicalRecord.class);

            if (record != null) {
                records.add(record);
            }
        }

        return records;
    }

    /**
     * Get medical records associated with an appointment.
     */
    public List<MedicalRecord> getAppointmentMedicalRecords(
            String appointmentId)
            throws ExecutionException, InterruptedException {

        List<MedicalRecord> records = new ArrayList<>();

        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            return records;
        }

        QuerySnapshot querySnapshot =
                firestore.collection(COLLECTION_NAME)
                        .whereEqualTo("appointmentId", appointmentId)
                        .get()
                        .get();

        for (DocumentSnapshot document : querySnapshot.getDocuments()) {

            MedicalRecord record =
                    document.toObject(MedicalRecord.class);

            if (record != null) {
                records.add(record);
            }
        }

        return records;
    }

    /**
     * Update an existing medical record.
     */
    public void updateMedicalRecord(MedicalRecord medicalRecord)
            throws ExecutionException, InterruptedException {

        if (medicalRecord == null) {
            throw new IllegalArgumentException(
                    "Medical record cannot be null."
            );
        }

        if (medicalRecord.getRecordId() == null
                || medicalRecord.getRecordId().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Record ID is required for update."
            );
        }

        DocumentReference documentReference =
                firestore.collection(COLLECTION_NAME)
                        .document(medicalRecord.getRecordId());

        DocumentSnapshot existingDocument =
                documentReference.get().get();

        if (!existingDocument.exists()) {
            throw new IllegalArgumentException(
                    "Medical record does not exist: "
                            + medicalRecord.getRecordId()
            );
        }

        documentReference.set(medicalRecord).get();
    }

    /**
     * Delete a medical record.
     */
    public void deleteMedicalRecord(String recordId)
            throws ExecutionException, InterruptedException {

        if (recordId == null || recordId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Record ID cannot be empty."
            );
        }

        firestore.collection(COLLECTION_NAME)
                .document(recordId)
                .delete()
                .get();
    }
}