package com.healthsphere.view.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

public class AdminMainShell {

    private final Stage primaryStage;
    private final BorderPane rootLayout;
    private List<Button> allNavButtons;

    public AdminMainShell(Stage stage) {
        this.primaryStage = stage;
        this.rootLayout = new BorderPane();

        // 1. Build and attach the persistent sidebar on the left
        VBox sidebar = createSidebar();
        rootLayout.setLeft(sidebar);

        // 2. Load the initial Command Dashboard view in the center
        AdminDashboardView dashboardView = new AdminDashboardView(stage);
        rootLayout.setCenter(dashboardView.getView());
    }

    public void show() {
        Scene scene = new Scene(rootLayout, 1366, 768);
        primaryStage.setTitle("HealthSphere AI — Enterprise Command Center");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(260);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #0F172A;"); // Dark enterprise navy theme

        // Brand Header
        VBox brandBox = new VBox(2);
        Label brandTitle = new Label("HealthSphere");
        brandTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        brandTitle.setTextFill(Color.WHITE);
        Label brandSub = new Label("COMMAND CENTER v4.2");
        brandSub.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
        brandSub.setTextFill(Color.web("#94A3B8"));
        brandBox.getChildren().addAll(brandTitle, brandSub);
        brandBox.setPadding(new Insets(0, 0, 15, 0));

        // Navigation Buttons
        Button dashboardBtn = createNavButton("📊  Command Dashboard");
        Button userDirBtn = createNavButton("👥  User Directory");
        Button hospitalBtn = createNavButton("🏥  Hospital Verification");
        Button doctorBtn = createNavButton("🩺  Doctor Credentialing");
        Button reportsBtn = createNavButton("📋  Reports & Moderation");
        Button analyticsBtn = createNavButton("📈  BI & Analytics");
        Button settingsBtn = createNavButton("⚙️  Neural Settings");

        allNavButtons = List.of(dashboardBtn, userDirBtn, hospitalBtn, doctorBtn, reportsBtn, analyticsBtn, settingsBtn);

        // Wire Event Handlers for View Swapping to actual modules
        dashboardBtn.setOnAction(e -> {
            setActiveButton(dashboardBtn);
            AdminDashboardView dashboardView = new AdminDashboardView(primaryStage);
            rootLayout.setCenter(dashboardView.getView());
        });

        userDirBtn.setOnAction(e -> {
            setActiveButton(userDirBtn);
            UserManagementView userManagementView = new UserManagementView();
            rootLayout.setCenter(userManagementView.getView());
        });

        hospitalBtn.setOnAction(e -> {
            setActiveButton(hospitalBtn);
            HospitalManagementView hospitalManagementView = new HospitalManagementView();
            rootLayout.setCenter(hospitalManagementView.getView());
        });

        doctorBtn.setOnAction(e -> {
            setActiveButton(doctorBtn);
            DoctorManagementView doctorManagementView = new DoctorManagementView();
            rootLayout.setCenter(doctorManagementView.getView());
        });

        reportsBtn.setOnAction(e -> {
            setActiveButton(reportsBtn);
            ReportsAnalyticsView reportsView = new ReportsAnalyticsView();
            rootLayout.setCenter(reportsView);
        });

        // analyticsBtn.setOnAction(e -> {
        //     setActiveButton(analyticsBtn);
        //     AppReviewDashboard analyticsView = new AppReviewDashboard();
        //     rootLayout.setCenter(analyticsView.getView());
        // });

        // settingsBtn.setOnAction(e -> {
        //     setActiveButton(settingsBtn);
        //     AdminSettingsView settingsView = new AdminSettingsView();
        //     rootLayout.setCenter(settingsView.getView());
        // });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // User Profile Footer Card
        VBox userProfileCard = createUserProfileFooter();

        sidebar.getChildren().addAll(
                brandBox,
                dashboardBtn,
                userDirBtn,
                hospitalBtn,
                doctorBtn,
                reportsBtn,
                analyticsBtn,
                settingsBtn,
                spacer,
                userProfileCard
        );

        setActiveButton(dashboardBtn);
        return sidebar;
    }

    private Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(10, 14, 10, 14));
        btn.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        btn.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-text-fill: #94A3B8; " +
                "-fx-background-radius: 8px; " +
                "-fx-cursor: hand;"
        );
        return btn;
    }

    private void setActiveButton(Button selectedBtn) {
        for (Button btn : allNavButtons) {
            btn.setStyle(
                    "-fx-background-color: transparent; " +
                    "-fx-text-fill: #94A3B8; " +
                    "-fx-background-radius: 8px; " +
                    "-fx-cursor: hand;"
            );
        }
        selectedBtn.setStyle(
                "-fx-background-color: #4F46E5; " +
                "-fx-text-fill: #FFFFFF; " +
                "-fx-background-radius: 8px; " +
                "-fx-cursor: hand;"
        );
    }

    private VBox createUserProfileFooter() {
        VBox footer = new VBox(4);
        footer.setPadding(new Insets(12));
        footer.setStyle("-fx-background-color: #1E293B; -fx-background-radius: 8px;");

        Label nameLbl = new Label("Prajwal Patil");
        nameLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        nameLbl.setTextFill(Color.WHITE);

        Label roleLbl = new Label("Super Administrator");
        roleLbl.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
        roleLbl.setTextFill(Color.web("#94A3B8"));

        HBox statusRow = new HBox(6);
        statusRow.setAlignment(Pos.CENTER_LEFT);
        Circle dot = new Circle(4, Color.web("#10B981"));
        Label statusLbl = new Label("Node: Asia-South1 (Active)");
        statusLbl.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 10));
        statusLbl.setTextFill(Color.web("#10B981"));
        statusRow.getChildren().addAll(dot, statusLbl);

        footer.getChildren().addAll(nameLbl, roleLbl, statusRow);
        return footer;
    }
}