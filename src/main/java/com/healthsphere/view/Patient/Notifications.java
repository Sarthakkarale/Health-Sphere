package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Notifications {

    private final Stage stage;

    public Notifications(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {

        BorderPane root = new BorderPane();

        root.setLeft(createSidebar());
        root.setTop(createHeader());

        VBox content = new VBox(20);
        content.setPadding(new Insets(28));
        content.setStyle("-fx-background-color: #f8fafc;");

        Label title = new Label("Notifications");
        title.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label subtitle = new Label(
                "Stay updated with your appointments, health records and important alerts."
        );

        subtitle.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #64748b;"
        );

        VBox heading = new VBox(5, title, subtitle);

        VBox todayCard = coloredCard(
                "#eff6ff",
                "#2563eb",
                "Today's Updates"
        );

        todayCard.getChildren().addAll(

                notification(
                        "Appointment Reminder",
                        "You have an appointment with Dr. Sarah Jenkins tomorrow at 10:00 AM.",
                        "Today • 6:30 PM",
                        "#dbeafe",
                        "#2563eb"
                ),

                notification(
                        "Health Passport Updated",
                        "Your latest health information has been added to your Health Passport.",
                        "Today • 4:15 PM",
                        "#dcfce7",
                        "#16a34a"
                ),

                notification(
                        "AI Health Insight",
                        "Your AI Health Assistant has generated a new health insight.",
                        "Today • 2:40 PM",
                        "#f3e8ff",
                        "#9333ea"
                )
        );

        VBox earlierCard = coloredCard(
                "#f8fafc",
                "#475569",
                "Earlier"
        );

        earlierCard.getChildren().addAll(

                notification(
                        "Medical Record Added",
                        "A new medical document has been added to your medical records.",
                        "Yesterday",
                        "#fef3c7",
                        "#d97706"
                ),

                notification(
                        "Hospital Search",
                        "Your recent hospital search results are available.",
                        "2 days ago",
                        "#e0f2fe",
                        "#0284c7"
                ),

                notification(
                        "Profile Reminder",
                        "Please review your emergency contact information.",
                        "3 days ago",
                        "#fee2e2",
                        "#dc2626"
                )
        );

        VBox emergencyCard = coloredCard(
                "#fef2f2",
                "#dc2626",
                "Emergency Information"
        );

        Label emergencyText = new Label(
                "If you are experiencing a medical emergency, use Emergency Assistance to quickly access emergency services and important contacts."
        );

        emergencyText.setWrapText(true);

        emergencyText.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #7f1d1d;"
        );

        Button emergencyButton = new Button("Open Emergency Assistance");

        emergencyButton.setStyle(
                "-fx-background-color: #dc2626;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10 18 10 18;"
        );

        emergencyButton.setOnAction(
                e -> showEmergencyAssistance()
        );

        emergencyCard.getChildren().addAll(
                emergencyText,
                emergencyButton
        );

        content.getChildren().addAll(
                heading,
                todayCard,
                earlierCard,
                emergencyCard
        );

        ScrollPane scroll = new ScrollPane(content);

        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        root.setCenter(scroll);

        return new Scene(root, 1440, 900);
    }

    // =========================================================
    // NOTIFICATION CARD
    // =========================================================

    private HBox notification(
            String title,
            String description,
            String time,
            String background,
            String accent
    ) {

        HBox box = new HBox(15);

        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(15));

        box.setStyle(
                "-fx-background-color: " + background + ";" +
                "-fx-background-radius: 10;"
        );

        VBox textBox = new VBox(5);

        Label titleLabel = new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + accent + ";"
        );

        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setWrapText(true);

        descriptionLabel.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #475569;"
        );

        Label timeLabel =
                new Label(time);

        timeLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #64748b;"
        );

        textBox.getChildren().addAll(
                titleLabel,
                descriptionLabel,
                timeLabel
        );

        Region spacer = new Region();

        HBox.setHgrow(
                textBox,
                Priority.ALWAYS
        );

        box.getChildren().addAll(
                textBox,
                spacer
        );

        return box;
    }

    // =========================================================
    // COLORED CARD
    // =========================================================

    private VBox coloredCard(
            String background,
            String accent,
            String title
    ) {

        VBox card = new VBox(14);

        card.setPadding(new Insets(20));

        card.setStyle(
                "-fx-background-color: " + background + ";" +
                "-fx-border-color: " + accent + ";" +
                "-fx-border-width: 0 0 0 5;" +
                "-fx-background-radius: 12;" +
                "-fx-border-radius: 12;"
        );

        Label label = new Label(title);

        label.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + accent + ";"
        );

        card.getChildren().add(label);

        return card;
    }

    // =========================================================
    // HEADER
    // =========================================================

    private HBox createHeader() {

        HBox header = new HBox();

        header.setAlignment(Pos.CENTER_RIGHT);
        header.setPadding(
                new Insets(15, 28, 15, 28)
        );

        header.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #e2e8f0;"
        );

        Region spacer = new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label notificationIcon =
                new Label("🔔");

        notificationIcon.setStyle(
                "-fx-font-size: 22px;"
        );

        Button dashboard =
                new Button("Dashboard");

        dashboard.setOnAction(
                e -> showDashboard()
        );

        Button profile =
                new Button("Sarah");

        profile.setOnAction(
                e -> showProfileSettings()
        );

        header.getChildren().addAll(
                spacer,
                notificationIcon,
                dashboard,
                profile
        );

        return header;
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private VBox createSidebar() {

        VBox sidebar = new VBox(8);

        sidebar.setPrefWidth(255);
        sidebar.setPadding(new Insets(22));

        sidebar.setStyle(
                "-fx-background-color: #0f172a;"
        );

        Label brand =
                new Label("✚  MediNexus AI");

        brand.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 21px;" +
                "-fx-font-weight: bold;"
        );

        Label module =
                new Label("Patient Module");

        module.setStyle(
                "-fx-text-fill: #94a3b8;"
        );

        Region spacer = new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        sidebar.getChildren().addAll(
                brand,
                module,

                nav(
                        "▦",
                        "Dashboard",
                        false,
                        this::showDashboard
                ),

                nav(
                        "⊞",
                        "Search Hospitals",
                        false,
                        this::showSearchHospitals
                ),

                nav(
                        "▣",
                        "Appointments",
                        false,
                        this::showAppointments
                ),

                nav(
                        "▧",
                        "Health Passport",
                        false,
                        this::showHealthPassport
                ),

                nav(
                        "▱",
                        "Medical Records",
                        false,
                        this::showMedicalRecords
                ),

                nav(
                        "♙",
                        "AI Health Assistant",
                        false,
                        this::showAIHealthAssistant
                ),

                nav(
                        "⌖",
                        "Emergency Assistance",
                        false,
                        this::showEmergencyAssistance
                ),

                spacer,

                nav(
                        "♧",
                        "Notifications",
                        true,
                        this::showNotifications
                ),

                nav(
                        "⚙",
                        "Profile & Settings",
                        false,
                        this::showProfileSettings
                )
        );

        return sidebar;
    }

    private HBox nav(
            String icon,
            String text,
            boolean selected,
            Runnable action
    ) {

        HBox item = new HBox(12);

        item.setAlignment(
                Pos.CENTER_LEFT
        );

        item.setPadding(
                new Insets(12)
        );

        item.setStyle(
                "-fx-background-color: " +
                (selected ? "#2563eb" : "transparent") +
                ";" +
                "-fx-background-radius: 8;"
        );

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 17px;"
        );

        Label textLabel =
                new Label(text);

        textLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;"
        );

        item.getChildren().addAll(
                iconLabel,
                textLabel
        );

        item.setOnMouseClicked(
                e -> action.run()
        );

        return item;
    }

    // =========================================================
    // NAVIGATION
    // =========================================================

    private void showDashboard() {
        stage.setScene(
                new Dashboard(stage).getScene()
        );
        stage.show();
    }

    private void showSearchHospitals() {
        stage.setScene(
                new SearchHospitals(stage).getScene()
        );
        stage.show();
    }

    private void showAppointments() {
        stage.setScene(
                new Appointments(stage).getScene()
        );
        stage.show();
    }

    private void showHealthPassport() {
        stage.setScene(
                new HealthPassport(stage).getScene()
        );
        stage.show();
    }

    private void showMedicalRecords() {
        stage.setScene(
                new MedicalRecords(stage).getScene()
        );
        stage.show();
    }

    private void showAIHealthAssistant() {
        stage.setScene(
                new AiHealthAssistant(stage).getScene()
        );
        stage.show();
    }

    private void showEmergencyAssistance() {
        stage.setScene(
                new EmergencyAssistance(stage).getScene()
        );
        stage.show();
    }

    private void showNotifications() {
        stage.setScene(
                new Notifications(stage).getScene()
        );
        stage.show();
    }

    private void showProfileSettings() {
        stage.setScene(
                new ProfileSettings(stage).getScene()
        );
        stage.show();
    }
}