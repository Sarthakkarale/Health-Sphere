package com.healthsphere.controller.authentication;

import com.healthsphere.dao.authentication.AuthenticationDAO;
import com.healthsphere.dao.authentication.UserDAO;
import com.healthsphere.exceptions.AuthenticationException;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.AccountStatus;
import com.healthsphere.model.AuthenticationResponse;
import com.healthsphere.model.Role;
import com.healthsphere.model.UserProfile;

public class DoctorRegistrationController {

    private final AuthenticationDAO authenticationDAO;
    private final UserDAO userDAO;

    public DoctorRegistrationController() {

        this.authenticationDAO =
                new AuthenticationDAO();

        this.userDAO =
                new UserDAO();
    }

    public UserProfile register(
            String email,
            String password) {

        try {

            // ====================================================
            // STEP 1 — Firebase Authentication
            // ====================================================

            AuthenticationResponse response =
                    authenticationDAO.register(
                            email,
                            password
                    );

            // ====================================================
            // STEP 2 — Create Doctor Profile
            // ====================================================

            UserProfile userProfile =
                    new UserProfile(
                            response.getUid(),
                            response.getEmail(),
                            Role.DOCTOR.name(),
                            AccountStatus.PENDING.name()
                    );

            // ====================================================
            // STEP 3 — Save Profile to Firestore
            // ====================================================

            userDAO.createUserProfile(
                    userProfile
            );

            // ====================================================
            // STEP 4 — Return Profile
            // ====================================================

            return userProfile;

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