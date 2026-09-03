package com.healthsphere.controller.patient;

import java.util.List;

import com.healthsphere.dao.patient.PrescriptionDAO;
import com.healthsphere.model.Prescription;
import com.healthsphere.util.SessionManager;

public class PrescriptionController {

    private final PrescriptionDAO prescriptionDAO;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PrescriptionController() {

        this.prescriptionDAO =
                new PrescriptionDAO();
    }

    // =========================================================
    // GET CURRENT PATIENT PRESCRIPTIONS
    // =========================================================

    /**
     * Get all prescriptions belonging to the
     * currently logged-in patient.
     */
    public List<Prescription>
    getCurrentPatientPrescriptions()
            throws Exception {

        String patientUid =
                getCurrentPatientUid();

        return prescriptionDAO
                .getPrescriptionsByPatientUid(
                        patientUid
                );
    }

    // =========================================================
    // GET PRESCRIPTION BY ID
    // =========================================================

    /**
     * Get a prescription by ID.
     *
     * Security check ensures that the current patient
     * can only access their own prescription.
     */
    public Prescription getPrescriptionById(
            String prescriptionId) throws Exception {

        Prescription prescription =
                prescriptionDAO
                        .getPrescriptionById(
                                prescriptionId
                        );

        if (prescription == null) {
            return null;
        }

        String currentPatientUid =
                getCurrentPatientUid();

        if (!currentPatientUid.equals(
                prescription.getPatientUid()
        )) {

            throw new SecurityException(
                    "You are not authorized to access this prescription."
            );
        }

        return prescription;
    }

    // =========================================================
    // DELETE CURRENT PATIENT PRESCRIPTION
    // =========================================================

    /**
     * Delete a prescription belonging to the
     * currently logged-in patient.
     *
     * Security check is performed before deletion.
     */
    public void deleteCurrentPatientPrescription(
            String prescriptionId) throws Exception {

        if (prescriptionId == null
                || prescriptionId.isBlank()) {

            throw new IllegalArgumentException(
                    "Prescription ID is missing."
            );
        }

        // -----------------------------------------------------
        // GET PRESCRIPTION
        // -----------------------------------------------------

        Prescription prescription =
                prescriptionDAO
                        .getPrescriptionById(
                                prescriptionId
                        );

        if (prescription == null) {

            throw new IllegalArgumentException(
                    "Prescription not found."
            );
        }

        // -----------------------------------------------------
        // GET CURRENT PATIENT UID
        // -----------------------------------------------------

        String currentPatientUid =
                getCurrentPatientUid();

        // -----------------------------------------------------
        // SECURITY CHECK
        // -----------------------------------------------------

        if (!currentPatientUid.equals(
                prescription.getPatientUid()
        )) {

            throw new SecurityException(
                    "You are not authorized to delete this prescription."
            );
        }

        // -----------------------------------------------------
        // DELETE
        // -----------------------------------------------------

        prescriptionDAO.deletePrescription(
                prescriptionId
        );
    }

    // =========================================================
    // GET CURRENT PATIENT UID
    // =========================================================

    /**
     * Get UID of currently logged-in patient.
     */
    private String getCurrentPatientUid() {

        if (!SessionManager.isLoggedIn()) {

            throw new IllegalStateException(
                    "No active patient session."
            );
        }

        if (SessionManager.getCurrentUser() == null) {

            throw new IllegalStateException(
                    "No active user session."
            );
        }

        String uid =
                SessionManager
                        .getCurrentUser()
                        .getUid();

        if (uid == null || uid.isBlank()) {

            throw new IllegalStateException(
                    "Patient UID is missing."
            );
        }

        return uid;
    }
}