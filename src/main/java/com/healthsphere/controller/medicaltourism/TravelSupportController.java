package com.healthsphere.controller.medicaltourism;

import com.healthsphere.dao.medicaltourism.TravelSupportDAO;
import com.healthsphere.dao.patient.NotificationDAO;
import com.healthsphere.model.Notification;
import com.healthsphere.model.TravelSupportRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class TravelSupportController {

    private final TravelSupportDAO travelSupportDAO;
    private final NotificationDAO notificationDAO;

    public TravelSupportController() {
        this.travelSupportDAO = new TravelSupportDAO();
        this.notificationDAO = new NotificationDAO();
    }

    public TravelSupportRequest submitRequest(TravelSupportRequest request) {
        return travelSupportDAO.createRequest(request);
    }

    public List<TravelSupportRequest> getPatientRequests(String patientId) {
        return travelSupportDAO.getPatientRequests(patientId);
    }

    public List<TravelSupportRequest> getHospitalRequests(String hospitalId) {
        return travelSupportDAO.getHospitalRequests(hospitalId);
    }

    public TravelSupportRequest getRequestById(String requestId) {
        return travelSupportDAO.getRequestById(requestId);
    }

    public void updateStatusAndNotify(String requestId, String status, String hospitalNotes) {
        TravelSupportRequest req = travelSupportDAO.getRequestById(requestId);
        travelSupportDAO.updateRequestStatus(requestId, status, hospitalNotes);

        if (req != null && req.getPatientId() != null && !req.getPatientId().isBlank()) {
            String title = "Travel Support Update";
            String description = "Your Travel Support Request for " + req.getServiceType() + " has been updated to " + status + ". " + (hospitalNotes != null && !hospitalNotes.isBlank() ? "Note: " + hospitalNotes : "");

            createPatientNotification(req.getPatientId(), title, description, "TRAVEL_SUPPORT");
        }
    }

    private void createPatientNotification(String patientId, String title, String description, String type) {
        try {
            Notification n = new Notification();
            n.setNotificationId(UUID.randomUUID().toString());
            n.setPatientUid(patientId);
            n.setTitle(title);
            n.setDescription(description);
            n.setType(type);
            n.setRead(false);
            n.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            notificationDAO.createNotification(n);
        } catch (Exception e) {
            System.err.println("Could not create notification for patient travel update: " + e.getMessage());
        }
    }
}
