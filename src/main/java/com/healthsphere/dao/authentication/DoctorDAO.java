package com.healthsphere.dao.authentication;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.DoctorProfile;

public class DoctorDAO {

    private final Firestore db;

    public DoctorDAO() {
        this.db = FirebaseConfig.getFirestore();
    }

    // ============================================================
    // CREATE DOCTOR PROFILE
    // ============================================================

    public void createDoctorProfile(
            DoctorProfile doctorProfile) {

        try {

            db.collection("doctors")
                    .document(doctorProfile.getUid())
                    .set(doctorProfile)
                    .get();

            System.out.println(
                    "Doctor profile created successfully."
            );

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to create doctor profile.",
                    e
            );
        }
    }

    // ============================================================
    // GET DOCTOR PROFILE
    // ============================================================

    public DoctorProfile getDoctorProfile(
            String uid) {

        try {

            DocumentSnapshot document =
                    db.collection("doctors")
                            .document(uid)
                            .get()
                            .get();

            if (!document.exists()) {

                throw new DatabaseException(
                        "Doctor profile not found."
                );
            }

            return document.toObject(
                    DoctorProfile.class
            );

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve doctor profile.",
                    e
            );
        }
    }

    // ============================================================
    // UPDATE DOCTOR PROFILE
    // ============================================================

    public void updateDoctorProfile(
            DoctorProfile doctorProfile) {

        try {

            db.collection("doctors")
                    .document(doctorProfile.getUid())
                    .set(doctorProfile)
                    .get();

            System.out.println(
                    "Doctor profile updated successfully."
            );

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to update doctor profile.",
                    e
            );
        }
    }
}