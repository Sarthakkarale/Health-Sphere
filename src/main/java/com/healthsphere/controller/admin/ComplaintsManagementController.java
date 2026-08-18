package com.healthsphere.controller.admin;

import com.healthsphere.dao.admin.ComplaintDAO;
import com.healthsphere.model.ComplaintModel;

import java.util.List;

public class ComplaintsManagementController {

    private final ComplaintDAO complaintDAO;

    public ComplaintsManagementController() {
        this.complaintDAO = new ComplaintDAO();
    }

    // ============================================================
    // CREATE
    // ============================================================

    public void createComplaint(ComplaintModel complaint) {

        validateComplaint(complaint);

        complaintDAO.createComplaint(complaint);
    }

    // ============================================================
    // READ ONE
    // ============================================================

    public ComplaintModel getComplaint(String ticketId) {

        validateTicketId(ticketId);

        return complaintDAO.getComplaint(ticketId);
    }

    // ============================================================
    // READ ALL
    // ============================================================

    public List<ComplaintModel> getAllComplaints() {

        return complaintDAO.getAllComplaints();
    }

    // ============================================================
    // UPDATE
    // ============================================================

    public void updateComplaint(ComplaintModel complaint) {

        validateComplaint(complaint);

        complaintDAO.updateComplaint(complaint);
    }

    // ============================================================
    // UPDATE STATUS
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

        ComplaintModel complaint =
                complaintDAO.getComplaint(ticketId);

        if (complaint == null) {
            throw new IllegalArgumentException(
                    "Complaint not found: " + ticketId
            );
        }

        complaint.setStatus(status);

        complaintDAO.updateComplaint(complaint);
    }

    // ============================================================
    // DELETE
    // ============================================================

    public void deleteComplaint(String ticketId) {

        validateTicketId(ticketId);

        complaintDAO.deleteComplaint(ticketId);
    }

    // ============================================================
    // VALIDATION
    // ============================================================

    private void validateComplaint(
            ComplaintModel complaint) {

        if (complaint == null) {
            throw new IllegalArgumentException(
                    "Complaint cannot be null."
            );
        }

        validateTicketId(complaint.getTicketId());

        if (isEmpty(complaint.getCategory())) {
            throw new IllegalArgumentException(
                    "Complaint category is required."
            );
        }

        if (isEmpty(complaint.getIssueTitle())) {
            throw new IllegalArgumentException(
                    "Issue title is required."
            );
        }

        if (isEmpty(complaint.getDescription())) {
            throw new IllegalArgumentException(
                    "Complaint description is required."
            );
        }

        if (isEmpty(complaint.getComplainant())) {
            throw new IllegalArgumentException(
                    "Complainant is required."
            );
        }

        if (isEmpty(complaint.getPriority())) {
            throw new IllegalArgumentException(
                    "Complaint priority is required."
            );
        }

        if (isEmpty(complaint.getStatus())) {
            throw new IllegalArgumentException(
                    "Complaint status is required."
            );
        }

        if (isEmpty(complaint.getCreatedDate())) {
            throw new IllegalArgumentException(
                    "Complaint creation date is required."
            );
        }
    }

    private void validateTicketId(String ticketId) {

        if (ticketId == null ||
                ticketId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Ticket ID is required."
            );
        }
    }

    private boolean isEmpty(String value) {

        return value == null ||
                value.trim().isEmpty();
    }
}