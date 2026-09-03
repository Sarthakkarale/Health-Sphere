package com.healthsphere.dao.hospital;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.model.HospitalProfile;
import com.healthsphere.util.SessionManager;

public class HospitalDAO {

    private final Firestore db;

    public HospitalDAO() {
        this.db = FirebaseConfig.getFirestore();
    }

    private String getCurrentHospitalUid() {
        if (SessionManager.getCurrentUser() == null) {
            throw new IllegalStateException("No hospital user is currently logged in.");
        }
        return SessionManager.getCurrentUser().getUid();
    }

    public HospitalProfile getHospitalProfile() {
        String uid = getCurrentHospitalUid();
        try {
            DocumentSnapshot document = db.collection("hospitals").document(uid).get().get();
            if (!document.exists()) {
                return null;
            }
            HospitalProfile hospital = document.toObject(HospitalProfile.class);
            if (hospital != null && (hospital.getUid() == null || hospital.getUid().isBlank())) {
                hospital.setUid(document.getId());
            }
            return hospital;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while retrieving hospital profile.", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to retrieve hospital profile.", e);
        }
    }

    public List<HospitalProfile> getAllHospitals() {
        List<HospitalProfile> hospitals = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection("hospitals").get();
            QuerySnapshot snapshot = future.get();
            for (DocumentSnapshot document : snapshot.getDocuments()) {
                if (document.exists()) {
                    HospitalProfile hospital = document.toObject(HospitalProfile.class);
                    if (hospital != null) {
                        if (hospital.getUid() == null || hospital.getUid().isBlank()) {
                            hospital.setUid(document.getId());
                        }
                        hospitals.add(hospital);
                    }
                }
            }
            return hospitals;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while retrieving hospitals.", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to retrieve hospitals.", e);
        }
    }

    public HospitalProfile getHospitalById(String uid) {
        if (uid == null || uid.isBlank()) {
            return null;
        }
        try {
            DocumentSnapshot document = db.collection("hospitals").document(uid).get().get();
            if (!document.exists()) {
                return null;
            }
            HospitalProfile hospital = document.toObject(HospitalProfile.class);
            if (hospital != null && (hospital.getUid() == null || hospital.getUid().isBlank())) {
                hospital.setUid(document.getId());
            }
            return hospital;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve hospital.", e);
        }
    }

    public void saveHospitalProfile(HospitalProfile hospitalProfile) {
        if (hospitalProfile == null) {
            throw new IllegalArgumentException("Hospital profile cannot be null.");
        }
        String uid = getCurrentHospitalUid();
        try {
            DocumentReference reference = db.collection("hospitals").document(uid);
            ApiFuture<WriteResult> future = reference.set(hospitalProfile);
            future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while saving hospital profile.", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to save hospital profile.", e);
        }
    }

    public void updateHospitalProfile(HospitalProfile hospitalProfile) {
        if (hospitalProfile == null) {
            throw new IllegalArgumentException("Hospital profile cannot be null.");
        }
        String uid = getCurrentHospitalUid();
        try {
            db.collection("hospitals").document(uid).set(hospitalProfile).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while updating hospital profile.", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to update hospital profile.", e);
        }
    }

    public boolean hospitalProfileExists() {
        String uid = getCurrentHospitalUid();
        try {
            DocumentSnapshot document = db.collection("hospitals").document(uid).get().get();
            return document.exists();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while checking hospital profile.", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to check hospital profile.", e);
        }
    }
}
