package com.healthsphere.dao.hospital;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.DoctorProfile;
import com.healthsphere.model.HospitalDoctor;
import com.healthsphere.util.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DoctorDAO {

    // =========================================================
    // FIRESTORE
    // =========================================================

    private final Firestore firestore;

    /*
     * IMPORTANT:
     *
     * There are two DoctorDAO classes in this project:
     *
     * 1. com.healthsphere.dao.authentication.DoctorDAO
     * 2. com.healthsphere.dao.hospital.DoctorDAO
     *
     * This DAO is the Hospital DoctorDAO.
     *
     * The authentication DoctorDAO is used only for reading
     * the shared DoctorProfile from:
     *
     * doctors/{doctorUid}
     */
    private final com.healthsphere.dao.authentication.DoctorDAO doctorProfileDAO;

    // =========================================================
    // COLLECTION
    // =========================================================

    private static final String COLLECTION =
            "hospitalDoctors";

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public DoctorDAO() {

        this.firestore =
                FirebaseConfig.getFirestore();

        this.doctorProfileDAO =
                new com.healthsphere.dao.authentication.DoctorDAO();
    }

    // =========================================================
    // GET CURRENT HOSPITAL ID
    // =========================================================

    private String getCurrentHospitalId() {

        if (!SessionManager.isLoggedIn()) {

            throw new IllegalStateException(
                    "No active hospital session."
            );
        }

        String hospitalId =
                SessionManager
                        .getCurrentUser()
                        .getUid();

        if (hospitalId == null
                || hospitalId.trim().isEmpty()) {

            throw new IllegalStateException(
                    "Hospital ID is not available."
            );
        }

        return hospitalId.trim();
    }

    // =========================================================
    // CREATE HOSPITAL DOCTOR ASSOCIATION
    // =========================================================

    public String createDoctorAssociation(
            HospitalDoctor hospitalDoctor) {

        validateHospitalDoctor(
                hospitalDoctor
        );

        String hospitalId =
                getCurrentHospitalId();

        try {

            // -------------------------------------------------
            // Verify that doctor profile exists
            // -------------------------------------------------

            DoctorProfile doctorProfile =
                    doctorProfileDAO.getDoctorProfile(
                            hospitalDoctor.getDoctorId()
                    );

            if (doctorProfile == null) {

                throw new DatabaseException(
                        "Doctor profile not found."
                );
            }

            // -------------------------------------------------
            // Force current hospital ID
            // -------------------------------------------------

            hospitalDoctor.setHospitalId(
                    hospitalId
            );

            // -------------------------------------------------
            // Generate association ID if missing
            // -------------------------------------------------

            if (hospitalDoctor.getAssociationId() == null
                    || hospitalDoctor
                    .getAssociationId()
                    .trim()
                    .isEmpty()) {

                hospitalDoctor.setAssociationId(
                        "HDOC-"
                                + UUID.randomUUID()
                                .toString()
                                .substring(0, 8)
                                .toUpperCase()
                );
            }

            // -------------------------------------------------
            // Check duplicate active association
            // -------------------------------------------------

            if (doctorAlreadyAssociated(
                    hospitalDoctor.getDoctorId()
            )) {

                throw new DatabaseException(
                        "This doctor is already associated "
                                + "with this hospital."
                );
            }

            // -------------------------------------------------
            // Save association
            // -------------------------------------------------

            firestore.collection(COLLECTION)
                    .document(
                            hospitalDoctor
                                    .getAssociationId()
                    )
                    .set(hospitalDoctor)
                    .get();

            System.out.println(
                    "Hospital doctor association created: "
                            + hospitalDoctor
                            .getAssociationId()
            );

            return hospitalDoctor
                    .getAssociationId();

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to create hospital doctor association.",
                    e
            );
        }
    }

    // =========================================================
    // GET ALL ACTIVE DOCTORS OF CURRENT HOSPITAL
    // =========================================================

    public List<HospitalDoctor> getAllDoctors() {

        String hospitalId =
                getCurrentHospitalId();

        try {

            List<HospitalDoctor> doctors =
                    new ArrayList<>();

            List<QueryDocumentSnapshot> documents =
                    firestore
                            .collection(COLLECTION)
                            .whereEqualTo(
                                    "hospitalId",
                                    hospitalId
                            )
                            .whereEqualTo(
                                    "active",
                                    true
                            )
                            .get()
                            .get()
                            .getDocuments();

            for (QueryDocumentSnapshot document :
                    documents) {

                HospitalDoctor doctor =
                        document.toObject(
                                HospitalDoctor.class
                        );

                if (doctor != null) {

                    doctors.add(doctor);
                }
            }

            return doctors;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve hospital doctors.",
                    e
            );
        }
    }

    // =========================================================
    // GET DOCTOR ASSOCIATION BY ID
    // =========================================================

    public HospitalDoctor getDoctorById(
            String associationId) {

        if (associationId == null
                || associationId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Doctor association ID cannot be empty."
            );
        }

        String hospitalId =
                getCurrentHospitalId();

        try {

            DocumentSnapshot document =
                    firestore
                            .collection(COLLECTION)
                            .document(
                                    associationId.trim()
                            )
                            .get()
                            .get();

            if (!document.exists()) {

                throw new DatabaseException(
                        "Hospital doctor association not found."
                );
            }

            HospitalDoctor doctor =
                    document.toObject(
                            HospitalDoctor.class
                    );

            if (doctor == null) {

                throw new DatabaseException(
                        "Unable to read hospital doctor association."
                );
            }

            // -------------------------------------------------
            // Security check
            // -------------------------------------------------

            if (!hospitalId.equals(
                    doctor.getHospitalId()
            )) {

                throw new DatabaseException(
                        "Doctor does not belong to "
                                + "the current hospital."
                );
            }

            return doctor;

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve hospital doctor.",
                    e
            );
        }
    }

    // =========================================================
    // GET SHARED DOCTOR PROFILE
    // =========================================================

    /*
     * Reads:
     *
     * doctors/{doctorUid}
     *
     * This method does NOT read hospitalDoctors.
     */

    public DoctorProfile getDoctorProfile(
            String doctorId) {

        if (doctorId == null
                || doctorId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Doctor UID cannot be empty."
            );
        }

        try {

            return doctorProfileDAO
                    .getDoctorProfile(
                            doctorId.trim()
                    );

        } catch (Exception e) {

            if (e instanceof DatabaseException) {
                throw e;
            }

            throw new DatabaseException(
                    "Unable to retrieve doctor profile.",
                    e
            );
        }
    }

    // =========================================================
    // GET ALL SHARED DOCTOR PROFILES
    // =========================================================

    /*
     * Used by the Hospital Add Doctor screen.
     *
     * Reads:
     *
     * doctors
     *
     * and returns all existing DoctorProfile objects.
     */

    public List<DoctorProfile> getAllDoctorProfiles() {

        try {

            List<DoctorProfile> doctors =
                    new ArrayList<>();

            List<QueryDocumentSnapshot> documents =
                    firestore
                            .collection("doctors")
                            .get()
                            .get()
                            .getDocuments();

            for (QueryDocumentSnapshot document :
                    documents) {

                DoctorProfile doctor =
                        document.toObject(
                                DoctorProfile.class
                        );

                if (doctor == null) {
                    continue;
                }

                /*
                 * Make sure the UID is populated.
                 *
                 * Firestore document ID is the ultimate
                 * source of the UID in case the stored
                 * object does not contain it.
                 */

                if (doctor.getUid() == null
                        || doctor.getUid()
                        .trim()
                        .isEmpty()) {

                    doctor.setUid(
                            document.getId()
                    );
                }

                doctors.add(doctor);
            }

            return doctors;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve doctor profiles.",
                    e
            );
        }
    }

    // =========================================================
    // UPDATE DOCTOR ASSOCIATION
    // =========================================================

    public void updateDoctor(
            HospitalDoctor hospitalDoctor) {

        validateHospitalDoctor(
                hospitalDoctor
        );

        String hospitalId =
                getCurrentHospitalId();

        try {

            HospitalDoctor existing =
                    getDoctorById(
                            hospitalDoctor
                                    .getAssociationId()
                    );

            /*
             * Never allow update operation to move a doctor
             * to another hospital.
             */

            hospitalDoctor.setHospitalId(
                    hospitalId
            );

            /*
             * Preserve identity fields.
             */

            hospitalDoctor.setAssociationId(
                    existing.getAssociationId()
            );

            hospitalDoctor.setDoctorId(
                    existing.getDoctorId()
            );

            /*
             * Preserve active state.
             */

            hospitalDoctor.setActive(
                    existing.isActive()
            );

            firestore.collection(COLLECTION)
                    .document(
                            existing
                                    .getAssociationId()
                    )
                    .set(hospitalDoctor)
                    .get();

            System.out.println(
                    "Hospital doctor association updated successfully."
            );

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to update hospital doctor.",
                    e
            );
        }
    }

    // =========================================================
    // DEACTIVATE DOCTOR
    // =========================================================

    public void deactivateDoctor(
            String associationId) {

        HospitalDoctor doctor =
                getDoctorById(
                        associationId
                );

        try {

            firestore
                    .collection(COLLECTION)
                    .document(
                            doctor.getAssociationId()
                    )
                    .update(
                            "active",
                            false
                    )
                    .get();

            System.out.println(
                    "Hospital doctor deactivated successfully."
            );

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to remove hospital doctor.",
                    e
            );
        }
    }

    // =========================================================
    // REACTIVATE DOCTOR
    // =========================================================

    public void reactivateDoctor(
            String associationId) {

        HospitalDoctor doctor =
                getDoctorById(
                        associationId
                );

        try {

            firestore
                    .collection(COLLECTION)
                    .document(
                            doctor.getAssociationId()
                    )
                    .update(
                            "active",
                            true
                    )
                    .get();

            System.out.println(
                    "Hospital doctor reactivated successfully."
            );

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to reactivate hospital doctor.",
                    e
            );
        }
    }

    // =========================================================
    // CHECK WHETHER DOCTOR IS ALREADY ASSOCIATED
    // =========================================================

    public boolean doctorAlreadyAssociated(
            String doctorId) {

        if (doctorId == null
                || doctorId.trim().isEmpty()) {

            return false;
        }

        String hospitalId =
                getCurrentHospitalId();

        try {

            List<QueryDocumentSnapshot> documents =
                    firestore
                            .collection(COLLECTION)
                            .whereEqualTo(
                                    "hospitalId",
                                    hospitalId
                            )
                            .whereEqualTo(
                                    "doctorId",
                                    doctorId.trim()
                            )
                            .whereEqualTo(
                                    "active",
                                    true
                            )
                            .get()
                            .get()
                            .getDocuments();

            return !documents.isEmpty();

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to check doctor association.",
                    e
            );
        }
    }

    // =========================================================
    // CHECK ASSOCIATION EXISTS
    // =========================================================

    public boolean doctorExists(
            String associationId) {

        if (associationId == null
                || associationId.trim().isEmpty()) {

            return false;
        }

        String hospitalId =
                getCurrentHospitalId();

        try {

            DocumentSnapshot document =
                    firestore
                            .collection(COLLECTION)
                            .document(
                                    associationId.trim()
                            )
                            .get()
                            .get();

            if (!document.exists()) {
                return false;
            }

            HospitalDoctor doctor =
                    document.toObject(
                            HospitalDoctor.class
                    );

            if (doctor == null) {
                return false;
            }

            return hospitalId.equals(
                    doctor.getHospitalId()
            );

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to check doctor association.",
                    e
            );
        }
    }

    // =========================================================
    // VALIDATE HOSPITAL DOCTOR
    // =========================================================

    private void validateHospitalDoctor(
            HospitalDoctor doctor) {

        if (doctor == null) {

            throw new IllegalArgumentException(
                    "Hospital doctor cannot be null."
            );
        }

        if (doctor.getDoctorId() == null
                || doctor.getDoctorId()
                .trim()
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "Doctor UID is required."
            );
        }

        if (doctor.getDepartmentId() == null
                || doctor.getDepartmentId()
                .trim()
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "Department is required."
            );
        }

        if (doctor.getQualification() == null
                || doctor.getQualification()
                .trim()
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "Qualification is required."
            );
        }

        if (doctor.getStatus() == null
                || doctor.getStatus()
                .trim()
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "Doctor status is required."
            );
        }
    }
}