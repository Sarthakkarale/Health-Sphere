package com.healthsphere.controller.hospital;

import com.healthsphere.dao.hospital.DepartmentDAO;
import com.healthsphere.dao.hospital.DoctorDAO;
import com.healthsphere.dao.hospital.HospitalDashboardDAO;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.Appointment;
import com.healthsphere.model.DashboardStats;
import com.healthsphere.model.DoctorProfile;
import com.healthsphere.model.HospitalDepartment;
import com.healthsphere.model.HospitalDoctor;
import com.healthsphere.model.HospitalDoctorDetails;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Controller for Hospital Dashboard.
 *
 * Coordinates:
 *
 * View
 *   ↓
 * Controller
 *   ↓
 * DAO
 *   ↓
 * Firestore
 */
public class HospitalDashboardController {

    private final HospitalDashboardDAO dashboardDAO;
    private final DepartmentDAO departmentDAO;
    private final DoctorDAO doctorDAO;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public HospitalDashboardController() {

        dashboardDAO =
                new HospitalDashboardDAO();

        departmentDAO =
                new DepartmentDAO();

        doctorDAO =
                new DoctorDAO();
    }

    // =========================================================
    // GET DASHBOARD STATISTICS
    // =========================================================

    public DashboardStats getDashboardStats() {

        try {

            return dashboardDAO
                    .getDashboardStats();

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to load dashboard statistics.",
                    e
            );
        }
    }

    // =========================================================
    // DASHBOARD SUMMARY
    // =========================================================

    /**
     * Compatibility method required by
     * HospitalDashboardView.
     */
    public DashboardSummary getSummary() {

        DashboardStats stats =
                getDashboardStats();

        return new DashboardSummary(
                stats.getTotalDoctors(),
                stats.getTotalAppointments(),
                stats.getTodayAppointments(),
                stats.getCompletedAppointments(),
                stats.getWaitingAppointments(),
                stats.getUpcomingAppointments(),
                stats.getCancelledAppointments(),
                stats.getTotalBeds(),
                stats.getAvailableBeds(),
                stats.getOccupiedBeds(),
                stats.getReservedBeds(),
                stats.getOccupancyPercentage(),
                stats.getEmergencyCases()
        );
    }

    // =========================================================
    // GET DEPARTMENTS
    // =========================================================

    /**
     * Returns departments belonging to the
     * current hospital.
     */
    public List<HospitalDepartment> getDepartments() {

        try {

            return departmentDAO
                    .getAllDepartments();

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to load hospital departments.",
                    e
            );
        }
    }

    // =========================================================
    // GET HOSPITAL DOCTORS WITH DETAILS
    // =========================================================

    /**
     * Combines:
     *
     * hospitalDoctors
     * +
     * doctors
     *
     * into HospitalDoctorDetails objects.
     */
    public List<HospitalDoctorDetails> getDoctors() {

        try {

            List<HospitalDoctor> hospitalDoctors =
                    doctorDAO.getAllDoctors();

            List<HospitalDoctorDetails> result =
                    new ArrayList<>();

            if (
                    hospitalDoctors == null
                    ||
                    hospitalDoctors.isEmpty()
            ) {

                return result;
            }

            for (
                    HospitalDoctor hospitalDoctor :
                    hospitalDoctors
            ) {

                if (hospitalDoctor == null) {
                    continue;
                }

                String doctorId =
                        hospitalDoctor.getDoctorId();

                if (
                        doctorId == null
                        ||
                        doctorId.trim().isEmpty()
                ) {

                    continue;
                }

                DoctorProfile doctorProfile =
                        doctorDAO.getDoctorProfile(
                                doctorId
                        );

                if (doctorProfile == null) {
                    continue;
                }

                HospitalDoctorDetails details =
                        new HospitalDoctorDetails(
                                hospitalDoctor,
                                doctorProfile
                        );

                result.add(details);
            }

            return result;

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to load hospital doctors.",
                    e
            );
        }
    }

    // =========================================================
    // GET DOCTORS FOR DEPARTMENT
    // =========================================================

    /**
     * Returns the number of active doctors assigned
     * to a particular department.
     */
    public int getDoctorsForDepartment(
            String departmentId) {

        if (
                departmentId == null
                ||
                departmentId.trim().isEmpty()
        ) {

            return 0;
        }

        String targetDepartmentId =
                departmentId.trim();

        try {

            List<HospitalDoctorDetails> doctors =
                    getDoctors();

            String departmentName = null;
            try {
                com.healthsphere.model.HospitalDepartment dept = departmentDAO.getDepartmentById(targetDepartmentId);
                if (dept != null && dept.getName() != null) {
                    departmentName = dept.getName().trim();
                }
            } catch (Exception ignore) {
            }

            int count = 0;

            for (
                    HospitalDoctorDetails doctor :
                    doctors
            ) {

                if (doctor == null || !doctor.isActive()) {
                    continue;
                }

                if ("Inactive".equalsIgnoreCase(doctor.getStatus())) {
                    continue;
                }

                String doctorDepartmentId =
                        doctor.getDepartmentId();

                if (doctorDepartmentId != null && !doctorDepartmentId.trim().isEmpty()) {
                    String doctorDept = doctorDepartmentId.trim();
                    boolean matchesId = doctorDept.equalsIgnoreCase(targetDepartmentId);
                    boolean matchesName = (departmentName != null && doctorDept.equalsIgnoreCase(departmentName));

                    if (matchesId || matchesName) {
                        count++;
                    }
                }
            }

            return count;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to count doctors for department.",
                    e
            );
        }
    }

    // =========================================================
    // GET RECENT APPOINTMENTS
    // =========================================================

    /**
     * Returns the latest hospital appointments.
     *
     * Uses the shared appointments collection.
     */
    public List<Appointment> getRecentAppointments(
            int limit) {

        if (limit <= 0) {

            return new ArrayList<>();
        }

        try {

            List<Appointment> appointments =
                    dashboardDAO
                            .getHospitalAppointments();

            if (
                    appointments == null
                    ||
                    appointments.isEmpty()
            ) {

                return new ArrayList<>();
            }

            /*
             * Sort newest appointments first.
             *
             * createdAt is expected to be an ISO
             * timestamp string.
             */
            appointments.sort(
                    Comparator.comparing(
                            Appointment::getCreatedAt,
                            Comparator.nullsLast(
                                    Comparator.reverseOrder()
                            )
                    )
            );

            int size =
                    Math.min(
                            limit,
                            appointments.size()
                    );

            return new ArrayList<>(
                    appointments.subList(
                            0,
                            size
                    )
            );

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to load recent appointments.",
                    e
            );
        }
    }

    // =========================================================
    // GET ALL APPOINTMENTS
    // =========================================================

    /**
     * Returns all appointments belonging to
     * the current hospital.
     */
    public List<Appointment> getAppointments() {

        try {

            return dashboardDAO
                    .getHospitalAppointments();

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to load hospital appointments.",
                    e
            );
        }
    }

    // =========================================================
    // INDIVIDUAL VALUES
    // =========================================================

    public int getTotalDoctors() {

        return getDashboardStats()
                .getTotalDoctors();
    }

    public int getTotalDepartments() {

        return getDashboardStats()
                .getTotalDepartments();
    }

    public int getTotalAppointments() {

        return getDashboardStats()
                .getTotalAppointments();
    }

    public int getTodayAppointments() {

        return getDashboardStats()
                .getTodayAppointments();
    }

    public int getCompletedAppointments() {

        return getDashboardStats()
                .getCompletedAppointments();
    }

    public int getWaitingAppointments() {

        return getDashboardStats()
                .getWaitingAppointments();
    }

    public int getUpcomingAppointments() {

        return getDashboardStats()
                .getUpcomingAppointments();
    }

    public int getCancelledAppointments() {

        return getDashboardStats()
                .getCancelledAppointments();
    }

    public int getTotalBeds() {

        return getDashboardStats()
                .getTotalBeds();
    }

    public int getAvailableBeds() {

        return getDashboardStats()
                .getAvailableBeds();
    }

    public int getOccupiedBeds() {

        return getDashboardStats()
                .getOccupiedBeds();
    }

    public int getReservedBeds() {

        return getDashboardStats()
                .getReservedBeds();
    }

    public double getOccupancyPercentage() {

        return getDashboardStats()
                .getOccupancyPercentage();
    }

    public int getEmergencyCases() {

        return getDashboardStats()
                .getEmergencyCases();
    }

    public int getCriticalEmergencyCases() {

        return getDashboardStats()
                .getCriticalEmergencyCases();
    }

    // =========================================================
    // DASHBOARD SUMMARY
    // =========================================================

    /**
     * UI-only summary object.
     *
     * This class is NOT stored in Firestore.
     */
    public static class DashboardSummary {

        private final int totalDoctors;
        private final int totalAppointments;
        private final int todayAppointments;
        private final int completedAppointments;
        private final int waitingAppointments;
        private final int upcomingAppointments;
        private final int cancelledAppointments;

        private final int totalBeds;
        private final int availableBeds;
        private final int occupiedBeds;
        private final int reservedBeds;

        private final double occupancyPercentage;

        private final int emergencyCases;

        public DashboardSummary(
                int totalDoctors,
                int totalAppointments,
                int todayAppointments,
                int completedAppointments,
                int waitingAppointments,
                int upcomingAppointments,
                int cancelledAppointments,
                int totalBeds,
                int availableBeds,
                int occupiedBeds,
                int reservedBeds,
                double occupancyPercentage,
                int emergencyCases
        ) {

            this.totalDoctors =
                    totalDoctors;

            this.totalAppointments =
                    totalAppointments;

            this.todayAppointments =
                    todayAppointments;

            this.completedAppointments =
                    completedAppointments;

            this.waitingAppointments =
                    waitingAppointments;

            this.upcomingAppointments =
                    upcomingAppointments;

            this.cancelledAppointments =
                    cancelledAppointments;

            this.totalBeds =
                    totalBeds;

            this.availableBeds =
                    availableBeds;

            this.occupiedBeds =
                    occupiedBeds;

            this.reservedBeds =
                    reservedBeds;

            this.occupancyPercentage =
                    occupancyPercentage;

            this.emergencyCases =
                    emergencyCases;
        }

        // =====================================================
        // DOCTORS
        // =====================================================

        public int getTotalDoctors() {

            return totalDoctors;
        }

        // =====================================================
        // APPOINTMENTS
        // =====================================================

        public int getTotalAppointments() {

            return totalAppointments;
        }

        public int getTodayAppointments() {

            return todayAppointments;
        }

        public int getCompletedAppointments() {

            return completedAppointments;
        }

        public int getWaitingAppointments() {

            return waitingAppointments;
        }

        public int getUpcomingAppointments() {

            return upcomingAppointments;
        }

        public int getCancelledAppointments() {

            return cancelledAppointments;
        }

        // =====================================================
        // BEDS
        // =====================================================

        public int getTotalBeds() {

            return totalBeds;
        }

        public int getAvailableBeds() {

            return availableBeds;
        }

        public int getOccupiedBeds() {

            return occupiedBeds;
        }

        public int getReservedBeds() {

            return reservedBeds;
        }

        public double getOccupancyPercentage() {

            return occupancyPercentage;
        }

        // =====================================================
        // EMERGENCY
        // =====================================================

        public int getEmergencyCases() {

            return emergencyCases;
        }
    }
}