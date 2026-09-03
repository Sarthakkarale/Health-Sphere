package com.healthsphere.dao.doctor;

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
    // GET DOCTOR PROFILE
    // ============================================================

    public DoctorProfile getDoctorProfile(String uid) {

        if (uid == null || uid.isBlank()) {
            throw new IllegalArgumentException(
                    "Doctor UID cannot be null or empty."
            );
        }

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

            DoctorProfile profile =
                    document.toObject(DoctorProfile.class);

            if (profile == null) {

                throw new DatabaseException(
                        "Unable to convert doctor profile."
                );
            }

            return profile;

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

        if (doctorProfile == null) {

            throw new IllegalArgumentException(
                    "Doctor profile cannot be null."
            );
        }

        if (doctorProfile.getUid() == null
                || doctorProfile.getUid().isBlank()) {

            throw new IllegalArgumentException(
                    "Doctor UID cannot be null or empty."
            );
        }

        try {

            db.collection("doctors")
                    .document(doctorProfile.getUid())
                    .set(doctorProfile)
                    .get();

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to update doctor profile.",
                    e
            );
        }
    }
}