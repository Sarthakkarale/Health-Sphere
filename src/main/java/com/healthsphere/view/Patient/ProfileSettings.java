package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProfileSettings {

    private final Stage stage;

    public ProfileSettings(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {

        BorderPane root = new BorderPane();

        root.setLeft(createSidebar());
        root.setTop(createHeader());

        VBox content = new VBox(20);

        content.setPadding(
                new Insets(28)
        );

        content.setStyle(
                "-fx-background-color: #f8fafc;"
        );

        Label title =
                new Label("Profile & Settings");

        title.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label subtitle =
                new Label(
                        "Manage your personal information, preferences and account settings."
                );

        subtitle.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #64748b;"
        );

        VBox heading =
                new VBox(5, title, subtitle);

        // =====================================================
        // PERSONAL INFORMATION
        // =====================================================

        VBox personal =
                coloredCard(
                        "#eff6ff",
                        "#2563eb",
                        "Personal Information"
                );

        HBox nameRow =
                new HBox(15);

        VBox firstName =
                field(
                        "First Name",
                        "Sarah"
                );

        VBox lastName =
                field(
                        "Last Name",
                        "Johnson"
                );

        HBox.setHgrow(
                firstName,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                lastName,
                Priority.ALWAYS
        );

        nameRow.getChildren().addAll(
                firstName,
                lastName
        );

        VBox email =
                field(
                        "Email Address",
                        "sarah.johnson@email.com"
                );

        VBox phone =
                field(
                        "Phone Number",
                        "+91 98765 43210"
                );

        personal.getChildren().addAll(
                nameRow,
                email,
                phone
        );

        // =====================================================
        // EMERGENCY CONTACT
        // =====================================================

        VBox emergency =
                coloredCard(
                        "#fef2f2",
                        "#dc2626",
                        "Emergency Contact"
                );

        VBox emergencyName =
                field(
                        "Emergency Contact Name",
                        "Michael Johnson"
                );

        VBox emergencyPhone =
                field(
                        "Emergency Contact Phone",
                        "+91 98765 12345"
                );

        VBox relationship =
                field(
                        "Relationship",
                        "Spouse"
                );

        emergency.getChildren().addAll(
                emergencyName,
                emergencyPhone,
                relationship
        );

        // =====================================================
        // HEALTH PREFERENCES
        // =====================================================

        VBox preferences =
                coloredCard(
                        "#f0fdf4",
                        "#16a34a",
                        "Health Preferences"
                );

        CheckBox appointmentReminder =
                new CheckBox(
                        "Receive appointment reminders"
                );

        appointmentReminder.setSelected(true);

        CheckBox healthNotifications =
                new CheckBox(
                        "Receive health notifications"
                );

        healthNotifications.setSelected(true);

        CheckBox aiInsights =
                new CheckBox(
                        "Receive AI health insights"
                );

        aiInsights.setSelected(true);

        CheckBox emergencyAlerts =
                new CheckBox(
                        "Receive emergency alerts"
                );

        emergencyAlerts.setSelected(true);

        preferences.getChildren().addAll(
                appointmentReminder,
                healthNotifications,
                aiInsights,
                emergencyAlerts
        );

        // =====================================================
        // SECURITY
        // =====================================================

        VBox security =
                coloredCard(
                        "#f3e8ff",
                        "#9333ea",
                        "Security"
                );

        Button changePassword =
                new Button("Change Password");

        changePassword.setStyle(
                "-fx-background-color: #9333ea;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10 18 10 18;"
        );

        Button logout =
                new Button("Logout");

        logout.setStyle(
                "-fx-background-color: #dc2626;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10 18 10 18;"
        );

        logout.setOnAction(
                e -> showDashboard()
        );

        security.getChildren().addAll(
                changePassword,
                logout
        );

        // =====================================================
        // SAVE BUTTON
        // =====================================================

        HBox buttons =
                new HBox(12);

        buttons.setAlignment(
                Pos.CENTER_RIGHT
        );

        Button save =
                new Button("Save Changes");

        save.setStyle(
                "-fx-background-color: #2563eb;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 12 24 12 24;"
        );

        save.setOnAction(
                e -> showSavedMessage()
        );

        Button cancel =
                new Button("Cancel");

        cancel.setStyle(
                "-fx-background-color: #e2e8f0;" +
                "-fx-text-fill: #334155;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 12 24 12 24;"
        );

        cancel.setOnAction(
                e -> showDashboard()
        );

        buttons.getChildren().addAll(
                cancel,
                save
        );

        content.getChildren().addAll(
                heading,
                personal,
                emergency,
                preferences,
                security,
                buttons
        );

        ScrollPane scroll =
                new ScrollPane(content);

        scroll.setFitToWidth(true);

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        root.setCenter(scroll);

        return new Scene(
                root,
                1440,
                900
        );
    }

    // =========================================================
    // FIELD
    // =========================================================

    private VBox field(
            String label,
            String value
    ) {

        VBox box = new VBox(6);

        Label fieldLabel =
                new Label(label);

        fieldLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #334155;"
        );

        TextField field =
                new TextField(value);

        field.setPrefHeight(40);

        box.getChildren().addAll(
                fieldLabel,
                field
        );

        VBox.setVgrow(
                box,
                Priority.ALWAYS
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

        VBox card =
                new VBox(14);

        card.setPadding(
                new Insets(20)
        );

        card.setStyle(
                "-fx-background-color: " +
                background +
                ";" +

                "-fx-border-color: " +
                accent +
                ";" +

                "-fx-border-width: 0 0 0 5;" +

                "-fx-background-radius: 12;" +
                "-fx-border-radius: 12;"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " +
                accent +
                ";"
        );

        card.getChildren().add(
                titleLabel
        );

        return card;
    }

    // =========================================================
    // HEADER
    // =========================================================

    private HBox createHeader() {

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_RIGHT
        );

        header.setPadding(
                new Insets(
                        15,
                        28,
                        15,
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

        Label icon =
                new Label("⚙");

        icon.setStyle(
                "-fx-font-size: 22px;"
        );

        Button notifications =
                new Button("Notifications");

        notifications.setOnAction(
                e -> showNotifications()
        );

        Button dashboard =
                new Button("Dashboard");

        dashboard.setOnAction(
                e -> showDashboard()
        );

        header.getChildren().addAll(
                spacer,
                icon,
                notifications,
                dashboard
        );

        return header;
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

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
                        false,
                        this::showNotifications
                ),

                nav(
                        "⚙",
                        "Profile & Settings",
                        true,
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
                        ? "#2563eb"
                        : "transparent") +
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

    // =========================================================
    // SAVE MESSAGE
    // =========================================================

    private void showSavedMessage() {

        // For now, return to the profile screen.
        // Replace this later with database persistence.

        stage.setScene(
                new ProfileSettings(stage).getScene()
        );

        stage.show();
    }
}