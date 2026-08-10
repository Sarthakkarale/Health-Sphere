package com.healthsphere.controller.authentication;

import com.healthsphere.dao.authentication.AuthenticationDAO;

public class LoginController {

    private final AuthenticationDAO authenticationDAO;

    public LoginController(AuthenticationDAO authenticationDAO) {
        this.authenticationDAO = authenticationDAO;
    }

    public void login(String email, String password) {
        // Validation will be added later.

        // Firebase authentication will be called through
        // AuthenticationDAO later.
    }
}