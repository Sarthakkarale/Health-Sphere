package com.healthsphere.controller.authentication;

import com.healthsphere.dao.authentication.AuthenticationDAO;

public class PasswordResetController {

    private final AuthenticationDAO authenticationDAO;

    public PasswordResetController(
            AuthenticationDAO authenticationDAO) {

        this.authenticationDAO = authenticationDAO;
    }

    public void sendResetCode(String email) {
        // Firebase implementation later.
    }

    public void resetPassword(
            String verificationCode,
            String newPassword) {

        // Firebase implementation later.
    }
}