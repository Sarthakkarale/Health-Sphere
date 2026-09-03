package com.healthsphere.test;

import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.dao.admin.ModerationDAO;
import com.healthsphere.model.ComplaintModel;

public class FirestoreComplaintTest {

    public static void main(String[] args) {

        try {

            // =====================================================
            // INITIALIZE FIREBASE FIRST
            // =====================================================

            FirebaseConfig.initialize();

            // =====================================================
            // CREATE DAO
            // =====================================================

            ModerationDAO moderationDAO =
                    new ModerationDAO();

            // =====================================================
            // CREATE TEST COMPLAINT
            // =====================================================

            ComplaintModel complaint =
                    new ComplaintModel(
                            "CMP-1001",
                            "Doctor",
                            "Doctor arrived late",
                            "Patient reported that the doctor arrived 45 minutes late for the scheduled appointment.",
                            "Rahul Patil",
                            "HIGH",
                            "OPEN",
                            "2026-09-03 14:30"
                    );

            // =====================================================
            // ADD TO FIRESTORE
            // =====================================================

            moderationDAO.createComplaint(complaint);

            System.out.println(
                    "======================================"
            );

            System.out.println(
                    "Complaint added successfully!"
            );

            System.out.println(
                    "Ticket ID: "
                            + complaint.getTicketId()
            );

            System.out.println(
                    "Category: "
                            + complaint.getCategory()
            );

            System.out.println(
                    "Issue: "
                            + complaint.getIssueTitle()
            );

            System.out.println(
                    "Status: "
                            + complaint.getStatus()
            );

            System.out.println(
                    "======================================"
            );

        } catch (Exception e) {

            System.err.println(
                    "Failed to add complaint."
            );

            e.printStackTrace();
        }
    }
}