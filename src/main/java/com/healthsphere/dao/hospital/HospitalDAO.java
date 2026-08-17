package com.healthsphere.dao.hospital;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.model.HospitalProfile;
import com.healthsphere.util.SessionManager;

import java.util.concurrent.ExecutionException;

public class HospitalDAO {

    private final Firestore db;

    public HospitalDAO() {
        this.db = FirebaseConfig.getFirestore();
    }

    /**
     * Get the UID of the currently logged-in hospital.
     */
    private String getCurrentHospitalUid() {

        return SessionManager
                .getCurrentUser()
                .getUid();
    }

    /**
     * Get the current hospital's profile from Firestore.
     *
     * Firestore path:
     * hospitals/{uid}
     */
    public HospitalProfile getHospitalProfile() {

        String uid = getCurrentHospitalUid();

        try {

            DocumentReference documentReference =
                    db.collection("hospitals")
                      .document(uid);

            ApiFuture<DocumentSnapshot> future =
                    documentReference.get();

            DocumentSnapshot document =
                    future.get();

            if (!document.exists()) {
                return null;
            }

            return document.toObject(HospitalProfile.class);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Interrupted while retrieving hospital profile.",
                    e
            );

        } catch (ExecutionException e) {

            throw new RuntimeException(
                    "Failed to retrieve hospital profile.",
                    e
            );
        }
    }

    /**
     * Create/save the current hospital's profile.
     *
     * Firestore path:
     * hospitals/{uid}
     */
    public void saveHospitalProfile(HospitalProfile hospitalProfile) {

        if (hospitalProfile == null) {
            throw new IllegalArgumentException(
                    "Hospital profile cannot be null."
            );
        }

        String uid = getCurrentHospitalUid();

        try {

            DocumentReference documentReference =
                    db.collection("hospitals")
                      .document(uid);

            ApiFuture<WriteResult> future =
                    documentReference.set(hospitalProfile);

            future.get();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Interrupted while saving hospital profile.",
                    e
            );

        } catch (ExecutionException e) {

            throw new RuntimeException(
                    "Failed to save hospital profile.",
                    e
            );
        }
    }

    /**
     * Update the current hospital's profile.
     *
     * Firestore path:
     * hospitals/{uid}
     */
    public void updateHospitalProfile(HospitalProfile hospitalProfile) {

        if (hospitalProfile == null) {
            throw new IllegalArgumentException(
                    "Hospital profile cannot be null."
            );
        }

        String uid = getCurrentHospitalUid();

        try {

            DocumentReference documentReference =
                    db.collection("hospitals")
                      .document(uid);

            ApiFuture<WriteResult> future =
                    documentReference.set(
                            hospitalProfile
                    );

            future.get();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Interrupted while updating hospital profile.",
                    e
            );

        } catch (ExecutionException e) {

            throw new RuntimeException(
                    "Failed to update hospital profile.",
                    e
            );
        }
    }

    /**
     * Check whether the current hospital has a profile.
     */
    public boolean hospitalProfileExists() {

        String uid = getCurrentHospitalUid();

        try {

            DocumentReference documentReference =
                    db.collection("hospitals")
                      .document(uid);

            ApiFuture<DocumentSnapshot> future =
                    documentReference.get();

            DocumentSnapshot document =
                    future.get();

            return document.exists();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Interrupted while checking hospital profile.",
                    e
            );

        } catch (ExecutionException e) {

            throw new RuntimeException(
                    "Failed to check hospital profile.",
                    e
            );
        }
    }
}