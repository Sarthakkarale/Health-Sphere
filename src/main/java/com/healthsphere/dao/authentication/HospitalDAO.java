package com.healthsphere.dao.authentication;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.HospitalProfile;

import java.util.ArrayList;
import java.util.List;

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

        if (hospitalProfile == null) {
            throw new IllegalArgumentException(
                    "Hospital profile cannot be null."
            );
        }

        if (hospitalProfile.getUid() == null
                || hospitalProfile.getUid().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Hospital UID is required."
            );
        }

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

        validateUid(uid);

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
    // GET ALL HOSPITAL PROFILES
    // ============================================================

    public List<HospitalProfile> getAllHospitalProfiles() {

        try {

            QuerySnapshot snapshot =
                    db.collection("hospitals")
                            .get()
                            .get();

            List<HospitalProfile> hospitals =
                    new ArrayList<>();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                if (!document.exists()) {
                    continue;
                }

                HospitalProfile hospital =
                        document.toObject(
                                HospitalProfile.class
                        );

                if (hospital != null) {
                    hospitals.add(hospital);
                }
            }

            return hospitals;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve hospital profiles.",
                    e
            );
        }
    }

    // ============================================================
    // UPDATE HOSPITAL PROFILE
    // ============================================================

    public void updateHospitalProfile(
            HospitalProfile hospitalProfile) {

        if (hospitalProfile == null) {
            throw new IllegalArgumentException(
                    "Hospital profile cannot be null."
            );
        }

        validateUid(
                hospitalProfile.getUid()
        );

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

    // ============================================================
    // DELETE HOSPITAL PROFILE
    // ============================================================

    public void deleteHospitalProfile(
            String uid) {

        validateUid(uid);

        try {

            db.collection("hospitals")
                    .document(uid)
                    .delete()
                    .get();

            System.out.println(
                    "Hospital profile deleted successfully."
            );

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to delete hospital profile.",
                    e
            );
        }
    }

    // ============================================================
    // VALIDATION
    // ============================================================

    private void validateUid(
            String uid) {

        if (uid == null
                || uid.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Hospital UID is required."
            );
        }
    }
}