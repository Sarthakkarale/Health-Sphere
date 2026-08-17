package com.healthsphere.controller.admin;

import com.healthsphere.dao.admin.AdminDAO;

import java.util.concurrent.CompletableFuture;

public class AdminDashboardController {

    private final AdminDAO adminDAO;

    private final HospitalVerificationController
            verificationController;

    public AdminDashboardController() {

        this.adminDAO =
                new AdminDAO();

        this.verificationController =
                new HospitalVerificationController();
    }

    /**
     * Load total number of registered hospitals.
     *
     * Database access is handled by AdminDAO.
     */
    public int getRegisteredHospitalCount()
            throws Exception {

        return adminDAO
                .getRegisteredHospitalCount();
    }

    /**
     * Load total number of doctors.
     *
     * Database access is handled by AdminDAO.
     */
    public int getActiveDoctorCount()
            throws Exception {

        return adminDAO
                .getActiveDoctorCount();
    }

    /**
     * Load number of pending hospital verifications.
     *
     * Hospital verification database operations
     * are handled by HospitalVerificationController.
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