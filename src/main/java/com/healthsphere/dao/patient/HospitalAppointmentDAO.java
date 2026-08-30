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
import com.healthsphere.model.HospitalAppointment;

public class HospitalAppointmentDAO {

    private static final String COLLECTION =
            "hospitalAppointments";

    private final Firestore db;

    public HospitalAppointmentDAO() {

        this.db =
                FirebaseConfig.getFirestore();
    }


    // ============================================================
    // CREATE HOSPITAL APPOINTMENT
    // ============================================================

    public HospitalAppointment createAppointment(
            HospitalAppointment appointment) {

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
                    "Hospital appointment created successfully."
            );

            return appointment;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to create hospital appointment.",
                    e
            );
        }
    }


    // ============================================================
    // GET CURRENT PATIENT HOSPITAL APPOINTMENTS
    // ============================================================

    public List<HospitalAppointment>
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

            List<HospitalAppointment>
                    appointments =
                    new ArrayList<>();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                HospitalAppointment appointment =
                        document.toObject(
                                HospitalAppointment.class
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
                    "Unable to retrieve hospital appointments.",
                    e
            );
        }
    }


    // ============================================================
    // GET SINGLE HOSPITAL APPOINTMENT
    // ============================================================

    public HospitalAppointment getAppointment(
            String appointmentId) {

        try {

            DocumentSnapshot document =
                    db.collection(COLLECTION)
                            .document(appointmentId)
                            .get()
                            .get();

            if (!document.exists()) {

                throw new DatabaseException(
                        "Hospital appointment not found."
                );
            }

            return document.toObject(
                    HospitalAppointment.class
            );

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve hospital appointment.",
                    e
            );
        }
    }


    // ============================================================
    // UPDATE HOSPITAL APPOINTMENT
    // ============================================================

    public void updateAppointment(
            HospitalAppointment appointment) {

        try {

            if (appointment == null
                    || appointment.getAppointmentId() == null
                    || appointment.getAppointmentId().isBlank()) {

                throw new IllegalArgumentException(
                        "Hospital appointment ID is required."
                );
            }

            db.collection(COLLECTION)
                    .document(
                            appointment.getAppointmentId()
                    )
                    .set(appointment)
                    .get();

            System.out.println(
                    "Hospital appointment updated successfully."
            );

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to update hospital appointment.",
                    e
            );
        }
    }
}