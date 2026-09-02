package com.healthsphere.dao.hospital;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.model.HospitalBed;
import com.healthsphere.model.HospitalBed.BedStatus;
import com.healthsphere.util.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DAO responsible for Hospital Bed operations.
 *
 * Firestore collection:
 *
 *      hospitalBeds
 *
 * Architecture:
 *
 * View
 *   ↓
 * Controller
 *   ↓
 * BedDAO
 *   ↓
 * Firestore
 */
public class BedDAO {

    // =========================================================
    // COLLECTION
    // =========================================================

    private static final String COLLECTION_NAME =
            "hospitalBeds";

    // =========================================================
    // FIRESTORE
    // =========================================================

    private final Firestore firestore;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public BedDAO() {

        this.firestore =
                FirebaseConfig.getFirestore();
    }

    // =========================================================
    // CURRENT HOSPITAL ID
    // =========================================================

    private String getCurrentHospitalId() {

        if (!SessionManager.isLoggedIn()) {

            throw new IllegalStateException(
                    "No active user session. Please login again."
            );
        }

        if (SessionManager.getCurrentUser() == null) {

            throw new IllegalStateException(
                    "Current user session is null."
            );
        }

        String hospitalId =
                SessionManager
                        .getCurrentUser()
                        .getUid();

        if (hospitalId == null
                || hospitalId.trim().isEmpty()) {

            throw new IllegalStateException(
                    "Hospital UID is missing from the current session."
            );
        }

        return hospitalId.trim();
    }

    // =========================================================
    // CREATE BED
    // =========================================================

    public String createBed(
            HospitalBed bed) {

        validateBed(bed);

        String hospitalId =
                getCurrentHospitalId();

        // -----------------------------------------------------
        // Set hospital
        // -----------------------------------------------------

        bed.setHospitalId(
                hospitalId
        );

        // -----------------------------------------------------
        // Generate bed ID
        // -----------------------------------------------------

        if (bed.getBedId() == null
                || bed.getBedId().trim().isEmpty()) {

            bed.setBedId(
                    "BED-"
                            + UUID.randomUUID()
                            .toString()
                            .substring(0, 8)
                            .toUpperCase()
            );
        }

        // -----------------------------------------------------
        // Default active
        // -----------------------------------------------------

        bed.setActive(true);

        // -----------------------------------------------------
        // Validate ward ownership
        // -----------------------------------------------------

        if (!wardBelongsToHospital(
                bed.getWardId()
        )) {

            throw new IllegalArgumentException(
                    "Selected ward does not belong to the current hospital."
            );
        }

        // -----------------------------------------------------
        // Duplicate bed number
        // -----------------------------------------------------

        if (bedNumberExists(
                bed.getBedNumber(),
                null
        )) {

            throw new IllegalArgumentException(
                    "A bed with this bed number already exists."
            );
        }

        // -----------------------------------------------------
        // Occupied bed must have patient
        // -----------------------------------------------------

        if (bed.getStatus() == BedStatus.OCCUPIED) {

            if (bed.getPatientId() == null
                    || bed.getPatientId()
                            .trim()
                            .isEmpty()) {

                throw new IllegalArgumentException(
                        "An occupied bed must have a patient ID."
                );
            }

        } else {

            /*
             * Available, reserved and maintenance beds
             * do not require a patient ID during creation.
             */
            if (bed.getStatus() != BedStatus.OCCUPIED) {

                bed.setPatientId(null);
            }
        }

        // -----------------------------------------------------
        // Save
        // -----------------------------------------------------

        try {

            if (firestore == null) {

                throw new IllegalStateException(
                        "Firestore instance is null. "
                                + "Check FirebaseConfig."
                );
            }

            ApiFuture<WriteResult> future =
                    firestore
                            .collection(COLLECTION_NAME)
                            .document(bed.getBedId())
                            .set(bed);

            future.get();

            return bed.getBedId();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to create bed: "
                            + getRootCauseMessage(e),
                    e
            );
        }
    }

    // =========================================================
    // GET ALL ACTIVE BEDS
    // =========================================================

    public List<HospitalBed> getAllBeds() {

        try {

            if (firestore == null) {

                throw new IllegalStateException(
                        "Firestore instance is null. "
                                + "Check FirebaseConfig."
                );
            }

            String hospitalId =
                    getCurrentHospitalId();

            /*
             * Only filter by hospitalId.
             *
             * We intentionally do not use:
             *
             * whereEqualTo("hospitalId", hospitalId)
             * .whereEqualTo("active", true)
             *
             * This avoids unnecessary Firestore query/index
             * requirements.
             */
            ApiFuture<QuerySnapshot> future =
                    firestore
                            .collection(COLLECTION_NAME)
                            .whereEqualTo(
                                    "hospitalId",
                                    hospitalId
                            )
                            .get();

            QuerySnapshot snapshot =
                    future.get();

            List<HospitalBed> beds =
                    new ArrayList<>();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                try {

                    // -------------------------------------------------
                    // Active check
                    // -------------------------------------------------

                    Boolean active =
                            document.getBoolean(
                                    "active"
                            );

                    /*
                     * If active is missing, treat old documents
                     * as active.
                     */
                    if (active != null
                            && !active) {

                        continue;
                    }

                    // -------------------------------------------------
                    // Convert Firestore document
                    // -------------------------------------------------

                    HospitalBed bed =
                            document.toObject(
                                    HospitalBed.class
                            );

                    if (bed == null) {
                        continue;
                    }

                    // -------------------------------------------------
                    // Bed ID fallback
                    // -------------------------------------------------

                    if (bed.getBedId() == null
                            || bed.getBedId()
                                    .trim()
                                    .isEmpty()) {

                        bed.setBedId(
                                document.getId()
                        );
                    }

                    // -------------------------------------------------
                    // Hospital ID fallback
                    // -------------------------------------------------

                    if (bed.getHospitalId() == null
                            || bed.getHospitalId()
                                    .trim()
                                    .isEmpty()) {

                        bed.setHospitalId(
                                hospitalId
                        );
                    }

                    beds.add(bed);

                } catch (Exception documentException) {

                    /*
                     * This is very important.
                     *
                     * If Firestore cannot convert one document
                     * into HospitalBed, we now know exactly
                     * which document caused the problem.
                     */
                    throw new RuntimeException(
                            "Failed to read bed document '"
                                    + document.getId()
                                    + "': "
                                    + getRootCauseMessage(
                                            documentException
                                    ),
                            documentException
                    );
                }
            }

            // -------------------------------------------------
            // Sort by bed number
            // -------------------------------------------------

            beds.sort(
                    (first, second) ->
                            safeString(
                                    first.getBedNumber()
                            ).compareToIgnoreCase(
                                    safeString(
                                            second.getBedNumber()
                                    )
                            )
            );

            return beds;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to retrieve beds: "
                            + getRootCauseMessage(e),
                    e
            );
        }
    }

    // =========================================================
    // GET BED BY ID
    // =========================================================

    public HospitalBed getBedById(
            String bedId) {

        if (bedId == null
                || bedId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Bed ID is required."
            );
        }

        String hospitalId =
                getCurrentHospitalId();

        try {

            DocumentSnapshot document =
                    firestore
                            .collection(COLLECTION_NAME)
                            .document(
                                    bedId.trim()
                            )
                            .get()
                            .get();

            if (!document.exists()) {
                return null;
            }

            HospitalBed bed =
                    document.toObject(
                            HospitalBed.class
                    );

            if (bed == null) {
                return null;
            }

            // -------------------------------------------------
            // Ownership check
            // -------------------------------------------------

            if (!hospitalId.equals(
                    bed.getHospitalId()
            )) {

                return null;
            }

            // -------------------------------------------------
            // Active check
            // -------------------------------------------------

            if (!bed.isActive()) {
                return null;
            }

            // -------------------------------------------------
            // ID fallback
            // -------------------------------------------------

            if (bed.getBedId() == null
                    || bed.getBedId().trim().isEmpty()) {

                bed.setBedId(
                        document.getId()
                );
            }

            return bed;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to retrieve bed: "
                            + getRootCauseMessage(e),
                    e
            );
        }
    }

    // =========================================================
    // GET BEDS BY WARD
    // =========================================================

    public List<HospitalBed> getBedsByWard(
            String wardId) {

        if (wardId == null
                || wardId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Ward ID is required."
            );
        }

        String hospitalId =
                getCurrentHospitalId();

        try {

            /*
             * We filter only by hospitalId.
             *
             * wardId and active are handled in Java.
             */
            ApiFuture<QuerySnapshot> future =
                    firestore
                            .collection(COLLECTION_NAME)
                            .whereEqualTo(
                                    "hospitalId",
                                    hospitalId
                            )
                            .get();

            QuerySnapshot snapshot =
                    future.get();

            List<HospitalBed> beds =
                    new ArrayList<>();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                Boolean active =
                        document.getBoolean(
                                "active"
                        );

                if (active != null
                        && !active) {

                    continue;
                }

                String documentWardId =
                        document.getString(
                                "wardId"
                        );

                if (!wardId.equals(
                        documentWardId
                )) {

                    continue;
                }

                HospitalBed bed =
                        document.toObject(
                                HospitalBed.class
                        );

                if (bed == null) {
                    continue;
                }

                if (bed.getBedId() == null
                        || bed.getBedId()
                                .trim()
                                .isEmpty()) {

                    bed.setBedId(
                            document.getId()
                    );
                }

                beds.add(bed);
            }

            beds.sort(
                    (first, second) ->
                            safeString(
                                    first.getBedNumber()
                            ).compareToIgnoreCase(
                                    safeString(
                                            second.getBedNumber()
                                    )
                            )
            );

            return beds;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to retrieve beds for ward: "
                            + getRootCauseMessage(e),
                    e
            );
        }
    }

    // =========================================================
    // UPDATE BED
    // =========================================================

    public void updateBed(
            HospitalBed bed) {

        validateBed(bed);

        if (bed.getBedId() == null
                || bed.getBedId()
                        .trim()
                        .isEmpty()) {

            throw new IllegalArgumentException(
                    "Bed ID is required for update."
            );
        }

        String hospitalId =
                getCurrentHospitalId();

        try {

            DocumentSnapshot existingDocument =
                    firestore
                            .collection(COLLECTION_NAME)
                            .document(
                                    bed.getBedId()
                            )
                            .get()
                            .get();

            if (!existingDocument.exists()) {

                throw new IllegalArgumentException(
                        "Bed does not exist."
                );
            }

            HospitalBed existingBed =
                    existingDocument.toObject(
                            HospitalBed.class
                    );

            if (existingBed == null) {

                throw new IllegalArgumentException(
                        "Unable to read existing bed."
                );
            }

            // -------------------------------------------------
            // Ownership
            // -------------------------------------------------

            if (!hospitalId.equals(
                    existingBed.getHospitalId()
            )) {

                throw new SecurityException(
                        "You are not authorized to modify this bed."
                );
            }

            // -------------------------------------------------
            // Ward ownership
            // -------------------------------------------------

            if (!wardBelongsToHospital(
                    bed.getWardId()
            )) {

                throw new IllegalArgumentException(
                        "Selected ward does not belong to this hospital."
                );
            }

            // -------------------------------------------------
            // Duplicate bed number
            // -------------------------------------------------

            if (bedNumberExists(
                    bed.getBedNumber(),
                    bed.getBedId()
            )) {

                throw new IllegalArgumentException(
                        "A bed with this bed number already exists."
                );
            }

            // -------------------------------------------------
            // Preserve hospital
            // -------------------------------------------------

            bed.setHospitalId(
                    hospitalId
            );

            // -------------------------------------------------
            // Preserve active state
            // -------------------------------------------------

            bed.setActive(
                    existingBed.isActive()
            );

            // -------------------------------------------------
            // Occupied validation
            // -------------------------------------------------

            if (bed.getStatus()
                    == BedStatus.OCCUPIED) {

                if (bed.getPatientId() == null
                        || bed.getPatientId()
                                .trim()
                                .isEmpty()) {

                    throw new IllegalArgumentException(
                            "An occupied bed must have a patient ID."
                    );
                }

            } else {

                bed.setPatientId(null);
            }

            // -------------------------------------------------
            // Save
            // -------------------------------------------------

            ApiFuture<WriteResult> future =
                    firestore
                            .collection(COLLECTION_NAME)
                            .document(
                                    bed.getBedId()
                            )
                            .set(bed);

            future.get();

        } catch (SecurityException e) {

            throw e;

        } catch (IllegalArgumentException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to update bed: "
                            + getRootCauseMessage(e),
                    e
            );
        }
    }

    // =========================================================
    // UPDATE BED STATUS
    // =========================================================

    public void updateBedStatus(
            String bedId,
            BedStatus status) {

        if (bedId == null
                || bedId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Bed ID is required."
            );
        }

        if (status == null) {

            throw new IllegalArgumentException(
                    "Bed status is required."
            );
        }

        /*
         * Occupied beds require a patient ID.
         * Therefore use occupyBed() instead.
         */
        if (status == BedStatus.OCCUPIED) {

            throw new IllegalArgumentException(
                    "Use occupyBed() when setting a bed to OCCUPIED."
            );
        }

        String hospitalId =
                getCurrentHospitalId();

        try {

            DocumentSnapshot document =
                    firestore
                            .collection(COLLECTION_NAME)
                            .document(
                                    bedId.trim()
                            )
                            .get()
                            .get();

            if (!document.exists()) {

                throw new IllegalArgumentException(
                        "Bed does not exist."
                );
            }

            HospitalBed bed =
                    document.toObject(
                            HospitalBed.class
                    );

            if (bed == null) {

                throw new IllegalArgumentException(
                        "Unable to read bed."
                );
            }

            if (!hospitalId.equals(
                    bed.getHospitalId()
            )) {

                throw new SecurityException(
                        "You are not authorized to modify this bed."
                );
            }

            // -------------------------------------------------
            // Patient handling
            // -------------------------------------------------

            if (status != BedStatus.OCCUPIED) {

                bed.setPatientId(null);
            }

            // -------------------------------------------------
            // Update
            // -------------------------------------------------

            ApiFuture<WriteResult> future =
                    firestore
                            .collection(COLLECTION_NAME)
                            .document(
                                    bedId.trim()
                            )
                            .update(
                                    "status",
                                    status,
                                    "patientId",
                                    bed.getPatientId()
                            );

            future.get();

        } catch (SecurityException e) {

            throw e;

        } catch (IllegalArgumentException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to update bed status: "
                            + getRootCauseMessage(e),
                    e
            );
        }
    }

    // =========================================================
    // OCCUPY BED
    // =========================================================

    public void occupyBed(
            String bedId,
            String patientId) {

        if (bedId == null
                || bedId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Bed ID is required."
            );
        }

        if (patientId == null
                || patientId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Patient ID is required to occupy a bed."
            );
        }

        HospitalBed bed =
                getBedById(
                        bedId.trim()
                );

        if (bed == null) {

            throw new IllegalArgumentException(
                    "Bed does not exist."
            );
        }

        if (bed.isOccupied()) {

            throw new IllegalStateException(
                    "Bed is already occupied."
            );
        }

        try {

            ApiFuture<WriteResult> future =
                    firestore
                            .collection(COLLECTION_NAME)
                            .document(
                                    bedId.trim()
                            )
                            .update(
                                    "status",
                                    BedStatus.OCCUPIED,
                                    "patientId",
                                    patientId.trim()
                            );

            future.get();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to occupy bed: "
                            + getRootCauseMessage(e),
                    e
            );
        }
    }

    // =========================================================
    // RELEASE BED
    // =========================================================

    public void releaseBed(
            String bedId) {

        updateBedStatus(
                bedId,
                BedStatus.AVAILABLE
        );
    }

    // =========================================================
    // RESERVE BED
    // =========================================================

    public void reserveBed(
            String bedId) {

        updateBedStatus(
                bedId,
                BedStatus.RESERVED
        );
    }

    // =========================================================
    // PUT BED UNDER MAINTENANCE
    // =========================================================

    public void setMaintenance(
            String bedId) {

        updateBedStatus(
                bedId,
                BedStatus.MAINTENANCE
        );
    }

    // =========================================================
    // DEACTIVATE BED
    // =========================================================

    public void deactivateBed(
            String bedId) {

        if (bedId == null
                || bedId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Bed ID is required."
            );
        }

        String hospitalId =
                getCurrentHospitalId();

        try {

            DocumentSnapshot document =
                    firestore
                            .collection(COLLECTION_NAME)
                            .document(
                                    bedId.trim()
                            )
                            .get()
                            .get();

            if (!document.exists()) {

                throw new IllegalArgumentException(
                        "Bed does not exist."
                );
            }

            String bedHospitalId =
                    document.getString(
                            "hospitalId"
                    );

            if (!hospitalId.equals(
                    bedHospitalId
            )) {

                throw new SecurityException(
                        "You are not authorized to deactivate this bed."
                );
            }

            ApiFuture<WriteResult> future =
                    firestore
                            .collection(COLLECTION_NAME)
                            .document(
                                    bedId.trim()
                            )
                            .update(
                                    "active",
                                    false
                            );

            future.get();

        } catch (SecurityException e) {

            throw e;

        } catch (IllegalArgumentException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to deactivate bed: "
                            + getRootCauseMessage(e),
                    e
            );
        }
    }

    // =========================================================
    // CHECK BED EXISTS
    // =========================================================

    public boolean bedExists(
            String bedId) {

        if (bedId == null
                || bedId.trim().isEmpty()) {

            return false;
        }

        try {

            String hospitalId =
                    getCurrentHospitalId();

            DocumentSnapshot document =
                    firestore
                            .collection(COLLECTION_NAME)
                            .document(
                                    bedId.trim()
                            )
                            .get()
                            .get();

            if (!document.exists()) {
                return false;
            }

            String bedHospitalId =
                    document.getString(
                            "hospitalId"
                    );

            if (!hospitalId.equals(
                    bedHospitalId
            )) {

                return false;
            }

            Boolean active =
                    document.getBoolean(
                            "active"
                    );

            return active == null || active;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to check bed: "
                            + getRootCauseMessage(e),
                    e
            );
        }
    }

    // =========================================================
    // CHECK DUPLICATE BED NUMBER
    // =========================================================

    private boolean bedNumberExists(
            String bedNumber,
            String excludedBedId) {

        if (bedNumber == null
                || bedNumber.trim().isEmpty()) {

            return false;
        }

        String hospitalId =
                getCurrentHospitalId();

        try {

            ApiFuture<QuerySnapshot> future =
                    firestore
                            .collection(COLLECTION_NAME)
                            .whereEqualTo(
                                    "hospitalId",
                                    hospitalId
                            )
                            .get();

            QuerySnapshot snapshot =
                    future.get();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                Boolean active =
                        document.getBoolean(
                                "active"
                        );

                if (active != null
                        && !active) {

                    continue;
                }

                String documentId =
                        document.getId();

                if (excludedBedId != null
                        && excludedBedId.trim()
                                .equals(documentId)) {

                    continue;
                }

                String existingBedNumber =
                        document.getString(
                                "bedNumber"
                        );

                if (existingBedNumber != null
                        && existingBedNumber.trim()
                                .equalsIgnoreCase(
                                        bedNumber.trim()
                                )) {

                    return true;
                }
            }

            return false;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to check duplicate bed number: "
                            + getRootCauseMessage(e),
                    e
            );
        }
    }

    // =========================================================
    // CHECK WARD OWNERSHIP
    // =========================================================

    private boolean wardBelongsToHospital(
            String wardId) {

        if (wardId == null
                || wardId.trim().isEmpty()) {

            return false;
        }

        String hospitalId =
                getCurrentHospitalId();

        try {

            DocumentSnapshot document =
                    firestore
                            .collection(
                                    "hospitalWards"
                            )
                            .document(
                                    wardId.trim()
                            )
                            .get()
                            .get();

            if (!document.exists()) {
                return false;
            }

            String wardHospitalId =
                    document.getString(
                            "hospitalId"
                    );

            if (!hospitalId.equals(
                    wardHospitalId
            )) {

                return false;
            }

            Boolean active =
                    document.getBoolean(
                            "active"
                    );

            return active == null || active;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to verify ward ownership: "
                            + getRootCauseMessage(e),
                    e
            );
        }
    }

    // =========================================================
    // VALIDATE BED
    // =========================================================

    private void validateBed(
            HospitalBed bed) {

        if (bed == null) {

            throw new IllegalArgumentException(
                    "Bed cannot be null."
            );
        }

        if (bed.getWardId() == null
                || bed.getWardId()
                        .trim()
                        .isEmpty()) {

            throw new IllegalArgumentException(
                    "Ward ID is required."
            );
        }

        if (bed.getBedNumber() == null
                || bed.getBedNumber()
                        .trim()
                        .isEmpty()) {

            throw new IllegalArgumentException(
                    "Bed number is required."
            );
        }

        if (bed.getBedType() == null
                || bed.getBedType()
                        .trim()
                        .isEmpty()) {

            throw new IllegalArgumentException(
                    "Bed type is required."
            );
        }

        if (bed.getStatus() == null) {

            throw new IllegalArgumentException(
                    "Bed status is required."
            );
        }
    }

    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safeString(
            String value) {

        return value == null
                ? ""
                : value.trim();
    }

    // =========================================================
    // ROOT CAUSE MESSAGE
    // =========================================================

    private String getRootCauseMessage(
            Throwable throwable) {

        Throwable current =
                throwable;

        Throwable deepest =
                throwable;

        while (current != null) {

            deepest = current;

            current =
                    current.getCause();
        }

        String message =
                deepest.getMessage();

        if (message == null
                || message.trim().isEmpty()) {

            return deepest
                    .getClass()
                    .getSimpleName();
        }

        return deepest
                .getClass()
                .getSimpleName()
                + ": "
                + message;
    }
}