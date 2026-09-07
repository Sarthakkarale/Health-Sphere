package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class AboutUs {

    private final Stage stage;

    public AboutUs(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        VBox content = new VBox(24);
        content.setPadding(new Insets(10, 10, 30, 10));
        content.setFillWidth(true);
        content.setMinWidth(0);
        content.setMaxWidth(Double.MAX_VALUE);

        // =====================================================
        // 1. GRADIENT TOP HEADER BAR WITH BACK BUTTON
        // =====================================================
        HBox headerBar = new HBox(16);
        headerBar.setAlignment(Pos.CENTER_LEFT);
        headerBar.setPadding(new Insets(16, 24, 16, 24));
        headerBar.setStyle(
                "-fx-background-color: linear-gradient(to right, #12355B 0%, #2F80ED 60%, #7DAAF5 100%);" +
                "-fx-background-radius: 14px;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(18, 53, 91, 0.15), 14, 0, 0, 4);"
        );

        Button backBtn = new Button("←  Back");
        backBtn.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.18);" +
                "-fx-text-fill: #FFFFFF;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 8px;" +
                "-fx-padding: 8px 18px;" +
                "-fx-cursor: hand;"
        );
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.32);" +
                "-fx-text-fill: #FFFFFF;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 8px;" +
                "-fx-padding: 8px 18px;" +
                "-fx-cursor: hand;"
        ));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.18);" +
                "-fx-text-fill: #FFFFFF;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 8px;" +
                "-fx-padding: 8px 18px;" +
                "-fx-cursor: hand;"
        ));
        backBtn.setOnAction(e -> stage.setScene(new ProfileSettings(stage).getScene()));

        Label headerTitle = new Label("About Us");
        headerTitle.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #FFFFFF;"
        );

        headerBar.getChildren().addAll(backBtn, headerTitle);

        // =====================================================
        // 2. HEALTHSPHERE BRANDING SECTION
        // =====================================================
        VBox brandBox = new VBox(8);
        brandBox.setAlignment(Pos.CENTER);
        brandBox.setPadding(new Insets(10, 0, 6, 0));

        ImageView brandLogo = createSafeImageView("/images/icons/brand_logo.png", 90, 90);

        Label brandTitle = new Label("HealthSphere");
        brandTitle.setStyle(
                "-fx-font-size: 32px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: #12355B;"
        );

        Label brandSubtitle = new Label("AI Powered Healthcare Management System");
        brandSubtitle.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #2F80ED;"
        );

        Label brandTagline = new Label("\"Connecting Patients, Doctors and Hospitals\"");
        brandTagline.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-style: italic;" +
                "-fx-text-fill: #64748B;"
        );

        brandBox.getChildren().addAll(brandLogo, brandTitle, brandSubtitle, brandTagline);

        // =====================================================
        // 3. SHASHI SIR PHOTO SECTION (ONLY ONE PERSONAL PHOTO)
        // =====================================================
        VBox photoSection = new VBox(10);
        photoSection.setAlignment(Pos.CENTER);
        photoSection.setPadding(new Insets(6, 0, 10, 0));

        try {
            Image image = new Image(getClass().getResourceAsStream("/images/shashi_sir.jpg"));
            ImageView photoView = new ImageView(image);
            photoView.setPreserveRatio(true);
            photoView.setFitHeight(260);

            // Container frame with white rounded background and drop shadow
            StackPane frame = new StackPane(photoView);
            frame.setAlignment(Pos.CENTER);
            frame.setStyle(
                    "-fx-background-color: #FFFFFF;" +
                    "-fx-padding: 8px;" +
                    "-fx-background-radius: 18px;" +
                    "-fx-border-color: #CBD5E1;" +
                    "-fx-border-width: 1px;" +
                    "-fx-border-radius: 18px;" +
                    "-fx-effect: dropshadow(three-pass-box, rgba(18, 53, 91, 0.15), 18, 0, 0, 6);"
            );

            // Smooth rounded clipping on photo
            Rectangle clip = new Rectangle();
            clip.setArcWidth(22);
            clip.setArcHeight(22);
            clip.widthProperty().bind(frame.widthProperty());
            clip.heightProperty().bind(frame.heightProperty());
            frame.setClip(clip);

            Label nameCaption = new Label("Shashi Sir");
            nameCaption.setStyle(
                    "-fx-font-size: 18px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: #12355B;"
            );

            photoSection.getChildren().addAll(frame, nameCaption);
        } catch (Exception ex) {
            Label placeholder = new Label("Shashi Sir");
            placeholder.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #12355B;");
            photoSection.getChildren().add(placeholder);
        }

        // =====================================================
        // 4. SPECIAL THANKS CARD
        // =====================================================
        VBox thanksCard = createWhiteCard(
                "Special Thanks",
                "We extend our heartfelt gratitude to Shashi Sir for his exceptional teaching, " +
                "guidance and continuous encouragement throughout our learning journey. His dedication " +
                "helped us develop not only technical skills, but also the values of perseverance, clarity and continuous growth."
        );

        // =====================================================
        // 5. ABOUT HEALTHSPHERE CARD
        // =====================================================
        VBox aboutCard = new VBox(12);
        aboutCard.getStyleClass().add("white-card");
        aboutCard.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 16px;" +
                "-fx-padding: 24px 28px;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 16px;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(18, 53, 91, 0.06), 14, 0, 0, 4);"
        );

        Label aboutHeading = new Label("About HealthSphere");
        aboutHeading.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #12355B;"
        );

        Label aboutDesc1 = new Label(
                "HealthSphere is an AI-powered healthcare management system designed to connect " +
                "patients, doctors and hospitals through a unified digital healthcare platform."
        );
        aboutDesc1.setWrapText(true);
        aboutDesc1.setMaxWidth(Double.MAX_VALUE);
        aboutDesc1.setStyle("-fx-font-size: 14px; -fx-text-fill: #475569; -fx-line-spacing: 4px;");

        Label aboutDesc2 = new Label(
                "Our goal is to make healthcare management more connected, accessible and organized through a single platform."
        );
        aboutDesc2.setWrapText(true);
        aboutDesc2.setMaxWidth(Double.MAX_VALUE);
        aboutDesc2.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #12355B; -fx-line-spacing: 3px;");

        aboutCard.getChildren().addAll(aboutHeading, aboutDesc1, aboutDesc2);

        // =====================================================
        // 6. PLATFORM MODULES SECTION
        // =====================================================
        VBox modulesSection = new VBox(14);
        Label modulesTitle = new Label("Platform Modules");
        modulesTitle.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #12355B;"
        );

        GridPane modulesGrid = new GridPane();
        modulesGrid.setHgap(16);
        modulesGrid.setVgap(16);
        modulesGrid.setMaxWidth(Double.MAX_VALUE);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        modulesGrid.getColumnConstraints().addAll(col1, col2);

        VBox mod1 = createModuleCard("Patient Management", "Manage patient information, appointments and healthcare activities.");
        VBox mod2 = createModuleCard("Doctor Management", "Doctors can manage appointments, profiles, availability and patients.");
        VBox mod3 = createModuleCard("Hospital Management", "Hospitals can manage doctors, departments, beds, appointments and reviews.");
        VBox mod4 = createModuleCard("Medical Records", "Access and organize healthcare records and prescriptions.");
        VBox mod5 = createModuleCard("Health Passport", "Keep important patient health information organized digitally.");
        VBox mod6 = createModuleCard("Medical Tourism", "Help international/outstation patients plan and request medical treatment.");
        VBox mod7 = createModuleCard("Video Consultation", "Support remote patient-doctor consultations.");
        VBox mod8 = createModuleCard("Appointments", "Connect patients and doctors through appointment scheduling.");

        modulesGrid.add(mod1, 0, 0);
        modulesGrid.add(mod2, 1, 0);
        modulesGrid.add(mod3, 0, 1);
        modulesGrid.add(mod4, 1, 1);
        modulesGrid.add(mod5, 0, 2);
        modulesGrid.add(mod6, 1, 2);
        modulesGrid.add(mod7, 0, 3);
        modulesGrid.add(mod8, 1, 3);

        modulesSection.getChildren().addAll(modulesTitle, modulesGrid);

        // =====================================================
        // 7. PROJECT HIGHLIGHT CARD
        // =====================================================
        VBox highlightCard = new VBox(8);
        highlightCard.setStyle(
                "-fx-background-color: #EEF5FF;" +
                "-fx-border-color: #2F80ED;" +
                "-fx-border-width: 1.5px;" +
                "-fx-border-radius: 14px;" +
                "-fx-background-radius: 14px;" +
                "-fx-padding: 20px 24px;"
        );

        Label highlightTitle = new Label("Built to Connect Healthcare");
        highlightTitle.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #2F80ED;"
        );

        Label highlightText = new Label(
                "HealthSphere brings patients, doctors and hospitals together through one integrated healthcare management system."
        );
        highlightText.setWrapText(true);
        highlightText.setMaxWidth(Double.MAX_VALUE);
        highlightText.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #172B4D;" +
                "-fx-line-spacing: 3px;"
        );

        highlightCard.getChildren().addAll(highlightTitle, highlightText);

        // =====================================================
        // ASSEMBLE ALL CONTENT
        // =====================================================
        content.getChildren().addAll(
                headerBar,
                brandBox,
                photoSection,
                thanksCard,
                aboutCard,
                modulesSection,
                highlightCard
        );

        return PatientUI.createScene(
                stage,
                "Profile & Settings",
                "About Us",
                "HealthSphere — AI Powered Healthcare Management System",
                content
        );
    }

    // =========================================================
    // HELPER: CREATE WHITE CARD
    // =========================================================
    private VBox createWhiteCard(String headingText, String bodyText) {
        VBox card = new VBox(10);
        card.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 16px;" +
                "-fx-padding: 24px 28px;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 16px;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(18, 53, 91, 0.06), 14, 0, 0, 4);"
        );

        Label heading = new Label(headingText);
        heading.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #12355B;"
        );

        Label body = new Label(bodyText);
        body.setWrapText(true);
        body.setMaxWidth(Double.MAX_VALUE);
        body.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #475569;" +
                "-fx-line-spacing: 4px;"
        );

        card.getChildren().addAll(heading, body);
        return card;
    }

    // =========================================================
    // HELPER: CREATE MODULE CARD
    // =========================================================
    private VBox createModuleCard(String titleText, String descText) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16, 18, 16, 18));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 12px;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 12px;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(18, 53, 91, 0.04), 10, 0, 0, 2);"
        );

        HBox titleHeader = new HBox(8);
        titleHeader.setAlignment(Pos.CENTER_LEFT);

        Label dot = new Label("•");
        dot.setStyle("-fx-text-fill: #2F80ED; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label title = new Label(titleText);
        title.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #12355B;"
        );

        titleHeader.getChildren().addAll(dot, title);

        Label desc = new Label(descText);
        desc.setWrapText(true);
        desc.setMaxWidth(Double.MAX_VALUE);
        desc.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #64748B;" +
                "-fx-line-spacing: 3px;"
        );

        card.getChildren().addAll(titleHeader, desc);
        return card;
    }

    // =========================================================
    // HELPER: SAFE IMAGE VIEW
    // =========================================================
    private ImageView createSafeImageView(String path, double width, double height) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(true);

        try {
            if (getClass().getResource(path) != null) {
                imageView.setImage(new Image(getClass().getResourceAsStream(path)));
            }
        } catch (Exception ignored) {
        }

        return imageView;
    }
}
