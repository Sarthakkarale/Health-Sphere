package com.healthsphere.util;

import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.dao.authentication.AuthenticationDAO;
import com.healthsphere.dao.authentication.UserDAO;
import com.healthsphere.exceptions.AuthenticationException;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.AuthenticationResponse;
import com.healthsphere.model.UserProfile;

public class UserDAOTest {

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
            // CREATE AUTHENTICATION DAO
            // ====================================================

            AuthenticationDAO authenticationDAO =
                    new AuthenticationDAO();

            // ====================================================
            // REGISTER USER
            // ====================================================

            AuthenticationResponse response =
                    authenticationDAO.register(
                            "sarthakkarale7@gmail.com",
                            "Test@12345"
                    );

            System.out.println(
                    "Registration successful!"
            );

            System.out.println(
                    "UID: " + response.getUid()
            );

            System.out.println(
                    "Email: " + response.getEmail()
            );

            // ====================================================
            // CREATE FIRESTORE USER PROFILE
            // ====================================================

            UserProfile userProfile =
                    new UserProfile(
                            response.getUid(),
                            response.getEmail(),
                            "PATIENT",
                            "ACTIVE"
                    );

            UserDAO userDAO =
                    new UserDAO();

            userDAO.createUserProfile(
                    userProfile
            );

            System.out.println(
                    "User profile created successfully."
            );

            // ====================================================
            // GET USER PROFILE FROM FIRESTORE
            // ====================================================

            UserProfile profile =
                    userDAO.getUserProfile(
                            response.getUid()
                    );

            System.out.println(
                    "User profile retrieved successfully."
            );

            System.out.println(
                    "--------------------------------"
            );

            System.out.println(
                    "UID: " + profile.getUid()
            );

            System.out.println(
                    "Email: " + profile.getEmail()
            );

            System.out.println(
                    "Role: " + profile.getRole()
            );

            System.out.println(
                    "Status: " + profile.getStatus()
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
                    "Database operation failed: "
                            + e.getMessage()
            );

        } catch (Exception e) {

            System.out.println(
                    "Unexpected error occurred."
            );

            e.printStackTrace();
        }
    }
}