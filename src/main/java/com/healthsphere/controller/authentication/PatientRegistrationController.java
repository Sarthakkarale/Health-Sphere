package com.healthsphere.controller.authentication;

import com.healthsphere.dao.authentication.AuthenticationDAO;
import com.healthsphere.dao.authentication.UserDAO;
import com.healthsphere.dao.authentication.PatientDAO;

import com.healthsphere.exceptions.AuthenticationException;
import com.healthsphere.exceptions.DatabaseException;

import com.healthsphere.model.AuthenticationResponse;
import com.healthsphere.model.PatientProfile;
import com.healthsphere.model.UserProfile;

public class PatientRegistrationController {

    private final AuthenticationDAO authenticationDAO;
    private final UserDAO userDAO;
    private final PatientDAO patientDAO;

    public PatientRegistrationController() {

        this.authenticationDAO =
                new AuthenticationDAO();

        this.userDAO =
                new UserDAO();

        this.patientDAO =
                new PatientDAO();
    }

    // ============================================================
    // REGISTER PATIENT
    // ============================================================

    public PatientProfile register(
            String firstName,
            String lastName,
            String email,
            String password,
            String phone,
            String dateOfBirth,
            String gender,
            String bloodGroup,
            String emergencyContact,
            String address) {

        try {

            // ====================================================
            // STEP 1 — CREATE FIREBASE AUTHENTICATION ACCOUNT
            // ====================================================

            AuthenticationResponse response =
                    authenticationDAO.register(
                            email,
                            password
                    );

            String uid = response.getUid();

            // ====================================================
            // STEP 2 — CREATE COMMON USER PROFILE
            // ====================================================

            UserProfile userProfile =
                    new UserProfile(
                            uid,
                            response.getEmail(),
                            "PATIENT",
                            "ACTIVE"
                    );

            userDAO.createUserProfile(
                    userProfile
            );

            // ====================================================
            // STEP 3 — CREATE PATIENT PROFILE
            // ====================================================

            PatientProfile patientProfile =
                    new PatientProfile(
                            uid,
                            firstName,
                            lastName,
                            response.getEmail(),
                            phone,
                            dateOfBirth,
                            gender,
                            bloodGroup,
                            emergencyContact,
                            address
                    );

            patientDAO.createPatientProfile(
                    patientProfile
            );

            // ====================================================
            // STEP 4 — RETURN CREATED PROFILE
            // ====================================================

            return patientProfile;

        } catch (AuthenticationException e) {

            throw e;

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Patient registration failed.",
                    e
            );
        }
    }
}