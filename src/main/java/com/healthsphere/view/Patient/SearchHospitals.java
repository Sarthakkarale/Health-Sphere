package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SearchHospitals {

    private final Stage stage;

    public SearchHospitals(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {

        BorderPane root = new BorderPane();

        root.setLeft(createSidebar());
        root.setTop(createHeader());

        VBox content = new VBox(22);
        content.setPadding(new Insets(28));
        content.setStyle("-fx-background-color: #f8fafc;");

        Label title = new Label("Search Hospitals");
        title.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label subtitle = new Label(
                "Find hospitals, doctors and healthcare facilities near you."
        );
        subtitle.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #64748b;"
        );

        VBox heading = new VBox(5, title, subtitle);

        // ---------------------------------------------------------
        // SEARCH CARD
        // ---------------------------------------------------------

        VBox searchCard = createCard(
                "#eff6ff",
                "#bfdbfe"
        );

        Label searchTitle = new Label("Find a Hospital");
        searchTitle.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1e3a8a;"
        );

        Label searchDescription = new Label(
                "Search by hospital name, city or medical specialty."
        );
        searchDescription.setStyle(
                "-fx-text-fill: #475569;"
        );

        TextField searchField = new TextField();
        searchField.setPromptText(
                "Search hospital, city or specialty..."
        );

        searchField.setPrefHeight(45);
        searchField.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10;"
        );

        Button searchButton = new Button("Search");
        searchButton.setPrefHeight(45);
        searchButton.setStyle(
                "-fx-background-color: #2563eb;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 0 24;"
        );

        searchButton.setOnAction(e -> {
            String query = searchField.getText().trim();

            if (!query.isEmpty()) {
                System.out.println(
                        "Searching hospitals for: " + query
                );
            }
        });

        HBox searchRow = new HBox(10);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        searchRow.getChildren().addAll(
                searchField,
                searchButton
        );

        HBox.setHgrow(
                searchField,
                Priority.ALWAYS
        );

        searchCard.getChildren().addAll(
                searchTitle,
                searchDescription,
                searchRow
        );

        // ---------------------------------------------------------
        // RECOMMENDED HOSPITALS
        // ---------------------------------------------------------

        VBox hospitalsCard = createCard(
                "#f0fdf4",
                "#bbf7d0"
        );

        Label hospitalsTitle = new Label(
                "Recommended Hospitals"
        );

        hospitalsTitle.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #166534;"
        );

        Label hospitalsSubtitle = new Label(
                "Healthcare facilities you may be interested in."
        );

        hospitalsSubtitle.setStyle(
                "-fx-text-fill: #64748b;"
        );

        hospitalsCard.getChildren().addAll(
                hospitalsTitle,
                hospitalsSubtitle,

                hospitalCard(
                        "Apollo Hospitals",
                        "Jubilee Hills",
                        "Multi-Speciality Hospital",
                        "4.7 ★",
                        "#dbeafe",
                        "#1d4ed8"
                ),

                hospitalCard(
                        "Fortis Healthcare",
                        "Bengaluru",
                        "Multi-Speciality Hospital",
                        "4.6 ★",
                        "#dcfce7",
                        "#15803d"
                ),

                hospitalCard(
                        "Max Healthcare",
                        "New Delhi",
                        "Multi-Speciality Hospital",
                        "4.5 ★",
                        "#fef3c7",
                        "#b45309"
                )
        );

        // ---------------------------------------------------------
        // EMERGENCY CARD
        // ---------------------------------------------------------

        VBox emergencyCard = createCard(
                "#fef2f2",
                "#fecaca"
        );

        Label emergencyTitle = new Label(
                "Need Emergency Care?"
        );

        emergencyTitle.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #991b1b;"
        );

        Label emergencyText = new Label(
                "For urgent medical assistance, use the Emergency Assistance section."
        );

        emergencyText.setWrapText(true);
        emergencyText.setStyle(
                "-fx-text-fill: #7f1d1d;"
        );

        Button emergencyButton = new Button(
                "Emergency Assistance"
        );

        emergencyButton.setStyle(
                "-fx-background-color: #dc2626;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10 18;"
        );

        emergencyButton.setOnAction(e -> {
            stage.setScene(
                    new EmergencyAssistance(stage).getScene()
            );
            stage.show();
        });

        emergencyCard.getChildren().addAll(
                emergencyTitle,
                emergencyText,
                emergencyButton
        );

        content.getChildren().addAll(
                heading,
                searchCard,
                hospitalsCard,
                emergencyCard
        );

        root.setCenter(content);

        return new Scene(root, 1440, 900);
    }

    // =============================================================
    // HOSPITAL CARD
    // =============================================================

    private VBox hospitalCard(
            String name,
            String location,
            String type,
            String rating,
            String background,
            String textColor
    ) {

        VBox card = new VBox(10);

        card.setPadding(new Insets(18));

        card.setStyle(
                "-fx-background-color: " + background + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #e2e8f0;" +
                "-fx-border-radius: 12;"
        );

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label hospitalIcon = new Label("✚");

        hospitalIcon.setStyle(
                "-fx-font-size: 24px;" +
                "-fx-text-fill: " + textColor + ";" +
                "-fx-font-weight: bold;"
        );

        VBox information = new VBox(4);

        Label nameLabel = new Label(name);

        nameLabel.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label locationLabel = new Label(
                "📍 " + location
        );

        locationLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        Label typeLabel = new Label(type);

        typeLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        information.getChildren().addAll(
                nameLabel,
                locationLabel,
                typeLabel
        );

        HBox.setHgrow(
                information,
                Priority.ALWAYS
        );

        Label ratingLabel = new Label(
                rating
        );

        ratingLabel.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #b45309;"
        );

        topRow.getChildren().addAll(
                hospitalIcon,
                information,
                ratingLabel
        );

        Button viewButton = new Button(
                "View Details"
        );

        viewButton.setPrefHeight(38);

        viewButton.setStyle(
                "-fx-background-color: white;" +
                "-fx-text-fill: #2563eb;" +
                "-fx-font-weight: bold;" +
                "-fx-border-color: #93c5fd;" +
                "-fx-border-radius: 7;" +
                "-fx-background-radius: 7;"
        );

        viewButton.setOnAction(e -> {

            System.out.println(
                    "Selected hospital: " + name
            );

            // Later you can navigate to:
            // new HospitalDetails(stage, name).getScene()
        });

        card.getChildren().addAll(
                topRow,
                viewButton
        );

        return card;
    }

    // =============================================================
    // COMMON CARD
    // =============================================================

    private VBox createCard(
            String background,
            String border
    ) {

        VBox card = new VBox(12);

        card.setPadding(new Insets(20));

        card.setStyle(
                "-fx-background-color: " + background + ";" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: " + border + ";" +
                "-fx-border-radius: 14;"
        );

        return card;
    }

    // =============================================================
    // HEADER
    // =============================================================

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

        Button notifications =
                new Button("Notifications");

        notifications.setOnAction(e -> {
            stage.setScene(
                    new Notifications(stage).getScene()
            );
            stage.show();
        });

        Button profile =
                new Button("Sarah");

        profile.setOnAction(e -> {
            stage.setScene(
                    new ProfileSettings(stage).getScene()
            );
            stage.show();
        });

        header.getChildren().addAll(
                spacer,
                notifications,
                profile
        );

        return header;
    }

    // =============================================================
    // SIDEBAR
    // =============================================================

    private VBox createSidebar() {

        VBox sidebar = new VBox(8);

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

                navButton(
                        "▦",
                        "Dashboard",
                        () -> {
                            stage.setScene(
                                    new Dashboard(stage).getScene()
                            );
                            stage.show();
                        }
                ),

                navButton(
                        "⊞",
                        "Search Hospitals",
                        () -> {}
                ),

                navButton(
                        "▣",
                        "Appointments",
                        () -> {
                            stage.setScene(
                                    new Appointments(stage).getScene()
                            );
                            stage.show();
                        }
                ),

                navButton(
                        "▧",
                        "Health Passport",
                        () -> {
                            stage.setScene(
                                    new HealthPassport(stage).getScene()
                            );
                            stage.show();
                        }
                ),

                navButton(
                        "▱",
                        "Medical Records",
                        () -> {
                            stage.setScene(
                                    new MedicalRecords(stage).getScene()
                            );
                            stage.show();
                        }
                ),

                navButton(
                        "♙",
                        "AI Health Assistant",
                        () -> {
                            stage.setScene(
                                    new AiHealthAssistant(stage).getScene()
                            );
                            stage.show();
                        }
                ),

                navButton(
                        "⌖",
                        "Emergency Assistance",
                        () -> {
                            stage.setScene(
                                    new EmergencyAssistance(stage).getScene()
                            );
                            stage.show();
                        }
                ),

                spacer,

                navButton(
                        "♧",
                        "Notifications",
                        () -> {
                            stage.setScene(
                                    new Notifications(stage).getScene()
                            );
                            stage.show();
                        }
                ),

                navButton(
                        "⚙",
                        "Profile & Settings",
                        () -> {
                            stage.setScene(
                                    new ProfileSettings(stage).getScene()
                            );
                            stage.show();
                        }
                )
        );

        return sidebar;
    }

    // =============================================================
    // SIDEBAR BUTTON
    // =============================================================

    private HBox navButton(
            String icon,
            String text,
            Runnable action
    ) {

        HBox item = new HBox(12);

        item.setAlignment(
                Pos.CENTER_LEFT
        );

        item.setPadding(
                new Insets(12)
        );

        item.setMaxWidth(
                Double.MAX_VALUE
        );

        item.setStyle(
                "-fx-background-color: transparent;" +
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

        item.setOnMouseEntered(e ->
                item.setStyle(
                        "-fx-background-color: #1e293b;" +
                        "-fx-background-radius: 8;"
                )
        );

        item.setOnMouseExited(e ->
                item.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-background-radius: 8;"
                )
        );

        item.setOnMouseClicked(e ->
                action.run()
        );

        return item;
    }
}