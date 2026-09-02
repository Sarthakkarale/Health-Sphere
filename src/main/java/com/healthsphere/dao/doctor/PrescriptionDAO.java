package com.healthsphere.dao.doctor;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.model.Prescription;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class PrescriptionDAO {

    private static final String COLLECTION_NAME =
            "prescriptions";

    private final Firestore firestore;

    public PrescriptionDAO() {
        this.firestore = FirebaseConfig.getFirestore();
    }

    /**
     * Create a new prescription.
     *
     * @return generated prescription ID
     */
    public String createPrescription(
            Prescription prescription)
            throws ExecutionException, InterruptedException {

        if (prescription == null) {
            throw new IllegalArgumentException(
                    "Prescription cannot be null."
            );
        }

        String prescriptionId =
                prescription.getPrescriptionId();

        if (prescriptionId == null
                || prescriptionId.trim().isEmpty()) {

            prescriptionId =
                    UUID.randomUUID().toString();

            prescription.setPrescriptionId(
                    prescriptionId
            );
        }

        DocumentReference documentReference =
                firestore.collection(COLLECTION_NAME)
                        .document(prescriptionId);

        documentReference.set(prescription).get();

        return prescriptionId;
    }

    /**
     * Get a prescription by ID.
     */
    public Prescription getPrescription(
            String prescriptionId)
            throws ExecutionException, InterruptedException {

        if (isEmpty(prescriptionId)) {
            return null;
        }

        DocumentSnapshot document =
                firestore.collection(COLLECTION_NAME)
                        .document(prescriptionId)
                        .get()
                        .get();

        if (!document.exists()) {
            return null;
        }

        return document.toObject(Prescription.class);
    }

    /**
     * Get all prescriptions for a patient.
     */
    public List<Prescription> getPatientPrescriptions(
            String patientUid)
            throws ExecutionException, InterruptedException {

        List<Prescription> prescriptions =
                new ArrayList<>();

        if (isEmpty(patientUid)) {
            return prescriptions;
        }

        QuerySnapshot snapshot =
                firestore.collection(COLLECTION_NAME)
                        .whereEqualTo(
                                "patientUid",
                                patientUid
                        )
                        .get()
                        .get();

        for (DocumentSnapshot document :
                snapshot.getDocuments()) {

            Prescription prescription =
                    document.toObject(
                            Prescription.class
                    );

            if (prescription != null) {
                prescriptions.add(prescription);
            }
        }

        return prescriptions;
    }

    /**
     * Get all prescriptions created by a doctor.
     */
    public List<Prescription> getDoctorPrescriptions(
            String doctorUid)
            throws ExecutionException, InterruptedException {

        List<Prescription> prescriptions =
                new ArrayList<>();

        if (isEmpty(doctorUid)) {
            return prescriptions;
        }

        QuerySnapshot snapshot =
                firestore.collection(COLLECTION_NAME)
                        .whereEqualTo(
                                "doctorUid",
                                doctorUid
                        )
                        .get()
                        .get();

        for (DocumentSnapshot document :
                snapshot.getDocuments()) {

            Prescription prescription =
                    document.toObject(
                            Prescription.class
                    );

            if (prescription != null) {
                prescriptions.add(prescription);
            }
        }

        return prescriptions;
    }

    /**
     * Get prescriptions associated with an appointment.
     */
    public List<Prescription> getAppointmentPrescriptions(
            String appointmentId)
            throws ExecutionException, InterruptedException {

        List<Prescription> prescriptions =
                new ArrayList<>();

        if (isEmpty(appointmentId)) {
            return prescriptions;
        }

        QuerySnapshot snapshot =
                firestore.collection(COLLECTION_NAME)
                        .whereEqualTo(
                                "appointmentId",
                                appointmentId
                        )
                        .get()
                        .get();

        for (DocumentSnapshot document :
                snapshot.getDocuments()) {

            Prescription prescription =
                    document.toObject(
                            Prescription.class
                    );

            if (prescription != null) {
                prescriptions.add(prescription);
            }
        }

        return prescriptions;
    }

    /**
     * Update an existing prescription.
     */
    public void updatePrescription(
            Prescription prescription)
            throws ExecutionException, InterruptedException {

        if (prescription == null) {
            throw new IllegalArgumentException(
                    "Prescription cannot be null."
            );
        }

        if (isEmpty(
                prescription.getPrescriptionId())) {

            throw new IllegalArgumentException(
                    "Prescription ID is required."
            );
        }

        DocumentReference documentReference =
                firestore.collection(COLLECTION_NAME)
                        .document(
                                prescription.getPrescriptionId()
                        );

        DocumentSnapshot existing =
                documentReference.get().get();

        if (!existing.exists()) {

            throw new IllegalArgumentException(
                    "Prescription does not exist: "
                            + prescription.getPrescriptionId()
            );
        }

        documentReference.set(prescription).get();
    }

    /**
     * Delete a prescription.
     */
    public void deletePrescription(
            String prescriptionId)
            throws ExecutionException, InterruptedException {

        if (isEmpty(prescriptionId)) {
            throw new IllegalArgumentException(
                    "Prescription ID cannot be empty."
            );
        }

        firestore.collection(COLLECTION_NAME)
                .document(prescriptionId)
                .delete()
                .get();
    }

    private boolean isEmpty(String value) {
        return value == null
                || value.trim().isEmpty();
    }
}