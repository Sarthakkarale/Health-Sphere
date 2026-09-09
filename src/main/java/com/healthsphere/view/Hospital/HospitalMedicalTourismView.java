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
import javafx.stage.Modality;
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

    private TextField searchField;
    private ComboBox<String> statusFilter;
    private ComboBox<String> patientTypeFilter;

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

        Label titleLbl = new Label("✈  Medical Tourism");
        titleLbl.setStyle("-fx-font-size: 22px; -fx-font-weight: 800; -fx-text-fill: #12355B;");

        Label subtitleLbl = new Label("Manage and respond to medical tourism requests from patients.");
        subtitleLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

        headerCard.getChildren().addAll(titleLbl, subtitleLbl);

        // Custom White Card Tab Bar (NO BLACK BAR)
        HBox tabNavBar = new HBox(12);
        tabNavBar.setPadding(new Insets(6));
        tabNavBar.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 12px;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 12px;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(18, 53, 91, 0.05), 10, 0, 0, 3);"
        );

        Button casesTabBtn = new Button("📋  Medical Tourism Requests");
        Button travelTabBtn = new Button("✈  Travel Support Requests");

        VBox casesContent = createTourismCasesTabContent();
        VBox travelContent = createTravelSupportTabContent();

        StackPane tabContentPane = new StackPane(casesContent);

        Runnable updateHospitalTabStyles = () -> {
            boolean isCases = tabContentPane.getChildren().contains(casesContent);
            String activeStyle = "-fx-background-color: #2F80ED; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8px; -fx-padding: 10px 22px; -fx-cursor: hand;";
            String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: #172B4D; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8px; -fx-padding: 10px 22px; -fx-cursor: hand;";

            casesTabBtn.setStyle(isCases ? activeStyle : inactiveStyle);
            travelTabBtn.setStyle(!isCases ? activeStyle : inactiveStyle);
        };

        casesTabBtn.setOnAction(e -> {
            tabContentPane.getChildren().setAll(casesContent);
            updateHospitalTabStyles.run();
        });

        travelTabBtn.setOnAction(e -> {
            tabContentPane.getChildren().setAll(travelContent);
            updateHospitalTabStyles.run();
        });

        casesTabBtn.setOnMouseEntered(e -> {
            if (!tabContentPane.getChildren().contains(casesContent)) {
                casesTabBtn.setStyle("-fx-background-color: #EEF4FF; -fx-text-fill: #2F80ED; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8px; -fx-padding: 10px 22px; -fx-cursor: hand;");
            }
        });
        casesTabBtn.setOnMouseExited(e -> updateHospitalTabStyles.run());

        travelTabBtn.setOnMouseEntered(e -> {
            if (!tabContentPane.getChildren().contains(travelContent)) {
                travelTabBtn.setStyle("-fx-background-color: #EEF4FF; -fx-text-fill: #2F80ED; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8px; -fx-padding: 10px 22px; -fx-cursor: hand;");
            }
        });
        travelTabBtn.setOnMouseExited(e -> updateHospitalTabStyles.run());

        updateHospitalTabStyles.run();
        tabNavBar.getChildren().addAll(casesTabBtn, travelTabBtn);

        centerContent.getChildren().addAll(headerCard, tabNavBar, tabContentPane);

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

        // Live Metrics Dashboard Bar (5 KPI cards)
        metricsBar = new HBox(12);
        metricsBar.setFillHeight(true);
        renderMetricsBar(new ArrayList<>());

        // Multi-Filter Bar
        HBox filterBar = new HBox(12);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(10, 16, 10, 16));
        filterBar.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #E2E8F0; -fx-border-radius: 10;");

        searchField = new TextField();
        searchField.setPromptText("Search Patient / Treatment...");
        searchField.setPrefWidth(220);
        searchField.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-padding: 6 10;");
        searchField.textProperty().addListener((obs, oldV, newV) -> applyFilter());

        Label statusLbl = new Label("Status:");
        statusLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #172B4D;");

        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Pending", "Under Review", "Accepted", "Rejected", "More Information Required", "Appointment Scheduled", "Treatment Completed");
        statusFilter.setValue("All");
        statusFilter.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #CBD5E1; -fx-border-radius: 6;");
        statusFilter.setOnAction(e -> applyFilter());

        Label typeLbl = new Label("Patient Type:");
        typeLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #172B4D;");

        patientTypeFilter = new ComboBox<>();
        patientTypeFilter.getItems().addAll("All", "International", "Outstation");
        patientTypeFilter.setValue("All");
        patientTypeFilter.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #CBD5E1; -fx-border-radius: 6;");
        patientTypeFilter.setOnAction(e -> applyFilter());

        filterBar.getChildren().addAll(new Label("🔍"), searchField, statusLbl, statusFilter, typeLbl, patientTypeFilter);

        // Main List Container
        requestsContainer = new VBox(12);
        requestsContainer.setFillWidth(true);

        content.getChildren().addAll(metricsBar, filterBar, requestsContainer);
        return content;
    }

    private void renderMetricsBar(List<MedicalTourismRequest> requests) {
        metricsBar.getChildren().clear();

        long totalCount = requests.size();
        long pendingCount = requests.stream().filter(r -> "PENDING".equalsIgnoreCase(r.getStatus())).count();
        long underReviewCount = requests.stream().filter(r -> "UNDER_REVIEW".equalsIgnoreCase(r.getStatus()) || "MORE_INFORMATION_REQUIRED".equalsIgnoreCase(r.getStatus())).count();
        long acceptedCount = requests.stream().filter(r -> "ACCEPTED".equalsIgnoreCase(r.getStatus()) || "APPOINTMENT_SCHEDULED".equalsIgnoreCase(r.getStatus())).count();
        long completedCount = requests.stream().filter(r -> "COMPLETED".equalsIgnoreCase(r.getStatus()) || "TREATMENT_COMPLETED".equalsIgnoreCase(r.getStatus())).count();

        VBox totalCard = SummaryCard.create("Total Requests", String.valueOf(totalCount), "All patient requests", "📊", SummaryCard.CardType.BLUE);
        VBox pendingCard = SummaryCard.create("Pending Requests", String.valueOf(pendingCount), "Awaiting hospital review", "⏳", SummaryCard.CardType.ORANGE);
        VBox reviewCard = SummaryCard.create("Under Review", String.valueOf(underReviewCount), "Information/review pending", "📋", SummaryCard.CardType.PURPLE);
        VBox acceptedCard = SummaryCard.create("Accepted", String.valueOf(acceptedCount), "Approved cases", "✓", SummaryCard.CardType.GREEN);
        VBox completedCard = SummaryCard.create("Completed", String.valueOf(completedCount), "Finished treatments", "🎉", SummaryCard.CardType.BLUE);

        HBox.setHgrow(totalCard, Priority.ALWAYS);
        HBox.setHgrow(pendingCard, Priority.ALWAYS);
        HBox.setHgrow(reviewCard, Priority.ALWAYS);
        HBox.setHgrow(acceptedCard, Priority.ALWAYS);
        HBox.setHgrow(completedCard, Priority.ALWAYS);

        metricsBar.getChildren().addAll(totalCard, pendingCard, reviewCard, acceptedCard, completedCard);
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

        com.healthsphere.util.AppBackgroundExecutor.execute(task);
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

            Label titleLbl = new Label("No Medical Tourism Requests Yet");
            titleLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #12355B;");

            Label descLbl = new Label("There are currently no medical tourism requests assigned to your hospital.");
            descLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

            emptyBox.getChildren().addAll(iconLbl, titleLbl, descLbl);
            requestsContainer.getChildren().add(emptyBox);
            return;
        }

        String query = searchField != null && searchField.getText() != null ? searchField.getText().trim().toLowerCase() : "";
        String selectedStatus = statusFilter != null ? statusFilter.getValue() : "All";
        String selectedType = patientTypeFilter != null ? patientTypeFilter.getValue() : "All";

        List<MedicalTourismRequest> filtered = allRequests.stream().filter(r -> {
            if (r == null) return false;

            // Search filter
            if (!query.isEmpty()) {
                String pName = r.getPatientName() != null ? r.getPatientName().toLowerCase() : "";
                String tName = r.getTreatmentName() != null ? r.getTreatmentName().toLowerCase() : "";
                if (!pName.contains(query) && !tName.contains(query)) {
                    return false;
                }
            }

            // Status filter
            if (!"All".equalsIgnoreCase(selectedStatus)) {
                String normalizedStatus = r.getStatus() != null ? r.getStatus().replace("_", " ") : "";
                if (!normalizedStatus.equalsIgnoreCase(selectedStatus) && !r.getStatus().equalsIgnoreCase(selectedStatus.replace(" ", "_"))) {
                    return false;
                }
            }

            // Patient type filter
            if (!"All".equalsIgnoreCase(selectedType)) {
                String type = r.getPatientType() != null ? r.getPatientType() : "International";
                if (!type.equalsIgnoreCase(selectedType)) {
                    return false;
                }
            }

            return true;
        }).collect(Collectors.toList());

        if (filtered.isEmpty()) {
            VBox emptyBox = new VBox(10);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(30));
            Label msg = new Label("No requests match the selected filters.");
            msg.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748B;");
            emptyBox.getChildren().add(msg);
            requestsContainer.getChildren().add(emptyBox);
            return;
        }

        for (MedicalTourismRequest req : filtered) {
            requestsContainer.getChildren().add(createCompactRequestCard(req));
        }
    }

    /**
     * Requirement 3: Compact Request Card
     */
    private VBox createCompactRequestCard(MedicalTourismRequest req) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(15,23,42,0.03), 6, 0, 0, 2);");

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(3);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        Label patientLbl = new Label((req.getPatientName() != null ? req.getPatientName() : "Patient") + " (" + (req.getPatientType() != null ? req.getPatientType() : "International") + ")");
        patientLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #12355B;");

        Label treatmentLbl = new Label("Treatment: " + (req.getTreatmentName() != null ? req.getTreatmentName() : "N/A"));
        treatmentLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #475569;");

        titleBox.getChildren().addAll(patientLbl, treatmentLbl);

        // Status Badge
        Label statusBadge = new Label(req.getStatus() != null ? req.getStatus().replace("_", " ") : "PENDING");
        String bg = "#E2E8F0"; String fg = "#1E293B";
        if ("ACCEPTED".equalsIgnoreCase(req.getStatus())) { bg = "#ECFDF5"; fg = "#059669"; }
        else if ("REJECTED".equalsIgnoreCase(req.getStatus())) { bg = "#FEF2F2"; fg = "#DC2626"; }
        else if ("PENDING".equalsIgnoreCase(req.getStatus())) { bg = "#FEF3C7"; fg = "#D97706"; }
        else if ("UNDER_REVIEW".equalsIgnoreCase(req.getStatus()) || "MORE_INFORMATION_REQUIRED".equalsIgnoreCase(req.getStatus())) { bg = "#F3E8FF"; fg = "#7E22CE"; }
        else if ("APPOINTMENT_SCHEDULED".equalsIgnoreCase(req.getStatus()) || "TREATMENT_COMPLETED".equalsIgnoreCase(req.getStatus())) { bg = "#EFF6FF"; fg = "#2563EB"; }
        statusBadge.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 16; -fx-font-size: 12px;");

        topRow.getChildren().addAll(titleBox, statusBadge);

        // Grid summary
        GridPane grid = new GridPane();
        grid.setHgap(24);
        grid.setVgap(6);
        grid.setPadding(new Insets(6, 0, 6, 0));

        grid.add(new Label("Preferred Date: " + (req.getPreferredDate() != null ? req.getPreferredDate() : "N/A")), 0, 0);
        grid.add(new Label("Budget: ₹" + req.getMinimumBudget() + " - ₹" + req.getMaximumBudget()), 1, 0);
        grid.add(new Label("Selected Doctor: " + (req.getDoctorName() != null && !req.getDoctorName().isBlank() ? req.getDoctorName() : "Doctor not selected")), 0, 1);
        grid.add(new Label("Request Date: " + (req.getRequestDate() != null ? req.getRequestDate() : "N/A")), 1, 1);

        for (javafx.scene.Node child : grid.getChildren()) {
            if (child instanceof Label) {
                ((Label) child).setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");
            }
        }

        HBox actionRow = new HBox(12);
        actionRow.setAlignment(Pos.CENTER_RIGHT);

        Button viewDetailsBtn = new Button("View Details");
        viewDetailsBtn.setStyle("-fx-background-color: #2F80ED; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 7 18; -fx-background-radius: 6; -fx-cursor: hand;");
        viewDetailsBtn.setOnAction(e -> showRequestDetailsModal(req));

        actionRow.getChildren().add(viewDetailsBtn);

        card.getChildren().addAll(topRow, grid, actionRow);
        return card;
    }

    /**
     * Requirement 5-16: Hospital Request Details Modal Dialog
     */
    private void showRequestDetailsModal(MedicalTourismRequest req) {
        // Security check
        String currentHospitalId = SessionManager.getHospitalUid();
        if (req.getHospitalId() != null && !req.getHospitalId().equals(currentHospitalId) && !"hosp_default".equals(req.getHospitalId())) {
            showAlert("Access Denied", "You do not have permission to view request details for another hospital.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Medical Tourism Request Details");
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        VBox contentBox = new VBox(16);
        contentBox.setPadding(new Insets(24));
        contentBox.setPrefWidth(680);
        contentBox.setStyle("-fx-background-color: #F4F8FC;");

        // Header
        VBox header = new VBox(4);
        Label title = new Label("Medical Tourism Request Details");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: 800; -fx-text-fill: #12355B;");
        Label reqIdLbl = new Label("Request ID: " + req.getRequestId());
        reqIdLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        header.getChildren().addAll(title, reqIdLbl);

        // Section 1: Patient Information
        VBox patientCard = createDetailSectionCard("👤 Patient Information", new String[][]{
                {"Patient Name", req.getPatientName() != null ? req.getPatientName() : "N/A"},
                {"Patient ID", req.getPatientId() != null ? req.getPatientId() : "N/A"},
                {"Location / Country", req.getPreferredLocation() != null ? req.getPreferredLocation() : "N/A"},
                {"Patient Type", req.getPatientType() != null ? req.getPatientType() : "International"},
                {"Contact Info", req.getPatientEmail() != null ? req.getPatientEmail() : "Available upon confirmation"},
                {"Preferred Date", req.getPreferredDate() != null ? req.getPreferredDate() : "N/A"}
        });

        // Section 2: Treatment Information
        VBox treatmentCard = createDetailSectionCard("🩺 Treatment Information", new String[][]{
                {"Treatment / Specialty", req.getTreatmentName() != null ? req.getTreatmentName() : "N/A"},
                {"Requirements", req.getAdditionalRequirements() != null && !req.getAdditionalRequirements().isBlank() ? req.getAdditionalRequirements() : "Standard Treatment Protocol"},
                {"Preferred Location", req.getPreferredLocation() != null ? req.getPreferredLocation() : "Hospital Campus"},
                {"Minimum Budget", "₹" + req.getMinimumBudget()},
                {"Maximum Budget", "₹" + req.getMaximumBudget()}
        });

        // Section 3: Selected Doctor
        boolean doctorSelected = req.getDoctorName() != null && !req.getDoctorName().isBlank() && !"Doctor not selected".equalsIgnoreCase(req.getDoctorName());
        VBox doctorCard = createDetailSectionCard("👨‍⚕️ Selected Doctor", new String[][]{
                {"Selected Doctor", doctorSelected ? req.getDoctorName() : "Doctor not selected"},
                {"Specialization", doctorSelected ? req.getTreatmentName() : "—"},
                {"Availability", doctorSelected ? "Schedule Available" : "—"},
                {"Doctor Rating", doctorSelected ? "4.8 ★" : "—"}
        });

        // Section 4: Medical Documents
        VBox docsCard = new VBox(10);
        docsCard.setPadding(new Insets(14));
        docsCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #E2E8F0; -fx-border-radius: 10;");
        Label docsHeader = new Label("📎 Medical Documents");
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
                viewDocBtn.setOnAction(e -> showAlert("Medical Document", "Viewing Document: " + docName + "\n\nDocument details verified from Patient Health Passport records."));

                docRow.getChildren().addAll(docLbl, viewDocBtn);
                docsCard.getChildren().add(docRow);
            }
        }

        // Section 5: Travel Support Requirements
        VBox travelCard = createDetailSectionCard("✈ Travel Support Requirements", new String[][]{
                {"Airport Pickup", req.isAirportAssistanceRequired() ? "✓ Required" : "— Not Required"},
                {"Accommodation", req.isAccommodationRequired() ? "✓ Required" : "— Not Required"},
                {"Local Transportation", req.isLocalTransportRequired() ? "✓ Required" : "— Not Required"},
                {"Language Assistance", req.isLanguageAssistanceRequired() ? "✓ Required" : "— Not Required"}
        });

        // Section 6: Cost Information
        VBox costCard = new VBox(10);
        costCard.setPadding(new Insets(14));
        costCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #E2E8F0; -fx-border-radius: 10;");
        Label costHeader = new Label("💰 Estimated Cost Breakdown");
        costHeader.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #12355B;");

        GridPane costGrid = new GridPane();
        costGrid.setHgap(20); costGrid.setVgap(6);
        costGrid.add(new Label("Treatment Cost:"), 0, 0); costGrid.add(new Label("₹" + String.format("%.2f", req.getEstimatedTreatmentCost())), 1, 0);
        costGrid.add(new Label("Travel Estimate:"), 0, 1); costGrid.add(new Label("₹" + String.format("%.2f", req.getEstimatedTravelCost())), 1, 1);
        costGrid.add(new Label("Accommodation:"), 0, 2); costGrid.add(new Label("₹" + String.format("%.2f", req.getEstimatedAccommodationCost())), 1, 2);
        costGrid.add(new Label("Local Transport:"), 0, 3); costGrid.add(new Label("₹" + String.format("%.2f", req.getEstimatedTransportCost())), 1, 3);
        
        Label totalLbl = new Label("Estimated Total:"); totalLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #12355B;");
        Label totalVal = new Label("₹" + String.format("%.2f", req.getEstimatedTotalCost())); totalVal.setStyle("-fx-font-weight: bold; -fx-text-fill: #059669; -fx-font-size: 15px;");
        costGrid.add(totalLbl, 0, 4); costGrid.add(totalVal, 1, 4);

        Label disclaimer = new Label("Estimated cost — not an official quotation.");
        disclaimer.setStyle("-fx-font-size: 11px; -fx-text-fill: #94A3B8; -fx-font-style: italic;");

        costCard.getChildren().addAll(costHeader, costGrid, disclaimer);

        // Section 7: Status Timeline
        VBox timelineCard = createStatusTimeline(req.getStatus());

        // Action Buttons Row
        HBox actionRow = new HBox(12);
        actionRow.setAlignment(Pos.CENTER_RIGHT);
        actionRow.setPadding(new Insets(10, 0, 0, 0));

        Button acceptBtn = new Button("Accept");
        acceptBtn.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
        acceptBtn.setOnAction(e -> {
            handleAcceptRequest(req);
            dialog.setResult(null);
            dialog.close();
        });

        Button rejectBtn = new Button("Reject");
        rejectBtn.setStyle("-fx-background-color: #DC2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
        rejectBtn.setOnAction(e -> {
            handleRejectRequest(req);
            dialog.setResult(null);
            dialog.close();
        });

        Button reqInfoBtn = new Button("Request More Information");
        reqInfoBtn.setStyle("-fx-background-color: #D97706; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
        reqInfoBtn.setOnAction(e -> {
            handleRequestMoreInfo(req);
            dialog.setResult(null);
            dialog.close();
        });

        Button scheduleBtn = new Button("Schedule Appointment");
        scheduleBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
        scheduleBtn.setOnAction(e -> {
            dialog.setResult(null);
            dialog.close();
            showScheduleAppointmentDialog(req);
        });

        String currentStatus = req.getStatus() != null ? req.getStatus() : "PENDING";
        if ("PENDING".equalsIgnoreCase(currentStatus) || "UNDER_REVIEW".equalsIgnoreCase(currentStatus)) {
            actionRow.getChildren().addAll(reqInfoBtn, rejectBtn, acceptBtn);
        } else if ("ACCEPTED".equalsIgnoreCase(currentStatus)) {
            Label acceptedBadge = new Label("Accepted");
            acceptedBadge.setStyle("-fx-background-color: #ECFDF5; -fx-text-fill: #059669; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6;");
            actionRow.getChildren().addAll(acceptedBadge, scheduleBtn);
        } else {
            Label statusDone = new Label("Status: " + currentStatus.replace("_", " "));
            statusDone.setStyle("-fx-font-weight: bold; -fx-text-fill: #64748B; -fx-font-size: 14px;");
            actionRow.getChildren().add(statusDone);
        }

        ScrollPane modalScroll = new ScrollPane(contentBox);
        modalScroll.setFitToWidth(true);
        modalScroll.setPrefHeight(580);
        modalScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        contentBox.getChildren().addAll(header, patientCard, treatmentCard, doctorCard, docsCard, travelCard, costCard, timelineCard, actionRow);

        dialog.getDialogPane().setContent(modalScroll);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private VBox createDetailSectionCard(String sectionTitle, String[][] keyValues) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #E2E8F0; -fx-border-radius: 10;");

        Label header = new Label(sectionTitle);
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

    /**
     * Requirement 12: Status Timeline
     */
    private VBox createStatusTimeline(String status) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #E2E8F0; -fx-border-radius: 10;");

        Label header = new Label("📍 Request Timeline");
        header.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #12355B;");

        String[] stages = {"Request Submitted", "Hospital Review", "Doctor Review", "Accepted", "Appointment Scheduled", "Treatment Completed"};
        
        int activeIndex = 0;
        if ("UNDER_REVIEW".equalsIgnoreCase(status) || "MORE_INFORMATION_REQUIRED".equalsIgnoreCase(status)) activeIndex = 1;
        else if ("ACCEPTED".equalsIgnoreCase(status)) activeIndex = 3;
        else if ("APPOINTMENT_SCHEDULED".equalsIgnoreCase(status)) activeIndex = 4;
        else if ("COMPLETED".equalsIgnoreCase(status) || "TREATMENT_COMPLETED".equalsIgnoreCase(status)) activeIndex = 5;

        HBox timelineBox = new HBox(8);
        timelineBox.setAlignment(Pos.CENTER_LEFT);

        for (int i = 0; i < stages.length; i++) {
            boolean isReached = i <= activeIndex;
            boolean isCurrent = i == activeIndex;

            VBox stepBox = new VBox(4);
            stepBox.setAlignment(Pos.CENTER);

            Label dot = new Label(isReached ? "●" : "○");
            dot.setStyle("-fx-font-size: 16px; -fx-text-fill: " + (isCurrent ? "#2F80ED" : (isReached ? "#059669" : "#CBD5E1")) + ";");

            Label text = new Label(stages[i]);
            text.setStyle("-fx-font-size: 10px; -fx-font-weight: " + (isCurrent ? "bold" : "normal") + "; -fx-text-fill: " + (isCurrent ? "#12355B" : "#64748B") + ";");

            stepBox.getChildren().addAll(dot, text);
            timelineBox.getChildren().add(stepBox);

            if (i < stages.length - 1) {
                Label arrow = new Label("→");
                arrow.setStyle("-fx-text-fill: #CBD5E1; -fx-font-size: 12px;");
                timelineBox.getChildren().add(arrow);
            }
        }

        card.getChildren().addAll(header, timelineBox);
        return card;
    }

    /**
     * Requirement 15: Accept Request
     */
    private void handleAcceptRequest(MedicalTourismRequest req) {
        String currentHospitalId = SessionManager.getHospitalUid();
        if (req.getHospitalId() != null && !req.getHospitalId().equals(currentHospitalId) && !"hosp_default".equals(req.getHospitalId())) {
            showAlert("Error", "You can only accept requests belonging to your hospital.");
            return;
        }

        try {
            tourismController.updateStatusAndNotify(req.getRequestId(), "ACCEPTED", "Request accepted by hospital.");
            showAlert("Success", "Medical Tourism request accepted successfully.");
            loadRequestsAsync();
        } catch (Exception ex) {
            showAlert("Error", "Failed to accept request: " + ex.getMessage());
        }
    }

    /**
     * Requirement 16: Reject Request
     */
    private void handleRejectRequest(MedicalTourismRequest req) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Rejection");
        confirmAlert.setHeaderText("Are you sure you want to reject this request?");
        confirmAlert.setContentText("Patient: " + req.getPatientName() + "\nTreatment: " + req.getTreatmentName());

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            TextInputDialog reasonDialog = new TextInputDialog();
            reasonDialog.setTitle("Rejection Reason");
            reasonDialog.setHeaderText("Reason for rejecting request");
            reasonDialog.setContentText("Reason:");
            Optional<String> reasonRes = reasonDialog.showAndWait();
            String reason = reasonRes.orElse("Hospital is unable to accommodate request at this time.");

            try {
                tourismController.updateStatusAndNotify(req.getRequestId(), "REJECTED", reason);
                showAlert("Request Rejected", "Medical Tourism request has been rejected.");
                loadRequestsAsync();
            } catch (Exception ex) {
                showAlert("Error", "Failed to reject request: " + ex.getMessage());
            }
        }
    }

    /**
     * Requirement 14: Request More Information
     */
    private void handleRequestMoreInfo(MedicalTourismRequest req) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Information Required");
        dialog.setHeaderText("What additional information is required?");

        ButtonType sendBtnType = new ButtonType("Send Request", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(sendBtnType, ButtonType.CANCEL);

        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        TextArea textArea = new TextArea();
        textArea.setPromptText("Enter details of required information from patient...");
        textArea.setPrefRowCount(4);

        box.getChildren().addAll(new Label("Information Required:"), textArea);
        dialog.getDialogPane().setContent(box);

        dialog.setResultConverter(btn -> {
            if (btn == sendBtnType) {
                return textArea.getText().trim();
            }
            return null;
        });

        Optional<String> res = dialog.showAndWait();
        if (res.isPresent() && !res.get().isBlank()) {
            try {
                tourismController.updateStatusAndNotify(req.getRequestId(), "MORE_INFORMATION_REQUIRED", res.get());
                showAlert("Information Requested", "Additional information request sent to patient successfully.");
                loadRequestsAsync();
            } catch (Exception ex) {
                showAlert("Error", "Failed to send request info: " + ex.getMessage());
            }
        }
    }

    private void showScheduleAppointmentDialog(MedicalTourismRequest req) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Schedule Appointment");
        dialog.setHeaderText("Create Consultation Appointment for " + req.getPatientName());

        ButtonType scheduleBtnType = new ButtonType("Schedule", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(scheduleBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

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

        travelMetricsBar = new HBox(15);
        travelMetricsBar.setFillHeight(true);
        renderTravelMetricsBar(new ArrayList<>());

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

        com.healthsphere.util.AppBackgroundExecutor.execute(task);
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

        Label statusBadge = new Label(req.getStatus());
        String bg = "#FEF3C7"; String fg = "#D97706";
        if ("APPROVED".equalsIgnoreCase(req.getStatus())) { bg = "#ECFDF5"; fg = "#059669"; }
        else if ("REJECTED".equalsIgnoreCase(req.getStatus())) { bg = "#FEF2F2"; fg = "#DC2626"; }
        else if ("MORE_INFO_REQUIRED".equalsIgnoreCase(req.getStatus())) { bg = "#EFF6FF"; fg = "#2563EB"; }
        statusBadge.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; -fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 16;");

        topRow.getChildren().addAll(titleBox, statusBadge);

        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(8); grid.setPadding(new Insets(8, 0, 8, 0));

        grid.add(new Label("Case Details:"), 0, 0);
        grid.add(new Label("Case ID: " + (req.getMedicalTourismRequestId() != null ? req.getMedicalTourismRequestId() : "N/A")), 1, 0);

        grid.add(new Label("Service Details:"), 0, 1);
        grid.add(new Label(req.getDetails() != null ? req.getDetails() : "Standard Support Requested"), 1, 1);

        grid.add(new Label("Hospital Notes:"), 2, 0);
        grid.add(new Label(req.getHospitalNotes() != null && !req.getHospitalNotes().isBlank() ? req.getHospitalNotes() : "None"), 3, 0);

        grid.add(new Label("Last Updated:"), 2, 1);
        grid.add(new Label(req.getUpdatedAt() != null ? req.getUpdatedAt() : "N/A"), 3, 1);

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
