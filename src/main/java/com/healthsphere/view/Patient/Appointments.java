package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

        BorderPane root =
                new BorderPane();

        root.setLeft(createSidebar());
        root.setTop(createHeader());

        VBox content =
                new VBox(22);

        content.setPadding(
                new Insets(28)
        );

        content.setStyle(
                "-fx-background-color: #f1f5f9;"
        );

        Label title =
                new Label("Appointments");

        title.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label subtitle =
                new Label(
                        "Manage your upcoming and previous healthcare appointments."
                );

        subtitle.setStyle(
                "-fx-text-fill: #64748b;" +
                "-fx-font-size: 15px;"
        );

        VBox heading =
                new VBox(
                        5,
                        title,
                        subtitle
                );

        // -------------------------------------------------
        // TOP IMAGE
        // -------------------------------------------------

        HBox images =
                new HBox(15);

        images.getChildren().addAll(
                appointmentImage(
                        "/images/appointments/appointment1.jpg"
                ),
                appointmentImage(
                        "/images/appointments/appointment2.jpg"
                ),
                appointmentImage(
                        "/images/appointments/appointment3.jpg"
                ),
                appointmentImage(
                        "/images/appointments/appointment4.jpg"
                )
        );

        // -------------------------------------------------
        // BOOK APPOINTMENT
        // -------------------------------------------------

        VBox bookingCard =
                card("Book a New Appointment");

        Label bookingText =
                new Label(
                        "Need to see a doctor? Find an available specialist and book your appointment."
                );

        bookingText.setWrapText(true);

        bookingText.setStyle(
                "-fx-text-fill: #64748b;"
        );

        Button book =
                button(
                        "Book Appointment",
                        this::showBookAppointment
                );

        bookingCard.getChildren().addAll(
                bookingText,
                book
        );

        // -------------------------------------------------
        // UPCOMING
        // -------------------------------------------------

        VBox upcoming =
                card("Upcoming Appointment");

        upcoming.getChildren().add(
                appointmentCard(
                        "Dr. Sarah Jenkins",
                        "Cardiology",
                        "Tomorrow",
                        "10:00 AM",
                        "Apollo Hospitals",
                        "/images/appointments/appointment1.jpg"
                )
        );

        // -------------------------------------------------
        // PREVIOUS
        // -------------------------------------------------

        VBox previous =
                card("Previous Appointments");

        previous.getChildren().addAll(

                appointmentCard(
                        "Dr. Michael Brown",
                        "General Medicine",
                        "12 August 2026",
                        "11:30 AM",
                        "Fortis Healthcare",
                        "/images/appointments/appointment2.jpg"
                ),

                appointmentCard(
                        "Dr. Emily Wilson",
                        "Dermatology",
                        "05 August 2026",
                        "02:00 PM",
                        "Max Healthcare",
                        "/images/appointments/appointment3.jpg"
                ),

                appointmentCard(
                        "Dr. Robert Smith",
                        "Orthopedics",
                        "28 July 2026",
                        "09:30 AM",
                        "Manipal Hospitals",
                        "/images/appointments/appointment4.jpg"
                )
        );

        content.getChildren().addAll(
                heading,
                images,
                bookingCard,
                upcoming,
                previous
        );

        ScrollPane scroll =
                new ScrollPane(content);

        scroll.setFitToWidth(true);

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        root.setCenter(scroll);

        return new Scene(
                root,
                1440,
                900
        );
    }

    // =====================================================
    // APPOINTMENT CARD
    // =====================================================

    private HBox appointmentCard(
            String doctor,
            String specialty,
            String date,
            String time,
            String hospital,
            String imagePath
    ) {

        HBox row =
                new HBox(18);

        row.setPadding(
                new Insets(15)
        );

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setStyle(
                "-fx-background-color: #f8fafc;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #dbeafe;" +
                "-fx-border-radius: 12;"
        );

        ImageView image =
                loadImage(
                        imagePath,
                        150,
                        100
                );

        VBox details =
                new VBox(7);

        Label doctorLabel =
                new Label(doctor);

        doctorLabel.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label specialtyLabel =
                new Label(specialty);

        specialtyLabel.setStyle(
                "-fx-text-fill: #2563eb;" +
                "-fx-font-weight: bold;"
        );

        Label dateLabel =
                new Label(
                        date + " • " + time
                );

        Label hospitalLabel =
                new Label(hospital);

        hospitalLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        details.getChildren().addAll(
                doctorLabel,
                specialtyLabel,
                dateLabel,
                hospitalLabel
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button view =
                button(
                        "View",
                        () -> {}
                );

        row.getChildren().addAll(
                image,
                details,
                spacer,
                view
        );

        return row;
    }

    // =====================================================
    // IMAGE
    // =====================================================

    private VBox appointmentImage(
            String imagePath
    ) {

        VBox box =
                new VBox();

        ImageView image =
                loadImage(
                        imagePath,
                        270,
                        150
                );

        box.getChildren().add(image);

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 14;"
        );

        return box;
    }

    // =====================================================
    // COMMON CARD
    // =====================================================

    private VBox card(
            String title
    ) {

        VBox box =
                new VBox(14);

        box.setPadding(
                new Insets(20)
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 14;" +
                "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.08), 10, 0, 0, 3);"
        );

        Label heading =
                new Label(title);

        heading.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        box.getChildren().add(
                heading
        );

        return box;
    }

    // =====================================================
    // BUTTON
    // =====================================================

    private Button button(
            String text,
            Runnable action
    ) {

        Button button =
                new Button(text);

        button.setPrefHeight(42);

        button.setPadding(
                new Insets(8, 18, 8, 18)
        );

        button.setStyle(
                "-fx-background-color: #2563eb;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
        );

        button.setOnAction(
                e -> action.run()
        );

        return button;
    }

    // =====================================================
    // IMAGE LOADER
    // =====================================================

    private ImageView loadImage(
            String path,
            double width,
            double height
    ) {

        Image image =
                new Image(
                        getClass()
                                .getResourceAsStream(path)
                );

        ImageView view =
                new ImageView(image);

        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setPreserveRatio(false);

        return view;
    }

    // =====================================================
    // SIDEBAR
    // =====================================================

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
                new Label("✚ Health-Sphere");

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

                nav(
                        "▦",
                        "Dashboard",
                        this::showDashboard
                ),

                nav(
                        "⊞",
                        "Search Hospitals",
                        this::showSearchHospitals
                ),

                nav(
                        "▣",
                        "Appointments",
                        this::showAppointments
                ),

                nav(
                        "▧",
                        "Health Passport",
                        this::showHealthPassport
                ),

                nav(
                        "▱",
                        "Medical Records",
                        this::showMedicalRecords
                ),

                nav(
                        "♙",
                        "AI Health Assistant",
                        this::showAIHealthAssistant
                ),

                nav(
                        "⌖",
                        "Emergency Assistance",
                        this::showEmergencyAssistance
                ),

                spacer,

                nav(
                        "♧",
                        "Notifications",
                        this::showNotifications
                ),

                nav(
                        "⚙",
                        "Profile & Settings",
                        this::showProfileSettings
                )
        );

        return sidebar;
    }

    private HBox nav(
            String icon,
            String text,
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

    // =====================================================
    // HEADER
    // =====================================================

    private HBox createHeader() {

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_RIGHT
        );

        header.setPadding(
                new Insets(16, 28, 16, 28)
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

        header.getChildren().addAll(
                spacer,

                button(
                        "Notifications",
                        this::showNotifications
                ),

                button(
                        "Sarah",
                        this::showProfileSettings
                )
        );

        return header;
    }

    // =====================================================
    // DIRECT NAVIGATION
    // =====================================================

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

    private void showBookAppointment() {
        stage.setScene(
                new BookAppointment(stage).getScene()
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