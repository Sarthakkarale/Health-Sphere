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

public class Appointments {

    private final PatientNavigator navigator;

    public Appointments(PatientNavigator navigator) {
        this.navigator = navigator;
    }

    public Scene getScene() {

        BorderPane root = createRoot();

        VBox content = new VBox(22);
        content.setPadding(new Insets(28));
        content.setStyle("-fx-background-color: #f8fafc;");

        // =========================================================
        // PAGE HEADER
        // =========================================================

        Label title = new Label("Appointments");

        title.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;"
        );

        Label subtitle = new Label(
                "Manage your upcoming appointments and book new consultations."
        );

        subtitle.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #64748b;"
        );

        VBox titleBox = new VBox(
                5,
                title,
                subtitle
        );

        Button bookButton =
                new Button("+ Book Appointment");

        bookButton.setPrefHeight(42);

        bookButton.setPadding(
                new Insets(0, 20, 0, 20)
        );

        bookButton.setStyle(
                "-fx-background-color: #2563eb;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 7;"
        );

        bookButton.setOnAction(
                e -> navigator.showBookAppointment()
        );

        Region headingSpacer = new Region();

        HBox.setHgrow(
                headingSpacer,
                Priority.ALWAYS
        );

        HBox heading = new HBox(
                15,
                titleBox,
                headingSpacer,
                bookButton
        );

        heading.setAlignment(
                Pos.CENTER_LEFT
        );

        // =========================================================
        // SUMMARY CARDS
        // =========================================================

        HBox summary = new HBox(18);

        summary.getChildren().addAll(
                summaryCard(
                        "Upcoming",
                        "2",
                        "Appointments"
                ),

                summaryCard(
                        "Completed",
                        "12",
                        "Appointments"
                ),

                summaryCard(
                        "Next Visit",
                        "Tomorrow",
                        "10:00 AM"
                ),

                summaryCard(
                        "Doctors",
                        "4",
                        "Connected"
                )
        );

        // =========================================================
        // UPCOMING APPOINTMENTS
        // =========================================================

        VBox upcomingCard =
                createCard("Upcoming Appointments");

        VBox appointment1 =
                appointmentCard(
                        "Dr. Sarah Jenkins",
                        "Cardiology",
                        "Tomorrow",
                        "10:00 AM",
                        "City Heart & Medical Center",
                        true
                );

        VBox appointment2 =
                appointmentCard(
                        "Dr. Emily Wilson",
                        "General Medicine",
                        "28 August 2026",
                        "02:30 PM",
                        "MediNexus Medical Center",
                        true
                );

        upcomingCard.getChildren().addAll(
                appointment1,
                new Separator(),
                appointment2
        );

        // =========================================================
        // PAST APPOINTMENTS
        // =========================================================

        VBox pastCard =
                createCard("Recent Appointments");

        VBox past1 =
                appointmentCard(
                        "Dr. Michael Anderson",
                        "Neurology",
                        "05 August 2026",
                        "11:00 AM",
                        "MediNexus Medical Center",
                        false
                );

        VBox past2 =
                appointmentCard(
                        "Dr. James Carter",
                        "Orthopedics",
                        "22 July 2026",
                        "03:00 PM",
                        "City General Hospital",
                        false
                );

        pastCard.getChildren().addAll(
                past1,
                new Separator(),
                past2
        );

        // =========================================================
        // QUICK ACTIONS
        // =========================================================

        VBox quickActions =
                createCard("Quick Actions");

        HBox actionButtons =
                new HBox(12);

        Button findDoctor =
                actionButton(
                        "Find Hospitals",
                        navigator::showSearchHospitals
                );

        Button healthPassport =
                actionButton(
                        "Health Passport",
                        navigator::showHealthPassport
                );

        Button medicalRecords =
                actionButton(
                        "Medical Records",
                        navigator::showMedicalRecords
                );

        Button aiAssistant =
                actionButton(
                        "AI Assistant",
                        navigator::showAIHealthAssistant
                );

        actionButtons.getChildren().addAll(
                findDoctor,
                healthPassport,
                medicalRecords,
                aiAssistant
        );

        quickActions.getChildren().add(
                actionButtons
        );

        // =========================================================
        // ADD CONTENT
        // =========================================================

        content.getChildren().addAll(
                heading,
                summary,
                upcomingCard,
                pastCard,
                quickActions
        );

        ScrollPane scroll =
                new ScrollPane(content);

        scroll.setFitToWidth(true);

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scroll.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scroll.setStyle(
                "-fx-background-color: #f8fafc;"
        );

        root.setCenter(scroll);

        return new Scene(
                root,
                1440,
                900
        );
    }

    // =============================================================
    // ROOT
    // =============================================================

    private BorderPane createRoot() {

        BorderPane root =
                new BorderPane();

        root.setLeft(
                createSidebar()
        );

        root.setTop(
                createHeader()
        );

        return root;
    }

    // =============================================================
    // SIDEBAR
    // =============================================================

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

        Separator separator =
                new Separator();

        sidebar.getChildren().addAll(
                brand,
                module,
                separator,

                nav(
                        "▦",
                        "Dashboard",
                        false,
                        navigator::showDashboard
                ),

                nav(
                        "⊞",
                        "Search Hospitals",
                        false,
                        navigator::showSearchHospitals
                ),

                nav(
                        "▣",
                        "Appointments",
                        true,
                        navigator::showAppointments
                ),

                nav(
                        "▧",
                        "Health Passport",
                        false,
                        navigator::showHealthPassport
                ),

                nav(
                        "▱",
                        "Medical Records",
                        false,
                        navigator::showMedicalRecords
                ),

                nav(
                        "♙",
                        "AI Health Assistant",
                        false,
                        navigator::showAIHealthAssistant
                ),

                nav(
                        "⌖",
                        "Emergency Assistance",
                        false,
                        navigator::showEmergencyAssistance
                )
        );

        Region spacer =
                new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        sidebar.getChildren().addAll(
                spacer,

                nav(
                        "♧",
                        "Notifications",
                        false,
                        navigator::showNotifications
                ),

                nav(
                        "⚙",
                        "Profile & Settings",
                        false,
                        navigator::showProfileSettings
                )
        );

        return sidebar;
    }

    // =============================================================
    // NAVIGATION ITEM
    // =============================================================

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

        item.setMaxWidth(
                Double.MAX_VALUE
        );

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
                e -> action.run()
        );

        return item;
    }

    // =============================================================
    // HEADER
    // =============================================================

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

        Label notificationIcon =
                new Label("♧");

        notificationIcon.setStyle(
                "-fx-font-size: 22px;"
        );

        Button notifications =
                new Button("Notifications");

        notifications.setOnAction(
                e -> navigator.showNotifications()
        );

        Button profile =
                new Button("Sarah");

        profile.setOnAction(
                e -> navigator.showProfileSettings()
        );

        header.getChildren().addAll(
                spacer,
                notificationIcon,
                notifications,
                profile
        );

        return header;
    }

    // =============================================================
    // SUMMARY CARD
    // =============================================================

    private VBox summaryCard(
            String title,
            String value,
            String subtitle
    ) {

        VBox box =
                new VBox(8);

        box.setPadding(
                new Insets(20)
        );

        box.setPrefHeight(125);

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #e2e8f0;" +
                "-fx-border-radius: 12;"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 14px;"
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-font-size: 23px;" +
                "-fx-font-weight: bold;"
        );

        Label subtitleLabel =
                new Label(subtitle);

        subtitleLabel.setStyle(
                "-fx-text-fill: #16a34a;" +
                "-fx-font-size: 13px;"
        );

        box.getChildren().addAll(
                titleLabel,
                valueLabel,
                subtitleLabel
        );

        HBox.setHgrow(
                box,
                Priority.ALWAYS
        );

        return box;
    }

    // =============================================================
    // APPOINTMENT CARD
    // =============================================================

    private VBox appointmentCard(
            String doctor,
            String specialty,
            String date,
            String time,
            String hospital,
            boolean upcoming
    ) {

        VBox box =
                new VBox(10);

        box.setPadding(
                new Insets(8, 4, 8, 4)
        );

        // Doctor row

        Label doctorLabel =
                new Label(doctor);

        doctorLabel.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;"
        );

        Label specialtyLabel =
                new Label(specialty);

        specialtyLabel.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 14px;"
        );

        VBox doctorInfo =
                new VBox(
                        3,
                        doctorLabel,
                        specialtyLabel
                );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label status =
                new Label(
                        upcoming
                                ? "Upcoming"
                                : "Completed"
                );

        status.setPadding(
                new Insets(6, 12, 6, 12)
        );

        status.setStyle(
                upcoming
                        ? "-fx-background-color: #dcfce7;" +
                          "-fx-text-fill: #15803d;" +
                          "-fx-background-radius: 15;"
                        : "-fx-background-color: #f1f5f9;" +
                          "-fx-text-fill: #64748b;" +
                          "-fx-background-radius: 15;"
        );

        HBox topRow =
                new HBox(
                        doctorInfo,
                        spacer,
                        status
                );

        topRow.setAlignment(
                Pos.CENTER_LEFT
        );

        // Appointment information

        Label dateLabel =
                new Label("▣  " + date);

        dateLabel.setStyle(
                "-fx-font-size: 14px;"
        );

        Label timeLabel =
                new Label("◷  " + time);

        timeLabel.setStyle(
                "-fx-font-size: 14px;"
        );

        Label hospitalLabel =
                new Label("⌖  " + hospital);

        hospitalLabel.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 14px;"
        );

        HBox details =
                new HBox(
                        25,
                        dateLabel,
                        timeLabel
                );

        // Buttons

        HBox buttons =
                new HBox(10);

        if (upcoming) {

            Button view =
                    new Button("View Details");

            view.setOnAction(
                    e -> showAppointmentDetails(
                            doctor,
                            specialty,
                            date,
                            time,
                            hospital
                    )
            );

            Button cancel =
                    new Button("Cancel");

            cancel.setStyle(
                    "-fx-text-fill: #dc2626;"
            );

            cancel.setOnAction(
                    e -> cancelAppointment(doctor)
            );

            buttons.getChildren().addAll(
                    view,
                    cancel
            );
        }

        box.getChildren().addAll(
                topRow,
                details,
                hospitalLabel,
                buttons
        );

        return box;
    }

    // =============================================================
    // QUICK ACTION BUTTON
    // =============================================================

    private Button actionButton(
            String text,
            Runnable action
    ) {

        Button button =
                new Button(text);

        button.setPrefHeight(42);

        button.setPadding(
                new Insets(0, 18, 0, 18)
        );

        button.setOnAction(
                e -> action.run()
        );

        return button;
    }

    // =============================================================
    // CARD
    // =============================================================

    private VBox createCard(
            String title
    ) {

        VBox card =
                new VBox(15);

        card.setPadding(
                new Insets(20)
        );

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #e2e8f0;" +
                "-fx-border-radius: 12;"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;"
        );

        card.getChildren().add(
                titleLabel
        );

        return card;
    }

    // =============================================================
    // VIEW APPOINTMENT DETAILS
    // =============================================================

    private void showAppointmentDetails(
            String doctor,
            String specialty,
            String date,
            String time,
            String hospital
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(
                "Appointment Details"
        );

        alert.setHeaderText(
                doctor
        );

        alert.setContentText(
                "Specialty: " + specialty +
                "\n\n" +
                "Date: " + date +
                "\n" +
                "Time: " + time +
                "\n\n" +
                "Hospital: " + hospital
        );

        alert.showAndWait();
    }

    // =============================================================
    // CANCEL APPOINTMENT
    // =============================================================

    private void cancelAppointment(
            String doctor
    ) {

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Cancel Appointment"
        );

        confirmation.setHeaderText(
                "Cancel appointment with " + doctor + "?"
        );

        confirmation.setContentText(
                "Are you sure you want to cancel this appointment?"
        );

        confirmation.showAndWait()
                .ifPresent(response -> {

                    if (response ==
                            javafx.scene.control.ButtonType.OK) {

                        Alert success =
                                new Alert(
                                        Alert.AlertType.INFORMATION
                                );

                        success.setTitle(
                                "Appointment Cancelled"
                        );

                        success.setHeaderText(
                                null
                        );

                        success.setContentText(
                                "The appointment has been cancelled."
                        );

                        success.showAndWait();
                    }
                });
    }
}