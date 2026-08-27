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

            AuthenticationResponse response =
                    authenticationDAO.login(
                            email,
                            password
                    );

            SessionManager.createSession(
                    response
            );

            UserProfile userProfile =
                    userDAO.getUserProfile(
                            response.getUid()
                    );

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

        String role = profile.getRole();
        String status = profile.getStatus();

        // PATIENT
        if (role.equals(Role.PATIENT.name())
                && status.equals(AccountStatus.ACTIVE.name())) {

            return LoginDestination.PATIENT_DASHBOARD;
        }

        // DOCTOR
        if (role.equals(Role.DOCTOR.name())) {

            if (status.equals(AccountStatus.ACTIVE.name())) {

                return LoginDestination.DOCTOR_DASHBOARD;
            }

            if (status.equals(AccountStatus.PENDING.name())) {

                return LoginDestination.DOCTOR_PENDING;
            }
        }

        // HOSPITAL
        if (role.equals(Role.HOSPITAL.name())) {

            if (status.equals(AccountStatus.ACTIVE.name())) {

                return LoginDestination.HOSPITAL_DASHBOARD;
            }

            if (status.equals(AccountStatus.PENDING.name())) {

                return LoginDestination.HOSPITAL_PENDING;
            }
        }

        // ADMIN
        if (role.equals(Role.ADMIN.name())
                && status.equals(AccountStatus.ACTIVE.name())) {

            return LoginDestination.ADMIN_DASHBOARD;
        }

        return LoginDestination.LOGIN;
    }
}