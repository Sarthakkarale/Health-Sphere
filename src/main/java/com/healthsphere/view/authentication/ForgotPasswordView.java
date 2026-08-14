package com.healthsphere.view.authentication;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ForgotPasswordView {

    private final Stage stage;
    private final StackPane rightContainer; // Container to swap between Step 1 and Step 2

    // Form inputs tracked across views if necessary
    private TextField emailField;
    private TextField otpField1, otpField2, otpField3, otpField4;
    private PasswordField newPasswordField, confirmPasswordField;

    public ForgotPasswordView(Stage stage) {
        this.stage = stage;
        this.rightContainer = new StackPane();
    }

    public Scene getScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-container");

        HBox mainContent = new HBox();

        StackPane leftPanel = createLeftMarketingPanel();
        
        ScrollPane rightPanelWrapper = new ScrollPane(rightContainer);
        rightPanelWrapper.getStyleClass().add("right-scroll-pane");
        rightPanelWrapper.setFitToWidth(true);

        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanelWrapper, Priority.ALWAYS);

        // 40% / 60% Layout Ratio
        leftPanel.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.40));
        leftPanel.maxWidthProperty().bind(mainContent.widthProperty().multiply(0.40));
        leftPanel.minWidthProperty().bind(mainContent.widthProperty().multiply(0.40));
        rightPanelWrapper.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.60));

        mainContent.getChildren().addAll(leftPanel, rightPanelWrapper);
        root.setCenter(mainContent);
        root.setBottom(createFooter());

        // Load initial Step 1: Request Reset Link / OTP Trigger
        showStep1EmailInput();

        Scene scene = new Scene(root,stage.getWidth(),stage.getHeight());
        String cssResource = getClass().getResource("/css/auth.css") != null
                ? getClass().getResource("/css/auth.css").toExternalForm()
                : null;
        if (cssResource != null) {
            scene.getStylesheets().add(cssResource);
        }

        return scene;
    }

    // =========================================================================
    // STEP 1: EMAIL INPUT SCREEN (Screen 1)
    // =========================================================================
    public void showStep1EmailInput() {
        VBox outerWrapper = new VBox();
        outerWrapper.setAlignment(Pos.CENTER);
        outerWrapper.setPadding(new Insets(32, 24, 32, 24));

        VBox mainCard = new VBox(24);
        mainCard.getStyleClass().add("main-white-card");
        mainCard.setMaxWidth(620);
        mainCard.setAlignment(Pos.TOP_CENTER);

        ImageView logo = createSafeImageView("/images/logo.png", 48, 48);
        Node logoGraphic = logo.getImage() != null ? logo : createFallbackLogoGraphic();

        Node lockIllustration = createIllustrationBadge("🔑", Color.web("#DBEAFE"), Color.rgb(37, 99, 235, 0.25), Color.web("#2563EB"));

        Text heading = new Text("Forgot Password?");
        heading.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-fill: #111827;");

        Text subtitle = new Text("No worries! Enter your registered email address below and we'll send you instructions to reset your password.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-fill: #6B7280; -fx-line-spacing: 3px;");
        subtitle.setTextAlignment(TextAlignment.CENTER);
        subtitle.setWrappingWidth(500);

        VBox formGroup = new VBox(8);
        formGroup.setAlignment(Pos.CENTER_LEFT);
        formGroup.setMaxWidth(500);

        Label emailLabel = new Label("Email Address");
        emailLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #374151;");

        emailField = new TextField();
        emailField.setPromptText("name@example.com");
        emailField.getStyleClass().add("text-field");
        emailField.setPrefHeight(45);
        formGroup.getChildren().addAll(emailLabel, emailField);

        Label feedbackLabel = new Label();
        feedbackLabel.setWrapText(true);
        feedbackLabel.setMaxWidth(500);
        feedbackLabel.setVisible(false);
        feedbackLabel.managedProperty().bind(feedbackLabel.visibleProperty());

        Button sendBtn = new Button("Send Reset Instructions →");
        sendBtn.getStyleClass().add("btn-continue");
        sendBtn.setPrefSize(500, 50);
        sendBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8px;");
        addBtnHoverEffect(sendBtn, Color.rgb(37, 99, 235, 0.40));

        // Action: Advance to Screen 2 (OTP Verification / Reset Form block)
        sendBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            if (email.isEmpty() || !email.contains("@")) {
                feedbackLabel.setText("⚠️ Please enter a valid registered email address.");
                feedbackLabel.setStyle("-fx-text-fill: #DC2626; -fx-font-size: 12px; -fx-font-weight: 500;");
                feedbackLabel.setVisible(true);
            } else {
                showStep2ResetPassword();
            }
        });

        HBox backBox = new HBox(6);
        backBox.setAlignment(Pos.CENTER);
        Hyperlink backToLoginLink = new Hyperlink("Back to Login");
        backToLoginLink.setStyle("-fx-text-fill: #2563EB; -fx-font-weight: bold; -fx-font-size: 13px;");
        backToLoginLink.setOnAction(e -> {
            LoginView loginView = new LoginView(stage);
            stage.setScene(loginView.getScene());
        });
        backBox.getChildren().add(backToLoginLink);

        mainCard.getChildren().addAll(logoGraphic, lockIllustration, heading, subtitle, formGroup, feedbackLabel, sendBtn, backBox);
        outerWrapper.getChildren().add(mainCard);

        playEntryAnimations(mainCard);
        rightContainer.getChildren().setAll(outerWrapper);
    }

    // =========================================================================
    // STEP 2: NEW PASSWORD & OTP SETUP SCREEN (Screen 2)
    // =========================================================================
    public void showStep2ResetPassword() {
        VBox outerWrapper = new VBox();
        outerWrapper.setAlignment(Pos.CENTER);
        outerWrapper.setPadding(new Insets(32, 24, 32, 24));

        VBox mainCard = new VBox(20);
        mainCard.getStyleClass().add("main-white-card");
        mainCard.setMaxWidth(620);
        mainCard.setAlignment(Pos.TOP_CENTER);

        ImageView logo = createSafeImageView("/images/logo.png", 48, 48);
        Node logoGraphic = logo.getImage() != null ? logo : createFallbackLogoGraphic();

        Node shieldIllustration = createIllustrationBadge("🛡️", Color.web("#DCFCE7"), Color.rgb(16, 185, 129, 0.25), Color.web("#10B981"));

        Text heading = new Text("Set New Password");
        heading.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-fill: #111827;");

        Text subtitle = new Text("Please enter the 4-digit verification code sent to your email and create a secure new password.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-fill: #6B7280; -fx-line-spacing: 3px;");
        subtitle.setTextAlignment(TextAlignment.CENTER);
        subtitle.setWrappingWidth(500);

        // OTP Row
        VBox otpGroup = new VBox(6);
        otpGroup.setAlignment(Pos.CENTER_LEFT);
        otpGroup.setMaxWidth(500);
        Label otpLabel = new Label("Verification Code");
        otpLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #374151;");
        
        HBox otpBox = new HBox(12);
        otpBox.setAlignment(Pos.CENTER_LEFT);
        otpField1 = createOtpDigitField();
        otpField2 = createOtpDigitField();
        otpField3 = createOtpDigitField();
        otpField4 = createOtpDigitField();
        otpBox.getChildren().addAll(otpField1, otpField2, otpField3, otpField4);
        otpGroup.getChildren().addAll(otpLabel, otpBox);

        // New Password Group
        VBox passGroup = new VBox(6);
        passGroup.setAlignment(Pos.CENTER_LEFT);
        passGroup.setMaxWidth(500);
        Label passLabel = new Label("New Password");
        passLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #374151;");
        newPasswordField = new PasswordField();
        newPasswordField.setPromptText("At least 8 characters");
        newPasswordField.getStyleClass().add("text-field");
        newPasswordField.setPrefHeight(45);
        passGroup.getChildren().addAll(passLabel, newPasswordField);

        // Confirm Password Group
        VBox confirmGroup = new VBox(6);
        confirmGroup.setAlignment(Pos.CENTER_LEFT);
        confirmGroup.setMaxWidth(500);
        Label confirmLabel = new Label("Confirm Password");
        confirmLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #374151;");
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Re-enter new password");
        confirmPasswordField.getStyleClass().add("text-field");
        confirmPasswordField.setPrefHeight(45);
        confirmGroup.getChildren().addAll(confirmLabel, confirmPasswordField);

        Label feedbackLabel = new Label();
        feedbackLabel.setWrapText(true);
        feedbackLabel.setMaxWidth(500);
        feedbackLabel.setVisible(false);
        feedbackLabel.managedProperty().bind(feedbackLabel.visibleProperty());

        // Update Password Button -> Success state or Dashboard
        Button updateBtn = new Button("Update Password & Login →");
        updateBtn.getStyleClass().add("btn-continue");
        updateBtn.setPrefSize(500, 50);
        updateBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8px;");
        addBtnHoverEffect(updateBtn, Color.rgb(37, 99, 235, 0.40));

        updateBtn.setOnAction(e -> {
            String p1 = newPasswordField.getText();
            String p2 = confirmPasswordField.getText();
            if (p1.length() < 6) {
                feedbackLabel.setText("⚠️ Password must be at least 6 characters long.");
                feedbackLabel.setStyle("-fx-text-fill: #DC2626; -fx-font-size: 12px; -fx-font-weight: 500;");
                feedbackLabel.setVisible(true);
            } else if (!p1.equals(p2)) {
                feedbackLabel.setText("⚠️ Passwords do not match.");
                feedbackLabel.setStyle("-fx-text-fill: #DC2626; -fx-font-size: 12px; -fx-font-weight: 500;");
                feedbackLabel.setVisible(true);
            } else {
                // Successfully reset password, navigate to Dashboard or LoginView
                LoginView dashboardView = new LoginView(stage);
                stage.setScene(dashboardView.getScene());
            }
        });

        HBox backToStep1Box = new HBox(6);
        backToStep1Box.setAlignment(Pos.CENTER);
        Hyperlink backLink = new Hyperlink("← Back to Email Verification");
        backLink.setStyle("-fx-text-fill: #4B5563; -fx-font-size: 12px;");
        backLink.setOnAction(e -> showStep1EmailInput());
        backToStep1Box.getChildren().add(backLink);

        mainCard.getChildren().addAll(logoGraphic, shieldIllustration, heading, subtitle, otpGroup, passGroup, confirmGroup, feedbackLabel, updateBtn, backToStep1Box);
        outerWrapper.getChildren().add(mainCard);

        playEntryAnimations(mainCard);
        rightContainer.getChildren().setAll(outerWrapper);
    }

    private TextField createOtpDigitField() {
        TextField field = new TextField();
        field.setAlignment(Pos.CENTER);
        field.setPrefSize(55, 50);
        field.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-alignment: center;");
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 1) {
                field.setText(newVal.substring(0, 1));
            }
        });
        return field;
    }

    private Node createIllustrationBadge(String symbol, Color glowColor, Color shadowColor, Color badgeColor) {
        StackPane pane = new StackPane();
        pane.setPrefSize(130, 130);
        pane.setMaxSize(130, 130);

        Circle glow = new Circle(55, glowColor);
        DropShadow shadow = new DropShadow();
        shadow.setColor(shadowColor);
        shadow.setRadius(20);
        glow.setEffect(shadow);

        Circle inner = new Circle(40, badgeColor);
        Text icon = new Text(symbol);
        icon.setStyle("-fx-font-size: 34px;");

        pane.getChildren().addAll(glow, inner, icon);
        return pane;
    }

    // =========================================================================
    // LEFT MARKETING PANEL (Reused across views)
    // =========================================================================
    private StackPane createLeftMarketingPanel() {
        StackPane leftStack = new StackPane();

        ImageView bgImageView = new ImageView();
        bgImageView.setPreserveRatio(false);
        bgImageView.fitWidthProperty().bind(leftStack.widthProperty());
        bgImageView.fitHeightProperty().bind(leftStack.heightProperty());

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(leftStack.widthProperty());
        clip.heightProperty().bind(leftStack.heightProperty());
        leftStack.setClip(clip);

        try {
            if (getClass().getResource("/images/auth-background.jpg") != null) {
                bgImageView.setImage(new Image(getClass().getResourceAsStream("/images/auth-background.jpg")));
            }
        } catch (Exception ignored) {}

        Rectangle overlay = new Rectangle();
        overlay.getStyleClass().add("overlay-rect");
        overlay.widthProperty().bind(leftStack.widthProperty());
        overlay.heightProperty().bind(leftStack.heightProperty());

        VBox contentBox = new VBox();
        contentBox.setPadding(new Insets(48));
        contentBox.setAlignment(Pos.TOP_LEFT);

        HBox logoBox = new HBox(12);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        ImageView logoIcon = createSafeImageView("/images/logo.png", 45, 45);
        Node logoGraphic = logoIcon.getImage() != null ? logoIcon : createFallbackLogoGraphic();
        Text brandTitle = new Text("Health-Sphere AI");
        brandTitle.getStyleClass().add("left-logo-text");
        logoBox.getChildren().addAll(logoGraphic, brandTitle);

        VBox cardContainer = new VBox(20);
        cardContainer.setAlignment(Pos.CENTER_LEFT);
        cardContainer.setPadding(new Insets(40, 0, 0, 0));

        cardContainer.getChildren().addAll(
                createFeatureCard("🔒 Secure Recovery Protocol", "Account recovery links are encrypted and time-bound for maximum security protection."),
                createFeatureCard("🤖 AI-Driven Verification", "Instant identity validation via registered security parameters and profile tokens."),
                createFeatureCard("⚡ Rapid Password Reset", "Regain swift access to your medical vault, appointments, and prescriptions.")
        );

        Region verticalSpacer = new Region();
        VBox.setVgrow(verticalSpacer, Priority.ALWAYS);

        VBox bottomCaptionBox = new VBox(4);
        Text line1 = new Text("Clinical grade precision.");
        line1.getStyleClass().add("bottom-caption");
        Text line2 = new Text("Empowering digital healthcare.");
        line2.getStyleClass().add("bottom-caption");
        bottomCaptionBox.getChildren().addAll(line1, line2);

        contentBox.getChildren().addAll(logoBox, cardContainer, verticalSpacer, bottomCaptionBox);
        leftStack.getChildren().addAll(bgImageView, overlay, contentBox);

        return leftStack;
    }

    private Node createFallbackLogoGraphic() {
        StackPane pane = new StackPane();
        Circle circle = new Circle(22, Color.web("#2563EB"));
        Text cross = new Text("+");
        cross.setStyle("-fx-fill: white; -fx-font-weight: bold; -fx-font-size: 24px;");
        pane.getChildren().addAll(circle, cross);
        return pane;
    }

    private HBox createFeatureCard(String titleText, String descText) {
        HBox card = new HBox(14);
        card.getStyleClass().add("glass-feature-card");
        card.setAlignment(Pos.CENTER_LEFT);

        StackPane iconCircle = new StackPane();
        Circle circle = new Circle(16);
        circle.getStyleClass().add("card-icon-circle");
        Text iconSymbol = new Text("✓");
        iconSymbol.setStyle("-fx-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");
        iconCircle.getChildren().addAll(circle, iconSymbol);

        VBox textBox = new VBox(4);
        Text title = new Text(titleText);
        title.getStyleClass().add("feature-title");
        Label desc = new Label(descText);
        desc.getStyleClass().add("feature-description");
        desc.setWrapText(true);
        textBox.getChildren().addAll(title, desc);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        card.getChildren().addAll(iconCircle, textBox);
        return card;
    }

    private void playEntryAnimations(Node card) {
        card.setOpacity(0.0);
        card.setTranslateY(15);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(350), card);
        fadeIn.setToValue(1.0);
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(350), card);
        slideUp.setToY(0);

        ParallelTransition pt = new ParallelTransition(fadeIn, slideUp);
        pt.play();
    }

    private void addBtnHoverEffect(Button btn, Color glowColor) {
        DropShadow btnGlow = new DropShadow();
        btnGlow.setColor(glowColor);
        btnGlow.setRadius(12);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(120), btn);
        scaleUp.setToX(1.02);
        scaleUp.setToY(1.02);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(120), btn);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        btn.setOnMouseEntered(e -> {
            btn.setEffect(btnGlow);
            scaleUp.playFromStart();
        });

        btn.setOnMouseExited(e -> {
            btn.setEffect(null);
            scaleDown.playFromStart();
        });
    }

    private HBox createFooter() {
        HBox footer = new HBox();
        footer.getStyleClass().add("footer-bar");
        footer.setAlignment(Pos.CENTER);

        Text copyright = new Text("Health-Sphere AI © 2026 Health-Sphere Systems Inc.");
        copyright.getStyleClass().add("footer-copyright");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox linksBox = new HBox(20);
        linksBox.setAlignment(Pos.CENTER_RIGHT);
        linksBox.getChildren().addAll(
                new Text("Privacy"),
                new Text("Terms"),
                new Text("Security"),
                new Text("HIPAA")
        );
        for (Node n : linksBox.getChildren()) {
            n.getStyleClass().add("footer-hyperlink");
        }

        footer.getChildren().addAll(copyright, spacer, linksBox);
        return footer;
    }

    private ImageView createSafeImageView(String path, double width, double height) {
        ImageView img = new ImageView();
        img.setFitWidth(width);
        img.setFitHeight(height);
        img.setPreserveRatio(true);
        try {
            if (getClass().getResource(path) != null) {
                img.setImage(new Image(getClass().getResourceAsStream(path)));
            }
        } catch (Exception ignored) {}
        return img;
    }
}