package com.healthsphere.dao.admin;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.model.ComplaintModel;

import java.util.ArrayList;
import java.util.List;

public class ComplaintDAO {

    private static final String COLLECTION_NAME = "complaints";

    private final Firestore firestore;

    public ComplaintDAO() {
        this.firestore = FirebaseConfig.getFirestore();
    }

    /**
     * Create a complaint.
     */
    public void createComplaint(ComplaintModel complaint) {

        if (complaint == null) {
            throw new IllegalArgumentException(
                    "Complaint cannot be null."
            );
        }

        if (complaint.getTicketId() == null ||
                complaint.getTicketId().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Ticket ID is required."
            );
        }

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

    /**
     * Get a complaint by ticket ID.
     */
    public ComplaintModel getComplaint(String ticketId) {

        if (ticketId == null ||
                ticketId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Ticket ID is required."
            );
        }

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

    /**
     * Get all complaints.
     */
    public List<ComplaintModel> getAllComplaints() {

        try {
            QuerySnapshot snapshot =
                    firestore
                            .collection(COLLECTION_NAME)
                            .get()
                            .get();

            List<ComplaintModel> complaints =
                    new ArrayList<>();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                if (document.exists()) {

                    ComplaintModel complaint =
                            document.toObject(
                                    ComplaintModel.class
                            );

                    if (complaint != null) {
                        complaints.add(complaint);
                    }
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

    /**
     * Update an existing complaint.
     */
    public void updateComplaint(
            ComplaintModel complaint) {

        if (complaint == null) {
            throw new IllegalArgumentException(
                    "Complaint cannot be null."
            );
        }

        if (complaint.getTicketId() == null ||
                complaint.getTicketId().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Ticket ID is required."
            );
        }

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

    /**
     * Delete a complaint.
     */
    public void deleteComplaint(String ticketId) {

        if (ticketId == null ||
                ticketId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Ticket ID is required."
            );
        }

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
}