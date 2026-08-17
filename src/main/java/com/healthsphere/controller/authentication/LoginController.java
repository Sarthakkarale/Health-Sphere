package com.healthsphere.controller.authentication;

import com.healthsphere.dao.authentication.AuthenticationDAO;
import com.healthsphere.dao.authentication.UserDAO;
import com.healthsphere.exceptions.AuthenticationException;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.AccountStatus;
import com.healthsphere.model.AuthenticationResponse;
import com.healthsphere.model.LoginDestination;
import com.healthsphere.model.Role;
import com.healthsphere.model.UserProfile;
import com.healthsphere.util.SessionManager;

public class LoginController {

    private final AuthenticationDAO authenticationDAO;
    private final UserDAO userDAO;

    public LoginController() {

        this.authenticationDAO =
                new AuthenticationDAO();

        this.userDAO =
                new UserDAO();
    }

    // ============================================================
    // LOGIN
    // ============================================================

    public UserProfile login(
            String email,
            String password) {

        try {

            // ====================================================
            // STEP 1 — FIREBASE AUTHENTICATION
            // ====================================================

            AuthenticationResponse response =
                    authenticationDAO.login(
                            email,
                            password
                    );

            // ====================================================
            // STEP 2 — GET APPLICATION USER PROFILE
            // ====================================================

            UserProfile userProfile =
                    userDAO.getUserProfile(
                            response.getUid()
                    );

            // ====================================================
            // STEP 3 — CREATE SESSION
            // ====================================================

            SessionManager.createSession(
                    response
            );

            // Store the already-fetched profile
            // in the current session.
            SessionManager.getInstance()
                    .setCurrentUser(
                            userProfile
                    );

            // ====================================================
            // STEP 4 — RETURN USER PROFILE
            // ====================================================

            return userProfile;

        } catch (AuthenticationException e) {

            throw e;

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Login failed.",
                    e
            );
        }
    }

    // ============================================================
    // ROLE + STATUS DECISION
    // ============================================================

    public LoginDestination determineDestination(
            UserProfile profile) {

        if (profile == null) {

            return LoginDestination.LOGIN;
        }

        String role =
                profile.getRole();

        String status =
                profile.getStatus();

        // ========================================================
        // PATIENT
        // ========================================================

        if (Role.PATIENT.name().equals(role)
                && AccountStatus.ACTIVE.name().equals(status)) {

            return LoginDestination.PATIENT_DASHBOARD;
        }

        // ========================================================
        // DOCTOR
        // ========================================================

        if (Role.DOCTOR.name().equals(role)) {

            if (AccountStatus.ACTIVE.name().equals(status)) {

                return LoginDestination.DOCTOR_DASHBOARD;
            }

            if (AccountStatus.PENDING.name().equals(status)) {

                return LoginDestination.DOCTOR_PENDING;
            }
        }

        // ========================================================
        // HOSPITAL
        // ========================================================

        if (Role.HOSPITAL.name().equals(role)) {

            if (AccountStatus.ACTIVE.name().equals(status)) {

                return LoginDestination.HOSPITAL_DASHBOARD;
            }

            if (AccountStatus.PENDING.name().equals(status)) {

                return LoginDestination.HOSPITAL_PENDING;
            }
        }

        // ========================================================
        // ADMIN
        // ========================================================

        if (Role.ADMIN.name().equals(role)
                && AccountStatus.ACTIVE.name().equals(status)) {

            return LoginDestination.ADMIN_DASHBOARD;
        }

        // ========================================================
        // UNKNOWN / INVALID ROLE OR STATUS
        // ========================================================

        return LoginDestination.LOGIN;
    }
}