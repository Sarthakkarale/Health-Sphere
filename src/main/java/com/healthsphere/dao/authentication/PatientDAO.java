package com.healthsphere.dao.authentication;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.PatientProfile;

public class PatientDAO {

    private final Firestore db;

    public PatientDAO() {
        this.db = FirebaseConfig.getFirestore();
    }

    // ============================================================
    // CREATE PATIENT PROFILE
    // ============================================================

    public void createPatientProfile(
            PatientProfile patientProfile) {

        try {

            db.collection("patients")
                    .document(patientProfile.getUid())
                    .set(patientProfile)
                    .get();

            System.out.println(
                    "Patient profile created successfully."
            );

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to create patient profile.",
                    e
            );
        }
    }

    // ============================================================
    // GET PATIENT PROFILE
    // ============================================================

    public PatientProfile getPatientProfile(
            String uid) {

        try {

            DocumentSnapshot document =
                    db.collection("patients")
                            .document(uid)
                            .get()
                            .get();

            if (!document.exists()) {

                throw new DatabaseException(
                        "Patient profile not found."
                );
            }

            return document.toObject(
                    PatientProfile.class
            );

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve patient profile.",
                    e
            );
        }
    }

    // ============================================================
    // UPDATE PATIENT PROFILE
    // ============================================================

    public void updatePatientProfile(
            PatientProfile patientProfile) {

        try {

            db.collection("patients")
                    .document(patientProfile.getUid())
                    .set(patientProfile)
                    .get();

            System.out.println(
                    "Patient profile updated successfully."
            );

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to update patient profile.",
                    e
            );
        }
    }

    // ============================================================
    // GET ALL PATIENTS
    // ============================================================

    public java.util.List<PatientProfile> getAllPatients() {
        try {
            java.util.List<com.google.cloud.firestore.QueryDocumentSnapshot> documents =
                    db.collection("patients")
                            .get()
                            .get()
                            .getDocuments();

            java.util.List<PatientProfile> list = new java.util.ArrayList<>();
            for (com.google.cloud.firestore.QueryDocumentSnapshot doc : documents) {
                PatientProfile profile = doc.toObject(PatientProfile.class);
                if (profile != null) {
                    list.add(profile);
                }
            }
            return list;
        } catch (Exception e) {
            System.err.println("Unable to get all patients: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }
}