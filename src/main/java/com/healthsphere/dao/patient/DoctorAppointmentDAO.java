package com.healthsphere.dao.patient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.DoctorAppointment;

public class DoctorAppointmentDAO {

    private static final String COLLECTION =
            "doctorAppointments";

    private final Firestore db;

    public DoctorAppointmentDAO() {

        this.db =
                FirebaseConfig.getFirestore();
    }

    // ============================================================
    // CREATE DOCTOR APPOINTMENT
    // ============================================================

    public DoctorAppointment createAppointment(
            DoctorAppointment appointment) {

        try {

            String appointmentId =
                    appointment.getAppointmentId();

            if (appointmentId == null
                    || appointmentId.isBlank()) {

                appointmentId =
                        UUID.randomUUID()
                                .toString();

                appointment.setAppointmentId(
                        appointmentId
                );
            }

            db.collection(COLLECTION)
                    .document(appointmentId)
                    .set(appointment)
                    .get();

            System.out.println(
                    "Doctor appointment created successfully."
            );

            return appointment;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to create doctor appointment.",
                    e
            );
        }
    }

    // ============================================================
    // GET CURRENT PATIENT DOCTOR APPOINTMENTS
    // ============================================================

    public List<DoctorAppointment>
            getPatientAppointments(
                    String patientUid) {

        try {

            ApiFuture<QuerySnapshot> future =
                    db.collection(COLLECTION)
                            .whereEqualTo(
                                    "patientUid",
                                    patientUid
                            )
                            .get();

            QuerySnapshot snapshot =
                    future.get();

            List<DoctorAppointment>
                    appointments =
                    new ArrayList<>();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                DoctorAppointment appointment =
                        document.toObject(
                                DoctorAppointment.class
                        );

                if (appointment != null) {

                    appointments.add(
                            appointment
                    );
                }
            }

            return appointments;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve doctor appointments.",
                    e
            );
        }
    }

    // ============================================================
    // GET SINGLE DOCTOR APPOINTMENT
    // ============================================================

    public DoctorAppointment getAppointment(
            String appointmentId) {

        try {

            DocumentSnapshot document =
                    db.collection(COLLECTION)
                            .document(appointmentId)
                            .get()
                            .get();

            if (!document.exists()) {

                throw new DatabaseException(
                        "Doctor appointment not found."
                );
            }

            return document.toObject(
                    DoctorAppointment.class
            );

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve doctor appointment.",
                    e
            );
        }
    }

    // ============================================================
    // UPDATE DOCTOR APPOINTMENT
    // ============================================================

    public void updateAppointment(
            DoctorAppointment appointment) {

        try {

            if (appointment == null
                    || appointment.getAppointmentId() == null
                    || appointment.getAppointmentId().isBlank()) {

                throw new IllegalArgumentException(
                        "Doctor appointment ID is required."
                );
            }

            db.collection(COLLECTION)
                    .document(
                            appointment.getAppointmentId()
                    )
                    .set(appointment)
                    .get();

            System.out.println(
                    "Doctor appointment updated successfully."
            );

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to update doctor appointment.",
                    e
            );
        }
    }
}