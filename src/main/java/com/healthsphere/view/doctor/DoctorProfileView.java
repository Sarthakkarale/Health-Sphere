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

public class DoctorProfileView {

    private final Stage stage;

    public DoctorProfileView(Stage stage) {
        this.stage = stage;
    }

    public Scene createScene() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("main-container");

        // 1. Sidebar
        mainLayout.setLeft(createSidebar());

        // 2. Main Workspace
        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(20, 24, 20, 24));
        contentBox.getStyleClass().add("content-area");

        HBox headerBar = createHeaderBar();

        HBox splitLayout = new HBox(20);
        HBox.setHgrow(splitLayout, Priority.ALWAYS);

        VBox leftColumn = createMainProfileColumn();
        VBox rightColumn = createSideInfoColumn();

        HBox.setHgrow(leftColumn, Priority.ALWAYS);
        rightColumn.setPrefWidth(320);

        splitLayout.getChildren().addAll(leftColumn, rightColumn);
        contentBox.getChildren().addAll(headerBar, splitLayout);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("custom-scroll-pane");

        mainLayout.setCenter(scrollPane);

        Scene scene = new Scene(mainLayout, stage.getWidth(), stage.getHeight());

        try {
            scene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/css/doctor_profile.css")).toExternalForm());
        } catch (Exception e) {
            System.err.println("CSS file /css/doctor_profile.css not found.");
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

        VBox mainNavBox = new VBox(6);
        mainNavBox.setPadding(new Insets(28, 0, 0, 0));

        Button btnDashboard = createNavButton("Dashboard", false, "M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z");
        Button btnQueue = createNavButton("Patient Queue", false, "M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5z");
        Button btnReports = createNavButton("Medical Reports", false, "M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z");
        Button btnAiAssistant = createNavButton("AI Assistant", false, "M12 2L14.5 9.5L22 12L14.5 14.5L12 22L9.5 14.5L2 12L9.5 9.5L12 2Z");

        btnDashboard.setOnAction(e -> stage.setScene(new DoctorDashboardView(stage).createScene()));
        btnQueue.setOnAction(e -> stage.setScene(new PatientQueueView(stage).createScene()));
        btnReports.setOnAction(e -> stage.setScene(new PrescriptionManagementView(stage).createScene()));
        btnAiAssistant.setOnAction(e -> stage.setScene(new AiAssistantView(stage).createScene()));

        mainNavBox.getChildren().addAll(btnDashboard, btnQueue, btnReports, btnAiAssistant);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Bottom User Card & Navigation Options
        HBox profileCard = new HBox(10);
        profileCard.getStyleClass().add("profile-card");
        profileCard.setAlignment(Pos.CENTER_LEFT);

        ImageView avatar = createImageView("/images/doctor/portrait-3d-male-doctor.png", 32, 32);
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
        Label userDept = new Label("Oncology Dept");
        userDept.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748B;");
        userDetails.getChildren().addAll(userName, userDept);

        profileCard.getChildren().addAll(avatarNode, userDetails);

        VBox bottomNav = new VBox(4);
        bottomNav.setPadding(new Insets(12, 0, 0, 0));
        Button btnSettings = createNavButton("Settings", false, "M19.43 12.98c.04-.32.07-.64.07-.98s-.03-.66-.07-.98l2.11-1.65c.19-.15.24-.42.12-.64l-2-3.46c-.12-.22-.39-.3-.61-.22l-2.49 1c-.52-.4-1.08-.73-1.69-.98l-.38-2.65C14.46 2.18 14.25 2 14 2h-4c-.25 0-.46.18-.49.42l-.38 2.65c-.61.25-1.17.59-1.69.98l-2.49-1c-.23-.09-.49 0-.61.22l-2 3.46c-.13.22-.07.49.12.64l2.11 1.65c-.04.32-.07.65-.07.98s.03.66.07.98l-2.11 1.65c-.19.15-.24.42-.12.64l2 3.46c.12.22.39.3.61.22l2.49-1c.52.4 1.08.73 1.69.98l.38 2.65c.03.24.24.42.49.42h4c.25 0 .46-.18.49-.42l.38-2.65c.61-.25 1.17-.59 1.69-.98l2.49 1c.23.09.49 0 .61-.22l2-3.46c.12-.22.07-.49-.12-.64l-2.11-1.65zM12 15.5c-1.93 0-3.5-1.57-3.5-3.5s1.57-3.5 3.5-3.5 3.5 1.57 3.5 3.5-1.57 3.5-3.5-3.5z");
        Button btnSupport = createNavButton("Support", false, "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 16h-2v-2h2v2zm1.07-7.75l-.9.92C12.45 11.9 12 12.5 12 14h-2v-.5c0-1.1.45-2.1 1.17-2.83l1.24-1.26c.37-.36.59-.86.59-1.41 0-1.1-.9-2-2-2s-2 .9-2 2H7c0-2.76 2.24-5 5-5s5 2.24 5 5c0 1.04-.42 1.99-1.07 2.75z");
        bottomNav.getChildren().addAll(btnSettings, btnSupport);

        sidebar.getChildren().addAll(brandLabel, mainNavBox, spacer, profileCard, bottomNav);
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
    // 2. HEADER BAR
    // =========================================================================
    private HBox createHeaderBar() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label pageTitle = new Label("Doctor Profile");
        pageTitle.getStyleClass().add("page-header-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actions = new HBox(12);
        actions.setAlignment(Pos.CENTER_RIGHT);

        HBox searchContainer = new HBox(8);
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.getStyleClass().add("search-container");
        searchContainer.setPadding(new Insets(0, 12, 0, 12));

        SVGPath searchIcon = createSVGPath("M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z", "#94A3B8", 0.7);
        TextField searchField = new TextField();
        searchField.setPromptText("Search patients or reports...");
        searchField.getStyleClass().add("search-field-inner");
        searchContainer.getChildren().addAll(searchIcon, searchField);

        Button btnBell = new Button();
        btnBell.getStyleClass().add("icon-button");
        btnBell.setGraphic(createSVGPath("M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.64-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z", "#475569", 0.7));

        Button btnUser = new Button();
        btnUser.getStyleClass().add("icon-button");
        btnUser.setGraphic(createSVGPath("M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 3c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3zm0 14.2c-2.5 0-4.71-1.28-6-3.22.03-1.99 4-3.08 6-3.08 1.99 0 5.97 1.09 6 3.08-1.29 1.94-3.5 3.22-6 3.22z", "#475569", 0.7));

        actions.getChildren().addAll(searchContainer, btnBell, btnUser);
        header.getChildren().addAll(pageTitle, spacer, actions);
        return header;
    }

    // =========================================================================
    // 3. MAIN LEFT COLUMN (Banner, Certificates, Reviews)
    // =========================================================================
    private VBox createMainProfileColumn() {
        VBox column = new VBox(20);

        // Doctor Header Banner Card
        HBox profileBannerCard = new HBox(20);
        profileBannerCard.getStyleClass().add("card");
        profileBannerCard.setPadding(new Insets(20));
        profileBannerCard.setAlignment(Pos.CENTER_LEFT);

        StackPane photoFrame = new StackPane();
        ImageView docPhoto = createImageView("/images/doctor/portrait-3d-male-doctor.png", 150, 180);
        if (docPhoto != null) {
            photoFrame.getChildren().add(docPhoto);
            Button btnEditPhoto = new Button();
            btnEditPhoto.getStyleClass().add("edit-photo-button");
            btnEditPhoto.setGraphic(createSVGPath("M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z", "#FFFFFF", 0.6));
            StackPane.setAlignment(btnEditPhoto, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(btnEditPhoto, new Insets(8));
            photoFrame.getChildren().add(btnEditPhoto);
        }

        VBox detailsBox = new VBox(10);
        HBox.setHgrow(detailsBox, Priority.ALWAYS);

        HBox nameRow = new HBox(12);
        nameRow.setAlignment(Pos.CENTER_LEFT);
        Label nameLbl = new Label("Dr. Jonathan Smith");
        nameLbl.getStyleClass().add("doctor-name-title");

        Label verifiedTag = new Label("VERIFIED");
        verifiedTag.getStyleClass().add("tag-verified");

        Region flexSpacer = new Region();
        HBox.setHgrow(flexSpacer, Priority.ALWAYS);

        nameRow.getChildren().addAll(nameLbl, flexSpacer, verifiedTag);

        Label subTitle = new Label("MD, FACS • Chief Surgeon, Cardiology");
        subTitle.getStyleClass().add("doctor-subtitle");

        Separator sep1 = new Separator();

        GridPane metaGrid = new GridPane();
        metaGrid.setHgap(24);
        metaGrid.setVgap(8);

        metaGrid.add(createMetaItem("M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-1.99.89-1.99 2L2 19c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6 0h-4V4h4v2z", "Experience", "15+ Years"), 0, 0);
        metaGrid.add(createMetaItem("M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm-2 16l-4-4 1.41-1.41L10 14.17l6.59-6.59L18 9l-8 8z", "Registration", "MC-90210-MD"), 1, 0);
        metaGrid.add(createMetaItem("M12.87 15.07l-2.54-2.51.03-.03c1.74-1.94 2.98-4.17 3.71-6.53H17V4h-7V2H8v2H1v2h11.17C11.5 7.92 10.44 9.75 9 11.35 8.07 10.32 7.3 9.19 6.69 8h-2c.73 1.63 1.73 3.17 2.98 4.56l-5.09 5.02L4 19l5-5 3.11 3.11.76-2.04z", "Languages", "English, Spanish, German"), 0, 1);

        Separator sep2 = new Separator();

        HBox btnRow = new HBox(12);
        Button btnEdit = new Button("✏  Edit Profile");
        btnEdit.getStyleClass().add("primary-button");

        Button btnCert = new Button("📄  Upload Certificates");
        btnCert.getStyleClass().add("outline-button");

        btnRow.getChildren().addAll(btnEdit, btnCert);

        detailsBox.getChildren().addAll(nameRow, subTitle, sep1, metaGrid, sep2, btnRow);
        profileBannerCard.getChildren().addAll(photoFrame, detailsBox);

        // Certificates & Awards Section
        VBox certsSection = new VBox(12);
        HBox certsHeader = new HBox();
        Label certsTitle = new Label("Certificates & Awards");
        certsTitle.getStyleClass().add("card-title");

        Region certSpacer = new Region();
        HBox.setHgrow(certSpacer, Priority.ALWAYS);

        Hyperlink viewAllLink = new Hyperlink("View All");
        viewAllLink.getStyleClass().add("table-link");

        certsHeader.getChildren().addAll(certsTitle, certSpacer, viewAllLink);

        HBox certsGrid = new HBox(12);
        certsGrid.getChildren().addAll(
                createCertCard("Excellence in Surgery", "Global Medical Forum 2022", "M19 5h-2V3c0-.55-.45-1-1-1H8c-.55 0-1 .45-1 1v2H5c-1.1 0-2 .9-2 2v1c0 2.55 1.92 4.63 4.39 4.94.63 1.5 1.98 2.63 3.61 2.96V19H7v2h10v-2h-4v-3.1c1.63-.33 2.98-1.46 3.61-2.96C19.08 12.63 21 10.55 21 8V7c0-1.1-.9-2-2-2z"),
                createCertCard("Board Certification", "American Board of Cardiology", "M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm-2 16l-4-4 1.41-1.41L10 14.17l6.59-6.59L18 9l-8 8z"),
                createCertCard("Top Specialist 2023", "Healthcare Excellence Awards", "M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 3c1.93 0 3.5 1.57 3.5 3.5S13.93 13 12 13s-3.5-1.57-3.5-3.5S10.07 6 12 6z"),
                createCertCard("Advanced Robotics", "Fellowship • Stanford Med", "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z")
        );

        certsSection.getChildren().addAll(certsHeader, certsGrid);

        // Patient Reviews & Ratings Section
        VBox reviewsSection = new VBox(12);
        HBox reviewsHeader = new HBox(8);
        reviewsHeader.setAlignment(Pos.BASELINE_LEFT);

        Label reviewsTitle = new Label("Patient Reviews & Ratings");
        reviewsTitle.getStyleClass().add("card-title");

        Region revSpacer = new Region();
        HBox.setHgrow(revSpacer, Priority.ALWAYS);

        Label scoreAvg = new Label("4.9");
        scoreAvg.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Label stars = new Label("⭐⭐⭐⭐⭐");
        stars.setStyle("-fx-font-size: 11px;");

        Label totalRev = new Label("(1,240 reviews)");
        totalRev.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");

        reviewsHeader.getChildren().addAll(reviewsTitle, revSpacer, scoreAvg, stars, totalRev);

        VBox reviewsList = new VBox(12);
        reviewsList.getChildren().addAll(
                createReviewCard("JD", "Jane Doe", "Post-Op Cardiology Patient • 2 days ago", "Dr. Smith was incredibly thorough during my consultation. He took the time to explain the surgical procedure in detail, which really put my mind at ease. The follow-up care has been exceptional."),
                createReviewCard("MB", "Michael Brown", "Routine Checkup • 1 week ago", "Professional, punctual, and very knowledgeable. The clinic staff were also very helpful. Highly recommended for anyone needing cardiac specialty care.")
        );

        reviewsSection.getChildren().addAll(reviewsHeader, reviewsList);

        column.getChildren().addAll(profileBannerCard, certsSection, reviewsSection);
        return column;
    }

    private HBox createMetaItem(String iconSvg, String title, String val) {
        HBox item = new HBox(8);
        item.setAlignment(Pos.CENTER_LEFT);

        SVGPath icon = createSVGPath(iconSvg, "#1D4ED8", 0.65);
        VBox text = new VBox(2);
        Label tLbl = new Label(title);
        tLbl.setStyle("-fx-font-size: 9px; -fx-text-fill: #64748B;");
        Label vLbl = new Label(val);
        vLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        text.getChildren().addAll(tLbl, vLbl);
        item.getChildren().addAll(icon, text);
        return item;
    }

    private VBox createCertCard(String title, String subtitle, String iconSvg) {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(14));
        HBox.setHgrow(card, Priority.ALWAYS);

        SVGPath icon = createSVGPath(iconSvg, "#1D4ED8", 0.85);

        Label tLbl = new Label(title);
        tLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        tLbl.setWrapText(true);

        Label sLbl = new Label(subtitle);
        sLbl.setStyle("-fx-font-size: 9px; -fx-text-fill: #64748B;");
        sLbl.setWrapText(true);

        card.getChildren().addAll(icon, tLbl, sLbl);
        return card;
    }

    private VBox createReviewCard(String init, String name, String meta, String comment) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(14));

        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER_LEFT);

        Circle initBg = new Circle(16, Color.web("#EFF6FF"));
        Label initLbl = new Label(init);
        initLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #1D4ED8;");
        StackPane avatar = new StackPane(initBg, initLbl);

        VBox user = new VBox(2);
        Label nameLbl = new Label(name);
        nameLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label metaLbl = new Label(meta);
        metaLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748B;");
        user.getChildren().addAll(nameLbl, metaLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label stars = new Label("⭐⭐⭐⭐⭐");
        stars.setStyle("-fx-font-size: 10px;");

        top.getChildren().addAll(avatar, user, spacer, stars);

        Label commentLbl = new Label(comment);
        commentLbl.setWrapText(true);
        commentLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #334155;");

        card.getChildren().addAll(top, commentLbl);
        return card;
    }

    // =========================================================================
    // 4. RIGHT SIDEBAR COLUMN (Affiliation, Fee Structure, AI Insights)
    // =========================================================================
    private VBox createSideInfoColumn() {
        VBox column = new VBox(16);

        // Primary Affiliation Card
        VBox affCard = new VBox(10);
        affCard.getStyleClass().add("card");
        affCard.setPadding(new Insets(16));

        Label affTitle = new Label("Primary Affiliation");
        affTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #475569;");

        HBox affDetails = new HBox(10);
        affDetails.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBg = new StackPane();
        iconBg.setStyle("-fx-background-color: #EFF6FF; -fx-padding: 8; -fx-background-radius: 6;");
        SVGPath hospitalIcon = createSVGPath("M19 3H5c-1.1 0-1.99.9-1.99 2L3 19c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-2 10h-4v4h-2v-4H7v-2h4V7h2v4h4v2z", "#1D4ED8", 0.75);
        iconBg.getChildren().add(hospitalIcon);

        VBox text = new VBox(2);
        Label centerName = new Label("St. Jude Medical Center");
        centerName.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label loc = new Label("Palo Alto, California");
        loc.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748B;");
        text.getChildren().addAll(centerName, loc);

        affDetails.getChildren().addAll(iconBg, text);
        affCard.getChildren().addAll(affTitle, affDetails);

        // Consultation Fee Card (Blue Hero Box)
        VBox feeCard = new VBox(10);
        feeCard.getStyleClass().add("fee-card");
        feeCard.setPadding(new Insets(16));

        Label feeTitle = new Label("Consultation Fee");
        feeTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #BFDBFE;");

        HBox feeBox = new HBox(4);
        feeBox.setAlignment(Pos.BASELINE_LEFT);
        Label feeAmount = new Label("$250.00");
        feeAmount.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");

        Region feeSpacer = new Region();
        HBox.setHgrow(feeSpacer, Priority.ALWAYS);

        SVGPath cashIcon = createSVGPath("M21 18v1c0 1.1-.9 2-2 2H5c-1.11 0-2-.9-2-2V5c0-1.1.89-2 2-2h14c1.1 0 2 .9 2 2v1h-9c-1.11 0-2 .9-2 2v8c0 1.1.89 2 2 2h9zm-9-2h10V8H12v8zm4-2.5c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5z", "#93C5FD", 0.8);
        feeBox.getChildren().addAll(feeAmount, feeSpacer, cashIcon);

        Label durationLbl = new Label("Standard 45-minute consultation");
        durationLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #BFDBFE;");

        Button btnFeeStructure = new Button("View Fee Structure");
        btnFeeStructure.getStyleClass().add("fee-button");
        btnFeeStructure.setMaxWidth(Double.MAX_VALUE);

        feeCard.getChildren().addAll(feeTitle, feeBox, durationLbl, btnFeeStructure);

        // MediNexus AI Insights
        VBox aiCard = new VBox(12);
        aiCard.getStyleClass().add("card");
        aiCard.setPadding(new Insets(16));

        HBox aiHeader = new HBox(6);
        SVGPath spark = createSVGPath("M12 2L14.5 9.5L22 12L14.5 14.5L12 22L9.5 14.5L2 12L9.5 9.5L12 2Z", "#1D4ED8", 0.6);
        Label aiTitle = new Label("MediNexus AI Insights");
        aiTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");
        aiHeader.getChildren().addAll(spark, aiTitle);

        VBox metric1 = new VBox(4);
        HBox m1Header = new HBox();
        Label m1Title = new Label("RESPONSE SCORE");
        m1Title.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #1D4ED8;");
        Region m1Spacer = new Region();
        HBox.setHgrow(m1Spacer, Priority.ALWAYS);
        Label m1Val = new Label("98%");
        m1Val.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        m1Header.getChildren().addAll(m1Title, m1Spacer, m1Val);

        Region progressBar = new Region();
        progressBar.setPrefHeight(6);
        progressBar.setStyle("-fx-background-color: #1D4ED8; -fx-background-radius: 3px;");

        Label m1Sub = new Label("Excellent patient response rate in 24h.");
        m1Sub.setStyle("-fx-font-size: 9px; -fx-text-fill: #64748B;");
        metric1.getChildren().addAll(m1Header, progressBar, m1Sub);

        VBox metric2 = new VBox(4);
        Label m2Title = new Label("SATISFACTION INDEX");
        m2Title.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #1D4ED8;");
        Label m2Val = new Label("Highly Positive");
        m2Val.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label m2Sub = new Label("Sentiment analysis shows \"trust\" and \"clarity\" as top keywords.");
        m2Sub.setWrapText(true);
        m2Sub.setStyle("-fx-font-size: 9px; -fx-text-fill: #64748B;");
        metric2.getChildren().addAll(m2Title, m2Val, m2Sub);

        aiCard.getChildren().addAll(aiHeader, metric1, metric2);

        column.getChildren().addAll(affCard, feeCard, aiCard);
        return column;
    }
}