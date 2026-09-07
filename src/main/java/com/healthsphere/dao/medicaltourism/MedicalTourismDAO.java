package com.healthsphere.dao.medicaltourism;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;

import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.MedicalTourismRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MedicalTourismDAO {

    private final Firestore db;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public MedicalTourismDAO() {
        this.db = FirebaseConfig.getFirestore();
    }

    public MedicalTourismRequest createRequest(MedicalTourismRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Medical Tourism Request cannot be null.");
        }

        try {
            // Prevent duplicate active requests for patient + hospital + treatment + preferredDate
            if (hasActiveDuplicate(request.getPatientId(), request.getHospitalId(), request.getTreatmentName(), request.getPreferredDate())) {
                throw new IllegalStateException("You already have an active medical tourism request for this hospital and treatment date.");
            }

            if (request.getRequestId() == null || request.getRequestId().isBlank()) {
                request.setRequestId(UUID.randomUUID().toString());
            }

            if (request.getRequestDate() == null || request.getRequestDate().isBlank()) {
                request.setRequestDate(LocalDateTime.now().format(FORMATTER));
            }

            if (request.getStatus() == null || request.getStatus().isBlank()) {
                request.setStatus("PENDING");
            }

            DocumentReference doc = db.collection("medical_tourism_requests").document(request.getRequestId());
            doc.set(request).get();

            System.out.println("Medical Tourism Request created successfully: " + request.getRequestId());
            return request;

        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Unable to submit Medical Tourism Request.", e);
        }
    }

    public boolean hasActiveDuplicate(String patientId, String hospitalId, String treatmentName, String preferredDate) {
        if (patientId == null || hospitalId == null) {
            return false;
        }
        try {
            Query query = db.collection("medical_tourism_requests")
                    .whereEqualTo("patientId", patientId)
                    .whereEqualTo("hospitalId", hospitalId);

            QuerySnapshot snapshot = query.get().get();
            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                MedicalTourismRequest req = doc.toObject(MedicalTourismRequest.class);
                if (req != null && !"REJECTED".equalsIgnoreCase(req.getStatus()) && !"TREATMENT_COMPLETED".equalsIgnoreCase(req.getStatus())) {
                    if (treatmentName != null && treatmentName.equalsIgnoreCase(req.getTreatmentName())
                            && preferredDate != null && preferredDate.equalsIgnoreCase(req.getPreferredDate())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking duplicate medical tourism request: " + e.getMessage());
        }
        return false;
    }

    public List<MedicalTourismRequest> getPatientRequests(String patientId) {
        if (patientId == null || patientId.isBlank()) {
            return new ArrayList<>();
        }
        try {
            Query query = db.collection("medical_tourism_requests")
                    .whereEqualTo("patientId", patientId);

            QuerySnapshot snapshot = query.get().get();
            List<MedicalTourismRequest> requests = new ArrayList<>();
            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                MedicalTourismRequest req = doc.toObject(MedicalTourismRequest.class);
                if (req != null) {
                    if (req.getRequestId() == null || req.getRequestId().isBlank()) {
                        req.setRequestId(doc.getId());
                    }
                    requests.add(req);
                }
            }
            return requests;
        } catch (Exception e) {
            throw new DatabaseException("Unable to fetch patient Medical Tourism Requests.", e);
        }
    }

    public List<MedicalTourismRequest> getHospitalRequests(String hospitalId) {
        if (hospitalId == null || hospitalId.isBlank()) {
            return new ArrayList<>();
        }
        try {
            Query query = db.collection("medical_tourism_requests")
                    .whereEqualTo("hospitalId", hospitalId);

            QuerySnapshot snapshot = query.get().get();
            List<MedicalTourismRequest> requests = new ArrayList<>();
            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                MedicalTourismRequest req = doc.toObject(MedicalTourismRequest.class);
                if (req != null) {
                    if (req.getRequestId() == null || req.getRequestId().isBlank()) {
                        req.setRequestId(doc.getId());
                    }
                    requests.add(req);
                }
            }
            return requests;
        } catch (Exception e) {
            throw new DatabaseException("Unable to fetch hospital Medical Tourism Requests.", e);
        }
    }

    public List<MedicalTourismRequest> getDoctorRequests(String doctorId) {
        if (doctorId == null || doctorId.isBlank()) {
            return new ArrayList<>();
        }
        try {
            Query query = db.collection("medical_tourism_requests")
                    .whereEqualTo("doctorId", doctorId);

            QuerySnapshot snapshot = query.get().get();
            List<MedicalTourismRequest> requests = new ArrayList<>();
            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                MedicalTourismRequest req = doc.toObject(MedicalTourismRequest.class);
                if (req != null) {
                    if (req.getRequestId() == null || req.getRequestId().isBlank()) {
                        req.setRequestId(doc.getId());
                    }
                    requests.add(req);
                }
            }
            return requests;
        } catch (Exception e) {
            throw new DatabaseException("Unable to fetch doctor Medical Tourism Requests.", e);
        }
    }

    public MedicalTourismRequest getRequestById(String requestId) {
        if (requestId == null || requestId.isBlank()) {
            return null;
        }
        try {
            DocumentSnapshot doc = db.collection("medical_tourism_requests").document(requestId).get().get();
            if (!doc.exists()) {
                return null;
            }
            MedicalTourismRequest req = doc.toObject(MedicalTourismRequest.class);
            if (req != null && (req.getRequestId() == null || req.getRequestId().isBlank())) {
                req.setRequestId(doc.getId());
            }
            return req;
        } catch (Exception e) {
            throw new DatabaseException("Unable to fetch Medical Tourism Request by ID.", e);
        }
    }

    public void updateRequestStatus(String requestId, String status, String hospitalNotes) {
        if (requestId == null || requestId.isBlank()) {
            throw new IllegalArgumentException("Request ID cannot be empty.");
        }
        try {
            DocumentReference doc = db.collection("medical_tourism_requests").document(requestId);
            doc.update("status", status, "hospitalNotes", hospitalNotes != null ? hospitalNotes : "").get();
        } catch (Exception e) {
            throw new DatabaseException("Unable to update Medical Tourism Request status.", e);
        }
    }

    public void linkAppointment(String requestId, String appointmentId) {
        if (requestId == null || requestId.isBlank()) {
            throw new IllegalArgumentException("Request ID cannot be empty.");
        }
        try {
            DocumentReference doc = db.collection("medical_tourism_requests").document(requestId);
            doc.update("appointmentId", appointmentId, "status", "APPOINTMENT_SCHEDULED").get();
        } catch (Exception e) {
            throw new DatabaseException("Unable to link appointment to Medical Tourism Request.", e);
        }
    }
}
