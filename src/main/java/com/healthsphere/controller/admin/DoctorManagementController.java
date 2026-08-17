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

        return doctorDAO.getAllDoctorProfiles();
    }

    /**
     * Get a single doctor by Firebase UID.
     */
    public DoctorProfile getDoctor(
            String doctorUid) {

        validateUid(doctorUid);

        return doctorDAO.getDoctorProfile(
                doctorUid
        );
    }

    /**
     * Approve / verify a doctor.
     */
    public boolean verifyDoctor(
            String doctorUid) {

        return updateVerificationStatus(
                doctorUid,
                "VERIFIED"
        );
    }

    /**
     * Put doctor verification back into pending state.
     */
    public boolean setDoctorPending(
            String doctorUid) {

        return updateVerificationStatus(
                doctorUid,
                "PENDING"
        );
    }

    /**
     * Reject a doctor's verification.
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

        return doctorDAO.getVerificationStatus(
                doctorUid
        );
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

            e.printStackTrace();

            return false;
        }
    }

    /**
     * Delete doctor profile.
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