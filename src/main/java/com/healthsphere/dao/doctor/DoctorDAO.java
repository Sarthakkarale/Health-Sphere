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

    private static volatile List<DoctorProfile> CACHED_ALL_DOCTORS = null;
    private static volatile long LAST_ALL_DOCTORS_TIME = 0;
    private static final java.util.Map<String, DoctorProfile> PROFILE_CACHE = new java.util.concurrent.ConcurrentHashMap<>();
    private static final long CACHE_TTL_MS = 60_000;

    public static void clearCache() {
        CACHED_ALL_DOCTORS = null;
        LAST_ALL_DOCTORS_TIME = 0;
        PROFILE_CACHE.clear();
    }

    public List<DoctorProfile> getAllDoctors() {
        long now = System.currentTimeMillis();
        if (CACHED_ALL_DOCTORS != null && (now - LAST_ALL_DOCTORS_TIME < CACHE_TTL_MS)) {
            return new ArrayList<>(CACHED_ALL_DOCTORS);
        }

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
                        if (doctor.getUid() != null) {
                            PROFILE_CACHE.put(doctor.getUid().trim(), doctor);
                        }
                    }
                }
            }
            CACHED_ALL_DOCTORS = new ArrayList<>(doctors);
            LAST_ALL_DOCTORS_TIME = now;
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
        if (PROFILE_CACHE.containsKey(uid.trim())) {
            return PROFILE_CACHE.get(uid.trim());
        }
        try {
            DocumentSnapshot document = db.collection("doctors").document(uid).get().get();
            if (!document.exists()) {
                return null;
            }
            DoctorProfile doctor = document.toObject(DoctorProfile.class);
            if (doctor != null) {
                if (doctor.getUid() == null || doctor.getUid().isBlank()) {
                    doctor.setUid(document.getId());
                }
                PROFILE_CACHE.put(uid.trim(), doctor);
            }
            return doctor;
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
