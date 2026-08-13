package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HealthPassport {

    private final Stage stage;

    public HealthPassport(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color: #f1f5f9;"
        );

        root.setLeft(createSidebar());
        root.setTop(createHeader());

        VBox content = new VBox(22);
        content.setPadding(new Insets(28));

        Label title = new Label("Health Passport");
        title.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label subtitle = new Label(
                "Your complete health profile and important medical information."
        );

        subtitle.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #64748b;"
        );

        VBox heading = new VBox(5, title, subtitle);

        /*
         * Patient Summary
         */
        VBox patientCard = coloredCard(
                "#dbeafe",
                "#2563eb"
        );

        Label patientTitle = new Label("Patient Information");
        patientTitle.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1e3a8a;"
        );

        GridPane patientGrid = new GridPane();

        patientGrid.setHgap(50);
        patientGrid.setVgap(15);

        patientGrid.add(
                information("Patient Name", "Sarah Johnson"),
                0,
                0
        );

        patientGrid.add(
                information("Date of Birth", "14 March 1998"),
                1,
                0
        );

        patientGrid.add(
                information("Blood Group", "O+"),
                0,
                1
        );

        patientGrid.add(
                information("Gender", "Female"),
                1,
                1
        );

        patientCard.getChildren().addAll(
                patientTitle,
                patientGrid
        );

        /*
         * Medical Information
         */
        HBox medicalRow = new HBox(18);

        VBox allergies = coloredCard(
                "#fee2e2",
                "#dc2626"
        );

        allergies.getChildren().addAll(
                cardTitle(
                        "Allergies",
                        "#991b1b"
                ),
                item(
                        "Penicillin",
                        "Medication allergy"
                ),
                item(
                        "Dust",
                        "Environmental allergy"
                )
        );

        VBox conditions = coloredCard(
                "#fef3c7",
                "#d97706"
        );

        conditions.getChildren().addAll(
                cardTitle(
                        "Medical Conditions",
                        "#92400e"
                ),
                item(
                        "Hypertension",
                        "Under monitoring"
                ),
                item(
                        "Seasonal Asthma",
                        "Mild condition"
                )
        );

        VBox medications = coloredCard(
                "#dcfce7",
                "#16a34a"
        );

        medications.getChildren().addAll(
                cardTitle(
                        "Current Medications",
                        "#166534"
                ),
                item(
                        "Amlodipine",
                        "5 mg • Once daily"
                ),
                item(
                        "Vitamin D3",
                        "Once weekly"
                )
        );

        HBox.setHgrow(
                allergies,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                conditions,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                medications,
                Priority.ALWAYS
        );

        medicalRow.getChildren().addAll(
                allergies,
                conditions,
                medications
        );

        /*
         * Emergency Information
         */
        VBox emergencyCard = coloredCard(
                "#fce7f3",
                "#db2777"
        );

        Label emergencyTitle = new Label(
                "Emergency Information"
        );

        emergencyTitle.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #9d174d;"
        );

        Label emergencyContact = new Label(
                "Emergency Contact: Robert Johnson"
        );

        Label relationship = new Label(
                "Relationship: Spouse"
        );

        Label phone = new Label(
                "Phone: +91 98765 43210"
        );

        emergencyContact.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;"
        );

        relationship.setStyle(
                "-fx-text-fill: #64748b;"
        );

        phone.setStyle(
                "-fx-text-fill: #64748b;"
        );

        emergencyCard.getChildren().addAll(
                emergencyTitle,
                emergencyContact,
                relationship,
                phone
        );

        /*
         * Recent Health Activity
         */
        VBox activityCard = coloredCard(
                "#ede9fe",
                "#7c3aed"
        );

        Label activityTitle = new Label(
                "Recent Health Activity"
        );

        activityTitle.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #5b21b6;"
        );

        activityCard.getChildren().addAll(
                activityTitle,
                activity(
                        "Blood Pressure Check",
                        "118 / 76 mmHg",
                        "2 days ago"
                ),
                new Separator(),
                activity(
                        "Heart Rate",
                        "72 BPM",
                        "2 days ago"
                ),
                new Separator(),
                activity(
                        "Medical Consultation",
                        "Cardiology",
                        "1 week ago"
                )
        );

        /*
         * Buttons
         */
        HBox buttons = new HBox(12);

        Button recordsButton = new Button(
                "View Medical Records"
        );

        recordsButton.setPrefHeight(42);

        recordsButton.setStyle(
                "-fx-background-color: #2563eb;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 0 20;"
        );

        recordsButton.setOnAction(
                e -> stage.setScene(
                        new MedicalRecords(stage).getScene()
                )
        );

        Button backButton = new Button("Back");

        backButton.setPrefHeight(42);

        backButton.setStyle(
                "-fx-background-color: #e2e8f0;" +
                "-fx-text-fill: #0f172a;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 0 20;"
        );

        backButton.setOnAction(
                e -> stage.setScene(
                        new Dashboard(stage).getScene()
                )
        );

        buttons.getChildren().addAll(
                recordsButton,
                backButton
        );

        content.getChildren().addAll(
                heading,
                patientCard,
                medicalRow,
                emergencyCard,
                activityCard,
                buttons
        );

        ScrollPane scrollPane =
                new ScrollPane(content);

        scrollPane.setFitToWidth(true);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setStyle(
                "-fx-background-color: transparent;"
        );

        root.setCenter(scrollPane);

        return new Scene(
                root,
                1440,
                900
        );
    }

    /*
     * SIDEBAR
     */
    private VBox createSidebar() {

        VBox sidebar = new VBox(8);

        sidebar.setPrefWidth(255);

        sidebar.setPadding(
                new Insets(22)
        );

        sidebar.setStyle(
                "-fx-background-color: #0f172a;"
        );

        Label brand = new Label(
                "✚  MediNexus AI"
        );

        brand.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 21px;" +
                "-fx-font-weight: bold;"
        );

        Label module = new Label(
                "Patient Module"
        );

        module.setStyle(
                "-fx-text-fill: #94a3b8;" +
                "-fx-font-size: 13px;"
        );

        Separator separator = new Separator();

        Region spacer = new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        sidebar.getChildren().addAll(
                brand,
                module,
                separator,

                navButton(
                        "▦",
                        "Dashboard",
                        false,
                        () -> showDashboard()
                ),

                navButton(
                        "⊞",
                        "Search Hospitals",
                        false,
                        () -> showSearchHospitals()
                ),

                navButton(
                        "▣",
                        "Appointments",
                        false,
                        () -> showAppointments()
                ),

                navButton(
                        "▧",
                        "Health Passport",
                        true,
                        () -> showHealthPassport()
                ),

                navButton(
                        "▱",
                        "Medical Records",
                        false,
                        () -> showMedicalRecords()
                ),

                navButton(
                        "♙",
                        "AI Health Assistant",
                        false,
                        () -> showAIHealthAssistant()
                ),

                navButton(
                        "⌖",
                        "Emergency Assistance",
                        false,
                        () -> showEmergencyAssistance()
                ),

                spacer,

                navButton(
                        "♧",
                        "Notifications",
                        false,
                        () -> showNotifications()
                ),

                navButton(
                        "⚙",
                        "Profile & Settings",
                        false,
                        () -> showProfileSettings()
                )
        );

        return sidebar;
    }

    private HBox navButton(
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

        item.setMaxWidth(
                Double.MAX_VALUE
        );

        item.setStyle(
                "-fx-background-color: " +
                        (selected
                                ? "#2563eb;"
                                : "transparent;") +
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

    /*
     * HEADER
     */
    private HBox createHeader() {

        HBox header = new HBox();

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

        Region spacer = new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button notifications =
                new Button("Notifications");

        notifications.setOnAction(
                e -> showNotifications()
        );

        Button profile =
                new Button("Sarah");

        profile.setOnAction(
                e -> showProfileSettings()
        );

        header.getChildren().addAll(
                spacer,
                notifications,
                profile
        );

        return header;
    }

    /*
     * COLORED CARD
     */
    private VBox coloredCard(
            String background,
            String borderColor
    ) {

        VBox box = new VBox(12);

        box.setPadding(
                new Insets(20)
        );

        box.setStyle(
                "-fx-background-color: " +
                        background + ";" +
                "-fx-border-color: " +
                        borderColor + ";" +
                "-fx-border-width: 1.5;" +
                "-fx-background-radius: 14;" +
                "-fx-border-radius: 14;"
        );

        return box;
    }

    private Label cardTitle(
            String text,
            String color
    ) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " +
                color + ";"
        );

        return label;
    }

    private VBox information(
            String title,
            String value
    ) {

        VBox box = new VBox(3);

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #64748b;"
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        box.getChildren().addAll(
                titleLabel,
                valueLabel
        );

        return box;
    }

    private VBox item(
            String title,
            String description
    ) {

        VBox box = new VBox(3);

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-weight: bold;"
        );

        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        box.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );

        return box;
    }

    private HBox activity(
            String title,
            String value,
            String date
    ) {

        HBox row = new HBox(15);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox details =
                new VBox(3);

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-weight: bold;"
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-text-fill: #475569;"
        );

        details.getChildren().addAll(
                titleLabel,
                valueLabel
        );

        Region spacer = new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label dateLabel =
                new Label(date);

        dateLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        row.getChildren().addAll(
                details,
                spacer,
                dateLabel
        );

        return row;
    }

    /*
     * DIRECT NAVIGATION
     */
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