package com.healthsphere.controller.admin;

import com.healthsphere.dao.admin.ModerationDAO;
import com.healthsphere.exceptions.DatabaseException;
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

            if (complaint == null) {
                throw new DatabaseException(
                        "Complaint data cannot be null.",
                        null
                );
            }

            moderationDAO.createComplaint(
                    complaint
            );

            return true;

        } catch (Exception e) {

            System.err.println(
                    "Create complaint failed: "
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

            if (ticketId == null ||
                    ticketId.trim().isEmpty()) {

                return null;
            }

            return moderationDAO.getComplaint(
                    ticketId.trim()
            );

        } catch (Exception e) {

            System.err.println(
                    "Get complaint failed: "
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

            List<ComplaintModel> complaints =
                    moderationDAO.getAllComplaints();

            if (complaints == null) {
                return List.of();
            }

            return complaints;

        } catch (Exception e) {

            System.err.println(
                    "Get all complaints failed: "
                            + e.getMessage()
            );

            return List.of();
        }
    }

    // ============================================================
    // UPDATE COMPLETE COMPLAINT
    // ============================================================

    public boolean updateComplaint(
            ComplaintModel complaint) {

        try {

            if (complaint == null) {
                throw new DatabaseException(
                        "Complaint data cannot be null.",
                        null
                );
            }

            moderationDAO.updateComplaint(
                    complaint
            );

            return true;

        } catch (Exception e) {

            System.err.println(
                    "Update complaint failed: "
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

            if (ticketId == null ||
                    ticketId.trim().isEmpty()) {

                throw new DatabaseException(
                        "Ticket ID is required.",
                        null
                );
            }

            if (status == null ||
                    status.trim().isEmpty()) {

                throw new DatabaseException(
                        "Complaint status is required.",
                        null
                );
            }

            moderationDAO.updateComplaintStatus(
                    ticketId.trim(),
                    status.trim()
            );

            return true;

        } catch (Exception e) {

            System.err.println(
                    "Update complaint status failed: "
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

            if (ticketId == null ||
                    ticketId.trim().isEmpty()) {

                throw new DatabaseException(
                        "Ticket ID is required.",
                        null
                );
            }

            if (priority == null ||
                    priority.trim().isEmpty()) {

                throw new DatabaseException(
                        "Complaint priority is required.",
                        null
                );
            }

            moderationDAO.updateComplaintPriority(
                    ticketId.trim(),
                    priority.trim()
            );

            return true;

        } catch (Exception e) {

            System.err.println(
                    "Update complaint priority failed: "
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

            if (ticketId == null ||
                    ticketId.trim().isEmpty()) {

                throw new DatabaseException(
                        "Ticket ID is required.",
                        null
                );
            }

            moderationDAO.deleteComplaint(
                    ticketId.trim()
            );

            return true;

        } catch (Exception e) {

            System.err.println(
                    "Delete complaint failed: "
                            + e.getMessage()
            );

            return false;
        }
    }
}