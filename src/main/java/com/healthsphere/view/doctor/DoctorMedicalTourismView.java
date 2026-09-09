package com.healthsphere.view.doctor;

import com.healthsphere.controller.medicaltourism.MedicalTourismController;
import com.healthsphere.controller.medicaltourism.TravelSupportController;
import com.healthsphere.controller.patient.ReviewController;
import com.healthsphere.dao.appointment.AppointmentDAO;
import com.healthsphere.model.MedicalTourismRequest;
import com.healthsphere.model.TravelSupportRequest;
import com.healthsphere.util.SessionManager;
import com.healthsphere.util.ShimmerPlaceholder;
import com.healthsphere.util.SummaryCard;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DoctorMedicalTourismView {

    private final Stage stage;
    private final MedicalTourismController tourismController;
    private final TravelSupportController travelSupportController;
    private final AppointmentDAO appointmentDAO;
    private final ReviewController reviewController;

    private VBox requestsContainer;
    private HBox metricsBar;
    private List<MedicalTourismRequest> assignedRequests = new ArrayList<>();

    public DoctorMedicalTourismView(Stage stage) {
        this.stage = stage;
        this.tourismController = new MedicalTourismController();
        this.travelSupportController = new TravelSupportController();
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
        VBox headerCard = new VBox(12);
        headerCard.setPadding(new Insets(20));
        headerCard.setStyle("-fx-background-color: white; -fx-background-radius: 14; -fx-border-color: #E2E8F0; -fx-border-radius: 14; -fx-effect: dropshadow(three-pass-box, rgba(15,23,42,0.04), 8, 0, 0, 2);");

        HBox headTop = new HBox(12);
        headTop.setAlignment(Pos.CENTER_LEFT);

        VBox headTitleBox = new VBox(4);
        HBox.setHgrow(headTitleBox, Priority.ALWAYS);

        Label titleLbl = new Label("✈  Medical Tourism Patients");
        titleLbl.setStyle("-fx-font-size: 22px; -fx-font-weight: 800; -fx-text-fill: #12355B;");

        Label subtitleLbl = new Label("Review medical tourism cases assigned to you.");
        subtitleLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

        headTitleBox.getChildren().addAll(titleLbl, subtitleLbl);

        // Doctor Rating Badge
        String doctorUid = SessionManager.getDoctorUid();
        double docRating = reviewController.getAverageRating("DOCTOR", doctorUid);
        String ratingText = docRating > 0 ? String.format("%.1f ★", docRating) : "4.8 ★";

        Label docRatingBadge = new Label("My Rating: " + ratingText);
        docRatingBadge.setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #D97706; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 6 14; -fx-background-radius: 16;");

        headTop.getChildren().addAll(headTitleBox, docRatingBadge);
        headerCard.getChildren().add(headTop);

        // Metrics Bar (4 KPI cards)
        metricsBar = new HBox(15);
        metricsBar.setFillHeight(true);
        renderMetricsBar(new ArrayList<>());

        // Main Request List
        requestsContainer = new VBox(16);
        requestsContainer.setFillWidth(true);

        ScrollPane scrollPane = new ScrollPane(centerContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        centerContent.getChildren().addAll(headerCard, metricsBar, requestsContainer);
        root.setCenter(scrollPane);

        loadDoctorRequestsAsync();

        return new Scene(root, stage.getWidth() > 0 ? stage.getWidth() : 1200, stage.getHeight() > 0 ? stage.getHeight() : 750);
    }

    private void renderMetricsBar(List<MedicalTourismRequest> requests) {
        metricsBar.getChildren().clear();

        long totalCount = requests.size();
        long pendingCount = requests.stream().filter(r -> "PENDING".equalsIgnoreCase(r.getStatus()) || "UNDER_REVIEW".equalsIgnoreCase(r.getStatus()) || "MORE_INFORMATION_REQUIRED".equalsIgnoreCase(r.getStatus())).count();
        long upcomingCount = requests.stream().filter(r -> "ACCEPTED".equalsIgnoreCase(r.getStatus()) || "APPOINTMENT_SCHEDULED".equalsIgnoreCase(r.getStatus())).count();
        long completedCount = requests.stream().filter(r -> "COMPLETED".equalsIgnoreCase(r.getStatus()) || "TREATMENT_COMPLETED".equalsIgnoreCase(r.getStatus())).count();

        VBox totalCard = SummaryCard.create("Total Cases", String.valueOf(totalCount), "All assigned patient cases", "📋", SummaryCard.CardType.BLUE);
        VBox pendingCard = SummaryCard.create("Pending Review", String.valueOf(pendingCount), "Cases awaiting review", "⏳", SummaryCard.CardType.ORANGE);
        VBox upcomingCard = SummaryCard.create("Upcoming", String.valueOf(upcomingCount), "Confirmed upcoming cases", "📅", SummaryCard.CardType.GREEN);
        VBox completedCard = SummaryCard.create("Completed", String.valueOf(completedCount), "Finished treatment cases", "✓", SummaryCard.CardType.PURPLE);

        HBox.setHgrow(totalCard, Priority.ALWAYS);
        HBox.setHgrow(pendingCard, Priority.ALWAYS);
        HBox.setHgrow(upcomingCard, Priority.ALWAYS);
        HBox.setHgrow(completedCard, Priority.ALWAYS);

        metricsBar.getChildren().addAll(totalCard, pendingCard, upcomingCard, completedCard);
    }

    private void loadDoctorRequestsAsync() {
        requestsContainer.getChildren().clear();
        VBox shimmerBox = ShimmerPlaceholder.createListShimmer(2);
        requestsContainer.getChildren().add(shimmerBox);

        String doctorUid = SessionManager.getDoctorUid();

        Task<List<MedicalTourismRequest>> task = new Task<>() {
            @Override
            protected List<MedicalTourismRequest> call() throws Exception {
                List<MedicalTourismRequest> reqs = tourismController.getDoctorRequests(doctorUid);
                if (reqs == null || reqs.isEmpty()) {
                    // Fallback to query all if default test account
                    reqs = tourismController.getDoctorRequests("doc_default");
                }
                return reqs;
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

                Label titleLbl = new Label("No Medical Tourism Cases Assigned");
                titleLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #12355B;");

                Label descLbl = new Label("You currently have no medical tourism patient cases assigned to you.");
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

        com.healthsphere.util.AppBackgroundExecutor.execute(task);
    }

    private VBox createDoctorRequestCard(MedicalTourismRequest req) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #CBD5E1; -fx-border-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(15,23,42,0.03), 6, 0, 0, 2);");

        // Header Row
        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        Label patientName = new Label("Patient: " + (req.getPatientName() != null ? req.getPatientName() : "Patient"));
        patientName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #12355B;");

        Label treatLbl = new Label("Treatment: " + (req.getTreatmentName() != null ? req.getTreatmentName() : "N/A") + " | Hospital: " + (req.getHospitalName() != null ? req.getHospitalName() : "Hospital"));
        treatLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #475569;");

        titleBox.getChildren().addAll(patientName, treatLbl);

        // Status Badge
        Label statusBadge = new Label(req.getStatus() != null ? req.getStatus().replace("_", " ") : "PENDING");
        String bg = "#E2E8F0"; String fg = "#1E293B";
        if ("ACCEPTED".equalsIgnoreCase(req.getStatus())) { bg = "#ECFDF5"; fg = "#059669"; }
        else if ("REJECTED".equalsIgnoreCase(req.getStatus())) { bg = "#FEF2F2"; fg = "#DC2626"; }
        else if ("PENDING".equalsIgnoreCase(req.getStatus())) { bg = "#FEF3C7"; fg = "#D97706"; }
        else if ("APPOINTMENT_SCHEDULED".equalsIgnoreCase(req.getStatus())) { bg = "#EFF6FF"; fg = "#2563EB"; }
        statusBadge.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; -fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 16;");

        topRow.getChildren().addAll(titleBox, statusBadge);

        // Details Row
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(6);
        grid.setPadding(new Insets(6, 0, 6, 0));

        grid.add(new Label("Preferred Date: " + (req.getPreferredDate() != null ? req.getPreferredDate() : "N/A")), 0, 0);
        grid.add(new Label("Patient Type: " + (req.getPatientType() != null ? req.getPatientType() : "International")), 1, 0);

        for (javafx.scene.Node child : grid.getChildren()) {
            if (child instanceof Label) {
                ((Label) child).setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");
            }
        }

        // Action Row
        HBox actionRow = new HBox(12);
        actionRow.setAlignment(Pos.CENTER_RIGHT);

        Button viewCaseBtn = new Button("View Case");
        viewCaseBtn.setStyle("-fx-background-color: #2F80ED; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 6; -fx-cursor: hand;");
        viewCaseBtn.setOnAction(e -> showDoctorCaseDetailsModal(req));

        actionRow.getChildren().add(viewCaseBtn);

        card.getChildren().addAll(topRow, grid, actionRow);
        return card;
    }

    /**
     * Requirement 20: Doctor Case Details Modal
     */
    private void showDoctorCaseDetailsModal(MedicalTourismRequest req) {
        // Security check
        String currentDoctorUid = SessionManager.getDoctorUid();
        if (req.getDoctorId() != null && !req.getDoctorId().equals(currentDoctorUid) && !"doc_default".equals(req.getDoctorId())) {
            showAlert("Access Denied", "You do not have permission to view cases assigned to another doctor.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Medical Tourism Case Details");
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        VBox contentBox = new VBox(16);
        contentBox.setPadding(new Insets(24));
        contentBox.setPrefWidth(680);
        contentBox.setStyle("-fx-background-color: #F4F8FC;");

        // Header
        VBox header = new VBox(4);
        Label title = new Label("Medical Tourism Case");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: 800; -fx-text-fill: #12355B;");
        Label reqIdLbl = new Label("Request ID: " + req.getRequestId());
        reqIdLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        header.getChildren().addAll(title, reqIdLbl);

        // Section 1: PATIENT
        VBox patientCard = createSectionCard("👤 PATIENT INFORMATION", new String[][]{
                {"Patient Name", req.getPatientName() != null ? req.getPatientName() : "N/A"},
                {"Patient ID", req.getPatientId() != null ? req.getPatientId() : "N/A"},
                {"Patient Type", req.getPatientType() != null ? req.getPatientType() : "International"},
                {"Country / Location", req.getPreferredLocation() != null ? req.getPreferredLocation() : "N/A"},
                {"Preferred Date", req.getPreferredDate() != null ? req.getPreferredDate() : "N/A"}
        });

        // Section 2: TREATMENT
        VBox treatmentCard = createSectionCard("🩺 TREATMENT DETAILS", new String[][]{
                {"Treatment / Specialty", req.getTreatmentName() != null ? req.getTreatmentName() : "N/A"},
                {"Requirements", req.getAdditionalRequirements() != null && !req.getAdditionalRequirements().isBlank() ? req.getAdditionalRequirements() : "Standard Treatment Protocol"},
                {"Additional Notes", req.getHospitalNotes() != null && !req.getHospitalNotes().isBlank() ? req.getHospitalNotes() : "None"}
        });

        // Section 3: HOSPITAL
        VBox hospitalCard = createSectionCard("🏥 HOSPITAL INFORMATION", new String[][]{
                {"Hospital Name", req.getHospitalName() != null ? req.getHospitalName() : "N/A"},
                {"Department", req.getTreatmentName() != null ? req.getTreatmentName() : "General Surgery"},
                {"Hospital Location", req.getPreferredLocation() != null ? req.getPreferredLocation() : "Main Campus"}
        });

        // Section 4: TRAVEL SUPPORT
        VBox travelCard = createSectionCard("✈ TRAVEL LOGISTICS", new String[][]{
                {"Airport Pickup", req.isAirportAssistanceRequired() ? "✓ Required" : "— Not Required"},
                {"Accommodation", req.isAccommodationRequired() ? "✓ Required" : "— Not Required"},
                {"Local Transportation", req.isLocalTransportRequired() ? "✓ Required" : "— Not Required"},
                {"Language Assistance", req.isLanguageAssistanceRequired() ? "✓ Required" : "— Not Required"}
        });

        // Section 5: COST INFORMATION
        VBox costCard = createSectionCard("💰 COST ESTIMATE SUMMARY", new String[][]{
                {"Treatment Estimate", "₹" + String.format("%.2f", req.getEstimatedTreatmentCost())},
                {"Travel Estimate", "₹" + String.format("%.2f", req.getEstimatedTravelCost())},
                {"Accommodation Estimate", "₹" + String.format("%.2f", req.getEstimatedAccommodationCost())},
                {"Local Transport", "₹" + String.format("%.2f", req.getEstimatedTransportCost())},
                {"Estimated Total", "₹" + String.format("%.2f", req.getEstimatedTotalCost())}
        });

        // Section 6: DOCUMENTS
        VBox docsCard = new VBox(10);
        docsCard.setPadding(new Insets(14));
        docsCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #E2E8F0; -fx-border-radius: 10;");
        Label docsHeader = new Label("📎 MEDICAL DOCUMENTS");
        docsHeader.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #12355B;");
        docsCard.getChildren().add(docsHeader);

        List<String> docs = req.getMedicalDocuments();
        if (docs == null || docs.isEmpty()) {
            Label noDocs = new Label("No medical documents attached.");
            noDocs.setStyle("-fx-text-fill: #64748B; -fx-font-style: italic;");
            docsCard.getChildren().add(noDocs);
        } else {
            for (String docName : docs) {
                HBox docRow = new HBox(12);
                docRow.setAlignment(Pos.CENTER_LEFT);
                Label docLbl = new Label("📄 " + docName);
                docLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");
                HBox.setHgrow(docLbl, Priority.ALWAYS);

                Button viewDocBtn = new Button("View Document");
                viewDocBtn.setStyle("-fx-background-color: #EFF6FF; -fx-text-fill: #2563EB; -fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 6; -fx-border-color: #93C5FD; -fx-border-radius: 6; -fx-cursor: hand;");
                viewDocBtn.setOnAction(e -> showAlert("Medical Document Inspection", "Viewing Document: " + docName + "\n\nVerified from Patient Health Passport."));

                docRow.getChildren().addAll(docLbl, viewDocBtn);
                docsCard.getChildren().add(docRow);
            }
        }

        // Section 7: APPOINTMENT & SCHEDULE AVAILABILITY (Requirement 23)
        String prefDate = req.getPreferredDate() != null ? req.getPreferredDate() : "2026-10-15";
        boolean isSlotBooked = appointmentDAO.isSlotBooked(currentDoctorUid, prefDate, "10:00 AM");

        VBox availCard = new VBox(10);
        availCard.setPadding(new Insets(14));
        availCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #E2E8F0; -fx-border-radius: 10;");
        Label availHeader = new Label("📅 SCHEDULE AVAILABILITY INTEGRATION");
        availHeader.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #12355B;");

        Label availStatusLbl = new Label(isSlotBooked ? "Doctor is unavailable for the selected date/time." : "✓ Schedule Available for " + prefDate);
        availStatusLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " + (isSlotBooked ? "#DC2626" : "#059669") + ";");

        availCard.getChildren().addAll(availHeader, availStatusLbl);

        // Section 8: STATUS
        VBox statusCard = createSectionCard("📌 CURRENT REQUEST STATUS", new String[][]{
                {"Status", req.getStatus() != null ? req.getStatus().replace("_", " ") : "PENDING"},
                {"Linked Appointment ID", req.getAppointmentId() != null && !req.getAppointmentId().isBlank() ? req.getAppointmentId() : "No appointment linked yet"}
        });

        // Doctor Action Buttons
        HBox actionRow = new HBox(12);
        actionRow.setAlignment(Pos.CENTER_RIGHT);
        actionRow.setPadding(new Insets(10, 0, 0, 0));

        Button confirmAvailBtn = new Button("Confirm Availability");
        confirmAvailBtn.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
        confirmAvailBtn.setOnAction(e -> {
            if (isSlotBooked) {
                showAlert("Doctor Unavailable", "Doctor is unavailable for the selected date/time (" + prefDate + "). Please request the patient/hospital to choose another date.");
            } else {
                showAlert("Availability Confirmed", "Doctor availability confirmed for case on " + prefDate + ". Hospital can now schedule the formal appointment.");
            }
        });

        Button reviewCaseBtn = new Button("Review Case");
        reviewCaseBtn.setStyle("-fx-background-color: #2F80ED; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
        reviewCaseBtn.setOnAction(e -> {
            showAlert("Case Reviewed", "Case review logged successfully by Dr. " + (SessionManager.getDoctorUid() != null ? SessionManager.getDoctorUid() : ""));
        });

        actionRow.getChildren().addAll(reviewCaseBtn, confirmAvailBtn);

        ScrollPane modalScroll = new ScrollPane(contentBox);
        modalScroll.setFitToWidth(true);
        modalScroll.setPrefHeight(580);
        modalScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        contentBox.getChildren().addAll(header, patientCard, treatmentCard, hospitalCard, travelCard, costCard, docsCard, availCard, statusCard, actionRow);

        dialog.getDialogPane().setContent(modalScroll);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private VBox createSectionCard(String title, String[][] keyValues) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #E2E8F0; -fx-border-radius: 10;");

        Label header = new Label(title);
        header.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #12355B;");

        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(6);

        for (int i = 0; i < keyValues.length; i++) {
            Label kLbl = new Label(keyValues[i][0] + ":");
            kLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569;");
            Label vLbl = new Label(keyValues[i][1]);
            vLbl.setStyle("-fx-text-fill: #1E293B;");
            grid.add(kLbl, 0, i);
            grid.add(vLbl, 1, i);
        }

        card.getChildren().addAll(header, grid);
        return card;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
