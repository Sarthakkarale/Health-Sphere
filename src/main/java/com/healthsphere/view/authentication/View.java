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

public class View extends Application {

    /*
     * =========================================================
     * ONE SHARED STAGE
     * =========================================================
     *
     * Every screen in the application must use this same Stage.
     */
    public static Stage stage;

    @Override
    public void start(Stage primaryStage) {

        stage = primaryStage;

        stage.setTitle(
                "Health-Sphere | AI Powered Healthcare Management System"
        );

        /*
         * Minimum usable window size.
         */
        stage.setMinWidth(1100);
        stage.setMinHeight(700);

        /*
         * Start maximized.
         */
        stage.setMaximized(true);

        /*
         * Initial screen.
         */
        stage.setScene(getScene());

        /*
         * Show the SAME Stage.
         */
        stage.show();

        /*
         * Make absolutely sure the initial window is maximized.
         */
        stage.setMaximized(true);
    }

    public Scene getScene() {

        BorderPane root =
                new BorderPane();

        root.getStyleClass().add("root");

        /*
         * IMPORTANT:
         *
         * No fixed Scene width.
         * No fixed Scene height.
         *
         * The Stage controls the actual size.
         */
        root.setTop(
                createHeader()
        );

        root.setCenter(
                createMainContent()
        );

        root.setBottom(
                createFooter()
        );

        Scene scene =
                new Scene(root);

        /*
         * Load CSS safely.
         */
        String cssPath = null;

        if (getClass().getResource(
                "/css/dashboard.css"
        ) != null) {

            cssPath =
                    getClass()
                            .getResource(
                                    "/css/dashboard.css"
                            )
                            .toExternalForm();
        }

        if (cssPath != null) {

            scene.getStylesheets().add(
                    cssPath
            );
        }

        return scene;
    }

    // =========================================================
    // HEADER
    // =========================================================

    private HBox createHeader() {

        HBox header =
                new HBox();

        header.getStyleClass().add(
                "header-bar"
        );

        header.setAlignment(
                Pos.CENTER_LEFT
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
                new Text("HS");

        avatarText.setStyle(
                "-fx-font-size: 10px;" +
                "-fx-fill: #475569;"
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

        /*
         * =====================================================
         * LEFT SIDE
         * =====================================================
         */

        VBox leftContent =
                new VBox(24);

        leftContent.setAlignment(
                Pos.CENTER_LEFT
        );

        leftContent.setMaxWidth(
                520
        );

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

        Label description =
                new Label(
                        "Precision care at scale. Intelligently " +
                        "connecting hospital operations and patient outcomes."
                );

        description.getStyleClass().add(
                "hero-description"
        );

        description.setWrapText(true);

        HBox buttonRow =
                new HBox(16);

        buttonRow.setAlignment(
                Pos.CENTER_LEFT
        );

        Button signUpBtn =
                new Button("Sign Up");

        signUpBtn.getStyleClass().add(
                "btn-primary"
        );

        signUpBtn.setOnAction(
                e -> {

                    stage.setScene(
                            new LoginView(stage)
                                    .getScene()
                    );

                    stage.show();
                    stage.setMaximized(true);
                }
        );

        Button registerBtn =
                new Button("Register");

        registerBtn.getStyleClass().add(
                "btn-teal"
        );

        registerBtn.setOnAction(
                e -> {

                    stage.setScene(
                            new RegisterView(stage)
                                    .getScene()
                    );

                    stage.show();
                    stage.setMaximized(true);
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
         * RIGHT SIDE
         * =====================================================
         */

        StackPane rightVisual =
                new StackPane();

        HBox.setHgrow(
                rightVisual,
                Priority.ALWAYS
        );

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

        imageWrapper.getChildren().add(
                heroImgView
        );

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
                        "Version 1.0.4-stable"
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

    // =========================================================
    // IMAGE
    // =========================================================

    private ImageView createSafeImageView(
            String path,
            double width,
            double height
    ) {

        ImageView imageView =
                new ImageView();

        imageView.setFitWidth(
                width
        );

        imageView.setFitHeight(
                height
        );

        imageView.setPreserveRatio(
                true
        );

        try {

            if (getClass().getResource(
                    path
            ) != null) {

                imageView.setImage(
                        new Image(
                                getClass()
                                        .getResourceAsStream(
                                                path
                                        )
                        )
                );
            }

        } catch (Exception ignored) {
        }

        return imageView;
    }

    // =========================================================
    // ICON BUTTON
    // =========================================================

    private Button createIconButton(
            String imagePath,
            String fallbackText
    ) {

        Button button =
                new Button();

        button.getStyleClass().add(
                "icon-btn"
        );

        ImageView icon =
                createSafeImageView(
                        imagePath,
                        18,
                        18
                );

        if (icon.getImage() != null) {

            button.setGraphic(
                    icon
            );

        } else {

            button.setText(
                    fallbackText
            );
        }

        return button;
    }
}