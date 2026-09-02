package com.healthsphere.controller.appointment;

import com.healthsphere.dao.appointment.AppointmentDAO;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.Appointment;

import java.util.List;

public class AppointmentController {

    private final AppointmentDAO appointmentDAO;

    public AppointmentController() {
        this.appointmentDAO = new AppointmentDAO();
    }

    // ============================================================
    // CREATE APPOINTMENT
    // ============================================================

    public Appointment createAppointment(
            Appointment appointment) {

        validateAppointment(appointment);

        try {

            return appointmentDAO.createAppointment(
                    appointment
            );

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to create appointment.",
                    e
            );
        }
    }

    // ============================================================
    // GET APPOINTMENT BY ID
    // ============================================================

    public Appointment getAppointment(
            String appointmentId) {

        validateId(
                appointmentId,
                "Appointment ID"
        );

        try {

            return appointmentDAO.getAppointment(
                    appointmentId
            );

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to retrieve appointment.",
                    e
            );
        }
    }

    // ============================================================
    // GET DOCTOR APPOINTMENTS
    // ============================================================

    public List<Appointment> getDoctorAppointments(
            String doctorUid) {

        validateId(
                doctorUid,
                "Doctor UID"
        );

        try {

            return appointmentDAO.getDoctorAppointments(
                    doctorUid
            );

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to retrieve doctor appointments.",
                    e
            );
        }
    }

    // ============================================================
    // GET PATIENT APPOINTMENTS
    // ============================================================

    public List<Appointment> getPatientAppointments(
            String patientUid) {

        validateId(
                patientUid,
                "Patient UID"
        );

        try {

            return appointmentDAO.getPatientAppointments(
                    patientUid
            );

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to retrieve patient appointments.",
                    e
            );
        }
    }

    // ============================================================
    // GET HOSPITAL APPOINTMENTS
    // ============================================================

    public List<Appointment> getHospitalAppointments(
            String hospitalId) {

        validateId(
                hospitalId,
                "Hospital ID"
        );

        try {

            return appointmentDAO.getHospitalAppointments(
                    hospitalId
            );

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to retrieve hospital appointments.",
                    e
            );
        }
    }

    // ============================================================
    // UPDATE APPOINTMENT
    // ============================================================

    public void updateAppointment(
            Appointment appointment) {

        validateAppointment(appointment);

        if (appointment.getAppointmentId() == null ||
                appointment.getAppointmentId().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Appointment ID cannot be empty."
            );
        }

        try {

            appointmentDAO.updateAppointment(
                    appointment
            );

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to update appointment.",
                    e
            );
        }
    }

    // ============================================================
    // UPDATE APPOINTMENT STATUS
    // ============================================================

    public void updateAppointmentStatus(
            String appointmentId,
            String status) {

        validateId(
                appointmentId,
                "Appointment ID"
        );

        if (status == null ||
                status.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Appointment status cannot be empty."
            );
        }

        try {

            appointmentDAO.updateAppointmentStatus(
                    appointmentId,
                    status
            );

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to update appointment status.",
                    e
            );
        }
    }

    // ============================================================
    // ASSIGN DOCTOR TO HOSPITAL APPOINTMENT
    // ============================================================

    public void assignDoctor(
            String appointmentId,
            String doctorUid,
            String doctorName) {

        validateId(
                appointmentId,
                "Appointment ID"
        );

        validateId(
                doctorUid,
                "Doctor UID"
        );

        if (doctorName == null ||
                doctorName.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Doctor name cannot be empty."
            );
        }

        try {

            Appointment appointment =
                    appointmentDAO.getAppointment(
                            appointmentId
                    );

            if (appointment == null) {

                throw new DatabaseException(
                        "Appointment not found."
                );
            }

            // ----------------------------------------------------
            // Only hospital appointments can be assigned this way
            // ----------------------------------------------------

            if (!"HOSPITAL".equalsIgnoreCase(
                    appointment.getBookingType())) {

                throw new IllegalArgumentException(
                        "Doctor assignment is only allowed "
                                + "for hospital appointments."
                );
            }

            // ----------------------------------------------------
            // Assign doctor
            // ----------------------------------------------------

            appointment.setDoctorUid(
                    doctorUid
            );

            appointment.setDoctorName(
                    doctorName
            );

            appointment.setStatus(
                    "CONFIRMED"
            );

            appointmentDAO.updateAppointment(
                    appointment
            );

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to assign doctor to appointment.",
                    e
            );
        }
    }

    // ============================================================
    // CANCEL APPOINTMENT
    // ============================================================

    public void cancelAppointment(
            String appointmentId) {

        validateId(
                appointmentId,
                "Appointment ID"
        );

        try {

            appointmentDAO.updateAppointmentStatus(
                    appointmentId,
                    "CANCELLED"
            );

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to cancel appointment.",
                    e
            );
        }
    }

    // ============================================================
    // VALIDATE APPOINTMENT
    // ============================================================

    private void validateAppointment(
            Appointment appointment) {

        if (appointment == null) {

            throw new IllegalArgumentException(
                    "Appointment cannot be null."
            );
        }

        // --------------------------------------------------------
        // Patient is always required
        // --------------------------------------------------------

        validateId(
                appointment.getPatientUid(),
                "Patient UID"
        );

        // --------------------------------------------------------
        // Booking type is required
        // --------------------------------------------------------

        String bookingType =
                appointment.getBookingType();

        if (bookingType == null ||
                bookingType.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Booking type cannot be empty."
            );
        }

        // --------------------------------------------------------
        // Doctor booking
        // --------------------------------------------------------

        if ("DOCTOR".equalsIgnoreCase(
                bookingType)) {

            if (appointment.getDoctorUid() == null ||
                    appointment.getDoctorUid().trim().isEmpty()) {

                throw new IllegalArgumentException(
                        "Doctor UID is required for "
                                + "doctor appointments."
                );
            }
        }

        // --------------------------------------------------------
        // Hospital booking
        // --------------------------------------------------------

        else if ("HOSPITAL".equalsIgnoreCase(
                bookingType)) {

            if (appointment.getHospitalId() == null ||
                    appointment.getHospitalId().trim().isEmpty()) {

                throw new IllegalArgumentException(
                        "Hospital ID is required for "
                                + "hospital appointments."
                );
            }
        }

        // --------------------------------------------------------
        // Unknown booking type
        // --------------------------------------------------------

        else {

            throw new IllegalArgumentException(
                    "Invalid booking type. "
                            + "Use DOCTOR or HOSPITAL."
            );
        }

        // --------------------------------------------------------
        // Date
        // --------------------------------------------------------

        if (appointment.getAppointmentDate() == null ||
                appointment.getAppointmentDate().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Appointment date cannot be empty."
            );
        }

        // --------------------------------------------------------
        // Time
        // --------------------------------------------------------

        if (appointment.getAppointmentTime() == null ||
                appointment.getAppointmentTime().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Appointment time cannot be empty."
            );
        }

        // --------------------------------------------------------
        // Status
        // --------------------------------------------------------

        if (appointment.getStatus() == null ||
                appointment.getStatus().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Appointment status cannot be empty."
            );
        }
    }

    // ============================================================
    // VALIDATE ID
    // ============================================================

    private void validateId(
            String id,
            String fieldName) {

        if (id == null ||
                id.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    fieldName + " cannot be empty."
            );
        }
    }
}