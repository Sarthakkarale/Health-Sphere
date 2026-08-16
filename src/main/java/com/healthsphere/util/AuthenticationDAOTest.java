package com.healthsphere.util;

import com.healthsphere.dao.authentication.AuthenticationDAO;
import com.healthsphere.model.AuthenticationResponse;

public class AuthenticationDAOTest {

    public static void main(String[] args) {

        AuthenticationDAO authenticationDAO =
                new AuthenticationDAO();

        try {

            AuthenticationResponse response =
                    authenticationDAO.login(
                            "test2@example.com",
                            "Test@12345"
                    );

            System.out.println(
                    "Login successful!"
            );

            System.out.println(
                    "UID: "
                    + response.getUid()
            );

            System.out.println(
                    "Email: "
                    + response.getEmail()
            );

            System.out.println(
                    "ID Token received: "
                    + (response.getIdToken() != null)
            );

            System.out.println(
                    "Refresh Token received: "
                    + (response.getRefreshToken() != null)
            );

            // Create session
            SessionManager.createSession(response);

            System.out.println(
                    "Session created: "
                    + SessionManager.isLoggedIn()
            );

            // Get current user
            AuthenticationResponse currentUser =
                    SessionManager.getCurrentUser();

            System.out.println(
                    "Current user UID: "
                    + currentUser.getUid()
            );

            System.out.println(
                    "Current user email: "
                    + currentUser.getEmail()
            );

            // Logout
            SessionManager.clearSession();

            System.out.println(
                    "Session after logout: "
                    + SessionManager.isLoggedIn()
            );

        } catch (Exception e) {

            System.out.println(
                    "Login failed:"
            );

            System.out.println(
                    e.getMessage()
            );
        }
    }
}