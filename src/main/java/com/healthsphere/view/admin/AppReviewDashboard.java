package com.healthsphere.view.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene; // Added import
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage; // Added import

import java.time.LocalDate;

public class AppReviewDashboard {

    private final Stage stage;

    public AppReviewDashboard(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        return new Scene(getContent());
    }

    // Inner Model Class for Review
    public static class Review {
        private final String username;
        private final int rating;
        private final String comment;
        private final LocalDate date;
        private final String appVersion;

        public Review(String username, int rating, String comment, LocalDate date, String appVersion) {
            this.username = username;
            this.rating = rating;
            this.comment = comment;
            this.date = date;
            this.appVersion = appVersion;
        }

        public String getUsername() { return username; }
        public int getRating() { return rating; }
        public String getComment() { return comment; }
        public LocalDate getDate() { return date; }
        public String getAppVersion() { return appVersion; }
    }

    private final ObservableList<Review> reviewData = FXCollections.observableArrayList();
    private FilteredList<Review> filteredData;

    /**
     * Returns the complete dashboard content view to be mounted 
     * inside AdminMainShell or AdminDashboardView central pane.
     */
    public Parent getContent() {
        loadSampleData();

        VBox rootLayout = new VBox(20);
        rootLayout.setPadding(new Insets(24));
        rootLayout.setStyle("-fx-background-color: #F8FAFC;"); // Light slate background

        // Header Title Section
        Label headerTitle = new Label("App Reviews & Patient Feedback");
        headerTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Label headerSub = new Label("Monitor user ratings, app store feedback, and release stability.");
        headerSub.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

        VBox headerBox = new VBox(4, headerTitle, headerSub);

        // Top Analytics Section (Metrics + Distribution Chart)
        HBox topAnalytics = new HBox(20, createSummaryCards(), createRatingChart());
        topAnalytics.setAlignment(Pos.CENTER_LEFT);

        // Search and Filter Bar
        HBox filterBar = createFilterBar();

        // Reviews Table View
        TableView<Review> reviewTable = createReviewTable();
        VBox.setVgrow(reviewTable, Priority.ALWAYS);

        rootLayout.getChildren().addAll(headerBox, topAnalytics, filterBar, reviewTable);

        // Wrap inside ScrollPane for dynamic stage resizing
        ScrollPane scrollPane = new ScrollPane(rootLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #F8FAFC;");

        return scrollPane;
    }

    // Metric Summary Cards
    private HBox createSummaryCards() {
        VBox avgCard = createCard("Average Rating", "4.6 ★", "#2563EB");
        VBox totalCard = createCard("Total Reviews", String.valueOf(reviewData.size()), "#059669");
        VBox posCard = createCard("Positive Feedback", "88%", "#7C3AED");

        HBox cards = new HBox(16, avgCard, totalCard, posCard);
        cards.setAlignment(Pos.CENTER_LEFT);
        return cards;
    }

    private VBox createCard(String title, String value, String accentColor) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16));
        card.setPrefSize(180, 100);
        card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12; " +
                      "-fx-border-color: #E2E8F0; -fx-border-radius: 12; " +
                      "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 8, 0, 0, 2);");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B; -fx-font-weight: 500;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + accentColor + ";");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    // Rating Breakdown Chart
    private BarChart<Number, String> createRatingChart() {
        NumberAxis xAxis = new NumberAxis();
        CategoryAxis yAxis = new CategoryAxis();

        xAxis.setLabel("Count");
        yAxis.setLabel("Stars");

        BarChart<Number, String> chart = new BarChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setPrefSize(400, 150);
        chart.setTitle("Rating Breakdown");
        chart.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12; " +
                       "-fx-border-color: #E2E8F0; -fx-border-radius: 12; -fx-padding: 10;");

        XYChart.Series<Number, String> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>(45, "5 ★"));
        series.getData().add(new XYChart.Data<>(25, "4 ★"));
        series.getData().add(new XYChart.Data<>(10, "3 ★"));
        series.getData().add(new XYChart.Data<>(5,  "2 ★"));
        series.getData().add(new XYChart.Data<>(3,  "1 ★"));

        chart.getData().add(series);
        return chart;
    }

    // Search and Filter Controls
    private HBox createFilterBar() {
        TextField searchField = new TextField();
        searchField.setPromptText("Search reviews by keyword, patient, or doctor...");
        searchField.setPrefWidth(320);
        searchField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #CBD5E1; -fx-padding: 8 12;");

        ComboBox<String> ratingFilter = new ComboBox<>();
        ratingFilter.getItems().addAll("All Ratings", "5 Stars", "4 Stars", "3 Stars", "2 Stars", "1 Star");
        ratingFilter.setValue("All Ratings");
        ratingFilter.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #CBD5E1;");

        filteredData = new FilteredList<>(reviewData, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter(newVal, ratingFilter.getValue()));
        ratingFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter(searchField.getText(), newVal));

        HBox filterBar = new HBox(12, searchField, ratingFilter);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        return filterBar;
    }

    private void applyFilter(String searchText, String selectedRating) {
        filteredData.setPredicate(review -> {
            boolean matchesSearch = searchText == null || searchText.isEmpty() ||
                    review.getComment().toLowerCase().contains(searchText.toLowerCase()) ||
                    review.getUsername().toLowerCase().contains(searchText.toLowerCase());

            boolean matchesRating = true;
            if (selectedRating != null && !selectedRating.equals("All Ratings")) {
                int targetStars = Integer.parseInt(selectedRating.split(" ")[0]);
                matchesRating = (review.getRating() == targetStars);
            }

            return matchesSearch && matchesRating;
        });
    }

    // Table View
    private TableView<Review> createReviewTable() {
        TableView<Review> table = new TableView<>();
        table.setItems(filteredData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-radius: 12;");

        TableColumn<Review, String> userCol = new TableColumn<>("User / Patient");
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        userCol.setPrefWidth(140);

        TableColumn<Review, Integer> ratingCol = new TableColumn<>("Rating");
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));
        ratingCol.setPrefWidth(100);
        ratingCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer rating, boolean empty) {
                super.updateItem(rating, empty);
                if (empty || rating == null) {
                    setText(null);
                } else {
                    setText("★".repeat(rating) + "☆".repeat(5 - rating));
                    setStyle("-fx-text-fill: #F59E0B; -fx-font-weight: bold; -fx-font-size: 14px;");
                }
            }
        });

        TableColumn<Review, String> commentCol = new TableColumn<>("Feedback Comment");
        commentCol.setCellValueFactory(new PropertyValueFactory<>("comment"));
        commentCol.setPrefWidth(380);

        TableColumn<Review, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(110);

        TableColumn<Review, String> versionCol = new TableColumn<>("App Version");
        versionCol.setCellValueFactory(new PropertyValueFactory<>("appVersion"));
        versionCol.setPrefWidth(100);

        table.getColumns().addAll(userCol, ratingCol, commentCol, dateCol, versionCol);
        return table;
    }

    private void loadSampleData() {
        if (reviewData.isEmpty()) {
            reviewData.addAll(
                new Review("Prajwal S.", 5, "Appointment scheduling and tele-consultation are seamless!", LocalDate.now().minusDays(1), "v2.1.0"),
                new Review("Aniket M.", 4, "Great platform for health reports, but dark mode needs polish.", LocalDate.now().minusDays(2), "v2.1.0"),
                new Review("Sarah K.", 5, "Doctor search and prescription download work flawlessly.", LocalDate.now().minusDays(3), "v2.0.8"),
                new Review("John D.", 2, "Experienced lag when loading lab test results on poor connection.", LocalDate.now().minusDays(4), "v2.0.8"),
                new Review("Neha P.", 3, "Good overall UI, would love faster OTP verification.", LocalDate.now().minusDays(5), "v2.0.7")
            );
        }
    }
}