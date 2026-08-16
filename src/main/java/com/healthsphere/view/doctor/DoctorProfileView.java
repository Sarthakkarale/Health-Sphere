package com.healthsphere.view.doctor;

import com.healthsphere.util.Navigation;
import com.healthsphere.util.ResourceImage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * DoctorProfileView presents the complete Doctor Profile screen for the Health-Sphere application.
 */
public class DoctorProfileView {

    private final Stage stage;
    private final Scene scene;

    public DoctorProfileView(Stage stage) {
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
        contentArea.setPadding(new Insets(20, 30, 30, 30));
        contentArea.getStyleClass().add("content-area");

        // Top Navigation Header
        HBox topHeader = createTopHeader();
        contentArea.getChildren().add(topHeader);

        // Doctor Header Banner Card
        HBox profileBanner = createProfileBannerCard();
        contentArea.getChildren().add(profileBanner);

        // Main Two-Column Layout (Left Main Content & Right Info Cards)
        HBox bodyLayout = createBodyLayout();
        contentArea.getChildren().add(bodyLayout);

        // ScrollPane Container
        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("content-scrollpane");
        mainRoot.setCenter(scrollPane);

        Scene profileScene = new Scene(mainRoot, stage.getWidth(), stage.getHeight());
        profileScene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/css/doctor_profile.css")).toExternalForm());

        return profileScene;
    }

    /** Creates Sidebar Navigation with active state on Doctor Profile tab */
    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPadding(new Insets(25, 15, 25, 15));
        sidebar.getStyleClass().add("sidebar");
        sidebar.setMinWidth(240);

        // Logo Section
        HBox logoSection = new HBox(10);
        logoSection.setPadding(new Insets(0, 0, 25, 0));
        logoSection.setAlignment(Pos.CENTER_LEFT);

        StackPane logoIconBox = new StackPane();
        logoIconBox.getStyleClass().add("logo-icon-box");
        ImageView logoIcon = new ImageView(ResourceImage.load("/images/icons/ic_shield.png"));
        logoIcon.setFitWidth(18); logoIcon.setFitHeight(18);
        logoIconBox.getChildren().add(logoIcon);

        VBox logoText = new VBox(0);
        Label appName = new Label("Health-Sphere");
        appName.getStyleClass().add("logo-name");
        Label doctorSubtext = new Label("Doctor Module");
        doctorSubtext.getStyleClass().add("logo-subtext");
        logoText.getChildren().addAll(appName, doctorSubtext);
        logoSection.getChildren().addAll(logoIconBox, logoText);

        // Navigation Tabs
        VBox navItems = new VBox(6);
        String[] tabs = {
            "Dashboard", "Today's Schedule", "Appointments", "Patient Details",
            "Medical Reports & Prescription", "Availability & Schedule", "Doctor Profile", "AI Health Assistant"
        };
        String[] icons = {
            "ic_dashboard", "ic_schedule", "ic_appointments", "ic_patient",
            "ic_reports", "ic_availability", "ic_profile", "ic_ai"
        };

        for (int i = 0; i < tabs.length; i++) {
            HBox navTab = new HBox(12);
            navTab.getStyleClass().add("nav-tab");

            if (i == 6) { // Active Highlight: Doctor Profile
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
            case 0:
                Navigation.goTo(stage, () -> new DoctorDashboardView(stage).getScene());
                break;
            case 1:
                Navigation.goTo(stage, () -> new TodaysScheduleView(stage).getScene());
                break;
            case 2:
                Navigation.goTo(stage, () -> new AppointmentsView(stage).getScene());
                break;
            case 3:
                Navigation.goTo(stage, () -> new PatientDetailsView(stage).getScene());
                break;
            case 4:
                Navigation.goTo(stage, () -> new MedicalReportsView(stage).getScene());
                break;
            case 5:
                Navigation.goTo(stage, () -> new AvailabilityScheduleView(stage).getScene());
                break;
            case 6:
                Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene());
                break;
            case 7:
                Navigation.goTo(stage, () -> new AIHealthAssistantView(stage).getScene());
                break;
            default:
                break;
        }
    }

    /** Top Search and User Profile Header */
    private HBox createTopHeader() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);

        HBox searchField = new HBox(10);
        searchField.getStyleClass().add("search-input-box");
        searchField.setAlignment(Pos.CENTER_LEFT);

        ImageView searchIcon = new ImageView(ResourceImage.load("/images/icons/ic_search.png"));
        searchIcon.setFitWidth(16); searchIcon.setFitHeight(16);

        TextField searchInput = new TextField();
        searchInput.setPromptText("Search...");
        searchInput.getStyleClass().add("search-text-field");
        searchField.getChildren().addAll(searchIcon, searchInput);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox rightIcons = new HBox(18);
        rightIcons.setAlignment(Pos.CENTER_RIGHT);

        ImageView bellIcon = new ImageView(ResourceImage.load("/images/icons/ic_bell.png"));
        bellIcon.setFitWidth(18); bellIcon.setFitHeight(18);
        bellIcon.getStyleClass().add("clickable-icon");

        ImageView settingsIcon = new ImageView(ResourceImage.load("/images/icons/ic_settings.png"));
        settingsIcon.setFitWidth(18); settingsIcon.setFitHeight(18);
        settingsIcon.getStyleClass().add("clickable-icon");

        Separator sep = new Separator(javafx.geometry.Orientation.VERTICAL);
        sep.setPrefHeight(20);

        ImageView topAvatar = new ImageView(ResourceImage.load("/images/mocks/dr_julian_avatar.png"));
        topAvatar.setFitWidth(36); topAvatar.setFitHeight(36);
        Circle clip = new Circle(18, 18, 18);
        topAvatar.setClip(clip);

        rightIcons.getChildren().addAll(bellIcon, settingsIcon, sep, topAvatar);

        topBar.getChildren().addAll(searchField, spacer, rightIcons);
        return topBar;
    }

    /** Profile Hero Banner Card */
    private HBox createProfileBannerCard() {
        HBox banner = new HBox(25);
        banner.getStyleClass().add("profile-banner-card");
        banner.setPadding(new Insets(24));
        banner.setAlignment(Pos.CENTER_LEFT);

        // Circular Doctor Image
        ImageView doctorImage = new ImageView(ResourceImage.load("/images/mocks/dr_julian_large.png"));
        doctorImage.setFitWidth(120);
        doctorImage.setFitHeight(120);
        Circle clip = new Circle(60, 60, 60);
        doctorImage.setClip(clip);

        // Main Information Box
        VBox infoBox = new VBox(6);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        Label docName = new Label("Dr. Julian");
        docName.getStyleClass().add("banner-doc-name");

        Label docTitle = new Label("Senior Cardiologist");
        docTitle.getStyleClass().add("banner-doc-title");

        HBox locationBox = new HBox(6);
        locationBox.setAlignment(Pos.CENTER_LEFT);
        ImageView locIcon = new ImageView(ResourceImage.load("/images/icons/ic_location.png"));
        locIcon.setFitWidth(14); locIcon.setFitHeight(14);
        Label locationText = new Label("St. Mary's Hospital, New York");
        locationText.getStyleClass().add("banner-subtext");
        locationBox.getChildren().addAll(locIcon, locationText);

        HBox ratingBox = new HBox(6);
        ratingBox.setAlignment(Pos.CENTER_LEFT);
        ImageView starIcon = new ImageView(ResourceImage.load("/images/icons/ic_star.png"));
        starIcon.setFitWidth(14); starIcon.setFitHeight(14);
        Label ratingText = new Label("4.9/5 ");
        ratingText.getStyleClass().add("banner-rating-bold");
        Label reviewsText = new Label("(120 Reviews)");
        reviewsText.getStyleClass().add("banner-subtext");
        ratingBox.getChildren().addAll(starIcon, ratingText, reviewsText);

        infoBox.getChildren().addAll(docName, docTitle, locationBox, ratingBox);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Top Action Buttons
        HBox actionBtns = new HBox(12);
        actionBtns.setAlignment(Pos.TOP_RIGHT);

        Button accountSettingsBtn = new Button("Account Settings");
        accountSettingsBtn.getStyleClass().add("btn-secondary-action");
        accountSettingsBtn.setOnAction(e -> System.out.println("Opening Account Settings..."));

        Button editProfileBtn = new Button("Edit Profile");
        editProfileBtn.getStyleClass().add("btn-primary-action");
        // Navigation to DoctorEditProfileView
        editProfileBtn.setOnAction(e -> Navigation.goTo(stage, () -> new DoctorEditProfileView(stage).getScene()));

        actionBtns.getChildren().addAll(accountSettingsBtn, editProfileBtn);

        banner.getChildren().addAll(doctorImage, infoBox, spacer, actionBtns);
        return banner;
    }

    /** Two-Column Main Content Body */
    private HBox createBodyLayout() {
        HBox body = new HBox(20);

        // Left Column (Personal Details, Qualification & Experience, Patient Reviews)
        VBox leftColumn = new VBox(20);
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        leftColumn.getChildren().add(createPersonalDetailsCard());
        leftColumn.getChildren().add(createQualificationCard());
        leftColumn.getChildren().add(createPatientReviewsCard());

        // Right Column (Consultation Details & Weekly Availability)
        VBox rightColumn = new VBox(20);
        rightColumn.setMinWidth(320);
        rightColumn.setMaxWidth(340);

        rightColumn.getChildren().add(createConsultationDetailsCard());
        rightColumn.getChildren().add(createWeeklyAvailabilityCard());

        body.getChildren().addAll(leftColumn, rightColumn);
        return body;
    }

    /** Personal Details Section Card */
    private VBox createPersonalDetailsCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("panel-card");
        card.setPadding(new Insets(20));

        HBox cardTitleBox = new HBox(8);
        cardTitleBox.setAlignment(Pos.CENTER_LEFT);
        ImageView personIcon = new ImageView(ResourceImage.load("/images/icons/ic_person.png"));
        personIcon.setFitWidth(18); personIcon.setFitHeight(18);
        Label title = new Label("Personal Details");
        title.getStyleClass().add("card-title");
        cardTitleBox.getChildren().addAll(personIcon, title);

        Separator sep = new Separator();

        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(40);
        detailsGrid.setVgap(16);

        // Row 1: Full Name & Email Address
        detailsGrid.add(createDetailItem("FULL NAME", "Dr. Julian Smith"), 0, 0);
        detailsGrid.add(createDetailItem("EMAIL ADDRESS", "dr.julian@healthsphere.com"), 1, 0);

        // Row 2: Phone Number & Languages Spoken
        detailsGrid.add(createDetailItem("PHONE NUMBER", "+1 (555) 123-4567"), 0, 1);
        detailsGrid.add(createDetailItem("LANGUAGES SPOKEN", "English, Spanish, French"), 1, 1);

        // Row 3: Bio Span across
        VBox bioBox = new VBox(4);
        Label bioLabel = new Label("BIO");
        bioLabel.getStyleClass().add("detail-field-label");
        Label bioText = new Label("Dedicated and compassionate Senior Cardiologist with over 15 years of clinical experience in diagnosing and treating cardiovascular diseases. Committed to providing comprehensive patient care and staying updated with the latest medical advancements.");
        bioText.getStyleClass().add("detail-field-value");
        bioText.setWrapText(true);
        bioBox.getChildren().addAll(bioLabel, bioText);

        detailsGrid.add(bioBox, 0, 2, 2, 1);

        card.getChildren().addAll(cardTitleBox, sep, detailsGrid);
        return card;
    }

    private VBox createDetailItem(String label, String value) {
        VBox box = new VBox(4);
        Label lbl = new Label(label);
        lbl.getStyleClass().add("detail-field-label");
        Label val = new Label(value);
        val.getStyleClass().add("detail-field-value");
        box.getChildren().addAll(lbl, val);
        return box;
    }

    /** Qualification & Experience Section Card */
    private VBox createQualificationCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("panel-card");
        card.setPadding(new Insets(20));

        HBox cardTitleBox = new HBox(8);
        cardTitleBox.setAlignment(Pos.CENTER_LEFT);
        ImageView gradIcon = new ImageView(ResourceImage.load("/images/icons/ic_academic.png"));
        gradIcon.setFitWidth(18); gradIcon.setFitHeight(18);
        Label title = new Label("Qualification & Experience");
        title.getStyleClass().add("card-title");
        cardTitleBox.getChildren().addAll(gradIcon, title);

        Separator sep = new Separator();

        VBox list = new VBox(16);

        // Item 1: Experience
        HBox expRow = new HBox(12);
        expRow.setAlignment(Pos.TOP_LEFT);

        StackPane expIconBox = new StackPane();
        expIconBox.getStyleClass().add("blue-icon-circle");
        ImageView expImg = new ImageView(ResourceImage.load("/images/icons/ic_briefcase.png"));
        expImg.setFitWidth(16); expImg.setFitHeight(16);
        expIconBox.getChildren().add(expImg);

        VBox expText = new VBox(4);
        Label expHeader = new Label("Experience");
        expHeader.getStyleClass().add("item-header-title");
        Label expDesc = new Label("15+ years at St. Mary's Hospital as Lead Cardiologist. Specialized in interventional cardiology and advanced heart failure management.");
        expDesc.getStyleClass().add("detail-field-value");
        expDesc.setWrapText(true);
        expText.getChildren().addAll(expHeader, expDesc);
        HBox.setHgrow(expText, Priority.ALWAYS);
        expRow.getChildren().addAll(expIconBox, expText);

        // Item 2: Specialization
        HBox specRow = new HBox(12);
        specRow.setAlignment(Pos.TOP_LEFT);

        StackPane specIconBox = new StackPane();
        specIconBox.getStyleClass().add("green-icon-circle");
        ImageView specImg = new ImageView(ResourceImage.load("/images/icons/ic_award.png"));
        specImg.setFitWidth(16); specImg.setFitHeight(16);
        specIconBox.getChildren().add(specImg);

        VBox specText = new VBox(4);
        Label specHeader = new Label("Specialization");
        specHeader.getStyleClass().add("item-header-title");
        Label specDesc = new Label("MD, PhD, Cardiology, Internal Medicine. Board certified in Cardiovascular Disease.");
        specDesc.getStyleClass().add("detail-field-value");
        specDesc.setWrapText(true);
        specText.getChildren().addAll(specHeader, specDesc);
        HBox.setHgrow(specText, Priority.ALWAYS);
        specRow.getChildren().addAll(specIconBox, specText);

        list.getChildren().addAll(expRow, specRow);

        card.getChildren().addAll(cardTitleBox, sep, list);
        return card;
    }

    /** Patient Reviews Section Card */
    private VBox createPatientReviewsCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("panel-card");
        card.setPadding(new Insets(20));

        BorderPane cardHeader = new BorderPane();
        HBox cardTitleBox = new HBox(8);
        cardTitleBox.setAlignment(Pos.CENTER_LEFT);
        ImageView starTitleIcon = new ImageView(ResourceImage.load("/images/icons/ic_star_blue.png"));
        starTitleIcon.setFitWidth(18); starTitleIcon.setFitHeight(18);
        Label title = new Label("Patient Reviews");
        title.getStyleClass().add("card-title");
        cardTitleBox.getChildren().addAll(starTitleIcon, title);

        Hyperlink viewAllLink = new Hyperlink("View All");
        viewAllLink.getStyleClass().add("card-link");
        viewAllLink.setOnAction(e -> System.out.println("Opening all reviews..."));

        cardHeader.setLeft(cardTitleBox);
        cardHeader.setRight(viewAllLink);

        HBox reviewsRow = new HBox(16);

        // Review 1
        VBox review1 = createReviewItem(
                "Sarah Jenkins",
                "2 weeks ago",
                5,
                "\"Dr. Julian is exceptional. He took the time to explain my condition thoroughly and made me feel completely at ease during my consultation.\""
        );

        // Review 2
        VBox review2 = createReviewItem(
                "Michael R.",
                "1 month ago",
                5,
                "\"Very professional and knowledgeable. The wait time was a bit long, but the care provided was top-notch.\""
        );

        HBox.setHgrow(review1, Priority.ALWAYS);
        HBox.setHgrow(review2, Priority.ALWAYS);

        reviewsRow.getChildren().addAll(review1, review2);

        card.getChildren().addAll(cardHeader, reviewsRow);
        return card;
    }

    private VBox createReviewItem(String author, String timeAgo, int stars, String comment) {
        VBox reviewCard = new VBox(10);
        reviewCard.getStyleClass().add("review-box");
        reviewCard.setPadding(new Insets(14));

        BorderPane topRow = new BorderPane();

        VBox authorInfo = new VBox(2);
        Label authorLabel = new Label(author);
        authorLabel.getStyleClass().add("review-author");
        Label timeLabel = new Label(timeAgo);
        timeLabel.getStyleClass().add("review-time");
        authorInfo.getChildren().addAll(authorLabel, timeLabel);

        HBox starRating = new HBox(2);
        starRating.setAlignment(Pos.CENTER_RIGHT);
        for (int i = 0; i < stars; i++) {
            ImageView star = new ImageView(ResourceImage.load("/images/icons/ic_star_green.png"));
            star.setFitWidth(12); star.setFitHeight(12);
            starRating.getChildren().add(star);
        }

        topRow.setLeft(authorInfo);
        topRow.setRight(starRating);

        Label commentLabel = new Label(comment);
        commentLabel.getStyleClass().add("review-comment");
        commentLabel.setWrapText(true);

        reviewCard.getChildren().addAll(topRow, commentLabel);
        return reviewCard;
    }

    /** Consultation Details Right-Column Blue Card */
    private VBox createConsultationDetailsCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("consultation-card");
        card.setPadding(new Insets(20));

        Label title = new Label("Consultation Details");
        title.getStyleClass().add("consultation-title");

        VBox list = new VBox(14);

        // Row 1: Standard Fee
        BorderPane feeRow = new BorderPane();
        HBox feeLeft = new HBox(10);
        feeLeft.setAlignment(Pos.CENTER_LEFT);
        ImageView feeIcon = new ImageView(ResourceImage.load("/images/icons/ic_card_white.png"));
        feeIcon.setFitWidth(18); feeIcon.setFitHeight(18);
        Label feeLabel = new Label("Standard Fee");
        feeLabel.getStyleClass().add("consultation-label");
        feeLeft.getChildren().addAll(feeIcon, feeLabel);

        Label feeValue = new Label("$150");
        feeValue.getStyleClass().add("consultation-value-bold");

        feeRow.setLeft(feeLeft);
        feeRow.setRight(feeValue);

        Separator sep1 = new Separator();
        sep1.getStyleClass().add("consultation-separator");

        // Row 2: Avg Duration
        BorderPane durRow = new BorderPane();
        HBox durLeft = new HBox(10);
        durLeft.setAlignment(Pos.CENTER_LEFT);
        ImageView clockIcon = new ImageView(ResourceImage.load("/images/icons/ic_clock_white.png"));
        clockIcon.setFitWidth(18); clockIcon.setFitHeight(18);
        Label durLabel = new Label("Avg. Duration");
        durLabel.getStyleClass().add("consultation-label");
        durLeft.getChildren().addAll(clockIcon, durLabel);

        Label durValue = new Label("30 mins");
        durValue.getStyleClass().add("consultation-value-medium");

        durRow.setLeft(durLeft);
        durRow.setRight(durValue);

        Separator sep2 = new Separator();
        sep2.getStyleClass().add("consultation-separator");

        // Row 3: Telehealth
        BorderPane teleRow = new BorderPane();
        HBox teleLeft = new HBox(10);
        teleLeft.setAlignment(Pos.CENTER_LEFT);
        ImageView camIcon = new ImageView(ResourceImage.load("/images/icons/ic_video_white.png"));
        camIcon.setFitWidth(18); camIcon.setFitHeight(18);
        Label teleLabel = new Label("Telehealth");
        teleLabel.getStyleClass().add("consultation-label");
        teleLeft.getChildren().addAll(camIcon, teleLabel);

        Label availableBadge = new Label("Available");
        availableBadge.getStyleClass().add("telehealth-badge");

        teleRow.setLeft(teleLeft);
        teleRow.setRight(availableBadge);

        list.getChildren().addAll(feeRow, sep1, durRow, sep2, teleRow);

        card.getChildren().addAll(title, list);
        return card;
    }

    /** Weekly Availability Right-Column White Card */
    private VBox createWeeklyAvailabilityCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("panel-card");
        card.setPadding(new Insets(20));

        HBox titleBox = new HBox(8);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        ImageView calIcon = new ImageView(ResourceImage.load("/images/icons/ic_calendar_blue.png"));
        calIcon.setFitWidth(18); calIcon.setFitHeight(18);
        Label title = new Label("Weekly Availability");
        title.getStyleClass().add("card-title");
        titleBox.getChildren().addAll(calIcon, title);

        VBox list = new VBox(10);

        list.getChildren().add(createAvailabilityRow("Monday - Wed", "09:00 AM - 05:00 PM", true));
        list.getChildren().add(createAvailabilityRow("Thursday", "10:00 AM - 06:00 PM", true));
        list.getChildren().add(createAvailabilityRow("Friday", "09:00 AM - 01:00 PM", true));
        list.getChildren().add(createAvailabilityRow("Weekend", "Unavailable", false));

        Button manageBtn = new Button("Manage Schedule");
        ImageView manageIcon = new ImageView(ResourceImage.load("/images/icons/ic_calendar_manage.png"));
        manageIcon.setFitWidth(14); manageIcon.setFitHeight(14);
        manageBtn.setGraphic(manageIcon);
        manageBtn.getStyleClass().add("btn-outline-full");
        manageBtn.setMaxWidth(Double.MAX_VALUE);
        manageBtn.setOnAction(e -> Navigation.goTo(stage, () -> new AvailabilityScheduleView(stage).getScene()));

        card.getChildren().addAll(titleBox, list, manageBtn);
        return card;
    }

    private BorderPane createAvailabilityRow(String day, String time, boolean isAvailable) {
        BorderPane row = new BorderPane();

        Label dayLabel = new Label(day);
        dayLabel.getStyleClass().add("avail-day-label");

        Label timeBadge = new Label(time);
        if (isAvailable) {
            timeBadge.getStyleClass().add("avail-time-badge");
        } else {
            timeBadge.getStyleClass().add("avail-unavailable-text");
        }

        row.setLeft(dayLabel);
        row.setRight(timeBadge);
        return row;
    }
}