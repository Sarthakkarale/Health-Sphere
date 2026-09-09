package com.healthsphere.view.admin;

import com.healthsphere.controller.admin.UserDirectoryController;
import com.healthsphere.model.UserProfile;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class UserManagementView {

    private BorderPane mainLayout;
    private Stage stage;

    private final UserDirectoryController userDirectoryController;

    private final List<UserRecord> userData =
            new ArrayList<>();

    private GridPane userTableContainer;

    private Label totalUsersLabel;
    private Label patientUsersLabel;
    private Label doctorUsersLabel;
    private Label hospitalUsersLabel;

    private ComboBox<String> roleFilter;
    private TextField searchField;

    // ============================================================
    // CONSTRUCTORS
    // ============================================================

    public UserManagementView() {
        this(null);
    }

    public UserManagementView(Stage stage) {

        this.stage = stage;

        this.userDirectoryController =
                new UserDirectoryController();
    }

    // ============================================================
    // VIEW
    // ============================================================

    public Parent getView() {
        return getContent();
    }

    public Scene getScene() {
        return createScene();
    }

    public Scene createScene() {

        Parent content =
                getContent();

        double width =
                stage != null && stage.getWidth() > 0
                        ? stage.getWidth()
                        : 1366;

        double height =
                stage != null && stage.getHeight() > 0
                        ? stage.getHeight()
                        : 768;

        return new Scene(
                content,
                width,
                height
        );
    }

    public Parent getContent() {

        mainLayout =
                new BorderPane();

        mainLayout.setStyle(
                "-fx-background-color: #F8FAFC;"
        );

        mainLayout.setCenter(
                buildUserDirectory()
        );

        return mainLayout;
    }

    // ============================================================
    // USER DIRECTORY
    // ============================================================

    private ScrollPane buildUserDirectory() {

        VBox root =
                new VBox(22);

        root.setPadding(
                new Insets(
                        28,
                        32,
                        32,
                        32
                )
        );

        root.setStyle(
                "-fx-background-color: #F8FAFC;"
        );

        // ========================================================
        // HEADER
        // ========================================================

        HBox header =
                createHeader(
                        "User Directory Management",
                        "View approved patients, doctors, and hospitals."
                );

        // ========================================================
        // STATISTICS
        // ========================================================

        HBox statistics =
                createStatisticsSection();

        // ========================================================
        // MAIN DIRECTORY CARD
        // ========================================================

        VBox card =
                new VBox(16);

        card.setPadding(
                new Insets(20)
        );

        card.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 16px;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 16px;" +
                "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.07), 18, 0.15, 0, 5);"
        );

        // ========================================================
        // TOP BAR
        // ========================================================

        HBox topBar =
                new HBox(12);

        topBar.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox sectionTitle =
                new VBox(3);

        Label directoryTitle =
                new Label(
                        "Approved Users"
                );

        directoryTitle.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        17
                )
        );

        directoryTitle.setTextFill(
                Color.web("#0F172A")
        );

        Label directorySubtitle =
                new Label(
                        "View verified users by category"
                );

        directorySubtitle.setFont(
                Font.font(
                        "Segoe UI",
                        11
                )
        );

        directorySubtitle.setTextFill(
                Color.web("#64748B")
        );

        sectionTitle.getChildren().addAll(
                directoryTitle,
                directorySubtitle
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        // ========================================================
        // SEARCH
        // ========================================================

        searchField =
                new TextField();

        searchField.setPromptText(
                "🔍  Search email or role..."
        );

        searchField.setPrefWidth(
                260
        );

        searchField.setMinHeight(
                38
        );

        searchField.setStyle(
                "-fx-background-color: #F8FAFC;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;" +
                "-fx-padding: 8 12;" +
                "-fx-font-size: 12px;"
        );

        // ========================================================
        // ROLE FILTER
        // ========================================================

        roleFilter =
                new ComboBox<>();

        roleFilter.getItems().addAll(
                "All Approved",
                "Patients",
                "Doctors",
                "Hospitals"
        );

        roleFilter.setValue(
                "All Approved"
        );

        roleFilter.setPrefWidth(
                145
        );

        roleFilter.setMinHeight(
                38
        );

        roleFilter.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;" +
                "-fx-font-size: 12px;"
        );

        // ========================================================
        // REFRESH
        // ========================================================

        Button refreshButton =
                new Button(
                        "↻  Refresh"
                );

        refreshButton.setMinHeight(
                38
        );

        refreshButton.setStyle(
                "-fx-background-color: #EEF2FF;" +
                "-fx-text-fill: #4338CA;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8px;" +
                "-fx-border-color: #C7D2FE;" +
                "-fx-border-radius: 8px;" +
                "-fx-padding: 8 15;" +
                "-fx-cursor: hand;"
        );

        refreshButton.setOnAction(
                e -> {

                    refreshButton.setDisable(true);

                    try {

                        loadUsers();

                    } finally {

                        refreshButton.setDisable(false);
                    }
                }
        );

        topBar.getChildren().addAll(
                sectionTitle,
                spacer,
                searchField,
                roleFilter,
                refreshButton
        );

        // ========================================================
        // TABLE
        // ========================================================

        GridPane tableHeader =
                createTableHeader();

        userTableContainer =
                new GridPane();

        userTableContainer.setHgap(0);
        userTableContainer.setVgap(8);

        loadUsers();

        // ========================================================
        // FILTER EVENTS
        // ========================================================

        searchField.textProperty()
                .addListener(
                        (obs, oldVal, newVal) ->
                                renderUserRows()
                );

        roleFilter.valueProperty()
                .addListener(
                        (obs, oldVal, newVal) ->
                                renderUserRows()
                );

        card.getChildren().addAll(
                topBar,
                tableHeader,
                userTableContainer
        );

        root.getChildren().addAll(
                header,
                statistics,
                card
        );

        return wrapInScrollPane(
                root
        );
    }

    // ============================================================
    // STATISTICS
    // ============================================================

    private HBox createStatisticsSection() {

        HBox section =
                new HBox(14);

        section.setAlignment(
                Pos.CENTER
        );

        totalUsersLabel =
                new Label("0");

        patientUsersLabel =
                new Label("0");

        doctorUsersLabel =
                new Label("0");

        hospitalUsersLabel =
                new Label("0");

        VBox totalCard =
                createStatCard(
                        "Approved Users",
                        totalUsersLabel,
                        "Verified accounts",
                        "#4F46E5",
                        "#EEF2FF",
                        "✓"
                );

        VBox patientCard =
                createStatCard(
                        "Patients",
                        patientUsersLabel,
                        "Approved patients",
                        "#2563EB",
                        "#EFF6FF",
                        "P"
                );

        VBox doctorCard =
                createStatCard(
                        "Doctors",
                        doctorUsersLabel,
                        "Approved doctors",
                        "#059669",
                        "#ECFDF5",
                        "D"
                );

        VBox hospitalCard =
                createStatCard(
                        "Hospitals",
                        hospitalUsersLabel,
                        "Approved hospitals",
                        "#7C3AED",
                        "#F5F3FF",
                        "H"
                );

        HBox.setHgrow(
                totalCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                patientCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                doctorCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                hospitalCard,
                Priority.ALWAYS
        );

        section.getChildren().addAll(
                totalCard,
                patientCard,
                doctorCard,
                hospitalCard
        );

        return section;
    }

    // ============================================================
    // STAT CARD
    // ============================================================

    private VBox createStatCard(
            String title,
            Label valueLabel,
            String subtitle,
            String accent,
            String background,
            String iconText) {

        VBox card =
                new VBox(7);

        card.setPadding(
                new Insets(16)
        );

        card.setMinHeight(
                105
        );

        card.setPrefHeight(
                112
        );

        card.setStyle(
                "-fx-background-color: "
                        + background
                        + ";" +
                "-fx-background-radius: 14px;" +
                "-fx-border-color: "
                        + accent
                        + ";" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 14px;"
        );

        HBox top =
                new HBox();

        top.setAlignment(
                Pos.CENTER_LEFT
        );

        Label titleLabel =
                new Label(
                        title
                );

        titleLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.SEMI_BOLD,
                        12
                )
        );

        titleLabel.setTextFill(
                Color.web("#475569")
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label icon =
                new Label(
                        iconText
                );

        icon.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        13
                )
        );

        icon.setTextFill(
                Color.web(accent)
        );

        top.getChildren().addAll(
                titleLabel,
                spacer,
                icon
        );

        valueLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        25
                )
        );

        valueLabel.setTextFill(
                Color.web(accent)
        );

        Label sub =
                new Label(
                        subtitle
                );

        sub.setFont(
                Font.font(
                        "Segoe UI",
                        10
                )
        );

        sub.setTextFill(
                Color.web("#64748B")
        );

        card.getChildren().addAll(
                top,
                valueLabel,
                sub
        );

        return card;
    }

    // ============================================================
    // FIXED TABLE HEADER
    // ============================================================

    private GridPane createTableHeader() {

        GridPane header =
                new GridPane();

        header.setPadding(
                new Insets(
                        10,
                        14,
                        10,
                        14
                )
        );

        header.setStyle(
                "-fx-background-color: #F8FAFC;" +
                "-fx-background-radius: 8px;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 8px;"
        );

        // Fixed columns
        ColumnConstraints roleColumn =
                new ColumnConstraints();

        roleColumn.setPrefWidth(180);
        roleColumn.setMinWidth(180);
        roleColumn.setMaxWidth(180);

        ColumnConstraints emailColumn =
                new ColumnConstraints();

        emailColumn.setPrefWidth(420);
        emailColumn.setMinWidth(300);

        ColumnConstraints statusColumn =
                new ColumnConstraints();

        statusColumn.setPrefWidth(180);
        statusColumn.setMinWidth(180);
        statusColumn.setMaxWidth(180);

        header.getColumnConstraints().addAll(
                roleColumn,
                emailColumn,
                statusColumn
        );

        Label role =
                createHeaderLabel(
                        "ROLE"
                );

        Label email =
                createHeaderLabel(
                        "EMAIL"
                );

        Label status =
                createHeaderLabel(
                        "STATUS"
                );

        header.add(
                role,
                0,
                0
        );

        header.add(
                email,
                1,
                0
        );

        header.add(
                status,
                2,
                0
        );

        return header;
    }

    private Label createHeaderLabel(
            String text) {

        Label label =
                new Label(
                        text
                );

        label.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        10
                )
        );

        label.setTextFill(
                Color.web("#64748B")
        );

        return label;
    }

    // ============================================================
    // LOAD USERS
    // ============================================================

    private void loadUsers() {
        if (userTableContainer == null) {
            return;
        }

        userTableContainer.getChildren().clear();
        userTableContainer.getChildren().add(com.healthsphere.util.ShimmerPlaceholder.createListShimmer(3));

        javafx.concurrent.Task<List<UserProfile>> loadTask = new javafx.concurrent.Task<>() {
            @Override
            protected List<UserProfile> call() throws Exception {
                List<UserProfile> users = userDirectoryController.getAllUsers();
                return users != null ? users : new ArrayList<>();
            }
        };

        loadTask.setOnSucceeded(e -> {
            userTableContainer.getChildren().clear();
            userData.clear();
            List<UserProfile> users = loadTask.getValue();

            for (UserProfile profile : users) {
                if (profile == null) continue;
                String email = safe(profile.getEmail());
                String role = safe(profile.getRole());
                String status = normalizeStatus(profile.getStatus());

                if (!"ACTIVE".equals(status)) continue;
                if (email.isEmpty()) email = "N/A";
                if (role.isEmpty()) continue;
                if (!"PATIENT".equals(role) && !"DOCTOR".equals(role) && !"HOSPITAL".equals(role)) continue;

                userData.add(new UserRecord(email, role, status));
            }

            updateUserCount();
            renderUserRows();
        });

        loadTask.setOnFailed(e -> {
            userTableContainer.getChildren().clear();
            userData.clear();
            updateUserCount();
            renderUserRows();
            Throwable ex = loadTask.getException();
            showErrorAlert("Unable to Load Users", "The approved user directory could not be loaded from Firestore.\n\n" + (ex != null ? ex.getMessage() : ""));
        });

        Thread bgThread = new Thread(loadTask);
        bgThread.setDaemon(true);
        bgThread.start();
    }

    // ============================================================
    // RENDER FIXED USER TABLE
    // ============================================================

    private void renderUserRows() {

        if (userTableContainer == null) {
            return;
        }

        userTableContainer
                .getChildren()
                .clear();

        String search =
                searchField == null
                        ? ""
                        : safe(
                                searchField.getText()
                        ).toLowerCase();

        String selectedRole =
                roleFilter == null
                        ? "All Approved"
                        : roleFilter.getValue();

        int rowIndex = 0;

        for (
                UserRecord user :
                userData
        ) {

            boolean roleMatches =
                    matchesSelectedRole(
                            user.role,
                            selectedRole
                    );

            boolean searchMatches =
                    search.isEmpty()
                            ||
                    user.email
                            .toLowerCase()
                            .contains(search)
                            ||
                    user.role
                            .toLowerCase()
                            .contains(search);

            if (
                    !roleMatches ||
                    !searchMatches
            ) {
                continue;
            }

            GridPane row =
                    createUserRow(
                            user
                    );

            userTableContainer.add(
                    row,
                    0,
                    rowIndex
            );

            rowIndex++;
        }

        if (rowIndex == 0) {

            VBox emptyBox =
                    new VBox(8);

            emptyBox.setAlignment(
                    Pos.CENTER
            );

            emptyBox.setPadding(
                    new Insets(35)
            );

            Label icon =
                    new Label(
                            "⌕"
                    );

            icon.setFont(
                    Font.font(
                            "Segoe UI",
                            FontWeight.BOLD,
                            28
                    )
            );

            icon.setTextFill(
                    Color.web("#94A3B8")
            );

            Label noMatch =
                    new Label(
                            "No approved users found"
                    );

            noMatch.setFont(
                    Font.font(
                            "Segoe UI",
                            FontWeight.SEMI_BOLD,
                            13
                    )
            );

            noMatch.setTextFill(
                    Color.web("#64748B")
            );

            Label hint =
                    new Label(
                            "Try selecting another category or changing your search."
                    );

            hint.setFont(
                    Font.font(
                            "Segoe UI",
                            11
                    )
            );

            hint.setTextFill(
                    Color.web("#94A3B8")
            );

            emptyBox.getChildren().addAll(
                    icon,
                    noMatch,
                    hint
            );

            userTableContainer.add(
                    emptyBox,
                    0,
                    0
            );
        }
    }

    // ============================================================
    // ROLE FILTER
    // ============================================================

    private boolean matchesSelectedRole(
            String role,
            String selectedRole) {

        if (
                selectedRole == null ||
                "All Approved".equals(selectedRole)
        ) {
            return true;
        }

        if (
                "Patients".equals(selectedRole)
        ) {
            return "PATIENT".equals(role);
        }

        if (
                "Doctors".equals(selectedRole)
        ) {
            return "DOCTOR".equals(role);
        }

        if (
                "Hospitals".equals(selectedRole)
        ) {
            return "HOSPITAL".equals(role);
        }

        return true;
    }

    // ============================================================
    // CREATE FIXED USER ROW
    // ============================================================

    private GridPane createUserRow(
            UserRecord user) {

        GridPane row =
                new GridPane();

        row.setPadding(
                new Insets(
                        12,
                        14,
                        12,
                        14
                )
        );

        row.setMinHeight(
                58
        );

        row.setMaxHeight(
                58
        );

        row.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 10px;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 10px;"
        );

        // ========================================================
        // FIXED COLUMNS
        // ========================================================

        ColumnConstraints roleColumn =
                new ColumnConstraints();

        roleColumn.setPrefWidth(180);
        roleColumn.setMinWidth(180);
        roleColumn.setMaxWidth(180);

        ColumnConstraints emailColumn =
                new ColumnConstraints();

        emailColumn.setPrefWidth(420);
        emailColumn.setMinWidth(300);

        ColumnConstraints statusColumn =
                new ColumnConstraints();

        statusColumn.setPrefWidth(180);
        statusColumn.setMinWidth(180);
        statusColumn.setMaxWidth(180);

        row.getColumnConstraints().addAll(
                roleColumn,
                emailColumn,
                statusColumn
        );

        // ========================================================
        // ROLE
        // ========================================================

        HBox roleBox =
                new HBox(9);

        roleBox.setAlignment(
                Pos.CENTER_LEFT
        );

        Label avatar =
                new Label(
                        getRoleLetter(
                                user.role
                        )
                );

        avatar.setAlignment(
                Pos.CENTER
        );

        avatar.setMinSize(
                32,
                32
        );

        avatar.setPrefSize(
                32,
                32
        );

        avatar.setMaxSize(
                32,
                32
        );

        avatar.setStyle(
                getRoleAvatarStyle(
                        user.role
                )
        );

        Label roleLabel =
                new Label(
                        formatRole(
                                user.role
                        )
                );

        roleLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        12
                )
        );

        roleLabel.setTextFill(
                Color.web("#0F172A")
        );

        roleBox.getChildren().addAll(
                avatar,
                roleLabel
        );

        // ========================================================
        // EMAIL
        // ========================================================

        Label emailLabel =
                new Label(
                        user.email
                );

        emailLabel.setFont(
                Font.font(
                        "Segoe UI",
                        11
                )
        );

        emailLabel.setTextFill(
                Color.web("#475569")
        );

        emailLabel.setMaxWidth(
                Double.MAX_VALUE
        );

        // ========================================================
        // STATUS
        // ========================================================

        Label statusLabel =
                createApprovedStatusLabel(user != null ? user.role : null);

        // ========================================================
        // ADD TO GRID
        // ========================================================

        row.add(
                roleBox,
                0,
                0
        );

        row.add(
                emailLabel,
                1,
                0
        );

        row.add(
                statusLabel,
                2,
                0
        );

        return row;
    }

    // ============================================================
    // APPROVED STATUS
    // ============================================================

    private Label createApprovedStatusLabel(String role) {

        String labelText = "● Approved";
        if (role != null && "PATIENT".equalsIgnoreCase(role.trim())) {
            labelText = "● Active";
        }

        Label label =
                new Label(
                        labelText
                );

        label.setAlignment(
                Pos.CENTER
        );

        label.setMinWidth(
                115
        );

        label.setPrefWidth(
                115
        );

        label.setPadding(
                new Insets(
                        6,
                        10,
                        6,
                        10
                )
        );

        label.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        10
                )
        );

        label.setTextFill(
                Color.web("#047857")
        );

        label.setStyle(
                "-fx-background-color: #D1FAE5;" +
                "-fx-background-radius: 20px;"
        );

        return label;
    }

    // ============================================================
    // ROLE AVATAR STYLE
    // ============================================================

    private String getRoleAvatarStyle(
            String role) {

        if ("PATIENT".equals(role)) {

            return
                    "-fx-background-color: #EFF6FF;" +
                    "-fx-background-radius: 50%;" +
                    "-fx-border-color: #BFDBFE;" +
                    "-fx-border-radius: 50%;" +
                    "-fx-text-fill: #2563EB;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 12px;";
        }

        if ("DOCTOR".equals(role)) {

            return
                    "-fx-background-color: #ECFDF5;" +
                    "-fx-background-radius: 50%;" +
                    "-fx-border-color: #A7F3D0;" +
                    "-fx-border-radius: 50%;" +
                    "-fx-text-fill: #059669;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 12px;";
        }

        return
                "-fx-background-color: #F5F3FF;" +
                "-fx-background-radius: 50%;" +
                "-fx-border-color: #DDD6FE;" +
                "-fx-border-radius: 50%;" +
                "-fx-text-fill: #7C3AED;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 12px;";
    }

    // ============================================================
    // ROLE LETTER
    // ============================================================

    private String getRoleLetter(
            String role) {

        if ("PATIENT".equals(role)) {
            return "P";
        }

        if ("DOCTOR".equals(role)) {
            return "D";
        }

        if ("HOSPITAL".equals(role)) {
            return "H";
        }

        return "U";
    }

    // ============================================================
    // ROLE NAME
    // ============================================================

    private String formatRole(
            String role) {

        if ("PATIENT".equals(role)) {
            return "Patient";
        }

        if ("DOCTOR".equals(role)) {
            return "Doctor";
        }

        if ("HOSPITAL".equals(role)) {
            return "Hospital";
        }

        return role;
    }

    // ============================================================
    // HEADER
    // ============================================================

    private HBox createHeader(
            String titleText,
            String subtitleText) {

        HBox header =
                new HBox(16);

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox titleBox =
                new VBox(6);

        Label title =
                new Label(
                        titleText
                );

        title.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        25
                )
        );

        title.setTextFill(
                Color.web("#0F172A")
        );

        Label subtitle =
                new Label(
                        subtitleText
                );

        subtitle.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.NORMAL,
                        13
                )
        );

        subtitle.setTextFill(
                Color.web("#64748B")
        );

        titleBox.getChildren().addAll(
                title,
                subtitle
        );

        header.getChildren().add(
                titleBox
        );

        return header;
    }

    // ============================================================
    // SCROLL PANE
    // ============================================================

    private ScrollPane wrapInScrollPane(
            VBox content) {

        ScrollPane scrollPane =
                new ScrollPane(
                        content
                );

        scrollPane.setFitToWidth(
                true
        );

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: #F8FAFC;" +
                "-fx-border-color: transparent;"
        );

        return scrollPane;
    }

    // ============================================================
    // USER COUNT
    // ============================================================

    private void updateUserCount() {

        if (totalUsersLabel == null) {
            return;
        }

        int total =
                userData.size();

        int patients =
                0;

        int doctors =
                0;

        int hospitals =
                0;

        for (
                UserRecord user :
                userData
        ) {

            if ("PATIENT".equals(user.role)) {
                patients++;
            }

            else if ("DOCTOR".equals(user.role)) {
                doctors++;
            }

            else if ("HOSPITAL".equals(user.role)) {
                hospitals++;
            }
        }

        totalUsersLabel.setText(
                String.valueOf(total)
        );

        patientUsersLabel.setText(
                String.valueOf(patients)
        );

        doctorUsersLabel.setText(
                String.valueOf(doctors)
        );

        hospitalUsersLabel.setText(
                String.valueOf(hospitals)
        );
    }

    // ============================================================
    // STATUS NORMALIZATION
    // ============================================================

    private String normalizeStatus(
            String status) {

        if (
                status == null ||
                status.trim().isEmpty()
        ) {
            return "UNKNOWN";
        }

        return status
                .trim()
                .replace(
                        "🟢",
                        ""
                )
                .replace(
                        "🔴",
                        ""
                )
                .replace(
                        "⚪",
                        ""
                )
                .replace(
                        "🟡",
                        ""
                )
                .replace(
                        "●",
                        ""
                )
                .replace(
                        "○",
                        ""
                )
                .trim()
                .toUpperCase();
    }

    // ============================================================
    // SAFE STRING
    // ============================================================

    private String safe(
            String value) {

        return value == null
                ? ""
                : value.trim();
    }

    // ============================================================
    // ERROR ALERT
    // ============================================================

    private void showErrorAlert(
            String title,
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR,
                        message,
                        ButtonType.OK
                );

        alert.setHeaderText(
                title
        );

        alert.setTitle(
                "User Directory"
        );

        alert.showAndWait();
    }

    // ============================================================
    // UI DATA HOLDER
    // ============================================================

    private static class UserRecord {

        private final String email;
        private final String role;
        private final String status;

        UserRecord(
                String email,
                String role,
                String status) {

            this.email = email;
            this.role = role;
            this.status = status;
        }
    }
}