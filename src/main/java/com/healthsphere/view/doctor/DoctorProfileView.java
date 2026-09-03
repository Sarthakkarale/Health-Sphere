package com.healthsphere.view.doctor;

import com.healthsphere.controller.doctor.DoctorController;
import com.healthsphere.model.DoctorProfile;
import com.healthsphere.util.Navigation;
import com.healthsphere.util.ResourceImage;

import com.healthsphere.model.UserProfile;
import com.healthsphere.util.SessionManager;
import com.healthsphere.view.authentication.LoginView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * DoctorProfileView presents the complete Doctor Profile screen for the
 * Health-Sphere application.
 *
 * Backend integration:
 * - Loads the currently authenticated doctor's profile from Firestore.
 * - Uses DoctorController to communicate with the backend.
 * - Keeps the original UI structure, CSS classes, navigation, images and layout
 * unchanged.
 */
public class DoctorProfileView {

        private final Stage stage;
        private final Scene scene;

        // ============================================================
        // BACKEND
        // ============================================================

        private final DoctorController doctorController;
        private DoctorProfile doctorProfile;

        public DoctorProfileView(Stage stage) {
                this.stage = stage;

                // Backend controller
                this.doctorController = new DoctorController();

                // Load current doctor's profile before creating the UI
                loadDoctorProfile();

                // Original UI creation
                this.scene = createScene();
        }

        public Scene getScene() {
                return this.scene;
        }

        // ============================================================
        // LOAD DOCTOR PROFILE
        // ============================================================

        /**
         * Loads the currently authenticated doctor's profile from Firestore.
         *
         * The UID is obtained internally by DoctorController through
         * SessionManager, so the View never supplies an arbitrary UID.
         */
        private void loadDoctorProfile() {

                try {

                        UserProfile currentUser = SessionManager.getInstance()
                                        .getCurrentUser();

                        if (currentUser == null) {

                                throw new IllegalStateException(
                                                "No logged-in user found.");
                        }

                        String doctorUid = currentUser.getUid();

                        if (doctorUid == null ||
                                        doctorUid.isBlank()) {

                                throw new IllegalStateException(
                                                "Logged-in doctor UID is missing.");
                        }

                        doctorProfile = doctorController.getDoctorProfile(
                                        doctorUid);

                } catch (Exception e) {

                        e.printStackTrace();

                        /*
                         * Keep the UI usable if profile loading fails.
                         */
                        doctorProfile = null;
                }
        }

        // ============================================================
        // SAFE DATA HELPERS
        // ============================================================

        private String getDoctorFullName() {

                if (doctorProfile == null) {
                        return "Doctor";
                }

                String firstName = doctorProfile.getFirstName();
                String lastName = doctorProfile.getLastName();

                firstName = firstName == null ? "" : firstName.trim();
                lastName = lastName == null ? "" : lastName.trim();

                String fullName = (firstName + " " + lastName).trim();

                if (fullName.isBlank()) {
                        return "Doctor";
                }

                return fullName;
        }

        private String getDoctorEmail() {

                if (doctorProfile == null ||
                                doctorProfile.getEmail() == null ||
                                doctorProfile.getEmail().isBlank()) {

                        return "Email not available";
                }

                return doctorProfile.getEmail();
        }

        private String getDoctorPhone() {

                if (doctorProfile == null ||
                                doctorProfile.getPhone() == null ||
                                doctorProfile.getPhone().isBlank()) {

                        return "Phone not available";
                }

                return doctorProfile.getPhone();
        }

        private String getDoctorSpecialization() {

                if (doctorProfile == null ||
                                doctorProfile.getSpecialization() == null ||
                                doctorProfile.getSpecialization().isBlank()) {

                        return "Specialization not available";
                }

                return doctorProfile.getSpecialization();
        }

        private String getDoctorHospital() {

                if (doctorProfile == null ||
                                doctorProfile.getHospitalAffiliation() == null ||
                                doctorProfile.getHospitalAffiliation().isBlank()) {

                        return "Hospital not available";
                }

                return doctorProfile.getHospitalAffiliation();
        }

        private String getDoctorExperience() {

                if (doctorProfile == null ||
                                doctorProfile.getExperience() == null ||
                                doctorProfile.getExperience().isBlank()) {

                        return "Experience not available";
                }

                return doctorProfile.getExperience();
        }

        // ============================================================
        // SCENE
        // ============================================================

        private Scene createScene() {

                BorderPane mainRoot = new BorderPane();
                mainRoot.getStyleClass().add("root-pane");

                // --- Sidebar (Left Navigation - Fixed Width & Colors) ---
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

                Scene profileScene = new Scene(
                                mainRoot,
                                stage.getWidth(),
                                stage.getHeight());

                profileScene.getStylesheets().add(
                                Objects.requireNonNull(
                                                getClass().getResource(
                                                                "/css/doctor_profile.css"))
                                                .toExternalForm());

                return profileScene;
        }

        // ============================================================
        // SIDEBAR
        // ============================================================

        /**
         * Creates Sidebar Navigation with exact Dashboard dark theme, active profile
         * highlight, and bottom profile/logout
         */
        private VBox createSidebar() {

                VBox sidebar = new VBox();

                sidebar.setPadding(
                                new Insets(25, 15, 25, 15));

                sidebar.getStyleClass().add("sidebar");

                sidebar.setStyle(
                                "-fx-background-color: #0F172A;");

                sidebar.setMinWidth(260);
                sidebar.setPrefWidth(260);
                sidebar.setMaxWidth(260);

                // Logo Section
                HBox logoSection = new HBox(12);

                logoSection.setPadding(
                                new Insets(0, 0, 25, 5));

                logoSection.setAlignment(Pos.CENTER_LEFT);

                StackPane logoIconBox = new StackPane();

                logoIconBox.getStyleClass().add(
                                "logo-icon-box");

                logoIconBox.setStyle(
                                "-fx-background-color: #3B82F6; " +
                                                "-fx-background-radius: 8px; " +
                                                "-fx-padding: 8px;");

                ImageView logoIcon = new ImageView(
                                ResourceImage.load(
                                                "/images/icons/ic_shield.png"));

                logoIcon.setFitWidth(20);
                logoIcon.setFitHeight(20);

                logoIconBox.getChildren().add(
                                logoIcon);

                VBox logoText = new VBox(2);

                Label appName = new Label("Health-Sphere");

                appName.getStyleClass().add(
                                "logo-name");

                appName.setStyle(
                                "-fx-text-fill: #FFFFFF; " +
                                                "-fx-font-weight: bold; " +
                                                "-fx-font-size: 16px;");

                Label doctorSubtext = new Label("Doctor Dashboard");

                doctorSubtext.getStyleClass().add(
                                "logo-subtext");

                doctorSubtext.setStyle(
                                "-fx-text-fill: #94A3B8; " +
                                                "-fx-font-size: 12px;");

                logoText.getChildren().addAll(
                                appName,
                                doctorSubtext);

                logoSection.getChildren().addAll(
                                logoIconBox,
                                logoText);

                // Navigation Tabs
                VBox navItems = new VBox(6);

                String[] tabs = {
                                "Dashboard",
                                "Today's Schedule",
                                "Appointments",
                                "Patient Details",
                                "Medical Reports & Prescription",
                                "Availability & Schedule",
                                "Doctor Profile",
                                "AI Health Assistant"
                };

                String[] icons = {
                                "ic_dashboard",
                                "ic_schedule",
                                "ic_appointments",
                                "ic_patient",
                                "ic_reports",
                                "ic_availability",
                                "ic_profile",
                                "ic_ai"
                };

                for (int i = 0; i < tabs.length; i++) {

                        HBox navTab = new HBox(12);

                        navTab.setAlignment(
                                        Pos.CENTER_LEFT);

                        navTab.setPadding(
                                        new Insets(10, 14, 10, 14));

                        navTab.getStyleClass().add(
                                        "nav-tab");

                        ImageView icon = new ImageView(
                                        ResourceImage.load(
                                                        "/images/icons/" +
                                                                        icons[i] +
                                                                        ".png"));

                        icon.setFitWidth(18);
                        icon.setFitHeight(18);

                        Label tabLabel = new Label(tabs[i]);

                        tabLabel.getStyleClass().add(
                                        "nav-text");

                        if (i == 6) {

                                navTab.getStyleClass().add(
                                                "nav-tab-active");

                                navTab.setStyle(
                                                "-fx-background-color: #3B82F6; " +
                                                                "-fx-background-radius: 8px;");

                                tabLabel.setStyle(
                                                "-fx-text-fill: #FFFFFF; " +
                                                                "-fx-font-weight: bold;");

                        } else {

                                navTab.setStyle(
                                                "-fx-background-color: transparent; " +
                                                                "-fx-background-radius: 8px;");

                                tabLabel.setStyle(
                                                "-fx-text-fill: #94A3B8;");
                        }

                        navTab.getChildren().addAll(
                                        icon,
                                        tabLabel);

                        navItems.getChildren().add(
                                        navTab);

                        final int index = i;

                        navTab.setOnMouseClicked(
                                        e -> handleSidebarTabClick(index));
                }

                // Spacer to push footer profile & logout to bottom
                Region spacer = new Region();

                VBox.setVgrow(
                                spacer,
                                Priority.ALWAYS);

                // Footer Section
                VBox footer = new VBox(10);

                footer.setPadding(
                                new Insets(15, 0, 0, 0));

                // Bottom Doctor Profile Box
                HBox sidebarProfile = new HBox(12);

                sidebarProfile.setAlignment(
                                Pos.CENTER_LEFT);

                sidebarProfile.setPadding(
                                new Insets(10, 12, 10, 12));

                sidebarProfile.getStyleClass().add(
                                "sidebar-profile-box");

                sidebarProfile.setStyle(
                                "-fx-background-color: #1E293B; " +
                                                "-fx-background-radius: 10px; " +
                                                "-fx-cursor: hand;");

                ImageView profileAvatar = new ImageView(
                                ResourceImage.load(
                                                "/images/mocks/dr_julian_avatar.png"));

                profileAvatar.setFitWidth(36);
                profileAvatar.setFitHeight(36);

                Circle profileClip = new Circle(
                                18,
                                18,
                                18);

                profileAvatar.setClip(
                                profileClip);

                VBox profileTexts = new VBox(2);

                Label profSubText = new Label("Doctor Profile");

                profSubText.setStyle(
                                "-fx-text-fill: #64748B; " +
                                                "-fx-font-size: 11px;");

                Label profName = new Label(
                                getDoctorFirstNameForSidebar());

                profName.getStyleClass().add(
                                "sidebar-profile-name");

                profName.setStyle(
                                "-fx-text-fill: #FFFFFF; " +
                                                "-fx-font-weight: bold; " +
                                                "-fx-font-size: 13px;");

                profileTexts.getChildren().addAll(
                                profSubText,
                                profName);

                sidebarProfile.getChildren().addAll(
                                profileAvatar,
                                profileTexts);

                sidebarProfile.setOnMouseClicked(
                                e -> Navigation.goTo(
                                                stage,
                                                () -> new DoctorProfileView(stage).getScene()));

                // Logout Tab
                HBox logoutTab = new HBox(12);

                logoutTab.setAlignment(
                                Pos.CENTER_LEFT);

                logoutTab.setPadding(
                                new Insets(10, 14, 10, 14));

                logoutTab.getStyleClass().add(
                                "nav-tab");

                logoutTab.setStyle(
                                "-fx-cursor: hand;");

                ImageView logoutIcon = new ImageView(
                                ResourceImage.load(
                                                "/images/icons/ic_logout.png"));

                logoutIcon.setFitWidth(18);
                logoutIcon.setFitHeight(18);

                Label logoutLabel = new Label("Logout");

                logoutLabel.getStyleClass().add(
                                "nav-text");

                logoutLabel.setStyle(
                                "-fx-text-fill: #94A3B8;");

                logoutTab.getChildren().addAll(
                                logoutIcon,
                                logoutLabel);

                logoutTab.setOnMouseClicked(
                                e -> handleLogout());

                footer.getChildren().addAll(
                                sidebarProfile,
                                logoutTab);

                sidebar.getChildren().addAll(
                                logoSection,
                                navItems,
                                spacer,
                                footer);

                return sidebar;
        }

        private String getDoctorFirstNameForSidebar() {
                return SessionManager.getDoctorDisplayName();
        }

        // ============================================================
        // SIDEBAR NAVIGATION
        // ============================================================

        private void handleSidebarTabClick(int index) {

                switch (index) {

                        case 0:
                                Navigation.goTo(
                                                stage,
                                                () -> new DoctorDashboardView(stage).getScene());
                                break;

                        case 1:
                                Navigation.goTo(
                                                stage,
                                                () -> new TodaysScheduleView(stage).getScene());
                                break;

                        case 2:
                                Navigation.goTo(
                                                stage,
                                                () -> new AppointmentsView(stage).getScene());
                                break;

                        case 3:
                                Navigation.goTo(
                                                stage,
                                                () -> new PatientDetailsView(stage).getScene());
                                break;

                        case 4:
                                Navigation.goTo(
                                                stage,
                                                () -> new MedicalReportsView(stage).getScene());
                                break;

                        case 5:
                                Navigation.goTo(
                                                stage,
                                                () -> new AvailabilityScheduleView(stage).getScene());
                                break;

                        case 6:
                                Navigation.goTo(
                                                stage,
                                                () -> new DoctorProfileView(stage).getScene());
                                break;

                        case 7:
                                Navigation.goTo(
                                                stage,
                                                () -> new AIHealthAssistantView(stage).getScene());
                                break;

                        default:
                                break;
                }
        }

        private void handleLogout() {
                try {
                        SessionManager.getInstance().clearSession();
                } catch (Exception e) {
                        e.printStackTrace();
                }
                Navigation.goTo(stage, () -> new LoginView(stage).getScene());
        }

        // ============================================================
        // TOP HEADER
        // ============================================================

        /** Top Search and User Profile Header */
        private HBox createTopHeader() {

                HBox topBar = new HBox();

                topBar.setAlignment(
                                Pos.CENTER_LEFT);

                HBox searchField = new HBox(10);

                searchField.getStyleClass().add(
                                "search-input-box");

                searchField.setAlignment(
                                Pos.CENTER_LEFT);

                ImageView searchIcon = new ImageView(
                                ResourceImage.load(
                                                "/images/icons/ic_search.png"));

                searchIcon.setFitWidth(16);
                searchIcon.setFitHeight(16);

                TextField searchInput = new TextField();

                searchInput.setPromptText(
                                "Search...");

                searchInput.getStyleClass().add(
                                "search-text-field");

                searchField.getChildren().addAll(
                                searchIcon,
                                searchInput);

                Region spacer = new Region();

                HBox.setHgrow(
                                spacer,
                                Priority.ALWAYS);

                HBox rightIcons = new HBox(18);

                rightIcons.setAlignment(
                                Pos.CENTER_RIGHT);

                ImageView bellIcon = new ImageView(
                                ResourceImage.load(
                                                "/images/icons/ic_bell.png"));

                bellIcon.setFitWidth(18);
                bellIcon.setFitHeight(18);

                bellIcon.getStyleClass().add(
                                "clickable-icon");

                ImageView settingsIcon = new ImageView(
                                ResourceImage.load(
                                                "/images/icons/ic_settings.png"));

                settingsIcon.setFitWidth(18);
                settingsIcon.setFitHeight(18);

                settingsIcon.getStyleClass().add(
                                "clickable-icon");

                Separator sep = new Separator(
                                javafx.geometry.Orientation.VERTICAL);

                sep.setPrefHeight(20);

                ImageView topAvatar = new ImageView(
                                ResourceImage.load(
                                                "/images/mocks/dr_julian_avatar.png"));

                topAvatar.setFitWidth(36);
                topAvatar.setFitHeight(36);

                Circle clip = new Circle(
                                18,
                                18,
                                18);

                topAvatar.setClip(clip);

                rightIcons.getChildren().addAll(
                                bellIcon,
                                settingsIcon,
                                sep,
                                topAvatar);

                topBar.getChildren().addAll(
                                searchField,
                                spacer,
                                rightIcons);

                return topBar;
        }

        // ============================================================
        // PROFILE BANNER
        // ============================================================

        /** Profile Hero Banner Card */
        private HBox createProfileBannerCard() {

                HBox banner = new HBox(25);

                banner.getStyleClass().add(
                                "profile-banner-card");

                banner.setPadding(
                                new Insets(24));

                banner.setAlignment(
                                Pos.CENTER_LEFT);

                // Circular Doctor Image
                ImageView doctorImage = new ImageView(
                                ResourceImage.load(
                                                "/images/mocks/dr_julian_large.png"));

                doctorImage.setFitWidth(120);
                doctorImage.setFitHeight(120);

                Circle clip = new Circle(
                                60,
                                60,
                                60);

                doctorImage.setClip(clip);

                // Main Information Box
                VBox infoBox = new VBox(6);

                infoBox.setAlignment(
                                Pos.CENTER_LEFT);

                Label docName = new Label(
                                "Dr. " + getDoctorFullName());

                docName.getStyleClass().add(
                                "banner-doc-name");

                Label docTitle = new Label(
                                getDoctorSpecialization());

                docTitle.getStyleClass().add(
                                "banner-doc-title");

                HBox locationBox = new HBox(6);

                locationBox.setAlignment(
                                Pos.CENTER_LEFT);

                ImageView locIcon = new ImageView(
                                ResourceImage.load(
                                                "/images/icons/ic_location.png"));

                locIcon.setFitWidth(14);
                locIcon.setFitHeight(14);

                Label locationText = new Label(
                                getDoctorHospital());

                locationText.getStyleClass().add(
                                "banner-subtext");

                locationBox.getChildren().addAll(
                                locIcon,
                                locationText);

                HBox ratingBox = new HBox(6);

                ratingBox.setAlignment(
                                Pos.CENTER_LEFT);

                ImageView starIcon = new ImageView(
                                ResourceImage.load(
                                                "/images/icons/ic_star.png"));

                starIcon.setFitWidth(14);
                starIcon.setFitHeight(14);

                Label ratingText = new Label("4.9/5 ");

                ratingText.getStyleClass().add(
                                "banner-rating-bold");

                Label reviewsText = new Label("(120 Reviews)");

                reviewsText.getStyleClass().add(
                                "banner-subtext");

                ratingBox.getChildren().addAll(
                                starIcon,
                                ratingText,
                                reviewsText);

                infoBox.getChildren().addAll(
                                docName,
                                docTitle,
                                locationBox,
                                ratingBox);

                Region spacer = new Region();

                HBox.setHgrow(
                                spacer,
                                Priority.ALWAYS);

                // Top Action Buttons
                HBox actionBtns = new HBox(12);

                actionBtns.setAlignment(
                                Pos.CENTER_RIGHT);

                ImageView settingsBtnIcon = new ImageView(
                                ResourceImage.load(
                                                "/images/icons/ic_settings.png"));

                settingsBtnIcon.setFitWidth(16);
                settingsBtnIcon.setFitHeight(16);

                Button settingsIconButton = new Button();

                settingsIconButton.setGraphic(
                                settingsBtnIcon);

                settingsIconButton.getStyleClass().add(
                                "btn-secondary-action");

                settingsIconButton.setOnAction(
                                e -> System.out.println(
                                                "Opening Quick Settings..."));

                Button accountSettingsBtn = new Button("Account Settings");

                accountSettingsBtn.getStyleClass().add(
                                "btn-secondary-action");

                accountSettingsBtn.setOnAction(
                                e -> System.out.println(
                                                "Opening Account Settings..."));

                Button editProfileBtn = new Button("Edit Profile");

                editProfileBtn.getStyleClass().add(
                                "btn-primary-action");

                editProfileBtn.setOnAction(
                                e -> Navigation.goTo(
                                                stage,
                                                () -> new DoctorEditProfileView(stage).getScene()));

                actionBtns.getChildren().addAll(
                                settingsIconButton,
                                accountSettingsBtn,
                                editProfileBtn);

                banner.getChildren().addAll(
                                doctorImage,
                                infoBox,
                                spacer,
                                actionBtns);

                return banner;
        }

        // ============================================================
        // BODY
        // ============================================================

        /** Two-Column Main Content Body */
        private HBox createBodyLayout() {

                HBox body = new HBox(20);

                // Left Column
                VBox leftColumn = new VBox(20);

                HBox.setHgrow(
                                leftColumn,
                                Priority.ALWAYS);

                leftColumn.getChildren().add(
                                createPersonalDetailsCard());

                leftColumn.getChildren().add(
                                createQualificationCard());

                leftColumn.getChildren().add(
                                createPatientReviewsCard());

                // Right Column
                VBox rightColumn = new VBox(20);

                rightColumn.setMinWidth(320);
                rightColumn.setMaxWidth(340);

                rightColumn.getChildren().add(
                                createAccountBalanceCard());

                rightColumn.getChildren().add(
                                createConsultationDetailsCard());

                rightColumn.getChildren().add(
                                createWeeklyAvailabilityCard());

                rightColumn.getChildren().add(
                                createSideImageCard());

                body.getChildren().addAll(
                                leftColumn,
                                rightColumn);

                return body;
        }

        // ============================================================
        // ACCOUNT BALANCE
        // ============================================================

        /** Account Balance & Revenue Growth Right-Column Card */
        private VBox createAccountBalanceCard() {

                VBox card = new VBox(16);

                card.getStyleClass().add(
                                "panel-card");

                card.setPadding(
                                new Insets(20));

                HBox cardTitleBox = new HBox(8);

                cardTitleBox.setAlignment(
                                Pos.CENTER_LEFT);

                ImageView walletIcon = new ImageView(
                                ResourceImage.load(
                                                "/images/icons/ic_card_white.png"));

                walletIcon.setFitWidth(18);
                walletIcon.setFitHeight(18);

                Label title = new Label("Account Balance");

                title.getStyleClass().add(
                                "card-title");

                cardTitleBox.getChildren().addAll(
                                walletIcon,
                                title);

                Separator sep = new Separator();

                VBox balanceBox = new VBox(6);

                balanceBox.setStyle(
                                "-fx-background-color: #F8FAFC; " +
                                                "-fx-padding: 14px; " +
                                                "-fx-background-radius: 8px; " +
                                                "-fx-border-color: #E2E8F0; " +
                                                "-fx-border-radius: 8px;");

                Label balLabel = new Label(
                                "TOTAL AVAILABLE BALANCE");

                balLabel.setStyle(
                                "-fx-text-fill: #64748B; " +
                                                "-fx-font-size: 11px; " +
                                                "-fx-font-weight: bold;");

                HBox amountGrowthBox = new HBox(10);

                amountGrowthBox.setAlignment(
                                Pos.BASELINE_LEFT);

                Label balAmount = new Label("$3,450.00");

                balAmount.setStyle(
                                "-fx-text-fill: #1E293B; " +
                                                "-fx-font-size: 24px; " +
                                                "-fx-font-weight: bold;");

                Label growthBadge = new Label("+14.5% ↑");

                growthBadge.setStyle(
                                "-fx-background-color: #D1FAE5; " +
                                                "-fx-text-fill: #059669; " +
                                                "-fx-font-weight: bold; " +
                                                "-fx-font-size: 11px; " +
                                                "-fx-padding: 2px 8px; " +
                                                "-fx-background-radius: 12px;");

                amountGrowthBox.getChildren().addAll(
                                balAmount,
                                growthBadge);

                balanceBox.getChildren().addAll(
                                balLabel,
                                amountGrowthBox);

                VBox chartBox = new VBox(4);

                Label chartHeader = new Label(
                                "Monthly Revenue Growth");

                chartHeader.setStyle(
                                "-fx-text-fill: #0F172A; " +
                                                "-fx-font-size: 12px; " +
                                                "-fx-font-weight: bold;");

                AreaChart<String, Number> revenueChart = createRevenueChart();

                chartBox.getChildren().addAll(
                                chartHeader,
                                revenueChart);

                VBox recentTxBox = new VBox(10);

                Label recentHeader = new Label(
                                "Recent Patient Payments");

                recentHeader.setStyle(
                                "-fx-text-fill: #0F172A; " +
                                                "-fx-font-size: 12px; " +
                                                "-fx-font-weight: bold;");

                recentTxBox.getChildren().add(
                                recentHeader);

                recentTxBox.getChildren().add(
                                createTransactionRow(
                                                "Robert Chen",
                                                "Video Consultation",
                                                "+$150.00"));

                recentTxBox.getChildren().add(
                                createTransactionRow(
                                                "Elena Smith",
                                                "In-Person Checkup",
                                                "+$150.00"));

                recentTxBox.getChildren().add(
                                createTransactionRow(
                                                "Sarah Jenkins",
                                                "Follow-up",
                                                "+$150.00"));

                Button withdrawBtn = new Button("Withdraw Funds");

                withdrawBtn.getStyleClass().add(
                                "btn-primary-action");

                withdrawBtn.setMaxWidth(
                                Double.MAX_VALUE);

                withdrawBtn.setOnAction(
                                e -> System.out.println(
                                                "Initiating withdrawal process..."));

                card.getChildren().addAll(
                                cardTitleBox,
                                sep,
                                balanceBox,
                                chartBox,
                                recentTxBox,
                                withdrawBtn);

                return card;
        }

        // ============================================================
        // REVENUE CHART
        // ============================================================

        /** Creates a compact Area Chart showing upward revenue growth trends */
        private AreaChart<String, Number> createRevenueChart() {

                CategoryAxis xAxis = new CategoryAxis();

                NumberAxis yAxis = new NumberAxis();

                xAxis.setAnimated(false);
                yAxis.setAnimated(false);

                yAxis.setVisible(false);
                yAxis.setOpacity(0);

                AreaChart<String, Number> areaChart = new AreaChart<>(
                                xAxis,
                                yAxis);

                areaChart.setLegendVisible(false);
                areaChart.setCreateSymbols(true);
                areaChart.setPrefHeight(130);
                areaChart.setMaxWidth(280);

                areaChart.setStyle(
                                "-fx-padding: 0; " +
                                                "-fx-background-color: transparent;");

                XYChart.Series<String, Number> series = new XYChart.Series<>();

                series.getData().add(
                                new XYChart.Data<>("May", 1800));

                series.getData().add(
                                new XYChart.Data<>("Jun", 2200));

                series.getData().add(
                                new XYChart.Data<>("Jul", 2700));

                series.getData().add(
                                new XYChart.Data<>("Aug", 3450));

                areaChart.getData().add(series);

                return areaChart;
        }

        // ============================================================
        // TRANSACTION ROW
        // ============================================================

        private BorderPane createTransactionRow(
                        String patientName,
                        String type,
                        String amount) {

                BorderPane row = new BorderPane();

                VBox left = new VBox(2);

                Label nameLbl = new Label(patientName);

                nameLbl.setStyle(
                                "-fx-text-fill: #334155; " +
                                                "-fx-font-size: 12px; " +
                                                "-fx-font-weight: bold;");

                Label typeLbl = new Label(type);

                typeLbl.setStyle(
                                "-fx-text-fill: #94A3B8; " +
                                                "-fx-font-size: 11px;");

                left.getChildren().addAll(
                                nameLbl,
                                typeLbl);

                Label amtLbl = new Label(amount);

                amtLbl.setStyle(
                                "-fx-text-fill: #10B981; " +
                                                "-fx-font-size: 12px; " +
                                                "-fx-font-weight: bold;");

                row.setLeft(left);
                row.setRight(amtLbl);

                BorderPane.setAlignment(
                                amtLbl,
                                Pos.CENTER_RIGHT);

                return row;
        }

        // ============================================================
        // SIDE IMAGE
        // ============================================================

        /** Side Promo Banner Card below Weekly Availability */
        private VBox createSideImageCard() {

                VBox card = new VBox();

                card.getStyleClass().add(
                                "panel-card");

                card.setStyle(
                                "-fx-padding: 0; " +
                                                "-fx-background-radius: 12px; " +
                                                "-fx-overflow: hidden;");

                ImageView sideImage = new ImageView(
                                ResourceImage.load(
                                                "/images/mocks/doctor_profile side.png"));

                sideImage.setFitWidth(340);
                sideImage.setPreserveRatio(true);

                Rectangle clip = new Rectangle(
                                340,
                                190);

                clip.setArcWidth(24);
                clip.setArcHeight(24);

                sideImage.setClip(clip);

                card.getChildren().add(
                                sideImage);

                return card;
        }

        // ============================================================
        // PERSONAL DETAILS
        // ============================================================

        /** Personal Details Section Card */
        private VBox createPersonalDetailsCard() {

                VBox card = new VBox(16);

                card.getStyleClass().add(
                                "panel-card");

                card.setPadding(
                                new Insets(20));

                HBox cardTitleBox = new HBox(8);

                cardTitleBox.setAlignment(
                                Pos.CENTER_LEFT);

                ImageView personIcon = new ImageView(
                                ResourceImage.load(
                                                "/images/icons/ic_person.png"));

                personIcon.setFitWidth(18);
                personIcon.setFitHeight(18);

                Label title = new Label("Personal Details");

                title.getStyleClass().add(
                                "card-title");

                cardTitleBox.getChildren().addAll(
                                personIcon,
                                title);

                Separator sep = new Separator();

                GridPane detailsGrid = new GridPane();

                detailsGrid.setHgap(40);
                detailsGrid.setVgap(16);

                // Row 1
                detailsGrid.add(
                                createDetailItem(
                                                "FULL NAME",
                                                "Dr. " + getDoctorFullName()),
                                0,
                                0);

                detailsGrid.add(
                                createDetailItem(
                                                "EMAIL ADDRESS",
                                                getDoctorEmail()),
                                1,
                                0);

                // Row 2
                detailsGrid.add(
                                createDetailItem(
                                                "PHONE NUMBER",
                                                getDoctorPhone()),
                                0,
                                1);

                /*
                 * Languages are not currently present in DoctorProfile.
                 * Keeping the original UI value until the model/schema
                 * is deliberately extended.
                 */
                detailsGrid.add(
                                createDetailItem(
                                                "LANGUAGES SPOKEN",
                                                "English, Spanish, French"),
                                1,
                                1);

                // Row 3: Bio
                VBox bioBox = new VBox(4);

                Label bioLabel = new Label("BIO");

                bioLabel.getStyleClass().add(
                                "detail-field-label");

                /*
                 * Bio is not currently present in DoctorProfile.
                 * Keep the original UI text until the database model
                 * is intentionally extended.
                 */
                Label bioText = new Label(
                                "Dedicated and compassionate Senior " +
                                                "Cardiologist with over 15 years of " +
                                                "clinical experience in diagnosing and " +
                                                "treating cardiovascular diseases. " +
                                                "Committed to providing comprehensive " +
                                                "patient care and staying updated with " +
                                                "the latest medical advancements.");

                bioText.getStyleClass().add(
                                "detail-field-value");

                bioText.setWrapText(true);

                bioBox.getChildren().addAll(
                                bioLabel,
                                bioText);

                detailsGrid.add(
                                bioBox,
                                0,
                                2,
                                2,
                                1);

                card.getChildren().addAll(
                                cardTitleBox,
                                sep,
                                detailsGrid);

                return card;
        }

        // ============================================================
        // DETAIL ITEM
        // ============================================================

        private VBox createDetailItem(
                        String label,
                        String value) {

                VBox box = new VBox(4);

                Label lbl = new Label(label);

                lbl.getStyleClass().add(
                                "detail-field-label");

                Label val = new Label(value);

                val.getStyleClass().add(
                                "detail-field-value");

                box.getChildren().addAll(
                                lbl,
                                val);

                return box;
        }

        // ============================================================
        // QUALIFICATION
        // ============================================================

        /** Qualification & Experience Section Card */
        private VBox createQualificationCard() {

                VBox card = new VBox(16);

                card.getStyleClass().add(
                                "panel-card");

                card.setPadding(
                                new Insets(20));

                HBox cardTitleBox = new HBox(8);

                cardTitleBox.setAlignment(
                                Pos.CENTER_LEFT);

                ImageView gradIcon = new ImageView(
                                ResourceImage.load(
                                                "/images/icons/ic_academic.png"));

                gradIcon.setFitWidth(18);
                gradIcon.setFitHeight(18);

                Label title = new Label(
                                "Qualification & Experience");

                title.getStyleClass().add(
                                "card-title");

                cardTitleBox.getChildren().addAll(
                                gradIcon,
                                title);

                Separator sep = new Separator();

                VBox list = new VBox(16);

                // Item 1: Experience
                HBox expRow = new HBox(12);

                expRow.setAlignment(
                                Pos.TOP_LEFT);

                StackPane expIconBox = new StackPane();

                expIconBox.getStyleClass().add(
                                "blue-icon-circle");

                ImageView expImg = new ImageView(
                                ResourceImage.load(
                                                "/images/icons/ic_briefcase.png"));

                expImg.setFitWidth(16);
                expImg.setFitHeight(16);

                expIconBox.getChildren().add(
                                expImg);

                VBox expText = new VBox(4);

                Label expHeader = new Label("Experience");

                expHeader.getStyleClass().add(
                                "item-header-title");

                Label expDesc = new Label(
                                getDoctorExperience());

                expDesc.getStyleClass().add(
                                "detail-field-value");

                expDesc.setWrapText(true);

                expText.getChildren().addAll(
                                expHeader,
                                expDesc);

                HBox.setHgrow(
                                expText,
                                Priority.ALWAYS);

                expRow.getChildren().addAll(
                                expIconBox,
                                expText);

                // Item 2: Specialization
                HBox specRow = new HBox(12);

                specRow.setAlignment(
                                Pos.TOP_LEFT);

                StackPane specIconBox = new StackPane();

                specIconBox.getStyleClass().add(
                                "green-icon-circle");

                ImageView specImg = new ImageView(
                                ResourceImage.load(
                                                "/images/icons/ic_award.png"));

                specImg.setFitWidth(16);
                specImg.setFitHeight(16);

                specIconBox.getChildren().add(
                                specImg);

                VBox specText = new VBox(4);

                Label specHeader = new Label("Specialization");

                specHeader.getStyleClass().add(
                                "item-header-title");

                Label specDesc = new Label(
                                getDoctorSpecialization());

                specDesc.getStyleClass().add(
                                "detail-field-value");

                specDesc.setWrapText(true);

                specText.getChildren().addAll(
                                specHeader,
                                specDesc);

                HBox.setHgrow(
                                specText,
                                Priority.ALWAYS);

                specRow.getChildren().addAll(
                                specIconBox,
                                specText);

                list.getChildren().addAll(
                                expRow,
                                specRow);

                card.getChildren().addAll(
                                cardTitleBox,
                                sep,
                                list);

                return card;
        }

        // ============================================================
        // PATIENT REVIEWS
        // ============================================================

        /** Patient Reviews Section Card */
        private VBox createPatientReviewsCard() {

                VBox card = new VBox(16);

                card.getStyleClass().add(
                                "panel-card");

                card.setPadding(
                                new Insets(20));

                BorderPane cardHeader = new BorderPane();

                HBox cardTitleBox = new HBox(8);

                cardTitleBox.setAlignment(
                                Pos.CENTER_LEFT);

                ImageView starTitleIcon = new ImageView(
                                ResourceImage.load(
                                                "/images/icons/ic_star_blue.png"));

                starTitleIcon.setFitWidth(18);
                starTitleIcon.setFitHeight(18);

                Label title = new Label("Patient Reviews");

                title.getStyleClass().add(
                                "card-title");

                cardTitleBox.getChildren().addAll(
                                starTitleIcon,
                                title);

                Hyperlink viewAllLink = new Hyperlink("View All");

                viewAllLink.getStyleClass().add(
                                "card-link");

                viewAllLink.setOnAction(
                                e -> System.out.println(
                                                "Opening all reviews..."));

                cardHeader.setLeft(
                                cardTitleBox);

                cardHeader.setRight(
                                viewAllLink);

                HBox reviewsRow = new HBox(16);

                VBox review1 = createReviewItem(
                                "Sarah Jenkins",
                                "2 weeks ago",
                                5,
                                "\"Dr. Sarah is exceptional. He took " +
                                                "the time to explain my condition " +
                                                "thoroughly and made me feel completely " +
                                                "at ease during my consultation.\"");

                VBox review2 = createReviewItem(
                                "Michael R.",
                                "1 month ago",
                                5,
                                "\"Very professional and knowledgeable. " +
                                                "The wait time was a bit long, but the " +
                                                "care provided was top-notch.\"");

                HBox.setHgrow(
                                review1,
                                Priority.ALWAYS);

                HBox.setHgrow(
                                review2,
                                Priority.ALWAYS);

                reviewsRow.getChildren().addAll(
                                review1,
                                review2);

                card.getChildren().addAll(
                                cardHeader,
                                reviewsRow);

                return card;
        }

        // ============================================================
        // REVIEW ITEM
        // ============================================================

        private VBox createReviewItem(
                        String author,
                        String timeAgo,
                        int stars,
                        String comment) {

                VBox reviewCard = new VBox(10);

                reviewCard.getStyleClass().add(
                                "review-box");

                reviewCard.setPadding(
                                new Insets(14));

                BorderPane topRow = new BorderPane();

                VBox authorInfo = new VBox(2);

                Label authorLabel = new Label(author);

                authorLabel.getStyleClass().add(
                                "review-author");

                Label timeLabel = new Label(timeAgo);

                timeLabel.getStyleClass().add(
                                "review-time");

                authorInfo.getChildren().addAll(
                                authorLabel,
                                timeLabel);

                HBox starRating = new HBox(2);

                starRating.setAlignment(
                                Pos.CENTER_RIGHT);

                for (int i = 0; i < stars; i++) {

                        ImageView star = new ImageView(
                                        ResourceImage.load(
                                                        "/images/icons/ic_star_green.png"));

                        star.setFitWidth(12);
                        star.setFitHeight(12);

                        starRating.getChildren().add(
                                        star);
                }

                topRow.setLeft(
                                authorInfo);

                topRow.setRight(
                                starRating);

                Label commentLabel = new Label(comment);

                commentLabel.getStyleClass().add(
                                "review-comment");

                commentLabel.setWrapText(true);

                reviewCard.getChildren().addAll(
                                topRow,
                                commentLabel);

                return reviewCard;
        }

        // ============================================================
        // CONSULTATION DETAILS
        // ============================================================

        /** Consultation Details Right-Column Blue Card */
        private VBox createConsultationDetailsCard() {

                VBox card = new VBox(16);

                card.getStyleClass().add(
                                "consultation-card");

                card.setPadding(
                                new Insets(20));

                Label title = new Label("Consultation Details");

                title.getStyleClass().add(
                                "consultation-title");

                VBox list = new VBox(14);

                // Row 1
                BorderPane feeRow = new BorderPane();

                HBox feeLeft = new HBox(10);

                feeLeft.setAlignment(
                                Pos.CENTER_LEFT);

                ImageView feeIcon = new ImageView(
                                ResourceImage.load(
                                                "/images/icons/ic_card_white.png"));

                feeIcon.setFitWidth(18);
                feeIcon.setFitHeight(18);

                Label feeLabel = new Label("Standard Fee");

                feeLabel.getStyleClass().add(
                                "consultation-label");

                feeLeft.getChildren().addAll(
                                feeIcon,
                                feeLabel);

                Label feeValue = new Label("$150");

                feeValue.getStyleClass().add(
                                "consultation-value-bold");

                feeRow.setLeft(feeLeft);
                feeRow.setRight(feeValue);

                Separator sep1 = new Separator();

                sep1.getStyleClass().add(
                                "consultation-separator");

                // Row 2
                BorderPane durRow = new BorderPane();

                HBox durLeft = new HBox(10);

                durLeft.setAlignment(
                                Pos.CENTER_LEFT);

                ImageView clockIcon = new ImageView(
                                ResourceImage.load(
                                                "/images/icons/ic_clock_white.png"));

                clockIcon.setFitWidth(18);
                clockIcon.setFitHeight(18);

                Label durLabel = new Label("Avg. Duration");

                durLabel.getStyleClass().add(
                                "consultation-label");

                durLeft.getChildren().addAll(
                                clockIcon,
                                durLabel);

                Label durValue = new Label("30 mins");

                durValue.getStyleClass().add(
                                "consultation-value-medium");

                durRow.setLeft(durLeft);
                durRow.setRight(durValue);

                Separator sep2 = new Separator();

                sep2.getStyleClass().add(
                                "consultation-separator");

                // Row 3
                BorderPane teleRow = new BorderPane();

                HBox teleLeft = new HBox(10);

                teleLeft.setAlignment(
                                Pos.CENTER_LEFT);

                ImageView camIcon = new ImageView(
                                ResourceImage.load(
                                                "/images/icons/ic_video_white.png"));

                camIcon.setFitWidth(18);
                camIcon.setFitHeight(18);

                Label teleLabel = new Label("Telehealth");

                teleLabel.getStyleClass().add(
                                "consultation-label");

                teleLeft.getChildren().addAll(
                                camIcon,
                                teleLabel);

                Label availableBadge = new Label("Available");

                availableBadge.getStyleClass().add(
                                "telehealth-badge");

                teleRow.setLeft(teleLeft);
                teleRow.setRight(availableBadge);

                list.getChildren().addAll(
                                feeRow,
                                sep1,
                                durRow,
                                sep2,
                                teleRow);

                card.getChildren().addAll(
                                title,
                                list);

                return card;
        }

        // ============================================================
        // WEEKLY AVAILABILITY
        // ============================================================

        /** Weekly Availability Right-Column White Card */
        private VBox createWeeklyAvailabilityCard() {

                VBox card = new VBox(16);

                card.getStyleClass().add(
                                "panel-card");

                card.setPadding(
                                new Insets(20));

                HBox titleBox = new HBox(8);

                titleBox.setAlignment(
                                Pos.CENTER_LEFT);

                ImageView calIcon = new ImageView(
                                ResourceImage.load(
                                                "/images/icons/ic_calendar_blue.png"));

                calIcon.setFitWidth(18);
                calIcon.setFitHeight(18);

                Label title = new Label("Weekly Availability");

                title.getStyleClass().add(
                                "card-title");

                titleBox.getChildren().addAll(
                                calIcon,
                                title);

                VBox list = new VBox(10);

                list.getChildren().add(
                                createAvailabilityRow(
                                                "Monday - Wed",
                                                "09:00 AM - 05:00 PM",
                                                true));

                list.getChildren().add(
                                createAvailabilityRow(
                                                "Thursday",
                                                "10:00 AM - 06:00 PM",
                                                true));

                list.getChildren().add(
                                createAvailabilityRow(
                                                "Friday",
                                                "09:00 AM - 01:00 PM",
                                                true));

                list.getChildren().add(
                                createAvailabilityRow(
                                                "Weekend",
                                                "Unavailable",
                                                false));

                Button manageBtn = new Button("Manage Schedule");

                ImageView manageIcon = new ImageView(
                                ResourceImage.load(
                                                "/images/icons/ic_calendar_manage.png"));

                manageIcon.setFitWidth(14);
                manageIcon.setFitHeight(14);

                manageBtn.setGraphic(
                                manageIcon);

                manageBtn.getStyleClass().add(
                                "btn-outline-full");

                manageBtn.setMaxWidth(
                                Double.MAX_VALUE);

                manageBtn.setOnAction(
                                e -> Navigation.goTo(
                                                stage,
                                                () -> new AvailabilityScheduleView(stage).getScene()));

                card.getChildren().addAll(
                                titleBox,
                                list,
                                manageBtn);

                return card;
        }

        // ============================================================
        // AVAILABILITY ROW
        // ============================================================

        private BorderPane createAvailabilityRow(
                        String day,
                        String time,
                        boolean isAvailable) {

                BorderPane row = new BorderPane();

                Label dayLabel = new Label(day);

                dayLabel.getStyleClass().add(
                                "avail-day-label");

                Label timeBadge = new Label(time);

                if (isAvailable) {

                        timeBadge.getStyleClass().add(
                                        "avail-time-badge");

                } else {

                        timeBadge.getStyleClass().add(
                                        "avail-unavailable-text");
                }

                row.setLeft(dayLabel);
                row.setRight(timeBadge);

                return row;
        }
}