package com.healthsphere.util;

import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.controller.authentication.HospitalRegistrationController;
import com.healthsphere.exceptions.AuthenticationException;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.UserProfile;

public class HospitalRegistrationControllerTest {

    public static void main(String[] args) {

        try {
            FirebaseConfig.initialize();

            System.out.println("Firebase initialized successfully.");

            HospitalRegistrationController controller = new HospitalRegistrationController();

            UserProfile profile = controller.register(
                    "hospitaltest@example.com",
                    "Test@12345"
            );

            System.out.println("Hospital registration successful!");
            System.out.println("--------------------------------");
            System.out.println("UID: " + profile.getUid());
            System.out.println("Email: " + profile.getEmail());
            System.out.println("Role: " + profile.getRole());
            System.out.println("Status: " + profile.getStatus());
            System.out.println("--------------------------------");

        } catch (AuthenticationException e) {
            System.out.println("Authentication failed: " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("Database failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error:");
            e.printStackTrace();
        }
    }
}