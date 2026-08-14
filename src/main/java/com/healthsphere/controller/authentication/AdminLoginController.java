package com.healthsphere.controller.authentication;

import com.healthsphere.dao.authentication.AuthenticationDAO;
import com.healthsphere.dao.authentication.UserDAO;
import com.healthsphere.exceptions.AuthenticationException;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.AccountStatus;
import com.healthsphere.model.AuthenticationResponse;
import com.healthsphere.model.Role;
import com.healthsphere.model.UserProfile;
import com.healthsphere.util.SessionManager;

public class AdminLoginController {

    private final AuthenticationDAO authenticationDAO;
    private final UserDAO userDAO;

    public AdminLoginController() {

        this.authenticationDAO =
                new AuthenticationDAO();

        this.userDAO =
                new UserDAO();
    }

    public UserProfile login(
            String email,
            String password) {

        try {

            // ====================================================
            // STEP 1 — Firebase Authentication
            // ====================================================

            AuthenticationResponse response =
                    authenticationDAO.login(
                            email,
                            password
                    );

            // ====================================================
            // STEP 2 — Get Firestore Profile
            // ====================================================

            UserProfile userProfile =
                    userDAO.getUserProfile(
                            response.getUid()
                    );

            // ====================================================
            // STEP 3 — Verify ADMIN role
            // ====================================================

            if (!Role.ADMIN.name().equals(
                    userProfile.getRole())) {

                throw new AuthenticationException(
                        "This account does not have administrator access."
                );
            }

            // ====================================================
            // STEP 4 — Verify ACTIVE status
            // ====================================================

            if (!AccountStatus.ACTIVE.name().equals(
                    userProfile.getStatus())) {

                throw new AuthenticationException(
                        "Administrator account is not active."
                );
            }

            // ====================================================
            // STEP 5 — Create Session
            // ====================================================

            SessionManager.createSession(
                    response
            );

            // ====================================================
            // STEP 6 — Return Profile
            // ====================================================

            return userProfile;

        } catch (AuthenticationException e) {

            throw e;

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Admin login failed.",
                    e
            );
        }
    }
}