package com.healthsphere.dao.appointment;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;

import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.Appointment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AppointmentDAO {

    private final Firestore db;

    public AppointmentDAO() {
        this.db = FirebaseConfig.getFirestore();
    }

    // ============================================================
    // CREATE APPOINTMENT
    // ============================================================

    public Appointment createAppointment(
            Appointment appointment) {

        try {

            if (appointment.getAppointmentId() == null ||
                    appointment.getAppointmentId().trim().isEmpty()) {

                appointment.setAppointmentId(
                        UUID.randomUUID().toString()
                );
            }

            DocumentReference document =
                    db.collection("appointments")
                            .document(
                                    appointment.getAppointmentId()
                            );

            document.set(appointment).get();

            System.out.println(
                    "Appointment created successfully: "
                            + appointment.getAppointmentId()
            );

            return appointment;

        } catch (Exception e) {

            throw new DatabaseException(
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

            return document.toObject(
                    Appointment.class
            );

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
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

        try {

            Query query =
                    db.collection("appointments")
                            .whereEqualTo(
                                    "doctorUid",
                                    doctorUid
                            );

            ApiFuture<QuerySnapshot> future =
                    query.get();

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
    // GET PATIENT APPOINTMENTS
    // ============================================================

    public List<Appointment> getPatientAppointments(
            String patientUid) {

        try {

            Query query =
                    db.collection("appointments")
                            .whereEqualTo(
                                    "patientUid",
                                    patientUid
                            );

            QuerySnapshot snapshot =
                    query.get()
                            .get();

            List<Appointment> appointments =
                    new ArrayList<>();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                Appointment appointment =
                        document.toObject(
                                Appointment.class
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

        try {

            Query query =
                    db.collection("appointments")
                            .whereEqualTo(
                                    "hospitalId",
                                    hospitalId
                            );

            QuerySnapshot snapshot =
                    query.get()
                            .get();

            List<Appointment> appointments =
                    new ArrayList<>();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                Appointment appointment =
                        document.toObject(
                                Appointment.class
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
    // UPDATE APPOINTMENT
    // ============================================================

    public void updateAppointment(
            Appointment appointment) {

        try {

            if (appointment.getAppointmentId() == null ||
                    appointment.getAppointmentId().trim().isEmpty()) {

                throw new DatabaseException(
                        "Appointment ID cannot be empty."
                );
            }

            db.collection("appointments")
                    .document(
                            appointment.getAppointmentId()
                    )
                    .set(appointment)
                    .get();

            System.out.println(
                    "Appointment updated successfully."
            );

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
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

        try {

            db.collection("appointments")
                    .document(appointmentId)
                    .update(
                            "status",
                            status
                    )
                    .get();

            System.out.println(
                    "Appointment status updated successfully."
            );

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to update appointment status.",
                    e
            );
        }
    }

    // ============================================================
// GET PATIENT UIDS FOR DOCTOR
// ============================================================

public java.util.List<String> getPatientUidsForDoctor(
        String doctorUid) {

    try {

        if (doctorUid == null || doctorUid.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Doctor UID cannot be empty."
            );
        }

        java.util.List<String> patientUids =
                new java.util.ArrayList<>();

        var documents =
                db.collection("appointments")
                        .whereEqualTo("doctorUid", doctorUid)
                        .get()
                        .get()
                        .getDocuments();

        for (DocumentSnapshot document : documents) {

            String patientUid =
                    document.getString("patientUid");

            if (patientUid != null
                    && !patientUid.trim().isEmpty()
                    && !patientUids.contains(patientUid)) {

                patientUids.add(patientUid);
            }
        }

        return patientUids;

    } catch (IllegalArgumentException e) {

        throw e;

    } catch (Exception e) {

        throw new DatabaseException(
                "Unable to retrieve patients for doctor.",
                e
        );
    }
}
}