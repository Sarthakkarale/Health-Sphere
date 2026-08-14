package com.healthsphere.view.Hospital;

import com.healthsphere.view.Hospital.HospitalProfileSettingsController;
import com.healthsphere.view.Hospital.HospitalProfileModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class HospitalProfileSettingsView {

    // =========================================================
    // MODERN LIGHT PALETTE (With Dark Sidebar)
    // =========================================================

    private static final String PRIMARY_BLUE = "#170eca";     // Vibrant Modern Blue
    private static final String PRIMARY_LIGHT = "#EFF6FF";    // Light Blue Accent Tint
    private static final String DARK_TEXT = "#0F172A";        // Slate Dark Text
    private static final String SECONDARY_TEXT = "#64748B";   // Slate Muted Text
    private static final String LIGHT_BACKGROUND = "#F8FAFC"; // Soft Off-White Background
    private static final String CARD_BACKGROUND = "#FFFFFF";  // Crisp Card Background
    private static final String CARD_ALT_BG = "#FAFCFF";      // Soft Light Surface Tint
    private static final String BORDER = "#E2E8F0";           // Soft Border Slate
    private static final String SUCCESS_GREEN = "#10B981";   // Emerald Green
    private static final String ERROR_RED = "#EF4444";       // Rose Red

    // Dark Sidebar Palette
    private static final String SIDEBAR_BG = "#0F172A";
    private static final String SIDEBAR_BORDER = "#1E293B";
    private static final String SIDEBAR_TEXT = "#94A3B8";
    private static final String SIDEBAR_TEXT_ACTIVE = "#F8FAFC";
    private static final String SIDEBAR_ICON_ACTIVE = "#3B82F6";
    private static final String SIDEBAR_ACTIVE_BG = "#1E293B";
    private static final String SIDEBAR_HOVER_BG = "#1E293B80";

    // Subtle Drop Shadow Effect for Cards
    private static final String SHADOW_EFFECT =
            "-fx-effect: dropshadow(three-pass-box, rgba(15, 23, 42, 0.05), 12, 0, 0, 3);";

    private HospitalProfileModel model;
    private HospitalProfileSettingsController controller;

    // =========================================================
    // CREATE SCENE
    // =========================================================

    public Scene createScene(Stage stage) {

        this.model = new HospitalProfileModel();
        this.controller = new HospitalProfileSettingsController(model, stage);

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color: " + LIGHT_BACKGROUND + ";"
        );

        root.setLeft(createSidebar(stage));
        root.setTop(createTopBar());
        root.setCenter(createMainContent());

        return new Scene(root, stage.getWidth(), stage.getHeight());
    }

    // =========================================================
    // SIDEBAR (DARK THEME)
    // =========================================================

    private VBox createSidebar(Stage stage) {

        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(230);
        sidebar.setPadding(new Insets(24, 16, 20, 16));

        sidebar.setStyle(
                "-fx-background-color: " + SIDEBAR_BG + ";" +
                "-fx-border-color: " + SIDEBAR_BORDER + ";" +
                "-fx-border-width: 0 1 0 0;"
        );

        // LOGO
        VBox logoBox = new VBox(2);
        logoBox.setPadding(new Insets(0, 6, 20, 6));

        Label logo = new Label("Health-Sphere");
        logo.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: #60A5FA;"
        );

        Label subtitle = new Label("SMART HEALTHCARE");
        subtitle.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-font-weight: 700;" +
                "-fx-letter-spacing: 1px;" +
                "-fx-text-fill: " + SIDEBAR_TEXT + ";"
        );

        logoBox.getChildren().addAll(logo, subtitle);
        sidebar.getChildren().add(logoBox);

        // NAVIGATION
        Button dashboardButton = createNavigationButton("▦", "Dashboard", false);
        Button doctorButton = createNavigationButton("♙", "Doctors", false);
        Button departmentButton = createNavigationButton("✚", "Departments", false);
        Button bedButton = createNavigationButton("▥", "Beds", false);
        Button appointmentButton = createNavigationButton("▣", "Appointments", false);
        Button analyticsButton = createNavigationButton("◈", "Analytics", false);
        Button settingsButton = createNavigationButton("⚙", "Hospital Settings", true);

        sidebar.getChildren().addAll(
                dashboardButton,
                doctorButton,
                departmentButton,
                bedButton,
                appointmentButton,
                analyticsButton,
                settingsButton
        );

        // Navigation Actions
        dashboardButton.setOnAction(event -> controller.handleNavigation("Dashboard"));
        doctorButton.setOnAction(event -> controller.handleNavigation("Doctors"));
        departmentButton.setOnAction(event -> controller.handleNavigation("Departments"));
        bedButton.setOnAction(event -> controller.handleNavigation("Beds"));
        appointmentButton.setOnAction(event -> controller.handleNavigation("Appointments"));
        analyticsButton.setOnAction(event -> controller.handleNavigation("Analytics"));

        // Bottom Spacer & Help/Logout
        Region sidebarSpacer = new Region();
        VBox.setVgrow(sidebarSpacer, Priority.ALWAYS);
        sidebar.getChildren().add(sidebarSpacer);

        Button helpButton = createNavigationButton("?", "Help Center", false);
        Button logoutButton = createNavigationButton("↪", "Logout", false);

        helpButton.setOnAction(e -> controller.handleNavigation("Help"));
        logoutButton.setOnAction(e -> controller.handleNavigation("Logout"));

        sidebar.getChildren().addAll(helpButton, logoutButton);

        return sidebar;
    }

    // =========================================================
    // NAVIGATION BUTTON (DARK THEME)
    // =========================================================

    private Button createNavigationButton(String icon, String text, boolean selected) {

        Button button = new Button();

        Label iconLabel = new Label(icon);
        iconLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-text-fill: " + (selected ? SIDEBAR_ICON_ACTIVE : SIDEBAR_TEXT) + ";"
        );

        Label textLabel = new Label(text);
        textLabel.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: " + (selected ? "bold" : "500") + ";" +
                "-fx-text-fill: " + (selected ? SIDEBAR_TEXT_ACTIVE : SIDEBAR_TEXT) + ";"
        );

        HBox content = new HBox(12);
        content.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().addAll(iconLabel, textLabel);

        button.setGraphic(content);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(40);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(0, 12, 0, 12));

        String baseStyle = "-fx-background-radius: 8; -fx-cursor: hand;";

        if (selected) {
            button.setStyle(baseStyle + "-fx-background-color: " + PRIMARY_BLUE + ";");
        } else {
            button.setStyle(baseStyle + "-fx-background-color: transparent;");

            // Hover effects
            button.setOnMouseEntered(e -> button.setStyle(baseStyle + "-fx-background-color: " + SIDEBAR_HOVER_BG + ";"));
            button.setOnMouseExited(e -> button.setStyle(baseStyle + "-fx-background-color: transparent;"));
        }

        return button;
    }

    // =========================================================
    // TOP BAR
    // =========================================================

    private HBox createTopBar() {

        HBox topBar = new HBox(16);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(12, 28, 12, 24));

        topBar.setStyle(
                "-fx-background-color: " + CARD_BACKGROUND + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 0 0 1 0;"
        );

        Label searchIcon = new Label("⌕");
        searchIcon.setStyle("-fx-font-size: 18px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        TextField searchInput = new TextField();
        searchInput.setPromptText("Search settings...");
        searchInput.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-font-size: 13px;" +
                "-fx-text-fill: " + DARK_TEXT + ";" +
                "-fx-prompt-text-fill: #94A3B8;" +
                "-fx-padding: 0;"
        );
        searchInput.setOnAction(e -> controller.handleSearch(searchInput.getText()));

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPrefWidth(340);
        searchBox.setPrefHeight(38);
        searchBox.setPadding(new Insets(0, 14, 0, 14));

        searchBox.setStyle(
                "-fx-background-color: " + LIGHT_BACKGROUND + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        searchBox.getChildren().addAll(searchIcon, searchInput);

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        Label notification = new Label("♧");
        notification.setStyle("-fx-font-size: 18px; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-cursor: hand;");

        Label settings = new Label("⚙");
        settings.setStyle("-fx-font-size: 18px; -fx-text-fill: " + SECONDARY_TEXT + "; -fx-cursor: hand;");

        Label administrator = new Label("Hospital Administrator");
        administrator.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TEXT + ";");

        Label role = new Label("HOSPITAL ADMIN");
        role.setStyle("-fx-font-size: 9px; -fx-font-weight: 600; -fx-text-fill: " + SECONDARY_TEXT + ";");

        VBox userInfo = new VBox(1);
        userInfo.setAlignment(Pos.CENTER_RIGHT);
        userInfo.getChildren().addAll(administrator, role);

        Circle avatar = new Circle(18);
        avatar.setFill(Color.web(PRIMARY_LIGHT));
        avatar.setStroke(Color.web(BORDER));

        Label avatarText = new Label("HA");
        avatarText.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_BLUE + ";");

        StackPane avatarBox = new StackPane(avatar, avatarText);

        topBar.getChildren().addAll(
                searchBox,
                topSpacer,
                notification,
                settings,
                userInfo,
                avatarBox
        );

        return topBar;
    }

    // =========================================================
    // MAIN CONTENT
    // =========================================================

    private VBox createMainContent() {

        VBox content = new VBox();
        content.setPadding(new Insets(24, 32, 24, 32));

        ScrollPane scrollPane = new ScrollPane();
        VBox settingsContent = new VBox(22);
        settingsContent.setPadding(new Insets(4, 8, 32, 4));

        settingsContent.getChildren().add(createPageHeader());
        settingsContent.getChildren().add(createHospitalInformationCard());
        settingsContent.getChildren().add(createContactAddressCard());
        settingsContent.getChildren().add(createOperatingHoursCard());
        settingsContent.getChildren().add(createEmergencyAndTourismCard());
        settingsContent.getChildren().add(createImagesAndDocumentsCard());
        settingsContent.getChildren().add(createAccountSettingsCard());

        scrollPane.setContent(settingsContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        scrollPane.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;"
        );

        content.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return content;
    }

    // =========================================================
    // PAGE HEADER
    // =========================================================

    private HBox createPageHeader() {

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);

        Label title = new Label("Hospital Profile & Settings");
        title.setStyle(
                "-fx-font-size: 24px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label subtitle = new Label("Manage hospital information, availability and account settings");
        subtitle.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        titleBox.getChildren().addAll(title, subtitle);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button saveButton = new Button("✓  Save Changes");
        saveButton.setPrefHeight(40);
        saveButton.setPadding(new Insets(0, 20, 0, 20));

        saveButton.setStyle(
                "-fx-background-color: " + PRIMARY_BLUE + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(37, 99, 235, 0.25), 8, 0, 0, 2);"
        );

        saveButton.setOnAction(e -> controller.handleSaveChanges());

        header.getChildren().addAll(titleBox, headerSpacer, saveButton);

        return header;
    }

    // =========================================================
    // HOSPITAL INFORMATION
    // =========================================================

    private VBox createHospitalInformationCard() {

        VBox card = createSectionCard();

        card.getChildren().add(
                createSectionHeader("Hospital Information", "Basic details and identification about your facility")
        );

        GridPane grid = createFormGrid();

        TextField hospitalName = createTextField("");
        hospitalName.textProperty().bindBidirectional(model.hospitalNameProperty());

        TextField registrationNumber = createTextField("");
        registrationNumber.textProperty().bindBidirectional(model.registrationNumberProperty());

        TextField hospitalType = createTextField("");
        hospitalType.textProperty().bindBidirectional(model.hospitalTypeProperty());

        TextField establishedYear = createTextField("");
        establishedYear.textProperty().bindBidirectional(model.establishedYearProperty());

        addField(grid, "Hospital Name", hospitalName, 0, 0);
        addField(grid, "Registration Number", registrationNumber, 1, 0);
        addField(grid, "Hospital Type", hospitalType, 0, 1);
        addField(grid, "Established Year", establishedYear, 1, 1);

        card.getChildren().add(grid);

        return card;
    }

    // =========================================================
    // CONTACT & ADDRESS
    // =========================================================

    private VBox createContactAddressCard() {

        VBox card = createSectionCard();

        card.getChildren().add(
                createSectionHeader("Contact Details & Address", "Hospital communication and location credentials")
        );

        GridPane grid = createFormGrid();

        TextField phone = createTextField("");
        phone.textProperty().bindBidirectional(model.phoneNumberProperty());

        TextField email = createTextField("");
        email.textProperty().bindBidirectional(model.emailAddressProperty());

        TextField website = createTextField("");
        website.textProperty().bindBidirectional(model.websiteProperty());

        TextField city = createTextField("");
        city.textProperty().bindBidirectional(model.cityProperty());

        TextField state = createTextField("");
        state.textProperty().bindBidirectional(model.stateProperty());

        TextField postalCode = createTextField("");
        postalCode.textProperty().bindBidirectional(model.postalCodeProperty());

        TextArea address = new TextArea();
        address.textProperty().bindBidirectional(model.addressProperty());
        address.setPrefRowCount(3);
        address.setWrapText(true);

        address.setStyle(
                "-fx-background-color: " + LIGHT_BACKGROUND + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 12px;" +
                "-fx-text-fill: " + DARK_TEXT + ";" +
                "-fx-padding: 8;"
        );

        addField(grid, "Phone Number", phone, 0, 0);
        addField(grid, "Email Address", email, 1, 0);
        addField(grid, "Website", website, 0, 1);
        addField(grid, "City", city, 1, 1);
        addField(grid, "State", state, 0, 2);
        addField(grid, "Postal Code", postalCode, 1, 2);

        Label addressLabel = createFieldLabel("Address");
        VBox addressBox = new VBox(6);
        addressBox.getChildren().addAll(addressLabel, address);

        GridPane.setColumnSpan(addressBox, 2);
        grid.add(addressBox, 0, 3);

        card.getChildren().add(grid);

        return card;
    }

    // =========================================================
    // OPERATING HOURS
    // =========================================================

    private VBox createOperatingHoursCard() {

        VBox card = createSectionCard();

        card.getChildren().add(
                createSectionHeader("Operating Hours", "Configure weekly working schedules and timing")
        );

        GridPane grid = createFormGrid();

        String[] days = {
                "Monday", "Tuesday", "Wednesday",
                "Thursday", "Friday", "Saturday", "Sunday"
        };

        String[] times = {
                "08:00 AM - 08:00 PM", "08:00 AM - 08:00 PM", "08:00 AM - 08:00 PM",
                "08:00 AM - 08:00 PM", "08:00 AM - 08:00 PM", "09:00 AM - 04:00 PM",
                "Emergency Only"
        };

        for (int i = 0; i < days.length; i++) {
            HBox dayRow = createDayRow(days[i], times[i], i != 6);
            grid.add(dayRow, i % 2, i / 2);
        }

        card.getChildren().add(grid);

        return card;
    }

    // =========================================================
    // DAY ROW
    // =========================================================

    private HBox createDayRow(String day, String hours, boolean active) {

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 14, 10, 14));

        row.setStyle(
                "-fx-background-color: " + LIGHT_BACKGROUND + ";" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 8;"
        );

        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(active);

        Label dayLabel = new Label(day);
        dayLabel.setPrefWidth(85);
        dayLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label hoursLabel = new Label(hours);
        hoursLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        row.getChildren().addAll(checkBox, dayLabel, hoursLabel);

        return row;
    }

    // =========================================================
    // EMERGENCY + MEDICAL TOURISM
    // =========================================================

    private HBox createEmergencyAndTourismCard() {

        HBox row = new HBox(20);

        VBox emergencyCard = createSectionCard();
        VBox tourismCard = createSectionCard();

        HBox.setHgrow(emergencyCard, Priority.ALWAYS);
        HBox.setHgrow(tourismCard, Priority.ALWAYS);

        // Emergency Services
        emergencyCard.getChildren().add(
                createSectionHeader("Emergency Availability", "Configure critical services availability")
        );

        HBox emergencyStatus = new HBox(10);
        emergencyStatus.setAlignment(Pos.CENTER_LEFT);
        emergencyStatus.setPadding(new Insets(4, 0, 4, 0));

        Circle greenDot = new Circle(5);
        greenDot.setFill(Color.web(SUCCESS_GREEN));

        Label emergencyLabel = new Label("Emergency Services Active");
        emergencyLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + SUCCESS_GREEN + ";"
        );

        emergencyStatus.getChildren().addAll(greenDot, emergencyLabel);

        CheckBox ambulance = new CheckBox("24/7 Ambulance Service");
        ambulance.selectedProperty().bindBidirectional(model.ambulance24x7Property());

        CheckBox emergencyWard = new CheckBox("24/7 Emergency Ward");
        emergencyWard.selectedProperty().bindBidirectional(model.emergencyWard24x7Property());

        CheckBox trauma = new CheckBox("Trauma & Critical Care Unit");
        trauma.selectedProperty().bindBidirectional(model.traumaUnitProperty());

        emergencyCard.getChildren().addAll(emergencyStatus, ambulance, emergencyWard, trauma);

        // Medical Tourism
        tourismCard.getChildren().add(
                createSectionHeader("Medical Tourism Support", "International patient services & logistics")
        );

        CheckBox tourismSupport = new CheckBox("Medical Tourism Support Available");
        tourismSupport.selectedProperty().bindBidirectional(model.medicalTourismAvailableProperty());

        CheckBox internationalDesk = new CheckBox("International Patient Desk");
        internationalDesk.selectedProperty().bindBidirectional(model.internationalDeskAvailableProperty());

        CheckBox airportPickup = new CheckBox("Airport Pickup Assistance");
        airportPickup.selectedProperty().bindBidirectional(model.airportPickupAvailableProperty());

        ComboBox<String> languages = new ComboBox<>();
        languages.getItems().addAll("English", "Hindi", "Marathi", "Arabic", "French");
        languages.valueProperty().bindBidirectional(model.primaryLanguageProperty());
        languages.setPrefWidth(200);
        languages.setStyle(
                "-fx-background-color: " + LIGHT_BACKGROUND + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        tourismCard.getChildren().addAll(
                tourismSupport,
                internationalDesk,
                airportPickup,
                createFieldLabel("Primary Support Language"),
                languages
        );

        row.getChildren().addAll(emergencyCard, tourismCard);

        return row;
    }

    // =========================================================
    // IMAGES + DOCUMENTS
    // =========================================================

    private HBox createImagesAndDocumentsCard() {

        HBox row = new HBox(20);

        VBox imagesCard = createSectionCard();
        VBox documentsCard = createSectionCard();

        HBox.setHgrow(imagesCard, Priority.ALWAYS);
        HBox.setHgrow(documentsCard, Priority.ALWAYS);

        // Hospital Images
        imagesCard.getChildren().add(
                createSectionHeader("Hospital Images", "Upload and manage hospital gallery photos")
        );

        HBox imagePreview = new HBox(12);
        imagePreview.getChildren().add(createImagePlaceholder("Hospital Front"));
        imagePreview.getChildren().add(createImagePlaceholder("Reception"));
        imagePreview.getChildren().add(createImagePlaceholder("Emergency"));

        Button uploadImage = new Button("+  Upload Image");
        uploadImage.setPrefHeight(36);
        uploadImage.setStyle(
                "-fx-background-color: " + PRIMARY_LIGHT + ";" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );
        uploadImage.setOnAction(e -> controller.handleUploadImage());

        imagesCard.getChildren().addAll(imagePreview, uploadImage);

        // Documents
        documentsCard.getChildren().add(
                createSectionHeader("Documents & Licenses", "Hospital verification records & credentials")
        );

        documentsCard.getChildren().add(createDocumentRow("Hospital Registration", "PDF • Verified"));
        documentsCard.getChildren().add(createDocumentRow("Medical License", "PDF • Verified"));
        documentsCard.getChildren().add(createDocumentRow("NABH Accreditation", "PDF • Verified"));

        Button uploadDocument = new Button("+  Upload Document");
        uploadDocument.setPrefHeight(36);
        uploadDocument.setStyle(
                "-fx-background-color: " + PRIMARY_LIGHT + ";" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );
        uploadDocument.setOnAction(e -> controller.handleUploadDocument());

        documentsCard.getChildren().add(uploadDocument);

        row.getChildren().addAll(imagesCard, documentsCard);

        return row;
    }

    // =========================================================
    // IMAGE PLACEHOLDER
    // =========================================================

    private VBox createImagePlaceholder(String title) {

        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(105);

        StackPane imageArea = new StackPane();
        imageArea.setPrefSize(105, 70);
        imageArea.setStyle(
                "-fx-background-color: " + PRIMARY_LIGHT + ";" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #DBEFEA;" +
                "-fx-border-radius: 8;"
        );

        Label imageIcon = new Label("▧");
        imageIcon.setStyle("-fx-font-size: 22px; -fx-text-fill: " + PRIMARY_BLUE + ";");

        imageArea.getChildren().add(imageIcon);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + SECONDARY_TEXT + ";");

        box.getChildren().addAll(imageArea, titleLabel);

        return box;
    }

    // =========================================================
    // DOCUMENT ROW
    // =========================================================

    private HBox createDocumentRow(String title, String details) {

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 12, 8, 12));

        row.setStyle(
                "-fx-background-color: " + LIGHT_BACKGROUND + ";" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 8;"
        );

        Label icon = new Label("▤");
        icon.setPrefSize(32, 32);
        icon.setAlignment(Pos.CENTER);
        icon.setStyle(
                "-fx-background-color: " + PRIMARY_LIGHT + ";" +
                "-fx-background-radius: 6;" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";" +
                "-fx-font-size: 14px;"
        );

        VBox detailsBox = new VBox(2);

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label detailLabel = new Label(details);
        detailLabel.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-text-fill: " + SUCCESS_GREEN + ";" +
                "-fx-font-weight: 600;"
        );

        detailsBox.getChildren().addAll(titleLabel, detailLabel);

        Region documentSpacer = new Region();
        HBox.setHgrow(documentSpacer, Priority.ALWAYS);

        Button viewButton = new Button("View");
        viewButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";" +
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );
        viewButton.setOnAction(e -> controller.handleViewDocument(title));

        row.getChildren().addAll(icon, detailsBox, documentSpacer, viewButton);

        return row;
    }

    // =========================================================
    // ACCOUNT SETTINGS
    // =========================================================

    private VBox createAccountSettingsCard() {

        VBox card = createSectionCard();

        card.getChildren().add(
                createSectionHeader("Account Settings", "Manage administrator profile preferences and safety")
        );

        GridPane grid = createFormGrid();

        TextField adminName = createTextField("");
        adminName.textProperty().bindBidirectional(model.adminNameProperty());

        TextField adminEmail = createTextField("");
        adminEmail.textProperty().bindBidirectional(model.adminEmailProperty());

        ComboBox<String> notificationPreference = new ComboBox<>();
        notificationPreference.getItems().addAll(
                "All Notifications",
                "Important Only",
                "Email Only",
                "Disabled"
        );
        notificationPreference.valueProperty().bindBidirectional(model.notificationPreferenceProperty());
        notificationPreference.setMaxWidth(Double.MAX_VALUE);
        notificationPreference.setStyle(
                "-fx-background-color: " + LIGHT_BACKGROUND + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        addField(grid, "Administrator Name", adminName, 0, 0);
        addField(grid, "Administrator Email", adminEmail, 1, 0);
        addField(grid, "Notification Preference", notificationPreference, 0, 1);

        CheckBox emailNotifications = new CheckBox("Receive email notifications");
        emailNotifications.selectedProperty().bindBidirectional(model.emailNotificationsProperty());

        CheckBox securityAlerts = new CheckBox("Receive security alerts");
        securityAlerts.selectedProperty().bindBidirectional(model.securityAlertsProperty());

        VBox notificationBox = new VBox(10);
        notificationBox.setPadding(new Insets(18, 0, 0, 0));
        notificationBox.getChildren().addAll(emailNotifications, securityAlerts);

        grid.add(notificationBox, 1, 1);

        card.getChildren().add(grid);

        HBox accountActions = new HBox(12);
        accountActions.setPadding(new Insets(6, 0, 0, 0));

        Button changePassword = new Button("Change Password");
        changePassword.setPrefHeight(36);
        changePassword.setStyle(
                "-fx-background-color: " + CARD_BACKGROUND + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-text-fill: " + DARK_TEXT + ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );
        changePassword.setOnAction(e -> controller.handleChangePassword());

        Button deleteAccount = new Button("Deactivate Account");
        deleteAccount.setPrefHeight(36);
        deleteAccount.setStyle(
                "-fx-background-color: #FEF2F2;" +
                "-fx-border-color: #FCA5A5;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-text-fill: " + ERROR_RED + ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );
        deleteAccount.setOnAction(e -> controller.handleDeactivateAccount());

        accountActions.getChildren().addAll(changePassword, deleteAccount);
        card.getChildren().add(accountActions);

        return card;
    }

    // =========================================================
    // SECTION CARD (LIGHT & STYLED)
    // =========================================================

    private VBox createSectionCard() {

        VBox card = new VBox(16);
        card.setPadding(new Insets(20));

        card.setStyle(
                "-fx-background-color: " + CARD_BACKGROUND + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;" +
                SHADOW_EFFECT
        );

        return card;
    }

    // =========================================================
    // SECTION HEADER
    // =========================================================

    private HBox createSectionHeader(String title, String subtitle) {

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(3);

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-font-size: 15px;" +
                "-fx-font-weight: 700;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        titleBox.getChildren().addAll(titleLabel, subtitleLabel);
        header.getChildren().add(titleBox);

        return header;
    }

    // =========================================================
    // FORM HELPERS
    // =========================================================

    private GridPane createFormGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(16);
        return grid;
    }

    private void addField(GridPane grid, String labelText, Control inputControl, int col, int row) {
        VBox box = new VBox(6);
        Label label = createFieldLabel(labelText);
        box.getChildren().addAll(label, inputControl);

        GridPane.setHgrow(box, Priority.ALWAYS);
        grid.add(box, col, row);
    }

    private Label createFieldLabel(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: 600;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );
        return label;
    }

    private TextField createTextField(String value) {
        TextField textField = new TextField(value);
        textField.setPrefHeight(38);
        textField.setStyle(
                "-fx-background-color: " + LIGHT_BACKGROUND + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 12px;" +
                "-fx-text-fill: " + DARK_TEXT + ";" +
                "-fx-padding: 0 12 0 12;"
        );
        return textField;
    }
}