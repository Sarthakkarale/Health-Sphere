package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ProfileSettings {

    private final PatientNavigator navigator;

    public ProfileSettings(
            PatientNavigator navigator
    ) {
        this.navigator = navigator;
    }

    public Scene getScene() {

        VBox content =
                new VBox(18);

        VBox profile =
                PatientUI.card(
                        "Personal Information"
                );

        GridPane grid =
                new GridPane();

        grid.setHgap(15);
        grid.setVgap(12);

        TextField name =
                new TextField("Sarah Johnson");

        TextField email =
                new TextField("sarah@example.com");

        TextField phone =
                new TextField("+91 XXXXX XXXXX");

        TextField dateOfBirth =
                new TextField("15 March 1995");

        grid.add(
                new Label("Full Name"),
                0,
                0
        );

        grid.add(
                name,
                1,
                0
        );

        grid.add(
                new Label("Email"),
                0,
                1
        );

        grid.add(
                email,
                1,
                1
        );

        grid.add(
                new Label("Phone"),
                0,
                2
        );

        grid.add(
                phone,
                1,
                2
        );

        grid.add(
                new Label("Date of Birth"),
                0,
                3
        );

        grid.add(
                dateOfBirth,
                1,
                3
        );

        Button save =
                PatientUI.button(
                        "Save Changes",
                        () -> {}
                );

        profile.getChildren().addAll(
                grid,
                save
        );

        VBox security =
                PatientUI.card(
                        "Security"
                );

        PasswordField password =
                new PasswordField();

        password.setPromptText(
                "New password"
        );

        Button updatePassword =
                PatientUI.button(
                        "Update Password",
                        () -> {}
                );

        HBox passwordRow =
                new HBox(10);

        passwordRow.getChildren().addAll(
                password,
                updatePassword
        );

        security.getChildren().add(
                passwordRow
        );

        VBox preferences =
                PatientUI.card(
                        "Preferences"
                );

        CheckBox notifications =
                new CheckBox(
                        "Enable notifications"
                );

        notifications.setSelected(true);

        CheckBox reminders =
                new CheckBox(
                        "Appointment reminders"
                );

        reminders.setSelected(true);

        CheckBox ai =
                new CheckBox(
                        "AI health insights"
                );

        ai.setSelected(true);

        preferences.getChildren().addAll(
                notifications,
                reminders,
                ai
        );

        VBox navigation =
                PatientUI.card(
                        "Account Navigation"
                );

        HBox navigationButtons =
                new HBox(10);

        navigationButtons.getChildren().addAll(

                PatientUI.button(
                        "Health Passport",
                        navigator::showHealthPassport
                ),

                PatientUI.button(
                        "Medical Records",
                        navigator::showMedicalRecords
                ),

                PatientUI.button(
                        "Notifications",
                        navigator::showNotifications
                )
        );

        navigation.getChildren().add(
                navigationButtons
        );

        content.getChildren().addAll(
                profile,
                security,
                preferences,
                navigation
        );

        return PatientUI.createScene(
                navigator,
                "Profile & Settings",
                "Profile & Settings",
                "Manage your personal information, security and application preferences.",
                content
        );
    }
}