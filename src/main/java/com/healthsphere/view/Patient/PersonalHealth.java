package com.healthsphere.view.Patient;

import com.healthsphere.controller.patient.PatientController;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PersonalHealth {

    private final Stage stage;
    private final PatientController patientController;

    public PersonalHealth(Stage stage) {

        this.stage = stage;

        this.patientController =
                new PatientController();
    }

    // =========================================================
    // MAIN SCENE
    // =========================================================

    public Scene getScene() {

        VBox content =
                new VBox(20);

        content.setPadding(
                new Insets(8)
        );

        // =====================================================
        // HERO
        // =====================================================

        HBox hero =
                new HBox(25);

        hero.setPadding(
                new Insets(25)
        );

        hero.setAlignment(
                Pos.CENTER_LEFT
        );

        hero.setStyle(
                "-fx-background-color: linear-gradient(to right, #eff6ff, #f5f3ff);" +
                "-fx-background-radius: 20;" +
                "-fx-border-color: #c7d2fe;" +
                "-fx-border-radius: 20;" +
                "-fx-effect: dropshadow(gaussian, rgba(49,46,129,0.12), 15, 0, 0, 4);"
        );

        // =====================================================
        // HERO LEFT CONTENT
        // =====================================================

        VBox heroText =
                new VBox(10);

        heroText.setAlignment(
                Pos.CENTER_LEFT
        );

        HBox.setHgrow(
                heroText,
                Priority.ALWAYS
        );

        Label icon =
                new Label(
                        "❤️"
                );

        icon.setStyle(
                "-fx-font-size: 42px;"
        );

        Label title =
                new Label(
                        "Personal Health"
                );

        title.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #312e81;"
        );

        Label description =
                new Label(
                        "Understand the key areas of your health and the habits that can help you maintain them."
                );

        description.setWrapText(
                true
        );

        description.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #475569;"
        );

        heroText.getChildren().addAll(
                icon,
                title,
                description
        );

        // =====================================================
        // HERO IMAGE - PROFILE 6
        // =====================================================

        VBox heroImageContainer =
                new VBox();

        heroImageContainer.setAlignment(
                Pos.CENTER
        );

        heroImageContainer.setPadding(
                new Insets(5)
        );

        heroImageContainer.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: #c7d2fe;" +
                "-fx-border-radius: 16;" +
                "-fx-effect: dropshadow(gaussian, rgba(49,46,129,0.18), 12, 0, 0, 3);"
        );

        var imageResource =
                getClass().getResource(
                        "/images/profile/profile6.jpg"
                );

        if (imageResource != null) {

            Image personalHealthImage =
                    new Image(
                            imageResource.toExternalForm()
                    );

            ImageView imageView =
                    new ImageView(
                            personalHealthImage
                    );

            imageView.setFitWidth(
                    190
            );

            imageView.setFitHeight(
                    190
            );

            imageView.setPreserveRatio(
                    false
            );

            imageView.setSmooth(
                    true
            );

            heroImageContainer.getChildren().add(
                    imageView
            );

        } else {

            Label imageUnavailable =
                    new Label(
                            "Image unavailable"
                    );

            imageUnavailable.setStyle(
                    "-fx-text-fill: #64748b;" +
                    "-fx-font-size: 13px;"
            );

            heroImageContainer.getChildren().add(
                    imageUnavailable
            );

            System.err.println(
                    "Profile image not found: /images/profile/profile6.jpg"
            );
        }

        hero.getChildren().addAll(
                heroText,
                heroImageContainer
        );

        // =====================================================
        // HEALTH AREAS
        // =====================================================

        VBox heart =
                healthCard(
                        "❤️",
                        "Heart Health",
                        "#fee2e2",
                        "#991b1b",
                        new String[] {

                                "Stay physically active regularly.",

                                "Choose more vegetables, fruits, whole grains and healthy fats.",

                                "Avoid smoking and tobacco products.",

                                "Monitor blood pressure when recommended.",

                                "Discuss persistent chest discomfort or unusual symptoms with a healthcare professional."
                        }
                );

        VBox blood =
                healthCard(
                        "🩸",
                        "Blood Pressure",
                        "#fef3c7",
                        "#92400e",
                        new String[] {

                                "Limit excessive salt and highly processed foods.",

                                "Maintain regular physical activity.",

                                "Follow your healthcare professional's monitoring advice.",

                                "Take prescribed medicines exactly as directed.",

                                "Do not change medication without medical advice."
                        }
                );

        VBox mental =
                healthCard(
                        "🧠",
                        "Mental Wellbeing",
                        "#ede9fe",
                        "#6d28d9",
                        new String[] {

                                "Maintain regular sleep and daily routines.",

                                "Make time for relaxation and enjoyable activities.",

                                "Stay connected with supportive people.",

                                "Use breathing, mindfulness or other stress-management techniques.",

                                "Seek professional support when emotional difficulties persist or interfere with daily life."
                        }
                );

        VBox preventive =
                healthCard(
                        "🩺",
                        "Preventive Healthcare",
                        "#dcfce7",
                        "#166534",
                        new String[] {

                                "Keep recommended health checkups.",

                                "Keep vaccinations up to date according to medical advice.",

                                "Complete screenings appropriate for your age and individual risk.",

                                "Keep your medical information and emergency contact details current.",

                                "Discuss changes in your health with your healthcare professional."
                        }
                );

        // =====================================================
        // TWO COLUMN LAYOUT
        // =====================================================

        HBox healthRow1 =
                new HBox(18);

        HBox healthRow2 =
                new HBox(18);

        HBox.setHgrow(
                heart,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                blood,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                mental,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                preventive,
                Priority.ALWAYS
        );

        healthRow1.getChildren().addAll(
                heart,
                blood
        );

        healthRow2.getChildren().addAll(
                mental,
                preventive
        );

        // =====================================================
        // MEDICATION SAFETY
        // =====================================================

        VBox medication =
                simpleCard(
                        "💊  Medication Safety",
                        "#e0f2fe",
                        "#075985"
                );

        medication.getChildren().addAll(

                tip(
                        "Take medicines exactly as prescribed."
                ),

                tip(
                        "Keep a current list of your medicines."
                ),

                tip(
                        "Do not stop or change prescribed medicines without discussing it with your healthcare professional."
                ),

                tip(
                        "Tell healthcare professionals about medicines, allergies and supplements you use."
                )
        );

        // =====================================================
        // HEALTH MAINTENANCE PLAN
        // =====================================================

        VBox maintenance =
                simpleCard(
                        "📅  Personal Health Maintenance",
                        "#ccfbf1",
                        "#115e59"
                );

        maintenance.getChildren().addAll(

                maintenanceRow(
                        "1",
                        "Know your health information",
                        "Keep important details such as allergies, medications, blood group and emergency contacts up to date."
                ),

                maintenanceRow(
                        "2",
                        "Monitor recommended measurements",
                        "Track health measurements that your healthcare professional recommends for you."
                ),

                maintenanceRow(
                        "3",
                        "Keep appointments",
                        "Attend recommended checkups and follow-up appointments."
                ),

                maintenanceRow(
                        "4",
                        "Notice changes",
                        "Pay attention to new, persistent or worsening symptoms and discuss them with a healthcare professional."
                )
        );

        // =====================================================
        // WARNING / SAFETY
        // =====================================================

        VBox safety =
                new VBox(8);

        safety.setPadding(
                new Insets(20)
        );

        safety.setStyle(
                "-fx-background-color: #fff1f2;" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: #fecdd3;" +
                "-fx-border-radius: 15;"
        );

        Label safetyTitle =
                new Label(
                        "🚨 When to Seek Medical Help"
                );

        safetyTitle.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #9f1239;"
        );

        Label safetyText =
                new Label(
                        "Seek appropriate medical care for symptoms that are severe, sudden, persistent or worsening. For a possible medical emergency, use your local emergency services rather than relying on this application."
                );

        safetyText.setWrapText(
                true
        );

        safetyText.setStyle(
                "-fx-text-fill: #881337;" +
                "-fx-font-size: 14px;"
        );

        safety.getChildren().addAll(
                safetyTitle,
                safetyText
        );

        // =====================================================
        // BACK BUTTON
        // =====================================================

        Button back =
                new Button(
                        "← Back to Profile & Settings"
                );

        back.setStyle(
                "-fx-background-color: #0f172a;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 9;" +
                "-fx-padding: 11 20;" +
                "-fx-cursor: hand;"
        );

        back.setOnAction(
                e -> stage.setScene(
                        new ProfileSettings(stage)
                                .getScene()
                )
        );

        // =====================================================
        // ADD CONTENT
        // =====================================================

        content.getChildren().addAll(

                hero,

                healthRow1,

                healthRow2,

                medication,

                maintenance,

                safety,

                back
        );

        // =====================================================
        // IMPORTANT:
        // NO INNER SCROLLPANE HERE
        //
        // PatientUI.createScene() handles the main screen
        // layout/scrolling. Adding another ScrollPane here
        // creates the double scrollbar.
        // =====================================================

        return PatientUI.createScene(
                stage,
                "Personal Health",
                "Personal Health",
                "Practical information for maintaining and monitoring your health.",
                content
        );
    }

    // =========================================================
    // HEALTH CARD
    // =========================================================

    private VBox healthCard(
            String icon,
            String title,
            String background,
            String textColor,
            String[] points
    ) {

        VBox card =
                simpleCard(
                        icon + "  " + title,
                        background,
                        textColor
                );

        for (String point : points) {

            card.getChildren().add(
                    tip(point)
            );
        }

        return card;
    }

    // =========================================================
    // SIMPLE CARD
    // =========================================================

    private VBox simpleCard(
            String title,
            String background,
            String textColor
    ) {

        VBox box =
                new VBox(12);

        box.setPadding(
                new Insets(20)
        );

        box.setStyle(
                "-fx-background-color: "
                        + background + ";" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: "
                        + background + ";" +
                "-fx-border-radius: 16;"
        );

        Label heading =
                new Label(
                        title
                );

        heading.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: "
                        + textColor + ";"
        );

        box.getChildren().add(
                heading
        );

        return box;
    }

    // =========================================================
    // TIP
    // =========================================================

    private Label tip(
            String text
    ) {

        Label label =
                new Label(
                        "•  " + text
                );

        label.setWrapText(
                true
        );

        label.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #334155;" +
                "-fx-padding: 3 0;"
        );

        return label;
    }

    // =========================================================
    // MAINTENANCE ROW
    // =========================================================

    private HBox maintenanceRow(
            String number,
            String title,
            String description
    ) {

        HBox row =
                new HBox(14);

        row.setAlignment(
                Pos.TOP_LEFT
        );

        row.setPadding(
                new Insets(10)
        );

        row.setStyle(
                "-fx-background-color: rgba(255,255,255,0.75);" +
                "-fx-background-radius: 12;"
        );

        Label numberLabel =
                new Label(
                        number
                );

        numberLabel.setMinWidth(
                30
        );

        numberLabel.setMinHeight(
                30
        );

        numberLabel.setAlignment(
                Pos.CENTER
        );

        numberLabel.setStyle(
                "-fx-background-color: #0f766e;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 15;"
        );

        VBox text =
                new VBox(3);

        Label titleLabel =
                new Label(
                        title
                );

        titleLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #134e4a;"
        );

        Label descriptionLabel =
                new Label(
                        description
                );

        descriptionLabel.setWrapText(
                true
        );

        descriptionLabel.setStyle(
                "-fx-text-fill: #475569;"
        );

        text.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );

        HBox.setHgrow(
                text,
                Priority.ALWAYS
        );

        row.getChildren().addAll(
                numberLabel,
                text
        );

        return row;
    }
}