package com.healthsphere.controller.admin;

import com.healthsphere.dao.admin.HospitalVerificationDAO;
import com.healthsphere.model.HospitalVerification;

import java.util.List;

public class HospitalVerificationController {

    private final HospitalVerificationDAO verificationDAO;

    public HospitalVerificationController() {

        this.verificationDAO =
                new HospitalVerificationDAO();
    }

    /**
     * Create a new hospital verification record.
     */
    public String createVerification(
            HospitalVerification verification) {

        try {

            validateVerification(verification);

            return verificationDAO
                    .createVerification(verification);

        } catch (Exception e) {

            System.err.println(
                    "Failed to create hospital verification: "
                            + e.getMessage()
            );

            return null;
        }
    }

    /**
     * Get verification by verification ID.
     */
    public HospitalVerification getVerificationById(
            String verificationId) {

        try {

            if (verificationId == null
                    || verificationId.isBlank()) {

                return null;
            }

            return verificationDAO
                    .getVerificationById(
                            verificationId
                    );

        } catch (Exception e) {

            System.err.println(
                    "Failed to get hospital verification: "
                            + e.getMessage()
            );

            return null;
        }
    }

    /**
     * Get verification by hospital ID.
     */
    public HospitalVerification getVerificationByHospitalId(
            String hospitalId) {

        try {

            if (hospitalId == null
                    || hospitalId.isBlank()) {

                return null;
            }

            return verificationDAO
                    .getVerificationByHospitalId(
                            hospitalId
                    );

        } catch (Exception e) {

            System.err.println(
                    "Failed to get hospital verification: "
                            + e.getMessage()
            );

            return null;
        }
    }

    /**
     * Get all hospital verification records.
     */
    public List<HospitalVerification> getAllVerifications() {

        try {

            return verificationDAO
                    .getAllVerifications();

        } catch (Exception e) {

            System.err.println(
                    "Failed to load hospital verifications: "
                            + e.getMessage()
            );

            return List.of();
        }
    }

    /**
     * Get verification records by status.
     *
     * Supported examples:
     *
     * PENDING
     * APPROVED
     * REJECTED
     */
    public List<HospitalVerification>
    getVerificationsByStatus(String status) {

        try {

            if (status == null
                    || status.isBlank()) {

                return List.of();
            }

            return verificationDAO
                    .getVerificationsByStatus(status);

        } catch (Exception e) {

            System.err.println(
                    "Failed to load verifications by status: "
                            + e.getMessage()
            );

            return List.of();
        }
    }

    /**
     * Update complete verification information.
     */
    public boolean updateVerification(
            HospitalVerification verification) {

        try {

            validateVerification(verification);

            return verificationDAO
                    .updateVerification(
                            verification
                    );

        } catch (Exception e) {

            System.err.println(
                    "Failed to update hospital verification: "
                            + e.getMessage()
            );

            return false;
        }
    }

    /**
     * Approve hospital verification.
     */
    public boolean approveVerification(
            String verificationId,
            String adminUid) {

        try {

            if (verificationId == null
                    || verificationId.isBlank()) {

                return false;
            }

            if (adminUid == null
                    || adminUid.isBlank()) {

                return false;
            }

            return verificationDAO
                    .approveVerification(
                            verificationId,
                            adminUid
                    );

        } catch (Exception e) {

            System.err.println(
                    "Failed to approve hospital verification: "
                            + e.getMessage()
            );

            return false;
        }
    }

    /**
     * Reject hospital verification.
     */
    public boolean rejectVerification(
            String verificationId,
            String adminUid,
            String rejectionReason) {

        try {

            if (verificationId == null
                    || verificationId.isBlank()) {

                return false;
            }

            if (adminUid == null
                    || adminUid.isBlank()) {

                return false;
            }

            if (rejectionReason == null
                    || rejectionReason.isBlank()) {

                return false;
            }

            return verificationDAO
                    .rejectVerification(
                            verificationId,
                            adminUid,
                            rejectionReason
                    );

        } catch (Exception e) {

            System.err.println(
                    "Failed to reject hospital verification: "
                            + e.getMessage()
            );

            return false;
        }
    }

    /**
     * Delete verification record.
     */
    public boolean deleteVerification(
            String verificationId) {

        try {

            if (verificationId == null
                    || verificationId.isBlank()) {

                return false;
            }

            return verificationDAO
                    .deleteVerification(
                            verificationId
                    );

        } catch (Exception e) {

            System.err.println(
                    "Failed to delete hospital verification: "
                            + e.getMessage()
            );

            return false;
        }
    }

    /**
     * Basic validation before DAO operation.
     */
    private void validateVerification(
            HospitalVerification verification) {

        if (verification == null) {

            throw new IllegalArgumentException(
                    "Hospital verification cannot be null."
            );
        }

        if (verification.getHospitalId() == null
                || verification.getHospitalId().isBlank()) {

            throw new IllegalArgumentException(
                    "Hospital ID is required."
            );
        }

        if (verification.getHospitalName() == null
                || verification.getHospitalName().isBlank()) {

            throw new IllegalArgumentException(
                    "Hospital name is required."
            );
        }

        if (verification.getNabhLicenseNumber() == null
                || verification
                .getNabhLicenseNumber()
                .isBlank()) {

            throw new IllegalArgumentException(
                    "NABH license number is required."
            );
        }

        if (verification.getVerificationStatus() == null
                || verification
                .getVerificationStatus()
                .isBlank()) {

            throw new IllegalArgumentException(
                    "Verification status is required."
            );
        }
    }
}