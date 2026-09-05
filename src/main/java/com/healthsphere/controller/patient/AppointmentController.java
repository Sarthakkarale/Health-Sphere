package com.healthsphere.controller.patient;

import java.util.List;

import com.healthsphere.dao.authentication.DoctorDAO;
import com.healthsphere.dao.authentication.HospitalDAO;
import com.healthsphere.dao.patient.AppointmentDAO;
import com.healthsphere.model.Appointment;
import com.healthsphere.model.DoctorProfile;
import com.healthsphere.model.HospitalProfile;
import com.healthsphere.model.PatientProfile;
import com.healthsphere.util.SessionManager;

public class AppointmentController {

    private final AppointmentDAO appointmentDAO;
    private final PatientController patientController;
    private final NotificationController notificationController;

    private final DoctorDAO doctorDAO;
    private final HospitalDAO hospitalDAO;

    public AppointmentController() {

        this.appointmentDAO =
                new AppointmentDAO();

        this.patientController =
                new PatientController();

        this.notificationController =
                new NotificationController();

        this.doctorDAO =
                new DoctorDAO();

        this.hospitalDAO =
                new HospitalDAO();
    }

    // =========================================================
    // GET REAL DOCTORS
    // =========================================================

    public List<DoctorProfile> getAllDoctors() {

        return doctorDAO.getAllDoctors();
    }

    // =========================================================
    // GET REAL HOSPITALS
    // =========================================================

    public List<HospitalProfile> getAllHospitals() {

        return hospitalDAO.getAllHospitals();
    }

    // =========================================================
    // CREATE DOCTOR APPOINTMENT
    // =========================================================

    public Appointment createDoctorAppointment(

            String doctorUid,
            String doctorName,
            String specialty,
            String appointmentDate,
            String appointmentTime,
            String reason) {

        validateSession();

        // -----------------------------------------------------
        // VALIDATION
        // -----------------------------------------------------

        if (doctorUid == null ||
                doctorUid.isBlank()) {

            throw new IllegalArgumentException(
                    "Please select a doctor."
            );
        }

        if (doctorName == null ||
                doctorName.isBlank()) {

            throw new IllegalArgumentException(
                    "Doctor name is missing."
            );
        }

        if (specialty == null ||
                specialty.isBlank()) {

            throw new IllegalArgumentException(
                    "Doctor specialization is missing."
            );
        }

        validateDateAndTime(
                appointmentDate,
                appointmentTime
        );

        // -----------------------------------------------------
        // CURRENT PATIENT
        // -----------------------------------------------------

        String patientUid =
                SessionManager
                        .getCurrentUser()
                        .getUid();

        PatientProfile profile =
                patientController
                        .getCurrentPatientProfile();

        String patientName =
                buildPatientName(profile);

        // -----------------------------------------------------
        // CREATE APPOINTMENT
        // -----------------------------------------------------

        Appointment appointment =
                new Appointment();

        appointment.setPatientUid(
                patientUid
        );

        appointment.setPatientName(
                patientName
        );

        appointment.setBookingType(
                "DOCTOR"
        );

        appointment.setDoctorUid(
                doctorUid.trim()
        );

        appointment.setDoctorName(
                doctorName.trim()
        );

        appointment.setHospitalId(
                null
        );

        appointment.setHospitalName(
                null
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
                "PENDING"
        );

        // -----------------------------------------------------
        // SAVE
        // -----------------------------------------------------

        Appointment savedAppointment =
                appointmentDAO.createAppointment(
                        appointment
                );

        // -----------------------------------------------------
        // NOTIFICATION
        // -----------------------------------------------------

        notificationController.createNotification(

                "Doctor Appointment Booked",

                "Your appointment with "
                        + doctorName
                        + " has been booked for "
                        + appointmentDate
                        + " at "
                        + appointmentTime
                        + ".",

                "APPOINTMENT"
        );

        return savedAppointment;
    }

    // =========================================================
    // CREATE HOSPITAL APPOINTMENT
    // =========================================================

    public Appointment createHospitalAppointment(

            String hospitalId,
            String hospitalName,
            String appointmentDate,
            String appointmentTime,
            String reason) {

        validateSession();

        // -----------------------------------------------------
        // VALIDATION
        // -----------------------------------------------------

        if (hospitalId == null ||
                hospitalId.isBlank()) {

            throw new IllegalArgumentException(
                    "Please select a hospital."
            );
        }

        if (hospitalName == null ||
                hospitalName.isBlank()) {

            throw new IllegalArgumentException(
                    "Hospital name is missing."
            );
        }

        validateDateAndTime(
                appointmentDate,
                appointmentTime
        );

        // -----------------------------------------------------
        // CURRENT PATIENT
        // -----------------------------------------------------

        String patientUid =
                SessionManager
                        .getCurrentUser()
                        .getUid();

        PatientProfile profile =
                patientController
                        .getCurrentPatientProfile();

        String patientName =
                buildPatientName(profile);

        // -----------------------------------------------------
        // CREATE APPOINTMENT
        // -----------------------------------------------------

        Appointment appointment =
                new Appointment();

        appointment.setPatientUid(
                patientUid
        );

        appointment.setPatientName(
                patientName
        );

        appointment.setBookingType(
                "HOSPITAL"
        );

        // Hospital relationship
        appointment.setHospitalId(
                hospitalId.trim()
        );

        appointment.setHospitalName(
                hospitalName.trim()
        );

        // Doctor will be assigned later
        appointment.setDoctorUid(
                null
        );

        appointment.setDoctorName(
                null
        );

        appointment.setSpecialty(
                null
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
                "PENDING_ASSIGNMENT"
        );

        // -----------------------------------------------------
        // SAVE
        // -----------------------------------------------------

        Appointment savedAppointment =
                appointmentDAO.createAppointment(
                        appointment
                );

        // -----------------------------------------------------
        // NOTIFICATION
        // -----------------------------------------------------

        notificationController.createNotification(

                "Hospital Appointment Booked",

                "Your appointment request at "
                        + hospitalName
                        + " has been submitted for "
                        + appointmentDate
                        + " at "
                        + appointmentTime
                        + ".",

                "APPOINTMENT"
        );

        return savedAppointment;
    }

    // =========================================================
    // GET CURRENT PATIENT APPOINTMENTS
    // =========================================================

    public List<Appointment>
            getCurrentPatientAppointments() {

        validateSession();

        String patientUid =
                SessionManager
                        .getCurrentUser()
                        .getUid();

        return appointmentDAO
                .getPatientAppointments(
                        patientUid
                );
    }

    // =========================================================
    // BUILD PATIENT NAME
    // =========================================================

    private String buildPatientName(
            PatientProfile profile) {

        if (profile == null) {
            return "Patient";
        }

        String firstName =
                profile.getFirstName() == null
                        ? ""
                        : profile
                                .getFirstName()
                                .trim();

        String lastName =
                profile.getLastName() == null
                        ? ""
                        : profile
                                .getLastName()
                                .trim();

        String fullName =
                (firstName + " " + lastName)
                        .trim();

        return fullName.isBlank()
                ? "Patient"
                : fullName;
    }

    // =========================================================
    // SESSION VALIDATION
    // =========================================================

    private void validateSession() {

        if (!SessionManager.isLoggedIn()) {

            throw new IllegalStateException(
                    "No active user session."
            );
        }

        if (SessionManager
                .getCurrentUser()
                .getUid() == null ||

                SessionManager
                        .getCurrentUser()
                        .getUid()
                        .isBlank()) {

            throw new IllegalStateException(
                    "Current user UID is missing."
            );
        }
    }

    // =========================================================
    // DATE AND TIME VALIDATION
    // =========================================================

    private void validateDateAndTime(

            String appointmentDate,
            String appointmentTime) {

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

    public boolean isSlotBooked(String doctorUidOrHospitalId, String date, String time) {
        if (doctorUidOrHospitalId == null || date == null || time == null) return false;
        return appointmentDAO.isSlotBooked(doctorUidOrHospitalId, date, time);
    }
}