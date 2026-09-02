package com.healthsphere.controller.doctor;

import com.healthsphere.dao.appointment.AppointmentDAO;
import com.healthsphere.dao.doctor.PrescriptionDAO;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.Appointment;
import com.healthsphere.model.Prescription;

import java.time.Instant;
import java.util.List;

/**
 * Controller for Doctor Prescription operations.
 *
 * Architecture:
 *
 * View
 *   ↓
 * PrescriptionController
 *   ↓
 * PrescriptionDAO
 *   ↓
 * Firestore
 *
 * Doctor can:
 * - Create prescriptions
 * - View prescriptions
 * - Update prescriptions
 * - Delete prescriptions
 *
 * Security:
 * A doctor can access a patient's prescriptions only when
 * that patient has an appointment relationship with the doctor.
 */
public class PrescriptionController {

    private final PrescriptionDAO prescriptionDAO;
    private final AppointmentDAO appointmentDAO;


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public PrescriptionController() {

        this.prescriptionDAO =
                new PrescriptionDAO();

        this.appointmentDAO =
                new AppointmentDAO();
    }


    // ============================================================
    // CREATE PRESCRIPTION
    // ============================================================

    /**
     * Creates a prescription for a patient.
     *
     * @param prescription prescription data
     * @return generated prescription ID
     */
    public String createPrescription(
            Prescription prescription) {

        validatePrescription(
                prescription
        );

        String doctorUid =
                prescription.getDoctorUid();

        String patientUid =
                prescription.getPatientUid();


        // --------------------------------------------------------
        // Verify doctor-patient relationship
        // --------------------------------------------------------

        verifyDoctorPatientRelationship(
                doctorUid,
                patientUid
        );


        // --------------------------------------------------------
        // Verify appointment if supplied
        // --------------------------------------------------------

        if (prescription.getAppointmentId() != null
                && !prescription.getAppointmentId()
                .trim()
                .isEmpty()) {

            verifyAppointmentRelationship(
                    prescription.getAppointmentId(),
                    doctorUid,
                    patientUid
            );
        }


        // --------------------------------------------------------
        // Set timestamps
        // --------------------------------------------------------

        String now =
                Instant.now().toString();


        if (prescription.getCreatedAt() == null
                || prescription.getCreatedAt()
                .trim()
                .isEmpty()) {

            prescription.setCreatedAt(
                    now
            );
        }


        prescription.setUpdatedAt(
                now
        );


        // --------------------------------------------------------
        // Default status
        // --------------------------------------------------------

        if (prescription.getStatus() == null
                || prescription.getStatus()
                .trim()
                .isEmpty()) {

            prescription.setStatus(
                    "ACTIVE"
            );
        }


        // --------------------------------------------------------
        // Save
        // --------------------------------------------------------

        try {

            return prescriptionDAO
                    .createPrescription(
                            prescription
                    );

        } catch (InterruptedException e) {

            Thread.currentThread()
                    .interrupt();

            throw new RuntimeException(
                    "Prescription creation was interrupted.",
                    e
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to create prescription.",
                    e
            );
        }
    }


    // ============================================================
    // GET PRESCRIPTION
    // ============================================================

    /**
     * Gets one prescription using its ID.
     */
    public Prescription getPrescription(
            String prescriptionId) {

        validateId(
                prescriptionId,
                "Prescription ID"
        );

        try {

            return prescriptionDAO
                    .getPrescription(
                            prescriptionId
                    );

        } catch (InterruptedException e) {

            Thread.currentThread()
                    .interrupt();

            throw new RuntimeException(
                    "Prescription retrieval was interrupted.",
                    e
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to retrieve prescription.",
                    e
            );
        }
    }


    // ============================================================
    // GET PATIENT PRESCRIPTIONS
    // ============================================================

    /**
     * Gets all prescriptions belonging to a patient.
     *
     * This method does not perform doctor authorization because
     * it is a general patient-level operation.
     */
    public List<Prescription> getPatientPrescriptions(
            String patientUid) {

        validateId(
                patientUid,
                "Patient UID"
        );

        try {

            return prescriptionDAO
                    .getPatientPrescriptions(
                            patientUid
                    );

        } catch (InterruptedException e) {

            Thread.currentThread()
                    .interrupt();

            throw new RuntimeException(
                    "Patient prescription retrieval was interrupted.",
                    e
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to retrieve patient prescriptions.",
                    e
            );
        }
    }


    // ============================================================
    // GET PATIENT PRESCRIPTIONS FOR DOCTOR
    // ============================================================

    /**
     * Gets prescriptions of a patient for a doctor.
     *
     * The doctor must have an appointment relationship
     * with the patient.
     */
    public List<Prescription>
    getPatientPrescriptionsForDoctor(
            String doctorUid,
            String patientUid) {

        validateId(
                doctorUid,
                "Doctor UID"
        );

        validateId(
                patientUid,
                "Patient UID"
        );


        // --------------------------------------------------------
        // Security check
        // --------------------------------------------------------

        verifyDoctorPatientRelationship(
                doctorUid,
                patientUid
        );


        try {

            return prescriptionDAO
                    .getPatientPrescriptions(
                            patientUid
                    );

        } catch (InterruptedException e) {

            Thread.currentThread()
                    .interrupt();

            throw new RuntimeException(
                    "Patient prescription retrieval was interrupted.",
                    e
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to retrieve patient prescriptions.",
                    e
            );
        }
    }


    // ============================================================
    // SAFE GET PATIENT PRESCRIPTIONS FOR DOCTOR
    // ============================================================

    /**
     * Safe method for JavaFX UI.
     *
     * If something goes wrong, an empty list is returned instead
     * of crashing the JavaFX screen.
     */
    public List<Prescription>
    getPatientPrescriptionsForDoctorSafe(
            String doctorUid,
            String patientUid) {

        try {

            return getPatientPrescriptionsForDoctor(
                    doctorUid,
                    patientUid
            );

        } catch (Exception e) {

            System.err.println(
                    "Unable to load patient prescriptions: "
                            + e.getMessage()
            );

            return List.of();
        }
    }


    // ============================================================
    // GET DOCTOR PRESCRIPTIONS
    // ============================================================

    /**
     * Gets all prescriptions created by a doctor.
     */
    public List<Prescription> getDoctorPrescriptions(
            String doctorUid) {

        validateId(
                doctorUid,
                "Doctor UID"
        );

        try {

            return prescriptionDAO
                    .getDoctorPrescriptions(
                            doctorUid
                    );

        } catch (InterruptedException e) {

            Thread.currentThread()
                    .interrupt();

            throw new RuntimeException(
                    "Doctor prescription retrieval was interrupted.",
                    e
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to retrieve doctor prescriptions.",
                    e
            );
        }
    }


    // ============================================================
    // GET APPOINTMENT PRESCRIPTIONS
    // ============================================================

    /**
     * Gets prescriptions belonging to an appointment.
     */
    public List<Prescription>
    getAppointmentPrescriptions(
            String appointmentId) {

        validateId(
                appointmentId,
                "Appointment ID"
        );

        try {

            return prescriptionDAO
                    .getAppointmentPrescriptions(
                            appointmentId
                    );

        } catch (InterruptedException e) {

            Thread.currentThread()
                    .interrupt();

            throw new RuntimeException(
                    "Appointment prescription retrieval was interrupted.",
                    e
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to retrieve appointment prescriptions.",
                    e
            );
        }
    }


    // ============================================================
    // UPDATE PRESCRIPTION
    // ============================================================

    /**
     * Updates an existing prescription.
     *
     * PrescriptionDAO.updatePrescription() returns void,
     * therefore this method also returns void.
     */
    public void updatePrescription(
            Prescription prescription) {

        if (prescription == null) {

            throw new IllegalArgumentException(
                    "Prescription cannot be null."
            );
        }


        validateId(
                prescription.getPrescriptionId(),
                "Prescription ID"
        );


        validatePrescription(
                prescription
        );


        String doctorUid =
                prescription.getDoctorUid();

        String patientUid =
                prescription.getPatientUid();


        // --------------------------------------------------------
        // Security
        // --------------------------------------------------------

        verifyDoctorPatientRelationship(
                doctorUid,
                patientUid
        );


        // --------------------------------------------------------
        // Appointment validation
        // --------------------------------------------------------

        if (prescription.getAppointmentId() != null
                && !prescription.getAppointmentId()
                .trim()
                .isEmpty()) {

            verifyAppointmentRelationship(
                    prescription.getAppointmentId(),
                    doctorUid,
                    patientUid
            );
        }


        // --------------------------------------------------------
        // Update timestamp
        // --------------------------------------------------------

        prescription.setUpdatedAt(
                Instant.now().toString()
        );


        // --------------------------------------------------------
        // Update Firestore
        // --------------------------------------------------------

        try {

            prescriptionDAO
                    .updatePrescription(
                            prescription
                    );

        } catch (InterruptedException e) {

            Thread.currentThread()
                    .interrupt();

            throw new RuntimeException(
                    "Prescription update was interrupted.",
                    e
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to update prescription.",
                    e
            );
        }
    }


    // ============================================================
    // DELETE PRESCRIPTION
    // ============================================================

    /**
     * Deletes a prescription.
     */
    public void deletePrescription(
            String prescriptionId,
            String doctorUid) {

        validateId(
                prescriptionId,
                "Prescription ID"
        );

        validateId(
                doctorUid,
                "Doctor UID"
        );


        try {

            Prescription prescription =
                    prescriptionDAO
                            .getPrescription(
                                    prescriptionId
                            );


            if (prescription == null) {

                throw new IllegalArgumentException(
                        "Prescription not found."
                );
            }


            // ----------------------------------------------------
            // Verify doctor owns relationship with patient
            // ----------------------------------------------------

            verifyDoctorPatientRelationship(
                    doctorUid,
                    prescription.getPatientUid()
            );


            prescriptionDAO
                    .deletePrescription(
                            prescriptionId
                    );

        } catch (InterruptedException e) {

            Thread.currentThread()
                    .interrupt();

            throw new RuntimeException(
                    "Prescription deletion was interrupted.",
                    e
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to delete prescription.",
                    e
            );
        }
    }


    // ============================================================
    // VERIFY DOCTOR-PATIENT RELATIONSHIP
    // ============================================================

    /**
     * Verifies that the patient has an appointment with
     * the specified doctor.
     *
     * AppointmentDAO already handles Firebase exceptions and
     * converts them into DatabaseException.
     */
    private void verifyDoctorPatientRelationship(
            String doctorUid,
            String patientUid) {

        validateId(
                doctorUid,
                "Doctor UID"
        );

        validateId(
                patientUid,
                "Patient UID"
        );


        try {

            List<String> patientUids =
                    appointmentDAO
                            .getPatientUidsForDoctor(
                                    doctorUid
                            );


            if (patientUids == null
                    || !patientUids.contains(
                            patientUid
                    )) {

                throw new SecurityException(
                        "Doctor is not authorized "
                                + "to access this patient's "
                                + "prescriptions."
                );
            }

        } catch (SecurityException e) {

            throw e;

        } catch (DatabaseException e) {

            throw new RuntimeException(
                    "Unable to verify doctor-patient relationship.",
                    e
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to verify doctor-patient relationship.",
                    e
            );
        }
    }


    // ============================================================
    // VERIFY APPOINTMENT RELATIONSHIP
    // ============================================================

    /**
     * Verifies that the appointment belongs to the same
     * doctor and patient.
     */
    private void verifyAppointmentRelationship(
            String appointmentId,
            String doctorUid,
            String patientUid) {

        validateId(
                appointmentId,
                "Appointment ID"
        );

        try {

            Appointment appointment =
                    appointmentDAO
                            .getAppointment(
                                    appointmentId
                            );


            if (appointment == null) {

                throw new IllegalArgumentException(
                        "Appointment not found."
                );
            }


            // ----------------------------------------------------
            // Doctor check
            // ----------------------------------------------------

            if (!doctorUid.equals(
                    appointment.getDoctorUid()
            )) {

                throw new SecurityException(
                        "Appointment does not belong "
                                + "to this doctor."
                );
            }


            // ----------------------------------------------------
            // Patient check
            // ----------------------------------------------------

            if (!patientUid.equals(
                    appointment.getPatientUid()
            )) {

                throw new SecurityException(
                        "Appointment does not belong "
                                + "to this patient."
                );
            }

        } catch (SecurityException e) {

            throw e;

        } catch (DatabaseException e) {

            throw new RuntimeException(
                    "Unable to verify appointment relationship.",
                    e
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to verify appointment relationship.",
                    e
            );
        }
    }


    // ============================================================
    // VALIDATE PRESCRIPTION
    // ============================================================

    private void validatePrescription(
            Prescription prescription) {

        if (prescription == null) {

            throw new IllegalArgumentException(
                    "Prescription cannot be null."
            );
        }


        // --------------------------------------------------------
        // Patient UID
        // --------------------------------------------------------

        validateId(
                prescription.getPatientUid(),
                "Patient UID"
        );


        // --------------------------------------------------------
        // Doctor UID
        // --------------------------------------------------------

        validateId(
                prescription.getDoctorUid(),
                "Doctor UID"
        );


        // --------------------------------------------------------
        // Medication
        // --------------------------------------------------------

        if (prescription.getMedication() == null
                || prescription.getMedication()
                .trim()
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "Medication cannot be empty."
            );
        }


        // --------------------------------------------------------
        // Strength
        // --------------------------------------------------------

        if (prescription.getStrength() == null
                || prescription.getStrength()
                .trim()
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "Medication strength cannot be empty."
            );
        }


        // --------------------------------------------------------
        // Dosage
        // --------------------------------------------------------

        if (prescription.getDosage() == null
                || prescription.getDosage()
                .trim()
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "Dosage cannot be empty."
            );
        }


        // --------------------------------------------------------
        // Frequency
        // --------------------------------------------------------

        if (prescription.getFrequency() == null
                || prescription.getFrequency()
                .trim()
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "Frequency cannot be empty."
            );
        }


        // --------------------------------------------------------
        // Duration
        // --------------------------------------------------------

        if (prescription.getDuration() == null
                || prescription.getDuration()
                .trim()
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "Duration cannot be empty."
            );
        }
    }


    // ============================================================
    // VALIDATE ID
    // ============================================================

    private void validateId(
            String id,
            String fieldName) {

        if (id == null
                || id.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    fieldName
                            + " cannot be empty."
            );
        }
    }
}  