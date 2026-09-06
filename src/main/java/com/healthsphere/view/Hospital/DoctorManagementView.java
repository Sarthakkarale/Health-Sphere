package com.healthsphere.view.hospital;

import com.healthsphere.controller.hospital.DoctorController;
import com.healthsphere.model.DoctorProfile;
import com.healthsphere.model.HospitalDoctorDetails;
import com.healthsphere.util.Navigation;
import com.healthsphere.util.ShimmerPlaceholder;
import com.healthsphere.util.SummaryCard;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DoctorManagementView {

    // =========================================================
    // COLOR PALETTE
    // =========================================================

    private static final String PRIMARY_BLUE = "#2F80ED";
    private static final String PRIMARY_LIGHT = "#EEF3FF";
    private static final String DARK_TEXT = "#172B4D";
    private static final String SECONDARY_TEXT = "#64748B";
    private static final String LIGHT_BACKGROUND = "linear-gradient(to bottom right, #F4F8FC, #EEF3FF)";
    private static final String CARD_BG = "#FFFFFF";
    private static final String BORDER = "#E2E8F0";

    private static final String SIDEBAR_BG = "#12355B";
    private static final String SIDEBAR_HOVER = "#1D4E7A";
    private static final String SIDEBAR_TEXT_MUTED = "#D6E4F0";
    private static final String SIDEBAR_BORDER = "#1D4E7A";

    private static final String SUCCESS_GREEN = "#059669";
    private static final String SUCCESS_LIGHT = "#ECFDF5";

    private static final String ERROR_RED = "#DC2626";
    private static final String ERROR_LIGHT = "#FEF2F2";

    private static final String WARNING_ORANGE = "#D97706";
    private static final String WARNING_LIGHT = "#FFFBEB";

    private static final String PURPLE = "#7C3AED";
    private static final String PURPLE_LIGHT = "#F5F3FF";

    // =========================================================
    // CONTROLLER
    // =========================================================

    private final DoctorController doctorController;

    // =========================================================
    // UI MODEL
    // =========================================================

    public static class Doctor {

        private String id;
        private String associationId;
        private String name;
        private String department;
        private String qualification;
        private String status;

        public Doctor(
                String id,
                String associationId,
                String name,
                String department,
                String qualification,
                String status) {

            this.id = id;
            this.associationId = associationId;
            this.name = name;
            this.department = department;
            this.qualification = qualification;
            this.status = status;
        }

        public String getId() {
            return id;
        }

        public String getAssociationId() {
            return associationId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public String getQualification() {
            return qualification;
        }

        public void setQualification(String qualification) {
            this.qualification = qualification;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getInitials() {

            if (name == null
                    || name.trim().isEmpty()) {

                return "DR";
            }

            String cleanName =
                    name.replace("Dr. ", "")
                            .replace("Dr.", "")
                            .trim();

            String[] parts =
                    cleanName.split("\\s+");

            if (parts.length >= 2) {

                return (
                        ""
                                + parts[0].charAt(0)
                                + parts[1].charAt(0)
                ).toUpperCase();
            }

            if (parts.length == 1
                    && !parts[0].isEmpty()) {

                return (
                        ""
                                + parts[0].charAt(0)
                ).toUpperCase();
            }

            return "DR";
        }
    }

    // =========================================================
    // DOCTOR PROFILE ITEM
    // =========================================================

    /*
     * This class is used only by the Add Doctor ComboBox.
     *
     * It keeps the complete DoctorProfile internally,
     * while showing a readable doctor name and email
     * to the Hospital Administrator.
     */
    private static class DoctorProfileItem {

        private final DoctorProfile profile;

        public DoctorProfileItem(
                DoctorProfile profile) {

            this.profile = profile;
        }

        public DoctorProfile getProfile() {

            return profile;
        }

        @Override
        public String toString() {

            if (profile == null) {
                return "Unknown Doctor";
            }

            String firstName =
                    profile.getFirstName() == null
                            ? ""
                            : profile.getFirstName().trim();

            String lastName =
                    profile.getLastName() == null
                            ? ""
                            : profile.getLastName().trim();

            String fullName =
                    (firstName + " " + lastName).trim();

            if (fullName.isEmpty()) {
                fullName = "Unknown Doctor";
            }

            String email =
                    profile.getEmail() == null
                            ? ""
                            : profile.getEmail().trim();

            if (!email.isEmpty()) {

                return "Dr. "
                        + fullName
                        + " - "
                        + email;
            }

            return "Dr. " + fullName;
        }
    }

    // =========================================================
    // DATA STATE
    // =========================================================

    private final ObservableList<Doctor> masterDoctorList =
            FXCollections.observableArrayList();

    private FilteredList<Doctor> filteredDoctorList;

    // =========================================================
    // UI REFERENCES
    // =========================================================

    private Label totalDocsValLabel;
    private Label activeDocsValLabel;
    private Label onLeaveDocsValLabel;
    private Label totalDeptValLabel;
    private Label doctorCountHeaderLabel;

    private VBox doctorRowsContainer;

    private TextField searchField;

    private ComboBox<String> departmentFilter;

    private ComboBox<String> statusFilter;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public DoctorManagementView() {

        this.doctorController =
                new DoctorController();
    }

    // =========================================================
    // CREATE SCENE
    // =========================================================

    public Scene createScene(Stage stage) {

        loadDoctorData();

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color: "
                        + LIGHT_BACKGROUND
                        + ";"
        );

        root.setLeft(
                HospitalSidebar.createSidebar(stage, HospitalSidebar.HospitalTab.DOCTORS)
        );

        root.setTop(
                createTopBar()
        );

        ScrollPane scrollPane =
                new ScrollPane(
                        createMainContent(stage)
                );

        scrollPane.setFitToWidth(true);

        scrollPane.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;" +
                "-fx-padding: 0;"
        );

        root.setCenter(
                scrollPane
        );

        applyFiltersAndRefreshUI();

        return new Scene(
                root,
                stage.getWidth() > 0 ? stage.getWidth() : 1200,
                stage.getHeight() > 0 ? stage.getHeight() : 750
        );
    }

    // =========================================================
    // LOAD REAL HOSPITAL DOCTOR DATA
    // =========================================================

    private void loadDoctorData() {
        if (doctorRowsContainer != null) {
            doctorRowsContainer.getChildren().clear();
            doctorRowsContainer.getChildren().add(ShimmerPlaceholder.createListShimmer(4));
        }

        javafx.concurrent.Task<List<HospitalDoctorDetails>> loadTask =
                new javafx.concurrent.Task<>() {
                    @Override
                    protected List<HospitalDoctorDetails> call() throws Exception {
                        return doctorController.getAllDoctorDetails();
                    }
                };

        loadTask.setOnSucceeded(event -> {
            masterDoctorList.clear();
            List<HospitalDoctorDetails> doctors = loadTask.getValue();
            if (doctors != null) {
                for (HospitalDoctorDetails details : doctors) {
                    if (details == null) {
                        continue;
                    }
                    String doctorName = details.getFullName();
                    if (doctorName == null || doctorName.trim().isEmpty()) {
                        doctorName = "Unknown Doctor";
                    } else if (!doctorName.trim().startsWith("Dr.")) {
                        doctorName = "Dr. " + doctorName.trim();
                    }
                    String department = details.getDepartmentId();
                    if (department == null || department.trim().isEmpty()) {
                        department = "Unassigned";
                    }
                    String qualification = details.getQualification();
                    if (qualification == null || qualification.trim().isEmpty()) {
                        qualification = "Not Specified";
                    }
                    String status = details.getStatus();
                    if (status == null || status.trim().isEmpty()) {
                        status = "Inactive";
                    }

                    masterDoctorList.add(
                            new Doctor(
                                    details.getDoctorId(),
                                    details.getAssociationId(),
                                    doctorName,
                                    department,
                                    qualification,
                                    status
                            )
                    );
                }
            }
            filteredDoctorList = new FilteredList<>(masterDoctorList, doctor -> true);
            applyFiltersAndRefreshUI();
        });

        loadTask.setOnFailed(event -> {
            masterDoctorList.clear();
            filteredDoctorList = new FilteredList<>(masterDoctorList, doctor -> true);
            applyFiltersAndRefreshUI();
            showAlert("Unable to Load Doctors", getRootMessage(loadTask.getException()));
        });

        new Thread(loadTask).start();
    }

    // =========================================================
    // DARK SIDEBAR
    // =========================================================

    private VBox createSidebar(Stage stage) {
        return HospitalSidebar.createSidebar(stage, HospitalSidebar.HospitalTab.DOCTORS);
    }

    // =========================================================
    // NAVIGATION BUTTON
    // =========================================================

    private Button createNavigationButton(
            String icon,
            String text,
            boolean selected) {

        Button button =
                new Button();

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-text-fill: "
                        + (
                        selected
                                ? "#FFFFFF"
                                : SIDEBAR_TEXT_MUTED
                )
                        + ";"
        );

        Label textLabel =
                new Label(text);

        textLabel.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: "
                        + (
                        selected
                                ? "bold"
                                : "500"
                )
                        + ";" +
                "-fx-text-fill: "
                        + (
                        selected
                                ? "#FFFFFF"
                                : SIDEBAR_TEXT_MUTED
                )
                        + ";"
        );

        HBox content =
                new HBox(12);

        content.setAlignment(
                Pos.CENTER_LEFT
        );

        content.getChildren().addAll(
                iconLabel,
                textLabel
        );

        button.setGraphic(
                content
        );

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setPrefHeight(42);

        button.setAlignment(
                Pos.CENTER_LEFT
        );

        button.setPadding(
                new Insets(
                        0,
                        12,
                        0,
                        12
                )
        );

        String baseStyle =
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;";

        if (selected) {

            button.setStyle(
                    baseStyle +
                    "-fx-background-color: "
                            + PRIMARY_BLUE
                            + ";"
            );

        } else {

            button.setStyle(
                    baseStyle +
                    "-fx-background-color: transparent;"
            );

            button.setOnMouseEntered(
                    e ->
                            button.setStyle(
                                    baseStyle +
                                    "-fx-background-color: "
                                            + SIDEBAR_HOVER
                                            + ";"
                            )
            );

            button.setOnMouseExited(
                    e ->
                            button.setStyle(
                                    baseStyle +
                                    "-fx-background-color: transparent;"
                            )
            );
        }

        return button;
    }

    // =========================================================
    // TOP BAR
    // =========================================================

    private HBox createTopBar() {

        HBox topBar =
                new HBox(16);

        topBar.setAlignment(
                Pos.CENTER_LEFT
        );

        topBar.setPadding(
                new Insets(
                        12,
                        28,
                        12,
                        28
                )
        );

        topBar.setStyle(
                "-fx-background-color: "
                        + CARD_BG
                        + ";" +
                "-fx-border-color: "
                        + BORDER
                        + ";" +
                "-fx-border-width: 0 0 1 0;"
        );

        Label searchIcon =
                new Label("⌕");

        searchIcon.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        TextField topSearchField =
                new TextField();

        topSearchField.setPromptText(
                "Search doctors, departments..."
        );

        topSearchField.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-prompt-text-fill: #94A3B8;" +
                "-fx-font-size: 13px;" +
                "-fx-text-inner-color: "
                        + DARK_TEXT
                        + ";"
        );

        topSearchField.textProperty()
                .addListener(
                        (obs, oldValue, newValue) -> {

                            if (searchField != null) {

                                searchField.setText(
                                        newValue
                                );
                            }
                        }
                );

        HBox.setHgrow(
                topSearchField,
                Priority.ALWAYS
        );

        HBox searchBox =
                new HBox(8);

        searchBox.setAlignment(
                Pos.CENTER_LEFT
        );

        searchBox.setPrefWidth(360);

        searchBox.setPrefHeight(40);

        searchBox.setPadding(
                new Insets(
                        0,
                        12,
                        0,
                        12
                )
        );

        searchBox.setStyle(
                "-fx-background-color: "
                        + LIGHT_BACKGROUND
                        + ";" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: "
                        + BORDER
                        + ";" +
                "-fx-border-radius: 8;"
        );

        searchBox.getChildren().addAll(
                searchIcon,
                topSearchField
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label notification =
                new Label("🔔");

        notification.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-cursor: hand;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        notification.setOnMouseClicked(
                e ->
                        showAlert(
                                "Notifications",
                                "You have 0 new notifications."
                        )
        );

        Label settings =
                new Label("⚙");

        settings.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-cursor: hand;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        Label administrator =
                new Label(
                        "Hospital Administrator"
                );

        administrator.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Label role =
                new Label("HOSPITAL ADMIN");

        role.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        VBox userInfo =
                new VBox(2);

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
                Color.web(PRIMARY_LIGHT)
        );

        avatar.setStroke(
                Color.web(BORDER)
        );

        Label avatarText =
                new Label("HA");

        avatarText.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: "
                        + PRIMARY_BLUE
                        + ";"
        );

        StackPane avatarBox =
                new StackPane(
                        avatar,
                        avatarText
                );

        topBar.getChildren().addAll(
                searchBox,
                spacer,
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

    private VBox createMainContent(
            Stage stage) {

        VBox content =
                new VBox(24);

        content.setPadding(
                new Insets(28)
        );

        content.setStyle(
                "-fx-background-color: "
                        + LIGHT_BACKGROUND
                        + ";"
        );

        content.getChildren().addAll(
                createPageHeader(stage),
                createStatistics(),
                createSearchAndFilters(stage),
                createDoctorList(stage)
        );

        return content;
    }

    // =========================================================
    // PAGE HEADER
    // =========================================================

    private HBox createPageHeader(
            Stage stage) {

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox titleBox =
                new VBox(4);

        Label title =
                new Label(
                        "Doctor Management"
                );

        title.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Label subtitle =
                new Label(
                        "Manage doctors, departments and availability"
                );

        subtitle.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        titleBox.getChildren().addAll(
                title,
                subtitle
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button addDoctor =
                new Button(
                        "＋ Add Doctor"
                );

        addDoctor.setPrefHeight(42);

        addDoctor.setPadding(
                new Insets(
                        0,
                        20,
                        0,
                        20
                )
        );

        String actionBtnStyle =
                "-fx-background-color: "
                        + PRIMARY_BLUE
                        + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;";

        addDoctor.setStyle(
                actionBtnStyle
        );

        addDoctor.setOnMouseEntered(
                e ->
                        addDoctor.setStyle(
                                actionBtnStyle +
                                "-fx-background-color: #1550B0;"
                        )
        );

        addDoctor.setOnMouseExited(
                e ->
                        addDoctor.setStyle(
                                actionBtnStyle
                        )
        );

        addDoctor.setOnAction(
                e ->
                        showAddDoctorDialog(stage)
        );

        header.getChildren().addAll(
                titleBox,
                spacer,
                addDoctor
        );

        return header;
    }

    // =========================================================
    // STATISTICS
    // =========================================================

    private HBox createStatistics() {
        HBox statistics = new HBox(18);

        SummaryCard.CardNode totalCardNode = SummaryCard.createCardNode("Total Doctors", "0", "Across all departments", "👨‍⚕️", SummaryCard.CardType.BLUE);
        SummaryCard.CardNode activeCardNode = SummaryCard.createCardNode("Active Doctors", "0", "Currently available", "✓", SummaryCard.CardType.GREEN);
        SummaryCard.CardNode leaveCardNode = SummaryCard.createCardNode("On Leave", "0", "Currently unavailable", "◷", SummaryCard.CardType.ORANGE);
        SummaryCard.CardNode deptCardNode = SummaryCard.createCardNode("Departments", "0", "Medical departments", "🏥", SummaryCard.CardType.PURPLE);

        totalDocsValLabel = totalCardNode.getValueLabel();
        activeDocsValLabel = activeCardNode.getValueLabel();
        onLeaveDocsValLabel = leaveCardNode.getValueLabel();
        totalDeptValLabel = deptCardNode.getValueLabel();

        statistics.getChildren().addAll(
                totalCardNode.getContainer(),
                activeCardNode.getContainer(),
                leaveCardNode.getContainer(),
                deptCardNode.getContainer()
        );

        for (javafx.scene.Node node : statistics.getChildren()) {
            HBox.setHgrow(node, Priority.ALWAYS);
        }

        return statistics;
    }

    private VBox createStatisticCard(
            String title,
            String value,
            String subtitle,
            String icon,
            String color,
            String bgColor) {

        VBox card =
                new VBox(10);

        card.setPadding(
                new Insets(20)
        );

        card.setPrefHeight(120);

        applyCardStyle(card);

        HBox top =
                new HBox();

        top.setAlignment(
                Pos.CENTER_LEFT
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: 600;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label iconLabel =
                new Label(icon);

        iconLabel.setPrefSize(
                36,
                36
        );

        iconLabel.setAlignment(
                Pos.CENTER
        );

        iconLabel.setStyle(
                "-fx-background-color: "
                        + bgColor
                        + ";" +
                "-fx-background-radius: 8;" +
                "-fx-text-fill: "
                        + color
                        + ";" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;"
        );

        top.getChildren().addAll(
                titleLabel,
                spacer,
                iconLabel
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Label subtitleLabel =
                new Label(subtitle);

        subtitleLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        card.getChildren().addAll(
                top,
                valueLabel,
                subtitleLabel
        );

        return card;
    }

    // =========================================================
    // SEARCH AND FILTERS
    // =========================================================

    private HBox createSearchAndFilters(
            Stage stage) {

        HBox container =
                new HBox(12);

        container.setAlignment(
                Pos.CENTER_LEFT
        );

        container.setPadding(
                new Insets(16)
        );

        applyCardStyle(container);

        searchField =
                new TextField();

        searchField.setPromptText(
                "Search by doctor name..."
        );

        searchField.setPrefHeight(40);

        searchField.setPrefWidth(320);

        searchField.setStyle(
                "-fx-background-color: "
                        + LIGHT_BACKGROUND
                        + ";" +
                "-fx-border-color: "
                        + BORDER
                        + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 0 14;" +
                "-fx-font-size: 12px;"
        );

        searchField.textProperty()
                .addListener(
                        (obs, oldValue, newValue) ->
                                applyFiltersAndRefreshUI()
                );

        departmentFilter =
                new ComboBox<>();

        departmentFilter.getItems().addAll(
                "All Departments",
                "Cardiology",
                "Neurology",
                "Orthopedics",
                "Pediatrics",
                "General Medicine"
        );

        departmentFilter.setValue(
                "All Departments"
        );

        departmentFilter.setPrefHeight(40);

        departmentFilter.setStyle(
                "-fx-font-size: 12px;"
        );

        departmentFilter.setOnAction(
                e ->
                        applyFiltersAndRefreshUI()
        );

        statusFilter =
                new ComboBox<>();

        statusFilter.getItems().addAll(
                "All Status",
                "Active",
                "Inactive",
                "On Leave"
        );

        statusFilter.setValue(
                "All Status"
        );

        statusFilter.setPrefHeight(40);

        statusFilter.setStyle(
                "-fx-font-size: 12px;"
        );

        statusFilter.setOnAction(
                e ->
                        applyFiltersAndRefreshUI()
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button filterButton =
                new Button(
                        "☷  Reset Filters"
                );

        filterButton.setPrefHeight(40);

        filterButton.setStyle(
                "-fx-background-color: "
                        + CARD_BG
                        + ";" +
                "-fx-border-color: "
                        + BORDER
                        + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: 600;" +
                "-fx-cursor: hand;"
        );

        filterButton.setOnAction(
                e -> {

                    searchField.clear();

                    departmentFilter.setValue(
                            "All Departments"
                    );

                    statusFilter.setValue(
                            "All Status"
                    );

                    applyFiltersAndRefreshUI();
                }
        );

        Button exportButton =
                new Button(
                        "↓  Export"
                );

        exportButton.setPrefHeight(40);

        exportButton.setPadding(
                new Insets(
                        0,
                        16,
                        0,
                        16
                )
        );

        exportButton.setStyle(
                "-fx-background-color: "
                        + PRIMARY_LIGHT
                        + ";" +
                "-fx-text-fill: "
                        + PRIMARY_BLUE
                        + ";" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        exportButton.setOnAction(
                e ->
                        exportDoctorDataToCSV(stage)
        );

        container.getChildren().addAll(
                searchField,
                departmentFilter,
                statusFilter,
                spacer,
                filterButton,
                exportButton
        );

        return container;
    }

    // =========================================================
    // DOCTOR LIST
    // =========================================================

    private VBox createDoctorList(
            Stage stage) {

        VBox card =
                new VBox(0);

        applyCardStyle(card);

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        header.setPadding(
                new Insets(
                        20,
                        24,
                        20,
                        24
                )
        );

        Label title =
                new Label(
                        "Doctors Directory"
                );

        title.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        doctorCountHeaderLabel =
                new Label(
                        "0 Doctors"
                );

        doctorCountHeaderLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: 600;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        header.getChildren().addAll(
                title,
                spacer,
                doctorCountHeaderLabel
        );

        card.getChildren().add(
                header
        );

        card.getChildren().add(
                new Separator()
        );

        card.getChildren().add(
                createTableHeader()
        );

        card.getChildren().add(
                new Separator()
        );

        doctorRowsContainer =
                new VBox(0);

        card.getChildren().add(
                doctorRowsContainer
        );

        return card;
    }

    private HBox createTableHeader() {

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        header.setPadding(
                new Insets(
                        12,
                        24,
                        12,
                        24
                )
        );

        header.setStyle(
                "-fx-background-color: #FAFAFA;"
        );

        Label doctor =
                createHeaderLabel(
                        "DOCTOR",
                        280
                );

        Label department =
                createHeaderLabel(
                        "DEPARTMENT",
                        160
                );

        Label qualification =
                createHeaderLabel(
                        "QUALIFICATION",
                        180
                );

        Label status =
                createHeaderLabel(
                        "STATUS",
                        120
                );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label actions =
                createHeaderLabel(
                        "ACTIONS",
                        180
                );

        actions.setAlignment(
                Pos.CENTER_RIGHT
        );

        header.getChildren().addAll(
                doctor,
                department,
                qualification,
                status,
                spacer,
                actions
        );

        return header;
    }

    private Label createHeaderLabel(
            String text,
            double width) {

        Label label =
                new Label(text);

        label.setPrefWidth(width);

        label.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        return label;
    }

    // =========================================================
    // FILTERING
    // =========================================================

    private void applyFiltersAndRefreshUI() {

        if (filteredDoctorList == null) {
            return;
        }

        String searchText =
                searchField != null
                        && searchField.getText() != null
                        ? searchField
                                .getText()
                                .toLowerCase()
                                .trim()
                        : "";

        String deptValue =
                departmentFilter != null
                        && departmentFilter.getValue() != null
                        ? departmentFilter.getValue()
                        : "All Departments";

        String statusValue =
                statusFilter != null
                        && statusFilter.getValue() != null
                        ? statusFilter.getValue()
                        : "All Status";

        filteredDoctorList.setPredicate(
                doctor -> {

                    boolean matchesSearch =
                            searchText.isEmpty()
                                    ||
                            safeLower(
                                    doctor.getName()
                            ).contains(searchText)
                                    ||
                            safeLower(
                                    doctor.getId()
                            ).contains(searchText)
                                    ||
                            safeLower(
                                    doctor.getQualification()
                            ).contains(searchText);

                    boolean matchesDepartment =
                            deptValue.equals(
                                    "All Departments"
                            )
                                    ||
                            safeEquals(
                                    doctor.getDepartment(),
                                    deptValue
                            );

                    boolean matchesStatus =
                            statusValue.equals(
                                    "All Status"
                            )
                                    ||
                            safeEquals(
                                    doctor.getStatus(),
                                    statusValue
                            );

                    return matchesSearch
                            && matchesDepartment
                            && matchesStatus;
                }
        );

        rebuildTableRows();

        updateStatistics();
    }

    // =========================================================
    // TABLE
    // =========================================================

    private void rebuildTableRows() {

        if (doctorRowsContainer == null) {
            return;
        }

        doctorRowsContainer
                .getChildren()
                .clear();

        if (filteredDoctorList == null
                || filteredDoctorList.isEmpty()) {

            Label emptyLabel =
                    new Label(
                            "No doctors match the selected search criteria."
                    );

            emptyLabel.setStyle(
                    "-fx-font-size: 13px;" +
                    "-fx-text-fill: "
                            + SECONDARY_TEXT
                            + ";" +
                    "-fx-padding: 24;"
            );

            doctorRowsContainer
                    .getChildren()
                    .add(emptyLabel);

            return;
        }

        for (int i = 0;
             i < filteredDoctorList.size();
             i++) {

            Doctor doctor =
                    filteredDoctorList.get(i);

            String statusColor =
                    SUCCESS_GREEN;

            String statusBgColor =
                    SUCCESS_LIGHT;

            if ("On Leave".equalsIgnoreCase(
                    doctor.getStatus())) {

                statusColor =
                        WARNING_ORANGE;

                statusBgColor =
                        WARNING_LIGHT;

            } else if (
                    "Inactive".equalsIgnoreCase(
                            doctor.getStatus())) {

                statusColor =
                        ERROR_RED;

                statusBgColor =
                        ERROR_LIGHT;
            }

            HBox row =
                    createDoctorRow(
                            doctor,
                            statusColor,
                            statusBgColor
                    );

            doctorRowsContainer
                    .getChildren()
                    .add(row);

            if (i < filteredDoctorList.size() - 1) {

                doctorRowsContainer
                        .getChildren()
                        .add(
                                new Separator()
                        );
            }
        }
    }

    // =========================================================
    // STATISTICS
    // =========================================================

    private void updateStatistics() {

        int total =
                masterDoctorList.size();

        int active = 0;

        int onLeave = 0;

        Set<String> departments =
                new HashSet<>();

        for (Doctor doctor :
                masterDoctorList) {

            if ("Active".equalsIgnoreCase(
                    doctor.getStatus())) {

                active++;

            } else if (
                    "On Leave".equalsIgnoreCase(
                            doctor.getStatus())) {

                onLeave++;
            }

            if (doctor.getDepartment() != null
                    && !doctor
                    .getDepartment()
                    .trim()
                    .isEmpty()
                    && !"Unassigned".equalsIgnoreCase(
                            doctor.getDepartment()
                    )) {

                departments.add(
                        doctor
                                .getDepartment()
                                .trim()
                );
            }
        }

        if (totalDocsValLabel != null) {

            totalDocsValLabel.setText(
                    String.valueOf(total)
            );
        }

        if (activeDocsValLabel != null) {

            activeDocsValLabel.setText(
                    String.valueOf(active)
            );
        }

        if (onLeaveDocsValLabel != null) {

            onLeaveDocsValLabel.setText(
                    String.valueOf(onLeave)
            );
        }

        if (totalDeptValLabel != null) {

            totalDeptValLabel.setText(
                    String.valueOf(
                            departments.size()
                    )
            );
        }

        if (doctorCountHeaderLabel != null) {

            doctorCountHeaderLabel.setText(
                    filteredDoctorList.size()
                            + " Doctors"
            );
        }
    }

    // =========================================================
    // DOCTOR ROW
    // =========================================================

    private HBox createDoctorRow(
            Doctor doctor,
            String statusColor,
            String statusBgColor) {

        HBox row =
                new HBox();

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(
                        14,
                        24,
                        14,
                        24
                )
        );

        row.setOnMouseEntered(
                e ->
                        row.setStyle(
                                "-fx-background-color: #F8FAFC;"
                        )
        );

        row.setOnMouseExited(
                e ->
                        row.setStyle(
                                "-fx-background-color: transparent;"
                        )
        );

        // =====================================================
        // DOCTOR INFORMATION
        // =====================================================

        HBox doctorBox =
                new HBox(12);

        doctorBox.setAlignment(
                Pos.CENTER_LEFT
        );

        doctorBox.setPrefWidth(280);

        Circle avatar =
                new Circle(18);

        avatar.setFill(
                Color.web(PRIMARY_LIGHT)
        );

        Label initialsLabel =
                new Label(
                        doctor.getInitials()
                );

        initialsLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: "
                        + PRIMARY_BLUE
                        + ";"
        );

        StackPane avatarPane =
                new StackPane(
                        avatar,
                        initialsLabel
                );

        VBox doctorInfo =
                new VBox(2);

        Label nameLabel =
                new Label(
                        doctor.getName()
                );

        nameLabel.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: 700;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Label idLabel =
                new Label(
                        "Doctor ID: "
                                + doctor.getId()
                );

        idLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        doctorInfo.getChildren().addAll(
                nameLabel,
                idLabel
        );

        doctorBox.getChildren().addAll(
                avatarPane,
                doctorInfo
        );

        // =====================================================
        // DEPARTMENT
        // =====================================================

        Label departmentLabel =
                new Label(
                        doctor.getDepartment()
                );

        departmentLabel.setPrefWidth(160);

        departmentLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: 600;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        // =====================================================
        // QUALIFICATION
        // =====================================================

        Label qualificationLabel =
                new Label(
                        doctor.getQualification()
                );

        qualificationLabel.setPrefWidth(180);

        qualificationLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        // =====================================================
        // STATUS
        // =====================================================

        Label statusLabel =
                new Label(
                        doctor.getStatus()
                );

        statusLabel.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: "
                        + statusColor
                        + ";" +
                "-fx-background-color: "
                        + statusBgColor
                        + ";" +
                "-fx-padding: 4 10;" +
                "-fx-background-radius: 12;"
        );

        HBox statusBox =
                new HBox(
                        statusLabel
                );

        statusBox.setPrefWidth(120);

        statusBox.setAlignment(
                Pos.CENTER_LEFT
        );

        // =====================================================
        // SPACER
        // =====================================================

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        // =====================================================
        // ACTIONS
        // =====================================================

        HBox actions =
                new HBox(8);

        actions.setPrefWidth(180);

        actions.setAlignment(
                Pos.CENTER_RIGHT
        );

        Button viewButton =
                createSmallButton(
                        "View",
                        PRIMARY_BLUE,
                        PRIMARY_LIGHT
                );

        Button editButton =
                createSmallButton(
                        "Edit",
                        PURPLE,
                        PURPLE_LIGHT
                );

        Button deleteButton =
                createSmallButton(
                        "Delete",
                        ERROR_RED,
                        ERROR_LIGHT
                );

        viewButton.setOnAction(
                e ->
                        showViewDoctorDialog(
                                doctor
                        )
        );

        editButton.setOnAction(
                e ->
                        showEditDoctorDialog(
                                doctor
                        )
        );

        deleteButton.setOnAction(
                e ->
                        handleDeleteDoctor(
                                doctor
                        )
        );

        actions.getChildren().addAll(
                viewButton,
                editButton,
                deleteButton
        );

        row.getChildren().addAll(
                doctorBox,
                departmentLabel,
                qualificationLabel,
                statusBox,
                spacer,
                actions
        );

        return row;
    }

    // =========================================================
    // SMALL BUTTON
    // =========================================================

    private Button createSmallButton(
            String text,
            String color,
            String bgColor) {

        Button button =
                new Button(text);

        button.setStyle(
                "-fx-background-color: "
                        + bgColor
                        + ";" +
                "-fx-text-fill: "
                        + color
                        + ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 4 10;"
        );

        return button;
    }

    // =========================================================
    // ADD DOCTOR
    // =========================================================

    private void showAddDoctorDialog(
            Stage parentStage) {

        Stage dialog =
                createModalDialog(
                        parentStage,
                        "Add Doctor"
                );

        VBox form =
                new VBox(14);

        form.setPadding(
                new Insets(24)
        );

        Label infoLabel =
                new Label(
                        "Select an existing doctor from the shared "
                                + "doctor profiles."
                );

        infoLabel.setWrapText(true);

        infoLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        // =====================================================
        // DOCTOR SELECTOR
        // =====================================================

        ComboBox<DoctorProfileItem> doctorInput =
                new ComboBox<>();

        doctorInput.setPromptText(
                "Select Doctor"
        );

        doctorInput.setMaxWidth(
                Double.MAX_VALUE
        );

        doctorInput.setPrefHeight(38);

        try {

            List<DoctorProfile> profiles =
                    doctorController
                            .getAllDoctorProfiles();

            for (DoctorProfile profile :
                    profiles) {

                if (profile == null) {
                    continue;
                }

                if (profile.getUid() == null
                        || profile.getUid()
                        .trim()
                        .isEmpty()) {

                    continue;
                }

                doctorInput.getItems().add(
                        new DoctorProfileItem(
                                profile
                        )
                );
            }

        } catch (Exception ex) {

            showAlert(
                    "Unable to Load Doctors",
                    getRootMessage(ex)
            );

            dialog.close();

            return;
        }

        // =====================================================
        // NO DOCTORS AVAILABLE
        // =====================================================

        if (doctorInput.getItems().isEmpty()) {

            showAlert(
                    "No Doctors Available",
                    "No doctor profiles were found in the "
                            + "shared doctor records.\n\n"
                            + "Please register a doctor first."
            );

            dialog.close();

            return;
        }

        // =====================================================
        // DEPARTMENT
        // =====================================================

        ComboBox<String> deptInput =
                new ComboBox<>(
                        FXCollections.observableArrayList(
                                "Cardiology",
                                "Neurology",
                                "Orthopedics",
                                "Pediatrics",
                                "General Medicine"
                        )
                );

        deptInput.setPromptText(
                "Select Department"
        );

        deptInput.setMaxWidth(
                Double.MAX_VALUE
        );

        deptInput.setPrefHeight(38);

        // =====================================================
        // QUALIFICATION
        // =====================================================

        TextField qualificationInput =
                createInputField(
                        "Qualification (e.g. MD, MS)"
                );

        // =====================================================
        // STATUS
        // =====================================================

        ComboBox<String> statusInput =
                new ComboBox<>(
                        FXCollections.observableArrayList(
                                "Active",
                                "Inactive",
                                "On Leave"
                        )
                );

        statusInput.setValue(
                "Active"
        );

        statusInput.setMaxWidth(
                Double.MAX_VALUE
        );

        statusInput.setPrefHeight(38);

        // =====================================================
        // ADD BUTTON
        // =====================================================

        Button saveButton =
                new Button(
                        "Add Doctor"
                );

        saveButton.setStyle(
                "-fx-background-color: "
                        + PRIMARY_BLUE
                        + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
        );

        saveButton.setMaxWidth(
                Double.MAX_VALUE
        );

        saveButton.setPrefHeight(40);

        saveButton.setOnAction(
                e -> {

                    DoctorProfileItem selectedDoctor =
                            doctorInput.getValue();

                    String department =
                            deptInput.getValue();

                    String qualification =
                            qualificationInput
                                    .getText()
                                    .trim();

                    String status =
                            statusInput.getValue();

                    // =================================================
                    // VALIDATION
                    // =================================================

                    if (selectedDoctor == null) {

                        showAlert(
                                "Validation Error",
                                "Please select a doctor."
                        );

                        return;
                    }

                    if (department == null
                            || department.trim().isEmpty()) {

                        showAlert(
                                "Validation Error",
                                "Please select a department."
                        );

                        return;
                    }

                    if (qualification.isEmpty()) {

                        showAlert(
                                "Validation Error",
                                "Qualification is required."
                        );

                        return;
                    }

                    if (status == null
                            || status.trim().isEmpty()) {

                        showAlert(
                                "Validation Error",
                                "Please select a status."
                        );

                        return;
                    }

                    // =================================================
                    // SAVE TO FIRESTORE
                    // =================================================

                    try {

                        String doctorUid =
                                selectedDoctor
                                        .getProfile()
                                        .getUid();

                        String associationId =
                                doctorController.addDoctor(
                                        doctorUid,
                                        department,
                                        qualification,
                                        status
                                );

                        loadDoctorData();

                        applyFiltersAndRefreshUI();

                        dialog.close();

                        showAlert(
                                "Success",
                                "Doctor associated successfully.\n\n"
                                        + "Doctor: "
                                        + selectedDoctor
                                        + "\n\n"
                                        + "Association ID: "
                                        + associationId
                        );

                    } catch (Exception ex) {

                        showAlert(
                                "Unable to Add Doctor",
                                getRootMessage(ex)
                        );
                    }
                }
        );

        // =====================================================
        // LABELS
        // =====================================================

        Label doctorLabel =
                createFormLabel(
                        "Select Doctor:"
                );

        Label departmentLabel =
                createFormLabel(
                        "Department:"
                );

        Label qualificationLabel =
                createFormLabel(
                        "Qualification:"
                );

        Label statusLabel =
                createFormLabel(
                        "Initial Status:"
                );

        // =====================================================
        // FORM
        // =====================================================

        form.getChildren().addAll(

                infoLabel,

                doctorLabel,
                doctorInput,

                departmentLabel,
                deptInput,

                qualificationLabel,
                qualificationInput,

                statusLabel,
                statusInput,

                saveButton
        );

        dialog.setScene(
                new Scene(
                        form,
                        440,
                        480
                )
        );

        dialog.showAndWait();
    }

    // =========================================================
    // FORM LABEL
    // =========================================================

    private Label createFormLabel(
            String text) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: 600;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        return label;
    }

    // =========================================================
    // EDIT DOCTOR
    // =========================================================

    private void showEditDoctorDialog(
            Doctor doctor) {

        Stage parentStage =
                (Stage) doctorRowsContainer
                        .getScene()
                        .getWindow();

        Stage dialog =
                createModalDialog(
                        parentStage,
                        "Edit Doctor - "
                                + doctor.getId()
                );

        VBox form =
                new VBox(16);

        form.setPadding(
                new Insets(24)
        );

        Label doctorName =
                new Label(
                        "Doctor: "
                                + doctor.getName()
                );

        doctorName.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        Label doctorId =
                new Label(
                        "Doctor UID: "
                                + doctor.getId()
                );

        doctorId.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        ComboBox<String> deptInput =
                new ComboBox<>(
                        FXCollections.observableArrayList(
                                "Cardiology",
                                "Neurology",
                                "Orthopedics",
                                "Pediatrics",
                                "General Medicine"
                        )
                );

        deptInput.setValue(
                doctor.getDepartment()
        );

        deptInput.setMaxWidth(
                Double.MAX_VALUE
        );

        deptInput.setPrefHeight(38);

        TextField qualificationInput =
                createInputField(
                        "Qualification"
                );

        qualificationInput.setText(
                doctor.getQualification()
        );

        ComboBox<String> statusInput =
                new ComboBox<>(
                        FXCollections.observableArrayList(
                                "Active",
                                "Inactive",
                                "On Leave"
                        )
                );

        statusInput.setValue(
                doctor.getStatus()
        );

        statusInput.setMaxWidth(
                Double.MAX_VALUE
        );

        statusInput.setPrefHeight(38);

        Button saveButton =
                new Button(
                        "Save Changes"
                );

        saveButton.setStyle(
                "-fx-background-color: "
                        + PRIMARY_BLUE
                        + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
        );

        saveButton.setMaxWidth(
                Double.MAX_VALUE
        );

        saveButton.setPrefHeight(40);

        saveButton.setOnAction(
                e -> {

                    String department =
                            deptInput.getValue();

                    String qualification =
                            qualificationInput
                                    .getText()
                                    .trim();

                    String status =
                            statusInput.getValue();

                    if (department == null) {

                        showAlert(
                                "Validation Error",
                                "Please select a department."
                        );

                        return;
                    }

                    if (qualification.isEmpty()) {

                        showAlert(
                                "Validation Error",
                                "Qualification is required."
                        );

                        return;
                    }

                    if (status == null) {

                        showAlert(
                                "Validation Error",
                                "Please select a status."
                        );

                        return;
                    }

                    try {

                        doctorController.updateDoctor(
                                doctor.getAssociationId(),
                                department,
                                qualification,
                                status
                        );

                        loadDoctorData();

                        applyFiltersAndRefreshUI();

                        dialog.close();

                        showAlert(
                                "Updated",
                                "Doctor details updated successfully."
                        );

                    } catch (Exception ex) {

                        showAlert(
                                "Update Failed",
                                getRootMessage(ex)
                        );
                    }
                }
        );

        form.getChildren().addAll(
                doctorName,
                doctorId,

                createFormLabel(
                        "Department:"
                ),
                deptInput,

                createFormLabel(
                        "Qualification:"
                ),
                qualificationInput,

                createFormLabel(
                        "Status:"
                ),
                statusInput,

                saveButton
        );

        dialog.setScene(
                new Scene(
                        form,
                        400,
                        430
                )
        );

        dialog.showAndWait();
    }

    // =========================================================
    // VIEW DOCTOR
    // =========================================================

    private void showViewDoctorDialog(
            Doctor doctor) {

        Stage parentStage =
                (Stage) doctorRowsContainer
                        .getScene()
                        .getWindow();

        Stage dialog =
                createModalDialog(
                        parentStage,
                        "Doctor Details"
                );

        VBox content =
                new VBox(12);

        content.setPadding(
                new Insets(24)
        );

        content.getChildren().addAll(

                new Label(
                        "Doctor UID: "
                                + doctor.getId()
                ),

                new Label(
                        "Association ID: "
                                + doctor
                                .getAssociationId()
                ),

                new Label(
                        "Name: "
                                + doctor.getName()
                ),

                new Label(
                        "Department: "
                                + doctor.getDepartment()
                ),

                new Label(
                        "Qualification: "
                                + doctor.getQualification()
                ),

                new Label(
                        "Status: "
                                + doctor.getStatus()
                )
        );

        for (javafx.scene.Node node :
                content.getChildren()) {

            node.setStyle(
                    "-fx-font-size: 14px;" +
                    "-fx-text-fill: "
                            + DARK_TEXT
                            + ";"
            );
        }

        Button closeButton =
                new Button(
                        "Close"
                );

        closeButton.setStyle(
                "-fx-background-color: "
                        + PRIMARY_LIGHT
                        + ";" +
                "-fx-text-fill: "
                        + PRIMARY_BLUE
                        + ";" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;"
        );

        closeButton.setOnAction(
                e ->
                        dialog.close()
        );

        content.getChildren().add(
                closeButton
        );

        dialog.setScene(
                new Scene(
                        content,
                        380,
                        340
                )
        );

        dialog.showAndWait();
    }

    // =========================================================
    // DELETE / REMOVE DOCTOR
    // =========================================================

    private void handleDeleteDoctor(
            Doctor doctor) {

        Alert alert =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        alert.setTitle(
                "Remove Doctor"
        );

        alert.setHeaderText(
                "Remove "
                        + doctor.getName()
                        + "?"
        );

        alert.setContentText(
                "The doctor will be removed from "
                        + "this hospital's active doctor list."
        );

        Optional<ButtonType> result =
                alert.showAndWait();

        if (result.isPresent()
                && result.get() == ButtonType.OK) {

            try {

                doctorController.removeDoctor(
                        doctor.getAssociationId()
                );

                loadDoctorData();

                applyFiltersAndRefreshUI();

                showAlert(
                        "Removed",
                        doctor.getName()
                                + " has been removed from "
                                + "the hospital."
                );

            } catch (Exception ex) {

                showAlert(
                        "Remove Failed",
                        getRootMessage(ex)
                );
            }
        }
    }

    // =========================================================
    // CSV EXPORT
    // =========================================================

    private void exportDoctorDataToCSV(
            Stage stage) {

        FileChooser fileChooser =
                new FileChooser();

        fileChooser.setTitle(
                "Export Doctor List"
        );

        fileChooser.setInitialFileName(
                "Doctors_Export.csv"
        );

        fileChooser
                .getExtensionFilters()
                .add(
                        new FileChooser
                                .ExtensionFilter(
                                        "CSV Files",
                                        "*.csv"
                                )
                );

        File file =
                fileChooser.showSaveDialog(
                        stage
                );

        if (file == null) {
            return;
        }

        try (
                PrintWriter writer =
                        new PrintWriter(file)
        ) {

            writer.println(
                    "Doctor UID,Association ID,"
                            + "Name,Department,"
                            + "Qualification,Status"
            );

            for (Doctor doctor :
                    filteredDoctorList) {

                writer.println(
                        String.format(
                                "\"%s\",\"%s\",\"%s\","
                                        + "\"%s\",\"%s\",\"%s\"",
                                escapeCsv(
                                        doctor.getId()
                                ),
                                escapeCsv(
                                        doctor
                                                .getAssociationId()
                                ),
                                escapeCsv(
                                        doctor.getName()
                                ),
                                escapeCsv(
                                        doctor.getDepartment()
                                ),
                                escapeCsv(
                                        doctor.getQualification()
                                ),
                                escapeCsv(
                                        doctor.getStatus()
                                )
                        )
                );
            }

            showAlert(
                    "Export Success",
                    "Doctor data exported successfully to "
                            + file.getName()
            );

        } catch (Exception ex) {

            showAlert(
                    "Export Error",
                    "Failed to export data: "
                            + getRootMessage(ex)
            );
        }
    }

    private String escapeCsv(
            String value) {

        if (value == null) {
            return "";
        }

        return value.replace(
                "\"",
                "\"\""
        );
    }

    // =========================================================
    // INPUT FIELD
    // =========================================================

    private TextField createInputField(
            String prompt) {

        TextField textField =
                new TextField();

        textField.setPromptText(
                prompt
        );

        textField.setPrefHeight(38);

        textField.setStyle(
                "-fx-background-color: "
                        + LIGHT_BACKGROUND
                        + ";" +
                "-fx-border-color: "
                        + BORDER
                        + ";" +
                "-fx-border-radius: 6;" +
                "-fx-background-radius: 6;" +
                "-fx-padding: 0 10;"
        );

        return textField;
    }

    // =========================================================
    // MODAL DIALOG
    // =========================================================

    private Stage createModalDialog(
            Stage parent,
            String title) {

        Stage dialog =
                new Stage();

        dialog.initModality(
                Modality.WINDOW_MODAL
        );

        dialog.initOwner(
                parent
        );

        dialog.setTitle(
                title
        );

        return dialog;
    }

    // =========================================================
    // CARD STYLE
    // =========================================================

    private void applyCardStyle(
            Pane card) {

        card.setStyle(
                "-fx-background-color: "
                        + CARD_BG
                        + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: "
                        + BORDER
                        + ";" +
                "-fx-border-radius: 12;"
        );

        DropShadow shadow =
                new DropShadow();

        shadow.setColor(
                Color.rgb(
                        15,
                        23,
                        42,
                        0.04
                )
        );

        shadow.setRadius(8);

        shadow.setOffsetY(2);

        card.setEffect(
                shadow
        );
    }

    // =========================================================
    // SAFE STRING HELPERS
    // =========================================================

    private String safeLower(
            String value) {

        return value == null
                ? ""
                : value.toLowerCase();
    }

    private boolean safeEquals(
            String first,
            String second) {

        if (first == null
                || second == null) {

            return false;
        }

        return first.equalsIgnoreCase(
                second
        );
    }

    // =========================================================
    // EXCEPTION MESSAGE
    // =========================================================

    private String getRootMessage(
            Throwable exception) {

        if (exception == null) {
            return "Unknown error.";
        }

        Throwable cause =
                exception;

        while (cause.getCause() != null) {

            cause =
                    cause.getCause();
        }

        if (cause.getMessage() != null
                && !cause
                .getMessage()
                .trim()
                .isEmpty()) {

            return cause.getMessage();
        }

        if (exception.getMessage() != null
                && !exception
                .getMessage()
                .trim()
                .isEmpty()) {

            return exception.getMessage();
        }

        return "Unknown database error.";
    }

    // =========================================================
    // ALERT
    // =========================================================

    private void showAlert(
            String title,
            String content) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(
                title
        );

        alert.setHeaderText(
                null
        );

        alert.setContentText(
                content
        );

        alert.showAndWait();
    }
}