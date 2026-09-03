package com.healthsphere.view.Patient;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.healthsphere.controller.patient.PatientController;
import com.healthsphere.model.PatientProfile;

public class Dashboard {

    private final Stage stage;
    private final PatientController patientController;

    private Timeline imageTimeline;

    // =========================================================
    // DASHBOARD CAROUSEL IMAGES
    // =========================================================

    private final String[] carouselImages = {
            "/images/dashboard/dashboard1.jpg",
            "/images/dashboard/dashboard2.jpg",
            "/images/dashboard/dashboard3.jpg",
            "/images/dashboard/dashboard4.jpg"
    };

    private final String[] carouselTitles = {
            "Smart Healthcare",
            "Connected Care",
            "Your Health, Your Records",
            "Healthcare Made Simple"
    };

    private final String[] carouselSubtitles = {
            "Manage your healthcare journey with HealthSphere.",
            "Access your healthcare services from one place.",
            "Keep your important medical information organized.",
            "Everything you need for better healthcare management."
    };

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Dashboard(Stage stage) {
        this.stage = stage;
        this.patientController = new PatientController();
    }

    // =========================================================
    // MAIN SCENE
    // =========================================================

    public Scene getScene() {

        String patientName = loadPatientName();

        VBox content = new VBox(24);

        content.setPadding(new Insets(5));
        content.setFillWidth(true);
        content.setMinWidth(0);
        content.setMaxWidth(Double.MAX_VALUE);

        // =====================================================
        // WELCOME
        // =====================================================

        VBox welcomeBox = new VBox(6);

        Label welcome = new Label(
                "Good day, " + patientName + "!"
        );

        welcome.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label welcomeSubtitle = new Label(
                "Welcome back to HealthSphere. " +
                "Your healthcare journey starts here."
        );

        welcomeSubtitle.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #64748b;"
        );

        welcomeSubtitle.setWrapText(true);

        welcomeBox.getChildren().addAll(
                welcome,
                welcomeSubtitle
        );

        // =====================================================
        // ORIGINAL IMAGE CAROUSEL
        // =====================================================

        StackPane carousel = createImageCarousel();

        // =====================================================
        // SERVICES HEADING
        // =====================================================

        VBox servicesHeading = new VBox(5);

        Label servicesTitle = new Label(
                "Your Healthcare Services"
        );

        servicesTitle.setStyle(
                "-fx-font-size: 23px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label servicesSubtitle = new Label(
                "Access all your healthcare services quickly and easily."
        );

        servicesSubtitle.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #64748b;"
        );

        servicesSubtitle.setWrapText(true);

        servicesHeading.getChildren().addAll(
                servicesTitle,
                servicesSubtitle
        );

        // =====================================================
        // SERVICE CARDS
        // =====================================================

        VBox serviceCards = createServiceCards();

        // =====================================================
        // ADD CONTENT
        // =====================================================

        content.getChildren().addAll(
                welcomeBox,
                carousel,
                servicesHeading,
                serviceCards
        );

        return PatientUI.createScene(
                stage,
                "Dashboard",
                "Dashboard",
                "Manage your healthcare journey from one place.",
                content
        );
    }

    // =========================================================
    // LOAD PATIENT NAME
    // =========================================================

    private String loadPatientName() {

        try {

            PatientProfile profile =
                    patientController.getCurrentPatientProfile();

            if (profile == null) {
                return "Patient";
            }

            String firstName =
                    profile.getFirstName() == null
                            ? ""
                            : profile.getFirstName().trim();

            String lastName =
                    profile.getLastName() == null
                            ? ""
                            : profile.getLastName().trim();

            String fullName =
                    (firstName + " " + lastName).trim();

            if (fullName.isEmpty()) {
                return "Patient";
            }

            return fullName;

        } catch (Exception e) {

            System.err.println(
                    "Unable to load patient name: "
                            + e.getMessage()
            );

            return "Patient";
        }
    }

    // =========================================================
    // IMAGE CAROUSEL - KEPT AS ORIGINAL
    // =========================================================

    private StackPane createImageCarousel() {

        StackPane container = new StackPane();

        container.setPrefHeight(290);
        container.setMinHeight(290);
        container.setMaxHeight(290);

        container.setMaxWidth(Double.MAX_VALUE);

        ImageView imageView = new ImageView();

        imageView.setSmooth(true);
        imageView.setPreserveRatio(false);

        imageView.setFitHeight(290);

        imageView.fitWidthProperty().bind(
                container.widthProperty()
        );

        Rectangle imageClip = new Rectangle();

        imageClip.setArcWidth(30);
        imageClip.setArcHeight(30);

        imageClip.widthProperty().bind(
                container.widthProperty()
        );

        imageClip.setHeight(290);

        imageView.setClip(imageClip);

        Image firstImage =
                loadImage(carouselImages[0]);

        if (firstImage != null) {
            imageView.setImage(firstImage);
        }

        container.getChildren().add(imageView);

        // =====================================================
        // DARK OVERLAY
        // =====================================================

        Rectangle overlay = new Rectangle();

        overlay.setFill(
                Color.rgb(0, 0, 0, 0.30)
        );

        overlay.setArcWidth(30);
        overlay.setArcHeight(30);

        overlay.widthProperty().bind(
                container.widthProperty()
        );

        overlay.setHeight(290);

        container.getChildren().add(overlay);

        // =====================================================
        // TEXT
        // =====================================================

        VBox textBox = new VBox(7);

        textBox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(
                carouselTitles[0]
        );

        title.setStyle(
                "-fx-font-size: 29px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;"
        );

        Label subtitle = new Label(
                carouselSubtitles[0]
        );

        subtitle.setWrapText(true);
        subtitle.setMaxWidth(500);

        subtitle.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: white;"
        );

        textBox.getChildren().addAll(
                title,
                subtitle
        );

        StackPane.setAlignment(
                textBox,
                Pos.CENTER_LEFT
        );

        StackPane.setMargin(
                textBox,
                new Insets(0, 40, 0, 42)
        );

        container.getChildren().add(textBox);

        // =====================================================
        // DOTS
        // =====================================================

        HBox dotsBox = new HBox(7);

        dotsBox.setAlignment(Pos.CENTER);

        Circle[] dots =
                new Circle[carouselImages.length];

        for (int i = 0;
             i < carouselImages.length;
             i++) {

            Circle dot = new Circle(5);

            if (i == 0) {

                dot.setFill(Color.WHITE);

            } else {

                dot.setFill(
                        Color.rgb(
                                255,
                                255,
                                255,
                                0.45
                        )
                );
            }

            dots[i] = dot;

            dotsBox.getChildren().add(dot);
        }

        StackPane.setAlignment(
                dotsBox,
                Pos.BOTTOM_CENTER
        );

        StackPane.setMargin(
                dotsBox,
                new Insets(0, 0, 15, 0)
        );

        container.getChildren().add(dotsBox);

        // =====================================================
        // AUTO SLIDE
        // =====================================================

        final int[] index = {0};

        imageTimeline =
                new Timeline(
                        new KeyFrame(
                                Duration.seconds(4),
                                event -> {

                                    int next =
                                            (index[0] + 1)
                                                    % carouselImages.length;

                                    Image nextImage =
                                            loadImage(
                                                    carouselImages[next]
                                            );

                                    if (nextImage == null) {
                                        return;
                                    }

                                    FadeTransition fadeOut =
                                            new FadeTransition(
                                                    Duration.millis(300),
                                                    imageView
                                            );

                                    fadeOut.setFromValue(1);
                                    fadeOut.setToValue(0);

                                    fadeOut.setOnFinished(
                                            e -> {

                                                imageView.setImage(
                                                        nextImage
                                                );

                                                title.setText(
                                                        carouselTitles[next]
                                                );

                                                subtitle.setText(
                                                        carouselSubtitles[next]
                                                );

                                                for (int i = 0;
                                                     i < dots.length;
                                                     i++) {

                                                    if (i == next) {

                                                        dots[i].setFill(
                                                                Color.WHITE
                                                        );

                                                    } else {

                                                        dots[i].setFill(
                                                                Color.rgb(
                                                                        255,
                                                                        255,
                                                                        255,
                                                                        0.45
                                                                )
                                                        );
                                                    }
                                                }

                                                FadeTransition fadeIn =
                                                        new FadeTransition(
                                                                Duration.millis(300),
                                                                imageView
                                                        );

                                                fadeIn.setFromValue(0);
                                                fadeIn.setToValue(1);

                                                fadeIn.play();
                                            }
                                    );

                                    fadeOut.play();

                                    index[0] = next;
                                }
                        )
                );

        imageTimeline.setCycleCount(
                Timeline.INDEFINITE
        );

        imageTimeline.play();

        container.sceneProperty().addListener(
                (observable, oldScene, newScene) -> {

                    if (newScene == null &&
                            imageTimeline != null) {

                        imageTimeline.stop();
                    }
                }
        );

        return container;
    }

    // =========================================================
    // LOAD IMAGE SAFELY
    // =========================================================

    private Image loadImage(String path) {

        try {

            java.net.URL resource =
                    getClass().getResource(path);

            if (resource == null) {

                System.err.println(
                        "Image not found: " + path
                );

                return null;
            }

            return new Image(
                    resource.toExternalForm()
            );

        } catch (Exception e) {

            System.err.println(
                    "Unable to load image: " + path
            );

            return null;
        }
    }

    // =========================================================
    // SERVICE CARDS - THREE CARDS PER ROW
    // =========================================================

    private VBox createServiceCards() {

        VBox allRows = new VBox(22);

        allRows.setFillWidth(true);
        allRows.setAlignment(Pos.CENTER);

        // =====================================================
        // ROW 1
        // =====================================================

        HBox row1 = new HBox(20);

        row1.setAlignment(Pos.CENTER);

        VBox appointments =
                createServiceCard(
                        "Appointments",
                        "Book and manage your doctor and hospital appointments.",
                        "/images/appointments/appointment1.jpg",
                        this::showAppointments
                );

        VBox medicalRecords =
                createServiceCard(
                        "Medical Records",
                        "View and manage your medical reports and records.",
                        "/images/medicalrecords/medicalrecord1.jpg",
                        this::showMedicalRecords
                );

        VBox healthPassport =
                createServiceCard(
                        "Health Passport",
                        "View your personal health information and health identity.",
                        "/images/healthpassport/healthpassport1.jpg",
                        this::showHealthPassport
                );

        row1.getChildren().addAll(
                appointments,
                medicalRecords,
                healthPassport
        );

        // =====================================================
        // ROW 2
        // =====================================================

        HBox row2 = new HBox(20);

        row2.setAlignment(Pos.CENTER);

        VBox healthMate =
                createServiceCard(
                        "HealthMate",
                        "Get smart assistance for your healthcare questions and needs.",
                        "/images/ai/ai1.jpg",
                        this::showHealthMate
                );

        VBox emergency =
                createServiceCard(
                        "Emergency Assistance",
                        "Quickly access emergency healthcare assistance.",
                        "/images/emergency/emergency1.jpg",
                        this::showEmergencyAssistance
                );

        VBox hospitals =
                createServiceCard(
                        "Search Hospitals",
                        "Find hospitals and healthcare facilities near you.",
                        "/images/logo/search_hospital.jpg",
                        this::showSearchHospitals
                );

        row2.getChildren().addAll(
                healthMate,
                emergency,
                hospitals
        );

        // =====================================================
        // ROW 3
        // =====================================================

        HBox row3 = new HBox(20);

        row3.setAlignment(Pos.CENTER);

        VBox notifications =
                createServiceCard(
                        "Notifications",
                        "View your latest healthcare notifications and updates.",
                        "/images/logo/notification.jpg",
                        this::showNotifications
                );

        VBox profile =
                createServiceCard(
                        "Profile & Settings",
                        "Manage your personal profile and application settings.",
                        "/images/profile/profile1.jpg",
                        this::showProfileSettings
                );

        row3.getChildren().addAll(
                notifications,
                profile
        );

        allRows.getChildren().addAll(
                row1,
                row2,
                row3
        );

        return allRows;
    }

    // =========================================================
    // INDIVIDUAL SERVICE CARD
    // =========================================================

    private VBox createServiceCard(
            String title,
            String description,
            String imagePath,
            Runnable action
    ) {

        final double cardWidth = 290;
        final double cardHeight = 300;

        VBox card = new VBox(10);

        card.setPrefWidth(cardWidth);
        card.setMinWidth(cardWidth);
        card.setMaxWidth(cardWidth);

        card.setPrefHeight(cardHeight);
        card.setMinHeight(cardHeight);
        card.setMaxHeight(cardHeight);

        card.setAlignment(Pos.TOP_CENTER);

        card.setPadding(
                new Insets(16)
        );

        setNormalCardStyle(card);

        // =====================================================
        // IMAGE CONTAINER
        // =====================================================

        StackPane imageContainer =
                new StackPane();

        imageContainer.setPrefWidth(240);
        imageContainer.setPrefHeight(150);

        imageContainer.setMinWidth(240);
        imageContainer.setMinHeight(150);

        imageContainer.setMaxWidth(240);
        imageContainer.setMaxHeight(150);

        imageContainer.setAlignment(
                Pos.CENTER
        );

        imageContainer.setStyle(
                "-fx-background-color: #f8fafc;" +
                "-fx-background-radius: 16;"
        );

        // =====================================================
        // IMAGE
        // =====================================================

        ImageView imageView =
                new ImageView();

        imageView.setSmooth(true);

        // IMAGE DOES NOT STRETCH

        imageView.setPreserveRatio(true);

        // Margin around image

        imageView.setFitWidth(210);
        imageView.setFitHeight(125);

        Image image =
                loadImage(imagePath);

        if (image != null) {
            imageView.setImage(image);
        }

        imageContainer.getChildren().add(
                imageView
        );

        // =====================================================
        // TITLE
        // =====================================================

        Label titleLabel =
                new Label(title);

        titleLabel.setWrapText(true);

        titleLabel.setAlignment(
                Pos.CENTER
        );

        titleLabel.setMaxWidth(255);

        titleLabel.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        // =====================================================
        // DESCRIPTION
        // =====================================================

        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setWrapText(true);

        descriptionLabel.setAlignment(
                Pos.CENTER
        );

        descriptionLabel.setMaxWidth(250);

        descriptionLabel.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #64748b;" +
                "-fx-text-alignment: center;"
        );

        card.getChildren().addAll(
                imageContainer,
                titleLabel,
                descriptionLabel
        );

        // =====================================================
        // CLICK
        // =====================================================

        card.setOnMouseClicked(
                event -> {

                    if (action != null) {
                        action.run();
                    }
                }
        );

        // =====================================================
        // HOVER
        // =====================================================

        card.setOnMouseEntered(
                event -> {

                    card.setStyle(
                            "-fx-background-color: white;" +
                            "-fx-background-radius: 18;" +
                            "-fx-border-color: #2563eb;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 18;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(" +
                            "gaussian, rgba(15,23,42,0.18), 15, 0, 0, 5);"
                    );

                    card.setScaleX(1.01);
                    card.setScaleY(1.01);
                }
        );

        card.setOnMouseExited(
                event -> {

                    setNormalCardStyle(card);

                    card.setScaleX(1.0);
                    card.setScaleY(1.0);
                }
        );

        return card;
    }

    // =========================================================
    // NORMAL CARD STYLE
    // =========================================================

    private void setNormalCardStyle(
            VBox card
    ) {

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: #dbe3ea;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 18;" +
                "-fx-cursor: hand;"
        );
    }

    // =========================================================
    // NAVIGATION - APPOINTMENTS
    // =========================================================

    private void showAppointments() {

        if (stage == null) {
            return;
        }

        stage.setScene(
                new Appointments(stage).getScene()
        );

        stage.show();
    }

    // =========================================================
    // NAVIGATION - MEDICAL RECORDS
    // =========================================================

    private void showMedicalRecords() {

        if (stage == null) {
            return;
        }

        stage.setScene(
                new MedicalRecords(stage).getScene()
        );

        stage.show();
    }

    // =========================================================
    // NAVIGATION - HEALTH PASSPORT
    // =========================================================

    private void showHealthPassport() {

        if (stage == null) {
            return;
        }

        stage.setScene(
                new HealthPassport(stage).getScene()
        );

        stage.show();
    }

    // =========================================================
    // NAVIGATION - HEALTHMATE
    // =========================================================

    private void showHealthMate() {

        if (stage == null) {
            return;
        }

        stage.setScene(
                new AiHealthAssistant(stage).getScene()
        );

        stage.show();
    }

    // =========================================================
    // NAVIGATION - EMERGENCY ASSISTANCE
    // =========================================================

    private void showEmergencyAssistance() {

        if (stage == null) {
            return;
        }

        stage.setScene(
                new EmergencyAssistance(stage).getScene()
        );

        stage.show();
    }

    // =========================================================
    // NAVIGATION - SEARCH HOSPITALS
    // =========================================================

    private void showSearchHospitals() {

        if (stage == null) {
            return;
        }

        stage.setScene(
                new SearchHospitals(stage).getScene()
        );

        stage.show();
    }

    // =========================================================
    // NAVIGATION - NOTIFICATIONS
    // =========================================================

    private void showNotifications() {

        if (stage == null) {
            return;
        }

        stage.setScene(
                new Notifications(stage).getScene()
        );

        stage.show();
    }

    // =========================================================
    // NAVIGATION - PROFILE & SETTINGS
    // =========================================================

    private void showProfileSettings() {

        if (stage == null) {
            return;
        }

        stage.setScene(
                new ProfileSettings(stage).getScene()
        );

        stage.show();
    }
}