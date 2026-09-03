package com.healthsphere.view.Hospital;

import com.healthsphere.view.Hospital.AnalyticsData;
import com.healthsphere.view.Hospital.AnalyticsData.ReportItem;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnalyticsService {

    public AnalyticsData fetchAnalyticsForPeriod(String period) {
        // Mock data logic based on period selection
        Map<String, Double> revenue = new LinkedHashMap<>();
        Map<String, Integer> appointments = new LinkedHashMap<>();

        if ("This Year".equals(period)) {
            revenue.put("Jan", 32.0); revenue.put("Feb", 38.0); revenue.put("Mar", 41.0);
            revenue.put("Apr", 47.0); revenue.put("May", 43.0); revenue.put("Jun", 48.6);
            
            appointments.put("Jan", 2480); appointments.put("Feb", 2670); appointments.put("Mar", 2890);
            appointments.put("Apr", 3150); appointments.put("May", 3540); appointments.put("Jun", 3842);
        } else { // "This Month" or default
            revenue.put("W1", 10.2); revenue.put("W2", 12.4); revenue.put("W3", 11.8); revenue.put("W4", 14.2);
            appointments.put("W1", 850); appointments.put("W2", 920); appointments.put("W3", 980); appointments.put("W4", 1092);
        }

        List<ReportItem> reports = List.of(
            new ReportItem("Monthly Operational Overview - " + period, "PDF • 2.4 MB", "Generated Today"),
            new ReportItem("Financial Performance & Revenue Audit", "XLSX • 4.1 MB", "Generated Yesterday"),
            new ReportItem("Departmental Efficiency & Occupancy Report", "PDF • 1.8 MB", "Generated 3 days ago")
        );

        return new AnalyticsData(48.6, 3842, 74.2, 12458, revenue, appointments, reports);
    }
}