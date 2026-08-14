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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SearchHospitals {

    private final Stage stage;

    public SearchHospitals(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {

        BorderPane root = new BorderPane();

        root.setLeft(createSidebar());
        root.setTop(createHeader());

        VBox content = new VBox(22);
        content.setPadding(new Insets(28));
        content.setStyle("-fx-background-color: #f1f5f9;");

        Label title = new Label("Find Healthcare Near You");

        title.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label subtitle = new Label(
                "Search hospitals, healthcare facilities and specialties."
        );

        subtitle.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-text-fill: #64748b;"
        );

        VBox heading = new VBox(5, title, subtitle);

        // -------------------------------------------------
        // SEARCH CARD
        // -------------------------------------------------

        VBox searchCard = card("Search Hospitals");

        TextField search = new TextField();

        search.setPromptText(
                "Search hospital, city or specialty..."
        );

        search.setPrefHeight(45);

        search.setStyle(
                "-fx-background-color: #f8fafc;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        Button searchButton =
                button("Search", () -> {

                    String value =
                            search.getText().trim();

                    if (!value.isEmpty()) {
                        filterHospitals(
                                value,
                                hospitalContainer
                        );
                    }
                });

        HBox searchRow = new HBox(12);

        searchRow.setAlignment(Pos.CENTER_LEFT);

        searchRow.getChildren().addAll(
                search,
                searchButton
        );

        HBox.setHgrow(
                search,
                Priority.ALWAYS
        );

        searchCard.getChildren().add(searchRow);

        // -------------------------------------------------
        // HOSPITALS
        // -------------------------------------------------

        hospitalContainer = new VBox(18);

        hospitalContainer.getChildren().addAll(

                hospitalCard(
                        "Apollo Hospitals",
                        "Jubilee Hills, Hyderabad",
                        "Multi-Speciality Hospital",
                        "4.7 ★",
                        "/images/hospitals/hospital1.jpg"
                ),

                hospitalCard(
                        "Fortis Healthcare",
                        "Bengaluru",
                        "Multi-Speciality Hospital",
                        "4.6 ★",
                        "/images/hospitals/hospital2.jpg"
                ),

                hospitalCard(
                        "Max Healthcare",
                        "New Delhi",
                        "Multi-Speciality Hospital",
                        "4.5 ★",
                        "/images/hospitals/hospital3.jpg"
                ),

                hospitalCard(
                        "Manipal Hospitals",
                        "Bengaluru",
                        "Advanced Healthcare",
                        "4.6 ★",
                        "/images/hospitals/hospital4.jpg"
                )
        );

        content.getChildren().addAll(
                heading,
                searchCard,
                hospitalContainer
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

    private VBox hospitalContainer;

    // =====================================================
    // HOSPITAL CARD
    // =====================================================

    private VBox hospitalCard(
            String name,
            String location,
            String type,
            String rating,
            String imagePath
    ) {

        VBox card = new VBox(12);

        card.setPadding(
                new Insets(16)
        );

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 14;" +
                "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.08), 10, 0, 0, 3);"
        );

        HBox row = new HBox(18);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        ImageView image =
                loadImage(
                        imagePath,
                        190,
                        125
                );

        VBox information =
                new VBox(7);

        Label nameLabel =
                new Label(name);

        nameLabel.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label locationLabel =
                new Label(location);

        locationLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        Label typeLabel =
                new Label(type);

        typeLabel.setStyle(
                "-fx-text-fill: #2563eb;" +
                "-fx-font-weight: bold;"
        );

        Label ratingLabel =
                new Label(rating);

        ratingLabel.setStyle(
                "-fx-text-fill: #16a34a;" +
                "-fx-font-weight: bold;"
        );

        information.getChildren().addAll(
                nameLabel,
                locationLabel,
                typeLabel,
                ratingLabel
        );

        Region spacer = new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button details =
                button(
                        "View Details",
                        () -> showHospitalDetails(
                                name,
                                location,
                                type,
                                rating,
                                imagePath
                        )
                );

        row.getChildren().addAll(
                image,
                information,
                spacer,
                details
        );

        card.getChildren().add(row);

        return card;
    }

    // =====================================================
    // HOSPITAL DETAILS SCREEN
    // =====================================================

    private void showHospitalDetails(
            String name,
            String location,
            String type,
            String rating,
            String imagePath
    ) {

        BorderPane root =
                new BorderPane();

        root.setLeft(createSidebar());
        root.setTop(createHeader());

        VBox content =
                new VBox(22);

        content.setPadding(
                new Insets(30)
        );

        content.setStyle(
                "-fx-background-color: #f1f5f9;"
        );

        Button back =
                button(
                        "← Back to Hospitals",
                        this::showSearchHospitals
                );

        ImageView image =
                loadImage(
                        imagePath,
                        700,
                        300
                );

        Label title =
                new Label(name);

        title.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        Label locationLabel =
                new Label(location);

        locationLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-text-fill: #64748b;"
        );

        Label typeLabel =
                new Label(type);

        typeLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-text-fill: #2563eb;" +
                "-fx-font-weight: bold;"
        );

        Label ratingLabel =
                new Label("Rating: " + rating);

        ratingLabel.setStyle(
                "-fx-text-fill: #16a34a;" +
                "-fx-font-weight: bold;"
        );

        VBox information =
                card("Hospital Information");

        information.getChildren().addAll(
                locationLabel,
                typeLabel,
                ratingLabel,
                new Label(
                        "24/7 Emergency Services"
                ),
                new Label(
                        "Outpatient and inpatient care available"
                ),
                new Label(
                        "Specialist doctors and diagnostic services"
                )
        );

        Button book =
                button(
                        "Book Appointment",
                        this::showBookAppointment
                );

        HBox actions =
                new HBox(12);

        actions.getChildren().addAll(
                book,
                button(
                        "Back",
                        this::showSearchHospitals
                )
        );

        content.getChildren().addAll(
                back,
                image,
                title,
                information,
                actions
        );

        ScrollPane scroll =
                new ScrollPane(content);

        scroll.setFitToWidth(true);

        root.setCenter(scroll);

        stage.setScene(
                new Scene(
                        root,
                        1440,
                        900
                )
        );
    }

    // =====================================================
    // SEARCH
    // =====================================================

    private void filterHospitals(
            String searchText,
            VBox container
    ) {

        String query =
                searchText.toLowerCase();

        for (javafx.scene.Node node :
                container.getChildren()) {

            if (!(node instanceof VBox)) {
                continue;
            }

            VBox hospital =
                    (VBox) node;

            boolean visible =
                    hospital.getUserData() != null &&
                    hospital.getUserData()
                            .toString()
                            .toLowerCase()
                            .contains(query);

            hospital.setVisible(visible);
            hospital.setManaged(visible);
        }
    }

    // =====================================================
    // CREATE HOSPITAL CARD WITH SEARCH DATA
    // =====================================================

    private VBox createSearchableHospital(
            String name
    ) {

        VBox box = new VBox();

        box.setUserData(name);

        return box;
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
    // COMMON CARD
    // =====================================================

    private VBox card(String title) {

        VBox box =
                new VBox(12);

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

        if (!title.isEmpty()) {

            Label label =
                    new Label(title);

            label.setStyle(
                    "-fx-font-size: 18px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: #0f172a;"
            );

            box.getChildren().add(label);
        }

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