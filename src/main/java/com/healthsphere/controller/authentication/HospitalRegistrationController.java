package com.healthsphere.controller.authentication;

import com.healthsphere.dao.authentication.AuthenticationDAO;
import com.healthsphere.dao.authentication.UserDAO;
import com.healthsphere.dao.authentication.HospitalDAO;
import com.healthsphere.exceptions.AuthenticationException;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.AuthenticationResponse;
import com.healthsphere.model.HospitalProfile;
import com.healthsphere.model.UserProfile;

public class HospitalRegistrationController {

    private final AuthenticationDAO authenticationDAO;
    private final UserDAO userDAO;
    private final HospitalDAO hospitalDAO;

    public HospitalRegistrationController() {

        this.authenticationDAO =
                new AuthenticationDAO();

        this.userDAO =
                new UserDAO();

        this.hospitalDAO =
                new HospitalDAO();
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
            // STEP 4 — RETURN PROFILE
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