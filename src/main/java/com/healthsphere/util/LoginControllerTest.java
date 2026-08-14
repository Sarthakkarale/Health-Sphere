package com.healthsphere.util;

import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.controller.authentication.LoginController;
import com.healthsphere.exceptions.AuthenticationException;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.LoginDestination;
import com.healthsphere.model.UserProfile;

public class LoginControllerTest {

    public static void main(String[] args) {

        try {

            // ====================================================
            // INITIALIZE FIREBASE
            // ====================================================

            FirebaseConfig.initialize();

            System.out.println(
                    "Firebase initialized successfully."
            );

            // ====================================================
            // CREATE LOGIN CONTROLLER
            // ====================================================

            LoginController loginController =
                    new LoginController();

            // ====================================================
            // LOGIN
            // ====================================================

            UserProfile profile =
                    loginController.login(
                            "test2@example.com",
                            "Test@12345"
                    );
                
                LoginDestination destination =
                loginController.determineDestination(
                        profile
                );

                System.out.println(
                        "Destination: " + destination
                );

            System.out.println(
                    "Login flow successful!"
            );

            System.out.println(
                    "--------------------------------"
            );

            System.out.println(
                    "UID: "
                            + profile.getUid()
            );

            System.out.println(
                    "Email: "
                            + profile.getEmail()
            );

            System.out.println(
                    "Role: "
                            + profile.getRole()
            );

            System.out.println(
                    "Status: "
                            + profile.getStatus()
            );

            System.out.println(
                    "Session active: "
                            + SessionManager.isLoggedIn()
            );

            System.out.println(
                    "--------------------------------"
            );

        } catch (AuthenticationException e) {

            System.out.println(
                    "Authentication failed: "
                            + e.getMessage()
            );

        } catch (DatabaseException e) {

            System.out.println(
                    "Database failed: "
                            + e.getMessage()
            );

        } catch (Exception e) {

            System.out.println(
                    "Unexpected error:"
            );

            e.printStackTrace();
        }
    }
}