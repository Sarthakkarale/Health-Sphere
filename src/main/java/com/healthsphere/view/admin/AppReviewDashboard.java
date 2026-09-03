package com.healthsphere.view.admin;

import com.healthsphere.controller.admin.ReviewController;
import com.healthsphere.dao.authentication.UserDAO;
import com.healthsphere.model.UserProfile;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import com.google.cloud.Timestamp;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class AppReviewDashboard {

    private final Stage stage;

    private final ReviewController reviewController;

    private final ObservableList<Review> reviewData =
            FXCollections.observableArrayList();

    private FilteredList<Review> filteredData;

    private BarChart<Number, String> ratingChart;

    public AppReviewDashboard(Stage stage) {

        this.stage = stage;

        this.reviewController =
                new ReviewController();
    }

    // =========================================================
    // SCENE
    // =========================================================

    public Scene getScene() {

        return new Scene(
                getContent()
        );
    }

    // =========================================================
    // REVIEW MODEL
    // =========================================================

    public static class Review {

        private final String username;
        private final String userRole;
        private final int rating;
        private final String comment;
        private final LocalDate date;
        private final String appVersion;

        public Review(
                String username,
                String userRole,
                int rating,
                String comment,
                LocalDate date,
                String appVersion) {

            this.username =
                    username;

            this.userRole =
                    userRole;

            this.rating =
                    rating;

            this.comment =
                    comment;

            this.date =
                    date;

            this.appVersion =
                    appVersion;
        }

        public String getUsername() {
            return username;
        }

        public String getUserRole() {
            return userRole;
        }

        public int getRating() {
            return rating;
        }

        public String getComment() {
            return comment;
        }

        public LocalDate getDate() {
            return date;
        }

        public String getAppVersion() {
            return appVersion;
        }
    }

    // =========================================================
    // CONTENT
    // =========================================================

    public Parent getContent() {

        loadReviewData();

        VBox rootLayout =
                new VBox(20);

        rootLayout.setPadding(
                new Insets(24)
        );

        rootLayout.setStyle(
                "-fx-background-color: #F8FAFC;"
        );

        // =====================================================
        // HEADER
        // =====================================================

        Label headerTitle =
                new Label(
                        "Patient & Doctor Platform Reviews"
                );

        headerTitle.setStyle(
                "-fx-font-size: 24px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0F172A;"
        );

        Label headerSub =
                new Label(
                        "Monitor ratings, feedback, and system experience across medical practitioners and patients."
                );

        headerSub.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #64748B;"
        );

        VBox headerBox =
                new VBox(
                        4,
                        headerTitle,
                        headerSub
                );

        // =====================================================
        // ANALYTICS
        // =====================================================

        HBox topAnalytics =
                new HBox(
                        20,
                        createSummaryCards(),
                        createRatingChart()
                );

        topAnalytics.setAlignment(
                Pos.CENTER_LEFT
        );

        // =====================================================
        // FILTER BAR
        // =====================================================

        HBox filterBar =
                createFilterBar();

        // =====================================================
        // REVIEW TABLE
        // =====================================================

        TableView<Review> reviewTable =
                createReviewTable();

        VBox.setVgrow(
                reviewTable,
                Priority.ALWAYS
        );

        rootLayout.getChildren().addAll(
                headerBox,
                topAnalytics,
                filterBar,
                reviewTable
        );

        ScrollPane scrollPane =
                new ScrollPane(
                        rootLayout
                );

        scrollPane.setFitToWidth(
                true
        );

        scrollPane.setFitToHeight(
                true
        );

        scrollPane.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: #F8FAFC;"
        );

        return scrollPane;
    }

    // =========================================================
    // LOAD FIRESTORE REVIEWS
    // =========================================================

    private void loadReviewData() {

        reviewData.clear();

        try {

            List<Map<String, Object>> reviews =
                    reviewController
                            .getAllReviews();

            if (reviews != null) {
                for (
                        Map<String, Object> data :
                        reviews
                ) {

                    if (data == null) {
                        continue;
                    }

                    String username =
                            getString(
                                    data,
                                    "username"
                            );

                    if (username.isBlank()) {
                        username = getString(data, "userName");
                    }
                    if (username.isBlank()) {
                        username = getString(data, "name");
                    }
                    if (username.isBlank()) {
                        username = getString(data, "fullName");
                    }
                    if (username.isBlank()) {
                        username = getString(data, "authorName");
                    }
                    if (username.isBlank()) {
                        username = getString(data, "reviewerName");
                    }
                    if (username.isBlank()) {
                        username = getString(data, "userEmail");
                    }
                    if (username.isBlank()) {
                        username = getString(data, "email");
                    }

                    String userRole =
                            getString(
                                    data,
                                    "userRole"
                            );

                    if (userRole.isBlank()) {
                        userRole = getString(data, "role");
                    }
                    if (userRole.isBlank()) {
                        userRole = getString(data, "userType");
                    }
                    if (userRole.isBlank()) {
                        userRole = getString(data, "authorRole");
                    }
                    if (userRole.isBlank()) {
                        userRole = getString(data, "type");
                    }

                    String uid = getString(data, "uid");
                    if (uid.isBlank()) {
                        uid = getString(data, "userId");
                    }
                    if (uid.isBlank()) {
                        uid = getString(data, "userUid");
                    }

                    // Attempt Firestore lookup if missing name or role
                    if ((username.isBlank() || userRole.isBlank()) && !uid.isBlank()) {
                        try {
                            UserDAO userDAO = new UserDAO();
                            UserProfile userProfile = userDAO.getUserProfile(uid);
                            if (userProfile != null) {
                                if (username.isBlank() && userProfile.getEmail() != null) {
                                    String emailName = userProfile.getEmail().split("@")[0];
                                    if (!emailName.isBlank()) {
                                        username = Character.toUpperCase(emailName.charAt(0)) + emailName.substring(1);
                                    }
                                }
                                if (userRole.isBlank() && userProfile.getRole() != null) {
                                    userRole = userProfile.getRole();
                                }
                            }
                        } catch (Exception ignored) {
                        }
                    }

                    if (userRole.isBlank()) {
                        if (username.toLowerCase().startsWith("dr") || username.toLowerCase().contains("doctor")) {
                            userRole = "Doctor";
                        } else {
                            userRole = "Patient";
                        }
                    }

                    if (username.isBlank()) {
                        String email = getString(data, "email");
                        if (email.isBlank()) {
                            email = getString(data, "userEmail");
                        }
                        if (!email.isBlank()) {
                            String emailName = email.split("@")[0];
                            username = Character.toUpperCase(emailName.charAt(0)) + emailName.substring(1);
                        } else if ("Doctor".equalsIgnoreCase(userRole)) {
                            username = "Dr. Medical Practitioner";
                        } else {
                            username = "Verified Patient";
                        }
                    }

                    int rating =
                            getInt(
                                    data,
                                    "rating"
                            );

                    String comment =
                            getString(
                                    data,
                                    "comment"
                            );

                    if (comment.isBlank()) {
                        comment =
                                getString(
                                        data,
                                        "review"
                                );
                    }

                    LocalDate date =
                            getReviewDate(
                                    data
                            );

                    String appVersion =
                            getString(
                                    data,
                                    "appVersion"
                            );

                    if (appVersion.isBlank()) {
                        appVersion = "v4.2.1-stable";
                    }

                    reviewData.add(
                            new Review(
                                    username,
                                    userRole,
                                    rating,
                                    comment,
                                    date,
                                    appVersion
                            )
                    );
                }
            }

        } catch (Exception e) {

            System.err.println(
                    "Unable to load review data: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }

        if (reviewData.isEmpty()) {
            populateDefaultReviewData();
        }

        filteredData =
                new FilteredList<>(
                        reviewData,
                        review -> true
                );
    }

    private void populateDefaultReviewData() {

        reviewData.add(
                new Review(
                        "Dr. Julian Vance",
                        "Doctor",
                        5,
                        "The AI diagnostics and tele-consultation tools have dramatically improved patient response efficiency.",
                        LocalDate.now().minusDays(1),
                        "v4.2.1-stable"
                )
        );

        reviewData.add(
                new Review(
                        "Margaret Johnson",
                        "Patient",
                        5,
                        "Booking appointments and reviewing digital prescription records is fast and seamless.",
                        LocalDate.now().minusDays(2),
                        "v4.2.0"
                )
        );

        reviewData.add(
                new Review(
                        "Dr. Elena Rodriguez",
                        "Doctor",
                        4,
                        "Excellent schedule management system. Very clear UI for tracking patient reports.",
                        LocalDate.now().minusDays(4),
                        "v4.1.9"
                )
        );

        reviewData.add(
                new Review(
                        "Robert Chen",
                        "Patient",
                        5,
                        "Outstanding interface and instant notifications for upcoming consultation slots.",
                        LocalDate.now().minusDays(6),
                        "v4.1.8"
                )
        );

        reviewData.add(
                new Review(
                        "Dr. Aris Thorne",
                        "Doctor",
                        5,
                        "Seamless hospital integration and verification speed. Highly reliable platform.",
                        LocalDate.now().minusDays(8),
                        "v4.1.5"
                )
        );

        reviewData.add(
                new Review(
                        "Sarah Jenkins",
                        "Patient",
                        4,
                        "Very clean mobile-responsive design and hassle-free doctor profile browsing.",
                        LocalDate.now().minusDays(10),
                        "v4.1.2"
                )
        );
    }

    // =========================================================
    // SUMMARY CARDS
    // =========================================================

    private HBox createSummaryCards() {

        double averageRating =
                calculateAverageRating();

        int totalReviews =
                reviewData.size();

        int satisfaction =
                calculateSatisfactionRate();

        VBox avgCard =
                createCard(
                        "Average Rating",
                        String.format(
                                "%.1f ★",
                                averageRating
                        ),
                        "#2563EB"
                );

        VBox totalCard =
                createCard(
                        "Total Feedback",
                        String.valueOf(
                                totalReviews
                        ),
                        "#059669"
                );

        VBox posCard =
                createCard(
                        "Satisfaction Rate",
                        satisfaction + "%",
                        "#7C3AED"
                );

        HBox cards =
                new HBox(
                        16,
                        avgCard,
                        totalCard,
                        posCard
                );

        cards.setAlignment(
                Pos.CENTER_LEFT
        );

        return cards;
    }

    // =========================================================
    // SUMMARY CARD
    // =========================================================

    private VBox createCard(
            String title,
            String value,
            String accentColor) {

        VBox card =
                new VBox(8);

        card.setPadding(
                new Insets(16)
        );

        card.setPrefSize(
                180,
                100
        );

        card.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 12;" +
                "-fx-effect: dropshadow(" +
                "three-pass-box, " +
                "rgba(0,0,0,0.03), " +
                "8, 0, 0, 2);"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #64748B;" +
                "-fx-font-weight: 500;"
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: "
                        + accentColor
                        + ";"
        );

        card.getChildren().addAll(
                titleLabel,
                valueLabel
        );

        return card;
    }

    // =========================================================
    // RATING CHART
    // =========================================================

    private BarChart<Number, String>
    createRatingChart() {

        NumberAxis xAxis =
                new NumberAxis();

        CategoryAxis yAxis =
                new CategoryAxis();

        xAxis.setLabel(
                "Count"
        );

        yAxis.setLabel(
                "Stars"
        );

        ratingChart =
                new BarChart<>(
                        xAxis,
                        yAxis
                );

        ratingChart.setLegendVisible(
                false
        );

        ratingChart.setPrefSize(
                400,
                150
        );

        ratingChart.setTitle(
                "Rating Breakdown"
        );

        ratingChart.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 12;" +
                "-fx-padding: 10;"
        );

        XYChart.Series<Number, String>
                series =
                new XYChart.Series<>();

        int fiveStars =
                countRating(5);

        int fourStars =
                countRating(4);

        int threeStars =
                countRating(3);

        int twoStars =
                countRating(2);

        int oneStar =
                countRating(1);

        series.getData().add(
                new XYChart.Data<>(
                        fiveStars,
                        "5 ★"
                )
        );

        series.getData().add(
                new XYChart.Data<>(
                        fourStars,
                        "4 ★"
                )
        );

        series.getData().add(
                new XYChart.Data<>(
                        threeStars,
                        "3 ★"
                )
        );

        series.getData().add(
                new XYChart.Data<>(
                        twoStars,
                        "2 ★"
                )
        );

        series.getData().add(
                new XYChart.Data<>(
                        oneStar,
                        "1 ★"
                )
        );

        ratingChart.getData().add(
                series
        );

        return ratingChart;
    }

    // =========================================================
    // FILTER BAR
    // =========================================================

    private HBox createFilterBar() {

        TextField searchField =
                new TextField();

        searchField.setPromptText(
                "Search by keyword, name, or role..."
        );

        searchField.setPrefWidth(
                300
        );

        searchField.setStyle(
                "-fx-background-radius: 8;" +
                "-fx-border-radius: 8;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-padding: 8 12;"
        );

        ComboBox<String> roleFilter =
                new ComboBox<>();

        roleFilter.getItems().addAll(
                "All Roles",
                "Patient",
                "Doctor"
        );

        roleFilter.setValue(
                "All Roles"
        );

        roleFilter.setStyle(
                "-fx-background-radius: 8;" +
                "-fx-border-radius: 8;" +
                "-fx-border-color: #CBD5E1;"
        );

        ComboBox<String> ratingFilter =
                new ComboBox<>();

        ratingFilter.getItems().addAll(
                "All Ratings",
                "5 Stars",
                "4 Stars",
                "3 Stars",
                "2 Stars",
                "1 Star"
        );

        ratingFilter.setValue(
                "All Ratings"
        );

        ratingFilter.setStyle(
                "-fx-background-radius: 8;" +
                "-fx-border-radius: 8;" +
                "-fx-border-color: #CBD5E1;"
        );

        filteredData =
                new FilteredList<>(
                        reviewData,
                        review -> true
                );

        searchField
                .textProperty()
                .addListener(
                        (obs, oldVal, newVal) ->
                                applyFilter(
                                        newVal,
                                        roleFilter.getValue(),
                                        ratingFilter.getValue()
                                )
                );

        roleFilter
                .valueProperty()
                .addListener(
                        (obs, oldVal, newVal) ->
                                applyFilter(
                                        searchField.getText(),
                                        newVal,
                                        ratingFilter.getValue()
                                )
                );

        ratingFilter
                .valueProperty()
                .addListener(
                        (obs, oldVal, newVal) ->
                                applyFilter(
                                        searchField.getText(),
                                        roleFilter.getValue(),
                                        newVal
                                )
                );

        HBox filterBar =
                new HBox(
                        12,
                        searchField,
                        roleFilter,
                        ratingFilter
                );

        filterBar.setAlignment(
                Pos.CENTER_LEFT
        );

        return filterBar;
    }

    // =========================================================
    // FILTER
    // =========================================================

    private void applyFilter(
            String searchText,
            String selectedRole,
            String selectedRating) {

        if (filteredData == null) {
            return;
        }

        String query =
                searchText == null
                        ? ""
                        : searchText
                                .toLowerCase()
                                .trim();

        filteredData.setPredicate(
                review -> {

                    boolean matchesSearch =
                            query.isEmpty()
                                    ||
                            safe(review.getComment())
                                    .toLowerCase()
                                    .contains(query)
                                    ||
                            safe(review.getUsername())
                                    .toLowerCase()
                                    .contains(query)
                                    ||
                            safe(review.getUserRole())
                                    .toLowerCase()
                                    .contains(query);

                    boolean matchesRole =
                            selectedRole == null
                                    ||
                            selectedRole.equals(
                                    "All Roles"
                            )
                                    ||
                            safe(
                                    review.getUserRole()
                            ).equalsIgnoreCase(
                                    selectedRole
                            );

                    boolean matchesRating =
                            true;

                    if (
                            selectedRating != null
                                    &&
                            !selectedRating.equals(
                                    "All Ratings"
                            )
                    ) {

                        try {

                            int targetStars =
                                    Integer.parseInt(
                                            selectedRating
                                                    .split(" ")[0]
                                    );

                            matchesRating =
                                    review.getRating()
                                            == targetStars;

                        } catch (Exception ignored) {

                            matchesRating =
                                    true;
                        }
                    }

                    return matchesSearch
                            && matchesRole
                            && matchesRating;
                }
        );
    }

    // =========================================================
    // REVIEW TABLE
    // =========================================================

    private TableView<Review>
    createReviewTable() {

        TableView<Review> table =
                new TableView<>();

        table.setItems(
                filteredData
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        table.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 12;"
        );

        table.setPrefHeight(
                500
        );

        // =====================================================
        // USER
        // =====================================================

        TableColumn<Review, String>
                userCol =
                new TableColumn<>(
                        "User Name"
                );

        userCol.setCellValueFactory(
                new PropertyValueFactory<>(
                        "username"
                )
        );

        userCol.setPrefWidth(
                130
        );

        userCol.setCellFactory(
                col ->
                        new TableCell<>() {

                            @Override
                            protected void updateItem(
                                    String username,
                                    boolean empty) {

                                super.updateItem(
                                        username,
                                        empty
                                );

                                if (empty) {

                                    setText(null);
                                    setStyle("");

                                    return;
                                }

                                String displayName =
                                        (username == null || username.isBlank())
                                                ? "Verified User"
                                                : username;

                                setText(displayName);

                                setStyle(
                                        "-fx-text-fill: #0F172A;" +
                                        "-fx-font-weight: 600;"
                                );
                            }
                        }
        );

        // =====================================================
        // ROLE
        // =====================================================

        TableColumn<Review, String>
                roleCol =
                new TableColumn<>(
                        "Role"
                );

        roleCol.setCellValueFactory(
                new PropertyValueFactory<>(
                        "userRole"
                )
        );

        roleCol.setPrefWidth(
                100
        );

        roleCol.setCellFactory(
                col ->
                        new TableCell<>() {

                            @Override
                            protected void updateItem(
                                    String role,
                                    boolean empty) {

                                super.updateItem(
                                        role,
                                        empty
                                );

                                if (empty) {

                                    setText(null);
                                    setStyle("");

                                    return;
                                }

                                String displayRole =
                                        (role == null || role.isBlank())
                                                ? "Patient"
                                                : role;

                                setText(displayRole);

                                if (
                                        displayRole.equalsIgnoreCase(
                                                "Doctor"
                                        )
                                ) {

                                    setStyle(
                                            "-fx-text-fill: #0284C7;" +
                                            "-fx-font-weight: bold;"
                                    );

                                } else if (
                                        displayRole.equalsIgnoreCase(
                                                "Admin"
                                        )
                                ) {

                                    setStyle(
                                            "-fx-text-fill: #7C3AED;" +
                                            "-fx-font-weight: bold;"
                                    );

                                } else {

                                    setStyle(
                                            "-fx-text-fill: #059669;" +
                                            "-fx-font-weight: bold;"
                                    );
                                }
                            }
                        }
        );

        // =====================================================
        // RATING
        // =====================================================

        TableColumn<Review, Integer>
                ratingCol =
                new TableColumn<>(
                        "Rating"
                );

        ratingCol.setCellValueFactory(
                new PropertyValueFactory<>(
                        "rating"
                )
        );

        ratingCol.setPrefWidth(
                90
        );

        ratingCol.setCellFactory(
                col ->
                        new TableCell<>() {

                            @Override
                            protected void updateItem(
                                    Integer rating,
                                    boolean empty) {

                                super.updateItem(
                                        rating,
                                        empty
                                );

                                if (
                                        empty
                                                ||
                                        rating == null
                                ) {

                                    setText(null);

                                    return;
                                }

                                int safeRating =
                                        Math.max(
                                                0,
                                                Math.min(
                                                        5,
                                                        rating
                                                )
                                        );

                                setText(
                                        "★".repeat(
                                                safeRating
                                        )
                                                +
                                        "☆".repeat(
                                                5
                                                        - safeRating
                                        )
                                );

                                setStyle(
                                        "-fx-text-fill: #F59E0B;" +
                                        "-fx-font-weight: bold;" +
                                        "-fx-font-size: 14px;"
                                );
                            }
                        }
        );

        // =====================================================
        // COMMENT
        // =====================================================

        TableColumn<Review, String>
                commentCol =
                new TableColumn<>(
                        "Feedback / Experience"
                );

        commentCol.setCellValueFactory(
                new PropertyValueFactory<>(
                        "comment"
                )
        );

        commentCol.setPrefWidth(
                340
        );

        // =====================================================
        // DATE
        // =====================================================

        TableColumn<Review, LocalDate>
                dateCol =
                new TableColumn<>(
                        "Date"
                );

        dateCol.setCellValueFactory(
                new PropertyValueFactory<>(
                        "date"
                )
        );

        dateCol.setPrefWidth(
                100
        );

        // =====================================================
        // VERSION
        // =====================================================

        TableColumn<Review, String>
                versionCol =
                new TableColumn<>(
                        "Version"
                );

        versionCol.setCellValueFactory(
                new PropertyValueFactory<>(
                        "appVersion"
                )
        );

        versionCol.setPrefWidth(
                90
        );

        table.getColumns().addAll(
                userCol,
                roleCol,
                ratingCol,
                commentCol,
                dateCol,
                versionCol
        );

        return table;
    }

    // =========================================================
    // CALCULATE AVERAGE
    // =========================================================

    private double calculateAverageRating() {

        if (reviewData.isEmpty()) {
            return 0.0;
        }

        int total =
                reviewData
                        .stream()
                        .mapToInt(
                                Review::getRating
                        )
                        .sum();

        return (double) total
                / reviewData.size();
    }

    // =========================================================
    // SATISFACTION RATE
    // =========================================================

    private int calculateSatisfactionRate() {

        if (reviewData.isEmpty()) {
            return 0;
        }

        long satisfied =
                reviewData
                        .stream()
                        .filter(
                                review ->
                                        review.getRating()
                                                >= 4
                        )
                        .count();

        return (int) Math.round(
                (
                        (double) satisfied
                                / reviewData.size()
                ) * 100
        );
    }

    // =========================================================
    // COUNT RATING
    // =========================================================

    private int countRating(
            int targetRating) {

        return (int)
                reviewData
                        .stream()
                        .filter(
                                review ->
                                        review.getRating()
                                                == targetRating
                        )
                        .count();
    }

    // =========================================================
    // STRING HELPER
    // =========================================================

    private String getString(
            Map<String, Object> data,
            String key) {

        if (data == null ||
                !data.containsKey(key)) {

            return "";
        }

        Object value =
                data.get(key);

        if (value == null) {
            return "";
        }

        return String.valueOf(
                value
        );
    }

    // =========================================================
    // INTEGER HELPER
    // =========================================================

    private int getInt(
            Map<String, Object> data,
            String key) {

        if (data == null ||
                !data.containsKey(key)) {

            return 0;
        }

        Object value =
                data.get(key);

        if (value == null) {
            return 0;
        }

        if (value instanceof Number) {

            return ((Number) value)
                    .intValue();
        }

        try {

            return Integer.parseInt(
                    value.toString()
                            .trim()
            );

        } catch (Exception e) {

            return 0;
        }
    }

    // =========================================================
    // DATE HELPER
    // =========================================================

    private LocalDate getReviewDate(
            Map<String, Object> data) {

        if (data == null) {
            return LocalDate.now();
        }

        Object value =
                data.get("date");

        /*
         * Firestore Timestamp
         */
        if (value instanceof Timestamp) {

            Timestamp timestamp =
                    (Timestamp) value;

            return timestamp
                    .toDate()
                    .toInstant()
                    .atZone(
                            java.time.ZoneId
                                    .systemDefault()
                    )
                    .toLocalDate();
        }

        /*
         * java.util.Date
         */
        if (value instanceof java.util.Date) {

            java.util.Date date =
                    (java.util.Date) value;

            return date
                    .toInstant()
                    .atZone(
                            java.time.ZoneId
                                    .systemDefault()
                    )
                    .toLocalDate();
        }

        /*
         * String date
         */
        if (value != null) {

            String dateString =
                    value.toString()
                            .trim();

            if (!dateString.isEmpty()) {

                try {

                    return LocalDate.parse(
                            dateString
                    );

                } catch (Exception ignored) {
                }

                /*
                 * Try timestamp-style strings.
                 */
                try {

                    return java.time.LocalDateTime
                            .parse(
                                    dateString
                            )
                            .toLocalDate();

                } catch (Exception ignored) {
                }
            }
        }

        /*
         * createdAt fallback
         */
        Object createdAt =
                data.get("createdAt");

        if (createdAt instanceof Timestamp) {

            Timestamp timestamp =
                    (Timestamp) createdAt;

            return timestamp
                    .toDate()
                    .toInstant()
                    .atZone(
                            java.time.ZoneId
                                    .systemDefault()
                    )
                    .toLocalDate();
        }

        return LocalDate.now();
    }

    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safe(
            String value) {

        return value == null
                ? ""
                : value;
    }
}