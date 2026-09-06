package com.healthsphere.dao.medicaltourism;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;

import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.TravelSupportRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TravelSupportDAO {

    private final Firestore db;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TravelSupportDAO() {
        this.db = FirebaseConfig.getFirestore();
    }

    public TravelSupportRequest createRequest(TravelSupportRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Travel Support Request cannot be null.");
        }

        try {
            if (request.getRequestId() == null || request.getRequestId().isBlank()) {
                request.setRequestId(UUID.randomUUID().toString());
            }

            String nowStr = LocalDateTime.now().format(FORMATTER);
            if (request.getRequestDate() == null || request.getRequestDate().isBlank()) {
                request.setRequestDate(nowStr);
            }
            request.setUpdatedAt(nowStr);

            if (request.getStatus() == null || request.getStatus().isBlank()) {
                request.setStatus("PENDING");
            }

            DocumentReference doc = db.collection("travel_support_requests").document(request.getRequestId());
            doc.set(request).get();

            System.out.println("Travel Support Request created successfully: " + request.getRequestId());
            return request;

        } catch (Exception e) {
            throw new DatabaseException("Unable to submit Travel Support Request.", e);
        }
    }

    public List<TravelSupportRequest> getPatientRequests(String patientId) {
        if (patientId == null || patientId.isBlank()) {
            return new ArrayList<>();
        }
        try {
            Query query = db.collection("travel_support_requests")
                    .whereEqualTo("patientId", patientId);

            QuerySnapshot snapshot = query.get().get();
            List<TravelSupportRequest> requests = new ArrayList<>();
            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                TravelSupportRequest req = doc.toObject(TravelSupportRequest.class);
                if (req != null) {
                    if (req.getRequestId() == null || req.getRequestId().isBlank()) {
                        req.setRequestId(doc.getId());
                    }
                    requests.add(req);
                }
            }
            return requests;
        } catch (Exception e) {
            throw new DatabaseException("Unable to fetch patient Travel Support Requests.", e);
        }
    }

    public List<TravelSupportRequest> getHospitalRequests(String hospitalId) {
        if (hospitalId == null || hospitalId.isBlank()) {
            return new ArrayList<>();
        }
        try {
            Query query = db.collection("travel_support_requests")
                    .whereEqualTo("hospitalId", hospitalId);

            QuerySnapshot snapshot = query.get().get();
            List<TravelSupportRequest> requests = new ArrayList<>();
            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                TravelSupportRequest req = doc.toObject(TravelSupportRequest.class);
                if (req != null) {
                    if (req.getRequestId() == null || req.getRequestId().isBlank()) {
                        req.setRequestId(doc.getId());
                    }
                    requests.add(req);
                }
            }
            return requests;
        } catch (Exception e) {
            throw new DatabaseException("Unable to fetch hospital Travel Support Requests.", e);
        }
    }

    public TravelSupportRequest getRequestById(String requestId) {
        if (requestId == null || requestId.isBlank()) {
            return null;
        }
        try {
            DocumentSnapshot doc = db.collection("travel_support_requests").document(requestId).get().get();
            if (!doc.exists()) {
                return null;
            }
            TravelSupportRequest req = doc.toObject(TravelSupportRequest.class);
            if (req != null && (req.getRequestId() == null || req.getRequestId().isBlank())) {
                req.setRequestId(doc.getId());
            }
            return req;
        } catch (Exception e) {
            throw new DatabaseException("Unable to fetch Travel Support Request by ID.", e);
        }
    }

    public void updateRequestStatus(String requestId, String status, String hospitalNotes) {
        if (requestId == null || requestId.isBlank()) {
            throw new IllegalArgumentException("Request ID cannot be empty.");
        }
        try {
            String nowStr = LocalDateTime.now().format(FORMATTER);
            DocumentReference doc = db.collection("travel_support_requests").document(requestId);
            doc.update("status", status, "hospitalNotes", hospitalNotes != null ? hospitalNotes : "", "updatedAt", nowStr).get();
        } catch (Exception e) {
            throw new DatabaseException("Unable to update Travel Support Request status.", e);
        }
    }
}
