package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EmergencyAssistance {

    private final Stage stage;

    public EmergencyAssistance(Stage stage) {
        this.stage = stage;
    }

    // =========================================================
    // MAIN SCENE
    // =========================================================

    public Scene getScene() {

        VBox content = new VBox(22);
        content.setPadding(new Insets(25));

        // =====================================================
        // EMERGENCY BANNER
        // =====================================================

        VBox emergencyBanner = new VBox(15);

        emergencyBanner.setPadding(new Insets(25));
        emergencyBanner.setAlignment(Pos.CENTER_LEFT);

        emergencyBanner.setStyle(
                "-fx-background-color: linear-gradient(to right, #fee2e2, #fecaca);" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: #fca5a5;" +
                "-fx-border-radius: 18;"
        );

        Label emergencyTitle = new Label(
                "🚨 Need Emergency Help?"
        );

        emergencyTitle.setStyle(
                "-fx-font-size: 25px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #991b1b;"
        );

        Label emergencyText = new Label(
                "If you are experiencing a life-threatening emergency, " +
                "contact emergency services immediately."
        );

        emergencyText.setWrapText(true);

        emergencyText.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #7f1d1d;"
        );

        Button emergencyButton = PatientUI.button(
                "🚨 CALL EMERGENCY SERVICES",
                this::openEmergencyNumbers
        );

        emergencyButton.setPrefHeight(48);

        emergencyButton.setStyle(
                "-fx-background-color: #dc2626;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 12 24;" +
                "-fx-cursor: hand;"
        );

        emergencyBanner.getChildren().addAll(
                emergencyTitle,
                emergencyText,
                emergencyButton
        );

        // =====================================================
        // MAIN IMAGE GALLERY
        // =====================================================

        GridPane imageGrid = createImageGallery();

        // =====================================================
        // EMERGENCY SERVICES
        // =====================================================

        VBox servicesCard = PatientUI.coloredCard(
                "🚑 Emergency Services",
                "#fee2e2"
        );

        servicesCard.getChildren().addAll(

                emergencyService(
                        "🚑",
                        "Ambulance",
                        "Request emergency medical transportation.",
                        () -> openAmbulanceScreen()
                ),

                emergencyService(
                        "🏥",
                        "Nearest Hospital",
                        "Find the nearest hospital and emergency department.",
                        () -> stage.setScene(
                                new SearchHospitals(stage).getScene()
                        )
                ),

                emergencyService(
                        "👨‍⚕",
                        "Doctor Support",
                        "Search for doctors and healthcare professionals.",
                        () -> openDoctorSupport()
                ),

                emergencyService(
                        "💊",
                        "Medical Information",
                        "Learn what to do during common medical emergencies.",
                        () -> openMedicalInformation()
                )
        );

        // =====================================================
        // QUICK ACTIONS
        // =====================================================

        VBox actionsCard = PatientUI.coloredCard(
                "⚡ Quick Actions",
                "#dbeafe"
        );

        HBox actions = new HBox(12);

        actions.setAlignment(Pos.CENTER_LEFT);

        Button hospitals = PatientUI.button(
                "🏥 Find Hospitals",
                () -> stage.setScene(
                        new SearchHospitals(stage).getScene()
                )
        );

        Button appointments = PatientUI.button(
                "📅 Appointments",
                () -> stage.setScene(
                        new Appointments(stage).getScene()
                )
        );

        Button healthPassport = PatientUI.button(
                "🩺 Health Passport",
                () -> stage.setScene(
                        new HealthPassport(stage).getScene()
                )
        );

        Button medicalRecords = PatientUI.button(
                "📋 Medical Records",
                () -> stage.setScene(
                        new MedicalRecords(stage).getScene()
                )
        );

        actions.getChildren().addAll(
                hospitals,
                appointments,
                healthPassport,
                medicalRecords
        );

        actionsCard.getChildren().add(actions);

        // =====================================================
        // ADD CONTENT
        // =====================================================

        content.getChildren().addAll(
                emergencyBanner,
                imageGrid,
                servicesCard,
                actionsCard
        );

        // =====================================================
        // SCROLL
        // =====================================================

        ScrollPane scroll = new ScrollPane(content);

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

        // =====================================================
        // COMMON PATIENT UI
        // =====================================================

        return PatientUI.createScene(
                stage,
                "Emergency Assistance",
                "Emergency Assistance",
                "Get immediate help and access important emergency services.",
                scroll
        );
    }

    // =========================================================
    // IMAGE GALLERY
    // =========================================================

    private GridPane createImageGallery() {

        GridPane grid = new GridPane();

        grid.setHgap(18);
        grid.setVgap(18);

        grid.add(
                imageCard(
                        "/images/emergency/emergency1.jpg",
                        "Emergency Care"
                ),
                0,
                0
        );

        grid.add(
                imageCard(
                        "/images/emergency/emergency2.jpg",
                        "Ambulance Services"
                ),
                1,
                0
        );

        grid.add(
                imageCard(
                        "/images/emergency/emergency3.jpg",
                        "Emergency Department"
                ),
                0,
                1
        );

        grid.add(
                imageCard(
                        "/images/emergency/emergency4.jpg",
                        "Emergency Medical Support"
                ),
                1,
                1
        );

        return grid;
    }

    // =========================================================
    // SQUARE IMAGE CARD
    // =========================================================

    private VBox imageCard(
            String imagePath,
            String title
    ) {

        VBox card = new VBox();

        card.setPrefWidth(300);
        card.setMaxWidth(300);

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: #fecaca;" +
                "-fx-border-radius: 16;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 10, 0, 0, 3);"
        );

        ImageView imageView = createSquareImage(
                imagePath,
                280
        );

        VBox imageContainer = new VBox(imageView);

        imageContainer.setAlignment(Pos.CENTER);

        imageContainer.setPadding(
                new Insets(10)
        );

        Label titleLabel = new Label(title);

        titleLabel.setMaxWidth(
                Double.MAX_VALUE
        );

        titleLabel.setAlignment(
                Pos.CENTER
        );

        titleLabel.setPadding(
                new Insets(12)
        );

        titleLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #991b1b;"
        );

        card.getChildren().addAll(
                imageContainer,
                titleLabel
        );

        return card;
    }

    // =========================================================
    // SQUARE IMAGE LOADER
    // =========================================================

    private ImageView createSquareImage(
            String imagePath,
            double size
    ) {

        ImageView imageView = new ImageView();

        var resource = getClass().getResource(
                imagePath
        );

        if (resource == null) {

            System.err.println(
                    "Emergency image not found: " +
                    imagePath
            );

            imageView.setFitWidth(size);
            imageView.setFitHeight(size);

            return imageView;
        }

        Image image = new Image(
                resource.toExternalForm()
        );

        imageView.setImage(image);

        imageView.setFitWidth(size);
        imageView.setFitHeight(size);

        /*
         * Square display.
         *
         * PreserveRatio is false so every image occupies
         * exactly the square area instead of becoming a
         * wide image across the page.
         */

        imageView.setPreserveRatio(false);

        return imageView;
    }

    // =========================================================
    // EMERGENCY SERVICE ROW
    // =========================================================

    private HBox emergencyService(
            String icon,
            String title,
            String description,
            Runnable action
    ) {

        HBox row = new HBox(15);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(15)
        );

        row.setStyle(
                "-fx-background-color: rgba(255,255,255,0.95);" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #fecaca;" +
                "-fx-border-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);"
        );

        Label iconLabel = new Label(icon);

        iconLabel.setStyle(
                "-fx-font-size: 30px;"
        );

        VBox information = new VBox(4);

        HBox.setHgrow(
                information,
                Priority.ALWAYS
        );

        Label titleLabel = new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label descriptionLabel = new Label(
                description
        );

        descriptionLabel.setWrapText(true);

        descriptionLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        information.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );

        Button actionButton = new Button(
                "Open"
        );

        actionButton.setStyle(
                "-fx-background-color: #dc2626;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 9 17;" +
                "-fx-cursor: hand;"
        );

        actionButton.setOnAction(
                e -> action.run()
        );

        row.getChildren().addAll(
                iconLabel,
                information,
                actionButton
        );

        return row;
    }

    // =========================================================
    // EMERGENCY NUMBERS SCREEN
    // =========================================================

    private void openEmergencyNumbers() {

        VBox content = new VBox(20);

        content.setPadding(
                new Insets(25)
        );

        // =====================================================
        // HERO IMAGE
        // =====================================================

        VBox hero = createHeroImage(
                "/images/emergency/emergency6.jpg",
                "Emergency Services in India",
                "Important emergency contact numbers"
        );

        // =====================================================
        // WARNING
        // =====================================================

        VBox warning = PatientUI.coloredCard(
                "🚨 Important",
                "#fee2e2"
        );

        Label warningText = new Label(
                "For a life-threatening emergency, call the appropriate " +
                "emergency service immediately. Emergency numbers and " +
                "service availability may vary by location."
        );

        warningText.setWrapText(true);

        warningText.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #7f1d1d;"
        );

        warning.getChildren().add(
                warningText
        );

        // =====================================================
        // INDIA EMERGENCY NUMBERS
        // =====================================================

        GridPane numbers = new GridPane();

        numbers.setHgap(15);
        numbers.setVgap(15);

        numbers.add(
                emergencyNumberCard(
                        "🚨",
                        "National Emergency Number",
                        "112",
                        "Police, fire and medical emergency assistance"
                ),
                0,
                0
        );

        numbers.add(
                emergencyNumberCard(
                        "🚑",
                        "Ambulance",
                        "108",
                        "Emergency ambulance service in many parts of India"
                ),
                1,
                0
        );

        numbers.add(
                emergencyNumberCard(
                        "🚑",
                        "Ambulance / Medical",
                        "102",
                        "Ambulance and maternal/child health services in supported areas"
                ),
                0,
                1
        );

        numbers.add(
                emergencyNumberCard(
                        "👮",
                        "Police",
                        "100",
                        "Police emergency assistance"
                ),
                1,
                1
        );

        numbers.add(
                emergencyNumberCard(
                        "🔥",
                        "Fire & Rescue",
                        "101",
                        "Fire and rescue emergency assistance"
                ),
                0,
                2
        );

        numbers.add(
                emergencyNumberCard(
                        "👧",
                        "Child Helpline",
                        "1098",
                        "Emergency assistance and support for children"
                ),
                1,
                2
        );

        numbers.add(
                emergencyNumberCard(
                        "👩",
                        "Women Helpline",
                        "181",
                        "Women support and assistance in supported states"
                ),
                0,
                3
        );

        numbers.add(
                emergencyNumberCard(
                        "🆘",
                        "Disaster Management",
                        "1078",
                        "Disaster-related assistance through the national system"
                ),
                1,
                3
        );

        // =====================================================
        // BACK
        // =====================================================

        Button back = PatientUI.secondaryButton(
                "← Back to Emergency Assistance",
                () -> stage.setScene(
                        new EmergencyAssistance(stage)
                                .getScene()
                )
        );

        content.getChildren().addAll(
                hero,
                warning,
                numbers,
                back
        );

        ScrollPane scroll = new ScrollPane(
                content
        );

        scroll.setFitToWidth(true);

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scroll.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;"
        );

        stage.setScene(
                PatientUI.createScene(
                        stage,
                        "Emergency Services",
                        "Emergency Services",
                        "Important emergency contact numbers in India.",
                        scroll
                )
        );

        stage.show();
    }

    // =========================================================
    // EMERGENCY NUMBER CARD
    // =========================================================

    private VBox emergencyNumberCard(
            String icon,
            String title,
            String number,
            String description
    ) {

        VBox card = new VBox(8);

        card.setPrefWidth(390);

        card.setPadding(
                new Insets(18)
        );

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: #fecaca;" +
                "-fx-border-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 3);"
        );

        Label iconLabel = new Label(icon);

        iconLabel.setStyle(
                "-fx-font-size: 30px;"
        );

        Label titleLabel = new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #334155;"
        );

        Label numberLabel = new Label(number);

        numberLabel.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #dc2626;"
        );

        Label descriptionLabel = new Label(
                description
        );

        descriptionLabel.setWrapText(true);

        descriptionLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        card.getChildren().addAll(
                iconLabel,
                titleLabel,
                numberLabel,
                descriptionLabel
        );

        return card;
    }

    // =========================================================
    // AMBULANCE SCREEN
    // =========================================================

    private void openAmbulanceScreen() {

        VBox content = new VBox(22);

        content.setPadding(
                new Insets(25)
        );

        VBox hero = createHeroImage(
                "/images/emergency/emergency7.jpg",
                "Ambulance Assistance",
                "Get emergency medical transportation"
        );

        // =====================================================
        // NUMBERS
        // =====================================================

        VBox numbersCard = PatientUI.coloredCard(
                "🚑 Ambulance Emergency Numbers",
                "#fee2e2"
        );

        numbersCard.getChildren().addAll(

                emergencyNumberRow(
                        "108",
                        "Emergency Ambulance",
                        "Emergency ambulance service available in many parts of India."
                ),

                emergencyNumberRow(
                        "102",
                        "Ambulance / Medical Transport",
                        "Ambulance services available for supported medical and maternal/child health needs."
                ),

                emergencyNumberRow(
                        "112",
                        "National Emergency",
                        "Use 112 for integrated emergency assistance including medical emergencies."
                )
        );

        // =====================================================
        // AMBULANCE LIST
        // =====================================================

        VBox available = PatientUI.coloredCard(
                "🚑 Ambulance Services",
                "#dbeafe"
        );

        available.getChildren().addAll(

                ambulanceCard(
                        "Emergency Ambulance",
                        "24/7 emergency medical transportation",
                        "108"
                ),

                ambulanceCard(
                        "Medical Transport",
                        "Ambulance support for eligible medical transportation",
                        "102"
                ),

                ambulanceCard(
                        "National Emergency Response",
                        "Integrated emergency response",
                        "112"
                )
        );

        // =====================================================
        // INFORMATION
        // =====================================================

        VBox info = PatientUI.coloredCard(
                "📍 Before the Ambulance Arrives",
                "#fef3c7"
        );

        info.getChildren().addAll(
                bullet(
                        "Provide your exact location and nearby landmark."
                ),
                bullet(
                        "Keep your phone available for the emergency team."
                ),
                bullet(
                        "Keep medications and allergy information ready."
                ),
                bullet(
                        "Do not move a seriously injured person unless necessary for immediate safety."
                )
        );

        Button back = PatientUI.secondaryButton(
                "← Back to Emergency Assistance",
                () -> stage.setScene(
                        new EmergencyAssistance(stage)
                                .getScene()
                )
        );

        content.getChildren().addAll(
                hero,
                numbersCard,
                available,
                info,
                back
        );

        showScrollableScene(
                content,
                "Ambulance Assistance",
                "Ambulance services and emergency transportation information."
        );
    }

    // =========================================================
    // AMBULANCE ROW
    // =========================================================

    private HBox emergencyNumberRow(
            String number,
            String title,
            String description
    ) {

        HBox row = new HBox(15);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(14)
        );

        row.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #fecaca;" +
                "-fx-border-radius: 12;"
        );

        Label numberLabel = new Label(
                number
        );

        numberLabel.setStyle(
                "-fx-font-size: 24px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #dc2626;"
        );

        VBox text = new VBox(4);

        HBox.setHgrow(
                text,
                Priority.ALWAYS
        );

        Label titleLabel = new Label(
                title
        );

        titleLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label descriptionLabel = new Label(
                description
        );

        descriptionLabel.setWrapText(true);

        descriptionLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        text.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );

        row.getChildren().addAll(
                numberLabel,
                text
        );

        return row;
    }

    // =========================================================
    // AMBULANCE CARD
    // =========================================================

    private VBox ambulanceCard(
            String title,
            String description,
            String number
    ) {

        VBox card = new VBox(7);

        card.setPadding(
                new Insets(15)
        );

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 12;"
        );

        Label titleLabel = new Label(
                "🚑  " + title
        );

        titleLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1e3a8a;"
        );

        Label descriptionLabel = new Label(
                description
        );

        descriptionLabel.setWrapText(true);

        descriptionLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        Label numberLabel = new Label(
                "Emergency Number: " + number
        );

        numberLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #dc2626;"
        );

        card.getChildren().addAll(
                titleLabel,
                descriptionLabel,
                numberLabel
        );

        return card;
    }

    // =========================================================
    // DOCTOR SUPPORT SCREEN
    // =========================================================

    private void openDoctorSupport() {

        VBox content = new VBox(22);

        content.setPadding(
                new Insets(25)
        );

        VBox hero = createHeroImage(
                "/images/emergency/emergency8.jpg",
                "Doctor Support",
                "Find medical professionals who can help"
        );

        // =====================================================
        // SEARCH
        // =====================================================

        VBox searchCard = PatientUI.coloredCard(
                "🔎 Search Doctors",
                "#ede9fe"
        );

        TextField searchField = new TextField();

        searchField.setPromptText(
                "Search by doctor name or specialty..."
        );

        searchField.setPrefHeight(42);

        searchField.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #c4b5fd;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10;"
        );

        Button searchButton = PatientUI.button(
                "Search Doctors",
                () -> {
                    // This is a UI search screen.
                    // Connect this button to your doctor database
                    // when the Doctor module is available.
                }
        );

        searchCard.getChildren().addAll(
                searchField,
                searchButton
        );

        // =====================================================
        // SPECIALTIES
        // =====================================================

        VBox specialties = PatientUI.coloredCard(
                "👨‍⚕ Popular Medical Specialists",
                "#dbeafe"
        );

        specialties.getChildren().addAll(

                doctorSpecialty(
                        "🫀",
                        "Cardiologist",
                        "Heart and cardiovascular conditions"
                ),

                doctorSpecialty(
                        "🧠",
                        "Neurologist",
                        "Brain and nervous system conditions"
                ),

                doctorSpecialty(
                        "🫁",
                        "Pulmonologist",
                        "Respiratory and lung conditions"
                ),

                doctorSpecialty(
                        "🩺",
                        "General Physician",
                        "General medical evaluation and support"
                )
        );

        // =====================================================
        // EMERGENCY NOTICE
        // =====================================================

        VBox notice = PatientUI.coloredCard(
                "🚨 Emergency Notice",
                "#fee2e2"
        );

        Label noticeText = new Label(
                "Doctor search is intended for finding healthcare support. " +
                "For a life-threatening emergency, contact emergency services " +
                "instead of waiting for an appointment."
        );

        noticeText.setWrapText(true);

        noticeText.setStyle(
                "-fx-text-fill: #7f1d1d;" +
                "-fx-font-size: 14px;"
        );

        notice.getChildren().add(
                noticeText
        );

        Button back = PatientUI.secondaryButton(
                "← Back to Emergency Assistance",
                () -> stage.setScene(
                        new EmergencyAssistance(stage)
                                .getScene()
                )
        );

        content.getChildren().addAll(
                hero,
                searchCard,
                specialties,
                notice,
                back
        );

        showScrollableScene(
                content,
                "Doctor Support",
                "Search for doctors and medical specialists."
        );
    }

    // =========================================================
    // DOCTOR SPECIALTY
    // =========================================================

    private HBox doctorSpecialty(
            String icon,
            String title,
            String description
    ) {

        HBox row = new HBox(14);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(12)
        );

        row.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 10;"
        );

        Label iconLabel = new Label(
                icon
        );

        iconLabel.setStyle(
                "-fx-font-size: 26px;"
        );

        VBox text = new VBox(3);

        Label titleLabel = new Label(
                title
        );

        titleLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1e3a8a;"
        );

        Label descriptionLabel = new Label(
                description
        );

        descriptionLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        text.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );

        row.getChildren().addAll(
                iconLabel,
                text
        );

        return row;
    }

    // =========================================================
    // MEDICAL INFORMATION SCREEN
    // =========================================================

    private void openMedicalInformation() {

        VBox content = new VBox(22);

        content.setPadding(
                new Insets(25)
        );

        VBox hero = createHeroImage(
                "/images/emergency/emergency9.jpg",
                "Emergency Medical Information",
                "Basic first-response information for common emergencies"
        );

        // =====================================================
        // CPR
        // =====================================================

        VBox cpr = medicalInfoCard(
                "❤️ CPR & Cardiac Emergency",
                "If a person is unresponsive and not breathing normally, " +
                "call emergency services immediately. Begin CPR if you " +
                "are trained and follow instructions from the emergency dispatcher."
        );

        // =====================================================
        // BLEEDING
        // =====================================================

        VBox bleeding = medicalInfoCard(
                "🩸 Severe Bleeding",
                "Apply firm, direct pressure to the bleeding area using " +
                "clean cloth or gauze. Continue pressure and seek emergency " +
                "medical assistance for severe or uncontrolled bleeding."
        );

        // =====================================================
        // BURNS
        // =====================================================

        VBox burns = medicalInfoCard(
                "🔥 Burns",
                "Cool a minor burn with clean, cool running water. Do not " +
                "apply ice directly to the burn. Serious or extensive burns " +
                "require urgent professional medical care."
        );

        // =====================================================
        // CHOKING
        // =====================================================

        VBox choking = medicalInfoCard(
                "🫁 Choking",
                "If someone cannot breathe, speak or cough effectively, " +
                "call emergency services and provide appropriate first aid " +
                "if you are trained."
        );

        // =====================================================
        // FRACTURE
        // =====================================================

        VBox fracture = medicalInfoCard(
                "🦴 Suspected Fracture",
                "Keep the injured area as still as possible. Do not attempt " +
                "to straighten a visibly deformed limb. Seek professional medical care."
        );

        // =====================================================
        // STROKE
        // =====================================================

        VBox stroke = medicalInfoCard(
                "🧠 Possible Stroke",
                "Sudden facial weakness, arm weakness, speech difficulty, " +
                "confusion or other sudden neurological symptoms require urgent " +
                "emergency medical assessment."
        );

        // =====================================================
        // ALLERGIC REACTION
        // =====================================================

        VBox allergy = medicalInfoCard(
                "⚠ Severe Allergic Reaction",
                "Difficulty breathing, swelling of the face or throat, fainting " +
                "or rapidly worsening symptoms can be an emergency. Contact " +
                "emergency services immediately."
        );

        // =====================================================
        // POISONING
        // =====================================================

        VBox poisoning = medicalInfoCard(
                "☠ Poisoning",
                "If poisoning is suspected, seek emergency medical advice immediately. " +
                "Do not induce vomiting unless specifically instructed by a qualified professional."
        );

        VBox emergencyReminder = PatientUI.coloredCard(
                "🚨 Remember",
                "#fee2e2"
        );

        Label reminder = new Label(
                "This information is for basic emergency awareness only. " +
                "It does not replace professional medical advice or emergency care."
        );

        reminder.setWrapText(true);

        reminder.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #991b1b;"
        );

        emergencyReminder.getChildren().add(
                reminder
        );

        Button back = PatientUI.secondaryButton(
                "← Back to Emergency Assistance",
                () -> stage.setScene(
                        new EmergencyAssistance(stage)
                                .getScene()
                )
        );

        content.getChildren().addAll(
                hero,
                cpr,
                bleeding,
                burns,
                choking,
                fracture,
                stroke,
                allergy,
                poisoning,
                emergencyReminder,
                back
        );

        showScrollableScene(
                content,
                "Medical Information",
                "Basic emergency medical information and first-response guidance."
        );
    }

    // =========================================================
    // MEDICAL INFORMATION CARD
    // =========================================================

    private VBox medicalInfoCard(
            String title,
            String description
    ) {

        VBox card = new VBox(8);

        card.setPadding(
                new Insets(18)
        );

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #ddd6fe;" +
                "-fx-border-radius: 14;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 9, 0, 0, 2);"
        );

        Label titleLabel = new Label(
                title
        );

        titleLabel.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #4c1d95;"
        );

        Label descriptionLabel = new Label(
                description
        );

        descriptionLabel.setWrapText(true);

        descriptionLabel.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #475569;"
        );

        card.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );

        return card;
    }

    // =========================================================
    // BULLET
    // =========================================================

    private HBox bullet(
            String text
    ) {

        HBox row = new HBox(10);

        row.setAlignment(
                Pos.TOP_LEFT
        );

        Label bullet = new Label(
                "●"
        );

        bullet.setStyle(
                "-fx-text-fill: #d97706;"
        );

        Label description = new Label(
                text
        );

        description.setWrapText(true);

        description.setStyle(
                "-fx-text-fill: #475569;"
        );

        HBox.setHgrow(
                description,
                Priority.ALWAYS
        );

        row.getChildren().addAll(
                bullet,
                description
        );

        return row;
    }

    // =========================================================
    // HERO IMAGE
    // =========================================================

    private VBox createHeroImage(
            String imagePath,
            String title,
            String subtitle
    ) {

        VBox hero = new VBox(14);

        hero.setAlignment(
                Pos.CENTER
        );

        hero.setPadding(
                new Insets(15)
        );

        hero.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: #fecaca;" +
                "-fx-border-radius: 18;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 12, 0, 0, 3);"
        );

        ImageView image = createSquareImage(
                imagePath,
                280
        );

        Label titleLabel = new Label(
                title
        );

        titleLabel.setStyle(
                "-fx-font-size: 25px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #991b1b;"
        );

        Label subtitleLabel = new Label(
                subtitle
        );

        subtitleLabel.setWrapText(true);

        subtitleLabel.setAlignment(
                Pos.CENTER
        );

        subtitleLabel.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #64748b;"
        );

        hero.getChildren().addAll(
                image,
                titleLabel,
                subtitleLabel
        );

        return hero;
    }

    // =========================================================
    // COMMON SCROLLABLE SCENE
    // =========================================================

    private void showScrollableScene(
            VBox content,
            String title,
            String description
    ) {

        ScrollPane scroll = new ScrollPane(
                content
        );

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

        stage.setScene(
                PatientUI.createScene(
                        stage,
                        title,
                        title,
                        description,
                        scroll
                )
        );

        stage.show();
    }
}