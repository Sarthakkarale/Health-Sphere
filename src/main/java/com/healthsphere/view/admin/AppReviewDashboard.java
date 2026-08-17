package com.healthsphere.view.admin;

import com.healthsphere.controller.admin.ApplicationReviewController;
import com.healthsphere.model.ApplicationReview;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AppReviewDashboard {

    private final Stage stage;
    private final ApplicationReviewController applicationReviewController;

    public AppReviewDashboard(Stage stage) {
        this.stage = stage;
        this.applicationReviewController =
                new ApplicationReviewController();
    }

    public Scene getScene() {
        return new Scene(getContent());
    }

    // UI model used by the existing JavaFX table.
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

            this.username = username;
            this.userRole = userRole;
            this.rating = rating;
            this.comment = comment;
            this.date = date;
            this.appVersion = appVersion;
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

    private final ObservableList<Review> reviewData =
            FXCollections.observableArrayList();

    private FilteredList<Review> filteredData;

    public Parent getContent() {

        // Load actual reviews from Firestore.
        loadReviewData();

        VBox rootLayout = new VBox(20);
        rootLayout.setPadding(new Insets(24));
        rootLayout.setStyle("-fx-background-color: #F8FAFC;");

        // Header Title Section
        Label headerTitle =
                new Label("Patient & Doctor Platform Reviews");

        headerTitle.setStyle(
                "-fx-font-size: 24px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: #0F172A;"
        );

        Label headerSub =
                new Label(
                        "Monitor ratings, feedback, and system experience " +
                        "across medical practitioners and patients."
                );

        headerSub.setStyle(
                "-fx-font-size: 13px; " +
                "-fx-text-fill: #64748B;"
        );

        VBox headerBox =
                new VBox(4, headerTitle, headerSub);

        // Top Analytics Section
        HBox topAnalytics =
                new HBox(
                        20,
                        createSummaryCards(),
                        createRatingChart()
                );

        topAnalytics.setAlignment(Pos.CENTER_LEFT);

        // Search and Filter Bar
        HBox filterBar = createFilterBar();

        // Reviews Table View
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
                new ScrollPane(rootLayout);

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        scrollPane.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-background: #F8FAFC;"
        );

        return scrollPane;
    }

    private HBox createSummaryCards() {

        VBox avgCard =
                createCard(
                        "Average Rating",
                        calculateAverageRating(),
                        "#2563EB"
                );

        VBox totalCard =
                createCard(
                        "Total Feedback",
                        String.valueOf(reviewData.size()),
                        "#059669"
                );

        VBox posCard =
                createCard(
                        "Satisfaction Rate",
                        calculateSatisfactionRate(),
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

    private VBox createCard(
            String title,
            String value,
            String accentColor) {

        VBox card = new VBox(8);

        card.setPadding(
                new Insets(16)
        );

        card.setPrefSize(
                180,
                100
        );

        card.setStyle(
                "-fx-background-color: #FFFFFF; " +
                "-fx-background-radius: 12; " +
                "-fx-border-color: #E2E8F0; " +
                "-fx-border-radius: 12; " +
                "-fx-effect: dropshadow(three-pass-box, " +
                "rgba(0,0,0,0.03), 8, 0, 0, 2);"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 13px; " +
                "-fx-text-fill: #64748B; " +
                "-fx-font-weight: 500;"
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-font-size: 26px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: " +
                accentColor +
                ";"
        );

        card.getChildren().addAll(
                titleLabel,
                valueLabel
        );

        return card;
    }

    private BarChart<Number, String> createRatingChart() {

        NumberAxis xAxis =
                new NumberAxis();

        CategoryAxis yAxis =
                new CategoryAxis();

        xAxis.setLabel("Count");
        yAxis.setLabel("Stars");

        BarChart<Number, String> chart =
                new BarChart<>(
                        xAxis,
                        yAxis
                );

        chart.setLegendVisible(false);

        chart.setPrefSize(
                400,
                150
        );

        chart.setTitle(
                "Rating Breakdown"
        );

        chart.setStyle(
                "-fx-background-color: #FFFFFF; " +
                "-fx-background-radius: 12; " +
                "-fx-border-color: #E2E8F0; " +
                "-fx-border-radius: 12; " +
                "-fx-padding: 10;"
        );

        int[] ratingCounts =
                new int[5];

        for (Review review : reviewData) {

            int rating =
                    review.getRating();

            if (rating >= 1 &&
                    rating <= 5) {

                ratingCounts[rating - 1]++;
            }
        }

        XYChart.Series<Number, String> series =
                new XYChart.Series<>();

        series.getData().add(
                new XYChart.Data<>(
                        ratingCounts[4],
                        "5 ★"
                )
        );

        series.getData().add(
                new XYChart.Data<>(
                        ratingCounts[3],
                        "4 ★"
                )
        );

        series.getData().add(
                new XYChart.Data<>(
                        ratingCounts[2],
                        "3 ★"
                )
        );

        series.getData().add(
                new XYChart.Data<>(
                        ratingCounts[1],
                        "2 ★"
                )
        );

        series.getData().add(
                new XYChart.Data<>(
                        ratingCounts[0],
                        "1 ★"
                )
        );

        chart.getData().add(
                series
        );

        return chart;
    }

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
                "-fx-background-radius: 8; " +
                "-fx-border-radius: 8; " +
                "-fx-border-color: #CBD5E1; " +
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
                "-fx-background-radius: 8; " +
                "-fx-border-radius: 8; " +
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
                "-fx-background-radius: 8; " +
                "-fx-border-radius: 8; " +
                "-fx-border-color: #CBD5E1;"
        );

        filteredData =
                new FilteredList<>(
                        reviewData,
                        p -> true
                );

        searchField.textProperty()
                .addListener(
                        (obs, oldVal, newVal) ->
                                applyFilter(
                                        newVal,
                                        roleFilter.getValue(),
                                        ratingFilter.getValue()
                                )
                );

        roleFilter.valueProperty()
                .addListener(
                        (obs, oldVal, newVal) ->
                                applyFilter(
                                        searchField.getText(),
                                        newVal,
                                        ratingFilter.getValue()
                                )
                );

        ratingFilter.valueProperty()
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

    private void applyFilter(
            String searchText,
            String selectedRole,
            String selectedRating) {

        if (filteredData == null) {
            return;
        }

        filteredData.setPredicate(
                review -> {

                    String comment =
                            review.getComment() == null
                                    ? ""
                                    : review.getComment();

                    String username =
                            review.getUsername() == null
                                    ? ""
                                    : review.getUsername();

                    String role =
                            review.getUserRole() == null
                                    ? ""
                                    : review.getUserRole();

                    boolean matchesSearch =
                            searchText == null ||
                            searchText.isEmpty() ||
                            comment
                                    .toLowerCase()
                                    .contains(
                                            searchText.toLowerCase()
                                    ) ||
                            username
                                    .toLowerCase()
                                    .contains(
                                            searchText.toLowerCase()
                                    ) ||
                            role
                                    .toLowerCase()
                                    .contains(
                                            searchText.toLowerCase()
                                    );

                    boolean matchesRole =
                            selectedRole == null ||
                            selectedRole.equals("All Roles") ||
                            role.equalsIgnoreCase(
                                    selectedRole
                            );

                    boolean matchesRating =
                            true;

                    if (selectedRating != null &&
                            !selectedRating.equals(
                                    "All Ratings"
                            )) {

                        int targetStars =
                                Integer.parseInt(
                                        selectedRating
                                                .split(" ")[0]
                                );

                        matchesRating =
                                review.getRating() ==
                                        targetStars;
                    }

                    return matchesSearch &&
                            matchesRole &&
                            matchesRating;
                }
        );
    }

    private TableView<Review> createReviewTable() {

        TableView<Review> table =
                new TableView<>();

        table.setItems(
                filteredData
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        table.setStyle(
                "-fx-background-color: #FFFFFF; " +
                "-fx-background-radius: 12; " +
                "-fx-border-color: #E2E8F0; " +
                "-fx-border-radius: 12;"
        );

        TableColumn<Review, String> userCol =
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

        TableColumn<Review, String> roleCol =
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

                                if (empty ||
                                        role == null) {

                                    setText(null);
                                    setStyle("");

                                } else {

                                    setText(role);

                                    if (role.equalsIgnoreCase(
                                            "Doctor"
                                    )) {

                                        setStyle(
                                                "-fx-text-fill: #0284C7; " +
                                                "-fx-font-weight: bold;"
                                        );

                                    } else {

                                        setStyle(
                                                "-fx-text-fill: #059669; " +
                                                "-fx-font-weight: bold;"
                                        );
                                    }
                                }
                            }
                        }
        );

        TableColumn<Review, Integer> ratingCol =
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

                                if (empty ||
                                        rating == null) {

                                    setText(null);

                                } else {

                                    setText(
                                            "★".repeat(rating) +
                                            "☆".repeat(
                                                    5 - rating
                                            )
                                    );

                                    setStyle(
                                            "-fx-text-fill: #F59E0B; " +
                                            "-fx-font-weight: bold; " +
                                            "-fx-font-size: 14px;"
                                    );
                                }
                            }
                        }
        );

        TableColumn<Review, String> commentCol =
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

        TableColumn<Review, LocalDate> dateCol =
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

        TableColumn<Review, String> versionCol =
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

    /**
     * Load actual ApplicationReview records from Firestore.
     */
    private void loadReviewData() {

        reviewData.clear();

        try {

            List<ApplicationReview> reviews =
                    applicationReviewController
                            .getAllReviews();

            if (reviews == null) {
                return;
            }

            for (ApplicationReview review :
                    reviews) {

                if (review == null) {
                    continue;
                }

                String username =
                        safe(
                                review.getApplicantName()
                        );

                String role =
                        safe(
                                review.getApplicantRole()
                        );

                String comment =
                        safe(
                                review.getReviewText()
                        );

                LocalDate date =
                        convertDate(
                                review.getCreatedAt()
                        );

                // The current ApplicationReview model
                // does not contain an appVersion field.
                String appVersion = "N/A";

                reviewData.add(
                        new Review(
                                username,
                                role,
                                review.getRating(),
                                comment,
                                date,
                                appVersion
                        )
                );
            }

        } catch (RuntimeException e) {

            e.printStackTrace();

            showAlert(
                    "Application Review Error",
                    "Unable to load application reviews from Firestore.\n\n"
                            + e.getMessage()
            );
        }
    }

    private LocalDate convertDate(
            LocalDateTime dateTime) {

        if (dateTime == null) {
            return null;
        }

        return dateTime.toLocalDate();
    }

    private String calculateAverageRating() {

        if (reviewData.isEmpty()) {
            return "0.0 ★";
        }

        double total = 0;

        for (Review review :
                reviewData) {

            total += review.getRating();
        }

        double average =
                total / reviewData.size();

        return String.format(
                "%.1f ★",
                average
        );
    }

    private String calculateSatisfactionRate() {

        if (reviewData.isEmpty()) {
            return "0%";
        }

        int positive = 0;

        for (Review review :
                reviewData) {

            if (review.getRating() >= 4) {
                positive++;
            }
        }

        double rate =
                (positive * 100.0) /
                reviewData.size();

        return String.format(
                "%.0f%%",
                rate
        );
    }

    private String safe(String value) {

        return value == null
                ? ""
                : value;
    }

    private void showAlert(
            String title,
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}