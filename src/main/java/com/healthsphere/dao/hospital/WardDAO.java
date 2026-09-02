package com.healthsphere.dao.hospital;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.model.HospitalWard;
import com.healthsphere.util.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DAO for Hospital Ward operations.
 *
 * Firestore collection:
 *      hospitalWards
 *
 * Architecture:
 *
 * View
 *   ↓
 * Controller
 *   ↓
 * DAO
 *   ↓
 * Firestore
 */
public class WardDAO {

    // =========================================================
    // COLLECTION
    // =========================================================

    private static final String COLLECTION_NAME =
            "hospitalWards";

    // =========================================================
    // FIRESTORE
    // =========================================================

    private final Firestore firestore;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public WardDAO() {

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
    // CREATE WARD
    // =========================================================

    public String createWard(
            HospitalWard ward) {

        validateWard(ward);

        String hospitalId =
                getCurrentHospitalId();

        ward.setHospitalId(
                hospitalId
        );

        // -----------------------------------------------------
        // Generate ID
        // -----------------------------------------------------

        if (ward.getWardId() == null
                || ward.getWardId().trim().isEmpty()) {

            ward.setWardId(
                    "WARD-"
                            + UUID.randomUUID()
                            .toString()
                            .substring(0, 8)
                            .toUpperCase()
            );
        }

        ward.setActive(true);

        // -----------------------------------------------------
        // Duplicate name
        // -----------------------------------------------------

        if (wardNameExists(
                ward.getName(),
                null
        )) {

            throw new IllegalArgumentException(
                    "A ward with the same name already exists."
            );
        }

        try {

            if (firestore == null) {

                throw new IllegalStateException(
                        "Firestore instance is null. Check FirebaseConfig."
                );
            }

            ApiFuture<WriteResult> future =
                    firestore
                            .collection(COLLECTION_NAME)
                            .document(ward.getWardId())
                            .set(ward);

            future.get();

            return ward.getWardId();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to create ward: "
                            + getRootCauseMessage(e),
                    e
            );
        }
    }

    // =========================================================
    // GET ALL ACTIVE WARDS
    // =========================================================

    public List<HospitalWard> getAllWards() {

        try {

            // -------------------------------------------------
            // Check Firestore
            // -------------------------------------------------

            if (firestore == null) {

                throw new IllegalStateException(
                        "Firestore instance is null. "
                                + "Check FirebaseConfig.getFirestore()."
                );
            }

            // -------------------------------------------------
            // Get hospital ID from session
            // -------------------------------------------------

            String hospitalId =
                    getCurrentHospitalId();

            /*
             * We intentionally do NOT use orderBy().
             *
             * This avoids unnecessary Firestore composite
             * index requirements.
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

            List<HospitalWard> wards =
                    new ArrayList<>();

            // -------------------------------------------------
            // Convert documents
            // -------------------------------------------------

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                try {

                    /*
                     * First check whether this ward is active.
                     *
                     * We do this manually instead of putting
                     * active=true into the Firestore query.
                     *
                     * This makes the DAO more tolerant of
                     * older ward documents.
                     */

                    Boolean active =
                            document.getBoolean(
                                    "active"
                            );

                    /*
                     * If active field does not exist,
                     * treat the document as active.
                     *
                     * This is useful for documents created
                     * before the active field was introduced.
                     */
                    if (active != null
                            && !active) {

                        continue;
                    }

                    HospitalWard ward =
                            document.toObject(
                                    HospitalWard.class
                            );

                    if (ward == null) {
                        continue;
                    }

                    // -------------------------------------------------
                    // Ensure ward ID
                    // -------------------------------------------------

                    if (ward.getWardId() == null
                            || ward.getWardId()
                                    .trim()
                                    .isEmpty()) {

                        ward.setWardId(
                                document.getId()
                        );
                    }

                    // -------------------------------------------------
                    // Ensure hospital ID
                    // -------------------------------------------------

                    if (ward.getHospitalId() == null
                            || ward.getHospitalId()
                                    .trim()
                                    .isEmpty()) {

                        ward.setHospitalId(
                                hospitalId
                        );
                    }

                    wards.add(ward);

                } catch (Exception documentException) {

                    /*
                     * Give the exact document ID and error.
                     */
                    throw new RuntimeException(
                            "Failed to read ward document '"
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
            // Sort locally
            // -------------------------------------------------

            wards.sort(
                    (first, second) ->
                            safeString(
                                    first.getName()
                            ).compareToIgnoreCase(
                                    safeString(
                                            second.getName()
                                    )
                            )
            );

            return wards;

        } catch (Exception e) {

            /*
             * IMPORTANT:
             *
             * This message now includes the real underlying
             * Firebase/Firestore exception.
             */
            throw new RuntimeException(
                    "Failed to retrieve wards: "
                            + getRootCauseMessage(e),
                    e
            );
        }
    }

    // =========================================================
    // GET WARD BY ID
    // =========================================================

    public HospitalWard getWardById(
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

            DocumentSnapshot document =
                    firestore
                            .collection(COLLECTION_NAME)
                            .document(wardId.trim())
                            .get()
                            .get();

            if (!document.exists()) {
                return null;
            }

            HospitalWard ward =
                    document.toObject(
                            HospitalWard.class
                    );

            if (ward == null) {
                return null;
            }

            // -------------------------------------------------
            // Hospital ownership
            // -------------------------------------------------

            if (!hospitalId.equals(
                    ward.getHospitalId()
            )) {

                return null;
            }

            // -------------------------------------------------
            // Ward ID fallback
            // -------------------------------------------------

            if (ward.getWardId() == null
                    || ward.getWardId().trim().isEmpty()) {

                ward.setWardId(
                        document.getId()
                );
            }

            return ward;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to retrieve ward: "
                            + getRootCauseMessage(e),
                    e
            );
        }
    }

    // =========================================================
    // UPDATE WARD
    // =========================================================

    public void updateWard(
            HospitalWard ward) {

        validateWard(ward);

        if (ward.getWardId() == null
                || ward.getWardId().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Ward ID is required for update."
            );
        }

        String hospitalId =
                getCurrentHospitalId();

        try {

            DocumentSnapshot existingDocument =
                    firestore
                            .collection(COLLECTION_NAME)
                            .document(
                                    ward.getWardId()
                            )
                            .get()
                            .get();

            if (!existingDocument.exists()) {

                throw new IllegalArgumentException(
                        "Ward does not exist."
                );
            }

            HospitalWard existingWard =
                    existingDocument.toObject(
                            HospitalWard.class
                    );

            if (existingWard == null) {

                throw new IllegalArgumentException(
                        "Unable to read existing ward."
                );
            }

            // -------------------------------------------------
            // Ownership
            // -------------------------------------------------

            if (!hospitalId.equals(
                    existingWard.getHospitalId()
            )) {

                throw new SecurityException(
                        "You are not authorized to modify this ward."
                );
            }

            // -------------------------------------------------
            // Duplicate name
            // -------------------------------------------------

            if (wardNameExists(
                    ward.getName(),
                    ward.getWardId()
            )) {

                throw new IllegalArgumentException(
                        "A ward with the same name already exists."
                );
            }

            // -------------------------------------------------
            // Preserve fields
            // -------------------------------------------------

            ward.setHospitalId(
                    hospitalId
            );

            ward.setActive(
                    existingWard.isActive()
            );

            // -------------------------------------------------
            // Save
            // -------------------------------------------------

            ApiFuture<WriteResult> future =
                    firestore
                            .collection(COLLECTION_NAME)
                            .document(
                                    ward.getWardId()
                            )
                            .set(ward);

            future.get();

        } catch (SecurityException e) {

            throw e;

        } catch (IllegalArgumentException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to update ward: "
                            + getRootCauseMessage(e),
                    e
            );
        }
    }

    // =========================================================
    // DEACTIVATE WARD
    // =========================================================

    public void deactivateWard(
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

            DocumentSnapshot document =
                    firestore
                            .collection(COLLECTION_NAME)
                            .document(
                                    wardId.trim()
                            )
                            .get()
                            .get();

            if (!document.exists()) {

                throw new IllegalArgumentException(
                        "Ward does not exist."
                );
            }

            HospitalWard ward =
                    document.toObject(
                            HospitalWard.class
                    );

            if (ward == null) {

                throw new IllegalArgumentException(
                        "Unable to read ward."
                );
            }

            // -------------------------------------------------
            // Ownership
            // -------------------------------------------------

            if (!hospitalId.equals(
                    ward.getHospitalId()
            )) {

                throw new SecurityException(
                        "You are not authorized to deactivate this ward."
                );
            }

            // -------------------------------------------------
            // Soft delete
            // -------------------------------------------------

            ApiFuture<WriteResult> future =
                    firestore
                            .collection(COLLECTION_NAME)
                            .document(
                                    wardId.trim()
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
                    "Failed to deactivate ward: "
                            + getRootCauseMessage(e),
                    e
            );
        }
    }

    // =========================================================
    // CHECK WARD EXISTS
    // =========================================================

    public boolean wardExists(
            String wardId) {

        if (wardId == null
                || wardId.trim().isEmpty()) {

            return false;
        }

        try {

            String hospitalId =
                    getCurrentHospitalId();

            DocumentSnapshot document =
                    firestore
                            .collection(COLLECTION_NAME)
                            .document(
                                    wardId.trim()
                            )
                            .get()
                            .get();

            if (!document.exists()) {
                return false;
            }

            String documentHospitalId =
                    document.getString(
                            "hospitalId"
                    );

            Boolean active =
                    document.getBoolean(
                            "active"
                    );

            if (documentHospitalId == null
                    || !hospitalId.equals(
                            documentHospitalId
                    )) {

                return false;
            }

            /*
             * If active is missing, consider the old
             * document active.
             */
            return active == null || active;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to check ward: "
                            + getRootCauseMessage(e),
                    e
            );
        }
    }

    // =========================================================
    // DUPLICATE WARD NAME
    // =========================================================

    private boolean wardNameExists(
            String wardName,
            String excludedWardId) {

        if (wardName == null
                || wardName.trim().isEmpty()) {

            return false;
        }

        String hospitalId =
                getCurrentHospitalId();

        try {

            /*
             * Only filter by hospitalId.
             * Active status is checked in Java.
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

                String existingId =
                        document.getId();

                if (excludedWardId != null
                        && excludedWardId.trim()
                                .equals(existingId)) {

                    continue;
                }

                String existingName =
                        document.getString(
                                "name"
                        );

                if (existingName != null
                        && existingName.trim()
                                .equalsIgnoreCase(
                                        wardName.trim()
                                )) {

                    return true;
                }
            }

            return false;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to check duplicate ward name: "
                            + getRootCauseMessage(e),
                    e
            );
        }
    }

    // =========================================================
    // VALIDATION
    // =========================================================

    private void validateWard(
            HospitalWard ward) {

        if (ward == null) {

            throw new IllegalArgumentException(
                    "Ward cannot be null."
            );
        }

        if (ward.getName() == null
                || ward.getName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Ward name is required."
            );
        }

        if (ward.getCategory() == null
                || ward.getCategory().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Ward category is required."
            );
        }

        if (ward.getCapacity() <= 0) {

            throw new IllegalArgumentException(
                    "Ward capacity must be greater than zero."
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