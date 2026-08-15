package com.healthsphere.controller.patient;

import java.util.List;

import com.healthsphere.dao.patient.AppointmentDAO;
import com.healthsphere.model.Appointment;
import com.healthsphere.model.PatientProfile;
import com.healthsphere.util.SessionManager;

public class AppointmentController {

    private final AppointmentDAO appointmentDAO;
    private final PatientController patientController;

    public AppointmentController() {

        this.appointmentDAO =
                new AppointmentDAO();

        this.patientController =
                new PatientController();
    }

    // ============================================================
    // CREATE CURRENT PATIENT APPOINTMENT
    // ============================================================

    public Appointment createAppointment(
            String doctorName,
            String specialty,
            String hospital,
            String appointmentDate,
            String appointmentTime,
            String reason) {

        validateSession();

        validateAppointmentInput(
                doctorName,
                specialty,
                hospital,
                appointmentDate,
                appointmentTime
        );

        String uid =
                SessionManager
                        .getCurrentUser()
                        .getUid();

        PatientProfile profile =
                patientController
                        .getCurrentPatientProfile();

        String patientName =
                buildPatientName(profile);

        Appointment appointment =
                new Appointment();

        appointment.setPatientUid(uid);
        appointment.setPatientName(patientName);
        appointment.setDoctorName(
                doctorName.trim()
        );
        appointment.setSpecialty(
                specialty.trim()
        );
        appointment.setHospital(
                hospital.trim()
        );
        appointment.setAppointmentDate(
                appointmentDate.trim()
        );
        appointment.setAppointmentTime(
                appointmentTime.trim()
        );
        appointment.setReason(
                reason == null
                        ? ""
                        : reason.trim()
        );

        appointment.setStatus(
                "Upcoming"
        );

        return appointmentDAO.createAppointment(
                appointment
        );
    }

    // ============================================================
    // GET CURRENT PATIENT APPOINTMENTS
    // ============================================================

    public List<Appointment> getCurrentPatientAppointments() {

        validateSession();

        String uid =
                SessionManager
                        .getCurrentUser()
                        .getUid();

        return appointmentDAO
                .getPatientAppointments(uid);
    }

    // ============================================================
    // PATIENT NAME
    // ============================================================

    private String buildPatientName(
            PatientProfile profile) {

        if (profile == null) {
            return "Patient";
        }

        String firstName =
                profile.getFirstName() == null
                        ? ""
                        : profile.getFirstName().trim();

        String lastName =
                profile.getLastName() == null
                        ? ""
                        : profile.getLastName().trim();

        String fullName =
                (firstName + " " + lastName)
                        .trim();

        return fullName.isBlank()
                ? "Patient"
                : fullName;
    }

    // ============================================================
    // SESSION VALIDATION
    // ============================================================

    private void validateSession() {

        if (!SessionManager.isLoggedIn()) {

            throw new IllegalStateException(
                    "No active user session."
            );
        }

        if (SessionManager
                .getCurrentUser()
                .getUid() == null) {

            throw new IllegalStateException(
                    "Current user UID is missing."
            );
        }
    }

    // ============================================================
    // INPUT VALIDATION
    // ============================================================

    private void validateAppointmentInput(
            String doctorName,
            String specialty,
            String hospital,
            String appointmentDate,
            String appointmentTime) {

        if (doctorName == null ||
                doctorName.isBlank()) {

            throw new IllegalArgumentException(
                    "Please select a doctor."
            );
        }

        if (specialty == null ||
                specialty.isBlank()) {

            throw new IllegalArgumentException(
                    "Please select a specialty."
            );
        }

        if (hospital == null ||
                hospital.isBlank()) {

            throw new IllegalArgumentException(
                    "Please select a hospital."
            );
        }

        if (appointmentDate == null ||
                appointmentDate.isBlank()) {

            throw new IllegalArgumentException(
                    "Please select an appointment date."
            );
        }

        if (appointmentTime == null ||
                appointmentTime.isBlank()) {

            throw new IllegalArgumentException(
                    "Please select an appointment time."
            );
        }
    }
}
