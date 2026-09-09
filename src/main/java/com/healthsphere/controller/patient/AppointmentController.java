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

        validateDoctorAvailabilityAndConflict(
                doctorUid,
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

        try {
            com.healthsphere.dao.doctor.DoctorAvailabilityDAO availabilityDAO = new com.healthsphere.dao.doctor.DoctorAvailabilityDAO();
            com.healthsphere.model.DoctorAvailability availability = availabilityDAO.getAvailability(doctorUid.trim());
            if (availability != null && availability.getConsultationFee() >= 0) {
                appointment.setFee(availability.getConsultationFee());
            }
        } catch (Exception ignored) {
        }

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

    public void validateDoctorAvailabilityAndConflict(String doctorUid, String dateStr, String timeStr) {
        if (doctorUid == null || doctorUid.isBlank() || dateStr == null || timeStr == null) {
            return;
        }

        if (isSlotBooked(doctorUid, dateStr, timeStr)) {
            throw new IllegalArgumentException("This time slot is already booked.");
        }

        try {
            com.healthsphere.dao.doctor.DoctorAvailabilityDAO availabilityDAO = new com.healthsphere.dao.doctor.DoctorAvailabilityDAO();
            com.healthsphere.model.DoctorAvailability availability = availabilityDAO.getAvailability(doctorUid);

            if (availability != null) {
                java.time.LocalDate localDate = java.time.LocalDate.parse(dateStr.trim());
                java.time.DayOfWeek dayOfWeek = localDate.getDayOfWeek();

                boolean dayEnabled = false;
                String startTime = null;
                String endTime = null;

                switch (dayOfWeek) {
                    case MONDAY:
                        dayEnabled = availability.isMondayEnabled();
                        startTime = availability.getMondayStartTime();
                        endTime = availability.getMondayEndTime();
                        break;
                    case TUESDAY:
                        dayEnabled = availability.isTuesdayEnabled();
                        startTime = availability.getTuesdayStartTime();
                        endTime = availability.getTuesdayEndTime();
                        break;
                    case WEDNESDAY:
                        dayEnabled = availability.isWednesdayEnabled();
                        startTime = availability.getWednesdayStartTime();
                        endTime = availability.getWednesdayEndTime();
                        break;
                    case THURSDAY:
                        dayEnabled = availability.isThursdayEnabled();
                        startTime = availability.getThursdayStartTime();
                        endTime = availability.getThursdayEndTime();
                        break;
                    case FRIDAY:
                        dayEnabled = availability.isFridayEnabled();
                        startTime = availability.getFridayStartTime();
                        endTime = availability.getFridayEndTime();
                        break;
                    case SATURDAY:
                        dayEnabled = availability.isSaturdayEnabled();
                        startTime = availability.getSaturdayStartTime();
                        endTime = availability.getSaturdayEndTime();
                        break;
                    case SUNDAY:
                        dayEnabled = availability.isSundayEnabled();
                        startTime = availability.getSundayStartTime();
                        endTime = availability.getSundayEndTime();
                        break;
                }

                if (availability.isSlotOff(dayOfWeek.toString(), timeStr)) {
                    throw new IllegalArgumentException("This time slot is not available for the selected doctor.");
                }

                if (!dayEnabled) {
                    throw new IllegalArgumentException("This time slot is not available for the selected doctor.");
                }

                if (startTime != null && !startTime.isBlank() && endTime != null && !endTime.isBlank()) {
                    int slotMin = parseTimeToMinutes(timeStr);
                    int startMin = parseTimeToMinutes(startTime);
                    int endMin = parseTimeToMinutes(endTime);

                    if (slotMin < startMin || slotMin >= endMin) {
                        throw new IllegalArgumentException("This time slot is not available for the selected doctor.");
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception ignored) {
        }
    }

    public List<String> getAvailableSlots(String doctorUid, String dateStr) {
        List<String> available = new java.util.ArrayList<>();
        if (doctorUid == null || doctorUid.isBlank() || dateStr == null || dateStr.isBlank()) {
            return available;
        }
        try {
            com.healthsphere.dao.doctor.DoctorAvailabilityDAO availabilityDAO = new com.healthsphere.dao.doctor.DoctorAvailabilityDAO();
            com.healthsphere.model.DoctorAvailability availability = availabilityDAO.getAvailability(doctorUid.trim());
            if (availability == null) {
                String[] defaultTimes = {"09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "02:00 PM", "02:30 PM", "03:00 PM", "03:30 PM", "04:00 PM"};
                for (String t : defaultTimes) {
                    if (!isSlotBooked(doctorUid, dateStr, t)) {
                        available.add(t);
                    }
                }
                return available;
            }

            java.time.LocalDate localDate = java.time.LocalDate.parse(dateStr.trim());
            java.time.DayOfWeek dayOfWeek = localDate.getDayOfWeek();
            boolean dayEnabled = false;
            String startTime = null;
            String endTime = null;

            switch (dayOfWeek) {
                case MONDAY: dayEnabled = availability.isMondayEnabled(); startTime = availability.getMondayStartTime(); endTime = availability.getMondayEndTime(); break;
                case TUESDAY: dayEnabled = availability.isTuesdayEnabled(); startTime = availability.getTuesdayStartTime(); endTime = availability.getTuesdayEndTime(); break;
                case WEDNESDAY: dayEnabled = availability.isWednesdayEnabled(); startTime = availability.getWednesdayStartTime(); endTime = availability.getWednesdayEndTime(); break;
                case THURSDAY: dayEnabled = availability.isThursdayEnabled(); startTime = availability.getThursdayStartTime(); endTime = availability.getThursdayEndTime(); break;
                case FRIDAY: dayEnabled = availability.isFridayEnabled(); startTime = availability.getFridayStartTime(); endTime = availability.getFridayEndTime(); break;
                case SATURDAY: dayEnabled = availability.isSaturdayEnabled(); startTime = availability.getSaturdayStartTime(); endTime = availability.getSaturdayEndTime(); break;
                case SUNDAY: dayEnabled = availability.isSundayEnabled(); startTime = availability.getSundayStartTime(); endTime = availability.getSundayEndTime(); break;
            }

            if (!dayEnabled) {
                return available;
            }

            int duration = availability.getSlotDurationMinutes() > 0 ? availability.getSlotDurationMinutes() : 30;
            int startMin = (startTime != null && !startTime.isBlank()) ? parseTimeToMinutes(startTime) : 9 * 60;
            int endMin = (endTime != null && !endTime.isBlank()) ? parseTimeToMinutes(endTime) : 17 * 60;

            for (int m = startMin; m + duration <= endMin; m += duration) {
                String timeStr = formatMinutesToTime(m);
                String dayName = dayOfWeek.toString();
                if (!availability.isSlotOff(dayName, timeStr) && !isSlotBooked(doctorUid, dateStr, timeStr)) {
                    available.add(timeStr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return available;
    }

    private String formatMinutesToTime(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        String meridiem = hours >= 12 ? "PM" : "AM";
        int displayHours = hours % 12;
        if (displayHours == 0) displayHours = 12;
        return String.format("%02d:%02d %s", displayHours, mins, meridiem);
    }

    private int parseTimeToMinutes(String value) {
        if (value == null || value.trim().isEmpty()) return 0;
        String normalized = value.trim().toUpperCase();
        String[] parts = normalized.split(" ");
        if (parts.length != 2) return 0;
        String[] hm = parts[0].split(":");
        if (hm.length != 2) return 0;
        try {
            int hour = Integer.parseInt(hm[0]);
            int minute = Integer.parseInt(hm[1]);
            String meridiem = parts[1];
            if (hour == 12) hour = 0;
            if ("PM".equals(meridiem)) hour += 12;
            return hour * 60 + minute;
        } catch (Exception e) {
            return 0;
        }
    }
}