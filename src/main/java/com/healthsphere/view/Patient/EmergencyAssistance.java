package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

public class EmergencyAssistance {

    private final Stage stage;

    public EmergencyAssistance(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color: #f1f5f9;"
        );

        root.setLeft(createSidebar());
        root.setTop(createHeader());

        VBox content =
                new VBox(22);

        content.setPadding(
                new Insets(28)
        );

        Label title =
                new Label("Emergency Assistance");

        title.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label subtitle =
                new Label(
                        "Quickly access emergency contacts and assistance."
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

        /*
         * Emergency alert
         */

        VBox emergency =
                coloredCard(
                        "#fee2e2",
                        "#dc2626"
                );

        Label emergencyTitle =
                new Label(
                        "⚠ Emergency Assistance"
                );

        emergencyTitle.setStyle(
                "-fx-font-size: 23px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #991b1b;"
        );

        Label emergencyText =
                new Label(
                        "If you are experiencing a life-threatening emergency, " +
                        "contact your local emergency service immediately."
                );

        emergencyText.setWrapText(true);

        emergencyText.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #7f1d1d;"
        );

        Button emergencyButton =
                new Button(
                        "CALL EMERGENCY SERVICES"
                );

        emergencyButton.setPrefHeight(48);

        emergencyButton.setStyle(
                "-fx-background-color: #dc2626;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 15px;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 0 24;"
        );

        emergencyButton.setOnAction(
                e -> showAlert(
                        "Emergency Services",
                        "Emergency services contact initiated."
                )
        );

        emergency.getChildren().addAll(
                emergencyTitle,
                emergencyText,
                emergencyButton
        );

        /*
         * Emergency contacts
         */

        HBox contacts =
                new HBox(18);

        VBox primary =
                coloredCard(
                        "#fce7f3",
                        "#db2777"
                );

        primary.getChildren().addAll(
                cardTitle(
                        "Primary Emergency Contact",
                        "#9d174d"
                ),

                contact(
                        "Robert Johnson",
                        "Spouse",
                        "+91 98765 43210"
                )
        );

        Button callPrimary =
                coloredButton(
                        "Call Contact",
                        "#db2777"
                );

        callPrimary.setOnAction(
                e -> showAlert(
                        "Calling Contact",
                        "Calling Robert Johnson..."
                )
        );

        primary.getChildren().add(
                callPrimary
        );

        VBox doctor =
                coloredCard(
                        "#dbeafe",
                        "#2563eb"
                );

        doctor.getChildren().addAll(
                cardTitle(
                        "Primary Doctor",
                        "#1e3a8a"
                ),

                contact(
                        "Dr. Sarah Jenkins",
                        "Cardiologist",
                        "+91 98765 12345"
                )
        );

        Button callDoctor =
                coloredButton(
                        "Call Doctor",
                        "#2563eb"
                );

        callDoctor.setOnAction(
                e -> showAlert(
                        "Calling Doctor",
                        "Calling Dr. Sarah Jenkins..."
                )
        );

        doctor.getChildren().add(
                callDoctor
        );

        HBox.setHgrow(
                primary,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                doctor,
                Priority.ALWAYS
        );

        contacts.getChildren().addAll(
                primary,
                doctor
        );

        /*
         * Emergency instructions
         */

        VBox instructions =
                coloredCard(
                        "#fef3c7",
                        "#d97706"
                );

        instructions.getChildren().addAll(

                cardTitle(
                        "Emergency Guidelines",
                        "#92400e"
                ),

                instruction(
                        "1",
                        "Stay calm and move to a safe location."
                ),

                instruction(
                        "2",
                        "Contact emergency services if the situation is serious."
                ),

                instruction(
                        "3",
                        "Keep your medical information and medication list available."
                ),

                instruction(
                        "4",
                        "Tell medical professionals about allergies and existing conditions."
                )
        );

        /*
         * Health information
         */

        VBox health =
                coloredCard(
                        "#ede9fe",
                        "#7c3aed"
                );

        health.getChildren().addAll(

                cardTitle(
                        "Important Medical Information",
                        "#5b21b6"
                ),

                medicalInfo(
                        "Blood Group",
                        "O+"
                ),

                medicalInfo(
                        "Allergies",
                        "Penicillin, Dust"
                ),

                medicalInfo(
                        "Conditions",
                        "Hypertension, Seasonal Asthma"
                ),

                medicalInfo(
                        "Current Medication",
                        "Amlodipine 5 mg"
                )
        );

        Button back =
                new Button(
                        "Back to Dashboard"
                );

        back.setPrefHeight(42);

        back.setStyle(
                "-fx-background-color: #e2e8f0;" +
                "-fx-text-fill: #0f172a;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 0 20;"
        );

        back.setOnAction(
                e -> stage.setScene(
                        new Dashboard(stage).getScene()
                )
        );

        content.getChildren().addAll(
                heading,
                emergency,
                contacts,
                instructions,
                health,
                back
        );

        ScrollPane scroll =
                new ScrollPane(content);

        scroll.setFitToWidth(true);

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scroll.setStyle(
                "-fx-background-color: transparent;"
        );

        root.setCenter(scroll);

        return new Scene(
                root,
                1440,
                900
        );
    }

    private VBox coloredCard(
            String background,
            String border
    ) {

        VBox box =
                new VBox(14);

        box.setPadding(
                new Insets(20)
        );

        box.setStyle(
                "-fx-background-color: " +
                        background + ";" +
                "-fx-border-color: " +
                        border + ";" +
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

    private VBox contact(
            String name,
            String relation,
            String phone
    ) {

        VBox box =
                new VBox(4);

        Label nameLabel =
                new Label(name);

        nameLabel.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;"
        );

        Label relationLabel =
                new Label(relation);

        relationLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        Label phoneLabel =
                new Label(phone);

        phoneLabel.setStyle(
                "-fx-font-weight: bold;"
        );

        box.getChildren().addAll(
                nameLabel,
                relationLabel,
                phoneLabel
        );

        return box;
    }

    private Button coloredButton(
            String text,
            String color
    ) {

        Button button =
                new Button(text);

        button.setPrefHeight(40);

        button.setStyle(
                "-fx-background-color: " +
                        color + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 0 18;"
        );

        return button;
    }

    private VBox instruction(
            String number,
            String text
    ) {

        VBox box =
                new VBox(3);

        Label numberLabel =
                new Label(number);

        numberLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #92400e;"
        );

        Label textLabel =
                new Label(text);

        textLabel.setWrapText(true);

        textLabel.setStyle(
                "-fx-text-fill: #475569;"
        );

        box.getChildren().addAll(
                numberLabel,
                textLabel
        );

        return box;
    }

    private HBox medicalInfo(
            String title,
            String value
    ) {

        HBox row =
                new HBox();

        row.setPadding(
                new Insets(6, 0, 6, 0)
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-weight: bold;"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-text-fill: #475569;"
        );

        row.getChildren().addAll(
                titleLabel,
                spacer,
                valueLabel
        );

        return row;
    }

    private HBox createHeader() {

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

        Button notifications =
                new Button(
                        "Notifications"
                );

        notifications.setOnAction(
                e -> stage.setScene(
                        new Notifications(stage).getScene()
                )
        );

        Button profile =
                new Button("Sarah");

        profile.setOnAction(
                e -> stage.setScene(
                        new ProfileSettings(stage).getScene()
                )
        );

        header.getChildren().addAll(
                spacer,
                notifications,
                profile
        );

        return header;
    }

    private VBox createSidebar() {

        VBox sidebar =
                new VBox(8);

        sidebar.setPrefWidth(255);

        sidebar.setPadding(
                new Insets(22)
        );

        sidebar.setStyle(
                "-fx-background-color: #0f172a;"
        );

        Label brand =
                new Label(
                        "✚  MediNexus AI"
                );

        brand.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 21px;" +
                "-fx-font-weight: bold;"
        );

        Label module =
                new Label(
                        "Patient Module"
                );

        module.setStyle(
                "-fx-text-fill: #94a3b8;"
        );

        Region spacer =
                new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        sidebar.getChildren().addAll(

                brand,
                module,
                new Separator(),

                nav(
                        "▦",
                        "Dashboard",
                        false,
                        () -> showDashboard()
                ),

                nav(
                        "⊞",
                        "Search Hospitals",
                        false,
                        () -> showSearchHospitals()
                ),

                nav(
                        "▣",
                        "Appointments",
                        false,
                        () -> showAppointments()
                ),

                nav(
                        "▧",
                        "Health Passport",
                        false,
                        () -> showHealthPassport()
                ),

                nav(
                        "▱",
                        "Medical Records",
                        false,
                        () -> showMedicalRecords()
                ),

                nav(
                        "♙",
                        "AI Health Assistant",
                        false,
                        () -> showAIHealthAssistant()
                ),

                nav(
                        "⌖",
                        "Emergency Assistance",
                        true,
                        () -> showEmergencyAssistance()
                ),

                spacer,

                nav(
                        "♧",
                        "Notifications",
                        false,
                        () -> showNotifications()
                ),

                nav(
                        "⚙",
                        "Profile & Settings",
                        false,
                        () -> showProfileSettings()
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

        HBox item =
                new HBox(12);

        item.setAlignment(
                Pos.CENTER_LEFT
        );

        item.setPadding(
                new Insets(12)
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
                "-fx-text-fill: white;"
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

    private void showAlert(
            String title,
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private void showDashboard() {
        stage.setScene(
                new Dashboard(stage).getScene()
        );
    }

    private void showSearchHospitals() {
        stage.setScene(
                new SearchHospitals(stage).getScene()
        );
    }

    private void showAppointments() {
        stage.setScene(
                new Appointments(stage).getScene()
        );
    }

    private void showHealthPassport() {
        stage.setScene(
                new HealthPassport(stage).getScene()
        );
    }

    private void showMedicalRecords() {
        stage.setScene(
                new MedicalRecords(stage).getScene()
        );
    }

    private void showAIHealthAssistant() {
        stage.setScene(
                new AiHealthAssistant(stage).getScene()
        );
    }

    private void showEmergencyAssistance() {
        stage.setScene(
                new EmergencyAssistance(stage).getScene()
        );
    }

    private void showNotifications() {
        stage.setScene(
                new Notifications(stage).getScene()
        );
    }

    private void showProfileSettings() {
        stage.setScene(
                new ProfileSettings(stage).getScene()
        );
    }
}