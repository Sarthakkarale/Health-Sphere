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
                new VBox(20);

        root.setPadding(
                new Insets(
                        28,
                        32,
                        28,
                        32
                )
        );

        HBox header =
                createHeader(
                        "User Directory Management",
                        "Manage user profiles, account statuses, and system access."
                );

        VBox card =
                new VBox(16);

        card.setPadding(
                new Insets(20)
        );

        card.setStyle(
                "-fx-background-color: #FFFFFF; " +
                "-fx-background-radius: 12px; " +
                "-fx-border-color: #E2E8F0; " +
                "-fx-border-radius: 12px;"
        );

        HBox topBar =
                new HBox(12);

        topBar.setAlignment(
                Pos.CENTER_LEFT
        );

        totalUsersLabel =
                new Label(
                        "Registered Users"
                );

        totalUsersLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        16
                )
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        TextField search =
                new TextField();

        search.setPromptText(
                "🔍 Search User ID, Email, Role..."
        );

        search.setPrefWidth(
                280
        );

        search.setStyle(
                "-fx-background-color: #F1F5F9; " +
                "-fx-background-radius: 8px; " +
                "-fx-padding: 8 12;"
        );

        Button refreshButton =
                new Button(
                        "↻ Refresh"
                );

        refreshButton.setStyle(
                "-fx-background-color: #E0E7FF; " +
                "-fx-text-fill: #3730A3; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 8px; " +
                "-fx-padding: 8 14; " +
                "-fx-cursor: hand;"
        );

        refreshButton.setOnAction(
                e -> loadUsers()
        );

        Button addUserButton =
                new Button(
                        "+ Register User"
                );

        addUserButton.setStyle(
                "-fx-background-color: #2563EB; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 8px; " +
                "-fx-padding: 8 14; " +
                "-fx-cursor: hand;"
        );

        addUserButton.setOnAction(
                e -> showInformationAlert(
                        "Register User",
                        "User registration is handled through the existing authentication and registration workflow."
                )
        );

        topBar.getChildren().addAll(
                totalUsersLabel,
                spacer,
                search,
                refreshButton,
                addUserButton
        );

        userTableContainer =
                new VBox(10);

        loadUsers();

        search.textProperty()
                .addListener(
                        (obs, oldVal, newVal) ->
                                renderUserRows(newVal)
                );

        card.getChildren().addAll(
                topBar,
                userTableContainer
        );

        root.getChildren().addAll(
                header,
                card
        );

        return wrapInScrollPane(
                root
        );
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

            Label noMatch =
                    new Label(
                            "⚠️ No matching user records found."
                    );

            noMatch.setStyle(
                    "-fx-text-fill: #94A3B8; " +
                    "-fx-font-size: 13px; " +
                    "-fx-padding: 10;"
            );

            userTableContainer
                    .getChildren()
                    .add(noMatch);
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
                new Insets(12)
        );

        row.setStyle(
                "-fx-background-color: #F8FAFC; " +
                "-fx-background-radius: 8px; " +
                "-fx-border-color: #E2E8F0; " +
                "-fx-border-radius: 8px;"
        );

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
                Color.web("#2563EB")
        );

        idLabel.setPrefWidth(
                180
        );

        Label nameLabel =
                new Label(
                        user.id
                );

        nameLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        13
                )
        );

        nameLabel.setPrefWidth(
                160
        );

        Label roleLabel =
                new Label(
                        user.role
                );

        roleLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.NORMAL,
                        12
                )
        );

        roleLabel.setTextFill(
                Color.web("#64748B")
        );

        roleLabel.setPrefWidth(
                100
        );

        Label emailLabel =
                new Label(
                        user.email
                );

        emailLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.NORMAL,
                        12
                )
        );

        HBox.setHgrow(
                emailLabel,
                Priority.ALWAYS
        );

        Label statusLabel =
                createStatusLabel(
                        user.status
                );

        statusLabel.setPrefWidth(
                110
        );

        Button actionButton =
                createStatusButton(
                        user
                );

        row.getChildren().addAll(
                idLabel,
                nameLabel,
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

        String displayStatus =
                normalized;

        Label label =
                new Label(
                        displayStatus
                );

        label.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        11
                )
        );

        if (
                "ACTIVE"
                        .equalsIgnoreCase(
                                normalized
                        )
        ) {

            label.setText(
                    "🟢 Active"
            );

            label.setTextFill(
                    Color.web("#059669")
            );

        } else if (
                "INACTIVE"
                        .equalsIgnoreCase(
                                normalized
                        )
        ) {

            label.setText(
                    "⚪ Inactive"
            );

            label.setTextFill(
                    Color.web("#64748B")
            );

        } else if (
                "SUSPENDED"
                        .equalsIgnoreCase(
                                normalized
                        )
        ) {

            label.setText(
                    "🔴 Suspended"
            );

            label.setTextFill(
                    Color.web("#DC2626")
            );

        } else {

            label.setText(
                    "🟡 " + normalized
            );

            label.setTextFill(
                    Color.web("#D97706")
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
                        "-fx-background-color: #FEE2E2; " +
                        "-fx-text-fill: #DC2626; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 6px; " +
                        "-fx-cursor: hand;"
                        :
                        "-fx-background-color: #D1FAE5; " +
                        "-fx-text-fill: #059669; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 6px; " +
                        "-fx-cursor: hand;"
        );

        button.setOnAction(
                e -> {

                    boolean success;

                    if (active) {

                        success =
                                userDirectoryController
                                        .suspendUser(
                                                user.id
                                        );

                        if (success) {

                            user.status =
                                    "SUSPENDED";

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

                            renderUserRows("");
                        }
                    }

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
                new VBox(4);

        Label title =
                new Label(
                        titleText
                );

        title.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        24
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

        scrollPane.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-background: #F8FAFC;"
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

        totalUsersLabel.setText(
                "Registered Users ("
                        + userData.size()
                        + " Total)"
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
                        .trim()
                        .toUpperCase();

        return normalized;
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