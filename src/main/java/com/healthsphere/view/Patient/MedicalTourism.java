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
import javafx.stage.Stage;

import java.io.InputStream;
import java.time.LocalDate;
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
    private CheckBox airportCheckBox;
    private CheckBox accommodationCheckBox;
    private CheckBox localTransportCheckBox;
    private CheckBox translatorCheckBox;
    private TextArea requirementsArea;
    private final List<String> attachedDocuments = new ArrayList<>();
    private Label attachedDocsLabel;

    // Wizard navigation state
    private int currentStep = 1; // 1: Search, 2: Hospital Results, 3: Compare, 4: Doctor Selection, 5: Confirmation
    private VBox wizardContainer;
    private HBox stepIndicatorBar;
    private TabPane mainTabPane;

    // Selected Data
    private HospitalProfile selectedHospital;
    private DoctorProfile selectedDoctor;
    private final List<HospitalProfile> comparisonList = new ArrayList<>();

    // Data cache
    private List<HospitalProfile> cachedHospitals = new ArrayList<>();
    private List<DoctorProfile> cachedDoctors = new ArrayList<>();
    private Map<String, Double> hospitalRatingsMap = new HashMap<>();

    public MedicalTourism(Stage stage) {
        this.stage = stage;
        this.tourismController = new MedicalTourismController();
        this.travelController = new TravelSupportController();
        this.hospitalDAO = new HospitalDAO();
        this.doctorDAO = new DoctorDAO();
        this.reviewController = new ReviewController();
    }

    public Scene getScene() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(10));
        mainContent.setFillWidth(true);

        mainTabPane = new TabPane();
        mainTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        mainTabPane.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Tab findTreatmentTab = new Tab("✈  Find Treatment & Request");
        findTreatmentTab.setContent(buildFindTreatmentWizard());

        Tab myRequestsTab = new Tab("📋  My Medical Tourism Requests");
        myRequestsTab.setContent(buildMyRequestsHistoryView());

        mainTabPane.getTabs().addAll(findTreatmentTab, myRequestsTab);

        mainContent.getChildren().add(mainTabPane);

        return PatientUI.createScene(
                stage,
                "Medical Tourism",
                "Medical Tourism",
                "Find hospitals and doctors based on your treatment, location and budget.",
                mainContent
        );
    }

    // =========================================================================
    // TAB 1: FIND TREATMENT WIZARD (STEPS 1 - 5)
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
        HBox bar = new HBox(10);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(12, 16, 12, 16));
        bar.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-radius: 12;");

        String[] steps = {"1. Find Treatment", "2. Suitable Hospitals", "3. Compare", "4. Choose Doctor", "5. Confirmation"};
        for (int i = 1; i <= steps.length; i++) {
            Label stepLbl = new Label(steps[i - 1]);
            if (i == currentStep) {
                stepLbl.setStyle("-fx-background-color: #2F80ED; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 16;");
            } else if (i < currentStep) {
                stepLbl.setStyle("-fx-background-color: #ECFDF5; -fx-text-fill: #059669; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 16;");
            } else {
                stepLbl.setStyle("-fx-background-color: transparent; -fx-text-fill: #94A3B8; -fx-padding: 6 14;");
            }
            bar.getChildren().add(stepLbl);
            if (i < steps.length) {
                Label arrow = new Label("→");
                arrow.setStyle("-fx-text-fill: #94A3B8; -fx-font-weight: bold;");
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
            case 3: wizardContainer.getChildren().add(buildStep3HospitalComparison()); break;
            case 4: wizardContainer.getChildren().add(buildStep4DoctorSelection()); break;
            case 5: wizardContainer.getChildren().add(buildStep5ConfirmationForm()); break;
            default:
                currentStep = 1;
                wizardContainer.getChildren().add(buildStep1HeroAndSearch());
                break;
        }
    }

    // =========================================================================
    // STEP 1 — HERO & SEARCH FORM
    // =========================================================================

    private VBox buildStep1HeroAndSearch() {
        VBox card = new VBox(20);
        card.setFillWidth(true);

        // Clean Hero Card
        VBox heroCard = new VBox(12);
        heroCard.setPadding(new Insets(24));
        heroCard.setStyle(
                "-fx-background-color: linear-gradient(to right, #12355B, #1D4E7A);" +
                "-fx-background-radius: 16;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 10, 0, 0, 4);"
        );

        HBox heroRow = new HBox(20);
        heroRow.setAlignment(Pos.CENTER_LEFT);

        VBox heroText = new VBox(8);
        HBox.setHgrow(heroText, Priority.ALWAYS);

        Label headline = new Label("Find the Right Care, Anywhere");
        headline.setStyle("-fx-font-size: 24px; -fx-font-weight: 800; -fx-text-fill: white;");

        Label subline = new Label("Find hospitals and doctors based on your treatment, location and budget.");
        subline.setStyle("-fx-font-size: 14px; -fx-text-fill: #D6E4F0;");

        heroText.getChildren().addAll(headline, subline);

        ImageView heroImg = createImageView("/images/healthcare_ai_hero.png", 200, 110);
        if (heroImg == null) {
            heroImg = createImageView("/images/hospital_care.jpg", 200, 110);
        }

        if (heroImg != null) {
            heroRow.getChildren().addAll(heroText, heroImg);
        } else {
            heroRow.getChildren().add(heroText);
        }

        heroCard.getChildren().add(heroRow);

        // Search Form Card
        VBox formCard = PatientUI.coloredCard("🔍  Search Hospitals & Treatments", "#DBEAFE");
        formCard.setFillWidth(true);

        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(15); grid.setPadding(new Insets(15));

        Label treatmentLbl = new Label("Treatment / Specialty *");
        treatmentLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");
        treatmentCombo = new ComboBox<>();
        treatmentCombo.getItems().addAll(
                "Cardiac Surgery", "Cardiology", "Orthopedics & Joint Replacement",
                "Neurosurgery & Neurology", "Oncology (Cancer Treatment)",
                "Organ Transplant", "Gastroenterology", "Dental Surgery",
                "Cosmetic & Plastic Surgery", "General Surgery"
        );
        treatmentCombo.setValue("Cardiac Surgery");
        treatmentCombo.setMaxWidth(Double.MAX_VALUE);

        Label locLbl = new Label("Preferred Location (City) *");
        locLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");
        locationCombo = new ComboBox<>();
        locationCombo.getItems().addAll("Pune", "Mumbai", "Delhi", "Bangalore", "Hyderabad", "All Cities");
        locationCombo.setValue("Pune");
        locationCombo.setMaxWidth(Double.MAX_VALUE);

        Label minBudgetCot = new Label("Minimum Budget (₹) *");
        minBudgetCot.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");
        minBudgetField = new TextField("100000");

        Label maxBudgetCot = new Label("Maximum Budget (₹) *");
        maxBudgetCot.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");
        maxBudgetField = new TextField("400000");

        Label dateLbl = new Label("Preferred Date *");
        dateLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");
        datePicker = new DatePicker(LocalDate.now().plusDays(14));
        datePicker.setMaxWidth(Double.MAX_VALUE);

        Label typeLbl = new Label("Patient Type *");
        typeLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");
        patientTypeCombo = new ComboBox<>();
        patientTypeCombo.getItems().addAll("International", "Outstation", "Local");
        patientTypeCombo.setValue("International");
        patientTypeCombo.setMaxWidth(Double.MAX_VALUE);

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
        actionRow.setPadding(new Insets(15, 0, 0, 0));

        Button findHospitalsBtn = new Button("Find Hospitals  →");
        findHospitalsBtn.setStyle("-fx-background-color: #2F80ED; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 10 24; -fx-background-radius: 8; -fx-cursor: hand;");
        findHospitalsBtn.setOnAction(e -> {
            if (validateStep1Inputs()) {
                currentStep = 2;
                renderCurrentStep();
            }
        });

        actionRow.getChildren().add(findHospitalsBtn);
        formCard.getChildren().addAll(grid, actionRow);

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
    // STEP 2 — HOSPITAL RESULTS ("SUITABLE HOSPITALS")
    // =========================================================================

    private VBox buildStep2HospitalResults() {
        VBox card = PatientUI.coloredCard("🏥  Suitable Hospitals", "#DBEAFE");
        card.setFillWidth(true);

        VBox hospitalsContainer = new VBox(15);
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
                VBox emptyBox = new VBox(15);
                emptyBox.setAlignment(Pos.CENTER);
                emptyBox.setPadding(new Insets(30));
                Label emptyLbl = new Label("No matching hospitals found for your preferred criteria.");
                emptyLbl.setStyle("-fx-font-size: 15px; -fx-text-fill: #64748B;");
                emptyBox.getChildren().add(emptyLbl);
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

        Button backBtn = new Button("← Back to Search");
        backBtn.setStyle("-fx-background-color: #E2E8F0; -fx-text-fill: #1E293B; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(evt -> { currentStep = 1; renderCurrentStep(); });

        Button compareBtn = new Button("Compare Selected Hospitals (" + comparisonList.size() + ")");
        compareBtn.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        compareBtn.setOnAction(evt -> {
            if (comparisonList.isEmpty()) {
                showAlert("Comparison", "Please select at least one hospital checkbox to compare.");
                return;
            }
            currentStep = 3;
            renderCurrentStep();
        });

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        navRow.getChildren().addAll(backBtn, spacer, compareBtn);

        card.getChildren().addAll(hospitalsContainer, navRow);
        return card;
    }

    private VBox createHospitalResultCard(HospitalProfile hospital) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #CBD5E1; -fx-border-radius: 12;");

        HBox topRow = new HBox(14);
        topRow.setAlignment(Pos.CENTER_LEFT);

        ImageView hospImg = createImageView("/images/hospital_care.jpg", 110, 80);
        if (hospImg == null) {
            hospImg = createImageView("/images/hospital_dashboard.jpg", 110, 80);
        }

        VBox titleBox = new VBox(4);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        Label nameLbl = new Label(hospital.getHospitalName());
        nameLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        Label locLbl = new Label("📍 " + (hospital.getAddress() != null && !hospital.getAddress().isBlank() ? hospital.getAddress() : "Pune / Maharashtra"));
        locLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

        titleBox.getChildren().addAll(nameLbl, locLbl);

        if (hospImg != null) {
            topRow.getChildren().addAll(hospImg, titleBox);
        } else {
            topRow.getChildren().add(titleBox);
        }

        HBox detailsRow = new HBox(20);
        detailsRow.setAlignment(Pos.CENTER_LEFT);
        detailsRow.setPadding(new Insets(8, 0, 8, 0));

        double rating = hospitalRatingsMap.getOrDefault(hospital.getUid(), 0.0);
        String ratingText = rating > 0 ? String.format("%.1f ★", rating) : "No ratings yet";

        Label ratingLbl = new Label("Rating: " + ratingText);
        ratingLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #D97706;");

        String beds = hospital.getBeds() != null && !hospital.getBeds().isBlank() ? hospital.getBeds() : "No bed data";
        Label bedsLbl = new Label("Beds: " + beds);
        bedsLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #2F80ED;");

        Label costLbl = new Label("Est. Cost: ₹2,50,000 - ₹3,50,000");
        costLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #16A34A;");

        long docCount = cachedDoctors.stream()
                .filter(d -> d != null && d.getHospitalAffiliation() != null && d.getHospitalAffiliation().toLowerCase().contains(hospital.getHospitalName().toLowerCase()))
                .count();
        Label docsCountLbl = new Label("Doctors: " + (docCount > 0 ? docCount + " Specialists" : "Team Available"));
        docsCountLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569;");

        detailsRow.getChildren().addAll(ratingLbl, bedsLbl, costLbl, docsCountLbl);

        HBox btnRow = new HBox(12);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        CheckBox selectCompare = new CheckBox("Add to Compare");
        selectCompare.setSelected(comparisonList.stream().anyMatch(c -> c.getUid() != null && c.getUid().equals(hospital.getUid())));
        selectCompare.setOnAction(e -> {
            if (selectCompare.isSelected()) {
                if (!comparisonList.contains(hospital)) comparisonList.add(hospital);
            } else {
                comparisonList.removeIf(c -> c.getUid() != null && c.getUid().equals(hospital.getUid()));
            }
        });

        Button selectHospitalBtn = new Button("Select Hospital  →");
        selectHospitalBtn.setStyle("-fx-background-color: #2F80ED; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        selectHospitalBtn.setOnAction(e -> {
            selectedHospital = hospital;
            currentStep = 4;
            renderCurrentStep();
        });

        btnRow.getChildren().addAll(selectCompare, selectHospitalBtn);

        card.getChildren().addAll(topRow, detailsRow, btnRow);
        return card;
    }

    // =========================================================================
    // STEP 3 — HOSPITAL COMPARISON
    // =========================================================================

    private VBox buildStep3HospitalComparison() {
        VBox card = PatientUI.coloredCard("⚖  Hospital Comparison", "#DBEAFE");
        card.setFillWidth(true);

        if (comparisonList.isEmpty()) {
            VBox emptyBox = new VBox(15);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(30));
            Label msg = new Label("No hospitals selected for comparison. Please go back and select at least one hospital.");
            msg.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748B;");

            Button backBtn = new Button("← Back to Hospitals");
            backBtn.setStyle("-fx-background-color: #2F80ED; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 8;");
            backBtn.setOnAction(e -> { currentStep = 2; renderCurrentStep(); });

            emptyBox.getChildren().addAll(msg, backBtn);
            card.getChildren().add(emptyBox);
            return card;
        }

        GridPane table = new GridPane();
        table.setHgap(15); table.setVgap(12); table.setPadding(new Insets(15));
        table.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #CBD5E1; -fx-border-radius: 10;");

        Label paramHead = new Label("Feature / Metric");
        paramHead.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1E3A8A;");
        table.add(paramHead, 0, 0);

        for (int i = 0; i < comparisonList.size(); i++) {
            HospitalProfile h = comparisonList.get(i);
            Label hName = new Label(h.getHospitalName());
            hName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1E3A8A;");
            table.add(hName, i + 1, 0);
        }

        String[][] metrics = {
                {"Location", "address"},
                {"Average Rating", "rating"},
                {"Bed Capacity", "beds"},
                {"Estimated Cost", "cost"},
                {"Doctors Available", "doctors"}
        };

        for (int r = 0; r < metrics.length; r++) {
            Label mTitle = new Label(metrics[r][0]);
            mTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569;");
            table.add(mTitle, 0, r + 1);

            for (int col = 0; col < comparisonList.size(); col++) {
                HospitalProfile h = comparisonList.get(col);
                String val = "N/A";
                switch (metrics[r][1]) {
                    case "address": val = h.getAddress() != null && !h.getAddress().isBlank() ? h.getAddress() : "Pune"; break;
                    case "rating":
                        double rat = hospitalRatingsMap.getOrDefault(h.getUid(), 0.0);
                        val = rat > 0 ? String.format("%.1f ★", rat) : "No ratings yet";
                        break;
                    case "beds": val = h.getBeds() != null && !h.getBeds().isBlank() ? h.getBeds() : "Available"; break;
                    case "cost": val = "₹2,50,000 - ₹3,50,000"; break;
                    case "doctors":
                        long dCount = cachedDoctors.stream()
                                .filter(d -> d != null && d.getHospitalAffiliation() != null && d.getHospitalAffiliation().toLowerCase().contains(h.getHospitalName().toLowerCase()))
                                .count();
                        val = dCount > 0 ? dCount + " Doctors" : "Medical Team";
                        break;
                }
                Label cellVal = new Label(val);
                table.add(cellVal, col + 1, r + 1);
            }
        }

        HBox chooseRow = new HBox(15);
        chooseRow.setPadding(new Insets(15, 0, 0, 0));
        chooseRow.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("← Back to Hospitals List");
        backBtn.setStyle("-fx-background-color: #E2E8F0; -fx-text-fill: #1E293B; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> { currentStep = 2; renderCurrentStep(); });

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        chooseRow.getChildren().addAll(backBtn, spacer);

        for (HospitalProfile h : comparisonList) {
            Button selectThisBtn = new Button("Select " + h.getHospitalName());
            selectThisBtn.setStyle("-fx-background-color: #2F80ED; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 14; -fx-background-radius: 8; -fx-cursor: hand;");
            selectThisBtn.setOnAction(e -> {
                selectedHospital = h;
                currentStep = 4;
                renderCurrentStep();
            });
            chooseRow.getChildren().add(selectThisBtn);
        }

        card.getChildren().addAll(table, chooseRow);
        return card;
    }

    // =========================================================================
    // STEP 4 — DOCTOR SELECTION ("CHOOSE YOUR DOCTOR")
    // =========================================================================

    private VBox buildStep4DoctorSelection() {
        VBox card = PatientUI.coloredCard("👨‍⚕️  Choose Your Doctor at " + (selectedHospital != null ? selectedHospital.getHospitalName() : "Selected Hospital"), "#DBEAFE");
        card.setFillWidth(true);

        VBox doctorsContainer = new VBox(12);
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
            Label msg = new Label("No specific doctors currently listed for this hospital. You may proceed with General Medical Team consultation.");
            msg.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748B;");

            Button proceedBtn = new Button("Proceed with Hospital Team Consultation →");
            proceedBtn.setStyle("-fx-background-color: #2F80ED; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 8;");
            proceedBtn.setOnAction(e -> {
                selectedDoctor = null;
                currentStep = 5;
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
        backBtn.setStyle("-fx-background-color: #E2E8F0; -fx-text-fill: #1E293B; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> { currentStep = 2; renderCurrentStep(); });

        navRow.getChildren().add(backBtn);
        card.getChildren().addAll(doctorsContainer, navRow);
        return card;
    }

    private VBox createDoctorCard(DoctorProfile doc) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #CBD5E1; -fx-border-radius: 10;");

        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        String docName = (doc.getFirstName() != null ? doc.getFirstName() : "") + " " + (doc.getLastName() != null ? doc.getLastName() : "");
        if (docName.isBlank()) docName = "Dr. Medical Practitioner";

        Label nameLbl = new Label(docName);
        nameLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        Label specLbl = new Label("Specialization: " + (doc.getSpecialization() != null ? doc.getSpecialization() : "General Specialist"));
        specLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #475569;");

        double docRating = reviewController.getAverageRating("DOCTOR", doc.getUid());
        String dRatingStr = docRating > 0 ? String.format("%.1f ★", docRating) : "No ratings yet";

        Label expLbl = new Label("Experience: " + (doc.getExperience() != null ? doc.getExperience() : "10+ Years") + " | Rating: " + dRatingStr);
        expLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");

        info.getChildren().addAll(nameLbl, specLbl, expLbl);

        Button selectBtn = new Button("Select Doctor  →");
        selectBtn.setStyle("-fx-background-color: #2F80ED; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 8; -fx-cursor: hand;");
        selectBtn.setOnAction(e -> {
            selectedDoctor = doc;
            currentStep = 5;
            renderCurrentStep();
        });

        row.getChildren().addAll(info, selectBtn);
        card.getChildren().add(row);
        return card;
    }

    // =========================================================================
    // STEP 5 — CONFIRMATION & CREATE REQUEST FORM
    // =========================================================================

    private VBox buildStep5ConfirmationForm() {
        VBox card = PatientUI.coloredCard("✅  Create Medical Tourism Request", "#DBEAFE");
        card.setFillWidth(true);

        GridPane summary = new GridPane();
        summary.setHgap(15); summary.setVgap(12); summary.setPadding(new Insets(15));
        summary.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #CBD5E1; -fx-border-radius: 10;");

        summary.add(new Label("Selected Treatment:"), 0, 0);
        summary.add(new Label(treatmentCombo.getValue()), 1, 0);

        summary.add(new Label("Selected Hospital:"), 0, 1);
        summary.add(new Label(selectedHospital != null ? selectedHospital.getHospitalName() : "City Hospital"), 1, 1);

        summary.add(new Label("Selected Doctor:"), 0, 2);
        String dName = selectedDoctor != null ? ((selectedDoctor.getFirstName() != null ? selectedDoctor.getFirstName() : "") + " " + (selectedDoctor.getLastName() != null ? selectedDoctor.getLastName() : "")) : "Hospital Medical Team";
        summary.add(new Label(dName), 1, 2);

        summary.add(new Label("Preferred Date:"), 0, 3);
        summary.add(new Label(datePicker.getValue() != null ? datePicker.getValue().toString() : "Not specified"), 1, 3);

        summary.add(new Label("Budget Range:"), 0, 4);
        summary.add(new Label("₹" + minBudgetField.getText() + " - ₹" + maxBudgetField.getText()), 1, 4);

        summary.add(new Label("Patient Type:"), 0, 5);
        summary.add(new Label(patientTypeCombo.getValue()), 1, 5);

        // Travel Support Simple Checkboxes
        VBox travelSupportBox = new VBox(8);
        travelSupportBox.setPadding(new Insets(12));
        travelSupportBox.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 8; -fx-border-color: #E2E8F0; -fx-border-radius: 8;");

        Label travelHead = new Label("🧳  Travel Support Requirements:");
        travelHead.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        HBox checkRow = new HBox(20);
        airportCheckBox = new CheckBox("Airport Pickup"); airportCheckBox.setSelected(true);
        accommodationCheckBox = new CheckBox("Hotel Accommodation"); accommodationCheckBox.setSelected(true);
        localTransportCheckBox = new CheckBox("Local Transport");
        translatorCheckBox = new CheckBox("Language Translator Support");

        checkRow.getChildren().addAll(airportCheckBox, accommodationCheckBox, localTransportCheckBox, translatorCheckBox);
        travelSupportBox.getChildren().addAll(travelHead, checkRow);

        // Medical Document Attachment Box
        VBox docBox = new VBox(8);
        docBox.setPadding(new Insets(12));
        docBox.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #CBD5E1; -fx-border-radius: 8;");

        Label docHead = new Label("📎  Medical Documents (Reports / Prescriptions)");
        docHead.setStyle("-fx-font-weight: bold; -fx-text-fill: #1D4ED8;");

        attachedDocsLabel = new Label("Attached Documents: " + (attachedDocuments.isEmpty() ? "None" : String.join(", ", attachedDocuments)));
        attachedDocsLabel.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");

        Button addDocBtn = new Button("+ Attach Medical Report Document");
        addDocBtn.setStyle("-fx-background-color: #EFF6FF; -fx-text-fill: #1D4ED8; -fx-font-weight: bold; -fx-border-color: #93C5FD; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
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

        requirementsArea = new TextArea();
        requirementsArea.setPromptText("Enter any additional medical notes or travel requirements...");
        requirementsArea.setPrefRowCount(2);

        HBox actionRow = new HBox(15);
        actionRow.setAlignment(Pos.CENTER_RIGHT);
        actionRow.setPadding(new Insets(15, 0, 0, 0));

        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: #E2E8F0; -fx-text-fill: #1E293B; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> { currentStep = 4; renderCurrentStep(); });

        Button submitBtn = new Button("🚀  Submit Medical Tourism Request");
        submitBtn.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 10 24; -fx-background-radius: 8; -fx-cursor: hand;");
        submitBtn.setOnAction(e -> submitFinalRequest());

        actionRow.getChildren().addAll(backBtn, submitBtn);
        card.getChildren().addAll(summary, travelSupportBox, docBox, requirementsArea, actionRow);
        return card;
    }

    private void submitFinalRequest() {
        try {
            MedicalTourismRequest req = new MedicalTourismRequest();
            req.setPatientId(SessionManager.getPatientUid());
            req.setPatientName(SessionManager.getCurrentUser() != null ? SessionManager.getCurrentUser().getEmail().split("@")[0] : "Patient");
            req.setPatientEmail(SessionManager.getCurrentUser() != null ? SessionManager.getCurrentUser().getEmail() : "");

            req.setHospitalId(selectedHospital != null ? selectedHospital.getUid() : "hosp_default");
            req.setHospitalName(selectedHospital != null ? selectedHospital.getHospitalName() : "City Hospital");

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
            req.setAdditionalRequirements(requirementsArea.getText());

            req.setAirportAssistanceRequired(airportCheckBox.isSelected());
            req.setAccommodationRequired(accommodationCheckBox.isSelected());
            req.setMedicalDocuments(new ArrayList<>(attachedDocuments));

            tourismController.submitRequest(req);

            // Also submit travel support if selected
            if (airportCheckBox.isSelected() || accommodationCheckBox.isSelected() || localTransportCheckBox.isSelected()) {
                TravelSupportRequest tr = new TravelSupportRequest();
                tr.setPatientId(SessionManager.getPatientUid());
                tr.setMedicalTourismRequestId(req.getRequestId());
                tr.setHospitalId(req.getHospitalId());
                tr.setHospitalName(req.getHospitalName());
                tr.setServiceType(airportCheckBox.isSelected() ? "AIRPORT_PICKUP" : (accommodationCheckBox.isSelected() ? "ACCOMMODATION" : "LOCAL_TRANSPORTATION"));
                tr.setTravelDate(req.getPreferredDate());
                travelController.submitRequest(tr);
            }

            showAlert("Success!", "Your Medical Tourism Request has been submitted successfully.\nStatus: PENDING hospital review.");
            currentStep = 1;
            renderCurrentStep();

            if (mainTabPane != null) {
                mainTabPane.getSelectionModel().select(1); // Switch to My Requests tab
            }
        } catch (Exception ex) {
            showAlert("Error", "Could not submit Medical Tourism Request: " + ex.getMessage());
        }
    }

    // =========================================================================
    // TAB 2 — MY MEDICAL TOURISM REQUESTS HISTORY & STATUS TIMELINE
    // =========================================================================

    private VBox buildMyRequestsHistoryView() {
        VBox card = PatientUI.coloredCard("📋  My Medical Tourism Requests", "#DBEAFE");
        card.setFillWidth(true);

        VBox historyListContainer = new VBox(15);
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
                VBox emptyBox = new VBox(10);
                emptyBox.setAlignment(Pos.CENTER);
                emptyBox.setPadding(new Insets(30));
                Label icon = new Label("✈");
                icon.setStyle("-fx-font-size: 32px; -fx-text-fill: #2F80ED;");
                Label title = new Label("No Medical Tourism Requests Yet");
                title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");
                Label desc = new Label("Submit a request to find treatment and schedule hospital consultations.");
                desc.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");
                emptyBox.getChildren().addAll(icon, title, desc);
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

        card.getChildren().add(historyListContainer);
        return card;
    }

    private VBox createRequestHistoryCard(MedicalTourismRequest req) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #CBD5E1; -fx-border-radius: 12;");

        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox headerInfo = new VBox(4);
        HBox.setHgrow(headerInfo, Priority.ALWAYS);

        Label titleLbl = new Label("Request #" + (req.getRequestId() != null ? req.getRequestId().substring(0, Math.min(8, req.getRequestId().length())) : "MTR") + " — " + req.getTreatmentName());
        titleLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        Label subLbl = new Label("Hospital: " + req.getHospitalName() + " | Doctor: " + (req.getDoctorName() != null ? req.getDoctorName() : "General Team") + " | Date: " + req.getPreferredDate());
        subLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

        headerInfo.getChildren().addAll(titleLbl, subLbl);

        Label statusBadge = new Label(req.getStatus());
        String bg = "#E2E8F0"; String fg = "#1E293B";
        if ("ACCEPTED".equalsIgnoreCase(req.getStatus())) { bg = "#ECFDF5"; fg = "#059669"; }
        else if ("REJECTED".equalsIgnoreCase(req.getStatus())) { bg = "#FEF2F2"; fg = "#DC2626"; }
        else if ("PENDING".equalsIgnoreCase(req.getStatus())) { bg = "#FEF3C7"; fg = "#D97706"; }
        else if ("APPOINTMENT_SCHEDULED".equalsIgnoreCase(req.getStatus())) { bg = "#EFF6FF"; fg = "#2563EB"; }
        statusBadge.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; -fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 16;");

        topRow.getChildren().addAll(headerInfo, statusBadge);

        // Simple 5-stage Status Timeline
        HBox timeline = buildSimpleStatusTimeline(req.getStatus());

        card.getChildren().addAll(topRow, timeline);
        return card;
    }

    private HBox buildSimpleStatusTimeline(String currentStatus) {
        HBox bar = new HBox(8);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(10, 14, 10, 14));
        bar.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 8;");

        String[] stages = {"Request Submitted", "Hospital Review", "Accepted", "Appointment", "Treatment"};
        int stageIndex = 1;
        if ("UNDER_REVIEW".equalsIgnoreCase(currentStatus) || "MORE_INFORMATION_REQUIRED".equalsIgnoreCase(currentStatus)) stageIndex = 2;
        else if ("ACCEPTED".equalsIgnoreCase(currentStatus)) stageIndex = 3;
        else if ("APPOINTMENT_SCHEDULED".equalsIgnoreCase(currentStatus)) stageIndex = 4;
        else if ("TREATMENT_COMPLETED".equalsIgnoreCase(currentStatus)) stageIndex = 5;

        if ("REJECTED".equalsIgnoreCase(currentStatus)) {
            Label rejectedLbl = new Label("❌ Request Rejected by Hospital");
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
