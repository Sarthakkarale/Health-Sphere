package com.healthsphere.controller.admin;

import com.healthsphere.dao.admin.ComplaintDAO;

import java.util.List;
import java.util.Map;

public class ComplaintController {

    private final ComplaintDAO complaintDAO;

    public ComplaintController() {

        this.complaintDAO =
                new ComplaintDAO();
    }

    // ============================================================
    // GET ALL COMPLAINTS
    // ============================================================

    public List<Map<String, Object>>
    getAllComplaints() {

        try {

            return complaintDAO
                    .getAllComplaints();

        } catch (Exception e) {

            System.err.println(
                    "Failed to load complaints: "
                            + e.getMessage()
            );

            return List.of();
        }
    }

    // ============================================================
    // CREATE COMPLAINT
    // ============================================================

    public boolean createComplaint(
            String ticketId,
            Map<String, Object> complaintData) {

        try {

            return complaintDAO
                    .createComplaint(
                            ticketId,
                            complaintData
                    );

        } catch (Exception e) {

            System.err.println(
                    "Failed to create complaint: "
                            + e.getMessage()
            );

            return false;
        }
    }

    // ============================================================
    // UPDATE STATUS
    // ============================================================

    public boolean updateStatus(
            String ticketId,
            String status) {

        try {

            return complaintDAO
                    .updateStatus(
                            ticketId,
                            status
                    );

        } catch (Exception e) {

            System.err.println(
                    "Failed to update complaint status: "
                            + e.getMessage()
            );

            return false;
        }
    }
}