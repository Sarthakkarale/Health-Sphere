package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Appointments {

    private final Stage stage;

    public Appointments(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {

        BorderPane root = new BorderPane();

        root.setLeft(createSidebar());
        root.setTop(createHeader());

        VBox content = new VBox(22);
        content.setPadding(new Insets(28));
        content.setStyle("-fx-background-color: #f8fafc;");

        // ---------------------------------------------------------
        // PAGE HEADING
        // ---------------------------------------------------------

        Label title = new Label("Appointments");

        title.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label subtitle = new Label(
                "Manage your upcoming and previous healthcare appointments."
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

        // ---------------------------------------------------------
        // BOOK APPOINTMENT CARD
        // ---------------------------------------------------------

        VBox bookingCard = createCard(
                "#eff6ff",
                "#bfdbfe"
        );

        Label bookingTitle = new Label(
                "Book a New Appointment"
        );

        bookingTitle.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1e3a8a;"
        );

        Label bookingDescription = new Label(
                "Find a doctor and schedule a consultation that works for you."
        );

        bookingDescription.setStyle(
                "-fx-text-fill: #475569;"
        );

        Button bookButton = new Button(
                "Book Appointment"
        );

        bookButton.setPrefHeight(42);

        bookButton.setStyle(
                "-fx-background-color: #2563eb;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 0 22;"
        );

        bookButton.setOnAction(e -> {

            stage.setScene(
                    new BookAppointment(stage).getScene()
            );

            stage.show();
        });

        bookingCard.getChildren().addAll(
                bookingTitle,
                bookingDescription,
                bookButton
        );

        // ---------------------------------------------------------
        // UPCOMING APPOINTMENT
        // ---------------------------------------------------------

        VBox upcomingCard = createCard(
                "#f0fdf4",
                "#bbf7d0"
        );

        Label upcomingTitle = new Label(
                "Upcoming Appointment"
        );

        upcomingTitle.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #166534;"
        );

        VBox doctorBox = new VBox(5);

        Label doctor = new Label(
                "Dr. Sarah Jenkins"
        );

        doctor.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label specialty = new Label(
                "Cardiology"
        );

        specialty.setStyle(
                "-fx-text-fill: #64748b;"
        );

        doctorBox.getChildren().addAll(
                doctor,
                specialty
        );

        Separator separator = new Separator();

        Label date = new Label(
                "📅 Tomorrow"
        );

        date.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;"
        );

        Label time = new Label(
                "🕐 10:00 AM"
        );

        time.setStyle(
                "-fx-font-size: 15px;"
        );

        Label location = new Label(
                "📍 Apollo Hospitals"
        );

        location.setStyle(
                "-fx-text-fill: #64748b;"
        );

        HBox appointmentInfo = new HBox(
                25,
                date,
                time
        );

        Button cancelButton = new Button(
                "Cancel Appointment"
        );

        cancelButton.setStyle(
                "-fx-background-color: white;" +
                "-fx-text-fill: #dc2626;" +
                "-fx-font-weight: bold;" +
                "-fx-border-color: #fca5a5;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        cancelButton.setOnAction(e -> {

            System.out.println(
                    "Appointment cancellation requested."
            );

        });

        upcomingCard.getChildren().addAll(
                upcomingTitle,
                doctorBox,
                separator,
                appointmentInfo,
                location,
                cancelButton
        );

        // ---------------------------------------------------------
        // PREVIOUS APPOINTMENTS
        // ---------------------------------------------------------

        VBox previousCard = createCard(
                "#f5f3ff",
                "#ddd6fe"
        );

        Label previousTitle = new Label(
                "Previous Appointments"
        );

        previousTitle.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #5b21b6;"
        );

        previousCard.getChildren().add(
                previousTitle
        );

        previousCard.getChildren().addAll(

                previousAppointment(
                        "Dr. Michael Brown",
                        "General Medicine",
                        "12 July 2026",
                        "Completed"
                ),

                previousAppointment(
                        "Dr. Emily Wilson",
                        "Dermatology",
                        "28 June 2026",
                        "Completed"
                ),

                previousAppointment(
                        "Dr. Robert Smith",
                        "Orthopedics",
                        "15 May 2026",
                        "Completed"
                )
        );

        // ---------------------------------------------------------
        // ADD TO CONTENT
        // ---------------------------------------------------------

        content.getChildren().addAll(
                heading,
                bookingCard,
                upcomingCard,
                previousCard
        );

        root.setCenter(content);

        return new Scene(
                root,
                1440,
                900
        );
    }

    // =============================================================
    // PREVIOUS APPOINTMENT
    // =============================================================

    private HBox previousAppointment(
            String doctorName,
            String specialty,
            String date,
            String status
    ) {

        HBox row = new HBox(15);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(15)
        );

        row.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #e2e8f0;" +
                "-fx-border-radius: 10;"
        );

        VBox information = new VBox(4);

        Label doctor = new Label(
                doctorName
        );

        doctor.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;"
        );

        Label specialtyLabel = new Label(
                specialty
        );

        specialtyLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        information.getChildren().addAll(
                doctor,
                specialtyLabel
        );

        HBox.setHgrow(
                information,
                Priority.ALWAYS
        );

        Label dateLabel = new Label(
                date
        );

        dateLabel.setStyle(
                "-fx-text-fill: #475569;"
        );

        Label statusLabel = new Label(
                status
        );

        statusLabel.setStyle(
                "-fx-background-color: #dcfce7;" +
                "-fx-text-fill: #166534;" +
                "-fx-padding: 6 12;" +
                "-fx-background-radius: 15;" +
                "-fx-font-weight: bold;"
        );

        row.getChildren().addAll(
                information,
                dateLabel,
                statusLabel
        );

        return row;
    }

    // =============================================================
    // COMMON CARD
    // =============================================================

    private VBox createCard(
            String background,
            String border
    ) {

        VBox card = new VBox(12);

        card.setPadding(
                new Insets(20)
        );

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

        header.setAlignment(
                Pos.CENTER_RIGHT
        );

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
                        () -> navigate(
                                new Dashboard(stage)
                        )
                ),

                navButton(
                        "⊞",
                        "Search Hospitals",
                        () -> navigate(
                                new SearchHospitals(stage)
                        )
                ),

                navButton(
                        "▣",
                        "Appointments",
                        () -> {}
                ),

                navButton(
                        "▧",
                        "Health Passport",
                        () -> navigate(
                                new HealthPassport(stage)
                        )
                ),

                navButton(
                        "▱",
                        "Medical Records",
                        () -> navigate(
                                new MedicalRecords(stage)
                        )
                ),

                navButton(
                        "♙",
                        "AI Health Assistant",
                        () -> navigate(
                                new AiHealthAssistant(stage)
                        )
                ),

                navButton(
                        "⌖",
                        "Emergency Assistance",
                        () -> navigate(
                                new EmergencyAssistance(stage)
                        )
                ),

                spacer,

                navButton(
                        "♧",
                        "Notifications",
                        () -> navigate(
                                new Notifications(stage)
                        )
                ),

                navButton(
                        "⚙",
                        "Profile & Settings",
                        () -> navigate(
                                new ProfileSettings(stage)
                        )
                )
        );

        return sidebar;
    }

    // =============================================================
    // NAVIGATION HELPER
    // =============================================================

    private void navigate(Object view) {

        if (view instanceof Dashboard) {
            stage.setScene(
                    ((Dashboard) view).getScene()
            );
        }

        else if (view instanceof SearchHospitals) {
            stage.setScene(
                    ((SearchHospitals) view).getScene()
            );
        }

        else if (view instanceof HealthPassport) {
            stage.setScene(
                    ((HealthPassport) view).getScene()
            );
        }

        else if (view instanceof MedicalRecords) {
            stage.setScene(
                    ((MedicalRecords) view).getScene()
            );
        }

        else if (view instanceof AiHealthAssistant) {
            stage.setScene(
                    ((AiHealthAssistant) view).getScene()
            );
        }

        else if (view instanceof EmergencyAssistance) {
            stage.setScene(
                    ((EmergencyAssistance) view).getScene()
            );
        }

        else if (view instanceof Notifications) {
            stage.setScene(
                    ((Notifications) view).getScene()
            );
        }

        else if (view instanceof ProfileSettings) {
            stage.setScene(
                    ((ProfileSettings) view).getScene()
            );
        }

        stage.show();
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