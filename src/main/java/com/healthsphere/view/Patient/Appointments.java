package com.healthsphere.view.Patient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.healthsphere.controller.patient.AppointmentController;
import com.healthsphere.model.Appointment;

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

    private final AppointmentController appointmentController;

    public Appointments(Stage stage) {

        this.stage = stage;

        this.appointmentController =
                new AppointmentController();
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
        // APPOINTMENT DATA
        // =====================================================

        List<Appointment> appointments =
                new ArrayList<>();

        String errorMessage = null;

        try {

            appointments =
                    appointmentController
                            .getCurrentPatientAppointments();

        } catch (Exception e) {

            errorMessage =
                    e.getMessage();

            System.err.println(
                    "Unable to load appointments: "
                            + errorMessage
            );
        }

        // =====================================================
        // UPCOMING APPOINTMENT
        // =====================================================

        VBox upcoming =
                PatientUI.card(
                        "Upcoming Appointment"
                );

        if (errorMessage != null) {

            upcoming.getChildren().add(
                    errorLabel(
                            "Unable to load appointments: "
                                    + errorMessage
                    )
            );

        } else {

            List<Appointment> upcomingAppointments =
                    getUpcomingAppointments(
                            appointments
                    );

            if (upcomingAppointments.isEmpty()) {

                upcoming.getChildren().add(
                        emptyLabel(
                                "You have no upcoming appointments."
                        )
                );

            } else {

                for (Appointment appointment :
                        upcomingAppointments) {

                    upcoming.getChildren().add(
                            appointmentCard(
                                    appointment
                            )
                    );
                }
            }
        }

        // =====================================================
        // PREVIOUS APPOINTMENTS
        // =====================================================

        VBox previous =
                PatientUI.card(
                        "Previous Appointments"
                );

        if (errorMessage != null) {

            previous.getChildren().add(
                    errorLabel(
                            "Unable to load previous appointments."
                    )
            );

        } else {

            List<Appointment> previousAppointments =
                    getPreviousAppointments(
                            appointments
                    );

            if (previousAppointments.isEmpty()) {

                previous.getChildren().add(
                        emptyLabel(
                                "You have no previous appointments."
                        )
                );

            } else {

                for (Appointment appointment :
                        previousAppointments) {

                    previous.getChildren().add(
                            previousAppointment(
                                    appointment
                            )
                    );
                }
            }
        }

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
    // GET UPCOMING APPOINTMENTS
    // =========================================================

    private List<Appointment> getUpcomingAppointments(
            List<Appointment> appointments
    ) {

        List<Appointment> result =
                new ArrayList<>();

        LocalDate today =
                LocalDate.now();

        for (Appointment appointment :
                appointments) {

            if (appointment == null) {
                continue;
            }

            LocalDate appointmentDate =
                    parseDate(
                            appointment
                                    .getAppointmentDate()
                    );

            if (appointmentDate != null &&
                    !appointmentDate.isBefore(today)) {

                result.add(
                        appointment
                );
            }
        }

        result.sort(
                Comparator.comparing(
                        appointment ->
                                parseDate(
                                        appointment
                                                .getAppointmentDate()
                                ),
                        Comparator.nullsLast(
                                Comparator.naturalOrder()
                        )
                )
        );

        return result;
    }

    // =========================================================
    // GET PREVIOUS APPOINTMENTS
    // =========================================================

    private List<Appointment> getPreviousAppointments(
            List<Appointment> appointments
    ) {

        List<Appointment> result =
                new ArrayList<>();

        LocalDate today =
                LocalDate.now();

        for (Appointment appointment :
                appointments) {

            if (appointment == null) {
                continue;
            }

            LocalDate appointmentDate =
                    parseDate(
                            appointment
                                    .getAppointmentDate()
                    );

            if (appointmentDate != null &&
                    appointmentDate.isBefore(today)) {

                result.add(
                        appointment
                );
            }
        }

        result.sort(
                Comparator.comparing(
                        appointment ->
                                parseDate(
                                        appointment
                                                .getAppointmentDate()
                                ),
                        Comparator.nullsLast(
                                Comparator.reverseOrder()
                        )
                )
        );

        return result;
    }

    // =========================================================
    // PARSE DATE
    // =========================================================

    private LocalDate parseDate(
            String date
    ) {

        if (date == null ||
                date.isBlank()) {

            return null;
        }

        try {

            return LocalDate.parse(
                    date
            );

        } catch (Exception e) {

            return null;
        }
    }

    // =========================================================
    // UPCOMING APPOINTMENT CARD
    // =========================================================

    private HBox appointmentCard(
            Appointment appointment
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
                        "/images/appointments/appointment1.jpg",
                        165,
                        110
                );

        VBox information =
                new VBox(7);

        Label doctor =
                new Label(
                        safe(
                                appointment.getDoctorName(),
                                "Doctor"
                        )
                );

        doctor.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label speciality =
                new Label(
                        safe(
                                appointment.getSpecialty(),
                                "Specialty"
                        )
                );

        speciality.setStyle(
                "-fx-text-fill: #2563eb;" +
                "-fx-font-weight: bold;"
        );

        Label date =
                new Label(
                        formatDate(
                                appointment.getAppointmentDate()
                        )
                        + " • "
                        + safe(
                                appointment.getAppointmentTime(),
                                "Time"
                        )
                );

        date.setStyle(
                "-fx-text-fill: #475569;"
        );

        Label hospital =
                new Label(
                        safe(
                                appointment.getHospital(),
                                "Hospital"
                        )
                );

        hospital.setStyle(
                "-fx-text-fill: #64748b;"
        );

        Label status =
                new Label(
                        safe(
                                appointment.getStatus(),
                                "Upcoming"
                        )
                );

        status.setStyle(
                "-fx-text-fill: #16a34a;" +
                "-fx-font-weight: bold;"
        );

        information.getChildren().addAll(

                doctor,
                speciality,
                date,
                hospital,
                status
        );

        HBox.setHgrow(
                information,
                Priority.ALWAYS
        );

        Button view =
                PatientUI.button(
                        "View",
                        () -> showAppointmentDetails(
                                appointment
                        )
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
            Appointment appointment
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
                        "/images/appointments/appointment2.jpg",
                        120,
                        80
                );

        VBox information =
                new VBox(5);

        Label doctor =
                new Label(
                        safe(
                                appointment.getDoctorName(),
                                "Doctor"
                        )
                );

        doctor.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label speciality =
                new Label(
                        safe(
                                appointment.getSpecialty(),
                                "Specialty"
                        )
                );

        speciality.setStyle(
                "-fx-text-fill: #2563eb;"
        );

        Label date =
                new Label(
                        formatDate(
                                appointment.getAppointmentDate()
                        )
                );

        date.setStyle(
                "-fx-text-fill: #64748b;"
        );

        information.getChildren().addAll(

                doctor,
                speciality,
                date
        );

        HBox.setHgrow(
                information,
                Priority.ALWAYS
        );

        Label status =
                new Label(
                        safe(
                                appointment.getStatus(),
                                "Completed"
                        )
                );

        status.setStyle(
                "-fx-text-fill: #16a34a;" +
                "-fx-font-weight: bold;"
        );

        box.getChildren().addAll(

                image,
                information,
                status
        );

        return box;
    }

    // =========================================================
    // APPOINTMENT DETAILS
    // =========================================================

    private void showAppointmentDetails(
            Appointment appointment
    ) {

        VBox content =
                new VBox(15);

        content.setPadding(
                new Insets(30)
        );

        content.setAlignment(
                Pos.TOP_LEFT
        );

        Label title =
                new Label(
                        "Appointment Details"
                );

        title.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label doctor =
                detailLabel(
                        "Doctor",
                        appointment.getDoctorName()
                );

        Label specialty =
                detailLabel(
                        "Specialty",
                        appointment.getSpecialty()
                );

        Label hospital =
                detailLabel(
                        "Hospital",
                        appointment.getHospital()
                );

        Label date =
                detailLabel(
                        "Date",
                        formatDate(
                                appointment.getAppointmentDate()
                        )
                );

        Label time =
                detailLabel(
                        "Time",
                        appointment.getAppointmentTime()
                );

        Label reason =
                detailLabel(
                        "Reason",
                        appointment.getReason()
                );

        Label status =
                detailLabel(
                        "Status",
                        appointment.getStatus()
                );

        Button back =
                PatientUI.secondaryButton(
                        "Back to Appointments",
                        this::showAppointments
                );

        content.getChildren().addAll(

                title,
                doctor,
                specialty,
                hospital,
                date,
                time,
                reason,
                status,
                back
        );

        VBox wrapper =
                new VBox(content);

        wrapper.setPadding(
                new Insets(20)
        );

        ScrollPane scroll =
                new ScrollPane(wrapper);

        scroll.setFitToWidth(true);

        stage.setScene(

                PatientUI.createScene(

                        stage,

                        "Appointment Details",

                        "Appointment Details",

                        "View your appointment information.",

                        scroll
                )
        );

        stage.show();
    }

    // =========================================================
    // DETAIL LABEL
    // =========================================================

    private Label detailLabel(
            String title,
            String value
    ) {

        Label label =
                new Label(
                        title + ": "
                                + safe(value, "Not available")
                );

        label.setWrapText(true);

        label.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #334155;"
        );

        return label;
    }

    // =========================================================
    // EMPTY MESSAGE
    // =========================================================

    private Label emptyLabel(
            String message
    ) {

        Label label =
                new Label(message);

        label.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 14px;"
        );

        return label;
    }

    // =========================================================
    // ERROR MESSAGE
    // =========================================================

    private Label errorLabel(
            String message
    ) {

        Label label =
                new Label(message);

        label.setWrapText(true);

        label.setStyle(
                "-fx-text-fill: #dc2626;" +
                "-fx-font-size: 14px;"
        );

        return label;
    }

    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safe(
            String value,
            String fallback
    ) {

        if (value == null ||
                value.isBlank()) {

            return fallback;
        }

        return value;
    }

    // =========================================================
    // FORMAT DATE
    // =========================================================

    private String formatDate(
            String date
    ) {

        if (date == null ||
                date.isBlank()) {

            return "Date not available";
        }

        try {

            LocalDate localDate =
                    LocalDate.parse(date);

            return localDate.getDayOfMonth()
                    + " "
                    + localDate.getMonth()
                            .toString()
                            .charAt(0)
                    + localDate.getMonth()
                            .toString()
                            .substring(1)
                            .toLowerCase()
                    + " "
                    + localDate.getYear();

        } catch (Exception e) {

            return date;
        }
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
                new BookAppointment(stage)
                        .getScene()
        );

        stage.show();
    }

    private void showAppointments() {

        stage.setScene(
                new Appointments(stage)
                        .getScene()
        );

        stage.show();
    }
}