package com.healthsphere.view.doctor;

import com.healthsphere.util.Navigation;
import com.healthsphere.util.ResourceImage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * PatientDetailsView displays detailed patient information.
 * Features dynamic patient selection, adding new patients via a dialog, 
 * full page vertical scrolling, and sidebar navigation.
 */
public class PatientDetailsView {

    private final Stage stage;
    private final Scene scene;

    // Dynamic UI Component References for Patient Switching
    private Label nameLbl;
    private Label metaLbl;
    private Label idLbl;
    private ImageView profileImg;
    private VBox vitalsContent;
    private Label historyText;
    private HBox patientSelectorBar;
    private final List<Button> patientTabButtons = new ArrayList<>();

    // Mock Data Representation
    private static class PatientData {
        String name;
        String meta;
        String id;
        String imgPath;
        String[] vitals;
        String history;

        PatientData(String name, String meta, String id, String imgPath, String[] vitals, String history) {
            this.name = name;
            this.meta = meta;
            this.id = id;
            this.imgPath = imgPath;
            this.vitals = vitals;
            this.history = history;
        }
    }

    private final List<PatientData> patientList = new ArrayList<>();

    public PatientDetailsView(Stage stage) {
        this.stage = stage;
        initPatientData();
        this.scene = createScene();
    }

    private void initPatientData() {
        patientList.add(new PatientData(
                "Robert Chen",
                "Male • 42 Years Old • Blood Group: A+",
                "Patient ID: #PID-8842",
                "/images/mocks/robert_chen.png",
                new String[]{"• Heart Rate: 72 bpm", "• Blood Pressure: 120/80 mmHg", "• Temperature: 98.6 °F", "• SpO2: 99%"},
                "Patient has a history of mild migraine. No known drug allergies reported. Last consultation conducted on Oct 26, 2023."
        ));
        patientList.add(new PatientData(
                "Emily Watson",
                "Female • 29 Years Old • Blood Group: O+",
                "Patient ID: #PID-3109",
                "/images/mocks/robert_chen.png",
                new String[]{"• Heart Rate: 78 bpm", "• Blood Pressure: 115/75 mmHg", "• Temperature: 98.4 °F", "• SpO2: 98%"},
                "Patient reports seasonal allergies and mild asthma. Prescribed inhaler for exercise-induced bronchospasm. Last consultation on Nov 12, 2023."
        ));
        patientList.add(new PatientData(
                "Michael Brown",
                "Male • 56 Years Old • Blood Group: B+",
                "Patient ID: #PID-5521",
                "/images/mocks/robert_chen.png",
                new String[]{"• Heart Rate: 84 bpm", "• Blood Pressure: 135/88 mmHg", "• Temperature: 98.8 °F", "• SpO2: 96%"},
                "Type 2 Diabetes mellitus under dietary management and metformin regime. Regular routine monitoring required. Last consultation on Dec 04, 2023."
        ));
        patientList.add(new PatientData(
                "Sophia Martinez",
                "Female • 35 Years Old • Blood Group: AB-",
                "Patient ID: #PID-9012",
                "/images/mocks/robert_chen.png",
                new String[]{"• Heart Rate: 68 bpm", "• Blood Pressure: 118/76 mmHg", "• Temperature: 98.2 °F", "• SpO2: 100%"},
                "Post-surgery follow-up for ACL reconstruction. Recovery progressing normally with daily physical therapy. Last consultation on Jan 15, 2024."
        ));
    }

    public Scene getScene() {
        return this.scene;
    }

    private Scene createScene() {
        BorderPane mainRoot = new BorderPane();
        mainRoot.getStyleClass().add("root-pane");

        // --- Sidebar (Left Navigation) ---
        VBox sidebar = createSidebar();
        mainRoot.setLeft(sidebar);

        // --- Main Content Area ---
        VBox contentArea = new VBox(20);
        contentArea.setPadding(new Insets(20, 30, 30, 30));
        contentArea.getStyleClass().add("content-area");

        // Top Header
        HBox topHeader = createTopHeader();
        contentArea.getChildren().add(topHeader);

        // Title and Add Patient Header Section
        BorderPane titleSection = createTitleSection();
        contentArea.getChildren().add(titleSection);

        // Patient Selector Bar (Multiple Patients Tab)
        patientSelectorBar = createPatientSelectorBar();
        contentArea.getChildren().add(patientSelectorBar);

        // Patient Overview Header Card
        VBox patientCard = createPatientOverviewCard();
        contentArea.getChildren().add(patientCard);

        // Information Grid Sections
        GridPane detailsGrid = createDetailsGrid();
        contentArea.getChildren().add(detailsGrid);

        mainRoot.setCenter(contentArea);

        // --- Outer ScrollPane to enable vertical scrolling down to the bottom ---
        ScrollPane outerScrollPane = new ScrollPane(mainRoot);
        outerScrollPane.setFitToWidth(true);
        outerScrollPane.setFitToHeight(true);
        outerScrollPane.getStyleClass().add("content-scrollpane");

        Scene patientDetailsScene = new Scene(outerScrollPane, stage.getWidth(), stage.getHeight());

        try {
            patientDetailsScene.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/css/appointments.css")).toExternalForm());
        } catch (Exception ignored) {}

        return patientDetailsScene;
    }

    private HBox createPatientSelectorBar() {
        HBox bar = new HBox(10);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(5, 0, 5, 0));

        Label selectLabel = new Label("Select Patient:");
        selectLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569; -fx-font-size: 13px;");
        bar.getChildren().add(selectLabel);

        rebuildPatientTabs(bar);

        return bar;
    }

    private void rebuildPatientTabs(HBox bar) {
        bar.getChildren().removeIf(node -> node instanceof Button);
        patientTabButtons.clear();

        for (int i = 0; i < patientList.size(); i++) {
            PatientData patient = patientList.get(i);
            Button patientBtn = new Button(patient.name);
            patientBtn.setCursor(javafx.scene.Cursor.HAND);

            final int index = i;
            patientBtn.setOnAction(e -> switchPatient(index));
            patientTabButtons.add(patientBtn);
            bar.getChildren().add(patientBtn);
        }

        updatePatientTabStyles(0);
    }

    private void switchPatient(int index) {
        if (index < 0 || index >= patientList.size()) return;
        PatientData patient = patientList.get(index);

        nameLbl.setText(patient.name);
        metaLbl.setText(patient.meta);
        idLbl.setText(patient.id);
        profileImg.setImage(ResourceImage.load(patient.imgPath));

        vitalsContent.getChildren().clear();
        for (String vital : patient.vitals) {
            vitalsContent.getChildren().add(new Label(vital));
        }

        historyText.setText(patient.history);

        updatePatientTabStyles(index);
    }

    private void updatePatientTabStyles(int activeIndex) {
        for (int i = 0; i < patientTabButtons.size(); i++) {
            Button btn = patientTabButtons.get(i);
            if (i == activeIndex) {
                btn.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 6 14;");
            } else {
                btn.setStyle("-fx-background-color: #E2E8F0; -fx-text-fill: #334155; -fx-font-weight: normal; -fx-background-radius: 6px; -fx-padding: 6 14;");
            }
        }
    }

    private BorderPane createTitleSection() {
        BorderPane section = new BorderPane();

        VBox titleBox = new VBox(2);
        Label mainTitle = new Label("Patient Details");
        mainTitle.getStyleClass().add("page-title");
        Label subTitle = new Label("Comprehensive medical record and personal profile");
        subTitle.getStyleClass().add("page-subtitle");
        titleBox.getChildren().addAll(mainTitle, subTitle);

        // CHANGED: Edit Profile replaced with Add Patient
        Button addPatientBtn = new Button("+ Add Patient");
        addPatientBtn.getStyleClass().add("btn-primary-action");

        // ACTION: Open Modal Dialog to Add New Patient
        addPatientBtn.setOnAction(e -> openAddPatientDialog());

        section.setLeft(titleBox);
        section.setRight(addPatientBtn);
        return section;
    }

    private void openAddPatientDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setTitle("Add New Patient");

        VBox form = new VBox(12);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #FFFFFF;");

        Label dialogTitle = new Label("New Patient Information");
        dialogTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name (e.g. John Doe)");

        TextField ageField = new TextField();
        ageField.setPromptText("Age (e.g. 30)");

        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("Male", "Female", "Other");
        genderBox.getSelectionModel().selectFirst();

        TextField bloodGroupField = new TextField();
        bloodGroupField.setPromptText("Blood Group (e.g. O+)");

        TextField vitalsField = new TextField();
        vitalsField.setPromptText("Vitals (comma-separated, e.g. HR: 72 bpm, BP: 120/80 mmHg)");

        TextArea historyArea = new TextArea();
        historyArea.setPromptText("Medical History & Notes...");
        historyArea.setPrefRowCount(3);

        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> dialog.close());

        Button saveBtn = new Button("Add Patient");
        saveBtn.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-font-weight: bold;");
        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                name = "New Patient";
            }
            String age = ageField.getText().trim().isEmpty() ? "30" : ageField.getText().trim();
            String gender = genderBox.getValue();
            String bloodGroup = bloodGroupField.getText().trim().isEmpty() ? "A+" : bloodGroupField.getText().trim();
            
            String meta = gender + " • " + age + " Years Old • Blood Group: " + bloodGroup;
            String randomId = "Patient ID: #PID-" + (1000 + new Random().nextInt(9000));
            
            String[] vitals;
            if (!vitalsField.getText().trim().isEmpty()) {
                String[] rawVitals = vitalsField.getText().split(",");
                vitals = new String[rawVitals.length];
                for (int i = 0; i < rawVitals.length; i++) {
                    vitals[i] = "• " + rawVitals[i].trim();
                }
            } else {
                vitals = new String[]{"• Heart Rate: 72 bpm", "• Blood Pressure: 120/80 mmHg", "• Temperature: 98.6 °F", "• SpO2: 99%"};
            }

            String history = historyArea.getText().trim().isEmpty() 
                ? "No prior medical history recorded." 
                : historyArea.getText().trim();

            PatientData newPatient = new PatientData(
                name, meta, randomId, "/images/mocks/robert_chen.png", vitals, history
            );

            patientList.add(newPatient);
            rebuildPatientTabs(patientSelectorBar);
            switchPatient(patientList.size() - 1);
            dialog.close();
        });

        actionButtons.getChildren().addAll(cancelBtn, saveBtn);

        form.getChildren().addAll(
            dialogTitle,
            new Label("Name:"), nameField,
            new Label("Age:"), ageField,
            new Label("Gender:"), genderBox,
            new Label("Blood Group:"), bloodGroupField,
            new Label("Vitals:"), vitalsField,
            new Label("History & Notes:"), historyArea,
            actionButtons
        );

        Scene dialogScene = new Scene(form, 400, 500);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    private VBox createPatientOverviewCard() {
        VBox card = new VBox(15);
        card.getStyleClass().add("filter-container-card");
        card.setPadding(new Insets(20));

        HBox profileHeader = new HBox(20);
        profileHeader.setAlignment(Pos.CENTER_LEFT);

        PatientData initialData = patientList.get(0);

        profileImg = new ImageView(ResourceImage.load(initialData.imgPath));
        profileImg.setFitWidth(70);
        profileImg.setFitHeight(70);
        Circle clip = new Circle(35, 35, 35);
        profileImg.setClip(clip);

        VBox infoBox = new VBox(4);
        nameLbl = new Label(initialData.name);
        nameLbl.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        metaLbl = new Label(initialData.meta);
        metaLbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748B;");

        idLbl = new Label(initialData.id);
        idLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #3B82F6; -fx-font-weight: bold;");

        infoBox.getChildren().addAll(nameLbl, metaLbl, idLbl);
        profileHeader.getChildren().addAll(profileImg, infoBox);

        card.getChildren().add(profileHeader);
        return card;
    }

    private GridPane createDetailsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        PatientData initialData = patientList.get(0);

        // Vitals Card
        VBox vitalsCard = new VBox(12);
        vitalsCard.getStyleClass().add("filter-container-card");
        vitalsCard.setPadding(new Insets(20));

        Label vitalsTitle = new Label("Recent Vitals");
        vitalsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        vitalsContent = new VBox(8);
        for (String vital : initialData.vitals) {
            vitalsContent.getChildren().add(new Label(vital));
        }

        vitalsCard.getChildren().addAll(vitalsTitle, vitalsContent);

        // Medical History Card
        VBox historyCard = new VBox(12);
        historyCard.getStyleClass().add("filter-container-card");
        historyCard.setPadding(new Insets(20));

        Label historyTitle = new Label("Medical History & Notes");
        historyTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        historyText = new Label(initialData.history);
        historyText.setWrapText(true);
        historyText.setStyle("-fx-text-fill: #475569; -fx-font-size: 13px;");

        historyCard.getChildren().addAll(historyTitle, historyText);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        grid.add(vitalsCard, 0, 0);
        grid.add(historyCard, 1, 0);

        return grid;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPadding(new Insets(30, 15, 30, 15));
        sidebar.getStyleClass().add("sidebar");
        sidebar.setMinWidth(250);

        HBox logoSection = new HBox(10);
        logoSection.setPadding(new Insets(0, 0, 30, 0));
        logoSection.setAlignment(Pos.CENTER_LEFT);

        StackPane logoIconBox = new StackPane();
        logoIconBox.getStyleClass().add("logo-icon-box");
        Label logoAbbr = new Label("HS");
        logoAbbr.getStyleClass().add("logo-icon-text");
        logoIconBox.getChildren().add(logoAbbr);

        VBox logoText = new VBox(0);
        Label appName = new Label("Health-Sphere");
        appName.getStyleClass().add("logo-name");
        Label doctorSubtext = new Label("Doctor Dashboard");
        doctorSubtext.getStyleClass().add("logo-subtext");
        logoText.getChildren().addAll(appName, doctorSubtext);
        logoSection.getChildren().addAll(logoIconBox, logoText);

        VBox navItems = new VBox(8);
        String[] tabs = {
            "Dashboard", "Today's Schedule", "Appointments", "Patient Details",
            "Medical Reports & Prescription", "Availability & Schedule", "Doctor Profile", "AI Health Assistant"
        };
        String[] icons = {
            "ic_dashboard", "ic_schedule", "ic_appointments", "ic_patient",
            "ic_reports", "ic_availability", "ic_profile", "ic_ai"
        };

        for (int i = 0; i < tabs.length; i++) {
            HBox navTab = new HBox(15);
            navTab.getStyleClass().add("nav-tab");
            navTab.setAlignment(Pos.CENTER_LEFT);

            if (i == 3) {
                navTab.getStyleClass().add("nav-tab-active");
            }

            ImageView icon = new ImageView(ResourceImage.load("/images/icons/" + icons[i] + ".png"));
            icon.setFitWidth(18); icon.setFitHeight(18);

            Label tabLabel = new Label(tabs[i]);
            tabLabel.getStyleClass().add("nav-text");

            navTab.getChildren().addAll(icon, tabLabel);
            navItems.getChildren().add(navTab);

            final int index = i;
            navTab.setOnMouseClicked(e -> handleSidebarTabClick(index));
        }

        VBox footer = new VBox(15);
        footer.setAlignment(Pos.BOTTOM_CENTER);
        VBox.setVgrow(footer, Priority.ALWAYS);

        HBox doctorProfile = new HBox(12);
        doctorProfile.getStyleClass().add("sidebar-profile");
        doctorProfile.setAlignment(Pos.CENTER_LEFT);

        ImageView profilePhoto = new ImageView(ResourceImage.load("/images/doctor/portrait-3d-male-doctor.png"));
        profilePhoto.setFitWidth(28); profilePhoto.setFitHeight(28);
        Circle profileClip = new Circle(14, 14, 14);
        profilePhoto.setClip(profileClip);

        VBox profileText = new VBox(0);
        Label doctorName = new Label("Dr. Sarah");
        doctorName.getStyleClass().add("sidebar-profile-name");
        profileText.getChildren().add(doctorName);
        doctorProfile.getChildren().addAll(profilePhoto, profileText);

        doctorProfile.setOnMouseClicked(e -> Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene()));

        HBox logout = new HBox(15);
        logout.getStyleClass().add("nav-tab");
        logout.setAlignment(Pos.CENTER_LEFT);

        ImageView logoutIcon = new ImageView(ResourceImage.load("/images/icons/ic_logout.png"));
        logoutIcon.setFitWidth(18); logoutIcon.setFitHeight(18);

        Label logoutLabel = new Label("Logout");
        logoutLabel.getStyleClass().add("nav-text-logout");
        logout.getChildren().addAll(logoutIcon, logoutLabel);

        footer.getChildren().addAll(doctorProfile, logout);
        sidebar.getChildren().addAll(logoSection, navItems, footer);
        return sidebar;
    }

    private void handleSidebarTabClick(int index) {
        switch (index) {
            case 0: Navigation.goTo(stage, () -> new DoctorDashboardView(stage).getScene()); break;
            case 1: Navigation.goTo(stage, () -> new TodaysScheduleView(stage).getScene()); break;
            case 2: Navigation.goTo(stage, () -> new AppointmentsView(stage).getScene()); break;
            case 3: Navigation.goTo(stage, () -> new PatientDetailsView(stage).getScene()); break;
            case 4: Navigation.goTo(stage, () -> new MedicalReportsView(stage).getScene()); break;
            case 5: Navigation.goTo(stage, () -> new AvailabilityScheduleView(stage).getScene()); break;
            case 6: Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene()); break;
            case 7: Navigation.goTo(stage, () -> new AIHealthAssistantView(stage).getScene()); break;
            default: break;
        }
    }

    private HBox createTopHeader() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_RIGHT);

        HBox searchField = new HBox(8);
        searchField.getStyleClass().add("search-input-box");
        searchField.setAlignment(Pos.CENTER_LEFT);

        ImageView searchIcon = new ImageView(ResourceImage.load("/images/icons/ic_search.png"));
        searchIcon.setFitWidth(16); searchIcon.setFitHeight(16);

        TextField searchInput = new TextField();
        searchInput.setPromptText("Search patients or IDs...");
        searchInput.getStyleClass().add("search-text-field");
        searchField.getChildren().addAll(searchIcon, searchInput);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox rightIcons = new HBox(18);
        rightIcons.setAlignment(Pos.CENTER_RIGHT);

        StackPane notificationBox = new StackPane();
        ImageView bellIcon = new ImageView(ResourceImage.load("/images/icons/ic_bell.png"));
        bellIcon.setFitWidth(18); bellIcon.setFitHeight(18);
        Circle badge = new Circle(4, Color.RED);
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        notificationBox.getChildren().addAll(bellIcon, badge);
        notificationBox.getStyleClass().add("clickable-icon");

        ImageView userAvatar = new ImageView(ResourceImage.load("/images/doctor/portrait-3d-male-doctor.png"));
        userAvatar.setFitWidth(32); userAvatar.setFitHeight(32);
        Circle clip = new Circle(16, 16, 16);
        userAvatar.setClip(clip);
        userAvatar.getStyleClass().add("clickable-icon");
        userAvatar.setOnMouseClicked(e -> Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene()));

        rightIcons.getChildren().addAll(notificationBox, userAvatar);
        topBar.getChildren().addAll(searchField, spacer, rightIcons);
        return topBar;
    }
}