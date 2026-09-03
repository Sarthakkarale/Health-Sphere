package com.healthsphere.controller.hospital;

import com.healthsphere.dao.hospital.AppointmentDAO;
import com.healthsphere.model.Appointment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AppointmentController {

    private final AppointmentDAO appointmentDAO;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AppointmentController() {

        this.appointmentDAO =
                new AppointmentDAO();
    }

    // =========================================================
    // GET HOSPITAL APPOINTMENTS
    // =========================================================

    public List<Appointment> getHospitalAppointments() {

        return appointmentDAO
                .getHospitalAppointments();
    }

    // =========================================================
    // GET APPOINTMENT BY ID
    // =========================================================

    public Appointment getAppointmentById(
            String appointmentId
    ) {

        validateAppointmentId(
                appointmentId
        );

        return appointmentDAO
                .getAppointmentById(
                        appointmentId
                );
    }

    // =========================================================
    // ASSIGN DOCTOR
    // =========================================================

    public void assignDoctor(
            String appointmentId,
            String doctorUid,
            String doctorName
    ) {

        validateAppointmentId(
                appointmentId
        );

        if (doctorUid == null ||
                doctorUid.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Doctor UID cannot be empty."
            );
        }

        if (doctorName == null ||
                doctorName.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Doctor name cannot be empty."
            );
        }

        appointmentDAO.assignDoctor(
                appointmentId,
                doctorUid.trim(),
                doctorName.trim()
        );
    }

    // =========================================================
    // ACCEPT APPOINTMENT
    // =========================================================

    public void acceptAppointment(
            String appointmentId
    ) {

        validateAppointmentId(
                appointmentId
        );

        appointmentDAO.updateAppointmentStatus(
                appointmentId,
                "ACCEPTED"
        );
    }

    // =========================================================
    // REJECT APPOINTMENT
    // =========================================================

    public void rejectAppointment(
            String appointmentId
    ) {

        validateAppointmentId(
                appointmentId
        );

        appointmentDAO.updateAppointmentStatus(
                appointmentId,
                "REJECTED"
        );
    }

    // =========================================================
    // COMPLETE APPOINTMENT
    // =========================================================

    public void completeAppointment(
            String appointmentId
    ) {

        validateAppointmentId(
                appointmentId
        );

        appointmentDAO.updateAppointmentStatus(
                appointmentId,
                "COMPLETED"
        );
    }

    // =========================================================
    // UPDATE STATUS
    // =========================================================

    public void updateAppointmentStatus(
            String appointmentId,
            String status
    ) {

        validateAppointmentId(
                appointmentId
        );

        if (status == null ||
                status.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Appointment status cannot be empty."
            );
        }

        appointmentDAO.updateAppointmentStatus(
                appointmentId,
                status.trim().toUpperCase()
        );
    }

    // =========================================================
    // CANCEL APPOINTMENT
    // =========================================================

    public void cancelAppointment(
            String appointmentId
    ) {

        validateAppointmentId(
                appointmentId
        );

        appointmentDAO.cancelAppointment(
                appointmentId
        );
    }

    // =========================================================
    // RESCHEDULE
    // =========================================================

    public void rescheduleAppointment(
            String appointmentId,
            String appointmentDate,
            String appointmentTime
    ) {

        validateAppointmentId(
                appointmentId
        );

        if (appointmentDate == null ||
                appointmentDate.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Appointment date cannot be empty."
            );
        }

        if (appointmentTime == null ||
                appointmentTime.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Appointment time cannot be empty."
            );
        }

        appointmentDAO.rescheduleAppointment(
                appointmentId,
                appointmentDate.trim(),
                appointmentTime.trim()
        );
    }

    // =========================================================
    // UPDATE APPOINTMENT
    // =========================================================

    public void updateAppointment(
            Appointment appointment
    ) {

        if (appointment == null) {

            throw new IllegalArgumentException(
                    "Appointment cannot be null."
            );
        }

        validateAppointmentId(
                appointment.getAppointmentId()
        );

        appointmentDAO.updateAppointment(
                appointment
        );
    }

    // =========================================================
    // CHECK EXISTS
    // =========================================================

    public boolean appointmentExists(
            String appointmentId
    ) {

        if (appointmentId == null ||
                appointmentId.trim().isEmpty()) {

            return false;
        }

        return appointmentDAO
                .appointmentExists(
                        appointmentId
                );
    }

    // =========================================================
    // FILTER BY STATUS
    // =========================================================

    public List<Appointment> filterByStatus(
            List<Appointment> appointments,
            String status
    ) {

        if (appointments == null) {
            return new ArrayList<>();
        }

        if (status == null ||
                status.trim().isEmpty() ||
                "All Status".equalsIgnoreCase(
                        status
                )) {

            return new ArrayList<>(
                    appointments
            );
        }

        String target =
                status.trim();

        return appointments
                .stream()
                .filter(
                        appointment ->
                                appointment != null
                                &&
                                appointment.getStatus() != null
                                &&
                                appointment.getStatus()
                                        .equalsIgnoreCase(
                                                target
                                        )
                )
                .collect(
                        Collectors.toList()
                );
    }

    // =========================================================
    // FILTER BY DOCTOR
    // =========================================================

    public List<Appointment> filterByDoctor(
            List<Appointment> appointments,
            String doctorName
    ) {

        if (appointments == null) {
            return new ArrayList<>();
        }

        if (doctorName == null ||
                doctorName.trim().isEmpty() ||
                "All Doctors".equalsIgnoreCase(
                        doctorName
                )) {

            return new ArrayList<>(
                    appointments
            );
        }

        String target =
                doctorName.trim();

        return appointments
                .stream()
                .filter(
                        appointment ->
                                appointment != null
                                &&
                                appointment.getDoctorName() != null
                                &&
                                appointment.getDoctorName()
                                        .equalsIgnoreCase(
                                                target
                                        )
                )
                .collect(
                        Collectors.toList()
                );
    }

    // =========================================================
    // FILTER BY DATE
    // =========================================================

    public List<Appointment> filterByDate(
            List<Appointment> appointments,
            String appointmentDate
    ) {

        if (appointments == null) {
            return new ArrayList<>();
        }

        if (appointmentDate == null ||
                appointmentDate.trim().isEmpty()) {

            return new ArrayList<>(
                    appointments
            );
        }

        String target =
                appointmentDate.trim();

        return appointments
                .stream()
                .filter(
                        appointment ->
                                appointment != null
                                &&
                                appointment
                                        .getAppointmentDate() != null
                                &&
                                appointment
                                        .getAppointmentDate()
                                        .equals(
                                                target
                                        )
                )
                .collect(
                        Collectors.toList()
                );
    }

    // =========================================================
    // SEARCH APPOINTMENTS
    // =========================================================

    public List<Appointment> searchAppointments(
            List<Appointment> appointments,
            String searchText
    ) {

        if (appointments == null) {
            return new ArrayList<>();
        }

        if (searchText == null ||
                searchText.trim().isEmpty()) {

            return new ArrayList<>(
                    appointments
            );
        }

        String search =
                searchText
                        .trim()
                        .toLowerCase();

        return appointments
                .stream()
                .filter(
                        appointment -> {

                            if (appointment == null) {
                                return false;
                            }

                            String patientName =
                                    safe(
                                            appointment
                                                    .getPatientName()
                                    ).toLowerCase();

                            String patientUid =
                                    safe(
                                            appointment
                                                    .getPatientUid()
                                    ).toLowerCase();

                            String doctorName =
                                    safe(
                                            appointment
                                                    .getDoctorName()
                                    ).toLowerCase();

                            String appointmentId =
                                    safe(
                                            appointment
                                                    .getAppointmentId()
                                    ).toLowerCase();

                            return patientName
                                    .contains(search)
                                    ||
                                    patientUid
                                            .contains(search)
                                    ||
                                    doctorName
                                            .contains(search)
                                    ||
                                    appointmentId
                                            .contains(search);
                        }
                )
                .collect(
                        Collectors.toList()
                );
    }

    // =========================================================
    // VALIDATE APPOINTMENT ID
    // =========================================================

    private void validateAppointmentId(
            String appointmentId
    ) {

        if (appointmentId == null ||
                appointmentId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Appointment ID cannot be empty."
            );
        }
    }

    // =========================================================
    // SAFE
    // =========================================================

    private String safe(
            String value
    ) {

        return value == null
                ? ""
                : value;
    }
}