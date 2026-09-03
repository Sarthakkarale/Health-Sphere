package com.healthsphere.util;

import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.controller.authentication.HospitalRegistrationController;
import com.healthsphere.exceptions.AuthenticationException;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.HospitalProfile;

public class HospitalRegistrationControllerTest {

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
            // CREATE CONTROLLER
            // ====================================================

            HospitalRegistrationController controller =
                    new HospitalRegistrationController();

            // ====================================================
            // REGISTER HOSPITAL
            // ====================================================

            HospitalProfile profile =
                    controller.register(

                            // Email
                            "hospitaltest@example.com",

                            // Password
                            "Test@12345",

                            // Hospital Name
                            "HealthSphere General Hospital",

                            // Registration Number
                            "HOSP-2026-12345",

                            // Hospital Type
                            "Multi-Specialty",

                            // Number of Beds
                            "250",

                            // Contact
                            "9876543210",

                            // Address
                            "Pune, Maharashtra, India"
                    );

            // ====================================================
            // DISPLAY RESULT
            // ====================================================

            System.out.println(
                    "Hospital registration successful!"
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
                    "Hospital Name: "
                            + profile.getHospitalName()
            );

            System.out.println(
                    "Registration Number: "
                            + profile.getRegistrationNumber()
            );

            System.out.println(
                    "Hospital Type: "
                            + profile.getHospitalType()
            );

            System.out.println(
                    "Beds: "
                            + profile.getBeds()
            );

            System.out.println(
                    "Contact: "
                            + profile.getContact()
            );

            System.out.println(
                    "Address: "
                            + profile.getAddress()
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