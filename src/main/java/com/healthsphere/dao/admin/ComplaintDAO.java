package com.healthsphere.dao.admin;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComplaintDAO {

    private final Firestore db;

    public ComplaintDAO() {

        this.db =
                FirebaseConfig.getFirestore();
    }

    // ============================================================
    // CREATE COMPLAINT
    // ============================================================

    public boolean createComplaint(
            String ticketId,
            Map<String, Object> complaintData) {

        try {

            db.collection("complaints")
                    .document(ticketId)
                    .set(complaintData)
                    .get();

            return true;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to create complaint.",
                    e
            );
        }
    }

    // ============================================================
    // GET ALL COMPLAINTS
    // ============================================================

    public List<Map<String, Object>>
    getAllComplaints() {

        try {

            List<Map<String, Object>> complaints =
                    new ArrayList<>();

            for (
                    DocumentSnapshot document :
                    db.collection("complaints")
                            .get()
                            .get()
                            .getDocuments()
            ) {

                if (document.exists()) {

                    Map<String, Object> data =
                            document.getData();

                    if (data != null) {

                        data.put(
                                "documentId",
                                document.getId()
                        );

                        complaints.add(data);
                    }
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
    // UPDATE STATUS
    // ============================================================

    public boolean updateStatus(
            String ticketId,
            String status) {

        try {

            db.collection("complaints")
                    .document(ticketId)
                    .update(
                            "status",
                            status
                    )
                    .get();

            return true;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to update complaint status.",
                    e
            );
        }
    }
}