package com.healthsphere.dao.doctor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.model.DoctorProfile;

public class DoctorDAO {

    private final Firestore db;

    public DoctorDAO() {
        this.db = FirebaseConfig.getFirestore();
    }

    // =========================================================
    // GET ALL DOCTORS
    // =========================================================

    public List<DoctorProfile> getAllDoctors() {

        List<DoctorProfile> doctors =
                new ArrayList<>();

        try {

            ApiFuture<QuerySnapshot> future =
                    db.collection("doctors")
                            .get();

            QuerySnapshot snapshot =
                    future.get();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                if (document.exists()) {

                    DoctorProfile doctor =
                            document.toObject(
                                    DoctorProfile.class
                            );

                    if (doctor != null) {

                        doctors.add(doctor);
                    }
                }
            }

            return doctors;

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Interrupted while retrieving doctors.",
                    e
            );

        } catch (ExecutionException e) {

            throw new RuntimeException(
                    "Failed to retrieve doctors.",
                    e
            );
        }
    }

    // =========================================================
    // GET DOCTOR BY UID
    // =========================================================

    public DoctorProfile getDoctorById(
            String uid) {

        if (uid == null || uid.isBlank()) {
            return null;
        }

        try {

            DocumentSnapshot document =
                    db.collection("doctors")
                            .document(uid)
                            .get()
                            .get();

            if (!document.exists()) {
                return null;
            }

            return document.toObject(
                    DoctorProfile.class
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to retrieve doctor.",
                    e
            );
        }
    }
}
