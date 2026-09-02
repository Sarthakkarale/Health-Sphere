package com.healthsphere.view.authentication;

import com.healthsphere.view.patient.PatientDashboardView;
import com.healthsphere.view.doctor.DoctorDashboardView;
import com.healthsphere.view.hospital.HospitalDashboardView;
import com.healthsphere.view.admin.AdminDashboardView;
import com.healthsphere.view.admin.AdminMainShell;
import com.healthsphere.view.authentication.*;

import com.healthsphere.controller.authentication.LoginController;
import com.healthsphere.exceptions.AuthenticationException;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.LoginDestination;
import com.healthsphere.model.Role;
import com.healthsphere.model.UserProfile;

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
import javafx.scene.control.Separator;
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

/**
 * Pure JavaFX Login View for Health-Sphere / MediNexus AI.
 * Implements navigation using the static View.stage reference and dynamic role routing.
 */
public class LoginView {

    private Button selectedRoleBtn = null;
    private final Stage stage;

    // Role references to track active selection
    private Button btnPatient;
    private Button btnDoctor;
    private Button btnHospital;
    private Button btnAdmin;

    private final LoginController loginController;
    private Label errorLabel;

    // Default Constructor
    public LoginView() {
        this.stage = new Stage();
        this.loginController = new LoginController();
    }

    // Overloaded Constructor for compatibility
    public LoginView(Stage stage) {
        this.stage = stage;
        this.loginController = new LoginController();
    }

    public Scene getScene() {
        HBox root = new HBox();
        root.getStyleClass().add("login-root");

        // Assemble Left and Right Columns
        VBox leftHero = createLeftHeroPanel();
        ScrollPane rightScroll = createRightFormPanel();

        HBox.setHgrow(leftHero, Priority.ALWAYS);
        HBox.setHgrow(rightScroll, Priority.ALWAYS);

        // Proportion split: 40% Left, 60% Right
        leftHero.prefWidthProperty().bind(root.widthProperty().multiply(0.40));
        rightScroll.prefWidthProperty().bind(root.widthProperty().multiply(0.60));

        root.getChildren().addAll(leftHero, rightScroll);

        Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());

        // Load CSS stylesheet safely
        String cssPath = getClass().getResource("/css/login.css") != null 
                ? getClass().getResource("/css/login.css").toExternalForm() 
                : null;
        if (cssPath != null) {
            scene.getStylesheets().add(cssPath);
        }

        return scene;
    }

    // ==========================================
    // 1. LEFT HERO PANEL
    // ==========================================
    private VBox createLeftHeroPanel() {
        VBox leftBox = new VBox(28);
        leftBox.getStyleClass().add("left-panel");
        leftBox.setAlignment(Pos.TOP_LEFT);

        // Brand Header Row
        HBox brandRow = new HBox(10);
        brandRow.setAlignment(Pos.CENTER_LEFT);
        ImageView logoView = createSafeImageView("/images/icons/brand_logo.png", 32, 32);
        Text brandTitle = new Text("MediNexus AI");
        brandTitle.getStyleClass().add("panel-brand");
        brandRow.getChildren().addAll(logoView, brandTitle);

        // Text Content
        VBox textContent = new VBox(12);
        Text headline = new Text("Advanced Clinical\nIntelligence");
        headline.getStyleClass().add("hero-header");

        Label subtext = new Label("Empowering healthcare professionals with real-time AI insights and seamless patient management.");
        subtext.getStyleClass().add("hero-subtext");
        subtext.setWrapText(true);
        textContent.getChildren().addAll(headline, subtext);

        // Feature Highlights Pills
        VBox featureList = new VBox(12);
        featureList.getChildren().addAll(
                createFeaturePill("✓  AI-powered Healthcare"),
                createFeaturePill("✓  Smart Appointment Management"),
                createFeaturePill("✓  Secure Medical Records")
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Bottom Illustration Image
        ImageView heroImg = createSafeImageView("/images/icons/login_hero.png", 420, 220);
        heroImg.setPreserveRatio(false);
        Rectangle clip = new Rectangle(420, 220);
        clip.setArcWidth(16);
        clip.setArcHeight(16);
        heroImg.setClip(clip);

        leftBox.getChildren().addAll(brandRow, textContent, featureList, spacer, heroImg);
        return leftBox;
    }

    private HBox createFeaturePill(String text) {
        HBox pill = new HBox();
        pill.getStyleClass().add("feature-pill");
        pill.setAlignment(Pos.CENTER_LEFT);
        Label pillText = new Label(text);
        pillText.getStyleClass().add("feature-pill-text");
        pill.getChildren().add(pillText);
        return pill;
    }

    // ==========================================
    // 2. RIGHT FORM PANEL
    // ==========================================
    private ScrollPane createRightFormPanel() {
        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(40, 60, 40, 60));

        VBox card = new VBox(20);
        card.getStyleClass().add("form-card");
        card.setMaxWidth(480);
        card.setAlignment(Pos.TOP_CENTER);

        // Emblem Logo at Top of Card
        ImageView cardLogo = createSafeImageView("/images/icons/brand_logo.png", 44, 44);

        // Titles
        VBox titleBox = new VBox(4);
        titleBox.setAlignment(Pos.CENTER);
        Text title = new Text("Welcome Back");
        title.getStyleClass().add("form-title");

        Text subtitle = new Text("Sign in to continue to your healthcare dashboard.");
        subtitle.getStyleClass().add("form-subtitle");

        Text accentText = new Text("SMART HEALTHCARE. CONNECTED CARE.");
        accentText.getStyleClass().add("accent-subtext");

        titleBox.getChildren().addAll(title, subtitle, accentText);

        // Role Selection Section
        VBox roleSection = new VBox(8);
        roleSection.setAlignment(Pos.CENTER_LEFT);
        Label roleLabel = new Label("Select Your Role");
        roleLabel.getStyleClass().add("input-label");

        GridPane roleGrid = new GridPane();
        roleGrid.setHgap(10);
        roleGrid.setVgap(10);

        btnPatient = createRoleButton("Patient", "/images/icon_patient.png", "👤");
        btnDoctor = createRoleButton("Doctor", "/images/icon_doctor.png", "🩺");
        btnHospital = createRoleButton("Hospital", "/images/icon_hospital.png", "🏥");
        btnAdmin = createRoleButton("Admin", "/images/icon_admin.png", "🔑");

        // Autofocus / Default Selected Role set to Patient
        selectRole(btnPatient);

        btnPatient.setOnAction(e -> selectRole(btnPatient));
        btnDoctor.setOnAction(e -> selectRole(btnDoctor));
        btnHospital.setOnAction(e -> selectRole(btnHospital));
        btnAdmin.setOnAction(e -> selectRole(btnAdmin));

        roleGrid.add(btnPatient, 0, 0);
        roleGrid.add(btnDoctor, 1, 0);
        roleGrid.add(btnHospital, 2, 0);
        roleGrid.add(btnAdmin, 3, 0);

        // Distribute columns evenly
        ColumnConstraints colCon = new ColumnConstraints();
        colCon.setPercentWidth(25);
        roleGrid.getColumnConstraints().addAll(colCon, colCon, colCon, colCon);

        roleSection.getChildren().addAll(roleLabel, roleGrid);

        // Visible Error Message Label on screen
        errorLabel = new Label();
        errorLabel.setWrapText(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        errorLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px; -fx-background-color: #ffeeef; -fx-padding: 8px; -fx-background-radius: 4px;");
        errorLabel.setMaxWidth(Double.MAX_VALUE);

        // Input Fields
        VBox emailBox = new VBox(6);
        Label emailLabel = new Label("Email Address");
        emailLabel.getStyleClass().add("input-label");
        TextField emailField = new TextField();
        emailField.setPromptText("e.g. dr.smith@medinexus.ai");
        emailField.getStyleClass().add("text-field-custom");
        emailBox.getChildren().addAll(emailLabel, emailField);

        VBox passBox = new VBox(6);
        HBox passHeader = new HBox();
        Label passLabel = new Label("Password");
        passLabel.getStyleClass().add("input-label");
        Region passSpacer = new Region();
        HBox.setHgrow(passSpacer, Priority.ALWAYS);
        Hyperlink forgotPass = new Hyperlink("Forgot Password?");
        forgotPass.setStyle("-fx-font-size: 11px; -fx-text-fill: #0256D0; -fx-padding: 0;");
        
        forgotPass.setOnAction(event -> {
            ForgotPasswordView forgotPasswordView = new ForgotPasswordView(stage);
            stage.setScene(forgotPasswordView.getScene());
        });
        passHeader.getChildren().addAll(passLabel, passSpacer, forgotPass);

        PasswordField passField = new PasswordField();
        passField.setPromptText("••••••••");
        passField.getStyleClass().add("text-field-custom");
        passBox.getChildren().addAll(passHeader, passField);

        // Remember Me Row
        CheckBox rememberBox = new CheckBox("Remember me for 30 days");
        rememberBox.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569;");

        // Main Action Buttons
        Button loginBtn = new Button("Login");
        loginBtn.getStyleClass().add("btn-login-primary");
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        // Login Action validating that selected tab matches user's role from profile
        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passField.getText();

            try {
                UserProfile profile = loginController.login(email, password);

                // Determine selected role from UI tab
                String selectedTabRole = getSelectedTabRoleString();
                String accountRole = profile.getRole();

                // Validate tab-role match to prevent logging into wrong dashboard
                if (!accountRole.equalsIgnoreCase(selectedTabRole)) {
                    showError("Access Denied: Selected role tab (" + selectedTabRole + ") does not match your account role.");
                    return;
                }

                LoginDestination destination = loginController.determineDestination(profile);
                handleLoginDestination(destination);

            } catch (AuthenticationException ex) {
                showError(ex.getMessage());
            } catch (DatabaseException ex) {
                showError(ex.getMessage());
            } catch (Exception ex) {
                showError("Unable to login. Please try again.");
            }
        });

        Button createAccountBtn = new Button("Create New Account");
        createAccountBtn.getStyleClass().add("btn-outline");
        createAccountBtn.setMaxWidth(Double.MAX_VALUE);

        createAccountBtn.setOnAction(e -> {
            RegisterView registerView = new RegisterView(stage);
            stage.setScene(registerView.getScene());
        });

        // Assemble Form Card (Google and Microsoft login buttons removed per request)
        card.getChildren().addAll(
                cardLogo, titleBox, roleSection, errorLabel, emailBox, passBox, rememberBox,
                loginBtn, createAccountBtn
        );

        container.getChildren().addAll(card, formFooterHelper());

        ScrollPane scrollPane = new ScrollPane(container);
        scrollPane.getStyleClass().add("right-panel-scroll");
        return scrollPane;
    }

    private HBox formFooterHelper() {
        HBox formFooter = new HBox(12);
        formFooter.setAlignment(Pos.CENTER);
        formFooter.setPadding(new Insets(16, 0, 0, 0));

        Hyperlink privacyLink = new Hyperlink("Privacy Policy");
        Hyperlink termsLink = new Hyperlink("Terms of Service");
        Hyperlink helpLink = new Hyperlink("Help");
        String linkStyle = "-fx-font-size: 11px; -fx-text-fill: #64748B;";
        privacyLink.setStyle(linkStyle);
        termsLink.setStyle(linkStyle);
        helpLink.setStyle(linkStyle);

        Region footSpacer = new Region();
        HBox.setHgrow(footSpacer, Priority.ALWAYS);

        Text verText = new Text("Version 1.0.4-stable");
        verText.setStyle("-fx-font-size: 11px; -fx-fill: #94A3B8;");

        formFooter.getChildren().addAll(privacyLink, termsLink, helpLink, footSpacer, verText);
        return formFooter;
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================
    private Button createRoleButton(String title, String iconPath, String fallbackEmoji) {
        Button btn = new Button();
        btn.getStyleClass().add("role-btn");
        btn.setMaxWidth(Double.MAX_VALUE);

        VBox content = new VBox(4);
        content.setAlignment(Pos.CENTER);

        ImageView icon = createSafeImageView(iconPath, 20, 20);
        Node graphic = icon.getImage() != null ? icon : new Text(fallbackEmoji);

        Label label = new Label(title);
        label.getStyleClass().add("role-label");

        content.getChildren().addAll(graphic, label);
        btn.setGraphic(content);

        return btn;
    }

    private void selectRole(Button btn) {
        if (selectedRoleBtn != null) {
            selectedRoleBtn.getStyleClass().remove("role-btn-selected");
            if (!selectedRoleBtn.getStyleClass().contains("role-btn")) {
                selectedRoleBtn.getStyleClass().add("role-btn");
            }
            selectedRoleBtn.setStyle("");
        }
        btn.getStyleClass().remove("role-btn");
        btn.getStyleClass().add("role-btn-selected");
        // Update selection box color styling when a specific role box is clicked/selected
        btn.setStyle("-fx-background-color: #E6F0FA; -fx-border-color: #0256D0; -fx-border-width: 2px; -fx-background-radius: 8px; -fx-border-radius: 8px;");
        selectedRoleBtn = btn;
    }

    private String getSelectedTabRoleString() {
        if (selectedRoleBtn == btnPatient) return Role.PATIENT.name();
        if (selectedRoleBtn == btnDoctor) return Role.DOCTOR.name();
        if (selectedRoleBtn == btnHospital) return Role.HOSPITAL.name();
        if (selectedRoleBtn == btnAdmin) return Role.ADMIN.name();
        return Role.PATIENT.name();
    }

    private void handleLoginDestination(LoginDestination destination) {
        switch (destination) {
            case PATIENT_DASHBOARD -> {
                stage.setScene(new PatientDashboardView(stage).getScene());
            }
            case DOCTOR_DASHBOARD -> {
                stage.setScene(new DoctorDashboardView(stage).getScene());
            }
            case DOCTOR_PENDING -> {
                // Handle doctor pending screen navigation if implemented
                stage.setScene(new DoctorPendingApprovalView(stage).getScene());
            }
            case HOSPITAL_DASHBOARD -> {
                stage.setScene(new HospitalDashboardView(stage).getScene());
            }
            case HOSPITAL_PENDING -> {
                // Handle hospital pending screen navigation if implemented
                stage.setScene(new DoctorPendingApprovalView(stage).getScene());
            }
            case ADMIN_DASHBOARD -> {
                stage.setScene(new AdminMainShell(stage).getScene(stage));
            }
            case LOGIN -> {
                showError("Unable to determine user access.");
            }
        }
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }

    private HBox createSecurityNotice() {
        HBox box = new HBox(12);
        box.getStyleClass().add("security-box");
        box.setAlignment(Pos.CENTER_LEFT);

        ImageView shieldIcon = createSafeImageView("/images/icon_shield.png", 24, 24);
        Node iconGraphic = shieldIcon.getImage() != null ? shieldIcon : new Text("🛡");

        VBox textGroup = new VBox(4);
        Text title = new Text("Secure Login");
        title.getStyleClass().add("security-title");

        Label bullet1 = new Label("• End-to-end 256-bit AES Encryption");
        Label bullet2 = new Label("• Powered by Firebase Identity Platform");
        Label bullet3 = new Label("• HIPAA & GDPR Compliant Architecture");

        bullet1.getStyleClass().add("security-text");
        bullet2.getStyleClass().add("security-text");
        bullet3.getStyleClass().add("security-text");

        textGroup.getChildren().addAll(title, bullet1, bullet2, bullet3);
        box.getChildren().addAll(iconGraphic, textGroup);

        return box;
    }

    private ImageView createSafeImageView(String path, double width, double height) {
        ImageView imgView = new ImageView();
        imgView.setFitWidth(width);
        imgView.setFitHeight(height);
        imgView.setPreserveRatio(true);

        try {
            if (getClass().getResource(path) != null) {
                imgView.setImage(new Image(getClass().getResourceAsStream(path)));
            }
        } catch (Exception ignored) {
            // Safe fallback during UI build phase
        }
        return imgView;
    }
}