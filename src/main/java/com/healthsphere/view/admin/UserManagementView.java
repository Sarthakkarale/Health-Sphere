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

    private VBox userTableContainer;

    private Label totalUsersLabel;
    private Label activeUsersLabel;
    private Label inactiveUsersLabel;
    private Label suspendedUsersLabel;

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

        Stage targetStage =
                this.stage;

        double width =
                targetStage != null &&
                targetStage.getWidth() > 0
                        ? targetStage.getWidth()
                        : 1366;

        double height =
                targetStage != null &&
                targetStage.getHeight() > 0
                        ? targetStage.getHeight()
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
                        "Manage user profiles, account statuses, and system access."
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
                        "Registered Users"
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
                        "Monitor account access and current user status"
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

        TextField search =
                new TextField();

        search.setPromptText(
                "🔍  Search User ID, Email, Role..."
        );

        search.setPrefWidth(
                300
        );

        search.setMinHeight(
                38
        );

        search.setStyle(
                "-fx-background-color: #F8FAFC;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;" +
                "-fx-padding: 8 12;" +
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
                search,
                refreshButton
        );

        // ========================================================
        // TABLE HEADER
        // ========================================================

        HBox tableHeader =
                createTableHeader();

        // ========================================================
        // USER ROW CONTAINER
        // ========================================================

        userTableContainer =
                new VBox(8);

        loadUsers();

        search.textProperty()
                .addListener(
                        (obs, oldVal, newVal) ->
                                renderUserRows(newVal)
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
    // STATISTICS SECTION
    // ============================================================

    private HBox createStatisticsSection() {

        HBox section =
                new HBox(14);

        section.setAlignment(
                Pos.CENTER
        );

        totalUsersLabel =
                new Label("0");

        activeUsersLabel =
                new Label("0");

        inactiveUsersLabel =
                new Label("0");

        suspendedUsersLabel =
                new Label("0");

        VBox totalCard =
                createStatCard(
                        "Total Users",
                        totalUsersLabel,
                        "Registered accounts",
                        "#4F46E5",
                        "#EEF2FF",
                        "👥"
                );

        VBox activeCard =
                createStatCard(
                        "Active",
                        activeUsersLabel,
                        "Currently enabled",
                        "#059669",
                        "#ECFDF5",
                        "✓"
                );

        VBox inactiveCard =
                createStatCard(
                        "Inactive",
                        inactiveUsersLabel,
                        "Currently disabled",
                        "#64748B",
                        "#F1F5F9",
                        "○"
                );

        VBox suspendedCard =
                createStatCard(
                        "Suspended",
                        suspendedUsersLabel,
                        "Access restricted",
                        "#DC2626",
                        "#FEF2F2",
                        "!"
                );

        HBox.setHgrow(
                totalCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                activeCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                inactiveCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                suspendedCard,
                Priority.ALWAYS
        );

        section.getChildren().addAll(
                totalCard,
                activeCard,
                inactiveCard,
                suspendedCard
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
    // TABLE HEADER
    // ============================================================

    private HBox createTableHeader() {

        HBox header =
                new HBox(12);

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        header.setPadding(
                new Insets(
                        10,
                        12,
                        10,
                        12
                )
        );

        header.setStyle(
                "-fx-background-color: #F8FAFC;" +
                "-fx-background-radius: 8px;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 8px;"
        );

        Label id =
                createHeaderLabel(
                        "USER ID",
                        180
                );

        Label name =
                createHeaderLabel(
                        "USER",
                        160
                );

        Label role =
                createHeaderLabel(
                        "ROLE",
                        100
                );

        Label email =
                createHeaderLabel(
                        "EMAIL",
                        -1
                );

        HBox.setHgrow(
                email,
                Priority.ALWAYS
        );

        Label status =
                createHeaderLabel(
                        "STATUS",
                        110
                );

        Label action =
                createHeaderLabel(
                        "ACTION",
                        100
                );

        header.getChildren().addAll(
                id,
                name,
                role,
                email,
                status,
                action
        );

        return header;
    }

    private Label createHeaderLabel(
            String text,
            double width) {

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

        if (width > 0) {
            label.setPrefWidth(width);
        }

        return label;
    }

    // ============================================================
    // LOAD USERS FROM FIRESTORE
    // ============================================================

    private void loadUsers() {

        if (userTableContainer == null) {
            return;
        }

        try {

            List<UserProfile> users =
                    userDirectoryController
                            .getAllUsers();

            userData.clear();

            if (users != null) {

                for (
                        UserProfile profile :
                        users
                ) {

                    if (profile == null) {
                        continue;
                    }

                    String uid =
                            safe(
                                    profile.getUid()
                            );

                    String email =
                            safe(
                                    profile.getEmail()
                            );

                    String role =
                            safe(
                                    profile.getRole()
                            );

                    String status =
                            safe(
                                    profile.getStatus()
                            );

                    if (uid.isEmpty()) {
                        uid = "N/A";
                    }

                    if (email.isEmpty()) {
                        email = "N/A";
                    }

                    if (role.isEmpty()) {
                        role = "N/A";
                    }

                    if (status.isEmpty()) {
                        status = "UNKNOWN";
                    }

                    userData.add(
                            new UserRecord(
                                    uid,
                                    email,
                                    role,
                                    status
                            )
                    );
                }
            }

            updateUserCount();

            renderUserRows("");

        } catch (Exception e) {

            e.printStackTrace();

            userData.clear();

            updateUserCount();

            renderUserRows("");

            showErrorAlert(
                    "Unable to Load Users",
                    "The User Directory could not be loaded from Firestore.\n\n"
                            + e.getMessage()
            );
        }
    }

    // ============================================================
    // RENDER USERS
    // ============================================================

    private void renderUserRows(
            String filter) {

        if (userTableContainer == null) {
            return;
        }

        userTableContainer
                .getChildren()
                .clear();

        String lowerFilter =
                filter == null
                        ? ""
                        : filter
                                .toLowerCase()
                                .trim();

        for (
                UserRecord user :
                userData
        ) {

            if (
                    lowerFilter.isEmpty()
                            ||
                    user.id
                            .toLowerCase()
                            .contains(
                                    lowerFilter
                            )
                            ||
                    user.email
                            .toLowerCase()
                            .contains(
                                    lowerFilter
                            )
                            ||
                    user.role
                            .toLowerCase()
                            .contains(
                                    lowerFilter
                            )
                            ||
                    user.status
                            .toLowerCase()
                            .contains(
                                    lowerFilter
                            )
            ) {

                userTableContainer
                        .getChildren()
                        .add(
                                createUserRow(user)
                        );
            }
        }

        if (
                userTableContainer
                        .getChildren()
                        .isEmpty()
        ) {

            VBox emptyBox =
                    new VBox(8);

            emptyBox.setAlignment(
                    Pos.CENTER
            );

            emptyBox.setPadding(
                    new Insets(30)
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
                            "No matching user records found"
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
                            "Try searching with a different User ID, email, or role."
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

            userTableContainer
                    .getChildren()
                    .add(
                            emptyBox
                    );
        }
    }

    // ============================================================
    // CREATE USER ROW
    // ============================================================

    private HBox createUserRow(
            UserRecord user) {

        HBox row =
                new HBox(12);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(
                        13,
                        12,
                        13,
                        12
                )
        );

        row.setMinHeight(
                62
        );

        row.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 10px;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 10px;"
        );

        // ========================================================
        // USER ID
        // ========================================================

        Label idLabel =
                new Label(
                        user.id
                );

        idLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        12
                )
        );

        idLabel.setTextFill(
                Color.web("#4F46E5")
        );

        idLabel.setPrefWidth(
                180
        );

        // ========================================================
        // USER
        // ========================================================

        HBox userBox =
                new HBox(9);

        userBox.setAlignment(
                Pos.CENTER_LEFT
        );

        Label avatar =
                new Label(
                        getAvatarLetter(user.id)
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
                "-fx-background-color: #EEF2FF;" +
                "-fx-background-radius: 50%;" +
                "-fx-border-color: #C7D2FE;" +
                "-fx-border-radius: 50%;" +
                "-fx-text-fill: #4338CA;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 12px;"
        );

        Label nameLabel =
                new Label(
                        user.id
                );

        nameLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        12
                )
        );

        nameLabel.setTextFill(
                Color.web("#0F172A")
        );

        userBox.getChildren().addAll(
                avatar,
                nameLabel
        );

        userBox.setPrefWidth(
                160
        );

        // ========================================================
        // ROLE
        // ========================================================

        Label roleLabel =
                new Label(
                        user.role
                );

        roleLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.SEMI_BOLD,
                        11
                )
        );

        roleLabel.setTextFill(
                Color.web("#475569")
        );

        roleLabel.setPrefWidth(
                100
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
                Color.web("#64748B")
        );

        HBox.setHgrow(
                emailLabel,
                Priority.ALWAYS
        );

        // ========================================================
        // STATUS
        // ========================================================

        Label statusLabel =
                createStatusLabel(
                        user.status
                );

        statusLabel.setPrefWidth(
                110
        );

        // ========================================================
        // ACTION
        // ========================================================

        Button actionButton =
                createStatusButton(
                        user
                );

        actionButton.setPrefWidth(
                92
        );

        row.getChildren().addAll(
                idLabel,
                userBox,
                roleLabel,
                emailLabel,
                statusLabel,
                actionButton
        );

        return row;
    }

    // ============================================================
    // STATUS LABEL
    // ============================================================

    private Label createStatusLabel(
            String status) {

        String normalized =
                normalizeStatus(status);

        Label label =
                new Label();

        label.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        10
                )
        );

        label.setAlignment(
                Pos.CENTER
        );

        label.setPadding(
                new Insets(
                        6,
                        10,
                        6,
                        10
                )
        );

        if (
                "ACTIVE"
                        .equalsIgnoreCase(
                                normalized
                        )
        ) {

            label.setText(
                    "● Active"
            );

            label.setTextFill(
                    Color.web("#047857")
            );

            label.setStyle(
                    "-fx-background-color: #D1FAE5;" +
                    "-fx-background-radius: 20px;"
            );

        } else if (
                "INACTIVE"
                        .equalsIgnoreCase(
                                normalized
                        )
        ) {

            label.setText(
                    "○ Inactive"
            );

            label.setTextFill(
                    Color.web("#475569")
            );

            label.setStyle(
                    "-fx-background-color: #E2E8F0;" +
                    "-fx-background-radius: 20px;"
            );

        } else if (
                "SUSPENDED"
                        .equalsIgnoreCase(
                                normalized
                        )
        ) {

            label.setText(
                    "● Suspended"
            );

            label.setTextFill(
                    Color.web("#B91C1C")
            );

            label.setStyle(
                    "-fx-background-color: #FEE2E2;" +
                    "-fx-background-radius: 20px;"
            );

        } else {

            label.setText(
                    "● " + normalized
            );

            label.setTextFill(
                    Color.web("#B45309")
            );

            label.setStyle(
                    "-fx-background-color: #FEF3C7;" +
                    "-fx-background-radius: 20px;"
            );
        }

        return label;
    }

    // ============================================================
    // STATUS BUTTON
    // ============================================================

    private Button createStatusButton(
            UserRecord user) {

        String normalized =
                normalizeStatus(
                        user.status
                );

        boolean active =
                "ACTIVE"
                        .equalsIgnoreCase(
                                normalized
                        );

        Button button =
                new Button(
                        active
                                ? "Suspend"
                                : "Activate"
                );

        button.setStyle(
                active
                        ?
                        "-fx-background-color: #FEF2F2;" +
                        "-fx-text-fill: #DC2626;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 7px;" +
                        "-fx-border-color: #FECACA;" +
                        "-fx-border-radius: 7px;" +
                        "-fx-padding: 7 11;" +
                        "-fx-cursor: hand;"
                        :
                        "-fx-background-color: #ECFDF5;" +
                        "-fx-text-fill: #059669;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 7px;" +
                        "-fx-border-color: #A7F3D0;" +
                        "-fx-border-radius: 7px;" +
                        "-fx-padding: 7 11;" +
                        "-fx-cursor: hand;"
        );

        button.setOnAction(
                e -> {

                    boolean success;

                    button.setDisable(
                            true
                    );

                    if (active) {

                        success =
                                userDirectoryController
                                        .suspendUser(
                                                user.id
                                        );

                        if (success) {

                            user.status =
                                    "SUSPENDED";

                            updateUserCount();

                            renderUserRows("");

                        }

                    } else {

                        success =
                                userDirectoryController
                                        .activateUser(
                                                user.id
                                        );

                        if (success) {

                            user.status =
                                    "ACTIVE";

                            updateUserCount();

                            renderUserRows("");

                        }
                    }

                    button.setDisable(
                            false
                    );

                    if (!success) {

                        showErrorAlert(
                                "Update Failed",
                                "Unable to update the account status for:\n"
                                        + user.id
                        );
                    }
                }
        );

        return button;
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
    // USER COUNT + STATISTICS
    // ============================================================

    private void updateUserCount() {

        if (totalUsersLabel == null) {
            return;
        }

        int total =
                userData.size();

        int active =
                0;

        int inactive =
                0;

        int suspended =
                0;

        for (
                UserRecord user :
                userData
        ) {

            String status =
                    normalizeStatus(
                            user.status
                    );

            if (
                    "ACTIVE"
                            .equalsIgnoreCase(
                                    status
                            )
            ) {

                active++;

            } else if (
                    "INACTIVE"
                            .equalsIgnoreCase(
                                    status
                            )
            ) {

                inactive++;

            } else if (
                    "SUSPENDED"
                            .equalsIgnoreCase(
                                    status
                            )
            ) {

                suspended++;
            }
        }

        totalUsersLabel.setText(
                String.valueOf(total)
        );

        if (activeUsersLabel != null) {

            activeUsersLabel.setText(
                    String.valueOf(active)
            );
        }

        if (inactiveUsersLabel != null) {

            inactiveUsersLabel.setText(
                    String.valueOf(inactive)
            );
        }

        if (suspendedUsersLabel != null) {

            suspendedUsersLabel.setText(
                    String.valueOf(suspended)
            );
        }
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

        String normalized =
                status
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

        return normalized;
    }

    // ============================================================
    // AVATAR LETTER
    // ============================================================

    private String getAvatarLetter(
            String value) {

        if (
                value == null ||
                value.trim().isEmpty()
        ) {

            return "U";
        }

        return value
                .trim()
                .substring(
                        0,
                        1
                )
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
    // INFORMATION ALERT
    // ============================================================

    private void showInformationAlert(
            String title,
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION,
                        message,
                        ButtonType.OK
                );

        alert.setHeaderText(
                title
        );

        alert.setTitle(
                "User Access Hub"
        );

        alert.showAndWait();
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
                "User Access Hub"
        );

        alert.showAndWait();
    }

    // ============================================================
    // UI DATA HOLDER
    // ============================================================

    private static class UserRecord {

        private final String id;
        private final String email;
        private final String role;

        private String status;

        UserRecord(
                String id,
                String email,
                String role,
                String status) {

            this.id = id;
            this.email = email;
            this.role = role;
            this.status = status;
        }
    }
}