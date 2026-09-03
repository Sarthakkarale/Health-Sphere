package com.healthsphere.dao.doctor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.DoctorProfile;

public class DoctorDAO {

    private final Firestore db;

    public DoctorDAO() {
        this.db = FirebaseConfig.getFirestore();
    }

    public List<DoctorProfile> getAllDoctors() {
        List<DoctorProfile> doctors = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection("doctors").get();
            QuerySnapshot snapshot = future.get();
            for (DocumentSnapshot document : snapshot.getDocuments()) {
                if (document.exists()) {
                    DoctorProfile doctor = document.toObject(DoctorProfile.class);
                    if (doctor != null) {
                        if (doctor.getUid() == null || doctor.getUid().isBlank()) {
                            doctor.setUid(document.getId());
                        }
                        doctors.add(doctor);
                    }
                }
            }
            return doctors;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while retrieving doctors.", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to retrieve doctors.", e);
        }
    }

    public DoctorProfile getDoctorProfile(String uid) {
        return getDoctorById(uid);
    }

    public DoctorProfile getDoctorById(String uid) {
        if (uid == null || uid.isBlank()) {
            return null;
        }
        try {
            DocumentSnapshot document = db.collection("doctors").document(uid).get().get();
            if (!document.exists()) {
                return null;
            }
            DoctorProfile profile = document.toObject(DoctorProfile.class);
            if (profile != null && (profile.getUid() == null || profile.getUid().isBlank())) {
                profile.setUid(document.getId());
            }
            return profile;
        } catch (Exception e) {
            throw new DatabaseException("Unable to retrieve doctor profile.", e);
        }
    }

    public void updateDoctorProfile(DoctorProfile doctorProfile) {
        if (doctorProfile == null) {
            throw new IllegalArgumentException("Doctor profile cannot be null.");
        }
        if (doctorProfile.getUid() == null || doctorProfile.getUid().isBlank()) {
            throw new IllegalArgumentException("Doctor UID cannot be null or empty.");
        }
        try {
            db.collection("doctors").document(doctorProfile.getUid()).set(doctorProfile).get();
        } catch (Exception e) {
            throw new DatabaseException("Unable to update doctor profile.", e);
        }
    }
}
