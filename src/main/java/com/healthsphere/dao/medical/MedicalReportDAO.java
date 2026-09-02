package com.healthsphere.dao.medical;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.CloudinaryConfig;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.model.MedicalReport;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * DAO for Medical Passport reports.
 *
 * Firestore collection:
 *     medicalReports
 *
 * File storage:
 *     Cloudinary
 *
 * Ownership:
 *     Medical reports belong to the PATIENT.
 *
 * Patient:
 *     Upload / View / Download / Delete
 *
 * Doctor:
 *     View / Download only
 *
 * Important:
 *     Existing reports may have been uploaded by different
 *     implementations. Therefore, download logic checks
 *     Cloudinary metadata instead of assuming PDF/image.
 */
public class MedicalReportDAO {

    private static final String COLLECTION = "medicalReports";

    private final Firestore firestore;
    private final Cloudinary cloudinary;

    public MedicalReportDAO() {

        this.firestore =
                FirebaseConfig.getFirestore();

        this.cloudinary =
                CloudinaryConfig.getCloudinary();
    }


    // ============================================================
    // PATIENT UPLOAD
    // ============================================================

    /**
     * Upload a medical report to Cloudinary and store metadata
     * in Firestore.
     */
    public MedicalReport uploadMedicalReport(
            String patientUid,
            String doctorUid,
            String hospitalId,
            String appointmentId,
            String reportName,
            String reportType,
            File file,
            String uploadedByUid,
            String uploadedByRole
    ) throws Exception {

        validateRequired(
                patientUid,
                "Patient UID"
        );

        validateRequired(
                reportName,
                "Report name"
        );

        validateRequired(
                file,
                "File"
        );


        if (!file.exists() || !file.isFile()) {

            throw new IllegalArgumentException(
                    "Medical report file does not exist: "
                            + file.getAbsolutePath()
            );
        }


        String fileName =
                file.getName();


        String extension =
                getExtension(fileName);


        if (!isSupportedExtension(extension)) {

            throw new IllegalArgumentException(
                    "Unsupported medical report format: "
                            + extension
                            + ". Supported formats: "
                            + "PDF, JPG, JPEG, PNG, GIF, WEBP, BMP, TIFF."
            );
        }


        /*
         * Determine resource type for NEW uploads.
         */
        String resourceType =
                getCloudinaryResourceType(
                        extension
                );


        /*
         * Folder structure.
         */
        String folder;

        if (appointmentId != null
                && !appointmentId.trim().isEmpty()) {

            folder =
                    "healthsphere/medical-reports/"
                            + patientUid
                            + "/"
                            + appointmentId;

        } else {

            folder =
                    "healthsphere/medical-reports/"
                            + patientUid
                            + "/general";
        }


        /*
         * Generate unique report ID.
         */
        String reportId =
                UUID.randomUUID().toString();


        /*
         * Public ID is only the report ID.
         *
         * Folder is specified separately.
         */
        String publicId =
                reportId;


        // --------------------------------------------------------
        // CLOUDINARY UPLOAD
        // --------------------------------------------------------

        Map<String, Object> uploadOptions =
                ObjectUtils.asMap(
                        "resource_type",
                        resourceType,

                        "type",
                        "authenticated",

                        "public_id",
                        publicId,

                        "folder",
                        folder
                );


        Map<?, ?> uploadResult =
                cloudinary
                        .uploader()
                        .upload(
                                file,
                                uploadOptions
                        );


        /*
         * Get actual Cloudinary public ID.
         */
        String storedPublicId =
                (String) uploadResult.get(
                        "public_id"
                );


        /*
         * Get actual format returned by Cloudinary.
         */
        String storedFormat =
                getStringValue(
                        uploadResult.get(
                                "format"
                        )
                );


        if (storedFormat == null
                || storedFormat.isEmpty()) {

            storedFormat =
                    extension;
        }


        /*
         * Get actual resource type returned by Cloudinary.
         */
        String storedResourceType =
                getStringValue(
                        uploadResult.get(
                                "resource_type"
                        )
                );


        if (storedResourceType == null
                || storedResourceType.isEmpty()) {

            storedResourceType =
                    resourceType;
        }


        /*
         * Generate signed URL for the newly uploaded file.
         */
        String fileUrl =
                generateAuthenticatedUrl(
                        storedPublicId,
                        storedFormat,
                        storedResourceType
                );


        // --------------------------------------------------------
        // MEDICAL REPORT OBJECT
        // --------------------------------------------------------

        MedicalReport report =
                new MedicalReport();


        report.setReportId(
                reportId
        );


        report.setPatientUid(
                patientUid
        );


        report.setDoctorUid(
                emptyToNull(
                        doctorUid
                )
        );


        report.setHospitalId(
                emptyToNull(
                        hospitalId
                )
        );


        report.setAppointmentId(
                emptyToNull(
                        appointmentId
                )
        );


        report.setReportName(
                reportName
        );


        report.setReportType(
                emptyToNull(
                        reportType
                )
        );


        report.setFileName(
                fileName
        );


        report.setFileUrl(
                fileUrl
        );


        /*
         * Store exact Cloudinary public ID.
         */
        report.setStoragePath(
                storedPublicId
        );


        /*
         * Store MIME type.
         */
        report.setFileType(
                getMimeType(
                        storedFormat
                )
        );


        report.setUploadedByUid(
                uploadedByUid
        );


        report.setUploadedByRole(
                uploadedByRole
        );


        report.setUploadedAt(
                Instant.now().toString()
        );


        // --------------------------------------------------------
        // FIRESTORE
        // --------------------------------------------------------

        firestore
                .collection(COLLECTION)
                .document(reportId)
                .set(report)
                .get();


        return report;
    }


    // ============================================================
    // GET PATIENT REPORTS
    // ============================================================

    /**
     * Get all medical reports belonging to a patient.
     */
    public List<MedicalReport> getPatientMedicalReports(
            String patientUid
    ) throws ExecutionException, InterruptedException {

        validateRequired(
                patientUid,
                "Patient UID"
        );


        ApiFuture<QuerySnapshot> future =
                firestore
                        .collection(COLLECTION)
                        .whereEqualTo(
                                "patientUid",
                                patientUid
                        )
                        .get();


        QuerySnapshot snapshot =
                future.get();


        List<MedicalReport> reports =
                new ArrayList<>();


        for (QueryDocumentSnapshot document
                : snapshot.getDocuments()) {

            MedicalReport report =
                    document.toObject(
                            MedicalReport.class
                    );

            reports.add(
                    report
            );
        }


        return reports;
    }


    // ============================================================
    // GET SINGLE REPORT
    // ============================================================

    /**
     * Get one medical report using report ID.
     */
    public MedicalReport getMedicalReport(
            String reportId
    ) throws ExecutionException, InterruptedException {

        validateRequired(
                reportId,
                "Report ID"
        );


        DocumentSnapshot document =
                firestore
                        .collection(COLLECTION)
                        .document(reportId)
                        .get()
                        .get();


        if (!document.exists()) {
            return null;
        }


        return document.toObject(
                MedicalReport.class
        );
    }


    // ============================================================
    // DELETE REPORT
    // ============================================================

    /**
     * Delete medical report from Cloudinary and Firestore.
     *
     * Patient module only.
     *
     * Doctor module must NOT call this.
     */
    public void deleteMedicalReport(
            String reportId
    ) throws Exception {

        validateRequired(
                reportId,
                "Report ID"
        );


        DocumentReference document =
                firestore
                        .collection(COLLECTION)
                        .document(reportId);


        DocumentSnapshot snapshot =
                document
                        .get()
                        .get();


        if (!snapshot.exists()) {

            throw new IllegalArgumentException(
                    "Medical report not found: "
                            + reportId
            );
        }


        MedicalReport report =
                snapshot.toObject(
                        MedicalReport.class
                );


        if (report != null
                && report.getStoragePath() != null
                && !report.getStoragePath()
                        .trim()
                        .isEmpty()) {

            /*
             * Resolve the actual resource type from Cloudinary.
             */
            String resourceType =
                    resolveResourceType(
                            report.getStoragePath()
                    );


            cloudinary
                    .uploader()
                    .destroy(
                            report.getStoragePath(),
                            ObjectUtils.asMap(
                                    "resource_type",
                                    resourceType,

                                    "type",
                                    "authenticated",

                                    "invalidate",
                                    true
                            )
                    );
        }


        /*
         * Delete Firestore metadata.
         */
        document
                .delete()
                .get();
    }


    // ============================================================
    // DOWNLOAD URL
    // ============================================================

    /**
     * Generate a fresh signed URL from a storage path.
     *
     * Kept for compatibility with existing code.
     */
    public String generateSignedUrl(
            String storagePath
    ) {

        validateRequired(
                storagePath,
                "Storage path"
        );


        /*
         * IMPORTANT:
         *
         * Instead of assuming PDF, first ask Cloudinary
         * what the asset actually is.
         */
        CloudinaryAssetInfo asset =
                resolveCloudinaryAsset(
                        storagePath
                );


        return generateAuthenticatedUrl(
                storagePath,
                asset.format,
                asset.resourceType
        );
    }


    /**
     * Generate a fresh signed URL using report ID.
     *
     * RECOMMENDED METHOD FOR DOCTOR MEDICAL REPORTS PAGE.
     *
     * This method:
     *
     * 1. Gets report from Firestore
     * 2. Gets exact storagePath
     * 3. Checks Cloudinary
     * 4. Finds actual resource type
     * 5. Finds actual format
     * 6. Generates correct signed URL
     */
    public String generateSignedUrlForReport(
            String reportId
    ) throws ExecutionException, InterruptedException {

        validateRequired(
                reportId,
                "Report ID"
        );


        MedicalReport report =
                getMedicalReport(
                        reportId
                );


        if (report == null) {

            throw new IllegalArgumentException(
                    "Medical report not found: "
                            + reportId
            );
        }


        String storagePath =
                report.getStoragePath();


        if (storagePath == null
                || storagePath.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Medical report has no Cloudinary "
                            + "storage path."
            );
        }


        /*
         * THIS IS THE IMPORTANT FIX.
         *
         * Do not trust old fileType/fileName metadata.
         *
         * Ask Cloudinary itself.
         */
        CloudinaryAssetInfo asset =
                resolveCloudinaryAsset(
                        storagePath
                );


        System.out.println(
                "=========================================="
        );

        System.out.println(
                "Medical Report Download"
        );

        System.out.println(
                "Report ID: "
                        + reportId
        );

        System.out.println(
                "Storage Path: "
                        + storagePath
        );

        System.out.println(
                "Cloudinary Resource Type: "
                        + asset.resourceType
        );

        System.out.println(
                "Cloudinary Format: "
                        + asset.format
        );

        System.out.println(
                "=========================================="
        );


        return generateAuthenticatedUrl(
                storagePath,
                asset.format,
                asset.resourceType
        );
    }


    // ============================================================
    // CLOUDINARY ASSET RESOLUTION
    // ============================================================

    /**
     * Find the actual Cloudinary resource type and format.
     *
     * This is specifically important because your application
     * contains reports uploaded by different implementations.
     *
     * We first try:
     *
     *     image/authenticated
     *
     * If not found, we try:
     *
     *     raw/authenticated
     */
    private CloudinaryAssetInfo resolveCloudinaryAsset(
            String storagePath
    ) {

        validateRequired(
                storagePath,
                "Storage path"
        );


        /*
         * --------------------------------------------------------
         * TRY IMAGE
         * --------------------------------------------------------
         */
        try {

            Map<?, ?> result =
                    cloudinary.api().resource(
                            storagePath,
                            ObjectUtils.asMap(
                                    "resource_type",
                                    "image",

                                    "type",
                                    "authenticated"
                            )
                    );


            String format =
                    getStringValue(
                            result.get(
                                    "format"
                            )
                    );


            if (format == null
                    || format.isEmpty()) {

                format =
                        getExtensionFromStoragePath(
                                storagePath
                        );
            }


            if (format == null
                    || format.isEmpty()) {

                format =
                        "jpg";
            }


            return new CloudinaryAssetInfo(
                    "image",
                    format
            );

        } catch (Exception ignored) {

            /*
             * Not an image or image resource was not found.
             *
             * Continue and try RAW.
             */
        }


        /*
         * --------------------------------------------------------
         * TRY RAW
         * --------------------------------------------------------
         */
        try {

            Map<?, ?> result =
                    cloudinary.api().resource(
                            storagePath,
                            ObjectUtils.asMap(
                                    "resource_type",
                                    "raw",

                                    "type",
                                    "authenticated"
                            )
                    );


            String format =
                    getStringValue(
                            result.get(
                                    "format"
                            )
                    );


            if (format == null
                    || format.isEmpty()) {

                format =
                        getExtensionFromStoragePath(
                                storagePath
                        );
            }


            if (format == null
                    || format.isEmpty()) {

                format =
                        "pdf";
            }


            return new CloudinaryAssetInfo(
                    "raw",
                    format
            );

        } catch (Exception ignored) {

            /*
             * Continue to final fallback.
             */
        }


        /*
         * --------------------------------------------------------
         * FALLBACK
         * --------------------------------------------------------
         *
         * If Cloudinary Admin API cannot identify the resource,
         * use Firestore-compatible defaults.
         */
        String extension =
                getExtensionFromStoragePath(
                        storagePath
                );


        if (extension == null
                || extension.isEmpty()) {

            extension =
                    "pdf";
        }


        String resourceType =
                getCloudinaryResourceType(
                        extension
                );


        return new CloudinaryAssetInfo(
                resourceType,
                extension
        );
    }


    /**
     * Resolve resource type only.
     */
    private String resolveResourceType(
            String storagePath
    ) {

        CloudinaryAssetInfo asset =
                resolveCloudinaryAsset(
                        storagePath
                );

        return asset.resourceType;
    }


    // ============================================================
    // CLOUDINARY SIGNED URL
    // ============================================================

    /**
     * Generate authenticated signed Cloudinary URL.
     *
     * Compatible with the Cloudinary Java SDK currently used
     * by this project.
     */
    private String generateAuthenticatedUrl(
            String storagePath,
            String format,
            String resourceType
    ) {

        validateRequired(
                storagePath,
                "Storage path"
        );


        validateRequired(
                format,
                "File format"
        );


        validateRequired(
                resourceType,
                "Resource type"
        );


        return cloudinary
                .url()
                .resourceType(
                        resourceType
                )
                .type(
                        "authenticated"
                )
                .secure(
                        true
                )
                .signed(
                        true
                )
                .format(
                        format
                )
                .generate(
                        storagePath
                );
    }


    // ============================================================
    // RESOURCE TYPE FROM EXTENSION
    // ============================================================

    private String getCloudinaryResourceType(
            String extension
    ) {

        if (extension == null
                || extension.trim().isEmpty()) {

            return "raw";
        }


        String ext =
                extension
                        .trim()
                        .toLowerCase(
                                Locale.ROOT
                        );


        switch (ext) {

            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "webp":
            case "bmp":
            case "tif":
            case "tiff":

                return "image";

            default:

                return "raw";
        }
    }


    // ============================================================
    // FILE EXTENSION
    // ============================================================

    private String getExtension(
            String fileName
    ) {

        if (fileName == null
                || fileName.trim().isEmpty()) {

            return "";
        }


        String cleanName =
                fileName.trim();


        int lastDot =
                cleanName.lastIndexOf(
                        '.'
                );


        if (lastDot < 0
                || lastDot ==
                cleanName.length() - 1) {

            return "";
        }


        return cleanName
                .substring(
                        lastDot + 1
                )
                .toLowerCase(
                        Locale.ROOT
                );
    }


    private String getExtensionFromStoragePath(
            String storagePath
    ) {

        if (storagePath == null
                || storagePath.trim().isEmpty()) {

            return "";
        }


        String path =
                storagePath.trim();


        int lastSlash =
                path.lastIndexOf(
                        '/'
                );


        String fileName;


        if (lastSlash >= 0) {

            fileName =
                    path.substring(
                            lastSlash + 1
                    );

        } else {

            fileName =
                    path;
        }


        return getExtension(
                fileName
        );
    }


    // ============================================================
    // MIME TYPE
    // ============================================================

    private String getMimeType(
            String extension
    ) {

        if (extension == null) {

            return "application/octet-stream";
        }


        switch (
                extension
                        .toLowerCase(
                                Locale.ROOT
                        )
        ) {

            case "pdf":
                return "application/pdf";

            case "jpg":
            case "jpeg":
                return "image/jpeg";

            case "png":
                return "image/png";

            case "gif":
                return "image/gif";

            case "webp":
                return "image/webp";

            case "bmp":
                return "image/bmp";

            case "tif":
            case "tiff":
                return "image/tiff";

            default:
                return "application/octet-stream";
        }
    }


    // ============================================================
    // SUPPORTED FILES
    // ============================================================

    private boolean isSupportedExtension(
            String extension
    ) {

        if (extension == null
                || extension.trim().isEmpty()) {

            return false;
        }


        switch (
                extension
                        .toLowerCase(
                                Locale.ROOT
                        )
        ) {

            case "pdf":
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "webp":
            case "bmp":
            case "tif":
            case "tiff":

                return true;

            default:

                return false;
        }
    }


    // ============================================================
    // STRING VALUE
    // ============================================================

    private String getStringValue(
            Object value
    ) {

        if (value == null) {
            return null;
        }


        return value.toString();
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
                    fieldName
                            + " is required."
            );
        }
    }


    private void validateRequired(
            File file,
            String fieldName
    ) {

        if (file == null) {

            throw new IllegalArgumentException(
                    fieldName
                            + " is required."
            );
        }
    }


    // ============================================================
    // EMPTY TO NULL
    // ============================================================

    private String emptyToNull(
            String value
    ) {

        if (value == null
                || value.trim().isEmpty()) {

            return null;
        }


        return value;
    }


    // ============================================================
    // CLOUDINARY ASSET INFO
    // ============================================================

    /**
     * Small internal object containing the actual Cloudinary
     * resource information.
     */
    private static class CloudinaryAssetInfo {

        private final String resourceType;
        private final String format;


        private CloudinaryAssetInfo(
                String resourceType,
                String format
        ) {

            this.resourceType =
                    resourceType;

            this.format =
                    format;
        }
    }
}