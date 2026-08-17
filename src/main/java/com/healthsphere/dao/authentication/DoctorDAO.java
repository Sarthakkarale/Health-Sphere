package com.healthsphere.dao.authentication;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.model.DoctorProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorDAO {

    private static final String COLLECTION_NAME = "doctors";

    /*
     * Firestore field used by Admin for doctor verification.
     *
     * Possible values:
     * VERIFIED
     * PENDING
     * REJECTED
     */
    private static final String VERIFICATION_STATUS_FIELD =
            "verificationStatus";

    private final Firestore firestore;

    public DoctorDAO() {
        this.firestore = FirebaseConfig.getFirestore();
    }

    /**
     * Create a doctor profile.
     */
    public void createDoctorProfile(
            DoctorProfile doctorProfile) {

        if (doctorProfile == null) {
            throw new IllegalArgumentException(
                    "Doctor profile cannot be null."
            );
        }

        if (doctorProfile.getUid() == null ||
                doctorProfile.getUid().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Doctor UID is required."
            );
        }

        try {

            /*
             * Store the normal DoctorProfile.
             */
            firestore
                    .collection(COLLECTION_NAME)
                    .document(doctorProfile.getUid())
                    .set(doctorProfile)
                    .get();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to create doctor profile.",
                    e
            );
        }
    }

    /**
     * Get a doctor profile by Firebase UID.
     */
    public DoctorProfile getDoctorProfile(
            String uid) {

        validateUid(uid);

        try {

            DocumentSnapshot document =
                    firestore
                            .collection(COLLECTION_NAME)
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
                    "Failed to retrieve doctor profile.",
                    e
            );
        }
    }

    /**
     * Get all doctor profiles.
     *
     * Used by the Admin Doctor Directory.
     */
    public List<DoctorProfile> getAllDoctorProfiles() {

        try {

            QuerySnapshot snapshot =
                    firestore
                            .collection(COLLECTION_NAME)
                            .get()
                            .get();

            List<DoctorProfile> doctors =
                    new ArrayList<>();

            for (
                    DocumentSnapshot document :
                    snapshot.getDocuments()
            ) {

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

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to retrieve doctor profiles.",
                    e
            );
        }
    }

    /**
     * Update an existing doctor profile.
     */
    public void updateDoctorProfile(
            DoctorProfile doctorProfile) {

        if (doctorProfile == null) {

            throw new IllegalArgumentException(
                    "Doctor profile cannot be null."
            );
        }

        validateUid(
                doctorProfile.getUid()
        );

        try {

            firestore
                    .collection(COLLECTION_NAME)
                    .document(doctorProfile.getUid())
                    .set(doctorProfile)
                    .get();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to update doctor profile.",
                    e
            );
        }
    }

    /**
     * Update the Admin verification status of a doctor.
     *
     * This does NOT create another Doctor model.
     *
     * It stores the status directly in the existing
     * doctor Firestore document.
     *
     * Example:
     *
     * verificationStatus = VERIFIED
     * verificationStatus = PENDING
     * verificationStatus = REJECTED
     */
    public void updateVerificationStatus(
            String doctorUid,
            String verificationStatus) {

        validateUid(doctorUid);

        if (
                verificationStatus == null ||
                verificationStatus.trim().isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Verification status is required."
            );
        }

        String normalizedStatus =
                verificationStatus
                        .trim()
                        .toUpperCase();

        if (
                !normalizedStatus.equals("VERIFIED") &&
                !normalizedStatus.equals("PENDING") &&
                !normalizedStatus.equals("REJECTED")
        ) {

            throw new IllegalArgumentException(
                    "Invalid verification status. " +
                    "Allowed values are VERIFIED, PENDING, or REJECTED."
            );
        }

        try {

            Map<String, Object> updateData =
                    new HashMap<>();

            updateData.put(
                    VERIFICATION_STATUS_FIELD,
                    normalizedStatus
            );

            firestore
                    .collection(COLLECTION_NAME)
                    .document(doctorUid)
                    .update(updateData)
                    .get();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to update doctor verification status.",
                    e
            );
        }
    }

    /**
     * Get the verification status of a doctor.
     *
     * Returns:
     * VERIFIED
     * PENDING
     * REJECTED
     *
     * If no status exists yet, returns PENDING.
     */
    public String getVerificationStatus(
            String doctorUid) {

        validateUid(doctorUid);

        try {

            DocumentSnapshot document =
                    firestore
                            .collection(COLLECTION_NAME)
                            .document(doctorUid)
                            .get()
                            .get();

            if (!document.exists()) {

                return null;
            }

            String status =
                    document.getString(
                            VERIFICATION_STATUS_FIELD
                    );

            if (
                    status == null ||
                    status.trim().isEmpty()
            ) {

                return "PENDING";
            }

            return status;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to retrieve doctor verification status.",
                    e
            );
        }
    }

    /**
     * Delete a doctor profile.
     */
    public void deleteDoctorProfile(
            String doctorUid) {

        validateUid(doctorUid);

        try {

            firestore
                    .collection(COLLECTION_NAME)
                    .document(doctorUid)
                    .delete()
                    .get();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to delete doctor profile.",
                    e
            );
        }
    }

    /**
     * Validate Firebase UID.
     */
    private void validateUid(
            String uid) {

        if (
                uid == null ||
                uid.trim().isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Doctor UID is required."
            );
        }
    }
}