package com.healthsphere.controller.authentication;

import com.healthsphere.dao.authentication.AuthenticationDAO;
import com.healthsphere.dao.authentication.DoctorDAO;
import com.healthsphere.dao.authentication.HospitalDAO;
import com.healthsphere.dao.authentication.PatientDAO;
import com.healthsphere.dao.authentication.UserDAO;

public class RegistrationController {

    private final AuthenticationDAO authenticationDAO;
    private final UserDAO userDAO;
    private final PatientDAO patientDAO;
    private final DoctorDAO doctorDAO;
    private final HospitalDAO hospitalDAO;

    public RegistrationController(
            AuthenticationDAO authenticationDAO,
            UserDAO userDAO,
            PatientDAO patientDAO,
            DoctorDAO doctorDAO,
            HospitalDAO hospitalDAO) {

        this.authenticationDAO = authenticationDAO;
        this.userDAO = userDAO;
        this.patientDAO = patientDAO;
        this.doctorDAO = doctorDAO;
        this.hospitalDAO = hospitalDAO;
    }

    public void registerPatient() {
        // Patient registration logic will be added later.
    }

    public void registerDoctor() {
        // Doctor registration logic will be added later.
    }

    public void registerHospital() {
        // Hospital registration logic will be added later.
    }
}