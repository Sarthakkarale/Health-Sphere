package com.healthsphere.view.Patient;

import com.healthsphere.controller.medicaltourism.MedicalTourismController;
import com.healthsphere.controller.medicaltourism.TravelSupportController;
import com.healthsphere.controller.patient.ReviewController;
import com.healthsphere.dao.doctor.DoctorDAO;
import com.healthsphere.dao.hospital.HospitalDAO;
import com.healthsphere.model.DoctorProfile;
import com.healthsphere.model.HospitalProfile;
import com.healthsphere.model.MedicalTourismRequest;
import com.healthsphere.model.TravelSupportRequest;
import com.healthsphere.util.SessionManager;
import com.healthsphere.util.ShimmerPlaceholder;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class MedicalTourism {

    private final Stage stage;
    private final MedicalTourismController tourismController;
    private final TravelSupportController travelController;
    private final HospitalDAO hospitalDAO;
    private final DoctorDAO doctorDAO;
    private final ReviewController reviewController;

    // Search & Form inputs
    private ComboBox<String> treatmentCombo;
    private ComboBox<String> locationCombo;
    private TextField minBudgetField;
    private TextField maxBudgetField;
    private DatePicker datePicker;
    private ComboBox<String> patientTypeCombo;

    // Cost Estimator Inputs
    private TextField treatmentCostField;
    private TextField travelCostField;
    private TextField accommodationCostField;
    private TextField transportCostField;
    private Label totalCostVal;

    // Travel Support Checkboxes
    private CheckBox airportCheckBox;
    private CheckBox accommodationCheckBox;
    private CheckBox localTransportCheckBox;
    private CheckBox translatorCheckBox;
    private TextArea requirementsArea;
    private final List<String> attachedDocuments = new ArrayList<>();
    private Label attachedDocsLabel;

    // Wizard navigation state (1: Requirements, 2: Select Hospital, 3: Select Doctor, 4: Cost & Support, 5: Confirmation)
    private int currentStep = 1;
    private VBox wizardContainer;
    private HBox stepIndicatorBar;
    private TabPane mainTabPane;

    // Selected Data
    private HospitalProfile selectedHospital;
    private DoctorProfile selectedDoctor;

    // Data cache
    private List<HospitalProfile> cachedHospitals = new ArrayList<>();
    private List<DoctorProfile> cachedDoctors = new ArrayList<>();
    private final Map<String, Double> hospitalRatingsMap = new HashMap<>();

    public MedicalTourism(Stage stage) {
        this.stage = stage;
        this.tourismController = new MedicalTourismController();
        this.travelController = new TravelSupportController();
        this.hospitalDAO = new HospitalDAO();
        this.doctorDAO = new DoctorDAO();
        this.reviewController = new ReviewController();

        initCostFields();
    }

    private void initCostFields() {
        treatmentCostField = new TextField("100000");
        travelCostField = new TextField("0");
        accommodationCostField = new TextField("0");
        transportCostField = new TextField("0");
        totalCostVal = new Label("₹0");

        String fieldStyle = "-fx-background-color: white; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 10; -fx-font-size: 13px; -fx-text-fill: #172B4D;";
        treatmentCostField.setStyle(fieldStyle);
        travelCostField.setStyle(fieldStyle);
        accommodationCostField.setStyle(fieldStyle);
        transportCostField.setStyle(fieldStyle);
        totalCostVal.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #059669;");

        Runnable recalculateTotal = () -> {
            try {
                double tc = Double.parseDouble(treatmentCostField.getText().trim());
                double tr = Double.parseDouble(travelCostField.getText().trim());
                double ac = Double.parseDouble(accommodationCostField.getText().trim());
                double tp = Double.parseDouble(transportCostField.getText().trim());
                double tot = tc + tr + ac + tp;
                totalCostVal.setText(String.format("₹%,.0f", tot));
            } catch (Exception ex) {
                totalCostVal.setText("₹ --");
            }
        };

        treatmentCostField.textProperty().addListener((o, oldV, newV) -> recalculateTotal.run());
        travelCostField.textProperty().addListener((o, oldV, newV) -> recalculateTotal.run());
        accommodationCostField.textProperty().addListener((o, oldV, newV) -> recalculateTotal.run());
        transportCostField.textProperty().addListener((o, oldV, newV) -> recalculateTotal.run());
        recalculateTotal.run();
    }

    private Button tab1Btn;
    private Button tab2Btn;
    private StackPane contentPane;
    private VBox tab1Content;
    private VBox tab2Content;

    public Scene getScene() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(10));
        mainContent.setFillWidth(true);

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

        tab1Btn = new Button("✈  Find Treatment & Request");
        tab2Btn = new Button("📋  My Medical Tourism Requests");

        tab1Content = buildFindTreatmentWizard();
        tab2Content = buildMyRequestsHistoryView();

        contentPane = new StackPane();
        contentPane.getChildren().add(tab1Content);

        tab1Btn.setOnAction(e -> switchToTab(0));
        tab2Btn.setOnAction(e -> switchToTab(1));

        tab1Btn.setOnMouseEntered(e -> {
            if (!contentPane.getChildren().contains(tab1Content)) {
                tab1Btn.setStyle("-fx-background-color: #EEF4FF; -fx-text-fill: #2F80ED; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8px; -fx-padding: 10px 22px; -fx-cursor: hand;");
            }
        });
        tab1Btn.setOnMouseExited(e -> updateTabStyles());

        tab2Btn.setOnMouseEntered(e -> {
            if (!contentPane.getChildren().contains(tab2Content)) {
                tab2Btn.setStyle("-fx-background-color: #EEF4FF; -fx-text-fill: #2F80ED; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8px; -fx-padding: 10px 22px; -fx-cursor: hand;");
            }
        });
        tab2Btn.setOnMouseExited(e -> updateTabStyles());

        updateTabStyles();
        tabNavBar.getChildren().addAll(tab1Btn, tab2Btn);

        mainContent.getChildren().addAll(tabNavBar, contentPane);

        return PatientUI.createScene(
                stage,
                "Medical Tourism",
                "Medical Tourism",
                "Discover accredited hospitals and top specialists for world-class treatment, location and budget.",
                mainContent
        );
    }

    private void switchToTab(int index) {
        contentPane.getChildren().clear();
        if (index == 1) {
            contentPane.getChildren().add(tab2Content);
        } else {
            contentPane.getChildren().add(tab1Content);
        }
        updateTabStyles();
    }

    private void updateTabStyles() {
        boolean isTab1 = contentPane.getChildren().contains(tab1Content);
        String activeStyle = "-fx-background-color: #2F80ED; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8px; -fx-padding: 10px 22px; -fx-cursor: hand;";
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: #172B4D; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8px; -fx-padding: 10px 22px; -fx-cursor: hand;";

        if (tab1Btn != null) tab1Btn.setStyle(isTab1 ? activeStyle : inactiveStyle);
        if (tab2Btn != null) tab2Btn.setStyle(!isTab1 ? activeStyle : inactiveStyle);
    }

    // =========================================================================
    // WIZARD NAVIGATION BAR
    // =========================================================================

    private VBox buildFindTreatmentWizard() {
        VBox box = new VBox(18);
        box.setPadding(new Insets(15, 0, 15, 0));
        box.setFillWidth(true);

        stepIndicatorBar = buildStepIndicatorBar();
        wizardContainer = new VBox(15);
        wizardContainer.setFillWidth(true);

        renderCurrentStep();

        box.getChildren().addAll(stepIndicatorBar, wizardContainer);
        return box;
    }

    private HBox buildStepIndicatorBar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(12, 18, 12, 18));
        bar.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.03), 6, 0, 0, 2);");

        String[] steps = {"1. Requirements", "2. Select Hospital", "3. Select Doctor", "4. Cost & Support", "5. Confirmation"};
        for (int i = 1; i <= steps.length; i++) {
            Label stepLbl = new Label(steps[i - 1]);
            if (i == currentStep) {
                stepLbl.setStyle("-fx-background-color: #2F80ED; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 16; -fx-background-radius: 16;");
            } else if (i < currentStep) {
                stepLbl.setStyle("-fx-background-color: #ECFDF5; -fx-text-fill: #059669; -fx-font-weight: bold; -fx-padding: 6 16; -fx-background-radius: 16;");
            } else {
                stepLbl.setStyle("-fx-background-color: transparent; -fx-text-fill: #94A3B8; -fx-padding: 6 16;");
            }
            bar.getChildren().add(stepLbl);
            if (i < steps.length) {
                Label arrow = new Label("→");
                arrow.setStyle("-fx-text-fill: #CBD5E1; -fx-font-weight: bold; -fx-font-size: 14px;");
                bar.getChildren().add(arrow);
            }
        }
        return bar;
    }

    private void renderCurrentStep() {
        wizardContainer.getChildren().clear();
        stepIndicatorBar.getChildren().setAll(buildStepIndicatorBar().getChildren());

        switch (currentStep) {
            case 1: wizardContainer.getChildren().add(buildStep1HeroAndSearch()); break;
            case 2: wizardContainer.getChildren().add(buildStep2HospitalResults()); break;
            case 3: wizardContainer.getChildren().add(buildStep3DoctorSelection()); break;
            case 4: wizardContainer.getChildren().add(buildStep4CostAndTravelSupport()); break;
            case 5: wizardContainer.getChildren().add(buildStep5ConfirmationForm()); break;
            default:
                currentStep = 1;
                wizardContainer.getChildren().add(buildStep1HeroAndSearch());
                break;
        }

        wizardContainer.applyCss();
        wizardContainer.layout();
    }

    // =========================================================================
    // STEP 1 — HERO & SEARCH FORM
    // =========================================================================

    private VBox buildStep1HeroAndSearch() {
        VBox card = new VBox(20);
        card.setFillWidth(true);

        // Hero Banner Card
        VBox heroCard = new VBox(12);
        heroCard.setPadding(new Insets(20, 24, 20, 24));
        heroCard.setStyle(
                "-fx-background-color: linear-gradient(to right, #12355B, #1D4E7A);" +
                "-fx-background-radius: 16;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(18, 53, 91, 0.20), 12, 0, 0, 4);"
        );

        HBox heroRow = new HBox(20);
        heroRow.setAlignment(Pos.CENTER_LEFT);

        VBox heroText = new VBox(8);
        HBox.setHgrow(heroText, Priority.ALWAYS);

        Label headline = new Label("Medical Tourism Planner");
        headline.setStyle("-fx-font-size: 24px; -fx-font-weight: 800; -fx-text-fill: white;");

        Label subline = new Label("Plan your medical journey with the right hospital, doctor and estimated budget.");
        subline.setStyle("-fx-font-size: 14px; -fx-text-fill: #D6E4F0;");
        subline.setWrapText(true);

        Button startJourneyBtn = new Button("Start Medical Journey  ↓");
        startJourneyBtn.setStyle("-fx-background-color: #2F80ED; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 8 18; -fx-background-radius: 8; -fx-cursor: hand;");
        startJourneyBtn.setOnAction(e -> treatmentCombo.requestFocus());

        heroText.getChildren().addAll(headline, subline, startJourneyBtn);

        ImageView heroImg = createImageView("/images/healthcare_ai_hero.png", 180, 100);
        if (heroImg == null) {
            heroImg = createImageView("/images/hospital_care.jpg", 180, 100);
        }

        if (heroImg != null) {
            heroImg.setStyle("-fx-background-radius: 12;");
            heroRow.getChildren().addAll(heroText, heroImg);
        } else {
            heroRow.getChildren().add(heroText);
        }

        heroCard.getChildren().add(heroRow);

        // Search Form Card
        VBox formCard = new VBox(16);
        formCard.setPadding(new Insets(20));
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-color: #E2E8F0; -fx-border-radius: 16; -fx-effect: dropshadow(three-pass-box, rgba(15,23,42,0.04), 8, 0, 0, 2);");

        Label formHeader = new Label("🔍 Step 1: Treatment & Search Criteria");
        formHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #12355B;");

        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(16); grid.setPadding(new Insets(10, 0, 10, 0));

        Label treatmentLbl = new Label("Treatment / Specialty *");
        treatmentLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #172B4D; -fx-font-size: 13px;");
        treatmentCombo = new ComboBox<>();
        treatmentCombo.getItems().addAll(
                "Cardiac Surgery", "Cardiology", "Orthopedics & Joint Replacement",
                "Neurosurgery & Neurology", "Oncology (Cancer Treatment)",
                "Organ Transplant", "Gastroenterology", "Dental Surgery",
                "Cosmetic & Plastic Surgery", "General Surgery"
        );
        treatmentCombo.setValue("Cardiac Surgery");
        treatmentCombo.setMaxWidth(Double.MAX_VALUE);
        treatmentCombo.setPrefHeight(40);
        treatmentCombo.setStyle("-fx-background-color: white; -fx-border-color: #CBD5E1; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label locLbl = new Label("Preferred Location (City) *");
        locLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #172B4D; -fx-font-size: 13px;");
        locationCombo = new ComboBox<>();
        locationCombo.getItems().addAll("Pune", "Mumbai", "Delhi", "Bangalore", "Hyderabad", "All Cities");
        locationCombo.setValue("Pune");
        locationCombo.setMaxWidth(Double.MAX_VALUE);
        locationCombo.setPrefHeight(40);
        locationCombo.setStyle("-fx-background-color: white; -fx-border-color: #CBD5E1; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label minBudgetCot = new Label("Minimum Budget (₹) *");
        minBudgetCot.setStyle("-fx-font-weight: bold; -fx-text-fill: #172B4D; -fx-font-size: 13px;");
        minBudgetField = new TextField("100000");
        minBudgetField.setPrefHeight(40);
        minBudgetField.setStyle("-fx-background-color: white; -fx-border-color: #CBD5E1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 0 10;");

        Label maxBudgetCot = new Label("Maximum Budget (₹) *");
        maxBudgetCot.setStyle("-fx-font-weight: bold; -fx-text-fill: #172B4D; -fx-font-size: 13px;");
        maxBudgetField = new TextField("400000");
        maxBudgetField.setPrefHeight(40);
        maxBudgetField.setStyle("-fx-background-color: white; -fx-border-color: #CBD5E1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 0 10;");

        Label dateLbl = new Label("Preferred Date *");
        dateLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #172B4D; -fx-font-size: 13px;");
        datePicker = new DatePicker(LocalDate.now().plusDays(14));
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setPrefHeight(40);
        datePicker.setStyle("-fx-background-color: white; -fx-border-color: #CBD5E1; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label typeLbl = new Label("Patient Type *");
        typeLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #172B4D; -fx-font-size: 13px;");
        patientTypeCombo = new ComboBox<>();
        patientTypeCombo.getItems().addAll("International", "Outstation", "Local");
        patientTypeCombo.setValue("International");
        patientTypeCombo.setMaxWidth(Double.MAX_VALUE);
        patientTypeCombo.setPrefHeight(40);
        patientTypeCombo.setStyle("-fx-background-color: white; -fx-border-color: #CBD5E1; -fx-border-radius: 8; -fx-background-radius: 8;");

        grid.add(treatmentLbl, 0, 0); grid.add(treatmentCombo, 0, 1);
        grid.add(locLbl, 1, 0); grid.add(locationCombo, 1, 1);
        grid.add(minBudgetCot, 0, 2); grid.add(minBudgetField, 0, 3);
        grid.add(maxBudgetCot, 1, 2); grid.add(maxBudgetField, 1, 3);
        grid.add(dateLbl, 0, 4); grid.add(datePicker, 0, 5);
        grid.add(typeLbl, 1, 4); grid.add(patientTypeCombo, 1, 5);

        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        HBox actionRow = new HBox(15);
        actionRow.setAlignment(Pos.CENTER_RIGHT);
        actionRow.setPadding(new Insets(10, 0, 0, 0));

        Button findHospitalsBtn = new Button("Find Hospitals  →");
        findHospitalsBtn.setStyle("-fx-background-color: #2F80ED; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 10 28; -fx-background-radius: 8; -fx-cursor: hand;");
        findHospitalsBtn.setOnAction(e -> {
            if (validateStep1Inputs()) {
                if (treatmentCostField != null) {
                    treatmentCostField.setText(minBudgetField.getText().trim());
                }
                currentStep = 2;
                renderCurrentStep();
            }
        });

        actionRow.getChildren().add(findHospitalsBtn);
        formCard.getChildren().addAll(formHeader, grid, actionRow);

        card.getChildren().addAll(heroCard, formCard);
        return card;
    }

    private boolean validateStep1Inputs() {
        if (treatmentCombo.getValue() == null || treatmentCombo.getValue().isBlank()) {
            showAlert("Validation Error", "Please select a treatment or specialty.");
            return false;
        }

        try {
            double minB = Double.parseDouble(minBudgetField.getText().trim());
            double maxB = Double.parseDouble(maxBudgetField.getText().trim());
            if (minB < 0 || maxB < 0 || minB > maxB) {
                showAlert("Validation Error", "Please enter a valid numeric budget range.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Please enter valid numeric budget values.");
            return false;
        }

        if (datePicker.getValue() == null || datePicker.getValue().isBefore(LocalDate.now())) {
            showAlert("Validation Error", "Please select a valid future date.");
            return false;
        }

        return true;
    }

    // =========================================================================
    // STEP 2 — HOSPITAL RESULTS ("SELECT HOSPITAL" — NO COMPARE CHECKBOX)
    // =========================================================================

    private VBox buildStep2HospitalResults() {
        VBox card = new VBox(16);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-color: #E2E8F0; -fx-border-radius: 16; -fx-effect: dropshadow(three-pass-box, rgba(15,23,42,0.04), 8, 0, 0, 2);");
        card.setFillWidth(true);

        Label headerLbl = new Label("🏥 Step 2: Select Hospital for " + treatmentCombo.getValue());
        headerLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #12355B;");

        VBox hospitalsContainer = new VBox(16);
        hospitalsContainer.setFillWidth(true);

        VBox shimmerBox = ShimmerPlaceholder.createListShimmer(3);
        hospitalsContainer.getChildren().add(shimmerBox);

        Task<List<HospitalProfile>> task = new Task<>() {
            @Override
            protected List<HospitalProfile> call() throws Exception {
                List<HospitalProfile> hospitals = hospitalDAO.getAllHospitals();
                List<DoctorProfile> doctors = doctorDAO.getAllDoctors();
                cachedDoctors = doctors;

                hospitalRatingsMap.clear();
                for (HospitalProfile h : hospitals) {
                    if (h != null && h.getUid() != null) {
                        double rating = reviewController.getAverageRating("HOSPITAL", h.getUid());
                        hospitalRatingsMap.put(h.getUid(), rating);
                    }
                }

                List<HospitalProfile> filtered = hospitals.stream()
                        .filter(h -> h != null && h.getHospitalName() != null && !h.getHospitalName().isBlank())
                        .collect(Collectors.toList());

                cachedHospitals = filtered;
                return filtered;
            }
        };

        task.setOnSucceeded(e -> {
            hospitalsContainer.getChildren().clear();
            List<HospitalProfile> hospitals = task.getValue();

            if (hospitals == null || hospitals.isEmpty()) {
                VBox emptyBox = new VBox(12);
                emptyBox.setAlignment(Pos.CENTER);
                emptyBox.setPadding(new Insets(35));
                emptyBox.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-radius: 12;");

                Label emptyIcon = new Label("🏥");
                emptyIcon.setStyle("-fx-font-size: 32px; -fx-text-fill: #2F80ED;");

                Label emptyTitle = new Label("No Suitable Hospitals Found");
                emptyTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #12355B;");

                Label emptyDesc = new Label("Try changing your treatment, location or budget range.");
                emptyDesc.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

                Button modifyBtn = new Button("Modify Search");
                modifyBtn.setStyle("-fx-background-color: #2F80ED; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 8; -fx-cursor: hand;");
                modifyBtn.setOnAction(evt -> { currentStep = 1; renderCurrentStep(); });

                emptyBox.getChildren().addAll(emptyIcon, emptyTitle, emptyDesc, modifyBtn);
                hospitalsContainer.getChildren().add(emptyBox);
                return;
            }

            for (HospitalProfile h : hospitals) {
                hospitalsContainer.getChildren().add(createHospitalResultCard(h));
            }
        });

        task.setOnFailed(e -> {
            hospitalsContainer.getChildren().clear();
            Label errLbl = new Label("Unable to load real hospital data from Firebase.");
            errLbl.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
            hospitalsContainer.getChildren().add(errLbl);
        });

        new Thread(task).start();

        HBox navRow = new HBox(15);
        navRow.setAlignment(Pos.CENTER_LEFT);
        navRow.setPadding(new Insets(15, 0, 0, 0));

        Button backBtn = new Button("← Back to Requirements");
        backBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #172B4D; -fx-font-weight: bold; -fx-padding: 9 18; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(evt -> { currentStep = 1; renderCurrentStep(); });

        navRow.getChildren().add(backBtn);
        card.getChildren().addAll(headerLbl, hospitalsContainer, navRow);
        return card;
    }

    private VBox createHospitalResultCard(HospitalProfile hospital) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18));
        
        boolean isSelected = (selectedHospital != null && selectedHospital.getUid() != null && selectedHospital.getUid().equals(hospital.getUid()));
        String borderCol = isSelected ? "#2F80ED" : "#CBD5E1";
        String bgCol = isSelected ? "#F0F7FF" : "white";

        card.setStyle("-fx-background-color: " + bgCol + "; -fx-background-radius: 12; -fx-border-color: " + borderCol + "; -fx-border-width: " + (isSelected ? "2" : "1") + "; -fx-border-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(15,23,42,0.03), 6, 0, 0, 2);");

        HBox topRow = new HBox(16);
        topRow.setAlignment(Pos.CENTER_LEFT);

        ImageView hospImg = createImageView("/images/hospital_care.jpg", 130, 90);
        if (hospImg == null) {
            hospImg = createImageView("/images/hospital_dashboard.jpg", 130, 90);
        }

        VBox titleBox = new VBox(6);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        Label nameLbl = new Label(hospital.getHospitalName());
        nameLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #12355B;");
        nameLbl.setWrapText(true);

        Label locLbl = new Label("📍 " + (hospital.getAddress() != null && !hospital.getAddress().isBlank() ? hospital.getAddress() : "Pune, Maharashtra"));
        locLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");
        locLbl.setWrapText(true);

        double rating = hospitalRatingsMap.getOrDefault(hospital.getUid(), 0.0);
        HBox ratingBox = buildVisualStarRating(rating);

        titleBox.getChildren().addAll(nameLbl, locLbl, ratingBox);

        if (hospImg != null) {
            topRow.getChildren().addAll(hospImg, titleBox);
        } else {
            topRow.getChildren().add(titleBox);
        }

        HBox detailsRow = new HBox(16);
        detailsRow.setAlignment(Pos.CENTER_LEFT);
        detailsRow.setPadding(new Insets(8, 12, 8, 12));
        detailsRow.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 8; -fx-border-color: #E2E8F0; -fx-border-radius: 8;");

        String beds = hospital.getBeds() != null && !hospital.getBeds().isBlank() ? hospital.getBeds() : "Available";
        Label bedsLbl = new Label("🛏 Beds: " + beds);
        bedsLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #2F80ED; -fx-font-size: 13px;");

        Label costLbl = new Label("💰 Est. Treatment Cost: ₹" + minBudgetField.getText() + " - ₹" + maxBudgetField.getText());
        costLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #16A34A; -fx-font-size: 13px;");

        long docCount = cachedDoctors.stream()
                .filter(d -> d != null && d.getHospitalAffiliation() != null && d.getHospitalAffiliation().toLowerCase().contains(hospital.getHospitalName().toLowerCase()))
                .count();
        Label docsCountLbl = new Label("👨‍⚕️ Doctors: " + (docCount > 0 ? docCount + " Specialists" : "Medical Team"));
        docsCountLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569; -fx-font-size: 13px;");

        detailsRow.getChildren().addAll(bedsLbl, costLbl, docsCountLbl);

        HBox btnRow = new HBox(12);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        Button detailsBtn = new Button("View Details");
        detailsBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #172B4D; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        detailsBtn.setOnAction(e -> showHospitalDetailsModal(hospital));

        Button selectHospitalBtn = new Button(isSelected ? "Selected ✓" : "Select Hospital  →");
        selectHospitalBtn.setStyle(isSelected
                ? "-fx-background-color: #059669; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 8; -fx-cursor: hand;"
                : "-fx-background-color: #2F80ED; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 8; -fx-cursor: hand;");
        
        selectHospitalBtn.setOnAction(e -> {
            selectedHospital = hospital;
            currentStep = 3;
            renderCurrentStep();
        });

        btnRow.getChildren().addAll(detailsBtn, selectHospitalBtn);

        card.getChildren().addAll(topRow, detailsRow, btnRow);
        return card;
    }

    private void showHospitalDetailsModal(HospitalProfile hospital) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle(hospital.getHospitalName() + " — Overview");

        VBox content = new VBox(16);
        content.setPadding(new Insets(24));
        content.setStyle("-fx-background-color: white;");

        Label title = new Label(hospital.getHospitalName());
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #12355B;");
        title.setWrapText(true);

        Label address = new Label("📍 Address: " + (hospital.getAddress() != null ? hospital.getAddress() : "Pune"));
        address.setWrapText(true);
        Label phone = new Label("📞 Contact Phone: " + (hospital.getContact() != null ? hospital.getContact() : "+91-20-41000000"));
        Label email = new Label("✉ Contact Email: " + (hospital.getEmail() != null ? hospital.getEmail() : "info@hospital.com"));
        Label beds = new Label("🛏 Beds Capacity: " + (hospital.getBeds() != null ? hospital.getBeds() : "Available"));

        double rating = hospitalRatingsMap.getOrDefault(hospital.getUid(), 0.0);
        HBox rBox = buildVisualStarRating(rating);

        VBox infoBox = new VBox(8, address, phone, email, beds, rBox);
        infoBox.setPadding(new Insets(14));
        infoBox.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 10; -fx-border-color: #E2E8F0; -fx-border-radius: 10;");

        Button selectBtn = new Button("Select This Hospital");
        selectBtn.setStyle("-fx-background-color: #2F80ED; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 8; -fx-cursor: hand;");
        selectBtn.setOnAction(e -> {
            selectedHospital = hospital;
            modal.close();
            currentStep = 3;
            renderCurrentStep();
        });

        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #172B4D; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> modal.close());

        HBox btnBox = new HBox(12, closeBtn, selectBtn);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        content.getChildren().addAll(title, infoBox, btnBox);

        Scene scene = new Scene(content, 480, 350);
        modal.setScene(scene);
        modal.showAndWait();
    }

    // =========================================================================
    // STEP 3 — DOCTOR SELECTION ("CHOOSE YOUR DOCTOR")
    // =========================================================================

    private VBox buildStep3DoctorSelection() {
        VBox card = new VBox(16);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-color: #E2E8F0; -fx-border-radius: 16; -fx-effect: dropshadow(three-pass-box, rgba(15,23,42,0.04), 8, 0, 0, 2);");
        card.setFillWidth(true);

        Label headerLbl = new Label("👨‍⚕️ Step 3: Choose Specialist Doctor at " + (selectedHospital != null ? selectedHospital.getHospitalName() : "Selected Hospital"));
        headerLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #12355B;");
        headerLbl.setWrapText(true);

        VBox doctorsContainer = new VBox(14);
        doctorsContainer.setFillWidth(true);

        List<DoctorProfile> hospitalDocs = cachedDoctors.stream()
                .filter(d -> d != null && d.getHospitalAffiliation() != null && selectedHospital != null && d.getHospitalAffiliation().toLowerCase().contains(selectedHospital.getHospitalName().toLowerCase()))
                .collect(Collectors.toList());

        if (hospitalDocs.isEmpty()) {
            hospitalDocs = cachedDoctors;
        }

        if (hospitalDocs.isEmpty()) {
            VBox emptyBox = new VBox(15);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(30));
            emptyBox.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-radius: 12;");

            Label msg = new Label("No specific doctors listed for this hospital. You may proceed with the Hospital Medical Team consultation.");
            msg.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748B;");
            msg.setWrapText(true);

            Button proceedBtn = new Button("Proceed with Hospital Team Consultation →");
            proceedBtn.setStyle("-fx-background-color: #2F80ED; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 9 18; -fx-background-radius: 8; -fx-cursor: hand;");
            proceedBtn.setOnAction(e -> {
                selectedDoctor = null;
                currentStep = 4;
                renderCurrentStep();
            });

            emptyBox.getChildren().addAll(msg, proceedBtn);
            doctorsContainer.getChildren().add(emptyBox);
        } else {
            for (DoctorProfile d : hospitalDocs) {
                doctorsContainer.getChildren().add(createDoctorCard(d));
            }
        }

        HBox navRow = new HBox(15);
        navRow.setAlignment(Pos.CENTER_LEFT);
        navRow.setPadding(new Insets(15, 0, 0, 0));

        Button backBtn = new Button("← Back to Hospitals");
        backBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #172B4D; -fx-font-weight: bold; -fx-padding: 9 18; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> { currentStep = 2; renderCurrentStep(); });

        Button skipDocBtn = new Button("Skip Doctor (Assign Best Medical Team) →");
        skipDocBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #12355B; -fx-font-weight: bold; -fx-padding: 9 18; -fx-background-radius: 8; -fx-cursor: hand;");
        skipDocBtn.setOnAction(e -> {
            selectedDoctor = null;
            currentStep = 4;
            renderCurrentStep();
        });

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        navRow.getChildren().addAll(backBtn, spacer, skipDocBtn);
        card.getChildren().addAll(headerLbl, doctorsContainer, navRow);
        return card;
    }

    private VBox createDoctorCard(DoctorProfile doc) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));

        boolean isSelected = (selectedDoctor != null && selectedDoctor.getUid() != null && selectedDoctor.getUid().equals(doc.getUid()));
        String borderCol = isSelected ? "#2F80ED" : "#CBD5E1";
        String bgCol = isSelected ? "#F0F7FF" : "white";

        card.setStyle("-fx-background-color: " + bgCol + "; -fx-background-radius: 12; -fx-border-color: " + borderCol + "; -fx-border-width: " + (isSelected ? "2" : "1") + "; -fx-border-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(15,23,42,0.03), 6, 0, 0, 2);");

        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);

        ImageView docAvatar = createImageView("/images/doctor.png", 60, 60);

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        String docName = (doc.getFirstName() != null ? doc.getFirstName() : "") + " " + (doc.getLastName() != null ? doc.getLastName() : "");
        if (docName.isBlank()) docName = "Dr. Medical Practitioner";

        Label nameLbl = new Label(docName);
        nameLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #12355B;");
        nameLbl.setWrapText(true);

        Label specLbl = new Label("Specialization: " + (doc.getSpecialization() != null ? doc.getSpecialization() : "General Specialist"));
        specLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #475569;");
        specLbl.setWrapText(true);

        double docRating = reviewController.getAverageRating("DOCTOR", doc.getUid());
        HBox ratingBox = buildVisualStarRating(docRating);

        Label expLbl = new Label("Experience: " + (doc.getExperience() != null ? doc.getExperience() : "10+ Years"));
        expLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");

        Label availBadge = new Label("✓ Available for Consultation");
        availBadge.setStyle("-fx-background-color: #ECFDF5; -fx-text-fill: #059669; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 12;");

        info.getChildren().addAll(nameLbl, specLbl, expLbl, ratingBox, availBadge);

        Button selectBtn = new Button(isSelected ? "Doctor Selected ✓" : "Select Doctor  →");
        selectBtn.setStyle(isSelected
                ? "-fx-background-color: #059669; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;"
                : "-fx-background-color: #2F80ED; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        
        selectBtn.setOnAction(e -> {
            selectedDoctor = doc;
            currentStep = 4;
            renderCurrentStep();
        });

        if (docAvatar != null) {
            row.getChildren().addAll(docAvatar, info, selectBtn);
        } else {
            row.getChildren().addAll(info, selectBtn);
        }

        card.getChildren().add(row);
        return card;
    }

    // =========================================================================
    // STEP 4 — COST ESTIMATOR & TRAVEL SUPPORT
    // =========================================================================

    private VBox buildStep4CostAndTravelSupport() {
        VBox card = new VBox(20);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-color: #E2E8F0; -fx-border-radius: 16; -fx-effect: dropshadow(three-pass-box, rgba(15,23,42,0.04), 8, 0, 0, 2);");
        card.setFillWidth(true);

        Label headerLbl = new Label("💰 Step 4: Estimated Medical Travel Cost & Travel Support");
        headerLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #12355B;");

        // COST ESTIMATOR CARD
        VBox costCard = new VBox(12);
        costCard.setPadding(new Insets(16));
        costCard.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 12; -fx-border-color: #CBD5E1; -fx-border-radius: 12;");

        Label costTitle = new Label("💰  Estimated Medical Travel Cost");
        costTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #12355B;");

        Label costDisclaimer = new Label("These are estimated costs for planning purposes only.");
        costDisclaimer.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

        GridPane costGrid = new GridPane();
        costGrid.setHgap(16); costGrid.setVgap(12); costGrid.setPadding(new Insets(10, 0, 10, 0));

        Label tcLbl = new Label("Treatment Cost (₹):"); tcLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #172B4D;");
        Label trLbl = new Label("Travel Estimate (₹):"); trLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #172B4D;");
        Label acLbl = new Label("Accommodation (₹):"); acLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #172B4D;");
        Label tpLbl = new Label("Local Transport (₹):"); tpLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #172B4D;");
        Label totLbl = new Label("Estimated Total Cost:"); totLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #12355B;");

        costGrid.add(tcLbl, 0, 0); costGrid.add(treatmentCostField, 1, 0);
        costGrid.add(trLbl, 0, 1); costGrid.add(travelCostField, 1, 1);
        costGrid.add(acLbl, 2, 0); costGrid.add(accommodationCostField, 3, 0);
        costGrid.add(tpLbl, 2, 1); costGrid.add(transportCostField, 3, 1);
        costGrid.add(totLbl, 0, 2); costGrid.add(totalCostVal, 1, 2);

        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(25);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPercentWidth(25);
        ColumnConstraints c3 = new ColumnConstraints(); c3.setPercentWidth(25);
        ColumnConstraints c4 = new ColumnConstraints(); c4.setPercentWidth(25);
        costGrid.getColumnConstraints().addAll(c1, c2, c3, c4);

        Label quoteDisclaimer = new Label("* Estimated cost only — not an official hospital quotation.");
        quoteDisclaimer.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B; -fx-font-style: italic;");

        costCard.getChildren().addAll(costTitle, costDisclaimer, costGrid, quoteDisclaimer);

        // MEDICAL TRAVEL PLAN TIMELINE
        VBox planCard = new VBox(12);
        planCard.setPadding(new Insets(16));
        planCard.setStyle("-fx-background-color: #EFF6FF; -fx-background-radius: 12; -fx-border-color: #BFDBFE; -fx-border-radius: 12;");

        Label planTitle = new Label("🗓  Medical Travel Plan Timeline");
        planTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1E40AF;");

        HBox timelineBox = new HBox(10);
        timelineBox.setAlignment(Pos.CENTER_LEFT);
        timelineBox.setPadding(new Insets(8, 0, 8, 0));

        String[] journeySteps = {
            "1. Arrival\n(Destination)",
            "2. Registration\n(Hospital)",
            "3. Consultation\n(Specialist)",
            "4. Treatment\n(Procedure)",
            "5. Recovery\n(Care)",
            "6. Follow-up\n(Post Care)"
        };

        for (int i = 0; i < journeySteps.length; i++) {
            VBox stepBox = new VBox(4);
            stepBox.setAlignment(Pos.CENTER);
            stepBox.setPadding(new Insets(8));
            stepBox.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #DBEAFE; -fx-border-radius: 8;");
            Label stepLbl = new Label(journeySteps[i]);
            stepLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-alignment: center; -fx-text-fill: #1E293B;");
            stepBox.getChildren().add(stepLbl);
            timelineBox.getChildren().add(stepBox);

            if (i < journeySteps.length - 1) {
                Label arr = new Label("➔");
                arr.setStyle("-fx-text-fill: #3B82F6; -fx-font-weight: bold;");
                timelineBox.getChildren().add(arr);
            }
        }

        planCard.getChildren().addAll(planTitle, timelineBox);

        // TRAVEL SUPPORT REQUIREMENTS
        VBox travelSection = new VBox(12);
        Label travelHead = new Label("🧳  Travel Support Requirements");
        travelHead.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #12355B;");

        GridPane travelGrid = new GridPane();
        travelGrid.setHgap(14); travelGrid.setVgap(14);

        if (airportCheckBox == null) airportCheckBox = new CheckBox("Airport Pickup Required");
        if (accommodationCheckBox == null) accommodationCheckBox = new CheckBox("Accommodation Required");
        if (localTransportCheckBox == null) localTransportCheckBox = new CheckBox("Local Transportation Required");
        if (translatorCheckBox == null) translatorCheckBox = new CheckBox("Language Assistance Required");

        airportCheckBox.setStyle("-fx-text-fill: #172B4D; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");
        accommodationCheckBox.setStyle("-fx-text-fill: #172B4D; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");
        localTransportCheckBox.setStyle("-fx-text-fill: #172B4D; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");
        translatorCheckBox.setStyle("-fx-text-fill: #172B4D; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");

        VBox airportCard = createTravelServiceCard("✈", "Airport Pickup", "Complimentary driver & airport transfer upon arrival.", airportCheckBox);
        VBox accommodationCard = createTravelServiceCard("🏨", "Accommodation", "Pre-booked partner hotel / hospital suite stay.", accommodationCheckBox);
        VBox transportCard = createTravelServiceCard("🚕", "Local Transport", "Dedicated city shuttle & hospital commute.", localTransportCheckBox);
        VBox translatorCard = createTravelServiceCard("🌐", "Language Assistance", "Certified medical interpreter & translator.", translatorCheckBox);

        travelGrid.add(airportCard, 0, 0);
        travelGrid.add(accommodationCard, 1, 0);
        travelGrid.add(transportCard, 0, 1);
        travelGrid.add(translatorCard, 1, 1);

        ColumnConstraints tCol1 = new ColumnConstraints(); tCol1.setPercentWidth(50);
        ColumnConstraints tCol2 = new ColumnConstraints(); tCol2.setPercentWidth(50);
        travelGrid.getColumnConstraints().addAll(tCol1, tCol2);

        travelSection.getChildren().addAll(travelHead, travelGrid);

        // MEDICAL DOCUMENTS & NOTES
        VBox docBox = new VBox(10);
        docBox.setPadding(new Insets(14));
        docBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #CBD5E1; -fx-border-radius: 10;");

        Label docHead = new Label("📎  Medical Documents & Reports");
        docHead.setStyle("-fx-font-weight: bold; -fx-text-fill: #2F80ED; -fx-font-size: 14px;");

        attachedDocsLabel = new Label(attachedDocuments.isEmpty() ? "No medical documents added" : "Attached Documents: " + String.join(", ", attachedDocuments));
        attachedDocsLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 13px;");

        Button addDocBtn = new Button("+ Attach Medical Document");
        addDocBtn.setStyle("-fx-background-color: #EFF6FF; -fx-text-fill: #2563EB; -fx-font-weight: bold; -fx-border-color: #93C5FD; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
        addDocBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog("Medical_Report_2026.pdf");
            dialog.setTitle("Attach Medical Document");
            dialog.setHeaderText("Enter Medical Document / Report Name");
            dialog.setContentText("Document name:");
            dialog.showAndWait().ifPresent(docName -> {
                if (!docName.isBlank()) {
                    attachedDocuments.add(docName.trim());
                    attachedDocsLabel.setText("Attached Documents: " + String.join(", ", attachedDocuments));
                }
            });
        });

        docBox.getChildren().addAll(docHead, attachedDocsLabel, addDocBtn);

        if (requirementsArea == null) {
            requirementsArea = new TextArea();
            requirementsArea.setPromptText("Enter any additional medical notes, dietary restrictions, or travel preferences...");
            requirementsArea.setPrefRowCount(2);
            requirementsArea.setStyle("-fx-background-color: white; -fx-border-color: #CBD5E1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-text-fill: #172B4D;");
        }

        HBox actionRow = new HBox(15);
        actionRow.setAlignment(Pos.CENTER_RIGHT);
        actionRow.setPadding(new Insets(15, 0, 0, 0));

        Button backBtn = new Button("← Back to Doctors");
        backBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #172B4D; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> { currentStep = 3; renderCurrentStep(); });

        Button proceedBtn = new Button("Proceed to Confirmation  →");
        proceedBtn.setStyle("-fx-background-color: #2F80ED; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 10 26; -fx-background-radius: 8; -fx-cursor: hand;");
        proceedBtn.setOnAction(e -> { currentStep = 5; renderCurrentStep(); });

        actionRow.getChildren().addAll(backBtn, proceedBtn);
        card.getChildren().addAll(headerLbl, costCard, planCard, travelSection, docBox, requirementsArea, actionRow);
        return card;
    }

    private VBox createTravelServiceCard(String iconStr, String titleStr, String descStr, CheckBox control) {
        VBox box = new VBox(8);
        box.setPadding(new Insets(14));
        box.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 10; -fx-border-color: #E2E8F0; -fx-border-radius: 10;");

        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label(iconStr);
        icon.setStyle("-fx-font-size: 20px;");

        Label title = new Label(titleStr);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #12355B;");

        top.getChildren().addAll(icon, title);

        Label desc = new Label(descStr);
        desc.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        desc.setWrapText(true);

        box.getChildren().addAll(top, desc, control);
        return box;
    }

    // =========================================================================
    // STEP 5 — CONFIRMATION & SUBMIT REQUEST
    // =========================================================================

    private VBox buildStep5ConfirmationForm() {
        VBox card = new VBox(20);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-color: #E2E8F0; -fx-border-radius: 16; -fx-effect: dropshadow(three-pass-box, rgba(15,23,42,0.04), 8, 0, 0, 2);");
        card.setFillWidth(true);

        Label headerLbl = new Label("✅ Step 5: Confirm Medical Tourism Request");
        headerLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #12355B;");

        // Summary Card
        GridPane summary = new GridPane();
        summary.setHgap(16); summary.setVgap(12); summary.setPadding(new Insets(16));
        summary.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-radius: 12;");

        summary.add(createSummaryLabel("Selected Treatment:"), 0, 0);
        summary.add(createSummaryValue(treatmentCombo != null ? treatmentCombo.getValue() : "Cardiac Surgery"), 1, 0);

        summary.add(createSummaryLabel("Selected Hospital:"), 0, 1);
        summary.add(createSummaryValue(selectedHospital != null ? selectedHospital.getHospitalName() : "Not Selected"), 1, 1);

        summary.add(createSummaryLabel("Selected Doctor:"), 0, 2);
        String dName = selectedDoctor != null ? ((selectedDoctor.getFirstName() != null ? selectedDoctor.getFirstName() : "") + " " + (selectedDoctor.getLastName() != null ? selectedDoctor.getLastName() : "")) : "Hospital Medical Team";
        summary.add(createSummaryValue(dName), 1, 2);

        summary.add(createSummaryLabel("Preferred Date:"), 0, 3);
        summary.add(createSummaryValue(datePicker != null && datePicker.getValue() != null ? datePicker.getValue().toString() : "Not specified"), 1, 3);

        summary.add(createSummaryLabel("Budget Range:"), 0, 4);
        summary.add(createSummaryValue("₹" + (minBudgetField != null ? minBudgetField.getText() : "100000") + " - ₹" + (maxBudgetField != null ? maxBudgetField.getText() : "400000")), 1, 4);

        summary.add(createSummaryLabel("Patient Type:"), 0, 5);
        summary.add(createSummaryValue(patientTypeCombo != null ? patientTypeCombo.getValue() : "International"), 1, 5);

        summary.add(createSummaryLabel("Estimated Total Cost:"), 0, 6);
        summary.add(createSummaryValue(totalCostVal != null ? totalCostVal.getText() : "₹0"), 1, 6);

        // Travel requirements summary
        List<String> reqsList = new ArrayList<>();
        if (airportCheckBox != null && airportCheckBox.isSelected()) reqsList.add("Airport Pickup");
        if (accommodationCheckBox != null && accommodationCheckBox.isSelected()) reqsList.add("Accommodation");
        if (localTransportCheckBox != null && localTransportCheckBox.isSelected()) reqsList.add("Local Transport");
        if (translatorCheckBox != null && translatorCheckBox.isSelected()) reqsList.add("Language Assistance");
        String travelReqStr = reqsList.isEmpty() ? "None specified" : String.join(", ", reqsList);

        summary.add(createSummaryLabel("Travel Support:"), 0, 7);
        summary.add(createSummaryValue(travelReqStr), 1, 7);

        summary.add(createSummaryLabel("Medical Documents:"), 0, 8);
        summary.add(createSummaryValue(attachedDocuments.isEmpty() ? "None attached" : String.join(", ", attachedDocuments)), 1, 8);

        ColumnConstraints sCol1 = new ColumnConstraints(); sCol1.setPercentWidth(35);
        ColumnConstraints sCol2 = new ColumnConstraints(); sCol2.setPercentWidth(65);
        summary.getColumnConstraints().addAll(sCol1, sCol2);

        HBox actionRow = new HBox(15);
        actionRow.setAlignment(Pos.CENTER_RIGHT);
        actionRow.setPadding(new Insets(15, 0, 0, 0));

        Button backBtn = new Button("← Back to Cost & Support");
        backBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #172B4D; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> { currentStep = 4; renderCurrentStep(); });

        Button submitBtn = new Button("🚀  Submit Request");
        submitBtn.setStyle("-fx-background-color: #2F80ED; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(47,128,237,0.30), 8, 0, 0, 3);");
        submitBtn.setOnAction(e -> submitFinalRequest());

        actionRow.getChildren().addAll(backBtn, submitBtn);
        card.getChildren().addAll(headerLbl, summary, actionRow);
        return card;
    }

    private Label createSummaryLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #64748B; -fx-font-size: 13px;");
        return lbl;
    }

    private Label createSummaryValue(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #12355B; -fx-font-size: 14px;");
        lbl.setWrapText(true);
        return lbl;
    }

    private void submitFinalRequest() {
        if (treatmentCombo.getValue() == null || treatmentCombo.getValue().isBlank()) {
            showAlert("Validation Error", "Please select a treatment or specialty.");
            return;
        }

        if (selectedHospital == null) {
            showAlert("Validation Error", "Please select a hospital before continuing.");
            currentStep = 2;
            renderCurrentStep();
            return;
        }

        if (datePicker.getValue() == null) {
            showAlert("Validation Error", "Please select a valid preferred date.");
            return;
        }

        // Check for duplicate active request before submitting
        try {
            String pUid = SessionManager.getPatientUid();
            List<MedicalTourismRequest> existing = tourismController.getPatientRequests(pUid);
            if (existing != null) {
                boolean duplicate = existing.stream().anyMatch(r ->
                    r != null &&
                    selectedHospital.getUid() != null && selectedHospital.getUid().equals(r.getHospitalId()) &&
                    treatmentCombo.getValue().equalsIgnoreCase(r.getTreatmentName()) &&
                    ("PENDING".equalsIgnoreCase(r.getStatus()) ||
                     "UNDER_REVIEW".equalsIgnoreCase(r.getStatus()) ||
                     "ACCEPTED".equalsIgnoreCase(r.getStatus()) ||
                     "APPOINTMENT_SCHEDULED".equalsIgnoreCase(r.getStatus()))
                );

                if (duplicate) {
                    showAlert("Duplicate Active Request",
                        "You already have an active Medical Tourism request for " +
                        treatmentCombo.getValue() + " at " + selectedHospital.getHospitalName() +
                        ".\n\nPlease check 'My Medical Tourism Requests' tab to track progress.");
                    return;
                }
            }
        } catch (Exception ex) {
            System.err.println("Could not check duplicate requests: " + ex.getMessage());
        }

        try {
            MedicalTourismRequest req = new MedicalTourismRequest();
            req.setRequestId(UUID.randomUUID().toString());
            req.setPatientId(SessionManager.getPatientUid());
            req.setPatientName(SessionManager.getCurrentUser() != null ? SessionManager.getCurrentUser().getEmail().split("@")[0] : "Patient");
            req.setPatientEmail(SessionManager.getCurrentUser() != null ? SessionManager.getCurrentUser().getEmail() : "");

            req.setHospitalId(selectedHospital.getUid());
            req.setHospitalName(selectedHospital.getHospitalName());

            if (selectedDoctor != null) {
                req.setDoctorId(selectedDoctor.getUid());
                String docName = (selectedDoctor.getFirstName() != null ? selectedDoctor.getFirstName() : "") + " " + (selectedDoctor.getLastName() != null ? selectedDoctor.getLastName() : "");
                req.setDoctorName(docName);
            } else {
                req.setDoctorName("Hospital Medical Team");
            }

            req.setTreatmentName(treatmentCombo.getValue());
            req.setPreferredLocation(locationCombo.getValue());
            req.setMinimumBudget(Double.parseDouble(minBudgetField.getText().trim()));
            req.setMaximumBudget(Double.parseDouble(maxBudgetField.getText().trim()));
            req.setPreferredDate(datePicker.getValue().toString());
            req.setPatientType(patientTypeCombo.getValue());
            req.setAdditionalRequirements(requirementsArea != null ? requirementsArea.getText() : "");

            req.setAirportAssistanceRequired(airportCheckBox != null && airportCheckBox.isSelected());
            req.setAccommodationRequired(accommodationCheckBox != null && accommodationCheckBox.isSelected());
            req.setLocalTransportRequired(localTransportCheckBox != null && localTransportCheckBox.isSelected());
            req.setLanguageAssistanceRequired(translatorCheckBox != null && translatorCheckBox.isSelected());
            req.setMedicalDocuments(new ArrayList<>(attachedDocuments));

            // Populate estimated cost fields
            try {
                double tc = Double.parseDouble(treatmentCostField.getText().trim());
                double tr = Double.parseDouble(travelCostField.getText().trim());
                double ac = Double.parseDouble(accommodationCostField.getText().trim());
                double tp = Double.parseDouble(transportCostField.getText().trim());
                req.setEstimatedTreatmentCost(tc);
                req.setEstimatedTravelCost(tr);
                req.setEstimatedAccommodationCost(ac);
                req.setEstimatedTransportCost(tp);
                req.setEstimatedTotalCost(tc + tr + ac + tp);
            } catch (Exception ignored) {}

            req.setRequestDate(LocalDate.now().toString());
            req.setCreatedAt(LocalDateTime.now().toString());
            req.setStatus("PENDING");

            tourismController.submitRequest(req);

            // Submit travel support request if required
            if (airportCheckBox.isSelected() || accommodationCheckBox.isSelected() || localTransportCheckBox.isSelected() || translatorCheckBox.isSelected()) {
                TravelSupportRequest tr = new TravelSupportRequest();
                tr.setPatientId(SessionManager.getPatientUid());
                tr.setMedicalTourismRequestId(req.getRequestId());
                tr.setHospitalId(req.getHospitalId());
                tr.setHospitalName(req.getHospitalName());
                tr.setServiceType(airportCheckBox.isSelected() ? "AIRPORT_PICKUP" : (accommodationCheckBox.isSelected() ? "ACCOMMODATION" : "LOCAL_TRANSPORTATION"));
                tr.setTravelDate(req.getPreferredDate());
                travelController.submitRequest(tr);
            }

            showAlert("Medical Tourism Request Submitted",
                "Your Medical Tourism request has been sent to " + selectedHospital.getHospitalName() + " for review.\n\nInitial Status: PENDING");

            currentStep = 1;
            renderCurrentStep();

            switchToTab(1); // Switch to My Requests tab
        } catch (Exception ex) {
            showAlert("Error", "Could not submit Medical Tourism Request: " + ex.getMessage());
        }
    }

    // =========================================================================
    // TAB 2 — MY MEDICAL TOURISM REQUESTS HISTORY & STATUS TIMELINE
    // =========================================================================

    private VBox buildMyRequestsHistoryView() {
        VBox card = new VBox(16);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-color: #E2E8F0; -fx-border-radius: 16; -fx-effect: dropshadow(three-pass-box, rgba(15,23,42,0.04), 8, 0, 0, 2);");
        card.setFillWidth(true);

        Label headerLbl = new Label("📋  My Medical Tourism Requests");
        headerLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #12355B;");

        VBox historyListContainer = new VBox(16);
        historyListContainer.setFillWidth(true);

        VBox shimmerBox = ShimmerPlaceholder.createListShimmer(2);
        historyListContainer.getChildren().add(shimmerBox);

        Task<List<MedicalTourismRequest>> task = new Task<>() {
            @Override
            protected List<MedicalTourismRequest> call() throws Exception {
                return tourismController.getPatientRequests(SessionManager.getPatientUid());
            }
        };

        task.setOnSucceeded(e -> {
            historyListContainer.getChildren().clear();
            List<MedicalTourismRequest> requests = task.getValue();

            if (requests == null || requests.isEmpty()) {
                VBox emptyBox = new VBox(12);
                emptyBox.setAlignment(Pos.CENTER);
                emptyBox.setPadding(new Insets(35));
                emptyBox.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-radius: 12;");

                Label icon = new Label("✈");
                icon.setStyle("-fx-font-size: 32px; -fx-text-fill: #2F80ED;");
                Label title = new Label("No Medical Tourism Requests");
                title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #12355B;");
                Label desc = new Label("Your treatment requests will appear here.");
                desc.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

                Button startBtn = new Button("Start Medical Tourism");
                startBtn.setStyle("-fx-background-color: #2F80ED; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 8; -fx-cursor: hand;");
                startBtn.setOnAction(evt -> {
                    switchToTab(0);
                });

                emptyBox.getChildren().addAll(icon, title, desc, startBtn);
                historyListContainer.getChildren().add(emptyBox);
                return;
            }

            for (MedicalTourismRequest r : requests) {
                historyListContainer.getChildren().add(createRequestHistoryCard(r));
            }
        });

        task.setOnFailed(e -> {
            historyListContainer.getChildren().clear();
            Label errLbl = new Label("Unable to load Medical Tourism history from Firebase.");
            errLbl.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
            historyListContainer.getChildren().add(errLbl);
        });

        new Thread(task).start();

        card.getChildren().addAll(headerLbl, historyListContainer);
        return card;
    }

    private VBox createRequestHistoryCard(MedicalTourismRequest req) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #CBD5E1; -fx-border-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(15,23,42,0.03), 6, 0, 0, 2);");

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox headerInfo = new VBox(4);
        HBox.setHgrow(headerInfo, Priority.ALWAYS);

        Label titleLbl = new Label(req.getTreatmentName());
        titleLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #12355B;");
        titleLbl.setWrapText(true);

        Label subLbl = new Label(req.getHospitalName() + " | " + (req.getDoctorName() != null ? req.getDoctorName() : "General Team") + " | Date: " + req.getPreferredDate());
        subLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");
        subLbl.setWrapText(true);

        headerInfo.getChildren().addAll(titleLbl, subLbl);

        Label statusBadge = new Label(req.getStatus());
        String bg = "#E2E8F0"; String fg = "#1E293B";
        if ("ACCEPTED".equalsIgnoreCase(req.getStatus())) { bg = "#ECFDF5"; fg = "#059669"; }
        else if ("REJECTED".equalsIgnoreCase(req.getStatus())) { bg = "#FEF2F2"; fg = "#DC2626"; }
        else if ("PENDING".equalsIgnoreCase(req.getStatus())) { bg = "#FEF3C7"; fg = "#D97706"; }
        else if ("APPOINTMENT_SCHEDULED".equalsIgnoreCase(req.getStatus())) { bg = "#EFF6FF"; fg = "#2563EB"; }
        statusBadge.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 16;");

        topRow.getChildren().addAll(headerInfo, statusBadge);

        // Progress Timeline
        HBox timeline = buildSimpleStatusTimeline(req.getStatus());

        HBox actionRow = new HBox(12);
        actionRow.setAlignment(Pos.CENTER_RIGHT);

        Button viewDetailsBtn = new Button("View Details");
        viewDetailsBtn.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #172B4D; -fx-font-weight: bold; -fx-padding: 7 14; -fx-background-radius: 8; -fx-cursor: hand;");
        viewDetailsBtn.setOnAction(e -> showRequestDetailsModal(req));

        actionRow.getChildren().add(viewDetailsBtn);

        card.getChildren().addAll(topRow, timeline, actionRow);
        return card;
    }

    private void showRequestDetailsModal(MedicalTourismRequest req) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Request Details — " + req.getTreatmentName());

        VBox content = new VBox(18);
        content.setPadding(new Insets(24));
        content.setStyle("-fx-background-color: white;");

        Label head = new Label("Request Details");
        head.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #12355B;");

        // REQUEST INFORMATION SECTION
        VBox reqSection = new VBox(8);
        Label reqTitle = new Label("REQUEST INFORMATION");
        reqTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #2F80ED;");
        Label reqInfo = new Label("Treatment: " + req.getTreatmentName() + "\nHospital: " + req.getHospitalName() + "\nDoctor: " + (req.getDoctorName() != null ? req.getDoctorName() : "General Team") + "\nPreferred Date: " + req.getPreferredDate() + "\nBudget: ₹" + req.getMinimumBudget() + " - ₹" + req.getMaximumBudget());
        reqInfo.setStyle("-fx-text-fill: #172B4D; -fx-font-size: 13px;");
        reqSection.getChildren().addAll(reqTitle, reqInfo);

        // ESTIMATED COSTS SECTION
        if (req.getEstimatedTotalCost() > 0) {
            VBox costSection = new VBox(8);
            Label costTitleLbl = new Label("ESTIMATED COST BREAKDOWN");
            costTitleLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #2F80ED;");
            Label costInfoLbl = new Label(String.format("Treatment Cost: ₹%,.0f\nTravel Estimate: ₹%,.0f\nAccommodation: ₹%,.0f\nLocal Transport: ₹%,.0f\nEstimated Total: ₹%,.0f",
                    req.getEstimatedTreatmentCost(), req.getEstimatedTravelCost(), req.getEstimatedAccommodationCost(), req.getEstimatedTransportCost(), req.getEstimatedTotalCost()));
            costInfoLbl.setStyle("-fx-text-fill: #172B4D; -fx-font-size: 13px;");
            costSection.getChildren().addAll(costTitleLbl, costInfoLbl);
            content.getChildren().add(costSection);
        }

        // TRAVEL REQUIREMENTS SECTION
        VBox travelSection = new VBox(8);
        Label travelTitle = new Label("TRAVEL REQUIREMENTS");
        travelTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #2F80ED;");
        Label travelInfo = new Label("Airport Pickup: " + (req.isAirportAssistanceRequired() ? "Yes" : "No") + "\nAccommodation: " + (req.isAccommodationRequired() ? "Yes" : "No") + "\nTransportation: " + (req.isLocalTransportRequired() ? "Yes" : "No") + "\nLanguage Assistance: " + (req.isLanguageAssistanceRequired() ? "Yes" : "No"));
        travelInfo.setStyle("-fx-text-fill: #172B4D; -fx-font-size: 13px;");
        travelSection.getChildren().addAll(travelTitle, travelInfo);

        // DOCUMENTS SECTION
        VBox docSection = new VBox(8);
        Label docTitle = new Label("DOCUMENTS");
        docTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #2F80ED;");
        String docsStr = req.getMedicalDocuments() != null && !req.getMedicalDocuments().isEmpty() ? String.join(", ", req.getMedicalDocuments()) : "No medical documents uploaded";
        Label docInfo = new Label(docsStr);
        docInfo.setStyle("-fx-text-fill: #172B4D; -fx-font-size: 13px;");
        docSection.getChildren().addAll(docTitle, docInfo);

        // STATUS TIMELINE
        VBox statusSection = new VBox(8);
        Label statusTitle = new Label("STATUS TIMELINE");
        statusTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #2F80ED;");
        HBox timeline = buildSimpleStatusTimeline(req.getStatus());
        statusSection.getChildren().addAll(statusTitle, timeline);

        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-background-color: #2F80ED; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 8; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> modal.close());

        content.getChildren().addAll(head, reqSection, travelSection, docSection, statusSection, closeBtn);

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        Scene scene = new Scene(sp, 520, 520);
        modal.setScene(scene);
        modal.showAndWait();
    }

    private HBox buildSimpleStatusTimeline(String currentStatus) {
        HBox bar = new HBox(8);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(10, 14, 10, 14));
        bar.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 8; -fx-border-color: #E2E8F0; -fx-border-radius: 8;");

        String[] stages = {"Submitted", "Review", "Accepted", "Appointment", "Completed"};
        int stageIndex = 1;
        if ("UNDER_REVIEW".equalsIgnoreCase(currentStatus) || "MORE_INFORMATION_REQUIRED".equalsIgnoreCase(currentStatus)) stageIndex = 2;
        else if ("ACCEPTED".equalsIgnoreCase(currentStatus)) stageIndex = 3;
        else if ("APPOINTMENT_SCHEDULED".equalsIgnoreCase(currentStatus)) stageIndex = 4;
        else if ("TREATMENT_COMPLETED".equalsIgnoreCase(currentStatus)) stageIndex = 5;

        if ("REJECTED".equalsIgnoreCase(currentStatus)) {
            Label rejectedLbl = new Label("✕ Request Rejected by Hospital");
            rejectedLbl.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
            bar.getChildren().add(rejectedLbl);
            return bar;
        }

        for (int i = 1; i <= stages.length; i++) {
            Label stageLbl = new Label(stages[i - 1]);
            if (i <= stageIndex) {
                stageLbl.setStyle("-fx-text-fill: #059669; -fx-font-weight: bold; -fx-font-size: 12px;");
            } else {
                stageLbl.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12px;");
            }
            bar.getChildren().add(stageLbl);
            if (i < stages.length) {
                Label sep = new Label("➔");
                sep.setStyle("-fx-text-fill: " + (i < stageIndex ? "#059669" : "#CBD5E1") + ";");
                bar.getChildren().add(sep);
            }
        }
        return bar;
    }

    private HBox buildVisualStarRating(double rating) {
        HBox box = new HBox(4);
        box.setAlignment(Pos.CENTER_LEFT);

        if (rating <= 0.0) {
            Label noRating = new Label("No ratings yet");
            noRating.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");
            box.getChildren().add(noRating);
            return box;
        }

        int fullStars = (int) Math.round(rating);
        StringBuilder starsStr = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            if (i < fullStars) starsStr.append("★");
            else starsStr.append("☆");
        }

        Label starsLbl = new Label(starsStr.toString());
        starsLbl.setStyle("-fx-text-fill: #F59E0B; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label numLbl = new Label(String.format("%.1f", rating));
        numLbl.setStyle("-fx-text-fill: #172B4D; -fx-font-weight: bold; -fx-font-size: 13px;");

        box.getChildren().addAll(starsLbl, numLbl);
        return box;
    }

    private ImageView createImageView(String path, double width, double height) {
        try {
            InputStream is = MedicalTourism.class.getResourceAsStream(path);
            if (is != null) {
                ImageView iv = new ImageView(new Image(is));
                iv.setFitWidth(width);
                iv.setFitHeight(height);
                iv.setPreserveRatio(true);
                return iv;
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
