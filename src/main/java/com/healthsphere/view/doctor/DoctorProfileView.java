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

        // UI Label References for Async Loading
        private Label docNameLabel;
        private Label docTitleLabel;
        private Label locationTextLabel;
        private Label ratingTextLabel;
        private Label reviewsTextLabel;
        private Label balAmountLabel;
        private Label fullNameDetailLabel;
        private Label emailDetailLabel;
        private Label phoneDetailLabel;
        private Label experienceDetailLabel;

        private static class ProfileBundle {
                final DoctorProfile profile;
                final double rating;
                final int reviews;
                final double balance;

                ProfileBundle(DoctorProfile profile, double rating, int reviews, double balance) {
                        this.profile = profile;
                        this.rating = rating;
                        this.reviews = reviews;
                        this.balance = balance;
                }
        }

        public DoctorProfileView(Stage stage) {
                this.stage = stage;

                // Backend controller
                this.doctorController = new DoctorController();
                this.paymentController = new com.healthsphere.controller.PaymentController();

                // UI creation (non-blocking)
                this.scene = createScene();

                // Load current doctor's profile & balance asynchronously
                loadDoctorProfileAsync();
        }

        public Scene getScene() {
                return this.scene;
        }

        private void loadDoctorProfileAsync() {
                String currentDocUid = SessionManager.getDoctorUid();
                if (currentDocUid == null || currentDocUid.isBlank()) {
                        return;
                }

                javafx.concurrent.Task<ProfileBundle> task = new javafx.concurrent.Task<>() {
                        @Override
                        protected ProfileBundle call() throws Exception {
                                DoctorProfile prof = null;
                                try {
                                        prof = doctorController.getDoctorProfile(currentDocUid);
                                } catch (Exception ignored) {}

                                double avgRating = 0.0;
                                int reviewCount = 0;
                                try {
                                        com.healthsphere.controller.patient.ReviewController revCtrl = new com.healthsphere.controller.patient.ReviewController();
                                        reviewCount = revCtrl.getReviewCount("DOCTOR", currentDocUid);
                                        if (reviewCount > 0) {
                                                avgRating = revCtrl.getAverageRating("DOCTOR", currentDocUid);
                                        }
                                } catch (Exception ignored) {}

                                double bal = 0.0;
                                try {
                                        bal = paymentController.getDoctorAccountBalance(currentDocUid);
                                } catch (Exception ignored) {}

                                return new ProfileBundle(prof, avgRating, reviewCount, bal);
                        }
                };

                task.setOnSucceeded(event -> {
                        ProfileBundle bundle = task.getValue();
                        this.doctorProfile = bundle.profile;

                        if (docNameLabel != null) docNameLabel.setText("Dr. " + getDoctorFullName());
                        if (docTitleLabel != null) docTitleLabel.setText(getDoctorSpecialization());
                        if (locationTextLabel != null) locationTextLabel.setText(getDoctorHospital());
                        if (ratingTextLabel != null) ratingTextLabel.setText(bundle.reviews > 0 ? String.format("%.1f/5 ", bundle.rating) : "No reviews ");
                        if (reviewsTextLabel != null) reviewsTextLabel.setText(String.format("(%d Reviews)", bundle.reviews));
                        if (balAmountLabel != null) balAmountLabel.setText(String.format("₹%.2f", bundle.balance));
                        if (fullNameDetailLabel != null) fullNameDetailLabel.setText("Dr. " + getDoctorFullName());
                        if (emailDetailLabel != null) emailDetailLabel.setText(getDoctorEmail());
                        if (phoneDetailLabel != null) phoneDetailLabel.setText(getDoctorPhone());
                        if (experienceDetailLabel != null) experienceDetailLabel.setText(getDoctorExperience());
                });

                task.setOnFailed(event -> {
                        if (balAmountLabel != null) balAmountLabel.setText("Unable to load account balance.");
                });

                Thread bgThread = new Thread(task);
                bgThread.setDaemon(true);
                bgThread.start();
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

        /** Top Navigation Header */
        private HBox createTopHeader() {

                HBox topBar = new HBox();

                topBar.setAlignment(
                                Pos.CENTER_LEFT);

                Region spacer = new Region();

                HBox.setHgrow(
                                spacer,
                                Priority.ALWAYS);

                HBox rightIcons = new HBox(18);

                rightIcons.setAlignment(
                                Pos.CENTER_RIGHT);

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

                rightIcons.getChildren().add(
                                topAvatar);

                topBar.getChildren().addAll(
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

                docNameLabel = new Label("Dr. " + getDoctorFullName());
                docNameLabel.getStyleClass().add("banner-doc-name");

                docTitleLabel = new Label(getDoctorSpecialization());
                docTitleLabel.getStyleClass().add("banner-doc-title");

                HBox locationBox = new HBox(6);
                locationBox.setAlignment(Pos.CENTER_LEFT);

                ImageView locIcon = new ImageView(ResourceImage.load("/images/icons/ic_location.png"));
                locIcon.setFitWidth(14);
                locIcon.setFitHeight(14);

                locationTextLabel = new Label(getDoctorHospital());
                locationTextLabel.getStyleClass().add("banner-subtext");

                locationBox.getChildren().addAll(locIcon, locationTextLabel);

                HBox ratingBox = new HBox(6);
                ratingBox.setAlignment(Pos.CENTER_LEFT);

                ImageView starIcon = new ImageView(ResourceImage.load("/images/icons/ic_star.png"));
                starIcon.setFitWidth(14);
                starIcon.setFitHeight(14);

                ratingTextLabel = new Label("... ");
                ratingTextLabel.getStyleClass().add("banner-rating-bold");

                reviewsTextLabel = new Label("(...)");
                reviewsTextLabel.getStyleClass().add("banner-subtext");

                ratingBox.getChildren().addAll(starIcon, ratingTextLabel, reviewsTextLabel);

                infoBox.getChildren().addAll(docNameLabel, docTitleLabel, locationBox, ratingBox);

                Region spacer = new Region();

                HBox.setHgrow(
                                spacer,
                                Priority.ALWAYS);

                // Top Action Buttons
                HBox actionBtns = new HBox(12);

                actionBtns.setAlignment(
                                Pos.CENTER_RIGHT);

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

                balAmountLabel = new Label("₹...");
                balAmountLabel.setStyle(
                                "-fx-text-fill: #1E293B; " +
                                                "-fx-font-size: 24px; " +
                                                "-fx-font-weight: bold;");

                Label growthBadge = new Label("✓ REAL EARNINGS");

                growthBadge.setStyle(
                                "-fx-background-color: #D1FAE5; " +
                                                "-fx-text-fill: #059669; " +
                                                "-fx-font-weight: bold; " +
                                                "-fx-font-size: 11px; " +
                                                "-fx-padding: 2px 8px; " +
                                                "-fx-background-radius: 12px;");

                amountGrowthBox.getChildren().addAll(
                                balAmountLabel,
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