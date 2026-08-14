package com.healthsphere.controller.authentication;

import com.healthsphere.dao.authentication.AuthenticationDAO;
import com.healthsphere.dao.authentication.UserDAO;
import com.healthsphere.dao.authentication.DoctorDAO;

import com.healthsphere.exceptions.AuthenticationException;
import com.healthsphere.exceptions.DatabaseException;

import com.healthsphere.model.AuthenticationResponse;
import com.healthsphere.model.DoctorProfile;
import com.healthsphere.model.UserProfile;

public class DoctorRegistrationController {

    private final AuthenticationDAO authenticationDAO;
    private final UserDAO userDAO;
    private final DoctorDAO doctorDAO;

    public DoctorRegistrationController() {

        this.authenticationDAO =
                new AuthenticationDAO();

        this.userDAO =
                new UserDAO();

        this.doctorDAO =
                new DoctorDAO();
    }

    // ============================================================
    // REGISTER DOCTOR
    // ============================================================

    public DoctorProfile register(
            String firstName,
            String lastName,
            String email,
            String password,
            String phone,
            String registrationNumber,
            String specialization,
            String experience,
            String hospitalAffiliation,
            String medicalCouncil) {

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
                            "DOCTOR",
                            "PENDING"
                    );

            userDAO.createUserProfile(
                    userProfile
            );

            // ====================================================
            // STEP 3 — DOCTOR PROFILE
            // ====================================================

            DoctorProfile doctorProfile =
                    new DoctorProfile(
                            uid,
                            firstName,
                            lastName,
                            response.getEmail(),
                            phone,
                            registrationNumber,
                            specialization,
                            experience,
                            hospitalAffiliation,
                            medicalCouncil
                    );

            doctorDAO.createDoctorProfile(
                    doctorProfile
            );

            // ====================================================
            // STEP 4 — RETURN PROFILE
            // ====================================================

            return doctorProfile;

        } catch (AuthenticationException e) {

            throw e;

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Doctor registration failed.",
                    e
            );
        }
    }
}