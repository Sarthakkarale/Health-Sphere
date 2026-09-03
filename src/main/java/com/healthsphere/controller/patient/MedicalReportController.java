
package com.healthsphere.controller.patient;

import com.healthsphere.dao.patient.MedicalReportDAO;
import com.healthsphere.model.MedicalReport;
import com.healthsphere.util.SessionManager;

import java.io.File;
import java.util.List;

public class MedicalReportController {

    private final MedicalReportDAO medicalReportDAO;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public MedicalReportController() {

        this.medicalReportDAO =
                new MedicalReportDAO();
    }


    // =========================================================
    // UPLOAD MEDICAL REPORT
    // =========================================================

    public MedicalReport uploadReport(
            String reportName,
            String reportType,
            File file)
            throws Exception {

        String patientUid =
                getCurrentPatientUid();

        if (reportName == null ||
                reportName.isBlank()) {

            throw new IllegalArgumentException(
                    "Report name is required."
            );
        }

        if (reportType == null ||
                reportType.isBlank()) {

            throw new IllegalArgumentException(
                    "Report type is required."
            );
        }

        if (file == null) {

            throw new IllegalArgumentException(
                    "Please select a file."
            );
        }


        MedicalReport report =
                new MedicalReport();

        report.setPatientUid(
                patientUid
        );

        report.setReportName(
                reportName.trim()
        );

        report.setReportType(
                reportType.trim()
        );

        report.setUploadedBy(
                patientUid
        );


        return medicalReportDAO
                .uploadReport(
                        report,
                        file
                );
    }


    // =========================================================
    // GET CURRENT PATIENT REPORTS
    // =========================================================

    public List<MedicalReport>
    getCurrentPatientReports()
            throws Exception {

        String patientUid =
                getCurrentPatientUid();

        return medicalReportDAO
                .getReportsByPatientUid(
                        patientUid
                );
    }


    // =========================================================
    // GET CURRENT PATIENT REPORT BY ID
    // =========================================================

    public MedicalReport getCurrentPatientReportById(
            String reportId)
            throws Exception {

        MedicalReport report =
                medicalReportDAO
                        .getReportById(
                                reportId
                        );

        if (report == null) {

            return null;
        }

        verifyOwnership(
                report
        );

        return report;
    }


    // =========================================================
    // DELETE CURRENT PATIENT REPORT
    // =========================================================

    public void deleteCurrentPatientReport(
            String reportId)
            throws Exception {

        MedicalReport report =
                medicalReportDAO
                        .getReportById(
                                reportId
                        );

        if (report == null) {

            throw new IllegalArgumentException(
                    "Medical report not found."
            );
        }

        verifyOwnership(
                report
        );

        medicalReportDAO
                .deleteReport(
                        reportId
                );
    }


    // =========================================================
    // VERIFY REPORT OWNERSHIP
    // =========================================================

    private void verifyOwnership(
            MedicalReport report) {

        String currentPatientUid =
                getCurrentPatientUid();

        String reportPatientUid =
                report.getPatientUid();

        if (reportPatientUid == null ||
                !reportPatientUid.equals(
                        currentPatientUid
                )) {

            throw new SecurityException(
                    "You do not have permission to access this medical report."
            );
        }
    }


    // =========================================================
    // GET CURRENT PATIENT UID
    // =========================================================

    private String getCurrentPatientUid() {

        if (!SessionManager.isLoggedIn()) {

            throw new IllegalStateException(
                    "No active user session."
            );
        }

        String uid =
                SessionManager
                        .getCurrentUser()
                        .getUid();

        if (uid == null ||
                uid.isBlank()) {

            throw new IllegalStateException(
                    "Unable to identify the current patient."
            );
        }

        return uid;
    }
}


