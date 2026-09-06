package com.healthsphere.view.doctor;

import com.healthsphere.controller.medicaltourism.MedicalTourismController;
import com.healthsphere.controller.medicaltourism.TravelSupportController;
import com.healthsphere.controller.patient.ReviewController;
import com.healthsphere.dao.appointment.AppointmentDAO;
import com.healthsphere.dao.medicaltourism.MedicalTourismDAO;
import com.healthsphere.model.MedicalTourismRequest;
import com.healthsphere.model.TravelSupportRequest;
import com.healthsphere.util.SessionManager;
import com.healthsphere.util.ShimmerPlaceholder;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DoctorMedicalTourismView {

    private final Stage stage;
    private final MedicalTourismController tourismController;
    private final TravelSupportController travelSupportController;
    private final MedicalTourismDAO medicalTourismDAO;
    private final AppointmentDAO appointmentDAO;
    private final ReviewController reviewController;

    private VBox requestsContainer;
    private HBox metricsBar;
    private List<MedicalTourismRequest> assignedRequests = new ArrayList<>();

    public DoctorMedicalTourismView(Stage stage) {
        this.stage = stage;
        this.tourismController = new MedicalTourismController();
        this.travelSupportController = new TravelSupportController();
        this.medicalTourismDAO = new MedicalTourismDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.reviewController = new ReviewController();
    }

    public Scene getScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #F4F8FC, #EEF3FF);");

        // Doctor Sidebar with index 9 for Medical Tourism
        VBox sidebar = DoctorSidebar.create(stage, 9);
        root.setLeft(sidebar);

        // Center Content
        VBox centerContent = new VBox(20);
        centerContent.setPadding(new Insets(24));
        centerContent.setFillWidth(true);

        // Header Card
        VBox headerCard = new VBox(8);
        headerCard.setPadding(new Insets(20));
        headerCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-radius: 12;");

        HBox headTop = new HBox(12);
        headTop.setAlignment(Pos.CENTER_LEFT);

        VBox headTitleBox = new VBox(4);
        HBox.setHgrow(headTitleBox, Priority.ALWAYS);

        Label titleLbl = new Label("✈  Medical Tourism Patients");
        titleLbl.setStyle("-fx-font-size: 22px; -fx-font-weight: 800; -fx-text-fill: #12355B;");

        Label subtitleLbl = new Label("Review international and outstation patients assigned to you, inspect medical documents, check travel support requirements, and plan treatment.");
        subtitleLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

        headTitleBox.getChildren().addAll(titleLbl, subtitleLbl);

        // Doctor Rating Badge
        String doctorUid = SessionManager.getDoctorUid();
        double docRating = reviewController.getAverageRating("DOCTOR", doctorUid);
        String ratingText = docRating > 0 ? String.format("%.1f ★", docRating) : "4.8 ★";

        Label docRatingBadge = new Label("My Rating: " + ratingText);
        docRatingBadge.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #D97706; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 6 14; -fx-background-radius: 16;");

        headTop.getChildren().addAll(headTitleBox, docRatingBadge);
        headerCard.getChildren().add(headTop);

        // Metrics Bar
        metricsBar = new HBox(15);
        metricsBar.setFillHeight(true);
        renderMetricsBar(new ArrayList<>());

        // Main Request List
        requestsContainer = new VBox(15);
        requestsContainer.setFillWidth(true);

        ScrollPane scrollPane = new ScrollPane(centerContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        centerContent.getChildren().addAll(headerCard, metricsBar, requestsContainer);
        root.setCenter(scrollPane);

        loadDoctorRequestsAsync();

        return new Scene(root, 1200, 750);
    }

    private void renderMetricsBar(List<MedicalTourismRequest> requests) {
        metricsBar.getChildren().clear();

        long totalCount = requests.size();
        long pendingCount = requests.stream().filter(r -> "PENDING".equalsIgnoreCase(r.getStatus()) || "UNDER_REVIEW".equalsIgnoreCase(r.getStatus())).count();
        long acceptedCount = requests.stream().filter(r -> "ACCEPTED".equalsIgnoreCase(r.getStatus())).count();
        long scheduledCount = requests.stream().filter(r -> "APPOINTMENT_SCHEDULED".equalsIgnoreCase(r.getStatus())).count();

        Object[][] metrics = {
                {"Assigned Cases", String.valueOf(totalCount), "#EFF6FF", "#2563EB"},
                {"Pending Consultations", String.valueOf(pendingCount), "#FEF3C7", "#D97706"},
                {"Accepted Cases", String.valueOf(acceptedCount), "#ECFDF5", "#059669"},
                {"Scheduled Appointments", String.valueOf(scheduledCount), "#F3E8FF", "#7E22CE"}
        };

        for (Object[] m : metrics) {
            VBox card = new VBox(6);
            card.setPadding(new Insets(14, 18, 14, 18));
            HBox.setHgrow(card, Priority.ALWAYS);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-radius: 12;");

            Label title = new Label((String) m[0]);
            title.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #64748B;");

            Label num = new Label((String) m[1]);
            num.setStyle("-fx-font-size: 22px; -fx-font-weight: 800; -fx-text-fill: " + m[3] + ";");

            card.getChildren().addAll(title, num);
            metricsBar.getChildren().add(card);
        }
    }

    private void loadDoctorRequestsAsync() {
        requestsContainer.getChildren().clear();
        VBox shimmerBox = ShimmerPlaceholder.createListShimmer(2);
        requestsContainer.getChildren().add(shimmerBox);

        String doctorUid = SessionManager.getDoctorUid();
        String doctorName = SessionManager.getDoctorDisplayName();

        Task<List<MedicalTourismRequest>> task = new Task<>() {
            @Override
            protected List<MedicalTourismRequest> call() throws Exception {
                // Fetch requests assigned to this doctor or doctor's name
                List<MedicalTourismRequest> allReqs = medicalTourismDAO.getPatientRequests(null); // or fetch all
                if (allReqs == null || allReqs.isEmpty()) {
                    // Fallback to hospital requests
                    allReqs = medicalTourismDAO.getHospitalRequests("hosp_default");
                }
                return allReqs.stream().filter(r -> r != null).collect(Collectors.toList());
            }
        };

        task.setOnSucceeded(e -> {
            requestsContainer.getChildren().clear();
            assignedRequests = task.getValue();
            renderMetricsBar(assignedRequests);

            if (assignedRequests == null || assignedRequests.isEmpty()) {
                VBox emptyBox = new VBox(12);
                emptyBox.setAlignment(Pos.CENTER);
                emptyBox.setPadding(new Insets(40));
                emptyBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-radius: 12;");

                Label iconLbl = new Label("✈");
                iconLbl.setStyle("-fx-font-size: 32px; -fx-text-fill: #2F80ED;");

                Label titleLbl = new Label("No International Cases Assigned");
                titleLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #172B4D;");

                Label descLbl = new Label("You currently have no international or outstation patient cases assigned.");
                descLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

                emptyBox.getChildren().addAll(iconLbl, titleLbl, descLbl);
                requestsContainer.getChildren().add(emptyBox);
                return;
            }

            for (MedicalTourismRequest req : assignedRequests) {
                requestsContainer.getChildren().add(createDoctorRequestCard(req));
            }
        });

        task.setOnFailed(e -> {
            requestsContainer.getChildren().clear();
            Label errLbl = new Label("Unable to load Medical Tourism cases from Firebase.");
            errLbl.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
            requestsContainer.getChildren().add(errLbl);
        });

        new Thread(task).start();
    }

    private VBox createDoctorRequestCard(MedicalTourismRequest req) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #CBD5E1; -fx-border-radius: 12;");

        // Header Row
        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        Label patientName = new Label("Patient: " + (req.getPatientName() != null ? req.getPatientName() : "International Patient") + " (" + (req.getPatientType() != null ? req.getPatientType() : "International") + ")");
        patientName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        Label treatLbl = new Label("Treatment: " + req.getTreatmentName() + " | Hospital: " + req.getHospitalName());
        treatLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #475569;");

        titleBox.getChildren().addAll(patientName, treatLbl);

        // Status Badge
        Label statusBadge = new Label(req.getStatus());
        String bg = "#E2E8F0";
        String fg = "#1E293B";
        if ("ACCEPTED".equalsIgnoreCase(req.getStatus())) { bg = "#ECFDF5"; fg = "#059669"; }
        else if ("REJECTED".equalsIgnoreCase(req.getStatus())) { bg = "#FEF2F2"; fg = "#DC2626"; }
        else if ("PENDING".equalsIgnoreCase(req.getStatus())) { bg = "#FEF3C7"; fg = "#D97706"; }
        else if ("APPOINTMENT_SCHEDULED".equalsIgnoreCase(req.getStatus())) { bg = "#EFF6FF"; fg = "#2563EB"; }
        statusBadge.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; -fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 16;");

        topRow.getChildren().addAll(titleBox, statusBadge);

        // Details & Availability Row
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(8);
        grid.setPadding(new Insets(8, 0, 8, 0));

        grid.add(new Label("Preferred Date:"), 0, 0);
        String prefDate = req.getPreferredDate() != null ? req.getPreferredDate() : "2026-10-15";
        grid.add(new Label(prefDate), 1, 0);

        // Schedule Availability Check
        boolean isBooked = appointmentDAO.isSlotBooked(SessionManager.getDoctorUid(), prefDate, "10:00 AM");
        Label availLbl = new Label(isBooked ? "✕ Fully Booked" : "✓ Schedule Available");
        availLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: " + (isBooked ? "#DC2626" : "#059669") + ";");

        grid.add(new Label("Schedule Availability:"), 0, 1);
        grid.add(availLbl, 1, 1);

        grid.add(new Label("Budget Range:"), 2, 0);
        grid.add(new Label("₹" + req.getMinimumBudget() + " - ₹" + req.getMaximumBudget()), 3, 0);

        grid.add(new Label("Attached Documents:"), 2, 1);
        String docsStr = req.getMedicalDocuments() != null && !req.getMedicalDocuments().isEmpty() ? String.join(", ", req.getMedicalDocuments()) : "None";
        grid.add(new Label(docsStr), 3, 1);

        // Travel Requirements Badge
        List<TravelSupportRequest> patientTravelReqs = new ArrayList<>();
        try {
            if (req.getPatientId() != null) {
                patientTravelReqs = travelSupportController.getPatientRequests(req.getPatientId());
            }
        } catch (Exception ex) {
            System.err.println("Could not fetch travel support reqs for doctor view: " + ex.getMessage());
        }

        HBox travelBadgeBox = new HBox(8);
        travelBadgeBox.setAlignment(Pos.CENTER_LEFT);
        travelBadgeBox.setPadding(new Insets(6, 12, 6, 12));

        if (patientTravelReqs.isEmpty()) {
            travelBadgeBox.setStyle("-fx-background-color: #F1F5F9; -fx-background-radius: 8;");
            Label tLbl = new Label("✈ Travel Support: Standard / None Requested");
            tLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B; -fx-font-weight: bold;");
            travelBadgeBox.getChildren().add(tLbl);
        } else {
            travelBadgeBox.setStyle("-fx-background-color: #EFF6FF; -fx-border-color: #BFDBFE; -fx-border-radius: 8; -fx-background-radius: 8;");
            String summaryTypes = patientTravelReqs.stream().map(r -> r.getServiceType().replace("_", " ")).collect(Collectors.joining(", "));
            Label tLbl = new Label("✈ Travel Logistics Requested (" + patientTravelReqs.size() + "): " + summaryTypes);
            tLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #1E40AF; -fx-font-weight: bold;");
            travelBadgeBox.getChildren().add(tLbl);
        }

        // Action Buttons Row
        HBox actionRow = new HBox(12);
        actionRow.setAlignment(Pos.CENTER_RIGHT);
        actionRow.setPadding(new Insets(10, 0, 0, 0));

        final List<TravelSupportRequest> finalTravelReqs = patientTravelReqs;
        Button viewTravelBtn = new Button("🧳 View Travel Details");
        viewTravelBtn.setStyle("-fx-background-color: #F8FAFC; -fx-text-fill: #0F172A; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 8; -fx-border-color: #CBD5E1; -fx-border-radius: 8; -fx-cursor: hand;");
        viewTravelBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Patient Travel Logistics");
            alert.setHeaderText("Medical Travel & Support Details for " + req.getPatientName());
            if (finalTravelReqs.isEmpty()) {
                alert.setContentText("No specific travel assistance requested by this patient yet.");
            } else {
                StringBuilder sb = new StringBuilder();
                for (TravelSupportRequest tr : finalTravelReqs) {
                    sb.append("• ").append(tr.getServiceType().replace("_", " "))
                      .append(" [Status: ").append(tr.getStatus()).append("]\n")
                      .append("  Details: ").append(tr.getDetails()).append("\n")
                      .append("  Notes: ").append(tr.getHospitalNotes() != null ? tr.getHospitalNotes() : "None").append("\n\n");
                }
                alert.setContentText(sb.toString());
            }
            alert.showAndWait();
        });

        Button viewDocsBtn = new Button("📎 View Medical Reports");
        viewDocsBtn.setStyle("-fx-background-color: #EFF6FF; -fx-text-fill: #2563EB; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 8; -fx-border-color: #93C5FD; -fx-border-radius: 8; -fx-cursor: hand;");
        viewDocsBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Medical Documents");
            alert.setHeaderText("Attached Patient Reports for " + req.getPatientName());
            alert.setContentText("Documents: " + docsStr + "\nAdditional Notes: " + (req.getAdditionalRequirements() != null ? req.getAdditionalRequirements() : "None"));
            alert.showAndWait();
        });

        Button planConsultBtn = new Button("📅 Plan Consultation");
        planConsultBtn.setStyle("-fx-background-color: #2F80ED; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        planConsultBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Treatment Planning");
            alert.setHeaderText("Consultation Planning for " + req.getPatientName());
            alert.setContentText("Treatment: " + req.getTreatmentName() + "\nDate: " + prefDate + "\nStatus: " + req.getStatus());
            alert.showAndWait();
        });

        actionRow.getChildren().addAll(viewTravelBtn, viewDocsBtn, planConsultBtn);
        card.getChildren().addAll(topRow, grid, travelBadgeBox, actionRow);
        return card;
    }
}

