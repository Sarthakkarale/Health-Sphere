package com.healthsphere.view.Patient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.healthsphere.controller.patient.AppointmentController;
import com.healthsphere.controller.patient.ReviewController;
import com.healthsphere.dao.authentication.DoctorDAO;
import com.healthsphere.model.Appointment;
import com.healthsphere.model.DoctorProfile;
import com.healthsphere.model.Review;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Appointments {

    private final Stage stage;

    private final AppointmentController appointmentController;

    private final ReviewController reviewController;

    private final DoctorDAO doctorDAO;


    public Appointments(Stage stage) {

        this.stage = stage;

        this.appointmentController =
                new AppointmentController();

        this.reviewController =
                new ReviewController();

        this.doctorDAO =
                new DoctorDAO();
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


        // =====================================================
        // HOSPITAL BOOKING
        // =====================================================

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
                        "Book an appointment directly with a hospital. "
                                + "No doctor selection is required."
                );

        hospitalText.setWrapText(true);

        hospitalText.setStyle(
                "-fx-text-fill: #64748b;"
                        + "-fx-font-size: 14px;"
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


        // =====================================================
        // DOCTOR BOOKING
        // =====================================================

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
                        "Book an appointment directly with a doctor. "
                                + "No hospital selection is required."
                );

        doctorText.setWrapText(true);

        doctorText.setStyle(
                "-fx-text-fill: #64748b;"
                        + "-fx-font-size: 14px;"
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

        String errorMessage =
                null;

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
        // UPCOMING APPOINTMENTS
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
        // PREVIOUS APPOINTMENTS
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
                                "You have no completed appointments."
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

            String status =
                    safe(
                            appointment.getStatus(),
                            ""
                    );

            if ("COMPLETED".equalsIgnoreCase(status)) {

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

        if (appointments == null) {

            return result;
        }

        for (Appointment appointment :
                appointments) {

            if (appointment == null) {

                continue;
            }

            String status =
                    safe(
                            appointment.getStatus(),
                            ""
                    );

            if ("COMPLETED".equalsIgnoreCase(
                    status.trim()
            )) {

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
    // UPCOMING APPOINTMENT CARD
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
                "-fx-background-color: #f8fafc;"
                        + "-fx-background-radius: 12;"
                        + "-fx-border-color: #bfdbfe;"
                        + "-fx-border-radius: 12;"
        );


        ImageView image;

        if ("HOSPITAL".equalsIgnoreCase(
                appointment.getBookingType()
        )) {

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
                "-fx-font-size: 12px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #2563eb;"
        );


        Label mainName;

        if ("HOSPITAL".equalsIgnoreCase(
                appointment.getBookingType()
        )) {

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
                "-fx-font-size: 18px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #0f172a;"
        );


        Label specialty =
                new Label();

        if ("HOSPITAL".equalsIgnoreCase(
                appointment.getBookingType()
        )) {

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
                "-fx-text-fill: #2563eb;"
                        + "-fx-font-weight: bold;"
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
                "-fx-text-fill: #16a34a;"
                        + "-fx-font-weight: bold;"
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
    // PREVIOUS APPOINTMENT CARD
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
                "-fx-background-color: white;"
                        + "-fx-background-radius: 10;"
                        + "-fx-border-color: #e2e8f0;"
                        + "-fx-border-radius: 10;"
        );


        ImageView image =
                createImage(

                        "HOSPITAL".equalsIgnoreCase(
                                appointment.getBookingType()
                        )

                                ? "/images/appointments/appointment2.jpg"

                                : "/images/appointments/appointment1.jpg",

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
                appointment.getBookingType()
        )) {

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
                "-fx-font-size: 16px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #0f172a;"
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


        Label status =
                new Label(
                        "COMPLETED"
                );

        status.setStyle(
                "-fx-text-fill: #16a34a;"
                        + "-fx-font-weight: bold;"
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


        Button reviewButton =
                createReviewButton(
                        appointment
                );


        box.getChildren().addAll(
                image,
                information,
                status,
                reviewButton
        );

        return box;
    }


    // =========================================================
    // REVIEW BUTTON
    // =========================================================

    private Button createReviewButton(
            Appointment appointment) {

        String targetType;

        String targetId;

        String targetName;


        if ("HOSPITAL".equalsIgnoreCase(
                appointment.getBookingType()
        )) {

            targetType =
                    "HOSPITAL";

            targetId =
                    safe(
                            appointment.getHospitalId(),
                            ""
                    );

            targetName =
                    safe(
                            appointment.getHospitalName(),
                            "Hospital"
                    );

        } else {

            targetType =
                    "DOCTOR";

            targetId =
                    safe(
                            appointment.getDoctorUid(),
                            ""
                    );

            targetName =
                    safe(
                            appointment.getDoctorName(),
                            "Doctor"
                    );
        }


        if (targetId.isBlank()) {

            Button unavailable =
                    new Button(
                            "Review Unavailable"
                    );

            unavailable.setDisable(true);

            unavailable.setStyle(
                    "-fx-background-color: #f1f5f9;"
                            + "-fx-text-fill: #94a3b8;"
                            + "-fx-font-weight: bold;"
                            + "-fx-background-radius: 8;"
                            + "-fx-padding: 8 14 8 14;"
            );

            return unavailable;
        }


        boolean alreadyReviewed =
                false;

        try {

            alreadyReviewed =
                    reviewController
                            .hasCurrentPatientReviewed(

                                    appointment.getAppointmentId(),

                                    targetType,

                                    targetId
                            );

        } catch (Exception e) {

            System.err.println(
                    "Unable to check review status: "
                            + e.getMessage()
            );
        }


        if (alreadyReviewed) {

            Button reviewed =
                    new Button(
                            "Reviewed ✓"
                    );

            reviewed.setDisable(true);

            reviewed.setStyle(
                    "-fx-background-color: #dcfce7;"
                            + "-fx-text-fill: #15803d;"
                            + "-fx-font-weight: bold;"
                            + "-fx-background-radius: 8;"
                            + "-fx-padding: 8 14 8 14;"
            );

            return reviewed;
        }


        return PatientUI.button(

                "Write Review",

                () ->
                        showReviewDialog(

                                appointment,

                                targetType,

                                targetId,

                                targetName
                        )
        );
    }


    // =========================================================
    // REVIEW DIALOG
    // =========================================================

    private void showReviewDialog(
            Appointment appointment,
            String targetType,
            String targetId,
            String targetName) {

        Stage reviewStage =
                new Stage();

        reviewStage.initOwner(stage);

        reviewStage.initModality(
                Modality.WINDOW_MODAL
        );

        reviewStage.setTitle(
                "Write Review"
        );


        VBox root =
                new VBox(18);

        root.setPadding(
                new Insets(28)
        );

        root.setAlignment(
                Pos.TOP_CENTER
        );

        root.setPrefWidth(500);

        root.setStyle(
                "-fx-background-color: white;"
        );


        Label title =
                new Label(
                        "Write a Review"
                );

        title.setStyle(
                "-fx-font-size: 26px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #0f172a;"
        );


        Label subtitle =
                new Label(
                        "Share your experience with "
                                + targetName
                );

        subtitle.setWrapText(true);

        subtitle.setStyle(
                "-fx-font-size: 14px;"
                        + "-fx-text-fill: #64748b;"
        );


        Label ratingTitle =
                new Label(
                        "Your Rating"
                );

        ratingTitle.setStyle(
                "-fx-font-size: 16px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #334155;"
        );


        HBox stars =
                new HBox(8);

        stars.setAlignment(
                Pos.CENTER
        );


        Button[] starButtons =
                new Button[5];

        final int[] selectedRating =
                {0};


        Label ratingLabel =
                new Label(
                        "Select a rating"
                );

        ratingLabel.setStyle(
                "-fx-text-fill: #64748b;"
                        + "-fx-font-size: 13px;"
        );


        for (int i = 0; i < 5; i++) {

            final int rating =
                    i + 1;

            Button star =
                    new Button("★");

            starButtons[i] =
                    star;

            star.setStyle(
                    "-fx-background-color: transparent;"
                            + "-fx-text-fill: #cbd5e1;"
                            + "-fx-font-size: 32px;"
                            + "-fx-padding: 0;"
                            + "-fx-cursor: hand;"
            );

            star.setOnMouseEntered(

                    event ->
                            updateStarDisplay(
                                    starButtons,
                                    rating
                            )
            );

            star.setOnMouseExited(

                    event ->
                            updateStarDisplay(
                                    starButtons,
                                    selectedRating[0]
                            )
            );

            star.setOnAction(

                    event -> {

                        selectedRating[0] =
                                rating;

                        updateStarDisplay(
                                starButtons,
                                selectedRating[0]
                        );

                        ratingLabel.setText(
                                rating
                                        + " out of 5"
                        );
                    }
            );

            stars.getChildren().add(
                    star
            );
        }


        Label commentTitle =
                new Label(
                        "Your Review"
                );

        commentTitle.setStyle(
                "-fx-font-size: 16px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #334155;"
        );


        TextArea commentArea =
                new TextArea();

        commentArea.setPromptText(
                "Write your experience here..."
        );

        commentArea.setWrapText(true);

        commentArea.setPrefRowCount(5);

        commentArea.setMaxWidth(
                Double.MAX_VALUE
        );


        Label message =
                new Label();

        message.setWrapText(true);

        message.setStyle(
                "-fx-text-fill: #dc2626;"
                        + "-fx-font-size: 13px;"
        );


        Button cancel =
                PatientUI.secondaryButton(
                        "Cancel",
                        reviewStage::close
                );


        Button submit =
                PatientUI.button(

                        "Submit Review",

                        () -> {

                            if (selectedRating[0] < 1
                                    || selectedRating[0] > 5) {

                                message.setText(
                                        "Please select a rating between 1 and 5."
                                );

                                return;
                            }


                            String comment =
                                    commentArea
                                            .getText()
                                            .trim();

                            if (comment.isBlank()) {

                                message.setText(
                                        "Please enter your review."
                                );

                                return;
                            }


                            try {

                                Review review =
                                        reviewController
                                                .createReview(

                                                        appointment
                                                                .getAppointmentId(),

                                                        targetType,

                                                        targetId,

                                                        targetName,

                                                        selectedRating[0],

                                                        comment
                                                );


                                if (review != null) {

                                    reviewStage.close();

                                    showSuccessMessage(
                                            "Your review has been submitted successfully."
                                    );

                                    refreshAppointments();
                                }

                            } catch (Exception e) {

                                message.setText(

                                        safe(
                                                e.getMessage(),
                                                "Unable to submit review."
                                        )
                                );

                                System.err.println(
                                        "Review submission failed: "
                                                + e.getMessage()
                                );
                            }
                        }
                );


        HBox buttonRow =
                new HBox(12);

        buttonRow.setAlignment(
                Pos.CENTER_RIGHT
        );

        buttonRow.getChildren().addAll(
                cancel,
                submit
        );


        root.getChildren().addAll(
                title,
                subtitle,
                ratingTitle,
                stars,
                ratingLabel,
                commentTitle,
                commentArea,
                message,
                buttonRow
        );


        ScrollPane scrollPane =
                new ScrollPane(root);

        scrollPane.setFitToWidth(true);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );


        Scene scene =
                new Scene(
                        scrollPane,
                        550,
                        600
                );

        reviewStage.setScene(
                scene
        );

        reviewStage.showAndWait();
    }


    // =========================================================
    // UPDATE STAR DISPLAY
    // =========================================================

    private void updateStarDisplay(
            Button[] stars,
            int rating) {

        for (int i = 0;
             i < stars.length;
             i++) {

            if (i < rating) {

                stars[i].setStyle(
                        "-fx-background-color: transparent;"
                                + "-fx-text-fill: #f59e0b;"
                                + "-fx-font-size: 32px;"
                                + "-fx-padding: 0;"
                                + "-fx-cursor: hand;"
                );

            } else {

                stars[i].setStyle(
                        "-fx-background-color: transparent;"
                                + "-fx-text-fill: #cbd5e1;"
                                + "-fx-font-size: 32px;"
                                + "-fx-padding: 0;"
                                + "-fx-cursor: hand;"
                );
            }
        }
    }


    // =========================================================
    // SUCCESS MESSAGE
    // =========================================================

    private void showSuccessMessage(
            String message) {

        Stage successStage =
                new Stage();

        successStage.initOwner(stage);

        successStage.initModality(
                Modality.WINDOW_MODAL
        );

        successStage.setTitle(
                "Review Submitted"
        );


        VBox root =
                new VBox(15);

        root.setAlignment(
                Pos.CENTER
        );

        root.setPadding(
                new Insets(30)
        );

        root.setPrefWidth(400);


        Label icon =
                new Label("✓");

        icon.setStyle(
                "-fx-font-size: 42px;"
                        + "-fx-text-fill: #16a34a;"
                        + "-fx-font-weight: bold;"
        );


        Label label =
                new Label(message);

        label.setWrapText(true);

        label.setAlignment(
                Pos.CENTER
        );

        label.setStyle(
                "-fx-font-size: 15px;"
                        + "-fx-text-fill: #334155;"
        );


        Button ok =
                PatientUI.button(
                        "OK",
                        successStage::close
                );


        root.getChildren().addAll(
                icon,
                label,
                ok
        );


        successStage.setScene(

                new Scene(
                        root,
                        450,
                        250
                )
        );

        successStage.showAndWait();
    }


    // =========================================================
    // REFRESH APPOINTMENTS
    // =========================================================

    private void refreshAppointments() {

        stage.setScene(

                new Appointments(stage)
                        .getScene()
        );

        stage.show();

        if (!stage.isMaximized()) {

            stage.setMaximized(true);
        }
    }


    // =========================================================
    // APPOINTMENT DETAILS
    // =========================================================

    private void showAppointmentDetails(
            Appointment appointment) {

        VBox content =
                new VBox(20);

        content.setPadding(
                new Insets(20)
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
                "-fx-font-size: 26px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #0f172a;"
        );

        content.getChildren().add(
                title
        );


        // =====================================================
        // APPOINTMENT INFORMATION
        // =====================================================

        VBox appointmentDetails =
                PatientUI.card(
                        "Appointment Information"
                );


        String bookingType =
                "HOSPITAL".equalsIgnoreCase(
                        appointment.getBookingType()
                )

                        ? "Hospital Appointment"

                        : "Doctor Appointment";


        appointmentDetails.getChildren().addAll(

                detailLabel(
                        "Type",
                        bookingType
                ),

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


        content.getChildren().add(
                appointmentDetails
        );


        // =====================================================
        // HOSPITAL APPOINTMENT
        // =====================================================

        if ("HOSPITAL".equalsIgnoreCase(
                appointment.getBookingType()
        )) {

            VBox hospitalDetails =
                    PatientUI.card(
                            "Hospital Details"
                    );


            hospitalDetails.getChildren().addAll(

                    detailLabel(
                            "Hospital",
                            appointment.getHospitalName()
                    ),

                    detailLabel(
                            "Specialty",
                            appointment.getSpecialty()
                    )
            );


            content.getChildren().add(
                    hospitalDetails
            );
        }


        // =====================================================
        // DOCTOR APPOINTMENT
        // =====================================================

        else {

            loadDoctorDetails(
                    appointment,
                    content
            );
        }


        // =====================================================
        // REVIEW BUTTON
        // =====================================================

        if ("COMPLETED".equalsIgnoreCase(

                safe(
                        appointment.getStatus(),
                        ""
                )
        )) {

            Button reviewButton =
                    createReviewButton(
                            appointment
                    );

            content.getChildren().add(
                    reviewButton
            );
        }


        // =====================================================
        // BACK BUTTON
        // =====================================================

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

                        "Appointments",

                        "Appointment Details",

                        "View appointment and doctor information.",

                        content
                )
        );

        stage.show();

        if (!stage.isMaximized()) {

            stage.setMaximized(true);
        }
    }


    // =========================================================
    // LOAD DOCTOR DETAILS
    // =========================================================

    private void loadDoctorDetails(
            Appointment appointment,
            VBox content) {

        String doctorUid =
                safe(
                        appointment.getDoctorUid(),
                        ""
                );


        if (doctorUid.isBlank()) {

            VBox unavailable =
                    PatientUI.card(
                            "Doctor Details"
                    );

            unavailable.getChildren().add(

                    errorLabel(
                            "Doctor information is not available."
                    )
            );

            content.getChildren().add(
                    unavailable
            );

            return;
        }


        try {

            DoctorProfile doctorProfile =
                    doctorDAO.getDoctorProfile(
                            doctorUid
                    );


            VBox doctorDetails =
                    PatientUI.card(
                            "Doctor Details"
                    );


            String doctorName =
                    buildDoctorName(
                            doctorProfile,
                            appointment
                    );


            String specialization =
                    safe(
                            doctorProfile != null
                                    ? doctorProfile.getSpecialization()
                                    : null,

                            safe(
                                    appointment.getSpecialty(),
                                    "Not available"
                            )
                    );


            String experience =
                    safe(
                            doctorProfile != null
                                    ? doctorProfile.getExperience()
                                    : null,

                            "Not available"
                    );


            String hospitalAffiliation =
                    safe(
                            doctorProfile != null
                                    ? doctorProfile.getHospitalAffiliation()
                                    : null,

                            "Not available"
                    );


            String phone =
                    safe(
                            doctorProfile != null
                                    ? doctorProfile.getPhone()
                                    : null,

                            "Not available"
                    );


            doctorDetails.getChildren().addAll(

                    detailLabel(
                            "Doctor",
                            doctorName
                    ),

                    detailLabel(
                            "Specialization",
                            specialization
                    ),

                    detailLabel(
                            "Experience",
                            experience
                    ),

                    detailLabel(
                            "Hospital Affiliation",
                            hospitalAffiliation
                    ),

                    detailLabel(
                            "Phone",
                            phone
                    )
            );


            content.getChildren().add(
                    doctorDetails
            );


            // =================================================
            // CONSULTATION INFORMATION
            // =================================================

            VBox consultationDetails =
                    PatientUI.card(
                            "Consultation Information"
                    );


            consultationDetails.getChildren().addAll(

                    detailLabel(
                            "Appointment Date",
                            formatDate(
                                    appointment.getAppointmentDate()
                            )
                    ),

                    detailLabel(
                            "Appointment Time",
                            safe(
                                    appointment.getAppointmentTime(),
                                    "Not available"
                            )
                    ),

                    detailLabel(
                            "Specialty",
                            safe(
                                    appointment.getSpecialty(),
                                    specialization
                            )
                    ),

                    detailLabel(
                            "Reason for Visit",
                            safe(
                                    appointment.getReason(),
                                    "Not available"
                            )
                    )
            );


            content.getChildren().add(
                    consultationDetails
            );


        } catch (Exception e) {

            System.err.println(
                    "Unable to load doctor details: "
                            + e.getMessage()
            );


            VBox errorCard =
                    PatientUI.card(
                            "Doctor Details"
                    );


            errorCard.getChildren().addAll(

                    detailLabel(
                            "Doctor",
                            safe(
                                    appointment.getDoctorName(),
                                    "Doctor"
                            )
                    ),

                    detailLabel(
                            "Specialty",
                            safe(
                                    appointment.getSpecialty(),
                                    "Not available"
                            )
                    ),

                    detailLabel(
                            "Appointment Date",
                            formatDate(
                                    appointment.getAppointmentDate()
                            )
                    ),

                    detailLabel(
                            "Appointment Time",
                            safe(
                                    appointment.getAppointmentTime(),
                                    "Not available"
                            )
                    ),

                    errorLabel(
                            "Some additional doctor information "
                                    + "could not be loaded."
                    )
            );


            content.getChildren().add(
                    errorCard
            );
        }
    }


    // =========================================================
    // BUILD DOCTOR NAME
    // =========================================================

    private String buildDoctorName(
            DoctorProfile doctorProfile,
            Appointment appointment) {

        if (doctorProfile == null) {

            return safe(
                    appointment.getDoctorName(),
                    "Doctor"
            );
        }


        String firstName =
                safe(
                        doctorProfile.getFirstName(),
                        ""
                );


        String lastName =
                safe(
                        doctorProfile.getLastName(),
                        ""
                );


        String fullName =
                (firstName
                        + " "
                        + lastName)
                        .trim();


        if (fullName.isBlank()) {

            return safe(
                    appointment.getDoctorName(),
                    "Doctor"
            );
        }


        if (!fullName
                .toLowerCase()
                .startsWith("dr.")) {

            fullName =
                    "Dr. "
                            + fullName;
        }


        return fullName;
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
                "-fx-font-size: 15px;"
                        + "-fx-text-fill: #334155;"
        );

        return label;
    }


    // =========================================================
    // EMPTY LABEL
    // =========================================================

    private Label emptyLabel(
            String message) {

        Label label =
                new Label(message);

        label.setWrapText(true);

        label.setStyle(
                "-fx-text-fill: #64748b;"
                        + "-fx-font-size: 14px;"
        );

        return label;
    }


    // =========================================================
    // ERROR LABEL
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
                "-fx-text-fill: #dc2626;"
                        + "-fx-font-size: 14px;"
        );

        return label;
    }


    // =========================================================
    // SAFE
    // =========================================================

    private String safe(
            String value,
            String fallback) {

        if (value == null
                || value.isBlank()) {

            return fallback;
        }

        return value;
    }


    // =========================================================
    // DATE PARSER
    // =========================================================

    private LocalDate parseDate(
            String date) {

        if (date == null
                || date.isBlank()) {

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
    // DATE FORMAT
    // =========================================================

    private String formatDate(
            String date) {

        if (date == null
                || date.isBlank()) {

            return "Date not available";
        }

        try {

            LocalDate localDate =
                    LocalDate.parse(
                            date
                    );

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
                getClass().getResource(
                        path
                );


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
    // BACK TO APPOINTMENTS
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