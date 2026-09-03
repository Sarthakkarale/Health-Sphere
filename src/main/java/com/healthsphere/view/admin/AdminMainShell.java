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

        // =========================================================
        // PERSISTENT SIDEBAR
        // =========================================================

        VBox sidebar = createSidebar();

        rootLayout.setLeft(sidebar);

        // =========================================================
        // INITIAL DASHBOARD
        // =========================================================

        AdminDashboardView dashboardView =
                new AdminDashboardView(primaryStage);

        rootLayout.setCenter(
                dashboardView.getView()
        );
    }

    // =========================================================
    // GET SCENE
    // =========================================================

    public Scene getScene(Stage st) {

        Scene scene =
                new Scene(
                        rootLayout,
                        st.getWidth(),
                        st.getHeight()
                );

        primaryStage.setTitle(
                "HealthSphere AI — Enterprise Command Center"
        );

        return scene;
    }

    // =========================================================
    // SHOW APPLICATION
    // =========================================================

    public void show() {

        primaryStage.setScene(
                getScene(primaryStage)
        );

        primaryStage.setMaximized(true);

        primaryStage.show();
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private VBox createSidebar() {

        VBox sidebar =
                new VBox(10);

        sidebar.setPrefWidth(260);

        sidebar.setPadding(
                new Insets(20)
        );

        sidebar.setStyle(
                "-fx-background-color: #0F172A;"
        );

        // =====================================================
        // BRAND HEADER
        // =====================================================

        VBox brandBox =
                new VBox(2);

        Label brandTitle =
                new Label("HealthSphere");

        brandTitle.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        20
                )
        );

        brandTitle.setTextFill(
                Color.WHITE
        );

        Label brandSub =
                new Label(
                        "COMMAND CENTER v4.2"
                );

        brandSub.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        10
                )
        );

        brandSub.setTextFill(
                Color.web("#94A3B8")
        );

        brandBox.getChildren().addAll(
                brandTitle,
                brandSub
        );

        brandBox.setPadding(
                new Insets(
                        0,
                        0,
                        15,
                        0
                )
        );

        // =====================================================
        // NAVIGATION BUTTONS
        // =====================================================

        Button dashboardBtn =
                createNavButton(
                        "📊  Command Dashboard"
                );

        Button userDirBtn =
                createNavButton(
                        "👥  User Directory"
                );

        Button hospitalBtn =
                createNavButton(
                        "🏥  Hospital Verification"
                );

        Button doctorBtn =
                createNavButton(
                        "🩺  Doctor Credentialing"
                );

        Button reportsBtn =
                createNavButton(
                        "📋  Reports & Moderation"
                );

        Button analyticsBtn =
                createNavButton(
                        "⭐  AppReviews"
                );

        Button settingsBtn =
                createNavButton(
                        "⚙️  Neural Settings"
                );

        allNavButtons =
                List.of(
                        dashboardBtn,
                        userDirBtn,
                        hospitalBtn,
                        doctorBtn,
                        reportsBtn,
                        analyticsBtn,
                        settingsBtn
                );

        // =====================================================
        // DASHBOARD
        // =====================================================

        dashboardBtn.setOnAction(e -> {

            setActiveButton(
                    dashboardBtn
            );

            AdminDashboardView dashboardView =
                    new AdminDashboardView(
                            primaryStage
                    );

            rootLayout.setCenter(
                    dashboardView.getView()
            );
        });

        // =====================================================
        // USER DIRECTORY
        // =====================================================

        userDirBtn.setOnAction(e -> {

            setActiveButton(
                    userDirBtn
            );

            UserManagementView userManagementView =
                    new UserManagementView();

            rootLayout.setCenter(
                    userManagementView.getView()
            );
        });

        // =====================================================
        // HOSPITAL MANAGEMENT
        // =====================================================

        hospitalBtn.setOnAction(e -> {

            setActiveButton(
                    hospitalBtn
            );

            HospitalManagementView hospitalManagementView =
                    new HospitalManagementView();

            rootLayout.setCenter(
                    hospitalManagementView.getView()
            );
        });

        // =====================================================
        // DOCTOR MANAGEMENT
        // =====================================================

        doctorBtn.setOnAction(e -> {

            setActiveButton(
                    doctorBtn
            );

            DoctorManagementView doctorManagementView =
                    new DoctorManagementView();

            rootLayout.setCenter(
                    doctorManagementView.getView()
            );
        });

        // =====================================================
        // REPORTS & MODERATION
        // =====================================================

        reportsBtn.setOnAction(e -> {

            setActiveButton(
                    reportsBtn
            );

            // -------------------------------------------------
            // OPEN COMPLAINTS MANAGEMENT
            // -------------------------------------------------

            ComplaintsManagementView complaintsView =
                    new ComplaintsManagementView(
                            primaryStage
                    );

            rootLayout.setCenter(
                    complaintsView.getView()
            );
        });

        // =====================================================
        // APP REVIEWS
        // =====================================================

        analyticsBtn.setOnAction(e -> {

            setActiveButton(
                    analyticsBtn
            );

            AppReviewDashboard analyticsView =
                    new AppReviewDashboard(
                            primaryStage
                    );

            rootLayout.setCenter(
                    analyticsView.getContent()
            );
        });

        // =====================================================
        // SETTINGS
        // =====================================================

        settingsBtn.setOnAction(e -> {

            setActiveButton(
                    settingsBtn
            );

            AdminSettingsView settingsView =
                    new AdminSettingsView(
                            primaryStage
                    );

            rootLayout.setCenter(
                    settingsView.getView()
            );
        });

        // =====================================================
        // SPACER
        // =====================================================

        Region spacer =
                new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        // =====================================================
        // USER PROFILE FOOTER
        // =====================================================

        VBox userProfileCard =
                createUserProfileFooter();

        // =====================================================
        // ADD SIDEBAR COMPONENTS
        // =====================================================

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

        // Dashboard active initially
        setActiveButton(
                dashboardBtn
        );

        return sidebar;
    }

    // =========================================================
    // CREATE NAVIGATION BUTTON
    // =========================================================

    private Button createNavButton(
            String text) {

        Button btn =
                new Button(text);

        btn.setMaxWidth(
                Double.MAX_VALUE
        );

        btn.setAlignment(
                Pos.CENTER_LEFT
        );

        btn.setPadding(
                new Insets(
                        10,
                        14,
                        10,
                        14
                )
        );

        btn.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.SEMI_BOLD,
                        13
                )
        );

        btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #94A3B8;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;"
        );

        return btn;
    }

    // =========================================================
    // ACTIVE NAVIGATION BUTTON
    // =========================================================

    private void setActiveButton(
            Button selectedBtn) {

        for (Button btn : allNavButtons) {

            btn.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: #94A3B8;" +
                    "-fx-background-radius: 8px;" +
                    "-fx-cursor: hand;"
            );
        }

        selectedBtn.setStyle(
                "-fx-background-color: #4F46E5;" +
                "-fx-text-fill: #FFFFFF;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;"
        );
    }

    // =========================================================
    // USER PROFILE FOOTER
    // =========================================================

    private VBox createUserProfileFooter() {

        VBox footer =
                new VBox(4);

        footer.setPadding(
                new Insets(12)
        );

        footer.setStyle(
                "-fx-background-color: #1E293B;" +
                "-fx-background-radius: 8px;"
        );

        // -----------------------------------------------------
        // NAME
        // -----------------------------------------------------

        Label nameLbl =
                new Label(
                        "Prajwal Patil"
                );

        nameLbl.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        12
                )
        );

        nameLbl.setTextFill(
                Color.WHITE
        );

        // -----------------------------------------------------
        // ROLE
        // -----------------------------------------------------

        Label roleLbl =
                new Label(
                        "Super Administrator"
                );

        roleLbl.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.NORMAL,
                        11
                )
        );

        roleLbl.setTextFill(
                Color.web("#94A3B8")
        );

        // -----------------------------------------------------
        // STATUS
        // -----------------------------------------------------

        HBox statusRow =
                new HBox(6);

        statusRow.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle dot =
                new Circle(
                        4,
                        Color.web("#10B981")
                );

        Label statusLbl =
                new Label(
                        "Node: Asia-South1 (Active)"
                );

        statusLbl.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.NORMAL,
                        10
                )
        );

        statusLbl.setTextFill(
                Color.web("#10B981")
        );

        statusRow.getChildren().addAll(
                dot,
                statusLbl
        );

        footer.getChildren().addAll(
                nameLbl,
                roleLbl,
                statusRow
        );

        return footer;
    }
}