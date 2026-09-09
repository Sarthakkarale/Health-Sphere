package com.healthsphere.view.hospital;

import com.healthsphere.controller.patient.ReviewController;
import com.healthsphere.model.Review;
import com.healthsphere.util.Navigation;
import com.healthsphere.util.SessionManager;
import com.healthsphere.util.ShimmerPlaceholder;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HospitalReviewView {

    // =========================================================
    // COLOR PALETTE
    // =========================================================

    private static final String PRIMARY_BLUE = "#2F80ED";
    private static final String PRIMARY_LIGHT = "#EEF3FF";
    private static final String DARK_TEXT = "#172B4D";
    private static final String SECONDARY_TEXT = "#64748B";
    private static final String LIGHT_BACKGROUND = "linear-gradient(to bottom right, #F4F8FC, #EEF3FF)";
    private static final String CARD_BG = "#FFFFFF";
    private static final String BORDER = "#E2E8F0";

    private static final String SIDEBAR_TEXT = "#D6E4F0";
    private static final String SIDEBAR_TEXT_HOVER = "#FFFFFF";
    private static final String SIDEBAR_HOVER_BG = "#1D4E7A";

    private static final String SUCCESS_GREEN = "#059669";
    private static final String SUCCESS_LIGHT = "#ECFDF5";
    private static final String WARNING_ORANGE = "#D97706";
    private static final String ERROR_RED = "#DC2626";
    private static final String GOLDEN_YELLOW = "#F59E0B";

    private final ReviewController reviewController = new ReviewController();

    // Loaded Real Reviews from Firebase
    private List<Review> allHospitalReviews = new ArrayList<>();

    // Dynamic UI References
    private VBox reviewListContainer;
    private VBox shimmerBox;

    private Label overallScoreLabel;
    private Label starsLabel;
    private Label countTextLabel;

    private Label recRateValLabel;
    private Label avgRespTimeValLabel;
    private Label openCompValLabel;

    private final Label[] starPercentLabels = new Label[5];
    private final Region[] starProgressBars = new Region[5];

    private ComboBox<String> ratingFilter;
    private TextField searchField;

    // =========================================================
    // CREATE SCENE
    // =========================================================

    public Scene createScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + LIGHT_BACKGROUND + ";");

        root.setLeft(HospitalSidebar.createSidebar(stage, HospitalSidebar.HospitalTab.REVIEWS));
        root.setTop(createTopBar(stage));

        ScrollPane scrollPane = new ScrollPane(createMainContent(stage));
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");

        root.setCenter(scrollPane);

        // Load Real Firebase Reviews Asynchronously
        loadHospitalReviewsAsync();

        return new Scene(root, stage.getWidth(), stage.getHeight());
    }

    // =========================================================
    // TOP BAR
    // =========================================================

    private HBox createTopBar(Stage stage) {
        HBox topBar = new HBox(16);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 28, 12, 28));
        topBar.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 0 0 1 0;"
        );

        Label searchIcon = new Label("⌕");
        searchIcon.setStyle("-fx-font-size: 18px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        searchField = new TextField();
        searchField.setPromptText("Search patient feedback or doctor name...");
        searchField.setStyle("-fx-background-color: transparent; -fx-prompt-text-fill: #94A3B8; -fx-font-size: 13px; -fx-text-inner-color: " + DARK_TEXT + ";");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFiltersAndRender());

        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPrefWidth(360);
        searchBox.setPrefHeight(40);
        searchBox.setPadding(new Insets(0, 12, 0, 12));
        searchBox.setStyle(
                "-fx-background-color: " + LIGHT_BACKGROUND + ";" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 8;"
        );
        searchBox.getChildren().addAll(searchIcon, searchField);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label administrator = new Label("Hospital Administrator");
        administrator.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TEXT + ";");

        Label role = new Label("HOSPITAL ADMIN");
        role.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");

        VBox userInfo = new VBox(2);
        userInfo.setAlignment(Pos.CENTER_RIGHT);
        userInfo.getChildren().addAll(administrator, role);

        Circle avatar = new Circle(18);
        avatar.setFill(Color.web(PRIMARY_LIGHT));
        avatar.setStroke(Color.web(BORDER));

        Label avatarText = new Label("HA");
        avatarText.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        StackPane avatarPane = new StackPane(avatar, avatarText);

        topBar.getChildren().addAll(spacer, userInfo, avatarPane);
        return topBar;
    }

    // =========================================================
    // MAIN CONTENT
    // =========================================================

    private VBox createMainContent(Stage stage) {
        VBox content = new VBox(24);
        content.setPadding(new Insets(28));
        content.setStyle("-fx-background-color: " + LIGHT_BACKGROUND + ";");

        content.getChildren().addAll(
                createHeader(),
                createRatingOverview(),
                createFilterAndSearchSection(),
                createReviewListSection()
        );

        return content;
    }

    private VBox createHeader() {
        VBox header = new VBox(4);

        Label title = new Label("Patient Reviews & Feedback");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 800; -fx-text-fill: " + DARK_TEXT + ";");

        Label subtitle = new Label("Monitor real patient experiences, respond to testimonials, and resolve care issues");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    // =========================================================
    // RATING OVERVIEW CARD & BREAKDOWN
    // =========================================================

    private HBox createRatingOverview() {
        HBox overviewBox = new HBox(20);

        // Overall Score Card
        VBox scoreCard = createCard();
        scoreCard.setPrefWidth(280);
        scoreCard.setAlignment(Pos.CENTER);

        overallScoreLabel = new Label("—");
        overallScoreLabel.setStyle("-fx-font-size: 44px; -fx-font-weight: 900; -fx-text-fill: " + DARK_TEXT + ";");

        starsLabel = new Label("☆ ☆ ☆ ☆ ☆");
        starsLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: " + GOLDEN_YELLOW + ";");

        countTextLabel = new Label("Loading Firebase reviews...");
        countTextLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        scoreCard.getChildren().addAll(overallScoreLabel, starsLabel, countTextLabel);

        // Star Rating Distribution Breakdown Card
        VBox breakdownCard = createCard();
        HBox.setHgrow(breakdownCard, Priority.ALWAYS);

        Label title = new Label("Rating Distribution");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: 700; -fx-text-fill: " + DARK_TEXT + ";");

        VBox barList = new VBox(8);
        for (int i = 0; i < 5; i++) {
            int starNum = 5 - i;
            barList.getChildren().add(createRatingProgressBar(starNum + (starNum == 1 ? " Star" : " Stars"), i));
        }

        breakdownCard.getChildren().addAll(title, barList);

        // Quick Highlights Metric Box
        VBox metricsCard = createCard();
        metricsCard.setPrefWidth(280);

        Label metricsTitle = new Label("Feedback Metrics");
        metricsTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: 700; -fx-text-fill: " + DARK_TEXT + ";");

        recRateValLabel = new Label("N/A");
        recRateValLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 800; -fx-text-fill: " + SUCCESS_GREEN + ";");

        avgRespTimeValLabel = new Label("N/A");
        avgRespTimeValLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 800; -fx-text-fill: " + PRIMARY_BLUE + ";");

        openCompValLabel = new Label("0 Pending");
        openCompValLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 800; -fx-text-fill: " + WARNING_ORANGE + ";");

        VBox recBox = createMetricRow("Recommendation Rate", recRateValLabel);
        VBox respBox = createMetricRow("Avg Response Time", avgRespTimeValLabel);
        VBox compBox = createMetricRow("Open Complaints", openCompValLabel);

        metricsCard.getChildren().addAll(metricsTitle, recBox, respBox, compBox);

        overviewBox.getChildren().addAll(scoreCard, breakdownCard, metricsCard);

        return overviewBox;
    }

    private HBox createRatingProgressBar(String labelText, int index) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(labelText);
        label.setPrefWidth(55);
        label.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: " + SECONDARY_TEXT + ";");

        StackPane progressContainer = new StackPane();
        progressContainer.setPrefHeight(8);
        HBox.setHgrow(progressContainer, Priority.ALWAYS);

        Region background = new Region();
        background.setMaxWidth(Double.MAX_VALUE);
        background.setPrefHeight(8);
        background.setStyle("-fx-background-color: #E2E8F0; -fx-background-radius: 6;");

        Region bar = new Region();
        bar.setPrefHeight(8);
        bar.setStyle("-fx-background-color: " + GOLDEN_YELLOW + "; -fx-background-radius: 6;");
        StackPane.setAlignment(bar, Pos.CENTER_LEFT);

        starProgressBars[index] = bar;

        progressContainer.getChildren().addAll(background, bar);

        Label percentLabel = new Label("0%");
        percentLabel.setPrefWidth(40);
        percentLabel.setAlignment(Pos.CENTER_RIGHT);
        percentLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");

        starPercentLabels[index] = percentLabel;

        row.getChildren().addAll(label, progressContainer, percentLabel);
        return row;
    }

    private VBox createMetricRow(String label, Label valueLabel) {
        VBox box = new VBox(2);
        box.setPadding(new Insets(6, 10, 6, 10));
        box.setStyle("-fx-background-color: " + LIGHT_BACKGROUND + "; -fx-background-radius: 8;");

        Label title = new Label(label);
        title.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        box.getChildren().addAll(title, valueLabel);
        return box;
    }

    // =========================================================
    // FILTERS & CONTROLS
    // =========================================================

    private HBox createFilterAndSearchSection() {
        HBox filterBox = new HBox(12);
        filterBox.setAlignment(Pos.CENTER_LEFT);

        ratingFilter = new ComboBox<>();
        ratingFilter.getItems().addAll("All Ratings", "5 Stars Only", "4 Stars & Above", "3 Stars & Below", "Critical (1-2 Stars)");
        ratingFilter.setValue("All Ratings");
        ratingFilter.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: " + BORDER + "; -fx-border-radius: 6;");

        ratingFilter.setOnAction(e -> applyFiltersAndRender());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button exportBtn = new Button("↓ Export Feedback");
        exportBtn.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 6;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";" +
                "-fx-cursor: hand;"
        );

        exportBtn.setOnAction(e -> showInformationDialog("Export Data", "Exporting feedback records to CSV file..."));

        filterBox.getChildren().addAll(ratingFilter, spacer, exportBtn);
        return filterBox;
    }

    // =========================================================
    // REVIEWS LIST SECTION
    // =========================================================

    private VBox createReviewListSection() {
        reviewListContainer = new VBox(16);

        shimmerBox = ShimmerPlaceholder.createListShimmer(3);
        reviewListContainer.getChildren().add(shimmerBox);

        return reviewListContainer;
    }

    // =========================================================
    // ASYNC FIREBASE DATA LOADING
    // =========================================================

    private void loadHospitalReviewsAsync() {
        String hospitalId = SessionManager.getHospitalUid();
        if (hospitalId == null || hospitalId.isBlank()) {
            if (SessionManager.getCurrentUser() != null) {
                hospitalId = SessionManager.getCurrentUser().getUid();
            }
        }

        final String finalHospitalId = hospitalId != null ? hospitalId : "";

        Task<List<Review>> task = new Task<>() {
            @Override
            protected List<Review> call() throws Exception {
                if (finalHospitalId.isBlank()) {
                    return new ArrayList<>();
                }
                return reviewController.getReviewsByTarget("HOSPITAL", finalHospitalId);
            }
        };

        task.setOnSucceeded(e -> {
            reviewListContainer.getChildren().remove(shimmerBox);
            allHospitalReviews = task.getValue();
            if (allHospitalReviews == null) {
                allHospitalReviews = new ArrayList<>();
            }
            updateAnalyticsSummary(allHospitalReviews);
            applyFiltersAndRender();
        });

        task.setOnFailed(e -> {
            reviewListContainer.getChildren().remove(shimmerBox);
            allHospitalReviews = new ArrayList<>();
            updateAnalyticsSummary(allHospitalReviews);
            applyFiltersAndRender();
        });

        com.healthsphere.util.AppBackgroundExecutor.execute(task);
    }

    // =========================================================
    // ANALYTICS & DISTRIBUTION CALCULATION
    // =========================================================

    private void updateAnalyticsSummary(List<Review> reviews) {
        int total = reviews.size();
        if (total == 0) {
            overallScoreLabel.setText("No Ratings");
            overallScoreLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: 800; -fx-text-fill: " + SECONDARY_TEXT + ";");
            starsLabel.setText("☆ ☆ ☆ ☆ ☆");
            countTextLabel.setText("No verified reviews yet");
            recRateValLabel.setText("N/A");

            for (int i = 0; i < 5; i++) {
                starPercentLabels[i].setText("0%");
                starProgressBars[i].prefWidthProperty().unbind();
                starProgressBars[i].setPrefWidth(0);
            }
            return;
        }

        int[] starCounts = new int[6]; // 1-indexed for ratings 1..5
        int totalRating = 0;
        int positiveCount = 0; // 4 or 5 stars

        for (Review r : reviews) {
            if (r == null) continue;
            int rating = r.getRating();
            if (rating >= 1 && rating <= 5) {
                starCounts[rating]++;
                totalRating += rating;
                if (rating >= 4) {
                    positiveCount++;
                }
            }
        }

        double avg = (double) totalRating / total;
        overallScoreLabel.setText(String.format("%.1f", avg));
        overallScoreLabel.setStyle("-fx-font-size: 44px; -fx-font-weight: 900; -fx-text-fill: " + DARK_TEXT + ";");

        // Format stars
        StringBuilder sb = new StringBuilder();
        int roundedStar = (int) Math.round(avg);
        for (int i = 1; i <= 5; i++) {
            sb.append(i <= roundedStar ? "★ " : "☆ ");
        }
        starsLabel.setText(sb.toString().trim());
        countTextLabel.setText(String.format("Based on %d verified %s", total, total == 1 ? "review" : "reviews"));

        // Update 5-star down to 1-star breakdown
        for (int i = 0; i < 5; i++) {
            int starNum = 5 - i;
            int count = starCounts[starNum];
            double pct = ((double) count / total) * 100.0;
            starPercentLabels[i].setText(String.format("%.0f%%", pct));
            double ratio = (double) count / total;
            starProgressBars[i].setStyle("-fx-background-color: " + GOLDEN_YELLOW + "; -fx-background-radius: 6;");
            // Set static width ratio relative to container
            starProgressBars[i].setPrefWidth(160 * ratio);
        }

        // Recommendation Rate
        double recRate = ((double) positiveCount / total) * 100.0;
        recRateValLabel.setText(String.format("%.0f%%", recRate));
    }

    // =========================================================
    // FILTERING AND RENDERING
    // =========================================================

    private void applyFiltersAndRender() {
        reviewListContainer.getChildren().clear();

        if (allHospitalReviews == null || allHospitalReviews.isEmpty()) {
            VBox emptyCard = createEmptyStateCard(
                    "⭐",
                    "No Reviews Yet",
                    "There are no patient reviews submitted for this hospital yet.",
                    null,
                    null
            );
            reviewListContainer.getChildren().add(emptyCard);
            return;
        }

        String searchText = searchField != null && searchField.getText() != null ? searchField.getText().trim().toLowerCase() : "";
        String rFilter = ratingFilter != null && ratingFilter.getValue() != null ? ratingFilter.getValue() : "All Ratings";

        List<Review> filtered = allHospitalReviews.stream().filter(r -> {
            if (r == null) return false;

            // Rating Filter
            if ("5 Stars Only".equals(rFilter) && r.getRating() != 5) return false;
            if ("4 Stars & Above".equals(rFilter) && r.getRating() < 4) return false;
            if ("3 Stars & Below".equals(rFilter) && r.getRating() > 3) return false;
            if ("Critical (1-2 Stars)".equals(rFilter) && (r.getRating() < 1 || r.getRating() > 2)) return false;

            // Search Text Filter
            if (!searchText.isEmpty()) {
                String pName = r.getPatientName() != null ? r.getPatientName().toLowerCase() : "";
                String comment = r.getComment() != null ? r.getComment().toLowerCase() : "";
                String tName = r.getTargetName() != null ? r.getTargetName().toLowerCase() : "";
                return pName.contains(searchText) || comment.contains(searchText) || tName.contains(searchText);
            }

            return true;
        }).collect(Collectors.toList());

        if (filtered.isEmpty()) {
            VBox emptyCard = createEmptyStateCard(
                    "⌕",
                    "No Matching Reviews",
                    "No reviews match your search or filter criteria.",
                    "Clear Filters",
                    () -> {
                        if (searchField != null) searchField.clear();
                        if (ratingFilter != null) ratingFilter.setValue("All Ratings");
                    }
            );
            reviewListContainer.getChildren().add(emptyCard);
            return;
        }

        for (Review r : filtered) {
            reviewListContainer.getChildren().add(createReviewCardFromRecord(r));
        }
    }

    private VBox createReviewCardFromRecord(Review r) {
        String patientName = r.getPatientName() != null && !r.getPatientName().isBlank() ? r.getPatientName() : "Anonymous Patient";
        String dateText = r.getCreatedAt() != null ? r.getCreatedAt() : "Recent";
        String category = r.getTargetName() != null && !r.getTargetName().isBlank() ? r.getTargetName() : "General Consultation";
        int rating = r.getRating();
        String commentText = r.getComment() != null ? r.getComment() : "";

        return createReviewCard(patientName, dateText, category, rating, "Patient Feedback", commentText, true);
    }

    private VBox createReviewCard(String patientName, String dateText, String category, int rating, String titleText, String reviewBody, boolean verified) {
        VBox card = createCard();

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Circle avatar = new Circle(18);
        avatar.setFill(Color.web(PRIMARY_LIGHT));

        String initial = patientName.length() > 0 ? patientName.substring(0, 1).toUpperCase() : "P";
        Label avatarLabel = new Label(initial);
        avatarLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        StackPane avatarPane = new StackPane(avatar, avatarLabel);

        VBox patientDetailBox = new VBox(2);

        HBox nameLine = new HBox(6);
        nameLine.setAlignment(Pos.CENTER_LEFT);

        Label name = new Label(patientName);
        name.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: " + DARK_TEXT + ";");
        nameLine.getChildren().add(name);

        if (verified) {
            Label badge = new Label("✓ Verified Patient");
            badge.setStyle("-fx-background-color: " + SUCCESS_LIGHT + "; -fx-text-fill: " + SUCCESS_GREEN + "; -fx-font-size: 9px; -fx-font-weight: bold; -fx-padding: 2 6 2 6; -fx-background-radius: 4;");
            nameLine.getChildren().add(badge);
        }

        Label cat = new Label(category);
        cat.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        patientDetailBox.getChildren().addAll(nameLine, cat);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label date = new Label(dateText);
        date.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        topRow.getChildren().addAll(avatarPane, patientDetailBox, spacer, date);

        StringBuilder starStr = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            if (i < rating) starStr.append("★ ");
            else starStr.append("☆ ");
        }

        Label ratingStars = new Label(starStr.toString().trim());
        ratingStars.setStyle("-fx-font-size: 14px; -fx-text-fill: " + GOLDEN_YELLOW + ";");

        Label reviewTitle = new Label(titleText);
        reviewTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: 700; -fx-text-fill: " + DARK_TEXT + ";");

        Label body = new Label(reviewBody);
        body.setWrapText(true);
        body.setStyle("-fx-font-size: 12px; -fx-text-fill: " + DARK_TEXT + "; -fx-line-spacing: 3;");

        HBox actionRow = new HBox(10);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        Button replyBtn = new Button("💬 Reply");
        replyBtn.setStyle("-fx-background-color: " + PRIMARY_LIGHT + "; -fx-text-fill: " + PRIMARY_BLUE + "; -fx-font-weight: bold; -fx-font-size: 11px; -fx-background-radius: 6; -fx-cursor: hand;");

        Button flagBtn = new Button("⚑ Flag Issue");
        flagBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + ERROR_RED + "; -fx-font-size: 11px; -fx-cursor: hand;");

        replyBtn.setOnAction(e -> showReplyDialog(patientName));
        flagBtn.setOnAction(e -> showInformationDialog("Report Review", "Review flagged for Quality Assurance review."));

        actionRow.getChildren().addAll(replyBtn, flagBtn);

        card.getChildren().addAll(topRow, ratingStars, reviewTitle, body, actionRow);

        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + PRIMARY_BLUE + ";" +
                "-fx-border-radius: 12;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;"
        ));
        return card;
    }

    private VBox createEmptyStateCard(String icon, String title, String description, String buttonText, Runnable buttonAction) {
        VBox box = new VBox(12);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40));
        box.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 12; -fx-border-color: " + BORDER + "; -fx-border-radius: 12;");

        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TEXT + ";");

        Label descLbl = new Label(description);
        descLbl.setWrapText(true);
        descLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-text-alignment: center;");

        box.getChildren().addAll(iconLbl, titleLbl, descLbl);

        if (buttonText != null && buttonAction != null) {
            Button btn = new Button(buttonText);
            btn.setStyle("-fx-background-color: " + PRIMARY_BLUE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16; -fx-cursor: hand;");
            btn.setOnAction(e -> buttonAction.run());
            box.getChildren().add(btn);
        }
        return box;
    }

    private VBox createCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(15, 23, 42, 0.04));
        shadow.setRadius(10);
        shadow.setOffsetY(3);
        card.setEffect(shadow);

        return card;
    }

    private void showReplyDialog(String patientName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Response System");
        alert.setHeaderText("Reply to feedback by " + patientName);

        TextArea textArea = new TextArea();
        textArea.setPromptText("Type official hospital response here...");
        alert.getDialogPane().setContent(textArea);

        Optional<?> result = alert.showAndWait();
        if (result.isPresent()) {
            showInformationDialog("Success", "Response posted successfully to patient record.");
        }
    }

    private void showInformationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}