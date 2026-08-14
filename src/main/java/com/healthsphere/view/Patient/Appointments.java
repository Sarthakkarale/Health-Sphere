package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Appointments {

    private final Stage stage;

    public Appointments(Stage stage) {
        this.stage = stage;
    }

    // =========================================================
    // MAIN SCENE
    // =========================================================

    public Scene getScene() {

        VBox content =
                new VBox(20);

        content.setPadding(
                new Insets(5)
        );

        // =====================================================
        // APPOINTMENT IMAGES
        // =====================================================

        HBox imageRow =
                new HBox(18);

        imageRow.setAlignment(
                Pos.CENTER_LEFT
        );

        imageRow.getChildren().addAll(

                createImage(
                        "/images/appointments/appointment1.jpg",
                        250,
                        165
                ),

                createImage(
                        "/images/appointments/appointment2.jpg",
                        250,
                        165
                ),

                createImage(
                        "/images/appointments/appointment3.jpg",
                        250,
                        165
                ),

                createImage(
                        "/images/appointments/appointment4.jpg",
                        250,
                        165
                )
        );

        // =====================================================
        // BOOK APPOINTMENT
        // =====================================================

        VBox booking =
                PatientUI.card(
                        "Book a New Appointment"
                );

        Label bookingText =
                new Label(
                        "Need to see a doctor? Find an available specialist and book your appointment."
                );

        bookingText.setWrapText(true);

        bookingText.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 14px;"
        );

        Button book =
                PatientUI.button(
                        "Book Appointment",
                        this::showBookAppointment
                );

        booking.getChildren().addAll(
                bookingText,
                book
        );

        // =====================================================
        // UPCOMING APPOINTMENT
        // =====================================================

        VBox upcoming =
                PatientUI.card(
                        "Upcoming Appointment"
                );

        HBox appointment =
                appointmentCard(
                        "/images/appointments/appointment1.jpg",
                        "Dr. Sarah Jenkins",
                        "Cardiology",
                        "Tomorrow • 10:00 AM",
                        "Apollo Hospitals"
                );

        upcoming.getChildren().add(
                appointment
        );

        // =====================================================
        // PREVIOUS APPOINTMENTS
        // =====================================================

        VBox previous =
                PatientUI.card(
                        "Previous Appointments"
                );

        previous.getChildren().addAll(

                previousAppointment(
                        "/images/appointments/appointment2.jpg",
                        "Dr. Michael Brown",
                        "General Medicine",
                        "10 August 2026",
                        "Completed"
                ),

                previousAppointment(
                        "/images/appointments/appointment3.jpg",
                        "Dr. Emily Carter",
                        "Dermatology",
                        "28 July 2026",
                        "Completed"
                ),

                previousAppointment(
                        "/images/appointments/appointment4.jpg",
                        "Dr. Robert Wilson",
                        "Orthopedics",
                        "15 July 2026",
                        "Completed"
                )
        );

        // =====================================================
        // ADD CONTENT
        // =====================================================

        content.getChildren().addAll(
                imageRow,
                booking,
                upcoming,
                previous
        );

        // =====================================================
        // SCROLL
        // =====================================================

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
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;"
        );

        VBox wrapper =
                new VBox(scroll);

        // =====================================================
        // COMMON PATIENT HEADER
        // =====================================================

        return PatientUI.createScene(
                stage,
                "Appointments",
                "Appointments",
                "Manage your upcoming and previous healthcare appointments.",
                wrapper
        );
    }

    // =========================================================
    // UPCOMING APPOINTMENT CARD
    // =========================================================

    private HBox appointmentCard(
            String imagePath,
            String doctorName,
            String speciality,
            String date,
            String hospital
    ) {

        HBox box =
                new HBox(18);

        box.setPadding(
                new Insets(14)
        );

        box.setAlignment(
                Pos.CENTER_LEFT
        );

        box.setStyle(
                "-fx-background-color: #f8fafc;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 12;"
        );

        ImageView image =
                createImage(
                        imagePath,
                        165,
                        110
                );

        VBox information =
                new VBox(7);

        Label doctor =
                new Label(
                        doctorName
                );

        doctor.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label specialityLabel =
                new Label(
                        speciality
                );

        specialityLabel.setStyle(
                "-fx-text-fill: #2563eb;" +
                "-fx-font-weight: bold;"
        );

        Label dateLabel =
                new Label(
                        date
                );

        dateLabel.setStyle(
                "-fx-text-fill: #475569;"
        );

        Label hospitalLabel =
                new Label(
                        hospital
                );

        hospitalLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        information.getChildren().addAll(
                doctor,
                specialityLabel,
                dateLabel,
                hospitalLabel
        );

        HBox.setHgrow(
                information,
                Priority.ALWAYS
        );

        Button view =
                PatientUI.button(
                        "View",
                        () -> {

                            System.out.println(
                                    "Viewing appointment with "
                                            + doctorName
                            );
                        }
                );

        box.getChildren().addAll(
                image,
                information,
                view
        );

        return box;
    }

    // =========================================================
    // PREVIOUS APPOINTMENT
    // =========================================================

    private HBox previousAppointment(
            String imagePath,
            String doctorName,
            String speciality,
            String date,
            String status
    ) {

        HBox box =
                new HBox(15);

        box.setPadding(
                new Insets(12)
        );

        box.setAlignment(
                Pos.CENTER_LEFT
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #e2e8f0;" +
                "-fx-border-radius: 10;"
        );

        ImageView image =
                createImage(
                        imagePath,
                        120,
                        80
                );

        VBox information =
                new VBox(5);

        Label doctor =
                new Label(
                        doctorName
                );

        doctor.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label specialityLabel =
                new Label(
                        speciality
                );

        specialityLabel.setStyle(
                "-fx-text-fill: #2563eb;"
        );

        Label dateLabel =
                new Label(
                        date
                );

        dateLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        information.getChildren().addAll(
                doctor,
                specialityLabel,
                dateLabel
        );

        HBox.setHgrow(
                information,
                Priority.ALWAYS
        );

        Label statusLabel =
                new Label(
                        status
                );

        statusLabel.setStyle(
                "-fx-text-fill: #16a34a;" +
                "-fx-font-weight: bold;"
        );

        box.getChildren().addAll(
                image,
                information,
                statusLabel
        );

        return box;
    }

    // =========================================================
    // IMAGE LOADER
    // =========================================================

    private ImageView createImage(
            String path,
            double width,
            double height
    ) {

        ImageView view =
                new ImageView();

        var resource =
                getClass().getResource(path);

        if (resource == null) {

            System.err.println(
                    "Appointment image not found: "
                            + path
            );

            view.setFitWidth(width);
            view.setFitHeight(height);

            return view;
        }

        Image image =
                new Image(
                        resource.toExternalForm()
                );

        view.setImage(image);

        view.setFitWidth(width);
        view.setFitHeight(height);

        view.setPreserveRatio(false);

        return view;
    }

    // =========================================================
    // NAVIGATION
    // =========================================================

    private void showBookAppointment() {

        stage.setScene(
                new BookAppointment(stage).getScene()
        );

        stage.show();
    }

    // =========================================================
    // SIDEBAR NAVIGATION
    // =========================================================

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