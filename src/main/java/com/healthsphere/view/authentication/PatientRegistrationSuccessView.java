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

public class PatientRegistrationSuccessView {

    private final Stage stage;

    public PatientRegistrationSuccessView(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-container");

        HBox mainContent = new HBox();

        StackPane leftPanel = createLeftMarketingPanel();
        ScrollPane rightPanel = createRightFormPanel();

        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        // 40% / 60% Layout Ratio
        leftPanel.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.40));
        leftPanel.maxWidthProperty().bind(mainContent.widthProperty().multiply(0.40));
        leftPanel.minWidthProperty().bind(mainContent.widthProperty().multiply(0.40));

        rightPanel.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.60));

        mainContent.getChildren().addAll(leftPanel, rightPanel);
        root.setCenter(mainContent);
        root.setBottom(createFooter());

        Scene scene = new Scene(root, 1280, 850);

        String cssResource = getClass().getResource("/css/auth.css") != null
                ? getClass().getResource("/css/auth.css").toExternalForm()
                : null;
        if (cssResource != null) {
            scene.getStylesheets().add(cssResource);
        }

        return scene;
    }

    // =========================================================================
    // LEFT MARKETING PANEL (100% Reused Branding)
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
                createFeatureCard("🔒 Secure Patient Vault", "Your personal medical profile is encrypted and accessible only by you and authorized doctors."),
                createFeatureCard("🤖 Personal Health Assistant", "AI-driven symptom tracking, appointment scheduling, and automated prescription reminders."),
                createFeatureCard("⚡ Instant Emergency Profile", "Emergency contact and medical baseline data immediately available during urgent care visits.")
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

    // =========================================================================
    // RIGHT PANEL - SUCCESS CARD
    // =========================================================================
    private ScrollPane createRightFormPanel() {
        VBox outerWrapper = new VBox();
        outerWrapper.setAlignment(Pos.CENTER);
        outerWrapper.setPadding(new Insets(32, 24, 32, 24));

        VBox mainCard = new VBox(24);
        mainCard.getStyleClass().add("main-white-card");
        mainCard.setMaxWidth(680);
        mainCard.setAlignment(Pos.TOP_CENTER);

        // 1. Logo
        ImageView centeredLogo = createSafeImageView("/images/logo.png", 48, 48);
        Node logoGraphic = centeredLogo.getImage() != null ? centeredLogo : createFallbackLogoGraphic();

        // 2. Success Illustration (~170x170)
        Node illustrationNode = createSuccessIllustration();

        // 3. Heading
        Text headingText = new Text("Welcome to Health-Sphere!");
        headingText.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-fill: #111827;");

        // 4. Subtitle
        Text subtitleText = new Text("Your account has been successfully created and verified.");
        subtitleText.setStyle("-fx-font-size: 15px; -fx-fill: #6B7280;");
        subtitleText.setTextAlignment(TextAlignment.CENTER);

        // 5. Description Paragraph
        Text descriptionText = new Text("You can now book appointments, manage your medical reports, access your personalized AI assistant, and keep your medical records organized in one secure place.");
        descriptionText.setStyle("-fx-font-size: 13px; -fx-fill: #6B7280; -fx-line-spacing: 4px;");
        descriptionText.setTextAlignment(TextAlignment.CENTER);
        descriptionText.setWrappingWidth(540);

        // 6. Account Activated Card
        VBox activatedCard = createAccountActivatedCard();

        // 7. Buttons HBox
        HBox buttonBox = createButtonLayout();

        mainCard.getChildren().addAll(
                logoGraphic,
                illustrationNode,
                headingText,
                subtitleText,
                descriptionText,
                activatedCard,
                buttonBox
        );

        outerWrapper.getChildren().add(mainCard);

        // Micro Animations Initiation
        playEntryAnimations(illustrationNode, headingText, activatedCard, buttonBox);

        ScrollPane scrollPane = new ScrollPane(outerWrapper);
        scrollPane.getStyleClass().add("right-scroll-pane");
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    // =========================================================================
    // SUCCESS ILLUSTRATION WITH SOFT GREEN GLOW
    // =========================================================================
    private Node createSuccessIllustration() {
        ImageView illustrationImg = createSafeImageView("/images/registration-success.png", 170, 170);
        if (illustrationImg.getImage() != null) {
            return illustrationImg;
        }

        // Custom Vector Graphic: Shield + Green Check Circle
        StackPane illustrationPane = new StackPane();
        illustrationPane.setPrefSize(170, 170);
        illustrationPane.setMaxSize(170, 170);

        // Soft Green Glow Background
        Circle glowCircle = new Circle(75, Color.web("#DCFCE7"));
        DropShadow softGlow = new DropShadow();
        softGlow.setColor(Color.rgb(16, 185, 129, 0.35));
        softGlow.setRadius(25);
        glowCircle.setEffect(softGlow);

        Circle innerCircle = new Circle(55, Color.web("#10B981"));

        Text checkSymbol = new Text("✓");
        checkSymbol.setStyle("-fx-font-size: 56px; -fx-font-weight: bold; -fx-fill: white;");

        Text shieldBadge = new Text("🛡️");
        shieldBadge.setStyle("-fx-font-size: 26px;");
        StackPane.setAlignment(shieldBadge, Pos.TOP_RIGHT);
        StackPane.setMargin(shieldBadge, new Insets(12, 18, 0, 0));

        illustrationPane.getChildren().addAll(glowCircle, innerCircle, checkSymbol, shieldBadge);
        return illustrationPane;
    }

    // =========================================================================
    // ACCOUNT ACTIVATED CARD
    // =========================================================================
    private VBox createAccountActivatedCard() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(20));
        card.setMaxWidth(580);
        card.setStyle("-fx-background-color: #F0FDF4; -fx-border-color: #E5E7EB; -fx-border-width: 1px; -fx-border-radius: 12px; -fx-background-radius: 12px;");

        // Header Line
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);

        Text headerCheck = new Text("✓");
        headerCheck.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #10B981;");

        Text headerTitle = new Text("Account Activated");
        headerTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-fill: #065F46;");

        header.getChildren().addAll(headerCheck, headerTitle);

        // Thin Divider
        Line divider = new Line(0, 0, 538, 0);
        divider.setStyle("-fx-stroke: #D1FAE5; -fx-stroke-width: 1px;");

        // Checklist Items
        VBox checklist = new VBox(12);

        checklist.getChildren().addAll(
                createChecklistRow("Email Verified"),
                createChecklistRow("Secure Account Created"),
                createChecklistRow("AI Healthcare Enabled"),
                createChecklistRow("Ready to Access Dashboard")
        );

        card.getChildren().addAll(header, divider, checklist);
        return card;
    }

    private HBox createChecklistRow(String text) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        Text greenCheck = new Text("✓");
        greenCheck.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-fill: #10B981;");

        Text rowText = new Text(text);
        rowText.setStyle("-fx-font-size: 13px; -fx-fill: #047857; -fx-font-weight: 500;");

        row.getChildren().addAll(greenCheck, rowText);
        return row;
    }

    // =========================================================================
    // BUTTONS LAYOUT (HBox) WITH DIRECT PURE JAVAFX NAVIGATION
    // =========================================================================
    private HBox createButtonLayout() {
        HBox box = new HBox(16);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(8, 0, 0, 0));

        // Secondary Button: White (220 x 50) -> Navigates to LoginView
        Button backToLoginBtn = new Button("Back to Login");
        backToLoginBtn.getStyleClass().add("btn-back");
        backToLoginBtn.setPrefSize(220, 50);
        backToLoginBtn.setOnAction(e -> {
            LoginView loginView = new LoginView(stage);
            stage.setScene(loginView.getScene());
        });

        // Primary Button: Blue #2563EB (250 x 50) -> Navigates to PatientDashboardView
        Button continueBtn = new Button("Continue to Dashboard →");
        continueBtn.getStyleClass().add("btn-continue");
        continueBtn.setPrefSize(250, 50);
        continueBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8px;");
        addBtnHoverEffect(continueBtn, Color.rgb(37, 99, 235, 0.40));
        
        // continueBtn.setOnAction(e -> {
        //     // Instantiate and navigate directly to Patient Dashboard
        //     // (Make sure PatientDashboardView exists in your patient view package)
        //     com.healthsphere.view.patient.PatientDashboardView dashboardView = 
        //         new com.healthsphere.view.patient.PatientDashboardView(stage);
        //     stage.setScene(dashboardView.getScene());
        // });

        box.getChildren().addAll(backToLoginBtn, continueBtn);
        return box;
    }

    // =========================================================================
    // MICRO ANIMATIONS
    // =========================================================================
    private void playEntryAnimations(Node illustration, Node heading, Node card, Node buttons) {
        // 1. Illustration: Scale 0.8 -> 1.0
        illustration.setScaleX(0.8);
        illustration.setScaleY(0.8);
        ScaleTransition scaleIllustration = new ScaleTransition(Duration.millis(350), illustration);
        scaleIllustration.setToX(1.0);
        scaleIllustration.setToY(1.0);

        // 2. Heading: Fade In
        heading.setOpacity(0.0);
        FadeTransition fadeHeading = new FadeTransition(Duration.millis(400), heading);
        fadeHeading.setToValue(1.0);

        // 3. Activated Card: Slide Up
        card.setTranslateY(25);
        card.setOpacity(0.0);
        TranslateTransition slideCard = new TranslateTransition(Duration.millis(400), card);
        slideCard.setToY(0);
        FadeTransition fadeCard = new FadeTransition(Duration.millis(400), card);
        fadeCard.setToValue(1.0);

        // 4. Buttons: Fade + Scale
        buttons.setOpacity(0.0);
        buttons.setScaleX(0.95);
        buttons.setScaleY(0.95);
        FadeTransition fadeButtons = new FadeTransition(Duration.millis(450), buttons);
        fadeButtons.setToValue(1.0);
        ScaleTransition scaleButtons = new ScaleTransition(Duration.millis(450), buttons);
        scaleButtons.setToX(1.0);
        scaleButtons.setToY(1.0);

        ParallelTransition animationSuite = new ParallelTransition(
                scaleIllustration,
                fadeHeading,
                slideCard,
                fadeCard,
                fadeButtons,
                scaleButtons
        );
        animationSuite.setDelay(Duration.millis(100));
        animationSuite.play();
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

    // =========================================================================
    // FOOTER
    // =========================================================================
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

        Text privacy = new Text("Privacy");
        privacy.getStyleClass().add("footer-hyperlink");

        Text terms = new Text("Terms");
        terms.getStyleClass().add("footer-hyperlink");

        Text security = new Text("Security");
        security.getStyleClass().add("footer-hyperlink");

        Text hipaa = new Text("HIPAA");
        hipaa.getStyleClass().add("footer-hyperlink");

        linksBox.getChildren().addAll(privacy, terms, security, hipaa);
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