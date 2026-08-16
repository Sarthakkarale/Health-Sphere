package com.healthsphere.view.doctor;

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

import java.util.Objects;

/**
 * MedicalReportsView represents the Prescription Manager & Medical Reports screen for Doctors in Health-Sphere.
 * Updated to include the AI Health Assistant option in the navigation sidebar 
 * and configured with full vertical scrolling down to the bottom.
 */
public class MedicalReportsView {

    private final Stage stage;
    private final Scene scene;

    public MedicalReportsView(Stage stage) {
        this.stage = stage;
        this.scene = createScene();
    }

    public Scene getScene() {
        return this.scene;
    }

    private Scene createScene() {
        BorderPane mainRoot = new BorderPane();
        mainRoot.getStyleClass().add("root-pane");

        // --- Sidebar (Left Navigation) ---
        VBox sidebar = createSidebar();
        mainRoot.setLeft(sidebar);

        // --- Main Content Area ---
        VBox contentArea = new VBox(20);
        contentArea.setPadding(new Insets(24, 32, 24, 32));
        contentArea.getStyleClass().add("content-area");

        // 1. Top Header Bar
        HBox topHeader = createTopHeader();
        contentArea.getChildren().add(topHeader);

        // 2. Patient Info Header Bar & Action Buttons
        BorderPane patientHeader = createPatientHeader();
        contentArea.getChildren().add(patientHeader);

        // 3. Two-Column Main Layout
        HBox bodyLayout = createBodyLayout();
        contentArea.getChildren().add(bodyLayout);

        mainRoot.setCenter(contentArea);

        // --- ScrollPane to enable vertical scrolling down to the very bottom ---
        ScrollPane scrollPane = new ScrollPane(mainRoot);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false); // Allows natural vertical height growth for scrolling
        scrollPane.getStyleClass().add("content-scrollpane");

        Scene medicalReportsScene = new Scene(scrollPane, stage.getWidth(), stage.getHeight());

        try {
            medicalReportsScene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/css/medical_reports.css")).toExternalForm());
        } catch (Exception ignored) {}

        return medicalReportsScene;
    }

    /** Creates Sidebar Navigation including the AI Health Assistant option */
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPadding(new Insets(28, 16, 28, 16));
        sidebar.getStyleClass().add("sidebar");
        sidebar.setMinWidth(240);
        sidebar.setPrefWidth(240);

        // Logo Section
        HBox logoSection = new HBox(12);
        logoSection.setPadding(new Insets(0, 0, 32, 0));
        logoSection.setAlignment(Pos.CENTER_LEFT);

        StackPane logoIconBox = new StackPane();
        logoIconBox.getStyleClass().add("logo-icon-box");
        Label logoAbbr = new Label("H");
        logoAbbr.getStyleClass().add("logo-icon-text");
        logoIconBox.getChildren().add(logoAbbr);

        VBox logoText = new VBox(2);
        Label appName = new Label("Health-Sphere");
        appName.getStyleClass().add("logo-name");
        Label doctorSubtext = new Label("Doctor Dashboard");
        doctorSubtext.getStyleClass().add("logo-subtext");
        logoText.getChildren().addAll(appName, doctorSubtext);
        logoSection.getChildren().addAll(logoIconBox, logoText);

        // Navigation Menu Options (Includes Doctor Profile & AI Health Assistant)
        VBox navItems = new VBox(6);
        String[] tabs = {
            "Dashboard", "Today's Schedule", "Appointments", "Patient Details",
            "Medical Reports & Prescription", "Availability & Schedule", 
            "Doctor Profile", "AI Health Assistant"
        };
        String[] icons = {
            "ic_dashboard", "ic_schedule", "ic_appointments", "ic_patient",
            "ic_reports", "ic_availability", "ic_profile", "ic_ai_assistant"
        };

        for (int i = 0; i < tabs.length; i++) {
            HBox navTab = new HBox(14);
            navTab.getStyleClass().add("nav-tab");
            navTab.setAlignment(Pos.CENTER_LEFT);

            if (i == 4) { // Active Highlight for "Medical Reports & Prescription"
                navTab.getStyleClass().add("nav-tab-active");
            }

            ImageView icon = new ImageView(ResourceImage.load("/images/icons/" + icons[i] + ".png"));
            icon.setFitWidth(18); 
            icon.setFitHeight(18);

            Label tabLabel = new Label(tabs[i]);
            tabLabel.getStyleClass().add("nav-text");

            navTab.getChildren().addAll(icon, tabLabel);
            navItems.getChildren().add(navTab);

            final int index = i;
            navTab.setOnMouseClicked(e -> handleSidebarTabClick(index));
        }

        // Bottom Footer: Doctor Profile & Logout Option
        VBox footer = new VBox(12);
        footer.setAlignment(Pos.BOTTOM_LEFT);
        VBox.setVgrow(footer, Priority.ALWAYS);

        Separator lineDivider = new Separator();
        lineDivider.getStyleClass().add("sidebar-divider");

        HBox doctorProfile = new HBox(12);
        doctorProfile.setAlignment(Pos.CENTER_LEFT);
        doctorProfile.getStyleClass().add("sidebar-profile");

        ImageView profileIcon = new ImageView(ResourceImage.load("/images/icons/ic_doctor_profile_small.png"));
        profileIcon.setFitWidth(24); 
        profileIcon.setFitHeight(24);

        Label doctorName = new Label("Dr. Sarah");
        doctorName.getStyleClass().add("sidebar-profile-name");
        doctorProfile.getChildren().addAll(profileIcon, doctorName);

        HBox logout = new HBox(12);
        logout.setAlignment(Pos.CENTER_LEFT);
        logout.getStyleClass().add("nav-tab-logout");

        ImageView logoutIcon = new ImageView(ResourceImage.load("/images/icons/ic_logout.png"));
        logoutIcon.setFitWidth(18); 
        logoutIcon.setFitHeight(18);

        Label logoutLabel = new Label("Logout");
        logoutLabel.getStyleClass().add("nav-text-logout");
        logout.getChildren().addAll(logoutIcon, logoutLabel);

        logout.setOnMouseClicked(e -> System.out.println("Logging out user..."));

        footer.getChildren().addAll(lineDivider, doctorProfile, logout);
        sidebar.getChildren().addAll(logoSection, navItems, footer);
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
            case 7: System.out.println("Navigating to AI Health Assistant..."); break;
            default: break;
        }
    }

    /** Top Header Bar containing Page Title, Search Field, Notification Bell & Avatar */
    private HBox createTopHeader() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label pageTitle = new Label("Prescription Manager");
        pageTitle.getStyleClass().add("header-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Search Input Box
        HBox searchField = new HBox(10);
        searchField.getStyleClass().add("search-input-box");
        searchField.setAlignment(Pos.CENTER_LEFT);
        searchField.setPrefWidth(300);

        ImageView searchIcon = new ImageView(ResourceImage.load("/images/icons/ic_search.png"));
        searchIcon.setFitWidth(16); 
        searchIcon.setFitHeight(16);

        TextField searchInput = new TextField();
        searchInput.setPromptText("Search medications, ICD-10...");
        searchInput.getStyleClass().add("search-text-field");
        HBox.setHgrow(searchInput, Priority.ALWAYS);

        searchField.getChildren().addAll(searchIcon, searchInput);

        // Right Notification Bell & Doctor Avatar Icon
        HBox rightIcons = new HBox(16);
        rightIcons.setAlignment(Pos.CENTER_RIGHT);
        rightIcons.setPadding(new Insets(0, 0, 0, 16));

        StackPane notificationBox = new StackPane();
        ImageView bellIcon = new ImageView(ResourceImage.load("/images/icons/ic_bell.png"));
        bellIcon.setFitWidth(18); 
        bellIcon.setFitHeight(18);

        Circle badge = new Circle(4, Color.web("#EF4444"));
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        notificationBox.getChildren().addAll(bellIcon, badge);
        notificationBox.getStyleClass().add("clickable-icon");

        ImageView userAvatar = new ImageView(ResourceImage.load("/images/mocks/dr_sarah_avatar.png"));
        userAvatar.setFitWidth(32); 
        userAvatar.setFitHeight(32);
        Circle clip = new Circle(16, 16, 16);
        userAvatar.setClip(clip);
        userAvatar.getStyleClass().add("clickable-icon");

        rightIcons.getChildren().addAll(notificationBox, userAvatar);

        topBar.getChildren().addAll(pageTitle, spacer, searchField, rightIcons);
        return topBar;
    }

    /** Patient Header Details Row (Patient Name, Tag, Download & Send Buttons) */
    private BorderPane createPatientHeader() {
        BorderPane header = new BorderPane();
        header.setPadding(new Insets(4, 0, 8, 0));

        VBox patientInfo = new VBox(4);
        Label name = new Label("Eleanor Vance");
        name.getStyleClass().add("patient-name-title");

        HBox metaRow = new HBox(10);
        metaRow.setAlignment(Pos.CENTER_LEFT);

        Label details = new Label("F • 42 yrs • ID: P-98234");
        details.getStyleClass().add("patient-sub-details");

        Label tag = new Label("Follow-up");
        tag.getStyleClass().add("tag-follow-up");

        metaRow.getChildren().addAll(details, tag);
        patientInfo.getChildren().addAll(name, metaRow);

        HBox actionBtns = new HBox(12);
        actionBtns.setAlignment(Pos.CENTER_RIGHT);

        Button downloadBtn = new Button("Download Reports");
        ImageView dlIcon = new ImageView(ResourceImage.load("/images/icons/ic_download.png"));
        dlIcon.setFitWidth(14); 
        dlIcon.setFitHeight(14);
        downloadBtn.setGraphic(dlIcon);
        downloadBtn.getStyleClass().add("btn-secondary-action");
        downloadBtn.setOnAction(e -> System.out.println("Downloading patient reports..."));

        Button sendBtn = new Button("Send to Patient");
        ImageView sendIcon = new ImageView(ResourceImage.load("/images/icons/ic_send.png"));
        sendIcon.setFitWidth(14); 
        sendIcon.setFitHeight(14);
        sendBtn.setGraphic(sendIcon);
        sendBtn.getStyleClass().add("btn-primary-action");
        sendBtn.setOnAction(e -> System.out.println("Sending prescription to patient..."));

        actionBtns.getChildren().addAll(downloadBtn, sendBtn);

        header.setLeft(patientInfo);
        header.setRight(actionBtns);
        return header;
    }

    /** Main Body Container organizing components into 2 Columns */
    private HBox createBodyLayout() {
        HBox layout = new HBox(20);

        // --- Left Column: Rx Editor Card & Drag/Drop Upload Area ---
        VBox leftColumn = new VBox(20);
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        VBox rxEditorCard = createRxEditorCard();
        VBox uploadCard = createUploadDropCard();

        leftColumn.getChildren().addAll(rxEditorCard, uploadCard);

        // --- Right Column: Clinical Assistant & Recent History Panel ---
        VBox rightColumn = new VBox(20);
        rightColumn.setMinWidth(330);
        rightColumn.setMaxWidth(360);

        VBox clinicalAssistantCard = createClinicalAssistantCard();
        VBox recentHistoryCard = createRecentHistoryCard();

        rightColumn.getChildren().addAll(clinicalAssistantCard, recentHistoryCard);

        layout.getChildren().addAll(leftColumn, rightColumn);
        return layout;
    }

    /** Rx Editor Card containing drug details, grid input controls, and physician notes */
    private VBox createRxEditorCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("panel-card");
        card.setPadding(new Insets(20));

        // Header: Title & Add Medication Link
        BorderPane cardHeader = new BorderPane();
        HBox titleBox = new HBox(8);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        ImageView rxIcon = new ImageView(ResourceImage.load("/images/icons/ic_rx.png"));
        rxIcon.setFitWidth(18); 
        rxIcon.setFitHeight(18);

        Label titleLbl = new Label("Rx Editor");
        titleLbl.getStyleClass().add("section-card-title");
        titleBox.getChildren().addAll(rxIcon, titleLbl);

        Hyperlink addMedBtn = new Hyperlink("+ Add Medication");
        addMedBtn.getStyleClass().add("link-add-medication");
        addMedBtn.setOnAction(e -> System.out.println("Adding new medication..."));

        cardHeader.setLeft(titleBox);
        cardHeader.setRight(addMedBtn);

        // Grey Form Container Block
        VBox formBox = new VBox(12);
        formBox.getStyleClass().add("med-form-box");
        formBox.setPadding(new Insets(16));

        // Drug Name & Category Header
        BorderPane medHeader = new BorderPane();
        VBox nameBox = new VBox(2);

        HBox medTitle = new HBox(6);
        Label medName = new Label("Amoxicillin");
        medName.getStyleClass().add("med-name");
        Label medDose = new Label("500mg");
        medDose.getStyleClass().add("med-dose");
        medTitle.getChildren().addAll(medName, medDose);

        Label category = new Label("ANTIBIOTIC");
        category.getStyleClass().add("med-category");
        nameBox.getChildren().addAll(medTitle, category);

        Label closeBtn = new Label("✕");
        closeBtn.getStyleClass().add("btn-close-med");
        closeBtn.setOnMouseClicked(e -> System.out.println("Removed medication row."));

        medHeader.setLeft(nameBox);
        medHeader.setRight(closeBtn);

        // Grid Inputs (Dosage, Frequency, Duration, Timing)
        GridPane fieldsGrid = new GridPane();
        fieldsGrid.setHgap(12);
        fieldsGrid.setVgap(6);

        // Column 0: Dosage
        fieldsGrid.add(createFieldLabel("Dosage"), 0, 0);
        TextField dosageInput = new TextField("1 Tablet");
        dosageInput.getStyleClass().add("input-field");
        fieldsGrid.add(dosageInput, 0, 1);

        // Column 1: Frequency
        fieldsGrid.add(createFieldLabel("Frequency"), 1, 0);
        ComboBox<String> freqSelect = new ComboBox<>();
        freqSelect.getItems().addAll("TID (3x a day)", "BID (2x a day)", "QD (1x a day)");
        freqSelect.setValue("TID (3x a day)");
        freqSelect.getStyleClass().add("input-select");
        fieldsGrid.add(freqSelect, 1, 1);

        // Column 2: Duration
        fieldsGrid.add(createFieldLabel("Duration"), 2, 0);
        TextField durationInput = new TextField("7 Days");
        durationInput.getStyleClass().add("input-field");
        fieldsGrid.add(durationInput, 2, 1);

        // Column 3: Timing
        fieldsGrid.add(createFieldLabel("Timing"), 3, 0);
        TextField timingInput = new TextField("After Meals (pc)");
        timingInput.getStyleClass().add("input-field");
        fieldsGrid.add(timingInput, 3, 1);

        // Ensure columns distribute width evenly
        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25);
            fieldsGrid.getColumnConstraints().add(col);
        }

        formBox.getChildren().addAll(medHeader, fieldsGrid);

        // Physician Notes Text Area
        VBox notesBox = new VBox(6);
        Label notesLabel = new Label("Physician Notes / Instructions");
        notesLabel.getStyleClass().add("input-label");

        TextArea notesArea = new TextArea("Take with plenty of water...");
        notesArea.getStyleClass().add("notes-text-area");
        notesArea.setPrefRowCount(3);

        notesBox.getChildren().addAll(notesLabel, notesArea);

        card.getChildren().addAll(cardHeader, formBox, notesBox);
        return card;
    }

    private Label createFieldLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("input-label");
        return l;
    }

    /** Drag and Drop Upload Box for Lab Reports */
    private VBox createUploadDropCard() {
        VBox dropCard = new VBox(12);
        dropCard.getStyleClass().add("upload-drop-card");
        dropCard.setAlignment(Pos.CENTER);
        dropCard.setPadding(new Insets(32));

        StackPane cloudIconBox = new StackPane();
        cloudIconBox.getStyleClass().add("cloud-icon-bg");

        ImageView cloudIcon = new ImageView(ResourceImage.load("/images/icons/ic_cloud_upload.png"));
        cloudIcon.setFitWidth(22); 
        cloudIcon.setFitHeight(22);
        cloudIconBox.getChildren().add(cloudIcon);

        Label title = new Label("Upload Lab Reports");
        title.getStyleClass().add("upload-title");

        Label subtext = new Label("Drag & drop clinical documents here, or click to browse.");
        subtext.getStyleClass().add("upload-subtext");

        Button browseBtn = new Button("Browse Files");
        browseBtn.getStyleClass().add("btn-browse-files");
        browseBtn.setOnAction(e -> System.out.println("Opening file chooser..."));

        dropCard.getChildren().addAll(cloudIconBox, title, subtext, browseBtn);
        return dropCard;
    }

    /** Clinical Assistant AI Box containing diagnostic suggestions and interaction warnings */
    private VBox createClinicalAssistantCard() {
        VBox card = new VBox(14);
        card.getStyleClass().add("assistant-card");
        card.setPadding(new Insets(18));

        HBox titleBox = new HBox(8);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        ImageView aiIcon = new ImageView(ResourceImage.load("/images/icons/ic_robot.png"));
        aiIcon.setFitWidth(18); 
        aiIcon.setFitHeight(18);

        Label titleLbl = new Label("Clinical Assistant");
        titleLbl.getStyleClass().add("assistant-title");
        titleBox.getChildren().addAll(aiIcon, titleLbl);

        // Interaction Alert
        VBox alert1 = new VBox(6);
        alert1.getStyleClass().add("assistant-alert-box");
        alert1.setPadding(new Insets(12));

        HBox alert1Header = new HBox(6);
        alert1Header.setAlignment(Pos.CENTER_LEFT);

        ImageView warnIcon = new ImageView(ResourceImage.load("/images/icons/ic_warning.png"));
        warnIcon.setFitWidth(14); 
        warnIcon.setFitHeight(14);

        Label alert1Title = new Label("Potential Interaction");
        alert1Title.getStyleClass().add("alert-warning-title");
        alert1Header.getChildren().addAll(warnIcon, alert1Title);

        Label alert1Text = new Label("Patient is currently taking Lisinopril. Amoxicillin has no severe interactions, but monitor for mild renal stress.");
        alert1Text.setWrapText(true);
        alert1Text.getStyleClass().add("assistant-alert-desc");

        alert1.getChildren().addAll(alert1Header, alert1Text);

        // Guideline Suggestion
        VBox alert2 = new VBox(6);
        alert2.getStyleClass().add("assistant-alert-box");
        alert2.setPadding(new Insets(12));

        HBox alert2Header = new HBox(6);
        alert2Header.setAlignment(Pos.CENTER_LEFT);

        ImageView lightIcon = new ImageView(ResourceImage.load("/images/icons/ic_bulb.png"));
        lightIcon.setFitWidth(14); 
        lightIcon.setFitHeight(14);

        Label alert2Title = new Label("Guideline Suggestion");
        alert2Title.getStyleClass().add("alert-info-title");
        alert2Header.getChildren().addAll(lightIcon, alert2Title);

        Label alert2Text = new Label("Consider prescribing a probiotic alongside the antibiotic course to prevent GI distress.");
        alert2Text.setWrapText(true);
        alert2Text.getStyleClass().add("assistant-alert-desc");

        alert2.getChildren().addAll(alert2Header, alert2Text);

        card.getChildren().addAll(titleBox, alert1, alert2);
        return card;
    }

    /** Recent History Sidebar Card with activity timeline nodes */
    private VBox createRecentHistoryCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("panel-card");
        card.setPadding(new Insets(18));

        HBox titleBox = new HBox(8);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        ImageView clockIcon = new ImageView(ResourceImage.load("/images/icons/ic_history.png"));
        clockIcon.setFitWidth(18); 
        clockIcon.setFitHeight(18);

        Label titleLbl = new Label("Recent History");
        titleLbl.getStyleClass().add("section-card-title");
        titleBox.getChildren().addAll(clockIcon, titleLbl);

        VBox historyList = new VBox(16);

        // Item 1
        VBox item1 = createHistoryTimelineItem(
                "Oct 12, 2023",
                "Complete Blood Count (CBC)",
                "WBC slightly elevated. All other markers normal.",
                true
        );

        // Item 2
        VBox item2 = createHistoryTimelineItem(
                "Sep 05, 2023",
                "Prescription Renewed",
                "Lisinopril 10mg QD.",
                false
        );

        // Item 3
        VBox item3 = createHistoryTimelineItem(
                "Jan 22, 2023",
                "Annual Physical",
                "General health good. BP 120/80.",
                false
        );

        historyList.getChildren().addAll(item1, item2, item3);
        card.getChildren().addAll(titleBox, historyList);
        return card;
    }

    private VBox createHistoryTimelineItem(String date, String title, String details, boolean isHighlighted) {
        VBox box = new VBox(3);
        box.getStyleClass().add("history-timeline-node");

        Label dateLbl = new Label(date);
        dateLbl.getStyleClass().add(isHighlighted ? "history-date-blue" : "history-date-grey");

        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("history-item-heading");

        Label detailsLbl = new Label(details);
        detailsLbl.setWrapText(true);
        detailsLbl.getStyleClass().add("history-item-subtext");

        box.getChildren().addAll(dateLbl, titleLbl, detailsLbl);
        return box;
    }
}