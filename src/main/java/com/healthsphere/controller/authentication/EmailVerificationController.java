package com.healthsphere.controller.authentication;

import com.healthsphere.dao.authentication.AuthenticationDAO;

public class EmailVerificationController {

    private final AuthenticationDAO authenticationDAO;

    public EmailVerificationController(
            AuthenticationDAO authenticationDAO) {

        this.authenticationDAO = authenticationDAO;
    }

    public void sendVerificationEmail() {
        // Firebase implementation later.
    }

    public boolean isEmailVerified() {
        // Firebase implementation later.
        return false;
    }
}