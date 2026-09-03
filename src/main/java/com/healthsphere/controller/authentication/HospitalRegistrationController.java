package com.healthsphere.controller.authentication;

import com.healthsphere.dao.admin.HospitalVerificationDAO;
import com.healthsphere.dao.authentication.AuthenticationDAO;
import com.healthsphere.dao.authentication.HospitalDAO;
import com.healthsphere.dao.authentication.UserDAO;
import com.healthsphere.exceptions.AuthenticationException;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.AuthenticationResponse;
import com.healthsphere.model.HospitalProfile;
import com.healthsphere.model.HospitalVerification;
import com.healthsphere.model.UserProfile;

public class HospitalRegistrationController {

    private final AuthenticationDAO authenticationDAO;
    private final UserDAO userDAO;
    private final HospitalDAO hospitalDAO;
    private final HospitalVerificationDAO hospitalVerificationDAO;

    public HospitalRegistrationController() {

        this.authenticationDAO =
                new AuthenticationDAO();

        this.userDAO =
                new UserDAO();

        this.hospitalDAO =
                new HospitalDAO();

        this.hospitalVerificationDAO =
                new HospitalVerificationDAO();
    }

    // ============================================================
    // REGISTER HOSPITAL
    // ============================================================

    public HospitalProfile register(
            String email,
            String password,
            String hospitalName,
            String registrationNumber,
            String hospitalType,
            String beds,
            String contact,
            String address) {

        try {

            // ====================================================
            // STEP 1 — FIREBASE AUTHENTICATION
            // ====================================================

            AuthenticationResponse response =
                    authenticationDAO.register(
                            email,
                            password
                    );

            String uid = response.getUid();

            if (uid == null || uid.isBlank()) {
                throw new DatabaseException(
                        "Firebase returned an empty hospital UID."
                );
            }

            // ====================================================
            // STEP 2 — COMMON USER PROFILE
            // ====================================================

            UserProfile userProfile =
                    new UserProfile(
                            uid,
                            response.getEmail(),
                            "HOSPITAL",
                            "PENDING"
                    );

            userDAO.createUserProfile(
                    userProfile
            );

            // ====================================================
            // STEP 3 — HOSPITAL PROFILE
            // ====================================================

            HospitalProfile hospitalProfile =
                    new HospitalProfile(
                            uid,
                            response.getEmail(),
                            hospitalName,
                            registrationNumber,
                            hospitalType,
                            beds,
                            contact,
                            address
                    );

            hospitalDAO.createHospitalProfile(
                    hospitalProfile
            );

            // ====================================================
            // STEP 4 — CREATE VERIFICATION RECORD
            // ====================================================
            // IMPORTANT:
            // The verification document ID is the same Firebase UID.
            // This keeps the following records linked by one ID:
            //
            // users/{uid}
            // hospitals/{uid}
            // hospital_verifications/{uid}
            //
            // Without this record the Admin Verification screen has
            // nothing to approve/reject.

            HospitalVerification verification =
                    new HospitalVerification();

            verification.setVerificationId(uid);
            verification.setHospitalId(uid);
            verification.setHospitalName(hospitalName);
            verification.setNabhLicenseNumber(registrationNumber);
            verification.setAiOcrMatchScore(0.0);
            verification.setAiOcrResult("PENDING");
            verification.setVerificationStatus("PENDING");
            verification.setDocumentStatus("PENDING");
            verification.setRejectionReason(null);
            verification.setVerifiedBy(null);

            String verificationId =
                    hospitalVerificationDAO.createVerification(
                            verification
                    );

            if (verificationId == null || verificationId.isBlank()) {
                throw new DatabaseException(
                        "Hospital was registered, but the verification record could not be created."
                );
            }

            System.out.println(
                    "Hospital registration completed successfully. " +
                    "UID=" + uid +
                    ", Verification ID=" + verificationId
            );

            // ====================================================
            // STEP 5 — RETURN PROFILE
            // ====================================================

            return hospitalProfile;

        } catch (AuthenticationException e) {

            throw e;

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Hospital registration failed.",
                    e
            );
        }
    }
}
