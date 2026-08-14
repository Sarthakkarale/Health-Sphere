package com.healthsphere.view.authentication;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * Main Entry View Class for Health-Sphere UI.
 * Extends Application and holds the central shared static Stage for navigation.
 */
public class View extends Application {

    // Single static Stage shared across all views in the application
    public static Stage stage;

    @Override
    public void start(Stage primaryStage) {
        View.stage = primaryStage;
        View.stage.setTitle("Health-Sphere | AI Powered Healthcare Management System");

        View.stage.setScene(getScene());
        View.stage.centerOnScreen();
        View.stage.setMaximized(true);
        View.stage.show();
    }

    public Scene getScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        // Assemble Layout Sections
        root.setTop(createHeader());
        root.setCenter(createMainContent());
        root.setBottom(createFooter());

        Scene scene = new Scene(root,stage.getWidth(),stage.getHeight());

        // Attach Stylesheet safely
        String cssPath = getClass().getResource("/css/dashboard.css") != null 
                ? getClass().getResource("/css/dashboard.css").toExternalForm() 
                : null;
        if (cssPath != null) {
            scene.getStylesheets().add(cssPath);
        }

        return scene;
    }

    // ==========================================
    // 1. HEADER SECTION
    // ==========================================
    private HBox createHeader() {
        HBox header = new HBox();
        header.getStyleClass().add("header-bar");
        header.setAlignment(Pos.CENTER_LEFT);

        // App Branding Text
        Text brandText = new Text("Health Sphere");
        brandText.getStyleClass().add("brand-title");

        // Spacer pushes controls to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Utility Buttons
        HBox utilityBox = new HBox(12);
        utilityBox.setAlignment(Pos.CENTER_RIGHT);

        Button bellBtn = createIconButton("/images/icons/icon_bell.png", "🔔");
        Button helpBtn = createIconButton("/images/icons/icon_help.png", "❓");

        Button newSessionBtn = new Button("New Session");
        newSessionBtn.getStyleClass().add("btn-primary");

        // User Avatar Circle
        StackPane avatar = new StackPane();
        avatar.getStyleClass().add("avatar-circle");
        Text avatarText = new Text("img");
        avatarText.setStyle("-fx-font-size: 10px; -fx-fill: #475569;");
        avatar.getChildren().add(avatarText);

        utilityBox.getChildren().addAll(bellBtn, helpBtn, newSessionBtn, avatar);

        header.getChildren().addAll(brandText, spacer, utilityBox);
        return header;
    }

    // ==========================================
    // 2. MAIN CONTENT SECTION
    // ==========================================
    private HBox createMainContent() {
        HBox mainContainer = new HBox(40);
        mainContainer.setPadding(new Insets(40, 60, 40, 60));
        mainContainer.setAlignment(Pos.CENTER);

        // --- LEFT COLUMN: CTA Content ---
        VBox leftContent = new VBox(24);
        leftContent.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(leftContent, Priority.ALWAYS);
        leftContent.setMaxWidth(520);

        ImageView logoView = createSafeImageView("/images/icons/brand_logo.png", 110, 110);

        // Headline Text
        Text titleLine1 = new Text("The Future of\n");
        titleLine1.getStyleClass().add("hero-title-dark");

        Text titleLine2 = new Text("Connected Healthcare.");
        titleLine2.getStyleClass().add("hero-title-blue");

        TextFlow headline = new TextFlow(titleLine1, titleLine2);

        // Subtitle Description
        Label description = new Label("Precision care at scale. Intelligently connecting hospital operations and patient outcomes.");
        description.getStyleClass().add("hero-description");
        description.setWrapText(true);

        // Action Buttons Row (Sign Up & Register)
        HBox buttonRow = new HBox(16);
        buttonRow.setAlignment(Pos.CENTER_LEFT);
        buttonRow.setPadding(new Insets(8, 0, 0, 0));

        Button signUpBtn = new Button("Sign Up");
        signUpBtn.getStyleClass().add("btn-primary");
        signUpBtn.setOnAction(e -> {
            // Direct stage scene switching using shared static stage
            View.stage.setScene(new LoginView(View.stage).getScene());
        });

        Button registerBtn = new Button("Register");
        registerBtn.getStyleClass().add("btn-teal");
        registerBtn.setOnAction(e -> {
            // Direct stage scene switching using shared static stage
            View.stage.setScene(new RegisterView(View.stage).getScene());
        });

        buttonRow.getChildren().addAll(signUpBtn, registerBtn);

        leftContent.getChildren().addAll(logoView, headline, description, buttonRow);

        // --- RIGHT COLUMN: Visual Banner & Overlay ---
        StackPane rightVisual = new StackPane();
        HBox.setHgrow(rightVisual, Priority.ALWAYS);
        rightVisual.setAlignment(Pos.BOTTOM_LEFT);

        // Hero Image
        ImageView heroImgView = createSafeImageView("/images/icons/hero_banner.png", 560, 360);
        heroImgView.setPreserveRatio(false);

        // Clip rounded corners on image
        Rectangle clip = new Rectangle(560, 360);
        clip.setArcWidth(24);
        clip.setArcHeight(24);
        heroImgView.setClip(clip);

        // Overlay Banner Text
        VBox imageOverlayText = new VBox(6);
        imageOverlayText.setAlignment(Pos.BOTTOM_LEFT);
        imageOverlayText.setPadding(new Insets(24));

        Text imgTitle = new Text("Precision care at scale.");
        imgTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-fill: #142901;");

        Text imgSub = new Text("Intelligently connecting hospital operations and patient outcomes.");
        imgSub.setStyle("-fx-font-size: 13px; -fx-fill: #000000;");

        imageOverlayText.getChildren().addAll(imgTitle, imgSub);

        StackPane imageWrapper = new StackPane();
        imageWrapper.getChildren().addAll(heroImgView, imageOverlayText);

        // Floating "LIVE INSIGHT" Card
        VBox floatingCard = new VBox(6);
        floatingCard.getStyleClass().add("floating-card");
        floatingCard.setMaxSize(160, 60);
        floatingCard.setTranslateX(-20);
        floatingCard.setTranslateY(20);

        HBox cardHeader = new HBox(6);
        cardHeader.setAlignment(Pos.CENTER_LEFT);

        ImageView sparkleIcon = createSafeImageView("/images/icons/icon_sparkle.png", 14, 14);
        Text cardTitle = new Text("LIVE INSIGHT");
        cardTitle.getStyleClass().add("floating-card-title");
        cardHeader.getChildren().addAll(sparkleIcon, cardTitle);

        floatingCard.getChildren().add(cardHeader);

        rightVisual.getChildren().addAll(imageWrapper, floatingCard);

        mainContainer.getChildren().addAll(leftContent, rightVisual);
        return mainContainer;
    }

    // ==========================================
    // 3. FOOTER SECTION
    // ==========================================
    private HBox createFooter() {
        HBox footer = new HBox(12);
        footer.getStyleClass().add("footer-bar");
        footer.setAlignment(Pos.CENTER_LEFT);

        Text footerBrand = new Text("health sphere");
        footerBrand.getStyleClass().add("footer-brand");

        Label copyLabel = new Label("© 2026 MediNexus AI. All rights reserved. Clinical precision at scale.");
        copyLabel.getStyleClass().add("footer-text");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox footerLinks = new HBox(16);
        footerLinks.setAlignment(Pos.CENTER_RIGHT);

        Label versionLabel = new Label("Version 2.4.1-stable");
        versionLabel.getStyleClass().add("footer-text");

        Button tosBtn = new Button("Terms of Service");
        tosBtn.getStyleClass().add("footer-link");

        Button privacyBtn = new Button("Privacy Policy");
        privacyBtn.getStyleClass().add("footer-link");

        Button statusBtn = new Button("System Status");
        statusBtn.getStyleClass().add("footer-link");

        footerLinks.getChildren().addAll(versionLabel, tosBtn, privacyBtn, statusBtn);

        footer.getChildren().addAll(footerBrand, copyLabel, spacer, footerLinks);
        return footer;
    }

    // ==========================================
    // HELPER UTILITIES
    // ==========================================
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
            // Gracefully handles missing assets during UI development
        }
        return imgView;
    }

    private Button createIconButton(String imagePath, String fallbackText) {
        Button btn = new Button();
        btn.getStyleClass().add("icon-btn");
        ImageView icon = createSafeImageView(imagePath, 18, 18);

        if (icon.getImage() != null) {
            btn.setGraphic(icon);
        } else {
            btn.setText(fallbackText);
        }
        return btn;
    }
}