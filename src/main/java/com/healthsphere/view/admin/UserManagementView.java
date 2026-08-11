package com.healthsphere.view.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class UserManagementView extends ScrollPane {

    private Stage primaryStage;
    private TableView<UserModel> userTable;
    private ObservableList<UserModel> masterUserData;
    private FilteredList<UserModel> filteredData;
    private Label totalUsersCountLabel;
    private PieChart rolePieChart;

    public UserManagementView() {
        this(null);
    }

    public UserManagementView(Stage stage) {
        this.primaryStage = stage;

        setFitToWidth(true);
        setStyle("-fx-background-color: #0F172A; -fx-background: #0F172A;");

        VBox mainContainer = new VBox(25);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setStyle("-fx-background-color: #0F172A;");

        // 1. Header Section
        VBox header = createHeader();

        // 2. Overview Banner (Stats + Real-time Chart)
        HBox overviewSection = createOverviewSection();

        // 3. Search & Filter Bar
        HBox filterBar = createFilterBar();

        // 4. Advanced Interactive Data Table
        VBox tableContainer = createTableContainer();

        mainContainer.getChildren().addAll(header, overviewSection, filterBar, tableContainer);
        setContent(mainContainer);

        // Load Initial Data
        loadUserData();
    }

    public Parent getView() {
        return this;
    }

    private VBox createHeader() {
        VBox header = new VBox(5);
        Label title = new Label("User Directory & Access Control");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Manage system accounts, user roles, security flags, and active sessions in real-time.");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        subtitle.setTextFill(Color.web("#94A3B8"));

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private HBox createOverviewSection() {
        HBox section = new HBox(20);
        section.setAlignment(Pos.CENTER);

        VBox statsBox = new VBox(15);
        HBox.setHgrow(statsBox, Priority.ALWAYS);

        totalUsersCountLabel = new Label("2,845");
        VBox totalCard = createStatBadge("Total Registered Users", totalUsersCountLabel, "+14% from last month", "#6366F1");
        VBox activeCard = createStatBadge("Active Today", new Label("1,920"), "67% online now", "#10B981");
        VBox suspendedCard = createStatBadge("Suspended / Banned", new Label("12"), "Requires Security Review", "#EF4444");

        HBox topTwo = new HBox(15, totalCard, activeCard);
        HBox.setHgrow(totalCard, Priority.ALWAYS);
        HBox.setHgrow(activeCard, Priority.ALWAYS);

        HBox.setHgrow(suspendedCard, Priority.ALWAYS);
        statsBox.getChildren().addAll(topTwo, suspendedCard);

        VBox chartCard = new VBox(10);
        chartCard.setPadding(new Insets(15));
        chartCard.setMinWidth(380);
        chartCard.setStyle(
            "-fx-background-color: #1E293B; " +
            "-fx-background-radius: 12px; " +
            "-fx-border-color: #334155; " +
            "-fx-border-radius: 12px;"
        );

        Label chartTitle = new Label("User Demographics Breakdown");
        chartTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        chartTitle.setTextFill(Color.WHITE);

        rolePieChart = new PieChart();
        rolePieChart.setPrefHeight(160);
        rolePieChart.setLegendVisible(false);
        rolePieChart.setLabelsVisible(true);

        chartCard.getChildren().addAll(chartTitle, rolePieChart);

        section.getChildren().addAll(statsBox, chartCard);
        return section;
    }

    private VBox createStatBadge(String title, Label valueLabel, String subtext, String accentColor) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(15));
        card.setStyle(
            "-fx-background-color: #1E293B; " +
            "-fx-background-radius: 12px; " +
            "-fx-border-color: #334155; " +
            "-fx-border-radius: 12px;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        titleLabel.setTextFill(Color.web("#94A3B8"));

        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        valueLabel.setTextFill(Color.WHITE);

        Label subLabel = new Label(subtext);
        subLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
        subLabel.setTextFill(Color.web(accentColor));

        card.getChildren().addAll(titleLabel, valueLabel, subLabel);
        return card;
    }

    private HBox createFilterBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(15));
        bar.setStyle(
            "-fx-background-color: #1E293B; " +
            "-fx-background-radius: 10px; " +
            "-fx-border-color: #334155; " +
            "-fx-border-radius: 10px;"
        );

        TextField searchInput = new TextField();
        searchInput.setPromptText("🔍 Search by Name, Email or User ID...");
        searchInput.setPrefWidth(320);
        searchInput.setStyle(
            "-fx-background-color: #0F172A; " +
            "-fx-text-fill: white; " +
            "-fx-border-color: #475569; " +
            "-fx-border-radius: 6px; " +
            "-fx-padding: 8px 12px;"
        );

        ComboBox<String> roleFilter = new ComboBox<>();
        roleFilter.getItems().addAll("All Roles", "PATIENT", "DOCTOR", "ADMIN");
        roleFilter.setValue("All Roles");
        roleFilter.setStyle("-fx-background-color: #0F172A; -fx-mark-color: white;");

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All Status", "ACTIVE", "SUSPENDED");
        statusFilter.setValue("All Status");
        statusFilter.setStyle("-fx-background-color: #0F172A; -fx-mark-color: white;");

        Runnable applyFilters = () -> {
            String query = searchInput.getText().toLowerCase().trim();
            String selectedRole = roleFilter.getValue();
            String selectedStatus = statusFilter.getValue();

            filteredData.setPredicate(user -> {
                boolean matchesSearch = query.isEmpty() ||
                        user.getName().toLowerCase().contains(query) ||
                        user.getEmail().toLowerCase().contains(query) ||
                        user.getUserId().toLowerCase().contains(query);

                boolean matchesRole = selectedRole.equals("All Roles") || user.getRole().equalsIgnoreCase(selectedRole);
                boolean matchesStatus = selectedStatus.equals("All Status") || user.getStatus().equalsIgnoreCase(selectedStatus);

                return matchesSearch && matchesRole && matchesStatus;
            });
        };

        searchInput.textProperty().addListener((obs, oldVal, newVal) -> applyFilters.run());
        roleFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters.run());
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters.run());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addUserBtn = new Button("+ Add New User");
        addUserBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        addUserBtn.setStyle(
            "-fx-background-color: #6366F1; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 6px; " +
            "-fx-padding: 8px 16px; " +
            "-fx-cursor: hand;"
        );
        addUserBtn.setOnAction(e -> showAddUserDialog());

        bar.getChildren().addAll(searchInput, roleFilter, statusFilter, spacer, addUserBtn);
        return bar;
    }

    @SuppressWarnings("unchecked")
    private VBox createTableContainer() {
        VBox container = new VBox();
        container.setStyle("-fx-background-color: #1E293B; -fx-background-radius: 12px; -fx-border-color: #334155; -fx-border-radius: 12px;");

        userTable = new TableView<>();
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        userTable.setStyle("-fx-background-color: transparent;");
        userTable.setPrefHeight(400);

        TableColumn<UserModel, String> idCol = new TableColumn<>("User ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        idCol.setPrefWidth(90);

        TableColumn<UserModel, String> nameCol = new TableColumn<>("User Info");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty || name == null) {
                    setGraphic(null);
                } else {
                    UserModel user = getTableView().getItems().get(getIndex());
                    HBox box = new HBox(12);
                    box.setAlignment(Pos.CENTER_LEFT);

                    StackPane avatar = createStyledAvatar(getInitials(name));

                    VBox textContainer = new VBox(2);
                    Label nameLbl = new Label(name);
                    nameLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                    nameLbl.setTextFill(Color.WHITE);

                    Label emailLbl = new Label(user.getEmail());
                    emailLbl.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
                    emailLbl.setTextFill(Color.web("#94A3B8"));

                    textContainer.getChildren().addAll(nameLbl, emailLbl);
                    box.getChildren().addAll(avatar, textContainer);
                    setGraphic(box);
                }
            }
        });

        TableColumn<UserModel, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String role, boolean empty) {
                super.updateItem(role, empty);
                if (empty || role == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(role);
                    badge.setPadding(new Insets(4, 10, 4, 10));
                    badge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));

                    switch (role.toUpperCase()) {
                        case "DOCTOR" -> badge.setStyle("-fx-background-color: #1E1B4B; -fx-text-fill: #818CF8; -fx-background-radius: 20px;");
                        case "ADMIN" -> badge.setStyle("-fx-background-color: #881337; -fx-text-fill: #FDA4AF; -fx-background-radius: 20px;");
                        default -> badge.setStyle("-fx-background-color: #064E3B; -fx-text-fill: #34D399; -fx-background-radius: 20px;");
                    }
                    setGraphic(badge);
                }
            }
        });

        TableColumn<UserModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(status);
                    badge.setPadding(new Insets(4, 10, 4, 10));
                    badge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));

                    if ("ACTIVE".equalsIgnoreCase(status)) {
                        badge.setStyle("-fx-background-color: #064E3B; -fx-text-fill: #10B981; -fx-background-radius: 6px;");
                    } else {
                        badge.setStyle("-fx-background-color: #7F1D1D; -fx-text-fill: #EF4444; -fx-background-radius: 6px;");
                    }
                    setGraphic(badge);
                }
            }
        });

        TableColumn<UserModel, String> dateCol = new TableColumn<>("Joined Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("joinedDate"));

        TableColumn<UserModel, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button toggleStatusBtn = new Button();
            private final Button viewBtn = new Button("Details");
            private final HBox btnGroup = new HBox(8, viewBtn, toggleStatusBtn);

            {
                btnGroup.setAlignment(Pos.CENTER);
                viewBtn.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11px;");
                toggleStatusBtn.setStyle("-fx-cursor: hand; -fx-font-size: 11px;");

                viewBtn.setOnAction(e -> {
                    UserModel user = getTableView().getItems().get(getIndex());
                    showUserDetailsModal(user);
                });

                toggleStatusBtn.setOnAction(e -> {
                    UserModel user = getTableView().getItems().get(getIndex());
                    if ("ACTIVE".equalsIgnoreCase(user.getStatus())) {
                        user.setStatus("SUSPENDED");
                    } else {
                        user.setStatus("ACTIVE");
                    }
                    userTable.refresh();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    UserModel user = getTableView().getItems().get(getIndex());
                    if ("ACTIVE".equalsIgnoreCase(user.getStatus())) {
                        toggleStatusBtn.setText("Suspend");
                        toggleStatusBtn.setStyle("-fx-background-color: #7F1D1D; -fx-text-fill: #FCA5A5; -fx-cursor: hand; -fx-font-size: 11px;");
                    } else {
                        toggleStatusBtn.setText("Activate");
                        toggleStatusBtn.setStyle("-fx-background-color: #064E3B; -fx-text-fill: #6EE7B7; -fx-cursor: hand; -fx-font-size: 11px;");
                    }
                    setGraphic(btnGroup);
                }
            }
        });

        userTable.getColumns().addAll(idCol, nameCol, roleCol, statusCol, dateCol, actionCol);
        container.getChildren().add(userTable);
        return container;
    }

    private StackPane createStyledAvatar(String initials) {
        Circle circle = new Circle(16);
        circle.setFill(Color.web("#334155"));
        circle.setStroke(Color.web("#6366F1"));
        circle.setStrokeWidth(1.5);

        Label label = new Label(initials);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        label.setTextFill(Color.web("#E2E8F0"));

        return new StackPane(circle, label);
    }

    private void loadUserData() {
        masterUserData = FXCollections.observableArrayList(
            new UserModel("USR-101", "Prajwal Patil", "prajwal@healthsphere.io", "ADMIN", "ACTIVE", "2026-01-15"),
            new UserModel("USR-102", "Dr. Rajesh Sharma", "dr.rajesh@carehospital.com", "DOCTOR", "ACTIVE", "2026-02-01"),
            new UserModel("USR-103", "Ananya Verma", "ananya.v@gmail.com", "PATIENT", "ACTIVE", "2026-02-10"),
            new UserModel("USR-104", "Vikram Malhotra", "vikram.m@yahoo.com", "PATIENT", "SUSPENDED", "2026-03-05"),
            new UserModel("USR-105", "Dr. Priya Nair", "priya.nair@cityclinic.org", "DOCTOR", "ACTIVE", "2026-03-12"),
            new UserModel("USR-106", "Saurabh Deshmukh", "saurabh.d@gmail.com", "PATIENT", "ACTIVE", "2026-04-01")
        );

        filteredData = new FilteredList<>(masterUserData, p -> true);
        userTable.setItems(filteredData);
        updatePieChart();
    }

    private void updatePieChart() {
        long patients = masterUserData.stream().filter(u -> u.getRole().equalsIgnoreCase("PATIENT")).count();
        long doctors = masterUserData.stream().filter(u -> u.getRole().equalsIgnoreCase("DOCTOR")).count();
        long admins = masterUserData.stream().filter(u -> u.getRole().equalsIgnoreCase("ADMIN")).count();

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Patients (" + patients + ")", patients),
            new PieChart.Data("Doctors (" + doctors + ")", doctors),
            new PieChart.Data("Admins (" + admins + ")", admins)
        );
        rolePieChart.setData(pieChartData);
    }

    private void showUserDetailsModal(UserModel user) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("User Profile Inspection");
        dialog.setHeaderText("Account Details: " + user.getName() + " (" + user.getUserId() + ")");
        dialog.setContentText(
            "Email: " + user.getEmail() + "\n" +
            "Role: " + user.getRole() + "\n" +
            "Account Status: " + user.getStatus() + "\n" +
            "Registration Date: " + user.getJoinedDate() + "\n\n" +
            "Security Status: 0 Active Threats\n" +
            "Last Login: 10 minutes ago via Desktop Agent"
        );
        dialog.showAndWait();
    }

    private void showAddUserDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Register new User into HealthSphere");
        dialog.setContentText("Enter User Full Name:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                String newId = "USR-" + (100 + masterUserData.size() + 1);
                UserModel newUser = new UserModel(newId, name, name.toLowerCase().replace(" ", ".") + "@healthsphere.io", "PATIENT", "ACTIVE", "2026-08-11");
                masterUserData.add(newUser);
                totalUsersCountLabel.setText(String.valueOf(2845 + masterUserData.size() - 6));
                updatePieChart();
            }
        });
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "US";
        String[] parts = name.split(" ");
        if (parts.length >= 2) {
            return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
        }
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }

    public static class UserModel {
        private final String userId;
        private final String name;
        private final String email;
        private final String role;
        private String status;
        private final String joinedDate;

        public UserModel(String userId, String name, String email, String role, String status, String joinedDate) {
            this.userId = userId;
            this.name = name;
            this.email = email;
            this.role = role;
            this.status = status;
            this.joinedDate = joinedDate;
        }

        public String getUserId() { return userId; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getJoinedDate() { return joinedDate; }
    }
}