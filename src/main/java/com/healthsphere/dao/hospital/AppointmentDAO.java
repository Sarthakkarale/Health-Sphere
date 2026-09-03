package com.healthsphere.dao.hospital;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.Appointment;
import com.healthsphere.util.SessionManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentDAO {

    private static final String COLLECTION = "appointments";

    private final Firestore db;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AppointmentDAO() {
        this.db = FirebaseConfig.getFirestore();
    }

    // =========================================================
    // GET CURRENT HOSPITAL ID
    // =========================================================

    private String getCurrentHospitalId() {

        if (SessionManager.getCurrentUser() == null) {
            throw new DatabaseException(
                    "No active hospital session found."
            );
        }

        String hospitalId =
                SessionManager
                        .getCurrentUser()
                        .getUid();

        if (hospitalId == null ||
                hospitalId.trim().isEmpty()) {

            throw new DatabaseException(
                    "Unable to determine current hospital."
            );
        }

        return hospitalId;
    }

    // =========================================================
    // GET HOSPITAL APPOINTMENTS
    // =========================================================

    public List<Appointment> getHospitalAppointments() {

        String hospitalId =
                getCurrentHospitalId();

        try {

            List<Appointment> appointments =
                    new ArrayList<>();

            Query query =
                    db.collection(COLLECTION)
                            .whereEqualTo(
                                    "hospitalId",
                                    hospitalId
                            );

            List<QueryDocumentSnapshot> documents =
                    query.get().get().getDocuments();

            for (DocumentSnapshot document :
                    documents) {

                if (!document.exists()) {
                    continue;
                }

                Appointment appointment =
                        document.toObject(
                                Appointment.class
                        );

                if (appointment == null) {
                    continue;
                }

                appointments.add(
                        appointment
                );
            }

            return appointments;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve hospital appointments.",
                    e
            );
        }
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

        String hospitalId =
                getCurrentHospitalId();

        try {

            DocumentSnapshot document =
                    db.collection(COLLECTION)
                            .document(appointmentId)
                            .get()
                            .get();

            if (!document.exists()) {

                throw new DatabaseException(
                        "Appointment not found."
                );
            }

            Appointment appointment =
                    document.toObject(
                            Appointment.class
                    );

            if (appointment == null) {

                throw new DatabaseException(
                        "Unable to read appointment data."
                );
            }

            /*
             * Security / ownership check.
             *
             * Hospital can only manage appointments
             * belonging to that hospital.
             */
            if (!hospitalId.equals(
                    appointment.getHospitalId()
            )) {

                throw new DatabaseException(
                        "You are not authorized to access this appointment."
                );
            }

            return appointment;

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve appointment.",
                    e
            );
        }
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

        validateDoctor(
                doctorUid,
                doctorName
        );

        Appointment appointment =
                getAppointmentById(
                        appointmentId
                );

        /*
         * Doctor assignment is only allowed for
         * Hospital bookings.
         */
        if (!"HOSPITAL".equalsIgnoreCase(
                safe(
                        appointment.getBookingType()
                )
        )) {

            throw new DatabaseException(
                    "Doctor assignment is only allowed for hospital appointments."
            );
        }

        /*
         * Do not reassign an already assigned doctor
         * through this operation.
         */
        if (appointment.getDoctorUid() != null &&
                !appointment.getDoctorUid()
                        .trim()
                        .isEmpty()) {

            throw new DatabaseException(
                    "A doctor is already assigned to this appointment."
            );
        }

        /*
         * Assignment must happen on the SAME
         * appointment document.
         */
        Map<String, Object> updates =
                new HashMap<>();

        updates.put(
                "doctorUid",
                doctorUid
        );

        updates.put(
                "doctorName",
                doctorName
        );

        /*
         * Hospital assigned doctor.
         *
         * Next state is CONFIRMED.
         */
        updates.put(
                "status",
                "CONFIRMED"
        );

        updates.put(
                "updatedAt",
                getCurrentTimestamp()
        );

        try {

            db.collection(COLLECTION)
                    .document(appointmentId)
                    .update(updates)
                    .get();

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to assign doctor to appointment.",
                    e
            );
        }
    }

    // =========================================================
    // UPDATE APPOINTMENT STATUS
    // =========================================================

    public void updateAppointmentStatus(
            String appointmentId,
            String newStatus
    ) {

        validateAppointmentId(
                appointmentId
        );

        validateStatus(
                newStatus
        );

        Appointment appointment =
                getAppointmentById(
                        appointmentId
                );

        String currentStatus =
                safe(
                        appointment.getStatus()
                ).toUpperCase();

        String targetStatus =
                newStatus.trim()
                        .toUpperCase();

        /*
         * Validate the appointment state transition.
         */
        validateStatusTransition(
                currentStatus,
                targetStatus
        );

        Map<String, Object> updates =
                new HashMap<>();

        updates.put(
                "status",
                targetStatus
        );

        updates.put(
                "updatedAt",
                getCurrentTimestamp()
        );

        try {

            db.collection(COLLECTION)
                    .document(appointmentId)
                    .update(updates)
                    .get();

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to update appointment status.",
                    e
            );
        }
    }

    // =========================================================
    // STATUS TRANSITION VALIDATION
    // =========================================================

    private void validateStatusTransition(
            String currentStatus,
            String targetStatus
    ) {

        /*
         * Same status does not need another update.
         */
        if (currentStatus.equals(
                targetStatus
        )) {

            return;
        }

        /*
         * Hospital appointment workflow:
         *
         * PENDING_ASSIGNMENT
         *        ↓
         * CONFIRMED
         *
         * CONFIRMED
         *    ↙       ↘
         * ACCEPTED   REJECTED
         *
         * ACCEPTED
         *    ↓
         * COMPLETED
         */

        if ("PENDING_ASSIGNMENT".equals(
                currentStatus
        )) {

            if (!"CONFIRMED".equals(
                    targetStatus
            ) &&
                    !"CANCELLED".equals(
                            targetStatus
                    )) {

                throw new DatabaseException(
                        "A pending assignment appointment must be assigned to a doctor before it can change to this status."
                );
            }

            return;
        }

        if ("CONFIRMED".equals(
                currentStatus
        )) {

            if (!"ACCEPTED".equals(
                    targetStatus
            )
                    &&
                    !"REJECTED".equals(
                            targetStatus
                    )
                    &&
                    !"CANCELLED".equals(
                            targetStatus
                    )) {

                throw new DatabaseException(
                        "A confirmed appointment can only be accepted, rejected, or cancelled."
                );
            }

            return;
        }

        if ("ACCEPTED".equals(
                currentStatus
        )) {

            if (!"COMPLETED".equals(
                    targetStatus
            )
                    &&
                    !"CANCELLED".equals(
                            targetStatus
                    )) {

                throw new DatabaseException(
                        "An accepted appointment can only be completed or cancelled."
                );
            }

            return;
        }

        /*
         * PENDING is allowed because doctor-booked
         * appointments can exist in the shared collection.
         */
        if ("PENDING".equals(
                currentStatus
        )) {

            if (!"CONFIRMED".equals(
                    targetStatus
            )
                    &&
                    !"ACCEPTED".equals(
                            targetStatus
                    )
                    &&
                    !"REJECTED".equals(
                            targetStatus
                    )
                    &&
                    !"CANCELLED".equals(
                            targetStatus
                    )) {

                throw new DatabaseException(
                        "Invalid status transition from PENDING."
                );
            }

            return;
        }

        /*
         * Legacy / general statuses.
         */
        if ("WAITING".equals(
                currentStatus
        )
                ||
                "UPCOMING".equals(
                        currentStatus
                )) {

            if (!"ACCEPTED".equals(
                    targetStatus
            )
                    &&
                    !"COMPLETED".equals(
                            targetStatus
                    )
                    &&
                    !"CANCELLED".equals(
                            targetStatus
                    )) {

                throw new DatabaseException(
                        "Invalid appointment status transition."
                );
            }

            return;
        }

        /*
         * Completed, rejected and cancelled appointments
         * are final states.
         */
        if ("COMPLETED".equals(
                currentStatus
        )
                ||
                "REJECTED".equals(
                        currentStatus
                )
                ||
                "CANCELLED".equals(
                        currentStatus
                )) {

            throw new DatabaseException(
                    "This appointment is already in a final state."
            );
        }

        throw new DatabaseException(
                "Invalid appointment status transition."
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

        Appointment appointment =
                getAppointmentById(
                        appointmentId
                );

        String currentStatus =
                safe(
                        appointment.getStatus()
                );

        if ("COMPLETED".equalsIgnoreCase(
                currentStatus
        )) {

            throw new DatabaseException(
                    "Completed appointments cannot be cancelled."
            );
        }

        if ("CANCELLED".equalsIgnoreCase(
                currentStatus
        )) {

            throw new DatabaseException(
                    "Appointment is already cancelled."
            );
        }

        Map<String, Object> updates =
                new HashMap<>();

        updates.put(
                "status",
                "CANCELLED"
        );

        updates.put(
                "updatedAt",
                getCurrentTimestamp()
        );

        try {

            db.collection(COLLECTION)
                    .document(appointmentId)
                    .update(updates)
                    .get();

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to cancel appointment.",
                    e
            );
        }
    }

    // =========================================================
    // RESCHEDULE APPOINTMENT
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

            throw new DatabaseException(
                    "Appointment date cannot be empty."
            );
        }

        if (appointmentTime == null ||
                appointmentTime.trim().isEmpty()) {

            throw new DatabaseException(
                    "Appointment time cannot be empty."
            );
        }

        Appointment appointment =
                getAppointmentById(
                        appointmentId
                );

        String currentStatus =
                safe(
                        appointment.getStatus()
                );

        if ("COMPLETED".equalsIgnoreCase(
                currentStatus
        )
                ||
                "CANCELLED".equalsIgnoreCase(
                        currentStatus
                )
                ||
                "REJECTED".equalsIgnoreCase(
                        currentStatus
                )) {

            throw new DatabaseException(
                    "This appointment cannot be rescheduled."
            );
        }

        Map<String, Object> updates =
                new HashMap<>();

        updates.put(
                "appointmentDate",
                appointmentDate.trim()
        );

        updates.put(
                "appointmentTime",
                appointmentTime.trim()
        );

        updates.put(
                "updatedAt",
                getCurrentTimestamp()
        );

        try {

            db.collection(COLLECTION)
                    .document(appointmentId)
                    .update(updates)
                    .get();

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to reschedule appointment.",
                    e
            );
        }
    }

    // =========================================================
    // UPDATE APPOINTMENT
    // =========================================================

    public void updateAppointment(
            Appointment appointment
    ) {

        if (appointment == null) {

            throw new DatabaseException(
                    "Appointment cannot be null."
            );
        }

        validateAppointmentId(
                appointment.getAppointmentId()
        );

        /*
         * First verify ownership.
         */
        Appointment existing =
                getAppointmentById(
                        appointment.getAppointmentId()
                );

        /*
         * Preserve fields that Hospital should not
         * accidentally change.
         */
        appointment.setHospitalId(
                existing.getHospitalId()
        );

        appointment.setHospitalName(
                existing.getHospitalName()
        );

        appointment.setPatientUid(
                existing.getPatientUid()
        );

        appointment.setBookingType(
                existing.getBookingType()
        );

        appointment.setCreatedAt(
                existing.getCreatedAt()
        );

        appointment.setUpdatedAt(
                getCurrentTimestamp()
        );

        if (appointment.getStatus() == null ||
                appointment.getStatus()
                        .trim()
                        .isEmpty()) {

            appointment.setStatus(
                    existing.getStatus()
            );
        }

        validateStatus(
                appointment.getStatus()
        );

        try {

            db.collection(COLLECTION)
                    .document(
                            appointment
                                    .getAppointmentId()
                    )
                    .set(appointment)
                    .get();

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to update appointment.",
                    e
            );
        }
    }

    // =========================================================
    // CHECK APPOINTMENT EXISTS
    // =========================================================

    public boolean appointmentExists(
            String appointmentId
    ) {

        if (appointmentId == null ||
                appointmentId.trim().isEmpty()) {

            return false;
        }

        try {

            DocumentSnapshot document =
                    db.collection(COLLECTION)
                            .document(
                                    appointmentId
                            )
                            .get()
                            .get();

            if (!document.exists()) {
                return false;
            }

            Appointment appointment =
                    document.toObject(
                            Appointment.class
                    );

            if (appointment == null) {
                return false;
            }

            String hospitalId =
                    getCurrentHospitalId();

            return hospitalId.equals(
                    appointment.getHospitalId()
            );

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to check appointment.",
                    e
            );
        }
    }

    // =========================================================
    // VALIDATE APPOINTMENT ID
    // =========================================================

    private void validateAppointmentId(
            String appointmentId
    ) {

        if (appointmentId == null ||
                appointmentId.trim().isEmpty()) {

            throw new DatabaseException(
                    "Appointment ID cannot be empty."
            );
        }
    }

    // =========================================================
    // VALIDATE DOCTOR
    // =========================================================

    private void validateDoctor(
            String doctorUid,
            String doctorName
    ) {

        if (doctorUid == null ||
                doctorUid.trim().isEmpty()) {

            throw new DatabaseException(
                    "Doctor UID cannot be empty."
            );
        }

        if (doctorName == null ||
                doctorName.trim().isEmpty()) {

            throw new DatabaseException(
                    "Doctor name cannot be empty."
            );
        }
    }

    // =========================================================
    // VALIDATE STATUS
    // =========================================================

    private void validateStatus(
            String status
    ) {

        if (status == null ||
                status.trim().isEmpty()) {

            throw new DatabaseException(
                    "Appointment status cannot be empty."
            );
        }

        String normalized =
                status.trim()
                        .toUpperCase();

        if (!"PENDING".equals(normalized)
                &&
                !"PENDING_ASSIGNMENT".equals(normalized)
                &&
                !"CONFIRMED".equals(normalized)
                &&
                !"ACCEPTED".equals(normalized)
                &&
                !"REJECTED".equals(normalized)
                &&
                !"WAITING".equals(normalized)
                &&
                !"UPCOMING".equals(normalized)
                &&
                !"COMPLETED".equals(normalized)
                &&
                !"CANCELLED".equals(normalized)) {

            throw new DatabaseException(
                    "Invalid appointment status: "
                            + status
            );
        }
    }

    // =========================================================
    // TIMESTAMP
    // =========================================================

    private String getCurrentTimestamp() {

        return LocalDateTime.now()
                .format(
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME
                );
    }

    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safe(
            String value
    ) {

        return value == null
                ? ""
                : value;
    }
}