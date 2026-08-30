package com.healthsphere.dao.patient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.Appointment;

public class AppointmentDAO {

    private final Firestore db;

    public AppointmentDAO() {
        this.db = FirebaseConfig.getFirestore();
    }

    // =========================================================
    // CREATE APPOINTMENT
    // =========================================================

    public Appointment createAppointment(
            Appointment appointment) {

        try {

            String appointmentId =
                    appointment.getAppointmentId();

            if (appointmentId == null ||
                    appointmentId.isBlank()) {

                appointmentId =
                        UUID.randomUUID().toString();

                appointment.setAppointmentId(
                        appointmentId
                );
            }

            String currentTime =
                    LocalDateTime.now().toString();

            appointment.setCreatedAt(
                    currentTime
            );

            appointment.setUpdatedAt(
                    currentTime
            );

            db.collection("appointments")
                    .document(appointmentId)
                    .set(appointment)
                    .get();

            System.out.println(
                    "Appointment created successfully."
            );

            return appointment;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to create appointment.",
                    e
            );
        }
    }

    // =========================================================
    // GET PATIENT APPOINTMENTS
    // =========================================================

    public List<Appointment> getPatientAppointments(
            String patientUid) {

        try {

            ApiFuture<QuerySnapshot> future =
                    db.collection("appointments")
                            .whereEqualTo(
                                    "patientUid",
                                    patientUid
                            )
                            .get();

            QuerySnapshot snapshot =
                    future.get();

            List<Appointment> appointments =
                    new ArrayList<>();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                Appointment appointment =
                        document.toObject(
                                Appointment.class
                        );

                if (appointment != null) {

                    // Ensure ID is available even if
                    // old documents don't contain it.
                    if (appointment.getAppointmentId() == null ||
                            appointment.getAppointmentId().isBlank()) {

                        appointment.setAppointmentId(
                                document.getId()
                        );
                    }

                    appointments.add(
                            appointment
                    );
                }
            }

            return appointments;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve patient appointments.",
                    e
            );
        }
    }

    // =========================================================
    // GET SINGLE APPOINTMENT
    // =========================================================

    public Appointment getAppointment(
            String appointmentId) {

        try {

            DocumentSnapshot document =
                    db.collection("appointments")
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

            if (appointment != null &&
                    (appointment.getAppointmentId() == null ||
                            appointment.getAppointmentId().isBlank())) {

                appointment.setAppointmentId(
                        document.getId()
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
    // UPDATE APPOINTMENT
    // =========================================================

    public void updateAppointment(
            Appointment appointment) {

        try {

            if (appointment == null ||
                    appointment.getAppointmentId() == null ||
                    appointment.getAppointmentId().isBlank()) {

                throw new IllegalArgumentException(
                        "Appointment ID is required."
                );
            }

            appointment.setUpdatedAt(
                    LocalDateTime.now().toString()
            );

            db.collection("appointments")
                    .document(
                            appointment.getAppointmentId()
                    )
                    .set(appointment)
                    .get();

            System.out.println(
                    "Appointment updated successfully."
            );

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to update appointment.",
                    e
            );
        }
    }
}