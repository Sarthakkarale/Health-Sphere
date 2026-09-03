package com.healthsphere.dao.authentication;

import java.util.ArrayList;
import java.util.List;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.HospitalProfile;

public class HospitalDAO {

    private final Firestore db;

    public HospitalDAO() {
        this.db = FirebaseConfig.getFirestore();
    }

    // =========================================================
    // CREATE HOSPITAL PROFILE
    // =========================================================

    public void createHospitalProfile(
            HospitalProfile hospitalProfile) {

        if (hospitalProfile == null) {
            throw new IllegalArgumentException(
                    "Hospital profile cannot be null."
            );
        }

        if (hospitalProfile.getUid() == null ||
                hospitalProfile.getUid().isBlank()) {

            throw new IllegalArgumentException(
                    "Hospital UID cannot be empty."
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

    // =========================================================
    // GET HOSPITAL PROFILE
    // =========================================================

    public HospitalProfile getHospitalProfile(
            String uid) {

        if (uid == null || uid.isBlank()) {

            throw new IllegalArgumentException(
                    "Hospital UID cannot be empty."
            );
        }

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

            HospitalProfile hospital =
                    document.toObject(
                            HospitalProfile.class
                    );

            if (hospital != null &&
                    (hospital.getUid() == null ||
                     hospital.getUid().isBlank())) {

                hospital.setUid(
                        document.getId()
                );
            }

            return hospital;

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve hospital profile.",
                    e
            );
        }
    }

    // =========================================================
    // GET ALL HOSPITALS
    // =========================================================

    public List<HospitalProfile> getAllHospitals() {

        List<HospitalProfile> hospitals =
                new ArrayList<>();

        try {

            QuerySnapshot snapshot =
                    db.collection("hospitals")
                            .get()
                            .get();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                if (!document.exists()) {
                    continue;
                }

                HospitalProfile hospital =
                        document.toObject(
                                HospitalProfile.class
                        );

                if (hospital == null) {
                    continue;
                }

                // -------------------------------------------------
                // Make sure UID is available.
                // If UID was not stored inside the document,
                // use Firestore document ID.
                // -------------------------------------------------

                if (hospital.getUid() == null ||
                        hospital.getUid().isBlank()) {

                    hospital.setUid(
                            document.getId()
                    );
                }

                hospitals.add(hospital);
            }

            System.out.println(
                    "Total hospitals loaded: "
                            + hospitals.size()
            );

            return hospitals;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve hospitals.",
                    e
            );
        }
    }

    // =========================================================
    // UPDATE HOSPITAL PROFILE
    // =========================================================

    public void updateHospitalProfile(
            HospitalProfile hospitalProfile) {

        if (hospitalProfile == null) {

            throw new IllegalArgumentException(
                    "Hospital profile cannot be null."
            );
        }

        if (hospitalProfile.getUid() == null ||
                hospitalProfile.getUid().isBlank()) {

            throw new IllegalArgumentException(
                    "Hospital UID cannot be empty."
            );
        }

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