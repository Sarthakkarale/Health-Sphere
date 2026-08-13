package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Dashboard {

    private final Stage stage;

    public Dashboard(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {

        VBox content = new VBox(20);

        // =====================================================
        // HEALTH STATISTICS
        // =====================================================

        HBox stats = new HBox(18);

        VBox heartRate = PatientUI.statCard(
                "♥",
                "Heart Rate",
                "72 BPM",
                "Normal",
                "#eff6ff",
                "#bfdbfe"
        );

        VBox bloodPressure = PatientUI.statCard(
                "BP",
                "Blood Pressure",
                "118 / 76",
                "Healthy",
                "#f0fdf4",
                "#bbf7d0"
        );

        VBox appointmentStat = PatientUI.statCard(
                "◷",
                "Next Appointment",
                "Tomorrow",
                "10:00 AM",
                "#fff7ed",
                "#fed7aa"
        );

        VBox aiStat = PatientUI.statCard(
                "✦",
                "AI Insights",
                "3",
                "New",
                "#faf5ff",
                "#e9d5ff"
        );

        stats.getChildren().addAll(
                heartRate,
                bloodPressure,
                appointmentStat,
                aiStat
        );

        // =====================================================
        // MIDDLE SECTION
        // =====================================================

        HBox middle = new HBox(18);

        // -----------------------------------------------------
        // UPCOMING APPOINTMENT
        // -----------------------------------------------------

        VBox appointment =
                PatientUI.orangeCard(
                        "Upcoming Appointment"
                );

        Label doctor =
                new Label(
                        "Dr. Sarah Jenkins"
                );

        doctor.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label specialty =
                PatientUI.muted(
                        "Cardiology"
                );

        Label date =
                new Label(
                        "Tomorrow"
                );

        date.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #c2410c;"
        );

        Label time =
                new Label(
                        "10:00 AM"
                );

        time.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #475569;"
        );

        Label hospital =
                PatientUI.muted(
                        "MediNexus Cardiology Center"
                );

        Button viewAppointments =
                PatientUI.orangeButton(
                        "View Appointments",
                        () -> stage.setScene(
                                new Appointments(stage).getScene()
                        )
                );

        appointment.getChildren().addAll(
                doctor,
                specialty,
                new Separator(),
                date,
                time,
                hospital,
                viewAppointments
        );

        // -----------------------------------------------------
        // AI INSIGHTS
        // -----------------------------------------------------

        VBox insights =
                PatientUI.purpleCard(
                        "AI Health Insights"
                );

        insights.getChildren().addAll(

                insight(
                        "Elevated Blood Pressure Trend",
                        "A slight upward trend has been detected in your recent readings."
                ),

                insight(
                        "Medication Review",
                        "Your medication list has been checked for possible interactions."
                ),

                insight(
                        "Health Passport",
                        "Your latest medical records are ready for review."
                )
        );

        HBox.setHgrow(
                appointment,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                insights,
                Priority.ALWAYS
        );

        middle.getChildren().addAll(
                appointment,
                insights
        );

        // =====================================================
        // QUICK ACTIONS
        // =====================================================

        VBox quickActions =
                PatientUI.blueCard(
                        "Quick Actions"
                );

        HBox actions =
                new HBox(12);

        Button hospitals =
                PatientUI.button(
                        "Find Hospitals",
                        () -> stage.setScene(
                                new SearchHospitals(stage).getScene()
                        )
                );

        Button passport =
                PatientUI.button(
                        "Health Passport",
                        () -> stage.setScene(
                                new HealthPassport(stage).getScene()
                        )
                );

        Button records =
                PatientUI.button(
                        "Medical Records",
                        () -> stage.setScene(
                                new MedicalRecords(stage).getScene()
                        )
                );

        Button aiAssistant =
                PatientUI.button(
                        "AI Assistant",
                        () -> stage.setScene(
                                new AiHealthAssistant(stage).getScene()
                        )
                );

        Button emergency =
                PatientUI.redButton(
                        "Emergency",
                        () -> stage.setScene(
                                new EmergencyAssistance(stage).getScene()
                        )
                );

        actions.getChildren().addAll(
                hospitals,
                passport,
                records,
                aiAssistant,
                emergency
        );

        quickActions.getChildren().add(
                actions
        );

        // =====================================================
        // HEALTH SUMMARY
        // =====================================================

        HBox summary =
                new HBox(18);

        VBox lifestyle =
                PatientUI.greenCard(
                        "Today's Health Summary"
                );

        lifestyle.getChildren().addAll(

                summaryRow(
                        "Daily Steps",
                        "6,842 steps",
                        "Good"
                ),

                summaryRow(
                        "Water Intake",
                        "5 / 8 glasses",
                        "On Track"
                ),

                summaryRow(
                        "Sleep",
                        "7h 32m",
                        "Healthy"
                )
        );

        VBox reminders =
                PatientUI.tealCard(
                        "Health Reminders"
                );

        reminders.getChildren().addAll(

                reminder(
                        "Take morning medication",
                        "Completed"
                ),

                reminder(
                        "Drink more water",
                        "Pending"
                ),

                reminder(
                        "Evening walk",
                        "Pending"
                )
        );

        HBox.setHgrow(
                lifestyle,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                reminders,
                Priority.ALWAYS
        );

        summary.getChildren().addAll(
                lifestyle,
                reminders
        );

        // =====================================================
        // ADD EVERYTHING
        // =====================================================

        content.getChildren().addAll(
                stats,
                middle,
                quickActions,
                summary
        );

        // =====================================================
        // CREATE SCENE
        // =====================================================

        return PatientUI.createScene(
                stage,
                "Dashboard",
                "Good evening, Sarah",
                "Here is your health overview and today's important updates.",
                content
        );
    }

    // =========================================================
    // AI INSIGHT
    // =========================================================

    private VBox insight(
            String title,
            String description
    ) {

        VBox box =
                new VBox(5);

        box.setPadding(
                new Insets(8)
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #581c87;"
        );

        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setWrapText(true);

        descriptionLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        box.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );

        return box;
    }

    // =========================================================
    // SUMMARY ROW
    // =========================================================

    private HBox summaryRow(
            String title,
            String value,
            String status
    ) {

        HBox row =
                new HBox();

        row.setPadding(
                new Insets(8, 0, 8, 0)
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #334155;"
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-text-fill: #475569;"
        );

        Label statusLabel =
                PatientUI.green(status);

        javafx.scene.layout.Region spacer =
                new javafx.scene.layout.Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        row.getChildren().addAll(
                titleLabel,
                spacer,
                valueLabel,
                new Label("   "),
                statusLabel
        );

        return row;
    }

    // =========================================================
    // REMINDER
    // =========================================================

    private HBox reminder(
            String text,
            String status
    ) {

        HBox row =
                new HBox(10);

        row.setPadding(
                new Insets(8, 0, 8, 0)
        );

        Label icon =
                new Label("✓");

        icon.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f766e;"
        );

        Label reminderText =
                new Label(text);

        reminderText.setStyle(
                "-fx-text-fill: #334155;"
        );

        javafx.scene.layout.Region spacer =
                new javafx.scene.layout.Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label statusLabel =
                new Label(status);

        if (status.equalsIgnoreCase("Completed")) {

            statusLabel.setStyle(
                    "-fx-text-fill: #15803d;" +
                    "-fx-font-weight: bold;"
            );

        } else {

            statusLabel.setStyle(
                    "-fx-text-fill: #c2410c;" +
                    "-fx-font-weight: bold;"
            );
        }

        row.getChildren().addAll(
                icon,
                reminderText,
                spacer,
                statusLabel
        );

        return row;
    }
}