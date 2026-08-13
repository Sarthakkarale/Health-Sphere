package com.healthsphere.util;

import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.controller.authentication.PatientRegistrationController;
import com.healthsphere.exceptions.AuthenticationException;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.PatientProfile;

public class PatientRegistrationControllerTest {

    public static void main(String[] args) {

        try {

            // ================================================
            // INITIALIZE FIREBASE
            // ================================================

            FirebaseConfig.initialize();

            System.out.println(
                    "Firebase initialized successfully."
            );

            // ================================================
            // CREATE CONTROLLER
            // ================================================

            PatientRegistrationController controller =
                    new PatientRegistrationController();

            // ================================================
            // REGISTER PATIENT
            // ================================================

            PatientProfile profile =
                    controller.register(
                            "Sarthak",
                            "Karale",
                            "patienttest@example.com",
                            "Test@12345",
                            "9999999999",
                            "2005-01-01",
                            "Male",
                            "B+",
                            "Parent - 9999999999",
                            "Pune, Maharashtra"
                    );

            // ================================================
            // DISPLAY RESULT
            // ================================================

            System.out.println(
                    "Patient registration successful!"
            );

            System.out.println(
                    "--------------------------------"
            );

            System.out.println(
                    "UID: "
                            + profile.getUid()
            );

            System.out.println(
                    "Name: "
                            + profile.getFirstName()
                            + " "
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
                    "Date of Birth: "
                            + profile.getDateOfBirth()
            );

            System.out.println(
                    "Gender: "
                            + profile.getGender()
            );

            System.out.println(
                    "Blood Group: "
                            + profile.getBloodGroup()
            );

            System.out.println(
                    "Emergency Contact: "
                            + profile.getEmergencyContact()
            );

            System.out.println(
                    "Address: "
                            + profile.getAddress()
            );

            System.out.println(
                    "--------------------------------"
            );

            System.out.println(
                    "Role: PATIENT"
            );

            System.out.println(
                    "Status: ACTIVE"
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