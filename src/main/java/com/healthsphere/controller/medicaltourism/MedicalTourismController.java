package com.healthsphere.controller.medicaltourism;

import com.healthsphere.dao.medicaltourism.MedicalTourismDAO;
import com.healthsphere.dao.patient.NotificationDAO;
import com.healthsphere.model.DoctorProfile;
import com.healthsphere.model.HospitalProfile;
import com.healthsphere.model.MedicalTourismRequest;
import com.healthsphere.model.Notification;
import com.healthsphere.util.SessionManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class MedicalTourismController {

    private final MedicalTourismDAO medicalTourismDAO;
    private final NotificationDAO notificationDAO;

    public MedicalTourismController() {
        this.medicalTourismDAO = new MedicalTourismDAO();
        this.notificationDAO = new NotificationDAO();
    }

    public MedicalTourismRequest submitRequest(MedicalTourismRequest request) {
        return medicalTourismDAO.createRequest(request);
    }

    public List<MedicalTourismRequest> getPatientRequests(String patientId) {
        return medicalTourismDAO.getPatientRequests(patientId);
    }

    public List<MedicalTourismRequest> getHospitalRequests(String hospitalId) {
        return medicalTourismDAO.getHospitalRequests(hospitalId);
    }

    public MedicalTourismRequest getRequestById(String requestId) {
        return medicalTourismDAO.getRequestById(requestId);
    }

    public void updateStatusAndNotify(String requestId, String status, String hospitalNotes) {
        MedicalTourismRequest req = medicalTourismDAO.getRequestById(requestId);
        medicalTourismDAO.updateRequestStatus(requestId, status, hospitalNotes);

        if (req != null && req.getPatientId() != null && !req.getPatientId().isBlank()) {
            String title = "Medical Tourism Update";
            String description = "";

            if ("ACCEPTED".equalsIgnoreCase(status)) {
                description = "Your Medical Tourism Request for " + req.getTreatmentName() + " has been ACCEPTED by " + (req.getHospitalName() != null ? req.getHospitalName() : "the hospital") + ".";
            } else if ("REJECTED".equalsIgnoreCase(status)) {
                description = "Your Medical Tourism Request for " + req.getTreatmentName() + " was not accepted by " + (req.getHospitalName() != null ? req.getHospitalName() : "the hospital") + ". " + (hospitalNotes != null && !hospitalNotes.isBlank() ? "Reason: " + hospitalNotes : "");
            } else if ("MORE_INFORMATION_REQUIRED".equalsIgnoreCase(status)) {
                description = (req.getHospitalName() != null ? req.getHospitalName() : "The hospital") + " has requested additional information regarding your Medical Tourism Request. Note: " + (hospitalNotes != null ? hospitalNotes : "");
            } else {
                description = "Status updated to " + status + " for your Medical Tourism Request.";
            }

            createPatientNotification(req.getPatientId(), title, description, "MEDICAL_TOURISM");
        }
    }

    public void linkAppointment(String requestId, String appointmentId) {
        medicalTourismDAO.linkAppointment(requestId, appointmentId);
        MedicalTourismRequest req = medicalTourismDAO.getRequestById(requestId);
        if (req != null && req.getPatientId() != null) {
            createPatientNotification(req.getPatientId(), "Appointment Scheduled", "An appointment (" + appointmentId + ") has been scheduled for your Medical Tourism Request.", "APPOINTMENT");
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
            System.err.println("Could not create notification for patient: " + e.getMessage());
        }
    }

    /**
     * Dynamic Recommendation Scoring Algorithm:
     * Calculates a match percentage out of 100 based on real hospital data:
     * - Average Rating (30% weight)
     * - Location Match (15% weight)
     * - Budget Match (20% weight)
     * - Doctor Availability (20% weight)
     * - Bed Availability (15% weight)
     */
    public int calculateRecommendationScore(HospitalProfile hospital, String preferredLocation, double maxBudget, List<DoctorProfile> hospitalDoctors, double avgRating) {
        if (hospital == null) return 50;

        double ratingScore = 0;
        if (avgRating > 0) {
            ratingScore = (avgRating / 5.0) * 30.0;
        } else {
            ratingScore = 20.0; // neutral starting rating
        }

        double locationScore = 0;
        if (preferredLocation != null && !preferredLocation.isBlank()) {
            String pLoc = preferredLocation.trim().toLowerCase();
            String hAddress = hospital.getAddress() != null ? hospital.getAddress().toLowerCase() : "";
            if (hAddress.contains(pLoc)) {
                locationScore = 15.0;
            } else {
                locationScore = 8.0;
            }
        } else {
            locationScore = 15.0;
        }

        double budgetScore = 20.0; // default full score if max budget unspecified
        if (maxBudget > 0) {
            // Assume typical cost around 1.5L to 3L
            if (maxBudget >= 200000) {
                budgetScore = 20.0;
            } else {
                budgetScore = 12.0;
            }
        }

        double doctorScore = 0;
        if (hospitalDoctors != null && !hospitalDoctors.isEmpty()) {
            doctorScore = 20.0;
        } else {
            doctorScore = 8.0;
        }

        double bedScore = 0;
        String bedsStr = hospital.getBeds();
        if (bedsStr != null && !bedsStr.isBlank()) {
            try {
                int bedsNum = Integer.parseInt(bedsStr.replaceAll("[^0-9]", ""));
                if (bedsNum > 0) {
                    bedScore = 15.0;
                } else {
                    bedScore = 5.0;
                }
            } catch (Exception e) {
                bedScore = 12.0;
            }
        } else {
            bedScore = 10.0;
        }

        int total = (int) Math.round(ratingScore + locationScore + budgetScore + doctorScore + bedScore);
        return Math.min(99, Math.max(60, total));
    }
}
