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
import javafx.stage.Stage;

public class PatientUI {

    // =========================================================
    // MAIN SCENE CREATOR
    // =========================================================

    public static Scene createScene(
            Stage stage,
            String activePage,
            String pageTitle,
            String pageSubtitle,
            Node content
    ) {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color: #f1f5f9;"
        );

        // =====================================================
        // LEFT SIDEBAR
        // =====================================================

        VBox sidebar =
                createSidebar(
                        stage,
                        activePage
                );

        root.setLeft(sidebar);

        // =====================================================
        // TOP HEADER
        // =====================================================

        HBox header =
                createHeader(
                        stage,
                        pageTitle
                );

        root.setTop(header);

        // =====================================================
        // MAIN PAGE
        // =====================================================

        VBox page =
                new VBox(20);

        page.setPadding(
                new Insets(28)
        );

        page.setFillWidth(true);

        // =====================================================
        // PAGE TITLE
        // =====================================================

        Label title =
                new Label(pageTitle);

        title.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label subtitle =
                new Label(pageSubtitle);

        subtitle.setWrapText(true);

        subtitle.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #64748b;"
        );

        VBox heading =
                new VBox(
                        5,
                        title,
                        subtitle
                );

        page.getChildren().add(
                heading
        );

        // =====================================================
        // SCREEN CONTENT
        // =====================================================

        if (content != null) {

            page.getChildren().add(
                    content
            );
        }

        // =====================================================
        // SCROLL PANE
        // =====================================================

        ScrollPane scroll =
                new ScrollPane(page);

        scroll.setFitToWidth(true);

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scroll.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scroll.setStyle(
                "-fx-background-color: #f1f5f9;" +
                "-fx-background: #f1f5f9;"
        );

        root.setCenter(scroll);

        // =====================================================
        // SCENE
        // =====================================================

        Scene scene =
                new Scene(
                        root,
                        1440,
                        900
                );

        return scene;
    }

    // =========================================================
    // LEFT SIDEBAR
    // =========================================================

    private static VBox createSidebar(
            Stage stage,
            String activePage
    ) {

        VBox sidebar =
                new VBox(8);

        sidebar.setPrefWidth(255);
        sidebar.setMinWidth(255);
        sidebar.setMaxWidth(255);

        sidebar.setPadding(
                new Insets(
                        22,
                        16,
                        22,
                        16
                )
        );

        sidebar.setStyle(
                "-fx-background-color: #0f172a;"
        );

        // =====================================================
        // LOGO
        // =====================================================

        Label logo =
                new Label(
                        "✚  MediNexus AI"
                );

        logo.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 21px;" +
                "-fx-font-weight: bold;"
        );

        // =====================================================
        // MODULE TITLE
        // =====================================================

        Label module =
                new Label(
                        "Patient Module"
                );

        module.setPadding(
                new Insets(
                        8,
                        10,
                        4,
                        10
                )
        );

        module.setStyle(
                "-fx-text-fill: #94a3b8;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );

        // =====================================================
        // SEPARATOR
        // =====================================================

        Separator separator =
                new Separator();

        separator.setStyle(
                "-fx-background-color: #334155;"
        );

        sidebar.getChildren().addAll(
                logo,
                module,
                separator
        );

        // =====================================================
        // MAIN NAVIGATION
        // =====================================================

        sidebar.getChildren().addAll(

                navItem(
                        stage,
                        "▦",
                        "Dashboard",
                        activePage,
                        "Dashboard"
                ),

                navItem(
                        stage,
                        "⊞",
                        "Search Hospitals",
                        activePage,
                        "Search Hospitals"
                ),

                navItem(
                        stage,
                        "▣",
                        "Appointments",
                        activePage,
                        "Appointments"
                ),

                navItem(
                        stage,
                        "▧",
                        "Health Passport",
                        activePage,
                        "Health Passport"
                ),

                navItem(
                        stage,
                        "▱",
                        "Medical Records",
                        activePage,
                        "Medical Records"
                ),

                navItem(
                        stage,
                        "♙",
                        "AI Health Assistant",
                        activePage,
                        "AI Assistant"
                ),

                navItem(
                        stage,
                        "⌖",
                        "Emergency Assistance",
                        activePage,
                        "Emergency Assistance"
                )
        );

        // =====================================================
        // SPACER
        // =====================================================

        Region spacer =
                new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        sidebar.getChildren().add(
                spacer
        );

        // =====================================================
        // BOTTOM NAVIGATION
        // =====================================================

        sidebar.getChildren().addAll(

                navItem(
                        stage,
                        "♧",
                        "Notifications",
                        activePage,
                        "Notifications"
                ),

                navItem(
                        stage,
                        "⚙",
                        "Profile & Settings",
                        activePage,
                        "Profile & Settings"
                )
        );

        return sidebar;
    }

    // =========================================================
    // NAVIGATION ITEM
    // =========================================================

    private static HBox navItem(
            Stage stage,
            String icon,
            String text,
            String activePage,
            String pageIdentifier
    ) {

        HBox item =
                new HBox(12);

        item.setAlignment(
                Pos.CENTER_LEFT
        );

        item.setPadding(
                new Insets(12)
        );

        item.setPrefHeight(46);
        item.setMinHeight(46);
        item.setMaxWidth(
                Double.MAX_VALUE
        );

        boolean selected =
                pageIdentifier.equals(
                        activePage
                );

        if (selected) {

            item.setStyle(
                    "-fx-background-color: #2563eb;" +
                    "-fx-background-radius: 9;"
            );

        } else {

            item.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-background-radius: 9;"
            );
        }

        // =====================================================
        // ICON
        // =====================================================

        Label iconLabel =
                new Label(icon);

        iconLabel.setPrefWidth(25);

        iconLabel.setAlignment(
                Pos.CENTER
        );

        iconLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 17px;"
        );

        // =====================================================
        // TEXT
        // =====================================================

        Label textLabel =
                new Label(text);

        textLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: " +
                (selected
                        ? "bold;"
                        : "normal;")
        );

        item.getChildren().addAll(
                iconLabel,
                textLabel
        );

        // =====================================================
        // HOVER
        // =====================================================

        if (!selected) {

            item.setOnMouseEntered(
                    e -> item.setStyle(
                            "-fx-background-color: #1e293b;" +
                            "-fx-background-radius: 9;"
                    )
            );

            item.setOnMouseExited(
                    e -> item.setStyle(
                            "-fx-background-color: transparent;" +
                            "-fx-background-radius: 9;"
                    )
            );
        }

        // =====================================================
        // DIRECT NAVIGATION
        // =====================================================

        item.setOnMouseClicked(
                e -> navigate(
                        stage,
                        pageIdentifier
                )
        );

        return item;
    }

    // =========================================================
    // DIRECT STAGE NAVIGATION
    // =========================================================

    private static void navigate(
            Stage stage,
            String page
    ) {

        switch (page) {

            case "Dashboard":

                stage.setScene(
                        new Dashboard(stage)
                                .getScene()
                );

                break;

            case "Search Hospitals":

                stage.setScene(
                        new SearchHospitals(stage)
                                .getScene()
                );

                break;

            case "Appointments":

                stage.setScene(
                        new Appointments(stage)
                                .getScene()
                );

                break;

            case "Health Passport":

                stage.setScene(
                        new HealthPassport(stage)
                                .getScene()
                );

                break;

            case "Medical Records":

                stage.setScene(
                        new MedicalRecords(stage)
                                .getScene()
                );

                break;

            case "AI Assistant":

                stage.setScene(
                        new AiHealthAssistant(stage)
                                .getScene()
                );

                break;

            case "Emergency Assistance":

                stage.setScene(
                        new EmergencyAssistance(stage)
                                .getScene()
                );

                break;

            case "Notifications":

                stage.setScene(
                        new Notifications(stage)
                                .getScene()
                );

                break;

            case "Profile & Settings":

                stage.setScene(
                        new ProfileSettings(stage)
                                .getScene()
                );

                break;

            default:

                System.out.println(
                        "Unknown page: " + page
                );

                return;
        }

        stage.show();
    }

    // =========================================================
    // TOP HEADER
    // =========================================================

    private static HBox createHeader(
            Stage stage,
            String pageTitle
    ) {

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        header.setPadding(
                new Insets(
                        14,
                        28,
                        14,
                        28
                )
        );

        header.setPrefHeight(70);

        header.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #e2e8f0;" +
                "-fx-border-width: 0 0 1 0;"
        );

        // =====================================================
        // PAGE TITLE
        // =====================================================

        Label currentPage =
                new Label(pageTitle);

        currentPage.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        // =====================================================
        // SPACER
        // =====================================================

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        // =====================================================
        // NOTIFICATION
        // =====================================================

        Button notification =
                new Button("♧");

        notification.setPrefWidth(42);
        notification.setPrefHeight(38);

        notification.setStyle(
                "-fx-background-color: #eff6ff;" +
                "-fx-text-fill: #2563eb;" +
                "-fx-font-size: 18px;" +
                "-fx-background-radius: 9;" +
                "-fx-cursor: hand;"
        );

        notification.setOnAction(
                e -> stage.setScene(
                        new Notifications(stage)
                                .getScene()
                )
        );

        // =====================================================
        // PROFILE
        // =====================================================

        Button profile =
                new Button("Sarah");

        profile.setPrefHeight(38);

        profile.setStyle(
                "-fx-background-color: #eff6ff;" +
                "-fx-text-fill: #2563eb;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 9;" +
                "-fx-padding: 8 18;" +
                "-fx-cursor: hand;"
        );

        profile.setOnAction(
                e -> stage.setScene(
                        new ProfileSettings(stage)
                                .getScene()
                )
        );

        header.getChildren().addAll(
                currentPage,
                spacer,
                notification,
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

        VBox box =
                new VBox(12);

        box.setPadding(
                new Insets(20)
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #cbd5e1;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 14;"
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

            box.getChildren().add(
                    label
            );
        }

        return box;
    }

    // =========================================================
    // COLORED CARD
    // =========================================================

    public static VBox coloredCard(
            String title,
            String backgroundColor
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
                "-fx-border-color: #cbd5e1;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 14;"
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

            box.getChildren().add(
                    label
            );
        }

        return box;
    }

    // =========================================================
    // STANDARD BUTTON
    // =========================================================

    public static Button button(
            String text,
            Runnable action
    ) {

        Button button =
                new Button(text);

        button.setPrefHeight(42);

        button.setStyle(
                "-fx-background-color: #2563eb;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 8 16;" +
                "-fx-cursor: hand;"
        );

        button.setOnAction(
                e -> {

                    if (action != null) {
                        action.run();
                    }
                }
        );

        return button;
    }

    // =========================================================
    // SECONDARY BUTTON
    // =========================================================

    public static Button secondaryButton(
            String text,
            Runnable action
    ) {

        Button button =
                new Button(text);

        button.setPrefHeight(42);

        button.setStyle(
                "-fx-background-color: #eff6ff;" +
                "-fx-text-fill: #2563eb;" +
                "-fx-font-weight: bold;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-width: 1;" +
                "-fx-background-radius: 8;" +
                "-fx-border-radius: 8;" +
                "-fx-padding: 8 16;" +
                "-fx-cursor: hand;"
        );

        button.setOnAction(
                e -> {

                    if (action != null) {
                        action.run();
                    }
                }
        );

        return button;
    }

    // =========================================================
    // MUTED LABEL
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

    // =========================================================
    // GREEN LABEL
    // =========================================================

    public static Label green(
            String text
    ) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-text-fill: #16a34a;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;"
        );

        return label;
    }

    // =========================================================
    // BLUE LABEL
    // =========================================================

    public static Label blue(
            String text
    ) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-text-fill: #2563eb;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;"
        );

        return label;
    }

    // =========================================================
    // RED LABEL
    // =========================================================

    public static Label red(
            String text
    ) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-text-fill: #dc2626;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;"
        );

        return label;
    }

    // =========================================================
    // ORANGE LABEL
    // =========================================================

    public static Label orange(
            String text
    ) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-text-fill: #ea580c;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;"
        );

        return label;
    }
}