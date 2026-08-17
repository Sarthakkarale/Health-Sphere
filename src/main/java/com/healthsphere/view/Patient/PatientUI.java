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
    // COMMON PATIENT SCENE
    // =========================================================

    public static Scene createScene(
            Stage stage,
            String activePage,
            String pageTitle,
            String pageSubtitle,
            javafx.scene.Node content
    ) {

        // =====================================================
        // ROOT
        // =====================================================

        BorderPane root = new BorderPane();

        root.setMinWidth(0);
        root.setMinHeight(0);

        root.setPrefWidth(0);
        root.setPrefHeight(0);

        root.setMaxWidth(Double.MAX_VALUE);
        root.setMaxHeight(Double.MAX_VALUE);

        root.setStyle(
                "-fx-background-color: #f1f5f9;"
        );

        // =====================================================
        // SIDEBAR
        // =====================================================

        VBox sidebar =
                createSidebar(
                        stage,
                        activePage
                );

        root.setLeft(sidebar);

        // =====================================================
        // HEADER
        // =====================================================

        HBox header =
                createHeader(
                        stage,
                        pageTitle
                );

        root.setTop(header);

        // =====================================================
        // PAGE
        // =====================================================

        VBox page =
                new VBox(20);

        page.setPadding(
                new Insets(28)
        );

        page.setSpacing(20);

        page.setFillWidth(true);

        page.setMinWidth(0);
        page.setPrefWidth(0);
        page.setMaxWidth(Double.MAX_VALUE);

        page.setMinHeight(0);
        page.setMaxHeight(Double.MAX_VALUE);

        // =====================================================
        // PAGE HEADING
        // =====================================================

        Label title =
                new Label(
                        pageTitle == null
                                ? ""
                                : pageTitle
                );

        title.setWrapText(true);

        title.setMaxWidth(
                Double.MAX_VALUE
        );

        title.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label subtitle =
                new Label(
                        pageSubtitle == null
                                ? ""
                                : pageSubtitle
                );

        subtitle.setWrapText(true);

        subtitle.setMaxWidth(
                Double.MAX_VALUE
        );

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

        heading.setMinWidth(0);
        heading.setMaxWidth(
                Double.MAX_VALUE
        );

        page.getChildren().add(
                heading
        );

        // =====================================================
        // SCREEN CONTENT
        // =====================================================

        if (content != null) {

            if (content instanceof Region) {

                Region region =
                        (Region) content;

                region.setMinWidth(0);

                region.setPrefWidth(0);

                region.setMaxWidth(
                        Double.MAX_VALUE
                );
            }

            page.getChildren().add(
                    content
            );
        }

        // =====================================================
        // SCROLL PANE
        // =====================================================

        ScrollPane scroll =
                new ScrollPane();

        scroll.setContent(page);

        /*
         * The page always follows the available width.
         */
        scroll.setFitToWidth(true);

        /*
         * Keep false so long pages can scroll vertically.
         */
        scroll.setFitToHeight(false);

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scroll.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scroll.setPannable(true);

        scroll.setMinWidth(0);
        scroll.setMinHeight(0);

        scroll.setPrefWidth(0);
        scroll.setPrefHeight(0);

        scroll.setMaxWidth(
                Double.MAX_VALUE
        );

        scroll.setMaxHeight(
                Double.MAX_VALUE
        );

        scroll.setStyle(
                "-fx-background-color: #f1f5f9;" +
                "-fx-background: #f1f5f9;" +
                "-fx-border-color: transparent;"
        );

        /*
         * Force the page to follow the ScrollPane viewport.
         */
        scroll.viewportBoundsProperty().addListener(
                (obs, oldBounds, newBounds) -> {

                    if (newBounds != null) {

                        double width =
                                newBounds.getWidth();

                        if (width > 0) {

                            page.setPrefWidth(
                                    width
                            );

                            page.setMinWidth(
                                    width
                            );
                        }
                    }
                }
        );

        // =====================================================
        // CENTER
        // =====================================================

        root.setCenter(scroll);

        BorderPane.setAlignment(
                scroll,
                Pos.CENTER
        );

        BorderPane.setMargin(
                scroll,
                Insets.EMPTY
        );

        // =====================================================
        // SCENE
        // =====================================================

        Scene scene =
                new Scene(root);

        /*
         * IMPORTANT:
         *
         * PatientUI does NOT:
         *
         * stage.show()
         * stage.setMaximized()
         * stage.sizeToScene()
         *
         * The shared Stage is controlled by View.
         */

        /*
         * Make root automatically follow the Scene size.
         */
        root.prefWidthProperty().bind(
                scene.widthProperty()
        );

        root.prefHeightProperty().bind(
                scene.heightProperty()
        );

        /*
         * Initial layout.
         */
        scene.getRoot().applyCss();
        scene.getRoot().layout();

        return scene;
    }

    // =========================================================
    // SIDEBAR
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
                        "✚  HealthSphere"
                );

        logo.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 21px;" +
                "-fx-font-weight: bold;"
        );

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
        // NAVIGATION
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

        item.setStyle(
                "-fx-background-color: " +
                (
                        selected
                                ? "#2563eb"
                                : "transparent"
                ) +
                ";" +
                "-fx-background-radius: 9;" +
                "-fx-cursor: hand;"
        );

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

        Label textLabel =
                new Label(text);

        textLabel.setWrapText(true);

        textLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: " +
                (
                        selected
                                ? "bold;"
                                : "normal;"
                )
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
                            "-fx-background-radius: 9;" +
                            "-fx-cursor: hand;"
                    )
            );

            item.setOnMouseExited(
                    e -> item.setStyle(
                            "-fx-background-color: transparent;" +
                            "-fx-background-radius: 9;" +
                            "-fx-cursor: hand;"
                    )
            );
        }

        // =====================================================
        // CLICK
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
    // CENTRALIZED NAVIGATION
    // =========================================================

    private static void navigate(
            Stage stage,
            String page
    ) {

        if (stage == null) {
            return;
        }

        Scene scene;

        switch (page) {

            case "Dashboard":

                scene =
                        new Dashboard(stage)
                                .getScene();

                break;

            case "Search Hospitals":

                scene =
                        new SearchHospitals(stage)
                                .getScene();

                break;

            case "Appointments":

                scene =
                        new Appointments(stage)
                                .getScene();

                break;

            case "Health Passport":

                scene =
                        new HealthPassport(stage)
                                .getScene();

                break;

            case "Medical Records":

                scene =
                        new MedicalRecords(stage)
                                .getScene();

                break;

            case "AI Assistant":

                scene =
                        new AiHealthAssistant(stage)
                                .getScene();

                break;

            case "Emergency Assistance":

                scene =
                        new EmergencyAssistance(stage)
                                .getScene();

                break;

            case "Notifications":

                scene =
                        new Notifications(stage)
                                .getScene();

                break;

            case "Profile & Settings":

                scene =
                        new ProfileSettings(stage)
                                .getScene();

                break;

            default:
                return;
        }

        /*
         * SAME SHARED STAGE.
         */
        stage.setScene(scene);

        /*
         * IMPORTANT:
         *
         * Do not call setMaximized(true) here.
         *
         * If the Stage is already maximized, it stays maximized.
         */
    }

    // =========================================================
    // HEADER
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
        header.setMinHeight(70);
        header.setMaxHeight(70);

        header.setMaxWidth(
                Double.MAX_VALUE
        );

        header.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #e2e8f0;" +
                "-fx-border-width: 0 0 1 0;"
        );

        // =====================================================
        // CURRENT PAGE
        // =====================================================

        Label currentPage =
                new Label(
                        pageTitle == null
                                ? ""
                                : pageTitle
                );

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

        notification.setPrefWidth(48);
        notification.setPrefHeight(42);

        notification.setStyle(
                "-fx-background-color: #eff6ff;" +
                "-fx-text-fill: #2563eb;" +
                "-fx-font-size: 18px;" +
                "-fx-background-radius: 9;" +
                "-fx-cursor: hand;"
        );

        notification.setOnAction(
                e -> navigate(
                        stage,
                        "Notifications"
                )
        );

        // =====================================================
        // PROFILE
        // =====================================================

        Button profile =
                new Button("Sarah");

        profile.setPrefHeight(42);

        profile.setStyle(
                "-fx-background-color: #eff6ff;" +
                "-fx-text-fill: #2563eb;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 9;" +
                "-fx-padding: 8 18;" +
                "-fx-cursor: hand;"
        );

        profile.setOnAction(
                e -> navigate(
                        stage,
                        "Profile & Settings"
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
    // NORMAL CARD
    // =========================================================

    public static VBox card(
            String title
    ) {

        VBox box =
                new VBox(12);

        box.setPadding(
                new Insets(20)
        );

        box.setMinWidth(0);

        box.setMaxWidth(
                Double.MAX_VALUE
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #cbd5e1;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 14;"
        );

        if (title != null &&
                !title.isBlank()) {

            Label label =
                    new Label(title);

            label.setWrapText(true);

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

        box.setMinWidth(0);

        box.setMaxWidth(
                Double.MAX_VALUE
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
                !title.isBlank()) {

            Label label =
                    new Label(title);

            label.setWrapText(true);

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
    // BUTTON
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
    // BLUE BUTTON
    // =========================================================

    public static Button blueButton(
            String text,
            Runnable action
    ) {

        return button(
                text,
                action
        );
    }

    // =========================================================
    // ORANGE BUTTON
    // =========================================================

    public static Button orangeButton(
            String text,
            Runnable action
    ) {

        Button button =
                button(
                        text,
                        action
                );

        button.setStyle(
                "-fx-background-color: #f97316;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 8 16;" +
                "-fx-cursor: hand;"
        );

        return button;
    }

    // =========================================================
    // RED BUTTON
    // =========================================================

    public static Button redButton(
            String text,
            Runnable action
    ) {

        Button button =
                button(
                        text,
                        action
                );

        button.setStyle(
                "-fx-background-color: #dc2626;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 8 16;" +
                "-fx-cursor: hand;"
        );

        return button;
    }

    // =========================================================
    // BLUE CARD
    // =========================================================

    public static VBox blueCard(
            String title
    ) {

        return coloredCard(
                title,
                "#dbeafe"
        );
    }

    // =========================================================
    // GREEN CARD
    // =========================================================

    public static VBox greenCard(
            String title
    ) {

        return coloredCard(
                title,
                "#dcfce7"
        );
    }

    // =========================================================
    // ORANGE CARD
    // =========================================================

    public static VBox orangeCard(
            String title
    ) {

        return coloredCard(
                title,
                "#fff7ed"
        );
    }

    // =========================================================
    // PURPLE CARD
    // =========================================================

    public static VBox purpleCard(
            String title
    ) {

        return coloredCard(
                title,
                "#f3e8ff"
        );
    }

    // =========================================================
    // TEAL CARD
    // =========================================================

    public static VBox tealCard(
            String title
    ) {

        return coloredCard(
                title,
                "#ccfbf1"
        );
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

    // =========================================================
    // STAT CARD
    // =========================================================

    public static VBox statCard(
            String icon,
            String title,
            String value,
            String status,
            String background,
            String border
    ) {

        VBox card =
                new VBox(7);

        card.setPadding(
                new Insets(18)
        );

        card.setMinWidth(0);

        card.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox.setHgrow(
                card,
                Priority.ALWAYS
        );

        card.setStyle(
                "-fx-background-color: " +
                background +
                ";" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: " +
                border +
                ";" +
                "-fx-border-radius: 14;"
        );

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-font-size: 24px;"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setWrapText(true);

        titleLabel.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #64748b;"
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label statusLabel =
                new Label(status);

        statusLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #16a34a;"
        );

        card.getChildren().addAll(
                iconLabel,
                titleLabel,
                valueLabel,
                statusLabel
        );

        return card;
    }
}