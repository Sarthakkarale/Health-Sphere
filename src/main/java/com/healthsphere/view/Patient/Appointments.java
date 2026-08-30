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

        content.setFillWidth(true);

        content.setMinWidth(0);

        content.setMaxWidth(
                Double.MAX_VALUE
        );

        // =====================================================
        // IMAGES
        // =====================================================

        HBox imageRow =
                new HBox(18);

        imageRow.setAlignment(
                Pos.CENTER_LEFT
        );

        imageRow.setFillHeight(true);

        imageRow.setMinWidth(0);

        imageRow.setMaxWidth(
                Double.MAX_VALUE
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
        // BOOKING OPTIONS
        // =====================================================

        HBox bookingOptions =
                new HBox(20);

        bookingOptions.setFillHeight(true);

        bookingOptions.setMinWidth(0);

        bookingOptions.setMaxWidth(
                Double.MAX_VALUE
        );

        VBox hospitalBooking =
                PatientUI.card(
                        "Hospital Appointment"
                );

        hospitalBooking.setMinWidth(0);

        hospitalBooking.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox.setHgrow(
                hospitalBooking,
                Priority.ALWAYS
        );

        Label hospitalText =
                new Label(
                        "Book an appointment directly with a hospital. " +
                        "No doctor selection is required."
                );

        hospitalText.setWrapText(true);

        hospitalText.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 14px;"
        );

        Button hospitalButton =
                PatientUI.button(
                        "Book Hospital Appointment",
                        this::showHospitalBooking
                );

        hospitalBooking.getChildren().addAll(
                hospitalText,
                hospitalButton
        );

        VBox doctorBooking =
                PatientUI.card(
                        "Doctor Appointment"
                );

        doctorBooking.setMinWidth(0);

        doctorBooking.setMaxWidth(
                Double.MAX_VALUE
        );

        HBox.setHgrow(
                doctorBooking,
                Priority.ALWAYS
        );

        Label doctorText =
                new Label(
                        "Book an appointment directly with a doctor. " +
                        "No hospital selection is required."
                );

        doctorText.setWrapText(true);

        doctorText.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 14px;"
        );

        Button doctorButton =
                PatientUI.button(
                        "Book Doctor Appointment",
                        this::showDoctorBooking
                );

        doctorBooking.getChildren().addAll(
                doctorText,
                doctorButton
        );

        bookingOptions.getChildren().addAll(
                hospitalBooking,
                doctorBooking
        );

        // =====================================================
        // LOAD APPOINTMENTS
        // =====================================================

        List<Appointment> appointments =
                new ArrayList<>();

        String errorMessage = null;

        try {

            List<Appointment> loadedAppointments =
                    appointmentController
                            .getCurrentPatientAppointments();

            if (loadedAppointments != null) {

                appointments =
                        loadedAppointments;
            }

        } catch (Exception e) {

            errorMessage =
                    e.getMessage();

            System.err.println(
                    "Unable to load appointments: "
                            + errorMessage
            );
        }

        // =====================================================
        // UPCOMING
        // =====================================================

        VBox upcoming =
                PatientUI.card(
                        "Upcoming Appointments"
                );

        upcoming.setMinWidth(0);

        upcoming.setMaxWidth(
                Double.MAX_VALUE
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

                    HBox appointmentBox =
                            appointmentCard(
                                    appointment
                            );

                    appointmentBox.setMaxWidth(
                            Double.MAX_VALUE
                    );

                    upcoming.getChildren().add(
                            appointmentBox
                    );
                }
            }
        }

        // =====================================================
        // PREVIOUS
        // =====================================================

        VBox previous =
                PatientUI.card(
                        "Previous Appointments"
                );

        previous.setMinWidth(0);

        previous.setMaxWidth(
                Double.MAX_VALUE
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

                    HBox previousBox =
                            previousAppointment(
                                    appointment
                            );

                    previousBox.setMaxWidth(
                            Double.MAX_VALUE
                    );

                    previous.getChildren().add(
                            previousBox
                    );
                }
            }
        }

        // =====================================================
        // ADD CONTENT
        // =====================================================

        content.getChildren().addAll(
                imageRow,
                bookingOptions,
                upcoming,
                previous
        );

        return PatientUI.createScene(
                stage,
                "Appointments",
                "Appointments",
                "Manage your hospital and doctor appointments.",
                content
        );
    }

    // =========================================================
    // UPCOMING APPOINTMENTS
    // =========================================================

    private List<Appointment> getUpcomingAppointments(
            List<Appointment> appointments) {

        List<Appointment> result =
                new ArrayList<>();

        LocalDate today =
                LocalDate.now();

        if (appointments == null) {
            return result;
        }

        for (Appointment appointment :
                appointments) {

            if (appointment == null) {
                continue;
            }

            LocalDate appointmentDate =
                    parseDate(
                            appointment.getAppointmentDate()
                    );

            if (appointmentDate != null
                    && !appointmentDate.isBefore(today)) {

                result.add(appointment);
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
    // PREVIOUS APPOINTMENTS
    // =========================================================

    private List<Appointment> getPreviousAppointments(
            List<Appointment> appointments) {

        List<Appointment> result =
                new ArrayList<>();

        LocalDate today =
                LocalDate.now();

        if (appointments == null) {
            return result;
        }

        for (Appointment appointment :
                appointments) {

            if (appointment == null) {
                continue;
            }

            LocalDate appointmentDate =
                    parseDate(
                            appointment.getAppointmentDate()
                    );

            if (appointmentDate != null
                    && appointmentDate.isBefore(today)) {

                result.add(appointment);
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
    // APPOINTMENT CARD
    // =========================================================

    private HBox appointmentCard(
            Appointment appointment) {

        HBox box =
                new HBox(18);

        box.setPadding(
                new Insets(14)
        );

        box.setAlignment(
                Pos.CENTER_LEFT
        );

        box.setMinWidth(0);

        box.setMaxWidth(
                Double.MAX_VALUE
        );

        box.setStyle(
                "-fx-background-color: #f8fafc;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 12;"
        );

        ImageView image;

        if ("HOSPITAL".equalsIgnoreCase(
                appointment.getBookingType())) {

            image =
                    createImage(
                            "/images/appointments/appointment2.jpg",
                            165,
                            110
                    );

        } else {

            image =
                    createImage(
                            "/images/appointments/appointment1.jpg",
                            165,
                            110
                    );
        }

        VBox information =
                new VBox(7);

        information.setMinWidth(0);

        information.setMaxWidth(
                Double.MAX_VALUE
        );

        Label type =
                new Label(
                        "HOSPITAL".equalsIgnoreCase(
                                appointment.getBookingType()
                        )
                                ? "Hospital Appointment"
                                : "Doctor Appointment"
                );

        type.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #2563eb;"
        );

        Label mainName;

        if ("HOSPITAL".equalsIgnoreCase(
                appointment.getBookingType())) {

            mainName =
                    new Label(
                            safe(
                                    appointment.getHospitalName(),
                                    "Hospital"
                            )
                    );

        } else {

            mainName =
                    new Label(
                            safe(
                                    appointment.getDoctorName(),
                                    "Doctor"
                            )
                    );
        }

        mainName.setWrapText(true);

        mainName.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label specialty =
                new Label();

        if ("HOSPITAL".equalsIgnoreCase(
                appointment.getBookingType())) {

            specialty.setText(
                    "Hospital booking"
            );

        } else {

            specialty.setText(
                    safe(
                            appointment.getSpecialty(),
                            "Specialty"
                    )
            );
        }

        specialty.setWrapText(true);

        specialty.setStyle(
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

        date.setWrapText(true);

        date.setStyle(
                "-fx-text-fill: #475569;"
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
                type,
                mainName,
                specialty,
                date,
                status
        );

        HBox.setHgrow(
                information,
                Priority.ALWAYS
        );

        Button view =
                PatientUI.button(
                        "View",
                        () ->
                                showAppointmentDetails(
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
            Appointment appointment) {

        HBox box =
                new HBox(15);

        box.setPadding(
                new Insets(12)
        );

        box.setAlignment(
                Pos.CENTER_LEFT
        );

        box.setMinWidth(0);

        box.setMaxWidth(
                Double.MAX_VALUE
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

        information.setMinWidth(0);

        information.setMaxWidth(
                Double.MAX_VALUE
        );

        String displayName;

        if ("HOSPITAL".equalsIgnoreCase(
                appointment.getBookingType())) {

            displayName =
                    safe(
                            appointment.getHospitalName(),
                            "Hospital"
                    );

        } else {

            displayName =
                    safe(
                            appointment.getDoctorName(),
                            "Doctor"
                    );
        }

        Label name =
                new Label(displayName);

        name.setWrapText(true);

        name.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label type =
                new Label(
                        "HOSPITAL".equalsIgnoreCase(
                                appointment.getBookingType()
                        )
                                ? "Hospital Appointment"
                                : "Doctor Appointment"
                );

        type.setStyle(
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
                name,
                type,
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
    // DETAILS
    // =========================================================

    private void showAppointmentDetails(
            Appointment appointment) {

        VBox content =
                new VBox(15);

        content.setPadding(
                new Insets(30)
        );

        content.setAlignment(
                Pos.TOP_LEFT
        );

        content.setMinWidth(0);

        content.setMaxWidth(
                Double.MAX_VALUE
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

        String bookingType =
                "HOSPITAL".equalsIgnoreCase(
                        appointment.getBookingType()
                )
                        ? "Hospital Appointment"
                        : "Doctor Appointment";

        content.getChildren().add(
                detailLabel(
                        "Type",
                        bookingType
                )
        );

        if ("HOSPITAL".equalsIgnoreCase(
                appointment.getBookingType())) {

            content.getChildren().add(
                    detailLabel(
                            "Hospital",
                            appointment.getHospitalName()
                    )
            );

        } else {

            content.getChildren().addAll(

                    detailLabel(
                            "Doctor",
                            appointment.getDoctorName()
                    ),

                    detailLabel(
                            "Specialty",
                            appointment.getSpecialty()
                    )
            );
        }

        content.getChildren().addAll(

                title,

                detailLabel(
                        "Date",
                        formatDate(
                                appointment.getAppointmentDate()
                        )
                ),

                detailLabel(
                        "Time",
                        appointment.getAppointmentTime()
                ),

                detailLabel(
                        "Reason",
                        appointment.getReason()
                ),

                detailLabel(
                        "Status",
                        appointment.getStatus()
                )
        );

        Button back =
                PatientUI.secondaryButton(
                        "Back to Appointments",
                        this::showAppointments
                );

        content.getChildren().add(
                back
        );

        stage.setScene(
                PatientUI.createScene(
                        stage,
                        "Appointment Details",
                        "Appointment Details",
                        "View your appointment information.",
                        content
                )
        );

        stage.show();

        if (!stage.isMaximized()) {
            stage.setMaximized(true);
        }
    }

    // =========================================================
    // DETAIL LABEL
    // =========================================================

    private Label detailLabel(
            String title,
            String value) {

        Label label =
                new Label(
                        title
                                + ": "
                                + safe(
                                        value,
                                        "Not available"
                                )
                );

        label.setWrapText(true);

        label.setMaxWidth(
                Double.MAX_VALUE
        );

        label.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #334155;"
        );

        return label;
    }

    // =========================================================
    // EMPTY
    // =========================================================

    private Label emptyLabel(
            String message) {

        Label label =
                new Label(message);

        label.setWrapText(true);

        label.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 14px;"
        );

        return label;
    }

    // =========================================================
    // ERROR
    // =========================================================

    private Label errorLabel(
            String message) {

        Label label =
                new Label(message);

        label.setWrapText(true);

        label.setMaxWidth(
                Double.MAX_VALUE
        );

        label.setStyle(
                "-fx-text-fill: #dc2626;" +
                "-fx-font-size: 14px;"
        );

        return label;
    }

    // =========================================================
    // SAFE
    // =========================================================

    private String safe(
            String value,
            String fallback) {

        if (value == null ||
                value.isBlank()) {

            return fallback;
        }

        return value;
    }

    // =========================================================
    // DATE PARSER
    // =========================================================

    private LocalDate parseDate(
            String date) {

        if (date == null ||
                date.isBlank()) {

            return null;
        }

        try {

            return LocalDate.parse(date);

        } catch (Exception e) {

            return null;
        }
    }

    // =========================================================
    // DATE FORMAT
    // =========================================================

    private String formatDate(
            String date) {

        if (date == null ||
                date.isBlank()) {

            return "Date not available";
        }

        try {

            LocalDate localDate =
                    LocalDate.parse(date);

            String month =
                    localDate
                            .getMonth()
                            .toString();

            month =
                    month.substring(0, 1)
                            + month.substring(1)
                            .toLowerCase();

            return localDate.getDayOfMonth()
                    + " "
                    + month
                    + " "
                    + localDate.getYear();

        } catch (Exception e) {

            return date;
        }
    }

    // =========================================================
    // IMAGE
    // =========================================================

    private ImageView createImage(
            String path,
            double width,
            double height) {

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
    // HOSPITAL BOOKING
    // =========================================================

    private void showHospitalBooking() {

        stage.setScene(
                new HospitalBooking(stage)
                        .getScene()
        );

        stage.show();

        if (!stage.isMaximized()) {
            stage.setMaximized(true);
        }
    }

    // =========================================================
    // DOCTOR BOOKING
    // =========================================================

    private void showDoctorBooking() {

        stage.setScene(
                new DoctorBooking(stage)
                        .getScene()
        );

        stage.show();

        if (!stage.isMaximized()) {
            stage.setMaximized(true);
        }
    }

    // =========================================================
    // BACK
    // =========================================================

    private void showAppointments() {

        stage.setScene(
                new Appointments(stage)
                        .getScene()
        );

        stage.show();

        if (!stage.isMaximized()) {
            stage.setMaximized(true);
        }
    }
}