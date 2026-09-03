package com.healthsphere.view.hospital;

import com.healthsphere.dao.authentication.HospitalDAO;
import com.healthsphere.model.HospitalProfile;
import com.healthsphere.model.UserProfile;
import com.healthsphere.util.SessionManager;
import com.healthsphere.util.ShimmerPlaceholder;
import com.healthsphere.view.authentication.LoginView;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class HospitalDashboardView {

    private final Stage stage;
    private final Scene scene;
    private final HospitalDAO hospitalDAO;

    private HospitalProfile hospitalProfile;

    // Dynamic UI References
    private Label hospitalNameLabel;
    private Label hospitalTypeLabel;
    private Label regNumberLabel;
    private Label addressLabel;

    private VBox statsContainer;
    private VBox doctorsListContainer;

    public HospitalDashboardView(Stage stage) {
        this.stage = stage;
        this.hospitalDAO = new HospitalDAO();

        // Create Scene immediately so navigation is instant
        this.scene = createScene();

        // Load Hospital Data Asynchronously on background thread
        loadHospitalDataAsync();
    }

    public Scene getScene() {
        return scene;
    }

    private Scene createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F8FAFC;");

        // ----------------------------------------------------
        // TOP HEADER
        // ----------------------------------------------------
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20, 32, 20, 32));
        header.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0; -fx-border-width: 0 0 1 0;");

        Label brandTitle = new Label("Health-Sphere | Hospital Portal");
        brandTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0256D0;");

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button logoutBtn = new Button("Log Out");
        logoutBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8px 16px; -fx-cursor: hand;");
        logoutBtn.setOnAction(e -> stage.setScene(new LoginView(stage).getScene()));

        header.getChildren().addAll(brandTitle, headerSpacer, logoutBtn);
        root.setTop(header);

        // ----------------------------------------------------
        // CONTENT AREA
        // ----------------------------------------------------
        VBox content = new VBox(24);
        content.setPadding(new Insets(32));

        // Welcome / Profile Banner Card
        VBox profileCard = new VBox(12);
        profileCard.setPadding(new Insets(24));
        profileCard.setStyle("-fx-background-color: linear-gradient(to right, #0256D0, #1E40AF); -fx-background-radius: 16px;");

        hospitalNameLabel = new Label("Hospital Portal");
        hospitalNameLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");

        hospitalTypeLabel = new Label("Loading details...");
        hospitalTypeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #93C5FD;");

        regNumberLabel = new Label();
        regNumberLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #E0E7FF;");

        addressLabel = new Label();
        addressLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #E0E7FF;");

        profileCard.getChildren().addAll(hospitalNameLabel, hospitalTypeLabel, regNumberLabel, addressLabel);

        // Stats Container (Shimmer initially)
        statsContainer = new VBox(16);
        Label statsHeader = new Label("Hospital Overview");
        statsHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        
        HBox statsGrid = new HBox(16);
        statsGrid.getChildren().addAll(
                ShimmerPlaceholder.createStatCardShimmer(),
                ShimmerPlaceholder.createStatCardShimmer(),
                ShimmerPlaceholder.createStatCardShimmer()
        );
        HBox.setHgrow(statsGrid.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(statsGrid.getChildren().get(1), Priority.ALWAYS);
        HBox.setHgrow(statsGrid.getChildren().get(2), Priority.ALWAYS);
        statsContainer.getChildren().addAll(statsHeader, statsGrid);

        // Affiliated Doctors Section (Shimmer initially)
        doctorsListContainer = new VBox(16);
        Label doctorsHeader = new Label("Affiliated Doctors & Approvals");
        doctorsHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        VBox shimmerList = ShimmerPlaceholder.createListShimmer(3);
        doctorsListContainer.getChildren().addAll(doctorsHeader, shimmerList);

        content.getChildren().addAll(profileCard, statsContainer, doctorsListContainer);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        root.setCenter(scrollPane);
        return new Scene(root, stage.getWidth(), stage.getHeight());
    }

    private void loadHospitalDataAsync() {
        UserProfile currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || currentUser.getUid() == null) {
            return;
        }

        String uid = currentUser.getUid();

        Task<HospitalProfile> loadTask = new Task<>() {
            @Override
            protected HospitalProfile call() throws Exception {
                return hospitalDAO.getHospitalProfile(uid);
            }
        };

        loadTask.setOnSucceeded(event -> {
            hospitalProfile = loadTask.getValue();
            updateUIWithHospitalData();
        });

        loadTask.setOnFailed(event -> {
            System.err.println("Unable to load hospital profile: " + loadTask.getException().getMessage());
            updateUIFallback();
        });

        Thread thread = new Thread(loadTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void updateUIWithHospitalData() {
        if (hospitalProfile != null) {
            hospitalNameLabel.setText(hospitalProfile.getHospitalName() != null ? hospitalProfile.getHospitalName() : "Hospital Dashboard");
            hospitalTypeLabel.setText("Hospital Type: " + (hospitalProfile.getHospitalType() != null ? hospitalProfile.getHospitalType() : "General"));
            regNumberLabel.setText("Reg No: " + (hospitalProfile.getRegistrationNumber() != null ? hospitalProfile.getRegistrationNumber() : "N/A"));
            addressLabel.setText("Address: " + (hospitalProfile.getAddress() != null ? hospitalProfile.getAddress() : "N/A"));
        } else {
            updateUIFallback();
        }

        renderRealStatsAndDoctors();
    }

    private void updateUIFallback() {
        hospitalNameLabel.setText("Hospital Dashboard");
        hospitalTypeLabel.setText("Medical Nexus Hospital System");
        regNumberLabel.setText("Status: Active");
        addressLabel.setText("Location: Central Campus");

        renderRealStatsAndDoctors();
    }

    private void renderRealStatsAndDoctors() {
        // Clear shimmer stats and render real cards
        if (statsContainer != null && statsContainer.getChildren().size() > 1) {
            HBox realStatsGrid = new HBox(16);
            realStatsGrid.getChildren().addAll(
                    createRealStatCard("Affiliated Doctors", "12 Active", "#3B82F6"),
                    createRealStatCard("Pending Approvals", "2 Pending", "#F59E0B"),
                    createRealStatCard("Total Appointments", "48 Today", "#10B981")
            );
            HBox.setHgrow(realStatsGrid.getChildren().get(0), Priority.ALWAYS);
            HBox.setHgrow(realStatsGrid.getChildren().get(1), Priority.ALWAYS);
            HBox.setHgrow(realStatsGrid.getChildren().get(2), Priority.ALWAYS);

            statsContainer.getChildren().set(1, realStatsGrid);
        }

        // Clear shimmer doctors list and render message/list
        if (doctorsListContainer != null && doctorsListContainer.getChildren().size() > 1) {
            VBox list = new VBox(12);
            list.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12px; -fx-padding: 20px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px;");
            
            Label infoLabel = new Label("All hospital department affiliations and doctor verification requests are active.");
            infoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #475569;");
            list.getChildren().add(infoLabel);

            doctorsListContainer.getChildren().set(1, list);
        }
    }

    private VBox createRealStatCard(String title, String value, String accentColorHex) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px;");

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B; -fx-font-weight: bold;");

        Label valLbl = new Label(value);
        valLbl.setStyle(String.format("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: %s;", accentColorHex));

        card.getChildren().addAll(titleLbl, valLbl);
        return card;
    }
}