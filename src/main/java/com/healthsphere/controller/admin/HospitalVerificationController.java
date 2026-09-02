package com.healthsphere.controller.admin;

import com.healthsphere.dao.admin.HospitalVerificationDAO;
import com.healthsphere.model.HospitalVerification;

import java.time.Instant;
import java.util.List;

public class HospitalVerificationController {

    private final HospitalVerificationDAO verificationDAO;

    public HospitalVerificationController() {
        this.verificationDAO = new HospitalVerificationDAO();
    }

    public String createVerification(HospitalVerification verification) {
        try {
            validateVerification(verification);
            return verificationDAO.createVerification(verification);
        } catch (Exception e) {
            System.err.println("Failed to create hospital verification: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a missing verification record as PENDING.
     * The hospital UID is used as the verification document ID.
     * This signature matches HospitalManagementView.
     */
    public boolean setPendingVerification(String hospitalId, String adminUid) {
        try {
            if (hospitalId == null || hospitalId.isBlank()) {
                throw new IllegalArgumentException("Hospital ID cannot be empty.");
            }

            if (adminUid == null || adminUid.isBlank()) {
                throw new IllegalArgumentException("Admin UID cannot be empty.");
            }

            HospitalVerification verification =
                    verificationDAO.getVerificationById(hospitalId);

            Instant now = Instant.now();

            if (verification == null) {
                verification = new HospitalVerification();
                verification.setVerificationId(hospitalId);
                verification.setHospitalId(hospitalId);
                verification.setHospitalName("Hospital " + hospitalId);
                verification.setNabhLicenseNumber("N/A");
                verification.setAiOcrMatchScore(0.0);
                verification.setAiOcrResult("PENDING");
                verification.setDocumentStatus("PENDING");
                verification.setCreatedAt(now);
            }

            verification.setVerificationId(hospitalId);
            verification.setHospitalId(hospitalId);
            verification.setVerificationStatus("PENDING");
            verification.setDocumentStatus("PENDING");
            verification.setAiOcrResult("PENDING");
            verification.setRejectionReason(null);
            verification.setVerifiedBy(null);
            verification.setUpdatedAt(now);

            if (verification.getCreatedAt() == null) {
                verification.setCreatedAt(now);
            }

            String result = verificationDAO.createVerification(verification);

            if (result != null && !result.isBlank()) {
                System.out.println("Hospital verification set to PENDING: " + hospitalId);
                return true;
            }

            return false;

        } catch (Exception e) {
            System.err.println("Failed to set hospital verification to PENDING: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public HospitalVerification getVerificationById(String verificationId) {
        try {
            if (verificationId == null || verificationId.isBlank()) return null;
            return verificationDAO.getVerificationById(verificationId);
        } catch (Exception e) {
            System.err.println("Failed to get hospital verification: " + e.getMessage());
            return null;
        }
    }

    public HospitalVerification getVerificationByHospitalId(String hospitalId) {
        try {
            if (hospitalId == null || hospitalId.isBlank()) return null;
            return verificationDAO.getVerificationByHospitalId(hospitalId);
        } catch (Exception e) {
            System.err.println("Failed to get hospital verification: " + e.getMessage());
            return null;
        }
    }

    public List<HospitalVerification> getAllVerifications() {
        try {
            return verificationDAO.getAllVerifications();
        } catch (Exception e) {
            System.err.println("Failed to load hospital verifications: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public List<HospitalVerification> getVerificationsByStatus(String status) {
        try {
            if (status == null || status.isBlank()) return List.of();
            return verificationDAO.getVerificationsByStatus(status);
        } catch (Exception e) {
            System.err.println("Failed to load verifications by status: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public boolean updateVerification(HospitalVerification verification) {
        try {
            validateVerification(verification);
            return verificationDAO.updateVerification(verification);
        } catch (Exception e) {
            System.err.println("Failed to update hospital verification: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean approveVerification(String verificationId, String adminUid) {
        try {
            if (verificationId == null || verificationId.isBlank()) {
                throw new IllegalArgumentException("Verification ID is required.");
            }
            if (adminUid == null || adminUid.isBlank()) {
                throw new IllegalArgumentException("Admin UID is required.");
            }

            /*
             * Existing hospitals may have no verification document.
             * Create a PENDING record first, then approve it.
             */
            HospitalVerification verification =
                    verificationDAO.getVerificationById(verificationId);

            if (verification == null) {
                boolean created = setPendingVerification(verificationId, adminUid);
                if (!created) return false;
            }

            return verificationDAO.approveVerification(verificationId, adminUid);

        } catch (Exception e) {
            System.err.println("Failed to approve hospital verification: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean rejectVerification(
            String verificationId,
            String adminUid,
            String rejectionReason) {
        try {
            if (verificationId == null || verificationId.isBlank()) {
                throw new IllegalArgumentException("Verification ID is required.");
            }
            if (adminUid == null || adminUid.isBlank()) {
                throw new IllegalArgumentException("Admin UID is required.");
            }
            if (rejectionReason == null || rejectionReason.isBlank()) {
                throw new IllegalArgumentException("Rejection reason cannot be empty.");
            }

            HospitalVerification verification =
                    verificationDAO.getVerificationById(verificationId);

            if (verification == null) {
                boolean created = setPendingVerification(verificationId, adminUid);
                if (!created) return false;
            }

            return verificationDAO.rejectVerification(
                    verificationId,
                    adminUid,
                    rejectionReason
            );

        } catch (Exception e) {
            System.err.println("Failed to reject hospital verification: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteVerification(String verificationId) {
        try {
            if (verificationId == null || verificationId.isBlank()) return false;
            return verificationDAO.deleteVerification(verificationId);
        } catch (Exception e) {
            System.err.println("Failed to delete hospital verification: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void validateVerification(HospitalVerification verification) {
        if (verification == null) {
            throw new IllegalArgumentException("Hospital verification cannot be null.");
        }
        if (verification.getHospitalId() == null || verification.getHospitalId().isBlank()) {
            throw new IllegalArgumentException("Hospital ID is required.");
        }
        if (verification.getHospitalName() == null || verification.getHospitalName().isBlank()) {
            throw new IllegalArgumentException("Hospital name is required.");
        }
        if (verification.getNabhLicenseNumber() == null || verification.getNabhLicenseNumber().isBlank()) {
            throw new IllegalArgumentException("NABH license number is required.");
        }
        if (verification.getVerificationStatus() == null || verification.getVerificationStatus().isBlank()) {
            throw new IllegalArgumentException("Verification status is required.");
        }
    }
}
