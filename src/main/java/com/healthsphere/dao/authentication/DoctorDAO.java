package com.healthsphere.dao.authentication;

import java.util.ArrayList;
import java.util.List;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.model.DoctorProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorDAO {

    private static final String COLLECTION_NAME =
            "doctors";

    private static final String CREDENTIALS_COLLECTION =
            "doctor_credentials";

    private static final String USERS_COLLECTION =
            "users";

    private static final String VERIFICATION_STATUS_FIELD =
            "verificationStatus";

    private final Firestore firestore;

    public DoctorDAO() {
        this.firestore =
                FirebaseConfig.getFirestore();
    }

    /**
     * Create a doctor profile.
     *
     * Also creates a PENDING credential record.
     */
    public void createDoctorProfile(
            DoctorProfile doctorProfile) {

        validateDoctorProfile(doctorProfile);

        try {

            firestore
                    .collection(COLLECTION_NAME)
                    .document(doctorProfile.getUid())
                    .set(doctorProfile)
                    .get();

            createPendingCredential(
                    doctorProfile
            );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Doctor profile creation was interrupted.",
                    e
            );

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

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Doctor profile retrieval was interrupted.",
                    e
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

            for (DocumentSnapshot document : snapshot.getDocuments()) {

                if (document.exists()) {

                    DoctorProfile doctor =
                            document.toObject(
                                    DoctorProfile.class
                            );

                    if (doctor != null) {
                        if (doctor.getUid() == null || doctor.getUid().isBlank()) {
                            doctor.setUid(document.getId());
                        }
                        doctors.add(doctor);
                    }
                }
            }

            return doctors;

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Doctor profile retrieval was interrupted.",
                    e
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to retrieve doctor profiles.",
                    e
            );
        }
    }

    public List<DoctorProfile> getAllDoctors() {
        return getAllDoctorProfiles();
    }

    /**
     * Update an existing doctor profile.
     */
    public void updateDoctorProfile(
            DoctorProfile doctorProfile) {

        validateDoctorProfile(doctorProfile);

        try {

            firestore
                    .collection(COLLECTION_NAME)
                    .document(doctorProfile.getUid())
                    .set(doctorProfile)
                    .get();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Doctor profile update was interrupted.",
                    e
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to update doctor profile.",
                    e
            );
        }
    }

    /**
     * Update doctor credentialing / verification status.
     *
     * Supported values:
     *
     * PENDING
     * APPROVED
     * VERIFIED
     * REJECTED
     *
     * VERIFIED is converted to APPROVED.
     */
    public void updateVerificationStatus(
            String doctorUid,
            String verificationStatus) {

        validateUid(doctorUid);

        if (verificationStatus == null ||
                verificationStatus.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Verification status is required."
            );
        }

        String normalizedStatus =
                verificationStatus
                        .trim()
                        .toUpperCase();

        if (!normalizedStatus.equals("VERIFIED") &&
                !normalizedStatus.equals("APPROVED") &&
                !normalizedStatus.equals("PENDING") &&
                !normalizedStatus.equals("REJECTED")) {

            throw new IllegalArgumentException(
                    "Invalid verification status. " +
                    "Allowed values are VERIFIED, APPROVED, " +
                    "PENDING, or REJECTED."
            );
        }

        try {

            DocumentSnapshot doctorDocument =
                    firestore
                            .collection(COLLECTION_NAME)
                            .document(doctorUid)
                            .get()
                            .get();

            if (!doctorDocument.exists()) {

                throw new IllegalArgumentException(
                        "Doctor profile not found: "
                                + doctorUid
                );
            }

            DoctorProfile doctor =
                    doctorDocument.toObject(
                            DoctorProfile.class
                    );

            /*
             * VERIFIED is maintained for compatibility
             * but stored as APPROVED in credentialing.
             */
            String credentialStatus =
                    normalizedStatus.equals("VERIFIED")
                            ? "APPROVED"
                            : normalizedStatus;

            /*
             * Update/create dedicated credential record.
             */
            createOrUpdateCredential(
                    doctorUid,
                    doctor,
                    credentialStatus
            );

            /*
             * Keep status inside doctors/{uid}.
             */
            Map<String, Object> doctorUpdate =
                    new HashMap<>();

            doctorUpdate.put(
                    VERIFICATION_STATUS_FIELD,
                    credentialStatus
            );

            firestore
                    .collection(COLLECTION_NAME)
                    .document(doctorUid)
                    .update(doctorUpdate)
                    .get();

            /*
             * Update users/{uid} status.
             */
            if (credentialStatus.equals("APPROVED")) {

                updateUserStatus(
                        doctorUid,
                        "ACTIVE"
                );

            } else if (
                    credentialStatus.equals("REJECTED")
            ) {

                updateUserStatus(
                        doctorUid,
                        "REJECTED"
                );
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Doctor verification operation was interrupted.",
                    e
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to update doctor verification status.",
                    e
            );
        }
    }

    /**
     * Get current doctor verification status.
     *
     * Checks doctor_credentials first.
     * Falls back to doctors/{uid}.
     */
    public String getVerificationStatus(
            String doctorUid) {

        validateUid(doctorUid);

        try {

            /*
             * Check credential collection first.
             */
            DocumentSnapshot credentialDocument =
                    firestore
                            .collection(
                                    CREDENTIALS_COLLECTION
                            )
                            .document(doctorUid)
                            .get()
                            .get();

            if (credentialDocument.exists()) {

                String credentialStatus =
                        credentialDocument.getString(
                                "credentialStatus"
                        );

                if (credentialStatus != null &&
                        !credentialStatus.trim().isEmpty()) {

                    return credentialStatus;
                }

                String verificationStatus =
                        credentialDocument.getString(
                                VERIFICATION_STATUS_FIELD
                        );

                if (verificationStatus != null &&
                        !verificationStatus.trim().isEmpty()) {

                    return verificationStatus;
                }
            }

            /*
             * Fallback to doctors/{uid}.
             */
            DocumentSnapshot doctorDocument =
                    firestore
                            .collection(COLLECTION_NAME)
                            .document(doctorUid)
                            .get()
                            .get();

            if (!doctorDocument.exists()) {
                return null;
            }

            String status =
                    doctorDocument.getString(
                            VERIFICATION_STATUS_FIELD
                    );

            if (status == null ||
                    status.trim().isEmpty()) {

                return "PENDING";
            }

            return status;

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Doctor verification retrieval was interrupted.",
                    e
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to retrieve doctor verification status.",
                    e
            );
        }
    }

    /**
     * Create a PENDING credential record if it
     * does not already exist.
     */
    public void createPendingCredential(
            DoctorProfile doctorProfile) {

        validateDoctorProfile(doctorProfile);

        try {

            DocumentSnapshot existing =
                    firestore
                            .collection(
                                    CREDENTIALS_COLLECTION
                            )
                            .document(
                                    doctorProfile.getUid()
                            )
                            .get()
                            .get();

            if (existing.exists()) {
                return;
            }

            createOrUpdateCredential(
                    doctorProfile.getUid(),
                    doctorProfile,
                    "PENDING"
            );

            /*
             * Keep doctors/{uid} compatible with
             * the existing Doctor UI.
             */
            Map<String, Object> doctorUpdate =
                    new HashMap<>();

            doctorUpdate.put(
                    VERIFICATION_STATUS_FIELD,
                    "PENDING"
            );

            firestore
                    .collection(COLLECTION_NAME)
                    .document(doctorProfile.getUid())
                    .update(doctorUpdate)
                    .get();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Pending credential creation was interrupted.",
                    e
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to create pending doctor credential.",
                    e
            );
        }
    }

    /**
     * Create or update the dedicated credential document.
     */
    private void createOrUpdateCredential(
            String doctorUid,
            DoctorProfile doctor,
            String credentialStatus) {

        try {

            Map<String, Object> credential =
                    new HashMap<>();

            credential.put(
                    "doctorId",
                    doctorUid
            );

            credential.put(
                    "credentialId",
                    doctorUid
            );

            credential.put(
                    "firstName",
                    safe(
                            doctor != null
                                    ? doctor.getFirstName()
                                    : null
                    )
            );

            credential.put(
                    "lastName",
                    safe(
                            doctor != null
                                    ? doctor.getLastName()
                                    : null
                    )
            );

            credential.put(
                    "email",
                    safe(
                            doctor != null
                                    ? doctor.getEmail()
                                    : null
                    )
            );

            credential.put(
                    "phone",
                    safe(
                            doctor != null
                                    ? doctor.getPhone()
                                    : null
                    )
            );

            credential.put(
                    "registrationNumber",
                    safe(
                            doctor != null
                                    ? doctor.getRegistrationNumber()
                                    : null
                    )
            );

            credential.put(
                    "specialization",
                    safe(
                            doctor != null
                                    ? doctor.getSpecialization()
                                    : null
                    )
            );

            credential.put(
                    "experience",
                    safe(
                            doctor != null
                                    ? doctor.getExperience()
                                    : null
                    )
            );

            credential.put(
                    "hospitalAffiliation",
                    safe(
                            doctor != null
                                    ? doctor.getHospitalAffiliation()
                                    : null
                    )
            );

            credential.put(
                    "medicalCouncil",
                    safe(
                            doctor != null
                                    ? doctor.getMedicalCouncil()
                                    : null
                    )
            );

            credential.put(
                    "credentialStatus",
                    credentialStatus
            );

            credential.put(
                    VERIFICATION_STATUS_FIELD,
                    credentialStatus
            );

            credential.put(
                    "updatedAt",
                    FieldValue.serverTimestamp()
            );

            firestore
                    .collection(CREDENTIALS_COLLECTION)
                    .document(doctorUid)
                    .set(credential)
                    .get();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Doctor credential operation was interrupted.",
                    e
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to create/update doctor credential.",
                    e
            );
        }
    }

    /**
     * Update users/{doctorUid}.status.
     */
    private void updateUserStatus(
            String doctorUid,
            String status) {

        try {

            Map<String, Object> update =
                    new HashMap<>();

            update.put(
                    "status",
                    status
            );

            firestore
                    .collection(USERS_COLLECTION)
                    .document(doctorUid)
                    .update(update)
                    .get();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "User status update was interrupted.",
                    e
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to update user status.",
                    e
            );
        }
    }

    /**
     * Delete doctor profile and credential record.
     */
    public void deleteDoctorProfile(
            String doctorUid) {

        validateUid(doctorUid);

        try {

            /*
             * Delete doctor profile.
             */
            firestore
                    .collection(COLLECTION_NAME)
                    .document(doctorUid)
                    .delete()
                    .get();

            /*
             * Delete credential record.
             */
            firestore
                    .collection(CREDENTIALS_COLLECTION)
                    .document(doctorUid)
                    .delete()
                    .get();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Doctor deletion was interrupted.",
                    e
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to delete doctor profile.",
                    e
            );
        }
    }

    /**
     * Safely handle nullable profile fields.
     */
    private String safe(String value) {

        return value == null
                ? ""
                : value;
    }

    /**
     * Validate complete doctor profile.
     */
    private void validateDoctorProfile(
            DoctorProfile doctorProfile) {

        if (doctorProfile == null) {

            throw new IllegalArgumentException(
                    "Doctor profile cannot be null."
            );
        }

        validateUid(
                doctorProfile.getUid()
        );
    }

    /**
     * Validate Firebase UID.
     */
    private void validateUid(
            String uid) {

        if (uid == null ||
                uid.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Doctor UID is required."
            );
        }
    }
}