package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.stage.Stage;

public final class PatientUI {

    private PatientUI() {
    }

    // =========================================================
    // COMMON SCENE
    // =========================================================

    public static Scene createScene(
            Stage stage,
            String activePage,
            String title,
            String subtitle,
            VBox content
    ) {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color: #f1f5f9;"
        );

        root.setLeft(
                createSidebar(stage, activePage)
        );

        root.setTop(
                createHeader(stage)
        );

        content.setPadding(
                new Insets(28)
        );

        ScrollPane scrollPane =
                new ScrollPane(content);

        scrollPane.setFitToWidth(true);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;"
        );

        root.setCenter(scrollPane);

        VBox pageHeading =
                new VBox(5);

        Label pageTitle =
                new Label(title);

        pageTitle.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label pageSubtitle =
                new Label(subtitle);

        pageSubtitle.setWrapText(true);

        pageSubtitle.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #64748b;"
        );

        pageHeading.getChildren().addAll(
                pageTitle,
                pageSubtitle
        );

        content.getChildren().add(
                0,
                pageHeading
        );

        return new Scene(
                root,
                1440,
                900
        );
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private static VBox createSidebar(
            Stage stage,
            String activePage
    ) {

        VBox sidebar =
                new VBox(7);

        sidebar.setPrefWidth(255);

        sidebar.setPadding(
                new Insets(22)
        );

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
                "-fx-text-fill: #94a3b8;" +
                "-fx-font-size: 13px;"
        );

        Separator separator =
                new Separator();

        separator.setStyle(
                "-fx-background-color: #334155;"
        );

        sidebar.getChildren().addAll(
                brand,
                module,
                separator
        );

        sidebar.getChildren().add(
                navItem(
                        stage,
                        "▦",
                        "Dashboard",
                        activePage,
                        () -> stage.setScene(
                                new Dashboard(stage).getScene()
                        )
                )
        );

        sidebar.getChildren().add(
                navItem(
                        stage,
                        "⊞",
                        "Search Hospitals",
                        activePage,
                        () -> stage.setScene(
                                new SearchHospitals(stage).getScene()
                        )
                )
        );

        sidebar.getChildren().add(
                navItem(
                        stage,
                        "▣",
                        "Appointments",
                        activePage,
                        () -> stage.setScene(
                                new Appointments(stage).getScene()
                        )
                )
        );

        sidebar.getChildren().add(
                navItem(
                        stage,
                        "▧",
                        "Health Passport",
                        activePage,
                        () -> stage.setScene(
                                new HealthPassport(stage).getScene()
                        )
                )
        );

        sidebar.getChildren().add(
                navItem(
                        stage,
                        "▱",
                        "Medical Records",
                        activePage,
                        () -> stage.setScene(
                                new MedicalRecords(stage).getScene()
                        )
                )
        );

        sidebar.getChildren().add(
                navItem(
                        stage,
                        "♙",
                        "AI Health Assistant",
                        activePage,
                        () -> stage.setScene(
                                new AiHealthAssistant(stage).getScene()
                        )
                )
        );

        sidebar.getChildren().add(
                navItem(
                        stage,
                        "⌖",
                        "Emergency Assistance",
                        activePage,
                        () -> stage.setScene(
                                new EmergencyAssistance(stage).getScene()
                        )
                )
        );

        Region spacer =
                new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        sidebar.getChildren().add(spacer);

        sidebar.getChildren().add(
                navItem(
                        stage,
                        "♧",
                        "Notifications",
                        activePage,
                        () -> stage.setScene(
                                new Notifications(stage).getScene()
                        )
                )
        );

        sidebar.getChildren().add(
                navItem(
                        stage,
                        "⚙",
                        "Profile & Settings",
                        activePage,
                        () -> stage.setScene(
                                new ProfileSettings(stage).getScene()
                        )
                )
        );

        return sidebar;
    }

    private static HBox navItem(
            Stage stage,
            String icon,
            String text,
            String activePage,
            Runnable action
    ) {

        HBox item =
                new HBox(12);

        item.setAlignment(
                Pos.CENTER_LEFT
        );

        item.setPadding(
                new Insets(12)
        );

        item.setMaxWidth(
                Double.MAX_VALUE
        );

        boolean selected =
                text.equals(activePage);

        String background =
                selected
                        ? "#2563eb"
                        : "transparent";

        item.setStyle(
                "-fx-background-color: " +
                background +
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
                event -> action.run()
        );

        return item;
    }

    // =========================================================
    // HEADER
    // =========================================================

    private static HBox createHeader(
            Stage stage
    ) {

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_RIGHT
        );

        header.setPadding(
                new Insets(
                        16,
                        28,
                        16,
                        28
                )
        );

        header.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #e2e8f0;"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label notificationIcon =
                new Label("♧");

        notificationIcon.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-text-fill: #334155;"
        );

        Button notifications =
                button(
                        "Notifications",
                        () -> stage.setScene(
                                new Notifications(stage).getScene()
                        )
                );

        Button profile =
                button(
                        "Sarah",
                        () -> stage.setScene(
                                new ProfileSettings(stage).getScene()
                        )
                );

        header.getChildren().addAll(
                spacer,
                notificationIcon,
                notifications,
                profile
        );

        return header;
    }

    // =========================================================
    // STANDARD CARD
    // =========================================================

    public static VBox card(
            String title
    ) {

        return card(
                title,
                "#ffffff",
                "#e2e8f0"
        );
    }

    public static VBox card(
            String title,
            String backgroundColor,
            String borderColor
    ) {

        VBox box =
                new VBox(12);

        box.setPadding(
                new Insets(20)
        );

        box.setStyle(
                "-fx-background-color: " +
                backgroundColor +
                ";" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: " +
                borderColor +
                ";" +
                "-fx-border-radius: 14;" +
                "-fx-border-width: 1;"
        );

        if (title != null &&
                !title.isEmpty()) {

            Label label =
                    new Label(title);

            label.setStyle(
                    "-fx-font-size: 18px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: #0f172a;"
            );

            box.getChildren().add(label);
        }

        return box;
    }

    // =========================================================
    // COLORED CARDS
    // =========================================================

    public static VBox blueCard(
            String title
    ) {

        return card(
                title,
                "#eff6ff",
                "#bfdbfe"
        );
    }

    public static VBox greenCard(
            String title
    ) {

        return card(
                title,
                "#f0fdf4",
                "#bbf7d0"
        );
    }

    public static VBox purpleCard(
            String title
    ) {

        return card(
                title,
                "#faf5ff",
                "#e9d5ff"
        );
    }

    public static VBox orangeCard(
            String title
    ) {

        return card(
                title,
                "#fff7ed",
                "#fed7aa"
        );
    }

    public static VBox redCard(
            String title
    ) {

        return card(
                title,
                "#fef2f2",
                "#fecaca"
        );
    }

    public static VBox tealCard(
            String title
    ) {

        return card(
                title,
                "#f0fdfa",
                "#99f6e4"
        );
    }

    // =========================================================
    // BUTTON
    // =========================================================

    public static Button button(
            String text,
            Runnable action
    ) {

        Button button =
                new Button(text);

        button.setPrefHeight(42);

        button.setPadding(
                new Insets(
                        8,
                        18,
                        8,
                        18
                )
        );

        button.setStyle(
                "-fx-background-color: #2563eb;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
        );

        button.setOnAction(
                event -> action.run()
        );

        return button;
    }

    public static Button greenButton(
            String text,
            Runnable action
    ) {

        Button button =
                button(text, action);

        button.setStyle(
                "-fx-background-color: #16a34a;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
        );

        return button;
    }

    public static Button orangeButton(
            String text,
            Runnable action
    ) {

        Button button =
                button(text, action);

        button.setStyle(
                "-fx-background-color: #ea580c;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
        );

        return button;
    }

    public static Button redButton(
            String text,
            Runnable action
    ) {

        Button button =
                button(text, action);

        button.setStyle(
                "-fx-background-color: #dc2626;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
        );

        return button;
    }

    // =========================================================
    // LABEL HELPERS
    // =========================================================

    public static Label muted(
            String text
    ) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 14px;"
        );

        return label;
    }

    public static Label green(
            String text
    ) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-text-fill: #15803d;" +
                "-fx-font-weight: bold;"
        );

        return label;
    }

    public static Label blue(
            String text
    ) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-text-fill: #1d4ed8;" +
                "-fx-font-weight: bold;"
        );

        return label;
    }

    public static Label orange(
            String text
    ) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-text-fill: #c2410c;" +
                "-fx-font-weight: bold;"
        );

        return label;
    }

    public static Label red(
            String text
    ) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-text-fill: #b91c1c;" +
                "-fx-font-weight: bold;"
        );

        return label;
    }

    // =========================================================
    // STAT CARD
    // =========================================================

    public static VBox statCard(
            String icon,
            String title,
            String value,
            String status,
            String backgroundColor,
            String borderColor
    ) {

        VBox box =
                card(
                        "",
                        backgroundColor,
                        borderColor
                );

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-font-size: 25px;"
        );

        Label titleLabel =
                muted(title);

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label statusLabel =
                green(status);

        box.getChildren().addAll(
                iconLabel,
                titleLabel,
                valueLabel,
                statusLabel
        );

        HBox.setHgrow(
                box,
                Priority.ALWAYS
        );

        return box;
    }
}