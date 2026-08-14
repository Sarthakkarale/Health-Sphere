package com.healthsphere.controller.authentication;

import com.healthsphere.dao.authentication.AuthenticationDAO;
import com.healthsphere.exceptions.AuthenticationException;

public class ForgotPasswordController {

    private final AuthenticationDAO authenticationDAO;

    public ForgotPasswordController() {

        this.authenticationDAO =
                new AuthenticationDAO();
    }

    public void sendPasswordResetEmail(
            String email) {

        try {

            authenticationDAO.sendPasswordResetEmail(
                    email
            );

        } catch (AuthenticationException e) {

            throw e;

        } catch (Exception e) {

            throw new AuthenticationException(
                    "Unable to process password reset request.",
                    e
            );
        }
    }
}