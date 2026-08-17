package com.healthsphere.view.authentication;

import com.healthsphere.controller.authentication.LoginController;
import com.healthsphere.exceptions.AuthenticationException;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.LoginDestination;
import com.healthsphere.model.Role;
import com.healthsphere.model.UserProfile;
import com.healthsphere.view.Patient.Dashboard;
import com.healthsphere.view.admin.AdminDashboardView;
import com.healthsphere.view.doctor.DoctorDashboardView;
import com.healthsphere.view.hospital.HospitalDashboardView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginView {

    private final Stage stage;
    private final LoginController loginController;

    private Button selectedRoleBtn;

    private Button btnPatient;
    private Button btnDoctor;
    private Button btnHospital;
    private Button btnAdmin;

    private Label errorLabel;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public LoginView(Stage stage) {

        this.stage = stage;

        this.loginController =
                new LoginController();
    }

    // =========================================================
    // LOGIN SCENE
    // =========================================================

    public Scene getScene() {

        HBox root =
                new HBox();

        root.setMinWidth(0);
        root.setMinHeight(0);

        root.setMaxWidth(
                Double.MAX_VALUE
        );

        root.setMaxHeight(
                Double.MAX_VALUE
        );

        root.getStyleClass().add(
                "login-root"
        );

        VBox leftHero =
                createLeftHeroPanel();

        ScrollPane rightScroll =
                createRightFormPanel();

        HBox.setHgrow(
                leftHero,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                rightScroll,
                Priority.ALWAYS
        );

        leftHero.prefWidthProperty()
                .bind(
                        root.widthProperty()
                                .multiply(0.40)
                );

        rightScroll.prefWidthProperty()
                .bind(
                        root.widthProperty()
                                .multiply(0.60)
                );

        root.getChildren().addAll(
                leftHero,
                rightScroll
        );

        Scene scene =
                new Scene(root);

        if (getClass().getResource(
                "/css/login.css"
        ) != null) {

            scene.getStylesheets().add(
                    getClass()
                            .getResource(
                                    "/css/login.css"
                            )
                            .toExternalForm()
            );
        }

        /*
         * IMPORTANT:
         *
         * Do NOT maximize here.
         * View already maximized the shared Stage.
         */

        return scene;
    }

    // =========================================================
    // LEFT HERO
    // =========================================================

    private VBox createLeftHeroPanel() {

        VBox leftBox =
                new VBox(28);

        leftBox.getStyleClass().add(
                "left-panel"
        );

        leftBox.setAlignment(
                Pos.TOP_LEFT
        );

        leftBox.setMinWidth(0);
        leftBox.setMinHeight(0);

        leftBox.setMaxWidth(
                Double.MAX_VALUE
        );

        leftBox.setMaxHeight(
                Double.MAX_VALUE
        );

        HBox brandRow =
                new HBox(10);

        brandRow.setAlignment(
                Pos.CENTER_LEFT
        );

        ImageView logoView =
                createSafeImageView(
                        "/images/icons/brand_logo.png",
                        32,
                        32
                );

        Text brandTitle =
                new Text(
                        "Health-Sphere"
                );

        brandTitle.getStyleClass().add(
                "panel-brand"
        );

        brandRow.getChildren().addAll(
                logoView,
                brandTitle
        );

        VBox textContent =
                new VBox(12);

        Text headline =
                new Text(
                        "Advanced Clinical\nIntelligence"
                );

        headline.getStyleClass().add(
                "hero-header"
        );

        Label subtext =
                new Label(
                        "Empowering healthcare professionals " +
                        "with real-time AI insights and seamless patient management."
                );

        subtext.getStyleClass().add(
                "hero-subtext"
        );

        subtext.setWrapText(true);

        subtext.setMaxWidth(
                Double.MAX_VALUE
        );

        textContent.getChildren().addAll(
                headline,
                subtext
        );

        VBox featureList =
                new VBox(12);

        featureList.getChildren().addAll(

                createFeaturePill(
                        "✓  AI-powered Healthcare"
                ),

                createFeaturePill(
                        "✓  Smart Appointment Management"
                ),

                createFeaturePill(
                        "✓  Secure Medical Records"
                )
        );

        Region spacer =
                new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        ImageView heroImg =
                createSafeImageView(
                        "/images/icons/login_hero.png",
                        420,
                        220
                );

        heroImg.setPreserveRatio(false);

        Rectangle clip =
                new Rectangle(
                        420,
                        220
                );

        clip.setArcWidth(16);
        clip.setArcHeight(16);

        heroImg.setClip(clip);

        leftBox.getChildren().addAll(
                brandRow,
                textContent,
                featureList,
                spacer,
                heroImg
        );

        return leftBox;
    }

    // =========================================================
    // FEATURE PILL
    // =========================================================

    private HBox createFeaturePill(
            String text
    ) {

        HBox pill =
                new HBox();

        pill.getStyleClass().add(
                "feature-pill"
        );

        pill.setAlignment(
                Pos.CENTER_LEFT
        );

        Label label =
                new Label(text);

        label.getStyleClass().add(
                "feature-pill-text"
        );

        pill.getChildren().add(
                label
        );

        return pill;
    }

    // =========================================================
    // RIGHT FORM
    // =========================================================

    private ScrollPane createRightFormPanel() {

        VBox container =
                new VBox();

        container.setAlignment(
                Pos.CENTER
        );

        container.setPadding(
                new Insets(
                        40,
                        60,
                        40,
                        60
                )
        );

        container.setMinWidth(0);

        container.setMaxWidth(
                Double.MAX_VALUE
        );

        VBox card =
                new VBox(20);

        card.getStyleClass().add(
                "form-card"
        );

        card.setMaxWidth(480);

        card.setAlignment(
                Pos.TOP_CENTER
        );

        ImageView cardLogo =
                createSafeImageView(
                        "/images/icons/brand_logo.png",
                        44,
                        44
                );

        VBox titleBox =
                new VBox(4);

        titleBox.setAlignment(
                Pos.CENTER
        );

        Text title =
                new Text(
                        "Welcome Back"
                );

        title.getStyleClass().add(
                "form-title"
        );

        Text subtitle =
                new Text(
                        "Sign in to continue to your healthcare dashboard."
                );

        subtitle.getStyleClass().add(
                "form-subtitle"
        );

        Text accentText =
                new Text(
                        "SMART HEALTHCARE. CONNECTED CARE."
                );

        accentText.getStyleClass().add(
                "accent-subtext"
        );

        titleBox.getChildren().addAll(
                title,
                subtitle,
                accentText
        );

        // =====================================================
        // ROLE
        // =====================================================

        VBox roleSection =
                new VBox(8);

        roleSection.setAlignment(
                Pos.CENTER_LEFT
        );

        Label roleLabel =
                new Label(
                        "Select Your Role"
                );

        roleLabel.getStyleClass().add(
                "input-label"
        );

        GridPane roleGrid =
                new GridPane();

        roleGrid.setHgap(10);
        roleGrid.setVgap(10);

        btnPatient =
                createRoleButton(
                        "Patient",
                        "/images/icon_patient.png",
                        "👤"
                );

        btnDoctor =
                createRoleButton(
                        "Doctor",
                        "/images/icon_doctor.png",
                        "🩺"
                );

        btnHospital =
                createRoleButton(
                        "Hospital",
                        "/images/icon_hospital.png",
                        "🏥"
                );

        btnAdmin =
                createRoleButton(
                        "Admin",
                        "/images/icon_admin.png",
                        "🔑"
                );

        selectRole(btnPatient);

        btnPatient.setOnAction(
                e -> selectRole(btnPatient)
        );

        btnDoctor.setOnAction(
                e -> selectRole(btnDoctor)
        );

        btnHospital.setOnAction(
                e -> selectRole(btnHospital)
        );

        btnAdmin.setOnAction(
                e -> selectRole(btnAdmin)
        );

        roleGrid.add(
                btnPatient,
                0,
                0
        );

        roleGrid.add(
                btnDoctor,
                1,
                0
        );

        roleGrid.add(
                btnHospital,
                2,
                0
        );

        roleGrid.add(
                btnAdmin,
                3,
                0
        );

        for (int i = 0; i < 4; i++) {

            ColumnConstraints column =
                    new ColumnConstraints();

            column.setPercentWidth(25);

            roleGrid
                    .getColumnConstraints()
                    .add(column);
        }

        roleSection.getChildren().addAll(
                roleLabel,
                roleGrid
        );

        // =====================================================
        // ERROR
        // =====================================================

        errorLabel =
                new Label();

        errorLabel.setWrapText(true);

        errorLabel.setVisible(false);

        errorLabel.setManaged(false);

        errorLabel.setStyle(
                "-fx-text-fill: #ef4444;" +
                "-fx-font-size: 12px;" +
                "-fx-background-color: #ffeeef;" +
                "-fx-padding: 8px;" +
                "-fx-background-radius: 4px;"
        );

        errorLabel.setMaxWidth(
                Double.MAX_VALUE
        );

        // =====================================================
        // EMAIL
        // =====================================================

        VBox emailBox =
                new VBox(6);

        Label emailLabel =
                new Label(
                        "Email Address"
                );

        emailLabel.getStyleClass().add(
                "input-label"
        );

        TextField emailField =
                new TextField();

        emailField.setPromptText(
                "e.g. dr.smith@healthsphere.ai"
        );

        emailField.getStyleClass().add(
                "text-field-custom"
        );

        emailBox.getChildren().addAll(
                emailLabel,
                emailField
        );

        // =====================================================
        // PASSWORD
        // =====================================================

        VBox passBox =
                new VBox(6);

        HBox passHeader =
                new HBox();

        Label passLabel =
                new Label(
                        "Password"
                );

        passLabel.getStyleClass().add(
                "input-label"
        );

        Region passSpacer =
                new Region();

        HBox.setHgrow(
                passSpacer,
                Priority.ALWAYS
        );

        Hyperlink forgotPass =
                new Hyperlink(
                        "Forgot Password?"
                );

        forgotPass.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-text-fill: #0256D0;" +
                "-fx-padding: 0;"
        );

        forgotPass.setOnAction(
                event -> {

                    ForgotPasswordView forgot =
                            new ForgotPasswordView(stage);

                    stage.setScene(
                            forgot.getScene()
                    );
                }
        );

        passHeader.getChildren().addAll(
                passLabel,
                passSpacer,
                forgotPass
        );

        PasswordField passField =
                new PasswordField();

        passField.setPromptText(
                "••••••••"
        );

        passField.getStyleClass().add(
                "text-field-custom"
        );

        passBox.getChildren().addAll(
                passHeader,
                passField
        );

        // =====================================================
        // REMEMBER
        // =====================================================

        CheckBox rememberBox =
                new CheckBox(
                        "Remember me for 30 days"
                );

        rememberBox.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #475569;"
        );

        // =====================================================
        // LOGIN
        // =====================================================

        Button loginBtn =
                new Button(
                        "Login"
                );

        loginBtn.getStyleClass().add(
                "btn-login-primary"
        );

        loginBtn.setMaxWidth(
                Double.MAX_VALUE
        );

        loginBtn.setOnAction(
                e -> {

                    String email =
                            emailField
                                    .getText()
                                    .trim();

                    String password =
                            passField
                                    .getText();

                    if (email.isEmpty()
                            || password.isEmpty()) {

                        showError(
                                "Please enter your email and password."
                        );

                        return;
                    }

                    try {

                        UserProfile profile =
                                loginController.login(
                                        email,
                                        password
                                );

                        String selectedRole =
                                getSelectedTabRoleString();

                        String accountRole =
                                profile.getRole();

                        if (!accountRole.equalsIgnoreCase(
                                selectedRole
                        )) {

                            showError(
                                    "Access Denied: Selected role tab (" +
                                    selectedRole +
                                    ") does not match your account role."
                            );

                            return;
                        }

                        LoginDestination destination =
                                loginController
                                        .determineDestination(
                                                profile
                                        );

                        handleLoginDestination(
                                destination
                        );

                    } catch (
                            AuthenticationException ex
                    ) {

                        showError(
                                ex.getMessage()
                        );

                    } catch (
                            DatabaseException ex
                    ) {

                        showError(
                                ex.getMessage()
                        );

                    } catch (Exception ex) {

                        ex.printStackTrace();

                        showError(
                                "Unable to login. Please try again."
                        );
                    }
                }
        );

        // =====================================================
        // REGISTER
        // =====================================================

        Button createAccountBtn =
                new Button(
                        "Create New Account"
                );

        createAccountBtn.getStyleClass().add(
                "btn-outline"
        );

        createAccountBtn.setMaxWidth(
                Double.MAX_VALUE
        );

        createAccountBtn.setOnAction(
                e -> {

                    RegisterView registerView =
                            new RegisterView(stage);

                    stage.setScene(
                            registerView.getScene()
                    );
                }
        );

        card.getChildren().addAll(
                cardLogo,
                titleBox,
                roleSection,
                errorLabel,
                emailBox,
                passBox,
                rememberBox,
                loginBtn,
                createAccountBtn
        );

        container.getChildren().addAll(
                card,
                formFooterHelper()
        );

        ScrollPane scrollPane =
                new ScrollPane(container);

        scrollPane.getStyleClass().add(
                "right-panel-scroll"
        );

        scrollPane.setFitToWidth(true);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setMinWidth(0);
        scrollPane.setMinHeight(0);

        scrollPane.setMaxWidth(
                Double.MAX_VALUE
        );

        scrollPane.setMaxHeight(
                Double.MAX_VALUE
        );

        return scrollPane;
    }

    // =========================================================
    // FOOTER
    // =========================================================

    private HBox formFooterHelper() {

        HBox footer =
                new HBox(12);

        footer.setAlignment(
                Pos.CENTER
        );

        footer.setPadding(
                new Insets(
                        16,
                        0,
                        0,
                        0
                )
        );

        Hyperlink privacy =
                new Hyperlink(
                        "Privacy Policy"
                );

        Hyperlink terms =
                new Hyperlink(
                        "Terms of Service"
                );

        Hyperlink help =
                new Hyperlink(
                        "Help"
                );

        String style =
                "-fx-font-size: 11px;" +
                "-fx-text-fill: #64748B;";

        privacy.setStyle(style);
        terms.setStyle(style);
        help.setStyle(style);

        Text version =
                new Text(
                        "Version 1.0.4-stable"
                );

        version.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-fill: #94A3B8;"
        );

        footer.getChildren().addAll(
                privacy,
                terms,
                help,
                version
        );

        return footer;
    }

    // =========================================================
    // ROLE BUTTON
    // =========================================================

    private Button createRoleButton(
            String title,
            String iconPath,
            String fallbackEmoji
    ) {

        Button button =
                new Button();

        button.getStyleClass().add(
                "role-btn"
        );

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        VBox content =
                new VBox(4);

        content.setAlignment(
                Pos.CENTER
        );

        ImageView icon =
                createSafeImageView(
                        iconPath,
                        20,
                        20
                );

        Node graphic =
                icon.getImage() != null
                        ? icon
                        : new Text(fallbackEmoji);

        Label label =
                new Label(title);

        label.getStyleClass().add(
                "role-label"
        );

        content.getChildren().addAll(
                graphic,
                label
        );

        button.setGraphic(
                content
        );

        return button;
    }

    // =========================================================
    // SELECT ROLE
    // =========================================================

    private void selectRole(
            Button button
    ) {

        if (selectedRoleBtn != null) {

            selectedRoleBtn
                    .getStyleClass()
                    .remove(
                            "role-btn-selected"
                    );

            if (!selectedRoleBtn
                    .getStyleClass()
                    .contains("role-btn")) {

                selectedRoleBtn
                        .getStyleClass()
                        .add("role-btn");
            }

            selectedRoleBtn.setStyle("");
        }

        button.getStyleClass().remove(
                "role-btn"
        );

        button.getStyleClass().add(
                "role-btn-selected"
        );

        button.setStyle(
                "-fx-background-color: #E6F0FA;" +
                "-fx-border-color: #0256D0;" +
                "-fx-border-width: 2px;" +
                "-fx-background-radius: 8px;" +
                "-fx-border-radius: 8px;"
        );

        selectedRoleBtn = button;
    }

    // =========================================================
    // ROLE STRING
    // =========================================================

    private String getSelectedTabRoleString() {

        if (selectedRoleBtn == btnPatient) {
            return Role.PATIENT.name();
        }

        if (selectedRoleBtn == btnDoctor) {
            return Role.DOCTOR.name();
        }

        if (selectedRoleBtn == btnHospital) {
            return Role.HOSPITAL.name();
        }

        if (selectedRoleBtn == btnAdmin) {
            return Role.ADMIN.name();
        }

        return Role.PATIENT.name();
    }

    // =========================================================
    // LOGIN DESTINATION
    // =========================================================

    private void handleLoginDestination(
            LoginDestination destination
    ) {

        Scene destinationScene;

        switch (destination) {

            case PATIENT_DASHBOARD -> {

                destinationScene =
                        new Dashboard(stage)
                                .getScene();
            }

            case DOCTOR_DASHBOARD -> {

                destinationScene =
                        new DoctorDashboardView(stage)
                                .getScene();
            }

            case DOCTOR_PENDING -> {

                destinationScene =
                        new DoctorPendingApprovalView(stage)
                                .getScene();
            }

            case HOSPITAL_DASHBOARD -> {

                destinationScene =
                        new HospitalDashboardView(stage)
                                .getScene();
            }

            case HOSPITAL_PENDING -> {

                destinationScene =
                        new DoctorPendingApprovalView(stage)
                                .getScene();
            }

            case ADMIN_DASHBOARD -> {

                destinationScene =
                        new AdminDashboardView(stage)
                                .getScene();
            }

            case LOGIN -> {

                showError(
                        "Unable to determine user access."
                );

                return;
            }

            default -> {
                return;
            }
        }

        /*
         * =====================================================
         * SAME SHARED STAGE
         * =====================================================
         *
         * No maximize.
         * No show().
         * No new Stage.
         */
        stage.setScene(
                destinationScene
        );
    }

    // =========================================================
    // ERROR
    // =========================================================

    private void showError(
            String message
    ) {

        if (errorLabel != null) {

            errorLabel.setText(
                    message
            );

            errorLabel.setVisible(
                    true
            );

            errorLabel.setManaged(
                    true
            );
        }
    }

    // =========================================================
    // SAFE IMAGE
    // =========================================================

    private ImageView createSafeImageView(
            String path,
            double width,
            double height
    ) {

        ImageView imageView =
                new ImageView();

        imageView.setFitWidth(width);
        imageView.setFitHeight(height);

        imageView.setPreserveRatio(true);

        try {

            if (getClass().getResource(path) != null) {

                imageView.setImage(
                        new Image(
                                getClass()
                                        .getResourceAsStream(path)
                        )
                );
            }

        } catch (Exception ignored) {
        }

        return imageView;
    }
}