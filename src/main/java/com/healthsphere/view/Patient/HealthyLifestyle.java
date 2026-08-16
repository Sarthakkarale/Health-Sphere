package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HealthyLifestyle {

    private final Stage stage;

    public HealthyLifestyle(Stage stage) {
        this.stage = stage;
    }

    // =========================================================
    // MAIN SCENE
    // =========================================================

    public Scene getScene() {

        VBox content = new VBox(20);

        content.setPadding(new Insets(18));

        // =====================================================
        // HERO WITH IMAGE ON RIGHT
        // =====================================================

        HBox hero = new HBox(25);

        hero.setPadding(new Insets(25));

        hero.setAlignment(Pos.CENTER_LEFT);

        hero.setPrefHeight(250);

        hero.setStyle(
                "-fx-background-color: linear-gradient(to right, #ecfdf5, #eff6ff);" +
                "-fx-background-radius: 20;" +
                "-fx-border-color: #a7f3d0;" +
                "-fx-border-radius: 20;" +
                "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.10), 15, 0, 0, 4);"
        );

        // -----------------------------------------------------
        // LEFT SIDE
        // -----------------------------------------------------

        VBox heroText = new VBox(10);

        HBox.setHgrow(
                heroText,
                Priority.ALWAYS
        );

        Label heroIcon = new Label("🌱");

        heroIcon.setStyle(
                "-fx-font-size: 40px;"
        );

        Label heroTitle = new Label(
                "Healthy Lifestyle"
        );

        heroTitle.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #14532d;"
        );

        Label heroDescription = new Label(
                "Simple daily habits can make a big difference to your long-term health."
        );

        heroDescription.setWrapText(true);

        heroDescription.setMaxWidth(550);

        heroDescription.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #475569;"
        );

        Label heroHint = new Label(
                "Build healthier habits • Stay active • Take care of yourself"
        );

        heroHint.setWrapText(true);

        heroHint.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #166534;"
        );

        heroText.getChildren().addAll(
                heroIcon,
                heroTitle,
                heroDescription,
                heroHint
        );

        // -----------------------------------------------------
        // RIGHT SIDE IMAGE
        // -----------------------------------------------------

        VBox heroImageContainer = createHeroImage(
                "/images/profile/profile5.jpg"
        );

        hero.getChildren().addAll(
                heroText,
                heroImageContainer
        );

        // =====================================================
        // DAILY BASICS
        // =====================================================

        VBox dailyBasics = card(
                "🌞  Daily Health Basics",
                "#dcfce7",
                "#166534"
        );

        dailyBasics.getChildren().addAll(

                healthTip(
                        "💧",
                        "Stay Hydrated",
                        "Drink water regularly throughout the day. Increase your fluid intake during hot weather or physical activity."
                ),

                healthTip(
                        "🥗",
                        "Eat Balanced Meals",
                        "Include vegetables, fruits, whole grains, protein sources and healthy fats. Try to keep highly processed foods occasional."
                ),

                healthTip(
                        "🏃",
                        "Keep Moving",
                        "Include regular physical activity in your week and break up long periods of sitting with short movement breaks."
                ),

                healthTip(
                        "😴",
                        "Prioritize Sleep",
                        "Maintain a consistent sleep and wake time and create a relaxing bedtime routine."
                )
        );

        // =====================================================
        // NUTRITION
        // =====================================================

        VBox nutrition = card(
                "🥗  Nutrition & Eating Habits",
                "#fef3c7",
                "#92400e"
        );

        nutrition.getChildren().addAll(

                bullet(
                        "Fill a good portion of your meals with vegetables and fruits."
                ),

                bullet(
                        "Choose whole grains and high-fiber foods regularly."
                ),

                bullet(
                        "Include protein from varied sources."
                ),

                bullet(
                        "Limit excessive sugar, salt and highly processed foods."
                ),

                bullet(
                        "Eat slowly and pay attention to hunger and fullness."
                )
        );

        // =====================================================
        // PHYSICAL ACTIVITY
        // =====================================================

        VBox activity = card(
                "🏃  Physical Activity",
                "#dbeafe",
                "#1e40af"
        );

        activity.getChildren().addAll(

                activityRow(
                        "🚶",
                        "Walking",
                        "Take regular walks and use movement breaks during the day."
                ),

                activityRow(
                        "💪",
                        "Strength",
                        "Include appropriate strength exercises during the week."
                ),

                activityRow(
                        "🧘",
                        "Flexibility",
                        "Gentle stretching can help maintain mobility and flexibility."
                ),

                activityRow(
                        "⚖️",
                        "Balance",
                        "Choose activities that match your current fitness level and gradually increase intensity."
                )
        );

        // =====================================================
        // SLEEP & STRESS
        // =====================================================

        HBox wellbeingRow = new HBox(18);

        VBox sleep = card(
                "😴  Better Sleep",
                "#ede9fe",
                "#6d28d9"
        );

        sleep.getChildren().addAll(

                bullet(
                        "Keep a regular sleep schedule."
                ),

                bullet(
                        "Reduce bright screens close to bedtime."
                ),

                bullet(
                        "Keep your sleeping environment comfortable and quiet."
                ),

                bullet(
                        "Avoid heavy meals immediately before sleeping."
                )
        );

        VBox stress = card(
                "🧘  Stress Management",
                "#fce7f3",
                "#9d174d"
        );

        stress.getChildren().addAll(

                bullet(
                        "Take short breaks during demanding tasks."
                ),

                bullet(
                        "Try slow breathing or mindfulness."
                ),

                bullet(
                        "Spend time with supportive people."
                ),

                bullet(
                        "Make time for hobbies and activities you enjoy."
                )
        );

        HBox.setHgrow(
                sleep,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                stress,
                Priority.ALWAYS
        );

        wellbeingRow.getChildren().addAll(
                sleep,
                stress
        );

        // =====================================================
        // HEALTHY ROUTINE
        // =====================================================

        VBox routine = card(
                "✅  Your Daily Healthy Routine",
                "#ccfbf1",
                "#115e59"
        );

        routine.getChildren().addAll(

                checklist(
                        "Start the day with water and a balanced breakfast."
                ),

                checklist(
                        "Take movement breaks throughout the day."
                ),

                checklist(
                        "Eat a variety of nutritious foods."
                ),

                checklist(
                        "Spend some time outdoors or in daylight."
                ),

                checklist(
                        "Take time to relax and manage stress."
                ),

                checklist(
                        "Maintain a consistent bedtime routine."
                )
        );

        // =====================================================
        // IMPORTANT NOTE
        // =====================================================

        VBox note = new VBox(8);

        note.setPadding(new Insets(18));

        note.setStyle(
                "-fx-background-color: #fff7ed;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #fed7aa;" +
                "-fx-border-radius: 14;"
        );

        Label noteTitle = new Label(
                "ℹ️ Important"
        );

        noteTitle.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #9a3412;"
        );

        Label noteText = new Label(
                "These are general healthy-lifestyle suggestions. Your ideal routine may depend on your age, fitness level, medical history and other individual factors."
        );

        noteText.setWrapText(true);

        noteText.setStyle(
                "-fx-text-fill: #7c2d12;"
        );

        note.getChildren().addAll(
                noteTitle,
                noteText
        );

        // =====================================================
        // BACK BUTTON
        // =====================================================

        Button back = new Button(
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
                        new ProfileSettings(stage).getScene()
                )
        );

        // =====================================================
        // ADD CONTENT
        // =====================================================

        content.getChildren().addAll(

                hero,

                dailyBasics,

                nutrition,

                activity,

                wellbeingRow,

                routine,

                note,

                back
        );

        // =====================================================
        // ONLY ONE SCROLL PANE
        // =====================================================

        ScrollPane scroll = new ScrollPane(
                content
        );

        scroll.setFitToWidth(true);

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scroll.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scroll.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;"
        );

        // =====================================================
        // RETURN SCENE
        // =====================================================

        return PatientUI.createScene(
                stage,
                "Healthy Lifestyle",
                "Healthy Lifestyle",
                "Practical habits for maintaining a healthier daily routine.",
                scroll
        );
    }

    // =========================================================
    // HERO IMAGE
    // =========================================================

    private VBox createHeroImage(
            String imagePath
    ) {

        VBox container = new VBox();

        container.setAlignment(
                Pos.CENTER
        );

        container.setPrefSize(
                230,
                200
        );

        container.setMinSize(
                230,
                200
        );

        container.setMaxSize(
                230,
                200
        );

        container.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: #a7f3d0;" +
                "-fx-border-radius: 16;" +
                "-fx-padding: 6;"
        );

        var resource =
                getClass().getResource(
                        imagePath
                );

        if (resource != null) {

            Image image =
                    new Image(
                            resource.toExternalForm()
                    );

            ImageView imageView =
                    new ImageView(image);

            imageView.setFitWidth(218);

            imageView.setFitHeight(188);

            imageView.setPreserveRatio(false);

            container.getChildren().add(
                    imageView
            );

        } else {

            Label unavailable =
                    new Label(
                            "Image unavailable"
                    );

            unavailable.setStyle(
                    "-fx-text-fill: #64748b;" +
                    "-fx-font-size: 13px;"
            );

            container.getChildren().add(
                    unavailable
            );

            System.err.println(
                    "Profile image not found: " + imagePath
            );
        }

        return container;
    }

    // =========================================================
    // CARD
    // =========================================================

    private VBox card(
            String title,
            String background,
            String textColor
    ) {

        VBox box = new VBox(13);

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
                new Label(title);

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
    // HEALTH TIP
    // =========================================================

    private HBox healthTip(
            String icon,
            String title,
            String description
    ) {

        HBox row = new HBox(14);

        row.setPadding(
                new Insets(12)
        );

        row.setAlignment(
                Pos.TOP_LEFT
        );

        row.setStyle(
                "-fx-background-color: rgba(255,255,255,0.75);" +
                "-fx-background-radius: 12;"
        );

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-font-size: 24px;"
        );

        VBox text =
                new VBox(4);

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #0f172a;"
        );

        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setWrapText(true);

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
                iconLabel,
                text
        );

        return row;
    }

    // =========================================================
    // ACTIVITY ROW
    // =========================================================

    private HBox activityRow(
            String icon,
            String title,
            String description
    ) {

        HBox row = new HBox(12);

        row.setAlignment(
                Pos.TOP_LEFT
        );

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-font-size: 22px;"
        );

        VBox text =
                new VBox(3);

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setWrapText(true);

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
                iconLabel,
                text
        );

        return row;
    }

    // =========================================================
    // BULLET
    // =========================================================

    private Label bullet(
            String text
    ) {

        Label label =
                new Label("•  " + text);

        label.setWrapText(true);

        label.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #334155;"
        );

        return label;
    }

    // =========================================================
    // CHECKLIST
    // =========================================================

    private Label checklist(
            String text
    ) {

        Label label =
                new Label("☐  " + text);

        label.setWrapText(true);

        label.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #134e4a;" +
                "-fx-padding: 4 0;"
        );

        return label;
    }
}