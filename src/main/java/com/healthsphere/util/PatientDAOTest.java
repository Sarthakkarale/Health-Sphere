package com.healthsphere.util;

import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.dao.authentication.PatientDAO;
import com.healthsphere.model.PatientProfile;

public class PatientDAOTest {

    public static void main(String[] args) {

        try {

            // Initialize Firebase
            FirebaseConfig.initialize();

            // Create patient object
            PatientProfile patient =
                    new PatientProfile(
                            "msYtJ6VADZRELeFgWIdnD7G3jSs1",
                            "Sarthak",
                            "Karale",
                            "sarthakkarale7@gmail.com",
                            "9999999999",
                            "2005-01-01",
                            "Male",
                            "B+",
                            "Parent - 9999999999",
                            "Pune, Maharashtra"
                    );

            // Create DAO
            PatientDAO patientDAO =
                    new PatientDAO();

            // Save
            patientDAO.createPatientProfile(
                    patient
            );

            System.out.println(
                    "Patient DAO test successful!"
            );

            // Read back
            PatientProfile profile =
                    patientDAO.getPatientProfile(
                            "msYtJ6VADZRELeFgWIdnD7G3jSs1"
                    );

            // Display
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
                    "DOB: "
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

        } catch (Exception e) {

            System.out.println(
                    "Patient DAO test failed:"
            );

            e.printStackTrace();
        }
    }
}