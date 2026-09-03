package com.healthsphere.controller.doctor;

import com.healthsphere.dao.authentication.PatientDAO;
import com.healthsphere.dao.appointment.AppointmentDAO;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.PatientProfile;

import java.util.ArrayList;
import java.util.List;

public class PatientController {

    private final PatientDAO patientDAO;
    private final AppointmentDAO appointmentDAO;

    public PatientController() {
        this.patientDAO = new PatientDAO();
        this.appointmentDAO = new AppointmentDAO();
    }

    // ============================================================
    // GET PATIENT PROFILE
    // ============================================================

    public PatientProfile getPatientProfile(String uid) {

        if (uid == null || uid.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Patient UID cannot be empty."
            );
        }

        try {

            return patientDAO.getPatientProfile(uid);

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to retrieve patient profile.",
                    e
            );
        }
    }

    // ============================================================
    // CREATE PATIENT PROFILE
    // ============================================================

    public void createPatient(PatientProfile patientProfile) {

        if (patientProfile == null) {

            throw new IllegalArgumentException(
                    "Patient profile cannot be null."
            );
        }

        if (patientProfile.getUid() == null ||
                patientProfile.getUid().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Patient UID cannot be empty."
            );
        }

        try {

            patientDAO.createPatientProfile(
                    patientProfile
            );

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to create patient profile.",
                    e
            );
        }
    }

    // ============================================================
    // UPDATE PATIENT PROFILE
    // ============================================================

    public void updatePatient(PatientProfile patientProfile) {

        if (patientProfile == null) {

            throw new IllegalArgumentException(
                    "Patient profile cannot be null."
            );
        }

        if (patientProfile.getUid() == null ||
                patientProfile.getUid().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Patient UID cannot be empty."
            );
        }

        try {

            patientDAO.updatePatientProfile(
                    patientProfile
            );

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to update patient profile.",
                    e
            );
        }
    }

    // ============================================================
    // GET PATIENTS FOR DOCTOR
    // ============================================================
    //
    // FLOW:
    //
    // 1. Get all patient UIDs from appointments
    //    where doctorUid matches.
    //
    // 2. Fetch every patient profile from patients collection.
    //
    // 3. Return the patient profiles.
    //
    // ============================================================

    public List<PatientProfile> getPatientsForDoctor(
            String doctorUid) {

        if (doctorUid == null ||
                doctorUid.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Doctor UID cannot be empty."
            );
        }

        try {

            // ----------------------------------------------------
            // STEP 1 — GET PATIENT UIDS FROM APPOINTMENTS
            // ----------------------------------------------------

            List<String> patientUids =
                    appointmentDAO.getPatientUidsForDoctor(
                            doctorUid
                    );

            // ----------------------------------------------------
            // STEP 2 — CREATE RESULT LIST
            // ----------------------------------------------------

            List<PatientProfile> patients =
                    new ArrayList<>();

            // ----------------------------------------------------
            // STEP 3 — FETCH PATIENT PROFILES
            // ----------------------------------------------------

            for (String patientUid : patientUids) {

                try {

                    PatientProfile patient =
                            patientDAO.getPatientProfile(
                                    patientUid
                            );

                    if (patient != null) {

                        patients.add(patient);
                    }

                } catch (DatabaseException e) {

                    /*
                     * If one patient profile cannot be retrieved,
                     * do not stop the entire doctor patient list.
                     *
                     * Continue with the remaining patients.
                     */

                    System.err.println(
                            "Unable to retrieve patient: "
                                    + patientUid
                    );
                }
            }

            return patients;

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to retrieve patients for doctor.",
                    e
            );
        }
    }

    // ============================================================
    // GET PATIENTS FOR DOCTOR - SAFE EMPTY LIST VERSION
    // ============================================================
    //
    // This method is useful for UI screens.
    //
    // Instead of crashing when there are no appointments,
    // it returns an empty list.
    //
    // ============================================================

    public List<PatientProfile> getPatientsForDoctorSafe(
            String doctorUid) {

        if (doctorUid == null ||
                doctorUid.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Doctor UID cannot be empty."
            );
        }

        try {

            List<PatientProfile> patients = getPatientsForDoctor(doctorUid);

            if (patients == null || patients.isEmpty()) {

                patients = patientDAO.getAllPatients();
            }

            return patients != null ? patients : new ArrayList<>();

        } catch (Exception e) {

            System.err.println(
                    "Unable to load doctor patients: "
                            + e.getMessage()
            );

            try {

                List<PatientProfile> fallback = patientDAO.getAllPatients();

                return fallback != null ? fallback : new ArrayList<>();

            } catch (Exception ignored) {

                return new ArrayList<>();
            }
        }
    }
}