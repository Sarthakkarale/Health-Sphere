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

    /*
     * =========================================================
     * START APPLICATION
     * =========================================================
     */
    @Override
    public void start(Stage primaryStage) {

        stage = primaryStage;

        stage.setTitle(
                "Health-Sphere | AI Powered Healthcare Management System"
        );

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

        BorderPane root =
                new BorderPane();

        /*
         * Allow the root to completely fill the Scene.
         */
        root.setMinWidth(0);
        root.setMinHeight(0);

        root.setMaxWidth(
                Double.MAX_VALUE
        );

        root.setMaxHeight(
                Double.MAX_VALUE
        );

        root.getStyleClass().add(
                "root"
        );

        /*
         * =====================================================
         * HEADER
         * =====================================================
         */
        root.setTop(
                createHeader()
        );

        /*
         * =====================================================
         * MAIN
         * =====================================================
         */
        NodeWrapper mainContent =
                new NodeWrapper(
                        createMainContent()
                );

        root.setCenter(
                mainContent.getNode()
        );

        /*
         * =====================================================
         * FOOTER
         * =====================================================
         */
        root.setBottom(
                createFooter()
        );

        /*
         * =====================================================
         * SCENE
         * =====================================================
         */
        Scene scene =
                new Scene(
                        root,
                        1100,
                        700
                );

        /*
         * =====================================================
         * CSS
         * =====================================================
         */
        if (getClass().getResource(
                "/css/dashboard.css"
        ) != null) {

            scene.getStylesheets().add(
                    getClass()
                            .getResource(
                                    "/css/dashboard.css"
                            )
                            .toExternalForm()
            );
        }

        /*
         * =====================================================
         * ROOT FILL SCENE
         * =====================================================
         */
        root.prefWidthProperty().bind(
                scene.widthProperty()
        );

        root.prefHeightProperty().bind(
                scene.heightProperty()
        );

        return scene;
    }

    // =========================================================
    // HEADER
    // =========================================================

    private HBox createHeader() {
<<<<<<< HEAD

        HBox header = new HBox();

        header.getStyleClass().add("header-bar");

        header.setAlignment(Pos.CENTER_LEFT);

        // App Branding Text
        Text brandText = new Text("Health Sphere");

        brandText.getStyleClass().add("brand-title");

        // Spacer pushes controls to the right
        Region spacer = new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        // Utility Buttons
        HBox utilityBox = new HBox(12);

        utilityBox.setAlignment(
                Pos.CENTER_RIGHT
        );

        Button bellBtn = createIconButton(
                "/images/icons/icon_bell.png",
                "🔔"
        );

        Button helpBtn = createIconButton(
                "/images/icons/icon_help.png",
                "❓"
        );

        Button newSessionBtn =
                new Button("New Session");

        newSessionBtn.getStyleClass().add(
                "btn-primary"
        );

        // User Avatar Circle
        StackPane avatar = new StackPane();

        avatar.getStyleClass().add(
                "avatar-circle"
        );

        Text avatarText = new Text("img");

        avatarText.setStyle(
                "-fx-font-size: 10px; -fx-fill: #475569;"
=======

        HBox header =
                new HBox();

        header.getStyleClass().add(
                "header-bar"
        );

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        header.setMaxWidth(
                Double.MAX_VALUE
        );

        Text brandText =
                new Text(
                        "Health Sphere"
                );

        brandText.getStyleClass().add(
                "brand-title"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        HBox utilityBox =
                new HBox(12);

        utilityBox.setAlignment(
                Pos.CENTER_RIGHT
        );

        Button bellBtn =
                createIconButton(
                        "/images/icons/icon_bell.png",
                        "🔔"
                );

        Button helpBtn =
                createIconButton(
                        "/images/icons/icon_help.png",
                        "❓"
                );

        Button newSessionBtn =
                new Button(
                        "New Session"
                );

        newSessionBtn.getStyleClass().add(
                "btn-primary"
        );

        StackPane avatar =
                new StackPane();

        avatar.getStyleClass().add(
                "avatar-circle"
        );

        Text avatarText =
                new Text(
                        "HS"
                );

        avatarText.setStyle(
                "-fx-font-size: 10px;" +
                "-fx-fill: #475569;"
>>>>>>> origin/feature/patient
        );

        avatar.getChildren().add(
                avatarText
        );

        utilityBox.getChildren().addAll(
                bellBtn,
                helpBtn,
                newSessionBtn,
                avatar
        );

        header.getChildren().addAll(
                brandText,
                spacer,
                utilityBox
        );

        return header;
    }

<<<<<<< HEAD
    // ==========================================
    // 2. MAIN CONTENT SECTION
    // ==========================================
    private HBox createMainContent() {

        HBox mainContainer =
                new HBox(40);

        mainContainer.setPadding(
                new Insets(
                        40,
                        60,
                        40,
                        60
                )
        );

        mainContainer.setAlignment(
                Pos.CENTER
        );

        // --- LEFT COLUMN: CTA Content ---
=======
    // =========================================================
    // MAIN CONTENT
    // =========================================================

    private HBox createMainContent() {

        HBox mainContainer =
                new HBox(40);

        mainContainer.setPadding(
                new Insets(
                        30,
                        50,
                        30,
                        50
                )
        );

        mainContainer.setAlignment(
                Pos.CENTER
        );

        mainContainer.setMinWidth(0);
        mainContainer.setMinHeight(0);

        mainContainer.setMaxWidth(
                Double.MAX_VALUE
        );

        mainContainer.setMaxHeight(
                Double.MAX_VALUE
        );

        /*
         * =====================================================
         * LEFT
         * =====================================================
         */

>>>>>>> origin/feature/patient
        VBox leftContent =
                new VBox(24);

        leftContent.setAlignment(
                Pos.CENTER_LEFT
        );

<<<<<<< HEAD
        HBox.setHgrow(
                leftContent,
                Priority.ALWAYS
        );

        leftContent.setMaxWidth(520);

        ImageView logoView =
                createSafeImageView(
                        "/images/icons/brand_logo.png",
                        110,
                        110
                );

        // Headline Text
        Text titleLine1 =
                new Text(
                        "The Future of\n"
                );

        titleLine1.getStyleClass().add(
                "hero-title-dark"
        );

=======
        leftContent.setMinWidth(0);
        leftContent.setMaxWidth(520);

        HBox.setHgrow(
                leftContent,
                Priority.ALWAYS
        );

        ImageView logoView =
                createSafeImageView(
                        "/images/icons/brand_logo.png",
                        110,
                        110
                );

        Text titleLine1 =
                new Text(
                        "The Future of\n"
                );

        titleLine1.getStyleClass().add(
                "hero-title-dark"
        );

>>>>>>> origin/feature/patient
        Text titleLine2 =
                new Text(
                        "Connected Healthcare."
                );

        titleLine2.getStyleClass().add(
                "hero-title-blue"
        );

        TextFlow headline =
                new TextFlow(
                        titleLine1,
                        titleLine2
                );
<<<<<<< HEAD

        // Subtitle Description
        Label description =
                new Label(
                        "Precision care at scale. Intelligently connecting hospital operations and patient outcomes."
                );

        description.getStyleClass().add(
                "hero-description"
        );

        description.setWrapText(true);

        // Action Buttons Row (Sign Up & Register)
        HBox buttonRow =
                new HBox(16);

        buttonRow.setAlignment(
                Pos.CENTER_LEFT
        );

        buttonRow.setPadding(
                new Insets(
                        8,
                        0,
                        0,
                        0
                )
        );

        Button signUpBtn =
                new Button("Log In");

        signUpBtn.getStyleClass().add(
                "btn-primary"
        );
        signUpBtn.setOnAction(e -> {

            // Direct stage scene switching using shared static stage
            View.stage.setScene(
                    new LoginView(
                            View.stage
                    ).getScene()
            );
        });

        Button registerBtn =
                new Button("Register");

        registerBtn.getStyleClass().add(
                "btn-teal"
        );

        registerBtn.setOnAction(e -> {

            // Direct stage scene switching using shared static stage
            View.stage.setScene(
                    new RegisterView(
                            View.stage
                    ).getScene()
            );
        });

        buttonRow.getChildren().addAll(
                signUpBtn,
                registerBtn
        );

        leftContent.getChildren().addAll(
                logoView,
                headline,
                description,
                buttonRow
        );

        // --- RIGHT COLUMN: Visual Banner & Overlay ---
        StackPane rightVisual =
                new StackPane();

=======

        headline.setMaxWidth(
                Double.MAX_VALUE
        );

        Label description =
                new Label(
                        "Precision care at scale. Intelligently " +
                        "connecting hospital operations and patient outcomes."
                );

        description.getStyleClass().add(
                "hero-description"
        );

        description.setWrapText(true);

        description.setMaxWidth(
                Double.MAX_VALUE
        );

        /*
         * =====================================================
         * BUTTONS
         * =====================================================
         */

        HBox buttonRow =
                new HBox(16);

        buttonRow.setAlignment(
                Pos.CENTER_LEFT
        );

        Button signUpBtn =
                new Button(
                        "Sign Up"
                );

        signUpBtn.getStyleClass().add(
                "btn-primary"
        );

        signUpBtn.setOnAction(
                e -> {

                    stage.setScene(
                            new LoginView(stage)
                                    .getScene()
                    );

                    /*
                     * Keep the shared window maximized.
                     */
                    keepMaximized();
                }
        );

        Button registerBtn =
                new Button(
                        "Register"
                );

        registerBtn.getStyleClass().add(
                "btn-teal"
        );

        registerBtn.setOnAction(
                e -> {

                    stage.setScene(
                            new RegisterView(stage)
                                    .getScene()
                    );

                    keepMaximized();
                }
        );

        buttonRow.getChildren().addAll(
                signUpBtn,
                registerBtn
        );

        leftContent.getChildren().addAll(
                logoView,
                headline,
                description,
                buttonRow
        );

        /*
         * =====================================================
         * RIGHT VISUAL
         * =====================================================
         */

        StackPane rightVisual =
                new StackPane();

        rightVisual.setMinWidth(0);
        rightVisual.setMinHeight(0);

>>>>>>> origin/feature/patient
        HBox.setHgrow(
                rightVisual,
                Priority.ALWAYS
        );
<<<<<<< HEAD

        rightVisual.setAlignment(
                Pos.BOTTOM_LEFT
        );

        // ==========================================
        // HERO VIDEO
        // ==========================================

        MediaView heroVideoView =
                createHeroVideoView();

        // Clip rounded corners on video
        Rectangle videoClip =
                new Rectangle(
                        560,
                        360
                );

        videoClip.setArcWidth(24);
        videoClip.setArcHeight(24);

        heroVideoView.setClip(
                videoClip
        );

        // Overlay Banner Text
        VBox imageOverlayText =
                new VBox(6);

        imageOverlayText.setAlignment(
                Pos.BOTTOM_LEFT
        );

        imageOverlayText.setPadding(
                new Insets(24)
        );

        Text imgTitle =
                new Text(
                        "Precision care at scale."
                );

        imgTitle.setStyle(
                "-fx-font-size: 22px;" +
                " -fx-font-weight: bold;" +
                " -fx-fill: #142901;"
        );

        Text imgSub =
                new Text(
                        "Intelligently connecting hospital operations and patient outcomes."
                );

        imgSub.setStyle(
                "-fx-font-size: 13px;" +
                " -fx-fill: #000000;"
        );

        imageOverlayText.getChildren().addAll(
                imgTitle,
                imgSub
        );

        StackPane imageWrapper =
                new StackPane();

        imageWrapper.getChildren().addAll(
                heroVideoView,
                imageOverlayText
        );

        // Floating "LIVE INSIGHT" Card
        VBox floatingCard =
                new VBox(6);

        floatingCard.getStyleClass().add(
                "floating-card"
        );

        floatingCard.setMaxSize(
                160,
                60
        );

        floatingCard.setTranslateX(-20);
        floatingCard.setTranslateY(20);

        HBox cardHeader =
                new HBox(6);

        cardHeader.setAlignment(
                Pos.CENTER_LEFT
        );

        ImageView sparkleIcon =
                createSafeImageView(
                        "/images/icons/icon_sparkle.png",
                        14,
                        14
                );

=======

        ImageView heroImgView =
                createSafeImageView(
                        "/images/icons/hero_banner.png",
                        560,
                        360
                );

        heroImgView.setPreserveRatio(
                false
        );

        Rectangle clip =
                new Rectangle(
                        560,
                        360
                );

        clip.setArcWidth(24);
        clip.setArcHeight(24);

        heroImgView.setClip(
                clip
        );

        StackPane imageWrapper =
                new StackPane();

        imageWrapper.setMinWidth(0);
        imageWrapper.setMinHeight(0);

        imageWrapper.setMaxWidth(
                Double.MAX_VALUE
        );

        imageWrapper.getChildren().add(
                heroImgView
        );

        /*
         * =====================================================
         * FLOATING CARD
         * =====================================================
         */

        VBox floatingCard =
                new VBox(6);

        floatingCard.getStyleClass().add(
                "floating-card"
        );

        floatingCard.setMaxSize(
                160,
                60
        );

        floatingCard.setTranslateX(
                -20
        );

        floatingCard.setTranslateY(
                20
        );

        HBox cardHeader =
                new HBox(6);

        cardHeader.setAlignment(
                Pos.CENTER_LEFT
        );

        ImageView sparkleIcon =
                createSafeImageView(
                        "/images/icons/icon_sparkle.png",
                        14,
                        14
                );

>>>>>>> origin/feature/patient
        Text cardTitle =
                new Text(
                        "LIVE INSIGHT"
                );

        cardTitle.getStyleClass().add(
                "floating-card-title"
        );

        cardHeader.getChildren().addAll(
                sparkleIcon,
                cardTitle
        );

        floatingCard.getChildren().add(
                cardHeader
        );

        rightVisual.getChildren().addAll(
                imageWrapper,
                floatingCard
        );

        mainContainer.getChildren().addAll(
                leftContent,
                rightVisual
        );

        return mainContainer;
    }

    // =========================================================
    // FOOTER
    // =========================================================

    private HBox createFooter() {

        HBox footer =
                new HBox(12);

        footer.getStyleClass().add(
                "footer-bar"
        );

        footer.setAlignment(
                Pos.CENTER_LEFT
        );

<<<<<<< HEAD
        Text footerBrand =
                new Text(
                        "health sphere"
                );

        footerBrand.getStyleClass().add(
                "footer-brand"
        );

        Label copyLabel =
                new Label(
                        "© 2026 MediNexus AI. All rights reserved. Clinical precision at scale."
                );

        copyLabel.getStyleClass().add(
                "footer-text"
        );

        Region spacer =
                new Region();

=======
        footer.setMaxWidth(
                Double.MAX_VALUE
        );

        Text footerBrand =
                new Text(
                        "health sphere"
                );

        footerBrand.getStyleClass().add(
                "footer-brand"
        );

        Label copyLabel =
                new Label(
                        "© 2026 Health-Sphere. All rights reserved. " +
                        "Clinical precision at scale."
                );

        copyLabel.getStyleClass().add(
                "footer-text"
        );

        Region spacer =
                new Region();

>>>>>>> origin/feature/patient
        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        HBox footerLinks =
                new HBox(16);

        footerLinks.setAlignment(
                Pos.CENTER_RIGHT
        );

        Label versionLabel =
                new Label(
<<<<<<< HEAD
                        "Version 2.4.1-stable"
=======
                        "Version 1.0.4-stable"
>>>>>>> origin/feature/patient
                );

        versionLabel.getStyleClass().add(
                "footer-text"
        );

        Button tosBtn =
                new Button(
                        "Terms of Service"
                );

        tosBtn.getStyleClass().add(
                "footer-link"
        );

        Button privacyBtn =
                new Button(
                        "Privacy Policy"
                );

        privacyBtn.getStyleClass().add(
                "footer-link"
        );

        Button statusBtn =
                new Button(
                        "System Status"
                );

        statusBtn.getStyleClass().add(
                "footer-link"
        );

        footerLinks.getChildren().addAll(
                versionLabel,
                tosBtn,
                privacyBtn,
                statusBtn
        );

        footer.getChildren().addAll(
                footerBrand,
                copyLabel,
                spacer,
                footerLinks
        );

        return footer;
    }

<<<<<<< HEAD
    // ==========================================
    // HERO VIDEO HELPER
    // ==========================================
    private MediaView createHeroVideoView() {

    MediaView mediaView = new MediaView();

    try {

        var resource = getClass().getResource(
                "/videos/healthsphere-splash.mp4"
        );

        if (resource == null) {

            System.out.println(
                    "❌ VIDEO NOT FOUND!"
            );

            System.out.println(
                    "Expected path: /videos/healthsphere-splash.mp4"
            );

            return mediaView;
        }

        String videoPath = resource.toExternalForm();

        System.out.println(
                "✅ VIDEO FOUND:"
        );

        System.out.println(videoPath);

        Media media = new Media(videoPath);

        media.setOnError(() -> {

            System.out.println(
                    "❌ MEDIA ERROR:"
            );

            if (media.getError() != null) {
                media.getError().printStackTrace();
            }
        });

        MediaPlayer mediaPlayer =
                new MediaPlayer(media);

        mediaPlayer.setOnReady(() -> {

            System.out.println(
                    "✅ VIDEO READY"
            );

            System.out.println(
                    "Video duration: "
                    + media.getDuration()
            );

            mediaPlayer.play();
        });

        mediaPlayer.setOnError(() -> {

            System.out.println(
                    "❌ MEDIAPLAYER ERROR:"
            );

            if (mediaPlayer.getError() != null) {
                mediaPlayer.getError().printStackTrace();
            }
        });

        mediaPlayer.setOnEndOfMedia(() -> {

            System.out.println(
                    "🔄 Video restarting..."
            );

            mediaPlayer.seek(
                    javafx.util.Duration.ZERO
            );

            mediaPlayer.play();
        });

        mediaPlayer.setMute(true);

        mediaView.setMediaPlayer(
                mediaPlayer
        );

        mediaView.setFitWidth(560);

        mediaView.setFitHeight(360);

        mediaView.setPreserveRatio(false);

        return mediaView;

    } catch (Exception e) {

        System.out.println(
                "❌ EXCEPTION WHILE LOADING VIDEO:"
        );

        e.printStackTrace();

        return mediaView;
    }
}
    // ==========================================
    // HELPER UTILITIES
    // ==========================================
=======
    // =========================================================
    // SAFE IMAGE
    // =========================================================

>>>>>>> origin/feature/patient
    private ImageView createSafeImageView(
            String path,
            double width,
            double height
    ) {

<<<<<<< HEAD
        ImageView imgView =
                new ImageView();

        imgView.setFitWidth(
                width
        );

        imgView.setFitHeight(
                height
        );

        imgView.setPreserveRatio(
=======
        ImageView imageView =
                new ImageView();

        imageView.setFitWidth(
                width
        );

        imageView.setFitHeight(
                height
        );

        imageView.setPreserveRatio(
>>>>>>> origin/feature/patient
                true
        );

        try {

            if (getClass().getResource(path) != null) {

<<<<<<< HEAD
                imgView.setImage(
=======
                imageView.setImage(
>>>>>>> origin/feature/patient
                        new Image(
                                getClass()
                                        .getResourceAsStream(path)
                        )
                );
            }

        } catch (Exception ignored) {
<<<<<<< HEAD

            // Gracefully handles missing assets during UI development
        }

        return imgView;
    }

=======
        }

        return imageView;
    }

    // =========================================================
    // ICON BUTTON
    // =========================================================

>>>>>>> origin/feature/patient
    private Button createIconButton(
            String imagePath,
            String fallbackText
    ) {

<<<<<<< HEAD
        Button btn =
                new Button();

        btn.getStyleClass().add(
=======
        Button button =
                new Button();

        button.getStyleClass().add(
>>>>>>> origin/feature/patient
                "icon-btn"
        );

        ImageView icon =
                createSafeImageView(
                        imagePath,
                        18,
                        18
                );

        if (icon.getImage() != null) {

<<<<<<< HEAD
            btn.setGraphic(
=======
            button.setGraphic(
>>>>>>> origin/feature/patient
                    icon
            );

        } else {

<<<<<<< HEAD
            btn.setText(
                    fallbackText
            );
        }

        return btn;
=======
            button.setText(
                    fallbackText
            );
        }

        return button;
    }

    // =========================================================
    // KEEP WINDOW MAXIMIZED
    // =========================================================

    private void keepMaximized() {

        if (stage == null) {
            return;
        }

        Platform.runLater(() -> {

            if (stage.isShowing()) {

                stage.setMaximized(true);

                if (stage.getScene() != null) {

                    stage.getScene()
                            .getRoot()
                            .applyCss();

                    stage.getScene()
                            .getRoot()
                            .layout();
                }
            }
        });
    }

    // =========================================================
    // SIMPLE NODE WRAPPER
    // =========================================================

    private static class NodeWrapper {

        private final javafx.scene.Node node;

        NodeWrapper(
                javafx.scene.Node node
        ) {

            this.node = node;
        }

        javafx.scene.Node getNode() {

            return node;
        }
>>>>>>> origin/feature/patient
    }
}