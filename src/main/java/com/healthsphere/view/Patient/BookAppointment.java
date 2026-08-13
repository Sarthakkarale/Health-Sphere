package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BookAppointment {

    private final Stage stage;

    public BookAppointment(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {

        BorderPane root = new BorderPane();

        root.setLeft(createSidebar());
        root.setTop(createHeader());

        VBox content = new VBox(22);

        content.setPadding(
                new Insets(28)
        );

        content.setStyle(
                "-fx-background-color: #f8fafc;"
        );

        // ---------------------------------------------------------
        // HEADING
        // ---------------------------------------------------------

        Label title = new Label(
                "Book Appointment"
        );

        title.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label subtitle = new Label(
                "Choose a doctor, specialty and convenient appointment time."
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
        // BOOKING FORM CARD
        // ---------------------------------------------------------

        VBox formCard = createCard(
                "#eff6ff",
                "#bfdbfe"
        );

        Label formTitle = new Label(
                "Appointment Details"
        );

        formTitle.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1e3a8a;"
        );

        Label formSubtitle = new Label(
                "Please provide the required information."
        );

        formSubtitle.setStyle(
                "-fx-text-fill: #475569;"
        );

        // ---------------------------------------------------------
        // DOCTOR
        // ---------------------------------------------------------

        Label doctorLabel =
                fieldLabel("Select Doctor");

        ComboBox<String> doctorCombo =
                new ComboBox<>();

        doctorCombo.setPromptText(
                "Choose a doctor"
        );

        doctorCombo.getItems().addAll(
                "Dr. Sarah Jenkins - Cardiology",
                "Dr. Michael Brown - General Medicine",
                "Dr. Emily Wilson - Dermatology",
                "Dr. Robert Smith - Orthopedics",
                "Dr. Lisa Anderson - Neurology"
        );

        doctorCombo.setMaxWidth(
                Double.MAX_VALUE
        );

        doctorCombo.setPrefHeight(42);

        // ---------------------------------------------------------
        // SPECIALTY
        // ---------------------------------------------------------

        Label specialtyLabel =
                fieldLabel("Specialty");

        ComboBox<String> specialtyCombo =
                new ComboBox<>();

        specialtyCombo.setPromptText(
                "Select specialty"
        );

        specialtyCombo.getItems().addAll(
                "Cardiology",
                "General Medicine",
                "Dermatology",
                "Orthopedics",
                "Neurology",
                "Pediatrics",
                "ENT",
                "Ophthalmology"
        );

        specialtyCombo.setMaxWidth(
                Double.MAX_VALUE
        );

        specialtyCombo.setPrefHeight(42);

        // ---------------------------------------------------------
        // DATE
        // ---------------------------------------------------------

        Label dateLabel =
                fieldLabel("Appointment Date");

        DatePicker datePicker =
                new DatePicker();

        datePicker.setPromptText(
                "Select date"
        );

        datePicker.setMaxWidth(
                Double.MAX_VALUE
        );

        datePicker.setPrefHeight(42);

        // ---------------------------------------------------------
        // TIME
        // ---------------------------------------------------------

        Label timeLabel =
                fieldLabel("Preferred Time");

        ComboBox<String> timeCombo =
                new ComboBox<>();

        timeCombo.setPromptText(
                "Select time"
        );

        timeCombo.getItems().addAll(
                "09:00 AM",
                "10:00 AM",
                "11:00 AM",
                "12:00 PM",
                "02:00 PM",
                "03:00 PM",
                "04:00 PM",
                "05:00 PM"
        );

        timeCombo.setMaxWidth(
                Double.MAX_VALUE
        );

        timeCombo.setPrefHeight(42);

        // ---------------------------------------------------------
        // APPOINTMENT TYPE
        // ---------------------------------------------------------

        Label typeLabel =
                fieldLabel("Appointment Type");

        ComboBox<String> typeCombo =
                new ComboBox<>();

        typeCombo.setPromptText(
                "Select appointment type"
        );

        typeCombo.getItems().addAll(
                "In-Person Consultation",
                "Video Consultation",
                "Follow-up Visit"
        );

        typeCombo.setMaxWidth(
                Double.MAX_VALUE
        );

        typeCombo.setPrefHeight(42);

        // ---------------------------------------------------------
        // REASON
        // ---------------------------------------------------------

        Label reasonLabel =
                fieldLabel("Reason for Visit");

        TextArea reasonArea =
                new TextArea();

        reasonArea.setPromptText(
                "Briefly describe the reason for your appointment..."
        );

        reasonArea.setPrefRowCount(4);

        reasonArea.setWrapText(true);

        reasonArea.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        // ---------------------------------------------------------
        // FORM GRID
        // ---------------------------------------------------------

        GridPane grid = new GridPane();

        grid.setHgap(18);
        grid.setVgap(12);

        ColumnConstraintsHelper.addColumns(
                grid
        );

        grid.add(
                doctorLabel,
                0,
                0
        );

        grid.add(
                doctorCombo,
                0,
                1
        );

        grid.add(
                specialtyLabel,
                1,
                0
        );

        grid.add(
                specialtyCombo,
                1,
                1
        );

        grid.add(
                dateLabel,
                0,
                2
        );

        grid.add(
                datePicker,
                0,
                3
        );

        grid.add(
                timeLabel,
                1,
                2
        );

        grid.add(
                timeCombo,
                1,
                3
        );

        grid.add(
                typeLabel,
                0,
                4
        );

        grid.add(
                typeCombo,
                0,
                5
        );

        grid.add(
                reasonLabel,
                1,
                4
        );

        grid.add(
                reasonArea,
                1,
                5
        );

        // ---------------------------------------------------------
        // BUTTONS
        // ---------------------------------------------------------

        Button backButton =
                new Button("Back");

        backButton.setPrefHeight(42);

        backButton.setStyle(
                "-fx-background-color: white;" +
                "-fx-text-fill: #475569;" +
                "-fx-font-weight: bold;" +
                "-fx-border-color: #cbd5e1;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 0 22;"
        );

        backButton.setOnAction(e -> {

            stage.setScene(
                    new Appointments(stage).getScene()
            );

            stage.show();
        });

        Button confirmButton =
                new Button("Confirm Appointment");

        confirmButton.setPrefHeight(42);

        confirmButton.setStyle(
                "-fx-background-color: #2563eb;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 0 22;"
        );

        confirmButton.setOnAction(e -> {

            if (
                    doctorCombo.getValue() == null ||
                    specialtyCombo.getValue() == null ||
                    datePicker.getValue() == null ||
                    timeCombo.getValue() == null ||
                    typeCombo.getValue() == null
            ) {

                System.out.println(
                        "Please complete all required fields."
                );

                return;
            }

            System.out.println(
                    "Appointment successfully booked."
            );

            stage.setScene(
                    new Appointments(stage).getScene()
            );

            stage.show();
        });

        HBox buttons = new HBox(
                12,
                backButton,
                confirmButton
        );

        buttons.setAlignment(
                Pos.CENTER_RIGHT
        );

        formCard.getChildren().addAll(
                formTitle,
                formSubtitle,
                grid,
                buttons
        );

        // ---------------------------------------------------------
        // INFORMATION CARD
        // ---------------------------------------------------------

        VBox informationCard = createCard(
                "#f0fdf4",
                "#bbf7d0"
        );

        Label informationTitle = new Label(
                "Before You Book"
        );

        informationTitle.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #166534;"
        );

        Label informationText = new Label(
                "Please arrive 10–15 minutes before your scheduled appointment. " +
                "Keep your medical records and current medication information available."
        );

        informationText.setWrapText(true);

        informationText.setStyle(
                "-fx-text-fill: #475569;"
        );

        informationCard.getChildren().addAll(
                informationTitle,
                informationText
        );

        content.getChildren().addAll(
                heading,
                formCard,
                informationCard
        );

        root.setCenter(content);

        return new Scene(
                root,
                1440,
                900
        );
    }

    // =============================================================
    // FIELD LABEL
    // =============================================================

    private Label fieldLabel(
            String text
    ) {

        Label label = new Label(text);

        label.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #334155;"
        );

        return label;
    }

    // =============================================================
    // CARD
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
                        () -> navigateDashboard()
                ),

                navButton(
                        "⊞",
                        "Search Hospitals",
                        () -> navigateSearchHospitals()
                ),

                navButton(
                        "▣",
                        "Appointments",
                        () -> {}
                ),

                navButton(
                        "▧",
                        "Health Passport",
                        () -> navigateHealthPassport()
                ),

                navButton(
                        "▱",
                        "Medical Records",
                        () -> navigateMedicalRecords()
                ),

                navButton(
                        "♙",
                        "AI Health Assistant",
                        () -> navigateAI()
                ),

                navButton(
                        "⌖",
                        "Emergency Assistance",
                        () -> navigateEmergency()
                ),

                spacer,

                navButton(
                        "♧",
                        "Notifications",
                        () -> navigateNotifications()
                ),

                navButton(
                        "⚙",
                        "Profile & Settings",
                        () -> navigateProfile()
                )
        );

        return sidebar;
    }

    // =============================================================
    // NAVIGATION
    // =============================================================

    private void navigateDashboard() {

        stage.setScene(
                new Dashboard(stage).getScene()
        );

        stage.show();
    }

    private void navigateSearchHospitals() {

        stage.setScene(
                new SearchHospitals(stage).getScene()
        );

        stage.show();
    }

    private void navigateHealthPassport() {

        stage.setScene(
                new HealthPassport(stage).getScene()
        );

        stage.show();
    }

    private void navigateMedicalRecords() {

        stage.setScene(
                new MedicalRecords(stage).getScene()
        );

        stage.show();
    }

    private void navigateAI() {

        stage.setScene(
                new AiHealthAssistant(stage).getScene()
        );

        stage.show();
    }

    private void navigateEmergency() {

        stage.setScene(
                new EmergencyAssistance(stage).getScene()
        );

        stage.show();
    }

    private void navigateNotifications() {

        stage.setScene(
                new Notifications(stage).getScene()
        );

        stage.show();
    }

    private void navigateProfile() {

        stage.setScene(
                new ProfileSettings(stage).getScene()
        );

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

    // =============================================================
    // GRID COLUMN HELPER
    // =============================================================

    private static class ColumnConstraintsHelper {

        static void addColumns(
                GridPane grid
        ) {

            javafx.scene.layout.ColumnConstraints first =
                    new javafx.scene.layout.ColumnConstraints();

            javafx.scene.layout.ColumnConstraints second =
                    new javafx.scene.layout.ColumnConstraints();

            first.setPercentWidth(50);
            second.setPercentWidth(50);

            grid.getColumnConstraints().addAll(
                    first,
                    second
            );
        }
    }
}