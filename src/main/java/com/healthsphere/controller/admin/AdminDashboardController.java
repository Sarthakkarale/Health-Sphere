package com.healthsphere.controller.admin;

import com.healthsphere.dao.admin.AdminDAO;

import java.util.concurrent.CompletableFuture;

/**
 * Controller for Admin Dashboard operations.
 *
 * This controller loads dashboard statistics from Firestore.
 *
 * IMPORTANT:
 * Hospital verification status is always read from Firestore.
 * No verification status is stored locally in this controller.
 */
public class AdminDashboardController {

    private final AdminDAO adminDAO;

    private final HospitalVerificationController
            verificationController;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public AdminDashboardController() {

        this.adminDAO =
                new AdminDAO();

        this.verificationController =
                new HospitalVerificationController();
    }

    // ============================================================
    // REGISTERED HOSPITAL COUNT
    // ============================================================

    /**
     * Get total number of registered hospitals.
     */
    public int getRegisteredHospitalCount()
            throws Exception {

        return adminDAO
                .getRegisteredHospitalCount();
    }

    // ============================================================
    // ACTIVE DOCTOR COUNT
    // ============================================================

    /**
     * Get total number of active doctors.
     */
    public int getActiveDoctorCount()
            throws Exception {

        return adminDAO
                .getActiveDoctorCount();
    }

    // ============================================================
    // PENDING VERIFICATION COUNT
    // ============================================================

    /**
     * Get number of hospitals whose CURRENT Firestore
     * verification status is PENDING.
     *
     * VERIFIED hospitals will NOT be counted as pending.
     */
    public int getPendingVerificationCount()
            throws Exception {

        return verificationController
                .getVerificationsByStatus(
                        "PENDING"
                )
                .size();
    }

    // ============================================================
    // LOAD DASHBOARD STATISTICS
    // ============================================================

    /**
     * Load all dashboard KPI values asynchronously.
     *
     * Firestore operations execute away from the JavaFX
     * Application Thread.
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

    // ============================================================
    // DASHBOARD STATISTICS DTO
    // ============================================================

    /**
     * Dashboard statistics DTO.
     *
     * This class does not store Firestore data permanently.
     * It only carries KPI values to the dashboard view.
     */
    public static class DashboardStats {

        private final int registeredHospitals;

        private final int activeDoctors;

        private final int pendingVerifications;

        // --------------------------------------------------------
        // CONSTRUCTOR
        // --------------------------------------------------------

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

        // --------------------------------------------------------
        // GETTERS
        // --------------------------------------------------------

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