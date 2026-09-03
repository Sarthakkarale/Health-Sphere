package com.healthsphere.dao.admin;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteBatch;

import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.model.HospitalVerification;

import java.time.Instant;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HospitalVerificationDAO {

    private static final String COLLECTION_NAME = "hospital_verifications";
    private static final String USERS_COLLECTION = "users";

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

        if (verification.getHospitalId() == null
                || verification.getHospitalId().isBlank()) {
            throw new IllegalArgumentException(
                    "Hospital ID cannot be empty."
            );
        }

        /*
         * If the caller does not provide an ID, use the hospital UID.
         * This is intentional: one hospital should have one predictable
         * verification document, making admin approval reliable.
         */
        String documentId = verification.getVerificationId();

        if (documentId == null || documentId.isBlank()) {
            documentId = verification.getHospitalId();
            verification.setVerificationId(documentId);
        }

        DocumentReference document =
                firestore
                        .collection(COLLECTION_NAME)
                        .document(documentId);

        Instant now = Instant.now();

        if (verification.getCreatedAt() == null) {
            verification.setCreatedAt(now);
        }

        verification.setUpdatedAt(now);

        document.set(toMap(verification)).get();

        System.out.println(
                "Hospital verification created/updated: " + documentId
        );

        return documentId;
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

        /*
         * Normal case:
         * verificationId == hospital UID == document ID.
         *
         * Compatibility case:
         * older verification records may have a random document ID.
         * In that case, use the supplied ID as hospitalId and find the
         * actual verification document by hospitalId.
         */
        DocumentReference verificationDocument =
                resolveVerificationDocument(verificationId);

        if (verificationDocument == null) {
            throw new IllegalStateException(
                    "No verification record was found for hospital ID: "
                            + verificationId
            );
        }

        DocumentSnapshot verificationSnapshot =
                verificationDocument.get().get();

        String hospitalId =
                verificationSnapshot.getString("hospitalId");

        if (hospitalId == null || hospitalId.isBlank()) {
            // For new records the verification document ID is the UID.
            hospitalId = verificationId;
        }

        Map<String, Object> verificationUpdates =
                new HashMap<>();

        verificationUpdates.put(
                "verificationStatus",
                "APPROVED"
        );

        verificationUpdates.put(
                "documentStatus",
                "VERIFIED"
        );

        verificationUpdates.put(
                "aiOcrResult",
                "VERIFIED"
        );

        verificationUpdates.put(
                "verifiedBy",
                adminUid
        );

        verificationUpdates.put(
                "rejectionReason",
                null
        );

        verificationUpdates.put(
                "updatedAt",
                Date.from(Instant.now())
        );

        /*
         * Use one batch so the verification result and user account
         * status are changed together.
         */
        WriteBatch batch = firestore.batch();

        batch.update(
                verificationDocument,
                verificationUpdates
        );

        DocumentReference userDocument =
                firestore
                        .collection(USERS_COLLECTION)
                        .document(hospitalId);

        batch.update(
                userDocument,
                "status",
                "ACTIVE"
        );

        batch.commit().get();

        System.out.println(
                "Hospital approved successfully. " +
                "Hospital UID=" + hospitalId +
                ", Verification Doc=" + verificationDocument.getId()
        );

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

        DocumentReference verificationDocument =
                resolveVerificationDocument(verificationId);

        if (verificationDocument == null) {
            throw new IllegalStateException(
                    "No verification record was found for hospital ID: "
                            + verificationId
            );
        }

        DocumentSnapshot verificationSnapshot =
                verificationDocument.get().get();

        String hospitalId =
                verificationSnapshot.getString("hospitalId");

        if (hospitalId == null || hospitalId.isBlank()) {
            hospitalId = verificationId;
        }

        Map<String, Object> verificationUpdates =
                new HashMap<>();

        verificationUpdates.put(
                "verificationStatus",
                "REJECTED"
        );

        verificationUpdates.put(
                "documentStatus",
                "REJECTED"
        );

        verificationUpdates.put(
                "aiOcrResult",
                "REJECTED"
        );

        verificationUpdates.put(
                "rejectionReason",
                rejectionReason.trim()
        );

        verificationUpdates.put(
                "verifiedBy",
                adminUid
        );

        verificationUpdates.put(
                "updatedAt",
                Date.from(Instant.now())
        );

        WriteBatch batch = firestore.batch();

        batch.update(
                verificationDocument,
                verificationUpdates
        );

        DocumentReference userDocument =
                firestore
                        .collection(USERS_COLLECTION)
                        .document(hospitalId);

        batch.update(
                userDocument,
                "status",
                "REJECTED"
        );

        batch.commit().get();

        System.out.println(
                "Hospital verification rejected. Hospital UID="
                        + hospitalId
        );

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

        document.delete().get();

        return true;
    }

    // =========================================================
    // RESOLVE VERIFICATION DOCUMENT
    // =========================================================

    private DocumentReference resolveVerificationDocument(
            String id) throws Exception {

        validateId(id);

        // 1. First try document ID.
        DocumentReference directDocument =
                firestore
                        .collection(COLLECTION_NAME)
                        .document(id);

        DocumentSnapshot directSnapshot =
                directDocument.get().get();

        if (directSnapshot.exists()) {
            return directDocument;
        }

        // 2. Compatibility fallback: find by hospitalId.
        QuerySnapshot querySnapshot =
                firestore
                        .collection(COLLECTION_NAME)
                        .whereEqualTo(
                                "hospitalId",
                                id
                        )
                        .limit(1)
                        .get()
                        .get();

        if (!querySnapshot.isEmpty()) {
            return querySnapshot
                    .getDocuments()
                    .get(0)
                    .getReference();
        }

        // 3. Existing-hospital compatibility fix.
        // If the hospital was registered before the verification
        // integration was added, create its missing PENDING record now.
        DocumentSnapshot hospitalSnapshot =
                firestore
                        .collection("hospitals")
                        .document(id)
                        .get()
                        .get();

        if (!hospitalSnapshot.exists()) {
            return null;
        }

        String hospitalName =
                hospitalSnapshot.getString("hospitalName");

        String registrationNumber =
                hospitalSnapshot.getString("registrationNumber");

        String email =
                hospitalSnapshot.getString("email");

        DocumentReference newVerification =
                firestore
                        .collection(COLLECTION_NAME)
                        .document(id);

        Instant now = Instant.now();

        Map<String, Object> pendingData =
                new HashMap<>();

        pendingData.put("verificationId", id);
        pendingData.put("hospitalId", id);
        pendingData.put("hospitalName",
                hospitalName == null ? "Unknown Hospital" : hospitalName);
        pendingData.put("nabhLicenseNumber",
                registrationNumber == null ? "N/A" : registrationNumber);
        pendingData.put("aiOcrMatchScore", 0.0);
        pendingData.put("aiOcrResult", "PENDING");
        pendingData.put("verificationStatus", "PENDING");
        pendingData.put("documentStatus", "PENDING");
        pendingData.put("rejectionReason", null);
        pendingData.put("verifiedBy", null);
        pendingData.put(
                "createdAt",
                Date.from(now)
        );
        pendingData.put(
                "updatedAt",
                Date.from(now)
        );

        newVerification.set(pendingData).get();

        System.out.println(
                "Created missing hospital verification record for UID="
                        + id
                        + ", email="
                        + email
        );

        return newVerification;
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
                    Date.from(
                            verification.getCreatedAt()
                    )
            );
        }

        if (verification.getUpdatedAt() != null) {

            data.put(
                    "updatedAt",
                    Date.from(
                            verification.getUpdatedAt()
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

        String verificationId =
                document.getString("verificationId");

        // Older records may not contain verificationId.
        if (verificationId == null || verificationId.isBlank()) {
            verificationId = document.getId();
        }

        verification.setVerificationId(
                verificationId
        );

        verification.setHospitalId(
                document.getString("hospitalId")
        );

        verification.setHospitalName(
                document.getString("hospitalName")
        );

        verification.setNabhLicenseNumber(
                document.getString("nabhLicenseNumber")
        );

        Double score =
                document.getDouble("aiOcrMatchScore");

        if (score != null) {
            verification.setAiOcrMatchScore(score);
        }

        verification.setAiOcrResult(
                document.getString("aiOcrResult")
        );

        verification.setVerificationStatus(
                document.getString("verificationStatus")
        );

        verification.setDocumentStatus(
                document.getString("documentStatus")
        );

        verification.setRejectionReason(
                document.getString("rejectionReason")
        );

        verification.setVerifiedBy(
                document.getString("verifiedBy")
        );

        Date createdAt =
                document.getDate("createdAt");

        if (createdAt != null) {

            verification.setCreatedAt(
                    createdAt.toInstant()
            );
        }

        Date updatedAt =
                document.getDate("updatedAt");

        if (updatedAt != null) {

            verification.setUpdatedAt(
                    updatedAt.toInstant()
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
