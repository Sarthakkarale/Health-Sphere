package com.healthsphere.dao.authentication;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.HospitalProfile;

public class HospitalDAO {

    private final Firestore db;

    public HospitalDAO() {
        this.db = FirebaseConfig.getFirestore();
    }

    // ============================================================
    // CREATE HOSPITAL PROFILE
    // ============================================================

    public void createHospitalProfile(
            HospitalProfile hospitalProfile) {

        try {

            db.collection("hospitals")
                    .document(hospitalProfile.getUid())
                    .set(hospitalProfile)
                    .get();

            System.out.println(
                    "Hospital profile created successfully."
            );

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to create hospital profile.",
                    e
            );
        }
    }

    // ============================================================
    // GET HOSPITAL PROFILE
    // ============================================================

    public HospitalProfile getHospitalProfile(
            String uid) {

        try {

            DocumentSnapshot document =
                    db.collection("hospitals")
                            .document(uid)
                            .get()
                            .get();

            if (!document.exists()) {

                throw new DatabaseException(
                        "Hospital profile not found."
                );
            }

            return document.toObject(
                    HospitalProfile.class
            );

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve hospital profile.",
                    e
            );
        }
    }

    // ============================================================
    // UPDATE HOSPITAL PROFILE
    // ============================================================

    public void updateHospitalProfile(
            HospitalProfile hospitalProfile) {

        try {

            db.collection("hospitals")
                    .document(hospitalProfile.getUid())
                    .set(hospitalProfile)
                    .get();

            System.out.println(
                    "Hospital profile updated successfully."
            );

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to update hospital profile.",
                    e
            );
        }
    }
}