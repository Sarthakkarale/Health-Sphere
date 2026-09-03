package com.healthsphere.controller.admin;

import com.healthsphere.dao.authentication.DoctorDAO;
import com.healthsphere.model.DoctorProfile;

import java.util.List;

public class DoctorManagementController {

    private final DoctorDAO doctorDAO;

    public DoctorManagementController() {
        this.doctorDAO = new DoctorDAO();
    }

    /**
     * Get all doctors for the Admin Doctor Directory.
     */
    public List<DoctorProfile> getAllDoctors() {

        try {
            return doctorDAO.getAllDoctorProfiles();

        } catch (RuntimeException e) {

            System.err.println(
                    "Failed to load doctors: "
                            + e.getMessage()
            );

            e.printStackTrace();

            return List.of();
        }
    }

    /**
     * Get a single doctor by Firebase UID.
     */
    public DoctorProfile getDoctor(
            String doctorUid) {

        validateUid(doctorUid);

        try {

            return doctorDAO.getDoctorProfile(
                    doctorUid
            );

        } catch (RuntimeException e) {

            System.err.println(
                    "Failed to get doctor: "
                            + e.getMessage()
            );

            return null;
        }
    }

    /**
     * Approve / verify a doctor.
     *
     * Updates:
     * doctors/{uid}
     * doctor_credentials/{uid}
     * users/{uid}
     */
    public boolean verifyDoctor(
            String doctorUid) {

        return updateVerificationStatus(
                doctorUid,
                "APPROVED"
        );
    }

    /**
     * Put doctor credentialing back into PENDING.
     */
    public boolean setDoctorPending(
            String doctorUid) {

        return updateVerificationStatus(
                doctorUid,
                "PENDING"
        );
    }

    /**
     * Reject a doctor's credentialing.
     */
    public boolean rejectDoctor(
            String doctorUid) {

        return updateVerificationStatus(
                doctorUid,
                "REJECTED"
        );
    }

    /**
     * Common verification-status update.
     */
    private boolean updateVerificationStatus(
            String doctorUid,
            String status) {

        validateUid(doctorUid);

        try {

            doctorDAO.updateVerificationStatus(
                    doctorUid,
                    status
            );

            return true;

        } catch (RuntimeException e) {

            System.err.println(
                    "Failed to update doctor verification status: "
                            + e.getMessage()
            );

            e.printStackTrace();

            return false;
        }
    }

    /**
     * Get current verification status.
     */
    public String getVerificationStatus(
            String doctorUid) {

        validateUid(doctorUid);

        try {

            return doctorDAO.getVerificationStatus(
                    doctorUid
            );

        } catch (RuntimeException e) {

            System.err.println(
                    "Failed to get doctor verification status: "
                            + e.getMessage()
            );

            return null;
        }
    }

    /**
     * Update complete doctor profile.
     */
    public boolean updateDoctorProfile(
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

            doctorDAO.updateDoctorProfile(
                    doctorProfile
            );

            return true;

        } catch (RuntimeException e) {

            System.err.println(
                    "Failed to update doctor profile: "
                            + e.getMessage()
            );

            e.printStackTrace();

            return false;
        }
    }

    /**
     * Delete doctor profile and credential record.
     */
    public boolean deleteDoctor(
            String doctorUid) {

        validateUid(doctorUid);

        try {

            doctorDAO.deleteDoctorProfile(
                    doctorUid
            );

            return true;

        } catch (RuntimeException e) {

            System.err.println(
                    "Failed to delete doctor: "
                            + e.getMessage()
            );

            e.printStackTrace();

            return false;
        }
    }

    /**
     * Validate Firebase UID.
     */
    private void validateUid(
            String doctorUid) {

        if (
                doctorUid == null ||
                doctorUid.trim().isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Doctor UID is required."
            );
        }
    }
}