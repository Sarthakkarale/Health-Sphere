package com.healthsphere.controller.admin;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.FirebaseConfig;

import java.util.concurrent.CompletableFuture;

public class AdminDashboardController {

    private static final String HOSPITALS_COLLECTION =
            "hospitals";

    private static final String DOCTORS_COLLECTION =
            "doctors";

    private final Firestore firestore;

    private final HospitalVerificationController
            verificationController;

    public AdminDashboardController() {

        this.firestore =
                FirebaseConfig.getFirestore();

        this.verificationController =
                new HospitalVerificationController();
    }

    /**
     * Load total number of registered hospitals.
     */
    public int getRegisteredHospitalCount()
            throws Exception {

        QuerySnapshot snapshot =
                firestore
                        .collection(
                                HOSPITALS_COLLECTION
                        )
                        .get()
                        .get();

        return snapshot.size();
    }

    /**
     * Load total number of doctors.
     */
    public int getActiveDoctorCount()
            throws Exception {

        QuerySnapshot snapshot =
                firestore
                        .collection(
                                DOCTORS_COLLECTION
                        )
                        .get()
                        .get();

        return snapshot.size();
    }

    /**
     * Load number of pending hospital verifications.
     */
    public int getPendingVerificationCount()
            throws Exception {

        return verificationController
                .getVerificationsByStatus(
                        "PENDING"
                )
                .size();
    }

    /**
     * Load all dashboard KPI values asynchronously.
     *
     * Firestore operations execute away from
     * the JavaFX Application Thread.
     */
    public CompletableFuture<DashboardStats>
    loadDashboardStats() {

        return CompletableFuture.supplyAsync(() -> {

            try {

                int hospitals =
                        getRegisteredHospitalCount();

                int doctors =
                        getActiveDoctorCount();

                int pending =
                        getPendingVerificationCount();

                return new DashboardStats(
                        hospitals,
                        doctors,
                        pending
                );

            } catch (Exception e) {

                throw new RuntimeException(
                        "Failed to load Admin Dashboard statistics.",
                        e
                );
            }
        });
    }

    /**
     * Dashboard statistics DTO.
     *
     * This is not a database model.
     * It only transports dashboard KPI values.
     */
    public static class DashboardStats {

        private final int registeredHospitals;

        private final int activeDoctors;

        private final int pendingVerifications;

        public DashboardStats(
                int registeredHospitals,
                int activeDoctors,
                int pendingVerifications) {

            this.registeredHospitals =
                    registeredHospitals;

            this.activeDoctors =
                    activeDoctors;

            this.pendingVerifications =
                    pendingVerifications;
        }

        public int getRegisteredHospitals() {

            return registeredHospitals;
        }

        public int getActiveDoctors() {

            return activeDoctors;
        }

        public int getPendingVerifications() {

            return pendingVerifications;
        }
    }
}