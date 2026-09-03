package com.healthsphere.dao.patient;

import java.util.ArrayList;
import java.util.List;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.model.Prescription;

public class PrescriptionDAO {

    private static final String COLLECTION_NAME = "prescriptions";

    private final Firestore firestore;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PrescriptionDAO() {
        this.firestore = FirebaseConfig.getFirestore();
    }

    // =========================================================
    // GET PRESCRIPTIONS BY PATIENT UID
    // =========================================================

    /**
     * Get all prescriptions belonging to a specific patient.
     */
    public List<Prescription> getPrescriptionsByPatientUid(
            String patientUid) throws Exception {

        if (patientUid == null || patientUid.isBlank()) {
            throw new IllegalArgumentException(
                    "Patient UID is missing."
            );
        }

        List<Prescription> prescriptions =
                new ArrayList<>();

        QuerySnapshot snapshot =
                firestore
                        .collection(COLLECTION_NAME)
                        .whereEqualTo(
                                "patientUid",
                                patientUid
                        )
                        .get()
                        .get();

        for (QueryDocumentSnapshot document :
                snapshot.getDocuments()) {

            Prescription prescription =
                    document.toObject(
                            Prescription.class
                    );

            if (prescription != null) {

                /*
                 * If prescriptionId is not stored
                 * inside the document, use the
                 * Firestore document ID.
                 */
                if (prescription.getPrescriptionId() == null
                        || prescription.getPrescriptionId()
                        .isBlank()) {

                    prescription.setPrescriptionId(
                            document.getId()
                    );
                }

                prescriptions.add(
                        prescription
                );
            }
        }

        return prescriptions;
    }

    // =========================================================
    // GET PRESCRIPTION BY ID
    // =========================================================

    /**
     * Get one prescription by Firestore document ID.
     */
    public Prescription getPrescriptionById(
            String prescriptionId) throws Exception {

        if (prescriptionId == null
                || prescriptionId.isBlank()) {

            throw new IllegalArgumentException(
                    "Prescription ID is missing."
            );
        }

        DocumentSnapshot document =
                firestore
                        .collection(COLLECTION_NAME)
                        .document(prescriptionId)
                        .get()
                        .get();

        if (!document.exists()) {
            return null;
        }

        Prescription prescription =
                document.toObject(
                        Prescription.class
                );

        if (prescription != null
                && (prescription.getPrescriptionId() == null
                || prescription.getPrescriptionId().isBlank())) {

            prescription.setPrescriptionId(
                    document.getId()
            );
        }

        return prescription;
    }

    // =========================================================
    // DELETE PRESCRIPTION
    // =========================================================

    /**
     * Delete a prescription from Firestore.
     *
     * Note:
     * Patient ownership/security validation is handled
     * by PrescriptionController before this method is called.
     */
    public void deletePrescription(
            String prescriptionId) throws Exception {

        if (prescriptionId == null
                || prescriptionId.isBlank()) {

            throw new IllegalArgumentException(
                    "Prescription ID is missing."
            );
        }

        firestore
                .collection(COLLECTION_NAME)
                .document(prescriptionId)
                .delete()
                .get();
    }
}