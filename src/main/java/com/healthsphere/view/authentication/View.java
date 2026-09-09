package com.healthsphere.view.authentication;

import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * Main application window.
 *
 * IMPORTANT:
 * This class owns the ONE shared Stage used by the entire application.
 *
 * Other views such as LoginView, Dashboard, Appointments etc.
 * must ONLY replace the Scene.
 */
public class View extends Application {

        /*
         * =========================================================
         * ONE SHARED STAGE
         * =========================================================
         */
        public static Stage stage;
        private MediaPlayer mediaPlayer;

        public void stopAndDisposeVideo() {
                try {
                        if (mediaPlayer != null) {
                                mediaPlayer.setOnEndOfMedia(null);
                                mediaPlayer.setOnReady(null);
                                mediaPlayer.setOnError(null);
                                mediaPlayer.stop();
                                mediaPlayer.dispose();
                                mediaPlayer = null;
                                System.out.println("✅ View MediaPlayer stopped and disposed successfully.");
                        }
                } catch (Exception e) {
                        System.err.println("Error disposing View MediaPlayer: " + e.getMessage());
                }
        }

        /*
         * =========================================================
         * START APPLICATION
         * =========================================================
         */
        @Override
        public void start(Stage primaryStage) {

                stage = primaryStage;

                stage.setTitle(
                                "Health-Sphere | AI Powered Healthcare Management System");

                /*
                 * Prevent the application from becoming a tiny window.
                 */
                stage.setMinWidth(1100);
                stage.setMinHeight(700);

                /*
                 * =====================================================
                 * INITIAL SCENE
                 * =====================================================
                 */
                Scene initialScene = getScene();

                stage.setScene(initialScene);

                /*
                 * =====================================================
                 * SHOW WINDOW
                 * =====================================================
                 */
                stage.show();

                /*
                 * =====================================================
                 * MAXIMIZE AFTER SHOW
                 * =====================================================
                 *
                 * On Windows, maximizing BEFORE the Stage is actually
                 * displayed can sometimes be ignored/reset.
                 *
                 * Therefore:
                 *
                 * 1. show()
                 * 2. maximize()
                 * 3. force layout
                 */
                maximizeWindow();

                /*
                 * =====================================================
                 * FINAL LAYOUT PASS
                 * =====================================================
                 */
                Platform.runLater(() -> {

                        maximizeWindow();

                        if (stage.getScene() != null) {

                                stage.getScene()
                                                .getRoot()
                                                .applyCss();

                                stage.getScene()
                                                .getRoot()
                                                .layout();
                        }
                });
        }

        /*
         * =========================================================
         * MAXIMIZE WINDOW
         * =========================================================
         */
        private void maximizeWindow() {

                if (stage == null) {
                        return;
                }

                /*
                 * First make sure the Stage is visible.
                 */
                if (!stage.isShowing()) {
                        stage.show();
                }

                /*
                 * Tell JavaFX/Windows to maximize.
                 */
                stage.setMaximized(true);

                /*
                 * If Windows does not immediately apply the state,
                 * apply it again on the next JavaFX pulse.
                 */
                Platform.runLater(() -> {

                        if (stage != null && stage.isShowing()) {
                                stage.setMaximized(true);
                        }
                });
        }

        // =========================================================
        // INITIAL SCENE
        // =========================================================

        public Scene getScene() {

                stopAndDisposeVideo();

                BorderPane root = new BorderPane();

                /*
                 * Allow the root to completely fill the Scene.
                 */
                root.setMinWidth(0);
                root.setMinHeight(0);

                root.setMaxWidth(
                                Double.MAX_VALUE);

                root.setMaxHeight(
                                Double.MAX_VALUE);

                root.getStyleClass().add(
                                "root");

                // Assemble Layout Sections
                root.setTop(createHeader());
                root.setCenter(createMainContent());
                root.setBottom(createFooter());

                double sceneWidth = stage != null && stage.getWidth() > 0 ? stage.getWidth() : 1200;
                double sceneHeight = stage != null && stage.getHeight() > 0 ? stage.getHeight() : 750;

                Scene scene = new Scene(
                                root,
                                sceneWidth,
                                sceneHeight);

                // Attach Stylesheet safely
                String cssPath = getClass().getResource("/css/view.css") != null
                                ? getClass().getResource("/css/view.css").toExternalForm()
                                : null;

                if (cssPath != null) {
                        scene.getStylesheets().add(cssPath);
                }

                // Automatic video cleanup when root is un-scened
                root.sceneProperty().addListener((obs, oldScene, newScene) -> {
                        if (newScene == null) {
                                stopAndDisposeVideo();
                        }
                });

                if (stage != null) {
                        javafx.beans.value.ChangeListener<Scene> sceneChangeListener = new javafx.beans.value.ChangeListener<>() {
                                @Override
                                public void changed(javafx.beans.value.ObservableValue<? extends Scene> observable, Scene oldVal, Scene newVal) {
                                        if (oldVal == scene && newVal != scene) {
                                                stopAndDisposeVideo();
                                                stage.sceneProperty().removeListener(this);
                                        }
                                }
                        };
                        stage.sceneProperty().addListener(sceneChangeListener);
                }

                return scene;
        }

        // =========================================================
        // HEADER
        // =========================================================

        private HBox createHeader() {

                HBox header = new HBox(12);

                header.getStyleClass().add("header-bar");

                header.setAlignment(Pos.CENTER_LEFT);

                // Header Logo Icon
                ImageView headerLogo = createSafeImageView("/images/icons/brand_logo.png", 32, 32);

                // App Branding Text
                Text brandText = new Text("HealthSphere");

                brandText.getStyleClass().add("brand-title");

                HBox brandBox = new HBox(10, headerLogo, brandText);
                brandBox.setAlignment(Pos.CENTER_LEFT);

                // Spacer pushes controls to the right
                Region spacer = new Region();

                HBox.setHgrow(
                                spacer,
                                Priority.ALWAYS);

                // Utility Buttons
                HBox utilityBox = new HBox(12);

                utilityBox.setAlignment(
                                Pos.CENTER_RIGHT);

                Button helpBtn = createIconButton(
                                "/images/icons/icon_help.png",
                                "❓");

                Button newSessionBtn = new Button("New Session");

                newSessionBtn.getStyleClass().add(
                                "btn-primary");

                newSessionBtn.setOnAction(e -> {
                        stopAndDisposeVideo();
                        if (stage != null) {
                                stage.setScene(new LoginView(stage).getScene());
                        }
                });

                utilityBox.getChildren().addAll(
                                helpBtn,
                                newSessionBtn);

                header.getChildren().addAll(
                                brandBox,
                                spacer,
                                utilityBox);

                return header;
        }

        // =========================================================
        // MAIN CONTENT
        // =========================================================

        private StackPane createMainContent() {

                StackPane wrapper = new StackPane();
                wrapper.setAlignment(Pos.CENTER);

                // Subtle ambient healthcare background graphics (very low opacity)
                Circle ambientBg1 = new Circle(280, Color.web("#2F80ED", 0.04));
                ambientBg1.setTranslateX(-350);
                ambientBg1.setTranslateY(-120);

                Circle ambientBg2 = new Circle(200, Color.web("#12355B", 0.03));
                ambientBg2.setTranslateX(400);
                ambientBg2.setTranslateY(150);

                HBox mainContainer = new HBox(40);

                mainContainer.setPadding(
                                new Insets(
                                                30,
                                                60,
                                                30,
                                                60));

                mainContainer.setAlignment(
                                Pos.CENTER);

                // --- LEFT COLUMN: CTA Content ---
                VBox leftContent = new VBox(20);

                leftContent.setAlignment(
                                Pos.CENTER_LEFT);

                HBox.setHgrow(
                                leftContent,
                                Priority.ALWAYS);

                leftContent.setMaxWidth(560);

                // Prominent HealthSphere Logo (Increased size, aspect ratio preserved)
                ImageView logoView = createSafeImageView(
                                "/images/icons/brand_logo.png",
                                160,
                                160);

                // Brand Headline
                Text titleLine1 = new Text("The Future of\n");
                titleLine1.getStyleClass().add("hero-title-dark");

                Text titleLine2 = new Text("Connected Healthcare.");
                titleLine2.getStyleClass().add("hero-title-blue");

                TextFlow headline = new TextFlow(
                                titleLine1,
                                titleLine2);

                // Platform Subtitle
                Label platformSub = new Label("AI Powered Healthcare Management System");
                platformSub.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2F80ED;");

                // Tagline Quote
                Label tagline = new Label("\"Connecting Patients, Doctors and Hospitals\"");
                tagline.getStyleClass().add("hero-subtitle-tagline");

                // Subtitle Description
                Label description = new Label(
                                "Precision care at scale. Intelligently connecting hospital operations and patient outcomes.");

                description.getStyleClass().add(
                                "hero-description");

                description.setWrapText(true);

                // Action Buttons Row (Sign Up & Register)
                HBox buttonRow = new HBox(16);

                buttonRow.setAlignment(
                                Pos.CENTER_LEFT);

                buttonRow.setPadding(
                                new Insets(
                                                12,
                                                0,
                                                0,
                                                0));

                Button signUpBtn = new Button("Get Started / Log In");

                signUpBtn.getStyleClass().add(
                                "btn-primary");
                signUpBtn.setOnAction(e -> {
                        stopAndDisposeVideo();
                        // Direct stage scene switching using shared static stage
                        View.stage.setScene(
                                        new LoginView(
                                                        View.stage).getScene());
                });

                Button registerBtn = new Button("Register");

                registerBtn.getStyleClass().add(
                                "btn-teal");

                registerBtn.setOnAction(e -> {
                        stopAndDisposeVideo();
                        // Direct stage scene switching using shared static stage
                        View.stage.setScene(
                                        new RegisterView(
                                                        View.stage).getScene());
                });

                buttonRow.getChildren().addAll(
                                signUpBtn,
                                registerBtn);

                leftContent.getChildren().addAll(
                                logoView,
                                headline,
                                platformSub,
                                tagline,
                                description,
                                buttonRow);

                // --- RIGHT COLUMN: Visual Banner & Overlay ---
                StackPane rightVisual = new StackPane();

                HBox.setHgrow(
                                rightVisual,
                                Priority.ALWAYS);

                rightVisual.setAlignment(
                                Pos.BOTTOM_LEFT);

                // ==========================================
                // HERO VIDEO
                // ==========================================

                MediaView heroVideoView = createHeroVideoView();

                // Clip rounded corners on video
                Rectangle videoClip = new Rectangle(
                                560,
                                360);

                videoClip.setArcWidth(24);
                videoClip.setArcHeight(24);

                heroVideoView.setClip(
                                videoClip);

                // Overlay Banner Text
                VBox imageOverlayText = new VBox(6);

                imageOverlayText.setAlignment(
                                Pos.BOTTOM_LEFT);

                imageOverlayText.setPadding(
                                new Insets(24));

                Text imgTitle = new Text(
                                "Precision care at scale.");

                imgTitle.setStyle(
                                "-fx-font-size: 22px;" +
                                                " -fx-font-weight: bold;" +
                                                " -fx-fill: #142901;");

                Text imgSub = new Text(
                                "Intelligently connecting hospital operations and patient outcomes.");

                imgSub.setStyle(
                                "-fx-font-size: 13px;" +
                                                " -fx-fill: #000000;");

                imageOverlayText.getChildren().addAll(
                                imgTitle,
                                imgSub);

                StackPane imageWrapper = new StackPane();

                imageWrapper.getChildren().addAll(
                                heroVideoView,
                                imageOverlayText);

                // Floating "LIVE INSIGHT" Card
                VBox floatingCard = new VBox(6);

                floatingCard.getStyleClass().add(
                                "floating-card");

                floatingCard.setMaxSize(
                                160,
                                60);

                floatingCard.setTranslateX(-20);
                floatingCard.setTranslateY(20);

                HBox cardHeader = new HBox(6);

                cardHeader.setAlignment(
                                Pos.CENTER_LEFT);

                ImageView sparkleIcon = createSafeImageView(
                                "/images/icons/icon_sparkle.png",
                                14,
                                14);

                Text cardTitle = new Text(
                                "LIVE INSIGHT");

                cardTitle.getStyleClass().add(
                                "floating-card-title");

                cardHeader.getChildren().addAll(
                                sparkleIcon,
                                cardTitle);

                floatingCard.getChildren().add(
                                cardHeader);

                rightVisual.getChildren().addAll(
                                imageWrapper,
                                floatingCard);

                mainContainer.getChildren().addAll(
                                leftContent,
                                rightVisual);

                wrapper.getChildren().addAll(
                                ambientBg1,
                                ambientBg2,
                                mainContainer);

                return wrapper;
        }

        // =========================================================
        // FOOTER
        // =========================================================

        private HBox createFooter() {

                HBox footer = new HBox(12);

                footer.getStyleClass().add(
                                "footer-bar");

                footer.setAlignment(
                                Pos.CENTER_LEFT);

                Text footerBrand = new Text(
                                "HealthSphere");

                footerBrand.getStyleClass().add(
                                "footer-brand");

                Label copyLabel = new Label(
                                "© 2026 MediNexus AI. All rights reserved. Clinical precision at scale.");

                copyLabel.getStyleClass().add(
                                "footer-text");

                Region spacer = new Region();

                HBox.setHgrow(
                                spacer,
                                Priority.ALWAYS);

                HBox footerLinks = new HBox(16);

                footerLinks.setAlignment(
                                Pos.CENTER_RIGHT);

                Label versionLabel = new Label(
                                "Version 2.4.1-stable");

                versionLabel.getStyleClass().add(
                                "footer-text");

                Button tosBtn = new Button(
                                "Terms of Service");

                tosBtn.getStyleClass().add(
                                "footer-link");

                Button privacyBtn = new Button(
                                "Privacy Policy");

                privacyBtn.getStyleClass().add(
                                "footer-link");

                footerLinks.getChildren().addAll(
                                versionLabel,
                                tosBtn,
                                privacyBtn);

                footer.getChildren().addAll(
                                footerBrand,
                                copyLabel,
                                spacer,
                                footerLinks);

                return footer;
        }

        // ==========================================
        // HERO VIDEO HELPER
        // ==========================================
        private MediaView createHeroVideoView() {

                MediaView mediaView = new MediaView();
                stopAndDisposeVideo();

                try {

                        var resource = getClass().getResource(
                                        "/videos/healthsphere-splash.mp4");

                        if (resource == null) {

                                System.out.println(
                                                "❌ VIDEO NOT FOUND!");

                                System.out.println(
                                                "Expected path: /videos/healthsphere-splash.mp4");

                                return mediaView;
                        }

                        String videoPath = resource.toExternalForm();

                        System.out.println(
                                        "✅ VIDEO FOUND:");

                        System.out.println(videoPath);

                        Media media = new Media(videoPath);

                        media.setOnError(() -> {

                                System.out.println(
                                                "❌ MEDIA ERROR:");

                                if (media.getError() != null) {
                                        media.getError().printStackTrace();
                                }
                        });

                        mediaPlayer = new MediaPlayer(media);

                        mediaPlayer.setOnReady(() -> {
                                if (mediaPlayer != null) {
                                        System.out.println(
                                                        "✅ VIDEO READY");

                                        System.out.println(
                                                        "Video duration: "
                                                                        + media.getDuration());

                                        mediaPlayer.play();
                                }
                        });

                        mediaPlayer.setOnError(() -> {

                                System.out.println(
                                                "❌ MEDIAPLAYER ERROR:");

                                if (mediaPlayer != null && mediaPlayer.getError() != null) {
                                        mediaPlayer.getError().printStackTrace();
                                }
                        });

                        mediaPlayer.setOnEndOfMedia(() -> {
                                if (mediaPlayer != null) {
                                        System.out.println(
                                                        "🔄 Video restarting...");

                                        mediaPlayer.seek(
                                                        javafx.util.Duration.ZERO);

                                        mediaPlayer.play();
                                }
                        });

                        mediaPlayer.setMute(true);

                        mediaView.setMediaPlayer(
                                        mediaPlayer);

                        mediaView.setFitWidth(560);

                        mediaView.setFitHeight(360);

                        mediaView.setPreserveRatio(false);

                        return mediaView;

                } catch (Exception e) {

                        System.out.println(
                                        "❌ EXCEPTION WHILE LOADING VIDEO:");

                        e.printStackTrace();

                        return mediaView;
                }
        }

        // ==========================================
        // HELPER UTILITIES
        // ==========================================
        private ImageView createSafeImageView(
                        String path,
                        double width,
                        double height) {

                ImageView imgView = new ImageView();

                imgView.setFitWidth(
                                width);

                imgView.setFitHeight(
                                height);

                imgView.setPreserveRatio(
                                true);

                try {

                        if (getClass().getResource(path) != null) {

                                imgView.setImage(
                                                new Image(
                                                                getClass()
                                                                                .getResourceAsStream(path)));
                        }

                } catch (Exception ignored) {

                        // Gracefully handles missing assets during UI development
                }

                return imgView;
        }

        private Button createIconButton(
                        String imagePath,
                        String fallbackText) {

                Button btn = new Button();

                btn.getStyleClass().add(
                                "icon-btn");

                ImageView icon = createSafeImageView(
                                imagePath,
                                18,
                                18);

                if (icon.getImage() != null) {

                        btn.setGraphic(
                                        icon);

                } else {

                        btn.setText(
                                        fallbackText);
                }

                return btn;
        }
}