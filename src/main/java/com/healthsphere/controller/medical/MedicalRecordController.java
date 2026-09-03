package com.healthsphere.controller.medical;

import com.healthsphere.dao.appointment.AppointmentDAO;
import com.healthsphere.dao.medical.MedicalRecordDAO;
import com.healthsphere.model.MedicalRecord;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for doctor/patient medical records.
 *
 * Architecture:
 *
 * JavaFX View
 *      ↓
 * MedicalRecordController
 *      ↓
 * MedicalRecordDAO
 *      ↓
 * Firebase Firestore
 *
 * The controller also verifies that a doctor has an
 * appointment relationship with a patient before allowing
 * doctor-side access to that patient's medical records.
 */
public class MedicalRecordController {

    private final MedicalRecordDAO medicalRecordDAO;
    private final AppointmentDAO appointmentDAO;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public MedicalRecordController() {

        this.medicalRecordDAO =
                new MedicalRecordDAO();

        this.appointmentDAO =
                new AppointmentDAO();
    }

    // ============================================================
    // CREATE
    // ============================================================

    public String createMedicalRecord(
            MedicalRecord medicalRecord) {

        validateRecord(
                medicalRecord
        );

        if (isEmpty(
                medicalRecord.getCreatedAt()
        )) {

            medicalRecord.setCreatedAt(
                    Instant.now().toString()
            );
        }

        medicalRecord.setUpdatedAt(
                Instant.now().toString()
        );

        try {

            return medicalRecordDAO
                    .createMedicalRecord(
                            medicalRecord
                    );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to create medical record.",
                    e
            );
        }
    }

    // ============================================================
    // GET ONE RECORD
    // ============================================================

    public MedicalRecord getMedicalRecord(
            String recordId) {

        validateId(
                recordId,
                "Medical record ID"
        );

        try {

            return medicalRecordDAO
                    .getMedicalRecord(
                            recordId
                    );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to retrieve medical record.",
                    e
            );
        }
    }

    // ============================================================
    // GET ALL PATIENT RECORDS
    // ============================================================

    public List<MedicalRecord>
    getPatientMedicalRecords(
            String patientUid) {

        validateId(
                patientUid,
                "Patient UID"
        );

        try {

            List<MedicalRecord> records =
                    medicalRecordDAO
                            .getPatientMedicalRecords(
                                    patientUid
                            );

            return records == null
                    ? new ArrayList<>()
                    : records;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to retrieve patient medical records.",
                    e
            );
        }
    }

    // ============================================================
    // GET PATIENT RECORDS FOR DOCTOR
    // ============================================================

    /**
     * Returns medical records for a patient only when the
     * doctor has an appointment relationship with that patient.
     */
    public List<MedicalRecord>
    getPatientMedicalRecordsForDoctor(
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

        verifyDoctorPatientRelationship(
                doctorUid,
                patientUid
        );

        try {

            return medicalRecordDAO
                    .getPatientMedicalRecords(
                            patientUid
                    );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to retrieve patient medical records.",
                    e
            );
        }
    }

    // ============================================================
    // SAFE VERSION FOR JAVAFX
    // ============================================================

    /**
     * Safe version used by JavaFX screens.
     *
     * Returns an empty list when records cannot be loaded.
     */
    public List<MedicalRecord>
    getPatientMedicalRecordsForDoctorSafe(
            String doctorUid,
            String patientUid) {

        try {

            return getPatientMedicalRecordsForDoctor(
                    doctorUid,
                    patientUid
            );

        } catch (Exception e) {

            e.printStackTrace();

            return new ArrayList<>();
        }
    }

    // ============================================================
    // GET DOCTOR RECORDS
    // ============================================================

    public List<MedicalRecord>
    getDoctorMedicalRecords(
            String doctorUid) {

        validateId(
                doctorUid,
                "Doctor UID"
        );

        try {

            List<MedicalRecord> records =
                    medicalRecordDAO
                            .getDoctorMedicalRecords(
                                    doctorUid
                            );

            return records == null
                    ? new ArrayList<>()
                    : records;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to retrieve doctor medical records.",
                    e
            );
        }
    }

    // ============================================================
    // GET APPOINTMENT RECORDS
    // ============================================================

    public List<MedicalRecord>
    getAppointmentMedicalRecords(
            String appointmentId) {

        validateId(
                appointmentId,
                "Appointment ID"
        );

        try {

            List<MedicalRecord> records =
                    medicalRecordDAO
                            .getAppointmentMedicalRecords(
                                    appointmentId
                            );

            return records == null
                    ? new ArrayList<>()
                    : records;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to retrieve appointment medical records.",
                    e
            );
        }
    }

    // ============================================================
    // UPDATE
    // ============================================================

    public void updateMedicalRecord(
            MedicalRecord medicalRecord) {

        validateRecord(
                medicalRecord
        );

        validateId(
                medicalRecord.getRecordId(),
                "Medical record ID"
        );

        medicalRecord.setUpdatedAt(
                Instant.now().toString()
        );

        try {

            medicalRecordDAO
                    .updateMedicalRecord(
                            medicalRecord
                    );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to update medical record.",
                    e
            );
        }
    }

    // ============================================================
    // DELETE
    // ============================================================

    public void deleteMedicalRecord(
            String recordId) {

        validateId(
                recordId,
                "Medical record ID"
        );

        try {

            medicalRecordDAO
                    .deleteMedicalRecord(
                            recordId
                    );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to delete medical record.",
                    e
            );
        }
    }

    // ============================================================
    // DOCTOR-PATIENT AUTHORIZATION
    // ============================================================

    private void verifyDoctorPatientRelationship(
            String doctorUid,
            String patientUid) {

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
                        "Doctor is not authorized to access "
                                + "this patient's medical records."
                );
            }

        } catch (SecurityException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to verify doctor-patient relationship.",
                    e
            );
        }
    }

    // ============================================================
    // VALIDATION
    // ============================================================

    private void validateRecord(
            MedicalRecord medicalRecord) {

        if (medicalRecord == null) {

            throw new IllegalArgumentException(
                    "Medical record cannot be null."
            );
        }

        validateId(
                medicalRecord.getPatientUid(),
                "Patient UID"
        );

        validateId(
                medicalRecord.getDoctorUid(),
                "Doctor UID"
        );
    }

    private void validateId(
            String value,
            String fieldName) {

        if (value == null
                || value.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    fieldName
                            + " cannot be empty."
            );
        }
    }

    private boolean isEmpty(
            String value) {

        return value == null
                || value.trim().isEmpty();
    }
}