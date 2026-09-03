package com.healthsphere.view.Hospital;

import java.util.List;
import java.util.Map;

public class AnalyticsData {
    private final double totalRevenue;
    private final int totalAppointments;
    private final double bedOccupancyRate;
    private final int patientCount;
    private final Map<String, Double> monthlyRevenue;
    private final Map<String, Integer> monthlyAppointments;
    private final List<ReportItem> reports;

    public AnalyticsData(double totalRevenue, int totalAppointments, double bedOccupancyRate, 
                         int patientCount, Map<String, Double> monthlyRevenue, 
                         Map<String, Integer> monthlyAppointments, List<ReportItem> reports) {
        this.totalRevenue = totalRevenue;
        this.totalAppointments = totalAppointments;
        this.bedOccupancyRate = bedOccupancyRate;
        this.patientCount = patientCount;
        this.monthlyRevenue = monthlyRevenue;
        this.monthlyAppointments = monthlyAppointments;
        this.reports = reports;
    }

    public double getTotalRevenue() { return totalRevenue; }
    public int getTotalAppointments() { return totalAppointments; }
    public double getBedOccupancyRate() { return bedOccupancyRate; }
    public int getPatientCount() { return patientCount; }
    public Map<String, Double> getMonthlyRevenue() { return monthlyRevenue; }
    public Map<String, Integer> getMonthlyAppointments() { return monthlyAppointments; }
    public List<ReportItem> getReports() { return reports; }

    public static class ReportItem {
        private final String title;
        private final String meta;
        private final String date;

        public ReportItem(String title, String meta, String date) {
            this.title = title;
            this.meta = meta;
            this.date = date;
        }

        public String getTitle() { return title; }
        public String getMeta() { return meta; }
        public String getDate() { return date; }
    }
}