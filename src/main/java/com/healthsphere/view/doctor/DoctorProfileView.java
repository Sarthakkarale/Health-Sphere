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
        private final com.healthsphere.controller.PaymentController paymentController;
        private DoctorProfile doctorProfile;

        public DoctorProfileView(Stage stage) {
                this.stage = stage;

                // Backend controller
                this.doctorController = new DoctorController();
                this.paymentController = new com.healthsphere.controller.PaymentController();

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
                return DoctorSidebar.create(stage, 7);
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
                                                () -> new DoctorSessionsView(stage).getScene());
                                break;

                        case 4:
                                Navigation.goTo(
                                                stage,
                                                () -> new PatientDetailsView(stage).getScene());
                                break;

                        case 5:
                                Navigation.goTo(
                                                stage,
                                                () -> new MedicalReportsView(stage).getScene());
                                break;

                        case 6:
                                Navigation.goTo(
                                                stage,
                                                () -> new AvailabilityScheduleView(stage).getScene());
                                break;

                        case 7:
                                Navigation.goTo(
                                                stage,
                                                () -> new DoctorProfileView(stage).getScene());
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

                // Left Column: Doctor Information
                VBox leftColumn = new VBox(20);

                HBox.setHgrow(
                                leftColumn,
                                Priority.ALWAYS);

                leftColumn.getChildren().add(
                                createPersonalDetailsCard());

                leftColumn.getChildren().add(
                                createQualificationCard());

                // Right Column: Total Account Balance
                VBox rightColumn = new VBox(20);

                rightColumn.setMinWidth(320);
                rightColumn.setMaxWidth(340);

                rightColumn.getChildren().add(
                                createAccountBalanceCard());

                body.getChildren().addAll(
                                leftColumn,
                                rightColumn);

                return body;
        }

        // ============================================================
        // ACCOUNT BALANCE
        // ============================================================

        /** Account Balance Right-Column Card */
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

                Label balAmount;
                String docUid = SessionManager.getDoctorUid();
                double liveBal = 3450.00;
                if (docUid != null && !docUid.isBlank()) {
                        liveBal = paymentController.getDoctorAccountBalance(docUid);
                }
                balAmount = new Label(String.format("₹%.2f", liveBal));

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
                                withdrawBtn);

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
}