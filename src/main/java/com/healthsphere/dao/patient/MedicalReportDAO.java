package com.healthsphere.dao.patient;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.CloudinaryConfig;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.model.MedicalReport;

public class MedicalReportDAO {

    private static final String COLLECTION_NAME = "medicalReports";

    private final Firestore firestore;

    public MedicalReportDAO() {

        this.firestore = FirebaseConfig.getFirestore();
    }

    // =========================================================
    // GET CLOUDINARY
    // =========================================================

    private Cloudinary getCloudinary() {

        return CloudinaryConfig.getCloudinary();
    }

    // =========================================================
    // UPLOAD MEDICAL REPORT
    // =========================================================

    public MedicalReport uploadReport(
            MedicalReport report,
            File file)
            throws Exception {

        if (report == null) {

            throw new IllegalArgumentException(
                    "Medical report cannot be null."
            );
        }

        if (file == null || !file.exists()) {

            throw new IllegalArgumentException(
                    "Selected file does not exist."
            );
        }

        if (report.getPatientUid() == null
                || report.getPatientUid().isBlank()) {

            throw new IllegalArgumentException(
                    "Patient UID is missing."
            );
        }

        Cloudinary cloudinary = getCloudinary();

        String folder =
                "healthsphere/medicalReports/"
                        + report.getPatientUid();

        Map uploadResult =
                cloudinary.uploader().upload(
                        file,
                        ObjectUtils.asMap(
                                "folder",
                                folder,
                                "resource_type",
                                "auto"
                        )
                );

        String fileUrl =
                getString(
                        uploadResult,
                        "secure_url"
                );

        String publicId =
                getString(
                        uploadResult,
                        "public_id"
                );

        CollectionReference collection =
                firestore.collection(COLLECTION_NAME);

        String reportId =
                collection.document().getId();

        report.setReportId(reportId);

        report.setFileName(file.getName());

        report.setFileUrl(fileUrl);

        report.setStoragePath(publicId);

        report.setFileType(getFileType(file));

        report.setUploadDate(
                LocalDateTime.now()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "yyyy-MM-dd HH:mm:ss"
                                )
                        )
        );

        collection
                .document(reportId)
                .set(report)
                .get();

        System.out.println(
                "Medical report uploaded successfully."
        );

        return report;
    }

    // =========================================================
    // GET PATIENT REPORTS
    // =========================================================

    public List<MedicalReport> getReportsByPatientUid(
            String patientUid)
            throws ExecutionException,
            InterruptedException {

        if (patientUid == null
                || patientUid.isBlank()) {

            throw new IllegalArgumentException(
                    "Patient UID is missing."
            );
        }

        List<MedicalReport> reports =
                new ArrayList<>();

        QuerySnapshot snapshot =
                firestore
                        .collection(COLLECTION_NAME)
                        .whereEqualTo(
                                "patientUid",
                                patientUid
                        )
                        .get()
                        .get();

        for (QueryDocumentSnapshot document :
                snapshot.getDocuments()) {

            MedicalReport report =
                    document.toObject(
                            MedicalReport.class
                    );

            if (report != null) {

                if (report.getReportId() == null
                        || report.getReportId().isBlank()) {

                    report.setReportId(
                            document.getId()
                    );
                }

                reports.add(report);
            }
        }

        System.out.println(
                "Medical reports found: "
                        + reports.size()
        );

        return reports;
    }

    // =========================================================
    // GET REPORT BY ID
    // =========================================================

    public MedicalReport getReportById(
            String reportId)
            throws ExecutionException,
            InterruptedException {

        if (reportId == null
                || reportId.isBlank()) {

            throw new IllegalArgumentException(
                    "Report ID is missing."
            );
        }

        DocumentSnapshot document =
                firestore
                        .collection(COLLECTION_NAME)
                        .document(reportId)
                        .get()
                        .get();

        if (!document.exists()) {

            return null;
        }

        MedicalReport report =
                document.toObject(
                        MedicalReport.class
                );

        if (report != null
                && (report.getReportId() == null
                || report.getReportId().isBlank())) {

            report.setReportId(
                    document.getId()
            );
        }

        return report;
    }

    // =========================================================
    // DELETE REPORT
    // =========================================================

    public void deleteReport(
            String reportId)
            throws Exception {

        if (reportId == null
                || reportId.isBlank()) {

            throw new IllegalArgumentException(
                    "Report ID is missing."
            );
        }

        // -----------------------------------------------------
        // GET REPORT
        // -----------------------------------------------------

        MedicalReport report =
                getReportById(reportId);

        if (report == null) {

            throw new IllegalArgumentException(
                    "Medical report not found."
            );
        }

        // -----------------------------------------------------
        // FIRST DELETE FROM FIRESTORE
        // -----------------------------------------------------

        firestore
                .collection(COLLECTION_NAME)
                .document(reportId)
                .delete()
                .get();

        System.out.println(
                "Medical report deleted from Firestore."
        );

        // -----------------------------------------------------
        // THEN DELETE FROM CLOUDINARY
        // -----------------------------------------------------

        if (report.getStoragePath() != null
                && !report.getStoragePath().isBlank()) {

            try {

                Cloudinary cloudinary =
                        getCloudinary();

                String publicId =
                        report.getStoragePath();

                Map result =
                        cloudinary.uploader().destroy(
                                publicId,
                                ObjectUtils.asMap(
                                        "resource_type",
                                        "auto",
                                        "type",
                                        "upload"
                                )
                        );

                System.out.println(
                        "Cloudinary delete result: "
                                + result
                );

            } catch (Exception cloudinaryException) {

                /*
                 * Firestore has already been deleted.
                 *
                 * Do not make the user think the report
                 * deletion failed just because Cloudinary
                 * could not delete the file.
                 */

                System.err.println(
                        "Cloudinary deletion failed: "
                                + cloudinaryException.getMessage()
                );
            }
        }

        System.out.println(
                "Medical report deletion completed."
        );
    }

    // =========================================================
    // GET FILE TYPE
    // =========================================================

    private String getFileType(File file) {

        String fileName =
                file.getName();

        int lastDot =
                fileName.lastIndexOf(".");

        if (lastDot == -1) {

            return "Unknown";
        }

        return fileName
                .substring(lastDot + 1)
                .toUpperCase();
    }

    // =========================================================
    // SAFE MAP STRING
    // =========================================================

    private String getString(
            Map map,
            String key) {

        Object value =
                map.get(key);

        if (value == null) {

            return null;
        }

        return value.toString();
    }
}