package com.healthsphere.dao.authentication;

import java.util.ArrayList;
import java.util.List;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
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

        if (doctorProfile == null) {
            throw new IllegalArgumentException(
                    "Doctor profile cannot be null."
            );
        }

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

            DoctorProfile doctor =
                    document.toObject(
                            DoctorProfile.class
                    );

            if (doctor != null &&
                    (doctor.getUid() == null ||
                     doctor.getUid().isBlank())) {

                doctor.setUid(
                        document.getId()
                );
            }

            return doctor;

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
    // GET ALL DOCTORS
    // ============================================================

    public List<DoctorProfile> getAllDoctors() {

        List<DoctorProfile> doctors =
                new ArrayList<>();

        try {

            QuerySnapshot snapshot =
                    db.collection("doctors")
                            .get()
                            .get();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                if (!document.exists()) {
                    continue;
                }

                DoctorProfile doctor =
                        document.toObject(
                                DoctorProfile.class
                        );

                if (doctor != null) {

                    // Make sure UID is available
                    if (doctor.getUid() == null ||
                            doctor.getUid().isBlank()) {

                        doctor.setUid(
                                document.getId()
                        );
                    }

                    doctors.add(doctor);
                }
            }

            return doctors;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve doctors.",
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