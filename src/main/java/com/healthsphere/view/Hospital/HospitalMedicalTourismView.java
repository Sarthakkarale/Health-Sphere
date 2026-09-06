package com.healthsphere.view.hospital;

import com.healthsphere.controller.medicaltourism.MedicalTourismController;
import com.healthsphere.controller.medicaltourism.TravelSupportController;
import com.healthsphere.dao.appointment.AppointmentDAO;
import com.healthsphere.model.Appointment;
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
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class HospitalMedicalTourismView {

    private final MedicalTourismController tourismController;
    private final TravelSupportController travelSupportController;
    private final AppointmentDAO appointmentDAO;

    private VBox requestsContainer;
    private HBox metricsBar;
    private List<MedicalTourismRequest> allRequests = new ArrayList<>();
    private ComboBox<String> statusFilter;

    // Travel Support Tab components
    private VBox travelRequestsContainer;
    private HBox travelMetricsBar;
    private List<TravelSupportRequest> allTravelRequests = new ArrayList<>();
    private ComboBox<String> travelStatusFilter;

    public HospitalMedicalTourismView() {
        this.tourismController = new MedicalTourismController();
        this.travelSupportController = new TravelSupportController();
        this.appointmentDAO = new AppointmentDAO();
    }

    public Scene createScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #F4F8FC, #EEF3FF);");

        // Sidebar
        VBox sidebar = HospitalSidebar.createSidebar(stage, HospitalSidebar.HospitalTab.MEDICAL_TOURISM);
        root.setLeft(sidebar);

        // Center Content
        VBox centerContent = new VBox(20);
        centerContent.setPadding(new Insets(24));
        centerContent.setFillWidth(true);

        // Header Card
        VBox headerCard = new VBox(8);
        headerCard.setPadding(new Insets(20));
        headerCard.setStyle("-fx-background-color: white; -fx-background-radius: 14; -fx-border-color: #E2E8F0; -fx-border-radius: 14; -fx-effect: dropshadow(three-pass-box, rgba(15,23,42,0.04), 8, 0, 0, 2);");

        Label titleLbl = new Label("✈  Medical Tourism Requests");
        titleLbl.setStyle("-fx-font-size: 22px; -fx-font-weight: 800; -fx-text-fill: #12355B;");

        Label subtitleLbl = new Label("Review incoming international patient cases, manage logistics & travel support requests, and schedule appointments.");
        subtitleLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

        headerCard.getChildren().addAll(titleLbl, subtitleLbl);

        // TabPane for Medical Tourism vs Travel Support
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-tab-min-width: 180px; -fx-tab-max-width: 250px; -fx-font-weight: bold;");

        Tab tourismTab = new Tab("📋  Medical Cases", createTourismCasesTabContent());
        tourismTab.setClosable(false);

        Tab travelTab = new Tab("✈  Travel Support Requests", createTravelSupportTabContent());
        travelTab.setClosable(false);

        tabPane.getTabs().addAll(tourismTab, travelTab);

        centerContent.getChildren().addAll(headerCard, tabPane);

        ScrollPane scrollPane = new ScrollPane(centerContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        root.setCenter(scrollPane);

        // Initial Data Load
        loadRequestsAsync();
        loadTravelRequestsAsync();

        return new Scene(root, stage.getWidth() > 0 ? stage.getWidth() : 1200, stage.getHeight() > 0 ? stage.getHeight() : 750);
    }

    // =========================================================================
    // TAB 1: MEDICAL TOURISM CASES
    // =========================================================================

    private VBox createTourismCasesTabContent() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(15, 0, 0, 0));
        content.setFillWidth(true);

        // Live Metrics Dashboard Bar (4 KPI cards)
        metricsBar = new HBox(15);
        metricsBar.setFillHeight(true);
        renderMetricsBar(new ArrayList<>());

        // Filter Bar
        HBox filterBar = new HBox(12);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(5, 0, 5, 0));

        Label filterLbl = new Label("Filter Case Status:");
        filterLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #172B4D;");

        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All Statuses", "PENDING", "ACCEPTED", "REJECTED", "MORE_INFORMATION_REQUIRED", "APPOINTMENT_SCHEDULED");
        statusFilter.setValue("All Statuses");
        statusFilter.setStyle("-fx-background-color: white; -fx-border-color: #CBD5E1; -fx-border-radius: 8; -fx-background-radius: 8;");
        statusFilter.setOnAction(e -> applyFilter());

        filterBar.getChildren().addAll(filterLbl, statusFilter);

        // Main List Container
        requestsContainer = new VBox(16);
        requestsContainer.setFillWidth(true);

        content.getChildren().addAll(metricsBar, filterBar, requestsContainer);
        return content;
    }

    private void renderMetricsBar(List<MedicalTourismRequest> requests) {
        metricsBar.getChildren().clear();

        long newCount = requests.stream().filter(r -> "PENDING".equalsIgnoreCase(r.getStatus())).count();
        long reviewCount = requests.stream().filter(r -> "UNDER_REVIEW".equalsIgnoreCase(r.getStatus()) || "MORE_INFORMATION_REQUIRED".equalsIgnoreCase(r.getStatus())).count();
        long acceptedCount = requests.stream().filter(r -> "ACCEPTED".equalsIgnoreCase(r.getStatus())).count();
        long scheduledCount = requests.stream().filter(r -> "APPOINTMENT_SCHEDULED".equalsIgnoreCase(r.getStatus())).count();

        VBox newCard = SummaryCard.create("New Requests", String.valueOf(newCount), "Awaiting hospital review", "⚡", SummaryCard.CardType.ORANGE);
        VBox reviewCard = SummaryCard.create("Under Review", String.valueOf(reviewCount), "In information review", "📋", SummaryCard.CardType.BLUE);
        VBox acceptedCard = SummaryCard.create("Accepted Cases", String.valueOf(acceptedCount), "Approved for treatment", "✓", SummaryCard.CardType.GREEN);
        VBox scheduledCard = SummaryCard.create("Upcoming Patients", String.valueOf(scheduledCount), "Scheduled appointments", "✈", SummaryCard.CardType.PURPLE);

        metricsBar.getChildren().addAll(newCard, reviewCard, acceptedCard, scheduledCard);
    }

    private void loadRequestsAsync() {
        requestsContainer.getChildren().clear();
        VBox shimmerBox = ShimmerPlaceholder.createListShimmer(3);
        requestsContainer.getChildren().add(shimmerBox);

        String hospitalId = SessionManager.getHospitalUid();

        Task<List<MedicalTourismRequest>> task = new Task<>() {
            @Override
            protected List<MedicalTourismRequest> call() throws Exception {
                List<MedicalTourismRequest> reqs = tourismController.getHospitalRequests(hospitalId);
                if (reqs == null || reqs.isEmpty()) {
                    reqs = tourismController.getHospitalRequests("hosp_default");
                }
                return reqs;
            }
        };

        task.setOnSucceeded(e -> {
            requestsContainer.getChildren().clear();
            allRequests = task.getValue();
            renderMetricsBar(allRequests);
            applyFilter();
        });

        task.setOnFailed(e -> {
            requestsContainer.getChildren().clear();
            Label errLbl = new Label("Unable to load Medical Tourism Requests from Firebase.");
            errLbl.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
            requestsContainer.getChildren().add(errLbl);
        });

        new Thread(task).start();
    }

    private void applyFilter() {
        requestsContainer.getChildren().clear();

        if (allRequests == null || allRequests.isEmpty()) {
            VBox emptyBox = new VBox(12);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(40));
            emptyBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-radius: 12;");

            Label iconLbl = new Label("✈");
            iconLbl.setStyle("-fx-font-size: 32px; -fx-text-fill: #2F80ED;");

            Label titleLbl = new Label("No Medical Tourism Requests");
            titleLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #12355B;");

            Label descLbl = new Label("There are currently no international or outstation patient requests for this hospital.");
            descLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

            emptyBox.getChildren().addAll(iconLbl, titleLbl, descLbl);
            requestsContainer.getChildren().add(emptyBox);
            return;
        }

        String filter = statusFilter.getValue();
        List<MedicalTourismRequest> filtered = allRequests.stream().filter(r -> {
            if (r == null) return false;
            if ("All Statuses".equalsIgnoreCase(filter)) return true;
            return filter.equalsIgnoreCase(r.getStatus());
        }).collect(Collectors.toList());

        if (filtered.isEmpty()) {
            VBox emptyBox = new VBox(10);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(30));
            Label msg = new Label("No requests match the selected status filter.");
            msg.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748B;");
            emptyBox.getChildren().add(msg);
            requestsContainer.getChildren().add(emptyBox);
            return;
        }

        for (MedicalTourismRequest req : filtered) {
            requestsContainer.getChildren().add(createRequestCard(req));
        }
    }

    private VBox createRequestCard(MedicalTourismRequest req) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #CBD5E1; -fx-border-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(15,23,42,0.03), 6, 0, 0, 2);");

        // Header
        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        Label patientName = new Label("Patient: " + (req.getPatientName() != null ? req.getPatientName() : "International Patient") + " (" + (req.getPatientType() != null ? req.getPatientType() : "International") + ")");
        patientName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #12355B;");

        Label treatLbl = new Label("Treatment: " + req.getTreatmentName() + " | Preferred Date: " + req.getPreferredDate());
        treatLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #475569;");

        titleBox.getChildren().addAll(patientName, treatLbl);

        // Status Badge
        Label statusBadge = new Label(req.getStatus());
        String bg = "#E2E8F0"; String fg = "#1E293B";
        if ("ACCEPTED".equalsIgnoreCase(req.getStatus())) { bg = "#ECFDF5"; fg = "#059669"; }
        else if ("REJECTED".equalsIgnoreCase(req.getStatus())) { bg = "#FEF2F2"; fg = "#DC2626"; }
        else if ("PENDING".equalsIgnoreCase(req.getStatus())) { bg = "#FEF3C7"; fg = "#D97706"; }
        else if ("APPOINTMENT_SCHEDULED".equalsIgnoreCase(req.getStatus())) { bg = "#EFF6FF"; fg = "#2563EB"; }
        statusBadge.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; -fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 16;");

        topRow.getChildren().addAll(titleBox, statusBadge);

        // Details Grid
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(8);
        grid.setPadding(new Insets(8, 0, 8, 0));

        grid.add(new Label("Budget:"), 0, 0);
        grid.add(new Label("₹" + req.getMinimumBudget() + " - ₹" + req.getMaximumBudget()), 1, 0);

        grid.add(new Label("Doctor Preference:"), 0, 1);
        grid.add(new Label(req.getDoctorName() != null ? req.getDoctorName() : "General Team"), 1, 1);

        grid.add(new Label("Travel Services:"), 2, 0);
        List<String> services = new ArrayList<>();
        if (req.isAirportAssistanceRequired()) services.add("Airport Pickup");
        if (req.isAccommodationRequired()) services.add("Hotel Accommodation");
        grid.add(new Label(services.isEmpty() ? "None" : String.join(", ", services)), 3, 0);

        grid.add(new Label("Attached Documents:"), 2, 1);
        grid.add(new Label(req.getMedicalDocuments() != null && !req.getMedicalDocuments().isEmpty() ? String.join(", ", req.getMedicalDocuments()) : "None"), 3, 1);

        // Action Buttons Row
        HBox actionRow = new HBox(12);
        actionRow.setAlignment(Pos.CENTER_RIGHT);
        actionRow.setPadding(new Insets(10, 0, 0, 0));

        Button acceptBtn = new Button("✓  Accept Request");
        acceptBtn.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        acceptBtn.setOnAction(e -> handleUpdateStatus(req, "ACCEPTED"));

        Button rejectBtn = new Button("✕  Reject");
        rejectBtn.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        rejectBtn.setOnAction(e -> handleUpdateStatus(req, "REJECTED"));

        Button reqInfoBtn = new Button("💬 Request Info");
        reqInfoBtn.setStyle("-fx-background-color: #D97706; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        reqInfoBtn.setOnAction(e -> handleUpdateStatus(req, "MORE_INFORMATION_REQUIRED"));

        Button scheduleBtn = new Button("📅 Schedule Appointment");
        scheduleBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        scheduleBtn.setOnAction(e -> showScheduleAppointmentDialog(req));

        if ("PENDING".equalsIgnoreCase(req.getStatus()) || "MORE_INFORMATION_REQUIRED".equalsIgnoreCase(req.getStatus())) {
            actionRow.getChildren().addAll(reqInfoBtn, rejectBtn, acceptBtn);
        } else if ("ACCEPTED".equalsIgnoreCase(req.getStatus())) {
            actionRow.getChildren().addAll(rejectBtn, scheduleBtn);
        } else {
            Label doneLbl = new Label("Status: " + req.getStatus());
            doneLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #64748B;");
            actionRow.getChildren().add(doneLbl);
        }

        card.getChildren().addAll(topRow, grid, actionRow);
        return card;
    }

    private void handleUpdateStatus(MedicalTourismRequest req, String newStatus) {
        String notes = "";
        if ("REJECTED".equalsIgnoreCase(newStatus) || "MORE_INFORMATION_REQUIRED".equalsIgnoreCase(newStatus)) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Update Status Notes");
            dialog.setHeaderText("Reason / Notes for " + newStatus);
            dialog.setContentText("Enter note for patient:");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                notes = result.get().trim();
            }
        }

        try {
            tourismController.updateStatusAndNotify(req.getRequestId(), newStatus, notes);
            showAlert("Success", "Request status updated to " + newStatus + " and notification sent to patient.");
            loadRequestsAsync();
        } catch (Exception ex) {
            showAlert("Error", "Could not update request status: " + ex.getMessage());
        }
    }

    private void showScheduleAppointmentDialog(MedicalTourismRequest req) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Schedule Appointment");
        dialog.setHeaderText("Create Consultation Appointment for " + req.getPatientName());

        ButtonType scheduleBtnType = new ButtonType("Schedule", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(scheduleBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField dateField = new TextField(req.getPreferredDate() != null ? req.getPreferredDate() : "2026-10-15");
        TextField timeField = new TextField("10:00 AM");

        grid.add(new Label("Appointment Date:"), 0, 0);
        grid.add(dateField, 1, 0);
        grid.add(new Label("Appointment Time:"), 0, 1);
        grid.add(timeField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == scheduleBtnType) {
                try {
                    Appointment apt = new Appointment();
                    apt.setAppointmentId(UUID.randomUUID().toString());
                    apt.setPatientUid(req.getPatientId());
                    apt.setPatientName(req.getPatientName());
                    apt.setHospitalId(req.getHospitalId());
                    apt.setDoctorUid(req.getDoctorId() != null ? req.getDoctorId() : "doc_default");
                    apt.setDoctorName(req.getDoctorName() != null ? req.getDoctorName() : "General Practitioner");
                    apt.setAppointmentDate(dateField.getText().trim());
                    apt.setAppointmentTime(timeField.getText().trim());
                    apt.setSpecialty(req.getTreatmentName());
                    apt.setReason("Medical Tourism Request: " + req.getTreatmentName());

                    appointmentDAO.createAppointment(apt);
                    tourismController.linkAppointment(req.getRequestId(), apt.getAppointmentId());

                    showAlert("Appointment Scheduled", "Appointment (" + apt.getAppointmentId() + ") successfully created and linked to the Medical Tourism Request!");
                    loadRequestsAsync();
                    return true;
                } catch (Exception ex) {
                    showAlert("Error", "Failed to schedule appointment: " + ex.getMessage());
                }
            }
            return false;
        });

        dialog.showAndWait();
    }

    // =========================================================================
    // TAB 2: TRAVEL SUPPORT REQUESTS MANAGEMENT
    // =========================================================================

    private VBox createTravelSupportTabContent() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(15, 0, 0, 0));
        content.setFillWidth(true);

        // Travel Metrics Bar
        travelMetricsBar = new HBox(15);
        travelMetricsBar.setFillHeight(true);
        renderTravelMetricsBar(new ArrayList<>());

        // Travel Filter Bar
        HBox filterBar = new HBox(12);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(5, 0, 5, 0));

        Label filterLbl = new Label("Filter Service Request Status:");
        filterLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #172B4D;");

        travelStatusFilter = new ComboBox<>();
        travelStatusFilter.getItems().addAll("All Statuses", "PENDING", "APPROVED", "REJECTED", "MORE_INFO_REQUIRED");
        travelStatusFilter.setValue("All Statuses");
        travelStatusFilter.setStyle("-fx-background-color: white; -fx-border-color: #CBD5E1; -fx-border-radius: 8; -fx-background-radius: 8;");
        travelStatusFilter.setOnAction(e -> applyTravelFilter());

        filterBar.getChildren().addAll(filterLbl, travelStatusFilter);

        // Main Travel List Container
        travelRequestsContainer = new VBox(16);
        travelRequestsContainer.setFillWidth(true);

        content.getChildren().addAll(travelMetricsBar, filterBar, travelRequestsContainer);
        return content;
    }

    private void renderTravelMetricsBar(List<TravelSupportRequest> requests) {
        travelMetricsBar.getChildren().clear();

        long pendingCount = requests.stream().filter(r -> "PENDING".equalsIgnoreCase(r.getStatus())).count();
        long approvedCount = requests.stream().filter(r -> "APPROVED".equalsIgnoreCase(r.getStatus())).count();
        long infoCount = requests.stream().filter(r -> "MORE_INFO_REQUIRED".equalsIgnoreCase(r.getStatus())).count();
        long totalCount = requests.size();

        VBox pendingCard = SummaryCard.create("Pending Services", String.valueOf(pendingCount), "Awaiting logistics approval", "⏳", SummaryCard.CardType.ORANGE);
        VBox approvedCard = SummaryCard.create("Approved Logistics", String.valueOf(approvedCount), "Confirmed travel support", "✓", SummaryCard.CardType.GREEN);
        VBox infoCard = SummaryCard.create("More Info Requested", String.valueOf(infoCount), "Patient details required", "💬", SummaryCard.CardType.BLUE);
        VBox totalCard = SummaryCard.create("Total Travel Requests", String.valueOf(totalCount), "All travel requests", "🧳", SummaryCard.CardType.PURPLE);

        travelMetricsBar.getChildren().addAll(pendingCard, approvedCard, infoCard, totalCard);
    }

    private void loadTravelRequestsAsync() {
        travelRequestsContainer.getChildren().clear();
        VBox shimmerBox = ShimmerPlaceholder.createListShimmer(2);
        travelRequestsContainer.getChildren().add(shimmerBox);

        String hospitalId = SessionManager.getHospitalUid();

        Task<List<TravelSupportRequest>> task = new Task<>() {
            @Override
            protected List<TravelSupportRequest> call() throws Exception {
                List<TravelSupportRequest> reqs = travelSupportController.getHospitalRequests(hospitalId);
                if (reqs == null || reqs.isEmpty()) {
                    reqs = travelSupportController.getHospitalRequests("hosp_default");
                }
                return reqs;
            }
        };

        task.setOnSucceeded(e -> {
            travelRequestsContainer.getChildren().clear();
            allTravelRequests = task.getValue();
            renderTravelMetricsBar(allTravelRequests);
            applyTravelFilter();
        });

        task.setOnFailed(e -> {
            travelRequestsContainer.getChildren().clear();
            Label errLbl = new Label("Unable to load Travel Support Requests from Firebase.");
            errLbl.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
            travelRequestsContainer.getChildren().add(errLbl);
        });

        new Thread(task).start();
    }

    private void applyTravelFilter() {
        travelRequestsContainer.getChildren().clear();

        if (allTravelRequests == null || allTravelRequests.isEmpty()) {
            VBox emptyBox = new VBox(12);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(40));
            emptyBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-radius: 12;");

            Label iconLbl = new Label("🧳");
            iconLbl.setStyle("-fx-font-size: 32px; -fx-text-fill: #2F80ED;");

            Label titleLbl = new Label("No Travel Support Requests");
            titleLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #12355B;");

            Label descLbl = new Label("Patients have not submitted airport pickup, accommodation, or travel coordination requests yet.");
            descLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

            emptyBox.getChildren().addAll(iconLbl, titleLbl, descLbl);
            travelRequestsContainer.getChildren().add(emptyBox);
            return;
        }

        String filter = travelStatusFilter.getValue();
        List<TravelSupportRequest> filtered = allTravelRequests.stream().filter(r -> {
            if (r == null) return false;
            if ("All Statuses".equalsIgnoreCase(filter)) return true;
            return filter.equalsIgnoreCase(r.getStatus());
        }).collect(Collectors.toList());

        if (filtered.isEmpty()) {
            VBox emptyBox = new VBox(10);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(30));
            Label msg = new Label("No travel requests match the selected status filter.");
            msg.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748B;");
            emptyBox.getChildren().add(msg);
            travelRequestsContainer.getChildren().add(emptyBox);
            return;
        }

        for (TravelSupportRequest req : filtered) {
            travelRequestsContainer.getChildren().add(createTravelRequestCard(req));
        }
    }

    private VBox createTravelRequestCard(TravelSupportRequest req) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #CBD5E1; -fx-border-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(15,23,42,0.03), 6, 0, 0, 2);");

        // Header Row
        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        String serviceIcon = "✈";
        if ("ACCOMMODATION".equalsIgnoreCase(req.getServiceType())) serviceIcon = "🏨";
        else if ("LOCAL_TRANSPORTATION".equalsIgnoreCase(req.getServiceType())) serviceIcon = "🚘";
        else if ("LANGUAGE_ASSISTANCE".equalsIgnoreCase(req.getServiceType())) serviceIcon = "🗣";
        else if ("TRAVEL_COORDINATION".equalsIgnoreCase(req.getServiceType())) serviceIcon = "🧭";
        else if ("EMERGENCY_INFO".equalsIgnoreCase(req.getServiceType())) serviceIcon = "🚨";

        Label serviceLbl = new Label(serviceIcon + "  " + req.getServiceType().replace("_", " ") + " Request");
        serviceLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #12355B;");

        Label patientInfo = new Label("Patient: " + (req.getPatientName() != null ? req.getPatientName() : "International Patient") + " | Request Date: " + (req.getRequestDate() != null ? req.getRequestDate() : "Recent"));
        patientInfo.setStyle("-fx-font-size: 13px; -fx-text-fill: #475569;");

        titleBox.getChildren().addAll(serviceLbl, patientInfo);

        // Status Badge
        Label statusBadge = new Label(req.getStatus());
        String bg = "#FEF3C7"; String fg = "#D97706";
        if ("APPROVED".equalsIgnoreCase(req.getStatus())) { bg = "#ECFDF5"; fg = "#059669"; }
        else if ("REJECTED".equalsIgnoreCase(req.getStatus())) { bg = "#FEF2F2"; fg = "#DC2626"; }
        else if ("MORE_INFO_REQUIRED".equalsIgnoreCase(req.getStatus())) { bg = "#EFF6FF"; fg = "#2563EB"; }
        statusBadge.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; -fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 16;");

        topRow.getChildren().addAll(titleBox, statusBadge);

        // Details Grid
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(8);
        grid.setPadding(new Insets(8, 0, 8, 0));

        grid.add(new Label("Case Details:"), 0, 0);
        grid.add(new Label("Case ID: " + (req.getMedicalTourismRequestId() != null ? req.getMedicalTourismRequestId() : "N/A")), 1, 0);

        grid.add(new Label("Service Details:"), 0, 1);
        grid.add(new Label(req.getDetails() != null ? req.getDetails() : "Standard Support Requested"), 1, 1);

        grid.add(new Label("Hospital Notes:"), 2, 0);
        grid.add(new Label(req.getHospitalNotes() != null && !req.getHospitalNotes().isBlank() ? req.getHospitalNotes() : "None"), 3, 0);

        grid.add(new Label("Last Updated:"), 2, 1);
        grid.add(new Label(req.getUpdatedAt() != null ? req.getUpdatedAt() : "N/A"), 3, 1);

        // Actions Row
        HBox actionRow = new HBox(12);
        actionRow.setAlignment(Pos.CENTER_RIGHT);
        actionRow.setPadding(new Insets(10, 0, 0, 0));

        Button approveBtn = new Button("✓ Approve Request");
        approveBtn.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        approveBtn.setOnAction(e -> handleUpdateTravelStatus(req, "APPROVED"));

        Button rejectBtn = new Button("✕ Reject");
        rejectBtn.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        rejectBtn.setOnAction(e -> handleUpdateTravelStatus(req, "REJECTED"));

        Button reqInfoBtn = new Button("💬 Request Info");
        reqInfoBtn.setStyle("-fx-background-color: #D97706; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        reqInfoBtn.setOnAction(e -> handleUpdateTravelStatus(req, "MORE_INFO_REQUIRED"));

        if ("PENDING".equalsIgnoreCase(req.getStatus()) || "MORE_INFO_REQUIRED".equalsIgnoreCase(req.getStatus())) {
            actionRow.getChildren().addAll(reqInfoBtn, rejectBtn, approveBtn);
        } else {
            Label doneLbl = new Label("Status Updated: " + req.getStatus());
            doneLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #64748B;");
            actionRow.getChildren().add(doneLbl);
        }

        card.getChildren().addAll(topRow, grid, actionRow);
        return card;
    }

    private void handleUpdateTravelStatus(TravelSupportRequest req, String newStatus) {
        String notes = "";
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Travel Request Notes");
        dialog.setHeaderText("Add hospital confirmation/notes for " + newStatus);
        dialog.setContentText("Message to Patient:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            notes = result.get().trim();
        }

        try {
            travelSupportController.updateStatusAndNotify(req.getRequestId(), newStatus, notes);
            showAlert("Success", "Travel Support Request status updated to " + newStatus + " and patient notified.");
            loadTravelRequestsAsync();
        } catch (Exception ex) {
            showAlert("Error", "Could not update Travel Support status: " + ex.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
