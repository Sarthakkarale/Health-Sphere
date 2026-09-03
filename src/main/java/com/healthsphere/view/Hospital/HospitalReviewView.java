package com.healthsphere.view.hospital;

import com.healthsphere.util.Navigation;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.Optional;

public class HospitalReviewView {

    // =========================================================
    // COLOR PALETTE (Light Content with Dark Sidebar)
    // =========================================================

    private static final String PRIMARY_BLUE = "#170eca";
    private static final String PRIMARY_LIGHT = "#EFF5FF";
    private static final String DARK_TEXT = "#0F172A";
    private static final String SECONDARY_TEXT = "#64748B";
    private static final String LIGHT_BACKGROUND = "#F8FAFC";
    private static final String CARD_BG = "#FFFFFF";
    private static final String BORDER = "#E2E8F0";

    // DARK SIDEBAR THEME
    private static final String SIDEBAR_BG = "#0F172A";
    private static final String SIDEBAR_BORDER = "#1E293B";
    private static final String SIDEBAR_TEXT = "#94A3B8";
    private static final String SIDEBAR_TEXT_HOVER = "#F8FAFC";
    private static final String SIDEBAR_HOVER_BG = "#1E293B";

    private static final String SUCCESS_GREEN = "#059669";
    private static final String SUCCESS_LIGHT = "#ECFDF5";

    private static final String WARNING_ORANGE = "#D97706";
    private static final String WARNING_LIGHT = "#FFFBEB";

    private static final String ERROR_RED = "#DC2626";

    private static final String GOLDEN_YELLOW = "#F59E0B";

    // Dynamic Container for Review Items
    private VBox reviewListContainer;

    // =========================================================
    // CREATE SCENE
    // =========================================================

    public Scene createScene(Stage stage) {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + LIGHT_BACKGROUND + ";");

        root.setLeft(createSidebar(stage));
        root.setTop(createTopBar(stage));

        ScrollPane scrollPane = new ScrollPane(createMainContent(stage));
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");

        root.setCenter(scrollPane);

        return new Scene(root, stage.getWidth(), stage.getHeight());
    }

    // =========================================================
    // SIDEBAR (DARK THEME)
    // =========================================================

    private VBox createSidebar(Stage stage) {

        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(240);
        sidebar.setPadding(new Insets(24, 16, 20, 16));

        sidebar.setStyle(
                "-fx-background-color: " + SIDEBAR_BG + ";" +
                "-fx-border-color: " + SIDEBAR_BORDER + ";" +
                "-fx-border-width: 0 1 0 0;"
        );

        // LOGO
        VBox logoBox = new VBox(2);
        logoBox.setPadding(new Insets(0, 8, 24, 8));

        Label logo = new Label("Health-Sphere");
        logo.setStyle("-fx-font-size: 22px; -fx-font-weight: 800; -fx-text-fill: #FFFFFF;");

        Label subtitle = new Label("SMART HEALTHCARE");
        subtitle.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-letter-spacing: 1px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        logoBox.getChildren().addAll(logo, subtitle);
        sidebar.getChildren().add(logoBox);

        // NAVIGATION BUTTONS
        Button dashboardButton = createNavigationButton("▦", "Dashboard", false);
        Button doctorButton = createNavigationButton("♙", "Doctors", false);
        Button departmentButton = createNavigationButton("✚", "Departments", false);
        Button bedButton = createNavigationButton("▥", "Beds", false);
        Button appointmentButton = createNavigationButton("▣", "Appointments", false);
        Button reviewsButton = createNavigationButton("★", "Patient Reviews", true); // Selected
        Button analyticsButton = createNavigationButton("◈", "Analytics", false);
        Button settingsButton = createNavigationButton("⚙", "Hospital Settings", false);

        sidebar.getChildren().addAll(
                dashboardButton,
                doctorButton,
                departmentButton,
                bedButton,
                appointmentButton,
                reviewsButton,
                analyticsButton,
                settingsButton
        );

        // NAVIGATION ACTIONS
        dashboardButton.setOnAction(event -> stage.setScene(new HospitalDashboardView().createScene(stage)));
        
        doctorButton.setOnAction(event -> {
            DoctorManagementView doctorView = new DoctorManagementView();
            stage.setScene(doctorView.createScene(stage));
        });

        departmentButton.setOnAction(event -> {
            DepartmentManagementView departmentView = new DepartmentManagementView();
            stage.setScene(departmentView.createScene(stage));
        });

        bedButton.setOnAction(event -> {
            BedManagementView bedView = new BedManagementView();
            stage.setScene(bedView.createScene(stage));
        });

        appointmentButton.setOnAction(event -> {
            AppointmentManagementView appointmentView = new AppointmentManagementView();
            stage.setScene(appointmentView.createScene(stage));
        });

        analyticsButton.setOnAction(event -> {
            HospitalAnalyticsView analyticsView = new HospitalAnalyticsView();
            stage.setScene(analyticsView.createScene(stage));
        });

        settingsButton.setOnAction(event -> {
            HospitalProfileSettingsView settingsView = new HospitalProfileSettingsView();
            stage.setScene(settingsView.createScene(stage));
        });

        // SPACER
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);

        // FOOTER BUTTONS
        Button helpButton = createNavigationButton("?", "Help Center", false);
        Button logoutButton = createNavigationButton("↪", "Logout", false);

        helpButton.setOnAction(e -> showInformationDialog("Help Center", "Support Email: support@healthsphere.com"));
        logoutButton.setOnAction(e -> Navigation.logout(stage));

        sidebar.getChildren().addAll(helpButton, logoutButton);

        return sidebar;
    }

    private Button createNavigationButton(String icon, String text, boolean selected) {

        Button button = new Button();

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + (selected ? "#FFFFFF" : SIDEBAR_TEXT) + ";");

        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: " + (selected ? "bold" : "500") + "; -fx-text-fill: " + (selected ? "#FFFFFF" : SIDEBAR_TEXT) + ";");

        HBox content = new HBox(12);
        content.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().addAll(iconLabel, textLabel);

        button.setGraphic(content);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(42);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(0, 12, 0, 12));

        String baseStyle = "-fx-background-radius: 8; -fx-cursor: hand;";

        if (selected) {
            button.setStyle(baseStyle + "-fx-background-color: " + PRIMARY_BLUE + ";");
        } else {
            button.setStyle(baseStyle + "-fx-background-color: transparent;");
            button.setOnMouseEntered(e -> {
                button.setStyle(baseStyle + "-fx-background-color: " + SIDEBAR_HOVER_BG + ";");
                iconLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + SIDEBAR_TEXT_HOVER + ";");
                textLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 500; -fx-text-fill: " + SIDEBAR_TEXT_HOVER + ";");
            });
            button.setOnMouseExited(e -> {
                button.setStyle(baseStyle + "-fx-background-color: transparent;");
                iconLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + SIDEBAR_TEXT + ";");
                textLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 500; -fx-text-fill: " + SIDEBAR_TEXT + ";");
            });
        }

        return button;
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

        TextField searchField = new TextField();
        searchField.setPromptText("Search patient feedback or doctor name...");
        searchField.setStyle("-fx-background-color: transparent; -fx-prompt-text-fill: #94A3B8; -fx-font-size: 13px; -fx-text-inner-color: " + DARK_TEXT + ";");
        HBox.setHgrow(searchField, Priority.ALWAYS);

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

        StackPane avatarBox = new StackPane(avatar, avatarText);

        topBar.getChildren().addAll(searchBox, spacer, userInfo, avatarBox);

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

    // =========================================================
    // HEADER
    // =========================================================

    private VBox createHeader() {

        VBox header = new VBox(4);

        Label title = new Label("Patient Reviews & Feedback");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: 800; -fx-text-fill: " + DARK_TEXT + ";");

        Label subtitle = new Label("Monitor patient experiences, respond to testimonials, and resolve care issues");
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

        Label overallScore = new Label("4.8");
        overallScore.setStyle("-fx-font-size: 48px; -fx-font-weight: 900; -fx-text-fill: " + DARK_TEXT + ";");

        Label stars = new Label("★ ★ ★ ★ ★");
        stars.setStyle("-fx-font-size: 18px; -fx-text-fill: " + GOLDEN_YELLOW + ";");

        Label countText = new Label("Based on 1,248 verified reviews");
        countText.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        scoreCard.getChildren().addAll(overallScore, stars, countText);

        // Star Rating Distribution Breakdown Card
        VBox breakdownCard = createCard();
        HBox.setHgrow(breakdownCard, Priority.ALWAYS);

        Label title = new Label("Rating Distribution");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: 700; -fx-text-fill: " + DARK_TEXT + ";");

        VBox barList = new VBox(8);
        barList.getChildren().addAll(
                createRatingProgressBar("5 Stars", 0.78, "78%"),
                createRatingProgressBar("4 Stars", 0.14, "14%"),
                createRatingProgressBar("3 Stars", 0.05, "5%"),
                createRatingProgressBar("2 Stars", 0.02, "2%"),
                createRatingProgressBar("1 Star", 0.01, "1%")
        );

        breakdownCard.getChildren().addAll(title, barList);

        // Quick Highlights Metric Box
        VBox metricsCard = createCard();
        metricsCard.setPrefWidth(280);

        Label metricsTitle = new Label("Feedback Metrics");
        metricsTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: 700; -fx-text-fill: " + DARK_TEXT + ";");

        VBox recBox = createMetricRow("Recommendation Rate", "94%", SUCCESS_GREEN);
        VBox respBox = createMetricRow("Avg Response Time", "2.4 Hours", PRIMARY_BLUE);
        VBox compBox = createMetricRow("Open Complaints", "3 Pending", WARNING_ORANGE);

        metricsCard.getChildren().addAll(metricsTitle, recBox, respBox, compBox);

        overviewBox.getChildren().addAll(scoreCard, breakdownCard, metricsCard);

        return overviewBox;
    }

    private HBox createRatingProgressBar(String labelText, double fillPercent, String percentageText) {

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
        bar.prefWidthProperty().bind(progressContainer.widthProperty().multiply(fillPercent));

        progressContainer.getChildren().addAll(background, bar);

        Label percentLabel = new Label(percentageText);
        percentLabel.setPrefWidth(35);
        percentLabel.setAlignment(Pos.CENTER_RIGHT);
        percentLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + SECONDARY_TEXT + ";");

        row.getChildren().addAll(label, progressContainer, percentLabel);

        return row;
    }

    private VBox createMetricRow(String label, String value, String color) {

        VBox box = new VBox(2);
        box.setPadding(new Insets(6, 10, 6, 10));
        box.setStyle("-fx-background-color: " + LIGHT_BACKGROUND + "; -fx-background-radius: 8;");

        Label title = new Label(label);
        title.setStyle("-fx-font-size: 11px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        Label val = new Label(value);
        val.setStyle("-fx-font-size: 14px; -fx-font-weight: 800; -fx-text-fill: " + color + ";");

        box.getChildren().addAll(title, val);

        return box;
    }

    // =========================================================
    // FILTERS & CONTROLS
    // =========================================================

    private HBox createFilterAndSearchSection() {

        HBox filterBox = new HBox(12);
        filterBox.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> departmentFilter = new ComboBox<>();
        departmentFilter.getItems().addAll("All Departments", "Cardiology", "Neurology", "Orthopedics", "Pediatrics", "Emergency");
        departmentFilter.setValue("All Departments");
        departmentFilter.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: " + BORDER + "; -fx-border-radius: 6;");

        ComboBox<String> ratingFilter = new ComboBox<>();
        ratingFilter.getItems().addAll("All Ratings", "5 Stars Only", "4 Stars & Above", "Critical (1-2 Stars)");
        ratingFilter.setValue("All Ratings");
        ratingFilter.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: " + BORDER + "; -fx-border-radius: 6;");

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

        filterBox.getChildren().addAll(departmentFilter, ratingFilter, spacer, exportBtn);

        return filterBox;
    }

    // =========================================================
    // REVIEWS LIST SECTION
    // =========================================================

    private VBox createReviewListSection() {

        reviewListContainer = new VBox(16);

        reviewListContainer.getChildren().addAll(
                createReviewCard(
                        "Ananya Roy",
                        "2 hours ago",
                        "Cardiology • Dr. R. K. Sharma",
                        5,
                        "Excellent Care & Swift Support",
                        "The medical team at Cardiology was exceptionally attentive during my treatment. Doctor Sharma explained the procedure thoroughly and put my mind at ease.",
                        true
                ),
                createReviewCard(
                        "Vikram Mehta",
                        "Yesterday",
                        "Emergency Dept",
                        2,
                        "Long Waiting Time in Emergency ER",
                        "The doctors were professional, but we had to wait over 45 minutes in the emergency admission room before being seen. System needs streamlining.",
                        false
                ),
                createReviewCard(
                        "Siddharth Patel",
                        "3 days ago",
                        "Orthopedics • Dr. Priya Verma",
                        5,
                        "Great facility and hygienic environment",
                        "Clean rooms, modern equipment, and highly supportive nursing staff during my knee rehabilitation.",
                        true
                )
        );

        return reviewListContainer;
    }

    private VBox createReviewCard(String patientName, String dateText, String category, int rating, String titleText, String reviewBody, boolean verified) {

        VBox card = createCard();

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Circle avatar = new Circle(18);
        avatar.setFill(Color.web(PRIMARY_LIGHT));

        Label avatarLabel = new Label(patientName.substring(0, 1));
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

        return card;
    }

    // =========================================================
    // HELPER UTILITIES & DIALOGS
    // =========================================================

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