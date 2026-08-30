package com.healthsphere.controller.patient;

import java.util.List;

import com.healthsphere.dao.patient.DoctorAppointmentDAO;
import com.healthsphere.model.DoctorAppointment;
import com.healthsphere.model.PatientProfile;
import com.healthsphere.util.SessionManager;

public class DoctorAppointmentController {

    private final DoctorAppointmentDAO
            doctorAppointmentDAO;

    private final PatientController
            patientController;

    private final NotificationController
            notificationController;


    public DoctorAppointmentController() {

        this.doctorAppointmentDAO =
                new DoctorAppointmentDAO();

        this.patientController =
                new PatientController();

        this.notificationController =
                new NotificationController();
    }


    // ============================================================
    // CREATE DOCTOR APPOINTMENT
    // ============================================================

    public DoctorAppointment createAppointment(

            String doctorId,
            String doctorName,
            String specialty,
            String appointmentDate,
            String appointmentTime,
            String reason) {

        validateSession();

        validateAppointmentInput(

                doctorName,
                specialty,
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


        DoctorAppointment appointment =
                new DoctorAppointment();

        appointment.setPatientUid(uid);

        appointment.setPatientName(
                patientName
        );

        appointment.setDoctorId(
                doctorId == null
                        ? ""
                        : doctorId.trim()
        );

        appointment.setDoctorName(
                doctorName.trim()
        );

        appointment.setSpecialty(
                specialty.trim()
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


        DoctorAppointment savedAppointment =
                doctorAppointmentDAO
                        .createAppointment(
                                appointment
                        );


        // ========================================================
        // CREATE NOTIFICATION
        // ========================================================

        notificationController
                .createNotification(

                        "Doctor Appointment Booked",

                        "Your appointment with Dr. "
                                + appointment.getDoctorName()
                                + " has been successfully booked for "
                                + appointment.getAppointmentDate()
                                + " at "
                                + appointment.getAppointmentTime()
                                + ".",

                        "DOCTOR_APPOINTMENT"
                );


        return savedAppointment;
    }


    // ============================================================
    // GET CURRENT PATIENT DOCTOR APPOINTMENTS
    // ============================================================

    public List<DoctorAppointment>
            getCurrentPatientAppointments() {

        validateSession();

        String uid =
                SessionManager
                        .getCurrentUser()
                        .getUid();

        return doctorAppointmentDAO
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
                        : profile.getFirstName()
                                .trim();

        String lastName =

                profile.getLastName() == null
                        ? ""
                        : profile.getLastName()
                                .trim();

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
            String appointmentDate,
            String appointmentTime) {

        if (doctorName == null
                || doctorName.isBlank()) {

            throw new IllegalArgumentException(
                    "Please select a doctor."
            );
        }

        if (specialty == null
                || specialty.isBlank()) {

            throw new IllegalArgumentException(
                    "Please select a specialty."
            );
        }

        if (appointmentDate == null
                || appointmentDate.isBlank()) {

            throw new IllegalArgumentException(
                    "Please select an appointment date."
            );
        }

        if (appointmentTime == null
                || appointmentTime.isBlank()) {

            throw new IllegalArgumentException(
                    "Please select an appointment time."
            );
        }
    }
}