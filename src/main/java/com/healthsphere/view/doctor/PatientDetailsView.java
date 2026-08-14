package com.healthsphere.view.doctor;

import com.healthsphere.model.Patient;
import com.healthsphere.util.Navigation;
import com.healthsphere.util.ResourceImage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PatientDetailsView {

    private final Stage stage;
    private final Scene scene;

    // Containers to dynamically update when switching patients
    private VBox dynamicContentArea;
    private HBox otherPatientsBarContainer;

    // Sample list of patients
    private final List<Patient> patientList = new ArrayList<>();
    private Patient currentPatient;

    public PatientDetailsView(Stage stage) {
        this.stage = stage;
        initData();
        this.scene = createScene();
    }

    private void initData() {
        patientList.add(new Patient("Sarah Miller", "28 Years", "Female", "A+", "+1 (555) 123-4567", "/images/mocks/sarah_miller.png", "SM", "#2563EB", "#FFFFFF"));
        patientList.add(new Patient("John Doe", "34 Years", "Male", "O+", "+1 (555) 987-6543", "/images/mocks/john_d.png", "JD", "#CBD5E1", "#475569"));
        patientList.add(new Patient("Alice Walker", "42 Years", "Female", "B-", "+1 (555) 246-8101", null, "AW", "#CBD5E1", "#475569"));
        patientList.add(new Patient("Robert Smith", "55 Years", "Male", "AB+", "+1 (555) 369-1122", null, "RS", "#0D9488", "#FFFFFF"));
        
        // Default selected patient
        this.currentPatient = patientList.get(0);
    }

    public Scene getScene() {
        return this.scene;
    }

    private Scene createScene() {
        BorderPane mainRoot = new BorderPane();
        mainRoot.getStyleClass().add("root-pane");

        // Sidebar Navigation
        VBox sidebar = createSidebar();
        mainRoot.setLeft(sidebar);

        // Content Area Container
        dynamicContentArea = new VBox(20);
        dynamicContentArea.setPadding(new Insets(20, 30, 20, 30));
        dynamicContentArea.getStyleClass().add("content-area");

        // Build main content layout
        rebuildMainContent();

        // ScrollPane Container
        ScrollPane scrollPane = new ScrollPane(dynamicContentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("content-scrollpane");
        mainRoot.setCenter(scrollPane);

        Scene patientDetailsScene = new Scene(mainRoot, stage.getWidth(), stage.getHeight());
        patientDetailsScene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/css/patient_details.css")).toExternalForm());

        return patientDetailsScene;
    }

    /** Rebuilds or updates the main view layout when selected patient changes */
    private void rebuildMainContent() {
        dynamicContentArea.getChildren().clear();

        // 1. Top Bar Header
        dynamicContentArea.getChildren().add(createTopHeader());

        // 2. Quick Access Patient Switcher Bar
        otherPatientsBarContainer = createOtherPatientsBar();
        dynamicContentArea.getChildren().add(otherPatientsBarContainer);

        // 3. Selected Patient Card
        dynamicContentArea.getChildren().add(createPatientHeaderCard(currentPatient));

        // 4. Summary Cards (Allergies, Chronic Diseases, Vitals)
        dynamicContentArea.getChildren().add(createSummaryCardsGrid(currentPatient));

        // 5. Medical History & Previous Appointments Grid
        dynamicContentArea.getChildren().add(createHistoryAndAppointmentsGrid(currentPatient));

        // 6. Uploaded Reports Section
        dynamicContentArea.getChildren().add(createUploadedReportsSection());
    }

    /** Switch active patient and reload dynamic view elements */
    private void switchPatient(Patient selectedPatient) {
        this.currentPatient = selectedPatient;
        rebuildMainContent();
    }

    /** Other Patients Quick Switcher Bar */
    private HBox createOtherPatientsBar() {
        HBox card = new HBox(20);
        card.getStyleClass().add("panel-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(12, 20, 12, 20));

        Label sectionLabel = new Label("OTHER PATIENTS:");
        sectionLabel.getStyleClass().add("section-mini-title");

        HBox avatarList = new HBox(15);
        avatarList.setAlignment(Pos.CENTER_LEFT);

        for (Patient p : patientList) {
            boolean isSelected = p.equals(currentPatient);
            VBox item;

            if (p.getAvatarPath() != null) {
                item = createPatientItem(p, isSelected);
            } else {
                item = createInitialsPatientItem(p, isSelected);
            }

            // Click listener to switch patient details dynamically
            item.setOnMouseClicked(e -> switchPatient(p));
            avatarList.getChildren().add(item);
        }

        StackPane addBtn = new StackPane();
        addBtn.getStyleClass().add("add-patient-circle");
        Label plusSign = new Label("+");
        plusSign.getStyleClass().add("add-patient-plus");
        addBtn.getChildren().add(plusSign);

        avatarList.getChildren().add(addBtn);
        card.getChildren().addAll(sectionLabel, avatarList);

        return card;
    }

    private VBox createPatientItem(Patient p, boolean isSelected) {
        VBox item = new VBox(4);
        item.setAlignment(Pos.CENTER);
        item.getStyleClass().add("clickable-icon");

        ImageView avatar = new ImageView(ResourceImage.load(p.getAvatarPath()));
        avatar.setFitWidth(38); avatar.setFitHeight(38);
        Circle clip = new Circle(19, 19, 19);
        avatar.setClip(clip);

        if (isSelected) {
            StackPane activeWrapper = new StackPane(avatar);
            activeWrapper.getStyleClass().add("patient-avatar-active");
            Label nameLbl = new Label(p.getName());
            nameLbl.getStyleClass().add("patient-name-active");
            item.getChildren().addAll(activeWrapper, nameLbl);
        } else {
            Label nameLbl = new Label(p.getName());
            nameLbl.getStyleClass().add("patient-name-inactive");
            item.getChildren().addAll(avatar, nameLbl);
        }

        return item;
    }

    private VBox createInitialsPatientItem(Patient p, boolean isSelected) {
        VBox item = new VBox(4);
        item.setAlignment(Pos.CENTER);
        item.getStyleClass().add("clickable-icon");

        StackPane circle = new StackPane();
        String style = "-fx-background-color: " + p.getBgColor() + "; -fx-background-radius: 20; -fx-min-width: 38px; -fx-min-height: 38px;";
        if (isSelected) {
            style += " -fx-border-color: #2563EB; -fx-border-width: 2px; -fx-border-radius: 20px;";
        }
        circle.setStyle(style);

        Label text = new Label(p.getInitials());
        text.setStyle("-fx-text-fill: " + p.getTextColor() + "; -fx-font-weight: bold; -fx-font-size: 12px;");
        circle.getChildren().add(text);

        Label nameLbl = new Label(p.getName());
        nameLbl.getStyleClass().add(isSelected ? "patient-name-active" : "patient-name-inactive");

        item.getChildren().addAll(circle, nameLbl);
        return item;
    }

    /** Selected Patient Header Card */
    private HBox createPatientHeaderCard(Patient patient) {
        HBox card = new HBox(20);
        card.getStyleClass().add("panel-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(20));

        ImageView avatar;
        if (patient.getAvatarPath() != null) {
            avatar = new ImageView(ResourceImage.load(patient.getAvatarPath()));
        } else {
            avatar = new ImageView(ResourceImage.load("/images/mocks/sarah_miller.png")); // Fallback default
        }
        avatar.setFitWidth(80); avatar.setFitHeight(80);
        Circle clip = new Circle(40, 40, 40);
        avatar.setClip(clip);

        VBox detailsBox = new VBox(8);
        Label name = new Label(patient.getName());
        name.getStyleClass().add("patient-header-name");

        HBox metaBox = new HBox(20);
        metaBox.setAlignment(Pos.CENTER_LEFT);

        HBox ageMeta = createMetaItem("/images/icons/ic_cake.png", patient.getAge());
        HBox genderMeta = createMetaItem("/images/icons/ic_female.png", patient.getGender());
        HBox bloodMeta = createMetaItem("/images/icons/ic_blood.png", patient.getBloodType());
        HBox phoneMeta = createMetaItem("/images/icons/ic_phone.png", patient.getPhone());

        metaBox.getChildren().addAll(ageMeta, genderMeta, bloodMeta, phoneMeta);
        detailsBox.getChildren().addAll(name, metaBox);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button editBtn = new Button("Edit Patient");
        ImageView editIcon = new ImageView(ResourceImage.load("/images/icons/ic_edit.png"));
        editIcon.setFitWidth(14); editIcon.setFitHeight(14);
        editBtn.setGraphic(editIcon);
        editBtn.getStyleClass().add("btn-edit-patient");

        card.getChildren().addAll(avatar, detailsBox, spacer, editBtn);
        return card;
    }

    private HBox createMetaItem(String iconPath, String text) {
        HBox box = new HBox(6);
        box.setAlignment(Pos.CENTER_LEFT);
        ImageView icon = new ImageView(ResourceImage.load(iconPath));
        icon.setFitWidth(14); icon.setFitHeight(14);
        Label label = new Label(text);
        label.getStyleClass().add("patient-header-meta");
        box.getChildren().addAll(icon, label);
        return box;
    }

    /** Summary Cards Grid dynamically rendered for selected patient */
    private HBox createSummaryCardsGrid(Patient patient) {
        HBox grid = new HBox(20);

        // 1. Allergies
        VBox allergiesCard = createBorderedCard("Allergies", "#EF4444", "/images/icons/ic_allergy.png");
        HBox allergyPills = new HBox(10);
        if (patient.getName().equals("Sarah Miller")) {
            allergyPills.getChildren().addAll(createPill("Penicillin"), createPill("Peanuts"));
        } else {
            allergyPills.getChildren().addAll(createPill("Dust/Pollen"));
        }
        allergiesCard.getChildren().add(allergyPills);

        // 2. Chronic Diseases
        VBox chronicCard = createBorderedCard("Chronic Diseases", "#10B981", "/images/icons/ic_disease.png");
        HBox chronicPills = new HBox(10);
        if (patient.getName().equals("Sarah Miller")) {
            chronicPills.getChildren().add(createPill("Asthma"));
        } else if (patient.getName().equals("Robert Smith")) {
            chronicPills.getChildren().addAll(createPill("Hypertension"), createPill("Diabetes"));
        } else {
            chronicPills.getChildren().add(createPill("None"));
        }
        chronicCard.getChildren().add(chronicPills);

        // 3. Latest Vitals
        VBox vitalsCard = createBorderedCard("Latest Vitals", "#2563EB", "/images/icons/ic_vitals.png");
        HBox vitalsRow = new HBox(20);
        vitalsRow.setAlignment(Pos.CENTER_LEFT);

        VBox hrBox = createVitalStat("HR", patient.getName().equals("Sarah Miller") ? "72" : "80", "bpm");
        VBox bpBox = createVitalStat("BP", patient.getName().equals("Sarah Miller") ? "120/80" : "130/85", "");
        VBox weightBox = createVitalStat("Weight", patient.getName().equals("Sarah Miller") ? "65" : "78", "kg");

        vitalsRow.getChildren().addAll(hrBox, createSeparator(), bpBox, createSeparator(), weightBox);
        vitalsCard.getChildren().add(vitalsRow);

        HBox.setHgrow(allergiesCard, Priority.ALWAYS);
        HBox.setHgrow(chronicCard, Priority.ALWAYS);
        HBox.setHgrow(vitalsCard, Priority.ALWAYS);

        grid.getChildren().addAll(allergiesCard, chronicCard, vitalsCard);
        return grid;
    }

    private Label createPill(String text) {
        Label pill = new Label(text);
        pill.getStyleClass().add("tag-pill");
        return pill;
    }

    private VBox createBorderedCard(String title, String borderColor, String iconPath) {
        VBox card = new VBox(14);
        card.getStyleClass().add("panel-card");
        card.setStyle("-fx-border-color: " + borderColor + " transparent transparent transparent; -fx-border-width: 3px 1px 1px 1px;");
        card.setPadding(new Insets(16));

        HBox titleBox = new HBox(8);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        ImageView icon = new ImageView(ResourceImage.load(iconPath));
        icon.setFitWidth(18); icon.setFitHeight(18);

        Label label = new Label(title);
        label.getStyleClass().add("card-header-title");

        titleBox.getChildren().addAll(icon, label);
        card.getChildren().add(titleBox);
        return card;
    }

    private VBox createVitalStat(String labelText, String valueText, String unitText) {
        VBox box = new VBox(2);
        box.setAlignment(Pos.CENTER);

        Label lbl = new Label(labelText);
        lbl.getStyleClass().add("vital-label");

        HBox valBox = new HBox(2);
        valBox.setAlignment(Pos.BASELINE_CENTER);
        Label val = new Label(valueText);
        val.getStyleClass().add("vital-value");
        Label unit = new Label(unitText);
        unit.getStyleClass().add("vital-unit");
        valBox.getChildren().addAll(val, unit);

        box.getChildren().addAll(lbl, valBox);
        return box;
    }

    private Separator createSeparator() {
        Separator s = new Separator(javafx.geometry.Orientation.VERTICAL);
        s.setPrefHeight(28);
        return s;
    }

    /** Medical History Timeline & Appointments Table dynamically rendered */
    private HBox createHistoryAndAppointmentsGrid(Patient patient) {
        HBox grid = new HBox(20);

        // Left Column: Medical History Timeline
        VBox historyCard = new VBox(15);
        historyCard.getStyleClass().add("panel-card");
        historyCard.setPadding(new Insets(20));
        HBox.setHgrow(historyCard, Priority.ALWAYS);

        Label historyTitle = new Label("Medical History");
        historyTitle.getStyleClass().add("section-card-title");
        historyCard.getChildren().add(historyTitle);

        VBox timeline = new VBox(18);

        if (patient.getName().equals("Sarah Miller")) {
            timeline.getChildren().add(createTimelineNode(
                    "Acute Bronchitis", "Oct 15, 2023",
                    "Prescribed Amoxicillin 500mg. Recommended rest and increased fluid intake.",
                    "/images/icons/ic_stethoscope.png", "#E0F2FE"));
            timeline.getChildren().add(createTimelineNode(
                    "Annual Checkup", "Jun 02, 2023",
                    "Routine blood work clear. Administered flu shot.",
                    "/images/icons/ic_pills.png", "#E0E7FF"));
        } else {
            timeline.getChildren().add(createTimelineNode(
                    "General Consultation", "Jan 10, 2024",
                    "Patient reported fatigue. Blood panel requested.",
                    "/images/icons/ic_stethoscope.png", "#E0F2FE"));
        }

        historyCard.getChildren().add(timeline);

        // Right Column: Appointments
        VBox apptCard = new VBox(15);
        apptCard.getStyleClass().add("panel-card");
        apptCard.setPadding(new Insets(20));
        HBox.setHgrow(apptCard, Priority.ALWAYS);

        Label apptTitle = new Label("Previous Appointments");
        apptTitle.getStyleClass().add("section-card-title");

        GridPane table = new GridPane();
        table.setHgap(20);
        table.setVgap(14);

        table.add(createTableHeader("Date"), 0, 0);
        table.add(createTableHeader("Doctor"), 1, 0);
        table.add(createTableHeader("Reason"), 2, 0);
        table.add(createTableHeader("Status"), 3, 0);

        if (patient.getName().equals("Sarah Miller")) {
            table.add(new Label("Oct 15, 2023"), 0, 1);
            table.add(new Label("Dr. Julian"), 1, 1);
            table.add(new Label("Severe Cough"), 2, 1);
            table.add(createStatusPill("Completed", "pill-completed"), 3, 1);

            table.add(new Label("Jun 02, 2023"), 0, 2);
            table.add(new Label("Dr. Julian"), 1, 2);
            table.add(new Label("Annual Physical"), 2, 2);
            table.add(createStatusPill("Completed", "pill-completed"), 3, 2);
        } else {
            table.add(new Label("Jan 10, 2024"), 0, 1);
            table.add(new Label("Dr. Julian"), 1, 1);
            table.add(new Label("Fatigue Evaluation"), 2, 1);
            table.add(createStatusPill("Completed", "pill-completed"), 3, 1);
        }

        apptCard.getChildren().addAll(apptTitle, table);
        grid.getChildren().addAll(historyCard, apptCard);
        return grid;
    }

    private HBox createTimelineNode(String title, String date, String desc, String iconPath, String badgeBgColor) {
        HBox wrapper = new HBox(12);
        wrapper.setAlignment(Pos.TOP_LEFT);

        VBox timelineGraphic = new VBox(0);
        timelineGraphic.setAlignment(Pos.TOP_CENTER);

        StackPane badgeBox = new StackPane();
        badgeBox.getStyleClass().add("timeline-badge");
        badgeBox.setStyle("-fx-background-color: " + badgeBgColor + ";");

        ImageView icon = new ImageView(ResourceImage.load(iconPath));
        icon.setFitWidth(14); icon.setFitHeight(14);
        badgeBox.getChildren().add(icon);

        Region line = new Region();
        line.getStyleClass().add("timeline-line");
        VBox.setVgrow(line, Priority.ALWAYS);

        timelineGraphic.getChildren().addAll(badgeBox, line);

        VBox cardContent = createHistoryCard(title, date, desc);
        HBox.setHgrow(cardContent, Priority.ALWAYS);

        wrapper.getChildren().addAll(timelineGraphic, cardContent);
        return wrapper;
    }

    private VBox createHistoryCard(String title, String date, String desc) {
        VBox box = new VBox(8);
        box.getStyleClass().add("history-item-box");
        box.setPadding(new Insets(12));

        BorderPane header = new BorderPane();
        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("history-item-title");

        Label dateLbl = new Label(date);
        dateLbl.getStyleClass().add("history-item-date");

        header.setLeft(titleLbl);
        header.setRight(dateLbl);

        Label descLbl = new Label(desc);
        descLbl.setWrapText(true);
        descLbl.getStyleClass().add("history-item-desc");

        box.getChildren().addAll(header, descLbl);
        return box;
    }

    private Label createTableHeader(String title) {
        Label l = new Label(title);
        l.getStyleClass().add("table-header-text");
        return l;
    }

    private Label createStatusPill(String status, String styleClass) {
        Label l = new Label(status);
        l.getStyleClass().addAll("pill-status", styleClass);
        return l;
    }

    /** Top Bar Header Search and Controls */
    private HBox createTopHeader() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_RIGHT);

        HBox searchField = new HBox(10);
        searchField.getStyleClass().add("search-input-box");
        searchField.setAlignment(Pos.CENTER_LEFT);

        ImageView searchIcon = new ImageView(ResourceImage.load("/images/icons/ic_search.png"));
        searchIcon.setFitWidth(16); searchIcon.setFitHeight(16);

        TextField searchInput = new TextField();
        searchInput.setPromptText("Search patients, records...");
        searchInput.getStyleClass().add("search-text-field");
        searchField.getChildren().addAll(searchIcon, searchInput);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox rightIcons = new HBox(18);
        rightIcons.setAlignment(Pos.CENTER_RIGHT);

        StackPane notificationBox = new StackPane();
        ImageView bellIcon = new ImageView(ResourceImage.load("/images/icons/ic_bell.png"));
        bellIcon.setFitWidth(18); bellIcon.setFitHeight(18);
        Circle badge = new Circle(4, Color.RED);
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        notificationBox.getChildren().addAll(bellIcon, badge);
        notificationBox.getStyleClass().add("clickable-icon");

        ImageView settingsIcon = new ImageView(ResourceImage.load("/images/icons/ic_settings.png"));
        settingsIcon.setFitWidth(18); settingsIcon.setFitHeight(18);
        settingsIcon.getStyleClass().add("clickable-icon");

        ImageView userAvatar = new ImageView(ResourceImage.load("/images/doctor/portrait-3d-male-doctor.png"));
        userAvatar.setFitWidth(32); userAvatar.setFitHeight(32);
        Circle clip = new Circle(16, 16, 16);
        userAvatar.setClip(clip);
        userAvatar.getStyleClass().add("clickable-icon");

        rightIcons.getChildren().addAll(notificationBox, settingsIcon, userAvatar);
        topBar.getChildren().addAll(searchField, spacer, rightIcons);
        return topBar;
    }

    /** Sidebar Navigation */
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPadding(new Insets(30, 15, 30, 15));
        sidebar.getStyleClass().add("sidebar");
        sidebar.setMinWidth(250);

        HBox logoSection = new HBox(10);
        logoSection.setPadding(new Insets(0, 0, 30, 0));
        logoSection.setAlignment(Pos.CENTER_LEFT);

        StackPane logoIconBox = new StackPane();
        logoIconBox.getStyleClass().add("logo-icon-box");
        Label logoAbbr = new Label("HS");
        logoAbbr.getStyleClass().add("logo-icon-text");
        logoIconBox.getChildren().add(logoAbbr);

        VBox logoText = new VBox(0);
        Label appName = new Label("Health-Sphere");
        appName.getStyleClass().add("logo-name");
        Label doctorSubtext = new Label("Doctor Module");
        doctorSubtext.getStyleClass().add("logo-subtext");
        logoText.getChildren().addAll(appName, doctorSubtext);
        logoSection.getChildren().addAll(logoIconBox, logoText);

        VBox navItems = new VBox(8);
        String[] tabs = {
            "Dashboard", "Today's Schedule", "Appointments", "Patient Details",
            "Medical Reports & Prescription", "Availability & Schedule", "Doctor Profile", "AI Health Assistant"
        };
        String[] icons = {
            "ic_dashboard", "ic_schedule", "ic_appointments", "ic_patient",
            "ic_reports", "ic_availability", "ic_profile", "ic_ai"
        };

        for (int i = 0; i < tabs.length; i++) {
            HBox navTab = new HBox(15);
            navTab.getStyleClass().add("nav-tab");
            navTab.setAlignment(Pos.CENTER_LEFT);

            if (i == 3) {
                navTab.getStyleClass().add("nav-tab-active");
            }

            ImageView icon = new ImageView(ResourceImage.load("/images/icons/" + icons[i] + ".png"));
            icon.setFitWidth(18); icon.setFitHeight(18);

            Label tabLabel = new Label(tabs[i]);
            tabLabel.getStyleClass().add("nav-text");

            navTab.getChildren().addAll(icon, tabLabel);
            navItems.getChildren().add(navTab);

            final int index = i;
            navTab.setOnMouseClicked(e -> handleSidebarTabClick(index));
        }

        sidebar.getChildren().addAll(logoSection, navItems);
        return sidebar;
    }

    private void handleSidebarTabClick(int index) {
        switch (index) {
            case 0: Navigation.goTo(stage, () -> new DoctorDashboardView(stage).getScene()); break;
            case 1: Navigation.goTo(stage, () -> new TodaysScheduleView(stage).getScene()); break;
            case 2: Navigation.goTo(stage, () -> new AppointmentsView(stage).getScene()); break;
            case 3: Navigation.goTo(stage, () -> new PatientDetailsView(stage).getScene()); break;
            case 4: Navigation.goTo(stage, () -> new MedicalReportsView(stage).getScene()); break;
            case 5: Navigation.goTo(stage, () -> new AvailabilityScheduleView(stage).getScene()); break;
            case 6: Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene()); break;
            case 7: Navigation.goTo(stage, () -> new AIHealthAssistantView(stage).getScene()); break;
            default: break;
        }
    }

    /** Uploaded Reports Footer */
    private HBox createUploadedReportsSection() {
        BorderPane footer = new BorderPane();
        footer.getStyleClass().add("panel-card");
        footer.setPadding(new Insets(15, 20, 15, 20));

        Label title = new Label("Uploaded Reports");
        title.getStyleClass().add("section-card-title");

        Hyperlink viewAll = new Hyperlink("View All");
        viewAll.getStyleClass().add("link-view-all");
        viewAll.setOnAction(e -> Navigation.goTo(stage, () -> new MedicalReportsView(stage).getScene()));

        footer.setLeft(title);
        footer.setRight(viewAll);

        HBox wrapper = new HBox(footer);
        HBox.setHgrow(footer, Priority.ALWAYS);
        return wrapper;
    }
}