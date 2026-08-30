package com.healthsphere.controller.patient;

import java.util.List;

import com.healthsphere.dao.patient.HospitalAppointmentDAO;
import com.healthsphere.model.HospitalAppointment;
import com.healthsphere.model.PatientProfile;
import com.healthsphere.util.SessionManager;

public class HospitalAppointmentController {

    private final HospitalAppointmentDAO
            hospitalAppointmentDAO;

    private final PatientController
            patientController;

    private final NotificationController
            notificationController;


    public HospitalAppointmentController() {

        this.hospitalAppointmentDAO =
                new HospitalAppointmentDAO();

        this.patientController =
                new PatientController();

        this.notificationController =
                new NotificationController();
    }


    // ============================================================
    // CREATE HOSPITAL APPOINTMENT
    // ============================================================

    public HospitalAppointment createAppointment(

            String hospitalId,
            String hospitalName,
            String appointmentDate,
            String appointmentTime,
            String reason) {

        validateSession();

        validateAppointmentInput(

                hospitalName,
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


        HospitalAppointment appointment =
                new HospitalAppointment();

        appointment.setPatientUid(
                uid
        );

        appointment.setPatientName(
                patientName
        );

        appointment.setHospitalId(

                hospitalId == null
                        ? ""
                        : hospitalId.trim()
        );

        appointment.setHospitalName(
                hospitalName.trim()
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


        HospitalAppointment savedAppointment =

                hospitalAppointmentDAO
                        .createAppointment(
                                appointment
                        );


        // ========================================================
        // CREATE NOTIFICATION
        // ========================================================

        notificationController
                .createNotification(

                        "Hospital Appointment Booked",

                        "Your appointment at "
                                + appointment.getHospitalName()
                                + " has been successfully booked for "
                                + appointment.getAppointmentDate()
                                + " at "
                                + appointment.getAppointmentTime()
                                + ".",

                        "HOSPITAL_APPOINTMENT"
                );


        return savedAppointment;
    }


    // ============================================================
    // GET CURRENT PATIENT HOSPITAL APPOINTMENTS
    // ============================================================

    public List<HospitalAppointment>
            getCurrentPatientAppointments() {

        validateSession();

        String uid =
                SessionManager
                        .getCurrentUser()
                        .getUid();

        return hospitalAppointmentDAO
                .getPatientAppointments(
                        uid
                );
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

            String hospitalName,
            String appointmentDate,
            String appointmentTime) {

        if (hospitalName == null
                || hospitalName.isBlank()) {

            throw new IllegalArgumentException(
                    "Please select a hospital."
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