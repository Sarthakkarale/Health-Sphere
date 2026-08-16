package com.healthsphere.util;

import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.controller.authentication.DoctorRegistrationController;
import com.healthsphere.exceptions.AuthenticationException;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.DoctorProfile;

public class DoctorRegistrationControllerTest {

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

            DoctorRegistrationController controller =
                    new DoctorRegistrationController();

            // ====================================================
            // REGISTER DOCTOR
            // ====================================================

            DoctorProfile profile =
                    controller.register(

                            // First Name
                            "John",

                            // Last Name
                            "Doe",

                            // Email
                            "doctortest@example.com",

                            // Password
                            "Test@12345",

                            // Phone
                            "9876543210",

                            // Registration Number
                            "MCI-2026-12345",

                            // Specialization
                            "Cardiology",

                            // Experience
                            "5 Years",

                            // Hospital Affiliation
                            "HealthSphere General Hospital",

                            // Medical Council
                            "Maharashtra Medical Council"
                    );

            // ====================================================
            // DISPLAY RESULT
            // ====================================================

            System.out.println(
                    "Doctor registration successful!"
            );

            System.out.println(
                    "--------------------------------"
            );

            System.out.println(
                    "UID: "
                            + profile.getUid()
            );

            System.out.println(
                    "First Name: "
                            + profile.getFirstName()
            );

            System.out.println(
                    "Last Name: "
                            + profile.getLastName()
            );

            System.out.println(
                    "Email: "
                            + profile.getEmail()
            );

            System.out.println(
                    "Phone: "
                            + profile.getPhone()
            );

            System.out.println(
                    "Registration Number: "
                            + profile.getRegistrationNumber()
            );

            System.out.println(
                    "Specialization: "
                            + profile.getSpecialization()
            );

            System.out.println(
                    "Experience: "
                            + profile.getExperience()
            );

            System.out.println(
                    "Hospital Affiliation: "
                            + profile.getHospitalAffiliation()
            );

            System.out.println(
                    "Medical Council: "
                            + profile.getMedicalCouncil()
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