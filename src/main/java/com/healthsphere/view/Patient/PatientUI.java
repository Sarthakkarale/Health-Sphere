package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class PatientUI {

    private PatientUI() {
    }

    public static Scene createScene(
            PatientNavigator navigator,
            String activePage,
            String title,
            String subtitle,
            Node content
    ) {

        BorderPane root = new BorderPane();

        root.setLeft(createSidebar(navigator, activePage));
        root.setTop(createHeader(navigator));

        VBox page = new VBox(22);
        page.setPadding(new Insets(28));
        page.setStyle("-fx-background-color: #f8fafc;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1e293b;"
        );

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #64748b;"
        );

        VBox heading = new VBox(5);
        heading.getChildren().addAll(
                titleLabel,
                subtitleLabel
        );

        page.getChildren().addAll(
                heading,
                content
        );

        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color: #f8fafc;");

        root.setCenter(scroll);

        return new Scene(root, 1440, 900);
    }

    private static VBox createSidebar(
            PatientNavigator navigator,
            String activePage
    ) {

        VBox sidebar = new VBox(8);

        sidebar.setPrefWidth(255);
        sidebar.setPadding(new Insets(22));

        sidebar.setStyle(
                "-fx-background-color: #0f172a;"
        );

        Label brand = new Label("✚  MediNexus AI");

        brand.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 21px;" +
                "-fx-font-weight: bold;"
        );

        Label module = new Label("Patient Module");

        module.setStyle(
                "-fx-text-fill: #94a3b8;" +
                "-fx-font-size: 13px;"
        );

        Separator separator = new Separator();

        separator.setStyle(
                "-fx-background-color: #334155;"
        );

        Region spacer = new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        sidebar.getChildren().addAll(
                brand,
                module,
                separator,

                nav(
                        "▦",
                        "Dashboard",
                        activePage.equals("Dashboard"),
                        navigator::showDashboard
                ),

                nav(
                        "⊞",
                        "Search Hospitals",
                        activePage.equals("Search Hospitals"),
                        navigator::showSearchHospitals
                ),

                nav(
                        "▣",
                        "Appointments",
                        activePage.equals("Appointments"),
                        navigator::showAppointments
                ),

                nav(
                        "▧",
                        "Health Passport",
                        activePage.equals("Health Passport"),
                        navigator::showHealthPassport
                ),

                nav(
                        "▱",
                        "Medical Records",
                        activePage.equals("Medical Records"),
                        navigator::showMedicalRecords
                ),

                nav(
                        "♙",
                        "AI Health Assistant",
                        activePage.equals("AI Health Assistant"),
                        navigator::showAIHealthAssistant
                ),

                nav(
                        "⌖",
                        "Emergency Assistance",
                        activePage.equals("Emergency Assistance"),
                        navigator::showEmergencyAssistance
                ),

                spacer,

                nav(
                        "♧",
                        "Notifications",
                        activePage.equals("Notifications"),
                        navigator::showNotifications
                ),

                nav(
                        "⚙",
                        "Profile & Settings",
                        activePage.equals("Profile & Settings"),
                        navigator::showProfileSettings
                )
        );

        return sidebar;
    }

    private static HBox nav(
            String icon,
            String text,
            boolean selected,
            Runnable action
    ) {

        HBox item = new HBox(12);

        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(12));
        item.setMaxWidth(Double.MAX_VALUE);

        String background = selected
                ? "#2563eb"
                : "transparent";

        item.setStyle(
                "-fx-background-color: " + background + ";" +
                "-fx-background-radius: 8;"
        );

        Label iconLabel = new Label(icon);

        iconLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 17px;"
        );

        Label textLabel = new Label(text);

        textLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;"
        );

        item.getChildren().addAll(
                iconLabel,
                textLabel
        );

        item.setOnMouseClicked(
                event -> action.run()
        );

        return item;
    }

    private static HBox createHeader(
            PatientNavigator navigator
    ) {

        HBox header = new HBox();

        header.setAlignment(
                Pos.CENTER_RIGHT
        );

        header.setPadding(
                new Insets(16, 28, 16, 28)
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

        Label bell = new Label("♧");

        bell.setStyle(
                "-fx-font-size: 24px;"
        );

        Button notifications =
                new Button("Notifications");

        notifications.setOnAction(
                e -> navigator.showNotifications()
        );

        Button profile =
                new Button("Sarah");

        profile.setOnAction(
                e -> navigator.showProfileSettings()
        );

        header.getChildren().addAll(
                spacer,
                bell,
                notifications,
                profile
        );

        return header;
    }

    public static VBox card(String title) {

        VBox box = new VBox(12);

        box.setPadding(
                new Insets(20)
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #e2e8f0;" +
                "-fx-border-radius: 12;"
        );

        if (title != null && !title.isEmpty()) {

            Label label = new Label(title);

            label.setStyle(
                    "-fx-font-size: 18px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: #1e293b;"
            );

            box.getChildren().add(label);
        }

        return box;
    }

    public static Label sectionTitle(String text) {

        Label label = new Label(text);

        label.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1e293b;"
        );

        return label;
    }

    public static Button button(
            String text,
            Runnable action
    ) {

        Button button = new Button(text);

        button.setPrefHeight(40);

        button.setOnAction(
                e -> action.run()
        );

        return button;
    }

    public static Label muted(String text) {

        Label label = new Label(text);

        label.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 14px;"
        );

        return label;
    }

    public static Label green(String text) {

        Label label = new Label(text);

        label.setStyle(
                "-fx-text-fill: #16a34a;" +
                "-fx-font-size: 14px;"
        );

        return label;
    }
}