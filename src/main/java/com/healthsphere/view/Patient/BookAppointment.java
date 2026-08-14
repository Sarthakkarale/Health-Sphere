package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
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
                new Label("Book an Appointment");

        title.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label subtitle =
                new Label(
                        "Choose a doctor, hospital, date and convenient time."
                );

        subtitle.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #64748b;"
        );

        VBox heading =
                new VBox(
                        5,
                        title,
                        subtitle
                );

        // -------------------------------------------------
        // IMAGES
        // -------------------------------------------------

        HBox images =
                new HBox(15);

        images.getChildren().addAll(
                imageCard(
                        "/images/appointments/appointment1.jpg"
                ),
                imageCard(
                        "/images/appointments/appointment2.jpg"
                ),
                imageCard(
                        "/images/appointments/appointment3.jpg"
                ),
                imageCard(
                        "/images/appointments/appointment4.jpg"
                )
        );

        // -------------------------------------------------
        // FORM CARD
        // -------------------------------------------------

        VBox form =
                card("Appointment Details");

        TextField patientName =
                field("Enter patient name");

        ComboBox<String> doctor =
                new ComboBox<>();

        doctor.getItems().addAll(
                "Dr. Sarah Jenkins",
                "Dr. Michael Brown",
                "Dr. Emily Wilson",
                "Dr. Robert Smith"
        );

        doctor.setPromptText(
                "Select doctor"
        );

        doctor.setPrefHeight(43);
        doctor.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> specialty =
                new ComboBox<>();

        specialty.getItems().addAll(
                "Cardiology",
                "General Medicine",
                "Dermatology",
                "Orthopedics",
                "Neurology",
                "Pediatrics"
        );

        specialty.setPromptText(
                "Select specialty"
        );

        specialty.setPrefHeight(43);
        specialty.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> hospital =
                new ComboBox<>();

        hospital.getItems().addAll(
                "Apollo Hospitals",
                "Fortis Healthcare",
                "Max Healthcare",
                "Manipal Hospitals"
        );

        hospital.setPromptText(
                "Select hospital"
        );

        hospital.setPrefHeight(43);
        hospital.setMaxWidth(Double.MAX_VALUE);

        DatePicker date =
                new DatePicker();

        date.setPromptText(
                "Select appointment date"
        );

        date.setPrefHeight(43);
        date.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> time =
                new ComboBox<>();

        time.getItems().addAll(
                "09:00 AM",
                "09:30 AM",
                "10:00 AM",
                "10:30 AM",
                "11:00 AM",
                "11:30 AM",
                "02:00 PM",
                "02:30 PM",
                "03:00 PM",
                "03:30 PM",
                "04:00 PM"
        );

        time.setPromptText(
                "Select time"
        );

        time.setPrefHeight(43);
        time.setMaxWidth(Double.MAX_VALUE);

        TextArea reason =
                new TextArea();

        reason.setPromptText(
                "Briefly describe the reason for your visit..."
        );

        reason.setPrefRowCount(4);

        reason.setWrapText(true);

        reason.setStyle(
                "-fx-background-color: #f8fafc;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        VBox left =
                new VBox(
                        7,
                        label("Patient Name"),
                        patientName,

                        label("Doctor"),
                        doctor,

                        label("Specialty"),
                        specialty
                );

        VBox right =
                new VBox(
                        7,
                        label("Hospital"),
                        hospital,

                        label("Date"),
                        date,

                        label("Time"),
                        time
                );

        HBox row =
                new HBox(20);

        HBox.setHgrow(
                left,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                right,
                Priority.ALWAYS
        );

        row.getChildren().addAll(
                left,
                right
        );

        Label reasonLabel =
                label("Reason for Visit");

        Button confirm =
                button(
                        "Confirm Appointment",
                        () -> confirmAppointment(
                                patientName,
                                doctor,
                                specialty,
                                hospital,
                                date,
                                time,
                                reason
                        )
                );

        Button cancel =
                secondaryButton(
                        "Cancel",
                        this::showAppointments
                );

        HBox actions =
                new HBox(
                        12,
                        confirm,
                        cancel
                );

        form.getChildren().addAll(
                row,
                reasonLabel,
                reason,
                actions
        );

        content.getChildren().addAll(
                heading,
                images,
                form
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
    // CONFIRM
    // =====================================================

    private void confirmAppointment(
            TextField patientName,
            ComboBox<String> doctor,
            ComboBox<String> specialty,
            ComboBox<String> hospital,
            DatePicker date,
            ComboBox<String> time,
            TextArea reason
    ) {

        if (
                patientName.getText().trim().isEmpty()
                        ||
                doctor.getValue() == null
                        ||
                specialty.getValue() == null
                        ||
                hospital.getValue() == null
                        ||
                date.getValue() == null
                        ||
                time.getValue() == null
        ) {

            showMessage(
                    "Please complete all required appointment details."
            );

            return;
        }

        showMessage(
                "Appointment confirmed successfully!"
        );
    }

    private void showMessage(
            String message
    ) {

        BorderPane root =
                new BorderPane();

        root.setLeft(createSidebar());
        root.setTop(createHeader());

        VBox content =
                new VBox(20);

        content.setAlignment(
                Pos.CENTER
        );

        content.setPadding(
                new Insets(40)
        );

        content.setStyle(
                "-fx-background-color: #f1f5f9;"
        );

        Label icon =
                new Label("✓");

        icon.setStyle(
                "-fx-font-size: 60px;" +
                "-fx-text-fill: #16a34a;" +
                "-fx-font-weight: bold;"
        );

        Label title =
                new Label(
                        "Appointment Confirmed"
                );

        title.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label description =
                new Label(message);

        description.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-text-fill: #64748b;"
        );

        Button appointments =
                button(
                        "View Appointments",
                        this::showAppointments
                );

        content.getChildren().addAll(
                icon,
                title,
                description,
                appointments
        );

        root.setCenter(content);

        stage.setScene(
                new Scene(
                        root,
                        1440,
                        900
                )
        );
    }

    // =====================================================
    // FORM HELPERS
    // =====================================================

    private TextField field(
            String prompt
    ) {

        TextField field =
                new TextField();

        field.setPromptText(
                prompt
        );

        field.setPrefHeight(43);

        field.setStyle(
                "-fx-background-color: #f8fafc;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        return field;
    }

    private Label label(
            String text
    ) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #334155;"
        );

        return label;
    }

    // =====================================================
    // IMAGE CARD
    // =====================================================

    private VBox imageCard(
            String path
    ) {

        VBox box =
                new VBox();

        ImageView image =
                loadImage(
                        path,
                        270,
                        145
                );

        box.getChildren().add(
                image
        );

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 14;"
        );

        return box;
    }

    // =====================================================
    // CARD
    // =====================================================

    private VBox card(
            String title
    ) {

        VBox box =
                new VBox(14);

        box.setPadding(
                new Insets(22)
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

    private Button secondaryButton(
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
                "-fx-background-color: #e2e8f0;" +
                "-fx-text-fill: #334155;" +
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
    // IMAGE
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