package com.healthsphere.model;

/**
 * DashboardStats
 *
 * Read-only result object returned by the dashboard backend.
 *
 * This class does not communicate with Firebase.
 * It only carries calculated dashboard statistics from
 * DAO -> Controller -> View.
 */
public class DashboardStats {

    // =========================================================
    // DOCTORS
    // =========================================================

    private int totalDoctors;

    // =========================================================
    // DEPARTMENTS
    // =========================================================

    private int totalDepartments;

    // =========================================================
    // APPOINTMENTS
    // =========================================================

    private int totalAppointments;

    private int todayAppointments;

    private int completedAppointments;

    private int waitingAppointments;

    private int upcomingAppointments;

    private int cancelledAppointments;

    // =========================================================
    // BEDS
    // =========================================================

    private int totalBeds;

    private int availableBeds;

    private int occupiedBeds;

    private int reservedBeds;

    private double occupancyPercentage;

    // =========================================================
    // EMERGENCY
    // =========================================================

    private int emergencyCases;

    private int criticalEmergencyCases;

    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    public DashboardStats() {
    }

    // =========================================================
    // GETTERS / SETTERS
    // =========================================================

    public int getTotalDoctors() {
        return totalDoctors;
    }

    public void setTotalDoctors(int totalDoctors) {
        this.totalDoctors = totalDoctors;
    }

    public int getTotalDepartments() {
        return totalDepartments;
    }

    public void setTotalDepartments(int totalDepartments) {
        this.totalDepartments = totalDepartments;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(int totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public int getTodayAppointments() {
        return todayAppointments;
    }

    public void setTodayAppointments(int todayAppointments) {
        this.todayAppointments = todayAppointments;
    }

    public int getCompletedAppointments() {
        return completedAppointments;
    }

    public void setCompletedAppointments(
            int completedAppointments
    ) {
        this.completedAppointments =
                completedAppointments;
    }

    public int getWaitingAppointments() {
        return waitingAppointments;
    }

    public void setWaitingAppointments(
            int waitingAppointments
    ) {
        this.waitingAppointments =
                waitingAppointments;
    }

    public int getUpcomingAppointments() {
        return upcomingAppointments;
    }

    public void setUpcomingAppointments(
            int upcomingAppointments
    ) {
        this.upcomingAppointments =
                upcomingAppointments;
    }

    public int getCancelledAppointments() {
        return cancelledAppointments;
    }

    public void setCancelledAppointments(
            int cancelledAppointments
    ) {
        this.cancelledAppointments =
                cancelledAppointments;
    }

    public int getTotalBeds() {
        return totalBeds;
    }

    public void setTotalBeds(int totalBeds) {
        this.totalBeds = totalBeds;
    }

    public int getAvailableBeds() {
        return availableBeds;
    }

    public void setAvailableBeds(int availableBeds) {
        this.availableBeds =
                availableBeds;
    }

    public int getOccupiedBeds() {
        return occupiedBeds;
    }

    public void setOccupiedBeds(int occupiedBeds) {
        this.occupiedBeds =
                occupiedBeds;
    }

    public int getReservedBeds() {
        return reservedBeds;
    }

    public void setReservedBeds(int reservedBeds) {
        this.reservedBeds =
                reservedBeds;
    }

    public double getOccupancyPercentage() {
        return occupancyPercentage;
    }

    public void setOccupancyPercentage(
            double occupancyPercentage
    ) {
        this.occupancyPercentage =
                occupancyPercentage;
    }

    public int getEmergencyCases() {
        return emergencyCases;
    }

    public void setEmergencyCases(
            int emergencyCases
    ) {
        this.emergencyCases =
                emergencyCases;
    }

    public int getCriticalEmergencyCases() {
        return criticalEmergencyCases;
    }

    public void setCriticalEmergencyCases(
            int criticalEmergencyCases
    ) {
        this.criticalEmergencyCases =
                criticalEmergencyCases;
    }
}