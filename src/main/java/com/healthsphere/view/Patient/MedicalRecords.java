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

public class MedicalRecords {

    private final Stage stage;

    public MedicalRecords(Stage stage) {
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

        Label title = new Label("Medical Records");

        title.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label subtitle = new Label(
                "View and manage your medical history, reports and prescriptions."
        );

        subtitle.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #64748b;"
        );

        VBox heading = new VBox(
                5,
                title,
                subtitle
        );

        /*
         * Summary cards
         */

        HBox summary = new HBox(18);

        summary.getChildren().addAll(

                summaryCard(
                        "▣",
                        "Medical Visits",
                        "12",
                        "Total consultations",
                        "#dbeafe",
                        "#2563eb"
                ),

                summaryCard(
                        "✚",
                        "Prescriptions",
                        "8",
                        "Active prescriptions",
                        "#dcfce7",
                        "#16a34a"
                ),

                summaryCard(
                        "⌁",
                        "Lab Reports",
                        "15",
                        "Available reports",
                        "#ede9fe",
                        "#7c3aed"
                ),

                summaryCard(
                        "♥",
                        "Health Status",
                        "Stable",
                        "Current condition",
                        "#fce7f3",
                        "#db2777"
                )
        );

        /*
         * Recent records
         */

        VBox recentRecords =
                coloredCard(
                        "Recent Medical Records",
                        "#eff6ff",
                        "#2563eb"
                );

        recentRecords.getChildren().addAll(

                record(
                        "Cardiology Consultation",
                        "Dr. Sarah Jenkins",
                        "12 August 2026",
                        "Follow-up consultation",
                        "#dbeafe",
                        "#2563eb"
                ),

                new Separator(),

                record(
                        "Blood Test Report",
                        "Apollo Diagnostics",
                        "05 August 2026",
                        "Complete blood count",
                        "#dcfce7",
                        "#16a34a"
                ),

                new Separator(),

                record(
                        "Blood Pressure Check",
                        "HealthSphere Clinic",
                        "28 July 2026",
                        "Routine monitoring",
                        "#fef3c7",
                        "#d97706"
                ),

                new Separator(),

                record(
                        "Prescription Update",
                        "Dr. Sarah Jenkins",
                        "20 July 2026",
                        "Medication review",
                        "#ede9fe",
                        "#7c3aed"
                )
        );

        /*
         * Important medical information
         */

        HBox informationRow =
                new HBox(18);

        VBox allergies =
                coloredCard(
                        "Allergies",
                        "#fee2e2",
                        "#dc2626"
                );

        allergies.getChildren().addAll(

                medicalItem(
                        "Penicillin",
                        "Medication allergy"
                ),

                medicalItem(
                        "Dust",
                        "Environmental allergy"
                )
        );

        VBox medications =
                coloredCard(
                        "Current Medications",
                        "#dcfce7",
                        "#16a34a"
                );

        medications.getChildren().addAll(

                medicalItem(
                        "Amlodipine",
                        "5 mg • Once daily"
                ),

                medicalItem(
                        "Vitamin D3",
                        "Once weekly"
                )
        );

        VBox conditions =
                coloredCard(
                        "Medical Conditions",
                        "#fef3c7",
                        "#d97706"
                );

        conditions.getChildren().addAll(

                medicalItem(
                        "Hypertension",
                        "Under monitoring"
                ),

                medicalItem(
                        "Seasonal Asthma",
                        "Mild condition"
                )
        );

        HBox.setHgrow(
                allergies,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                medications,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                conditions,
                Priority.ALWAYS
        );

        informationRow.getChildren().addAll(
                allergies,
                medications,
                conditions
        );

        /*
         * Buttons
         */

        HBox buttons = new HBox(12);

        Button passportButton =
                styledButton(
                        "View Health Passport",
                        "#2563eb"
                );

        passportButton.setOnAction(
                e -> stage.setScene(
                        new HealthPassport(stage).getScene()
                )
        );

        Button backButton =
                secondaryButton("Back to Dashboard");

        backButton.setOnAction(
                e -> stage.setScene(
                        new Dashboard(stage).getScene()
                )
        );

        buttons.getChildren().addAll(
                passportButton,
                backButton
        );

        content.getChildren().addAll(
                heading,
                summary,
                recentRecords,
                informationRow,
                buttons
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

    private VBox summaryCard(
            String icon,
            String title,
            String value,
            String description,
            String background,
            String accent
    ) {

        VBox box =
                coloredCard(
                        "",
                        background,
                        accent
                );

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-font-size: 25px;" +
                "-fx-text-fill: " + accent + ";"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #64748b;"
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-font-size: 23px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #64748b;"
        );

        box.getChildren().addAll(
                iconLabel,
                titleLabel,
                valueLabel,
                descriptionLabel
        );

        HBox.setHgrow(
                box,
                Priority.ALWAYS
        );

        return box;
    }

    private HBox record(
            String title,
            String doctor,
            String date,
            String description,
            String background,
            String accent
    ) {

        HBox row =
                new HBox(15);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox iconBox =
                new VBox();

        iconBox.setAlignment(
                Pos.CENTER
        );

        iconBox.setPrefSize(
                48,
                48
        );

        iconBox.setStyle(
                "-fx-background-color: " +
                        background + ";" +
                "-fx-background-radius: 10;"
        );

        Label icon =
                new Label("▣");

        icon.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-text-fill: " +
                        accent + ";"
        );

        iconBox.getChildren().add(icon);

        VBox details =
                new VBox(4);

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;"
        );

        Label doctorLabel =
                new Label(doctor);

        doctorLabel.setStyle(
                "-fx-text-fill: #475569;"
        );

        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #64748b;"
        );

        details.getChildren().addAll(
                titleLabel,
                doctorLabel,
                descriptionLabel
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label dateLabel =
                new Label(date);

        dateLabel.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 13px;"
        );

        Button viewButton =
                styledButton(
                        "View",
                        accent
                );

        viewButton.setOnAction(
                e -> showMessage(
                        "Record Selected",
                        title
                )
        );

        row.getChildren().addAll(
                iconBox,
                details,
                spacer,
                dateLabel,
                viewButton
        );

        return row;
    }

    private VBox medicalItem(
            String title,
            String description
    ) {

        VBox box =
                new VBox(4);

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-font-size: 15px;"
        );

        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 13px;"
        );

        box.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );

        return box;
    }

    private VBox coloredCard(
            String title,
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

        if (!title.isEmpty()) {

            Label label =
                    new Label(title);

            label.setStyle(
                    "-fx-font-size: 19px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: #0f172a;"
            );

            box.getChildren().add(label);
        }

        return box;
    }

    private Button styledButton(
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

    private Button secondaryButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setPrefHeight(40);

        button.setStyle(
                "-fx-background-color: #e2e8f0;" +
                "-fx-text-fill: #0f172a;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 0 18;"
        );

        return button;
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

        Button notification =
                new Button("Notifications");

        notification.setOnAction(
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
                notification,
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
                        () -> go(
                                new Dashboard(stage)
                        )
                ),

                nav(
                        "⊞",
                        "Search Hospitals",
                        false,
                        () -> go(
                                new SearchHospitals(stage)
                        )
                ),

                nav(
                        "▣",
                        "Appointments",
                        false,
                        () -> go(
                                new Appointments(stage)
                        )
                ),

                nav(
                        "▧",
                        "Health Passport",
                        false,
                        () -> go(
                                new HealthPassport(stage)
                        )
                ),

                nav(
                        "▱",
                        "Medical Records",
                        true,
                        () -> go(
                                new MedicalRecords(stage)
                        )
                ),

                nav(
                        "♙",
                        "AI Health Assistant",
                        false,
                        () -> go(
                                new AiHealthAssistant(stage)
                        )
                ),

                nav(
                        "⌖",
                        "Emergency Assistance",
                        false,
                        () -> go(
                                new EmergencyAssistance(stage)
                        )
                ),

                spacer,

                nav(
                        "♧",
                        "Notifications",
                        false,
                        () -> go(
                                new Notifications(stage)
                        )
                ),

                nav(
                        "⚙",
                        "Profile & Settings",
                        false,
                        () -> go(
                                new ProfileSettings(stage)
                        )
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

    private void go(Object view) {

        if (view instanceof Dashboard)
            stage.setScene(
                    ((Dashboard) view).getScene()
            );

        else if (view instanceof SearchHospitals)
            stage.setScene(
                    ((SearchHospitals) view).getScene()
            );

        else if (view instanceof Appointments)
            stage.setScene(
                    ((Appointments) view).getScene()
            );

        else if (view instanceof HealthPassport)
            stage.setScene(
                    ((HealthPassport) view).getScene()
            );

        else if (view instanceof MedicalRecords)
            stage.setScene(
                    ((MedicalRecords) view).getScene()
            );

        else if (view instanceof AiHealthAssistant)
            stage.setScene(
                    ((AiHealthAssistant) view).getScene()
            );

        else if (view instanceof EmergencyAssistance)
            stage.setScene(
                    ((EmergencyAssistance) view).getScene()
            );

        else if (view instanceof Notifications)
            stage.setScene(
                    ((Notifications) view).getScene()
            );

        else if (view instanceof ProfileSettings)
            stage.setScene(
                    ((ProfileSettings) view).getScene()
            );

        stage.show();
    }

    private void showMessage(
            String title,
            String message
    ) {

        javafx.scene.control.Alert alert =
                new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.INFORMATION
                );

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}