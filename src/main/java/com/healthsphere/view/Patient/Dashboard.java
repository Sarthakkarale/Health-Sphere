package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class Dashboard {

    private final PatientNavigator navigator;

    public Dashboard(PatientNavigator navigator) {
        this.navigator = navigator;
    }

    public Scene getScene() {

        VBox content = new VBox(20);
        content.setPadding(new Insets(4));

        // =========================================================
        // HEALTH STATISTICS
        // =========================================================

        HBox stats = new HBox(18);

        VBox heartRate = statCard(
                "♥",
                "Heart Rate",
                "72 BPM",
                "Normal",
                "#EFF6FF",
                "#2563EB"
        );

        VBox bloodPressure = statCard(
                "BP",
                "Blood Pressure",
                "118 / 76",
                "Healthy",
                "#F0FDF4",
                "#16A34A"
        );

        VBox appointmentStat = statCard(
                "◷",
                "Next Appointment",
                "Tomorrow",
                "10:00 AM",
                "#F5F3FF",
                "#7C3AED"
        );

        VBox aiInsights = statCard(
                "✦",
                "AI Insights",
                "3",
                "New",
                "#FFF7ED",
                "#EA580C"
        );

        stats.getChildren().addAll(
                heartRate,
                bloodPressure,
                appointmentStat,
                aiInsights
        );

        HBox.setHgrow(heartRate, Priority.ALWAYS);
        HBox.setHgrow(bloodPressure, Priority.ALWAYS);
        HBox.setHgrow(appointmentStat, Priority.ALWAYS);
        HBox.setHgrow(aiInsights, Priority.ALWAYS);

        // =========================================================
        // MIDDLE SECTION
        // =========================================================

        HBox middle = new HBox(18);

        // ---------------------------------------------------------
        // UPCOMING APPOINTMENT
        // ---------------------------------------------------------

        VBox appointment =
                PatientUI.card("Upcoming Appointment");

        appointment.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #DBEAFE;" +
                "-fx-border-radius: 14;" +
                "-fx-border-width: 1.2;"
        );

        Label appointmentStatus =
                new Label("UPCOMING");

        appointmentStatus.setStyle(
                "-fx-background-color: #EFF6FF;" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 5 11 5 11;" +
                "-fx-text-fill: #2563EB;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;"
        );

        Label doctor =
                new Label("Dr. Sarah Jenkins");

        doctor.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0F172A;"
        );

        Label specialty =
                PatientUI.muted("Cardiology");

        Label hospital =
                PatientUI.muted("CityCare Medical Center");

        Label time =
                new Label("Tomorrow • 10:00 AM");

        time.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #334155;"
        );

        Button appointmentButton =
                PatientUI.button(
                        "View Appointments",
                        navigator::showAppointments
                );

        Button bookButton =
                PatientUI.button(
                        "Book New Appointment",
                        navigator::showBookAppointment
                );

        HBox appointmentButtons =
                new HBox(10);

        appointmentButtons.getChildren().addAll(
                appointmentButton,
                bookButton
        );

        appointment.getChildren().addAll(
                appointmentStatus,
                doctor,
                specialty,
                hospital,
                new Separator(),
                time,
                appointmentButtons
        );

        HBox.setHgrow(
                appointment,
                Priority.ALWAYS
        );

        // ---------------------------------------------------------
        // AI HEALTH INSIGHTS
        // ---------------------------------------------------------

        VBox insights =
                PatientUI.card("AI Health Insights");

        insights.setStyle(
                "-fx-background-color: #FCF9FF;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #E9D5FF;" +
                "-fx-border-radius: 14;" +
                "-fx-border-width: 1.2;"
        );

        Label aiStatus =
                new Label("✦ AI MONITORING ACTIVE");

        aiStatus.setStyle(
                "-fx-background-color: #F3E8FF;" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 5 11 5 11;" +
                "-fx-text-fill: #7C3AED;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;"
        );

        insights.getChildren().addAll(
                aiStatus,

                insight(
                        "Elevated Blood Pressure Trend",
                        "A slight upward trend has been detected in your recent readings.",
                        "#FEF2F2",
                        "#DC2626"
                ),

                insight(
                        "Medication Review",
                        "Your medication list has been checked for possible interactions.",
                        "#FFF7ED",
                        "#EA580C"
                ),

                insight(
                        "Health Passport",
                        "Your latest medical records are ready for review.",
                        "#EFF6FF",
                        "#2563EB"
                )
        );

        HBox.setHgrow(
                insights,
                Priority.ALWAYS
        );

        middle.getChildren().addAll(
                appointment,
                insights
        );

        // =========================================================
        // QUICK ACTIONS
        // =========================================================

        VBox quickActions =
                PatientUI.card("Quick Actions");

        quickActions.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 14;"
        );

        HBox actions =
                new HBox(12);

        Button hospitals =
                PatientUI.button(
                        "Find Hospitals",
                        navigator::showSearchHospitals
                );

        Button passport =
                PatientUI.button(
                        "Health Passport",
                        navigator::showHealthPassport
                );

        Button records =
                PatientUI.button(
                        "Medical Records",
                        navigator::showMedicalRecords
                );

        Button assistant =
                PatientUI.button(
                        "AI Assistant",
                        navigator::showAIHealthAssistant
                );

        Button emergency =
                PatientUI.button(
                        "Emergency",
                        navigator::showEmergencyAssistance
                );

        emergency.setStyle(
                "-fx-background-color: #FEF2F2;" +
                "-fx-text-fill: #DC2626;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 9;" +
                "-fx-border-color: #FECACA;" +
                "-fx-border-radius: 9;"
        );

        actions.getChildren().addAll(
                hospitals,
                passport,
                records,
                assistant,
                emergency
        );

        quickActions.getChildren().add(actions);

        // =========================================================
        // ADD EVERYTHING
        // =========================================================

        content.getChildren().addAll(
                stats,
                middle,
                quickActions
        );

        return PatientUI.createScene(
                navigator,
                "Dashboard",
                "Good evening, Sarah",
                "Here is your health overview and today's important updates.",
                content
        );
    }

    // =============================================================
    // STAT CARD
    // =============================================================

    private VBox statCard(
            String icon,
            String title,
            String value,
            String status,
            String background,
            String accent
    ) {

        VBox box =
                PatientUI.card("");

        box.setMinHeight(150);

        box.setStyle(
                "-fx-background-color: " + background + ";" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: " + accent + "33;" +
                "-fx-border-radius: 14;" +
                "-fx-border-width: 1.2;"
        );

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 9;" +
                "-fx-text-fill: " + accent + ";" +
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;"
        );

        Label titleLabel =
                PatientUI.muted(title);

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-font-size: 23px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0F172A;"
        );

        Label statusLabel =
                new Label(status);

        statusLabel.setStyle(
                "-fx-text-fill: " + accent + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;"
        );

        box.getChildren().addAll(
                iconLabel,
                titleLabel,
                valueLabel,
                statusLabel
        );

        HBox.setHgrow(
                box,
                Priority.ALWAYS
        );

        return box;
    }

    // =============================================================
    // AI INSIGHT
    // =============================================================

    private VBox insight(
            String title,
            String description,
            String background,
            String accent
    ) {

        VBox box =
                new VBox(6);

        box.setPadding(
                new Insets(12)
        );

        box.setStyle(
                "-fx-background-color: " + background + ";" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: " + accent + "22;" +
                "-fx-border-radius: 10;"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1E293B;"
        );

        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setWrapText(true);

        descriptionLabel.setStyle(
                "-fx-text-fill: #64748B;" +
                "-fx-font-size: 12px;"
        );

        box.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );

        return box;
    }
}