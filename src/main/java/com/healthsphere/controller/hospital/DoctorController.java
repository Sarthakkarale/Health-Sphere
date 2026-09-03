package com.healthsphere.controller.hospital;

import com.healthsphere.dao.hospital.DoctorDAO;
import com.healthsphere.model.DoctorProfile;
import com.healthsphere.model.HospitalDoctor;
import com.healthsphere.model.HospitalDoctorDetails;

import java.util.ArrayList;
import java.util.List;

public class DoctorController {

    // =========================================================
    // DAO
    // =========================================================

    private final DoctorDAO doctorDAO;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public DoctorController() {

        this.doctorDAO = new DoctorDAO();
    }

    // =========================================================
    // ADD DOCTOR
    // =========================================================

    /**
     * Associates an existing DoctorProfile with the
     * currently logged-in hospital.
     *
     * The doctor profile itself is NOT modified.
     *
     * @param doctorId       DoctorProfile UID
     * @param departmentId   Hospital department
     * @param qualification Hospital-specific qualification
     * @param status         Active / Inactive / On Leave
     * @return generated HospitalDoctor association ID
     */
    public String addDoctor(
            String doctorId,
            String departmentId,
            String qualification,
            String status) {

        validateDoctorId(doctorId);
        validateDepartment(departmentId);
        validateQualification(qualification);
        validateStatus(status);

        // -----------------------------------------------------
        // Verify shared doctor profile exists
        // -----------------------------------------------------

        DoctorProfile doctorProfile =
                doctorDAO.getDoctorProfile(
                        doctorId.trim()
                );

        if (doctorProfile == null) {

            throw new IllegalArgumentException(
                    "Doctor profile not found."
            );
        }

        // -----------------------------------------------------
        // Prevent duplicate active association
        // -----------------------------------------------------

        if (doctorDAO.doctorAlreadyAssociated(
                doctorId.trim()
        )) {

            throw new IllegalArgumentException(
                    "This doctor is already associated "
                            + "with this hospital."
            );
        }

        // -----------------------------------------------------
        // Create hospital-specific association
        // -----------------------------------------------------

        HospitalDoctor hospitalDoctor =
                new HospitalDoctor();

        hospitalDoctor.setDoctorId(
                doctorId.trim()
        );

        hospitalDoctor.setDepartmentId(
                departmentId.trim()
        );

        hospitalDoctor.setQualification(
                qualification.trim()
        );

        hospitalDoctor.setStatus(
                status.trim()
        );

        hospitalDoctor.setActive(true);

        // -----------------------------------------------------
        // Save association
        // -----------------------------------------------------

        return doctorDAO.createDoctorAssociation(
                hospitalDoctor
        );
    }

    // =========================================================
    // GET ALL DOCTOR DETAILS
    // =========================================================

    /**
     * Returns hospital doctors combined with their
     * shared DoctorProfile.
     *
     * HospitalDoctor
     * +
     * DoctorProfile
     * =
     * HospitalDoctorDetails
     */
    public List<HospitalDoctorDetails> getAllDoctorDetails() {

        List<HospitalDoctorDetails> result =
                new ArrayList<>();

        List<HospitalDoctor> associations =
                doctorDAO.getAllDoctors();

        for (HospitalDoctor association :
                associations) {

            if (association == null) {
                continue;
            }

            try {

                DoctorProfile profile =
                        doctorDAO.getDoctorProfile(
                                association.getDoctorId()
                        );

                if (profile == null) {

                    System.err.println(
                            "Doctor profile missing for UID: "
                                    + association.getDoctorId()
                    );

                    continue;
                }

                result.add(
                        new HospitalDoctorDetails(
                                association,
                                profile
                        )
                );

            } catch (Exception e) {

                /*
                 * If one doctor profile cannot be loaded,
                 * do not stop the complete doctor directory.
                 */

                System.err.println(
                        "Unable to load doctor profile for UID "
                                + association.getDoctorId()
                                + ": "
                                + e.getMessage()
                );
            }
        }

        return result;
    }

    // =========================================================
    // GET ALL HOSPITAL DOCTORS
    // =========================================================

    /**
     * Returns all active HospitalDoctor associations
     * belonging to the current hospital.
     */
    public List<HospitalDoctor> getAllDoctors() {

        return doctorDAO.getAllDoctors();
    }

    // =========================================================
    // GET DOCTOR DETAILS
    // =========================================================

    /**
     * Gets one hospital doctor association and combines it
     * with the shared DoctorProfile.
     *
     * @param associationId HospitalDoctor association ID
     */
    public HospitalDoctorDetails getDoctorDetails(
            String associationId) {

        validateAssociationId(associationId);

        HospitalDoctor association =
                doctorDAO.getDoctorById(
                        associationId.trim()
                );

        if (association == null) {

            throw new IllegalArgumentException(
                    "Hospital doctor not found."
            );
        }

        DoctorProfile profile =
                doctorDAO.getDoctorProfile(
                        association.getDoctorId()
                );

        if (profile == null) {

            throw new IllegalArgumentException(
                    "Doctor profile not found."
            );
        }

        return new HospitalDoctorDetails(
                association,
                profile
        );
    }

    // =========================================================
    // GET ALL SHARED DOCTOR PROFILES
    // =========================================================

    /**
     * Gets all existing DoctorProfile records from:
     *
     * doctors/{doctorUid}
     *
     * This method is used by the Add Doctor screen so that
     * the hospital administrator does not have to manually
     * enter a Firebase UID.
     */
    public List<DoctorProfile> getAllDoctorProfiles() {

        return doctorDAO.getAllDoctorProfiles();
    }

    // =========================================================
    // UPDATE DOCTOR
    // =========================================================

    /**
     * Updates only hospital-specific doctor information.
     *
     * DoctorProfile is NOT modified here.
     */
    public void updateDoctor(
            String associationId,
            String departmentId,
            String qualification,
            String status) {

        validateAssociationId(associationId);
        validateDepartment(departmentId);
        validateQualification(qualification);
        validateStatus(status);

        // -----------------------------------------------------
        // Load existing association
        // -----------------------------------------------------

        HospitalDoctor existing =
                doctorDAO.getDoctorById(
                        associationId.trim()
                );

        if (existing == null) {

            throw new IllegalArgumentException(
                    "Hospital doctor not found."
            );
        }

        // -----------------------------------------------------
        // Update hospital-specific fields only
        // -----------------------------------------------------

        existing.setDepartmentId(
                departmentId.trim()
        );

        existing.setQualification(
                qualification.trim()
        );

        existing.setStatus(
                status.trim()
        );

        // -----------------------------------------------------
        // Save
        // -----------------------------------------------------

        doctorDAO.updateDoctor(
                existing
        );
    }

    // =========================================================
    // REMOVE DOCTOR
    // =========================================================

    /**
     * Soft-removes a doctor from the hospital.
     *
     * Firestore document remains.
     * active becomes false.
     */
    public void removeDoctor(
            String associationId) {

        validateAssociationId(associationId);

        doctorDAO.deactivateDoctor(
                associationId.trim()
        );
    }

    // =========================================================
    // REACTIVATE DOCTOR
    // =========================================================

    /**
     * Reactivates a previously removed hospital doctor.
     */
    public void reactivateDoctor(
            String associationId) {

        validateAssociationId(associationId);

        doctorDAO.reactivateDoctor(
                associationId.trim()
        );
    }

    // =========================================================
    // CHECK DUPLICATE DOCTOR
    // =========================================================

    /**
     * Checks whether a DoctorProfile is already actively
     * associated with the current hospital.
     */
    public boolean doctorAlreadyAssociated(
            String doctorId) {

        if (doctorId == null
                || doctorId.trim().isEmpty()) {

            return false;
        }

        return doctorDAO.doctorAlreadyAssociated(
                doctorId.trim()
        );
    }

    // =========================================================
    // CHECK ASSOCIATION EXISTS
    // =========================================================

    /**
     * Checks whether a HospitalDoctor association exists
     * in the current hospital.
     */
    public boolean doctorExists(
            String associationId) {

        if (associationId == null
                || associationId.trim().isEmpty()) {

            return false;
        }

        return doctorDAO.doctorExists(
                associationId.trim()
        );
    }

    // =========================================================
    // VALIDATE DOCTOR ID
    // =========================================================

    private void validateDoctorId(
            String doctorId) {

        if (doctorId == null
                || doctorId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Doctor UID is required."
            );
        }
    }

    // =========================================================
    // VALIDATE ASSOCIATION ID
    // =========================================================

    private void validateAssociationId(
            String associationId) {

        if (associationId == null
                || associationId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Doctor association ID is required."
            );
        }
    }

    // =========================================================
    // VALIDATE DEPARTMENT
    // =========================================================

    private void validateDepartment(
            String departmentId) {

        if (departmentId == null
                || departmentId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Department is required."
            );
        }
    }

    // =========================================================
    // VALIDATE QUALIFICATION
    // =========================================================

    private void validateQualification(
            String qualification) {

        if (qualification == null
                || qualification.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Qualification is required."
            );
        }
    }

    // =========================================================
    // VALIDATE STATUS
    // =========================================================

    private void validateStatus(
            String status) {

        if (status == null
                || status.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Doctor status is required."
            );
        }

        String normalized =
                status.trim();

        if (!normalized.equalsIgnoreCase("Active")
                && !normalized.equalsIgnoreCase("Inactive")
                && !normalized.equalsIgnoreCase("On Leave")) {

            throw new IllegalArgumentException(
                    "Invalid doctor status. "
                            + "Allowed values: Active, "
                            + "Inactive, On Leave."
            );
        }
    }
}