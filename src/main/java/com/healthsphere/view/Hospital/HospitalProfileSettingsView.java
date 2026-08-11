package com.healthsphere.view.Hospital;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class HospitalProfileSettingsView {

    // =========================================================
    // COLORS
    // =========================================================

    private static final String PRIMARY_BLUE = "#0756C9";
    private static final String DARK_TEXT = "#18212F";
    private static final String SECONDARY_TEXT = "#667085";
    private static final String LIGHT_BACKGROUND = "#F7F8FC";
    private static final String BORDER = "#E1E5ED";
    private static final String SUCCESS_GREEN = "#16856F";
    private static final String ERROR_RED = "#D64545";

    // =========================================================
    // CREATE SCENE
    // =========================================================

    public Scene createScene(Stage stage) {

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
    // SIDEBAR
    // =========================================================

    private VBox createSidebar(Stage stage) {

        VBox sidebar = new VBox(8);

        sidebar.setPrefWidth(220);

        sidebar.setPadding(
                new Insets(22, 15, 18, 15)
        );

        sidebar.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 0 1 0 0;"
        );

        // -----------------------------------------------------
        // LOGO
        // -----------------------------------------------------

        VBox logoBox = new VBox(2);

        logoBox.setPadding(
                new Insets(0, 5, 18, 5)
        );

        Label logo = new Label("Health-Sphere");

        logo.setStyle(
                "-fx-font-size: 21px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";"
        );

        Label subtitle = new Label(
                "SMART HEALTHCARE"
        );

        subtitle.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        logoBox.getChildren().addAll(
                logo,
                subtitle
        );

        sidebar.getChildren().add(logoBox);

        // -----------------------------------------------------
        // NAVIGATION
        // -----------------------------------------------------

        Button dashboardButton =
                createNavigationButton(
                        "▦",
                        "Dashboard",
                        false
                );

        Button doctorButton =
                createNavigationButton(
                        "♙",
                        "Doctors",
                        false
                );

        Button departmentButton =
                createNavigationButton(
                        "✚",
                        "Departments",
                        false
                );

        Button bedButton =
                createNavigationButton(
                        "▥",
                        "Beds",
                        false
                );

        Button appointmentButton =
                createNavigationButton(
                        "▣",
                        "Appointments",
                        false
                );

        Button analyticsButton =
                createNavigationButton(
                        "◈",
                        "Analytics",
                        false
                );

        Button settingsButton =
                createNavigationButton(
                        "⚙",
                        "Hospital Settings",
                        true
                );

        sidebar.getChildren().addAll(
                dashboardButton,
                doctorButton,
                departmentButton,
                bedButton,
                appointmentButton,
                analyticsButton,
                settingsButton
        );

        // =====================================================
        // NAVIGATION EVENTS
        // =====================================================

        dashboardButton.setOnAction(event -> {

            HospitalDashboardView dashboardView =
                    new HospitalDashboardView();

            stage.setScene(
                    dashboardView.createScene(stage)
            );
        });

        doctorButton.setOnAction(event -> {

            DoctorManagementView doctorView =
                    new DoctorManagementView();

            stage.setScene(
                    doctorView.createScene(stage)
            );
        });

        departmentButton.setOnAction(event -> {

            DepartmentManagementView departmentView =
                    new DepartmentManagementView();

            stage.setScene(
                    departmentView.createScene(stage)
            );
        });

        bedButton.setOnAction(event -> {

            BedManagementView bedView =
                    new BedManagementView();

            stage.setScene(
                    bedView.createScene(stage)
            );
        });

        appointmentButton.setOnAction(event -> {

            AppointmentManagementView appointmentView =
                    new AppointmentManagementView();

            stage.setScene(
                    appointmentView.createScene(stage)
            );
        });

        analyticsButton.setOnAction(event -> {

            HospitalAnalyticsView analyticsView =
                    new HospitalAnalyticsView();

            stage.setScene(
                    analyticsView.createScene(stage)
            );
        });

        // -----------------------------------------------------
        // BOTTOM SIDEBAR
        // -----------------------------------------------------

        Region sidebarSpacer = new Region();

        VBox.setVgrow(
                sidebarSpacer,
                Priority.ALWAYS
        );

        sidebar.getChildren().add(
                sidebarSpacer
        );

        Button helpButton =
                createNavigationButton(
                        "?",
                        "Help Center",
                        false
                );

        Button logoutButton =
                createNavigationButton(
                        "↪",
                        "Logout",
                        false
                );

        sidebar.getChildren().addAll(
                helpButton,
                logoutButton
        );

        return sidebar;
    }

    // =========================================================
    // NAVIGATION BUTTON
    // =========================================================

    private Button createNavigationButton(
            String icon,
            String text,
            boolean selected
    ) {

        Button button = new Button();

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-text-fill: " +
                (selected
                        ? PRIMARY_BLUE
                        : DARK_TEXT) + ";"
        );

        Label textLabel =
                new Label(text);

        textLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: " +
                (selected
                        ? "bold"
                        : "normal") + ";" +
                "-fx-text-fill: " +
                (selected
                        ? PRIMARY_BLUE
                        : DARK_TEXT) + ";"
        );

        HBox content =
                new HBox(13);

        content.setAlignment(
                Pos.CENTER_LEFT
        );

        content.getChildren().addAll(
                iconLabel,
                textLabel
        );

        button.setGraphic(content);

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setPrefHeight(42);

        button.setAlignment(
                Pos.CENTER_LEFT
        );

        if (selected) {

            button.setStyle(
                    "-fx-background-color: #E8F0FF;" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;"
            );

        } else {

            button.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-background-radius: 8;" +
                    "-fx-cursor: hand;"
            );
        }

        return button;
    }

    // =========================================================
    // TOP BAR
    // =========================================================

    private HBox createTopBar() {

        HBox topBar =
                new HBox(15);

        topBar.setAlignment(
                Pos.CENTER_LEFT
        );

        topBar.setPadding(
                new Insets(10, 22, 10, 20)
        );

        topBar.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 0 0 1 0;"
        );

        Label searchIcon =
                new Label("⌕");

        searchIcon.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        Label searchText =
                new Label(
                        "Search settings..."
                );

        searchText.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #98A2B3;"
        );

        HBox searchBox =
                new HBox(8);

        searchBox.setAlignment(
                Pos.CENTER_LEFT
        );

        searchBox.setPrefWidth(330);
        searchBox.setPrefHeight(38);

        searchBox.setPadding(
                new Insets(0, 12, 0, 12)
        );

        searchBox.setStyle(
                "-fx-background-color: #F5F6FC;" +
                "-fx-background-radius: 8;"
        );

        searchBox.getChildren().addAll(
                searchIcon,
                searchText
        );

        Region topSpacer =
                new Region();

        HBox.setHgrow(
                topSpacer,
                Priority.ALWAYS
        );

        Label notification =
                new Label("♧");

        notification.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label settings =
                new Label("⚙");

        settings.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label administrator =
                new Label(
                        "Hospital Administrator"
                );

        administrator.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label role =
                new Label(
                        "HOSPITAL ADMIN"
                );

        role.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        VBox userInfo =
                new VBox(1);

        userInfo.setAlignment(
                Pos.CENTER_RIGHT
        );

        userInfo.getChildren().addAll(
                administrator,
                role
        );

        Circle avatar =
                new Circle(18);

        avatar.setFill(
                Color.web("#DCE8F8")
        );

        Label avatarText =
                new Label("HA");

        avatarText.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";"
        );

        StackPane avatarBox =
                new StackPane(
                        avatar,
                        avatarText
                );

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

        VBox content =
                new VBox();

        content.setPadding(
                new Insets(24)
        );

        ScrollPane scrollPane =
                new ScrollPane();

        VBox settingsContent =
                new VBox(18);

        settingsContent.setPadding(
                new Insets(2, 4, 30, 2)
        );

        settingsContent.getChildren().add(
                createPageHeader()
        );

        settingsContent.getChildren().add(
                createHospitalInformationCard()
        );

        settingsContent.getChildren().add(
                createContactAddressCard()
        );

        settingsContent.getChildren().add(
                createOperatingHoursCard()
        );

        settingsContent.getChildren().add(
                createEmergencyAndTourismCard()
        );

        settingsContent.getChildren().add(
                createImagesAndDocumentsCard()
        );

        settingsContent.getChildren().add(
                createAccountSettingsCard()
        );

        scrollPane.setContent(
                settingsContent
        );

        scrollPane.setFitToWidth(true);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;"
        );

        content.getChildren().add(
                scrollPane
        );

        VBox.setVgrow(
                scrollPane,
                Priority.ALWAYS
        );

        return content;
    }

    // =========================================================
    // PAGE HEADER
    // =========================================================

    private HBox createPageHeader() {

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox titleBox =
                new VBox(4);

        Label title =
                new Label(
                        "Hospital Profile & Settings"
                );

        title.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label subtitle =
                new Label(
                        "Manage hospital information, availability and account settings"
                );

        subtitle.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        titleBox.getChildren().addAll(
                title,
                subtitle
        );

        Region headerSpacer =
                new Region();

        HBox.setHgrow(
                headerSpacer,
                Priority.ALWAYS
        );

        Button saveButton =
                new Button(
                        "✓  Save Changes"
                );

        saveButton.setPrefHeight(38);

        saveButton.setPadding(
                new Insets(0, 18, 0, 18)
        );

        saveButton.setStyle(
                "-fx-background-color: " +
                PRIMARY_BLUE + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        header.getChildren().addAll(
                titleBox,
                headerSpacer,
                saveButton
        );

        return header;
    }

    // =========================================================
    // HOSPITAL INFORMATION
    // =========================================================

    private VBox createHospitalInformationCard() {

        VBox card =
                createSectionCard();

        card.getChildren().add(
                createSectionHeader(
                        "Hospital Information",
                        "Basic information about your hospital"
                )
        );

        GridPane grid =
                createFormGrid();

        TextField hospitalName =
                createTextField(
                        "CityCare Multispeciality Hospital"
                );

        TextField registrationNumber =
                createTextField(
                        "HSP-2026-00124"
                );

        TextField hospitalType =
                createTextField(
                        "Multispeciality Hospital"
                );

        TextField establishedYear =
                createTextField(
                        "2008"
                );

        addField(
                grid,
                "Hospital Name",
                hospitalName,
                0,
                0
        );

        addField(
                grid,
                "Registration Number",
                registrationNumber,
                1,
                0
        );

        addField(
                grid,
                "Hospital Type",
                hospitalType,
                0,
                1
        );

        addField(
                grid,
                "Established Year",
                establishedYear,
                1,
                1
        );

        card.getChildren().add(
                grid
        );

        return card;
    }

    // =========================================================
    // CONTACT & ADDRESS
    // =========================================================

    private VBox createContactAddressCard() {

        VBox card =
                createSectionCard();

        card.getChildren().add(
                createSectionHeader(
                        "Contact Details & Address",
                        "Hospital contact and location information"
                )
        );

        GridPane grid =
                createFormGrid();

        TextField phone =
                createTextField(
                        "+91 20 4567 8900"
                );

        TextField email =
                createTextField(
                        "contact@citycarehospital.com"
                );

        TextField website =
                createTextField(
                        "www.citycarehospital.com"
                );

        TextField city =
                createTextField(
                        "Pune"
                );

        TextField state =
                createTextField(
                        "Maharashtra"
                );

        TextField postalCode =
                createTextField(
                        "411001"
                );

        TextArea address =
                new TextArea(
                        "123 Healthcare Avenue, Central Business District"
                );

        address.setPrefRowCount(3);

        address.setWrapText(true);

        address.setStyle(
                "-fx-background-color: #FAFBFD;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 7;" +
                "-fx-background-radius: 7;" +
                "-fx-font-size: 10px;"
        );

        addField(
                grid,
                "Phone Number",
                phone,
                0,
                0
        );

        addField(
                grid,
                "Email Address",
                email,
                1,
                0
        );

        addField(
                grid,
                "Website",
                website,
                0,
                1
        );

        addField(
                grid,
                "City",
                city,
                1,
                1
        );

        addField(
                grid,
                "State",
                state,
                0,
                2
        );

        addField(
                grid,
                "Postal Code",
                postalCode,
                1,
                2
        );

        Label addressLabel =
                createFieldLabel("Address");

        VBox addressBox =
                new VBox(6);

        addressBox.getChildren().addAll(
                addressLabel,
                address
        );

        GridPane.setColumnSpan(
                addressBox,
                2
        );

        grid.add(
                addressBox,
                0,
                3
        );

        card.getChildren().add(
                grid
        );

        return card;
    }

    // =========================================================
    // OPERATING HOURS
    // =========================================================

    private VBox createOperatingHoursCard() {

        VBox card =
                createSectionCard();

        card.getChildren().add(
                createSectionHeader(
                        "Operating Hours",
                        "Configure regular hospital working hours"
                )
        );

        GridPane grid =
                createFormGrid();

        String[] days = {
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday",
                "Sunday"
        };

        String[] times = {
                "08:00 AM - 08:00 PM",
                "08:00 AM - 08:00 PM",
                "08:00 AM - 08:00 PM",
                "08:00 AM - 08:00 PM",
                "08:00 AM - 08:00 PM",
                "09:00 AM - 04:00 PM",
                "Emergency Only"
        };

        for (int i = 0; i < days.length; i++) {

            HBox dayRow =
                    createDayRow(
                            days[i],
                            times[i],
                            i != 6
                    );

            grid.add(
                    dayRow,
                    i % 2,
                    i / 2
            );
        }

        card.getChildren().add(
                grid
        );

        return card;
    }

    // =========================================================
    // DAY ROW
    // =========================================================

    private HBox createDayRow(
            String day,
            String hours,
            boolean active
    ) {

        HBox row =
                new HBox(10);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(10)
        );

        row.setStyle(
                "-fx-background-color: #FAFBFD;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #EEF0F4;" +
                "-fx-border-radius: 8;"
        );

        CheckBox checkBox =
                new CheckBox();

        checkBox.setSelected(
                active
        );

        Label dayLabel =
                new Label(day);

        dayLabel.setPrefWidth(75);

        dayLabel.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label hoursLabel =
                new Label(hours);

        hoursLabel.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        row.getChildren().addAll(
                checkBox,
                dayLabel,
                hoursLabel
        );

        return row;
    }

    // =========================================================
    // EMERGENCY + MEDICAL TOURISM
    // =========================================================

    private HBox createEmergencyAndTourismCard() {

        HBox row =
                new HBox(18);

        VBox emergencyCard =
                createSectionCard();

        VBox tourismCard =
                createSectionCard();

        HBox.setHgrow(
                emergencyCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                tourismCard,
                Priority.ALWAYS
        );

        // -----------------------------------------------------
        // EMERGENCY
        // -----------------------------------------------------

        emergencyCard.getChildren().add(
                createSectionHeader(
                        "Emergency Availability",
                        "Configure emergency services"
                )
        );

        HBox emergencyStatus =
                new HBox(10);

        emergencyStatus.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle greenDot =
                new Circle(5);

        greenDot.setFill(
                Color.web(SUCCESS_GREEN)
        );

        Label emergencyLabel =
                new Label(
                        "Emergency Services Available"
                );

        emergencyLabel.setStyle(
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        emergencyStatus.getChildren().addAll(
                greenDot,
                emergencyLabel
        );

        CheckBox ambulance =
                new CheckBox(
                        "24/7 Ambulance Service"
                );

        ambulance.setSelected(true);

        CheckBox emergencyWard =
                new CheckBox(
                        "24/7 Emergency Ward"
                );

        emergencyWard.setSelected(true);

        CheckBox trauma =
                new CheckBox(
                        "Trauma & Critical Care"
                );

        trauma.setSelected(true);

        emergencyCard.getChildren().addAll(
                emergencyStatus,
                ambulance,
                emergencyWard,
                trauma
        );

        // -----------------------------------------------------
        // MEDICAL TOURISM
        // -----------------------------------------------------

        tourismCard.getChildren().add(
                createSectionHeader(
                        "Medical Tourism Support",
                        "International patient services"
                )
        );

        CheckBox tourismSupport =
                new CheckBox(
                        "Medical Tourism Support Available"
                );

        tourismSupport.setSelected(true);

        CheckBox internationalDesk =
                new CheckBox(
                        "International Patient Desk"
                );

        internationalDesk.setSelected(true);

        CheckBox airportPickup =
                new CheckBox(
                        "Airport Pickup Assistance"
                );

        airportPickup.setSelected(false);

        ComboBox<String> languages =
                new ComboBox<>();

        languages.getItems().addAll(
                "English",
                "Hindi",
                "Marathi",
                "Arabic",
                "French"
        );

        languages.setValue(
                "English"
        );

        languages.setPrefWidth(180);

        tourismCard.getChildren().addAll(
                tourismSupport,
                internationalDesk,
                airportPickup,
                createFieldLabel(
                        "Primary Support Language"
                ),
                languages
        );

        row.getChildren().addAll(
                emergencyCard,
                tourismCard
        );

        return row;
    }

    // =========================================================
    // IMAGES + DOCUMENTS
    // =========================================================

    private HBox createImagesAndDocumentsCard() {

        HBox row =
                new HBox(18);

        VBox imagesCard =
                createSectionCard();

        VBox documentsCard =
                createSectionCard();

        HBox.setHgrow(
                imagesCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                documentsCard,
                Priority.ALWAYS
        );

        // -----------------------------------------------------
        // HOSPITAL IMAGES
        // -----------------------------------------------------

        imagesCard.getChildren().add(
                createSectionHeader(
                        "Hospital Images",
                        "Manage hospital photos"
                )
        );

        HBox imagePreview =
                new HBox(10);

        imagePreview.getChildren().add(
                createImagePlaceholder(
                        "Hospital Front"
                )
        );

        imagePreview.getChildren().add(
                createImagePlaceholder(
                        "Reception"
                )
        );

        imagePreview.getChildren().add(
                createImagePlaceholder(
                        "Emergency"
                )
        );

        Button uploadImage =
                new Button(
                        "+  Upload Image"
                );

        uploadImage.setPrefHeight(34);

        uploadImage.setStyle(
                "-fx-background-color: #EAF1FF;" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";" +
                "-fx-background-radius: 7;" +
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        imagesCard.getChildren().addAll(
                imagePreview,
                uploadImage
        );

        // -----------------------------------------------------
        // DOCUMENTS
        // -----------------------------------------------------

        documentsCard.getChildren().add(
                createSectionHeader(
                        "Documents & Licenses",
                        "Hospital registration and certificates"
                )
        );

        documentsCard.getChildren().add(
                createDocumentRow(
                        "Hospital Registration",
                        "PDF • Verified"
                )
        );

        documentsCard.getChildren().add(
                createDocumentRow(
                        "Medical License",
                        "PDF • Verified"
                )
        );

        documentsCard.getChildren().add(
                createDocumentRow(
                        "NABH Accreditation",
                        "PDF • Verified"
                )
        );

        Button uploadDocument =
                new Button(
                        "+  Upload Document"
                );

        uploadDocument.setPrefHeight(34);

        uploadDocument.setStyle(
                "-fx-background-color: #EAF1FF;" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";" +
                "-fx-background-radius: 7;" +
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        documentsCard.getChildren().add(
                uploadDocument
        );

        row.getChildren().addAll(
                imagesCard,
                documentsCard
        );

        return row;
    }

    // =========================================================
    // IMAGE PLACEHOLDER
    // =========================================================

    private VBox createImagePlaceholder(
            String title
    ) {

        VBox box =
                new VBox(5);

        box.setAlignment(
                Pos.CENTER
        );

        box.setPrefWidth(95);

        StackPane imageArea =
                new StackPane();

        imageArea.setPrefSize(
                95,
                65
        );

        imageArea.setStyle(
                "-fx-background-color: #EAF1FF;" +
                "-fx-background-radius: 8;"
        );

        Label imageIcon =
                new Label("▧");

        imageIcon.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";"
        );

        imageArea.getChildren().add(
                imageIcon
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 7px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        box.getChildren().addAll(
                imageArea,
                titleLabel
        );

        return box;
    }

    // =========================================================
    // DOCUMENT ROW
    // =========================================================

    private HBox createDocumentRow(
            String title,
            String details
    ) {

        HBox row =
                new HBox(10);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(8)
        );

        row.setStyle(
                "-fx-background-color: #FAFBFD;" +
                "-fx-background-radius: 7;"
        );

        Label icon =
                new Label("▤");

        icon.setPrefSize(
                28,
                28
        );

        icon.setAlignment(
                Pos.CENTER
        );

        icon.setStyle(
                "-fx-background-color: #EAF1FF;" +
                "-fx-background-radius: 6;" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";"
        );

        VBox detailsBox =
                new VBox(2);

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label detailLabel =
                new Label(details);

        detailLabel.setStyle(
                "-fx-font-size: 7px;" +
                "-fx-text-fill: " + SUCCESS_GREEN + ";"
        );

        detailsBox.getChildren().addAll(
                titleLabel,
                detailLabel
        );

        Region documentSpacer =
                new Region();

        HBox.setHgrow(
                documentSpacer,
                Priority.ALWAYS
        );

        Button viewButton =
                new Button(
                        "View"
                );

        viewButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";" +
                "-fx-font-size: 8px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        row.getChildren().addAll(
                icon,
                detailsBox,
                documentSpacer,
                viewButton
        );

        return row;
    }

    // =========================================================
    // ACCOUNT SETTINGS
    // =========================================================

    private VBox createAccountSettingsCard() {

        VBox card =
                createSectionCard();

        card.getChildren().add(
                createSectionHeader(
                        "Account Settings",
                        "Manage administrator account preferences"
                )
        );

        GridPane grid =
                createFormGrid();

        TextField adminName =
                createTextField(
                        "Hospital Administrator"
                );

        TextField adminEmail =
                createTextField(
                        "admin@citycarehospital.com"
                );

        ComboBox<String> notificationPreference =
                new ComboBox<>();

        notificationPreference.getItems().addAll(
                "All Notifications",
                "Important Only",
                "Email Only",
                "Disabled"
        );

        notificationPreference.setValue(
                "All Notifications"
        );

        notificationPreference.setMaxWidth(
                Double.MAX_VALUE
        );

        addField(
                grid,
                "Administrator Name",
                adminName,
                0,
                0
        );

        addField(
                grid,
                "Administrator Email",
                adminEmail,
                1,
                0
        );

        addField(
                grid,
                "Notification Preference",
                notificationPreference,
                0,
                1
        );

        CheckBox emailNotifications =
                new CheckBox(
                        "Receive email notifications"
                );

        emailNotifications.setSelected(true);

        CheckBox securityAlerts =
                new CheckBox(
                        "Receive security alerts"
                );

        securityAlerts.setSelected(true);

        VBox notificationBox =
                new VBox(8);

        notificationBox.getChildren().addAll(
                emailNotifications,
                securityAlerts
        );

        grid.add(
                notificationBox,
                1,
                1
        );

        card.getChildren().add(
                grid
        );

        HBox accountActions =
                new HBox(10);

        Button changePassword =
                new Button(
                        "Change Password"
                );

        changePassword.setPrefHeight(34);

        changePassword.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 7;" +
                "-fx-background-radius: 7;" +
                "-fx-text-fill: " + DARK_TEXT + ";" +
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        Button deleteAccount =
                new Button(
                        "Deactivate Account"
                );

        deleteAccount.setPrefHeight(34);

        deleteAccount.setStyle(
                "-fx-background-color: #FFF0F0;" +
                "-fx-text-fill: " + ERROR_RED + ";" +
                "-fx-background-radius: 7;" +
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        accountActions.getChildren().addAll(
                changePassword,
                deleteAccount
        );

        card.getChildren().add(
                accountActions
        );

        return card;
    }

    // =========================================================
    // SECTION CARD
    // =========================================================

    private VBox createSectionCard() {

        VBox card =
                new VBox(14);

        card.setPadding(
                new Insets(18)
        );

        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;"
        );

        return card;
    }

    // =========================================================
    // SECTION HEADER
    // =========================================================

    private HBox createSectionHeader(
            String title,
            String subtitle
    ) {

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox titleBox =
                new VBox(3);

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        Label subtitleLabel =
                new Label(subtitle);

        subtitleLabel.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-text-fill: " + SECONDARY_TEXT + ";"
        );

        titleBox.getChildren().addAll(
                titleLabel,
                subtitleLabel
        );

        header.getChildren().add(
                titleBox
        );

        return header;
    }

    // =========================================================
    // FORM GRID
    // =========================================================

    private GridPane createFormGrid() {

        GridPane grid =
                new GridPane();

        grid.setHgap(18);
        grid.setVgap(12);

        ColumnConstraintsHelper.configure(
                grid
        );

        return grid;
    }

    // =========================================================
    // ADD FIELD
    // =========================================================

    private void addField(
            GridPane grid,
            String labelText,
            javafx.scene.Node field,
            int column,
            int row
    ) {

        VBox fieldBox =
                new VBox(6);

        Label label =
                createFieldLabel(
                        labelText
                );

        fieldBox.getChildren().addAll(
                label,
                field
        );

        grid.add(
                fieldBox,
                column,
                row
        );
    }

    // =========================================================
    // FIELD LABEL
    // =========================================================

    private Label createFieldLabel(
            String text
    ) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-font-size: 8px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
        );

        return label;
    }

    // =========================================================
    // TEXT FIELD
    // =========================================================

    private TextField createTextField(
            String value
    ) {

        TextField field =
                new TextField(value);

        field.setPrefHeight(36);

        field.setMaxWidth(
                Double.MAX_VALUE
        );

        field.setStyle(
                "-fx-background-color: #FAFBFD;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 7;" +
                "-fx-background-radius: 7;" +
                "-fx-font-size: 10px;" +
                "-fx-text-fill: " + DARK_TEXT + ";" +
                "-fx-padding: 0 10 0 10;"
        );

        return field;
    }

    // =========================================================
    // COLUMN CONFIGURATION HELPER
    // =========================================================

    private static class ColumnConstraintsHelper {

        private static void configure(
                GridPane grid
        ) {

            javafx.scene.layout.ColumnConstraints firstColumn =
                    new javafx.scene.layout.ColumnConstraints();

            javafx.scene.layout.ColumnConstraints secondColumn =
                    new javafx.scene.layout.ColumnConstraints();

            firstColumn.setPercentWidth(50);
            secondColumn.setPercentWidth(50);

            firstColumn.setHgrow(
                    Priority.ALWAYS
            );

            secondColumn.setHgrow(
                    Priority.ALWAYS
            );

            grid.getColumnConstraints().addAll(
                    firstColumn,
                    secondColumn
            );
        }
    }
}