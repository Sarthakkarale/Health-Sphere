package com.healthsphere.dao.doctor;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.model.DoctorAvailability;

import java.util.concurrent.ExecutionException;

/**
 * DAO for Doctor Availability.
 *
 * Firestore:
 *
 * doctorAvailability/{doctorUid}
 *
 * Responsibilities:
 * - Read doctor availability
 * - Create/update doctor availability
 *
 * No JavaFX code belongs here.
 */
public class DoctorAvailabilityDAO {

    private static final String COLLECTION =
            "doctorAvailability";

    private final Firestore firestore;

    public DoctorAvailabilityDAO() {

        this.firestore =
                FirebaseConfig.getFirestore();
    }

    // =========================================================
    // CREATE / UPDATE
    // =========================================================

    public void saveAvailability(
            DoctorAvailability availability)
            throws ExecutionException, InterruptedException {

        if (availability == null) {

            throw new IllegalArgumentException(
                    "Doctor availability cannot be null."
            );
        }

        String doctorUid =
                availability.getDoctorUid();

        if (doctorUid == null
                || doctorUid.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Doctor UID is required."
            );
        }

        DocumentReference documentReference =
                firestore
                        .collection(COLLECTION)
                        .document(doctorUid.trim());

        documentReference
                .set(availability)
                .get();
    }

    // =========================================================
    // READ
    // =========================================================

    public DoctorAvailability getAvailability(
            String doctorUid)
            throws ExecutionException, InterruptedException {

        if (doctorUid == null
                || doctorUid.trim().isEmpty()) {

            return null;
        }

        DocumentSnapshot documentSnapshot =
                firestore
                        .collection(COLLECTION)
                        .document(doctorUid.trim())
                        .get()
                        .get();

        if (!documentSnapshot.exists()) {

            return null;
        }

        return documentSnapshot.toObject(
                DoctorAvailability.class
        );
    }

    // =========================================================
    // DELETE
    // =========================================================

    public void deleteAvailability(
            String doctorUid)
            throws ExecutionException, InterruptedException {

        if (doctorUid == null
                || doctorUid.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Doctor UID is required."
            );
        }

        firestore
                .collection(COLLECTION)
                .document(doctorUid.trim())
                .delete()
                .get();
    }
}