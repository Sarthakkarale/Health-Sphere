package com.healthsphere.util;

import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.controller.authentication.DoctorRegistrationController;
import com.healthsphere.exceptions.AuthenticationException;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.DoctorProfile;

public class DoctorRegistrationControllerTest {

    public static void main(String[] args) {

        try {
            FirebaseConfig.initialize();
            System.out.println("Firebase initialized successfully.");

            DoctorRegistrationController controller = new DoctorRegistrationController();

            DoctorProfile profile = controller.register(
                    "Doctor",
                    "Test",
                    "doctortest@example.com",
                    "Test@12345",
                    "1234567890",
                    "REG12345",
                    "Cardiology",
                    "5 years",
                    "General Hospital",
                    "Medical Council"
            );

            System.out.println("Doctor registration successful!");
            System.out.println("--------------------------------");
            System.out.println("UID: " + profile.getUid());
            System.out.println("Email: " + profile.getEmail());
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