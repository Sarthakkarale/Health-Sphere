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
import javafx.scene.shape.Polyline;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.Objects;

public class PatientDetailView {

    private final Stage stage;

    public PatientDetailView(Stage stage) {
        this.stage = stage;
    }

    public Scene createScene() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("main-container");

        // 1. Sidebar Navigation
        mainLayout.setLeft(createSidebar());

        // 2. Main Content Split View
        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(20, 24, 20, 24));
        contentBox.getStyleClass().add("content-area");

        HBox headerBar = createHeaderBar();

        HBox splitView = new HBox(20);
        HBox.setHgrow(splitView, Priority.ALWAYS);

        VBox leftColumn = createMainPatientColumn();
        VBox rightColumn = createRightSidebarColumn();

        HBox.setHgrow(leftColumn, Priority.ALWAYS);
        rightColumn.setPrefWidth(310);

        splitView.getChildren().addAll(leftColumn, rightColumn);

        Label footerLabel = new Label("© 2023 MediNexus AI Healthcare Systems. All patient records are encrypted and HIPAA compliant.");
        footerLabel.getStyleClass().add("footer-text");
        HBox footerBox = new HBox(footerLabel);
        footerBox.setAlignment(Pos.CENTER);
        footerBox.setPadding(new Insets(10, 0, 10, 0));

        contentBox.getChildren().addAll(headerBar, splitView, footerBox);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("custom-scroll-pane");

        mainLayout.setCenter(scrollPane);

        Scene scene = new Scene(mainLayout, stage.getWidth(), stage.getHeight());

        try {
            scene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/css/patient_detail.css")).toExternalForm());
        } catch (Exception e) {
            System.err.println("CSS file /css/patient_detail.css not found.");
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

        Label brandLabel = new Label("St. Jude Medical");
        brandLabel.getStyleClass().add("brand-title");

        Label unitSub = new Label("Clinical Unit A");
        unitSub.setStyle("-fx-font-size: 10px; -fx-text-fill: #94A3B8; -fx-padding: 0 0 16 0;");

        VBox navBox = new VBox(6);

        Button btnDashboard = createNavButton("Dashboard", false, "M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z");
        Button btnSchedule = createNavButton("Schedule", false, "M19 4h-1V2h-2v2H8V2H6v2H5c-1.11 0-1.99.9-1.99 2L3 20c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V10h14v10z");
        Button btnPatients = createNavButton("Patients", true, "M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5z");
        Button btnAnalytics = createNavButton("Analytics", false, "M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z");
        Button btnSettings = createNavButton("Settings", false, "M19.43 12.98c.04-.32.07-.64.07-.98s-.03-.66-.07-.98l2.11-1.65c.19-.15.24-.42.12-.64l-2-3.46c-.12-.22-.39-.3-.61-.22l-2.49 1c-.52-.4-1.08-.73-1.69-.98l-.38-2.65C14.46 2.18 14.25 2 14 2h-4c-.25 0-.46.18-.49.42l-.38 2.65c-.61.25-1.17.59-1.69.98l-2.49-1c-.23-.09-.49 0-.61.22l-2 3.46c-.13.22-.07.49.12.64l2.11 1.65c-.04.32-.07.65-.07.98s.03.66.07.98l-2.11 1.65c-.19.15-.24.42-.12.64l2 3.46c.12.22.39.3.61.22l2.49-1c.52.4 1.08.73 1.69.98l.38 2.65c.03.24.24.42.49.42h4c.25 0 .46-.18.49-.42l.38-2.65c.61-.25 1.17-.59 1.69-.98l2.49 1c.23.09.49 0 .61-.22l2-3.46c.12-.22.07-.49-.12-.64l-2.11-1.65zM12 15.5c-1.93 0-3.5-1.57-3.5-3.5s1.57-3.5 3.5-3.5 3.5 1.57 3.5 3.5-1.57 3.5-3.5-3.5z");

        btnDashboard.setOnAction(e -> stage.setScene(new DoctorDashboardView(stage).createScene()));
        btnSchedule.setOnAction(e -> stage.setScene(new ScheduleView(stage).createScene()));
        btnPatients.setOnAction(e -> stage.setScene(new PatientDetailView(stage).createScene()));

        navBox.getChildren().addAll(btnDashboard, btnSchedule, btnPatients, btnAnalytics, btnSettings);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnNewConsultation = new Button("+ New Consultation");
        btnNewConsultation.getStyleClass().add("primary-button");
        btnNewConsultation.setMaxWidth(Double.MAX_VALUE);

        VBox bottomNav = new VBox(6);
        bottomNav.setPadding(new Insets(16, 0, 0, 0));
        Button btnSupport = createNavButton("Support", false, "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 16h-2v-2h2v2zm1.07-7.75l-.9.92C12.45 11.9 12 12.5 12 14h-2v-.5c0-1.1.45-2.1 1.17-2.83l1.24-1.26c.37-.36.59-.86.59-1.41 0-1.1-.9-2-2-2s-2 .9-2 2H7c0-2.76 2.24-5 5-5s5 2.24 5 5c0 1.04-.42 1.99-1.07 2.75z");
        Button btnLogout = createNavButton("Logout", false, "M10.09 15.59L11.5 17l5-5-5-5-1.41 1.41L12.67 11H3v2h9.67l-2.58 2.59zM19 3H5c-1.11 0-2 .9-2 2v4h2V5h14v14H5v-4H3v4c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2z");

        bottomNav.getChildren().addAll(btnSupport, btnLogout);

        sidebar.getChildren().addAll(brandLabel, unitSub, navBox, spacer, btnNewConsultation, bottomNav);
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

        Label appName = new Label("HealPath Pro");
        appName.getStyleClass().add("header-app-title");

        HBox searchContainer = new HBox(8);
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.getStyleClass().add("search-container");
        searchContainer.setPadding(new Insets(0, 12, 0, 12));

        SVGPath searchIcon = createSVGPath("M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z", "#94A3B8", 0.7);
        TextField searchField = new TextField();
        searchField.setPromptText("Search patient database...");
        searchField.getStyleClass().add("search-field-inner");

        searchContainer.getChildren().addAll(searchIcon, searchField);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actionsBox = new HBox(10);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);

        Button btnBell = new Button();
        btnBell.getStyleClass().add("icon-button");
        btnBell.setGraphic(createSVGPath("M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.64-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z", "#475569", 0.7));

        Button btnHelp = new Button("?");
        btnHelp.getStyleClass().add("icon-button-circle");

        Button btnGrid = new Button("⠌");
        btnGrid.getStyleClass().add("icon-button-circle");

        VBox userBox = new VBox(2);
        userBox.setAlignment(Pos.CENTER_RIGHT);
        Label userName = new Label("Dr. Julian Thorne");
        userName.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label userRole = new Label("CHIEF SURGEON");
        userRole.setStyle("-fx-font-size: 9px; -fx-text-fill: #64748B;");
        userBox.getChildren().addAll(userName, userRole);

        ImageView avatar = createImageView("/images/doctor/uifaces-popular-avatar.png", 32, 32);
        Node avatarNode;
        if (avatar != null) {
            Circle clip = new Circle(16, 16, 16);
            avatar.setClip(clip);
            avatarNode = avatar;
        } else {
            Circle circle = new Circle(16, Color.web("#1E3A8A"));
            Label init = new Label("JT");
            init.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px;");
            avatarNode = new StackPane(circle, init);
        }

        actionsBox.getChildren().addAll(btnBell, btnHelp, btnGrid, userBox, avatarNode);
        header.getChildren().addAll(appName, searchContainer, spacer, actionsBox);
        return header;
    }

    // =========================================================================
    // 3. MAIN LEFT COLUMN (Patient Info, Vitals, History, Docs, Notes)
    // =========================================================================
    private VBox createMainPatientColumn() {
        VBox column = new VBox(16);

        // Header Patient Profile Summary
        HBox profileHeader = new HBox();
        profileHeader.setAlignment(Pos.CENTER_LEFT);

        ImageView avatar = createImageView("/images/doctor/uifaces-popular-avatar (1).png", 64, 64);
        Node avatarNode;
        if (avatar != null) {
            Circle clip = new Circle(32, 32, 32);
            avatar.setClip(clip);
            avatarNode = avatar;
        } else {
            Circle circle = new Circle(32, Color.web("#3B82F6"));
            Label init = new Label("SJ");
            init.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
            avatarNode = new StackPane(circle, init);
        }

        VBox details = new VBox(4);
        details.setPadding(new Insets(0, 0, 0, 16));

        HBox nameRow = new HBox(8);
        nameRow.setAlignment(Pos.CENTER_LEFT);
        Label nameLbl = new Label("Sarah Jenkins");
        nameLbl.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Label idBadge = new Label("ID: PN-8829-01");
        idBadge.getStyleClass().add("tag-gray");

        nameRow.getChildren().addAll(nameLbl, idBadge);

        HBox infoRow = new HBox(12);
        Label ageLbl = new Label("📅 68 Years");
        ageLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");
        Label genderLbl = new Label("♀ Female");
        genderLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");
        infoRow.getChildren().addAll(ageLbl, genderLbl);

        HBox tagsRow = new HBox(6);
        Label activeTag = new Label("ACTIVE");
        activeTag.getStyleClass().add("tag-active");

        Label bloodTag = new Label("🩸 O+ (Positive)");
        bloodTag.getStyleClass().add("tag-blood");

        tagsRow.getChildren().addAll(activeTag, bloodTag);
        details.getChildren().addAll(nameRow, infoRow, tagsRow);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actionBtns = new HBox(8);
        actionBtns.setAlignment(Pos.TOP_RIGHT);

        Button btnEdit = new Button("✏ Edit Profile");
        btnEdit.getStyleClass().add("outline-button");

        Button btnSched = new Button("📅 Schedule");
        btnSched.getStyleClass().add("outline-button");

        Button btnSummary = new Button("✨ Generate Summary");
        btnSummary.getStyleClass().add("primary-button");

        Button btnPrint = new Button("🖨");
        btnPrint.getStyleClass().add("outline-button");

        actionBtns.getChildren().addAll(btnEdit, btnSched, btnSummary, btnPrint);
        profileHeader.getChildren().addAll(avatarNode, details, spacer, actionBtns);

        // Vitals Grid (4 Cards)
        HBox vitalsGrid = new HBox(12);
        vitalsGrid.getChildren().addAll(
                createVitalSparklineCard("HEART RATE", "72", "bpm", "#DC2626", "M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"),
                createVitalSparklineCard("BLOOD PRESSURE", "118/76", "mmHg", "#1D4ED8", "M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-2 10h-4v4h-2v-4H7v-2h4V7h2v4h4v2z"),
                createVitalBarCard("RESP. RATE", "16", "br/m", "#0D9488"),
                createVitalStatusCard("TEMPERATURE", "98.4", "°F", "Normal Range")
        );

        // Medical History
        VBox historyCard = new VBox(12);
        historyCard.getStyleClass().add("card");
        historyCard.setPadding(new Insets(16));

        HBox historyHeader = new HBox();
        Label historyTitle = new Label("Medical History");
        historyTitle.getStyleClass().add("card-title");

        Region historySpacer = new Region();
        HBox.setHgrow(historySpacer, Priority.ALWAYS);

        Hyperlink viewAllHistory = new Hyperlink("View All");
        viewAllHistory.getStyleClass().add("table-link");

        historyHeader.getChildren().addAll(historyTitle, historySpacer, viewAllHistory);

        VBox historyTimeline = new VBox(12);
        historyTimeline.getChildren().addAll(
                createHistoryTimelineItem("Post-Operative Review", "Oct 12, 2023", "Follow-up for hip replacement surgery. Wound healing normally. Patient reports mild stiffness in the morning.", "#1D4ED8"),
                createHistoryTimelineItem("Total Hip Arthroplasty (Surgery)", "Aug 24, 2023", "Successful procedure under general anesthesia. No immediate complications noted in the recovery ward.", "#0D9488"),
                createHistoryTimelineItem("Chronic Hypertension Diagnosis", "May 15, 2021", "Persistent BP readings above 140/90. Started on Lisinopril 10mg daily.", "#64748B")
        );

        historyCard.getChildren().addAll(historyHeader, historyTimeline);

        // Documents & Reports
        VBox docsCard = new VBox(12);
        docsCard.getStyleClass().add("card");
        docsCard.setPadding(new Insets(16));

        HBox docsHeader = new HBox();
        Label docsTitle = new Label("Documents & Reports");
        docsTitle.getStyleClass().add("card-title");

        Region docsSpacer = new Region();
        HBox.setHgrow(docsSpacer, Priority.ALWAYS);

        Button btnUpload = new Button("⇪");
        btnUpload.getStyleClass().add("icon-button-subtle");
        Button btnAddDoc = new Button("+");
        btnAddDoc.getStyleClass().add("icon-button-subtle");

        docsHeader.getChildren().addAll(docsTitle, docsSpacer, btnUpload, btnAddDoc);

        HBox docsGrid = new HBox(12);
        docsGrid.getChildren().addAll(
                createDocCard("Blood_Work_Oct_23....", "Lab Report • 2.4 MB", "#DC2626"),
                createDocCard("Hip_X-Ray_L_Side.jpg", "Radiology • 5.1 MB", "#1D4ED8")
        );

        docsCard.getChildren().addAll(docsHeader, docsGrid);

        // Consultation Notes
        VBox notesCard = new VBox(12);
        notesCard.getStyleClass().add("card");
        notesCard.setPadding(new Insets(16));

        HBox notesHeader = new HBox();
        Label notesTitle = new Label("Consultation Notes");
        notesTitle.getStyleClass().add("card-title");

        Region notesSpacer = new Region();
        HBox.setHgrow(notesSpacer, Priority.ALWAYS);

        Hyperlink newNoteLink = new Hyperlink("New Note");
        newNoteLink.getStyleClass().add("table-link");

        notesHeader.getChildren().addAll(notesTitle, notesSpacer, newNoteLink);

        VBox notesList = new VBox(10);
        notesList.getChildren().addAll(
                createNoteCard("Dr. Sarah Mills (PT)", "3 days ago", "Patient is progressing well with mobility exercises. Range of motion in left hip increased by 15 degrees. Advised to continue home exercise plan twice daily."),
                createNoteCard("Dr. Julian Thorne", "Oct 12", "Review of medications. Hypertension is well controlled with current dose. No changes needed. Monitor for any signs of swelling.")
        );

        notesCard.getChildren().addAll(notesHeader, notesList);

        column.getChildren().addAll(profileHeader, vitalsGrid, historyCard, docsCard, notesCard);
        return column;
    }

    private VBox createVitalSparklineCard(String title, String val, String unit, String color, String svgIcon) {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(12));
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox top = new HBox();
        Label tLbl = new Label(title);
        tLbl.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #64748B;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        SVGPath icon = createSVGPath(svgIcon, color, 0.6);
        top.getChildren().addAll(tLbl, spacer, icon);

        HBox valBox = new HBox(4);
        valBox.setAlignment(Pos.BASELINE_LEFT);
        Label vLbl = new Label(val);
        vLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label uLbl = new Label(unit);
        uLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #94A3B8;");
        valBox.getChildren().addAll(vLbl, uLbl);

        // Vector wave polyline chart
        Polyline polyline = new Polyline();
        polyline.getPoints().addAll(new Double[]{
                0.0, 10.0, 15.0, 5.0, 30.0, 12.0, 45.0, 3.0, 60.0, 15.0, 75.0, 8.0, 90.0, 10.0
        });
        polyline.setStroke(Color.web(color));
        polyline.setStrokeWidth(2);

        card.getChildren().addAll(top, valBox, polyline);
        return card;
    }

    private VBox createVitalBarCard(String title, String val, String unit, String color) {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(12));
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox top = new HBox();
        Label tLbl = new Label(title);
        tLbl.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
        top.getChildren().add(tLbl);

        HBox valBox = new HBox(4);
        valBox.setAlignment(Pos.BASELINE_LEFT);
        Label vLbl = new Label(val);
        vLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label uLbl = new Label(unit);
        uLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #94A3B8;");
        valBox.getChildren().addAll(vLbl, uLbl);

        Region bar = new Region();
        bar.setPrefHeight(8);
        bar.setStyle("-fx-background-color: #E2E8F0; -fx-background-radius: 4px;");

        card.getChildren().addAll(top, valBox, bar);
        return card;
    }

    private VBox createVitalStatusCard(String title, String val, String unit, String statusText) {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(12));
        HBox.setHgrow(card, Priority.ALWAYS);

        Label tLbl = new Label(title);
        tLbl.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #64748B;");

        HBox valBox = new HBox(4);
        valBox.setAlignment(Pos.BASELINE_LEFT);
        Label vLbl = new Label(val);
        vLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label uLbl = new Label(unit);
        uLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #94A3B8;");
        valBox.getChildren().addAll(vLbl, uLbl);

        Label statusLbl = new Label(statusText);
        statusLbl.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #475569; -fx-font-size: 9px; -fx-padding: 3 6; -fx-background-radius: 4;");

        card.getChildren().addAll(tLbl, valBox, statusLbl);
        return card;
    }

    private HBox createHistoryTimelineItem(String title, String date, String desc, String dotColor) {
        HBox item = new HBox(12);
        item.setAlignment(Pos.TOP_LEFT);

        Circle dot = new Circle(5, Color.web(dotColor));
        VBox.setMargin(dot, new Insets(4, 0, 0, 0));

        VBox details = new VBox(4);
        HBox top = new HBox();
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label dateLbl = new Label(date);
        dateLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #94A3B8;");

        top.getChildren().addAll(titleLbl, spacer, dateLbl);

        Label descLbl = new Label(desc);
        descLbl.setWrapText(true);
        descLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #475569;");

        details.getChildren().addAll(top, descLbl);
        HBox.setHgrow(details, Priority.ALWAYS);

        item.getChildren().addAll(dot, details);
        return item;
    }

    private HBox createDocCard(String filename, String info, String iconColor) {
        HBox card = new HBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(card, Priority.ALWAYS);

        StackPane iconBox = new StackPane();
        iconBox.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 8; -fx-background-radius: 6;");
        SVGPath fileIcon = createSVGPath("M14 2H6c-1.1 0-1.99.9-1.99 2L4 20c0 1.1.89 2 1.99 2H18c1.1 0 2-.9 2-2V8l-6-6zm2 16H8v-2h8v2zm0-4H8v-2h8v2zm-3-5V3.5L18.5 9H13z", iconColor, 0.7);
        iconBox.getChildren().add(fileIcon);

        VBox text = new VBox(2);
        Label name = new Label(filename);
        name.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label sub = new Label(info);
        sub.setStyle("-fx-font-size: 9px; -fx-text-fill: #64748B;");

        text.getChildren().addAll(name, sub);
        card.getChildren().addAll(iconBox, text);
        return card;
    }

    private VBox createNoteCard(String author, String time, String content) {
        VBox card = new VBox(6);
        card.getStyleClass().add("note-box");
        card.setPadding(new Insets(12));

        HBox top = new HBox();
        Label authorLbl = new Label(author);
        authorLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label timeLbl = new Label(time);
        timeLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #94A3B8;");

        top.getChildren().addAll(authorLbl, spacer, timeLbl);

        Label text = new Label(content);
        text.setWrapText(true);
        text.setStyle("-fx-font-size: 11px; -fx-text-fill: #334155;");

        card.getChildren().addAll(top, text);
        return card;
    }

    // =========================================================================
    // 4. RIGHT SIDEBAR COLUMN (Health Passport, AI Insights, Clinical Profile)
    // =========================================================================
    private VBox createRightSidebarColumn() {
        VBox column = new VBox(16);

        // Health Passport Card (Blue Hero Box)
        VBox passportCard = new VBox(12);
        passportCard.getStyleClass().add("passport-card");
        passportCard.setPadding(new Insets(16));

        HBox passHeader = new HBox();
        Label passTitle = new Label("Health Passport");
        passTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");

        Region passSpacer = new Region();
        HBox.setHgrow(passSpacer, Priority.ALWAYS);

        SVGPath qrIcon = createSVGPath("M3 11h8V3H3v8zm2-6h4v4H5V5zm8-2v8h8V3h-8zm6 6h-4V5h4v4zM3 21h8v-8H3v8zm2-6h4v4H5v-4zm13-2h-2v2h2v-2zm1 2h2v2h-2v-2zm-3 2h2v2h-2v-2zm1 2h2v2h-2v-2z", "#FFFFFF", 0.6);
        passHeader.getChildren().addAll(passTitle, passSpacer, qrIcon);

        HBox statusBox = new HBox(12);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        Circle statusCircle = new Circle(16, Color.web("#3B82F6"));

        VBox statusDetails = new VBox(2);
        Label statusSub = new Label("CURRENT STATUS");
        statusSub.setStyle("-fx-font-size: 9px; -fx-text-fill: #93C5FD;");
        Label statusVal = new Label("Stable");
        statusVal.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");

        statusDetails.getChildren().addAll(statusSub, statusVal);
        statusBox.getChildren().addAll(statusCircle, statusDetails);

        HBox metaBox1 = new HBox();
        Label checkupLbl = new Label("Last Checkup");
        checkupLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #BFDBFE;");
        Region mSpacer1 = new Region();
        HBox.setHgrow(mSpacer1, Priority.ALWAYS);
        Label checkupVal = new Label("3 days ago");
        checkupVal.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");
        metaBox1.getChildren().addAll(checkupLbl, mSpacer1, checkupVal);

        HBox metaBox2 = new HBox();
        Label riskLbl = new Label("Risk Level");
        riskLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #BFDBFE;");
        Region mSpacer2 = new Region();
        HBox.setHgrow(mSpacer2, Priority.ALWAYS);
        Label riskVal = new Label("Low");
        riskVal.setStyle("-fx-background-color: #059669; -fx-text-fill: #FFFFFF; -fx-font-size: 9px; -fx-font-weight: bold; -fx-padding: 2 6; -fx-background-radius: 4;");
        metaBox2.getChildren().addAll(riskLbl, mSpacer2, riskVal);

        passportCard.getChildren().addAll(passHeader, statusBox, metaBox1, metaBox2);

        // AI Insights Card
        VBox aiCard = new VBox(12);
        aiCard.getStyleClass().add("card");
        aiCard.setPadding(new Insets(16));

        HBox aiHeader = new HBox(6);
        aiHeader.setAlignment(Pos.CENTER_LEFT);
        SVGPath aiSpark = createSVGPath("M12 2L14.5 9.5L22 12L14.5 14.5L12 22L9.5 14.5L2 12L9.5 9.5L12 2Z", "#1D4ED8", 0.6);
        Label aiTitle = new Label("AI Insights");
        aiTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");
        aiHeader.getChildren().addAll(aiSpark, aiTitle);

        VBox alertBox1 = new VBox(4);
        Label alertTitle = new Label("⚠ Risk: Bone Density");
        alertTitle.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #991B1B;");
        Label alertDesc = new Label("Age and previous fracture history suggest a DXA scan update is due in Q4.");
        alertDesc.setWrapText(true);
        alertDesc.setStyle("-fx-font-size: 10px; -fx-text-fill: #475569;");
        alertBox1.getChildren().addAll(alertTitle, alertDesc);

        VBox alertBox2 = new VBox(4);
        Label tipTitle = new Label("💡 Lifestyle Tip");
        tipTitle.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #065F46;");
        Label tipDesc = new Label("Recommended 15min low-impact walking daily to improve post-op circulation.");
        tipDesc.setWrapText(true);
        tipDesc.setStyle("-fx-font-size: 10px; -fx-text-fill: #475569;");
        alertBox2.getChildren().addAll(tipTitle, tipDesc);

        Button btnRiskAnalysis = new Button("Full Risk Analysis");
        btnRiskAnalysis.getStyleClass().add("outline-button");
        btnRiskAnalysis.setMaxWidth(Double.MAX_VALUE);

        aiCard.getChildren().addAll(aiHeader, alertBox1, alertBox2, btnRiskAnalysis);

        // Clinical Profile Card
        VBox profileCard = new VBox(12);
        profileCard.getStyleClass().add("card");
        profileCard.setPadding(new Insets(16));

        Label profileTitle = new Label("Clinical Profile");
        profileTitle.getStyleClass().add("card-title");

        Label allergiesTitle = new Label("ALLERGIES");
        allergiesTitle.getStyleClass().add("section-subtitle");

        HBox allergiesTags = new HBox(6);
        Label alg1 = new Label("Penicillin");
        alg1.getStyleClass().add("tag-danger");

        Label alg2 = new Label("Dust Mites");
        alg2.getStyleClass().add("tag-gray");

        Button btnAddAlg = new Button("+");
        btnAddAlg.getStyleClass().add("icon-button-circle");

        allergiesTags.getChildren().addAll(alg1, alg2, btnAddAlg);

        Label conditionsTitle = new Label("CHRONIC CONDITIONS");
        conditionsTitle.getStyleClass().add("section-subtitle");

        VBox conditionsList = new VBox(6);
        conditionsList.getChildren().addAll(
                createConditionItem("Type 2 Diabetes"),
                createConditionItem("Hypertension"),
                createConditionItem("Osteoarthritis")
        );

        profileCard.getChildren().addAll(profileTitle, allergiesTitle, allergiesTags, conditionsTitle, conditionsList);

        // Emergency Contact Card
        VBox emergencyCard = new VBox(10);
        emergencyCard.getStyleClass().add("card");
        emergencyCard.setPadding(new Insets(14));

        Label emergencyTitle = new Label("Emergency Contact");
        emergencyTitle.getStyleClass().add("card-title");

        HBox contactBox = new HBox(10);
        contactBox.setAlignment(Pos.CENTER_LEFT);

        StackPane contactIcon = new StackPane();
        contactIcon.setStyle("-fx-background-color: #F1F5F9; -fx-padding: 8; -fx-background-radius: 6;");
        SVGPath phoneIcon = createSVGPath("M6.62 10.79c1.44 2.83 3.76 5.14 6.59 6.59l2.2-2.2c.27-.27.67-.36 1.02-.24 1.12.37 2.33.57 3.57.57.55 0 1 .45 1 1V20c0 .55-.45 1-1 1-9.39 0-17-7.61-17-17 0-.55.45-1 1-1h3.5c.55 0 1 .45 1 1 0 1.25.2 2.45.57 3.57.11.35.03.74-.25 1.02l-2.2 2.2z", "#1D4ED8", 0.7);
        contactIcon.getChildren().add(phoneIcon);

        VBox contactDetails = new VBox(2);
        Label contactName = new Label("Mark Jenkins (Son)");
        contactName.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Label contactPhone = new Label("+1 (555) 902-3341");
        contactPhone.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748B;");

        contactDetails.getChildren().addAll(contactName, contactPhone);
        contactBox.getChildren().addAll(contactIcon, contactDetails);

        emergencyCard.getChildren().addAll(emergencyTitle, contactBox);

        column.getChildren().addAll(passportCard, aiCard, profileCard, emergencyCard);
        return column;
    }

    private HBox createConditionItem(String condition) {
        HBox item = new HBox(8);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 8 10; -fx-background-radius: 6;");

        Circle dot = new Circle(3, Color.web("#0D9488"));
        Label label = new Label(condition);
        label.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #334155;");

        item.getChildren().addAll(dot, label);
        return item;
    }
}