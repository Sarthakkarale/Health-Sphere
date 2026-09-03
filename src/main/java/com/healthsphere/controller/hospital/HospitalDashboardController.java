package com.healthsphere.controller.hospital;

import com.healthsphere.dao.hospital.HospitalDashboardDAO;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.DashboardStats;

/**
 * HospitalDashboardController
 *
 * Coordinates dashboard requests between the View
 * and HospitalDashboardDAO.
 */
public class HospitalDashboardController {

    private final HospitalDashboardDAO
            dashboardDAO;

    public HospitalDashboardController() {

        dashboardDAO =
                new HospitalDashboardDAO();
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
}