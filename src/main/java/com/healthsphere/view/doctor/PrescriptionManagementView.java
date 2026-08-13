package com.healthsphere.view.doctor;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.Objects;

public class PrescriptionManagementView {

    private final Stage stage;

    public PrescriptionManagementView(Stage stage) {
        this.stage = stage;
    }

    public Scene createScene() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("main-container");

        // 1. Sidebar Navigation
        mainLayout.setLeft(createSidebar());

        // 2. Center Content Area Split
        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(20, 24, 20, 24));
        contentBox.getStyleClass().add("content-area");

        HBox headerBar = createHeaderBar();

        HBox splitView = new HBox(20);
        HBox.setHgrow(splitView, Priority.ALWAYS);

        VBox leftColumn = createMainPrescriptionColumn();
        VBox rightColumn = createRightPatientSummaryColumn();

        HBox.setHgrow(leftColumn, Priority.ALWAYS);
        rightColumn.setPrefWidth(320);

        splitView.getChildren().addAll(leftColumn, rightColumn);
        contentBox.getChildren().addAll(headerBar, splitView);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("custom-scroll-pane");

        mainLayout.setCenter(scrollPane);

        Scene scene = new Scene(mainLayout, stage.getWidth(), stage.getHeight());

        try {
            scene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/css/prescription_reports.css")).toExternalForm());
        } catch (Exception e) {
            System.err.println("CSS file /css/prescription_reports.css not found.");
        }

        return scene;
    }

    private SVGPath createSVGPath(String d, String fillColor, double scale) {
        SVGPath path = new SVGPath();
        path.setContent(d);
        path.setFill(Color.web(fillColor));
        path.setScaleX(scale);
        path.setScaleY(scale);
        return path;
    }

    private ImageView createImageView(String resourcePath, double width, double height) {
        try {
            InputStream stream = getClass().getResourceAsStream(resourcePath);
            if (stream != null) {
                Image img = new Image(stream);
                ImageView iv = new ImageView(img);
                iv.setFitWidth(width);
                iv.setFitHeight(height);
                iv.setPreserveRatio(true);
                iv.setSmooth(true);
                return iv;
            }
        } catch (Exception e) {
            System.err.println("Resource missing: " + resourcePath);
        }
        return null;
    }

    // =========================================================================
    // 1. LEFT SIDEBAR NAVIGATION
    // =========================================================================
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(220);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(24, 16, 24, 16));

        Label brandLabel = new Label("MediNexus AI");
        brandLabel.getStyleClass().add("brand-title");

        Label subTitle = new Label("Clinician Portal");
        subTitle.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B; -fx-padding: 0 0 20 0;");

        VBox navBox = new VBox(6);

        Button btnDashboard = createNavButton("Dashboard", false, "M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z");
        Button btnAppointments = createNavButton("Appointments", false, "M19 4h-1V2h-2v2H8V2H6v2H5c-1.11 0-1.99.9-1.99 2L3 20c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V10h14v10z");
        Button btnPatients = createNavButton("Patients", false, "M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5z");
        Button btnReports = createNavButton("Reports", true, "M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z");
        Button btnSettings = createNavButton("Settings", false, "M19.43 12.98c.04-.32.07-.64.07-.98s-.03-.66-.07-.98l2.11-1.65c.19-.15.24-.42.12-.64l-2-3.46c-.12-.22-.39-.3-.61-.22l-2.49 1c-.52-.4-1.08-.73-1.69-.98l-.38-2.65C14.46 2.18 14.25 2 14 2h-4c-.25 0-.46.18-.49.42l-.38 2.65c-.61.25-1.17.59-1.69.98l-2.49-1c-.23-.09-.49 0-.61.22l-2 3.46c-.13.22-.07.49.12.64l2.11 1.65c-.04.32-.07.65-.07.98s.03.66.07.98l-2.11 1.65c-.19.15-.24.42-.12.64l2 3.46c.12.22.39.3.61.22l2.49-1c.52.4 1.08.73 1.69.98l.38 2.65c.03.24.24.42.49.42h4c.25 0 .46-.18.49-.42l.38-2.65c.61-.25 1.17-.59 1.69-.98l2.49 1c.23.09.49 0 .61-.22l2-3.46c.12-.22.07-.49-.12-.64l-2.11-1.65zM12 15.5c-1.93 0-3.5-1.57-3.5-3.5s1.57-3.5 3.5-3.5 3.5 1.57 3.5 3.5-1.57 3.5-3.5-3.5z");

        btnDashboard.setOnAction(e -> stage.setScene(new DoctorDashboardView(stage).createScene()));
        btnAppointments.setOnAction(e -> stage.setScene(new ScheduleView(stage).createScene()));
        btnPatients.setOnAction(e -> stage.setScene(new PatientQueueView(stage).createScene()));
        btnReports.setOnAction(e -> stage.setScene(new PrescriptionManagementView(stage).createScene()));

        navBox.getChildren().addAll(btnDashboard, btnAppointments, btnPatients, btnReports, btnSettings);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox profileCard = new HBox(10);
        profileCard.getStyleClass().add("profile-card");
        profileCard.setAlignment(Pos.CENTER_LEFT);

        ImageView avatar = createImageView("/images/doctor/uifaces-popular-avatar.png", 32, 32);
        Node avatarNode;
        if (avatar != null) {
            Circle clip = new Circle(16, 16, 16);
            avatar.setClip(clip);
            avatarNode = avatar;
        } else {
            Circle circle = new Circle(16, Color.web("#1D4ED8"));
            Label init = new Label("DS");
            init.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px;");
            avatarNode = new StackPane(circle, init);
        }

        VBox userDetails = new VBox(2);
        Label userName = new Label("Dr. Smith");
        userName.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label userRole = new Label("Chief Surgeon");
        userRole.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748B;");
        userDetails.getChildren().addAll(userName, userRole);

        profileCard.getChildren().addAll(avatarNode, userDetails);
        sidebar.getChildren().addAll(brandLabel, subTitle, navBox, spacer, profileCard);
        return sidebar;
    }

    private Button createNavButton(String title, boolean isActive, String iconSvg) {
        Button btn = new Button(title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.getStyleClass().add(isActive ? "nav-button-active" : "nav-button");

        SVGPath icon = createSVGPath(iconSvg, isActive ? "#FFFFFF" : "#64748B", 0.75);
        btn.setGraphic(icon);
        btn.setGraphicTextGap(12);
        return btn;
    }

    // =========================================================================
    // 2. TOP HEADER BAR
    // =========================================================================
    private HBox createHeaderBar() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        HBox searchContainer = new HBox(8);
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.getStyleClass().add("search-container");
        searchContainer.setPadding(new Insets(0, 12, 0, 12));

        SVGPath searchIcon = createSVGPath("M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z", "#94A3B8", 0.7);
        TextField searchField = new TextField();
        searchField.setPromptText("Search patients or reports...");
        searchField.getStyleClass().add("search-field-inner");

        searchContainer.getChildren().addAll(searchIcon, searchField);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actionsBox = new HBox(12);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);

        Button btnBell = new Button();
        btnBell.getStyleClass().add("icon-button");
        btnBell.setGraphic(createSVGPath("M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.64-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z", "#DC2626", 0.7));

        Button btnHelp = new Button("?");
        btnHelp.getStyleClass().add("icon-button-circle");

        Label brandRight = new Label("MediNexus AI");
        brandRight.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1D4ED8;");

        actionsBox.getChildren().addAll(btnBell, btnHelp, brandRight);
        header.getChildren().addAll(searchContainer, spacer, actionsBox);
        return header;
    }

    // =========================================================================
    // 3. MAIN LEFT COLUMN (New Medication Entry & Prescribed Table)
    // =========================================================================
    private VBox createMainPrescriptionColumn() {
        VBox column = new VBox(16);

        // Header Action Bar
        HBox headerActionRow = new HBox();
        headerActionRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        Label title = new Label("Prescription Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label caseId = new Label("Case ID: #MED-992384-LX");
        caseId.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        titleBox.getChildren().addAll(title, caseId);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actionBtns = new HBox(10);
        Button btnSaveDraft = new Button("💾 Save Draft");
        btnSaveDraft.getStyleClass().add("outline-button");

        Button btnPdf = new Button("📄 Generate PDF");
        btnPdf.getStyleClass().add("outline-button");

        Button btnPrint = new Button("🖨 Print Prescription");
        btnPrint.getStyleClass().add("primary-button");

        actionBtns.getChildren().addAll(btnSaveDraft, btnPdf, btnPrint);
        headerActionRow.getChildren().addAll(titleBox, spacer, actionBtns);

        // New Medication Entry Form Card
        VBox entryCard = new VBox(12);
        entryCard.getStyleClass().add("card");
        entryCard.setPadding(new Insets(16));

        Label entryTitle = new Label("⊕ New Medication Entry");
        entryTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1D4ED8;");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(12);
        formGrid.setVgap(8);

        Label lblName = new Label("Medicine Name");
        lblName.getStyleClass().add("form-label");
        TextField txtName = new TextField();
        txtName.setPromptText("e.g. Amoxici");
        txtName.getStyleClass().add("search-field");

        Label lblDosage = new Label("Dosage");
        lblDosage.getStyleClass().add("form-label");
        TextField txtDosage = new TextField();
        txtDosage.setPromptText("e.g. 500mg");
        txtDosage.getStyleClass().add("search-field");

        Label lblFreq = new Label("Frequency");
        lblFreq.getStyleClass().add("form-label");
        ComboBox<String> cbFreq = new ComboBox<>();
        cbFreq.getItems().addAll("Once daily (OD)", "Twice daily (BD)", "Thrice daily (TDS)", "PRN (As needed)");
        cbFreq.setValue("Twice daily (BD)");
        cbFreq.getStyleClass().add("custom-combo");

        Label lblDuration = new Label("Duration");
        lblDuration.getStyleClass().add("form-label");
        TextField txtDuration = new TextField();
        txtDuration.setPromptText("e.g. 7");
        txtDuration.getStyleClass().add("search-field");

        Button btnAdd = new Button("Add");
        btnAdd.getStyleClass().add("teal-button");

        formGrid.add(lblName, 0, 0);
        formGrid.add(txtName, 0, 1);
        formGrid.add(lblDosage, 1, 0);
        formGrid.add(txtDosage, 1, 1);
        formGrid.add(lblFreq, 2, 0);
        formGrid.add(cbFreq, 2, 1);
        formGrid.add(lblDuration, 3, 0);
        formGrid.add(txtDuration, 3, 1);
        formGrid.add(btnAdd, 4, 1);

        Label lblInstructions = new Label("Special Instructions");
        lblInstructions.getStyleClass().add("form-label");
        TextField txtInstructions = new TextField();
        txtInstructions.setPromptText("e.g. To be taken after food with plenty of water");
        txtInstructions.getStyleClass().add("search-field");

        entryCard.getChildren().addAll(entryTitle, formGrid, lblInstructions, txtInstructions);

        // Prescribed Medications Table Box
        VBox tableCard = new VBox(12);
        tableCard.getStyleClass().add("card");
        tableCard.setPadding(new Insets(16));

        HBox tableHeader = new HBox();
        Label tableTitle = new Label("Prescribed Medications");
        tableTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Region tSpacer = new Region();
        HBox.setHgrow(tSpacer, Priority.ALWAYS);

        Label badgeItems = new Label("3 Active Items");
        badgeItems.setStyle("-fx-background-color: #EFF6FF; -fx-text-fill: #1D4ED8; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 12;");

        tableHeader.getChildren().addAll(tableTitle, tSpacer, badgeItems);

        VBox rows = new VBox(8);
        rows.getChildren().addAll(
                createTableHeaderRow(),
                createMedicationRow("Lipitor (Atorvastatin)", "After meals", "20 mg", "Once daily (OD)", "30 days"),
                createMedicationRow("Metformin", "With breakfast", "500 mg", "Twice daily (BD)", "90 days"),
                createMedicationRow("Ventolin Inhaler", "As needed", "100 mcg", "PRN", "Until finished")
        );

        tableCard.getChildren().addAll(tableHeader, rows);

        // AI Interaction Checker Banner
        VBox aiCard = new VBox(8);
        aiCard.getStyleClass().add("ai-card");
        aiCard.setPadding(new Insets(14));

        HBox aiHeader = new HBox(6);
        SVGPath spark = createSVGPath("M12 2L14.5 9.5L22 12L14.5 14.5L12 22L9.5 14.5L2 12L9.5 9.5L12 2Z", "#1D4ED8", 0.6);
        Label aiTitle = new Label("AI Interaction Checker");
        aiTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");
        aiHeader.getChildren().addAll(spark, aiTitle);

        Label aiDesc = new Label("MediNexus AI has analyzed the current prescription list. No moderate or severe drug-drug interactions detected.");
        aiDesc.setStyle("-fx-font-size: 11px; -fx-text-fill: #1E3A8A;");

        aiCard.getChildren().addAll(aiHeader, aiDesc);

        column.getChildren().addAll(headerActionRow, entryCard, tableCard, aiCard);
        return column;
    }

    private HBox createTableHeaderRow() {
        HBox row = new HBox();
        row.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 8 12; -fx-background-radius: 6;");

        Label col1 = new Label("Medicine");
        col1.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #64748B; -fx-pref-width: 160;");

        Label col2 = new Label("Dosage");
        col2.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #64748B; -fx-pref-width: 90;");

        Label col3 = new Label("Frequency");
        col3.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #64748B; -fx-pref-width: 120;");

        Label col4 = new Label("Duration");
        col4.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #64748B; -fx-pref-width: 90;");

        Label col5 = new Label("Actions");
        col5.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #64748B;");

        row.getChildren().addAll(col1, col2, col3, col4, col5);
        return row;
    }

    private HBox createMedicationRow(String name, String subText, String dosage, String freq, String duration) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 10 12; -fx-border-color: #E2E8F0; -fx-border-width: 0 0 1px 0;");

        VBox medCell = new VBox(2);
        medCell.setPrefWidth(160);
        Label nameLbl = new Label(name);
        nameLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");
        Label subLbl = new Label(subText);
        subLbl.setStyle("-fx-font-size: 10px; -fx-font-style: italic; -fx-text-fill: #64748B;");
        medCell.getChildren().addAll(nameLbl, subLbl);

        Label dosageLbl = new Label(dosage);
        dosageLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #334155; -fx-pref-width: 90;");

        Label freqLbl = new Label(freq);
        freqLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #334155; -fx-pref-width: 120;");

        Label durationLbl = new Label(duration);
        durationLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #334155; -fx-pref-width: 90;");

        HBox actions = new HBox(6);
        Button btnEdit = new Button("✏");
        btnEdit.getStyleClass().add("icon-button-subtle");
        Button btnDelete = new Button("🗑");
        btnDelete.getStyleClass().add("icon-button-subtle");
        actions.getChildren().addAll(btnEdit, btnDelete);

        row.getChildren().addAll(medCell, dosageLbl, freqLbl, durationLbl, actions);
        return row;
    }

    // =========================================================================
    // 4. RIGHT SIDEBAR COLUMN (Patient Vitals, Timeline & Upload Documents)
    // =========================================================================
    private VBox createRightPatientSummaryColumn() {
        VBox column = new VBox(16);

        // Patient Header Card
        VBox profileCard = new VBox(12);
        profileCard.getStyleClass().add("card");
        profileCard.setPadding(new Insets(14));

        HBox pHeader = new HBox(12);
        pHeader.setAlignment(Pos.CENTER_LEFT);

        ImageView avatar = createImageView("/images/doctor/uifaces-popular-avatar (1).png", 44, 44);
        Node avatarNode;
        if (avatar != null) {
            Circle clip = new Circle(22, 22, 22);
            avatar.setClip(clip);
            avatarNode = avatar;
        } else {
            Circle circle = new Circle(22, Color.web("#3B82F6"));
            Label init = new Label("JA");
            init.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;");
            avatarNode = new StackPane(circle, init);
        }

        VBox pDetails = new VBox(2);
        Label pName = new Label("James T. Anderson");
        pName.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label pMeta = new Label("45 Years • Male • A+");
        pMeta.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748B;");
        pDetails.getChildren().addAll(pName, pMeta);

        pHeader.getChildren().addAll(avatarNode, pDetails);

        GridPane vitalsGrid = new GridPane();
        vitalsGrid.setHgap(8);
        vitalsGrid.setVgap(8);

        vitalsGrid.add(createMiniVitalCard("Blood Pressure", "128/84", "mmHg"), 0, 0);
        vitalsGrid.add(createMiniVitalCard("Heart Rate", "72", "BPM"), 1, 0);
        vitalsGrid.add(createMiniVitalCard("SpO2", "98%", ""), 0, 1);
        vitalsGrid.add(createMiniVitalCard("Weight", "82.5", "kg"), 1, 1);

        profileCard.getChildren().addAll(pHeader, vitalsGrid);

        // Medical Timeline
        VBox timelineCard = new VBox(12);
        timelineCard.getStyleClass().add("card");
        timelineCard.setPadding(new Insets(14));

        Label timelineTitle = new Label("⏱ Medical Timeline");
        timelineTitle.getStyleClass().add("card-title");

        VBox historyList = new VBox(10);
        historyList.getChildren().addAll(
                createTimelineItem("OCT 14, 2023", "Lipid Profile Analysis", "LDL: 140 mg/dL (Elevated)", "#1D4ED8"),
                createTimelineItem("SEP 22, 2023", "General Consultation", "Dr. Sarah Miller • Follow-up", "#0D9488"),
                createTimelineItem("AUG 05, 2023", "Prescription Renewed", "Metformin 500mg • 90 Days", "#64748B")
        );

        timelineCard.getChildren().addAll(timelineTitle, historyList);

        // Documents Upload Card
        VBox docsCard = new VBox(12);
        docsCard.getStyleClass().add("card");
        docsCard.setPadding(new Insets(14));

        Label docsTitle = new Label("📁 Documents");
        docsTitle.getStyleClass().add("card-title");

        VBox dropZone = new VBox(8);
        dropZone.setAlignment(Pos.CENTER);
        dropZone.setStyle("-fx-border-color: #BFDBFE; -fx-border-style: dashed; -fx-border-radius: 8px; -fx-background-color: #F8FAFC; -fx-padding: 16;");

        SVGPath cloudIcon = createSVGPath("M19.35 10.04C18.67 6.59 15.64 4 12 4 9.11 4 6.6 5.64 5.35 8.04 2.34 8.36 0 10.91 0 14c0 3.31 2.69 6 6 6h13c2.76 0 5-2.24 5-5 0-2.64-2.05-4.78-4.65-4.96zM14 13v4h-4v-4H7l5-5 5 5h-3z", "#1D4ED8", 0.7);
        Label dropText = new Label("Drag & drop files here");
        dropText.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #334155;");
        Label dropSub = new Label("Or click to browse (PDF, JPG, DICOM)");
        dropSub.setStyle("-fx-font-size: 9px; -fx-text-fill: #94A3B8;");

        dropZone.getChildren().addAll(cloudIcon, dropText, dropSub);

        VBox fileChips = new VBox(6);
        fileChips.getChildren().addAll(
                createFileChip("blood_work_o...", "#DC2626"),
                createFileChip("chest_xray_fin...", "#1D4ED8")
        );

        docsCard.getChildren().addAll(docsTitle, dropZone, fileChips);
        column.getChildren().addAll(profileCard, timelineCard, docsCard);
        return column;
    }

    private VBox createMiniVitalCard(String label, String val, String unit) {
        VBox card = new VBox(2);
        card.setStyle("-fx-background-color: #EFF6FF; -fx-padding: 8; -fx-background-radius: 6;");

        Label lLbl = new Label(label);
        lLbl.setStyle("-fx-font-size: 9px; -fx-text-fill: #64748B;");

        HBox valBox = new HBox(3);
        valBox.setAlignment(Pos.BASELINE_LEFT);
        Label vLbl = new Label(val);
        vLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label uLbl = new Label(unit);
        uLbl.setStyle("-fx-font-size: 9px; -fx-text-fill: #94A3B8;");

        valBox.getChildren().addAll(vLbl, uLbl);
        card.getChildren().addAll(lLbl, valBox);
        return card;
    }

    private HBox createTimelineItem(String date, String title, String desc, String dotColor) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.TOP_LEFT);

        Circle dot = new Circle(4, Color.web(dotColor));
        VBox.setMargin(dot, new Insets(3, 0, 0, 0));

        VBox content = new VBox(2);
        Label dateLbl = new Label(date);
        dateLbl.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #1D4ED8;");

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Label descLbl = new Label(desc);
        descLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748B;");

        content.getChildren().addAll(dateLbl, titleLbl, descLbl);
        item.getChildren().addAll(dot, content);
        return item;
    }

    private HBox createFileChip(String fileName, String iconColor) {
        HBox chip = new HBox(8);
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #E2E8F0; -fx-border-radius: 6px; -fx-padding: 6 10;");

        SVGPath icon = createSVGPath("M14 2H6c-1.1 0-1.99.9-1.99 2L4 20c0 1.1.89 2 1.99 2H18c1.1 0 2-.9 2-2V8l-6-6zm2 16H8v-2h8v2zm0-4H8v-2h8v2zm-3-5V3.5L18.5 9H13z", iconColor, 0.6);
        Label name = new Label(fileName);
        name.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #334155;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnClose = new Button("✕");
        btnClose.getStyleClass().add("icon-button-subtle");

        chip.getChildren().addAll(icon, name, spacer, btnClose);
        return chip;
    }
}