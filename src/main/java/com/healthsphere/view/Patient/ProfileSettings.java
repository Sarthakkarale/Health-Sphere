
package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProfileSettings {

    private final Stage stage;

    public ProfileSettings(Stage stage) {
        this.stage = stage;
    }

    // =========================================================
    // MAIN SCENE
    // =========================================================

    public Scene getScene() {

        // =====================================================
        // MAIN CONTENT
        // =====================================================

        VBox content =
                new VBox(22);

        content.setPadding(
                new Insets(5)
        );

        // =====================================================
        // PROFILE IMAGE
        // =====================================================

        VBox profileBanner =
                new VBox();

        profileBanner.setAlignment(
                Pos.CENTER
        );

        profileBanner.setPadding(
                new Insets(20)
        );

        profileBanner.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: #ddd6fe;" +
                "-fx-border-radius: 18;"
        );

        ImageView banner =
                createSquareImage(
                        "/images/profile/profile1.jpg",
                        300
                );

        profileBanner.getChildren().add(
                banner
        );

        // =====================================================
        // PROFILE CARD
        // =====================================================

        HBox profileCard =
                new HBox(20);

        profileCard.setPadding(
                new Insets(20)
        );

        profileCard.setAlignment(
                Pos.CENTER_LEFT
        );

        profileCard.setStyle(
                "-fx-background-color: #f5f3ff;" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: #ddd6fe;" +
                "-fx-border-radius: 16;"
        );

        ImageView profileImage =
                createImage(
                        "/images/profile/profile2.jpg",
                        120,
                        120
                );

        VBox profileInformation =
                new VBox(7);

        Label name =
                new Label(
                        "Sarah Johnson"
                );

        name.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #4c1d95;"
        );

        Label patientId =
                new Label(
                        "Patient ID: HS-2026-001"
                );

        patientId.setStyle(
                "-fx-text-fill: #64748b;"
        );

        Label status =
                new Label(
                        "● Active Patient"
                );

        status.setStyle(
                "-fx-text-fill: #16a34a;" +
                "-fx-font-weight: bold;"
        );

        profileInformation.getChildren().addAll(
                name,
                patientId,
                status
        );

        profileCard.getChildren().addAll(
                profileImage,
                profileInformation
        );

        // =====================================================
        // PERSONAL INFORMATION
        // =====================================================

        VBox personal =
                card(
                        "👤  Personal Information"
                );

        GridPane personalGrid =
                new GridPane();

        personalGrid.setHgap(
                15
        );

        personalGrid.setVgap(
                12
        );

        TextField nameField =
                field(
                        "Sarah Johnson"
                );

        TextField emailField =
                field(
                        "sarah.johnson@email.com"
                );

        TextField phoneField =
                field(
                        "+91 98765 43210"
                );

        TextField cityField =
                field(
                        "Hyderabad"
                );

        personalGrid.add(
                label("Full Name"),
                0,
                0
        );

        personalGrid.add(
                nameField,
                1,
                0
        );

        personalGrid.add(
                label("Email"),
                0,
                1
        );

        personalGrid.add(
                emailField,
                1,
                1
        );

        personalGrid.add(
                label("Phone"),
                0,
                2
        );

        personalGrid.add(
                phoneField,
                1,
                2
        );

        personalGrid.add(
                label("City"),
                0,
                3
        );

        personalGrid.add(
                cityField,
                1,
                3
        );

        personal.getChildren().add(
                personalGrid
        );

        // =====================================================
        // HEALTH & WELLNESS
        // =====================================================

        VBox imageCards =
                card(
                        "💜  Health & Wellness"
                );

        HBox images =
                new HBox(18);

        images.getChildren().addAll(

                imageCard(
                        "/images/profile/profile3.jpg",
                        "Healthy Lifestyle"
                ),

                imageCard(
                        "/images/profile/profile4.jpg",
                        "Personal Health"
                )
        );

        imageCards.getChildren().add(
                images
        );

        // =====================================================
        // ACCOUNT PREFERENCES
        // =====================================================

        VBox preferences =
                card(
                        "⚙  Account Preferences"
                );

        preferences.getChildren().addAll(

                setting(
                        "Notifications",
                        "Receive appointment and health reminders."
                ),

                setting(
                        "Health Insights",
                        "Allow AI-generated health insights."
                ),

                setting(
                        "Privacy",
                        "Manage your healthcare information privacy."
                )
        );

        // =====================================================
        // BUTTONS
        // =====================================================

        Button save =
                new Button(
                        "Save Changes"
                );

        save.setStyle(
                "-fx-background-color: #7c3aed;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 11 22;" +
                "-fx-cursor: hand;"
        );

        save.setOnAction(
                e -> {

                    System.out.println(
                            "Profile changes saved."
                    );
                }
        );

        Button back =
                new Button(
                        "← Back to Dashboard"
                );

        back.setStyle(
                "-fx-background-color: #0f172a;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10 18;" +
                "-fx-cursor: hand;"
        );

        back.setOnAction(
                e -> showDashboard()
        );

        HBox buttons =
                new HBox(12);

        buttons.setAlignment(
                Pos.CENTER_LEFT
        );

        buttons.getChildren().addAll(
                save,
                back
        );

        // =====================================================
        // ADD EVERYTHING TO CONTENT
        // =====================================================

        content.getChildren().addAll(

                profileBanner,

                profileCard,

                personal,

                imageCards,

                preferences,

                buttons
        );

        // =====================================================
        // SCROLL
        // =====================================================

        ScrollPane scroll =
                new ScrollPane(
                        content
                );

        scroll.setFitToWidth(
                true
        );

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
        // WRAPPER
        //
        // PatientUI provides:
        // LEFT SIDEBAR
        // TOP HEADER
        // CENTER CONTENT
        // =====================================================

        VBox wrapper =
                new VBox(
                        scroll
                );

        VBox.setVgrow(
                scroll,
                Priority.ALWAYS
        );

        // =====================================================
        // RETURN SHARED PATIENT SCENE
        // =====================================================

        return PatientUI.createScene(
                stage,
                "Profile & Settings",
                "Profile & Settings",
                "Manage your personal information, preferences and account settings.",
                wrapper
        );
    }

    // =========================================================
    // REUSABLE CARD
    // =========================================================

    private VBox card(
            String title
    ) {

        VBox box =
                new VBox(15);

        box.setPadding(
                new Insets(20)
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: #ddd6fe;" +
                "-fx-border-radius: 16;"
        );

        Label heading =
                new Label(title);

        heading.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #4c1d95;"
        );

        box.getChildren().add(
                heading
        );

        return box;
    }

    // =========================================================
    // FORM LABEL
    // =========================================================

    private Label label(
            String text
    ) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #475569;"
        );

        return label;
    }

    // =========================================================
    // TEXT FIELD
    // =========================================================

    private TextField field(
            String text
    ) {

        TextField field =
                new TextField(text);

        field.setPrefWidth(
                350
        );

        field.setStyle(
                "-fx-background-color: #f8fafc;" +
                "-fx-border-color: #c4b5fd;" +
                "-fx-border-radius: 7;" +
                "-fx-background-radius: 7;" +
                "-fx-padding: 9;"
        );

        return field;
    }

    // =========================================================
    // HEALTH & WELLNESS IMAGE CARD
    // =========================================================

    private VBox imageCard(
            String path,
            String title
    ) {

        VBox box =
                new VBox();

        box.setStyle(
                "-fx-background-color: #faf5ff;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #ddd6fe;" +
                "-fx-border-radius: 14;"
        );

        ImageView image =
                createImage(
                        path,
                        360,
                        150
                );

        Label label =
                new Label(title);

        label.setPadding(
                new Insets(12)
        );

        label.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #5b21b6;"
        );

        box.getChildren().addAll(
                image,
                label
        );

        return box;
    }

    // =========================================================
    // ACCOUNT SETTING
    // =========================================================

    private HBox setting(
            String title,
            String description
    ) {

        HBox row =
                new HBox(15);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(10)
        );

        row.setStyle(
                "-fx-background-color: #faf5ff;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #ede9fe;" +
                "-fx-border-radius: 10;"
        );

        VBox text =
                new VBox(4);

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        descriptionLabel.setWrapText(
                true
        );

        text.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );

        HBox.setHgrow(
                text,
                Priority.ALWAYS
        );

        Button configure =
                new Button(
                        "Manage"
                );

        configure.setStyle(
                "-fx-background-color: #ede9fe;" +
                "-fx-text-fill: #6d28d9;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 7;" +
                "-fx-padding: 8 14;" +
                "-fx-cursor: hand;"
        );

        row.getChildren().addAll(
                text,
                configure
        );

        return row;
    }

    // =========================================================
    // NORMAL IMAGE LOADER
    // =========================================================

    private ImageView createImage(
            String path,
            double width,
            double height
    ) {

        ImageView view =
                new ImageView();

        var resource =
                getClass().getResource(path);

        if (resource == null) {

            System.err.println(
                    "Profile image not found: "
                            + path
            );

            view.setFitWidth(
                    width
            );

            view.setFitHeight(
                    height
            );

            return view;
        }

        Image image =
                new Image(
                        resource.toExternalForm()
                );

        view.setImage(
                image
        );

        view.setFitWidth(
                width
        );

        view.setFitHeight(
                height
        );

        view.setPreserveRatio(
                false
        );

        return view;
    }

    // =========================================================
    // SQUARE IMAGE LOADER
    //
    // Used specifically for profile1.jpg.
    //
    // It crops the image to a square instead of stretching
    // the complete image into a wide banner.
    // =========================================================

    private ImageView createSquareImage(
            String path,
            double size
    ) {

        ImageView view =
                new ImageView();

        var resource =
                getClass().getResource(path);

        if (resource == null) {

            System.err.println(
                    "Profile image not found: "
                            + path
            );

            view.setFitWidth(
                    size
            );

            view.setFitHeight(
                    size
            );

            return view;
        }

        Image image =
                new Image(
                        resource.toExternalForm()
                );

        view.setImage(
                image
        );

        // -----------------------------------------------------
        // GET ORIGINAL IMAGE SIZE
        // -----------------------------------------------------

        double imageWidth =
                image.getWidth();

        double imageHeight =
                image.getHeight();

        // -----------------------------------------------------
        // SELECT THE LARGEST POSSIBLE SQUARE
        // -----------------------------------------------------

        double cropSize =
                Math.min(
                        imageWidth,
                        imageHeight
                );

        // -----------------------------------------------------
        // CENTER THE SQUARE CROP
        // -----------------------------------------------------

        double x =
                (imageWidth - cropSize) / 2;

        double y =
                (imageHeight - cropSize) / 2;

        view.setViewport(
                new Rectangle2D(
                        x,
                        y,
                        cropSize,
                        cropSize
                )
        );

        // -----------------------------------------------------
        // DISPLAY AS 300 × 300
        // -----------------------------------------------------

        view.setFitWidth(
                size
        );

        view.setFitHeight(
                size
        );

        view.setPreserveRatio(
                false
        );

        return view;
    }

    // =========================================================
    // DASHBOARD
    // =========================================================

    private void showDashboard() {

        stage.setScene(
                new Dashboard(stage)
                        .getScene()
        );

        stage.show();
    }
}
