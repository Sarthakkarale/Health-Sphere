package com.healthsphere.dao.admin;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.model.ComplaintModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModerationDAO {

    private static final String COLLECTION_NAME = "complaints";

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Firestore firestore;

    public ModerationDAO() {
        this.firestore = FirebaseConfig.getFirestore();
    }

    // ============================================================
    // CREATE COMPLAINT
    // ============================================================

    public void createComplaint(ComplaintModel complaint) {

        validateComplaint(complaint);

        try {

            firestore
                    .collection(COLLECTION_NAME)
                    .document(complaint.getTicketId())
                    .set(complaint)
                    .get();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to create complaint.",
                    e
            );
        }
    }

    // ============================================================
    // GET ONE COMPLAINT
    // ============================================================

    public ComplaintModel getComplaint(String ticketId) {

        validateTicketId(ticketId);

        try {

            DocumentSnapshot document =
                    firestore
                            .collection(COLLECTION_NAME)
                            .document(ticketId)
                            .get()
                            .get();

            if (!document.exists()) {
                return null;
            }

            return document.toObject(
                    ComplaintModel.class
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to retrieve complaint.",
                    e
            );
        }
    }

    // ============================================================
    // GET ALL COMPLAINTS
    // ============================================================

    public List<ComplaintModel> getAllComplaints() {

        try {

            QuerySnapshot snapshot =
                    firestore
                            .collection(COLLECTION_NAME)
                            .get()
                            .get();

            List<ComplaintModel> complaints =
                    new ArrayList<>();

            for (DocumentSnapshot document : snapshot.getDocuments()) {

                if (!document.exists()) {
                    continue;
                }

                ComplaintModel complaint =
                        document.toObject(
                                ComplaintModel.class
                        );

                if (complaint != null) {
                    complaints.add(complaint);
                }
            }

            return complaints;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to retrieve complaints.",
                    e
            );
        }
    }

    // ============================================================
    // UPDATE COMPLETE COMPLAINT
    // ============================================================

    public void updateComplaint(ComplaintModel complaint) {

        validateComplaint(complaint);

        try {

            firestore
                    .collection(COLLECTION_NAME)
                    .document(complaint.getTicketId())
                    .set(complaint)
                    .get();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to update complaint.",
                    e
            );
        }
    }

    // ============================================================
    // UPDATE COMPLAINT STATUS
    // ============================================================

    public void updateComplaintStatus(
            String ticketId,
            String status) {

        validateTicketId(ticketId);

        if (status == null || status.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Complaint status is required."
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

            /*
             * When complaint becomes RESOLVED,
             * store the actual resolution time.
             *
             * When complaint is reopened,
             * clear the previous resolvedDate.
             */

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

            firestore
                    .collection(COLLECTION_NAME)
                    .document(ticketId)
                    .update(updates)
                    .get();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to update complaint status.",
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

        validateTicketId(ticketId);

        if (priority == null || priority.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Complaint priority is required."
            );
        }

        try {

            firestore
                    .collection(COLLECTION_NAME)
                    .document(ticketId)
                    .update(
                            "priority",
                            priority.trim().toUpperCase()
                    )
                    .get();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to update complaint priority.",
                    e
            );
        }
    }

    // ============================================================
    // DELETE COMPLAINT
    // ============================================================

    public void deleteComplaint(String ticketId) {

        validateTicketId(ticketId);

        try {

            firestore
                    .collection(COLLECTION_NAME)
                    .document(ticketId)
                    .delete()
                    .get();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to delete complaint.",
                    e
            );
        }
    }

    // ============================================================
    // VALIDATE COMPLAINT
    // ============================================================

    private void validateComplaint(
            ComplaintModel complaint) {

        if (complaint == null) {

            throw new IllegalArgumentException(
                    "Complaint cannot be null."
            );
        }

        validateTicketId(
                complaint.getTicketId()
        );
    }

    // ============================================================
    // VALIDATE TICKET ID
    // ============================================================

    private void validateTicketId(
            String ticketId) {

        if (ticketId == null ||
                ticketId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Complaint ticket ID is required."
            );
        }
    }
}