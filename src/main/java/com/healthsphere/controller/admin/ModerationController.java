package com.healthsphere.controller.admin;

import com.healthsphere.dao.admin.ModerationDAO;
import com.healthsphere.model.ComplaintModel;

import java.util.List;

public class ModerationController {

    private final ModerationDAO moderationDAO;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public ModerationController() {
        this.moderationDAO = new ModerationDAO();
    }

    // ============================================================
    // CREATE COMPLAINT
    // ============================================================

    public boolean createComplaint(
            ComplaintModel complaint) {

        try {

            moderationDAO.createComplaint(complaint);

            return true;

        } catch (Exception e) {

            System.err.println(
                    "Error creating complaint: "
                            + e.getMessage()
            );

            return false;
        }
    }

    // ============================================================
    // GET ONE COMPLAINT
    // ============================================================

    public ComplaintModel getComplaint(
            String ticketId) {

        try {

            return moderationDAO.getComplaint(
                    ticketId
            );

        } catch (Exception e) {

            System.err.println(
                    "Error retrieving complaint: "
                            + e.getMessage()
            );

            return null;
        }
    }

    // ============================================================
    // GET ALL COMPLAINTS
    // ============================================================

    public List<ComplaintModel> getAllComplaints() {

        try {

            return moderationDAO.getAllComplaints();

        } catch (Exception e) {

            System.err.println(
                    "Error retrieving complaints: "
                            + e.getMessage()
            );

            throw new RuntimeException(
                    "Unable to load complaints.",
                    e
            );
        }
    }

    // ============================================================
    // UPDATE COMPLAINT
    // ============================================================

    public boolean updateComplaint(
            ComplaintModel complaint) {

        try {

            moderationDAO.updateComplaint(
                    complaint
            );

            return true;

        } catch (Exception e) {

            System.err.println(
                    "Error updating complaint: "
                            + e.getMessage()
            );

            return false;
        }
    }

    // ============================================================
    // UPDATE STATUS
    // ============================================================

    public boolean updateComplaintStatus(
            String ticketId,
            String status) {

        try {

            moderationDAO.updateComplaintStatus(
                    ticketId,
                    status
            );

            return true;

        } catch (Exception e) {

            System.err.println(
                    "Error updating complaint status: "
                            + e.getMessage()
            );

            return false;
        }
    }

    // ============================================================
    // UPDATE PRIORITY
    // ============================================================

    public boolean updateComplaintPriority(
            String ticketId,
            String priority) {

        try {

            moderationDAO.updateComplaintPriority(
                    ticketId,
                    priority
            );

            return true;

        } catch (Exception e) {

            System.err.println(
                    "Error updating complaint priority: "
                            + e.getMessage()
            );

            return false;
        }
    }

    // ============================================================
    // DELETE COMPLAINT
    // ============================================================

    public boolean deleteComplaint(
            String ticketId) {

        try {

            moderationDAO.deleteComplaint(
                    ticketId
            );

            return true;

        } catch (Exception e) {

            System.err.println(
                    "Error deleting complaint: "
                            + e.getMessage()
            );

            return false;
        }
    }
}