package com.healthsphere.dao.admin;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.Timestamp;

import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.model.HospitalVerification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HospitalVerificationDAO {

    /*
     * Shared Firestore collection.
     *
     * Confirm this name against the team's shared
     * Firestore architecture before final integration.
     */
    private static final String COLLECTION_NAME = "hospital_verifications";

    private final Firestore firestore;

    public HospitalVerificationDAO() {
        this.firestore = FirebaseConfig.getFirestore();
    }

    // =========================================================
    // CREATE
    // =========================================================

    public String createVerification(
            HospitalVerification verification) throws Exception {

        if (verification == null) {
            throw new IllegalArgumentException(
                    "Hospital verification data cannot be null."
            );
        }

        DocumentReference document;

        if (verification.getVerificationId() == null
                || verification.getVerificationId().isBlank()) {

            document = firestore
                    .collection(COLLECTION_NAME)
                    .document();

            verification.setVerificationId(
                    document.getId()
            );

        } else {

            document = firestore
                    .collection(COLLECTION_NAME)
                    .document(
                            verification.getVerificationId()
                    );
        }

        Instant now = Instant.now();

        if (verification.getCreatedAt() == null) {
            verification.setCreatedAt(now);
        }

        verification.setUpdatedAt(now);

        document.set(
                toMap(verification)
        ).get();

        return verification.getVerificationId();
    }

    // =========================================================
    // READ BY ID
    // =========================================================

    public HospitalVerification getVerificationById(
            String verificationId) throws Exception {

        validateId(verificationId);

        DocumentSnapshot snapshot =
                firestore
                        .collection(COLLECTION_NAME)
                        .document(verificationId)
                        .get()
                        .get();

        if (!snapshot.exists()) {
            return null;
        }

        return fromDocument(snapshot);
    }

    // =========================================================
    // READ ALL
    // =========================================================

    public List<HospitalVerification> getAllVerifications()
            throws Exception {

        ApiFuture<QuerySnapshot> future =
                firestore
                        .collection(COLLECTION_NAME)
                        .orderBy(
                                "createdAt",
                                Query.Direction.DESCENDING
                        )
                        .get();

        QuerySnapshot snapshot = future.get();

        List<HospitalVerification> verifications =
                new ArrayList<>();

        for (DocumentSnapshot document :
                snapshot.getDocuments()) {

            HospitalVerification verification =
                    fromDocument(document);

            if (verification != null) {
                verifications.add(verification);
            }
        }

        return verifications;
    }

    // =========================================================
    // READ BY HOSPITAL ID
    // =========================================================

    public HospitalVerification getVerificationByHospitalId(
            String hospitalId) throws Exception {

        validateId(hospitalId);

        QuerySnapshot snapshot =
                firestore
                        .collection(COLLECTION_NAME)
                        .whereEqualTo(
                                "hospitalId",
                                hospitalId
                        )
                        .limit(1)
                        .get()
                        .get();

        if (snapshot.isEmpty()) {
            return null;
        }

        return fromDocument(
                snapshot.getDocuments().get(0)
        );
    }

    // =========================================================
    // READ BY STATUS
    // =========================================================

    public List<HospitalVerification> getVerificationsByStatus(
            String status) throws Exception {

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException(
                    "Verification status cannot be empty."
            );
        }

        QuerySnapshot snapshot =
                firestore
                        .collection(COLLECTION_NAME)
                        .whereEqualTo(
                                "verificationStatus",
                                status
                        )
                        .get()
                        .get();

        List<HospitalVerification> verifications =
                new ArrayList<>();

        for (DocumentSnapshot document :
                snapshot.getDocuments()) {

            HospitalVerification verification =
                    fromDocument(document);

            if (verification != null) {
                verifications.add(verification);
            }
        }

        return verifications;
    }

    // =========================================================
    // UPDATE
    // =========================================================

    public boolean updateVerification(
            HospitalVerification verification) throws Exception {

        if (verification == null) {
            throw new IllegalArgumentException(
                    "Hospital verification data cannot be null."
            );
        }

        validateId(
                verification.getVerificationId()
        );

        verification.setUpdatedAt(
                Instant.now()
        );

        DocumentReference document =
                firestore
                        .collection(COLLECTION_NAME)
                        .document(
                                verification.getVerificationId()
                        );

        /*
         * update() directly avoids an unnecessary
         * existence READ before the database operation.
         *
         * If the document does not exist, Firestore throws
         * an exception which is propagated to the Controller.
         */
        document.update(
                toMap(verification)
        ).get();

        return true;
    }

    // =========================================================
    // APPROVE
    // =========================================================

    public boolean approveVerification(
            String verificationId,
            String adminUid) throws Exception {

        validateId(verificationId);

        if (adminUid == null || adminUid.isBlank()) {
            throw new IllegalArgumentException(
                    "Admin UID cannot be empty."
            );
        }

        DocumentReference document =
                firestore
                        .collection(COLLECTION_NAME)
                        .document(verificationId);

        Map<String, Object> updates =
                new HashMap<>();

        updates.put(
                "verificationStatus",
                "APPROVED"
        );

        updates.put(
                "documentStatus",
                "VERIFIED"
        );

        updates.put(
                "verifiedBy",
                adminUid
        );

        updates.put(
                "rejectionReason",
                null
        );

        updates.put(
                "updatedAt",
                Timestamp.now()
        );

        /*
         * Direct update avoids an unnecessary
         * existence READ.
         */
        document.update(updates).get();

        return true;
    }

    // =========================================================
    // REJECT
    // =========================================================

    public boolean rejectVerification(
            String verificationId,
            String adminUid,
            String rejectionReason) throws Exception {

        validateId(verificationId);

        if (adminUid == null || adminUid.isBlank()) {
            throw new IllegalArgumentException(
                    "Admin UID cannot be empty."
            );
        }

        if (rejectionReason == null
                || rejectionReason.isBlank()) {

            throw new IllegalArgumentException(
                    "Rejection reason cannot be empty."
            );
        }

        DocumentReference document =
                firestore
                        .collection(COLLECTION_NAME)
                        .document(verificationId);

        Map<String, Object> updates =
                new HashMap<>();

        updates.put(
                "verificationStatus",
                "REJECTED"
        );

        updates.put(
                "documentStatus",
                "REJECTED"
        );

        updates.put(
                "rejectionReason",
                rejectionReason
        );

        updates.put(
                "verifiedBy",
                adminUid
        );

        updates.put(
                "updatedAt",
                Timestamp.now()
        );

        /*
         * Direct update avoids an unnecessary
         * existence READ.
         */
        document.update(updates).get();

        return true;
    }

    // =========================================================
    // DELETE
    // =========================================================

    public boolean deleteVerification(
            String verificationId) throws Exception {

        validateId(verificationId);

        DocumentReference document =
                firestore
                        .collection(COLLECTION_NAME)
                        .document(verificationId);

        /*
         * Direct delete avoids an unnecessary READ.
         */
        document.delete().get();

        return true;
    }

    // =========================================================
    // MODEL → FIRESTORE MAP
    // =========================================================

    private Map<String, Object> toMap(
            HospitalVerification verification) {

        Map<String, Object> data =
                new HashMap<>();

        data.put(
                "verificationId",
                verification.getVerificationId()
        );

        data.put(
                "hospitalId",
                verification.getHospitalId()
        );

        data.put(
                "hospitalName",
                verification.getHospitalName()
        );

        data.put(
                "nabhLicenseNumber",
                verification.getNabhLicenseNumber()
        );

        data.put(
                "aiOcrMatchScore",
                verification.getAiOcrMatchScore()
        );

        data.put(
                "aiOcrResult",
                verification.getAiOcrResult()
        );

        data.put(
                "verificationStatus",
                verification.getVerificationStatus()
        );

        data.put(
                "documentStatus",
                verification.getDocumentStatus()
        );

        data.put(
                "rejectionReason",
                verification.getRejectionReason()
        );

        data.put(
                "verifiedBy",
                verification.getVerifiedBy()
        );

        if (verification.getCreatedAt() != null) {

            data.put(
                    "createdAt",
                    Timestamp.ofTimeSecondsAndNanos(
                            verification
                                    .getCreatedAt()
                                    .getEpochSecond(),
                            verification
                                    .getCreatedAt()
                                    .getNano()
                    )
            );
        }

        if (verification.getUpdatedAt() != null) {

            data.put(
                    "updatedAt",
                    Timestamp.ofTimeSecondsAndNanos(
                            verification
                                    .getUpdatedAt()
                                    .getEpochSecond(),
                            verification
                                    .getUpdatedAt()
                                    .getNano()
                    )
            );
        }

        return data;
    }

    // =========================================================
    // FIRESTORE DOCUMENT → MODEL
    // =========================================================

    private HospitalVerification fromDocument(
            DocumentSnapshot document) {

        if (document == null
                || !document.exists()) {

            return null;
        }

        HospitalVerification verification =
                new HospitalVerification();

        verification.setVerificationId(
                document.getString(
                        "verificationId"
                )
        );

        verification.setHospitalId(
                document.getString(
                        "hospitalId"
                )
        );

        verification.setHospitalName(
                document.getString(
                        "hospitalName"
                )
        );

        verification.setNabhLicenseNumber(
                document.getString(
                        "nabhLicenseNumber"
                )
        );

        Double score =
                document.getDouble(
                        "aiOcrMatchScore"
                );

        if (score != null) {
            verification.setAiOcrMatchScore(score);
        }

        verification.setAiOcrResult(
                document.getString(
                        "aiOcrResult"
                )
        );

        verification.setVerificationStatus(
                document.getString(
                        "verificationStatus"
                )
        );

        verification.setDocumentStatus(
                document.getString(
                        "documentStatus"
                )
        );

        verification.setRejectionReason(
                document.getString(
                        "rejectionReason"
                )
        );

        verification.setVerifiedBy(
                document.getString(
                        "verifiedBy"
                )
        );

        Timestamp createdAt =
                document.getTimestamp(
                        "createdAt"
                );

        if (createdAt != null) {

            verification.setCreatedAt(
                    createdAt
                            .toDate()
                            .toInstant()
            );
        }

        Timestamp updatedAt =
                document.getTimestamp(
                        "updatedAt"
                );

        if (updatedAt != null) {

            verification.setUpdatedAt(
                    updatedAt
                            .toDate()
                            .toInstant()
            );
        }

        return verification;
    }

    // =========================================================
    // ID VALIDATION
    // =========================================================

    private void validateId(String id) {

        if (id == null || id.isBlank()) {

            throw new IllegalArgumentException(
                    "ID cannot be null or empty."
            );
        }
    }
}