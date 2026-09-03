package com.healthsphere.dao.admin;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.ComplaintModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModerationDAO {

    private static final String COLLECTION = "complaints";

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Firestore db;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public ModerationDAO() {
        this.db = FirebaseConfig.getFirestore();
    }

    // ============================================================
    // CREATE COMPLAINT
    // ============================================================

    public void createComplaint(ComplaintModel complaint) {

        validateComplaint(complaint);

        String ticketId = complaint.getTicketId().trim();

        try {

            Map<String, Object> data =
                    complaintToMap(complaint);

            db.collection(COLLECTION)
                    .document(ticketId)
                    .set(data)
                    .get();

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to create complaint.",
                    e
            );
        }
    }

    // ============================================================
    // GET ONE COMPLAINT
    // ============================================================

    public ComplaintModel getComplaint(String ticketId) {

        if (isEmpty(ticketId)) {
            return null;
        }

        try {

            DocumentSnapshot document =
                    db.collection(COLLECTION)
                            .document(ticketId.trim())
                            .get()
                            .get();

            if (!document.exists()) {
                return null;
            }

            return documentToComplaint(document);

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve complaint.",
                    e
            );
        }
    }

    // ============================================================
    // GET ALL COMPLAINTS
    // ============================================================

    public List<ComplaintModel> getAllComplaints() {

        try {

            List<ComplaintModel> complaints =
                    new ArrayList<>();

            ApiFuture<QuerySnapshot> future =
                    db.collection(COLLECTION)
                            .get();

            QuerySnapshot snapshot =
                    future.get();

            for (QueryDocumentSnapshot document :
                    snapshot.getDocuments()) {

                try {

                    ComplaintModel complaint =
                            documentToComplaint(document);

                    if (complaint != null) {
                        complaints.add(complaint);
                    }

                } catch (Exception e) {

                    System.err.println(
                            "Skipping invalid complaint document: "
                                    + document.getId()
                                    + " | "
                                    + e.getMessage()
                    );
                }
            }

            return complaints;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve complaints.",
                    e
            );
        }
    }

    // ============================================================
    // UPDATE COMPLETE COMPLAINT
    // ============================================================

    public void updateComplaint(
            ComplaintModel complaint) {

        validateComplaint(complaint);

        String ticketId =
                complaint.getTicketId().trim();

        try {

            Map<String, Object> data =
                    complaintToMap(complaint);

            db.collection(COLLECTION)
                    .document(ticketId)
                    .set(
                            data,
                            SetOptions.merge()
                    )
                    .get();

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to update complaint.",
                    e
            );
        }
    }

    // ============================================================
    // UPDATE STATUS
    // ============================================================

    public void updateComplaintStatus(
            String ticketId,
            String status) {

        if (isEmpty(ticketId)) {

            throw new DatabaseException(
                    "Ticket ID is required.",
                    null
            );
        }

        if (isEmpty(status)) {

            throw new DatabaseException(
                    "Complaint status is required.",
                    null
            );
        }

        String normalizedStatus =
                status.trim().toUpperCase();

        try {

            Map<String, Object> updates =
                    new HashMap<>();

            updates.put(
                    "status",
                    normalizedStatus
            );

            // ----------------------------------------------------
            // RESOLVED DATE
            // ----------------------------------------------------

            if ("RESOLVED".equals(normalizedStatus)) {

                updates.put(
                        "resolvedDate",
                        LocalDateTime.now()
                                .format(DATE_FORMATTER)
                );

            } else {

                updates.put(
                        "resolvedDate",
                        null
                );
            }

            db.collection(COLLECTION)
                    .document(ticketId.trim())
                    .update(updates)
                    .get();

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to update complaint status.",
                    e
            );
        }
    }

    // ============================================================
    // UPDATE PRIORITY
    // ============================================================

    public void updateComplaintPriority(
            String ticketId,
            String priority) {

        if (isEmpty(ticketId)) {

            throw new DatabaseException(
                    "Ticket ID is required.",
                    null
            );
        }

        if (isEmpty(priority)) {

            throw new DatabaseException(
                    "Complaint priority is required.",
                    null
            );
        }

        try {

            Map<String, Object> updates =
                    new HashMap<>();

            updates.put(
                    "priority",
                    priority.trim().toUpperCase()
            );

            db.collection(COLLECTION)
                    .document(ticketId.trim())
                    .update(updates)
                    .get();

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to update complaint priority.",
                    e
            );
        }
    }

    // ============================================================
    // DELETE COMPLAINT
    // ============================================================

    public void deleteComplaint(String ticketId) {

        if (isEmpty(ticketId)) {

            throw new DatabaseException(
                    "Ticket ID is required.",
                    null
            );
        }

        try {

            db.collection(COLLECTION)
                    .document(ticketId.trim())
                    .delete()
                    .get();

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to delete complaint.",
                    e
            );
        }
    }

    // ============================================================
    // MODEL → FIRESTORE MAP
    // ============================================================

    private Map<String, Object> complaintToMap(
            ComplaintModel complaint) {

        Map<String, Object> data =
                new HashMap<>();

        data.put(
                "ticketId",
                safe(complaint.getTicketId()).trim()
        );

        data.put(
                "category",
                safe(complaint.getCategory())
        );

        data.put(
                "issueTitle",
                safe(complaint.getIssueTitle())
        );

        data.put(
                "description",
                safe(complaint.getDescription())
        );

        data.put(
                "complainant",
                safe(complaint.getComplainant())
        );

        data.put(
                "priority",
                safe(complaint.getPriority())
                        .trim()
                        .toUpperCase()
        );

        data.put(
                "status",
                safe(complaint.getStatus())
                        .trim()
                        .toUpperCase()
        );

        data.put(
                "createdDate",
                safe(complaint.getCreatedDate())
        );

        data.put(
                "resolvedDate",
                complaint.getResolvedDate()
        );

        return data;
    }

    // ============================================================
    // FIRESTORE DOCUMENT → MODEL
    // ============================================================

    private ComplaintModel documentToComplaint(
            DocumentSnapshot document) {

        if (document == null ||
                !document.exists()) {

            return null;
        }

        String ticketId =
                getString(document, "ticketId");

        // --------------------------------------------------------
        // FALLBACK TO DOCUMENT ID
        // --------------------------------------------------------

        if (isEmpty(ticketId)) {

            ticketId =
                    document.getId();
        }

        String category =
                getString(document, "category");

        String issueTitle =
                getString(document, "issueTitle");

        String description =
                getString(document, "description");

        String complainant =
                getString(document, "complainant");

        String priority =
                getString(document, "priority");

        String status =
                getString(document, "status");

        String createdDate =
                getString(document, "createdDate");

        String resolvedDate =
                getString(document, "resolvedDate");

        ComplaintModel complaint =
                new ComplaintModel(
                        ticketId,
                        category,
                        issueTitle,
                        description,
                        complainant,
                        priority,
                        status,
                        createdDate
                );

        complaint.setResolvedDate(
                resolvedDate
        );

        return complaint;
    }

    // ============================================================
    // VALIDATE COMPLAINT
    // ============================================================

    private void validateComplaint(
            ComplaintModel complaint) {

        if (complaint == null) {

            throw new DatabaseException(
                    "Complaint data cannot be null.",
                    null
            );
        }

        if (isEmpty(complaint.getTicketId())) {

            throw new DatabaseException(
                    "Ticket ID is required.",
                    null
            );
        }
    }

    // ============================================================
    // SAFE FIRESTORE STRING
    // ============================================================

    private String getString(
            DocumentSnapshot document,
            String field) {

        Object value =
                document.get(field);

        if (value == null) {
            return "";
        }

        return String.valueOf(value);
    }

    // ============================================================
    // SAFE STRING
    // ============================================================

    private String safe(String value) {

        return value == null
                ? ""
                : value;
    }

    // ============================================================
    // EMPTY CHECK
    // ============================================================

    private boolean isEmpty(String value) {

        return value == null ||
                value.trim().isEmpty();
    }
}