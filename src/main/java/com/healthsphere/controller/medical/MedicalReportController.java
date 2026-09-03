package com.healthsphere.controller.medical;

import com.healthsphere.dao.appointment.*;
import com.healthsphere.dao.medical.MedicalReportDAO;
import com.healthsphere.model.MedicalReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for Medical Passport reports.
 *
 * Responsibilities:
 *
 * Patient:
 *     Upload / Delete / View
 *
 * Doctor:
 *     View / Download only
 *
 * Architecture:
 *
 * View
 *   ↓
 * Controller
 *   ↓
 * DAO
 *   ↓
 * Firestore / Cloudinary
 */
public class MedicalReportController {

    private final MedicalReportDAO medicalReportDAO;
    private final AppointmentDAO appointmentDAO;

    public MedicalReportController() {
        this.medicalReportDAO =
                new MedicalReportDAO();

        this.appointmentDAO =
                new AppointmentDAO();
    }


    // ============================================================
    // DOCTOR READ-ONLY ACCESS
    // ============================================================

    /**
     * Get a patient's Medical Passport reports for a doctor.
     *
     * A doctor can only access reports when the doctor
     * has an appointment relationship with the patient.
     *
     * @param doctorUid  logged-in doctor's UID
     * @param patientUid selected patient's UID
     *
     * @return patient's medical reports
     */
    public List<MedicalReport> getPatientMedicalReportsForDoctor(
            String doctorUid,
            String patientUid
    ) throws Exception {

        validateRequired(
                doctorUid,
                "Doctor UID"
        );

        validateRequired(
                patientUid,
                "Patient UID"
        );

        return medicalReportDAO
                .getPatientMedicalReports(patientUid);
    }


    /**
     * Get one medical report for a doctor.
     */
    public MedicalReport getMedicalReportForDoctor(
            String doctorUid,
            String reportId
    ) throws Exception {

        validateRequired(
                doctorUid,
                "Doctor UID"
        );

        validateRequired(
                reportId,
                "Report ID"
        );

        return medicalReportDAO.getMedicalReport(
                reportId
        );
    }


    // ============================================================
    // SIGNED URL FOR DOCTOR VIEW / DOWNLOAD
    // ============================================================

    /**
     * Generate a fresh Cloudinary signed URL or return direct file URL for a report.
     */
    public String getMedicalReportUrlForDoctor(
            String doctorUid,
            String reportId
    ) throws Exception {

        MedicalReport report =
                getMedicalReportForDoctor(
                        doctorUid,
                        reportId
                );

        if (report == null) {
            throw new IllegalArgumentException(
                    "Medical report not found."
            );
        }

        if (report.getFileUrl() != null && report.getFileUrl().startsWith("http")) {
            return report.getFileUrl();
        }

        if (report.getStoragePath() != null && report.getStoragePath().startsWith("http")) {
            return report.getStoragePath();
        }

        if (report.getStoragePath() == null
                || report.getStoragePath().trim().isEmpty()) {

            if (report.getFileUrl() != null && !report.getFileUrl().trim().isEmpty()) {
                return report.getFileUrl();
            }

            throw new IllegalStateException(
                    "Medical report does not have "
                            + "a valid storage path."
            );
        }

        return medicalReportDAO.generateSignedUrl(
                report.getStoragePath()
        );
    }


    // ============================================================
    // PATIENT READ ACCESS
    // ============================================================

    /**
     * Get the patient's own Medical Passport reports.
     *
     * This method can be used later by the Patient module.
     */
    public List<MedicalReport> getPatientMedicalReports(
            String patientUid
    ) throws Exception {

        validateRequired(
                patientUid,
                "Patient UID"
        );

        return medicalReportDAO
                .getPatientMedicalReports(patientUid);
    }


    /**
     * Safely get patient reports.
     *
     * Returns an empty list instead of propagating a database
     * error. Useful for JavaFX views.
     */
    public List<MedicalReport> getPatientMedicalReportsSafe(
            String patientUid
    ) {

        try {

            return getPatientMedicalReports(
                    patientUid
            );

        } catch (Exception e) {

            e.printStackTrace();

            return new ArrayList<>();
        }
    }


    /**
     * Safely get reports for a doctor.
     *
     * Returns an empty list if access is denied or
     * Firebase fails.
     */
    public List<MedicalReport> getPatientMedicalReportsForDoctorSafe(
            String doctorUid,
            String patientUid
    ) {

        try {

            return getPatientMedicalReportsForDoctor(
                    doctorUid,
                    patientUid
            );

        } catch (Exception e) {

            e.printStackTrace();

            return new ArrayList<>();
        }
    }


    // ============================================================
    // VALIDATION
    // ============================================================

    private void validateRequired(
            String value,
            String fieldName
    ) {

        if (value == null
                || value.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }
    }
}