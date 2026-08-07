package com.healthsphere.view.authentication;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node; // Added explicit import for Node
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
 * Updated with complete explicit imports including javafx.scene.Node.
 */
public class LoginView {

    private final Stage stage;
    private Button selectedRoleBtn = null;

    public LoginView(Stage stage) {
        this.stage = stage;
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

        Scene scene = new Scene(root, 1280, 850);

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

        Button btnPatient = createRoleButton("Patient", "/images/icon_patient.png", "👤");
        Button btnDoctor = createRoleButton("Doctor", "/images/icon_doctor.png", "🩺");
        Button btnHospital = createRoleButton("Hospital", "/images/icon_hospital.png", "🏥");
        Button btnAdmin = createRoleButton("Admin", "/images/icon_admin.png", "🔑");

        // Set Doctor as Default Selected Role
        selectRole(btnDoctor);

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

        // Navigation on Login Click -> Switch scene root directly to DashboardView
        loginBtn.setOnAction(e -> {
            DashboardView dashboard = new DashboardView(stage);
            stage.getScene().setRoot(dashboard.getScene().getRoot());
        });

        Button createAccountBtn = new Button("Create New Account");
        createAccountBtn.getStyleClass().add("btn-outline");
        createAccountBtn.setMaxWidth(Double.MAX_VALUE);

        // Divider
        HBox divider = new HBox(10);
        divider.setAlignment(Pos.CENTER);
        Separator s1 = new Separator();
        Separator s2 = new Separator();
        HBox.setHgrow(s1, Priority.ALWAYS);
        HBox.setHgrow(s2, Priority.ALWAYS);
        Text orText = new Text("OR");
        orText.setStyle("-fx-font-size: 11px; -fx-fill: #94A3B8;");
        divider.getChildren().addAll(s1, orText, s2);

        // Social Login Placeholders
        Button googleBtn = new Button("Login with Google");
        googleBtn.getStyleClass().add("btn-social");
        googleBtn.setMaxWidth(Double.MAX_VALUE);

        Button msBtn = new Button("Login with Microsoft");
        msBtn.getStyleClass().add("btn-social");
        msBtn.setMaxWidth(Double.MAX_VALUE);

        Text socialNote = new Text("Social login available in a future release");
        socialNote.setStyle("-fx-font-size: 11px; -fx-fill: #94A3B8;");

        VBox socialBox = new VBox(8);
        socialBox.setAlignment(Pos.CENTER);
        socialBox.getChildren().addAll(googleBtn, msBtn, socialNote);

        // Security Info Banner
        HBox securityBox = createSecurityNotice();

        // Footer Legal Links
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

        // Assemble Form Card
        card.getChildren().addAll(
                cardLogo, titleBox, roleSection, emailBox, passBox, rememberBox,
                loginBtn, createAccountBtn, divider, socialBox, securityBox
        );

        container.getChildren().addAll(card, formFooter);

        ScrollPane scrollPane = new ScrollPane(container);
        scrollPane.getStyleClass().add("right-panel-scroll");
        return scrollPane;
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
        }
        btn.getStyleClass().remove("role-btn");
        btn.getStyleClass().add("role-btn-selected");
        selectedRoleBtn = btn;
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